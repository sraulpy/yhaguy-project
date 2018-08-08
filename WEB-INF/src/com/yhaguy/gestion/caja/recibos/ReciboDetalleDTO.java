package com.yhaguy.gestion.caja.recibos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyConverter;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class ReciboDetalleDTO extends DTO {
	
	private String concepto = "";
	private double montoGs = 0;
	private double montoDs = 0;	
	
	private double saldoGs = 0;
	private double saldoDs = 0;
	private double tipoCambio = 0;

	private CtaCteEmpresaMovimientoDTO movimiento;
	
	//atributos auxiliares	
	private String styleSaldoGs = "";
	private String styleSaldoDs = "";
	private boolean selected = false;	
	private MyConverter format;
	
	/**
	 * @return true si es cancelacion de factura..
	 */
	public boolean isCancelacion() {
		return (this.movimiento.getSaldo() - this.montoGs) <= 1;
	}
	
	/**
	 * @return la descripcion del detalle..
	 */
	public String getDescripcion(){
		if((this.concepto != null) && (!this.concepto.isEmpty()))
			return this.concepto;
		return (this.movimiento.getNroComprobante() + " - " + 
				this.movimiento.getTipoMovimiento().getPos1()).toUpperCase();
	}
	
	/**
	 * @return el estilo para el vencimiento..
	 */
	public String getStyleVencimiento(){
		Date today = getMisc().getFechaHoy00();
		Date vencimiento = this.movimiento.getFechaVencimiento();		
		if(vencimiento == null)
			return "";		
		if (today.compareTo(vencimiento) >= 0) {
			return "font-weight:bold;color:red";
		} else {
			return "";
		}		
	}
	
	/**
	 * @return la fecha del detalle..
	 */
	public String getFecha() {
		if (this.movimiento != null) {
			if (this.movimiento.esNuevo() == false)
				return Utiles.getDateToString(
						this.movimiento.getFechaEmision(), Utiles.DD_MM_YYYY);
		}
		return Utiles.getDateToString(this.getModificado(), Utiles.DD_MM_YYYY);
	}
	
	public double getMontoGs() {
		return montoGs;
	}
	
	/**
	 * @return el monto formateado como String..
	 */
	public String getMontoGs_() {
		NumberFormat formatter = new DecimalFormat("###,###,##0");
		return formatter.format(this.getMontoGs());
	}

	public void setMontoGs(double montoGs) {
		this.montoGs = getMisc().redondeoCuatroDecimales(montoGs);
	}

	public double getMontoDs() {
		return montoDs;
	}

	public void setMontoDs(double montoDs) {
		this.montoDs = getMisc().redondeoCuatroDecimales(montoDs);
	}

	public CtaCteEmpresaMovimientoDTO getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(CtaCteEmpresaMovimientoDTO movimiento) {
		this.movimiento = movimiento;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public double getSaldoGs() {
		return saldoGs;
	}

	public void setSaldoGs(double saldoGs) {
		this.saldoGs = getMisc().redondeoCuatroDecimales(saldoGs);
	}

	public double getSaldoDs() {
		return saldoDs;
	}

	public void setSaldoDs(double saldoDs) {
		this.saldoDs = getMisc().redondeoCuatroDecimales(saldoDs);
	}

	public String getStyleSaldoGs() {
		return styleSaldoGs;
	}

	public void setStyleSaldoGs(String styleSaldoGs) {
		this.styleSaldoGs = styleSaldoGs;
	}

	public String getStyleSaldoDs() {
		return styleSaldoDs;
	}

	public void setStyleSaldoDs(String styleSaldoDs) {
		this.styleSaldoDs = styleSaldoDs;
	}

	public MyConverter getFormat() {
		return format;
	}

	public void setFormat(MyConverter format) {
		this.format = format;
	}	

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	public int compareTo(Object o) {
		ReciboDetalleDTO opd = (ReciboDetalleDTO) o;
		Date vto = this.movimiento.getFechaVencimiento();
		Date opdVto = opd.getMovimiento().getFechaVencimiento();
		Date vto_ = vto == null ? new Date() : vto;
		Date opdVto_ = opdVto == null ? new Date() : opdVto;
		return vto_.compareTo(opdVto_);
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
}
