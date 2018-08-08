package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ReporteFavoritos extends Domain {

	private String usuario;
	private String codigoReporte;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getCodigoReporte() {
		return codigoReporte;
	}

	public void setCodigoReporte(String codigoReporte) {
		this.codigoReporte = codigoReporte;
	}
}
