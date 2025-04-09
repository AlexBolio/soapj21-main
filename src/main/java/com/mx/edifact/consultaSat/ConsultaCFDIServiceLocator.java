/**
 * ConsultaCFDIServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mx.edifact.consultaSat;

public class ConsultaCFDIServiceLocator extends org.apache.axis.client.Service implements com.mx.edifact.consultaSat.ConsultaCFDIService {
private java.lang.String BasicHttpBinding_IConsultaCFDIService_address = "";
    public ConsultaCFDIServiceLocator(java.lang.String ambiente) {
        
        if(ambiente.equals("produccion")){
        	BasicHttpBinding_IConsultaCFDIService_address="https://consultaqr.facturaelectronica.sat.gob.mx/ConsultaCFDIService.svc";
        }else{
        	BasicHttpBinding_IConsultaCFDIService_address="https://pruebacfdiconsultaqr.cloudapp.net/ConsultaCFDIService.svc";
        }
    }


    public ConsultaCFDIServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ConsultaCFDIServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BasicHttpBinding_IConsultaCFDIService
    

    public java.lang.String getBasicHttpBinding_IConsultaCFDIServiceAddress() {
        return BasicHttpBinding_IConsultaCFDIService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BasicHttpBinding_IConsultaCFDIServiceWSDDServiceName = "BasicHttpBinding_IConsultaCFDIService";

    public java.lang.String getBasicHttpBinding_IConsultaCFDIServiceWSDDServiceName() {
        return BasicHttpBinding_IConsultaCFDIServiceWSDDServiceName;
    }

    public void setBasicHttpBinding_IConsultaCFDIServiceWSDDServiceName(java.lang.String name) {
        BasicHttpBinding_IConsultaCFDIServiceWSDDServiceName = name;
    }

    public com.mx.edifact.consultaSat.IConsultaCFDIService getBasicHttpBinding_IConsultaCFDIService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BasicHttpBinding_IConsultaCFDIService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBasicHttpBinding_IConsultaCFDIService(endpoint);
    }

    public com.mx.edifact.consultaSat.IConsultaCFDIService getBasicHttpBinding_IConsultaCFDIService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.mx.edifact.consultaSat.BasicHttpBinding_IConsultaCFDIServiceStub _stub = new com.mx.edifact.consultaSat.BasicHttpBinding_IConsultaCFDIServiceStub(portAddress, this);
            _stub.setPortName(getBasicHttpBinding_IConsultaCFDIServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBasicHttpBinding_IConsultaCFDIServiceEndpointAddress(java.lang.String address) {
        BasicHttpBinding_IConsultaCFDIService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.mx.edifact.consultaSat.IConsultaCFDIService.class.isAssignableFrom(serviceEndpointInterface)) {
                com.mx.edifact.consultaSat.BasicHttpBinding_IConsultaCFDIServiceStub _stub = new com.mx.edifact.consultaSat.BasicHttpBinding_IConsultaCFDIServiceStub(new java.net.URL(BasicHttpBinding_IConsultaCFDIService_address), this);
                _stub.setPortName(getBasicHttpBinding_IConsultaCFDIServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
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
        java.lang.String inputPortName = portName.getLocalPart();
        if ("BasicHttpBinding_IConsultaCFDIService".equals(inputPortName)) {
            return getBasicHttpBinding_IConsultaCFDIService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "ConsultaCFDIService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BasicHttpBinding_IConsultaCFDIService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("BasicHttpBinding_IConsultaCFDIService".equals(portName)) {
            setBasicHttpBinding_IConsultaCFDIServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
