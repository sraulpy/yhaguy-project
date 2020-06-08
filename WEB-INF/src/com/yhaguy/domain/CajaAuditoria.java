package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CajaAuditoria extends Domain {
	
	public static final String CONCEPTO_EFECTIVO = "EFECTIVO SALDO";
	public static final String CONCEPTO_CHEQUE = "CHEQUE AL DÍA";
	public static final String CONCEPTO_CHEQUE_DIFERIDO = "CHEQUE DIFERIDO";

	private Date fecha;
	private String resumen;
	private String concepto;
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
				|| this.concepto.equals(CONCEPTO_CHEQUE_DIFERIDO)) {
			out = this.importe;
		}
		return out;
	}
	
	/**
	 * @return el haber..
	 */
	public double getHaber() {
		double out = 0;
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

}
