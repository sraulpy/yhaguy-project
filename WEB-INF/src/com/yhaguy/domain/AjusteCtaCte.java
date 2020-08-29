package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AjusteCtaCte extends Domain {

	public static final String ANTICIPOS = "ANTICIPO";
	public static final String ANTICIPOS_PAGOS = "ANTICIPO PAGO";
	
	private Date fecha;
	private String descripcion;
	private double importe; // siempre almacena el importe en gs.
	private double tipoCambio = 1; // si la moneda es gs el tipoCambio = 1
	
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

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

}
