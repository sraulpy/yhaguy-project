package com.yhaguy.domain;

public class ArticuloRentabilidad {
	
	private long cantidad;
	private double costoGs;
	private double precioGsSinIva;
	private double rentabilidad;
	private double importeGsDevolucion = 0;

	private Articulo articulo;
	private Venta venta;
	private NotaCredito notaCredito;
	
	/**
	 * @return el numero de movimiento..
	 */
	public String getNumeroMovimiento() {
		return this.venta != null ? this.venta.getNumero() : this.notaCredito.getNumero();
	}
	
	/**
	 * @return el tipo de movimiento..
	 */
	public String getTipoMovimiento() {
		String sigla = this.venta != null ? this.venta.getTipoMovimiento()
				.getSigla() : this.notaCredito.getTipoMovimiento().getSigla();
		return TipoMovimiento.getAbreviatura(sigla);
	}
	
	/**
	 * @return el costo total..
	 */
	public double getTotalCostoGs() {
		return this.costoGs * this.cantidad;
	}
	
	/**
	 * @return el importe sin iva..
	 */
	public double getImporteGsSinIva() {
		return (this.getPrecioGsSinIva() * this.cantidad);
	}

	public long getCantidad() {
		return cantidad;
	}

	public void setCantidad(long cantidad) {
		this.cantidad = cantidad;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public double getPrecioGsSinIva() {
		return precioGsSinIva;
	}

	public void setPrecioGsSinIva(double precioGsSinIva) {
		this.precioGsSinIva = precioGsSinIva;
	}

	public double getRentabilidad() {
		return rentabilidad;
	}

	public void setRentabilidad(double rentabilidad) {
		this.rentabilidad = rentabilidad;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public NotaCredito getNotaCredito() {
		return notaCredito;
	}

	public void setNotaCredito(NotaCredito notaCredito) {
		this.notaCredito = notaCredito;
	}

	public double getImporteGsDevolucion() {
		return importeGsDevolucion;
	}

	public void setImporteGsDevolucion(double importeGsDevolucion) {
		this.importeGsDevolucion = importeGsDevolucion;
	}	
}
