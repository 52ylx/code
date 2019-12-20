package test;

import com.lx.web.core.AC;
import com.lx.web.http.HttpServer;
import com.lx.web.http.Servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

@AC.Bean
public class Test {

    public static void main(String[]args) throws Exception {
        AC run = AC.run(Test.class);
        new HttpServer(8080,run.getBean(MyServlet.class));
    }

}
