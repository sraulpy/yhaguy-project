package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class OrdenPedidoGastoDetalle extends Domain {
	
	private double importe;
	private String descripcion;
	private DepartamentoApp departamento;
	private CentroCosto centroCosto;	
	private ArticuloGasto articuloGasto;
	private Tipo iva;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}
	
	public DepartamentoApp getDepartamento() {
		return departamento;
	}

	public void setDepartamento(DepartamentoApp departamento) {
		this.departamento = departamento;
	}

	public CentroCosto getCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(CentroCosto centroCosto) {
		this.centroCosto = centroCosto;
	}

	public ArticuloGasto getArticuloGasto() {
		return articuloGasto;
	}

	public void setArticuloGasto(ArticuloGasto articuloGasto) {
		this.articuloGasto = articuloGasto;
	}

	public Tipo getIva() {
		return iva;
	}

	public void setIva(Tipo iva) {
		this.iva = iva;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}	
}
