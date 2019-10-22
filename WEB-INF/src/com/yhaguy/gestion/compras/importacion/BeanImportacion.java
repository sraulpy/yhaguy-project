package com.yhaguy.gestion.compras.importacion;

import java.util.Date;

public class BeanImportacion {

	private String fechaDespacho;
	private String numeroImportacion;
	private String numeroFactura;
	private String cantidad;
	private String observacion;
	private String proveedor;
	private String fobgs;
	private String fobds;
	private String cifgs;
	private String cifds;
	private String tipocambio;
	
	private Date fechaDespacho_;
	
	public BeanImportacion(String fechaDespacho, String numeroImportacion, String numeroFactura, String cantidad,
			String observacion, String proveedor, String fobgs, String fobds, String cifgs, String cifds, Date fechaDespacho_,
			String tipocambio) {
		this.fechaDespacho = fechaDespacho;
		this.numeroImportacion = numeroImportacion;
		this.numeroFactura = numeroFactura;
		this.cantidad = cantidad;
		this.observacion = observacion;
		this.proveedor = proveedor;
		this.fobgs = fobgs;
		this.fobds = fobds;
		this.cifgs = cifgs;
		this.cifds = cifds;
		this.tipocambio = tipocambio;
		this.fechaDespacho_ = fechaDespacho_;
	}
	public String getFechaDespacho() {
		return fechaDespacho;
	}
	public void setFechaDespacho(String fechaDespacho) {
		this.fechaDespacho = fechaDespacho;
	}
	public String getNumeroImportacion() {
		return numeroImportacion;
	}
	public void setNumeroImportacion(String numeroImportacion) {
		this.numeroImportacion = numeroImportacion;
	}
	public String getNumeroFactura() {
		return numeroFactura;
	}
	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}
	public String getCantidad() {
		return cantidad;
	}
	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	public String getFobgs() {
		return fobgs;
	}
	public void setFobgs(String fobgs) {
		this.fobgs = fobgs;
	}
	public String getFobds() {
		return fobds;
	}
	public void setFobds(String fobds) {
		this.fobds = fobds;
	}
	public String getCifgs() {
		return cifgs;
	}
	public void setCifgs(String cifgs) {
		this.cifgs = cifgs;
	}
	public String getCifds() {
		return cifds;
	}
	public void setCifds(String cifds) {
		this.cifds = cifds;
	}
	public Date getFechaDespacho_() {
		return fechaDespacho_;
	}
	public void setFechaDespacho_(Date fechaDespacho_) {
		this.fechaDespacho_ = fechaDespacho_;
	}
	public String getTipocambio() {
		return tipocambio;
	}
	public void setTipocambio(String tipocambio) {
		this.tipocambio = tipocambio;
	}	
}
