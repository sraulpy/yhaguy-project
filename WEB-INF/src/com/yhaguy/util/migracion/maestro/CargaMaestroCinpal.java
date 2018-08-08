package com.yhaguy.util.migracion.maestro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.coreweb.domain.Domain;
import com.yhaguy.domain.RegisterDomain;

public class CargaMaestroCinpal {
	
		private RegisterDomain rr = RegisterDomain.getInstance();
		public boolean grabar = false;

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


		public void CargaMaestroCinpal() throws Exception {
			
			
			// ... leer la tabla de cipec
			Class.forName("org.postgresql.Driver");

			String url = "jdbc:postgresql://127.0.0.1:5432/cinpal";
			Connection con = DriverManager.getConnection(url, "postgres",
					"postgres");
			Statement rs = con.createStatement();

			String q2Template =  "select  p.codigoproduto, ap.complementoaplicacaoe as complemento,"
					+ " a.descricaoaplicacaoe as descripcion ,ta.descricaotipoaplicacaoe as descripcion2 "
					+ " ,fa.descricaofabricantee as fabricante  ,po.observacaoe as observacion "
					+ "from ( produto as p left join aplicacao_produto ap on p.codigoproduto= ap.codigoproduto "
					+ "left join aplicacao a on ap.codigoaplicacao = a.codigoaplicacao "
					+ "left join tipoaplicacao ta on  ta.codigotipoaplicacao = a.codigotipoaplicacao "
					+ "left join fabricante fa on a.codigofabricante = fa.codigofabricante ) "
					+ "left join produto_obs po on p.codigoproduto = po.codigoproduto "
					+ "where p.codigoproduto = 'XXX'"
					+ "order by p.codigoproduto, fa.codigofabricante , a.codigoaplicacao  ";

			
			
			
			String q1 = " select  p.codigoproduto,  MIN(p.numeroempresaproduto)as numeroproduto ,"
					+ " MIN (p.descricaoprodutoe)as descripcion,MIN(p.arquivofotoproduto)as imagen , "
					+ " string_agg(r.numerofabricante , ' - ')as codigooriginal "
					+ " from (produto as p  left join referenciacruzada r  "
					+ " on p.codigoproduto = r.codigoproduto ) "
					+ " group by p.codigoproduto order by p.codigoproduto  " ; 
			        

			ResultSet rss = rs.executeQuery(q1);

			int i = 0;
			while (rss.next()) {

				// sacar la info que necesitas
				String codigoproduto = getStr(rss.getString("codigoproduto"));
		     	String numerofabricante= getStr(rss.getString("codigooriginal"));
				String numeroempresaproduto = getStr(rss.getString("numeroproduto"));
			    String descricaoprodutoe = getStr(rss.getString("descripcion"));
			    String arquivofotoproduto = getStr(rss.getString ("imagen")) ;
			    
				
				

			

			 System.out.println(arquivofotoproduto) ;		 
				 
				 
				String q2 = q2Template.replaceAll("XXX", codigoproduto);

			//	System.out.println(q2);

				Statement rs2 = con.createStatement();
				ResultSet rss2 = rs2.executeQuery(q2);

				String datoExtra = "";
				while (rss2.next()) {

			    	String descripcion =  ("" + rss2.getString("descripcion")).trim();
			    	String descripcion2 =  ("" + rss2.getString("descripcion2")).trim();
					String complemento = ("" + rss2.getString("complemento")).trim();
					String observacaoe = getStr(rss2.getString("observacion"));
					String fabricante =   ("" + rss2.getString("fabricante")).trim();
					
					
					String linea = "["+descripcion +" - "+ complemento +" - "+ descripcion2+" - "+ fabricante+" - "+observacaoe+"]\n";
					datoExtra = datoExtra + linea;
				}
			      	System.out.println(datoExtra);

				
				// grabar en maestro
				Maestro maestro = new Maestro();
			    maestro.setCodigoProveedor(numeroempresaproduto);
		        maestro.setCodigoOriginal(numerofabricante);
				maestro.setDescripcion(descricaoprodutoe);
			    maestro.setDatoExtra(datoExtra);
		        maestro.setImagen(arquivofotoproduto);
			    maestro.setMarca("CINPAL");
			    maestro.setFamilia("Respuestos");
			    maestro.setLinea("Pesada");
			    maestro.setParte("");
			    
			    
			    
			    
			    try {
					grabar(maestro);
				} catch (Exception e) {
					
				}

			
				
				
			}

		}

		public static void main(String[] args) throws Exception {

			CargaMaestroCinpal cc = new CargaMaestroCinpal();
			cc.CargaMaestroCinpal();
		}

	

}
