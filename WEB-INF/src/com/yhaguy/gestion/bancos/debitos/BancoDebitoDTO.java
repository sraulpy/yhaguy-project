package com.yhaguy.gestion.bancos.debitos;

import java.util.Date;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;

@SuppressWarnings("serial")
public class BancoDebitoDTO extends DTO {

	private Date fecha;
	private String numero = "";
	private String descripcion = "";
	private double importe = 0;
	private boolean confirmado;

	private BancoCtaDTO cuenta;
	private MyPair sucursal;

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
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
		this.descripcion = descripcion.toUpperCase();
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public BancoCtaDTO getCuenta() {
		return cuenta;
	}

	public void setCuenta(BancoCtaDTO cuenta) {
		this.cuenta = cuenta;
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}
}
