package com.yhaguy.gestion.compras.locales;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;

@SuppressWarnings("serial")
public class CompraLocalOrdenDetalleDTO extends DTO {

	private double costoGs = 0;
	private double costoDs = 0;
	private double ultCostoGs = 0;
	private int cantidad = 0;
	private int cantidadRecibida = 0;
	
	private boolean presupuesto = false;
	private boolean ordenCompra = false;
	
	private MyArray articulo = new MyArray();
	private MyPair iva;
	
	@DependsOn({"costoGs", "cantidad"})
	public double getImporteGs(){
		return costoGs * cantidad;
	}
	
	@DependsOn({"costoDs", "cantidad"})
	public double getImporteDs(){
		return costoDs * cantidad;
	}
	
	@DependsOn({"iva", "costoGs", "cantidad"})
	public double getImporteIva() {
		if(this.isExenta())
			return 0;
		int porc = this.isIva10()? 10 : 5;
		return this.getMisc().calcularIVA(this.getImporteGs(), porc);
	}
	
	@DependsOn({"costoGs", "ultCostoGs"})
	public double getDiferencia(){
		return this.getMisc().redondeoDosDecimales(costoGs - ultCostoGs);
	}	
	
	@DependsOn({"diferencia", "ultCostoGs"})
	public double getPorcentajeDiferencia(){
		double out = (this.getDiferencia() * 100)/this.ultCostoGs;
		if (out > 500) {
			out = (double) 500;
		}
		return this.getMisc().redondeoDosDecimales(out);
	}

	@DependsOn({"ultCostoGs", "costoGs"})
	public String getStyleItem(){
		return this.getMisc().colorVariacion(this.ultCostoGs, this.costoGs);
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
	
	@DependsOn("articulo")
	public long getStock() throws Exception {
		if(this.articulo.esNuevo()) return 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getStock(this.articulo.getId());
	}
	
	public double getCostoGs() {
		return costoGs;
	}

	public void setCostoGs(double costoGs) {
		this.costoGs = costoGs;
	}

	public double getCostoDs() {
		return costoDs;
	}

	public void setCostoDs(double costoDs) {
		this.costoDs = this.getMisc().redondeoDosDecimales(costoDs);
	}

	public double getUltCostoGs() {
		return ultCostoGs;
	}

	public void setUltCostoGs(double ultCostoGs) {
		this.ultCostoGs = ultCostoGs;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public MyArray getArticulo() {
		return articulo;
	}

	public void setArticulo(MyArray articulo) {
		this.articulo = articulo;
	}

	public boolean isPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(boolean presupuesto) {
		this.presupuesto = presupuesto;
	}

	public boolean isOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(boolean ordenCompra) {
		this.ordenCompra = ordenCompra;
	}

	public MyPair getIva() {
		return iva;
	}

	public void setIva(MyPair iva) {
		this.iva = iva;
	}

	public int getCantidadRecibida() {
		return cantidadRecibida;
	}

	public void setCantidadRecibida(int cantidadRecibida) {
		this.cantidadRecibida = cantidadRecibida;
	}
}
