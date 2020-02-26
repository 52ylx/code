package com.lx.core.util;

import com.lx.core.anno.Order;
import com.lx.core.processorInterface.Processor;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;

public class ScanPackage {
    private ScanPackage(){}
    //说明:对指定列表按类上Order排序
    /**{ ylx } 2020/1/16 15:19 */
    public static void sortByOrder(List ls){
        Collections.sort(ls, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return  (o2.getClass().isAnnotationPresent(Order.class)?o2.getClass().getAnnotation(Order.class).value():0)
                        -(o1.getClass().isAnnotationPresent(Order.class)?o1.getClass().getAnnotation(Order.class).value():0);
            }
        });
    }

    //说明:获取指定类型的所有实现类
    /**{ ylx } 2020/1/13 10:11 */
    public static <T>Set<Class<T>> scan(Class<T> cls, Class<? extends Annotation> anno, String[] scans) throws Exception {
        Set<Class<T>> beans = new HashSet<>();
        for (String page:scans){//多个包
            if (page == null ) continue;
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(page.replace(".", "/"));//获取当前包的所有类信息
            while (dirs.hasMoreElements()){
                URL url = dirs.nextElement();// 获取下一个元素
                String protocol = url.getProtocol();// 得到协议的名称
                if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                    findAndAddClassesInPackageByFile(page,filePath,beans,cls,anno);
                }else if ("jar".equals(protocol)) {//查询jar包里的类
                    findAndAddClassByjar(url,page.replace('.', '/'),page,beans,cls,anno);
                }
            }
        }
        return beans;
    }
    private static <T>void findAndAddClassesInPackageByFile(String packageName, String packagePath, Set<Class<T>> classes,Class<T> cls,Class<? extends Annotation> anno) throws ClassNotFoundException {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) return;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {// 如果是目录 则继续扫描
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes,cls,anno);
            } else if (file.getName().endsWith(".class")){//去掉.class
                Class c = Class.forName((packageName.charAt(0)=='.'?packageName.substring(1):packageName) + '.' + (file.getName().substring(0, file.getName().length() - 6)));
                if (isBean(c,cls,anno)){//不是接口 抽象类 是类的子类
                    classes.add(c);
                }
            }
        }
    }
    private static boolean isBean(Class<?>c,Class<?> cls,Class<? extends Annotation> anno){
        return !c.isInterface() && !Modifier.isAbstract(c.getModifiers())  //不是接口 抽象类
                &&((cls==null)||(cls!=null && cls.isAssignableFrom(c)))     //是类的子类
                &&((anno==null)||(anno!=null&&c.isAnnotationPresent(anno)));//类上有注解
    }
    private static <T>void findAndAddClassByjar(URL url,String packageDirName,String packageName,Set<Class<T>> classes,Class<T> cls,Class<? extends Annotation> anno) throws ClassNotFoundException, IOException {
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
                        if (isBean(c,cls,anno)){//不是接口 抽象类 是类的子类
                            classes.add(c);
                        }
                    }
                }
            }
        }
    }
}