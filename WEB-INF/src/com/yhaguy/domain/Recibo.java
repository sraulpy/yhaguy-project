package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class Recibo extends Domain {
	
	private String numero;
	private long nro;
	private long idUsuarioCarga;	
	private String nombreUsuarioCarga;
	private Date fechaEmision;
	private char estado; // anulado - contabilizado
	private String tesaka;
	private String cobrador;
	
	private double totalImporteGs;
	private double totalImporteDs;
	private double tipoCambio;
	
	private String numeroPlanilla;
	private String numeroRecibo;
	private Date fechaRecibo;
	
	/** Para saber si requiere actualizar el movimiento de banco **/
	private boolean movimientoBancoActualizado;
	
	/** Para cobros a otra sucursal **/
	private boolean cobroExterno;
	
	/** Orden de Pago entregado **/
	private boolean entregado;
	
	/** El estado del comprobante: pendiente - cerrado - anulado **/
	private Tipo estadoComprobante;
	private String motivoAnulacion;
	
	private TipoMovimiento tipoMovimiento;
	private Proveedor proveedor;
	private Cliente cliente;
	private SucursalApp sucursal;
	private Tipo moneda;
	private RetencionIva retencion;
	
	private Set<ReciboDetalle> detalles = new HashSet<ReciboDetalle>();
	private Set<ReciboFormaPago> formasPago = new HashSet<ReciboFormaPago>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return true si es anulado..
	 */
	public boolean isAnulado() {
		if(this.estadoComprobante == null)
			return false;
		String sigla = this.estadoComprobante.getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO);
	}
	
	/**
	 * @return true si es un recibo de cobro..
	 */
	public boolean isCobro() {
		String sigla = (String) this.tipoMovimiento.getSigla();
		boolean cobro = sigla.equals(Configuracion.SIGLA_TM_RECIBO_COBRO);
		boolean anticipoCobro = sigla.equals(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
		return cobro || anticipoCobro;
	}
	
	/**
	 * @return true si es un pago..
	 */
	public boolean isPago() {
		String sigla = (String) this.tipoMovimiento.getSigla();
		boolean pago = sigla.equals(Configuracion.SIGLA_TM_RECIBO_PAGO);
		boolean anticipoPago = sigla.equals(Configuracion.SIGLA_TM_ANTICIPO_PAGO);
		return pago || anticipoPago;
	}

	
	/**
	 * @return true si es un recibo de cobro..
	 */
	public boolean isCobroAnticipo() {
		String sigla = (String) this.tipoMovimiento.getSigla();
		return sigla.equals(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
	}
	
	/**
	 * @return true si es una cancelacion de cheque rechazado..
	 */
	public boolean isCancelacionCheque() {
		String sigla = (String) this.tipoMovimiento.getSigla();
		return sigla.equals(Configuracion.SIGLA_TM_CANCELACION_CHEQ_RECHAZADO);
	}
	
	
	/**
	 * @return true si es un reembolso de prestamo..
	 */
	public boolean isReembolsoPrestamo() {
		String sigla = (String) this.tipoMovimiento.getSigla();
		return sigla.equals(Configuracion.SIGLA_TM_REEMBOLSO_PRESTAMO);
	}
	
	/**
	 * @return el total efectivo..
	 */
	public double getTotalEfectivo() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isEfectivo()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total cheque al dia..
	 */
	public double getTotalChequeClienteAldia(Date fecha) {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isChequeTercero() && item.isChequeAlDia(fecha)) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total cheque adelantado..
	 */
	public double getTotalChequeClienteAdelantado(Date fecha) {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isChequeTercero() && item.isChequeAdelantado(fecha)) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total de retencion..
	 */
	public double getTotalRetencion() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isRetencion()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total deposito bancario..
	 */
	public double getTotalDepositoBancario() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isDepositoBancario()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total tarjeta credito..
	 */
	public double getTotalTarjetaCredito() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isTarjetaCredito()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total tarjeta debito..
	 */
	public double getTotalTarjetaDebito() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isTarjetaDebito()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total debito por cobranza central..
	 */
	public double getTotalDebitoCobranzaCentral() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isDebitoCobranzaCentral()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total recaudacion casa central..
	 */
	public double getTotalRecaudacionCentral() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isRecaudacionCentral()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total transferencia casa central..
	 */
	public double getTotalTransferenciaCentral() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isTransferenciaCentral()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total saldo a favor generado..
	 */
	public double getTotalSaldoFavorCliente() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isSaldoFavorGenerado()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total saldo a favor cobrado..
	 */
	public double getTotalSaldoFavorCobrado() {
		double out = 0;
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isSaldoFavorCobrado()) {
				out += item.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el importe total sin iva..
	 */
	public double getTotalImporteGsSinIva() {
		return this.getTotalImporteGs() - Utiles.getIVA(this.getTotalImporteGs(), Configuracion.VALOR_IVA_10);
	}
	
	/**
	 * @return el importe total del recibo segun el proveedor..
	 */
	public double getImporteByProveedor(long idProveedor) throws Exception {
		double out = 0;
		for (ReciboDetalle item : this.detalles) {
			out += item.getImporteByProveedor(idProveedor);
		}
		return out;
	}
	
	/**
	 * @return true si el cobro tiene cheque adelantado..
	 */
	public boolean isCobroConChequeAdelantado(Date fecha) {
		for (ReciboFormaPago item : this.formasPago) {
			if ((item.isChequeTercero() == true) && (item.isChequeAlDia(fecha) == false))
				return true;
		}
		return false;
	}
	
	/**
	 * @return true si es una cancelacion de cheque rechazado interno..
	 */
	public boolean isCancelacionChequeRechazadoInterno() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (ReciboDetalle item : this.detalles) {
			if (item.getMovimiento().getTipoMovimiento().getSigla().equals(Configuracion.SIGLA_TM_CHEQUE_RECHAZADO)) {
				List<BancoChequeTercero> cheques = (List<BancoChequeTercero>) rr.getChequesTercero(item.getMovimiento().getNroComprobante());
				if (cheques.size() > 0) {
					BancoChequeTercero cheque = cheques.get(0);
					return cheque.isRechazoInterno();
				}
			}
		}
		return false;
	}
	
	/**
	 * @return el detalle de cobranzas al dia..
	 */
	public Object[] getCobranzaAlDia(Date fecha) {		
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;
		boolean adelantado = false;
		
		for (ReciboFormaPago item : this.formasPago) {
			if (item.isChequeAdelantado(fecha))
				adelantado = true;
		}

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isChequeTercero() && item.isChequeAlDia(fecha)) {
				String desc = "REC. " + this.getNumero_()
						+ " - CHEQUE " + String.format("%08d", Integer.parseInt(item.getChequeNumero())) + " - "
						+ item.getChequeBanco().getDescripcion() + " - VTO. "
						+ item.getChequeVencimiento();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
			if (item.isEfectivo()) {
				String desc = "REC. " + this.getNumero_() + " - EN EFECTIVO";
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
			if (item.isDepositoBancario()) {
				String desc = "REC. "
						+ this.getNumero_()
						+ " - CON DEPÓSITO BANCARIO NRO. "
						+ item.getDepositoNroReferencia() + " - "
						+ item.getDepositoBancoCta().getBanco().getDescripcion();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
			if (item.isTarjetaCredito()) {
				String desc = "REC. " + this.getNumero_() + " - CON TARJETA DE CRÉDITO";
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}	
			if (item.isTarjetaDebito()) {
				String desc = "REC. " + this.getNumero_() + " - CON TARJETA DE DÉBITO";
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
			if ((item.isRetencion()) && (adelantado == false)) {
				String desc = "REC. " + this.getNumero_() + " - RETENCION NRO. "
						+ item.getRetencionNumero();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}
	
	/**
	 * @return el detalle de cobranzas con efectivo..
	 */
	public Object[] getCobranzasConEfectivo() {		
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isEfectivo()) {
				String desc = "REC. " + this.getNumero_() + " - "
						+ item.getDescripcion().toUpperCase();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}
	
	/**
	 * @return el detalle de cobranzas con tarjeta de credito..
	 */
	public Object[] getCobranzasConTarjetaCredito() {		
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isTarjetaCredito()) {
				String desc = "REC. " + this.getNumero_() + " - "
						+ item.getTarjetaProcesadora().getNombre().toUpperCase() + " - " + item.getTarjetaNumero();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}
	
	/**
	 * @return el detalle de cobranzas con tarjeta de debito..
	 */
	public Object[] getCobranzasConTarjetaDebito() {		
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isTarjetaDebito()) {
				String desc = "REC. " + this.getNumero_() + " - "
						+ item.getTarjetaProcesadora().getNombre().toUpperCase() + " - " + item.getTarjetaNumero();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}
	
	/**
	 * @return el detalle de cobranzas con cheque al dia..
	 */
	public Object[] getCobranzasChequeAlDia(Date fecha) {		
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isChequeTercero() && item.isChequeAlDia(fecha)) {
				String desc = "REC." + this.getNumero_()
						+ " - CH." + String.format("%08d", Integer.parseInt(item.getChequeNumero())) + " - "
						+ item.getChequeVencimiento() + " - "
						+ "[" + item.getChequeBanco().getDescripcion().toUpperCase().substring(0, 3) + "]";
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}
	
	/**
	 * @return el detalle de cobranzas con cheque adelantado..
	 */
	public Object[] getCobranzaChequeAdelantado(Date fecha) {
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isChequeAdelantado(fecha)) {
				String desc = "REC." + this.getNumero_()
						+ " - CH." + String.format("%08d", Integer.parseInt(item.getChequeNumero())) + " - "
						+ item.getChequeVencimiento() + " - "
						+ "[" + item.getChequeBanco().getDescripcion().toUpperCase().substring(0, 3) + "]";
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}

	/**
	 * @return el detalle de cobranzas con deposito bancario..
	 */
	public Object[] getCobranzasConDepositoBancario() {		
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isDepositoBancario()) {
				String desc = "REC. " + this.getNumero_() + " - "
						+ item.getDescripcion().toUpperCase();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}

	
	/**
	 * @return el detalle de cobranzas con cta cte saldo a favor generado..
	 */
	public Object[] getCobranzasConCtaCteSaldoFavorGenerado() {
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isSaldoFavorGenerado()) {
				String desc = "REC. " + this.getNumero_() + " - " + item.getDescripcion().toUpperCase();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}
	
	/**
	 * @return el detalle de cobranzas con cta cte saldo a favor cobrado..
	 */
	public Object[] getCobranzasConCtaCteSaldoFavorCobrado() {
		List<Object[]> out = new ArrayList<Object[]>();
		double importeGs = 0;

		for (ReciboFormaPago item : this.formasPago) {
			if (item.isSaldoFavorCobrado()) {
				String desc = "REC. " + this.getNumero_() + " - " + item.getDescripcion().toUpperCase();
				double monto = item.getMontoGs();
				importeGs += monto;
				out.add(new Object[] { desc, monto });
			}
		}
		if (out.size() > 0)
			return new Object[] { out, importeGs };
		return null;
	}
	
	public boolean isMonedaLocal() {
		return this.moneda.getSigla().equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}
	
	public String getNumero_() {
		return numero.substring(7, numero.length());
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public long getIdUsuarioCarga() {
		return idUsuarioCarga;
	}

	public void setIdUsuarioCarga(long idUsuarioCarga) {
		this.idUsuarioCarga = idUsuarioCarga;
	}

	public String getNombreUsuarioCarga() {
		return nombreUsuarioCarga;
	}

	public void setNombreUsuarioCarga(String nombreUsuarioCarga) {
		this.nombreUsuarioCarga = nombreUsuarioCarga;
	}

	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Tipo getMoneda() {
		return moneda;
	}

	public void setMoneda(Tipo moneda) {
		this.moneda = moneda;
	}

	public Set<ReciboDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<ReciboDetalle> detalles) {
		this.detalles = detalles;
	}

	public Set<ReciboFormaPago> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(Set<ReciboFormaPago> formasPago) {
		this.formasPago = formasPago;
	}
	
	public SucursalApp getSucursal() {
		return sucursal;
	}

	public void setSucursal(SucursalApp sucursal) {
		this.sucursal = sucursal;
	}	

	public TipoMovimiento getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}	

	public double getTotalImporteGs() {
		return Math.rint(totalImporteGs * 1) / 1;
	}

	public void setTotalImporteGs(double totalImporteGs) {
		this.totalImporteGs = totalImporteGs;
	}

	public double getTotalImporteDs() {
		return totalImporteDs;
	}

	public void setTotalImporteDs(double totalImporteDs) {
		this.totalImporteDs = totalImporteDs;
	}	

	public char getEstado() {
		return estado;
	}

	public void setEstado(char estado) {
		this.estado = estado;
	}	

	public boolean isMovimientoBancoActualizado() {
		return movimientoBancoActualizado;
	}

	public void setMovimientoBancoActualizado(boolean movimientoBancoActualizado) {
		this.movimientoBancoActualizado = movimientoBancoActualizado;
	}

	public Tipo getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(Tipo estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public RetencionIva getRetencion() {
		return retencion;
	}

	public void setRetencion(RetencionIva retencion) {
		this.retencion = retencion;
	}

	public String getMotivoAnulacion() {
		return motivoAnulacion;
	}

	public void setMotivoAnulacion(String motivoAnulacion) {
		this.motivoAnulacion = motivoAnulacion;
	}

	public boolean isCobroExterno() {
		return cobroExterno;
	}

	public void setCobroExterno(boolean cobroExterno) {
		this.cobroExterno = cobroExterno;
	}

	public String getTesaka() {
		return tesaka;
	}

	public void setTesaka(String tesaka) {
		this.tesaka = tesaka;
	}

	public String getNumeroPlanilla() {
		return numeroPlanilla;
	}

	public void setNumeroPlanilla(String numeroPlanilla) {
		this.numeroPlanilla = numeroPlanilla;
	}

	public boolean isEntregado() {
		return entregado;
	}

	public void setEntregado(boolean entregado) {
		this.entregado = entregado;
	}

	public String getNumeroRecibo() {
		return numeroRecibo;
	}

	public void setNumeroRecibo(String numeroRecibo) {
		this.numeroRecibo = numeroRecibo;
	}

	public Date getFechaRecibo() {
		return fechaRecibo;
	}

	public void setFechaRecibo(Date fechaRecibo) {
		this.fechaRecibo = fechaRecibo;
	}

	public long getNro() {
		return nro;
	}

	public void setNro(long nro) {
		this.nro = nro;
	}

	public String getCobrador() {
		if (cobrador == null) {
			return "- - -";
		}
		return cobrador;
	}

	public void setCobrador(String cobrador) {
		this.cobrador = cobrador;
	}
}
