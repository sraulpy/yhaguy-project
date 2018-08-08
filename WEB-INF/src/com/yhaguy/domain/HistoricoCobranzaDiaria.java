package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class HistoricoCobranzaDiaria extends Domain {

	private Date fecha;
	private long id_funcionario;
	private double total_cobranza;

	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
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
}
