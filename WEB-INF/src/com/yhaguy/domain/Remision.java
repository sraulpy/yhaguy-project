package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Remision extends Domain {
	
	private Date fecha;
	private String numero;
	private double importeGs;
	private String observacion = "";
	private String vehiculo = "";
	
	private String cdc;
	private String respuestaSET = "Pendiente";
	private String observacionSET;
	private String url;
	
	private Venta venta;
	private Set<RemisionDetalle> detalles = new HashSet<RemisionDetalle>();

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

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public Set<RemisionDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<RemisionDetalle> detalles) {
		this.detalles = detalles;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(String vehiculo) {
		this.vehiculo = vehiculo;
	}

	public String getCdc() {
		return cdc;
	}

	public void setCdc(String cdc) {
		this.cdc = cdc;
	}

	public String getRespuestaSET() {
		return respuestaSET;
	}

	public void setRespuestaSET(String respuestaSET) {
		this.respuestaSET = respuestaSET;
	}

	public String getObservacionSET() {
		return observacionSET;
	}

	public void setObservacionSET(String observacionSET) {
		this.observacionSET = observacionSET;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
