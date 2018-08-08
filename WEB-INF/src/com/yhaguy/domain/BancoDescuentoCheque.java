package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class BancoDescuentoCheque extends Domain {

	public static final String PRESTAMO = "prestamo";
	public static final String ANTICIPO_UTILIDAD = "anticipo";
	
	private Date fecha;
	private double totalChequesDescontado;
	private SucursalApp sucursalApp;
	private String observacion = "";
	
	private double liq_impuestos;
	private double liq_gastos_adm;
	private double liq_intereses;
	private double liq_neto_aldia;
	private double liq_neto_diferidos;
	private boolean liq_registrado;
	
	public double totalImporte_gs;
	private boolean confirmado;
	
	private Tipo moneda;
	private BancoCta banco;
	
	private Set<BancoChequeTercero> cheques = new HashSet<BancoChequeTercero>();
	private Set<BancoCheque> chequesPropios = new HashSet<BancoCheque>();
	private Set<ReciboFormaPago> formasPago = new HashSet<ReciboFormaPago>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return true si es prestamo casa central..
	 */
	public boolean isPrestamoCC() {
		return this.getAuxi().equals(PRESTAMO);
	}

	/**
	 * @return el importe total del descuento de cheques..
	 */
	public double getTotalImporteGs() {
		double out = 0;
		for (BancoChequeTercero cheque : this.cheques) {
			out += cheque.getMonto();
		}
		for (BancoCheque cheque : this.chequesPropios) {
			out += cheque.getMonto();
		}
		return out;
	}
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getTotalChequesDescontado() {
		return totalChequesDescontado;
	}

	public void setTotalChequesDescontado(double totalChequesDescontado) {
		this.totalChequesDescontado = totalChequesDescontado;
	}

	public Set<BancoChequeTercero> getCheques() {
		return cheques;
	}

	public void setCheques(Set<BancoChequeTercero> cheques) {
		this.cheques = cheques;
	}

	public SucursalApp getSucursalApp() {
		return sucursalApp;
	}

	public void setSucursalApp(SucursalApp sucursalApp) {
		this.sucursalApp = sucursalApp;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public Set<BancoCheque> getChequesPropios() {
		return chequesPropios;
	}

	public void setChequesPropios(Set<BancoCheque> chequesPropios) {
		this.chequesPropios = chequesPropios;
	}

	public double getLiq_impuestos() {
		return liq_impuestos;
	}

	public void setLiq_impuestos(double liq_impuestos) {
		this.liq_impuestos = liq_impuestos;
	}

	public double getLiq_gastos_adm() {
		return liq_gastos_adm;
	}

	public void setLiq_gastos_adm(double liq_gastos_adm) {
		this.liq_gastos_adm = liq_gastos_adm;
	}

	public double getLiq_intereses() {
		return liq_intereses;
	}

	public void setLiq_intereses(double liq_intereses) {
		this.liq_intereses = liq_intereses;
	}

	public double getLiq_neto_aldia() {
		return liq_neto_aldia;
	}

	public void setLiq_neto_aldia(double liq_neto_aldia) {
		this.liq_neto_aldia = liq_neto_aldia;
	}

	public double getLiq_neto_diferidos() {
		return liq_neto_diferidos;
	}

	public void setLiq_neto_diferidos(double liq_neto_diferidos) {
		this.liq_neto_diferidos = liq_neto_diferidos;
	}

	public boolean isLiq_registrado() {
		return liq_registrado;
	}

	public void setLiq_registrado(boolean liq_registrado) {
		this.liq_registrado = liq_registrado;
	}

	public Set<ReciboFormaPago> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(Set<ReciboFormaPago> formasPago) {
		this.formasPago = formasPago;
	}

	public BancoCta getBanco() {
		return banco;
	}

	public void setBanco(BancoCta banco) {
		this.banco = banco;
	}

	public double getTotalImporte_gs() {
		return totalImporte_gs;
	}

	public void setTotalImporte_gs(double totalImporte_gs) {
		this.totalImporte_gs = totalImporte_gs;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}

}
