package com.lx.auth.test;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 游林夕 on 2019/10/8.
 */
public class Test {
    public static String int2chineseNum(int src) {
        final String num[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final String unit[] = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String dst = "";
        int count = 0;
        while(src > 0) {
            dst = (num[src % 10] + unit[count]) + dst;
            src = src / 10;
            count++;
        }
        return dst.replaceAll("零[千百十]", "零").replaceAll("零+万", "万")
                .replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
                .replaceAll("零+", "零").replaceAll("零$", "");
    }

    public static void main(String[]args){
        System.out.println(int2chineseNum(12323));
    }
    public static void main1(String[]args){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("icon.txt")))){
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine())!=null){
                sb.append(line);
            }
            String[]arr = sb.toString().split("</li>");
            for (String str : arr){
//                System.out.println(str);
                Pattern pattern = Pattern.compile("<div class=\"doc-icon-name\">(.*?)</div>");
                Matcher m = pattern.matcher(str);
                if(m.find()){
                    System.out.print("{name:'"+m.group(1)+"'");
                    pattern = Pattern.compile("<div class=\"doc-icon-fontclass\">(.*?)</div>");
                    m = pattern.matcher(str);
                    if(m.find()) {
                        System.out.println(",icon:'" + m.group(1) + "'},");
                    }
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }



}
