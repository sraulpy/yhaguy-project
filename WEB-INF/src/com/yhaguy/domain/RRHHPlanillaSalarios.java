package com.yhaguy.domain;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class RRHHPlanillaSalarios extends Domain {

	private String mes;
	private String anho;
	private String funcionario;
	
	private double salarios;
	private double comision;
	private double anticipo;
	private double bonificacion;
	private double otrosHaberes;
	private double totalHaberes;
	private double prestamos;
	private double adelantos;
	private double otrosDescuentos;
	private double corporativo;
	private double uniforme;
	private double repuestos;
	private double seguro;
	private double embargo;
	private double ips;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	@DependsOn({ "salarios", "comision", "anticipo", "bonificacion", "otrosHaberes", "otrosDescuentos", "corporativo",
			"uniforme", "repuestos", "seguro", "embargo", "ips", })
	public double getTotalACobrar() {
		return this.salarios + this.comision + this.anticipo + this.bonificacion + this.otrosHaberes
				+ this.otrosDescuentos + this.corporativo + this.uniforme + this.repuestos + this.seguro + this.embargo
				+ this.ips;
	}
	
	@DependsOn({ "salarios", "comision", "anticipo", "bonificacion", "otrosHaberes", "otrosDescuentos", "corporativo",
		"uniforme", "repuestos", "seguro", "embargo", "ips", })
	public double getTotalADescontar() {
		double out = 0;
		if (this.salarios < 0) out += this.salarios; if (this.comision < 0) out += this.comision;
		if (this.anticipo < 0) out += this.anticipo; if (this.bonificacion < 0) out += this.bonificacion;
		if (this.otrosHaberes < 0) out += this.otrosHaberes; if (this.otrosDescuentos < 0) out += this.otrosDescuentos;
		if (this.corporativo < 0) out += this.corporativo; if (this.uniforme < 0) out += this.uniforme;
		if (this.repuestos < 0) out += this.repuestos; if (this.seguro < 0) out += this.seguro;
		if (this.embargo < 0) out += this.embargo; if (this.ips < 0) out += this.ips;
		return out;
}

	public String getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}

	public double getSalarios() {
		return salarios;
	}

	public void setSalarios(double salarios) {
		this.salarios = salarios;
	}

	public double getComision() {
		return comision;
	}

	public void setComision(double comision) {
		this.comision = comision;
	}

	public double getAnticipo() {
		return anticipo;
	}

	public void setAnticipo(double anticipo) {
		this.anticipo = anticipo;
	}

	public double getBonificacion() {
		return bonificacion;
	}

	public void setBonificacion(double bonificacion) {
		this.bonificacion = bonificacion;
	}

	public double getOtrosHaberes() {
		return otrosHaberes;
	}

	public void setOtrosHaberes(double otrosHaberes) {
		this.otrosHaberes = otrosHaberes;
	}

	public double getTotalHaberes() {
		return totalHaberes;
	}

	public void setTotalHaberes(double totalHaberes) {
		this.totalHaberes = totalHaberes;
	}

	public double getPrestamos() {
		return prestamos;
	}

	public void setPrestamos(double prestamos) {
		this.prestamos = prestamos;
	}

	public double getAdelantos() {
		return adelantos;
	}

	public void setAdelantos(double adelantos) {
		this.adelantos = adelantos;
	}

	public double getOtrosDescuentos() {
		return otrosDescuentos;
	}

	public void setOtrosDescuentos(double otrosDescuentos) {
		this.otrosDescuentos = otrosDescuentos;
	}

	public double getCorporativo() {
		return corporativo;
	}

	public void setCorporativo(double corporativo) {
		this.corporativo = corporativo;
	}

	public double getUniforme() {
		return uniforme;
	}

	public void setUniforme(double uniforme) {
		this.uniforme = uniforme;
	}

	public double getRepuestos() {
		return repuestos;
	}

	public void setRepuestos(double repuestos) {
		this.repuestos = repuestos;
	}

	public double getSeguro() {
		return seguro;
	}

	public void setSeguro(double seguro) {
		this.seguro = seguro;
	}

	public double getEmbargo() {
		return embargo;
	}

	public void setEmbargo(double embargo) {
		this.embargo = embargo;
	}

	public double getIps() {
		return ips;
	}

	public void setIps(double ips) {
		this.ips = ips;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getAnho() {
		return anho;
	}

	public void setAnho(String anho) {
		this.anho = anho;
	}
}
