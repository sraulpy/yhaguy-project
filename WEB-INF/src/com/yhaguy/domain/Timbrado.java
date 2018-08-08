package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Timbrado extends Domain {

	private String numero;
	private Date vencimiento;
	
	public String getDescripcion() {
		return this.numero;
	}
	
	public void setDescripcion(String descripcion) {
	}
	
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	@Override
	public int compareTo(Object o) {
		return -1;
	}	
}
