package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;


@SuppressWarnings("serial")
public class ImportacionResumenGastosDespacho extends Domain{
	
	private String nroDespacho;
	private String nroLiquidacion;
	private Date fechaFacturaDespacho;
	private double tipoCambio;
	private double valorCIFds;
	private double valorCIFgs;
	private double valorFOBds;
	private double valorFOBgs;
	private double valorFleteDs;
	private double valorFleteGs;
	private double valorSeguroDs;
	private double valorSeguroGs;
	private double totalIVAds;
	private double totalIVAgs;
	private double totalGastosDs;
	private double totalGastosGs;
	private double coeficiente;
	private double coeficienteAsignado;
	private boolean cambioGastoDespacho;
	private Proveedor despachante;

	public String getNroDespacho() {
		return nroDespacho;
	}

	public void setNroDespacho(String nroDespacho) {
		this.nroDespacho = nroDespacho;
	}
	
	public String getNroLiquidacion() {
		return nroLiquidacion;
	}

	public void setNroLiquidacion(String nroLiquidacion) {
		this.nroLiquidacion = nroLiquidacion;
	}

	public Date getFechaFacturaDespacho() {
		return fechaFacturaDespacho;
	}

	public void setFechaFacturaDespacho(Date fechaFacturaDespacho) {
		this.fechaFacturaDespacho = fechaFacturaDespacho;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public double getValorCIFds() {
		return valorCIFds;
	}

	public void setValorCIFds(double valorCIFds) {
		this.valorCIFds = valorCIFds;
	}

	public double getValorCIFgs() {
		return valorCIFgs;
	}

	public void setValorCIFgs(double valorCIFgs) {
		this.valorCIFgs = valorCIFgs;
	}

	public double getValorFOBds() {
		return valorFOBds;
	}

	public void setValorFOBds(double valorFOBds) {
		this.valorFOBds = valorFOBds;
	}

	public double getValorFOBgs() {
		return valorFOBgs;
	}

	public void setValorFOBgs(double valorFOBgs) {
		this.valorFOBgs = valorFOBgs;
	}
	
	public double getValorFleteDs() {
		return valorFleteDs;
	}

	public void setValorFleteDs(double valorFleteDs) {
		this.valorFleteDs = valorFleteDs;
	}

	public double getValorFleteGs() {
		return valorFleteGs;
	}

	public void setValorFleteGs(double valorFleteGs) {
		this.valorFleteGs = valorFleteGs;
	}

	public double getValorSeguroDs() {
		return valorSeguroDs;
	}

	public void setValorSeguroDs(double valorSeguroDs) {
		this.valorSeguroDs = valorSeguroDs;
	}

	public double getValorSeguroGs() {
		return valorSeguroGs;
	}

	public void setValorSeguroGs(double valorSeguroGs) {
		this.valorSeguroGs = valorSeguroGs;
	}

	public double getTotalIVAds() {
		return totalIVAds;
	}

	public void setTotalIVAds(double totalIVAds) {
		this.totalIVAds = totalIVAds;
	}

	public double getTotalIVAgs() {
		return totalIVAgs;
	}

	public void setTotalIVAgs(double totalIVAgs) {
		this.totalIVAgs = totalIVAgs;
	}

	public double getTotalGastosDs() {
		return totalGastosDs;
	}

	public void setTotalGastosDs(double totalGastosDs) {
		this.totalGastosDs = totalGastosDs;
	}

	public double getTotalGastosGs() {
		return totalGastosGs;
	}

	public void setTotalGastosGs(double totalGastosGs) {
		this.totalGastosGs = totalGastosGs;
	}	

	public ImportacionResumenGastosDespacho() {
	}

	public double getCoeficiente() {
		return coeficiente;
	}

	public void setCoeficiente(double coeficiente) {
		this.coeficiente = coeficiente;
	}

	public double getCoeficienteAsignado() {
		return coeficienteAsignado;
	}

	public void setCoeficienteAsignado(double coeficienteAsignado) {
		this.coeficienteAsignado = coeficienteAsignado;
	}

	public boolean isCambioGastoDespacho() {
		return cambioGastoDespacho;
	}

	public void setCambioGastoDespacho(boolean cambioGastoDespacho) {
		this.cambioGastoDespacho = cambioGastoDespacho;
	}

	public Proveedor getDespachante() {
		return despachante;
	}

	public void setDespachante(Proveedor despachante) {
		this.despachante = despachante;
	}

	@Override
	public int compareTo(Object arg0) {
		
		return 0;
	}	
}
