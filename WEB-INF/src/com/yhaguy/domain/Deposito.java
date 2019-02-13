package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Deposito extends Domain {

	public static final long ID_DEPOSITO_PRINCIPAL = 2;
	public static final long ID_DEPOSITO_CONTROL = 13;
	
	public static final long ID_MINORISTA = 1;
	public static final long ID_CENTRAL_TEMPORAL = 2;
	public static final long ID_CENTRAL_RECLAMOS = 3;
	public static final long ID_CENTRAL_REPOSICION = 4;
	public static final long ID_MCAL_LOPEZ = 5;
	public static final long ID_MCAL_TEMPORAL = 6;
	public static final long ID_MAYORISTA = 7;
	public static final long ID_MAYORISTA_TEMPORAL = 8;
	public static final long ID_MRA = 9;
	public static final long ID_VIRTUAL_INVENTARIO = 10;
	
	public static final String TEMPORAL = "TEMPORAL";
	
	private String observacion;
	private String descripcion;	
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public boolean isTemporal() {
		return this.descripcion.contains(TEMPORAL);
	}
	
	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
