package com.lx.core.processorInterface;

import com.lx.core.ApplicationContext;

/**
 * 初始化处理器
 */
public abstract class BeanInitProcessor implements Processor{
    public abstract void process(ApplicationContext ac,String id) throws Exception;
}
