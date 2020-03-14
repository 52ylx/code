package com.lx.da7.service;

import com.lx.authority.config.OS;
import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.MathUtil;
import com.sun.deploy.xml.GeneralEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("CoreService")
public class CoreService {

    private static final int MAX = 54;
    private static final ConcurrentHashMap<String,Object[]> data = new ConcurrentHashMap();

    private final static String REDIS = "DA7:";
    private static RedisUtil redisUtil;

    public static void put(String key, Object val){
        redisUtil.put(REDIS+key,val);
    }
    public static void put(String key, Object val, int m){
        redisUtil.put(REDIS+key,val,60*m);
    }
    public static String get(String key){
        return redisUtil.get(REDIS+key);
    }
    public static void del(String key){
        redisUtil.del(REDIS+key);
    }

    @PostConstruct
    public void init(){
        redisUtil = OS.getBean(RedisUtil.class);
        put("wait4",new Var());
    }
    //说明:随机匹配中
    /**{ ylx } 2020/3/11 20:52 */
    public synchronized void random(Var var) throws InterruptedException {
        String user = var.getStr("user");//获取当前用户id
        int room = var.getInt("room");//创建几人间
        Var ls = LX.toMap(get("wait" + room));//获取等待列表
        if (ls.size()==room-1){
            put("wait"+room,new Var());//清空等待队列
            removeRoom();//移除不存在的房间
            String roomID = createRoom(var,ls);//开始创建房间
            put("room:"+roomID,1,5);//存入房间 5分钟没人动就移除
        }else{
            ls.put(user,var);
            put("wait"+room,ls);//加入等待列表 等待
            for (String u : (Set<String>)ls.keySet()){
                WebSocketService.sendInfo(LX.toJSONString(new Var("state",1).put("msg",ls.size()+"/"+room)),u);//发送有几个人在等待
            }
        }
    }
    //说明://移除不存在的房间
    /**{ ylx } 2020/3/13 12:26 */
    private void removeRoom() {
        for (Iterator<Map.Entry<String,Object[]>> it = data.entrySet().iterator(); it.hasNext();){
            String room = it.next().getKey();
            if (LX.isEmpty(get("room:"+room))){
                it.remove();//移除不存在的房间
            }
        }
    }


    //0 0牌 1 1牌 2.2牌 3.3牌 4.4牌 5.状态(0准备 1发牌 2抢庄了 3.扣底 4.反牌 5.出牌) 6.倍数 7.叫主人 8叫主类型 9.主类型
    //10 0出牌 11 1出牌 12 2出牌 13 3出牌 14 4出牌 15.当前第几手 16.先手几张 17.先手类型 18.先手 19.当前谁大
    //20 0号分数 21 1分数 22 2分数 23 3分数 24 4分数 25 底牌 26.当前操作者 27.参与者列表，28.几人间
    public static String createRoom(Var var,Var ls){
        //创建房间信息
        Object[]cp = new Object[40];
        String roomID = LX.uuid(5);//房间号
        int room = var.getInt("room");//创建几人间
        cp[28]=room;//几人间
        String user = var.getStr("user");//获取当前用户id
        List<Var> cyz =  new ArrayList<Var>();//参入者列表
        cyz.add(var);//第一位
        CoreService.put("user:"+user,var.put("roomID",roomID),5);//存用户房间号
        int i =1;
        for (Object k : ls.keySet()){//循环加入
            Var userInfo = ls.getVar(k);
            cyz.add(userInfo);//位置
            put("user:"+userInfo.getStr("user"),userInfo.put("roomID",roomID),5);//存用户房间号
        }
        cp[27]=cyz;//参与者
        cp[29]=roomID;//房间号
        start(cp);//开始洗牌
        cp[5]=2;//开始抢庄
        data.put(roomID,cp);//存入
        WebSocketService.sendInfo(LX.toJSONString(new Var("state",2).put("msg",cp)),cyz);//将信息发送给所有参与者
        return roomID;
    }
    //说明:开始发牌
    /**{ ylx } 2020/3/13 11:31 */
    public static void start(Object[] cp){
        cp[5]=cp[6]=1;//开始发牌 倍数
        cp[7]=cp[8]=cp[9]=cp[10]=cp[11]=cp[12]=cp[13]=cp[14]=cp[15]=cp[16]=cp[17]=cp[18]=cp[19]=cp[26]=null;//当前操作者
        cp[20]=cp[21]=cp[22]=cp[23]=cp[24]=0;//分数
        int num = 100/(int)cp[28];//每人几张
        int dpNum = MAX*2-100;//底牌张数
        cp[0] = new Integer[num];
        cp[1] = new Integer[num];
        cp[2] = new Integer[num];
        cp[3] = new Integer[num];
        cp[4] = new Integer[num];
        cp[25] = new Integer[dpNum];//底牌
        List<Integer> arr = new ArrayList<Integer>();
        for (int i=0;i<MAX;i++){
            arr.add(i);
            arr.add(i);
        }
        Collections.shuffle(arr);
        Integer[] ar = arr.toArray(new Integer[0]);
        System.arraycopy(ar,0,cp[0],0,num);
        System.arraycopy(ar,num,cp[1],0,num);
        System.arraycopy(ar,num*2,cp[2],0,num);
        System.arraycopy(ar,num*3,cp[3],0,num);
        if ((int)cp[28] == 5){//5人
            System.arraycopy(ar,num*4,cp[4],0,num);
        }
        System.arraycopy(ar,100,cp[25],0,dpNum);//底牌
    }
    public void no(Var var){

    }
    //0 0牌 1 1牌 2.2牌 3.3牌 4.4牌 5.状态(0准备 1发牌 2抢庄了 3.扣底 4.反牌 5.出牌) 6.倍数 7.叫主人 8叫主类型 9.主类型
    //10 0出牌 11 1出牌 12 2出牌 13 3出牌 14 4出牌 15.当前第几手 16.先手几张 17.先手类型 18.先手 19.当前谁大
    //20 0号分数 21 1分数 22 2分数 23 3分数 24 4分数 25 底牌 26.当前操作者 27.参与者列表，28.几人间,29房间号,30 当前触发者
    //31 最后反牌人
    public void send(Var var){
        if (var== null || LX.isEmpty(var.get("roomID"))||LX.isEmpty(var.get("wz"))){
            return;
        }
        String roomID = var.getStr("roomID");//房间号
        Object[] cp = data.get(roomID);//获取信息
        List<String> ls1 = (List<String>) var.get("msg");//牌
        Integer[] ls = new Integer[0];
        if (ls1 != null){
            ls = msgToInteger(ls1);
        }
        int wz = var.getInt("wz");//位置
        if (cp[26]!=null && (int)cp[26] != wz){//不是当前操作者
            WebSocketService.sendInfo(LX.toJSONString(new Var("state",9).put("msg",cp)),var.getStr("user"));
            return;
        }
        cp[30] = wz;//当前操作者
        Var res = new Var();
        if ((int)cp[5] == 2 && ls.length==1 && ls[0]%13==5){//叫主
            cp[5] = 3;//叫牌了
            cp[31]=cp[26]=cp[7] = wz;//叫主人 操作者 最后反牌
            cp[9]=cp[8] = ls[0]%13;//8叫主类型
        }else if ((int)cp[5] == 3){//扣牌
            if (ls.length != 8){//不是8张
                WebSocketService.sendInfo(LX.toJSONString(new Var("state",9).put("msg",cp)),var.getStr("user"));
                return;
            }
            cp[25] = ls;//底牌
            cp[wz] = addObject((Integer[]) cp[wz],(Integer[]) cp[25]);//加入底牌
            cp[wz] = removePai((Integer[]) cp[wz],ls);//移除底牌
            cp[5]=4;//可以反牌了
            cp[26]=((int)cp[26]+1)%(int)cp[28];//下一位
        }else if ((int)cp[5] == 4) {//反牌
            if (ls.length==0){//不返
                int kou = (int) cp[31];//最后扣牌者
                int i = (wz+1)%(int)cp[28];//当前下一位
                if (i==kou) {//没人反牌 开始庄出牌  或者大王
                    cp[5] = 5;//开始出牌
                    cp[18] = cp[19] = cp[7] = cp[26] = cp[7];//操作者 先手
                    cp[10] = cp[11] = cp[12] = cp[13] = null;//清空牌
                    cp[15] = 0;//当前第0手
                }else{
                    cp[26]=((int)cp[26]+1)%(int)cp[28];//下一位
                }
            }else{
                if (ls.length == 2 && ls[0] == ls[1] ){//两张且一样
                    cp[5] = 3;//反牌了
                    cp[31] = wz;//最后反
//                    cp[wz] = addObject((Integer[]) cp[wz],(Integer[]) cp[25]);//加入底牌
                    cp[31]=cp[26]=cp[7] = wz;//叫主人 操作者 最后反牌
                    cp[9] = ls[0]%13;//8叫主类型
                    cp[6]=(int)cp[6]+1;//倍数
                    cp[10+wz]=ls;//反的牌
                }else{
                    WebSocketService.sendInfo(LX.toJSONString(new Var("state",9).put("msg",cp)),var.getStr("user"));
                    return;
                }

            }
        }else if ((int)cp[5] == 5) {//出牌
            cp[10+wz]=ls;//出的牌
            //最后一手
            //1.判断谁大
            //2.记分
            //3.等待一秒 清空出牌

        }
        WebSocketService.sendInfo(LX.toJSONString(new Var("state","update").put("msg",cp)),(List<Var>) cp[27]);//通知所有人
    }

    //说明:移除等待用户
    /**{ ylx } 2020/3/12 8:54 */
    public void onClose(String user){
        Var ls = LX.toMap(get("wait4"));//获取等待列表
        ls.remove(user);
        put("wait4",ls);
    }

    //说明:创建房间方法
    /**{ ylx } 2020/3/11 19:40 */
    public void create_room(Var var){
    }
    //说明:登陆 配合 sys/login?cls=CoreService
    /**{ ylx } 2020/3/11 16:08 */
    public Map login(Var map) throws Exception {
        LX.exMap(map,"username");
        HttpServletRequest req = OS.get("req");
        String token = OS.login(req,new OS.User(map.getStr("username"),"","",""),null);
        return LX.toMap("{result='success',token='{0}'}",token);
    }

//    public void getCP(Var var){//获取自己的牌
//        Object[] cp = send(var);
//        if (LX.isEmpty(cp)) return;
//        int wz = var.getInt("wz");//位置
//        WebSocketService.sendInfo(LX.toJSONString(new Var("state",9).put("msg",cp)),var.getStr("user"));
//    }
    private static Integer[] msgToInteger(List<String> ls){
        Integer[] arr = new Integer[ls.size()];
        for (int i =0;i<ls.size();i++){
            arr[i] = (int)(Double.parseDouble(ls.get(i)));
        }
        return arr;
    }
    private static Integer[] addObject(Integer[] a1,Integer[]a2){
        Integer [] a = new Integer[a1.length+a2.length];
        System.arraycopy(a1,0,a,0,a1.length);
        System.arraycopy(a2,0,a,a1.length,a2.length);
        return a;
    }
    private static Integer[] removePai(Integer[] a1,Integer[]a2){
        Integer [] a = new Integer[a1.length-a2.length];
        for (Integer o2 : a2){
            for (int i =0;i<a1.length;i++){
                if (o2 == a1[i]){
                    a1[i]=-1;
                    break;
                }
            }
        }
        Arrays.sort(a1);
        System.arraycopy(a1,a2.length,a,0,a.length);
        return a;
    }
}
