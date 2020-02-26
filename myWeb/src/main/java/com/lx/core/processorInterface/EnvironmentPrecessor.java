package com.lx.core.processorInterface;

import com.lx.core.ApplicationContext;
import com.lx.core.entity.Environment;

/**
 * 环境变量处理器
 */
public abstract class EnvironmentPrecessor extends LoadAllBeanDefinitionAfterProcessor{
    public void processor(ApplicationContext applicationContext){
        this.process(applicationContext.getEnvironment());
    }
    //说明:获取环境变量
    /**{ ylx } 2020/1/17 10:58 */
    public abstract void process(Environment environment);
}
