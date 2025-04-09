package com.mx.edifact.model;

public class CfdiCancel {
	private int id;
	private String uuid;
	private String REQUEST_CFDI;
	private String CodigoEstatus;
	private String EsCancelable;
	private String Estado;
	private String EstatusCancelacion;
	private String fechaCancelado;
	private String fechaRechazo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getREQUEST_CFDI() {
		return REQUEST_CFDI;
	}

	public void setREQUEST_CFDI(String rEQUEST_CFDI) {
		REQUEST_CFDI = rEQUEST_CFDI;
	}

	public String getCodigoEstatus() {
		return CodigoEstatus;
	}

	public void setCodigoEstatus(String codigoEstatus) {
		CodigoEstatus = codigoEstatus;
	}

	public String getEsCancelable() {
		return EsCancelable;
	}

	public void setEsCancelable(String esCancelable) {
		EsCancelable = esCancelable;
	}

	public String getEstado() {
		return Estado;
	}

	public void setEstado(String estado) {
		Estado = estado;
	}

	public String getEstatusCancelacion() {
		return EstatusCancelacion;
	}

	public void setEstatusCancelacion(String estatusCancelacion) {
		EstatusCancelacion = estatusCancelacion;
	}

	public String getFechaCancelado() {
		return fechaCancelado;
	}

	public void setFechaCancelado(String fechaCancelado) {
		this.fechaCancelado = fechaCancelado;
	}

	public String getFechaRechazo() {
		return fechaRechazo;
	}

	public void setFechaRechazo(String fechaRechazo) {
		this.fechaRechazo = fechaRechazo;
	}

}
