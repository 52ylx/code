package lx.com.study;

import com.lx.authority.config.OS;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rzy on 2020/2/13.
 */
@Service("im")
@ServerEndpoint(value = "/LLWS/{userId}")
public class ImService {

    static Logger log= LoggerFactory.getLogger(ImService.class);
    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<String,ImService> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private String userId="";
    public static Object[] cp = new Object[18];//当前牌  0先手 1 右 2 对 3 左 4 倍数 5主 6 先手 7 当前第几手  8出了几张
    // 9 当前谁大
    // 10 [**,主,**,获取分数]
    // 11 先手牌型 12叫的庄 13 底牌 14 [在线,积分]
//    房间
    public final static ConcurrentHashMap<String,Object> fj = new ConcurrentHashMap<>();
    static {
        fj.put("state",0);//准备
        cp[6] = 0;
        cp[10]=new int[4][4];
        cp[14]=new int[4][4];
    }
    public static void main(String[]args) throws IOException {
//        new ImService().start();
    }


    private void init(){
        Object cp14 = cp[14];
        cp = new Object[18];
        cp[4]=1;
        cp[6] = 0;
        cp[10]=new int[4][4];
        cp[14]=cp14;
        fj.remove("self");
        fj.put("state",1);//开始发牌
    }
    private void start() throws IOException {//开始游戏
        init();
        List<Object[]> ls = new ArrayList();
        for (int i =2;i<15;i++){
            ls.add(new Object[]{i,"A"});
            ls.add(new Object[]{i,"A"});
            ls.add(new Object[]{i,"B"});
            ls.add(new Object[]{i,"B"});
            ls.add(new Object[]{i,"C"});
            ls.add(new Object[]{i,"C"});
            ls.add(new Object[]{i,"D"});
            ls.add(new Object[]{i,"D"});
        }
        ls.add(new Object[]{15,"E"});
        ls.add(new Object[]{15,"E"});
        ls.add(new Object[]{16,"F"});
        ls.add(new Object[]{16,"F"});
        Collections.shuffle(ls);
        List ls0 = new MyArrayList();
        List ls1 = new MyArrayList();
        List ls2 = new MyArrayList();
        List ls3 = new MyArrayList();
        List ls4 = new MyArrayList();
        fj.put("ls0",ls0);
        fj.put("ls1",ls1);
        fj.put("ls2",ls2);
        fj.put("ls3",ls3);
        fj.put("ls4",ls4);
        for (int i=0;i<ls.size();i++){
            if (i>99){
                ls4.add(ls.get(i));
                continue;
            }
//            LX.sleep(100);
            switch (i%4){
                case 0:
                    ls0.add(ls.get(i));
                    break;
                case 1:
                    ls1.add(ls.get(i));
                    break;
                case 2:
                    ls2.add(ls.get(i));
                    break;
                case 3:
                    ls3.add(ls.get(i));
                    break;
            }
        }
        fj.put("state",2);//发牌完成 开始抢庄
//        cp[13] = fj.get("ls4");
        pushMsg();
    }

    private String getMyMsg(int i){
        i = i%4;
        if (!fj.contains(i+"")){
            return null;
        }
        int xs = (int) cp[6];//先手者
        String self = (String) fj.get("self");//当前操作者
        MyArrayList ls = (MyArrayList) fj.get("ls"+i);
        if (ls != null) ls.sort(ls.c);
        MyArrayList lls = (MyArrayList) cp[i];//能看到自己叫的
        if (lls != null && lls.size() == 1){//叫7
            Object[]o = new Object[]{lls.get(0)[0],lls.get(0)[1]};
            lls = new MyArrayList();
            lls.add(o);
        }
        int[][] cp10 = new int[4][4];
        cp10[0] = ((int[][])cp[10])[i];
        cp10[1] = ((int[][])cp[10])[(i+1)%4];
        cp10[2] = ((int[][])cp[10])[(i+2)%4];
        cp10[3] = ((int[][])cp[10])[(i+3)%4];
        int[][] cp14 = new int[4][4];
        cp14[0] = ((int[][])cp[14])[i];
        cp14[1] = ((int[][])cp[14])[(i+1)%4];
        cp14[2] = ((int[][])cp[14])[(i+2)%4];
        cp14[3] = ((int[][])cp[14])[(i+3)%4];
        return LX.toJSONString(new Var(new Object[][]{{"xf",ls}
        ,{"an",self==null || i==Integer.parseInt(self)%4 ?1 : 0}
        ,{"state",fj.get("state")}
        ,{"zhu",new Object[]{cp[5],cp[12],cp[4]}}//主 叫主 倍数
        ,{"bf",lls}
        ,{"yf",cp[(i+1)%4]}
        ,{"sf",cp[(i+2)%4]}
        ,{"zf",cp[(i+3)%4]}
        ,{"cp10",cp10}
        ,{"dp",cp[13]}
        ,{"cp14",cp14}
        }));
    }
    private void pushMsg() throws IOException {
        sendInfo(getMyMsg(0), (String) fj.get("0"));
        sendInfo(getMyMsg(1), (String) fj.get("1"));
        sendInfo(getMyMsg(2), (String) fj.get("2"));
        sendInfo(getMyMsg(3), (String) fj.get("3"));
    }
    private void getMsg(String msg,String  userId) throws IOException {
        synchronized (this){
            String self = (String) fj.get("self");
            String userCode = (String) fj.get(userId);//当前用户
            int userCodeInt = Integer.parseInt(userCode);//用户数字
            if (self != null && !self.equals(userCode)){//不是当前操作者
                sendInfo(getMyMsg(Integer.parseInt(userCode)), userId);
            }
            int state = (int) fj.get("state");

            Var var = LX.toMap(msg);
            List<Var> ls = var.getList("ls");
            if (state == 2){//叫
                if (ls.size() == 1 && LX.compareTo(14,ls.get(0).getStr("num"), MathUtil.Type.EQ)){//是7
                    MyArrayList lls = new MyArrayList();
                    lls.add(new Object[]{(int)Double.parseDouble(ls.get(0).getStr("num")),ls.get(0).get("type"),1});
                    cp[userCodeInt] = lls;
                    fj.put("state",3);//抢庄成功 待扣底
                    fj.put("zhuang",userCode);//记录庄
                    ((int[][])cp[10])[userCodeInt][1]=1;
                    fj.put("kou",userCode);//当前扣牌者
                    fj.put("self",userCode);//当前操作者
                    fj.put("fan",0);//当前未有人反牌
                    fj.put("jiao",ls.get(0).get("type"));//庄家叫的7
                    List pai = (List) fj.get("ls"+userCode);//当前抢庄的所有牌
                    for (Object o : (List)fj.get("ls4")){//加入底牌
                        pai.add(o);
                    }
                    fj.put("ls4",new MyArrayList());//将底牌滞空
                    pushMsg();//发送牌
                }
            }else if (state == 3){//扣底
                if (ls.size() == 8){
                    MyArrayList dp = (MyArrayList) fj.get("ls4");//获取底牌
                    MyArrayList sl = (MyArrayList) fj.get("ls"+fj.get(userId));//获取当前扣牌着的牌
                    for (Var v : ls){
                        dp.add(new Object[]{(int)Double.parseDouble(v.getStr("num")),v.getStr("type")});//添加到底牌
                        boolean ex = true;
                        for (Iterator<Object[]> it = sl.iterator();it.hasNext();){//遍历扣牌着的牌
                            Object[] o = it.next();
                            if (LX.compareTo(o[0],v.getStr("num"), MathUtil.Type.EQ)&&v.getStr("type").equals(o[1])){//移除扣牌
                                it.remove();
                                ex = false;
                                break;
                            }
                        }
                        if (ex){
                            LX.exMsg("出现错误!");
                        }
                    }
                    int i = (Integer.parseInt(self)+1)%4;//当前下一位
                    fj.put("self",i+"");//下一位
                    fj.put("state",4);//等待反牌
                    pushMsg();
//                    thread(i);//给当前操作者加时间
                }
            }else if (state == 4){//反牌
                if (LX.isEmpty(ls)){//不反
                    String kou = (String) fj.get("kou");//最后扣牌者
                    int i = (Integer.parseInt(self)+1)%4;//当前下一位
                    int s = (int) fj.get("fan");//当前主
                    if (kou.equals(i+"") || s == 6){//没人反牌 开始庄出牌  或者大王
                        fj.put("state",5);//开始出牌
                        cp[12]=fj.get("jiao");//叫的类型
                        MyArrayList lls = new MyArrayList();
                        lls.add(new Object[]{14,cp[12]});
                        cp[Integer.parseInt((String) fj.get("zhuang"))] = lls;//庄家叫的7
                        fj.put("self","-1");//庄出牌 显示叫的牌型
                        pushMsg();
                        LX.sleep(3000);//等待3秒
                        fj.put("self",fj.get("zhuang"));//庄出牌
                        cp[0]=cp[1]=cp[2]=cp[3]=null;//清空牌
                        cp[5] = s==0?fj.get("jiao"):s==1?"A":s==2?"B":s==3?"C":s==4?"D":s==5?"E":"F";//当前主
                        cp[6] = Integer.parseInt((String) fj.get("zhuang"));//当前牌局先手
                        cp[7] = 0;//当前第0手
                    }else{
                        fj.put("self",i+"");//下一位反
                    }
                }else{
                    int s = (int) fj.get("fan");
                    if (ls.size() == 2 && ls.get(0).getStr("type").equals(ls.get(1).getStr("type"))){
                        int i = 0;
                        switch (ls.get(0).getStr("type")){
                            case "A" : i=1; break;
                            case "B" : i=2; break;
                            case "C" : i=3; break;
                            case "D" : i=4; break;
                            case "E" : i=5; break;
                            case "F" : i=6; break;
                            default: LX.exMsg("反牌类型不对");
                        }
                        if (i>s){//大于反牌类型
                            fj.put("kou",userCode);//最后反的
                            List pai = (List) fj.get("ls"+self);//当前反牌的所有牌
                            for (Object o : (List)fj.get("ls4")){//加入底牌
                                pai.add(o);
                            }
                            fj.put("ls4",new MyArrayList());//将底牌滞空
                            fj.put("fan",i);//反牌
                            cp[5] = ls.get(0).getStr("type");//主
                            cp[4]=(int)cp[4]+1;//翻倍
                            MyArrayList lls = new MyArrayList();
                            lls.add(new Object[]{(int)Double.parseDouble(ls.get(0).getStr("num")),ls.get(0).get("type")});
                            lls.add(new Object[]{(int)Double.parseDouble(ls.get(1).getStr("num")),ls.get(1).get("type")});
                            cp[userCodeInt]= lls;
                            fj.put("state",3);//等待当前扣牌
                        }
                    }
                }
                pushMsg();//发送牌
            }else if (state == 5){//出牌
                String zhu = (String) cp[5];//主
                int xs = (int) cp[6];//先手者
                int i = (int) cp[7];//当前第几手
                int u = (xs+i)%4;//当前出牌者

                if (ChuPai.check(cp,ls,userCode)){//出牌正确
                    cp[8]=ls.size();//出了几张
                    MyArrayList pa = new MyArrayList();
                    MyArrayList sl = (MyArrayList) fj.get("ls"+fj.get(userId));//获取当前打牌着的牌
                    for (Var v : ls){
                        pa.add(new Object[]{(int)Double.parseDouble(v.getStr("num")),v.getStr("type")});
                        boolean ex = true;
                        for (Iterator<Object[]> it = sl.iterator();it.hasNext();){//遍历扣牌着的牌
                            Object[] o = it.next();
                            if (LX.compareTo(o[0],v.getStr("num"), MathUtil.Type.EQ)&&v.getStr("type").equals(o[1])){//移除扣牌
                                it.remove();
                                ex = false;
                                break;
                            }
                        }
                        if (ex){
                            LX.exMsg("出现错误!");
                        }
                    }
                    cp[u]=pa;
                    if (i==3){//最后一手
                        //1.判断谁大
                        int userCodeDa = (int) cp[9];//大的编号
                        //2.记分
                        int fen = ChuPai.getFen(cp);
                        ((int[][])cp[10])[userCodeDa][3]+=fen;//加分
                        //3.等待一秒 清空出牌
                        fj.put("self",-1+"");
                        pushMsg();
                        LX.sleep(3000);
                        cp[0]=cp[1]=cp[2]=cp[3]=null;//清空牌
                        cp[7]=0;
                        if (((List)fj.get("ls0")).size()<=0){//所有牌出完
                            cp[13] = fj.get("ls4");//显示底牌
                            fen = ChuPai.getFen1((MyArrayList) cp[13]);
                            ((int[][])cp[10])[userCodeDa][3]+=(fen<<ls.size());//加分

                            pushMsg();
                            LX.sleep(12000);
                            start();
                            return;
                        }
                        fj.put("self",userCodeDa+"");//大牌出
                        //6 先手 7 当前第几手
                        cp[6] = userCodeDa;

                    }else{//不是
                        cp[7] = 1 + i;//下一手
                        fj.put("self",u+1+"");//下一位
                    }
                    pushMsg();
                }else{//出牌不对
                    sendInfo(getMyMsg(Integer.parseInt(userCode)), userId);
                }
            }//出牌结束
        }
    }
    static class MyArrayList extends ArrayList<Object[]>{
        private int getPoint(Object[] a){
            int i = 0;
            switch ((String)a[1]){
                case "A": i=20; break;
                case "B": i=40; break;
                case "C": i=60; break;
                case "D": i=80; break;
                case "E": i=700; break;
                case "F": i=800; break;
            }
            if ((int) a[0] == 14){//7
                i+=200;
            }
            if (((String)a[1]).equals(cp[5])){//主
                i +=80;
            }
            return (int) a[0] + i;
        }
        class MyCompare implements Comparator<Object[]> {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                return getPoint(o2)-getPoint(o1);
            }
        }
        MyCompare c = new MyCompare();
//        @Override
//        public boolean add(Object[] objects) {
//            boolean b = super.add(objects);
//            this.sort(c);
//            return b;
//        }
    }
    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        synchronized (this){
            this.session = session;
            if ("0".equalsIgnoreCase(userId)){
                userId = LX.uuid(5);
            }
            this.userId=userId;
            if(webSocketMap.containsKey(userId)){
                webSocketMap.remove(userId);
                webSocketMap.put(userId,this);
                //加入set中
            }else{
                webSocketMap.put(userId,this);
                //加入set中
                addOnlineCount();
                //在线数加1
            }
            log.info("用户连接:"+fj.get(userId)+",当前在线人数为:" + getOnlineCount());
            try {
                if (fj.contains(userId)){
                    if ((int)fj.get("state")>0){//恢复断线
                        String i = (String) fj.get(userId);//用户
                        sendInfo(getMyMsg(Integer.parseInt(i)), userId);
                    }
                }else{
                    sendMessage("{userId:\""+userId+"\",msg:\"连接成功\"}");
                    if (fj.get("count")==null){
                        fj.put("0",userId);
                        fj.put(userId,"0");
                        fj.put("count",0);
                    }else{
                        int i = (int)fj.get("count")+1;
                        fj.put("count",i);
                        fj.put(""+i,userId);
                        fj.put(userId,i+"");
                        if (i==3){//人够了
                            start();
                        }
                    }
                }
                ((int[][])cp[14])[Integer.parseInt((String) fj.get(userId))][0]=1;
                pushMsg();//发送牌
            } catch (IOException e) {
                log.error("用户:"+userId+",网络异常!!!!!!");
            }
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() throws IOException {
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        ((int[][])cp[14])[Integer.parseInt((String) fj.get(userId))][0]=0;
        pushMsg();
        log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:"+fj.get(userId)+",报文:"+message);
        try {
            getMsg(message,userId);
        } catch (IOException e) {
            e.printStackTrace();
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


    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message,@PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:"+userId+"，报文:"+message);
        if(LX.isNotEmpty(userId)&&webSocketMap.containsKey(userId)){
            webSocketMap.get(userId).sendMessage(message);
        }else{
            log.error("用户"+userId+",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ImService.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ImService.onlineCount--;
    }
}
