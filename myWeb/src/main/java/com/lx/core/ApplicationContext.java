package com.lx.core;

import com.lx.core.anno.Configura;
import com.lx.core.entity.BeanDefinition;
import com.lx.core.entity.Environment;
import com.lx.core.processorImpl.BeanWarpProcessorDefault;
import com.lx.core.processorInterface.*;
import com.lx.core.util.MyException;
import com.lx.core.util.ScanPackage;
import com.lx.util.LX;
import org.util.ASMUtil;

import java.util.*;

/**
 * Created by rzy on 2020/1/10.
 */
public class ApplicationContext {
    private Environment environment = new Environment();
    private List<Processor> allProcessor = new ArrayList<>();
    private Map<String,BeanDefinition> beanDefinitions = new HashMap<>();
    private Map<String,Object> ioc = new HashMap<>();
    private Set<String> cyclicDependency = new HashSet<>();//循环依赖
    public ApplicationContext(Class<?> cls) {
        try {
            addProcessor();//添加系统处理器
            runScanBeanDefinitionProcessor(cls); //说明:查询并执行Bean定义信息的处理器
            ScanPackage.sortByOrder(allProcessor);//将处理器排序
            runLoadAllBeanDefinitionAfterProcessor();//加载所有的Bean后执行处理器
            instanceBean();//创建bean
            populate();//填充Bean
            initBean();//调用Bean的初始化方法
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    //说明:填充
    /**{ ylx } 2020/1/19 15:39 */
    private void populate() throws Exception {
        for (Object bean : ioc.values()){
            List<BeanInstancePopulateProcessor> processors = getProcessors(BeanInstancePopulateProcessor.class);
            for (BeanInstancePopulateProcessor bip : processors){//循环所有Bean处理器
                bip.process(bean,this);
            }
            List<BeanWarpProcessorDefault> warps = getProcessors(BeanWarpProcessorDefault.class);
            for (BeanWarpProcessorDefault bip : warps){//循环所有Bean包装处理器
                Object bean1 = bip.process(bean,this);
                if (bean1 != null) bean = bean1;
            }
        }
    }
    //说明:初始化
    /**{ ylx } 2020/1/19 9:40 */
    private void initBean() throws Exception {
        List<BeanInitProcessor> processors = getProcessors(BeanInitProcessor.class);
        for (String id : ioc.keySet()){
            for (BeanInitProcessor b : processors){
                b.process(this,id);
            }
        }
    }
    //说明:添加系统处理器
    /**{ ylx } 2020/1/17 15:23 */
    private void addProcessor() throws Exception {
        Set<Class<Processor>> classes = ScanPackage.scan(Processor.class,null,new String[]{"com.lx.core.processorImpl"});
        for (Class<?> c : classes){
            putBeamDefintion(new BeanDefinition("",c));
        }
    }
    //说明:通过Class添加Bean的定义信息
    /**{ ylx } 2020/1/17 15:24 */
    public void addClassPathToBeanDefinition(String...paths) throws Exception {
        for (String s : paths){
            putBeamDefintion(new BeanDefinition("",Class.forName(s)));
        }
    }
    //说明:加载所有的Bean后执行处理器
    /**{ ylx } 2020/1/17 13:34 */
    private void runLoadAllBeanDefinitionAfterProcessor() {
        List<LoadAllBeanDefinitionAfterProcessor> processors = getProcessors(LoadAllBeanDefinitionAfterProcessor.class);
        for (LoadAllBeanDefinitionAfterProcessor processor : processors){
            processor.processor(this);
        }
    }
    public <T> List<T> getProcessors(Class<T> pro){
        List ls = new ArrayList();
        for (Processor p : allProcessor){
            if (pro.isAssignableFrom(p.getClass())){
                ls.add(p);
            }
        }
        return ls;
    }
    //说明:初始化Bean
    /**{ ylx } 2020/1/16 16:27 */
    private void instanceBean() throws Exception {
        for (BeanDefinition b : beanDefinitions.values()){
            if (!Processor.class.isAssignableFrom(b.getType())) {//不是处理器
                createBean(b);
            }
        }
    }
    private Object createBean(BeanDefinition b) throws Exception {
        if (ioc.containsKey(b.getId())) return ioc.get(b.getId());//已经存在 直接返回
        if (cyclicDependency.contains(b.getId())) throw new Exception("出现循环依赖:"+b.getId());
        cyclicDependency.add(b.getId());//添加标识标识待创建
        Object bean = null;
        if (b.getMethod() == null){//通过构造初始化
            bean = b.getType().newInstance();
        }else{
            String[] paramNames = ASMUtil.getMethodParamNames(b.getMethod());
            if (paramNames.length == 0 ){
                bean = b.getMethod().invoke(b.getObj());
            }else{
                Object [] objs = new Object[paramNames.length];
                for (int i = 0;i<paramNames.length;i++){
                    BeanDefinition bs = getBeanDefinition(paramNames[i] , b.getMethod().getParameterTypes()[i]);
                    if (bs == null){
                        LX.exMsg("没有找到: '"+b.getMethod()+".."+paramNames[i]+"' 的Bean!");
                    }
                    objs[i] = createBean(bs);
                }
                bean = b.getMethod().invoke(b.getObj(),objs);
            }
        }
        cyclicDependency.remove(b.getId());//对象创建成功 将从检查中移除
        ioc.put(b.getId(),bean);
        return bean;
    }

    //说明:查询并执行Bean定义信息的处理器
    /**{ ylx } 2020/1/16 15:54 */
    private void runScanBeanDefinitionProcessor(Class<?> cls) throws Exception{
        if (!cls.isAnnotationPresent(Configura.class)) MyException.throwStr("传入类不为没有Configura注解");
        String[] scan = cls.getAnnotation(Configura.class).packageScan();
        Set<Class<ScanBeanDefinitionProcessor>> set = ScanPackage.scan(ScanBeanDefinitionProcessor.class,null,scan);
        List<ScanBeanDefinitionProcessor> ls = new ArrayList<>();
        for (Class<ScanBeanDefinitionProcessor> c : set){
            putBeamDefintion(new BeanDefinition("",c));
        }
        ScanPackage.sortByOrder(allProcessor);//将处理器排序
        List<ScanBeanDefinitionProcessor> processors = getProcessors(ScanBeanDefinitionProcessor.class);
        for (ScanBeanDefinitionProcessor scanBeanDefinitionProcessor : processors){
            scanBeanDefinitionProcessor.process(this,scan);
        }
    }
    //说明:存入bean定义信息
    /**{ ylx } 2020/1/16 14:55 */
    public void putBeamDefintions(Set<BeanDefinition> allBeandefinition) throws Exception {
        if (allBeandefinition == null) return;
        for (BeanDefinition beanDefinition : allBeandefinition){
            putBeamDefintion(beanDefinition);
        }
    }
    public void putBeamDefintion(BeanDefinition beanDefinition) throws Exception {
        if (beanDefinition.getId()==null || "".equals(beanDefinition.getId())) beanDefinition.setId(beanDefinition.getType().getSimpleName());
        if (beanDefinitions.containsKey(beanDefinition.getId())){
            if (beanDefinition.equals(beanDefinitions.get(beanDefinition.getId()))){
                return;
            }
            throw new RuntimeException("对象名重复"+beanDefinition.getId());
        }
        if (Processor.class.isAssignableFrom(beanDefinition.getType())){//处理器
            allProcessor.add(instanceProcessor(beanDefinition));
        }
        beanDefinitions.put(beanDefinition.getId(),beanDefinition);
    }
    private Processor instanceProcessor(BeanDefinition b) throws Exception {
        if (b.getMethod() == null){
            return (Processor) b.getType().newInstance();
        }
        return (Processor) b.getMethod().invoke(b.getObj());
    }

    public Environment getEnvironment() {
        return environment;
    }

    public <T>T getBean(String id){
        return (T) ioc.get(id);
    }
    public <T>T getBean(Class<T> cls){
        T bean = getBean(cls.getSimpleName());
        if (bean == null){
            for (BeanDefinition b : beanDefinitions.values()){
                if (b.getType() == cls){
                    if (bean != null) LX.exMsg("通过类型匹配时!发现多个相同类型:"+cls.getName());
                    bean = getBean(b.getId());
                }
            }
        }
        return bean;
    }
    //说明:通过id 获取类型匹配
    /**{ ylx } 2020/1/19 11:14 */
    public <T>T getBean(String id, Class<T> cls){
        T bean = getBean(id);
        if (bean == null){
            bean = getBean(cls);
        }
        return bean;
    }

    public BeanDefinition getBeanDefinition(String id){
        return beanDefinitions.get(id);
    }
    //说明:通过id 或者类型查询 Bean定义信息
    /**{ ylx } 2020/1/19 11:19 */
    private BeanDefinition getBeanDefinition(String id , Class<?> cls){
        BeanDefinition b = getBeanDefinition(id);
        if (b == null){
            b = getBeanDefinition(cls.getSimpleName());
            if (b == null){
                for (BeanDefinition bs : beanDefinitions.values()){
                    if (b.getType() == cls){
                        if (b != null) LX.exMsg("通过类型匹配时!发现多个相同类型:"+cls.getName());
                        b = bs;
                    }
                }
            }
        }
        return b;
    }
}
