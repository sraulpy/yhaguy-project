package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class BancoCheque extends Domain {

	private long numero;
	private Date fechaEmision;
	private Date fechaVencimiento;
	private String beneficiario;
	private String concepto;
	private double monto;
	
	// datos de origen..
	private String numeroCaja;
	private String numeroOrdenPago;

	private Tipo moneda;
	// pendiente; aprobado; cerrado; anulado; confeccionado(por defecto)
	private Tipo estadoComprobante;
	// automatico manual
	private Tipo modoDeCreacion;

	private BancoMovimiento movimiento;
	private BancoCta banco;
	
	private ReciboFormaPago reciboFormaPago;
	
	private boolean anulado;
	private boolean cobrado;
	private Date fechaCobro;
	
	/**
	 * @return true si es cheque al dia..
	 */
	public boolean isChequeAlDia() {
		int cmp = this.fechaEmision.compareTo(this.fechaVencimiento);
		return cmp >= 0;
	}
	
	/**
	 * @return true si es cheque a vencer..
	 */
	public boolean isAvencer() {
		Date hoy = new Date();
		int cmp = hoy.compareTo(this.fechaVencimiento);
		return cmp < 0;
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

	public BancoMovimiento getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(BancoMovimiento movimiento) {
		this.movimiento = movimiento;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public Tipo getModoDeCreacion() {
		return modoDeCreacion;
	}

	public void setModoDeCreacion(Tipo modoDeCreacion) {
		this.modoDeCreacion = modoDeCreacion;
	}

	@Override
	public int compareTo(Object o) {

		BancoCheque cmp = (BancoCheque) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}

	public ReciboFormaPago getReciboFormaPago() {
		return reciboFormaPago;
	}

	public void setReciboFormaPago(ReciboFormaPago reciboFormaPago) {
		this.reciboFormaPago = reciboFormaPago;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}

	public BancoCta getBanco() {
		return banco;
	}

	public void setBanco(BancoCta banco) {
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

	public boolean isCobrado() {
		return cobrado;
	}

	public void setCobrado(boolean cobrado) {
		this.cobrado = cobrado;
	}

	public Date getFechaCobro() {
		return fechaCobro;
	}

	public void setFechaCobro(Date fechaCobro) {
		this.fechaCobro = fechaCobro;
	}
}
