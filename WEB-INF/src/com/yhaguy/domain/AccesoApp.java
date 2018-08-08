package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Usuario;

@SuppressWarnings("serial")
public class AccesoApp extends Domain {

	private String descripcion = "";
	private DepartamentoApp departamento = null;
	private Set<SucursalApp> sucursales = new HashSet<SucursalApp>();
	//private Set<Deposito> depositos = new HashSet<Deposito>();
	private Usuario usuario = null;

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public DepartamentoApp getDepartamento() {
		return departamento;
	}

	public void setDepartamento(DepartamentoApp departamento) {
		this.departamento = departamento;
	}

	public Set<SucursalApp> getSucursales() {
		return sucursales;
	}

	public void setSucursales(Set<SucursalApp> sucursales) {
		this.sucursales = sucursales;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
