package com.lx.core.processorInterface;

import com.lx.core.ApplicationContext;
import com.lx.core.entity.BeanDefinition;

import java.util.Set;

/**
 * Created by rzy on 2020/1/10.
 */
public abstract class ScanBeanDefinitionProcessor  implements Processor{
    protected abstract Set<BeanDefinition> findAllBeandefinition(String... path) throws Exception;//获取所有的Bean定义信息
    public final void process(ApplicationContext applicationContext, String[]scans) throws Exception{
        Set<BeanDefinition> allBeandefinition = findAllBeandefinition(scans);
        applicationContext.putBeamDefintions(allBeandefinition);
    }
}
