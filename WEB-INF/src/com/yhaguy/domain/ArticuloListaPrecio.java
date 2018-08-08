package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloListaPrecio extends Domain {
	
	private String descripcion;
	private String formula;
	private int margen;
	private boolean activo;
	
	private Date desde;
	private Date hasta;
	
	private Set<ArticuloListaPrecioDetalle> detalles = new HashSet<ArticuloListaPrecioDetalle>();

	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return true si el precio es vigente..
	 */
	public boolean isVigente() {
		Date hoy = new Date();
		return (hoy.after(this.desde) && this.hasta == null) || hoy.after(this.desde) && hoy.before(this.hasta);		
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getMargen() {
		return margen;
	}

	public void setMargen(int margen) {
		this.margen = margen;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public Set<ArticuloListaPrecioDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<ArticuloListaPrecioDetalle> detalles) {
		this.detalles = detalles;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
}
