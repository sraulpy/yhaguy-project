package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class BancoTransferencia extends Domain {

	private Date fecha;
	private String numero;
	private double importe;
	private double tipoCambio;
	
	private BancoCta origen;
	private BancoCta destino;
	private Tipo moneda;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	/**
	 * @return true si es moneda local..
	 */
	public boolean isMonedaLocal() {
		return this.moneda.getSigla().equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}

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

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public BancoCta getOrigen() {
		return origen;
	}

	public void setOrigen(BancoCta origen) {
		this.origen = origen;
	}

	public BancoCta getDestino() {
		return destino;
	}

	public void setDestino(BancoCta destino) {
		this.destino = destino;
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
}
