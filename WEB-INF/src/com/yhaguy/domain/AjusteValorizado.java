package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AjusteValorizado extends Domain {
	
	private Date fecha;
	private double costoGs;
	private Articulo articulo;	

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}
}
