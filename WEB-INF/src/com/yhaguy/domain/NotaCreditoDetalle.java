package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class NotaCreditoDetalle extends Domain{
	
	private int cantidad;
	
	private double montoGs;
	private double montoDs;
	private double costoGs;
	
	private double importeGs;
	private double importeDs;
	
	private Tipo tipoIva;
	
	/** El detalle puede hacer referencia a un articulo ó una factura segun el tipo **/
	private Tipo tipoDetalle;
	
	/** Se podran aplicar N.C a facturas de compra, venta y especificar el articulo **/
	private Articulo articulo;
	
	private Venta venta;
	private Gasto gasto;
	private CompraLocalFactura compra;
	private ImportacionFactura importacion;
	
	/** El deposito donde se realiza la devolucion **/
	private Deposito deposito;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return la rentabilidad del articulo..
	 */
	public double getRentabilidad() {
		double ganancia = this.getImporteGsSinIva() - this.getCostoTotalGsSinIva();		
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getCostoTotalGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return el precio sin iva..
	 */
	public double getPrecioGsSinIva() {
		double iva = Utiles.getIVA(this.montoGs, 10);
		return this.montoGs - iva;
	}
	
	/**
	 * @return el importe sin iva..
	 */
	public double getImporteGsSinIva() {
		Misc misc = new Misc();
		double iva = misc.calcularIVA(this.importeGs, 10);
		return this.importeGs - iva;
	}
	
	/**
	 * @return el costo total del item sin iva..
	 */
	public double getCostoTotalGsSinIva() {
		return this.getCostoGs() * this.cantidad;
	}
	
	/**
	 * Retorna true si es un detalle de tipo factura
	 */
	public boolean isDetalleFactura() {
		return this.tipoDetalle.getSigla().compareTo(
				Configuracion.SIGLA_TIPO_NC_DETALLE_FACTURA) == 0;
	}

	public Deposito getDeposito() {
		return deposito;
	}

	public void setDeposito(Deposito deposito) {
		this.deposito = deposito;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
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

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}	

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public Tipo getTipoDetalle() {
		return tipoDetalle;
	}

	public void setTipoDetalle(Tipo tipoDetalle) {
		this.tipoDetalle = tipoDetalle;
	}	

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
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

	public Tipo getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(Tipo tipoIva) {
		this.tipoIva = tipoIva;
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public ImportacionFactura getImportacion() {
		return importacion;
	}

	public void setImportacion(ImportacionFactura importacion) {
		this.importacion = importacion;
	}
}
