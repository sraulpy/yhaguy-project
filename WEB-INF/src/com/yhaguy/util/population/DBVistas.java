package com.yhaguy.util.population;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;

public class DBVistas {

	RegisterDomain rr = RegisterDomain.getInstance();

	String queryVistaCuentaClienteDrop = "DROP TABLE IF EXISTS view_cuenta_cliente;";
	String queryVistaCuentaCliente = " CREATE OR REPLACE VIEW view_cuenta_cliente AS "
			+ " SELECT ROW_NUMBER() OVER (ORDER BY e.razonsocial) AS ROW, "
			+ "       e.id id_empresa, "
			+ "       max(e.razonsocial) razon_social, "
			+ "       max(c.fechaaperturacuentacliente) fecha_apertura_cuenta_cliente, "
			+ "       max(c.idestadocomocliente) id_estado_como_cliente, "
			+ "       max(c.idlineacredito) id_linea_credito, "
			+ "       m.idtipomoneda id_tipo_moneda, "
			+ "       COALESCE(SUM(m.saldo), 0) pendiente "
			+ " FROM empresa e "
			+ " INNER JOIN ctacteempresa c ON e.idctacteempresa = c.id "
			+ " AND c.idestadocomocliente IS NOT NULL AND c.idestadocomocliente <>"
			+ "		(SELECT tipo.id FROM tipo WHERE tipo.sigla LIKE 'CC-EM-SC')"
			+ " LEFT JOIN ctacteempresamovimiento m ON e.id = m.idempresa AND m.idtipocaractermovimiento = "
			+ "  	(SELECT tipo.id FROM tipo WHERE tipo.sigla LIKE 'CC-EMP-CARMOV-CLI') "
			+ " GROUP BY e.id, "
			+ "         m.idtipomoneda, "
			+ "         m.idtipocaractermovimiento ";

	String queryVistaCuentaProveedorDrop = "DROP TABLE IF EXISTS view_cuenta_proveedor;";
	String queryVistaCuentaProveedor = "	CREATE OR REPLACE VIEW view_cuenta_proveedor AS "
			+ "	SELECT ROW_NUMBER() OVER (ORDER BY e.razonsocial) AS Row,"
			+ "		   e.id id_empresa, "
			+ "	       max(e.razonsocial) razon_social, "
			+ "	       max(c.fechaaperturacuentaproveedor) fecha_apertura_cuenta_proveedor, "
			+ "	       max(c.idestadocomoproveedor) id_estado_como_proveedor, "
			+ "	       m.idtipomoneda id_tipo_moneda, "
			+ "	       COALESCE(SUM(m.saldo), 0) pendiente " 
			+ " FROM empresa e "
			+ " INNER JOIN ctacteempresa c ON e.idctacteempresa = c.id "
			+ " AND c.idestadocomoproveedor IS NOT NULL AND c.idestadocomoproveedor <>"
			+ "		(SELECT tipo.id FROM tipo WHERE tipo.sigla LIKE 'CC-EM-SC')"
			+ " LEFT JOIN ctacteempresamovimiento m ON e.id = m.idempresa AND m.idtipocaractermovimiento = "
			+ " 	(SELECT tipo.id FROM tipo WHERE tipo.sigla LIKE 'CC-EMP-CARMOV-PROV') "
			+ " GROUP BY e.id, "
			+ "         m.idtipomoneda, "
			+ "         m.idtipocaractermovimiento ";

	public void ejecutarQuerys() throws Exception {

		System.out.println("\n.......Creando Vistas(SQL)");
		rr.sql2(queryVistaCuentaClienteDrop);
		rr.sql2(queryVistaCuentaCliente);
		rr.sql2(queryVistaCuentaProveedorDrop);
		rr.sql2(queryVistaCuentaProveedor);

	}

	public static void main(String[] args) throws Exception {

		DBVistas v = new DBVistas();
		v.ejecutarQuerys();

	}

}
