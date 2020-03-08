package lx.com.study;

import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.MathUtil;
import com.sun.org.apache.regexp.internal.RE;

import java.util.*;

public class ChuPai {
    //说明:算分
    /**{ ylx } 2020/3/8 17:38 */
    public static int getFen(Object[] cp) {
        int fen = 0;
        for (int i=0;i<4;i++){
            for (Object[] o : (ImService.MyArrayList) cp[i]){
                int num = (int) o[0];
               if (num == 5) fen += 5;
               else if(num == 9 || num == 12) fen+=10;
            }
        }
        return fen;
    }

    public static int getFen1(ImService.MyArrayList ls) {
        int fen = 0;
        for (Object[] o : ls){
            int num = (int) o[0];
            if (num == 5) fen += 5;
            else if(num == 9 || num == 12) fen+=10;
        }
        return fen;
    }

    static class MyCompare implements Comparator<Var> {
        public static int getPoint(Var a){
            int num1 = (int)Double.parseDouble(a.getStr("num"));
            if (num1>=7){
                if (num1==14) num1 = 7;
                else num1+=1;
            }
            return num1;
        }
        @Override//同一牌型的排序 拖拉机
        public int compare(Var o1, Var o2) {
            return getPoint(o2)-getPoint(o1);
        }
    }
    static MyCompare c = new MyCompare();
    public static boolean check(Object[] cp, List<Var> ls,String userCode){
        String zhu = (String) cp[5];//主
        int xs = (int) cp[6];//先手者
        int i = (int) cp[7];//当前第几手
        boolean b = checkPX(ls);//检查牌型是否一致
        if (b){
            ls.sort(c);//对一致的牌型排序
        }

        if (ls.size()>0){//有牌
            if (i == 0){//先手者出牌 牌型要一样 单张 对子 非主的 便三轮
                cp[9] = Integer.parseInt(userCode);//当前一手大
                cp[8] = ls.size();
                cp[11] = ls.get(0).getStr("type");//先手牌型
                if (!b){//牌型不一致
                }else if (ls.size()%2==0){//两张 或者拖拉机
                    return checkTLJ(ls);
                }else if (ls.size() == 1){//1张
                    return true;
                }else{//边三轮
                    return checkDanZhang(ls,13);
                }
            }else{//后手 张数一致
                if (ls.size() != (int)cp[8]){
                    return false;
                }
                //检查对子 牌型
                int jd = (int)cp[8]/2;//要几对
                ImService.MyArrayList sl = (ImService.MyArrayList) ImService.fj.get("ls"+userCode);//获取当前打牌着的牌
                ImService.MyArrayList xsp = (ImService.MyArrayList) cp[xs];
                int typeByLs = getTypeByLs(sl, (String) cp[11], zhu,xsp);//指定牌有几张
                int typeByLs1 = getTypeByLs(ls, (String) cp[11], zhu,xsp);//打了几张
                System.out.println("有"+typeByLs+"张,打"+typeByLs1+"张,要"+ls.size());
                if (jd>0){//有对子
                    int duiByLs = getDuiByLs(sl, (String) cp[11], zhu,xsp);//有几对
                    int duiByLs1 = getDuiByLs(ls, (String) cp[11], zhu,xsp);//打几对
                    System.out.println("有"+duiByLs+"对,打"+duiByLs1+"张,要"+jd);
                    if ((typeByLs1<ls.size() && typeByLs1<typeByLs)||(duiByLs1<jd && duiByLs1<duiByLs)){//打的比要的少 而且 打的比有的小
                        return false;
                    }
                }else{//单张
                    if (typeByLs1<ls.size() && typeByLs1<typeByLs){//打的小于有的
                        return false;
                    }
                }

                //9 当前谁大
                ImService.MyArrayList da = (ImService.MyArrayList) cp[(int)cp[9]];//获取大的牌
                if (ls.size()%2==0){//两张 或者拖拉机
                    if(b&&checkTLJ(ls)){//牌型一致 开始比较
                        if (bidaxiao((int)Double.parseDouble(ls.get(0).getStr("num")),ls.get(0).getStr("type"),(String)cp[5],cp)
                                > bidaxiao((int)da.get(0)[0],(String) da.get(0)[1],(String) cp[5],cp)){
                            cp[9]=Integer.parseInt(userCode);
                        }
                    }
                }else if (ls.size() == 1){//1张
                    if (bidaxiao((int)Double.parseDouble(ls.get(0).getStr("num")),ls.get(0).getStr("type"),(String)cp[5],cp)
                            > bidaxiao((int)da.get(0)[0],(String) da.get(0)[1],(String) cp[5],cp)){
                        cp[9]=Integer.parseInt(userCode);
                    }
                }else{//边三轮
                    if (b&&checkDanZhang(ls,(int)Double.parseDouble(ls.get(0).getStr("num")))){
                        if (bidaxiao((int)Double.parseDouble(ls.get(0).getStr("num")),ls.get(0).getStr("type"),(String)cp[5],cp)
                                > bidaxiao((int)da.get(0)[0],(String) da.get(0)[1],(String) cp[5],cp)){
                            cp[9]=Integer.parseInt(userCode);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    //说明:获取指定牌型有几张  牌序 先手牌 主
    /**{ ylx } 2020/3/8 22:14 */
    private static int getTypeByLs(Object ls, String s1, String zhu, ImService.MyArrayList xsp) {
        boolean zhuB = (zhu+",E,F").indexOf(s1)!=-1 || (xsp.size()<3 && (int)xsp.get(0)[0] == 14); //先手是不是主; 两张或者一张7也是主
        int i = 0;
        if (ls instanceof ImService.MyArrayList){
            for (Object[] o : (ImService.MyArrayList)ls){
                if (zhuB){//是主
                    if ((zhu+",E,F").indexOf((String) o[1]) !=-1 || (int)o[0]==14){ i++; }
                }else{//不是主
                    if (s1.equals((String) o[1]) && (int)o[0]!=14){ i++; };//二者牌型一致
                }
            }
        }else{//List<Var>
            for (Var o : (List<Var>)ls){
                int nun = (int)Double.parseDouble(o.getStr("num"));
                if (zhuB){//是主
                    if ((zhu+",E,F").indexOf(o.getStr("type")) !=-1 || nun == 14){ i++; }
                }else{//不是主
                    if (s1.equals(o.getStr("type")) && nun != 14){ i++; };//二者牌型一致
                }
            }
        }

        return i;
    }
    //说明:指定牌型对子有几对
    /**{ ylx } 2020/3/8 22:14 */
    private static int getDuiByLs(Object ls, String s1, String zhu, ImService.MyArrayList xsp) {
        boolean zhuB = (zhu+",E,F").indexOf(s1)!=-1  || (xsp.size()<3 && (int)xsp.get(0)[0] == 14); //先手是不是主;
        List<Object[]> arr = new ArrayList<>();
        if (ls instanceof ImService.MyArrayList){
            for (Object[] o : (ImService.MyArrayList)ls){
                if (zhuB){//是主
                    if ((zhu+",E,F").indexOf((String) o[1]) !=-1 || (int)o[0]==14){ arr.add(o); }
                }else{//不是主
                    if (s1.equals((String) o[1]) && (int)o[0]!=14){ arr.add(o); };//二者牌型一致 但不算7
                }
            }
        }else{//List<Var>
            for (Var o : (List<Var>)ls){
                int nun = (int)Double.parseDouble(o.getStr("num"));
                if (zhuB){//是主
                    if ((zhu+",E,F").indexOf(o.getStr("type")) !=-1 || nun == 14){ arr.add(new Object[]{nun,o.getStr("type")}); }
                }else{//不是主 不算7
                    if (s1.equals(o.getStr("type")) && nun != 14){  arr.add( new Object[]{nun,o.getStr("type")}); };//二者牌型一致
                }
            }
        }
        if (arr.size()<2) return 0;//小于一张 0对
        Object[] prev = null;
        int i = 0;
        for (int j=0;j<arr.size();j++){
            if(prev!=null&& arr.get(j)[0]==prev[0]&&arr.get(j)[1].equals(prev[1])){//和上一张一样
                i++;
            }else{
                prev = arr.get(j);
            }
        }
        return i;
    }


    private static int bidaxiao(int num,String type,String zhu,Object[] cp){
        int i=0;
        switch (type){
            case "E": i=700; break;
            case "F": i=800; break;
        }
        if (type.equals(zhu)){
            i += 100;
        }
        if (type.equals(cp[11])){//和先手牌型一样
            i+=20;
        }
        if (num==14){
            i+=200;
        }
        return i+num;
    }
    //说明:检查拖拉机
    /**{ ylx } 2020/3/8 11:49 */
    private static boolean checkTLJ(List<Var> ls) {
        boolean b = true;
        for (int i=0;i<ls.size()/2;i++){
            if (!LX.compareTo(ls.get(i*2).getObj("num"),ls.get(i*2+1).getObj("num"), MathUtil.Type.EQ)){//一样
                b = false;
                break;
            }
            if (i>0&&MyCompare.getPoint(ls.get(i*2))!=MyCompare.getPoint(ls.get((i-1)*2))-1){//不是和上一张相差1
                if (!(MyCompare.getPoint(ls.get(i*2))==6 && MyCompare.getPoint(ls.get((i-1)*2))==8)
                &&!(i==1&&MyCompare.getPoint(ls.get(0))==14 && MyCompare.getPoint(ls.get(ls.size()-1))==2)
                ){//也不是66 88 第一张是AA 最后是22
                    b = false;
                    break;
                }
            }
        }
        return b;
    }

    //说明:检查牌型是否一致
    /**{ ylx } 2020/3/8 11:39 */
    private static boolean checkPX(List<Var> ls) {
        boolean dui = true;
        if (ls.size()>1){//多张牌 牌型一样
            String type = ls.get(0).getStr("type");//牌型
            for (Var v : ls){
                if (!type.equals(v.getStr("type"))){
                    dui = false;
                    break;
                }
            }
        }
        if (!dui){//检查是不是4张王
            if (ls.size()==4||ls.size()==3){//4张王
                boolean b = true;
                for (Var v : ls){
                    if (!"E".equals(v.getStr("type")) && !"F".equals(v.getStr("type"))){
                        b = false;
                        break;
                    }
                }
                return b;
            }
        }
        return dui;
    }

    //说明:检查便三轮 五轮 7...
    /**{ ylx } 2020/3/8 11:19 */
    public static boolean checkDanZhang(List<Var> ls,int num){//最上一张牌
        int A = 0;
        for (Var v : ls){
            if (LX.compareTo(num,v.getObj("num"), MathUtil.Type.EQ)){
                A++;
            }
        }
        if (A==0) return false;
        if (A == 1){
            if (MyCompare.getPoint(ls.get(0))!=MyCompare.getPoint(ls.get(1))+1){//第二张不是比第一张小
                if (!(MyCompare.getPoint(ls.get(0))==8 && MyCompare.getPoint(ls.get(1))==6)) {//也不是8 66
                    return false;
                }
            }
            List<Var> lss = new ArrayList<>();
            for (int i = 1;i<ls.size();i++){
                lss.add(ls.get(i));
            }
            return checkTLJ(lss);//判断后几张是不是拖拉机

        }else{//对A开头的拖拉机加最后一张
            if (MyCompare.getPoint(ls.get(ls.size()-2))!=MyCompare.getPoint(ls.get(ls.size()-1))+1) {//不是和上一张相差1
                if (!(MyCompare.getPoint(ls.get(ls.size()-2))==8 && MyCompare.getPoint(ls.get(ls.size()-1))==6)) {//也不是88 6
                    return false;
                }
            }
            List<Var> lss = new ArrayList<>();
            for (int i = 0;i<ls.size()-1;i++){
                lss.add(ls.get(i));
            }
            return checkTLJ(lss);//判断前几张是不是拖拉机
        }
    }



    public void thisWin(Object[] cp){
        String zhu = (String) cp[5];//主
        int xs = (int) cp[6];//先手者


    }

    public static void main(String[]args){
        ImService.cp[5]="B";
        ImService.cp[6]=0;
        ImService.cp[7]=0;
        ImService.cp[9]=0;
        ImService.MyArrayList ls = new ImService.MyArrayList();
        ls.add(new Object[]{14,"A"});
        ls.add(new Object[]{14,"A"});
        ls.add(new Object[]{10,"A"});
        ls.add(new Object[]{10,"A"});
        ls.add(new Object[]{15,"E"});
        ls.add(new Object[]{15,"E"});
        ls.add(new Object[]{7,"B"});
        ImService.fj.put("ls1",ls);
        add(ImService.cp,LX.toList(Var.class,"[" +
//                        "{'num':2,'type':'A'},"+
//                "{'num':2,'type':'A'},{'num':2,'type':'A'},"+
                "{'num':14,'type':'A'},{'num':14,'type':'A'}" +
                "]"),"0");
        ImService.cp[7]=1;
        add(ImService.cp,LX.toList(Var.class,"[" +
//                        "{'num':2,'type':'A'},"+
//                "{'num':14,'type':'B'},{'num':14,'type':'A'},"+
                "{'num':10,'type':'A'},{'num':10,'type':'A'}" +
                "]"),"1");

        System.out.println(ImService.cp[9]);
    }
    private static void add(Object[] cp, List<Var> ls, String userCode) {
        if (ChuPai.check(cp, ls, userCode)) {//出牌正确
            ImService.MyArrayList pa = new ImService.MyArrayList();
            for (Var v : ls) {
                pa.add(new Object[]{(int) Double.parseDouble(v.getStr("num")), v.getStr("type")});
            }
            cp[Integer.parseInt(userCode)] = pa;
        }else {
            System.out.println("出不对");
        }
    }
}
