package com.yhaguy.gestion.articulos;

public class BeanArticuloHistorial {

	private String fecha;
	private String hora;
	private String numero;
	private String concepto;
	private String deposito;
	private String entrada;
	private String salida;
	private String saldo;
	private String importe;

	public BeanArticuloHistorial(String fecha, String hora, String numero, String concepto, String deposito,
			String entrada, String salida, String saldo, String importe) {
		this.fecha = fecha;
		this.hora = hora;
		this.numero = numero;
		this.concepto = concepto;
		this.entrada = entrada;
		this.salida = salida;
		this.saldo = saldo;
		this.importe = importe;
		this.deposito = deposito;
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

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getEntrada() {
		return entrada;
	}

	public void setEntrada(String entrada) {
		this.entrada = entrada;
	}

	public String getSalida() {
		return salida;
	}

	public void setSalida(String salida) {
		this.salida = salida;
	}

	public String getImporte() {
		return importe;
	}

	public void setImporte(String importe) {
		this.importe = importe;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getDeposito() {
		return deposito;
	}

	public void setDeposito(String deposito) {
		this.deposito = deposito;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}
}
