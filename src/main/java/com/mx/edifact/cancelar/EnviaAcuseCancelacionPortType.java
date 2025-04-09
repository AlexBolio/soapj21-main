/**
 * EnviaAcuseCancelacionPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mx.edifact.cancelar;

public interface EnviaAcuseCancelacionPortType extends java.rmi.Remote {
    public java.lang.String enviaAcuseCancelacion(java.lang.String xmlFile, java.lang.String ambiente) throws java.rmi.RemoteException;
    public java.lang.String aceptarRechazarCancelacion(java.lang.String xmlFile) throws java.rmi.RemoteException;
    public java.lang.String obtenerPeticionesPendientes(java.lang.String rfcReceptor) throws java.rmi.RemoteException;
    public java.lang.String obtenerRelacionados(java.lang.String xmlFile) throws java.rmi.RemoteException;
}
