package com.yhaguy.gestion.compras.importacion;

import java.util.Date;

import com.coreweb.dto.DTO;
import com.yhaguy.gestion.empresa.ProveedorDTO;

@SuppressWarnings("serial")
public class ResumenGastosDespachoDTO extends DTO{

	private String nroDespacho = "";
	private String nroLiquidacion = "";
	private Date fechaFacturaDespacho = new Date();
	private double tipoCambio = 0;
	private double valorCIFds = 0;
	private double valorCIFgs = 0;
	private double valorFOBds = 0;
	private double valorFOBgs = 0;
	private double valorFleteDs = 0;
	private double valorFleteGs = 0;
	private double valorSeguroDs = 0;
	private double valorSeguroGs = 0;
	private double totalIVAds = 0;
	private double totalIVAgs = 0;
	private double totalGastosDs = 0;
	private double totalGastosGs = 0;	
	private double totalGastosImprevistosDs = 0;
	private double coeficiente = 0;
	private double coeficienteAsignado = 0;
	
	private ProveedorDTO despachante = new ProveedorDTO();
		
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
		this.tipoCambio = this.getMisc().redondeoCuatroDecimales(tipoCambio);
	}
	
	public double getValorCIFds() {
		return valorCIFds;
	}
	
	public void setValorCIFds(double valorCIFds) {
		this.valorCIFds = this.getMisc().redondeoCuatroDecimales(valorCIFds);
	}
	
	public double getValorCIFgs() {
		return valorCIFgs;
	}
	
	public void setValorCIFgs(double valorCIFgs) {
		this.valorCIFgs = this.getMisc().redondeoCuatroDecimales(valorCIFgs);
	}
	
	public double getValorFOBds() {
		return valorFOBds;
	}

	public void setValorFOBds(double valorFOBds) {
		this.valorFOBds = this.getMisc().redondeoCuatroDecimales(valorFOBds);
	}

	public double getValorFOBgs() {
		return valorFOBgs;
	}

	public void setValorFOBgs(double valorFOBgs) {
		this.valorFOBgs = this.getMisc().redondeoCuatroDecimales(valorFOBgs);
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
		this.totalIVAds = this.getMisc().redondeoCuatroDecimales(totalIVAds);
	}
	
	public double getTotalIVAgs() {
		return totalIVAgs;
	}
	
	public void setTotalIVAgs(double totalIVAgs) {
		this.totalIVAgs = this.getMisc().redondeoCuatroDecimales(totalIVAgs);
	}
	
	public double getTotalGastosDs() {
		return totalGastosDs;
	}
	
	public void setTotalGastosDs(double totalGastosDs) {
		this.totalGastosDs = this.getMisc().redondeoCuatroDecimales(totalGastosDs);
	}
	
	public double getTotalGastosGs() {
		return totalGastosGs;
	}
	
	public void setTotalGastosGs(double totalGastosGs) {
		this.totalGastosGs = this.getMisc().redondeoCuatroDecimales(totalGastosGs);
	}

	public double getTotalGastosImprevistosDs() {
		return totalGastosImprevistosDs;
	}

	public void setTotalGastosImprevistosDs(double totalGastosImprevistosDs) {
		this.totalGastosImprevistosDs = this.getMisc().redondeoCuatroDecimales(totalGastosImprevistosDs);
	}

	public double getCoeficiente() {
		return coeficiente;
	}

	public void setCoeficiente(double coeficiente) {
		this.coeficiente = this.getMisc().redondeoCuatroDecimales(coeficiente);
	}

	public double getCoeficienteAsignado() {
		return coeficienteAsignado;
	}

	public void setCoeficienteAsignado(double coeficienteAsignado) {
		this.coeficienteAsignado = this.getMisc().redondeoCuatroDecimales(coeficienteAsignado);
	}

	public ProveedorDTO getDespachante() {
		return despachante;
	}
	
	public void setDespachante(ProveedorDTO despachante) {
		this.despachante = despachante;
	}	
	
	public ProveedorDTO getProveedor(){
		return despachante;
	}
	
	public void setProveedor(ProveedorDTO proveedor){
		this.despachante = proveedor;
	}
}
