package com.yhaguy.gestion.empresa.ctacte.visor;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
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
	private MyArray cliente;
	private DetalleMovimiento detalle = new DetalleMovimiento();
	private DetalleGroupsModel groupModel;
	
	private int sizeCheques = 0;
	private double totalCheques = 0;
	private boolean fraccionado = false;
	
	private boolean habilitarLinea = false;
	private boolean updateLineaCredito = false;
	private double lineaTemporalGs = 0;
	private String motivo = "";
	private List<HistoricoLineaCredito> historicoLineaCredito;
	
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
	private Popup popDetalleRecibo;
	
	@Wire
	private Popup popMenu;
	
	@Wire
	private Listbox listAplicaciones;
	
	@Wire
	private Listbox listAplicacionesRec;
	
	@Wire
	private Window visorCtaCte;
	
	@Wire
	private Tab tabFac;
	
	@Wire
	private Tab tabRec;
	
	@Init
	public void init() {
		try {
			this.desde = Utiles.getFecha("01-01-2016 00:00:00");
			groupModel = new DetalleGroupsModel(detalle.getAplicaciones(), new DetalleComparator());
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
	@NotifyChange({ "groupModel", "detalle" })
	public void verItems(@BindingParam("item") MyArray item,
			@BindingParam("parent") Component parent) throws Exception {
		this.tabFac.setSelected(true);
		this.tabRec.setSelected(true);
		this.groupModel = new DetalleGroupsModel(new ArrayList<DetalleMovimiento>(), new DetalleComparator());
		this.selectedItem_ = item;
		this.detalle = new DetalleMovimiento();
		this.detalle.setEmision((Date) item.getPos1());
		this.detalle.setVencimiento((Date) item.getPos2());
		this.detalle.setNumero(String.valueOf(item.getPos4()));
		this.detalle.setSigla(String.valueOf(item.getPos8()));
		this.detalle.setTipoMovimiento(String.valueOf(item.getPos3()));
		this.setDetalles(item);
		if (this.isRecibo(String.valueOf(item.getPos8()))) {
			this.popDetalleRecibo.open(parent, "start_before");
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
			rr.saveObject(cli, this.getLoginNombre());
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
	
	/**
	 * @return el cliente convertido a myarray..
	 */
	private MyArray clienteToMyArray(Cliente cliente) {
		MyArray out = new MyArray();
		out.setId(cliente.getId());
		out.setPos1(cliente.getLimiteCredito());
		out.setPos2(cliente.isVentaCredito());
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
		}		
		this.detalle.setDetalles(detalles);
		this.detalle.setFormasPago(formasPago);
	}
	
	/**
	 * @return las aplicaciones del movimiento..
	 */
	private List<DetalleMovimiento> getAplicaciones(MyArray item,
			DetalleMovimiento movim) throws Exception {
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
			out.add(det);
		}
		
		// ventas
		if (this.isVenta(sigla)) {
			vta = (Venta) rr.getObject(Venta.class.getName(), idmovimiento);
			movim.setNumero(vta.getNumero());
			movim.setImporteGs(vta.getTotalImporteGs());
			movim.setDescripcion(movim.getTipoMovimiento() + " - " + movim.getNumero());
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
		if (!this.isReciboCobro(sigla)) {
			out.add(movim);
			// ordena la lista segun fecha..
			Collections.sort(out, new Comparator<DetalleMovimiento>() {
				@Override
				public int compare(DetalleMovimiento o1, DetalleMovimiento o2) {
					Date fecha1 = o1.getEmision();
					Date fecha2 = o2.getEmision();
					return fecha1.compareTo(fecha2);
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
		emp.setObservacion(this.selectedItem.getPos11().toString().toUpperCase());
		rr.saveObject(emp, this.getLoginNombre());
		Clients.showNotification("Observacion registrada..!");
	}
	
	/**
	 * impresion del reporte..
	 */
	private void imprimir_(boolean mobile) throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();

		for (MyArray mov : this.getMovimientos()) {
			Date emision = (Date) mov.getPos1();
			Date vto = (Date) mov.getPos2();
			String nro = (String) mov.getPos4();
			Object[] obj = new Object[] {
					m.dateToString(emision, Misc.DD_MM_YYYY),
					m.dateToString(vto, Misc.DD_MM_YYYY),
					mov.getPos3(), nro.replace("(1/1)", "").replace("(1/3)", "").replace("(2/3)", "").replace("(3/3)", ""),
					mov.getPos5(), mov.getPos6() };
			data.add(obj);
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
			
			List<Object[]> data = new ArrayList<Object[]>();
			List<Object[]> historico;
			List<Object[]> historicoDEBE;
			List<Object[]> historicoHABER;
			Map<String, String> totalSaldo = new HashMap<String, String>();

			long idCliente = cliente != null ? cliente.getId() : 0;

			List<Object[]> ventas = rr.getVentasPorCliente_(idCliente, desde, hasta);
			List<Object[]> cheques_rechazados = rr.getChequesRechazadosPorCliente(idCliente, desde, hasta);
			List<Object[]> prestamos_cc = rr.getPrestamosCC(idCliente, desde, hasta);
			List<Object[]> ntcsv = rr.getNotasCreditoPorCliente(idCliente, desde, hasta);
			List<Object[]> recibos = rr.getRecibosPorCliente(idCliente, desde, hasta);
			List<Object[]> reembolsos_cheques_rechazados = rr.getReembolsosChequesRechazadosPorCliente(idCliente, desde, hasta);
			List<Object[]> reembolsos_prestamos_cc = rr.getReembolsosPrestamosCC(idCliente, desde, hasta);

			historicoDEBE = new ArrayList<Object[]>();
			historicoHABER = new ArrayList<Object[]>();
			
			historicoDEBE.addAll(ventas);
			historicoDEBE.addAll(cheques_rechazados);
			historicoDEBE.addAll(prestamos_cc);
			
			historicoHABER.addAll(ntcsv);
			historicoHABER.addAll(recibos);
			historicoHABER.addAll(reembolsos_cheques_rechazados);
			historicoHABER.addAll(reembolsos_prestamos_cc);

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
			String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_SALDO_DET_DHS;
			String titulo = "SALDOS DE CLIENTES DETALLADO (HISTORIAL A UNA FECHA)";
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new CtaCteSaldosDHSDataSource(data, cli, totalSaldo);
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
	 * Despliega el reporte en un pdf para su impresion..
	 */
	public void imprimirJasper(String source, Map<String, Object> parametros,
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
		List<Empresa> arts = rr.getEmpresas(this.ruc, this.cedula, this.razonSocial, this.nombreFantasia);

		return this.empresasToMyArray(arts);
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
			my.setPos7(emp.getDireccion());
			my.setPos8(emp.getTelefono());
			my.setPos9(emp.getAuxi().equals(Empresa.DESBLOQUEO_TEMPORAL) ? true : false); // true si es desbloqueo temporal..
			my.setPos10(emp.getVendedor() != null? emp.getVendedor().getRazonSocial() : "");
			my.setPos11(emp.getObservacion().toUpperCase());
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
		return rr.getTareasProgramadasPendientes(this.selectedItem.getId());
	}
		
	/**
	 * @return los movimientos de cta cte convertidos a myarray..
	 */
	private List<MyArray> movimientosToMyArray(List<CtaCteEmpresaMovimiento> movs, String concepto, String numero, String numeroImportacion) {
		List<MyArray> out = new ArrayList<MyArray>();
		for (CtaCteEmpresaMovimiento mov : movs) {
			MyArray my = new MyArray();
			my.setPos1(mov.getFechaEmision());
			my.setPos2(mov.getFechaVencimiento());
			my.setPos3(mov.getTipoMovimiento().getDescripcion());
			my.setPos4(mov.getNroComprobante());
			my.setPos5(mov.getImporteOriginalFinal());
			my.setPos6(mov.getSaldoFinal());	
			my.setPos7(mov.isVencido());
			my.setPos8(mov.getTipoMovimiento().getSigla());
			my.setPos9(mov.getIdMovimientoOriginal());
			my.setPos10(mov.getNumeroImportacion().isEmpty() ? "- - -" : mov.getNumeroImportacion());
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
		private String numero;
		private String numeroPlanilla;
		private String facturaAplicada;
		private String sigla;
		private String tipoMovimiento;
		private String motivo;
		private List<MyArray> detalles;
		private List<MyArray> formasPago;
		private String agrupador;
		private String descripcion;
		
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
			return this.isVentaCredito() ? this.importeGs : 0.0;
		}
		
		/**
		 * @return el haber..
		 */
		public double getHaber() {
			return this.isNotaCredito() || this.isReciboCobro() ? this.importeGs : 0.0;
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
	 * @return las monedas..
	 */
	public List<String> getMonedas() {
		List<String> out = new ArrayList<String>();
		out.add(CTA_GS);
		out.add(CTA_DS);
		return out;
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
}

/**
 * Reporte de Presupuesto..
 */
class ReporteEstadoCuenta extends ReporteYhaguy {
	
	private Map<String, String> params;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Emisión", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Vto.", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Número", TIPO_STRING, 70);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, 50);
	static DatosColumnas col6 = new DatosColumnas("Saldo", TIPO_DOUBLE_GS, 50, true);
	
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
