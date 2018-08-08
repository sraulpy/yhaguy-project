package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class TransferenciaDetalle extends Domain {

	private int cantidad;
	private long cantidadPedida;
	private long cantidadEnviada;
	private long cantidadRecibida;
	private double costo;
	private String estado;
	private Articulo articulo;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public double getImporteGs() {
		return this.costo * this.cantidad;
	}

	public long getCantidadPedida() {
		return cantidadPedida;
	}

	public void setCantidadPedida(long cantidadPedida) {
		this.cantidadPedida = cantidadPedida;
	}

	public long getCantidadEnviada() {
		return cantidadEnviada;
	}

	public void setCantidadEnviada(long cantidadEnviada) {
		this.cantidadEnviada = cantidadEnviada;
	}

	public long getCantidadRecibida() {
		return cantidadRecibida;
	}

	public void setCantidadRecibida(long cantidadRecibida) {
		this.cantidadRecibida = cantidadRecibida;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
