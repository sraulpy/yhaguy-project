package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class Pagare extends Domain {

	private Date fecha;
	private String numero;
	private String descripcion;
	private double importe;
	private double tipoCambio;
	private String numeroPago = "";
	private boolean pagado;
	
	private Tipo moneda;
	private Empresa firmante;
	private Empresa beneficiario;
		
	@Override
	public int compareTo(Object arg0) {
		return 0;
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public Empresa getFirmante() {
		return firmante;
	}

	public void setFirmante(Empresa firmante) {
		this.firmante = firmante;
	}

	public Empresa getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(Empresa beneficiario) {
		this.beneficiario = beneficiario;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public String getNumeroPago() {
		return numeroPago;
	}

	public void setNumeroPago(String numeroPago) {
		this.numeroPago = numeroPago;
	}

	public boolean isPagado() {
		return pagado;
	}

	public void setPagado(boolean pagado) {
		this.pagado = pagado;
	}

}
