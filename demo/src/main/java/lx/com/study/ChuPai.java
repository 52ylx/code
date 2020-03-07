package lx.com.study;

import com.lx.entity.Var;

import java.util.List;

public class ChuPai {
    public static boolean check(Object[] cp, List<Var> ls){
        String zhu = (String) cp[5];//主
        int xs = (int) cp[6];//先手者
        int i = (int) cp[7];//当前第几手
        boolean dui = true;//对不对
        if (ls.size()>0) return true;



//                if (ls.size()>0){//有牌
//                    if (i == 0){//先手者出牌 牌型要一样 单张 对子 非主的 便三轮
//                        if (ls.size()>1){//多张牌 牌型一样
//                            String type = ls.get(0).getStr("type");//牌型
//                            for (Var v : ls){
//                                if (!type.equals(v.getStr(type))){
//                                    dui = false;
//                                    break;
//                                }
//                            }
//                        }
//                        if (!dui){//不对不往后走
//                        }else if (ls.size()%2==0){//两张 或者拖拉机
//
//                        }else if (ls.size() == 3){//3张
//
//                        }
//
//                    }
//                }
        return false;
    }

    public void thisWin(Object[] cp){
        String zhu = (String) cp[5];//主
        int xs = (int) cp[6];//先手者


    }
}
