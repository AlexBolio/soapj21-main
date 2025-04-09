/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.edifact.utils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.mx.edifact.dto.AddendaEmisor;

import mx.gob.sat.cfd4_0.CTipoDeComprobante;
import mx.gob.sat.cfd4_0.CTipoFactor;
import mx.gob.sat.cfd4_0.Comprobante;
import mx.gob.sat.cfd4_0.Comprobante.Emisor;
import mx.gob.sat.cfd4_0.Comprobante.Receptor;

/**
 *
 * @author Germán Melchor
 */
public class CreaPDF40 extends JPDF {

    private float fin = 0;
    private DecimalFormat formatter = new DecimalFormat("###,##0.00");
    private float Wheaderimpuesto[] = {20, 30, 30, 30, 30, 30};
    private Comprobante comprobante;
    private String cadenaOriginalTFD;
    private Document documentXml;
    private AddendaEmisor addendaEmisor;

    public CreaPDF40(String orientation, String format, String unit) {
        super(orientation, format, unit);
    }

    public void footer() throws DocumentException {
        fin = 203;
        this.rect(10, fin, 155, fin + 7);
        this.rect(155, fin, 205, fin + 18);
//			this.rect(185, fin, 205, fin + 15);
        this.setXY(0, fin);
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
        this.setFont("", "", 7);
//			this.setTextColor(blue);
        String[] temp = comprobante.getTotal().toString().split("\\.");
        String moneda = comprobante.getMoneda().value();
        if (moneda.equals("MXN") || moneda.equals("XXX")) {
            moneda = "M.N.";
        }
        this.cell("IMPORTE EN LETRAS:", 55, "L", "0", 1);
        if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.I)
                || comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.E)) {
            if (moneda.equals("M.N.")) {
                this.cell(
                        "*** " + importe_letra(temp[0]).toUpperCase() + " PESOS " + temp[1] + "/100 " + moneda + " ***",
                        55, "L", "0", 1);
            } else if (moneda.equals("USD")) {
                this.cell("*** " + importe_letra(temp[0]).toUpperCase() + " DOLARES " + temp[1] + "/100 " + moneda
                        + " ***", 55, "L", "0", 1);
            } else {
                this.cell("*** " + importe_letra(temp[0]).toUpperCase() + " " + temp[1] + "/100 " + moneda + " ***", 55,
                        "L", "0", 1);
            }
        } else if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.T)) {
            this.cell("*** CERO PESOS 0/100 " + moneda + " ***", 55, "L", "0", 1);
        } else {
            if (moneda.equals("M.N.")) {
                this.cell("*** " + importe_letra(temp[0]).toUpperCase() + " PESOS 0/100 " + moneda + " ***", 55, "L",
                        "0", 1);
            } else {
                this.cell("*** " + importe_letra(temp[0]).toUpperCase() + "0/100 " + moneda + ". ***", 55, "L", "0", 1);
            }
        }

        this.setXY(145, fin);
        this.cell("SUBTOTAL", 30, "L", "0", 1);
        String tasaOCuota = "0";
        if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.I) || comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.E)) {
            if (comprobante.getImpuestos() != null) {
                tasaOCuota = comprobante.getImpuestos().getTraslados() == null ? ""
                        : comprobante.getImpuestos().getTraslados().getTraslado().get(0).getTasaOCuota().toString();
                tasaOCuota = tasaOCuota.substring(2, 4);
                tasaOCuota.replaceAll("0", "");
            }
        }
        String tasaOCuotaRet = "0";
        if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.I) || comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.E)) {
            for (Comprobante.Conceptos.Concepto concepto : comprobante.getConceptos().getConcepto()) {
                if (concepto.getImpuestos().getRetenciones() != null) {
                    tasaOCuotaRet = concepto.getImpuestos().getRetenciones().getRetencion().get(0).getTasaOCuota()
                            .toString();
                    tasaOCuotaRet = tasaOCuotaRet.substring(2, 4);
                    if (!tasaOCuotaRet.equals("00")) {
                        tasaOCuotaRet = tasaOCuotaRet.replace("0", "");
                    }
                }
            }
        }
        this.cell("IVA " + tasaOCuota + ".00%", 30, "L", "0", 1);
        this.cell("I.V.A. RETENIDO " + formatter.format(new BigDecimal(tasaOCuotaRet)) + "%", 30, "L", "0", 1);
        this.cell("DESCUENTO ", 30, "L", "0", 1);
        this.cell("TOTAL ", 30, "L", "0", 1);

        this.setXY(175, fin);
        this.cell("$" + formatter.format(comprobante.getSubTotal()), 20, "R", "0", 1);
        String totalImpuestosTrasladados = "0.00";
        if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.I) || comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.E)) {
            if (comprobante.getImpuestos() != null) {
                totalImpuestosTrasladados = comprobante.getImpuestos().getTotalImpuestosTrasladados() == null ? "0.00"
                        : comprobante.getImpuestos().getTotalImpuestosTrasladados().toString();
            }
        }
        this.cell("$" + formatter.format(new BigDecimal(totalImpuestosTrasladados)), 20, "R", "0", 1);
        String totalImpuestosRetenidos = "0.00";
        if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.I) || comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.E)) {
            if (comprobante.getImpuestos() != null) {
                totalImpuestosRetenidos = comprobante.getImpuestos().getTotalImpuestosRetenidos() == null ? "0.00"
                        : comprobante.getImpuestos().getTotalImpuestosRetenidos().toString();
            }
        }
        this.cell("$" + formatter.format(new BigDecimal(totalImpuestosRetenidos)), 20, "R", "0", 1);
        this.cell(
                "$" + formatter.format(
                        comprobante.getDescuento() == null ? new BigDecimal("0.0") : comprobante.getDescuento()),
                20, "R", "0", 1);
        this.cell("$" + formatter.format(comprobante.getTotal()), 20, "R", "0", 1);

        fin = this.getY() + 4;
        NodeList nodeTimbreFiscalDigital = this.documentXml.getElementsByTagName("tfd:TimbreFiscalDigital");
        String url = "https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx";
        String uuid = nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("UUID").getNodeValue();
        String selloCFD = nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("SelloCFD").getNodeValue();
        String sSubCadena = selloCFD.substring(selloCFD.length() - 8, selloCFD.length());

        String qrcode = url + "?id=" + uuid + "&re=" + comprobante.getEmisor().getRfc() + "&rr="
                + comprobante.getReceptor().getRfc() + "&tt=" + comprobante.getTotal().toString() + "&fe=" + sSubCadena;// version
        // 3.3

        QRGenerator qr = new QRGenerator();
        qr.build(qrcode);
        InputStream imageQRCode = qr.getQrcode();
        this.image(imageQRCode, 9, fin + 7, 22);

        this.rect(50, fin, 205, fin + 32);
        this.setXY(40, fin);
//			this.setTextColor(blue);
        this.setFont("", "B", 7);
        this.setFill(true);
        this.setTextColor(BaseColor.WHITE);
        this.setBackgroundColor(BaseColor.GRAY);
        this.cell("Cadena Original del complemento de certificación digital del SAT:", 155, "C", "0", 1);
        this.setFont("", "", 5);
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
        this.multiCell(cadenaOriginalTFD, 155, "L", "0");

        this.setFont("", "B", 7);
        this.setX(40);
        this.setFill(true);
        this.setTextColor(BaseColor.WHITE);
        this.setBackgroundColor(BaseColor.GRAY);
        this.cell("Sello Digital del Emisor:", 155, "C", "0", 1);
        this.setFont("", "", 5);
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
        this.multiCell(nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("SelloCFD").getNodeValue(), 155,
                "L", "0");
        this.setFont("", "B", 7);
        this.setX(40);
        this.setFill(true);
        this.setTextColor(BaseColor.WHITE);
        this.setBackgroundColor(BaseColor.GRAY);
        this.cell("Sello Digital del SAT:", 155, "C", "0", 1);
        this.setFont("", "", 5);
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
        this.multiCell(nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("SelloSAT").getNodeValue(), 155,
                "L", "0");
        this.setFont("", "", 7);
        this.setY(this.getY() + 2);
        this.cell("Este documento es una representación impresa de un CFDI " + comprobante.getVersion(), 205, "C", "0",
                1);
    }

    public void header() throws DocumentException {
        this.setFill(false);
        NodeList nodeTimbreFiscalDigital = documentXml.getElementsByTagName("tfd:TimbreFiscalDigital");
        NodeList nodeComprobante = documentXml.getElementsByTagName("cfdi:Comprobante");
        this.setFont("", "", 7);

        this.image(Utils.getParametro("path_logo") + comprobante.getEmisor().getRfc() + ".png", 15, 5, 60);
//        this.image(Utils.getParametro("path_logo") + comprobante.getEmisor().getRfc() + ".png", 10, 255, 20);
//		this.setTextColor(blue);

        Emisor emisor = comprobante.getEmisor();
        this.setXY(55, 1);
        this.setFont("", "B", 7);
//		this.setTextColor(blue);
        this.cell(emisor.getNombre(), 70, "C", "0", 1);
        this.setFont("", "", 7);
        this.cell(emisor.getRfc(), 70, "C", "0", 1);
        this.cell("Régimen Fiscal: " + cambiaRegimen(emisor.getRegimenFiscal()), 70, "C", "0", 1);
        this.cell("No. de Serie del Certificado del Emisor: "
                + nodeComprobante.item(0).getAttributes().getNamedItem("NoCertificado").getNodeValue(), 70, "C", "0", 1);
        this.cell("No. de Serie del Certificado del SAT: "
                + nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("NoCertificadoSAT").getNodeValue(), 70,
                "C", "0", 1);
        if (addendaEmisor != null) {
            this.multiCell(addendaEmisor.getDomicilioEmisor() == null ? "Domicilio:" : "Domicilio: "
                    + addendaEmisor.getDomicilioEmisor().toString(), 70, "C", "0");
        } else {
            this.multiCell("Domicilio:", 70, "C", "0");
        }

        String tipoDocumento = "";
        if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.I)) {
            tipoDocumento = "FACTURA";
        } else if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.E)) {
            tipoDocumento = "NOTA DE CRÉDITO";
        } else if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.T)) {
            tipoDocumento = "TRASLADO";
        }

        this.setFill(false);
        this.setXY(140, 1);
        this.setFont("", "B", 7);
//		this.setTextColor(BaseColor.WHITE);
//		this.setBackgroundColor(blue);
        this.cell(tipoDocumento, 55, "C", "1", 1);
        this.setFont("", "", 7);
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
//		this.setTextColor(blue);
        String serie = comprobante.getSerie() == null ? "" : comprobante.getSerie();
        String folio = comprobante.getFolio() == null ? "" : comprobante.getFolio();
        this.cell(serie + "-" + folio, 55, "C", "0", 1);
        this.setFill(false);
        this.setFont("", "B", 7);
//		this.setBackgroundColor(blue);
//		this.setTextColor(BaseColor.WHITE);
        this.cell("Fecha y hora de emisión", 55, "C", "1", 1);
        this.setFont("", "", 7);
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
//		this.setTextColor(blue);
        this.cell(comprobante.getFecha().toString(), 55, "C", "0", 1);
        this.setFill(false);
        this.setFont("", "B", 7);
//		this.setBackgroundColor(blue);
//		this.setTextColor(BaseColor.WHITE);
        this.cell("Fecha y hora de certificación", 55, "C", "1", 1);
        this.setFont("", "", 7);
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
//		this.setTextColor(blue);
        this.cell(nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("FechaTimbrado").getNodeValue(), 55, "C",
                "0", 1);
        this.setFill(false);
        this.setFont("", "B", 7);
//		this.setBackgroundColor(blue);
//		this.setTextColor(BaseColor.WHITE);
        this.cell("Folio fiscal", 55, "C", "1", 1);
        this.setFont("", "", 7);
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
//		this.setTextColor(blue);
        this.cell(nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("UUID").getNodeValue(), 55, "C", "0", 1);
        this.cell("Lugar de Expedición: " + comprobante.getLugarExpedicion(), 55, "L", "0", 1);
        this.cell("Tipo de comprobante: " + comprobante.getTipoDeComprobante(), 55, "L", "0", 1);
        this.cell("Moneda: " + comprobante.getMoneda(), 55, "L", "0", 1);
        String formaPago = comprobante.getFormaPago() == null ? "" : comprobante.getFormaPago();
        this.cell("Forma de pago: " + formaPago, 55, "L", "0", 1);
        String metodoPago = comprobante.getMetodoPago() == null ? "" : comprobante.getMetodoPago().toString();
        this.cell("Metodo de pago: " + metodoPago, 55, "L", "0", 1);
        this.cell("Exportacion: " + comprobante.getExportacion(), 55, "L", "0", 1);
        this.cell(
                "Rfc PAC: "
                + nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("RfcProvCertif").getNodeValue(),
                55, "L", "0", 1);

        this.setFill(true);
        this.setXY(0, 30);
        this.setFont("", "B", 7);
        this.setTextColor(BaseColor.WHITE);
        this.setBackgroundColor(BaseColor.GRAY);
        this.cell("RECEPTOR", 130, "C", "1", 1);

        this.setFill(true);
        this.setBackgroundColor(BaseColor.WHITE);
        this.rect(10, 33, 140, 55);
        this.setFill(false);

        Receptor receptor = comprobante.getReceptor();
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
        this.setFont("", "", 7);
//		this.setTextColor(blue);
        this.cell("Nombre: " + receptor.getNombre(), 55, "l", "0", 1);
        this.cell("Rfc: " + receptor.getRfc(), 55, "l", "0", 1);
        this.cell("Uso CFDI: " + cambiaUsoCFDI(receptor.getUsoCFDI().value()).toUpperCase(), 55, "l", "0", 1);
        this.cell("Régimen Fiscal: " + cambiaRegimen(receptor.getRegimenFiscalReceptor()), 55, "l", "0", 1);
        if (addendaEmisor != null) {
            this.cell(addendaEmisor.getOrdenCompra() == null ? "PO: " : "PO: " + addendaEmisor.getOrdenCompra(), 55, "l", "0", 1);
            this.multiCell(addendaEmisor.getDomicilioReceptor() == null ? "Domicilio:" : "Domicilio : "
                    + addendaEmisor.getDomicilioReceptor().toString(), 139, "l", "0");
        } else {
            this.cell("PO: ", 55, "l", "0", 1);
            this.multiCell("Domicilio:", 139, "l", "0");
        }
        this.ln();
//		this.setXY(0, 52);
        this.setFont("", "B", 7);
        this.rect(10, this.getY(), 205, this.getY() + 6.3f);

        float wheaderConceptor[] = {18, 20, 15, 15, 17, 55, 15, 25};

        this.cell("ClaveProdServ", wheaderConceptor[0], "C", "0");
        float y = this.getY();
        this.cell("No. Identificación", wheaderConceptor[1], "C", "0");
        this.cell("Cantidad", wheaderConceptor[2], "C", "0");
        this.cell("Clave Unidad", wheaderConceptor[3], "C", "0");
        this.cell("Unidad", wheaderConceptor[4], "C", "0");
        this.cell("Descripción del producto", wheaderConceptor[5], "C", "0");
        this.cell("", wheaderConceptor[6], "C", "0");
        this.cell("Importe", wheaderConceptor[7], "C", "0");
        this.setXY(wheaderConceptor[0] + wheaderConceptor[1] + wheaderConceptor[2] + wheaderConceptor[3]
                + wheaderConceptor[4] + wheaderConceptor[5], y);
        this.multiCell("Precio unitario", wheaderConceptor[6], "C", "0");

        this.setFont("", "", 7);
    }

    public void body() throws DocumentException {
        this.setStroke(true);
        this.setXY(0, this.getY() - 3);
        this.setFont("", "B", 7);
        this.setFill(false);

        float wheaderConceptor[] = {17, 18, 15, 15, 20, 55, 15, 25};

        this.setFill(false);
        for (Comprobante.Conceptos.Concepto concepto : comprobante.getConceptos().getConcepto()) {
            this.setFont("", "", 7);
            this.cell(concepto.getClaveProdServ(), wheaderConceptor[0], "C", "0");
            float y3 = this.getY();
            this.cell(concepto.getNoIdentificacion() == null ? "" : concepto.getNoIdentificacion(), wheaderConceptor[1],
                    "C", "0");
            this.cell(formatter.format(concepto.getCantidad()), wheaderConceptor[2], "C", "0");
            this.cell(concepto.getClaveUnidad(), wheaderConceptor[3], "C", "0");
            this.cell(concepto.getUnidad(), wheaderConceptor[4], "C", "0");
            String numeroPedimento = "";
            String tempPedimento = "";
            if (!concepto.getInformacionAduanera().isEmpty()) {
                for (Comprobante.Conceptos.Concepto.InformacionAduanera informacionAduanera : concepto
                        .getInformacionAduanera()) {
                    tempPedimento = tempPedimento + informacionAduanera.getNumeroPedimento() + "\n";
                }
                numeroPedimento = "\nPedimento: " + tempPedimento;
            }
            String descripcion = concepto.getDescripcion().replaceAll("&#xA;", "\n");

            this.cell("", wheaderConceptor[5], "" + "l", "0");
            this.cell("$" + formatter.format(concepto.getValorUnitario()), wheaderConceptor[6], "R", "0");
            this.cell("$" + formatter.format(concepto.getImporte()), wheaderConceptor[7], "R", "0");
            this.setXY(wheaderConceptor[0] + wheaderConceptor[1] + wheaderConceptor[2] + wheaderConceptor[3]
                    + wheaderConceptor[4], y3);
            this.multiCell(descripcion + numeroPedimento, wheaderConceptor[5], "J", "0");
//            this.ln();
            if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.I)) {
                if (concepto.getImpuestos().getTraslados() != null) {
                    if (!concepto.getImpuestos().getTraslados().getTraslado().isEmpty()) {
                        for (Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado traslado : concepto
                                .getImpuestos().getTraslados().getTraslado()) {
                            this.cell("Traslado:", Wheaderimpuesto[0], "L", "", 0);
                            this.cell("Base: " + traslado.getBase(), Wheaderimpuesto[1], "L", "", 0);
                            this.cell("Impuesto: " + traslado.getImpuesto(), Wheaderimpuesto[2], "L", "", 0);

                            if (traslado.getTipoFactor().equals(CTipoFactor.TASA)) {
                                this.cell("TipoFactor: Tasa", Wheaderimpuesto[3], "L", "", 0);
                            } else if (traslado.getTipoFactor().equals(CTipoFactor.CUOTA)) {
                                this.cell("TipoFactor: Cuota", Wheaderimpuesto[3], "L", "", 0);
                            } else if (traslado.getTipoFactor().equals(CTipoFactor.EXENTO)) {
                                this.cell("TipoFactor: Exento", Wheaderimpuesto[3], "L", "", 0);
                            }
                            this.cell("TasaOCuota: " + traslado.getTasaOCuota(), Wheaderimpuesto[4], "L", "", 0);
                            this.cell("Importe: " + traslado.getImporte(), Wheaderimpuesto[4], "L", "", 0);
                            this.ln();
                        }
                    }
                }
                if (concepto.getImpuestos().getRetenciones() != null) {
                    if (!concepto.getImpuestos().getRetenciones().getRetencion().isEmpty()) {
                        for (Comprobante.Conceptos.Concepto.Impuestos.Retenciones.Retencion retencion : concepto
                                .getImpuestos().getRetenciones().getRetencion()) {
                            if (retencion.getImporte().compareTo(BigDecimal.ZERO) > 0) {
                                this.cell("Retencion:", Wheaderimpuesto[0], "L", "", 0);
                                this.cell("Base: " + retencion.getBase(), Wheaderimpuesto[1], "L", "", 0);
                                this.cell("Impuesto: " + retencion.getImpuesto(), Wheaderimpuesto[2], "L", "", 0);

                                if (retencion.getTipoFactor().equals(CTipoFactor.TASA)) {
                                    this.cell("TipoFactor: Tasa", Wheaderimpuesto[3], "L", "", 0);
                                } else if (retencion.getTipoFactor().equals(CTipoFactor.CUOTA)) {
                                    this.cell("TipoFactor: Cuota", Wheaderimpuesto[3], "L", "", 0);
                                } else if (retencion.getTipoFactor().equals(CTipoFactor.EXENTO)) {
                                    this.cell("TipoFactor: Exento", Wheaderimpuesto[3], "L", "", 0);
                                }
                                this.cell("TasaOCuota: " + retencion.getTasaOCuota(), Wheaderimpuesto[4], "L", "", 0);
                                this.cell("Importe: " + retencion.getImporte(), Wheaderimpuesto[4], "L", "", 0);
                                this.ln();
                            }
                        }
                    }
                }
            }
        }

        if (!this.comprobante.getCfdiRelacionados().isEmpty()) {
            this.setFill(false);
            this.ln();
            this.setFont("HELVETICA", "B", 7);
            this.cell("CfdiRelacionados", 55, "L", "0", 1);
            for (Comprobante.CfdiRelacionados cfdiRelacionados : this.comprobante.getCfdiRelacionados()) {
                this.setFont("HELVETICA", "", 7);
                this.setTextColor(DEFAULT_COLOR);
                this.setFill(false);
                this.cell("TipoRelacion: " + cfdiRelacionados.getTipoRelacion(), 55, "L", "0", 1);
                String uuids = "";
                for (Comprobante.CfdiRelacionados.CfdiRelacionado cfdiRelacionado : cfdiRelacionados.getCfdiRelacionado()) {
                    uuids = uuids + cfdiRelacionado.getUUID() + "   ";
                }
                this.multiCell("UUID: " + uuids, 205, "L", "0");
            }
        }
        if (addendaEmisor != null) {
            if (addendaEmisor.getObservaciones() != null) {
                this.ln();
                this.multiCell("Observaciones: " + addendaEmisor.getObservaciones(), 185, "L", "0");
            }
        }
        this.ln();
        float y = this.getY();
        this.setTextColor(DEFAULT_COLOR);
        this.setFill(false);
        this.setFont("", "B", 7);
//		this.setTextColor(blue);
        this.cell("Cuenta: ", 30, "R", "0", 0);
        this.setX(110);
        this.cell("Cuenta: ", 30, "R", "0", 0);

        this.setX(31);
        this.setFill(false);
        this.setFont("", "", 7);
        this.cell("9001335", 30, "L", "0", 0);
        this.setX(141);
        this.cell("3794469", 30, "L", "0", 1);

        this.setX(0);
        this.setFill(false);
        this.setFont("", "B", 7);
        this.cell("Clabe interbancaria: ", 30, "R", "0", 0);
        this.setX(110);
        this.cell("Clabe interbancaria: ", 30, "R", "0", 0);
        this.setX(31);
        this.setFill(false);
        this.setFont("", "", 7);
        this.cell("0026-804556-9001-3357", 30, "L", "0", 0);
        this.setX(141);
        this.cell("0026-807003-3794-4696", 30, "L", "0", 1);

        this.setX(0);
        this.setFont("", "B", 7);
        this.setFill(false);
        this.cell("Banco: ", 30, "R", "0", 0);
        this.setX(110);
        this.cell("Banco: ", 30, "R", "0", 0);
        this.setX(31);
        this.setFill(false);
        this.setFont("", "", 7);
        this.cell("BANAMEX", 30, "L", "0", 0);
        this.setX(141);
        this.cell("BANAMEX", 30, "L", "0", 1);

        this.setX(0);
        this.setFill(false);
        this.setFont("", "B", 7);
        this.cell("Moneda: ", 30, "R", "0", 0);
        this.setX(110);
        this.cell("Moneda: ", 30, "R", "0", 0);
        this.setX(31);
        this.setFill(false);
        this.setFont("", "", 7);
        this.cell("Dolares (USD)", 30, "L", "0", 0);
        this.setX(141);
        this.cell("Peso Mexicano (MXN)", 30, "L", "0", 1);

        this.setFill(false);
        fin = this.getY();
    }

    public void creaPDF(String carpeta, Document documentXml, String uuid, Comprobante comprobante,
            String cadenaOriginalTFD, AddendaEmisor addendaEmisor) throws Exception {
        this.comprobante = comprobante;
        this.cadenaOriginalTFD = cadenaOriginalTFD;
        this.documentXml = documentXml;
        this.addendaEmisor = addendaEmisor;

        this.createPDF(carpeta + uuid + "_temp1.pdf");
        this.setMargin(10, 10, 10);
        this.addFont(Utils.getParametro("path_fonts") + "LiberationSans-Regular.ttf", "LiberationSans");
        this.setFont("LiberationSans", "", 12);
        this.setPageBreak(70);
        this.addPage();
        this.body();
        this.closePDF();
//        PdfPTable pdfPTable = this.numberOfPage("Página: %d/%d", com.itextpdf.text.Element.ALIGN_CENTER);
//        PdfPTable pdfPTable = this.numberOfPage("", com.itextpdf.text.Element.ALIGN_CENTER);
//        ByteArrayOutputStream pdfStream = this.getPDF(pdfPTable, "footer");
//        Output.save(new ByteArrayInputStream(pdfStream.toByteArray()), carpeta, uuid + "_temp1.pdf");
//        pdfStream.close();
    }

    private String cambiaUsoCFDI(String usoCfdi) {
        String regresa = "";
        if (usoCfdi.equals("G01")) {
            regresa = "G01 - Adquisición de mercancias";
        } else if (usoCfdi.equals("G02")) {
            regresa = "G02 - Devoluciones, descuentos o bonificaciones";
        } else if (usoCfdi.equals("G03")) {
            regresa = "G03 - Gastos en general";
        } else if (usoCfdi.equals("I01")) {
            regresa = "I01 - Construcciones";
        } else if (usoCfdi.equals("I02")) {
            regresa = "I02 - Mobilario y equipo de oficina por inversiones";
        } else if (usoCfdi.equals("I03")) {
            regresa = "I03 - Equipo de transporte";
        } else if (usoCfdi.equals("I04")) {
            regresa = "I04 - Equipo de computo y accesorios";
        } else if (usoCfdi.equals("I05")) {
            regresa = "I05 - Dados, troqueles, moldes, matrices y herramental";
        } else if (usoCfdi.equals("I06")) {
            regresa = "I06 - Comunicaciones telefónicas";
        } else if (usoCfdi.equals("I07")) {
            regresa = "I07 - Comunicaciones satelitales";
        } else if (usoCfdi.equals("I08")) {
            regresa = "I08 - Otra maquinaria y equipo";
        } else if (usoCfdi.equals("D01")) {
            regresa = "D01 - Honorarios médicos, dentales y gastos hospitalarios.";
        } else if (usoCfdi.equals("D02")) {
            regresa = "D02 - Gastos médicos por incapacidad o discapacidad";
        } else if (usoCfdi.equals("D03")) {
            regresa = "D03 - Gastos funerales.";
        } else if (usoCfdi.equals("D04")) {
            regresa = "D04 - Donativos.";
        } else if (usoCfdi.equals("D05")) {
            regresa = "D05 - Intereses reales efectivamente pagados por créditos hipotecarios (casa habitación).";
        } else if (usoCfdi.equals("D06")) {
            regresa = "D06 - Aportaciones voluntarias al SAR.";
        } else if (usoCfdi.equals("D07")) {
            regresa = "D07 - Primas por seguros de gastos médicos.";
        } else if (usoCfdi.equals("D08")) {
            regresa = "D08 - Gastos de transportación escolar obligatoria.";
        } else if (usoCfdi.equals("D09")) {
            regresa = "D09 - Depósitos en cuentas para el ahorro, primas que tengan como base planes de pensiones.";
        } else if (usoCfdi.equals("D10")) {
            regresa = "D10 - Pagos por servicios educativos (colegiaturas)";
        } else if (usoCfdi.equals("P01")) {
            regresa = "P01 - Por definir.";
        } else if (usoCfdi.equals("S01")) {
            regresa = "S01 - Sin efectos fiscales.";
        }
        return regresa;
    }

    private String importe_letra(String cadena) {
        String retorno = "";
        ImporteLetra numero;
        int i = Integer.parseInt(cadena);
        numero = new ImporteLetra(i);
        retorno = numero.convertirLetras(i);
        return retorno;
    }

    private String cambiaRegimen(String regimen) {
        if ("601".equals(regimen)) {
            regimen = "601-General de Ley Personas Morales";
        } else if ("603".equals(regimen)) {
            regimen = "603-Personas Morales con Fines no Lucrativos";
        } else if ("605".equals(regimen)) {
            regimen = "605-Sueldos y Salarios e Ingresos Asimilados a Salarios";
        } else if ("606".equals(regimen)) {
            regimen = "606-Arrendamiento";
        } else if ("607".equals(regimen)) {
            regimen = "607-Régimen de Enajenación o Adquisición de Bienes";
        } else if ("608".equals(regimen)) {
            regimen = "608-Demás ingresos";
        } else if ("610".equals(regimen)) {
            regimen = "610-Residentes en el Extranjero sin Establecimiento Permanente en México";
        } else if ("611".equals(regimen)) {
            regimen = "611-Ingresos por Dividendos (socios y accionistas)";
        } else if ("612".equals(regimen)) {
            regimen = "612-Personas Físicas con Actividades Empresariales y Profesionales";
        } else if ("614".equals(regimen)) {
            regimen = "614-Ingresos por intereses";
        } else if ("615".equals(regimen)) {
            regimen = "615-Régimen de los ingresos por obtención de premios";
        } else if ("616".equals(regimen)) {
            regimen = "616-Sin obligaciones fiscales";
        } else if ("620".equals(regimen)) {
            regimen = "620-Sociedades Cooperativas de Producción que optan por diferir sus ingresos";
        } else if ("621".equals(regimen)) {
            regimen = "621-Incorporación Fiscal";
        } else if ("622".equals(regimen)) {
            regimen = "622-Actividades Agrícolas, Ganaderas, Silvícolas y Pesqueras";
        } else if ("623".equals(regimen)) {
            regimen = "623-Opcional para Grupos de Sociedades";
        } else if ("624".equals(regimen)) {
            regimen = "624-Coordinados";
        } else if ("625".equals(regimen)) {
            regimen = "625-Régimen de las Actividades Empresariales con ingresos a través de Plataformas Tecnológicas";
        } else if ("626".equals(regimen)) {
            regimen = "626-Régimen Simplificado de Confianza";
        }
        return regimen;
    }
}
