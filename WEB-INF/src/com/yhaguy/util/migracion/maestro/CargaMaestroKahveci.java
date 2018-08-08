package com.yhaguy.util.migracion.maestro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.coreweb.domain.Domain;
import com.yhaguy.domain.RegisterDomain;

public class CargaMaestroKahveci {

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
	

	// cargar los datos en la base de datos
	public void cargaKahveci() throws Exception {

		// ... leer la tabla de kahveci
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/kahveci";
		Connection con = DriverManager.getConnection(url, "postgres","postgres");
		Statement rs = con.createStatement();

		ResultSet rss = rs.executeQuery("select * from kahveci ");

		// poner y ejecutar el query
		int i = 0;
		while (rss.next()) {


			// sacar la info que necesitas
	    String codigoOriginal = getStr(rss.getString("CODIGO_ORIGINAL"));
		String codigoProveedor = getStr(rss.getString("CODIGO_PROVEEDOR"));
		String descripcion = getStr(rss.getString("DESCRIPCION"));
		String tipoMotor = getStr(rss.getString("TIPO_MOTOR"));
	    String marca_Aplicacion = getStr(rss.getString("MARCA_APLICACION"));
		String marca = getStr(rss.getString("MARCA"));
		String datoExtra =  tipoMotor +" - "+marca_Aplicacion ;
	
				
		System.out.println(codigoProveedor + " - " + codigoOriginal + " - "+descripcion +" - "+ tipoMotor +" - " + marca_Aplicacion + " - " +marca +" - " );
			  
			// guardar en maestro la info de kahveci
			Maestro maestro = new Maestro();

		maestro.setCodigoProveedor(codigoProveedor);
		maestro.setCodigoOriginal(codigoOriginal);
		maestro.setDescripcion(descripcion);
		maestro.setDatoExtra(datoExtra);
	    maestro.setMarca("kahveci");
	    maestro.setFamilia("Respuestos");
	    maestro.setLinea("Pesada");
	    maestro.setParte("Accesorios");
	    
	    
	    
	    
			// resto de los atributos que tengas
			try {
				grabar(maestro);
			} catch (Exception e) {
				System.out.println("error....");
			}

		}

	}

	public static void main(String[] args) throws Exception {
		// invocar el inicio
		CargaMaestroKahveci ck = new CargaMaestroKahveci();
		ck.cargaKahveci();
	}

}
