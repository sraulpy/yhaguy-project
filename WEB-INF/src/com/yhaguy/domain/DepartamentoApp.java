package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class DepartamentoApp extends Domain {

	private String nombre = "";
	private String descripcion = "";
	private SucursalApp sucursal = null;
	
	private Set<CentroCosto> centroCostos = new HashSet<CentroCosto>(); //Centros de Costo asignados al Departamento
	private Set<CuentaContable> cuentas = new HashSet<CuentaContable>(); //Cuentas asignadas al Departamento

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public Set<CentroCosto> getCentroCostos() {
		return centroCostos;
	}

	public void setCentroCostos(Set<CentroCosto> centroCostos) {
		this.centroCostos = centroCostos;
	}

	public Set<CuentaContable> getCuentas() {
		return cuentas;
	}

	public void setCuentas(Set<CuentaContable> cuentas) {
		this.cuentas = cuentas;
	}

	@Override
	public int compareTo(Object o) {
		DepartamentoApp cmp = (DepartamentoApp) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id)==0);
		   
		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}

}
