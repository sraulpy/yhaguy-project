package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class RecaudacionCentral extends Domain {
	
	private String numeroRecibo;
	private double importeGs;
	private double saldoGs;
	
	private String numeroCheque;
	private String numeroDeposito;
	private String razonSocial;

	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public void setDescripcion(String descripcion) {
	}
	
	public String getDescripcion() {
		return this.numeroRecibo + " - " + Utiles.getNumberFormat(this.importeGs);
	}

	public String getNumeroRecibo() {
		return numeroRecibo;
	}

	public void setNumeroRecibo(String numeroRecibo) {
		this.numeroRecibo = numeroRecibo;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public double getSaldoGs() {
		return saldoGs;
	}

	public void setSaldoGs(double saldoGs) {
		this.saldoGs = saldoGs;
	}

	public String getNumeroCheque() {
		return numeroCheque;
	}

	public void setNumeroCheque(String numeroCheque) {
		this.numeroCheque = numeroCheque;
	}

	public String getNumeroDeposito() {
		return numeroDeposito;
	}

	public void setNumeroDeposito(String numeroDeposito) {
		this.numeroDeposito = numeroDeposito;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
}
