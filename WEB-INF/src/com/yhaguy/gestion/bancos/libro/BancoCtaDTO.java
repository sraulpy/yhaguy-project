package com.yhaguy.gestion.bancos.libro;

import java.util.Date;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class BancoCtaDTO extends DTO {
	
	private String nroCuenta;
	private Date fechaApertura = new Date();
	
	private MyArray banco = new MyArray();
	private MyPair tipo;
	private MyArray moneda = new MyArray();
	private MyArray cuentaContable = new MyArray();
	
	@Override
	public String[] getCamposMyPair() {
		String[] cs = {"nroCuenta"};
		return cs;
	}
	
	public String getBancoDescripcion(){
		return this.banco.getPos1().toString();
	}
	
	public void setBancoDescripcion(String bancoD){
		this.getBanco().setPos1(bancoD);
	}
	
	public Date getFechaApertura() {
		return fechaApertura;
	}
	
	public void setFechaApertura(Date fechaApertura) {
		this.fechaApertura = fechaApertura;
	}
	
	public MyArray getBanco() {
		return banco;
	}
	
	public void setBanco(MyArray banco) {
		this.banco = banco;
	}
	
	public MyPair getTipo() {
		return tipo;
	}
	
	public void setTipo(MyPair tipo) {
		this.tipo = tipo;
	}
	
	public MyArray getMoneda() {
		return moneda;
	}
	
	public void setMoneda(MyArray moneda) {
		this.moneda = moneda;
	}

	public String getNroCuenta() {
		return nroCuenta;
	}

	public void setNroCuenta(String nroCuenta) {
		this.nroCuenta = nroCuenta;
	}

	public MyArray getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(MyArray cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

}
