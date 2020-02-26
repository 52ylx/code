package com.lx.core.processorImpl;

import com.lx.core.anno.Bean;
import com.lx.core.anno.Configura;
import com.lx.core.entity.BeanDefinition;
import com.lx.core.processorInterface.ScanBeanDefinitionProcessor;
import com.lx.core.util.ScanPackage;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

//说明:扫描配置类中的@Bean注解注入的Bean
/**{ ylx } 2020/1/14 16:12 */
public class ScanBeanDefinitionProcessorConfigura extends ScanBeanDefinitionProcessor {
    @Override
    protected Set<BeanDefinition> findAllBeandefinition(String... path) throws Exception {
        Set<Class<Object>> classes = ScanPackage.scan(null, Configura.class,path);//扫注解
        Set<BeanDefinition> set = new HashSet<>();
        for (Class<?> cls : classes ){
            Object o = cls.newInstance();//构造
            for (Method method : cls.getDeclaredMethods()){//获取所有方法
                if (method.isAnnotationPresent(Bean.class)){//方法上有@Bean
                    Bean b = method.getAnnotation(Bean.class);
                    set.add(new BeanDefinition(b.value(),method.getReturnType(),method,o,b.init()));
                }
            }
        }
        return set;
    }
}
