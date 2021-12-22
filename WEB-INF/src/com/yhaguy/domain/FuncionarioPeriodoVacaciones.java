package com.yhaguy.domain;

import java.util.Date;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class FuncionarioPeriodoVacaciones extends Domain {

	private int diasVacaciones;
	private int diasUsados;
	private int saldoAnterior;
	
	private Date vigencia;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	@DependsOn({ "diasVacaciones", "saldoAnterior", "diasUsados" })
	public int getSaldo() {
		return (this.diasVacaciones + this.saldoAnterior) - this.diasUsados;
	}
	
	@DependsOn("vigencia")
	public boolean isVigente() {
		return this.vigencia != null ? new Date().compareTo(this.vigencia) < 0 : false;
	}

	public int getDiasVacaciones() {
		return diasVacaciones;
	}

	public void setDiasVacaciones(int diasVacaciones) {
		this.diasVacaciones = diasVacaciones;
	}

	public int getDiasUsados() {
		return diasUsados;
	}

	public void setDiasUsados(int diasUsados) {
		this.diasUsados = diasUsados;
	}

	public int getSaldoAnterior() {
		return saldoAnterior;
	}

	public void setSaldoAnterior(int saldoAnterior) {
		this.saldoAnterior = saldoAnterior;
	}

	public Date getVigencia() {
		return vigencia;
	}

	public void setVigencia(Date vigencia) {
		this.vigencia = vigencia;
	}
}
