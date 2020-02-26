package com.lx.core.processorImpl;

import com.lx.core.ApplicationContext;
import com.lx.core.anno.PostConstruct;
import com.lx.core.entity.BeanDefinition;
import com.lx.core.processorInterface.BeanInitProcessor;
import com.lx.util.LX;
import org.util.ASMUtil;

import java.lang.reflect.Method;

/**
 * 调用初始化方法
 */
public class BeanInitProcessorDefault extends BeanInitProcessor {
    @Override
    public void process(ApplicationContext ac,String id) throws Exception {
        BeanDefinition beanDefinition = ac.getBeanDefinition(id);
        Class<?> cls = beanDefinition.getType();
        Object obj  = ac.getBean(id);
        if (LX.isNotEmpty(beanDefinition.getInit())){//调用初始化方法
            cls.getDeclaredMethod(beanDefinition.getInit()).invoke(obj);
        }
        for (Method m : cls.getDeclaredMethods()){
            if (m.isAnnotationPresent(PostConstruct.class)){
                m.invoke(obj);
            }
        }
    }
}
