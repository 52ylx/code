package com.lx.core.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 环境变量
 */
public class Environment {
    private Map<String,String> property;
    public Environment() {
        this.property = new HashMap<>();
    }
    public void put(String key,String val){
        property.put(key,val);
    }
    public void putAll(Map<String,String> m){
        property.putAll(m);
    }
    public String get(String key){
        return property.get(key);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "property=" + property +
                '}';
    }

    public Map<String, String> getProperty() {
        return property;
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }
}
