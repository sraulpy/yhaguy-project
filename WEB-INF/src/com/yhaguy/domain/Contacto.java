package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Contacto extends Domain {

	private String cargo;
	private String nombre;
	private String telefono;
	private String correo;
	private Date fechaCumpleanhos;
	private String cedula;

	Tipo profesion;
	ContactoSexo contactoSexo;
	EstadoCivil estadoCivil;
	Sucursal sucursal;

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public String getDescripcionProfesion() {
		String descripcion = this.profesion.getDescripcion();
		return descripcion;
	}

	public String getDescripcionSexo() {
		String descripcion = this.contactoSexo.getDescripcion();
		return descripcion;
	}

	public String getDescripcionEstadoCivil() {
		String descripcion = this.estadoCivil.getDescripcion();
		return descripcion;
	}

	public Tipo getProfesion() {
		return profesion;
	}

	public void setProfesion(Tipo profesion) {
		this.profesion = profesion;
	}

	public ContactoSexo getContactoSexo() {
		return contactoSexo;
	}

	public void setContactoSexo(ContactoSexo contactoSexo) {
		this.contactoSexo = contactoSexo;
	}

	public EstadoCivil getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(EstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Date getFechaCumpleanhos() {
		return fechaCumpleanhos;
	}

	public void setFechaCumpleanhos(Date fechaCumpleanhos) {
		this.fechaCumpleanhos = fechaCumpleanhos;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	public Contacto() {

	}

	public Contacto(String nombre, String direccion, String telefono,
			String correo) {
		this.nombre = nombre;
		this.telefono = telefono;
		this.correo = correo;
	}

	@Override
	public int compareTo(Object o) {
		Contacto cmp = (Contacto) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}

}
