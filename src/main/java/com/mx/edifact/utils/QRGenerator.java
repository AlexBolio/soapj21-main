/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.edifact.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author ITIC Roger Azcorra Novelo [RJAN]
 */
public class QRGenerator {

    private static final String FORMATO_IMAGEN = "jpg";
    private int ancho;
    private int alto;
    private InputStream qrcode;

    public QRGenerator() {
        ancho = 500;
        alto = 500;
    }

    public QRGenerator(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
    }

    public void build(String datos) {
        try {
            Writer writer = new QRCodeWriter();
            BitMatrix bm = writer.encode(datos, BarcodeFormat.QR_CODE, ancho, alto);
            BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < ancho; y++) {
                for (int x = 0; x < alto; x++) {
                    int grayValue = (bm.get(x, y) ? 0 : 1) & 0xff;
                    image.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
                }
            }
            //Variable output contrendra el flujo de salida
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, FORMATO_IMAGEN, output);
            this.qrcode = new ByteArrayInputStream(output.toByteArray());

        } catch (IOException ex) {
            this.qrcode = null;
            Logger.getLogger(QRGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriterException ex) {
            this.qrcode = null;
            Logger.getLogger(QRGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public InputStream getQrcode() {
        return qrcode;
    }
}
