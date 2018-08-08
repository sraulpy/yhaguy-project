package com.yhaguy.gestion.contabilidad.retencion;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class RetencionIvaDetalleDTO extends DTO {
	
	private double importeIvaGs = 0;
	private int porcentaje;
	
	private MyArray gasto;
	private MyArray compra;
	
	@DependsOn({ "importeIvaGs", "porcentaje" })
	public double getImporteRetenido() {		
		return this.getMisc().obtenerValorDelPorcentaje(this.importeIvaGs, this.porcentaje);
	}
	
	/**
	 * @return el tipo de movimiento..
	 */
	public MyPair getTipoMovimiento() {
		if(this.gasto != null)
			return (MyPair) this.gasto.getPos1();
		if(this.compra != null)
			return (MyPair) this.compra.getPos1();
		return null;
	}
	
	/**
	 * @return el importe de la factura..
	 */
	public double getImporteFactura() {
		if (this.gasto != null)
			return (double) this.gasto.getPos4();
		if (this.compra != null)
			return (double) this.compra.getPos4();
		return 0;
	}
	
	/**
	 * @return el numero de la factura..
	 */
	public String getNumeroFactura() {
		if (this.gasto != null)
			return (String) this.gasto.getPos2();
		if (this.compra != null)
			return (String) this.compra.getPos2();
		return "";	
	}

	public double getImporteIvaGs() {
		return importeIvaGs;
	}

	public void setImporteIvaGs(double importeIvaGs) {
		this.importeIvaGs = importeIvaGs;
	}

	public MyArray getGasto() {
		return gasto;
	}

	public void setGasto(MyArray gasto) {
		this.gasto = gasto;
	}

	public MyArray getCompra() {
		return compra;
	}

	public void setCompra(MyArray compra) {
		this.compra = compra;
	}

	public int getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(int porcentaje) {
		this.porcentaje = porcentaje;
	}

}
