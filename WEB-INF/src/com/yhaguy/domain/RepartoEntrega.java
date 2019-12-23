package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class RepartoEntrega extends Domain {

	private long cantidad;
	
	private VentaDetalle detalle;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public long getCantidad() {
		return cantidad;
	}

	public void setCantidad(long cantidad) {
		this.cantidad = cantidad;
	}

	public VentaDetalle getDetalle() {
		return detalle;
	}

	public void setDetalle(VentaDetalle detalle) {
		this.detalle = detalle;
	}
}
