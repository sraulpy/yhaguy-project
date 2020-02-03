package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class VentaVale extends Domain {
	
	private String descripcion;
	private double facturacionGs;
	private double valeGs;
	private Date vigenciaDesde;
	private Date vigenciaHasta;
	
	private Set<Articulo> articulos = new HashSet<Articulo>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getFacturacionGs() {
		return facturacionGs;
	}

	public void setFacturacionGs(double facturacionGs) {
		this.facturacionGs = facturacionGs;
	}

	public double getValeGs() {
		return valeGs;
	}

	public void setValeGs(double valeGs) {
		this.valeGs = valeGs;
	}

	public Date getVigenciaDesde() {
		return vigenciaDesde;
	}

	public void setVigenciaDesde(Date vigenciaDesde) {
		this.vigenciaDesde = vigenciaDesde;
	}

	public Date getVigenciaHasta() {
		return vigenciaHasta;
	}

	public void setVigenciaHasta(Date vigenciaHasta) {
		this.vigenciaHasta = vigenciaHasta;
	}

	public Set<Articulo> getArticulos() {
		return articulos;
	}

	public void setArticulos(Set<Articulo> articulos) {
		this.articulos = articulos;
	}
}
