package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloReferencia extends Domain {

	private long idReferencia;
	private long idArticulo;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public long getIdReferencia() {
		return idReferencia;
	}

	public void setIdReferencia(long idReferencia) {
		this.idReferencia = idReferencia;
	}

	public long getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(long idArticulo) {
		this.idArticulo = idArticulo;
	}
}
