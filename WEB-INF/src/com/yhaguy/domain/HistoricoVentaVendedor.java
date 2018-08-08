package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class HistoricoVentaVendedor extends Domain {

	private int mes;
	private int anho;
	private long id_funcionario;
	private double total_venta;
	private double total_venta_servicio;
	private double total_notacredito;
	private double meta;

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAnho() {
		return anho;
	}

	public void setAnho(int anho) {
		this.anho = anho;
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

	public double getMeta() {
		return meta;
	}

	public void setMeta(double meta) {
		this.meta = meta;
	}

	public double getTotal_notacredito() {
		return total_notacredito;
	}

	public void setTotal_notacredito(double total_notacredito) {
		this.total_notacredito = total_notacredito;
	}

	public double getTotal_venta_servicio() {
		return total_venta_servicio;
	}

	public void setTotal_venta_servicio(double total_venta_servicio) {
		this.total_venta_servicio = total_venta_servicio;
	}

}
