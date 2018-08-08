package com.yhaguy.gestion.transferencia;

public class BeanTransferenciaDetalle {

	String cantidad;
	String descripcion;
	
	public BeanTransferenciaDetalle(String cantidad, String descripcion) {
		this.cantidad = cantidad;
		this.descripcion = descripcion;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
