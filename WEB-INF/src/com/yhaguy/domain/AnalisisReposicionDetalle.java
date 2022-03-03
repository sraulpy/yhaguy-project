package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AnalisisReposicionDetalle extends Domain {
	
	private int ranking;
	private String codigoInterno;
	private double ventasUnidades;
	private double ventasImporte;
	private double pedidoReposicion;
	private double comprasUnidades;
	private double sugerido;
	private String observacion;

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
}
