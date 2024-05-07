package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class CajaPeriodo extends Domain {
	
	public static final String TIPO_VENTA = "CAJA VENTAS";
	public static final String TIPO_CHICA = "CAJA CHICA";
	public static final String TIPO_COBROS = "CAJA COBRANZAS";
	public static final String TIPO_COBROS_MOBILE = "CAJA COBRANZAS MOVILES";
	public static final String TIPO_PAGOS = "CAJA PAGOS";
	public static final String TIPO_PAGOS_EXTERIOR = "CAJA PAGOS EXTERIOR";
	public static final String TIPO_COBROS_MOBILE_INT = "CAJA COBRANZAS MOVILES INTERIOR";
	public static final String TIPO_COMPRAS_NEUMATICOS = "CAJA COMPRAS NEUMATICOS";
	public static final String TIPO_DEBITOS_CENTRAL = "CAJA DEBITOS CENTRAL";
	public static final String TIPO_CHICA_REQUERIMIENTOS = "CAJA CHICA - REQUERIMIENTOS";
	public static final String TIPO_CHICA_REPOSICION = "CAJA CHICA - REPOSICION";
	public static final String TIPO_VIATICOS = "CAJA VIATICOS";
	public static final String TIPO_PAGOS_DEPURACION = "DEPURACION DE PAGOS";

	private String numero;
	private Date apertura;
	private Date cierre;
	private String tipo;
	private boolean controlRendicion;
	
	private double saldoCajaChica;
	private String saldoCajaChicaAplicado;

	/**
	 * estados de la planilla: abierta - cerrada - procesada (tiene una caja de
	 * tesoreria asignada)
	 **/
	private Tipo estado;

	/** la caja padre de esta planilla **/
	private Caja caja;

	/** quien verifica la rendición **/
	private Funcionario verificador;

	/** el responsable de la Caja **/
	private Funcionario responsable;
	
	/** el arqueo de la Caja **/
	private CajaPeriodoArqueo arqueo;

	/** ingresos-egresos cobros-pagos **/
	private Set<Recibo> recibos = new HashSet<Recibo>();

	/** ingresos por ventas **/
	private Set<Venta> ventas = new HashSet<Venta>();

	/** egresos con Nota de Credito **/
	private Set<NotaCredito> notasCredito = new HashSet<NotaCredito>();

	/** reposición del fondo de Caja **/
	private Set<CajaReposicion> reposiciones = new HashSet<CajaReposicion>();

	/** egresos por gastos contado **/
	private Set<Gasto> gastos = new HashSet<Gasto>();
	
	/** egresos por compras **/
	private Set<CompraLocalFactura> compras = new HashSet<CompraLocalFactura>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return los tipos de cajas..
	 */
	public static List<String> getTipos() {
		List<String> out = new ArrayList<String>();
		out.add(TIPO_VENTA);		
		out.add(TIPO_PAGOS);
		out.add(TIPO_COBROS);
		out.add(TIPO_COBROS_MOBILE);
		out.add(TIPO_COBROS_MOBILE_INT);
		out.add(TIPO_PAGOS_EXTERIOR);
		out.add(TIPO_PAGOS_DEPURACION);
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_AUTOCENTRO)) {
			out.add(TIPO_COMPRAS_NEUMATICOS);
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GROUPAUTO)) {
			out.add(TIPO_COMPRAS_NEUMATICOS);
			out.add(TIPO_DEBITOS_CENTRAL);
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GROUPAUTO)
				|| Configuracion.empresa.equals(Configuracion.EMPRESA_YRSA)) {
			out.add(TIPO_CHICA_REPOSICION);
			out.add(TIPO_CHICA_REQUERIMIENTOS);
			out.add(TIPO_VIATICOS);
		} else {
			out.add(TIPO_CHICA);
		}
		return out;
	}
	
	/**
	 * @return si la planilla esta abierta..
	 */
	public boolean isAbierto() {
		return this.cierre == null;
	}
	
	/**
	 * @return si la planilla es de ventas..
	 */
	public boolean isCajaVentas() {
		return this.tipo.equals(TIPO_VENTA);
	}
	
	/**
	 * @return si la planilla es de cobranzas..
	 */
	public boolean isCajaCobros() {
		return this.tipo.equals(TIPO_COBROS);
	}
	
	/**
	 * @return si la planilla es de cobranzas moviles..
	 */
	public boolean isCajaCobrosMobile() {
		return this.tipo.equals(TIPO_COBROS_MOBILE);
	}
	
	/**
	 * @return si la planilla es de cobranzas moviles interior..
	 */
	public boolean isCajaCobrosMobileInterior() {
		return this.tipo.equals(TIPO_COBROS_MOBILE_INT);
	}
	
	/**
	 * @return si la planilla es de caja chica..
	 */
	public boolean isCajaChica() {
		return this.tipo.equals(TIPO_CHICA) || this.tipo.equals(TIPO_CHICA_REQUERIMIENTOS)
				|| this.tipo.equals(TIPO_CHICA_REPOSICION) || this.tipo.equals(TIPO_VIATICOS);
	}
	
	/**
	 * @return si la planilla es de caja pagos..
	 */
	public boolean isCajaPagos() {
		return this.tipo.equals(TIPO_PAGOS);
	}
	
	/**
	 * @return ingreso total con efectivo..
	 */
	public double getTotalEfectivoIngreso() {
		double out = 0;
		for (Venta venta : this.ventas) {
			if (!venta.isAnulado() && venta.isVentaContado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if(fp.isEfectivo() && fp.isMonedaLocal())
						out += fp.getMontoGs();
				}
			}			
		}		
		for (Recibo cobro : this.recibos) {
			if(cobro.isCobro() && !cobro.isAnulado() && !cobro.getTipoMovimiento().getSigla().equals(Configuracion.SIGLA_TM_ANTICIPO_COBRO)) {
				for (ReciboFormaPago fp : cobro.getFormasPago()) {
					if(fp.isEfectivo() && fp.isMonedaLocal())
						out += fp.getMontoGs();
				}
			}			
		}
		for (Recibo cobro : this.recibos) {
			if(cobro.isCancelacionCheque() && !cobro.isAnulado()) {
				for (ReciboFormaPago fp : cobro.getFormasPago()) {
					if(fp.isEfectivo() && fp.isMonedaLocal())
						out += fp.getMontoGs();
				}
			}			
		}
		for (Recibo cobro : this.recibos) {
			if(cobro.isCobroAnticipo() && !cobro.isAnulado()) {
				for (ReciboFormaPago fp : cobro.getFormasPago()) {
					if(fp.isEfectivo() && fp.isMonedaLocal())
						out += fp.getMontoGs();
				}
			}			
		}
		for (Recibo cobro : this.recibos) {
			if(cobro.isReembolsoPrestamo()) {
				for (ReciboFormaPago fp : cobro.getFormasPago()) {
					if(fp.isEfectivo() && fp.isMonedaLocal())
						out += fp.getMontoGs();
				}
			}			
		}
		return out;
	}
	
	/**
	 * @return ingreso total con efectivo en ds..
	 */
	public double getTotalEfectivoIngresoDs() {
		double out = 0;
		for (Venta venta : this.ventas) {
			if (!venta.isAnulado() && venta.isVentaContado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if(fp.isEfectivo() && !fp.isMonedaLocal())
						out += fp.getMontoDs();
				}
			}			
		}		
		for (Recibo cobro : this.recibos) {
			if(cobro.isCobro() && !cobro.isAnulado()) {
				for (ReciboFormaPago fp : cobro.getFormasPago()) {
					if(fp.isEfectivo() && !fp.isMonedaLocal())
						out += fp.getMontoDs();
				}
			}			
		}
		for (Recibo cobro : this.recibos) {
			if(cobro.isCancelacionCheque() && !cobro.isAnulado()) {
				for (ReciboFormaPago fp : cobro.getFormasPago()) {
					if(fp.isEfectivo() && !fp.isMonedaLocal())
						out += fp.getMontoDs();
				}
			}			
		}
		for (Recibo cobro : this.recibos) {
			if(cobro.isCobroAnticipo() && !cobro.isAnulado()) {
				for (ReciboFormaPago fp : cobro.getFormasPago()) {
					if(fp.isEfectivo() && !fp.isMonedaLocal())
						out += fp.getMontoDs();
				}
			}			
		}
		return out;
	}
	
	/**
	 * @return egreso total con efectivo..
	 */
	public double getTotalEfectivoEgreso() {
		double out = 0;
		for (Gasto gasto : this.gastos) {
			if (!gasto.isAnulado()) {
				for (ReciboFormaPago fp : gasto.getFormasPago()) {
					if(fp.isEfectivo())
						out += fp.getMontoGs();
				}
			}						
		}		
		for (Recibo pago : this.recibos) {
			if (pago.isPago() && !pago.isAnulado()) {
				for (ReciboFormaPago fp : pago.getFormasPago()) {
					if(fp.isEfectivo())
						out += fp.getMontoGs();
				}
			}
		}
		for (CajaReposicion rep : this.reposiciones) {
			if (!rep.isIngreso() && !rep.isAnulado()) {
				if(rep.getFormaPago().isEfectivo())
					out -= rep.getFormaPago().getMontoGs();
			}
		}
		for (NotaCredito nc : this.notasCredito) {
			if (!nc.isAnulado() && nc.isNotaCreditoVentaContado()) {
				out += nc.getImporteGs();
			}			
		}
		return out;
	}
	
	/**
	 * @return egreso total con efectivo ds..
	 */
	public double getTotalEfectivoEgresoDs() {
		double out = 0;
		for (NotaCredito nc : this.notasCredito) {
			if (!nc.isAnulado() && nc.isNotaCreditoVentaContado()) {
				if (!nc.isMonedaLocal()) {
					out += nc.getImporteDs();
				}
			}			
		}
		return out;
	}
	
	/**
	 * @return el total de notas de credito contado..
	 */
	public double getTotalNotaCreditoContado() {
		double out = 0;
		for (NotaCredito nc : this.notasCredito) {
			if (!nc.isAnulado() && nc.isNotaCreditoVentaContado()) {
				out += nc.getImporteGs();
			}			
		}
		return out;
	}
	
	/**
	 * @return el total de retenciones sobre ventas..
	 */
	public double getTotalRetencionesVentas() {
		double out = 0;
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if(fp.isRetencion()) {
						out += fp.getMontoGs();
					}
				}
			}			
		}
		return out;
	}
	
	/**
	 * @return el importe total de recaudacion mra..
	 */
	public double getTotalRecaudacionMra() {
		double out = 0;
		for (Recibo rec : this.getRecibos()) {
			if (rec.isRecaudacionMra()) {
				for (ReciboFormaPago fp : rec.getFormasPago()) {
					if (fp.isRecaudacionMra()) {
						out += fp.getMontoGs();
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return el importe total de cheques diferidos..
	 */
	public double getTotalChequeDiferido(Date fecha) {
		double out = 0;
		for (ReciboFormaPago fp : this.getChequesTercero()) {
			if (fp.isChequeAdelantado(fecha)) {
				out += fp.getMontoChequeGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el importe total de cheques diferidos ds..
	 */
	public double getTotalChequeDiferidoDs(Date fecha) {
		double out = 0;
		for (ReciboFormaPago fp : this.getChequesTercero()) {
			if ((!fp.isMonedaLocal()) && fp.isChequeAdelantado(fecha)) {
				out += fp.getMontoDs();
			}
		}
		return out;
	}
	
	/**
	 * @return el importe total de cheques al dia..
	 */
	public double getTotalChequeAlDia(Date fecha) {
		double out = 0;
		for (ReciboFormaPago fp : this.getChequesTercero()) {
			if (fp.isChequeAlDia(fecha)) {
				out += fp.getMontoChequeGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el importe total de cheques al dia ds..
	 */
	public double getTotalChequeAlDiaDs(Date fecha) {
		double out = 0;
		for (ReciboFormaPago fp : this.getChequesTercero()) {
			if ((!fp.isMonedaLocal()) && fp.isChequeAlDia(fecha)) {
				out += fp.getMontoDs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total de transferencias bancarias..
	 */
	public double getTotalTransferenciasBancarias() {
		double out = 0;
		for (ReciboFormaPago fp : this.getDepositosBancarios()) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	/**
	 * @return el total de transferencias bancarias ds..
	 */
	public double getTotalTransferenciasBancariasDs() {
		double out = 0;
		for (ReciboFormaPago fp : this.getDepositosBancariosDs()) {
			out += fp.getMontoDs();
		}
		return out;
	}
	
	/**
	 * @return el total de recaudacion cc ds..
	 */
	public double getTotalRecaudacionCCDs() {
		double out = 0;
		for (ReciboFormaPago fp : this.getRecaudacionCentral()) {
			out += fp.getMontoDs();
		}
		return out;
	}
	
	/**
	 * @return el total de recaudacion casa central..
	 */
	public double getTotalRecaudacionCentral() {
		double out = 0;
		for (ReciboFormaPago fp : this.getRecaudacionCentral()) {
			out += fp.getMontoGs();
		}
		return out;	
	}
	
	/**
	 * @return el importe total tarjeta de debito..
	 */
	public double getTotalTarjetaDebito() {
		double out = 0;
		for (ReciboFormaPago fp : this.getTarjetasDebito()) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	/**
	 * @return el importe total tarjeta de credito..
	 */
	public double getTotalTarjetaCredito() {
		double out = 0;
		for (ReciboFormaPago fp : this.getTarjetasCredito()) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	/**
	 * @return el total de reembolso prestamos..
	 */
	public double getTotalReembolsoPrestamos() {
		double out = 0;
		for (Recibo recibo : this.recibos) {
			if (recibo.isReembolsoPrestamo()) {
				out += recibo.getTotalImporteGs();
			}
		}
		return out;
	}

	/**
	 * @return el total de retenciones al dia..
	 */
	public double getTotalRetencionesAlDia(Date fecha) {
		double out = 0;
		for (ReciboFormaPago fp : this.getRetencionesClienteAlDia(fecha)) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	/**
	 * @return el total de retenciones diferidos..
	 */
	public double getTotalRetencionesDiferidos(Date fecha) {
		double out = 0;
		for (ReciboFormaPago fp : this.getRetencionesClienteDiferidos(fecha)) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	/**
	 * @return el total de reembolsos de cheques rechazados con efectivo..
	 */
	public double getTotalReembolsoChequeRechazadoEfectivo() {
		double out = 0;
		for (Recibo reembolso : this.getReembolsosChequesRechazados()) {
			for (ReciboFormaPago fp : reembolso.getFormasPago()) {
				if (fp.isEfectivo()) {
					out += fp.getMontoGs();
				}
			}
		}
		return out;
	}
	
	/**
	 * @return el total de reembolso de cheques rechazados con cheque..
	 */
	public double getTotalReembolsoChequeRechazadoCheque(Date fecha) {
		return this.getTotalReembolsoChequeRechazadoChequeAldia(fecha) + this.getTotalReembolsoChequeRechazadoChequeDiferido(fecha);
	}
	
	/**
	 * @return el total de reembolsos de cheques rechazados con cheque al dia..
	 */
	public double getTotalReembolsoChequeRechazadoChequeAldia(Date fecha) {
		double out = 0;
		for (Recibo reembolso : this.getReembolsosChequesRechazados()) {
			for (ReciboFormaPago fp : reembolso.getFormasPago()) {
				if (fp.isChequeTercero() && fp.isChequeAlDia(fecha)) {
					out += fp.getMontoGs();
				}
			}
		}
		return out;
	}
	
	/**
	 * @return el total de reembolsos de cheques rechazados con cheque diferido..
	 */
	public double getTotalReembolsoChequeRechazadoChequeDiferido(Date fecha) {
		double out = 0;
		for (Recibo reembolso : this.getReembolsosChequesRechazados()) {
			for (ReciboFormaPago fp : reembolso.getFormasPago()) {
				if (fp.isChequeTercero() && fp.isChequeAdelantado(fecha)) {
					out += fp.getMontoGs();
				}
			}
		}
		return out;
	}
	
	/**
	 * @return el total de saldo cliente generado..
	 */
	public double getTotalSaldoClienteGenerado() {
		double out = 0;
		for (ReciboFormaPago fp : this.getSaldoFavorCliente()) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	/**
	 * @return el total de saldo cliente cobrado..
	 */
	public double getTotalSaldoClienteCobrado() {
		double out = 0;
		for (ReciboFormaPago fp : this.getSaldoFavorClienteCobrado()) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	/**
	 * @return el total del resumen de efectivo..
	 */
	public double getTotalResumenEfectivo(Date fecha) {
		double efe = this.getTotalEfectivoIngreso() - this.getTotalEfectivoEgreso();
		double ncr = this.getTotalNotaCreditoContado();
		double ret = this.getTotalRetencionesVentas();
		double che = this.getTotalChequeAlDia(fecha);
		double trf = this.getTotalTransferenciasBancarias();
		return efe + ncr + ret + che + trf;
	}
	
	/**
	 * @return el total resumen de cobranzas al dia..
	 */
	public double getTotalResumenCobranzasAlDia(Date fecha) {
		double efe = this.getTotalEfectivoIngreso() - this.getTotalReembolsoChequeRechazadoEfectivo();
		double ret = this.getTotalRetencionesAlDia(fecha);
		double ree = this.getTotalReembolsoChequeRechazadoEfectivo();
		double rec = this.getTotalReembolsoChequeRechazadoChequeAldia(fecha) + this.getTotalReembolsoChequeRechazadoChequeDiferido(fecha);
		double che = this.getTotalChequeAlDia(fecha) - this.getTotalReembolsoChequeRechazadoChequeAldia(fecha);
		double tra = this.getTotalTransferenciasBancarias();
		return efe + ret + ree + rec + che + tra;
	}
	
	/**
	 * @return las formas de pago con cheque tercero..
	 */
	public List<ReciboFormaPago> getChequesTercero() {
		Misc misc = new Misc();
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (fp.isChequeTercero()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length())
								+ " - "
								+ "NRO. "
								+ fp.getChequeNumero()
								+ " - "
								+ "BANCO "
								+ fp.getChequeBanco().getDescripcion()
										.toUpperCase()
								+ " - VTO. "
								+ misc.dateToString(fp.getChequeFecha(),
										Misc.DD_MM_YYYY);
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		for (Recibo cobro : this.getRecibosOrdenado()) {
			if (!cobro.isAnulado()) {
				for (ReciboFormaPago fp : cobro.getFormasPago()) {
					if (fp.isChequeTercero()) {
						String desc = "COB. "
								+ cobro.getNumero().substring(8,
										cobro.getNumero().length())
								+ " - "
								+ "NRO. "
								+ fp.getChequeNumero()
								+ " - "
								+ "BANCO "
								+ fp.getChequeBanco().getDescripcion()
										.toUpperCase()
								+ " - VTO. "
								+ misc.dateToString(fp.getChequeFecha(),
										Misc.DD_MM_YYYY);
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return los cheques de tercero al dia..
	 */
	public List<BancoChequeTercero> getChequesAlDia() throws Exception {
		List<BancoChequeTercero> out = new ArrayList<BancoChequeTercero>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (ReciboFormaPago fp : this.getChequesTercero()) {
			BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
			if (cheque.isChequeAlDia(this.cierre)) {
				out.add(cheque);
			}
		}
		return out;
	}
	
	/**
	 * @return los cheques de tercero diferidos..
	 */
	public List<BancoChequeTercero> getChequesDiferidos() throws Exception {
		List<BancoChequeTercero> out = new ArrayList<BancoChequeTercero>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (ReciboFormaPago fp : this.getChequesTercero()) {
			BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
			if (!cheque.isChequeAlDia(this.cierre)) {
				out.add(cheque);
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con tarjeta de credito..
	 */
	public List<ReciboFormaPago> getTarjetasCredito() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (fp.isTarjetaCredito()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length()) + " - "
								+ fp.getTarjetaProcesadora().getNombre();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if(fp.isTarjetaCredito()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length()) + " - "
								+ fp.getTarjetaProcesadora().getNombre();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}			
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con tarjeta de debito..
	 */
	public List<ReciboFormaPago> getTarjetasDebito() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if(fp.isTarjetaDebito()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length()) + " - "
								+ fp.getTarjetaProcesadora().getNombre();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}			
		}		
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if(fp.isTarjetaDebito()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length()) + " - "
								+ fp.getTarjetaProcesadora().getNombre();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}			
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con retencion cliente..
	 */
	public List<ReciboFormaPago> getRetencionesCliente(Date fecha) {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if(fp.isRetencion()) {
						String desc = "VTA. "
								+ venta.getNumero() + " - " 
								+ venta.getCliente().getRazonSocial().toUpperCase();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}			
		}		
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if ((!recibo.isAnulado()) && (recibo.isCobro())
					&& (!recibo.isCobroConChequeAdelantado(fecha))) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isRetencion()) {
						String desc = "REC. "
								+ recibo.getNumero() + " - " 
								+ recibo.getCliente().getRazonSocial().toUpperCase();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con retencion cliente diferidos..
	 */
	public List<ReciboFormaPago> getRetencionesClienteDiferidos(Date fecha) {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();		
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if ((!recibo.isAnulado()) && (recibo.isCobro())
					&& (recibo.isCobroConChequeAdelantado(fecha))) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isRetencion()) {
						String desc = "REC. "
								+ recibo.getNumero() + " - " 
								+ recibo.getCliente().getRazonSocial().toUpperCase();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con retencion cliente al dia..
	 */
	public List<ReciboFormaPago> getRetencionesClienteAlDia(Date fecha) {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();		
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if ((!recibo.isAnulado()) && (recibo.isCobro())
					&& (!recibo.isCobroConChequeAdelantado(fecha))) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isRetencion()) {
						String desc = "REC. "
								+ recibo.getNumero() + " - " 
								+ recibo.getCliente().getRazonSocial().toUpperCase();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con retencion proveedor..
	 */
	public List<ReciboFormaPago> getRetencionesProveedor() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Recibo pago : this.getRecibosOrdenado()) {
			if ((pago.isAnulado() == false) && (pago.isCobro() == false)) {
				for (ReciboFormaPago fp : pago.getFormasPago()) {
					if(fp.isRetencion()) {
						String desc = "PAGO "
								+ pago.getNumero();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}			
		}		
		for (Gasto gasto : this.gastos) {
			if (!gasto.isAnulado()) {
				for (ReciboFormaPago fp : gasto.getFormasPago()) {
					if(fp.isRetencion()) {
						String desc = "GASTO "
								+ gasto.getNumeroFactura().substring(8,
										gasto.getNumeroFactura().length());
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}			
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con deposito bancario..
	 */
	public List<ReciboFormaPago> getDepositosBancarios() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (fp.isDepositoBancario()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length())
								+ " - "
								+ fp.getDepositoBancoCta()
										.getBancoDescripcion() + " - "
								+ fp.getDepositoNroReferencia();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado() && !recibo.isReembolsoPrestamo()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isDepositoBancario()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length())
								+ " - "
								+ fp.getDepositoBancoCta()
										.getBancoDescripcion() + " - "
								+ fp.getDepositoNroReferencia();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con deposito bancario ds..
	 */
	public List<ReciboFormaPago> getDepositosBancariosDs() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if ((!fp.isMonedaLocal()) && fp.isDepositoBancario()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length())
								+ " - "
								+ fp.getDepositoBancoCta()
										.getBancoDescripcion() + " - "
								+ fp.getDepositoNroReferencia();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado() && !recibo.isReembolsoPrestamo()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if ((!fp.isMonedaLocal()) && fp.isDepositoBancario()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length())
								+ " - "
								+ fp.getDepositoBancoCta()
										.getBancoDescripcion() + " - "
								+ fp.getDepositoNroReferencia();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con deposito bancario de los reembolsos de prestamo..
	 */
	public List<ReciboFormaPago> getDepositosBancariosReembolsoPrestamos() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado() && recibo.isReembolsoPrestamo()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isDepositoBancario()) {
						String desc = "PRESTAMO "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length())
								+ " - "
								+ fp.getDepositoBancoCta().getBancoDescripcion() + " - "
								+ fp.getDepositoNroReferencia();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con tarjeta de credito..
	 */
	public List<ReciboFormaPago> getVueltosPorVenta() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if ((fp.isEfectivo()) && (fp.getMontoGs() < 0)) {
						String desc = "VUELTO EN EFECTIVO - VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length());
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con recaudacion casa central..
	 */
	public List<ReciboFormaPago> getRecaudacionCentral() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isRecaudacionCentral()) {
						String desc = "COB. " + recibo.getNumero() + " - "
								+ recibo.getCliente().getRazonSocial().toUpperCase();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (fp.isRecaudacionCentral()) {
						String desc = "VTA. " + venta.getNumero() + " - "
								+ venta.getCliente().getRazonSocial().toUpperCase();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con transferencia casa central..
	 */
	public List<ReciboFormaPago> getTransferenciasCentral() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isTransferenciaCentral()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length());
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con saldo a favor del cliente..
	 */
	public List<ReciboFormaPago> getSaldoFavorCliente() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isSaldoFavorGenerado()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length());
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con saldo a favor del cliente cobrado..
	 */
	public List<ReciboFormaPago> getSaldoFavorClienteCobrado() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isSaldoFavorCobrado()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length());
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con moneda extranjera..
	 */
	public List<ReciboFormaPago> getRecaudacionDolares() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (!fp.isMonedaLocal()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8, venta.getNumero().length())
								+ " - "
								+ fp.getDescripcion();
						fp.setDescripcion(desc);
						out.add(fp);						
					}
				}
			}
		}
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado() && !recibo.isReembolsoPrestamo()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (!fp.isMonedaLocal()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length())
								+ " - "
								+ fp.getTipo().getDescripcion().toUpperCase()
								+ " - "
								+ fp.getDescripcion();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con valores representaciones..
	 */
	public List<ReciboFormaPago> getValoresRepresentaciones() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (fp.isValoresRepresentaciones()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length())
								+ " - "
								+ fp.getTipo().getDescripcion();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado() && !recibo.isReembolsoPrestamo()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isValoresRepresentaciones()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length())
								+ " - "
								+ fp.getTipo().getDescripcion();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con diferencias de tipo cambio..
	 */
	public List<ReciboFormaPago> getDiferenciasTipoCambio() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (fp.isDiferenciaTipoCambio()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length())
								+ " - "
								+ fp.getTipo().getDescripcion();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado() && !recibo.isReembolsoPrestamo()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isDiferenciaTipoCambio()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length())
								+ " - "
								+ fp.getTipo().getDescripcion();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return las formas de pago con otros comprobantes..
	 */
	public List<ReciboFormaPago> getOtrosComprobantes() {
		List<ReciboFormaPago> out = new ArrayList<ReciboFormaPago>();
		for (Venta venta : this.getVentasOrdenado()) {
			if (!venta.isAnulado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (fp.isOtrosComprobantes()) {
						String desc = "VTA. "
								+ venta.getNumero().substring(8,
										venta.getNumero().length())
								+ " - "
								+ fp.getTipo().getDescripcion() + " - " + fp.getTarjetaNumeroComprobante();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		for (Recibo recibo : this.getRecibosOrdenado()) {
			if (!recibo.isAnulado() && !recibo.isReembolsoPrestamo()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isOtrosComprobantes()) {
						String desc = "COB. "
								+ recibo.getNumero().substring(8,
										recibo.getNumero().length())
								+ " - "
								+ fp.getTipo().getDescripcion() + " - " + fp.getTarjetaNumeroComprobante();
						fp.setDescripcion(desc);
						out.add(fp);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * @return los reembolsos de cheques rechazados..
	 */
	public List<Recibo> getReembolsosChequesRechazados() {
		List<Recibo> out = new ArrayList<Recibo>();
		for (Recibo recibo : this.recibos) {
			if (recibo.isCancelacionCheque()) {
				out.add(recibo);
			}
		}
		return out;
	}
	
	/**
	 * @return las ventas ordenadas..
	 */
	public List<Venta> getVentasOrdenado() {
		List<Venta> out = new ArrayList<Venta>();
		out.addAll(this.ventas);
		Collections.sort(out, new Comparator<Venta>() {
			@Override
			public int compare(Venta o1, Venta o2) {
				return o1.getNumero().compareTo(o2.getNumero());
			}
		});
		return out;

	}
	
	/**
	 * @return los recibos ordenados..
	 */
	public List<Recibo> getRecibosOrdenado() {
		List<Recibo> out = new ArrayList<Recibo>();
		out.addAll(this.recibos);
		Collections.sort(out, new Comparator<Recibo>() {
			@Override
			public int compare(Recibo o1, Recibo o2) {
				return o1.getNumero().compareTo(o2.getNumero());
			}
		});
		return out;

	}
	
	/**
	 * @return los gastos ordenados..
	 */
	public List<Gasto> getGastosOrdenado() {
		List<Gasto> out = new ArrayList<Gasto>();
		out.addAll(this.gastos);
		Collections.sort(out, new Comparator<Gasto>() {
			@Override
			public int compare(Gasto o1, Gasto o2) {
				Date fecha1 = o1.getFecha();
				Date fecha2 = o2.getFecha();
				return fecha1.compareTo(fecha2);
			}
		});
		return out;
	}
	
	/**
	 * @return saldo de caja..
	 */
	public double getSaldoCaja() throws Exception {
		CajaPeriodo planilla = this;

		double totalReposiciones = 0;
		double totalVentaContadoEfectivo = 0;
		double totalCobranzaEfectivo = 0;
		double totalAnticipoEfectivo = 0;
		double totalCancelacionChequeEfectivo = 0;

		double totalNotaCreditoContado = 0;
		double totalGastosEfectivo = 0;
		double totalPagosEfectivo = 0;

		// reposiciones..
		for (CajaReposicion rep : planilla.getReposiciones()) {
			if (rep.isIngreso()) {
				if (rep.getFormaPago().isEfectivo()) {
					double importe = rep.isAnulado() ? 0.0 : rep.getMontoGs();
					totalReposiciones += importe;
				}
			}
		}

		// ventas contado..
		for (Venta venta : planilla.getVentasOrdenado()) {
			if (venta.isVentaContado()) {
				for (ReciboFormaPago fp : venta.getFormasPago()) {
					if (fp.isEfectivo() && fp.isMonedaLocal()) {
						double montoEf = venta.isAnulado() ? 0.0 : fp.getMontoGs();
						totalVentaContadoEfectivo += montoEf;
					}
				}
			}
		}

		// cobranzas con efectivo..
		for (Recibo cobro : planilla.getRecibosOrdenado()) {
			if (cobro.isCobro() && !cobro.isAnulado() && !cobro.isCobroExterno() && !cobro.isCancelacionCheque()
					&& !cobro.isCobroAnticipo()) {
				Object[] aldia = cobro.getCobranzasConEfectivo();
				if (aldia != null) {
					totalCobranzaEfectivo += (double) aldia[1];
				}
			}
		}

		// anticipos con efectivo..
		for (Recibo cobro : planilla.getRecibosOrdenado()) {
			if (cobro.isCobroAnticipo() && !cobro.isAnulado() && !cobro.isCobroExterno()
					&& !cobro.isCancelacionCheque()) {
				Object[] efectivo = cobro.getCobranzasConEfectivo();
				if (efectivo != null) {
					totalAnticipoEfectivo += (double) efectivo[1];
				}
			}
		}

		// reembolso de cheques rechazados..
		for (Recibo cobro : planilla.getRecibosOrdenado()) {
			if (cobro.isCancelacionCheque() && !cobro.isCancelacionChequeRechazadoInterno() && !cobro.isAnulado()
					&& !cobro.isCobroExterno()) {
				for (ReciboFormaPago rfp : cobro.getFormasPago()) {
					if (rfp.isEfectivo()) {
						totalCancelacionChequeEfectivo += rfp.getMontoGs();
					}
				}
			}
		}

		// reembolso de cheques rechazados internos..
		for (Recibo cobro : planilla.getRecibosOrdenado()) {
			if (cobro.isCancelacionCheque() && cobro.isCancelacionChequeRechazadoInterno() && !cobro.isAnulado()
					&& !cobro.isCobroExterno()) {
				for (ReciboFormaPago rfp : cobro.getFormasPago()) {
					if (rfp.isEfectivo()) {
						totalCancelacionChequeEfectivo += rfp.getMontoGs();
					}
				}
			}
		}

		// notas de credito contra contado..
		for (NotaCredito nc : planilla.getNotasCredito()) {
			if (nc.isNotaCreditoVenta()) {
				double importe = nc.isAnulado() ? 0.0 : nc.getImporteGs();
				if (nc.isNotaCreditoVentaContado()) {
					totalNotaCreditoContado += importe;
				}
			}
		}

		// gastos en efectivo..
		for (Gasto gasto : planilla.getGastosOrdenado()) {
			double importe = gasto.isAnulado() ? 0.0 : gasto.getTotalEfectivoGs();
			totalGastosEfectivo += importe;
		}

		// pagos con efectivo..
		for (Recibo pago : planilla.getRecibosOrdenado()) {
			if (pago.isPago()) {
				Object[] efectivo = pago.getPagosConEfectivo();
				if (efectivo != null) {
					totalPagosEfectivo += (double) efectivo[1];
				}
			}
		}

		double ingresos = totalReposiciones + totalVentaContadoEfectivo + totalCobranzaEfectivo + totalAnticipoEfectivo
				+ totalCancelacionChequeEfectivo;
		double egresos = totalNotaCreditoContado + totalGastosEfectivo + totalPagosEfectivo;
		
		return ingresos - egresos;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getApertura() {
		return apertura;
	}

	public void setApertura(Date apertura) {
		this.apertura = apertura;
	}

	public Date getCierre() {
		return cierre;
	}

	public void setCierre(Date cierre) {
		this.cierre = cierre;
	}

	public Funcionario getVerificador() {
		return verificador;
	}

	public void setVerificador(Funcionario verificador) {
		this.verificador = verificador;
	}

	public Funcionario getResponsable() {
		return responsable;
	}

	public void setResponsable(Funcionario responsable) {
		this.responsable = responsable;
	}

	public Set<Recibo> getRecibos() {
		return recibos;
	}

	public void setRecibos(Set<Recibo> pagos) {
		this.recibos = pagos;
	}

	public Set<Venta> getVentas() {
		return ventas;
	}

	public void setVentas(Set<Venta> ventas) {
		this.ventas = ventas;
	}

	public Set<CajaReposicion> getReposiciones() {
		return reposiciones;
	}

	public void setReposiciones(Set<CajaReposicion> reposiciones) {
		this.reposiciones = reposiciones;
	}

	public Set<NotaCredito> getNotasCredito() {
		return notasCredito;
	}

	public void setNotasCredito(Set<NotaCredito> notasCredito) {
		this.notasCredito = notasCredito;
	}

	public Caja getCaja() {
		return caja;
	}

	public void setCaja(Caja caja) {
		this.caja = caja;
	}

	public Set<Gasto> getGastos() {
		return gastos;
	}

	public void setGastos(Set<Gasto> gastos) {
		this.gastos = gastos;
	}

	public Tipo getEstado() {
		return estado;
	}

	public void setEstado(Tipo estado) {
		this.estado = estado;
	}

	public CajaPeriodoArqueo getArqueo() {
		return arqueo;
	}

	public void setArqueo(CajaPeriodoArqueo arqueo) {
		this.arqueo = arqueo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Set<CompraLocalFactura> getCompras() {
		return compras;
	}

	public void setCompras(Set<CompraLocalFactura> compras) {
		this.compras = compras;
	}

	public boolean isControlRendicion() {
		return controlRendicion;
	}

	public void setControlRendicion(boolean controlRendicion) {
		this.controlRendicion = controlRendicion;
	}

	public double getSaldoCajaChica() {
		return saldoCajaChica;
	}

	public void setSaldoCajaChica(double saldoCajaChica) {
		this.saldoCajaChica = saldoCajaChica;
	}

	public String getSaldoCajaChicaAplicado() {
		return saldoCajaChicaAplicado;
	}

	public void setSaldoCajaChicaAplicado(String saldoCajaChicaAplicado) {
		this.saldoCajaChicaAplicado = saldoCajaChicaAplicado;
	}
}
