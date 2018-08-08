package com.yhaguy.gestion.empresa.ctacte;

import com.coreweb.domain.Tipo;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;

public class CtaCteImputacionDTO extends DTO {

	private String quienImputa = "";
	private String dondeImputa = "";
	private double montoImputado = 0;
	private MyPair tipoImputacion = new MyPair(); /*Parcial/Completa*/
	private MyPair moneda = new MyPair();/*Dolar, Gs, Etc.*/
	private double tipoCambio = 0;/*Cambio utilizado para la imputacion*/
	public String getQuienImputa() {
		return quienImputa;
	}
	public void setQuienImputa(String quienImputa) {
		this.quienImputa = quienImputa;
	}
	public String getDondeImputa() {
		return dondeImputa;
	}
	public void setDondeImputa(String dondeImputa) {
		this.dondeImputa = dondeImputa;
	}
	public double getMontoImputado() {
		return montoImputado;
	}
	public void setMontoImputado(double montoImputado) {
		this.montoImputado = montoImputado;
	}
	public MyPair getTipoImputacion() {
		return tipoImputacion;
	}
	public void setTipoImputacion(MyPair tipoImputacion) {
		this.tipoImputacion = tipoImputacion;
	}
	public MyPair getMoneda() {
		return moneda;
	}
	public void setMoneda(MyPair moneda) {
		this.moneda = moneda;
	}
	public double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	
}
