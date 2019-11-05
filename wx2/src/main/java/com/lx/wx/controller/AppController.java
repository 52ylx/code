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

}
