package lx.com.study;

import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.doc.Doc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by rzy on 2020/2/16.
 */
@Service("AppService")
public class AppService {
    @Autowired
    private RedisUtil redisUtil;

    private Random random = new Random();

    @Doc(name = "获取下一道题", method = "getNext", type = "", auth = "ylx", day = "2020-02-16"
    ,in = "name=名字,index=上次题目,score=分数=-1对 2错")
    public Var getNext(Var map){
        LX.exMap(map,"name,index,score");
        String key = "study:"+map.getStr("name");
        return redisUtil.exec(jedis -> {
            jedis.zincrby(key,map.getInt("score"),map.getStr("index"));
            if (jedis.zcount(key,1,1000)>10){//获取做错题数
                Set<String> test = jedis.zrevrange("test", 0, 10);
                List<String> ls = new ArrayList<String>(test);
                return new Var("index",ls.get(random.nextInt(ls.size())));
            }
            return new Var("index",-1);
        });

    }

    public static void main(String[]args){
        System.out.println(new Random().nextInt(2));
//        Jedis jedis = new Jedis("localhost");
//        jedis.auth("123");
//        Double zincrby = jedis.zincrby("test", 1, "a");
//        System.out.println(zincrby);
//        jedis.zincrby("test", 1, "b");
//        jedis.zincrby("test", 2, "c");
//        jedis.zincrby("test", 2, "d");
//        Set<String> test = jedis.zrevrange("test", 0, 2);
//        System.out.println(test);
//        System.out.println(jedis.zcount("test",1,20));
    }

}
