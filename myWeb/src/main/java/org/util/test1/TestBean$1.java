package org.util.test1;

import com.lx.core.aop.AOPChain;

/**
 * Created by rzy on 2020/1/20.
 */
public class TestBean$1 extends TestBean {
    public TestBean obj;
    public AOPChain chain;

    public void halloAop() {
        chain.beforeing();
        try {
            obj.halloAop();
        }catch (Exception e){
            chain.afterThrowing(e);
        }
        chain.aftering(null);
    }
    public void halloAop(String a) {
        chain.beforeing(a);
        try {
            obj.halloAop(a);
        }catch (Exception e){
            chain.afterThrowing(e);
        }
        chain.aftering(null,a);
    }


    public void halloAop(String str,TestBean testBean) {
        chain.beforeing(str,testBean);
        try {
            obj.halloAop(str,testBean);
        }catch (Exception e){
            chain.afterThrowing(e);
        }
        chain.aftering(null,str,testBean);
    }
    public TestBean halloAop(int a){
        chain.beforeing(a);
        Object o = null;
        try {
            o = obj.halloAop(a);
        }catch (Exception e){
            chain.afterThrowing(e);
        }
        return (TestBean) chain.aftering(o,a);
    }
}
