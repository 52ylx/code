package com.lx.selfHelpPlatform.util;//说明:

import com.lx.util.LX;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建人:游林夕/2019/3/21 16 13
 */
public class LXWebServiceUtil {
    private Call call;
    public LXWebServiceUtil(Builder builder) throws Exception {
        Service service = new Service();
        try {
            call=(Call) service.createCall();
            call.setTargetEndpointAddress(builder.wsdlUrl); //设置请求路径
            call.setOperationName(new QName(builder.nameSpase, builder.method)); //设置命名空间和需要调用的方法名
            if (LX.isNotEmpty(builder.soapActionUri)) {//设置SOAPAction地址
                call.setSOAPActionURI(builder.soapActionUri);
            }
            for (Builder.ParaType q : builder.params){//设置参数
                call.addParameter(q.name, q.type, ParameterMode.IN);
            }
            call.setTimeout(builder.timeOut);		//设置请求超时
            call.setReturnType(builder.resultType);//设置返回类型
        } catch (Exception e) {
            LX.exMsg("创建SOAP连接失败!");
        }

    }

    public String call(Object... objects) throws RemoteException {
        return (String) call.invoke(objects);
    }
    public static class Builder {
        private String wsdlUrl,soapActionUri,nameSpase,method;
        private String [] param;//参数数组默认使用 XMLType.SOAP_STRING
        private List<ParaType> params;//参数
        private QName resultType = XMLType.SOAP_STRING;//返回类型
        private int timeOut = 10000;
        public Builder(String wsdlUrl,String nameSpase,String method){
            this.wsdlUrl = wsdlUrl;
            this.nameSpase = nameSpase;
            this.method = method;
            params = new ArrayList<ParaType>();
        }
        //方法的参数及类型
        class ParaType{
            QName name;
            QName type;
            public ParaType(QName name,QName type){
                this.name = name;
                this.type = type;
            }
        }



        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String[] getParam() {
            return param;
        }

        public void setParam(String nameSpase ,String[] param) {
            for (String s:param) {
                params.add(new ParaType(new QName(nameSpase,s),XMLType.SOAP_STRING));
            }
        }

        public String getWsdlUrl() {
            return wsdlUrl;
        }

        public void setWsdlUrl(String wsdlUrl) {
            this.wsdlUrl = wsdlUrl;
        }

        public String getSoapActionUri() {
            return soapActionUri;
        }

        public void setSoapActionUri(String soapActionUri) {
            this.soapActionUri = soapActionUri;
        }

        public String getNameSpase() {
            return nameSpase;
        }

        public void setNameSpase(String nameSpase) {
            this.nameSpase = nameSpase;
        }

        public List<ParaType> getParams() {
            return params;
        }

        public void setParams(List<ParaType> params) {
            this.params = params;
        }

        public QName getResultType() {
            return resultType;
        }

        public void setResultType(QName resultType) {
            this.resultType = resultType;
        }
    }
}
