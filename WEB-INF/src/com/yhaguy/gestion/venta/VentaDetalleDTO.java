package com.yhaguy.gestion.venta;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.VehiculoMarca;
import com.yhaguy.domain.VehiculoModelo;
import com.yhaguy.domain.VehiculoTipo;

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
	private double 	impuestoFinal = 0;
	private boolean reservado = false;
	private boolean costoIvaIncluido = false;
	private long 	stockDisponible = 0;
	private String 	nombreRegla = "";
	private double costoPromedioGs = 0;
	
	private double precioMinimoGs = 0;
	private double precioGs = 0;
	private double 	precioVentaFinalDs = 0;
	private MyArray listaPrecio;
	private MyPair tipoIVA;
	
	private boolean impresionDescuento = false;
	private double descuentoPorcentajeMax = 0.0;
	
	private String ubicacion = "SIN UBICACIÓN..";
	
	private VehiculoTipo vehiculoTipo;
	private VehiculoMarca vehiculoMarca;
	private VehiculoModelo vehiculoModelo;
	
	private int ampere = 0;
	private int kilogramo = 0;
	private int cantidadDescuento = 0;
	private String marca = "";
	
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
	
	@DependsOn({ "precioVentaFinalDs", "cantidad", "descuentoUnitarioGs" })
	public double getImporteDs() {
		return (this.precioVentaFinalDs * this.cantidad) - this.descuentoUnitarioGs;
	}
	
	@DependsOn({ "precioGs", "cantidad" })
	public double getImporteGsSinDescuento() {
		return this.precioGs * this.cantidad;
	}
	
	@DependsOn({ "precioVentaFinalDs", "cantidad" })
	public double getImporteDsSinDescuento() {
		return this.precioVentaFinalDs * this.cantidad;
	}
	
	/**
	 * @return el importe sin iva..
	 */
	public double getImporteGsSinIva() {
		return (this.getPrecioGsSinIva() * this.cantidad) - this.getDescuentoUnitarioGsSinIva();
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
	
	@DependsOn("tipoIVA")
	public boolean isIva10() {
		return this.tipoIVA.getSigla().equals(Configuracion.SIGLA_IVA_10);
	}
	
	@DependsOn("tipoIVA")
	public boolean isIva5() {
		return this.tipoIVA.getSigla().equals(Configuracion.SIGLA_IVA_5);
	}
	
	@DependsOn("tipoIVA")
	public boolean isExenta() {
		return this.tipoIVA.getSigla().equals(Configuracion.SIGLA_IVA_EXENTO);
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
	
	@DependsOn({ "precioVentaFinalDs", "cantidad" })
	public double getTotalIvaDs() {
		if (this.isIva10())
			return (this.getIva10Ds());
		if (this.isIva5())
			return (this.getIva5Ds());
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
	
	@DependsOn("precioVentaFinalDs")
	public double getIvaDs() {
		if(this.isIva10())
			return this.getIva10Ds();
		if(this.isIva5())
			return this.getIva5Ds();
		return 0;
	}
	
	@DependsOn("precioGs")
	public double getIva10() {
		if (this.isIva10() == false)
			return 0;
		return this.getMisc().calcularIVA((this.getImporteGs()), 10);
	}
	
	@DependsOn("precioVentaFinalDs")
	public double getIva10Ds() {
		if (this.isIva10() == false)
			return 0;
		return this.getMisc().calcularIVA((this.getImporteDs()), 10);
	}

	@DependsOn("precioGs")
	public double getIva5() {
		if (this.isIva5() == false)
			return 0;
		return this.getMisc().calcularIVA((this.getImporteGs()), 5);
	}
	
	@DependsOn("precioVentaFinalDs")
	public double getIva5Ds() {
		if (this.isIva5() == false)
			return 0;
		return this.getMisc().calcularIVA((this.getImporteDs()), 5);
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

	public double getDescuentoPorcentajeMax() {
		return descuentoPorcentajeMax;
	}

	public void setDescuentoPorcentajeMax(double descuentoPorcentajeMax) {
		this.descuentoPorcentajeMax = descuentoPorcentajeMax;
	}
}
