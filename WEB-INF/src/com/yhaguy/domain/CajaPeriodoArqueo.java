package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class CajaPeriodoArqueo extends Domain {
	
	private double totalEfectivo;
	private double totalCheque;
	private double totalTarjeta;

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public double getTotalEfectivo() {
		return totalEfectivo;
	}

	public void setTotalEfectivo(double totalEfectivo) {
		this.totalEfectivo = totalEfectivo;
	}

	public double getTotalCheque() {
		return totalCheque;
	}

	public void setTotalCheque(double totalCheque) {
		this.totalCheque = totalCheque;
	}

	public double getTotalTarjeta() {
		return totalTarjeta;
	}

	public void setTotalTarjeta(double totalTarjeta) {
		this.totalTarjeta = totalTarjeta;
	}
}
