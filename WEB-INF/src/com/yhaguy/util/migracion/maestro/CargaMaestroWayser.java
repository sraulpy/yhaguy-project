package com.yhaguy.util.migracion.maestro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.coreweb.domain.Domain;
import com.yhaguy.domain.RegisterDomain;

public class CargaMaestroWayser {

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
	public void cargaWayser() throws Exception {

		// ... leer la tabla de wayser
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/wayser";
		Connection con = DriverManager.getConnection(url, "postgres","postgres");
		Statement rs = con.createStatement();

		ResultSet rss = rs.executeQuery("select * from wayser ");

		// poner y ejecutar el query
		int i = 0;
		while (rss.next()) {

			// sacar la info que necesitas
			String codigoproveedor = getStr(rss.getString("codigoproveedor"));
			String fabricante = getStr(rss.getString("fabricante"));
			String codigoOriginal = getStr(rss.getString("codigoOriginal"));
			String referencia = getStr(rss.getString("referencia"));
			String peso = getStr(rss.getString("peso"));
			String descripcion = getStr(rss.getString("descripcion"));;
			String datoExtra =  referencia +" - "+fabricante  ;
			
			System.out.println(codigoproveedor + " - " + codigoOriginal + " - "+descripcion +" - ");

	
			// guardar en maestro la info de wayser
			Maestro maestro = new Maestro();

			maestro.setCodigoProveedor(codigoproveedor);
			maestro.setCodigoOriginal(codigoOriginal);
			maestro.setDescripcion(descripcion);
			maestro.setDatoExtra(datoExtra);
			maestro.setPeso(peso);
			maestro.setMarca("wayser");
            maestro.setFamilia("Respuestos");
			maestro.setLinea("");
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
		CargaMaestroWayser cw = new CargaMaestroWayser();
		cw.cargaWayser();
	}
		
	
}
  