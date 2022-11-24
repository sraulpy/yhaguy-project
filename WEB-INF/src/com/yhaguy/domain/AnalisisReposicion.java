package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class AnalisisReposicion extends Domain {
	
	public static String POR_UNIDADES = "POR UNIDADES";
	public static String POR_IMPORTE = "POR IMPORTE";
	
	private Date fecha;
	private Date desde;
	private Date hasta;
	private double cantidadMeses;
	private String tipoRanking;
	private boolean incluirDevoluciones;
	private String incluirDevoluciones_;
	private boolean incluirRepresentaciones;
	private String incluirRepresentaciones_;
	private boolean incluirValvoline;
	private String incluirValvoline_;
	private String depositos = "";
	
	private Proveedor proveedor;
	private ArticuloMarca marca;
	private ArticuloFamilia familia;
	
	private Set<AnalisisReposicionDetalle> detalles = new HashSet<AnalisisReposicionDetalle>();

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	@DependsOn({"desde", "hasta"})
	public int getTotalMeses() {
		if (this.desde != null && this.hasta != null) {
			return Utiles.getNumeroMeses(this.desde, this.hasta);
		}
		return 0;
	}
	
	/**
	 * @return title
	 */
	public String getTitle() {
		return "Análisis de Reposición - Stock: " + this.depositos;
	}
	
	/**
	 * @return detalles ordenado
	 */
	public List<AnalisisReposicionDetalle> getDetallesOrdenado() {
		List<AnalisisReposicionDetalle> out = new ArrayList<AnalisisReposicionDetalle>();
		out.addAll(this.detalles);
		Collections.sort(out, new Comparator<AnalisisReposicionDetalle>() {
			@Override
			public int compare(AnalisisReposicionDetalle o1, AnalisisReposicionDetalle o2) {
				return o1.getRanking() - o2.getRanking();
			}
		});
		return out;

	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
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

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Set<AnalisisReposicionDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<AnalisisReposicionDetalle> detalles) {
		this.detalles = detalles;
	}

	public String getTipoRanking() {
		return tipoRanking;
	}

	public void setTipoRanking(String tipoRanking) {
		this.tipoRanking = tipoRanking;
	}

	public boolean isIncluirDevoluciones() {
		return incluirDevoluciones;
	}

	public void setIncluirDevoluciones(boolean incluirDevoluciones) {
		this.incluirDevoluciones = incluirDevoluciones;
	}

	public ArticuloMarca getMarca() {
		return marca;
	}

	public void setMarca(ArticuloMarca marca) {
		this.marca = marca;
	}

	public String getIncluirDevoluciones_() {
		if (this.incluirDevoluciones_ == null) {
			return this.incluirDevoluciones ? "SI" : "NO";
		}
		return incluirDevoluciones_;
	}

	public void setIncluirDevoluciones_(String incluirDevoluciones_) {
		this.incluirDevoluciones_ = incluirDevoluciones_;
		this.incluirDevoluciones = incluirDevoluciones_.equals("SI");
	}

	public double getCantidadMeses() {
		return cantidadMeses;
	}

	public void setCantidadMeses(double cantidadMeses) {
		this.cantidadMeses = cantidadMeses;
	}

	public String getDepositos() {
		return depositos;
	}

	public void setDepositos(String depositos) {
		this.depositos = depositos;
	}

	public ArticuloFamilia getFamilia() {
		return familia;
	}

	public void setFamilia(ArticuloFamilia familia) {
		this.familia = familia;
	}

	public boolean isIncluirRepresentaciones() {
		return incluirRepresentaciones;
	}

	public void setIncluirRepresentaciones(boolean incluirRepresentaciones) {
		this.incluirRepresentaciones = incluirRepresentaciones;
	}
	
	public String getIncluirRepresentaciones_() {
		if (this.incluirRepresentaciones_ == null) {
			return this.incluirRepresentaciones ? "SI" : "NO";
		}
		return incluirRepresentaciones_;
	}

	public void setIncluirRepresentaciones_(String incluirRepresentaciones_) {
		this.incluirRepresentaciones_ = incluirRepresentaciones_;
		this.incluirRepresentaciones = incluirRepresentaciones_.equals("SI");
	}	
	
	public boolean isIncluirValvoline() {
		return incluirValvoline;
	}

	public void setIncluirValvoline(boolean incluirValvoline) {
		this.incluirValvoline = incluirValvoline;
	}
	
	public String getIncluirValvoline_() {
		if (this.incluirValvoline_ == null) {
			return this.incluirValvoline ? "SI" : "NO";
		}
		return incluirValvoline_;
	}

	public void setIncluirValvoline_(String incluirValvoline_) {
		this.incluirValvoline_ = incluirValvoline_;
		this.incluirValvoline = incluirValvoline_.equals("SI");
	}
}
