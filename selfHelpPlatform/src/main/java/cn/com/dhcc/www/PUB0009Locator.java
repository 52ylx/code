/**
 * PUB0009Locator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package cn.com.dhcc.www;

public class PUB0009Locator extends org.apache.axis.client.Service implements PUB0009 {

    public PUB0009Locator() {
    }


    public PUB0009Locator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PUB0009Locator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PUB0009Soap
    private String PUB0009Soap_address = "http://192.168.15.119/csp/hsb/DHC.Published.PUB0009.BS.PUB0009.cls";

    public String getPUB0009SoapAddress() {
        return PUB0009Soap_address;
    }

    // The WSDD service name defaults to the port name.
    private String PUB0009SoapWSDDServiceName = "PUB0009Soap";

    public String getPUB0009SoapWSDDServiceName() {
        return PUB0009SoapWSDDServiceName;
    }

    public void setPUB0009SoapWSDDServiceName(String name) {
        PUB0009SoapWSDDServiceName = name;
    }

    public PUB0009Soap getPUB0009Soap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PUB0009Soap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPUB0009Soap(endpoint);
    }

    public PUB0009Soap getPUB0009Soap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            cn.com.dhcc.www.PUB0009SoapStub _stub = new cn.com.dhcc.www.PUB0009SoapStub(portAddress, this);
            _stub.setPortName(getPUB0009SoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPUB0009SoapEndpointAddress(String address) {
        PUB0009Soap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (PUB0009Soap.class.isAssignableFrom(serviceEndpointInterface)) {
                cn.com.dhcc.www.PUB0009SoapStub _stub = new cn.com.dhcc.www.PUB0009SoapStub(new java.net.URL(PUB0009Soap_address), this);
                _stub.setPortName(getPUB0009SoapWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("PUB0009Soap".equals(inputPortName)) {
            return getPUB0009Soap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.dhcc.com.cn", "PUB0009");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.dhcc.com.cn", "PUB0009Soap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("PUB0009Soap".equals(portName)) {
            setPUB0009SoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
