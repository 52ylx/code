package com.lx.core.processorInterface;

import com.lx.core.ApplicationContext;

/**
 * 扫描所有的Bean定义信息后执行
 */
public abstract class LoadAllBeanDefinitionAfterProcessor implements Processor{
    public abstract void processor(ApplicationContext applicationContext);
}
