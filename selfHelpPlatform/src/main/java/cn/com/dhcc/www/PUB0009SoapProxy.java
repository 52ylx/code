package cn.com.dhcc.www;

public class PUB0009SoapProxy implements PUB0009Soap {
  private String _endpoint = null;
  private PUB0009Soap pUB0009Soap = null;
  
  public PUB0009SoapProxy() {
    _initPUB0009SoapProxy();
  }
  
  public PUB0009SoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initPUB0009SoapProxy();
  }
  
  private void _initPUB0009SoapProxy() {
    try {
      pUB0009Soap = (new PUB0009Locator()).getPUB0009Soap();
      if (pUB0009Soap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)pUB0009Soap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)pUB0009Soap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (pUB0009Soap != null)
      ((javax.xml.rpc.Stub)pUB0009Soap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public PUB0009Soap getPUB0009Soap() {
    if (pUB0009Soap == null)
      _initPUB0009SoapProxy();
    return pUB0009Soap;
  }
  
  public String HIPMessageServer(String input1) throws java.rmi.RemoteException{
    if (pUB0009Soap == null)
      _initPUB0009SoapProxy();
    return pUB0009Soap.HIPMessageServer(input1);
  }
  
  
}