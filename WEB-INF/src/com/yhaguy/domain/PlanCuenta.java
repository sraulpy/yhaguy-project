package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class PlanCuenta extends Domain {
	
	private String codigo;
	private String descripcion;
	private String imputable;
	private String impositivo;
	private String Ccosto;
	private int nivel;
	private int nro;
	
	public int getNro() {
		return nro;
	}

	public void setNro(int nro) {
		this.nro = nro;
	}

	public boolean isAsentable() {
		return nivel > 3;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion.toUpperCase();
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getImputable() {
		return imputable;
	}

	public void setImputable(String imputable) {
		this.imputable = imputable;
	}

	public String getImpositivo() {
		return impositivo;
	}

	public void setImpositivo(String impositivo) {
		this.impositivo = impositivo;
	}

	public String getCcosto() {
		return Ccosto;
	}

	public void setCcosto(String ccosto) {
		Ccosto = ccosto;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}
}
