package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import com.coreweb.util.Misc;

public class MigracionSectorUbicacionesArticulos {

	static Misc m = new Misc();
	
	public static Hashtable<String, Object[]> ctaCteTodo = new Hashtable<>();

	static String archivoArticuloSector = "./WEB-INF/docs/migracion/articulos/Articulo/Sector_Articulo_GE.csv";
	
	static String archivoArticuloUbicaciones = "./WEB-INF/docs/migracion/articulos/Articulo/Ubicacion_Articulos_GE.csv";
	

	// ========================================================
	// todo esto es para poder acceder de forma fácil a las columnas de los
	// datos

	static String[] colArticuloSector = { "IDSECTOR", "DESCRIPCION" };
	
	static String[] colArticuloUbicacion = { "IDDEPOSITO", "IDARTICULO", "IDSECTOR" };
			


	static Hashtable<String, Integer> colNombresPosSector = new Hashtable<>();
	
	static Hashtable<String, Integer> colNombresPosUbicacion = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colArticuloSector.length; i++) {
			String c = colArticuloSector[i];
			colNombresPosSector.put(c, i);
		}
		
		for (int i = 0; i < colArticuloUbicacion.length; i++) {
			String c = colArticuloUbicacion[i];
			colNombresPosUbicacion.put(c, i);
		}
	}
	

	static String getDatoSector(String col, String[] datoSector) {
		return getColSector(colNombresPosSector, col, datoSector);
	}

	static String getDatoUbicacion(String col, String[] datoUbicacion) {
		return getColUbicacion(colNombresPosUbicacion, col, datoUbicacion);
	}

	static String getColSector(Hashtable<String, Integer> tabla, String col,
			String[] datoSector) {
		int p = tabla.get(col);
		String out = (datoSector[p]).trim();
		out = out.replaceAll("\"", "");
		return out;
	}
	
	static String getColUbicacion(Hashtable<String, Integer> tabla, String col,
			String[] datoUbicacion) {
		int p = tabla.get(col);
		String out = (datoUbicacion[p]).trim();
		out = out.replaceAll("\"", "");
		return out;
	}

	static BufferedReader abrirArchivoSector() throws Exception {
		String ff = archivoArticuloSector;
		File f = new File(ff);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		return entrada;
	}
	
	static BufferedReader abrirArchivoUbicacion() throws Exception {
		String ff = archivoArticuloUbicaciones;
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

	public static void migrarDatosSector() throws Exception {

		setingInicial();
		String linea = "";

		BufferedReader archivo = abrirArchivoSector();

		linea = archivo.readLine();

		int i = 0;
		while (archivo.ready() == true) {
			i++;

			linea = archivo.readLine();
			String[] datos = parserLinea(linea);

			String IDSECTOR = getDatoSector("IDSECTOR", datos);
			String DESCRIPCION = getDatoSector("DESCRIPCION", datos);
			

			
			System.out.println(i + ") " + IDSECTOR + " - " + DESCRIPCION);

		}
		System.out.println("\n");
		System.out.println("==============================================================================");
		System.out.println("\n");
	}


	public static void migrarDatosUbicacion() throws Exception {

		setingInicial();
		String linea = "";

		BufferedReader archivo = abrirArchivoUbicacion();

		linea = archivo.readLine();

		int i = 0;
		while (archivo.ready() == true) {
			i++;

			linea = archivo.readLine();
			String[] datos = parserLinea(linea);

			String IDDEPOSITO = getDatoUbicacion("IDDEPOSITO", datos);
			String IDARTICULO = getDatoUbicacion("IDARTICULO", datos);
			String IDSECTOR = getDatoUbicacion("IDSECTOR", datos);
			

			
			System.out.println(i + ") " + IDDEPOSITO + " - " + IDARTICULO + " - " + IDSECTOR);

		}
	}
	
	public static void main(String[] args) throws Exception {

		migrarDatosSector();
		migrarDatosUbicacion();

	}
}
