/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mx.edifact.controller;

import com.itextpdf.text.DocumentException;
import com.mx.edifact.dto.AddendaEmisor;
import com.mx.edifact.dto.ResponceWs;
import com.mx.edifact.dto.RespuestaTimbrado;
import com.mx.edifact.model.CfdiRespaldo;
import com.mx.edifact.model.Cfdis;
import com.mx.edifact.model.LogCatcher;
import com.mx.edifact.service.CfdisService;
import com.mx.edifact.service.LogCatcherService;
import com.mx.edifact.service.RespaldoService;
import com.mx.edifact.utils.Base64Coder;
import com.mx.edifact.utils.CreaPDF40;
import com.mx.edifact.utils.CreaPDFComercioExterior;
import com.mx.edifact.utils.CreaPDFPagos20;
import com.mx.edifact.utils.DigitalSignature;
import com.mx.edifact.utils.Output;
import com.mx.edifact.utils.Utils;
import com.mx.edifact.utils.UtilsJaxb;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import mx.gob.sat.cfd4_0.CTipoDeComprobante;
import mx.gob.sat.cfd4_0.Comprobante;
import mx.gob.sat.cfd4_0.Comprobante.Complemento;
import mx.gob.sat.cfd4_0.ObjectFactory;
import comercioexterior20.ComercioExterior;
import mx.gob.sat.pagos20.Pagos;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author germa
 */
public class CreaComprobanteController {

    private static final Logger log = LogManager.getLogger(CreaComprobanteController.class);

    private Document xmlDocument;

    public synchronized ResponceWs procesoTimbrado(String xml, AddendaEmisor addendaEmisor, String addenda, String addendaViscofan) throws Exception {
        if (xml.contains("<pago20:Pagos")) {
            if (!xml.contains("xmlns:pago20=\"http://www.sat.gob.mx/Pagos20\"")) {
                String[] temp = xml.split("<pago20:Pagos Version=\"2.0\">");
                xml = temp[0] + "<pago20:Pagos xmlns:pago20=\"http://www.sat.gob.mx/Pagos20\" Version=\"2.0\">" + temp[1];
            }
        } else if (xml.contains("<cfdi:ComercioExterior")) {
            if (!xml.contains("xmlns:cce20=\"http://www.sat.gob.mx/ComercioExterior20\"")) {
                String[] temp = xml.split("<cfdi:ComercioExterior");
                xml = temp[0] + "<cfdi:ComercioExterior xmlns:cce20=\"http://www.sat.gob.mx/ComercioExterior20\" " + temp[1];
            }
        } else if (xml.contains("<cfdi:ComercioExterior")) {
            if (!xml.contains("xmlns:cce11=\"http://www.sat.gob.mx/ComercioExterior11\"")) {
                String[] temp = xml.split("<cfdi:ComercioExterior");
                xml = temp[0] + "<cfdi:ComercioExterior xmlns:cce11=\"http://www.sat.gob.mx/ComercioExterior11\" " + temp[1];
            }
        }
        Comprobante comprobante = UtilsJaxb.parseXML(xml);
        try {
            if (comprobante != null && comprobante.getComplemento() != null) {
                ObjectFactory of = new ObjectFactory();
                Complemento complemento = of.createComprobanteComplemento();
                Complemento o = comprobante.getComplemento();
                for (Object c : o.getAny()) {
                    if (c.toString().contains("<pago20:Pagos") || c.toString().contains("Pagos")) {
                        String[] temp = xml.split("<pago20:Pagos");
                        String[] temp2 = temp[1].split("</pago20:Pagos>");
                        String compPagos = "";
                        if (temp2[0].contains("xmlns:pago20=\"http://www.sat.gob.mx/Pagos20\"")) {
                            compPagos = "<pago20:Pagos "
                                    + temp2[0] + "</pago20:Pagos>";
                        } else {
                            compPagos = "<pago20:Pagos xmlns:pago20=\"http://www.sat.gob.mx/Pagos20\""
                                    + temp2[0] + "</pago20:Pagos>";
                        }
                        Pagos pagos = UtilsJaxb.parsePagos(compPagos);
                        complemento.getAny().add(pagos);
                    } else if (c.toString().contains("<cfdi:ComercioExterior") || c.toString().contains("ComercioExterior")) {
                        String[] temp = xml.split("<cfdi:ComercioExterior");
                        String[] temp2 = temp[1].split("</cfdi:ComercioExterior>");
                        String compCCE = "";
                        if (temp2[0].contains("xmlns:cce20=\"http://www.sat.gob.mx/ComercioExterior20\"")) {
                            compCCE = "<cfdi:ComercioExterior "
                                    + temp2[0] + "</cfdi:ComercioExterior>";
                        } else {
                            compCCE = "<cce20:ComercioExterior xmlns:cce20=\"http://www.sat.gob.mx/ComercioExterior20\" "
                                    + temp2[0] + "</cfdi:ComercioExterior>";
                        }
                        compCCE = compCCE.replaceAll("cfdi:", "cce20:");
                        ComercioExterior cce = UtilsJaxb.parseCCE(compCCE);
                        complemento.getAny().add(cce);
                    }

                }
                comprobante.setComplemento(null);
                comprobante.setComplemento(complemento);
            }
        } catch (Exception e) {
            log.error("Error ::", e);
        }
        String xmlSello = null;
        RespuestaTimbrado respuestaTimbrado = null;
        try {
            RespaldoService respaldoService = new RespaldoService();
            CfdiRespaldo cfdiRespaldo = null;
            try {
                cfdiRespaldo = respaldoService.consultarPassword(comprobante.getEmisor().getRfc());
                if (cfdiRespaldo == null) {
                    return creaRespuesta("WS207", "El rfc del emisor no existe en los certificados.", null, null, null, null, null, null, null);
                }
            } catch (Exception e) {
                log.error("Error ::", e);
            }
            try {
                xmlDocument = Utils.agregaSchemaLocationComprobante(comprobante);
            } catch (Exception e) {
                log.error("Error :: ", e);
            }
            xmlSello = generaSello(Utils.getParametro("path_files_key_cert") + comprobante.getEmisor().getRfc() + ".key",
                    Utils.quitarAlias(cfdiRespaldo.getCfdi_tool(), "XMzDdG4D03CKm2IxIWQw7g=="),
                    Utils.getParametro("path_files_key_cert") + comprobante.getEmisor().getRfc() + ".cer", this.xmlDocument);

            respuestaTimbrado = timbrarComprobante(comprobante, xmlSello);
            if (respuestaTimbrado.getCodigoResultado().equals("100")) {
                byte[] CFDI = Base64Coder.decodeLines(respuestaTimbrado.getDocumentoTimbrado());
                String str = new String(CFDI, "UTF-8");
                String[] tempCadOrg = str.split("<tfd:TimbreFiscalDigital");
                String[] temptempCadOrg2 = tempCadOrg[1].split("></tfd:TimbreFiscalDigital>");
                String tempCadOrg3 = "<tfd:TimbreFiscalDigital " + temptempCadOrg2[0] + "/>";
                String cadenaOriginalTFD = creaCadenaOriginalTFD(
                        Utils.getParametro("path_files_xsd_xslt") + "cadenaoriginal_TFD_1_1.xslt", tempCadOrg3);
                str = Utils.reemplazar(str, "></tfd:TimbreFiscalDigital>", "/>");
                Document documentXML = Utils.loadXMLFromString(str);
                NodeList nodeComprobante = documentXML.getElementsByTagName("cfdi:Comprobante");
                NodeList nodeTimbreFiscalDigital = documentXML.getElementsByTagName("tfd:TimbreFiscalDigital");
                String noCertificado = nodeComprobante.item(0).getAttributes().getNamedItem("NoCertificado").getNodeValue();
                String fechaTimbrado = nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("FechaTimbrado").getNodeValue();
                String selloSAT = nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("SelloSAT").getNodeValue();
                String uuid = nodeTimbreFiscalDigital.item(0).getAttributes().getNamedItem("UUID").getNodeValue();
                String selloCFDI = nodeComprobante.item(0).getAttributes().getNamedItem("Sello").getNodeValue();
                String pdfBase64 = "";
                String temp[] = fechaTimbrado.split("T");
                String rutaPDF = Utils.getParametro("path_base") + "pdf/" + temp[0] + "/";
                String rutaXML = Utils.getParametro("path_base") + "xml/" + temp[0] + "/";
                if (!new File(rutaXML).exists()) {
                    new File(rutaXML).mkdirs();
                }
                if (!new File(rutaPDF).exists()) {
                    new File(rutaPDF).mkdirs();
                }
                String xmlAddenda = "";
                try {
                    if (!addenda.equals("")) {
                        addenda = addenda.replaceAll("<cfdi:AddendaEU>", "<cfdi:AddendaEU xmlns:eu=\"http://factura.envasesuniversales.com/addenda/eu\" xsi:schemaLocation=\"http://factura.envasesuniversales.com/addenda/eu http://factura.envasesuniversales.com/addenda/eu/EU_Addenda.xsd\">");
                        addenda = addenda.replaceAll("cfdi", "eu");
                        String xmlTemp[] = str.split("</cfdi:Complemento>");
                        String addendaTemp = "</cfdi:Complemento><cfdi:Addenda>" + addenda + "</cfdi:Addenda>";
                        xmlAddenda = xmlTemp[0] + addendaTemp + xmlTemp[1];
                    } else if (!addendaViscofan.equals("")) {
                        addendaViscofan = Utils.crearAddendaViscofan(addendaViscofan);
                        String temp2[] = addendaViscofan.split("<cfdi:Addenda>");
                        String xmlTemp[] = str.split("</cfdi:Complemento>");
                        String addendaTemp = "</cfdi:Complemento><cfdi:Addenda>" + temp2[1];
                        xmlAddenda = xmlTemp[0] + addendaTemp + xmlTemp[1];

                    }
                } catch (Exception e) {
                    log.error("Error al crear el XML :: ", e);
                }
                try {
                    CfdisService cfdisService = new CfdisService();
                    Cfdis cfdis = new Cfdis();
                    cfdis.setVersion(comprobante.getVersion());
                    cfdis.setSerie(comprobante.getSerie());
                    cfdis.setFolio(comprobante.getFolio());
                    cfdis.setFecha(comprobante.getFecha().toString());
                    cfdis.setSubTotal(comprobante.getSubTotal().doubleValue());
                    cfdis.setTotal(comprobante.getTotal().doubleValue());
                    cfdis.setTipoComprobante(comprobante.getTipoDeComprobante().toString());
                    if (xmlAddenda.equals("")) {
                        cfdis.setXml(respuestaTimbrado.getDocumentoTimbrado());
                    } else {
                        cfdis.setXml(Base64Coder.encodeString(xmlAddenda));
                    }
                    cfdis.setFechaTimbrado(Utils.stringToDatePatern(fechaTimbrado, "yyyy-MM-dd'T'HH:mm:ss"));
                    cfdis.setEstatus("F");
                    cfdis.setUuid(uuid);
                    cfdis.setRfcEmisor(comprobante.getEmisor().getRfc());
                    cfdis.setRfcReceptor(comprobante.getReceptor().getRfc());
                    cfdis.setNombreReceptor(comprobante.getReceptor().getNombre());
                    cfdis.setPagoGenerado(0);
//                    cfdis.setTipo_nomina(tipoNomina);
                    if (!cfdisService.buscarUuid(uuid)) {
                        cfdisService.insertar(cfdis);
                    } else {
                        log.info("Ya existe el UUID " + uuid + " en la BD.");
                    }
                } catch (SQLException | ParseException e) {
                    log.error("Error al insertar Cfdis :: ", e);
                }
                try {
                    LogCatcherService logCatcherService = new LogCatcherService();
                    LogCatcher logCatcher = new LogCatcher();
                    Date fecha = new Date();
                    logCatcher.setMoment(fecha);
                    logCatcher.setProject("Nomina");
                    logCatcher.setJob("Nomina");
                    logCatcher.setMessage(respuestaTimbrado.getCodigoDescripcion());
                    logCatcher.setCode(respuestaTimbrado.getCodigoResultado());
                    logCatcher.setFolio(comprobante.getFolio());
                    logCatcher.setSerie(comprobante.getSerie());
                    logCatcher.setUuid(uuid);
                    logCatcher.setRfc(comprobante.getEmisor().getRfc());
                    logCatcher.setRfcReceptor(comprobante.getReceptor().getRfc());
                    logCatcher.setEnviado("0");
//                    logCatcher.setEmails(email);
//                    logCatcher.setTipoNomina(tipoNomina);
                    logCatcherService.insertar(logCatcher);
                } catch (SQLException e) {
                    log.error("Error al insertar LogCatcher :: ", e);
                }

                try {
                    if (xmlAddenda.equals("")) {
                        Output.save(str, rutaXML, Utils.getNombreCfdi(comprobante) + ".xml");
                    } else {
                        Output.save(xmlAddenda, rutaXML, Utils.getNombreCfdi(comprobante) + ".xml");
                    }
                } catch (Exception e) {
                    log.error("Error al crear el XML :: ", e);
                }
                String nombrePdf = "";
                try {
                    nombrePdf = Utils.getNombreCfdi(comprobante);
                    if (comprobante.getTipoDeComprobante().equals(CTipoDeComprobante.P)) {
                        CreaPDFPagos20 pdfPagos20 = new CreaPDFPagos20("", "Letter", "mm");
                        pdfPagos20.creaPDF(rutaPDF, documentXML, nombrePdf, comprobante, cadenaOriginalTFD);
                        try {
                            rutaPDF = rutaPDF.replace("\\", "/");
                            Utils.manipulatePdf(rutaPDF + "/" + nombrePdf + "_temp1.pdf",
                                    rutaPDF + "/" + nombrePdf + ".pdf");
                            File f = new File(rutaPDF + "/" + nombrePdf + "_temp1.pdf");
                            if (!f.delete()) {
                                System.out.println("error al borrar el PDF");
                            }
                        } catch (DocumentException | IOException e) {
                            log.error("Error :: ", e);
                        }
                    } else {

                        CreaPDF40 creaPDF = new CreaPDF40("", "Letter", "mm");
                        creaPDF.creaPDF(rutaPDF, documentXML, nombrePdf, comprobante, cadenaOriginalTFD, addendaEmisor);
                        try {
                            if (comprobante != null && comprobante.getComplemento() != null) {
                                Comprobante.Complemento o = comprobante.getComplemento();
                                for (Object c : o.getAny()) {
                                    if (c instanceof comercioexterior20.ComercioExterior) {
                                        CreaPDFComercioExterior pdfComercioExterior = new CreaPDFComercioExterior("", "Letter", "mm");
                                        pdfComercioExterior.creaPDF(rutaPDF, documentXML, nombrePdf, comprobante, cadenaOriginalTFD);
                                        try {
                                            rutaPDF = rutaPDF.replace("\\", "/");
                                            Utils.manipulatePdfCartaPorte(rutaPDF + "/" + nombrePdf + "_temp1.pdf",
                                                    rutaPDF + "/" + nombrePdf + "_temp2.pdf", rutaPDF + "/" + nombrePdf);
                                            File f = new File(rutaPDF + "/" + nombrePdf + "_temp1.pdf");
                                            if (!f.delete()) {
                                                System.out.println("error al borrar el PDF");
                                            }
                                            f = new File(rutaPDF + "/" + nombrePdf + "_temp2.pdf");
                                            if (!f.delete()) {
                                                System.out.println("error al borrar el PDF");
                                            }
                                            f = new File(rutaPDF + "/" + nombrePdf + "_temp3.pdf");
                                            if (!f.delete()) {
                                                System.out.println("error al borrar el PDF");
                                            }
                                        } catch (Exception e) {
                                            log.error("Error :: ", e);
                                        }
                                    } else {
                                        System.out.println("El complemento " + c + " aún no ha sido declarado.");
                                        try {
                                            rutaPDF = rutaPDF.replace("\\", "/");
                                            Utils.manipulatePdf(rutaPDF + "/" + nombrePdf + "_temp1.pdf",
                                                    rutaPDF + "/" + nombrePdf + ".pdf");
                                            File f = new File(rutaPDF + "/" + nombrePdf + "_temp1.pdf");
                                            if (!f.delete()) {
                                                System.out.println("error al borrar el PDF");
                                            }
                                        } catch (Exception e) {
                                            log.error("Error :: ", e);
                                        }
                                    }
                                }
                            } else {
                                try {
                                    rutaPDF = rutaPDF.replace("\\", "/");
                                    Utils.manipulatePdf(rutaPDF + "/" + nombrePdf + "_temp1.pdf", rutaPDF + "/" + nombrePdf + ".pdf");
                                    File f = new File(rutaPDF + "/" + nombrePdf + "_temp1.pdf");
                                    if (!f.delete()) {
                                        System.out.println("error al borrar el PDF");
                                    }
                                } catch (Exception e) {
                                    log.error("Error :: ", e);
                                }
                            }
                        } catch (Exception e) {
                            log.error("Error al crear el PDF :: ", e);
                        }
//                        try {
//                            if (comprobante != null && comprobante.getComplemento() != null) {
//                                Comprobante.Complemento o = comprobante.getComplemento();
//                                for (Object c : o.getAny()) {
//                                    System.out.println("El complemento " + c + " aún no ha sido declarado.");
//                                }
//                            } else {
//                                try {
//                                    rutaPDF = rutaPDF.replace("\\", "/");
//                                    Utils.manipulatePdf(rutaPDF + "/" + nombrePdf + "_temp1.pdf",
//                                            rutaPDF + "/" + nombrePdf + ".pdf");
//                                    File f = new File(rutaPDF + "/" + nombrePdf + "_temp1.pdf");
//                                    if (!f.delete()) {
//                                        System.out.println("error al borrar el PDF");
//                                    }
//                                } catch (Exception e) {
//                                    log.error("Error :: ", e);
//                                }
//                            }
//                        } catch (Exception e) {
//                            log.error("Error al crear el PDF :: ", e);
//                        }
                    }
                } catch (Exception e) {
                    log.error("Error :: ", e);
                }
                try {
                    byte[] input_file = Files.readAllBytes(Paths.get(rutaPDF + nombrePdf + ".pdf"));
                    pdfBase64 = Base64Coder.encodeLines(input_file);
                    pdfBase64 = pdfBase64.replaceAll("(\n|\r)", "");
                } catch (IOException e) {
                    log.error("Error al convertir el pdf a Base64 :: ", e);
                }
                if (xmlAddenda.equals("")) {
                    return creaRespuesta(respuestaTimbrado.getCodigoResultado(), respuestaTimbrado.getCodigoDescripcion(), pdfBase64,
                            respuestaTimbrado.getDocumentoTimbrado(), fechaTimbrado, noCertificado, selloCFDI, selloSAT, uuid);
                } else {
                    return creaRespuesta(respuestaTimbrado.getCodigoResultado(), respuestaTimbrado.getCodigoDescripcion(), pdfBase64,
                            Base64Coder.encodeString(xmlAddenda), fechaTimbrado, noCertificado, selloCFDI, selloSAT, uuid);
                }
            } else {
                try {
                    LogCatcherService logCatcherService = new LogCatcherService();
                    LogCatcher logCatcher = new LogCatcher();
                    Date fecha = new Date();
                    logCatcher.setMoment(fecha);
                    logCatcher.setProject("Nomina");
                    logCatcher.setJob("Nomina");
                    logCatcher.setMessage(respuestaTimbrado.getCodigoDescripcion());
                    logCatcher.setCode(respuestaTimbrado.getCodigoResultado());
                    logCatcher.setFolio(comprobante.getFolio());
                    logCatcher.setSerie(comprobante.getSerie());
                    logCatcher.setRfc(comprobante.getEmisor().getRfc());
                    logCatcher.setRfcReceptor(comprobante.getReceptor().getRfc());
//                    logCatcher.setEmails(email);
//                    logCatcher.setTipoNomina(tipoNomina);
                    logCatcherService.insertar(logCatcher);
                } catch (SQLException e) {
                    log.error("Error al insertar LogCatcher :: ", e);
                }
                return creaRespuesta(respuestaTimbrado.getCodigoResultado(), respuestaTimbrado.getCodigoDescripcion(), null, null, null, null, null, null, null);
            }
        } catch (Exception e) {
            log.error("Error :: ", e);
            return creaRespuesta("000", "Error interno", null, null, null, null, null, null, null);
        }

    }

    private ResponceWs creaRespuesta(String codigo, String descripcion, String documentoPdf, String documentoXml,
            String fechaTimbrado, String noCertificado, String selloCFDI, String selloSAT, String uuid) {
        ResponceWs responceWs = new ResponceWs();
        responceWs.setCodigo(codigo);
        responceWs.setDescripcion(descripcion);
        responceWs.setDocumentopdf(documentoPdf);
        responceWs.setDocumentoxml(documentoXml);
        responceWs.setFechaTimbrado(fechaTimbrado);
        responceWs.setNoCertificado(noCertificado);
        responceWs.setSelloCFDI(selloCFDI);
        responceWs.setSelloSAT(selloSAT);
        responceWs.setUUID(uuid);
        return responceWs;
    }

    public String generaSello(String private_key_dir, String private_key_pass, String cert_dir, Document docComprobante)
            throws Exception {
        byte[] cert = FileUtils.readFileToByteArray(new File(cert_dir));
        byte[] key = FileUtils.readFileToByteArray(new File(private_key_dir));

        DigitalSignature digitalSignature = new DigitalSignature(cert, key, private_key_pass);

        docComprobante = digitalSignature.stamp40(docComprobante);

//        byte[] ptext = Utils.getString(docComprobante).getBytes("UTF-8");
//        return Base64Coder.encodeLines(ptext).replaceAll("(\n|\r)", "");
        return Base64Coder.encodeString(Utils.getString(docComprobante));
    }

    public RespuestaTimbrado timbrarComprobante(Comprobante comprobante, String docXML) {
        RespuestaTimbrado result = null;
        try {
//            Document documentXML = Utils.loadXMLFromString(comprobante);
//            NodeList nodeComprobante = documentXML.getElementsByTagName("cfdi:Comprobante");
//            String serie = nodeComprobante.item(0).getAttributes().getNamedItem("Serie").getNodeValue();
//            String folio = nodeComprobante.item(0).getAttributes().getNamedItem("Folio").getNodeValue();
            log.info("Documento enviado :: " + docXML);
//            SSLContext ctx = SSLContext.getInstance("SSL");
//            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
//            SSLContext.setDefault(ctx);
            log.info("Envia a timbrar: " + comprobante.getSerie() + "-" + comprobante.getFolio());
            TimbrarControllerImpl timbrarController = new TimbrarControllerImpl();
            result = timbrarController.timbrarComprobante(docXML);
            if (result.getCodigoResultado().equals("100")) {

                log.info("Documento :: " + result.getCodigoResultado());
                log.info("Documento descripcion :: " + result.getCodigoDescripcion());
            } else {
                log.info("Documento :: " + result.getCodigoResultado());
                log.info("Documento descripcion :: " + result.getCodigoDescripcion());
            }

        } catch (Exception e) {
            log.error("Error al timbrar el CFDI :: ", e);
        }
        return result;
    }

    public String creaCadenaOriginalTFD(String xslt, String cfdi) throws Exception {
        String retorno = "";
        try {
            StreamSource sourceXSL = new StreamSource(xslt);
            StreamSource sourceXML = new StreamSource(new java.io.StringReader(cfdi));
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(sourceXSL);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Result out = new StreamResult(baos);
            transformer.transform(sourceXML, out);
            byte[] cadenaOriginalArray = baos.toByteArray();

            retorno = new String(cadenaOriginalArray, "UTF-8");
        } catch (UnsupportedEncodingException | TransformerException e) {
            throw new Exception(e);
        }
        return retorno;
    }
}
