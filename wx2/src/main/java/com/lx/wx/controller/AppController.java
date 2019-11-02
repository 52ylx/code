package com.lx.wx.controller;//说明:

import com.lx.authority.config.OS;
import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.LogUtil;
import com.lx.wx.entity.GZH;
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
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            if ("<![CDATA[text]]>".equals(getVal(sb.toString(),"MsgType"))){//字符串消息
                String text = getTrim(str,"Content").trim();//消息
                if (text.startsWith("tx_")){
                    String txm = text.substring(3);
                    String s = redisUtil.get("app:tx:" + txm);
                    if (LX.isNotEmpty(s)){
                        msg = parse(str,s);
                    }
                }else if ("查询".equals(text)){
                    msg = parseTW(str,new TW("尊敬的用户:请点击查看您的订单!","付款:即已付款订单\n结算:即已收货,可提现的订单!\n提现:即已经提现的订单","http://cdn.duitang.com/uploads/blog/201402/28/20140228230748_VLMv5.thumb.700_0.jpeg","http://www.52ylx.cn/s/" +name));
                }else if ("提现".equals(text)){
                    BigDecimal fx = taoBaoService.getTX(name);
                    if (fx.compareTo(new BigDecimal(0))==0){
                        msg = parse(str,"尊敬的用户:\n已确认收货:0元,不能提现的哦!如有疑问请点击:查询");
                    }else{
                        String uuid = LX.uuid32(5);
                        int time = 2 * 24 * 60 * 60;
                        String txm = "提现"+fx.setScale(2, RoundingMode.CEILING)+"元提现码:"+uuid;
                        redisUtil.put("app:tx:" + uuid,txm,time);
                        msg = parseTW(str,new TW("尊敬的用户:由于公众号无法转账!请添加我的微信!","请长按二维码图片添加好友!","https://mmbiz.qpic.cn/mmbiz_jpg/8el48ibPNafehj6z6pXHfSYFYpZZekP8wlYHtmibLFSdlc5ZMVb65PvhsntOrTdtT2vEHRPIFxU7GxQq6dwgWhBQ/0?wx_fmt=jpeg","http://52ylx.cn/tx.html?txm="+txm));
                    }
                }else{//查询商品
                    TW t = taoBaoService.findFL(text,name);
                    if (t == null){
                        msg = parse(str,"暂未找到优惠信息!\n"
                                +"一一一一一温馨提示一一一一\n" +
                                "回复“提现”可以提取当前余.额\n" +
                                "回复“查询”可以获取账.单信息!");
                    }else {
                        msg = parseTW(str,t);
                    }
                }
            }else if ("event".equals(getTrim(sb.toString(),"MsgType"))){//关注事件
                switch (getTrim(sb.toString(),"Event")){
                    case "subscribe":
                        if (!redisUtil.exists("app:user:nick:"+name)){//查询昵称
                            redisUtil.put("app:user:nick:"+name,new Var("nick",LX.getDate("yyyyMMdd")));//将昵称存入
                        }
                        msg = parse(str,"终于等到您!敬请吩咐!");
                        break;
                    case "CLICK":
                        switch (getTrim(sb.toString(),"EventKey")){
                            case "query":
                                msg = parseTW(str,new TW("尊敬的用户:请点击查看您的订单!","付款:即已付款订单\n结算:即已收货,可提现的订单!\n提现:即已经提现的订单","http://cdn.duitang.com/uploads/blog/201402/28/20140228230748_VLMv5.thumb.700_0.jpeg","http://www.52ylx.cn/s/" +name));
                                break;
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
        s.setStatus(Show.Status.总提现);
        s.setTitle("尊敬的用户:已累计提现:");
        s.setFx(LX.getBigDecimal(v.get("fx")));
        shows.add(s);
        return new OS.Page(shows);
    }

//    @RequestMapping("/token")
//    @ResponseBody
//    public String token(String signature,String timestamp,String nonce,String echostr){
//        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
//        if (signature != null && WeixinCheckoutUtil.checkSignature(signature, timestamp, nonce)) {
//            return echostr;
//        }
//        return null;
//    }
//
//
//
//    public static class WeixinCheckoutUtil {
//
//        // 与接口配置信息中的Token要一致
//        private static String token = "youjp";
//
//        /**
//         * 验证签名
//         *
//         * @param signature
//         * @param timestamp
//         * @param nonce
//         * @return
//         */
//        public static boolean checkSignature(String signature, String timestamp, String nonce) {
//            String[] arr = new String[] { token, timestamp, nonce };
//            // 将token、timestamp、nonce三个参数进行字典序排序
//            // Arrays.sort(arr);
//            sort(arr);
//            StringBuilder content = new StringBuilder();
//            for (int i = 0; i < arr.length; i++) {
//                content.append(arr[i]);
//            }
//            MessageDigest md = null;
//            String tmpStr = null;
//
//            try {
//                md = MessageDigest.getInstance("SHA-1");
//                // 将三个参数字符串拼接成一个字符串进行sha1加密
//                byte[] digest = md.digest(content.toString().getBytes());
//                tmpStr = byteToStr(digest);
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//            content = null;
//            // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
//
//            return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
//        }
//
//        /**
//         * 将字节数组转换为十六进制字符串
//         *
//         * @param byteArray
//         * @return
//         */
//        private static String byteToStr(byte[] byteArray) {
//            String strDigest = "";
//            for (int i = 0; i < byteArray.length; i++) {
//                strDigest += byteToHexStr(byteArray[i]);
//            }
//            return strDigest;
//        }
//
//        /**
//         * 将字节转换为十六进制字符串
//         *
//         * @param mByte
//         * @return
//         */
//        private static String byteToHexStr(byte mByte) {
//            char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
//            char[] tempArr = new char[2];
//            tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
//            tempArr[1] = Digit[mByte & 0X0F];
//            String s = new String(tempArr);
//            return s;
//        }
//        public static void sort(String a[]) {
//            for (int i = 0; i < a.length - 1; i++) {
//                for (int j = i + 1; j < a.length; j++) {
//                    if (a[j].compareTo(a[i]) < 0) {
//                        String temp = a[i];
//                        a[i] = a[j];
//                        a[j] = temp;
//                    }
//                }
//            }
//        }
//
//    }
}
