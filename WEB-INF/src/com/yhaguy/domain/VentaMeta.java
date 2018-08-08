package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class VentaMeta extends Domain {

	private String periodo;
	private double enero;
	private double febrero;
	private double marzo;
	private double abril;
	private double mayo;
	private double junio;
	private double julio;
	private double agosto;
	private double setiembre;
	private double octubre;
	private double noviembre;
	private double diciembre;
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public double getEnero() {
		return enero;
	}

	public void setEnero(double enero) {
		this.enero = enero;
	}

	public double getFebrero() {
		return febrero;
	}

	public void setFebrero(double febrero) {
		this.febrero = febrero;
	}

	public double getMarzo() {
		return marzo;
	}

	public void setMarzo(double marzo) {
		this.marzo = marzo;
	}

	public double getAbril() {
		return abril;
	}

	public void setAbril(double abril) {
		this.abril = abril;
	}

	public double getMayo() {
		return mayo;
	}

	public void setMayo(double mayo) {
		this.mayo = mayo;
	}

	public double getJunio() {
		return junio;
	}

	public void setJunio(double junio) {
		this.junio = junio;
	}

	public double getJulio() {
		return julio;
	}

	public void setJulio(double julio) {
		this.julio = julio;
	}

	public double getAgosto() {
		return agosto;
	}

	public void setAgosto(double agosto) {
		this.agosto = agosto;
	}

	public double getSetiembre() {
		return setiembre;
	}

	public void setSetiembre(double setiembre) {
		this.setiembre = setiembre;
	}

	public double getOctubre() {
		return octubre;
	}

	public void setOctubre(double octubre) {
		this.octubre = octubre;
	}

	public double getNoviembre() {
		return noviembre;
	}

	public void setNoviembre(double noviembre) {
		this.noviembre = noviembre;
	}

	public double getDiciembre() {
		return diciembre;
	}

	public void setDiciembre(double diciembre) {
		this.diciembre = diciembre;
	}

}
