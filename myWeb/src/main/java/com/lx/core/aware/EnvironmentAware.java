package com.lx.core.aware;

import com.lx.core.entity.Environment;

/**
 * Created by rzy on 2020/1/17.
 */
public interface EnvironmentAware extends Aware{
    void setEnvironment(Environment ev);
}
