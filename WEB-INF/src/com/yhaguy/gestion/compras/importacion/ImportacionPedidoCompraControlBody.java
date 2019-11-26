package com.yhaguy.gestion.compras.importacion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.domain.IiD;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.csv.CSV;
import com.coreweb.extras.email.CuerpoCorreo;
import com.coreweb.extras.email.EnviarCorreo;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.CierreDocumento;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.GastoDetalle;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Timbrado;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.gestion.comun.ControlLogica;
import com.yhaguy.gestion.empresa.ctacte.AssemblerCtaCteEmpresaMovimiento;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ImportacionPedidoCompraControlBody extends BodyApp {
	
	static final String PATH = Configuracion.pathPedidoCompra;
	static final String PATH_GENERICO = Configuracion.pathPedidoCompraGenerico;
	static final String ZUL_GASTOS = "/yhaguy/gestion/compras/importacion/gastos.zul";	
	
	static final String[][] CAB = { { "Empresa", CSV.STRING } };
	static final String[][] DET = { { "CODIGO", CSV.STRING }, { "CANTIDAD", CSV.STRING } };
	static final String[][] DET_PROFORMA = { { "CODIGO", CSV.STRING }, { "CANTIDAD", CSV.STRING }, { "COSTO", CSV.STRING } };
	static final String[][] DET_PRECIOS = { { "CODIGO", CSV.STRING }, { "MAYORISTA", CSV.STRING }, { "MINORISTA", CSV.STRING }, { "LISTA", CSV.STRING } };

	private ImportacionPedidoCompraDTO dto = new ImportacionPedidoCompraDTO();	
	
	private String filterNumero = "";
	private String filterRazonSocial = "";
	private ImportacionPedidoCompra selectedImportacion;
	private Proveedor selectedProveedor;
	private Gasto nvoGasto = new Gasto();
	private GastoDetalle nvoGastoDetalle = new GastoDetalle();
	
	private ImportacionAplicacionAnticipoDTO selectedAnticipo;
	private Object[] selectedFormato;
	
	private CtaCteEmpresaMovimiento nvoDesglose = new CtaCteEmpresaMovimiento();
	
	private MyArray nvaTrazabilidad = new MyArray(new Date(), "", "", "", "", 1, "", 0.0);
	
	public MyArray totalesCostoFinal = new MyArray(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	
	private Date fechaCierre;

	@Init(superclass = true)
	public void init() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<CierreDocumento> cierres = rr.getCierreDocumentos();
			if (cierres.size() > 0) {
				this.fechaCierre = cierres.get(0).getFecha();
			} else {
				this.fechaCierre = new Date();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterCompose(superclass = true)
	public void afterCompose() throws Exception {
		seleccionarTab();
	}

	@Override
	public Assembler getAss() {
		return new AssemblerImportacionPedidoCompra();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (ImportacionPedidoCompraDTO) dto;
		this.prorrateado = this.dto.isCifProrrateado();

		if (this.dto.isImportacionAnulada() == true) {
			this.enmascarar();
			return;
		}

		if (this.dto.getImportacionFactura().size() > 0) {
			this.setSelectedImportacionFactura(this.dto.getImportacionFactura().get(0));
			this.setSelectedRecepcion(this.dto.getImportacionFactura().get(0));
		} else {
			this.selectedImportacionFactura = new ImportacionFacturaDTO();
			this.selectedRecepcion = new ImportacionFacturaDTO();
		}
		this.setearValoresDespacho();
		this.renderizarDesgloseCuentas();
		this.agruparCantidades();
		this.desEnmascarar();
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		ImportacionPedidoCompraDTO out = new ImportacionPedidoCompraDTO();			
		this.sugerirValores(out);
		this.renderizarDesgloseCuentas();
		return out;
	}

	@Override
	public String getEntidadPrincipal() {
		return ImportacionPedidoCompra.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}	
	
	public Browser getBrowser(){
		return new ImportacionPedidoCompraBrowser();
	}
	
	public void clickCtrI() throws Exception{
		this.insertarItem();	
		BindUtils.postNotifyChange(null, null, this, "*");
	}

	Misc misc = this.m;			
	
	@Command
	@NotifyChange("*")
	public void importarDetalles(@BindingParam("comp") Popup comp) throws Exception {
		AssemblerImportacionPedidoCompra ass = new AssemblerImportacionPedidoCompra();
		ImportacionPedidoCompraDTO imp = (ImportacionPedidoCompraDTO) ass.domainToDto(this.selectedImportacion);
		for (ImportacionPedidoCompraDetalleDTO item : imp.getImportacionPedidoCompraDetalle()) {
			item.setId((long) -1);
		}
		this.dto.getImportacionPedidoCompraDetalle().addAll(imp.getImportacionPedidoCompraDetalle());
		Clients.showNotification("TOTAL ÍTEMS IMPORTADOS: " + imp.getImportacionPedidoCompraDetalle().size());
		comp.close();
		this.selectedImportacion = null;
	}
	
	@Command
	@NotifyChange("*")
	public void importarDetalles_(@BindingParam("comp") Popup comp) throws Exception {
		AssemblerImportacionPedidoCompra ass = new AssemblerImportacionPedidoCompra();
		ImportacionPedidoCompraDTO imp = (ImportacionPedidoCompraDTO) ass.domainToDto(this.selectedImportacion);
		for (ImportacionPedidoCompraDetalleDTO item : imp.getImportacionPedidoCompraDetalle()) {
			item.setId((long) -1);
		}
		this.dto.getSolicitudCotizaciones().addAll(imp.getImportacionPedidoCompraDetalle());
		Clients.showNotification("TOTAL ÍTEMS IMPORTADOS: " + imp.getImportacionPedidoCompraDetalle().size());
		comp.close();
		this.selectedImportacion = null;
	}
	
	@Command
	@NotifyChange("*")
	public void addTrazabilidad(@BindingParam("comp") Popup comp) {
		String text = (String) this.nvaTrazabilidad.getPos2();
		if (this.dto.getEstado().getText().equals("EN TRANSITO")) {
			text += " - " + this.nvaTrazabilidad.getPos4();
			text += " - " + this.nvaTrazabilidad.getPos5();
		}
		this.nvaTrazabilidad.setPos2(text.toUpperCase());
		this.dto.getTrazabilidad().add(this.nvaTrazabilidad);
		this.nvaTrazabilidad = new MyArray(new Date(), "", "", "", "", 1, "", 0.0);
		comp.close();
	}
	
	@Command
	@NotifyChange("nvaTrazabilidad")
	public void openTrazabilidad(@BindingParam("comp") Popup comp, @BindingParam("parent") Component parent) {
		if (this.dto.getEstado().getText().equals("EN TRANSITO")) {
			this.nvaTrazabilidad.setPos2("EN TRANSITO");
			this.nvaTrazabilidad.setPos4(this.dto.getVia());
		}
		comp.open(parent, "after_start");
	}
	
	@Command @NotifyChange("*")
	public void refreshTipoCambio() {
		double tipoCambio = 1;
		if (!this.dto.isMonedaLocal()) {
			try {
	            tipoCambio = this.getTipoCambio();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.dto.setCambio(tipoCambio);
	}
	
	@Command 
	@NotifyChange({ "nvoGasto", "nvoGastoDetalle" })
	public void addGastoDetalle() {
		this.nvoGasto.getDetalles().add(this.nvoGastoDetalle);
		this.nvoGastoDetalle = new GastoDetalle();
	}
	
	@Command
	@NotifyChange({ "nvoGasto", "nvoGastoDetalle" })
	public void deleteGastoDetalle(@BindingParam("item") GastoDetalle item) {
		this.nvoGasto.getDetalles().remove(item);
	}
	
	@Command 
	@NotifyChange("nvoGasto")
	public void openGasto(@BindingParam("comp") Popup comp, @BindingParam("parent") Component parent) 
		throws Exception {
		if (this.dto.getResumenGastosDespacho().getDespachante().esNuevo()) {
			Clients.showNotification("Antes, debe asignar el despachante..", Clients.NOTIFICATION_TYPE_WARNING, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		Timbrado timbrado = new Timbrado();
		timbrado.setVencimiento(Utiles.getFechaFinMes());
		this.nvoGasto = new Gasto();
		this.nvoGasto.setTipoCambio_(Gasto.TC_SET);
		this.nvoGasto.setSucursal(rr.getSucursalAppById(SucursalApp.ID_CENTRAL));
		this.win = (Window) Executions.createComponents(ZUL_GASTOS, this.mainComponent, null);
		this.win.doModal();
	}
	
	@Command 
	@NotifyChange("*")
	public void addGasto() throws Exception {
		String acreedor = this.nvoGasto.isAcreedorDespachante() ? 
				this.dto.getResumenGastosDespacho().getDespachante().getRazonSocial() : this.nvoGasto.getProveedor().getRazonSocial();
		if (!this.mensajeSiNo("La obligación de pago de esta factura será asignada al " + this.nvoGasto.getAcreedor() + " : " + acreedor)) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		if (rr.getGastoByNumero(this.nvoGasto.getNumero(), this.nvoGasto.getTimbrado()) != null) {
			Clients.showNotification("Ya existe una factura con el mismo número y timbrado..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		this.nvoGasto.setIdImportacion(this.dto.getId());
		this.nvoGasto.setFechaCarga(new Date());
		this.nvoGasto.setNumeroFactura(this.nvoGasto.getNumero());
		for (GastoDetalle item : this.nvoGasto.getDetalles()) {
			item.setMontoIva(item.getMontoIva_());
		}
		this.nvoGasto.setImporteGs(this.nvoGasto.getImporteGs_());
		this.nvoGasto.setImporteDs(this.nvoGasto.getImporteDs_());
		rr.saveObject(this.nvoGasto, this.getLoginNombre());
		long idEmpresa = this.nvoGasto.isAcreedorDespachante() ? 
				this.dto.getResumenGastosDespacho().getDespachante().getEmpresa().getId() : this.nvoGasto.getProveedor().getIdEmpresa();
		ControlCuentaCorriente.addGastoImportacion(this.nvoGasto, this.dto.getNumeroPedidoCompra(), idEmpresa, this.getLoginNombre());
		this.nvoGasto = new Gasto();
		this.win.detach();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	public void refresh(@BindingParam("item") ImportacionFacturaDetalleDTO item) {
		BindUtils.postNotifyChange(null, null, item, "*");
	}
	
	@Command
	@NotifyChange("*")
	public void habilitarRecepcion() {
		this.dto.setRecepcionHabilitada(true);
		Clients.showNotification("GUARDAR LOS CAMBIOS PARA HABILITAR RECEPCION DE MERCADERIAS..");
	}
	
	@Command
	public void imprimirVerificacion() {		
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				Object[] obj1 = new Object[] {
						item.getArticulo().getPos1(),
						item.getArticulo().getPos4(),
						item.isVerificado() ? "SI" : "NO"
				};
				data.add(obj1);			
			}
		}

		ReporteYhaguy rep = new ReporteVerificacionImportacion(this.dto);
		rep.setDatosReporte(data);

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	
	}
	
	@Command
	public void imprimirConteo() {		
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				Object[] obj1 = new Object[] {
						item.getArticulo().getPos1(),
						item.getArticulo().getPos4(),
						""
				};
				data.add(obj1);			
			}
		}
		
		// ordena la lista segun codigo..
		Collections.sort(data, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String cod1 = (String) o1[0];
				String cod2 = (String) o2[0];
				return cod1.compareTo(cod2);
			}
		});

		ReporteYhaguy rep = new ReporteConteoImportacion(this.dto);
		rep.setDatosReporte(data);

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	
	}
	
	@Command
	public void imprimirConteo_() {		
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (ImportacionFacturaDetalleDTO item : this.selectedImportacionFactura.getDetalles()) {
			Object[] obj1 = new Object[] {
					item.getArticulo().getPos1(),
					item.getConteo1(), (item.getConteo1() - item.getCantidad_acum()),
					item.getConteo2(), (item.getConteo2() - item.getCantidad_acum()),
					item.getConteo3(), (item.getConteo3() - item.getCantidad_acum())
			};
			data.add(obj1);
		}
		// ordena la lista segun codigo..
		Collections.sort(data, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String cod1 = (String) o1[0];
				String cod2 = (String) o2[0];
				return cod1.compareTo(cod2);
			}
		});

		ReporteYhaguy rep = new ReporteConteoImportacion_(this.dto);
		rep.setDatosReporte(data);

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	
	}
	
	@Command
	public void imprimirDiferencias() {		
		boolean mostrar = false;
		if (this.mensajeSiNo("Mostrar columna de diferencias?")) {
			mostrar = true;
		}
		
		List<Object[]> data = new ArrayList<Object[]>();
		String conteo = "";
		
		for (ImportacionFacturaDetalleDTO item : this.selectedImportacionFactura.getDetalles()) {
			if (this.dto.isConteo1() && !this.dto.isConteo2() && !this.dto.isConteo3()) {
				conteo = "1er Conteo";
				int dif = item.getConteo1() - item.getCantidad_acum();
				if (dif != 0) {
					Object[] obj = mostrar ? new Object[] { item.getArticulo().getPos1(), item.getConteo1(), dif } : new Object[] { item.getArticulo().getPos1(), item.getConteo1() };
					data.add(obj);
				}
			} else if (this.dto.isConteo1() && this.dto.isConteo2() && !this.dto.isConteo3()) {
				conteo = "2do Conteo";
				int dif1 = item.getConteo1() - item.getCantidad_acum();
				int dif = item.getConteo2() - item.getCantidad_acum();
				if (dif1 != 0) {
					if (dif != 0) {
						Object[] obj = mostrar ? new Object[] { item.getArticulo().getPos1(), item.getConteo2(), dif } : new Object[] { item.getArticulo().getPos1(), item.getConteo2()};
						data.add(obj);
					}
				}				
			} else {
				conteo = "3er Conteo";
				int dif1 = item.getConteo1() - item.getCantidad_acum();
				int dif2 = item.getConteo2() - item.getCantidad_acum();
				int dif = item.getConteo3() - item.getCantidad_acum();
				if (dif1 != 0) {
					if (dif2 != 0) {
						if (dif != 0) {
							Object[] obj = mostrar ? new Object[] { item.getArticulo().getPos1(), item.getConteo3(), dif } : new Object[] { item.getArticulo().getPos1(), item.getConteo3()};
							data.add(obj);
						}
					}					
				}				
			}
		}
		// ordena la lista segun codigo..
		Collections.sort(data, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String cod1 = (String) o1[0];
				String cod2 = (String) o2[0];
				return cod1.compareTo(cod2);
			}
		});

		ReporteYhaguy rep = mostrar ? new ReporteDiferenciaImportacion(this.dto, conteo) : new ReporteDiferenciaImportacion_(this.dto, conteo);
		rep.setDatosReporte(data);

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	
	}
	
	@Command
	@NotifyChange("*")
	public void addDesglose() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvoDesglose.setSaldo(this.nvoDesglose.getImporteOriginal());
		this.nvoDesglose.setIdEmpresa(this.dto.getProveedor().getEmpresa().getId());
		this.nvoDesglose.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_IMPORT_CREDITO));
		this.nvoDesglose.setIdMovimientoOriginal(this.dto.getImportacionFactura().get(0).getId());	
		this.nvoDesglose.setMoneda(rr.getTipoById(this.dto.getMoneda().getId()));
		this.nvoDesglose.setSucursal(rr.getSucursalAppById(SucursalApp.ID_CENTRAL));
		this.nvoDesglose.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		this.nvoDesglose.setIdImportacionPedido(this.dto.getId());
		rr.saveObject(this.nvoDesglose, this.getLoginNombre());
		this.nvoDesglose = new CtaCteEmpresaMovimiento();
	}
	
	/************************** ELIMINAR ITEM DETALLE ORDEN COMPRA ****************************/
	
	private List<ImportacionPedidoCompraDetalleDTO> selectedOrdenItems = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
	private String ordenItemsEliminar;

	@Command
	@NotifyChange("*")
	public void eliminarItem() {
		if (this.operacionValidaPedido(ELIMINAR_ITEM_ORDEN) == false) {
			return;
		}		
		if (this.confirmarEliminarItemOrden() == true) {
			for (ImportacionPedidoCompraDetalleDTO d : this.selectedOrdenItems) {
				this.dto.getImportacionPedidoCompraDetalle().remove(d);
			}
			this.selectedOrdenItems = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
		}
	}	
	
	@Command
	@NotifyChange("*")
	public void eliminarItem_() {
		for (ImportacionPedidoCompraDetalleDTO d : this.selectedOrdenItems) {
			this.dto.getSolicitudCotizaciones().remove(d);
		}
		this.selectedOrdenItems = null;
	}
	
	private boolean confirmarEliminarItemOrden(){
		this.ordenItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (ImportacionPedidoCompraDetalleDTO d : this.selectedOrdenItems) {
			this.ordenItemsEliminar = this.ordenItemsEliminar + "\n - " + d.getArticulo().getPos1();
		}		
		return this.mensajeSiNo(this.ordenItemsEliminar);
	}

	/******************************************************************************************/	
	
	
	/********************************** INSERTAR ITEM MANUAL **********************************/

	private ImportacionPedidoCompraDetalleDTO newDetalle = new ImportacionPedidoCompraDetalleDTO();
	
	@Command @NotifyChange("*")
	public void insertarItem() throws Exception {
		if (this.operacionValidaPedido(INSERTAR_ITEM_ORDEN) == false) {
			return;
		}		
		try {
			this.newDetalle = new ImportacionPedidoCompraDetalleDTO();
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo("Insertar Item");
			w.setWidth("450px");
			w.setHigth("310px");
			w.setDato(this);
			w.setCheckAC(new ValidadorInsertarItemOrdenCompra(this));
			w.show(Configuracion.INSERTAR_ITEM_PEDIDO_COMPRA_ZUL);
			if (w.isClickAceptar()) {
				this.newDetalle.setUltimoCostoDs(this.getUltimoCosto(this.newDetalle.getArticulo().getId()));
				this.dto.getImportacionPedidoCompraDetalle().add(this.newDetalle);
				this.selectedOrdenItems.add(newDetalle);
			}			
		} catch (Exception e) {
			mensajeError(e.getMessage());
		}
	}	
	
	@Command @NotifyChange("*")
	public void insertarItem_() throws Exception {		
		try {
			this.newDetalle = new ImportacionPedidoCompraDetalleDTO();
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo("Insertar Item");
			w.setWidth("450px");
			w.setHigth("310px");
			w.setDato(this);
			w.setCheckAC(new ValidadorInsertarItemOrdenCompra(this));
			w.show(Configuracion.INSERTAR_ITEM_PEDIDO_COMPRA_ZUL);
			if (w.isClickAceptar()) {
				this.newDetalle.setUltimoCostoDs(this.getUltimoCosto(this.newDetalle.getArticulo().getId()));
				this.dto.getSolicitudCotizaciones().add(this.newDetalle);
			}			
		} catch (Exception e) {
			mensajeError(e.getMessage());
		}
	}
	
	private double getUltimoCosto(long idArticulo){		
		double out = 0;
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			out = rr.getUltimoCostoFOBDs(idArticulo, 0); //arreglar!!!
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	@Command 
	@NotifyChange("*")
	public void uploadFile(@BindingParam("file") Media file) {
		try {
			Misc misc = new Misc();
			String name = this.dto.getNumeroPedidoCompra();
			boolean isText = "text/csv".equals(file.getContentType());
			InputStream file_ = new ByteArrayInputStream(isText ? file.getStringData().getBytes() : file.getByteData());
			misc.uploadFile(PATH, name, ".csv", file_);
			this.csvCotizacion();
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command 
	@NotifyChange("*")
	public void uploadFile_(@BindingParam("file") Media file) {
		try {
			Misc misc = new Misc();
			String name = "proforma_" + this.dto.getNumeroPedidoCompra();
			boolean isText = "text/csv".equals(file.getContentType());
			InputStream file_ = new ByteArrayInputStream(isText ? file.getStringData().getBytes() : file.getByteData());
			misc.uploadFile(PATH, name, ".csv", file_);
			this.csvProforma();
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command 
	@NotifyChange("*")
	public void uploadFilefactura(@BindingParam("file") Media file) {
		try {
			Misc misc = new Misc();
			String name = "factura_" + this.dto.getNumeroPedidoCompra();
			boolean isText = "text/csv".equals(file.getContentType());
			InputStream file_ = new ByteArrayInputStream(isText ? file.getStringData().getBytes() : file.getByteData());
			misc.uploadFile(PATH, name, ".csv", file_);
			this.csvFactura();
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command 
	@NotifyChange("*")
	public void uploadFileTrazabilidad(@BindingParam("file") Media file) {
		try {
			Misc misc = new Misc();
			String name = this.dto.getNumeroPedidoCompra() + "_" + Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY_HH_MM_SS);
			boolean isText = "text/csv".equals(file.getContentType());
			InputStream file_ = new ByteArrayInputStream(isText ? file.getStringData().getBytes() : file.getByteData());
			String ext = file.getName().substring(file.getName().lastIndexOf("."));
			misc.uploadFile(PATH, name, ext, file_);
			this.nvaTrazabilidad.setPos3(Configuracion.pathPedidoCompraGenerico + name + ext);
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command 
	@NotifyChange("*")
	public void uploadFilePrecios(@BindingParam("file") Media file) {
		try {
			Misc misc = new Misc();
			String name = "precios_" + this.dto.getNumeroPedidoCompra();
			boolean isText = "text/csv".equals(file.getContentType());
			InputStream file_ = new ByteArrayInputStream(isText ? file.getStringData().getBytes() : file.getByteData());
			misc.uploadFile(PATH, name, ".csv", file_);
			this.csvPrecios();
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command
	@NotifyChange("nvoGasto")
	public void setCondicion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CondicionPago cont = rr.getCondicionPagoById(CondicionPago.ID_CONTADO);
		CondicionPago cred = rr.getCondicionPagoById(CondicionPago.ID_CREDITO_30);
		this.nvoGasto.setCondicionPago(this.nvoGasto.isContado() ? cont : cred);
	}
	
	@Command
	@NotifyChange("nvoGasto")
	public void setVencimiento() throws Exception {
		this.setTipoCambio();
		if (this.nvoGasto.getTipoMovimiento() == null) {
			return;
		}
		int dias = 0;
		if (!this.nvoGasto.isContado()) {
			dias = 31;
		}
		this.nvoGasto.setVencimiento(Utiles.agregarDias(this.nvoGasto.getFecha(), dias));
	}
	
	@Command
	@NotifyChange("nvoGasto")
	public void setTipoCambio() throws Exception {
		if (this.nvoGasto.isTipoCambioSET()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			if (this.nvoGasto.getFecha() == null) return;
			double tc = rr.getTipoCambioVenta(Utiles.agregarDias(this.nvoGasto.getFecha(), -1));
			this.nvoGasto.setTipoCambio(tc);
		} else {
			this.nvoGasto.setTipoCambio(this.dto.getResumenGastosDespacho().getTipoCambio());
		}
	}
	
	/**
	 * csv de cotizacion..
	 */
	private void csvCotizacion() {
		try {
			this.dto.getSolicitudCotizaciones().clear();
			List<ImportacionPedidoCompraDetalleDTO> list = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
			RegisterDomain rr = RegisterDomain.getInstance();
			
			CSV csv = new CSV(CAB, DET, PATH + this.dto.getNumeroPedidoCompra() + ".csv", ';');
			String noEncontrado = "Códigos no encontrados: \n";
			csv.start();
			while (csv.hashNext()) {
				String codigo = csv.getDetalleString("CODIGO"); 
				String cantidad = csv.getDetalleString("CANTIDAD");
				
				ImportacionPedidoCompraDetalleDTO item = new ImportacionPedidoCompraDetalleDTO();
				Articulo art = rr.getArticulo(codigo);
				if (art != null) {
					MyArray ar = new MyArray(art.getCodigoInterno(), art.getCodigoProveedor(), art.getCodigoOriginal(),
							art.getDescripcion(), art.isServicio());
					ar.setId(art.getId());
					item.setArticulo(ar);
					item.setCantidad(Integer.parseInt(cantidad));
					list.add(item);
				} else {
					noEncontrado += codigo + "\n";
				}
			}
			this.dto.getSolicitudCotizaciones().addAll(list);
			this.dto.setConfirmadoAdministracion(true);
			this.mensajePopupTemporal("SE IMPORTARON " + list.size() + " ÍTEMS");
			Clients.showNotification(noEncontrado);
			
			MyArray trazabilidad = new MyArray(new Date(), "SOLICITANDO COTIZACION", PATH_GENERICO + this.dto.getNumeroPedidoCompra() + ".csv", "", "", 1, "", 0.0);
			this.dto.getTrazabilidad().add(trazabilidad);
		
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al leer el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	/**
	 * csv proforma..
	 */
	private void csvProforma() {
		try {
			this.dto.getImportacionPedidoCompraDetalle().clear();
			List<ImportacionPedidoCompraDetalleDTO> list = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
			RegisterDomain rr = RegisterDomain.getInstance();
			
			CSV csv = new CSV(CAB, DET_PROFORMA, PATH + "proforma_" + this.dto.getNumeroPedidoCompra() + ".csv", ';');
			String noEncontrado = "Códigos no encontrados: \n";
			csv.start();
			while (csv.hashNext()) {
				String codigo = csv.getDetalleString("CODIGO"); 
				String cantidad = csv.getDetalleString("CANTIDAD");
				String costoDs = csv.getDetalleString("COSTO");
				
				ImportacionPedidoCompraDetalleDTO item = new ImportacionPedidoCompraDetalleDTO();
				Articulo art = rr.getArticulo(codigo);
				if (art != null) {
					MyArray ar = new MyArray(art.getCodigoInterno(), art.getCodigoProveedor(), art.getCodigoOriginal(),
							art.getDescripcion(), art.isServicio());
					ar.setId(art.getId());
					item.setArticulo(ar);
					item.setCantidad(Integer.parseInt(cantidad));
					item.setCostoProformaDs(Double.parseDouble(costoDs.replace(".", "").replace(",", ".")));
					list.add(item);
				} else {
					noEncontrado += codigo + "\n";
				}				
			}
			this.dto.getImportacionPedidoCompraDetalle().addAll(list);
			this.dto.setConfirmadoImportacion(true);
			this.mensajePopupTemporal("SE IMPORTARON " + list.size() + " ÍTEMS");
			Clients.showNotification(noEncontrado);
			MyArray trazabilidad = new MyArray(new Date(), "PROFORMA CARGADA", PATH_GENERICO + "proforma_" + this.dto.getNumeroPedidoCompra() + ".csv", "", "", 1, "", 0.0);
			this.dto.getTrazabilidad().add(trazabilidad);
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al leer el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	/**
	 * csv factura..
	 */
	private void csvFactura() {
		try {
			this.dto.getImportacionFactura().get(0).getDetalles().clear();
			List<ImportacionFacturaDetalleDTO> list = new ArrayList<ImportacionFacturaDetalleDTO>();
			RegisterDomain rr = RegisterDomain.getInstance();
			
			CSV csv = new CSV(CAB, DET_PROFORMA, PATH + "factura_" + this.dto.getNumeroPedidoCompra() + ".csv", ';');
			String noEncontrado = "Códigos no encontrados: \n";
			csv.start();
			while (csv.hashNext()) {
				String codigo = csv.getDetalleString("CODIGO"); 
				String cantidad = csv.getDetalleString("CANTIDAD");
				String costoDs = csv.getDetalleString("COSTO");
				
				ImportacionFacturaDetalleDTO item = new ImportacionFacturaDetalleDTO();
				Articulo art = rr.getArticulo(codigo);
				if (art != null) {
					MyArray ar = new MyArray(art.getCodigoInterno(), art.getCodigoProveedor(), art.getCodigoOriginal(),
							art.getDescripcion(), art.isServicio());
					ar.setId(art.getId());
					item.setArticulo(ar);
					item.setCantidad(Integer.parseInt(cantidad));
					item.setCostoDs(Double.parseDouble(costoDs.replace(".", "").replace(",", ".")));
					list.add(item);
				} else {
					noEncontrado += codigo + "\n";
				}
			}
			this.dto.getImportacionFactura().get(0).getDetalles().addAll(list);
			this.dto.setConfirmadoImportacion(true);
			this.mensajePopupTemporal("SE IMPORTARON " + list.size() + " ÍTEMS");
			Clients.showNotification(noEncontrado);
			MyArray trazabilidad = new MyArray(new Date(), "FACTURA CARGADA", PATH_GENERICO + "factura_" + this.dto.getNumeroPedidoCompra() + ".csv", "", "", 1, "", 0.0);
			this.dto.getTrazabilidad().add(trazabilidad);
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al leer el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	/**
	 * csv precios..
	 */
	private void csvPrecios() {
		try {			
			CSV csv = new CSV(CAB, DET_PRECIOS, PATH + "precios_" + this.dto.getNumeroPedidoCompra() + ".csv", ';');
			String noEncontrado = "Códigos no encontrados: \n";
			Map<String, Object[]> precios = new HashMap<String, Object[]>();
			csv.start();
			while (csv.hashNext()) {
				String codigo = csv.getDetalleString("CODIGO"); 
				String mayorista = csv.getDetalleString("MAYORISTA");
				String minorista = csv.getDetalleString("MINORISTA");
				String lista = csv.getDetalleString("LISTA");
			 	double mayorista_ =  Double.parseDouble(mayorista.replace(".", "").replace(",", "."));
			 	double minorista_ =  Double.parseDouble(minorista.replace(".", "").replace(",", "."));
			 	double lista_ =  Double.parseDouble(lista.replace(".", "").replace(",", "."));
				Object[] precio = new Object[] { mayorista_, minorista_, lista_ };
				precios.put(codigo, precio);								
			}
			for (ImportacionFacturaDetalleDTO item : this.dto.getImportacionFactura().get(0).getDetalles()) {
				Object[] precio = precios.get(item.getArticulo().getPos1());
				if (precio != null) {
					double may = (double) precio[0];
					double min = (double) precio[1];
					double lis = (double) precio[2];
					item.setPrecioFinalGs(may);
					item.setMinoristaGs(min);
					item.setListaGs(lis);
				} else {
					noEncontrado += " \n - " + item.getArticulo().getPos1();
				}
			}
			this.mensajePopupTemporal("SE IMPORTARON " + precios.size() + " ÍTEMS");
			Clients.showNotification(noEncontrado);
			MyArray trazabilidad = new MyArray(new Date(), "PRECIOS ASIGNADOS", PATH_GENERICO + "precios_" + this.dto.getNumeroPedidoCompra() + ".csv", "", "", 1, "", 0.0);
			this.dto.getTrazabilidad().add(trazabilidad);
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al leer el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command
	public void imprimirResumen() throws Exception {
		this.imprimirResumen_();
	}
	
	@Command
	public void imprimirResumenImportacion() throws Exception {
		this.imprimirResumenImportacion_();
	}
	
	/**
	 * Despliega el Reporte de detalle..
	 */
	private void imprimirResumen_() throws Exception {		
		String source = ReportesViewModel.SOURCE_RESUMEN_IMPORTACION;
		if (!this.selectedFormato[0].equals("PDF")) {
			source = ReportesViewModel.SOURCE_RESUMEN_IMPORTACION_SIN_CAB;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ResumenImportacionDataSource();
		params.put("Titulo", "DETALLE DE IMPORTACION NRO. " + this.dto.getNumeroPedidoCompra() + " - PROVEEDOR: " + this.dto.getProveedor().getRazonSocial());
		params.put("Usuario", this.getUs().getNombre());
		this.imprimirComprobante(source, params, dataSource, this.selectedFormato);
	}
	
	/**
	 * Despliega el Reporte de resumen..
	 */
	private void imprimirResumenImportacion_() throws Exception {		
		String source = ReportesViewModel.SOURCE_RESUMEN_IMPORTACION_;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ResumenDataSource();
		params.put("Usuario", this.getUs().getNombre());
		params.put("Numero", this.dto.getNumeroPedidoCompra());
		params.put("Proveedor", this.dto.getProveedor().getRazonSocial());
		params.put("TipoCambio", Utiles.getNumberFormatDs(this.dto.getResumenGastosDespacho().getTipoCambio()));
		this.imprimirComprobante(source, params, dataSource, this.selectedFormato);
	}
	
	/**
	 * DataSource del resumen..
	 */
	class ResumenDataSource implements JRDataSource {

		List<Object[]> detalle = new ArrayList<Object[]>();
		Map<String, Double> totales = new HashMap<String, Double>();
		Map<String, Double> totalesDs = new HashMap<String, Double>();
		Map<String, Double> totalesIva = new HashMap<String, Double>();
		Map<String, Double> totalesNeto = new HashMap<String, Double>();

		public ResumenDataSource() {
			try {
				this.detalle = dto.getGastosDetallado();
				for (Object[] det : this.detalle) {
					String key = (String) det[5];
					String desc = (String) det[2];
					Double acum = totales.get(key);
					Double acumDs = totalesDs.get(key);
					Double acumNeto = totalesNeto.get(key);
					Double acumIva = totalesIva.get(key);
					if(acum != null) {
						acum += (double) det[3];
						acumDs += (double) det[4];
						if (!desc.equals("IVA - IMPORTACION")) {
							acumNeto += ((double) det[3] - (double) det[6]);
						}						
						acumIva += (double) det[6];
						totales.put(key, acum);
						totalesDs.put(key, acumDs);
						totalesNeto.put(key, acumNeto);
						totalesIva.put(key, acumIva);
					} else {
						totales.put(key, (double) det[3]);
						totalesDs.put(key, (double) det[4]);
						if (desc.equals("IVA - IMPORTACION")) {
							totalesNeto.put(key, (double) 0);
						} else {
							totalesNeto.put(key, (((double) det[3]) - (double) det[6]));
						}						
						totalesIva.put(key, (double) det[6]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			Object[] item = this.detalle.get(index);
			double importe = Utiles.getRedondeo((double) item[3]);
			double importeDs = misc.redondeoDosDecimales((double) item[4]);
			double iva = Utiles.getRedondeo((double) item[6]);
			double totalImporte = Utiles.getRedondeo(totales.get(item[5]));
			double totalImporteDs = misc.redondeoDosDecimales(totalesDs.get(item[5]));
			double totalIva = Utiles.getRedondeo(totalesIva.get(item[5]));
			double totalNeto = Utiles.getRedondeo(totalesNeto.get(item[5]));

			if ("TituloDetalle".equals(fieldName)) {
				value = item[5];
			} else if ("Descripcion".equals(fieldName)) {
				value = item[2];
			} else if ("NroFactura".equals(fieldName)) {
				value = item[1];
			} else if ("Importe".equals(fieldName)) {
				value = Utiles.getNumberFormat(importe);
			} else if ("Iva".equals(fieldName)) {
				value = Utiles.getNumberFormat(iva);
			} else if ("Neto".equals(fieldName)) {
				String desc = (String) item[2];
				if (desc.equals("IVA - IMPORTACION")) {
					value = Utiles.getNumberFormat(0);
				} else {
					value = Utiles.getNumberFormat((importe) - (iva));
				}
			} else if ("ImporteDs".equals(fieldName)) {
				value = Utiles.getNumberFormatDs(importeDs);
			} else if ("TotalImporte".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalImporte);
			} else if ("TotalImporteDs".equals(fieldName)) {
				value = Utiles.getNumberFormatDs(totalImporteDs);
			} else if ("TotalIva".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalIva);
			} else if ("TotalNeto".equals(fieldName)) {
				value = Utiles.getNumberFormat(totalNeto);
			} else if ("TipoCambio_".equals(fieldName)) {
				value = Utiles.getNumberFormatDs(importe / importeDs);
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < detalle.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}
	
	/**
	 * DataSource del resumen importacion..
	 */
	class ResumenImportacionDataSource implements JRDataSource {

		List<MyArray> detalle = new ArrayList<MyArray>();

		public ResumenImportacionDataSource() {
			this.detalle = getItemsCostoFinal_();
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			MyArray item = this.detalle.get(index);

			if ("Codigo".equals(fieldName)) {
				value = item.getPos1();
			} else if ("Descripcion".equals(fieldName)) {
				value = item.getPos2();
			} else if ("Cantidad".equals(fieldName)) {				
				value = item.getPos3() + "";
			} else if ("CostoFobDs".equals(fieldName)) {
				value = Utiles.getNumberFormatDs_((double) item.getPos4());
			} else if ("CostoFobGs".equals(fieldName)) {	
				value = Utiles.getNumberFormat((double) item.getPos5());
			} else if ("TotalDs".equals(fieldName)) {
				value = Utiles.getNumberFormatDs_((double) item.getPos6());
			} else if ("TotalGs".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) item.getPos7());
			} else if ("Incidencia".equals(fieldName)) {
				value = Utiles.getNumberFormatDs_((double) item.getPos8());
			} else if ("FleteSeguro".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) item.getPos9());
			} else if ("CIF".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) item.getPos10());
			} else if ("Gastos".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) item.getPos11());
			} else if ("CostoFinal".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) item.getPos12());
			} else if ("CostoUnitario".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) item.getPos13());
			} else if ("CostoUnitario_".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) item.getPos14());
			} else if ("Tot_1".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) getTotalesCostoFinal().getPos1());
			} else if ("Tot_2".equals(fieldName)) {				
				value = Utiles.getNumberFormatDs_((double) getTotalesCostoFinal().getPos2());
			} else if ("Tot_3".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) getTotalesCostoFinal().getPos3());
			} else if ("Tot_4".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) getTotalesCostoFinal().getPos4());
			} else if ("Tot_5".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) getTotalesCostoFinal().getPos5());
			} else if ("Tot_6".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) getTotalesCostoFinal().getPos6());
			} else if ("Tot_7".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) getTotalesCostoFinal().getPos7());
			} else if ("Tot_8".equals(fieldName)) {				
				value = Utiles.getNumberFormat((double) getTotalesCostoFinal().getPos8());
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < detalle.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
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

	/******************************************************************************************/	
	
	
	/************************************* ENVIO DE CORREO ************************************/
	
	private String linkImportacionPedidoCompra = "";
	private String pathRealImportacionPedidoCompra = "";
	
	private CuerpoCorreo correo = new CuerpoCorreo(
			Configuracion.DPTO_IMPORTACION_MAIL_ADDRESS,
			Configuracion.DPTO_IMPORTACION_MAIL_PASSWD,
			Configuracion.TEXTO_CORREO_IMPORTACION);		
		
	@Command
	@NotifyChange("*")
	public void correo(){
		
		if (this.operacionValidaPedido(ENVIAR_CORREO) == false) {
			return;
		}		
		correo.setAsunto(this.getTipoPedido());		
		this.generarAdjuntoCorreo(false);			
		this.showCorreo();
	}
	
	public void showCorreo(){
		try {
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo("Envio de Correos");
			w.setWidth("750px");
			w.setHigth("500px");
			w.setDato(this);
			w.setCheckAC(new ValidadorEnviarCorreo(this));
			w.show(Configuracion.CORREO_IMPORTACION_ZUL);
			if (w.isClickAceptar()) {				
				this.enviarCorreo();
			} else {
				//si no envia borra el archivo
				this.m.borrarArchivo(this.pathRealImportacionPedidoCompra);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}

	// Genera el informe de Pedido Compra, guarda en un directorio en formato
	// PDF y de ahi adjunta al correo..
	public void generarAdjuntoCorreo(boolean mostrar){
		
		List<Object[]> data = new ArrayList<>();
		Object[] obj;		
		for (ImportacionPedidoCompraDetalleDTO d : this.dto.getImportacionPedidoCompraDetalle()) {
			obj = new Object[4];
			obj[0] = d.getArticulo().getPos1();
			obj[1] = d.getArticulo().getPos4();
			obj[2] = d.getCantidad();
			obj[3] = d.getCostoProformaDs();
			data.add(obj);	
		}
	}	
	
	public void enviarCorreo() {		
		String[] send = correo.getDestinatario().trim().split(";");
		String[] sendCC = correo.getDestinatarioCopia().trim().split(";");
		String[] sendCCO = correo.getDestinatarioCopiaOculta().trim().split(";");

		correo.setAdjunto(this.pathRealImportacionPedidoCompra);
		try {		
			EnviarCorreo enviarCorreo = new EnviarCorreo();			
			enviarCorreo.sendMessage(send, sendCC, sendCCO,
					correo.getAsunto(), correo.getCuerpo(),
					correo.getRemitente(), correo.getClave(), this.getNombreArchivo() + ".pdf",
					correo.getAdjunto());
			
			this.addEventoAgendaLink(Configuracion.TEXTO_CORREO_ENVIADO + correo.getAsunto(), this.linkImportacionPedidoCompra);
			
			this.actualizarEtapaActual(this.getTipoPedidoKey());
			this.m.mensajePopupTemporal(Configuracion.TEXTO_CORREO_ENVIADO_CORRECTAMENTE);			
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}	
	
	public String getTipoPedido() {
		String out = "";
		if (this.dto.isPedidoConfirmado()) {
			out = Configuracion.TEXTO_PEDIDO_CONFIRMADO;
		} else {
			out = Configuracion.TEXTO_SOLICITUD_COTIZACION;
		}
		return out;
	}	

	/******************************************************************************************/
	

	/*********************************** DERIVAR PEDIDO COMPRA ********************************/	

	private Window win;
	
	@Command
	public void abrirPropietariosPedidoCompra() {
		if (this.operacionValidaPedido(DERIVAR_PEDIDO) == false) {
			return;
		}
		this.selectedPropietarioPedidoCompra = null;
		win = (Window) Executions.createComponents(
				Configuracion.LISTA_PROPIETARIOS_PEDIDO_COMPRA_ZUL, this.mainComponent, null);
		win.doModal();
	}

	@Command
	@NotifyChange("*")
	public void selectPropietarioPedidoCompra() throws Exception {

		if (this.selectedPropietarioPedidoCompra
				.compareTo(Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION) == 0) {
			this.dto.setPropietarioActual(Configuracion.PROPIETARIO_IMPORTACION_KEY);
		}

		if (this.selectedPropietarioPedidoCompra
				.compareTo(Configuracion.PROPIETARIO_VENTAS_DESCRIPCION) == 0) {
			this.dto.setPropietarioActual(Configuracion.PROPIETARIO_VENTAS_KEY);
		}

		if (this.selectedPropietarioPedidoCompra
				.compareTo(Configuracion.PROPIETARIO_ADMINISTRACION_DESCRIPCION) == 0) {
			this.dto.setPropietarioActual(Configuracion.PROPIETARIO_ADMINISTRACION_KEY);
		}

		win.detach();
		this.addEventoAgenda(EVENTO_DERIVAR_ORDEN_COMPRA + this.dto.getPropietario());
	}

	public String getLabelImportacionStyle() {
		String out = "";
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_IMPORTACION_KEY) {
			out = Misc.LABEL_BORDER;
		}
		return out;
	}

	public String getLabelVentasStyle() {
		String out = "";
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_VENTAS_KEY) {
			out = Misc.LABEL_BORDER;
		}
		return out;
	}

	public String getLabelAdministracionStyle() {
		String out = "";
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_ADMINISTRACION_KEY) {
			out = Misc.LABEL_BORDER;
		}
		return out;
	}

	/******************************************************************************************/	
	
	
	/***************************** CONFIRMACION ORDEN COMPRA **********************************/

	// Comportamiento Checkbox Importacion (Habilitado/Desabilitado)
	public boolean getCheckboxImportacionEnabled() {
		boolean out = false;
		if ((this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_IMPORTACION_KEY)
				&& (this.dto.isConfirmadoVentas() == true)) {
			out = true;
		}
		return out;
	}

	// Comportamiento Checkbox Ventas (Habilitado/Desabilitado)
	public boolean getCheckboxVentasEnabled() {
		boolean out = false;
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_VENTAS_KEY) {
			out = true;
		}
		return out;
	}

	// Comportamiento Checkbox Administracion (Habilitado/Desabilitado)
	public boolean getCheckboxAdministracionEnabled() {
		boolean out = false;
		if (this.dto.getPropietarioActual() == Configuracion.PROPIETARIO_ADMINISTRACION_KEY) {
			out = true;
		}
		return out;
	}

	@Command @NotifyChange("*")
	public void confirmarPedidoCompra() throws Exception {
		
		if (this.operacionValidaPedido(CONFIRMAR_ORDEN_COMPRA) == false) {
			return;
		}
		
		try {
			if (mensajeSiNo("Desea Confirmar La Orden de Compra Nro.: "
					+ this.dto.getNumeroPedidoCompra() + "?")) {
				this.dto.setPropietarioActual(Configuracion.PROPIETARIO_ADMINISTRACION_KEY);
				this.dto.setPedidoConfirmado(true);
				this.dto.setEstado(this.getDtoUtil().getImportacionEstadoPendienteEnvio()); 
				this.addEventoAgenda(EVENTO_CONFIRMAR_ORDEN_COMPRA);
				this.actualizarEtapaActual(this.getDtoUtil().getImportacionEstadoPendienteEnvio());
				
				// Verifica que los datos esten correctos y graba en la BD al confirmar el Pedido			
				if (this.validarFormulario() == true) {
					this.grabarEventosAgenda();
					this.saveDTO(dto);
					this.mensajePopupTemporal(EVENTO_CONFIRMAR_ORDEN_COMPRA);
				}
						
			} else {
				this.dto.setConfirmadoImportacion(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}

	/******************************************************************************************/	
	

	/********************************** DETALLE PEDIDO COMPRA *********************************/

	
	public double getTotalProformaGs() {
		double total = 0;
		for (ImportacionPedidoCompraDetalleDTO d : this.dto
				.getImportacionPedidoCompraDetalle()) {
			total = total + d.getImporteProformaGs();
		}
		return total;
	}

	public double getTotalProformaDs() {
		double total = 0;
		for (ImportacionPedidoCompraDetalleDTO d : this.dto
				.getImportacionPedidoCompraDetalle()) {
			total = total + d.getImporteProformaDs();
		}
		return total;
	}

	@Command
	@NotifyChange({"totalProformaGs", "totalProformaDs", "itemsCostoFinal"})
	public void actualizarCostoProformaDs(
			@BindingParam("detalle") ImportacionPedidoCompraDetalleDTO detalle) {
		double costoGs = detalle.getCostoProformaGs();
		detalle.setCostoProformaDs(costoGs / this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, detalle, "costoProformaDs");
	}

	@Command
	@NotifyChange({"totalProformaGs", "totalProformaDs", "itemsCostoFinal"})
	public void actualizarCostoProformaGs(
			@BindingParam("detalle") ImportacionPedidoCompraDetalleDTO detalle) {
		double costoDs = detalle.getCostoProformaDs();
		detalle.setCostoProformaGs(costoDs * this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, detalle, "costoProformaGs");
	}

	/******************************************************************************************/	
	

	/***************************** ESTADO GENERAL PEDIDO COMPRA *******************************/

	@DependsOn({"dto.importacionAnulada", "dto.pedidoConfirmado"})
	public String getEstadoGeneralPedidoCompra() {
		String out = Configuracion.ESTADO_PEDIDO_COMPRA_ELABORACION;
		if (this.dto.isImportacionAnulada() == true) {
			out = Configuracion.ESTADO_PEDIDO_COMPRA_ANULADO;
			
		} else if (this.dto.isPedidoConfirmado() == true) {
			out = Configuracion.ESTADO_PEDIDO_COMPRA_CONFIRMADO;
		}
		return out;
	}

	/******************************************************************************************/	
	

	/************ ACTUALIZACION AUTOMATICA ETAPA ACTUAL DEL PEDIDO COMPRA *********************/

	public void actualizarEtapaActual(MyPair estado) {
		this.dto.setEstado(estado);
	}

	public MyPair getTipoPedidoKey() {
		MyPair out = null;
		if (this.dto.isPedidoConfirmado()) {
			out = this.getDtoUtil().getImportacionEstadoPedidoEnviado();
		} else {
			out = this.getDtoUtil().getImportacionEstadoSolicitandoCotizacion();
		}
		return out;
	}

	/******************************************************************************************/	
	
	
	/************************** ASIGNACION DEL TIPO DE IMPORTACION ****************************/
	
	private MyPair tipoSelected = new MyPair();
	private boolean prorrateado = false;	
	
	@Command @NotifyChange("*")
	public void asignarTipoImportacion(){
		if (this.operacionValidaPedido(MODIFICAR_TIPO_IMPORTACION) == false) {			
			this.tipoSelected = new MyPair();
			return;
		}
		this.dto.setTipo(this.tipoSelected);
	}
	
	@Command @NotifyChange("*")
	public void checkCifProrrateado(){
		if (this.operacionValidaPedido(MODIFICAR_TIPO_IMPORTACION) == false) {
			this.prorrateado = !this.prorrateado;
			return;
		}
		this.dto.setCifProrrateado(this.prorrateado);
	}
	
	/******************************************************************************************/	
	
	
	/**************************** RECALCULO DE COSTOS MONEDA LOCAL ****************************/
	
	/**
	 * Recalcula los costos de la Orden de Compra y las facturas
	 * Es usado cuando se realizan acciones que afectan a los costos
	 * ej: cuando se modifica la moneda..
	 */
	private void recalcularCostosMonedaLocal(){
		double tipoCambio = this.dto.getCambio();
				
		for (ImportacionPedidoCompraDetalleDTO item : this.dto.getImportacionPedidoCompraDetalle()) {
			double costoGs = item.getCostoProformaDs() * tipoCambio;
			item.setCostoProformaGs(costoGs);
		}
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				double costoGs = item.getCostoDs() * tipoCambio;
				double costoSPgs = item.getCostoSinProrrateoDs() * tipoCambio;
				item.setCostoGs(costoGs);
				item.setCostoSinProrrateoGs(costoSPgs);
			}
			
		}
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	/******************************************************************************************/	
	
	
	/************************************* VALIDACION PEDIDO **********************************/
	
	// Orden Compra
	private static int ELIMINAR_ITEM_ORDEN = 1;
	private static int INSERTAR_ITEM_ORDEN = 2;
	private static int UPLOAD_PEDIDO_COMPRA = 3;
	private static int UPLOAD_PROFORMA = 4;
	private static int ENVIAR_CORREO = 5;
	private static int DERIVAR_PEDIDO = 6;
	private static int CONFIRMAR_ORDEN_COMPRA = 7;
	private static int MODIFICAR_TIPO_IMPORTACION = 8;
	
		
	private boolean operacionValidaPedido(int operacion){
		boolean out = true;
		String mensaje = "No se puede realizar la operación debido a: \n";
		
		if (this.dto.isPedidoConfirmado() 
				&& (operacion != ENVIAR_CORREO)
					&& (operacion != MODIFICAR_TIPO_IMPORTACION)) {
			mensaje += "\n - La Orden de Compra ya fue confirmada.";
			out = false;			
		}
		
		if(operacion == ELIMINAR_ITEM_ORDEN){			
			if (this.selectedOrdenItems.size() == 0) {
				mensaje += "\n - No se ha seleccionado ningún ítem.";
				out = false;
			}			
		}
		
		if (operacion == INSERTAR_ITEM_ORDEN) {
			if (this.dto.getProveedor().esNuevo() == true) {
				mensaje += "\n - Debe seleccionar un Proveedor.";
				out = false;
			}
		}
		
		if (operacion == UPLOAD_PEDIDO_COMPRA) {
			if (this.dto.getProveedor().esNuevo() == true) {
				mensaje += "\n - Debe seleccionar un Proveedor.";
				out = false;
			}
		}
		
		if (operacion == UPLOAD_PROFORMA) {
			if (this.dto.getImportacionPedidoCompraDetalle().size() == 0) {
				mensaje += "\n - Debe ingresar al menos un ítem.";
				out = false;
			}			
		}
		
		if (operacion == ENVIAR_CORREO) {
			if (this.dto.esNuevo()) {
				mensaje += "\n - Debe Guardar los Datos antes de enviar el Correo.";
				out = false;
			}
			if (this.dto.getImportacionPedidoCompraDetalle().size() == 0) {
				mensaje = "\n - Debe ingresar al menos un item.";
				out = false;
			}			
		}
		
		if (operacion == DERIVAR_PEDIDO) {
			if (this.comprobarGrupo() == false) {
				mensaje += "\n - el Propietario actual es: " + this.dto.getPropietario();
				out = false;
			}
			if (this.dto.getImportacionPedidoCompraDetalle().size() == 0) {
				mensaje += "\n - Debe ingresar al menos un ítem.";
				out = false;
			}
		}
		
		if (operacion == CONFIRMAR_ORDEN_COMPRA) {
			
			if (this.dto.esNuevo() == true) {
				mensaje += "\n - Debe grabar la Importación.";
				out = false;
			}
			
			if (this.proformaCargada() == false) {
				mensaje += "\n - Aún no fue ingresado el costo de todos los ítems.";
				out = false;
			}
		}
		
		if (operacion == MODIFICAR_TIPO_IMPORTACION) {
			Object[] obj = this.verificarItemsDeReferencia();
			mensaje += (String) obj[1];
			out = (boolean) obj[0];						
		}
		
		if (out == false) {
			mensajeError(mensaje);
		}
		return out;
	}	
	
	private boolean proformaCargada(){
		boolean out = true;
		for (ImportacionPedidoCompraDetalleDTO d : this.dto.getImportacionPedidoCompraDetalle()) {
			if ((d.getCostoProformaDs() >= 0) && (d.getCostoProformaDs() <= 0.0001)) {
				out = false;
			}
		}
		return out;
	}
	
	
	/**
	 * Verifica si es posible la modificacion del tipo de Importacion
	 * siguiendo la sgte regla sobre las facturas:
	 * -Si se modifica a CIF prorrateado no debe existir items de gastos
	 * -Si se modifica a CIF no prorrateado no debe existir items de prorrateo
	 * -Si se modifica a FOB no debe existir items de prorrateo ni gastos
	 * -Si se modifica a CF no debe existir items de prorrateo ni gasto de flete
	 * @return 
	 * [0]:booleano que indica si es posible modificar
	 * [1]:String con el contenido del mensaje
	 */
	private Object[] verificarItemsDeReferencia(){
		boolean out = true;
		String msg = "- Se ha encontrado los sgts ítems de referencia:";
		
		List<ImportacionFacturaDTO> facs = this.dto.getImportacionFactura();
		Hashtable<Long, ImportacionFacturaDetalleDTO> facsTable = new Hashtable<>();
		
		if (facs.size() == 0) {
			return new Object[]{out, ""};
		}
		
		MyPair tipoSelected = this.tipoSelected;
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		MyPair tipoFOB = utilDto.getTipoImportacionFOB();
		MyPair tipoCF = utilDto.getTipoImportacionCF();
		boolean prorrateado = this.prorrateado;
		
		MyPair itemTipoGastoFlete = utilDto.getTipoCompraGastoFlete();
		MyPair itemTipoGastoSeguro = utilDto.getTipoCompraGastoSeguro();
		MyPair itemTipoProrrFlete = utilDto.getTipoCompraProrrateoFlete();
		MyPair itemTipoProrrSeguro = utilDto.getTipoCompraProrrateoSeguro();
		
		for (ImportacionFacturaDTO fac : facs) {
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				if (item.isGastoDescuento() == true) {
					facsTable.put(item.getTipoGastoDescuento().getId(), item);
				}
			}
		}
		
		ImportacionFacturaDetalleDTO itemGastoFlete = facsTable.get(itemTipoGastoFlete.getId());
		ImportacionFacturaDetalleDTO itemGastoSeguro = facsTable.get(itemTipoGastoSeguro.getId());
		ImportacionFacturaDetalleDTO itemProrrFlete = facsTable.get(itemTipoProrrFlete.getId());
		ImportacionFacturaDetalleDTO itemProrrSeguro = facsTable.get(itemTipoProrrSeguro.getId());
		
		//Si el tipo es CIF prorrateado
		if ((tipoSelected.compareTo(tipoCIF) == 0)
				&& (prorrateado == true)
					&& (itemGastoFlete != null || itemGastoSeguro != null)) {
			out = false;
			msg += "\n - ítems de Gastos.";
		}		
		
		//Si el tipo es CIF pero no prorrateado
		if ((tipoSelected.compareTo(tipoCIF) == 0)
				&& (prorrateado == false)
					&& (itemProrrFlete != null || itemProrrSeguro != null)) {
			out = false;
			msg += "\n - ítems de Prorrateo.";
		}
		
		//Si el tipo es FOB
		if ((tipoSelected.compareTo(tipoFOB) == 0)
				&& (itemProrrFlete != null || itemProrrSeguro != null
						|| itemGastoFlete != null || itemGastoSeguro != null)) {
			out = false;
			msg += "\n - ítems de Prorrateo y/o de Gastos";
		}
		
		//Si el tipo es CF
		if ((tipoSelected.compareTo(tipoCF) == 0)
				&& (itemProrrFlete != null || itemProrrSeguro != null
						|| itemGastoSeguro != null)) {
			out = false;
			msg += "\n - ítems de Prorrateo y/o de seguro";
		}
		
		return new Object[]{out, msg};
	}
	
	/******************************************************************************************/	
	
	
	/***************************************** AGENDA *****************************************/
	
	private static String EVENTO_DERIVAR_ORDEN_COMPRA = "La Orden de Compra fue derivada a: ";
	private static String EVENTO_CONFIRMAR_ORDEN_COMPRA = "Orden Compra Confirmada..";
	private static String EVENTO_IMPORTACION_ANULADA = "La Importación fue anulada - Motivo: ";

	
	@Override
	public int getCtrAgendaTipo(){
		return ControlAgendaEvento.COTIZACION;
	}
	
	@Override
	public String getCtrAgendaKey(){
		return this.dto.getNumeroPedidoCompra();
	}
	
	@Override
	public String getCtrAgendaTitulo(){
		return "[Orden Compra: " + this.getCtrAgendaKey() + "]";
	}
	
	@Override
	public boolean getAgendaDeshabilitado(){
		return this.dto.esNuevo();
	}

	/******************************************************************************************/
	
	
	/***************************************** IMPRIMIR ***************************************/
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return this.dto.esNuevo();
	}
	
	@Override
	public void showImprimir() {
		this.imprimir();
	}
	
	/******************************************************************************************/
	
	
	/*********************************** INFORMACION ADICIONAL ********************************/
	
	@Override
	public boolean getInformacionDeshabilitado() {
		return this.dto.esNuevo();
	}
	
	@Override
	public void showInformacion() throws Exception {
		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Información Adicional");
		wp.setHigth("400px");
		wp.setWidth("400px");
		wp.setDato(this);
		wp.setSoloBotonCerrar();
		wp.show(Configuracion.INFO_ADICIONAL_IMPORTACION_ZUL);
	}
	
	/******************************************************************************************/
	
	
	/*********************************** ANULAR IMPORTACION ***********************************/
	
	@Override
	public void showAnular() throws Exception {
		
		String motivo = this.getMotivoAnulacion();
		
		if (motivo.length() > 0) {
			this.dto.setEstado(this.getDtoUtil().getImportacionEstadoAnulado());
			this.dto.setReadonly();
			this.saveDTO(this.dto);
			this.enmascarar();
			this.getCtrAgenda().addDetalle(this.getCtrAgendaTipo(),
					this.getCtrAgendaKey(), 0, EVENTO_IMPORTACION_ANULADA + motivo,
					null);
			BindUtils.postNotifyChange(null, null, this.dto, "estado");
		}
	}
	
	/******************************************************************************************/
	
	/**
	 * Importacion en curso
	 */
	@Command
	public void setImportacionEnCurso() throws Exception {
		if (!this.mensajeSiNo("Desea volcar a Importación en Curso..?")) 
			return;
		this.dto.setAuxi(Configuracion.SIGLA_IMPORT_EN_CURSO);
		this.dto = (ImportacionPedidoCompraDTO) this.saveDTO(dto);
		this.mensajePopupTemporal("Volcado a Importación en Curso");
	}
	

	/****************************************** UTILES ****************************************/

	@Wire
	private Tab tab1;	
	@Wire
	private Tab tab2;	
	@Wire
	private Tab tab3;	
	@Wire
	private Tab tab4;	
	@Wire
	private Tab tab5;	
	@Wire
	private Tab tab6;		
	@Wire
	private Tabbox tbx;
	@Wire
	private Tabpanel tp4;
	
	private UtilDTO utilDto = this.getDtoUtil();
	private MyArray ordenCompraImportacion = utilDto.getTmOrdenCompraImportacion();
	
	private void seleccionarTab() throws Exception {
		
		if (this.operacionHabilitada("VerDatosGenerales") == true) {
			tab1.setSelected(true);
			
		} else if (this.operacionHabilitada("VerRecepcion") == true) {
			tab4.setSelected(true);
			tbx.setSelectedPanel(tp4);
		}
	}	
	
	@Command
	@NotifyChange({"totalProformaGs", "totalProformaDs", "totalImporteFactura", 
		"itemsCostoFinal", "totalCostoFinal"})
	public void notificarCambios() {
	}	
	
	private String emptyMsgFactura = "SELECCIONE UNA FACTURA...";

	public String getEmptyMsgFactura() {
		return emptyMsgFactura;
	}

	public void setEmptyMsgFactura(String emptyMsgFactura) {
		this.emptyMsgFactura = emptyMsgFactura;
	}
	
	public boolean getCheckmarkOrdenCompra(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getImportacionPedidoCompraDetalle().size() > 0) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckmarkFactura(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.selectedImportacionFactura.getDetalles().size() > 0) {
			out = true;
		}
		return out;
	}
		
	private void sugerirValores(ImportacionPedidoCompraDTO dto) throws Exception{
		dto.setEstado(this.getDtoUtil().getImportacionEstadoConfirmado());
		dto.setTipoMovimiento(ordenCompraImportacion);
		dto.setMoneda(this.getDtoUtil().getMonedaDolaresConSimbolo());
		dto.setCambio(this.getTipoCambio());
		this.prorrateado = false;
		this.refreshTipoCambio();
	}
	
	@Command
	public void updateTipoCambio() {
		this.recalcularCostosMonedaLocal();
		BindUtils.postNotifyChange(null, null, this.dto, "cambio");
	}	
	
	//determina si el checkbox [cif prorrateado] es visible
	@DependsOn("dto.tipo")
	public boolean isCheckProrrateadoVisible(){
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		return this.dto.getTipo().compareTo(tipoCIF) == 0;
	}
	
	public String concatenar(String uno, String dos) {
		return uno + " " + dos;
	}
	
	/******************************************************************************************/
	
	
	/************************** ELIMINAR ITEM IMPORTACION FACTURA *****************************/
	
	private List<ImportacionFacturaDetalleDTO> selectedFacturaItems = new ArrayList<ImportacionFacturaDetalleDTO>();
	private String facturaItemsEliminar;	

	@Command
	@NotifyChange("*")
	public void eliminarItemFactura(){
		
		if (this.operacionValidaFactura(ELIMINAR_ITEM_FACTURA) == false) {
			return;
		}
		
		if (this.confirmarEliminarItemFactura() == true) {
			for (ImportacionFacturaDetalleDTO d : this.selectedFacturaItems) {
				this.selectedImportacionFactura.getDetalles().remove(d);
			}
			this.selectedFacturaItems = new ArrayList<ImportacionFacturaDetalleDTO>();
			this.recalcularCostosSinProrrateo();
		}		
	}
	
	private boolean confirmarEliminarItemFactura(){
		this.facturaItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (ImportacionFacturaDetalleDTO d : this.selectedFacturaItems) {
			this.facturaItemsEliminar = this.facturaItemsEliminar + "\n - " + d.getArticulo().getPos1();
		}		
		return this.mensajeSiNo(this.facturaItemsEliminar);
	}
	
	/******************************************************************************************/	
	
	
	/******************************** INSERTAR ITEM FACTURA ***********************************/

	private ImportacionFacturaDetalleDTO nvoItem = new ImportacionFacturaDetalleDTO();		
	private ImportacionPedidoCompraDetalleDTO nvoItemProforma = new ImportacionPedidoCompraDetalleDTO();

	@Command
	@NotifyChange("*")
	public void insertarItemFactura(){
		
		if (this.operacionValidaFactura(INSERTAR_ITEM_FACTURA) == false) {
			return;
		}
		
		try {
			this.nvoItem = new ImportacionFacturaDetalleDTO();			
			WindowPopup w = new WindowPopup();
			w.setTitulo("Insertar Ítem");
			w.setModo(WindowPopup.NUEVO);
			w.setDato(this);
			w.setWidth("450px");
			w.setHigth("320px");
			w.setCheckAC(new ValidadorInsertarItemFactura(this));
			w.show(Configuracion.INSERTAR_ITEM_IMPORTACION_FACTURA_ZUL);
			if (w.isClickAceptar()) {
				this.selectedImportacionFactura.getDetalles().add(nvoItem);
				this.recalcularCostosSinProrrateo();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.mensajeError(e.getMessage());
		}
	}	
	
	/******************************************************************************************/	
	
	
	/*********************************** FACTURA SELECCIONADA *********************************/

	private ImportacionFacturaDTO selectedImportacionFactura = new ImportacionFacturaDTO();

	public ImportacionFacturaDTO getSelectedImportacionFactura() {
		return selectedImportacionFactura;
	}

	public void setSelectedImportacionFactura(
			ImportacionFacturaDTO selectedImportacionFactura) {
		this.selectedImportacionFactura = selectedImportacionFactura;
	}

	/******************************************************************************************/
	
	
	/************************************ NUEVA FACTURA ***************************************/
	
	private static String MSG_ADD_ITEMS = "AGREGUE ÍTEMS A LA FACTURA...";	 
	
	private ImportacionFacturaDTO nvaFactura = new ImportacionFacturaDTO();	

	@Command
	@NotifyChange("*")
	public void nuevaFactura() {
		
		try {
			this.nvaFactura = new ImportacionFacturaDTO();
			this.nvaFactura.setProveedor(this.dto.getProveedor());
			this.nvaFactura.setMoneda(this.dto.getMoneda());
			this.nvaFactura.setCondicionPago(this.dto.getProveedorCondicionPago());
			this.nvaFactura.setTipoMovimiento(this.dto.isCondicionContado() ? 
					getDtoUtil().getTmFacturaImportacionContado() : 
						getDtoUtil().getTmFacturaImportacionCredito()); 
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo(Configuracion.TEXTO_AGREGAR_NUEVA_FACTURA);
			w.setWidth("400px");
			w.setHigth("340px");
			w.setDato(this);
			w.show(Configuracion.INSERTAR_NUEVA_FACTURA_ZUL);
			if (w.isClickAceptar()) {
				this.dto.getImportacionFactura().add(this.nvaFactura);
				this.setSelectedImportacionFactura(this.nvaFactura);
				this.emptyMsgFactura = MSG_ADD_ITEMS;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}		
	}	
	
	
	/******************************* DETALLE IMPORTACION FACTURA ******************************/
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void notificarCostoDs(@BindingParam("item") ImportacionFacturaDetalleDTO item,
			@BindingParam("comp") Doublebox comp){
		double costoGs = item.getCostoGs();
		if (costoGs < 0) {
			Clients.showNotification("El costo no puede ser negativo..", "error", comp, null, 2000);
			item.setCostoGs(0);
			costoGs = 0;
		}
		item.setCostoDs(costoGs / this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, item, "costoDs");
		BindUtils.postNotifyChange(null, null, item, "costoGs");
	}
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void notificarCostoGs(@BindingParam("item") ImportacionFacturaDetalleDTO item,
			@BindingParam("comp") Doublebox comp){
		double costoDs = item.getCostoDs();
		if (costoDs < 0) {
			Clients.showNotification("El costo no puede ser negativo..", "error", comp, null, 2000);
			item.setCostoDs(0);
			costoDs = 0;
		}
		item.setCostoGs(costoDs * this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, item, "costoGs");
		BindUtils.postNotifyChange(null, null, item, "costoDs");		
	}
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void validarCantidad(@BindingParam("item") ImportacionFacturaDetalleDTO item,
			@BindingParam("comp") Intbox comp){
		if (item.getCantidad() < 0) {
			Clients.showNotification("La cantidad no puede ser negativa..", "error", comp, null, 2000);
			item.setCantidad(0);
		}
		BindUtils.postNotifyChange(null, null, item, "cantidad");
	}
	
	/**
	 * Retorna los sgtes valores de la factura seleccionada:
	 * [0]:Total Importe Gs.
	 * [1]:Total Importe Ds.
	 * [2]:Total SinProrrateo Gs.
	 * [3]:Total SinProrrateo Ds.
	 * [4]:Total itemsProrrateo Ds. (ProrrateoFlete + ProrrateoSeguro)
	 */
	public Double[] getTotalImporteFactura(){
		
		double totalGs = 0; double totalDs = 0;
		double totalSPgs = 0; double totalSPds = 0;
		double dctoGs = this.selectedImportacionFactura.getDescuentoGs();
		double dctoDs = this.selectedImportacionFactura.getDescuentoDs();
		double totalProrrateoDs = 0;
		
		for (ImportacionFacturaDetalleDTO item : this.selectedImportacionFactura.getDetalles()) {
			if (item.isValorProrrateo() == false) {
				totalGs += item.getImporteGsCalculado();
				totalDs += item.getImporteDsCalculado();				
				totalSPgs += item.getImporteSinProrrateoGs();
				totalSPds += item.getImporteSinProrrateoDs();
			} else {
				totalProrrateoDs += item.getImporteDsCalculado();
			}
		}
		
		if (this.dto.isCifProrrateado() == false) {
			totalProrrateoDs = 0;
		}
		
		Double[] out = {(totalGs - dctoGs), (totalDs - dctoDs), (totalSPgs - dctoGs), (totalSPds - dctoDs),
						 totalProrrateoDs};
		return out;
	}
	
	public ImportacionFacturaDetalleDTO setearImportacionFacturaDetalleDTO(
			MyArray articulo, Double cantidad, Double costoDs) {

		ImportacionFacturaDetalleDTO out = new ImportacionFacturaDetalleDTO();
		out.setArticulo(articulo);
		out.setCantidad(cantidad.intValue());
		out.setCostoDs(costoDs);
		out.setCostoGs(costoDs * this.dto.getCambio());
		
		return out;
	}
	
	/******************************************************************************************/
	
	
	/***************************** SUPRIMIR IMPORTACION FACTURA *******************************/
	
	@Command
	@NotifyChange("*")
	public void suprimirImportacionFactura(){
		
		if (this.operacionValidaFactura(SUPRIMIR_FACTURA) == false) {
			return;
		}
		
		String nroFactura = this.selectedImportacionFactura.getNumero();		
		if (this.mensajeSiNo("Desea eliminar la Factura Nro: \n" + nroFactura)) {
			this.dto.getImportacionFactura().remove(this.selectedImportacionFactura);					
			this.selectedImportacionFactura = new ImportacionFacturaDTO();
			this.addEventoAgenda("Se eliminó de la lista la Factura nro. " + nroFactura);	
		}
	}
	
	/******************************************************************************************/	
	
	
	/*********************** IMPORTACION FACTURA [GASTOS - DESCUENTOS] ************************/
	
	@Command @NotifyChange("*")
	public void agregarGastosDescuentos() throws Exception {				
		this.nvoItem = new ImportacionFacturaDetalleDTO();
		this.nvoItem.setGastoDescuento(true);			
		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.NUEVO);
		w.setDato(this);
		w.setTitulo("Agregar Gastos/Descuentos");
		w.setWidth("400px");
		w.setHigth("280px");
		w.show(Configuracion.IMPORTACION_GASTOS_DESCTOS_ZUL);
		if (w.isClickAceptar()) {
						
			this.selectedImportacionFactura.getDetalles().add(this.nvoItem);
			
			if (this.nvoItem.isValorProrrateo() == true) {
				this.actualizarCostosSinProrrateo();
			} else {
				this.recalcularCostosSinProrrateo();
			}			
		}		
	}	
	
	public List<MyArray> getItemsGastoDescuento() throws Exception{
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Articulo art : rr.getItemsGastoDescuento()) {
			MyArray a = new MyArray(art.getCodigoInterno(), art.getCodigoProveedor(), art.getCodigoOriginal(), art.getDescripcion(), art.isServicio());
			a.setId(art.getId());
			out.add(a);
		}		
		return out;
	}
	
	@DependsOn("nvoItem.articulo")
	public List<MyPair> getTiposGastoDescuento(){
		List<MyPair> out = new ArrayList<MyPair>();
		
		List<MyPair> descuentos = utilDto.getCompraTiposDescuento();
		List<MyPair> gastos = utilDto.getCompraTiposGastos();
		List<MyPair> prorrateos = utilDto.getCompraTiposProrrateo();		
		
		MyArray art = this.nvoItem.getArticulo();
		
		if (art != null) {
			String codigo = (String) art.getPos1();
			
			if (codigo.indexOf(Configuracion.CODIGO_ITEM_DESCUENTO_KEY) >= 0) {
				out = descuentos;
			}
			
			if (codigo.indexOf(Configuracion.CODIGO_ITEM_GASTO_KEY) >= 0) {
				out = gastos;
			}
			
			if (codigo.indexOf(Configuracion.CODIGO_ITEM_PRORRATEO_KEY) >= 0) {
				out = prorrateos;
			}
			
		}
		return out;
	}	
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void dolarizarDescuento(){
		double descGs = this.selectedImportacionFactura.getDescuentoGs();
		this.selectedImportacionFactura.setDescuentoDs(descGs / this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, this.selectedImportacionFactura, "descuentoDs");
	}
	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void guaranizarDescuento(){
		double descDs = this.selectedImportacionFactura.getDescuentoDs();
		this.selectedImportacionFactura.setDescuentoGs(descDs * this.dto.getCambio());
		BindUtils.postNotifyChange(null, null, this.selectedImportacionFactura, "descuentoGs");
	}	
	
	//actualiza los valores de la columna 'costo sin Prorrateo'	
	private void actualizarCostosSinProrrateo(){
		
		ImportacionFacturaDTO factura = this.selectedImportacionFactura;
		double totalDs = this.getTotalImporteFactura()[1];
		double valorProrrateo = this.getTotalImporteFactura()[4];
		double porcProrrateo = m.obtenerPorcentajeDelValor(valorProrrateo, totalDs);
		
		factura.setPorcProrrateo(porcProrrateo);
		
		for (ImportacionFacturaDetalleDTO item : factura.getDetalles()) {
			
			double valorProrrateoGs = m.obtenerValorDelPorcentaje(item.getCostoGs(), porcProrrateo);
			double valorProrrateoDs = m.obtenerValorDelPorcentaje(item.getCostoDs(), porcProrrateo);
			
			double valorSinProrrateoGs = item.getCostoGs() - valorProrrateoGs;
			double valorSinProrrateoDs = item.getCostoDs() - valorProrrateoDs;
			
			if (valorProrrateo == 0) {
				valorSinProrrateoGs = 0;
				valorSinProrrateoDs = 0;
			}
			
			item.setCostoSinProrrateoGs(valorSinProrrateoGs);
			item.setCostoSinProrrateoDs(valorSinProrrateoDs);
		}
	}
	
	/**
	 * Recalcula los valores de las columnas costo S/P
	 * Se usa cuando se realizan acciones que afectan al costo y 
	 * es necesario recalcular los valores..
	 * ej: cuando se elimina un item de la factura..
	 */
	private void recalcularCostosSinProrrateo(){		
		this.actualizarCostosSinProrrateo();
		BindUtils.postNotifyChange(null, null, this.selectedImportacionFactura, "*");
	}
	
	/******************************************************************************************/	
	
	
	
	
	/************************************ IMPORTAR ITEMS **************************************/
	
	private List<ImportacionFacturaDetalleDTO> itemsImportar = new ArrayList<ImportacionFacturaDetalleDTO>();
	private List<ImportacionFacturaDetalleDTO> selectedItemsImportar = new ArrayList<ImportacionFacturaDetalleDTO>();	

	@Command
	@NotifyChange("*")
	public void importarItems(){
		
		if (this.operacionValidaFactura(IMPORTAR_ITEMS) == false) {
			return;
		}
		
		if (this.selectedImportacionFactura.getDetalles().size() > 0) {
			if (!this.mensajeSiNo("Esta Factura ya tiene ítems en el detalle, "
					+ "si continúa se eliminarán.. \n \n Desea continuar?")) {
				return;
			}
		}
		
		try {
			this.exportarItems();
			WindowPopup w = new WindowPopup();
			w.setModo(WindowPopup.NUEVO);
			w.setDato(this);
			w.setCheckAC(new ValidadorImportarItems(this));
			w.setTitulo("Importar Ítems de la Orden de Compra..");
			w.setWidth("950px");
			w.setHigth("500px");
			w.show(Configuracion.IMPORTAR_ITEMS_IMPORTACION_ZUL);
			if (w.isClickAceptar()) {
				this.selectedImportacionFactura.setDetalles(selectedItemsImportar);
			}
			this.selectedItemsImportar = new ArrayList<ImportacionFacturaDetalleDTO>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void exportarItems(){
		this.itemsImportar = new ArrayList<ImportacionFacturaDetalleDTO>();
		for (ImportacionPedidoCompraDetalleDTO d : this.dto.getImportacionPedidoCompraDetalle()) {
			ImportacionFacturaDetalleDTO item = new ImportacionFacturaDetalleDTO();
			item.setArticulo(d.getArticulo());
			item.setCantidad(d.getCantidad());
			item.setCostoDs(d.getCostoProformaDs());
			item.setCostoGs(d.getCostoProformaGs());
			itemsImportar.add(item);
		}
	}
	
	/******************************************************************************************/
	
	
	/**************************** DERIVAR IMPORTACION FACTURA *********************************/
	
	private String selectedPropietarioImportacionFactura = "";	
	
	public List<String> getPropietariosImportacionFactura(){
		
		List<String> out = new ArrayList<String>();
		out.add(Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION);
		out.add(Configuracion.PROPIETARIO_AUDITORIA_DESCRIPCION);
		out.add(Configuracion.PROPIETARIO_VENTAS_DESCRIPCION);
		out.add(Configuracion.PROPIETARIO_ADMINISTRACION_DESCRIPCION);
		
		return out;
	}
	
	private Window w;
	@Command
	public void abrirPropietariosImportacionFactura(){
		this.selectedPropietarioImportacionFactura = "";
		w = (Window) Executions.createComponents(
				Configuracion.LISTA_PROPIETARIOS_IMPORTACION_FACTURA_ZUL, this.mainComponent, null);
		w.doModal();
	}
	
	@Command
	@NotifyChange("*")
	public void derivarImportacionFactura(){
		
		if (this.selectedPropietarioImportacionFactura.compareTo(
					Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION) == 0) {
			this.selectedImportacionFactura.setPropietarioActual(Configuracion.PROPIETARIO_IMPORTACION_KEY);
		}
		
		if (this.selectedPropietarioImportacionFactura.compareTo(
					Configuracion.PROPIETARIO_AUDITORIA_DESCRIPCION) == 0) {
			this.selectedImportacionFactura.setPropietarioActual(Configuracion.PROPIETARIO_AUDITORIA_KEY);
		}
		
		if (this.selectedPropietarioImportacionFactura.compareTo(
					Configuracion.PROPIETARIO_VENTAS_DESCRIPCION) == 0) {
			this.selectedImportacionFactura.setPropietarioActual(Configuracion.PROPIETARIO_VENTAS_KEY);
		}
		
		if (this.selectedPropietarioImportacionFactura.compareTo(
					Configuracion.PROPIETARIO_ADMINISTRACION_DESCRIPCION) == 0) {
			this.selectedImportacionFactura.setPropietarioActual(Configuracion.PROPIETARIO_ADMINISTRACION_KEY);
		}
		w.detach();
	}
	
	// Comportamiento Checkbox (Habilitado/Desabilitado)
	public boolean getCheckboxImportacionEnable() {
		boolean out = false;
		if (this.selectedImportacionFactura.getPropietarioActual() == Configuracion.PROPIETARIO_IMPORTACION_KEY) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckboxAuditoriaEnable(){
		boolean out = false;
		if (this.selectedImportacionFactura.getPropietarioActual() == Configuracion.PROPIETARIO_AUDITORIA_KEY) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckboxVentasEnable(){
		boolean out = false;
		if (this.selectedImportacionFactura.getPropietarioActual() == Configuracion.PROPIETARIO_VENTAS_KEY) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckboxAdministracionEnable(){
		boolean out = false;
		if (this.selectedImportacionFactura.getPropietarioActual() == Configuracion.PROPIETARIO_ADMINISTRACION_KEY) {
			out = true;
		}
		return out;
	}
	
	/******************************************************************************************/	
	
	
	/******************************** DESCUENTOS POR ITEM *************************************/
	
	/**
	 * Si el descuento es ingresado como porcentaje: se calcula el valor en guaranies y dolares..
	 * Si el descuento es ingresado como valor: se toma el valor como dolares..
	 * */	
	@Command
	@NotifyChange({"totalImporteFactura", "itemsCostoFinal"})
	public void descuentoPorItem(@BindingParam("item") ImportacionFacturaDetalleDTO item,
			@BindingParam("comp") Textbox comp){
		String desc = item.getTextoDescuento().trim();
		Double valorDesc = (double) 0;
		if (m.containsOnlyNumbers(desc) == true) {
			valorDesc = Double.parseDouble(desc);
			if (valorDesc < 0) {
				valorDesc = valorDesc * -1;
			}
			item.setDescuentoDs(valorDesc);
			item.setDescuentoGs(valorDesc * this.dto.getCambio());
		} else if (m.containsOnlyNumbersAndPercent(desc) == true) {
			valorDesc = Double.parseDouble(desc.replace("%", ""));
			if (valorDesc > 100) {
				m.mensajePopupTemporal("'"+desc+"' no es un valor de descuento correcto..", "error", comp);
				item.setTextoDescuento("0");
				item.setDescuentoDs(0);
				item.setDescuentoGs(0);
			} else {
				item.setDescuentoGs((item.getCostoGs() * valorDesc) / 100);
				item.setDescuentoDs((item.getCostoDs() * valorDesc) / 100);
			}			
		} else{
			m.mensajePopupTemporal("'"+desc+"' no es un valor de descuento correcto..", "error", comp);
			item.setTextoDescuento("0");
			item.setDescuentoDs(0);
			item.setDescuentoGs(0);
		}
		BindUtils.postNotifyChange(null, null, item, "descuentoGs");
		BindUtils.postNotifyChange(null, null, item, "descuentoDs");
	}
	
	/************************************************************************************************/	
	
	
	/*********************************** RECEPCION DE MERCADERIAS **********************************/
	
	private ImportacionFacturaDTO selectedRecepcion;	

	@Command
	@NotifyChange("*")
	public void confirmarRecepcion() {
		boolean continuar = true;

		if (this.operacionValidaFactura(CONFIRMAR_RECEPCION) == false) {
			return;
		}

		if (this.existeDiferencias() == true) {
			continuar = this
					.mensajeSiNo("Existen cantidades con diferencias..Desea continuar?");
		}

		if (continuar == true) {
			if (mensajeSiNo("Desea Confirmar la Recepción de la Factura Nro. "
					+ this.selectedRecepcion.getNumero())) {
				this.selectedRecepcion.setRecepcionConfirmada(true);
				this.addEventoAgenda("Se confirmó la Recepción de la Factura Nro. "
						+ this.selectedRecepcion.getNumero());
			}
		}
	}	
	
	/**
	 * @return booleano que indica si hay diferencias en la recepcion de mercaderias..
	 */
	private boolean existeDiferencias() {
		
		if (this.dto.getImportacionFactura().size() == 0) {
			return false;
		}	
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			if (this.existeDiferencias(fac) == true) {
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * @param factura..
	 * @return booleano que indica si hay diferencias en la recepcion de mercaderias..
	 */
	private boolean existeDiferencias(ImportacionFacturaDTO factura) {		
		
			for (ImportacionFacturaDetalleDTO item : factura.getDetalles()) {
				if (item.getDiferenciaCantidad() != 0) {
					return true;
				}
			}
		
		return false;
	}
	
	@Command
	public void imprimirPackingList() {
		ImportacionPackingList ipl = new ImportacionPackingList();
		
		ipl.setDatosReporte(this.getDatosPackingList());
		ipl.setProveedor(this.getDto().getProveedor().getRazonSocial());
		ipl.setNumeroOrden(this.getDto().getNumeroPedidoCompra());
		ipl.setFactura(this.selectedImportacionFactura.getNumero());
		
		//ipl.setApaisada();
		
		ViewPdf vp = new ViewPdf();
		vp.showReporte(ipl, this);		
	}
	
	private List<Object[]> getDatosPackingList() {
		List<Object[]> out = new ArrayList<>();
		List<ImportacionFacturaDetalleDTO> items = new ArrayList<ImportacionFacturaDetalleDTO>();
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				items.add(item);
			}
		}		
		
		for (ImportacionFacturaDetalleDTO item : items) {
			Object[] data = new Object[6];
			data[0] = item.getArticulo().getPos1();
			data[1] = item.getArticulo().getPos1();
			data[2] = item.getArticulo().getPos4();
			data[3] = "";
			data[4] = "";
			data[5] = "";
			out.add(data);
		}
		
		return out;
	}
	
	/***********************************************************************************************/	
	
	
	/************************************ RESUMEN GASTOS DESPACHO ***********************************/
	
	private static String MSG_ERROR_NEGATIVO = "El valor no puede ser negativo..";
	
	private double valorFleteGs = 0;
	private double valorFleteDs = 0;
	private double valorSeguroGs = 0;
	private double valorSeguroDs = 0;
 	
	@DependsOn("dto.tipo")
	public double getValorFleteGs() {
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoFOB = utilDto.getTipoImportacionFOB();
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		
		if (tipo.compareTo(tipoFOB) == 0) {
			return valorFleteGs;
		} else {
			double valor = this.getValorFleteDs();
			return valor * cambio;
		}		
	}

	public void setValorFleteGs(double valorFleteGs) {
		this.valorFleteGs = valorFleteGs;
	}

	@DependsOn("dto.tipo")
	public double getValorFleteDs() {
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoFOB = utilDto.getTipoImportacionFOB();
		
		if (tipo.compareTo(tipoFOB) == 0) {
			return valorFleteDs;
		} else {
			return this.getValoresFromFacturas()[1];
		}		
	}

	public void setValorFleteDs(double valorFleteDs) {
		this.valorFleteDs = valorFleteDs;
	}

	@DependsOn("dto.tipo")
	public double getValorSeguroGs() {
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		
		if (tipo.compareTo(tipoCIF) == 0) {
			double valor = this.getValorSeguroDs();
			return valor * cambio;
		} else {
			return valorSeguroGs;
		}		
	}

	public void setValorSeguroGs(double valorSeguroGs) {
		this.valorSeguroGs = valorSeguroGs;
	}

	@DependsOn("dto.tipo")
	public double getValorSeguroDs() {
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		
		if (tipo.compareTo(tipoCIF) == 0) {
			return this.getValoresFromFacturas()[3];
		} else {
			return valorSeguroDs;
		}		
	}

	public void setValorSeguroDs(double valorSeguroDs) {
		this.valorSeguroDs = valorSeguroDs;
	}
	
	public double getValorFOBds() {		
		return this.getValoresFromFacturas()[5];		
	}
	
	@DependsOn("valorFOBds")	
	public double getValorFOBgs(){
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		return this.getValorFOBds() * cambio;
	}
	
	@DependsOn({"dto.tipo", "valorFleteDs", "valorSeguroDs"})
	public double getValorCIFds(){
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		
		if (tipo.compareTo(tipoCIF) == 0) {
			return this.getValoresFromFacturas()[7];
		} else {
			return this.getValorCIFdsCalculado();
		}
	}
	
	@DependsOn("valorCIFds")
	public double getValorCIFgs(){
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		return this.getValorCIFds() * cambio;
	}
	
	@DependsOn({"dto.resumenGastosDespacho.totalGastosDs", 
				"dto.resumenGastosDespacho.totalGastosImprevistosDs",
				"valorFleteDs", "valorSeguroDs", "valorFOBds"})
	public double getCoeficienteGasto(){		
		ResumenGastosDespachoDTO despacho = this.dto.getResumenGastosDespacho();
		
		double gastosDs = despacho.getTotalGastosDs();
		double gastosImprevistosDs = despacho.getTotalGastosImprevistosDs();
		double flete = this.getValorFleteDs();
		double seguro = this.getValorSeguroDs();				
		
		double totalGastosDs = gastosDs + gastosImprevistosDs + flete + seguro;
		double valorFOBds = this.getValorFOBds();
		
		double coef = totalGastosDs / valorFOBds;
		
		return m.redondeoCuatroDecimales(coef);
	}
	
	/**
	 * Metodo que extrae de las facturas los valores para el 
	 * resumen de Gastos de Despacho...
	 * @return [0]:valorFleteGs  [1]:valorFleteDs [2]:valorSeguroGs 
	 * [3]:valorSeguroDs [4]:valorFOBgs [5]:valorFOBds
	 * [6]:valorCIFgs [7]:valorCIFds
	 */
	private Double[] getValoresFromFacturas() {
		
		double valorFleteGs = 0;
		double valorFleteDs = 0;
		double valorSeguroGs = 0;
		double valorSeguroDs = 0;
		double valorFOBgs = 0;
		double valorFOBds = 0;
		double valorCIFgs = 0;
		double valorCIFds = 0;
		
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			
			for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {
				if (this.dto.getTipo().getText().equals(ImportacionPedidoCompra.TIPO_CIF)) {
					if (!item.getArticulo().getPos6().toString().equals(ArticuloFamilia.CONTABILIDAD)) {					
						valorFOBgs += item.getImporteGsCalculado();
						valorFOBds += item.getImporteDsCalculado();
					}
					if (item.getArticulo().getPos6().toString().equals(ArticuloFamilia.CONTABILIDAD)) {
						valorFleteGs += item.getImporteGsCalculado();
						valorFleteDs += item.getImporteDsCalculado();
					}
					valorCIFgs += item.getImporteGsCalculado();
					valorCIFds += item.getImporteDsCalculado();
					
				} else if (this.dto.getTipo().getText().equals(ImportacionPedidoCompra.TIPO_FOB)) {
					valorFOBgs += item.getImporteGsCalculado();
					valorFOBds += item.getImporteDsCalculado();
					valorCIFgs += item.getImporteGsCalculado();
					valorCIFds += item.getImporteDsCalculado();
				}
			}
		}
		Double[] out = {valorFleteGs, valorFleteDs, valorSeguroGs, valorSeguroDs,
						valorFOBgs, valorFOBds, valorCIFgs, valorCIFds};
		return out;
	}
	
	//calcula el valor CIF en ds
	private double getValorCIFdsCalculado(){
		
		double valFOB = this.getValorFOBds();
		double valFlete = this.getValorFleteDs();
		double valSeguro = this.getValorSeguroDs();
		
		return valFOB + valFlete + valSeguro;
	}
			
	@Command @NotifyChange({"itemsCostoFinal", "valorFleteDs", "valorFleteGs"})
	public void dolarizarFlete(@BindingParam("comp") Doublebox comp){
		double valorGs = this.getValorFleteGs();
		if (valorGs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.setValorFleteGs(0);
			valorGs = 0;
		}
		double result = valorGs / this.dto.getResumenGastosDespacho().getTipoCambio();
		this.setValorFleteDs(result);
	}
	
	@Command @NotifyChange({"itemsCostoFinal", "valorFleteDs", "valorFleteGs"})
	public void guaranizarFlete(@BindingParam("comp") Doublebox comp){
		double valorDs = this.getValorFleteDs();
		if (valorDs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.setValorFleteDs(0);
			valorDs = 0;
		}
		double result = valorDs * this.dto.getResumenGastosDespacho().getTipoCambio();
		this.setValorFleteGs(result);
	}
	
	@Command @NotifyChange({"itemsCostoFinal", "valorSeguroDs", "valorSeguroGs"})
	public void dolarizarSeguro(@BindingParam("comp") Doublebox comp){
		double valorGs = this.getValorSeguroGs();
		if (valorGs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.setValorSeguroGs(0);
			valorGs = 0;
		}
		double result = valorGs / this.dto.getResumenGastosDespacho().getTipoCambio();
		this.setValorSeguroDs(result);
	}
	
	@Command @NotifyChange({"itemsCostoFinal", "valorSeguroDs", "valorSeguroGs"})
	public void guaranizarSeguro(@BindingParam("comp") Doublebox comp){
		double valorDs = this.getValorSeguroDs();
		if (valorDs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.setValorSeguroDs(0);
			valorDs = 0;
		}
		double result = valorDs * this.dto.getResumenGastosDespacho().getTipoCambio();
		this.setValorSeguroGs(result);
	}
	
	@Command
	public void dolarizarIVA(@BindingParam("comp") Doublebox comp){
		double valorGs = this.dto.getResumenGastosDespacho().getTotalIVAgs();
		if (valorGs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.dto.getResumenGastosDespacho().setTotalIVAgs(0);
			valorGs = 0;
		}
		this.dto.getResumenGastosDespacho().setTotalIVAds(valorGs / this.dto.getResumenGastosDespacho().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalIVAds");
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalIVAgs");
	}
	
	@Command
	public void guaranizarIVA(@BindingParam("comp") Doublebox comp){
		double valorDs = this.dto.getResumenGastosDespacho().getTotalIVAds();
		if (valorDs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.dto.getResumenGastosDespacho().setTotalIVAds(0);
			valorDs = 0;
		}
		this.dto.getResumenGastosDespacho().setTotalIVAgs(valorDs * this.dto.getResumenGastosDespacho().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalIVAds");
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalIVAgs");
	}
	
	@Command
	@NotifyChange({"itemsCostoFinal"})
	public void dolarizarGasto(@BindingParam("comp") Doublebox comp){
		double valorGs = this.dto.getResumenGastosDespacho().getTotalGastosGs();
		if (valorGs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.dto.getResumenGastosDespacho().setTotalGastosGs(0);
			valorGs = 0;
		}
		this.dto.getResumenGastosDespacho().setTotalGastosDs(valorGs / this.dto.getResumenGastosDespacho().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalGastosDs");
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalGastosGs");
	}
	
	@Command
	@NotifyChange({"itemsCostoFinal"})
	public void guaranizarGasto(@BindingParam("comp") Doublebox comp){
		double valorDs = this.dto.getResumenGastosDespacho().getTotalGastosDs();
		if (valorDs < 0) {
			m.mensajePopupTemporal(MSG_ERROR_NEGATIVO, "error", comp);
			this.dto.getResumenGastosDespacho().setTotalGastosDs(0);
			valorDs = 0;
		}
		this.dto.getResumenGastosDespacho().setTotalGastosGs(valorDs * this.dto.getResumenGastosDespacho().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalGastosDs");
		BindUtils.postNotifyChange(null, null, this.dto.getResumenGastosDespacho(), "totalGastosGs");
	}
	
	@Command
	public void notificarValores(){
		
		ResumenGastosDespachoDTO resumen = this.dto.getResumenGastosDespacho();
		double cambio = this.dto.getResumenGastosDespacho().getTipoCambio();		
		double valFleteDs = this.getValorFleteDs();
		double valSeguroDs = this.getValorSeguroDs();
		double valIVAds = this.dto.getResumenGastosDespacho().getTotalIVAds();
		double valGastoDs = this.dto.getResumenGastosDespacho().getTotalGastosDs();		
		
		this.setValorFleteGs(valFleteDs * cambio);
		this.setValorSeguroGs(valSeguroDs * cambio);
		resumen.setTotalIVAgs(valIVAds * cambio);
		resumen.setTotalGastosGs(valGastoDs * cambio);
		
		BindUtils.postNotifyChange(null, null, this, "valorCIFgs");
		BindUtils.postNotifyChange(null, null, this, "valorFOBgs");
		BindUtils.postNotifyChange(null, null, this, "valorFleteGs");
		BindUtils.postNotifyChange(null, null, this, "valorSeguroGs");
		BindUtils.postNotifyChange(null, null, resumen, "totalIVAgs");
		BindUtils.postNotifyChange(null, null, resumen, "totalGastosGs");
	}
	
	//para restringir la modificacion del tipo de cambio del despacho
	@DependsOn("dto.subDiarioConfirmado")
	public boolean isTipoCambioEditable(){
		
		if (this.isDeshabilitado() == true) {
			return false;
		} else {
			return !this.dto.isSubDiarioConfirmado();
		}		
	}
	
	//para restringir la modificacion del valorFlete en despacho
	@DependsOn("dto.tipo")
	public boolean isValorFleteEditable(){
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoFOB = utilDto.getTipoImportacionFOB();
		
		if (this.isDeshabilitado() == true) {
			return false;
		} else {
			return tipo.compareTo(tipoFOB) == 0;
		}		
	}
	
	//para restringir la modificacion del valorSeguro en despacho
	public boolean isValorSeguroEditable(){
		
		MyPair tipo = this.dto.getTipo();
		MyPair tipoCIF = utilDto.getTipoImportacionCIF();
		
		if (this.isDeshabilitado() == true) {
			return false;
		} else {
			return tipo.compareTo(tipoCIF) != 0;
		}
	}
	
	//para setear los valores del despacho en el controlador (this)
	private void setearValoresDespacho(){
		ResumenGastosDespachoDTO desp = this.dto.getResumenGastosDespacho();
		this.setValorFleteDs(desp.getValorFleteDs());
		this.setValorFleteGs(desp.getValorFleteGs());
		this.setValorSeguroDs(desp.getValorSeguroDs());
		this.setValorSeguroGs(desp.getValorSeguroGs());
	}
	
	//para actualizar los valores del ResumenGastoDespachoDTO (se invoca al Grabar).
	private void actualizarValoresDespacho(){
		ResumenGastosDespachoDTO desp = this.dto.getResumenGastosDespacho();
		desp.setValorFleteDs(this.getValorFleteDs());
		desp.setValorFleteGs(this.getValorFleteGs());
		desp.setValorSeguroDs(this.getValorSeguroDs());
		desp.setValorSeguroGs(this.getValorSeguroGs());
		desp.setValorFOBds(this.getValorFOBds());
		desp.setValorFOBgs(this.getValorFOBgs());
		desp.setValorCIFds(this.getValorCIFds());
		desp.setValorCIFgs(this.getValorCIFgs());
		desp.setCoeficiente(this.getCoeficienteGasto());
	}
	
	/**
	 * actualiza el total importe..
	 */
	private void actualizarImporte() {
		double importeGs = 0;
		double importeDs = 0;
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			importeGs += fac.getTotalImporteGs();
			importeDs += fac.getTotalImporteDs();
		}
		this.dto.setTotalImporteGs(importeGs);
		this.dto.setTotalImporteDs(importeDs);
	}
	
	/**
	 * impresion de la compra..
	 */
	private void imprimir() {
		if (!(this.dto.getImportacionFactura().size() > 0)) {
			Clients.showNotification("SIN DATOS DE FACTURA..");
			return;
		}
		
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (MyArray item : this.getItemsCostoFinal()) {
			Object[] obj1 = new Object[] { item.getPos1(), 
					item.getPos5(), 
					Utiles.getRedondeo((double) item.getPos3()), 
					misc.redondeoDosDecimales((double) item.getPos4()), 
					Utiles.getRedondeo((double) item.getPos8()),
					misc.redondeoDosDecimales((double) item.getPos9()) };
			data.add(obj1);
		}
		
		// ordena la lista segun codigo..
		Collections.sort(data, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String rs1 = (String) o1[0];
				String rs2 = (String) o2[0];
				return rs1.compareTo(rs2);
			}
		});

		ReporteYhaguy rep = new ReporteImportacion(this.dto);
		rep.setDatosReporte(data);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	}
	
	/***********************************************************************************************/	
	
	
	/*************************************** GASTOS IMPREVISTOS ************************************/
	
	private ImportacionGastoImprevistoDTO nvoGastoImprevisto = new ImportacionGastoImprevistoDTO();
	private List<ImportacionGastoImprevistoDTO> selectedGastosImprevistos = new ArrayList<ImportacionGastoImprevistoDTO>();
	private String gastosImprevistosEliminar;	

	@Command
	@NotifyChange("*")
	public void insertarGastoImprevisto(){
		
		if (this.operacionValidaFactura(AGREGAR_GASTO_IMPREVISTO) == false) {
			return;
		}
		
		try {
			this.nvoGastoImprevisto = new ImportacionGastoImprevistoDTO();
			
			WindowPopup w = new WindowPopup();
			w.setDato(this);
			w.setCheckAC(new ValidadorAgregarGastoImprevisto(this));
			w.setModo(WindowPopup.NUEVO);
			w.setTitulo("Agregar Gasto Imprevisto");
			w.setWidth("400px");
			w.setHigth("260px");
			w.show(Configuracion.IMPORTACION_GASTOS_IMPREVISTOS_ZUL);
			if (w.isClickAceptar()) {
				this.dto.getGastosImprevistos().add(nvoGastoImprevisto);
				this.dto.getResumenGastosDespacho().setTotalGastosImprevistosDs(this.getTotalImporteGastoImprevisto()[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@Command
	@NotifyChange("*")
	public void eliminarItemGastoImprevisto(){
		
		if (this.operacionValidaFactura(ELIMINAR_GASTO_IMPREVISTO) == false) {
			return;
		}
		
		if (this.confirmarEliminarGastoImprevisto() == true) {
			for (ImportacionGastoImprevistoDTO d : this.selectedGastosImprevistos) {
				this.dto.getGastosImprevistos().remove(d);
				this.dto.getResumenGastosDespacho().setTotalGastosImprevistosDs(this.getTotalImporteGastoImprevisto()[1]);
			}
			this.selectedGastosImprevistos = new ArrayList<ImportacionGastoImprevistoDTO>();
		}		
	}
	
	private boolean confirmarEliminarGastoImprevisto(){
		this.ordenItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (ImportacionGastoImprevistoDTO d : this.selectedGastosImprevistos) {
			this.gastosImprevistosEliminar = this.gastosImprevistosEliminar + "\n - " + d.getProveedor().getPos2();
		}		
		return this.mensajeSiNo(this.ordenItemsEliminar);
	}
	
	public boolean getCheckmarkGastoImprevisto(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getGastosImprevistos().size() > 0) {
			out = true;
		}
		return out;
	}
	
	public Double[] getTotalImporteGastoImprevisto(){
		double totalGs = 0; double totalDs = 0;
		for (ImportacionGastoImprevistoDTO gasto : this.dto.getGastosImprevistos()) {
			totalGs = totalGs + gasto.getImporteGs();
			totalDs = totalDs + gasto.getImporteDs();
		}
		Double[] out = {totalGs, totalDs};
		return out;
	}
	
	/***********************************************************************************************/	
	

	/**
	 *  COSTO FINAL 
	 */
	
	public List<MyArray> getItemsCostoFinal_() {
		List<MyArray> out = new ArrayList<MyArray>();	
		double despachoTipoCambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		double despachoFleteSeguro = this.dto.getResumenGastosDespacho().getValorFleteGs();
		double despachoGastos = this.dto.getResumenGastosDespacho().getTotalGastosGs();
		double totalCant = 0;
		double totalCostoDs = 0;
		double totalCostoGs = 0;
		double totalPorcentaje = 0;
		double totalFleteSeguro = 0;
		double totalCIF = 0;
		double totalGastos = 0;
		double totalCostoFinal = 0;
		
		for (ImportacionFacturaDTO f : this.dto.getImportacionFactura()) {
			
			double coefAutomatico = this.getCoeficienteGasto();
			double coefManual = this.dto.getResumenGastosDespacho().getCoeficienteAsignado();
			double coefGasto = 0;
			
			if (coefManual > 0) {
				coefGasto = coefManual;
			} else {
				coefGasto = coefAutomatico;
			}			
			
			for (ImportacionFacturaDetalleDTO d : f.getDetalles()) {				
				
				double costoDs = d.getCostoDs();
				double costoGs = costoDs * despachoTipoCambio;
				
				long idArt = d.getArticulo().getId();
				int cant = d.getCantidad();				
				double valorGasto = coefGasto * costoGs;
				
				double costoFinalGs = this.getCostoFinal(costoGs, valorGasto, 0);
				
				if (d.isGastoDescuento() == false) {
					MyArray mr = new MyArray();
					mr.setId(idArt);
					mr.setPos1(d.getArticulo().getPos1());
					mr.setPos2(d.getArticulo().getPos4());
					mr.setPos3(new Integer(cant));
					mr.setPos4(costoDs);
					mr.setPos5(costoGs);
					mr.setPos6(costoDs * cant);
					mr.setPos7(costoGs * cant);
					mr.setPos8(0.0);						
					mr.setPos9(0.0);
					mr.setPos10(0.0);
					mr.setPos11(0.0);
					mr.setPos12(0.0);
					mr.setPos13(0.0);
					mr.setPos14(costoFinalGs);
					out.add(mr);
					totalCant += cant;
					totalCostoDs += (costoDs * cant);
					totalCostoGs += (costoGs * cant);
				}			
			}
		}	
		for (MyArray mr : out) {
			int cantidad = (int) mr.getPos3();
			double importeGs = (double) mr.getPos7();
			double porcentaje = (importeGs * 100) / totalCostoGs;
			double fleteSeguro = (despachoFleteSeguro * porcentaje) / 100;
			double costoCIF = importeGs + fleteSeguro;
			double gastos = (despachoGastos * porcentaje) / 100;
			double costoFinal = costoCIF + gastos;
			double costoFinalUnd = costoFinal / cantidad;
			mr.setPos8(porcentaje);
			mr.setPos9(fleteSeguro);
			mr.setPos10(costoCIF);
			mr.setPos11(gastos);
			mr.setPos12(costoFinal);
			mr.setPos13(costoFinalUnd);
			totalPorcentaje += porcentaje;
			totalFleteSeguro += fleteSeguro;
			totalGastos += gastos;
			totalCIF += costoCIF;
			totalCostoFinal += costoFinal;
		}
		this.totalesCostoFinal = new MyArray(totalCant, totalCostoDs, totalCostoGs, totalPorcentaje, totalFleteSeguro, totalCIF, totalGastos, totalCostoFinal);
		BindUtils.postNotifyChange(null, null, this, "totalesCostoFinal");
		return out;
	}
	
	/**
	 * PRECIO FINAL
	 */	
	public List<MyArray> getItemsPrecioFinal() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		if (this.dto.getResumenGastosDespacho().getTipoCambio() <= 100	) {
			return out;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		double despachoTipoCambio = this.dto.getResumenGastosDespacho().getTipoCambio();
		double despachoFleteSeguro = this.dto.getResumenGastosDespacho().getValorFleteGs();
		double despachoGastos = this.dto.getResumenGastosDespacho().getTotalGastosGs();
		double totalCostoGs = 0;
		
		for (ImportacionFacturaDTO f : this.dto.getImportacionFactura()) {
			
			double coefAutomatico = this.getCoeficienteGasto();
			double coefManual = this.dto.getResumenGastosDespacho().getCoeficienteAsignado();
			double coefGasto = 0;
			
			if (coefManual > 0) {
				coefGasto = coefManual;
			} else {
				coefGasto = coefAutomatico;
			}			
			
			for (ImportacionFacturaDetalleDTO d : f.getDetalles()) {				
				
				double costoDs = d.getCostoDs();
				double costoGs = costoDs * despachoTipoCambio;
				
				long idArt = d.getArticulo().getId();
				int cant = d.getCantidad();				
				double valorGasto = coefGasto * costoGs;
				
				double costoFinalGs = this.getCostoFinal(costoGs, valorGasto, 0);
				
				if (d.isGastoDescuento() == false) {
					Object[] art = rr.getArticulo(idArt);
					MyArray mr = new MyArray();
					mr.setId(idArt);
					mr.setPos1(d.getArticulo().getPos1());
					mr.setPos2(d.getArticulo().getPos4());
					mr.setPos3(new Integer(cant));
					mr.setPos4(costoGs * cant);
					mr.setPos5(0.0);						
					mr.setPos6(0.0);
					mr.setPos7(art[3]); //costoActualGs
					mr.setPos8(art[5]); //precioActualGs
					mr.setPos9(0.0);
					mr.setPos10(0.0);
					mr.setPos11(0.0);
					mr.setPos12(0.0);
					mr.setPos14(costoFinalGs);
					out.add(mr);
					totalCostoGs += (costoGs * cant);
				}			
			}
		}	
		for (MyArray mr : out) {
			int cantidad = (int) mr.getPos3();
			double importeGs = (double) mr.getPos4();
			double porcentaje = (importeGs * 100) / totalCostoGs;
			double fleteSeguro = (despachoFleteSeguro * porcentaje) / 100;
			double costoCIF = importeGs + fleteSeguro;
			double gastos = (despachoGastos * porcentaje) / 100;
			double costoFinal = costoCIF + gastos;
			double costoFinalUnd = costoFinal / cantidad;
			double costoFinalUndDs = costoFinalUnd / despachoTipoCambio;
			double costoFinalUndIvaInc = costoFinalUnd * 1.1;
			double precioMayorista = costoFinalUndIvaInc * 1.25;
			double margenGs = precioMayorista - costoFinalUndIvaInc;
			mr.setPos5(costoFinalUnd);
			mr.setPos6(costoFinalUndDs);
			mr.setPos9(precioMayorista);
			mr.setPos10(costoFinalUndIvaInc);
			mr.setPos11(margenGs);
			mr.setPos12(Utiles.obtenerPorcentajeDelValor(margenGs, costoFinalUndIvaInc));
		}
		return out;
	}
	
	/**
	 * test group..
	 */
	@Wire
	private Listbox listbox;
	private void renderizarDesgloseCuentas() {
		try {
			Map<String, List<Object[]>> data = new HashMap<String, List<Object[]>>();
			Map<String, Double> totales = new HashMap<String, Double>();
			Map<String, Double> totalesDs = new HashMap<String, Double>();
			for (Object[] det : this.dto.getGastosDetallado()) {
				String key = (String) det[2];
				List<Object[]> items = data.get(key);
				Double importe = totales.get(key);
				Double importeDs = totalesDs.get(key);
				if (items == null) {
					items = new ArrayList<>();
					importe = (Double) det[3];
					importeDs = (Double) det[4];
				} else {
					importe += (double) det[3];
					importeDs += (double) det[4];
				}
				items.add(det);
				data.put(key, items);
				totales.put(key, importe);
				totalesDs.put(key, importeDs);
			}
			for (String key : data.keySet()) {
				List<Object[]> items = data.get(key);
				double importe = totales.get(key);
				double importeDs = totalesDs.get(key);
				Listgroup listgroup = new Listgroup(key + " - Gs. " + Utiles.getNumberFormat(importe) + " - U$D. " + Utiles.getNumberFormatDs(importeDs));
				listgroup.setOpen(false);
				listgroup.setStyle("font-weight:bold");
				listgroup.setParent(listbox);
				for (Object[] det : items) {
					Listitem listitem = new Listitem();
					for (int j = 1; j < 5; j++) {
						if(j == 3) det[j] = Utiles.getNumberFormat((double) det[j]);
						if(j == 4) det[j] = Utiles.getNumberFormatDs((double) det[j]);
						Listcell listcell = new Listcell(det[j].toString());
						listcell.setParent(listitem);
					}
					listitem.setParent(listbox);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * agrupa las cantidades de items repetidos..
	 */
	private void agruparCantidades() {
		Map<String, ImportacionFacturaDetalleDTO> map = new HashMap<String, ImportacionFacturaDetalleDTO>();
		Map<String, Integer> cants = new HashMap<String, Integer>();
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			for (ImportacionFacturaDetalleDTO det : fac.getDetalles()) {	
				det.setDuplicado(false);
				ImportacionFacturaDetalleDTO item = map.get(det.getArticulo().getPos1());
				if ((!det.getAuxi().equals("DUPLICADO")) && item != null) {
					det.setAuxi("DUPLICADO");
				} else if (!det.getAuxi().equals("DUPLICADO")) {
					map.put((String) det.getArticulo().getPos1(), det);
				}
				
				Integer acum = cants.get(det.getArticulo().getPos1());
				if (acum != null) {
					acum += det.getCantidad();
				} else {
					acum = det.getCantidad();					
				}
				cants.put((String) det.getArticulo().getPos1(), acum);
			}
		}
		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			for (ImportacionFacturaDetalleDTO det : fac.getDetalles()) {
				det.setCantidad_acum(cants.get(det.getArticulo().getPos1()));
			}
		}
	}
	
	/**
	 * after save..
	 */
	public void afterSave() {
		this.agruparCantidades();
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	public List<MyArray> getItemsCostoFinal(){
		List<MyArray> out = new ArrayList<MyArray>();	
		
		for (ImportacionFacturaDTO f : this.dto.getImportacionFactura()) {
			
			double coefAutomatico = this.getCoeficienteGasto();
			double coefManual = this.dto.getResumenGastosDespacho().getCoeficienteAsignado();
			double coefGasto = 0;
			
			if (coefManual > 0) {
				coefGasto = coefManual;
			} else {
				coefGasto = coefAutomatico;
			}			
			
			for (ImportacionFacturaDetalleDTO d : f.getDetalles()) {				
				
				double costoGs = d.getCostoGs();
				double costoDs = d.getCostoDs();
				
				long idArt = d.getArticulo().getId();
				int cant = d.getCantidad();				
				double valorGasto = coefGasto * costoDs;
				
				double costoFinalDs = this.getCostoFinal(costoDs, valorGasto, 0);
				double costoFinal = (costoFinalDs * this.dto.getResumenGastosDespacho().getTipoCambio());
				
				if (d.isGastoDescuento() == false) {
					MyArray mr = new MyArray();
					mr.setId(idArt);
					mr.setPos1(d.getArticulo().getPos1());
					mr.setPos2(d.getArticulo().getPos4());
					mr.setPos3(costoFinal);
					mr.setPos4(costoFinalDs);
					mr.setPos8(costoFinal * cant);
					mr.setPos9(costoFinalDs * cant);						
					mr.setPos5(new Integer(cant));
					mr.setPos6(costoGs);
					mr.setPos7(costoDs);
					out.add(mr);
				}			
			}			
		}	
		
		return out;
	}
	
	/**
	 * Calcula el costo final en base al costo + valorGasto + descuento
	 */
	private double getCostoFinal(double costoGravado, double valorGasto, double valorDescuento){
		return costoGravado + valorGasto - valorDescuento;
	}
	
	public String getItemsCostoFinalSize(){
		return this.getItemsCostoFinal().size() + " ítems";
	}
	
	public Object[] getTotalCostoFinal() {
		double totalGs = 0;
		double totalDs = 0;

		for (MyArray mr : this.getItemsCostoFinal()) {
			double importeGs = (double) mr.getPos8();
			double importeDs = (double) mr.getPos9();
			totalGs += importeGs;
			totalDs += importeDs;
		}

		return new Object[] { totalGs, totalDs };
	}
	
	/**
	 * @return el coeficiente utilizado para el costo final..
	 */
	public double getCoeficienteUtilizado() {
		
		if (this.dto.getResumenGastosDespacho().esNuevo() == true) {
			return 0;
		}
		
		double coefAutomatico = this.getCoeficienteGasto();
		double coefManual = this.dto.getResumenGastosDespacho().getCoeficienteAsignado();
		double coefGasto = 0;
		
		if (coefManual > 0) {
			coefGasto = coefManual;
		} else {
			coefGasto = coefAutomatico;
		}
		
		return coefGasto;
	}
	
	/**
	 * @return la restriccion de fecha..
	 */
	public String getConstraintFecha() {
	    return "no future, after " + new SimpleDateFormat("yyyyMMdd").format(this.getFechaCierre());
	}
	
	/***********************************************************************************************/	
	
	
	/************************************ VOLCADO DE COSTOS Y STOCK ********************************/
	
	private String linkReporteFinal;
	
	@Command @NotifyChange("*")
	public void confirmarImportacion() throws Exception {		
		if (mensajeSiNo("Esta seguro de confirmar la Importación Nro.: " + this.dto.getNumeroPedidoCompra())) {
			if (!this.validarFormulario()) {
				this.mensajeError(this.mensajeErrorVerificar);
				return;
			}
			if (this.dto.getResumenGastosDespacho().getTipoCambio() <= 100) {
				this.mensajeError("Falta el tipo de cambio en el resumen de despacho..");
				return;
			}
			try {
				this.generarDiferenciaTipoCambio();
				this.volcarImportacion();
				this.actualizarEtapaActual(this.getDtoUtil().getImportacionEstadoCerrado());
				this.dto.setAuxi("");
				this.dto.setReadonly();
				for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
					fac.setReadonly();
					fac.setTotalAsignadoDs(fac.getTotalImporteDs());
					fac.setTotalAsignadoGs(fac.getTotalImporteGs());
				}
				this.dto.setFechaCierre(new Date());
				this.dto.setImportacionConfirmada(true);
				this.dto.setPedidoConfirmado(true);
				this.dto.getImportacionFactura().get(0).setFechaOriginal(this.dto.getResumenGastosDespacho().getFechaDespacho());
				this.setDto((ImportacionPedidoCompraDTO) this.saveDTO(this.dto));
				this.addEventoAgendaLink("Se confirmó la Importación..", linkReporteFinal); 
				this.mensajePopupTemporal("La Importación fue correctamente confirmada..");	
				this.setEstadoABMConsulta();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}		
	}	
	
	//MyArray item | pos1:idMovimiento - pos2:tipoMovimiento - pos3:cantidad - pos4:articulo
	//pos5:costoFOBgs - pos6:costoFOBds - pos7:costoFinalGs - pos8:costoFinalDs |
	private void volcarImportacion() throws Exception {
		ControlLogica ctr = new ControlLogica(null);
		RegisterDomain rr = RegisterDomain.getInstance();
		
		for (MyArray m : this.getItemsCostoFinal()) {
			
			MyArray art = new MyArray();
			art.setId(m.getId());
			art.setPos1(m.getPos1());
			
			Integer cant = (Integer) m.getPos5();
			double costoFinalGs = (double) m.getPos3();
			
			ctr.actualizarArticuloDepositoCompra(this.dto.getId(), this.dto.getTipoMovimiento(), 
					art, cant.longValue(), costoFinalGs, 
						true, this.dto.getDeposito(), Configuracion.OP_SUMA);
			
			ControlArticuloCosto.addMovimientoCosto(art.getId(), costoFinalGs,
							this.dto.getFechaCierre(), this.dto.getId(),
							this.dto.getTipoMovimiento().getId(),
							this.getLoginNombre());
		}	
		
		for (ImportacionFacturaDetalleDTO item : this.dto.getImportacionFactura().get(0).getDetalles()) {
			long idArticulo = item.getArticulo().getId();
			double mayoristaGs = item.getPrecioFinalGs();
			double minoristaGs = item.getMinoristaGs();
			double listaGs = item.getListaGs();
			Articulo art = rr.getArticuloById(idArticulo);
			art.setFechaUltimaCompra(new Date());
			art.setCantUltimaCompra(item.getCantidad());
			rr.saveObject(art, this.getLoginNombre());
			ControlArticuloCosto.actualizarPrecio(idArticulo, mayoristaGs, minoristaGs, listaGs, this.getLoginNombre());
		}
		
		this.actualizarCtaCte();
	}	
	
	/**
	 * actualizar Cta Cte..
	 */
	private void actualizarCtaCte() throws Exception {
		ControlCtaCteEmpresa ctr = new ControlCtaCteEmpresa(null);

		IiD empresa = this.dto.getProveedor().getEmpresa();
		MyPair moneda = new MyPair(this.dto.getMoneda().getId(), "");
		MyArray sucursal = new MyArray();
		sucursal.setId(this.getSucursal().getId());

		for (ImportacionFacturaDTO fac : this.dto.getImportacionFactura()) {
			long idMovimientoOriginal = fac.getId();
			String nroComprobante = fac.getNumero();
			Date fechaEmision = fac.getFechaOriginal();
			double importeOriginal = fac.getTotalImporteDs();
			MyArray tipoMovimiento = fac.getTipoMovimiento();

			ctr.addCtaCteEmpresaMovimientoFacturaCredito(empresa,
					idMovimientoOriginal, nroComprobante, fechaEmision,
					(int) fac.getCondicionPago().getPos2(), 1,
					importeOriginal, 0, (importeOriginal - this.dto.getTotalAnticipoAplicadoDs()), moneda,
					tipoMovimiento, this.getDtoUtil().getCtaCteEmpresaCaracterMovProveedor(), sucursal, "", 
					this.dto.getResumenGastosDespacho().getTipoCambio(), "");			
		}
		
		for (ImportacionAplicacionAnticipoDTO anticipo : this.dto.getAplicacionAnticipos()) {
			CtaCteEmpresaMovimientoDTO movim = anticipo.getMovimiento();
			movim.setSaldo(movim.getSaldo() + anticipo.getImporteDs());
			this.saveDTO(movim, new AssemblerCtaCteEmpresaMovimiento());
		}
	}
	
	@DependsOn({ "deshabilitado", "dto.importacionFactura", "dto.deposito" })
	public boolean isCerrarImportacionDisabled() {
		return this.isDeshabilitado() || this.dto.getImportacionFactura().size() == 0
				|| this.dto.getDeposito() == null;
	}
	
	/**
	 * genera el registro de diferencia tipo - cambio..
	 */
	private void generarDiferenciaTipoCambio() throws Exception {
		double dif_tc = this.getDiferenciaTipoCambio();
		if (dif_tc >= 1000 || dif_tc <= -1000) {
			RegisterDomain rr = RegisterDomain.getInstance();
			Recibo rec = new Recibo();
			rec.setCobrador("- - -");
			rec.setProveedor(rr.getProveedorById(this.dto.getProveedor().getId()));
			rec.setFechaEmision(new Date());
			rec.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
			rec.setNumero("REC-PAG-" + AutoNumeroControl.getAutoNumero("REC-PAG", 5));
			rec.setNombreUsuarioCarga(this.getLoginNombre());
			rec.setEstadoComprobante(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CONFECCIONADO));
			rec.setMotivoAnulacion("");
			rec.setNumeroPlanilla("- - -");
			rec.setNumeroRecibo("DIF. TIPO CAMBIO");
			rec.setFechaRecibo(new Date());
			rec.setEntregado(true);
			rec.setSucursal(rr.getSucursalAppById(2));
			rec.setTipoCambio(0);
			rec.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_RECIBO_PAGO));
			ReciboDetalle det = new ReciboDetalle();
			det.setAuxi(ReciboDetalle.TIPO_DIF_CAMBIO);
			det.setConcepto("DIFERENCIA POR TIPO DE CAMBIO");
			det.setMontoGs(dif_tc);
			rec.getDetalles().add(det);
			rr.saveObject(rec, this.getLoginNombre());
		}
	}
	
	//Genera el Reporte del Resultado Final de la Importacion..
	public void generarReporteFinal() {
			
		List<Object[]> data = new ArrayList<>();
		Object[] obj = new Object[6];		
		for (MyArray d : this.getItemsCostoFinal()) {
			obj = new Object[6];
			obj[0] = d.getPos1();
			obj[1] = d.getPos1();
			obj[2] = d.getPos2();
			obj[3] = d.getPos3();
			obj[4] = 0; //d.getCostoFinalDs();
			obj[5] = 0; //d.getCostoFinalGs();
			data.add(obj);	
		}
	}
	
	/***********************************************************************************************/	
	
	
	/***************************************** COMPROBAR GRUPO *************************************/	

	private boolean comprobarGrupo() {
		boolean out = false;
		
		if ((this.esGrupo(Configuracion.GRUPO_VENTAS)) && 
				(this.dto.getPropietario().compareTo(Configuracion.PROPIETARIO_VENTAS_DESCRIPCION) == 0)) {
			out = true;
		}
		
		if ((this.esGrupo(Configuracion.GRUPO_IMPORTACION)) && 
				(this.dto.getPropietario().compareTo(Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION) == 0)) {
			out = true;
		}
		
		if (this.esGrupo(Configuracion.GRUPO_GRUPO_ELITE) == true) {
			out = true;
		}
		
		return out;
	}
	
	/***********************************************************************************************/	
	
	
	/*************************************** VALIDACION FORMULARIO *********************************/
	
	@Override
	public boolean verificarAlGrabar() {
		try {
			String sigla = this.dto.getProveedor().getEmpresa().getSigla().trim();
			
			if (sigla.length() == 0) {
				sigla = Configuracion.NRO_IMPORTACION_PEDIDO;
			}
			
			if ((this.dto.esNuevo() == true) && (this.validarFormulario() == true)) {
				this.dto.setNumeroPedidoCompra(sigla + "-" + AutoNumeroControl.getAutoNumero(sigla, 5));
				
				this.grabarEventosAgenda();
			}
			
			if ((this.dto.esNuevo() == false) && (this.validarFormulario() == true)) {
				// Aqui graba en la BD los eventos de la agenda que se fueron cargando
				this.grabarEventosAgenda();
			}
			
			if (this.validarFormulario() == true) {				
				this.actualizarValoresDespacho();
				this.actualizarImporte();
			}
			
			return this.validarFormulario();
			
		} catch (Exception e) {
			mensajeError(e.getMessage());
		}		
		return false;
	}

	private String mensajeErrorVerificar = "";
	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeErrorVerificar;
	}
	
	public boolean validarFormulario() throws Exception {
		boolean out = true;		
		this.mensajeErrorVerificar = "No se puede realizar la operación debido a: \n";
			
			if (this.dto.getProveedor().esNuevo() == true) {
				this.mensajeErrorVerificar += "\n - Debe seleccionar un Proveedor..";
				out = false;
			}
			
			if (this.dto.getCambio() <= 0.001) {
				this.mensajeErrorVerificar += "\n - El Tipo de Cambio debe ser mayor a cero..";
				out = false;
			}
			
			if (this.dto.getProveedorCondicionPago().esNuevo() == true) {
				this.mensajeErrorVerificar += "\n - Debe seleccionar una Condición de Pago..";
				out = false;
			}
			
			if (this.dto.getTipo().esNuevo() == true) {
				this.mensajeErrorVerificar += "\n - Debe asignar un Tipo de Importación..";
				out = false;
			}	
			
			if (!this.dto.esNuevo()) {
				RegisterDomain rr = RegisterDomain.getInstance();
				Object[] imp = rr.getImportacion(this.dto.getId());
				Character estado = (Character) imp[1];
				if (estado.equals('R')) {
					out = false;
					this.mensajeErrorVerificar += "\n - La importación ya fue cerrada por otro usuario..";
				}
			}
		
		return out;
	}
	
	/***********************************************************************************************/	
	
	
	/********************************** VER COMPARATIVO COSTOS *************************************/
	
	private boolean comparativo = false;
	private boolean comparativoPrr = false;	

	@Command
	@NotifyChange("comparativo")
	public void verComparativo(){
		this.setComparativo(!this.comparativo);
	}
	
	@Command
	@NotifyChange("comparativoPrr")
	public void verComparativoProrrateo(){
		this.setComparativoPrr(!this.comparativoPrr);
	}
	
	/***********************************************************************************************/
	
	
	/********************************** SUGERIR VALORES POR DEFECTO ********************************/
	
	@Command
	@NotifyChange("*")
	public void sugerirValoresProveedor() throws Exception {		
		this.dto.setProveedorCondicionPago(this.dto.getProveedor().getCondicionPago());			
		this.dto.setMoneda(this.dto.getProveedor().getMonedaConSimbolo());				
	}	
	
	/***********************************************************************************************/	
	
	
	/*************************************** VALIDACION FACTURA ************************************/
	
	private static int UPLOAD_FACTURA = 8;
	private static int AGREGAR_FACTURA = 9;
	private static int SUPRIMIR_FACTURA = 10;
	private static int ELIMINAR_ITEM_FACTURA = 11;
	private static int INSERTAR_ITEM_FACTURA = 12;
	private static int IMPORTAR_ITEMS = 13;
	private static int ADD_GASTOS_DESCUENTOS = 14;
	private static int AGREGAR_GASTO_IMPREVISTO = 15;
	private static int ELIMINAR_GASTO_IMPREVISTO = 16;
	private static int CONFIRMAR_RECEPCION = 17;
	
	private boolean operacionValidaFactura(int operacion) {
		boolean out = true;
		String mensaje = "No se puede realizar la operación debido a: \n";
		
			if (operacion == UPLOAD_FACTURA || operacion == SUPRIMIR_FACTURA
					|| operacion == ADD_GASTOS_DESCUENTOS || operacion == INSERTAR_ITEM_FACTURA
						|| operacion == IMPORTAR_ITEMS) {
				if (this.selectedImportacionFactura.getNumero().length() == 0) {
					out = false;
					mensaje = "Debe agregar o seleccionar una Factura para realizar esta operación";
				}
			}
			
			if (operacion == AGREGAR_FACTURA || operacion == AGREGAR_GASTO_IMPREVISTO) {
				if (this.dto.isPedidoConfirmado() == false) {
					out = false;
					mensaje = "La Orden de Compra aun no fue Confirmada..";
				}
			}
			
			if (operacion == ELIMINAR_ITEM_FACTURA) {
				if (this.selectedFacturaItems.size() == 0) {
					out = false;
					mensaje = "Debe seleccionar al menos un ítem..";
				}
			}
			
			if (operacion == ELIMINAR_GASTO_IMPREVISTO) {
				if (this.selectedGastosImprevistos.size() == 0) {
					mensaje = "Debe seleccionar al menos un ítem..";
					out = false;
				}
			}
			
			if (operacion == CONFIRMAR_RECEPCION) {
				
				if (this.dto.getImportacionFactura().size() == 0) {
					mensaje = "Esta Importación aún no tiene Facturas cargadas..";
					out = false;
				}
				
				if (this.selectedRecepcion.esNuevo() == true) {
					mensaje = "La Factura seleccionada aun no fue guardada..";
					out = false;
				}
			}
			
			if (out == false) {
				mensajeError(mensaje);
			}		
		return out;
	}	
	
	/***********************************************************************************************/	
	
	@Command
	@NotifyChange("*")
	public void aplicarAnticipo(@BindingParam("comp") Popup comp) {
		if(this.selectedAnticipo == null) return;
		this.dto.getAplicacionAnticipos().add(this.selectedAnticipo);
		this.selectedAnticipo = null;
		comp.close();
	}
	
	@Command
	public void guaranizarGastoDetalle() {
		double tc = this.nvoGasto.getTipoCambio();
		double ds = this.nvoGastoDetalle.getMontoDs();
		this.nvoGastoDetalle.setMontoGs(ds * tc);
		BindUtils.postNotifyChange(null, null, this.nvoGastoDetalle, "montoGs");
	}
	
	@Command
	public void dolarizarGastoDetalle() {
		double tc = this.nvoGasto.getTipoCambio();
		double gs = this.nvoGastoDetalle.getMontoGs();
		this.nvoGastoDetalle.setMontoDs(gs / tc);
		BindUtils.postNotifyChange(null, null, this.nvoGastoDetalle, "montoDs");
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn("filterNumero")
	public List<ImportacionPedidoCompra> getImportaciones() throws Exception {
		if (this.filterNumero.isEmpty()) {
			return new ArrayList<ImportacionPedidoCompra>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getImportaciones_(this.filterNumero);
	}
	
	@DependsOn({ "dto.proveedor", "dto.moneda" })
	public List<ImportacionAplicacionAnticipoDTO> getAnticiposPendientes() throws Exception {
		List<ImportacionAplicacionAnticipoDTO> out = new ArrayList<ImportacionAplicacionAnticipoDTO>();
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		List<CtaCteEmpresaMovimiento> movims = rr.getPagosAnticipadosConSaldo(desde, new Date(),
				this.dto.getProveedor().getEmpresa().getId(), this.dto.getMoneda().getId());
		for (CtaCteEmpresaMovimiento movim : movims) {
			if (movim.getTipoMovimiento().getSigla().equals(Configuracion.SIGLA_TM_ANTICIPO_PAGO)) {
				CtaCteEmpresaMovimientoDTO mvdto = (CtaCteEmpresaMovimientoDTO) new AssemblerCtaCteEmpresaMovimiento().domainToDto(movim);
				ImportacionAplicacionAnticipoDTO ant = new ImportacionAplicacionAnticipoDTO();
				ant.setMovimiento(mvdto);
				ant.setImporteDs(mvdto.getSaldo() * -1);
				out.add(ant);
			}
		}
		return out;
	}
	
	@DependsOn({ "dto" })
	public double getDiferenciaTipoCambio() throws Exception {
		if(this.dto.getImportacionFactura().size() == 0 || this.dto.getResumenGastosDespacho().esNuevo()) return 0;
		double totalFacGs = this.dto.getImporteGsTcDespacho();
		double dif = 0;
		
		RegisterDomain rr = RegisterDomain.getInstance();		
		for (ImportacionAplicacionAnticipoDTO anticipo : this.dto.getAplicacionAnticipos()) {
			Recibo ant = rr.getOrdenPagoById(anticipo.getMovimiento().getIdMovimientoOriginal());
			dif += (ant.getTotalImporteGs() - totalFacGs);
		}		
		return dif;
	}
	
	@DependsOn({ "dto.proveedor" })
	public List<CtaCteEmpresaMovimiento> getSaldosPendientes() throws Exception {
		if(this.dto.getProveedor().esNuevo()) return new ArrayList<CtaCteEmpresaMovimiento>();
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getSaldosCtaCte(this.dto.getProveedor().getEmpresa().getId(), Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR);
	}
	
	@DependsOn({ "dto.proveedor" })
	public double getTotalSaldosPendientes() throws Exception {
		double out = 0;
		for (CtaCteEmpresaMovimiento movim : this.getSaldosPendientes()) {
			out += movim.getSaldo();
		}
		return out;
	}
	
	/**
	 * @return los formatos de reporte..
	 */
	public List<Object[]> getFormatos() {
		List<Object[]> out = new ArrayList<Object[]>();
		out.add(ReportesViewModel.FORMAT_PDF);
		out.add(ReportesViewModel.FORMAT_XLS);
		out.add(ReportesViewModel.FORMAT_CSV);
		return out;
	}
	
	/**
	 * @return las monedas..
	 */
	public List<Tipo> getMonedas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_MONEDA);
	}
	
	@SuppressWarnings("unchecked")
	public List<SucursalApp> getSucursales() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(SucursalApp.class.getName());
	}
	
	/**
	 * @return las condiciones..
	 */
	public List<CondicionPago> getCondicionesPago() throws Exception {
		List<CondicionPago> out = new ArrayList<CondicionPago>();
		RegisterDomain rr = RegisterDomain.getInstance();
		CondicionPago cont = rr.getCondicionPagoById(CondicionPago.ID_CONTADO);
		CondicionPago cred = rr.getCondicionPagoById(CondicionPago.ID_CREDITO_30);
		out.add(cont); out.add(cred);
		return out;
	}
	
	/**
	 * @return la ultima cotizacion..
	 */
	public double getTipoCambio() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipoCambioVenta();
	}
	
	/**
	 * @return las vias de importacion..
	 */
	public List<String> getVias() {
		List<String> out = new ArrayList<String>();
		out.add(ImportacionPedidoCompra.VIA_MARITIMA);
		out.add(ImportacionPedidoCompra.VIA_TERRESTRE);
		return out;
	}
	
	/**
	 * @return el link del archivo importado..
	 */
	public String getCotizacionCsv() {
		return Configuracion.pathPedidoCompraGenerico + this.dto.getNumeroPedidoCompra() + ".csv";
	}
	
	/**
	 * @return el link del archivo importado..
	 */
	public String getProformaCsv() {
		return Configuracion.pathPedidoCompraGenerico + "proforma_" + this.dto.getNumeroPedidoCompra() + ".csv";
	}
	
	/**
	 * @return el link del archivo importado..
	 */
	public String getFacturaCsv() {
		return Configuracion.pathPedidoCompraGenerico + "factura_" + this.dto.getNumeroPedidoCompra() + ".csv";
	}
	
	/**
	 * @return los tipos de movimiento gastos..
	 */
	public List<TipoMovimiento> getTiposMovimientoGastos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		TipoMovimiento t1 = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO);
		TipoMovimiento t2 = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO);
		List<TipoMovimiento> out = new ArrayList<TipoMovimiento>();
		out.add(t1); out.add(t2);
		return out;
	}
	
	@DependsOn("filterRazonSocial")
	public List<Proveedor> getProveedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedores(this.filterRazonSocial);
	}
	
	/**
	 * @return las cuentas de gasto importacion..
	 */
	public List<ArticuloGasto> getCuentasGastos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulosGastos("IMPORTACION", 100);
	}
	
	/**
	 * @return los tipos de iva..
	 */
	public List<Tipo> getTiposIva() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_IVA);
	}
	
	@DependsOn({ "nvoGasto.tipoMovimiento", "nvoGasto.fecha", "nvoGasto.condicionPago", "nvoGasto.numero",
			"nvoGasto.moneda", "nvoGasto.proveedor", "nvoGasto.tipoCambio", "nvoGasto.acreedor" })
	public boolean isDetalleVisible() {
		return this.nvoGasto.getTipoMovimiento() != null && this.nvoGasto.getFecha() != null
				&& this.nvoGasto.getCondicionPago() != null && this.nvoGasto.getNumero() != null
				&& this.nvoGasto.getMoneda() != null && this.nvoGasto.getProveedor() != null
				&& this.nvoGasto.getTipoCambio() > 0 && this.nvoGasto.getAcreedor() != null;
	}
	
	@DependsOn({ "nvoGasto.tipoMovimiento", "nvoGasto.fecha", "nvoGasto.condicionPago", "nvoGasto.numero",
		"nvoGasto.moneda", "nvoGasto.proveedor", "nvoGasto.tipoCambio", "nvoGasto.acreedor", "nvoGasto.detalles" })
	public boolean isGastoEnabled() {
		return this.nvoGasto.getTipoMovimiento() != null && this.nvoGasto.getFecha() != null
			&& this.nvoGasto.getCondicionPago() != null && this.nvoGasto.getNumero() != null
			&& this.nvoGasto.getMoneda() != null && this.nvoGasto.getProveedor() != null
			&& this.nvoGasto.getTipoCambio() > 0 && this.nvoGasto.getAcreedor() != null && this.nvoGasto.getDetalles().size() > 0;
}
	
	@DependsOn({ "nvoGastoDetalle.articuloGasto", "nvoGastoDetalle.montoGs", "nvoGastoDetalle.tipoIva" })
	public boolean isItemDisabled() {
		return this.nvoGastoDetalle.getArticuloGasto() == null || this.nvoGastoDetalle.getMontoGs() <= 0 || this.nvoGastoDetalle.getTipoIva() == null;
	}
	
	public ImportacionPedidoCompraDTO getDto() {
		return dto;
	}

	public void setDto(ImportacionPedidoCompraDTO dto) {		
		this.dto = dto;
	}
	
	public List<ImportacionPedidoCompraDetalleDTO> getSelectedOrdenItems() {
		return selectedOrdenItems;
	}

	public void setSelectedOrdenItems(
			List<ImportacionPedidoCompraDetalleDTO> selectedOrdenItems) {
		this.selectedOrdenItems = selectedOrdenItems;
	}

	public String getOrdenItemsEliminar() {
		return ordenItemsEliminar;
	}

	public void setOrdenItemsEliminar(String ordenItemsEliminar) {
		this.ordenItemsEliminar = ordenItemsEliminar;
	}
	
	public ImportacionPedidoCompraDetalleDTO getNewDetalle() {
		return newDetalle;
	}

	public void setNewDetalle(ImportacionPedidoCompraDetalleDTO newDetalle) {
		this.newDetalle = newDetalle;
	}
	
	public CuerpoCorreo getCorreo() {
		return correo;
	}

	public void setCorreo(CuerpoCorreo correo) {
		this.correo = correo;
	}

	public String getNombreArchivo() {
		return this.dto.getNumeroPedidoCompra();
	}
	
	public String getLinkImportacionPedidoCompra() {
		return linkImportacionPedidoCompra;
	}

	public void setLinkImportacionPedidoCompra(String linkImportacionPedidoCompra) {
		this.linkImportacionPedidoCompra = linkImportacionPedidoCompra;
	}	
	
	public String getPathRealImportacionPedidoCompra() {
		return pathRealImportacionPedidoCompra;
	}

	public void setPathRealImportacionPedidoCompra(
			String pathRealImportacionPedidoCompra) {
		this.pathRealImportacionPedidoCompra = pathRealImportacionPedidoCompra;
	}
	
	private String selectedPropietarioPedidoCompra = "";

	public String getSelectedPropietarioPedidoCompra() {
		return selectedPropietarioPedidoCompra;
	}

	public void setSelectedPropietarioPedidoCompra(
			String selectedPropietarioPedidoCompra) {
		this.selectedPropietarioPedidoCompra = selectedPropietarioPedidoCompra;
	}

	public List<String> getPropietariosPedidoCompra() {

		List<String> out = new ArrayList<String>();
		out.add(Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION);
		out.add(Configuracion.PROPIETARIO_VENTAS_DESCRIPCION);

		return out;
	}
	

	public MyPair getTipoSelected() {
		return tipoSelected;
	}

	public void setTipoSelected(MyPair tipoSelected) {
		this.tipoSelected = tipoSelected;
	}
	
	public boolean isProrrateado() {
		return prorrateado;
	}

	public void setProrrateado(boolean prorrateado) {
		this.prorrateado = prorrateado;
	}
	
	public List<ImportacionFacturaDetalleDTO> getSelectedFacturaItems() {
		return selectedFacturaItems;
	}

	public void setSelectedFacturaItems(
			List<ImportacionFacturaDetalleDTO> selectedFacturaItems) {
		this.selectedFacturaItems = selectedFacturaItems;
	}

	public String getFacturaItemsEliminar() {
		return facturaItemsEliminar;
	}

	public void setFacturaItemsEliminar(String facturaItemsEliminar) {
		this.facturaItemsEliminar = facturaItemsEliminar;
	}
	
	public ImportacionFacturaDetalleDTO getNvoItem() {
		return nvoItem;
	}

	public void setNvoItem(ImportacionFacturaDetalleDTO nvoItem) {
		this.nvoItem = nvoItem;
	}
	
	public ImportacionFacturaDTO getNvaFactura() {
		return nvaFactura;
	}

	public void setNvaFactura(ImportacionFacturaDTO nvaFactura) {
		this.nvaFactura = nvaFactura;
	}
	
	public List<ImportacionFacturaDetalleDTO> getItemsImportar() {
		return itemsImportar;
	}

	public void setItemsImportar(List<ImportacionFacturaDetalleDTO> itemsImportar) {
		this.itemsImportar = itemsImportar;
	}

	public List<ImportacionFacturaDetalleDTO> getSelectedItemsImportar() {
		return selectedItemsImportar;
	}

	public void setSelectedItemsImportar(
			List<ImportacionFacturaDetalleDTO> selectedItemsImportar) {
		this.selectedItemsImportar = selectedItemsImportar;
	}
	
	public String getSelectedPropietarioImportacionFactura() {
		return selectedPropietarioImportacionFactura;
	}

	public void setSelectedPropietarioImportacionFactura(
			String selectedPropietarioImportacionFactura) {
		this.selectedPropietarioImportacionFactura = selectedPropietarioImportacionFactura;
	}

	public String getLabelImportacionBorder() {		
		return getLabelBorder(Configuracion.PROPIETARIO_IMPORTACION_KEY);
	}	
	
	public String getLabelAdministracionBorder() {
		return getLabelBorder(Configuracion.PROPIETARIO_ADMINISTRACION_KEY);
	}
	
	public String getLabelVentasBorder(){
		return getLabelBorder(Configuracion.PROPIETARIO_VENTAS_KEY);
	}
	
	public String getLabelAuditoriaBorder(){
		return getLabelBorder(Configuracion.PROPIETARIO_AUDITORIA_KEY);
	}
	
	public String getLabelBorder(int propietarioKey){
		String out = "";
		if (this.selectedImportacionFactura.getPropietarioActual() == propietarioKey) {
			out = Misc.LABEL_BORDER;
		}
		return out;
	}
	
	public ImportacionFacturaDTO getSelectedRecepcion() {
		return selectedRecepcion;
	}

	public void setSelectedRecepcion(ImportacionFacturaDTO selectedRecepcion) {
		this.selectedRecepcion = selectedRecepcion;		
	}
	
	public ImportacionGastoImprevistoDTO getNvoGastoImprevisto() {
		return nvoGastoImprevisto;
	}

	public void setNvoGastoImprevisto(
			ImportacionGastoImprevistoDTO nvoGastoImprevisto) {
		this.nvoGastoImprevisto = nvoGastoImprevisto;
	}

	public List<ImportacionGastoImprevistoDTO> getSelectedGastosImprevistos() {
		return selectedGastosImprevistos;
	}

	public void setSelectedGastosImprevistos(
			List<ImportacionGastoImprevistoDTO> selectedGastosImprevistos) {
		this.selectedGastosImprevistos = selectedGastosImprevistos;
	}

	public String getGastosImprevistosEliminar() {
		return gastosImprevistosEliminar;
	}

	public void setGastosImprevistosEliminar(String gastosImprevistosEliminar) {
		this.gastosImprevistosEliminar = gastosImprevistosEliminar;
	}
	
	public boolean isComparativo() {
		return comparativo;
	}

	public void setComparativo(boolean comparativo) {
		this.comparativo = comparativo;
	}
	
	public boolean isComparativoPrr() {
		return comparativoPrr;
	}

	public void setComparativoPrr(boolean comparativoPrr) {
		this.comparativoPrr = comparativoPrr;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public ImportacionPedidoCompra getSelectedImportacion() {
		return selectedImportacion;
	}

	public void setSelectedImportacion(ImportacionPedidoCompra selectedImportacion) {
		this.selectedImportacion = selectedImportacion;
	}

	public ImportacionAplicacionAnticipoDTO getSelectedAnticipo() {
		return selectedAnticipo;
	}

	public void setSelectedAnticipo(ImportacionAplicacionAnticipoDTO selectedAnticipo) {
		this.selectedAnticipo = selectedAnticipo;
	}

	public MyArray getNvaTrazabilidad() {
		return nvaTrazabilidad;
	}

	public void setNvaTrazabilidad(MyArray nvaTrazabilidad) {
		this.nvaTrazabilidad = nvaTrazabilidad;
	}

	public MyArray getTotalesCostoFinal() {
		return totalesCostoFinal;
	}

	public void setTotalesCostoFinal(MyArray totalesCostoFinal) {
		this.totalesCostoFinal = totalesCostoFinal;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public Proveedor getSelectedProveedor() {
		return selectedProveedor;
	}

	public void setSelectedProveedor(Proveedor selectedProveedor) {
		this.selectedProveedor = selectedProveedor;
	}

	public Gasto getNvoGasto() {
		return nvoGasto;
	}

	public void setNvoGasto(Gasto nvoGasto) {
		this.nvoGasto = nvoGasto;
	}

	public GastoDetalle getNvoGastoDetalle() {
		return nvoGastoDetalle;
	}

	public void setNvoGastoDetalle(GastoDetalle nvoGastoDetalle) {
		this.nvoGastoDetalle = nvoGastoDetalle;
	}

	public ImportacionPedidoCompraDetalleDTO getNvoItemProforma() {
		return nvoItemProforma;
	}

	public void setNvoItemProforma(ImportacionPedidoCompraDetalleDTO nvoItemProforma) {
		this.nvoItemProforma = nvoItemProforma;
	}

	public Object[] getSelectedFormato() {
		return selectedFormato;
	}

	public void setSelectedFormato(Object[] selectedFormato) {
		this.selectedFormato = selectedFormato;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public CtaCteEmpresaMovimiento getNvoDesglose() {
		return nvoDesglose;
	}

	public void setNvoDesglose(CtaCteEmpresaMovimiento nvoDesglose) {
		this.nvoDesglose = nvoDesglose;
	}
	
	/***********************************************************************************************/
	
}



//Validador Insertar Item Orden de Compra..
class ValidadorInsertarItemOrdenCompra implements VerificaAceptarCancelar {
	
	private ImportacionPedidoCompraDetalleDTO newDetalle = new ImportacionPedidoCompraDetalleDTO();
	private String mensajeError = "";	

	public ValidadorInsertarItemOrdenCompra(ImportacionPedidoCompraControlBody ctr){
		this.newDetalle = ctr.getNewDetalle();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.newDetalle.getArticulo().esNuevo()) {
			this.mensajeError = this.mensajeError + "\n - " + Configuracion.TEXTO_ERROR_ITEM_NO_SELECCIONADO;
			out = false;
		}
		
		if (this.newDetalle.getCantidad() <= 0.001) {
			this.mensajeError = this.mensajeError + "\n - " + Configuracion.TEXTO_ERROR_ITEM_CANTIDAD;
			out = false;
		}
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {		
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error de validación al Cancelar..";
	}	
}


//Validar Envio de Correo
class ValidadorEnviarCorreo implements VerificaAceptarCancelar{
	
	private CuerpoCorreo correo;
	private Misc misc = new Misc();
	private String mensajeError = "";
	
	public ValidadorEnviarCorreo(ImportacionPedidoCompraControlBody ctr){
		this.correo = ctr.getCorreo();
	}

	@Override
	public boolean verificarAceptar() {		
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if ((correo.getDestinatario().trim().length() == 0) 
				&& (correo.getDestinatarioCopia().trim().length() == 0)
					&& (correo.getDestinatarioCopiaOculta().trim().length() == 0)) {
			this.mensajeError = this.mensajeError + "\n - Debe ingresar una dirección de correo..";
			out = false;
		}
		
		if (correo.getDestinatario().trim().length() == 0) {
			this.mensajeError = this.mensajeError + "\n - Debe ingresar el Destinatario..";
			out = false;
		}
		
		String send = correo.getDestinatario();
		String sendCC = correo.getDestinatarioCopia();
		String sendCCO = correo.getDestinatarioCopiaOculta();
		
		String[] arraySend = null;
		String[] arraySendCC = null;
		String[] arraySendCCO = null;
		
		if (send.trim().length() > 0) {
			arraySend = correo.getDestinatario().trim().split(";");
		}
		
		if (sendCC.trim().length() > 0) {
			arraySendCC = correo.getDestinatarioCopia().trim().split(";");
		}
		
		if (sendCCO.trim().length() > 0) {
			arraySendCCO = correo.getDestinatarioCopiaOculta().trim().split(";");
		}

		String[] correos = misc.concatenarArraysDeString(arraySend, arraySendCC, arraySendCCO, null, null);
		boolean correosCheck = true;
		String correosMsg = "";
		
		if (correos.length > 0) {
			correosCheck = (boolean) misc.chequearMultipleCorreos(correos)[0];
			correosMsg = (String) misc.chequearMultipleCorreos(correos)[1];
		}	
		
		if (correosCheck == false) {
			this.mensajeError = mensajeError + "Las sgtes direcciones de Correo son "
					+ "inválidas: \n" + correosMsg;
			out = false;
		}		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {		
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error de validación al Cancelar..";
	}	
}


//Validar Nueva Factura
class ValidadorNuevaFactura implements VerificaAceptarCancelar{
	
	private ImportacionFacturaDTO nvaFactura;
	private List<ImportacionFacturaDTO> facturas;
	private String mensajeError = "";
	
	public ValidadorNuevaFactura(ImportacionPedidoCompraControlBody ctr){
		this.nvaFactura = ctr.getNvaFactura();
		this.facturas = ctr.getDto().getImportacionFactura();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.nvaFactura.getNumero().length() == 0) {
			this.mensajeError = this.mensajeError + "\n - El número no puede quedar vacío..";
			out = false;
		}			
		if (this.nvaFactura.getFechaOriginal() == null) {
			this.mensajeError = this.mensajeError + "\n - La fecha no puede quedar vacía..";
			out = false;
		}
		if (this.nvaFactura.getTotalAsignadoDs() <= 0.001) {
			this.mensajeError = this.mensajeError + "\n - El Total asignado debe ser mayor a cero..";
			out = false;
		}
		for (ImportacionFacturaDTO f: this.facturas) {
			if (f.getNumero().compareTo(this.nvaFactura.getNumero()) == 0) {
				this.mensajeError = this.mensajeError + "\n - Número de Factura duplicada: "+this.nvaFactura.getNumero();
				out = false;
			}
		}
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error de validación al Cancelar..";
	}
}


//Validar Insertar Item Factura
class ValidadorInsertarItemFactura implements VerificaAceptarCancelar{
	
	private ImportacionFacturaDetalleDTO nvoItem;
	private String mensajeError = "";
	
	public ValidadorInsertarItemFactura(ImportacionPedidoCompraControlBody ctr){
		this.nvoItem = ctr.getNvoItem();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;		
		this.mensajeError = "No se puede realizar la operación debido a: \n";

		if (this.nvoItem.getArticulo().esNuevo() == true) {
			this.mensajeError = this.mensajeError + "\n - Debe seleccionar un artículo..";
			out = false;
		}
		if (this.nvoItem.getCantidad() <= 0) {
			this.mensajeError = this.mensajeError + "\n - La cantidad debe ser mayor a cero..";
			out = false;
		}
		if (this.nvoItem.getCostoDs() <= 0.001) {
			this.mensajeError = this.mensajeError + "\n - El costo debe ser mayor a cero..";
			out = false;
		}	
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error de validación al Cancelar..";
	}
}


//Validar Importar Items
class ValidadorImportarItems implements VerificaAceptarCancelar{

	private List<ImportacionFacturaDetalleDTO> selectedItemsImportar;
	private String mensajeError = "";
	
	public ValidadorImportarItems(ImportacionPedidoCompraControlBody ctr){
		this.selectedItemsImportar = ctr.getSelectedItemsImportar();
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;		
		this.mensajeError = "No se puede realizar la operación debido a: \n";

		if (this.selectedItemsImportar.size() == 0) {
			//this.mensajeError = this.mensajeError + "\n - Debe seleccionar al menos un ítem..";
			//out = false;
		}	
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error de validación al Cancelar..";
	}
}


//Validar Insertar Gastos-Descuentos
class ValidadorInsertarGastosDescuentos implements VerificaAceptarCancelar {

	private ImportacionFacturaDetalleDTO nvoItem;
	private ImportacionPedidoCompraControlBody ctr;
	private String mensajeError = "";
	
	public ValidadorInsertarGastosDescuentos(ImportacionPedidoCompraControlBody ctr){
		this.nvoItem = ctr.getNvoItem();
		this.ctr = ctr;
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		MyPair tipo = this.nvoItem.getTipoGastoDescuento();	
		double valorDs = this.nvoItem.getImporteGastoDescuentoDs();
		
		if (tipo.esNuevo() == true) {
			this.mensajeError += "\n - Debe seleccionar el ítem de Gasto o Descuento..";
			out = false;
		}
		
		if ((valorDs >= 0) && (valorDs <= 0.001)) {
			this.mensajeError += "\n - El valor no debe ser cero..";
			out = false;
		}		
		
		if (this.existeItem(tipo) == true) {
			this.mensajeError += "\n - Ya existe un ítem del mismo tipo en la Factura..";
			out = false;
		}
		
		if (this.importeMayorQueFactura() == true) {
				this.mensajeError += "\n - El valor del item supera al Importe total de la Factura..";
				out = false;
		}
		
		if ((this.nvoItem.isValorProrrateo() == true)
				&& (this.nvoItem.getImporteDsCalculado() <= 0)) {
			this.mensajeError += "\n - El valor del prorrateo no debe ser negativo..";
			out = false;
		}
		
		if (this.tipoCorrecto() == false) {
			this.mensajeError += "\n - El tipo de Importación es: " + this.getDescripcionTipo();
			out = false;
		}
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error de validación al Cancelar..";
	}
	
	//verifica si ya existe un item segun el idArticulo en la factura
	private boolean existeItem(MyPair tipo){
		ImportacionFacturaDTO fac = ctr.getSelectedImportacionFactura();
		
		for (ImportacionFacturaDetalleDTO item : fac.getDetalles()) {			
			if (item.getTipoGastoDescuento().compareTo(tipo) == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	//verifica si el valor del item supera al total de la factura
	private boolean importeMayorQueFactura(){
		
		double importeItem = this.nvoItem.getImporteGsCalculado();
		double importeFact = this.ctr.getTotalImporteFactura()[0];
		
		return importeItem >= importeFact;
	}	
	
	//verifica si es correcto la insercion del item segun el tipo de Importacion
	private boolean tipoCorrecto(){
		boolean out = true;		
		
		MyPair tipo = this.ctr.getDto().getTipo();
		MyPair tipoCIF = this.ctr.getDtoUtil().getTipoImportacionCIF();
		MyPair tipoFOB = this.ctr.getDtoUtil().getTipoImportacionFOB();
		MyPair tipoCF = this.ctr.getDtoUtil().getTipoImportacionCF();
		boolean prorrateado = this.ctr.getDto().isCifProrrateado();
		
		MyPair tipoItem = this.nvoItem.getTipoGastoDescuento();
		MyPair tipoItemGastoFlete = this.ctr.getDtoUtil().getTipoCompraGastoFlete();
		MyPair tipoItemGastoSeguro = this.ctr.getDtoUtil().getTipoCompraGastoSeguro();
		MyPair tipoItemProrrFlete = this.ctr.getDtoUtil().getTipoCompraProrrateoFlete();
		MyPair tipoItemProrrSeguro = this.ctr.getDtoUtil().getTipoCompraProrrateoSeguro();
		
		if ((tipoItem.compareTo(tipoItemGastoFlete) == 0)
				&& ((tipo.compareTo(tipoFOB) == 0) 
						|| (tipo.compareTo(tipoCIF) == 0 && prorrateado == true))) {
			out = false;
		}
		
		if ((tipoItem.compareTo(tipoItemGastoSeguro) == 0)
				&& ((tipo.compareTo(tipoFOB) == 0)
						|| (tipo.compareTo(tipoCF) == 0)
							|| (tipo.compareTo(tipoCIF) == 0 && prorrateado == true))) {
			out = false;
		}
		
		if ((tipoItem.compareTo(tipoItemProrrFlete) == 0 
				|| tipoItem.compareTo(tipoItemProrrSeguro) == 0)
					&& ((tipo.compareTo(tipoFOB) == 0)
						|| (tipo.compareTo(tipoCF) == 0)
							|| (tipo.compareTo(tipoCIF) == 0 && prorrateado == false))) {
			out = false;
		}
		
		return out;
	}
	
	private String getDescripcionTipo(){
		String add = "";
		
		MyPair tipo = this.ctr.getDto().getTipo();
		MyPair tipoCIF = this.ctr.getDtoUtil().getTipoImportacionCIF();
		boolean prorrateado = this.ctr.getDto().isCifProrrateado();
		
		if ((tipo.compareTo(tipoCIF) == 0)
				&& (prorrateado == true)) {
			add = " Prorrateado";
		}
		
		if ((tipo.compareTo(tipoCIF) == 0)
				&& (prorrateado == false)) {
			add = " Sin Prorratear";
		}
		
		return tipo.getText() + add;
	}
}


//Validar Agregar Gasto Imprevisto
class ValidadorAgregarGastoImprevisto implements VerificaAceptarCancelar{

	private ImportacionGastoImprevistoDTO nvoGastoImprevisto;
	private Misc misc = new Misc();
	private String mensajeError = "";
	
	public ValidadorAgregarGastoImprevisto(ImportacionPedidoCompraControlBody ctr){
		this.nvoGastoImprevisto = ctr.getNvoGastoImprevisto();
	}
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.nvoGastoImprevisto.getProveedor().esNuevo() == true) {
			this.mensajeError = this.mensajeError + "\n - Debe seleccionar el Proveedor..";
			out = false;
		}
		
		if (misc.esIgual(this.nvoGastoImprevisto.getImporteDs(), 0) == true) {
			this.mensajeError = this.mensajeError + "\n - El importe debe ser mayor a cero..";
			out = false;			
		}	
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error de validación al Cancelar..";
	}
}

/**
 * Reporte de Importacion..
 */
class ReporteImportacion extends ReporteYhaguy {
	
	private ImportacionPedidoCompraDTO importacion;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_INTEGER, 30, false);
	static DatosColumnas col4 = new DatosColumnas("Costo Gs.", TIPO_DOUBLE_GS, 30, false); 
	static DatosColumnas col5 = new DatosColumnas("Costo USD", TIPO_DOUBLE_DS, 30, false); 
	static DatosColumnas col6 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 30, true); 
	static DatosColumnas col7 = new DatosColumnas("Importe USD", TIPO_DOUBLE_DS, 30, true);
	
	public ReporteImportacion(ImportacionPedidoCompraDTO importacion) {
		this.importacion = importacion;
	}
	
	static {
		cols.add(col1);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Compra Importacion");
		this.setDirectorio("compras");
		this.setNombreArchivo("Importacion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.importacion.getNumeroPedidoCompra();
		String factura = this.importacion.getImportacionFactura().get(0).getNumero();
		String proveedor = this.importacion.getProveedor().getRazonSocial();
		String tipoCambio = Utiles.getNumberFormat(this.importacion.getResumenGastosDespacho().getTipoCambio());
		String coeficiente = Utiles.getNumberFormatDs_(this.importacion.getResumenGastosDespacho().getCoeficiente());

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Importación", numero))
				.add(this.textoParValor("Nro. Factura", factura))
				.add(this.textoParValor("Tipo Cambio Gs.", tipoCambio))
				.add(this.textoParValor("Coeficiente", coeficiente)));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", proveedor)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de verificacion de importacion..
 */
class ReporteVerificacionImportacion extends ReporteYhaguy {
	
	private ImportacionPedidoCompraDTO importacion;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Verificado", TIPO_STRING, 30);
	
	public ReporteVerificacionImportacion(ImportacionPedidoCompraDTO importacion) {
		this.importacion = importacion;
	}
	
	static {
		col3.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Verificación de Códigos");
		this.setDirectorio("compras");
		this.setNombreArchivo("Importacion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.importacion.getNumeroPedidoCompra();

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Importación", numero))
				.add(this.textoParValor("Proveedor", this.importacion.getProveedor().getRazonSocial())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de conteo de importacion..
 */
class ReporteConteoImportacion extends ReporteYhaguy {
	
	private ImportacionPedidoCompraDTO importacion;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_STRING, 30);
	
	public ReporteConteoImportacion(ImportacionPedidoCompraDTO importacion) {
		this.importacion = importacion;
	}
	
	static {
		col3.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Recepción de Mercaderías");
		this.setDirectorio("compras");
		this.setNombreArchivo("Importacion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.importacion.getNumeroPedidoCompra();

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Importación", numero))
				.add(this.textoParValor("Proveedor", this.importacion.getProveedor().getRazonSocial())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de conteo de importacion..
 */
class ReporteConteoImportacion_ extends ReporteYhaguy {
	
	private ImportacionPedidoCompraDTO importacion;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Conteo 1", TIPO_INTEGER, 30);
	static DatosColumnas col4 = new DatosColumnas("Dif. 1", TIPO_INTEGER, 30);
	static DatosColumnas col5 = new DatosColumnas("Conteo 2", TIPO_INTEGER, 30);
	static DatosColumnas col6 = new DatosColumnas("Dif. 2", TIPO_INTEGER, 30);
	static DatosColumnas col7 = new DatosColumnas("Conteo 3", TIPO_INTEGER, 30);
	static DatosColumnas col8 = new DatosColumnas("Dif. 3", TIPO_INTEGER, 30);
	
	public ReporteConteoImportacion_(ImportacionPedidoCompraDTO importacion) {
		this.importacion = importacion;
	}
	
	static {
		cols.add(col1);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Recepción de Mercaderías");
		this.setDirectorio("compras");
		this.setNombreArchivo("Importacion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.importacion.getNumeroPedidoCompra();

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Importación", numero))
				.add(this.textoParValor("Proveedor", this.importacion.getProveedor().getRazonSocial())));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de diferencia de importacion..
 */
class ReporteDiferenciaImportacion extends ReporteYhaguy {
	
	private ImportacionPedidoCompraDTO importacion;	
	private String conteo;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Conteo", TIPO_INTEGER, 30);
	static DatosColumnas col4 = new DatosColumnas("Diferencia", TIPO_INTEGER, 30);
	
	public ReporteDiferenciaImportacion(ImportacionPedidoCompraDTO importacion, String conteo) {
		this.importacion = importacion;
		this.conteo = conteo;
	}
	
	static {
		cols.add(col1);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Recepción de Mercaderías");
		this.setDirectorio("compras");
		this.setNombreArchivo("Importacion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.importacion.getNumeroPedidoCompra();

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Importación", numero))
				.add(this.textoParValor("Proveedor", this.importacion.getProveedor().getRazonSocial()))
				.add(this.textoParValor("Conteo Nro.", conteo)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de diferencia de importacion..
 */
class ReporteDiferenciaImportacion_ extends ReporteYhaguy {
	
	private ImportacionPedidoCompraDTO importacion;	
	private String conteo;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Conteo", TIPO_INTEGER, 30);
	
	public ReporteDiferenciaImportacion_(ImportacionPedidoCompraDTO importacion, String conteo) {
		this.importacion = importacion;
		this.conteo = conteo;
	}
	
	static {
		cols.add(col1);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Recepción de Mercaderías");
		this.setDirectorio("compras");
		this.setNombreArchivo("Importacion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.importacion.getNumeroPedidoCompra();

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Importación", numero))
				.add(this.textoParValor("Proveedor", this.importacion.getProveedor().getRazonSocial()))
				.add(this.textoParValor("Conteo Nro.", conteo)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}