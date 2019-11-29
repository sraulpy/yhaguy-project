package com.yhaguy.gestion.empresa.ctacte.visor;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.GroupComparator;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteCtaCte;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.EmpresaCartera;
import com.yhaguy.domain.EmpresaObservacion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.HistoricoLineaCredito;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TareaProgramada;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class VisorCtaCteViewModel extends SimpleViewModel {
	
	static final String CLIENTE = "CLIENTE";
	static final String PROVEEDOR = "PROVEEDOR";
	
	static final String TODOS = "Todos";
	static final String PENDIENTES = "Pendientes";
	static final String VENCIDOS = "Vencidos";
	
	static final String CTA_GS = "Cta. Gs.";
	static final String CTA_DS = "Cta. USD.";
	
	private String ruc = "";
	private String cedula = "";
	private String razonSocial = "";
	private String nombreFantasia = "";
	
	private String concepto = "";
	private String numero = "";
	private String numeroImportacion = "";
	
	private Date desde;
	private Date hasta = new Date();
	private boolean verEstCta = false;
	private String selectedFilter = TODOS;
	private String selectedTipo = CLIENTE;
	private String selectedMoneda = CTA_GS;
	
	private MyArray selectedItem;
	private MyArray selectedItem_;
	private MyArray selectedAplicacion;
	private MyArray cliente;
	private DetalleMovimiento detalle = new DetalleMovimiento();
	private DetalleMovimiento detalle_ = new DetalleMovimiento();
	private DetalleGroupsModel groupModel;
	private DetalleGroupsModel groupModel_;
	
	private int sizeCheques = 0;
	private double totalCheques = 0;
	private boolean fraccionado = false;
	private boolean aplicaciones = false;
	
	private boolean habilitarLinea = false;
	private boolean updateLineaCredito = false;
	private double lineaTemporalGs = 0;
	private String motivo = "";
	private List<HistoricoLineaCredito> historicoLineaCredito;
	private List<EmpresaCartera> carteras;
	private EmpresaCartera selectedCartera;
	
	private Component view;
	private Window win;
	
	@Wire
	private Hlayout cab;
	
	@Wire
	private Listbox visCta;
	
	@Wire
	private Vlayout estCta;
	
	@Wire
	private Popup popDetalle;
	
	@Wire
	private Popup popDetalle_;
	
	@Wire
	private Popup popSaldos;
	
	@Wire
	private Popup popDetalleRecibo;
	
	@Wire
	private Popup popDetalleCheque;
	
	@Wire
	private Popup popMenu;
	
	@Wire
	private Listbox listAplicaciones;
	
	@Wire
	private Listbox listAplicaciones_;
	
	@Wire
	private Listbox listAplicacionesRec;
	
	@Wire
	private Window visorCtaCte;
	
	@Wire
	private Tab tabFac;
	
	@Wire
	private Tab tabRec;
	
	@Wire
	private Popup popCartera;
	
	@Init
	public void init() {
		try {
			this.desde = Utiles.getFecha("01-01-2016 00:00:00");
			groupModel = new DetalleGroupsModel(detalle.getAplicaciones(), new DetalleComparator());
			groupModel_ = new DetalleGroupsModel(detalle_.getAplicaciones(), new DetalleComparator());
			this.carteras = this.getCarteras_();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterCompose
	public void AfterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		this.view = view;
	}
	
	@Command
	public void imprimir() throws Exception {
		this.imprimir_(false);
	}
	
	@Command
	public void imprimirMobile() throws Exception {
		this.imprimir_(true);
	}
	
	@Command
	public void imprimirDHS() throws Exception {
		if (this.selectedTipo.equals(CLIENTE)) {
			this.historialMovimientosClientes();
		} else {
			RegisterDomain rr = RegisterDomain.getInstance();
		    Proveedor proveedor = rr.getProveedorByEmpresa(this.selectedItem.getId());
		    if (proveedor != null) {
				if (proveedor.isProveedorExterior()) {
					this.historialMovimientosProveedoresExterior(proveedor);
				} else {
					this.historialMovimientosProveedores(proveedor);
				}
			}
		}
	}
	
	@Command
	public void imprimirColumnas() throws Exception {
		if (this.selectedTipo.equals(CLIENTE)) {
			this.imprimirFormatoColumnas();
		}
	}
	
	@Command
	@NotifyChange("movimientos")
	public void obtenerValores() throws Exception {
		this.cab.setVisible(false);
		this.visCta.setVisible(false);
		this.estCta.setVisible(true);
		this.setVerEstCta(true);
	}
	
	@Command
	@NotifyChange("movimientos")
	public void obtenerValoresMobile() throws Exception {
		this.setVerEstCta(true);
		this.selectedFilter = PENDIENTES;
	}
	
	@Command
	@NotifyChange("*")
	public void volver() throws Exception {
		this.selectedItem = null;
		this.estCta.setVisible(false);
		this.visCta.setVisible(true);
		this.cab.setVisible(true);
		this.setVerEstCta(false);
		this.desde = Utiles.getFecha("01-01-2016 00:00:00");
		this.hasta = new Date();
		this.concepto = "";
		this.numero = "";
		this.numeroImportacion = "";
		this.selectedFilter = TODOS;
	}
	
	@Command
	@NotifyChange("selectedItem")
	public void bloquearCuenta(@BindingParam("bloquear") boolean bloquear)
			throws Exception {
		if (bloquear) {
			this.win = (Window) Executions.createComponents(
					"/yhaguy/gestion/empresa/MotivoBloqueo.zul", this.view,
					null);
			this.win.doOverlapped();

		} else {
			this.bloquearCuenta(this.selectedItem.getId(), bloquear,
					"Cuenta Desbloqueada en fecha " + new Date() + " por "
							+ this.getLoginNombre(), false);
			this.selectedItem.setPos5(bloquear);
		}
	}
	
	@Command
	@NotifyChange("selectedItem")
	public void aceptarBloqueo(@BindingParam("motivo") String motivo)
			throws Exception {
		if (motivo.isEmpty()) {
			this.mensajePopupTemporalWarning("Debe ingresar el motivo de bloqueo..", 5000);
			return;
		}
		this.bloquearCuenta(this.selectedItem.getId(), true, motivo, false);
		this.selectedItem.setPos5(true);
		this.win.detach();
	}
	
	@Command
	@NotifyChange({ "selectedItem", "cliente" })
	public void desbloqueoTemporal() throws Exception {
		this.bloquearCuenta(this.selectedItem.getId(), false,
				"Cuenta Desbloqueada en fecha " + new Date() + " por "
						+ this.getLoginNombre(), true);
		this.habilitarCredito();
		this.selectedItem.setPos5(false);
	}
	
	@Command
	public void cancelarBloqueo() {
		this.win.detach();
	}
	
	@Command
	@NotifyChange("selectedItem_")
	public void setSelected(@BindingParam("item") MyArray item) {
		this.selectedItem_ = item;
	}
	
	@Command
	public void updateCtaCte(@BindingParam("comp") Popup comp) throws Exception {
		double saldo = (double) this.selectedItem_.getPos6();
		String observacion = (String) this.selectedItem_.getPos11();
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteEmpresaMovimientoById(this.selectedItem_.getId());
		ctacte.setSaldo(saldo);
		ctacte.setObservacion(observacion.toUpperCase());
		rr.saveObject(ctacte, this.getLoginNombre());
		Clients.showNotification("REGISTRO ACTUALIZADO..");
		comp.close();
	}
	
	@Command
	@NotifyChange({ "groupModel", "detalle", "groupModel_" })
	public void verItems(@BindingParam("item") MyArray item,
			@BindingParam("parent") Component parent) throws Exception {
		this.tabFac.setSelected(true);
		this.tabRec.setSelected(true);
		this.groupModel = new DetalleGroupsModel(new ArrayList<DetalleMovimiento>(), new DetalleComparator());
		this.groupModel_ = new DetalleGroupsModel(new ArrayList<DetalleMovimiento>(), new DetalleComparator());
		this.selectedItem_ = item;
		this.detalle = new DetalleMovimiento();
		this.detalle.setEmision((Date) item.getPos1());
		this.detalle.setVencimiento((Date) item.getPos2());
		this.detalle.setNumero(String.valueOf(item.getPos4()));
		this.detalle.setSigla(String.valueOf(item.getPos8()));
		this.detalle.setTipoMovimiento(String.valueOf(item.getPos3()));
		if (this.isAnticipoCobro(String.valueOf(item.getPos8()))) {
			this.detalle.setImporteGs((double) item.getPos5());
		}
		this.setDetalles(item);
		if (this.isRecibo(String.valueOf(item.getPos8()))) {
			this.popDetalleRecibo.open(parent, "start_before");
		} else if (this.isChequeRechazado(String.valueOf(item.getPos8()))) {
			this.popDetalleCheque.open(parent, "start_before");
		} else {
			this.popDetalle.open(parent, "start_before");
		}
	}
	
	@Command
	public void verAplicaciones() throws Exception {
		Clients.showBusy(this.listAplicaciones, "Buscando Aplicaciones...");
		Events.echoEvent("onLater", this.listAplicaciones, null);
	}
	
	@Command
	public void verAplicaciones_(@BindingParam("parent") Component parent) throws Exception {
		this.popDetalle_.open(parent, "start_before");
		Clients.showBusy(this.listAplicaciones_, "Buscando Aplicaciones...");
		Events.echoEvent("onLater", this.listAplicaciones_, null);
	}
	
	@Command
	public void verAplicacionesRecibo() throws Exception {
		Clients.showBusy(this.listAplicacionesRec, "Buscando Aplicaciones...");
		Events.echoEvent("onLater", this.listAplicacionesRec, null);
	}
	
	/**
	 * Busca las aplicaciones..
	 */
	private void buscarAplicaciones() throws Exception {
		this.detalle.setAplicaciones(this.getAplicaciones(this.selectedItem_, this.detalle));
		BindUtils.postNotifyChange(null, null, this, "detalle");
		BindUtils.postNotifyChange(null, null, this, "groupModel");
	}
	
	/**
	 * Busca las aplicaciones..
	 */
	private void buscarAplicaciones_() throws Exception {
		this.detalle_.setAplicaciones(this.getAplicaciones_());
		BindUtils.postNotifyChange(null, null, this, "detalle_");
		BindUtils.postNotifyChange(null, null, this, "groupModel_");
	}
	
	@Command
	@NotifyChange("cliente")
	public void verMenu(@BindingParam("parent") Component parent) {
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			Cliente cli = rr.getClienteByIdEmpresa(this.selectedItem.getId());
			this.cliente = this.clienteToMyArray(cli);
			this.popMenu.open(parent, "after_end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	public void notificarCambioLineaCredito(@BindingParam("comp") Component comp) {
		this.updateLineaCredito = true;
		comp.setVisible(true);
	}
	
	@Command
	public void habilitarLineaTemporal(@BindingParam("comp1") Doublebox comp1, @BindingParam("comp2") Component comp2) {
		this.habilitarLinea = true;
		comp1.setReadonly(false);
		comp2.setVisible(true);
	}
	
	@Command
	@NotifyChange("historicoLineaCredito")
	public void verHistoricoLineaCredito(@BindingParam("popup") Popup popup, @BindingParam("parent") Component parent) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Cliente cli = rr.getClienteByIdEmpresa(this.selectedItem.getId());
		this.historicoLineaCredito = rr.getHistoricoLineaCredito(cli.getId());
		popup.open(parent, "start_before");
	}
	
	@Command
	@NotifyChange("cliente")
	public void actualizarDatos() {
		if ((this.habilitarLinea || this.updateLineaCredito) && this.motivo.trim().isEmpty()) {
			Clients.showNotification("ERROR: DEBE INGRESAR EL MOTIVO..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			Cliente cli = rr.getClienteByIdEmpresa(this.selectedItem.getId());
			cli.setLimiteCredito((double) this.cliente.getPos1());
			cli.setVentaCredito((boolean) this.cliente.getPos2());
			cli.setCobrador((Funcionario) this.cliente.getPos3());
			cli.setVentaExenta((boolean) this.cliente.getPos4());
			rr.saveObject(cli, this.getLoginNombre());
			Empresa emp = rr.getEmpresaById(this.selectedItem.getId());
			emp.setCartera((EmpresaCartera) this.selectedItem.getPos13());
			rr.saveObject(emp, this.getLoginNombre());
			if (this.habilitarLinea) {
				HistoricoLineaCredito hist = new HistoricoLineaCredito();
				hist.setActivo(true);
				hist.setFecha(new Date());
				hist.setIdCliente(cli.getId());
				hist.setImporteGs(this.lineaTemporalGs);
				hist.setMotivo(this.motivo.toUpperCase());
				hist.setTemporal(true);
				rr.saveObject(hist, this.getLoginNombre());
				this.habilitarLinea = false;
			}
			if (this.updateLineaCredito) {
				HistoricoLineaCredito hist = new HistoricoLineaCredito();
				hist.setActivo(true);
				hist.setFecha(new Date());
				hist.setIdCliente(cli.getId());
				hist.setImporteGs((double) this.cliente.getPos1());
				hist.setMotivo(this.motivo.toUpperCase());
				hist.setTemporal(false);
				rr.saveObject(hist, this.getLoginNombre());
				this.updateLineaCredito = false;
			}
			Clients.showNotification("Datos actualizados..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	@NotifyChange("selectedTipo")
	public void selectTipo(@BindingParam("tipo") int tipo) {
		if (tipo == 1) {
			this.selectedTipo = CLIENTE;
		} else {
			this.selectedTipo = PROVEEDOR;
		}
	}
	
	@Command
	@NotifyChange("selectedFilter")
	public void selectFilter(@BindingParam("filter") int filter) {
		if (filter == 1) {
			this.selectedFilter = TODOS;
		} else if (filter == 2) {
			this.selectedFilter = PENDIENTES;
		} else {
			this.selectedFilter = VENCIDOS;
		}
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void clearProgress() throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(listAplicaciones);
			}
		});
		timer.setParent(this.visorCtaCte);		
		this.buscarAplicaciones();
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void clearProgress_() throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(listAplicaciones_);
			}
		});
		timer.setParent(this.visorCtaCte);		
		this.buscarAplicaciones_();
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void clearProgressRec() throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(listAplicacionesRec);
			}
		});
		timer.setParent(this.visorCtaCte);		
		this.buscarAplicaciones();
	}
	
	@Command
	public void saveObservacion(@BindingParam("comp1") Textbox comp1, @BindingParam("comp2") Button comp2)
			throws Exception {
		this.guardarObservacion();
		comp1.setReadonly(true);
		comp2.setDisabled(true);
	}
	
	@Command
	@NotifyChange("selectedItem_")
	public void verItems_(@BindingParam("item") MyArray item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedItem_ = item;
		this.popSaldos.open(parent, "start_before");
	}
	
	@Command
	@NotifyChange("movimientos")
	public void aplicacionDeSaldos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		double saldo1 = (double) this.selectedItem_.getPos6();
		if (saldo1 < 0) saldo1 = saldo1 * -1;
		double saldo2 = (double) this.selectedAplicacion.getPos6(); 
		double importe = (saldo1 - saldo2) > 0 ? saldo2 : saldo1;
		
		CtaCteEmpresaMovimiento deb = rr.getCtaCteEmpresaMovimientoById(this.selectedItem_.getId().longValue());
		CtaCteEmpresaMovimiento cre = rr.getCtaCteEmpresaMovimientoById(this.selectedAplicacion.getId().longValue());
		
		AjusteCtaCte ajuste = new AjusteCtaCte();
		if (deb.isAnticipoCobro()) {
			ajuste.setAuxi(AjusteCtaCte.ANTICIPOS);
			ajuste.setOrden(deb.getNroComprobante());
			ajuste.setIp_pc(deb.getEmpresa().getRuc());
		}
		ajuste.setDescripcion(deb.getEmpresa().getRazonSocial());
		ajuste.setFecha(new Date());
		ajuste.setDebito(deb);
		ajuste.setCredito(cre);
		ajuste.setImporte(importe);
		rr.saveObject(ajuste, this.getLoginNombre());
		
		ControlCuentaCorriente.recalcularSaldoCtaCte(deb);
		ControlCuentaCorriente.recalcularSaldoCtaCte(cre);
		
		Clients.showNotification("SALDO APLICADO..");
		
		this.popSaldos.close();	
	}
	
	@Command
	@NotifyChange("movimientos")
	public void depurarSaldos(@BindingParam("item") MyArray item) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String sigla = (String) item.getPos8();
		if (this.isVenta(sigla)) {
			CtaCteEmpresaMovimiento ctacte = rr.getCtaCteEmpresaMovimientoById(item.getId().longValue());
			ControlCuentaCorriente.recalcularSaldoCtaCte(ctacte);
		}	
		Clients.showNotification("SALDOS ACTUALIZADOS..");
	}
	
	@Command
	@NotifyChange("movimientos")
	public void reasignarSaldo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteEmpresaMovimientoById(this.selectedItem_.getId().longValue());
		ctacte.setSaldo(ctacte.getImporteOriginal());
		rr.saveObject(ctacte, this.getLoginNombre());
		Clients.showNotification("SALDO REASIGNADO..");
	}
	
	@Command
	@NotifyChange("selectedCartera")
	public void openCartera(@BindingParam("parent") Component parent) {
		this.selectedCartera = null;
		this.popCartera.open(parent, "after_end");
	}
	
	@Command
	@NotifyChange("selectedItem_")
	public void saveCartera() throws Exception {
		this.selectedItem_.setPos12(this.selectedCartera);
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteEmpresaMovimientoById(this.selectedItem_.getId().longValue());
		ctacte.setCarteraCliente(this.selectedCartera);
		rr.saveObject(ctacte, this.getLoginNombre());
		Clients.showNotification("CARTERA ASIGNADA..");
		this.popCartera.close();
	}
	
	/**
	 * @return el cliente convertido a myarray..
	 */
	private MyArray clienteToMyArray(Cliente cliente) {
		MyArray out = new MyArray();
		out.setId(cliente.getId());
		out.setPos1(cliente.getLimiteCredito());
		out.setPos2(cliente.isVentaCredito());
		out.setPos3(cliente.getCobrador());
		out.setPos4(cliente.isVentaExenta());
		return out;
	}
	
	/**
	 * @return los items del movimiento..
	 */
	private void setDetalles(MyArray item) throws Exception {
		List<MyArray> detalles = new ArrayList<MyArray>();
		List<MyArray> formasPago = new ArrayList<MyArray>();
		String sigla = (String) item.getPos8();
		long idmovimiento = (long) item.getPos9();
		RegisterDomain rr = RegisterDomain.getInstance();
		
		//ventas..
		if (this.isVenta(sigla)) {
			Venta vta = (Venta) rr.getObject(Venta.class.getName(), idmovimiento);
			if (vta != null) {
				this.detalle.setNumeroPlanilla(vta.getNumeroPlanillaCaja());
				for (VentaDetalle det : vta.getDetalles()) {
					detalles.add(new MyArray(det.getArticulo().getCodigoInterno(), det
							.getArticulo().getDescripcion(), det.getCantidad(), det
							.getPrecioGs(), (det.getPrecioGs() * det.getCantidad())));
				}
			}
			
		//notas de credito..	
		} else if (this.isNotaCredito(sigla)) {
			NotaCredito ncr = (NotaCredito) rr.getObject(NotaCredito.class.getName(), idmovimiento);
			this.detalle.setNumeroPlanilla(ncr.getPlanillaCajaNro());
			this.detalle.setTipoMovimiento(ncr.getTipoMovimiento().getDescripcion() + " - " + ncr.getMotivo().getDescripcion());
			this.detalle.setMotivo(ncr.getMotivo().getDescripcion().toUpperCase());
			for (NotaCreditoDetalle det : ncr.getDetalles()) {
				if (det.getArticulo() != null) {
					detalles.add(new MyArray(det.getArticulo().getCodigoInterno(), det
							.getArticulo().getDescripcion(), det.getCantidad(), det
							.getMontoGs(), (det.getMontoGs() * det.getCantidad())));
				} else if(det.getVenta() != null) {
					this.detalle.setFacturaAplicada(det.getVenta().getNumero());
				}
			}
			
		//recibos..	
		} else if (this.isRecibo(sigla)) {
			Recibo rec = (Recibo) rr.getObject(Recibo.class.getName(),
					idmovimiento);
			this.detalle.setNumeroPlanilla(rec.getNumeroPlanilla());
			for (ReciboDetalle det : rec.getDetalles()) {
				detalles.add(new MyArray(this.m.dateToString(det.getMovimiento()
						.getFechaEmision(), Misc.DD_MM_YYYY), this.m
						.dateToString(
								det.getMovimiento().getFechaVencimiento(),
								Misc.DD_MM_YYYY), det.getMovimiento()
						.getNroComprobante(), det.getMontoGs(), det
						.getMontoGs()));
			}
			for (ReciboFormaPago fp : rec.getFormasPago()) {
				formasPago.add(new MyArray(fp.getDescripcion(), fp.getMontoGs()));
			}
		}  else if (this.isChequeRechazado(sigla)) {
			BancoChequeTercero che = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), idmovimiento);
			if (che != null) {
				this.detalle.setDescripcion(che.getBanco().getDescripcion());
				this.detalle.setLibrador(che.getLibrado());
				this.detalle.setMotivo(che.getObservacion());
				this.detalle.setFechaRechazo(che.getFechaRechazo());
			}
		}		
		this.detalle.setDetalles(detalles);
		this.detalle.setFormasPago(formasPago);
	}
	
	/**
	 * @return las aplicaciones del movimiento..
	 */
	private List<DetalleMovimiento> getAplicaciones(MyArray item, DetalleMovimiento movim) throws Exception {
		List<DetalleMovimiento> out = new ArrayList<DetalleMovimiento>();
		String sigla = (String) item.getPos8();
		long idmovimiento = (long) item.getPos9();
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta vta = null;
		movim.setSelf(true);
		
		// notas de credito
		if (this.isNotaCredito(sigla)) {
			NotaCredito nc = (NotaCredito) rr.getObject(NotaCredito.class.getName(), idmovimiento);
			vta = nc.getVentaAplicada();
			movim.setNumero(nc.getNumero());
			movim.setImporteGs(nc.getImporteGs());
			movim.setTipoMovimiento(nc.getTipoMovimiento().getDescripcion() + " - " + 
						nc.getMotivo().getDescripcion().toUpperCase());
			movim.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
			
			DetalleMovimiento det = new DetalleMovimiento();
			det.setEmision(vta.getFecha());
			det.setNumero(vta.getNumero());
			det.setSigla(vta.getTipoMovimiento().getSigla());
			det.setTipoMovimiento(vta.getTipoMovimiento().getDescripcion());
			det.setImporteGs(vta.getTotalImporteGs());
			det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
			det.setIdMovimiento(vta.getId());
			out.add(det);
			
			List<AjusteCtaCte> ajustes = rr.getAjustesCredito(nc.getId(), nc.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes) {
				DetalleMovimiento det_ = new DetalleMovimiento();
				det_.setEmision(ajuste.getFecha());
				det_.setNumero(ajuste.getDebito().getNroComprobante());
				det_.setSigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
				det_.setTipoMovimiento("CREDITO CTA.CTE.");
				det_.setImporteGs(ajuste.getImporte());
				det_.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det_.setIdMovimiento(ajuste.getId());
				out.add(det_);			
			}
			
			List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(nc.getId(), nc.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes_) {
				DetalleMovimiento det_ = new DetalleMovimiento();
				det_.setEmision(ajuste.getFecha());
				det_.setNumero(ajuste.getCredito().getNroComprobante());
				det_.setSigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
				det_.setTipoMovimiento("DEBITO CTA.CTE.");
				det_.setImporteGs(ajuste.getImporte());
				det_.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det_.setIdMovimiento(ajuste.getCredito().getIdMovimientoOriginal());
				det_.setSigla_(ajuste.getCredito().getTipoMovimiento().getSigla());
				out.add(det_);			
			}
		}
		
		// ventas
		if (this.isVenta(sigla)) {
			vta = (Venta) rr.getObject(Venta.class.getName(), idmovimiento);
			movim.setNumero(vta.getNumero());
			movim.setImporteGs(vta.getTotalImporteGs());
			movim.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
			movim.setIdMovimiento(vta.getId());
		}

		if (vta != null) {
			List<NotaCredito> ncs = rr.getNotaCreditosByVenta(vta.getId());
			for (NotaCredito nc : ncs) {
				if (!nc.isAnulado()) {
					DetalleMovimiento det = new DetalleMovimiento();
					det.setEmision(nc.getFechaEmision());
					det.setNumero(nc.getNumero());
					det.setSigla(nc.getTipoMovimiento().getSigla());
					det.setTipoMovimiento(nc.getTipoMovimiento().getDescripcion() + " - " + 
							nc.getMotivo().getDescripcion().toUpperCase());
					det.setTipoMovimiento_(nc.getTipoMovimiento().getDescripcion());
					det.setImporteGs(nc.getImporteGs());
					det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
					det.setIdMovimiento(nc.getId());
					if ((this.isNotaCredito(sigla) && !nc.getNumero().equals(movim.getNumero()))
							|| !this.isNotaCredito(sigla)) {
						out.add(det);
					}
				}				
			}
			List<Object[]> recs = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
			for (Object[] rec : recs) {
				Recibo recibo = (Recibo) rec[0];
				ReciboDetalle rdet = (ReciboDetalle) rec[1];
				DetalleMovimiento det = new DetalleMovimiento();
				det.setEmision(recibo.getFechaEmision());
				det.setNumero(recibo.getNumero());
				det.setSigla(recibo.getTipoMovimiento().getSigla());
				det.setTipoMovimiento(recibo.getTipoMovimiento().getDescripcion());
				det.setTipoMovimiento_(recibo.getTipoMovimiento().getDescripcion());
				det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det.setImporteGs(rdet.getMontoGs());
				det.setIdMovimiento(recibo.getId());
				out.add(det);
			}
			
			List<AjusteCtaCte> ajustes = rr.getAjustesCredito(vta.getId(), vta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes) {
				DetalleMovimiento det = new DetalleMovimiento();
				det.setEmision(ajuste.getFecha());
				det.setNumero(ajuste.getDebito().getNroComprobante());
				det.setSigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
				det.setTipoMovimiento("CREDITO CTA.CTE.");
				det.setTipoMovimiento_(det.getTipoMovimiento());
				det.setImporteGs(ajuste.getImporte());
				det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det.setIdMovimiento(ajuste.getDebito().getIdMovimientoOriginal());
				det.setSigla_(ajuste.getDebito().getTipoMovimiento().getSigla());
				out.add(det);			
			}
			
			List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(vta.getId(), vta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes_) {
				DetalleMovimiento det = new DetalleMovimiento();
				det.setEmision(ajuste.getFecha());
				det.setNumero(ajuste.getCredito().getNroComprobante());
				det.setSigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
				det.setTipoMovimiento("DEBITO CTA.CTE.");
				det.setTipoMovimiento_(det.getTipoMovimiento());
				det.setImporteGs(ajuste.getImporte());
				det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det.setIdMovimiento(ajuste.getCredito().getIdMovimientoOriginal());
				det.setSigla_(ajuste.getCredito().getTipoMovimiento().getSigla());
				out.add(det);			
			}
		}	
		
		if (this.isReciboCobro(sigla)) {
			Recibo rec = (Recibo) rr.getObject(Recibo.class.getName(), idmovimiento);
			Map<String, String> vtas = new HashMap<String, String>();
			for (ReciboDetalle rdet : rec.getDetalles()) {
				Venta venta = rdet.getVenta();
				if (venta != null) {
					String data = vtas.get(venta.getNumero());
					if (data == null) {
						vtas.put(venta.getNumero(), venta.getNumero());
						DetalleMovimiento vdet = new DetalleMovimiento();
						vdet.setEmision(venta.getFecha());
						vdet.setNumero(venta.getNumero());
						vdet.setSigla(venta.getTipoMovimiento().getSigla());
						vdet.setTipoMovimiento(venta.getTipoMovimiento().getDescripcion());
						vdet.setImporteGs(venta.getTotalImporteGs());
						vdet.setAgrupador(venta.getNumero());
						vdet.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
						vdet.setIdMovimiento(venta.getId());
						out.add(vdet);
						
						List<NotaCredito> ncs = rr.getNotaCreditosByVenta(venta.getId());
						for (NotaCredito nc : ncs) {
							DetalleMovimiento det = new DetalleMovimiento();
							det.setEmision(nc.getFechaEmision());
							det.setNumero(nc.getNumero());
							det.setSigla(nc.getTipoMovimiento().getSigla());
							det.setTipoMovimiento(nc.getTipoMovimiento().getDescripcion() + " - " + 
									nc.getMotivo().getDescripcion().toUpperCase());
							det.setImporteGs(nc.getImporteGs());
							det.setAgrupador(venta.getNumero());
							det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
							det.setIdMovimiento(nc.getId());
							if ((this.isNotaCredito(sigla) && !nc.getNumero().equals(movim.getNumero()))
									|| !this.isNotaCredito(sigla)) {
								out.add(det);
							}				
						}
						List<Object[]> recs = rr.getRecibosByVenta(venta.getId(), venta.getTipoMovimiento().getId());
						for (Object[] rec_ : recs) {
							Recibo recibo = (Recibo) rec_[0];
							ReciboDetalle rdet_ = (ReciboDetalle) rec_[1];
							DetalleMovimiento det = new DetalleMovimiento();
							det.setEmision(recibo.getFechaEmision());
							det.setNumero(recibo.getNumero());
							det.setSigla(recibo.getTipoMovimiento().getSigla());
							det.setTipoMovimiento(recibo.getTipoMovimiento().getDescripcion());
							det.setImporteGs(rdet_.getMontoGs());
							det.setAgrupador(venta.getNumero());
							det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
							det.setIdMovimiento(recibo.getId());
							if (recibo.getNumero().equals(movim.getNumero())) {
								det.setSelf(true);
							}
							out.add(det);
						}
						List<AjusteCtaCte> ajustes = rr.getAjustesCredito(vta.getId(), vta.getTipoMovimiento().getId());
						for (AjusteCtaCte ajuste : ajustes) {
							DetalleMovimiento det = new DetalleMovimiento();
							det.setEmision(ajuste.getFecha());
							det.setNumero(ajuste.getDebito().getNroComprobante());
							det.setSigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
							det.setTipoMovimiento("CREDITO CTA.CTE.");
							det.setTipoMovimiento_(det.getTipoMovimiento());
							det.setImporteGs(ajuste.getImporte());
							det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
							det.setIdMovimiento(ajuste.getId());
							out.add(det);			
						}
						
						List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(vta.getId(), vta.getTipoMovimiento().getId());
						for (AjusteCtaCte ajuste : ajustes_) {
							DetalleMovimiento det = new DetalleMovimiento();
							det.setEmision(ajuste.getFecha());
							det.setNumero(ajuste.getCredito().getNroComprobante());
							det.setSigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
							det.setTipoMovimiento("DEBITO CTA.CTE.");
							det.setTipoMovimiento_(det.getTipoMovimiento());
							det.setImporteGs(ajuste.getImporte());
							det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
							det.setIdMovimiento(ajuste.getCredito().getIdMovimientoOriginal());
							out.add(det);			
						}
					}					
				}
			}
		}
		
		if (this.isAnticipoCobro(sigla)) {
			Recibo rec = (Recibo) rr.getObject(Recibo.class.getName(), idmovimiento);
			
			List<AjusteCtaCte> ajustes = rr.getAjustesCredito(rec.getId(), rec.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes) {
				DetalleMovimiento det = new DetalleMovimiento();
				det.setEmision(ajuste.getFecha());
				det.setNumero(ajuste.getDebito().getNroComprobante());
				det.setSigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
				det.setTipoMovimiento("CREDITO CTA.CTE.");
				det.setTipoMovimiento_(det.getTipoMovimiento());
				det.setImporteGs(ajuste.getImporte());
				det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det.setIdMovimiento(ajuste.getId());
				out.add(det);			
			}
			
			List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(rec.getId(), rec.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes_) {
				DetalleMovimiento det = new DetalleMovimiento();
				det.setEmision(ajuste.getFecha());
				det.setNumero(ajuste.getCredito().getNroComprobante());
				det.setSigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
				det.setTipoMovimiento("DEBITO CTA.CTE.");
				det.setTipoMovimiento_(det.getTipoMovimiento());
				det.setImporteGs(ajuste.getImporte());
				det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det.setIdMovimiento(ajuste.getCredito().getIdMovimientoOriginal());
				out.add(det);			
			}
		}
		
		if (!this.isReciboCobro(sigla) && !this.isAnticipoCobro(sigla)) {
			out.add(movim);
			// ordena la lista segun fecha..
			Collections.sort(out, new Comparator<DetalleMovimiento>() {
				@Override
				public int compare(DetalleMovimiento o1, DetalleMovimiento o2) {
					Date fecha1 = o1.getEmision();
					Date fecha2 = o2.getEmision();
					if (fecha1 != null && fecha2 != null) {
						return fecha1.compareTo(fecha2);
					}
					return 0;
				}
			});
		}	
		
		if (this.isAnticipoCobro(sigla)) {
			out.add(movim);
			// ordena la lista segun anticipo, luego fecha..
			Collections.sort(out, new Comparator<DetalleMovimiento>() {
				@Override
				public int compare(DetalleMovimiento o1, DetalleMovimiento o2) {
					if (o1.isSelf()) {
						return -1;
					}
					Date fecha1 = o1.getEmision();
					Date fecha2 = o2.getEmision();
					if (fecha1 != null && fecha2 != null) {
						return fecha1.compareTo(fecha2);
					}
					return 0;
				}
			});
		}
		
		double saldo = 0;
		for (DetalleMovimiento det : out) {
			saldo += det.getDebe() - det.getHaber();
			det.setSaldo(saldo);
		}		
		return out;
	}
	
	/**
	 * @return las aplicaciones del movimiento..
	 */
	private List<DetalleMovimiento> getAplicaciones_() throws Exception {
		
		DetalleMovimiento movim = new DetalleMovimiento();
		movim.setEmision(this.detalle_.getEmision());
		movim.setVencimiento(this.detalle_.getVencimiento());
		movim.setNumero(this.detalle_.getNumero());
		movim.setSigla(this.detalle_.getSigla());
		movim.setTipoMovimiento(this.detalle_.getTipoMovimiento());
		
		List<DetalleMovimiento> out = new ArrayList<DetalleMovimiento>();
		String sigla = this.detalle_.getSigla_();
		long idmovimiento = this.detalle_.getIdMovimiento();
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta vta = null;
		movim.setSelf(true);
		
		// notas de credito
		if (this.isNotaCredito(sigla)) {
			NotaCredito nc = (NotaCredito) rr.getObject(NotaCredito.class.getName(), idmovimiento);
			vta = nc.getVentaAplicada();
			movim.setNumero(nc.getNumero());
			movim.setImporteGs(nc.getImporteGs());
			movim.setTipoMovimiento(nc.getTipoMovimiento().getDescripcion() + " - " + 
						nc.getMotivo().getDescripcion().toUpperCase());
			movim.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
			
			DetalleMovimiento det = new DetalleMovimiento();
			det.setEmision(vta.getFecha());
			det.setNumero(vta.getNumero());
			det.setSigla(vta.getTipoMovimiento().getSigla());
			det.setTipoMovimiento(vta.getTipoMovimiento().getDescripcion());
			det.setImporteGs(vta.getTotalImporteGs());
			det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
			out.add(det);
		}
		
		// ventas
		if (this.isVenta(sigla)) {
			vta = (Venta) rr.getObject(Venta.class.getName(), idmovimiento);
			movim.setNumero(vta.getNumero());
			movim.setImporteGs(vta.getTotalImporteGs());
			movim.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
			
			DetalleMovimiento det = new DetalleMovimiento();
			det.setEmision(vta.getFecha());
			det.setNumero(vta.getNumero());
			det.setSigla(vta.getTipoMovimiento().getSigla());
			det.setTipoMovimiento(vta.getTipoMovimiento().getDescripcion());
			det.setImporteGs(vta.getTotalImporteGs());
			det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
			out.add(det);
		}

		if (vta != null) {
			List<NotaCredito> ncs = rr.getNotaCreditosByVenta(vta.getId());
			for (NotaCredito nc : ncs) {
				if (!nc.isAnulado()) {
					DetalleMovimiento det = new DetalleMovimiento();
					det.setEmision(nc.getFechaEmision());
					det.setNumero(nc.getNumero());
					det.setSigla(nc.getTipoMovimiento().getSigla());
					det.setTipoMovimiento(nc.getTipoMovimiento().getDescripcion() + " - " + 
							nc.getMotivo().getDescripcion().toUpperCase());
					det.setImporteGs(nc.getImporteGs());
					det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
					if ((this.isNotaCredito(sigla) && !nc.getNumero().equals(movim.getNumero()))
							|| !this.isNotaCredito(sigla)) {
						out.add(det);
					}
				}				
			}
			List<Object[]> recs = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
			for (Object[] rec : recs) {
				Recibo recibo = (Recibo) rec[0];
				ReciboDetalle rdet = (ReciboDetalle) rec[1];
				DetalleMovimiento det = new DetalleMovimiento();
				det.setEmision(recibo.getFechaEmision());
				det.setNumero(recibo.getNumero());
				det.setSigla(recibo.getTipoMovimiento().getSigla());
				det.setTipoMovimiento(recibo.getTipoMovimiento().getDescripcion());
				det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det.setImporteGs(rdet.getMontoGs());
				out.add(det);
			}
			
			List<AjusteCtaCte> ajustes = rr.getAjustesCredito(vta.getId(), vta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes) {
				DetalleMovimiento det = new DetalleMovimiento();
				det.setEmision(ajuste.getFecha());
				det.setNumero(ajuste.getDebito().getNroComprobante());
				det.setSigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
				det.setTipoMovimiento("CREDITO CTA.CTE.");
				det.setImporteGs(ajuste.getImporte());
				det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det.setIdMovimiento(ajuste.getDebito().getIdMovimientoOriginal());
				det.setSigla_(ajuste.getDebito().getTipoMovimiento().getSigla());
				out.add(det);			
			}
			
			List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(vta.getId(), vta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes_) {
				DetalleMovimiento det = new DetalleMovimiento();
				det.setEmision(ajuste.getFecha());
				det.setNumero(ajuste.getCredito().getNroComprobante());
				det.setSigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
				det.setTipoMovimiento("DEBITO CTA.CTE.");
				det.setImporteGs(ajuste.getImporte());
				det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
				det.setIdMovimiento(ajuste.getCredito().getIdMovimientoOriginal());
				det.setSigla_(ajuste.getCredito().getTipoMovimiento().getSigla());
				out.add(det);			
			}
		}	
		
		if (this.isReciboCobro(sigla)) {
			Recibo rec = (Recibo) rr.getObject(Recibo.class.getName(), idmovimiento);
			Map<String, String> vtas = new HashMap<String, String>();
			for (ReciboDetalle rdet : rec.getDetalles()) {
				Venta venta = rdet.getVenta();
				if (venta != null) {
					String data = vtas.get(venta.getNumero());
					if (data == null) {
						vtas.put(venta.getNumero(), venta.getNumero());
						DetalleMovimiento vdet = new DetalleMovimiento();
						vdet.setEmision(venta.getFecha());
						vdet.setNumero(venta.getNumero());
						vdet.setSigla(venta.getTipoMovimiento().getSigla());
						vdet.setTipoMovimiento(venta.getTipoMovimiento().getDescripcion());
						vdet.setImporteGs(venta.getTotalImporteGs());
						vdet.setAgrupador(venta.getNumero());
						vdet.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
						out.add(vdet);
						
						List<NotaCredito> ncs = rr.getNotaCreditosByVenta(venta.getId());
						for (NotaCredito nc : ncs) {
							DetalleMovimiento det = new DetalleMovimiento();
							det.setEmision(nc.getFechaEmision());
							det.setNumero(nc.getNumero());
							det.setSigla(nc.getTipoMovimiento().getSigla());
							det.setTipoMovimiento(nc.getTipoMovimiento().getDescripcion() + " - " + 
									nc.getMotivo().getDescripcion().toUpperCase());
							det.setImporteGs(nc.getImporteGs());
							det.setAgrupador(venta.getNumero());
							det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
							if ((this.isNotaCredito(sigla) && !nc.getNumero().equals(movim.getNumero()))
									|| !this.isNotaCredito(sigla)) {
								out.add(det);
							}				
						}
						List<Object[]> recs = rr.getRecibosByVenta(venta.getId(), venta.getTipoMovimiento().getId());
						for (Object[] rec_ : recs) {
							Recibo recibo = (Recibo) rec_[0];
							ReciboDetalle rdet_ = (ReciboDetalle) rec_[1];
							DetalleMovimiento det = new DetalleMovimiento();
							det.setEmision(recibo.getFechaEmision());
							det.setNumero(recibo.getNumero());
							det.setSigla(recibo.getTipoMovimiento().getSigla());
							det.setTipoMovimiento(recibo.getTipoMovimiento().getDescripcion());
							det.setImporteGs(rdet_.getMontoGs());
							det.setAgrupador(venta.getNumero());
							det.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
							if (recibo.getNumero().equals(movim.getNumero())) {
								det.setSelf(true);
							}
							out.add(det);
						}
					}					
				}
			}
		}
			
		Collections.sort(out, new Comparator<DetalleMovimiento>() {
			@Override
			public int compare(DetalleMovimiento o1, DetalleMovimiento o2) {
				Date fecha1 = o1.getEmision();
				Date fecha2 = o2.getEmision();
				return fecha1.compareTo(fecha2);
			}
		});
		
		double saldo = 0;
		for (DetalleMovimiento det : out) {
			saldo += det.getDebe() - det.getHaber();
			det.setSaldo(saldo);
		}		
		return out;
	}
	
	/**
	 * bloquea / desbloquea la cuenta corriente..
	 */
	private void bloquearCuenta(long idEmpresa, boolean bloquear, String motivo, boolean temporal) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Empresa emp = rr.getEmpresaById(idEmpresa);
		emp.setCuentaBloqueada(bloquear);
		emp.setMotivoBloqueo(motivo);
		if(temporal) emp.setAuxi(Empresa.DESBLOQUEO_TEMPORAL);
		rr.saveObject(emp, this.getLoginNombre());
		Clients.showNotification(bloquear? "Cuenta Bloqueada.." : "Cuenta Desbloqueada..");
	}
	
	/**
	 * habilita credito al cliente..
	 */
	private void habilitarCredito() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Cliente cli = rr.getClienteByIdEmpresa(this.selectedItem.getId());
		cli.setVentaCredito(true);
		rr.saveObject(cli, this.getLoginNombre());
		this.cliente = this.clienteToMyArray(cli);
	}
	
	/**
	 * guarda la observacion del cliente..
	 */
	private void guardarObservacion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Empresa emp = rr.getEmpresaById(this.selectedItem.getId());
		EmpresaObservacion obs = new EmpresaObservacion();
		obs.setDescripcion(this.selectedItem.getPos11().toString().toUpperCase());
		obs.setFecha(new Date());
		obs.setUsuario(this.getLoginNombre().toUpperCase());
		emp.getObservaciones().add(obs);
		rr.saveObject(emp, this.getLoginNombre());
		Clients.showNotification("Observacion registrada..!");
	}
	
	/**
	 * impresion del reporte..
	 */
	private void imprimir_(boolean mobile) throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();		
		Date hoy = new Date();
		
		for (MyArray mov : this.getMovimientos()) {
			Date emision = (Date) mov.getPos1();
			Date vto = (Date) mov.getPos2();
			String nro = (String) mov.getPos4();
			double saldo = (double) mov.getPos6();
			
			Object[] obj = new Object[] {
					m.dateToString(emision, Utiles.DD_MM_YY),
					m.dateToString(vto, Utiles.DD_MM_YY),
					mov.getPos3(), nro.replace("(1/1)", "").replace("(1/3)", "").replace("(2/3)", "").replace("(3/3)", ""),
					mov.getPos5(), mov.getPos6(), Utiles.diasEntreFechas(vto != null? vto : hoy, hoy) };
			data.add(obj);
			
			if (this.selectedFilter.equals(CLIENTE)) {
				if (saldo < 0) {
					this.setDetalles(mov);
					List<DetalleMovimiento> dets = this.getAplicaciones(mov, this.detalle);
					for (DetalleMovimiento det : dets) {
						if (!det.isSelf()) {
							Object[] obj_ = new Object[] {
									Utiles.getDateToString(det.getEmision(), Utiles.DD_MM_YY),
									Utiles.getDateToString(det.getEmision(), Utiles.DD_MM_YY),
									">> " + det.getTipoMovimiento_(), det.getNumero(),
									det.getImporteGs() * -1, 0.0, 0 };
							data.add(obj_);
						}							
					}
				}
			}			
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("RazonSocial", (String) this.selectedItem.getPos3());
		params.put("Ruc", (String) this.selectedItem.getPos1());
		params.put("Direccion", (String) this.selectedItem.getPos7());
		params.put("Telefono", (String) this.selectedItem.getPos8());
		params.put("Titulo", "Estado de Cuenta - " + this.selectedMoneda + "" );
		ReporteYhaguy rep = new ReporteEstadoCuenta(params);
		rep.setDatosReporte(data);
		
		if (!mobile) {
			ViewPdf vp = new ViewPdf();
			vp.setBotonImprimir(false);
			vp.setBotonCancelar(false);
			vp.showReporte(rep, this);
		} else {
			rep.ejecutar();
			Filedownload.save("/reportes/" + rep.getArchivoSalida(), null);
		}		
	}	
	
	/**
	 * impresion en formato Debe Haber Saldo..
	 */
	private void historialMovimientosClientes() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = Utiles.getFechaInicioOperaciones();
			Date hasta = this.hasta;
			Cliente cliente = rr.getClienteByEmpresa(this.selectedItem.getId());
			boolean incluirChequesRechazados = true;
			boolean incluirPrestamos = false;
			
			if (hasta == null) hasta = new Date();

			List<Object[]> data = new ArrayList<Object[]>();
			List<Object[]> historico;
			List<Object[]> historicoDEBE;
			List<Object[]> historicoHABER;
			Map<String, String> totalSaldo = new HashMap<String, String>();
			double totalVentas = 0;
			double totalChequesRechazados = 0;
			double totalNotasCredito = 0;
			double totalRecibos = 0;
			double totalNotasDebito = 0;
			double totalReembolsosCheques = 0;

			long idCliente = cliente != null ? cliente.getId() : 0;

			List<Object[]> ventas = rr.getVentasPorCliente_(idCliente, desde, hasta);
			List<Object[]> cheques_rechazados = rr.getChequesRechazadosPorCliente(idCliente, desde, hasta);
			List<Object[]> prestamos_cc = rr.getPrestamosCC(idCliente, desde, hasta);
			List<Object[]> ntcsv = rr.getNotasCreditoPorCliente(idCliente, desde, hasta);
			List<Object[]> recibos = rr.getRecibosPorCliente(idCliente, desde, hasta);
			List<Object[]> reembolsos_cheques_rechazados = rr.getReembolsosChequesRechazadosPorCliente(idCliente, desde, hasta);
			List<Object[]> reembolsos_prestamos_cc = rr.getReembolsosPrestamosCC(idCliente, desde, hasta);
			
			for (Object[] venta : ventas) {
				totalVentas += ((double) venta[3]);
			}
			
			for (Object[] chequeRech : cheques_rechazados) {
				totalChequesRechazados += ((double) chequeRech[3]);
			}
			
			for (Object[] ncred : ntcsv) {
				totalNotasCredito -= ((double) ncred[3]);
			}
			
			for (Object[] rec : recibos) {
				totalRecibos -= ((double) rec[3]);
			}
			
			for (Object[] reemb : reembolsos_cheques_rechazados) {
				totalReembolsosCheques -= ((double) reemb[3]);
			}

			historicoDEBE = new ArrayList<Object[]>();
			historicoHABER = new ArrayList<Object[]>();
			
			historicoDEBE.addAll(ventas);				
			if (incluirChequesRechazados) historicoDEBE.addAll(cheques_rechazados);
			if (incluirPrestamos) historicoDEBE.addAll(prestamos_cc);				
			
			historicoHABER.addAll(ntcsv);
			historicoHABER.addAll(recibos);
			if (incluirChequesRechazados) historicoHABER.addAll(reembolsos_cheques_rechazados);
			if (incluirPrestamos) historicoHABER.addAll(reembolsos_prestamos_cc);				

			for (Object[] movim : historicoDEBE) {
				movim[0] = "(+)" + movim[0];
			}

			historico = new ArrayList<Object[]>();
			historico.addAll(historicoDEBE);
			historico.addAll(historicoHABER);

			// ordena la lista segun fecha..
			Collections.sort(historico, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					Date fecha1 = (Date) o1[1];
					Date fecha2 = (Date) o2[1];
					return fecha1.compareTo(fecha2);
				}
			});
			
			double saldo = 0;
			
			Collections.sort(historico, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String val1 = (String) o1[4];
					String val2 = (String) o2[4];			
					int compare = val1.compareTo(val2);
					if (compare == 0) {
						Date emision1 = (Date) o1[1];
						Date emision2 = (Date) o2[1];
			            return emision1.compareTo(emision2);
			        }
			        else {
			            return compare;
			        }
				}
			});
			
			String key = "";
			for (Object[] hist : historico) {
				String razonsocial = (String) hist[4];
				if(!key.equals(razonsocial)) saldo = 0;					
				boolean ent = ((String) hist[0]).startsWith("(+)");
				String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
				String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
				String numero = (String) hist[2];
				String concepto = ((String) hist[0]).replace("(+)", "");
				String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
				String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
				saldo += ent ? Double.parseDouble(hist[3] + "") :  Double.parseDouble(hist[3] + "") * -1;
				String saldo_ = Utiles.getNumberFormat(saldo);
				data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
				totalSaldo.put(razonsocial, saldo_);
				key = razonsocial;
			}
			
			String cli = cliente != null ? cliente.getRazonSocial() : "TODOS..";
			String sourceDetallado = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
			String source = sourceDetallado;
			String titulo = "SALDOS DE CLIENTES DETALLADO (HISTORIAL A UNA FECHA)";
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosDHSDataSource_(data, cli, totalSaldo, totalVentas,
					totalChequesRechazados, totalNotasCredito, totalRecibos, totalNotasDebito,
					totalReembolsosCheques);
			params.put("Titulo", titulo);
			params.put("Usuario", getUs().getNombre());
			params.put("Moneda", this.selectedMoneda);
			params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
			params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_PDF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * impresion en formato Debe Haber Saldo..
	 */
	private void historialMovimientosProveedores(Proveedor proveedor_) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = Utiles.getFechaInicioOperaciones();
			Date hasta = this.hasta;
			Object[] formato = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_PDF;
			Proveedor proveedor = proveedor_;

			List<Object[]> data = new ArrayList<Object[]>();
			List<Object[]> historico;
			List<Object[]> historicoDEBE;
			List<Object[]> historicoHABER;
			Map<String, String> totalSaldo = new HashMap<String, String>();

			long idProveedor = proveedor != null ? proveedor.getId() : 0;

			List<Object[]> compras = rr.getComprasPorProveedor_(idProveedor, desde, hasta);
			List<Object[]> gastos = rr.getGastosPorProveedor_(idProveedor, desde, hasta);
			List<Object[]> pagos = rr.getPagosPorProveedor(idProveedor, desde, hasta, true);
			List<Object[]> notascredito = rr.getNotasCreditoPorProveedor(idProveedor, desde, hasta, true);

			historicoDEBE = new ArrayList<Object[]>();
			historicoHABER = new ArrayList<Object[]>();
			
			historicoDEBE.addAll(pagos);
			historicoDEBE.addAll(notascredito);
			
			historicoHABER.addAll(compras);
			historicoHABER.addAll(gastos);

			for (Object[] movim : historicoDEBE) {
				movim[0] = "(+)" + movim[0];
			}

			historico = new ArrayList<Object[]>();
			historico.addAll(historicoHABER);
			historico.addAll(historicoDEBE);

			// ordena la lista segun fecha..
			Collections.sort(historico, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					Date fecha1 = (Date) o1[1];
					Date fecha2 = (Date) o2[1];
					return fecha1.compareTo(fecha2);
				}
			});
			
			double saldo = 0;
			
			Collections.sort(historico, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String val1 = (String) o1[4];
					String val2 = (String) o2[4];			
					int compare = val1.compareTo(val2);
					if (compare == 0) {
						Date emision1 = (Date) o1[1];
						Date emision2 = (Date) o2[1];
			            return emision1.compareTo(emision2);
			        }
			        else {
			            return compare;
			        }
				}
			});
			
			String key = "";
			for (Object[] hist : historico) {
				String razonsocial = (String) hist[4];
				if(!key.equals(razonsocial)) saldo = 0;					
				boolean ent = ((String) hist[0]).startsWith("(+)");
				String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
				String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
				String numero = (String) hist[2];
				String concepto = ((String) hist[0]).replace("(+)", "");
				String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
				String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
				saldo += ent ? Double.parseDouble(hist[3] + "") * -1 : Double.parseDouble(hist[3] + "");
				String saldo_ = Utiles.getNumberFormat(saldo);
				data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
				totalSaldo.put(razonsocial, saldo_);
				key = razonsocial;
			}
			
			String cli = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
			String titulo = "SALDOS DE PROVEEDORES DETALLADO (A UNA FECHA)";
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
			params.put("Titulo", titulo);
			params.put("Usuario", getUs().getNombre());
			params.put("Moneda", this.selectedMoneda);
			params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
			params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, formato);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * impresion en formato Debe Haber Saldo..
	 */
	private void historialMovimientosProveedoresExterior(Proveedor proveedor_) {
		try {
			Date desde = Utiles.getFechaInicioOperaciones();
			Date hasta = this.hasta;
			Object[] formato = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_PDF;
			Proveedor proveedor = proveedor_;

			if (desde == null) desde = new Date();
			if (hasta == null) hasta = new Date();

			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> data = new ArrayList<Object[]>();
			List<Object[]> historico;
			List<Object[]> historicoDEBE;
			List<Object[]> historicoHABER;
			Map<String, String> totalSaldo = new HashMap<String, String>();

			long idProveedor = proveedor != null ? proveedor.getId() : 0;

			List<Object[]> importaciones = rr.getImportacionesPorProveedor_(idProveedor, desde, hasta);
			List<Object[]> pagos = rr.getPagosPorProveedorExterior(idProveedor, desde, hasta, false);
			List<Object[]> anticipos = rr.getPagosAnticipadosPorProveedorExterior(idProveedor, desde, hasta, false);
			List<Object[]> notascredito = rr.getNotasCreditoPorProveedor(idProveedor, desde, hasta, false);
			
			historicoDEBE = new ArrayList<Object[]>();
			historicoHABER = new ArrayList<Object[]>();
			
			historicoDEBE.addAll(pagos);
			historicoDEBE.addAll(anticipos);
			historicoDEBE.addAll(notascredito);
			historicoHABER.addAll(importaciones);

			for (Object[] movim : historicoDEBE) {
				movim[0] = "(+)" + movim[0];
			}

			historico = new ArrayList<Object[]>();
			historico.addAll(historicoHABER);
			historico.addAll(historicoDEBE);

			// ordena la lista segun fecha..
			Collections.sort(historico, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					Date fecha1 = (Date) o1[1];
					Date fecha2 = (Date) o2[1];
					return fecha1.compareTo(fecha2);
				}
			});
			
			double saldo = 0;
			
			Collections.sort(historico, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String val1 = (String) o1[4];
					String val2 = (String) o2[4];			
					int compare = val1.compareTo(val2);
					if (compare == 0) {
						Date emision1 = (Date) o1[1];
						Date emision2 = (Date) o2[1];
			            return emision1.compareTo(emision2);
			        }
			        else {
			            return compare;
			        }
				}
			});
			
			String key = "";
			for (Object[] hist : historico) {
				String razonsocial = (String) hist[4];
				if(!key.equals(razonsocial)) saldo = 0;					
				boolean ent = ((String) hist[0]).startsWith("(+)");
				String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
				String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
				String numero = (String) hist[2];
				String concepto = ((String) hist[0]).replace("(+)", "");
				String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "";
				String salida = ent ? "" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
				saldo += ent ? Double.parseDouble(hist[3] + "") * -1 : Double.parseDouble(hist[3] + "");
				String saldo_ = Utiles.getNumberFormat(saldo);
				data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, razonsocial, (Date) hist[1] });
				totalSaldo.put(razonsocial, saldo_);
				key = razonsocial;
			}
			
			String cli = proveedor != null ? proveedor.getRazonSocial() : "TODOS..";
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
			String titulo = "SALDOS DE PROVEEDORES EXTERIOR DETALLADO (A UNA FECHA)";
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
			params.put("Titulo", titulo);
			params.put("Usuario", getUs().getNombre());
			params.put("Moneda", this.selectedMoneda);
			params.put("desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
			params.put("hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, formato);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * imprimir en formato columnas..
	 */
	private void imprimirFormatoColumnas() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Date desde = Utiles.getFechaInicioOperaciones();
			Date hasta = this.hasta;
			Cliente cliente = rr.getClienteByEmpresa(this.selectedItem.getId());

			if (desde == null) desde = new Date();
			
			Map<String, Object[]> acum = new HashMap<String, Object[]>();
			List<Object[]> data = new ArrayList<Object[]>();
			
			double totalVentas = 0.0;
			double totalChequesRechazados = 0.0;
			double totalNotasCredito = 0.0;
			double totalRecibos = 0.0;
			double totalNotasDebito = 0.0;
			double totalChequesReembolsos = 0.0;
			double totalMigracion = 0.0;
			double totalMigracionChequesRechazados = 0.0;
			double totalPrestamos = 0.0;
			double totalReembPrestamos = 0.0;

			long idCliente = cliente != null ? cliente.getId() : 0;
			long idEmpresa = cliente != null ? cliente.getIdEmpresa() : 0;

			List<Object[]> ventas = rr.getVentasCreditoPorCliente(desde, hasta, idCliente);
			List<Object[]> chequesRechazados = rr.getChequesRechazadosPorCliente(desde, hasta, idCliente);
			List<Object[]> ncreditos = rr.getNotasCreditoPorCliente(desde, hasta, idCliente);
			List<Object[]> recibos = rr.getRecibosPorCliente(desde, hasta, idCliente);
			List<Object[]> ndebitos = rr.getNotasDebitoPorCliente(desde, hasta, idCliente);
			List<Object[]> reembolsos = rr.getReembolsosPorCliente(desde, hasta, idCliente);
			List<Object[]> migracion = rr.getCtaCteMigracionPorClienteVentasGs(desde, hasta, idCliente);
			List<Object[]> migracionChequesRechazados = rr.getCtaCteMigracionPorClienteChequesRechazados(desde, hasta, idCliente);
			List<Object[]> prestamos = rr.getPrestamosPorDeudor(desde, hasta, idEmpresa);
			List<Object[]> reembPrestamos = rr.getReembolsosPrestamos(desde, hasta, idCliente);
			
			for (Object[] venta : ventas) {
				String key = (String) venta[1];
				venta = Arrays.copyOf(venta, venta.length + 9);
				venta[3] = 0.0;
				venta[4] = 0.0;
				venta[5] = 0.0;
				venta[6] = 0.0;
				venta[7] = 0.0;
				venta[8] = 0.0;
				venta[9] = 0.0;
				venta[10] = 0.0;
				venta[11] = 0.0;
				acum.put(key, venta);
			}
			
			for (Object[] cheque : chequesRechazados) {
				String key = (String) cheque[1];
				double importe = (double) cheque[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[3] = importe;
				} else {
					obj = new Object[] { cheque[0], cheque[1], 0.0, cheque[2], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
				}
				acum.put(key, obj);
			}
			
			for (Object[] ncred : ncreditos) {
				String key = (String) ncred[1];
				double importe = (double) ncred[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[4] = importe * -1;
				} else {
					obj = new Object[] { ncred[0], ncred[1], 0.0, 0.0, importe * -1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
				}
				acum.put(key, obj);
			}
			
			for (Object[] rec : recibos) {
				String key = (String) rec[1];
				double importe = (double) rec[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[5] = importe * -1;
				} else {
					obj = new Object[] { rec[0], rec[1], 0.0, 0.0, 0.0, importe * -1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
				}
				acum.put(key, obj);
			}
			
			for (Object[] ndeb : ndebitos) {
				String key = (String) ndeb[1];
				double importe = (double) ndeb[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[6] = importe;
				} else {
					obj = new Object[] { ndeb[0], ndeb[1], 0.0, 0.0, 0.0, 0.0, importe, 0.0, 0.0, 0.0, 0.0, 0.0 };
				}
				acum.put(key, obj);
			}
			
			for (Object[] reemb : reembolsos) {
				String key = (String) reemb[1];
				double importe = (double) reemb[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[7] = importe * -1;
				} else {
					obj = new Object[] { reemb[0], reemb[1], 0.0, 0.0, 0.0, 0.0, 0.0, importe * -1, 0.0, 0.0, 0.0, 0.0 };
				}
				acum.put(key, obj);
			}
			
			for (Object[] mig : migracion) {
				String key = (String) mig[1];
				double importe = (double) mig[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[8] = importe;
				} else {
					obj = new Object[] { mig[0], mig[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, importe, 0.0, 0.0, 0.0 };
				}
				acum.put(key, obj);
			}
			
			for (Object[] mig : migracionChequesRechazados) {
				String key = (String) mig[1];
				double importe = (double) mig[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[9] = importe;
				} else {
					obj = new Object[] { mig[0], mig[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, importe, 0.0, 0.0 };
				}
				acum.put(key, obj);
			}
			
			for (Object[] prest : prestamos) {
				String key = (String) prest[1];
				double importe = (double) prest[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[10] = importe;
				} else {
					obj = new Object[] { prest[0], prest[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, importe, 0.0 };
				}
				acum.put(key, obj);
			}
			
			for (Object[] reemb : reembPrestamos) {
				String key = (String) reemb[1];
				double importe = (double) reemb[2];
				Object[] obj = acum.get(key);
				if (obj != null) {
					obj[11] = importe * -1;
				} else {
					obj = new Object[] { reemb[0], reemb[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, importe };
				}
				acum.put(key, obj);
			}
			
			for (String key : acum.keySet()) {
				Object[] data_ = acum.get(key);
				double vtas = (double) data_[2];
				double rech = (double) data_[3];
				double ncrs = (double) data_[4];
				double recs = (double) data_[5];
				double ndeb = (double) data_[6];
				double reem = (double) data_[7];
				double migr = (double) data_[8];
				double mgcr = (double) data_[9];
				double pres = (double) data_[10];
				double reep = (double) data_[11];
				totalVentas += vtas;
				totalChequesRechazados += rech;
				totalNotasCredito += ncrs;
				totalRecibos += recs;
				totalNotasDebito += ndeb;
				totalChequesReembolsos += reem;
				totalMigracion += migr;
				totalMigracionChequesRechazados += mgcr;
				totalPrestamos += pres;
				totalReembPrestamos += reep;
				data.add(data_);
			}
			
			Collections.sort(data, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					String val1 = (String) o1[1];
					String val2 = (String) o2[1];
					int compare = val1.compareTo(val2);				
					return compare;
				}
			});	
			
			String cli = cliente != null ? cliente.getRazonSocial() : "TODOS..";
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_CLIENTES_RESUMIDO;
			String titulo = "SALDOS DE CLIENTES RESUMIDO (A UNA FECHA)";
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosResumidoDataSource(data, cli, totalVentas,
					totalChequesRechazados, totalNotasCredito, totalRecibos, totalNotasDebito,
					totalChequesReembolsos, totalMigracion, totalMigracionChequesRechazados, totalPrestamos, totalReembPrestamos);
			params.put("Titulo", titulo);
			params.put("Usuario", getUs().getNombre());
			params.put("Desde", Utiles.getDateToString(desde, Utiles.DD_MM_YYYY));
			params.put("Hasta", Utiles.getDateToString(hasta, Utiles.DD_MM_YYYY));
			imprimirJasper(source, params, dataSource, com.yhaguy.gestion.reportes.formularios.ReportesViewModel.FORMAT_PDF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * Despliega el reporte en un pdf para su impresion..
	 */
	private void imprimirJasper(String source, Map<String, Object> parametros,
			JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions
				.createComponents(
						com.yhaguy.gestion.reportes.formularios.ReportesViewModel.ZUL_REPORTES,
						this.mainComponent, params);
		this.win.doModal();
	}
	
	@DependsOn({ "ruc", "cedula", "razonSocial", "nombreFantasia" })
	public List<MyArray> getEmpresas() throws Exception {
		this.setSelectedItem(null);
		
		if (this.ruc.isEmpty() && this.cedula.isEmpty()
				&& this.razonSocial.isEmpty() && this.nombreFantasia.isEmpty())
			return new ArrayList<MyArray>();

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Empresa> emps = rr.getEmpresas(this.ruc, this.cedula, this.razonSocial, this.nombreFantasia);

		return this.empresasToMyArray(emps);
	}
	
	/**
	 * @return las empresas convertidas a myarray..
	 */
	private List<MyArray> empresasToMyArray(List<Empresa> emps) throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		for (Empresa emp : emps) {
			MyArray my = new MyArray();
			my.setId(emp.getId());
			my.setPos1(emp.getRuc());
			my.setPos2(emp.getCi());
			my.setPos3(emp.getRazonSocial());
			my.setPos4(emp.getNombre());
			my.setPos5(emp.isCuentaBloqueada());
			my.setPos6(emp.getMotivoBloqueo());
			my.setPos7(emp.getDireccion_());
			my.setPos8(emp.getTelefono_());
			my.setPos9(emp.getAuxi().equals(Empresa.DESBLOQUEO_TEMPORAL) ? true : false); // true si es desbloqueo temporal..
			my.setPos10(emp.getVendedor() != null? emp.getVendedor().getRazonSocial() : "");
			my.setPos11("");
			my.setPos12(emp.getObservaciones());
			my.setPos13(emp.getCartera());
			out.add(my);
		}
		return out;
	}
	
	/**
	 * @return los movimientos de cta cte de la empresa seleccionada..
	 */
	@DependsOn({ "verEstCta", "desde", "hasta", "selectedFilter",
			"fraccionado", "concepto", "numero", "numeroImportacion", "selectedMoneda" })
	public List<MyArray> getMovimientos() throws Exception {
		if (!this.verEstCta || this.desde == null) {
			return new ArrayList<MyArray>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CtaCteEmpresaMovimiento> list = rr.getCtaCteMovimientos(this.selectedItem.getId(), 
				this.desde, this.hasta, this.getCaracterMovimientos(), this.getIdMoneda());
		if (!this.fraccionado) {
			Map<Long, CtaCteEmpresaMovimiento> acum = new HashMap<Long, CtaCteEmpresaMovimiento>();
			List<CtaCteEmpresaMovimiento> aux = new ArrayList<CtaCteEmpresaMovimiento>();
			for (CtaCteEmpresaMovimiento movim : list) {
				if (movim.isVentaCredito()) {
					CtaCteEmpresaMovimiento cmovim = acum.get(movim.getIdMovimientoOriginal());
					if (cmovim != null) {
						cmovim.setSaldo(cmovim.getSaldo() + movim.getSaldo());
						int cmp = movim.getFechaVencimiento().compareTo(cmovim.getFechaVencimiento());
						if (cmp < 0) {
							cmovim.setFechaVencimiento(movim.getFechaVencimiento());
							cmovim.setNroComprobante(movim.getNroComprobante());
						}
						acum.put(movim.getIdMovimientoOriginal(), cmovim);
					} else {
						acum.put(movim.getIdMovimientoOriginal(), movim);
					}					
				} else {
					aux.add(movim);
				}
			}
			for (Long key : acum.keySet()) {
				aux.add(acum.get(key));
			}
			// ordena la lista segun fecha..
			Collections.sort(aux, new Comparator<CtaCteEmpresaMovimiento>() {
				@Override
				public int compare(CtaCteEmpresaMovimiento o1, CtaCteEmpresaMovimiento o2) {
					Date fecha1 = o1.getFechaEmision();
					Date fecha2 = o2.getFechaEmision();
					return fecha1.compareTo(fecha2);
				}
			});
			return this.movimientosToMyArray(aux, this.concepto, this.numero, this.numeroImportacion);
		}
		return this.movimientosToMyArray(list, this.concepto, this.numero, this.numeroImportacion);
	}
	
	@DependsOn("selectedItem")
	public List<MyArray> getChequesPendientes() throws Exception {
		this.totalCheques = 0;
		List<MyArray> out = new ArrayList<MyArray>();
		if (this.isTipoCliente() && this.selectedItem != null) {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<BancoChequeTercero> cheques = rr.getChequesTercero("",
					"", "", "", "", "",
					(String) this.selectedItem.getPos3(), "", "", "", "",
					"", "FALSE", null, "FALSE", "FALSE", null, null, null, null, "", "", true);
			for (BancoChequeTercero cheque : cheques) {
				if (cheque.getFecha().compareTo(new Date()) > 0) {
					MyArray my = new MyArray();
					my.setId(cheque.getId());
					my.setPos1(cheque.getNumero());
					my.setPos2(cheque.getFecha());
					my.setPos3(cheque.getBanco().getDescripcion());
					my.setPos4(cheque.getMonto());
					out.add(my);
					this.totalCheques += cheque.getMonto();
				}				
			}
		}
		this.sizeCheques = out.size();
		BindUtils.postNotifyChange(null, null, this, "sizeCheques");
		BindUtils.postNotifyChange(null, null, this, "totalCheques");
		return out;
	}
	
	@DependsOn("selectedItem")
	public List<TareaProgramada> getTareasPendientes() throws Exception {
		if (this.selectedItem == null) {
			return new ArrayList<TareaProgramada>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTareasProgramadas(this.selectedItem.getId());
	}
		
	/**
	 * @return los movimientos de cta cte convertidos a myarray..
	 */
	private List<MyArray> movimientosToMyArray(List<CtaCteEmpresaMovimiento> movs, String concepto, String numero, String numeroImportacion) {
		List<MyArray> out = new ArrayList<MyArray>();
		for (CtaCteEmpresaMovimiento mov : movs) {
			MyArray my = new MyArray();
			my.setId(mov.getId());
			my.setPos1(mov.getFechaEmision());
			my.setPos2(mov.getFechaVencimiento());
			my.setPos3(mov.getTipoMovimiento().getDescripcion());
			my.setPos4(mov.getNroComprobante());
			my.setPos5(mov.getImporteOriginal());
			my.setPos6(mov.getSaldo());	
			my.setPos7(mov.isVencido());
			my.setPos8(mov.getTipoMovimiento().getSigla());
			my.setPos9(mov.getIdMovimientoOriginal());
			my.setPos10(mov.getNumeroImportacion().isEmpty() ? "- - -" : mov.getNumeroImportacion());
			my.setPos11(mov.getObservacion());
			my.setPos12(mov.getCarteraCliente());
			if (this.isTodos()) {
				out.add(my);
			} else if (this.isPendientes()) {
				if (mov.getSaldoFinal() != 0)
					out.add(my);
			} else if (this.isVencidos()) {
				if(mov.getSaldoFinal() != 0 && mov.isVencido())
					out.add(my);
			}			
		}
		if ((!concepto.isEmpty()) || (!numero.isEmpty()) || (!numeroImportacion.isEmpty())) {
			List<MyArray> aux = new ArrayList<MyArray>();
			for (MyArray my : out) {
				String ccpto = (String) my.getPos3();
				String nroFac = (String) my.getPos4();
				String nroImp = (String) my.getPos10();
				if (((!concepto.isEmpty() && ccpto.toLowerCase().contains(
						concepto.toLowerCase())) || concepto.isEmpty())
						&& ((!numero.isEmpty() && nroFac.toLowerCase()
								.contains(numero.toLowerCase())) || numero
								.isEmpty())
						&& ((!numeroImportacion.isEmpty() && nroImp
								.toLowerCase().contains(
										numeroImportacion.toLowerCase())) || numeroImportacion
								.isEmpty())) {
					aux.add(my);
				}
			}
			return aux;
		}		
		return out;
	}	
	
	/**
	 * contiene los datos del movimiento..
	 */
	public class DetalleMovimiento {
		
		private Date emision; 
		private Date vencimiento;
		private Date fechaRechazo;
		private String numero;
		private String numeroPlanilla;
		private String facturaAplicada;
		private String sigla;
		private String sigla_;
		private String tipoMovimiento;
		private String tipoMovimiento_;
		private String motivo;
		private List<MyArray> detalles;
		private List<MyArray> formasPago;
		private String agrupador;
		private String descripcion;
		private String librador;
		private long idMovimiento;
		
		private List<DetalleMovimiento> aplicaciones = new ArrayList<DetalleMovimiento>();
		
		private boolean self = false;
		private double importeGs = 0;
		private double saldo = 0;
		
		/**
		 * @return el importe total..
		 */
		public double getTotalImporteGs() {
			if (this.detalles == null) {
				return 0;
			}
			double out = 0;
			for (MyArray item : detalles) {
				double importe = (double) item.getPos5();
				out += importe;
			}			
			return out;
		}
		
		/**
		 * @return el importe total de formas de pago..
		 */
		public double getTotalImporteFormaPagoGs() {
			if (this.formasPago == null) {
				return 0;
			}
			double out = 0;
			for (MyArray item : formasPago) {
				double importe = (double) item.getPos2();
				out += importe;
			}			
			return out;
		}
		
		/**
		 * @return el debe..
		 */
		public double getDebe() {
			return this.isVentaCredito() || this.isAjusteDebito() ? this.importeGs : 0.0;
		}
		
		/**
		 * @return el haber..
		 */
		public double getHaber() {
			return this.isNotaCredito() || this.isReciboCobro() || this.isAnticipoCobro() || this.isAjusteCredito() ? this.importeGs : 0.0;
		}
		
		/**
		 * @return el saldo total..
		 */
		public double getTotalSaldo() {
			return this.getTotalDebe() - this.getTotalHaber();
		}
		
		/**
		 * @return el total del debe..
		 */
		public double getTotalDebe() {
			double out = 0;
			for (DetalleMovimiento movim : this.aplicaciones) {
				out += movim.getDebe();
			}
			return out;
		}
		
		/**
		 * @return el total del haber..
		 */
		public double getTotalHaber() {
			double out = 0;
			for (DetalleMovimiento movim : this.aplicaciones) {
				out += movim.getHaber();
			}
			return out;
		}
		
		/**
		 * @return true si es nota de credito..
		 */
		public boolean isNotaCredito() {
			if(sigla == null) return false;
			return sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_COMPRA)
					|| sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA);
		}
		
		/**
		 * @return true si es venta credito..
		 */
		public boolean isVentaCredito() {
			if(sigla == null) return false;
			return sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
		}
		
		/**
		 * @return true si es recibo de cobro..
		 */
		public boolean isReciboCobro() {
			if(sigla == null) return false;
			return sigla.equals(Configuracion.SIGLA_TM_RECIBO_COBRO);
		}
		
		/**
		 * @return true si es anticipo de cobro..
		 */
		public boolean isAnticipoCobro() {
			if(sigla == null) return false;
			return sigla.equals(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
		}
		
		/**
		 * @return true si es ajuste credito..
		 */
		public boolean isAjusteCredito() {
			if(sigla == null) return false;
			return sigla.equals(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
		}
		
		/**
		 * @return true si es ajuste debito..
		 */
		public boolean isAjusteDebito() {
			if(sigla == null) return false;
			return sigla.equals(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
		}

		public String getNumero() {
			return numero;
		}

		public void setNumero(String numero) {
			this.numero = numero.replace("(1/1)", "");
		}

		public Date getEmision() {
			return emision;
		}

		public void setEmision(Date emision) {
			this.emision = emision;
		}

		public List<MyArray> getDetalles() {
			return detalles;
		}

		public void setDetalles(List<MyArray> detalle) {
			this.detalles = detalle;
		}

		public Date getVencimiento() {
			return vencimiento;
		}

		public void setVencimiento(Date vencimiento) {
			this.vencimiento = vencimiento;
		}

		public String getSigla() {
			return sigla;
		}

		public void setSigla(String sigla) {
			this.sigla = sigla;
		}

		public String getFacturaAplicada() {
			return facturaAplicada;
		}

		public void setFacturaAplicada(String facturaAplicada) {
			this.facturaAplicada = facturaAplicada;
		}

		public String getTipoMovimiento() {
			return tipoMovimiento;
		}

		public void setTipoMovimiento(String tipoMovimiento) {
			this.tipoMovimiento = tipoMovimiento;
		}

		public List<DetalleMovimiento> getAplicaciones() {
			return aplicaciones;
		}

		public boolean isSelf() {
			return self;
		}

		public void setSelf(boolean self) {
			this.self = self;
		}

		public void setAplicaciones(List<DetalleMovimiento> aplicaciones) {
			this.aplicaciones = aplicaciones;
			groupModel = new DetalleGroupsModel(aplicaciones, new DetalleComparator());
			groupModel_ = new DetalleGroupsModel(aplicaciones, new DetalleComparator());
		}

		public double getImporteGs() {
			return importeGs;
		}

		public void setImporteGs(double importeGs) {
			this.importeGs = importeGs;
		}

		public double getSaldo() {
			return saldo;
		}

		public void setSaldo(double saldo) {
			this.saldo = saldo;
		}

		public String getMotivo() {
			return motivo;
		}

		public void setMotivo(String motivo) {
			this.motivo = motivo;
		}

		public List<MyArray> getFormasPago() {
			return formasPago;
		}

		public void setFormasPago(List<MyArray> formasPago) {
			this.formasPago = formasPago;
		}

		public String getNumeroPlanilla() {
			return numeroPlanilla;
		}

		public void setNumeroPlanilla(String numeroPlanilla) {
			this.numeroPlanilla = numeroPlanilla;
		}

		public String getAgrupador() {
			return "VENTA NRO. " + agrupador;
		}

		public void setAgrupador(String agrupador) {
			this.agrupador = agrupador;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}

		public long getIdMovimiento() {
			return idMovimiento;
		}

		public void setIdMovimiento(long idMovimiento) {
			this.idMovimiento = idMovimiento;
		}

		public String getSigla_() {
			return sigla_;
		}

		public void setSigla_(String sigla_) {
			this.sigla_ = sigla_;
		}

		public String getTipoMovimiento_() {
			return tipoMovimiento_;
		}

		public void setTipoMovimiento_(String tipoMovimiento_) {
			this.tipoMovimiento_ = tipoMovimiento_;
		}

		public String getLibrador() {
			return librador;
		}

		public void setLibrador(String librador) {
			this.librador = librador;
		}

		public Date getFechaRechazo() {
			return fechaRechazo;
		}

		public void setFechaRechazo(Date fechaRechazo) {
			this.fechaRechazo = fechaRechazo;
		}		
	}
	
	// para agrupar la lista de aplicaciones..
	public class DetalleGroupsModel extends GroupsModelArray<DetalleMovimiento, String, String, Object> {
		private static final long serialVersionUID = 1L;

		public DetalleGroupsModel(List<DetalleMovimiento> data, Comparator<DetalleMovimiento> cmpr) {
			super(data.toArray(new DetalleMovimiento[0]), cmpr);
		}

		protected String createGroupHead(DetalleMovimiento[] groupdata, int index, int col) {
	        String ret = "";
	        if (groupdata.length > 0) {
	            ret = groupdata[0].getAgrupador();
	        }	 
	        return ret;
	    }
	 
	    protected String createGroupFoot(DetalleMovimiento[] groupdata, int index, int col) {
	        return String.format("Total %d items", groupdata.length);
	    }
	}
	
	/**
	 * comparador para agrupar..
	 */
	class DetalleComparator implements Comparator<DetalleMovimiento>, GroupComparator<DetalleMovimiento>, Serializable {
		private static final long serialVersionUID = 1L;
		 
	    public int compare(DetalleMovimiento o1, DetalleMovimiento o2) {
	        return o1.getAgrupador().compareTo(o2.getAgrupador().toString());
	    }
	 
	    public int compareGroup(DetalleMovimiento o1, DetalleMovimiento o2) {
	        if(o1.getAgrupador().equals(o2.getAgrupador()))
	            return 0;
	        else
	            return 1;
	    }
	}
	
	/**
	 * GETTER / SETTER
	 */	
	public boolean isOperacionHabilitada(String operacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.isOperacionHabilitada(this.getLoginNombre(), operacion);
	}
	
	@DependsOn("movimientos")
	public double getTotalSaldoGs() throws Exception {
		double out = 0;
		for (MyArray item : this.getMovimientos()) {
			double saldo = (double) item.getPos6();
			out += saldo;
		}
		return out;
	}
	
	/**
	 * @return los parametros de filtro..
	 */
	public List<String> getFiltros() {
		List<String> out = new ArrayList<String>();
		out.add(TODOS);
		out.add(PENDIENTES);
		out.add(VENCIDOS);
		return out;
	}
	
	/**
	 * @return los parametros de tipo de cuenta..
	 */
	public List<String> getTiposCuenta() {
		List<String> out = new ArrayList<String>();
		out.add(CLIENTE);
		out.add(PROVEEDOR);
		return out;
	}
	
	@DependsOn("selectedTipo")
	public boolean isTipoCliente() {
		return this.selectedTipo.equals(CLIENTE);
	}
	
	/**
	 * @return el caracter de los movimientos..
	 */
	private String getCaracterMovimientos() {
		return this.selectedTipo.equals(CLIENTE) ? Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE
				: Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR;
	}

	/**
	 * @return el id de moneda gs o ds..
	 */
	private long getIdMoneda() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Tipo gs = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		Tipo ds = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR);
		return this.selectedMoneda.equals(CTA_GS)? gs.getId() : ds.getId(); 
	}
	
	/**
	 * @return true si es un movimiento de venta..
	 */
	private boolean isVenta(String sigla) {
		return sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO)
				|| sigla.equals(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
	}
	
	/**
	 * @return true si es un movimiento de nota de credito..
	 */
	private boolean isNotaCredito(String sigla) {
		return sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_COMPRA)
				|| sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA);
	}
	
	/**
	 * @return true si es un movimiento de cobro o pago..
	 */
	private boolean isRecibo(String sigla) {
		return sigla.equals(Configuracion.SIGLA_TM_RECIBO_COBRO)
				|| sigla.equals(Configuracion.SIGLA_TM_RECIBO_PAGO);
	}
	
	/**
	 * @return true si es un movimiento de cobro..
	 */
	private boolean isReciboCobro(String sigla) {
		return sigla.equals(Configuracion.SIGLA_TM_RECIBO_COBRO);
	}
	
	/**
	 * @return true si es un movimiento de anticipo..
	 */
	private boolean isAnticipoCobro(String sigla) {
		return sigla.equals(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
	}
	
	/**
	 * @return true si es un movimiento de cheque rechazado..
	 */
	private boolean isChequeRechazado(String sigla) {
		return sigla.equals(Configuracion.SIGLA_TM_CHEQUE_RECHAZADO);
	}
	
	/**
	 * @return las monedas..
	 */
	public List<String> getMonedas() {
		List<String> out = new ArrayList<String>();
		out.add(CTA_GS);
		out.add(CTA_DS);
		return out;
	}
	
	/**
	 * @return los telecobradores..
	 */
	public List<Funcionario> getTeleCobradores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTeleCobradores();
	}
	
	/**
	 * @return las carteras..
	 */
	private List<EmpresaCartera> getCarteras_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCarteras();
	}	
	
	@DependsOn("selectedMoneda")
	public String getColumnImporte() {
		return this.selectedMoneda.equals(CTA_GS)? "Importe Gs." : "Importe USD.";
	}
	
	@DependsOn("selectedMoneda")
	public String getColumnSaldo() {
		return this.selectedMoneda.equals(CTA_GS)? "Saldo Gs." : "Saldo USD.";
	}
	
	@DependsOn("selectedMoneda")
	public String getLabelTotalSaldo() {
		return this.selectedMoneda.equals(CTA_GS)? "Total Gs." : "Total USD.";
	}
	
	@DependsOn({ "selectedItem_", "selectedAplicacion" })
	public double getSaldoAplicar() {
		if (this.selectedItem_ == null || this.selectedAplicacion == null) return 0.0;
		double saldo1 = (double) this.selectedItem_.getPos6();
		if (saldo1 < 0) saldo1 = saldo1 * -1;
		double saldo2 = (double) this.selectedAplicacion.getPos6(); 
		double importe = (saldo1 - saldo2) > 0 ? saldo2 : saldo1;
		return importe;
	}
	
	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getNombreFantasia() {
		return nombreFantasia;
	}

	public void setNombreFantasia(String nombreFantasia) {
		this.nombreFantasia = nombreFantasia;
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public boolean isVerEstCta() {
		return verEstCta;
	}

	public void setVerEstCta(boolean verEstCta) {
		this.verEstCta = verEstCta;
	}

	private boolean isTodos() {
		return this.selectedFilter.equals(TODOS);
	}

	private boolean isPendientes() {
		return this.selectedFilter.equals(PENDIENTES);
	}

	private boolean isVencidos() {
		return this.selectedFilter.equals(VENCIDOS);
	}

	public String getSelectedFilter() {
		return selectedFilter;
	}

	public void setSelectedFilter(String selectedFilter) {
		this.selectedFilter = selectedFilter;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public DetalleMovimiento getDetalle() {
		return detalle;
	}

	public void setDetalle(DetalleMovimiento detalle) {
		this.detalle = detalle;
	}

	public MyArray getCliente() {
		return cliente;
	}

	public void setCliente(MyArray cliente) {
		this.cliente = cliente;
	}

	public String getSelectedTipo() {
		return selectedTipo;
	}

	public void setSelectedTipo(String selectedTipo) {
		this.selectedTipo = selectedTipo;
	}

	public int getSizeCheques() {
		return sizeCheques;
	}

	public void setSizeCheques(int sizeCheques) {
		this.sizeCheques = sizeCheques;
	}

	public double getTotalCheques() {
		return totalCheques;
	}

	public void setTotalCheques(double totalCheques) {
		this.totalCheques = totalCheques;
	}

	public boolean isFraccionado() {
		return fraccionado;
	}

	public void setFraccionado(boolean fraccionado) {
		this.fraccionado = fraccionado;
	}

	public MyArray getSelectedItem_() {
		return selectedItem_;
	}

	public void setSelectedItem_(MyArray selectedItem_) {
		this.selectedItem_ = selectedItem_;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumeroImportacion() {
		return numeroImportacion;
	}

	public void setNumeroImportacion(String numeroImportacion) {
		this.numeroImportacion = numeroImportacion;
	}

	public DetalleGroupsModel getGroupModel() {
		return groupModel;
	}

	public void setGroupModel(DetalleGroupsModel groupModel) {
		this.groupModel = groupModel;
	}

	public String getSelectedMoneda() {
		return selectedMoneda;
	}

	public void setSelectedMoneda(String selectedMoneda) {
		this.selectedMoneda = selectedMoneda;
	}

	public boolean isHabilitarLinea() {
		return habilitarLinea;
	}

	public void setHabilitarLinea(boolean habilitarLinea) {
		this.habilitarLinea = habilitarLinea;
	}

	public double getLineaTemporalGs() {
		return lineaTemporalGs;
	}

	public void setLineaTemporalGs(double lineaTemporalGs) {
		this.lineaTemporalGs = lineaTemporalGs;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public List<HistoricoLineaCredito> getHistoricoLineaCredito() {
		return historicoLineaCredito;
	}

	public void setHistoricoLineaCredito(List<HistoricoLineaCredito> historicoLineaCredito) {
		this.historicoLineaCredito = historicoLineaCredito;
	}

	public boolean isAplicaciones() {
		return aplicaciones;
	}

	public void setAplicaciones(boolean aplicaciones) {
		this.aplicaciones = aplicaciones;
	}

	public MyArray getSelectedAplicacion() {
		return selectedAplicacion;
	}

	public void setSelectedAplicacion(MyArray selectedAplicacion) {
		this.selectedAplicacion = selectedAplicacion;
	}

	public DetalleMovimiento getDetalle_() {
		return detalle_;
	}

	public void setDetalle_(DetalleMovimiento detalle_) {
		this.detalle_ = detalle_;
	}

	public DetalleGroupsModel getGroupModel_() {
		return groupModel_;
	}

	public void setGroupModel_(DetalleGroupsModel groupModel_) {
		this.groupModel_ = groupModel_;
	}

	public void setCarteras(List<EmpresaCartera> carteras) {
		this.carteras = carteras;
	}

	public List<EmpresaCartera> getCarteras() {
		return carteras;
	}

	public EmpresaCartera getSelectedCartera() {
		return selectedCartera;
	}

	public void setSelectedCartera(EmpresaCartera selectedCartera) {
		this.selectedCartera = selectedCartera;
	}	
}

/**
 * Reporte de Presupuesto..
 */
class ReporteEstadoCuenta extends ReporteYhaguy {
	
	private Map<String, String> params;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Emi.", TIPO_STRING, 33);
	static DatosColumnas col2 = new DatosColumnas("Vto.", TIPO_STRING, 33);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Número", TIPO_STRING, 70);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, 50);
	static DatosColumnas col6 = new DatosColumnas("Saldo", TIPO_DOUBLE_GS, 50, true);
	static DatosColumnas col7 = new DatosColumnas("Mora", TIPO_LONG, 25);
	
	public ReporteEstadoCuenta(Map<String, String> params) {
		this.params = params;
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
		this.setTitulo(params.get("Titulo"));
		this.setDirectorio("Clientes");
		this.setNombreArchivo("Cuenta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Cuenta", this.params.get("RazonSocial")))
				.add(this.textoParValor("Ruc", this.params.get("Ruc"))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Dirección", this.params.get("Direccion")))
				.add(this.textoParValor("Teléfono", this.params.get("Telefono"))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

/**
 * DataSource de Saldos de Clientes detallado DHS..
 */
class CtaCteSaldosDHSDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, String> totalSaldo;
	double totalSaldo_ = 0;
	
	/**
	 * [0]:emision
	 * [1]:hora
	 * [2]:numero
	 * [3]:concepto
	 * [4]:debe
	 * [5]:haber
	 * [6]:saldo
	 * [7]:razonsocial
	 * [8]:emision
	 */
	public CtaCteSaldosDHSDataSource(List<Object[]> values, String cliente, Map<String, String> totalSaldo) {
		this.values = values;
		this.totalSaldo = totalSaldo;
		for (String key : this.totalSaldo.keySet()) {
			String saldo = this.totalSaldo.get(key);
			totalSaldo_ += Double.parseDouble(saldo.replace(",", "").replace(".", ""));
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		String fecha = (String) det[0];
		String hora = (String) det[1];
		String numero = (String) det[2];
		String concepto = (String) det[3];
		String debe = (String) det[4];
		String haber = (String) det[5];
		String saldo = (String) det[6];
		String razonsocial = (String) det[7];
		
		if ("fecha".equals(fieldName)) {
			value = fecha;
		} else if ("hora".equals(fieldName)) {
			value = hora;
		} else if ("numero".equals(fieldName)) {
			value = numero;
		} else if ("concepto".equals(fieldName)) {
			value = concepto;
		} else if ("debe".equals(fieldName)) {
			value = debe;
		} else if ("haber".equals(fieldName)) {
			value = haber;
		} else if ("saldo".equals(fieldName)) {
			value = saldo;
		} else if ("TituloDetalle".equals(fieldName)) {
			value = razonsocial;
		} else if ("totalimporte".equals(fieldName)) {
			value = this.totalSaldo.get(razonsocial);
		} else if ("TotalSaldo".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalSaldo_);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Saldos de Clientes detallado DHS..
 */
class CtaCteSaldosResumidoDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	double totalVentas = 0.0;
	double totalChequesRechazados = 0.0;
	double totalNotasCredito = 0.0;
	double totalRecibos = 0.0;
	double totalNotasDebito = 0.0;
	double totalChequesReembolsos = 0.0;
	double totalMigracion = 0.0;
	double totalMigracionChequesRechazados = 0.0;
	double totalPrestamos = 0.0;
	double totalReembPrestamos = 0.0;
	
	/**
	 * [0]:cliente
	 * [1]:importe ventas
	 */
	public CtaCteSaldosResumidoDataSource(List<Object[]> values, String cliente, double totalVentas,
			double totalChequesRechazados, double totalNotasCredito, double totalRecibos, double totalNotasDebito,
			double totalChequesReembolsos, double totalMigracion, double totalMigracionChequesRechazados,
			double totalPrestamos, double totalReembPrestamos) {
		this.values = values;
		this.totalVentas = totalVentas;
		this.totalChequesRechazados = totalChequesRechazados;
		this.totalNotasCredito = totalNotasCredito;
		this.totalRecibos = totalRecibos;
		this.totalNotasDebito = totalNotasDebito;
		this.totalChequesReembolsos = totalChequesReembolsos;
		this.totalMigracion = totalMigracion;
		this.totalMigracionChequesRechazados = totalMigracionChequesRechazados;
		this.totalPrestamos = totalPrestamos;
		this.totalReembPrestamos = totalReembPrestamos;
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		String cliente = (String) det[1];
		double ventas = (double) det[2];
		double chequesRechazados = (double) det[3];
		double ncreditos = (double) det[4];
		double recibos = (double) det[5];
		double ndebitos = (double) det[6];
		double reembolsos = (double) det[7];
		double migracion = (double) det[8];
		double migracionChequesRechazados = (double) det[9];
		double prestamos = (double) det[10];
		double reembprestamos = (double) det[11];
		double saldo = ventas + chequesRechazados + ncreditos + recibos + ndebitos + reembolsos + migracion + migracionChequesRechazados + prestamos + reembprestamos;
		
		if ("Cliente".equals(fieldName)) {
			value = cliente;
		} else if ("Ventas".equals(fieldName)) {
			value = Utiles.getNumberFormat(ventas);
		} else if ("ChequesRechazados".equals(fieldName)) {
			value = Utiles.getNumberFormat(chequesRechazados);
		} else if ("NotasCredito".equals(fieldName)) {
			value = Utiles.getNumberFormat(ncreditos);
		} else if ("Recibos".equals(fieldName)) {
			value = Utiles.getNumberFormat(recibos);
		} else if ("NotasDebito".equals(fieldName)) {
			value = Utiles.getNumberFormat(ndebitos);
		} else if ("Reembolsos".equals(fieldName)) {
			value = Utiles.getNumberFormat(reembolsos);
		} else if ("Prestamos".equals(fieldName)) {
			value = Utiles.getNumberFormat(prestamos);
		} else if ("ReembPrestamos".equals(fieldName)) {
			value = Utiles.getNumberFormat(reembprestamos);
		} else if ("Migracion".equals(fieldName)) {
			value = Utiles.getNumberFormat(migracion);
		} else if ("MigracionChequesRechazados".equals(fieldName)) {
			value = Utiles.getNumberFormat(migracionChequesRechazados);
		} else if ("Saldo".equals(fieldName)) {
			value = Utiles.getNumberFormat(saldo);
		} else if ("TotalVentas".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalVentas);
		} else if ("TotalChequesRechazados".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalChequesRechazados);
		} else if ("TotalNotasCredito".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalNotasCredito);
		} else if ("TotalRecibos".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalRecibos);
		} else if ("TotalNotasDebito".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalNotasDebito);
		} else if ("TotalChequesReembolso".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalChequesReembolsos);
		} else if ("TotalMigracion".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalMigracion);
		} else if ("TotalMigracionChequesRechazados".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalMigracionChequesRechazados);
		} else if ("TotalPrestamos".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalPrestamos);
		} else if ("TotalReembPrestamos".equals(fieldName)) {
			value = Utiles.getNumberFormat(totalReembPrestamos);
		} else if ("TotalSaldo".equals(fieldName)) {
			double totalSaldo = totalVentas + totalChequesRechazados + totalNotasCredito + totalRecibos
					+ totalNotasDebito + totalChequesReembolsos + totalMigracion + totalMigracionChequesRechazados
					+ totalPrestamos + totalReembPrestamos;
			value = Utiles.getNumberFormat(totalSaldo);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}

/**
 * DataSource de Saldos de Clientes detallado DHS..
 */
class CtaCteSaldosDHSDataSource_ implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, String> totalSaldo;
	double totalSaldo_ = 0;
	
	double totalVentas = 0;
	double totalChequesRechazados = 0;
	double totalNotasCredito = 0;
	double totalRecibos = 0;
	double totalNotasDebito = 0;
	double totalReembolsosCheques = 0;
	
	/**
	 * [0]:emision
	 * [1]:hora
	 * [2]:numero
	 * [3]:concepto
	 * [4]:debe
	 * [5]:haber
	 * [6]:saldo
	 * [7]:razonsocial
	 * [8]:emision
	 */
	public CtaCteSaldosDHSDataSource_(List<Object[]> values, String cliente, Map<String, String> totalSaldo,
			double totalVentas, double totalChequesRechazados, double totalNotasCredito, double totalRecibos,
			double totalNotasDebito, double totalReembolsosCheques) {
		this.values = values;
		this.totalSaldo = totalSaldo;
		for (String key : this.totalSaldo.keySet()) {
			String saldo = this.totalSaldo.get(key);
			totalSaldo_ += Double.parseDouble(saldo.replace(",", "").replace(".", ""));
		}
		this.totalVentas = totalVentas;
		this.totalChequesRechazados = totalChequesRechazados;
		this.totalNotasCredito = totalNotasCredito;
		this.totalRecibos = totalRecibos;
		this.totalNotasDebito = totalNotasDebito;
		this.totalReembolsosCheques = totalReembolsosCheques;
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);
		String fecha = (String) det[0];
		String hora = (String) det[1];
		String numero = (String) det[2];
		String concepto = (String) det[3];
		String debe = (String) det[4];
		String haber = (String) det[5];
		String saldo = (String) det[6];
		String razonsocial = (String) det[7];
		
		if ("fecha".equals(fieldName)) {
			value = fecha;
		} else if ("hora".equals(fieldName)) {
			value = hora;
		} else if ("numero".equals(fieldName)) {
			value = numero;
		} else if ("concepto".equals(fieldName)) {
			value = concepto;
		} else if ("debe".equals(fieldName)) {
			value = debe;
		} else if ("haber".equals(fieldName)) {
			value = haber;
		} else if ("saldo".equals(fieldName)) {
			value = saldo;
		} else if ("TituloDetalle".equals(fieldName)) {
			value = razonsocial;
		} else if ("totalimporte".equals(fieldName)) {
			value = this.totalSaldo.get(razonsocial);
		} else if ("TotalSaldo".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalSaldo_);
		}  else if ("TotalVentas".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalVentas);
		} else if ("TotalChequesRech".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalChequesRechazados);
		} else if ("TotalNotasCredito".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNotasCredito);
		} else if ("TotalRecibos".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalRecibos);
		} else if ("TotalNotasDebito".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalNotasDebito);
		} else if ("TotalReembolsosCheques".equals(fieldName)) {
			value = Utiles.getNumberFormat(this.totalReembolsosCheques);
		} else if ("TotalMigracion".equals(fieldName)) {
			value = Utiles.getNumberFormat(0.0);
		} else if ("TotalMigracionCh".equals(fieldName)) {
			value = Utiles.getNumberFormat(0.0);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}
