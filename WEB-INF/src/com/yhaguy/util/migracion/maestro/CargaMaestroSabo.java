package com.yhaguy.util.migracion.maestro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.coreweb.domain.Domain;
import com.yhaguy.domain.RegisterDomain;

public class CargaMaestroSabo {

	
	
	private RegisterDomain rr = RegisterDomain.getInstance();
	public boolean grabar = true;

	public void grabar(Domain d) throws Exception {
		if (grabar == true) {
			rr.saveObject(d, "migra");
		}
	}

	
	String getStr(String s){
		if (s == null){
			return "";
		}
		return s.trim();
	}


	public void CargaMaestroSabo() throws Exception {
		
		
		// ... leer la tabla de Sabo
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/sabo";
		Connection con = DriverManager.getConnection(url, "postgres",
				"postgres");
		Statement rs = con.createStatement();

		String q2Template =  "select p.codigoproduto,  a.descricaoaplicacaoe as descripcion ,"
				+ "a.complementoaplicacaoe as complemento,"
				+ " f.descricaofabricante as fabricante "
				+ " from(produto as p  left join produto_aplicacao pa"
				+ "  on p.codigoproduto = pa.codigoproduto )"
				+ " left join aplicacao a on a.codigoaplicacao = pa.codigoaplicacao"
				+ " left join fabricante f on a.codigofabricante = f.codigofabricante "
				+ " where p.codigoproduto = 'XXX'"
				+ " order by p.codigoproduto, f.codigofabricante , a.codigoaplicacao   ";

		
		
		
		String q1 = "select p.codigoproduto,  MIN(p.numeroproduto)as numeroproduto,"
				+ " MIN(p.descricaoprodutoe)as descripcion,"
				+ " MIN(p.arquivofotoproduto)as imagen ,"
				+ " string_agg(r.numeroproduto , ' - ')as codigooriginal"
				+ " from (produto as p  left join referenciacruzada r"
				+ " on p.codigoproduto = r.codigoproduto )"
				+ "group by p.codigoproduto order by p.codigoproduto  " ;
		        

		ResultSet rss = rs.executeQuery(q1);

		int i = 0;
		while (rss.next()) {

			// sacar la info que necesitas
			String codigoproduto = getStr(rss.getString("codigoproduto"));
	     	String codigooriginal = getStr(rss.getString("codigooriginal"));
			String numeroproduto = getStr(rss.getString("numeroproduto"));
		    String descricaoprodutoe =getStr(rss.getString("descripcion"));
			String arquivofotoproduto = getStr(rss.getString("imagen"));
			

		

		 System.out.println(codigoproduto + " - " + codigooriginal) ;		 
			 
			 
			String q2 = q2Template.replaceAll("XXX", codigoproduto);

		//	System.out.println(q2);

			Statement rs2 = con.createStatement();
			ResultSet rss2 = rs2.executeQuery(q2);

			
			
			String datoExtra = "";
			while (rss2.next()) {

		    	String descripcion = getStr(rss2.getString("descripcion"));
		    	String complemento = getStr(rss2.getString("complemento"));
		    	String fabricante =  getStr(rss2.getString("fabricante"));
				
		    	String linea = "";
		    	boolean siTiene = false;

		    	if (descripcion.length() > 0){
		    		siTiene = true;
		    		linea += descripcion;
		    	}
		    	
		    	if (complemento.length() > 0){
		    		if (siTiene == true){
		    			linea += " - ";
		    		}
		    		linea += complemento;
		    		siTiene = true;
		    	}
		    	
		    	if (fabricante.length() > 0){
		    		if (siTiene == true){
		    			linea += " - ";
		    		}
		    		linea += fabricante;
		    		siTiene = true;
		    	}
		    	
		    	if (siTiene == true){
	    			linea = "["+linea+"]";
	    		}
		    	
				datoExtra = datoExtra + linea;
			}
		     	System.out.println(datoExtra);

			
			// grabar en maestro
			Maestro maestro = new Maestro();
		    maestro.setCodigoProveedor(numeroproduto);
	        maestro.setCodigoOriginal(codigooriginal);
			maestro.setDescripcion(descricaoprodutoe);
		    maestro.setDatoExtra(datoExtra);
	        maestro.setImagen(arquivofotoproduto);
		    maestro.setMarca("Sabo");
		    maestro.setFamilia("Repuestos");
		    maestro.setLinea("");
		    maestro.setParte("");
		    
		    
		    
		    
		    try {
				grabar(maestro);
			} catch (Exception e) {
				
			}

		
			
		}
		

		}

	public static void main(String[] args) throws Exception {

		CargaMaestroSabo cs = new CargaMaestroSabo();
		cs.CargaMaestroSabo();
	}

		
	
	
	
}
