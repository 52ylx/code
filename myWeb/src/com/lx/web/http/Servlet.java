package com.lx.web.http;

import java.io.*;
import java.net.Socket;

/**
 * Created by rzy on 2019/12/12.
 */
public abstract class Servlet {
    public abstract Object doPost();
    public void doPost(Socket accept){
        try {
            String s = inToStr(accept.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object o = doPost();
        BufferedOutputStream bis=null;
        try {
            bis = new BufferedOutputStream(accept.getOutputStream());
            if (o instanceof String){
                FileInputStream in = new FileInputStream((String) o);
                String responseHeader="HTTP/1.1 200 \r\nContent-Type: "+getContentType((String) o)+"; charset=utf-8\r\n\r\n";
                inToOut(new ByteArrayInputStream(responseHeader.getBytes()),bis);
                inToOut(in,bis);
                return;
            }
            String responseHeader="HTTP/1.1 200 \r\nContent-Type: text/html; charset=utf-8\r\n\r\n";
            inToOut(new ByteArrayInputStream(responseHeader.getBytes()),bis);
            inToOut(new ByteArrayInputStream(o.toString().getBytes()),bis);
        }catch (Exception e){
            String responseHeader="HTTP/1.1 500 \r\nContent-Type: text/html; charset=utf-8\r\n\r\n";
            try {
                inToOut(new ByteArrayInputStream(responseHeader.getBytes()),bis);
                inToOut(new ByteArrayInputStream(e.toString().getBytes()),bis);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }finally {
            if (bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
