package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import com.coreweb.util.Misc;




public class MigracionPrecioBaseMayorista {

	static Misc m = new Misc();
	
	public static String articuloPrecioBaseCSV = "./WEB-INF/docs/migracion/articulos/Articulo/Precio_Base_Mayorista_GE.csv";

	// ========================================================
	// todo esto es para poder acceder de forma fácil a las columnas de los datos
	
	static String[] colNombreDet = { "IDARTICULO", "PRECIO_BASE_MAYORISTA" };
	
		


	static Hashtable<String, Integer> colNombresPos = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colNombreDet.length; i++) {
			String c = colNombreDet[i];
			colNombresPos.put(c, i);
		}
	}

	static String getDato(String col, String[] dato) {
		return getCol(colNombresPos, col, dato);
	}

	static double getDatoDouble(String col, String[] dato) {
		String d = getCol(colNombresPos, col, dato);
		String dd = d.replace(',', '.');
		double out = 0;
		if (d.compareTo("Null") != 0) {
			out = Double.parseDouble(dd.trim());
		}		
		return out;
	}
	
	static java.util.Date getDatoDate(String col, String[] dato) {
		String f = getCol(colNombresPos, col, dato);
		java.util.Date fh = m.stringMesDiaAnoToDate(f);
		return fh;
	}

	static String getCol(Hashtable<String, Integer> tabla, String col,
			String[] dato) {
		int p = tabla.get(col);
		String out = (dato[p]).trim();
		out = out.replaceAll("\"", "");
		return out;
	}

	static BufferedReader abrirArchivo() throws Exception {
		String ff = articuloPrecioBaseCSV;
		File f = new File(ff);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		return entrada;
	}

	static String[] parserLinea(String linea) {
		// divide la línea, que es un String, en todas las columnas que hay (o
		// campos).
		// En este caso, considera "tab" como separador
		String[] dato = linea.split("\t");
		return dato;
	}

	// ========================================================

	public static void migrarDatos() throws Exception {

		setingInicial();
		String linea = "";

		BufferedReader archivo = abrirArchivo();

		linea = archivo.readLine();

		int i = 0;
		while (archivo.ready() == true) {
			i++;

			linea = archivo.readLine();
			String[] datos = parserLinea(linea);

			String IDARTICULO = getDato("IDARTICULO", datos);
			double PRECIO_BASE_MAYORISTA = getDatoDouble("PRECIO_BASE_MAYORISTA", datos);
			
			

			System.out.println(i + ") " + IDARTICULO + " - " +  PRECIO_BASE_MAYORISTA );
		}
	}

	public static void main(String[] args) throws Exception {
		migrarDatos();
	}
}
