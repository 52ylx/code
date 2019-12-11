package com.lx.web.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
@AC.AAC
public class AC {
    public static AC run(){
        return new AC();
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface AAC{

    }
    public static void main(String[]args) throws Exception {
        List<Class<?>> ls = ScanPackage.scan("");
        System.out.println(ls);
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
                    if (c.isAnnotationPresent(AAC.class)){
                        classes.add(c);
                    }
                }
            }
        }
    }

}
