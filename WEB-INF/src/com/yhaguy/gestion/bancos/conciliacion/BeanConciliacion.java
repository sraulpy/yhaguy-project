package com.yhaguy.gestion.bancos.conciliacion;

public class BeanConciliacion {

	String numero;
	String concepto;
	String importeGs;

	public BeanConciliacion(String numero, String concepto, String importeGs) {
		this.numero = numero;
		this.concepto = concepto;
		this.importeGs = importeGs;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(String importeGs) {
		this.importeGs = importeGs;
	}

}
