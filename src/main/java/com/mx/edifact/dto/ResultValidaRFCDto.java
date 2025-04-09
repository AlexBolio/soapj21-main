package com.mx.edifact.dto;

public class ResultValidaRFCDto {

    private String pass;
    private String pathcert;
    private String pathkey;
    private String direccion;

    /**
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * @return the pathcert
     */
    public String getPathcert() {
        return pathcert;
    }

    /**
     * @param pathcert the pathcert to set
     */
    public void setPathcert(String pathcert) {
        this.pathcert = pathcert;
    }

    /**
     * @return the pathkey
     */
    public String getPathkey() {
        return pathkey;
    }

    /**
     * @param pathkey the pathkey to set
     */
    public void setPathkey(String pathkey) {
        this.pathkey = pathkey;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
}
