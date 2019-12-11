package com.lx.wx.service;


import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

@Service
public class KGService {
    @Autowired
    private RedisUtil redisUtil;
    public String getLS(String keyword){
        String s = null;
        try {
            s = LX.doGet("http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=" + URLEncoder.encode(keyword,"utf-8") + "&page=1&pagesize=5&showtype=1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Var var = LX.toMap(s);
        List<Var> ls = var.getVar("data").getList("info");
        if (LX.isEmpty(ls)) return null;
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (Var v :  ls) {
            Var hash = LX.toMap(post(v.getStr("hash")));
            String uuid = LX.uuid32(5);
            redisUtil.put("app:mp3:"+uuid,hash.getVar("data").getStr("play_url"),24*60*60);
            sb.append(v.getStr("singername") + "-" + v.getStr("songname_original") + " : http://52ylx.cn/mp3/" + uuid +"\n");
        }
        return sb.toString();
    }


//    public static void main(String[]args){
//        download();
//    }
    public static void download(){
        Scanner sc = new Scanner(System.in);
        while (true) {
            String keyword = sc.nextLine();
            if (LX.isEmpty(keyword)) continue;
            String s = LX.doGet("http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=" + URLEncoder.encode(keyword) + "&page=1&pagesize=5&showtype=1");
            Var var = LX.toMap(s);
            List<Var> ls = var.getVar("data").getList("info");
            if (LX.isEmpty(ls)) continue;
            Var ms = new Var();
            int i = 0;
            for (Var v :  ls) {
                Var hash = LX.toMap(post(v.getStr("hash")));
                System.out.println(i++ + ":  " + v.getStr("singername") + "-" + v.getStr("songname_original") + " :" + hash.getVar("data").getStr("play_url"));
                String path = "D:/mp3/" + v.getStr("songname_original") + "/" + v.getStr("songname_original") + "-" + v.getStr("singername") + ".mp3";
                v.put("path",path);
                v.put("url",hash.getVar("data").getStr("play_url"));
            }
            System.out.println("请选择!");
            int j = sc.nextInt();
            saveFile(ls.get(j).getStr("path"),ls.get(j).getStr("url"));
            System.out.println("下载完成!");
        }
    }
    public static void saveFile(String path, String url) {
        File file = new File(path);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try(BufferedInputStream bis = new BufferedInputStream(LX.doGetStream(url))){
            try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))){
                byte[] bytes = new byte[1024];
                while ((bis.read(bytes))!=-1){
                    bos.write(bytes);
                }
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static String post(String hash){
        String coo = "kg_mid=f2b21abd14e1eb756b0fdda90b561e84;";
        String url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash="+hash;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Cookie", coo);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));// 获取URLConnection对象对应的输出流
            out.flush();// flush输出流的缓冲
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));// 定义BufferedReader输入流来读取URL的响应
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally{//使用finally块来关闭输出流、输入流
            try{
                if(out!=null)out.close();
                if(in!=null)in.close();
            }catch(IOException ex){}
        }
    }
}