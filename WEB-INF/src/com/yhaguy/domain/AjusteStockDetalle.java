package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AjusteStockDetalle extends Domain {
	
	private int cantidad;	
	private int cantidadSistema;
	private double costoGs;
	private Articulo articulo;

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public int getCantidadSistema() {
		return cantidadSistema;
	}

	public void setCantidadSistema(int cantidadSistema) {
		this.cantidadSistema = cantidadSistema;
	}
}
