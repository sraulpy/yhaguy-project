package com.yhaguy.gestion.caja.recibos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.cheques.BancoChequeDTO;
import com.yhaguy.gestion.bancos.cheques.WindowCheque;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.caja.periodo.CajaPeriodoControlBody;
import com.yhaguy.gestion.empresa.ctacte.AssemblerCtaCteEmpresaMovimiento;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;
import com.yhaguy.util.Utiles;

public class ReciboSimpleControl extends SoloViewModel {
	
	static final String ADD_ITEM_ZUL = "/yhaguy/gestion/caja/recibos/insertarItem.zul";
	static final String SALDO_A_FAVOR_ZUL = "/yhaguy/gestion/caja/recibos/saldoafavor.zul";
	
	private CajaPeriodoControlBody dato = new CajaPeriodoControlBody();	
	private ReciboDetalleDTO nvoItem;
	private ReciboDetalleDTO nvoItem_dif_cambio;
	
	private String filterChequeNro = "";
	private String filterChequeBanco = "";
	private String filterChequeCliente = "";
	
	private String filterNro = "";
	
	private BancoChequeTercero selectedChequeAutoCobranza;

	@Init(superclass=true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) CajaPeriodoControlBody dato) {
		this.dato = dato;
		this.inicializarItemDiferenciaCambio();
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose() {
	}
	
	@Override
	public String getAliasFormularioCorriente() {
		return ID.F_RECIBO_INSERCION_DETALLE;
	}	
	
	@Command
	@NotifyChange("*")
	public void insertarDetalle() throws Exception {
		this.filterNro = "";
		if (this.dato.getReciboDTO().isCobroExterno()) {
			this.nvoItem = new ReciboDetalleDTO();
			w = (Window) Executions.createComponents(ADD_ITEM_ZUL, this.mainComponent, null);
			w.doModal();

		} else {
			this.abrirPopupFacturas();
		}
	}
	
	@Command
	@NotifyChange("*")
	public void addDetalle() {
		this.dato.getReciboDTO().getDetalles().add(this.nvoItem);
		this.nvoItem = null;
		this.w.detach();
	}
	
	@Command
	@NotifyChange("*")
	public void addDetalleDiferenciaCambio(@BindingParam("comp") Popup comp) {
		this.dato.getReciboDTO().getDetalles().add(this.nvoItem_dif_cambio);
		this.inicializarItemDiferenciaCambio();
		comp.close();
	}
	
	
	/********* ELIMINAR ITEM DETALLES FACTURAS *********/
	
	private List<ReciboDetalleDTO> selectedFacturaItems = new ArrayList<ReciboDetalleDTO>();
	private String facturaItemsEliminar;	

	@Command 
	@NotifyChange("*")
	public void eliminarItemFactura() {

		Object[] validar = this
				.validarOperacion(OPERACION_ELIMINAR_ITEM_FACTURA);
		boolean valido = (boolean) validar[0];
		String mensaje = (String) validar[1];

		if (valido == false) {
			this.mensajeError(mensaje);
			return;
		}

		if (this.confirmarEliminarItemFactura() == true) {
			this.dato.getReciboDTO().getDetalles()
					.removeAll(this.selectedFacturaItems);
			this.selectedFacturaItems = new ArrayList<ReciboDetalleDTO>();
		}
	}
	
	private boolean confirmarEliminarItemFactura(){
		this.facturaItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (ReciboDetalleDTO d : this.selectedFacturaItems) {
			this.facturaItemsEliminar = this.facturaItemsEliminar + "\n - " + d.getDescripcion();
		}		
		return this.mensajeSiNo(this.facturaItemsEliminar);
	}	
	
	/***************************************************/


	/**************** AGREGAR FACTURAS *****************/
	
	private List<ReciboDetalleDTO> movimientosPendientes = new ArrayList<ReciboDetalleDTO>();
	private List<ReciboDetalleDTO> selectedMovimientos;
	private boolean cancelarTodo = false;
	private Window w;	
	
	@Command @NotifyChange("*")
	public void abrirPopupFacturas() throws Exception {
		
		Object[] validar = this.validarOperacion(OPERACION_INSERTAR_ITEM_FACTURA);
		boolean valido = (boolean) validar[0];
		String mensaje = (String) validar[1];
		
		if (valido == false) {
			this.mensajeError(mensaje);
			return;
		}
		
		this.buscarMovimientosPendientes();
		this.dato.getReciboDTO().setTotalImporteDs(0);
		this.dato.getReciboDTO().setTotalImporteGs(0);
		this.cancelarTodo = false;
		w = (Window) Executions.createComponents(Configuracion.RECIBO_ADD_FACTURA_ZUL, this.mainComponent, null);
		w.doModal();
	}
	
	@Command @NotifyChange("datosMovimientosPendientes")
	public void habilitarMontos(){
		for (ReciboDetalleDTO item : this.movimientosPendientes) {
			if (this.selectedMovimientos.contains(item) == true) {
				item.setSelected(true);
				item.setMontoGs(item.getSaldoGs());
				item.setMontoDs(item.getSaldoDs());
			} else {
				item.setMontoGs(0);
				item.setMontoDs(0);
				item.setSelected(false);
			}			
			BindUtils.postNotifyChange(null, null, item, "selected");
			BindUtils.postNotifyChange(null, null, item, "montoGs");
			BindUtils.postNotifyChange(null, null, item, "montoDs");
		}
	}
	
	@Command 
	@NotifyChange("*")
	public void agregarFactura() {
		
		Object[] validacion = this.validarAgregarFactura();
		boolean valido = (boolean) validacion[0];
		String mensaje = (String) validacion[1];
		
		if (valido == false) {
			this.mensajeError(mensaje);
			return;
		}

		for (ReciboDetalleDTO d : this.selectedMovimientos) {
			this.updateMovimientoSaldo(d);
			this.dato.getReciboDTO().getDetalles().add(d);
		}
		this.selectedMovimientos = null;
		w.detach();
	}	
	
	@Command 
	@NotifyChange("*")
	public void cancelarFactura() {
		this.selectedMovimientos = null;
		w.detach();
	}
	
	@Command @NotifyChange("*")
	public void cancelarTodo() throws Exception{
		double totalGs = 0;
		double totalDs = 0;
		
		if (this.cancelarTodo == true) {
			totalGs = (double) this.getDatosMovimientosPendientes()[2];
			totalDs = m.redondeoDosDecimales(totalGs / this.getTipoCambio());
			this.dato.getReciboDTO().setTotalImporteGs(totalGs);
			this.dato.getReciboDTO().setTotalImporteDs(totalDs);
			this.distribuirMonto(null);
		} else {
			this.dato.getReciboDTO().setTotalImporteGs(totalGs);
			this.dato.getReciboDTO().setTotalImporteDs(totalDs);
			this.cerarMontos();
			this.selectedMovimientos = new ArrayList<ReciboDetalleDTO>();
		}		
	}
	
	//Busca los movimientos pendientes del Proveedor seleccionado..
	@SuppressWarnings("unchecked")
	private void buscarMovimientosPendientes() throws Exception {
		
		MyArray emp = null; MyPair caracterMvto = null;
		if (this.dato.isCobro() == true 
				|| this.dato.isCancelacionCheque() == true
					|| this.dato.getReciboDTO().isReembolsoPrestamo() == true) {
			emp = this.dato.getReciboDTO().getCliente();
			caracterMvto = this.caracterMvtoCliente;
		} else {
			emp = this.dato.getReciboDTO().getProveedor();
			caracterMvto = this.caracterMvtoProveedor;
		}
		
		this.movimientosPendientes = new ArrayList<ReciboDetalleDTO>();	
		long idEmpresa = (long) emp.getPos4();
		long idMoneda = this.dato.getReciboDTO().getMoneda().getId();
		MyPair empresa = new MyPair(idEmpresa, "");
		AssemblerCtaCteEmpresaMovimiento ass = new AssemblerCtaCteEmpresaMovimiento(); 
			
		List<CtaCteEmpresaMovimientoDTO> pendientes = ctr.getCtaCteEmpresaMovimientosPendientes(empresa, caracterMvto, idMoneda);
			
		for (CtaCteEmpresaMovimientoDTO pendiente : pendientes) {
			String sigla = (String) pendiente.getTipoMovimiento().getPos2();
			if (!sigla.equals(Configuracion.SIGLA_TM_CTA_CTE_SALDO_FAVOR)) {
				CtaCteEmpresaMovimientoDTO ccm = (CtaCteEmpresaMovimientoDTO) 
						ass.getDTObyId(CtaCteEmpresaMovimiento.class.getName(), pendiente.getId());
				ReciboDetalleDTO opd = new ReciboDetalleDTO();			
					
				if (ccm.getMoneda().getId().compareTo(this.monedaLocal.getId()) == 0) {
					opd.setTipoCambio(this.dato.getReciboDTO().getTipoCambio());
					opd.setSaldoGs(ccm.getSaldo());
					opd.setSaldoDs(0);
					opd.setStyleSaldoGs("font-weight:bold");
					opd.setFormat(this.getCnv().getMonedaLocal());
				} else {
					opd.setTipoCambio(this.dato.getReciboDTO().getTipoCambio());
					opd.setSaldoDs(ccm.getSaldo());
					opd.setSaldoGs(ccm.getSaldo() * ccm.getTipoCambio());
					opd.setStyleSaldoDs("font-weight:bold");
					opd.setFormat(this.getCnv().getMonedaExtranjera());
				}
				opd.setMovimiento(ccm);
				if (this.dato.isCancelacionCheque()) {
					if (sigla.equals(Configuracion.SIGLA_TM_CHEQUE_RECHAZADO)) {
						this.movimientosPendientes.add(opd);
					}
				} else if (this.dato.getReciboDTO().isReembolsoPrestamo()) {
					if (sigla.equals(Configuracion.SIGLA_TM_PRESTAMO_CASA_CENTRAL)) {
						this.movimientosPendientes.add(opd);
					}
				} else {
					this.movimientosPendientes.add(opd);
				}
			}			
			Collections.sort(this.movimientosPendientes);
		}
	}		
	
	public void distribuirMonto(Doublebox comp) throws Exception {
		double deudaMonedaLocal = (double)this.getDatosMovimientosPendientes()[2];
		this.selectedMovimientos = new ArrayList<ReciboDetalleDTO>();
		
		if (this.dato.getReciboDTO().getTotalImporteGs() > deudaMonedaLocal) {
			m.mensajePopupTemporal("El monto supera el Total del Saldo..", "error", comp);	
			this.cerarMontos();
			return;
		}
		double total = this.dato.getReciboDTO().getTotalImporteGs();
		double resto = total;
		for (ReciboDetalleDTO det : this.getMovimientosPendientes()) {
			if (resto > 0.0001) {
				if (det.getSaldoGs() <= resto) {
					resto = resto - det.getSaldoGs();
					det.setMontoGs(det.getSaldoGs());
					det.setMontoDs(det.getMontoGs() / this.getTipoCambio());
					det.setSelected(true);
					this.selectedMovimientos.add(det);
				} else {
					det.setMontoGs(resto);
					det.setMontoDs(det.getMontoGs() / this.getTipoCambio());
					resto = 0;
					det.setSelected(true);
					this.selectedMovimientos.add(det);
				}
			} else {
				det.setMontoGs(0);
				det.setMontoDs(0);
				det.setSelected(false);
			}
		}
	}
	
	private void cerarMontos(){
		for (ReciboDetalleDTO item : this.movimientosPendientes) {
			item.setMontoDs(0);
			item.setMontoGs(0);
			item.setSelected(false);
		}
	}
	
	private void updateMovimientoSaldo(ReciboDetalleDTO opd){
		
		CtaCteEmpresaMovimientoDTO mvd = opd.getMovimiento();
		MyPair moneda = opd.getMovimiento().getMoneda();
		String sigla = moneda.getSigla();
		boolean monedaLocal = this.isMonedaLocal(sigla);		
		double saldo = mvd.getSaldo();
		double tc = this.dato.getReciboDTO().getTipoCambio();
		double importe  = 0;
		
		if (monedaLocal == true) {
			importe = opd.getMontoGs();
		} else {			
			importe = this.m.redondeoDosDecimales(opd.getMontoGs() / tc);			
		}
		
		mvd.setSaldo(saldo - importe);		
	}
	
	// [0]:booleano si la validacion es correcta - [1]:mensaje
	private Object[] validarAgregarFactura() {
		boolean valido = true;
		String mensaje = "No se puede completar la operación debido a: \n";

		if ((this.selectedMovimientos == null)
				|| (this.selectedMovimientos.size() == 0)) {
			valido = false;
			mensaje += "\n - No se ha seleccionado ningún ítem..";

		} else {
			if (this.isDuplicado()) {
				valido = false;
				mensaje += "\n - No se permiten ítems duplicados..";
			}
			if (!Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
				if (((this.selectedMovimientos.size() + this.dato.getReciboDTO()
						.getDetalles().size()) > Configuracion.LIMITE_ITEMS_RECIBO_COBRO)
						&& (this.dato.getReciboDTO().isCobro())) {
					valido = false;
					mensaje += "\n - Solo se permiten hasta "
							+ Configuracion.LIMITE_ITEMS_RECIBO_COBRO + " ítems..";
				}
			}
		}		
		return new Object[] { valido, mensaje };
	}
	
	/**
	 * @return si el item es duplicado..
	 */
	private boolean isDuplicado() {
		
		for (ReciboDetalleDTO item : this.selectedMovimientos) {
			String desc = item.getDescripcion();
			for (ReciboDetalleDTO item_ : this.dato.getReciboDTO().getDetalles()) {
				if(desc.equals(item_.getDescripcion()))
					return true;
			}
		}		
		return false;
	}
	
	/***************************************************/	
	
	
	/******************** ANTICIPOS ********************/	
	
	@Command 
	@NotifyChange("*")
	public void checkAnticipo(@BindingParam("check") boolean check) {
		boolean cobro = this.dato.isCobro();
		
		if (cobro == true) {
			this.dato.getReciboDTO().setTipoMovimiento(
					check ? tipoMvtoAnticipoCobro : tipoMvtoCobro);
		} else {
			this.dato.getReciboDTO().setTipoMovimiento(
					check ? tipoMvtoAnticipoPago : tipoMvtoPago);
		}
		
		//quita las facturas si es que fueron previamente asignadas..
		if (this.isAnticipo() == true) {
			this.dato.getReciboDTO().setDetalles(new ArrayList<ReciboDetalleDTO>());
		}
	}
	
	/***************************************************/	
	
	
	/**************** PENDIENTES DE COBRO **************/
	
	@Command @NotifyChange("*")
	public void checkPendienteCobro(){
		if (this.isPendienteCobro() == true) {
			this.dato.getReciboDTO().setFormasPago(new ArrayList<ReciboFormaPagoDTO>());
		}
	}
	
	/***************************************************/
	
	
	/************** COMPORTAMIENTO MONEDA **************/
	
	@Command @NotifyChange("*")
	public void guaranizarTotalDs(@BindingParam("comp") Doublebox comp) throws Exception{
		double totalPagoDs = this.dato.getReciboDTO().getTotalImporteDs();
		this.dato.getReciboDTO().setTotalImporteGs(totalPagoDs * this.getTipoCambio());
		this.cancelarTodo = false;
		distribuirMonto(comp);
	}
	
	@Command @NotifyChange("*")
	public void dolarizarTotalGs(@BindingParam("comp") Doublebox comp) throws Exception {
		double totalPagoGs = this.dato.getReciboDTO().getTotalImporteGs();
		this.dato.getReciboDTO().setTotalImporteDs(totalPagoGs / this.getTipoCambio());
		this.cancelarTodo = false;
		distribuirMonto(comp);
	}
	
	@Command @NotifyChange({"datosMovimientosPendientes", "cancelarTodo"})
	public void guaranizar(@BindingParam("item") ReciboDetalleDTO item, 
			@BindingParam("comp") Doublebox comp) {
		double tc = item.getMovimiento().getTipoCambio();
		double montoDs = item.getMontoDs();
		double montoGs = montoDs * tc;
		double saldoGs = item.getSaldoGs();		
		
		if (montoGs > saldoGs) {
			item.setMontoDs(item.getMontoGs() / tc);
			m.mensajePopupTemporal("El monto ingresado supera al saldo..", "error", comp);
		} else {
			item.setMontoGs(montoGs);
		}		
		this.cancelarTodo = false;
		BindUtils.postNotifyChange(null, null, item, "montoGs");
	}	
	
	@Command @NotifyChange({"datosMovimientosPendientes", "cancelarTodo"})
	public void dolarizar(@BindingParam("item") ReciboDetalleDTO item, 
			@BindingParam("comp") Doublebox comp){
		double montoGs = item.getMontoGs();
		double saldoGs = item.getSaldoGs();
		double tc = item.getMovimiento().getTipoCambio();
		
		if (montoGs > saldoGs) {
			item.setMontoGs(item.getMontoDs() * tc);
			m.mensajePopupTemporal("El monto ingresado supera al saldo..", "error", comp);
		} else {
			item.setMontoDs(montoGs / tc);
		}		
		this.cancelarTodo = false;
		BindUtils.postNotifyChange(null, null, item, "montoDs");
	}	
	
	/***************************************************/	
	
	
	/****************** FORMA DE PAGO ******************/
	
	private ReciboFormaPagoDTO nvoFormaPago = new ReciboFormaPagoDTO();
	private MyArray selectedTarjetaCredito = new MyArray("", new ArrayList<MyPair>());

	@Command @NotifyChange("*")
	public void abrirPopupFormaPago(){
		
		Object[] validar = this.validarOperacion(OPERACION_INSERTAR_FORMA_PAGO);
		boolean valido = (boolean) validar[0];
		String mensaje = (String) validar[1];
		
		if (valido == false) {
			this.mensajeError(mensaje);
			return;
		}
		
		Object[] datos  = this.getDatosMovimientosApagar();
		double totalGs  = (double) datos[0]; 
		double totalDs  = (double) datos[1];
		double totalGs_ = (double) datos[2];
		double totalDs_ = (double) datos[3];
		
		this.selectedTarjetaCredito = new MyArray("", new ArrayList<MyPair>());
		
		this.nvoFormaPago = new ReciboFormaPagoDTO();
		this.nvoFormaPago.setTipo(this.formaPagoEfectivo);		
		this.nvoFormaPago.setMontoGs(totalGs - totalGs_);
		this.nvoFormaPago.setMontoDs(totalDs - totalDs_);
		this.setearMoneda();
		
		w = (Window) Executions.createComponents(Configuracion.RECIBO_ADD_FORMA_PAGO_ZUL, 
				this.mainComponent, null);
		Selectors.wireComponents(w, this, false);
		w.doOverlapped();
	}
	
	@Command @NotifyChange("*")
	public void agregarFormaPago() {
		
		if (this.nvoFormaPago.isSaldoFavorCobrado() && this.nvoFormaPago.getMontoGs() < 0) {
			this.nvoFormaPago.setMontoGs(this.nvoFormaPago.getMontoGs() * -1);
		}
		
		Object[] validar = this.validarFormaDePago();
		boolean valido = (boolean) validar[0];
		String mensaje = (String) validar[1];
		
		if (valido == false) {
			this.mensajeError(mensaje);
			return;
		}
		
		this.nvoFormaPago.setDescripcion(this.getDescripcion());
		if (this.nvoFormaPago.isSaldoFavorGenerado() && this.nvoFormaPago.getMontoGs() > 0) {
			this.nvoFormaPago.setMontoGs(this.nvoFormaPago.getMontoGs() * -1);
		}
		
		if (!this.dato.getReciboDTO().isMonedaLocal()) {
			this.nvoFormaPago.setMontoGs(this.nvoFormaPago.getMontoDs() * this.dato.getReciboDTO().getTipoCambio());
		}
		
		this.dato.getReciboDTO().getFormasPago().add(nvoFormaPago);
		w.detach();
	}
	
	@Command @NotifyChange("*")
	public void cancelarFormaPago(){
		w.detach();
	}
	
	@Command @NotifyChange("*")
	public void dolarizarFormaPago(){
		double montoGs = this.nvoFormaPago.getMontoGs();
		this.nvoFormaPago.setMontoDs(montoGs / this.getTipoCambio());
	}
	
	@Command @NotifyChange("*")
	public void guaranizarFormaPago(){
		double montoDs = this.nvoFormaPago.getMontoDs();
		this.nvoFormaPago.setMontoGs(montoDs * this.getTipoCambio());
	}
	
	@GlobalCommand @NotifyChange("*")
	public void updateCuentaBanco(@BindingParam("cuenta") BancoCtaDTO cuenta){
		this.nvoFormaPago.setBancoCta(cuenta);
	}
	
	/**
	 * setea la moneda a la forma de pago..
	 */
	private void setearMoneda() {
		long id = this.dato.getReciboDTO().getMoneda().getId();
		this.nvoFormaPago.setMoneda(new MyPair(id));
	}
	
	/**
	 * Despliega la ventana del cheque..
	 * @throws Exception
	 */
	public void showCheque() throws Exception {
		String beneficiario = (String) this.dato.getReciboDTO().getProveedor().getPos2();
		MyArray moneda = this.dato.getReciboDTO().getMoneda();
		
		WindowCheque w = new WindowCheque();
		w.getChequeDTO().setBeneficiario(beneficiario);
		w.getChequeDTO().setMoneda(moneda);	
		w.getChequeDTO().setMonto(this.getMontoCheque());
		w.setMontoRecibo(this.getMontoCheque());
		w.show(WindowPopup.NUEVO);
		
		if (w.isClickAceptar()) {					
			this.nvoFormaPago.setChequePropio(w.getChequeDTO(), w.getCuentaDTO().getBancoDescripcion());
			this.nvoFormaPago.setDepositoBancoCta(w.getCuentaDTO().toMyArray());
			this.nvoFormaPago.setBancoCta(w.getCuentaDTO());
			actualizarMontoFormaPago(w.getChequeDTO());
			//this.nvoFormaPago.setBancoMovimiento(this.generarBancoMovimiento());
			String descrip = this.nvoFormaPago.getTipo().getSigla() 
					+ " - " +this.nvoFormaPago.getBancoCta().getBanco().getPos1()
					+ " - " +this.nvoFormaPago.getChequeNumero();
			this.nvoFormaPago.setDescripcion(descrip);
		}
	}
	
	public void actualizarMontoFormaPago(BancoChequeDTO cheque){
		
		if (cheque.getMoneda().getId().compareTo(this.monedaLocal.getId()) == 0) {
			this.nvoFormaPago.setMontoGs(cheque.getMonto());
			this.nvoFormaPago.setMontoDs(cheque.getMonto() / this.getTipoCambio());
		} else {
			this.nvoFormaPago.setMontoDs(cheque.getMonto());
			this.nvoFormaPago.setMontoGs(cheque.getMonto() * this.getTipoCambio());
		}
	}	
	
	private double getMontoCheque(){		
		if (this.isPagoEnMonedaLocal() == true) {
			return this.nvoFormaPago.getMontoGs();
		} else {
			return this.nvoFormaPago.getMontoDs();
		}
	}
	
	private Object[] validarFormaDePago(){
		boolean valido = true;
		String mensaje = Configuracion.TEXTO_ERROR_VALIDACION;
		long idFormaPago = this.nvoFormaPago.getTipo().getId().longValue();
		long idFormaPagoChequePropio = this.formaPagoChequePropio.getId().longValue();
		long idFormaPagoTarjetaCred = this.formaPagoTarjetaCredito.getId().longValue();
		long idFormaPagoTarjetaDeb = this.formaPagoTarjetaDebito.getId().longValue();
		long idFormaPagoChequeTercero = this.formaPagoChequeTercero.getId().longValue();
		long idFormaPagoRetencion = this.formaPagoRetencion.getId().longValue();
		long idFormaPagoDepositoBanco = this.formaPagoDepositoBancario.getId().longValue();
		long idFormaPagoSaldoCobrado = this.formaPagoSaldoFavorCobrado.getId().longValue();
		
		// Forma de pago cheque..
		if (idFormaPago == idFormaPagoChequePropio) {
			
			if (this.nvoFormaPago.getBancoCta().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar una Cuenta..";
			}
			
			if (this.nvoFormaPago.getChequeFecha() == null) {
				valido = false;
				mensaje += "\n - Debe ingresar la Fecha de Vencimiento..";
			}
		
		// Forma de pago tarjeta de credito..	
		} else if (idFormaPago == idFormaPagoTarjetaCred) {
			
			if (((String) this.selectedTarjetaCredito.getPos1()).trim().length() == 0) {
				valido = false;
				mensaje += "\n - Debe seleccionar el Tipo de Tarjeta..";
			}
			
			if (this.nvoFormaPago.getTarjetaTipo().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar el Emisor de la Tarjeta..";
			}
			
			if (this.nvoFormaPago.getTarjetaProcesadora().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar la Procesadora..";
			}
			
			if (this.nvoFormaPago.getTarjetaNumero().trim().length() == 0) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número de Tarjeta..";
			}
			
			if (this.nvoFormaPago.getTarjetaNumeroComprobante().trim().length() == 0) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número del Comprobante..";
			}
			
			if (this.nvoFormaPago.getTarjetaCuotas() == 0) {
				valido = false;
				mensaje += "\n - El número de cuotas debe ser mayor a cero..";
			}
		
		// Forma de pago tarjeta de debito..	
		} else if (idFormaPago == idFormaPagoTarjetaDeb) {
			
			if (this.nvoFormaPago.getTarjetaProcesadora().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar la Procesadora..";
			}
			
			if (this.nvoFormaPago.getTarjetaNumero().trim().length() == 0) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número de Tarjeta..";
			}
			
			if (this.nvoFormaPago.getTarjetaNumeroComprobante().trim().length() == 0) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número del Comprobante..";
			}			
		
		// Forma de Pago Cheque Terceros..	
		} else if (idFormaPago == idFormaPagoChequeTercero) {
			
			if (this.nvoFormaPago.getChequeBanco().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar el Banco..";
			}
			
			if (this.nvoFormaPago.getChequeNumero().isEmpty() == true) {
				valido = false;
				mensaje += "\n - Debe ingresar el numero del cheque..";
			}
			
			if (this.nvoFormaPago.getChequeLibrador().isEmpty() == true) {
				valido = false;
				mensaje += "\n - Debe ingresar el Librador del cheque..";
			}
			
			if (this.nvoFormaPago.getChequeFecha() == null) {
				valido = false;
				mensaje += "\n - Debe ingresar la fecha del cheque..";
			}
		
		// Forma de Pago con Retencion..	
		} else if (idFormaPago == idFormaPagoRetencion) {
			
			if (this.nvoFormaPago.getRetencionNumero().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el número..";
			}
			
			if (this.nvoFormaPago.getRetencionTimbrado().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el timbrado..";
			}
			
			if (this.nvoFormaPago.getRetencionVencimiento() == null) {
				valido = false;
				mensaje += "\n - Debe ingresar el vencimiento del timbrado..";
			}
		
		// Forma de Pago con Deposito bancario..	
		} else if (idFormaPago == idFormaPagoDepositoBanco) {
			
			if (this.nvoFormaPago.getDepositoBancoCta().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar la Cuenta de Banco..";
			}
			
			if (this.nvoFormaPago.getDepositoNroReferencia().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número de referencia..";
			}			
		
		} else if (idFormaPago == idFormaPagoSaldoCobrado) {
			
			if (this.nvoFormaPago.getCtaCteSaldoFavor() == null) {
				valido = false;
				mensaje += "\n - Debe seleccionar la aplicación de saldo..";
			} else {
				double montoGs = this.nvoFormaPago.getMontoGs();
				double saldoFavor = (double) this.nvoFormaPago.getCtaCteSaldoFavor().getPos3();			
				if (montoGs > saldoFavor) {
					valido = false;
					mensaje += "\n - El monto no debe ser mayor al saldo a favor del cliente..";
				}
			}			
		} 		
		
		return new Object[] { valido, mensaje };
	}
	
	/***************************************************/	
	
	
	/****** ELIMINAR ITEM DETALLES FORMA DE PAGO *******/
	
	private List<ReciboFormaPagoDTO> selectedFormaPagoItems = new ArrayList<ReciboFormaPagoDTO>();
	private String formaPagoItemsEliminar;	

	@Command @NotifyChange("*")
	public void eliminarItemFormaPago(){
		
		Object[] validar = this.validarOperacion(OPERACION_ELIMINAR_FORMA_PAGO);
		boolean valido = (boolean) validar[0];
		String mensaje = (String) validar[1];
		
		if (valido == false) {
			this.mensajeError(mensaje);
			return;
		}
		
		if (this.confirmarEliminarItemFormaPago() == true) {
			for (ReciboFormaPagoDTO d : this.selectedFormaPagoItems) {
				this.dato.getReciboDTO().getFormasPago().remove(d);
			}
			this.selectedFormaPagoItems = new ArrayList<ReciboFormaPagoDTO>();
		}		
	}
	
	private boolean confirmarEliminarItemFormaPago(){
		this.formaPagoItemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (ReciboFormaPagoDTO d : this.selectedFormaPagoItems) {
			this.formaPagoItemsEliminar = this.formaPagoItemsEliminar + "\n - " + d.getDescripcion();
		}		
		return this.mensajeSiNo(this.formaPagoItemsEliminar);
	}
	
	/***************************************************/		
	
	
	/********** SELECCION DE LA FORMA DE PAGO **********/
	
	@Wire
	private Row rwTarjeta;
	@Wire
	private Row rwEmisor;
	@Wire
	private Row rwProcesadora;
	@Wire
	private Row rwNroTarjeta;
	@Wire
	private Row rwNroComprobante;
	@Wire
	private Row rwCuotas;
	@Wire
	private Row rwBanco;	
	@Wire
	private Row rwChequera;	
	@Wire
	private Row rwChequeBanco;
	@Wire
	private Row rwNroCheque;	
	@Wire
	private Row rwMontoCheque;
	@Wire
	private Row rwLibrador;
	@Wire
	private Row rwVencimiento;	
	@Wire
	private Doublebox dbxUS;
	@Wire
	private Doublebox dbxGs;
	@Wire
	private Row rwDepositoBanco;
	@Wire
	private Row rwDepositoReferencia;
	@Wire
	private Row rwNroRetencion;
	@Wire
	private Row rwTimbradoRetencion;
	@Wire
	private Row rwTimbradoVencimiento;
	@Wire
	private Row rwChequeAutoCobranza;
	@Wire
	private Row rwChequeBancoAutoCobro;
	@Wire
	private Row rwLibradorAutoCobro;
	@Wire
	private Row rwVencimientoAutoCobro;
	@Wire
	private Bandbox bndCheq;
	@Wire
	private Row rwDebitoCobroCentral;
	@Wire
	private Row rwSaldoFavorCobrado;
	@Wire
	private Row rwMontoAplicado;
	
	@Command @NotifyChange("*")
	public void seleccionarFormaPago() throws Exception {
		this.reloadFormaPago(this.nvoFormaPago);
		String siglaFP = this.getNvoFormaPago().getTipo().getSigla();
		String siglaFPCH = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO;
		String siglaFPCT = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO;
		String siglaFPTC = Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO;
		String siglaFPTD = Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO;
		String siglaFPDB = Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO;
		String siglaFPDE = Configuracion.SIGLA_FORMA_PAGO_DEBITO_CTA_BANCARIA;
		String siglaFPRE = Configuracion.SIGLA_FORMA_PAGO_RETENCION;
		String siglaFPCA = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_AUTOCOBRANZA;
		String siglaFPDC = Configuracion.SIGLA_FORMA_PAGO_DEBITO_COBRANZA_CENTRAL;
		String siglaSFCO = Configuracion.SIGLA_FORMA_PAGO_SALDO_FAVOR_COBRADO;
		
		if (siglaFP.compareTo(siglaFPCH) == 0) {
			showCheque();
			rwBanco.setVisible(true); rwChequera.setVisible(true);
			rwNroCheque.setVisible(true); rwVencimiento.setVisible(true);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false); 
			rwDebitoCobroCentral.setVisible(false);
			rwSaldoFavorCobrado.setVisible(false);
			dbxGs.setReadonly(true); dbxUS.setReadonly(true);
			rwMontoAplicado.setVisible(true);
			
		} else if(siglaFP.compareTo(siglaFPTC) == 0){
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(true); rwEmisor.setVisible(true);
			rwNroTarjeta.setVisible(true); rwProcesadora.setVisible(true);
			rwNroComprobante.setVisible(true); rwCuotas.setVisible(true);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			rwDebitoCobroCentral.setVisible(false);
			rwSaldoFavorCobrado.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			rwMontoAplicado.setVisible(true);
			this.nvoFormaPago.setDescripcion(this.nvoFormaPago.getTipo().getText());
			
		} else if(siglaFP.compareTo(siglaFPTD) == 0){
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(true); rwProcesadora.setVisible(true);
			rwNroComprobante.setVisible(true); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwDebitoCobroCentral.setVisible(false);
			rwTimbradoVencimiento.setVisible(false); 
			rwSaldoFavorCobrado.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			rwMontoAplicado.setVisible(true);
			this.nvoFormaPago.setDescripcion(this.nvoFormaPago.getTipo().getText());
			
		} else if(siglaFP.compareTo(siglaFPDB) == 0){
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(true); rwDepositoReferencia.setVisible(true);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false); 
			rwDebitoCobroCentral.setVisible(false);
			rwSaldoFavorCobrado.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			rwMontoAplicado.setVisible(true);
			this.nvoFormaPago.setDescripcion(this.nvoFormaPago.getTipo().getText());
		
		} else if(siglaFP.compareTo(siglaFPDE) == 0){
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(true); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false); 
			rwDebitoCobroCentral.setVisible(false);
			rwSaldoFavorCobrado.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			rwMontoAplicado.setVisible(true);
			this.nvoFormaPago.setDescripcion(this.nvoFormaPago.getTipo().getText());
		
		} else if (siglaFP.compareTo(siglaFPCT) == 0) {
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(true); rwVencimiento.setVisible(true);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(true); rwLibrador.setVisible(true);
			rwMontoCheque.setVisible(true); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwDebitoCobroCentral.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			rwSaldoFavorCobrado.setVisible(false);
			rwMontoAplicado.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);	
			this.nvoFormaPago.setChequeLibrador((String) this.dato.getReciboDTO().getCliente().getPos2());
		
		} else if (siglaFP.equals(siglaFPRE)) {
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwNroRetencion.setVisible(true); rwTimbradoRetencion.setVisible(true);
			rwDebitoCobroCentral.setVisible(false);
			rwTimbradoVencimiento.setVisible(true); 
			rwSaldoFavorCobrado.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			rwMontoAplicado.setVisible(true);
			this.calcularRetencion();
				
		} else if (siglaFP.equals(siglaFPCA)) {
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(true);
			rwChequeBancoAutoCobro.setVisible(true); rwLibradorAutoCobro.setVisible(true);
			rwVencimientoAutoCobro.setVisible(true);
			rwDebitoCobroCentral.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			rwSaldoFavorCobrado.setVisible(false);
			rwMontoAplicado.setVisible(true);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			
		} else if (siglaFP.equals(siglaFPDC)) {
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwDebitoCobroCentral.setVisible(true);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			rwSaldoFavorCobrado.setVisible(false);
			rwMontoAplicado.setVisible(true);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			
		} else if (siglaFP.equals(siglaSFCO)) {
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwDebitoCobroCentral.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			rwSaldoFavorCobrado.setVisible(true);
			rwMontoAplicado.setVisible(true);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			
		} else {
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwMontoCheque.setVisible(false); rwChequeAutoCobranza.setVisible(false);
			rwChequeBancoAutoCobro.setVisible(false); rwLibradorAutoCobro.setVisible(false);
			rwVencimientoAutoCobro.setVisible(false);
			rwDebitoCobroCentral.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false); 
			rwSaldoFavorCobrado.setVisible(false);
			rwMontoAplicado.setVisible(true);
			this.nvoFormaPago.setDescripcion(this.nvoFormaPago.getTipo().getText());		
		}
	}
	
	/**
	 * Inicializa los datos de forma de pago..
	 */
	private void reloadFormaPago(ReciboFormaPagoDTO formaPago) {
		this.selectedTarjetaCredito = new MyArray("", new ArrayList<MyPair>());
		formaPago.setDescripcion("");
		formaPago.setTarjetaNumero("");
		formaPago.setTarjetaNumeroComprobante("");
		formaPago.setTarjetaProcesadora(new MyArray());
		formaPago.setTarjetaCuotas(0);
		formaPago.setTarjetaTipo(new MyPair());
		formaPago.setBancoCta(null);
		formaPago.setChequeFecha(null);
		formaPago.setChequeBanco(new MyPair());
		formaPago.setChequeNumero("");
		formaPago.setChequeLibrador("");
		formaPago.setDepositoBancoCta(new MyArray());
		formaPago.setDepositoNroReferencia("");
		formaPago.setRetencionNumero("");
		formaPago.setRetencionTimbrado("");
		formaPago.setRetencionVencimiento(null);
		formaPago.setReciboDebitoNro("");
		formaPago.setCtaCteSaldoFavor(null);
		this.selectedChequeAutoCobranza = null;
	}
	
	/**
	 * calcula el monto de retencion
	 */
	private void calcularRetencion() {
		double total = 0;
		for (ReciboDetalleDTO item : this.dato.getReciboDTO().getDetalles()) {
			total += item.getMontoGs();
		}
		double iva = m.calcularIVA(total, Configuracion.VALOR_IVA_10);
		double ret = this.m.obtenerValorDelPorcentaje(iva, Configuracion.PORCENTAJE_RETENCION);
		this.nvoFormaPago.setMontoGs(ret);
	}
	
	/**
	 * inicializa el item de dif. cambio..
	 */
	private void inicializarItemDiferenciaCambio() {
		this.nvoItem_dif_cambio = new ReciboDetalleDTO();
		this.nvoItem_dif_cambio.setAuxi(ReciboDetalle.TIPO_DIF_CAMBIO);
		this.nvoItem_dif_cambio.setConcepto("DIFERENCIA POR TIPO DE CAMBIO");
		this.nvoItem_dif_cambio.setMovimiento(null);
	}
	
	/**
	 * @return las formas de pago..
	 */
	public List<MyPair> getFormasDePago() {
		List<MyPair> out = new ArrayList<MyPair>();
		out.addAll(this.getDtoUtil().getFormasDePago());
		
		if (this.dato.isCobro() 
				|| this.dato.isCancelacionCheque()
					|| this.dato.isReembolsoPrestamo()) {
			out.remove(this.formaPagoChequePropio);
			out.remove(this.formaPagoChequeAutoCobranza);
			out.remove(this.formaPagoDebitoCuentaBancaria);
			
		} else {
			out.remove(this.formaPagoChequeTercero);
			out.remove(this.formaPagoDepositoBancario);
			out.remove(this.formaPagoTarjetaCredito);
			out.remove(this.formaPagoTarjetaDebito);	
			out.remove(this.formaPagoRecaudacionCentral);
			out.remove(this.formaPagoTransferenciaCentral);
			out.remove(this.formaPagoSaldoFavorGenerado);
			out.remove(this.formaPagoSaldoFavorCobrado);
		}
		return out;
	}
	
	@Command
	@NotifyChange("*")
	public void setFormaPagoAutoCobranza() {
		this.bndCheq.close();
		this.rwChequeAutoCobranza.setVisible(true);
		this.rwChequeBancoAutoCobro.setVisible(true);
		this.rwLibradorAutoCobro.setVisible(true);
		this.rwVencimientoAutoCobro.setVisible(true);
		this.dbxGs.setReadonly(true);
		this.dbxUS.setReadonly(true);

		this.nvoFormaPago.setChequeBanco(new MyPair(
				this.selectedChequeAutoCobranza.getBanco().getId(),
				this.selectedChequeAutoCobranza.getBanco().getDescripcion()));
		this.nvoFormaPago.setMontoGs(this.selectedChequeAutoCobranza.getMonto());
		this.nvoFormaPago.setChequeFecha(this.selectedChequeAutoCobranza.getFecha());
		this.nvoFormaPago.setChequeLibrador(this.selectedChequeAutoCobranza.getLibrado());
		this.nvoFormaPago.setChequeNumero(this.selectedChequeAutoCobranza.getNumero());
	
	}
	
	/***************************************************/		
	
	
	/******************* IMPRIMIR **********************/
	
	@Command
	public void imprimirOrdenPago() throws Exception {
		
		List<Object[]> data = new ArrayList<>();
		Object[] obj = new Object[6];
		for (ReciboDetalleDTO d : this.dato.getReciboDTO().getDetalles()) {
			obj = new Object[6];
			obj[0] = d.getMovimiento().getIdMovimientoOriginal(); //cambiar por descripcion de la factura
			obj[1] = d.getMovimiento().getNroComprobante();
			obj[2] = d.getMovimiento().getTipoMovimiento().getPos1();
			obj[3] = d.getMovimiento().getFechaEmision();
			obj[4] = d.getMovimiento().getFechaVencimiento();
			obj[5] = d.getMontoGs();
			data.add(obj);
		}
		
		ReciboReporte r = new ReciboReporte();
		r.setDatosReporte(data);
		r.setBeneficiario((String) this.dato.getReciboDTO().getProveedor().getPos2());
		r.setNroRecibo(this.dato.getReciboDTO().getNumero());
		
		ViewPdf vp = new ViewPdf();
		vp.showReporte(r, this);
		
		this.getCtrAgenda().addDetalle(
				ControlAgendaEvento.NORMAL, this.dato.getDto().getNumero(), 0, "Se imprimió el Pago Número: "+ 
						this.dato.getReciboDTO().getNumero(), null);
	}
	
	/***************************************************/			
	
	
	/************ VALIDACION DE OPERACIONES ************/
	
	private static final int OPERACION_INSERTAR_ITEM_FACTURA = 1;
	private static final int OPERACION_ELIMINAR_ITEM_FACTURA = 2;
	private static final int OPERACION_INSERTAR_FORMA_PAGO = 3;
	private static final int OPERACION_ELIMINAR_FORMA_PAGO = 4;
	
	//valida las operaciones
	private Object[] validarOperacion(int operacion){
		boolean valido = true;
		String mensaje = "No se puede completar la operación debido a: \n";		
		
		switch (operacion) {
		
		case OPERACION_INSERTAR_ITEM_FACTURA:
			
			if ((this.dato.getReciboDTO().getProveedor().esNuevo() == true)
					&& (this.dato.isCobro() == false 
					&& this.dato.isCancelacionCheque() == false
					&& this.dato.isReembolsoPrestamo() == false)) {
				valido = false;
				mensaje += "\n - Debe seleccionar un proveedor..";
			}
			
			if ((this.dato.getReciboDTO().getCliente().esNuevo() == true)
					&& (this.dato.isCobro() == true)) {
				valido = false;
				mensaje += "\n - Debe seleccionar un cliente..";
			}
			
			if ((this.dato.getReciboDTO().getDetalles().size() > Configuracion.LIMITE_ITEMS_RECIBO_COBRO)
					&& (this.dato.getReciboDTO().isCobro())) {
				valido = false;
				mensaje += "\n - Solo se permite hasta 10 ítems..";
			}
			
			break;

		case OPERACION_ELIMINAR_ITEM_FACTURA:
			
			if (this.selectedFacturaItems.size() == 0) {
				valido = false;
				mensaje += "\n - No hay ítems seleccionados..";
			}
			
			break;
			
		case OPERACION_INSERTAR_FORMA_PAGO:
			
			MyArray tm = this.dato.getReciboDTO().getTipoMovimiento();
			boolean tmAntCob = tm.compareTo(tipoMvtoAnticipoCobro) == 0;
			boolean tmAntPag = tm.compareTo(tipoMvtoAnticipoPago) == 0;
			boolean anticipo = tmAntCob || tmAntPag;
			
			if (anticipo == false) {
				
				if (this.dato.getReciboDTO().getDetalles().size() == 0) {
					valido = false;
					mensaje += "\n - No hay facturas a pagar..";
				}
				
				if (this.dato.getReciboDTO().getDetalles().size() == 0) {
					valido = false;
					mensaje += "\n - No hay facturas a pagar..";
				}
				
			} else {
				
				String siglaTm = this.dato.getReciboDTO().getTipoMovimiento().getPos2() + "";
				
				//Si es cobro verifica que se haya seleccionado un cliente..
				if (siglaTm.compareTo(Configuracion.SIGLA_TM_ANTICIPO_COBRO) == 0) {
					if (this.dato.getReciboDTO().getCliente().esNuevo() == true) {
						valido = false;
						mensaje += " \n - Debe seleccionar un Cliente..";
					}
					
				//Si es pago verifica que se haya seleccionado un proveedor..
				} else if (siglaTm.compareTo(Configuracion.SIGLA_TM_ANTICIPO_PAGO) == 0) {
					if (this.dato.getReciboDTO().getProveedor().esNuevo() == true) {
						valido = false;
						mensaje += "\n - Debe seleccionar un Proveedor..";
					}
				}
			}
			
			break;
			
		case OPERACION_ELIMINAR_FORMA_PAGO:
			
			if (this.selectedFormaPagoItems.size() == 0) {
				valido = false;
				mensaje += "\n - No hay ítems seleccionados..";
			}
			
			break;			
		}
		
		return new Object[]{valido, mensaje};
	}
	
	/***************************************************/	
	
	
	/********************* UTILES **********************/
	
	private UtilDTO dtoUtil = (UtilDTO) this.getDtoUtil();
	private ControlCtaCteEmpresa ctr = new ControlCtaCteEmpresa(null);
	private ControlAgendaEvento ctrAgenda = new ControlAgendaEvento();
	private MyArray tipoMvtoPago = dtoUtil.getTmReciboPago();
	private MyArray tipoMvtoCobro = dtoUtil.getTmReciboCobro();
	private MyArray tipoMvtoAnticipoCobro = dtoUtil.getTmAnticipoCobro();
	private MyArray tipoMvtoAnticipoPago = dtoUtil.getTmAnticipoPago();
	private MyPair caracterMvtoProveedor = dtoUtil.getCtaCteEmpresaCaracterMovProveedor();
	private MyPair caracterMvtoCliente = dtoUtil.getCtaCteEmpresaCaracterMovCliente();
	private MyPair monedaLocal = dtoUtil.getMonedaGuarani();
	private MyPair formaPagoEfectivo = dtoUtil.getFormaPagoEfectivo();
	private MyPair formaPagoChequePropio = dtoUtil.getFormaPagoChequePropio();
	private MyPair formaPagoChequeTercero = dtoUtil.getFormaPagoChequeTercero();
	private MyPair formaPagoTarjetaCredito = dtoUtil.getFormaPagoTarjetaCredito();
	private MyPair formaPagoTarjetaDebito = dtoUtil.getFormaPagoTarjetaDebito();
	private MyPair formaPagoDepositoBancario = dtoUtil.getFormaPagoDepositoBancario();
	private MyPair formaPagoRetencion = dtoUtil.getFormaPagoRetencion();
	private MyPair formaPagoChequeAutoCobranza = dtoUtil.getFormaPagoChequeAutoCobranza();
	private MyPair formaPagoRecaudacionCentral = dtoUtil.getFormaPagoRecaudacionCentral();
	private MyPair formaPagoTransferenciaCentral = dtoUtil.getFormaPagoTransferenciaCentral();
	private MyPair formaPagoSaldoFavorGenerado = dtoUtil.getFormaPagoSaldoFavorGenerado();
	private MyPair formaPagoSaldoFavorCobrado = dtoUtil.getFormaPagoSaldoFavorCobrado();
	private MyPair formaPagoDebitoCuentaBancaria = dtoUtil.getFormaPagoDebitoEnCuentaBancaria();
	
	@Command @NotifyChange("*")
	public void refresh(){
	}
	
	@Command @NotifyChange("*")
	public void refreshTipoTarjeta() {
		this.nvoFormaPago.setTarjetaTipo(new MyPair());
	}
	
	public boolean getCheckmarkFacturas(){
		boolean out = false;
		if (this.dato.getReciboDTO().getDetalles().size() > 0) {
			out = true;
		}
		return out;
	}
	
	public boolean getCheckmarkFormasPago(){
		boolean out = false;
		if (this.dato.getReciboDTO().getFormasPago().size() > 0) {
			out = true;
		}
		return out;
	}	
	
	//para determinar si el Pago es en moneda local
	public boolean isPagoEnMonedaLocal(){
		return this.dato.getReciboDTO().getMoneda().getId().compareTo(this.monedaLocal.getId()) == 0;
	}
	
	//devuelve un booleano que indica si es moneda local
	private boolean isMonedaLocal(String sigla){
		boolean out = false;	
		if (sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0) {
			out = true;
		}
		return out;
	}
	
	//Para el label del header, [0]:monedaLocal [1]:monedaExtranjera
	public String[] getHeaderMonto(){
		String header1 = "Monto " + this.monedaLocal.getSigla();
		String header2 = "Monto " + this.dato.getReciboDTO().getMoneda().getPos2();
		return new String[]{header1, header2};
	}
	
	/**
	 * @return datos de la lista movimientos pendientes
	 * [0]:total en moneda local
	 * [1]:total en moneda extranjera
	 * [2]:total deuda moneda local
	 * [3]:total deuda moneda extranjera
	 */
	public Object[] getDatosMovimientosPendientes(){
		
		double totalGs = 0;
		double totalDs = 0;
		double totalDeudaGs = 0;
		double totalDeudaDs = 0;
		
		for (ReciboDetalleDTO opd : this.getMovimientosPendientes()) {
			totalGs += opd.getMontoGs();
			totalDs += opd.getMontoDs();
			totalDeudaGs += opd.getSaldoGs();
			totalDeudaDs += opd.getSaldoDs();
		}
		
		return new Object[]{totalGs, totalDs, totalDeudaGs, totalDeudaDs};
	}
	
	/**
	 * @return datos de la lista movimientos a pagar
	 * [0]:total facturas en moneda local
	 * [1]:total facturas en moneda extranjera
	 * [2]:total formaPago en moneda local
	 * [3]:total formaPago en moneda extranjera
	 */
	public Object[] getDatosMovimientosApagar(){
		double totalGs = 0;
		double totalDs = 0;
		double totalFormaPagoGs = 0;
		double totalFormaPagoDs = 0;
		
		for (ReciboDetalleDTO opd : this.dato.getReciboDTO().getDetalles()) {
			totalGs += opd.getMontoGs();
			totalDs += opd.getMontoDs();			
		}
		
		for (ReciboFormaPagoDTO opfp : this.dato.getReciboDTO().getFormasPago()) {
			totalFormaPagoGs += opfp.getMontoGs();
			totalFormaPagoDs += opfp.getMontoDs();
		}
		
		return new Object[]{totalGs, totalDs, totalFormaPagoGs, totalFormaPagoDs};
	}
	
	/**
	 * Arma una descripcion de la forma de pago segun el tipo seleccionado..
	 */
	private String getDescripcion(){
		String out = this.nvoFormaPago.getTipo().getText();
		
		String siglaFP = this.nvoFormaPago.getTipo().getSigla();
		String siglaFPCH = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO;
		String siglaFPCT = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO;
		String siglaFPTC = Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO;
		String siglaFPTD = Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO;
		String siglaFPCA = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_AUTOCOBRANZA;
		String siglaFPDC = Configuracion.SIGLA_FORMA_PAGO_DEBITO_COBRANZA_CENTRAL;
		String siglaFPSF = Configuracion.SIGLA_FORMA_PAGO_SALDO_FAVOR_COBRADO;
		
		if (siglaFP.equals(siglaFPCH)) {
			out += " - " + this.nvoFormaPago.getBancoCta().getBancoDescripcion();
			
		} else if (siglaFP.equals(siglaFPTC)) {
			out += " - " + this.nvoFormaPago.getTarjetaTipo().getText();
			
		} else if (siglaFP.equals(siglaFPTD)) {
			out += " - " + this.nvoFormaPago.getTarjetaNumeroComprobante();
			
		} else if (siglaFP.equals(siglaFPCT)) {
			out += " - (" + this.nvoFormaPago.getChequeNumero() + ")";
			out += " - " + this.nvoFormaPago.getChequeBanco().getText();			
		
		} else if (siglaFP.equals(siglaFPCA)) {
			out += " - (" + this.nvoFormaPago.getChequeNumero() + ")";
			out += " - " + this.nvoFormaPago.getChequeBanco().getText();		
			
		} else if (siglaFP.equals(siglaFPDC)) {
			out += " - (" + this.nvoFormaPago.getReciboDebitoNro() + ")";		
		
		} else if (siglaFP.equals(siglaFPSF)) {
			out = this.nvoFormaPago.getDescripcion();		
		}		
		return out;
	}
	
	/**
	 * @return la lista de saldos a favor del cliente..
	 */
	public List<MyArray> getSaldosFavor() {
		List<MyArray> out = new ArrayList<MyArray>();
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			long idEmpresa = (long) this.dato.getReciboDTO().getCliente().getPos4();
			List<CtaCteEmpresaMovimiento> mvms = rr.getCtaCteSaldosFavor(idEmpresa);
			for (CtaCteEmpresaMovimiento movim : mvms) {
				MyArray my = new MyArray( 
						movim.getNroComprobante() + " (" + Utiles.getNumberFormat(movim.getSaldo() * -1) + ")",
						movim.getNroComprobante(),
						movim.getSaldo() * -1);
				my.setId(movim.getId());
				out.add(my);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;
	}
			
	/***************************************************/	
	
	
	/******************* GETTER/SETTER *****************/	
	
	/**
	 * utilDto de la app.
	 */
	public UtilDTO getDtoUtil() {
		UtilDTO u = (UtilDTO)super.getDtoUtil();
		return u;
	}
	
	/**
	 * @return los cobradores..
	 */
	public List<String> getCobradores() throws Exception {
		List<String> out = new ArrayList<String>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Funcionario> cobradores = rr.getFuncionariosCobradores();
		for (Funcionario cob : cobradores) {
			out.add(cob.getRazonSocial());
		}
		return out;
	}
	
	@DependsOn("filterNro")
	public List<ReciboDetalleDTO> getMovimientosPendientes_() {
		List<ReciboDetalleDTO> out = new ArrayList<ReciboDetalleDTO>();
		if (this.filterNro.trim().isEmpty()) {
			out = this.movimientosPendientes;
		} else {
			for (ReciboDetalleDTO item : this.movimientosPendientes) {
				if (item.getMovimiento().getNroComprobante().contains(this.filterNro)) {
					out.add(item);
				}
			}
		}
		return out;
	}
	
	@DependsOn({ "filterChequeNro", "filterChequeBanco", "filterChequeCliente" })
	public List<BancoChequeTercero> getChequesTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getChequesTercero(this.filterChequeCliente, "", this.filterChequeBanco, this.filterChequeNro, "", false, false);
	}
	
	public CajaPeriodoControlBody getDato() {
		return dato;
	}

	public void setDato(CajaPeriodoControlBody dato) {
		this.dato = dato;		
	}
	
	public List<ReciboDetalleDTO> getSelectedMovimientos() {
		return selectedMovimientos;
	}

	public void setSelectedMovimientos(List<ReciboDetalleDTO> selectedMovimientos) {
		this.selectedMovimientos = selectedMovimientos;
	}
	
	public List<ReciboDetalleDTO> getSelectedFacturaItems() {
		return selectedFacturaItems;
	}

	public void setSelectedFacturaItems(
			List<ReciboDetalleDTO> selectedFacturaItems) {
		this.selectedFacturaItems = selectedFacturaItems;
	}

	public String getFacturaItemsEliminar() {
		return facturaItemsEliminar;
	}

	public void setFacturaItemsEliminar(String facturaItemsEliminar) {
		this.facturaItemsEliminar = facturaItemsEliminar;
	}
	
	public List<ReciboFormaPagoDTO> getSelectedFormaPagoItems() {
		return selectedFormaPagoItems;
	}

	public void setSelectedFormaPagoItems(
			List<ReciboFormaPagoDTO> selectedFormaPagoItems) {
		this.selectedFormaPagoItems = selectedFormaPagoItems;
	}

	public String getFormaPagoItemsEliminar() {
		return formaPagoItemsEliminar;
	}

	public void setFormaPagoItemsEliminar(String formaPagoItemsEliminar) {
		this.formaPagoItemsEliminar = formaPagoItemsEliminar;
	}

	public List<ReciboDetalleDTO> getMovimientosPendientes() {
		return movimientosPendientes;
	}

	public void setMovimientosPendientes(
			List<ReciboDetalleDTO> movimientosPendientes) {
		this.movimientosPendientes = movimientosPendientes;
	}
	
	public ReciboFormaPagoDTO getNvoFormaPago() {
		return nvoFormaPago;
	}

	public void setNvoFormaPago(ReciboFormaPagoDTO nvoFormaPago) {
		this.nvoFormaPago = nvoFormaPago;
	}

	public boolean isCancelarTodo() {
		return cancelarTodo;
	}

	public void setCancelarTodo(boolean cancelarTodo) {
		this.cancelarTodo = cancelarTodo;
	}
	
	public ControlAgendaEvento getCtrAgenda() {
		return ctrAgenda;
	}
	
	private double getTipoCambio(){
		return this.dato.getReciboDTO().getTipoCambio();
	}
	
	@DependsOn("dato.reciboDTO.anticipo")
	public boolean isAnticipo() {
		return this.dato.getReciboDTO().isAnticipo();
	}
	
	@DependsOn("dato.reciboDTO.pendienteCobro")
	public boolean isPendienteCobro() {
		return this.dato.getReciboDTO().isPendienteCobro();
	}

	public MyArray getSelectedTarjetaCredito() {
		return selectedTarjetaCredito;
	}

	public void setSelectedTarjetaCredito(MyArray selectedTarjetaCredito) {
		this.selectedTarjetaCredito = selectedTarjetaCredito;
	}

	public ReciboDetalleDTO getNvoItem() {
		return nvoItem;
	}

	public void setNvoItem(ReciboDetalleDTO nvoItem) {
		this.nvoItem = nvoItem;
	}

	public String getFilterChequeNro() {
		return filterChequeNro;
	}

	public void setFilterChequeNro(String filterChequeNro) {
		this.filterChequeNro = filterChequeNro;
	}

	public String getFilterChequeBanco() {
		return filterChequeBanco;
	}

	public void setFilterChequeBanco(String filterChequeBanco) {
		this.filterChequeBanco = filterChequeBanco;
	}

	public String getFilterChequeCliente() {
		return filterChequeCliente;
	}

	public void setFilterChequeCliente(String filterChequeCliente) {
		this.filterChequeCliente = filterChequeCliente;
	}

	public BancoChequeTercero getSelectedChequeAutoCobranza() {
		return selectedChequeAutoCobranza;
	}

	public void setSelectedChequeAutoCobranza(
			BancoChequeTercero selectedChequeAutoCobranza) {
		this.selectedChequeAutoCobranza = selectedChequeAutoCobranza;
	}

	public String getFilterNro() {
		return filterNro;
	}

	public void setFilterNro(String filterNro) {
		this.filterNro = filterNro;
	}

	public ReciboDetalleDTO getNvoItem_dif_cambio() {
		return nvoItem_dif_cambio;
	}

	public void setNvoItem_dif_cambio(ReciboDetalleDTO nvoItem_dif_cambio) {
		this.nvoItem_dif_cambio = nvoItem_dif_cambio;
	}
}
