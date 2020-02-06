package com.yhaguy.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class VentaVale extends Domain {
	
	private String descripcion;
	private double facturacionGs;
	private double valeGs;
	private Date vigenciaDesde;
	private Date vigenciaHasta;
	private Date vigenciaEfectivizacion;
	private double valePorcentaje;
	private boolean todosArticulos;
	
	private CondicionPago condicion;
	
	private Set<Articulo> articulos = new HashSet<Articulo>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return true si es vigente
	 */
	public boolean isVigente() {
		Date hoy = new Date();
		return hoy.compareTo(vigenciaDesde) >= 0 && hoy.compareTo(vigenciaHasta) <= 1;
	}
	
	/**
	 * @return el map de articulos..
	 */
	public Map<String, String> getArticulosMap() {
		Map<String, String> out = new HashMap<String, String>();
		for (Articulo art : this.articulos) {
			out.put(art.getCodigoInterno(), art.getCodigoInterno());
		}
		return out;
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

	public double getValePorcentaje() {
		return valePorcentaje;
	}

	public void setValePorcentaje(double valePorcentaje) {
		this.valePorcentaje = valePorcentaje;
	}

	public CondicionPago getCondicion() {
		return condicion;
	}

	public void setCondicion(CondicionPago condicion) {
		this.condicion = condicion;
	}

	public Date getVigenciaEfectivizacion() {
		return vigenciaEfectivizacion;
	}

	public void setVigenciaEfectivizacion(Date vigenciaEfectivizacion) {
		this.vigenciaEfectivizacion = vigenciaEfectivizacion;
	}

	public boolean isTodosArticulos() {
		return todosArticulos;
	}

	public void setTodosArticulos(boolean todosArticulos) {
		this.todosArticulos = todosArticulos;
	}
}
