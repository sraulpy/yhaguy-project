package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Localidad extends Domain {

	String descripcion = "--LOCALIDAD DESCRIPCION--";
	String pais = "Paraguay";
	String departamento = "";
	String localidad = "";
	String barrio = "";
	String cp = "0000";

	
	public String getDescripcion() {
		return this.barrio + " - " + this.localidad + " - " + this.departamento;
	}

	public void setDescripcion(String descripcion) {
		System.out.println("[ERROR] No usar Localidad.serDescripcion():"+descripcion);
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getBarrio() {
		return barrio;
	}

	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return -1;
	}

}
