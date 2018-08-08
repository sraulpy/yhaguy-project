package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class BancoBoletaDeposito extends Domain {

	private BancoCta nroCuenta; // donde se hace el depósito
	private Date fecha;
	private String numeroBoleta;
	private double monto; // total de la suma de cheques
	private double totalEfectivo = 0;
	private Set<BancoChequeTercero> cheques = new HashSet<BancoChequeTercero>();
	private BancoMovimiento bancoMovimiento; // el movimiento de banco
	private SucursalApp sucursalApp;
	private String observacion;
	private String planillaCaja; //nros de planillas separados por ;
	private boolean cerrado;
	private double totalImporte_gs;

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return -1;
	}
	
	/**
	 * @return el importe total..
	 */
	@DependsOn({ "cheques", "totalEfectivo" })
	public double getTotalImporteGs() {
		double out = 0;
		for (BancoChequeTercero cheque : this.cheques) {
			out += cheque.getMonto();
		}
		out += this.totalEfectivo;
		return out;
	}
	
	/**
	 * @return el total de importe en cheques..
	 */
	@DependsOn("cheques")
	public double getTotalImporteCheques() {
		double out = 0;
		for (BancoChequeTercero cheque : this.cheques) {
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

	public String getNumeroBoleta() {
		return numeroBoleta;
	}

	public void setNumeroBoleta(String numeroBoleta) {
		this.numeroBoleta = numeroBoleta;
	}

	public double getMonto() {
		return monto;
	}

	public void setMonto(double monto) {
		this.monto = monto;
	}

	public double getTotalEfectivo() {
		return totalEfectivo;
	}

	public void setTotalEfectivo(double totalEfectivo) {
		this.totalEfectivo = totalEfectivo;
	}

	public BancoCta getNroCuenta() {
		return nroCuenta;
	}

	public Set<BancoChequeTercero> getCheques() {
		return cheques;
	}

	public void setCheques(Set<BancoChequeTercero> cheques) {
		this.cheques = cheques;
	}

	public void setNroCuenta(BancoCta nroCuenta) {
		this.nroCuenta = nroCuenta;
	}

	public BancoMovimiento getBancoMovimiento() {
		return bancoMovimiento;
	}

	public void setBancoMovimiento(BancoMovimiento bancoMovimiento) {
		this.bancoMovimiento = bancoMovimiento;
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

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public String getPlanillaCaja() {
		return planillaCaja;
	}

	public void setPlanillaCaja(String planillaCaja) {
		this.planillaCaja = planillaCaja;
	}

	public double getTotalImporte_gs() {
		return totalImporte_gs;
	}

	public void setTotalImporte_gs(double totalImporte_gs) {
		this.totalImporte_gs = totalImporte_gs;
	}

}
