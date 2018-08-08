package com.yhaguy.domain;

import java.util.Date;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class OrdenCompra extends Domain {
	
	private Date fecha;
	private String numero;
	private String observacion;
	private String autorizadoPor;
	private String solicitadoPor;
	private String retiradoPor;
	
	private Proveedor proveedor;
	private Set<OrdenCompraDetalle> detalles = new java.util.HashSet<OrdenCompraDetalle>();

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	/**
	 * @return la url de la imagen..
	 */
	public String getUrlImagen() {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "orden_compras/" + this.id + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_BAT + "orden_compras/" + this.id + ".png";
	}
	
	@DependsOn("detalles")
	public double getTotalImporteGs() {
		double out = 0;
		for (OrdenCompraDetalle det : this.detalles) {
			out += det.getImporteGs();
		}
		return out;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion.toUpperCase();
	}

	public String getAutorizadoPor() {
		return autorizadoPor;
	}

	public void setAutorizadoPor(String autorizadoPor) {
		this.autorizadoPor = autorizadoPor;
	}

	public String getSolicitadoPor() {
		return solicitadoPor;
	}

	public void setSolicitadoPor(String solicitadoPor) {
		this.solicitadoPor = solicitadoPor;
	}

	public String getRetiradoPor() {
		return retiradoPor;
	}

	public void setRetiradoPor(String retiradoPor) {
		this.retiradoPor = retiradoPor;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Set<OrdenCompraDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<OrdenCompraDetalle> detalles) {
		this.detalles = detalles;
	}
}
