package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class BancoExtractoDetalle extends Domain {
	
	private Date fecha;
	private String numero;
	private String descripcion;
	private double debe;
	private double haber;
	private boolean conciliado;
	
	private BancoMovimiento bancoMovimiento;

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BancoMovimiento getBancoMovimiento() {
		return bancoMovimiento;
	}

	public void setBancoMovimiento(BancoMovimiento bancoMovimiento) {
		this.bancoMovimiento = bancoMovimiento;
	}

	public boolean isConciliado() {
		return conciliado;
	}

	public void setConciliado(boolean conciliado) {
		this.conciliado = conciliado;
	}

	public double getDebe() {
		return debe;
	}

	public void setDebe(double debe) {
		this.debe = debe;
	}

	public double getHaber() {
		return haber;
	}

	public void setHaber(double haber) {
		this.haber = haber;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}
