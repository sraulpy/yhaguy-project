package com.yhaguy.gestion.comun;

import java.util.Date;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;

@SuppressWarnings("serial")
public class ArticuloCostoDTO extends DTO {

	private Long idMovimiento;
	private MyArray tipoMovimiento = new MyArray();
	private Date fechaCompra;
	private double costoFinalGs;
	private double costoFinalDs;

	public Long getIdMovimiento() {
		return idMovimiento;
	}

	public void setIdMovimiento(Long idMovimiento) {
		this.idMovimiento = idMovimiento;
	}

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public Date getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(Date fechaCompra) {
		this.fechaCompra = fechaCompra;
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
