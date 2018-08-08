package com.yhaguy.gestion.comun;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;

@SuppressWarnings("serial")
public class ReservaDetalleDTO extends DTO {

	private long cantidadReservada = 0;

	/**
	 * Si el item fue modificado, la cantidad actualizada es la diferencia entre
	 * la cantidad original - nueva cantidad (usado para reposicion de stock)
	 **/
	private long cantidadActualizada = 0;

	private MyArray articulo = new MyArray();
	
	/** id del item que origino esta reservaDetalle..**/
	private long idDetalleOrigen;
	
	private MyArray articuloDeposito;

	public long getCantidadReservada() {
		return cantidadReservada;
	}

	public void setCantidadReservada(long cantidadReservada) {
		this.cantidadReservada = cantidadReservada;
	}

	public MyArray getArticulo() {
		return articulo;
	}

	public void setArticulo(MyArray articulo) {
		this.articulo = articulo;
	}

	public long getIdDetalleOrigen() {
		return idDetalleOrigen;
	}

	public void setIdDetalleOrigen(long idDetalleOrigen) {
		this.idDetalleOrigen = idDetalleOrigen;
	}

	public long getCantidadActualizada() {
		return cantidadActualizada;
	}

	public void setCantidadActualizada(long cantidadActualizada) {
		this.cantidadActualizada = cantidadActualizada;
	}

	public MyArray getArticuloDeposito() {
		return articuloDeposito;
	}

	public void setArticuloDeposito(MyArray articuloDeposito) {
		this.articuloDeposito = articuloDeposito;
	}

}
