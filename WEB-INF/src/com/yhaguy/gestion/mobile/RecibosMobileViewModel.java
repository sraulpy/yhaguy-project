package com.yhaguy.gestion.mobile;

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
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Vlayout;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TareaProgramada;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.process.ProcesosHistoricos;
import com.yhaguy.util.Utiles;

public class RecibosMobileViewModel extends SimpleViewModel {

	private Empresa selectedEmpresa;
	private Funcionario selectedCobrador;
	private List<ReciboDetalle> selectedDetalles = new ArrayList<ReciboDetalle>();
	private List<ReciboFormaPago> selectedFormasPagos = new ArrayList<ReciboFormaPago>();
	private List<ReciboDetalle> movimientos = new ArrayList<ReciboDetalle>();
	private Tipo selectedTipoFormaPago;
	private ReciboFormaPago nvoFormaPago = new ReciboFormaPago();
	private Tipo selectedMoneda;
	private Tipo selectedMonedaFormaPago;
	private double tipoCambio = 0;
	
	private TipoMovimiento selectedConcepto;
	private TareaProgramada selectedTarea;
	
	private String razonSocial = "";
	private String razonSocialCobrador = "";
	
	private String mensaje = "";
	
	private Date desde;
	private Date hasta = new Date();
	
	@Init(superclass = true)
	public void init() {
		try {
			this.desde = Utiles.getFecha("01-01-2016 00:00:00");
			RegisterDomain rr = RegisterDomain.getInstance();
			this.tipoCambio = rr.getTipoCambioCompra();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void refresh() {
		BindUtils.postNotifyChange(null, null, this, "totalImporteGs");
		BindUtils.postNotifyChange(null, null, this, "totalImporteDs");
	}
	
	@Command
	public void selectConcepto(@BindingParam("comp1") Vlayout comp1, @BindingParam("rec") Vlayout rec,
			@BindingParam("ant") Vlayout ant, @BindingParam("ree") Vlayout ree) {
		if (this.isRecibo()) rec.setVisible(true);
		if (this.isAnticipo()) ant.setVisible(true);
		if (this.isReembolsoCheque()) ree.setVisible(true);
		comp1.setVisible(false);
	}
	
	@Command
	public void setMonto() {
		if (this.selectedMoneda.getSigla().equals(Configuracion.SIGLA_MONEDA_GUARANI)) {
			this.tipoCambio = 1;
		}
		for (ReciboDetalle item : this.selectedDetalles) {
			if (item.getMontoGs() == 0) {
				item.setMontoGs(item.getMovimiento().getSaldo());
				if (this.selectedMoneda.getSigla().equals(Configuracion.SIGLA_MONEDA_DOLAR)) {
					item.setMontoDs(item.getMovimiento().getSaldo());
					item.setMontoGs(item.getMovimiento().getSaldo() * item.getMovimiento().getTipoCambio());
				}
				BindUtils.postNotifyChange(null, null, item, "montoGs");
				BindUtils.postNotifyChange(null, null, item, "montoDs");
			}
		}
		for (ReciboDetalle item : this.movimientos) {
			if (!this.selectedDetalles.contains(item)) {
				item.setMontoGs(0);
				item.setMontoDs(0);
				item.setSelected(false);
				BindUtils.postNotifyChange(null, null, item, "selected");
				BindUtils.postNotifyChange(null, null, item, "montoGs");
				BindUtils.postNotifyChange(null, null, item, "montoDs");
			} else {
				item.setSelected(true);
				BindUtils.postNotifyChange(null, null, item, "selected");
			}
		}
	}
	
	@Command
	@NotifyChange("*")
	public void seleccionarFormaPago(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) 
			throws Exception {
		this.inicializarFormaPago();
		this.nvoFormaPago.setTipo(this.selectedTipoFormaPago);
		if (this.nvoFormaPago.isRetencion()) {
			this.calcularRetencion();
		} else {
			this.nvoFormaPago.setMontoGs(this.getTotalImporteGs() - this.getTotalImporteFormasPagoGs());
			this.nvoFormaPago.setMontoDs(this.getTotalImporteDs() - this.getTotalImporteFormasPagoDs());
			if (this.selectedMoneda.getSigla().equals(Configuracion.SIGLA_MONEDA_DOLAR)) {
				this.nvoFormaPago.setMontoGs(this.getTotalImporteGs_() - this.getTotalImporteFormasPagoGs());
			}
		}
		comp1.setVisible(true);
		comp2.setVisible(false);
		comp3.setVisible(false);
		comp4.setVisible(true);
		this.selectedMonedaFormaPago = null;
	}
	
	@Command
	@NotifyChange("*")
	public void seleccionarFormaPago_(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2) 
			throws Exception {
		this.inicializarFormaPago();
		this.nvoFormaPago.setTipo(this.selectedTipoFormaPago);
		if (this.nvoFormaPago.isRetencion()) {
			this.calcularRetencion();
		} else {
			this.nvoFormaPago.setMontoGs(this.getTotalImporteGs() - this.getTotalImporteFormasPagoGs());
			this.nvoFormaPago.setMontoDs(this.getTotalImporteDs() - this.getTotalImporteFormasPagoDs());
			if (this.selectedMoneda.getSigla().equals(Configuracion.SIGLA_MONEDA_DOLAR)) {
				this.nvoFormaPago.setMontoGs(this.getTotalImporteGs_() - this.getTotalImporteFormasPagoGs());
			}
		}
		comp1.setVisible(true);
		comp2.setVisible(false);
		this.selectedMonedaFormaPago = null;
	}
	
	@Command
	@NotifyChange("*")
	public void agregarFormaPago(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) 
			throws Exception {
		this.nvoFormaPago.setMontoChequeGs(this.nvoFormaPago.getMontoGs());
		this.selectedFormasPagos.add(this.nvoFormaPago);
		comp1.setVisible(false);
		comp2.setVisible(false);
		comp3.setVisible(true);
		comp4.setVisible(true);
	}
	
	@Command
	@NotifyChange("*")
	public void generarRecibo(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) throws Exception {		
		if (this.isDatosValidos() == false) {
			comp1.setVisible(false);
			comp2.setVisible(false);
			comp3.setVisible(true);
			comp4.setVisible(true);
			return;
		}		
		CajaPeriodo caja = this.getCajaPlanilla();		
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo recibo = new Recibo();
		recibo.setNombreUsuarioCarga("mobile");
		String numero = AutoNumeroControl.getAutoNumero(Configuracion.NRO_RECIBO_COBRO_MOBILE, 7);
		recibo.setNumero("002-002"+ "-" + numero);
		recibo.setNro(Long.parseLong(numero));
		recibo.setNumeroPlanilla(caja != null ? caja.getNumero() : "");
		recibo.setNumeroRecibo("");
		recibo.setCliente(rr.getClienteByEmpresa(this.selectedEmpresa.getId()));
		recibo.setCobroExterno(false);
		recibo.setCobrador(this.selectedCobrador.getRazonSocial());
		recibo.getDetalles().addAll(this.selectedDetalles);
		recibo.setEntregado(false);
		recibo.setEstadoComprobante(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CONFECCIONADO));
		recibo.setFechaEmision(new Date());
		recibo.getFormasPago().addAll(this.selectedFormasPagos);
		recibo.setMoneda(this.selectedMoneda);
		recibo.setSucursal(rr.getSucursalAppById(2));
		recibo.setTipoCambio(this.tipoCambio);
		recibo.setTipoMovimiento(this.selectedConcepto);
		recibo.setTotalImporteGs(this.getTotalImporteFormasPagoGs());
		recibo.setTotalImporteDs(this.getTotalImporteFormasPagoDs());
		recibo.setMovimientoBancoActualizado(true);
		if (!recibo.isMonedaLocal() && this.isRecibo()) {
			ReciboDetalle det = new ReciboDetalle();
			det.setAuxi(ReciboDetalle.TIPO_DIF_CAMBIO);
			det.setMontoGs(this.getTotalImporteFormasPagoGs() - this.getTotalImporteGs());
			det.setConcepto("DIFERENCIA POR TIPO DE CAMBIO");
			recibo.getDetalles().add(det);
		}
		rr.saveObject(recibo, "mobile");
		this.addMovimientoCtaCte(recibo, "mobile");
		this.addMovimientosBanco(recibo, "mobile");
		ProcesosHistoricos.updateHistoricoCobranzaMeta(recibo.getTotalImporteGsSinIva());
		ProcesosHistoricos.updateHistoricoCobranzaDiaria(new Date(), recibo.getTotalImporteGsSinIva());
		if (caja != null) {
			caja.getRecibos().add(recibo);
			rr.saveObject(caja, "mobile");
		}
		if (this.selectedTarea != null) {
			this.selectedTarea.setRealizado(true);
			this.selectedTarea.setRealizadoPor("mobile: " + Utiles.getDateToString(new Date(), "dd-MM-yyyy hh:mm:ss"));
			rr.saveObject(this.selectedTarea, "mobile");
		}
		
		this.mensaje = this.selectedConcepto.getDescripcion() + " " + recibo.getNumero() + " CORRECTAMENTE GENERADO..";
		Clients.showNotification(this.selectedConcepto.getDescripcion() + " GENERADO..!");
		comp1.setVisible(false);
		comp2.setVisible(false);
		comp3.setVisible(true);
		comp4.setVisible(true);
		this.selectedDetalles = new ArrayList<ReciboDetalle>();
		this.selectedFormasPagos = new ArrayList<ReciboFormaPago>();
		this.razonSocial = "";
		this.selectedConcepto = null;
	}
	
	/**
	 * @return los movimientos de cta cte de la empresa seleccionada..
	 */
	@Command
	@NotifyChange("movimientos")
	public void buscarMovimientos() throws Exception {
		this.movimientos.clear();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CtaCteEmpresaMovimiento> list = rr.getMovimientosConSaldo(
				this.desde, this.hasta, Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE, 0,
				this.selectedEmpresa.getId(), this.selectedMoneda.getId());
		List<CtaCteEmpresaMovimiento> list_ = this.getMovimientosDesfraccionados(list);
		for (CtaCteEmpresaMovimiento item : list_) {
			ReciboDetalle det = new ReciboDetalle();
			det.setMontoGs(0);
			det.setMontoDs(0);
			det.setMovimiento(item);
			this.movimientos.add(det);
		}
	}
	
	@Command
	@NotifyChange({ "nvoFormaPago.montoGs", "totalImporteGs", "totalImporteDs", "totalImporteFormasPagoGs_", "totalImporteFormasPagoDs_" })
	public void guaranizar() {
		this.nvoFormaPago.setMontoGs(this.nvoFormaPago.getMontoDs() * this.tipoCambio);
	}
	
	@Command
	@NotifyChange({ "nvoFormaPago.montoDs", "totalImporteDs", "totalImporteGs", "totalImporteFormasPagoGs_", "totalImporteFormasPagoDs_" })
	public void dolarizar() {
		this.nvoFormaPago.setMontoDs(this.nvoFormaPago.getMontoGs() / this.tipoCambio);
	}
	
	/**
	 * inicializa los valores de la forma de pago..
	 */
	private void inicializarFormaPago() throws Exception {
		this.nvoFormaPago = new ReciboFormaPago();
		this.nvoFormaPago.setDescripcion("");
		this.nvoFormaPago.setChequeLibrador("");
		this.nvoFormaPago.setChequeNumero("");
		this.nvoFormaPago.setDepositoNroReferencia("");
		this.nvoFormaPago.setDescripcion("");
		this.nvoFormaPago.setNroComprobanteAsociado("");
		this.nvoFormaPago.setReciboDebitoNro("");
		this.nvoFormaPago.setRetencionNumero("VIRTUAL");
		this.nvoFormaPago.setRetencionTimbrado("SIN TIMBRADO");
		this.nvoFormaPago.setRetencionVencimiento(Utiles.getFechaFinMes());
		this.nvoFormaPago.setTarjetaNumero("");
		this.nvoFormaPago.setTarjetaNumeroComprobante("");
		this.nvoFormaPago.setMoneda(this.selectedMonedaFormaPago);
		this.nvoFormaPago.setIdSucursal(2);
		if (this.selectedEmpresa != null) {
			this.nvoFormaPago.setChequeLibrador(this.selectedEmpresa.getRazonSocial());
		}
	}
	
	/**
	 * calcula el monto de retencion
	 */
	private void calcularRetencion() throws Exception {
		double total = this.getTotalImporteGs();
		double iva = this.m.calcularIVA(total, Configuracion.VALOR_IVA_10);
		double ret = this.m.obtenerValorDelPorcentaje(iva, Configuracion.PORCENTAJE_RETENCION);
		this.nvoFormaPago.setMontoGs(ret);
	}
	
	/**
	 * agrega los movimientos de banco..
	 */
	private void addMovimientosBanco(Recibo rec, String user) 
		throws Exception {
		for (ReciboFormaPago fp : rec.getFormasPago()) {
			if (fp.isChequeTercero()) {
				ControlBancoMovimiento.addChequeTercero(fp, rec, user);
			}
		}
	}
	
	/**
	 * agrega los movimientos de recibo..
	 */
	private void addMovimientoCtaCte(Recibo rec, String user) 
		throws Exception {
		ControlCuentaCorriente.addReciboDeCobro(rec.getId(), user);
		ControlCuentaCorriente.verificarBloqueoCliente(rec.getCliente().getIdEmpresa(), rec.getCliente().getId(), user);
	}
	
	/**
	 * @return los movimientos sin fraccionar en cuotas..
	 */
	private List<CtaCteEmpresaMovimiento> getMovimientosDesfraccionados(List<CtaCteEmpresaMovimiento> list) {
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
		return aux;	
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn("razonSocial")
	public List<Empresa> getEmpresas() throws Exception {
		if (this.razonSocial.isEmpty()) {
			return new ArrayList<Empresa>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresas("", "", this.razonSocial, "");
	}
	
	/**
	 * @return los conceptos..
	 */
	public List<TipoMovimiento> getConceptos() throws Exception {
		List<TipoMovimiento> out = new ArrayList<TipoMovimiento>();
		RegisterDomain rr = RegisterDomain.getInstance();
		TipoMovimiento rec = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_RECIBO_COBRO);
		TipoMovimiento ant = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
		TipoMovimiento ree = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_CANCELACION_CHEQ_RECHAZADO);
		out.add(rec);
		out.add(ant);
		out.add(ree);
		return out;
	}
	
	/**
	 * @return las tareas programadas..
	 */
	@DependsOn({ "selectedEmpresa" })
	public List<TareaProgramada> getTareas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.agregarDias(new Date(), -30);
		if (this.selectedEmpresa != null) {
			List<TareaProgramada> tareas = rr.getTareasProgramadasPendientes(desde, this.hasta, "PASAR A COBRAR", this.selectedEmpresa.getId());			
			return tareas;
		}
		return new ArrayList<TareaProgramada>();
	}
	
	@DependsOn("selectedConcepto")
	public boolean isRecibo() {
		if (this.selectedConcepto == null) return false;
		return this.selectedConcepto.getSigla().equals(Configuracion.SIGLA_TM_RECIBO_COBRO);
	}
	
	@DependsOn("selectedConcepto")
	public boolean isAnticipo() {
		if (this.selectedConcepto == null) return false;
		return this.selectedConcepto.getSigla().equals(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
	}
	
	@DependsOn("selectedConcepto")
	public boolean isReembolsoCheque() {
		if (this.selectedConcepto == null) return false;
		return this.selectedConcepto.getSigla().equals(Configuracion.SIGLA_TM_CANCELACION_CHEQ_RECHAZADO);
	}
	
	@DependsOn("selectedDetalles")
	public double getTotalImporteGs() throws Exception {
		double out = 0;
		for (ReciboDetalle item : this.selectedDetalles) {
			out += item.getMontoGs();
		}
		return out;
	}
	
	@DependsOn("selectedDetalles")
	public double getTotalImporteDs() throws Exception {
		double out = 0;
		for (ReciboDetalle item : this.selectedDetalles) {
			out += item.getMontoDs();
		}
		return out;
	}
	
	@DependsOn("selectedDetalles")
	public double getTotalImporteGs_() throws Exception {
		double out = 0;
		for (ReciboDetalle item : this.selectedDetalles) {
			out += (item.getMontoDs() * this.tipoCambio);
		}
		return out;
	}
	
	/**
	 * @return las monedas..
	 */
	public List<Tipo> getMonedas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Tipo> out = new ArrayList<Tipo>();
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR));
		return out;
	}
	
	/**
	 * @return la caja de cobranzas del dia..
	 */
	private CajaPeriodo getCajaPlanilla() throws Exception {
		String fecha = Utiles.getDateToString(new Date(), "yyyy-MM-dd");
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CajaPeriodo> cajas = rr.getCajaPlanillas_("", fecha);
		for (CajaPeriodo caja : cajas) {
			if (this.selectedCobrador.isCobradorInterior()) {
				if (caja.isCajaCobrosMobileInterior() && caja.isAbierto()) {
					return caja;
				}
			} else {
				if (caja.isCajaCobrosMobile() && caja.isAbierto()) {
					return caja;
				}
			}			
		}
		return null;
	}
	
	/**
	 * @return las formas de pago..
	 */
	public List<Tipo> getFormasDePago() throws Exception {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_EFECTIVO));
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO));
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO));
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_RECAUDACION_CENTRAL));
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_RETENCION));
		return out;
	}
	
	/**
	 * @return los cobradores..
	 */
	public List<Funcionario> getCobradores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionariosCobradores();
	}
	
	@DependsOn("selectedTipoFormaPago")
	public boolean isEfectivo() {
		if (this.selectedTipoFormaPago == null) {
			return false;
		}
		return this.selectedTipoFormaPago.getSigla().equals(Configuracion.SIGLA_FORMA_PAGO_EFECTIVO);
	}
	
	@DependsOn("selectedTipoFormaPago")
	public boolean isChequeTercero() {
		if (this.selectedTipoFormaPago == null) {
			return false;
		}
		return this.selectedTipoFormaPago.getSigla().equals(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO);
	}
	
	@DependsOn("selectedTipoFormaPago")
	public boolean isDepositoBancario() {
		if (this.selectedTipoFormaPago == null) {
			return false;
		}
		return this.selectedTipoFormaPago.getSigla().equals(Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO);
	}
	
	@DependsOn("selectedTipoFormaPago")
	public boolean isRetencion() {
		if (this.selectedTipoFormaPago == null) {
			return false;
		}
		return this.selectedTipoFormaPago.getSigla().equals(Configuracion.SIGLA_FORMA_PAGO_RETENCION);
	}
	
	@DependsOn("selectedTipoFormaPago")
	public boolean isRecaudacionCC() {
		if (this.selectedTipoFormaPago == null) {
			return false;
		}
		return this.selectedTipoFormaPago.getSigla().equals(Configuracion.SIGLA_FORMA_PAGO_RECAUDACION_CENTRAL);
	}
	
	/**
	 * @return el numero de recibo preliminar..
	 */
	public String getNumero() throws Exception {
		return "001-001"+ "-" +
				AutoNumeroControl.getAutoNumero(Configuracion.NRO_RECIBO_COBRO_MOBILE, 7, true);
	}
	
	/**
	 * @return el total importe formas pago..
	 */
	public double getTotalImporteFormasPagoGs() {
		double out = 0;
		for (ReciboFormaPago fp : this.selectedFormasPagos) {
			out += fp.getMontoGs();
		}
		return out;
	}
	
	/**
	 * @return el total importe formas pago..
	 */
	public double getTotalImporteFormasPagoDs() {
		double out = 0;
		for (ReciboFormaPago fp : this.selectedFormasPagos) {
			out += fp.getMontoDs();
		}
		return out;
	}
	
	/**
	 * @return el total importe formas pago como subtotal..
	 */
	@DependsOn("nvoFormaPago.montoGs")
	public double getTotalImporteFormasPagoGs_() {
		return this.getTotalImporteFormasPagoGs() + this.nvoFormaPago.getMontoGs();
	}
	
	/**
	 * @return el total importe formas pago como subtotal..
	 */
	@DependsOn("nvoFormaPago.montoDs")
	public double getTotalImporteFormasPagoDs_() {
		return this.getTotalImporteFormasPagoDs() + this.nvoFormaPago.getMontoDs();
	}
	
	/**
	 * @return los bancos..
	 */
	public List<Tipo> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_BANCOS_TERCEROS);
	}
	
	/**
	 * @return los bancos con apertura de cuenta..
	 */
	public List<BancoCta> getBancosCuentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancosCta();
	}
	
	/**
	 * @return la validacion de datos..
	 */
	private boolean isDatosValidos() throws Exception {
		boolean out = true;
		if (this.selectedMoneda.getSigla().equals(Configuracion.SIGLA_MONEDA_DOLAR)) {
			return true;
		}
		this.mensaje = "NO SE PUDO GENERAR EL RECIBO DEBIDO A: \n";
		double dif = this.getTotalImporteGs() - this.getTotalImporteFormasPagoGs();
		if (this.isRecibo() && (dif > 100 || dif < -100)) {
			out = false;
			this.mensaje += "\n EL TOTAL DE FACTURAS Y FORMAS DE PAGO NO COINCIDEN..";
		}
		return out;
	}
	
	@DependsOn({ "nvoFormaPago.chequeBanco", "nvoFormaPago.chequeFecha", "nvoFormaPago.chequeNumero", "nvoFormaPago.montoGs" })
	public boolean isAgregarFormaPagoEnabled() {
		if (this.isChequeTercero()) {
			if (this.nvoFormaPago.getChequeBanco() != null && this.nvoFormaPago.getChequeFecha() != null
					&& !this.nvoFormaPago.getChequeNumero().isEmpty() && this.nvoFormaPago.getMontoGs() != 0) {
				return true;
			}
		} else {
			if (this.nvoFormaPago.getMontoGs() != 0) {
				return true;
			}
		}
		return false;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public Empresa getSelectedEmpresa() {
		return selectedEmpresa;
	}

	public void setSelectedEmpresa(Empresa selectedEmpresa) {
		this.selectedEmpresa = selectedEmpresa;
	}

	public List<ReciboDetalle> getSelectedDetalles() {
		return selectedDetalles;
	}

	public void setSelectedDetalles(List<ReciboDetalle> selectedDetalles) {
		this.selectedDetalles = selectedDetalles;
	}

	public void setMovimientos(List<ReciboDetalle> movimientos) {
		this.movimientos = movimientos;
	}

	public List<ReciboDetalle> getMovimientos() {
		return movimientos;
	}

	public Tipo getSelectedTipoFormaPago() {
		return selectedTipoFormaPago;
	}

	public void setSelectedTipoFormaPago(Tipo selectedTipoFormaPago) {
		this.selectedTipoFormaPago = selectedTipoFormaPago;
	}

	public List<ReciboFormaPago> getSelectedFormasPagos() {
		return selectedFormasPagos;
	}

	public void setSelectedFormasPagos(List<ReciboFormaPago> selectedFormasPagos) {
		this.selectedFormasPagos = selectedFormasPagos;
	}

	public ReciboFormaPago getNvoFormaPago() {
		return nvoFormaPago;
	}

	public void setNvoFormaPago(ReciboFormaPago nvoFormaPago) {
		this.nvoFormaPago = nvoFormaPago;
	}

	public String getRazonSocialCobrador() {
		return razonSocialCobrador;
	}

	public void setRazonSocialCobrador(String razonSocialCobrador) {
		this.razonSocialCobrador = razonSocialCobrador;
	}

	public Funcionario getSelectedCobrador() {
		return selectedCobrador;
	}

	public void setSelectedCobrador(Funcionario selectedCobrador) {
		this.selectedCobrador = selectedCobrador;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Tipo getSelectedMoneda() {
		return selectedMoneda;
	}

	public void setSelectedMoneda(Tipo selectedMoneda) {
		this.selectedMoneda = selectedMoneda;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public Tipo getSelectedMonedaFormaPago() {
		return selectedMonedaFormaPago;
	}

	public void setSelectedMonedaFormaPago(Tipo selectedMonedaFormaPago) {
		this.selectedMonedaFormaPago = selectedMonedaFormaPago;
	}

	public TipoMovimiento getSelectedConcepto() {
		return selectedConcepto;
	}

	public void setSelectedConcepto(TipoMovimiento selectedConcepto) {
		this.selectedConcepto = selectedConcepto;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public TareaProgramada getSelectedTarea() {
		return selectedTarea;
	}

	public void setSelectedTarea(TareaProgramada selectedTarea) {
		this.selectedTarea = selectedTarea;
	}
}
