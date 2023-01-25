package com.yhaguy.domain;

import org.zkoss.bind.BindUtils;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class FuncionarioDescuento extends Domain {

	private String descripcion;
	private double importeGs;
	private int cuotas;
	
	private Funcionario funcionario;
	
	private boolean permanente = false;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public int getCuotas() {
		return cuotas;
	}

	public void setCuotas(int cuotas) {
		this.cuotas = cuotas;
	}

	public boolean isPermanente() {
		return permanente;
	}

	public void setPermanente(boolean permanente) {
		this.permanente = permanente;
		if (permanente) this.cuotas = 0 ;
		BindUtils.postNotifyChange(null, null, this, "cuotas");
	}
}
