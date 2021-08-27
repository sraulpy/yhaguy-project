package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class DebitoGroupauto extends Domain {

	private String origen;
	private String numero;
	private Date fecha;
	private String ruc;
	private String razonSocial;	
	
	private Set<DebitoGroupautoDetalle> detalles = new HashSet<DebitoGroupautoDetalle>();
	private Set<DebitoGroupautoFormaPago> formasPago = new HashSet<DebitoGroupautoFormaPago>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return total importe gs..
	 */
	public double getTotalImporteGs() {
		double out = 0;
		for (DebitoGroupautoDetalle item : detalles) {
			out += item.getPrecioGs();
		}
		return out;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public Set<DebitoGroupautoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<DebitoGroupautoDetalle> detalles) {
		this.detalles = detalles;
	}

	public Set<DebitoGroupautoFormaPago> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(Set<DebitoGroupautoFormaPago> formasPago) {
		this.formasPago = formasPago;
	}

}
