package com.yhaguy.gestion.bancos.cheques;

import java.util.Date;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.bancos.libro.BancoMovimientoDTO;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class BancoChequeDTO extends DTO {

	private long numero = 0;
	private Date fechaEmision = new Date();
	private Date fechaVencimiento = new Date();
	private String beneficiario = "";
	private String concepto = "ÓRDEN DE PAGO NRO:";
	private double monto = 0.0;
	
	// datos de origen..
	private String numeroCaja = "";
	private String numeroOrdenPago = "";

	private MyArray moneda;
	private MyPair estadoComprobante;
	private MyPair modoDeCreacion;
	private BancoMovimientoDTO movimiento;
	private MyPair reciboFormaPago = new MyPair();
	
	private BancoCtaDTO banco;

	@DependsOn("fechaEmision")
	public String getFechaEmisionFormateado() {
		return "Asunción, "
				+ Utiles.getDateToString(this.fechaEmision, Utiles.D_MMMM_YYYY);
	}

	@DependsOn("monto")
	public String getTextoMonto() {
		return getMisc().numberToLetter(this.monto);
	}

	@DependsOn("numero")
	public boolean isNumeroProvisorio() {
		boolean out = false;
		if ((this.esNuevo()) && this.numero != 0) {
			out = true;
		}
		return out;
	}

	public long getNumero() {
		return numero;
	}

	public void setNumero(long numero) {
		this.numero = numero;
	}

	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public double getMonto() {
		return monto;
	}

	public void setMonto(double monto) {
		this.monto = monto;
	}

	public MyArray getMoneda() {
		return moneda;
	}

	public void setMoneda(MyArray moneda) {
		this.moneda = moneda;
	}

	public BancoMovimientoDTO getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(BancoMovimientoDTO movimiento) {
		this.movimiento = movimiento;
	}

	public MyPair getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(MyPair estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public MyPair getModoDeCreacion() {
		return modoDeCreacion;
	}

	public void setModoDeCreacion(MyPair modoDeCreacion) {
		this.modoDeCreacion = modoDeCreacion;
	}

	public MyPair getReciboFormaPago() {
		return reciboFormaPago;
	}

	public void setReciboFormaPago(MyPair reciboFormaPago) {
		this.reciboFormaPago = reciboFormaPago;
	}

	public BancoCtaDTO getBanco() {
		return banco;
	}

	public void setBanco(BancoCtaDTO banco) {
		this.banco = banco;
	}

	public String getNumeroCaja() {
		return numeroCaja;
	}

	public void setNumeroCaja(String numeroCaja) {
		this.numeroCaja = numeroCaja;
	}

	public String getNumeroOrdenPago() {
		return numeroOrdenPago;
	}

	public void setNumeroOrdenPago(String numeroOrdenPago) {
		this.numeroOrdenPago = numeroOrdenPago;
	}

}
