package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloListaPrecioDetalle extends Domain {

	private double precioGs_contado;
	private double precioGs_credito;
	private boolean activo;
	
	private Articulo articulo;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	/**
	 * @return true si el precio contado y credito coinciden..
	 */
	public boolean isCreditoContado() {
		return this.precioGs_contado - this.precioGs_credito == 0;
	}

	public double getPrecioGs_contado() {
		return precioGs_contado;
	}

	public void setPrecioGs_contado(double precioGs) {
		this.precioGs_contado = precioGs;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public double getPrecioGs_credito() {
		return precioGs_credito;
	}

	public void setPrecioGs_credito(double precioGs_credito) {
		this.precioGs_credito = precioGs_credito;
	}
}
