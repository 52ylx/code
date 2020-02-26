package com.lx.core;

import com.lx.core.anno.Autowired;
import com.lx.core.anno.Bean;
import com.lx.core.anno.PostConstruct;
import com.lx.core.anno.Value;


/**
 * Created by rzy on 2020/1/14.
 */
@Bean(init = "init")
public class A {

    @Autowired()
    private B b;
    @Value("server.port")
    private String c;

    public A(){
        System.out.println("构造方法");
    }
    public void init(){
        System.out.println("初始化方法!");
        System.out.println(toString());
    }

    @PostConstruct
    public void c(){
        System.out.println("PostConstruct");
    }

    @Override
    public String toString() {
        return "A{" +
                "b=" + b +
                ", c='" + c + '\'' +
                '}';
    }
}
