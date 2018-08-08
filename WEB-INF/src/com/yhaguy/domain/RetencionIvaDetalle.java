package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class RetencionIvaDetalle extends Domain {
	
	private double importeIvaGs;
	
	private Gasto gasto;
	private CompraLocalFactura compra;

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public double getImporteIvaGs() {
		return importeIvaGs;
	}

	public void setImporteIvaGs(double montoGs) {
		this.importeIvaGs = montoGs;
	}

	public Gasto getGasto() {
		return gasto;
	}

	public void setGasto(Gasto gasto) {
		this.gasto = gasto;
	}

	public CompraLocalFactura getCompra() {
		return compra;
	}

	public void setCompra(CompraLocalFactura compra) {
		this.compra = compra;
	}	
}
