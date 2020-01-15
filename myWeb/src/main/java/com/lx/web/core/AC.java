package com.lx.web.core;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;

public class AC {
    private Map<String,Object> ioc = new HashMap<>();//ioc容器
    public <T>T getBean(String name){
        return (T) ioc.get(name);
    }
    private void putObj(String name,Object obj){//存入对象
        if (name==null || name.length() == 0) name = obj.getClass().getSimpleName();
        if (ioc.containsKey(name)) throw new RuntimeException("对象名重复"+name);
        ioc.put(name,obj);
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
    private AC(Class<?> main_cls) throws Exception {
        if (main_cls.isAnnotationPresent(Bean.class)){
            createObj(main_cls.getAnnotation(Bean.class).packageScan());//创建对象
            fill();//填充对象 并调用初始化方法
        }
    }
    //获取初始化信息
    private void createObj(String ... pages) throws Exception {
        Set<Class<?>> ls = ScanPackage.scan(pages);
        for (Class<?> cls:ls){
            Object o = cls.newInstance();//构造
            putObj(cls.getAnnotation(Bean.class).value(),o);//添加到容器
            for (Method method : cls.getDeclaredMethods()){
                if (method.isAnnotationPresent(Bean.class)){
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0){//方法没有参数
                        putObj(method.getAnnotation(Bean.class).value() ,method.invoke(o));
                    }else{
                        throw new Exception("函数不能设置参数!"+cls.getName()+"."+method.getName());
                    }
                }
            }
        }
    }
    //填充bean的成员变量
    private void fill() throws Exception {
        for (Object o : ioc.values()){
            for (Field f : o.getClass().getDeclaredFields()){
                if (f.isAnnotationPresent(Bean.class)){
                    f.setAccessible(true);
                    f.set(o,getObj(f.getAnnotation(Bean.class).value(),f.getType()));
                }
            }
            Bean bean = o.getClass().getAnnotation(Bean.class);
            if (bean == null)continue;
            String init = bean.init();
            if (!"".equals(init)){//调用初始化方法
                Method[] methods = o.getClass().getDeclaredMethods();
                for (Method m : methods){
                    if (m.getName().equals(init)){
                        Class<?>[] types = m.getParameterTypes();
                        if (types.length>0){
                            Object [] objs = new Object[types.length];
                            for (int i = 0;i<types.length;i++){
                                objs[i] = getObj(null,types[i]);
                            }
                            m.invoke(o,objs);//调用初始化方法
                        }else{
                            m.invoke(o);//调用初始化方法
                        }
                    }
                }

            }
        }
    }

    static class ScanPackage {
        public static Set<Class<?>> scan(String...scans) throws Exception {
            Set<Class<?>> beans = new HashSet<>();
            for (String page:scans){//多个包
                if (page == null) continue;
                Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(page.replace(".", "/"));//获取当前包的所有类信息
                while (dirs.hasMoreElements()){
                    URL url = dirs.nextElement();// 获取下一个元素
                    String protocol = url.getProtocol();// 得到协议的名称
                    if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                        findAndAddClassesInPackageByFile(page,filePath,beans);
                    }else if ("jar".equals(protocol)) {//查询jar包里的类
                        findAndAddClassByjar(url,page.replace('.', '/'),page,beans);
                    }
                }
            }
            return beans;
        }
        private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, Set<Class<?>> classes) throws ClassNotFoundException {
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
        private static void findAndAddClassByjar(URL url,String packageDirName,String packageName,Set<Class<?>> classes) throws ClassNotFoundException, IOException {
            Enumeration<JarEntry> entries = ((JarURLConnection) url.openConnection()).getJarFile().entries();// 从此jar包 得到一个枚举类
            while (entries.hasMoreElements()) {// 同样的进行循环迭代
                JarEntry entry = entries.nextElement();// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                String name = entry.getName();
                if (name.charAt(0) == '/') {name = name.substring(1);}//如果是以/开头的 获取后面的字符串
                if (name.startsWith(packageDirName)) {// 如果前半部分和定义的包名相同
                    int idx = name.lastIndexOf('/');
                    if ((idx != -1)) {// 如果可以迭代下去 并且是一个包
                        packageName = name.substring(0, idx).replace('/', '.');// 获取包名 把"/"替换成"."
                        if (name.endsWith(".class") && !entry.isDirectory()) {// 如果是一个.class文件 而且不是目录
                            Class c = Class.forName(packageName + '.' + (name.substring(packageName.length() + 1, name.length() - 6)));//去掉后面的".class" 获取真正的类名
                            if (c.isAnnotationPresent(Bean.class)){
                                classes.add(c);
                            }
                        }
                    }
                }
            }
        }
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
    public @interface Bean{
        String value() default "";//bean ID
        String[] packageScan() default {""};//扫包
        String init() default "";//初始化方法
    }
}
