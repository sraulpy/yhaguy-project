package com.yhaguy.gestion.compras.locales;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AnalisisReposicion;
import com.yhaguy.domain.AnalisisReposicionDetalle;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ArticuloMarca;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.CompraLocalOrdenDetalle;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class AnalisisReposicionVM extends SimpleViewModel {
	
	static final String ZUL_DETALLE = "/yhaguy/gestion/compras/locales/analisis_reposicion_detalle.zul";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaYY = "";
	
	private AnalisisReposicion analisis;
	private AnalisisReposicion selectedAnalisis;
	private String razonSocialProveedor = "";
	private String descripcionMarca = "";
	
	private List<Deposito> selectedDepositos = new ArrayList<Deposito>();
	
	private List<CompraLocalOrdenDetalle> detalles;
	
	private Proveedor proveedor;
	
	private Window win;

	@Init(superclass = true)
	public void init() {
		try {
			this.inicializar();
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaYY = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Listen("onUpload=#upImg")
	public void uploadAdjunto(UploadEvent event) throws IOException {
		this.subirAdjunto(event);
	}
	
	@Command
	public void ejecutar() {
		try {
			this.generarInforme();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	@NotifyChange("analisis")
	public void modificar() {
		this.analisis = this.selectedAnalisis;
		this.win = (Window) Executions.createComponents(ZUL_DETALLE, this.mainComponent, null);
		this.win.doModal();
	}
	
	/**
	 * generacion del informe..
	 */
	private void generarInforme() throws Exception {
		Date desde = this.analisis.getDesde();
		Date hasta = this.analisis.getHasta();
		Proveedor prov = this.analisis.getProveedor();
		ArticuloMarca marca = this.analisis.getMarca();
		ArticuloFamilia flia = this.analisis.getFamilia();
		long idProv = prov != null ? prov.getId() : 0;
		long idMarca = marca != null ? marca.getId() : 0;
		long idFlia = flia != null ? flia.getId() : 0;
		String rucClienteExcluido = null;
		long idProveedorExcluido = 0;
		
		if (desde == null || hasta == null || this.selectedDepositos.size() == 0) {
			Clients.showNotification("DEBE COMPLETAR LOS PARÁMETROS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		this.analisis.getDetalles().clear();
		this.analisis.setDepositos("");
		
		int order = this.analisis.getTipoRanking().equals(AnalisisReposicion.POR_UNIDADES) ? 3 : 4;
		
		for (Deposito d : this.selectedDepositos) {
			this.analisis.setDepositos(this.analisis.getDepositos() + d.getDescripcion() + " - ");
		}
		
		if (!this.analisis.isIncluirRepresentaciones()) {
			rucClienteExcluido = Configuracion.RUC_YHAGUY_REPRESENTACIONES;
		} else {
			rucClienteExcluido = null;
		}
		
		if (!this.analisis.isIncluirValvoline()) {
			idProveedorExcluido = Configuracion.ID_PROVEEDOR_VALVOLINE;
		} else {
			idProveedorExcluido = 0;
		}

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> data = new ArrayList<Object[]>();
		List<Object[]> ventas = rr.getVentasDetallado_(desde, hasta, idProv, idMarca, idFlia, order, rucClienteExcluido);
		List<Object[]> repos = rr.getArticuloReposicionesDetallado(desde, hasta, idProv, idMarca, idFlia);
		List<Object[]> compras = rr.getComprasLocalesDetallado_(desde, hasta, idProv, idMarca, idFlia);
		List<Object[]> imports = rr.getImportacionesDetallado_(desde, hasta, idProv, idMarca, idFlia, idProveedorExcluido);
		Map<String, Double> mapRepos = new HashMap<String, Double>();
		Map<String, Double> mapCompras = new HashMap<String, Double>();
		Map<String, Double> mapImports = new HashMap<String, Double>();
		Map<String, Double> mapNcreds = new HashMap<String, Double>();
		Map<String, Double> mapVentas = new HashMap<String, Double>();
		Map<String, String> mapCodigos = new HashMap<String, String>();
		
		for (Object[] obj : repos) {
			String key = (String) obj[1];
			Double value = (Double) obj[2];
			mapRepos.put(key, value);
		}
		
		for (Object[] obj : compras) {
			String key = (String) obj[1];
			Double value = (Double) obj[2];
			mapCompras.put(key, value);
		}
		
		for (Object[] obj : imports) {
			String key = (String) obj[1];
			Double value = (Double) obj[2];
			mapImports.put(key, value);
		}
		
		if (this.analisis.isIncluirDevoluciones()) {
			List<Object[]> ncreds = rr.getNotasCreditoVentaDetallado_(desde, hasta, idProv, idMarca);
			for (Object[] obj : ncreds) {
				String key = (String) obj[1];
				Double value = (Double) obj[2];
				mapNcreds.put(key, value);
			}
		}
		
		for (Object[] venta : ventas) {
			Date fecha = (Date) venta[6];
			int nro = Utiles.getNumeroMes(fecha);
			double vtas = (double) venta[2];
			String key = ((String) venta[1]) + "-" + nro;
			Double acum = mapVentas.get(key);
			if (acum != null) {
				mapVentas.put(key, (acum + vtas));
			} else {
				mapVentas.put(key, vtas);
			}			
		}

		for (int i = 0; i < ventas.size(); i++) {
			Object[] venta = ventas.get(i);
			
			if (mapCodigos.get(venta[1]) == null) {
				Double rep = mapRepos.get(venta[1]);
				if (rep == null) {
					rep = 0.0;
				}
				Double com = mapCompras.get(venta[1]);
				if (com == null) {
					com = 0.0;
				}
				Double imp = mapImports.get(venta[1]);
				if (imp == null) {
					imp = 0.0;
				}
				Double ncs = mapNcreds.get(venta[1]);
				if (ncs == null) {
					ncs = 0.0;
				}
				List<Long> cantClientes = rr.getVentasCantClientes(desde, hasta, (long) venta[0], rucClienteExcluido);
				Object[] ultCompra = rr.getUltimaCompraLocalImportacion((long) venta[0]);
				String ultProv = ultCompra != null ? (String) ultCompra[2] : "";
				Object[] art = rr.getArticulo((long) venta[0]);
				Double ene = mapVentas.get(venta[1] + "-1"); Double feb = mapVentas.get(venta[1] + "-2"); Double mar = mapVentas.get(venta[1] + "-3");
				Double abr = mapVentas.get(venta[1] + "-4"); Double may = mapVentas.get(venta[1] + "-5"); Double jun = mapVentas.get(venta[1] + "-6");
				Double jul = mapVentas.get(venta[1] + "-7"); Double ago = mapVentas.get(venta[1] + "-8"); Double set = mapVentas.get(venta[1] + "-9");
				Double oct = mapVentas.get(venta[1] + "-10"); Double nov = mapVentas.get(venta[1] + "-11"); Double dic = mapVentas.get(venta[1] + "-12");
				data.add(new Object[] { i + 1, venta[1], venta[2], rep, com, venta[3], imp, venta[4], ncs, venta[5], ene, feb, mar, abr, may, jun, jul, ago, set, oct, nov, dic, cantClientes.size(), ultProv, ((double) art[3] * 1.1) });
				mapCodigos.put((String) venta[1], (String) venta[1]);
			}
		}
		
		for (Object[] item : data) {
			long stock = rr.getStockArticulo((String) item[1], this.selectedDepositos);		
			AnalisisReposicionDetalle det = new AnalisisReposicionDetalle();
			det.setRanking((int) item[0]);
			det.setCodigoInterno((String) item[1]);
			det.setDescripcion((String) item[7]);
			det.setFamilia((String) item[9]);
			det.setDevoluciones((double) item[8]);
			det.setVentasImporte((double) item[5]);
			det.setPedidoReposicion((double) item[3]);
			det.setComprasUnidades((double) item[4]);
			det.setImportacionUnidades((double) item[6]);
			det.setStock(stock);
			det.setSugerido(0.0);
			det.setObservacion("");			
			det.setEne(item[10] != null? (double) item[10] : 0.0);
			det.setFeb(item[11] != null? (double) item[11] : 0.0);
			det.setMar(item[12] != null? (double) item[12] : 0.0);
			det.setAbr(item[13] != null? (double) item[13] : 0.0);
			det.setMay(item[14] != null? (double) item[14] : 0.0);
			det.setJun(item[15] != null? (double) item[15] : 0.0);
			det.setJul(item[16] != null? (double) item[16] : 0.0);
			det.setAgo(item[17] != null? (double) item[17] : 0.0);
			det.setSet(item[18] != null? (double) item[18] : 0.0);
			det.setOct(item[19] != null? (double) item[19] : 0.0);
			det.setNov(item[20] != null? (double) item[20] : 0.0);
			det.setDic(item[21] != null? (double) item[21] : 0.0);
			det.setVentasUnidades(det.getEne() + det.getFeb() + det.getMar() + det.getAbr() + det.getMay()
					+ det.getJun() + det.getJul() + det.getAgo() + det.getSet() + det.getOct() + det.getNov() + det.getDic());
			
			int meses = 0;
			if (det.getEne() > 0) meses ++; if (det.getFeb() > 0) meses ++; if (det.getMar() > 0) meses ++;
			if (det.getAbr() > 0) meses ++; if (det.getMay() > 0) meses ++; if (det.getJun() > 0) meses ++;
			if (det.getJul() > 0) meses ++; if (det.getAgo() > 0) meses ++; if (det.getSet() > 0) meses ++;
			if (det.getOct() > 0) meses ++; if (det.getNov() > 0) meses ++; if (det.getDic() > 0) meses ++;
			det.setPromedio(det.getVentasUnidades() / meses);
			det.setCantClientes((int) item[22]);
			det.setUltProveedor((String) item[23]);
			det.setUltCosto((double) item[24]);
			this.analisis.getDetalles().add(det);
		}
		
		this.win = (Window) Executions.createComponents(ZUL_DETALLE, this.mainComponent, null);
		win.doModal();
	}
	
	@Command
	@NotifyChange("*")
	public void confirmar() throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.analisis, this.getLoginNombre());
		
		this.win.detach();
		
		this.imprimir(this.analisis);		
		this.inicializar();
	}
	
	@Command
	public void imprimirSeleccionado() throws Exception {				
		this.imprimir(this.selectedAnalisis);		
	}
	
	static final String ZUL_BUSCADOR_ART = "/yhaguy/gestion/articulos/BuscadorArticulos.zul";
	
	@Command
	public void buscarArticulos(@BindingParam("codigo") String codigo) {	
		Map<String, String> params = new HashMap<String, String>();
		params.put("codigo", codigo);
		Window win = (Window) Executions.createComponents(ZUL_BUSCADOR_ART, null, params);
		win.doModal();
	}
	
	
	/**
	 * print
	 */
	private void imprimir(AnalisisReposicion item) throws Exception {		
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (AnalisisReposicionDetalle det : item.getDetallesOrdenado()) {
			data.add(new Object[] { det.getRanking(), det.getCodigoInterno(), det.getDescripcion(), det.getFamilia(),
					det.getVentasUnidades(), det.getPromedio(), det.getDevoluciones(), det.getPedidoReposicion(), det.getComprasUnidades(),
					det.getImportacionUnidades(), det.getStock(), det.getSugerido(), det.getAprobado(), det.getVentasImporte(), det.getObservacion(),
					det.getEne(), det.getFeb(), det.getMar(), det.getAbr(), det.getMay(), det.getJun(), det.getJul(), det.getAgo(), det.getSet(),
					det.getOct(), det.getNov(), det.getDic(), det.getCantClientes(), det.getUltProveedor(), det.getUltCosto() });
		}
		this.exportExcel(data);
	}
	
	private void exportExcel(List<Object[]> items) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Análisis Reposición");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("RANKING");
		r.createCell(cell++).setCellValue("CODIGO");
		r.createCell(cell++).setCellValue("DESCRIPCION");
		r.createCell(cell++).setCellValue("FAMILIA");
		r.createCell(cell++).setCellValue("ENERO"); r.createCell(cell++).setCellValue("FEBRERO"); r.createCell(cell++).setCellValue("MARZO");
		r.createCell(cell++).setCellValue("ABRIL"); r.createCell(cell++).setCellValue("MAYO"); r.createCell(cell++).setCellValue("JUNIO");
		r.createCell(cell++).setCellValue("JULIO"); r.createCell(cell++).setCellValue("AGOSTO"); r.createCell(cell++).setCellValue("SETIEMBRE");
		r.createCell(cell++).setCellValue("OCTUBRE"); r.createCell(cell++).setCellValue("NOVIEMBRE"); r.createCell(cell++).setCellValue("DICIEMBRE");		
		r.createCell(cell++).setCellValue("TOTAL VENTAS");
		r.createCell(cell++).setCellValue("PROMEDIO VENTAS");
		r.createCell(cell++).setCellValue("CANTIDAD CLIENTES");
		r.createCell(cell++).setCellValue("DEVOLUCIONES");
		r.createCell(cell++).setCellValue("PEDIDOS REPOSICION");
		r.createCell(cell++).setCellValue("COMPRAS LOCALES");
		r.createCell(cell++).setCellValue("IMPORTACIONES");
		r.createCell(cell++).setCellValue("STOCK");
		r.createCell(cell++).setCellValue("ULTIMO PROVEEDOR");
		r.createCell(cell++).setCellValue("SUGERIDO");
		r.createCell(cell++).setCellValue("APROBADO");
		r.createCell(cell++).setCellValue("IMPORTE VENTAS IVA INC.");
		r.createCell(cell++).setCellValue("ULTIMO COSTO IVA INC.");
		r.createCell(cell++).setCellValue("OBSERVACIONES");
		for (Object[] c : items) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(c[0] + "");
			row.createCell(cellIndex++).setCellValue(c[1] + "");
			row.createCell(cellIndex++).setCellValue(c[2] + "");
			row.createCell(cellIndex++).setCellValue(c[3] + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[15]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[16]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[17]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[18]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[19]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[20]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[21]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[22]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[23]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[24]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[25]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[26]));			
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[4]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[5]));
			row.createCell(cellIndex++).setCellValue(c[27] + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[6]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[7]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[8]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[9]));
			row.createCell(cellIndex++).setCellValue(c[10] + "");
			row.createCell(cellIndex++).setCellValue(c[28] + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[11]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[12]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[13]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[29]));
			row.createCell(cellIndex++).setCellValue(c[14] + "");
		}
		listSheet.autoSizeColumn(0);
		listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2);
		listSheet.autoSizeColumn(3);
		listSheet.autoSizeColumn(4);
		listSheet.autoSizeColumn(5);
		listSheet.autoSizeColumn(6);
		listSheet.autoSizeColumn(7);
		listSheet.autoSizeColumn(8);
		listSheet.autoSizeColumn(9);
		listSheet.autoSizeColumn(10);
		listSheet.autoSizeColumn(11);
		listSheet.autoSizeColumn(12);
		listSheet.autoSizeColumn(13);
		listSheet.autoSizeColumn(14);
		listSheet.autoSizeColumn(15);
		listSheet.autoSizeColumn(16);
		listSheet.autoSizeColumn(17);
		listSheet.autoSizeColumn(18);
		listSheet.autoSizeColumn(19);
		listSheet.autoSizeColumn(20);
		listSheet.autoSizeColumn(21);
		listSheet.autoSizeColumn(22);
		listSheet.autoSizeColumn(23);
		listSheet.autoSizeColumn(24);
		listSheet.autoSizeColumn(25);
		listSheet.autoSizeColumn(26);
		listSheet.autoSizeColumn(27);
		listSheet.autoSizeColumn(28);
		listSheet.autoSizeColumn(29);
		listSheet.autoSizeColumn(30);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("AnalisisReposicion.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * inicializar
	 */
	private void inicializar() {
		this.analisis = new AnalisisReposicion();
		this.analisis.setTipoRanking(AnalisisReposicion.POR_UNIDADES);
		this.analisis.setIncluirDevoluciones_("NO");
		this.analisis.setIncluirRepresentaciones_("SI");
		this.analisis.setIncluirValvoline_("SI");
		this.analisis.setFecha(new Date());
	}
	
	@Command
	@NotifyChange("*")
	public void prepararOrdenCompra(@BindingParam("parent") Component parent, @BindingParam("popup") Popup popup) throws Exception {
		this.detalles = new ArrayList<CompraLocalOrdenDetalle>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (AnalisisReposicionDetalle item : this.selectedAnalisis.getDetallesOrdenado()) {
			
			if (item.getAprobado() > 0) {
				Articulo ar = rr.getArticuloByCodigoInterno(item.getCodigoInterno());
				CompraLocalOrdenDetalle det = new CompraLocalOrdenDetalle();
				det.setArticulo(ar);
				det.setCantidad(((Double) item.getAprobado()).intValue());
				det.setCantidadRecibida(det.getCantidad());
				det.setCostoGs(0);
				det.setCostoDs(0);
				det.setOrdenCompra(true);
				det.setIva(rr.getTipoPorSigla(Configuracion.SIGLA_IVA_10));
				det.setAuxi("ANA-REP-" + this.selectedAnalisis.getId() + "");
				this.detalles.add(det);
			}
			
		}
		popup.open(parent, "after_start");
	}
	
	@Command
	@NotifyChange("*")
	public void generarOrdenCompra(@BindingParam("popup") Popup popup) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CompraLocalOrden oc = new CompraLocalOrden();
		oc.setAutorizado(false);
		oc.setAutorizadoPor("");
		oc.setFechaCreacion(new Date());
		oc.setNumero(Configuracion.NRO_COMPRA_LOCAL_ORDEN + "-"
				+ AutoNumeroControl.getAutoNumero(Configuracion.NRO_COMPRA_LOCAL_ORDEN, 5));
		oc.setObservacion("ANÁLISIS DE REPOSICION - " + this.selectedAnalisis.getId());
		oc.setProveedor(proveedor);
		oc.setSucursal(rr.getSucursalAppById(this.getAcceso().getSucursalOperativa().getId()));
		oc.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ORDEN_COMPRA));
		oc.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		oc.setTipoCambio(1);
		oc.setCondicionPago(rr.getCondicionPagoById(1));
		Set<CompraLocalOrdenDetalle> dets = new HashSet<CompraLocalOrdenDetalle>();
		for (CompraLocalOrdenDetalle det : this.detalles) {
			dets.add(det);
		}
		oc.setDetalles(dets);
		rr.saveObject(oc, this.getLoginNombre());
		Clients.showNotification("ORDEN COMPRA GENERADA NRO. " + oc.getNumero());
		popup.close();
	}
	
	/**
	 * upload del adjunto..
	 */
	private void subirAdjunto(UploadEvent event) throws IOException {
		String folder = Configuracion.pathOrdenCompra;
		String format = "." + event.getMedia().getFormat();		
		String name = "ANALISIS_REPOSICION_" + this.selectedAnalisis.getId() + "";
		
		boolean isText = "text/csv".equals(event.getMedia().getContentType());
		InputStream file_ = new ByteArrayInputStream(isText ? event.getMedia().getStringData().getBytes() : event.getMedia().getByteData());
		this.m.uploadFile(folder, name, format, file_);
		this.selectedAnalisis.setAuxi(Configuracion.pathOrdenCompraGenerico + name + format);
		BindUtils.postNotifyChange(null, null, this, "*");
		Clients.showNotification("Adjunto correctamente subido..");
	}
	
	@Command
	public void imprimirPDF() throws Exception {
		this.imprimirAnalisis();
	}
	
	/**
	 * Despliega el Reporte de Analisis..
	 */
	private void imprimirAnalisis() throws Exception {	
		String source = ReportesViewModel.SOURCE_ANALISIS_REPOSICION;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new AnalisisDataSource();
		params.put("Titulo", "ANÁLISIS DE REPOSICIÓN");
		params.put("Usuario", this.getLoginNombre());
		params.put("Desde", Utiles.getDateToString(this.selectedAnalisis.getDesde(), "dd-MM-yyyy"));
		params.put("Hasta", Utiles.getDateToString(this.selectedAnalisis.getHasta(), "dd-MM-yyyy"));
		params.put("Familia", this.selectedAnalisis.getFamilia() != null ? this.selectedAnalisis.getFamilia().getDescripcion() : "TODOS");
		params.put("RankingPor", this.selectedAnalisis.getTipoRanking());
		this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	public void imprimirComprobante(String source,
			Map<String, Object> parametros, JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions.createComponents(
				ReportesViewModel.ZUL_REPORTES, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * DataSource del Analisis..
	 */
	class AnalisisDataSource implements JRDataSource {

		public AnalisisDataSource() {
			System.out.println(selectedAnalisis.getDetallesOrdenado().size() + "");
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			AnalisisReposicionDetalle item = selectedAnalisis.getDetallesOrdenado().get(index);

			if ("Descripcion".equals(fieldName)) {
				value = item.getDescripcion();
			} else if ("Codigo".equals(fieldName)) {
				value = item.getCodigoInterno();
			} else if ("Ranking".equals(fieldName)) {
				value = item.getRanking() + "";
			} else if ("Ene".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getEne());
			} else if ("Feb".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getFeb());
			} else if ("Mar".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getMar());
			} else if ("Abr".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAbr());
			} else if ("May".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getMay());
			} else if ("Jun".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getJun());
			} else if ("Jul".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getJul());
			} else if ("Ago".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAgo());
			} else if ("Set".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getSet());
			} else if ("Oct".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getOct());
			} else if ("Nov".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getNov());
			} else if ("Dic".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getDic());
			} else if ("Total".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getVentasUnidades());
			} else if ("Promedio".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getPromedio());
			} else if ("Sugerido".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getSugerido());
			} else if ("Aprobado".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAprobado());
			} else if ("Stock".equals(fieldName)) {
				value = item.getStock() + "";
			} else if ("ImporteVtas".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getVentasImporte());
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < selectedAnalisis.getDetalles().size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}
	
	/**
	 * GETS AND SETS
	 */
	
	@DependsOn("razonSocialProveedor")
	public List<Proveedor> getProveedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedores(this.razonSocialProveedor);
	}
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaYY" })
	public List<AnalisisReposicion> getListaAnalisis() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getAnalisisReposicion(this.getFilterFecha());
	}
	
	@DependsOn("descripcionMarca")
	public List<ArticuloMarca> getMarcas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getMarcas(this.descripcionMarca);
	}
	
	/**
	 * @return familias
	 */
	@SuppressWarnings("unchecked")
	public List<ArticuloMarca> getFamilias() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloFamilia.class.getName());
	}
	
	/**
	 * @return los depositos..
	 */
	public List<Deposito> getDepositos() {
		List<Deposito> out = new ArrayList<Deposito>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			out = rr.getDepositos();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaYY.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaYY.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaYY;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaYY;
		return this.filterFechaYY + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
	}
	
	/**
	 * @return los tipos
	 */
	public List<String> getTipos() {
		List<String> out = new ArrayList<String>();
		out.add(AnalisisReposicion.POR_UNIDADES);
		out.add(AnalisisReposicion.POR_IMPORTE);
		return out;
	}
	
	/**
	 * @return los tipos
	 */
	public List<String> getListaSN() {
		List<String> out = new ArrayList<String>();
		out.add("SI");
		out.add("NO");
		return out;
	}
	
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		AccesoDTO out = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		if (out == null) {
			try {
				AssemblerAcceso as = new AssemblerAcceso();
				out = (AccesoDTO) as.obtenerAccesoDTO(Configuracion.USER_MOBILE);
				s.setAttribute(Configuracion.ACCESO, out);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}			
		return out;
	}
	
	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFechaDD) {
		this.filterFechaDD = filterFechaDD;
	}

	public String getFilterFechaMM() {
		return filterFechaMM;
	}

	public void setFilterFechaMM(String filterFechaMM) {
		this.filterFechaMM = filterFechaMM;
	}

	public String getFilterFechaYY() {
		return filterFechaYY;
	}

	public void setFilterFechaYY(String filterFechaYY) {
		this.filterFechaYY = filterFechaYY;
	}

	public AnalisisReposicion getAnalisis() {
		return analisis;
	}

	public void setAnalisis(AnalisisReposicion analisis) {
		this.analisis = analisis;
	}

	public String getRazonSocialProveedor() {
		return razonSocialProveedor;
	}

	public void setRazonSocialProveedor(String razonSocialProveedor) {
		this.razonSocialProveedor = razonSocialProveedor;
	}

	public AnalisisReposicion getSelectedAnalisis() {
		return selectedAnalisis;
	}

	public void setSelectedAnalisis(AnalisisReposicion selectedAnalisis) {
		this.selectedAnalisis = selectedAnalisis;
	}

	public String getDescripcionMarca() {
		return descripcionMarca;
	}

	public void setDescripcionMarca(String descripcionMarca) {
		this.descripcionMarca = descripcionMarca;
	}

	public List<Deposito> getSelectedDepositos() {
		return selectedDepositos;
	}

	public void setSelectedDepositos(List<Deposito> selectedDepositos) {
		this.selectedDepositos = selectedDepositos;
	}

	public List<CompraLocalOrdenDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<CompraLocalOrdenDetalle> detalles) {
		this.detalles = detalles;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	
}

/**
 * Reporte de analisis reposicion..
 */
class ReporteAnalisisReposicion extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String proveedor;
	private String tipoRanking;
	private String marca;
	private String devoluciones;
	private String depositos;
	private String familia;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Ran.", TIPO_INTEGER, 20);
	static DatosColumnas col2 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Ventas", TIPO_DOUBLE, 30);
	static DatosColumnas col5 = new DatosColumnas("Prom.", TIPO_DOUBLE, 30);
	static DatosColumnas col6 = new DatosColumnas("N.Créd", TIPO_DOUBLE, 30);
	static DatosColumnas col7 = new DatosColumnas("P.Rep.", TIPO_DOUBLE, 30);
	static DatosColumnas col8 = new DatosColumnas("Compr.", TIPO_DOUBLE, 30);
	static DatosColumnas col9 = new DatosColumnas("Impor.", TIPO_DOUBLE, 30);
	static DatosColumnas col10 = new DatosColumnas("Stock", TIPO_LONG, 30);
	static DatosColumnas col11 = new DatosColumnas("Sug.", TIPO_DOUBLE, 30);
	static DatosColumnas col12 = new DatosColumnas("Imp.Vtas", TIPO_DOUBLE, 50);
	static DatosColumnas col13 = new DatosColumnas("Observación", TIPO_STRING);

	public ReporteAnalisisReposicion(Date desde, Date hasta, String proveedor, String tipoRanking, String marca, String devoluciones, String depositos, String familia) {
		this.desde = desde;
		this.hasta = hasta;
		this.proveedor = proveedor;
		this.tipoRanking = tipoRanking;
		this.marca = marca;
		this.devoluciones = devoluciones;
		this.depositos = depositos;
		this.familia = familia;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
		cols.add(col9);
		cols.add(col10);
		cols.add(col11);
		cols.add(col12);
		cols.add(col13);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Analisis de Reposicion");
		this.setDirectorio("compras");
		this.setNombreArchivo("Analisis-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Devoluciones", this.devoluciones)));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.textoParValor("Marca", this.marca))
				.add(this.textoParValor("Ranking", this.tipoRanking)));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Familia", this.familia))
				.add(this.textoParValor("Depósitos", this.depositos))
				.add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
