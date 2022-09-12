package com.yhaguy.gestion.articulos.buscador;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.ArticuloListaPrecioDetalle;
import com.yhaguy.domain.ArticuloPrecioJedisoft;
import com.yhaguy.domain.ArticuloUbicacion;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.ImportacionPedidoCompraDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.gestion.modulos.Permisos;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

public class BuscadorArticulosViewModel extends SimpleViewModel {
	
	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	
	public static final long ID_SUC_PRINCIPAL = 2;
	public static final long ID_DEP_1 = Configuracion.ID_DEPOSITO_PRINCIPAL;

	private String codInterno = "";
	private String codOriginal = "";
	private String codProveedor = "";
	private String descripcion = "";
	private String marca = "";
	private String familia = "";
	private String proveedor = "";
	
	private String filter_razonsocial = "";
	private String filter_ruc = "";
	
	private List<ArticuloUbicacion> ubicaciones = new ArrayList<ArticuloUbicacion>();
	
	private MyArray selectedItem;	
	private MyArray selectedPrecio;
	private List<MyArray> precios;
	private List<MyArray> existencia;
	private List<MyArray> importaciones = new ArrayList<MyArray>();
	private List<Object[]> historicoEntrada;
	private List<Object[]> historicoSalida;
	
	private int calcPorcentaje = 0;
	private int calcPorcentaje_ = 0;
	
	private long stockSalida = 0;
	private long stockEntrada = 0;
	private long stock = 0;
	
	private double montosPositivos = 0;
	private double montosNegativos = 0;
	
	private Object[] selectedCliente;
	private double precioDescontado = 0;
	
	private Date desde;
	private Date hasta = new Date();
	
	private int filterStock = 0;
	
	@Wire
	private Popup pop_img;
	
	@Wire
	private Popup pop_barcode;
	
	@Wire
	private Window findArt;
	
	@Init
	public void init() {
		try {
			this.desde = Utiles.getFechaInicioOperaciones();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose
	public void AfterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	@Wire
	private Popup historial;
	@Wire
	private Popup historialMonto;
	@Wire
	private Listbox listArt;
	
	@Command
	@NotifyChange({"precios", "existencia", "importaciones", "stock", "ubicaciones"})
	public void obtenerValores() throws Exception {
		this.obtenerPrecio();
		this.obtenerExistencia();
		this.obtenerImportacionesEnCurso();
		this.obtenerUbicaciones();
	}
	
	@Command
	@NotifyChange("selectedPrecio")
	public void calculadora() {
		double precio = (double) this.selectedPrecio.getPos2();
		double out = this.m.obtenerValorDelPorcentaje(precio, this.calcPorcentaje);
		double out_ = this.m.obtenerValorDelPorcentaje(precio, this.calcPorcentaje_);
		this.selectedPrecio.setPos3(precio + out);
		this.selectedPrecio.setPos4(precio - out_);
	}
	
	@Command
	@NotifyChange({"calcPorcentaje", "calcPorcentaje_"})
	public void reloadCalc() {
		this.calcPorcentaje = 0;
		this.calcPorcentaje_ = 0;
	}
	
	@Command
	public void habilitarPrecio() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idArticulo = this.selectedItem.getId();
		double precioMinimo = (double) this.selectedPrecio.getPos4();
		double precioMinimo_ = this.m.redondeoCuatroDecimales(precioMinimo);
		String user = this.getLoginNombre();
		rr.addArticuloPrecioMinimo(idArticulo, precioMinimo_, user);
		Clients.showNotification("Precio correctamente habilitado..");
	}
	
	@Command
	public void loadHistorico() throws Exception {
		this.loadHistorico_();
		this.historial.open(findArt, "overlap");
	}
	
	@Command
	public void loadHistoricoPorMonto() throws Exception {
		this.loadHistoricoPorMonto_();
		this.historialMonto.open(findArt, "overlap");
	}
	
	@Command
	public void refreshHistorico() throws Exception {
		this.loadHistorico_();
	}
	
	@Command
	public void refreshHistoricoMonto() throws Exception {
		this.loadHistoricoPorMonto_();
	}
	
	@Command
	public void verImagen() throws Exception {
		this.pop_img.open(200, 100);
		Clients.evalJavaScript("setImage('" + this.getUrlImagen() + "')");
	}
	
	@Command
	public void verBarcode() {
		this.pop_barcode.open(200, 100);
	}
	
	@Command
	@NotifyChange("filterStock")
	public void filtrarStock(@BindingParam("comp1") Hlayout comp1, @BindingParam("comp2") Spinner comp2) {
		this.filterStock = 1;
		comp1.setVisible(true);
		comp2.focus();
	}
	
	@Command
	@NotifyChange("filterStock")
	public void filtrarStockTodos(@BindingParam("comp") Spinner comp) {
		this.filterStock = 0;
		comp.setVisible(false);
	}
	
	@Command
	@NotifyChange("*")
	public void cleanFiltros() {
		this.filterStock = 0;
		this.filter_razonsocial = "";
		this.filter_ruc = "";
		this.codInterno = "";
		this.codOriginal = "";
		this.codProveedor = "";
		this.descripcion = "";
		this.marca = "";
		this.familia = "";
		this.proveedor = "";
	}
	
	@Command
	@NotifyChange("precioDescontado")
	public void setPrecioDescontadoMayorista(@BindingParam("comp") Bandbox comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Object[] art = rr.getArticulo_(this.selectedItem.getId());
		double mayorista = (double) art[4];
		double descuento = (double) this.selectedCliente[3];
		double descuento_ = Utiles.obtenerValorDelPorcentaje(mayorista, descuento);
		this.precioDescontado = mayorista - descuento_;
		comp.close();
	}
	
	@Command
	public void exportExcel() throws Exception {
		List<Object[]> items = this.getHistoricoEntrada();
		items.addAll(this.getHistoricoSalida());
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Historial Movimientos");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("CONCEPTO");
		r.createCell(cell++).setCellValue("EMPRESA");
		r.createCell(cell++).setCellValue("FECHA");
		r.createCell(cell++).setCellValue("NUMERO");
		r.createCell(cell++).setCellValue("CANTIDAD");
		r.createCell(cell++).setCellValue("PRECIO");
		for (Object[] c : items) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(c[0] + "");
			row.createCell(cellIndex++).setCellValue(c[5] + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getDateToString((Date) c[1], "dd-MM-yyyy") + "");
			row.createCell(cellIndex++).setCellValue(c[2] + "");
			row.createCell(cellIndex++).setCellValue(c[3] + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat(Double.parseDouble(c[4] + "")));
		}
		listSheet.autoSizeColumn(0);
		listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2);
		listSheet.autoSizeColumn(3);
		listSheet.autoSizeColumn(4);
		listSheet.autoSizeColumn(5);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("Historial.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return la url de la foto.
	 */
	private String getUrlImagen() {
		if (this.selectedItem == null)
			return "http://mra.yhaguyrepuestos.com.py/images/default.png";
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "articulos/" + this.selectedItem.getId() + ".png";
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRPS)) {
			return Configuracion.URL_IMAGES_PUBLIC_RPS + "articulos/" + this.selectedItem.getId() + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_BAT + "articulos/" + this.selectedItem.getId() + ".png";
	}
	
	/**
	 * obtiene los precios..
	 */
	public void obtenerPrecio() throws Exception {
		long idArticulo = this.selectedItem.getId();
		long idDeposito = ID_DEP_1;
		String codArt = (String) this.selectedItem.getPos1();
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			this.precios = this.getPreciosBaterias(codArt, idDeposito);
		} else {
			this.precios = this.getPrecios(idArticulo, idDeposito);
		}
	}
	
	/**
	 * obtiene el stock..
	 */
	private void obtenerExistencia() throws Exception {
		long idArticulo = this.selectedItem.getId();
		this.existencia = this.getExistencia(idArticulo);
	}
	
	/**
	 * obtiene los datos de importacion en curso..
	 */
	private void obtenerImportacionesEnCurso() throws Exception {
		this.importaciones = new ArrayList<MyArray>();
		long idArticulo = this.selectedItem.getId();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (ImportacionPedidoCompra imp : rr.getImportacionesEnCurso()) {
			for (ImportacionPedidoCompraDetalle det : imp.getImportacionPedidoCompraDetalle()) {
				if (det.getArticulo().getId().longValue() == idArticulo) {
					MyArray my = new MyArray();
					my.setPos1(imp.getNumeroPedidoCompra());
					my.setPos2(det.getCantidad());
					this.importaciones.add(my);
				}
			}
		}
	}
	
	/**
	 * obtiene las ubicaciones..
	 */
	private void obtenerUbicaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idArticulo = this.selectedItem.getId();
		List<ArticuloUbicacion> list = rr.getUbicacion(idArticulo);
		this.ubicaciones = list;
	}
	
	@DependsOn({ "codInterno", "codOriginal", "codProveedor", "descripcion", "marca", "filterStock", "familia", "proveedor" })
	public List<MyArray> getArticulos() throws Exception {
		
		if (this.codInterno.trim().isEmpty() && this.codOriginal.trim().isEmpty() && this.codProveedor.trim().isEmpty()
				&& this.descripcion.trim().isEmpty() && this.marca.trim().isEmpty() && this.familia.trim().isEmpty()
				&& this.proveedor.trim().isEmpty()) {
			this.setSelectedItem(null);
			return new ArrayList<MyArray>();
		}
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> arts = rr.getArticulos_(this.codInterno,
				this.codOriginal, this.codProveedor, this.descripcion, this.marca, this.familia, this.proveedor, "");
		
		List<Object[]> arts_ = new ArrayList<>();
		
		if (this.filterStock > 0) {
			for (Object[] art : arts) {
				long stock = rr.getStockArticulo((long) art[0]);				
				if (stock >= this.filterStock) {
					arts_.add(art);
				}
			}
		} else {
			arts_.addAll(arts);
		}
		
		List<MyArray> out = this.articulosToMyArray(arts_);		
		if (out.size() > 0) {
			this.setSelectedItem(null);
		}
		
		return out;
	}
	
	/**
	 * @return los articulos convertidos a myarray..
	 */
	private List<MyArray> articulosToMyArray(List<Object[]> arts) throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		for (Object[] art : arts) {
			MyArray my = new MyArray();
			my.setId((Long) art[0]);
			my.setPos1(art[1]);
			my.setPos2(art[2]);
			my.setPos3(art[3]);
			my.setPos4(art[4]);	
			my.setPos6(art[5]);
			my.setPos7(art[6]);
			my.setPos8(art[7]);
			my.setPos9((boolean) art[9] ? "ACTIVO" : "INACTIVO");
			List<MyArray> ubics = new ArrayList<MyArray>();
			my.setPos5(ubics);
			out.add(my);
		}
		return out;
	}
	
	/**
	 * @return los precios del articulo..
	 */
	private List<MyArray> getPrecios(long idArticulo, long idDeposito) throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		
		for (MyArray precio : this.getListasDePrecio()) {
			double precioGs = this.getPrecioVenta(idArticulo, precio.getId());
			MyArray my = new MyArray();
			my.setPos1(precio.getPos1());
			my.setPos2(precioGs);
			my.setPos3(precioGs);
			my.setPos4(precioGs);
			my.setPos5(Utiles.getNumberFormat(precioGs));
			out.add(my);
		}		
		return out;
	}
	
	/**
	 * @return los precios de baterias..
	 */
	private List<MyArray> getPreciosBaterias(String codArticulo, long idDeposito) throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		
		for (MyArray item : this.getListasDePrecio()) {
			ArticuloListaPrecioDetalle det = rr.getListaPrecioDetalle(item.getId(), codArticulo);
			String formula = (String) item.getPos3();
			double precioGs = 0;
			String precioGs_ = "0";
			if (det != null && formula == null) {
				precioGs = det.getPrecioGs_contado();
				String contado = Utiles.getNumberFormat(precioGs);
				String credito = Utiles.getNumberFormat(det.getPrecioGs_credito());
				precioGs_ = (det.isCreditoContado()? contado : "CON: " + contado + " - CRE: " + credito);
			} else {
				
				// formula lista precio mayorista..
				if (item.getId().longValue() == 2 && formula != null) {
					ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
					ArticuloListaPrecioDetalle precioDet = rr.getListaPrecioDetalle(distribuidor.getId(), codArticulo);
					if (precioDet != null) {
						double cont = precioDet.getPrecioGs_contado();
						double cred = precioDet.getPrecioGs_credito();
						double formulaCont = cont + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_contado(), 10);
						double formulaCred = cred + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_credito(), 10);						
						precioGs = formulaCont;
						String contado = Utiles.getNumberFormat(precioGs);
						String credito = Utiles.getNumberFormat(formulaCred);
						precioGs_ = (precioDet.isCreditoContado()? contado : "CON: " + contado + " - CRE: " + credito);
					} else {
						int margen = distribuidor.getMargen();
						double precio = ControlArticuloCosto.getPrecioVenta(this.getCostoGs(), margen);
						double formula_ = precio + Utiles.obtenerValorDelPorcentaje(precioGs, 10);
						precioGs = formula_;
						precioGs_ = Utiles.getNumberFormat(formula_);
					}
				
				// formula lista precio minorista..
				} else if (item.getId().longValue() == 3 && formula != null) {
					ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
					ArticuloListaPrecioDetalle precioDet = rr.getListaPrecioDetalle(distribuidor.getId(), codArticulo);
					if (precioDet != null) {
						double cont = precioDet.getPrecioGs_contado() + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_contado(), 10);
						double cred = precioDet.getPrecioGs_credito() + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_credito(), 10);
						double formulaCont = (cont * 1.18) / 0.8;
						double formulaCred = (cred * 1.18) / 0.8;
						precioGs = formulaCont;
						String contado = Utiles.getNumberFormat(precioGs);
						String credito = Utiles.getNumberFormat(formulaCred);
						precioGs_ = (precioDet.isCreditoContado()? contado : "CON: " + contado + " - CRE: " + credito);
					} else {
						int margen = distribuidor.getMargen();
						double precio = ControlArticuloCosto.getPrecioVenta(this.getCostoGs(), margen);
						double formula_ = ((precioGs + Utiles.obtenerValorDelPorcentaje(precio, 10)) * 1.18) / 0.8;
						precioGs = formula_;
						precioGs_ = Utiles.getNumberFormat(formula_);
					}				
				// formula lista precio transportadora..
				} else if (item.getId().longValue() == 7 && formula != null) {
						ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
						ArticuloListaPrecioDetalle precioDet = rr.getListaPrecioDetalle(distribuidor.getId(), codArticulo);
						if (precioDet != null) {
							double cont = precioDet.getPrecioGs_contado();
							double cred = precioDet.getPrecioGs_credito();
							double formulaCont = cont + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_contado(), 15);
							double formulaCred = cred + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_credito(), 15);						
							precioGs = formulaCont;
							String contado = Utiles.getNumberFormat(precioGs);
							String credito = Utiles.getNumberFormat(formulaCred);
							precioGs_ = (precioDet.isCreditoContado()? contado : "CON: " + contado + " - CRE: " + credito);
						} else {
							int margen = distribuidor.getMargen();
							double precio = ControlArticuloCosto.getPrecioVenta(this.getCostoGs(), margen);
							double formula_ = precio + Utiles.obtenerValorDelPorcentaje(precioGs, 15);
							precioGs = formula_;
							precioGs_ = Utiles.getNumberFormat(formula_);
						}
				} else {
					precioGs = this.getPrecioVenta(this.getCostoGs(), (int) item.getPos2());
					precioGs_ = Utiles.getNumberFormat(precioGs);
				}				
				
			}
			
			MyArray my = new MyArray();
			my.setPos1(item.getPos1());
			my.setPos2(precioGs);
			my.setPos3(my.getPos2());
			my.setPos4(my.getPos2());
			my.setPos5(precioGs_);
			out.add(my);
		}
		
		return out;
	}
	
	/**
	 * @return el costo del articulo segun el ArticuloDeposito..
	 * [0]: costo
	 * [1]: ivaIncluido
	 */
	private Object[] getCostoArticulo(long idArticulo, long idDeposito) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloById(idArticulo);
		return new Object[] { art.getCostoGs(), false };
	}
	
	/**
	 * el precio de venta segun su margen..
	 */
	private double getPrecioVenta(double costo, int margen)
			throws Exception {
		return ControlArticuloCosto.getPrecioVenta(costo, margen);
	}
	
	/**
	 * el precio de venta almacenado en el articulo..
	 */
	private double getPrecioVenta(long idArticulo, long idListaPrecio)
			throws Exception {
		return ControlArticuloCosto.getPrecioVenta_(idArticulo, idListaPrecio);
	}
	
	/**
	 * @return las listas de precio..
	 * pos1:descripcion
	 * pos2:margen
	 */
	private List<MyArray> getListasDePrecio() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloListaPrecio> precios = rr.getListasDePrecio();
		List<MyArray> out = new ArrayList<MyArray>();
		for (ArticuloListaPrecio precio : precios) {
			MyArray mprecio = new MyArray(precio.getDescripcion(), precio.getMargen(), precio.getFormula());
			mprecio.setId(precio.getId());
			out.add(mprecio);
			if (!this.getLoginNombre().equals("login")) {
				if (!this.isOperacionHabilitada(Permisos.VER_PRECIO_MAYORISTA)) {
					if (precio.getDescripcion().contains("MAYORISTA")) {
						out.remove(mprecio);
					}
				}
				if (!this.isOperacionHabilitada(Permisos.VER_PRECIO_BATERIAS)) {
					if (precio.getDescripcion().contains("BATERIAS")) {
						out.remove(mprecio);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * inicializa los precios y existencia..
	 */
	private void reloadValues() {
		this.precios = null;
		this.existencia = null;
		this.importaciones = new ArrayList<MyArray>();
		this.precioDescontado = 0;
		this.selectedCliente = null;
		BindUtils.postNotifyChange(null, null, this, "precios");
		BindUtils.postNotifyChange(null, null, this, "existencia");
		BindUtils.postNotifyChange(null, null, this, "importaciones");
		BindUtils.postNotifyChange(null, null, this, "precioDescontado");
		BindUtils.postNotifyChange(null, null, this, "selectedCliente");
	}
	
	/**
	 * recupera el historico de movimientos del articulo..
	 */
	private void loadHistorico_() throws Exception {
		if (this.selectedItem == null) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> ventas = rr.getVentasPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> transfsOrigenMRA = rr.getTransferenciasPorArticuloOrigenMRA(this.selectedItem.getId(), this.desde, this.hasta, false);
		List<Object[]> transfsOrigenCentral = rr.getTransferenciasPorArticuloOrigenCentral(this.selectedItem.getId(), this.desde, this.hasta, false);
		List<Object[]> transfsDestinoMRA = rr.getTransferenciasPorArticuloDestinoMRA(this.selectedItem.getId(), this.desde, this.hasta, false);
		List<Object[]> transfsDestinoCentral = rr.getTransferenciasPorArticuloDestinoCentral(this.selectedItem.getId(), this.desde, this.hasta, false);
		List<Object[]> transfsOrigenDifInventarioMRA = rr.getTransferenciasPorArticuloOrigenDiferenciaInvMRA2019(this.selectedItem.getId(), desde, hasta, false);
		List<Object[]> transfsDestinoDifInventario = rr.getTransferenciasPorArticuloDestinoDiferenciaInv2019(this.selectedItem.getId(), desde, hasta, false);	
		List<Object[]> transfsDestinoDifInventarioMRA = rr.getTransferenciasPorArticuloDestinoDiferenciaInvMRA2019(this.selectedItem.getId(), desde, hasta, false);
		List<Object[]> transfsOrigenDifInventario = rr.getTransferenciasPorArticuloOrigenDiferenciaInv2019(this.selectedItem.getId(), desde, hasta, false);
		List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
		List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
		List<Object[]> migracion = rr.getMigracionPorArticulo((String) this.selectedItem.getPos1(), this.desde, this.hasta, 0);
		List<Object[]> usados = rr.getVentasPedidoUsados(this.selectedItem.getId(), this.desde, this.hasta, false);
		
		this.historicoEntrada = new ArrayList<Object[]>();
		this.historicoEntrada.addAll(migracion);
		this.historicoEntrada.addAll(ajustStockPost);		
		this.historicoEntrada.addAll(ntcsv);
		this.historicoEntrada.addAll(compras);
		this.historicoEntrada.addAll(importaciones);
		if (!this.isEmpresaGTSA()) {
			this.historicoEntrada.addAll(this.isEmpresaMRA() ? transfsOrigenCentral : transfsOrigenMRA);
			this.historicoEntrada.addAll(this.isEmpresaMRA() ? transfsOrigenDifInventarioMRA : transfsOrigenDifInventario);
		}
		if (this.selectedItem.getPos7().toString().contains("USADAS")) {
			this.historicoEntrada.addAll(usados);
		}
		
		this.historicoSalida = new ArrayList<Object[]>();
		this.historicoSalida.addAll(ajustStockNeg);
		this.historicoSalida.addAll(ventas);
		this.historicoSalida.addAll(ntcsc);
		if (!this.isEmpresaGTSA()) {
			this.historicoSalida.addAll(this.isEmpresaMRA() ? transfsDestinoDifInventarioMRA : transfsDestinoDifInventario);
			this.historicoSalida.addAll(this.isEmpresaMRA() ? transfsDestinoCentral : transfsDestinoMRA);
		}
		
		this.actualizarTotal(this.historicoEntrada, true);
		this.actualizarTotal(this.historicoSalida, false);
		
		// ordena la lista segun fecha..
		Collections.sort(this.historicoEntrada, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha2.compareTo(fecha1);
			}
		});
		
		// ordena la lista segun fecha..
		Collections.sort(this.historicoSalida, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha2.compareTo(fecha1);
			}
		});
		
		BindUtils.postNotifyChange(null, null, this, "historicoEntrada");
		BindUtils.postNotifyChange(null, null, this, "historicoSalida");
		BindUtils.postNotifyChange(null, null, this, "stockEntrada");
		BindUtils.postNotifyChange(null, null, this, "stockSalida");
	}
	
	/**
	 * load historico por monto..
	 */
	private void loadHistoricoPorMonto_() throws Exception {
		if (this.selectedItem == null) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> ventas = rr.getVentasPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> transfsOrigenMRA = rr.getTransferenciasPorArticuloOrigenMRA(this.selectedItem.getId(), this.desde, this.hasta, false);
		List<Object[]> transfsOrigenCentral = rr.getTransferenciasPorArticuloOrigenCentral(this.selectedItem.getId(), this.desde, this.hasta, false);
		List<Object[]> transfsDestinoMRA = rr.getTransferenciasPorArticuloDestinoMRA(this.selectedItem.getId(), this.desde, this.hasta, false);
		List<Object[]> transfsDestinoCentral = rr.getTransferenciasPorArticuloDestinoCentral(this.selectedItem.getId(), this.desde, this.hasta, false);
		List<Object[]> transfsOrigenDifInventarioMRA = rr.getTransferenciasPorArticuloOrigenDiferenciaInvMRA2019(this.selectedItem.getId(), desde, hasta, false);
		List<Object[]> transfsDestinoDifInventario = rr.getTransferenciasPorArticuloDestinoDiferenciaInv2019(this.selectedItem.getId(), desde, hasta, false);	
		List<Object[]> transfsDestinoDifInventarioMRA = rr.getTransferenciasPorArticuloDestinoDiferenciaInvMRA2019(this.selectedItem.getId(), desde, hasta, false);
		List<Object[]> transfsOrigenDifInventario = rr.getTransferenciasPorArticuloOrigenDiferenciaInv2019(this.selectedItem.getId(), desde, hasta, false);
		List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
		List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
		List<Object[]> migracion = rr.getMigracionPorArticulo((String) this.selectedItem.getPos1(), this.desde, this.hasta, 0);
		
		List<Object[]> historico = new ArrayList<Object[]>();
		historico.addAll(migracion);
		historico.addAll(ajustStockPost);		
		historico.addAll(ntcsv);
		historico.addAll(compras);
		historico.addAll(importaciones);
		if (!this.isEmpresaGTSA()) {
			historico.addAll(this.isEmpresaMRA() ? transfsOrigenCentral : transfsOrigenMRA);
			historico.addAll(this.isEmpresaMRA() ? transfsOrigenDifInventarioMRA : transfsOrigenDifInventario);
		}
		historico.addAll(ajustStockNeg);
		historico.addAll(ventas);
		historico.addAll(ntcsc);
		if (!this.isEmpresaGTSA()) {
			historico.addAll(this.isEmpresaMRA() ? transfsDestinoDifInventarioMRA : transfsDestinoDifInventario);
			historico.addAll(this.isEmpresaMRA() ? transfsDestinoCentral : transfsDestinoMRA);
		}
		
		this.historicoEntrada = new ArrayList<Object[]>();		
		this.historicoSalida = new ArrayList<Object[]>();
		
		for (Object[] movim : ntcsv) {
			double monto = (double) movim[4];
			movim[4] = monto * -1;
		}
		
		for (Object[] movim : ajustStockNeg) {
			double monto = (double) movim[4];
			movim[4] = monto * -1;
		}
		
		for (Object[] movim : historico) {
			double monto = (double) movim[4];
			if (monto < 0) {
				this.historicoSalida.add(movim);
			} else {
				this.historicoEntrada.add(movim);
			}
		}
		
		this.actualizarTotalMonto(this.historicoEntrada, true);
		this.actualizarTotalMonto(this.historicoSalida, false);
		
		// ordena la lista segun fecha..
		Collections.sort(this.historicoEntrada, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha2.compareTo(fecha1);
			}
		});
		
		// ordena la lista segun fecha..
		Collections.sort(this.historicoSalida, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha2.compareTo(fecha1);
			}
		});
		
		BindUtils.postNotifyChange(null, null, this, "historicoEntrada");
		BindUtils.postNotifyChange(null, null, this, "historicoSalida");
		BindUtils.postNotifyChange(null, null, this, "montosPositivos");
		BindUtils.postNotifyChange(null, null, this, "montosNegativos");
	}
	
	/**
	 * suma los totales de stock en cada mov de art.
	 */
	public void actualizarTotal(List<Object[]> historico, boolean entrada) {
		if(entrada){
			this.stockEntrada = 0;
			for(Object[] obj : historico){
				this.stockEntrada += Long.parseLong(String.valueOf(obj[3]));
			}
		} else {
			this.stockSalida = 0;
			for(Object[] obj : historico){
				long cantidad = Long.parseLong(String.valueOf(obj[3]));
				if(cantidad < 0)
					cantidad = cantidad * -1;
				this.stockSalida += cantidad;
			}
		}		
	}
	
	/**
	 * suma los totales de stock en cada mov de art.
	 */
	public void actualizarTotalMonto(List<Object[]> historico, boolean entrada) {
		if (entrada) {
			this.montosPositivos = 0;
			for (Object[] obj : historico) {
				this.montosPositivos += (Long.parseLong(String.valueOf(obj[3]))
						* Double.parseDouble(String.valueOf(obj[4])));
			}
		} else {
			this.montosNegativos = 0;
			for (Object[] obj : historico) {
				long cantidad = Long.parseLong(String.valueOf(obj[3]));
				if(cantidad < 0)
					cantidad = cantidad * -1;
				this.montosNegativos -= (cantidad
						* Double.parseDouble(String.valueOf(obj[4])));
			}
		}
	}
	
	/**
	 * @return los precios del articulo..
	 */
	private List<MyArray> getExistencia(long idArticulo) throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		this.stock = 0;
		
		for (MyPair dep : this.getDepositos()) {
			MyArray my = new MyArray();
			my.setPos1(dep.getText());
			my.setPos2(this.getStock(idArticulo, dep.getId()));
			out.add(my);
			this.stock += (long) my.getPos2();
		}		
		return out;
	}
	
	/**
	 * @return el stock del articulo..
	 */
	private long getStock(long idArticulo, long idDeposito) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloDeposito adp = rr.getArticuloDeposito(idArticulo, idDeposito);
		if(adp == null)
			return 0;
		return adp.getStock();
	}
	
	/**
	 * @return los depositos de la sucursal..
	 */
	private List<MyPair> getDepositos() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		
		if (this.isEmpresaGTSA()) {
			List<Deposito> deps = rr.getDepositosPorSucursal(ID_SUC_PRINCIPAL);
			for (Deposito dep : deps) {
				out.add(new MyPair(dep.getId(), dep.getDescripcion()));
			}
		} else {
			List<Deposito> deps = rr.getDepositos();
			for (Deposito dep : deps) {
				if (dep.getId().longValue() != Deposito.ID_MRA_TEMPORAL) {
					out.add(new MyPair(dep.getId(), dep.getDescripcion()));
				}
			}
		}		
		return out;
	}

	/**
	 * GET / SET
	 */	
	@DependsOn("selectedItem")
	public String getUltimaCompra() {	
		if (this.selectedItem == null)
			return "";
		RegisterDomain rr = RegisterDomain.getInstance();	
		Object[] ultcompra = null;
		try {
			ultcompra = rr.getUltimoCosto(this.selectedItem.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ultcompra == null ? "" : this.m.dateToString(
				(Date) ultcompra[0], Misc.DD_MM_YYYY)
				+ " - " + (String) ultcompra[1]
				+ " - " + FORMATTER.format((double) ultcompra[2]) + " Gs.";
	}
	
	@DependsOn("selectedItem")
	public double getPrecioJedisoft() throws Exception {
		if (this.selectedItem == null)
			return 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloPrecioJedisoft artp = rr
				.getPrecioJedisoft((String) this.selectedItem.getPos1());
		if (artp != null)
			return artp.getPrecio();
		return 0;
	}
	
	@DependsOn("selectedItem")
	public double getCostoGs() throws Exception {
		if (this.selectedItem == null) {
			return 0;
		}
		Object[] costoInf = this.getCostoArticulo(this.selectedItem.getId(), ID_DEP_1);
		double costo = (double) costoInf[0];
		return costo;
	}
	
	@DependsOn("selectedItem")
	public double getCostoPromedioGs() throws Exception {
		if (this.selectedItem == null) {
			return 0.0;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		double out = rr.getCostoPromedioGs(this.selectedItem.getId());
		return out > 0.0 ? out : this.getCostoGs();
	}
	
	@DependsOn("selectedItem")
	public String getUrlBarcode() {
		if (this.selectedItem == null) return "";
		String codigo = (String) this.selectedItem.getPos1();
		return "/yhaguy/archivos/barcodes/" + codigo.replace("/", "-") + ".pdf";
	}
	
	@DependsOn({ "filter_razonsocial", "filter_ruc" })
	public List<Object[]> getClientes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getClientesConDescuento(this.filter_razonsocial, this.filter_ruc);
	}
	
	/**
	 * @return los estados..
	 */
	public List<String> getEstados() {
		List<String> out = new ArrayList<String>();
		out.add("ACTIVO");
		out.add("INACTIVO");
		return out;
	}
	
	/**
	 * @return el acceso..
	 */
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}
	
	/**
	 * @return si la operacion es habilitada..
	 */
	public boolean isOperacionHabilitada(String operacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.isOperacionHabilitada(this.getLoginNombre(), operacion);
	}
	
	/**
	 * @return true si es baterias..
	 */
	public boolean isEmpresaGTSA() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA);
	}
	
	/**
	 * @return true si es mra..
	 */
	public boolean isEmpresaMRA() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_YMRA);
	}
	
	public String getSiglaEstadoAnulado() {
		return Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO;
	}
	
	public String getCodInterno() {
		return codInterno;
	}

	public void setCodInterno(String codInterno) {
		this.codInterno = codInterno;
	}

	public String getCodOriginal() {
		return codOriginal;
	}

	public void setCodOriginal(String codOriginal) {
		this.codOriginal = codOriginal;
	}

	public String getCodProveedor() {
		return codProveedor;
	}

	public void setCodProveedor(String codProveedor) {
		this.codProveedor = codProveedor;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
		this.reloadValues();
	}

	public void setPrecios(List<MyArray> precios) {
		this.precios = precios;
	}

	public List<MyArray> getPrecios() {
		return precios;
	}
	
	@DependsOn("precios")
	public List<MyArray> getPreciosMobile() {
		List<MyArray> out = new ArrayList<MyArray>();
		if (this.precios != null && this.precios.size() > 0) {
			out.add(this.precios.get(0));
			if (this.precios.size() > 1) {
				out.add(this.precios.get(1));
				if (this.precios.size() > 2) {
					out.add(this.precios.get(2));
				}
			}
		}		
		return out;
	}

	public List<MyArray> getExistencia() {
		return existencia;
	}

	public void setExistencia(List<MyArray> existencia) {
		this.existencia = existencia;
	}

	public MyArray getSelectedPrecio() {
		return selectedPrecio;
	}

	public void setSelectedPrecio(MyArray selectedPrecio) {
		this.selectedPrecio = selectedPrecio;
	}

	public int getCalcPorcentaje() {
		return calcPorcentaje;
	}

	public void setCalcPorcentaje(int calcPorcentaje) {
		this.calcPorcentaje = calcPorcentaje;
	}

	public int getCalcPorcentaje_() {
		return calcPorcentaje_;
	}

	public void setCalcPorcentaje_(int calcPorcentaje_) {
		this.calcPorcentaje_ = calcPorcentaje_;
	}

	public List<Object[]> getHistoricoEntrada() {
		return historicoEntrada;
	}

	public void setHistoricoEntrada(List<Object[]> historico) {
		this.historicoEntrada = historico;
	}

	public List<Object[]> getHistoricoSalida() {
		return historicoSalida;
	}

	public void setHistoricoSalida(List<Object[]> historicoOUT) {
		this.historicoSalida = historicoOUT;
	}

	public long getStockSalida() {
		return stockSalida;
	}

	public void setStockSalida(long stockSalida) {
		this.stockSalida = stockSalida;
	}

	public long getStockEntrada() {
		return stockEntrada;
	}

	public void setStockEntrada(long stockEntrada) {
		this.stockEntrada = stockEntrada;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public List<MyArray> getImportaciones() {
		return importaciones;
	}

	public void setImportaciones(List<MyArray> importaciones) {
		this.importaciones = importaciones;
	}

	public long getStock() {
		return stock;
	}

	public void setStock(long stock) {
		this.stock = stock;
	}

	public String getFilter_ruc() {
		return filter_ruc;
	}

	public void setFilter_ruc(String filter_ruc) {
		this.filter_ruc = filter_ruc;
	}

	public String getFilter_razonsocial() {
		return filter_razonsocial;
	}

	public void setFilter_razonsocial(String filter_razonsocial) {
		this.filter_razonsocial = filter_razonsocial;
	}

	public double getPrecioDescontado() {
		return precioDescontado;
	}

	public void setPrecioDescontado(double precioDescontado) {
		this.precioDescontado = precioDescontado;
	}

	public Object[] getSelectedCliente() {
		return selectedCliente;
	}

	public void setSelectedCliente(Object[] selectedCliente) {
		this.selectedCliente = selectedCliente;
	}

	public List<ArticuloUbicacion> getUbicaciones() {
		return ubicaciones;
	}

	public void setUbicaciones(List<ArticuloUbicacion> ubicaciones) {
		this.ubicaciones = ubicaciones;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public int getFilterStock() {
		return filterStock;
	}

	public void setFilterStock(int filterStock) {
		this.filterStock = filterStock;
	}

	public String getFamilia() {
		return familia;
	}

	public void setFamilia(String familia) {
		this.familia = familia;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public double getMontosPositivos() {
		return montosPositivos;
	}

	public void setMontosPositivos(double montosPositivos) {
		this.montosPositivos = montosPositivos;
	}

	public double getMontosNegativos() {
		return montosNegativos;
	}

	public void setMontosNegativos(double montosNegativos) {
		this.montosNegativos = montosNegativos;
	}
}
