package com.yhaguy.gestion.caja.resumen;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CajaPlanillaResumen;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.gestion.caja.periodo.CajaPeriodoResumenDataSource;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

public class CajaPlanillaResumenVM extends SimpleViewModel {
	
	static final String ZUL_VER_RESUMEN = "/yhaguy/gestion/caja/resumen/caja_planilla_resumen_.zul";
	
	private CajaPlanillaResumen nvoResumen;
	private Object[] selectedResumen;
	private CajaPlanillaResumen selectedResumen_;
	
	private CajaPeriodo selectedPlanilla;
	private BancoBoletaDeposito selectedDeposito;
	private BancoBoletaDeposito selectedDepositoValoresBaterias;
	private BancoBoletaDeposito nvoDeposito = new BancoBoletaDeposito();
	private BancoBoletaDeposito nvoDeposito_ = new BancoBoletaDeposito();
	private BancoBoletaDeposito selectedDeposito_;
	private BancoChequeTercero selectedCheque;
	private BancoChequeTercero selectedCheque_;
	
	private String filterNumeroDeposito = "";
	private String filterNumeroDeposito_ = "";
	private String filterNumeroDepositoValoresBaterias = "";
	private String filterNumeroPlanilla = "";
	private String filterFechaPlanilla = "";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private String filterNumero = "";
	private String filterPlanillas = "";
	
	private String filterNumeroCheque = "";
	
	private int listSize = 0;
	
	private Window win;
	
	@Wire
	private Window win_;
	
	@Wire
	private Listbox lst_res;

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
			this.filterFechaPlanilla = Utiles.getDateToString(Utiles.agregarDias(new Date(), -1), "yyyy-MM-dd");
			//this.inicializarResumen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("nvoResumen")
	public void addResumen_(@BindingParam("parent") Component parent, @BindingParam("comp") Popup comp) 
		throws Exception {
		this.inicializarResumen();
		comp.open(parent, "after_start");
	}
	
	@Command
	@NotifyChange("*")
	public void addDetalle() {
		this.nvoResumen.getPlanillas().add(this.selectedPlanilla);
		this.selectedPlanilla = null;
	}
	
	@Command
	@NotifyChange("nvoResumen")
	public void deleteItem(@BindingParam("item") CajaPeriodo item) {
		this.nvoResumen.getPlanillas().remove(item);
	}
	
	@Command
	@NotifyChange("*")
	public void addDeposito() {
		this.nvoResumen.getDepositos_diferidos().add(this.selectedDeposito);
		this.selectedDeposito = null;
	}
	
	@Command
	@NotifyChange("nvoResumen")
	public void deleteDeposito(@BindingParam("item") BancoBoletaDeposito item) {
		this.nvoResumen.getDepositos_diferidos().remove(item);
	}
	
	@Command
	@NotifyChange({ "selectedResumen_", "nvoDeposito", "nvoDeposito_", "selectedCheque" })
	public void verResumen() throws Exception {
		Clients.showBusy(this.lst_res, "PROCESANDO CAJAS: " + this.selectedResumen[2] + " ...");
		Events.echoEvent("onLater", this.lst_res, null);
	}
	
	/**
	 * ver resumen..
	 */
	public void verResumen_(Component comp1, Component comp2, Include inc) throws Exception {
		this.selectResumen();
		comp1.setVisible(false); 
		comp2.setVisible(false); 
		inc.setVisible(true); 
		inc.setSrc(ZUL_VER_RESUMEN);
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	@NotifyChange({ "selectedResumen_", "nvoDeposito", "nvoDeposito_", "selectedCheque" })
	public void clearProgress(@BindingParam("comp1") Component comp1, 
			@BindingParam("comp2") Component comp2, @BindingParam("inc") Include inc) throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(lst_res);
			}
		});
		timer.setParent(this.win_);
		
		this.verResumen_(comp1, comp2, inc);
	}
	
	/**
	 * seleccion del resumen..
	 */
	private void selectResumen() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance(); 
		long id = (long) this.selectedResumen[3];
		this.selectedResumen_ = (CajaPlanillaResumen) rr.getObject(CajaPlanillaResumen.class.getName(), id);
		this.inicializarDeposito();
		this.inicializarDepositoDiferido();
		this.selectedCheque = null;
	}	
	
	@Command
	@NotifyChange("*")
	public void addResumen(@BindingParam("comp") Popup comp, @BindingParam("comp1") Component comp1, 
			@BindingParam("comp2") Component comp2, @BindingParam("comp3") Component comp3) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String nro = AutoNumeroControl.getAutoNumero("RESUMEN-", 7);
		this.nvoResumen.setNumero(nro);
		this.nvoResumen.setNumeroPlanillas(this.nvoResumen.getNumeroPlanillas_());
		rr.saveObject(this.nvoResumen, this.getLoginNombre());
		this.selectedResumen_ = (CajaPlanillaResumen) rr.getObject(CajaPlanillaResumen.class.getName(), this.nvoResumen.getId());
		this.inicializarResumen();
		this.inicializarDeposito();
		this.inicializarDepositoDiferido();
		this.selectedPlanilla = null;
		comp.close();
		comp1.setVisible(false);
		comp2.setVisible(false);
		comp3.setVisible(true);
		Clients.showNotification(nro + " AGREGADO");
	}
	
	@Command
	@NotifyChange("nvoDeposito")
	public void saveResumen(@BindingParam("comp") Popup comp) throws Exception {
		this.nvoDeposito.setTotalEfectivo(this.selectedResumen_.getResumenEfectivo_());
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.selectedResumen_, this.getLoginNombre());
		if (comp != null) {
			comp.close();
		}
	}
	
	@Command
	public void printResumen() throws Exception {
		this.imprimirResumen(this.selectedResumen_);
	}
	
	@Command
	public void printPlanilla(@BindingParam("planilla") CajaPeriodo planilla) {
		this.imprimirPlanilla(planilla);
	}
	
	@Command
	@NotifyChange("selectedDeposito_")
	public void verDeposito(@BindingParam("comp") Popup comp,
			@BindingParam("ref") Component ref,
			@BindingParam("deposito") BancoBoletaDeposito deposito)  {
		this.selectedDeposito_ = deposito;
		comp.open(ref, "start_before");
	}
	
	@Command
	@NotifyChange("nvoDeposito")
	public void deleteItem_(@BindingParam("item") BancoChequeTercero item) {
		this.nvoDeposito.getCheques().remove(item);
	}
	
	@Command
	@NotifyChange("nvoDeposito_")
	public void _deleteItem(@BindingParam("item") BancoChequeTercero item) {
		this.nvoDeposito_.getCheques().remove(item);
	}
	
	@Command
	@NotifyChange("*")
	public void generarDeposito(@BindingParam("comp") Popup comp) 
		throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvoDeposito.setDbEstado('R');
		this.nvoDeposito.setCerrado(true);
		this.nvoDeposito.setTotalImporte_gs(this.nvoDeposito.getTotalImporteGs());
		ControlBancoMovimiento.addMovimientoDepositoBancario(this.nvoDeposito, this.getLoginNombre());
		rr.saveObject(this.nvoDeposito, this.getLoginNombre());
		
		this.selectedResumen_.getDepositos_generados().add(this.nvoDeposito);
		rr.saveObject(this.selectedResumen_, this.getLoginNombre());
		this.inicializarDeposito();
		
		comp.close();
		Clients.showNotification("Depósito Generado..");
	}
	
	@Command
	@NotifyChange("*")
	public void generarDepositoDiferido(@BindingParam("comp") Popup comp) 
		throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvoDeposito_.setDbEstado('R');
		this.nvoDeposito_.setCerrado(true);
		this.nvoDeposito.setTotalImporte_gs(this.nvoDeposito.getTotalImporteGs());
		ControlBancoMovimiento.addMovimientoDepositoBancario(this.nvoDeposito_, this.getLoginNombre());
		rr.saveObject(this.nvoDeposito_, this.getLoginNombre());
		
		this.selectedResumen_.getDepositos_diferidos().add(this.nvoDeposito_);
		rr.saveObject(this.selectedResumen_, this.getLoginNombre());
		this.inicializarDepositoDiferido();
		
		comp.close();
		Clients.showNotification("Depósito Generado..");
	}
	
	@Command
	@NotifyChange({ "nvoDeposito_", "selectedCheque" })
	public void addCheque() {
		this.nvoDeposito_.getCheques().add(this.selectedCheque);
		this.selectedCheque = null;
	}
	
	@Command
	@NotifyChange({ "nvoDeposito", "selectedCheque_" })
	public void addCheque_() {
		this.nvoDeposito.getCheques().add(this.selectedCheque_);
		this.selectedCheque_ = null;
	}
	
	@Command
	@NotifyChange("*")
	public void asignarValoresBaterias(@BindingParam("comp") Popup comp) throws Exception {
		this.asignarValoresBaterias_();
		comp.close();
		Clients.showNotification("Depósito asignado..");
	}
	
	/**
	 * inicializa el resumen..
	 */
	private void inicializarResumen() throws Exception {
		this.nvoResumen = new CajaPlanillaResumen();
		this.nvoResumen.setFecha(new Date());
		this.nvoResumen.setNumero("RESUMEN-" + AutoNumeroControl.getAutoNumero("RESUMEN-", 7, true));
		/*this.nvoResumen.getPlanillas().addAll(this.getCajaPlanillas());
		List<CajaPeriodo> cajas = new ArrayList<CajaPeriodo>();
		for (CajaPeriodo caja : this.nvoResumen.getPlanillas()) {
			if (caja.isCajaChica() || caja.isCajaPagos()) {
				cajas.add(caja);
			}
		}
		this.nvoResumen.getPlanillas().removeAll(cajas);*/
	}
	
	/**
	 * inicializa el deposito a generar..
	 */
	private void inicializarDeposito() throws Exception {
		this.nvoDeposito = new BancoBoletaDeposito();
		this.nvoDeposito.setFecha(new Date());
		this.nvoDeposito.setNroCuenta(this.getBancoCuentas().get(0));
		this.nvoDeposito.setObservacion("CAJAS: " + this.selectedResumen_.getNumeroPlanillas_());
		this.nvoDeposito.setTotalEfectivo(this.selectedResumen_.getResumenEfectivo_());
		this.nvoDeposito.getCheques().addAll(this.selectedResumen_.getChequesAlDiaSinPrestamosCC());
		this.nvoDeposito.setSucursalApp(this.getSucursalOperativa());
		for (CajaPeriodo planilla : this.selectedResumen_.getPlanillas()) {
			this.nvoDeposito.setPlanillaCaja(this.nvoDeposito.getPlanillaCaja() + ";" + planilla.getNumero());			
		}
	}
	
	/**
	 * inicializa el deposito diferido a generar..
	 */
	private void inicializarDepositoDiferido() throws Exception {
		this.nvoDeposito_ = new BancoBoletaDeposito();
		this.nvoDeposito_.setFecha(new Date());
		this.nvoDeposito_.setNroCuenta(this.getBancoCuentas().get(0));
		this.nvoDeposito_.setObservacion("CAJAS: " + this.selectedResumen_.getNumeroPlanillas_() + " DEPOSITO DIFERIDOS..");
		this.nvoDeposito_.setTotalEfectivo(0);
		this.nvoDeposito_.getCheques().addAll(this.getChequesDiferidosAdepositar());
		this.nvoDeposito_.getCheques().removeAll(this.selectedResumen_.getChequesAlDia());
		this.nvoDeposito_.setSucursalApp(this.getSucursalOperativa());
		for (CajaPeriodo planilla : this.selectedResumen_.getPlanillas()) {
			this.nvoDeposito_.setPlanillaCaja(this.nvoDeposito_.getPlanillaCaja() + ";" + planilla.getNumero());			
		}
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaAA.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaAA.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaAA;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaAA;
		return this.filterFechaAA + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
	}
	
	/**
	 * asigna los valores de baterias..
	 */
	private void asignarValoresBaterias_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedResumen_.getDepositos_valores_bat().add(this.selectedDepositoValoresBaterias);
		rr.saveObject(this.selectedResumen_, this.getLoginNombre());
	}
	
	/**
	 * Despliega el Reporte de Resumen..
	 */
	private void imprimirResumen(CajaPlanillaResumen resumen) throws Exception {		
		String source = ReportesViewModel.SOURCE_RESUMEN_;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ResumenDataSource(resumen);
		params.put("Fecha", Utiles.getDateToString(resumen.getFecha(), Utiles.DD_MM_YYYY));
		params.put("NroResumen", resumen.getNumero());
		params.put("Usuario", getUs().getNombre());
		this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * Despliega el reporte de resumen de planilla de caja..
	 */
	private void imprimirPlanilla(CajaPeriodo planilla) {
		String source = ReportesViewModel.SOURCE_RESUMEN;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new CajaPeriodoResumenDataSource(planilla);
		params.put("Usuario", getUs().getNombre());
		params.put("Periodo", Utiles.getDateToString(planilla.getApertura(), Misc.DD_MM_YYYY)
						+ " AL " + Utiles.getDateToString(planilla.getCierre(), Misc.DD_MM_YYYY));
		params.put("NroPlanilla", planilla.getNumero());
		params.put("Cajero", planilla.getResponsable().getDescripcion());
		this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	private void imprimirComprobante(String source,
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
	 * DataSource del Resumen..
	 */
	class ResumenDataSource implements JRDataSource {
		
		List<MyArray> dets = new ArrayList<MyArray>();

		public ResumenDataSource(CajaPlanillaResumen resumen) {
			for (CajaPeriodo planilla : resumen.getPlanillasVentas()) {
				this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"EFECTIVO", (planilla.getTotalEfectivoIngreso() - planilla.getTotalEfectivoEgreso()),
						planilla.getTotalResumenEfectivo(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero() 
						+ " - " + planilla.getResponsable().getRazonSocial(),
						"NOTAS DE CREDITO CONTADO", planilla.getTotalNotaCreditoContado(),
						planilla.getTotalResumenEfectivo(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"RETENCIONES", planilla.getTotalRetencionesVentas(),
						planilla.getTotalResumenEfectivo(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"CHEQUES AL DIA", planilla.getTotalChequeAlDia(resumen.getFecha()),
						planilla.getTotalResumenEfectivo(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE VENTAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"TRANSFERENCIAS BANCARIAS", planilla.getTotalTransferenciasBancarias(),
						planilla.getTotalResumenEfectivo(resumen.getFecha())));
			
			}
			for (CajaPeriodo planilla : resumen.getPlanillasCobranzas()) {
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(), 
						"EFECTIVO", (planilla.getTotalEfectivoIngreso() - planilla.getTotalReembolsoChequeRechazadoEfectivo()),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero() 
						+ " - " + planilla.getResponsable().getRazonSocial(),
						"REEMBOLSOS PRÉSTAMOS", planilla.getTotalReembolsoPrestamos(),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"RETENCIONES CONTADO", planilla.getTotalRetencionesAlDia(resumen.getFecha()),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"RETENCIONES CRÉDITO", planilla.getTotalRetencionesDiferidos(resumen.getFecha()),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"RECAUDACIÓN CASA CENTRAL", planilla.getTotalRecaudacionCentral(),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));				
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"REEMBOLSOS CHEQUES RECHAZADOS CON EFECTIVO", planilla.getTotalReembolsoChequeRechazadoEfectivo(),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"REEMBOLSOS CHEQUES RECHAZADOS CON CHEQUE DIFERIDO", planilla.getTotalReembolsoChequeRechazadoChequeDiferido(resumen.getFecha()),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"REEMBOLSOS CHEQUES RECHAZADOS CON CHEQUE AL DIA", planilla.getTotalReembolsoChequeRechazadoChequeAldia(resumen.getFecha()),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"CHEQUES AL DÍA", (planilla.getTotalChequeAlDia(resumen.getFecha()) - planilla.getTotalReembolsoChequeRechazadoChequeAldia(resumen.getFecha())),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"CHEQUES DIFERIDOS", planilla.getTotalChequeDiferido(resumen.getFecha()),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"SALDO CLIENTE GENERADO", planilla.getTotalSaldoClienteGenerado(),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"SALDO CLIENTE COBRADO", planilla.getTotalSaldoClienteCobrado(),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
				this.dets.add(new MyArray("CAJA DE COBRANZAS NRO. " + planilla.getNumero()
						 + " - " + planilla.getResponsable().getRazonSocial(),
						"TRANSFERENCIAS BANCARIAS", planilla.getTotalTransferenciasBancarias(),
						planilla.getTotalResumenCobranzasAlDia(resumen.getFecha())));
			
			}	
			for (BancoBoletaDeposito dep : resumen.getDepositos_valores_bat()) {
				for (BancoChequeTercero cheque : dep.getCheques()) {
					this.dets.add(new MyArray("REEMBOLSO VALORES BATERÍAS",
							"CHEQUE NRO. " + cheque.getNumero() + " - " + Utiles.getDateToString(cheque.getFecha(), Utiles.DD_MM_YYYY)
							+ " - " + cheque.getBanco().getDescripcion().toUpperCase(), 
							cheque.getMonto(),
							resumen.getTotalDepositosValoresBat()));
				}
			}
			for (BancoBoletaDeposito dep : resumen.getDepositos_diferidos()) {
				for (BancoChequeTercero cheque : dep.getCheques()) {
					int length = cheque.getBanco().getDescripcion().length();
					this.dets.add(new MyArray("DEPÓSITOS DIFERIDOS",
							"DEP.NRO." + dep.getNumeroBoleta() + " - CH.NRO." + cheque.getNumero() + " " + Utiles.getDateToString(cheque.getFecha(), "dd/MM/yy")
							+ " " + cheque.getBanco().getDescripcion().toUpperCase().substring(0, length >= 6 ? 6 : length) + ".. "
							+ " REC." + cheque.getNumeroRecibo().replace("001-001-", "")
							+ " CAJ." + cheque.getNumeroPlanilla().substring(0, 9).replace("CJP-", ""), 
							cheque.getMonto(),
							resumen.getTotalDepositosDiferidos()));
				}
			}
			for (BancoBoletaDeposito dep : resumen.getDepositos_generados()) {
				for (BancoChequeTercero cheque : dep.getCheques()) {
					int length = cheque.getBanco().getDescripcion().length();
					this.dets.add(new MyArray("DEPÓSITOS GENERADOS",
							"DEP.NRO." + dep.getNumeroBoleta() + " - CH.NRO." + cheque.getNumero() + " " + Utiles.getDateToString(cheque.getFecha(), "dd/MM/yy")
							+ " " + cheque.getBanco().getDescripcion().toUpperCase().substring(0, length >= 6 ? 6 : length) + ".. "
							+ " REC." + cheque.getNumeroRecibo().replace("001-001-", "")
							+ " CAJ." + cheque.getNumeroPlanilla().substring(0, 9).replace("CJP-", ""), 
							cheque.getMonto(),
							resumen.getTotalDepositosGenerados()));
				}
				if (dep.getTotalEfectivo() > 0) {
					this.dets.add(new MyArray("DEPÓSITOS GENERADOS",
							"DEP.NRO." + dep.getNumeroBoleta() + " - EFECTIVO GS.", 
							dep.getTotalEfectivo(),
							resumen.getTotalDepositosGenerados()));
				}
			}
			for (ReciboFormaPago transf : resumen.getTransferenciasBancarias()) {
				this.dets.add(new MyArray("TRANSFERENCIAS BANCARIAS",
						"DEP.NRO." + transf.getDepositoNroReferencia() 
						+ " - BANCO " + transf.getDepositoBancoCta().getBanco().getDescripcion(), 
						transf.getMontoGs(),
						resumen.getResumenTransferenciasBancarias()));
			}
			// Resumen al dia
			this.dets.add(new MyArray("RESUMEN AL DÍA",
					"EFECTIVO", resumen.getResumenEfectivo(), resumen.getTotalResumenAlDia()));
			this.dets.add(new MyArray("RESUMEN AL DÍA",
					"CHEQUES", resumen.getResumenChequeAlDia(), resumen.getTotalResumenAlDia()));
			this.dets.add(new MyArray("RESUMEN AL DÍA",
					"TRANSFERENCIAS BANCARIAS", resumen.getResumenTransferenciasBancarias(), resumen.getTotalResumenAlDia()));
			this.dets.add(new MyArray("RESUMEN AL DÍA",
					"TARJETA", resumen.getResumenTarjeta(), resumen.getTotalResumenAlDia()));
			this.dets.add(new MyArray("RESUMEN AL DÍA",
					"SOBRANTE/FALTANTE", resumen.getSobranteFaltante(), resumen.getTotalResumenAlDia()));
			this.dets.add(new MyArray("RESUMEN AL DÍA",
					"CAJA NO DEPOSITADA (EFECTIVO)", resumen.getEfectivoNoDepositado(), resumen.getTotalResumenAlDia()));
			this.dets.add(new MyArray("RESUMEN AL DÍA",
					"CAJA NO DEPOSITADA (CHEQUES)", resumen.getChequeNoDepositado(), resumen.getTotalResumenAlDia()));
			
			// Resumen adelantado
			this.dets.add(new MyArray("RESUMEN ADELANTADO",
					"CHEQUES DIFERIDOS", resumen.getResumenChequeDiferido(), resumen.getTotalResumenAdelantado()));
			
			// Resumen a depositar
			this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
					"EFECTIVO", resumen.getResumenEfectivo_(), resumen.getTotalADepositar()));
			this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
					"CHEQUE AL DIA", resumen.getResumenChequeAlDiaSinReembolsos(), resumen.getTotalADepositar()));
			this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
					"REEMBOLSO VALORES BATERIAS", resumen.getTotalDepositosValoresBat(), resumen.getTotalADepositar()));
			this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
					"TRANSFERENCIAS BANCARIAS", resumen.getResumenTransferenciasBancarias(), resumen.getTotalADepositar()));
			this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
					"DEPÓSITO CHEQUES DIFERIDOS", resumen.getResumenChequeDiferidoADepositar(), resumen.getTotalADepositar()));
			this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
					"REEMBOLSO CHEQUES RECHAZADOS", resumen.getResumenReembolsoChequesRechazados(), resumen.getTotalADepositar()));
			this.dets.add(new MyArray("RESUMEN A DEPOSITAR",
					"PRESTAMOS CASA CENTRAL (CHEQUE AL DIA)", resumen.getResumenChequeAlDiaPrestamoCC(), resumen.getTotalADepositar()));
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			MyArray item = this.dets.get(index);
			if ("TituloDetalle".equals(fieldName)) {
				value = item.getPos1();
			} else if ("Descripcion".equals(fieldName)) {
				value = item.getPos2();
			} else if ("Importe".equals(fieldName)) {
				double imp = (double) item.getPos3();
				value = Utiles.getNumberFormat(imp);
			} else if ("TotalImporte".equals(fieldName)) {
				double imp = (double) item.getPos4();
				value = Utiles.getNumberFormat(imp);
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < dets.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA",
			"filterNumero", "filterPlanillas" })
	public List<Object[]> getResumenPlanillas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getCajaPlanillaResumenes_(this.getFilterFecha(), this.filterNumero, this.filterPlanillas);
		this.listSize = out.size();
		BindUtils.postNotifyChange(null, null, this, "listSize");
		return out;
	}
	
	@DependsOn("filterNumeroDeposito")
	public List<BancoBoletaDeposito> getBancoDepositos() throws Exception {
		if (this.filterNumeroDeposito.trim().isEmpty()) {
			return new ArrayList<BancoBoletaDeposito>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoDepositos(this.filterNumeroDeposito);
	}
	
	@DependsOn("filterNumeroDeposito_")
	public List<BancoBoletaDeposito> getBancoDepositos_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoDepositos(this.filterNumeroDeposito_);
	}
	
	@DependsOn("filterNumeroDepositoValoresBaterias")
	public List<BancoBoletaDeposito> getBancoDepositosValoresBaterias() throws Exception {
		if (this.filterNumeroDepositoValoresBaterias.trim().isEmpty()) {
			return new ArrayList<BancoBoletaDeposito>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoDepositos(this.filterNumeroDepositoValoresBaterias);
	}
	
	@DependsOn({ "filterNumeroPlanilla", "filterFechaPlanilla" })
	public List<CajaPeriodo> getCajaPlanillas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCajaPlanillas(this.filterNumeroPlanilla, this.filterFechaPlanilla);
	}
	
	@DependsOn("filterNumeroCheque")
	public List<BancoChequeTercero> getCheques() throws Exception {
		if (this.filterNumeroCheque.isEmpty()) {
			return new ArrayList<BancoChequeTercero>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getChequesTercero("", "", "", "",
				"", "", "", "", "", this.filterNumeroCheque, "",
				"", null, null, null, null, null, null,
				null, null, "", "", true);
	}
	
	/**
	 * @return los bancos..
	 */
	public List<BancoCta> getBancoCuentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancosCta();
	}
	
	/**
	 * @return la sucursal.. 
	 */
	public SucursalApp getSucursalOperativa() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Session s = Sessions.getCurrent();
		AccesoDTO acc = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		return rr.getSucursalAppById(acc.getSucursalOperativa().getId());
	}
	
	/**
	 * @return cheques diferidos a depositar..
	 */
	public List<BancoChequeTercero> getChequesDiferidosAdepositar() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getChequesTerceroDiferidosAdepositar(this.selectedResumen_.getFecha());
	}
	
	public CajaPlanillaResumen getNvoResumen() {
		return nvoResumen;
	}

	public void setNvoResumen(CajaPlanillaResumen nvoResumen) {
		this.nvoResumen = nvoResumen;
	}

	public String getFilterNumeroDeposito() {
		return filterNumeroDeposito;
	}

	public void setFilterNumeroDeposito(String filterNumeroDeposito) {
		this.filterNumeroDeposito = filterNumeroDeposito;
	}

	public CajaPeriodo getSelectedPlanilla() {
		return selectedPlanilla;
	}

	public void setSelectedPlanilla(CajaPeriodo selectedPlanilla) {
		this.selectedPlanilla = selectedPlanilla;
	}

	public String getFilterNumeroPlanilla() {
		return filterNumeroPlanilla;
	}

	public void setFilterNumeroPlanilla(String filterNumeroPlanilla) {
		this.filterNumeroPlanilla = filterNumeroPlanilla;
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

	public String getFilterFechaAA() {
		return filterFechaAA;
	}

	public void setFilterFechaAA(String filterFechaAA) {
		this.filterFechaAA = filterFechaAA;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public String getFilterPlanillas() {
		return filterPlanillas;
	}

	public void setFilterPlanillas(String filterPlanillas) {
		this.filterPlanillas = filterPlanillas;
	}

	public int getListSize() {
		return listSize;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public CajaPlanillaResumen getSelectedResumen_() {
		return selectedResumen_;
	}

	public void setSelectedResumen_(CajaPlanillaResumen selectedResumen_) {
		this.selectedResumen_ = selectedResumen_;
	}

	public Object[] getSelectedResumen() {
		return selectedResumen;
	}

	public void setSelectedResumen(Object[] selectedResumen) {
		this.selectedResumen = selectedResumen;
	}

	public BancoBoletaDeposito getSelectedDeposito() {
		return selectedDeposito;
	}

	public void setSelectedDeposito(BancoBoletaDeposito selectedDeposito) {
		this.selectedDeposito = selectedDeposito;
	}

	public String getFilterNumeroDeposito_() {
		return filterNumeroDeposito_;
	}

	public void setFilterNumeroDeposito_(String filterNumeroDeposito_) {
		this.filterNumeroDeposito_ = filterNumeroDeposito_;
	}

	public BancoBoletaDeposito getNvoDeposito() {
		return nvoDeposito;
	}

	public void setNvoDeposito(BancoBoletaDeposito nvoDeposito) {
		this.nvoDeposito = nvoDeposito;
	}

	public BancoBoletaDeposito getSelectedDeposito_() {
		return selectedDeposito_;
	}

	public void setSelectedDeposito_(BancoBoletaDeposito selectedDeposito_) {
		this.selectedDeposito_ = selectedDeposito_;
	}

	public String getFilterFechaPlanilla() {
		return filterFechaPlanilla;
	}

	public void setFilterFechaPlanilla(String filterFechaPlanilla) {
		this.filterFechaPlanilla = filterFechaPlanilla;
	}

	public BancoBoletaDeposito getNvoDeposito_() {
		return nvoDeposito_;
	}

	public void setNvoDeposito_(BancoBoletaDeposito nvoDeposito_) {
		this.nvoDeposito_ = nvoDeposito_;
	}

	public BancoChequeTercero getSelectedCheque() {
		return selectedCheque;
	}

	public void setSelectedCheque(BancoChequeTercero selectedCheque) {
		this.selectedCheque = selectedCheque;
	}

	public String getFilterNumeroCheque() {
		return filterNumeroCheque;
	}

	public void setFilterNumeroCheque(String filterNumeroCheque) {
		this.filterNumeroCheque = filterNumeroCheque;
	}

	public BancoChequeTercero getSelectedCheque_() {
		return selectedCheque_;
	}

	public void setSelectedCheque_(BancoChequeTercero selectedCheque_) {
		this.selectedCheque_ = selectedCheque_;
	}

	public String getFilterNumeroDepositoValoresBaterias() {
		return filterNumeroDepositoValoresBaterias;
	}

	public void setFilterNumeroDepositoValoresBaterias(String filterNumeroDepositoValoresBaterias) {
		this.filterNumeroDepositoValoresBaterias = filterNumeroDepositoValoresBaterias;
	}

	public BancoBoletaDeposito getSelectedDepositoValoresBaterias() {
		return selectedDepositoValoresBaterias;
	}

	public void setSelectedDepositoValoresBaterias(BancoBoletaDeposito selectedDepositoValoresBaterias) {
		this.selectedDepositoValoresBaterias = selectedDepositoValoresBaterias;
	}	
}
