package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloGasto extends Domain{

	private String descripcion;
	private String creadoPor;
	private String verificadoPor;	
	
	private CuentaContable cuentaContable;
	
	public String getDescripcion() {
		return descripcion.toUpperCase();
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	public String getVerificadoPor() {
		return verificadoPor;
	}

	public void setVerificadoPor(String verificadoPor) {
		this.verificadoPor = verificadoPor;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	@Override
	public int compareTo(Object o) {
		return -1;
	}
}
