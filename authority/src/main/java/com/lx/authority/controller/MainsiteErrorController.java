package com.lx.authority.controller;//说明:


import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**[2019/7/3]说明: 如果页面不存在返回错误页
   __ _ _  __    ___   __    __   __ _ _
  / / / /  \#\  /#/#|  \#\  /#/   \ \ \ \
 / / / /    \#--#/|#|   \#\/#/     \ \ \ \
 \ \ \ \     [##] |#|___/#/\#\     / / / /
  \_\_\_\    [##] |####/#/  \#\   /_/_/*/
@Controller
public class MainsiteErrorController implements ErrorController,EnvironmentAware {

    private String e404="error/404";
    private String e500="error/500";

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request){
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(statusCode == 401){
            return e404;
        }else if(statusCode == 404){
            return e404;
        }else if(statusCode == 403){
            return e404;
        }else{
            return e500;
        }

    }
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.e404 = Optional.ofNullable(environment.getProperty("server.e404")).orElse("error/404");
        this.e500 = Optional.ofNullable(environment.getProperty("server.e500")).orElse("error/500");
    }
}
