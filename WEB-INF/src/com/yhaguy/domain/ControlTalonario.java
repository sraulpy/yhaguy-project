package com.yhaguy.domain;

import java.util.Date;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ControlTalonario extends Domain {

	private Date fecha;
	private String receptor;
	private int cajas;
	private int unidades;
	private int facturadas;
	private boolean activo;
	
	private int unidades_caja;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	@DependsOn({ "unidades", "facturadas" })
	public int getDisponible() {
		return this.unidades - this.facturadas;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getReceptor() {
		return receptor;
	}

	public void setReceptor(String receptor) {
		this.receptor = receptor;
	}

	public int getCajas() {
		return cajas;
	}

	public void setCajas(int cajas) {
		this.cajas = cajas;
	}

	public int getUnidades() {
		return unidades;
	}

	public void setUnidades(int unidades) {
		this.unidades = unidades;
	}

	public int getFacturadas() {
		return facturadas;
	}

	public void setFacturadas(int facturadas) {
		this.facturadas = facturadas;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public int getUnidades_caja() {
		return unidades_caja;
	}

	public void setUnidades_caja(int unidades_caja) {
		this.unidades_caja = unidades_caja;
		this.unidades = (this.cajas * unidades_caja);
		BindUtils.postNotifyChange(null, null, this, "unidades");
	}
}
