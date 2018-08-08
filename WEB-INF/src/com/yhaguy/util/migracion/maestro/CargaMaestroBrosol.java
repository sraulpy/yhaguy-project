package com.yhaguy.util.migracion.maestro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.coreweb.domain.Domain;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.migracion.maestro.CargaMaestroBrosol;

public class CargaMaestroBrosol {
	
	
	
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


	public void CargaMaestroBrosol() throws Exception {
		
		
		// ... leer la tabla de Brosol
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/Brosol";
		Connection con = DriverManager.getConnection(url, "postgres",
				"postgres");
		Statement rs = con.createStatement();

		String q2Template =  "select p.codigoproduto,  a.descricaoaplicacao as descripcion,"
				+ " a.complementoaplicacao2 as complemento, complementoaplicacao4 as complemento4,"
				+ " f.descricaofabricante as fabricante"
				+ " from(produto as p  left join produto_aplicacao pa "
				+ " on p.codigoproduto = pa.codigoproduto )"
				+ " left join aplicacao a on a.codigoaplicacao = pa.codigoaplicacao "
				+ " left join fabricante f on a.codigofabricante = f.codigofabricante "
				+ " where p.codigoproduto = 'XXX'"
				+ " order by p.codigoproduto, f.codigofabricante , a.codigoaplicacao ";

		
		
		
		String q1 = "select p.codigoproduto,  MIN(p.numeroproduto)as numeroproduto ,"
				+ " MIN(p.descricaoprodutoe)as descripcion , "
				+ "MIN(p.arquivofotoproduto)as imagen , "
				+ " string_agg(r.numeroproduto , ' - ')as codigooriginal "
				+"from (produto as p  left join referenciacruzada1 r "
			    +" on p.codigoproduto = r.codigoproduto ) "
		        +"group by p.codigoproduto " + " order by p.codigoproduto " ;
		        

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

			System.out.println(q2);

			Statement rs2 = con.createStatement(); 
			ResultSet rss2 = rs2.executeQuery(q2);

			
			String datoExtra = "";
			while (rss2.next()) {

		    	String descripcion =  ("" + rss2.getString("descripcion")).trim();
				String complemento2 = ("" + rss2.getString("complemento")).trim();
				String complemento4 = ("" + rss2.getString("complemento4")).trim();
				String fabricante =   ("" + rss2.getString("fabricante")).trim();
				
				String linea = "["+descripcion +" - "+ complemento2 +" - "+ complemento4 +" - "+ fabricante+"]\n";
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
		    maestro.setMarca("Brosol");
		    maestro.setFamilia("Repuestos");
		    maestro.setParte("Motor");
		    maestro.setLinea("liviana");
		    
		   
		    
		    try {
				grabar(maestro);
			} catch (Exception e) {
				
			}

		
			
			
		}

	}

	public static void main(String[] args) throws Exception {

		CargaMaestroBrosol cbr = new CargaMaestroBrosol();
		cbr.CargaMaestroBrosol();
	}

}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


