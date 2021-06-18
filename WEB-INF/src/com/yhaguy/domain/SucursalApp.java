package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class SucursalApp extends Domain {
	
	public static final long ID_MRA = 1;
	public static final long ID_CENTRAL = 2;
	public static final long ID_MCAL = 3;
	public static final long ID_GAM = 4;

	public static final long ID_MRA_CENTRAL = 1;
	public static final long ID_MRA_MRA = 2;
	public static final long ID_MRA_MCAL = 3;
	
	private String nombre;
	private String descripcion;
	private String establecimiento;
	private String direccion;
	private String telefono;
	
	private boolean saldoTransferencias;
	
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

	public boolean isSaldoTransferencias() {
		return saldoTransferencias;
	}

	public void setSaldoTransferencias(boolean saldoTransferencias) {
		this.saldoTransferencias = saldoTransferencias;
	}
}
