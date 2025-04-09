/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.edifact.dto;

/**
 *
 * @author German
 */
public class ResponceWs {

    private String codigo;
    private String descripcion;
    private String documentopdf;
    private String documentoxml;
    private String fechaTimbrado;
    private String noCertificado;
    private String selloCFDI;
    private String selloSAT;
    private String UUID;

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the documentopdf
     */
    public String getDocumentopdf() {
        return documentopdf;
    }

    /**
     * @param documentopdf the documentopdf to set
     */
    public void setDocumentopdf(String documentopdf) {
        this.documentopdf = documentopdf;
    }

    /**
     * @return the documentoxml
     */
    public String getDocumentoxml() {
        return documentoxml;
    }

    /**
     * @param documentoxml the documentoxml to set
     */
    public void setDocumentoxml(String documentoxml) {
        this.documentoxml = documentoxml;
    }

    /**
     * @return the fechaTimbrado
     */
    public String getFechaTimbrado() {
        return fechaTimbrado;
    }

    /**
     * @param fechaTimbrado the fechaTimbrado to set
     */
    public void setFechaTimbrado(String fechaTimbrado) {
        this.fechaTimbrado = fechaTimbrado;
    }

    /**
     * @return the noCertificado
     */
    public String getNoCertificado() {
        return noCertificado;
    }

    /**
     * @param noCertificado the noCertificado to set
     */
    public void setNoCertificado(String noCertificado) {
        this.noCertificado = noCertificado;
    }

    /**
     * @return the selloCFDI
     */
    public String getSelloCFDI() {
        return selloCFDI;
    }

    /**
     * @param selloCFDI the selloCFDI to set
     */
    public void setSelloCFDI(String selloCFDI) {
        this.selloCFDI = selloCFDI;
    }

    /**
     * @return the selloSAT
     */
    public String getSelloSAT() {
        return selloSAT;
    }

    /**
     * @param selloSAT the selloSAT to set
     */
    public void setSelloSAT(String selloSAT) {
        this.selloSAT = selloSAT;
    }

    /**
     * @return the UUID
     */
    public String getUUID() {
        return UUID;
    }

    /**
     * @param UUID the UUID to set
     */
    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

}
