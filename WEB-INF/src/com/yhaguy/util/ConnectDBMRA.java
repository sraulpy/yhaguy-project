package com.yhaguy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConnectDBMRA {

	final static String DB_CONNECTION = "jdbc:postgresql://10.25.2.250:5432/yhaguydb";
	final static String DB_USER = "postgres";
	final static String DB_PASS = "yrsa0985";
	
	private static ConnectDBMRA instance = new ConnectDBMRA();
	private static Connection connection = null;
	
	public synchronized static ConnectDBMRA getInstance() {
		if (instance == null || connection == null) {
			instance = new ConnectDBMRA();
		}
		return instance;
	}

	/**
	 * Al instanciar la clase se conecta a la bd..
	 */
	private ConnectDBMRA() {
		try {
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {
			System.out.println("Driver Postgresql no registrado..");
			e.printStackTrace();
			return;
		}

		try {
			connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASS);

		} catch (SQLException e) {
			System.err.println("Fallo la conexion..");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			System.out.println("Correctamente conectado a la Base de Datos yhaguy..! \n");
		}
	}
	
	/**
	 * test..
	 */
	public void addTransferencia(String usuario, String numero, String numeroRemision, String observacion) {
		String sql = "INSERT INTO transferenciamra(" + 
				"id, dbestado, modificado, usuariomod, auxi, orden, numero, fechacreacion, " + 
				"fechaenvio, fecharecepcion, idestado, idtipo, idfuncionariocreador, " + 
				"iddepositosalida, " + 
				"iddepositoentrada, observacion, idsucursal, " + 
				"idsucursaldestino, ip_pc, numeroremision) " + 
				"VALUES (nextval('transferencia_seq'), '', NOW(), '" + usuario + "', 'MRA', '', '" + numero + "', NOW(), NOW(), " + 
				"NOW(), 7, 3, 4, " + 
				"1, 2, " + 
				"'" + observacion + "', 1, 2, " + 
				"'', '"+ numeroRemision +"');";
		System.out.println(sql);
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * test..
	 */
	public void addTransferenciaDetalle(String codigo, int cantidad, double costo) {
		try {
			ResultSet art = this.getDatosArticulo(codigo);
			String idArticulo = null;
			while (art.next()) {
				idArticulo = art.getObject(1) + "";
			}
			
			String sql = "INSERT INTO transferenciamradetalle(" + 
					"id, dbestado, modificado, usuariomod, auxi, orden, cantidad, " + 
					"cantidadpedida, cantidadenviada, cantidadrecibida, costo, estado, " + 
					"idarticulo, idtransferencia, ip_pc, costopromediogs, origen) " + 
					"VALUES (nextval('transferenciadetalle_seq'), '', NOW(), '', '', '', " + cantidad + ", " + 
					"" + cantidad + ", " + cantidad + ", " + cantidad + ", " + costo + ", 'Pendiente', " + 
					"" + idArticulo + ", (select last_value from transferencia_seq), '', 0, '"+ codigo +"');";
			System.out.println(sql);
			
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stock de articulos..
	 */
	private ResultSet getDatosArticulo(String codigo) {
		String sql = "SELECT id "
			+ "FROM articulo "
			+ "WHERE codigoInterno = '" + codigo + "'";	                   	
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * Stock de articulos..
	 */
	public List<Object[]> getDepositosBancarios(Date desde, Date hasta) {
		List<Object[]> out = new ArrayList<Object[]>();
		String desde_ = Utiles.getDateToString(desde, "dd-MM-yyyy hh:mm:ss");
		//String hasta_ = Utiles.getDateToString(hasta, "dd-MM-yyyy hh:mm:ss");
		String sql = "SELECT ('DEPOSITO CTA. BANCARIA - MRA'), fechaoperacion, depositoNroReferencia, montoGs, 'ATLAS', descripcion "
			+ "FROM reciboformapago "
			+ "WHERE idtipo = 89 and fechaoperacion >= '" + desde_ + "'";	                   	
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				Object[] data = new Object[] { result.getObject(1), result.getObject(2), result.getObject(3),
						result.getObject(4), result.getObject(5), result.getObject(6) };
				out.add(data);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return out;
	}
	
	public static void main(String[] args) {
		try {
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
