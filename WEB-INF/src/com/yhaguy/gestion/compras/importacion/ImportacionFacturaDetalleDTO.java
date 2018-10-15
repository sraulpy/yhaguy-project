package com.yhaguy.gestion.compras.importacion;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.articulos.ArticuloDTO;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class ImportacionFacturaDetalleDTO extends DTO{

	private double costoGs = 0;
	private double costoDs = 0;
	private double costoSinProrrateoGs = 0;
	private double costoSinProrrateoDs = 0;
	private String textoDescuento = "0";
	private double descuentoGs = 0;
	private double descuentoDs = 0;
	private double importeGastoDescuentoGs = 0;
	private double importeGastoDescuentoDs = 0;
	private boolean gastoDescuento = false;
	private boolean valorProrrateo = false; 
	private MyPair tipoGastoDescuento = new MyPair();
	private int cantidad = 0;
	private int cantidadRecibida = 0;
	
	private double precioFinalGs = 0;
	private double minoristaGs = 0;
	private double listaGs = 0;
	
	private ArticuloDTO articulo = new ArticuloDTO();		
	
	@DependsOn({"articulo", "gastoDescuento"})
	public String getDescripcion(){
		if (this.isGastoDescuento() == true) {
			return this.tipoGastoDescuento.getText();
		}
		return this.articulo.getDescripcion();
	}
	
	@DependsOn({"costoGs", "cantidad", "descuentoGs", "gastoDescuento", "importeGastoDescuentoGs"})
	public double getImporteGsCalculado(){
		if (this.isGastoDescuento() == true) {
			return importeGastoDescuentoGs;
		} else {
			double out = (costoGs * cantidad) - (descuentoGs * cantidad);
			return this.getMisc().redondeoCuatroDecimales(out);
		}		
	}
	
	@DependsOn({"costoDs", "cantidad", "descuentoDs", "gastoDescuento", "importeGastoDescuentoDs"})
	public double getImporteDsCalculado(){
		if (this.isGastoDescuento() == true) {
			return importeGastoDescuentoDs;
		} else {
			double out = (costoDs * cantidad) - (descuentoDs * cantidad);
			return this.getMisc().redondeoCuatroDecimales(out);
		}		
	}
	
	@DependsOn({"costoSinProrrateoGs", "cantidad", "descuentoGs"})
	public double getImporteSinProrrateoGs(){
		double out = (costoSinProrrateoGs * cantidad) - (descuentoGs * cantidad);
		return this.getMisc().redondeoCuatroDecimales(out);
	}
	
	@DependsOn({"costoSinProrrateoDs", "cantidad", "descuentoDs"})
	public double getImporteSinProrrateoDs(){
		double out = (costoSinProrrateoDs * cantidad) - (descuentoDs * cantidad);
		return this.getMisc().redondeoCuatroDecimales(out);
	}
	
	@DependsOn({"gastoDescuento", "valorProrrateo", "importeGastoDescuentoDs"})
	public String getStyleItem(){
		if ((this.isGastoDescuento() == true) && (this.importeGastoDescuentoDs < 0)) {
			return Configuracion.BACKGROUND_NARANJA;
		} else if (this.isValorProrrateo() == true) {
			return Configuracion.BACKGROUND_AMARILLO;
		} else {
			return "";
		}
	}	
	
	@DependsOn({"cantidad", "cantidadRecibida"})
	public int getDiferenciaCantidad(){
		return cantidadRecibida - cantidad;
	}
	
	@SuppressWarnings("static-access")
	@DependsOn("diferenciaCantidad")
	public String getStyle(){
		int dif = this.getDiferenciaCantidad();
		if (dif > 0) {
			return this.getMisc().TEXTO_VERDE;
		} else if( dif < 0){
			return this.getMisc().TEXTO_ROJO;
		} else {
			return this.getMisc().TEXTO_NORMAL;
		}
	}
	
	@DependsOn("articulo")
	public boolean isReferencia(){
		return this.getArticulo().getCodigoInterno().trim().startsWith("@");
	}
	
	/**
	 * @return el margen..
	 */
	public double getMargen(double tipoCambio, double coeficiente) {
		double costoGs = this.costoDs * tipoCambio;
		double incrementoGs = costoGs * coeficiente;
		double costoFinalGs = costoGs + incrementoGs;
		return Utiles.obtenerPorcentajeDelValor((this.precioFinalGs - costoFinalGs), costoFinalGs);
	}
	
	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = this.getMisc().redondeoCuatroDecimales(costoGs);
	}

	public double getCostoDs() {
		return costoDs;
	}

	public void setCostoDs(double costoDs) {
		this.costoDs = this.getMisc().redondeoCuatroDecimales(costoDs);
	}

	public String getTextoDescuento() {
		return textoDescuento;
	}

	public void setTextoDescuento(String textoDescuento) {
		this.textoDescuento = textoDescuento;
	}

	public double getDescuentoGs() {
		return descuentoGs;
	}

	public void setDescuentoGs(double descuentoGs) {
		this.descuentoGs = this.getMisc().redondeoCuatroDecimales(descuentoGs);
	}

	public double getDescuentoDs() {
		return descuentoDs;
	}

	public void setDescuentoDs(double descuentoDs) {
		this.descuentoDs = this.getMisc().redondeoCuatroDecimales(descuentoDs);
	}

	public double getImporteGastoDescuentoGs() {
		return importeGastoDescuentoGs;
	}

	public void setImporteGastoDescuentoGs(double importeGastoDescuentoGs) {
		this.importeGastoDescuentoGs = this.getMisc().redondeoCuatroDecimales(importeGastoDescuentoGs);
	}

	public double getImporteGastoDescuentoDs() {
		return importeGastoDescuentoDs;
	}

	public void setImporteGastoDescuentoDs(double importeGastoDescuentoDs) {
		this.importeGastoDescuentoDs = this.getMisc().redondeoCuatroDecimales(importeGastoDescuentoDs);
	}

	public boolean isGastoDescuento() {
		return gastoDescuento;
	}

	public void setGastoDescuento(boolean gastoDescuento) {
		this.gastoDescuento = gastoDescuento;
	}

	public MyPair getTipoGastoDescuento() {
		return tipoGastoDescuento;
	}

	public void setTipoGastoDescuento(MyPair tipoGastoDescuento) {
		this.tipoGastoDescuento = tipoGastoDescuento;
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

	public ArticuloDTO getArticulo() {
		return articulo;
	}

	public void setArticulo(ArticuloDTO articulo) {
		this.articulo = articulo;
	}

	public boolean isValorProrrateo() {
		return valorProrrateo;
	}

	public void setValorProrrateo(boolean valorProrrateo) {
		this.valorProrrateo = valorProrrateo;
	}

	public double getCostoSinProrrateoGs() {
		return costoSinProrrateoGs;
	}

	public void setCostoSinProrrateoGs(double costoSinProrrateoGs) {
		this.costoSinProrrateoGs = costoSinProrrateoGs;
	}

	public double getCostoSinProrrateoDs() {
		return costoSinProrrateoDs;
	}

	public void setCostoSinProrrateoDs(double costoSinProrrateoDs) {
		this.costoSinProrrateoDs = costoSinProrrateoDs;
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
}
