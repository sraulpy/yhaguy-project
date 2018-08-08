package com.yhaguy.gestion.empresa.ctacte;

import java.util.Date;

public class FiltroCtaCteMovimientos {

	private String comprobanteNro = "", tipoMovimiento = "", moneda = "";
	private String emision = "", vencimiento = "", sucursal = "";

	public String getEmision() {
		return emision;
	}

	public void setEmision(String emision) {
		this.emision = emision == null ? "" : emision.trim();
	}

	public String getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(String vencimiento) {
		this.vencimiento = vencimiento == null ? "" : vencimiento.trim();
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal == null ? "" : sucursal.trim();
	}

	public String getComprobanteNro() {
		return comprobanteNro;
	}

	public void setComprobanteNro(String comprobanteNro) {
		this.comprobanteNro = comprobanteNro == null ? "" : comprobanteNro.trim();
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento == null ? "" : tipoMovimiento.trim();
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda == null ? "" : moneda.trim();
	}

}
