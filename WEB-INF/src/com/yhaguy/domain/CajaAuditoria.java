package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CajaAuditoria extends Domain {
	
	public static final String CONCEPTO_EFECTIVO = "EFECTIVO SALDO";
	public static final String CONCEPTO_CHEQUE = "CHEQUE AL DÍA";

	private Date fecha;
	private String resumen;
	private String concepto;
	private String supervisor;
	private double importe;
	
	private Tipo moneda;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
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

}
