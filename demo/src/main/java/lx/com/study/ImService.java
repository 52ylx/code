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
//    房间
    private final static ConcurrentHashMap<String,Object> fj = new ConcurrentHashMap<>();
    static {
        fj.put("state",0);//准备
    }
    public static void main(String[]args) throws IOException {
        new ImService().start();
    }


    private void init(){
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
        pushMsg();
        System.out.println(fj.get("ls4"));
    }

    private void pushMsg() throws IOException {
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+0)},{"an",1},{"state",fj.get("state")}})), (String) fj.get("0"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+1)},{"an",1},{"state",fj.get("state")}})), (String) fj.get("1"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+2)},{"an",1},{"state",fj.get("state")}})), (String) fj.get("2"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+3)},{"an",1},{"state",fj.get("state")}})), (String) fj.get("3"));
    }
    private void pushMsg(String i) throws IOException {
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+0)},{"an","0".equals(i)?1:0},{"state",fj.get("state")}})), (String) fj.get("0"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+1)},{"an","1".equals(i)?1:0},{"state",fj.get("state")}})), (String) fj.get("1"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+2)},{"an","2".equals(i)?1:0},{"state",fj.get("state")}})), (String) fj.get("2"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+3)},{"an","3".equals(i)?1:0},{"state",fj.get("state")}})), (String) fj.get("3"));
    }
    private void pushMsg1(String i) throws IOException {
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+0)},{"an",!"0".equals(i)?1:0},{"state",fj.get("state")}})), (String) fj.get("0"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+1)},{"an",!"1".equals(i)?1:0},{"state",fj.get("state")}})), (String) fj.get("1"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+2)},{"an",!"2".equals(i)?1:0},{"state",fj.get("state")}})), (String) fj.get("2"));
        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+3)},{"an",!"3".equals(i)?1:0},{"state",fj.get("state")}})), (String) fj.get("3"));
    }
    private void getMsg(String msg) throws IOException {
        synchronized (this){
            int state = (int) fj.get("state");
            Var var = LX.toMap(msg);
            List<Var> ls = var.getList("ls");
            if (state == 2){//叫
                if (ls.size() == 1 && LX.compareTo(14,ls.get(0).getStr("num"), MathUtil.Type.EQ)){//是7
                    fj.put("state",3);//抢庄成功 待扣底
                    List pai = (List) fj.get("ls"+fj.get(userId));//当前抢庄
                    for (Object o : (List)fj.get("ls4")){
                        pai.add(o);
                    }
                    fj.put("ls4",new MyArrayList());
                    pushMsg((String) fj.get(userId));
                }
            }else if (state == 3){//扣底
                if (ls.size() == 8){
                    MyArrayList dp = (MyArrayList) fj.get("ls4");
                    MyArrayList sl = (MyArrayList) fj.get("ls"+fj.get(userId));
                    for (Var v : ls){
                        dp.add(new Object[]{(int)Double.parseDouble(v.getStr("num")),v.getStr("type")});
                        boolean ex = true;
                        for (Iterator<Object[]> it = sl.iterator();it.hasNext();){
                            Object[] o = it.next();
                            if (LX.compareTo(o[0],v.getStr("num"), MathUtil.Type.EQ)&&v.getStr("type").equals(o[1])){
                                it.remove();
                                ex = false;
                                break;
                            }
                        }
                        if (ex){
                            LX.exMsg("出现错误!");
                        }
                    }
                    pushMsg1((String) fj.get(userId));
                }
            }else if (state == 4){//反牌

            }
        }
    }
    class MyArrayList extends ArrayList<Object[]>{
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
            if ((int) a[0] == 14){
                i+=100;
            }else if (((String)a[1]).equals(fj.get("zhu"))){
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
        @Override
        public boolean add(Object[] objects) {
            boolean b = super.add(objects);
            this.sort(c);
            return b;
        }
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
            log.info("用户连接:"+userId+",当前在线人数为:" + getOnlineCount());
            try {
                if (fj.contains(userId)){
                    if ((int)fj.get("state")>0){//恢复断线
                        String i = (String) fj.get(userId);
                        sendInfo(LX.toJSONString(new Var(new Object[][]{{"xf",fj.get("ls"+i)},{"an","1"},{"state",fj.get("state")}})), userId);
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
            } catch (IOException e) {
                log.error("用户:"+userId+",网络异常!!!!!!");
            }
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("用户消息:"+userId+",报文:"+message);
        getMsg(message);
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
