package com.yhaguy.util.migracion.maestro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.coreweb.domain.Domain;
import com.yhaguy.domain.RegisterDomain;

public class CargaMaestroCipec {

	
	
	
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


	public void CargaMaestroCipec() throws Exception {
		
		
		// ... leer la tabla de cipec
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/cipec";
		Connection con = DriverManager.getConnection(url, "postgres",
				"postgres");
		Statement rs = con.createStatement();

		String q2Template =  "select pa.codigoproduto ,  a.descricaoaplicacao as descripcion1 ,gp.descricaogrupoprodutoe as descripcion2,"
				+ " fa.descricaofabricante as fabricante"
				+ " from ( produto as p left join produtoaplicacao pa on p.codigoproduto"
				+ " = pa.codigoproduto left join grupoproduto gp on  gp.codigogrupoproduto "
				+ " = p.codigogrupoproduto left join aplicacao a on pa.codigoaplicacao = a.codigoaplicacao"
				+ " left join fabricante fa on a.codigofabricante = fa.codigofabricante)"
				+ " where p.codigoproduto = 'XXX'"
				+ " order by p.codigoproduto, fa.codigofabricante , a.codigoaplicacao   ";

		
		
		
		String q1 = " select p.codigoproduto, MIN(p.numeroproduto ) as  codigoproveedor,"
				+ " MIN(p.codprodgridpesq ) as codigooriginal, MIN(p.descricaoproduto ) as  descripcion ,"
				+ " MIN(p.arquivofotoproduto) as  imagen from (produto as p left join referenciacruzada r "
				+ " on r.codigoproduto = p.codigoproduto )"
				+ " group by p.codigoproduto order by p.codigoproduto " ; 
		        

		ResultSet rss = rs.executeQuery(q1);

		int i = 0;
		while (rss.next()) {

			// sacar la info que necesitas
			String codigoproduto = getStr(rss.getString("codigoproduto"));
	     	String codprodgridpesq= getStr(rss.getString("codigooriginal"));
			String numeroproduto = getStr(rss.getString("codigoproveedor"));
		    String descricaoproduto =getStr(rss.getString("descripcion"));
			String arquivofotoproduto = getStr(rss.getString("imagen"));
			

		

		 System.out.println(codigoproduto + " - " + codprodgridpesq) ;		 
			 
			 
			String q2 = q2Template.replaceAll("XXX", codigoproduto);

		//	System.out.println(q2);

			Statement rs2 = con.createStatement();
			ResultSet rss2 = rs2.executeQuery(q2);

			
			String datoExtra = "";
			while (rss2.next()) {

		    	String descripcion1 =  getStr (rss2.getString("descripcion1"));
		    	String descripcion2 =  ("" + rss2.getString("descripcion2")).trim();
			//	String complemento = ("" + rss2.getString("complemento")).trim();
			//	String observacaoe = getStr(rss2.getString("observacion"));
				String fabricante =   ("" + rss2.getString("fabricante")).trim();
				
				
				String linea = "["+descripcion1 +" - "+ descripcion2+" - "+ fabricante+" ]\n";
				datoExtra = datoExtra + linea;
			}
		      	System.out.println(datoExtra);

			
			// grabar en maestro
			Maestro maestro = new Maestro();
		    maestro.setCodigoProveedor(numeroproduto);
	        maestro.setCodigoOriginal(codprodgridpesq);
			maestro.setDescripcion(descricaoproduto);
		    maestro.setDatoExtra(datoExtra);
	        maestro.setImagen(arquivofotoproduto);
		    maestro.setMarca("Cipec");
		    maestro.setFamilia("Repuestos");
		    maestro.setLinea("Pesada");
		    maestro.setParte("");
		    
		    
		    
		    
		    try {
				grabar(maestro);
			} catch (Exception e) {
				
			}

		}

	}

	public static void main(String[] args) throws Exception {

		CargaMaestroCipec cc1 = new CargaMaestroCipec();
		cc1.CargaMaestroCipec();
	}

	
	
}
