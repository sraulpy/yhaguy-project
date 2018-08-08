package com.yhaguy.domain;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class Proveedor extends Domain {

	Empresa empresa;
	Tipo estadoProveedor;
	Tipo tipoProveedor;
	CuentaContable cuentaContable;
	Set<CondicionPago> condicionPagos = new HashSet<CondicionPago>();
	Set<Timbrado> timbrados = new HashSet<Timbrado>();
	private Hashtable<Tipo, String> emails;
	private String emailsLista = "";
	long prioridad = 0;
	boolean completo = false;
	
	private String descripcion;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return true si es proveedor exterior..
	 */
	public boolean isProveedorExterior() {
		if(this.tipoProveedor == null) return false;
		return this.tipoProveedor.getSigla().equals(Configuracion.SIGLA_PROVEEDOR_TIPO_EXTERIOR);
	}
	
	public String getDireccion() {
		for (Sucursal sucursal : this.empresa.getSucursales()) {
			return sucursal.getDireccion();
		}
		return "Sin direccion..";
	}
	
	public void setDireccion(String direccion) {
	}
	
	public String getTelefono() {
		for (Sucursal sucursal : this.empresa.getSucursales()) {
			return sucursal.getTelefono();
		}
		return "...";
	}
	
	public void setTelefono(String direccion) {
	}

	public Tipo getEstadoProveedor() {
		return estadoProveedor;
	}

	public void setEstadoProveedor(Tipo estadoProveedor) {
		this.estadoProveedor = estadoProveedor;
	}

	public Tipo getTipoProveedor() {
		return tipoProveedor;
	}

	public void setTipoProveedor(Tipo tipoProveedor) {
		this.tipoProveedor = tipoProveedor;
	}

	public long getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(long prioridad) {
		this.prioridad = prioridad;
	}

	public boolean isCompleto() {
		return completo;
	}

	public void setCompleto(boolean completo) {
		this.completo = completo;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public Set<CondicionPago> getCondicionPagos() {
		return condicionPagos;
	}

	public void setCondicionPagos(Set<CondicionPago> condicionPagos) {
		this.condicionPagos = condicionPagos;
	}

	public Set<Timbrado> getTimbrados() {
		return timbrados;
	}

	public void setTimbrados(Set<Timbrado> timbrados) {
		this.timbrados = timbrados;
	}

	public Hashtable<Tipo, String> getEmails() {
		return emails;
	}

	public void setEmails(Hashtable<Tipo, String> emails) {
		this.emails = emails;
	}

	public String getEmailsLista() {
		return emailsLista;
	}

	public void setEmailsLista(String emailsLista) {
		this.emailsLista = emailsLista;
	}
	
	public long getIdEmpresa(){
		return empresa.getId();
	}
	
	public void setIdEmpresa(long idEmpresa){
		//empresa.setId(idEmpresa);
	}
	
	public String getCodigoEmpresa(){
		return empresa.getCodigoEmpresa();
	}
	
	public void setCodigoEmpresa(String codigoEmpresa){
		empresa.setCodigoEmpresa(codigoEmpresa);
	}
	
	public String getRazonSocial(){
		return empresa.getRazonSocial();
	}
	
	public void setRazonSocial(String razonSocial){
		empresa.setRazonSocial(razonSocial);
	}
	
	public String getRuc(){
		return empresa.getRuc();
	}
	
	public void setRuc(String ruc){
		empresa.setRuc(ruc);
	}
	
	public Tipo getMoneda(){
		return empresa.getMoneda();
	}
	
	public void setMoneda(Tipo moneda){
		empresa.setMoneda(moneda);
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getDescripcion() {
		this.descripcion = this.empresa.getRazonSocial();
		return this.descripcion;
	}
}
