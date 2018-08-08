package com.yhaguy.gestion.caja.recibos;

public class BeanFormaPagoCheque {

	private String numero;
	private String banco;
	private String vencimiento;
	private String librador;
	
	public BeanFormaPagoCheque(String numero, String banco, String vencimiento, String librador) {
		this.numero = numero;
		this.banco = banco;
		this.vencimiento = vencimiento;
		this.librador = librador;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(String vencimiento) {
		this.vencimiento = vencimiento;
	}

	public String getLibrador() {
		return librador;
	}

	public void setLibrador(String librador) {
		this.librador = librador;
	}	
}
