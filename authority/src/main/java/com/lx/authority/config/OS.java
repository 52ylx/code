package com.lx.authority.config;//说明:

/**
 * 创建人:游林夕/2019/4/28 09 04
 */

import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class OS implements ApplicationContextAware,EnvironmentAware {

    private static Logger log = LoggerFactory.getLogger(OS.class);
    //超级管理员 用户名和密码
    public static String ROOTNAME = "";
    static String ROOTPASS = "";
    //存入redis
    private static final String USER_TOKEN = "system:login:token:";
    //token超时时间
    private static int token_timeout;
    //尝试登录错误次数
    private static int minuteLimit;
    //单点登录
    private static String server_login_single;
    private static final ThreadLocal<Map<String, Object>> tl = new ThreadLocal() {
        protected Map<String, Object> initialValue() {
            return new HashMap(4);
        }
    };
    private static RedisUtil redisUtil;
    private static Environment environment;
    static String sever_web_log;

    /** 从缓存中移除登录信息*/
    public static void logout(HttpServletRequest request){
        removeUser(request);
    }

    /**
     * username , 用户对应的库里的信息
     * func 返回true则登录成功 false 登录失败
     */
    public static String login(HttpServletRequest request,String username, Function<User,Boolean> func){
        return login(request,username,null,func);
    }
    public static String login(HttpServletRequest request,String username,Object custom, Function<User,Boolean> func){
        User user = null;
        if (ROOTNAME.equals(username)){
            user = new User(ROOTNAME,ROOTPASS,"#menus#","#btns#");
        }else{
            Var u = redisUtil.find("system:user",Var.class,username);
            LX.exObj(u,"没有找到用户信息!");
            Var role = redisUtil.find("system:role",Var.class,u.getStr("role"));
            LX.exObj(role,"没有绑定角色权限!");
            user = new User(u.getStr("id"),u.getStr("password"),role.getStr("menus"),role.getStr("btns"));
        }
        return login(request,user,custom,func);
    }
    public static String login(HttpServletRequest request,User user, Object custom){
        return login(request,user,custom,(u)->{return true;});
    }
    public static String login(HttpServletRequest request,User user,Object custom, Function<User,Boolean> func){
        String token = LX.uuid();
        long ipLimit = getIpLimit(request,user.getName(),()->{//
            if (func.apply(user)){
                user.setCustom(custom);
                saveUser(request,token,user);
            }else{
                LX.exMsg("登陆验证失败!");
            }
            return true;
        });
        if (ipLimit>0) LX.exMsg("请{0}秒后重试!",ipLimit);
        return token;
    }
    private static void saveUser(HttpServletRequest request, String token, User user){
        user.setToken(token);
        //登陆成功,返回登陆TOKEN
        if ("1".equals(server_login_single)){
            //单点登录
            redisUtil.del(USER_TOKEN+redisUtil.get("system:login:user_token:"+user.getName()));//删除之前的token
            redisUtil.put("system:login:user_token:"+user.getName(),token);//将当前用户的token记住
            request.getSession().removeAttribute("token");//移除登录使用
//            request.getSession().removeAttribute("user");//移除登录使用
        }
        redisUtil.put(USER_TOKEN+token,user,token_timeout);//缓存
        request.getSession().setAttribute("token",token);//移除登录使用
//        request.getSession().setAttribute("user",user);//移除登录使用
        request.getSession().setMaxInactiveInterval(token_timeout);//超时
    }
    /** 移除用户*/
    private static void removeUser(HttpServletRequest request){
        String token = (String)request.getSession().getAttribute("token");
        if (LX.isNotEmpty(token)){
            redisUtil.del(USER_TOKEN+token);//缓存
            request.getSession().removeAttribute("token");
            request.getSession().removeAttribute("user");
        }
    }
    public static void removeUser(String token){
        if (LX.isNotEmpty(token)){
            redisUtil.del(USER_TOKEN+token);//缓存
        }
    }
    //设置用户
    static boolean setUser(HttpServletRequest request){
//        User user = (User) request.getSession().getAttribute("user");
//        if(user != null){
//            put(USER_TOKEN,user);//存入threadLocal
//            request.getSession().setAttribute("user",user);//移除登录使用
//            request.getSession().setMaxInactiveInterval(token_timeout);//超时
//            return true;
//        }
        User user;
        String token = (String)request.getSession().getAttribute("token");
        if (LX.isEmpty(token)){
            token = request.getParameter("token");
        }
        if (LX.isNotEmpty(token)){
            user = redisUtil.get(USER_TOKEN+token,User.class);
            if (LX.isNotEmpty(user)){
                request.getSession().setAttribute("token",token);//移除登录使用
//                request.getSession().setAttribute("user",user);//移除登录使用
                request.getSession().setMaxInactiveInterval(token_timeout);//超时
                redisUtil.expire(USER_TOKEN+token,token_timeout);//重置超时时间
                put(USER_TOKEN,user);
                return true;
            }
        }
        return false;
    }

    /** 验证方法 */
    static boolean checkMethod(HttpServletRequest request,Authority authority) throws Exception {
        //注解写入指定验证接方法
        if(!authority.classAndMethod().isInterface()){
            if (authority.classAndMethod().newInstance().test(request)){
                return true;
            }
        }else{
            Var var = getParameterMap(request);
            User user = getUser();
            String btns = user.getBtns();
            if (LX.isNotEmpty(btns) &&("#btns#".equals(btns) || new HashSet<>(Arrays.asList(btns.split(","))).contains(var.get("method")))){
                return true;
            }
        }
        return LX.exMsg("没有该接口权限");
    }

    /**返回用户*/
    public static User getUser(){
        return get(USER_TOKEN);
    }

    public static  <T>T get(String key){
        return (T) tl.get().get(key);
    }
    public static void put(String key,Object obj){
        tl.get().put(key,obj);
    }
    public static void remove(){
        tl.remove();
    }
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        redisUtil = applicationContext.getBean(RedisUtil.class);
    }

    /**
     * 通过name获取 Bean.
     */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    public static <T>T getBean(Class<T> t){
        return applicationContext.getBean(t);
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.token_timeout = Integer.parseInt(Optional.ofNullable(getProperty("server.token.timeout")).orElse(10*60+""));
        this.server_login_single = Optional.ofNullable(getProperty("server.login.single")).orElse("1");
        this.minuteLimit = Integer.parseInt(Optional.ofNullable(getProperty("server.login.minuteLimit")).orElse("3"));
        this.sever_web_log = getProperty("sever.web.log");
        this.ROOTNAME = Optional.ofNullable(getProperty("server.sys.rootname")).orElse("admin");
        this.ROOTPASS = Optional.ofNullable(getProperty("server.sys.rootpass")).orElse("123456");
    }
    public static String getProperty(String key){
        return environment.getProperty(key);
    }
    /** 根据key 获取方法*/
    public static Var getMethod(String method){
        LX.exObj(method,"方法名不能为空!");
        return redisUtil.find("system:service",Var.class,method);
    }
    public static Object invoke(Var map) throws Exception {
        LX.exMap(map,"method");
        String[] arr = map.getStr("method").split("\\.");
        if (arr.length != 2) LX.exMsg("接口不存在!");
        Object cls = OS.getBean(arr[0]);
        Method m;
        try {
             m = cls.getClass().getDeclaredMethod(arr[1], new Class[]{Var.class});
        }catch (Exception e){
            return LX.exMsg("不能查找到方法==>" + map.getStr("method"));
        }
        Object res = m.invoke(cls, map);
        if (res instanceof List) return new OS.Page((List) res);
        if (res instanceof OS.Page) return res;
        if (res == null || res instanceof Map) return new OS.Page((Map) res);
        return new OS.Page(LX.toMap(res));
    }
    /** 调用方法*/
    public static Object invokeMethod(Map map) throws Exception {
        LX.exMap(map,"method");
        HashMap<String,String> sc = redisUtil.find("system:service",HashMap.class,map.get("method").toString());
        LX.exObj(sc,"接口服务配置不存在!");
        Method m = null;
        Object cls = null;
        try {
            cls = getBean(sc.get("cls"));
            m = cls.getClass().getDeclaredMethod(sc.get("method"),Map.class);
        } catch (Exception e) {
            LX.exMsg("不能查找到方法==>"+sc.get("cls")+"."+sc.get("method"));
        }
        return m.invoke(cls,map);
    }

    public static Var getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map<?, ?> properties = request.getParameterMap();
        // 返回值Map
        Var returnMap = new Var();
        Iterator<?> entries = properties.entrySet().iterator();

        Map.Entry<String, Object> entry;
        String name = "";
        String value = "";
        Object valueObj =null;
        while (entries.hasNext()) {
            entry = (Map.Entry<String, Object>) entries.next();
            name = (String) entry.getKey();
            valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }
    public static long getIpLimit(HttpServletRequest req,String username, Supplier<Boolean> supp){
        return redisUtil.minuteLimit(getIpAddress(req)+":"+username,minuteLimit,supp);
    }
    /** 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址, */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)){
            return "127.0.0.1";
        }
        return ip;
    }


    public static class User {
        private String name,password,menus,btns,token;
        private Object custom;

        public User(String name, String password, String menus, String btns) {
            this.name = name;
            this.password = password;
            this.menus = menus;
            this.btns = btns;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMenus() {
            return menus;
        }

        public void setMenus(String menus) {
            this.menus = menus;
        }

        public String getBtns() {
            return btns;
        }

        public void setBtns(String btns) {
            this.btns = btns;
        }

        public Object getCustom() {
            return custom;
        }

        public void setCustom(Object custom) {
            this.custom = custom;
        }
    }
    public static class Page {
        private String msg = "";
        private int success = 1;
        private long count = 0;
        private List rows = new ArrayList();
        private Map entity = new HashMap();

        public Page(){}
        public Page(Map map){
            if (map == null) return;
            this.entity = map;
        }
        public Page(List rows){
            if (rows == null) return;
            this.rows = rows;
            this.count = rows.size();
        }
        public Page(List rows, long count){
            this.count = count;
            if (rows == null) return;
            this.rows = rows;
        }
        public Page(String msg){
            this.msg = msg;
            this.success = 0;
        }
        public Page(String msg,int success){
            this.msg = msg;
            this.success = success;
        }
        public static String toLogin(){
            return LX.toJSONString(new OS.Page("请重新登陆",9));
        }
        public String getMsg() {
            return msg;
        }

        @Override
        public String toString() {
            return "Page{" +
                    "msg='" + msg + '\'' +
                    ", success=" + success +
                    ", count=" + count +
                    ", rows=" + rows +
                    ", entity=" + entity +
                    '}';
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
        public int getSuccess() {
            return success;
        }
        public void setSuccess(int success) {
            this.success = success;
        }
        public long getCount() {
            return count;
        }
        public void setCount(long count) {
            this.count = count;
        }
        public List getRows() {
            return rows;
        }
        public void setRows(List rows) {
            this.rows = rows;
        }
        public Map getEntity() {
            return entity;
        }
        public void setEntity(Map entity) {
            this.entity = entity;
        }
    }
}

