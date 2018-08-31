package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class RRHHLiquidacionSalarioDetalle extends Domain {

	private String concepto;
	private double haberes;
	private double descuentos;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public double getHaberes() {
		return haberes;
	}

	public void setHaberes(double haberes) {
		this.haberes = haberes;
	}

	public double getDescuentos() {
		return descuentos;
	}

	public void setDescuentos(double descuentos) {
		this.descuentos = descuentos;
	}
}
