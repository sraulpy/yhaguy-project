package com.yhaguy.domain;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class ReciboFormaPago extends Domain {

	private double montoGs;
	private double montoDs;
	
	/** El monto original del cheque **/
	private double montoChequeGs;

	private String descripcion;
	private String nroComprobanteAsociado = "";

	/** La moneda en que fue hecha la transaccion **/
	private Tipo moneda;

	/** Tipo de la forma de Pago: cheque, efectivo, etc.. **/
	private Tipo tipo;

	/** Los ultimos 4 digitos del nro de la tarjeta **/
	private String tarjetaNumero;

	/** El numero del movimiento del ticket generado por el POS.. **/
	private String tarjetaNumeroComprobante;

	/**
	 * Para los pagos con Tarjeta: si es visa-itau, mastercard-continental,
	 * etc..
	 **/
	private Tipo tarjetaTipo;

	/** La cantidad de cuotas de la tarjeta **/
	private int tarjetaCuotas;

	/** La procesadora de la Tarjeta.. **/
	private ProcesadoraTarjeta tarjetaProcesadora;

	/** Cobros o pagos con cheque **/
	private Date chequeFecha;
	private Tipo chequeBanco;
	private String chequeNumero;
	private String chequeLibrador;
	private String chequeBancoDescripcion;

	/** Cobro por Depósito **/
	private BancoCta depositoBancoCta;
	private String depositoNroReferencia;

	/** Cobros con retencion **/
	private String retencionNumero;
	private String retencionTimbrado;
	private Date retencionVencimiento;
	
	/** Pagos con debito por cobranza central **/
	private String reciboDebitoNro;
	
	/**
	 * @return true si es efectivo..
	 */
	public boolean isEfectivo() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_EFECTIVO);
	}
	
	/**
	 * @return true si es cheque de tercero..
	 */
	public boolean isChequeTercero() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO);
	}
	
	/**
	 * @return true si es cheque de tercero por autocobranza..
	 */
	public boolean isChequeTerceroAutocobranza() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_AUTOCOBRANZA);
	}
	
	/**
	 * @return true si es cheque de tercero..
	 */
	public boolean isChequePropio() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO);
	}
	
	/**
	 * @return true si es cheque al dia..
	 */
	public boolean isChequeAlDia(Date fecha) {
		int cmp = fecha.compareTo(this.chequeFecha);
		return cmp >= 0;
	}
	
	/**
	 * @return true si es cheque adelantado..
	 */
	public boolean isChequeAdelantado(Date fecha) {
		return (this.isChequeTercero()) && (this.isChequeAlDia(fecha) == false);
	}
	
	/**
	 * @return true si es tarjeta de credito..
	 */
	public boolean isTarjetaCredito() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO);
	}
	
	/**
	 * @return true si es tarjeta de debito..
	 */
	public boolean isTarjetaDebito() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO);
	}
	
	/**
	 * @return true si es deposito bancario..
	 */
	public boolean isDepositoBancario() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO);
	}
	
	/**
	 * @return true si es retencion cliente..
	 */
	public boolean isRetencion() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_RETENCION);
	}
	
	/**
	 * @return true si es saldo a favor generado..
	 */
	public boolean isSaldoFavorGenerado() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_SALDO_FAVOR_GENERADO);
	}
	
	/**
	 * @return true si es saldo a favor cobrado..
	 */
	public boolean isSaldoFavorCobrado() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_SALDO_FAVOR_COBRADO);
	}
	
	/**
	 * @return true si es recaudacion casa central..
	 */
	public boolean isRecaudacionCentral() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_RECAUDACION_CENTRAL);
	}
	
	/**
	 * @return true si es transferencia casa central..
	 */
	public boolean isTransferenciaCentral() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_TRANSFERENCIA_CENTRAL);
	}
	
	/**
	 * @return true si es debito por cobranza central..
	 */
	public boolean isDebitoCobranzaCentral() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_DEBITO_COBRANZA_CENTRAL);
	}

	public String getMontoGs_() {
		NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
		return FORMATTER.format(this.getMontoGs());
	}
	
	public double getMontoGs() {
		return Math.rint(montoGs * 1) / 1;
	}

	public void setMontoGs(double montoGs) {
		this.montoGs = montoGs;
	}

	public double getMontoDs() {
		return montoDs;
	}

	public void setMontoDs(double montoDs) {
		this.montoDs = montoDs;
	}

	/**
	 * @return la descripcion segun la forma de pago..
	 */
	public String getDescripcion() {
		String formaPago = this.tipo.getDescripcion();
		return this.descripcion.isEmpty() ? formaPago : this.descripcion;
	}
	
	/**
	 * @return la fecha de vencimiento del cheque..
	 */
	public String getChequeVencimiento() {
		Misc misc = new Misc();
		return misc.dateToString(this.chequeFecha, Misc.DD_MM_YYYY);
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public String getTarjetaNumero() {
		return tarjetaNumero;
	}

	public void setTarjetaNumero(String numeroTarjeta) {
		this.tarjetaNumero = numeroTarjeta;
	}

	public Tipo getTarjetaTipo() {
		return tarjetaTipo;
	}

	public void setTarjetaTipo(Tipo tipoTarjeta) {
		this.tarjetaTipo = tipoTarjeta;
	}

	public ProcesadoraTarjeta getTarjetaProcesadora() {
		return tarjetaProcesadora;
	}

	public void setTarjetaProcesadora(ProcesadoraTarjeta procesadoraTarjeta) {
		this.tarjetaProcesadora = procesadoraTarjeta;
	}

	public String getTarjetaNumeroComprobante() {
		return tarjetaNumeroComprobante;
	}

	public void setTarjetaNumeroComprobante(String nroComprobanteTarjeta) {
		this.tarjetaNumeroComprobante = nroComprobanteTarjeta;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public int getTarjetaCuotas() {
		return tarjetaCuotas;
	}

	public void setTarjetaCuotas(int tarjetaCuotas) {
		this.tarjetaCuotas = tarjetaCuotas;
	}

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	public BancoCta getDepositoBancoCta() {
		return depositoBancoCta;
	}

	public void setDepositoBancoCta(BancoCta depositoBancoCta) {
		this.depositoBancoCta = depositoBancoCta;
	}

	public String getDepositoNroReferencia() {
		return depositoNroReferencia;
	}

	public void setDepositoNroReferencia(String depositoNroReferencia) {
		this.depositoNroReferencia = depositoNroReferencia;
	}

	public Date getChequeFecha() {
		return chequeFecha;
	}

	public void setChequeFecha(Date chequeFecha) {
		this.chequeFecha = chequeFecha;
	}

	public Tipo getChequeBanco() {
		return chequeBanco;
	}

	public void setChequeBanco(Tipo chequeBanco) {
		this.chequeBanco = chequeBanco;
	}

	public String getChequeNumero() {
		return chequeNumero;
	}

	public void setChequeNumero(String chequeNumero) {
		this.chequeNumero = chequeNumero;
	}

	public String getChequeLibrador() {
		return chequeLibrador;
	}

	public void setChequeLibrador(String chequeLibrador) {
		this.chequeLibrador = chequeLibrador;
	}

	public String getRetencionNumero() {
		return retencionNumero;
	}

	public void setRetencionNumero(String retencionNumero) {
		this.retencionNumero = retencionNumero;
	}

	public String getRetencionTimbrado() {
		return retencionTimbrado;
	}

	public void setRetencionTimbrado(String retencionTimbrado) {
		this.retencionTimbrado = retencionTimbrado;
	}

	public Date getRetencionVencimiento() {
		return retencionVencimiento;
	}

	public void setRetencionVencimiento(Date retencionVencimiento) {
		this.retencionVencimiento = retencionVencimiento;
	}

	public String getChequeBancoDescripcion() {
		return chequeBancoDescripcion;
	}

	public void setChequeBancoDescripcion(String chequeBancoDescripcion) {
		this.chequeBancoDescripcion = chequeBancoDescripcion;
	}

	public String getNroComprobanteAsociado() {
		return nroComprobanteAsociado;
	}

	public void setNroComprobanteAsociado(String nroComprobanteAsociado) {
		this.nroComprobanteAsociado = nroComprobanteAsociado;
	}

	public double getMontoChequeGs() {
		return montoChequeGs;
	}

	public void setMontoChequeGs(double montoChequeGs) {
		this.montoChequeGs = montoChequeGs;
	}

	public String getReciboDebitoNro() {
		return reciboDebitoNro;
	}

	public void setReciboDebitoNro(String reciboDebitoNro) {
		this.reciboDebitoNro = reciboDebitoNro;
	}
}
