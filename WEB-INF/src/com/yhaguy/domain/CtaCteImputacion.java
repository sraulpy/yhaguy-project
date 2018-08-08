package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CtaCteImputacion extends Domain{
	
	private String quienImputa;
	private String dondeImputa;
	private double montoImputado;
	private Tipo tipoImputacion; /*Parcial/Completa*/
	private Tipo moneda;/*Dolar, Gs, Etc.*/
	private double tipoCambio;/*Cambio utilizado para la imputacion*/
	
	public String getDondeImputa() {
		return dondeImputa;
	}
	public void setDondeImputa(String dondeImputa) {
		this.dondeImputa = dondeImputa;
	}
	
	
	public String getQuienImputa() {
		return quienImputa;
	}
	public void setQuienImputa(String quienImputa) {
		this.quienImputa = quienImputa;
	}
	
	
	public double getMontoImputado() {
		return montoImputado;
	}
	public void setMontoImputado(double montoImputado) {
		this.montoImputado = montoImputado;
	}


	public Tipo getTipoImputacion() {
		return tipoImputacion;
	}
	public void setTipoImputacion(Tipo tipoImputacion) {
		this.tipoImputacion = tipoImputacion;
	}
	public Tipo getMoneda() {
		return moneda;
	}
	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}
	public double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
