package com.lx.web.core;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by rzy on 2020/1/9.
 */

@AC.Bean(value = "myService")
public class MyService {
    @AC.Bean
    Test test;
    public String name = "c.html";

}
