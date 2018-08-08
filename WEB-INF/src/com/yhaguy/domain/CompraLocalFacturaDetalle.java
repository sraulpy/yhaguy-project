package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class CompraLocalFacturaDetalle extends Domain {
	
	private double costoGs;
	private double costoDs;
	private double importeExentaGs;
	private double importeExentaDs;
	private double importeGravadaGs;
	private double importeGravadaDs;
	private double descuentoGs;
	private double descuentoDs;
	private String textoDescuento;
	private double importeDescuentoGs;
	private double importeDescuentoDs;
	private boolean descuento;
	private Tipo tipoDescuento;
	private int cantidad;
	private int cantidadRecibida;
	
	private Articulo articulo;	
	private Tipo iva;

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

	public boolean isDescuento() {
		return descuento;
	}

	public void setDescuento(boolean descuento) {
		this.descuento = descuento;
	}

	public Tipo getTipoDescuento() {
		return tipoDescuento;
	}

	public void setTipoDescuento(Tipo tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
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

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	@Override
	public int compareTo(Object o) {
		CompraLocalFacturaDetalle cmp = (CompraLocalFacturaDetalle) o;
		boolean isOk = true;		
		isOk = isOk && (this.id.compareTo(cmp.id)==0);		   
		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}

	public Tipo getIva() {
		return iva;
	}

	public void setIva(Tipo iva) {
		this.iva = iva;
	}
}
