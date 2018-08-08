package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import com.coreweb.util.Misc;

public class MigracionSaldosClientesConsolidado {

	static Misc m = new Misc();
	
	public static Hashtable<String, Object[]> ctaCteTodo = new Hashtable<>();

	static String archivoClienteCon = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/Saldos/Clientes/Clientes_Saldos_Consolidado_GE.csv";

	// ========================================================
	// todo esto es para poder acceder de forma fácil a las columnas de los
	// datos

	static String[] colNombreCon = { "IDPERSONA", "PERSONA", "IDMONEDA",
			"TOTALEXENTA", "TOTALGRAVADA", "TOTALIVA", "TOTAL", "LIMITECREDITO" };

	static Hashtable<String, Integer> colNombresPos = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colNombreCon.length; i++) {
			String c = colNombreCon[i];
			colNombresPos.put(c, i);
		}
	}
	
	static java.util.Date getDatoDateConsolidado(String col, String[] dato) {
		String f = getCol(colNombresPos, col, dato);
		java.util.Date fh = m.stringMesDiaAnoToDate(f);
		return fh;
	}

	static String getDatoConsolidaddo(String col, String[] dato) {
		return getCol(colNombresPos, col, dato);
	}

	static double getDatoDoubleConsolidaddo(String col, String[] dato) {
		String d = getCol(colNombresPos, col, dato);
		d = d.replaceAll("\\.", "");
		String dd = d.replace(',', '.');
		double out = 0;
		if (d.compareTo("Null") != 0) {
			out = Double.parseDouble(dd.trim());
		}
		return out;
	}

	static String getCol(Hashtable<String, Integer> tabla, String col,
			String[] dato) {
		int p = tabla.get(col);
		String out = (dato[p]).trim();
		out = out.replaceAll("\"", "");
		return out;
	}

	static BufferedReader abrirArchivo() throws Exception {
		String ff = archivoClienteCon;
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

	public static void migrarDatosConsolidado() throws Exception {

		setingInicial();
		String linea = "";

		BufferedReader archivo = abrirArchivo();

		linea = archivo.readLine();

		int i = 0;
		while (archivo.ready() == true) {
			i++;

			linea = archivo.readLine();
			String[] datos = parserLinea(linea);

			String IDPERSONA = getDatoConsolidaddo("IDPERSONA", datos);
			String PERSONA = getDatoConsolidaddo("PERSONA", datos);
			String IDMONEDA = getDatoConsolidaddo("IDMONEDA", datos);
			double TOTALEXENTA = getDatoDoubleConsolidaddo("TOTALEXENTA", datos);
			double TOTALGRAVADA = getDatoDoubleConsolidaddo("TOTALGRAVADA",	datos);
			double TOTALIVA = getDatoDoubleConsolidaddo("TOTALIVA", datos);
			double TOTAL = getDatoDoubleConsolidaddo("TOTAL", datos);
			double LIMITECREDITO = getDatoDoubleConsolidaddo("LIMITECREDITO",datos);

			System.out.printf("%-5s%-50s%-5s%-20s%-20s%-20s%-20s\n",
					IDPERSONA, PERSONA, IDMONEDA, TOTALEXENTA, TOTALGRAVADA,
					TOTALIVA, TOTAL, LIMITECREDITO);
			/*
			 * voy poniendo en el hash todos los datos de las cuentas
			 * corrientes, el ID es el IDPERSONA
			 */
			
			Object[] detalle = new Object[2];
			detalle[0] = datos;
			detalle[1] = new ArrayList<>();
			ctaCteTodo.put(IDPERSONA, detalle);

		}
	}

	public static void cargaHastTableConsolodadoYDetalle() throws Exception {
		migrarDatosConsolidado();
		MigracionSaldosClientesDetallado.migrarDatos(ctaCteTodo);
	}

	public static void main(String[] args) throws Exception {

		migrarDatosConsolidado();
		//cargaHastTableConsolodadoYDetalle();

	}
}
