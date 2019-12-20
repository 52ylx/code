package test;

import com.lx.web.core.AC;
import com.lx.web.http.Servlet;

/**
 * Created by rzy on 2019/12/12.
 */
@AC.Bean
public class MyServlet extends Servlet {
    @AC.Bean
    private C c;
    @Override
    public Object doPost() {
        return c.getA();
    }
}
