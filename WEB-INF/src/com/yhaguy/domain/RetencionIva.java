package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class RetencionIva extends Domain {
	
	private Date fecha;
	private String numero;
	private double montoIvaIncluido;
	private double montoIva;
	private double montoRetencion;
	private String observacion;
	private int porcentaje;
	
	private Timbrado timbrado;
	private Tipo estadoComprobante;
	private Empresa empresa;
	
	private Set<RetencionIvaDetalle> detalles = new HashSet<RetencionIvaDetalle>();

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public double getMontoIvaIncluido() {
		return montoIvaIncluido;
	}

	public void setMontoIvaIncluido(double montoIvaIncluido) {
		this.montoIvaIncluido = montoIvaIncluido;
	}

	public double getMontoIva() {
		return montoIva;
	}

	public void setMontoIva(double montoIva) {
		this.montoIva = montoIva;
	}

	public double getMontoRetencion() {
		return montoRetencion;
	}

	public void setMontoRetencion(double montoRetencion) {
		this.montoRetencion = montoRetencion;
	}

	public Timbrado getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(Timbrado timbrado) {
		this.timbrado = timbrado;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Set<RetencionIvaDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<RetencionIvaDetalle> detalles) {
		this.detalles = detalles;
	}

	public int getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(int porcentaje) {
		this.porcentaje = porcentaje;
	}
}
