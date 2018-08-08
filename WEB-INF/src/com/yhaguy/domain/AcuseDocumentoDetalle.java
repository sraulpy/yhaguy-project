package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AcuseDocumentoDetalle extends Domain {

	private String numeroDocumento;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

}
