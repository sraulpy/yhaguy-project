package com.yhaguy.gestion.empresa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.FuncionarioDocumentoApp;
import com.yhaguy.domain.FuncionarioPeriodoVacaciones;
import com.yhaguy.domain.FuncionarioSalario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class FuncionarioDTO extends DTO {

	private String correoFuncionario = "";
	private MyPair funcionarioEstado = new MyPair();
	private MyPair funcionarioCargo = new MyPair();
	private EmpresaDTO empresa = new EmpresaDTO();
	private List<MyPair> contactosInternos = new ArrayList<MyPair>();
	private List<AccesoDTO> accesos = new ArrayList<AccesoDTO>();
	
	private String estadoCivil;
	private String gradoAcademico;
	private int cantidadHijos = 0;
	
	private String nombres;
	private String apellidos;

	/*
	 * 0 = nombre; 1 = direccion, 2 = telefono; 3 = correoPersonal
	 */
	private FuncionarioSucursal datoSuc = null;

	public FuncionarioDTO() {

		this.datoSuc = new FuncionarioSucursal();
		this.datoSuc.setDatos(new MyArray());
		this.empresa = new EmpresaDTO();

	}
	
	/**
	 * @return antiguedad..
	 */
	public int getAntiguedad() {
		if (this.getFechaIngreso() == null) {
			return 0;
		}
		return Utiles.getAnhos(this.getFechaIngreso(), new Date());
	}
	
	/**
	 * @return url ubicacion..
	 */
	public String getUrlUbicacion() {
		String latitud = this.empresa.getLatitud();
		String longitud = this.empresa.getLongitud();
		if (latitud.isEmpty() || longitud.isEmpty()) {
			return null;
		}
		return "https://maps.google.com/?q="+ latitud +"," + longitud;
	}
	
	/**
	 * @return la url de la imagen..
	 */
	public String getUrlImagen() {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRPS)) {
			return Configuracion.URL_IMAGES_PUBLIC_RPS + "funcionarios/" + this.getEmpresa().getId() + ".png";
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRSA)) {
			return Configuracion.URL_IMAGES_PUBLIC_CENT + "funcionarios/" + this.getEmpresa().getId() + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_CENT + "funcionarios/" + this.getEmpresa().getId() + ".png";
	}
	
	/**
	 * @return los periodos..
	 */
	public List<FuncionarioPeriodoVacaciones> getPeriodos() throws Exception {
		List<FuncionarioPeriodoVacaciones> out = new ArrayList<FuncionarioPeriodoVacaciones>();
		if (this.isEsNuevo()) {
			return out;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		Funcionario f = rr.getFuncionarioById(this.getId());
		out.addAll(f.getPeriodos());
		return out;
	}
	
	/**
	 * @return los documentos app..
	 */
	public List<FuncionarioDocumentoApp> getDocumentosApp() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<FuncionarioDocumentoApp> out = rr.getFuncionarioDocumentosApp(this.empresa.getId());
		return out;
	}
	
	/**
	 * @return salario vigente..
	 */
	public Double getSalarioVigente() throws Exception {
		Double out = 0.0;
		if (this.getSalarios().size() > 0) {
			return this.getSalarios().get(0).getImporteGs();
		}
		return out;
	}
	
	/**
	 * @return salario vigente..
	 */
	public Double getBonificacionFamiliarVigente() throws Exception {
		Double out = 0.0;
		if (this.getBonificacionesFamiliar().size() > 0) {
			return this.getBonificacionesFamiliar().get(0).getImporteGs();
		}
		return out;
	}
	
	/**
	 * @return salario vigente..
	 */
	public Double getBonificacionResponsabilidadVigente() throws Exception {
		Double out = 0.0;
		if (this.getBonificacionesResponsabilidad().size() > 0) {
			return this.getBonificacionesResponsabilidad().get(0).getImporteGs();
		}
		return out;
	}
	
	/**
	 * @return los salarios..
	 */
	public List<FuncionarioSalario> getSalarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();	
		return rr.getFuncionarioSalarios(this.getId());
	}
	
	/**
	 * @return los salarios..
	 */
	public List<FuncionarioSalario> getBonificacionesFamiliar() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();	
		return rr.getFuncionarioBonificacionesFamiliar(this.getId());
	}
	
	/**
	 * @return los salarios..
	 */
	public List<FuncionarioSalario> getBonificacionesResponsabilidad() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();	
		return rr.getFuncionarioBonificacionesResponsabilidad(this.getId());
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

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getGradoAcademico() {
		return gradoAcademico;
	}

	public void setGradoAcademico(String gradoAcademico) {
		this.gradoAcademico = gradoAcademico;
	}

	public int getCantidadHijos() {
		return cantidadHijos;
	}

	public void setCantidadHijos(int cantidadHijos) {
		this.cantidadHijos = cantidadHijos;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
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
