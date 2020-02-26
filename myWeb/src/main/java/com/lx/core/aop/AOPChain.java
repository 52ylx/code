package com.lx.core.aop;

/**
 * Created by rzy on 2020/1/20.
 */
public abstract class AOPChain {
    private AOPChain chain;
    protected void before(Object...objects){}
    protected Object after(Object re,Object...objects){
        return null;
    }
    protected void afterThrow(Exception e){}

    public final void beforeing(Object...objects){
        if (chain!=null){
            chain.beforeing(objects);
        }
        this.before();
    }
    public final Object aftering(Object re,Object...objects){
        if (chain!=null){
            re = chain.aftering(objects);
        }
        return this.after(re,objects);
    }
    public final void afterThrowing(Exception e){
        if (chain!=null){
            chain.afterThrowing(e);
        }
        this.afterThrow(e);
    }

}
