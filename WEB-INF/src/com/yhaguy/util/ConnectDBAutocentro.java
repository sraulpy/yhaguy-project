package com.yhaguy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConnectDBAutocentro {

	final static String DB_CONNECTION = "jdbc:postgresql://localhost:5433/autocentro";
	final static String DB_USER = "postgres";
	final static String DB_PASS = "Yrsa0985*";
	
	private static ConnectDBAutocentro instance = new ConnectDBAutocentro();
	private static Connection connection = null;
	
	public synchronized static ConnectDBAutocentro getInstance() {
		if (instance == null || connection == null) {
			instance = new ConnectDBAutocentro();
		}
		return instance;
	}

	/**
	 * Al instanciar la clase se conecta a la bd..
	 */
	private ConnectDBAutocentro() {
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
				"NOW(), 7, 3, 2, " + 
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
	 * depositos bancarios mra..
	 */
	public List<Object[]> getDepositosBancariosRecibos(Date desde, Date hasta) {
		List<Object[]> out = new ArrayList<Object[]>();
		String desde_ = Utiles.getDateToString(desde, "dd-MM-yyyy 00:00:00");
		String hasta_ = Utiles.getDateToString(hasta, "dd-MM-yyyy 23:59:00");
		String sql = "SELECT ('DEPOSITO CTA. BANCARIA - MRA'), f.fechaoperacion, f.depositoNroReferencia, f.montoGs, 'ATLAS',"
				+ " concat('RECIBO NRO. ', r.numero, ' - ', e.razonSocial) "
			+ " FROM reciboformapago f inner join recibo r on f.idrecibo = r.id "
			+ " inner join cliente c on r.idcliente = c.id"
			+ " inner join empresa e on c.idempresa = e.id"
			+ " WHERE f.idtipo = 89 and f.fechaoperacion >= '" + desde_ + "' and f.fechaoperacion <= '" + hasta_ + "'";	                   	
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
	
	/**
	 * depositos bancarios mra..
	 */
	public List<Object[]> getDepositosBancariosVentas(Date desde, Date hasta) {
		List<Object[]> out = new ArrayList<Object[]>();
		String desde_ = Utiles.getDateToString(desde, "dd-MM-yyyy 00:00:00");
		String hasta_ = Utiles.getDateToString(hasta, "dd-MM-yyyy 23:59:00");
		String sql = "SELECT ('DEPOSITO CTA. BANCARIA - MRA'), f.fechaoperacion, f.depositoNroReferencia, f.montoGs, 'ATLAS',"
				+ " concat('VENTA NRO. ', v.numero, ' - ', e.razonSocial) "
			+ " FROM reciboformapago f inner join venta v on f.venta = v.id "
			+ " inner join cliente c on v.idcliente = c.id"
			+ " inner join empresa e on c.idempresa = e.id"
			+ " WHERE f.idtipo = 89 and f.fechaoperacion >= '" + desde_ + "' and f.fechaoperacion <= '" + hasta_ + "'";	                   	
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
