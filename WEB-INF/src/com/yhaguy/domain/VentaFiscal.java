package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class VentaFiscal extends Domain{
	
	private long idVenta;
	private long idCliente;
	private long idSucursal;
	private long idDeposito;
	private long idModoVenta;
	private long idTipoMovimiento;
	
	private Date emision;
	private Date vencimiento;
	
	private String condicion;	
	private String sucursal;
	private String numero;
	private String moneda;
	private String timbrado;
	private String tipoIva;
	
	private String razonSocial;
	private String ruc;
	
	private double importeGs;
	private double importeDs;
	private double tipoCambio;
	private double gravada;
	private double iva;
	private double exenta;
	
	


	public String getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(String timbrado) {
		this.timbrado = timbrado;
	}

	public String getTipoIva() {
		return tipoIva;
	}

	public void setTipoIva(String tipoIva) {
		this.tipoIva = tipoIva;
	}

	public double getGravada() {
		return gravada;
	}

	public void setGravada(double gravada) {
		this.gravada = gravada;
	}

	public double getIva() {
		return iva;
	}

	public void setIva(double iva) {
		this.iva = iva;
	}

	public double getExenta() {
		return exenta;
	}

	public void setExenta(double exenta) {
		this.exenta = exenta;
	}

	public long getIdVenta() {
		return idVenta;
	}

	public void setIdVenta(long idVenta) {
		this.idVenta = idVenta;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public long getIdSucursal() {
		return idSucursal;
	}

	public void setIdSucursal(long idSucursal) {
		this.idSucursal = idSucursal;
	}

	public long getIdDeposito() {
		return idDeposito;
	}

	public void setIdDeposito(long idDeposito) {
		this.idDeposito = idDeposito;
	}

	public long getIdModoVenta() {
		return idModoVenta;
	}

	public void setIdModoVenta(long idModoVenta) {
		this.idModoVenta = idModoVenta;
	}

	public long getIdTipoMovimiento() {
		return idTipoMovimiento;
	}

	public void setIdTipoMovimiento(long idTipoMovimiento) {
		this.idTipoMovimiento = idTipoMovimiento;
	}

	public Date getEmision() {
		return emision;
	}

	public void setEmision(Date emision) {
		this.emision = emision;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public String getCondicion() {
		return condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
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

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
