package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class HistoricoVentaDiaria extends Domain {

	private Date fecha;
	private long id_funcionario;
	private double total_venta;
	private double total_notacredito;

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public long getId_funcionario() {
		return id_funcionario;
	}

	public void setId_funcionario(long id_funcionario) {
		this.id_funcionario = id_funcionario;
	}

	public double getTotal_venta() {
		return total_venta;
	}

	public void setTotal_venta(double total_venta) {
		this.total_venta = total_venta;
	}

	public double getTotal_notacredito() {
		return total_notacredito;
	}

	public void setTotal_notacredito(double total_notacredito) {
		this.total_notacredito = total_notacredito;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}
