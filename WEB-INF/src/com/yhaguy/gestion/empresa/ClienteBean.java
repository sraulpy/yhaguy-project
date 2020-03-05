package com.yhaguy.gestion.empresa;

public class ClienteBean {

	private String ruc;
	private String razonSocial;
	private String direccion;
	private String telefono;
	
	public ClienteBean(String ruc, String razonSocial, String direccion, String telefono) {
		this.ruc = ruc;
		this.razonSocial = razonSocial;
		this.direccion = direccion;
		this.telefono = telefono;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
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
}
