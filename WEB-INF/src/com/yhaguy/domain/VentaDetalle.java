package com.yhaguy.domain;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.util.Utiles;

/**
 * Los precios incluyen IVA
 */
@SuppressWarnings("serial")
public class VentaDetalle extends Domain {

	private Articulo articulo;
	
	private String descripcion;

	/** el costo por unidad, sin iva */
	private double costoUnitarioGs;

	/** el precio por unidad, iva incluido y sin ningún descuento */
	private double precioVentaUnitarioGs;
	private double precioVentaUnitarioDs;

	/** Cantidad de unidades */
	private long cantidad;
	private long cantidadEntregada;
	
	/** El nombre de la Regla de Precio aplicada al ítem */
	private String nombreRegla;
	
	/** importe descontado del precio que le correspondía por lista 
	 * si descuento es del 20%
	 * descuentoUnitario = precioVentaUnitario * .20
	 * */
	private double descuentoUnitarioGs;
	private double descuentoUnitarioDs;

	/**
	 * precio final de la venta 
	 * precioVentaFinalUnitario = precioVentaUnitario - descuentoUnitario
	 * */
	private double precioVentaFinalUnitarioGs;
	private double precioVentaFinalUnitarioDs;

	
	/** el monto del IVA u otro impuesto 
	 * impuestoUnitario = precioVentaFinalUnitario / 11
	 * */
	private double impuestoUnitario;

	
	/**
	 * precioVentaFinal = precioVentaFinalUnitario * cantidad
	 */
	private double precioVentaFinalGs;
	private double precioVentaFinalDs;
	
	/** el monto del IVA u otro impuesto 
	 * impuestoFinal = precioVentaFinal / 11 = impuestoUnitario * cantidad
	 * */
	private double impuestoFinal;
	
	/**
	 * booleano que indica si el item ya esta reservado..
	 * */
	private boolean reservado;
	
	private double precioGs;
	private ArticuloListaPrecio listaPrecio;
	private Tipo tipoIVA;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}	
	
	/**
	 * @return la rentabilidad del articulo..
	 */
	public double getRentabilidad() {
		double ganancia = (this.getImporteGsSinIva() - this.getDescuentoUnitarioGsSinIva()) - this.getCostoTotalGsSinIva();		
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getCostoTotalGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return true si el item es del proveedor que recibe como parametro..
	 */
	public boolean isProveedor(long idProveedor) {
		for (ProveedorArticulo item : this.articulo.getProveedorArticulos()) {
			if (item.getProveedor().getId().longValue() == idProveedor) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return el precio sin iva..
	 */
	public double getPrecioGsSinIva() {
		Misc misc = new Misc();
		double iva = misc.calcularIVA(this.precioGs, 10);
		return this.precioGs - iva;
	}
	
	/**
	 * @return el descuento unitario sin iva..
	 */
	public double getDescuentoUnitarioGsSinIva() {
		Misc misc = new Misc();
		double iva = misc.calcularIVA(this.descuentoUnitarioGs, 10);
		return this.descuentoUnitarioGs - iva;
	}
	
	/**
	 * @return el importe sin iva..
	 */
	public double getImporteGsSinIva() {
		return (this.getPrecioGsSinIva() * this.cantidad);
	}
	
	/**
	 * @return el importe..
	 */
	@DependsOn({ "precioGs", "cantidad", "descuentoUnitarioGs" })
	public double getImporteGs() {
		return (this.precioGs * this.cantidad) - this.descuentoUnitarioGs;
	}
	
	/**
	 * @return el costo total del item..
	 */
	public double getCostoTotalGs() {
		return this.articulo.getCostoGs() * this.cantidad;
	}
	
	/**
	 * @return el costo total del item sin iva..
	 */
	public double getCostoTotalGsSinIva() {
		return this.getCostoUnitarioGs() * this.cantidad;
	}
	
	public double getPrecioVentaFinalGs() {
		return precioVentaFinalGs;
	}

	public void setPrecioVentaFinalGs(double precioVentaFinal) {
		this.precioVentaFinalGs = precioVentaFinal;
	}

	public double getImpuestoFinal() {
		return impuestoFinal;
	}

	public void setImpuestoFinal(double impuestoFinal) {
		this.impuestoFinal = impuestoFinal;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public long getCantidad() {
		return cantidad;
	}

	public void setCantidad(long cantidad) {
		this.cantidad = cantidad;
		this.cantidadEntregada = cantidad;
	}

	public double getCostoUnitarioGs() {
		return costoUnitarioGs;
	}

	public void setCostoUnitarioGs(double costoUnitario) {
		this.costoUnitarioGs = costoUnitario;
	}

	public double getPrecioVentaUnitarioGs() {
		return precioVentaUnitarioGs;
	}

	public void setPrecioVentaUnitarioGs(double precioVentaUnitario) {
		this.precioVentaUnitarioGs = precioVentaUnitario;
	}
	
	public double getPrecioVentaUnitarioDs() {
		return precioVentaUnitarioDs;
	}

	public void setPrecioVentaUnitarioDs(double precioVentaUnitarioDs) {
		this.precioVentaUnitarioDs = precioVentaUnitarioDs;
	}

	public double getImpuestoUnitario() {
		return impuestoUnitario;
	}

	public void setImpuestoUnitario(double impuestoUnitario) {
		this.impuestoUnitario = impuestoUnitario;
	}

	public double getDescuentoUnitarioGs() {
		return descuentoUnitarioGs;
	}

	public void setDescuentoUnitarioGs(double descuentoUnitario) {
		this.descuentoUnitarioGs = descuentoUnitario;
	}

	public double getPrecioVentaFinalUnitarioGs() {
		return precioVentaFinalUnitarioGs;
	}

	public void setPrecioVentaFinalUnitarioGs(double precioVentaFinalUnitario) {
		this.precioVentaFinalUnitarioGs = precioVentaFinalUnitario;
	}
	
	public String getNombreRegla() {
		return nombreRegla;
	}

	public void setNombreRegla(String nombreRegla) {
		this.nombreRegla = nombreRegla;
	}

	public boolean isReservado() {
		return reservado;
	}

	public void setReservado(boolean reservado) {
		this.reservado = reservado;
	}	

	public double getDescuentoUnitarioDs() {
		return descuentoUnitarioDs;
	}

	public void setDescuentoUnitarioDs(double descuentoUnitarioDs) {
		this.descuentoUnitarioDs = descuentoUnitarioDs;
	}

	public double getPrecioVentaFinalUnitarioDs() {
		return precioVentaFinalUnitarioDs;
	}

	public void setPrecioVentaFinalUnitarioDs(double precioVentaFinalUnitarioDs) {
		this.precioVentaFinalUnitarioDs = precioVentaFinalUnitarioDs;
	}

	public double getPrecioVentaFinalDs() {
		return precioVentaFinalDs;
	}

	public void setPrecioVentaFinalDs(double precioVentaFinalDs) {
		this.precioVentaFinalDs = precioVentaFinalDs;
	}

	public ArticuloListaPrecio getListaPrecio() {
		return listaPrecio;
	}

	public void setListaPrecio(ArticuloListaPrecio listaPrecio) {
		this.listaPrecio = listaPrecio;
	}

	public double getPrecioGs() {
		return precioGs;
	}

	public void setPrecioGs(double precioGs) {
		this.precioGs = precioGs;
	}

	public Tipo getTipoIVA() {
		return tipoIVA;
	}

	public void setTipoIVA(Tipo tipoIVA) {
		this.tipoIVA = tipoIVA;
	}

	public long getCantidadEntregada() {
		return cantidadEntregada;
	}

	public void setCantidadEntregada(long cantidadEntregada) {
		this.cantidadEntregada = cantidadEntregada;
	}
}
