package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class VentaMigracion extends Domain {

	private Date fechaDesde;
	private Date fechaHasta;
	private long idCaja = 0;
	private String caja = "";

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public long getIdCaja() {
		return idCaja;
	}

	public void setIdCaja(long idCaja) {
		this.idCaja = idCaja;
	}

	public String getCaja() {
		return caja;
	}

	public void setCaja(String caja) {
		this.caja = caja;
	}

	@Override
	public int compareTo(Object o) {
		VentaMigracion cmp = (VentaMigracion) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}

}
