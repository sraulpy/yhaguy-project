package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class FuncionarioPremio extends Domain {

	private String descripcion;
	private double importeGs;
	private int mes;
	private int anho;
	
	private Funcionario funcionario;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	public String getPeriodo() {
		return this.mes + " - " + this.anho;
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

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAnho() {
		return anho;
	}

	public void setAnho(int anho) {
		this.anho = anho;
	}
}
