package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class HistoricoCobranzaVendedor extends Domain {

	private int mes;
	private int anho;
	private long id_funcionario;
	private double total_cobranza;
	private double meta;

	@Override
	public int compareTo(Object arg0) {
		return -1;
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

	public long getId_funcionario() {
		return id_funcionario;
	}

	public void setId_funcionario(long id_funcionario) {
		this.id_funcionario = id_funcionario;
	}

	public double getTotal_cobranza() {
		return total_cobranza;
	}

	public void setTotal_cobranza(double total_cobranza) {
		this.total_cobranza = total_cobranza;
	}

	public double getMeta() {
		return meta;
	}

	public void setMeta(double meta) {
		this.meta = meta;
	}

}
