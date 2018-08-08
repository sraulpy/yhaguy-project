package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ServicioTecnicoDetalle extends Domain {
	
	private String estado;
	private String verifica_carga;
	private String verifica_descarga;
	private String verifica_borne;
	private String verifica_celda;
	private String verifica_conexion;
	private boolean verifica_fallaFabrica;
	private boolean verifica_reposicion;
	private String observacion;
	private String diagnostico;
	private String numeroFactura;
	
	private Articulo articulo;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getVerifica_carga() {
		return verifica_carga;
	}

	public void setVerifica_carga(String verifica_carga) {
		this.verifica_carga = verifica_carga;
	}

	public String getVerifica_descarga() {
		return verifica_descarga;
	}

	public void setVerifica_descarga(String verifica_descarga) {
		this.verifica_descarga = verifica_descarga;
	}

	public String getVerifica_borne() {
		return verifica_borne;
	}

	public void setVerifica_borne(String verifica_borne) {
		this.verifica_borne = verifica_borne;
	}

	public String getVerifica_celda() {
		return verifica_celda;
	}

	public void setVerifica_celda(String verifica_celda) {
		this.verifica_celda = verifica_celda;
	}

	public String getVerifica_conexion() {
		return verifica_conexion;
	}

	public void setVerifica_conexion(String verifica_conexion) {
		this.verifica_conexion = verifica_conexion;
	}

	public boolean isVerifica_fallaFabrica() {
		return verifica_fallaFabrica;
	}

	public void setVerifica_fallaFabrica(boolean verifica_fallaFabrica) {
		this.verifica_fallaFabrica = verifica_fallaFabrica;
	}

	public boolean isVerifica_reposicion() {
		return verifica_reposicion;
	}

	public void setVerifica_reposicion(boolean verifica_reposicion) {
		this.verifica_reposicion = verifica_reposicion;
	}

	public String getObservacion() {
		return observacion;
	}
	
	public String getObservacion_() {
		return (this.observacion == null || this.observacion.isEmpty()) ? "" : "(" + this.observacion + ")";
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

}
