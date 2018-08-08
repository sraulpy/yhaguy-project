package com.yhaguy.gestion.articulos.buscador;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Popup;

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
import com.yhaguy.util.Utiles;

public class BuscadorArticulosViewModel extends SimpleViewModel {
	
	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	
	public static final long ID_SUC_PRINCIPAL = 2;
	public static final long ID_DEP_1 = Configuracion.ID_DEPOSITO_PRINCIPAL;

	private String codInterno = "";
	private String codOriginal = "";
	private String codProveedor = "";
	private String descripcion = "";
	
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
	
	private Date desde;
	private Date hasta = new Date();
	
	@Wire
	private Popup pop_img;
	
	@Wire
	private Popup pop_barcode;
	
	@Init
	public void init() {
		try {
			String desde="01-01-2016 00:00:00";
			DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			this.desde = formatter.parse(desde);
		} catch (ParseException e) {
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
	private Listbox listArt;
	
	@Command
	@NotifyChange({"precios", "existencia", "importaciones", "stock"})
	public void obtenerValores() throws Exception {
		this.obtenerPrecio();
		this.obtenerExistencia();
		this.obtenerImportacionesEnCurso();
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
	
	/**
	 * @return la url de la foto..
	 */
	private String getUrlImagen() {
		if (this.selectedItem == null)
			return "http://190.211.240.30/images/default.png";
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
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
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
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
	
	@DependsOn({ "codInterno", "codOriginal", "codProveedor", "descripcion" })
	public List<MyArray> getArticulos() {
		this.setSelectedItem(null);
		List<MyArray> out = new ArrayList<MyArray>();
		
		if (this.codInterno.isEmpty() && this.codOriginal.isEmpty()
				&& this.codProveedor.isEmpty() && this.descripcion.isEmpty())
			return new ArrayList<MyArray>();

		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Articulo> arts = rr.getArticulos(this.codInterno,
					this.codOriginal, this.codProveedor, this.descripcion);
			out = this.articulosToMyArray(arts);
			
		} catch (Exception e) {
			return new ArrayList<MyArray>();
		}		
		return out;
	}
	
	/**
	 * @return los articulos convertidos a myarray..
	 */
	private List<MyArray> articulosToMyArray(List<Articulo> arts) throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		for (Articulo art : arts) {
			MyArray my = new MyArray();
			my.setId(art.getId());
			my.setPos1(art.getCodigoInterno());
			my.setPos2(art.getCodigoOriginal());
			my.setPos3(art.getCodigoProveedor());
			my.setPos4(art.getDescripcion());			
			List<MyArray> ubics = new ArrayList<MyArray>();
			for (ArticuloUbicacion ubic : art.getUbicaciones()) {
				ubics.add(new MyArray(ubic.getEstante(), ubic.getFila(), ubic.getColumna()));
			}
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
		Object[] costoInf = this.getCostoArticulo(idArticulo, idDeposito);
		double costo = (double) costoInf[0];
		
		for (MyArray precio : this.getListasDePrecio()) {
			double precioGs = this.getPrecioVenta(costo, (int) precio.getPos2());
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
		BindUtils.postNotifyChange(null, null, this, "precios");
		BindUtils.postNotifyChange(null, null, this, "existencia");
		BindUtils.postNotifyChange(null, null, this, "importaciones");
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
		List<Object[]> migracion = rr.getMigracionPorArticulo((String) this.selectedItem.getPos1(), this.desde, this.hasta, ID_SUC_PRINCIPAL);
		
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
		
		for (Object[] transf : transfs) {
			long idsuc = (long) transf[6];
			if(idsuc == ID_SUC_PRINCIPAL) {
				this.historicoSalida.add(transf);
			} else {				
				this.historicoEntrada.add(transf);
			}
		}
		
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
		List<Deposito> deps = rr.getDepositosPorSucursal(ID_SUC_PRINCIPAL);
		for (Deposito dep : deps) {
			out.add(new MyPair(dep.getId(), dep.getDescripcion()));
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
	public String getUrlBarcode() {
		if (this.selectedItem == null) return "";
		String codigo = (String) this.selectedItem.getPos1();
		return "/yhaguy/archivos/barcodes/" + codigo.replace("/", "-") + ".pdf";
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
		return Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS);
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
}
