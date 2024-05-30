package com.yhaguy.util;

public class BeanDetalle {

	private String descripcion;
	private String cantidad;
	private String costo;
	private String importe;
	private double totalImporte;
	private String codigo;
	private String ubicacion;
	private String stock;

	public BeanDetalle(String descripcion, double cantidad, double costo, double importe, double totalImporte) {
		this.descripcion = descripcion;
		this.cantidad = Utiles.getNumberFormat(cantidad);
		this.costo = Utiles.getNumberFormat(costo);
		this.importe = Utiles.getNumberFormat(importe);
		this.totalImporte = totalImporte;
	}

	public BeanDetalle(String descripcion, double cantidad, double costo, double importe, double totalImporte,
			String codigo, String ubicacion, String stock) {
		this.descripcion = descripcion;
		this.cantidad = Utiles.getNumberFormat(cantidad);
		this.costo = Utiles.getNumberFormat(costo);
		this.importe = Utiles.getNumberFormat(importe);
		this.totalImporte = totalImporte;
		this.codigo = codigo;
		this.ubicacion = ubicacion;
		this.stock = stock;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	public String getCosto() {
		return costo;
	}

	public void setCosto(String costo) {
		this.costo = costo;
	}

	public String getImporte() {
		return importe;
	}

	public void setImporte(String importe) {
		this.importe = importe;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public double getTotalImporte() {
		return totalImporte;
	}

	public void setTotalImporte(double totalImporte) {
		this.totalImporte = totalImporte;
	}
}
