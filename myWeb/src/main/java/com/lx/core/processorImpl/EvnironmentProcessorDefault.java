package com.lx.core.processorImpl;

import com.lx.core.anno.Bean;
import com.lx.core.anno.Order;
import com.lx.core.entity.Environment;
import com.lx.core.processorInterface.EnvironmentPrecessor;
import com.lx.core.util.LXUtil;
import com.lx.util.LX;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 获取环境变量
 */
@Order(100)
public class EvnironmentProcessorDefault extends EnvironmentPrecessor {
    @Override
    public void process(Environment environment) {
        environment.putAll(new HashMap<>(System.getenv()));//系统环境变量
        environment.putAll(new HashMap<String, String>((Map) System.getProperties()));
        environment.putAll(loadProperties("application"));
        String otherProperties = environment.get("server.otherProperties");
        if (LX.isNotEmpty(otherProperties)){
            for (String pro : otherProperties.split(",")){
                environment.putAll(loadProperties(pro.trim()));
            }
        }
    }
    public static Map<String,String> loadProperties(String fileName){
        ResourceBundle prb = ResourceBundle.getBundle(fileName);
        Map<String,String> pp = new HashMap<>();
        for (String key : prb.keySet()){
            pp.put(key , prb.getString(key));
        }
        return pp;
    }
}
