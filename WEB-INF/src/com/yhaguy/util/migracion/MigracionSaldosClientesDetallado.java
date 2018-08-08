package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;

import com.coreweb.util.Misc;


public class MigracionSaldosClientesDetallado {

	static Misc m = new Misc();
	
	static String archivoClienteDet = "./WEB-INF/docs/migracion/Cliente-Proveedor-Cuentas/Cliente-Proveedor/Saldos/Clientes/Clientes_Saldos_Detallado_GE.csv";
	

	// ========================================================
	// todo esto es para poder acceder de forma fácil a las columnas de los datos
	
	static String[] colNombreDet = {
		"IDPERSONA", "FECHA", "VENCIMIENTO", "LOCAL", "BOCA", "NROMOVIMIENTO", "OBSERVACION", "FACTURA",
		"PERSONA", "RUC", "TELEFONO", "DIRECCION", "ESTADO", "IDMONEDA", "LIMITECREDITO", "TOTALEXENTA",
		"TOTALGRAVADA", "TOTALIVA", "TOTAL" };

	static Hashtable<String, Integer> colNombresPos = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colNombreDet.length; i++) {
			String c = colNombreDet[i];
			colNombresPos.put(c, i);
		}
	}

	static String getDatoDetalle(String col, String[] dato) {
		return getCol(colNombresPos, col, dato);
	}

	static double getDatoDoubleDetalle(String col, String[] dato) {
		String d = getCol(colNombresPos, col, dato);
		d = d.replaceAll("\\.", "");
		String dd = d.replace(',', '.');
		double out = 0;
		if (d.compareTo("Null") != 0) {
			out = Double.parseDouble(dd.trim());
		}		
		return out;
	}
	
	static java.util.Date getDatoDateDetalle(String col, String[] dato) {
		String f = getCol(colNombresPos, col, dato);
		java.util.Date fh = m.ParseFecha(f, "MM/dd/yyyy HH:mm:ss");
		return fh;
	}

	static String getCol(Hashtable<String, Integer> tabla, String col,String[] dato) {
		int p = tabla.get(col);
		String out = (dato[p]).trim();
		out = out.replaceAll("\"", "");
		return out;
	}

	static BufferedReader abrirArchivo() throws Exception {
		String ff = archivoClienteDet;
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

	public static void migrarDatos(Hashtable<String, Object[]> ctaCteTodo) throws Exception {

		setingInicial();
		String linea = "";

		BufferedReader archivo = abrirArchivo();

		linea = archivo.readLine();

		int i = 0;
		while (archivo.ready() == true) {
			i++;

			linea = archivo.readLine();
			String[] datos = parserLinea(linea);

			String IDPERSONA = getDatoDetalle("IDPERSONA", datos);
			java.util.Date FECHA = getDatoDateDetalle("FECHA", datos);
			java.util.Date VENCIMIENTO = getDatoDateDetalle("VENCIMIENTO", datos);
			String LOCAL = getDatoDetalle("LOCAL", datos);
			String BOCA = getDatoDetalle("BOCA", datos);
			String NROMOVIMIENTO = getDatoDetalle("NROMOVIMIENTO", datos);
			String OBSERVACION = getDatoDetalle("OBSERVACION", datos);
			String FACTURA = getDatoDetalle("FACTURA", datos);
			String PERSONA = getDatoDetalle("PERSONA", datos);
			String RUC = getDatoDetalle("RUC", datos);
			String TELEFONO = getDatoDetalle("TELEFONO", datos);
			String DIRECCION = getDatoDetalle("DIRECCION", datos);
			String IDMONEDA = getDatoDetalle("IDMONEDA", datos);
			double TOTALEXENTA = getDatoDoubleDetalle("TOTALEXENTA", datos);
			double TOTALGRAVADA = getDatoDoubleDetalle("TOTALGRAVADA", datos);
			double TOTALIVA = getDatoDoubleDetalle("TOTALIVA", datos);
			double TOTAL = getDatoDoubleDetalle("TOTAL", datos);
			
			// buscar la ctaCte que le pertene
			Object[] datoCtaCte = ctaCteTodo.get(IDPERSONA);
			if (datoCtaCte == null){
				System.err.println("No encontró este ID:"+IDPERSONA);
			}else{
				
				ArrayList<String[]> ldatos = (ArrayList<String[]>) datoCtaCte[1];
				ldatos.add(datos);
			}
			
		}
	}

	public static void main(String[] args) throws Exception {
		migrarDatos(null);
	}
}
