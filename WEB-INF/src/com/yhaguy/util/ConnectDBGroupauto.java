package com.yhaguy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDBGroupauto {

	final static String DB_CONNECTION = "jdbc:postgresql://localhost:5433/groupauto";
	final static String DB_USER = "postgres";
	final static String DB_PASS = "postgres";
	
	private static ConnectDBGroupauto instance = new ConnectDBGroupauto();
	private static Connection connection = null;
	
	public synchronized static ConnectDBGroupauto getInstance() {
		if (instance == null || connection == null) {
			instance = new ConnectDBGroupauto();
		}
		return instance;
	}

	/**
	 * Al instanciar la clase se conecta a la bd..
	 */
	private ConnectDBGroupauto() {
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
	 * add debitoGroupauto..
	 */
	public void addDebitoGroupauto(String usuario, String numero, String ruc, String razonSocial) {
		String sql = "INSERT INTO debitogroupauto("
				+ "id, dbestado, modificado, usuariomod, auxi, orden, ip_pc, origen,"
				+ "numero, fecha, ruc, razonsocial)" + "VALUES (nextval('debitogroupauto_seq'), '', NOW(), '" + usuario
				+ "', 'PENDIENTE', '', '', '" + numero + "', '', NOW()," + "'" + ruc + "', '" + razonSocial + "');";
		
		System.out.println(sql);
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add debitoGroupautoDetalle..
	 */
	public void addDebitoGroupautoDetalle(String codigo, String descripcion, long idFamilia, long cantidad, double costoGs) {
		try {
			String sql = "INSERT INTO debitogroupautodetalle("
					+ "id, dbestado, modificado, usuariomod, auxi, orden, ip_pc, cantidad, "
					+ "costogs, preciogs, codigo, descripcion, idfamilia, " + "iddebitogroupauto) "
					+ "VALUES (nextval('debitogroupautodetalle_seq'), '', NOW(), '', '', '', '', " + cantidad + ", "
					+ costoGs + ", " + costoGs + ", '" + codigo + "', '" + descripcion + "', " + idFamilia
					+ ", (select last_value from debitogroupauto_seq));";
			System.out.println(sql);

			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add DebitoGroupautoFormaPago..
	 */
	public void addDebitoGroupautoFormaPago(double montoGs, String tarjetaNumero, String tarjetanumerocomprobante, 
			String depositonroreferencia, String retencionnumero, String retenciontimbrado, String nrocomprobanteasociado, long idTipo, long idMoneda) {
		try {
			String sql = "INSERT INTO debitogroupautoformapago("
					+ "id, dbestado, modificado, usuariomod, auxi, orden, ip_pc, montogs,"
					+ "montods, montochequegs, descripcion, tarjetanumero, tarjetanumerocomprobante,"
					+ "chequefecha, chequenumero, chequelibrador, chequebancodescripcion,"
					+ "tarjetacuotas, depositonroreferencia, retencionnumero, retenciontimbrado,"
					+ "retencionvencimiento, nrocomprobanteasociado, recibodebitonro,"
					+ "fechaacreditacion, fechaoperacion, idsucursal, importeacreditado,"
					+ "pagarenumero, pagarefirmante, acreditado, idtipo, idmoneda, idtarjetatipo,"
					+ "idtarjetaprocesadora, idchequetercerobanco, iddepositobancocta,"
					+ "iddebitogroupauto) "
					+ "VALUES (nextval('debitogroupautoformapago_seq'), '', NOW(), '', '', '', '', " + montoGs + ", "
					+ "0.0, " + montoGs + ", 'DEBITO GROUPAUTO', '" + tarjetaNumero + "', '" + tarjetanumerocomprobante + "',"
					+ "null, '', '', '', 1, '" + depositonroreferencia + "', '" + retencionnumero + "', '" + retenciontimbrado + "',"
					+ "null, '" + nrocomprobanteasociado + "', '', null, NOW(), 2, " + montoGs + ", '', '', true, " + idTipo + ", " + idMoneda + ","
					+ "null, null, null, null," 
					+ "(select last_value from debitogroupauto_seq));";
			System.out.println(sql);

			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			ConnectDBGroupauto db = ConnectDBGroupauto.getInstance();
			db.addDebitoGroupauto("test", "001", "3538820-0", "TEST DE PRUEBA");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
