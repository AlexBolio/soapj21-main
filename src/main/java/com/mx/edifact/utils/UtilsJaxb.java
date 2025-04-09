/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.edifact.utils;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import mx.gob.sat.cfd4_0.Comprobante;
import comercioexterior20.ComercioExterior;
import mx.gob.sat.pagos20.Pagos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author German
 */
public class UtilsJaxb {

    private static final Logger log = LogManager.getLogger(UtilsJaxb.class);

    public static Comprobante parseXML(final String xml) {
        Comprobante comprobante = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Comprobante.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            comprobante = (Comprobante) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            log.error("Error :: ", e);
        }
        return comprobante;
    }

    public static Pagos parsePagos(final String xml){
         Pagos pagos = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Pagos.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            pagos = (Pagos) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pagos;
    }
    public static ComercioExterior parseCCE(final String xml){
         ComercioExterior cce = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ComercioExterior.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            cce = (ComercioExterior) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cce;
    }
    
    public static String objectToXml(Comprobante comprobante) throws FileNotFoundException {
        String retorno = "";
        try {
            JAXBContext context = JAXBContext.newInstance(Comprobante.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            m.marshal(comprobante, sw);
            retorno = sw.toString();
        } catch (JAXBException e) {
            log.error("Error :: ", e);
        }
        return retorno;
    }

}
