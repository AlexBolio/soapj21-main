/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.edifact.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
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
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.ssl.PKCS8Key;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ITIC Roger Azcorra Novelo [RJAN]
 */
public class DigitalSignature {

    private final byte[] cer;
    private final byte[] key;
    private final String password;

    /**
     *
     * @param cer - Certificado en binario
     * @param key - Llave priavada en binario
     * @param password - Contraseña de la llave primaria
     */
    public DigitalSignature(byte[] cer, byte[] key, String password) {
        this.cer = cer;
        this.key = key;
        this.password = password;
    }

    /**
     *
     * @param document
     * @return document firmado digitalmente
     * @throws Exception
     */
    public Document sign10(Document document) throws Exception {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        Reference ref = fac.newReference("",
                fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(
                        fac.newTransform(Transform.ENVELOPED,
                                (TransformParameterSpec) null
                        )
                ),
                null,
                null
        );

        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(
                        CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec) null
                ),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref)
        );

        X509Certificate cert = getCertificate(this.cer);
        KeyInfoFactory kif = fac.getKeyInfoFactory();

        List x509Content = new ArrayList();
        x509Content.add(kif.newX509IssuerSerial(cert.getIssuerDN().getName(), cert.getSerialNumber()));
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);

        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        PrivateKey privateKey = getPrivateKey(this.key, this.password);
        XMLSignature signature = fac.newXMLSignature(si, ki);
        DOMSignContext dsc = new DOMSignContext(privateKey, document.getDocumentElement());
        signature.sign(dsc);
        //Retorna el documento firmado
        return document;
    }

    public void sign11(Document document) throws Exception {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        Reference ref = fac.newReference("",
                fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(
                        fac.newTransform(Transform.XPATH,
                                new XPathFilterParameterSpec("not(ancestor-or-self::ds:Signature)"))
                ),
                null,
                null
        );

        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(
                        "http://www.w3.org/2006/12/xml-c14n11",
                        (C14NMethodParameterSpec) null
                ),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref)
        );

        X509Certificate cert = getCertificate(cer);

        String serialNumber = getSerial(cert.getSerialNumber());
        PublicKey publicKey = cert.getPublicKey();

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        KeyName newKeyName = kif.newKeyName(serialNumber);
        KeyValue newKeyValue = kif.newKeyValue(publicKey);

        List listKeyInfo = new ArrayList();
        listKeyInfo.add(newKeyName);
        listKeyInfo.add(newKeyValue);
        KeyInfo ki = kif.newKeyInfo(listKeyInfo);

        PrivateKey privateKey = getPrivateKey(key, password);
        XMLSignature signature = fac.newXMLSignature(si, ki);
        DOMSignContext dsc = new DOMSignContext(privateKey, document.getDocumentElement());
        dsc.setDefaultNamespacePrefix("ds");
        signature.sign(dsc);
    }


    public Document stamp33(Document document) throws TransformerException, NoSuchAlgorithmException, GeneralSecurityException, IOException {
        DOMSource source = new DOMSource(document);
        Writer outputWriter = new StringWriter();
        Result result = new StreamResult(outputWriter);
        //TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = CadenaOriginal40.getInstance().getCachedXSLT().newTransformer();
        transformer.transform(source, result);

        String cadenaOriginal = outputWriter.toString();

        //Se firma la cadena original y se obtiene el sello sin codificar
        Signature instance = Signature.getInstance("SHA256withRSA");
        PrivateKey privateKey = getPrivateKey(key, password);
        instance.initSign(privateKey);
        Charset csets = Charset.forName("UTF-8");
        instance.update(csets.encode(cadenaOriginal));
        byte[] signature = instance.sign();

        X509Certificate certificate = getCertificate(cer);

        //Se crea el sellodigital y certificado codificado en base 64 y se agrega al documento XML
        String sello = Base64.encodeBase64String(signature);
        String certificado = Base64.encodeBase64String(certificate.getEncoded());
        Element root = document.getDocumentElement();
        root.setAttribute("Sello", sello);
        root.setAttribute("Certificado", certificado);
        return document;
    }
    
    public Document stamp40(Document document) throws TransformerException, NoSuchAlgorithmException, GeneralSecurityException, IOException {
        
        X509Certificate certificate = getCertificate(cer);
        Element root = document.getDocumentElement();
        root.setAttribute("NoCertificado", getSerial(certificate.getSerialNumber()));
        
    	DOMSource source = new DOMSource(document);
        Writer outputWriter = new StringWriter();
        Result result = new StreamResult(outputWriter);
        //TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = CadenaOriginal40.getInstance().getCachedXSLT().newTransformer();
        transformer.transform(source, result);
        
        String cadenaOriginal = outputWriter.toString();

        //Se firma la cadena original y se obtiene el sello sin codificar
        Signature instance = Signature.getInstance("SHA256withRSA");
        PrivateKey privateKey = getPrivateKey(key, password);
        instance.initSign(privateKey);
        Charset csets = Charset.forName("UTF-8");
        instance.update(csets.encode(cadenaOriginal));
        byte[] signature = instance.sign();

        //Se crea el sellodigital y certificado codificado en base 64 y se agrega al documento XML
        String sello = Base64.encodeBase64String(signature);
        String certificado = Base64.encodeBase64String(certificate.getEncoded());
        root.setAttribute("Sello", sello);
        root.setAttribute("Certificado", certificado);
        return document;
    }

    private X509Certificate getCertificate(byte[] cer) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cer));
        return cert;
    }

    private PrivateKey getPrivateKey(byte[] key, String password) throws GeneralSecurityException, IOException {
        PKCS8Key key2 = new PKCS8Key(key, password.toCharArray());
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(key2.getDecryptedBytes());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(privKeySpec);
        return privateKey;
    }

    private String getSerial(BigInteger serialNumber) {
        String st = serialNumber.toString(16);
        int inicio = 1;
        int total = st.length();
        StringBuilder serial = new StringBuilder();
        while (inicio < total) {
            String par = st.substring(inicio, inicio + 1);
            serial.append(par);
            inicio += 2;
        }
        return serial.toString();
    }

}
