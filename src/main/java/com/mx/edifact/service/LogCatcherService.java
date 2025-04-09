package com.mx.edifact.service;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mx.edifact.model.LogCatcher;
import com.mx.edifact.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

@Service
public class LogCatcherService {

    private static final Logger log = LogManager.getLogger(LogCatcherService.class);

    private JdbcTemplate jdbcTemplate;

    @SuppressWarnings("deprecation")
    public void insertar(LogCatcher logCatcher) throws SQLException {
        jdbcTemplate = Utils.jdbcTemplate;
        try {
            String sql = "SELECT count(*) FROM logcatcher WHERE serie = ? and folio = ?";
            if (jdbcTemplate.queryForObject(sql, new Object[]{logCatcher.getSerie(), logCatcher.getFolio()},
                    Boolean.class)) {
                sql = "UPDATE logcatcher SET moment = ?, message = ?, code = ?, folio = ?, serie = ?, uuid = ?, enviado = ?,  "
                        + "emails = ? where " + "serie = ? and folio = ?";
                jdbcTemplate.update(sql, logCatcher.getMoment(), logCatcher.getMessage(), logCatcher.getCode(),
                        logCatcher.getFolio(), logCatcher.getSerie(), logCatcher.getUuid(), logCatcher.getEnviado(),
                        logCatcher.getEmails(), logCatcher.getSerie(), logCatcher.getFolio());
            } else {
                sql = "INSERT INTO logcatcher (moment,project,job,message,code,folio,serie,uuid,enviado,emails,rfc,rfc_receptor) "
                        + "VALUES " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(sql, logCatcher.getMoment(), logCatcher.getProject(), logCatcher.getJob(),
                        logCatcher.getMessage(), logCatcher.getCode(), logCatcher.getFolio(), logCatcher.getSerie(),
                        logCatcher.getUuid(), logCatcher.getEnviado(), logCatcher.getEmails(), logCatcher.getRfc(),
                        logCatcher.getRfcReceptor());
            }
        } catch (DataAccessException e) {
            log.error("Error al ejecutar insertCfdis :: ", e);
        }
    }

}
