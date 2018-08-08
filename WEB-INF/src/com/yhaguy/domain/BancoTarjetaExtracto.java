package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class BancoTarjetaExtracto extends Domain {

	private String nroEstracto = "";
	private Date periodoDesde;
	private Date periodoHasta;
	private double totalTarjeta = 0;
	private double totalAcreditado = 0;
	private SucursalApp sucursalApp;
	private ProcesadoraTarjeta procesadora;
	private BancoCta bancoCta;
	private Set<BancoTarjetaExtractoDetalle> detalles = new HashSet<BancoTarjetaExtractoDetalle>();

	public String getNroEstracto() {
		return nroEstracto;
	}

	public void setNroEstracto(String nroEstracto) {
		this.nroEstracto = nroEstracto;
	}

	public Date getPeriodoDesde() {
		return periodoDesde;
	}

	public void setPeriodoDesde(Date periodoDesde) {
		this.periodoDesde = periodoDesde;
	}

	public Date getPeriodoHasta() {
		return periodoHasta;
	}

	public void setPeriodoHasta(Date periodoHasta) {
		this.periodoHasta = periodoHasta;
	}

	public double getTotalTarjeta() {
		return totalTarjeta;
	}

	public void setTotalTarjeta(double totalTarjeta) {
		this.totalTarjeta = totalTarjeta;
	}

	public double getTotalAcreditado() {
		return totalAcreditado;
	}

	public void setTotalAcreditado(double totalAcreditado) {
		this.totalAcreditado = totalAcreditado;
	}

	public SucursalApp getSucursalApp() {
		return sucursalApp;
	}

	public void setSucursalApp(SucursalApp sucursalApp) {
		this.sucursalApp = sucursalApp;
	}

	public ProcesadoraTarjeta getProcesadora() {
		return procesadora;
	}

	public void setProcesadora(ProcesadoraTarjeta procesadora) {
		this.procesadora = procesadora;
	}

	public BancoCta getBancoCta() {
		return bancoCta;
	}

	public void setBancoCta(BancoCta bancoCta) {
		this.bancoCta = bancoCta;
	}

	public Set<BancoTarjetaExtractoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<BancoTarjetaExtractoDetalle> detalles) {
		this.detalles = detalles;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
