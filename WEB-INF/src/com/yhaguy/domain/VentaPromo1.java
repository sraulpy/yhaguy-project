package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class VentaPromo1 extends Domain {
	
	/**
	 * Promo 2000 por cada bateria / registro de clientes..
	 */

	private Date fecha;
	private String nombreApellido;
	private String telefono;
	private String direccion;
	private Date nacimiento;
	private String correo;
	private Empresa empresa;
	
	/**
	 * Necesito nombre y apellido, teléfono al cual hacer la transferencia, dirección, fecha de nacimiento y mail
	 */
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNombreApellido() {
		return nombreApellido;
	}

	public void setNombreApellido(String nombreApellido) {
		this.nombreApellido = nombreApellido;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Date getNacimiento() {
		return nacimiento;
	}

	public void setNacimiento(Date nacimiento) {
		this.nacimiento = nacimiento;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

}
