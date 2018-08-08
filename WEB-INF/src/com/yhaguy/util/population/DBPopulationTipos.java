package com.yhaguy.util.population;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.domain.TipoTipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.ArticuloPresentacion;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.ContactoSexo;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.EmpresaGrupoSociedad;
import com.yhaguy.domain.EstadoCivil;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;

public class DBPopulationTipos {

	/***********
	 * OJO Respetar el orden de definición de los tipos que hay en este archivo,
	 * sino Verena se enoja .. para evitar la furia, empecé a cambiar los id
	 * enteros por String :)
	 *************/

	private RegisterDomain rr = RegisterDomain.getInstance();

	private void grabarDB(Domain d) throws Exception {
		rr.saveObject(d, "sys");
	}

	TipoTipo sinReferencia = new TipoTipo();
	public Tipo sinReferenciaTipo = new Tipo();

	TipoTipo transferenciaTipo = new TipoTipo();
	Tipo transfTipo1 = new Tipo();
	Tipo transfTipo2 = new Tipo();

	TipoTipo transferenciaEstado = new TipoTipo();
	Tipo transfEstado1 = new Tipo();
	Tipo transfEstado2 = new Tipo();
	Tipo transfEstado3 = new Tipo();
	Tipo transfEstado4 = new Tipo();
	Tipo transfEstado5 = new Tipo();
	Tipo transfEstado6 = new Tipo();
	Tipo transfEstado7 = new Tipo();

	TipoTipo cmlgt = new TipoTipo();
	Tipo cmlgt1 = new Tipo();
	Tipo cmlgt2 = new Tipo();

	TipoTipo repartoTipo = new TipoTipo();
	Tipo tipoReparto1 = new Tipo();
	Tipo tipoReparto2 = new Tipo();
	Tipo tipoReparto3 = new Tipo();
	Tipo tipoReparto4 = new Tipo();

	TipoTipo estadoReparto = new TipoTipo();
	Tipo estadoReparto1 = new Tipo();
	Tipo estadoReparto2 = new Tipo();
	Tipo estadoReparto3 = new Tipo();
	Tipo estadoReparto4 = new Tipo();
	Tipo estadoReparto5 = new Tipo();
	Tipo estadoReparto6 = new Tipo();

	TipoTipo ventaTipo = new TipoTipo();
	Tipo tipoVta1 = new Tipo();
	Tipo tipoVta2 = new Tipo();
	Tipo tipoVta3 = new Tipo();
	Tipo tipoVta4 = new Tipo();
	Tipo tipoVta6 = new Tipo();
	Tipo tipoVta7 = new Tipo();

	TipoTipo nct = new TipoTipo();
	Tipo nct1 = new Tipo();
	Tipo nct2 = new Tipo();

	TipoTipo moneda = new TipoTipo();
	public Tipo guarani = new Tipo();
	Tipo dolar = new Tipo();
	Tipo peso = new Tipo();
	Tipo euro = new Tipo();

	TipoTipo tipoCambio = new TipoTipo();
	Tipo tipoCambioYhaguy = new Tipo();
	Tipo tipoCambioOficial = new Tipo();

	TipoTipo emailEmpresa = new TipoTipo();
	Tipo emailCompra = new Tipo();
	Tipo emailVenta = new Tipo();
	Tipo emailUnico = new Tipo();
	Tipo emailPromocion = new Tipo();

	TipoTipo paisEmpresa = new TipoTipo();
	Tipo paisPry = new Tipo();
	Tipo paisNoDefinido = new Tipo();
	Tipo paisArg = new Tipo();
	Tipo paisBra = new Tipo();
	Tipo paisBol = new Tipo();
	Tipo paisUry = new Tipo();
	Tipo paisUsa = new Tipo();
	Tipo paisJpn = new Tipo();
	Tipo paisChn = new Tipo();
	Tipo paisTur = new Tipo();

	TipoTipo tipoPersona = new TipoTipo();
	Tipo personaFisica = new Tipo();
	Tipo personaJuridica = new Tipo();

	TipoTipo mailEmpresa = new TipoTipo();
	Tipo mailEmpresaCompra = new Tipo();
	Tipo mailEmpresaUnico = new Tipo();
	Tipo mailEmpresaPromocion = new Tipo();

	TipoTipo bancoCtaTipo = new TipoTipo();
	Tipo bancoCtaCte = new Tipo();
	Tipo bancoCajaAhorro = new Tipo();

	TipoTipo cajaReposicion = new TipoTipo();
	Tipo cajaRepIngreso = new Tipo();
	Tipo cajaRepEgreso = new Tipo();
	
	TipoTipo cajaReposicionEgreso = new TipoTipo();
	Tipo cajaRepEgresoSinCmpbte = new Tipo();
	Tipo cajaRepEgresoVale = new Tipo();
	Tipo cajaRepEgresoPrestamo = new Tipo();
	Tipo cajaRepEgresoGratificacion = new Tipo();

	TipoTipo cajaClasificacion = new TipoTipo();
	Tipo cajaClasificacion1 = new Tipo();
	Tipo cajaClasificacion2 = new Tipo();

	TipoTipo cajaTipo = new TipoTipo();
	Tipo cajaTipo1 = new Tipo();
	Tipo cajaTipo2 = new Tipo();
	Tipo cajaTipo3 = new Tipo();

	TipoTipo cajaEstado = new TipoTipo();
	Tipo cajaEstado1 = new Tipo();
	Tipo cajaEstado2 = new Tipo();
	
	TipoTipo cajaPeriodoEstado = new TipoTipo();
	Tipo cajaAbierta = new Tipo();
	Tipo cajaCerrada = new Tipo();
	Tipo cajaProcesada = new Tipo();

	TipoTipo cajaDuracion = new TipoTipo();
	Tipo cajaDuracion1 = new Tipo();
	Tipo cajaDuracion2 = new Tipo();
	Tipo cajaDuracion3 = new Tipo();

	TipoTipo importacionEstado = new TipoTipo();
	Tipo importacionEstado1 = new Tipo();
	Tipo importacionEstado2 = new Tipo();
	Tipo importacionEstado3 = new Tipo();
	Tipo importacionEstado4 = new Tipo();
	Tipo importacionEstado5 = new Tipo();
	Tipo importacionEstado6 = new Tipo();
	Tipo importacionEstado7 = new Tipo();

	TipoTipo reciboEstado = new TipoTipo();
	Tipo reciboEstado1 = new Tipo();
	Tipo reciboEstado2 = new Tipo();

	TipoTipo reciboFormaPago = new TipoTipo();
	Tipo reciboFormaPagoEfectivo = new Tipo();
	Tipo reciboFormaPagoChequePropio = new Tipo();
	Tipo reciboFormaPagoChequeTercero = new Tipo();
	Tipo reciboFormaPagoTarjetaCredito = new Tipo();
	Tipo reciboFormaPagoTarjetaDebito = new Tipo();
	Tipo reciboFormaPagoRetencion = new Tipo();
	Tipo reciboFormaPagoDepositoBancario = new Tipo();

	TipoTipo ctaCteProveedorEstado = new TipoTipo();
	Tipo ctaCteProveedorEstado1 = new Tipo();
	Tipo ctaCteProveedorEstado2 = new Tipo();

	TipoTipo ctaCtetipoImputacion = new TipoTipo();
	Tipo ctaCtetipoImputacion1 = new Tipo();
	Tipo ctaCtetipoImputacion2 = new Tipo();

	TipoTipo ctaCteEmpresaEstado = new TipoTipo();
	Tipo ctaCteEmpresaEstado1 = new Tipo();
	Tipo ctaCteEmpresaEstado2 = new Tipo();
	Tipo ctaCteEmpresaEstado3 = new Tipo();
	Tipo ctaCteEmpresaEstado4 = new Tipo();

	TipoTipo ctaCteEmpresaCaracterMovimiento = new TipoTipo();
	Tipo ctaCteEmpresaCaracterMovimiento1 = new Tipo();
	Tipo ctaCteEmpresaCaracterMovimiento2 = new Tipo();

	TipoTipo ctaCteEmpresaSaldoEstado = new TipoTipo();
	Tipo ctaCteEmpresaSaldoEstado1 = new Tipo();
	Tipo ctaCteEmpresaSaldoEstado2 = new Tipo();
	Tipo ctaCteEmpresaSaldoEstado3 = new Tipo();

	TipoTipo movEstadoReparto = new TipoTipo();
	Tipo estadoMovRep1 = new Tipo();
	Tipo estadoMovRep2 = new Tipo();
	Tipo estadoMovRep3 = new Tipo();
	Tipo estadoMovRep4 = new Tipo();
	Tipo estadoMovRep5 = new Tipo();

	TipoTipo compraTipoDescuentoSAC = new TipoTipo(); // Afecta Costo Final..
	TipoTipo compraTipoDescuentoNAC = new TipoTipo(); // No afecta el Costo
														// Final..
	Tipo tipoDescuento1 = new Tipo();
	Tipo tipoDescuento2 = new Tipo();
	Tipo tipoDescuento3 = new Tipo();

	TipoTipo compraTipoGasto = new TipoTipo();
	Tipo tipoCompraGasto1 = new Tipo();
	Tipo tipoCompraGasto2 = new Tipo();
	Tipo tipoCompraGasto3 = new Tipo();

	TipoTipo compraTipoProrrateo = new TipoTipo();
	Tipo compraTipoProrrateo1 = new Tipo();
	Tipo compraTipoProrrateo2 = new Tipo();

	TipoTipo ventaEstado = new TipoTipo();
	Tipo venta_soloPresupuesto = new Tipo();
	Tipo venta_pasadoPedido = new Tipo();
	Tipo venta_soloPedido = new Tipo();
	Tipo venta_pedidoCerrado = new Tipo();
	Tipo venta_pedidoFacturado = new Tipo();

	TipoTipo modoVenta = new TipoTipo();
	Tipo modoVenta_mostrador = new Tipo();
	Tipo modoVenta_externa = new Tipo();
	Tipo modoVenta_telefonica = new Tipo();

	TipoTipo tipoIva = new TipoTipo();
	Tipo tipoIva1 = new Tipo();
	Tipo tipoIva2 = new Tipo();
	Tipo tipoIva3 = new Tipo();

	TipoTipo tipoAlerta = new TipoTipo();
	Tipo alertaMuchos = new Tipo();
	Tipo alertaUno = new Tipo();
	Tipo alertaComunitaria = new Tipo();

	TipoTipo nivelAlerta = new TipoTipo();
	Tipo alertaInformativa = new Tipo();
	Tipo alertaError = new Tipo();

	TipoTipo tipoRubroEmpresa = new TipoTipo();
	Tipo rubroEmpresaConsumidorFinal = new Tipo();
	public Tipo rubroEmpresaFuncionario = new Tipo();
	Tipo rubroEmpresaMayoristaBaterias = new Tipo();
	Tipo rubroEmpresaMayoristaCubiertas = new Tipo();
	Tipo rubroEmpresaMayoristaLubricantes = new Tipo();
	Tipo rubroEmpresaMayoristaRepuestos = new Tipo();
	Tipo rubroEmpresaDistribuidorBaterias = new Tipo();
	Tipo rubroEmpresaDistribuidorCubiertas = new Tipo();
	Tipo rubroEmpresaDistribuidorLubricantes = new Tipo();
	Tipo rubroEmpresaDistribuidorRepuestos = new Tipo();
	Tipo rubroEmpresaCasaRepuestos = new Tipo();
	Tipo rubroEmpresaAseguradoras = new Tipo();

	// Regimen Tributario
	TipoTipo tipoRegimenTributario = new TipoTipo();
	Tipo regimenTributario1 = new Tipo();
	Tipo regimenTributario2 = new Tipo();

	// Datos de Proveedores
	TipoTipo tipoProveedores = new TipoTipo();
	Tipo tipoProveedor1 = new Tipo();
	Tipo tipoProveedor2 = new Tipo();

	TipoTipo tipoProveedorEstados = new TipoTipo();
	Tipo tipoProvEstado1 = new Tipo();
	Tipo tipoProvEstado2 = new Tipo();

	// Datos de Clientes
	TipoTipo tipoClientes = new TipoTipo();
	Tipo tipoCliente1 = new Tipo();
	Tipo tipoCliente2 = new Tipo();
	Tipo tipoCliente3 = new Tipo();
	Tipo tipoCliente4 = new Tipo();
	Tipo tipoCliente5 = new Tipo();

	// Datos de Articulos : estados
	TipoTipo estadoArticulo = new TipoTipo();
	public Tipo estadoArticulo1 = new Tipo();
	Tipo estadoArticulo2 = new Tipo();
	Tipo estadoArticulo3 = new Tipo();
	
	 // Datos de Articulos : UNID_MED 
	TipoTipo unid_medArticulo = new TipoTipo(); 
	Tipo unid_medArticulo1 = new Tipo(); 
	Tipo unid_medArticulo2 =new Tipo(); 
	Tipo unid_medArticulo3 = new Tipo();	 

	// Tipos de Importacion
	TipoTipo tipoImportacion = new TipoTipo();
	Tipo tipoImportacion1 = new Tipo();
	Tipo tipoImportacion2 = new Tipo();
	Tipo tipoImportacion3 = new Tipo();

	// Tipos de Comprobantes
	TipoTipo tipoComprobante = new TipoTipo();
	Tipo comprobanteLegal = new Tipo();
	Tipo comprobanteInterno = new Tipo();

	// Tipos de Operacion
	TipoTipo tipoOperacion = new TipoTipo();
	Tipo operacionCompra = new Tipo();
	Tipo operacionGasto = new Tipo();
	Tipo operacionPago = new Tipo();
	Tipo operacionVenta = new Tipo();
	Tipo operacionCobro = new Tipo();
	Tipo operacionRemision = new Tipo();
	Tipo operacionBancaria = new Tipo();
	Tipo operacionAjuste = new Tipo();

	// Tipos de Empresa
	TipoTipo tipoEmpresa = new TipoTipo();
	Tipo cliente = new Tipo();
	Tipo proveedor = new Tipo();
	Tipo funcionario = new Tipo();
	Tipo yhaguy = new Tipo();
	Tipo banco = new Tipo();

	TipoTipo tipoEstadoRegla = new TipoTipo();
	Tipo estadoMayor = new Tipo();
	Tipo estadoMenor = new Tipo();
	Tipo estadoIgual = new Tipo();
	Tipo estadoDiferente = new Tipo();
	Tipo estadoNinguno = new Tipo();

	// Tipos de Estado de Conciliacion
	TipoTipo estadoConciliacion = new TipoTipo();
	Tipo estadoConciliacion1 = new Tipo();
	Tipo estadoConciliacion2 = new Tipo();
	Tipo estadoConciliacion3 = new Tipo();

	// Cargos de Funcionarios
	TipoTipo funcionarioCargos = new TipoTipo();
	Tipo cargoJefeVentas = new Tipo();
	Tipo cargoVentasMostrador = new Tipo();
	Tipo cargoSoporteTecnico = new Tipo();
	Tipo cargoGerenteRRHH = new Tipo();
	Tipo cargoAuxiliarAdministrativo = new Tipo();
	Tipo cargoGerenteAdministrativo = new Tipo();
	Tipo cargoEncargadoCompras = new Tipo();
	Tipo cargoEncargadoDeposito = new Tipo();
	Tipo cargoVentaExterna = new Tipo();

	// Estados de Funcionarios
	TipoTipo funcionarioEstados = new TipoTipo();
	Tipo funcionarioEstadoActivo = new Tipo();
	Tipo funcionarioEstadoInactivo = new Tipo();

	// Motivos de Notas de Credito
	TipoTipo notaCreditoMotivos = new TipoTipo();
	Tipo nc_motivo_descuento = new Tipo();
	Tipo nc_motivo_devolucion = new Tipo();
	Tipo nc_motivo_dif_precio = new Tipo();
	Tipo nc_motivo_reclamo = new Tipo();

	// Tipos de Detalle Nota de Credito
	TipoTipo notaCreditoDetalleTipos = new TipoTipo();
	Tipo ncDet_factura = new Tipo();
	Tipo ncDet_articulo = new Tipo();

	// Estados de Comprobantes (Usado por los movimientos)
	TipoTipo estadosComprobantes = new TipoTipo();
	Tipo estadoComprobante_pendiente = new Tipo();
	Tipo estadoComprobante_aprobado = new Tipo();
	Tipo estadoComprobante_cerrado = new Tipo();
	Tipo estadoComprobante_anulado = new Tipo();
	Tipo estadoComprobante_confeccionado = new Tipo();

	// Tipos de tarjetas
	TipoTipo tipoTarjetas = new TipoTipo();
	Tipo tipoTarjetas1 = new Tipo();
	Tipo tipoTarjetas2 = new Tipo();
	Tipo tipoTarjetas3 = new Tipo();
	Tipo tipoTarjetas4 = new Tipo();
	Tipo tipoTarjetas5 = new Tipo();
	Tipo tipoTarjetas6 = new Tipo();
	Tipo tipoTarjetas7 = new Tipo();
	Tipo tipoTarjetas8 = new Tipo();
	Tipo tipoTarjetas9 = new Tipo();

	// Procesadora de tarjetas
	TipoTipo procesadoraTarjeta = new TipoTipo();
	Tipo procesadoraProcard = new Tipo();
	Tipo procesadoraPanal = new Tipo();
	Tipo procesadoraBancard = new Tipo();
	Tipo procesadoraCabal = new Tipo();

	// Tipos de Cuentas Contables
	TipoTipo tipoCtaContable = new TipoTipo();
	Tipo tipoCtaContableActivo = new Tipo();
	Tipo tipoCtaContablePasivo = new Tipo();
	Tipo tipoCtaContablePatrimonioNeto = new Tipo();
	Tipo tipoCtaContableIngreso = new Tipo();
	Tipo tipoCtaContableEgreso = new Tipo();

	// Tipo de documentos correspondiente a los tipos de movimientos
	TipoTipo tipoDocumento = new TipoTipo();
	Tipo tipoFacturaContado = new Tipo();
	Tipo tipoFacturaCredito = new Tipo();
	Tipo tipoNotaCredito = new Tipo();
	Tipo tipoNotaDebito = new Tipo();
	Tipo tipoReciboDinero = new Tipo();
	Tipo tipoAutoFactura = new Tipo();
	Tipo tipoPagare = new Tipo();
	Tipo tipoNotaRemision = new Tipo();
	Tipo tipoOtrosDocumentos = new Tipo();

	// Tipos de Reservas
	TipoTipo tipoReserva = new TipoTipo();
	Tipo reservaInterna = new Tipo();
	Tipo reservaReparto = new Tipo();
	Tipo reservaVenta = new Tipo();
	Tipo reservaDevolucion = new Tipo();

	// Estados de Reservas
	TipoTipo estadoReserva = new TipoTipo();
	Tipo estadoReservaActiva = new Tipo();
	Tipo estadoReservaCancelada = new Tipo();
	Tipo estadoReservaFinalizada = new Tipo();

	// Motivos de Devolucion
	TipoTipo motivoDevolucion = new TipoTipo();
	Tipo motivoDevolucionDiaSguiente = new Tipo();
	Tipo motivoDevolucionEntregaParcial = new Tipo();
	Tipo motivoDevolucionEntregaParcialDefectuoso = new Tipo();

	// Si NO
	TipoTipo siNo = new TipoTipo();
	Tipo siNo_SI = new Tipo();
	public Tipo siNo_NO = new Tipo();

	TipoTipo tipoBancoDeposito = new TipoTipo();
	Tipo bancoDepEfectivo = new Tipo();
	Tipo bancoDepChequesBanco = new Tipo();
	Tipo bancoDepChequesOtrosBancos = new Tipo();
	Tipo bancoDepTodos = new Tipo();
	
	
	//tipo Modo de creacion de un cheque
	TipoTipo tipoModoDeCreacionCheque = new TipoTipo();
	Tipo chequeAutomatico = new Tipo();
	Tipo chequeManual = new Tipo();
	
	// Bancos para Cheques de Terceros
	TipoTipo bancosTerceros = new TipoTipo();
	Tipo bancoTerceroLloyds = new Tipo();
	Tipo bancoTerceroIntegracion = new Tipo();
	Tipo bancoTerceroContinental = new Tipo();
	Tipo bancoTerceroAtlas = new Tipo();
	Tipo bancoTerceroRegional = new Tipo();
	Tipo bancoTerceroSudameris = new Tipo();
	Tipo bancoTerceroABN = new Tipo();
	Tipo bancoTerceroHSBC = new Tipo();
	Tipo bancoTerceroBBVA = new Tipo();
	Tipo bancoTerceroItau = new Tipo();
	Tipo bancoTerceroAmambay = new Tipo();
	Tipo bancoTerceroCitibank = new Tipo();
	Tipo bancoTerceroBancop = new Tipo();
	Tipo bancoTerceroGNB = new Tipo();
	Tipo bancoTerceroBrasil = new Tipo();	
	
	//Tarjeta Extracto
	TipoTipo tipoTarjetaExtractoDetalle = new TipoTipo();
	Tipo tipoTarjetaExtractoDetalleTE = new Tipo();
	Tipo tipoTarjetaExtractoDetalleBM = new Tipo();	
	
	// ==================================================================
	// ==================================================================
	// ==================================================================
	// ==================================================================

	public void cargaTiposGeneral() throws Exception {

		sinReferencia.setDescripcion("sin referencia");
		grabarDB(sinReferencia);

		sinReferenciaTipo.setTipoTipo(sinReferencia);
		sinReferenciaTipo.setDescripcion("sin referencia");
		sinReferenciaTipo.setSigla("sin-referencia");
		grabarDB(sinReferenciaTipo);

		/************************************** TRANSFERENCIA TIPO **************************************/
		transferenciaTipo.setDescripcion("Tipo Transferencia");
		grabarDB(transferenciaTipo);

		transfTipo1.setTipoTipo(this.transferenciaTipo);
		transfTipo1.setDescripcion("TRANSF. INTERNA");
		transfTipo1.setSigla(Configuracion.SIGLA_TM_TRANSF_INTERNA);

		transfTipo2.setTipoTipo(this.transferenciaTipo);
		transfTipo2.setDescripcion("TRANSF. EXTERNA");
		transfTipo2.setSigla(Configuracion.SIGLA_TM_TRANSF_EXTERNA);

		grabarDB(transfTipo1);
		grabarDB(transfTipo2);

		/************************************** TRASNFERENCIA ESTADO ************************************/

		transferenciaEstado.setDescripcion("Estado Transferencia");
		grabarDB(transferenciaEstado);

		transfEstado1.setTipoTipo(this.transferenciaEstado);
		transfEstado1.setDescripcion("ELABORACION");
		transfEstado1.setSigla(Configuracion.SIGLA_ESTADO_TRANSF_ELABORACION);

		transfEstado2.setTipoTipo(this.transferenciaEstado);
		transfEstado2.setDescripcion("PENDIENTE");
		transfEstado2.setSigla(Configuracion.SIGLA_ESTADO_TRANSF_PENDIENTE);

		transfEstado3.setTipoTipo(this.transferenciaEstado);
		transfEstado3.setDescripcion("PREPARACION");
		transfEstado3.setSigla("TRF-PREP");

		transfEstado4.setTipoTipo(this.transferenciaEstado);
		transfEstado4.setDescripcion("CONFIRMADO");
		transfEstado4.setSigla(Configuracion.SIGLA_ESTADO_TRANSF_CONFIRMADA);

		transfEstado5.setTipoTipo(this.transferenciaEstado);		
		transfEstado5.setDescripcion("RECIBIDO");
		transfEstado5.setSigla(Configuracion.SIGLA_ESTADO_TRANSF_RECIBIDA);

		transfEstado6.setTipoTipo(this.transferenciaEstado);
		transfEstado6.setDescripcion("FINALIZADA");
		transfEstado6.setSigla("TRF-FINA");

		transfEstado7.setTipoTipo(this.transferenciaEstado);
		transfEstado7.setDescripcion("CANCELADA");
		transfEstado7.setSigla("TRF-CANC");

		grabarDB(transfEstado1);
		grabarDB(transfEstado2);
		grabarDB(transfEstado3);
		grabarDB(transfEstado4);
		grabarDB(transfEstado5);
		grabarDB(transfEstado6);
		grabarDB(transfEstado7);

		// TIPOS DE GASTOS EN COMPRAS LOCALES
		cmlgt.setDescripcion(Configuracion.ID_TIPO_GASTO_COMPRA_LOCAL);
		grabarDB(cmlgt);

		cmlgt1.setDescripcion("GASTOS FLETE");
		cmlgt1.setSigla("GTS-FLE");
		cmlgt1.setTipoTipo(this.cmlgt);
		grabarDB(cmlgt1);

		cmlgt2.setDescripcion("OTROS GASTOS");
		cmlgt2.setSigla("GTS-OTR");
		cmlgt2.setTipoTipo(this.cmlgt);
		grabarDB(cmlgt2);

		// TIPOS DE REPARTO
		repartoTipo.setDescripcion(Configuracion.ID_TIPO_REPARTOS);
		grabarDB(repartoTipo);

		tipoReparto1.setDescripcion("TERMINAL");
		tipoReparto1.setSigla(Configuracion.SIGLA_TIPO_REPARTO_TERMINAL);
		tipoReparto1.setTipoTipo(this.repartoTipo);
		grabarDB(tipoReparto1);

		tipoReparto2.setDescripcion("COLECTIVO");
		tipoReparto2.setSigla(Configuracion.SIGLA_TIPO_REPARTO_COLECTIVO);
		tipoReparto2.setTipoTipo(this.repartoTipo);
		grabarDB(tipoReparto2);

		tipoReparto3.setDescripcion("ENCOMIENDA");
		tipoReparto3.setSigla(Configuracion.SIGLA_TIPO_REPARTO_ENCOMIENDA);
		tipoReparto3.setTipoTipo(this.repartoTipo);
		grabarDB(tipoReparto3);

		tipoReparto4.setDescripcion("YHAGUY");
		tipoReparto4.setSigla(Configuracion.SIGLA_TIPO_REPARTO_YHAGUY);
		tipoReparto4.setTipoTipo(this.repartoTipo);
		grabarDB(tipoReparto4);

		// ESTADO REPARTO
		estadoReparto.setDescripcion("Estado Reparto");
		grabarDB(estadoReparto);

		estadoReparto1.setDescripcion("EN PREPARACION");
		estadoReparto1.setSigla(Configuracion.SIGLA_ESTADO_REP_PREPARACION);
		estadoReparto1.setTipoTipo(this.estadoReparto);
		grabarDB(estadoReparto1);

		estadoReparto2.setDescripcion("PREPARADO");
		estadoReparto2.setSigla("REP-PREP");
		estadoReparto2.setTipoTipo(this.estadoReparto);
		grabarDB(estadoReparto2);

		estadoReparto3.setDescripcion("EN TRANSITO");
		estadoReparto3.setSigla(Configuracion.SIGLA_ESTADO_REP_TRANSITO);
		estadoReparto3.setTipoTipo(this.estadoReparto);
		grabarDB(estadoReparto3);

		estadoReparto4.setDescripcion("ENTREGADO");
		estadoReparto4.setSigla(Configuracion.SIGLA_ESTADO_REP_ENTREGADO);
		estadoReparto4.setTipoTipo(this.estadoReparto);
		grabarDB(estadoReparto4);

		estadoReparto5.setDescripcion("FINALIZADO CON OBSERVACIONES");
		estadoReparto5.setSigla("REP-FINO");
		estadoReparto5.setTipoTipo(this.estadoReparto);
		grabarDB(estadoReparto5);

		estadoReparto6.setDescripcion("CANCELADO");
		estadoReparto6.setSigla("REP-CANC");
		estadoReparto6.setTipoTipo(this.estadoReparto);
		grabarDB(estadoReparto6);

		// TIPO VENTA
		ventaTipo.setDescripcion("Tipo Venta");
		grabarDB(ventaTipo);

		tipoVta1.setDescripcion("MOSTRADOR");
		tipoVta1.setSigla("VTA-MOS");
		tipoVta1.setTipoTipo(this.ventaTipo);
		grabarDB(tipoVta1);

		tipoVta2.setDescripcion("VENDEDOR");
		tipoVta2.setSigla("VTA-VEN");
		tipoVta2.setTipoTipo(this.ventaTipo);
		grabarDB(tipoVta2);

		tipoVta3.setDescripcion("TERMINAL");
		tipoVta3.setSigla("VTA-TER");
		tipoVta3.setTipoTipo(this.ventaTipo);
		grabarDB(tipoVta3);

		tipoVta4.setDescripcion("ENCOMIENDA");
		tipoVta4.setSigla("VTA-ENC");
		tipoVta4.setTipoTipo(this.ventaTipo);
		grabarDB(tipoVta4);

		tipoVta6.setDescripcion("COLECTIVO");
		tipoVta6.setSigla("VTA-COL");
		tipoVta6.setTipoTipo(this.ventaTipo);
		grabarDB(tipoVta6);

		tipoVta7.setDescripcion("REPARTO");
		tipoVta7.setSigla("VTA-REP");
		tipoVta7.setTipoTipo(this.ventaTipo);
		grabarDB(tipoVta7);

		// NOTAS DE CREDITO
		nct.setDescripcion("Tipos de Nota de Credito");
		grabarDB(nct);

		nct1.setDescripcion("N.C. COMPRA");
		nct1.setSigla("NCR-COM");
		nct1.setTipoTipo(this.nct);
		grabarDB(nct1);

		nct2.setDescripcion("N.C. VENTA");
		nct2.setSigla("NCR-VEN");
		nct2.setTipoTipo(this.nct);
		grabarDB(nct2);

		// ============= MONEDAS ==============
		moneda.setDescripcion(Configuracion.ID_TIPO_MONEDA);
		grabarDB(moneda);

		guarani.setDescripcion(Configuracion.ID_TIPO_MONEDA_GUARANI);
		guarani.setSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		guarani.setTipoTipo(moneda);
		grabarDB(guarani);

		dolar.setDescripcion(Configuracion.ID_TIPO_MONEDA_DOLAR);
		dolar.setSigla(Configuracion.SIGLA_MONEDA_DOLAR);
		dolar.setTipoTipo(moneda);
		grabarDB(dolar);

		peso.setDescripcion("Peso");
		peso.setSigla(Configuracion.SIGLA_MONEDA_PESO);
		peso.setTipoTipo(moneda);
		grabarDB(peso);

		euro.setDescripcion("Euro");
		euro.setSigla(Configuracion.SIGLA_MONEDA_EURO);
		euro.setTipoTipo(moneda);
		grabarDB(euro);

		tipoCambio.setDescripcion("Tipo Cambio");
		grabarDB(tipoCambio);

		tipoCambioYhaguy.setDescripcion(Configuracion.ID_TIPO_CAMBIO_APP);
		tipoCambioYhaguy.setSigla(Configuracion.SIGLA_TIPO_CAMBIO_APP);
		tipoCambioYhaguy.setTipoTipo(tipoCambio);
		grabarDB(tipoCambioYhaguy);

		tipoCambioOficial.setDescripcion(Configuracion.ID_TIPO_CAMBIO_BCP);
		tipoCambioOficial.setSigla(Configuracion.SIGLA_TIPO_CAMBIO_BCP);
		tipoCambioOficial.setTipoTipo(tipoCambio);
		grabarDB(tipoCambioOficial);

		// ============= E-MAILS EMPRESA ==============
		emailEmpresa.setDescripcion(Configuracion.ID_TIPO_EMPRESA_EMAILS);
		grabarDB(emailEmpresa);

		emailCompra.setDescripcion("Compra");
		emailCompra.setSigla("E-COM");
		emailCompra.setTipoTipo(emailEmpresa);
		grabarDB(emailCompra);

		emailVenta.setDescripcion("Venta");
		emailVenta.setSigla("E-VEN");
		emailVenta.setTipoTipo(emailEmpresa);
		grabarDB(emailVenta);

		emailUnico.setDescripcion("Único");
		emailUnico.setSigla("E-UNI");
		emailUnico.setTipoTipo(emailEmpresa);
		grabarDB(emailUnico);

		emailPromocion.setDescripcion("Promoción");
		emailPromocion.setSigla("E-PRO");
		emailPromocion.setTipoTipo(emailEmpresa);
		grabarDB(emailPromocion);

		// ============= PAISES ==============
		paisEmpresa.setDescripcion(Configuracion.ID_TIPO_PAIS_EMPRESA);
		grabarDB(paisEmpresa);

		paisPry.setDescripcion("Paraguay");
		paisPry.setSigla(Configuracion.SIGLA_PAIS_PARAGUAY);
		paisPry.setTipoTipo(paisEmpresa);
		grabarDB(paisPry);

		paisNoDefinido.setDescripcion("sin definir");
		paisNoDefinido.setSigla(Configuracion.SIGLA_PAIS_NO_DEFINIDO);
		paisNoDefinido.setTipoTipo(paisEmpresa);
		grabarDB(paisNoDefinido);

		paisArg.setDescripcion("Argentina");
		paisArg.setSigla("ARG");
		paisArg.setTipoTipo(paisEmpresa);
		grabarDB(paisArg);

		paisBra.setDescripcion("Brasil");
		paisBra.setSigla("BRA");
		paisBra.setTipoTipo(paisEmpresa);
		grabarDB(paisBra);

		paisBol.setDescripcion("Bolivia");
		paisBol.setSigla("BOL");
		paisBol.setTipoTipo(paisEmpresa);
		grabarDB(paisBol);

		paisUry.setDescripcion("Uruguay");
		paisUry.setSigla("URY");
		paisUry.setTipoTipo(paisEmpresa);
		grabarDB(paisUry);

		paisUsa.setDescripcion("Estados Unidos");
		paisUsa.setSigla("USA");
		paisUsa.setTipoTipo(paisEmpresa);
		grabarDB(paisUsa);

		paisJpn.setDescripcion("Japón");
		paisJpn.setSigla("JPN");
		paisJpn.setTipoTipo(paisEmpresa);
		grabarDB(paisJpn);

		paisChn.setDescripcion("China");
		paisChn.setSigla("CHN");
		paisChn.setTipoTipo(paisEmpresa);
		grabarDB(paisChn);

		paisTur.setDescripcion("Turquía");
		paisTur.setSigla("TUR");
		paisTur.setTipoTipo(paisEmpresa);
		grabarDB(paisTur);

		// ============= TIPO PERSONA ==============
		tipoPersona.setDescripcion(Configuracion.ID_TIPO_PERSONA);
		grabarDB(tipoPersona);

		personaFisica.setDescripcion("Física");
		personaFisica.setSigla(Configuracion.SIGLA_TIPO_PERSONA_FISICA);
		personaFisica.setTipoTipo(tipoPersona);
		grabarDB(personaFisica);

		personaJuridica.setDescripcion("Jurídica");
		personaJuridica.setSigla(Configuracion.SIGLA_TIPO_PERSONA_JURIDICA);
		personaJuridica.setTipoTipo(tipoPersona);
		grabarDB(personaJuridica);

		// ============= BANCO CUENTA ==============
		bancoCtaTipo.setDescripcion(Configuracion.ID_TIPO_BANCO_CUENTA);
		grabarDB(bancoCtaTipo);

		bancoCtaCte.setDescripcion("Cuenta Corriente");
		bancoCtaCte.setSigla("BCO-CTA-CTE");
		bancoCtaCte.setTipoTipo(bancoCtaTipo);
		grabarDB(bancoCtaCte);

		bancoCajaAhorro.setDescripcion("Caja de Ahorro");
		bancoCajaAhorro.setSigla("BCO-CAJ-AHORRO");
		bancoCajaAhorro.setTipoTipo(bancoCtaTipo);
		grabarDB(bancoCajaAhorro);
		

		/*********************** CAJA ***********************/
		
		cajaReposicion.setDescripcion(Configuracion.ID_TIPO_CAJA_REPOSICION);
		grabarDB(cajaReposicion);

		cajaRepIngreso.setDescripcion("Ingreso de Caja");
		cajaRepIngreso.setSigla(Configuracion.SIGLA_CAJA_REPOSICION_INGRESO);
		cajaRepIngreso.setTipoTipo(cajaReposicion);
		grabarDB(cajaRepIngreso);

		cajaRepEgreso.setDescripcion("Egreso de Caja");
		cajaRepEgreso.setSigla(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO);
		cajaRepEgreso.setTipoTipo(cajaReposicion);
		grabarDB(cajaRepEgreso);
		
		cajaReposicionEgreso.setDescripcion(Configuracion.ID_TIPO_CAJA_REPOSICION_EGRESO);
		grabarDB(cajaReposicionEgreso);
		
		cajaRepEgresoSinCmpbte.setDescripcion("Egreso Sin Comprobante");
		cajaRepEgresoSinCmpbte.setSigla(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO_SIN_COMP);
		cajaRepEgresoSinCmpbte.setTipoTipo(cajaReposicionEgreso);
		grabarDB(cajaRepEgresoSinCmpbte);
		
		cajaRepEgresoVale.setDescripcion("Egreso por Vale");
		cajaRepEgresoVale.setSigla(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO_VALE);
		cajaRepEgresoVale.setTipoTipo(cajaReposicionEgreso);
		grabarDB(cajaRepEgresoVale);
		
		cajaRepEgresoPrestamo.setDescripcion("Egreso por Prestamo");
		cajaRepEgresoPrestamo.setSigla(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO_PRESTAMO);
		cajaRepEgresoPrestamo.setTipoTipo(cajaReposicionEgreso);
		grabarDB(cajaRepEgresoPrestamo);
		
		cajaRepEgresoGratificacion.setDescripcion("Egreso por Gratificacion");
		cajaRepEgresoGratificacion.setSigla(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO_GRATIFICACION);
		cajaRepEgresoGratificacion.setTipoTipo(cajaReposicionEgreso);
		grabarDB(cajaRepEgresoGratificacion);

		cajaClasificacion.setDescripcion(Configuracion.ID_TIPO_CAJA_CLASIFICACION);
		grabarDB(cajaClasificacion);

		cajaClasificacion1.setDescripcion("Proveedores Locales");
		cajaClasificacion1.setSigla("CAJ-PROV-LOC");
		cajaClasificacion1.setTipoTipo(cajaClasificacion);
		grabarDB(cajaClasificacion1);

		cajaClasificacion2.setDescripcion("Proveedores Internacionales");
		cajaClasificacion2.setSigla("CAJ-PROV-INT");
		cajaClasificacion2.setTipoTipo(cajaClasificacion);
		grabarDB(cajaClasificacion2);

		cajaTipo.setDescripcion(Configuracion.ID_TIPO_CAJA);
		grabarDB(cajaTipo);

		cajaTipo1.setDescripcion("Caja Pagos a Proveedores");
		cajaTipo1.setSigla("CAJ-PRV");
		cajaTipo1.setTipoTipo(cajaTipo);
		grabarDB(cajaTipo1);

		cajaTipo2.setDescripcion("Caja Chica");
		cajaTipo2.setSigla("CAJ-CH");
		cajaTipo2.setTipoTipo(cajaTipo);
		grabarDB(cajaTipo2);
		
		cajaTipo3.setDescripcion("Caja de Movimientos Varios");
		cajaTipo3.setSigla(Configuracion.SIGLA_CAJA_TIPO_MOVIMIENTOS_VARIOS);
		cajaTipo3.setTipoTipo(cajaTipo);
		grabarDB(cajaTipo3);

		cajaEstado.setDescripcion(Configuracion.ID_TIPO_CAJA_ESTADO);
		grabarDB(cajaEstado);

		cajaEstado1.setDescripcion("Activo");
		cajaEstado1.setSigla(Configuracion.SIGLA_CAJA_ESTADO_ACTIVO);
		cajaEstado1.setTipoTipo(cajaEstado);
		grabarDB(cajaEstado1);

		cajaEstado2.setDescripcion("Inactivo");
		cajaEstado2.setSigla(Configuracion.SIGLA_CAJA_ESTADO_INACTIVO);
		cajaEstado2.setTipoTipo(cajaEstado);
		grabarDB(cajaEstado2);
		
		cajaPeriodoEstado.setDescripcion(Configuracion.ID_TIPO_CAJA_PERIODO_ESTADO);
		grabarDB(cajaPeriodoEstado);
		
		cajaAbierta.setDescripcion("Abierta");
		cajaAbierta.setSigla(Configuracion.SIGLA_CAJA_PERIODO_ABIERTA);
		cajaAbierta.setTipoTipo(cajaPeriodoEstado);
		grabarDB(cajaAbierta);

		cajaCerrada.setDescripcion("Cerrada");
		cajaCerrada.setSigla(Configuracion.SIGLA_CAJA_PERIODO_CERRADA);
		cajaCerrada.setTipoTipo(cajaPeriodoEstado);
		grabarDB(cajaCerrada);
		
		cajaProcesada.setDescripcion("Procesada");
		cajaProcesada.setSigla(Configuracion.SIGLA_CAJA_PERIODO_PROCESADA);
		cajaProcesada.setTipoTipo(cajaPeriodoEstado);
		grabarDB(cajaProcesada);

		
		cajaDuracion.setDescripcion(Configuracion.ID_TIPO_CAJA_DURACION);
		grabarDB(cajaDuracion);

		cajaDuracion1.setDescripcion("Cierre Diario");
		cajaDuracion1.setSigla("CAJ-CI-DIA");
		cajaDuracion1.setTipoTipo(cajaDuracion);
		grabarDB(cajaDuracion1);

		cajaDuracion2.setDescripcion("Cierre Semanal");
		cajaDuracion2.setSigla("CAJ-CI-SEM");
		cajaDuracion2.setTipoTipo(cajaDuracion);
		grabarDB(cajaDuracion2);

		cajaDuracion3.setDescripcion("Cierre Mensual");
		cajaDuracion3.setSigla("CAJ-CI-MEN");
		cajaDuracion3.setTipoTipo(cajaDuracion);
		grabarDB(cajaDuracion3);

		// ================= ESTADOS DE IMPORTACION ===============

		importacionEstado
				.setDescripcion(Configuracion.ID_TIPO_IMPORTACION_ESTADO);
		grabarDB(importacionEstado);

		importacionEstado1.setDescripcion("Confeccionando el Pedido");
		importacionEstado1
				.setSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_CONFIRMADO);
		importacionEstado1.setTipoTipo(importacionEstado);
		grabarDB(importacionEstado1);

		importacionEstado2.setDescripcion("Solicitando Cotizacion");
		importacionEstado2
				.setSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_SOLICITANDO_COTIZACION);
		importacionEstado2.setTipoTipo(importacionEstado);
		grabarDB(importacionEstado2);

		importacionEstado3.setDescripcion("Proforma Recibida");
		importacionEstado3
				.setSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_PROFORMA_RECIBIDA);
		importacionEstado3.setTipoTipo(importacionEstado);
		grabarDB(importacionEstado3);

		importacionEstado4.setDescripcion("Pendiente de Envio");
		importacionEstado4
				.setSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_PENDIENTE_DE_ENVIO);
		importacionEstado4.setTipoTipo(importacionEstado);
		grabarDB(importacionEstado4);

		importacionEstado5.setDescripcion("Pedido Enviado");
		importacionEstado5
				.setSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_PEDIDO_ENVIADO);
		importacionEstado5.setTipoTipo(importacionEstado);
		grabarDB(importacionEstado5);

		importacionEstado6.setDescripcion("Importación Cerrada");
		importacionEstado6
				.setSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_CERRADO);
		importacionEstado6.setTipoTipo(importacionEstado);
		grabarDB(importacionEstado6);
		importacionEstado7.setDescripcion("Importación Anulada");
		importacionEstado7
				.setSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_ANULADO);
		importacionEstado7.setTipoTipo(importacionEstado);
		grabarDB(importacionEstado7);

		/********************** ESTADOS DE RECIBOS **********************/

		reciboEstado.setDescripcion(Configuracion.ID_TIPO_RECIBO_ESTADO);
		grabarDB(reciboEstado);

		reciboEstado1.setDescripcion("Guardado");
		reciboEstado1.setSigla(Configuracion.SIGLA_RECIBO_ESTADO_GUARDADO);
		reciboEstado1.setTipoTipo(reciboEstado);
		grabarDB(reciboEstado1);

		reciboEstado2.setDescripcion("Anulado");
		reciboEstado2.setSigla(Configuracion.SIGLA_RECIBO_ESTADO_ANULADO);
		reciboEstado2.setTipoTipo(reciboEstado);
		grabarDB(reciboEstado2);

		/************************ FORMAS DE PAGO ************************/

		reciboFormaPago.setDescripcion(Configuracion.ID_TIPO_RECIBO_FORMA_PAGO);
		grabarDB(reciboFormaPago);

		reciboFormaPagoEfectivo.setDescripcion("Efectivo");
		reciboFormaPagoEfectivo
				.setSigla(Configuracion.SIGLA_FORMA_PAGO_EFECTIVO);
		reciboFormaPagoEfectivo.setTipoTipo(reciboFormaPago);
		grabarDB(reciboFormaPagoEfectivo);

		reciboFormaPagoChequePropio.setDescripcion("Cheque Propio");
		reciboFormaPagoChequePropio
				.setSigla(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO);
		reciboFormaPagoChequePropio.setTipoTipo(reciboFormaPago);
		grabarDB(reciboFormaPagoChequePropio);

		reciboFormaPagoChequeTercero.setDescripcion("Cheque Tercero");
		reciboFormaPagoChequeTercero
				.setSigla(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO);
		reciboFormaPagoChequeTercero.setTipoTipo(reciboFormaPago);
		grabarDB(reciboFormaPagoChequeTercero);
		
		reciboFormaPagoTarjetaCredito.setDescripcion("Tarjeta de Crédito");
		reciboFormaPagoTarjetaCredito
				.setSigla(Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO);
		reciboFormaPagoTarjetaCredito.setTipoTipo(reciboFormaPago);
		grabarDB(reciboFormaPagoTarjetaCredito);

		reciboFormaPagoTarjetaDebito.setDescripcion("Tarjeta de Débito");
		reciboFormaPagoTarjetaDebito
				.setSigla(Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO);
		reciboFormaPagoTarjetaDebito.setTipoTipo(reciboFormaPago);
		grabarDB(reciboFormaPagoTarjetaDebito);
		
		reciboFormaPagoRetencion.setDescripcion("Retención IVA");
		reciboFormaPagoRetencion
				.setSigla(Configuracion.SIGLA_FORMA_PAGO_RETENCION);
		reciboFormaPagoRetencion.setTipoTipo(reciboFormaPago);
		grabarDB(reciboFormaPagoRetencion);
		
		reciboFormaPagoDepositoBancario.setDescripcion("Depósito Bancario");
		reciboFormaPagoDepositoBancario
				.setSigla(Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO);
		reciboFormaPagoDepositoBancario.setTipoTipo(reciboFormaPago);
		grabarDB(reciboFormaPagoDepositoBancario);

		// ================= CTA CTE PROVEEDOR ===============

		ctaCteProveedorEstado
				.setDescripcion(Configuracion.ID_TIPO_CTA_CTE_PROVEEDOR_ESTADO);
		grabarDB(ctaCteProveedorEstado);

		ctaCteProveedorEstado1.setDescripcion("Activo");
		ctaCteProveedorEstado1.setSigla("CC-PRV-ACT");
		ctaCteProveedorEstado1.setTipoTipo(ctaCteProveedorEstado);
		grabarDB(ctaCteProveedorEstado1);

		ctaCteProveedorEstado2.setDescripcion("Inactivo");
		ctaCteProveedorEstado2.setSigla("CC-PRV-INAC");
		ctaCteProveedorEstado2.setTipoTipo(ctaCteProveedorEstado);
		grabarDB(ctaCteProveedorEstado2);

		// ==========Imputacion Movimiento Cta Cte=============

		ctaCtetipoImputacion
				.setDescripcion(Configuracion.ID_TIPO_CTA_CTE_EMPRESA_IMPUTACION);
		grabarDB(ctaCtetipoImputacion);

		ctaCtetipoImputacion1
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_IMPUTACION_PARCIAL);
		ctaCtetipoImputacion1
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_IMPUTACION_PARCIAL);
		ctaCtetipoImputacion1.setTipoTipo(ctaCtetipoImputacion);
		grabarDB(ctaCtetipoImputacion1);

		ctaCtetipoImputacion2
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_IMPUTACION_COMPLETA);
		ctaCtetipoImputacion2
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_IMPUTACION_COMPLETA);
		ctaCtetipoImputacion2.setTipoTipo(ctaCtetipoImputacion);
		grabarDB(ctaCtetipoImputacion2);

		// ===================Cta Cte Empresa==================

		ctaCteEmpresaEstado
				.setDescripcion(Configuracion.ID_TIPO_CTA_CTE_EMPRESA_ESTADO);
		grabarDB(ctaCteEmpresaEstado);

		ctaCteEmpresaEstado1
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_ESTADO_ACTIVO);
		ctaCteEmpresaEstado1
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_ACTIVO);
		ctaCteEmpresaEstado1.setTipoTipo(ctaCteEmpresaEstado);
		grabarDB(ctaCteEmpresaEstado1);

		ctaCteEmpresaEstado2
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_ESTADO_INACTIVO);
		ctaCteEmpresaEstado2
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_INACTIVO);
		ctaCteEmpresaEstado2.setTipoTipo(ctaCteEmpresaEstado);
		grabarDB(ctaCteEmpresaEstado2);

		ctaCteEmpresaEstado3
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_ESTADO_BLOQUEADO);
		ctaCteEmpresaEstado3
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_BLOQUEADO);
		ctaCteEmpresaEstado3.setTipoTipo(ctaCteEmpresaEstado);
		grabarDB(ctaCteEmpresaEstado3);

		ctaCteEmpresaEstado4
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_ESTADO_SINCUENTA);
		ctaCteEmpresaEstado4
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA);
		ctaCteEmpresaEstado4.setTipoTipo(ctaCteEmpresaEstado);
		grabarDB(ctaCteEmpresaEstado4);

		CtaCteLineaCredito l3 = new CtaCteLineaCredito();
		l3.setLinea(0);
		l3.setDescripcion(Configuracion.CTA_CTE_EMPRESA_LINEA_CREDITO_DEFAULT);
		grabarDB(l3);

		// ==== Caracter del Movimiento de la CtaCte Empresa====

		ctaCteEmpresaCaracterMovimiento
				.setDescripcion(Configuracion.ID_TIPO_CTA_CTE_EMPRESA_CARACTER_MOVIMIENTO);
		grabarDB(ctaCteEmpresaCaracterMovimiento);

		ctaCteEmpresaCaracterMovimiento1
				.setDescripcion(Configuracion.TIPO_CTA_CTE_CARACTER_MOV_CLIENTE);
		ctaCteEmpresaCaracterMovimiento1
				.setSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE);
		ctaCteEmpresaCaracterMovimiento1
				.setTipoTipo(ctaCteEmpresaCaracterMovimiento);
		grabarDB(ctaCteEmpresaCaracterMovimiento1);

		ctaCteEmpresaCaracterMovimiento2
				.setDescripcion(Configuracion.TIPO_CTA_CTE_CARACTER_MOV_PROVEEDOR);
		ctaCteEmpresaCaracterMovimiento2
				.setSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR);
		ctaCteEmpresaCaracterMovimiento2
				.setTipoTipo(ctaCteEmpresaCaracterMovimiento);
		grabarDB(ctaCteEmpresaCaracterMovimiento2);

		// ===================Seleccion de Movimientos Cta Cte
		// Empresa==================

		ctaCteEmpresaSaldoEstado
				.setDescripcion(Configuracion.ID_TIPO_CTA_CTE_EMPRESA_SELECCION_MOV);
		grabarDB(ctaCteEmpresaSaldoEstado);

		ctaCteEmpresaSaldoEstado1
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_TODOS);
		ctaCteEmpresaSaldoEstado1
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_SELECCION_MOV_TODOS);
		ctaCteEmpresaSaldoEstado1.setTipoTipo(ctaCteEmpresaSaldoEstado);
		grabarDB(ctaCteEmpresaSaldoEstado1);

		ctaCteEmpresaSaldoEstado2
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_PENDIENTES);
		ctaCteEmpresaSaldoEstado2
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_SELECCION_MOV_PENDIENTES);
		ctaCteEmpresaSaldoEstado2.setTipoTipo(ctaCteEmpresaSaldoEstado);
		grabarDB(ctaCteEmpresaSaldoEstado2);

		ctaCteEmpresaSaldoEstado3
				.setDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_VENCIDOS);
		ctaCteEmpresaSaldoEstado3
				.setSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_SELECCION_MOV_VENCIDOS);
		ctaCteEmpresaSaldoEstado3.setTipoTipo(ctaCteEmpresaSaldoEstado);
		grabarDB(ctaCteEmpresaSaldoEstado3);

		// ================= MOV ESTADO REPARTO ===============

		movEstadoReparto.setDescripcion("Estado Reparto Movimiento");
		grabarDB(movEstadoReparto);

		estadoMovRep1.setDescripcion("PENDIENTE");
		estadoMovRep1.setSigla("MOV-REP-PEN");
		estadoMovRep1.setTipoTipo(this.movEstadoReparto);
		grabarDB(estadoMovRep1);

		estadoMovRep2.setDescripcion("PREPARACION");
		estadoMovRep2.setSigla("MOV-REP-ENPRE");
		estadoMovRep2.setTipoTipo(this.movEstadoReparto);
		grabarDB(estadoMovRep2);

		estadoMovRep3.setDescripcion("PREPARADO");
		estadoMovRep3.setSigla("MOV-REP-PREP");
		estadoMovRep3.setTipoTipo(this.movEstadoReparto);
		grabarDB(estadoMovRep3);

		estadoMovRep4.setDescripcion("EN TRANSITO");
		estadoMovRep4.setSigla("MOV-REP-TRAN");
		estadoMovRep4.setTipoTipo(this.movEstadoReparto);
		grabarDB(estadoMovRep4);

		estadoMovRep5.setDescripcion("FINALIZADO");
		estadoMovRep5.setSigla("MOV-REP-FINA");
		estadoMovRep5.setTipoTipo(this.movEstadoReparto);
		grabarDB(estadoMovRep5);

		// ================= TIPOS DE GASTO / DESCUENTO / PRORRATEOS EN COMPRAS

		compraTipoDescuentoSAC
				.setDescripcion(Configuracion.ID_TIPO_DESCUENTO_COMPRA);
		grabarDB(compraTipoDescuentoSAC);

		tipoDescuento1.setDescripcion("PUBLICIDAD");
		tipoDescuento1.setSigla("COMP-DESC-PUB");
		tipoDescuento1.setTipoTipo(compraTipoDescuentoSAC);
		grabarDB(tipoDescuento1);

		tipoDescuento2.setDescripcion("FACTURACIÓN ANTERIOR");
		tipoDescuento2.setSigla("COMP-DESC-FAC-ANT");
		tipoDescuento2.setTipoTipo(compraTipoDescuentoSAC);
		grabarDB(tipoDescuento2);

		tipoDescuento3.setDescripcion("OTROS");
		tipoDescuento3.setSigla("COMP-DESC-OTROS");
		tipoDescuento3.setTipoTipo(compraTipoDescuentoSAC);
		grabarDB(tipoDescuento3);

		compraTipoGasto.setDescripcion(Configuracion.ID_TIPO_GASTO_COMPRA);
		grabarDB(compraTipoGasto);

		tipoCompraGasto1.setDescripcion("FLETE");
		tipoCompraGasto1.setSigla(Configuracion.SIGLA_TIPO_COMPRA_GASTO_1);
		tipoCompraGasto1.setTipoTipo(compraTipoGasto);
		grabarDB(tipoCompraGasto1);

		tipoCompraGasto2.setDescripcion("SEGURO");
		tipoCompraGasto2.setSigla(Configuracion.SIGLA_TIPO_COMPRA_GASTO_2);
		tipoCompraGasto2.setTipoTipo(compraTipoGasto);
		grabarDB(tipoCompraGasto2);

		tipoCompraGasto3.setDescripcion("OTROS");
		tipoCompraGasto3.setSigla(Configuracion.SIGLA_TIPO_COMPRA_GASTO_3);
		tipoCompraGasto3.setTipoTipo(compraTipoGasto);
		grabarDB(tipoCompraGasto3);

		compraTipoProrrateo
				.setDescripcion(Configuracion.ID_TIPO_PRORRATEO_COMPRA);
		grabarDB(compraTipoProrrateo);

		compraTipoProrrateo1.setDescripcion("PRORRATEO FLETE");
		compraTipoProrrateo1
				.setSigla(Configuracion.SIGLA_TIPO_COMPRA_PRORRATEO_1);
		compraTipoProrrateo1.setTipoTipo(compraTipoProrrateo);
		grabarDB(compraTipoProrrateo1);

		compraTipoProrrateo2.setDescripcion("PRORRATEO SEGURO");
		compraTipoProrrateo2
				.setSigla(Configuracion.SIGLA_TIPO_COMPRA_PRORRATEO_2);
		compraTipoProrrateo2.setTipoTipo(compraTipoProrrateo);
		grabarDB(compraTipoProrrateo2);

		/****************************** VENTA ****************************/

		ventaEstado.setDescripcion(Configuracion.ID_TIPO_ESTADO_VENTA);
		grabarDB(ventaEstado);

		venta_soloPresupuesto.setDescripcion("Solo Presupuesto");
		venta_soloPresupuesto
				.setSigla(Configuracion.SIGLA_VENTA_ESTADO_SOLO_PRESUPUESTO);
		venta_soloPresupuesto.setTipoTipo(ventaEstado);
		grabarDB(venta_soloPresupuesto);

		venta_pasadoPedido.setDescripcion("Pasado a Pedido");
		venta_pasadoPedido
				.setSigla(Configuracion.SIGLA_VENTA_ESTADO_PASADO_A_PEDIDO);
		venta_pasadoPedido.setTipoTipo(ventaEstado);
		grabarDB(venta_pasadoPedido);

		venta_soloPedido.setDescripcion("Solo Pedido");
		venta_soloPedido.setSigla(Configuracion.SIGLA_VENTA_ESTADO_SOLO_PEDIDO);
		venta_soloPedido.setTipoTipo(ventaEstado);
		grabarDB(venta_soloPedido);

		venta_pedidoCerrado.setDescripcion("Pedido Cerrado");
		venta_pedidoCerrado.setSigla(Configuracion.SIGLA_VENTA_ESTADO_CERRADO);
		venta_pedidoCerrado.setTipoTipo(ventaEstado);
		grabarDB(venta_pedidoCerrado);

		venta_pedidoFacturado.setDescripcion("Pedido Facturado");
		venta_pedidoFacturado
				.setSigla(Configuracion.SIGLA_VENTA_ESTADO_FACTURADO);
		venta_pedidoFacturado.setTipoTipo(ventaEstado);
		grabarDB(venta_pedidoFacturado);

		modoVenta.setDescripcion(Configuracion.ID_TIPO_MODO_VENTA);
		grabarDB(modoVenta);

		modoVenta_mostrador.setDescripcion("Venta Mostrador");
		modoVenta_mostrador
				.setSigla(Configuracion.SIGLA_TIPO_MODO_VENTA_MOSTRADOR);
		modoVenta_mostrador.setTipoTipo(modoVenta);
		grabarDB(modoVenta_mostrador);

		modoVenta_externa.setDescripcion("Venta Externa");
		modoVenta_externa.setSigla(Configuracion.SIGLA_TIPO_MODO_VENTA_EXTERNA);
		modoVenta_externa.setTipoTipo(modoVenta);
		grabarDB(modoVenta_externa);

		modoVenta_telefonica.setDescripcion("Venta Telefónica");
		modoVenta_telefonica
				.setSigla(Configuracion.SIGLA_TIPO_MODO_VENTA_TELEFONICA);
		modoVenta_telefonica.setTipoTipo(modoVenta);
		grabarDB(modoVenta_telefonica);

		/************************** TIPOS DE IVA ************************/

		tipoIva.setDescripcion(Configuracion.ID_TIPO_IVA);
		grabarDB(tipoIva);

		tipoIva1.setDescripcion("IVA 10%");
		tipoIva1.setSigla(Configuracion.SIGLA_IVA_10);
		tipoIva1.setTipoTipo(tipoIva);
		grabarDB(tipoIva1);

		tipoIva2.setDescripcion("IVA 5%");
		tipoIva2.setSigla(Configuracion.SIGLA_IVA_5);
		tipoIva2.setTipoTipo(tipoIva);
		grabarDB(tipoIva2);

		tipoIva3.setDescripcion("EXENTA");
		tipoIva3.setSigla(Configuracion.SIGLA_IVA_EXENTO);
		tipoIva3.setTipoTipo(tipoIva);
		grabarDB(tipoIva3);

		/************************** Tipo y Nivel Alerta ************************/

		tipoAlerta.setDescripcion(Configuracion.ID_TIPO_TIPO_ALERTA);
		grabarDB(tipoAlerta);

		alertaMuchos.setDescripcion("Muchos destinos, muchos canceladores");
		alertaMuchos.setSigla("DESTINO-MUCHOS");
		alertaMuchos.setTipoTipo(tipoAlerta);
		grabarDB(alertaMuchos);

		alertaUno.setDescripcion("Un destino, un cancelador");
		alertaUno.setSigla("DESTINO-UNO");
		alertaUno.setTipoTipo(tipoAlerta);
		grabarDB(alertaUno);

		alertaComunitaria.setDescripcion("Muchos destinos, algun cancelador");
		alertaComunitaria.setSigla("DESTINO-COMUN");
		alertaComunitaria.setTipoTipo(tipoAlerta);
		grabarDB(alertaComunitaria);

		nivelAlerta.setDescripcion(Configuracion.ID_TIPO_NIVEL_ALERTA);
		grabarDB(nivelAlerta);

		alertaInformativa.setDescripcion("Alerta informativa");
		alertaInformativa.setSigla("ALER-INFO");
		alertaInformativa.setTipoTipo(nivelAlerta);
		grabarDB(alertaInformativa);

		alertaError.setDescripcion("Alerta error");
		alertaError.setSigla("ALER-ERROR");
		alertaError.setTipoTipo(nivelAlerta);
		grabarDB(alertaError);

		/********************** Rubro Empresa ***************/

		tipoRubroEmpresa.setDescripcion(Configuracion.ID_TIPO_RUBRO_EMPRESAS);
		grabarDB(tipoRubroEmpresa);

		rubroEmpresaConsumidorFinal.setDescripcion("Consumidor Final");
		rubroEmpresaConsumidorFinal
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_CONSUMIDOR_FINAL);
		rubroEmpresaConsumidorFinal.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaConsumidorFinal);

		rubroEmpresaFuncionario.setDescripcion("Funcionario");
		rubroEmpresaFuncionario
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_FUNCIONARIO);
		rubroEmpresaFuncionario.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaFuncionario);

		rubroEmpresaMayoristaBaterias.setDescripcion("Mayorista Baterias");
		rubroEmpresaMayoristaBaterias
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_MAYORISTA_BATERIAS);
		rubroEmpresaMayoristaBaterias.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaMayoristaBaterias);

		rubroEmpresaMayoristaCubiertas.setDescripcion("Mayorista Cubiertas");
		rubroEmpresaMayoristaCubiertas
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_MAYORISTA_CUBIERTAS);
		rubroEmpresaMayoristaCubiertas.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaMayoristaCubiertas);

		rubroEmpresaMayoristaLubricantes
				.setDescripcion("Mayorista Lubricantes");
		rubroEmpresaMayoristaLubricantes
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_MAYORISTA_LUBRICANTES);
		rubroEmpresaMayoristaLubricantes.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaMayoristaLubricantes);

		rubroEmpresaMayoristaRepuestos.setDescripcion("Mayorista Repuestos");
		rubroEmpresaMayoristaRepuestos
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_MAYORISTA_REPUESTOS);
		rubroEmpresaMayoristaRepuestos.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaMayoristaRepuestos);

		rubroEmpresaDistribuidorBaterias
				.setDescripcion("Distribuidor Baterias");
		rubroEmpresaDistribuidorBaterias
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_DISTRIBUIDOR_BATERIAS);
		rubroEmpresaDistribuidorBaterias.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaDistribuidorBaterias);

		rubroEmpresaDistribuidorCubiertas
				.setDescripcion("Distribuidor Cubiertas");
		rubroEmpresaDistribuidorCubiertas
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_DISTRIBUIDOR_CUBIERTAS);
		rubroEmpresaDistribuidorCubiertas.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaDistribuidorCubiertas);

		rubroEmpresaDistribuidorLubricantes
				.setDescripcion("Distribuidor Lubricantes");
		rubroEmpresaDistribuidorLubricantes
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_DISTRIBUIDOR_LUBRICANTES);
		rubroEmpresaDistribuidorLubricantes.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaDistribuidorLubricantes);

		rubroEmpresaDistribuidorRepuestos
				.setDescripcion("Distribuidor Repuestos");
		rubroEmpresaDistribuidorRepuestos
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_DISTRIBUIDOR_REPUESTOS);
		rubroEmpresaDistribuidorRepuestos.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaDistribuidorRepuestos);

		rubroEmpresaCasaRepuestos.setDescripcion("Casa de Repuestos");
		rubroEmpresaCasaRepuestos
				.setSigla(Configuracion.SIGLA_RUBRO_EMPRESA_CASA_REPUESTOS);
		rubroEmpresaCasaRepuestos.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaCasaRepuestos);
		
		rubroEmpresaAseguradoras.setDescripcion("Aseguradoras");
		rubroEmpresaAseguradoras
				.setSigla(Configuracion.SIGLA_TIPO_RUBRO_EMP_ASEGURADORA);
		rubroEmpresaAseguradoras.setTipoTipo(tipoRubroEmpresa);
		grabarDB(rubroEmpresaAseguradoras);

		/************************** TIPOS DE REGIMEN TRIBUTARIO ************************/

		tipoRegimenTributario
				.setDescripcion(Configuracion.ID_TIPO_REGIMEN_TRIBUTARIO);
		grabarDB(tipoRegimenTributario);

		regimenTributario1.setDescripcion("EXENTA");
		regimenTributario1.setSigla(Configuracion.SIGLA_REGIMEN_TRIB_EXENTA);
		regimenTributario1.setTipoTipo(tipoRegimenTributario);
		grabarDB(regimenTributario1);

		regimenTributario2.setDescripcion("NO EXENTA");
		regimenTributario2.setSigla(Configuracion.SIGLA_REGIMEN_TRIB_NO_EXENTA);
		regimenTributario2.setTipoTipo(tipoRegimenTributario);
		grabarDB(regimenTributario2);

		/***************************** DATOS DE PROVEEDORES ****************************/

		tipoProveedores.setDescripcion(Configuracion.ID_TIPO_TIPO_PROVEEDOR);
		grabarDB(tipoProveedores);

		tipoProveedor1.setDescripcion("Local");
		tipoProveedor1.setSigla(Configuracion.SIGLA_PROVEEDOR_TIPO_LOCAL);
		tipoProveedor1.setTipoTipo(tipoProveedores);
		grabarDB(tipoProveedor1);

		tipoProveedor2.setDescripcion("Internacional");
		tipoProveedor2.setSigla(Configuracion.SIGLA_PROVEEDOR_TIPO_EXTERIOR);
		tipoProveedor2.setTipoTipo(tipoProveedores);
		grabarDB(tipoProveedor2);

		tipoProveedorEstados
				.setDescripcion(Configuracion.ID_TIPO_ESTADO_PROVEEDOR);
		grabarDB(tipoProveedorEstados);

		tipoProvEstado1.setDescripcion("Activo");
		tipoProvEstado1.setSigla(Configuracion.SIGLA_PROVEEDOR_ESTADO_ACTIVO);
		tipoProvEstado1.setTipoTipo(tipoProveedorEstados);
		grabarDB(tipoProvEstado1);

		tipoProvEstado2.setDescripcion("Inactivo");
		tipoProvEstado2.setSigla(Configuracion.SIGLA_PROVEEDOR_ESTADO_INACT);
		tipoProvEstado2.setTipoTipo(tipoProveedorEstados);
		grabarDB(tipoProvEstado2);

		/****************************** DATOS DE CLIENTES *****************************/

		tipoClientes.setDescripcion(Configuracion.ID_TIPO_CLIENTE);
		grabarDB(tipoClientes);

		tipoCliente1.setDescripcion("No Definido");
		tipoCliente1.setSigla(Configuracion.SIGLA_CLIENTE_TIPO_NO_DEFINIDO);
		tipoCliente1.setTipoTipo(tipoClientes);
		grabarDB(tipoCliente1);

		tipoCliente2.setDescripcion("Mayorista");
		tipoCliente2.setSigla(Configuracion.SIGLA_CLIENTE_TIPO_MAYORISTA);
		tipoCliente2.setTipoTipo(tipoClientes);
		grabarDB(tipoCliente2);

		tipoCliente3.setDescripcion("Minorista");
		tipoCliente3.setSigla(Configuracion.SIGLA_CLIENTE_TIPO_MINORISTA);
		tipoCliente3.setTipoTipo(tipoClientes);
		grabarDB(tipoCliente3);

		tipoCliente4.setDescripcion("Distribuidor");
		tipoCliente4.setSigla(Configuracion.SIGLA_CLIENTE_TIPO_DISTRIBUIDOR);
		tipoCliente4.setTipoTipo(tipoClientes);
		grabarDB(tipoCliente4);

		tipoCliente5.setDescripcion("Ocasional");
		tipoCliente5.setSigla(Configuracion.SIGLA_CLIENTE_TIPO_OCASIONAL);
		tipoCliente5.setTipoTipo(tipoClientes);
		grabarDB(tipoCliente5);

		/****************************** DATOS DE ARTICULOS ****************************/

		// Estados
		estadoArticulo.setDescripcion(Configuracion.ID_TIPO_ARTICULO_ESTADO);
		grabarDB(estadoArticulo);

		estadoArticulo1.setDescripcion("Activo");
		estadoArticulo1.setSigla(Configuracion.SIGLA_ARTICULO_ESTADO_ACTIVO);
		estadoArticulo1.setTipoTipo(estadoArticulo);
		grabarDB(estadoArticulo1);

		estadoArticulo2.setDescripcion("Inactivo");
		estadoArticulo2.setSigla(Configuracion.SIGLA_ARTICULO_ESTADO_INACTIVO);
		estadoArticulo2.setTipoTipo(estadoArticulo);
		grabarDB(estadoArticulo2);

		estadoArticulo3.setDescripcion("Temporal");
		estadoArticulo3.setSigla(Configuracion.SIGLA_ARTICULO_ESTADO_TEMPORAL);
		estadoArticulo3.setTipoTipo(estadoArticulo);
		grabarDB(estadoArticulo3);

		/*
		 * //Familias
		 * familiaArticulo.setDescripcion(Configuracion.ID_TIPO_ARTICULO_FAMILIA
		 * ); grabarDB(familiaArticulo);
		 * 
		 * familiaArticulo1.setDescripcion("Sin Familia");
		 * familiaArticulo1.setSigla
		 * (Configuracion.SIGLA_ARTICULO_FAMILIA_DEFAULT);
		 * familiaArticulo1.setTipoTipo(familiaArticulo);
		 * grabarDB(familiaArticulo1);
		 * 
		 * familiaArticulo2.setDescripcion("Filtros");
		 * familiaArticulo2.setSigla(
		 * Configuracion.SIGLA_ARTICULO_FAMILIA_FILTROS);
		 * familiaArticulo2.setTipoTipo(familiaArticulo);
		 * grabarDB(familiaArticulo2);
		 * 
		 * familiaArticulo3.setDescripcion("Lubricantes");
		 * familiaArticulo3.setSigla
		 * (Configuracion.SIGLA_ARTICULO_FAMILIA_LUBRICANTES);
		 * familiaArticulo3.setTipoTipo(familiaArticulo);
		 * grabarDB(familiaArticulo3);
		 * 
		 * familiaArticulo4.setDescripcion("Baterias");
		 * familiaArticulo4.setSigla
		 * (Configuracion.SIGLA_ARTICULO_FAMILIA_BATERIAS);
		 * familiaArticulo4.setTipoTipo(familiaArticulo);
		 * grabarDB(familiaArticulo4);
		 * 
		 * familiaArticulo5.setDescripcion("Cubiertas");
		 * familiaArticulo5.setSigla
		 * (Configuracion.SIGLA_ARTICULO_FAMILIA_CUBIERTAS);
		 * familiaArticulo5.setTipoTipo(familiaArticulo);
		 * grabarDB(familiaArticulo5);
		 * 
		 * familiaArticulo6.setDescripcion("Repuestos");
		 * familiaArticulo6.setSigla
		 * (Configuracion.SIGLA_ARTICULO_FAMILIA_REPUESTOS);
		 * familiaArticulo6.setTipoTipo(familiaArticulo);
		 * grabarDB(familiaArticulo6);
		 * 
		 * //Lineas
		 * lineaArticulo.setDescripcion(Configuracion.ID_TIPO_ARTICULO_LINEA);
		 * grabarDB(lineaArticulo);
		 * 
		 * 
		 * lineaArticulo1.setDescripcion("Sin Linea");
		 * lineaArticulo1.setSigla(Configuracion.SIGLA_ARTICULO_LINEA_DEFAULT);
		 * lineaArticulo1.setTipoTipo(lineaArticulo); grabarDB(lineaArticulo1);
		 * 
		 * lineaArticulo2.setDescripcion("Naviera");
		 * lineaArticulo2.setSigla(Configuracion.SIGLA_ARTICULO_LINEA_NAVIERA);
		 * lineaArticulo2.setTipoTipo(lineaArticulo); grabarDB(lineaArticulo2);
		 * 
		 * lineaArticulo3.setDescripcion("Agricola");
		 * lineaArticulo3.setSigla(Configuracion.SIGLA_ARTICULO_LINEA_AGRICOLA);
		 * lineaArticulo3.setTipoTipo(lineaArticulo); grabarDB(lineaArticulo3);
		 * 
		 * lineaArticulo4.setDescripcion("Pesada");
		 * lineaArticulo4.setSigla(Configuracion.SIGLA_ARTICULO_LINEA_PESADA);
		 * lineaArticulo4.setTipoTipo(lineaArticulo); grabarDB(lineaArticulo4);
		 * 
		 * lineaArticulo5.setDescripcion("Liviana");
		 * lineaArticulo5.setSigla(Configuracion.SIGLA_ARTICULO_LINEA_LIVIANA);
		 * lineaArticulo5.setTipoTipo(lineaArticulo); grabarDB(lineaArticulo5);
		 * 
		 * 
		 * //Marcas
		 * marcaArticulo.setDescripcion(Configuracion.ID_TIPO_ARTICULO_MARCA);
		 * grabarDB(marcaArticulo);
		 * 
		 * marcaArticulo1.setDescripcion(" Sin Marca");
		 * marcaArticulo1.setSigla(Configuracion.SIGLA_ARTICULO_MARCA_DEFAULT);
		 * marcaArticulo1.setTipoTipo(marcaArticulo); grabarDB(marcaArticulo1);
		 * 
		 * marcaArticulo2.setDescripcion("Kahveci");
		 * marcaArticulo2.setSigla(Configuracion.SIGLA_ARTICULO_MARCA_KHAV);
		 * marcaArticulo2.setTipoTipo(marcaArticulo); grabarDB(marcaArticulo2);
		 * 
		 * 
		 * marcaArticulo3.setDescripcion("Urba");
		 * marcaArticulo3.setSigla(Configuracion.SIGLA_ARTICULO_MARCA_URBA);
		 * marcaArticulo3.setTipoTipo(marcaArticulo); grabarDB(marcaArticulo3);
		 * 
		 * marcaArticulo4.setDescripcion("Brosol");
		 * marcaArticulo4.setSigla(Configuracion.SIGLA_ARTICULO_MARCA_BROSOL);
		 * marcaArticulo4.setTipoTipo(marcaArticulo); grabarDB(marcaArticulo4);
		 * 
		 * 
		 * 
		 * //Parte
		 * parteArticulo.setDescripcion(Configuracion.ID_TIPO_ARTICULO_PARTE);
		 * grabarDB(parteArticulo);
		 * 
		 * parteArticulo1.setDescripcion("Sin Parte");
		 * parteArticulo1.setSigla(Configuracion.SIGLA_ARTICULO_PARTE_DEFAULT);
		 * parteArticulo1.setTipoTipo(parteArticulo); grabarDB(parteArticulo1);
		 * 
		 * 
		 * parteArticulo2.setDescripcion("Caja");
		 * parteArticulo2.setSigla(Configuracion.SIGLA_ARTICULO_PARTE_CAJA);
		 * parteArticulo2.setTipoTipo(parteArticulo); grabarDB(parteArticulo2);
		 * 
		 * parteArticulo3.setDescripcion("Suspensión");
		 * parteArticulo3.setSigla(Configuracion
		 * .SIGLA_ARTICULO_PARTE_SUSPENSION);
		 * parteArticulo3.setTipoTipo(parteArticulo); grabarDB(parteArticulo3);
		 * 
		 * parteArticulo4.setDescripcion("Motor");
		 * parteArticulo4.setSigla(Configuracion.SIGLA_ARTICULO_PARTE_MOTOR);
		 * parteArticulo4.setTipoTipo(parteArticulo); grabarDB(parteArticulo4);
		 * 
		 * parteArticulo5.setDescripcion("Accesorio");
		 * parteArticulo5.setSigla(Configuracion
		 * .SIGLA_ARTICULO_PARTE_ACCESORIO);
		 * parteArticulo5.setTipoTipo(parteArticulo); grabarDB(parteArticulo5);
		 * 
		 * parteArticulo6.setDescripcion("frenos");
		 * parteArticulo6.setSigla(Configuracion.SIGLA_ARTICULO_PARTE_FRENOS);
		 * parteArticulo6.setTipoTipo(parteArticulo); grabarDB(parteArticulo6);
		 * 
		 */
		
		 //Unid_Med
		 unid_medArticulo.setDescripcion(Configuracion.ID_TIPO_ARTICULO_UNID_MED); 
		 grabarDB(unid_medArticulo);
		  
		 unid_medArticulo1.setDescripcion("UND.");
		 unid_medArticulo1.setSigla(Configuracion.SIGLA_ARTICULO_UNID_MED_UND);
		 unid_medArticulo1.setTipoTipo(unid_medArticulo);
		 grabarDB(unid_medArticulo1);
		 
		 unid_medArticulo2.setDescripcion("MTS.");
		 unid_medArticulo2.setSigla(Configuracion.SIGLA_ARTICULO_UNID_MED_MTS);
		 unid_medArticulo2.setTipoTipo(unid_medArticulo);
		 grabarDB(unid_medArticulo2);
		  
		 
		 unid_medArticulo3.setDescripcion("LTS.");
		 unid_medArticulo3.setSigla(Configuracion.SIGLA_ARTICULO_UNID_MED_LTS);
		 unid_medArticulo3.setTipoTipo(unid_medArticulo);
		 grabarDB(unid_medArticulo3);
		 
		/**************************** TIPOS DE IMPORTACION ****************************/

		tipoImportacion.setDescripcion(Configuracion.ID_TIPO_IMPORTACION);
		grabarDB(tipoImportacion);

		tipoImportacion1.setDescripcion("FOB");
		tipoImportacion1.setSigla(Configuracion.SIGLA_IMPORTACION_TIPO_1);
		tipoImportacion1.setTipoTipo(tipoImportacion);
		grabarDB(tipoImportacion1);

		tipoImportacion2.setDescripcion("CIF");
		tipoImportacion2.setSigla(Configuracion.SIGLA_IMPORTACION_TIPO_2);
		tipoImportacion2.setTipoTipo(tipoImportacion);
		grabarDB(tipoImportacion2);

		tipoImportacion3.setDescripcion("C&F");
		tipoImportacion3.setSigla(Configuracion.SIGLA_IMPORTACION_TIPO_3);
		tipoImportacion3.setTipoTipo(tipoImportacion);
		grabarDB(tipoImportacion3);

		/*************************** TIPOS DE COMPROBANTES ****************************/

		tipoComprobante.setDescripcion(Configuracion.ID_TIPO_COMPROBANTE);
		grabarDB(tipoComprobante);

		comprobanteLegal.setDescripcion("Comprobante Legal");
		comprobanteLegal.setSigla(Configuracion.SIGLA_TIPO_COMPROBANTE_LEGAL);
		comprobanteLegal.setTipoTipo(tipoComprobante);
		grabarDB(comprobanteLegal);

		comprobanteInterno.setDescripcion("Comprobante Interno");
		comprobanteInterno
				.setSigla(Configuracion.SIGLA_TIPO_COMPROBANTE_INTERNO);
		comprobanteInterno.setTipoTipo(tipoComprobante);
		grabarDB(comprobanteInterno);

		/******************* TIPOS DE OPERACIONES EN LOS MOVIMIENTOS ******************/

		tipoOperacion.setDescripcion(Configuracion.ID_TIPO_OPERACION);
		grabarDB(tipoOperacion);

		operacionCompra.setDescripcion("Compras");
		operacionCompra.setSigla(Configuracion.SIGLA_TIPO_OPERACION_COMPRA);
		operacionCompra.setTipoTipo(tipoOperacion);
		grabarDB(operacionCompra);

		operacionGasto.setDescripcion("Gastos");
		operacionGasto.setSigla(Configuracion.SIGLA_TIPO_OPERACION_GASTO);
		operacionGasto.setTipoTipo(tipoOperacion);
		grabarDB(operacionGasto);

		operacionPago.setDescripcion("Pagos");
		operacionPago.setSigla(Configuracion.SIGLA_TIPO_OPERACION_PAGO);
		operacionPago.setTipoTipo(tipoOperacion);
		grabarDB(operacionPago);

		operacionVenta.setDescripcion("Ventas");
		operacionVenta.setSigla(Configuracion.SIGLA_TIPO_OPERACION_VENTA);
		operacionVenta.setTipoTipo(tipoOperacion);
		grabarDB(operacionVenta);

		operacionCobro.setDescripcion("Cobros");
		operacionCobro.setSigla(Configuracion.SIGLA_TIPO_OPERACION_COBRO);
		operacionCobro.setTipoTipo(tipoOperacion);
		grabarDB(operacionCobro);

		operacionRemision.setDescripcion("Remisiones");
		operacionRemision.setSigla(Configuracion.SIGLA_TIPO_OPERACION_REMISION);
		operacionRemision.setTipoTipo(tipoOperacion);
		grabarDB(operacionRemision);

		operacionBancaria.setDescripcion("Operacion Bancaria");
		operacionBancaria.setSigla(Configuracion.SIGLA_TIPO_OPERACION_BANCARIA);
		operacionBancaria.setTipoTipo(tipoOperacion);
		grabarDB(operacionBancaria);
		
		operacionAjuste.setDescripcion("Operacion Ajuste Stock");
		operacionAjuste.setSigla(Configuracion.SIGLA_TIPO_OPERACION_AJUSTE);
		operacionAjuste.setTipoTipo(tipoOperacion);
		grabarDB(operacionAjuste);

		/****************************** TIPOS DE EMPRESA ******************************/

		tipoEmpresa.setDescripcion(Configuracion.ID_TIPO_EMPRESA);
		grabarDB(tipoEmpresa);

		cliente.setDescripcion("Cliente");
		cliente.setSigla(Configuracion.SIGLA_TIPO_EMPRESA_CLIENTE);
		cliente.setTipoTipo(tipoEmpresa);
		grabarDB(cliente);

		proveedor.setDescripcion("Proveedor");
		proveedor.setSigla(Configuracion.SIGLA_TIPO_EMPRESA_PROVEEDOR);
		proveedor.setTipoTipo(tipoEmpresa);
		grabarDB(proveedor);

		funcionario.setDescripcion("Funcionario");
		funcionario.setSigla(Configuracion.SIGLA_TIPO_EMPRESA_FUNCIONARIO);
		funcionario.setTipoTipo(tipoEmpresa);
		grabarDB(funcionario);

		yhaguy.setDescripcion("Yhaguy Repuestos S.A.");
		yhaguy.setSigla(Configuracion.SIGLA_TIPO_EMPRESA_YHAGUY);
		yhaguy.setTipoTipo(tipoEmpresa);
		grabarDB(yhaguy);

		banco.setDescripcion("Banco");
		banco.setSigla(Configuracion.SIGLA_TIPO_EMPRESA_BANCO);
		banco.setTipoTipo(tipoEmpresa);
		grabarDB(banco);

		tipoEmpresa.setDescripcion(Configuracion.ID_TIPO_EMPRESA);
		grabarDB(tipoEmpresa);

		/****************************** ESTADOS DE REGLAS DE PRECIO *******************/

		tipoEstadoRegla
				.setDescripcion(Configuracion.ID_TIPO_ESTADO_REGLA_PRECIO_VOLUMEN);
		grabarDB(tipoEstadoRegla);

		estadoMayor.setDescripcion("Mayor");
		estadoMayor.setSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_MAYOR);
		estadoMayor.setTipoTipo(tipoEstadoRegla);
		grabarDB(estadoMayor);

		estadoMenor.setDescripcion("Menor");
		estadoMenor.setSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_MENOR);
		estadoMenor.setTipoTipo(tipoEstadoRegla);
		grabarDB(estadoMenor);

		estadoIgual.setDescripcion("Igual");
		estadoIgual.setSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_IGUAL);
		estadoIgual.setTipoTipo(tipoEstadoRegla);
		grabarDB(estadoIgual);

		estadoDiferente.setDescripcion("Diferente");
		estadoDiferente
				.setSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_DIFERENTE);
		estadoDiferente.setTipoTipo(tipoEstadoRegla);
		grabarDB(estadoDiferente);

		estadoNinguno.setDescripcion("Ninguno");
		estadoNinguno.setSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_NINGUNO);
		estadoNinguno.setTipoTipo(tipoEstadoRegla);
		grabarDB(estadoNinguno);

		/* Tipo de tarjeta */
		tipoTarjetas.setDescripcion(Configuracion.ID_TIPO_TARJETA);
		grabarDB(tipoTarjetas);

		tipoTarjetas1.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas1.setDescripcion("Itau - Visa");
		tipoTarjetas1.setSigla(Configuracion.SIGLA_TIPO_TARJETA_VISA);
		grabarDB(tipoTarjetas1);

		tipoTarjetas2.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas2.setDescripcion("Itau - Mastercard");
		tipoTarjetas2.setSigla(Configuracion.SIGLA_TIPO_TARJETA_MC);
		grabarDB(tipoTarjetas2);

		tipoTarjetas3.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas3.setDescripcion("Itau - American Express");
		tipoTarjetas3.setSigla(Configuracion.SIGLA_TIPO_TARJETA_AX);
		grabarDB(tipoTarjetas3);

		tipoTarjetas4.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas4.setDescripcion("Continental - Visa");
		tipoTarjetas4.setSigla(Configuracion.SIGLA_TIPO_TARJETA_VISA);
		grabarDB(tipoTarjetas4);

		tipoTarjetas5.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas5.setDescripcion("Continental - Mastercard");
		tipoTarjetas5.setSigla(Configuracion.SIGLA_TIPO_TARJETA_MC);
		grabarDB(tipoTarjetas5);

		tipoTarjetas6.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas6.setDescripcion("Continental - American Express");
		tipoTarjetas6.setSigla(Configuracion.SIGLA_TIPO_TARJETA_AX);
		grabarDB(tipoTarjetas6);

		tipoTarjetas7.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas7.setDescripcion("Otros - Visa");
		tipoTarjetas7.setSigla(Configuracion.SIGLA_TIPO_TARJETA_VISA);
		grabarDB(tipoTarjetas7);

		tipoTarjetas8.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas8.setDescripcion("Otros - Mastercard");
		tipoTarjetas8.setSigla(Configuracion.SIGLA_TIPO_TARJETA_MC);
		grabarDB(tipoTarjetas8);

		tipoTarjetas9.setTipoTipo(this.tipoTarjetas);
		tipoTarjetas9.setDescripcion("Otros - American Express");
		tipoTarjetas9.setSigla(Configuracion.SIGLA_TIPO_TARJETA_AX);
		grabarDB(tipoTarjetas9);

		/**************************** PROCESADORAS DE TARJETAS **************************/

		procesadoraTarjeta.setDescripcion(Configuracion.ID_TIPO_PROCESADORA_TC);
		grabarDB(procesadoraTarjeta);

		procesadoraProcard.setDescripcion("Procard");
		procesadoraProcard.setSigla(Configuracion.SIGLA_PROCESADORA_TC_PROCARD);
		procesadoraProcard.setTipoTipo(procesadoraTarjeta);
		grabarDB(procesadoraProcard);

		procesadoraPanal.setDescripcion("Panal");
		procesadoraPanal.setSigla(Configuracion.SIGLA_PROCESADORA_TC_PANAL);
		procesadoraPanal.setTipoTipo(procesadoraTarjeta);
		grabarDB(procesadoraPanal);

		procesadoraBancard.setDescripcion("Bancard");
		procesadoraBancard.setSigla(Configuracion.SIGLA_PROCESADORA_TC_BANCARD);
		procesadoraBancard.setTipoTipo(procesadoraTarjeta);
		grabarDB(procesadoraBancard);

		procesadoraCabal.setDescripcion("Cabal");
		procesadoraCabal.setSigla(Configuracion.SIGLA_PROCESADORA_TC_CABAL);
		procesadoraCabal.setTipoTipo(procesadoraTarjeta);
		grabarDB(procesadoraCabal);

		/****************************** ESTADOS DE CONCILIACION *************************/

		estadoConciliacion
				.setDescripcion(Configuracion.ID_TIPO_ESTADO_CONCILIACION);
		grabarDB(estadoConciliacion);

		estadoConciliacion1.setDescripcion("Conciliado");
		estadoConciliacion1
				.setSigla(Configuracion.SIGLA_ESTADO_CONCILIACION_CONCILIADO);
		estadoConciliacion1.setTipoTipo(estadoConciliacion);
		grabarDB(estadoConciliacion1);

		estadoConciliacion2.setDescripcion("Pendiente");
		estadoConciliacion2
				.setSigla(Configuracion.SIGLA_ESTADO_CONCILIACION_PENDIENTE);
		estadoConciliacion2.setTipoTipo(estadoConciliacion);
		grabarDB(estadoConciliacion2);

		estadoConciliacion3.setDescripcion("Diferencia");
		estadoConciliacion3
				.setSigla(Configuracion.SIGLA_ESTADO_CONCILIACION_DIFERENCIA);
		estadoConciliacion3.setTipoTipo(estadoConciliacion);
		grabarDB(estadoConciliacion3);

		/*************************** CARGOS DE FUNCIONARIOS ***************************/

		funcionarioCargos
				.setDescripcion(Configuracion.ID_TIPO_CARGO_FUNCIONARIO);
		grabarDB(funcionarioCargos);

		cargoAuxiliarAdministrativo.setDescripcion("Auxiliar Administrativo");
		cargoAuxiliarAdministrativo
				.setSigla(Configuracion.SIGLA_TIPO_CARGO_AUXILIAR_ADMINISTRATIVO);
		cargoAuxiliarAdministrativo.setTipoTipo(funcionarioCargos);
		grabarDB(cargoAuxiliarAdministrativo);

		cargoGerenteRRHH.setDescripcion("Gerente RR. HH.");
		cargoGerenteRRHH.setSigla(Configuracion.SIGLA_TIPO_CARGO_GERENTE_RRHH);
		cargoGerenteRRHH.setTipoTipo(funcionarioCargos);
		grabarDB(cargoGerenteRRHH);

		cargoJefeVentas.setDescripcion("Jefe Ventas Mostrador");
		cargoJefeVentas.setSigla(Configuracion.SIGLA_TIPO_CARGO_JEFE_MOSTRADOR);
		cargoJefeVentas.setTipoTipo(funcionarioCargos);
		grabarDB(cargoJefeVentas);

		cargoSoporteTecnico.setDescripcion("Soporte Técnico");
		cargoSoporteTecnico
				.setSigla(Configuracion.SIGLA_TIPO_CARGO_SOPORTE_TECNICO);
		cargoSoporteTecnico.setTipoTipo(funcionarioCargos);
		grabarDB(cargoSoporteTecnico);

		cargoVentasMostrador.setDescripcion("Vendedor Mostrador");
		cargoVentasMostrador
				.setSigla(Configuracion.SIGLA_TIPO_CARGO_VENTAS_MOSTRADOR);
		cargoVentasMostrador.setTipoTipo(funcionarioCargos);
		grabarDB(cargoVentasMostrador);

		cargoGerenteAdministrativo.setDescripcion("Gerente Administrativo");
		cargoGerenteAdministrativo
				.setSigla(Configuracion.SIGLA_TIPO_CARGO_GERENTE_ADMINISTRATIVO);
		cargoGerenteAdministrativo.setTipoTipo(funcionarioCargos);
		grabarDB(cargoGerenteAdministrativo);

		cargoEncargadoCompras.setDescripcion("Encargado/a Compras");
		cargoEncargadoCompras
				.setSigla(Configuracion.SIGLA_TIPO_CARGO_ENCARGADO_COMPRAS);
		cargoEncargadoCompras.setTipoTipo(funcionarioCargos);
		grabarDB(cargoEncargadoCompras);

		cargoEncargadoDeposito.setDescripcion("Encargado/a Depósito");
		cargoEncargadoDeposito
				.setSigla(Configuracion.SIGLA_TIPO_CARGO_ENCARGADO_DEPOSITO);
		cargoEncargadoDeposito.setTipoTipo(funcionarioCargos);
		grabarDB(cargoEncargadoDeposito);

		cargoVentaExterna.setDescripcion("Vendedor/a Externo");
		cargoVentaExterna
				.setSigla(Configuracion.SIGLA_TIPO_CARGO_VENTAS_EXTERNAS);
		cargoVentaExterna.setTipoTipo(funcionarioCargos);
		grabarDB(cargoVentaExterna);

		/*************************** ESTADOS DE FUNCIONARIOS ***************************/

		funcionarioEstados
				.setDescripcion(Configuracion.ID_TIPO_ESTADO_FUNCIONARIO);
		grabarDB(funcionarioEstados);

		funcionarioEstadoActivo.setDescripcion("Activo");
		funcionarioEstadoActivo
				.setSigla(Configuracion.SIGLA_TIPO_FUNCIONARIO_ESTADO_ACTIVO);
		funcionarioEstadoActivo.setTipoTipo(funcionarioEstados);
		grabarDB(funcionarioEstadoActivo);

		funcionarioEstadoInactivo.setDescripcion("Inactivo");
		funcionarioEstadoInactivo
				.setSigla(Configuracion.SIGLA_TIPO_FUNCIONARIO_ESTADO_INACTIVO);
		funcionarioEstadoInactivo.setTipoTipo(funcionarioEstados);
		grabarDB(funcionarioEstadoInactivo);

		/************************* MOTIVOS DE NOTA DE CREDITO **************************/

		notaCreditoMotivos
				.setDescripcion(Configuracion.ID_TIPO_NOTA_CREDITO_MOTIVOS);
		grabarDB(notaCreditoMotivos);

		nc_motivo_descuento.setDescripcion("Descuento");
		nc_motivo_descuento
				.setSigla(Configuracion.SIGLA_TIPO_NC_MOTIVO_DESCUENTO);
		nc_motivo_descuento.setTipoTipo(notaCreditoMotivos);
		grabarDB(nc_motivo_descuento);

		nc_motivo_devolucion.setDescripcion("Devolución");
		nc_motivo_devolucion
				.setSigla(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION);
		nc_motivo_devolucion.setTipoTipo(notaCreditoMotivos);
		grabarDB(nc_motivo_devolucion);
		
		nc_motivo_dif_precio.setDescripcion("Diferencia de Precio");
		nc_motivo_dif_precio.setSigla(Configuracion.SIGLA_TIPO_NC_MOTIVO_DIF_PRECIO);
		nc_motivo_dif_precio.setTipoTipo(notaCreditoMotivos);
		grabarDB(nc_motivo_dif_precio);
		
		nc_motivo_reclamo.setDescripcion("Reclamo");
		nc_motivo_reclamo.setSigla(Configuracion.SIGLA_TIPO_NC_MOTIVO_RECLAMO);
		nc_motivo_reclamo.setTipoTipo(notaCreditoMotivos);
		grabarDB(nc_motivo_reclamo);

		/********************* TIPOS DE DETALLE EN NOTAS DE CREDITO ********************/

		notaCreditoDetalleTipos
				.setDescripcion(Configuracion.ID_TIPO_NOTA_CREDITO_DETALLE);
		grabarDB(notaCreditoDetalleTipos);

		ncDet_factura.setDescripcion("Item Factura");
		ncDet_factura.setSigla(Configuracion.SIGLA_TIPO_NC_DETALLE_FACTURA);
		ncDet_factura.setTipoTipo(notaCreditoDetalleTipos);
		grabarDB(ncDet_factura);

		ncDet_articulo.setDescripcion("Item Articulo");
		ncDet_articulo.setSigla(Configuracion.SIGLA_TIPO_NC_DETALLE_ARTICULO);
		ncDet_articulo.setTipoTipo(notaCreditoDetalleTipos);
		grabarDB(ncDet_articulo);

		/*************************** ESTADOS DE COMPROBANTES ***************************/

		estadosComprobantes
				.setDescripcion(Configuracion.ID_TIPO_ESTADO_COMPROBANTE);
		grabarDB(estadosComprobantes);

		estadoComprobante_pendiente.setDescripcion("Pendiente");
		estadoComprobante_pendiente
				.setSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_PENDIENTE);
		estadoComprobante_pendiente.setTipoTipo(estadosComprobantes);
		grabarDB(estadoComprobante_pendiente);

		estadoComprobante_aprobado.setDescripcion("Aprobado");
		estadoComprobante_aprobado
				.setSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_APROBADO);
		estadoComprobante_aprobado.setTipoTipo(estadosComprobantes);
		grabarDB(estadoComprobante_aprobado);

		estadoComprobante_cerrado.setDescripcion("Cerrado");
		estadoComprobante_cerrado
				.setSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CERRADO);
		estadoComprobante_cerrado.setTipoTipo(estadosComprobantes);
		grabarDB(estadoComprobante_cerrado);

		estadoComprobante_anulado.setDescripcion("Anulado");
		estadoComprobante_anulado
				.setSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO);
		estadoComprobante_anulado.setTipoTipo(estadosComprobantes);
		grabarDB(estadoComprobante_anulado);
		
		estadoComprobante_confeccionado.setDescripcion("Confeccionado");
		estadoComprobante_confeccionado
				.setSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CONFECCIONADO);
		estadoComprobante_confeccionado.setTipoTipo(estadosComprobantes);
		grabarDB(estadoComprobante_confeccionado);

		/************************** TIPOS DE CUENTAS CONTABLES **************************/

		tipoCtaContable.setDescripcion(Configuracion.ID_TIPO_CUENTAS_CONTABLES);
		grabarDB(tipoCtaContable);

		tipoCtaContableActivo.setDescripcion("Activo");
		tipoCtaContableActivo
				.setSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_ACTIVO);
		tipoCtaContableActivo.setTipoTipo(tipoCtaContable);
		grabarDB(tipoCtaContableActivo);

		tipoCtaContablePasivo.setDescripcion("Pasivo");
		tipoCtaContablePasivo
				.setSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_PASIVO);
		tipoCtaContablePasivo.setTipoTipo(tipoCtaContable);
		grabarDB(tipoCtaContablePasivo);

		tipoCtaContablePatrimonioNeto.setDescripcion("Patrimonio Neto");
		tipoCtaContablePatrimonioNeto
				.setSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_PATRIMONIO);
		tipoCtaContablePatrimonioNeto.setTipoTipo(tipoCtaContable);
		grabarDB(tipoCtaContablePatrimonioNeto);

		tipoCtaContableIngreso.setDescripcion("Ingreso");
		tipoCtaContableIngreso
				.setSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_INGRESO);
		tipoCtaContableIngreso.setTipoTipo(tipoCtaContable);
		grabarDB(tipoCtaContableIngreso);

		tipoCtaContableEgreso.setDescripcion("Egreso");
		tipoCtaContableEgreso
				.setSigla(Configuracion.SIGLA_TIPO_CTA_CONTABLE_EGRESO);
		tipoCtaContableEgreso.setTipoTipo(tipoCtaContable);
		grabarDB(tipoCtaContableEgreso);

		/*************************** Tipo de Documentos ***************************/

		tipoDocumento.setDescripcion(Configuracion.ID_TIPO_DOCUMENTO);
		grabarDB(tipoDocumento);

		tipoFacturaContado.setDescripcion("Factura Contado");
		tipoFacturaContado.setSigla(Configuracion.SIGLA_DOC_FAC_CONTADO);
		tipoFacturaContado.setTipoTipo(tipoDocumento);
		grabarDB(tipoFacturaContado);

		tipoFacturaCredito.setDescripcion("Factura Credito");
		tipoFacturaCredito.setSigla(Configuracion.SIGLA_DOC_FAC_CREDITO);
		tipoFacturaCredito.setTipoTipo(tipoDocumento);
		grabarDB(tipoFacturaCredito);

		tipoNotaCredito.setDescripcion("Nota de Credito");
		tipoNotaCredito.setSigla(Configuracion.SIGLA_DOC_NOTA_CREDITO);
		tipoNotaCredito.setTipoTipo(tipoDocumento);
		grabarDB(tipoNotaCredito);

		tipoNotaDebito.setDescripcion("Nota de Debito");
		tipoNotaDebito.setSigla(Configuracion.SIGLA_DOC_NOTA_DEBITO);
		tipoNotaDebito.setTipoTipo(tipoDocumento);
		grabarDB(tipoNotaDebito);

		tipoReciboDinero.setDescripcion("Recibo de Dinero");
		tipoReciboDinero.setSigla(Configuracion.SIGLA_DOC_REC_DINERO);
		tipoReciboDinero.setTipoTipo(tipoDocumento);
		grabarDB(tipoReciboDinero);

		tipoAutoFactura.setDescripcion("Autofactura");
		tipoAutoFactura.setSigla(Configuracion.SIGLA_DOC_AUTOFAC);
		tipoAutoFactura.setTipoTipo(tipoDocumento);
		grabarDB(tipoAutoFactura);

		tipoPagare.setDescripcion("Pagare");
		tipoPagare.setSigla(Configuracion.SIGLA_DOC_PAGARE);
		tipoPagare.setTipoTipo(tipoDocumento);
		grabarDB(tipoPagare);

		tipoNotaRemision.setDescripcion("Nota de Remision");
		tipoNotaRemision.setSigla(Configuracion.SIGLA_DOC_NOT_REMISION);
		tipoNotaRemision.setTipoTipo(tipoDocumento);
		grabarDB(tipoNotaRemision);

		tipoOtrosDocumentos.setDescripcion("Otros");
		tipoOtrosDocumentos.setSigla(Configuracion.SIGLA_DOC_OTROS);
		tipoOtrosDocumentos.setTipoTipo(tipoDocumento);
		grabarDB(tipoOtrosDocumentos);

		/************************* TIPOS DE RESERVA ****************************/

		tipoReserva.setDescripcion(Configuracion.ID_TIPO_RESERVA);
		grabarDB(tipoReserva);

		reservaInterna.setDescripcion("Reserva Interna");
		reservaInterna.setSigla(Configuracion.SIGLA_RESERVA_INTERNA);
		reservaInterna.setTipoTipo(tipoReserva);
		grabarDB(reservaInterna);

		reservaReparto.setDescripcion("Reserva por Reparto");
		reservaReparto.setSigla(Configuracion.SIGLA_RESERVA_REPARTO);
		reservaReparto.setTipoTipo(tipoReserva);
		grabarDB(reservaReparto);

		reservaVenta.setDescripcion("Reserva por Venta");
		reservaVenta.setSigla(Configuracion.SIGLA_RESERVA_VENTA);
		reservaVenta.setTipoTipo(tipoReserva);
		grabarDB(reservaVenta);

		reservaDevolucion.setDescripcion("Reserva por boleta devolucion");
		reservaDevolucion.setSigla(Configuracion.SIGLA_RESERVA_DEVOLUCION);
		reservaDevolucion.setTipoTipo(tipoReserva);
		grabarDB(reservaDevolucion);

		/************************ ESTADOS DE RESERVA ***************************/

		estadoReserva.setDescripcion(Configuracion.ID_TIPO_ESTADO_RESERVA);
		grabarDB(estadoReserva);

		estadoReservaActiva.setDescripcion("Reserva Activa");
		estadoReservaActiva.setSigla(Configuracion.SIGLA_ESTADO_RESERVA_ACTIVA);
		estadoReservaActiva.setTipoTipo(estadoReserva);
		grabarDB(estadoReservaActiva);

		estadoReservaCancelada.setDescripcion("Reserva Cancelada");
		estadoReservaCancelada
				.setSigla(Configuracion.SIGLA_ESTADO_RESERVA_CANCELADA);
		estadoReservaCancelada.setTipoTipo(estadoReserva);
		grabarDB(estadoReservaCancelada);

		estadoReservaFinalizada.setDescripcion("Reserva Finalizada");
		estadoReservaFinalizada
				.setSigla(Configuracion.SIGLA_ESTADO_RESERVA_FINALIZADA);
		estadoReservaFinalizada.setTipoTipo(estadoReserva);
		grabarDB(estadoReservaFinalizada);

		/************************ MOTIVOS DEVOLUCION ***************************/
		motivoDevolucion
				.setDescripcion(Configuracion.ID_TIPO_MOTIVO_DEVOLUCION);
		grabarDB(motivoDevolucion);

		motivoDevolucionDiaSguiente
				.setDescripcion("Devolucion al dia siguiente");
		motivoDevolucionDiaSguiente
				.setSigla(Configuracion.SIGLA_MOTIVO_DEVOLUCION_DIA_SIGUIENTE);
		motivoDevolucionDiaSguiente.setTipoTipo(motivoDevolucion);
		grabarDB(motivoDevolucionDiaSguiente);

		motivoDevolucionEntregaParcial
				.setDescripcion("Devolucion entrega parcial");
		motivoDevolucionEntregaParcial
				.setSigla(Configuracion.SIGLA_MOTIVO_DEVOLUCION_ENTREGA_PARCIAL);
		motivoDevolucionEntregaParcial.setTipoTipo(motivoDevolucion);
		grabarDB(motivoDevolucionEntregaParcial);

		motivoDevolucionEntregaParcialDefectuoso
				.setDescripcion("Devolucion estado defectuoso");
		motivoDevolucionEntregaParcialDefectuoso
				.setSigla(Configuracion.SIGLA_MOTIVO_DEVOLUCION_DEFECTUOSO);
		motivoDevolucionEntregaParcialDefectuoso.setTipoTipo(motivoDevolucion);
		grabarDB(motivoDevolucionEntregaParcialDefectuoso);

		/******* SI NO **************/
		siNo.setDescripcion(Configuracion.ID_TIPO_TIPO_SINO);
		grabarDB(siNo);

		siNo_SI.setDescripcion("SI");
		siNo_SI.setSigla(Configuracion.ID_TIPO_TIPO_SINO);
		siNo_SI.setTipoTipo(siNo);
		grabarDB(siNo_SI);

		siNo_NO.setDescripcion("NO");
		siNo_NO.setSigla(Configuracion.ID_TIPO_TIPO_SINO);
		siNo_NO.setTipoTipo(siNo);
		grabarDB(siNo_NO);

		/*************************** Banco Deposito **********************************/

		tipoBancoDeposito.setDescripcion(Configuracion.ID_TIPO_BOLETA_DEPOSITO);
		grabarDB(tipoBancoDeposito);

		bancoDepEfectivo
				.setDescripcion(Configuracion.TIPO_BANCO_DEPOSITO_EFECTIVO);
		bancoDepEfectivo.setSigla(Configuracion.SIGLA_BANCO_DEPOSITO_EFECTIVO);
		bancoDepEfectivo.setTipoTipo(tipoBancoDeposito);
		grabarDB(bancoDepEfectivo);

		bancoDepChequesBanco
				.setDescripcion(Configuracion.TIPO_BANCO_DEPOSITO_CHEQUES_BANCO);
		bancoDepChequesBanco
				.setSigla(Configuracion.SIGLA_BANCO_DEPOSITO_CHEQUES_BANCO);
		bancoDepChequesBanco.setTipoTipo(tipoBancoDeposito);
		grabarDB(bancoDepChequesBanco);

		bancoDepChequesOtrosBancos
				.setDescripcion(Configuracion.TIPO_BANCO_DEPOSITO_CHEQUES_OTRO_BANCO);
		bancoDepChequesOtrosBancos
				.setSigla(Configuracion.SIGLA_BANCO_DEPOSITO_CHEQUES_OTRO_BANCO);
		bancoDepChequesOtrosBancos.setTipoTipo(tipoBancoDeposito);
		grabarDB(bancoDepChequesOtrosBancos);

		bancoDepTodos.setDescripcion(Configuracion.TIPO_BANCO_DEPOSITO_TODOS);
		bancoDepTodos.setSigla(Configuracion.SIGLA_BANCO_DEPOSITO_TODOS);
		bancoDepTodos.setTipoTipo(tipoBancoDeposito);
		grabarDB(bancoDepTodos);
		
		// modo de crear cheque
		tipoModoDeCreacionCheque.setDescripcion(Configuracion.ID_TIPO_MODO_DE_CREACION_CHEQUE);
		grabarDB(tipoModoDeCreacionCheque);
				
		chequeAutomatico.setDescripcion("Automatico");
		chequeAutomatico.setSigla(Configuracion.SIGLA_TIPO_CHEQUE_AUTOMATICO);
		chequeAutomatico.setTipoTipo(tipoModoDeCreacionCheque);
		grabarDB(chequeAutomatico);
				
		chequeManual.setDescripcion("Manual");
		chequeManual.setSigla(Configuracion.SIGLA_TIPO_CHEQUE_MANUAL);
		chequeManual.setTipoTipo(tipoModoDeCreacionCheque);
		grabarDB(chequeManual);
		
		
		/******************** Bancos para Cheques de Tercero ********************/
		
		bancosTerceros.setDescripcion(Configuracion.ID_TIPO_BANCOS_TERCEROS);
		grabarDB(bancosTerceros);
		
		bancoTerceroLloyds.setDescripcion("Lloyds TSB");
		bancoTerceroLloyds.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_TSB);
		bancoTerceroLloyds.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroLloyds);
		
		bancoTerceroIntegracion.setDescripcion("Integración");
		bancoTerceroIntegracion.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_INTEGRACION);
		bancoTerceroIntegracion.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroIntegracion);
		
		bancoTerceroContinental.setDescripcion("Continental");
		bancoTerceroContinental.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_CONTI);
		bancoTerceroContinental.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroContinental);
		
		bancoTerceroAtlas.setDescripcion("Atlas");
		bancoTerceroAtlas.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_ATLAS);
		bancoTerceroAtlas.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroAtlas);
		
		bancoTerceroRegional.setDescripcion("Regional");
		bancoTerceroRegional.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_REGIONAL);
		bancoTerceroRegional.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroRegional);
		
		bancoTerceroSudameris.setDescripcion("Sudameris");
		bancoTerceroSudameris.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_SUDAMERIS);
		bancoTerceroSudameris.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroSudameris);
		
		bancoTerceroABN.setDescripcion("ABN-AMRO");
		bancoTerceroABN.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_ABN);
		bancoTerceroABN.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroABN);
		
		bancoTerceroHSBC.setDescripcion("HSBC");
		bancoTerceroHSBC.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_HSBC);
		bancoTerceroHSBC.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroHSBC);
		
		bancoTerceroBBVA.setDescripcion("BBVA");
		bancoTerceroBBVA.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_BBVA);
		bancoTerceroBBVA.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroBBVA);
		
		bancoTerceroItau.setDescripcion("Itau");
		bancoTerceroItau.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_ITAU);
		bancoTerceroItau.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroItau);
		
		bancoTerceroAmambay.setDescripcion("Amambay S.A.");
		bancoTerceroAmambay.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_AMAMBAY);
		bancoTerceroAmambay.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroAmambay);
		
		bancoTerceroCitibank.setDescripcion("Citibank");
		bancoTerceroCitibank.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_CITI);
		bancoTerceroCitibank.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroCitibank);
		
		bancoTerceroBancop.setDescripcion("Bancop");
		bancoTerceroBancop.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_BANCOP);
		bancoTerceroBancop.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroBancop);
		
		bancoTerceroGNB.setDescripcion("GNB");
		bancoTerceroGNB.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_GNB);
		bancoTerceroGNB.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroGNB);
		
		bancoTerceroBrasil.setDescripcion("Do Brasil");
		bancoTerceroBrasil.setSigla(Configuracion.SIGLA_TIPO_BANCOS_TERCEROS_DOBRASIL);
		bancoTerceroBrasil.setTipoTipo(bancosTerceros);
		grabarDB(bancoTerceroBrasil);
		
		tipoTarjetaExtractoDetalle.setDescripcion(Configuracion.ID_TIPO_BANCO_TARJETA_EXTRACTO_DETALLE);
		grabarDB(tipoTarjetaExtractoDetalle);
		
		tipoTarjetaExtractoDetalleTE.setSigla(Configuracion.SIGLA_TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_DET_TARJ);
		tipoTarjetaExtractoDetalleTE.setDescripcion(Configuracion.TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_DET_TARJ);
		tipoTarjetaExtractoDetalleTE.setTipoTipo(tipoTarjetaExtractoDetalle);
		grabarDB(tipoTarjetaExtractoDetalleTE);
		
		tipoTarjetaExtractoDetalleBM.setSigla(Configuracion.SIGLA_TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_BANCO_MOVIMIENTO);
		tipoTarjetaExtractoDetalleBM.setDescripcion(Configuracion.TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_BANCO_MOVIMIENTO);
		tipoTarjetaExtractoDetalleBM.setTipoTipo(tipoTarjetaExtractoDetalle);
		grabarDB(tipoTarjetaExtractoDetalleBM);
		

		// esto siembre va al final, porque usa los tipos definidos arriba
		this.cargaTipoMovimientos();
		this.condicionPagoProveedor();
		this.condicionPagoProveedor();
		this.cargaEmpresaGrupoSociedad();
		this.cargaVarias();
		this.cargaReglaPrecio();
	} 

	// =======================================================================
	// ====================== Tipos de Movimientos ===========================
	// =======================================================================

	TipoMovimiento tipoMov1 = new TipoMovimiento();
	TipoMovimiento tipoMov2 = new TipoMovimiento();
	TipoMovimiento tipoMov3 = new TipoMovimiento();
	TipoMovimiento tipoMov4 = new TipoMovimiento();
	TipoMovimiento tipoMov5 = new TipoMovimiento();
	TipoMovimiento tipoMov6 = new TipoMovimiento();
	TipoMovimiento tipoMov7 = new TipoMovimiento();
	TipoMovimiento tipoMov8 = new TipoMovimiento();
	TipoMovimiento tipoMov9 = new TipoMovimiento();
	TipoMovimiento tipoMov10 = new TipoMovimiento();
	TipoMovimiento tipoMov11 = new TipoMovimiento();
	TipoMovimiento tip_Mov11 = new TipoMovimiento();
	TipoMovimiento tipoMov12 = new TipoMovimiento();
	TipoMovimiento tipoMov13 = new TipoMovimiento();
	TipoMovimiento tipoMov14 = new TipoMovimiento();
	TipoMovimiento tipoMov15 = new TipoMovimiento();
	TipoMovimiento tipoMov16 = new TipoMovimiento();
	TipoMovimiento tipoMov17 = new TipoMovimiento();
	TipoMovimiento tipoMov18 = new TipoMovimiento();
	TipoMovimiento tipoMov19 = new TipoMovimiento();
	TipoMovimiento tipoMov20 = new TipoMovimiento();
	TipoMovimiento tipoMov21 = new TipoMovimiento();
	TipoMovimiento tipoMov22 = new TipoMovimiento();
	TipoMovimiento tipoMov23 = new TipoMovimiento();
	TipoMovimiento tipoMov24 = new TipoMovimiento();
	TipoMovimiento tipoMov25 = new TipoMovimiento();
	TipoMovimiento tipoMov26 = new TipoMovimiento();
	TipoMovimiento tipoMov27 = new TipoMovimiento();
	TipoMovimiento tipoMov28 = new TipoMovimiento();
	TipoMovimiento tipoMov29 = new TipoMovimiento();
	TipoMovimiento tipoMov30 = new TipoMovimiento();
	TipoMovimiento tipoMov31 = new TipoMovimiento();
	TipoMovimiento tipoMov32 = new TipoMovimiento();
	TipoMovimiento tipoMov33 = new TipoMovimiento();
	TipoMovimiento tipoMov34 = new TipoMovimiento();
	TipoMovimiento tipoMov35 = new TipoMovimiento();
	TipoMovimiento tipoMov36 = new TipoMovimiento();
	TipoMovimiento tipoMov37 = new TipoMovimiento();

	private void cargaTipoMovimientos() throws Exception {

		// Factura Compra Contado
		tipoMov1.setDescripcion("Fac.Compra Contado");
		tipoMov1.setClase("CompraLocalFactura");
		tipoMov1.setSigla(Configuracion.SIGLA_TM_FAC_COMPRA_CONTADO);
		tipoMov1.setTipoIva(this.tipoIva1);
		tipoMov1.setTipoEmpresa(proveedor);
		tipoMov1.setTipoComprobante(comprobanteLegal);
		tipoMov1.setTipoOperacion(operacionCompra);
		tipoMov1.setTipoDocumento(tipoFacturaContado);
		grabarDB(tipoMov1);

		// Factura Compra Credito
		tipoMov2.setDescripcion("Fac.Compra Credito");
		tipoMov2.setClase("CompraLocalFactura");
		tipoMov2.setSigla(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO);
		tipoMov2.setTipoIva(this.tipoIva1);
		tipoMov2.setTipoEmpresa(proveedor);
		tipoMov2.setTipoComprobante(comprobanteLegal);
		tipoMov2.setTipoOperacion(operacionCompra);
		tipoMov2.setTipoDocumento(tipoFacturaCredito);
		grabarDB(tipoMov2);

		// Factura Importacion Contado
		tipoMov3.setDescripcion("Fac. Importación Contado");
		tipoMov3.setClase("ImportacionFactura");
		tipoMov3.setSigla(Configuracion.SIGLA_TM_FAC_IMPORT_CONTADO);
		tipoMov3.setTipoIva(this.tipoIva3);
		tipoMov3.setTipoEmpresa(proveedor);
		tipoMov3.setTipoComprobante(comprobanteLegal);
		tipoMov3.setTipoOperacion(operacionCompra);
		tipoMov3.setTipoDocumento(tipoFacturaContado);
		grabarDB(tipoMov3);

		// Factura Importacion Credito
		tipoMov4.setDescripcion("Fac. Importación Crédito");
		tipoMov4.setClase("ImportacionFactura");
		tipoMov4.setSigla(Configuracion.SIGLA_TM_FAC_IMPORT_CREDITO);
		tipoMov4.setTipoIva(this.tipoIva3);
		tipoMov4.setTipoEmpresa(proveedor);
		tipoMov4.setTipoComprobante(comprobanteLegal);
		tipoMov4.setTipoOperacion(operacionCompra);
		tipoMov4.setTipoDocumento(tipoFacturaCredito);
		grabarDB(tipoMov4);

		// Nota de Credito Compra
		tipoMov5.setDescripcion("Nota de Crédito-Compra");
		tipoMov5.setClase("CompraLocalFactura");
		tipoMov5.setSigla(Configuracion.SIGLA_TM_NOTA_CREDITO_COMPRA);
		tipoMov5.setTipoIva(this.tipoIva1);
		tipoMov5.setTipoEmpresa(proveedor);
		tipoMov5.setTipoComprobante(comprobanteLegal);
		tipoMov5.setTipoOperacion(operacionCompra);
		tipoMov5.setTipoDocumento(tipoNotaCredito);
		grabarDB(tipoMov5);

		// Nota de Debito Compra
		tipoMov6.setDescripcion("Nota Débito Compra");
		tipoMov6.setClase("CompraLocalFactura");
		tipoMov6.setSigla(Configuracion.SIGLA_TM_NOTA_DEBITO_COMPRA);
		tipoMov6.setTipoIva(this.tipoIva1);
		tipoMov6.setTipoEmpresa(proveedor);
		tipoMov6.setTipoComprobante(comprobanteLegal);
		tipoMov6.setTipoOperacion(operacionCompra);
		tipoMov6.setTipoDocumento(tipoNotaDebito);
		grabarDB(tipoMov6);

		// Orden de Compra
		tipoMov7.setDescripcion("Orden de Compra");
		tipoMov7.setClase("CompraLocalOrden");
		tipoMov7.setSigla(Configuracion.SIGLA_TM_ORDEN_COMPRA);
		tipoMov7.setTipoIva(this.tipoIva3);
		tipoMov7.setTipoEmpresa(proveedor);
		tipoMov7.setTipoComprobante(comprobanteInterno);
		tipoMov7.setTipoOperacion(operacionCompra);
		tipoMov7.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov7);

		// Orden de Compra Importacion
		tipoMov8.setDescripcion("Orden de Compra-Importación");
		tipoMov8.setClase("ImportacionPedidoCompra");
		tipoMov8.setSigla(Configuracion.SIGLA_TM_ORDEN_COMPRA_IMPOR);
		tipoMov8.setTipoIva(this.tipoIva3);
		tipoMov8.setTipoEmpresa(proveedor);
		tipoMov8.setTipoComprobante(comprobanteInterno);
		tipoMov8.setTipoOperacion(operacionCompra);
		tipoMov8.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov8);

		// Factura de Gasto Contado
		tipoMov9.setDescripcion("Fac. Gasto Contado");
		tipoMov9.setClase("Gasto");
		tipoMov9.setSigla(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO);
		tipoMov9.setTipoIva(this.tipoIva1);
		tipoMov9.setTipoEmpresa(proveedor);
		tipoMov9.setTipoComprobante(comprobanteLegal);
		tipoMov9.setTipoOperacion(operacionGasto);
		tipoMov9.setTipoDocumento(tipoFacturaContado);
		grabarDB(tipoMov9);

		// Factura de Gasto Credito
		tipoMov10.setDescripcion("Fac. Gasto Crédito");
		tipoMov10.setClase("Gasto");
		tipoMov10.setSigla(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO);
		tipoMov10.setTipoIva(this.tipoIva1);
		tipoMov10.setTipoEmpresa(proveedor);
		tipoMov10.setTipoComprobante(comprobanteLegal);
		tipoMov10.setTipoOperacion(operacionGasto);
		tipoMov10.setTipoDocumento(tipoFacturaCredito);
		grabarDB(tipoMov10);

		// Autofactura
		tipoMov11.setDescripcion("Autofactura");
		tipoMov11.setClase("Gasto");
		tipoMov11.setSigla(Configuracion.SIGLA_TM_AUTO_FACTURA);
		tipoMov11.setTipoIva(this.tipoIva3);
		tipoMov11.setTipoEmpresa(yhaguy);
		tipoMov11.setTipoComprobante(comprobanteLegal);
		tipoMov11.setTipoOperacion(operacionGasto);
		tipoMov11.setTipoDocumento(tipoAutoFactura);
		grabarDB(tipoMov11);
		
		// Boleta de Venta
		tip_Mov11.setDescripcion("Boleta de Venta");
		tip_Mov11.setClase("Gasto");
		tip_Mov11.setSigla(Configuracion.SIGLA_TM_BOLETA_VENTA);
		tip_Mov11.setTipoIva(this.tipoIva3);
		tip_Mov11.setTipoEmpresa(proveedor);
		tip_Mov11.setTipoComprobante(comprobanteLegal);
		tip_Mov11.setTipoOperacion(operacionGasto);
		tip_Mov11.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tip_Mov11);

		// Orden de Gasto
		tipoMov12.setDescripcion("Orden de Gasto");
		tipoMov12.setClase("OrdenPedidoGasto");
		tipoMov12.setSigla(Configuracion.SIGLA_TM_ORDEN_GASTO);
		tipoMov12.setTipoIva(this.tipoIva1);
		tipoMov12.setTipoEmpresa(proveedor);
		tipoMov12.setTipoComprobante(comprobanteInterno);
		tipoMov12.setTipoOperacion(operacionGasto);
		tipoMov12.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov12);

		// Recibo de Pago
		tipoMov13.setDescripcion("Órden de Pago");
		tipoMov13.setClase("Recibo");
		tipoMov13.setSigla(Configuracion.SIGLA_TM_RECIBO_PAGO);
		tipoMov13.setTipoIva(this.tipoIva1);
		tipoMov13.setTipoEmpresa(proveedor);
		tipoMov13.setTipoComprobante(comprobanteLegal);
		tipoMov13.setTipoOperacion(operacionPago);
		tipoMov13.setTipoDocumento(tipoReciboDinero);
		grabarDB(tipoMov13);

		// Pagare
		tipoMov14.setDescripcion("Pagaré");
		tipoMov14.setClase("");
		tipoMov14.setSigla(Configuracion.SIGLA_TM_PAGARE);
		tipoMov14.setTipoIva(this.tipoIva1);
		tipoMov14.setTipoEmpresa(proveedor);
		tipoMov14.setTipoComprobante(comprobanteLegal);
		tipoMov14.setTipoOperacion(operacionPago);
		tipoMov14.setTipoDocumento(tipoPagare);
		grabarDB(tipoMov14);

		// Retencion Iva
		tipoMov15.setDescripcion("Retención Iva");
		tipoMov15.setClase("");
		tipoMov15.setSigla(Configuracion.SIGLA_TM_RETENCION);
		tipoMov15.setTipoIva(this.tipoIva1);
		tipoMov15.setTipoEmpresa(proveedor);
		tipoMov15.setTipoComprobante(comprobanteLegal);
		tipoMov15.setTipoOperacion(operacionPago);
		tipoMov15.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov15);

		// Anticipo - Pago
		tipoMov16.setDescripcion("Anticipo de Pago");
		tipoMov16.setClase("Recibo");
		tipoMov16.setSigla(Configuracion.SIGLA_TM_ANTICIPO_PAGO);
		tipoMov16.setTipoIva(this.tipoIva1);
		tipoMov16.setTipoEmpresa(proveedor);
		tipoMov16.setTipoComprobante(comprobanteLegal);
		tipoMov16.setTipoOperacion(operacionPago);
		tipoMov16.setTipoDocumento(tipoReciboDinero);
		grabarDB(tipoMov16);

		// Factura Venta Contado
		tipoMov17.setDescripcion("Fac. Venta Contado");
		tipoMov17.setClase("Venta");
		tipoMov17.setSigla(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO);
		tipoMov17.setTipoIva(this.tipoIva1);
		tipoMov17.setTipoEmpresa(cliente);
		tipoMov17.setTipoComprobante(comprobanteLegal);
		tipoMov17.setTipoOperacion(operacionVenta);
		tipoMov17.setTipoDocumento(tipoFacturaContado);
		grabarDB(tipoMov17);

		// Factura Venta Credito
		tipoMov18.setDescripcion("Fac. Venta Credito");
		tipoMov18.setClase("Venta");
		tipoMov18.setSigla(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
		tipoMov18.setTipoIva(this.tipoIva1);
		tipoMov18.setTipoEmpresa(cliente);
		tipoMov18.setTipoComprobante(comprobanteLegal);
		tipoMov18.setTipoOperacion(operacionVenta);
		tipoMov18.setTipoDocumento(tipoFacturaCredito);
		grabarDB(tipoMov18);

		// Nota de Credito Venta
		tipoMov19.setDescripcion("Nota de Crédito-Venta");
		tipoMov19.setClase("Venta");
		tipoMov19.setSigla(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA);
		tipoMov19.setTipoIva(this.tipoIva1);
		tipoMov19.setTipoEmpresa(cliente);
		tipoMov19.setTipoComprobante(comprobanteLegal);
		tipoMov19.setTipoOperacion(operacionVenta);
		tipoMov19.setTipoDocumento(tipoNotaCredito);
		grabarDB(tipoMov19);

		// Nota de Credito Venta
		tipoMov20.setDescripcion("Nota de Débito-Venta");
		tipoMov20.setClase("Venta");
		tipoMov20.setSigla(Configuracion.SIGLA_TM_NOTA_DEBITO_VENTA);
		tipoMov20.setTipoIva(this.tipoIva1);
		tipoMov20.setTipoEmpresa(cliente);
		tipoMov20.setTipoComprobante(comprobanteLegal);
		tipoMov20.setTipoOperacion(operacionVenta);
		tipoMov20.setTipoDocumento(tipoNotaDebito);
		grabarDB(tipoMov20);

		// Presupuesto de Venta
		tipoMov21.setDescripcion("Presupuesto de Venta");
		tipoMov21.setClase("Venta");
		tipoMov21.setSigla(Configuracion.SIGLA_TM_PRESUPUESTO_VENTA);
		tipoMov21.setTipoIva(this.tipoIva3);
		tipoMov21.setTipoEmpresa(cliente);
		tipoMov21.setTipoComprobante(comprobanteInterno);
		tipoMov21.setTipoOperacion(operacionVenta);
		tipoMov21.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov21);

		// Pedido de Venta
		tipoMov22.setDescripcion("Pedido de Venta");
		tipoMov22.setClase("Venta");
		tipoMov22.setSigla(Configuracion.SIGLA_TM_PEDIDO_VENTA);
		tipoMov22.setTipoIva(this.tipoIva3);
		tipoMov22.setTipoEmpresa(cliente);
		tipoMov22.setTipoComprobante(comprobanteInterno);
		tipoMov22.setTipoOperacion(operacionVenta);
		tipoMov22.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov22);

		// Recibo de Cobro
		tipoMov23.setDescripcion("Recibo de Cobro");
		tipoMov23.setClase("Recibo");
		tipoMov23.setSigla(Configuracion.SIGLA_TM_RECIBO_COBRO);
		tipoMov23.setTipoIva(this.tipoIva1);
		tipoMov23.setTipoEmpresa(cliente);
		tipoMov23.setTipoComprobante(comprobanteLegal);
		tipoMov23.setTipoOperacion(operacionCobro);
		tipoMov23.setTipoDocumento(tipoReciboDinero);
		grabarDB(tipoMov23);

		// Anticipo Cobro
		tipoMov24.setDescripcion("Anticipo de Cobro");
		tipoMov24.setClase("Recibo");
		tipoMov24.setSigla(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
		tipoMov24.setTipoIva(this.tipoIva1);
		tipoMov24.setTipoEmpresa(cliente);
		tipoMov24.setTipoComprobante(comprobanteLegal);
		tipoMov24.setTipoOperacion(operacionCobro);
		tipoMov24.setTipoDocumento(tipoReciboDinero);
		grabarDB(tipoMov24);

		// Nota de Remision
		tipoMov25.setDescripcion("Nota de Remisión");
		tipoMov25.setClase("Transferencia");
		tipoMov25.setSigla(Configuracion.SIGLA_TM_NOTA_REMISION);
		tipoMov25.setTipoIva(this.tipoIva1);
		tipoMov25.setTipoEmpresa(cliente);
		tipoMov25.setTipoComprobante(comprobanteLegal);
		tipoMov25.setTipoOperacion(operacionRemision);
		tipoMov25.setTipoDocumento(tipoNotaRemision);
		grabarDB(tipoMov25);

		// Transferencia Interna
		tipoMov26.setDescripcion("Transferencia Mercaderias");
		tipoMov26.setClase("Transferencia");
		tipoMov26.setSigla(Configuracion.SIGLA_TM_TRANS_MERCADERIA);
		tipoMov26.setTipoIva(this.tipoIva3);
		tipoMov26.setTipoEmpresa(yhaguy);
		tipoMov26.setTipoComprobante(comprobanteInterno);
		tipoMov26.setTipoOperacion(operacionRemision);
		tipoMov26.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov26);

		// Movimiento Bancario Cheque propio
		tipoMov27.setDescripcion("Cheque Propio");
		tipoMov27.setClase("BancoMovimiento");
		tipoMov27.setSigla(Configuracion.SIGLA_TM_EMISION_CHEQUE);
		tipoMov27.setTipoIva(this.tipoIva3);
		tipoMov27.setTipoEmpresa(banco);
		tipoMov27.setTipoComprobante(comprobanteLegal);
		tipoMov27.setTipoOperacion(operacionBancaria);
		tipoMov27.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov27);

		// Movimiento Bancario Deposito efectivo
		tipoMov29.setDescripcion("Deposito Efectivo");
		tipoMov29.setClase("BancoMovimiento");
		tipoMov29.setSigla(Configuracion.SIGLA_TM_DEPOSITO_BANCARIO);
		tipoMov29.setTipoIva(this.tipoIva3);
		tipoMov29.setTipoEmpresa(banco);
		tipoMov29.setTipoComprobante(comprobanteLegal);
		tipoMov29.setTipoOperacion(operacionBancaria);
		tipoMov29.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov29);

		// Movimiento Solicitud Nota Crédito-Venta
		tipoMov34.setDescripcion("Solicitud N.C. Venta");
		tipoMov34.setClase("NotaCredito");
		tipoMov34.setSigla(Configuracion.SIGLA_TM_SOLICITUD_NC_VENTA);
		tipoMov34.setTipoIva(this.tipoIva3);
		tipoMov34.setTipoEmpresa(cliente);
		tipoMov34.setTipoComprobante(comprobanteInterno);
		tipoMov34.setTipoOperacion(operacionVenta);
		tipoMov34.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov34);

		// Movimiento Solicitud Nota Crédito-Compra
		tipoMov35.setDescripcion("Solicitud N.C. Compra");
		tipoMov35.setClase("NotaCredito");
		tipoMov35.setSigla(Configuracion.SIGLA_TM_SOLICITUD_NC_COMPRA);
		tipoMov35.setTipoIva(this.tipoIva3);
		tipoMov35.setTipoEmpresa(proveedor);
		tipoMov35.setTipoComprobante(comprobanteInterno);
		tipoMov35.setTipoOperacion(operacionCompra);
		tipoMov35.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov35);
		
		// Movimiento Ajuste Stock Positivo
		tipoMov36.setDescripcion("Ajuste Stock Positivo");
		tipoMov36.setClase("AjusteStock");
		tipoMov36.setSigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
		tipoMov36.setTipoIva(this.tipoIva3);
		tipoMov36.setTipoEmpresa(proveedor);
		tipoMov36.setTipoComprobante(comprobanteInterno);
		tipoMov36.setTipoOperacion(operacionAjuste);
		tipoMov36.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov36);
		
		// Movimiento Ajuste Stock Negativo
		tipoMov37.setDescripcion("Ajuste Stock Negativo");
		tipoMov37.setClase("AjusteStock");
		tipoMov37.setSigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
		tipoMov37.setTipoIva(this.tipoIva3);
		tipoMov37.setTipoEmpresa(proveedor);
		tipoMov37.setTipoComprobante(comprobanteInterno);
		tipoMov37.setTipoOperacion(operacionAjuste);
		tipoMov37.setTipoDocumento(tipoOtrosDocumentos);
		grabarDB(tipoMov37);
		
	}

	// =======================================================================
	// ============================ Condición pago proveedor =================
	// =======================================================================

	CondicionPago icp = new CondicionPago();
	CondicionPago icp2 = new CondicionPago();
	CondicionPago icp3 = new CondicionPago();
	CondicionPago icp4 = new CondicionPago();
	CondicionPago icp5 = new CondicionPago();

	private void condicionPagoProveedor() throws Exception {
		icp.setDescripcion("Contado");
		icp.setPlazo(0);
		icp.setCuotas(0);
		icp.setDiasEntreCuotas(0);
		grabarDB(icp);

		icp2.setDescripcion("Crédito 30");
		icp2.setPlazo(30);
		icp2.setCuotas(1);
		icp2.setDiasEntreCuotas(30);
		grabarDB(icp2);

		icp3.setDescripcion("Crédito 60");
		icp3.setPlazo(60);
		icp3.setCuotas(2);
		icp3.setDiasEntreCuotas(30);
		grabarDB(icp3);

		icp4.setDescripcion("Crédito 90");
		icp4.setPlazo(90);
		icp4.setCuotas(3);
		icp4.setDiasEntreCuotas(30);
		grabarDB(icp4);

		icp5.setDescripcion("Otros");
		icp5.setPlazo(0);
		icp5.setCuotas(0);
		icp5.setDiasEntreCuotas(0);
		grabarDB(icp5);

	}

	// =======================================================================
	// ============================ Condición pago proveedor =================
	// =======================================================================

	EmpresaGrupoSociedad egs1 = new EmpresaGrupoSociedad();
	ContactoSexo sexo1 = new ContactoSexo();
	ContactoSexo sexo2 = new ContactoSexo();

	private void cargaEmpresaGrupoSociedad() throws Exception {
		egs1.setDescripcion(Configuracion.EMPRESA_GRUPO_NO_DEFINIDO);
		egs1.setAuxi("0");
		grabarDB(egs1);
	}

	private void cargaVarias() throws Exception {

		// No se debe cambiar el orden
		String[][] datos = {
				{ Configuracion.ID_TIPO_ESTADO_CLIENTE,
						Configuracion.SIGLA_ESTADO_CLIENTE,
						Configuracion.ESTADO_CLIENTE_ACTIVO, "Inactivo",
						"Temporal" },
				{ Configuracion.ID_TIPO_CATEGORIA_CLIENTE,
						Configuracion.SIGLA_CATEGORIA_CLIENTE, "No definida",
						"Al dia", "Incobrable", "Judiciales", "Inforconf" },
				{ Configuracion.ID_TIPO_PROFESIONES,
						Configuracion.SIGLA_PROFESIONES, "Chapista",
						"Mecanico", "Electricista" },
				{ Configuracion.ID_TIPO_TIPO_CONTACTO_INTERNO,
						Configuracion.SIGLA_TIPO_CONTACTO_INTERNO, "Vendedor",
						"Tecnico", "Cobrador" },
				{ Configuracion.ID_TIPO_CTA_CTE_TIPO_OPERACION,
						Configuracion.SIGLA_TIPO_OPERACION, "Contado",
						"Credito", "Cheque" },
				{ Configuracion.ID_TIPO_CTA_CTE_ESTADO,
						Configuracion.SIGLA_CTA_CTE_ESTADO, "Activo",
						"Inactivo" } };

		for (int i = 0; i < datos.length; i++) {
			String[] dato = datos[i];
			TipoTipo tt = new TipoTipo();
			tt.setDescripcion(dato[0]);
			grabarDB(tt);
			for (int j = 2; j < dato.length; j++) {
				Tipo tipo = new Tipo();
				tipo.setDescripcion(dato[j]);
				tipo.setSigla(dato[1]);
				tipo.setTipoTipo(tt);
				grabarDB(tipo);
			}
		}

		String[] estadosCiviles = { "Soltero", "Casado" };
		for (int i = 0; i < estadosCiviles.length; i++) {
			EstadoCivil estadoCivil = new EstadoCivil();
			estadoCivil.setDescripcion(estadosCiviles[i]);
			grabarDB(estadoCivil);
		}

		sexo1.setDescripcion("Masculino");
		grabarDB(sexo1);

		sexo2.setDescripcion("Femenino");
		grabarDB(sexo2);

		// articulo Presentacion Default
		ArticuloPresentacion ap = new ArticuloPresentacion();
		ap.setDescripcion(Configuracion.ID_ARTICULO_PRESENTACION_DEFAULT);
		ap.setObservacion("Sin Obs..");
		ap.setUnidad(1);
		ap.setPeso(0);
		ap.setUnidadMedida(this.sinReferenciaTipo);
		grabarDB(ap);

	}

	// =======================================================================
	// ============================ Regla de Precio ==========================
	// =======================================================================

	// Reglas de Precios
	TipoTipo reglaPrecio = new TipoTipo();
	Tipo reglaPrecioCliente = new Tipo();
	Tipo reglaPrecioArticulo = new Tipo();
	Tipo reglaPrecioVenta = new Tipo();
	Tipo reglaPrecioVendedor = new Tipo();
	// cliente
	Tipo reglaPrecioTipoCliente = new Tipo();
	Tipo reglaPrecioClienteCategoria = new Tipo();
	Tipo reglaPrecioRubroEmpresa = new Tipo();
	// articulo
	Tipo reglaPrecioArticuloMarca = new Tipo();
	Tipo reglaPrecioArticuloFamilia = new Tipo();
	Tipo reglaPrecioArticuloLinea = new Tipo();
	Tipo reglaPrecioArticuloParte = new Tipo();
	// venta
	Tipo reglaPrecioModoVenta = new Tipo();

	// vendedor rubro
	Tipo reglaPrecioVendedorRubro = new Tipo();
	// suc
	Tipo reglaPrecioSucursal = new Tipo();

	private void cargaReglaPrecio() throws Exception {

		reglaPrecio.setDescripcion(Configuracion.REGLA_PRECIO);
		grabarDB(reglaPrecio);

		Object[][] datos = {
				{ reglaPrecioRubroEmpresa, Configuracion.ID_TIPO_RUBRO_EMPRESAS },
				{ reglaPrecioTipoCliente, Configuracion.ID_TIPO_CLIENTE },
				{ reglaPrecioVendedor, Configuracion.REGLA_PRECIO_VENDEDOR },
				{ reglaPrecioVenta, Configuracion.REGLA_PRECIO_VENTA },
				{ reglaPrecioArticulo, Configuracion.REGLA_PRECIO_ARTICULO },
				{ reglaPrecioCliente, Configuracion.REGLA_PRECIO_CLIENTE },
				{ reglaPrecioArticuloMarca,
						Configuracion.ID_TIPO_ARTICULO_MARCA },
				{ reglaPrecioArticuloFamilia,
						Configuracion.ID_TIPO_ARTICULO_FAMILIA },
				{ reglaPrecioArticuloLinea,
						Configuracion.ID_TIPO_ARTICULO_LINEA },
				{ reglaPrecioArticuloParte,
						Configuracion.ID_TIPO_ARTICULO_PARTE },
				{ reglaPrecioModoVenta, Configuracion.ID_TIPO_VENTA_MODO },
				{ reglaPrecioSucursal, Configuracion.ID_TIPO_SUCURSAL },
				{ reglaPrecioVendedorRubro,
						Configuracion.ID_TIPO_VENDEDOR_RUBRO } };
		for (int i = 0; i < datos.length; i++) {
			Object[] d = datos[i];
			Tipo ti = (Tipo) d[0];
			String ID = (String) d[1];

			ti.setTipoTipo(reglaPrecio);
			ti.setDescripcion(ID);
			ti.setSigla(Configuracion.REGLA_PRECIO);
			grabarDB(ti);
		}

	}

	public void cargaTipos() throws Exception {
		this.cargaTiposGeneral();

		DBPolpulationTiposArticulos dbTipoA = new DBPolpulationTiposArticulos();
		dbTipoA.cargaTipoArticulos();
	}

	public static void main(String[] args) throws Exception {
		DBPopulationTipos db = new DBPopulationTipos();
		db.cargaTipos();

	}

}
