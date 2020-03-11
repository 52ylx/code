package com.lx.da7.service;

import com.lx.authority.config.Authority;
import com.lx.authority.config.OS;
import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import javafx.scene.chart.ValueAxis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Service("CoreService")
public class CoreService {

    private final static String REDIS = "DA7:";
    @Autowired
    private RedisUtil redisUtil;

    private void put(String key,Object val){
        redisUtil.put(REDIS+key,val);
    }
    private void put(String key,Object val,int m){
        redisUtil.put(REDIS+key,val,60*m);
    }
    private String get(String key){
        return redisUtil.get(REDIS+key);
    }
    private void del(String key){
        redisUtil.del(REDIS+key);
    }

    @PostConstruct
    public void init(){
        put("wait4",new Var());
    }

    //说明:随机匹配中
    /**{ ylx } 2020/3/11 20:52 */
    public synchronized void random(Var var) throws InterruptedException {
        String user = var.getStr("user");//获取当前用户id
        int room = var.getInt("room");//创建几人间
        Var ls = LX.toMap(get("wait" + room));//获取等待列表
        if (ls.size()==3){
            //0 0牌 1 1牌 2.2牌 3.3牌 4.底牌 5.状态(0准备 1发牌 2抢庄 3.扣底 4.反牌 5.出牌) 6.倍数 7.叫主人 8叫主类型 9.主类型
            //10 0出牌 11 1出牌 12 2出牌 13 3出牌 14.当前谁大 15.当前第几手 16.先手几张 17.先手类型 18.先手 19.当前操作者
            //20 0号分数 21 1分数 22 2分数 23 3分数
            //创建房间信息
            Var roomInfo = new Var("cp", new Object[24]);
            //开始发牌
            //添加用户到缓存
            put("wait4",new Var());//清空等待队列
        }else{
            ls.put(user,var);
            put("wait"+room+":"+user,ls);//加入等待列表 等待
            for (String u : (Set<String>)ls.keySet()){
                WebSocketService.sendInfo(LX.toJSONString(new Var("state",1).put("msg",ls.size()+"/"+room)),u);//发送有几个人在等待
            }
        }
    }

    public void onClose(String user){
        del("wait4:"+user);//删除等待队列
        del("wait5:"+user);//删除等待队列
    }

    //说明:创建房间方法
    /**{ ylx } 2020/3/11 19:40 */
    public void create_room(Var var){
//        String user = var.getStr("user");//获取当前用户id
//        String room = var.getStr("room");//创建几人间
//        Var roomInfo = new Var();
//        roomInfo.put("No",LX.uuid(5));//设置房间号
//        roomInfo.put("lastTime",System.currentTimeMillis());//记录房间信息
//        roomInfo.put("user",user);//记录房主id
//        roomInfo.put("room",room);//记录房间类型
//        List ls = (List) data.get(room);//获取房间列表
//        ls.add(roomInfo);//添加到房间列表
//        Var users = (Var) data.get("users");//获取用户列表
//        users.put(user,roomInfo.getStr("No"));//存入用户的房间号
    }
    //说明:登陆 配合 sys/login?cls=CoreService
    /**{ ylx } 2020/3/11 16:08 */
    public Map login(Var map) throws Exception {
        LX.exMap(map,"username");
        HttpServletRequest req = OS.get("req");
        String token = OS.login(req,new OS.User(map.getStr("username"),"","",""),null);
        return LX.toMap("{result='success',token='{0}'}",token);
    }
}
