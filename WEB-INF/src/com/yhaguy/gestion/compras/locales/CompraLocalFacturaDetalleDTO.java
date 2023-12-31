package com.yhaguy.gestion.compras.locales;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class CompraLocalFacturaDetalleDTO extends DTO {

	private double costoGs = 0;
	private double costoDs = 0;
	private double importeExentaGs = 0;
	private double importeExentaDs = 0;
	private double importeGravadaGs = 0;
	private double importeGravadaDs = 0;
	private double descuentoGs = 0;
	private double descuentoDs = 0;
	private String textoDescuento = "0";
	private double importeDescuentoGs = 0;
	private double importeDescuentoDs = 0;
	private boolean descuento = false;
	private MyPair tipoDescuento;
	private int cantidad = 0;
	private int cantidadRecibida = 0;
	
	private double precioFinalGs = 0;
	private double minoristaGs = 0;
	private double listaGs = 0;
	
	private boolean ignorarDescuento = false;
	
	private MyArray articulo = new MyArray();	
	private MyPair iva;
	
	@DependsOn({"costoGs", "cantidad"})
	public double getImporteGs(){
		int signo = this.isDescuento()? -1 : 1;
		return (costoGs * cantidad) * signo;
	}
	
	@DependsOn({"costoDs", "cantidad"})
	public double getImporteDs(){
		int signo = this.isDescuento()? -1 : 1;
		return (costoDs * cantidad) * signo;
	}
	
	@DependsOn({"iva", "costoGs", "cantidad"})
	public double getImporteIva() {
		if(this.isExenta())
			return 0;
		int porc = this.isIva10()? 10 : 5;
		return Utiles.getRedondeo(this.getMisc().calcularIVA(this.getImporteGs(), porc));
	}
	
	@DependsOn({"cantidad", "cantidadRecibida"})
	public int getDiferenciaCantidad(){
		return cantidadRecibida - cantidad;
	}
	
	@SuppressWarnings("static-access")
	@DependsOn("diferenciaCantidad")
	public String getStyleItem(){
		int dif = this.getDiferenciaCantidad();
		if (dif > 0) {
			return this.getMisc().TEXTO_VERDE;
		} else if( dif < 0){
			return this.getMisc().TEXTO_ROJO;
		} else {
			return this.getMisc().TEXTO_NORMAL;
		}
	}
	
	/**
	 * @return el ultimo precio del articulo..
	 */
	public double getPrecioActualGs() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (!this.articulo.esNuevo()) {
			Object[] art = rr.getArticulo(this.articulo.getId());
			return (double) art[5];
		}
		return 0.0;
	}
	
	/**
	 * @return el margen..
	 */
	@DependsOn({ "costoGs", "precioFinalGs" })
	public double getMargen() {
		double costoGs = this.costoGs;
		return Utiles.obtenerPorcentajeDelValor((this.precioFinalGs - costoGs), costoGs);
	}
	
	/**
	 * @return el margen..
	 */
	@DependsOn({ "costoGs", "minoristaGs" })
	public double getMargenMinorista() {
		double costoGs = this.costoGs;
		return Utiles.obtenerPorcentajeDelValor((this.minoristaGs - costoGs), costoGs);
	}
	
	@DependsOn("iva")
	public boolean isExenta() {
		return this.iva.getSigla().equals(Configuracion.SIGLA_IVA_EXENTO);
	}
	
	@DependsOn("iva")
	public boolean isIva10() {
		return this.iva.getSigla().equals(Configuracion.SIGLA_IVA_10);
	}
	
	@DependsOn("iva")
	public boolean isIva5() {
		return this.iva.getSigla().equals(Configuracion.SIGLA_IVA_5);
	}

	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public double getCostoDs() {
		return this.getMisc().redondeoDosDecimales(costoDs);
	}

	public void setCostoDs(double costoDs) {
		this.costoDs = costoDs;
	}

	public double getImporteExentaGs() {
		return importeExentaGs;
	}

	public void setImporteExentaGs(double importeExentaGs) {
		this.importeExentaGs = importeExentaGs;
	}

	public double getImporteExentaDs() {
		return importeExentaDs;
	}

	public void setImporteExentaDs(double importeExentaDs) {
		this.importeExentaDs = importeExentaDs;
	}

	public double getImporteGravadaGs() {
		return importeGravadaGs;
	}

	public void setImporteGravadaGs(double importeGravadaGs) {
		this.importeGravadaGs = importeGravadaGs;
	}

	public double getImporteGravadaDs() {
		return importeGravadaDs;
	}

	public void setImporteGravadaDs(double importeGravadaDs) {
		this.importeGravadaDs = importeGravadaDs;
	}

	public double getDescuentoGs() {
		return descuentoGs;
	}

	public void setDescuentoGs(double descuentoGs) {
		this.descuentoGs = descuentoGs;
	}

	public double getDescuentoDs() {
		return descuentoDs;
	}

	public void setDescuentoDs(double descuentoDs) {
		this.descuentoDs = descuentoDs;
	}

	public boolean isDescuento() {
		return descuento;
	}

	public void setDescuento(boolean descuento) {
		this.descuento = descuento;
	}

	public MyPair getTipoDescuento() {
		return tipoDescuento;
	}

	public void setTipoDescuento(MyPair tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}

	public String getTextoDescuento() {
		return textoDescuento;
	}

	public void setTextoDescuento(String textoDescuento) {
		this.textoDescuento = textoDescuento;
	}

	public double getImporteDescuentoGs() {
		return importeDescuentoGs;
	}

	public void setImporteDescuentoGs(double importeDescuentoGs) {
		this.importeDescuentoGs = importeDescuentoGs;
	}

	public double getImporteDescuentoDs() {
		return importeDescuentoDs;
	}

	public void setImporteDescuentoDs(double importeDescuentoDs) {
		this.importeDescuentoDs = importeDescuentoDs;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getCantidadRecibida() {
		return cantidadRecibida;
	}

	public void setCantidadRecibida(int cantidadRecibida) {
		this.cantidadRecibida = cantidadRecibida;
	}

	public MyArray getArticulo() {
		return articulo;
	}

	public void setArticulo(MyArray articulo) {
		this.articulo = articulo;
	}

	public MyPair getIva() {
		return iva;
	}

	public void setIva(MyPair iva) {
		this.iva = iva;
	}

	public double getPrecioFinalGs() {
		return precioFinalGs;
	}

	public void setPrecioFinalGs(double precioFinalGs) {
		this.precioFinalGs = precioFinalGs;
	}

	public double getMinoristaGs() {
		return minoristaGs;
	}

	public void setMinoristaGs(double minoristaGs) {
		this.minoristaGs = minoristaGs;
	}

	public double getListaGs() {
		return listaGs;
	}

	public void setListaGs(double listaGs) {
		this.listaGs = listaGs;
	}

	public boolean isIgnorarDescuento() {
		return ignorarDescuento;
	}

	public void setIgnorarDescuento(boolean ignorarDescuento) {
		this.ignorarDescuento = ignorarDescuento;
	}	
}
