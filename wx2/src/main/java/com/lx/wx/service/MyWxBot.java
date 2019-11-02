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
            getText(new WxMessage("教学",a.getUserName(),a.getNickName(),a.getRemarkName(),""));//发送信息
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
            //发送消息到我的账号
            GZH gzh = taoBaoService.wx_in(msg.getText(), msg.getFromRemarkName(),msg.getFromNickName());
            log.info("返回消息:"+gzh);
            if (LX.isNotEmpty(gzh)){
                String s = "";
                if (gzh instanceof TW){
                    s = ((TW)gzh).getText()+"\n"+((TW)gzh).getUrl();
                }else{
                    s = gzh.text;
                }
                if ("教学".equals(msg.getText())){
                    sendText(msg.getFromUserName(),
                            "一一一一一温馨提示一一一一\n" +
                            "回复“提现”可以提取当前余.额\n" +
                            "回复“查询”可以获取账.单信息");
                }
                if (s.endsWith("jpg")||s.endsWith("png")){
                    sendImg(msg.getFromUserName(),s);
                }else{
                    sendText(msg.getFromUserName(),s);
                    if ("提现".equals(msg.getText())){//发送给晓贴
                        sendText(rnameToName.get("lxzz000001"),s);
                        sendText(rnameToName.get("lxzz000002"),s);
                    }else{//
                        sendText(msg.getFromUserName(),"微信不回复请使用我的公众号:\n https://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=MzAxOTc2OTMwNQ==&scene=124#wechat_redirect");
                    }
                }
            }
        }catch (Exception e){
            log.error("收到消息",e);
//            sendText(msg.getFromUserName(),"对不起!我出现问题了!正在修复....");
        }
    }
}
