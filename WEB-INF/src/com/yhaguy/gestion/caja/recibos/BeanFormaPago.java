package com.yhaguy.gestion.caja.recibos;

public class BeanFormaPago {
	
	String emision;
	String numero;
	String tipo;
	String montoGs;
	
	public BeanFormaPago(String emision, String numero, String tipo, String montoGs) {
		this.emision = emision;
		this.numero = numero;
		this.tipo = tipo;
		this.montoGs = montoGs;
	}
	
	public String getEmision() {
		return emision;
	}
	public void setEmision(String emision) {
		this.emision = emision;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getMontoGs() {
		return montoGs;
	}
	public void setMontoGs(String montoGs) {
		this.montoGs = montoGs;
	}
	
}

