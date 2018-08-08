package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaDTO;

@SuppressWarnings("serial")
public class EmpresaDTO extends DTO {

	private String nombre = "";
	private String codigoEmpresa = "";
	private String razonSocial = "";
	private String ruc = "";
	private String ci = "";
	private MyPair empresaGrupoSociedad = new MyPair();
	private String observacion = "";
	private Date fechaIngreso = new Date();
	private Date fechaAniversario = new Date();
	private MyPair pais;
	private MyPair moneda;
	private MyArray monedaConSimbolo;
	private MyPair tipoPersona;
	private MyPair regimenTributario;
	private String web = "";
	private String sigla = "";
	private boolean razonSocialSet = false; // indica si la razon social fue
											// obtenido de la BD del SET.
	
	private String direccion_ = "";
	private String telefono_ = "";
	private String correo_ = "";
	
	private String latitud = "";
	private String longitud = "";
	private String userlocation = "";
	
	private boolean cuentaBloqueada;

	List<MyPair> monedas = new ArrayList<MyPair>();
	List<MyPair> rubroEmpresas = new ArrayList<MyPair>();
	List<MyArray> sucursales = new ArrayList<MyArray>();
	List<ContactoDTO> contactos = new ArrayList<ContactoDTO>();

	MyPair tipoPagoProveedor;
	MyPair tipoProveedor;

	private int tipoEmpresa; // 1: Proveedor, 2:Cliente, 3:Cliente/Proveedor

	private CtaCteEmpresaDTO ctaCteEmpresa = new CtaCteEmpresaDTO();

	public EmpresaDTO() {
	}
	
	public String getDireccion() {
		for (MyArray suc : this.sucursales) {
			return (String) suc.getPos2();
		}
		return "Sin direccion..";
	}

	public CtaCteEmpresaDTO getCtaCteEmpresa() {
		return this.ctaCteEmpresa;
	}

	public void setCtaCteEmpresa(CtaCteEmpresaDTO ctaCteEmpresa) {
		this.ctaCteEmpresa = ctaCteEmpresa;
	}

	public MyPair getEmpresaGrupoSociedad() {
		return empresaGrupoSociedad;
	}

	public void setEmpresaGrupoSociedad(MyPair empresaGrupoSociedad) {
		this.empresaGrupoSociedad = empresaGrupoSociedad;
	}

	public MyPair getRegimenTributario() {
		return regimenTributario;
	}

	public void setRegimenTributario(MyPair regimenTributario) {
		this.regimenTributario = regimenTributario;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial.toUpperCase();
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public List<MyPair> getRubroEmpresas() {
		return rubroEmpresas;
	}

	public void setRubroEmpresas(List<MyPair> rubroEmpresas) {
		this.rubroEmpresas = rubroEmpresas;
	}

	public List<MyPair> getMonedas() {
		return monedas;
	}

	public void setMonedas(List<MyPair> monedas) {
		this.monedas = monedas;
	}

	public List<MyArray> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<MyArray> sucursales) {
		this.sucursales = sucursales;
	}

	public List<ContactoDTO> getContactos() {
		return contactos;
	}

	public void setContactos(List<ContactoDTO> contactos) {
		this.contactos = contactos;
	}

	public MyPair getTipoPagoProveedor() {
		return tipoPagoProveedor;
	}

	public void setTipoPagoProveedor(MyPair tipoPagoProveedor) {
		this.tipoPagoProveedor = tipoPagoProveedor;
	}

	public MyPair getTipoProveedor() {
		return tipoProveedor;
	}

	public void setTipoProveedor(MyPair tipoProveedor) {
		this.tipoProveedor = tipoProveedor;
	}

	public int getTipoEmpresa() {
		return tipoEmpresa;
	}

	public void setTipoEmpresa(int tipoEmpresa) {
		this.tipoEmpresa = tipoEmpresa;
	}

	public Date getFechaAniversario() {
		return fechaAniversario;
	}

	public void setFechaAniversario(Date fechaAniversario) {
		this.fechaAniversario = fechaAniversario;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public MyPair getPais() {
		return pais;
	}

	public void setPais(MyPair pais) {
		this.pais = pais;
	}

	public MyPair getMoneda() {
		return moneda;
	}

	public void setMoneda(MyPair moneda) {
		this.moneda = moneda;
	}

	public MyPair getTipoPersona() {
		return tipoPersona;
	}

	public void setTipoPersona(MyPair tipoPersona) {
		this.tipoPersona = tipoPersona;
	}

	public MyArray getMonedaConSimbolo() {
		return monedaConSimbolo;
	}

	public void setMonedaConSimbolo(MyArray monedaConSimbolo) {
		this.monedaConSimbolo = monedaConSimbolo;
	}

	public boolean isRazonSocialSet() {
		return razonSocialSet;
	}

	public void setRazonSocialSet(boolean razonSocialSet) {
		this.razonSocialSet = razonSocialSet;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla.toUpperCase();
	}

	public boolean isCuentaBloqueada() {
		return cuentaBloqueada;
	}

	public void setCuentaBloqueada(boolean cuentaBloqueada) {
		this.cuentaBloqueada = cuentaBloqueada;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}

	public String getDireccion_() {
		return direccion_;
	}

	public void setDireccion_(String direccion_) {
		this.direccion_ = direccion_;
	}

	public String getTelefono_() {
		return telefono_;
	}

	public void setTelefono_(String telefono_) {
		this.telefono_ = telefono_;
	}

	public String getCorreo_() {
		return correo_;
	}

	public void setCorreo_(String correo_) {
		this.correo_ = correo_;
	}
}
