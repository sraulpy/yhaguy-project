package com.yhaguy.gestion.rrhh;

public class BeanIps {

	private String cedula;
	private String funcionario;
	private double diasTrabajados;
	private double bonificacion;
	private double salario;
	private double comision;
	
	public BeanIps() {
	}	

	public BeanIps(String cedula, String funcionario, double diasTrabajados, double bonificacion, double salario) {
		super();
		this.cedula = cedula;
		this.funcionario = funcionario;
		this.diasTrabajados = diasTrabajados;
		this.bonificacion = bonificacion;
		this.salario = salario;
	}
	
	public double getTotal() {
		return this.comision + this.salario;
	}
	
	public double getIps() {
		return this.getTotal() * 0.09;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}

	public double getDiasTrabajados() {
		return diasTrabajados;
	}

	public void setDiasTrabajados(double diasTrabajados) {
		this.diasTrabajados = diasTrabajados;
	}
	
	public double getBonificacion() {
		return bonificacion;
	}

	public void setBonificacion(double bonificacion) {
		this.bonificacion = bonificacion;
	}

	public double getSalario() {
		return salario;
	}

	public void setSalario(double salario) {
		this.salario = salario;
	}

	public double getComision() {
		return comision;
	}

	public void setComision(double comision) {
		this.comision = comision;
	}
}
