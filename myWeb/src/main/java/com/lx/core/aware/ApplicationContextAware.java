package com.lx.core.aware;

import com.lx.core.ApplicationContext;

/**
 * Created by rzy on 2020/1/17.
 */
public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext applicationContext);
}
