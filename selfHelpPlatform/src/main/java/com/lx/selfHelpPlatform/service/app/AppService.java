package com.lx.selfHelpPlatform.service.app;

import cn.com.dhcc.www.PUB0009;
import cn.com.dhcc.www.PUB0009Soap;
import cn.com.dhcc.www.PUB0009SoapProxy;
import com.lx.entity.Var;
import com.lx.util.LX;
import com.lx.selfHelpPlatform.util.LXWebServiceUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("AppService")
public class AppService {
    private  PUB0009Soap pub0009Soap;//代理
    private boolean boo;//是否初始化
    private Logger log = LoggerFactory.getLogger(AppService.class);

//    TradeCode	交易代码 HospitalID	院区ID  CardNo	卡号 SecrityNo	卡校验码 CardType	卡类型 PatientID	患者ID(HIS登记号)
//    MedicalNo	住院号/病案号 AimFlag	业务类型（Dep:交押金,Pay：出院结算,All：全部） UserCode	用户代码 TerminalID	终端号（自助机编号）
//    StartDate	开始日期 EndDate	结束日期 ExpStr	扩展信息
    //http://localhost:8080/admin/call?method=AppService.g5001&TradeCode=5001&HospitalID=2&CardNo=0000000002&UserCode=demo&TerminalID=1234&StartDate=20190101&EndDate=20201010
    public List<Var> g5001(Var pd) throws Exception {
        LX.exMap(pd,"TradeCode,HospitalID,CardNo,UserCode,TerminalID,StartDate,EndDate");
        return call(FormatXml.objToXml(pd,"TradeCode,HospitalID,CardNo,SecrityNo,CardType,PatientID,MedicalNo,AimFlag,UserCode,TerminalID,StartDate,EndDate,ExpStr","Request"));
    }
    //TradeCode	交易代码   AdmID 住院就诊号 StartDate	开始日期 EndDate	结束日期 OutFlag(0返回收费项目明细，1返回医嘱明细)	返回数据类型 ExpStr	扩展信息
    //http://localhost:8080/admin/call?method=AppService.g5004&TradeCode=5004&AdmID=12312&OutFlag=0&StartDate=20190101&EndDate=20201010
    public List<Var> g5004(Var pd) throws Exception {
        LX.exMap(pd,"TradeCode,AdmID,OutFlag,StartDate,EndDate");
        return call(FormatXml.objToXml(pd,"TradeCode,AdmID,OutFlag,StartDate,EndDate,ExpStr","Request"));
    }


    //连接webService获取值
    public List<Var> call(String para) throws Exception {
        if (!boo){//没有加载过
            synchronized (AppService.class){
                if (!boo){
                    init();
                }
            }
        }
        log.info("请求:"+para);
        String result = pub0009Soap.HIPMessageServer(para);
        log.info("返回值:"+result);
        List<Var> ls = parseXml(result);
        LX.exObj(ls,"返回值不能为空!");
        return ls;
    }
    public void init() {
        PUB0009SoapProxy p = new PUB0009SoapProxy();
        pub0009Soap = p.getPUB0009Soap();
        log.info("初始化完成!");
        boo = true;
    }
public static void main(String[]args) throws Exception {
    System.out.println(parseXml("<Response><ResultCode>-1</ResultCode><ResultContent><![CDATA[调用MES0013服务异常错误 #6242: 向SOAP WebService发出的HTTP请求返回了意外状态: 404.]]></ResultContent></Response>"));
}
    private static List<Var> parseXml(String xml) throws Exception {
        Document doc = DocumentHelper.parseText(xml);
        //获取xml文件的根节点
        Element rootElement=doc.getRootElement();
        Element ResultCode = rootElement.element("ResultCode");
        Element ResultContent = rootElement.element("ResultContent");
        if (ResultCode == null) LX.exMsg("没有获取到ResultCode节点!");
        if (!"0".equals(ResultCode.getTextTrim()))LX.exMsg(ResultContent.getTextTrim());
        Element data = rootElement;
        Var pd = new Var();
        List<Var> list = new ArrayList<>();
        if (LX.isEmpty(data)) return list;
        Var p = new Var();
        for (Object e : data.elements()){
            p = new Var();
            for (Object c : ((Element) e).elements()){
                Element col = (Element) c;
                p.put(col.attributeValue("Key"),col.attributeValue("Value"));
            }
            list.add(p);
        }
        return list;
    }

    //将结果转为xml
    static class FormatXml{
        private String head = "";
        private String foot = "";
        public FormatXml(){};
        public FormatXml(String head,String foot){
            this.head = head;
            this.foot = foot;
        }
        public static String objToXml(Object obj, String nodeNames, String name) throws Exception {
            StringBuilder sb = new StringBuilder();
            if (obj instanceof List){
                List<Map> ls = (List<Map>)obj;
                for (Map m : ls){
                    sb.append(LX.format("<{0}>",name));
                    for (String n : nodeNames.split(",")){
                        sb.append(LX.format("<{0}>{1}</{0}>",n,m.get(n)));
                    }
                    sb.append(LX.format("</{0}>",name));
                }

            }else if (obj instanceof Map){
                sb.append(LX.format("<{0}>",name));
                Map m = (Map)obj;
                for (String n : nodeNames.split(",")){
                    sb.append(LX.format("<{0}>{1}</{0}>",n,m.get(n)));
                }
                sb.append(LX.format("</{0}>",name));
            }else{
                LX.exMsg("目前只支持Map和List类型参数!");
            }
            return sb.toString();
        }
    }
    class Para{
        private String methodName;
        private String paraXml;
        public Para(String methodName,String paraXml){this.methodName=methodName;this.paraXml = paraXml;}
        @Override
        public String toString() {
            return "{methodName=" + methodName + ", paraXml=" + paraXml + "}";
        }

    }
}
