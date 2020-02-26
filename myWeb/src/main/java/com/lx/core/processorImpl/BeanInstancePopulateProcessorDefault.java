package com.lx.core.processorImpl;

import com.lx.core.A;
import com.lx.core.ApplicationContext;
import com.lx.core.anno.Autowired;
import com.lx.core.anno.Value;
import com.lx.core.aware.ApplicationContextAware;
import com.lx.core.aware.EnvironmentAware;
import com.lx.core.processorInterface.BeanInstancePopulateProcessor;
import com.lx.util.LX;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Bean初始化及注入
 */
public class BeanInstancePopulateProcessorDefault extends BeanInstancePopulateProcessor {
    @Override
    public void process(Object obj, ApplicationContext applicationContext) throws Exception {
        //注入各种Aware
        if (obj instanceof ApplicationContextAware){
            ((ApplicationContextAware) obj).setApplicationContext(applicationContext);
        }else if (obj instanceof EnvironmentAware){
            ((EnvironmentAware) obj).setEnvironment(applicationContext.getEnvironment());
        }
        //注入成员变量
        for (Field field : obj.getClass().getDeclaredFields()){
            field.setAccessible(true);
            if (field.isAnnotationPresent(Value.class)){//注入环境变量
                Value v = field.getAnnotation(Value.class);
                field.set(obj,applicationContext.getEnvironment().get(LX.isEmpty(v.value()) ? field.getName() : v.value()));
            }else if (field.isAnnotationPresent(Autowired.class)){//注入类
                Autowired autowired = field.getAnnotation(Autowired.class);//有注解
                if (LX.isEmpty(autowired.value())){//没有值
                    field.set(obj,applicationContext.getBean(field.getName(),field.getType()));
                }else {//注解有值
                    field.set(obj,applicationContext.getBean(autowired.value()));
                }
            }
        }
    }
}
