package com.lx.authority.dao;//说明:

import com.lx.util.LX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 创建人:游林夕/2019/4/28 08 26
 */
@Configuration
public class JedisConfig extends CachingConfigurerSupport {

    private Logger logger = LoggerFactory.getLogger(JedisConfig.class);
    int database;   //数据库
    String password;//密码
//    String autoBackupName;//上传项目名
//    String filename;//redis文件名
    @Bean
    public JedisPool redisPoolFactory(Environment environment){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(Integer.parseInt(Optional.ofNullable(environment.getProperty("spring.redis.pool.max-idle")).orElse("200")));
//        jedisPoolConfig.setMaxWaitMillis(Long.parseLong(Optional.ofNullable(environment.getProperty("spring.redis.pool.max-wait")).orElse("500")));
//        jedisPoolConfig.setMaxTotal(Integer.parseInt(Optional.ofNullable(environment.getProperty("spring.redis.pool.max-active")).orElse("200")));
//        jedisPoolConfig.setMinIdle(Integer.parseInt(Optional.ofNullable(environment.getProperty("spring.redis.pool.min-idle")).orElse("0")));
        String host = Optional.ofNullable(environment.getProperty("spring.redis.host")).orElse("localhost");
        int port = Integer.parseInt(Optional.ofNullable(environment.getProperty("spring.redis.port")).orElse("6379"));
        this.database = Integer.parseInt(Optional.ofNullable(environment.getProperty("spring.redis.database")).orElse("0"));
        this.password = environment.getProperty("spring.redis.password");
//        this.autoBackupName = environment.getProperty("spring.redis.autoBackupName");
//        this.filename = environment.getProperty("spring.redis.filename");
//        LX.exObj(password,"请配置redis密码:spring.redis.password");
        JedisPool jedisPool = new JedisPool(jedisPoolConfig ,host ,port
                ,Integer.parseInt(Optional.ofNullable(environment.getProperty("spring.redis.timeout")).orElse("1000"))
        );
        logger.info("redis地址："+host + ":" + port + " -database:"+database);
        return  jedisPool;
    }
}