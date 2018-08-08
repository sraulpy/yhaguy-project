package com.yhaguy.gestion.empresa;

import java.util.Date;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

public class ContactoDTO extends DTO{
	
	private String cargo = "";
	private String nombre = "";
	private String telefono = "";
	private String correo = "";
	private Date fechaCumpleanhos = new Date();
	private String cedula  = "";
	
	private MyPair profesion = new MyPair();
	private MyPair contactoSexo = new MyPair();
	private MyPair estadoCivil = new MyPair();
	private MyArray sucursal = new MyArray();
	
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
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
	public MyPair getProfesion() {
		return profesion;
	}
	public void setProfesion(MyPair profesion) {
		this.profesion = profesion;
	}
	public MyPair getContactoSexo() {
		return contactoSexo;
	}
	public void setContactoSexo(MyPair contactoSexo) {
		this.contactoSexo = contactoSexo;
	}
	public MyPair getEstadoCivil() {
		return estadoCivil;
	}
	public void setEstadoCivil(MyPair estadoCivil) {
		this.estadoCivil = estadoCivil;
	}
	public MyArray getSucursal() {
		return sucursal;
	}
	public void setSucursal(MyArray sucursal) {
		this.sucursal = sucursal;
	}
	

	
}
