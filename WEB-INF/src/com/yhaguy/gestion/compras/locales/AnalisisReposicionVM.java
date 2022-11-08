package com.yhaguy.gestion.compras.locales;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AnalisisReposicion;
import com.yhaguy.domain.AnalisisReposicionDetalle;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ArticuloMarca;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

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
	public void updateMeses() {
		if (this.analisis.getDesde() != null && this.analisis.getHasta() != null) {
			System.out.println("--- meses: " + Utiles.getNumeroMeses(this.analisis.getDesde(), this.analisis.getHasta()));
			this.analisis.setCantidadMeses(Utiles.getNumeroMeses(this.analisis.getDesde(), this.analisis.getHasta()));
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
		
		if (desde == null || hasta == null || this.selectedDepositos.size() == 0) {
			Clients.showNotification("DEBE COMPLETAR LOS PARÁMETROS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		if (this.analisis.getCantidadMeses() <= 0) {
			Clients.showNotification("CANTIDAD MESES DEBE SER MAYOR A CERO..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		this.analisis.getDetalles().clear();
		this.analisis.setDepositos("");
		
		int order = this.analisis.getTipoRanking().equals(AnalisisReposicion.POR_UNIDADES) ? 3 : 4;
		
		for (Deposito d : this.selectedDepositos) {
			this.analisis.setDepositos(this.analisis.getDepositos() + d.getDescripcion() + " - ");
		}

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> data = new ArrayList<Object[]>();
		List<Object[]> ventas = rr.getVentasDetallado_(desde, hasta, idProv, idMarca, idFlia, order);
		List<Object[]> repos = rr.getArticuloReposicionesDetallado(desde, hasta, idProv, idMarca, idFlia);
		List<Object[]> compras = rr.getComprasLocalesDetallado_(desde, hasta, idProv, idMarca, idFlia);
		List<Object[]> imports = rr.getImportacionesDetallado_(desde, hasta, idProv, idMarca, idFlia);
		Map<String, Double> mapRepos = new HashMap<String, Double>();
		Map<String, Double> mapCompras = new HashMap<String, Double>();
		Map<String, Double> mapImports = new HashMap<String, Double>();
		Map<String, Double> mapNcreds = new HashMap<String, Double>();
		
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

		for (int i = 0; i < ventas.size(); i++) {
			Object[] venta = ventas.get(i);
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
			data.add(new Object[] { i + 1, venta[1], venta[2], rep, com, venta[3], imp, venta[4], ncs, venta[5] });
		}
		
		for (Object[] item : data) {
			long stock = rr.getStockArticulo((String) item[1], this.selectedDepositos);
			AnalisisReposicionDetalle det = new AnalisisReposicionDetalle();
			det.setRanking((int) item[0]);
			det.setCodigoInterno((String) item[1]);
			det.setDescripcion((String) item[7]);
			det.setFamilia((String) item[9]);
			det.setVentasUnidades((double) item[2]);
			det.setDevoluciones((double) item[8]);
			det.setVentasImporte((double) item[5]);
			det.setPedidoReposicion((double) item[3]);
			det.setComprasUnidades((double) item[4]);
			det.setImportacionUnidades((double) item[6]);
			det.setStock(stock);
			det.setSugerido(0.0);
			det.setObservacion("");
			det.setPromedio(det.getVentasUnidades() / this.analisis.getCantidadMeses());
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
	
	/**
	 * print
	 */
	private void imprimir(AnalisisReposicion item) throws Exception {		
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (AnalisisReposicionDetalle det : item.getDetallesOrdenado()) {
			data.add(new Object[] { det.getRanking(), det.getCodigoInterno(), det.getDescripcion(), det.getFamilia(),
					det.getVentasUnidades(), det.getPromedio(), det.getDevoluciones(), det.getPedidoReposicion(), det.getComprasUnidades(),
					det.getImportacionUnidades(), det.getStock(), det.getSugerido(), det.getAprobado(), det.getVentasImporte(), det.getObservacion() });
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
		r.createCell(cell++).setCellValue("VENTAS");
		r.createCell(cell++).setCellValue("PROMEDIO VENTAS");
		r.createCell(cell++).setCellValue("DEVOLUCIONES");
		r.createCell(cell++).setCellValue("PEDIDOS REPOSICION");
		r.createCell(cell++).setCellValue("COMPRAS LOCALES");
		r.createCell(cell++).setCellValue("IMPORTACIONES");
		r.createCell(cell++).setCellValue("STOCK");
		r.createCell(cell++).setCellValue("SUGERIDO");
		r.createCell(cell++).setCellValue("APROBADO");
		r.createCell(cell++).setCellValue("IMPORTE VENTAS");
		r.createCell(cell++).setCellValue("OBSERVACIONES");
		for (Object[] c : items) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(c[0] + "");
			row.createCell(cellIndex++).setCellValue(c[1] + "");
			row.createCell(cellIndex++).setCellValue(c[2] + "");
			row.createCell(cellIndex++).setCellValue(c[3] + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[4]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[5]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[6]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[7]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[8]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[9]));
			row.createCell(cellIndex++).setCellValue(c[10] + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[11]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[12]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[13]));
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
		this.analisis.setFecha(new Date());
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
