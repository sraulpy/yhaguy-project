package com.yhaguy.gestion.caja.periodo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.IiD;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.coreweb.util.Ruc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaPeriodoArqueo;
import com.yhaguy.domain.CajaReposicion;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.gestion.caja.principal.AssemblerCaja;
import com.yhaguy.gestion.caja.recibos.AssemblerRecibo;
import com.yhaguy.gestion.caja.recibos.AssemblerReciboFormaPago;
import com.yhaguy.gestion.caja.recibos.ReciboDTO;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.AssemblerGasto;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.gestion.notacredito.NotaCreditoDTO;
import com.yhaguy.gestion.notacredito.NotaCreditoDetalleDTO;
import com.yhaguy.gestion.venta.VentaDTO;

public class CajaPeriodoAssembler extends Assembler {

	final static String[] ATT_IGUALES = { "numero", "apertura", "cierre", "tipo" };
	final static String[] ATT_NC = { "numero" };
	final static String[] ATT_ARQUEO = { "totalEfectivo", "totalCheque", "totalTarjeta" };
	
	final static String[] ATT_VENTAS = { "numero", "tipoMovimiento", "fecha",
			"vendedor", "cliente", "condicionPago", "moneda", "tipoCambio",
			"totalImporteGs", "totalImporteDs", "formasPago", "estado", "denominacion" };	
	
	final static String[] ATT_REPOSICION = { "responsable", "funcionario",
			"fechaEmision", "tipoCambio", "montoGs", "montoDs", "observacion",
			"tipo", "moneda", "tipoEgreso", "estadoComprobante",
			"motivoAnulacion", "funcionarioAsignado", "formaPago", "numero" };

	final static String ITEM_DE_PAGO = "PAGO";
	final static String ITEM_DE_REPOSICION = "REPOSICIÓN";
	final static String ITEM_DE_EGRESO = "EGRESO";
	final static String ITEM_DE_COBRO = "COBRO";
	final static String ITEM_DE_ANTICIPO = "ANTICIPO";
	final static String ITEM_DE_CANCELACION_CHEQUE = "CANCELACION CHEQUE";
	final static String ITEM_DE_VENTA_CONTADO = "VENTA-CONTADO";
	final static String ITEM_DE_VENTA_CREDITO = "VENTA-CRÉDITO";
	final static String ITEM_DE_NOTA_CREDITO_VENTA = "NOTA-CRÉDITO-VENTA";
	final static String ITEM_DE_NOTA_CREDITO_COMPRA = "NOTA-CRÉDITO-COMPRA";
	final static String ITEM_DE_GASTO = "GASTO";

	final static int ES_VENTA_CONTADO = CajaPeriodoControlBody.ES_VENTA_CONTADO;
	final static int ES_VENTA_CREDITO = CajaPeriodoControlBody.ES_VENTA_CREDITO;
	final static int ES_NC_VENTA = CajaPeriodoControlBody.ES_NOTA_CREDITO_VENTA;
	final static int ES_NC_COMPRA = CajaPeriodoControlBody.ES_NOTA_CREDITO_COMPRA;

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		CajaPeriodo domain = (CajaPeriodo) getDomain(dto, CajaPeriodo.class);
		CajaPeriodoDTO dtoC = (CajaPeriodoDTO) dto;

		this.copiarValoresAtributos(dto, domain, ATT_IGUALES);
		this.myPairToDomain(dto, domain, "estado");
		this.hijoDtoToHijoDomain(dto, domain, "caja", new AssemblerCaja(), false);
		this.myArrayToDomain(dto, domain, "verificador");
		this.myArrayToDomain(dto, domain, "responsable");
		this.listaDTOToListaDomain(dto, domain, "recibos", true, true, new AssemblerRecibo());
		this.listaMyArrayToListaDomain(dto, domain, "ventas");
		this.listaMyArrayToListaDomain(dto, domain, "notasCredito");
		this.listaMyArrayToListaDomain(dto, domain, "reposiciones", ATT_REPOSICION, true, true);
		this.listaDTOToListaDomain(dto, domain, "gastos", true, true, new AssemblerGasto());
		
		MyArray arqueo = dtoC.getArqueo();
		if (arqueo.esNuevo() == true) {
			this.saveArqueo(arqueo);
		}
		this.myArrayToDomain(dto, domain, "arqueo");		

		this.actualizarCuentaCorriente(dtoC.getRecibos(),
				dtoC.getVentas_a_imputar(), dtoC.getNotaCredito_a_imputar());

		return domain;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		CajaPeriodoDTO dto = (CajaPeriodoDTO) getDTO(domain, CajaPeriodoDTO.class);
		CajaPeriodo dom = (CajaPeriodo) domain;

		this.copiarValoresAtributos(domain, dto, ATT_IGUALES);
		this.domainToMyPair(domain, dto, "estado");
		this.hijoDomainToHijoDTO(domain, dto, "caja", new AssemblerCaja());
		dto.setVerificador(this.getFuncionario(dom.getVerificador()));
		dto.setResponsable(this.getFuncionario(dom.getResponsable()));
		dto.setFuncionario(this.getFuncionario(dom.getVerificador()));
		this.domainToMyArray(domain, dto, "arqueo", ATT_ARQUEO);
		this.listaDomainToListaDTO(domain, dto, "recibos", new AssemblerRecibo());
		this.listaDomainToListaMyArray(domain, dto, "ventas", ATT_VENTAS);
		this.listaDomainToListaMyArray(domain, dto, "notasCredito", ATT_NC);
		this.listaDomainToListaMyArray(domain, dto, "reposiciones", ATT_REPOSICION);
		this.listaDomainToListaDTO(domain, dto, "gastos", new AssemblerGasto());
		dto.setDetalles(this.getDetalles(dom.getRecibos(), dom.getVentas(),
				dom.getNotasCredito(), dom.getReposiciones(), dom.getGastos()));
		
		this.addSiglaMoneda(dto.getReposiciones());	
		this.convertFormaPago(dto.getReposiciones());
		
		for (MyArray vta : dto.getVentas()) {
			List<MyPair> fp = (List<MyPair>) vta.getPos11();
			for (MyPair myPair : fp) {
				System.out.println(myPair.getText());
			}
		}

		return dto;
	}

	/**
	 * Retorna un funcionario en forma de MyArray
	 */
	private MyArray getFuncionario(Funcionario fun) {
		MyArray out = new MyArray();
		if (fun != null) {
			out.setId(fun.getId());
			out.setPos1(fun.getEmpresa().getNombre());
		}
		
		return out;
	}

	/**
	 * Procesa los detalles para pasarlos al DTO..
	 */
	private List<MyArray> getDetalles(Set<Recibo> recibos, Set<Venta> ventas,
			Set<NotaCredito> notasCredito, Set<CajaReposicion> reposiciones,
			Set<Gasto> gastos) {

		List<MyArray> out = new ArrayList<MyArray>();

		// Convierte los recibos a MyArray
		for (Recibo rec : recibos) {
			int signo = 1;
			int tipo;
			String razonSocial = null;
			String tipo_;
			MyPair estadoComprobante = this.tipoToMyPair(rec
					.getEstadoComprobante());

			if (isCobro(rec.getTipoMovimiento().getSigla()) == true) {
				tipo = CajaPeriodoControlBody.ES_COBRO;
				razonSocial = rec.getCliente().getRazonSocial() + " (" + rec.getCobrador() + ")";
				tipo_ = ITEM_DE_COBRO;
			
			} else if (isCancelacionCheque(rec.getTipoMovimiento().getSigla())) {
				tipo = CajaPeriodoControlBody.ES_CANC_CHQ_RECHAZADO;
				razonSocial = rec.getCliente().getRazonSocial()  + " (" + rec.getCobrador() + ")";
				tipo_ = ITEM_DE_COBRO;
				
			} else if (isReembolsoPrestamo(rec.getTipoMovimiento().getSigla())) {
				tipo = CajaPeriodoControlBody.ES_REEMBOLSO_PRESTAMO;
				razonSocial = rec.getCliente().getRazonSocial()  + " (" + rec.getCobrador() + ")";
				tipo_ = ITEM_DE_COBRO;
				
			} else {
				signo = -1;
				tipo = CajaPeriodoControlBody.ES_PAGO;
				razonSocial = rec.getProveedor().getRazonSocial();
				tipo_ = ITEM_DE_PAGO;
			}

			if (this.isAnticipo(rec.getTipoMovimiento().getSigla()) == true) {
				tipo_ += " - " + ITEM_DE_ANTICIPO;
			}
			
			String s_monedaExt = rec.getMoneda().getSigla();
			String numero = rec.getNumero();
			double importeDs = rec.getTotalImporteDs();
			double importeGs = rec.getTotalImporteGs();

			MyArray m = this.getMyArray(rec.getId(), tipo,
					rec.getFechaEmision(), razonSocial, importeDs, importeGs,
					signo, tipo_, s_monedaExt, estadoComprobante, numero);
			
			if(rec.getRetencion() != null)
				m.setPos20("RETENCIÓN GENERADA");

			out.add(m);
		}

		// Convierte las ventas a MyArray
		for (Venta v : ventas) {

			int signo = 1;
			int tipo = (v.isVentaContado() ? ES_VENTA_CONTADO
					: ES_VENTA_CREDITO);
			String tipo_ = (v.isVentaContado() ? ITEM_DE_VENTA_CONTADO
					: ITEM_DE_VENTA_CREDITO);
			String s_monedaExt = v.getMoneda().getSigla();
			String razonSocial = v.getCliente().getRazonSocial();
			String numero = v.getNumero();

			double importeGs = v.getTotalImporteGs();
			double importeDs = v.getTotalImporteDs();

			MyPair estadoComprobante = this.tipoToMyPair(v
					.getEstadoComprobante() == null ? v.getEstado() : v
					.getEstadoComprobante());

			MyArray m = this.getMyArray(v.getId(), tipo, v.getFecha(),
					razonSocial, importeDs, importeGs, signo, tipo_,
					s_monedaExt, estadoComprobante, numero);

			if (v.isReparto())
				m.setPos20("VENTA REMISIÓN");

			out.add(m);
		}

		// Convierte las notas de credito a MyArray
		for (NotaCredito nc : notasCredito) {

			int signo = -1;
			int tipo = ES_NC_VENTA;
			String tipo_ = ITEM_DE_NOTA_CREDITO_VENTA;
			String s_monedaExt = nc.getMoneda().getSigla();
			String razonSocial = "";
			String numero = nc.getNumero();
			
			if (nc.isNotaCreditoCompra()) {
				signo = 1;
				tipo = ES_NC_COMPRA;
				tipo_ = ITEM_DE_NOTA_CREDITO_COMPRA;
				razonSocial = nc.getProveedor().getRazonSocial();
			} else {
				razonSocial = nc.getCliente().getRazonSocial();
			}

			double importeGs = nc.getImporteGs();
			double importeDs = nc.getImporteDs();
			
			MyPair estadoComprobante = this.tipoToMyPair(nc.getEstadoComprobante());

			MyArray m = this.getMyArray(nc.getId(), tipo, nc.getFechaEmision(),
					razonSocial, importeDs, importeGs, signo, tipo_,
					s_monedaExt, estadoComprobante, numero);

			out.add(m);
		}

		// Convierte las reposiciones a MyArray
		for (CajaReposicion c : reposiciones) {

			int tipo;
			int signo = 1;
			String tipo_;
			String sigla = c.getTipo().getSigla();
			String descripcion = c.getNumero() + " - "
					+ c.getTipo().getDescripcion();
			String s_monedaExt = c.getMoneda().getSigla();
			MyPair estadoComprobante = this.tipoToMyPair(c
					.getEstadoComprobante());
			String numero = c.getNumero();

			if (sigla.compareTo(Configuracion.SIGLA_CAJA_REPOSICION_INGRESO) == 0) {
				tipo = CajaPeriodoControlBody.ES_REPOSICION;
				tipo_ = ITEM_DE_REPOSICION;
			} else if (sigla
					.compareTo(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO) == 0) {
				tipo = CajaPeriodoControlBody.ES_EGRESO;
				tipo_ = ITEM_DE_EGRESO;
				signo = -1;
			} else {
				tipo = -1; // "Error de Tipo"
				tipo_ = null;
			}

			double importeDs = c.getMontoDs();
			double importeGs = c.getMontoGs();

			MyArray m = this.getMyArray(c.getId(), tipo, c.getFechaEmision(),
					descripcion, importeDs, importeGs, signo, tipo_,
					s_monedaExt, estadoComprobante, numero);

			out.add(m);
		}
		
		//Convierte los gastos a MyArray
		for (Gasto gasto : gastos) {

			int signo = -1;
			int tipo = CajaPeriodoControlBody.ES_GASTO;
			MyPair estadoComprobante = this.tipoToMyPair(gasto
					.getEstadoComprobante());
			String tipo_ = ITEM_DE_GASTO;
			String s_monedaExt = gasto.getMoneda().getSigla();
			String razonSocial = gasto.getProveedor().getRazonSocial();
			String numero = gasto.getNumeroFactura();

			double importeGs = gasto.getImporteGs();
			double importeDs = gasto.getImporteDs();

			MyArray m = this.getMyArray(gasto.getId(), tipo, gasto.getFecha(),
					razonSocial, importeDs, importeGs, signo, tipo_,
					s_monedaExt, estadoComprobante, numero);

			out.add(m);
		}
		
		return out;
	}

	/**
	 * Retorna el MyArray que usa el Detalle del DTO..
	 */
	private MyArray getMyArray(long id, int tipo, Date fecha,
			String descripcion, double importeDs, double importeGs, int signo,
			String itemTipo, String simboloMonedaExtranjera,
			MyPair estadoComprobante, String numero) {

		boolean monedaLocal = (simboloMonedaExtranjera
				.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0);

		MyArray out = new MyArray();
		out.setId(id);
		out.setPos1(tipo);
		out.setPos2(fecha);
		out.setPos3(descripcion.toUpperCase());
		out.setPos4(importeDs * signo);
		out.setPos5(importeGs * signo);
		out.setPos6(this.getIconoDetalle(estadoComprobante.getSigla()));
		out.setPos8(itemTipo);
		out.setPos10(Configuracion.SIGLA_MONEDA_GUARANI);
		out.setPos11(simboloMonedaExtranjera);
		out.setPos12(monedaLocal);
		out.setPos13(estadoComprobante);
		out.setPos14(numero);

		return out;
	}
	
	/**
	 * @return el icono correspondiente al detalle..
	 */
	private String getIconoDetalle(String siglaEstadoComprobante) {
		String out = Configuracion.ICONO_ACEPTAR_16X16;

		if (this.isComprobanteAnulado(siglaEstadoComprobante) == true) {
			out = Configuracion.ICONO_ANULAR_16X16;

		} else if (this.isComprobantePendiente(siglaEstadoComprobante) == true) {
			out = Configuracion.ICONO_EXCLAMACION_YELLOW_16X16;
		}

		return out;
	}

	/**
	 * Actualiza la Cuenta Corriente
	 */
	private void actualizarCuentaCorriente(List<ReciboDTO> recibos,
			List<VentaDTO> ventas, NotaCreditoDTO notaCredito) throws Exception {

		if (recibos.size() > 0) {
			MyArray sucursal = new MyArray();
			sucursal.setId(recibos.get(0).getSucursal().getId());

			// recorre los recibos y si es nuevo o anulado actualiza la CtaCte..
			for (ReciboDTO rec : recibos) {
				if ((rec.isImputar() == true) 
						&& (rec.isCobroExterno() == false)
						&& (rec.isCobro() || rec.isReembolsoPrestamo() || rec.isCancelacionChequeRechazado())) {
					this.actualizarCtaCteRecibo(rec);
					this.addSaldoFavorCliente(rec);
					this.cancelSaldoFavorCliente(rec);
				}
				
				// si es pago de gastos contado..actualiza la ctacte..
				if (rec.isImputar() && rec.isOrdenPagoGastosContado()) {
					this.actualizarCtaCteRecibo(rec);
				}
				
				// si es pago anticipado..actualiza la ctacte..
				if (rec.isImputar() && rec.isAnticipoPago()) {
					this.actualizarCtaCteRecibo(rec);
				}
			}
		}
		
		for (VentaDTO venta : ventas) {
			if (venta.esNuevo() == false) {
				this.actualizarCtaCteVenta(venta);
			}
		}		
		
		if (notaCredito.esNuevo() == false) {
			this.actualizarCtaCteNotaCredito(notaCredito);
		}
	}

	/**
	 * Invoca a la API de Cta Cte para actualizar el recibo sea de Pago ó Cobro
	 */
	private void actualizarCtaCteRecibo(ReciboDTO rec)
			throws Exception {		
		if (rec.isCobro() || rec.isCancelacionChequeRechazado() || rec.isReembolsoPrestamo()) {
			ControlCuentaCorriente.addReciboDeCobro(rec.getId(), this.getLogin());
		} 
		// es una orden de pago de gastos contado (contiene solo facturas contado)
		if (rec.isOrdenPago() && rec.isOrdenPagoGastosContado_()) {
			AssemblerRecibo.registrarReciboPago("GASTO CONTADO", new Date(), rec.getId(), this.getLogin(), true);
		}
		// es una orden de pago de gastos contado (que tambien incluye facturas credito)
		if (rec.isOrdenPago() && rec.isOrdenPagoGastosContado() && !rec.isOrdenPagoGastosContado_()) {
			ControlCuentaCorriente.addReciboDePagoGastosContado(rec.getId(), this.getLogin());
		}
		// es una orden de pago anticipado..
		if (rec.isAnticipoPago()) {
			AssemblerRecibo.registrarReciboPago("ANTICIPO", new Date(), rec.getId(), this.getLogin(), true);
			ControlCuentaCorriente.addReciboDePagoAnticipado(rec.getId(), this.getLogin(), (String) rec.getMoneda().getPos2());
		}
	}

	/**
	 * Invoca a la API de Cta Cte para actualizar la Venta sea Contado ó
	 * Crédito..
	 */
	private void actualizarCtaCteVenta(VentaDTO venta) throws Exception {

		ControlCtaCteEmpresa ctr = new ControlCtaCteEmpresa(null);
		String siglaCaracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;
		MyPair caracterMovimiento = this.getCaracterMovimiento(siglaCaracter);

		MyArray sucursal = new MyArray();
		sucursal.setId(venta.getSucursal().getId());

		long idEmpresa = (long) venta.getCliente().getPos4();
		MyPair empresa = new MyPair(idEmpresa, "");

		long idMoneda = venta.getMoneda().getId();
		MyPair moneda = new MyPair(idMoneda, "");

		boolean monedaLocal = this.isOperacionEnMonedaLocal(venta.getMoneda());
		double importe = 0;

		if (monedaLocal == true) {
			importe = venta.getTotalImporteGs();
		} else {
			importe = venta.getTotalImporteDs();
		}

		if (venta.isCondicionContado() == true) {
			ctr.addCtaCteEmpresaMovimientoFacturaContado(empresa, venta.getId(),
					venta.getNumero(), venta.getFecha(), importe, moneda,
					venta.getTipoMovimiento(), caracterMovimiento, sucursal, false, "");
		} else {
			ctr.addCtaCteEmpresaMovimientoFacturaCredito(empresa, venta.getId(),
					venta.getNumero(), venta.getFecha(), (int)venta.getCondicionPago().getPos4(), (int)venta.getCondicionPago().getPos3(),
					importe, 0, importe, moneda, venta.getTipoMovimiento(),
					caracterMovimiento, sucursal, "", venta.getTipoCambio());
		}
	}
	
	/**
	 * Invoca a la API de Cta Cte para actualizar la NC..
	 */
	private void actualizarCtaCteNotaCredito(NotaCreditoDTO nc)
			throws Exception {

		ControlCtaCteEmpresa ctr = new ControlCtaCteEmpresa(null);
		String siglaCaracter = Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE;
		MyPair caracterMovimiento = this.getCaracterMovimiento(siglaCaracter);

		MyArray sucursal = new MyArray();
		sucursal.setId(nc.getSucursal().getId());

		long idEmpresa = (long) nc.getCliente().getPos4();
		MyPair empresa = new MyPair(idEmpresa, "");
		List<NotaCreditoDetalleDTO> items = nc.isMotivoDescuento() ? nc
				.getDetallesFacturas() : nc.getDetallesArticulos();

		boolean monedaLocal = this.isOperacionEnMonedaLocal(nc.getMoneda());
		double importe = 0;

		if (monedaLocal == true) {
			importe = nc.getImporteGs();
		} else {
			importe = nc.getImporteDs();
		}

		ctr.addMovimientoNotaCredito(empresa, nc.getId(), nc.getNumero(),
				nc.getFechaEmision(), importe, nc.getMoneda(),
				nc.getTipoMovimiento(), caracterMovimiento, sucursal, items);
	}
	
	/**
	 * agrega los saldos a favor del cliente..
	 */
	private void addSaldoFavorCliente(ReciboDTO cobro) throws Exception {
		for (ReciboFormaPagoDTO item : cobro.getFormasPago()) {
			if (item.isSaldoFavorGenerado()) {
				RegisterDomain rr = RegisterDomain.getInstance();
				CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
				ctm.setFechaEmision(new Date());
				ctm.setCerrado(true);
				ctm.setIdEmpresa((long) cobro.getCliente().getPos4());
				ctm.setIdMovimientoOriginal(cobro.getId());
				ctm.setImporteOriginal(cobro.getTotalImporteGs());
				ctm.setSaldo(item.getMontoGs() > 0 ? item.getMontoGs() * -1 : item.getMontoGs());
				ctm.setNroComprobante(cobro.getNumero());
				ctm.setSucursal(rr.getSucursalAppById(2));
				ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
				ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
				ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_CTA_CTE_SALDO_FAVOR));
				rr.saveObject(ctm, this.getLogin());
			}
		}
	}
	
	/**
	 * cancela los saldos a favor del cliente..
	 */
	private void cancelSaldoFavorCliente(ReciboDTO cobro) throws Exception {
		for (ReciboFormaPagoDTO item : cobro.getFormasPago()) {
			if (item.isSaldoFavorCobrado()) {
				RegisterDomain rr = RegisterDomain.getInstance();
				CtaCteEmpresaMovimiento ctm = rr.getCtaCteEmpresaMovimientoById(item.getCtaCteSaldoFavor().getId());
				ctm.setSaldo(ctm.getSaldo() + item.getMontoGs());
				rr.saveObject(ctm, this.getLogin());
			}
		}
	}

	/**
	 * Retorna el Caracter de Movimiento según la Sigla..
	 */
	private MyPair getCaracterMovimiento(String sigla) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Tipo cm = rr.getTipoPorSigla(sigla);
		return new MyPair(cm.getId(), "");
	}

	/**
	 * Indica si la operacion es en moneda local..
	 */
	private boolean isOperacionEnMonedaLocal(IiD moneda) {
		String sigla = "";

		if (moneda instanceof MyArray) {
			sigla = (String) ((MyArray) moneda).getPos2();

		} else if (moneda instanceof MyPair) {
			sigla = ((MyPair) moneda).getSigla();
		}

		if (sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Indica si es un Recibo de Cobro..
	 */
	private boolean isCobro(String sigla_tm) {
		String siglaCobro = Configuracion.SIGLA_TM_RECIBO_COBRO;
		String siglaAnticipo = Configuracion.SIGLA_TM_ANTICIPO_COBRO;

		boolean cobro = siglaCobro.compareTo(sigla_tm) == 0;
		boolean anticipo = siglaAnticipo.compareTo(sigla_tm) == 0;

		return cobro || anticipo;
	}
	
	/**
	 * Indica si es una cancelacion de cheque rechazado..
	 */
	private boolean isCancelacionCheque(String sigla_tm) {
		return sigla_tm.equals(Configuracion.SIGLA_TM_CANCELACION_CHEQ_RECHAZADO);
	}
	
	/**
	 * Indica si es un reembolso de prestamo..
	 */
	private boolean isReembolsoPrestamo(String sigla_tm) {
		return sigla_tm.equals(Configuracion.SIGLA_TM_REEMBOLSO_PRESTAMO);
	}

	/**
	 * Indica si es un Recibo de Anticipo..
	 */
	private boolean isAnticipo(String sigla_tm) {

		String siglaAntCob = Configuracion.SIGLA_TM_ANTICIPO_COBRO;
		String siglaAntPag = Configuracion.SIGLA_TM_ANTICIPO_PAGO;

		boolean anticipoCobro = siglaAntCob.compareTo(sigla_tm) == 0;
		boolean anticipoPago = siglaAntPag.compareTo(sigla_tm) == 0;

		return anticipoCobro || anticipoPago;
	}
	
	/**
	 * Indica si es un comprobante anulado..
	 */
	private boolean isComprobanteAnulado(String sigla) {
		return sigla.compareTo(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO) == 0;
	}
	
	/**
	 * Indica si es un comprobante pendiente..
	 */
	private boolean isComprobantePendiente(String sigla) {
		return sigla.compareTo(Configuracion.SIGLA_ESTADO_COMPROBANTE_PENDIENTE) == 0;
	}
	
	/**
	 * Setea la sigla a las monedas de las reposiciones..
	 */
	private void addSiglaMoneda(List<MyArray> reposiciones) throws Exception {
		for (MyArray reposicion : reposiciones) {
			MyPair moneda = (MyPair) reposicion.getPos9();
			this.addSiglaMoneda(moneda);
		}
	}
	
	/**
	 * convierte las formas de pago de las reposiciones a DTO..
	 */
	private void convertFormaPago(List<MyArray> reposiciones) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		AssemblerReciboFormaPago ass = new AssemblerReciboFormaPago("");
		String clase = ReciboFormaPago.class.getName();

		for (MyArray reposicion : reposiciones) {
			MyPair fp = (MyPair) reposicion.getPos14();
			if (fp != null) {
				ReciboFormaPago rfp = (ReciboFormaPago) rr.getObject(clase,
						fp.getId());
				ReciboFormaPagoDTO rfpDto = (ReciboFormaPagoDTO) ass
						.domainToDto(rfp);
				reposicion.setPos14(rfpDto);
			}
		}
	}
	
	/**
	 * setea la sigla a la moneda..
	 */
	private void addSiglaMoneda(MyPair moneda) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Tipo moneda_ = rr.getTipoById(moneda.getId());
		moneda.setSigla(moneda_.getSigla());
	}
	
	/**
	 * graba el arqueo de caja..
	 */
	private void saveArqueo(MyArray arqueo) throws Exception {
		
		if (arqueo.getPos1() instanceof String) {
			return;
		}
		
		double totalEfectivo = (double) arqueo.getPos1();
		double totalCheque = (double) arqueo.getPos2();
		double totalTarjeta = (double) arqueo.getPos3();
		
		RegisterDomain rr = RegisterDomain.getInstance();
		CajaPeriodoArqueo arqueoDomain = new CajaPeriodoArqueo();
		arqueoDomain.setTotalEfectivo(totalEfectivo);
		arqueoDomain.setTotalCheque(totalCheque);
		arqueoDomain.setTotalTarjeta(totalTarjeta);
		rr.saveObject(arqueoDomain, this.getLogin());
		
		arqueo.setId(arqueoDomain.getId());		
	}
	
	public static void main(String[] args) {
		Ruc ruc = new Ruc();
		System.out.println(ruc.calcularDigitoVerificador("3555481"));
	}
}
