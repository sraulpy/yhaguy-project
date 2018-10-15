package com.yhaguy.gestion.compras.importacion;

import java.util.Date;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.yhaguy.gestion.articulos.ArticuloDTO;

@SuppressWarnings("serial")
public class ImportacionPedidoCompraDetalleDTO extends DTO{

	private int cantidad = 0;
	private double ultimoCostoDs = 0;
	private Date fechaUltimoCosto = new Date();
	private double costoProformaGs = 0;
	private double costoProformaDs = 0;
	private String observacion = "";
	private int motivo;	//para las diferencias en csv
	private int cantidadSistema = 0; //para las diferencias en csv
	
	private ArticuloDTO articulo = new ArticuloDTO();	
	
	@DependsOn({"cantidad", "costoProformaGs"})
	public double getImporteProformaGs() {
		return this.cantidad * this.costoProformaGs;
	}
	
	@DependsOn({"cantidad", "costoProformaDs"})
	public double getImporteProformaDs(){
		double out = this.cantidad * this.costoProformaDs; 
		return this.getMisc().redondeoCuatroDecimales(out);
	}
	
	@DependsOn({"costoProformaDs", "ultimoCostoDs"})
	public double getVariacion(){
		double out = this.costoProformaDs - this.ultimoCostoDs;
		return this.getMisc().redondeoCuatroDecimales(out);
	}
	
	@DependsOn({"variacion", "ultimoCostoDs"})
	public double getPorcentajeVariacion(){
		double out = (this.getVariacion() * 100) / this.ultimoCostoDs;
		if (out > 500) {
			out = 500;
		}
		return this.getMisc().redondeoCuatroDecimales(out);
	}
	
	@DependsOn({"ultimoCostoDs", "costoProformaDs"})
	public String getColorVariacion(){
		return this.getMisc().colorVariacion(this.ultimoCostoDs, this.costoProformaDs);		
	}
	
	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getUltimoCostoDs() {
		return ultimoCostoDs;
	}

	public void setUltimoCostoDs(double ultimoCostoDs) {
		this.ultimoCostoDs = this.getMisc().redondeoCuatroDecimales(ultimoCostoDs);
	}

	public Date getFechaUltimoCosto() {
		return fechaUltimoCosto;
	}

	public void setFechaUltimoCosto(Date fechaUltimoCosto) {
		this.fechaUltimoCosto = fechaUltimoCosto;
	}

	public double getCostoProformaGs() {
		return costoProformaGs;
	}

	public void setCostoProformaGs(double costoProformaGs) {
		this.costoProformaGs = this.getMisc().redondeoCuatroDecimales(costoProformaGs);
	}

	public double getCostoProformaDs() {
		return costoProformaDs;
	}

	public void setCostoProformaDs(double costoProformaDs) {
		this.costoProformaDs = this.getMisc().redondeoCuatroDecimales(costoProformaDs);
	}		
	
	public ArticuloDTO getArticulo() {
		return articulo;
	}

	public void setArticulo(ArticuloDTO articulo) {
		this.articulo = articulo;
	}

	public int getMotivo() {
		return motivo;
	}

	public void setMotivo(int motivo) {
		this.motivo = motivo;
	}	
	
	public int getCantidadSistema() {
		return cantidadSistema;
	}

	public void setCantidadSistema(int cantidadSistema) {
		this.cantidadSistema = cantidadSistema;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}			
}
