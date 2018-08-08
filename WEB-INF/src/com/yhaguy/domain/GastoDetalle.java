package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class GastoDetalle extends Domain{

	private String observacion;
	private int cantidad;
	private double montoGs;
	private double montoDs;
	private double montoIva;
	
	private ArticuloGasto articuloGasto;
	private CentroCosto centroCosto;
	private Tipo tipoIva;
	
	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getMontoGs() {
		return montoGs;
	}

	public void setMontoGs(double montoGs) {
		this.montoGs = montoGs;
	}

	public double getMontoDs() {
		return montoDs;
	}

	public void setMontoDs(double montoDs) {
		this.montoDs = montoDs;
	}

	public ArticuloGasto getArticuloGasto() {
		return articuloGasto;
	}

	public void setArticuloGasto(ArticuloGasto articuloGasto) {
		this.articuloGasto = articuloGasto;
	}

	public CentroCosto getCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(CentroCosto centroCosto) {
		this.centroCosto = centroCosto;
	}

	public Tipo getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(Tipo tipoIva) {
		this.tipoIva = tipoIva;
	}

	public double getMontoIva() {
		return montoIva;
	}

	public void setMontoIva(double montoIva) {
		this.montoIva = montoIva;
	}

	@Override
	public int compareTo(Object o) {
		GastoDetalle cmp = (GastoDetalle) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.id) == 0);
		
		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}		
	}

}
