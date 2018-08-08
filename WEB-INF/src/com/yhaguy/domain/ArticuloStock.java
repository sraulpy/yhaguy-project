package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloStock extends Domain {

	private long cantidad;
	private Date fechaMovimiento;
	private long idMovimiento;
	private boolean anulado;
	private TipoMovimiento tipoMovimiento;
	private ArticuloDeposito articuloDep;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public long getCantidad() {
		return cantidad;
	}

	public void setCantidad(long cantidad) {
		this.cantidad = cantidad;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Date getFechaMovimiento() {
		return fechaMovimiento;
	}

	public void setFechaMovimiento(Date fechaMovimiento) {
		this.fechaMovimiento = fechaMovimiento;
	}

	public ArticuloDeposito getArticuloDep() {
		return articuloDep;
	}

	public void setArticuloDep(ArticuloDeposito articuloDep) {
		this.articuloDep = articuloDep;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}

	public long getIdMovimiento() {
		return idMovimiento;
	}

	public void setIdMovimiento(long idMovimiento) {
		this.idMovimiento = idMovimiento;
	}
}
