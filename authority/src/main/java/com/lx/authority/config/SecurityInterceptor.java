package com.lx.authority.config;

import com.lx.util.LX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author blueriver
 * @description 权限拦截器
 * @date 2017/11/17
 * @since 1.0
 */
public class SecurityInterceptor implements HandlerInterceptor {
    Logger log = LoggerFactory.getLogger(SecurityInterceptor.class);
    public static final String HTMl = ".*/.html.*";
    public static final String SYS = ".*/sys/.*";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {//不是方法
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //获取方法上或者类上的注解
        Authority authority = Optional.ofNullable(handlerMethod.getMethod().getAnnotation(Authority.class))
                .orElse(handlerMethod.getMethod().getDeclaringClass().getAnnotation(Authority.class));
        if (authority == null || !authority.value()){//如果接口没有进行验证
            return true;
        }
        try{
            if (OS.setUser(request)){//没有设置成功
                if ("1".equals(OS.sever_web_log) || request.getServletPath().matches(SYS)){
                    log.info(OS.getIpAddress(request)+"==>"+OS.getUser().getName()+"==>请求==>"+request.getRequestURL()+" 参数:"+LX.toJSONString(OS.getParameterMap(request)));
                }
                //是否检查接口权限
                if (!authority.method()) return true;//不用直接返回true
                return OS.checkMethod(request,authority);
            }else{
                response.setContentType("application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(OS.Page.toLogin());
                return false;
            }
        }catch (Exception e){
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(LX.toJSONString(new OS.Page(e.getMessage())));
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        OS.remove();
    }
}
