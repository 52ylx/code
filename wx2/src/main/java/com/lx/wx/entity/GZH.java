package com.lx.wx.entity;

/**
 * Created by 游林夕 on 2019/10/24.
 */
public class GZH {
    public String text;
    public static GZH newInstance(String text){
        GZH g = new GZH();
        g.text = text;
        return g;
    }

    @Override
    public String toString() {
        return "GZH{" +
                "text='" + text + '\'' +
                '}';
    }
}
