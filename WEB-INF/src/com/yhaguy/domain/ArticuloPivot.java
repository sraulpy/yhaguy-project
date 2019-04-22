package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ArticuloPivot extends Domain {

	private Date fecha;
	private String usuario;
	private String concepto;
	
	private Proveedor proveedor;
	private Set<ArticuloPivotDetalle> detalles = new HashSet<ArticuloPivotDetalle>();
	
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

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public Set<ArticuloPivotDetalle> getDetalles() {
		return detalles;
	}
	
	public List<ArticuloPivotDetalle> getDetalles_() {
		List<ArticuloPivotDetalle> out = new ArrayList<ArticuloPivotDetalle>();
		out.addAll(this.getDetalles());
		return out;
	}

	public void setDetalles(Set<ArticuloPivotDetalle> detalles) {
		this.detalles = detalles;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
}
