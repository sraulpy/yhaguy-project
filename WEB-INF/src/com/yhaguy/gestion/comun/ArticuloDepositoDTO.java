package com.yhaguy.gestion.comun;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class ArticuloDepositoDTO extends DTO {

	private String ubicacion;
	private long stock = 0;
	private long stockMinimo = 0;
	private long stockMaximo = 0;
	private MyArray articulo = new MyArray();
	private MyPair deposito = new MyPair();
	
	@Override
	public String[] getCamposMyArray() {
		String[] camps = { "ubicacion" };
		return camps;
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

	public MyArray getArticulo() {
		return articulo;
	}

	public void setArticulo(MyArray articulo) {
		this.articulo = articulo;
	}
	
	public MyPair getDeposito() {
		return deposito;
	}

	public void setDeposito(MyPair deposito) {
		this.deposito = deposito;
	}
}
