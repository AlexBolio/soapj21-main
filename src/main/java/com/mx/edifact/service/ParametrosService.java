package com.mx.edifact.service;

import java.util.ArrayList;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mx.edifact.model.CfdiParametros;
import com.mx.edifact.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class ParametrosService {

    private static final Logger log = LogManager.getLogger(ParametrosService.class);

    private JdbcTemplate jdbcTemplate;

    public void cargarParametros() {
        try {
            jdbcTemplate = Utils.jdbcTemplate;
        } catch (Exception e1) {
            log.error("Error ::", e1);
        }
        try {
            String sql = "select * from cfdi_parametros";
            Utils.listParametros = (ArrayList<CfdiParametros>) jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<CfdiParametros>(CfdiParametros.class));
        } catch (Exception e) {
            log.error("Error al ejecutar selectParametros :: ", e);
        }
    }

}
