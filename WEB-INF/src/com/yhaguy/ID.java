package com.yhaguy;

import com.coreweb.IDCore;



public class ID extends IDCore {
	//Operacion generica para todos
	final public static String O_ABRIR_FORMULARIO = "AbrirFormulario";
	
	//Regla de precio
	final public static String F_REGLA_PRECIO = "ReglaPrecio";
	final public static String F_REGLA_PRECIO_VOLUMEN = "ReglaPrecioVolumen";
	final public static String F_REGLA_FORMA_PAGO = "ReglaPrecioFormaPago";
	final public static String F_REGLA_PRECIO_INSERTAR = "InsertarReglaPrecio";
	final public static String O_ABRIR_POPUP_REGLA_PRECIO = "PopupReglaPrecio";
	final public static String O_ABRIR_POPUP_REGLA_PRECIO_FORMA_PAGO = "PopupReglaPrecioFormaPago";

	
	// Ventas Mayoristas
	final public static String F_VENTA_MAYORISTA = "VentasMayorista";
	final public static String O_VENTA_MAYORISTA_CONTADO = "VentasMayoristaContado";
	final public static String O_VENTA_MAYORISTA_CREDITO = "VentasMayoristaCredito";
	
		
	//MODULO IMPORTACIONES..
	final public static String F_IMPORTACION_ABM = "Importacion";
	final public static String O_VER_DATOS_GENERALES = "VerDatosGenerales";
	final public static String O_VER_ORDEN_COMPRA = "VerOrdenCompra";
	final public static String O_VER_FACTURA = "VerFactura";
	final public static String O_VER_RECEPCION = "VerRecepcion";
	final public static String O_VER_RESUMEN = "VerResumen";
	final public static String O_VER_COSTO_FINAL = "VerCostoFinal";
	final public static String O_CHEQUEAR_IMPORTACION = "ChequearImportacion";
	final public static String O_CHEQUEAR_VENTAS = "ChequearVentas";
	final public static String O_CHEQUEAR_ADMINISTRACION = "ChequearAdministracion";
	final public static String O_ELIMINAR_ITEM_ORDEN_COMPRA = "EliminarItemOrdenCompra";	
	final public static String O_INSERTAR_ITEM_ORDEN_COMPRA = "InsertarItemOrdenCompra";
	final public static String O_IMPORTAR_ORDEN_COMPRA_CSV = "ImportarOrdenCompraCSV";
	final public static String O_IMPORTAR_PROFORMA_CSV= "ImportarProformaCSV";
	final public static String O_ENVIAR_CORREO = "EnviarCorreo";
	final public static String O_DERIVAR_ORDEN_COMPRA = "DerivarOrdenCompra";
	final public static String O_AGREGAR_FACTURA = "AgregarFactura";
	final public static String O_SUPRIMIR_FACTURA = "SuprimirFactura";
	final public static String O_ELIMINAR_ITEM_FACTURA = "EliminarItemFactura";
	final public static String O_INSERTAR_ITEM_FACTURA = "InsertarItemFactura";
	final public static String O_IMPORTAR_ITEMS = "ImportarItems";
	final public static String O_IMPORTAR_FACTURA_CSV = "ImportarFacturaCSV";
	final public static String O_INSERTAR_GASTOS_DESCUENTOS = "InsertarGastosDescuentos";
	final public static String O_DERIVAR_FACTURA = "DerivarFactura";
	final public static String O_CONFIRMAR_RECEPCION = "ConfirmarRecepcion";
	final public static String O_VER_DIFERENCIAS = "VerDiferencias";
	final public static String O_INSERTAR_GASTO_IMPREVISTO = "InsertarGastoImprevisto";
	final public static String O_ELIMINAR_GASTO_IMPREVISTO = "EliminarGastoImprevisto";
	final public static String O_CONFIRMAR_IMPORTACION = "ConfirmarImportacion";	
	
	//MODULO GASTOS
	final public static String F_FACTURA_GASTO ="FacturaGasto";
	
	final public static String F_IMPORTACION_GASTOS_ABM = "ImportacionGastos";	 
	final public static String O_ABRIR_IMPORTACION_GASTOS = "AbrirImportacionGastos";
	final public static String O_AGREGAR_GASTO = "AgregarGasto";
	final public static String O_CONFIRMAR_SUB_DIARIO = "ConfirmarSubDiario";
	
	final public static String F_GASTO_GENERAL_ABM = "GastoGeneral";
	final public static String O_ABRIR_GASTO_GENERAL = "AbrirGastoGeneral";
	
	//TIMBRADO
	final public static String F_TIMBRADO = "Timbrado";
	
	//MODULO PAGOS
	final public static String F_RECIBO = "Recibo";	
	final public static String F_RECIBO_INSERCION_DETALLE = "ReciboInsercionDetalle";
	
	//Articulos Temporales
	final public static String F_ARTICULO_TEMPORAL = "ArticuloTemporal";
	final public static String O_ABRIR_FORM_ARTICULO_TEMPORAL = "AbrirFormArticuloTemporal";
	
	//Transferencias
	final public static String F_TRANSFERENCIA_INTERNA_ABM = "TransferenciaInterna";
	final public static String F_TRANSFERENCIA_EXTERNA_ABM = "TransferenciaExterna";
	
	final public static String O_CONFIRMAR_PEDIDO_TRANSFERENCIA_INTERNA = "ConfirmarPedidoTransferenciaInterna";
	final public static String O_CANCELAR_PEDIDO_TRANSFERENCIA_INTERNA = "CancelarPedidoTransferenciaInterna";
	final public static String O_CANCELAR_TRANSFERENCIA_INTERNA = "CancelarTransferenciaInterna";
	final public static String O_ENVIAR_TRANSFERENCIA_INTERNA = "EnviarTransferenciaInterna";
	final public static String O_RECIBIR_TRANSFERENCIA_INTERNA = "RecibirTransferenciaInterna";
	final public static String O_UBICAR_TRANSFERENCIA_INTERNA = "UbicarTransferenciaInterna";
	
	final public static String O_CONFIRMAR_PEDIDO_TRANSFERENCIA_EXTERNA = "ConfirmarPedidoTransferenciaExterna";
	final public static String O_CANCELAR_PEDIDO_TRANSFERENCIA_EXTERNA = "CancelarPedidoTransferenciaExterna";
	final public static String O_CANCELAR_TRANSFERENCIA_EXTERNA = "CancelarTransferenciaExterna";
	final public static String O_ENVIAR_TRANSFERENCIA_EXTERNA = "EnviarTransferenciaExterna";
	final public static String O_RECIBIR_TRANSFERENCIA_EXTERNA = "RecibirTransferenciaExterna";
	final public static String O_UBICAR_TRANSFERENCIA_EXTERNA = "UbicarTransferenciaExterna";
	final public static String O_HABILITAR_TODAS_SUCURSALES = "HabilitarTodasSucursales";
	
	//Funcionarios
	final public static String F_FUNCIONARIOS_ABM = "Funcionarios";
	final public static String F_FUNCIONARIOS_POPUP_USUARIO = "FuncionariosPopupUsuario";
	final public static String F_FUNCIONARIOS_POPUP_SUCURSALES= "FuncionariosPopupSucursales";
	
	//Definiciones
	final public static String F_CONFIG_ABM = "Config";
	
	//Bancos
	final public static String F_BANCO_CHEQUE = "BancoCheque";
	final public static String F_CONCILIACION_ABM = "Conciliacion";
	final public static String F_BANCO_BOLETA_DEPOSITO_ABM = "BancoBoletaDeposito";
	final public static String F_BANCO_DESCUENTO_CHEQUE_ABM = "BancoDescuentoCheque";
	
	
	
	//Inventario
	final public static String F_INV_PLANILLA = "InvPlanilla";
	final public static String F_AGREGAR_INV_ARTICULO = "AgregarInvArticulo";
	final public static String F_INV_LOTE = "InvLote";
	
	//Reparto
	final public static String F_REPARTO = "Reparto";
	
	//Compras Locales
	final public static String F_COMPRA_LOCAL_ABM = "CompraLocal";
	final public static String O_COMPRA_LOCAL_ELIMINAR_ITEM_ORDEN = "CompraLocalEliminarItemOrden";
	final public static String O_COMPRA_LOCAL_INSERTAR_ITEM_ORDEN = "CompraLocalInsertarItemOrden";
	final public static String O_COMPRA_LOCAL_AUTORIZAR_ORDEN_COMPRA = "CompraLocalAutorizarOrdenCompra";
	final public static String O_COMPRA_LOCAL_ELIMINAR_ITEM_FACTURA = "CompraLocalEliminarItemFactura";
	final public static String O_COMPRA_LOCAL_INSERTAR_ITEM_FACTURA = "CompraLocalInsertarItemFactura";
	final public static String O_COMPRA_LOCAL_IMPORTAR_ITEMS = "CompraLocalImportarItems";
	final public static String O_COMPRA_LOCAL_AGREGAR_FACTURA = "CompraLocalAgregarFactura";
	final public static String O_COMPRA_LOCAL_SUPRIMIR_FACTURA = "CompraLocalSuprimirFactura";
	final public static String O_COMPRA_LOCAL_AGREGAR_DESCUENTO = "CompraLocalAgregarDescuento";
	final public static String O_COMPRA_LOCAL_INSERTAR_ITEM_RESUMEN = "CompraLocalInsertarItemResumen";
	final public static String O_COMPRA_LOCAL_ELIMINAR_ITEM_RESUMEN = "CompraLocalEliminarItemResumen"; 
	final public static String O_COMPRA_LOCAL_CONFIRMAR_COMPRA = "CompraLocalConfirmarCompra";
	final public static String O_COMPRA_LOCAL_VER_DIFERENCIAS = "CompraLocalVerDiferencias";
	final public static String O_COMPRA_LOCAL_CONFIRMAR_RECEPCION = "CompraLocalConfirmarRecepcion";
	final public static String O_COMPRA_LOCAL_VER_DATOS_GENERALES = "CompraLocalVerDatosGenerales";
	final public static String O_COMPRA_LOCAL_VER_ORDEN_COMPRA = "CompraLocalVerOrdenCompra";
	final public static String O_COMPRA_LOCAL_VER_FACTURA = "CompraLocalVerFactura";
	final public static String O_COMPRA_LOCAL_VER_RECEPCION = "CompraLocalVerRecepcion";
	final public static String O_COMPRA_LOCAL_VER_COSTO_FINAL = "CompraLocalVerCostoFinal"; 
	final public static String O_COMPRA_LOCAL_VER_GASTOS = "CompraLocalVerGastos"; 
	final public static String O_COMPRA_LOCAL_ENVIAR_CORREO = "CompraLocalEnviarCorreo";
	final public static String O_COMPRA_LOCAL_CSV = "CompraLocalCSV";
	
	//Repartos
	final public static String F_REPARTO_ABM = "Reparto";
	final public static String F_REPARTO_ALTERNATIVO_ABM = "RepartoAlternativo";
	final public static String O_CONFIRMAR_REPARTO = "ConfirmarReparto";
	final public static String O_FINALIZAR_REPARTO = "FinalizarReparto";
	final public static String O_CONFIRMAR_REPARTO_ALTERNATIVO = "ConfirmarRepartoAlternativo";
	final public static String O_FINALIZAR_REPARTO_ALTERNATIVO = "FinalizarRepartoAlternativo";
	
	
	//OrdenPedidoGastos
	final public static String F_ORDEN_PEDIDO_GASTO_ABM = "OrdenPedidoGasto";
	final public static String O_ORDEN_PEDIDO_GASTO_AUTORIZAR = "OrdenPedidoGastoAutorizar";
	final public static String O_ORDEN_PEDIDO_GASTO_INSERTAR_ITEM = "OrdenPedidoGastoInsertarItem";
	final public static String O_ORDEN_PEDIDO_GASTO_ELIMINAR_ITEM = "OrdenPedidoGastoEliminarItem";
	
	//Articulos
	final public static String F_ARTICULO_ABM = "Articulo";
	final public static String F_BUSCAR_ARTICULO = "BuscarArticulo";
	
	// Venta Pedido
	final public static String F_VENTA_PEDIDO = "VentaPedido";
	
	// Venta Presupuesto
	final public static String F_VENTA_PRESUPUESTO = "VentaPresupuesto";
	
	// Cliente Ocasional
	final public static String F_CLIENTE_OCASIONAL = "ClienteOcasional";
	
	final public static String F_CLIENTE_ABM_BODY = "Clientes";
	final public static String F_PROVEEDOR_ABM_BODY = "Proveedores";
	
	//CtaCte Empresa
	final public static String F_CTA_CTE_EMPRESA_DETALLE_MOVIMIENTO = "DetalleMovimientoCtaCte";
	final public static String F_CTA_CTE_EMPRESA_MAS_INFORMACION = "InformacionCtaCte";
	final public static String F_CTA_CTE_EMPRESA_ASIGNAR_IMPUTACION = "AsignarImputacionCtaCte";
	final public static String F_CUENTAS_POR_COBRAR = "CuentasCobrar";
	final public static String F_CUENTAS_A_PAGAR = "CuentasPagar";
	
	//Caja
	final public static String F_CAJA_ABM = "Caja";
	final public static String F_CAJA_PLANILLA_ABM = "CajaPlanilla";
	
	final public static String O_BOLETA_DEPOSITO_CERRAR = "CerrarBancoBoletaDeposito";
	
	
	// Contabilidad - Asientos
	final public static String F_VISOR_SUBDIARIO = "VisorSubdiario";
	
	final public static String F_BANCO_CONCILIACION_POPUP_CONCILIACION = "PopupConciliacion";
	
	
	
	
}
