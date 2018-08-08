package com.yhaguy.util.migracion.kahveci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class kahveci_jedi {

	public void getJedi() throws Exception {	 

		
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/kahveci";
		Connection con = DriverManager.getConnection(url, "postgres", "postgres");
		  Statement s = con.createStatement();

		ResultSet rs = s.executeQuery("SELECT  \"CODIGO_ORIGINAL\" FROM  JEDI");

		
		while (rs.next()) {
			String cod = rs.getString("CODIGO_ORIGINAL").trim();
			
			
			Statement ss = con.createStatement();
			ResultSet rss = ss.executeQuery("SELECT  \"CODIGO_PROVEEDOR\", \"CODIGO_ORIGINAL\", \"DESCRIPCION\","
					+ " \"TIPO_MOTOR\", \"MARCA\" FROM  KAHVECI WHERE TRIM(\"CODIGO_ORIGINAL\") like '%" + cod +"%'");
			
	
			while (rss.next()) {
			
			System.out.println(rss.getString("CODIGO_PROVEEDOR").trim() + " - " + rss.getString("CODIGO_ORIGINAL").trim()+ " - " + rss.getString("DESCRIPCION").trim() + " - " + rss.getString("TIPO_MOTOR").trim() + " - " + rss.getString("MARCA").trim());
			
			}			
			rss.close();
			ss.close();
		}
		//System.out.println("Encontro en total: " + count);
		
		rs.close();
		s.close();
		con.close();
	}


	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		kahveci_jedi p = new kahveci_jedi();
		p.getJedi();
	}
}
