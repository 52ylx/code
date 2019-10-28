package com.lx.wx.controller;//说明:

import com.lx.authority.config.OS;
import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.wx.entity.GZH;
import com.lx.wx.entity.Order;
import com.lx.wx.entity.Show;
import com.lx.wx.entity.TW;
import com.lx.wx.service.TaoBaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 创建人:游林夕/2019/4/26 19 13
 */
@Controller
public class AppController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TaoBaoService taoBaoService;
    //获取token
    //https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx1640681a4d340625&secret=c87e28af1be4cbc7960f86c8e9dbb5a1

    //获取永久素材列表
    //https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=26_5WZWO34wsX6GVnHJP0bfkeUo01yvgBvJIrBjgRFkgm0uqsYD2PbVFNzi_9C4QI4eEyp_lLCuY4ybSzYuuPL1TmF-jY_WNCw0bj8lvIjKzQAOpTxShLNcbeCCkZo0tzwXP_4-NfWWWhgeGBIXRBLaAIAGQA
    //{"type":"image","offset":0,"count":20}
    //{"item":[{"media_id":"RmBeagJC407TpN38-o3rwemuVDdTa4B2rHndmvQATFs","name":"pq.jpg","update_time":1572146980,"url":"http:\/\/mmbiz.qpic.cn\/mmbiz_jpg\/R6HibJCghziaDtOJEEJG2xchL54Bj2Jo5PQzEaMzXibw1BeIs8aoibr5X1bJDDlBTEiaIzqW9eKmOd35evPEFo8t3IA\/0?wx_fmt=jpeg"}
    // ,{"media_id":"RmBeagJC407TpN38-o3rweS-eEzQMkF1NhieYqLJO0g","name":"jiaocheng.jpg","update_time":1562164740,"url":"http:\/\/mmbiz.qpic.cn\/mmbiz_jpg\/R6HibJCghziaDMCuO0PGSU1S6AJQnWDySicayfabDx00nMgEHUFtjXOJQRnb8qwlyFZlQaufFobzTC6N7O66JaalQ\/0?wx_fmt=jpeg"},{"media_id":"RmBeagJC407TpN38-o3rwSsebCAx2IiSw4FLW-IR-vE","name":"cxt.jpg","update_time":1562164717,"url":"http:\/\/mmbiz.qpic.cn\/mmbiz_jpg\/R6HibJCghziaDMCuO0PGSU1S6AJQnWDySic3KzADjXe2VT6umemiaflAJKOOcKfiaf61HDWXuq0XcvibbgiaSx7hC38lA\/0?wx_fmt=jpeg"}],"total_count":3,"item_count":3}
    Logger log = LoggerFactory.getLogger(AppController.class);
    @RequestMapping("/wx")
    public void wx(HttpServletRequest req, HttpServletResponse response) throws Exception {
        String msg = "";
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(),"utf-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line=br.readLine())!=null){
                sb.append(line);
            }
            String str = sb.toString();
            log.info(str);
            String name = getVal(str,"FromUserName").replace("<![CDATA[","").replace("]]>","");//用户唯一id
            String rname = redisUtil.get("app:user:"+name);
            if ("<![CDATA[text]]>".equals(getVal(sb.toString(),"MsgType"))){//字符串消息
                String text = getTrim(str,"Content").trim();//消息
                if (LX.isEmpty(rname)){
                    rname = "lxzz"+LX.getDate("yyMMddHHmmssSSS");//设置18备注名
                    redisUtil.put("app:user:"+name,rname);
                    redisUtil.put("app:user:nick:"+rname,LX.toJSONString(new Var("nick",rname)));//将昵称存入
                }
                if ("查询".equals(text)){
                    msg = parse(str,"http://www.52ylx.cn/s/" +rname);
                }else if ("提现".equals(text)){
                    if (rname.length()==19){
                        msg = toImg(str, "RmBeagJC407TpN38-o3rwdtScXzBUtWmYi1T6ctjZE0");//去绑定
                    }else{
                        msg = parse(str,"公众号无法转账,请去关联的微信发起提现哦");
                    }
                }else if (text.matches("\\d{6}") || text.matches("\\d{12}")){//绑定微信号
                    String lastName = rname;//上次备注
                    rname = "lxzz"+text;//备注名
                    Var v = redisUtil.get("app:user:nick:"+rname, Var.class);
                    if (LX.isNotEmpty(v)){//用户存在
                        if (lastName.length() == 19){//公众号用户
                            Map<String,String> shows = redisUtil.findMap("app:tb:ls:" + lastName);
                            for (Map.Entry<String,String> e : shows.entrySet()){
                                redisUtil.save("app:tb:ls:"+rname,e.getKey(),e.getValue());
                            }
                            redisUtil.put("app:user:"+name,rname);//绑定备注
                            redisUtil.del("app:tb:ls:"+lastName);//删除之前备注
                            v.put("gzh","1");//已绑定公众号
                            redisUtil.put("app:user:nick:"+rname,LX.toJSONString(v));//将昵称存入
                            msg = parse(str,"尊敬的用户:"+v.getStr("nick")+"\n绑定成功!");
                        }else{
                            msg = parse(str,"暂不支持重复绑定,请联系客服.");
                        }
                    }else{
                        msg = parse(str,"请完整复制关联微信返回的消息!");
                    }
                }else{//查询商品
                    TW t = taoBaoService.findFL(text,rname);
                    if (t == null){
                        msg = parse(str,"暂未找到优惠信息!\n"
                                +"一一一一一温馨提示一一一一\n" +
                                "回复“提现”可以提取当前余.额\n" +
                                "回复“查询”可以获取账.单信息");
                    }else {
                        msg = parseTW(str,t);
                    }
                }
            }else if ("event".equals(getTrim(sb.toString(),"MsgType"))){//关注事件
                if ("subscribe".equals(getTrim(sb.toString(),"Event"))){
                    msg = toImg(str, "RmBeagJC407TpN38-o3rweS-eEzQMkF1NhieYqLJO0g");
                }
            }
        }catch (Exception e){
            log.error("微信错误",e);
        }finally {
            //根据消息自动回复
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter pw = response.getWriter();
            pw.println(msg);
            pw.close();
        }
    }
    public void add(StringBuilder s,List<Order> ls){
        if (LX.isNotEmpty(ls)){
            for (Order o:ls){
                s.append(o.getStatus()+": "+(o.getTitle().substring(0,10))+"...\n");
            }
        }
    }
    //说明:回复图文消息
    /**{ ylx } 2019/7/3 23:07 */
    public String parseTW(String str, TW tg) throws Exception {
        return "<xml>\n" +
                "  <ToUserName>"+getVal(str,"FromUserName")+"</ToUserName>\n" +
                "  <FromUserName>"+getVal(str,"ToUserName")+"</FromUserName>\n" +
                "  <CreateTime>"+System.currentTimeMillis()/1000L+"</CreateTime>\n" +
                "  <MsgType><![CDATA[news]]></MsgType>\n" +
                "  <ArticleCount>1</ArticleCount>\n" +
                "  <Articles>\n" +
                "    <item>\n" +
                "      <Title><![CDATA["+tg.text+"]]></Title>\n" +
                "      <Description><![CDATA["+tg.description+"]]></Description>\n" +
                "      <PicUrl><![CDATA["+tg.picUrl+"]]></PicUrl>\n" +
                "      <Url><![CDATA["+tg.url+"]]></Url>\n" +
                "    </item>\n" +
                "  </Articles>\n" +
                "</xml>";
    }
    //说明:回复文本消息
    /**{ ylx } 2019/7/3 23:07 */
    public String parse(String str,String content){
        return "<xml>\n" +
                "  <ToUserName>"+getVal(str,"FromUserName")+"</ToUserName>\n" +
                "  <FromUserName>"+getVal(str,"ToUserName")+"</FromUserName>\n" +
                "  <CreateTime>"+System.currentTimeMillis()/1000L+"</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA["+content+"]]></Content>\n" +
                "</xml>";

    }
    public String toImg(String str,String media_id){
        return "<xml>\n" +
                "  <ToUserName>"+getVal(str,"FromUserName")+"</ToUserName>\n" +
                "  <FromUserName>"+getVal(str,"ToUserName")+"</FromUserName>\n" +
                "  <CreateTime>"+System.currentTimeMillis()/1000L+"</CreateTime>\n" +
                "  <MsgType><![CDATA[image]]></MsgType>\n" +
                "  <Image>\n" +
                "    <MediaId><![CDATA["+media_id+"]]></MediaId>\n" +
                "  </Image>\n" +
                "</xml>";
    }
    public static String getTrim(String str,String key){
        return getVal(str,key).replace("<![CDATA[","").replace("]]>","");
    }
    public static String getVal(String str,String key) {
        return str.substring(str.indexOf(key)+key.length()+1,str.indexOf("</"+key));
    }


    @RequestMapping("/h/{main}")
    public String toMain(@PathVariable("main") String main, HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        String str = redisUtil.get("app:gw:"+main);
        return "redirect:/main.html?data="+ URLEncoder.encode(str,"utf-8");
    }
    @RequestMapping("/s/{main}")
    public String toShow(@PathVariable("main") String main, HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        return "redirect:/show.html?name="+ URLEncoder.encode(main,"utf-8");
    }
    @RequestMapping("/getShowLs")
    @ResponseBody
    public OS.Page getShowLs(String name, HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        List<Show> shows = redisUtil.find("app:tb:ls:" + name, Show.class);
        shows.sort((o1,o2)->{return o1.getStatus().hashCode()-o2.getStatus().hashCode();});//排序
        Var v = redisUtil.get("app:user:nick:"+name, Var.class);
        Show s = new Show();
        s.setAdd_time(LX.getTime());
        s.setStatus(Order.Status.总提现);
        s.setTitle("尊敬的用户:"+v.getStr("nick")+",已累计提现");
        s.setFx(LX.getBigDecimal(v.get("fx")));
        shows.add(s);
        return new OS.Page(shows);
    }
}
