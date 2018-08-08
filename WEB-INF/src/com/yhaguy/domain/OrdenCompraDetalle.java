package com.yhaguy.domain;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class OrdenCompraDetalle extends Domain {

	private String descripcion;
	private int cantidad;
	private double precioGs;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	@DependsOn({ "cantidad", "precioGs" })
	public double getImporteGs() {
		return this.cantidad * this.precioGs;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecioGs() {
		return precioGs;
	}

	public void setPrecioGs(double precioGs) {
		this.precioGs = precioGs;
	}
}
