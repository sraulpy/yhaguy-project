package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class Remision extends Domain {
	
	private Date fecha;
	private String numero;
	
	private Venta venta;
	private Set<RemisionDetalle> detalles = new HashSet<RemisionDetalle>();

	@Override
	public int compareTo(Object o) {
		return -1;
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

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}

	public Set<RemisionDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<RemisionDetalle> detalles) {
		this.detalles = detalles;
	}

}
