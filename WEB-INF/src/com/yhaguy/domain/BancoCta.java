package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class BancoCta extends Domain{
	
	private String nroCuenta;
	private Date fechaApertura;
	
	private Banco banco;
	private Tipo tipo; //ej: cuenta corriente - caja de ahorro
	private Tipo moneda;	
	private CuentaContable cuentaContable;	
	
	public String getBancoDescripcion(){
		return this.banco.getDescripcion().toUpperCase();
	}
	
	public void setBancoDescripcion(String bancoD){
		this.getBanco().setDescripcion(bancoD);
	}

	public Date getFechaApertura() {
		return fechaApertura;
	}

	public void setFechaApertura(Date fechaApertura) {
		this.fechaApertura = fechaApertura;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	
	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	@Override
	public int compareTo(Object o) {

		BancoCta cmp = (BancoCta) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}

	public String getNroCuenta() {
		return nroCuenta;
	}

	public void setNroCuenta(String nroCuenta) {
		this.nroCuenta = nroCuenta;
	}

	public CuentaContable getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(CuentaContable cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

}
