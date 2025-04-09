/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.edifact.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author RobinsonGarcia
 */
public class Output {
    
    public static void moveTo(String source, String destination){
        try{
            File fileSource = new File(source);
            File fileDestination = new File(destination);
            FileInputStream input = new FileInputStream(fileSource);
            FileOutputStream output = new FileOutputStream(fileDestination);
            byte[] bytes = new byte[1000];
            int leidos;
            while((leidos = input.read(bytes)) > 0){
                output.write(bytes, 0, leidos);
            }
            input.close();
            output.close();
        } catch(IOException ex){
        }
    }
    
    public static void delete(String delete){
        new File(delete).delete();
    }
    
    public static void save(String contenido, String ruta, String nombre) {
        try {
            //Archivo destino
            File fileDestination = new File(ruta);
            if (!fileDestination.exists())
                fileDestination.mkdirs();
            fileDestination = new File(ruta + "/" + nombre);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileDestination), "UTF-8");
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(contenido)));
            doc.setXmlStandalone(true);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource origen = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(origen, result);
            
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            Logger.getLogger(Output.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void save(InputStream input, String destination, String nameXML){
        try{
            File fileDestination = new File(destination);
            if (!fileDestination.exists())
                fileDestination.mkdirs();
            fileDestination = new File(destination + "/" + nameXML);
            FileOutputStream output = new FileOutputStream(fileDestination);
            byte[] bytes = new byte[1000];
            int leidos;
            while((leidos = input.read(bytes)) > 0){
                output.write(bytes, 0, leidos);
            }
            input.close();
            output.close();
        } catch(IOException ex){
        }
    }
    
    public static void makedirs(String dir){
        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
    }
}
