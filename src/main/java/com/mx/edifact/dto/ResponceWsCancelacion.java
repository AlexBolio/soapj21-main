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
public class ResponceWsCancelacion {

    private String codigo;
    private String descripcion;
    private String cancelable;
    private String estadoPeticion;
    private String estatus;
    private String uuid;

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
     * @return the cancelable
     */
    public String getCancelable() {
        return cancelable;
    }

    /**
     * @param cancelable the cancelable to set
     */
    public void setCancelable(String cancelable) {
        this.cancelable = cancelable;
    }

    /**
     * @return the estadoPeticion
     */
    public String getEstadoPeticion() {
        return estadoPeticion;
    }

    /**
     * @param estadoPeticion the estadoPeticion to set
     */
    public void setEstadoPeticion(String estadoPeticion) {
        this.estadoPeticion = estadoPeticion;
    }

    /**
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
