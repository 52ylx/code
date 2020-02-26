package com.lx.core.util;

/**
 * Created by rzy on 2020/1/15.
 */
public class MyException extends RuntimeException {
    public MyException(String msg) {
        super(msg);
    }

    public static  <T>T throwStr(String msg){
        throw new MyException(msg);
    }
}
