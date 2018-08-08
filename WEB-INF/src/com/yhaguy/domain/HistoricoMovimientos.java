package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;


@SuppressWarnings("serial")
public class HistoricoMovimientos extends Domain {
	
	private Date fechaProceso;
	private long idCliente;
	private String ruc;
	private String razonSocial;
	private double totalVentasContado;
	private double totalVentasCredito;
	private double totalNotasDeCredito;
	private double totalSaldoGs;
	private double totalChequePendientesGs;

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public double getTotalVentasContado() {
		return totalVentasContado;
	}

	public void setTotalVentasContado(double totalVentasContado) {
		this.totalVentasContado = totalVentasContado;
	}

	public double getTotalVentasCredito() {
		return totalVentasCredito;
	}

	public void setTotalVentasCredito(double totalVentasCredito) {
		this.totalVentasCredito = totalVentasCredito;
	}

	public double getTotalNotasDeCredito() {
		return totalNotasDeCredito;
	}

	public void setTotalNotasDeCredito(double totalNotasDeCredito) {
		this.totalNotasDeCredito = totalNotasDeCredito;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public double getTotalSaldoGs() {
		return totalSaldoGs;
	}

	public void setTotalSaldoGs(double totalSaldoGs) {
		this.totalSaldoGs = totalSaldoGs;
	}

	public double getTotalChequePendientesGs() {
		return totalChequePendientesGs;
	}

	public void setTotalChequePendientesGs(double totalChequePendientesGs) {
		this.totalChequePendientesGs = totalChequePendientesGs;
	}
}
