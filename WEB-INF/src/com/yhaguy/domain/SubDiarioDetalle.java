package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class SubDiarioDetalle extends Domain{
	
	private int tipo; //Debe - Haber
	private String descripcion;	
	private double importe;
	private boolean editable;
	
	private CuentaContable cuenta;

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public CuentaContable getCuenta() {
		return cuenta;
	}

	public void setCuenta(CuentaContable cuenta) {
		this.cuenta = cuenta;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public int compareTo(Object o) {
		SubDiarioDetalle cmp = (SubDiarioDetalle) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.getId()) == 0);
		if (isOk) {
			return 0;
		} else {
			return -1;
		}		
	}
}
