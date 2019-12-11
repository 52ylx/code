package com.lx.authority.controller;

import com.lx.authority.config.Authority;
import com.lx.authority.config.OS;
import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by 游林夕 on 2019/8/23.
 */
@Controller
@Authority()
public class LoginController {
    @Autowired
    private RedisUtil redis;

    @Authority(false)
    @RequestMapping("/login")
    @ResponseBody
    public Map getUsers(@RequestParam(required = false) Map map, HttpServletRequest request) throws Exception {
        LX.exMap(map,"loginname,password,code");
        if (!map.get("code").toString().toLowerCase().equals(LX.str(request.getSession().getAttribute("code")).toLowerCase())) return LX.toMap("{result='codeerror'}");;
        //从缓存中查找用户信息
        //登录
        String token = OS.login(request,map.get("loginname").toString(),null,(user)->{
            return user.getName().equals(map.get("loginname"))&&user.getPassword().equals(map.get("password"));
        });
        return LX.toMap("{result='success',token='{0}'}",token);
    }
    //说明:退出登录
    /**{ ylx } 2019/9/11 11:32 */
    @RequestMapping("/logout")
    @Authority(false)
    public String logout(@RequestParam(required = false) Map map,HttpServletRequest request){
        OS.logout(request);
        return LX.isEmpty(map.get("path"))?"redirect:/index.html":"redirect:/sys/index.html";
    }
    //用户菜单
    @RequestMapping("/menu")
    @ResponseBody
    public List<Var> list_menu(HttpServletRequest request) throws Exception {
        List<Var> ls = null;
        //获取当前用户信息
        OS.User user = OS.getUser();
        if(LX.isEmpty(user)) return null;
        if ("#menus#".equals(user.getMenus())){//如果是amin用户 或 所有权限
            ls =  redis.find("system:menu",Var.class);
        }else{
            ls = redis.findAll("system:menu",Var.class,user.getMenus().split(","));
        }
        if (LX.isNotEmpty(ls)){
            ls.sort((o1,o2)->{return o1.get("id").hashCode()-o2.get("id").hashCode();});
        }
        return ls;
    }
    //获取指定用户指定 pid下的所有菜单 并将子菜单放入 父菜单的 list 中
    @RequestMapping("/format_menu")
    @ResponseBody
    public Object format_menu(HttpServletRequest request,int id) throws Exception {
        List<Var> ls = list_menu(request);
        Map<String,Var> map = new HashMap<>();
        Iterator<Var> it = ls.iterator();
        while (it.hasNext()){
            Var v = it.next();
            v.put("list",new ArrayList<>());
            if (LX.compareTo(v.get("pid"),id, MathUtil.Type.EQ)){//当前父id是指定id
                map.put(v.getStr("id"),v);
            }else if (map.containsKey(v.getStr("pid"))){//当前父id在map中
                map.put(v.getStr("id"),v);
                map.get(v.getStr("pid")).getList("list").add(v);
                it.remove();
            }else{
                it.remove();
            }

        }
        return ls;
    }

    //说明:admin管理员菜单
    /**{ ylx } 2019/5/9 16:14 */
    @RequestMapping("/admin_menu")
    @ResponseBody
    public Object admin_menu(HttpServletRequest request) throws Exception {
        OS.User user = OS.getUser();
        if(LX.isEmpty(user)) return OS.Page.toLogin();
        if (OS.ROOTNAME.equals(user.getName())){
            return LX.toList(HashMap.class,"[" +
                    "{name='接口管理',url='/sys/sys/service/list.html',icon='layui-icon-form'}" +
                    ",{name='菜单管理',url='/sys/sys/menu/list.html',icon='layui-icon-layouts'}" +
                    ",{name='权限管理',url='/sys/sys/role/list.html',icon='layui-icon-vercode'}" +
                    ",{name='用户管理',url='/sys/sys/user/list.html',icon='layui-icon-group'}" +
//                    ",{name='字典管理',url='/sys/sys/dict/list.html',icon='layui-icon-template-1'}" +
//                    ",{name='配置管理',url='/sys/sys/config/list.html',icon='layui-icon-set-fill'}" +
                    "]");
        }
        return LX.toList(HashMap.class,"[]");
    }



    @Authority(false)
    @RequestMapping("/login/code")
    public void generate(HttpServletRequest req,HttpServletResponse response) throws IOException {
        genCaptcha(req,response);
    }

    public static void genCaptcha(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        char[] codeSequence ={'0'};
        char[] codeSequence1 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(90, 40, BufferedImage.TYPE_INT_RGB);
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.decode("#FFFFFF"));
        gd.fillRect(0, 0, 90, 40);
        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Dialog", 0, 22);
        // 设置字体。
        gd.setFont(font);
        // 画边框。
        gd.setColor(Color.decode("#E0AB48"));
        gd.drawRect(0, 0, 90 - 1, 40 - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.decode("#000000"));
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(90);
            int y = random.nextInt(40);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        int c = random.nextInt(3);
        final String num[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String randomCode = "0000";
//        String randomCode = ""+num[a % 10]+(c==0?"加":c==1?"减":"乘")+num[b % 10];
        int i=0;
        for (char d : randomCode.toCharArray()){
            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(random.nextInt(80), random.nextInt(80), random.nextInt(80)));
            gd.drawString(d+"", (++i) * 18, 27);
        }
//        randomCode = ""+(c==0?a+b:c==1?a-b:a*b);
        // 将四位数字的验证码保存到Session中。
        HttpSession session = req.getSession();
        session.setAttribute("code", randomCode.toString());
        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        resp.setContentType("image/jpeg");
        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos = resp.getOutputStream();
        ImageIO.write(buffImg, "jpeg", sos);
        sos.close();
    }
}
