package com.mx.edifact.service;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mx.edifact.model.CfdiRespaldo;
import com.mx.edifact.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class RespaldoService {

    private static final Logger log = LogManager.getLogger(RespaldoService.class);

    private JdbcTemplate jdbcTemplate;

    @SuppressWarnings("deprecation")
    public CfdiRespaldo consultarPassword(String rfc) {
        try {
            jdbcTemplate = Utils.jdbcTemplate;
        } catch (Exception e1) {
            log.error("Error ::", e1);
        }
        CfdiRespaldo cfdiRespaldo = null;
        try {
            String sql = "select * from cfdi_respaldo where cfdi_rfc = ?";
            cfdiRespaldo = (CfdiRespaldo) jdbcTemplate.queryForObject(sql, new Object[]{rfc},
                    new BeanPropertyRowMapper<CfdiRespaldo>(CfdiRespaldo.class));
        } catch (Exception e) {
            log.error("Error al ejecutar selectCfdiRespaldo :: ", e);
            return cfdiRespaldo;
        }
        return cfdiRespaldo;
    }

}
