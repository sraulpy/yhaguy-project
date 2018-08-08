package com.yhaguy.gestion.compras.locales;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.domain.IiD;
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
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.articulos.buscador.BuscadorArticulosViewModel;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.gestion.comun.ControlLogica;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class CompraLocalControlBody extends BodyApp {
	
	static final String ZUL_PRINT = "/yhaguy/gestion/compras/locales/impresion.zul";
	static final String ZUL_IMPORTAR_PRESUP = "/yhaguy/gestion/compras/locales/importarPresupuesto.zul";
	static final String ZUL_IMPORTAR_OC = "/yhaguy/gestion/compras/locales/importarOrdenCompra.zul";
	static final String ZUL_ADD_GASTO = "/yhaguy/gestion/compras/locales/insertarResumenGasto.zul";
	
	final static String EVENTO_CREACION_ORDEN_COMPRA = "Se creó la Orden de Compra.";
	final static String EVENTO_AUTORIZACION_ORDEN_COMPRA = "Se autorizó la Orden de Compra..";
	final static String EVENTO_NUEVA_FACTURA = "Se agregó la factura nro. ";
	final static String EVENTO_ELIMINACION_FACTURA = "Se eliminó de la lista la Factura nro. ";
	final static String EVENTO_ELIMINACION_GASTO = "Se eliminó un gasto por valor de: ";
	final static String EVENTO_CONFIRMACION_RECEPCION = "Se confirmó la Recepción de Mercaderías..";
	
	private CompraLocalOrdenDTO dto = new CompraLocalOrdenDTO();	
	private CompraLocalFacturaDTO selectedFactura = new CompraLocalFacturaDTO();
	private CompraLocalFacturaDTO nvaFactura;
	
	private CompraLocalOrdenDetalleDTO nvoDetalle;
	private CompraLocalFacturaDetalleDTO nvoItem;
	private CompraLocalOrdenDetalleDTO selectedOrdenItem;
	
	private List<CompraLocalOrdenDetalleDTO> selectedOrdenItems;
	private List<CompraLocalOrdenDetalleDTO> selectedPresupuestoItems;
	private List<CompraLocalOrdenDetalleDTO> selectedImportarItems;
	private List<CompraLocalFacturaDetalleDTO> selectedFacturaItems;
	
	private List<CompraLocalGastoDTO> selectedGastos;
	private CompraLocalGastoDTO nvoGasto;	
	
	private String facturaItemsEliminar;
	private String itemsToDelete;
	private String mensajeError;
	
	private int totalCompras = 0;
	private long totalVentas = 0;
	private long totalStock = 0;
	
	@Wire
	private Popup popComparativo;
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
	private Textbox txNro;

	@Init(superclass=true)
	public void init(){		
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose(){
		this.seleccionarTab();
	}	

	@Override
	public Assembler getAss() {
		return new AssemblerCompraLocalOrden();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (CompraLocalOrdenDTO) dto;
		if (this.dto.getFacturas().size() > 0) {
			this.setSelectedFactura(this.dto.getFacturas().get(0));
		} else {
			this.setSelectedFactura(new CompraLocalFacturaDTO());
		}
		BindUtils.postNotifyChange(null, null, this, "*");
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		CompraLocalOrdenDTO nvoDto = new CompraLocalOrdenDTO();
		tab1.setSelected(true);
		this.sugerirValores(nvoDto);
		this.addEventoAgenda(EVENTO_CREACION_ORDEN_COMPRA);
		return nvoDto;		
	}

	@Override
	public String getEntidadPrincipal() {
		return CompraLocalOrden.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}	
	
	@Override
	public Browser getBrowser(){
		return new CompraLocalBrowser();
	}
	
	@Override
	public void clickCtrI() throws Exception{		
		this.insertarItemOrden(false);
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return this.dto.esNuevo();
	}
	
	@Override
	public void showImprimir() {
		this.imprimir();
	}
	
	@Override
	public boolean verificarAlGrabar() {
		return this.validarFormulario();
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeError;
	}	
	
	@Override
	public int getCtrAgendaTipo(){
		return ControlAgendaEvento.COTIZACION;
	}
	
	@Override
	public String getCtrAgendaKey(){
		return this.dto.getNumero();
	}
	
	@Override
	public String getCtrAgendaTitulo() {
		return "[Orden de Compra: " + this.getCtrAgendaKey() + "]";
	}
	
	/**
	 * COMANDOS..
	 */
	
	@Command 
	@NotifyChange("*")
	public void eliminarItemOrden(){
		this.eliminarItemOrdenCompra();		
	}
	
	@Command
	@NotifyChange("*")
	public void insertarItemOrden(
			@BindingParam("presupuesto") boolean presupuesto) throws Exception {
		// accion asociada a 'CTRL+I'
		if (this.isDeshabilitado() == true) {
			return;
		}
		this.insertarItem(presupuesto);
		txNro.focus();	
	}
	
	@Command
	@NotifyChange("*")
	public void importPresupuesto() throws Exception {
		this.importarPresupuesto();
	}
	
	@Command
	@NotifyChange("*")
	public void autorizarOrdenCompra() throws Exception {
		this.autorizarOC();
	}
	
	@Command
	@NotifyChange("*")
	public void insertarItemFactura() throws Exception {
		this.insertItemFactura();	
	}	
	
	@Command
	@NotifyChange("*")
	public void eliminarItemFactura() {		
		this.deleteItemFac();	
	}	
	
	@Command
	@NotifyChange("*")
	public void importarItems() throws Exception {
		this.importarItemsOC();
	}	
	
	@Command
	@NotifyChange("*")
	public void agregarFactura() throws Exception {
		this.addFactura();
	}
	
	@Command
	@NotifyChange("*")
	public void suprimirFactura(){
		this.deleteFactura();
	}	
	
	@Command
	@NotifyChange("*")
	public void insertarDescuento() throws Exception {
		this.insertDescuento();
	}	
	
	@Command
	@NotifyChange("*")
	public void confirmarRecepcion() {
		this.confirmRecepcion();
	}
	
	@Command
	@NotifyChange("*")
	public void agregarGasto() throws Exception {
		this.addGasto();
	}
	
	@Command
	@NotifyChange("*")
	public void eliminarGasto(){
		this.deleteGasto();
	}	
	
	@Command
	@NotifyChange("*")
	public void closeCompra() throws Exception {
		this.cerrarCompra();
	}
	
	@Command
	@NotifyChange("selectedOrdenItem")
	public void setSelected_Item(
			@BindingParam("item") CompraLocalOrdenDetalleDTO item,
			@BindingParam("comp") Component comp, @BindingParam("pop") Popup pop) {
		this.selectedOrdenItem = item;
		pop.open(comp, "after_end");
	}
	
	@Command
	public void setCondicion() {
		CompraLocalFacturaDTO factura = this.dto.getFactura();
		MyArray condicion = this.getUtil().getCondicionPagoContado();
		String sigla = (String) factura.getTipoMovimiento().getPos2();
		if(sigla.equals(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO))
			condicion = this.getUtil().getCondicionPagoCredito30();
		factura.setCondicionPago(condicion);
		BindUtils.postNotifyChange(null, null, factura, "condicionPago");
	}
	
	@Command
	public void calcularVencimiento(){
		CompraLocalFacturaDTO factura = this.dto.getFactura();
		int plazo = (Integer) factura.getCondicionPago().getPos2();
		Date emision = factura.getFechaOriginal();
		Date vencimiento = m.calcularFechaVencimiento(emision, plazo);
		factura.setFechaVencimiento(vencimiento);
		BindUtils.postNotifyChange(null, null, factura, "fechaVencimiento");
	}
	
	@Command
	@NotifyChange("*")
	public void abrirVentanaTimbrado() {
		WindowTimbrado w = new WindowTimbrado();
		w.setIdProveedor(this.dto.getProveedor().getId());
		w.setTimbrado((String) this.dto.getFactura().getTimbrado().getPos1());
		w.show(WindowPopup.NUEVO, w);

		if (w.isClickAceptar()) {
			this.dto.getFactura().setTimbrado(w.getSelectedTimbrado());
		} else {
			this.dto.getFactura().setTimbrado(new MyArray("", null));
		}
	}
	
	/*********************************************************/
	
	
	/*********************** FUNCIONES ***********************/
	
	/**
	 * impresion de la compra..
	 */
	private void imprimir() {
		List<Object[]> data = new ArrayList<Object[]>();

		for (CompraLocalOrdenDetalleDTO item : this.dto.getDetalles()) {
			Object[] obj1 = new Object[] { item.getArticulo().getPos1(),
					item.getArticulo().getPos2(), item.getArticulo().getPos3(),
					item.getArticulo().getPos4(), item.getCantidad(),
					item.getCostoGs(), item.getImporteGs() };
			data.add(obj1);
		}

		ReporteYhaguy rep = new ReporteOrdenCompra(this.dto);
		rep.setDatosReporte(data);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	}
	
	/**
	 * eliminar item orden compra..
	 */
	private void eliminarItemOrdenCompra() {
		if (this.mensajeSiNo("Desea eliminar el item seleccionado..?")) {
			this.dto.getDetalles().remove(this.selectedOrdenItem);
			this.selectedOrdenItem = null;
		}
		txNro.focus();
	}
	
	/**
	 * inserta el item..
	 */
	private void insertarItem(boolean presupuesto) throws Exception {
		this.nvoDetalle = new CompraLocalOrdenDetalleDTO();
		this.nvoDetalle.setPresupuesto(presupuesto);
		this.nvoDetalle.setIva(this.getTiposDeIva().get(0));
		WindowPopup w = new WindowPopup();
		w.setTitulo("Insertar Ítem");
		w.setModo(WindowPopup.NUEVO);
		w.setDato(this);
		w.setWidth("450px");
		w.setHigth("400px");
		w.setCheckAC(new ValidadorInsertarItem(this));
		w.show(Configuracion.INSERTAR_ITEM_COMPRA_LOCAL_ORDEN_ZUL);
		if (w.isClickAceptar()) {
			this.nvoDetalle.setUltCostoGs(this.getUltimoCosto());
			this.dto.getDetalles().add(nvoDetalle);
		}		
	}
	
	/**
	 * importar items del Presupuesto..
	 */
	private void importarPresupuesto() throws Exception {
		WindowPopup wp = new WindowPopup();		
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Importar ítems del Presupuesto");
		wp.setHigth("550px");
		wp.setWidth("950px");
		wp.setDato(this);
		wp.setCheckAC(new ValidadorImportarPresupuesto());		
		wp.show(ZUL_IMPORTAR_PRESUP);
		if (wp.isClickAceptar()) {	
			this.importarItemsSeleccionados();
		}
		this.selectedImportarItems = null;
	}
	
	/**
	 * importar items del presupuesto..
	 */
	private void importarItemsSeleccionados() {
		for (CompraLocalOrdenDetalleDTO item : this.selectedImportarItems) {
			item.setOrdenCompra(true);
		}
	}
	
	/**
	 * autorizacion de la Orden de Compra..
	 */
	private void autorizarOC() throws Exception {
		if (!this.mensajeSiNo("Desea autorizar la Orden de Compra..")) 
			return;
		this.dto.setAutorizadoPor(this.getUs().getNombre());
		this.dto.setAutorizado(true);
		CompraLocalFacturaDTO fac = new CompraLocalFacturaDTO();
		fac.setProveedor(this.dto.getProveedor());
		fac.setMoneda(this.dto.getMoneda());
		fac.setTipoCambio(this.dto.getTipoCambio());
		fac.setSucursal(this.dto.getSucursal());
		fac.setCondicionPago(this.dto.getCondicionPago());
		for (CompraLocalOrdenDetalleDTO item : this.dto.getDetalles()) {
			CompraLocalFacturaDetalleDTO det = new CompraLocalFacturaDetalleDTO();
			det.setArticulo(item.getArticulo());
			det.setCantidad(item.getCantidad());
			det.setCantidadRecibida(item.getCantidad());
			det.setCostoGs(item.getCostoGs());
			det.setCostoDs(item.getCostoDs());
			det.setIva(item.getIva());
			fac.getDetalles().add(det);
		}
		if (this.dto.getCondicionPago().getId().longValue() == this.getUtil()
				.getCondicionPagoContado().getId().longValue()) {
			fac.setTipoMovimiento(this.getUtil().getTmFacturaCompraContado());
		} else {
			fac.setTipoMovimiento(this.getUtil().getTmFacturaCompraCredito());
		}
		this.dto.setFactura(fac);
		this.dto = (CompraLocalOrdenDTO) this.saveDTO(this.dto);
		this.setEstadoABMConsulta();
		this.addEventoAgenda(EVENTO_AUTORIZACION_ORDEN_COMPRA);
		Clients.showNotification("Orden de Compra autorizada..");
	}
	
	/**
	 * inserta el item en la factura..
	 */
	private void insertItemFactura() throws Exception {
		this.nvoItem = new CompraLocalFacturaDetalleDTO();
		this.nvoItem.setIva(this.getTiposDeIva().get(0));
		WindowPopup w = new WindowPopup();
		w.setTitulo("Insertar Ítem");
		w.setModo(WindowPopup.NUEVO);
		w.setDato(this);
		w.setWidth("450px");
		w.setHigth("380px");
		w.setCheckAC(new ValidadorInsertarItemFactura(this));
		w.show(Configuracion.INSERTAR_ITEM_COMPRA_LOCAL_FACTURA_ZUL);
		if (w.isClickAceptar()) {
			this.dto.getFactura().getDetalles().add(nvoItem);
		}	
	}
	
	/**
	 * @return confirmacion para eliminar item..
	 */
	private boolean confirmarEliminarItemFactura(){
		this.facturaItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (CompraLocalFacturaDetalleDTO d : this.selectedFacturaItems) {
			this.facturaItemsEliminar = this.facturaItemsEliminar + "\n - " + d.getArticulo().getPos1();
		}		
		return this.mensajeSiNo(this.facturaItemsEliminar);
	}
	
	/**
	 * elimina items de la factura..
	 */
	private void deleteItemFac() {
		if (this.confirmarEliminarItemFactura() == true) {
			this.dto.getFactura().getDetalles().removeAll(this.selectedFacturaItems);
			this.selectedFacturaItems = null;
		}	
	}
	
	/**
	 * importa los items de la orden de compra..
	 */
	private void importarItemsOC() throws Exception {
		WindowPopup wp = new WindowPopup();		
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Importar ítems de la Orden de Compra");
		wp.setHigth("550px");
		wp.setWidth("950px");
		wp.setDato(this);
		wp.setCheckAC(new ValidadorImportarPresupuesto());		
		wp.show(ZUL_IMPORTAR_OC);
		if (wp.isClickAceptar()) {	
			this.exportarItems();
		}
		this.selectedImportarItems = null;
	}
	
	/**
	 * items de orden de compra pasa a factura..
	 */
	private void exportarItems() {
		for (CompraLocalOrdenDetalleDTO item : this.selectedImportarItems) {
			CompraLocalFacturaDetalleDTO det = new CompraLocalFacturaDetalleDTO();
			det.setArticulo(item.getArticulo());
			det.setCantidad(item.getCantidad());
			det.setCostoGs(item.getCostoGs());
			det.setIva(item.getIva());
			this.selectedFactura.getDetalles().add(det);
		}
	}
	
	/**
	 * agrega facturas..
	 */
	private void addFactura() throws Exception {		
		int plazo = (Integer) this.dto.getCondicionPago().getPos2();
		Date vencimiento = m.calcularFechaVencimiento(new Date(), plazo);
		
		this.nvaFactura = new CompraLocalFacturaDTO();
		this.nvaFactura.setMoneda(this.dto.getMoneda());
		this.nvaFactura.setSucursal(this.dto.getSucursal());			
		this.nvaFactura.setTipoMovimiento(this.getTipoMovimiento(this.dto.getCondicionPago(), true));
		this.nvaFactura.setCondicionPago(this.dto.getCondicionPago());
		this.nvaFactura.setProveedor(this.dto.getProveedor());
		this.nvaFactura.setFechaVencimiento(vencimiento);		
	
		WindowPopup w = new WindowPopup();
		w.setDato(this);
		w.setModo(WindowPopup.NUEVO);
		w.setTitulo("Agregar Factura");
		w.setWidth("420px");
		w.setHigth("370px");
		w.setCheckAC(new ValidadorAgregarFactura(this));
		w.show(Configuracion.AGREGAR_FACTURA_COMPRA_LOCAL_ZUL);
		if (w.isClickAceptar()) {
			if (this.nvaFactura.getTipoMovimiento()
					.compareTo(this.notaCreditoCompra) == 0) {
				if (this.nvaFactura.getTotalAsignadoGs() > 0) {
					this.nvaFactura.setTotalAsignadoGs(this.nvaFactura.getTotalAsignadoGs() * -1);
					this.nvaFactura.setTotalAsignadoDs(this.nvaFactura.getTotalAsignadoDs() * -1);
				}
			}
			this.dto.getFacturas().add(nvaFactura);
			this.setSelectedFactura(nvaFactura);
			this.addEventoAgenda(EVENTO_NUEVA_FACTURA + this.selectedFactura.getNumero());
		}	
	}
	
	/**
	 * inserta el item de descuento..
	 */
	private void insertDescuento() throws Exception {		
		this.nvoItem = new CompraLocalFacturaDetalleDTO();
		this.nvoItem.setArticulo(this.getItemDescuento());
		this.nvoItem.setCantidad(1);
		this.nvoItem.setDescuento(true);
		this.nvoItem.setIva(this.getTipoIvaExenta());
		
		WindowPopup w = new WindowPopup();
		w.setDato(this);
		w.setCheckAC(new ValidadorInsertarDescuento(this));
		w.setModo(WindowPopup.NUEVO);
		w.setTitulo("Insertar Descuento");
		w.setWidth("400px");
		w.setHigth("280px");
		w.show(Configuracion.INSERTAR_DESCUENTO_COMPRA_LOCAL_ZUL);
		if (w.isClickAceptar()) {
			this.dto.getFactura().getDetalles().add(this.nvoItem);
		}	
	}
	
	/**
	 * confirma la recepcion..
	 */
	private void confirmRecepcion() {
		if(this.mensajeSiNo("Desea confirmar la recepción de mercaderías..") == false)
			return;
		this.dto.setRecepcionado(true);
		Clients.showNotification("Recepción confirmada..");
		this.addEventoAgenda(EVENTO_CONFIRMACION_RECEPCION);
	}
	
	/**
	 * asigna el gasto a la compra..
	 */
	private void addGasto() throws Exception {
		this.nvoGasto = new CompraLocalGastoDTO();
		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.NUEVO);
		w.setDato(this);
		w.setTitulo("Asignar Gastos");
		w.setWidth("400px");
		w.setHigth("300px");
		w.setCheckAC(new ValidadorInsertarResumenGasto(this));
		w.show(ZUL_ADD_GASTO);
		if (w.isClickAceptar()) {
			this.dto.getResumenGastos().add(this.nvoGasto);
			this.addEventoAgenda("Se agregó un Gasto por valor de: "
					+ m.redondeo(nvoGasto.getMontoGs()) + " Gs.");
		}
		this.nvoGasto = null;
	}
	
	/**
	 * desasigna el gasto de la compra..
	 */
	private void deleteGasto() {
		if (mensajeSiNo("Esta seguro de eliminar el o los gastos seleccionados..")) {
			for (CompraLocalGastoDTO g : this.selectedGastos) {
				this.addEventoAgenda(EVENTO_ELIMINACION_GASTO + m.redondeo(g.getMontoGs()) + " Gs.");
				this.dto.getResumenGastos().remove(g);
			}
		}
	}
	
	/**
	 * suprime la factura de la compra..
	 */
	private void deleteFactura() {
		if (this.mensajeSiNo("Desea eliminar la Factura nro: \n"
				+ this.selectedFactura.getNumero())) {
			this.dto.getFacturas().remove(this.selectedFactura);
			this.addEventoAgenda(EVENTO_ELIMINACION_FACTURA
					+ this.selectedFactura.getNumero());
			this.selectedFactura = new CompraLocalFacturaDTO();
		}

	}	
	
	/**
	 * @return los costos finales..
	 */
	public List<MyArray> getItemsCostoFinal() {
		
		List<MyArray> out = new ArrayList<MyArray>();
		Hashtable<Long, MyArray> items = new Hashtable<>();
		CompraLocalFacturaDTO f = this.dto.getFactura() == null? 
				new CompraLocalFacturaDTO() : this.dto.getFactura();

			Object[] datos = this.datosFactura(f);
			double dctoGlobal = (double) datos[0];
			double importeFactura = (double) datos[1]; // Importe sin
														// Descuento..
			double coefGasto = this.getCoeficienteGasto(importeFactura);
			double coefDescuento = this.getCoeficienteDescuento(dctoGlobal,
					importeFactura);

			for (CompraLocalFacturaDetalleDTO d : f.getDetalles()) {

				double costoGravado = d.getCostoGs();
				double valorGasto = coefGasto * costoGravado;
				double valorDescuento = (coefDescuento * costoGravado)
						+ d.getDescuentoGs();
				double costoFinal = this.getCostoFinal(costoGravado,
						valorGasto, valorDescuento);

				if (items.get(d.getArticulo().getId()) != null) {
					Integer cantAnterior = (int) items.get(
							d.getArticulo().getId()).getPos5();
					double costoFinalAnterior = (double) items.get(
							d.getArticulo().getId()).getPos3();
					if (costoFinal > costoFinalAnterior) {
						items.get(d.getArticulo().getId()).setPos3(costoFinal - Utiles.getIVA(costoFinal, Configuracion.VALOR_IVA_10));
						items.get(d.getArticulo().getId()).setPos6(
								d.getCostoGs());
						items.get(d.getArticulo().getId()).setPos7(
								d.getCostoDs());
					}
					items.get(d.getArticulo().getId()).setPos5(
							cantAnterior + d.getCantidad());
				} else {
					if (d.isDescuento() == false) {
						MyArray mr = new MyArray();
						mr.setId(d.getArticulo().getId());
						mr.setPos1(d.getArticulo().getPos1());
						mr.setPos2(d.getArticulo().getPos4());
						mr.setPos3(costoFinal - Utiles.getIVA(costoFinal, Configuracion.VALOR_IVA_10));
						mr.setPos4(m.redondeoCuatroDecimales(
								(costoFinal - Utiles.getIVA(costoFinal, Configuracion.VALOR_IVA_10))
								/ this.dto.getTipoCambio()));

						mr.setPos5(new Integer(d.getCantidad()));
						mr.setPos6(d.getCostoGs());
						mr.setPos7(d.getCostoDs());
						items.put(d.getArticulo().getId(), mr);
					}
				}
			}

			Set<Long> keys = items.keySet();
			for (Long key : keys) {
				out.add(items.get(key));
			}
		return out;
	}
	
	/**
	 * cierre de la compra..
	 */
	private void cerrarCompra() throws Exception {		
		if(this.mensajeSiNo("Desea cerrar la compra..") == false)
			return;
		this.dto.setReadonly();
		this.dto.setCerrado(true);
		this.setTimbrado();
		this.dto.getFactura().setReadonly();
		this.dto = (CompraLocalOrdenDTO) this.saveDTO(this.dto);
		this.volcarCompra();
		this.setEstadoABMConsulta();
		Clients.showNotification("Compra correctamente cerrada..");
	}
	
	/**
	 * asigna los timbrados a las facturas.
	 */
	private void setTimbrado() {
		if (this.dto.getFactura().getTimbrado().esNuevo()) {
			WindowTimbrado w = new WindowTimbrado();
			w.agregarTimbrado(this.dto.getFactura().getTimbrado(), this.dto.getFactura().getProveedor().getId());
		}
	}
	
	/**
	 * volcar compra..
	 */
	private void volcarCompra() throws Exception{		
		this.volcarStock_Costos();
		this.actualizarCtaCte();		
	}	
	
	/**
	 * Vuelca el Stock con los costos..
	 */
	private void volcarStock_Costos() throws Exception {
		
		for (MyArray m : this.getItemsCostoFinal()) {
			MyArray art = new MyArray();
			art.setId(m.getId());
			art.setPos1(m.getPos1());

			Integer cant = (Integer) m.getPos5();
			double costoFinalGs = (double) m.getPos3();

			this.ctr.actualizarArticuloDepositoCompra(this.dto.getId(),
					this.dto.getTipoMovimiento(), art, cant.longValue(),
					costoFinalGs, true, this.dto.getDeposito(),
					Configuracion.OP_SUMA);
			ControlArticuloCosto.addMovimientoCosto(art.getId(), costoFinalGs, 
					this.dto.getFactura().getFechaOriginal(), this.dto.getFactura().getId(),
					this.dto.getFactura().getTipoMovimiento().getId(), this.getLoginNombre());
		}
	}
	
	/**
	 * actualizar Cta Cte..
	 */
	private void actualizarCtaCte() throws Exception {
		ControlCtaCteEmpresa ctr = new ControlCtaCteEmpresa(null);

		IiD empresa = this.dto.getProveedor().getEmpresa();
		MyPair moneda = new MyPair(this.dto.getMoneda().getId(), "");
		MyArray sucursal = new MyArray();
		sucursal.setId(this.dto.getSucursal().getId());
		CompraLocalFacturaDTO fac = this.dto.getFactura();

			long idMovimientoOriginal = fac.getId();
			String nroComprobante = fac.getNumero();
			Date fechaEmision = fac.getFechaOriginal();
			double importeOriginal = this.getImporteOriginalFactura(fac);
			MyArray tipoMovimiento = fac.getTipoMovimiento();

			// si es factura contado
			if (fac.getTipoMovimiento().compareTo(this.facturaCompraContado) == 0) {
				ctr.addCtaCteEmpresaMovimientoFacturaContado(empresa,
						idMovimientoOriginal, nroComprobante, fechaEmision,
						importeOriginal, moneda, tipoMovimiento,
						this.caracterMovimientoPrv, sucursal, true, "");
			}

			// si es factura credito
			if (fac.getTipoMovimiento().compareTo(facturaCompraCredito) == 0) {
				ctr.addCtaCteEmpresaMovimientoFacturaCredito(empresa,
						idMovimientoOriginal, nroComprobante, fechaEmision,
						(int) fac.getCondicionPago().getPos2(), 1,
						importeOriginal, 0, importeOriginal, moneda,
						tipoMovimiento, this.caracterMovimientoPrv, sucursal, "",
						fac.getTipoCambio());
			}

			// si es nota de credito
			if (fac.getTipoMovimiento().compareTo(notaCreditoCompra) == 0) {
				// falta implementar
			}

			// si es nota de debito
			if (fac.getTipoMovimiento().compareTo(notaDebitoCompra) == 0) {
				// falta implementar
			}
	}
	
	/***************************************************************/	
	
	
	/*************************** UTILES ****************************/	
	
	private ControlLogica ctr = new ControlLogica(null);
	private UtilDTO utilDto = this.getDtoUtil();
	private AccesoDTO accesosUsuario = this.getAcceso();
	
	//Tipos de Movimiento de Compras
	private MyArray facturaCompraContado = utilDto.getTmFacturaCompraContado();
	private MyArray facturaCompraCredito = utilDto.getTmFacturaCompraCredito();
	private MyArray notaCreditoCompra = utilDto.getTmNotaCreditoCompra();
	private MyArray notaDebitoCompra = utilDto.getTmNotaCreditoCompra();
	private MyArray monedaLocal = utilDto.getMonedaGuaraniConSimbolo();
	
	private MyPair caracterMovimientoPrv = utilDto.getCtaCteEmpresaCaracterMovProveedor();
	
	//Retorna los tipos de movimiento de Compra
	public List<MyArray> getTiposMovimiento(){		
		List<MyArray> out = new ArrayList<MyArray>();
		out.add(this.utilDto.getTmFacturaCompraContado());
		out.add(this.utilDto.getTmFacturaCompraCredito());
		return out;
	}	
	
	private void seleccionarTab(){
		List<Tab> tabs = new ArrayList<Tab>();
		tabs.add(tab1);	tabs.add(tab2);
		tabs.add(tab3);	tabs.add(tab4);
		tabs.add(tab5);	tabs.add(tab6);
		for (Tab tab : tabs) {
			if (tab.isDisabled() == false) {
				tab.setSelected(true);
				return;
			}
		}
	}
	
	//[0]:exenta Gs [1]:exenta Ds [2]:gravada Gs [3]:gravada Ds
	public Double[] getImporteFactura(){
		Object[] datos = this.datosFactura(this.selectedFactura);
		
		double exenGs = 0;	double exenDs = 0;
		double gravGs = (double) datos[1];
		double gravGsDto = this.selectedFactura.getDescuentoGs();
		double gravDs = (double) datos[2];
		double gravDsDto = this.selectedFactura.getDescuentoDs();
		
		Double[] out = {exenGs, exenDs, (gravGs - gravGsDto), (gravDs - gravDsDto)};
		return out;
	}
	
	public double getImporteFacturaGs(CompraLocalFacturaDTO factura){
		Object[] datos = this.datosFactura(factura);
		
		double gravGs = (double) datos[1];
		double gravGsDto = factura.getDescuentoGs();
		
		return gravGs - gravGsDto;
	}
	
	/**
	 * retorna el importe original de la factura de acuerdo
	 * al tipo de moneda..
	 */
	private double getImporteOriginalFactura(CompraLocalFacturaDTO factura){
		double importeGravadaGs = (double) factura.getTotalImporteGs();
		double importeGravadaDs = (double) factura.getTotalImporteGs();
		
		if (this.dto.getMoneda().compareTo(utilDto.getMonedaGuaraniConSimbolo()) == 0) {
			return importeGravadaGs;
		} else {
			return importeGravadaDs;
		}		
	}

	private void sugerirValores(CompraLocalOrdenDTO nvoDto){
		nvoDto.setSucursal(accesosUsuario.getSucursalOperativa());	
		nvoDto.setTipoMovimiento(utilDto.getTmOrdenCompra());
		nvoDto.setMoneda(monedaLocal);
		nvoDto.setTipoCambio(utilDto.getCambioVentaBCP(nvoDto.getMoneda()));
		nvoDto.setCondicionPago(utilDto.getCondicionPagoContado());
	}
	
	private MyArray getTipoMovimiento(IiD condicion, boolean esFactura){
		
		if (esFactura == false) {
			return null;
		}
		
		long idCondicion = condicion.getId();
		long idCondicionContado = utilDto.getCondicionPagoContado().getId();
		MyArray facCompraContado = utilDto.getTmFacturaCompraContado();
		MyArray facCompraCredito = utilDto.getTmFacturaCompraCredito();
		
		if (idCondicion == idCondicionContado) {
			return facCompraContado;
		} else {
			return facCompraCredito;
		}
	}
	
	/**
	 * @return [0]:dtoGlobalGs - [1]:importeGravadaGs - [2]:importeGravadaDs 
	 */
	private Object[] datosFactura(CompraLocalFacturaDTO factura){
		double dtoConcedido = 0;  double importeGravGs = 0;	double importeGravDs = 0;
		
		for (CompraLocalFacturaDetalleDTO d : factura.getDetalles()) {
			
			if (d.isDescuento() == true) {
				dtoConcedido += d.getImporteGs();
			} else {
				importeGravGs += d.getImporteGs();
			}
		}		
		double dtoGlobal = factura.getDescuentoGs() + (dtoConcedido * -1);		
		
		Object[] out = {dtoGlobal, importeGravGs, importeGravDs};
		return out;
	}
	
	@Command
	@NotifyChange("importeOrdenCompra")
	public void dolarizar(@BindingParam("item") CompraLocalOrdenDetalleDTO item){
		double costoGs = item.getCostoGs();
		item.setCostoDs(costoGs / this.dto.getTipoCambio());
		BindUtils.postNotifyChange(null, null, item, "costoDs");
	}
	
	@Command
	@NotifyChange("importeOrdenCompra")
	public void guaranizar(@BindingParam("item") CompraLocalOrdenDetalleDTO item){
		double costoDs = item.getCostoDs();
		item.setCostoGs(costoDs * this.dto.getTipoCambio());
		BindUtils.postNotifyChange(null, null, item, "costoGs");
	}
	
	@Command
	@NotifyChange({"importeFactura", "itemsCostoFinal"})
	public void dolarizarFactura(@BindingParam("item") CompraLocalFacturaDetalleDTO item){
		double costoGs = item.getCostoGs();
		item.setCostoDs(costoGs / this.dto.getTipoCambio());
		BindUtils.postNotifyChange(null, null, item, "costoDs");
	}
	
	@Command
	@NotifyChange({"importeFactura", "itemsCostoFinal"})
	public void guaranizarFactura(@BindingParam("item") CompraLocalFacturaDetalleDTO item){
		double costoDs = item.getCostoDs();
		item.setCostoGs(costoDs * this.dto.getTipoCambio());
		BindUtils.postNotifyChange(null, null, item, "costoGs");
	}
	
	@Command
	@NotifyChange({"importeGasto", "itemsCostoFinal"})
	public void dolarizarGasto(@BindingParam("item") CompraLocalGastoDTO item){
		double montoGs = item.getMontoGs();
		item.setMontoDs(montoGs / this.dto.getTipoCambio());
		BindUtils.postNotifyChange(null, null, item, "montoDs");
	}
	
	@Command
	@NotifyChange({"importeGasto", "itemsCostoFinal"})
	public void guaranizarGasto(@BindingParam("item") CompraLocalGastoDTO item){
		double montoDs = item.getMontoDs();
		item.setMontoGs(montoDs * this.dto.getTipoCambio());
		BindUtils.postNotifyChange(null, null, item, "montoGs");
	}
	
	@Command
	@NotifyChange({"importeFactura", "itemsCostoFinal"})
	public void dolarizarDescuento(){
		double descGs = this.selectedFactura.getDescuentoGs();
		this.selectedFactura.setDescuentoDs(descGs / this.dto.getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.selectedFactura, "descuentoDs");
	}
	
	@Command
	@NotifyChange({"importeFactura", "itemsCostoFinal"})
	public void guaranizarDescuento(){
		double descDs = this.selectedFactura.getDescuentoDs();
		this.selectedFactura.setDescuentoGs(descDs * this.dto.getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.selectedFactura, "descuentoGs");
	}
	
	@Command
	@NotifyChange({"importeOrdenCompra", "importeFactura", "itemsCostoFinal"})
	public void refresh(){
	}
	
	@Command
	@NotifyChange({"importeFactura", "itemsCostoFinal"})
	public void verificarNotaCredito(@BindingParam("item") CompraLocalFacturaDetalleDTO item){
		if (this.selectedFactura.getTipoMovimiento()
				.compareTo(this.notaCreditoCompra) == 0) {
			if (item.getCantidad() > 0) {
				item.setCantidad(item.getCantidad() * -1);
			}
		}
		BindUtils.postNotifyChange(null, null, item, "cantidad");
	}	
	
	@Command @NotifyChange("*")
	public void refreshTipoCambio(){
		double tipoCambio = this.getDtoUtil().getCambioVentaBCP(this.dto.getMoneda());
		this.dto.setTipoCambio(tipoCambio);
	}
	
	private UtilDTO getUtil() {
		return (UtilDTO) this.getDtoUtil();
	}
	
	public boolean getCheckmarkOrdenCompra(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getDetalles().size() > 0) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckmarkFactura(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.selectedFactura.getDetalles().size() > 0) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckmarkGastos(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getResumenGastos().size() > 0) {
			out = true;
		}
		return out;
	}
		
	//Para evitar ingresar una nueva Factura con nro y timbrado duplicado..
	public boolean facturaDuplicada() {
		boolean out = false;
		String nvoTimbrado = (String) this.getNvaFactura().getTimbrado().getPos1();

		for (CompraLocalFacturaDTO f : this.dto.getFacturas()) {
			String nroTimbrado = (String) f.getTimbrado().getPos1();
			if ((f.getNumero().compareTo(this.getNvaFactura().getNumero()) == 0)
					&& (nroTimbrado.compareTo(nvoTimbrado)) == 0) {
				out = true;
			}
		}
		return out;
	}
	
	public String getTipoOrdenCompra() {
		String out = "";
		if (this.dto.isAutorizado()) {
			out = "Orden de Compra Autorizada..";
		} else {
			out = "Orden de Compra Pendiente de Autorización..";
		}
		return out;
	}
	
	/***********************************************************************************************/
	
	
	/**************************************** ENVIO DE CORREO **************************************/
	
	String linkOrdenCompra = "";
	String pathRealOrdenCompra = "";
	
	CuerpoCorreo correo = new CuerpoCorreo("compraslocales@yhaguyrepuestos.com.py", "yrmkt1970",			
			Configuracion.TEXTO_CORREO_IMPORTACION);			
		
	@Command
	@NotifyChange("*")
	public void correo() throws Exception {
		correo.setAsunto(this.getTipoOrdenCompra());		
		this.generarAdjuntoCorreo();			
		this.showCorreo();
		txNro.focus();
	}
	
	public void showCorreo() throws Exception{
		
		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.NUEVO);
		w.setTitulo("Envio de Correos");
		w.setWidth("750px");
		w.setHigth("500px");
		w.setDato(this);
		w.setCheckAC(new ValidadorEnviarCorreo(this));
		w.show(Configuracion.CORREO_COMPRA_LOCAL_ZUL);
		if (w.isClickAceptar()) {				
			this.enviarCorreo();
		} else {
			//si no envia borra el archivo
			this.m.borrarArchivo(this.pathRealOrdenCompra);
		}
	}

	// Genera el informe de Pedido Compra, guarda en un directorio en formato
	// PDF y de ahi adjunta al correo..
	public void generarAdjuntoCorreo(){}	
	
	public void enviarCorreo() throws Exception {
		
		String[] send = correo.getDestinatario().trim().split(";");
		String[] sendCC = correo.getDestinatarioCopia().trim().split(";");
		String[] sendCCO = correo.getDestinatarioCopiaOculta().trim().split(";");

		correo.setAdjunto(this.pathRealOrdenCompra);
				
		EnviarCorreo enviarCorreo = new EnviarCorreo();			
		enviarCorreo.sendMessage(send, sendCC, sendCCO,
		correo.getAsunto(), correo.getCuerpo(),
		correo.getRemitente(), correo.getClave(), this.getNombreArchivo() + ".pdf",
		correo.getAdjunto());
			
		this.getCtrAgenda().addDetalle(ControlAgendaEvento.COTIZACION, this.dto.getNumero(),
					0, Configuracion.TEXTO_CORREO_ENVIADO + correo.getAsunto(), this.linkOrdenCompra);
			
		this.mensajePopupTemporal(Configuracion.TEXTO_CORREO_ENVIADO_CORRECTAMENTE);
	}		

	/***********************************************************************************************/	
	
	
	/*************************************** UPLOAD DE ARCHIVOS CSV ********************************/
	
	private String nombreArchivoCSV;
	
	//Upload Orden Compra CSV
	@SuppressWarnings("static-access")
	@Command
	public void uploadPedidoCompra(@BindingParam("file") Media file){
		
		if (this.dto.getDetalles().size() > 0) {
			if (this.mensajeSiNo("Esta Orden ya tiene ítems.."
					+ "\n Si continúa estos se reemplazarán por los ítems del archivo csv.. \n"
					+ "\n Desea continuar..?") == false) {
				return;
			}
		}
		
		String name = Configuracion.NRO_COMPRA_LOCAL_ORDEN + "_" + m.getIdUnique();
		String path = Configuracion.pathOrdenCompra;
		this.setPathCsvOrdenCompra(path + name + ".csv");
		this.setLinkCsvImportado(Configuracion.pathOrdenCompraGenerico + name + ".csv");
		this.nombreArchivoCSV = name;
		
		m.uploadFile(path, name, ".csv", (InputStream) new ByteArrayInputStream(file.getStringData().getBytes()));
		this.csvOrdenCompra();
		BindUtils.postNotifyChange(null, null, this, "*");
		txNro.focus();
	}
	
	/************************************************************************************************/
	
	
	/**************************************** CSV ORDEN COMPRA **************************************/
	
	/**
	 * En el evento onUpload() se guarda en el 'pathCsvOrdenCompra' la ruta completa donde
	 * se guardo el archivo y en 'linkCsvImportado' el link para acceder al mismo desde el browser.
	 * Se ejecuta la funcion csvPedidoCompra() que invoca al metodo start() de la clase CSV
	 * pasandole la ruta completa del archivo 'pathCsvOrdenCompra'. Si el formato del archivo es
	 * el correcto recorre el mismo y por cada fila genera un detalle en la Orden de Compra.
 	 */

	private String pathCsvOrdenCompra = "";
	private String linkCsvImportado = "";		
	
	public void csvOrdenCompra() {	
		
		// Se declaran los parametros para pasar a la clase CSV
		String[][] cab = { { "Cliente", CSV.STRING }, { "Fecha", CSV.DATE },
				{ "Codigo", CSV.STRING }, { "Tipo de Compra", CSV.STRING } };

		String[][] cabDet = { { "codigo", CSV.STRING },
				{ "cantidad", CSV.NUMERICO }, { "articulo", CSV.STRING },
				{ "valor", CSV.NUMERICO }, { "total", CSV.NUMERICO } };

		RegisterDomain rr = RegisterDomain.getInstance();
		try {

			CSV csv = new CSV(cab, cabDet, this.getPathCsvOrdenCompra());
			List<CompraLocalOrdenDetalleDTO> detalles = new ArrayList<CompraLocalOrdenDetalleDTO>();
			if (this.verificarCabeceraCSV(csv.getCabecera("Codigo")) == false) {
				this.mensajeError("El Proveedor seleccionado es distinto al ingresado en el archivo csv.. "
						+ "\n Favor verifique..");
				return;
			}

			csv.start();
			while (csv.hashNext()) {

				Articulo art = rr.getArticulo(csv.getDetalleString("codigo"));	
				MyArray articulo = null;

				if (art == null) {
					
					if (rr.getArticuloByCodigoInterno(csv.getDetalleString("codigo")) != null) {
						this.mensajeError("El articulo con código: " + csv.getDetalleString("codigo")
										+ "\n no corresponde al Proveedor: " + this.dto.getProveedor().getRazonSocial()
										+ "\n Favor verifique..");
						return;
					}
					
				} else {									
					articulo = new MyArray();
					articulo.setId(art.getId());
					articulo.setPos1(art.getCodigoInterno());
					articulo.setPos2(art.getDescripcion());
				}				
				
				CompraLocalOrdenDetalleDTO d = this.nvoItemOrden(
						articulo, csv.getDetalleDouble("cantidad"), csv.getDetalleDouble("valor"));
				detalles.add(d);				
			}
			
			this.dto.setDetalles(detalles);
				
			this.addEventoAgenda(ControlAgendaEvento.COTIZACION, this.dto.getNumero(),
					0, Configuracion.TEXTO_PEDIDO_COMPRA_CSV_IMPORTADO + this.nombreArchivoCSV, this.linkCsvImportado);

		} catch (Exception e) {
			e.printStackTrace();
			mensajeError(e.getMessage());
		}
	}
	
	
	//El costo que recibe como parametro debe ser en Guaranies
	private CompraLocalOrdenDetalleDTO nvoItemOrden(MyArray articulo, Double cantidad, double costoGs) throws Exception {

		double costoDs = (costoGs / this.dto.getTipoCambio());
		
		CompraLocalOrdenDetalleDTO item = new CompraLocalOrdenDetalleDTO();
		item.setArticulo(articulo);
		item.setCantidad(cantidad.intValue());
		item.setCostoGs(costoGs);
		item.setCostoDs(costoDs);
		item.setUltCostoGs(this.getUltimoCosto(articulo.getId(), this.dto.getTipoMovimiento()));
		
		return item;
	}
	
	
	// Verifica los datos de cabecera al importar el Pedido Compra CSV
	public boolean verificarCabeceraCSV(String codigoProveedor) throws Exception {
		boolean out = true;
		if (this.dto.getProveedor().getCodigoEmpresa().compareTo(codigoProveedor) != 0) {
			out = false;
		}
		return out;
	}	
	
	
	/***************************************** VALIDACIONES ****************************************/	
	
	/**
	 * @return validacion del formulario..
	 */
	private boolean validarFormulario(){	
		boolean out = true;	
		this.mensajeError = "No se puede grabar la información debido a: \n";
		
		if (this.dto.getProveedor().esNuevo() == true) {
			out = false;
			this.mensajeError += "\n - Debe seleccionar un Proveedor..";
		}
		
		if (this.dto.getCondicionPago().esNuevo() == true) {
			out = false;
			this.mensajeError += "\n - Debe asignar una Condición de Pago..";
		}
		
		if (this.dto.getDetalles().size() == 0) {
			out = false;
			this.mensajeError += "\n - Debe ingresar al menos un ítem..";
		}
		
		for (CompraLocalFacturaDTO fac : this.dto.getFacturas()) {
			if(fac.getDetalles().size() == 0){
				out = false;
				this.mensajeError += "\n - Cada factura debe tener al menos un ítem..";
			}
		}	
		
		try {
			if ((this.dto.esNuevo()) && (out == true)) {
				this.dto.setNumero(Configuracion.NRO_COMPRA_LOCAL_ORDEN
						+ "-"
						+ AutoNumeroControl.getAutoNumero(
								Configuracion.NRO_COMPRA_LOCAL_ORDEN, 5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return out;
	}
	
	/**
	 * Validador importar presupuesto..
	 */
	class ValidadorImportarPresupuesto implements VerificaAceptarCancelar {

		private String mensaje;
		
		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			if ((selectedImportarItems == null) || (selectedImportarItems.size() == 0)) {
				out = false;
				this.mensaje += "\n - Debe seleccionar los ítems a importar..";
			}
			
			if(selectedImportarItems != null) {
				for (CompraLocalOrdenDetalleDTO item : selectedImportarItems) {

					if (item.getCantidad() <= 0) {
						out = false;
						this.mensaje += "\n - La cantidad de c/ ítem debe ser mayor a cero..";
					}
					if (item.getCostoGs() <= 0) {
						out = false;
						this.mensaje += "\n - El precio de c/ ítem ser mayor a cero..";
					}
				}
			}
			
			return out;
		}

		@Override
		public String textoVerificarAceptar() {
			return this.mensaje;
		}

		@Override
		public boolean verificarCancelar() {
			return true;
		}

		@Override
		public String textoVerificarCancelar() {
			return "Error al cancelar..";
		}		
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "totalVentas", "totalCompras" })
	public String getSrcRiesgo() {
		return this.totalVentas - this.totalCompras >= 0? "/core/images/tick.png" : "/core/images/exclamation.png";
	}
	
	/**
	 * @return las compras locales del articulo en el anho corriente..
	 */
	@DependsOn("selectedOrdenItem")
	public List<Object[]> getHistorialCompras() throws Exception {
		this.totalCompras = 0;
		if (this.selectedOrdenItem == null) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getComprasLocalesPorArticulo(this.selectedOrdenItem
				.getArticulo().getId(), Utiles.getFechaInicioAnho(), new Date());
		for (Object[] compra : out) {
			this.totalCompras += (int) compra[3];
		}		
		BindUtils.postNotifyChange(null, null, this, "totalCompras");
		return out;
	}
	
	/**
	 * @return las ventas del articulo en el anho corriente..
	 */
	@DependsOn("selectedOrdenItem")
	public List<Object[]> getHistorialVentas() throws Exception {
		this.totalVentas = 0;
		if (this.selectedOrdenItem == null) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getVentasPorArticulo(this.selectedOrdenItem
				.getArticulo().getId(), Utiles.getFechaInicioAnho(), new Date());
		for (Object[] vta : out) {
			this.totalVentas += (long) vta[3];
		}		
		BindUtils.postNotifyChange(null, null, this, "totalVentas");
		return out;
	}
	
	/**
	 * @return el titulo de la ventana comparativo..
	 */
	public String getTitleComparativo() {
		return "Compras registradas (" + Utiles.getAnhoActual() + ")";
	}
	
	/**
	 * @return el titulo de la ventana comparativo..
	 */
	public String getTitleComparativo_() {
		return "Ventas registradas (" + Utiles.getAnhoActual() + ")";
	}
	
	@DependsOn("selectedOrdenItem")
	public List<MyArray> getExistencia() throws Exception {
		this.totalStock = 0;
		List<MyArray> out = new ArrayList<MyArray>();
		if(this.selectedOrdenItem == null) return out;
		for (MyPair dep : this.getDepositos()) {
			MyArray my = new MyArray();
			my.setPos1(dep.getText());
			my.setPos2(this.getStock(this.selectedOrdenItem.getArticulo().getId(), dep.getId()));
			out.add(my);
		}	
		for (MyArray my : out) {
			long stock = (long) my.getPos2();
			this.totalStock += stock;
		}
		BindUtils.postNotifyChange(null, null, this, "totalStock");
		return out;
	}
	
	/**
	 * @return los depositos de la sucursal..
	 */
	private List<MyPair> getDepositos() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Deposito> deps = rr.getDepositosPorSucursal(BuscadorArticulosViewModel.ID_SUC_PRINCIPAL);
		for (Deposito dep : deps) {
			out.add(new MyPair(dep.getId(), dep.getDescripcion()));
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
	
	@DependsOn("dto.proveedor")
	public boolean isDetalleVisible() {
		return !this.dto.getProveedor().esNuevo();
	}
	
	@DependsOn("dto.proveedor")
	public boolean isPresupuestoVisible() {
		return !this.dto.getProveedor().esNuevo();
	}
	
	@DependsOn("dto")
	public boolean isOrdenCompraVisible() {
		return !this.dto.esNuevo();
	}
	
	@DependsOn("dto")
	public boolean isFacturaVisible() {
		return this.dto.isAutorizado();
	}
	
	@DependsOn({ "deshabilitado", "dto.autorizado" })
	public boolean isEditarItemVisible() {
		return (this.isDeshabilitado() == false)
				&& (this.dto.isAutorizado() == false);
	}
	
	@DependsOn({ "deshabilitado", "dto" })
	public boolean isEditarItemFacVisible() {
		return (this.isDeshabilitado() == false);
	}
	
	@DependsOn({ "deshabilitado", "selectedPresupuestoItems", "dto.autorizado" })
	public boolean isDeleteItemDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.selectedPresupuestoItems == null)
				|| (this.selectedPresupuestoItems.size() == 0)
				|| (this.dto.isAutorizado());
	}
	
	@DependsOn({ "deshabilitado", "dto.autorizado" })
	public boolean isInsertItemDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.dto.isAutorizado());
	}
	
	@DependsOn({ "deshabilitado", "selectedOrdenItem" })
	public boolean isDeleteItemOrdenDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.selectedOrdenItem == null);
	}
	
	@DependsOn({ "deshabilitado", "dto.autorizado" })
	public boolean isInsertItemOrdenDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.dto.isAutorizado());
	}
	
	@DependsOn({ "deshabilitado", "dto" })
	public boolean isAutorizarDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.dto.getDetalles().size() == 0)
				|| (this.dto.isAutorizado() 
				|| (this.dto.esNuevo()));
	}
	
	@DependsOn({ "deshabilitado", "dto" })
	public boolean isComparativoDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.dto.getDetallesOrdenCompra().size() == 0);
	}
	
	@DependsOn({ "deshabilitado", "dto" })
	public boolean isImportarDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.dto.getDetallesOrdenCompra().size() > 0)
				|| (this.dto.isAutorizado());
	}
	
	@DependsOn({ "deshabilitado", "selectedFactura" })
	public boolean isDeleteFacturaDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.selectedFactura == null)
				|| (this.selectedFactura.getNumero().isEmpty());
	}
	
	@DependsOn({ "deshabilitado", "selectedFacturaItems" })
	public boolean isDeleteItemFacDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.selectedFacturaItems == null)
				|| (this.selectedFacturaItems.size() == 0);
	}
	
	@DependsOn({ "deshabilitado", "selectedFactura" })
	public boolean isImportarOCDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.selectedFactura == null)
				|| (this.selectedFactura.getNumero().isEmpty())
				|| (this.selectedFactura.getDetalles().size() > 0);
	}
	
	@DependsOn({ "deshabilitado", "selectedGastos" })
	public boolean isDeleteGastoDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.selectedGastos == null)
				|| (this.selectedGastos.size() == 0);
	}	
	
	@DependsOn({ "deshabilitado", "dto.factura", "dto.deposito", "selectedFactura.detalles" })
	public boolean isCerrarCompraDisabled() {
		return this.isDeshabilitado()
				|| (this.dto.getFactura() == null)
				|| this.dto.getDeposito() == null;
	}
	
	/**
	 * @return si la operacion es habilitada..
	 */
	public boolean isOperacionHabilitada(String operacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.isOperacionHabilitada(this.getLoginNombre(), operacion);
	}
	
	/**
	 * @return los tipos de iva..
	 */
	public List<MyPair> getTiposDeIva() {
		List<MyPair> out = new ArrayList<MyPair>();
		for (MyArray iva : this.getDtoUtil().getTiposDeIva()) {
			MyPair iva_ = new MyPair(iva.getId(), (String) iva.getPos1());
			iva_.setSigla((String) iva.getPos2());
			out.add(iva_);
		}
		return out;
	}
	
	/**
	 * @return tipo de iva exenta..
	 */
	private MyPair getTipoIvaExenta() {
		MyArray iva = this.getDtoUtil().getTipoIvaExento();
		MyPair out = new MyPair();
		out.setId(iva.getId());
		out.setText((String) iva.getPos1());
		out.setSigla((String) iva.getPos2());
		return out;
	}
	
	/**
	 * @return el item de descuento..
	 */
	private MyArray getItemDescuento() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo desc = rr.getArticuloByCodigoInterno(Configuracion.CODIGO_ITEM_DESCUENTO_KEY);
		MyArray m = new MyArray();
		m.setId(desc.getId());
		m.setPos1(desc.getCodigoInterno());
		m.setPos2(desc.getDescripcion());
		return m;
	}
	
	/**
	 * @return la cantidad facturada del item..
	 */
	public int getCantidadFacturada(String codigo) {
		int out = 0;
		
		for (CompraLocalFacturaDTO fac : this.dto.getFacturas()) {
			for (CompraLocalFacturaDetalleDTO item : fac.getDetalles()) {
				String cod = (String) item.getArticulo().getPos1();
				if(cod.equals(codigo))
					out += item.getCantidad();
			}
		}		
		return out;
	}
	
	/**
	 * @return la diferencia de cantidad..
	 */
	public int getDiferenciaCantidad(String codigo, int cantRec) {
		return cantRec - this.getCantidadFacturada(codigo);
	}
	
	/**
	 * @return el estilo para la diferencia de cantidad..
	 */
	public String getStyleItem(String codigo, int cantRec) {
		int dif = this.getDiferenciaCantidad(codigo, cantRec);
		return dif < 0? "font-weight:bold;color:red" : "";
	}
	
	/**
	 * @return el ultimo costo del articulo..
	 */
	private double getUltimoCosto() throws Exception{		
		return getUltimoCosto(this.nvoDetalle.getArticulo().getId(), this.dto.getTipoMovimiento());
	}
	
	/**
	 * @return el ultimo costo del articulo..
	 */
	private double getUltimoCosto(long idArticulo, IiD tipoMovimiento) throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getUltimoCostoGs(idArticulo);
	}
	
	/**
	 * @return el costo final..
	 */
	private double getCostoFinal(double costoGravado, double valorGasto, double valorDescuento){
		return costoGravado + valorGasto - valorDescuento;		
	}
	
	/**
	 * @return el coeficiente de gasto..
	 */
	private double getCoeficienteGasto(double importeFactura){
		return this.dto.getTotalResumenGastos() / importeFactura;
	}
	
	/**
	 * @return el coeficiente de descuento..
	 */
	private double getCoeficienteDescuento(double descGlobal, double importeFactura){			
		return descGlobal / importeFactura;
	}
	
	public CompraLocalOrdenDTO getDto() {
		return dto;
	}

	public void setDto(CompraLocalOrdenDTO dto) {
		this.dto = dto;		
	}
	
	public CompraLocalFacturaDTO getSelectedFactura() {
		return selectedFactura;
	}

	public void setSelectedFactura(CompraLocalFacturaDTO selectedFactura) {
		this.selectedFactura = selectedFactura;
	}
	
	public List<CompraLocalOrdenDetalleDTO> getSelectedOrdenItems() {
		return selectedOrdenItems;
	}

	public void setSelectedOrdenItems(
			List<CompraLocalOrdenDetalleDTO> selectedOrdenItems) {
		this.selectedOrdenItems = selectedOrdenItems;
	}
	
	public String getItemsToDelete() {
		return itemsToDelete;
	}

	public void setItemsToDelete(String ordenItemsEliminar) {
		this.itemsToDelete = ordenItemsEliminar;
	}
	
	public CompraLocalOrdenDetalleDTO getNvoDetalle() {
		return nvoDetalle;
	}

	public void setNvoDetalle(CompraLocalOrdenDetalleDTO nvoDetalle) {
		this.nvoDetalle = nvoDetalle;
	}
	
	public CompraLocalFacturaDTO getNvaFactura() {
		return nvaFactura;
	}

	public void setNvaFactura(CompraLocalFacturaDTO nvaFactura) {
		this.nvaFactura = nvaFactura;
	}
	
	public CompraLocalFacturaDetalleDTO getNvoItem() {
		return nvoItem;
	}

	public void setNvoItem(CompraLocalFacturaDetalleDTO nvoItem) {
		this.nvoItem = nvoItem;
	}
	
	public List<CompraLocalFacturaDetalleDTO> getSelectedFacturaItems() {
		return selectedFacturaItems;
	}

	public void setSelectedFacturaItems(
			List<CompraLocalFacturaDetalleDTO> selectedFacturaItems) {
		this.selectedFacturaItems = selectedFacturaItems;
	}

	public String getFacturaItemsEliminar() {
		return facturaItemsEliminar;
	}

	public void setFacturaItemsEliminar(String facturaItemsEliminar) {
		this.facturaItemsEliminar = facturaItemsEliminar;
	}
	
	public List<CompraLocalGastoDTO> getSelectedGastos() {
		return selectedGastos;
	}

	public void setSelectedGastos(List<CompraLocalGastoDTO> selectedGastos) {
		this.selectedGastos = selectedGastos;
	}

	public CompraLocalGastoDTO getNvoGasto() {
		return nvoGasto;
	}

	public void setNvoGasto(CompraLocalGastoDTO nvoGasto) {
		this.nvoGasto = nvoGasto;
	}
	
	public CuerpoCorreo getCorreo() {
		return correo;
	}

	public void setCorreo(CuerpoCorreo correo) {
		this.correo = correo;
	}

	public String getNombreArchivo() {
		return this.dto.getNumero();
	}
	
	public String getLinkOrdenCompra() {
		return linkOrdenCompra;
	}

	public void setLinkOrdenCompra(String linkOrdenCompra) {
		this.linkOrdenCompra = linkOrdenCompra;
	}	
	
	public String getPathRealOrdenCompra() {
		return pathRealOrdenCompra;
	}

	public void setPathRealOrdenCompra(String pathRealOrdenCompra) {
		this.pathRealOrdenCompra = pathRealOrdenCompra;
	}
	
	public String getPathCsvOrdenCompra() {
		return pathCsvOrdenCompra;
	}

	public void setPathCsvOrdenCompra(String pathCsvOrdenCompra) {
		this.pathCsvOrdenCompra = pathCsvOrdenCompra;
	}

	public String getLinkCsvImportado() {
		return linkCsvImportado;
	}

	public void setLinkCsvImportado(String linkCsvImportado) {
		this.linkCsvImportado = linkCsvImportado;
	}

	public List<CompraLocalOrdenDetalleDTO> getSelectedPresupuestoItems() {
		return selectedPresupuestoItems;
	}

	public void setSelectedPresupuestoItems(
			List<CompraLocalOrdenDetalleDTO> selectedPresupuestoItems) {
		this.selectedPresupuestoItems = selectedPresupuestoItems;
	}

	public List<CompraLocalOrdenDetalleDTO> getSelectedImportarItems() {
		return selectedImportarItems;
	}

	public void setSelectedImportarItems(
			List<CompraLocalOrdenDetalleDTO> selectedImportarItems) {
		this.selectedImportarItems = selectedImportarItems;
	}

	public CompraLocalOrdenDetalleDTO getSelectedOrdenItem() {
		return selectedOrdenItem;
	}

	public void setSelectedOrdenItem(CompraLocalOrdenDetalleDTO selectedOrdenItem) {
		this.selectedOrdenItem = selectedOrdenItem;
	}

	public int getTotalCompras() {
		return totalCompras;
	}

	public void setTotalCompras(int totalCompras) {
		this.totalCompras = totalCompras;
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
}

/**
 * Validador de Insertar Item..
 */
class ValidadorInsertarItem implements VerificaAceptarCancelar {
	
	private CompraLocalOrdenDTO dto;
	private CompraLocalOrdenDetalleDTO nvoDet;
	private String mensajeError = "";
	
	public ValidadorInsertarItem(CompraLocalControlBody ctr){
		this.nvoDet = ctr.getNvoDetalle();
		this.dto = ctr.getDto();
	}	
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";

		if ((!this.nvoDet.isPresupuesto())
				&& (this.nvoDet.getCostoGs() <= 0.001)) {
			out = false;
			this.mensajeError = this.mensajeError
					+ "\n - El costo debe ser mayor a cero..";
		}

		if (this.nvoDet.getCantidad() <= 0.001) {
			out = false;
			this.mensajeError = this.mensajeError
					+ "\n - La cantidad debe ser mayor a cero..";
		}

		if (this.nvoDet.getArticulo().esNuevo() == true) {
			out = false;
			this.mensajeError = this.mensajeError
					+ "\n - Debe seleccionar un Artículo..";
		}
		
		if (this.isDuplicado()) {
			out = false;
			this.mensajeError += "\n - No se permiten ítems duplicados..";
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
		return null;
	}	
	
	/**
	 * @return true si ya esta el item en el detalle..
	 */
	private boolean isDuplicado() {
		String codNvo = (String) this.nvoDet.getArticulo().getPos1();
		
		for (CompraLocalOrdenDetalleDTO item : this.dto.getDetalles()) {
			String cod = (String) item.getArticulo().getPos1();
			if(codNvo.equals(cod))
				return true;
		}		
		return false;
	}
}


/**
 * Validador de Agregar Factura..
 */
class ValidadorAgregarFactura implements VerificaAceptarCancelar{

	private CompraLocalControlBody ctr = new CompraLocalControlBody();
	private CompraLocalFacturaDTO fac = new CompraLocalFacturaDTO();
	private String mensajeError = "";
	
	//Constructor
	public ValidadorAgregarFactura(CompraLocalControlBody ctr){
		this.ctr = ctr;
		this.fac = ctr.getDto().getFactura();
	}
	
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		try {

			RegisterDomain rr = RegisterDomain.getInstance();

			String nroFactura = this.fac.getNumero();
			String nroTimbrado = (String) this.fac.getTimbrado().getPos1();

			if (this.fac.getTotalAsignadoGs() < 0.001) {
				out = false;
				this.mensajeError = mensajeError
						+ "\n - El Total debe ser mayor a cero..";
			}

			if (this.ctr.facturaDuplicada() == true) {
				out = false;
				this.mensajeError = mensajeError
						+ "\n - Esta Orden de Compra ya cuenta con una "
						+ "factura con el mismo número y el mismo timbrado..";
			}

			if (rr.existeFacturaCompra(this.ctr.getDto().getId(), nroFactura,
					nroTimbrado) == true) {
				out = false;
				this.mensajeError = mensajeError
						+ "\n - Ya existe en la Base de Datos una Factura "
						+ "con el mismo número y el mismo timbrado..";
			}

			if (((this.fac.getNumero().split("-").length != 3) || !this.ctr.m
					.containsOnlyNumbers(this.fac.getNumero()))) {
				out = false;
				this.mensajeError = mensajeError
						+ "\n - Mal formato del número de Factura..";
			}

			if ((this.fac.getNumero().length() == 0)
					|| (((String) this.fac.getTimbrado().getPos1()).isEmpty())
					|| (this.fac.getFechaOriginal() == null)) {
				out = false;
				this.mensajeError = mensajeError
						+ "\n - Los campos obligatorios no pueden quedar vacios..";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
		return "Error al Cancelar";
	}
	
}



// Validador de Insertar Item en Factura
class ValidadorInsertarItemFactura implements VerificaAceptarCancelar{

	private CompraLocalControlBody ctr;
	private CompraLocalFacturaDetalleDTO det;
	private String mensajeError;

	//Constructor
	public ValidadorInsertarItemFactura(CompraLocalControlBody ctr){
		this.ctr = ctr;
		this.det = ctr.getNvoItem();
	}
	

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.det.getCostoGs() <= 0.001) { 
			out = false;
			this.mensajeError = this.mensajeError + "\n - El Precio debe ser mayor a cero..";
		}			
		
		if (this.det.getCantidad() <= 0.001) {
			out = false;
			this.mensajeError = this.mensajeError + "\n - La cantidad debe ser mayor a cero..";
		}	
		
		if (this.det.getArticulo().esNuevo() == true) {
			out = false;
			this.mensajeError = this.mensajeError + "\n - Debe seleccionar un Artículo..";
		}
		
		if(this.isDuplicado()) {
			out = false;
			this.mensajeError += "\n - No se permite ítems duplicados..";
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
		return "Error al Cancelar";
	}
	
	/**
	 * @return true si ya esta el item en el detalle..
	 */
	private boolean isDuplicado() {
		String codNvo = (String) this.det.getArticulo().getPos1();
		
		for (CompraLocalFacturaDetalleDTO item : this.ctr.getSelectedFactura().getDetalles()) {
			String cod = (String) item.getArticulo().getPos1();
			if(codNvo.equals(cod))
				return true;
		}		
		return false;
	}
}


// Validador Insertar Resumen Gasto..
class ValidadorInsertarResumenGasto implements VerificaAceptarCancelar {
	
	private CompraLocalGastoDTO gasto;
	private String mensaje;	

	//Constructor
	public ValidadorInsertarResumenGasto(CompraLocalControlBody ctr){
		this.gasto = ctr.getNvoGasto();
	}
	

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensaje = "No se puede realizar la operación debido a: \n";
		
		if (this.gasto.getMontoGs() <= 0.001) {
			out = false;
			this.mensaje = this.mensaje + "\n - El monto debe ser mayor a cero..";
		}
		
		if (this.gasto.getGasto() == null) {
			out = false;
			this.mensaje += "\n - Debe asignar una factura de Gasto..";
		}
		
		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al Cancelar";
	}	
}



//Validador de Insertar Descuento..
class ValidadorInsertarDescuento implements VerificaAceptarCancelar{

	private CompraLocalFacturaDetalleDTO desc = new CompraLocalFacturaDetalleDTO();
	private String mensajeError = "";
	
	public CompraLocalFacturaDetalleDTO getDesc() {
		return desc;
	}

	public void setDesc(CompraLocalFacturaDetalleDTO desc) {
		this.desc = desc;
	}
	
	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}
	

	//Constructor
	public ValidadorInsertarDescuento(CompraLocalControlBody ctr){
		this.desc = ctr.getNvoItem();
	}
	
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if (this.desc.getTipoDescuento().esNuevo() == true) {
			out = false;
			this.mensajeError = this.mensajeError + "\n - Debe especificar el Tipo de Descuento..";
		}
		
		if (this.desc.getCostoGs() <= 0) {
			out = false;
			this.mensajeError = this.mensajeError + "\n - El valor del Descuento debe ser mayor a cero..";
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
		return "Error al Cancelar";
	}
	
}



//Validador de Enviar Correo..
class ValidadorEnviarCorreo implements VerificaAceptarCancelar{

	private String mensajeError = "";
	private CuerpoCorreo correo = new CuerpoCorreo("", "", "");
	private Misc m = new Misc();
	
	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}
	
	//Constructor
	public ValidadorEnviarCorreo(CompraLocalControlBody ctr){
		this.correo = ctr.getCorreo();
	}

	@Override
	public boolean verificarAceptar() {		
		boolean out = true;
		this.mensajeError = "No se puede realizar la operación debido a: \n";
		
		if ((correo.getDestinatario().trim().length() == 0) 
				&& (correo.getDestinatarioCopia().trim().length() == 0)
					&& (correo.getDestinatarioCopiaOculta().trim().length() == 0)) {
			this.mensajeError = mensajeError + "\n - Debe ingresar "
					+ "al menos una dirección de correo..";
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

		String[] correos = m.concatenarArraysDeString(arraySend, arraySendCC, arraySendCCO, null, null);
		boolean correosCheck = (boolean) m.chequearMultipleCorreos(correos)[0];
		String correosMsg = (String) m.chequearMultipleCorreos(correos)[1];
		
		if (correosCheck == false) {
			this.mensajeError = mensajeError + "Las siguientes direcciones de Correo son "
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
		return "Error al Cancelar";
	}	
}

/**
 * Reporte de Presupuesto..
 */
class ReportePresupuesto extends ReporteYhaguy {
	
	private CompraLocalOrdenDTO compra;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Código Proveedor", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Código Original", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Cantidad", TIPO_INTEGER, 30, false);
	
	public ReportePresupuesto(CompraLocalOrdenDTO compra) {
		this.compra = compra;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Presupuesto de Compra");
		this.setDirectorio("compras/locales");
		this.setNombreArchivo("Presupuesto-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.compra.getNumero();
		String origen = this.compra.getSucursal().getText();
		String proveedor = this.compra.getProveedor().getRazonSocial();
		String condicion = (String) this.compra.getCondicionPago().getPos1();
		String moneda = (String) this.compra.getMoneda().getPos1();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", numero)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Sucursal", origen)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Proveedor", proveedor)));
		
		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Condición", condicion)));
		
		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Moneda", moneda)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Orden de Compra..
 */
class ReporteOrdenCompra extends ReporteYhaguy {
	
	private CompraLocalOrdenDTO compra;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Código Proveedor", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Código Original", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Cantidad", TIPO_INTEGER, 30, false);
	static DatosColumnas col6 = new DatosColumnas("Precio", TIPO_DOUBLE, 30, false); 
	static DatosColumnas col7 = new DatosColumnas("Importe", TIPO_DOUBLE, 30, true); 
	
	public ReporteOrdenCompra(CompraLocalOrdenDTO compra) {
		this.compra = compra;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Orden de Compra");
		this.setDirectorio("compras/locales");
		this.setNombreArchivo("OrdenCompra-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.compra.getNumero();
		String origen = this.compra.getSucursal().getText();
		String proveedor = this.compra.getProveedor().getRazonSocial();
		String condicion = (String) this.compra.getCondicionPago().getPos1();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Número", numero))
				.add(this.textoParValor("Sucursal", origen)));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Proveedor", proveedor))
				.add(this.textoParValor("Condición", condicion)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Recepcion de Merc..
 */
class ReporteRecepcionMercaderia extends ReporteYhaguy {
	
	private CompraLocalOrdenDTO compra;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_STRING, 30);
	
	public ReporteRecepcionMercaderia(CompraLocalOrdenDTO compra) {
		this.compra = compra;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Recepcion de Mercadería - Orden de Compra Nro. " + this.compra.getNumero());
		this.setDirectorio("compras/locales");
		this.setNombreArchivo("Recepcion-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.compra.getNumero();
		String origen = this.compra.getSucursal().getText();
		String proveedor = this.compra.getProveedor().getRazonSocial();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", numero)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Sucursal", origen)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Proveedor", proveedor)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
