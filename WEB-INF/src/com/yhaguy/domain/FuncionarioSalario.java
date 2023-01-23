package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class FuncionarioSalario extends Domain {

	private Date fecha;
	private String descripcion;
	private double importeGs;
	
	private Funcionario funcionario;
	
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}
