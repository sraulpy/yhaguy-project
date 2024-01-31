package com.yhaguy.gestion.empresa;

public class ClienteBean {

	private String ruc;
	private String razonSocial;
	private String direccion;
	private String telefono;
	private String rubro;
	private String limiteCredito;
	private String ciudad;
	private String ventas;
	private String departamento;
	private String nombreFantasia;
	
	public ClienteBean(String ruc, String razonSocial, String direccion, String telefono, String rubro,
			String limiteCredito, String ciudad, String ventas, String departamento, String nombreFantasia) {
		this.ruc = ruc;
		this.razonSocial = razonSocial;
		this.direccion = direccion;
		this.telefono = telefono;
		this.rubro = rubro;
		this.limiteCredito = limiteCredito;
		this.ciudad = ciudad;
		this.ventas = ventas;
		this.departamento = departamento;
		this.nombreFantasia = nombreFantasia;
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

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getVentas() {
		return ventas;
	}

	public void setVentas(String ventas) {
		this.ventas = ventas;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getNombreFantasia() {
		return nombreFantasia;
	}

	public void setNombreFantasia(String nombreFantasia) {
		this.nombreFantasia = nombreFantasia;
	}
}
