package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloPrecioJedisoft extends Domain {
	
	private long idPlanVenta;
	private String codigoInterno;
	private double precio;
	private double precioMinimo;

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public long getIdPlanVenta() {
		return idPlanVenta;
	}

	public void setIdPlanVenta(long idPlanVenta) {
		this.idPlanVenta = idPlanVenta;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public double getPrecioMinimo() {
		return precioMinimo;
	}

	public void setPrecioMinimo(double precioMinimo) {
		this.precioMinimo = precioMinimo;
	}

}
