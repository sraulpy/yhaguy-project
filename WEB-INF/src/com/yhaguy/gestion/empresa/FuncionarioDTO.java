package com.yhaguy.gestion.empresa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.empresa.EmpresaDTO;
import com.yhaguy.inicio.AccesoDTO;

@SuppressWarnings("serial")
public class FuncionarioDTO extends DTO {

	private String correoFuncionario = "";
	private MyPair funcionarioEstado = new MyPair();
	private MyPair funcionarioCargo = new MyPair();
	private EmpresaDTO empresa = new EmpresaDTO();
	private List<MyPair> contactosInternos = new ArrayList<MyPair>();
	private List<AccesoDTO> accesos = new ArrayList<AccesoDTO>();

	/*
	 * 0 = nombre; 1 = direccion, 2 = telefono; 3 = correoPersonal
	 */
	private FuncionarioSucursal datoSuc = null;

	public FuncionarioDTO() {

		this.datoSuc = new FuncionarioSucursal();
		this.datoSuc.setDatos(new MyArray());
		this.empresa = new EmpresaDTO();

	}

	public String toString() {
		return this.getNombre() + " (" + this.getId() + ")";
	}

	public String getNombre() {
		return this.getEmpresa().getNombre();
	}

	public void setNombre(String nombre) {
		this.getEmpresa().setNombre(nombre);
		this.getEmpresa().setRazonSocial(nombre);
	}

	public String getRuc() {
		return this.getEmpresa().getRuc();
	}

	public void setRuc(String ruc) {
		this.getEmpresa().setRuc(ruc);
	}

	public String getCi() {
		return this.getEmpresa().getCi();
	}

	public void setCi(String ci) {
		this.getEmpresa().setCi(ci);
	}

	public String getDireccion() {
		return this.datoSuc.getDireccion();
	}

	public void setDireccion(String direccion) {
		this.datoSuc.setDireccion(direccion);
	}

	public String getTelefono() {
		return this.datoSuc.getTelefono();
	}

	public void setTelefono(String telefono) {
		this.datoSuc.setTelefono(telefono);
	}

	public String getCorreoPersonal() {
		return this.datoSuc.getCorreoPersonal();
	}

	public void setCorreoPersonal(String correoPersonal) {
		this.datoSuc.setCorreoPersonal(correoPersonal);
	}

	public Date getFechaIngreso() {
		return this.getEmpresa().getFechaIngreso();
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.getEmpresa().setFechaIngreso(fechaIngreso);
	}

	public Date getFechaCumpleanhos() {
		return this.getEmpresa().getFechaAniversario();
	}

	public void setFechaCumpleanhos(Date fechaCumpleanios) {
		this.getEmpresa().setFechaAniversario(fechaCumpleanios);
	}

	public String getObservacion() {
		return this.getEmpresa().getObservacion();
	}

	public void setObservacion(String observacion) {
		this.getEmpresa().setObservacion(observacion);
	}

	public String getCorreoFuncionario() {
		return correoFuncionario;
	}

	public void setCorreoFuncionario(String correoFuncionario) {
		this.correoFuncionario = correoFuncionario;
	}

	public MyPair getFuncionarioEstado() {
		return funcionarioEstado;
	}

	public void setFuncionarioEstado(MyPair funcionarioEstado) {
		this.funcionarioEstado = funcionarioEstado;
	}

	public MyPair getFuncionarioCargo() {
		return funcionarioCargo;
	}

	public void setFuncionarioCargo(MyPair funcionarioCargo) {
		this.funcionarioCargo = funcionarioCargo;
	}

	public EmpresaDTO getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(EmpresaDTO empresa) {
		this.empresa = empresa;
		try {
			this.datoSuc.setDatos(this.empresa.getSucursales().get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<MyPair> getContactosInternos() {
		return contactosInternos;
	}

	public void setContactosInternos(List<MyPair> contactosInternos) {
		this.contactosInternos = contactosInternos;
	}

	public List<AccesoDTO> getAccesos() {
		return accesos;
	}

	public void setAccesos(List<AccesoDTO> accesos) {
		this.accesos = accesos;
	}

	public FuncionarioSucursal getDatoSuc() {
		return datoSuc;
	}

	public void setDatoSuc(FuncionarioSucursal datoSuc) {
		this.datoSuc = datoSuc;
	}

	public void pasaDtoToSucursal() {
		if (this.getEmpresa().getSucursales().size() == 0) {
			this.getEmpresa().getSucursales().add(new MyArray());
		}

		MyArray ma = this.getEmpresa().getSucursales().get(0);
		this.datoSuc.pasarDatos(ma);
	}

	public void pasaSucursalToDto() {
		try {
			this.datoSuc.setDatos(this.empresa.getSucursales().get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}


@SuppressWarnings("serial")
class FuncionarioSucursal implements Serializable {

	String nombre; // pos 1
	String direccion; // pos 2
	String telefono; // pos 3
	String correoPersonal; // pos 4

	public void setDatos(MyArray ma) {
		this.nombre = (String) ma.getPos1();
		this.direccion = (String) ma.getPos2();
		this.telefono = (String) ma.getPos3();
		this.correoPersonal = (String) ma.getPos4();
	}

	public void pasarDatos(MyArray ma) {
		ma.setPos1(this.nombre);
		ma.setPos2(this.direccion);
		ma.setPos3(this.telefono);
		ma.setPos4(this.correoPersonal);
	}

	public MyArray xgetNewDatos() {
		MyArray ma = new MyArray();
		ma.setPos1(this.nombre);
		ma.setPos2(this.direccion);
		ma.setPos3(this.telefono);
		ma.setPos4(this.correoPersonal);

		return ma;
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

	public String getCorreoPersonal() {
		return correoPersonal;
	}

	public void setCorreoPersonal(String correoPersonal) {
		this.correoPersonal = correoPersonal;
	}

}
