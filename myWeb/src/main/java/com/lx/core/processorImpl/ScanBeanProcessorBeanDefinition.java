package com.lx.core.processorImpl;

import com.lx.core.anno.Bean;
import com.lx.core.anno.Order;
import com.lx.core.entity.BeanDefinition;
import com.lx.core.processorInterface.ScanBeanDefinitionProcessor;
import com.lx.core.util.ScanPackage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rzy on 2020/1/10.
 */
public class ScanBeanProcessorBeanDefinition extends ScanBeanDefinitionProcessor {

    @Override
    protected Set<BeanDefinition> findAllBeandefinition(String... path) throws Exception {
        Set<Class<Object>> classes = ScanPackage.scan(null,Bean.class,path);
        Set<BeanDefinition> set = new HashSet<>();
        for (Class<?> c : classes){
            Bean b = c.getAnnotation(Bean.class);
            set.add(new BeanDefinition(b.value(),c,b.init()));
        }
        return set;
    }
}
