package com.yhaguy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.coreweb.domain.IiD;
import com.coreweb.dto.UtilCoreDTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class UtilDTO extends UtilCoreDTO {

	// para usar en todos los combobox
	MyPair sinDefinir = new MyPair(-1, "Sin definir");

	// articulo
	List<MyPair> articuloEstado = new ArrayList<MyPair>();
	List<MyPair> articuloFamilia = new ArrayList<MyPair>();
	List<MyPair> articuloLinea = new ArrayList<MyPair>();
	List<MyPair> articuloMarca = new ArrayList<MyPair>();
	List<MyPair> articuloParte = new ArrayList<MyPair>();
	List<MyPair> articuloUnidadMedida = new ArrayList<MyPair>();
	List<MyArray> articuloModeloAplicacion = new ArrayList<MyArray>();
	List<MyArray> articuloMarcaAplicacion = new ArrayList<MyArray>();
	List<MyArray> articuloPresentacion = new ArrayList<MyArray>();
	MyPair articuloEstadoActivo = new MyPair();

	// cliente
	List<MyPair> categoriaCliente = new ArrayList<MyPair>();
	List<MyPair> contactoSexo = new ArrayList<MyPair>();
	List<MyPair> estadoCivil = new ArrayList<MyPair>();
	List<MyPair> estadoCliente = new ArrayList<MyPair>();
	List<MyPair> tipoCliente = new ArrayList<MyPair>();
	MyPair estadoClienteActivo = new MyPair();
	MyPair categoriaClienteAldia = new MyPair();

	MyPair tipoClienteMinorista = new MyPair();
	MyPair tipoClienteOcasional = new MyPair();

	// tarjetas
	List<MyPair> tipoTarjeta = new ArrayList<MyPair>();
	MyPair tipoTarjeta1 = new MyPair();
	MyPair tipoTarjeta2 = new MyPair();
	MyPair tipoTarjeta3 = new MyPair();

	List<MyArray> tarjetas = new ArrayList<>();

	// venta
	List<MyPair> tipoVenta = new ArrayList<MyPair>();
	MyPair tipoVentaMostrador = new MyPair();
	MyPair tipoVentaVendedor = new MyPair();
	MyPair tipoVentaTerminal = new MyPair();
	MyPair tipoVentaEncomienda = new MyPair();
	MyPair tipoVentaColectivo = new MyPair();
	MyPair tipoVentaReparto = new MyPair();

	// CtaCte
	List<MyArray> ctaCteLineaCredito = new ArrayList<MyArray>();
	List<MyPair> ctaCteEstado = new ArrayList<MyPair>();
	List<MyPair> ctaCteTipoOperacion = new ArrayList<MyPair>();
	List<MyPair> ctaCteEmpresaEstado = new ArrayList<MyPair>();
	List<MyPair> ctaCteEmpresaSeleccionMov = new ArrayList<MyPair>();
	MyArray ctaCteLineaCreditoDefault = new MyArray();
	MyPair ctaCteImputacionParcial = new MyPair();
	MyPair ctaCteImputacionCompleta = new MyPair();
	MyPair ctaCteEmpresaCaracterMovProveedor = new MyPair();
	MyPair ctaCteEmpresaCaracterMovCliente = new MyPair();
	MyPair ctaCteEmpresaEstadoActivo = new MyPair();
	MyPair ctaCteEmpresaEstadoInactivo = new MyPair();
	MyPair ctaCteEmpresaEstadoBloqueado = new MyPair();
	MyPair ctaCteEmpresaEstadoSinCuenta = new MyPair();

	// proveedor
	List<MyPair> proveedorEstado = new ArrayList<MyPair>();
	List<MyPair> proveedorTipo = new ArrayList<MyPair>();
	List<MyPair> proveedorTipoPago = new ArrayList<MyPair>();
	MyPair proveedorEstadoActivo = new MyPair();
	MyPair proveedorTipoLocal = new MyPair();
	MyPair proveedorTipoPagoContado = new MyPair();

	// empresa
	List<MyPair> rubroEmpresa = new ArrayList<MyPair>();
	List<MyPair> emailEmpresa = new ArrayList<MyPair>();
	List<MyPair> tipoContactoInterno = new ArrayList<MyPair>();

	List<MyPair> paisEmpresas = new ArrayList<MyPair>();
	MyPair paisParaguay = new MyPair();

	List<MyPair> tipoPersonas = new ArrayList<MyPair>();
	MyPair tipoPersonaFisica = new MyPair();
	MyPair tipoPersonaJuridica = new MyPair();

	// funcionario
	List<MyPair> funcionarioEstado = new ArrayList<MyPair>();
	List<MyPair> funcionarioCargo = new ArrayList<MyPair>();

	List<MyPair> importado = new ArrayList<MyPair>();

	List<MyPair> profesion = new ArrayList<MyPair>();

	List<MyPair> importacionEstados = new ArrayList<MyPair>();
	MyPair importacionEstadoConfirmado = new MyPair();
	MyPair importacionEstadoSolicitandoCotizacion = new MyPair();
	MyPair importacionEstadoProformaRecibida = new MyPair();
	MyPair importacionEstadoPendienteEnvio = new MyPair();
	MyPair importacionEstadoPedidoEnviado = new MyPair();
	MyPair importacionEstadoCerrado = new MyPair();
	MyPair importacionEstadoAnulado = new MyPair();

	// Cliente ocasional
	MyPair clienteOcasional = new MyPair();

	// ================== Monedas - Cambios =========================
	// Moneda
	MyPair monedaGuarani;
	MyArray monedaGuaraniConSimbolo;
	MyArray monedaDolaresConSimbolo;
	List<MyArray> monedasConSimbolo = new ArrayList<MyArray>();
	List<MyPair> monedas = new ArrayList<MyPair>();

	// Tipo de cambios
	MyPair cambioAPP;
	MyPair cambioBCP;
	List<MyPair> tiposCambio = new ArrayList<MyPair>();

	transient private Hashtable<String, CambioMoneda> cambiosMonedas = new Hashtable<String, CambioMoneda>();

	/**
	 * Inicializa la lista con los valores de cambio para cada tipo de moneda.
	 * OJO, no borra la lista 'monedas'
	 */
	public void borrarListaCambioMonedas() {
		this.cambiosMonedas = new Hashtable<String, CambioMoneda>();
	}

	public void addCambioMoneda(CambioMoneda cm) {
		String k = cm.getTipoMoneda() + "";
		this.cambiosMonedas.put(k, cm);
	}

	public double getCambioCompraBCP(IiD id) {
		return this.getCambio(id, false, true);
	}

	public double getCambioVentaBCP(IiD id) {
		return this.getCambio(id, false, false);
	}

	public double getCambioCompraAPP(IiD id) {
		return this.getCambio(id, true, true);
	}

	public double getCambioVentaAPP(IiD id) {
		return this.getCambio(id, true, false);
	}

	// segun lo que se quiere retorna el valor
	private double getCambio(IiD tipoMoneda, boolean esAPP, boolean esCompra) {
		double out = 0;
		CambioMoneda cm = this.cambiosMonedas.get(tipoMoneda.getId() + "");
		if ((esAPP == true) && (esCompra == true)) {
			out = cm.getAPPCompra();
		}
		if ((esAPP == true) && (esCompra == false)) {
			out = cm.getAPPVenta();
		}
		if ((esAPP == false) && (esCompra == true)) {
			out = cm.getBCPCompra();
		}
		if ((esAPP == false) && (esCompra == false)) {
			out = cm.getBCPVenta();
		}
		// System.out.println("======================== cambio:"+out+ "  tm:" +
		// tipoMoneda + "   esApp:" + esAPP + " esCompra:" + esCompra);
		return out;
	}

	// ==============================================================

	List<MyArray> departamentos = new ArrayList<MyArray>();
	List<MyArray> sucursales = new ArrayList<MyArray>();
	List<MyPair> sucursalesMyPair = new ArrayList<MyPair>();
	List<MyArray> usuarios = new ArrayList<MyArray>();
	List<MyPair> sucursalesAppSeleccion = new ArrayList<MyPair>();
	MyPair sucursalAppTodas = new MyPair();

	List<MyPair> transportes = new ArrayList<MyPair>();

	// depositos y articulos deposito
	List<MyArray> xdepositos = new ArrayList<MyArray>();
	List<MyPair> depositosMyPair = new ArrayList<MyPair>();

	// Bancos
	List<MyArray> bancos = new ArrayList<MyArray>();
	List<MyPair> bancoCtaTipos = new ArrayList<MyPair>();
	List<MyPair> estadosConciliacion = new ArrayList<MyPair>();
	MyPair estadoConciliacionConciliado = new MyPair();
	MyPair estadoConciliacionPendiente = new MyPair();
	MyPair estadoConciliacionDiferencia = new MyPair();

	// transferencias
	// List<MyPair> transferenciaTipos = new ArrayList<MyPair>();
	MyPair tipoTransferenciaInterna = new MyPair();
	MyPair tipoTransferenciaExterna = new MyPair();

	MyPair estadoTransferenciaElaboracion = new MyPair();
	MyPair estadoTransferenciaPedido = new MyPair();
	MyPair estadoTransferenciaPreparado = new MyPair();
	MyPair estadoTransferenciaConfirmada = new MyPair();
	MyPair estadoTransferenciaRecibida = new MyPair();
	MyPair estadoTransferenciaUbicada = new MyPair();
	MyPair estadoTransferenciaCancelada = new MyPair();

	// reservas (Estos tipos hay que sacar!!)
	String estadoReservaActiva = Configuracion.ESTADO_RESERVA_ACTIVA;
	String estadoReservaCancelada = Configuracion.ESTADO_RESERVA_CANCELADA;
	String estadoReservaFinalizada = Configuracion.ESTADO_RESERVA_FINALIZADA;
	String estadoReservaEnReparto = Configuracion.ESTADO_RESERVA_REPARTO;

	String tipoReservaInterna = Configuracion.TIPO_RESERVA_INTERNA;
	String tipoReservaRemision = Configuracion.TIPO_RESERVA_REMISION;

	List<MyArray> funcionarios = new ArrayList<MyArray>();
	List<MyArray> funcionariosMyPair = new ArrayList<MyArray>();

	// repartos
	MyPair estadoRepartoPreparacion = new MyPair();
	MyPair estadoRepartoEnTransito = new MyPair();
	MyPair estadoRepartoEntregado = new MyPair();

	List<MyPair> repartoTipos = new ArrayList<MyPair>();
	MyPair tipoRepartoTerminal = new MyPair();
	MyPair tipoRepartoColectivo = new MyPair();
	MyPair tipoRepartoEncomienda = new MyPair();
	MyPair tipoRepartoYhaguy = new MyPair();

	// prueba de tabla hash para obtener la sucursal dado el deposito
	HashMap<Long, MyArray> sucPorDep = new HashMap<Long, MyArray>();

	// Tipos de Movimientos
	List<MyArray> movimientosDeCompra = new ArrayList<MyArray>();
	List<MyArray> movimientosDeGasto = new ArrayList<MyArray>();
	List<MyArray> movimientosDePago = new ArrayList<MyArray>();
	List<MyArray> movimientosDeVenta = new ArrayList<MyArray>();
	List<MyArray> movimientosDeCobro = new ArrayList<MyArray>();
	List<MyArray> movimientosDeRemision = new ArrayList<MyArray>();
	List<MyArray> movimientosBancarios = new ArrayList<MyArray>();
	List<MyArray> movimientosDeAjuste = new ArrayList<MyArray>();

	// Tipos de Comprobante
	MyPair comprobanteLegal = new MyPair();
	MyPair comprobanteInterno = new MyPair();

	// Tipos de Operacion en los movimientos
	MyPair operacionCompra = new MyPair();
	MyPair operacionGasto = new MyPair();
	MyPair operacionPago = new MyPair();
	MyPair operacionVenta = new MyPair();
	MyPair operacionCobro = new MyPair();
	MyPair operacionRemision = new MyPair();
	MyPair operacionBancaria = new MyPair();
	MyPair operacionDevolucion = new MyPair();
	MyPair operacionAjuste = new MyPair();

	// Movimientos de Compra
	MyArray tmFacturaCompraContado = new MyArray();
	MyArray tmFacturaCompraCredito = new MyArray();
	MyArray tmFacturaImportacionContado = new MyArray();
	MyArray tmFacturaImportacionCredito = new MyArray();
	MyArray tmNotaCreditoCompra = new MyArray();
	MyArray tmNotaDebitoCompra = new MyArray();
	MyArray tmOrdenCompra = new MyArray();
	MyArray tmOrdenCompraImportacion = new MyArray();
	MyArray tmSolicitudNotaCreditoCompra = new MyArray();

	// Movimientos de Gasto
	MyArray tmFacturaGastoContado = new MyArray();
	MyArray tmFacturaGastoCredito = new MyArray();
	MyArray tmAutoFactura = new MyArray();
	MyArray tmBoletaVenta = new MyArray();
	MyArray tmOrdenGasto = new MyArray();
	MyArray tmOtrosComprobantes = new MyArray();

	// Movimientos de Pago
	MyArray tmReciboPago = new MyArray();
	MyArray tmPagare = new MyArray();
	MyArray tmRetencionIva = new MyArray();
	MyArray tmAnticipoPago = new MyArray();

	// Movimientos de Venta
	MyArray tmFacturaVentaContado = new MyArray();
	MyArray tmFacturaVentaCredito = new MyArray();
	MyArray tmNotaCreditoVenta = new MyArray();
	MyArray tmNotaDebitoVenta = new MyArray();
	MyArray tmPresupuestoVenta = new MyArray();
	MyArray tmPedidoVenta = new MyArray();
	MyArray tmSolicitudNotaCreditoVenta = new MyArray();

	// Movimientos de Cobro
	MyArray tmReciboCobro = new MyArray();
	MyArray tmAnticipoCobro = new MyArray();
	MyArray tmCancelacionChequeRechazado = new MyArray();
	MyArray tmReembolsoPrestamo = new MyArray();

	// Movimientos de Remision
	MyArray tmNotaRemision = new MyArray();
	MyArray tmTransferenciaMercaderia = new MyArray();
	
	// Movimientos de Ajuste Stock
	MyArray tmAjustePositivo = new MyArray();
	MyArray tmAjusteNegativo = new MyArray();
	MyArray tmInventario = new MyArray();

	// Movimientos Bancarios
	MyArray tmBancoEmisionCheque = new MyArray();
	MyArray tmBancoDepositoBancario = new MyArray();
	MyPair estadoMovimientoBancoPendiente = new MyPair();

	// Bancos para cheques de terceros
	List<MyPair> bancosTerceros = new ArrayList<MyPair>();

	// Estados de Comprobantes que implican Movimiento..
	List<MyPair> estadosComprobantes = new ArrayList<MyPair>();
	MyPair estadoComprobantePendiente = new MyPair();
	MyPair estadoComprobanteAprobado = new MyPair();
	MyPair estadoComprobanteCerrado = new MyPair();
	MyPair estadoComprobanteAnulado = new MyPair();
	MyPair estadoComprobanteConfeccionado = new MyPair();

	// Tipos de Empresa
	MyPair empresaCliente = new MyPair();
	MyPair empresaProveedor = new MyPair();
	MyPair empresaFuncionario = new MyPair();
	MyPair empresaBanco = new MyPair();

	// Cuentas Contables
	List<MyPair> cuentasContablesTipos = new ArrayList<MyPair>();
	MyArray cuentaProveedoresVarios = new MyArray();
	MyArray cuentaClientesOcasionales = new MyArray();
	MyArray cuentaIvaCF10 = new MyArray();
	MyArray cuentaIvaCF5 = new MyArray();

	// Pagos
	List<MyPair> reciboEstados = new ArrayList<MyPair>();
	MyPair reciboEstadoGuardado = new MyPair();
	MyPair reciboEstadoAnulado = new MyPair();

	List<MyPair> formasDePago = new ArrayList<MyPair>();
	MyPair formaPagoEfectivo = new MyPair();
	MyPair formaPagoChequePropio = new MyPair();
	MyPair formaPagoChequeTercero = new MyPair();
	MyPair formaPagoTarjetaCredito = new MyPair();
	MyPair formaPagoTarjetaDebito = new MyPair();
	MyPair formaPagoRetencion = new MyPair();
	MyPair formaPagoDepositoBancario = new MyPair();
	MyPair formaPagoChequeAutoCobranza = new MyPair();
	MyPair formaPagoDebitoCobranzaCentral = new MyPair();
	MyPair formaPagoRecaudacionCentral = new MyPair();
	MyPair formaPagoTransferenciaCentral = new MyPair();
	MyPair formaPagoSaldoFavorGenerado = new MyPair();
	MyPair formaPagoSaldoFavorCobrado = new MyPair();
	MyPair formaPagoDebitoEnCuentaBancaria = new MyPair();

	// Procesadoras de Tarjetas
	List<MyArray> procesadoras = new ArrayList<MyArray>();

	// Tipos de Gastos / Descuento / Prorrateos en Compras
	List<MyPair> compraTiposDescuento = new ArrayList<MyPair>();
	List<MyPair> compraTiposGastos = new ArrayList<MyPair>();
	List<MyPair> compraTiposProrrateo = new ArrayList<MyPair>();
	MyPair tipoCompraGastoFlete = new MyPair();
	MyPair tipoCompraGastoSeguro = new MyPair();
	MyPair tipoCompraProrrateoFlete = new MyPair();
	MyPair tipoCompraProrrateoSeguro = new MyPair();

	List<MyPair> compraLocalGastos = new ArrayList<MyPair>();

	// Venta y transferencia usan
	MyPair movEstadoRepartoPendiente = new MyPair();
	MyPair movEstadoRepartoPreparacion = new MyPair();
	MyPair movEstadoRepartoPreparado = new MyPair();
	MyPair movEstadoRepartoEnTransito = new MyPair();
	MyPair movEstadoRepartoFinalizado = new MyPair();

	// Condiciones de Pago
	List<MyArray> condicionesPago = new ArrayList<MyArray>();
	MyArray condicionPagoContado = new MyArray();
	MyArray condicionPagoCredito30 = new MyArray();
	MyArray condicionPagoCredito60 = new MyArray();
	MyArray condicionPagoCredito90 = new MyArray();

	// Tipos de Iva
	List<MyArray> tiposDeIva = new ArrayList<MyArray>();
	MyArray tipoIva10 = new MyArray();
	MyArray tipoIva5 = new MyArray();
	MyArray tipoIvaExento = new MyArray();

	// Grupos de Empresa
	List<MyPair> gruposDeEmpresa = new ArrayList<MyPair>();
	MyPair grupoEmpresaNoDefinido = new MyPair();

	// Regimen Tributario
	List<MyPair> regimenesTributarios = new ArrayList<MyPair>();
	MyPair regimenTributarioNoExenta = new MyPair();

	// Zonas
	List<MyPair> zonas = new ArrayList<MyPair>();

	// Estados de Pedido de Venta
	List<MyArray> estadosVenta = new ArrayList<MyArray>();
	MyArray estadoVenta_soloPresupuesto = new MyArray(); // Solo Presupuesto
	MyArray estadoVenta_pasadoApedido = new MyArray(); // Pasado a Pedido
	MyArray estadoVenta_soloPedido = new MyArray(); // Solo Pedido
	MyArray estadoVenta_cerrado = new MyArray(); // Pedido Cerrado
	MyArray estadoVenta_facturado = new MyArray(); // Pedido Facturado

	// Modos de Venta
	List<MyPair> modosVenta = new ArrayList<MyPair>();
	MyPair modoVenta_mostrador = new MyPair();
	MyPair modoVenta_externa = new MyPair();
	MyPair modoVenta_telefonica = new MyPair();

	// Tipos de Importacion
	List<MyPair> tiposImportacion = new ArrayList<MyPair>();
	MyPair tipoImportacionFOB = new MyPair();
	MyPair tipoImportacionCIF = new MyPair();
	MyPair tipoImportacionCF = new MyPair();

	// Reglas de precios
	MyPair reglaCliente = new MyPair();
	MyPair reglaArticulo = new MyPair();
	MyPair reglaVendedor = new MyPair();
	MyPair reglaVenta = new MyPair();

	// por cada uno, poner los tipos, sólo está para cliente
	MyPair reglaClienteCategoria = new MyPair();
	MyPair reglaClienteRubro = new MyPair();

	MyPair reglaArticuloMarca = new MyPair();
	MyPair reglaArticuloFamilia = new MyPair();
	MyPair reglaArticuloParte = new MyPair();
	MyPair reglaArticuloRubro = new MyPair();

	MyPair reglaVentaModoVenta = new MyPair();
	MyPair reglaVentaSucursales = new MyPair();

	MyPair reglaVendedorRubro = new MyPair();

	// Caja
	List<MyPair> cajaTipos = new ArrayList<MyPair>();
	List<MyPair> cajaClasificaciones = new ArrayList<MyPair>();
	List<MyPair> cajaEstados = new ArrayList<MyPair>();
	List<MyPair> cajaDuraciones = new ArrayList<MyPair>();
	List<MyPair> cajaReposiciones = new ArrayList<MyPair>();
	List<MyPair> cajaReposicionEgresos = new ArrayList<MyPair>();

	MyPair cajaEstadoActivo = new MyPair();
	MyPair cajaTipoMovimientosVarios = new MyPair();

	MyPair cajaReposicion_Ingreso = new MyPair();
	MyPair cajaReposicion_Egreso = new MyPair();
	MyPair cajaReposicionEgreso_SinComprobante = new MyPair();
	MyPair cajaReposicionEgreso_Vale = new MyPair();

	MyPair cajaPeriodoEstadoAbierta = new MyPair();
	MyPair cajaPeriodoEstadoCerrada = new MyPair();
	MyPair cajaPeriodoEstadoProcesada = new MyPair();

	// estado de regla de precio
	List<MyPair> tipoEstadoReglaPrecioVolumen = new ArrayList<MyPair>();
	MyPair tipoEstadoMayor = new MyPair();
	MyPair tipoEstadoMenor = new MyPair();
	MyPair tipoEstadoIgual = new MyPair();
	MyPair tipoEstadoDiferente = new MyPair();
	MyPair tipoEstadoNinguno = new MyPair();

	// Motivos de Nota de Crédito
	List<MyPair> motivosNotaCredito = new ArrayList<MyPair>();
	MyPair motivoNotaCreditoDescuento = new MyPair();
	MyPair motivoNotaCreditoDevolucion = new MyPair();
	MyPair motivoNotaCreditoReclamo = new MyPair();
	MyPair motivoNotaCreditoDifPrecio = new MyPair();

	// Tipos de Detalle Nota de Credito
	MyPair notaCreditoDetalleFactura = new MyPair();
	MyPair notaCreditoDetalleArticulo = new MyPair();

	// Tipos de Reserva
	List<MyPair> tiposDeReserva = new ArrayList<MyPair>();
	MyPair reservaInterna = new MyPair();
	MyPair reservaReparto = new MyPair();
	MyPair reservaVenta = new MyPair();
	MyPair reservaDevolucion = new MyPair();
	// Estados de Reserva
	List<MyPair> estadosDeReserva = new ArrayList<MyPair>();
	MyPair estado_reservaActiva = new MyPair();
	MyPair estado_reservaCancelada = new MyPair();
	MyPair estado_reservaFinalizada = new MyPair();

	// Motivos de Devolucion

	List<MyPair> motivosDeDevolucion = new ArrayList<MyPair>();
	MyPair motivoDevolucionDiaSiguiente = new MyPair();
	MyPair motivoDevolucionEntregaParcial = new MyPair();
	MyPair motivoDevolucionDefectuoso = new MyPair();

	List<MyPair> tiposBancoDeposito = new ArrayList<MyPair>();
	MyPair bancoDepositoEfectivo = new MyPair();
	MyPair bancoDepositoChequesBanco = new MyPair();
	MyPair bancoDepositoChequesOtrosBancos = new MyPair();
	MyPair bancoDepositoTodos = new MyPair();

	List<MyPair> modosDeCreacionCheque = new ArrayList<MyPair>();
	MyPair chequeAutomatico = new MyPair();
	MyPair ChequeManual = new MyPair();

	// Implementacion MRA.
	MyPair MRA_deposito = new MyPair();
	MyPair MRA_modoVenta = new MyPair();
	MyPair MRA_sucursal = new MyPair();
	MyArray MRA_vendedor = new MyArray();
	MyArray MRA_articulo = new MyArray();

	MyPair tipoTarjetaExtractoDetalleTE = new MyPair();
	MyPair tipoTarjetaExtractoDetalleBM = new MyPair();

	/************************** GETTER/SETTER *************************/

	public String getSimboloMonedaLocal() {
		return Configuracion.SIGLA_MONEDA_GUARANI;
	}

	public MyPair getTipoTarjetaExtractoDetalleTE() {
		return tipoTarjetaExtractoDetalleTE;
	}

	public void setTipoTarjetaExtractoDetalleTE(
			MyPair tipoTarjetaExtractoDetalleTE) {
		this.tipoTarjetaExtractoDetalleTE = tipoTarjetaExtractoDetalleTE;
	}

	public MyPair getTipoTarjetaExtractoDetalleBM() {
		return tipoTarjetaExtractoDetalleBM;
	}

	public void setTipoTarjetaExtractoDetalleBM(
			MyPair tipoTarjetaExtractoDetalleBM) {
		this.tipoTarjetaExtractoDetalleBM = tipoTarjetaExtractoDetalleBM;
	}

	public List<MyPair> getTiposBancoDeposito() {
		return tiposBancoDeposito;
	}

	public void setTiposBancoDeposito(List<MyPair> tiposBancoDeposito) {
		this.tiposBancoDeposito = tiposBancoDeposito;
	}

	public MyPair getBancoDepositoEfectivo() {
		return bancoDepositoEfectivo;
	}

	public void setBancoDepositoEfectivo(MyPair bancoDepositoEfectivo) {
		this.bancoDepositoEfectivo = bancoDepositoEfectivo;
	}

	public MyPair getBancoDepositoChequesBanco() {
		return bancoDepositoChequesBanco;
	}

	public void setBancoDepositoChequesBanco(MyPair bancoDepositoChequesBanco) {
		this.bancoDepositoChequesBanco = bancoDepositoChequesBanco;
	}

	public MyPair getBancoDepositoChequesOtrosBancos() {
		return bancoDepositoChequesOtrosBancos;
	}

	public void setBancoDepositoChequesOtrosBancos(
			MyPair bancoDepositoChequesOtrosBancos) {
		this.bancoDepositoChequesOtrosBancos = bancoDepositoChequesOtrosBancos;
	}

	public MyPair getBancoDepositoTodos() {
		return bancoDepositoTodos;
	}

	public void setBancoDepositoTodos(MyPair bancoDepositoTodos) {
		this.bancoDepositoTodos = bancoDepositoTodos;
	}

	public HashMap<Long, MyArray> getSucPorDep() {
		return sucPorDep;
	}

	public MyPair getSinDefinir() {
		return sinDefinir;
	}

	public void setSinDefinir(MyPair sinDefinir) {
		this.sinDefinir = sinDefinir;
	}

	public MyArray getSucursalPorDeposito(MyPair dep) {
		return (MyArray) this.sucPorDep.get(dep.getId());
	}

	public MyArray getSucursalPorDepositoArr(MyArray dep) {
		return (MyArray) this.sucPorDep.get(dep.getId());
	}

	public void setSucPorDep(HashMap<Long, MyArray> sucPorDep) {
		this.sucPorDep = sucPorDep;
	}

	/*
	 * public List<MyPair> getMarcaModeloAplicacion(long idMarca){ List<MyPair>
	 * lmp = new ArrayList<MyPair>(); List<MyArray> lma =
	 * this.getArticuloMarcaAplicacion(); for (int i = 0; i < lma.size(); i++) {
	 * MyArray ma = lma.get(i); if (ma.getId() == idMarca){ return
	 * (List<MyPair>)ma.getPos2(); } } return lmp; }
	 */

	public List<MyPair> getTransportes() {
		return transportes;
	}

	public void setTransportes(List<MyPair> transportes) {
		this.transportes = transportes;
	}

	public List<MyArray> getDepartamentos() {
		return departamentos;
	}

	public List<MyArray> getArticuloModeloAplicacion() {
		return articuloModeloAplicacion;
	}

	public void setArticuloModeloAplicacion(
			List<MyArray> articuloModeloAplicacion) {
		this.articuloModeloAplicacion = articuloModeloAplicacion;
	}

	public void setDepartamentos(List<MyArray> departamentos) {
		this.departamentos = departamentos;
	}

	public List<MyArray> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<MyArray> sucursales) {
		this.sucursales = sucursales;
	}

	public List<MyPair> getSucursalesMyPair() {
		return sucursalesMyPair;
	}

	public void setSucursalesMyPair(List<MyPair> sucursalesMyPair) {
		this.sucursalesMyPair = sucursalesMyPair;
	}

	public List<MyArray> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<MyArray> usuarios) {
		this.usuarios = usuarios;
	}

	public List<MyPair> getSucursalesAppSeleccion() {
		return sucursalesAppSeleccion;
	}

	public void setSucursalesAppSeleccion(List<MyPair> sucursalesAppSeleccion) {
		this.sucursalesAppSeleccion = sucursalesAppSeleccion;
	}

	public MyPair getSucursalAppTodas() {
		return sucursalAppTodas;
	}

	public void setSucursalAppTodas(MyPair sucursalAppTodas) {
		this.sucursalAppTodas = sucursalAppTodas;
	}

	public List<MyPair> getArticuloEstado() {
		return articuloEstado;
	}

	public List<MyPair> getFuncionarioEstado() {
		return this.funcionarioEstado;
	}

	public void setFuncionarioEstado(List<MyPair> funcionarioEstado) {
		this.funcionarioEstado = funcionarioEstado;
	}

	public List<MyPair> getFuncionarioCargo() {
		return funcionarioCargo;
	}

	public void setFuncionarioCargo(List<MyPair> funcionarioCargo) {
		this.funcionarioCargo = funcionarioCargo;
	}

	public List<MyArray> getCondicionesPago() {
		return condicionesPago;
	}

	public void setCondicionesPago(List<MyArray> condicionesPago) {
		this.condicionesPago = condicionesPago;
	}

	public void setArticuloEstado(List<MyPair> articuloEstado) {
		this.articuloEstado = articuloEstado;
	}

	public List<MyPair> getArticuloFamilia() {
		return articuloFamilia;
	}

	public void setArticuloFamilia(List<MyPair> articuloFamilia) {
		this.articuloFamilia = articuloFamilia;
	}

	public List<MyPair> getArticuloLinea() {
		return articuloLinea;
	}

	public void setArticuloLinea(List<MyPair> articuloLinea) {
		this.articuloLinea = articuloLinea;
	}

	public List<MyPair> getArticuloMarca() {
		return articuloMarca;
	}

	public void setArticuloMarca(List<MyPair> articuloMarca) {
		this.articuloMarca = articuloMarca;
	}

	public List<MyArray> getArticuloMarcaAplicacion() {
		return articuloMarcaAplicacion;
	}

	public void setArticuloMarcaAplicacion(List<MyArray> articuloMarcaAplicacion) {
		this.articuloMarcaAplicacion = articuloMarcaAplicacion;
	}

	public List<MyPair> getArticuloParte() {
		return articuloParte;
	}

	public void setArticuloParte(List<MyPair> articuloParte) {
		this.articuloParte = articuloParte;
	}

	public List<MyPair> getArticuloUnidadMedida() {
		return articuloUnidadMedida;
	}

	public void setArticuloUnidadMedida(List<MyPair> articuloUnidadMedida) {
		this.articuloUnidadMedida = articuloUnidadMedida;
	}

	public List<MyPair> getCategoriaCliente() {
		return categoriaCliente;
	}

	public void setCategoriaCliente(List<MyPair> categoriaCliente) {
		this.categoriaCliente = categoriaCliente;
	}

	public List<MyPair> getContactoSexo() {
		return contactoSexo;
	}

	public void setContactoSexo(List<MyPair> contactoSexo) {
		this.contactoSexo = contactoSexo;
	}

	public List<MyPair> getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(List<MyPair> estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public List<MyPair> getEstadoCliente() {
		return estadoCliente;
	}

	public void setEstadoCliente(List<MyPair> estadoCliente) {
		this.estadoCliente = estadoCliente;
	}

	public List<MyPair> getProfesion() {
		return profesion;
	}

	public void setProfesion(List<MyPair> profesion) {
		this.profesion = profesion;
	}

	public List<MyPair> getProveedorEstado() {
		return proveedorEstado;
	}

	public void setProveedorEstado(List<MyPair> proveedorEstado) {
		this.proveedorEstado = proveedorEstado;
	}

	public List<MyPair> getProveedorTipo() {
		return proveedorTipo;
	}

	public void setProveedorTipo(List<MyPair> proveedorTipo) {
		this.proveedorTipo = proveedorTipo;
	}

	public List<MyPair> getProveedorTipoPago() {
		return proveedorTipoPago;
	}

	public void setProveedorTipoPago(List<MyPair> proveedorTipoPago) {
		this.proveedorTipoPago = proveedorTipoPago;
	}

	public List<MyPair> getRubroEmpresa() {
		return rubroEmpresa;
	}

	public void setRubroEmpresa(List<MyPair> rubroEmpresa) {
		this.rubroEmpresa = rubroEmpresa;
	}

	public List<MyPair> getTipoCliente() {
		return tipoCliente;
	}

	public void setTipoCliente(List<MyPair> tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	public List<MyPair> getTipoContactoInterno() {
		return tipoContactoInterno;
	}

	public void setTipoContactoInterno(List<MyPair> tipoContactoInterno) {
		this.tipoContactoInterno = tipoContactoInterno;
	}

	public List<MyArray> getArticuloPresentacion() {
		return articuloPresentacion;
	}

	public void setArticuloPresentacion(List<MyArray> articuloPresentacion) {
		this.articuloPresentacion = articuloPresentacion;
	}

	public List<MyPair> getImportacionEstados() {
		return importacionEstados;
	}

	public void setImportacionEstados(List<MyPair> importacionEstados) {
		this.importacionEstados = importacionEstados;
	}

	public MyPair getMonedaGuarani() {
		return monedaGuarani;
	}

	public void setMonedaGuarani(MyPair monedaGuarani) {
		this.monedaGuarani = monedaGuarani;
	}

	public MyArray getMonedaGuaraniConSimbolo() {
		return monedaGuaraniConSimbolo;
	}

	public void setMonedaGuaraniConSimbolo(MyArray monedaGuaraniConSimbolo) {
		this.monedaGuaraniConSimbolo = monedaGuaraniConSimbolo;
	}

	public List<MyArray> getMonedasConSimbolo() {
		return monedasConSimbolo;
	}

	public void setMonedasConSimbolo(List<MyArray> monedasConSimbolo) {
		this.monedasConSimbolo = monedasConSimbolo;
	}

	public List<MyPair> getImportado() {
		return importado;
	}

	public void setImportado(List<MyPair> importado) {
		this.importado = importado;
	}

	public List<MyArray> getCtaCteLineaCredito() {
		return ctaCteLineaCredito;
	}

	public void setCtaCteLineaCredito(List<MyArray> ctaCteLineaCredito) {
		this.ctaCteLineaCredito = ctaCteLineaCredito;
	}

	public List<MyPair> getCtaCteEstado() {
		return ctaCteEstado;
	}

	public void setCtaCteEstado(List<MyPair> ctaCteEstado) {
		this.ctaCteEstado = ctaCteEstado;
	}

	public List<MyPair> getCtaCteTipoOperacion() {
		return ctaCteTipoOperacion;
	}

	public void setCtaCteTipoOperacion(List<MyPair> ctaCteTipoOperacion) {
		this.ctaCteTipoOperacion = ctaCteTipoOperacion;
	}

	public List<MyPair> getCtaCteEmpresaEstado() {
		return ctaCteEmpresaEstado;
	}

	public void setCtaCteEmpresaEstado(List<MyPair> ctaCteEmpresaEstado) {
		this.ctaCteEmpresaEstado = ctaCteEmpresaEstado;
	}

	public List<MyPair> getCtaCteEmpresaSeleccionMov() {
		return this.ctaCteEmpresaSeleccionMov;
	}

	public void setCtaCteEmpresaSeleccionMov(
			List<MyPair> ctaCteEmpresaSeleccionMov) {
		this.ctaCteEmpresaSeleccionMov = ctaCteEmpresaSeleccionMov;
	}

	public MyArray getCtaCteLineaCreditoDefault() {
		return this.ctaCteLineaCreditoDefault;
	}

	public MyPair getCtaCteImputacionParcial() {
		return ctaCteImputacionParcial;
	}

	public void setCtaCteImputacionParcial(MyPair ctaCteImputacionParcial) {
		this.ctaCteImputacionParcial = ctaCteImputacionParcial;
	}

	public MyPair getCtaCteImputacionCompleta() {
		return ctaCteImputacionCompleta;
	}

	public void setCtaCteImputacionCompleta(MyPair ctaCteImputacionCompleta) {
		this.ctaCteImputacionCompleta = ctaCteImputacionCompleta;
	}

	public MyPair getCtaCteEmpresaCaracterMovProveedor() {
		return ctaCteEmpresaCaracterMovProveedor;
	}

	public void setCtaCteEmpresaCaracterMovProveedor(
			MyPair ctaCteEmpresaCaracterMovProveedor) {
		this.ctaCteEmpresaCaracterMovProveedor = ctaCteEmpresaCaracterMovProveedor;
	}

	public MyPair getCtaCteEmpresaCaracterMovCliente() {
		return ctaCteEmpresaCaracterMovCliente;
	}

	public void setCtaCteEmpresaCaracterMovCliente(
			MyPair ctaCteEmpresaCaracterMovCliente) {
		this.ctaCteEmpresaCaracterMovCliente = ctaCteEmpresaCaracterMovCliente;
	}

	public MyPair getCtaCteEmpresaEstadoActivo() {
		return ctaCteEmpresaEstadoActivo;
	}

	public void setCtaCteEmpresaEstadoActivo(MyPair ctaCteEmpresaEstadoActivo) {
		this.ctaCteEmpresaEstadoActivo = ctaCteEmpresaEstadoActivo;
	}

	public MyPair getCtaCteEmpresaEstadoInactivo() {
		return ctaCteEmpresaEstadoInactivo;
	}

	public void setCtaCteEmpresaEstadoInactivo(
			MyPair ctaCteEmpresaEstadoInactivo) {
		this.ctaCteEmpresaEstadoInactivo = ctaCteEmpresaEstadoInactivo;
	}

	public MyPair getCtaCteEmpresaEstadoBloqueado() {
		return ctaCteEmpresaEstadoBloqueado;
	}

	public void setCtaCteEmpresaEstadoBloqueado(
			MyPair ctaCteEmpresaEstadoBloqueado) {
		this.ctaCteEmpresaEstadoBloqueado = ctaCteEmpresaEstadoBloqueado;
	}

	public MyPair getCtaCteEmpresaEstadoSinCuenta() {
		return ctaCteEmpresaEstadoSinCuenta;
	}

	public void setCtaCteEmpresaEstadoSinCuenta(
			MyPair ctaCteEmpresaEstadoSinCuenta) {
		this.ctaCteEmpresaEstadoSinCuenta = ctaCteEmpresaEstadoSinCuenta;
	}

	public void setCtaCteLineaCreditoDefault(MyArray ctaCteLineaCreditoDefault) {
		this.ctaCteLineaCreditoDefault = ctaCteLineaCreditoDefault;
	}

	public List<MyPair> getCajaTipos() {
		return cajaTipos;
	}

	public void setCajaTipos(List<MyPair> cajaPagoTipos) {
		this.cajaTipos = cajaPagoTipos;
	}

	public List<MyPair> getCajaClasificaciones() {
		return cajaClasificaciones;
	}

	public void setCajaClasificaciones(List<MyPair> cajaPagoClasificaciones) {
		this.cajaClasificaciones = cajaPagoClasificaciones;
	}

	public List<MyPair> getCajaEstados() {
		return cajaEstados;
	}

	public void setCajaEstados(List<MyPair> cajaPagoEstados) {
		this.cajaEstados = cajaPagoEstados;
	}

	public List<MyPair> getCajaDuraciones() {
		return cajaDuraciones;
	}

	public void setCajaDuraciones(List<MyPair> cajaPagoDuraciones) {
		this.cajaDuraciones = cajaPagoDuraciones;
	}

	public List<MyPair> getReciboEstados() {
		return reciboEstados;
	}

	public void setReciboEstados(List<MyPair> ordenPagoEstados) {
		this.reciboEstados = ordenPagoEstados;
	}

	/*
	 * public List<MyArray> getDepositos() { return depositos; }
	 * 
	 * public void setDepositos(List<MyArray> depositos) { this.depositos =
	 * depositos; }
	 */

	public MyPair getReciboEstadoGuardado() {
		return reciboEstadoGuardado;
	}

	public void setReciboEstadoGuardado(MyPair ordenPagoEstadoGuardado) {
		this.reciboEstadoGuardado = ordenPagoEstadoGuardado;
	}

	public MyPair getReciboEstadoAnulado() {
		return reciboEstadoAnulado;
	}

	public void setReciboEstadoAnulado(MyPair ordenPagoEstadoAnulado) {
		this.reciboEstadoAnulado = ordenPagoEstadoAnulado;
	}

	public MyPair getTipoTransferenciaInterna() {
		return tipoTransferenciaInterna;
	}

	public void setTipoTransferenciaInterna(MyPair tipoTransferenciaInterna) {
		this.tipoTransferenciaInterna = tipoTransferenciaInterna;
	}

	public MyPair getTipoTransferenciaExterna() {
		return tipoTransferenciaExterna;
	}

	public void setTipoTransferenciaExterna(MyPair tipoTransferenciaRemision) {
		this.tipoTransferenciaExterna = tipoTransferenciaRemision;
	}

	public MyPair getEstadoTransferenciaElaboracion() {
		return estadoTransferenciaElaboracion;
	}

	public void setEstadoTransferenciaElaboracion(
			MyPair estadoTransferenciaElaboracion) {
		this.estadoTransferenciaElaboracion = estadoTransferenciaElaboracion;
	}

	public MyPair getEstadoTransferenciaPedido() {
		return estadoTransferenciaPedido;
	}

	public void setEstadoTransferenciaPedido(MyPair estadoTransferenciaPedido) {
		this.estadoTransferenciaPedido = estadoTransferenciaPedido;
	}

	public MyPair getEstadoTransferenciaPreparado() {
		return estadoTransferenciaPreparado;
	}

	public void setEstadoTransferenciaPreparado(
			MyPair estadoTransferenciaPreparado) {
		this.estadoTransferenciaPreparado = estadoTransferenciaPreparado;
	}

	public MyPair getEstadoTransferenciaConfirmada() {
		return estadoTransferenciaConfirmada;
	}

	public void setEstadoTransferenciaConfirmada(MyPair estadoTransferenciaEnviada) {
		this.estadoTransferenciaConfirmada = estadoTransferenciaEnviada;
	}

	public MyPair getEstadoTransferenciaRecibida() {
		return estadoTransferenciaRecibida;
	}

	public void setEstadoTransferenciaRecibida(
			MyPair estadoTransferenciaRecibida) {
		this.estadoTransferenciaRecibida = estadoTransferenciaRecibida;
	}

	public MyPair getEstadoTransferenciaUbicada() {
		return estadoTransferenciaUbicada;
	}

	public void setEstadoTransferenciaUbicada(MyPair estadoTransferenciaUbicada) {
		this.estadoTransferenciaUbicada = estadoTransferenciaUbicada;
	}

	public MyPair getEstadoTransferenciaCancelada() {
		return estadoTransferenciaCancelada;
	}

	public void setEstadoTransferenciaCancelada(
			MyPair estadoTransferenciaCancelada) {
		this.estadoTransferenciaCancelada = estadoTransferenciaCancelada;
	}

	public String getEstadoReservaActiva() {
		return estadoReservaActiva;
	}

	public void setEstadoReservaActiva(String estadoReservaActiva) {
		this.estadoReservaActiva = estadoReservaActiva;
	}

	public String getEstadoReservaCancelada() {
		return estadoReservaCancelada;
	}

	public void setEstadoReservaCancelada(String estadoReservaCancelada) {
		this.estadoReservaCancelada = estadoReservaCancelada;
	}

	public String getEstadoReservaFinalizada() {
		return estadoReservaFinalizada;
	}

	public void setEstadoReservaFinalizada(String estadoReservaFinalizada) {
		this.estadoReservaFinalizada = estadoReservaFinalizada;
	}

	public String getEstadoReservaEnReparto() {
		return estadoReservaEnReparto;
	}

	public void setEstadoReservaEnReparto(String estadoReservaEnReparto) {
		this.estadoReservaEnReparto = estadoReservaEnReparto;
	}

	public String getTipoReservaInterna() {
		return tipoReservaInterna;
	}

	public void setTipoReservaInterna(String tipoReservaInterna) {
		this.tipoReservaInterna = tipoReservaInterna;
	}

	public String getTipoReservaRemision() {
		return tipoReservaRemision;
	}

	public void setTipoReservaRemision(String tipoReservaRemision) {
		this.tipoReservaRemision = tipoReservaRemision;
	}

	public List<MyPair> getDepositosMyPair() {
		return depositosMyPair;
	}

	public void setDepositosMyPair(List<MyPair> depositosMyPair) {
		this.depositosMyPair = depositosMyPair;
	}

	public List<MyArray> getBancos() {
		return bancos;
	}

	public void setBancos(List<MyArray> bancos) {
		this.bancos = bancos;
	}
	
	public List<MyArray> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(List<MyArray> funcionarios) {
		this.funcionarios = funcionarios;
	}

	public List<MyArray> getFuncionariosMyPair() {
		return funcionariosMyPair;
	}

	public void setFuncionariosMyPair(List<MyArray> funcionariosMyPair) {
		this.funcionariosMyPair = funcionariosMyPair;
	}

	public MyPair getEstadoRepartoPreparacion() {
		return estadoRepartoPreparacion;
	}

	public void setEstadoRepartoPreparacion(MyPair estadoRepartoPreparacion) {
		this.estadoRepartoPreparacion = estadoRepartoPreparacion;
	}

	public MyPair getEstadoRepartoEntregado() {
		return estadoRepartoEntregado;
	}

	public MyPair getEstadoRepartoEnTransito() {
		return estadoRepartoEnTransito;
	}

	public void setEstadoRepartoEnTransito(MyPair estadoRepartoEnTransito) {
		this.estadoRepartoEnTransito = estadoRepartoEnTransito;
	}

	public void setEstadoRepartoEntregado(MyPair estadoRepartoFinalizado) {
		this.estadoRepartoEntregado = estadoRepartoFinalizado;
	}

	public List<MyPair> getRepartoTipos() {
		return repartoTipos;
	}

	public void setRepartoTipos(List<MyPair> repartoTipos) {
		this.repartoTipos = repartoTipos;
	}

	public MyPair getTipoRepartoTerminal() {
		return tipoRepartoTerminal;
	}

	public void setTipoRepartoTerminal(MyPair tipoRepartoTerminal) {
		this.tipoRepartoTerminal = tipoRepartoTerminal;
	}

	public MyPair getTipoRepartoColectivo() {
		return tipoRepartoColectivo;
	}

	public void setTipoRepartoColectivo(MyPair tipoRepartoColectivo) {
		this.tipoRepartoColectivo = tipoRepartoColectivo;
	}

	public MyPair getTipoRepartoEncomienda() {
		return tipoRepartoEncomienda;
	}

	public void setTipoRepartoEncomienda(MyPair tipoRepartoEncomienda) {
		this.tipoRepartoEncomienda = tipoRepartoEncomienda;
	}

	public MyPair getTipoRepartoYhaguy() {
		return tipoRepartoYhaguy;
	}

	public void setTipoRepartoYhaguy(MyPair tipoRepartoYhaguy) {
		this.tipoRepartoYhaguy = tipoRepartoYhaguy;
	}

	public List<MyPair> getCompraLocalGastos() {
		return compraLocalGastos;
	}

	public void setCompraLocalGastos(List<MyPair> compraLocalGastos) {
		this.compraLocalGastos = compraLocalGastos;
	}

	public List<MyPair> getPaisEmpresas() {
		return paisEmpresas;
	}

	public void setPaisEmpresas(List<MyPair> paisEmpresas) {
		this.paisEmpresas = paisEmpresas;
	}

	public List<MyPair> getEmailEmpresa() {
		return emailEmpresa;
	}

	public void setEmailEmpresa(List<MyPair> emailEmpresa) {
		this.emailEmpresa = emailEmpresa;
	}

	public List<MyPair> getTipoPersonas() {
		return tipoPersonas;
	}

	public void setTipoPersonas(List<MyPair> tipoPersonas) {
		this.tipoPersonas = tipoPersonas;
	}

	public MyArray getCuentaProveedoresVarios() {
		return cuentaProveedoresVarios;
	}

	public void setCuentaProveedoresVarios(MyArray cuentaProveedoresVarios) {
		this.cuentaProveedoresVarios = cuentaProveedoresVarios;
	}

	public MyArray getCuentaClientesOcasionales() {
		return cuentaClientesOcasionales;
	}

	public void setCuentaClientesOcasionales(MyArray cuentaClientesOcasionales) {
		this.cuentaClientesOcasionales = cuentaClientesOcasionales;
	}

	public List<MyPair> getMonedas() {
		return monedas;
	}

	public void setMonedas(List<MyPair> monedas) {
		this.monedas = monedas;
	}

	public List<MyPair> getBancoCtaTipos() {
		return bancoCtaTipos;
	}

	public void setBancoCtaTipos(List<MyPair> bancoCtaTipos) {
		this.bancoCtaTipos = bancoCtaTipos;
	}

	public List<MyPair> getCompraTiposDescuento() {
		return compraTiposDescuento;
	}

	public void setCompraTiposDescuento(List<MyPair> compraTiposDescuento) {
		this.compraTiposDescuento = compraTiposDescuento;
	}

	public MyPair getMovEstadoRepartoPendiente() {
		return movEstadoRepartoPendiente;
	}

	public void setMovEstadoRepartoPendiente(MyPair movEstadoRepartoPendiente) {
		this.movEstadoRepartoPendiente = movEstadoRepartoPendiente;
	}

	public void setMovEstadoRepartoFinalizado(MyPair movEstadoRepartoFinalizado) {
		this.movEstadoRepartoFinalizado = movEstadoRepartoFinalizado;
	}

	public MyPair getMovEstadoRepartoPreparacion() {
		return movEstadoRepartoPreparacion;
	}

	public void setMovEstadoRepartoPreparacion(
			MyPair movEstadoRepartoPreparacion) {
		this.movEstadoRepartoPreparacion = movEstadoRepartoPreparacion;
	}

	public MyPair getMovEstadoRepartoPreparado() {
		return movEstadoRepartoPreparado;
	}

	public void setMovEstadoRepartoPreparado(MyPair movEstadoRepartoPreparado) {
		this.movEstadoRepartoPreparado = movEstadoRepartoPreparado;
	}

	public MyPair getMovEstadoRepartoEnTransito() {
		return movEstadoRepartoEnTransito;
	}

	public void setMovEstadoRepartoEnTransito(MyPair movEstadoRepartoEnTransito) {
		this.movEstadoRepartoEnTransito = movEstadoRepartoEnTransito;
	}

	public MyPair getMovEstadoRepartoFinalizado() {
		return movEstadoRepartoFinalizado;
	}

	public List<MyPair> getCompraTiposGastos() {
		return compraTiposGastos;
	}

	public void setCompraTiposGastos(List<MyPair> compraTiposGastos) {
		this.compraTiposGastos = compraTiposGastos;
	}

	public MyPair getCajaEstadoActivo() {
		return cajaEstadoActivo;
	}

	public void setCajaEstadoActivo(MyPair cajaPagoEstadoActivo) {
		this.cajaEstadoActivo = cajaPagoEstadoActivo;
	}

	public MyArray getCondicionPagoContado() {
		return condicionPagoContado;
	}

	public void setCondicionPagoContado(MyArray condicionPagoContado) {
		this.condicionPagoContado = condicionPagoContado;
	}

	public MyArray getCondicionPagoCredito30() {
		return condicionPagoCredito30;
	}

	public void setCondicionPagoCredito30(MyArray condicionPagoCredito30) {
		this.condicionPagoCredito30 = condicionPagoCredito30;
	}

	public MyArray getCondicionPagoCredito60() {
		return condicionPagoCredito60;
	}

	public void setCondicionPagoCredito60(MyArray condicionPagoCredito60) {
		this.condicionPagoCredito60 = condicionPagoCredito60;
	}

	public MyArray getCondicionPagoCredito90() {
		return condicionPagoCredito90;
	}

	public void setCondicionPagoCredito90(MyArray condicionPagoCredito90) {
		this.condicionPagoCredito90 = condicionPagoCredito90;
	}

	public List<MyArray> getTiposDeIva() {
		return tiposDeIva;
	}

	public void setTiposDeIva(List<MyArray> tiposDeIva) {
		this.tiposDeIva = tiposDeIva;
	}

	public MyArray getTipoIva10() {
		return tipoIva10;
	}

	public void setTipoIva10(MyArray tipoIva10) {
		this.tipoIva10 = tipoIva10;
	}

	public MyArray getTipoIva5() {
		return tipoIva5;
	}

	public void setTipoIva5(MyArray tipoIva5) {
		this.tipoIva5 = tipoIva5;
	}

	public MyArray getTipoIvaExento() {
		return tipoIvaExento;
	}

	public void setTipoIvaExento(MyArray tipoIvaExento) {
		this.tipoIvaExento = tipoIvaExento;
	}

	public List<MyPair> getGruposDeEmpresa() {
		return gruposDeEmpresa;
	}

	public void setGruposDeEmpresa(List<MyPair> gruposDeEmpresa) {
		this.gruposDeEmpresa = gruposDeEmpresa;
	}

	public List<MyPair> getRegimenesTributarios() {
		return regimenesTributarios;
	}

	public void setRegimenesTributarios(List<MyPair> regimenesTributarios) {
		this.regimenesTributarios = regimenesTributarios;
	}

	public List<MyPair> getZonas() {
		return zonas;
	}

	public void setZonas(List<MyPair> zonas) {
		this.zonas = zonas;
	}

	public MyPair getGrupoEmpresaNoDefinido() {
		return grupoEmpresaNoDefinido;
	}

	public void setGrupoEmpresaNoDefinido(MyPair grupoEmpresaNoDefinido) {
		this.grupoEmpresaNoDefinido = grupoEmpresaNoDefinido;
	}

	public MyPair getRegimenTributarioNoExenta() {
		return regimenTributarioNoExenta;
	}

	public void setRegimenTributarioNoExenta(MyPair regimenTributarioNoExenta) {
		this.regimenTributarioNoExenta = regimenTributarioNoExenta;
	}

	public MyPair getEstadoClienteActivo() {
		return estadoClienteActivo;
	}

	public void setEstadoClienteActivo(MyPair estadoClienteActivo) {
		this.estadoClienteActivo = estadoClienteActivo;
	}

	public MyPair getTipoClienteMinorista() {
		return tipoClienteMinorista;
	}

	public void setTipoClienteMinorista(MyPair tipoClienteMinorista) {
		this.tipoClienteMinorista = tipoClienteMinorista;
	}

	public MyPair getCategoriaClienteAldia() {
		return categoriaClienteAldia;
	}

	public void setCategoriaClienteAldia(MyPair categoriaClienteAldia) {
		this.categoriaClienteAldia = categoriaClienteAldia;
	}

	public MyPair getTipoPersonaFisica() {
		return tipoPersonaFisica;
	}

	public void setTipoPersonaFisica(MyPair tipoPersonaFisica) {
		this.tipoPersonaFisica = tipoPersonaFisica;
	}

	public MyPair getTipoPersonaJuridica() {
		return tipoPersonaJuridica;
	}

	public void setTipoPersonaJuridica(MyPair tipoPersonaJuridica) {
		this.tipoPersonaJuridica = tipoPersonaJuridica;
	}

	public MyPair getPaisParaguay() {
		return paisParaguay;
	}

	public void setPaisParaguay(MyPair paisParaguay) {
		this.paisParaguay = paisParaguay;
	}

	public MyPair getImportacionEstadoConfirmado() {
		return importacionEstadoConfirmado;
	}

	public void setImportacionEstadoConfirmado(MyPair importacionEstado1) {
		this.importacionEstadoConfirmado = importacionEstado1;
	}

	public MyPair getImportacionEstadoSolicitandoCotizacion() {
		return importacionEstadoSolicitandoCotizacion;
	}

	public void setImportacionEstadoSolicitandoCotizacion(
			MyPair importacionEstado2) {
		this.importacionEstadoSolicitandoCotizacion = importacionEstado2;
	}

	public MyPair getImportacionEstadoProformaRecibida() {
		return importacionEstadoProformaRecibida;
	}

	public void setImportacionEstadoProformaRecibida(MyPair importacionEstado3) {
		this.importacionEstadoProformaRecibida = importacionEstado3;
	}

	public MyPair getImportacionEstadoPendienteEnvio() {
		return importacionEstadoPendienteEnvio;
	}

	public void setImportacionEstadoPendienteEnvio(MyPair importacionEstado4) {
		this.importacionEstadoPendienteEnvio = importacionEstado4;
	}

	public MyPair getImportacionEstadoPedidoEnviado() {
		return importacionEstadoPedidoEnviado;
	}

	public void setImportacionEstadoPedidoEnviado(MyPair importacionEstado5) {
		this.importacionEstadoPedidoEnviado = importacionEstado5;
	}

	public MyPair getImportacionEstadoCerrado() {
		return importacionEstadoCerrado;
	}

	public void setImportacionEstadoCerrado(MyPair importacionEstado6) {
		this.importacionEstadoCerrado = importacionEstado6;
	}

	public List<MyArray> getEstadosVenta() {
		return estadosVenta;
	}

	public void setEstadosVenta(List<MyArray> estadosPedidoVenta) {
		this.estadosVenta = estadosPedidoVenta;
	}

	public MyArray getEstadoVenta_soloPresupuesto() {
		return estadoVenta_soloPresupuesto;
	}

	public void setEstadoVenta_soloPresupuesto(MyArray estadoPedidoVenta1) {
		this.estadoVenta_soloPresupuesto = estadoPedidoVenta1;
	}

	public MyArray getEstadoVenta_pasadoApedido() {
		return estadoVenta_pasadoApedido;
	}

	public void setEstadoVenta_pasadoApedido(MyArray estadoPedidoVenta2) {
		this.estadoVenta_pasadoApedido = estadoPedidoVenta2;
	}

	public MyArray getEstadoVenta_cerrado() {
		return estadoVenta_cerrado;
	}

	public void setEstadoVenta_cerrado(MyArray estadoPedidoVenta3) {
		this.estadoVenta_cerrado = estadoPedidoVenta3;
	}

	public MyArray getEstadoVenta_facturado() {
		return estadoVenta_facturado;
	}

	public void setEstadoVenta_facturado(MyArray estadoPedidoVenta4) {
		this.estadoVenta_facturado = estadoPedidoVenta4;
	}

	public MyPair getProveedorEstadoActivo() {
		return proveedorEstadoActivo;
	}

	public void setProveedorEstadoActivo(MyPair proveedorEstadoActivo) {
		this.proveedorEstadoActivo = proveedorEstadoActivo;
	}

	public MyPair getProveedorTipoLocal() {
		return proveedorTipoLocal;
	}

	public void setProveedorTipoLocal(MyPair proveedorTipoLocal) {
		this.proveedorTipoLocal = proveedorTipoLocal;
	}

	public MyPair getProveedorTipoPagoContado() {
		return proveedorTipoPagoContado;
	}

	public void setProveedorTipoPagoContado(MyPair proveedorTipoPagoContado) {
		this.proveedorTipoPagoContado = proveedorTipoPagoContado;
	}

	public MyPair getArticuloEstadoActivo() {
		return articuloEstadoActivo;
	}

	public void setArticuloEstadoActivo(MyPair articuloEstadoActivo) {
		this.articuloEstadoActivo = articuloEstadoActivo;
	}

	public MyPair getTipoClienteOcasional() {
		return tipoClienteOcasional;
	}

	public void setTipoClienteOcasional(MyPair tipoClienteOcasional) {
		this.tipoClienteOcasional = tipoClienteOcasional;
	}

	public List<MyPair> getTiposImportacion() {
		return tiposImportacion;
	}

	public void setTiposImportacion(List<MyPair> tiposImportacion) {
		this.tiposImportacion = tiposImportacion;
	}

	public MyPair getTipoImportacionFOB() {
		return tipoImportacionFOB;
	}

	public void setTipoImportacionFOB(MyPair tipoImportacionFOB) {
		this.tipoImportacionFOB = tipoImportacionFOB;
	}

	public MyPair getTipoImportacionCIF() {
		return tipoImportacionCIF;
	}

	public void setTipoImportacionCIF(MyPair tipoImportacionCIF) {
		this.tipoImportacionCIF = tipoImportacionCIF;
	}

	public MyPair getTipoCompraGastoFlete() {
		return tipoCompraGastoFlete;
	}

	public void setTipoCompraGastoFlete(MyPair tipoCompraGastoFlete) {
		this.tipoCompraGastoFlete = tipoCompraGastoFlete;
	}

	public MyPair getTipoCompraGastoSeguro() {
		return tipoCompraGastoSeguro;
	}

	public void setTipoCompraGastoSeguro(MyPair tipoCompraGastoSeguro) {
		this.tipoCompraGastoSeguro = tipoCompraGastoSeguro;
	}

	public MyPair getTipoImportacionCF() {
		return tipoImportacionCF;
	}

	public void setTipoImportacionCF(MyPair tipoImportacionCF) {
		this.tipoImportacionCF = tipoImportacionCF;
	}

	public List<MyPair> getCompraTiposProrrateo() {
		return compraTiposProrrateo;
	}

	public void setCompraTiposProrrateo(List<MyPair> compraTiposProrrateo) {
		this.compraTiposProrrateo = compraTiposProrrateo;
	}

	public MyPair getTipoCompraProrrateoFlete() {
		return tipoCompraProrrateoFlete;
	}

	public void setTipoCompraProrrateoFlete(MyPair tipoCompraProrrateoFlete) {
		this.tipoCompraProrrateoFlete = tipoCompraProrrateoFlete;
	}

	public MyPair getTipoCompraProrrateoSeguro() {
		return tipoCompraProrrateoSeguro;
	}

	public void setTipoCompraProrrateoSeguro(MyPair tipoCompraProrrateoSeguro) {
		this.tipoCompraProrrateoSeguro = tipoCompraProrrateoSeguro;
	}

	public List<MyArray> getMovimientosDeCompra() {
		return movimientosDeCompra;
	}

	public void setMovimientosDeCompra(List<MyArray> movimientosDeCompra) {
		this.movimientosDeCompra = movimientosDeCompra;
	}

	public List<MyArray> getMovimientosDeGasto() {
		return movimientosDeGasto;
	}

	public void setMovimientosDeGasto(List<MyArray> movimientosDeGasto) {
		this.movimientosDeGasto = movimientosDeGasto;
	}

	public List<MyArray> getMovimientosDePago() {
		return movimientosDePago;
	}

	public void setMovimientosDePago(List<MyArray> movimientosDePago) {
		this.movimientosDePago = movimientosDePago;
	}

	public List<MyArray> getMovimientosDeVenta() {
		return movimientosDeVenta;
	}

	public void setMovimientosDeVenta(List<MyArray> movimientosDeVenta) {
		this.movimientosDeVenta = movimientosDeVenta;
	}

	public List<MyArray> getMovimientosDeCobro() {
		return movimientosDeCobro;
	}

	public void setMovimientosDeCobro(List<MyArray> movimientosDeCobro) {
		this.movimientosDeCobro = movimientosDeCobro;
	}

	public List<MyArray> getMovimientosDeRemision() {
		return movimientosDeRemision;
	}

	public void setMovimientosDeRemision(List<MyArray> movimientosDeRemision) {
		this.movimientosDeRemision = movimientosDeRemision;
	}

	public MyArray getTmFacturaCompraContado() {
		return tmFacturaCompraContado;
	}

	public void setTmFacturaCompraContado(MyArray tmFacturaCompraContado) {
		this.tmFacturaCompraContado = tmFacturaCompraContado;
	}

	public MyArray getTmFacturaCompraCredito() {
		return tmFacturaCompraCredito;
	}

	public void setTmFacturaCompraCredito(MyArray tmFacturaCompraCredito) {
		this.tmFacturaCompraCredito = tmFacturaCompraCredito;
	}

	public MyArray getTmFacturaImportacionContado() {
		return tmFacturaImportacionContado;
	}

	public void setTmFacturaImportacionContado(
			MyArray tmFacturaImportacionContado) {
		this.tmFacturaImportacionContado = tmFacturaImportacionContado;
	}

	public MyArray getTmFacturaImportacionCredito() {
		return tmFacturaImportacionCredito;
	}

	public void setTmFacturaImportacionCredito(
			MyArray tmFacturaImportacionCredito) {
		this.tmFacturaImportacionCredito = tmFacturaImportacionCredito;
	}

	public MyArray getTmNotaCreditoCompra() {
		return tmNotaCreditoCompra;
	}

	public void setTmNotaCreditoCompra(MyArray tmNotaCreditoCompra) {
		this.tmNotaCreditoCompra = tmNotaCreditoCompra;
	}

	public MyArray getTmNotaDebitoCompra() {
		return tmNotaDebitoCompra;
	}

	public void setTmNotaDebitoCompra(MyArray tmNotaDebitoCompra) {
		this.tmNotaDebitoCompra = tmNotaDebitoCompra;
	}

	public MyArray getTmFacturaGastoContado() {
		return tmFacturaGastoContado;
	}

	public void setTmFacturaGastoContado(MyArray tmFacturaGastoContado) {
		this.tmFacturaGastoContado = tmFacturaGastoContado;
	}

	public MyArray getTmFacturaGastoCredito() {
		return tmFacturaGastoCredito;
	}

	public void setTmFacturaGastoCredito(MyArray tmFacturaGastoCredito) {
		this.tmFacturaGastoCredito = tmFacturaGastoCredito;
	}

	public MyArray getTmAutoFactura() {
		return tmAutoFactura;
	}

	public void setTmAutoFactura(MyArray tmAutoFactura) {
		this.tmAutoFactura = tmAutoFactura;
	}

	public MyArray getTmReciboPago() {
		return tmReciboPago;
	}

	public void setTmReciboPago(MyArray tmReciboPago) {
		this.tmReciboPago = tmReciboPago;
	}

	public MyArray getTmPagare() {
		return tmPagare;
	}

	public void setTmPagare(MyArray tmPagare) {
		this.tmPagare = tmPagare;
	}

	public MyArray getTmRetencionIva() {
		return tmRetencionIva;
	}

	public void setTmRetencionIva(MyArray tmRetencionIva) {
		this.tmRetencionIva = tmRetencionIva;
	}

	public MyArray getTmAnticipoPago() {
		return tmAnticipoPago;
	}

	public void setTmAnticipoPago(MyArray tmAnticipoPago) {
		this.tmAnticipoPago = tmAnticipoPago;
	}

	public MyArray getTmFacturaVentaContado() {
		return tmFacturaVentaContado;
	}

	public void setTmFacturaVentaContado(MyArray tmFacturaVentaContado) {
		this.tmFacturaVentaContado = tmFacturaVentaContado;
	}

	public MyArray getTmFacturaVentaCredito() {
		return tmFacturaVentaCredito;
	}

	public void setTmFacturaVentaCredito(MyArray tmFacturaVentaCredito) {
		this.tmFacturaVentaCredito = tmFacturaVentaCredito;
	}

	public MyArray getTmNotaCreditoVenta() {
		return tmNotaCreditoVenta;
	}

	public void setTmNotaCreditoVenta(MyArray tmNotaCreditoVenta) {
		this.tmNotaCreditoVenta = tmNotaCreditoVenta;
	}

	public MyArray getTmNotaDebitoVenta() {
		return tmNotaDebitoVenta;
	}

	public void setTmNotaDebitoVenta(MyArray tmNotaDebitoVenta) {
		this.tmNotaDebitoVenta = tmNotaDebitoVenta;
	}

	public MyArray getTmReciboCobro() {
		return tmReciboCobro;
	}

	public void setTmReciboCobro(MyArray tmReciboCobro) {
		this.tmReciboCobro = tmReciboCobro;
	}

	public MyArray getTmNotaRemision() {
		return tmNotaRemision;
	}

	public void setTmNotaRemision(MyArray tmNotaRemision) {
		this.tmNotaRemision = tmNotaRemision;
	}

	public MyPair getEmpresaCliente() {
		return empresaCliente;
	}

	public void setEmpresaCliente(MyPair empresaCliente) {
		this.empresaCliente = empresaCliente;
	}

	public MyPair getEmpresaProveedor() {
		return empresaProveedor;
	}

	public void setEmpresaProveedor(MyPair empresaProveedor) {
		this.empresaProveedor = empresaProveedor;
	}

	public MyPair getEmpresaFuncionario() {
		return empresaFuncionario;
	}

	public void setEmpresaFuncionario(MyPair empresaFuncionario) {
		this.empresaFuncionario = empresaFuncionario;
	}

	public MyArray getTmAnticipoCobro() {
		return tmAnticipoCobro;
	}

	public void setTmAnticipoCobro(MyArray tmAnticipoCobro) {
		this.tmAnticipoCobro = tmAnticipoCobro;
	}

	public MyPair getOperacionCompra() {
		return operacionCompra;
	}

	public void setOperacionCompra(MyPair operacionCompra) {
		this.operacionCompra = operacionCompra;
	}

	public MyPair getOperacionGasto() {
		return operacionGasto;
	}

	public void setOperacionGasto(MyPair operacionGasto) {
		this.operacionGasto = operacionGasto;
	}

	public MyPair getOperacionPago() {
		return operacionPago;
	}

	public void setOperacionPago(MyPair operacionPago) {
		this.operacionPago = operacionPago;
	}

	public MyPair getOperacionVenta() {
		return operacionVenta;
	}

	public void setOperacionVenta(MyPair operacionVenta) {
		this.operacionVenta = operacionVenta;
	}

	public MyPair getOperacionCobro() {
		return operacionCobro;
	}

	public void setOperacionCobro(MyPair operacionCobro) {
		this.operacionCobro = operacionCobro;
	}

	public MyPair getOperacionRemision() {
		return operacionRemision;
	}

	public void setOperacionRemision(MyPair operacionRemision) {
		this.operacionRemision = operacionRemision;
	}

	public MyPair getComprobanteLegal() {
		return comprobanteLegal;
	}

	public void setComprobanteLegal(MyPair comprobanteLegal) {
		this.comprobanteLegal = comprobanteLegal;
	}

	public MyPair getComprobanteInterno() {
		return comprobanteInterno;
	}

	public void setComprobanteInterno(MyPair comprobanteInterno) {
		this.comprobanteInterno = comprobanteInterno;
	}

	public MyArray getTmOrdenCompra() {
		return tmOrdenCompra;
	}

	public void setTmOrdenCompra(MyArray tmOrdenCompra) {
		this.tmOrdenCompra = tmOrdenCompra;
	}

	public MyArray getTmOrdenCompraImportacion() {
		return tmOrdenCompraImportacion;
	}

	public void setTmOrdenCompraImportacion(MyArray tmOrdenCompraImportacion) {
		this.tmOrdenCompraImportacion = tmOrdenCompraImportacion;
	}

	public MyArray getTmOrdenGasto() {
		return tmOrdenGasto;
	}

	public void setTmOrdenGasto(MyArray tmOrdenGasto) {
		this.tmOrdenGasto = tmOrdenGasto;
	}

	public MyArray getTmPresupuestoVenta() {
		return tmPresupuestoVenta;
	}

	public void setTmPresupuestoVenta(MyArray tmPresupuestoVenta) {
		this.tmPresupuestoVenta = tmPresupuestoVenta;
	}

	public MyArray getTmPedidoVenta() {
		return tmPedidoVenta;
	}

	public void setTmPedidoVenta(MyArray tmPedidoVenta) {
		this.tmPedidoVenta = tmPedidoVenta;
	}

	public MyArray getTmTransferenciaMercaderia() {
		return tmTransferenciaMercaderia;
	}

	public void setTmTransferenciaMercaderia(MyArray tmTransferenciaInterna) {
		this.tmTransferenciaMercaderia = tmTransferenciaInterna;
	}

	public List<MyPair> getEstadosConciliacion() {
		return estadosConciliacion;
	}

	public void setEstadosConciliacion(List<MyPair> estadosConciliacion) {
		this.estadosConciliacion = estadosConciliacion;
	}

	public MyPair getEstadoConciliacionConciliado() {
		return estadoConciliacionConciliado;
	}

	public void setEstadoConciliacionConciliado(
			MyPair estadoConciliacionConciliado) {
		this.estadoConciliacionConciliado = estadoConciliacionConciliado;
	}

	public MyPair getEstadoConciliacionPendiente() {
		return estadoConciliacionPendiente;
	}

	public void setEstadoConciliacionPendiente(
			MyPair estadoConciliacionPendiente) {
		this.estadoConciliacionPendiente = estadoConciliacionPendiente;
	}

	public MyPair getEstadoConciliacionDiferencia() {
		return estadoConciliacionDiferencia;
	}

	public void setEstadoConciliacionDiferencia(
			MyPair estadoConciliacionDiferencia) {
		this.estadoConciliacionDiferencia = estadoConciliacionDiferencia;
	}

	public List<MyArray> getMovimientosBancarios() {
		return movimientosBancarios;
	}

	public void setMovimientosBancarios(List<MyArray> movimientosBancarios) {
		this.movimientosBancarios = movimientosBancarios;
	}

	public MyPair getOperacionBancaria() {
		return operacionBancaria;
	}

	public void setOperacionBancaria(MyPair operacionBancaria) {
		this.operacionBancaria = operacionBancaria;
	}

	public MyPair getEmpresaBanco() {
		return empresaBanco;
	}

	public void setEmpresaBanco(MyPair empresaBanco) {
		this.empresaBanco = empresaBanco;
	}

	public List<MyPair> getTipoVenta() {
		return tipoVenta;
	}

	public void setTipoVenta(List<MyPair> tipoVenta) {
		this.tipoVenta = tipoVenta;
	}

	public MyPair getTipoVentaMostrador() {
		return tipoVentaMostrador;
	}

	public void setTipoVentaMostrador(MyPair tipoVentaMostrador) {
		this.tipoVentaMostrador = tipoVentaMostrador;
	}

	public MyPair getTipoVentaVendedor() {
		return tipoVentaVendedor;
	}

	public void setTipoVentaVendedor(MyPair tipoVentaVendedor) {
		this.tipoVentaVendedor = tipoVentaVendedor;
	}

	public MyPair getTipoVentaTerminal() {
		return tipoVentaTerminal;
	}

	public void setTipoVentaTerminal(MyPair tipoVentaTerminal) {
		this.tipoVentaTerminal = tipoVentaTerminal;
	}

	public MyPair getTipoVentaEncomienda() {
		return tipoVentaEncomienda;
	}

	public void setTipoVentaEncomienda(MyPair tipoVentaEncomienda) {
		this.tipoVentaEncomienda = tipoVentaEncomienda;
	}

	public MyPair getTipoVentaColectivo() {
		return tipoVentaColectivo;
	}

	public void setTipoVentaColectivo(MyPair tipoVentaColectivo) {
		this.tipoVentaColectivo = tipoVentaColectivo;
	}

	public List<MyPair> getFormasDePago() {
		return formasDePago;
	}

	public void setFormasDePago(List<MyPair> formasDePago) {
		this.formasDePago = formasDePago;
	}

	public MyPair getFormaPagoEfectivo() {
		return formaPagoEfectivo;
	}

	public void setFormaPagoEfectivo(MyPair formaPagoEfectivo) {
		this.formaPagoEfectivo = formaPagoEfectivo;
	}

	public MyPair getFormaPagoChequePropio() {
		return formaPagoChequePropio;
	}

	public void setFormaPagoChequePropio(MyPair formaPagoChequeDiferido) {
		this.formaPagoChequePropio = formaPagoChequeDiferido;
	}

	public MyPair getEstadoMovimientoBancoPendiente() {
		return estadoMovimientoBancoPendiente;
	}

	public void setEstadoMovimientoBancoPendiente(
			MyPair estadoMovimientoBancoPendiente) {
		this.estadoMovimientoBancoPendiente = estadoMovimientoBancoPendiente;
	}

	public MyPair getReglaCliente() {
		return reglaCliente;
	}

	public void setReglaCliente(MyPair reglaCliente) {
		this.reglaCliente = reglaCliente;
	}

	public MyPair getReglaArticulo() {
		return reglaArticulo;
	}

	public void setReglaArticulo(MyPair reglaArticulo) {
		this.reglaArticulo = reglaArticulo;
	}

	public MyPair getReglaClienteCategoria() {
		return reglaClienteCategoria;
	}

	public void setReglaClienteCategoria(MyPair reglaClienteCategoria) {
		this.reglaClienteCategoria = reglaClienteCategoria;
	}

	public MyPair getReglaClienteRubro() {
		return reglaClienteRubro;
	}

	public void setReglaClienteRubro(MyPair reglaClienteRubro) {
		this.reglaClienteRubro = reglaClienteRubro;
	}

	public MyPair getReglaVendedor() {
		return reglaVendedor;
	}

	public void setReglaVendedor(MyPair reglaVendedor) {
		this.reglaVendedor = reglaVendedor;
	}

	public MyPair getReglaVenta() {
		return reglaVenta;
	}

	public void setReglaVenta(MyPair reglaVenta) {
		this.reglaVenta = reglaVenta;
	}

	public MyPair getReglaArticuloMarca() {
		return reglaArticuloMarca;
	}

	public void setReglaArticuloMarca(MyPair reglaArticuloMarca) {
		this.reglaArticuloMarca = reglaArticuloMarca;
	}

	public MyPair getReglaArticuloFamilia() {
		return reglaArticuloFamilia;
	}

	public void setReglaArticuloFamilia(MyPair reglaArticuloFamilia) {
		this.reglaArticuloFamilia = reglaArticuloFamilia;
	}

	public MyPair getReglaArticuloParte() {
		return reglaArticuloParte;
	}

	public void setReglaArticuloParte(MyPair reglaArticuloParte) {
		this.reglaArticuloParte = reglaArticuloParte;
	}

	public MyPair getReglaArticuloRubro() {
		return reglaArticuloRubro;
	}

	public void setReglaArticuloRubro(MyPair reglaArticuloRubro) {
		this.reglaArticuloRubro = reglaArticuloRubro;
	}

	public MyPair getReglaVentaModoVenta() {
		return reglaVentaModoVenta;
	}

	public void setReglaVentaModoVenta(MyPair reglaVentaModoVenta) {
		this.reglaVentaModoVenta = reglaVentaModoVenta;
	}

	public MyPair getReglaVentaSucursales() {
		return reglaVentaSucursales;
	}

	public void setReglaVentaSucursales(MyPair reglaVentaSucursales) {
		this.reglaVentaSucursales = reglaVentaSucursales;
	}

	public MyPair getReglaVendedorRubro() {
		return reglaVendedorRubro;
	}

	public void setReglaVendedorRubro(MyPair reglaVendedorRubro) {
		this.reglaVendedorRubro = reglaVendedorRubro;
	}

	public MyPair getCajaReposicion_Ingreso() {
		return cajaReposicion_Ingreso;
	}

	public void setCajaReposicion_Ingreso(MyPair cajaReposicion_Ingreso) {
		this.cajaReposicion_Ingreso = cajaReposicion_Ingreso;
	}

	public MyPair getCajaReposicion_Egreso() {
		return cajaReposicion_Egreso;
	}

	public void setCajaReposicion_Egreso(MyPair cajaReposicion_Egreso) {
		this.cajaReposicion_Egreso = cajaReposicion_Egreso;
	}

	public List<MyPair> getCajaReposiciones() {
		return cajaReposiciones;
	}

	public void setCajaReposiciones(List<MyPair> cajaReposiciones) {
		this.cajaReposiciones = cajaReposiciones;
	}

	public List<MyPair> getModosVenta() {
		return modosVenta;
	}

	public void setModosVenta(List<MyPair> modosVenta) {
		this.modosVenta = modosVenta;
	}

	public MyPair getModoVenta_mostrador() {
		return modoVenta_mostrador;
	}

	public void setModoVenta_mostrador(MyPair modoVenta_mostrador) {
		this.modoVenta_mostrador = modoVenta_mostrador;
	}

	public MyPair getModoVenta_externa() {
		return modoVenta_externa;
	}

	public void setModoVenta_externa(MyPair modoVenta_externa) {
		this.modoVenta_externa = modoVenta_externa;
	}

	public MyPair getModoVenta_telefonica() {
		return modoVenta_telefonica;
	}

	public void setModoVenta_telefonica(MyPair modoVenta_telefonica) {
		this.modoVenta_telefonica = modoVenta_telefonica;
	}

	public MyArray getEstadoVenta_soloPedido() {
		return estadoVenta_soloPedido;
	}

	public void setEstadoVenta_soloPedido(MyArray estadoVenta_soloPedido) {
		this.estadoVenta_soloPedido = estadoVenta_soloPedido;
	}

	public MyPair getTipoEstadoMayor() {
		return tipoEstadoMayor;
	}

	public void setTipoEstadoMayor(MyPair tipoEstadoMayor) {
		this.tipoEstadoMayor = tipoEstadoMayor;
	}

	public MyPair getTipoEstadoMenor() {
		return tipoEstadoMenor;
	}

	public void setTipoEstadoMenor(MyPair tipoEstadoMenor) {
		this.tipoEstadoMenor = tipoEstadoMenor;
	}

	public MyPair getTipoEstadoIgual() {
		return tipoEstadoIgual;
	}

	public void setTipoEstadoIgual(MyPair tipoEstadoIgual) {
		this.tipoEstadoIgual = tipoEstadoIgual;
	}

	public MyPair getTipoEstadoDiferente() {
		return tipoEstadoDiferente;
	}

	public void setTipoEstadoDiferente(MyPair tipoEstadoDiferente) {
		this.tipoEstadoDiferente = tipoEstadoDiferente;
	}

	public MyPair getTipoEstadoNinguno() {
		return tipoEstadoNinguno;
	}

	public void setTipoEstadoNinguno(MyPair tipoEstadoNinguno) {
		this.tipoEstadoNinguno = tipoEstadoNinguno;
	}

	public List<MyPair> getTipoEstadoReglaPrecioVolumen() {
		return tipoEstadoReglaPrecioVolumen;
	}

	public void setTipoEstadoReglaPrecioVolumen(
			List<MyPair> tipoEstadoReglaPrecioVolumen) {
		this.tipoEstadoReglaPrecioVolumen = tipoEstadoReglaPrecioVolumen;
	}

	public List<MyPair> getEstadosComprobantes() {
		return estadosComprobantes;
	}

	public void setEstadosComprobantes(List<MyPair> estadosComprobantes) {
		this.estadosComprobantes = estadosComprobantes;
	}

	public MyPair getEstadoComprobantePendiente() {
		return estadoComprobantePendiente;
	}

	public void setEstadoComprobantePendiente(MyPair estadoComprobanteBorrador) {
		this.estadoComprobantePendiente = estadoComprobanteBorrador;
	}

	public MyPair getEstadoComprobanteCerrado() {
		return estadoComprobanteCerrado;
	}

	public void setEstadoComprobanteCerrado(MyPair estadoComprobanteCerrado) {
		this.estadoComprobanteCerrado = estadoComprobanteCerrado;
	}

	public MyPair getEstadoComprobanteAnulado() {
		return estadoComprobanteAnulado;
	}

	public void setEstadoComprobanteAnulado(MyPair estadoComprobanteAnulado) {
		this.estadoComprobanteAnulado = estadoComprobanteAnulado;
	}

	public List<MyPair> getTipoTarjeta() {
		return tipoTarjeta;
	}

	public void setTipoTarjeta(List<MyPair> tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}

	public MyPair getTipoTarjeta1() {
		return tipoTarjeta1;
	}

	public void setTipoTarjeta1(MyPair tipoTarjeta1) {
		this.tipoTarjeta1 = tipoTarjeta1;
	}

	public MyPair getTipoTarjeta2() {
		return tipoTarjeta2;
	}

	public void setTipoTarjeta2(MyPair tipoTarjeta2) {
		this.tipoTarjeta2 = tipoTarjeta2;
	}

	public MyPair getTipoTarjeta3() {
		return tipoTarjeta3;
	}

	public void setTipoTarjeta3(MyPair tipoTarjeta3) {
		this.tipoTarjeta3 = tipoTarjeta3;
	}

	public List<MyPair> getMotivosNotaCredito() {
		return motivosNotaCredito;
	}

	public void setMotivosNotaCredito(List<MyPair> motivosNotaCredito) {
		this.motivosNotaCredito = motivosNotaCredito;
	}

	public MyPair getMotivoNotaCreditoDescuento() {
		return motivoNotaCreditoDescuento;
	}

	public void setMotivoNotaCreditoDescuento(MyPair motivoNotaCreditoDescuento) {
		this.motivoNotaCreditoDescuento = motivoNotaCreditoDescuento;
	}

	public MyPair getMotivoNotaCreditoDevolucion() {
		return motivoNotaCreditoDevolucion;
	}

	public void setMotivoNotaCreditoDevolucion(
			MyPair motivoNotaCreditoDevolucion) {
		this.motivoNotaCreditoDevolucion = motivoNotaCreditoDevolucion;
	}

	public MyPair getOperacionDevolucion() {
		return operacionDevolucion;
	}

	public void setOperacionDevolucion(MyPair operacionDevolucion) {
		this.operacionDevolucion = operacionDevolucion;
	}

	public MyArray getTmSolicitudNotaCreditoCompra() {
		return tmSolicitudNotaCreditoCompra;
	}

	public void setTmSolicitudNotaCreditoCompra(
			MyArray tmSolicitudNotaCreditoCompra) {
		this.tmSolicitudNotaCreditoCompra = tmSolicitudNotaCreditoCompra;
	}

	public MyArray getTmSolicitudNotaCreditoVenta() {
		return tmSolicitudNotaCreditoVenta;
	}

	public void setTmSolicitudNotaCreditoVenta(
			MyArray tmSolicitudNotaCreditoVenta) {
		this.tmSolicitudNotaCreditoVenta = tmSolicitudNotaCreditoVenta;
	}

	public MyPair getEstadoComprobanteAprobado() {
		return estadoComprobanteAprobado;
	}

	public void setEstadoComprobanteAprobado(MyPair estadoComprobanteConfirmado) {
		this.estadoComprobanteAprobado = estadoComprobanteConfirmado;
	}

	public MyArray getCuentaIvaCF10() {
		return cuentaIvaCF10;
	}

	public void setCuentaIvaCF10(MyArray cuentaIvaCF10) {
		this.cuentaIvaCF10 = cuentaIvaCF10;
	}

	public List<MyArray> getTarjetas() {
		return tarjetas;
	}

	public void setTarjetas(List<MyArray> tarjetas) {
		this.tarjetas = tarjetas;
	}

	public MyPair getFormaPagoTarjetaCredito() {
		return formaPagoTarjetaCredito;
	}

	public void setFormaPagoTarjetaCredito(MyPair formaPagoTarjetaCredito) {
		this.formaPagoTarjetaCredito = formaPagoTarjetaCredito;
	}

	public MyPair getFormaPagoTarjetaDebito() {
		return formaPagoTarjetaDebito;
	}

	public void setFormaPagoTarjetaDebito(MyPair formaPagoTarjetaDebito) {
		this.formaPagoTarjetaDebito = formaPagoTarjetaDebito;
	}

	public List<MyPair> getTiposDeReserva() {
		return tiposDeReserva;
	}

	public void setTiposDeReserva(List<MyPair> tiposDeReserva) {
		this.tiposDeReserva = tiposDeReserva;
	}

	public MyPair getReservaInterna() {
		return reservaInterna;
	}

	public void setReservaInterna(MyPair reservaInterna) {
		this.reservaInterna = reservaInterna;
	}

	public MyPair getReservaReparto() {
		return reservaReparto;
	}

	public void setReservaReparto(MyPair reservaReparto) {
		this.reservaReparto = reservaReparto;
	}

	public MyPair getReservaVenta() {
		return reservaVenta;
	}

	public void setReservaVenta(MyPair reservaVenta) {
		this.reservaVenta = reservaVenta;
	}

	public List<MyPair> getEstadosDeReserva() {
		return estadosDeReserva;
	}

	public void setEstadosDeReserva(List<MyPair> estadosDeReserva) {
		this.estadosDeReserva = estadosDeReserva;
	}

	public MyPair getEstado_reservaActiva() {
		return estado_reservaActiva;
	}

	public void setEstado_reservaActiva(MyPair estado_reservaActiva) {
		this.estado_reservaActiva = estado_reservaActiva;
	}

	public MyPair getEstado_reservaCancelada() {
		return estado_reservaCancelada;
	}

	public void setEstado_reservaCancelada(MyPair estado_reservaCancelada) {
		this.estado_reservaCancelada = estado_reservaCancelada;
	}

	public MyPair getEstado_reservaFinalizada() {
		return estado_reservaFinalizada;
	}

	public void setEstado_reservaFinalizada(MyPair estado_reservaFinalizada) {
		this.estado_reservaFinalizada = estado_reservaFinalizada;
	}

	public MyPair getNotaCreditoDetalleFactura() {
		return notaCreditoDetalleFactura;
	}

	public void setNotaCreditoDetalleFactura(MyPair notaCreditoDetalleFactura) {
		this.notaCreditoDetalleFactura = notaCreditoDetalleFactura;
	}

	public MyPair getNotaCreditoDetalleArticulo() {
		return notaCreditoDetalleArticulo;
	}

	public void setNotaCreditoDetalleArticulo(MyPair notaCreditoDetalleArticulo) {
		this.notaCreditoDetalleArticulo = notaCreditoDetalleArticulo;
	}

	public MyPair getReservaDevolucion() {
		return reservaDevolucion;
	}

	public void setReservaDevolucion(MyPair reservaDevolucion) {
		this.reservaDevolucion = reservaDevolucion;
	}

	public List<MyPair> getMotivosDeDevolucion() {
		return motivosDeDevolucion;
	}

	public void setMotivosDeDevolucion(List<MyPair> motivosDeDevolucion) {
		this.motivosDeDevolucion = motivosDeDevolucion;
	}

	public MyPair getMotivoDevolucionDiaSiguiente() {
		return motivoDevolucionDiaSiguiente;
	}

	public void setMotivoDevolucionDiaSiguiente(
			MyPair motivoDevolucionDiaSiguiente) {
		this.motivoDevolucionDiaSiguiente = motivoDevolucionDiaSiguiente;
	}

	public MyPair getMotivoDevolucionEntregaParcial() {
		return motivoDevolucionEntregaParcial;
	}

	public void setMotivoDevolucionEntregaParcial(
			MyPair motivoDevolucionEntregaParcial) {
		this.motivoDevolucionEntregaParcial = motivoDevolucionEntregaParcial;
	}

	public MyPair getMotivoDevolucionDefectuoso() {
		return motivoDevolucionDefectuoso;
	}

	public void setMotivoDevolucionDefectuoso(MyPair motivoDevolucionDefectuoso) {
		this.motivoDevolucionDefectuoso = motivoDevolucionDefectuoso;
	}

	public MyPair getTipoVentaReparto() {
		return tipoVentaReparto;
	}

	public void setTipoVentaReparto(MyPair tipoVentaReparto) {
		this.tipoVentaReparto = tipoVentaReparto;
	}

	public MyPair getImportacionEstadoAnulado() {
		return importacionEstadoAnulado;
	}

	public void setImportacionEstadoAnulado(MyPair importacionEstadoAnulado) {
		this.importacionEstadoAnulado = importacionEstadoAnulado;
	}

	public MyArray getCuentaIvaCF5() {
		return cuentaIvaCF5;
	}

	public void setCuentaIvaCF5(MyArray cuentaIvaCF5) {
		this.cuentaIvaCF5 = cuentaIvaCF5;
	}

	/**
	 * get y set tipos de cambios
	 * 
	 * @return
	 */

	public MyPair getCambioAPP() {
		return cambioAPP;
	}

	public void setCambioAPP(MyPair cambioAPP) {
		this.cambioAPP = cambioAPP;
	}

	public MyPair getCambioBCP() {
		return cambioBCP;
	}

	public void setCambioBCP(MyPair cambioBCP) {
		this.cambioBCP = cambioBCP;
	}

	public List<MyPair> getTiposCambio() {
		return tiposCambio;
	}

	public void setTiposCambio(List<MyPair> tiposCambio) {
		this.tiposCambio = tiposCambio;
	}

	public List<MyPair> getCuentasContablesTipos() {
		return cuentasContablesTipos;
	}

	public void setCuentasContablesTipos(List<MyPair> cuentasContablesTipos) {
		this.cuentasContablesTipos = cuentasContablesTipos;
	}

	public MyArray getTmBancoEmisionCheque() {
		return tmBancoEmisionCheque;
	}

	public void setTmBancoEmisionCheque(MyArray tnBancoChequePropio) {
		this.tmBancoEmisionCheque = tnBancoChequePropio;
	}

	public MyArray getTmBancoDepositoBancario() {
		return tmBancoDepositoBancario;
	}

	public void setTmBancoDepositoBancario(MyArray tmBancoDepositoEfectivo) {
		this.tmBancoDepositoBancario = tmBancoDepositoEfectivo;
	}

	public List<MyArray> getProcesadoras() {
		return procesadoras;
	}

	public void setProcesadoras(List<MyArray> procesadorasTarjetas) {
		this.procesadoras = procesadorasTarjetas;
	}

	public List<MyPair> getCajaReposicionEgresos() {
		return cajaReposicionEgresos;
	}

	public void setCajaReposicionEgresos(List<MyPair> cajaReposicionEgresos) {
		this.cajaReposicionEgresos = cajaReposicionEgresos;
	}

	public MyPair getCajaReposicionEgreso_SinComprobante() {
		return cajaReposicionEgreso_SinComprobante;
	}

	public void setCajaReposicionEgreso_SinComprobante(
			MyPair cajaReposicionEgreso_SinComprobante) {
		this.cajaReposicionEgreso_SinComprobante = cajaReposicionEgreso_SinComprobante;
	}

	public MyPair getCajaReposicionEgreso_Vale() {
		return cajaReposicionEgreso_Vale;
	}

	public void setCajaReposicionEgreso_Vale(MyPair cajaReposicionEgreso_Vale) {
		this.cajaReposicionEgreso_Vale = cajaReposicionEgreso_Vale;
	}

	public MyPair getFormaPagoChequeTercero() {
		return formaPagoChequeTercero;
	}

	public void setFormaPagoChequeTercero(MyPair formaPagoChequeTercero) {
		this.formaPagoChequeTercero = formaPagoChequeTercero;
	}

	public MyPair getCajaPeriodoEstadoAbierta() {
		return cajaPeriodoEstadoAbierta;
	}

	public void setCajaPeriodoEstadoAbierta(MyPair cajaPeriodoEstadoAbierta) {
		this.cajaPeriodoEstadoAbierta = cajaPeriodoEstadoAbierta;
	}

	public MyPair getCajaPeriodoEstadoCerrada() {
		return cajaPeriodoEstadoCerrada;
	}

	public void setCajaPeriodoEstadoCerrada(MyPair cajaPeriodoEstadoCerrada) {
		this.cajaPeriodoEstadoCerrada = cajaPeriodoEstadoCerrada;
	}

	public MyPair getCajaPeriodoEstadoProcesada() {
		return cajaPeriodoEstadoProcesada;
	}

	public void setCajaPeriodoEstadoProcesada(MyPair cajaPeriodoEstadoProcesada) {
		this.cajaPeriodoEstadoProcesada = cajaPeriodoEstadoProcesada;
	}

	public MyPair getEstadoComprobanteConfeccionado() {
		return estadoComprobanteConfeccionado;
	}

	public void setEstadoComprobanteConfeccionado(
			MyPair estadoComprobanteConfeccionado) {
		this.estadoComprobanteConfeccionado = estadoComprobanteConfeccionado;
	}

	public MyPair getFormaPagoRetencion() {
		return formaPagoRetencion;
	}

	public void setFormaPagoRetencion(MyPair formaPagoRetencion) {
		this.formaPagoRetencion = formaPagoRetencion;
	}

	public MyPair getFormaPagoDepositoBancario() {
		return formaPagoDepositoBancario;
	}

	public void setFormaPagoDepositoBancario(MyPair formaPagoDepositoBancario) {
		this.formaPagoDepositoBancario = formaPagoDepositoBancario;
	}

	public List<MyPair> getModosDeCreacionCheque() {
		return modosDeCreacionCheque;
	}

	public void setModosDeCreacionCheque(List<MyPair> modosDeCreacionCheque) {
		this.modosDeCreacionCheque = modosDeCreacionCheque;
	}

	public MyPair getChequeAutomatico() {
		return chequeAutomatico;
	}

	public void setChequeAutomatico(MyPair chequeAutomatico) {
		this.chequeAutomatico = chequeAutomatico;
	}

	public MyPair getChequeManual() {
		return ChequeManual;
	}

	public void setChequeManual(MyPair chequeManual) {
		ChequeManual = chequeManual;
	}

	public List<MyPair> getBancosTerceros() {
		return bancosTerceros;
	}

	public void setBancosTerceros(List<MyPair> bancosTerceros) {
		this.bancosTerceros = bancosTerceros;
	}

	public MyPair getMRA_deposito() {
		return MRA_deposito;
	}

	public void setMRA_deposito(MyPair mRA_deposito) {
		MRA_deposito = mRA_deposito;
	}

	public MyPair getMRA_modoVenta() {
		return MRA_modoVenta;
	}

	public void setMRA_modoVenta(MyPair mRA_modoVenta) {
		MRA_modoVenta = mRA_modoVenta;
	}

	public MyPair getMRA_sucursal() {
		return MRA_sucursal;
	}

	public void setMRA_sucursal(MyPair mRA_sucursal) {
		MRA_sucursal = mRA_sucursal;
	}

	public MyArray getMRA_vendedor() {
		return MRA_vendedor;
	}

	public void setMRA_vendedor(MyArray mRA_vendedor) {
		MRA_vendedor = mRA_vendedor;
	}

	public MyArray getMRA_articulo() {
		return MRA_articulo;
	}

	public void setMRA_articulo(MyArray mRA_articulo) {
		MRA_articulo = mRA_articulo;
	}

	public MyPair getClienteOcasional() {
		return clienteOcasional;
	}

	public void setClienteOcasional(MyPair clienteOcasional) {
		this.clienteOcasional = clienteOcasional;
	}

	public MyPair getMotivoNotaCreditoDifPrecio() {
		return motivoNotaCreditoDifPrecio;
	}

	public void setMotivoNotaCreditoDifPrecio(MyPair motivoNotaCreditoDifPrecio) {
		this.motivoNotaCreditoDifPrecio = motivoNotaCreditoDifPrecio;
	}

	public List<MyArray> getMovimientosDeAjuste() {
		return movimientosDeAjuste;
	}

	public void setMovimientosDeAjuste(List<MyArray> movimientosDeAjuste) {
		this.movimientosDeAjuste = movimientosDeAjuste;
	}

	public MyPair getOperacionAjuste() {
		return operacionAjuste;
	}

	public void setOperacionAjuste(MyPair operacionAjuste) {
		this.operacionAjuste = operacionAjuste;
	}

	public MyArray getTmAjustePositivo() {
		return tmAjustePositivo;
	}

	public void setTmAjustePositivo(MyArray tmAjustePositivo) {
		this.tmAjustePositivo = tmAjustePositivo;
	}

	public MyArray getTmAjusteNegativo() {
		return tmAjusteNegativo;
	}

	public void setTmAjusteNegativo(MyArray tmAjusteNegativo) {
		this.tmAjusteNegativo = tmAjusteNegativo;
	}

	public MyPair getCajaTipoMovimientosVarios() {
		return cajaTipoMovimientosVarios;
	}

	public void setCajaTipoMovimientosVarios(MyPair cajaTipoMovimientosVarios) {
		this.cajaTipoMovimientosVarios = cajaTipoMovimientosVarios;
	}

	public MyArray getTmBoletaVenta() {
		return tmBoletaVenta;
	}

	public void setTmBoletaVenta(MyArray tmBoletaVenta) {
		this.tmBoletaVenta = tmBoletaVenta;
	}

	public MyPair getFormaPagoChequeAutoCobranza() {
		return formaPagoChequeAutoCobranza;
	}

	public void setFormaPagoChequeAutoCobranza(MyPair formaPagoSaldoFavor) {
		this.formaPagoChequeAutoCobranza = formaPagoSaldoFavor;
	}

	public MyPair getMotivoNotaCreditoReclamo() {
		return motivoNotaCreditoReclamo;
	}

	public void setMotivoNotaCreditoReclamo(MyPair motivoNotaCreditoReclamo) {
		this.motivoNotaCreditoReclamo = motivoNotaCreditoReclamo;
	}

	public MyPair getFormaPagoDebitoCobranzaCentral() {
		return formaPagoDebitoCobranzaCentral;
	}

	public void setFormaPagoDebitoCobranzaCentral(
			MyPair formaPagoDebitoBancarioCobranzaCentral) {
		this.formaPagoDebitoCobranzaCentral = formaPagoDebitoBancarioCobranzaCentral;
	}

	public MyPair getFormaPagoRecaudacionCentral() {
		return formaPagoRecaudacionCentral;
	}

	public void setFormaPagoRecaudacionCentral(MyPair formaPagoRecaudacionCentral) {
		this.formaPagoRecaudacionCentral = formaPagoRecaudacionCentral;
	}

	public MyPair getFormaPagoTransferenciaCentral() {
		return formaPagoTransferenciaCentral;
	}

	public void setFormaPagoTransferenciaCentral(
			MyPair formaPagoTransferenciaCentral) {
		this.formaPagoTransferenciaCentral = formaPagoTransferenciaCentral;
	}

	public MyPair getFormaPagoSaldoFavorGenerado() {
		return formaPagoSaldoFavorGenerado;
	}

	public void setFormaPagoSaldoFavorGenerado(MyPair formaPagoSaldoFavorCliente) {
		this.formaPagoSaldoFavorGenerado = formaPagoSaldoFavorCliente;
	}

	public MyPair getFormaPagoSaldoFavorCobrado() {
		return formaPagoSaldoFavorCobrado;
	}

	public void setFormaPagoSaldoFavorCobrado(MyPair formaPagoSaldoFavorCobrado) {
		this.formaPagoSaldoFavorCobrado = formaPagoSaldoFavorCobrado;
	}

	public MyArray getTmCancelacionChequeRechazado() {
		return tmCancelacionChequeRechazado;
	}

	public void setTmCancelacionChequeRechazado(MyArray tmCancelacionChequeRechazado) {
		this.tmCancelacionChequeRechazado = tmCancelacionChequeRechazado;
	}

	public MyArray getTmReembolsoPrestamo() {
		return tmReembolsoPrestamo;
	}

	public void setTmReembolsoPrestamo(MyArray tmReembolsoPrestamo) {
		this.tmReembolsoPrestamo = tmReembolsoPrestamo;
	}

	public MyArray getTmInventario() {
		return tmInventario;
	}

	public void setTmInventario(MyArray tmInventario) {
		this.tmInventario = tmInventario;
	}

	public MyPair getFormaPagoDebitoEnCuentaBancaria() {
		return formaPagoDebitoEnCuentaBancaria;
	}

	public void setFormaPagoDebitoEnCuentaBancaria(MyPair formaPagoDebitoEnCtaBancaria) {
		this.formaPagoDebitoEnCuentaBancaria = formaPagoDebitoEnCtaBancaria;
	}

	public MyArray getMonedaDolaresConSimbolo() {
		return monedaDolaresConSimbolo;
	}

	public void setMonedaDolaresConSimbolo(MyArray monedaDolaresConSimbolo) {
		this.monedaDolaresConSimbolo = monedaDolaresConSimbolo;
	}

	public MyArray getTmOtrosComprobantes() {
		return tmOtrosComprobantes;
	}

	public void setTmOtrosComprobantes(MyArray tmOtrosComprobantes) {
		this.tmOtrosComprobantes = tmOtrosComprobantes;
	}
}
