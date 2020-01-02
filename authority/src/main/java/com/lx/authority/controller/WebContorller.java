package com.lx.authority.controller;//说明:

import com.lx.authority.config.Authority;
import com.lx.authority.config.OS;
import com.lx.authority.dao.RedisUtil;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.util.MathUtil;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.util.*;

/**
 * 创建人:游林夕/2019/4/28 14 00
 */
@RestController
@RequestMapping("/sys")
@Authority(method = true) //验证接口
public class WebContorller {
    Logger logger = LoggerFactory.getLogger(WebContorller.class);
    @Autowired
    private RedisUtil redis;
    //说明:接口调用 token登陆令牌 method为方法名
    /**{ ylx } 2019/5/14 14:14 */
    @RequestMapping(value = "/service")
    public Object service(HttpServletRequest req) throws Exception {
        Map pd = OS.getParameterMap(req);
        logger.info("--start--"+pd);
        LX.exMap(pd,"method");
        return OS.invokeMethod(pd);
    }
    @RequestMapping("/list/{main}")
    public OS.Page list(@PathVariable("main") String main) throws Exception {
        List<HashMap> ls =  redis.find("system:"+main,HashMap.class);
        ls.sort((o1,o2)->{return o1.get("id").hashCode()-o2.get("id").hashCode();});
        return new OS.Page(ls,ls.size());
    }
    @RequestMapping("/listPage/{main}")
    public OS.Page listPage(@RequestParam(required = false) Map map, @PathVariable("main") String main){
        LX.exMap(map,"limit,page");
        List ls =  redis.listPage("system:"+main,map.get("limit").toString(),map.get("page").toString()
                ,LX.isEmpty(map.get("find"))?"":map.get("find").toString());
        return new OS.Page(LX.toList(HashMap.class,ls.get(1)),(long)ls.get(0));
    }

    @RequestMapping("/obj/{main}")
    public OS.Page obj(@RequestParam(required = false) Map map, @PathVariable("main") String main){
        LX.exMap(map,"id");
        return new OS.Page(redis.find("system:"+main,HashMap.class,map.get("id").toString()));
    }
    //说明:部分修改
    /**{ ylx } 2019/5/6 14:58 */
    @RequestMapping("/edit/{main}")
    public OS.Page edit(@RequestParam(required = false) Map map, @PathVariable("main") String main){
        if(LX.isEmpty(map.get("id"))){
            return ins(map,main);
        }
        Map old = null;
        old = redis.find("system:"+main,HashMap.class,map.get("id").toString());
        LX.exObj(old,"没有找到准备修改的对象!请刷新界面后重试!");
        old.putAll(map);
        old.put("u_time", LX.getTime());
        old.remove("token");
        redis.save("system:"+main,old.get("id").toString(),old);
        return new OS.Page();
    }
    //说明:全部修改
    /**{ ylx } 2019/5/6 14:58 */
    @RequestMapping("/ins/{main}")
    public OS.Page ins(@RequestParam(required = false) Map map, @PathVariable("main") String main){
        LX.exObj(map,"不能存储空的对象!");
        if (LX.isEmpty(map.get("id"))){//新增
            map.put("id",redis.getId("system:"+main,5));
        }else{
            if (redis.exists("system:"+main,map.get("id").toString())) LX.exMsg("该不能保存id相同的对象");
        }
        //全部修改
        map.put("u_time", LX.getTime());
        map.remove("token");
        redis.save("system:"+main,map.get("id").toString(),map);
        return new OS.Page();
    }
    //说明:删除
    /**{ ylx } 2019/5/5 15:42 */
    @RequestMapping("/del/{main}")
    public OS.Page del(@RequestParam(required = false) Map map, @PathVariable("main") String main){
        LX.exMap(map,"id");
        String[]idss = map.get("id").toString().split(",");
        Var var = redis.find("system:"+main,Var.class,idss[0]);
        Set<String> ids = new HashSet<>(Arrays.asList(idss));
        if (var.containsKey("pid")){
            List<Var> ls =  redis.find("system:"+main,Var.class);//获取所有
            ls.sort((o1,o2)->{return o1.get("id").hashCode()-o2.get("id").hashCode();});//排序
            ls.forEach((v -> {
                if (ids.contains(v.get("pid"))){
                    ids.add(v.getStr("id"));
                }
            }));
            idss = ids.toArray(new String[ids.size()]);
        }
        redis.del("system:"+main,idss);
        return new OS.Page();
    }

    @RequestMapping("/backup")
    public void backup(){
        redis.backup();
    }

   final String updateFile = "service,menu,dict,config";
    @RequestMapping("/up_version")
    public void saveFile() throws Exception {
        saveFile("up_version.txt",updateFile);
    }
    @RequestMapping("/up_user")
    public void saveUser() throws Exception {
        saveFile("up_version_user.txt","user,role");
    }
    private void saveFile(String fileName,String name) throws Exception{
        File file = new File(fileName);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
        try {
            String v = System.currentTimeMillis()+"";
            pw.println(v);
            for (String uf : name.split(",")){
                if (redis.get("system:"+uf+"_incr") != null){
                    pw.println("system:"+uf+"_incr@~@`@"+redis.get("system:"+uf+"_incr"));
                }
                List<HashMap> ls =  redis.find("system:"+uf,HashMap.class);
                if (LX.isNotEmpty(ls)){
                    for (HashMap h : ls){
                        pw.println("system:"+uf+"@~@`@"+h.get("id")+"@~@`@"+ LX.toJSONString(h));
                    }
                }
            }
            pw.flush();
        }finally {
            if(pw!=null) pw.close();
        }
    }
    @PostConstruct
    public void updateFile() throws Exception {
        try{
            File file = new File("up_version.txt");
            if (file.exists()){
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
                String ver = br.readLine();
                String len =null;
                String rv = redis.get("system:version");
                if (ver !=null && LX.compareTo(ver,rv, MathUtil.Type.GT)){
                    //删除旧的版本
                    for (String uf : updateFile.split(",")) {
                        redis.del("system:"+uf);
                        redis.del("system:"+uf+"_incr");
                    }
                    while ((len=br.readLine())!=null){
                        String [] pojo = len.split("@~@`@");
                        if (pojo.length==3){
                            redis.save(pojo[0],pojo[1],pojo[2]);
                        }else{
                            redis.put(pojo[0],pojo[1]);
                        }
                    }
                    redis.put("system:version",ver);
                }
            }
        }catch (Exception e){
            logger.error("初始化版本!",e);
        }
    }

    @RequestMapping("/getPCInfo")
    @Authority(false)
    public Object getPCInfo(){
        Map map = new HashMap();
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        map.put("Java的执行环境版本号：" , System.getProperty("java.version"));
        map.put("Java的安装路径：" , System.getProperty("java.home"));
        map.put("服务器最大内存",osmb.getTotalPhysicalMemorySize()/1024/1024+"M");
        map.put("服务器可用内存",osmb.getFreePhysicalMemorySize()/1024/1024+"M");
        map.put("内存剩余率",LX.getBigDecimal(osmb.getFreePhysicalMemorySize()*100L/osmb.getTotalPhysicalMemorySize()).setScale(2, BigDecimal.ROUND_HALF_UP));
        return map;
    }

}
