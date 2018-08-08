package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ProcesadoraTarjeta extends Domain {

	private String nombre;	
	
	/** la sucursal donde se registro la operacion con Tarjeta **/
	private SucursalApp sucursal;
	
	/** la cuenta de banco donde la procesadora deposita los valores **/
	private BancoCta banco;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public BancoCta getBanco() {
		return banco;
	}

	public void setBanco(BancoCta banco) {
		this.banco = banco;
	}
	
	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
