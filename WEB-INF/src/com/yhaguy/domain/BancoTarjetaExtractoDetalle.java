package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class BancoTarjetaExtractoDetalle extends Domain {

	private Tipo tipoDetalle;
	private double importe = 0;
	private double cuota = 0;
	private double saldo = 0;
	private String referencia = "";
	private Date fecha;
	private long idBancoTarjeta = 0;

	public Tipo getTipoDetalle() {
		return tipoDetalle;
	}

	public void setTipoDetalle(Tipo tipoDetalle) {
		this.tipoDetalle = tipoDetalle;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public long getIdBancoTarjeta() {
		return idBancoTarjeta;
	}

	public void setIdBancoTarjeta(long idBancoTarjeta) {
		this.idBancoTarjeta = idBancoTarjeta;
	}

	public double getCuota() {
		return cuota;
	}

	public void setCuota(double cuota) {
		this.cuota = cuota;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
