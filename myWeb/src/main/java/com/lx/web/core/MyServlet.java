package com.lx.web.core;


/**
 * Created by rzy on 2019/12/12.
 */
@AC.Bean()
public class MyServlet extends HttpServer.Servlet {
    @AC.Bean("myService")
    private MyService myService;
    @Override
    public Object doPost(HttpServer.Request request) {
        return request.url;
    }
}
