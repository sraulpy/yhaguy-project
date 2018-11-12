package com.yhaguy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDB {

	final static String DB_CONNECTION = "jdbc:firebirdsql:10.25.1.245/3050:yhaguy";
	final static String DB_USER = "SYSDBA";
	final static String DB_PASS = "masterkey";
	
	private static ConnectDB instance = new ConnectDB();
	private static Connection connection = null;
	
	public synchronized static ConnectDB getInstance() {
		if (instance == null || connection == null) {
			instance = new ConnectDB();
		}
		return instance;
	}

	/**
	 * Al instanciar la clase se conecta a la bd..
	 */
	private ConnectDB() {
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");

		} catch (ClassNotFoundException e) {
			System.out.println("Driver Firebird no registrado..");
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
			System.out.println("Correctamente conectado a la Base de Datos Firebird yhaguy..! \n");
		}
	}
	
	/**
	 * Stock de articulos..
	 */
	public ResultSet getDatosArticulos(String codigo, String planVenta) {
		String sql = "SELECT resu.idarticulo, "
			+ "SUM(decode(plve.idplanventa, " + planVenta + ", p.precio, 0)) AS PLANVENTA "
			+ "FROM (SELECT b.idarticulo "
			+ "FROM articulo b "
			+ "WHERE b.idarticulo = '" + codigo + "'"
			+ "GROUP BY b.idarticulo "
			+ "ORDER BY b.idarticulo) resu, "
			+ "precio p, "
			+ "planventa plve "
			+ "WHERE p.idarticulo = resu.idarticulo "
			+ "AND plve.idplanventa = p.idplanventa "
			+ "GROUP BY resu.idarticulo";	                   	
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
	 * detalle de movimientos..
	 */
	public ResultSet getDetalleMovimiento(String idMovimiento) {
		String sql = "select m.idarticulo, (m.cantidad * m.precio) from detallemovimiento m where m.idmovimiento = " + idMovimiento;	                   	
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public static void main(String[] args) {
		try {
			ConnectDB conn = ConnectDB.getInstance();
			ResultSet result = conn.getDatosArticulos("SC-2043", "1");
			while (result.next()) {
				System.out.println(result.getObject(1) + " - " + result.getObject(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
