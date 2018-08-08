package com.yhaguy.gestion.bancos.libro;

import java.util.Date;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.bancos.cheques.BancoChequeDTO;

@SuppressWarnings("serial")
public class BancoMovimientoDTO extends DTO{
	
	private Date fecha = new Date();
	private double monto = 0;
	private MyArray tipoMovimiento;
	private boolean anulado;
	private MyArray cheque;
	private BancoChequeDTO chequeDto;
	
	private String nroReferencia;
	private String descripcion;
	private MyPair nroCuenta;
	
	private double saldo = 0;
	
	/**
	 * @return el debe..
	 */
	public double getDebe() {
		return this.isDepositoBancario() || this.isSaldoInicial() ? this.monto : 0.0;
	}
	
	/**
	 * @return el haber..
	 */
	public double getHaber() {
		return this.isEmisionCheque() || this.isDebitoBanco()
				|| this.isChequeRechazado() ? this.monto : 0.0;
	}
	
	/**
	 * @return true si es un deposito bancario..
	 */
	public boolean isDepositoBancario() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_DEPOSITO_BANCARIO);
	}
	
	/**
	 * @return true si es un saldo inicial del banco..
	 */
	public boolean isSaldoInicial() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_SALDO_INICIAL_BANCO);
	}
	
	/**
	 * @return true si es emision de cheque..
	 */
	public boolean isEmisionCheque() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_EMISION_CHEQUE);
	}
	
	/**
	 * @return true si es debito banco..
	 */
	public boolean isDebitoBanco() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_DEBITO_BANCARIO);
	}
	
	/**
	 * @return true si es un cheque rechazado..
	 */
	public boolean isChequeRechazado() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_CHEQUE_RECHAZADO);
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
	
	

	public MyArray getCheque() {
		return cheque;
	}

	public void setCheque(MyArray cheque) {
		this.cheque = cheque;
	}

	public String getNroReferencia() {
		return nroReferencia;
	}

	public void setNroReferencia(String nroReferencia) {
		this.nroReferencia = nroReferencia;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	
	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public MyPair getNroCuenta() {
		return nroCuenta;
	}

	public void setNroCuenta(MyPair nroCuenta) {
		this.nroCuenta = nroCuenta;
	}

	
	public BancoChequeDTO getChequeDto() {
		return chequeDto;
	}

	public void setChequeDto(BancoChequeDTO chequeDto) {
		this.chequeDto = chequeDto;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	
}
