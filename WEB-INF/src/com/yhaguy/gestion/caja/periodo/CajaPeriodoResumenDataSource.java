package com.yhaguy.gestion.caja.periodo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.coreweb.util.MyArray;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaReposicion;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

@SuppressWarnings("unchecked")
public class CajaPeriodoResumenDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");		
	List<MyArray> values = new ArrayList<MyArray>();

	double totalIngresos = 0;
	double totalEgresos = 0;

	double totalEfectivo = 0;
	double totalChequeTerceroAlDia = 0;
	double totalChequeTerceroAdelantado = 0;
	double totalTarjetaCredito = 0;
	double totalTarjetaDebito = 0;
	double totalRetencionCliente = 0;
	double totalRetencionClienteDiferido = 0;
	double totalRetencionProveedor = 0;
	double totalDepositoBancario = 0;
	double totalRecaudacionCentral = 0;
	double totalTransferenciaCentral = 0;

	double totalVentaContado = 0;
	double totalVentaContadoCheque = 0;
	double totalVentaContadoEfectivo = 0;
	double totalVentaCredito = 0;
	double totalCobranza = 0;
	double totalCobranzaAlDia = 0;
	double totalCobranzaChequeAlDia = 0;
	double totalCobranzaEfectivo = 0;
	double totalCobranzaTarjetaCredito = 0;
	double totalCobranzaTarjetaDebito = 0;
	double totalCobranzaDepositoBancario = 0;
	double totalCobranzaAlDiaOtraSuc = 0;
	double totalCobranzaAdelantado = 0;
	double totalCobranzaAdelantadoOtraSuc = 0;
	double totalCobroEfectivoCheque = 0;
	double totalReposiciones = 0;
	double totalPagos = 0;
	double totalGastos = 0;
	double totalRepEgresos = 0;
	double totalNotaCreditoContado = 0;
	double totalNotaCreditoCredito = 0;
	double totalNotaCreditoCompra = 0;
	double totalSaldoFavorClienteGenerado = 0;
	double totalSaldoFavorClienteCobrado = 0;
	double totalCancelacionCheque = 0;
	double totalCancelacionChequeEfectivo = 0;
	double totalCancelacionChequeAldia = 0;

	public CajaPeriodoResumenDataSource(CajaPeriodo planilla) {
		try {
			Date fechaPlanilla = planilla.getApertura();
			
			// ventas contado..
			for (Venta venta : planilla.getVentasOrdenado()) {
				if (venta.isVentaContado()) {
					double importe = venta.isAnulado() ? 0.0 : venta.getTotalImporteGs();
					this.totalIngresos += importe;
					this.totalVentaContado += importe;
					MyArray my = new MyArray(
							venta.getDescripcionTipoMovimiento(),
							venta.getNumero() + " - " + venta.getCliente().getRazonSocial().toUpperCase(), importe, "VENTAS CONTADO",
							this.totalVentaContado);
					this.values.add(my);
					for (ReciboFormaPago fp : venta.getFormasPago()) {
						if (fp.isEfectivo()) {
							double montoEf = venta.isAnulado() ? 0.0 : fp.getMontoGs();
							this.totalVentaContadoEfectivo += montoEf;
						} else if (fp.isChequeTercero()) {
							double montoCh = venta.isAnulado() ? 0.0 : fp.getMontoChequeGs();
							this.totalVentaContadoCheque += montoCh;
						}
					}
				}
			}
			
			// ventas contado efectivo..
			for (Venta venta : planilla.getVentasOrdenado()) {
				if (venta.isVentaContado() && !venta.isAnulado()) {
					for (ReciboFormaPago fp : venta.getFormasPago()) {
						if (fp.isEfectivo()) {
							MyArray my = new MyArray(
									venta.getDescripcionTipoMovimiento(),
									venta.getNumero() + " - " + venta.getCliente().getRazonSocial().toUpperCase(), fp.getMontoGs(), "VENTAS CONTADO CON EFECTIVO",
									this.totalVentaContadoEfectivo);
							this.values.add(my);
						}
					}
				}
			}
			
			// ventas contado cheque..
			for (Venta venta : planilla.getVentasOrdenado()) {
				if (venta.isVentaContado() && !venta.isAnulado()) {
					for (ReciboFormaPago fp : venta.getFormasPago()) {
						if (fp.isChequeTercero()) {
							MyArray my = new MyArray(
									venta.getDescripcionTipoMovimiento(),
									venta.getNumero() + " - " + 
									fp.getChequeBanco().getDescripcion().toUpperCase() + " - " + fp.getChequeNumero(), 
									fp.getMontoGs(), "VENTAS CONTADO CON CHEQUE",
									this.totalVentaContadoCheque);
							this.values.add(my);
						}
					}
				}
			}

			// ventas credito..
			for (Venta venta : planilla.getVentasOrdenado()) {
				if (!venta.isVentaContado()) {
					double importe = venta.isAnulado() ? 0.0 : venta.getTotalImporteGs();
					this.totalVentaCredito += importe;
					MyArray my = new MyArray(
							venta.getDescripcionTipoMovimiento(),
							venta.getNumero() + " - " + venta.getCliente().getRazonSocial().toUpperCase(), 
							importe, "VENTAS CREDITO",
							this.totalVentaCredito);
					this.values.add(my);
				}
			}
			
			// cobranzas
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado() && !cobro.isCancelacionCheque()) {
					this.totalCobranza += cobro.getTotalImporteGs();
					String desc = "REC. " + cobro.getNumero_() + " - (" + cobro.getCobrador() + ")";
					MyArray my = new MyArray(cobro.getTipoMovimiento().getDescripcion(), 
							desc + " - " + Utiles.getMaxLength(cobro.getCliente().getRazonSocial(), 43), 
							cobro.getTotalImporteGs(),
							"COBRANZAS",
							this.totalCobranza);
					this.values.add(my);				
				}
			}

			// cobranzas (al dia)..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado()
						&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
					Object[] aldia = cobro.getCobranzaAlDia(fechaPlanilla);
					if (aldia != null) {
						//List<Object[]> items = (List<Object[]>) aldia[0];
						this.totalCobranzaAlDia += (double) aldia[1];
						/*for (Object[] item : items) {
							MyArray my = new MyArray(
									cobro.getTipoMovimiento().getDescripcion(),
									item[0].toString().toUpperCase(),
									item[1],
									"COBRANZAS CON EFECTIVO / CHEQUE AL DÍA",
									this.totalCobranzaAlDia);
							//this.values.add(my);
						}*/
					}
				}
			}
			
			// cobranzas con efectivo..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado()
						&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
					Object[] aldia = cobro.getCobranzasConEfectivo();
					if (aldia != null) {
						List<Object[]> items = (List<Object[]>) aldia[0];
						this.totalCobranzaEfectivo += (double) aldia[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro.getTipoMovimiento()
									.getDescripcion(), item[0].toString()
									.toUpperCase() + " - " 
									+ Utiles.getMaxLength(cobro.getCliente().getRazonSocial().toUpperCase(), 45), item[1],
									"COBRANZAS CON EFECTIVO",
									this.totalCobranzaEfectivo);
							this.values.add(my);
						}
					}
				}
			}
			
			// cobranzas con tarjeta de credito..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado()
						&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
					Object[] aldia = cobro.getCobranzasConTarjetaCredito();
					if (aldia != null) {
						List<Object[]> items = (List<Object[]>) aldia[0];
						this.totalCobranzaTarjetaCredito += (double) aldia[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro.getTipoMovimiento()
									.getDescripcion(), item[0].toString()
									.toUpperCase() + " - " 
									+ Utiles.getMaxLength(cobro.getCliente().getRazonSocial().toUpperCase(), 30), item[1],
									"COBRANZAS CON TARJETA DE CREDITO",
									this.totalCobranzaTarjetaCredito);
							this.values.add(my);
						}
					}
				}
			}
			
			// cobranzas con tarjeta de debito..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado()
						&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
					Object[] aldia = cobro.getCobranzasConTarjetaDebito();
					if (aldia != null) {
						List<Object[]> items = (List<Object[]>) aldia[0];
						this.totalCobranzaTarjetaDebito += (double) aldia[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro.getTipoMovimiento()
									.getDescripcion(), item[0].toString().toUpperCase() + " - " 
									+ Utiles.getMaxLength(cobro.getCliente().getRazonSocial().toUpperCase(), 30), item[1],
									"COBRANZAS CON TARJETA DE DEBITO",
									this.totalCobranzaTarjetaDebito);
							this.values.add(my);
						}
					}
				}
			}
			
			// cobranzas con cheque (al dia)..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado()
						&& !cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
					Object[] aldia = cobro.getCobranzasChequeAlDia(fechaPlanilla);
					if (aldia != null) {
						List<Object[]> items = (List<Object[]>) aldia[0];
						this.totalCobranzaChequeAlDia += (double) aldia[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro.getTipoMovimiento()
									.getDescripcion(), item[0].toString().toUpperCase() + " - " 
									+ Utiles.getMaxLength(cobro.getCliente().getRazonSocial().toUpperCase(), 29), item[1],
									"COBRANZAS CON CHEQUE AL DÍA",
									this.totalCobranzaChequeAlDia);
							this.values.add(my);
						}
					}
				}
			}

			
			// cobranzas de otra suc. (al dia)..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado()
						&& cobro.isCobroExterno() && !cobro.isCancelacionCheque()) {
					Object[] aldia = cobro.getCobranzaAlDia(fechaPlanilla);
					if (aldia != null) {
						List<Object[]> items = (List<Object[]>) aldia[0];
						this.totalCobranzaAlDiaOtraSuc += (double) aldia[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(
									cobro.getTipoMovimiento()
											.getDescripcion(),
									item[0].toString().toUpperCase() + " - " + cobro.getCliente().getRazonSocial().toUpperCase(),
									item[1],
									"COBRANZAS CON EFECTIVO / CHEQUE AL DÍA (OTRAS SUC.)",
									this.totalCobranzaAlDiaOtraSuc);
							this.values.add(my);
						}
					}
				}
			}
			
			// cobranzas (adelantado)..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado() && !cobro.isCobroExterno()
						&& !cobro.isCancelacionCheque()) {
					Object[] adel = cobro.getCobranzaChequeAdelantado(fechaPlanilla);
					if (adel != null) {
						List<Object[]> items = (List<Object[]>) adel[0];
						this.totalCobranzaAdelantado += (double) adel[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro
									.getTipoMovimiento().getDescripcion(),
									item[0].toString().toUpperCase() + " - " 
									+ Utiles.getMaxLength(cobro.getCliente().getRazonSocial().toUpperCase(), 29), item[1],
									"COBRANZAS CON CHEQUE ADELANTADO",
									this.totalCobranzaAdelantado);
							this.values.add(my);
						}
					}
				}		
			}
			
			// cobranzas de otra suc. (adelantado)..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado() && cobro.isCobroExterno()) {
					Object[] adel = cobro.getCobranzaChequeAdelantado(fechaPlanilla);
					if (adel != null) {
						List<Object[]> items = (List<Object[]>) adel[0];
						this.totalCobranzaAdelantadoOtraSuc += (double) adel[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro
									.getTipoMovimiento().getDescripcion(),
									item[0].toString().toUpperCase() + " - " + cobro.getCliente().getRazonSocial().toUpperCase(), item[1],
									"COBRANZAS CON CHEQUE ADELANTADO (OTRAS SUC.)",
									this.totalCobranzaAdelantadoOtraSuc);
							this.values.add(my);
						}
					}
				}		
			}
			
			// cobranzas con deposito bancario..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCobro() && !cobro.isAnulado()
						&& !cobro.isCobroExterno()
						&& !cobro.isCancelacionCheque()) {
					Object[] deps = cobro.getCobranzasConDepositoBancario();
					if (deps != null) {
						List<Object[]> items = (List<Object[]>) deps[0];
						this.totalCobranzaDepositoBancario += (double) deps[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro.getTipoMovimiento()
									.getDescripcion(), item[0].toString()
									.toUpperCase()
									+ " - "
									+ cobro.getCliente().getRazonSocial()
											.toUpperCase(), item[1],
									"COBRANZAS CON DEPÓSITO BANCARIO",
									this.totalCobranzaDepositoBancario);
							this.values.add(my);
						}
					}
				}
			}
			
			// aplicaciones de cta cte saldo a favor generado..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if ((cobro.isCobro() && !cobro.isAnulado() && !cobro.isCobroExterno()) || cobro.isCancelacionCheque()) {
					Object[] adel = cobro.getCobranzasConCtaCteSaldoFavorGenerado();
					if (adel != null) {
						List<Object[]> items = (List<Object[]>) adel[0];
						this.totalSaldoFavorClienteGenerado += (double) adel[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro
									.getTipoMovimiento().getDescripcion(),
									item[0].toString().toUpperCase(), item[1],
									"SALDO A FAVOR DEL CLIENTE (GENERADO)",
									this.totalSaldoFavorClienteGenerado);
							this.values.add(my);
						}
					}
				}		
			}
			
			// aplicaciones de cta cte saldo a favor cobrado..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if ((cobro.isCobro() && !cobro.isAnulado() && !cobro.isCobroExterno()) || cobro.isCancelacionCheque()) {
					Object[] adel = cobro.getCobranzasConCtaCteSaldoFavorCobrado();
					if (adel != null) {
						List<Object[]> items = (List<Object[]>) adel[0];
						this.totalSaldoFavorClienteCobrado += (double) adel[1];
						for (Object[] item : items) {
							MyArray my = new MyArray(cobro.getTipoMovimiento()
									.getDescripcion(), item[0].toString().toUpperCase(), item[1],
									"SALDO A FAVOR DEL CLIENTE (COBRADO)",
									this.totalSaldoFavorClienteCobrado);
							this.values.add(my);
						}
					}
				}
			}
			
			// reembolso de cheques rechazados..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCancelacionCheque() && !cobro.isCancelacionChequeRechazadoInterno() && !cobro.isAnulado()
						&& !cobro.isCobroExterno()) {
					for (ReciboFormaPago rfp : cobro.getFormasPago()) {
						if (rfp.isEfectivo()) {
							this.totalCancelacionChequeEfectivo += rfp.getMontoGs();
							MyArray my = new MyArray(cobro.getTipoMovimiento().getDescripcion(), 
									rfp.getDescripcion().toUpperCase() + " - CLIENTE: " + cobro.getCliente().getRazonSocial(),
									rfp.getMontoGs(),
									"REEMBOLSO CHEQUES RECHAZADOS CON EFECTIVO", this.totalCancelacionChequeEfectivo);
							this.values.add(my);
						}						
					}
				}
			}
			
			// reembolso de cheques rechazados..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCancelacionCheque() && !cobro.isCancelacionChequeRechazadoInterno() && !cobro.isAnulado()
						&& !cobro.isCobroExterno()) {
					for (ReciboFormaPago rfp : cobro.getFormasPago()) {
						if (rfp.isChequeTercero() && rfp.isChequeAlDia(fechaPlanilla)) {
							this.totalCancelacionChequeAldia += rfp.getMontoGs();
							MyArray my = new MyArray(cobro.getTipoMovimiento().getDescripcion(), 
									rfp.getDescripcion().toUpperCase() + " - CLIENTE: " + cobro.getCliente().getRazonSocial(), 
									rfp.getMontoGs(),
									"REEMBOLSO CHEQUES RECHAZADOS CON CHEQUE AL DÍA", this.totalCancelacionChequeAldia);
							this.values.add(my);
						}						
					}
				}
			}
			
			// reembolso de cheques rechazados..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCancelacionCheque() && !cobro.isCancelacionChequeRechazadoInterno() && !cobro.isAnulado()
						&& !cobro.isCobroExterno()) {
					for (ReciboFormaPago rfp : cobro.getFormasPago()) {
						if ((!(rfp.isChequeTercero() && rfp.isChequeAlDia(fechaPlanilla)))
								&& (!rfp.isEfectivo())) {
							this.totalCancelacionCheque += rfp.getMontoGs();
							MyArray my = new MyArray(cobro.getTipoMovimiento()
									.getDescripcion(), rfp.getDescripcion()
									.toUpperCase(), rfp.getMontoGs(),
									"REEMBOLSO DE CHEQUES RECHAZADOS", this.totalCancelacionCheque);
							this.values.add(my);
						}
					}
				}
			}
			
			// reembolso de cheques rechazados internos..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCancelacionCheque() && cobro.isCancelacionChequeRechazadoInterno() && !cobro.isCancelacionChequeRechazadoInterno() && !cobro.isAnulado()
						&& !cobro.isCobroExterno()) {
					for (ReciboFormaPago rfp : cobro.getFormasPago()) {
						if (rfp.isEfectivo()) {
							this.totalCancelacionChequeEfectivo += rfp.getMontoGs();
							MyArray my = new MyArray(cobro.getTipoMovimiento().getDescripcion(),
									rfp.getDescripcion().toUpperCase() + " - CLIENTE: "
											+ cobro.getCliente().getRazonSocial(),
									rfp.getMontoGs(), "REEMBOLSO CHEQUES RECHAZADOS CON EFECTIVO (INTERNOS)",
									this.totalCancelacionChequeEfectivo);
							this.values.add(my);
						}
					}
				}
			}
			
			// reembolso de cheques rechazados internos..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCancelacionCheque() && cobro.isCancelacionChequeRechazadoInterno() && !cobro.isAnulado() && !cobro.isCobroExterno()) {
					for (ReciboFormaPago rfp : cobro.getFormasPago()) {
						if (rfp.isChequeTercero() && rfp.isChequeAlDia(fechaPlanilla)) {
							this.totalCancelacionChequeAldia += rfp.getMontoGs();
							MyArray my = new MyArray(cobro.getTipoMovimiento().getDescripcion(),
									rfp.getDescripcion().toUpperCase() + " - CLIENTE: "
											+ cobro.getCliente().getRazonSocial(),
									rfp.getMontoGs(), "REEMBOLSO CHEQUES RECHAZADOS CON CHEQUE AL DÍA (INTERNOS)",
									this.totalCancelacionChequeAldia);
							this.values.add(my);
						}
					}
				}
			}
			
			// reembolso de cheques rechazados internos..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isCancelacionCheque() && cobro.isCancelacionChequeRechazadoInterno() && !cobro.isAnulado() && !cobro.isCobroExterno()) {
					for (ReciboFormaPago rfp : cobro.getFormasPago()) {
						if ((!(rfp.isChequeTercero() && rfp.isChequeAlDia(fechaPlanilla))) && (!rfp.isEfectivo())) {
							this.totalCancelacionCheque += rfp.getMontoGs();
							MyArray my = new MyArray(cobro.getTipoMovimiento().getDescripcion(),
									rfp.getDescripcion().toUpperCase(), rfp.getMontoGs(),
									"REEMBOLSO DE CHEQUES RECHAZADOS (INTERNOS)", this.totalCancelacionCheque);
							this.values.add(my);
						}
					}
				}
			}
			
			// reembolso de prestamos..
			for (Recibo cobro : planilla.getRecibosOrdenado()) {
				if (cobro.isReembolsoPrestamo() && !cobro.isAnulado()
						&& !cobro.isCobroExterno()) {
					for (ReciboFormaPago rfp : cobro.getFormasPago()) {
						this.totalCancelacionCheque += rfp.getMontoGs();
						MyArray my = new MyArray(cobro.getTipoMovimiento()
								.getDescripcion(), rfp.getDescripcion().toUpperCase(),
								rfp.getMontoGs(),
								"REEMBOLSO DE PRESTAMOS",
								this.totalCancelacionCheque);
						this.values.add(my);
					}
				}
			}

			// reposiciones..
			for (CajaReposicion rep : planilla.getReposiciones()) {
				if (rep.isIngreso()) {
					double importe = rep.isAnulado() ? 0.0 : rep.getMontoGs();
					this.totalIngresos += importe;
					this.totalReposiciones += importe;
					MyArray my = new MyArray(
							"REPOSICION DE CAJA",
							(rep.getNumero() + " - " + rep.getFormaPago().getDescripcion().toUpperCase()),
							importe, "REPOSICIONES DE CAJA",
							this.totalReposiciones);
					this.values.add(my);
				}
			}

			// pagos..
			for (Recibo pago : planilla.getRecibosOrdenado()) {
				if (pago.isPago()) {
					double importe = pago.isAnulado() ? 0.0 : pago
							.getTotalImporteGs();
					this.totalEgresos += importe;
					this.totalPagos += importe;
					MyArray my = new MyArray(pago.getTipoMovimiento().getDescripcion(),
							pago.getNumero() + " - " + pago.getProveedor().getRazonSocial().toUpperCase(), importe,
							"PAGOS A PROVEEDORES", this.totalPagos);
					this.values.add(my);
				}
			}

			// gastos..
			for (Gasto gasto : planilla.getGastosOrdenado()) {
				double importe = gasto.isAnulado() ? 0.0 : gasto.getImporteGs();
				this.totalEgresos += importe;
				this.totalGastos += importe;
				MyArray my = new MyArray(gasto.getTipoMovimiento()
						.getDescripcion(), gasto.getNumeroFactura() + " - " + gasto.getDescripcionCuenta(), importe,
						"GASTOS DE CAJA CHICA", this.totalGastos);
				this.values.add(my);
			}

			// egresos..
			for (CajaReposicion rep : planilla.getReposiciones()) {
				if (!rep.isIngreso()) {
					double importe = rep.isAnulado() ? 0.0 : rep.getMontoGs();
					this.totalEgresos += rep.getMontoGs();
					this.totalRepEgresos += rep.getMontoGs();
					MyArray my = new MyArray(rep.getTipoEgreso()
							.getDescripcion(),
							(rep.getNumero() + " - " + rep.getResponsable()	+ " - " + rep.getObservacion()),
							importe, "EGRESOS VARIOS", this.totalRepEgresos);
					this.values.add(my);
				}
			}
			
			// egresos por vueltos (diferencia con cheque)..
			for (ReciboFormaPago fp : planilla.getVueltosPorVenta()) {
				double importe = (fp.getMontoGs() * -1);
				this.totalEgresos += (fp.getMontoGs() * -1);
				this.totalRepEgresos += (fp.getMontoGs() * -1);
				MyArray my = new MyArray("EGRESO EN EFECTIVO",
						fp.getDescripcion(), importe, "EGRESOS VARIOS",
						this.totalRepEgresos);
				this.values.add(my);
			}

			// notas de credito contra contado..
			for (NotaCredito nc : planilla.getNotasCredito()) {
				if (nc.isNotaCreditoVenta()) {
					double importe = nc.isAnulado() ? 0.0 : nc.getImporteGs();
					this.totalEgresos += importe;
					if (nc.isNotaCreditoVentaContado()) {
						this.totalNotaCreditoContado += importe;
						MyArray my = new MyArray(nc.getTipoMovimiento()
								.getDescripcion(), nc.getNumero() + " - " 
							  + nc.getCliente().getRazonSocial().toUpperCase(), importe,
								"NOTAS DE CRÉDITO - (CONTADO)",
								this.totalNotaCreditoContado);
						this.values.add(my);
					}
				
				}
			}

			// notas de credito contra credito..
			for (NotaCredito nc : planilla.getNotasCredito()) {
				if (nc.isNotaCreditoVenta()) {
					double importe = nc.isAnulado() ? 0.0 : nc.getImporteGs();
					this.totalEgresos += importe;
					if (nc.isNotaCreditoVentaContado() == false) {
						this.totalNotaCreditoCredito += importe;
						MyArray my = new MyArray(nc.getTipoMovimiento()
								.getDescripcion(), nc.getNumero() + " - ("
								+ nc.getNumeroFacturaAplicada() + ")", importe,
								"NOTAS DE CRÉDITO - (CRÉDITO)",
								this.totalNotaCreditoCredito);
						this.values.add(my);
					}				
				}
			}
			
			// notas de credito compra..
			for (NotaCredito nc : planilla.getNotasCredito()) {
				if (nc.isNotaCreditoCompra()) {
					double importe = nc.isAnulado() ? 0.0 : nc.getImporteGs();
					this.totalIngresos += importe;
					this.totalNotaCreditoCompra += importe;
					MyArray my = new MyArray(nc.getTipoMovimiento()
							.getDescripcion(), nc.getNumero() + " - "
							+ nc.getProveedor().getRazonSocial().toUpperCase(),
							importe, "NOTAS DE CRÉDITO - COMPRA",
							this.totalNotaCreditoCompra);
					this.values.add(my);
				
				}				
			}

			// cheques de tercero al dia..
			// unifica los montos de un mismo cheque..
			Map<String, Double> montoCheque_ = new HashMap<String, Double>();
			for (ReciboFormaPago fp : planilla.getChequesTercero()) {
				if (fp.isChequeAlDia(fechaPlanilla)) {
					this.totalChequeTerceroAlDia += fp.getMontoChequeGs();

					if (montoCheque_.get(fp.getChequeNumero()) == null) {
						montoCheque_.put(fp.getChequeNumero(), fp.getMontoChequeGs());
					} else {
						montoCheque_.put(fp.getChequeNumero(), 
								(fp.getMontoChequeGs() + montoCheque_.get(fp.getChequeNumero())));
					}
				}
			}

			// cheques de tercero adelantados..
			// unifica los montos de un mismo cheque..
			Map<String, Double> montoCheque = new HashMap<String, Double>();
			for (ReciboFormaPago fp : planilla.getChequesTercero()) {
				if (fp.isChequeAlDia(fechaPlanilla) == false) {
					this.totalChequeTerceroAdelantado += fp.getMontoChequeGs();						
					
					if (montoCheque.get(fp.getChequeNumero()) == null) {
						montoCheque.put(fp.getChequeNumero(),
								fp.getMontoChequeGs());
					} else {
						montoCheque.put(fp.getChequeNumero(), (fp
								.getMontoChequeGs() + montoCheque.get(fp
								.getChequeNumero())));
					}
				}
			}

			// tarjetas de credito..
			for (ReciboFormaPago fp : planilla.getTarjetasCredito()) {
				this.totalTarjetaCredito += fp.getMontoGs();
				MyArray my = new MyArray(fp.getTipo().getDescripcion(),
						fp.getDescripcion(), fp.getMontoGs(),
						"TARJETAS DE CREDITO", this.totalTarjetaCredito);
				this.values.add(my);
			}

			// tarjetas de debito..
			for (ReciboFormaPago fp : planilla.getTarjetasDebito()) {
				this.totalTarjetaDebito += fp.getMontoGs();
				MyArray my = new MyArray(fp.getTipo().getDescripcion(),
						fp.getDescripcion(), fp.getMontoGs(),
						"TARJETAS DE DEBITO", this.totalTarjetaDebito);
				this.values.add(my);
			}

			// retenciones de cliente al dia..
			for (ReciboFormaPago fp : planilla.getRetencionesCliente(fechaPlanilla)) {
				this.totalRetencionCliente += fp.getMontoGs();
				MyArray my = new MyArray(fp.getTipo().getDescripcion(),
						fp.getDescripcion(), fp.getMontoGs(),
						"RETENCIONES DE CLIENTE - AL DÍA",
						this.totalRetencionCliente);
				this.values.add(my);
			}
			
			// retenciones de cliente diferidos..
			for (ReciboFormaPago fp : planilla.getRetencionesClienteDiferidos(fechaPlanilla)) {
				this.totalRetencionClienteDiferido += fp.getMontoGs();
				MyArray my = new MyArray(fp.getTipo().getDescripcion(),
						fp.getDescripcion(), fp.getMontoGs(),
						"RETENCIONES DE CLIENTE - DIFERIDOS",
						this.totalRetencionClienteDiferido);
				this.values.add(my);
			}

			// retenciones de proveedor..
			for (ReciboFormaPago fp : planilla.getRetencionesProveedor()) {
				this.totalRetencionProveedor += fp.getMontoGs();
				MyArray my = new MyArray(fp.getTipo().getDescripcion(),
						fp.getDescripcion(), fp.getMontoGs(),
						"RETENCIONES DE PROVEEDOR",
						this.totalRetencionProveedor);
				this.values.add(my);
			}
			
			// depositos bancarios..
			for (ReciboFormaPago fp : planilla.getDepositosBancarios()) {
				this.totalDepositoBancario += fp.getMontoGs();
				MyArray my = new MyArray(fp.getTipo().getDescripcion(),
						fp.getDescripcion(), fp.getMontoGs(),
						"DEPÓSITOS BANCARIOS", this.totalDepositoBancario);
				this.values.add(my);
			}
			
			// recaudacion central..
			for (ReciboFormaPago fp : planilla.getRecaudacionCentral()) {
				this.totalRecaudacionCentral += fp.getMontoGs();
				MyArray my = new MyArray(fp.getTipo().getDescripcion(),
						fp.getDescripcion(), fp.getMontoGs(),
						"RECAUDACION CASA CENTRAL", this.totalRecaudacionCentral);
				this.values.add(my);
			}
			
			// transferencia central..
			for (ReciboFormaPago fp : planilla.getTransferenciasCentral()) {
				this.totalTransferenciaCentral += fp.getMontoGs();
				MyArray my = new MyArray(fp.getTipo().getDescripcion(),
						fp.getDescripcion(), fp.getMontoGs(),
						"TRANSFERENCIA CASA CENTRAL",
						this.totalTransferenciaCentral);
				this.values.add(my);
			}

			// total efectivo..
			this.totalEfectivo = planilla.getTotalEfectivoIngreso()
					- planilla.getTotalEfectivoEgreso();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		MyArray det = this.values.get(index);

		if ("Concepto".equals(fieldName)) {
			value = det.getPos1();
		} else if ("Descripcion".equals(fieldName)) {
			value = det.getPos2();
		} else if ("Importe".equals(fieldName)) {
			value = FORMATTER.format(det.getPos3());
		} else if ("TituloDetalle".equals(fieldName)) {
			value = det.getPos4();
		} else if ("ResumenIngresos".equals(fieldName)) {
			value = FORMATTER.format(this.totalIngresos);
		} else if ("ResumenEgresos".equals(fieldName)) {
			value = FORMATTER.format(this.totalEgresos);
		} else if ("TotalEfectivo".equals(fieldName)) {
			value = FORMATTER.format(this.totalEfectivo);
		} else if ("TotalChequeTercero".equals(fieldName)) {
			value = FORMATTER.format(this.totalChequeTerceroAlDia
					+ this.totalChequeTerceroAdelantado);
		} else if ("TotalTarjCredito".equals(fieldName)) {
			value = FORMATTER.format(this.totalTarjetaCredito);
		} else if ("TotalTarjDebito".equals(fieldName)) {
			value = FORMATTER.format(this.totalTarjetaDebito);
		} else if ("TotalImporte".equals(fieldName)) {
			value = FORMATTER.format(det.getPos5());
		} else if ("TotalVtaContado".equals(fieldName)) {
			value = FORMATTER.format(this.totalVentaContado);
		}else if ("TotalNotaCredCont".equals(fieldName)) {
			value = FORMATTER.format(this.totalNotaCreditoContado * -1);
		} else if ("TotalTarjDebito_".equals(fieldName)) {
			value = FORMATTER.format(this.totalTarjetaDebito * -1);
		} else if ("TotalTarjCredito_".equals(fieldName)) {
			value = FORMATTER.format(this.totalTarjetaCredito * -1);
		} else if ("TotalCobranzasEfe".equals(fieldName)) {
			value = FORMATTER.format(this.totalCobranzaEfectivo);
		} else if ("TotalCobranzasCheq".equals(fieldName)) {
			value = FORMATTER.format(this.totalCobranzaChequeAlDia);
		} else if ("TotalReembolsoEfe".equals(fieldName)) {
			value = FORMATTER.format(this.totalCancelacionChequeEfectivo);
		} else if ("TotalReembolsoCheq".equals(fieldName)) {
			value = FORMATTER.format(this.totalCancelacionChequeAldia);
		} else if ("TotalCobranzasOtraSuc".equals(fieldName)) {
			value = FORMATTER.format(this.totalCobranzaAlDiaOtraSuc);
		} else if ("TotalRetencionCli".equals(fieldName)) {
			value = FORMATTER.format(this.totalRetencionCliente * -1);
		} else if ("TotalResumen2".equals(fieldName)) {
			value = FORMATTER
					.format((this.totalVentaContado
							+ this.totalCobranzaAlDia
							+ this.totalCancelacionChequeEfectivo
							+ this.totalCancelacionChequeAldia
							+ this.totalCobranzaAlDiaOtraSuc
							+ this.totalReposiciones 
							+ this.totalRetencionProveedor)
							- (this.totalNotaCreditoContado
									+ this.totalTarjetaDebito
									+ this.totalRetencionCliente
									+ this.totalDepositoBancario
									+ this.totalTarjetaCredito
									+ this.totalRepEgresos 
									+ this.totalPagos));
		} else if ("TotalPagos".equals(fieldName)) {
			value = FORMATTER.format(this.totalPagos * -1);
		} else if ("TotalReposicion".equals(fieldName)) {
			value = FORMATTER.format(this.totalReposiciones);
		} else if ("TotalGastos".equals(fieldName)) {
			value = FORMATTER.format(this.totalGastos * -1);
		} else if ("TotalEgresos".equals(fieldName)) {
			value = FORMATTER.format(this.totalRepEgresos * -1);
		} else if ("TotalRetProv".equals(fieldName)) {
			value = FORMATTER.format(this.totalRetencionProveedor);
		} else if ("TotalDepBancarios".equals(fieldName)) {
			value = FORMATTER.format(this.totalDepositoBancario * -1);
		} else if ("SaldoCajaChica".equals(fieldName)) {
			value = FORMATTER
					.format((this.totalReposiciones + this.totalRetencionProveedor)
							- (this.totalPagos + this.totalGastos + this.totalRepEgresos));
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}
