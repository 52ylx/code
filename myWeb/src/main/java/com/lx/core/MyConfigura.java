package com.lx.core;

import com.lx.core.anno.Bean;
import com.lx.core.anno.Configura;

/**
 * Created by rzy on 2020/1/14.
 */
@Configura(packageScan = {""})
public class MyConfigura {
    public static void main(String[]a123) throws Exception {
        new ApplicationContext(MyConfigura.class);
    }


//    @Bean("d")
//    public B a1(A a){
//        System.out.println(a);
//        return new B();
//    }
//
//    @Bean("c")
//    public A a2(A a,A b){
//        System.out.println(a);
//        System.out.println(b);
//        return new A();
//    }
//    @Bean("d")
//    public A a3(A d){
//        System.out.println(d);
//        return new A();
//    }

}
