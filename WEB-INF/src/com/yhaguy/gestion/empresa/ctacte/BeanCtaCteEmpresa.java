package com.yhaguy.gestion.empresa.ctacte;

import com.yhaguy.Configuracion;

public class BeanCtaCteEmpresa {

	private String razonSocial;
	private String ruc;
	private double avencer;
	private double vencido;
	private double saldo;

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getRuc() {
		if(ruc == null || ruc.isEmpty())
			return Configuracion.RUC_EMPRESA_LOCAL;
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public double getAvencer() {
		return Math.rint(avencer * 1) / 1;
	}

	public void setAvencer(double avencer) {
		this.avencer = avencer;
	}

	public double getVencido() {
		return Math.rint(vencido * 1) / 1;
	}

	public void setVencido(double vencido) {
		this.vencido = vencido;
	}

	public double getSaldo() {
		return Math.rint(saldo * 1) / 1;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

}
