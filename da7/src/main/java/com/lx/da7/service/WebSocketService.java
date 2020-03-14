package com.lx.da7.service;

import com.lx.authority.config.OS;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rzy on 2020/2/13.
 */
@Service("WebSocketService")
@ServerEndpoint(value = "/WS/{userId}")
public class WebSocketService {

    private static CoreService coreService;

    @PostConstruct
    public void init(){
        coreService = OS.getBean(CoreService.class);
    }

    static Logger log= LoggerFactory.getLogger(WebSocketService.class);
    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<String,WebSocketService> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private String userId="";

    public String getUserId() {
        return userId;
    }

    public static ConcurrentHashMap<String, WebSocketService> getWebSocketMap() {
        return webSocketMap;
    }

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        synchronized (this){
            this.session = session;
            this.userId=userId;
            if(webSocketMap.containsKey(userId)){
                webSocketMap.remove(userId);
                webSocketMap.put(userId,this);
            }else{
                webSocketMap.put(userId,this);
                addOnlineCount();
            }
            log.info("用户连接:"+userId+",当前在线人数为:" + getOnlineCount());
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() throws IOException {
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            subOnlineCount();
        }
        coreService.onClose(userId);
        log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:"+userId+",报文:"+message);
        try {
            Var var = LX.toMap(message);
            if (LX.isEmpty(var) || LX.isEmpty(var.getStr("method"))) return;
            var.put("user",userId);
            OS.invoke(var);
        }catch (Exception e){
            log.info("客户端发送过来的消息,出现错误:"+e);
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:"+this.userId+",原因:"+error.getMessage());
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    //说明:批量发送消息
    /**{ ylx } 2020/3/11 21:51 */
    public static void sendInfo(String msg,List<Var> ls){
        try {
            for (Var var : ls){
                sendInfo(msg,var.getStr("user"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message,@PathParam("userId") String userId) {
        log.info("发送消息到:"+userId+"，报文:"+message);
        if(LX.isNotEmpty(userId)&&webSocketMap.containsKey(userId)){
            try {
                webSocketMap.get(userId).sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            log.error("用户"+userId+",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketService.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketService.onlineCount--;
    }
}
