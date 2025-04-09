package com.mx.edifact.dto;

public class RespuestaTimbrado {
	private String documentoTimbrado;
	private String codigoResultado;
	private String codigoDescripcion;

	public String getDocumentoTimbrado() {
		return documentoTimbrado;
	}

	public void setDocumentoTimbrado(String documentoTimbrado) {
		this.documentoTimbrado = documentoTimbrado;
	}

	public String getCodigoResultado() {
		return codigoResultado;
	}

	public void setCodigoResultado(String codigoResultado) {
		this.codigoResultado = codigoResultado;
	}

	public String getCodigoDescripcion() {
		return codigoDescripcion;
	}

	public void setCodigoDescripcion(String codigoDescripcion) {
		this.codigoDescripcion = codigoDescripcion;
	}

}
