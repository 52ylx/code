package com.lx.web.core;


@AC.Bean(init = "init")
public class Test {

    public static void main(String[]args) throws Exception {
        AC run = AC.run(Test.class);
    }
}
