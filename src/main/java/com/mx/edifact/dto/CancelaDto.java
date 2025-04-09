package com.mx.edifact.dto;

public class CancelaDto {

    private String uuid;
    private String rfcEmisor;
    private String rfcReceptor;
    private String total;
    private String motivo;
    private String folioSustitucion;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRfcEmisor() {
        return rfcEmisor;
    }

    public void setRfcEmisor(String rfcEmisor) {
        this.rfcEmisor = rfcEmisor;
    }

    public String getRfcReceptor() {
        return rfcReceptor;
    }

    public void setRfcReceptor(String rfcReceptor) {
        this.rfcReceptor = rfcReceptor;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getFolioSustitucion() {
        return folioSustitucion;
    }

    public void setFolioSustitucion(String folioSustitucion) {
        this.folioSustitucion = folioSustitucion;
    }

    
}
