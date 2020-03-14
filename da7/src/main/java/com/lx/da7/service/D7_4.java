package com.lx.da7.service;

//说明:4人打7

import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**{ ylx } 2020/3/12 9:25 */
public class D7_4 {

    //说明:算分
    /**{ ylx } 2020/3/8 17:38 */
    public static int getFen(Object[] cp) {
        int fen = 0;
        for (int i=0;i<(int)cp[28];i++){//几人间
            fen+=getFen1((Integer[]) cp[10+i]);
        }
        return fen;
    }
    public static int getFen1(Integer[] ls) {
        int fen = 0;
        for (Integer num : ls){
            if (num%13 == 3) fen += 5;//5
            else if(num%13 == 8 || num%13 == 11) fen+=10;//10 K
        }
        return fen;
    }

}
