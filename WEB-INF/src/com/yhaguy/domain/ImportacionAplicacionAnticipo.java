package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ImportacionAplicacionAnticipo extends Domain {
	
	private double importeGs;
	private double importeDs;	
	
	private CtaCteEmpresaMovimiento movimiento;

	@Override
	public int compareTo(Object o) {
		return -1; 
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}

	public CtaCteEmpresaMovimiento getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(CtaCteEmpresaMovimiento movimiento) {
		this.movimiento = movimiento;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}		
}