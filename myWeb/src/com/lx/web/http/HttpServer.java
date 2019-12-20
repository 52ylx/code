package com.lx.web.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServer {
    public HttpServer(int port, final Servlet servlet) throws IOException {
        ServerSocket server = new ServerSocket(port);//端口 和 最大连接 创建服务
        ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 1000, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());//创建连接池
        while (true){
            final Socket accept = server.accept();//等待连接
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    servlet.doPost(accept);
                }
            });
        }
    }

}
