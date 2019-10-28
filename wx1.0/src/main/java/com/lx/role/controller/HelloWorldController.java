package com.lx.role.controller;//说明:

import com.lx.entity.GZH;
import com.lx.entity.TW;
import com.lx.role.dao.RedisUtil;
import com.lx.entity.TGRespose;
import com.lx.entity.WxMessage;
import com.lx.service.MyWxBot;
import com.lx.service.QueryService;
import com.lx.util.LX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * 创建人:游林夕/2019/4/26 19 13
 */
@RestController
public class HelloWorldController {
    @Autowired
    private QueryService queryService;
    @Autowired
    private RedisUtil redisUtil;
    //获取token
    //https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx1640681a4d340625&secret=c87e28af1be4cbc7960f86c8e9dbb5a1

    //获取永久素材列表
    //https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=26_5WZWO34wsX6GVnHJP0bfkeUo01yvgBvJIrBjgRFkgm0uqsYD2PbVFNzi_9C4QI4eEyp_lLCuY4ybSzYuuPL1TmF-jY_WNCw0bj8lvIjKzQAOpTxShLNcbeCCkZo0tzwXP_4-NfWWWhgeGBIXRBLaAIAGQA
    //{"type":"image","offset":0,"count":20}

    //{"item":[
    // {"media_id":"RmBeagJC407TpN38-o3rwU21H6SPPJnGOMdNfNnKWqE","name":"ylx.jpg","update_time":1571966909,"url":"http:\/\/mmbiz.qpic.cn\/mmbiz_jpg\/R6HibJCghziaAMGsdicAUSNqUTDogLiaYK6ha6ENfffR2ibeoVQIia3YHiaa7WiaK6YO8kRoEG0WBTF6kjbDF1wEScZia3Q\/0?wx_fmt=jpeg"}
    // ,{"media_id":"RmBeagJC407TpN38-o3rweS-eEzQMkF1NhieYqLJO0g","name":"jiaocheng.jpg","update_time":1562164740,"url":"http:\/\/mmbiz.qpic.cn\/mmbiz_jpg\/R6HibJCghziaDMCuO0PGSU1S6AJQnWDySicayfabDx00nMgEHUFtjXOJQRnb8qwlyFZlQaufFobzTC6N7O66JaalQ\/0?wx_fmt=jpeg"}
    // ,{"media_id":"RmBeagJC407TpN38-o3rwSsebCAx2IiSw4FLW-IR-vE","name":"cxt.jpg","update_time":1562164717,"url":"http:\/\/mmbiz.qpic.cn\/mmbiz_jpg\/R6HibJCghziaDMCuO0PGSU1S6AJQnWDySic3KzADjXe2VT6umemiaflAJKOOcKfiaf61HDWXuq0XcvibbgiaSx7hC38lA\/0?wx_fmt=jpeg"}]
    // ,"total_count":3,"item_count":3}
    Logger log = LoggerFactory.getLogger(HelloWorldController.class);
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
            String rname = redisUtil.get("app:gzh:"+name);//查询备注名
            if ("<![CDATA[text]]>".equals(getVal(sb.toString(),"MsgType"))){//字符串消息
                String text = getTrim(str,"Content").trim();
                if (text.startsWith("BING_")){//绑定账号
                    try {
                        rname = text.substring(5);
                        rname = rname.substring(0,rname.length()-1);
                        redisUtil.put("app:gzh:"+name,rname);//将微信号绑定到数据库
                        msg = parse(str,"绑定成功!");
                    }catch (Exception e){
                        log.error("微信错误",e);
                        msg = parse(str,"绑定错误!请复制全!");
                    }
                }else{//查询
                    if(LX.isEmpty(rname)){//备注名不存在
                        msg = toImg(str,"RmBeagJC407TpN38-o3rwU21H6SPPJnGOMdNfNnKWqE");
                    }else{//调用查询消息
                        WxMessage m = new WxMessage(text,name,name,rname,"");
                        GZH gzh = queryService.getTW(m);
                        if (gzh instanceof TW){
                            msg = parseTW(str,(TW)gzh);
                        }else{
                            msg = parse(str,gzh.text);
                        }
                    }
                }
            }else if ("event".equals(getTrim(sb.toString(),"MsgType"))){//关注事件
                if ("subscribe".equals(getTrim(sb.toString(),"Event"))){
                    if(LX.isEmpty(rname)) {//备注名不存在
                        msg = toImg(str, "RmBeagJC407TpN38-o3rwU21H6SPPJnGOMdNfNnKWqE");
                    }else{
                        msg = toImg(str, "RmBeagJC407TpN38-o3rweS-eEzQMkF1NhieYqLJO0g");
                    }
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
}
