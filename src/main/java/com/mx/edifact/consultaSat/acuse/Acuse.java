/**
 * Acuse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mx.edifact.consultaSat.acuse;

public class Acuse  implements java.io.Serializable {
    private java.lang.String codigoEstatus;

    private java.lang.String esCancelable;

    private java.lang.String estado;

    private java.lang.String estatusCancelacion;

    private java.lang.String validacionEFOS;

    public Acuse() {
    }

    public Acuse(
           java.lang.String codigoEstatus,
           java.lang.String esCancelable,
           java.lang.String estado,
           java.lang.String estatusCancelacion,
           java.lang.String validacionEFOS) {
           this.codigoEstatus = codigoEstatus;
           this.esCancelable = esCancelable;
           this.estado = estado;
           this.estatusCancelacion = estatusCancelacion;
           this.validacionEFOS = validacionEFOS;
    }


    /**
     * Gets the codigoEstatus value for this Acuse.
     * 
     * @return codigoEstatus
     */
    public java.lang.String getCodigoEstatus() {
        return codigoEstatus;
    }


    /**
     * Sets the codigoEstatus value for this Acuse.
     * 
     * @param codigoEstatus
     */
    public void setCodigoEstatus(java.lang.String codigoEstatus) {
        this.codigoEstatus = codigoEstatus;
    }


    /**
     * Gets the esCancelable value for this Acuse.
     * 
     * @return esCancelable
     */
    public java.lang.String getEsCancelable() {
        return esCancelable;
    }


    /**
     * Sets the esCancelable value for this Acuse.
     * 
     * @param esCancelable
     */
    public void setEsCancelable(java.lang.String esCancelable) {
        this.esCancelable = esCancelable;
    }


    /**
     * Gets the estado value for this Acuse.
     * 
     * @return estado
     */
    public java.lang.String getEstado() {
        return estado;
    }


    /**
     * Sets the estado value for this Acuse.
     * 
     * @param estado
     */
    public void setEstado(java.lang.String estado) {
        this.estado = estado;
    }


    /**
     * Gets the estatusCancelacion value for this Acuse.
     * 
     * @return estatusCancelacion
     */
    public java.lang.String getEstatusCancelacion() {
        return estatusCancelacion;
    }


    /**
     * Sets the estatusCancelacion value for this Acuse.
     * 
     * @param estatusCancelacion
     */
    public void setEstatusCancelacion(java.lang.String estatusCancelacion) {
        this.estatusCancelacion = estatusCancelacion;
    }


    /**
     * Gets the validacionEFOS value for this Acuse.
     * 
     * @return validacionEFOS
     */
    public java.lang.String getValidacionEFOS() {
        return validacionEFOS;
    }


    /**
     * Sets the validacionEFOS value for this Acuse.
     * 
     * @param validacionEFOS
     */
    public void setValidacionEFOS(java.lang.String validacionEFOS) {
        this.validacionEFOS = validacionEFOS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Acuse)) return false;
        Acuse other = (Acuse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codigoEstatus==null && other.getCodigoEstatus()==null) || 
             (this.codigoEstatus!=null &&
              this.codigoEstatus.equals(other.getCodigoEstatus()))) &&
            ((this.esCancelable==null && other.getEsCancelable()==null) || 
             (this.esCancelable!=null &&
              this.esCancelable.equals(other.getEsCancelable()))) &&
            ((this.estado==null && other.getEstado()==null) || 
             (this.estado!=null &&
              this.estado.equals(other.getEstado()))) &&
            ((this.estatusCancelacion==null && other.getEstatusCancelacion()==null) || 
             (this.estatusCancelacion!=null &&
              this.estatusCancelacion.equals(other.getEstatusCancelacion()))) &&
            ((this.validacionEFOS==null && other.getValidacionEFOS()==null) || 
             (this.validacionEFOS!=null &&
              this.validacionEFOS.equals(other.getValidacionEFOS())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCodigoEstatus() != null) {
            _hashCode += getCodigoEstatus().hashCode();
        }
        if (getEsCancelable() != null) {
            _hashCode += getEsCancelable().hashCode();
        }
        if (getEstado() != null) {
            _hashCode += getEstado().hashCode();
        }
        if (getEstatusCancelacion() != null) {
            _hashCode += getEstatusCancelacion().hashCode();
        }
        if (getValidacionEFOS() != null) {
            _hashCode += getValidacionEFOS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Acuse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "Acuse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codigoEstatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "CodigoEstatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("esCancelable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "EsCancelable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "Estado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("estatusCancelacion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "EstatusCancelacion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validacionEFOS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Sat.Cfdi.Negocio.ConsultaCfdi.Servicio", "ValidacionEFOS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
