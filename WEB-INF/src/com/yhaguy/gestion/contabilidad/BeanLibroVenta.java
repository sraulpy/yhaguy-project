package com.yhaguy.gestion.contabilidad;

import java.util.Date;

public class BeanLibroVenta {

	Date fecha_;
	private String fecha;
	private String concepto;
	private String numero;
	private String razonSocial;
	private String ruc;
	private Double gravado5;
	private Double gravado10;
	private Double iva5;
	private Double iva10;
	private Double total;
	private Double exenta;
	private String clase;

	public BeanLibroVenta(String fecha, String concepto, String numero,
			String razonSocial, String ruc, double gravado10, double iva10,
			double gravado5, double iva5, double total, double exenta, Date fecha_, String clase) {
		this.fecha_ = fecha_;
		this.fecha = fecha;
		this.concepto = concepto;
		this.numero = numero;
		this.razonSocial = razonSocial;
		this.ruc = ruc;
		this.gravado10 = gravado10;
		this.iva10 = iva10;
		this.gravado5 = gravado5;
		this.iva5 = iva5;
		this.total = total;
		this.exenta = exenta;
		this.clase = clase;
	}
	
	public int getNumero_() {
		String nro = this.numero.substring(8);
		return Integer.parseInt(nro);
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

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public Double getGravado5() {
		return gravado5;
	}

	public void setGravado5(Double gravado5) {
		this.gravado5 = gravado5;
	}

	public Double getGravado10() {
		return gravado10;
	}

	public void setGravado10(Double gravado10) {
		this.gravado10 = gravado10;
	}

	public Double getIva5() {
		return iva5;
	}

	public void setIva5(Double iva5) {
		this.iva5 = iva5;
	}

	public Double getIva10() {
		return iva10;
	}

	public void setIva10(Double iva10) {
		this.iva10 = iva10;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getExenta() {
		return exenta;
	}

	public void setExenta(Double exenta) {
		this.exenta = exenta;
	}

	public Date getFecha_() {
		return fecha_;
	}

	public void setFecha_(Date fecha_) {
		this.fecha_ = fecha_;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}
}
