package lx.com.study;

import com.lx.authority.config.OS;
import com.lx.entity.Var;
import com.lx.util.LX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by rzy on 2020/2/16.
 */
@Controller
@RequestMapping("/app")
public class AppController {

    Logger log = LoggerFactory.getLogger(AppController.class);

    @RequestMapping("/service")
    public Object service(HttpServletRequest request) throws Exception {
        Var pd = OS.getParameterMap(request);
        log.info("--start--"+pd);
        LX.exMap(pd,"method");
        String [] arr = pd.getStr("method").split("\\.");
        if (arr.length!=2)LX.exMsg("接口不存在!");
        Method m = null;
        Object cls = null;
        try {
            cls = OS.getBean(arr[0]);
            m = cls.getClass().getDeclaredMethod(arr[1], new Class[]{Var.class});
        } catch (Exception var5) {
            LX.exMsg("不能查找到方法==>" +pd.get("method"));
        }
        Object res = m.invoke(cls, pd);
        if (res instanceof List){//返回值是List
            res =  new OS.Page((List) res);
        }if (res instanceof OS.Page){

        }else{//返回值是空或是Map
            if (res==null || res instanceof Map){
                res = new OS.Page((Map) res);
            }else{
                res = new OS.Page(LX.toMap(res));
            }
        }
        log.info("--end--");
        return res;
    }
}
