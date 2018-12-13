package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class VehiculoModelo extends Domain {

	private String descripcion;
	
	private VehiculoMarca marca;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public VehiculoMarca getMarca() {
		return marca;
	}

	public void setMarca(VehiculoMarca marca) {
		this.marca = marca;
	}
}
