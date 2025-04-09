package com.mx.edifact.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.SplitCharacter;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfChunk;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author RobinsonGarcia
 * @version 0.2 alpha
 *
 */
public class JPDF {

    private ByteArrayOutputStream baos;
    ByteArrayOutputStream documentoFinal;
    private String Pagination;
    private int page = 0;
    private boolean isHeader = false;
    private boolean isFooter = false;
    private float unit;
    private Rectangle format;
    private Document document;
    private PdfWriter writer;
    private float x = 0;
    private float y = 0;
    private float h = 0;
    private float pageBreak = 28.3464566929f;
    private float width;
    private float height;
    // margins
    private float left;
    private float right;
    private float top;
    private float bottom;
    // tipo de letra
    private float fontSize = 12;
    private String fontFamily;
    private int[] fontStyles;
    private Font font;
    private BaseColor textColor;
    // Background color
    private BaseColor backColor;

    private BaseColor color;
    private HashMap fonts;

    // predefined colors
    public static BaseColor DEFAULT_COLOR = new BaseColor(0, 0, 0);
    public static BaseColor DEFAULT_BACKGROUND_COLOR = new BaseColor(255, 255, 255);
    public static BaseColor DEFAULT_TEXT_COLOR = new BaseColor(0, 0, 0);
    private boolean stroke = true;
    private boolean fill = false;

    public JPDF() {
        format = PageSize.A5;
        unit = 1f;
        textColor = new BaseColor(0, 0, 0);
        color = new BaseColor(0, 0, 0);
    }

    public JPDF(String orientation, String format, String unit) {
        // TamaÃ±o de pÃ¡gina
        switch (format) {
            case "Letter":
                this.format = PageSize.LETTER;
                break;
            case "A5":
                this.format = PageSize.A5;
                break;
            case "Legal":
                this.format = PageSize.LEGAL;
                break;
            default:
                this.format = PageSize.A5;
                break;
        }
        // Orientacion
        if (orientation.equals("H")) {
            this.format = this.format.rotate();
        }
        // Unidad de medida
        switch (unit) {
            case "pts":
                this.unit = 1f;
                break;
            case "inch":
                this.unit = 72f;
                break;
            case "cm":
                this.unit = 28.3464566929f;
                break;
            case "mm":
                this.unit = 2.83464566929f;
                break;
            default:
                this.unit = 1f;
                break;
        }
        // colors
        textColor = new BaseColor(0, 0, 0);
        color = new BaseColor(0, 0, 0);
    }

    public void createPDF(String filename) throws Exception {
        document = new Document(format);
        writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        getSizes();
        setMargin();
    }

    public void createPDF() throws Exception {
        document = new Document(format);
        baos = new ByteArrayOutputStream();
        writer = PdfWriter.getInstance(document, baos);
        getSizes();
        setMargin();
    }

    private void setMargin() {
        this.left = 0 * unit;
        this.right = 0 * unit;
        this.top = 0 * unit;
        this.bottom = 0;
        document.setMargins(this.left, this.right, this.top, 0);
    }

    public void setMargin(float left, float right, float top) {
        this.left = left * unit;
        this.right = right * unit;
        this.top = top * unit;
        this.bottom = 0;
        document.setMargins(this.left, this.right, this.top, 0);
    }

    public void addPage() throws DocumentException {
        if (page > 0 && isFooter == false && isHeader == false) {
            isFooter = true;
            this.setXY(0, -(pageBreak - 4) / unit);
            footer();
            isFooter = false;
        }
        if (!document.isOpen()) {
            document.open();
        } else {
            document.newPage();
        }
        setXY(0, 0);
        document.setPageCount(++page);
        if (isFooter == false && isHeader == false) {
            isHeader = true;
            header();
            this.ln();
            isHeader = false;
        }
    }

    public void addFont(String directory, String name) throws DocumentException {
        try {
            if (fonts == null) {
                fonts = new HashMap();
            }
            BaseFont baseFont = BaseFont.createFont(directory, BaseFont.CP1252, true);
            Font font = new Font(baseFont);
            fonts.put(name, font);
        } catch (IOException ex) {
            Logger.getLogger(JPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closePDF() throws DocumentException {
        if (page > 0) {
            isFooter = true;
            this.setY(-(pageBreak - 4) / unit);
            footer();
            isFooter = false;
        }
        document.close();
    }

    public PdfPTable addNumberOfPage(PdfPTable table, int x, int y) {
        table.deleteLastRow();
        Phrase phrase = new Phrase();
        phrase.setFont(font);
        phrase.add(String.format(this.Pagination, x, y));
        table.addCell(phrase);
        return table;
    }

    public PdfPTable numberOfPage(String texto, int align) {
        PdfPTable table = new PdfPTable(1);
        table.setTotalWidth(this.width - this.right - this.left);
        table.setLockedWidth(true);
        table.getDefaultCell().setBorder(0);
        table.getDefaultCell().setFixedHeight(20);
        table.getDefaultCell().setHorizontalAlignment(align);
        this.Pagination = texto;
        return table;
    }

    public ByteArrayOutputStream getPDF() {
        try {
            closePDF();
            return baos;
        } catch (DocumentException ex) {
            return null;
        }
    }

    public ByteArrayOutputStream getPDF(PdfPTable nop, String place) {
        try {
            closePDF(nop, place);
            return documentoFinal;
        } catch (DocumentException ex) {
            return null;
        }
    }

    public void closePDF(PdfPTable nop, String place) throws DocumentException {
        documentoFinal = new ByteArrayOutputStream();
        try {
            closePDF();
            PdfReader reader = new PdfReader(baos.toByteArray());
            // Create a stamper
            PdfStamper stamper = new PdfStamper(reader, documentoFinal);

            // Loop over the pages and add a header to each page
            int n = reader.getNumberOfPages();
            for (int i = 1; i <= n; i++) {
                if (place.toUpperCase().equals("HEADER")) {
                    addNumberOfPage(nop, i, n).writeSelectedRows(0, -1, this.left, this.height,
                            stamper.getOverContent(i));
                } else {
                    addNumberOfPage(nop, i, n).writeSelectedRows(0, -1, this.left, 20, stamper.getOverContent(i));
                }
            }
            // Close the stamper
            stamper.close();
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(JPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cell(String texto) {
        this.cell(texto, 0, "L", "", 0);
    }

    public void cell(String texto, float size) {
        this.cell(texto, size, "L", "", 0);
    }

    public void cell(String texto, float size, String alignment) {
        this.cell(texto, size, alignment, "", 0);
    }

    public void cell(String texto, float size, String alignment, String border) {
        this.cell(texto, size, alignment, border, 0);
    }

    public void cell(String texto, float size, String alignment, String border, int salto) {
        try {
            this.verifyBorder();
            float x = this.x;
            float y = this.y;
            size = size == 0 ? width - x - right : size * unit;

            int elementAlignment = getAlignment(alignment);

            PdfContentByte canvas = writer.getDirectContent();

            PdfPTable table = new PdfPTable(1);
            table.setTotalWidth(size);

            PdfPCell cell = new PdfPCell();

            cell.setNoWrap(true);
            cell.setHorizontalAlignment(elementAlignment);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setPaddingTop(-0.5f);
            cell.setPaddingBottom(3.5f);

            if (fill) {
                cell.setBackgroundColor(backColor);
            }

            if (texto == null || texto.isEmpty()) {
                texto = "\u00a0";
            }

            Phrase phrase = new Phrase();
            phrase.setFont(font);
            phrase.add(texto);
            cell.setPhrase(phrase);
            cell = setBorder(cell, border);
            table.addCell(cell);
            this.h = table.getTotalHeight();
            table.writeSelectedRows(0, -1, x, y, canvas);
            switch (salto) {
                case 0:
                    this.x += size;
                    break;
                case 1:
                    this.y -= this.h;
                    break;
                case 2:
                    this.ln();
                    break;
            }
        } catch (DocumentException ex) {
            Logger.getLogger(JPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void multiCell(String texto) {
        this.multiCell(texto, 0, "J", "");
    }

    public void multiCell(String texto, float size) {
        this.multiCell(texto, size, "J", "");
    }

    public void multiCell(String texto, float size, String alignment) {
        this.multiCell(texto, size, alignment, "");
    }

    public void multiCell(String texto, float size, String alignment, String border) {
        try {
            size = size == 0 ? width - this.x - right : size * unit;
            int elementAlignment;
            if (alignment.isEmpty()) {
                elementAlignment = getAlignment("J");
            } else {
                elementAlignment = getAlignment(alignment);
            }

            PdfContentByte canvas = writer.getDirectContent();

            if (texto == null || texto.isEmpty()) {
                texto = "\u00a0";
            }
            Chunk chunk = new Chunk();
            chunk.append(texto);
            chunk.setSplitCharacter(new SpaceSplitCharacter());

            Phrase phrase = new Phrase();
            phrase.setFont(font);
            phrase.add(chunk);

            boolean[] bord = this.setBorder(border);
            float vh = fontSize * 0.3f;
            float h = fontSize + vh;
            float[] COLUMNS = {this.x + 2, this.y - h, this.x + size - 2, this.y};

            ColumnText ct = new ColumnText(canvas);
            // ct.addText(phrase);
            ct.addText(phrase);
            ct.setAlignment(elementAlignment);
            ct.setExtraParagraphSpace(6);
            ct.setLeading(0, 1.2f);
            int status = ColumnText.START_COLUMN;
            int fila = 0;
            while (ColumnText.hasMoreText(status)) {
                this.verifyBorder();
                COLUMNS[1] = this.y - h + vh - 0.5f;
                COLUMNS[3] = this.y + vh - 0.5f;

                // background color
                if (fill) {
                    boolean current = stroke;
                    stroke = false;
                    this.rect(getX(), getY(), this.getX() + (size / unit), getY() + h / unit);
                    stroke = current;
                }

                ct.setSimpleColumn(COLUMNS[0], COLUMNS[1], COLUMNS[2], COLUMNS[3]);
                ct.setYLine(COLUMNS[3]);
                status = ct.go();
                if (fila == 0 && bord[2] == true) {
                    this.line(getX() - 0.08f, getY(), this.getX() + (size / unit) + 0.08f, getY());
                }
                if (fila >= 0 && bord[0] == true) {
                    this.line(this.getX(), getY() - 0.08f, this.getX(), getY() + h / unit + 0.08f);
                }
                if (fila >= 0 && bord[1] == true) {
                    this.line(this.getX() + (size / unit), getY() - 0.08f, this.getX() + (size / unit),
                            getY() + h / unit + 0.08f);
                }
                if (status == ColumnText.NO_MORE_TEXT && bord[3] == true) {
                    this.line(this.getX() - 0.08f, getY() + h / unit, this.getX() + (size / unit) + 0.08f,
                            getY() + h / unit);
                }
                fila++;
                this.y -= h;
            }
            this.h = h;
            this.ln(0);
        } catch (DocumentException ex) {
            Logger.getLogger(JPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setLineDash(boolean Dash) {

        if (Dash) {
            PdfContentByte canvas = writer.getDirectContent();
            canvas.setLineDash(2, 2);
        } else {
            PdfContentByte canvas = writer.getDirectContent();
            canvas.setLineDash(0f);
        }

    }

    public void line(float i, float j, float k, float l) {
        if (stroke) {
            i *= unit;
            j = (height - top) - (j * unit);
            k *= unit;
            l = (height - top) - (l * unit);

            PdfContentByte canvas = writer.getDirectContent();
            canvas.saveState();
            canvas.setLineWidth(.5f);
            canvas.moveTo(k, l);
            canvas.lineTo(i, j);
            canvas.setColorStroke(color);
            canvas.stroke();
            canvas.restoreState();
        }
    }

    public void rect(float lx, float ly, float rx, float ry) {
        lx *= unit;
        ly = (height - top) - (ly * unit);
        rx = (rx * unit) - lx;
        ry = ((height - top) - (ry * unit)) - ly;

        PdfContentByte canvas = writer.getDirectContent();
        canvas.saveState();
        canvas.setLineWidth(.5f);
        canvas.rectangle(lx, ly, rx, ry);
        if (stroke && fill) {
            canvas.setColorStroke(color);
            canvas.setColorFill(backColor);
            canvas.fillStroke();
        } else if (stroke) {
            canvas.setColorStroke(color);
            canvas.stroke();
        } else if (fill) {
            canvas.setColorFill(backColor);
            canvas.fill();
        }
        canvas.restoreState();
    }

    public void roundRect(float lx, float ly, float rx, float ry, float v) {
        lx *= unit;
        ly = (height - top) - (ly * unit);
        rx = (rx * unit) - lx;
        ry = ((height - top) - (ry * unit)) - ly;

        PdfContentByte canvas = writer.getDirectContent();
        canvas.saveState();
        canvas.setLineWidth(.1f);
        canvas.roundRectangle(lx, ly, rx, ry, v);
        if (stroke && fill) {
            canvas.setColorStroke(color);
            canvas.setColorFill(backColor);
            canvas.fillStroke();
        } else if (stroke) {
            canvas.setColorStroke(color);
            canvas.stroke();
        } else if (fill) {
            canvas.setColorFill(backColor);
            canvas.fill();
        }
        canvas.restoreState();
    }

    public void image(String ruta, float x, float y, float size) {
        try {
            Image imagen = Image.getInstance(ruta);
            float img_height = imagen.getHeight();
            if (size != 0) {
//                imagen.scalePercent(size);
                imagen.scaleAbsoluteWidth(130f);
                imagen.scaleAbsoluteHeight(50f);
                float documentWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                float documentHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
//                imagen.scaleAbsolute(documentWidth - 470, documentHeight - 710);
                img_height *= (size / 100);
            }

            x = x * unit;
            y = y * unit;
            y = height - y - img_height;

            imagen.setAbsolutePosition(x, y);
            document.add(imagen);
        } catch (DocumentException | IOException ex) {
            Logger.getLogger(JPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void image(InputStream image, float x, float y, float size) {
        try {
            Image imagen = Image.getInstance(IOUtils.toByteArray(image));
            float img_height = imagen.getHeight();
            if (size != 0) {
                imagen.scalePercent(size);
                img_height *= (size / 100);
            }
            x = x * unit;
            y = y * unit;
            y = height - y - img_height;
            imagen.setAbsolutePosition(x, y);
            document.add(imagen);
            image.reset();
            image.close();
        } catch (DocumentException | IOException | IllegalArgumentException ex) {
            Logger.getLogger(JPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logo(InputStream image, float x, float y) {
        try {
            Image imagen = Image.getInstance(IOUtils.toByteArray(image));
            float img_height = imagen.getHeight();
            float img_width = imagen.getWidth();
            float size;
            if (img_width > 142) {
                size = 142 / img_width;
                img_width *= size;
                img_height *= size;
            }
            if (img_height > 84) {
                size = 84 / img_height;
                img_height *= size;
                img_width *= size;
            }
            imagen.scaleAbsolute(img_width, img_height);
            x = x * unit;
            y = y * unit;
            y = height - y - img_height;
            imagen.setAbsolutePosition(x, y);
            document.add(imagen);
            image.reset();
            image.close();
        } catch (DocumentException | IOException | IllegalArgumentException ex) {
            Logger.getLogger(JPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verifyBorder() throws DocumentException {
        if (this.x >= width - right) {
            this.ln();
        }
        if (this.y <= pageBreak && this.isFooter == false) {
            float x = (this.x - left) / unit;
            this.addPage();
            this.setX(x);
        } else if (this.y <= bottom) {
            float x = (this.x - left) / unit;
            this.addPage();
            this.setX(x);
        }
    }

    private PdfPCell setBorder(PdfPCell cell, String border) {
        if (stroke) {
            cell.setBorderColor(color);
            int j = border.length();
            String[] borders = new String[j];
            for (int i = 0; i < j; i++) {
                borders[i] = border.substring(i, i + 1);
            }
            if (borders.length == 0) {
                cell.setBorder(0);
            } else {
                cell.setBorder(0);
                for (String b : borders) {
                    switch (b) {
                        case "1":
                            cell.setBorder(15);
                            break;
                        case "0":
                            cell.setBorder(0);
                            break;
                        case "L":
                            cell.enableBorderSide(4);
                            break;
                        case "R":
                            cell.enableBorderSide(8);
                            break;
                        case "T":
                            cell.enableBorderSide(1);
                            break;
                        case "B":
                            cell.enableBorderSide(2);
                            break;
                    }
                }
            }
        }
        return cell;
    }

    private boolean[] setBorder(String border) {
        int j = border.length();
        String[] borders = new String[j];
        for (int i = 0; i < j; i++) {
            borders[i] = border.substring(i, i + 1);
        }
        boolean[] bord = new boolean[4];
        if (borders.length == 0) {
            bord[0] = false;
            bord[1] = false;
            bord[2] = false;
            bord[3] = false;
        } else {
            bord[0] = false;
            bord[1] = false;
            bord[2] = false;
            bord[3] = false;
            for (String b : borders) {
                switch (b) {
                    case "1":
                        bord[0] = true;
                        bord[1] = true;
                        bord[2] = true;
                        bord[3] = true;
                        break;
                    case "0":
                        bord[0] = false;
                        bord[1] = false;
                        bord[2] = false;
                        bord[3] = false;
                        break;
                    case "L":
                        bord[0] = true;
                        break;
                    case "R":
                        bord[1] = true;
                        break;
                    case "T":
                        bord[2] = true;
                        break;
                    case "B":
                        bord[3] = true;
                        break;
                }
            }
        }
        return bord;
    }

    public void setColor(BaseColor color) {
        this.color = color;
    }

    public void setColor(int x, int y, int z) {
        setColor(new BaseColor(x, y, z));
    }

    public void setBackgroundColor(BaseColor backColor) {
        this.backColor = backColor;
    }

    public void setBackgroundColor(int x, int y, int z) {
        setBackgroundColor(new BaseColor(x, y, z));
    }

    public void setTextColor(BaseColor fontColor) {
        this.textColor = fontColor;
        font.setColor(fontColor);
    }

    public void setTextColor(int x, int y, int z) {
        setTextColor(new BaseColor(x, y, z));
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public void setStroke(boolean stroke) {
        this.stroke = stroke;
    }

    public void setXY(float x, float y) {
        setX(x);
        setY(y);
    }

    public void setX(float x) {
        if (x < 0) {
            x = (width - right) + (x * unit);
        } else {
            x = left + (x * unit);
        }
        this.x = x;
    }

    public void setY(float y) {
        if (y < 0) {
            y = bottom - (y * unit);
        } else {
            y = (height - top) - (y * unit);
        }
        this.y = y;
    }

    public float getX() {
        return this.x / unit;
    }

    public float getY() {
        float totalY = this.y + top;
        float position = height - totalY;
        return position < 0 ? 0 : position / unit;
    }

    public int getPageNo() {
        return document.getPageNumber();
    }

    public void setFont(String name, String style) {
        this.setFont(name, style, 0);
    }

    public void setFont(String name, String style, float size) {
        fontSize = size == 0 ? fontSize : size;
        if (name.isEmpty()) {
            font.setSize(fontSize);
            font.setColor(textColor);
            font.setStyle(Font.NORMAL);
        } else if (fonts == null || !fonts.containsKey(name)) {
            this.setFont(name);
        } else {
            font = (Font) fonts.get(name);
            font.setSize(fontSize);
            font.setColor(textColor);
            font.setStyle(Font.NORMAL);
        }
        setFontStyle(style);
        if (fontStyles != null) {
            for (int i : fontStyles) {
                int old = font.getStyle();
                font.setStyle(old | i);
            }
        }
    }

    private void setFont(String family) {
        switch (family) {
            case "COURIER":
                fontFamily = FontFactory.COURIER;
                break;
            case "HELVETICA":
                fontFamily = FontFactory.HELVETICA;
                break;
            case "TIMES":
                fontFamily = FontFactory.TIMES;
                break;
            default:
                fontFamily = FontFactory.TIMES;
                break;
        }
        font = FontFactory.getFont(fontFamily, fontSize, Font.NORMAL, textColor);
    }

    private void setFontStyle(String style) {
        fontStyles = null;
        int j = style.length();
        String[] styles = new String[j];
        for (int i = 0; i < j; i++) {
            styles[i] = style.substring(i, i + 1);
        }
        int totalStyles = styles.length;
        if (totalStyles != 0) {
            fontStyles = new int[totalStyles];
            for (int i = 0; i < totalStyles; i++) {
                switch (styles[i]) {
                    case "B":
                        fontStyles[i] = Font.BOLD;
                        break;
                    case "I":
                        fontStyles[i] = Font.ITALIC;
                        break;
                    case "T":
                        fontStyles[i] = Font.STRIKETHRU;
                        break;
                    case "U":
                        fontStyles[i] = Font.UNDERLINE;
                        break;
                }
            }
        }
    }

    public void setPageBreak(float i) {
        this.pageBreak = i * unit;
    }

    private int getAlignment(String alignment) {
        int element;
        switch (alignment) {
            case "L":
                element = Element.ALIGN_LEFT;
                break;
            case "R":
                element = Element.ALIGN_RIGHT;
                break;
            case "C":
                element = Element.ALIGN_CENTER;
                break;
            case "J":
                element = Element.ALIGN_JUSTIFIED;
                break;
            default:
                element = Element.ALIGN_LEFT;
                break;
        }
        return element;
    }

    private void getSizes() {
        Rectangle pageSize = document.getPageSize();
        width = pageSize.getWidth();
        height = pageSize.getHeight();
    }

    public void ln() throws DocumentException {
        this.x = left;
        this.y -= this.h;
    }

    public void ln(float valor) {
        this.x = left;
        this.y -= valor;
    }

    public void header() throws DocumentException {
        // Implementar
    }

    public void footer() throws DocumentException {
        // Implementar
    }

    public void body() throws DocumentException {
        // Implementar
    }

    // Clase para evitar salto de linea con el caracter - (Guion medio - hyphen)
    public class SpaceSplitCharacter implements SplitCharacter {

        @Override
        public boolean isSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck) {
            char c;
            if (ck == null) {
                c = cc[current];
            } else {
                c = (char) ck[Math.min(current, ck.length - 1)].getUnicodeEquivalent(cc[current]);
            }
            return (c <= ' ');
        }
    }

    public void printText(String text, float x, float y, float rotation) {
        try {
            PdfContentByte canvas = writer.getDirectContent();

            if (text == null || text.isEmpty()) {
                text = "\u00a0";
            }
            Chunk chunk = new Chunk();
            chunk.append(text);
            chunk.setSplitCharacter(new SpaceSplitCharacter());

            Phrase phrase = new Phrase();
            phrase.setFont(font);
            phrase.add(chunk);

//            boolean[] bord = this.setBorder("0");
//            float vh = fontSize * 0.3f;
//            float h = fontSize + vh;
//            
//            //background color
//            if (fill){
//                boolean current = stroke;
//                stroke = false;
//                this.rect(getX(), getY(), this.getX() + (size/unit), getY() + h/unit);
//                stroke = current;
//            }
            x = x * unit;
            y = this.height - (y * unit);

            ColumnText.showTextAligned(canvas, getAlignment("J"), phrase, x, y, rotation);

        } catch (Exception ex) {
            Logger.getLogger(JPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
