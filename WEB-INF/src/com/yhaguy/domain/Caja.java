package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Caja extends Domain{
	
	private String numero;
	private String descripcion;
	private Date fecha;
	
	private Tipo clasificacion;		//ej: prov. locales - prov. internacionales
	private Tipo tipo;				//ej: caja pagos a proveedores - caja chica
	private Tipo estado;			//ej: activo - inactivo
	private Funcionario responsable;
	private Funcionario creador;
	private SucursalApp sucursal;
	private Tipo duracion;			//ej: cierre diario - semanal - mensual
	
	private double fondo;			//el fondo minimo de la caja..
	
	// acciones permitidas..
	private boolean cobro;
	private boolean reposicion;
	private boolean facturacion;
	private boolean pago;
	private boolean gasto;
	private boolean egreso;
	private boolean notaCredito;
	
	private Talonario talonarioVentas;
	private Talonario talonarioNotasCredito;
	private Talonario talonarioAutoFacturas;
	private Talonario talonarioRecibos;
	private Talonario talonarioRetenciones;
	
	private Set<Funcionario> supervisores = new HashSet<Funcionario>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Tipo getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(Tipo clasificacion) {
		this.clasificacion = clasificacion;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Tipo getEstado() {
		return estado;
	}

	public void setEstado(Tipo estado) {
		this.estado = estado;
	}

	public Funcionario getResponsable() {
		return responsable;
	}

	public void setResponsable(Funcionario responsable) {
		this.responsable = responsable;
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

	public Tipo getDuracion() {
		return duracion;
	}

	public void setDuracion(Tipo duracion) {
		this.duracion = duracion;
	}

	public Set<Funcionario> getSupervisores() {
		return supervisores;
	}

	public void setSupervisores(Set<Funcionario> supervisores) {
		this.supervisores = supervisores;
	}

	public double getFondo() {
		return fondo;
	}

	public void setFondo(double fondo) {
		this.fondo = fondo;
	}

	public Talonario getTalonarioVentas() {
		return talonarioVentas;
	}

	public void setTalonarioVentas(Talonario talonario) {
		this.talonarioVentas = talonario;
	}

	public Talonario getTalonarioNotasCredito() {
		return talonarioNotasCredito;
	}

	public void setTalonarioNotasCredito(Talonario talonarioNotasCredito) {
		this.talonarioNotasCredito = talonarioNotasCredito;
	}

	public Talonario getTalonarioAutoFacturas() {
		return talonarioAutoFacturas;
	}

	public void setTalonarioAutoFacturas(Talonario talonarioAutoFacturas) {
		this.talonarioAutoFacturas = talonarioAutoFacturas;
	}

	public Talonario getTalonarioRecibos() {
		return talonarioRecibos;
	}

	public void setTalonarioRecibos(Talonario talonarioRecibos) {
		this.talonarioRecibos = talonarioRecibos;
	}

	public Talonario getTalonarioRetenciones() {
		return talonarioRetenciones;
	}

	public void setTalonarioRetenciones(Talonario talonarioRetenciones) {
		this.talonarioRetenciones = talonarioRetenciones;
	}

	public boolean isCobro() {
		return cobro;
	}

	public void setCobro(boolean cobro) {
		this.cobro = cobro;
	}

	public boolean isReposicion() {
		return reposicion;
	}

	public void setReposicion(boolean reposicion) {
		this.reposicion = reposicion;
	}

	public boolean isFacturacion() {
		return facturacion;
	}

	public void setFacturacion(boolean facturacion) {
		this.facturacion = facturacion;
	}

	public boolean isPago() {
		return pago;
	}

	public void setPago(boolean pago) {
		this.pago = pago;
	}

	public boolean isGasto() {
		return gasto;
	}

	public void setGasto(boolean gasto) {
		this.gasto = gasto;
	}

	public boolean isEgreso() {
		return egreso;
	}

	public void setEgreso(boolean egreso) {
		this.egreso = egreso;
	}

	public boolean isNotaCredito() {
		return notaCredito;
	}

	public void setNotaCredito(boolean notaCredito) {
		this.notaCredito = notaCredito;
	}
}
