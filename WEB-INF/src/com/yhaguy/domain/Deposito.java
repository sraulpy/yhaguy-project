package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Deposito extends Domain {

	public static final long ID_DEPOSITO_PRINCIPAL = 2;
	
	private String observacion;
	private String descripcion;	
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
