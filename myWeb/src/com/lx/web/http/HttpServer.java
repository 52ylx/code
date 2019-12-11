package com.lx.web.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServer {
    public static void main(String[]args) throws IOException {
        ServerSocket server = new ServerSocket(8080);//端口 和 最大连接 创建服务
        ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 1000, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());//创建连接池
        while (true){
            final Socket accept = server.accept();//等待连接
            pool.execute(new Runnable() {
                @Override
                public void run() {

                    String responseHeader="HTTP/1.1 200 \r\nContent-Type: text/html; charset=utf-8\r\n\r\n";
                    try (BufferedOutputStream bis = new BufferedOutputStream(accept.getOutputStream())){
                        inToOut(new ByteArrayInputStream(responseHeader.getBytes()),bis);
                        inToOut(new ByteArrayInputStream("{\"aa\":1}".getBytes()),bis);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

    }
    //将输入流转为字符串
    private static String inToStr(InputStream is) throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(is);
        char[] charBuf = new char[1024];
        int mark;
        while ((mark = isr.read(charBuf)) != -1) {
            builder.append(charBuf, 0, mark);
            if (mark < charBuf.length) {
                break;
            }
        }
        return builder.toString();
    }
    //将输入流转入输出流
    private static void inToOut(InputStream is, BufferedOutputStream bis) throws IOException {
        byte[] charBuf = new byte[1024];
        int mark;
        BufferedInputStream isr = new BufferedInputStream(is);
        while ((mark = isr.read(charBuf)) != -1) {
            bis.write(charBuf);
            if (mark < charBuf.length) {
                break;
            }
        }
    }
    //将对应的文件转为类型
    private String getContentType(String fileName){
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        switch (suffix){
            case "css": return "text/css";
            case "js": return "application/javascript";
            case "jpg":case "jpeg": return "image/jpeg";
            case "png": return "image/png";
        }
        return "text/html";
    }
}
