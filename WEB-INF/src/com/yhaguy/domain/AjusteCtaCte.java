package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AjusteCtaCte extends Domain {

	public static final String ANTICIPOS = "ANTICIPO";
	
	private Date fecha;
	private String descripcion;
	private double importe;
	
	private CtaCteEmpresaMovimiento debito;
	private CtaCteEmpresaMovimiento credito;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public long getIdSucursalCredito() {
		return this.credito.getSucursal().getId().longValue();
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public CtaCteEmpresaMovimiento getDebito() {
		return debito;
	}

	public void setDebito(CtaCteEmpresaMovimiento debito) {
		this.debito = debito;
	}

	public CtaCteEmpresaMovimiento getCredito() {
		return credito;
	}

	public void setCredito(CtaCteEmpresaMovimiento credito) {
		this.credito = credito;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
