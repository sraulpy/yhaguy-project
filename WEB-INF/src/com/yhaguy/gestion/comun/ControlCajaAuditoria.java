package com.yhaguy.gestion.comun;

import java.util.Date;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.CajaAuditoria;
import com.yhaguy.domain.RegisterDomain;

public class ControlCajaAuditoria {

	/**
	 * add reposicion de caja..
	 */
	public static void addReposicionCaja(String numeroCaja, String numeroReposicion, Date fecha, double importe,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CajaAuditoria ca = new CajaAuditoria();
		ca.setConcepto(CajaAuditoria.CONCEPTO_REPOSICION_CAJA);
		ca.setDescripcion("REPOSICION CAJA CHICA NRO. " + numeroCaja);
		ca.setFecha(fecha);
		ca.setImporte(importe);
		ca.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		ca.setResumen("- - -");
		ca.setNumero(numeroReposicion);
		ca.setSupervisor(user);
		rr.saveObject(ca, user);
	}
	
	/**
	 * add egreso de caja..
	 */
	public static void addEgresoCaja(String numeroCaja, String numeroReposicion, Date fecha, double importe,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CajaAuditoria ca = new CajaAuditoria();
		ca.setConcepto(CajaAuditoria.CONCEPTO_EGRESO_CAJA);
		ca.setDescripcion("EGRESO CAJA NRO. " + numeroCaja);
		ca.setFecha(fecha);
		ca.setImporte(importe);
		ca.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		ca.setResumen("- - -");
		ca.setNumero(numeroReposicion);
		ca.setSupervisor(user);
		rr.saveObject(ca, user);
	}
	
	/**
	 * add egreso de caja con efectivo..
	 */
	public static void addEgresoEfectivo(String numeroCaja, String numeroReposicion, Date fecha, double importe,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CajaAuditoria ca = new CajaAuditoria();
		ca.setConcepto(CajaAuditoria.CONCEPTO_PAGO_EFECTIVO);
		ca.setDescripcion("PAGOS CON EFECTIVO CAJA NRO. " + numeroCaja);
		ca.setFecha(fecha);
		ca.setImporte(importe);
		ca.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		ca.setResumen("- - -");
		ca.setNumero(numeroReposicion);
		ca.setSupervisor(user);
		rr.saveObject(ca, user);
	}
	
}
