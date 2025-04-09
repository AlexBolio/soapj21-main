package com.mx.edifact.utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.mx.edifact.dto.AddendaEmisor;
import com.mx.edifact.dto.CancelaDto;
import com.mx.edifact.dto.DomicilioEmisor;
import com.mx.edifact.dto.DomicilioReceptor;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mx.edifact.model.CfdiParametros;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Random;
import jakarta.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mx.com.edifact.add.Extra2CFDI;
import mx.com.edifact.xml.Serializer;
import mx.gob.sat.cfd4_0.Comprobante;
import mx.gob.sat.cfd4_0.Comprobante.Complemento;
import mx.gob.sat.cfd4_0.ObjectFactory;
import comercioexterior20.ComercioExterior;
import mx.gob.sat.pagos20.Pagos;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

public class Utils {

    private static final Logger log = LogManager.getLogger(Utils.class);
    public static JdbcTemplate jdbcTemplate;
    public static ArrayList<CfdiParametros> listParametros = new ArrayList<CfdiParametros>();

    public static Document docTest() {
        Document docAddendaAirbus = null;
        try {
            String namespace = "urn:cliente.com";
            String prefix = "ns1";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // Elemento raíz
            docAddendaAirbus = docBuilder.newDocument();
            // Primer elemento
            Element elementAirbus = docAddendaAirbus.createElementNS(namespace, "TestConnection_Rs");
            elementAirbus.setPrefix(prefix);
            docAddendaAirbus.appendChild(elementAirbus);
            // Se agrega un atributo al nodo elemento y su valor
            Element elemento2 = docAddendaAirbus.createElement("Code");
            elemento2.setTextContent("200");
            elementAirbus.appendChild(elemento2);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return docAddendaAirbus;
    }

    public static Document docNotification() {
        Document docAddendaAirbus = null;
        try {
            String namespace = "urn:cliente.com";
            String prefix = "ns1";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // Elemento raíz
            docAddendaAirbus = docBuilder.newDocument();
            // Primer elemento
            Element elementAirbus = docAddendaAirbus.createElementNS(namespace, "SendNotification_Rs");
            elementAirbus.setPrefix(prefix);
            docAddendaAirbus.appendChild(elementAirbus);
            // Se agrega un atributo al nodo elemento y su valor
            Element elemento2 = docAddendaAirbus.createElement("Code");
            elemento2.setTextContent("200");
            elementAirbus.appendChild(elemento2);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return docAddendaAirbus;
    }

    public static Document loadXMLFromString(String xml) throws SAXException, IOException, Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

    }

    public static XMLGregorianCalendar stringXMLGregorian(Date fecha)
            throws ParseException, DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(fecha);
        // System.out.println(respxml.getFecha());
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        String f = date2.toString();
        String[] fec = f.split("\\.");
        String valor = fec[0];
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(valor);
        return date;
    }

    public static String quitarAlias(String textocrypt, String toolOut) {
        Cipher cipher;
        String encryptedString;
        byte[] encryptText = null;
        byte[] raw;
        SecretKeySpec skeySpec;

        try {
            raw = Base64.decodeBase64(toolOut);
            skeySpec = new SecretKeySpec(raw, "AES");
            encryptText = Base64.decodeBase64(textocrypt);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            encryptedString = new String(cipher.doFinal(encryptText));

        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        return encryptedString;
    }

    public static String getParametro(String key) {
        String valor = "";
        for (int i = 0; i < listParametros.size(); i++) {
            if (key.equals(listParametros.get(i).getParametro_llave())) {
                valor = listParametros.get(i).getParametro_valor();
                break;
            }
        }
        return valor;
    }

    public static String getCurrentDateToString() {
        Date date = new Date();
        DateFormat da = new SimpleDateFormat("HH:mm:ss");
        DateFormat dd = new SimpleDateFormat("yyyy-MM-dd");
        String fechaActual = dd.format(date) + "T" + da.format(date);
        return fechaActual;
    }

    public static Date stringToDatePatern(String fecha, String patern_out) throws ParseException {
        Date dateout = null;
        SimpleDateFormat format2 = new SimpleDateFormat(patern_out); // "yyyy-MM-dd'T'HH:mm:ss"
        dateout = format2.parse(fecha);
        return dateout;
    }

    public synchronized static void writeXMLFileDate(String xmlString, String xmlName, String fechah) {
        BufferedWriter out = null;
        try {
            String word = "</EstatusUUID>";
            String xsi1 = "<Acuse ";
            String xsi2 = "<Folios";
            try {
                String headerXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                if (xmlString.indexOf(headerXML) == -1) {
                    String xsi1s = "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
                    String xsi2s = " xmlns=\"http://cancelacfd.sat.gob.mx\"";
                    String[] xsp1 = xmlString.split(xsi1);

                    xmlString = "";
                    xmlString = xsp1[0] + xsi1 + xsi1s + xsp1[1];
                    String[] xsp2 = xmlString.split(xsi2);

                    xmlString = "";
                    xmlString = xsp2[0] + xsi2 + xsi2s + xsp2[1];

                    String[] facturaXML = xmlString.split(word);
                    xmlString = headerXML + facturaXML[0] + word + "<Fecha>" + fechah + "</Fecha>" + facturaXML[1];
                } else {
                    String[] facturaXML = xmlString.split(word);
                    xmlString = facturaXML[0] + word + "<Fecha>" + fechah + "</Fecha>" + facturaXML[1];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            out = new BufferedWriter(new FileWriter(xmlName));
            out.write(xmlString);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String reemplazar(String cadena, String busqueda, String reemplazo) {
        return cadena.replaceAll(busqueda, reemplazo);
    }

    public static String xmlDomi() {
        return "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Y2ZkaTpDb21wcm9iYW50ZSB4bWxuczpjZmRpPSJodHRwOi8vd3d3LnNhdC5nb2IubXgvY2ZkLzMiIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIENlcnRpZmljYWRvPSJNSUlGK1RDQ0ErR2dBd0lCQWdJVU16QXdNREV3TURBd01EQXpNREF3TWpNM01EZ3dEUVlKS29aSWh2Y05BUUVMQlFBd2dnRm1NU0F3SGdZRFZRUUREQmRCTGtNdUlESWdaR1VnY0hKMVpXSmhjeWcwTURrMktURXZNQzBHQTFVRUNnd21VMlZ5ZG1samFXOGdaR1VnUVdSdGFXNXBjM1J5WVdOcHc3TnVJRlJ5YVdKMWRHRnlhV0V4T0RBMkJnTlZCQXNNTDBGa2JXbHVhWE4wY21GamFjT3piaUJrWlNCVFpXZDFjbWxrWVdRZ1pHVWdiR0VnU1c1bWIzSnRZV05wdzdOdU1Ta3dKd1lKS29aSWh2Y05BUWtCRmhwaGMybHpibVYwUUhCeWRXVmlZWE11YzJGMExtZHZZaTV0ZURFbU1DUUdBMVVFQ1F3ZFFYWXVJRWhwWkdGc1oyOGdOemNzSUVOdmJDNGdSM1ZsY25KbGNtOHhEakFNQmdOVkJCRU1CVEEyTXpBd01Rc3dDUVlEVlFRR0V3Sk5XREVaTUJjR0ExVUVDQXdRUkdsemRISnBkRzhnUm1Wa1pYSmhiREVTTUJBR0ExVUVCd3dKUTI5NWIyRmp3NkZ1TVJVd0V3WURWUVF0RXd4VFFWUTVOekEzTURGT1RqTXhJVEFmQmdrcWhraUc5dzBCQ1FJTUVsSmxjM0J2Ym5OaFlteGxPaUJCUTBSTlFUQWVGdzB4TnpBMU1UZ3dNelUwTlRaYUZ3MHlNVEExTVRnd016VTBOVFphTUlIbE1Ta3dKd1lEVlFRREV5QkJRME5GVFNCVFJWSldTVU5KVDFNZ1JVMVFVa1ZUUVZKSlFVeEZVeUJUUXpFcE1DY0dBMVVFS1JNZ1FVTkRSVTBnVTBWU1ZrbERTVTlUSUVWTlVGSkZVMEZTU1VGTVJWTWdVME14S1RBbkJnTlZCQW9USUVGRFEwVk5JRk5GVWxaSlEwbFBVeUJGVFZCU1JWTkJVa2xCVEVWVElGTkRNU1V3SXdZRFZRUXRFeHhCUVVFd01UQXhNREZCUVVFZ0x5QklSVWRVTnpZeE1EQXpORk15TVI0d0hBWURWUVFGRXhVZ0x5QklSVWRVTnpZeE1EQXpUVVJHVWs1T01Ea3hHekFaQmdOVkJBc1VFa05UUkRBeFgwRkJRVEF4TURFd01VRkJRVENDQVNJd0RRWUpLb1pJaHZjTkFRRUJCUUFEZ2dFUEFEQ0NBUW9DZ2dFQkFKZFVjc0hJRUlnd2l2dkFhbnRHbllWSU8zKzd5VGREMXRrS29wYkwrdEtTalJGbzFFclBkR0p4UDNneFQ1TytBQ0lEUVhOK0hTOXVNV0RZbmFVUmFsU0lGOUNPRkNkaC9PSDJQbitVbWtONGN1bHIyRGFuS3p0VklPOGlkWE02YzlhSG41aE9vN2hEeFhNQzN1T3VHVjNGUzRPYmt4VFYrOU5zdk9BVjJsTWUyN1NIclNCMERodUx1clViWndYbSsvcjRkdHozYjJ1TGdCYytEaXk5NVBHK01JdTdvTktNODlhQk5HY2pUSncrOWsrV3pKaVBkM1pwUWdJZWRZQkQrOFFXeGxZQ2d4aG50YTNrOXlsZ1hLWVhDWWswazBxYXV2QkoxalNSVmY1QmpqSVViT3N0YVFwNTlua2dIaDQ1YzlnbndKUlY2MThOVzBmTWVEenVLUjBDQXdFQUFhTWRNQnN3REFZRFZSMFRBUUgvQkFJd0FEQUxCZ05WSFE4RUJBTUNCc0F3RFFZSktvWklodmNOQVFFTEJRQURnZ0lCQUJLajBEQ05MMWxoNDR5K09jV0ZyVDJpY25LRjdXeVNPVmloeDBvUitIUHJXS0JNWHhvOUt0cm9kbkIxdGdJeDhmK1hqcXlwaGhidytqdURTZURyYjk5UGhDNCtFNkplWE9rZFFjSnQ1MEt5b2RsOVVScENWV05XalViM0YveXBhOG9UY2ZmL2VNZnRRWlQ3TVExTHFodCt4bTNRaFZveFRJQVNjZTBqanNuQlRHRDJKUTR1VDNvQ2VtOGJtb01YVi9mazlhSjN2MCtaSUw0Mk1wWTRQT0dVYS9pVGFhd2tsS1JBTDFYajlJZElSMDZSSzY4UlM2eHJHazZqd2JEVEVLeEpwbVozU1BMdGxzbVBVVE8xa3JhVFBJbzlGQ21VL3paa1dHcGQ4WkVBQUZ3K1pmSStiZFhCZnZkRHdhTTJpTUdUUVpUVEVnVTVLS1RJdmtBbkhvOU80NVNxU0p3cVY5TkxmUEF4Q281ZVJSMk9HaWJkOWpoSGU4MXpVc3A1R2RFMW1aaVNxSlU4MkgzY3U2QmlFK0QzWWJaZVpuanJOU3hCZ0tUSWY4dytLTllQTTRhV251VU1sMG1MZ3RPeFRVWGk5TUtuVWNjcTNHWkxBN2J4N1puMjExeVBScUVqU0FxeWJVTVZJT2hvNmFxemtmYzNXTFo2TG5HVStoeUh1WlVmUHdibkNsYjdvRkZ6MVBsdkdPcE5Ec1ViMHFQNDJRQ0dCaVRVc2VHdWdBenFPUDZFWXBWUEM3M2dGb3VybWRCUWdmYXlhRXZpM3hqTmFuRmtQbFcxWEVZTnJZSkI0eU5qcGhGcnZXd1RZODZ2TDJvOGdaTjBVdG1jNWZub0JUZk05cjJ6VkttRWk2RlVlSjFpYURhVk52NDd0ZTlpUzFhaTRWNHZCWThyIiBEZXNjdWVudG89IjAuMDAiIEZlY2hhPSIyMDIxLTAyLTAzVDEzOjI3OjE0IiBGb2xpbz0iNTQ1MCIgRm9ybWFQYWdvPSIwMSIgTHVnYXJFeHBlZGljaW9uPSIwODE4MCIgTWV0b2RvUGFnbz0iUFVFIiBNb25lZGE9Ik1YTiIgTm9DZXJ0aWZpY2Fkbz0iMzAwMDEwMDAwMDAzMDAwMjM3MDgiIFNlbGxvPSJla2JzRFJpUTlLc1FDMUpQSHJEbTVwV3lZdnNMUEc0WCtHdUU0VnNnYzJKb1JUM3pvb05LZjMySDFQMU5RYWZncHUxeUZmL29IZ0R0SlE5M0hWNHZDaHMzN3B3R0dPN0s3ZHV6YlBwaUlaTk03OXpHTkNuMHg5NlpINk9DRWliM21ndjQyaksxeVY1NFB4aEhoM0lyaDE2bjJnbVByNVlWUWJ3WnJ2SUMwZDcvZXByV2dObytDL1NzTlVlSzZGT2RZRjZGNjQyRkdyMzBhUWNISDAzNjhhWTBOa1p2QTZWWUZWS3NkZzgyRzdoc3BjQjhNQThBL1BvSGcyKzJIQnB3ZXdsVFBHMnhhVG5wNW5TWGRVbTVPQVR4RE00QU4rRnBBWUUyVEorQWhQUlZKdk1CazlubFhTYTgvc1BMUDRCMEQ3RWl5YllRczRmK2Nselh3bDVSdHc9PSIgU2VyaWU9IkRBQUFKIiBTdWJUb3RhbD0iMi4xNiIgVGlwb0NhbWJpbz0iMSIgVGlwb0RlQ29tcHJvYmFudGU9IkkiIFRvdGFsPSIyLjUxIiBWZXJzaW9uPSIzLjMiIHhzaTpzY2hlbWFMb2NhdGlvbj0iaHR0cDovL3d3dy5zYXQuZ29iLm14L2NmZC8zIGh0dHA6Ly93d3cuc2F0LmdvYi5teC9zaXRpb19pbnRlcm5ldC9jZmQvMy9jZmR2MzMueHNkIj4KICAgIDxjZmRpOkVtaXNvciBOb21icmU9IlRFUkVYIE1FWElDTyIgUmVnaW1lbkZpc2NhbD0iNjAxIiBSZmM9IkFBQTAxMDEwMUFBQSIvPgogICAgPGNmZGk6UmVjZXB0b3IgTm9tYnJlPSJKVUFOIElHTkFDSU8gQ0FSQkFKQUwgUklWRVJBIiBSZmM9IkNBUko3NDAyMDFVMzYiIFVzb0NGREk9IkcwMyIvPgogICAgPGNmZGk6Q29uY2VwdG9zPgogICAgICAgIDxjZmRpOkNvbmNlcHRvIENhbnRpZGFkPSIxLjAwIiBDbGF2ZVByb2RTZXJ2PSI1MDE2MTgxNSIgQ2xhdmVVbmlkYWQ9Ikg4NyIgRGVzY3JpcGNpb249IkNMT1JFVFMgNCBNQVMgMSBNRU5UQSIgSW1wb3J0ZT0iMi4xNiIgTm9JZGVudGlmaWNhY2lvbj0iODMxMDE1MDEiIFZhbG9yVW5pdGFyaW89IjIuMTYiPgogICAgICAgICAgICA8Y2ZkaTpJbXB1ZXN0b3M+CiAgICAgICAgICAgICAgICA8Y2ZkaTpUcmFzbGFkb3M+CiAgICAgICAgICAgICAgICAgICAgPGNmZGk6VHJhc2xhZG8gQmFzZT0iMi4xNiIgSW1wb3J0ZT0iMC4zNSIgSW1wdWVzdG89IjAwMiIgVGFzYU9DdW90YT0iMC4xNjAwMDAiIFRpcG9GYWN0b3I9IlRhc2EiLz4KICAgICAgICAgICAgICAgIDwvY2ZkaTpUcmFzbGFkb3M+CiAgICAgICAgICAgIDwvY2ZkaTpJbXB1ZXN0b3M+CiAgICAgICAgPC9jZmRpOkNvbmNlcHRvPgogICAgPC9jZmRpOkNvbmNlcHRvcz4KICAgIDxjZmRpOkltcHVlc3RvcyBUb3RhbEltcHVlc3Rvc1RyYXNsYWRhZG9zPSIwLjM1Ij4KICAgICAgICA8Y2ZkaTpUcmFzbGFkb3M+CiAgICAgICAgICAgIDxjZmRpOlRyYXNsYWRvIEltcG9ydGU9IjAuMzUiIEltcHVlc3RvPSIwMDIiIFRhc2FPQ3VvdGE9IjAuMTYwMDAwIiBUaXBvRmFjdG9yPSJUYXNhIi8+CiAgICAgICAgPC9jZmRpOlRyYXNsYWRvcz4KICAgIDwvY2ZkaTpJbXB1ZXN0b3M+CjxjZmRpOkNvbXBsZW1lbnRvPjx0ZmQ6VGltYnJlRmlzY2FsRGlnaXRhbCB4bWxuczp0ZmQ9Imh0dHA6Ly93d3cuc2F0LmdvYi5teC9UaW1icmVGaXNjYWxEaWdpdGFsIiBGZWNoYVRpbWJyYWRvPSIyMDIxLTAyLTAzVDIwOjE3OjQwIiBOb0NlcnRpZmljYWRvU0FUPSIzMDAwMTAwMDAwMDQwMDAwMjQ5NSIgUmZjUHJvdkNlcnRpZj0iU1BSMTkwNjEzSTUyIiBTZWxsb0NGRD0iZWtic0RSaVE5S3NRQzFKUEhyRG01cFd5WXZzTFBHNFgrR3VFNFZzZ2MySm9SVDN6b29OS2YzMkgxUDFOUWFmZ3B1MXlGZi9vSGdEdEpROTNIVjR2Q2hzMzdwd0dHTzdLN2R1emJQcGlJWk5NNzl6R05DbjB4OTZaSDZPQ0VpYjNtZ3Y0MmpLMXlWNTRQeGhIaDNJcmgxNm4yZ21QcjVZVlFid1pydklDMGQ3L2VwcldnTm8rQy9Tc05VZUs2Rk9kWUY2RjY0MkZHcjMwYVFjSEgwMzY4YVkwTmtadkE2VllGVktzZGc4Mkc3aHNwY0I4TUE4QS9Qb0hnMisySEJwd2V3bFRQRzJ4YVRucDVuU1hkVW01T0FUeERNNEFOK0ZwQVlFMlRKK0FoUFJWSnZNQms5bmxYU2E4L3NQTFA0QjBEN0VpeWJZUXM0ZitjbHpYd2w1UnR3PT0iIFNlbGxvU0FUPSJRbm9kZ1pvSEYrRDdzNFZqbk9uQy9pUUx3eUdHdXFISThtTGtRaHozWHFZaFFVQlFMSUF1cjIzQ1FnMU5ORVEwcGJSVk5CNmNQaXd6VnE1bjR1YkJEczg4ZExqRGFqbnQxNWcyTDRPKy9zWWI3anY1R0F1S2NQdWlyVFRWSW5oRWwwdm5lWjUrcGVPdjJDUGl4MGN5dFQ1NGphcTFma1ZESHZzcEE0K0R4UEFhamZ1WGpObW9pUlU5bHJxUjZYNHRSZGlFTHhFdVovSGRldEwwMWJIMlVTYkpvOWpubEpMKzBmY3Z6ejVwNnhVc2l4L3pFUUdhT0RmQ3VCNUVNMEs1amY4WENIWnhYUlA3VmYwM0RITmtaMWVEd3RSekc0YkhvZDZDc0RnS3FpczVNaGo1Y0xGVm1RaWE5V3lQUkxSVy8rTU5sU2JXM1A4SFhMV2FscUNVeEE9PSIgVVVJRD0iNmMyYTBkNmQtYjE0Ni00OTU1LWJjMmUtYzA5ZTBiMWY3OGUyIiBWZXJzaW9uPSIxLjEiIHhzaTpzY2hlbWFMb2NhdGlvbj0iaHR0cDovL3d3dy5zYXQuZ29iLm14L1RpbWJyZUZpc2NhbERpZ2l0YWwgaHR0cDovL3d3dy5zYXQuZ29iLm14L3NpdGlvX2ludGVybmV0L2NmZC9UaW1icmVGaXNjYWxEaWdpdGFsL1RpbWJyZUZpc2NhbERpZ2l0YWx2MTEueHNkIi8+PC9jZmRpOkNvbXBsZW1lbnRvPjwvY2ZkaTpDb21wcm9iYW50ZT4=";
    }

    public static String getexpresionImpresa(CancelaDto cfdis) {
        String cadena = "";
        try {
            cadena = cfdis.getRfcEmisor() + "," + cfdis.getRfcReceptor() + "," + cfdis.getTotal() + ","
                    + cfdis.getUuid();
        } catch (Exception e) {

        }

        return cadena;
    }

    public static String getexpresionImpresa2(CancelaDto cfdis) {
        String cadena = "";
        try {
            cadena = "?re=" + cfdis.getRfcEmisor() + "&rr=" + cfdis.getRfcReceptor() + "&tt=" + cfdis.getTotal()
                    + "&id=" + cfdis.getUuid();
        } catch (Exception e) {

        }
        return cadena;
    }

    public static CancelaDto creaObjetCancelaDto(Document document) {
        CancelaDto cancelaDto = new CancelaDto();
        NodeList listaNotification = document.getElementsByTagName("Cancelacion");
        Node nodo = listaNotification.item(0);
        if (nodo.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) nodo;
            cancelaDto.setUuid(element.getElementsByTagName("UUID").item(0).getTextContent());
            cancelaDto.setRfcEmisor(element.getElementsByTagName("RfcEmisor").item(0).getTextContent());
            cancelaDto.setRfcReceptor(element.getElementsByTagName("RfcReceptor").item(0).getTextContent());
            cancelaDto.setTotal(element.getElementsByTagName("Total").item(0).getTextContent());
            cancelaDto.setMotivo(element.getElementsByTagName("Motivo").item(0).getTextContent());
            cancelaDto.setFolioSustitucion(element.getElementsByTagName("FolioSustitucion").item(0).getTextContent());
        }
        return cancelaDto;
    }

    public static AddendaEmisor creaObjetAddendaInterna(Document document) {
        AddendaEmisor addendaEmisor = new AddendaEmisor();
        DomicilioEmisor domicilioEmisor = new DomicilioEmisor();
        DomicilioReceptor domicilioReceptor = new DomicilioReceptor();

        NodeList nodeDomicilioEmisor = document.getElementsByTagName("DomicilioEmisor");
        domicilioEmisor.setCp(nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("CP") == null ? ""
                : nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("CP").getNodeValue());
        domicilioEmisor.setCalle(nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("Calle") == null ? ""
                : nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("Calle").getNodeValue());
        domicilioEmisor.setCiudad(nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("Ciudad") == null ? ""
                : nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("Ciudad").getNodeValue());
        domicilioEmisor.setEstado(nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("Estado") == null ? ""
                : nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("Estado").getNodeValue());
        domicilioEmisor.setPais(nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("Pais") == null ? ""
                : nodeDomicilioEmisor.item(0).getAttributes().getNamedItem("Pais").getNodeValue());

        NodeList nodeDomicilioReceptor = document.getElementsByTagName("DomicilioReceptor");
        domicilioReceptor.setCp(nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("CP") == null ? ""
                : nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("CP").getNodeValue());
        domicilioReceptor.setCalle(nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("Calle") == null ? ""
                : nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("Calle").getNodeValue());
        domicilioReceptor.setCiudad(nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("Ciudad") == null ? ""
                : nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("Ciudad").getNodeValue());
        domicilioReceptor.setEstado(nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("Estado") == null ? ""
                : nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("Estado").getNodeValue());
        domicilioReceptor.setPais(nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("Pais") == null ? ""
                : nodeDomicilioReceptor.item(0).getAttributes().getNamedItem("Pais").getNodeValue());

        NodeList nodeAddendaEmisor = document.getElementsByTagName("AddendaEmisor");
        if (nodeAddendaEmisor.item(0).getAttributes().getNamedItem("Observaciones") != null) {
            addendaEmisor.setObservaciones(nodeAddendaEmisor.item(0).getAttributes().getNamedItem("Observaciones").getNodeValue());
        }
        if (nodeAddendaEmisor.item(0).getAttributes().getNamedItem("OrdenCompra") != null) {
            addendaEmisor.setOrdenCompra(nodeAddendaEmisor.item(0).getAttributes().getNamedItem("OrdenCompra").getNodeValue());
        }
        addendaEmisor.setDomicilioEmisor(domicilioEmisor);
        addendaEmisor.setDomicilioReceptor(domicilioReceptor);

        return addendaEmisor;
    }

    public static String getNombreRandom() {
        char n;
        Random rnd = new Random();
        String cadena = new String();
        for (int i = 0; i < 10; i++) {
            n = (char) (rnd.nextDouble() * 26.0 + 65.0);
            cadena += n;
        }
        return cadena;
    }

    public static Document agregaSchemaLocationComprobante(Comprobante comprobante) {
        ObjectFactory of = new ObjectFactory();
        Complemento complemento = of.createComprobanteComplemento();
        Serializer serializer = new Serializer();
        Document xmlDocument = null;
        Pagos pagos = null;
        ComercioExterior comercioExterior = null;
        try {
            if (comprobante != null && comprobante.getComplemento() != null) {
                Comprobante.Complemento o = comprobante.getComplemento();
                for (Object c : o.getAny()) {
                    if (c instanceof mx.gob.sat.pagos20.Pagos) {
                        pagos = (Pagos) c;
                        comprobante.setComplemento(null);
                    } else if (c instanceof comercioexterior20.ComercioExterior) {
                        comercioExterior = (ComercioExterior) c;
                        comprobante.setComplemento(null);
                    }
                }
            }
            serializer.setSchemaLocation("http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd");
            xmlDocument = serializer.serialize(comprobante, false);
            if (pagos != null) {
                Serializer serializerComplemento = new Serializer();
                serializerComplemento.setSchemaLocation(
                        "http://www.sat.gob.mx/Pagos20 http://www.sat.gob.mx/sitio_internet/cfd/Pagos/Pagos20.xsd");
                org.w3c.dom.Document docCommplemento = serializerComplemento.serialize(pagos, false);
                Extra2CFDI e2Cfdi = new Extra2CFDI(Extra2CFDI.CfdiElementType.COMPLEMENTO);
                xmlDocument = e2Cfdi.generate(xmlDocument, docCommplemento);
                complemento.getAny().add(pagos);
            } else if (comercioExterior != null) {
                Serializer serializerComplemento = new Serializer();
                serializerComplemento.setSchemaLocation(
                        "http://www.sat.gob.mx/ComercioExterior20 http://www.sat.gob.mx/sitio_internet/cfd/ComercioExterior20/ComercioExterior20.xsd");
                org.w3c.dom.Document docCommplemento = serializerComplemento.serialize(comercioExterior, false);
                Extra2CFDI e2Cfdi = new Extra2CFDI(Extra2CFDI.CfdiElementType.COMPLEMENTO);
                xmlDocument = e2Cfdi.generate(xmlDocument, docCommplemento);
                complemento.getAny().add(comercioExterior);
            }
            if (!complemento.getAny().isEmpty()) {
                comprobante.setComplemento(complemento);
            }
        } catch (ParserConfigurationException | JAXBException ex) {
            log.error("Error ::", ex);
        }
        return xmlDocument;
    }

    public static String getString(Document document)
            throws TransformerConfigurationException, TransformerException, IOException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(domSource, streamResult);
        stringWriter.close();
        String stringResult = stringWriter.toString();
        return stringResult;
    }

    public static String requestTimbrado() {
        String retorno = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:respuestaTimbrado\">\n"
                + "	<SOAP-ENV:Body>\n" + "		<tns:timbrarCFDI xmlns:tns=\"urn:respuestaTimbrado\">\n"
                + "			<suscriptorRFC xsi:type=\"xsd:string\"></suscriptorRFC>\n"
                + "			<agenteTI xsi:type=\"xsd:string\">EDI101020E99</agenteTI>\n"
                + "			<documentoXML xsi:type=\"xsd:string\"></documentoXML>\n" + "		</tns:timbrarCFDI>\n"
                + "	</SOAP-ENV:Body>\n" + "</SOAP-ENV:Envelope>";
        return retorno;
    }

    public static String generarResponse(HttpURLConnection httpURLConnection) {
        BufferedReader responseBuffer;
        String output;
        String response = "";
        try {
            responseBuffer = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));
            while ((output = responseBuffer.readLine()) != null) {
                response += output;
            }
        } catch (IOException IOException) {
            IOException.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }
        return response.replaceAll("\n", "");
    }

    // Creación del conteo de paginas con dos pdf distintos.
    public static void manipulatePdfCartaPorte(String src, String src2, String dest)
            throws IOException, DocumentException {
        PDFMergerUtility obj = new PDFMergerUtility();
        obj.setDestinationFileName(dest + "_temp3.pdf");
        obj.addSource(new File(src));
        obj.addSource(new File(src2));
        obj.mergeDocuments();

        Font normal7 = new Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL);
        PdfReader reader = new PdfReader(dest + "_temp3.pdf");

        int n = reader.getNumberOfPages();
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest + ".pdf"));
        PdfContentByte pagecontent;
        for (int i = 0; i < n;) {
            pagecontent = stamper.getOverContent(++i);
            ColumnText.showTextAligned(pagecontent, com.itextpdf.text.Element.ALIGN_CENTER,
                    new Phrase(String.format("Página: %s/%s", i, n), normal7), 310, 20, 0);
        }
        stamper.close();
        reader.close();
    }

    // Creación del conteo de paginas.
    public static void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        Font normal7 = new Font(Font.FontFamily.HELVETICA, 7f);
        PdfReader reader = new PdfReader(src);
        int n = reader.getNumberOfPages();
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        PdfContentByte pagecontent;
        for (int i = 0; i < n;) {
            pagecontent = stamper.getOverContent(++i);
            ColumnText.showTextAligned(pagecontent, com.itextpdf.text.Element.ALIGN_CENTER,
                    new Phrase(String.format("Página: %s/%s", i, n), normal7), 310, 20, 0);
        }
        stamper.close();
        reader.close();
    }

    public static String getNombreCfdi(Comprobante cfdis) {
        String serie = cfdis.getSerie() == null ? "" : cfdis.getSerie();
        String folio = cfdis.getFolio() == null ? "" : cfdis.getFolio();
        return serie + folio;
    }

    public static String crearAddendaViscofan(String addenda) {
        String retorno = null;
        try {
            Document docAddenda = convertStringToDocument(addenda);
            Node nodoRaiz = docAddenda.getFirstChild();
            NamedNodeMap atributos = nodoRaiz.getAttributes();
            String ordenCompra = atributos.getNamedItem("ordenCompra").getNodeValue();
            String noAcreedor = atributos.getNamedItem("noAcreedor").getNodeValue();
            String plantaEntrega = atributos.getNamedItem("plantaEntrega").getNodeValue();
            String noLineaArticulo = atributos.getNamedItem("noLineaArticulo").getNodeValue();
            String email = atributos.getNamedItem("e-mail").getNodeValue();

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            //Elemento raíz
            Document doc = docBuilder.newDocument();
            Element elementRoot = doc.createElement("cfdi:Addenda");
            doc.appendChild(elementRoot);

            Element elementOrdenCompra= doc.createElement("ordenCompra");
            elementOrdenCompra.setTextContent(ordenCompra);
            elementRoot.appendChild(elementOrdenCompra);
            
            Element elementnoAcreedor= doc.createElement("noAcreedor");
            elementnoAcreedor.setTextContent(noAcreedor);
            elementRoot.appendChild(elementnoAcreedor);
            
            Element elementplantaEntrega= doc.createElement("plantaEntrega");
            elementplantaEntrega.setTextContent(plantaEntrega);
            elementRoot.appendChild(elementplantaEntrega);
            
            Element elementnoLineaArticulo= doc.createElement("noLineaArticulo");
            elementnoLineaArticulo.setTextContent(noLineaArticulo);
            elementRoot.appendChild(elementnoLineaArticulo);
            
            Element elementnemail= doc.createElement("e-mail");
            elementnemail.setTextContent(email);
            elementRoot.appendChild(elementnemail);
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            retorno = writer.getBuffer().toString();
        } catch (Exception e) {
            log.error("Error ::", e);
        }
        return retorno;
    }

    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
