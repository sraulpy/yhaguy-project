package com.yhaguy.domain;

import com.coreweb.domain.Domain;


@SuppressWarnings("serial")
public class RRHHMarcaciones extends Domain {
	
	private String tipo;
	private String descripcion;
	private String usuario;

	@Override
	public int compareTo(Object o) {
		RRHHMarcaciones cmp = (RRHHMarcaciones) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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