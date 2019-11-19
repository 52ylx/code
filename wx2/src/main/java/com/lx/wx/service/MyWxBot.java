package com.lx.wx.service;//说明:

import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.wx.entity.GZH;
import com.lx.wx.entity.TW;
import com.lx.wx.entity.WxMessage;
import com.lx.util.LX;
import io.github.biezhi.wechat.api.enums.AccountType;
import io.github.biezhi.wechat.api.model.Account;
import io.github.biezhi.wechat.api.model.Recommend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 创建人:游林夕/2019/5/28 19 09
 */
@Service
public class MyWxBot extends WxBot {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TaoBaoService taoBaoService;

    public static void main(String [] args){
        MyWxBot b = new MyWxBot();
    }
    @Override
    protected void addFriend(Recommend r) {
        if (!r.getContent().startsWith("lxzz")) {//不是已这个开头
            verify(r);//添加好友
            Account a = new Account();
            a.setUserName(r.getUserName());
            a.setNickName(r.getNickName());
            a.setAccountType(AccountType.TYPE_FRIEND);
            accountHashMap.put(a.getUserName(),a);
            remarkName(a.getUserName());//修改备注
            getText(new WxMessage("查询",a.getUserName(),a.getNickName(),a.getRemarkName(),""));//发送信息
        }
    }
    //修改备注名
    public String remarkName(String name){
        Account a = accountHashMap.get(name);
        String rName = a.getRemarkName();
        if (LX.isEmpty(rName)){//不是自定义开头
//            String pName = rName.startsWith("M_lxzz")?rName.substring(2):"";
            rName = "lxzz"+LX.getDate("yyMMddHHmmss");//公众号关联,不可修改
//            if (LX.isNotEmpty(pName)){//有推荐人
//                rName = rName+"_M_"+pName;
//                send(pName,"您的好友["+a.getNickName()+"]已成功添加!");
//            }
            rName(a.getUserName(),rName);//修改备注名
            a.setRemarkName(rName);//修改系统中的备注名
        }
        //通过备注找用户
        rnameToName.put(rName,name);
        return a.getRemarkName();
    }
    public void send(String rname ,String s){
        try {
            String toUserName = rnameToName.get(rname);
            if (LX.isNotEmpty(toUserName)){
                if (s.endsWith("jpg")||s.endsWith("png")){
                    sendImg(toUserName,s);
                }else{
                    sendText(toUserName,s);
                }
            }
        }catch (Exception e){
            log.error("根据备注发消息",e);
        }
    }
    @Override
    protected void getText(WxMessage msg) {
        try{
            Account a = accountHashMap.get(msg.getFromUserName());
            if (a!=null){
                msg.setFromNickName(a.getNickName());
                msg.setFromRemarkName(a.getRemarkName());
                String r = remarkName(msg.getFromUserName());
                if (r == null) return;
                msg.setFromRemarkName(r);//修改信息备注名
            }else{
                msg.setText(msg.getText().substring(msg.getText().indexOf("<br/>")+5));
            }
            if (!redisUtil.exists("app:user:nick:"+msg.getFromRemarkName())){//查询昵称
                redisUtil.put("app:user:nick:"+msg.getFromRemarkName(),LX.toJSONString(new Var("nick",msg.getFromNickName())));//将昵称存入
            }
            log.info("收到消息:"+msg.getFromNickName()+"   "+msg.getText());

            if(msg.getText().matches("\\d{6}")||msg.getText().matches("\\d{12}")){
                Var v = redisUtil.get("app:user:nick:"+msg.getFromRemarkName(),Var.class);
                if (("lxzz"+msg.getText()).equals(msg.getFromRemarkName())||v.containsKey("bing")){//推荐码是自己 或者有推荐码
                    sendText(msg.getFromUserName(),"不可重复绑定哦!");
                    return;
                }
                v.put("bing","lxzz"+msg.getText());
                redisUtil.put("app:user:nick:"+msg.getFromRemarkName(),v);//将昵称存入
                sendText(msg.getFromUserName(),"绑定成功!");
                send("lxzz"+msg.getText(),"好友:"+v.getStr("nick")+".绑定成功!");
            }else if ("提现".equals(msg.getText())) {//发送给晓贴
                BigDecimal fx = taoBaoService.getTX(msg.getFromRemarkName());
                if (fx.compareTo(new BigDecimal(0))==0){
                    sendText(msg.getFromUserName(),"暂无收货订单!");
                }else{
                    String text = "尊敬的用户:"+msg.getFromNickName()+".\n请稍后!由于是人工服务,每天24点前完成!全部:"+fx.setScale(2, RoundingMode.CEILING);
                    sendText(msg.getFromUserName(),text);
                    sendText(rnameToName.get("lxzz000001"),text);
                    sendText(rnameToName.get("lxzz000002"),text);
                }
            }else if (msg.getText().matches(".*(￥|₤|\\$|€|¢|₳|₴|查询).*")) {
                GZH tx = taoBaoService.wx_in(msg.getText(),msg.getFromRemarkName(), msg.getFromNickName());
                if (tx instanceof TW){
                    sendText(msg.getFromUserName(),tx.text+"\n"+((TW) tx).url);
                }else{
                    sendText(msg.getFromUserName(),tx.text);
                }
                sendText(msg.getFromUserName(),"下次不回复请点击:\n http://52ylx.cn/a/"+(a.getRemarkName().substring(4)));
            }

        }catch (Exception e){
            log.error("收到消息",e);
        }
    }
}
