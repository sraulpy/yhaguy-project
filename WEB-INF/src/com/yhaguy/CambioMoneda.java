package com.yhaguy;

import java.util.Date;

public class CambioMoneda {

	long tipoMoneda = 0;

	double BCPCompra = 0;
	double BCPVenta = 0;
	Date BCPFecha = new Date();
	Date BCPFechaCarga = new Date();

	double APPCompra = 0;
	double APPVenta = 0;
	Date APPFecha = new Date();
	Date APPFechaCarga = new Date();


	
	public long getTipoMoneda() {
		return tipoMoneda;
	}

	public void setTipoMoneda(long tipoMoneda) {
		this.tipoMoneda = tipoMoneda;
	}

	public double getBCPCompra() {
		return BCPCompra;
	}

	public void setBCPCompra(double bCPCompra) {
		BCPCompra = bCPCompra;
	}

	public double getBCPVenta() {
		return BCPVenta;
	}

	public void setBCPVenta(double bCPVenta) {
		BCPVenta = bCPVenta;
	}

	public Date getBCPFechaCarga() {
		return BCPFechaCarga;
	}

	public void setBCPFechaCarga(Date bCPFechaCarga) {
		BCPFechaCarga = bCPFechaCarga;
	}

	public double getAPPCompra() {
		return APPCompra;
	}

	public void setAPPCompra(double aPPCompra) {
		APPCompra = aPPCompra;
	}

	public double getAPPVenta() {
		return APPVenta;
	}

	public void setAPPVenta(double aPPVenta) {
		APPVenta = aPPVenta;
	}

	public Date getAPPFechaCarga() {
		return APPFechaCarga;
	}

	public void setAPPFechaCarga(Date aPPFechaCarga) {
		APPFechaCarga = aPPFechaCarga;
	}

	public Date getBCPFecha() {
		return BCPFecha;
	}

	public void setBCPFecha(Date bCPFecha) {
		BCPFecha = bCPFecha;
	}

	public Date getAPPFecha() {
		return APPFecha;
	}

	public void setAPPFecha(Date aPPFecha) {
		APPFecha = aPPFecha;
	}

	
}
