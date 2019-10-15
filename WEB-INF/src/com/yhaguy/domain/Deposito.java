package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Deposito extends Domain {

	// depositos empresa baterias..
	public static final long ID_DEPOSITO_PRINCIPAL = 2;
	public static final long ID_DEPOSITO_CONTROL = 13;
	public static final long ID_DEPOSITO_AVERIADOS = 6;
	public static final long ID_DEPOSITO_AUXILIO = 7;
	public static final long ID_DEPOSITO_FALLADOS = 8;
	public static final long ID_DEPOSITO_RECLAMOS = 9;
	public static final long ID_DEPOSITO_SECAS = 10;
	public static final long ID_DEPOSITO_TRANSITORIO = 11;
	public static final long ID_DEPOSITO_PRODUCCION = 12;
	public static final long ID_DEPOSITO_SC1 = 1;
	
	// depositos central
	public static final long ID_MINORISTA = 1;
	public static final long ID_CENTRAL_TEMPORAL = 2;
	public static final long ID_CENTRAL_RECLAMOS = 3;
	public static final long ID_CENTRAL_REPOSICION = 4;
	public static final long ID_MCAL_LOPEZ = 5;
	public static final long ID_MCAL_TEMPORAL = 6;
	public static final long ID_MAYORISTA = 7;
	public static final long ID_MAYORISTA_TEMPORAL = 8;
	public static final long ID_MRA_TEMPORAL = 9;
	public static final long ID_MRA = 11;
	public static final long ID_VIRTUAL_INVENTARIO = 10;
	public static final long ID_REFACTURACIONES = 12;
	public static final long ID_MAYORISTA_CENTRAL = 13;
	
	public static final String TEMPORAL = "TEMPORAL";
	public static final Character VIRTUAL = 'V';
	
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
