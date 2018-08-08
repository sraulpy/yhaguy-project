package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Retencion extends Domain {
	
	public static final String EMITIDAS = "Emitidas";
	public static final String RECIBIDAS = "Recibidas";
	
	private String estado;
	private String numero;
	private String ruc;
	private String razonSocial;
	private String fecha;
	private String numeroFactura;
	private String numeroOrdenPago;
	private double importeGs;
	private int periodo;
	private String tipoRetencion;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public String getNumeroOrdenPago() {
		return numeroOrdenPago;
	}

	public void setNumeroOrdenPago(String numeroOrdenPago) {
		this.numeroOrdenPago = numeroOrdenPago;
	}

	public String getTipoRetencion() {
		return tipoRetencion;
	}

	public void setTipoRetencion(String tipoRetencion) {
		this.tipoRetencion = tipoRetencion;
	}
}
