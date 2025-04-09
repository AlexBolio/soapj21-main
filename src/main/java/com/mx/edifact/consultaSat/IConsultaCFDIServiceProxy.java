package com.mx.edifact.consultaSat;

public class IConsultaCFDIServiceProxy implements com.mx.edifact.consultaSat.IConsultaCFDIService {
  private String _endpoint = null;
  private String ambiente = null;
  private com.mx.edifact.consultaSat.IConsultaCFDIService iConsultaCFDIService = null;
  
  public IConsultaCFDIServiceProxy() {
    _initIConsultaCFDIServiceProxy();
  }
  
  public IConsultaCFDIServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initIConsultaCFDIServiceProxy();
  }
  
  private void _initIConsultaCFDIServiceProxy() {
    try {
      iConsultaCFDIService = (new com.mx.edifact.consultaSat.ConsultaCFDIServiceLocator(ambiente)).getBasicHttpBinding_IConsultaCFDIService();
      if (iConsultaCFDIService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iConsultaCFDIService)._setProperty("jakarta.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iConsultaCFDIService)._getProperty("jakarta.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iConsultaCFDIService != null)
      ((javax.xml.rpc.Stub)iConsultaCFDIService)._setProperty("jakarta.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.mx.edifact.consultaSat.IConsultaCFDIService getIConsultaCFDIService() {
    if (iConsultaCFDIService == null)
      _initIConsultaCFDIServiceProxy();
    return iConsultaCFDIService;
  }
  
  public com.mx.edifact.consultaSat.acuse.Acuse consulta(java.lang.String expresionImpresa, java.lang.String ambiente) throws java.rmi.RemoteException{
      ambiente = ambiente;
    if (iConsultaCFDIService == null)
      _initIConsultaCFDIServiceProxy();
    return iConsultaCFDIService.consulta(expresionImpresa, ambiente);
  }
  
  
}