package com.yhaguy.gestion.empresa;

public class ClienteBean {

	private String ruc;
	private String razonSocial;
	private String direccion;
	private String telefono;
	private String rubro;
	private String limiteCredito;
	
	public ClienteBean(String ruc, String razonSocial, String direccion, String telefono, String rubro, String limiteCredito) {
		this.ruc = ruc;
		this.razonSocial = razonSocial;
		this.direccion = direccion;
		this.telefono = telefono;
		this.rubro = rubro;
		this.limiteCredito = limiteCredito;
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

	public String getRubro() {
		return rubro;
	}

	public void setRubro(String rubro) {
		this.rubro = rubro;
	}

	public String getLimiteCredito() {
		return limiteCredito;
	}

	public void setLimiteCredito(String limiteCredito) {
		this.limiteCredito = limiteCredito;
	}
}
