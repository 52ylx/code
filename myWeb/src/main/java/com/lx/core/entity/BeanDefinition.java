package com.lx.core.entity;

import java.lang.reflect.Method;

//说明:Bean的定义信息
/**{ ylx } 2020/1/13 10:54 */
public class BeanDefinition{
    private String id;//id名
    private Class<?> type;//使用构造生成对象
    private Method method;//使用方法生成对象
    private Object obj;//使用方法生成对象的对象
    private String init;//初始化方法
    public BeanDefinition(String id,Class<?> type){
        this(id,type,null,null,null);
    }
    public BeanDefinition(String id,Class<?> type,String init){
        this(id,type,null,null,init);
    }
    public BeanDefinition(String id,Class<?> type,Method method,Object obj,String init){
        this.id = id;
        this.type = type;
        this.method  = method;
        this.obj = obj;
        this.init = init;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object o) {
        this.obj = obj;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanDefinition that = (BeanDefinition) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (obj != null ? !obj.equals(that.obj) : that.obj != null) return false;
        if (init != null ? !init.equals(that.init) : that.init != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (obj != null ? obj.hashCode() : 0);
        result = 31 * result + (init != null ? init.hashCode() : 0);
        return result;
    }
}