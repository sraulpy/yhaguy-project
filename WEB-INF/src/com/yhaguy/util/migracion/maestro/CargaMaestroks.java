package com.yhaguy.util.migracion.maestro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.coreweb.domain.Domain;
import com.yhaguy.domain.RegisterDomain;

public class CargaMaestroks {

	private RegisterDomain rr = RegisterDomain.getInstance();
	public boolean grabar = true;

	public void grabar(Domain d) throws Exception {
		if (grabar == true) {
			rr.saveObject(d, "migra");
		}
	}

	String getStr(String s) {
		if (s == null) {
			return "";
		}
		return s.trim();
	}

	// cargar los datos en la base de datos
	public void cargaks() throws Exception {

		// ... leer la tabla de ks
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/ks";
		Connection con = DriverManager.getConnection(url, "postgres",
				"postgres");
		Statement rs = con.createStatement();

		ResultSet rss = rs.executeQuery("select * from ks ");

		// poner y ejecutar el query
		int i = 0;
		while (rss.next()) {

			// sacar la info que necesitas
			String codigo = getStr(rss.getString("codigo"));
			String codigoProveedor = getStr(rss.getString("codigoProveedor"));
			String motor = getStr(rss.getString("motor"));
			String aplicacion = getStr(rss.getString("aplicacion"));
			String referencia = getStr(rss.getString("aplicacion"));
			String linea = getStr(rss.getString("linea"));
			String familia = ("" + rss.getString("familia")).trim();
			String medida = ("" + rss.getString("medida")).trim();
			String descripcion = linea +" - "+ familia +" - "+  medida ;
			String datoExtra = aplicacion+" - "+ referencia +" - "+motor  ;
			
			System.out.println(codigoProveedor + " - " + codigo + " - " + motor
					+ " - ");

	
			// guardar en maestro la info de ks
			Maestro maestro = new Maestro();

			maestro.setCodigoProveedor(codigoProveedor);
			maestro.setCodigoOriginal(codigo);
			maestro.setDescripcion(descripcion);
			maestro.setDatoExtra(datoExtra);
			maestro.setMarca("ks");
			maestro.setFamilia("Repuestos");
			maestro.setLinea("Pesada");
			maestro.setParte("");
			
			

			try {
				grabar(maestro);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws Exception {
		// invocar el inicio
		CargaMaestroks cks = new CargaMaestroks();
		cks.cargaks();
	}

}
