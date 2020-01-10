package com.lx.web.core;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@AC.Bean(init = "init")
public class HttpServer {

    public void init(final MyServlet servlet) throws IOException {
        final ServerSocket server = new ServerSocket(8082);//端口 和 最大连接 创建服务
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 1000, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());//创建连接池
        pool.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        final Socket accept = server.accept();//等待连接
                        pool.execute(new Runnable() {
                            @Override
                            public void run() {
                                servlet.doPost(accept);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


   public abstract static class Servlet {
        public abstract Object doPost(Request request);
        public void doPost(Socket accept){
            String s = "";

            BufferedOutputStream bis=null;
            try {
                Object o = doPost(new Request(accept));
                bis = new BufferedOutputStream(accept.getOutputStream());
                if (o instanceof String){
                    InputStream in = this.getClass().getResourceAsStream((String) o);
                    if (in == null){
                        String responseHeader="HTTP/1.1 404 \r\nContent-Type: "+getContentType((String) o)+"; charset=utf-8\r\n\r\n";
                        inToOut(new ByteArrayInputStream(responseHeader.getBytes()),bis);
                        inToOut(new ByteArrayInputStream("文件不存在!".getBytes()),bis);
                        return;
                    }
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
        //将输入流转入输出流
        private static void inToOut(InputStream is, BufferedOutputStream bis) throws IOException {
            byte[] charBuf = new byte[1024];
            int mark;
            BufferedInputStream isr = new BufferedInputStream(is);
            while ((mark = isr.read(charBuf)) != -1) {
                bis.write(charBuf);
                if (mark < charBuf.length) break;
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
    public static class Request{
        public String url;
        public Map<String,String> parameter;
        public String body;
        public Request(Socket socket) throws IOException {
            StringBuilder builder = new StringBuilder();
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            char[] charBuf = new char[1024];
            int mark;
            while ((mark = isr.read(charBuf)) != -1) {
                builder.append(charBuf, 0, mark);
                if (mark < charBuf.length) break;
            }
            if ("".equals(builder.toString())) return;
            String line = builder.substring(0, builder.indexOf("\r\n"));
            url = URLDecoder.decode(line.split("\\s")[1]);
            if ("/".equals(url)){
                url = "/index.html";
            }else if (url.indexOf("?")!=-1){
                url = url.substring(0,url.indexOf("?"));
            }
            body = builder.substring(builder.indexOf("\r\n\r\n")+4);
        }
    }

}
