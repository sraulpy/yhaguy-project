package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class EmpresaObservacion extends Domain {

	private Date fecha;
	private String descripcion;
	private String usuario;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
