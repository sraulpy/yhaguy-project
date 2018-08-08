package com.yhaguy.gestion.empresa.ctacte;

import java.util.Date;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyConverter;

public class CtaCteEmpresaDetalleImputacion extends DTO {

		//Montos a Asignar
		private double montoGs = 0;
		private double montoDs = 0;
		
		//Saldos del movimiento
		private double saldoGs = 0;
		private double saldoDs = 0;
		private double tipoCambio = 0;

		private CtaCteEmpresaMovimientoDTO movimiento = new CtaCteEmpresaMovimientoDTO();
		
		private String styleSaldoGs = "";
		private String styleSaldoDs = "";
		private boolean selected = false;	
		private MyConverter format;
		
		
		public String getDescripcion(){
			return (this.movimiento.getNroComprobante() + " - " + 
					this.movimiento.getTipoMovimiento().getPos1()).toUpperCase();
		}
		
		public String getStyleVencimiento(){
			Date today = this.getMisc().getFechaHoy00();
			Date vencimiento = this.movimiento.getFechaVencimiento();
			if(vencimiento == null)
				return "";
			
			if (today.compareTo(vencimiento) >= 0) {
				return "font-weight:bold;color:red";
			} else {
				return "";
			}		
		}
		
		public double getMontoGs() {
			return montoGs;
		}

		public void setMontoGs(double montoGs) {
			this.montoGs = this.getMisc().redondeoDosDecimales(montoGs);
		}

		public double getMontoDs() {
			return montoDs;
		}

		public void setMontoDs(double montoDs) {
			this.montoDs = this.getMisc().redondeoDosDecimales(montoDs);
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
			this.saldoGs = this.getMisc().redondeoDosDecimales(saldoGs);
		}

		public double getSaldoDs() {
			return saldoDs;
		}

		public void setSaldoDs(double saldoDs) {
			this.saldoDs = this.getMisc().redondeoDosDecimales(saldoDs);
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
	}