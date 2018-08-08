package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;


@SuppressWarnings("serial")
public class HistoricoRecalculoStock extends Domain {
	
	private Date fecha;
	private String observacion;
	private String usuario;

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
