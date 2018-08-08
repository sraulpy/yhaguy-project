package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloDeposito extends Domain {

	private String ubicacion;
	private long stock;
	private long stockMinimo;
	private long stockMaximo;
	private Articulo articulo = null;
	private Deposito deposito = null;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public long getStock() {
		return stock;
	}

	public void setStock(long stock) {
		this.stock = stock;
	}

	public long getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(long stockMinimo) {
		this.stockMinimo = stockMinimo;
	}

	public long getStockMaximo() {
		return stockMaximo;
	}

	public void setStockMaximo(long stockMaximo) {
		this.stockMaximo = stockMaximo;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}


	public Deposito getDeposito() {
		return deposito;
	}

	public void setDeposito(Deposito deposito) {
		this.deposito = deposito;
	}
}
