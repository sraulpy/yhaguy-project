package com.yhaguy.domain;

import java.util.Date;
import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloCosto extends Domain {
	
	private Date fechaCompra;
	private double costoFinalGs;
	private double costoFinalDs;

	private Articulo articulo;
	
	TipoMovimiento tipoMovimiento;
	Long idMovimiento;

	public Date getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(Date fechaCompra) {
		this.fechaCompra = fechaCompra;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Long getIdMovimiento() {
		return idMovimiento;
	}

	public void setIdMovimiento(Long idMovimiento) {
		this.idMovimiento = idMovimiento;
	}

	@Override
	public int compareTo(Object o) {
		ArticuloCosto cmp = (ArticuloCosto) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public double getCostoFinalGs() {
		return costoFinalGs;
	}

	public void setCostoFinalGs(double costoFinalGs) {
		this.costoFinalGs = costoFinalGs;
	}

	public double getCostoFinalDs() {
		return costoFinalDs;
	}

	public void setCostoFinalDs(double costoFinalDs) {
		this.costoFinalDs = costoFinalDs;
	}
}
