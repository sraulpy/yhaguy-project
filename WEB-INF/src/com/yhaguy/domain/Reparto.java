package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Reparto extends Domain {

	private String numero;
	private Date fechaCreacion;
	private Date fechaRecepcion;
	private String observaciones = "";
	private double costo;
	private Tipo estadoReparto;
	private Tipo tipoReparto;
	private Vehiculo vehiculo;
	private Funcionario repartidor;
	private Funcionario creador;
	private Funcionario receptor;
	private SucursalApp sucursal;

	private Proveedor proveedor;

	private Set<RepartoDetalle> detalles = new HashSet<RepartoDetalle>();	
	private Set<ServicioTecnico> serviciosTecnicos = new HashSet<ServicioTecnico>();

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	public Funcionario getReceptor() {
		return receptor;
	}

	public void setReceptor(Funcionario receptor) {
		this.receptor = receptor;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Tipo getEstadoReparto() {
		return estadoReparto;
	}

	public void setEstadoReparto(Tipo estadoReparto) {
		this.estadoReparto = estadoReparto;
	}

	public Tipo getTipoReparto() {
		return tipoReparto;
	}

	public void setTipoReparto(Tipo tipoReparto) {
		this.tipoReparto = tipoReparto;
	}

	public Vehiculo getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(Vehiculo vehiculo) {
		this.vehiculo = vehiculo;
	}

	public Funcionario getRepartidor() {
		return repartidor;
	}

	public void setRepartidor(Funcionario repartidor) {
		this.repartidor = repartidor;
	}

	public Funcionario getCreador() {
		return creador;
	}

	public void setCreador(Funcionario creador) {
		this.creador = creador;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public double getCosto() {
		return costo;
	}

	public void setCosto(double costo) {
		this.costo = costo;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Set<RepartoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<RepartoDetalle> detalles) {
		this.detalles = detalles;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set<ServicioTecnico> getServiciosTecnicos() {
		return serviciosTecnicos;
	}

	public void setServiciosTecnicos(Set<ServicioTecnico> serviciosTecnicos) {
		this.serviciosTecnicos = serviciosTecnicos;
	}

}
