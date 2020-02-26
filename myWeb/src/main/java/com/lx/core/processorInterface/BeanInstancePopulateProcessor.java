package com.lx.core.processorInterface;


import com.lx.core.ApplicationContext;

/**
 * 创建Bean和填充成员变量
 */
public abstract class BeanInstancePopulateProcessor implements Processor{
    public abstract void process(Object obj, ApplicationContext applicationContext) throws  Exception;//初始化的bean
}
