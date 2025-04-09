/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mx.edifact.controller;

import com.mx.edifact.consultaSat.ConsultaCFDIServiceLocator;
import com.mx.edifact.consultaSat.IConsultaCFDIService;
import com.mx.edifact.consultaSat.acuse.Acuse;
import com.mx.edifact.dto.CancelaDto;
import com.mx.edifact.dto.ResponceWsCancelacion;
import com.mx.edifact.model.CfdiRespaldo;
import com.mx.edifact.model.ReturnCancelaCFDI;
import com.mx.edifact.service.CfdisService;
import com.mx.edifact.service.RespaldoService;
import com.mx.edifact.utils.DefaultTrustManager;
import com.mx.edifact.utils.Utils;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author germa
 */
public class CancelaComprobanteController {

    private static final Logger log = LogManager.getLogger(CancelaComprobanteController.class);
    private Acuse acuse = new Acuse();

    public ResponceWsCancelacion cancelarCFDI(String inbound, String rfc) {
        CancelaDto cancelaDto = null;
        ReturnCancelaCFDI response = new ReturnCancelaCFDI();
        CfdiRespaldo cfdiRespaldo = null;
        RespaldoService respaldoService = new RespaldoService();
        try {
            cancelaDto = Utils.creaObjetCancelaDto(Utils.loadXMLFromString(inbound));
            try {
                consultarCFDI(cancelaDto);
                if (acuse.getEstado().equalsIgnoreCase("No Encontrado")) {
                    log.error("CFDI No Encontrado.");
                    return creaRespuestaCancelacion("300", "", acuse.getEsCancelable(),
                            acuse.getEstatusCancelacion(), acuse.getEstado(), cancelaDto.getUuid());
                } else {
                    CfdisService cfdisService = new CfdisService();
                    cfdiRespaldo = respaldoService.consultarPassword(rfc);
                    CancelaCFDIController cancela = new CancelaCFDIController();
                    String respuesta;
                    respuesta = cancela.firmarXML(cancelaDto.getRfcEmisor(), Utils.getParametro("path_files_key_cert"),
                            Utils.quitarAlias(cfdiRespaldo.getCfdi_tool(), "XMzDdG4D03CKm2IxIWQw7g=="), cancelaDto.getUuid(),
                            Utils.getCurrentDateToString(), Utils.getParametro("ambiente_timbrado"), cancelaDto.getMotivo(),
                            cancelaDto.getFolioSustitucion());
                    response = cancela.validarRespuestaCancelacion(respuesta);
                    if (response.getCode().equals("201") || response.getCode().equals("202")) {
                        log.info(cancelaDto.getUuid() + " Cancelado correctamente.");
                        // cancelaDto.setEstatus("E");
                        cfdisService.updateCfdisEstatus("E", cancelaDto.getUuid());
//                        administraDatosDao.actualizaEstatus(cancelaDto.getUuid(), "E");
                        Utils.writeXMLFileDate(respuesta,
                                Utils.getParametro("path_cfdi_cancel") + cancelaDto.getUuid() + ".xml",
                                Utils.getCurrentDateToString());
                        consultarCFDI(cancelaDto);
                    } else {
                        log.error("Error al Cancelar el CFDI " + cancelaDto.getUuid() + " " + response.getCode() + " "
                                + response.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Error al Cancelar :: ", e);
                return creaRespuestaCancelacion("000", "Error al cancelar el documento", null, null, null, null);
            }
        } catch (Exception e) {
            log.error("Error al parsear el inbound :: ", e);
            return creaRespuestaCancelacion("000", "Error al parsear el inbound", null, null, null, null);
        }
        return creaRespuestaCancelacion(response.getCode(), response.getMessage(), acuse.getEsCancelable(),
                acuse.getEstatusCancelacion(), acuse.getEstado(), cancelaDto.getUuid());
    }

    public void consultarCFDI(CancelaDto cancelaDto) throws Exception {
        CfdisService cfdisService = new CfdisService();
        try {
            String expresionImpresa = Utils.getexpresionImpresa2(cancelaDto);
            SSLContext ctx = SSLContext.getInstance("SSL");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);
            ConsultaCFDIServiceLocator serviceLocator = new ConsultaCFDIServiceLocator(Utils.getParametro("ambiente_timbrado"));
            IConsultaCFDIService consultaCFDIService = serviceLocator.getBasicHttpBinding_IConsultaCFDIService();
            acuse = consultaCFDIService.consulta(expresionImpresa, Utils.getParametro("ambiente_timbrado"));
            log.info("CodigoEstatus: " + acuse.getCodigoEstatus());
            log.info("EsCancelable: " + acuse.getEsCancelable());
            log.info("Estado: " + acuse.getEstado());
            log.info("EstatusCancelacion: " + acuse.getEstatusCancelacion());
            try {
                cfdisService.insertCfdiCancela(cancelaDto.getUuid(), acuse, expresionImpresa);
//                administraDatosDao.insertCfdiCancel(acuse, expresionImpresa, cancelaDto.getUuid());
            } catch (Exception e) {
                log.error("Error al insertar cfdiCancel :: " + e);
                throw new Exception("Error al insertar cfdiCancel.");
            }
//            if (!acuse.getEsCancelable().equalsIgnoreCase("No Cancelable")) {
            if (acuse.getEstado().equalsIgnoreCase("Cancelado")) {
                try {
                    cfdisService.updateCfdisEstatus("C", cancelaDto.getUuid());
//                        administraDatosDao.actualizaEstatus(cancelaDto.getUuid(), "C");
                } catch (Exception e) {
                    log.error("Error al actualizar el cfdi :: " + cancelaDto.getUuid());
                    throw new Exception("Error al actualizar el cfdi :: " + cancelaDto.getUuid());
                }
            } else if (acuse.getEstado().equalsIgnoreCase("Vigente")) {
                if (acuse.getEstatusCancelacion().equalsIgnoreCase("Solicitud rechazada")) {
                    try {
                        cfdisService.updateCfdisEstatus("R", cancelaDto.getUuid());
//                            administraDatosDao.actualizaEstatus(cancelaDto.getUuid(), "R");
                    } catch (Exception e) {
                        log.error("Error al actualizar el cfdi :: " + cancelaDto.getUuid());
                        throw new Exception("Error al actualizar el cfdi :: " + cancelaDto.getUuid());
                    }
                }
            }
//            } else {
//                try {
//                    cfdisService.updateCfdisEstatus("C", cancelaDto.getUuid());
//                    administraDatosDao.actualizaEstatus(cancelaDto.getUuid(), "V");
//                } catch (Exception e) {
//                    log.error("Error al actualizar el cfdi :: " + cancelaDto.getUuid());
//                    throw new Exception("Error al actualizar el cfdi :: " + cancelaDto.getUuid());
//                }
//            }
        } catch (Exception e) {
            log.error("Error al consultar el estatus en el SAT :: ", e);
            throw new Exception("Error al consultar el estatus en el SAT.");
        }
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
