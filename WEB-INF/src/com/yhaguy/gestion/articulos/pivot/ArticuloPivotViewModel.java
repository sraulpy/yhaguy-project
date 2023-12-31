package com.yhaguy.gestion.articulos.pivot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
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
import com.yhaguy.domain.ArticuloPivot;
import com.yhaguy.domain.ArticuloPivotDetalle;
import com.yhaguy.domain.ArticuloPrecioJedisoft;
import com.yhaguy.domain.ArticuloUbicacion;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.util.Utiles;

public class ArticuloPivotViewModel extends SimpleViewModel {
	
	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	
	static final String ZUL_ORDEN_COMPRA = "/yhaguy/gestion/articulos/articulo_pivot_compra.zul";	
	
	public static final long ID_SUC_PRINCIPAL = 2;
	public static final long ID_DEP_1 = Configuracion.ID_DEPOSITO_PRINCIPAL;

	private String codInterno = "";
	private String codOriginal = "";
	private String codProveedor = "";
	private String descripcion = "";
	private String marca = "";
	private String familia = "";
	private String proveedor = "";
	private String origen = "";
	
	private String filter_razonsocial = "";
	private String filter_ruc = "";
	private String filter_codInterno = "";
	private String filter_codOriginal = "";
	private String filter_stock = "";
	private String filter_kpi = "";
	
	private List<ArticuloUbicacion> ubicaciones = new ArrayList<ArticuloUbicacion>();
	
	private MyArray selectedItem;	
	private MyArray selectedPrecio;
	private List<MyArray> precios;
	private List<MyArray> existencia;
	private List<MyArray> importaciones = new ArrayList<MyArray>();
	private List<Object[]> historicoEntrada;
	private List<Object[]> historicoSalida;
	
	private Object[] selectedMovimiento;
	private List<Object[]> abastecimiento = new ArrayList<Object[]>();
	private List<ArticuloPivot> compras = new ArrayList<ArticuloPivot>();
	private ArticuloPivot selectedCompra;
	private List<Funcionario> selectedVendedores = new ArrayList<Funcionario>();
	
	private int calcPorcentaje = 0;
	private int calcPorcentaje_ = 0;
	
	private long stockSalida = 0;
	private long stockEntrada = 0;
	private long stock = 0;
	
	private Object[] selectedCliente;
	private double precioDescontado = 0;
	
	private Date desde;
	private Date hasta = new Date();
	
	private Date desde_;
	private Date hasta_ = new Date();
	private SucursalApp selectedSucursal;
	
	private int filterStock = 0;
	
	private long totalVentas = 0;
	private long totalStock = 0;
	private long totalResto = 0;
	
	@Wire
	private Popup pop_img;
	
	@Wire
	private Popup pop_barcode;
	
	private Window win;
	
	@Init(superclass = true)
	public void init() {
		try {
			String desde="05-10-2018 00:00:00";
			DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			this.desde = formatter.parse(desde);
			this.desde_ = Utiles.agregarDias(new Date(), -1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void AfterCompose() {
	}
	
	@Wire
	private Popup historial;
	
	@Wire
	private Listbox listArt;
	
	@Command
	@NotifyChange({"precios", "existencia", "importaciones", "stock", "ubicaciones"})
	public void obtenerValores() throws Exception {
		this.obtenerPrecio();
		this.obtenerExistencia();
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
		this.historial.open(listArt, "overlap");
	}
	
	@Command
	public void refreshHistorico() throws Exception {
		this.loadHistorico_();
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
	@NotifyChange("codInterno")
	public void setFiltro(@BindingParam("codigo") String codigo) {
		this.cleanFiltros();
		this.codInterno = codigo;
	}
	
	@Command
	@NotifyChange("selectedMovimiento")
	public void selectItem(@BindingParam("item") Object[] item, @BindingParam("comp") Intbox comp) {
		this.selectedMovimiento = item;
		this.selectedMovimiento[7] = (int) item[2] - (long) item[5];
		this.selectedMovimiento[8] = (int) item[2] - ((long) item[5] + (long) item[6]) > 0 ? true : false; 
		this.selectedMovimiento[9] = (int) item[2] - ((long) item[5] + (long) item[6]) > 0 ? false : true; 
		comp.focus();
	}
	
	@Command
	@NotifyChange("abastecimiento")
	public void addItem(@BindingParam("comp") Component comp) {
		for (Object[] item : this.abastecimiento) {
			String cod = (String) item[0];
			String cod_ = (String) this.selectedMovimiento[0];
			if (cod.equals(cod_)) {
				Clients.showNotification("el item ya fue agregado..", Clients.NOTIFICATION_TYPE_ERROR, comp, "after_end", 0);
				return;
			}
		}
		this.abastecimiento.add(this.selectedMovimiento);
	}
	
	@Command
	@NotifyChange("filter_kpi")
	public void refreshKPI() {
	}
	
	@Command
	@NotifyChange("compras")
	public void generarCompra() throws Exception {
		this.compras.clear();
		this.selectedCompra = null;
		RegisterDomain rr = RegisterDomain.getInstance();
		Map<Long, Long> proveedores = new HashMap<Long, Long>();
		for (Object[] item : this.abastecimiento) {
			if ((boolean) item[8]) {
				long idprov = (long) item[4];
				proveedores.put(idprov, idprov);
			}			
		}
		for (Long idprov : proveedores.keySet()) {
			Proveedor prov = rr.getProveedorById(idprov);
			ArticuloPivot ap = new ArticuloPivot();
			ap.setFecha(new Date());
			ap.setConcepto(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ORDEN_COMPRA).getDescripcion());
			ap.setProveedor(prov);
			ap.setUsuario(this.getLoginNombre());
			for (Object[] item : this.abastecimiento) {
				if ((boolean) item[8]) {
					long idprov_ = (long) item[4];
					if (idprov_ == idprov.longValue()) {
						Articulo art = rr.getArticulo((String) item[0]);
						ArticuloPivotDetalle det = new ArticuloPivotDetalle();
						det.setArticulo(art);
						det.setCantidad(Integer.parseInt(item[7] + ""));
						ap.getDetalles().add(det);
					}
				}
			}
			this.compras.add(ap);
		}	
		this.win = (Window) Executions.createComponents(ZUL_ORDEN_COMPRA, this.mainComponent, null);
		this.win.doOverlapped();
	}
	
	@Command
	@NotifyChange("compras")
	public void generarTransferencia() throws Exception {
		this.compras.clear();
		this.selectedCompra = null;
		RegisterDomain rr = RegisterDomain.getInstance();
		Proveedor prov = rr.getProveedorById(Configuracion.ID_PROVEEDOR_YHAGUY);
		ArticuloPivot ap = new ArticuloPivot();
		ap.setFecha(new Date());
		ap.setConcepto(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_TRANS_MERCADERIA).getDescripcion());
		ap.setProveedor(prov);
		ap.setUsuario(this.getLoginNombre());
		for (Object[] item : this.abastecimiento) {
			if ((boolean) item[9]) {
				Articulo art = rr.getArticulo((String) item[0]);
				ArticuloPivotDetalle det = new ArticuloPivotDetalle();
				det.setArticulo(art);
				det.setCantidad(Integer.parseInt(item[7] + ""));
				ap.getDetalles().add(det);			
			}
		}
		this.compras.add(ap);	
		this.win = (Window) Executions.createComponents(ZUL_ORDEN_COMPRA, this.mainComponent, null);
		this.win.doOverlapped();
	}
	
	@Command
	public void generarCompras_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (ArticuloPivot compra : this.compras) {
			rr.saveObject(compra, this.getLoginNombre());
		}
		this.win.detach();
		Clients.showNotification("ORDENES DE COMPRA GENERADAS..");
	}
	
	@Command
	@NotifyChange("abastecimiento")
	public void deleteItem(@BindingParam("item") Object[] item) {
		this.abastecimiento.remove(item);
	}
	
	/**
	 * @return la url de la foto.
	 */
	private String getUrlImagen() {
		if (this.selectedItem == null)
			return "http://190.211.240.30/images/default.png";
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "articulos/" + this.selectedItem.getId() + ".png";
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
	 * obtiene las ubicaciones..
	 */
	private void obtenerUbicaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idArticulo = this.selectedItem.getId();
		List<ArticuloUbicacion> list = rr.getUbicacion(idArticulo);
		this.ubicaciones = list;
	}
	
	@DependsOn({ "codInterno", "codOriginal", "codProveedor", "descripcion", "marca", "filterStock", "familia", "proveedor", "origen" })
	public List<MyArray> getArticulos() throws Exception {
		
		if (this.codInterno.trim().isEmpty() && this.codOriginal.trim().isEmpty() && this.codProveedor.trim().isEmpty()
				&& this.descripcion.trim().isEmpty() && this.marca.trim().isEmpty() && this.familia.trim().isEmpty()
				&& this.proveedor.trim().isEmpty() && this.origen.trim().isEmpty()) {
			this.setSelectedItem(null);
			return new ArrayList<MyArray>();
		}
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> arts = rr.getArticulos_(this.codInterno,
				this.codOriginal, this.codProveedor, this.descripcion, this.marca, this.familia, this.proveedor, this.origen);
		
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
			my.setPos9(art[8]);
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
		List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> transfs = rr.getTransferenciasPorArticulo(this.selectedItem.getId(), this.desde, this.hasta);
		List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
		List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(this.selectedItem.getId(), this.desde, this.hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
		List<Object[]> migracion = rr.getMigracionPorArticulo((String) this.selectedItem.getPos1(), this.desde, this.hasta, 0);
		
		this.historicoEntrada = new ArrayList<Object[]>();
		this.historicoEntrada.addAll(migracion);
		this.historicoEntrada.addAll(ajustStockPost);		
		this.historicoEntrada.addAll(ntcsv);
		this.historicoEntrada.addAll(compras);
		this.historicoEntrada.addAll(importaciones);
		
		this.historicoSalida = new ArrayList<Object[]>();
		this.historicoSalida.addAll(ajustStockNeg);
		this.historicoSalida.addAll(ventas);
		this.historicoSalida.addAll(ntcsc);
		this.historicoSalida.addAll(transfs);
		
		/** for (Object[] transf : transfs) {
			long idsuc = (long) transf[6];
			if(idsuc == ID_SUC_PRINCIPAL) {
				
			} else {				
				this.historicoEntrada.add(transf);
			}
		} **/
		
		this.actualizarTotal(this.historicoEntrada, true);
		this.actualizarTotal(this.historicoSalida, false);
		
		BindUtils.postNotifyChange(null, null, this, "historicoEntrada");
		BindUtils.postNotifyChange(null, null, this, "historicoSalida");
		BindUtils.postNotifyChange(null, null, this, "stockEntrada");
		BindUtils.postNotifyChange(null, null, this, "stockSalida");
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
		
		if (this.isSucursalBaterias()) {
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
	 * @return las sucursales..
	 */
	@SuppressWarnings("unchecked")
	public List<SucursalApp> getSucursales() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<SucursalApp> list = rr.getObjects(SucursalApp.class.getName());
		SucursalApp todos = new SucursalApp();
		todos.setId((long) 0);
		todos.setDescripcion("TODOS");
		list.add(todos);
		return list;
	}
	
	@DependsOn("selectedSucursal")
	private long getIdDeposito() {
		if (this.selectedSucursal != null) {
			if (this.selectedSucursal.getId().longValue() == SucursalApp.ID_MCAL) {
				return Deposito.ID_MCAL_LOPEZ;
			}
		}
		if (this.selectedSucursal != null) {
			if (this.selectedSucursal.getId().longValue() == SucursalApp.ID_CENTRAL) {
				return Deposito.ID_MINORISTA;
			}
		}
		return 0;
	}
	
	@DependsOn("selectedSucursal")
	private Object[] getIdDepositoResto() {
		if (this.selectedSucursal != null) {
			if (this.selectedSucursal.getId().longValue() == SucursalApp.ID_MCAL) {
				return new Object[] { Deposito.ID_MINORISTA, Deposito.ID_MAYORISTA, Deposito.ID_CENTRAL_REPOSICION };
			}
			if (this.selectedSucursal.getId().longValue() == SucursalApp.ID_CENTRAL) {
				return new Object[] { Deposito.ID_MCAL_LOPEZ, Deposito.ID_MAYORISTA, (long) 0 };
			}
			if (this.selectedSucursal.getDescripcion().equals("TODOS")) {
				return new Object[] { Deposito.ID_MCAL_LOPEZ, Deposito.ID_MINORISTA, Deposito.ID_MAYORISTA };
			}
		}
		return new Object[] { (long) 0, (long) 0, (long) 0 };
	}
	
	@DependsOn("selectedSucursal")
	public String getDeposito() {
		if (this.selectedSucursal != null) {
			return " Stock " + this.selectedSucursal.getDescripcion();
		}
		return "";
	}
	
	@DependsOn("selectedSucursal")
	public String getDepositoResto() {
		if (this.selectedSucursal != null) {
			if (this.selectedSucursal.getId().longValue() == SucursalApp.ID_MCAL) {
				return "Stock minorista + mayorista + rep.central";
			}
			if (this.selectedSucursal.getId().longValue() == SucursalApp.ID_CENTRAL) {
				return "Stock mariscal + mayorista";
			}
		}
		return "";
	}
	
	
	/**
	 * @return los movimientos..
	 */
	@DependsOn({ "desde_", "hasta_", "familia", "proveedor", "marca", "selectedSucursal", "filter_codInterno",
			"filter_codOriginal", "filter_stock", "selectedVendedores", "filter_kpi" })
	public List<Object[]> getMovimientos() throws Exception {
		String vendedores = "";
		if (this.selectedVendedores.size() > 0) {
			for (Funcionario vend : this.selectedVendedores) {
				vendedores += vend.getId() + ",";
			}
			vendedores = vendedores.substring(0, vendedores.length() - 1);
		}
		this.totalVentas = 0;
		this.totalResto = 0;
		this.totalStock = 0;
		if (this.selectedSucursal == null) return new ArrayList<Object[]>();
		RegisterDomain rr = RegisterDomain.getInstance();		
		List<Object[]> list = rr.getMovimientosArticulos(this.desde_, this.hasta_, this.familia, this.proveedor,
				this.marca, this.selectedSucursal.getId(), this.filter_codInterno, this.filter_codOriginal, vendedores);
		List<Object[]> list_ = new ArrayList<Object[]>();
		for (Object[] item : list) {
			item = Arrays.copyOf(item, item.length + 5);
			String cod = (String) item[0];
			long idDep = this.getIdDeposito();
			long resto = 0;
			Object[] rest0 = this.getIdDepositoResto();
			Object[] stock = rr.getStockDisponible_(cod, idDep);
			Object[] rest1 = rr.getStockDisponible_(cod, (long) rest0[0]);
			Object[] rest2 = rr.getStockDisponible_(cod, (long) rest0[1]);
			Object[] rest3 = rr.getStockDisponible_(cod, (long) rest0[2]);
			if (rest1 != null) {
				resto += (long) rest1[1];
			}
			if (rest2 != null) {
				resto += (long) rest2[1];
			}
			if (rest3 != null) {
				resto += (long) rest3[1];
			}
			if (stock != null) {
				long stock_ = (long) stock[1];
				item[5] = stock_;
				item[6] = resto;
			} else {
				if (this.selectedSucursal.getDescripcion().equals("TODOS")) {
					item[5] = resto;
					item[6] = resto;
				} else {
					item[5] = (long) 0;
					item[6] = resto;
				}
			}
			item[7] = 0;
			item[8] = false;
			item[9] = false;
			if (!this.filter_stock.isEmpty()) {
				long fstock = Long.parseLong(this.filter_stock);
				long _stock = (long) item[5];
				if (fstock == _stock) {
					list_.add(item);
				}
			} else if (!this.filter_kpi.isEmpty()) {
				String kpi = "";
				if((int) item[2] - (long) item[5] > 0) kpi = "<";
				if((int) item[2] - (long) item[5] < 0) kpi = ">";
				if (this.filter_kpi.equals(kpi)) {
					list_.add(item);
				}
			} else {
				list_.add(item);
			}
		}
		for (Object[] item : list_) {
			this.totalVentas += (long) item[3];
			this.totalStock += (long) item[5];
			this.totalResto += (long) item[6];
		}
		BindUtils.postNotifyChange(null, null, this, "totalVentas");
		BindUtils.postNotifyChange(null, null, this, "totalStock");
		BindUtils.postNotifyChange(null, null, this, "totalResto");
		return list_;
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
	 * @return los vendedores..
	 */
	public List<Funcionario> getVendedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getVendedores();
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
	public boolean isSucursalBaterias() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA);
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
			out.add(this.precios.get(1));
			out.add(this.precios.get(2));
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

	public Date getDesde_() {
		return desde_;
	}

	public void setDesde_(Date desde_) {
		this.desde_ = desde_;
	}

	public Date getHasta_() {
		return hasta_;
	}

	public void setHasta_(Date hasta_) {
		this.hasta_ = hasta_;
	}

	public SucursalApp getSelectedSucursal() {
		return selectedSucursal;
	}

	public void setSelectedSucursal(SucursalApp selectedSucursal) {
		this.selectedSucursal = selectedSucursal;
	}

	public String getFilter_codInterno() {
		return filter_codInterno;
	}

	public void setFilter_codInterno(String filter_codInterno) {
		this.filter_codInterno = filter_codInterno;
	}

	public String getFilter_codOriginal() {
		return filter_codOriginal;
	}

	public void setFilter_codOriginal(String filter_codOriginal) {
		this.filter_codOriginal = filter_codOriginal;
	}

	public Object[] getSelectedMovimiento() {
		return selectedMovimiento;
	}

	public void setSelectedMovimiento(Object[] selectedMovimiento) {
		this.selectedMovimiento = selectedMovimiento;
	}

	public List<Object[]> getAbastecimiento() {
		return abastecimiento;
	}

	public void setAbastecimiento(List<Object[]> abastecimiento) {
		this.abastecimiento = abastecimiento;
	}

	public List<ArticuloPivot> getCompras() {
		return compras;
	}

	public void setCompras(List<ArticuloPivot> compras) {
		this.compras = compras;
	}

	public ArticuloPivot getSelectedCompra() {
		return selectedCompra;
	}

	public void setSelectedCompra(ArticuloPivot selectedCompra) {
		this.selectedCompra = selectedCompra;
	}

	public String getFilter_stock() {
		return filter_stock;
	}

	public void setFilter_stock(String filter_stock) {
		this.filter_stock = filter_stock;
	}

	public long getTotalVentas() {
		return totalVentas;
	}

	public void setTotalVentas(long totalVentas) {
		this.totalVentas = totalVentas;
	}

	public long getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(long totalStock) {
		this.totalStock = totalStock;
	}

	public long getTotalResto() {
		return totalResto;
	}

	public void setTotalResto(long totalResto) {
		this.totalResto = totalResto;
	}

	public List<Funcionario> getSelectedVendedores() {
		return selectedVendedores;
	}

	public void setSelectedVendedores(List<Funcionario> selectedVendedores) {
		this.selectedVendedores = selectedVendedores;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getFilter_kpi() {
		return filter_kpi;
	}

	public void setFilter_kpi(String filter_kpi) {
		this.filter_kpi = filter_kpi;
	}
}
