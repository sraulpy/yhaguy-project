package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class RRHHLiquidacionSalario extends Domain {

	private Date fecha;
	private String cargo;
	private double importeGs;
	
	private Funcionario funcionario;
	private Set<RRHHLiquidacionSalarioDetalle> detalles = new HashSet<RRHHLiquidacionSalarioDetalle>();
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	@DependsOn("detalles")
	public double getImporteGs_() {
		double out = 0;
		for (RRHHLiquidacionSalarioDetalle item : detalles) {
			out += item.getHaberes();
			out -= item.getDescuentos();
		}
		return out;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public Set<RRHHLiquidacionSalarioDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<RRHHLiquidacionSalarioDetalle> detalles) {
		this.detalles = detalles;
	}

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}
}
