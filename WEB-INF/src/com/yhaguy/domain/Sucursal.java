package com.yhaguy.domain;

import com.coreweb.domain.Domain;


@SuppressWarnings("serial")
public class Sucursal extends Domain{


	private String nombre;
	private String direccion;	
	private Localidad localidad;
	private Zona zona;
	private String telefono;	
	private String correo;
	
	

	public Localidad getLocalidad() {
		return localidad;
	}
	public void setLocalidad(Localidad localidad) {
		this.localidad = localidad;
	}
	public Zona getZona() {
		return zona;
	}
	public void setZona(Zona zona) {
		this.zona = zona;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
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
	
	public Sucursal() {
		
	}
	
	@Override
	public int compareTo(Object o) {
		Sucursal cmp = (Sucursal) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id)==0);
		   
		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}
	
}
