/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mx.edifact.service;

import com.mx.edifact.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author germa
 */
public class UsuarioWSService {

    private static final Logger log = LogManager.getLogger(UsuarioWSService.class);

    private JdbcTemplate jdbcTemplate;

    public boolean buscarUsuario(String user, String password) {
        try {
            jdbcTemplate = Utils.jdbcTemplate;
        } catch (Exception e1) {
            log.error("Error ::", e1);
        }
        boolean retorno = false;
        try {
            String sql = "SELECT count(*) FROM usuario_ws WHERE user = ? and password = ?";
            retorno = jdbcTemplate.queryForObject(sql, new Object[]{user, password}, Boolean.class);
        } catch (DataAccessException e) {
            log.error("Error al ejecutar selectCountCfdis :: ", e);
            return retorno;
        }
        return retorno;
    }
}
