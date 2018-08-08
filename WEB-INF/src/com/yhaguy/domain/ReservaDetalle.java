package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ReservaDetalle extends Domain {

	private long cantidadReservada;
	private long idDetalleOrigen; //id del item que genero esta ReservaDetalle..
	
	private Articulo articulo;	
	
	private ArticuloDeposito articuloDeposito;

	public long getCantidadReservada() {
		return cantidadReservada;
	}

	public void setCantidadReservada(long cantidadReservada) {
		this.cantidadReservada = cantidadReservada;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public long getIdDetalleOrigen() {
		return idDetalleOrigen;
	}

	public void setIdDetalleOrigen(long idDetalleOrigen) {
		this.idDetalleOrigen = idDetalleOrigen;
	}	
	
	

	public ArticuloDeposito getArticuloDeposito() {
		return articuloDeposito;
	}

	public void setArticuloDeposito(ArticuloDeposito articuloDeposito) {
		this.articuloDeposito = articuloDeposito;
	}

	@Override
	public int compareTo(Object o) {
		return -1;
	}
}
