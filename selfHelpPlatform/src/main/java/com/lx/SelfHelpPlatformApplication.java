package com.lx;

import com.github.pagehelper.PageHelper;
import com.lx.authority.dao.RedisUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.Properties;

@SpringBootApplication
@ComponentScan({"com.lx.authority", "com.lx"})
//@EnableScheduling   // 2.开启定时任务
public class SelfHelpPlatformApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SelfHelpPlatformApplication.class, args);
        run.getBean(RedisUtil.class).exec(jedis -> {//清空缓存
            jedis.flushDB();
        });
    }
    //配置mybatis的分页插件pageHelper
    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","true");
        properties.setProperty("dialect","sqlServer");    //配置mysql数据库的方言
        pageHelper.setProperties(properties);
        return pageHelper;
    }
    //websocket需要注入该类
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
