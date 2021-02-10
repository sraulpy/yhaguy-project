package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloReposicion extends Domain {
	
	public static final String ESTADO_PENDIENTE = "PENDIENTE";
	public static final String ESTADO_GENERADO = "GENERADO";
	public static final String ESTADO_CERRADO = "CERRADO";
	
	private Date fecha;
	private String observacion;
	private String solicitante;
	private int cantidad = 1;	
	private String numeroOrdenCompra;
	private String estado = ESTADO_PENDIENTE;
	
	private Articulo articulo;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}

	public String getNumeroOrdenCompra() {
		return numeroOrdenCompra;
	}

	public void setNumeroOrdenCompra(String numeroOrdenCompra) {
		this.numeroOrdenCompra = numeroOrdenCompra;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
