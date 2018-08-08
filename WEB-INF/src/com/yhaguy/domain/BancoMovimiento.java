package com.yhaguy.domain;

import java.util.Date;

import com.coreweb.domain.Domain;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class BancoMovimiento extends Domain{
	
	private BancoCta nroCuenta;
	private Date fecha;
	private double monto;
	private String nroReferencia;
	private TipoMovimiento tipoMovimiento; //cheque propio; cheque terceros; etc
	private String descripcion;
	private String numeroConciliacion;
	private boolean anulado;
	private boolean conciliado;

	/**
	 * @return true si es un deposito..
	 */
	public boolean isDeposito() {
		return this.tipoMovimiento.getSigla().equals(Configuracion.SIGLA_TM_DEPOSITO_BANCARIO);
	}
	
	/**
	 * @return true si es saldo inicial banco..
	 */
	public boolean isSaldoInicial() {
		return this.tipoMovimiento.getSigla().equals(Configuracion.SIGLA_TM_SALDO_INICIAL_BANCO);
	}
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getMonto() {
		return monto;
	}

	public void setMonto(double monto) {
		this.monto = monto;
	}
	
	@Override
	public int compareTo(Object o) {

		BancoMovimiento cmp = (BancoMovimiento) o;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {
			return -1;
		}
	}

	public BancoCta getNroCuenta() {
		return nroCuenta;
	}

	public void setNroCuenta(BancoCta nroCuenta) {
		this.nroCuenta = nroCuenta;
	}

	public String getNroReferencia() {
		return nroReferencia;
	}

	public void setNroReferencia(String nroReferencia) {
		this.nroReferencia = nroReferencia;
	}

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}

	public boolean isConciliado() {
		return conciliado;
	}

	public void setConciliado(boolean conciliado) {
		this.conciliado = conciliado;
	}

	public String getNumeroConciliacion() {
		return numeroConciliacion;
	}

	public void setNumeroConciliacion(String numeroConciliacion) {
		this.numeroConciliacion = numeroConciliacion;
	}
}
