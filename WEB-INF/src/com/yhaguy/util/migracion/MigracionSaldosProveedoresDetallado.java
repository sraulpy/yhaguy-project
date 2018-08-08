package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;

import com.coreweb.util.Misc;


public class MigracionSaldosProveedoresDetallado {

	static Misc m = new Misc();
	
	public static Hashtable<String, Object[]> ctaCteTodo = new Hashtable<>();
	
	static String archivoProveedorDet = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/Saldos/Proveedores/Proveedores_Saldos_Detallado_GE.csv";
	

	// ========================================================
	// todo esto es para poder acceder de forma fácil a las columnas de los datos
	
	static String[] colNombreDet = {
		"IDPERSONA", "PERSONA", "RUC", "FECHA", "VENCIMIENTO", "LOCAL", "BOCA", "NROMOVIMIENTO",
		"OBSERVACION", "CONCEPTO", "FACTURA", "TELEFONO", "DIRECCION", "IDMONEDA", "TOTALEXENTA",
		"TOTALGRAVADA", "TOTALIVA", "TOTAL" };


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
		d= d.replaceAll("\\.", "");
		// las comas son los separadores decimales
		String dd = d.replace(',', '.');
		double out = 0;
		if (d.compareTo("Null") != 0) {
			out = Double.parseDouble(dd.trim());
		}		
		return out;
	}
	
	static java.util.Date getDatoDate(String col, String[] dato) {
		String f = getCol(colNombresPos, col, dato);
		java.util.Date fh = m.ParseFecha(f, "MM/dd/yyyy HH:mm:ss");
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
		String ff = archivoProveedorDet;
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

	public static void migrarDatos(Hashtable<String, Object[]> todo) throws Exception {

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
			String RUC = getDato("RUC", datos);
			java.util.Date FECHA = getDatoDate("FECHA", datos);
			java.util.Date VENCIMIENTO = getDatoDate("VENCIMIENTO", datos);
			String LOCAL = getDato("LOCAL", datos);
			String BOCA = getDato("BOCA", datos);
			String NROMOVIMIENTO = getDato("NROMOVIMIENTO", datos);
			String OBSERVACION = getDato("OBSERVACION", datos);
			String CONCEPTO = getDato("CONCEPTO", datos);
			String FACTURA = getDato("FACTURA", datos);
			String TELEFONO = getDato("TELEFONO", datos);
			String DIRECCION = getDato("DIRECCION", datos);
			String IDMONEDA = getDato("IDMONEDA", datos);
			double TOTALEXENTA = getDatoDouble("TOTALEXENTA", datos);
			double TOTALGRAVADA = getDatoDouble("TOTALGRAVADA", datos);
			double TOTALIVA = getDatoDouble("TOTALIVA", datos);
			double TOTAL = getDatoDouble("TOTAL", datos);
			
			Object[] datoCtaCte = todo.get(IDPERSONA);
			if (datoCtaCte == null){
				System.err.println("No encontró este ID:"+IDPERSONA);
			}else{	
				ArrayList<String[]> ldatos = (ArrayList<String[]>) datoCtaCte[1];
				ldatos.add(datos);
			}
		}
	}
}
