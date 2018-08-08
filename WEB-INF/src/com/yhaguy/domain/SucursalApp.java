package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class SucursalApp extends Domain {

	private String nombre;
	private String descripcion;
	private String establecimiento;
	private String direccion;
	private String telefono;
	
	private Set<Deposito> depositos = new HashSet<Deposito>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion.toUpperCase();
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getEstablecimiento() {
		return establecimiento;
	}

	public void setEstablecimiento(String establecimiento) {
		this.establecimiento = establecimiento;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Set<Deposito> getDepositos() {
		return depositos;
	}

	public void setDepositos(Set<Deposito> depositos) {
		this.depositos = depositos;
	}
}
