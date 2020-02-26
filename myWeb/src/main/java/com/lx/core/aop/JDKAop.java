package com.lx.core.aop;//说明:

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 创建人:游林夕/2019/4/3 15 57
 */
public class JDKAop{
    public static void main(String [] args){
        A o = (A) Proxy.newProxyInstance(B.class.getClassLoader(), B.class.getInterfaces(), new C(new B()));
        o.add();
    }
}
class C implements InvocationHandler{
    public A a ;
    public C(A a){
        this.a = a;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("开始");
        return method.invoke(a,args);
    }
}
class B implements A{

    @Override
    public void add() {
        System.out.println("啊啊啊");
    }
}
interface A{
    void add();
}