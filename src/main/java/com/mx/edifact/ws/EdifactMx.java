/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/WebService.java to edit this template
 */
package com.mx.edifact.ws;

import com.mx.edifact.config.DataSourceConfig;
import com.mx.edifact.controller.CancelaComprobanteController;
import com.mx.edifact.controller.CreaComprobanteController;
import com.mx.edifact.dto.AddendaEmisor;
import com.mx.edifact.dto.CancelaDto;
import com.mx.edifact.dto.ResponceWs;
import com.mx.edifact.dto.ResponceWsCancelacion;
import com.mx.edifact.service.ParametrosService;
import com.mx.edifact.service.UsuarioWSService;
import com.mx.edifact.utils.Output;
import com.mx.edifact.utils.Utils;
import java.io.IOException;
import java.time.LocalDate;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import org.apache.axis.encoding.Base64;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author germa
 */
@WebService(serviceName = "EdifactMx")
public class EdifactMx {

    private static final Logger log = LogManager.getLogger(EdifactMx.class);
    private CreaComprobanteController comprobanteController = new CreaComprobanteController();
    private CancelaComprobanteController cancelaComprobanteController = new CancelaComprobanteController();
    private UsuarioWSService usuarioWSService;

    /**
     * This is a sample web service operation
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @return
     */
    @WebMethod(operationName = "facturaCFDI")
    public ResponceWs facturaCFDI(@WebParam(name = "arg0") String arg0, @WebParam(name = "arg1") String arg1,
            @WebParam(name = "arg2") String arg2, @WebParam(name = "arg3") String arg3) {
        usuarioWSService = new UsuarioWSService();
        if (Utils.jdbcTemplate == null) {
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            try {
                Utils.jdbcTemplate = new JdbcTemplate(dataSourceConfig.dataSource());
            } catch (IOException e) {
                log.error("Error :: ", e);
                return this.creaRespuesta("WS101", "Error al conectarse a la B.D", null, null, null, null, null, null, null);
            }
        }
        try {
            ParametrosService parametrosService = new ParametrosService();
            parametrosService.cargarParametros();
        } catch (Exception e) {
            log.error("Error :: ", e);
            return this.creaRespuesta("WS102", "Error al conectarse a la B.D", null, null, null, null, null, null, null);
        }
        if (arg0 == null || arg0.equals("")) {
            return creaRespuesta("W201", "El parámetro arg0 es requerido.", null, null, null, null, null, null, null);
        }
        if (arg1 == null || arg1.equals("")) {
            return creaRespuesta("W202", "El parámetro arg1 es requerido.", null, null, null, null, null, null, null);
        }
        if (arg2 == null || arg2.equals("")) {
            return creaRespuesta("W203", "El parámetro arg2 es requerido.", null, null, null, null, null, null, null);
        }
        if (arg3 == null || arg3.equals("")) {
            return creaRespuesta("W204", "El parámetro arg3 es requerido.", null, null, null, null, null, null, null);
        }
        if (!arg2.equals("piovan") && !arg3.equals("pQ5YJ9CDkygBCD@M")) {
//        if (!arg2.equals("pruebas") && !arg3.equals("t3st2022")) {
            log.error("Credenciales invalidas.");
            return creaRespuesta("W205", "Credenciales invalidas.", null, null, null, null, null, null, null);
        }
//        try {
//            Boolean keepDeclaration = Boolean.valueOf(Base64Coder.decodeString(arg0).startsWith("<?xml"));
//            if (!keepDeclaration) {
//                return creaRespuesta("W206", "El archivo enviado en el arg0 no tiene formato de xml.", null, null, null, null, null, null, null);
//            }
//        } catch (Exception e) {
//            log.error("Error al validar si el arg0 tiene formato de xml :: ", e);
//            return creaRespuesta("W206", "El archivo enviado en el arg0 no tiene formato de xml.", null, null, null, null, null, null, null);
//        }
        try {
//                return creaRespuestaDomi("100", "Cfdi timbrado correctamente", "", Utils.xmlDomi(), "", "", "","","");
//            String xml = Base64Coder.decodeString(arg0).replaceAll("&", "&amp;");
            String xml = "";
            byte[] ptext = Base64.decode(arg0);
//            byte[] ptext = xml.getBytes("ISO-8859-1");
            xml = new String(ptext, "UTF-8");
//            Comprobante comprobante = UtilsJaxb.parseXML(xml);
            xml = xml.replaceAll("&amp;#xA;", "");
            String fecha = Utils.getCurrentDateToString();
            String[] tempFecha = fecha.split("T");
            Output.save(xml, Utils.getParametro("path_fglobal_procesa") + LocalDate.now(), arg1
                    + "_" + tempFecha[1].replaceAll(":", "") + ".xml");
            AddendaEmisor addendaEmisor = null;
            String AddendaEU = "";
            String addendaViscofan = "";
            String temp[] = xml.split("<cfdi:Addenda>");
            if (xml.contains("<cfdi:Addenda>")) {
                xml = temp[0] + "</cfdi:Comprobante>";
                try {
                    String addenda = "";
                    String temp2[] = temp[1].split("</cfdi:Addenda>");
                    if (temp2[0].contains("<cfdi:AddendaEU>")) {
                        String[] tempAddendaEU = temp2[0].split("<cfdi:AddendaEU>");
                        AddendaEU = "<cfdi:AddendaEU>" + tempAddendaEU[1];
                        addenda = tempAddendaEU[0].replaceAll("cfdi:", "");
                    } else if (temp2[0].contains("<cfdi:AddendaViscofan")) {
                        String[] tempAddendaViscofan = temp2[0].split("<cfdi:AddendaViscofan");
                        addendaViscofan = "<cfdi:AddendaViscofan" + tempAddendaViscofan[1];
                        addenda = tempAddendaViscofan[0].replaceAll("cfdi:", "");
                    } else {
                        addenda = temp2[0].replaceAll("cfdi:", "");
                    }
                    addendaEmisor = Utils.creaObjetAddendaInterna(Utils.loadXMLFromString(addenda));
                } catch (Exception e) {
                    log.error(e);
                }
            }
//            Comprobante comprobante = Utils.loadXMLFromString2(Utils.loadXMLFromString(xml));

            return comprobanteController.procesoTimbrado(xml, addendaEmisor, AddendaEU, addendaViscofan);
        } catch (Exception e) {
            log.error("Error al timbrar el documento :: ", e);
            return creaRespuesta("000", "Error interno.", null, null, null, null, null, null, null);
        }
    }

    /**
     * Web service operation
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @return
     */
    @WebMethod(operationName = "cancelaComprobante")
    public ResponceWsCancelacion cancelaComprobante(@WebParam(name = "arg0") String arg0, @WebParam(name = "arg1") String arg1,
            @WebParam(name = "arg2") String arg2, @WebParam(name = "arg3") String arg3) {
        if (Utils.jdbcTemplate == null) {
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            try {
                Utils.jdbcTemplate = new JdbcTemplate(dataSourceConfig.dataSource());
            } catch (IOException e) {
                log.error("Error :: ", e);
                return creaRespuestaCancelacion("WS101", "Error al conectarse a la B.D", null, null, null, null);
            }
        }
        try {
            ParametrosService parametrosService = new ParametrosService();
            parametrosService.cargarParametros();
        } catch (Exception e) {
            log.error("Error :: ", e);
            return creaRespuestaCancelacion("WS102", "Error al conectarse a la B.D", null, null, null, null);
        }
        if (arg0 == null || arg0.equals("")) {
            return creaRespuestaCancelacion("W401", "El parámetro arg0 es requerido.", null, null, null, null);
        }
        if (arg1 == null || arg1.equals("")) {
            return creaRespuestaCancelacion("W402", "El parámetro arg1 es requerido.", null, null, null, null);
        }
        if (arg2 == null || arg2.equals("")) {
            return creaRespuestaCancelacion("W403", "El parámetro arg2 es requerido.", null, null, null, null);
        }
        if (arg3 == null || arg3.equals("")) {
            return creaRespuestaCancelacion("W404", "El parámetro arg3 es requerido.", null, null, null, null);
        }
        if (!arg2.equals("piovan") && !arg3.equals("pQ5YJ9CDkygBCD@M")) {
//        if (!arg2.equals("pruebas") && !arg3.equals("t3st2022")) {
            log.error("Credenciales invalidas.");
            return creaRespuestaCancelacion("W405", "Credenciales invalidas.", null, null, null, null);
        }
        try {
            byte[] ptext = Base64.decode(arg0);
            String xmlCancel = new String(ptext, "UTF-8");
            CancelaDto cancelaDto = Utils.creaObjetCancelaDto(Utils.loadXMLFromString(xmlCancel));
            Output.save(xmlCancel, Utils.getParametro("path_fglobal_procesa_cancel") + LocalDate.now(),
                    cancelaDto.getUuid() + ".xml");
            return cancelaComprobanteController.cancelarCFDI(xmlCancel, arg1);
        } catch (Exception e) {
            log.error("Error al cancelar e CFDI :: ", e);
        }
        return creaRespuestaCancelacion("000", "Error interno.", null, null, null, null);
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

    private ResponceWsCancelacion creaRespuestaCancelacion(String codigo, String descripcion, String cancelable,
            String estadoPeticion, String estatus, String uuid) {
        ResponceWsCancelacion responceWsCancelacion = new ResponceWsCancelacion();
        responceWsCancelacion.setCodigo(codigo);
        responceWsCancelacion.setDescripcion(descripcion);
        responceWsCancelacion.setCancelable(cancelable);
        responceWsCancelacion.setEstadoPeticion(estadoPeticion);
        responceWsCancelacion.setEstatus(estatus);
        responceWsCancelacion.setUuid(uuid);
        return responceWsCancelacion;
    }
}
