package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Talonario extends Domain {	
	
	private String numero;
	private Date fecha;
	private Date vencimiento;	
	private int bocaExpedicion;
	private int puntoExpedicion;
	private long desde;
	private long hasta;
	private long saldo;
	
	private SucursalApp sucursal;
	private Timbrado timbrado;
	
	private Set<TipoMovimiento> movimientos = new HashSet<TipoMovimiento>();

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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public int getBocaExpedicion() {
		return bocaExpedicion;
	}

	public void setBocaExpedicion(int bocaExpedicion) {
		this.bocaExpedicion = bocaExpedicion;
	}

	public int getPuntoExpedicion() {
		return puntoExpedicion;
	}

	public void setPuntoExpedicion(int puntoExpedicion) {
		this.puntoExpedicion = puntoExpedicion;
	}

	public long getDesde() {
		return desde;
	}

	public void setDesde(long desde) {
		this.desde = desde;
	}

	public long getHasta() {
		return hasta;
	}

	public void setHasta(long hasta) {
		this.hasta = hasta;
	}

	public long getSaldo() {
		return saldo;
	}

	public void setSaldo(long saldo) {
		this.saldo = saldo;
	}

	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}

	public Timbrado getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(Timbrado timbrado) {
		this.timbrado = timbrado;
	}

	public Set<TipoMovimiento> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(Set<TipoMovimiento> movimientos) {
		this.movimientos = movimientos;
	}
}
