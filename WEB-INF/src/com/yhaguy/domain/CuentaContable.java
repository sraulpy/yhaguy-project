package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class CuentaContable extends Domain{
	/**
	 * Cuentas asentables que tienen un codigo ej: (CT-1000)
	 * y que hacen referencia a una cuenta padre en el Plan de Cuentas..
	 */
	
	private String codigo;
	private String descripcion;	
	private String alias; 				//Identificacion de la cuenta ej: 'P-PR-VARIOS' = PASIVO : PROVEEDORES VARIOS
	
	private PlanDeCuenta planCuenta;    //1.01.01.01.01 el plan de cuenta oficial de esta cuenta..

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public PlanDeCuenta getPlanCuenta() {
		return planCuenta;
	}

	public void setPlanCuenta(PlanDeCuenta planCuenta) {
		this.planCuenta = planCuenta;
	}
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
}
