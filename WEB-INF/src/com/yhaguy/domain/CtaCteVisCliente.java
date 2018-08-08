package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CtaCteVisCliente extends Domain {

	private long row = -1;
	private long idEmpresa = -1;
	private String razonSocial = "";
	private Date fechaAperturaCuentaCliente;
	private Tipo estadoComoCliente;
	private CtaCteLineaCredito lineaCredito;
	private Tipo tipoMoneda;
	private double pendiente;

	public long getRow() {
		return row;
	}

	public void setRow(long row) {
		this.row = row;
	}

	public long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public Date getFechaAperturaCuentaCliente() {
		return fechaAperturaCuentaCliente;
	}

	public void setFechaAperturaCuentaCliente(Date fechaAperturaCuentaCliente) {
		this.fechaAperturaCuentaCliente = fechaAperturaCuentaCliente;
	}

	public Tipo getEstadoComoCliente() {
		return estadoComoCliente;
	}

	public void setEstadoComoCliente(Tipo estadoComoCliente) {
		this.estadoComoCliente = estadoComoCliente;
	}

	public CtaCteLineaCredito getLineaCredito() {
		return lineaCredito;
	}

	public void setLineaCredito(CtaCteLineaCredito lineaCredito) {
		this.lineaCredito = lineaCredito;
	}

	public Tipo getTipoMoneda() {
		return tipoMoneda;
	}

	public void setTipoMoneda(Tipo tipoMoneda) {
		this.tipoMoneda = tipoMoneda;
	}

	public double getPendiente() {
		return pendiente;
	}

	public void setPendiente(double pendiente) {
		this.pendiente = pendiente;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}