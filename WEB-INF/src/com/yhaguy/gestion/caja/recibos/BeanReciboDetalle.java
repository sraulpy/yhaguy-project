package com.yhaguy.gestion.caja.recibos;

public class BeanReciboDetalle {

	String fecha;
	String concepto;
	String factura;
	String importe;
	
	public BeanReciboDetalle(String fecha, String concepto, String factura, String importe) {
		this.fecha = fecha;
		this.concepto = concepto;
		this.factura = factura;
		this.importe = importe;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getFactura() {
		return factura.substring(8, factura.length());
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}

	public String getImporte() {
		return importe;
	}

	public void setImporte(String importe) {
		this.importe = importe;
	}
}
