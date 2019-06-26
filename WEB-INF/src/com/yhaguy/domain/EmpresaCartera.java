package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class EmpresaCartera extends Domain {

	public static final String CORRIENTE = "CORRIENTE";
	public static final String DUDOSO_COBRO = "DUDOSO COBRO";
	public static final String JUDICIAL = "JUDICIAL";
	public static final String OTROS = "OTROS";
	public static final String INCOBRABLE = "INCOBRABLE";
	
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
