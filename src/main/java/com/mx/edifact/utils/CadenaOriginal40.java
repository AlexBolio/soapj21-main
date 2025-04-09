
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.edifact.utils;

import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ITIC Roger Azcorra Novelo [RJAN]
 */
public class CadenaOriginal40 {

    private static final Logger log = LogManager.getLogger(CadenaOriginal40.class);

    private Templates cachedXSLT;

    private CadenaOriginal40() {
        try {
            Source xsltSource = new StreamSource(new File(Utils.getParametro("path_files_xsd_xslt") + "cadenaoriginal_4_0.xslt").getAbsoluteFile());
            TransformerFactory transFact = TransformerFactory.newInstance();
            cachedXSLT = transFact.newTemplates(xsltSource);
        } catch (TransformerConfigurationException e) {
            log.error("Error ::", e);
        }
    }

    public static CadenaOriginal40 getInstance() {
        return CadenaOriginal32Holder.INSTANCE;
    }

    private static class CadenaOriginal32Holder {

        private static final CadenaOriginal40 INSTANCE = new CadenaOriginal40();
    }

    public Templates getCachedXSLT() {
        return cachedXSLT;
    }
}
