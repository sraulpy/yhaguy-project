package com.yhaguy.domain;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
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
	private double costoPromedioGs;

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
	
	/** el monto del IVA u otro impuesto 
	 * impuestoFinal = precioVentaFinal / 11 = impuestoUnitario * cantidad
	 * */
	private double impuestoFinal;
	
	/**
	 * booleano que indica si el item ya esta reservado..
	 * */
	private boolean reservado;
	
	private double precioGs;
	private double precioVentaFinalDs;
	private ArticuloListaPrecio listaPrecio;
	private Tipo tipoIVA;
	
	private VehiculoTipo vehiculoTipo;
	private VehiculoMarca vehiculoMarca;
	private VehiculoModelo vehiculoModelo;
	
	private int ampere;
	private int kilogramo;
	private int cantidadDescuento;
	private String marca = "";
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return true si corresponde a flia articulo..
	 */
	public boolean isFamilia(long idFamilia) {
		ArticuloFamilia flia = this.articulo.getFamilia();
		if (flia != null) {
			return flia.getId().longValue() == idFamilia;
		}
		return false;
	}
	
	public boolean isIva10() {
		return this.tipoIVA.getSigla().equals(Configuracion.SIGLA_IVA_10);
	}
	
	public boolean isIva5() {
		return this.tipoIVA.getSigla().equals(Configuracion.SIGLA_IVA_5);
	}
	
	public boolean isExenta() {
		return this.tipoIVA.getSigla().equals(Configuracion.SIGLA_IVA_EXENTO);
	}
	
	/**
	 * @return la rentabilidad del articulo..
	 */
	public double getRentabilidad() {
		double ganancia = (this.getImporteGsSinIva()) - this.getCostoTotalGsSinIva();
		if (ganancia <= 0) return 0.0;
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getCostoTotalGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return la rentabilidad del articulo..
	 */
	public double getRentabilidadPromedio() {
		double ganancia = (this.getImporteGsSinIva()) - this.getCostoPromedioTotalGsSinIva();
		if (ganancia <= 0) return 0.0;
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getCostoPromedioTotalGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return la rentabilidad del articulo sobre venta..
	 */
	public double getRentabilidadVenta() {
		double ganancia = (this.getImporteGsSinIva()) - this.getCostoTotalGsSinIva();	
		if (ganancia <= 0) return 0.0;
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getImporteGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return la rentabilidad del articulo sobre venta..
	 */
	public double getRentabilidadVentaPromedio() {
		double ganancia = (this.getImporteGsSinIva()) - this.getCostoPromedioTotalGsSinIva();	
		if (ganancia <= 0) return 0.0;
		double out = Utiles.obtenerPorcentajeDelValor(ganancia, this.getCostoPromedioTotalGsSinIva());
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return el porcentaje descontado..
	 */
	public double getPorcentajeDescuento() {
		double subtotal = this.getPrecioGs() * this.getCantidad();
		if (this.descuentoUnitarioGs <= 0) return 0.0;
		double out = Utiles.obtenerPorcentajeDelValor(this.descuentoUnitarioGs, subtotal);
		return Utiles.redondeoDosDecimales(out);
	}
	
	/**
	 * @return true si el item es del proveedor que recibe como parametro..
	 */
	public boolean isProveedor(long idProveedor) {
		if (this.articulo.getProveedor() == null) return false;
		return this.articulo.getProveedor().getId().longValue() == idProveedor;
	}
	
	/**
	 * @return el precio sin iva..
	 */
	public double getPrecioGsSinIva() {
		if (this.isExenta()) {
			return this.precioGs;
		}
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
		return (this.getPrecioGsSinIva() * this.cantidad) - this.getDescuentoUnitarioGsSinIva();
	}
	
	/**
	 * @return el importe..
	 */
	@DependsOn({ "precioGs", "cantidad", "descuentoUnitarioGs" })
	public double getImporteGs() {
		return (this.precioGs * this.cantidad) - this.descuentoUnitarioGs;
	}
	
	/**
	 * @return gravada 10..
	 */
	public double getGravada10() {
		if (this.isExenta()) {
			return 0.0;
		}
		Misc misc = new Misc();
		double out = misc.calcularGravado(this.getImporteGs(), 10);
		return out;
	}
	
	/**
	 * @return gravada 10..
	 */
	public double getGravada10Unitario() {
		if (this.isExenta()) {
			return 0.0;
		}
		return this.getGravada10() / this.cantidad;
	}
	
	/**
	 * @return el iva 10..
	 */
	public double getIva10() {
		if (this.isExenta()) {
			return 0.0;
		}
		double iva = Utiles.getIVA(this.getImporteGs(), 10);
		return iva;
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
	
	/**
	 * @return el costo total del item sin iva..
	 */
	public double getCostoPromedioTotalGsSinIva() {
		return this.getCostoPromedioGs() * this.cantidad;
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

	public VehiculoTipo getVehiculoTipo() {
		return vehiculoTipo;
	}

	public void setVehiculoTipo(VehiculoTipo vehiculoTipo) {
		this.vehiculoTipo = vehiculoTipo;
	}

	public VehiculoMarca getVehiculoMarca() {
		return vehiculoMarca;
	}

	public void setVehiculoMarca(VehiculoMarca vehiculoMarca) {
		this.vehiculoMarca = vehiculoMarca;
	}

	public VehiculoModelo getVehiculoModelo() {
		return vehiculoModelo;
	}

	public void setVehiculoModelo(VehiculoModelo vehiculoModelo) {
		this.vehiculoModelo = vehiculoModelo;
	}

	public int getAmpere() {
		return ampere;
	}

	public void setAmpere(int ampere) {
		this.ampere = ampere;
	}

	public int getKilogramo() {
		return kilogramo;
	}

	public void setKilogramo(int kilogramo) {
		this.kilogramo = kilogramo;
	}

	public int getCantidadDescuento() {
		return cantidadDescuento;
	}

	public void setCantidadDescuento(int cantidadDescuento) {
		this.cantidadDescuento = cantidadDescuento;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public double getCostoPromedioGs() {
		return costoPromedioGs;
	}

	public void setCostoPromedioGs(double costoPromedioGs) {
		this.costoPromedioGs = costoPromedioGs;
	}
}
