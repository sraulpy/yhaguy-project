package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class BancoIngreso extends Domain {

	public static final String CONCEPTO_CAPITALIZACION_INTERESES = "CAPITALIZACIÓN DE INTERESES";
	public static final String CONCEPTO_REVERSION = "REVERSIÓN DE EGRESO";
	
	private Date fecha;
	private String numero;
	private double importe;
	private double tipoCambio;
	
	private BancoCta banco;
	private Tipo moneda;
	private String concepto;
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}
	
	/**
	 * @return true si es moneda local..
	 */
	public boolean isMonedaLocal() {
		return this.moneda.getSigla().equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}
	
	/**
	 * @return conceptos..
	 */
	public static List<String> getConceptos() {
		List<String> out = new ArrayList<>();
		out.add(CONCEPTO_CAPITALIZACION_INTERESES);
		out.add(CONCEPTO_REVERSION);
		return out;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public BancoCta getBanco() {
		return banco;
	}

	public void setBanco(BancoCta banco) {
		this.banco = banco;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
}
