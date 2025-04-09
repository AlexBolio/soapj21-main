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
public class AddendaEmisor {

    private DomicilioEmisor domicilioEmisor;
    private DomicilioReceptor domicilioReceptor;
    private String observaciones;
    private String ordenCompra;

    /**
     * @return the domicilioEmisor
     */
    public DomicilioEmisor getDomicilioEmisor() {
        return domicilioEmisor;
    }

    /**
     * @param domicilioEmisor the domicilioEmisor to set
     */
    public void setDomicilioEmisor(DomicilioEmisor domicilioEmisor) {
        this.domicilioEmisor = domicilioEmisor;
    }

    /**
     * @return the domicilioReceptor
     */
    public DomicilioReceptor getDomicilioReceptor() {
        return domicilioReceptor;
    }

    /**
     * @param domicilioReceptor the domicilioReceptor to set
     */
    public void setDomicilioReceptor(DomicilioReceptor domicilioReceptor) {
        this.domicilioReceptor = domicilioReceptor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(String ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

}
