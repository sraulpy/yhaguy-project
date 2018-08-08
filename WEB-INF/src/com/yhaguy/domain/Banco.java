package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Banco extends Domain {

	private String descripcion;
	private String direccion;
	private String telefono;
	private String correo;
	private String contacto;
	private Tipo bancoTipo;

	private Set<BancoSucursal> sucursales = new HashSet<BancoSucursal>();

	public String getDescripcion() {
		return descripcion.toUpperCase();
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getContacto() {
		return contacto;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public Set<BancoSucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(Set<BancoSucursal> sucursales) {
		this.sucursales = sucursales;
	}

	public Tipo getBancoTipo() {
		return bancoTipo;
	}

	public void setBancoTipo(Tipo bancoTipo) {
		this.bancoTipo = bancoTipo;
	}

	@Override
	public int compareTo(Object o) {

		Banco cmp = (Banco) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}
}
