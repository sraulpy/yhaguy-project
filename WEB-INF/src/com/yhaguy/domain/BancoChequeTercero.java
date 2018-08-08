package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class BancoChequeTercero extends Domain {

	public static final String PRESTAMO_CC = "PRESTAMO CASA CENTRAL";
	
	private Date emision = new Date();
	private Date fecha;
	private Tipo banco;
	private String numero = "";
	private String librado = "";
	private Tipo moneda;
	private double monto;
	private ReciboFormaPago reciboFormaPago;
	private Cliente cliente;
	private boolean depositado;
	private SucursalApp sucursalApp;
	private String observacion = "";
	private boolean descontado;
	private boolean anulado;
	private boolean rechazado;
	private boolean rechazoInterno;
	private boolean diferido;
	private boolean reembolsado;
	private boolean cancelado;
	
	private String numeroPlanilla;
	private String numeroVenta;
	private String numeroRecibo;
	private String numeroDeposito;
	private String numeroDescuento;
	private String numeroReembolso;
	private String vendedor;
	
	private Date fechaDeposito;
	private Date fechaDescuento;
	private Date fechaRechazo;
	
	private Set<RecaudacionCentral> recaudacionesCentral = new HashSet<RecaudacionCentral>();

	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return true si es prestamo cc..
	 */
	public boolean isPrestamoCC() {
		return this.observacion.equals(PRESTAMO_CC);
	}
	
	/**
	 * @return true si es cheque al dia..
	 */
	public boolean isChequeAlDia(Date fecha) {
		int cmp = fecha.compareTo(this.fecha);
		return cmp >= 0;
	}
	
	/**
	 * @return true si el cheque esta en cartera segun fecha..
	 */
	public boolean isEnCartera(Date fecha) throws Exception {
		Date fechaDeposito = this.getFechaDeposito();
		Date fechaDescuento = this.getFechaDescuento();
		if (fechaDeposito != null) {
			int cmp = fechaDeposito.compareTo(fecha);
			return cmp > 0;
		} else if (fechaDescuento != null) {
			int cmp = fechaDescuento.compareTo(fecha);
			return cmp > 0;
		}
		return true;
	}
	
	/**
	 * @return la fecha del deposito..
	 */
	public Date getFechaDeposito() throws Exception {
		return this.fechaDeposito;
	}
	
	/**
	 * @return la fecha del descuento..
	 */
	public Date getFechaDescuento() throws Exception {
		return this.fechaDescuento;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Tipo getBanco() {
		return banco;
	}

	public void setBanco(Tipo banco) {
		this.banco = banco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getLibrado() {
		return librado;
	}

	public void setLibrado(String librado) {
		this.librado = librado;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public double getMonto() {
		return monto;
	}

	public void setMonto(double monto) {
		this.monto = monto;
	}

	public ReciboFormaPago getReciboFormaPago() {
		return reciboFormaPago;
	}

	public void setReciboFormaPago(ReciboFormaPago reciboFormaPago) {
		this.reciboFormaPago = reciboFormaPago;
	}

	public boolean isDepositado() {
		return depositado;
	}

	public void setDepositado(boolean depositado) {
		this.depositado = depositado;
	}

	public SucursalApp getSucursalApp() {
		return sucursalApp;
	}

	public void setSucursalApp(SucursalApp sucursalApp) {
		this.sucursalApp = sucursalApp;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public boolean isDescontado() {
		return descontado;
	}

	public void setDescontado(boolean descontado) {
		this.descontado = descontado;
	}

	public boolean isAnulado() {
		return anulado;
	}

	public void setAnulado(boolean anulado) {
		this.anulado = anulado;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public boolean isRechazado() {
		return rechazado;
	}

	public void setRechazado(boolean rechazado) {
		this.rechazado = rechazado;
	}

	public String getNumeroPlanilla() {
		return numeroPlanilla;
	}

	public void setNumeroPlanilla(String numeroPlanilla) {
		this.numeroPlanilla = numeroPlanilla;
	}

	public String getNumeroVenta() {
		return numeroVenta;
	}

	public void setNumeroVenta(String numeroVenta) {
		this.numeroVenta = numeroVenta;
	}

	public String getNumeroRecibo() {
		return numeroRecibo;
	}

	public void setNumeroRecibo(String numeroRecibo) {
		this.numeroRecibo = numeroRecibo;
	}

	public String getNumeroDeposito() {
		return numeroDeposito;
	}

	public void setNumeroDeposito(String numeroDeposito) {
		this.numeroDeposito = numeroDeposito;
	}

	public String getNumeroDescuento() {
		return numeroDescuento;
	}

	public void setNumeroDescuento(String numeroDescuento) {
		this.numeroDescuento = numeroDescuento;
	}

	public boolean isDiferido() {
		return diferido;
	}

	public void setDiferido(boolean diferido) {
		this.diferido = diferido;
	}

	public String getVendedor() {
		return vendedor;
	}

	public void setVendedor(String vendedor) {
		this.vendedor = vendedor;
	}

	public Set<RecaudacionCentral> getRecaudacionesCentral() {
		return recaudacionesCentral;
	}

	public void setRecaudacionesCentral(Set<RecaudacionCentral> recaudacionesCentral) {
		this.recaudacionesCentral = recaudacionesCentral;
	}

	public Date getEmision() {
		return emision;
	}

	public void setEmision(Date emision) {
		this.emision = emision;
	}

	public boolean isReembolsado() {
		return reembolsado;
	}

	public void setReembolsado(boolean reembolsado) {
		this.reembolsado = reembolsado;
	}

	public String getNumeroReembolso() {
		return numeroReembolso;
	}

	public void setNumeroReembolso(String numeroReembolso) {
		this.numeroReembolso = numeroReembolso;
	}

	public void setFechaDeposito(Date fechaDeposito) {
		this.fechaDeposito = fechaDeposito;
	}

	public void setFechaDescuento(Date fechaDescuento) {
		this.fechaDescuento = fechaDescuento;
	}

	public boolean isCancelado() {
		return cancelado;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}

	public Date getFechaRechazo() {
		return fechaRechazo;
	}

	public void setFechaRechazo(Date fechaRechazo) {
		this.fechaRechazo = fechaRechazo;
	}

	public boolean isRechazoInterno() {
		return rechazoInterno;
	}

	public void setRechazoInterno(boolean rechazoInterno) {
		this.rechazoInterno = rechazoInterno;
	}
}
