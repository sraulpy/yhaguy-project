package com.yhaguy.util.migracion.cinpal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.yhaguy.domain.ArticuloMarcaAplicacion;
import com.yhaguy.domain.RegisterDomain;

public class PruebaCinpal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 try
	        {
	                Class.forName("org.postgresql.Driver");

	                String url = "jdbc:postgresql://127.0.0.1:5432/cinpal";
	                Connection con = DriverManager.getConnection(url, "postgres","postgres");
	                Statement s = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                                                  ResultSet.CONCUR_READ_ONLY);

	                ResultSet rs= s.executeQuery("select * from figura");
	                
	               // RegisterDomain rr = RegisterDomain.getInstance();
	                
	              //  rr.deleteAllObjects(ArticuloMarcaAplicacion.class.getName());
	                
	                while (rs.next()) {
	                	String a = rs.getString(1);
	                	String b = rs.getString(2);
	                	
	                	System.out.println(a+""+b);
	                	
	                	
						
	                	//ArticuloMarcaAplicacion aMA = new ArticuloMarcaAplicacion();
	                	//aMA.setDescripcion(rs.getString(3));
	                	
	                	
	                	//rr.saveObject(aMA, "Ronald");
	                	
	                	
					} 
	                
	                rs.close();
	                s.close();
	                con.close();

	             
	        }
	        catch(Exception e)
	        {
	               System.out.println(e.getMessage());
	        }
		 
	}

}
