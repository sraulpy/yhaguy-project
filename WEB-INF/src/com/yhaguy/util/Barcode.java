package com.yhaguy.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfWriter;
import com.yhaguy.Configuracion;

public class Barcode {

	/**
	 * genera el barcode pdf
	 */
	public static void generarBarcode(String codigo, String descripcion) throws FileNotFoundException,
			DocumentException {

		Document document = new Document(new Rectangle(PageSize.A4));
		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(Configuracion.pathBarcodes + codigo.replace("/", "-") + ".pdf"));

		document.open();
		document.add(new Paragraph(descripcion));

		Barcode128 code128 = new Barcode128();
		code128.setGenerateChecksum(true);
		code128.setBarHeight(80);
		code128.setX(2);
		code128.setCode(codigo);

		document.add(code128.createImageWithBarcode(writer.getDirectContent(),
				null, null));
		document.close();
	}

}
