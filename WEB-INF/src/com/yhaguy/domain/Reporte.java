package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Reporte extends Domain {
	
	public static final String KEY_TESORERIA = "TES";
	public static final String KEY_COMPRAS = "COM";
	public static final String KEY_VENTAS = "VEN";
	public static final String KEY_STOCK = "STK";
	public static final String KEY_LOGISTICA = "LOG";
	public static final String KEY_CONTABILIDAD = "CON";
	public static final String KEY_RRHH = "RRHH";
	public static final String KEY_SISTEMA = "SIS";
	
	private int modulo;
	private String codigo;
	private String descripcion;

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public int getModulo() {
		return modulo;
	}

	public void setModulo(int modulo) {
		this.modulo = modulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
