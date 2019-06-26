package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class EmpresaCartera extends Domain {

	static final String CORRIENTE = "CORRIENTE";
	static final String DUDOSO_COBRO = "DUDOSO COBRO";
	static final String JUDICIAL = "JUDICIAL";
	static final String OTROS = "OTROS";
	
	private String descripcion;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
