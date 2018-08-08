package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ContableAsientoDetalle extends Domain {
	
	private String descripcion;
	private double debe;
	private double haber;
	
	private PlanCuenta cuenta;

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getDebe() {
		return debe;
	}

	public void setDebe(double debe) {
		this.debe = debe;
	}

	public double getHaber() {
		return haber;
	}

	public void setHaber(double haber) {
		this.haber = haber;
	}

	public PlanCuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(PlanCuenta cuenta) {
		this.cuenta = cuenta;
	}
}
