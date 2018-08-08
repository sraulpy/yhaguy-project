package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class Empresa extends Domain {
	
	public static final String DESBLOQUEO_TEMPORAL = "DESBLOQUEO_TEMPORAL";
	
	private String nombre = "";
	private String codigoEmpresa = "";
	private String razonSocial = "";
	private String ruc = "";
	private String ci = "";
	private Tipo pais;
	private Tipo tipoPersona;
	private Tipo regimenTributario;
	private String web = "";
	private String sigla;
	
	private String direccion_ = "";
	private String telefono_ = "";
	private String correo_ = "";
	private String zona = "";

	private String observacion = "";
	private Date fechaIngreso;
	private Date fechaAniversario;
	
	private String latitud = "";
	private String longitud = "";
	private String userlocation = "";
	
	// Bloqueo de Cuenta Corriente..
	private boolean cuentaBloqueada;
	private String motivoBloqueo;
	
	private Funcionario vendedor;
	private Funcionario cobrador;

	private EmpresaGrupoSociedad empresaGrupoSociedad;

	private CtaCteEmpresa ctaCteEmpresa;

	Set<Tipo> rubroEmpresas = new HashSet<Tipo>();

	Set<Sucursal> sucursales = new HashSet<Sucursal>();
	Tipo moneda;
	Set<Tipo> monedas = new HashSet<Tipo>();
	Set<Contacto> contactos = new HashSet<Contacto>();
	
	public Empresa() {
	}
	
	/**
	 * @return true si es una aseguradora..
	 */
	public boolean isAseguradora() {
		for (Tipo rubro : this.rubroEmpresas) {
			if (rubro.getSigla().equals(Configuracion.SIGLA_TIPO_RUBRO_EMP_ASEGURADORA)) {
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * @return true si es del rubro segun sigla..
	 */
	public boolean isRubro(long idRubro) {
		for (Tipo rubro : this.rubroEmpresas) {
			if (rubro.getId().longValue() == idRubro) {
				return true;
			}
		}		
		return false;
	}
	
	public String getTelefono() {
		for (Sucursal sucursal : this.getSucursales()) {
			return sucursal.getTelefono();
		}
		return "...";
	}
	
	public String getDireccion() {
		for (Sucursal sucursal : this.getSucursales()) {
			return sucursal.getDireccion();
		}
		return "Sin direccion..";
	}
	
	public String getCorreo() {
		for (Sucursal sucursal : this.getSucursales()) {
			return sucursal.getCorreo();
		}
		return "...";
	}
	
	public void setDireccion(String direccion) {
	}

	public EmpresaGrupoSociedad getEmpresaGrupoSociedad() {
		return empresaGrupoSociedad;
	}

	public void setEmpresaGrupoSociedad(
			EmpresaGrupoSociedad empresaGrupoSociedad) {
		this.empresaGrupoSociedad = empresaGrupoSociedad;
	}

	public Tipo getRegimenTributario() {
		return regimenTributario;
	}

	public void setRegimenTributario(Tipo regimenTributario) {
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

	public Empresa(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public Empresa(String razonSocial, String ruc) {
		this.razonSocial = razonSocial;
		this.ruc = ruc;
	}

	public String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	public String getRazonSocial() {
		return razonSocial.toUpperCase();
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
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

	public Set<Tipo> getRubroEmpresas() {
		return rubroEmpresas;
	}

	public void setRubroEmpresas(Set<Tipo> rubroEmpresas) {
		this.rubroEmpresas = rubroEmpresas;
	}

	public Set<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(Set<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}

	public Set<Tipo> getMonedas() {
		return monedas;
	}

	public void setMonedas(Set<Tipo> monedas) {
		this.monedas = monedas;
	}

	public Set<Contacto> getContactos() {
		return contactos;
	}

	public void setContactos(Set<Contacto> contactos) {
		this.contactos = contactos;
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

	public Tipo getPais() {
		return pais;
	}

	public void setPais(Tipo pais) {
		this.pais = pais;
	}

	public Tipo getTipoPersona() {
		return tipoPersona;
	}

	public void setTipoPersona(Tipo tipoPersona) {
		this.tipoPersona = tipoPersona;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	public CtaCteEmpresa getCtaCteEmpresa() {
		return ctaCteEmpresa;
	}

	public void setCtaCteEmpresa(CtaCteEmpresa ctaCteEmpresa) {
		this.ctaCteEmpresa = ctaCteEmpresa;
	}

	public String toString() {
		String out = "";
		out += this.getRazonSocial();
		return out;
	}

	@Override
	public int compareTo(Object o) {
		Empresa cmp = (Empresa) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}

	public boolean isCuentaBloqueada() {
		return cuentaBloqueada;
	}

	public void setCuentaBloqueada(boolean cuentaBloqueada) {
		this.cuentaBloqueada = cuentaBloqueada;
	}

	public String getMotivoBloqueo() {
		return motivoBloqueo;
	}

	public void setMotivoBloqueo(String motivoBloqueo) {
		this.motivoBloqueo = motivoBloqueo;
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

	public String getZona() {
		return zona == null || zona.trim().isEmpty()? "SIN ZONA" : zona ;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public Funcionario getVendedor() {
		return vendedor;
	}

	public void setVendedor(Funcionario vendedor) {
		this.vendedor = vendedor;
	}

	public Funcionario getCobrador() {
		return cobrador;
	}

	public void setCobrador(Funcionario cobrador) {
		this.cobrador = cobrador;
	}
}
