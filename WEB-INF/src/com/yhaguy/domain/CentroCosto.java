package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class CentroCosto extends Domain{
	
	private String numero;
	private String descripcion;
	private double montoAsignado;

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getMontoAsignado() {
		return montoAsignado;
	}

	public void setMontoAsignado(double montoAsignado) {
		this.montoAsignado = montoAsignado;
	}

	@Override
	public int compareTo(Object o) {	
		CentroCosto cmp = (CentroCosto) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id)==0);
		   
		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}
}
