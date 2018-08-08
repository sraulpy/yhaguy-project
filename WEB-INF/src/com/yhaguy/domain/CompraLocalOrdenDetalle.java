package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CompraLocalOrdenDetalle extends Domain{
	
	private double costoGs;
	private double costoDs;
	private double ultCostoGs;
	private int cantidad;
	private int cantidadRecibida;
	
	private boolean presupuesto;
	private boolean ordenCompra;
	
	private Articulo articulo;
	private Tipo iva;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public double getCostoDs() {
		return costoDs;
	}

	public void setCostoDs(double costoDs) {
		this.costoDs = costoDs;
	}

	public double getUltCostoGs() {
		return ultCostoGs;
	}

	public void setUltCostoGs(double ultCostoGs) {
		this.ultCostoGs = ultCostoGs;
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

	public boolean isPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(boolean presupuesto) {
		this.presupuesto = presupuesto;
	}

	public boolean isOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(boolean ordenCompra) {
		this.ordenCompra = ordenCompra;
	}

	public Tipo getIva() {
		return iva;
	}

	public void setIva(Tipo iva) {
		this.iva = iva;
	}

	public int getCantidadRecibida() {
		return cantidadRecibida;
	}

	public void setCantidadRecibida(int cantidadRecibida) {
		this.cantidadRecibida = cantidadRecibida;
	}
}
