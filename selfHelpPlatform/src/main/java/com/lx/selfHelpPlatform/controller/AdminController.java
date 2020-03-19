package com.lx.selfHelpPlatform.controller;

import com.lx.authority.config.Authority;
import com.lx.authority.config.OS;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.selfHelpPlatform.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by rzy on 2020/2/5.
 */
@Controller()
@RequestMapping("/admin")
@Authority
public class AdminController {

    @Autowired
    private Dao dao;

    Logger log = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping(value = "/call")
    @ResponseBody
    @Authority(false)
    public Object call(HttpServletRequest req) throws Exception {
        Var pd = OS.getParameterMap(req);
        log.info("--start--" + pd);
        LX.exMap(pd, "method");
        return OS.invoke(pd);
    }


    @RequestMapping("/page/{main}")
    @ResponseBody
    public Object page(@RequestParam(required = false) Map<Object,Object> map, @PathVariable("main") String main) throws Exception {
//        Var user = LX.toMap(OS.getUser().getCustom());
//        if(user!= null && !"1".equals(user.get("user_type"))){
//            map.put("lx_jg_id",user.get("jg_id"));
//        }
        for (Iterator<Map.Entry<Object, Object>> it = map.entrySet().iterator(); it.hasNext();){
            Map.Entry<Object, Object> entry=it.next();
            if(LX.isEmpty(entry.getValue())){
                it.remove();
            }
        }
        return dao.page(main,map);
    }
    @RequestMapping("/list/{main}")
    @ResponseBody
    public Object list(@RequestParam(required = false) Map<Object,Object> map, @PathVariable("main") String main) throws Exception {
//        Var user = LX.toMap(OS.getUser().getCustom());
//        if(user!= null && !"1".equals(user.get("user_type"))){
//            map.put("lx_jg_id",user.get("jg_id"));
//        }
        return new OS.Page(dao.selectAll(main,map));
    }
    @RequestMapping("/obj/{main}")
    @ResponseBody
    public Object obj(@RequestParam(required = false) Map map, @PathVariable("main") String main) throws Exception {
        return new OS.Page(dao.selectOne(main,map));
    }
    //说明:部分修改
    /**{ ylx } 2019/5/6 14:58 */
    @RequestMapping("/edit/{main}")
    @ResponseBody
    public Object edit(@RequestParam(required = false) Map map, @PathVariable("main") String main) throws Exception {
        map.put("update_time",LX.getTime());
        map.put("update_user", ((Map<String,String>)OS.getUser().getCustom()).get("user_id"));
        dao.autoInsertORUpdate(main,map);
        return new OS.Page();
    }
    //说明:删除
    /**{ ylx } 2019/5/5 15:42 */
    @RequestMapping("/del/{main}")
    @ResponseBody
    public Object del(@RequestParam(required = false) Map map, @PathVariable("main") String main) throws Exception {
        dao.del(main,map);
        return new OS.Page();
    }
    //说明:删除
    /**{ ylx } 2019/5/5 15:42 */
    @RequestMapping("/delAll/{main}")
    @ResponseBody
    public Object delAll(@RequestParam(required = false) Map map, @PathVariable("main") String main) throws Exception {
        LX.exMap(map,"ls");
        List<Var> ls = LX.toList(Var.class,map.get("ls"));
        if (LX.isNotEmpty(ls)){
            for(Var v :ls){
                dao.del(main,v);
            }
        }
        return new OS.Page();
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public OS.Page list(HttpServletRequest req) throws Exception {
        Map pd = OS.getParameterMap(req);
        log.info("--start--" + pd);
        LX.exMap(pd, "method");
        return (OS.Page) OS.invokeMethod(pd);
    }

}
