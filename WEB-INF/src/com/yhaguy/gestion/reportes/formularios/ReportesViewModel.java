package com.yhaguy.gestion.reportes.formularios;

import java.util.Date;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;

import net.sf.jasperreports.engine.JRDataSource;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ReportesViewModel {
	
	public static final String ZUL_REPORTES = "/yhaguy/gestion/reportes/formularios.zul";
	public static final String ZUL_REPORTES_ = "/yhaguy/gestion/reportes/formularios_.zul";
	public static final String ZUL_REPORTES_NC = "/yhaguy/gestion/reportes/formularios_nc.zul";
	public static final String ZUL_REPORTES_CH = "/yhaguy/gestion/reportes/formularios_ch.zul";
	
	public static final String SOURCE_REPOSICION = "/reportes/jasper/CajaReposicion_.jasper";
	public static final String SOURCE_RECIBO = "/reportes/jasper/Recibo.jasper";
	public static final String SOURCE_RECIBO_COBRO = "/reportes/jasper/ReciboCobro.jasper";
	public static final String SOURCE_RETENCION = "/reportes/jasper/Retencion.jasper";
	public static final String SOURCE_VENTA = "/reportes/jasper/FacturaVenta.jasper";
	public static final String SOURCE_VENTA_ = "/reportes/jasper/FacturaVenta_.jasper";
	public static final String SOURCE_VENTA_BATERIAS = "/reportes/jasper/FacturaVentaBaterias.jasper";
	public static final String SOURCE_NOTA_CREDITO = "/reportes/jasper/NotaCredito.jasper";
	public static final String SOURCE_NOTA_CREDITO_ = "/reportes/jasper/NotaCredito_.jasper";
	public static final String SOURCE_NOTA_CREDITO_BATERIAS = "/reportes/jasper/NotaCreditoBaterias.jasper";
	public static final String SOURCE_RESUMEN = "/reportes/jasper/ResumenCajaPlanilla.jasper";
	public static final String SOURCE_RESUMEN_ = "/reportes/jasper/ResumenCajaPlanilla_.jasper";
	public static final String SOURCE_SALDO_DET = "/reportes/jasper/CtaCteSaldosDetallado.jasper";
	public static final String SOURCE_SALDO_DET_DHS = "/reportes/jasper/CtaCteSaldosDetalladoDHS.jasper";
	public static final String SOURCE_SALDO_DET_DHS_ = "/reportes/jasper/CtaCteSaldosDetalladoDHS_.jasper";
	public static final String SOURCE_SALDO_CONSOLIDADO_DHS = "/reportes/jasper/CtaCteSaldosConsolidadoDHS.jasper";
	public static final String SOURCE_SALDO_CONSOLIDADO = "/reportes/jasper/CtaCteSaldosConsolidado.jasper";
	public static final String SOURCE_SALDO_CLIENTES_RESUMIDO = "/reportes/jasper/CtaCteSaldosResumido.jasper";
	public static final String SOURCE_SALDO_CLIENTES_RESUMIDO_ = "/reportes/jasper/CtaCteSaldosResumido_.jasper";
	public static final String SOURCE_CTA_CTE_SALDOS_DESGLOSADO = "/reportes/jasper/CtaCteSaldosDesglosado.jasper";
	public static final String SOURCE_COBROS_DET = "/reportes/jasper/CobrosDetallado.jasper";
	public static final String SOURCE_CHEQUE_AL_DIA = "/reportes/jasper/test.jasper";
	public static final String SOURCE_CHEQUE_DIFERIDO = "/reportes/jasper/ChequeDiferido.jasper";
	public static final String SOURCE_LIBRO_DIARIO = "/reportes/jasper/LibroDiario.jasper";
	public static final String SOURCE_LIBRO_MAYOR = "/reportes/jasper/LibroMayor.jasper";
	public static final String SOURCE_LIBRO_VENTAS = "/reportes/jasper/LibroVentas.jasper";
	public static final String SOURCE_LIBRO_VENTAS_FC = "/reportes/jasper/LibroVentasFormularioContinuo.jasper";
	public static final String SOURCE_LIBRO_VENTAS_FC_XLS = "/reportes/jasper/LibroVentasFormularioContinuo_xls.jasper";
	public static final String SOURCE_LISTADO_COBRANZAS = "/reportes/jasper/ListadoCobranzas.jasper";
	public static final String SOURCE_LISTADO_TRANSFERENCIAS = "/reportes/jasper/ListadoTransferencias.jasper";
	public static final String SOURCE_MOROSIDAD_CLI_DET = "/reportes/jasper/CarteraClientesDetallado.jasper";
	public static final String SOURCE_RANKING_VTAS_FLIA_DET = "/reportes/jasper/RankingVentasPorFamiliaDetallado.jasper";
	public static final String SOURCE_RANKING_VTAS_FLIA_RES = "/reportes/jasper/RankingVentasPorFamiliaResumido.jasper";
	public static final String SOURCE_HISTORIAL_MOVIMIENTOS_ART = "/reportes/jasper/HistorialMovimientosArticulo.jasper";
	public static final String SOURCE_REMISION = "/reportes/jasper/Remision.jasper";
	public static final String SOURCE_COMISION_COBRO = "/reportes/jasper/ComisionPorCobros.jasper";
	public static final String SOURCE_CTA_CTE_SALDOS_DHS = "/reportes/jasper/CtaCteSaldosDHS.jasper";
	public static final String SOURCE_RESUMEN_CONCILIACION = "/reportes/jasper/ResumenConciliacion.jasper";
	public static final String SOURCE_VENTAS_VENDEDOR = "/reportes/jasper/VentasPorVendedor.jasper";
	public static final String SOURCE_VENTAS_POR_CLIENTES = "/reportes/jasper/VentasPorCliente.jasper";
	public static final String SOURCE_VENTAS_POR_CLIENTES_ = "/reportes/jasper/_VentasPorCliente.jasper";
	public static final String SOURCE_VENTAS_POR_CLIENTES_ULT_VTA = "/reportes/jasper/VentasPorCliente_.jasper";
	public static final String SOURCE_METAS_VENDEDOR = "/reportes/jasper/MetasPorVendedor.jasper";
	public static final String SOURCE_CONCILIACIONES_BANCARIAS = "/reportes/jasper/ConciliacionBancaria.jasper";
	public static final String SOURCE_VENTAS_UTILIDAD_DETALLADO = "/reportes/jasper/VentasUtilidadDetallado.jasper";
	public static final String SOURCE_VENTAS_UTILIDAD_DETALLADO_SIN_CAB = "/reportes/jasper/VentasUtilidadDetallado_sin_cabecera.jasper";
	public static final String SOURCE_ACUSE = "/reportes/jasper/Acuse.jasper";
	public static final String SOURCE_ORDEN_SERV_TEC = "/reportes/jasper/OrdenServicioTecnico.jasper";
	public static final String SOURCE_INFORME_TECNICO = "/reportes/jasper/InformeTecnico.jasper";
	public static final String SOURCE_ORDEN_COMPRA = "/reportes/jasper/OrdenCompra.jasper";
	public static final String SOURCE_GASTOS_POR_CUENTA_CONTABLE = "/reportes/jasper/GastosPorCuentaContable.jasper";
	public static final String SOURCE_VENTAS_DESGLOSADO = "/reportes/jasper/VentasDesglosado.jasper";
	public static final String SOURCE_CONSTANCIA_ENTREGA = "/reportes/jasper/ConstanciaEntrega.jasper";
	public static final String SOURCE_SOLICITUD_NC = "/reportes/jasper/SolicitudNotaCredito.jasper";
	public static final String SOURCE_NC_COMPRA = "/reportes/jasper/NotaCreditoCompra.jasper";
	public static final String SOURCE_LIQUIDACION_SALARIO = "/reportes/jasper/ReciboLiquidacionSalario.jasper";
	public static final String SOURCE_RESUMEN_IMPORTACION = "/reportes/jasper/ResumenImportacion.jasper";
	public static final String SOURCE_RESUMEN_IMPORTACION_SIN_CAB = "/reportes/jasper/ResumenImportacion_sin_cab.jasper";
	public static final String SOURCE_RESUMEN_IMPORTACION_ = "/reportes/jasper/ResumenImportacion_.jasper";
	public static final String SOURCE_ABASTECIMIENTO_MOVIM_ARTICULOS = "/reportes/jasper/MovimientoArticulos.jasper";
	public static final String SOURCE_LIBRO_COMPRAS_INDISTINTO = "/reportes/jasper/LibroComprasIndistinto.jasper";
	public static final String SOURCE_LIBRO_COMPRAS_INDISTINTO_ = "/reportes/jasper/LibroComprasIndistinto_.jasper";
	public static final String SOURCE_LIBRO_COMPRAS_IVA_DIRECTO = "/reportes/jasper/LibroComprasIvaDirecto.jasper";
	public static final String SOURCE_VENTAS_LITRAJE = "/reportes/jasper/LitrajeVentas.jasper";
	public static final String SOURCE_VENTAS_LITRAJE_ = "/reportes/jasper/LitrajeVentas_.jasper";
	public static final String SOURCE_VENTAS_LISTA_FAMILIA = "/reportes/jasper/VentaListaPrecioFamilia.jasper";
	public static final String SOURCE_COBRANZAS_VENDEDOR_DETALLADO = "/reportes/jasper/CobranzasVendedorDetallado.jasper";
	public static final String SOURCE_LIBRO_COMPRAS_MATRICIAL = "/reportes/jasper/LibroComprasMatricial.jasper";
	public static final String SOURCE_LIBRO_VENTAS_MATRICIAL = "/reportes/jasper/LibroVentasMatricial.jasper";
	public static final String SOURCE_VENTAS_CLIENTE_ARTICULO = "/reportes/jasper/VentasClienteArticulo.jasper";
	public static final String SOURCE_VENTAS_CLIENTE_ARTICULO_ = "/reportes/jasper/VentasClienteArticulo_.jasper";
	public static final String SOURCE_VENTAS_PROVEEDOR_CLIENTE = "/reportes/jasper/VentasProveedorCliente.jasper";
	public static final String SOURCE_VENTAS_PROVEEDOR_CLIENTE_ = "/reportes/jasper/VentasProveedorCliente_.jasper";
	public static final String SOURCE_COBRANZAS_DETALLADO = "/reportes/jasper/CobranzasDetallado.jasper";
	public static final String SOURCE_REEMBOLSOS_DETALLADO = "/reportes/jasper/ReembolsosDetallado.jasper";
	public static final String SOURCE_LISTADO_ANTICIPOS = "/reportes/jasper/ListadoAnticipos.jasper";
	public static final String SOURCE_MATRIZ_COMPRAS_LOCALES = "/reportes/jasper/MatrizComprasLocales.jasper";
	public static final String SOURCE_MATRIZ_COMPRAS_LOCALES_ = "/reportes/jasper/MatrizComprasLocales_.jasper";
	public static final String SOURCE_PLANILLA_SALARIOS = "/reportes/jasper/PlanillaSalarios.jasper";
	public static final String SOURCE_VENTAS_PROVEEDOR = "/reportes/jasper/VentasProveedor.jasper";
	public static final String SOURCE_VENTAS_PROVEEDOR_ = "/reportes/jasper/VentasProveedor_.jasper";
	public static final String SOURCE_LISTADO_IMPORTACIONES = "/reportes/jasper/ListadoImportaciones.jasper";
	public static final String SOURCE_SALDO_FAMILIA = "/reportes/jasper/CtaCteSaldosFamilia.jasper";
	public static final String SOURCE_REPARTOS_DETALLADO = "/reportes/jasper/RepartosDetallado.jasper";
	public static final String SOURCE_REPARTOS_DETALLADO_ = "/reportes/jasper/RepartosDetallado_.jasper";
	public static final String SOURCE_CLIENTES_VENDEDOR = "/reportes/jasper/ClientesVendedor.jasper";
	public static final String SOURCE_CLIENTES_VENDEDOR_ = "/reportes/jasper/ClientesVendedor_.jasper";
	
	static final String CONTEXT = Sessions.getCurrent().getWebApp().getRealPath("/");
	static final String LOGO = CONTEXT + "/logo.png";
	
	public static final Object[] FORMAT_PDF = new Object[]{ "PDF", "pdf" };
	public static final Object[] FORMAT_XLS = new Object[]{ "Excel", "xls" };
	public static final Object[] FORMAT_CSV = new Object[]{ "CSV", "csv" };
	public static final Object[] FORMAT_HTML = new Object[]{ "HTML", "html" };
	
	private String source;
	private JRDataSource dataSource;
	private Map<String, Object> params;
	
	private Object[] selectedReporte;
	private Object[] selectedFormato;
	
	private ReportConfig reportConfig = null;

	@Init()
	public void init(@ExecutionArgParam("source") String source,
			@ExecutionArgParam("parametros") Map<String, Object> params,
			@ExecutionArgParam("dataSource") JRDataSource dataSource,
			@ExecutionArgParam("format") Object[] format) {
		Misc misc = new Misc();
		this.source = source;
		this.params = params;
		this.dataSource = dataSource;
		this.selectedFormato = format == null ? FORMAT_PDF : format;
		this.params.put("Logo", LOGO);
		this.params.put("Empresa", Configuracion.empresa);
		this.params.put("Generado", misc.dateToString(new Date(), Misc.DD_MM__YYY_HORA_MIN));
	}
	
	@AfterCompose()
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}	
	
	@Wire
	private Jasperreport report;
	
	@Wire
	private Window win;	
	
	@Command("showReport")
	public void showReport() {
		
		Clients.showBusy(this.report, "Procesando Información..");
		
		this.reportConfig = new ReportConfig();
		this.reportConfig.setSource(this.source);
		this.reportConfig.setParameters(this.params);
		this.reportConfig.setDataSource(this.dataSource);
		
		Events.echoEvent("onLater", this.report, null);
	}	
	
	@Command
	public void showReport_() {		
		this.reportConfig = new ReportConfig();
		this.reportConfig.setSource(this.source);
		this.reportConfig.setParameters(this.params);
		this.reportConfig.setDataSource(this.dataSource);
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@Command
	public void clearProgress() {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(report);
			}
		});
		timer.setParent(this.win);
	}
	
	
	/************************* GET/SET *************************/
	
	public static String getRucEmpresa() {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			return "80093865-8";
		}
		return "80024884-8";
	}
	
	public static String getDireccionEmpresa() {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			return "Avda. Mcal. Estigarribia e/ Blas Garay y Curupayty";
		}
		return "Av Mariscal José Félix Estigarribia, Fernando De La Mora";
	}

	public ReportConfig getReportConfig() {
		return reportConfig;
	}

	public void setReportConfig(ReportConfig reportConfig) {
		this.reportConfig = reportConfig;
	}

	public Object[] getSelectedReporte() {
		return selectedReporte;
	}

	public void setSelectedReporte(Object[] selectedReporte) {
		this.selectedReporte = selectedReporte;
	}

	public Object[] getSelectedFormato() {
		return selectedFormato;
	}

	public void setSelectedFormato(Object[] selectedFormato) {
		this.selectedFormato = selectedFormato;
	}
}
