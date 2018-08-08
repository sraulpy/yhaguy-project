package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class VendedorComision extends Domain {

	private long id_proveedor;
	private long id_funcionario;
	private double porc_comision;
	private double porc_comision_cobros;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public long getId_proveedor() {
		return id_proveedor;
	}

	public void setId_proveedor(long id_proveedor) {
		this.id_proveedor = id_proveedor;
	}

	public long getId_funcionario() {
		return id_funcionario;
	}

	public void setId_funcionario(long id_funcionario) {
		this.id_funcionario = id_funcionario;
	}

	public double getPorc_comision() {
		return porc_comision;
	}

	public void setPorc_comision(double porc_comision) {
		this.porc_comision = porc_comision;
	}

	public double getPorc_comision_cobros() {
		return porc_comision_cobros;
	}

	public void setPorc_comision_cobros(double porc_comision_cobros) {
		this.porc_comision_cobros = porc_comision_cobros;
	}
}
