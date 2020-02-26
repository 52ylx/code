package org.util.test1;

/**
 * Created by rzy on 2020/1/20.
 */
public interface MyProxy {
    void before(Object...objs);
    Object after(Object...objects);
}
