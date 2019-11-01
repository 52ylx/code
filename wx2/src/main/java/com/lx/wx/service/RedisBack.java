package com.lx.wx.service;

import com.lx.authority.dao.RedisUtil;
import com.lx.util.LX;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by 游林夕 on 2019/11/1.
 */
@Service
public class RedisBack {
    Logger log = LoggerFactory.getLogger(RedisBack.class);
    @Autowired
    private Environment environment;
    @Autowired
    private RedisUtil redisUtil;



    /**
     * FTP上传单个文件测试
     * 1.连接服务
     * 2.确定你要上传的文件
     * 3.指定你在服务器端存放的位置
     */
    public void testUpload(String path) {
        FTPClient ftpClient = new FTPClient();
        try(FileInputStream fis=new FileInputStream(path)){
            ftpClient.connect("39.106.58.4");
            ftpClient.login("ylx", "17mWBkNM");
            //设置上传目录
            ftpClient.changeWorkingDirectory("/a");
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("utf-8");
            //设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile("dump.rdb", fis);
        }catch (Exception e){
            log.error("上传ftp错误",e);
        }finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
            }
        }
    }
    @PostConstruct
    public void init(){
        String path = environment.getProperty("spring.redis.redisName");
        if (LX.isNotEmpty(path)){
            // 参数：1、任务体 2、首次执行的延时时间 3、任务执行间隔 4、间隔时间单位
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(()->{
                redisUtil.backup();//执行备份
                testUpload(path);
            }, 0, 24, TimeUnit.HOURS);
        }

    }
}
