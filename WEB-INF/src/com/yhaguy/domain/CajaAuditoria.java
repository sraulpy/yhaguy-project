package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CajaAuditoria extends Domain {
	
	public static final String CONCEPTO_EFECTIVO = "EFECTIVO SALDO";
	public static final String CONCEPTO_CHEQUE = "CHEQUE AL DÍA";
	public static final String CONCEPTO_CHEQUE_DIFERIDO = "CHEQUE DIFERIDO";
	
	public static final String CONCEPTO_DEPOSITO_CHEQUE = "DEPÓSITO CHEQUE";
	public static final String CONCEPTO_DEPOSITO_EFECTIVO = "DEPÓSITO EFECTIVO";
	public static final String CONCEPTO_DESCUENTO_CHEQUE = "DESCUENTO CHEQUE";
	public static final String CONCEPTO_PAGO_CHEQUE = "PAGO CON CHEQUE";
	public static final String CONCEPTO_REPOSICION_CAJA = "REPOSICION CAJA CHICA";
	public static final String CONCEPTO_EGRESO_CAJA = "EGRESO DE CAJA";
	public static final String CONCEPTO_PAGO_EFECTIVO = "PAGO CON EFECTIVO";
	
	private Date fecha;
	private String resumen;
	private String concepto;
	private String numero;
	private String descripcion;
	private String supervisor;
	private double importe;
	
	private Tipo moneda;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	/**
	 * @return el debe..
	 */
	public double getDebe() {
		double out = 0;
		if (this.concepto.equals(CONCEPTO_EFECTIVO) || this.concepto.equals(CONCEPTO_CHEQUE)
				|| this.concepto.equals(CONCEPTO_CHEQUE_DIFERIDO)
				|| this.concepto.equals(CONCEPTO_REPOSICION_CAJA)) {
			out = this.importe;
		}
		return out;
	}
	
	/**
	 * @return el haber..
	 */
	public double getHaber() {
		double out = 0;
		if (this.concepto.equals(CONCEPTO_DEPOSITO_CHEQUE) || this.concepto.equals(CONCEPTO_DESCUENTO_CHEQUE)
				|| this.concepto.equals(CONCEPTO_PAGO_CHEQUE) || this.concepto.equals(CONCEPTO_DEPOSITO_EFECTIVO)
				|| this.concepto.equals(CONCEPTO_EGRESO_CAJA)
				|| this.concepto.equals(CONCEPTO_PAGO_EFECTIVO)) {
			out = this.importe;
		}
		return out;
	}
	
	/**
	 * @return el saldo..
	 */
	public double getSaldo() {
		return this.getDebe() - this.getHaber();
	}

	public String getResumen() {
		return resumen;
	}

	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

}
