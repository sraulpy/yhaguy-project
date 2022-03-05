package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AnalisisReposicion extends Domain {
	
	public static String POR_UNIDADES = "POR UNIDADES";
	public static String POR_IMPORTE = "POR IMPORTE";
	
	private Date fecha;
	private Date desde;
	private Date hasta;
	private String tipoRanking;
	private boolean incluirDevoluciones;
	private String incluirDevoluciones_;
	
	private Proveedor proveedor;
	private ArticuloMarca marca;
	
	private Set<AnalisisReposicionDetalle> detalles = new HashSet<AnalisisReposicionDetalle>();

	@Override
	public int compareTo(Object arg0) {
		return -1;
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

}
