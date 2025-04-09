package com.mx.edifact.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mx.edifact.dto.RespuestaTimbrado;
import com.mx.edifact.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimbrarControllerImpl {

    private static final Logger log = LogManager.getLogger(TimbrarControllerImpl.class);

    public RespuestaTimbrado timbrarComprobante(String docXML) {
        RespuestaTimbrado respuestaTimbrado = null;
        try {
            String request = Utils.requestTimbrado().replaceAll("<documentoXML xsi:type=\"xsd:string\"></documentoXML>",
                    "<documentoXML xsi:type=\"xsd:string\">" + docXML + "</documentoXML>");
            URL url = null;
            if (Utils.getParametro("ambiente_timbrado").equals("produccion")) {
                url = new URL("https://www.edifactmx-pac.com/serviceCFDI4/timbraCFDI40.php?wsdl");
            } else {
                url = new URL("https://www.comprobantes-fiscales.com/serviceCFDI4/timbraCFDI.php?wsdl");
            }
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "text/xml; charset=ISO-8859-1");
            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(request.length()));
            httpURLConnection.getOutputStream().write(request.getBytes());
            httpURLConnection.getOutputStream().flush();
            httpURLConnection.connect();
            String responce;
            if (httpURLConnection.getResponseCode() != 200) {
                log.error("Error al consumir el servicio de timbrado ::" + httpURLConnection.getResponseCode() + " "
                        + httpURLConnection.getResponseMessage());
                respuestaTimbrado = new RespuestaTimbrado();
                respuestaTimbrado.setCodigoDescripcion("000");
                respuestaTimbrado.setCodigoDescripcion("Error interno");
                return respuestaTimbrado;
            } else {
                responce = Utils.generarResponse(httpURLConnection);
            }
            /*
			 * // -----------Escribe el Request--------------- if (new
			 * File("D:/German/sat/log/Request.txt").exists()) { new
			 * File("D:/German/sat/log/Request.txt").delete(); } for (String key :
			 * httpURLConnection.getRequestProperties().keySet()) { if
			 * (!key.equals("Accept") && !key.equals("Connection")) { escribeRequest(key +
			 * ": " + httpURLConnection.getRequestProperties().get(key)); } }
			 * escribeRequest(""); escribeRequest(request.replaceAll("\n",
			 * "").replaceAll("\r", "")); // ---------------------------------------------
			 * // -----------Escribe el Responce--------------- if (new
			 * File("D:/German/sat/log/Responce.txt").exists()) { new
			 * File("D:/German/sat/log/Responce.txt").delete(); } for (Map.Entry<String,
			 * List<String>> entries : httpURLConnection.getHeaderFields().entrySet()) {
			 * String values = ""; for (String value : entries.getValue()) { values +=
			 * value; } escribeResponce(entries.getKey() + ": " + values); }
			 * escribeResponce(""); escribeResponce(responce); //
			 * ---------------------------------------------
             */
            httpURLConnection.disconnect();
            respuestaTimbrado = creaRespuesta(responce);
            if (respuestaTimbrado.getCodigoResultado().equals("307")) {
                log.info("Hay un timbre prebio esta buscando la informacion");
                request = Utils.requestTimbrado().replaceAll("<documentoXML xsi:type=\"xsd:string\"></documentoXML>",
                        "<documentoXML xsi:type=\"xsd:string\">" + docXML + "</documentoXML>");
//				if (Utils.getParametro("ambiente_timbrado").equals("produccion")) {
//					url = new URL("https://www.edifactmx-pac.com/serviceCFDI/regresaCFDIU.php?wsdl"); // Recupera
//				} else {
                if (Utils.getParametro("ambiente_timbrado").equals("produccion")) {
                    url = new URL("https://www.edifactmx-pac.com/serviceCFDI4/regresaCFDIU.php?wsdl");
                } else {
                    url = new URL("https://comprobantes-fiscales.com/serviceCFDI4/regresaCFDIU.php?wsdl");
                }
//				}
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "text/xml; charset=ISO-8859-1");
                httpURLConnection.setRequestProperty("Content-Length", Integer.toString(request.length()));
                httpURLConnection.getOutputStream().write(request.getBytes());
                httpURLConnection.getOutputStream().flush();
                httpURLConnection.connect();
                responce = Utils.generarResponse(httpURLConnection);
                httpURLConnection.disconnect();
                respuestaTimbrado = creaRespuesta(responce);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuestaTimbrado;
    }

    private RespuestaTimbrado creaRespuesta(String responce) {
        RespuestaTimbrado respuestaTimbrado = new RespuestaTimbrado();
        String[] tempCodigo = responce.split("<codigoResultado xsi:type=\"xsd:string\">");
        String[] tempCodigo2 = tempCodigo[1].split("</codigoResultado>");
        respuestaTimbrado.setCodigoResultado(tempCodigo2[0]);
        String[] tempDescripcion = responce.split("<codigoDescripcion xsi:type=\"xsd:string\">");
        String[] tempDescripcion2 = tempDescripcion[1].split("</codigoDescripcion>");
        respuestaTimbrado.setCodigoDescripcion(tempDescripcion2[0]);
        String[] tempDocumento = responce.split("<documentoTimbrado xsi:type=\"xsd:string\">");
        String[] tempDocumento2 = tempDocumento[1].split("</documentoTimbrado>");
        respuestaTimbrado.setDocumentoTimbrado(tempDocumento2[0]);
        return respuestaTimbrado;
    }

//	private void escribeRequest(String mensaje) {
//		mensaje = mensaje.replaceAll("\\[", "");
//		mensaje = mensaje.replaceAll("\\]", "");
//		mensaje = mensaje.replaceAll(": null", "");
//		mensaje = mensaje.replaceAll("null: ", "");
//		try {
//			FileWriter fstream;
//			fstream = new FileWriter("D:/German/sat/log/Request.txt", true);
//			try (BufferedWriter out = new BufferedWriter(fstream)) {
//				out.write(mensaje + "\r\n");
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//
//	}
//	private void escribeResponce(String mensaje) {
//		mensaje = mensaje.replaceAll("\\[", "");
//		mensaje = mensaje.replaceAll("\\]", "");
//		mensaje = mensaje.replaceAll(": null", "");
//		mensaje = mensaje.replaceAll("null: ", "");
//		try {
//			FileWriter fstream;
//			fstream = new FileWriter("D:/German/sat/log/Responce.txt", true);
//			try (BufferedWriter out = new BufferedWriter(fstream)) {
//				out.write(mensaje + "\r\n");
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//
//	}
}
