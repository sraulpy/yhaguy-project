package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloCostoPromediogs extends Domain {

	private Date fecha;
	private double costoPromedio;
	private double ultimoCosto;
	private Articulo articulo;

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getCostoPromedio() {
		return costoPromedio;
	}

	public void setCostoPromedio(double costoPromedio) {
		this.costoPromedio = costoPromedio;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public double getUltimoCosto() {
		return ultimoCosto;
	}

	public void setUltimoCosto(double ultimoCosto) {
		this.ultimoCosto = ultimoCosto;
	}

}
