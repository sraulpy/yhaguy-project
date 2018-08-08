package com.yhaguy.gestion.compras.locales;

import org.zkoss.bind.BindUtils;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;

@SuppressWarnings("serial")
public class CompraLocalGastoDTO extends DTO{

	private String descripcion = "";
	private double montoGs = 0;
	private double montoDs = 0;
	
	private MyArray gasto;
	
	private int porcentaje = 0;
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.toUpperCase();
	}
	
	public double getMontoGs() {
		return montoGs;
	}
	
	public void setMontoGs(double montoGs) {
		this.montoGs = montoGs;
	}
	
	public double getMontoDs() {
		return montoDs;
	}
	
	public void setMontoDs(double montoDs) {
		this.montoDs = montoDs;
	}

	public MyArray getGasto() {
		return gasto;
	}

	public void setGasto(MyArray gasto) {
		this.gasto = gasto;
	}

	public int getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(int porcentaje) {
		double importe = (double) this.gasto.getPos3();
		this.setMontoGs(this.getMisc().obtenerValorDelPorcentaje(importe, porcentaje));
		this.porcentaje = porcentaje;
		BindUtils.postNotifyChange(null, null, this, "montoGs");
	}		
}
