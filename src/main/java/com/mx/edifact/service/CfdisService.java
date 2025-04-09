package com.mx.edifact.service;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mx.edifact.consultaSat.acuse.Acuse;
import com.mx.edifact.model.CfdiCancel;
import com.mx.edifact.model.Cfdis;
import com.mx.edifact.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CfdisService {

    private static final Logger log = LogManager.getLogger(CfdisService.class);

    private JdbcTemplate jdbcTemplate;

    public void insertar(Cfdis cfdis) {
        try {
            jdbcTemplate = Utils.jdbcTemplate;
        } catch (Exception e1) {
            log.error("Error ::", e1);
        }
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String fechaTimbrado = hourdateFormat.format(cfdis.getFechaTimbrado());
        try {
            String sql = "INSERT INTO cfdis (version, serie, folio, fecha, sub_total, total, tipo_comprobante, xml, fecha_timbrado, estatus,"
                    + "uuid, rfc_emisor, rfc_receptor, nombre_receptor, pago_generado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, cfdis.getVersion(), cfdis.getSerie(), cfdis.getFolio(), cfdis.getFecha(),
                    cfdis.getSubTotal(), cfdis.getTotal(), cfdis.getTipoComprobante(), cfdis.getXml(), fechaTimbrado,
                    cfdis.getEstatus(), cfdis.getUuid(), cfdis.getRfcEmisor(), cfdis.getRfcReceptor(),
                    cfdis.getNombreReceptor(), 0);
        } catch (Exception e) {
            log.error("Error al ejecutar insertCfdis :: ", e);
        }
    }

    public void updateCfdisEstatus(String estatus, String uuid) {
        try {
            jdbcTemplate = Utils.jdbcTemplate;
        } catch (Exception e1) {
            log.error("Error ::", e1);
        }
        try {
            String sql = "UPDATE cfdis SET estatus = ? WHERE uuid=?";
            jdbcTemplate.update(sql, estatus, uuid);
        } catch (Exception e) {
            log.error("Error ::", e);
        }
    }

    @SuppressWarnings("deprecation")
    public boolean buscarUuid(String uuid) throws ParseException, SQLException {
        try {
            jdbcTemplate = Utils.jdbcTemplate;
        } catch (Exception e1) {
            log.error("Error ::", e1);
        }
        boolean retorno = false;
        try {
            String sql = "SELECT count(*) FROM cfdis WHERE uuid = ?";
            retorno = jdbcTemplate.queryForObject(sql, new Object[]{uuid}, Boolean.class);
        } catch (Exception e) {
            log.error("Error al ejecutar selectCountCfdis :: ", e);
            return retorno;
        }
        return retorno;
    }

    @SuppressWarnings("deprecation")
    public void insertCfdiCancela(String uuid, Acuse acuse, String expresionImpresa) {
        jdbcTemplate = Utils.jdbcTemplate;
        List<CfdiCancel> list = new ArrayList<CfdiCancel>();
        String fecha = "";
        fecha = Utils.getCurrentDateToString();
        try {
            String querySelect = "SELECT * FROM cfdis_cancel where uuid = ?";
            list = (ArrayList<CfdiCancel>) jdbcTemplate.query(querySelect, new Object[]{uuid},
                    new BeanPropertyRowMapper<CfdiCancel>(CfdiCancel.class));
            if (list.isEmpty()) {
                String sql = "";
                if (acuse.getEstatusCancelacion().equalsIgnoreCase("Solicitud rechazada")) {
                    sql = "INSERT INTO cfdis_cancel (uuid, REQUEST_CFDI, CodigoEstatus, EsCancelable, Estado, EstatusCancelacion,"
                            + "fechaRechazo) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(sql, uuid, expresionImpresa, acuse.getCodigoEstatus(), acuse.getEsCancelable(),
                            acuse.getEstado(), acuse.getEstatusCancelacion(), fecha);
                } else if (acuse.getEstado().equalsIgnoreCase("Cancelado")) {
                    sql = "INSERT INTO cfdis_cancel (uuid, REQUEST_CFDI, CodigoEstatus, EsCancelable, Estado, EstatusCancelacion,"
                            + "fechaCancelado) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(sql, uuid, expresionImpresa, acuse.getCodigoEstatus(), acuse.getEsCancelable(),
                            acuse.getEstado(), acuse.getEstatusCancelacion(), fecha);
                } else if (acuse.getEsCancelable().equalsIgnoreCase("No Cancelable")) {
                    sql = "INSERT INTO cfdis_cancel (uuid, REQUEST_CFDI, CodigoEstatus, EsCancelable, Estado) "
                            + "VALUES (?, ?, ?, ?, ?)";
                    jdbcTemplate.update(sql, uuid, expresionImpresa, acuse.getCodigoEstatus(), acuse.getEsCancelable(),
                            acuse.getEstado());
                } else {
                    sql = "INSERT INTO cfdis_cancel (uuid, REQUEST_CFDI, CodigoEstatus, EsCancelable, Estado, EstatusCancelacion) "
                            + "VALUES (?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(sql, uuid, expresionImpresa, acuse.getCodigoEstatus(), acuse.getEsCancelable(),
                            acuse.getEstado(), acuse.getEstatusCancelacion());
                }
            } else {
                String sql = "";
                if (acuse.getEstatusCancelacion().equalsIgnoreCase("Solicitud rechazada")) {
                    sql = "UPDATE cfdis_cancel SET REQUEST_CFDI = ?, CodigoEstatus = ?, EsCancelable = ?, EstatusCancelacion = ?, "
                            + "fechaRechazo = ? WHERE uuid=?";
                    jdbcTemplate.update(sql, expresionImpresa, acuse.getCodigoEstatus(), acuse.getEsCancelable(),
                            acuse.getEstatusCancelacion(), uuid);
                } else if (acuse.getEstado().equalsIgnoreCase("Cancelado")) {
                    sql = "UPDATE cfdis_cancel SET REQUEST_CFDI = ?, CodigoEstatus = ?, EsCancelable = ?, Estado = ?, EstatusCancelacion = ?,"
                            + " fechaCancelado = ? WHERE uuid= ?";
                    jdbcTemplate.update(sql, expresionImpresa, acuse.getCodigoEstatus(), acuse.getEsCancelable(),
                            acuse.getEstado(), acuse.getEstatusCancelacion(), fecha, uuid);
                }
            }
        } catch (Exception e) {
            log.error("Error al ejecutar insertCfdis :: ", e);
        }
    }
}
