package org.util.test1;

import org.objectweb.asm.*;

import java.io.FileOutputStream;

/**
 * Created by rzy on 2020/1/19.
 */
public class TestBean {
    public void halloAop() {
        System.out.println("Hello Aop");
        System.out.println(1);
    }
    public void halloAop(String str) {
        System.out.println("Hello Aop");
        System.out.println(1);
    }
    public void halloAop(String str,TestBean testBean) {
        System.out.println("Hello Aop");
        System.out.println(1);
    }
    public TestBean halloAop(int i){
        return null;
    }
}