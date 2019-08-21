package com.yhaguy.gestion.caja.recibos;

public class BeanRecibo {

	private String fecha;
	private String numero;
	private String razonSocial;
	private String ruc;
	private String importe;
	private String saldo;

	public BeanRecibo(String fecha, String numero, String razonSocial,
			String ruc, String importe, String saldo) {
		this.fecha = fecha;
		this.numero = numero;
		this.razonSocial = razonSocial;
		this.ruc = ruc;
		this.importe = importe;
		this.saldo = saldo;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getImporte() {
		return importe;
	}

	public void setImporte(String importe) {
		this.importe = importe;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}
}
