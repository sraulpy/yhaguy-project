package com.yhaguy.domain;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;
import com.yhaguy.util.Utiles;


@SuppressWarnings("serial")
public class HistoricoComisiones extends Domain {
	
	private int mes;
	private String anho;
	private String vendedor;
	private String proveedor;
	private double importeVenta;
	private double importeCobro;
	private double importeNotaCredito;
	
	private double porc_Venta = 0.0;
	private double porc_Cobro = 0.0;

	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	@DependsOn({ "importeVenta", "porc_Venta" })
	public double getComisionVenta() {
		return Utiles.obtenerValorDelPorcentaje(this.importeVenta, this.porc_Venta);
	}
	
	@DependsOn({ "importeCobro", "porc_Cobro" })
	public double getComisionCobro() {
		return Utiles.obtenerValorDelPorcentaje(this.importeCobro, this.porc_Cobro);
	}
	
	@DependsOn({ "importeCobro", "porc_Cobro", "importeVenta", "porc_Venta" })
	public double getTotalComision() {
		return Utiles.getRedondeo(this.getComisionCobro() + this.getComisionVenta());
	}
	
	@DependsOn({ "importeCobro", "importeVenta", "importeNotaCredito" })
	public double getTotalSaldoGs() {
		return Utiles.getRedondeo((this.getImporteVenta() + this.getImporteCobro()));
	}

	public String getVendedor() {
		return vendedor;
	}

	public void setVendedor(String vendedor) {
		this.vendedor = vendedor;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public double getImporteVenta() {
		return importeVenta;
	}

	public void setImporteVenta(double importeVenta) {
		this.importeVenta = importeVenta;
	}

	public double getImporteCobro() {
		return importeCobro;
	}

	public void setImporteCobro(double importeCobro) {
		this.importeCobro = importeCobro;
	}

	public double getImporteNotaCredito() {
		return importeNotaCredito;
	}

	public void setImporteNotaCredito(double importeNotaCredito) {
		this.importeNotaCredito = importeNotaCredito;
	}
	
	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public String getAnho() {
		return anho;
	}

	public void setAnho(String anho) {
		this.anho = anho;
	}

	public double getPorc_Venta() {
		return porc_Venta;
	}

	public void setPorc_Venta(double porc_Venta) {
		this.porc_Venta = porc_Venta;
	}

	public double getPorc_Cobro() {
		return porc_Cobro;
	}

	public void setPorc_Cobro(double porc_Cobro) {
		this.porc_Cobro = porc_Cobro;
	}

}
