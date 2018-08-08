package com.yhaguy.gestion.stock.ajustes;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;

@SuppressWarnings("serial")
public class AjusteStockDetalleDTO extends DTO {

	private int cantidad = 0;
	private double costoGs = 0;
	private MyArray articulo = new MyArray();
	
	@DependsOn({ "cantidad", "costoGs" })
	public double getTotalCostoGs() {
		return this.cantidad * this.costoGs;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public MyArray getArticulo() {
		return articulo;
	}

	public void setArticulo(MyArray articulo) {
		this.articulo = articulo;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}
}
