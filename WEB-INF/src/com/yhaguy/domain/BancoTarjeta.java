package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class BancoTarjeta extends Domain {

	/*
	 * No se pone depositado porque cuando sean cuotas, entonces no se puede
	 * poner varios depositados.
	 */

	SucursalApp sucursalApp;
	ReciboFormaPago reciboFormaPago;
	Date fecha;
	String tarjetaNumero = "";
	String comprobanteNumero = "";
	ProcesadoraTarjeta procesadora;
	Tipo tarjetaTipo = null;

	int cuotas = 0;
	double importe = 0;
	// esto es porque en cada cuota se va descontando el pago.
	double saldo = 0;
	
	private boolean anulado;

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public SucursalApp getSucursalApp() {
		return sucursalApp;
	}

	public void setSucursalApp(SucursalApp sucursalApp) {
		this.sucursalApp = sucursalApp;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getTarjetaNumero() {
		return tarjetaNumero;
	}

	public void setTarjetaNumero(String tarjetaNumero) {
		this.tarjetaNumero = tarjetaNumero;
	}

	public String getComprobanteNumero() {
		return comprobanteNumero;
	}

	public void setComprobanteNumero(String comprobanteNumero) {
		this.comprobanteNumero = comprobanteNumero;
	}

	public ProcesadoraTarjeta getProcesadora() {
		return procesadora;
	}

	public void setProcesadora(ProcesadoraTarjeta procesadora) {
		this.procesadora = procesadora;
	}

	public Tipo getTarjetaTipo() {
		return tarjetaTipo;
	}

	public void setTarjetaTipo(Tipo tarjetaTipo) {
		this.tarjetaTipo = tarjetaTipo;
	}

	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public ReciboFormaPago getReciboFormaPago() {
		return reciboFormaPago;
	}

	public void setReciboFormaPago(ReciboFormaPago reciboFormaPago) {
		this.reciboFormaPago = reciboFormaPago;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}

}
