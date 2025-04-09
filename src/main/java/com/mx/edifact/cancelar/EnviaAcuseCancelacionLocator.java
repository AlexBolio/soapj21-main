/**
 * EnviaAcuseCancelacionLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mx.edifact.cancelar;

public class EnviaAcuseCancelacionLocator extends org.apache.axis.client.Service implements com.mx.edifact.cancelar.EnviaAcuseCancelacion {
	private java.lang.String enviaAcuseCancelacionPort_address="";
    public EnviaAcuseCancelacionLocator(java.lang.String ambiente) {
    	if(ambiente.equals("produccion"))
        {
        	enviaAcuseCancelacionPort_address="https://www.edifactmx-pac.com/serviceCFDI/cancelaCFDI.php";
        }
        else
        {
        	enviaAcuseCancelacionPort_address="http://comprobantes-fiscales.com/service/cancelaCFDI.php";
        	//enviaAcuseCancelacionPort_address="http://edifactmex.dyndns-ip.com/service/cancelarCFDI.php";
        }
    }


    public EnviaAcuseCancelacionLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EnviaAcuseCancelacionLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }


    public java.lang.String getenviaAcuseCancelacionPortAddress() {
        return enviaAcuseCancelacionPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String enviaAcuseCancelacionPortWSDDServiceName = "enviaAcuseCancelacionPort";

    public java.lang.String getenviaAcuseCancelacionPortWSDDServiceName() {
        return enviaAcuseCancelacionPortWSDDServiceName;
    }

    public void setenviaAcuseCancelacionPortWSDDServiceName(java.lang.String name) {
        enviaAcuseCancelacionPortWSDDServiceName = name;
    }

    public com.mx.edifact.cancelar.EnviaAcuseCancelacionPortType getenviaAcuseCancelacionPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(enviaAcuseCancelacionPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getenviaAcuseCancelacionPort(endpoint);
    }

    public com.mx.edifact.cancelar.EnviaAcuseCancelacionPortType getenviaAcuseCancelacionPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
        	com.mx.edifact.cancelar.EnviaAcuseCancelacionBindingStub _stub = new com.mx.edifact.cancelar.EnviaAcuseCancelacionBindingStub(portAddress, this);
            _stub.setPortName(getenviaAcuseCancelacionPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setenviaAcuseCancelacionPortEndpointAddress(java.lang.String address) {
        enviaAcuseCancelacionPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.mx.edifact.cancelar.EnviaAcuseCancelacionPortType.class.isAssignableFrom(serviceEndpointInterface)) {
            	com.mx.edifact.cancelar.EnviaAcuseCancelacionBindingStub _stub = new com.mx.edifact.cancelar.EnviaAcuseCancelacionBindingStub(new java.net.URL(enviaAcuseCancelacionPort_address), this);
                _stub.setPortName(getenviaAcuseCancelacionPortWSDDServiceName());
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
        if ("enviaAcuseCancelacionPort".equals(inputPortName)) {
            return getenviaAcuseCancelacionPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://edifact.com.mx/xsd", "enviaAcuseCancelacion");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://edifact.com.mx/xsd", "enviaAcuseCancelacionPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("enviaAcuseCancelacionPort".equals(portName)) {
            setenviaAcuseCancelacionPortEndpointAddress(address);
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
