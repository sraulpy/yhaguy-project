package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class FuncionarioDocumentoApp extends Domain {

	private Date fecha;
	private String descripcion;
	private String idEmpresa;
	private String path;
	
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(String idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
