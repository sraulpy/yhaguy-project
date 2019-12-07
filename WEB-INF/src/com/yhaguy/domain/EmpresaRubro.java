package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class EmpresaRubro extends Domain {
	
	public static final String CONSUMIDOR_FINAL = "CONSUMIDOR FINAL";

	private String descripcion;
	private String subrubro; // para definir segmentaciones adicionales
	
	private double descuentoBaterias;
	private double descuentoCubiertas;
	private double descuentoFiltros;
	private double descuentoLubricantes;
	private double descuentoRepuestos;
	
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

	public String getSubrubro() {
		return subrubro;
	}

	public void setSubrubro(String subrubro) {
		this.subrubro = subrubro;
	}

	public double getDescuentoBaterias() {
		return descuentoBaterias;
	}

	public void setDescuentoBaterias(double descuentoBaterias) {
		this.descuentoBaterias = descuentoBaterias;
	}

	public double getDescuentoCubiertas() {
		return descuentoCubiertas;
	}

	public void setDescuentoCubiertas(double descuentoCubiertas) {
		this.descuentoCubiertas = descuentoCubiertas;
	}

	public double getDescuentoFiltros() {
		return descuentoFiltros;
	}

	public void setDescuentoFiltros(double descuentoFiltros) {
		this.descuentoFiltros = descuentoFiltros;
	}

	public double getDescuentoLubricantes() {
		return descuentoLubricantes;
	}

	public void setDescuentoLubricantes(double descuentoLubricantes) {
		this.descuentoLubricantes = descuentoLubricantes;
	}

	public double getDescuentoRepuestos() {
		return descuentoRepuestos;
	}

	public void setDescuentoRepuestos(double descuentoRepuestos) {
		this.descuentoRepuestos = descuentoRepuestos;
	}
}
