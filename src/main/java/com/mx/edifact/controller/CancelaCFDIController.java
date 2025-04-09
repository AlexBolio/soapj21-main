package com.mx.edifact.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.sslmod.PKCS8Key;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.mx.edifact.cancelar.EnviaAcuseCancelacionBindingStub;
import com.mx.edifact.cancelar.EnviaAcuseCancelacionLocator;
import com.mx.edifact.cancelar.EnviaAcuseCancelacionPortType;
import com.mx.edifact.model.ReturnCancelaCFDI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class CancelaCFDIController {

    private Document doc;
    private static final Logger log = LogManager.getLogger(CancelaCFDIController.class);

    public String firmarXML(String rfc, String path_cert, String passwd, String uuid, String fecha, String ambiente,
            String motivo, String folioSustitucion) {
        try {
            byte[] certificado = FileUtils.readFileToByteArray(new File(path_cert + "/" + rfc + ".cer"));
            byte[] llave = FileUtils.readFileToByteArray(new File(path_cert + "/" + rfc + ".key"));
            String uuids = "";
            if (folioSustitucion != null) {
                uuids = "<Folio UUID=\"" + uuid + "\" Motivo=\"" + motivo + "\" FolioSustitucion=\"" + folioSustitucion
                        + "\"/>";
            } else {
                uuids = "<Folio UUID=\"" + uuid + "\" Motivo=\"" + motivo + "\"/>";
            }

            String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Cancelacion xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://cancelacfd.sat.gob.mx\" Fecha=\""
                    + fecha + "\" RfcEmisor=\"" + rfc.replace("&", "&amp;") + "\">" + "<Folios>" + uuids + "</Folios>"
                    + "</Cancelacion>";

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null, null);
            SignedInfo si = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificado));
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            List x509Content = new ArrayList();
            // x509Content.add(kif.newX509IssuerSerial(cert.getIssuerDN().getName(),
            // cert.getSerialNumber()));
            x509Content.add(kif.newX509IssuerSerial(cert.getIssuerDN().getName(), cert.getSerialNumber()));
            x509Content.add(cert);
            X509Data xd = kif.newX509Data(x509Content);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            InputSource source = new InputSource(new StringReader(xmlString));
            this.doc = dbf.newDocumentBuilder().parse(source);
            InputStream bis = new ByteArrayInputStream(llave);
            PKCS8Key key = new PKCS8Key(bis, passwd.toCharArray());
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(key.getDecryptedBytes());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(privKeySpec);
            DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
            XMLSignature signature = fac.newXMLSignature(si, ki);
            signature.sign(dsc);
        } catch (Exception e) {
            log.error("Error :: ", e);
        }
        return enviarPeticionCancelacion(ambiente);
    }

    private synchronized String enviarPeticionCancelacion(String ambiente) {
        String respuestaCancelacion = "";
        try {
            // String ambiente =
            // this.request.getEmpresa().getPropiedades().getProperty("timbrado.ambiente");
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            String strArchivoCancelacion = writer.toString();

            // System.out.println("FileCancel: "+ strArchivoCancelacion);
//	        EnviaAcuseCancelacionPortType weatherSoap = new EnviaAcuseCancelacionLocator(ambiente_).getenviaAcuseCancelacionPort();
//	        EnviaAcuseCancelacionBindingStub stub = (EnviaAcuseCancelacionBindingStub)weatherSoap;
//	        String respuestaCancelacion=weatherSoap.enviaAcuseCancelacion(strArchivoCancelacion, ambiente_);
            // Cancelacion SAT
            EnviaAcuseCancelacionPortType weatherSoap = new EnviaAcuseCancelacionLocator(ambiente)
                    .getenviaAcuseCancelacionPort();
            EnviaAcuseCancelacionBindingStub stub = (EnviaAcuseCancelacionBindingStub) weatherSoap;
            respuestaCancelacion = weatherSoap.enviaAcuseCancelacion(strArchivoCancelacion, ambiente);
        } catch (Exception e) {
            log.error("Error :: ", e);
        }
        return respuestaCancelacion;
    }

    public ReturnCancelaCFDI validarRespuestaCancelacion(String respuesta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(true);

        ReturnCancelaCFDI cancelaCFDIDto = new ReturnCancelaCFDI();

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(respuesta));

        Document docRespuesta = db.parse(is);

        // docRespuesta = factory.newDocumentBuilder().parse(new InputSource(new
        // StringReader(this.strAcuse)));
        if (docRespuesta == null) {
            throw new Exception("Respuesta de acuse incompleto :( no se pudo parsear el strAcuse a docAcuse");
        }
        String mensaje = "";

        String CodEstatus = getTagValue(respuesta, "EstatusUUID");
        if (CodEstatus == null) {
            CodEstatus = getTagValue(respuesta, "CodEstatus");
        }
        String descripcionError = "";

        if (CodEstatus.trim().equals("201")) {
            mensaje = "UUID Enviado correctamente, codigo 201";
        } else if (CodEstatus.trim().equals("202")) {
            mensaje = "UUID Previamente enviado, codigo 202";
        } else {
            if (CodEstatus != null && !CodEstatus.isEmpty()) {
                int codigoEstatus = 0;
                try {
                    codigoEstatus = Integer.parseInt(CodEstatus);
                } catch (Exception e) {
                }

                switch (codigoEstatus) {
                    case 201:
                        descripcionError = "solicitud de cancelaciónviada correctamente 201";
                        break;
                    case 202:
                        descripcionError = "solicitud de cancelaciónviada correctamente 202";
                        break;
                    case 203:
                        descripcionError = "UUID no corresponde al emisor. 203";
                        break;
                    case 205:
                        descripcionError = "UUID no existe en el SAT. 205";
                        break;
                    case 301:
                        descripcionError = "XML mal formado";
                        break;
                    case 302:
                        descripcionError = "Sello mal formado o invádo";
                        break;
                    case 303:
                        descripcionError = "Sello no corresponde a emisor o caduco";
                        break;
                    case 304:
                        descripcionError = "Certificado revicado o caduco";
                        break;
                    case 305:
                        descripcionError = "La fecha de emisión no está dentro de la vigencia del CSD del emisor";
                        break;
                    case 306:
                        descripcionError = "El certificado no es del tipo CFD";
                        break;
                    case 307:
                        descripcionError = "El CFDI contiene un timbre previo";
                        break;
                    case 308:
                        descripcionError = "Certificado no expedido por el SAT";
                        break;
                    default:
                        descripcionError = "Error no especificado";
                }
                mensaje = "Se produjo un error con codigo " + CodEstatus + " (" + descripcionError + ")";

            }
        }
        cancelaCFDIDto.setCode(CodEstatus);
        cancelaCFDIDto.setMessage(mensaje);
        return cancelaCFDIDto;
    }

    public String getTagValue(String xml, String tagName) {
        try {
            if (tagName.equals("CodEstatus")) {
                xml = xml.split("<Acuse " + tagName + "=\"")[1].split("\"")[0];
            } else {
                xml = xml.split("<" + tagName + ">")[1].split("</" + tagName + ">")[0];
            }
        } catch (Exception e) {
            xml = null;
        }
        return xml;
    }

}
