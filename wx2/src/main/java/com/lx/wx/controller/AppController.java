package com.lx.wx.controller;//说明:

import com.lx.authority.config.OS;
import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.LogUtil;
import com.lx.wx.entity.GZH;
import com.lx.wx.entity.Show;
import com.lx.wx.entity.TW;
import com.lx.wx.service.MyWxBot;
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


    @RequestMapping("/getYH")
    @ResponseBody
    public OS.Page getYH(String in_title, String name){
        try {
            log.info(name+"  "+in_title);
            TW tw = taoBaoService.findFL(in_title, name);
            LX.exObj(tw,"暂未查找到优惠信息");
            return new OS.Page( LX.toMap(tw));
        }catch (Exception e){
            return new OS.Page("暂未查找到优惠信息!",0);
        }

    }
    @RequestMapping("/a/{main}")
    public String toAll(@PathVariable("main") String main, HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        return "redirect:/all.html?name=lxzz"+ main;
    }
    @RequestMapping("/h/{main}")
    public String toMain(@PathVariable("main") String main, HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        String str = redisUtil.get("app:gw:"+main);
        return "redirect:/main.html?data="+ URLEncoder.encode(str,"utf-8");
    }
    @RequestMapping("/s/{main}")
    public String toShow(@PathVariable("main") String main, HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
        return "redirect:/show.html?name=lxzz"+main;
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
        s.setTitle("已累计提:");
        s.setFx(LX.getBigDecimal(v.get("fx")));
        shows.add(s);
        return new OS.Page(shows);
    }
    @Autowired
    private MyWxBot myWxBot;
    @RequestMapping("/addOrder")
    @ResponseBody
    public void addOrder(String item,String id,String tad){
        Show show = redisUtil.get("app:numiid_pid:" + item + "_" + id, Show.class);
        if (show != null){
            show.setStatus(Show.Status.付款);
            redisUtil.put("app:tb:ls:order:"+tad,show.getName(),40*24*3600);//将订单加入
            redisUtil.save("app:tb:ls:"+show.getName(),tad,show);//将用户订单存入
            try {
                myWxBot.send(show.getName(),show.getTitle()+"付㝟成功!"+show.getFx());
                Var v = redisUtil.get("app:user:nick:"+show.getName(),Var.class);
                if (v.containsKey("bing")){//推荐码是自己 或者有推荐码
                    myWxBot.send(v.getStr("bing"),v.getStr("nick")+";付㝟成功!"
                            +(show.getFx().multiply(new BigDecimal(0.1)).setScale(2,BigDecimal.ROUND_FLOOR)));
                }
            }catch (Exception e){
                log.error("推送消息失败!");
            }
        }
    }
    @RequestMapping("/uOrder")
    @ResponseBody
    public void uOrder(String item,String id,String tad){
        String name = redisUtil.get("app:tb:ls:order:" + tad);
        if (name != null){
            Show s = redisUtil.find("app:tb:ls:"+name,Show.class,tad);
            s.setStatus(Show.Status.结算);
            redisUtil.save("app:tb:ls:"+name,tad,LX.toJSONString(s));//加入到结算中
            try {
                myWxBot.send(name,s.getTitle()+"\n结算成功:"+s.getFx());
                Var v = redisUtil.get("app:user:nick:"+name,Var.class);
                if (v.containsKey("bing")){//推荐码是自己 或者有推荐码
                    Show s1 = new Show();
                    s1.setAdd_time(LX.getTime());
                    s1.setTitle("推荐:"+v.getStr("nick"));
                    s1.setFx(s.getFx().multiply(new BigDecimal(0.1)).setScale(2,BigDecimal.ROUND_FLOOR));
                    s1.setStatus(Show.Status.结算);
                    redisUtil.save("app:tb:ls:"+v.getStr("bing"),tad,s1);//加入到结算中
                    myWxBot.send(v.getStr("bing"),v.getStr("nick")+";结算成功!可提现:"
                            +(s1.getFx()));
                }
            }catch (Exception e){
                log.error("推送消息失败!");
            }
        }
    }

    public static void main(String[] args) {
        //13914880109_108979600467
        //520538406051_108824400102
        String t = LX.getDate("yyyyMMddHHmmss");
        LX.doGet("http://52ylx.cn/addOrder?item=520538406051&id=108824400102&tad="+t);
        System.out.print(t);
        LX.doGet("http://52ylx.cn/uOrder?item=13914880109&id=108979600467&tad="+t);
    }
}
