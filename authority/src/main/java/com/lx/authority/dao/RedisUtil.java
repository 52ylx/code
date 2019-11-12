package com.lx.authority.dao;//说明:

import com.lx.authority.config.OS;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.MathUtil;
import com.lx.util.doc.ScanPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import redis.clients.jedis.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 创建人:游林夕/2019/4/24 14 36
 */
@Repository("redisUtil")
public class RedisUtil implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private JedisConfig jedisConfig;
    @Autowired
    private Executor executor;



    //说明:从连接池获取链接
    /**{ ylx } 2019/4/24 14:52 */
    protected Jedis getRedis() {
        Jedis jedis = jedisPool.getResource();
//        Jedis jedis = new Jedis("localhost");
        jedis.auth(jedisConfig.password);
        jedis.select(jedisConfig.database);
        LX.exObj(jedis,"不能获取到Jedis连接!");
        return jedis;
    }
    public void exec(Consumer<Jedis> func){
        Jedis jedis = getRedis();
        try{
            func.accept(jedis);
        }finally {
            if (jedis !=null) jedis.close();
        }
    }
    public <R>R exec(Function<Jedis, R> func){
        Jedis jedis = getRedis();
        try{
            return func.apply(jedis);
        }finally {
            if (jedis !=null) jedis.close();
        }
    }
    //获取set集合
    public Set<String> smembers(String key){
        return exec(redis->{
            return redis.smembers(key);
        });
    }

    //说明:存储对象
    /**{ ylx } 2019/4/24 14:52 */
    public void put(String key, Object obj) {
        exec(redis->{
            redis.set(key, LX.toJSONString(obj));
        });
    }

    //说明:存储指定有效时间的key val
    /**{ ylx } 2019/4/24 16:56 */
    public void put(String key,Object val,int time){
        //nxxx： 只能取NX或者XX，如果取NX，则只有当key不存在是才进行set，如果取XX，则只有当key已经存在时才进行set
        //expx： 只能取EX或者PX，代表数据过期时间的单位，EX代表秒，PX代表毫秒。
        //time： 过期时间，单位是expx所代表的单位。
        del(key);
        exec(redis->{
            redis.set(key, LX.toJSONString(val),"NX","EX",time);
        });
    }

    //说明:删除
    /**{ ylx } 2019/4/24 17:01 */
    public void del(String key) {
        if (LX.isEmpty(key)) return;
        exec(jedis -> {
            jedis.del(key);
        });

    }

    //说明:获取指定key存储的Map
    /**{ ylx } 2019/5/5 9:52 */
    public <T> List<T> getls(String pattern,Class<T> t) throws Exception {
        Set<String> keys = exec(jedis -> {
            return jedis.keys(pattern);
        });
        List<T> ls = new ArrayList<>();
        keys.forEach((k)->{ls.add(get(k,t));});
        return ls;
    }

    //说明:获取指定类型的对象
    /**{ ylx } 2019/4/24 17:01 */
    public <T> T get(String key,Class<T> t) {
        return exec(jedis -> {
            return LX.toObj(t,jedis.get(key));
        });
    }
    //说明:获取指定类型对象,不存在时存入,时间单位为秒
    /**{ ylx } 2019/4/25 11:18 */
    public <T> T get(String key, Class<T> t, Supplier<? extends T> other,int time) {
        return Optional.ofNullable(LX.toObj(t,getRedis().get(key))).orElseGet(()->{//缓存不存在该key
            T obj = null;
            synchronized (key){//加锁防止缓存雪崩
                obj = get(key,t);//重新重缓存获取
                if (LX.isEmpty(obj)){//如果还是不存在就去数据库查询
                    obj = other.get();
                    if (time <=0){
                        put(key,obj);//永久缓存
                    }else{
                        put(key,obj,time);//缓存指定时间
                    }
                }
            }
            return obj;
        });
    }
    //说明:获取字符串类型的值
    /**{ ylx } 2019/4/24 17:01 */
    public String get(String key) {
        return exec(jedis -> {
            return jedis.get(key);
        });
    }
    //说明:判断key是否存在
    /**{ ylx } 2019/4/25 14:59 */
    public boolean exists(String key){
        return exec(jedis -> {
            return jedis.exists(key);
        });
    }
    //说明:判断key是否存在
    /**{ ylx } 2019/4/25 14:59 */
    public boolean exists(String key,String field){
        return exec(jedis -> {
            return jedis.hexists(key,field);
        });
    }

    //说明:添加list数组
    /**{ ylx } 2019/5/5 9:45 */
    public void add(String key ,Object obj){
        exec(jedis -> {
           jedis.rpush(key, LX.toJSONString(obj));
        });
    }
    //说明:添加List
    /**{ ylx } 2019/5/5 11:02 */
    public void addAll(String key,List ls){
        List<String> list = new ArrayList<>();
        ls.forEach((obj)->{list.add(LX.toJSONString(obj));});
        exec(jedis -> {
            jedis.rpush(key, list.toArray(new String[ls.size()]));
        });
    }

    //说明:获取指定key存储的list
    /**{ ylx } 2019/5/5 9:52 */
    public <T> List<T> getlist(String key,Class<T> t) throws Exception {
        return LX.toList(t,getlist(key));
    }
    public List<String> getlist(String key){
        return exec(jedis -> {
            return jedis.lrange(key,0,-1);
        });
    }
    //说明:获取指定key存储的list
    /**{ ylx } 2019/5/5 9:52 */
    public <T> List<T> getlist(String key,int start,int end,Class<T> t) throws Exception {
        return LX.toList(t,getlist(key,start,end));
    }
    public List<String> getlist(String key,int start,int end){
        return exec(jedis -> {
            return jedis.lrange(key,start,end);
        });
    }
    //说明:获取list的长度
    /**{ ylx } 2019/10/15 13:57 */
    public long getLLen(String key){
        return exec(jedis -> {
            return jedis.llen(key);
        });
    }
    //说明:删除指定下标的值(多个值相同时,会从左到右删除)
    /**{ ylx } 2019/5/5 11:34 */
    public void del(String key,int index){
        exec(jedis -> {
            jedis.lrem(key,1,jedis.lindex(key,index));
        });
    }

    //说明:获取指定长度的自增id
    /**{ ylx } 2019/5/5 15:44 */
    public String getId(String key,int length){
        String str = exec(jedis -> {
            return LX.right(jedis.incr(key+"_incr").toString(),length,'0');
        });
        return str;
    }

    //说明:添加对象
    /**{ ylx } 2019/5/6 8:46 */
    public void save(String key,String id,Object obj){
        exec(jedis -> {
            jedis.hset(key,id, LX.toJSONString(obj));
        });
    }
    //说明:删除对象
    /**{ ylx } 2019/5/6 8:50 */
    public void del(String key,String... id){
        exec(jedis -> {
            jedis.hdel(key,id);
        });
    }
    //说明:获取所有对象
    /**{ ylx } 2019/5/6 8:52 */
    public <T> List<T> find(String key,Class<T> t){
        return exec(jedis -> {
            return LX.toList(t,jedis.hvals(key));
        });
    }
    //获取对象
    public Map<String,String> findMap(String key){
        return exec(jedis -> {
            return jedis.hgetAll(key);
        });
    }
    //说明:获取一个对象
    /**{ ylx } 2019/5/6 8:55 */
    public <T> List<T> findAll(String key,Class<T> t,String... ids){
        LX.exObj(ids,"ids不能为空!");
        return exec(jedis -> {
            return LX.toList(t,jedis.hmget(key,ids));
        });
    }
    //说明:获取一个对象
    /**{ ylx } 2019/5/6 8:55 */
    public <T> T find(String key,Class<T> t,String id){
        return exec(jedis -> {
            return LX.toObj(t,jedis.hget(key,id));
        });
    }
    /** 备份*/
     public void backup(){
         exec(jedis->{jedis.save();});
     }
    /** 设置超时时间*/
    public void expire(String key,int time){
        exec(jedis->{
            jedis.expire(key,time);
        });
    }
    //获取分页
    public List listPage(String key,String limit,String page,String find){
        String lua = "local fields = redis.call(\"HVALS\", KEYS[1]);local limit = tonumber(KEYS[2]);local page = tonumber(KEYS[3]);local result = {};local res={0};local j=0;local k=0; for i, v in ipairs(fields) do if string.len(KEYS[4])== 0 or string.find(v,KEYS[4]) then k=k+1; res[1]=k; if k>(page-1)*limit and k<=page*limit then j = j+1; result[j] = v; end; end;end;res[2]=result; return res;";
        return evalsha(lua,key,limit,page,find);
    }
    /** 每分钟限制多少次 */
    public long minuteLimit(String key , int limit, Supplier<Boolean> supp){
        return limit(key,LX.str(limit),"60",supp);
    }
    //删除匹配的key
    public void dels(String key){
        String lua = "local keys = unpack(redis.call(\"keys\",KEYS[1])); if (keys) then redis.call(\"del\",keys);end;";
        evalsha(lua,key);
    }
    public long limit(String key , String limit,String time, Supplier<Boolean> supp){
        String lua = "local key = KEYS[1];local limit = tonumber(KEYS[2]);local time = tonumber(KEYS[3]);local ttl = redis.call(\"ttl\",key);local incr = redis.call(\"incr\",key);if (ttl > 0) then if incr > limit then return ttl;end;else redis.call(\"expire\",key,KEYS[3]);end;return 0;";
        key = "system:limit:"+key;
        long t =  evalsha(lua,key,limit,time);
        if (t==0 && supp.get()){
            dels(key);//删除记录
        }
        return t;
    }
    private Var scripts = new Var();
    public <T>T evalsha(String script,String...KEYS){
        //获取脚本hash值
        String hash = "";
        if (scripts.containsKey(LX.md5(script))){
            hash = scripts.getStr(LX.md5(script));
        }else{
            hash = exec(jedis -> {
                return jedis.scriptLoad(script);
            });
            scripts.put(LX.md5(script),hash);
        }
        String finalHash = hash;
        return exec(jedis -> {
            return  (T)jedis.evalsha(finalHash, KEYS.length ,KEYS);
        });
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            sub(applicationContext);//开启监听
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Sub {
        public String value();
    }
    //发布消息
    public void pub(String channel,String msg){
        exec(jedis -> {
            jedis.publish(channel,msg);
        });
    }
    //取消订阅频道  unsubscribe String... channels  不传就是所有
    //增加订阅频道  subscribe String... channels
    public void sub(ApplicationContext applicationContext) throws IOException {
        //获取Spring 容器中所有的bean
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Var var = new Var();
        for (String bean : beanDefinitionNames){
            Class<?> cls = applicationContext.getType(bean);
            Method[] methods = cls.getDeclaredMethods();//获取所有方法
            for (Method m : methods){
                Parameter[] parameters = m.getParameters();
                //方法上有@Sub 且参数只有String 的
                if (m.isAnnotationPresent(Sub.class)&& parameters.length==1 && parameters[0].getType()==String.class){
                    Sub s = m.getAnnotation(Sub.class);
                    String channe = s.value();
                    List ls = new ArrayList();
                    if (var.containsKey(channe)){
                        ls = var.getList(channe);
                    }
                    ls.add(new Var(new Object[][]{{"cls",cls},{"method",m}}));
                    var.put(channe,ls);
                }
            }
        }
        if (var.size() == 0) return;
        String [] channels = (String[]) var.keySet().toArray(new String[var.size()]);
        JedisPubSub jedisPubSub = new JedisPubSub() {
            public void onMessage(String channel, String message) {
                try {
                    List<Var> ls = var.getList(channel);
                    for (Var v : ls){
                        executor.execute(()->{
                            try {
                                Object o = OS.getBean((Class<?>) v.getObj("cls"));
                                Method m = v.getObj("method");
                                m.invoke(o,message);
                            } catch (Exception e) {}
                        });

                    }
                }catch (Exception e){}
            }
        };
        new Thread(()->{
            bind(jedisPubSub, channels);//监听指定channels
        }).start();
    }
    private void bind(JedisPubSub jedisPubSub,String...channels){
        try {
            exec(jedis -> {
                jedis.subscribe(jedisPubSub,channels);
            });
        } catch (Exception e) {
            log.info("订阅连接出现异常!重试");
            LX.sleep(3000);
            bind(jedisPubSub, channels);
        }
    }

}
