package com.mx.edifact.cancelar;

public class EnviaAcuseCancelacionPortTypeProxy implements com.mx.edifact.cancelar.EnviaAcuseCancelacionPortType {
  private String _endpoint = null;
  private String _ambiente = null;
  private com.mx.edifact.cancelar.EnviaAcuseCancelacionPortType enviaAcuseCancelacionPortType = null;
  
  public EnviaAcuseCancelacionPortTypeProxy() {
    _initEnviaAcuseCancelacionPortTypeProxy();
  }
  
  public EnviaAcuseCancelacionPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initEnviaAcuseCancelacionPortTypeProxy();
  }
  
  private void _initEnviaAcuseCancelacionPortTypeProxy() {
    try {
      enviaAcuseCancelacionPortType = (new com.mx.edifact.cancelar.EnviaAcuseCancelacionLocator(_ambiente)).getenviaAcuseCancelacionPort();
      if (enviaAcuseCancelacionPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)enviaAcuseCancelacionPortType)._setProperty("jakarta.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)enviaAcuseCancelacionPortType)._getProperty("jakarta.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (enviaAcuseCancelacionPortType != null)
      ((javax.xml.rpc.Stub)enviaAcuseCancelacionPortType)._setProperty("jakarta.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.mx.edifact.cancelar.EnviaAcuseCancelacionPortType getEnviaAcuseCancelacionPortType() {
    if (enviaAcuseCancelacionPortType == null)
      _initEnviaAcuseCancelacionPortTypeProxy();
    return enviaAcuseCancelacionPortType;
  }
  
  public java.lang.String enviaAcuseCancelacion(java.lang.String xmlFile, java.lang.String ambiente) throws java.rmi.RemoteException{
	  _ambiente = ambiente;
    if (enviaAcuseCancelacionPortType == null)
      _initEnviaAcuseCancelacionPortTypeProxy();
    return enviaAcuseCancelacionPortType.enviaAcuseCancelacion(xmlFile, ambiente);
  }
  
  public java.lang.String aceptarRechazarCancelacion(java.lang.String xmlFile) throws java.rmi.RemoteException{
    if (enviaAcuseCancelacionPortType == null)
      _initEnviaAcuseCancelacionPortTypeProxy();
    return enviaAcuseCancelacionPortType.aceptarRechazarCancelacion(xmlFile);
  }
  
  public java.lang.String obtenerPeticionesPendientes(java.lang.String rfcReceptor) throws java.rmi.RemoteException{
    if (enviaAcuseCancelacionPortType == null)
      _initEnviaAcuseCancelacionPortTypeProxy();
    return enviaAcuseCancelacionPortType.obtenerPeticionesPendientes(rfcReceptor);
  }
  
  public java.lang.String obtenerRelacionados(java.lang.String xmlFile) throws java.rmi.RemoteException{
    if (enviaAcuseCancelacionPortType == null)
      _initEnviaAcuseCancelacionPortTypeProxy();
    return enviaAcuseCancelacionPortType.obtenerRelacionados(xmlFile);
  }
  
  
}