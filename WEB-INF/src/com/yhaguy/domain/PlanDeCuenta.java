package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class PlanDeCuenta extends Domain{
	
	private String codigo;
	private String descripcion;
	private Tipo tipoCuenta; 		//Activos - Pasivos - Ingresos - Egresos	
	private String imputable;
	private String impositivo;
	private String Ccosto;
	private int nivel;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion.toUpperCase();
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Tipo getTipoCuenta() {
		return tipoCuenta;
	}

	public void setTipoCuenta(Tipo tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getImputable() {
		return imputable;
	}

	public void setImputable(String imputable) {
		this.imputable = imputable;
	}

	public String getImpositivo() {
		return impositivo;
	}

	public void setImpositivo(String impositivo) {
		this.impositivo = impositivo;
	}

	public String getCcosto() {
		return Ccosto;
	}

	public void setCcosto(String ccosto) {
		Ccosto = ccosto;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

}
