package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AnalisisReposicionDetalle extends Domain {
	
	private int ranking;
	private String codigoInterno;
	private String descripcion;
	private double ventasUnidades;
	private double ventasImporte;
	private double pedidoReposicion;
	private double comprasUnidades;
	private double importacionUnidades;
	private double devoluciones;
	private double sugerido;
	private long stock;
	private String observacion;
	private double promedio;

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public double getVentasUnidades() {
		return ventasUnidades;
	}

	public void setVentasUnidades(double ventasUnidades) {
		this.ventasUnidades = ventasUnidades;
	}

	public double getVentasImporte() {
		return ventasImporte;
	}

	public void setVentasImporte(double ventasImporte) {
		this.ventasImporte = ventasImporte;
	}

	public double getPedidoReposicion() {
		return pedidoReposicion;
	}

	public void setPedidoReposicion(double pedidoReposicion) {
		this.pedidoReposicion = pedidoReposicion;
	}

	public double getComprasUnidades() {
		return comprasUnidades;
	}

	public void setComprasUnidades(double comprasUnidades) {
		this.comprasUnidades = comprasUnidades;
	}

	public double getSugerido() {
		return sugerido;
	}

	public void setSugerido(double sugerido) {
		this.sugerido = sugerido;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public double getImportacionUnidades() {
		return importacionUnidades;
	}

	public void setImportacionUnidades(double importacionUnidades) {
		this.importacionUnidades = importacionUnidades;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public long getStock() {
		return stock;
	}

	public void setStock(long stock) {
		this.stock = stock;
	}

	public double getDevoluciones() {
		return devoluciones;
	}

	public void setDevoluciones(double devoluciones) {
		this.devoluciones = devoluciones;
	}

	public double getPromedio() {
		return promedio;
	}

	public void setPromedio(double promedio) {
		this.promedio = promedio;
	}
}
