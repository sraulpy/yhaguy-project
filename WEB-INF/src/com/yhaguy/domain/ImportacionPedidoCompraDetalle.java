package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ImportacionPedidoCompraDetalle extends Domain{
	
	private int cantidad;
	private double ultimoCostoDs;
	private Date fechaUltimoCosto;
	private double costoProformaGs;
	private double costoProformaDs;
	private String observacion;
	
	private Articulo articulo;	
	
	public int getCantidad() {
		return cantidad;
	}
	
	public long getCantidad_() {
		Long out = new Long(this.cantidad);
		return out.longValue();
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getUltimoCostoDs() {
		return ultimoCostoDs;
	}

	public void setUltimoCostoDs(double ultimoCostoDs) {
		this.ultimoCostoDs = ultimoCostoDs;
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
		this.costoProformaGs = costoProformaGs;
	}

	public double getCostoProformaDs() {
		return costoProformaDs;
	}

	public void setCostoProformaDs(double costoProformaDs) {
		this.costoProformaDs = costoProformaDs;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Override
	public int compareTo(Object o) {
		ImportacionPedidoCompra cmp = (ImportacionPedidoCompra) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.getId()) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}		
}