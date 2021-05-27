package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class NotaDebitoDetalle extends Domain {
	
	private String descripcion;
	private double importeGs;
	private double importeDs;

	private Tipo tipoIva;
	
	public boolean isIva10() {
		return this.tipoIva.getSigla().equals(Configuracion.SIGLA_IVA_10);
	}
	
	public boolean isIva5() {
		return this.tipoIva.getSigla().equals(Configuracion.SIGLA_IVA_5);
	}

	public boolean isExenta() {
		return this.tipoIva.getSigla().equals(Configuracion.SIGLA_IVA_EXENTO);
	}
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Tipo getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(Tipo tipoIva) {
		this.tipoIva = tipoIva;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}
}
