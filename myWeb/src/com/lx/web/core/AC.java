package com.lx.web.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
public class AC {

    private Map<Class<?>,List<Init>> initObj = new HashMap<>();//初始化容器
    private Map<String,Object> ioc = new HashMap<>();//ioc容器
    private Map<Class<?>,List<Object>> type_ioc = new HashMap<>();//按类型容器
    public <T>T getBean(String name){
        return (T) ioc.get(name);
    }
    public <T>T getBean(Class<T> t){
        List<Object> objects = type_ioc.get(t);
        if (objects!= null && objects.size() == 1) return (T)objects.get(0);
        List<Init> inits = initObj.get(t);
        if (inits!= null && inits.size() == 1){

        }

        throw new RuntimeException("存在多个相同类型的对象!");
    }
    private void putObj(String name,Object obj){//存入对象
        if (name==null || name.length() == 0) name = obj.getClass().getSimpleName();
        if (ioc.containsKey(name)) throw new RuntimeException("对象名重复"+name);
        ioc.put(name,obj);
        type_ioc.put(obj.getClass(),obj);
        addInit(obj.getClass(),null);//记录在初始化中
    }
    private void addInit(Class<?> cls,Init init){
        List<Init> list = new ArrayList<>();
        if (initObj.containsKey(cls)){
            list = initObj.get(cls);
        }
        list.add(init);//加到初始
        initObj.put(cls,list);
    }
    private Object getObj(String name,Class<?> cls){//获取对象
        if (name==null || name.length() == 0) name = cls.getSimpleName();
        return ioc.get(name);
    }
    public static AC run(Class<?> main_cls){
        try {
            return new AC(main_cls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public AC(){}
    private AC(Class<?> main_cls) throws Exception {
        if (main_cls.isAnnotationPresent(Bean.class)){
            createObj(main_cls.getAnnotation(Bean.class));//创建对象
            createInitObj();
            fill();//填充对象
        }
    }
    //获取初始化信息
    private void createObj(Bean bean) throws Exception {
        List<Class<?>> ls = ScanPackage.scan(bean.packageScan());
        for (Class<?> cls:ls){
            Object o = cls.newInstance();//构造
            putObj(cls.getAnnotation(Bean.class).value(),o);//添加到容器
            for (Method method : cls.getDeclaredMethods()){
                if (method.isAnnotationPresent(Bean.class)){
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0){//方法没有参数
                        putObj(method.getAnnotation(Bean.class).value() ,method.invoke(o));
                    }else{//有参数,需要稍后进行加载
                        addInit(method.getReturnType() ,new Init(o,method));
                    }
                }
            }
        }
    }

    private void createInitObj() throws Exception {
        for (List<Init> list: initObj.values()){
            for (Init i : list){
                if (i != null){

                }
            }
        }
    }
    private void fill() throws IllegalAccessException {
        for (Object o : ioc.values()){
            for (Field f : o.getClass().getDeclaredFields()){
                if (f.isAnnotationPresent(Bean.class)){
                    f.setAccessible(true);
                    f.set(o,getObj(f.getAnnotation(Bean.class).value(),f.getType()));
                }
            }
        }
    }

    static class ScanPackage {
        public static List<Class<?>> scan(String...scans) throws Exception {
            List<Class<?>> beans = new ArrayList<>();
            for (String page:scans){//多个包
                Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(page.replace(".", "/"));//获取当前包的所有类信息
                while (dirs.hasMoreElements()){
                    URL url = dirs.nextElement();// 获取下一个元素
                    String protocol = url.getProtocol();// 得到协议的名称
                    if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                        findAndAddClassesInPackageByFile(page,filePath,beans);
                    }
                }
            }
            return beans;
        }
        private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, List<Class<?>> classes) throws ClassNotFoundException {
            File dir = new File(packagePath);
            if (!dir.exists() || !dir.isDirectory()) return;
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {// 如果是目录 则继续扫描
                    findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
                } else if (file.getName().endsWith(".class")){//去掉.class
                    Class<?> c = Class.forName((packageName.charAt(0)=='.'?packageName.substring(1):packageName) + '.' + (file.getName().substring(0, file.getName().length() - 6)));
                    if (c.isAnnotationPresent(Bean.class)){
                        classes.add(c);
                    }
                }
            }
        }
    }

    class Init{
        Method method;
        Object obj;
        public Init(Object obj){
            this(obj,null);
        }
        public Init(Object obj,Method method){
            this.obj = obj;
            this.method = method;
        }
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
    public @interface Bean{
        String value() default "";
        String[] packageScan() default {""};
    }
}
