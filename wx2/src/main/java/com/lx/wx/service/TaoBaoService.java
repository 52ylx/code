package com.lx.wx.service;//说明:

import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.LogUtil;
import com.lx.util.MathUtil;
import com.lx.wx.controller.AppController;
import com.lx.wx.entity.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.NTbkOrder;
import com.taobao.api.internal.util.StringUtils;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 创建人:游林夕/2019/6/28 18 53
 */
@Service
public class TaoBaoService {
    @Autowired
    private RedisUtil redisUtil;
    private Logger log = LoggerFactory.getLogger(TaoBaoService.class);
    private TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "26309418", "5aaa2a578663815a5faab64858937f52");
    @Autowired
    private MyWxBot myWxBot;

    private LogUtil lx_log = new LogUtil("order");

    @PostConstruct
    public void getNewOrder() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String time ="";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long t1 = new Date().getTime();
                    long bc = 1000L*60;
                    while (true){
                        Thread.sleep(1000);
                        if (t1+bc*2>new Date().getTime()){//大于当前时间
                            Thread.sleep(bc);
                            continue;
                        }
                        time=sdf.format(new Date(t1+=bc));
                        redisUtil.put("app:tb:time",time);
                        try{
                            List<TbkOrderDetailsGetResponse.PublisherOrderDto> ls = queryNew(time,sdf.format(new Date(t1+bc-1000L)),12L);
                            if (LX.isNotEmpty(ls)){
                                for (TbkOrderDetailsGetResponse.PublisherOrderDto it : ls){
                                    try{
                                        if (12==it.getTkStatus()){//已付款
                                            Show show = redisUtil.get("app:numiid_pid:" + it.getItemId() + "_" + it.getAdzoneId(), Show.class);
                                            if (show != null){
                                                lx_log.info("付款:"+show.getName()+","+it.getTradeId()+","+show.getTitle());
                                                BigDecimal b = LX.getBigDecimal(it.getPubSharePreFee()).multiply(new BigDecimal(0.8)).setScale(2, RoundingMode.CEILING);
                                                if (b.compareTo(show.getFx())<0){//实际金额小于订单金额
                                                    show.setFx(b);
                                                }
                                                show.setStatus(Show.Status.付款);
                                                redisUtil.put("app:tb:ls:order:"+it.getTradeId(),show.getName(),40*24*3600);//将订单加入
                                                redisUtil.save("app:tb:ls:"+show.getName(),it.getTradeId(),show);//将用户订单存入
                                                try {
                                                    myWxBot.send(show.getName(),show.getTitle()+"\n付㝟成功!"+show.getFx());
                                                    Var v = redisUtil.get("app:user:nick:"+show.getName(),Var.class);
                                                    if (v.containsKey("bing")){//推荐码是自己 或者有推荐码
                                                        myWxBot.send(v.getStr("bing"),v.getStr("nick")+";付㝟成功!"
                                                                +(show.getFx().multiply(new BigDecimal(0.1)).setScale(2,BigDecimal.ROUND_FLOOR)));
                                                    }
                                                }catch (Exception e){
                                                    log.error("推送消息失败!");
                                                }
                                            }
                                        }
                                    }catch (Exception e){
                                        log.error("tb订单",e);
                                    }
                                }
                            }
                            ls = queryNew(time,sdf.format(new Date(t1+bc-1000L)),3L);
                            if (LX.isNotEmpty(ls)){
                                for (TbkOrderDetailsGetResponse.PublisherOrderDto it : ls){
                                    try{
                                        if(3==it.getTkStatus()){//已结算
                                            String name = redisUtil.get("app:tb:ls:order:" + it.getTradeId());
                                            if (name != null){
                                                Show s = redisUtil.find("app:tb:ls:"+name,Show.class,it.getTradeId());
                                                s.setStatus(Show.Status.结算);
                                                redisUtil.save("app:tb:ls:"+name,it.getTradeId(),LX.toJSONString(s));//加入到结算中
                                                try {
                                                    myWxBot.send(name,s.getTitle()+"\n结算成功:"+s.getFx());
                                                    Var v = redisUtil.get("app:user:nick:"+name,Var.class);
                                                    if (v.containsKey("bing")){//推荐码是自己 或者有推荐码
                                                        Show s1 = new Show();
                                                        s1.setAdd_time(LX.getTime());
                                                        s1.setTitle("推荐:"+v.getStr("nick"));
                                                        s1.setFx(s.getFx().multiply(new BigDecimal(0.1)).setScale(2,BigDecimal.ROUND_FLOOR));
                                                        s1.setStatus(Show.Status.结算);
                                                        redisUtil.save("app:tb:ls:"+v.getStr("bing"),it.getTradeId(),s1);//加入到结算中
                                                        myWxBot.send(v.getStr("bing"),v.getStr("nick")+";结算成功!可提现:"
                                                                +(s1.getFx()));
                                                    }
                                                }catch (Exception e){
                                                    log.error("推送消息失败!");
                                                }
                                            }
                                        }
                                    }catch (Exception e){
                                        log.error("tb订单",e);
                                    }
                                }
                            }
                        }catch (Exception e){
                            Thread.sleep(1000L*60*5);
                            log.error("tb",e);
                        }
                    }
                }catch (Exception e){
                    log.error("tb",e);
                }
            }
        }).start();
    }
    //12-付款，13-关闭，14-确认收货，3-结算成功
    public List<TbkOrderDetailsGetResponse.PublisherOrderDto> queryNew(String time, String end, long type) throws ApiException {
        TbkOrderDetailsGetRequest req = new TbkOrderDetailsGetRequest();
        //查询时间类型，1：按照订单淘客创建时间查询，2:按照订单淘客付款时间查询，3:按照订单淘客结算时间查询
        req.setQueryType(type==12?2L:3L);
        req.setPageSize(100L);
        req.setTkStatus(type);
        req.setStartTime(time);
        req.setEndTime(end);
        req.setPageNo(1L);
        req.setOrderScene(1L);
        TbkOrderDetailsGetResponse rsp = client.execute(req);
        if (rsp == null || rsp.getData() == null) return null;
        return rsp.getData().getResults();
    }



    /**{ ylx } 2019/5/19 15:44 */
    public TW findFL(String in_title, String name) throws Exception {
        if (!in_title.matches(patten)) {
            return null;
        }
        long numiid = getID(in_title);//获取商品id
        if ("0".equals(redisUtil.get("app:numiid_to_TKL:"+numiid))){
            log.info("没有优惠:"+numiid);
            return null;
        }
        String numiid_name = redisUtil.get("app:numiid_name:"+numiid+"_"+name);
        if (LX.isNotEmpty(numiid_name)){//如果该用户已经查询过 直接返回
            log.info("已经查询过:"+numiid+"_"+name);
            return LX.toObj(TW.class,numiid_name);
        }
        Set<String> smembers = redisUtil.smembers("taobao:pid");
        long adzoneId = 0L;//推广位
        for (String s : smembers){
            if (!redisUtil.exists("app:numiid_pid:"+numiid+"_"+s)){//不存在
                adzoneId = Long.parseLong(s);
                break;
            }
        }
        log.info("获取到pid:"+adzoneId);
        try {
            TbkItemInfoGetResponse.NTbkItem item = getProduct(numiid + "");
            in_title = item.getTitle();
            String city = item.getProvcity();
            city = city.indexOf(" ") != -1 ? city.substring(city.lastIndexOf(" ")) : city;
            TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
            req.setQ(in_title);
            req.setItemloc(city);
            req.setAdzoneId(adzoneId);
            req.setPlatform(1L);
            req.setPageSize(500L);
            req.setPageNo(1L);
            long pageNo = 1L;
            long res = 0L;
            do {
                TbkDgMaterialOptionalResponse rsp = client.execute(req);
                if (pageNo > 10) return null;//搜索不到
                res = rsp.getTotalResults() / 100L + 1;//每页100条
                req.setPageNo(pageNo += 1);//循环查询
                List<TbkDgMaterialOptionalResponse.MapData> ls = rsp.getResultList();
                if (LX.isNotEmpty(ls)) {
                    for (TbkDgMaterialOptionalResponse.MapData nt : ls) {
                        if (nt.getNumIid() == numiid) {
                            String url = nt.getCouponShareUrl();
                            if (LX.isEmpty(url)) url = nt.getUrl();
                            if (url.startsWith("//")) {
                                url = "https:" + url;
                            }
                            url = url + "&relationId=relationId";//渠道
                            BigDecimal to = LX.getBigDecimal(nt.getZkFinalPrice());
                            if (LX.isNotEmpty(nt.getCouponInfo())) {
                                String s = nt.getCouponInfo().substring(nt.getCouponInfo().indexOf("减") + 1, nt.getCouponInfo().length() - 1);
                                to = to.subtract(LX.getBigDecimal(s));
                            }
                            Show s = new Show(name,LX.getTime(),in_title,to, LX.getBigDecimal(nt.getCommissionRate()).divide(LX.getBigDecimal(10)));
//                            Order o = new Order(name, in_title, to, LX.getBigDecimal(nt.getCommissionRate()).divide(LX.getBigDecimal(10)));
                            String out = "总:" + nt.getZkFinalPrice() + ",返:" + s.getFx();
                            if (LX.isNotEmpty(nt.getCouponInfo())) {
                                out += "\n" + nt.getCouponInfo();
                            }
                            String imgUrl = nt.getSmallImages() == null ? nt.getPictUrl() : nt.getSmallImages().get(0);
                            String uuid = LX.uuid32(5);
                            int time = 2 * 24 * 60 * 60;
                            redisUtil.put("app:gw:" + uuid, LX.toMap("{t='{0}',tkl='{1}'}"
                                    ,out.replace("\n","</br>"), toTKL(url, nt.getPictUrl(), nt.getTitle())), time);
                            TW tw = new TW(out, in_title, imgUrl, "http://www.52ylx.cn/h/" + uuid);
                            redisUtil.put("app:numiid_name:" + numiid + "_" + name, tw, time);//商品+用户
                            redisUtil.put("app:numiid_pid:" + numiid + "_" + adzoneId, s, time * 10);//商品+pid
                            return tw;
                        }
                    }
                }
            } while (res >= pageNo);
        }catch (Exception e){
            log.error("查询报错:",e);
        }
        redisUtil.put("app:numiid_to_TKL:"+numiid,"0");
        return null;
    }
    //说明:优惠券链接转淘口令
    /**{ ylx } 2019/5/17 23:07 */
    public String toTKL(String url,String imgUrl,String title) throws ApiException {
        if (url.startsWith("//s")){
            url="https:"+url;
        }
        TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
        req.setText(title);
        req.setLogo(imgUrl);
        req.setUrl(url);
        TbkTpwdCreateResponse rsp = client.execute(req);
        if (LX.isEmpty(rsp))return "";
        return rsp.getData().getModel();
    }
    private static String gettkl(String str){
        String[] ls = {"￥","₤","\\$","€","¢","₳","₴"};
        for (String code : ls){
            Pattern pattern = Pattern.compile(code+"(.*?)"+code);
            Matcher m = pattern.matcher(str);
            if(m.find()){
                return m.group(1);
            }
        }
        return LX.exMsg("没有查询到");
    }
    //说明:通过喵有券获取商品id https://open.21ds.cn/index/index/openapi/id/5.shtml?ptype=1
    /**{ ylx } 2019/6/6 18:26 */
    public long getID(String title) throws Exception {
        String tkl = gettkl(title);
        String id = "";
        id = redisUtil.get("taobao:tkl:"+tkl);
        if (LX.isNotEmpty(id)){
            return Long.parseLong(id);
        }
        try{
            String url = LX.doGet("https://api.open.21ds.cn/apiv2/tpwdtoid?apkey=4541e955-5d12-f3fe-3856-d73af33d8a2d&tpwd="+ URLEncoder.encode(tkl, "utf-8"));
            id = LX.toMap(url).getStr("data");
            LX.exObj(id,"");
        }catch (Exception e){
            String data = LX.doGet("https://api.taokouling.com/tkl/tkljm?apikey=ecbQiGksUk&tkl="+ URLEncoder.encode(tkl, "utf-8"));
            String url = LX.toMap(data).getStr("url");
            Pattern pattern = Pattern.compile("https://a.m.taobao.com/i(.*?)\\.htm?");
            Matcher m = pattern.matcher(url);
            if(m.find()){
                id =  m.group(1);
            }
        }
        if (LX.isNotEmpty(id)){
            redisUtil.put("taobao:tkl:"+tkl,id,3600*24*30);
        }

        return Long.parseLong(id);

    }

    public TbkItemInfoGetResponse.NTbkItem getProduct(String numid) throws ApiException {
        TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
        req.setNumIids(numid);
        TbkItemInfoGetResponse rsp = client.execute(req);
        LX.exObj(rsp,"没有获取到商品信息!");
        return rsp.getResults().get(0);
    }
    String patten = ".*(￥|₤|\\$|€|¢|₳|₴).*";
    public GZH wx_in(String in_title, String name,String bName) throws Exception {
        switch (in_title.trim()){
            case "查询":
                return GZH.newInstance("http://www.52ylx.cn/s/" +name.substring(4));
//            case "验证码":
//                return GZH.newInstance(name.substring(4));
//            case "教学":
//                return GZH.newInstance(System.getProperty("user.dir")+"\\wx\\"+"jiaocheng.jpg");
//            case "提现":
//                return getTX(name,bName);
        }
        if (in_title.matches(patten)) {//符合购物
            TW tw = findFL(in_title, name);
            if(tw == null){
                return GZH.newInstance("暂未找到优惠信息");
            }
            return tw;
        }
        return null;
    }

    public GZH getTX(String name,String bName){
        BigDecimal fx = getTX(name);
        if (fx.compareTo(new BigDecimal(0))==0){
            return GZH.newInstance("尊敬的用户:"+bName+".\n");
        }
        return GZH.newInstance("尊敬的用户:"+bName+".\n请稍后!由于是人工服务,每天24点前完成!全部:"+fx.setScale(2,RoundingMode.CEILING)+"元");
    }
    public BigDecimal getTX(String name){
        Map<String,String> shows = redisUtil.findMap("app:tb:ls:" + name);
        BigDecimal fx = new BigDecimal(0);
        String tx_time = LX.getTime();
        for (Map.Entry<String,String> e : shows.entrySet()){
            Show o = LX.toObj(Show.class,e.getValue());
            if (o.getStatus() == Show.Status.结算){
                fx=fx.add(o.getFx());
                o.setStatus(Show.Status.提现);
                o.setTx_time(tx_time);
                redisUtil.save("app:tb:ls:"+name,e.getKey(),LX.toJSONString(o));
            }
        }
        Var v = redisUtil.get("app:user:nick:"+name, Var.class);
        v.put("fx",LX.getBigDecimal(v.get("fx")).add(fx));//fx
        redisUtil.put("app:user:nick:"+name,LX.toJSONString(v));//将昵称存入
        return fx;
    }
}
