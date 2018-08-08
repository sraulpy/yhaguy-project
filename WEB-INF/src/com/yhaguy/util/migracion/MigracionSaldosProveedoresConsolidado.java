package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;

public class MigracionSaldosProveedoresConsolidado {

	static String archivoProveedorCon = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/Saldos/Proveedores/Proveedores_Saldos_Consolidado_GE.csv";
	
	public static Hashtable<String, Object[]> ctaCteProveedoresTodo = new Hashtable<>();
										
	// ========================================================
	// todo esto es para poder acceder de forma fácil a las columnas de los datos
	
	static String[] colNombreCon = {
		"IDPERSONA", "PERSONA", "IDMONEDA", "TOTALEXENTA", "TOTALGRAVADA", 
		"TOTALIVA", "TOTAL" };
							

	static Hashtable<String, Integer> colNombresPos = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colNombreCon.length; i++) {
			String c = colNombreCon[i];
			colNombresPos.put(c, i);
		}
	}

	static String getDato(String col, String[] dato) {
		return getCol(colNombresPos, col, dato);
	}

	static double getDatoDouble(String col, String[] dato) {
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
		String ff = archivoProveedorCon;
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

			String IDPERSONA = getDato("IDPERSONA", datos);
			String PERSONA = getDato("PERSONA", datos);
			String IDMONEDA = getDato("IDMONEDA", datos);
			double TOTALEXENTA = getDatoDouble("TOTALEXENTA", datos);
			double TOTALGRAVADA = getDatoDouble("TOTALGRAVADA", datos);
			double TOTALIVA = getDatoDouble("TOTALIVA", datos);
			double TOTAL = getDatoDouble("TOTAL", datos);
			System.out.printf("%-5s%-50%-20s%-20s%-20s%-20s\n",IDPERSONA,PERSONA,IDMONEDA,TOTALEXENTA,TOTALGRAVADA,TOTALIVA,TOTAL);
			
			Object[] detalle = new Object[2];
			detalle[0] = datos;
			detalle[1] = new ArrayList<>();
			ctaCteProveedoresTodo.put(IDPERSONA, detalle);
		
		}
	}
	
	public static void cargaHastTableConsolodadoYDetalle() throws Exception {
		migrarDatos();
		MigracionSaldosProveedoresDetallado.migrarDatos(ctaCteProveedoresTodo);
	}

	public static void main(String[] args) throws Exception {
		migrarDatos();
	}
}
