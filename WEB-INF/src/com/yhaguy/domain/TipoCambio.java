package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class TipoCambio extends Domain {

	private Tipo moneda;
	private Tipo tipoCambio;
	private Date fecha = new Date();
	private double compra = 0;
	private double venta = 0;
	private String fechaString;


	public String toString(){
		String out = "";
		out += " moneda:"+this.moneda;
		out += " tipoCambio:" + this.tipoCambio;
		out += " fecha:"+ this.fecha;
		out += " compra:"+ this.compra;
		out += " venta:"+ this.venta;
		return out;
	}
	
	public Tipo getMoneda() {
		return moneda;
	}


	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}


	public Tipo getTipoCambio() {
		return tipoCambio;
	}


	public void setTipoCambio(Tipo tipoCambio) {
		this.tipoCambio = tipoCambio;
	}


	public Date getFecha() {
		return fecha;
	}


	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}


	public double getCompra() {
		return compra;
	}


	public void setCompra(double compra) {
		this.compra = compra;
	}


	public double getVenta() {
		return venta;
	}


	public void setVenta(double venta) {
		this.venta = venta;
	}


	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getFechaString() {
		return fechaString;
	}

	public void setFechaString(String fechaString) {
		this.fechaString = fechaString;
	}

}
