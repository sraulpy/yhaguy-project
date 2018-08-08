package com.yhaguy.gestion.bancos.depositos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.bancos.libro.BancoMovimientoDTO;

@SuppressWarnings("serial")
public class BancoBoletaDepositoDTO extends DTO {

	private BancoCtaDTO nroCuenta = new BancoCtaDTO();
	private Date fecha;
	private String numeroBoleta = "";
	private double monto = 0;
	private double totalEfectivo = 0;
	private List<MyArray> cheques = new ArrayList<MyArray>();
	BancoMovimientoDTO bancoMovimiento;
	private MyPair sucursalApp;
	private String observacion = "CORRESPONDIENTE A LAS CAJAS ";
	private String planillaCaja = "";
	private boolean cerrado = false;
	
	/**
	 * @return el total de importe en cheques..
	 */
	public double getTotalImporteCheques() {
		double out = 0;
		for (MyArray cheque : this.cheques) {
			double monto = (double) cheque.getPos6();
			out += monto;
		}
		return out;
	}
	
	/**
	 * @return el total a depositar..
	 */
	public double getTotalAdepositar() {
		return this.totalEfectivo + this.getTotalImporteCheques();
	}
	
	/**
	 * @return las planillas de caja de la boleta..
	 */
	public List<String> getPlanillas() {
		if (this.planillaCaja.isEmpty()) {
			return new ArrayList<String>();
		}
		String[] items = this.planillaCaja.split(";");		
		return Arrays.asList(items);
	}

	public BancoCtaDTO getNroCuenta() {
		return nroCuenta;
	}

	public void setNroCuenta(BancoCtaDTO nroCuenta) {
		this.nroCuenta = nroCuenta;
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

	public BancoMovimientoDTO getBancoMovimiento() {
		return bancoMovimiento;
	}

	public void setBancoMovimiento(BancoMovimientoDTO bancoMovimiento) {
		this.bancoMovimiento = bancoMovimiento;
	}

	public MyPair getSucursalApp() {
		return sucursalApp;
	}

	public void setSucursalApp(MyPair sucursalApp) {
		this.sucursalApp = sucursalApp;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public List<MyArray> getCheques() {
		return cheques;
	}

	public void setCheques(List<MyArray> cheques) {
		this.cheques = cheques;
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

}
