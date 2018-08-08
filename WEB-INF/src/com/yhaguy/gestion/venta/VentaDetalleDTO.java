package com.yhaguy.gestion.venta;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;

@SuppressWarnings("serial")
public class VentaDetalleDTO extends DTO {

	private MyArray articulo = new MyArray();	
	private String 	descripcion = "";
	private double 	costoUnitarioGs = 0;
	private double 	costoUnitarioDs = 0;
	private double 	precioVentaUnitarioGs = 0;
	private double 	precioVentaUnitarioDs = 0;
	private long 	cantidad = 0;
	private long 	cantidadEntregada = 0;
	private double 	descuentoUnitarioGs = 0;
	private double 	descuentoUnitarioDs = 0;
	private double  descuentoPorcentaje = 0;
	private double 	precioVentaFinalUnitarioGs = 0;
	private double 	precioVentaFinalUnitarioDs = 0;
	private double 	impuestoUnitario = 0;
	private double 	precioVentaFinalGs = 0;
	private double 	precioVentaFinalDs = 0;
	private double 	impuestoFinal = 0;
	private boolean reservado = false;
	private boolean costoIvaIncluido = false;
	private long 	stockDisponible = 0;
	private String 	nombreRegla = "";
	
	private double precioMinimoGs = 0;
	private double precioGs = 0;
	private MyArray listaPrecio;
	private MyPair tipoIVA;
	
	private boolean impresionDescuento = false;
	
	private String ubicacion = "SIN UBICACIÓN..";
	
	/**
	 * Determina hasta que porcentaje de descuento 
	 * puede hacer el usuario..
	 * El valor lo obtiene de la regla precio..
	 */
	private double coef_descuento = 0;
	
	@DependsOn({ "precioGs", "cantidad", "descuentoUnitarioGs" })
	public double getImporteGs() {
		return (this.precioGs * this.cantidad) - this.descuentoUnitarioGs;
	}
	
	@DependsOn({ "precioGs", "cantidad" })
	public double getImporteGsSinDescuento() {
		return this.precioGs * this.cantidad;
	}
	
	@DependsOn("tipoIVA")
	public boolean isIva10() {
		return this.tipoIVA.getSigla().equals(Configuracion.SIGLA_IVA_10);
	}
	
	@DependsOn("tipoIVA")
	public boolean isIva5() {
		return this.tipoIVA.getSigla().equals(Configuracion.SIGLA_IVA_5);
	}
	
	@DependsOn("articulo")
	public boolean isServicio() {
		boolean out = (boolean) this.articulo.getPos5();
		return out;
	}
	
	@DependsOn({ "precioGs", "cantidad" })
	public double getTotalIva() {
		if (this.isIva10())
			return (this.getIva10());
		if (this.isIva5())
			return (this.getIva5());
		return 0;
	}
	
	@DependsOn("precioGs")
	public double getIva() {
		if(this.isIva10())
			return this.getIva10();
		if(this.isIva5())
			return this.getIva5();
		return 0;
	}
	
	@DependsOn("precioGs")
	public double getIva10() {
		if (this.isIva10() == false)
			return 0;
		return this.getMisc().calcularIVA((this.getImporteGs()), 10);
	}

	@DependsOn("precioGs")
	public double getIva5() {
		if (this.isIva5() == false)
			return 0;
		return this.getMisc().calcularIVA((this.getImporteGs()), 5);
	}
	
	/**
	 * @return el stock disponible..
	 */
	public long getStockActual(long idDeposito) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getStockDisponible(this.articulo.getId(), idDeposito);
	}
	
	/**
	 * @return el codigo del articulo..
	 */
	public String getCodigoInterno() {
		return (String) this.articulo.getPos1();
	}
	
	/**
	 * @return la descripcion del articulo..
	 */
	public String getDescripcionArticulo() {
		return (String) this.articulo.getPos4();
	}
	
	public MyArray getArticulo() {
		return articulo;
	}

	public void setArticulo(MyArray articulo) {
		this.articulo = articulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getCostoUnitarioGs() {
		return costoUnitarioGs;
	}

	public void setCostoUnitarioGs(double costoUnitario) {
		this.costoUnitarioGs = costoUnitario;
	}

	public double getImpuestoUnitario() {
		return impuestoUnitario;
	}

	public void setImpuestoUnitario(double impuestoUnitario) {
		this.impuestoUnitario = impuestoUnitario;
	}

	public double getImpuestoFinal() {
		return impuestoFinal;
	}

	public void setImpuestoFinal(double impuestoFinal) {
		this.impuestoFinal = impuestoFinal;
	}

	public boolean isReservado() {
		return reservado;
	}

	public void setReservado(boolean reservado) {
		this.reservado = reservado;
	}

	public long getStockDisponible() {
		return stockDisponible;
	}

	public void setStockDisponible(long stockDisponible) {
		this.stockDisponible = stockDisponible;
	}

	public String getNombreRegla() {
		return nombreRegla;
	}

	public void setNombreRegla(String nombreRegla) {
		this.nombreRegla = nombreRegla;
	}

	public double getCostoUnitarioDs() {
		return costoUnitarioDs;
	}

	public void setCostoUnitarioDs(double costoUnitarioDs) {
		this.costoUnitarioDs = costoUnitarioDs;
	}

	public double getPrecioVentaUnitarioGs() {
		return precioVentaUnitarioGs;
	}

	public void setPrecioVentaUnitarioGs(double precioVentaUnitarioGs) {
		this.precioVentaUnitarioGs = precioVentaUnitarioGs;
	}

	public double getPrecioVentaUnitarioDs() {
		return precioVentaUnitarioDs;
	}

	public void setPrecioVentaUnitarioDs(double precioVentaUnitarioDs) {
		this.precioVentaUnitarioDs = this.getMisc().redondeoDosDecimales(precioVentaUnitarioDs);
	}

	public long getCantidad() {
		return cantidad;
	}

	public void setCantidad(long cantidad) {
		this.cantidad = cantidad;
	}

	public double getDescuentoUnitarioGs() {
		return descuentoUnitarioGs;
	}

	public void setDescuentoUnitarioGs(double descuentoUnitarioGs) {
		this.descuentoUnitarioGs = descuentoUnitarioGs;
	}

	public double getDescuentoUnitarioDs() {
		return descuentoUnitarioDs;
	}

	public void setDescuentoUnitarioDs(double descuentoUnitarioDs) {
		this.descuentoUnitarioDs = this.getMisc().redondeoDosDecimales(descuentoUnitarioDs);
	}

	public double getPrecioVentaFinalUnitarioGs() {
		return precioVentaFinalUnitarioGs;
	}

	public void setPrecioVentaFinalUnitarioGs(double precioVentaFinalUnitarioGs) {
		this.precioVentaFinalUnitarioGs = precioVentaFinalUnitarioGs;
	}

	public double getPrecioVentaFinalUnitarioDs() {
		return precioVentaFinalUnitarioDs;
	}

	public void setPrecioVentaFinalUnitarioDs(double precioVentaFinalUnitarioDs) {
		this.precioVentaFinalUnitarioDs = this.getMisc().redondeoDosDecimales(precioVentaFinalUnitarioDs);
	}

	public double getPrecioVentaFinalGs() {
		return precioVentaFinalGs;
	}

	public void setPrecioVentaFinalGs(double precioVentaFinalGs) {
		this.precioVentaFinalGs = precioVentaFinalGs;
	}

	public double getPrecioVentaFinalDs() {
		return precioVentaFinalDs;
	}

	public void setPrecioVentaFinalDs(double precioVentaFinalDs) {
		this.precioVentaFinalDs = this.getMisc().redondeoDosDecimales(precioVentaFinalDs);
	}

	public double getCoef_descuento() {
		return coef_descuento;
	}

	public void setCoef_descuento(double coef_descuento) {
		this.coef_descuento = coef_descuento;
	}

	public double getDescuentoPorcentaje() {
		return descuentoPorcentaje;
	}

	public void setDescuentoPorcentaje(double descuentoPorcentaje) {
		this.descuentoPorcentaje = descuentoPorcentaje;
	}

	public MyArray getListaPrecio() {
		return listaPrecio;
	}

	public void setListaPrecio(MyArray listaPrecio) {
		this.listaPrecio = listaPrecio;
	}

	public double getPrecioGs() {
		return precioGs;
	}

	public void setPrecioGs(double precioGs) {
		this.precioGs = precioGs;
	}

	public MyPair getTipoIVA() {
		return tipoIVA;
	}

	public void setTipoIVA(MyPair tipoIVA) {
		this.tipoIVA = tipoIVA;
	}

	public double getPrecioMinimoGs() {
		return precioMinimoGs;
	}

	public void setPrecioMinimoGs(double precioMinimoGs) {
		this.precioMinimoGs = precioMinimoGs;
	}

	public boolean isCostoIvaIncluido() {
		return costoIvaIncluido;
	}

	public void setCostoIvaIncluido(boolean costoIvaIncluido) {
		this.costoIvaIncluido = costoIvaIncluido;
	}

	public boolean isImpresionDescuento() {
		return impresionDescuento;
	}

	public void setImpresionDescuento(boolean impresionDescuento) {
		this.impresionDescuento = impresionDescuento;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public long getCantidadEntregada() {
		return cantidadEntregada;
	}

	public void setCantidadEntregada(long cantidadEntregada) {
		this.cantidadEntregada = cantidadEntregada;
	}
}
