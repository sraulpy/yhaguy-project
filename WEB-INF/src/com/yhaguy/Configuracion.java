package com.yhaguy;

import org.zkoss.zk.ui.Sessions;

import com.coreweb.Config;

public class Configuracion extends Config {

	/**************************************** SESION *******************************************/

	public static String ACCESO = "AccesoDTO";

	/********************************************************************************************/
	public static String PATH_SESSION = ".";

	public static String empresa = "Yhaguy Repuestos S.A.";
	
	public static final String EMPRESA_BATERIAS = "Yhaguy Baterias S.A.";
	public static final String EMPRESA_MRA = "Yhaguy Repuestos S.A.";
	public static final String USER_MOBILE = "sergio";
	
	public static final String URL_IMAGES_PUBLIC_MRA = "http://190.211.240.30/images/";
	public static final String URL_IMAGES_PUBLIC_BAT = "http://190.211.240.242/images/";

	public static String pathProforma = PATH_SESSION + ("/yhaguy/archivos/proformas/") + "/";
	public static String pathProformaGenerico = "/yhaguy/archivos/proformas/";
	public static String pathOrdenCompra = PATH_SESSION + ("/yhaguy/archivos/ordenCompras/") + "/";
	public static String pathOrdenCompraGenerico = "/yhaguy/archivos/ordenCompras/";
	public static String pathPedidoCompra = PATH_SESSION + ("/yhaguy/archivos/pedidoCompra/") + "/";
	public static String pathPedidoCompraAdjunto = "/reportes/compras/importacion/correos/";
	public static String pathReportesImportacion = "/reportes/compras/importacion/";
	public static String pathPedidoCompraAdjuntoDir = PATH_SESSION + ("/reportes/compras/importacion/correos/") + "/";
	public static String pathFacturaImportacion = PATH_SESSION + ("/yhaguy/archivos/facturaImportacion/") + "/";
	public static String pathFacturaImportacionGenerico = "/yhaguy/archivos/facturaImportacion/";
	public static String pathFacturaImportacionPDF = PATH_SESSION + ("/yhaguy/archivos/facturaImportacionPDF/") + "/";
	public static String pathFacturaImportacionPDFGenerico = "/yhaguy/archivos/facturaImportacionPDF/";
	public static String pathPresupuestoGasto = "/yhaguy/archivos/presupuestosGastos/";
	public static String pathReportesJasper = PATH_SESSION + ("/reportes/") + "/";
	
	public static String URL_IMAGENES_ARTICULOS = "/yhaguy/archivos/articulos/img/";
	public static String URL_ARCHIVOS_IMAGENES = "/yhaguy/archivos/imagenes/";
	public static String IMAGENES_PATH = PATH_SESSION + ("/core/images/") + "/";
	public static String URL_SIN_IMAGEN = URL_IMAGENES_ARTICULOS + "sin_imagen.png";
	public static String ICONO_YHAGUY_PATH = IMAGENES_PATH + "logoYhaguy.jpg";
	public static String IMPORTACION_PEDIDO_COMPRA_JASPER = PATH_SESSION + ("/yhaguy/jasper/") + "/pedidoCompra.jasper";
	public static String URL_ESPECIFICACION_ARTICULOS = "/yhaguy/archivos/articulos/especificaciones/";

	public static String PATH_ESPECIFICACION_ARTICULOS = PATH_SESSION + URL_ESPECIFICACION_ARTICULOS;
	public static String PATH_IMAGENES_ARTICULOS = PATH_SESSION + URL_IMAGENES_ARTICULOS;
	public static String urlBancoExtracto = ("/yhaguy/archivos/bancoExtracto/");
	public static String pathBancoExtracto = PATH_SESSION + urlBancoExtracto;	
	public static String pathRetencionTesaka = PATH_SESSION + ("/yhaguy/archivos/tesaka/") + "/";
	public static String pathConciliaciones = PATH_SESSION + ("/yhaguy/archivos/conciliacion/") + "/";
	public static String pathBarcodes = PATH_SESSION + ("/yhaguy/archivos/barcodes/") + "/";

	static {
		try {
			pathProforma = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/proformas/")
					+ "/";
			pathOrdenCompra = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/ordenCompras/")
					+ "/";
			pathPedidoCompra = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/pedidoCompra/")
					+ "/";
			pathPedidoCompraAdjuntoDir = Sessions.getCurrent().getWebApp()
					.getRealPath("/reportes/compras/importacion/correos/")
					+ "/";
			pathFacturaImportacion = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/facturaImportacion/")
					+ "/";
			pathFacturaImportacionPDF = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/facturaImportacionPDF/")
					+ "/";
			pathReportesJasper = Sessions.getCurrent().getWebApp()
					.getRealPath("/reportes/")
					+ "/";
			pathRetencionTesaka = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/tesaka/")
					+ "/";
			IMAGENES_PATH = Sessions.getCurrent().getWebApp()
					.getRealPath("/core/images/")
					+ "/";
			IMPORTACION_PEDIDO_COMPRA_JASPER = Sessions.getCurrent()
					.getWebApp().getRealPath("/yhaguy/jasper/")
					+ "/pedidoCompra.jasper";
			PATH_IMAGENES_ARTICULOS = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/articulos/img/")
					+ "/";
			PATH_ESPECIFICACION_ARTICULOS = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/articulos/especificaciones")
					+ "/";

			pathBancoExtracto = Sessions.getCurrent().getWebApp()
					.getRealPath(urlBancoExtracto)
					+ "/";
			pathConciliaciones = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/conciliacion/")
					+ "/";
			pathBarcodes = Sessions.getCurrent().getWebApp()
					.getRealPath("/yhaguy/archivos/barcodes/")
					+ "/";

		} catch (Exception e) {
			System.err.println("Sin session ......");
		}

	}

	/**************************************** CLAVES *******************************************/

	public static final long PEDIDO_CONFIRMADO_KEY = 5;
	public static final long SOLICITUD_COTIZACION_KEY = 2;
	public static final long PROFORMA_RECIBIDA_KEY = 3;
	public static final long PENDIENTE_ENVIO_KEY = 4;

	public static final int PROPIETARIO_IMPORTACION_KEY = 1;
	public static final int PROPIETARIO_VENTAS_KEY = 2;
	public static final int PROPIETARIO_ADMINISTRACION_KEY = 3;
	public static final int PROPIETARIO_AUDITORIA_KEY = 4;

	public static final int VALOR_IVA_10 = 10;
	public static final int VALOR_IVA_5 = 5;
	public static final int VALOR_DIFERENCIA_ACEPTADA_IVA = 50;
	public static final int VALOR_DIFERENCIA_ACEPTADA_GASTOS = 500;
	public static final int PORCENTAJE_RETENCION = 30;
	public static final int PORCENTAJE_RENTABILIDAD_MINIMA = 30;

	public static final int IMPORTE_TIPO_IVA_10 = 1;
	public static final int IMPORTE_TIPO_IVA_5 = 2;
	public static final int IMPORTE_TIPO_EXENTA = 3;
	
	public static final int LIMITE_ITEMS_FACTURA_VENTA = 9;
	public static final int LIMITE_ITEMS_FACTURA_VENTA_BAT = 10;
	public static final int LIMITE_ITEMS_RECIBO_COBRO = 10;

	public static final double IMPORTE_LIMITE_GASTO_FONDO_FIJO = 500000;
	public static final double IMPORTE_MINIMO_RETENCION = 700000;

	public static String CODIGO_ITEM_DESCUENTO_KEY = "@DESCUENTO";
	public static String CODIGO_ITEM_GASTO_KEY = "@GASTOS";
	public static String CODIGO_ITEM_PRORRATEO_KEY = "@PRORRATEO";
	public static String CODIGO_ITEM_GENERICO_MRA = "ART-MRA";
	public static String TIPO_ITEM_DESCUENTO_KEY = "1";
	public static String TIPO_ITEM_GASTO_KEY = "2";
	public static String CODIGO_CLIENTE_OCASIONAL_CL = "-CL000-";
	public static String CODIGO_CLIENTE_OCASIONAL = "000";

	public static final String ESTADO_PENDIENTE_KEY = "PEN";
	public static final String ESTADO_AUTORIZADO_KEY = "AUT";
	public static final String ESTADO_CANCELADO_KEY = "CAN";
	public static final String ESTADO_CERRADO_KEY = "CER";

	public static final String NRO_SUB_DIARIO = "SUB";
	public static final String NRO_ORDEN_PEDIDO_GASTO = "OCG";
	public static final String NRO_IMPORTACION_PEDIDO = "IMP";
	public static final String NRO_IMPORTACION_FACTURA = "FAC";
	public static final String NRO_TRANSFERENCIA_INTERNA = "TRF-INT";
	public static final String NRO_TRANSFERENCIA_EXTERNA = "TRF-EXT";
	public static final String NRO_RECIBO_PAGO = "REC-PAG";
	public static final String NRO_RECIBO_COBRO = "REC-COB";
	public static final String NRO_RECIBO_COBRO_MOBILE = "REC-COB-MOB";
	public static final String NRO_CANCELACION_CHEQUE_RECHAZADO = "CAN-CHQ";
	public static final String NRO_CAJA = "CAJ";
	public static final String NRO_CAJA_PERIODO = "CJP";
	public static final String NRO_CAJA_TESORERIA = "CJT";
	public static final String NRO_CAJA_TESORERIA_PERIODO = "CAJ-TES";
	public static final String NRO_CHEQUERA = "CHQ";
	public static final String NRO_COMPRA_LOCAL_ORDEN = "OCL";
	public static final String NRO_REPARTO = "REP";
	public static final String NRO_REPARTO_YHAGUY = "REP-YHA";
	public static final String NRO_REMISION = "REM";
	public static final String NRO_BOLETA_DEVOLUCION = "BOL";
	public static final String NRO_VENTA_PEDIDO = "V-PED";
	public static final String NRO_VENTA_PRESUPUESTO = "V-PRE";
	public static final String NRO_VENTA_FACTURA_CREDITO = "V-CRE";
	public static final String NRO_VENTA_FACTURA_CONTADO = "V-CON";
	public static final String NRO_BANCO_EXTRACTO = "BEX";
	public static final String SIG_COM_MOV_DEBE = "-DEB";
	public static final String NRO_CUENTA_CONTABLE = "CT";
	public static final String NRO_SOLICITUD_NC_VENTA = "SNCV";
	public static final String NRO_NC_VENTA = "NCV";
	public static final String NRO_CENTRO_COSTO = "CC";
	public static final String NRO_RETENCION_IVA = "RET-IVA";
	public static final String NRO_TALONARIOS = "TAL";
	public static final String NRO_CAJA_REPOSICION = "CAJ-ING";
	public static final String NRO_CAJA_EGRESO = "CAJ-EGR";

	public static String CUENTA_DEBE = "DEBE";
	public static String CUENTA_HABER = "HABER";

	public static String RUC_EMPRESA_LOCAL = "44444401-7";
	public static String RUC_EMPRESA_EXTERIOR = "99999901-0";
	
	public static final String RUC_PY_AUTOPARTES = "80087516-8";
	public static final String RUC_ICATURBO = "80001949-0";
	public static final String RUC_NF_AUTORREPUESTOS = "80080166-0";
	public static final String RUC_YHAGUY_REPUESTOS = "80024884-8";

	public static String PROPIETARIO_IMPORTACION_DESCRIPCION = "Importacion";
	public static String PROPIETARIO_VENTAS_DESCRIPCION = "Ventas";
	public static String PROPIETARIO_ADMINISTRACION_DESCRIPCION = "Administracion";
	public static String PROPIETARIO_AUDITORIA_DESCRIPCION = "Auditoria";

	public static String ESTADO_PEDIDO_COMPRA_ELABORACION = "EN ELABORACION";
	public static String ESTADO_PEDIDO_COMPRA_CONFIRMADO = "CONFIRMADO";
	public static String ESTADO_PEDIDO_COMPRA_ANULADO = "ANULADO";

	public static String EMPRESA_GRUPO_NO_DEFINIDO = "NO GRUPO";
	public static String ESTADO_CLIENTE_ACTIVO = "Activo";
	public static String TIPO_CLIENTE_MINORISTA = "Minorista";
	public static String CATEGORIA_CLIENTE_AL_DIA = "Al dia";

	// dr
	public static String ID_TIPO_TIPO_SINO = "SI / NO";

	public static long ID_TIPO_GASTO_IMPORTACION = 1;
	public static long ID_TIPO_GASTO_GENERAL = 2;
	public static String ID_TIPO_GASTO_COMPRA_LOCAL = "Gastos Compras Locales";

	public static final String ID_TIPO_MONEDA = "Moneda";
	public static final String ID_TIPO_MONEDA_GUARANI = "Guaranies";
	public static final String ID_TIPO_MONEDA_DOLAR = "Dolares";
	public static final String ID_TIPO_CAMBIO_APP = "CambioAPP";
	public static final String ID_TIPO_CAMBIO_BCP = "CambioBCP";
	public static final long ID_TIPO_GASTO_COMPRAS_LOCALES = -1;
	public static final String ID_TIPO_REPARTOS = "Tipos de Reparto";
	public static final long ID_ESTADO_ARTICULO_TEMPORAL = 3;
	public static final long ID_ESTADO_PENDIENTE_ENVIO = 4;
	public static final long ID_ESTADO_IMPORTACION_CERRADA = 6;
	public static final String ID_TIPO_PAIS_EMPRESA = "Pais Empresa";
	public static final String ID_TIPO_PERSONA = "Tipo Persona";
	public static final String ID_TIPO_EMPRESA_EMAILS = "Mails Empresa";
	public static final String ID_TIPO_VENTA = "Tipo Venta";
	public static final String ID_TIPO_BANCO_CUENTA = "Cuenta Bancaria";
	public static final String ID_TIPO_CAJA = "Tipo de Caja";
	public static final String ID_TIPO_CAJA_ESTADO = "Estado de Caja";
	public static final String ID_TIPO_CAJA_PERIODO_ESTADO = "Estado de Planilla de Caja";
	public static final String ID_TIPO_CAJA_DURACION = "Duracion de Caja";
	public static final String ID_TIPO_CAJA_REPOSICION = "Reposicion Caja";
	public static final String ID_TIPO_CAJA_REPOSICION_EGRESO = "Reposicion Caja - Tipo de Egreso";
	public static final String ID_TIPO_CAJA_CLASIFICACION = "Clasificacion Caja";
	public static final String ID_TIPO_IMPORTACION_ESTADO = "Estado de Importacion";
	public static final String ID_TIPO_RECIBO_ESTADO = "Estado de Recibo";
	public static final String ID_TIPO_RECIBO_FORMA_PAGO = "Forma de Pago del Recibo";
	public static final String ID_TIPO_CTA_CTE_PROVEEDOR_ESTADO = "Estado CtaCte Proveedor";
	public static final String ID_TIPO_CTA_CTE_EMPRESA_IMPUTACION = "Tipo de Imputacion";
	public static final String ID_TIPO_CTA_CTE_EMPRESA_ESTADO = "Estado CtaCte Empresa";
	public static final String ID_TIPO_CTA_CTE_EMPRESA_CARACTER_MOVIMIENTO = "Caracter del Movimiento de CtaCte Empresa";
	public static final String ID_TIPO_CTA_CTE_EMPRESA_SELECCION_MOV = "Estado de Saldo CtaCte ";
	public static final String ID_TIPO_DESCUENTO_COMPRA = "Tipo de Descuento en Compras";
	public static final String ID_TIPO_GASTO_COMPRA = "Tipo de Gasto en Compras";
	public static final String ID_TIPO_PRORRATEO_COMPRA = "Tipo de Prorrateo en Compras";
	public static final String ID_TIPO_RUBRO_EMPRESAS = "Rubros Empresas";
	public static final String ID_TIPO_IVA = "Tipos de Iva";
	public static final String ID_TIPO_VENTA_PEDIDO = "Tipo Pedido Venta";
	public static final String ID_TIPO_TIPO_ALERTA = "Tipo Alerta";
	public static final String ID_TIPO_NIVEL_ALERTA = "Nivel Alerta";
	public static final String ID_TIPO_REGIMEN_TRIBUTARIO = "Regimen Tributario";
	public static final String ID_TIPO_RESERVA = "Tipos de Reserva";
	public static final String ID_TIPO_ESTADO_RESERVA = "Estados de Reserva";
	public static final String ID_TIPO_MOTIVO_DEVOLUCION = "Motivos de Devolucion";
	public static final String ID_TIPO_BANCO_TARJETA_EXTRACTO_DETALLE = "Tipo de Detalle Banco Tarjeta Extracto";
	public static final String ID_TIPO_ZONAS = "Zonas";

	public static final long ID_DEPOSITO_IMPORTACION = 2;
	public static final long ID_DEPOSITO_PRINCIPAL = 2;
	public static final long ID_ITEM_RESERVA_LIBERADA = -1000;
	public static final long ID_PROVEEDOR_YHAGUY_MRA = 353;
	public static final long ID_SUC_ACCESO_SUR = 6922;

	// Id's condiciones de Pago
	public static long ID_CONDICION_PAGO_CONTADO = 1;
	public static long ID_CONDICION_PAGO_CREDITO_30 = 2;
	public static long ID_CONDICION_PAGO_CREDITO_60 = 3;
	public static long ID_CONDICION_PAGO_CREDITO_90 = 4;
	public static long ID_CONDICION_PAGO_OTROS = 5;

	// Importante: estos id's se usan para buscar las ventas
	// que deben ser por reparto o transferencias remision
	public static String ID_TIPO_VENTA_REPARTO = "VTA-REP"; // 27;
	public static String ID_ESTADO_REPARTO_PENDIENTE = "MOV-REP-PEN"; // 78;
	public static String ID_TIPO_TRANSFERENCIA_EXTERNA = "TRF-EXT"; // 2;
	public static String ID_ESTADO_PEDIDO_TRF_CONFIRMADO = "TRF-CONF"; // 5;

	public static String ID_TIPO_ESTADO_CLIENTE = "Estado Cliente";
	public static String ID_TIPO_CATEGORIA_CLIENTE = "Categoria Cliente";
	public static String ID_TIPO_CLIENTE = "Tipo Cliente";
	public static String ID_TIPO_PROFESIONES = "Profesiones";
	public static String ID_TIPO_TIPO_CONTACTO_INTERNO = "Tipo Contacto Interno";
	public static String ID_TIPO_CTA_CTE_TIPO_OPERACION = "Tipo Operacion";
	public static String ID_TIPO_CTA_CTE_ESTADO = "Cuenta Corriente Estado";
	public static String ID_TIPO_TIPO_PROVEEDOR = "Tipo Proveedor";
	public static String ID_TIPO_ESTADO_PROVEEDOR = "Estado Proveedor";
	public static String ID_TIPO_ESTADO_FUNCIONARIO = "Estados de Funcionarios";
	public static String ID_TIPO_CARGO_FUNCIONARIO = "Funcionario Cargo";
	public static String ID_TIPO_ARTICULO_ESTADO = "Articulo Estado";
	public static String ID_TIPO_ARTICULO_LINEA = "Articulo Linea";
	public static String ID_TIPO_ARTICULO_PARTE = "Artículo Parte";
	public static String ID_TIPO_ARTICULO_MARCA = "Articulo Marca";
	public static String ID_TIPO_ARTICULO_FAMILIA = "Articulo Familia";
	public static String ID_TIPO_ARTICULO_UNID_MED = "Articulo Unidad de Medida";
	public static String ID_TIPO_ESTADO_VENTA = "Estados de Venta";
	public static String ID_TIPO_IMPORTACION = "Tipos de Importacion";
	public static String ID_TIPO_COMPROBANTE = "Tipos de Comprobantes";
	public static String ID_TIPO_OPERACION = "Tipos de Operaciones en los Movimientos";
	public static String ID_TIPO_EMPRESA = "Tipos de Empresa";
	public static String ID_TIPO_ESTADO_CONCILIACION = "Estados de Conciliacion";
	public static String ID_TIPO_MODO_VENTA = "Modos de Venta";
	public static String ID_TIPO_NOTA_CREDITO_MOTIVOS = "Motivos de Notas de Créditos";
	public static String ID_TIPO_NOTA_CREDITO_DETALLE = "Tipo de Detalle de Notas de Credito";
	public static String ID_TIPO_ESTADO_COMPROBANTE = "Estados de Comprobantes";
	public static String ID_TIPO_CUENTAS_CONTABLES = "Tipos de Cuentas Contables";
	public static String ID_TIPO_DOCUMENTO = "Tipo de Documento";
	public static String ID_TIPO_PROCESADORA_TC = "Procesadoras de Tarjetas";
	public static String ID_TIPO_BOLETA_DEPOSITO = "Tipo de Banco Deposito";
	public static String ID_TIPO_BANCOS_TERCEROS = "Bancos para Cheques de Tercero";
	public static final String ID_TIPO_COMBUSTIBLE = "Tipos de Combustible";
	public static final String ID_TIPO_ALERTA_TALONARIO = "ALERTA_TALONARIOS";

	public static String ID_SUCURSALAPP_CENTRAL = "Central";
	public static String ID_SUCURSALAPP_CENTRAL_GRUPO_ELITE = "GE Central";
	public static String ID_SUCURSALAPP_CENTRAL_MRA = "Mariano R. Alonso";
	public static String ID_SUCURSALAPP_BATERIAS = "Baterias";
	
	public static String ID_TIPOTIPO_PROPIEDADES_VENTAS = "Propiedades de Ventas";
	public static String ID_TIPOTIPO_PROPIEDADES_SISTEMAS = "Propiedades de Sistemas";

	public static String ID_ARTICULO_PRESENTACION_DEFAULT = "DEFAULT";
	public static String ID_SUCURSALAPP_MCAL_LOPEZ = "Mcal. López";
	public static String ID_SUCURSALAPP_FDO = "Fdo. de la Mora";
	public static String PORCETAJE_DIFERENCIA_TARJETA_EXTRACTO = "PORCETAJE_DIFERENCIA_TARJETA_EXTRACTO";

	public static String DPTO_IMPORTACION_MAIL_ADDRESS = "importaciones@yhaguyrepuestos.com.py";
	public static String DPTO_IMPORTACION_MAIL_PASSWD = "yrmkt1970";
	public static String TEXTO_CORREO_IMPORTACION = "\n" + "Estimado Proveedor;" + "\n" + "\n" + "\t"
			+ "Se adjunta a este correo el Documento correspondiente...";
	public static String TEXTO_SOLICITUD_COTIZACION = "Solicitud Cotizacion";
	public static String TEXTO_PEDIDO_CONFIRMADO = "Orden Compra Confirmada";
	public static String TEXTO_PEDIDO_COMPRA_CSV_IMPORTADO = "Se Importo un Pedido Compra CSV : ";
	public static String TEXTO_PROFORMA_CSV_IMPORTADO = "Se importo una Proforma CSV : ";
	public static String TEXTO_FACTURA_CSV = "Se importo una Factura CSV : ";
	public static String TEXTO_CORREO_GUARDAR_DATOS = "Debe Guardar los Datos antes de enviar el Correo";
	public static String TEXTO_CORREO_ENVIADO = "El correo fue enviado - Asunto: ";
	public static String TEXTO_CORREO_ENVIADO_CORRECTAMENTE = "El correo fue correctamente enviado";
	public static String TEXTO_DERIVAR_PEDIDO_COMPRA = "El Pedido Compra fue derivado a: ";
	public static String TEXTO_ARTICULO_INEXISTENTE = "No se encuentra en la Base de Datos el Articulo con Codigo: ";
	public static String TEXTO_PREGUNTA_CONTINUAR = "\n \n Desea Continuar ?";
	public static String TEXTO_NUEVO_ARTICULO = "[NUEVO ARTICULO] - ";
	public static String TEXTO_ERROR_ITEM_NO_SELECCIONADO = "No se ha seleccionado ningún ítem";
	public static String TEXTO_BORRAR_ITEM_SELECCIONADO = "Esta seguro de borrar los siguientes items: ";
	public static String TEXTO_ELIMINAR_ITEM_SELECCIONADO = "Si continua se eliminaran los siguientes items: \n";
	public static String TEXTO_BORRAR_REGISTRO = "Esta seguro de borrar el registro: ";
	public static String TEXTO_DIFERENCIA_POR_CANTIDAD = "Cantidad no coincide";
	public static String TEXTO_NUEVO_ITEM = "Nuevo Item ";
	public static String TEXTO_ITEM_NO_ENCONTRADO_CSV = "No se encuentra en el CSV";
	public static String TEXTO_ITEM_NO_ENCONTRADO_EN_DETALLE = "No se encuentra en el Detalle";
	public static String TEXTO_ITEM_AGREGADO = "Se agrego un nuevo item : ";
	public static String TEXTO_ALERTA_IMPORTAR_CSV_PROFORMA = "Se ha encontrado las siguientes Diferencias al Importar el archivo Proforma CSV: \n";
	public static String TEXTO_NO_COINCIDE_COSTO_PROFORMA = "- Diferente costo Proforma del item: ";
	public static String TEXTO_CSV_PROFORMA_PISAR_DATOS = "\n Si continua, estos items se modificaran..";
	public static String TEXTO_DESEA_CONTINUAR = "\n Desea Continuar ?";
	public static String TEXTO_AGREGAR_NUEVA_FACTURA = "Agregar Nueva Factura";
	public static String TEXTO_TOTALES_NO_COINCIDEN = "Los totales no coinciden";
	public static String TEXTO_PRORRATEO = "El valor de prorrateo es: ";
	public static String TEXTO_NUEVA_FACTURA = "Se agrego una nueva Factura: ";
	public static String TEXTO_FACTURA_ELIMINADA = "Se elimino la Factura Nro: ";
	public static String TEXTO_SE_INGRESO_UN = "Se ingreso un: ";
	public static String TEXTO_DESCUENTO = "Descuento";
	public static String TEXTO_GASTO = "Gasto";
	public static String TEXTO_SUB_DIARIO_CONFIRMADO = "El Sub-Diario se ha confirmado satisfactoriamente";
	public static String TEXTO_IVA_10 = "Valor Iva 10% : ";
	public static String TEXTO_IVA_5 = "Valor Iva 5% : ";
	public static String TEXTO_GRAVADO_10 = "Valor Gravado 10% : ";
	public static String TEXTO_GRAVADO_5 = "Valor Gravado 5% : ";
	public static String TEXTO_AGREGAR_TIMBRADO = "Desea asignar un nuevo timbrado al Proveedor: ";
	public static String TEXTO_GASTO_AGREGADO = "Se ha agregado un Gasto : ";
	public static String TEXTO_GASTOS_DEPARTAMENTO = "GASTOS DEPARTAMENTO: ";
	public static String TEXTO_SE_ELIMINO_ITEM = "Se elimino un item: ";
	public static String TEXTO_ESTADO_TRF_CAMBIADO = "El estado de la transferencia cambio a: ";
	public static String TEXTO_VEHICULO_NUEVO = "Se ha creado un nuevo vehículo";
	public static String TEXTO_ESTADO_REP_CAMBIADO = "El estado del reparto cambio a: ";
	public static String TEXTO_REM_CREADA = "Ha sido creada la remision: ";
	public static String TEXTO_ERROR_DEP_NO_HAB_ENTRADA = "No está habilitado para realizar operaciones sobre el depósito de entrada.";
	public static String TEXTO_ERROR_DEP_NO_HAB_SALIDA = "No está habilitado para realizar operaciones sobre el depósito de salida.";
	public static String TEXTO_MAS_DE_UN_ITEM_SELECCIONADO = "No puede seleccionar más de un ítem";

	public static String CAMPO_NRO_FACTURA = "Nro. Factura - ";
	public static String CAMPO_TOTAL_FACTURA = "Total Factura:";
	public static String CAMPO_FECHA = "Fecha:";
	public static String CAMPO_REFERENCIA = "Referencia:";
	public static String CAMPO_CODIGO_GASTO_DESCUENTO = "Codigo: ";
	public static String CAMPO_TIPO_GASTO_DESCUENTO = "Tipo: ";
	public static String CAMPO_DESCRIPCION_GASTO_DESCUENTO = "Descripcion: ";
	public static String CAMPO_VALOR_GASTO_DESCUENTO = "Valor: ";
	public static String CAMPO_NRO_DESPACHO = "DESPACHO NUMERO: ";

	public static String TIPO_IMPORTE_IVA_10 = "Iva 10% Incluído";
	public static String TIPO_IMPORTE_IVA_5 = "Iva 5% Incluído";
	public static String TIPO_IMPORTE_EXENTA = "Exenta";

	public static String ESTADO_GUARDADO = "Guardado";
	public static String ESTADO_PENDIENTE = "Sin reserva";
	public static String ESTADO_NUEVO = "Pendiente de guardar";
	public static String ESTADO_ERROR = "Stock insuficiente ";

	public static String TIPO_TRF_INTERNA = "INTERNA";
	public static String TIPO_TRF_REMISION = "REMISION";

	public static String ESTADO_RESERVA_ACTIVA = "Activa";
	public static String ESTADO_RESERVA_REPARTO = "Reparto";
	public static String ESTADO_RESERVA_CANCELADA = "Cancelada";
	public static String ESTADO_RESERVA_FINALIZADA = "Finalizada";

	public static String TIPO_RESERVA_INTERNA = "Interna";
	public static String TIPO_RESERVA_REMISION = "Remision";
	public static String TIPO_RESERVA_VENTA = "Venta";

	public static String TIPO_MOV_TRF_INTERNA = "TRANSFERENCIA INTERNA";
	public static String TIPO_MOV_TRF_REMISION = "TRANSFERENCIA REMISION";
	public static String TIPO_MOV_VTA_CONTADO = "VENTA CONTADO";
	public static String TIPO_MOV_VTA_CREDITO = "VENTA CREDITO";
	public static String TIPO_MOV_INVENTARIO = "INVENTARIO";
	public static String TIPO_MOV_COMPRA_LOCAL_CREDITO = "COMPRA LOCAL CREDITO";
	public static String TIPO_MOV_COMPRA_LOCAL_CONTADO = "COMPRA LOCAL CONTADO";
	public static String TIPO_MOV_COMPRA_IMPORTACION = "COMPRA IMPORTACION";
	public static String TIPO_MOV_ORDEN_COMPRA_LOCAL = "ORDEN DE COMPRA LOCAL";
	public static String TIPO_MOV_NOTA_CREDITO = "NOTA DE CREDITO";
	public static String TIPO_MOV_TRF_INTERNA_CANC = "TRANSFERENCIA INTERNA CANCELACION";
	public static String TIPO_MOV_TRF_REMISION_CANC = "TRANSFERENCIA REMISION CANCELACION";
	public static String TIPO_MOV_COBRO = "COBRO";
	public static String TIPO_MOV_PAGO = "PAGO";

	public static String TIPO_CTA_CTE_EMPRESA_ESTADO_ACTIVO = "Activo";
	public static String TIPO_CTA_CTE_EMPRESA_ESTADO_INACTIVO = "Inactivo";
	public static String TIPO_CTA_CTE_EMPRESA_ESTADO_BLOQUEADO = "Bloqueado";
	public static String TIPO_CTA_CTE_EMPRESA_ESTADO_SINCUENTA = "Sin Cta. Cte.";
	public static String TIPO_CTA_CTE_CARACTER_MOV_PROVEEDOR = "Proveedor";
	public static String TIPO_CTA_CTE_CARACTER_MOV_CLIENTE = "Cliente";
	public static String TIPO_CTA_CTE_CARACTER_MOV_TODOS = "Todos";
	public static String TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_TODOS = "Todos";
	public static String TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_PENDIENTES = "Pendientes";
	public static String TIPO_CTA_CTE_EMPRESA_SELECCION_MOV_VENCIDOS = "Vencidos";
	public static String TIPO_CTA_CTE_EMPRESA_IMPUTACION_PARCIAL = "Parcial";
	public static String TIPO_CTA_CTE_EMPRESA_IMPUTACION_COMPLETA = "Completa";
	public static String CTA_CTE_EMPRESA_LINEA_CREDITO_DEFAULT = "Sin Linea de Credito";

	public static String TIPO_SUCURSAL_APP_TODAS = "Todas";

	public static String OP_SUMA = "suma";
	public static String OP_RESTA = "resta";

	/*************************************
	 * ERRORES Y EXCEPCIONES
	 ***********************************/

	public static String TEXTO_ERROR_IMPORTAR_PEDIDO_COMPRA = "El Costo Proforma no debe contener valores al importar un Pedido Compra CSV.";
	public static String TEXTO_ERROR_PROVEEDOR = "El Proveedor del Pedido Compra no coincide con el archivo importado."
			+ "\n" + "Favor Verifique..";
	public static String TEXTO_ERROR_IMPORTAR_CSV_PROFORMA_ITEM_BD = "Verifique los Datos del Archivo CSV importado. \n No se ha encontrado en la Base de Datos el item: ";
	public static String TEXTO_ERROR_CAMPO_NO_NULO = "Debe ingresar un valor en el Campo: ";
	public static String TEXTO_ERROR_CAMPO = "Error en el Campo: ";
	public static String TEXTO_ERROR_VALOR_MAYOR_A_CERO = "El valor debe ser mayor a cero";
	public static String TEXTO_ERROR_ITEM_DUPLICADO = "No se permite items duplicados";
	public static String TEXTO_ERROR_ITEM_CANTIDAD = "El campo cantidad debe ser mayor a cero";
	public static String TEXTO_ERROR_VALOR_DUPLICADO = "No se permite valores duplicados";
	public static String TEXTO_ERROR_SUB_DIARIO = "No es Posible Confirmar el Sub-Diario. \n El Saldo no es igual a 0(cero)";
	public static String TEXTO_ERROR_SUB_DIARIO_CONFIRMADO = "No es posible Modificar. \n El Sub-Diario ya fue confirmado";
	public static String TEXTO_ERROR_DERIVAR_PEDIDO_COMPRA = "No es posible Derivar, el Propietario actual es: \n";
	public static String TEXTO_ERROR_IVA_10 = "El valor Gravado 10% No concuerda con el valor IVA 10%. \n";
	public static String TEXTO_ERROR_IVA_5 = "El valor Gravado 5% No concuerda con el valor IVA 5%. \n";
	public static String TEXTO_ERROR_VALIDACION = "Error al validar los datos: \n";
	public static String TEXTO_ERROR_CAMPOS_REQUERIDOS = "Los Campos Requeridos (*) \n No pueden quedar vacios";
	public static String TEXTO_ERROR_PROVEEDOR_NO_SELECCIONADO = "Debe seleccionar un Proveedor";
	public static String TEXTO_ERROR_YA_EXISTE_EN_LA_BD = "El Registro que intenta ingresar ya existe en la Base de Datos ";
	public static String TEXTO_ERROR_VENCIMIENTO_TIMBRADO = "La fecha de la factura no puede ser mayor o igual a la fecha de vencimiento del timbrado";
	public static String TEXTO_ERROR_COMPROBANTE_FISICO = "Debe especificar el motivo por el cual no se cuenta con el Comprobante físico";
	public static String TEXTO_ERROR_GASTOS_SIN_COMPROBANTE = "No es posible confirmar el Sub-Diario debido a que existen Gastos sin Comprobante Fisico";
	public static String TEXTO_ERROR_IMPORTACION_NO_CONFIRMADA = "No es posible confirmar el Sub-Diario debido a que la Importación aun no fue confirmada";
	public static String TEXTO_ERROR_TIPO_CAMBIO = "Debe ingresar un valor correcto para el Tipo de Cambio";
	public static String TEXTO_ERROR_INGRESAR_ITEM = "Debe ingresar al menos un item ";
	public static String TEXTO_ERROR_CORREO = "El destinatario, el asunto y el texto del Correo son obligatorios";
	public static String TEXTO_ERROR_DIRECCIONES_CORREO = "debe ingresar direcciones de correo válidas";
	public static String TEXTO_ERROR_PEDIDO_CONFIRMADO = "No es posible Modificar. \n El Pedido ya fue confirmado";
	public static String TEXTO_ERROR_FORMATO_FACTURA = "Mal formato del número de Factura";
	public static String TEXTO_ERROR_PAGO_ANULADO = "Existen Movimientos pendientes de Grabar en la Caja..\n Debe guardarlos para poder realizar esta operación";

	/******************************
	 * CONSTANTES DE LOS .ZUL
	 *****************************************/

	public static String ABRIR_POPUP_REGLA_PRECIO_ZUL = "/yhaguy/gestion/venta/ConsultaReglaPrecioPopup.zul";
	public static String ABRIR_POPUP_REGLA_PRECIO_FORMA_PAGO = "/yhaguy/gestion/venta/ConsultaReglaPrecioFormaPagoPopup.zul";
	public static String ABRIR_REGLA_PRECIO_VOLUMEN = "/yhaguy/gestion/venta/ReglaPrecioVolumen.zul";
	public static String ABRIR_REGLA_PRECIO_FORMA_PAGO = "/yhaguy/gestion/venta/ReglaPrecioFormaPago.zul";
	public static String INSERTAR_ITEM_PEDIDO_COMPRA_ZUL = "/yhaguy/gestion/compras/importacion/insertarItem.zul";
	public static String LISTA_PROPIETARIOS_PEDIDO_COMPRA_ZUL = "/yhaguy/gestion/compras/importacion/listaPropietariosPedidoCompra.zul";
	public static String LISTA_PROPIETARIOS_IMPORTACION_FACTURA_ZUL = "/yhaguy/gestion/compras/importacion/listaPropietariosImportacionFactura.zul";
	public static String CORREO_IMPORTACION_ZUL = "/yhaguy/gestion/compras/importacion/correoImportacion.zul";
	public static String DIFERENCIAS_CSV_ZUL = "/yhaguy/gestion/compras/importacion/diferenciasCSV.zul";
	public static String IMPORTACION_FACTURA_GASTOS_DESC_ZUL = "/yhaguy/gestion/compras/importacion/gastos_descuentos.zul";
	public static String GASTOS_FACTURA_ZUL = "/yhaguy/gestion/compras/gastos/subDiario/gasto.zul";
	public static String TIMBRADO_ZUL = "/yhaguy/gestion/compras/timbrado/timbrado.zul";
	public static String ORDEN_PEDIDO_GASTO_DETALLE_ZUL = "/yhaguy/gestion/compras/gastos/generales/pedidos/insercionDetalle.zul";
	public static String INSERTAR_NUEVA_FACTURA_ZUL = "/yhaguy/gestion/compras/importacion/nuevaFactura.zul";
	public static String RECIBO_ADD_FACTURA_ZUL = "/yhaguy/gestion/caja/recibos/insertarDetalle.zul";
	public static String RECIBO_ADD_FORMA_PAGO_ZUL = "/yhaguy/gestion/caja/recibos/insertarFormaPago.zul";
	public static String RECIBO_ZUL = "/yhaguy/gestion/caja/recibos/reciboBody.zul";
	public static String MOTIVO_ANULADO_ZUL = "/yhaguy/gestion/pagos/motivoAnulado.zul";
	public static String CAJA_REPOSICION_ZUL = "/yhaguy/gestion/caja/periodo/cajaReposicion.zul";
	public static String CAJA_VENTA_ZUL = "/yhaguy/gestion/caja/periodo/cajaVenta.zul";
	public static String CAJA_NOTA_CREDITO_ZUL = "yhaguy/gestion/caja/periodo/cajaNotaCredito.zul";
	public static String CHEQUE_ZUL = "/yhaguy/gestion/bancos/cheque.zul";
	public static String CHEQUERA_ZUL = "/yhaguy/gestion/bancos/chequera.zul";
	public static String AGREGARINVARTICULO_ZUL = "/yhaguy/gestion/inventario/agregarArticulo.zul";
	public static String INSERTAR_ITEM_COMPRA_LOCAL_ORDEN_ZUL = "/yhaguy/gestion/compras/locales/insertarItemOrden.zul";
	public static String INSERTAR_DETALLE_REPARTO_ZUL = "/yhaguy/gestion/reparto/insertarDetalleReparto.zul";
	public static String VER_DETALLES_ZUL = "/yhaguy/gestion/reparto/verDetalles.zul";
	public static String VER_DETALLE_ROMANEO_ZUL = "/yhaguy/gestion/reparto/romaneoReparto.zul";
	public static String INSERTAR_ITEM_COMPRA_LOCAL_FACTURA_ZUL = "/yhaguy/gestion/compras/locales/insertarItemFactura.zul";
	public static String AGREGAR_FACTURA_COMPRA_LOCAL_ZUL = "/yhaguy/gestion/compras/locales/agregarFactura.zul";
	public static String IMPORTAR_ITEMS_ORDEN_COMPRA_LOCAL_ZUL = "/yhaguy/gestion/compras/locales/importarItems.zul";
	public static String AGREGAR_RESUMEN_GASTO_COMPRA_LOCAL_ZUL = "/yhaguy/gestion/compras/locales/insertarResumenGasto.zul";
	public static String INSERTAR_DESCUENTO_COMPRA_LOCAL_ZUL = "/yhaguy/gestion/compras/locales/insertarDescuento.zul";
	public static String INSERTAR_ITEM_IMPORTACION_FACTURA_ZUL = "/yhaguy/gestion/compras/importacion/insertarItemFactura.zul";
	public static String IMPORTAR_ITEMS_IMPORTACION_ZUL = "/yhaguy/gestion/compras/importacion/importarItems.zul";
	public static String IMPORTACION_GASTOS_DESCTOS_ZUL = "/yhaguy/gestion/compras/importacion/gastos_descuentos.zul";
	public static String IMPORTACION_GASTOS_IMPREVISTOS_ZUL = "/yhaguy/gestion/compras/importacion/agregarGastoImprevisto.zul";
	public static String CORREO_COMPRA_LOCAL_ZUL = "/yhaguy/gestion/compras/locales/correoCompraLocal.zul";
	public static String BUSCAR_ARTICULO_ZUL = "/yhaguy/gestion/articulos/buscarArticulos.zul";
	public static String RESERVAS_ARTICULO_ZUL = "/yhaguy/gestion/articulos/reservasArticulo.zul";
	public static String INSERTAR_ITEM_GASTO = "/yhaguy/gestion/compras/gastos/subDiario/insertarDetalle.zul";
	public static String INGRESAR_ITEM_GASTO = "/yhaguy/gestion/compras/gastos/subDiario/articuloGasto.zul";
	public static String OTROS_DEP_ARTICULO_ZUL = "/yhaguy/gestion/articulos/otrosDepositosArticulo.zul";
	public static String VER_IMAGEN_ARTICULO_ZUL = "/yhaguy/gestion/articulos/imagenArticulo.zul";
	public static String INSERTAR_ITEM_CONCILIACION_ZUL = "/yhaguy/gestion/bancos/insertarItem.zul";
	public static String CONCILIACION_POPUP_ZUL = "/yhaguy/gestion/bancos/PopupConciliacion.zul";

	public static String INFO_ADICIONAL_VENTA_PEDIDO_ZUL = "/yhaguy/gestion/venta/informacionAdicional.zul";
	public static String IMPORTAR_PRESUPUESTO_VENTA_ZUL = "/yhaguy/gestion/venta/importarPresupuesto.zul";
	public static String VENTA_ADD_FORMA_PAGO_ZUL = "/yhaguy/gestion/venta/VentaFormaPago.zul";
	public static String VENTA_ADD_FORMA_PAGO_ZUL_ = "/yhaguy/gestion/venta/VentaFormaPago_.zul";
	public static String VENTA_LISTA_FORMA_PAGO_ZUL = "/yhaguy/gestion/venta/VentaListaFormaPago.zul";
	public static String VENTA_LISTA_FORMA_PAGO_ZUL_ = "/yhaguy/gestion/venta/VentaListaFormaPago_.zul";
	public static String NC_FORMA_PAGO_ZUL = "/yhaguy/gestion/notacredito/NotaCreditoFormaPago.zul";
	public static String INFO_ADICIONAL_NOTA_CREDITO_ZUL = "/yhaguy/gestion/notacredito/InformacionAdicional.zul";
	public static String INFO_ADICIONAL_IMPORTACION_ZUL = "/yhaguy/gestion/compras/importacion/informacionAdicional.zul";
	public static String DEFINICIONES_CUENTAS_CONTABLES_ZUL = "/yhaguy/gestion/compras/definicionesCuentasContablesPopup.zul";
	public static String ARQUEO_CAJA_ZUL = "/yhaguy/gestion/caja/periodo/cajaArqueo.zul";
	public static String INSERTAR_ITEM_TRANSFERENCIA_ZUL = "/yhaguy/gestion/transferencia/insertarItem.zul";
	public static String TRANSFERENCIA_RECEPCION_ZUL = "/yhaguy/gestion/transferencia/recepcion.zul";
	public static String PRESTAMOS_CC_FORMA_PAGO_ZUL = "/yhaguy/gestion/bancos/prestamo_lista_forma_pago.zul";
	public static String PRESTAMOS_CC_ADD_FORMA_PAGO_ZUL_ = "/yhaguy/gestion/bancos/prestamo_forma_pago_.zul";

	public static String VENTA_ITEM_ZUL = "/yhaguy/gestion/venta/VentaItem.zul";
	public static String CREAR_BOLETA_DEVOLUCION = "/yhaguy/gestion/reparto/crearBoletaDevolucion.zul";
	public static String VER_DETALLES_BOLETA_ZUL = "/yhaguy/gestion/reparto/mostrarDetallesBoleta.zul";
	public static String CLIENTES_OCASIONALES_ZUL = "/yhaguy/gestion/venta/clienteOcasional.zul";

	public static String CTACTE_EMPRESA_DETALLES_MOVIMIENTO_ZUL = "/yhaguy/gestion/empresa/CtaCteEmpresaDetalleMovimiento.zul";
	public static String CTACTE_EMPRESA_MAS_INFORMACION_ZUL = "/yhaguy/gestion/empresa/CtaCteMasInformacion.zul";
	public static String CTACTE_EMPRESA_ASIGNAR_IMPUTACION_ZUL = "/yhaguy/gestion/empresa/CtaCteAsignarImputacion.zul";

	public static String EMPRESA_IMPRIMIR_ZUL = "/yhaguy/gestion/empresa/EmpresaImprimir.zul";
	public static String DEFINICION_BANCO_POPUP_ZUL = "/yhaguy/gestion/bancos/definicionesBancoPopup.zul";
	public static String DEFINICION_BANCO_POPUP_TIPO_ZUL = "/yhaguy/gestion/bancos/definicionesBancoPopupTipo.zul";
	public static String DEFINICION_BANCO_POPUP_TIPO_CUENTA_ZUL = "/yhaguy/gestion/bancos/definicionesBancoPopupTipoCuenta.zul";

	public static String FUNCIONARIO_POPUP_USUARIO_ZUL = "/yhaguy/gestion/empresa/FuncionarioPopupUsuario.zul";
	public static String FUNCIONARIO_POPUP_SUCURSALES_ZUL = "/yhaguy/gestion/empresa/FuncionarioPopupSucursales.zul";

	/************************************************ SIMBOLOS **************************************/

	public static final String SIMBOLO_DOLAR = "U$.";
	public static final String SIMBOLO_GS = "Gs.";
	public static final String SIMBOLO_PORCENTAJE = "%";
	public static final String SIMBOLO_GUION_MEDIO = "-";
	public static final String SIMBOLO_ASTERISCO = "(*)";
	public static final String FORMATO_DOLAR = "#,##0.00";
	public static final String FORMATO_GUARANIES = "#,##0";

	/************************************************ COLORES ***************************************/

	public static String COLOR_ROJO = "color:red";
	public static String BACKGROUND_NARANJA = "background:#f6d197";
	public static String BACKGROUND_AMARILLO = "background:#ffff99";
	public static String COLOR_HABER = "background:#F2DAB5";

	/******************************************** FORMATOS ******************************************/

	public static String FORMAT_MONEDA_LOCAL = "#,##0";
	public static String FORMAT_MONEDA_EXTRANJERA = "#,##0.00";

	/******************************************** TITULOS *******************************************/

	public static String TITULO_FORMULARIO_GASTOS = "Formulario Gastos";
	public static String TITULO_POPUP_TIMBRADO = "Asignación de Timbrados";

	/************************************************ ICONOS ****************************************/

	public static String ICONO_ACEPTAR_16X16 = "/core/images/accept_.png";
	public static String ICONO_ANULAR_16X16 = "/core/images/delete_.png";
	public static String ICONO_EXCLAMACION_16X16 = "/core/images/exclamation_yellow.png";
	public static String ICONO_LOCK_16X16 = "/core/images/lock_.png";
	public static String ICONO_UNLOCK_16X16 = "/core/images/lock_open.png";
	public static String ICONO_ADD_16X16 = "/core/images/add2.png";
	public static String ICONO_EDIT_16X16 = "/core/images/edit2.png";

	public static String IMAGEN_ARTICULO_DEFAULT = "/yhaguy/archivos/articulos/sin_imagen.png";

	/************************************
	 * ALIAS DE CUENTAS CONTABLES
	 *********************************/

	public static int CUENTA_DEBE_KEY = 1;
	public static int CUENTA_HABER_KEY = -1;
	public static String ALIAS_CUENTA_IMPORTACION_EN_CURSO = "CT-IM-CU";
	public static String ALIAS_CUENTA_IVA_10_CF = "CT-IV-10-CR-FI";
	public static String ALIAS_CUENTA_IVA_10_DF = "CT-IV-10-DE-FI";
	public static String ALIAS_CUENTA_IVA_5_CF = "CT-IV-5-CR-FI";
	public static String ALIAS_CUENTA_IVA_5_DF = "CT-IV-5-DE-FI";
	public static String ALIAS_CUENTA_PROVEEDORES_VARIOS = "CT-PR-VA";
	public static String ALIAS_CUENTA_MERCADERIAS_GRAVADAS = "A-MER-GRAVADAS";
	public static String ALIAS_CUENTA_CLIENTES_OCASIONALES = "CT-CL-OC";
	public static String ALIAS_CUENTA_CLIENTES_VARIOS = "CT-CL-VA";
	public static String ALIAS_CUENTA_DERECHO_ADUANERO = "CT-DE-AD";
	public static String ALIAS_CUENTA_TAZA_INDI = "CT-TA-IN";
	public static String ALIAS_CUENTA_VALORACION_ADUANERA = "CT-VA-AD";
	public static String ALIAS_CUENTA_IVA_DESPACHO = "CT-IV-DE-IM";
	public static String ALIAS_CUENTA_CANON_INFORMATICO = "CT-CA-IN";
	public static String ALIAS_CUENTA_GASTOS_IMPORTACION = "CT-GA-IM";

	public static String ALIAS_CUENTA_RETENCION_IVA = "CT-RETE-IVA";
	public static String ALIAS_CUENTA_IVA_COMPRAS = "CT-IVA-COMPRAS";
	public static String ALIAS_CUENTA_CAJA_GASTOS = "CT-CAJA-GASTOS";
	public static String ALIAS_CUENTA_CHEQUE_DIF_A_PAGAR = "CT-CH-DIF-A-PAGAR";
	public static String ALIAS_CUENTA_MERCADERIA_GRAVADAS = "CT-MERCADERIA-GRABADA";
	public static String ALIAS_CUENTA_IVA_VENTAS = "CT-IVA-VENTAS";
	public static String ALIAS_CUENTA_COBRO_CHEQUE_A_DEPOSITAR = "CT-COBRO-CH-A-DEPO";
	public static String ALIAS_CUENTA_COBRO_EFECTIVO = "CT-COBRO-EFEC";
	public static String ALIAS_CUENTA_COBRO_TARJETA_CREDITO = "CT-COBRO-TJ-CRE";
	public static String ALIAS_CUENTA_COBRO_TARJETA_DEBITO = "CT-COBRO-TJ-DEB";
	public static String ALIAS_CUENTA_COBRO_RETENCION = "CT-COBRO-RETENCION";
	public static String ALIAS_CUENTA_COBRO_DEPOSITO_BANCARIO = "CT-COBRO-DEP-BANCARIO";

	/******************************************
	 * SIGLAS DE TIPOS
	 ***************************************/

	public static final String SIGLA_CAJA_REPOSICION_INGRESO = "CAJ-INGRESO";
	public static final String SIGLA_CAJA_REPOSICION_EGRESO = "CAJ-EGRESO";
	public static final String SIGLA_CAJA_REPOSICION_EGRESO_SIN_COMP = "CAJ-EGRESO-SIN-COMPROBANTE";
	public static final String SIGLA_CAJA_REPOSICION_EGRESO_VALE = "CAJ-EGRESO-VALE";
	public static final String SIGLA_CAJA_REPOSICION_EGRESO_PRESTAMO = "CAJ-EGRESO-PRESTAMO";
	public static final String SIGLA_CAJA_REPOSICION_EGRESO_GRATIFICACION = "CAJ-EGRESO-GRATIFICACION";
	public static final String SIGLA_CAJA_ESTADO_ACTIVO = "CAJ-ACT";
	public static final String SIGLA_CAJA_ESTADO_INACTIVO = "CAJ-INACT";
	public static final String SIGLA_CAJA_PERIODO_ABIERTA = "CAJ-PER-ABI";
	public static final String SIGLA_CAJA_PERIODO_CERRADA = "CAJ-PER-CER";
	public static final String SIGLA_CAJA_PERIODO_PROCESADA = "CAJ-PER-PRO";
	public static final String SIGLA_CAJA_TIPO_MOVIMIENTOS_VARIOS = "CAJ-VAR";

	public static final String SIGLA_RECIBO_ESTADO_GUARDADO = "REC-EST-GUAR";
	public static final String SIGLA_RECIBO_ESTADO_ANULADO = "REC-EST-ANUL";
	public static final String SIGLA_FORMA_PAGO_CHEQUE_PROPIO = "OP-TIP-CH-PROP";
	public static final String SIGLA_FORMA_PAGO_CHEQUE_TERCERO = "OP-TIP-CH-TERC";
	public static final String SIGLA_FORMA_PAGO_EFECTIVO = "OP-TIP-EF";
	public static final String SIGLA_FORMA_PAGO_TARJETA_CREDITO = "OP-TIP-TCR";
	public static final String SIGLA_FORMA_PAGO_TARJETA_DEBITO = "OP-TIP-TDB";
	public static final String SIGLA_FORMA_PAGO_RETENCION = "FP-TIP-RET";
	public static final String SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO = "FP-TIP-DEP-BAN";
	public static final String SIGLA_FORMA_PAGO_CHEQUE_AUTOCOBRANZA = "FP-TIP-CH-AUT";
	public static final String SIGLA_FORMA_PAGO_DEBITO_COBRANZA_CENTRAL = "FP-TIP-DE-CO-CE";
	public static final String SIGLA_FORMA_PAGO_RECAUDACION_CENTRAL = "FP-TIP-RCC";
	public static final String SIGLA_FORMA_PAGO_TRANSFERENCIA_CENTRAL = "FP-TIP-TCC";
	public static final String SIGLA_FORMA_PAGO_SALDO_FAVOR_GENERADO = "FP-TIP-SFC";
	public static final String SIGLA_FORMA_PAGO_SALDO_FAVOR_COBRADO = "FP-TIP-SFC-COB";
	public static final String SIGLA_FORMA_PAGO_DEBITO_CTA_BANCARIA = "FP-TIP-DB-CT-BC";
	public static String SIGLA_MONEDA_GUARANI = "Gs.";
	public static String SIGLA_MONEDA_DOLAR = "U$D";
	public static String SIGLA_MONEDA_PESO = "$";
	public static String SIGLA_MONEDA_EURO = "€";
	public static String SIGLA_IVA_10 = "10";
	public static String SIGLA_IVA_5 = "5";
	public static String SIGLA_IVA_EXENTO = "0";
	public static String SIGLA_REGIMEN_TRIB_EXENTA = "REG-EXENTA";
	public static String SIGLA_REGIMEN_TRIB_NO_EXENTA = "REG-NO-EXENTA";
	public static String SIGLA_TIPO_PERSONA_JURIDICA = "PJ";
	public static String SIGLA_TIPO_PERSONA_FISICA = "PF";
	public static String SIGLA_PAIS_PARAGUAY = "PRY";
	public static String SIGLA_PAIS_NO_DEFINIDO = "SIN-PAIS";

	public static String SIGLA_TIPO_VENTA_MOSTRADOR = "VTA-MOS";
	public static String SIGLA_TIPO_VENTA_VENDEDOR = "VTA-VEN";
	public static String SIGLA_TIPO_VENTA_TERMINAL = "VTA-TER";
	public static String SIGLA_TIPO_VENTA_ENCOMIENDA = "VTA-ENC";
	public static String SIGLA_TIPO_VENTA_COLECTIVO = "VTA-COL";
	public static String SIGLA_TIPO_VENTA_REPARTO = "VTA-REP";

	public static String SIGLA_ESTADO_CLIENTE = "EST-CLI";
	public static String SIGLA_CATEGORIA_CLIENTE = "CAT-CLI";
	public static String SIGLA_CLIENTE_TIPO_NO_DEFINIDO = "CLI-TIP-NO-DEF";
	public static String SIGLA_CLIENTE_TIPO_MINORISTA = "CLI-TIP-MIN";
	public static String SIGLA_CLIENTE_TIPO_MAYORISTA = "CLI-TIP-MAY";
	public static String SIGLA_CLIENTE_TIPO_DISTRIBUIDOR = "CLI-TIP-DIST";
	public static String SIGLA_CLIENTE_TIPO_OCASIONAL = "CLI-TIP-OCAS";
	public static String SIGLA_PROFESIONES = "PROFE";
	public static String SIGLA_TIPO_CONTACTO_INTERNO = "TIPO-CONT-INT";
	public static String SIGLA_TIPO_OPERACION = "TIPO-OPE";
	public static String SIGLA_CTA_CTE_ESTADO = "CTA-CTE-EST";
	public static String SIGLA_PROVEEDOR_TIPO_LOCAL = "PROV-TIPO-LOCAL";
	public static String SIGLA_PROVEEDOR_TIPO_EXTERIOR = "PROV-TIPO-EXT";
	public static String SIGLA_PROVEEDOR_ESTADO_ACTIVO = "PROV-EST-ACT";
	public static String SIGLA_PROVEEDOR_ESTADO_INACT = "PROV-EST-INAC";
	public static final String SIGLA_ARTICULO_ESTADO_ACTIVO = "ART-EST-ACT";
	public static final String SIGLA_ARTICULO_ESTADO_INACTIVO = "ART-EST-INAC";
	public static final String SIGLA_ARTICULO_ESTADO_TEMPORAL = "ART-EST-TMP";
	public static String SIGLA_ARTICULO_LINEA = "ART-LIN";
	public static String SIGLA_ARTICULO_LINEA_DEFAULT = "ART-LIN-DEF";
	public static String SIGLA_ARTICULO_LINEA_NAVIERA = "ART-LIN-NAV";
	public static String SIGLA_ARTICULO_LINEA_AGRICOLA = "ART-LIN-AGR";
	public static String SIGLA_ARTICULO_LINEA_PESADA = "ART-LIN-PES";
	public static String SIGLA_ARTICULO_LINEA_LIVIANA = "ART-LIN-LIV";
	public static String SIGLA_ARTICULO_MARCA = "ART-MAR";
	public static String SIGLA_ARTICULO_MARCA_KHAV = "ART-MAR-KAV";
	public static String SIGLA_ARTICULO_MARCA_DEFAULT = "ART-MAR-DEF";
	public static String SIGLA_ARTICULO_MARCA_URBA = "ART-MAR-URB";
	public static String SIGLA_ARTICULO_MARCA_BROSOL = "ART-MAR-BRO";
	public static String SIGLA_ARTICULO_FAMILIA = "ART-FAM";
	public static String SIGLA_ARTICULO_FAMILIA_DEFAULT = "ART-FAM-DEF";
	public static String SIGLA_ARTICULO_FAMILIA_FILTROS = "ART-FAM-FIL";
	public static String SIGLA_ARTICULO_FAMILIA_LUBRICANTES = "ART-FAM-LUB";
	public static String SIGLA_ARTICULO_FAMILIA_BATERIAS = "ART-FAM-BAT";
	public static String SIGLA_ARTICULO_FAMILIA_CUBIERTAS = "ART-FAM-CUB";
	public static String SIGLA_ARTICULO_FAMILIA_REPUESTOS = "ART-FAM-REP";
	public static String SIGLA_ARTICULO_ESTADO = "ART-EST";
	public static String SIGLA_ARTICULO_PARTE = "ART-PAR";
	public static String SIGLA_ARTICULO_PARTE_DEFAULT = "ART-PAR-DEF";
	public static String SIGLA_ARTICULO_PARTE_CAJA = "ART-PAR-CAJ";
	public static String SIGLA_ARTICULO_PARTE_SUSPENSION = "ART-PAR-SUS";
	public static String SIGLA_ARTICULO_PARTE_MOTOR = "ART-PAR-MOT";
	public static String SIGLA_ARTICULO_PARTE_ACCESORIO = "ART-PAR-ACC";
	public static String SIGLA_ARTICULO_PARTE_FRENOS = "ART-PAR-FRE";
	public static String SIGLA_ARTICULO_UNID_MED = "ART-UM";
	public static String SIGLA_ARTICULO_UNID_MED_UND = "ART-UM-UND";
	public static String SIGLA_ARTICULO_UNID_MED_MTS = "ART-UM-MTS";
	public static String SIGLA_ARTICULO_UNID_MED_LTS = "ART-UM-LTS";
	public static String SIGLA_IMPORTACION_ESTADO_CONFIRMADO = "IMP-EST-CONF";
	public static String SIGLA_IMPORTACION_ESTADO_SOLICITANDO_COTIZACION = "IMP-EST-SOL";
	public static String SIGLA_IMPORTACION_ESTADO_PROFORMA_RECIBIDA = "IMP-EST-PRO";
	public static String SIGLA_IMPORTACION_ESTADO_PENDIENTE_DE_ENVIO = "IMP-EST-PEN";
	public static String SIGLA_IMPORTACION_ESTADO_PEDIDO_ENVIADO = "IMP-EST-ENV";
	public static String SIGLA_IMPORTACION_ESTADO_CERRADO = "IMP-EST-CERR";
	public static String SIGLA_IMPORTACION_ESTADO_ANULADO = "IMP-EST-ANUL";
	public static String SIGLA_VENTA_ESTADO_SOLO_PRESUPUESTO = "VTA-SOLO-PRES";
	public static String SIGLA_VENTA_ESTADO_PASADO_A_PEDIDO = "VTA-PAS-PED";
	public static String SIGLA_VENTA_ESTADO_SOLO_PEDIDO = "VTA-SOLO-PED";
	public static String SIGLA_VENTA_ESTADO_CERRADO = "VTA-PED-CERR";
	public static String SIGLA_VENTA_ESTADO_FACTURADO = "VTA-PED-FACT";
	public static String SIGLA_IMPORTACION_TIPO_1 = "IMP-FOB";
	public static String SIGLA_IMPORTACION_TIPO_2 = "IMP-CIF";
	public static String SIGLA_IMPORTACION_TIPO_3 = "IMP-C&F";
	public static String SIGLA_RUBRO_EMPRESA_FUNCIONARIO = "RUB-EMP-FUN";
	public static String SIGLA_RUBRO_EMPRESA_CONSUMIDOR_FINAL = "RUB-EMP-CON-FIN";
	public static String SIGLA_RUBRO_EMPRESA_MAYORISTA_BATERIAS = "RUB-EMP-MAY-BAT";
	public static String SIGLA_RUBRO_EMPRESA_MAYORISTA_CUBIERTAS = "RUB-EMP-MAY-CUB";
	public static String SIGLA_RUBRO_EMPRESA_MAYORISTA_LUBRICANTES = "RUB-EMP-MAY-LUB";
	public static String SIGLA_RUBRO_EMPRESA_MAYORISTA_REPUESTOS = "RUB-EMP-MAY-REP";
	public static String SIGLA_RUBRO_EMPRESA_DISTRIBUIDOR_BATERIAS = "RUB-EMP-DIST-BAT";
	public static String SIGLA_RUBRO_EMPRESA_DISTRIBUIDOR_CUBIERTAS = "RUB-EMP-DIST-CUB";
	public static String SIGLA_RUBRO_EMPRESA_DISTRIBUIDOR_LUBRICANTES = "RUB-EMP-DIST-LUB";
	public static String SIGLA_RUBRO_EMPRESA_DISTRIBUIDOR_REPUESTOS = "RUB-EMP-DIST-REP";
	public static String SIGLA_RUBRO_EMPRESA_CASA_REPUESTOS = "RUB-EMP-CAS-REP";
	public static String SIGLA_TIPO_COMPRA_GASTO_1 = "COMP-GAST-FL";
	public static String SIGLA_TIPO_COMPRA_GASTO_2 = "COMP-GAST-SEG";
	public static String SIGLA_TIPO_COMPRA_GASTO_3 = "COMP-GAST-OTR";
	public static String SIGLA_TIPO_COMPRA_PRORRATEO_1 = "COMP-PROR-FLE";
	public static String SIGLA_TIPO_COMPRA_PRORRATEO_2 = "COMP-PROR-SEG";
	public static String SIGLA_TIPO_REPARTO_YHAGUY = "REP-YHA";
	public static String SIGLA_TIPO_REPARTO_TERMINAL = "REP-TER";
	public static String SIGLA_TIPO_REPARTO_COLECTIVO = "REP-COL";
	public static String SIGLA_TIPO_REPARTO_ENCOMIENDA = "REP-ENC";
	public static String SIGLA_TIPO_COMPROBANTE_LEGAL = "CBT-LEG";
	public static String SIGLA_TIPO_COMPROBANTE_INTERNO = "CBT-INT";
	public static String SIGLA_TIPO_OPERACION_COMPRA = "OPE-COM";
	public static String SIGLA_TIPO_OPERACION_GASTO = "OPE-GTO";
	public static String SIGLA_TIPO_OPERACION_PAGO = "OPE-PAG";
	public static String SIGLA_TIPO_OPERACION_VENTA = "OPE-VEN";
	public static String SIGLA_TIPO_OPERACION_COBRO = "OPE-COB";
	public static String SIGLA_TIPO_OPERACION_REMISION = "OPE-REM";
	public static String SIGLA_TIPO_OPERACION_BANCARIA = "OPE-BAN";
	public static String SIGLA_TIPO_OPERACION_DEVOLUCION = "OPE-DEV";
	public static String SIGLA_TIPO_OPERACION_AJUSTE = "OPE-AJU";
	public static String SIGLA_TIPO_EMPRESA_CLIENTE = "EMP-CLI";
	public static String SIGLA_TIPO_EMPRESA_PROVEEDOR = "EMP-PRV";
	public static String SIGLA_TIPO_EMPRESA_FUNCIONARIO = "EMP-FUN";
	public static String SIGLA_TIPO_EMPRESA_YHAGUY = "EMP-YHA";
	public static String SIGLA_TIPO_EMPRESA_BANCO = "EMP-BAN";
	public static String SIGLA_TIPO_CARGO_JEFE_MOSTRADOR = "FUN-CRG-JEF-VTA-MOS";
	public static String SIGLA_TIPO_CARGO_VENTAS_MOSTRADOR = "FUN-CRG-VTA-MOS";
	public static String SIGLA_TIPO_CARGO_VENTAS_EXTERNAS = "FUN-CRG-VTA-EXT";
	public static String SIGLA_TIPO_CARGO_SOPORTE_TECNICO = "FUN-CRG-SOP-TEC";
	public static String SIGLA_TIPO_CARGO_GERENTE_RRHH = "FUN-CRG-GTE-RH";
	public static String SIGLA_TIPO_CARGO_GERENTE_ADMINISTRATIVO = "FUN-CRG-GTE-ADM";
	public static String SIGLA_TIPO_CARGO_AUXILIAR_ADMINISTRATIVO = "FUN-CRG-AUX-ADM";
	public static String SIGLA_TIPO_CARGO_ENCARGADO_COMPRAS = "FUN-CRG-ENC-COM";
	public static String SIGLA_TIPO_CARGO_ENCARGADO_DEPOSITO = "FUN-CRG-ENC-DEP";
	public static String SIGLA_TIPO_FUNCIONARIO_ESTADO_ACTIVO = "FUN-EST-ACT";
	public static String SIGLA_TIPO_FUNCIONARIO_ESTADO_INACTIVO = "FUN-EST-INA";
	public static String SIGLA_TIPO_NC_MOTIVO_DESCUENTO = "NCR-MOT-DES";
	public static String SIGLA_TIPO_NC_MOTIVO_DEVOLUCION = "NCR-MOT-DEV";
	public static String SIGLA_TIPO_NC_MOTIVO_DIF_PRECIO = "NCR-MOT-DIF-PRE";
	public static String SIGLA_TIPO_NC_MOTIVO_RECLAMO = "NCR-MOT-REC";
	public static String SIGLA_TIPO_NC_DETALLE_FACTURA = "NCR-DET-FAC";
	public static String SIGLA_TIPO_NC_DETALLE_ARTICULO = "NCR-DET-ART";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_TSB = "BCO-TER-TSB";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_INTEGRACION = "BCO-TER-INTE";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_CONTI = "BCO-TER-CONTI";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_ATLAS = "BCO-TER-ATLAS";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_REGIONAL = "BCO-TER-REGIO";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_SUDAMERIS = "BCO-TER-SUDA";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_ABN = "BCO-TER-ABN";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_HSBC = "BCO-TER-HSBC";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_BBVA = "BCO-TER-BBVA";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_ITAU = "BCO-TER-ITAU";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_AMAMBAY = "BCO-TER-AMAM";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_CITI = "BCO-TER-CITI";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_BANCOP = "BCO-TER-BCOP";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_GNB = "BCO-TER-GNB";
	public static String SIGLA_TIPO_BANCOS_TERCEROS_DOBRASIL = "BCO-TER-DBRA";

	public static String SIGLA_TIPO_CTA_CONTABLE_ACTIVO = "CTA-CBL-ACT";
	public static String SIGLA_TIPO_CTA_CONTABLE_PASIVO = "CTA-CBL-PAS";
	public static String SIGLA_TIPO_CTA_CONTABLE_PATRIMONIO = "CTA-CBL-PAT-NET";
	public static String SIGLA_TIPO_CTA_CONTABLE_INGRESO = "CTA-CBL-ING";
	public static String SIGLA_TIPO_CTA_CONTABLE_EGRESO = "CTA-CBL-EGR";
	
	public static final String SIGLA_TIPO_RUBRO_EMP_ASEGURADORA = "RUB-EMP-ASE";

	public static String SIGLA_ESTADO_COMPROBANTE_PENDIENTE = "EST-CBT-PEN";
	public static String SIGLA_ESTADO_COMPROBANTE_APROBADO = "EST-CBT-APR";
	public static String SIGLA_ESTADO_COMPROBANTE_CERRADO = "EST-CBT-CER";
	public static String SIGLA_ESTADO_COMPROBANTE_ANULADO = "EST-CBT-ANU";
	public static String SIGLA_ESTADO_COMPROBANTE_CONFECCIONADO = "EST-CBT-CON";

	public static String SIGLA_PROCESADORA_TC_PROCARD = "TAR-PRC-PRO";
	public static String SIGLA_PROCESADORA_TC_BANCARD = "TAR-PRC-BAN";
	public static String SIGLA_PROCESADORA_TC_PANAL = "TAR-PRC-PAN";
	public static String SIGLA_PROCESADORA_TC_CABAL = "TAR-PRC-CAB";

	public static String SIGLA_RESERVA_INTERNA = "RES-INT";
	public static String SIGLA_RESERVA_REPARTO = "RES-REP";
	public static String SIGLA_RESERVA_VENTA = "RES-VTA";
	public static String SIGLA_RESERVA_DEVOLUCION = "RES-DEV";

	public static String SIGLA_ESTADO_RESERVA_ACTIVA = "EST-RES-ACT";
	public static String SIGLA_ESTADO_RESERVA_CANCELADA = "EST-RES-CAN";
	public static String SIGLA_ESTADO_RESERVA_FINALIZADA = "EST-RES-FIN";

	public static String SIGLA_ESTADO_TRANSF_ELABORACION = "TRF-ELAB";
	public static String SIGLA_ESTADO_TRANSF_PENDIENTE = "TRF-PEND";
	public static String SIGLA_ESTADO_TRANSF_CONFIRMADA = "TRF-CONF";
	public static String SIGLA_ESTADO_TRANSF_RECIBIDA = "TRF-RECI";

	public static String SIGLA_ESTADO_REP_PREPARACION = "REP-ENPRE";
	public static String SIGLA_ESTADO_REP_TRANSITO = "REP-TRAN";
	public static String SIGLA_ESTADO_REP_ENTREGADO = "REP-ENTR";

	public static String SIGLA_SUCURSAL_APP_TODAS = "SUC-APP-TO";

	public static String SIGLA_TIPO_CAMBIO_APP = "YG";
	public static String SIGLA_TIPO_CAMBIO_BCP = "BCP";

	public static String SIGLA_TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_DET_TARJ = "BAN-TAR-DET-TARJ";
	public static String TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_DET_TARJ = "Banco Tarjeta Extracto";
	public static String SIGLA_TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_BANCO_MOVIMIENTO = "BAN-TAR-DET-BANM";
	public static String TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_BANCO_MOVIMIENTO = "Acreditacion - Banco Movimiento";
	
	public static final String SIGLA_IMPORT_EN_CURSO = "IMP.CURSO";

	/***********************************
	 * SIGLAS DE TIPOS DE MOVIMIENTO
	 ********************************/

	public static String SIGLA_TM_TRANSF_INTERNA = "TRF-INT";
	public static String SIGLA_TM_TRANSF_INTERNA_CANCEL = "TRF-INT-CAN";
	public static String SIGLA_TM_TRANSF_EXTERNA = "TRF-EXT";
	public static String SIGLA_TM_TRANSF_REMISION_CANCEL = "TRF-REM-CAN";
	public static String SIGLA_TM_INVENTARIO = "INV";

	public static String SIGLA_TM_COBRO = "TM-COB";
	public static String SIGLA_TM_PAGO = "TM-PAG";

	// Siglas de movimientos de Compra
	public final static String SIGLA_TM_FAC_COMPRA_CONTADO = "MOV-COM-FAC-CON";
	public final static String SIGLA_TM_FAC_COMPRA_CREDITO = "MOV-COM-FAC-CRE";
	public final static String SIGLA_TM_FAC_IMPORT_CONTADO = "MOV-IMP-FAC-CON";
	public final static String SIGLA_TM_FAC_IMPORT_CREDITO = "MOV-IMP-FAC-CRE";
	public final static String SIGLA_TM_NOTA_CREDITO_COMPRA = "MOV-COM-FAC-NCR";
	public final static String SIGLA_TM_SOLICITUD_NC_COMPRA = "MOV-COM-SOL-NCR";
	public final static String SIGLA_TM_NOTA_DEBITO_COMPRA = "MOV-COM-FAC-NDB";
	public final static String SIGLA_TM_ORDEN_COMPRA = "MOV-COM-CBI-OCL";
	public final static String SIGLA_TM_ORDEN_COMPRA_IMPOR = "MOV-COM-CBI-OCI";

	// Siglas de movimientos de Gasto
	public final static String SIGLA_TM_FAC_GASTO_CONTADO = "MOV-GTO-FAC-CON";
	public final static String SIGLA_TM_FAC_GASTO_CREDITO = "MOV-GTO-FAC-CRE";
	public final static String SIGLA_TM_AUTO_FACTURA = "MOV-GTO-FAC-AUT";
	public final static String SIGLA_TM_BOLETA_VENTA = "MOV-GTO-BOL-VTA";
	public final static String SIGLA_TM_ORDEN_GASTO = "MOV-GTO-CBI-OGT";
	public final static String SIGLA_TM_OTROS_COMPROBANTES = "MOV-GTO-OTR-CBT";

	// Siglas de movimientos de Pago
	public final static String SIGLA_TM_RECIBO_PAGO = "MOV-PAG-REC";
	public final static String SIGLA_TM_PAGARE = "MOV-PAG-PGR";
	public final static String SIGLA_TM_RETENCION = "MOV-PAG-RET";
	public final static String SIGLA_TM_ANTICIPO_PAGO = "MOV-PAG-ANT";
	public final static String SIGLA_TM_CHEQUE_PAGO = "MOV-PAG-CHQ";
	public final static String SIGLA_TM_PRESTAMO_CASA_CENTRAL = "MOV-PRT-CCT";

	// Siglas de movimientos de Venta
	public final static String SIGLA_TM_FAC_VENTA_CONTADO = "MOV-VTA-FAC-CON";
	public final static String SIGLA_TM_FAC_VENTA_CREDITO = "MOV-VTA-FAC-CRE";
	public final static String SIGLA_TM_NOTA_CREDITO_VENTA = "MOV-VTA-FAC-NCR";
	public final static String SIGLA_TM_NOTA_DEBITO_VENTA = "MOV-VTA-FAC-NDB";
	public final static String SIGLA_TM_PRESUPUESTO_VENTA = "MOV-VTA-CBI-PRE";
	public final static String SIGLA_TM_SOLICITUD_NC_VENTA = "MOV-VTA-SOL-NCR";
	public final static String SIGLA_TM_PEDIDO_VENTA = "MOV-VTA-CBI-PED";

	// Siglas de movimientos de Cobro
	public final static String SIGLA_TM_RECIBO_COBRO = "MOV-CBR-REC";
	public final static String SIGLA_TM_ANTICIPO_COBRO = "MOV-CBR-ANT";
	public final static String SIGLA_TM_CANCELACION_CHEQ_RECHAZADO = "MOV-CBR-CCR";
	public final static String SIGLA_TM_REEMBOLSO_PRESTAMO = "MOV-CBR-RPCC";

	// Siglas de movimientos de Remision
	public final static String SIGLA_TM_NOTA_REMISION = "MOV-TRF-REM";
	public final static String SIGLA_TM_TRANS_MERCADERIA = "MOV-TRF-INT";

	// Siglas de movimientos de Ajuste Stock
	public final static String SIGLA_TM_AJUSTE_POSITIVO = "MOV-AJU-POS";
	public final static String SIGLA_TM_AJUSTE_NEGATIVO = "MOV-AJU-NEG";
	public final static String SIGLA_TM_INVENTARIO_MERCADERIAS = "MOV-INV";
	
	// Siglas de movimientos de Ajuste en Cta. Cte.
	public final static String SIGLA_TM_CTA_CTE_SALDO_FAVOR = "MOV-CTA-CTE-POS";

	// Siglas de movimientos Bancarios
	public final static String SIGLA_TM_EMISION_CHEQUE = "MOV-BAN-CHP-DEB";
	public final static String SIGLA_TM_DEPOSITO_BANCARIO = "MOV-BAN-DEF-HAB";
	public final static String SIGLA_TM_SALDO_INICIAL_BANCO = "MOV-BAN-SA-IN";
	public final static String SIGLA_TM_DEBITO_BANCARIO = "MOV-BAN-OTR-DEB";
	public final static String SIGLA_TM_CHEQUE_RECHAZADO = "MOV-BAN-CHT-REC";
	public final static String SIGLA_TM_PRESTAMO_BANCARIO = "MOV-BAN-PRE";

	// Siglas de tipos de documentos
	public static String SIGLA_DOC_FAC_CONTADO = "DOC-FAC-CON";
	public static String SIGLA_DOC_FAC_CREDITO = "DOC-FAC-CRE";
	public static String SIGLA_DOC_NOTA_CREDITO = "DOC-NOT-CRE";
	public static String SIGLA_DOC_NOTA_DEBITO = "DOC-NOT-DEB";
	public static String SIGLA_DOC_REC_DINERO = "DOC-REC-DIN";
	public static String SIGLA_DOC_AUTOFAC = "DOC-AUT-FAC";
	public static String SIGLA_DOC_PAGARE = "DOC-PAGARE";
	public static String SIGLA_DOC_NOT_REMISION = "DOC-NOTA-REMISION";
	public static String SIGLA_DOC_OTROS = "DOC-OTROS";

	/********************** SIGLAS DE CTACTEEMPRESA *************************/

	public static String SIGLA_CTA_CTE_EMPRESA_IMPUTACION_PARCIAL = "CC-IM-PAR";
	public static String SIGLA_CTA_CTE_EMPRESA_IMPUTACION_COMPLETA = "CC-IM-COM";
	public static String SIGLA_CTA_CTE_EMPRESA_ESTADO_ACTIVO = "CC-EMP-ACT";
	public static String SIGLA_CTA_CTE_EMPRESA_ESTADO_INACTIVO = "CC-EMP-INAC";
	public static String SIGLA_CTA_CTE_EMPRESA_ESTADO_BLOQUEADO = "CC-EMP-BLOQ";
	public static String SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA = "CC-EM-SC";
	public static final String SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE = "CC-EMP-CARMOV-CLI";
	public static final String SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR = "CC-EMP-CARMOV-PROV";
	public static String SIGLA_CTA_CTE_EMPRESA_SELECCION_MOV_TODOS = "CC-EMP-TOD";
	public static String SIGLA_CTA_CTE_EMPRESA_SELECCION_MOV_PENDIENTES = "CC-EMP-PEND";
	public static String SIGLA_CTA_CTE_EMPRESA_SELECCION_MOV_VENCIDOS = "CC-EMP-VENC";

	/*******************
	 * Propiedades de los usuario
	 ********************************************/

	public static String USUARIO_LIMITE_COMPRA = "UsuarioLimiteCompra";
	public static String USUARIO_SOBRE_CREDITO = "UsuarioSobreCredito";
	public static String USUARIO_DEPOSITOS_HAB_ENTRADA = "UsuarioDepositosHabEntrada";
	public static String USUARIO_DEPOSITOS_HAB_SALIDA = "UsuarioDepositosHabSalida";
	public static String USUARIO_DEPOSITO_HAB_FACTURAR = "UsuarioDepositoHabFacturar";
	public static String USUARIO_MODO_VENTA = "UsuarioModoVenta";
	public static String USUARIO_DESARROLLADOR = "UsuarioDesarrollador";

	/*******************
	 * Descripciones destinos predefinidos para las alertas
	 *****************/

	public static String DESTINOS_TRF_INTERNA = "Destinos transferencia interna";
	public static String DESTINOS_TRF_REMISION = "Destinos transferencia remision";

	/*******************
	 * SIGLAS tipos Estados de Conciliacion
	 *********************************/

	public static String SIGLA_ESTADO_CONCILIACION_CONCILIADO = "EST-CON-CON";
	public static String SIGLA_ESTADO_CONCILIACION_PENDIENTE = "EST-CON-PEN";
	public static String SIGLA_ESTADO_CONCILIACION_DIFERENCIA = "EST-CON-DIF";

	public static int KEY_COD_CTRL_I = 73;

	/******************* Regla de Precio *********************************/

	public static String REGLA_PRECIO = "Regla Precio";
	public static String REGLA_PRECIO_CLIENTE = "Regla Cliente";
	public static String REGLA_PRECIO_ARTICULO = "Regla Articulo";
	public static String REGLA_PRECIO_VENTA = "Regla Venta";
	public static String REGLA_PRECIO_VENDEDOR = "Regla Vendedor";

	// public static String REGLA_PRECIO_SUCURSAL = "Regla Sucursal";
	// los de articulos ya se encuentran en esta clase
	public static String ID_TIPO_ESTADO_REGLA_PRECIO_VOLUMEN = "Estado de las reglas";
	public static String SIGLA_TIPO_REGLA_PRECIO_MAYOR = "ESTA-MAYOR";
	public static String SIGLA_TIPO_REGLA_PRECIO_MENOR = "ESTA-MENOR";
	public static String SIGLA_TIPO_REGLA_PRECIO_IGUAL = "ESTA-IGUAL";
	public static String SIGLA_TIPO_REGLA_PRECIO_NINGUNO = "ESTA-NING";
	public static String SIGLA_TIPO_REGLA_PRECIO_DIFERENTE = "ESTA-DIFER";

	public static String ID_TIPO_SUCURSAL = "Sucursal";
	public static String ID_TIPO_VENDEDOR_RUBRO = "Vendedor Rubro";
	public static String ID_TIPO_VENTA_MODO = "Modo Venta";

	public static String ID_TIPO_TARJETA = "Tarjeta";
	public static String SIGLA_TIPO_TARJETA_VISA = "Visa";
	public static String SIGLA_TIPO_TARJETA_MC = "Mastercard";
	public static String SIGLA_TIPO_TARJETA_AX = "American Express";

	/***********************************
	 * ESTADOS DE DOCUMENTOS
	 ********************************/

	public static char DOCUMENTO_ESTADO_PENDIENTE = 'P'; // Pendiente de
															// contabilizar
	public static char DOCUMENTO_ESTADO_ANULADO = 'A'; // Anulado
	public static char DOCUMENTO_ESTADO_CONTABILIZADO = 'C'; // Asiento Generado

	/***********************************
	 * SIGLAS TIPO DE DEVOLUCION
	 ********************************/

	public static String SIGLA_MOTIVO_DEVOLUCION_DIA_SIGUIENTE = "MOT-DEV-SIG";
	public static String SIGLA_MOTIVO_DEVOLUCION_ENTREGA_PARCIAL = "MOT-DEV-PAR";
	public static String SIGLA_MOTIVO_DEVOLUCION_DEFECTUOSO = "MOT-DEV-DEF";

	// Grupo de Usuarios para Grupo Elite
	public static String GRUPO_GRUPO_ELITE = "Grupo Elite";

	/***********************************
	 * BOLETA DE DOPOSITO
	 ***************************************/

	public static String TIPO_BANCO_DEPOSITO_EFECTIVO = "Efectivo";
	public static String TIPO_BANCO_DEPOSITO_CHEQUES_BANCO = "Cheques del banco";
	public static String TIPO_BANCO_DEPOSITO_CHEQUES_OTRO_BANCO = "Cheques de otros bancos";
	public static String TIPO_BANCO_DEPOSITO_TODOS = "Todos";

	public static String SIGLA_BANCO_DEPOSITO_EFECTIVO = "BAN-DEP-EFE";
	public static String SIGLA_BANCO_DEPOSITO_CHEQUES_BANCO = "BAN-DEP-CPRO";
	public static String SIGLA_BANCO_DEPOSITO_CHEQUES_OTRO_BANCO = "BAN-DEP-COTR";
	public static String SIGLA_BANCO_DEPOSITO_TODOS = "BAN-DEP-TOD";

	// constante de SubDiario
	public static String SUBDIARIO_CARGA_MANUAL = "Manual";

	// tipo modo de creacion cheque
	public static String ID_TIPO_MODO_DE_CREACION_CHEQUE = "Modo de creación";

	public static String SIGLA_TIPO_CHEQUE_AUTOMATICO = "CH-AUT";
	public static String SIGLA_TIPO_CHEQUE_MANUAL = "CH-MAN";

	/***********************
	 * PROPIEDADES TIPO
	 ***********************/
	public static String SIGLA_TIPO_USUARIO_DESARROLLADOR = "USU-DES";
	public static String SIGLA_TIPO_MODO_VENTA_MOSTRADOR = "MOD-VTA-MOS";
	public static String SIGLA_TIPO_MODO_VENTA_EXTERNA = "MOD-VTA-EXT";
	public static String SIGLA_TIPO_MODO_VENTA_TELEFONICA = "MOD-VTA-TEL";
	
	/************************ AGENDA ***********************/
	public static String NUEVO_ITEM = "Se creó un nuevo ítem de ";
	public static String ELIMINAR_ITEM = "Se eliminó el ítem de ";
	public static String EDITAR_ITEM = "Se modificó el ítem de ";
	
	public static long ID_EMPRESA_YHAGUY_CENTRAL = 7221;
	public static long ID_EMPRESA_YHAGUY_1_MRA = 7223;
	public static long ID_EMPRESA_YHAGUY_2_LUBER = 7196;
	public static long ID_EMPRESA_YHAGUY_3_PJC = 7194;
	public static long ID_EMPRESA_YHAGUY_4_BATERIAS = 6920;
	public static long ID_EMPRESA_YHAGUY_5_FLORENTIN = 4665;
	public static long ID_EMPRESA_YHAGUY_6_GRAN_ALMACEN = 7225;
	public static long ID_EMPRESA_YHAGUY_7_ACCESO_SUR = 7195;
	
	public static long ID_CLIENTE_YHAGUY_CENTRAL = 6919;
	
	// Eventos
	public static final String EVENTO_NUEVO_PEDIDO = "nuevoPedido";
	public static final String ON_NUEVO_PEDIDO = "onNuevoPedido";

}
