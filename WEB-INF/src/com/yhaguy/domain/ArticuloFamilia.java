package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloFamilia extends Domain {
	
	public static final String FILTROS = "FILTROS";
	public static final String LUBRICANTES = "LUBRICANTES";
	public static final String CUBIERTAS = "CUBIERTAS";
	public static final String REPUESTOS = "REPUESTOS";
	public static final String BATERIAS = "BATERIAS";
	public static final String MARKETING = "MARKETING";
	public static final String RETAIL_SHOP = "RETAIL SHOP";
	public static final String SERVICIOS = "SERVICIOS";
	public static final String CONTABILIDAD = "CONTABILIDAD";
	public static final String VENTAS_ESPECIALES = "VENTAS ESPECIALES";
	public static final String MERCADERIAS_USADAS = "MERCADERIAS USADAS";
	public static final String BATERIAS_DISTRIBUIDOR = "BATERIAS DISTRIBUIDOR";

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
