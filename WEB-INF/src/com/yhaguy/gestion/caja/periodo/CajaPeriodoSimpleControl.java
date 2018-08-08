package com.yhaguy.gestion.caja.periodo;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.gestion.bancos.cheques.BancoChequeDTO;
import com.yhaguy.gestion.bancos.cheques.WindowCheque;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;

public class CajaPeriodoSimpleControl extends SimpleViewModel {
	
	private CajaPeriodoControlBody dato = new CajaPeriodoControlBody();	
	
	private List<ReciboFormaPagoDTO> selectedFormasPago;

	@Init(superclass=true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) CajaPeriodoControlBody dato){
		this.dato = dato;		
		
		String labelF = this.getUs().formLabel(ID.F_CAJA_PLANILLA_ABM);
		this.setAliasFormularioCorriente(ID.F_CAJA_PLANILLA_ABM);		
		this.setTextoFormularioCorriente(labelF);
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose(){
	}
	
	/***************************** BUSCAR FUNCIONARIO ******************************/	
	
	private static String[] attFuncionario = {"empresa.nombre"};
	private static String[] columns = {"Apellido y Nombre"};
 	
	@Command 
	@NotifyChange("*")
	public void buscarResponsable() throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(Funcionario.class);
		b.setAtributos(attFuncionario);
		b.setNombresColumnas(columns);
		b.setTitulo("Buscar Funcionario");
		b.setWidth("600px");
		b.addOrden("empresa.nombre");
		b.show((String) this.dato.getReposicion().getPos1());
		if (b.isClickAceptar()) {
			MyArray selected = b.getSelectedItem();
			this.dato.getReposicion().setPos1(selected.getPos1());
			this.dato.getReposicion().setPos2(true);
			this.dato.getReposicion().setPos13(
					new MyPair(selected.getId().longValue()));
		}
	}
	
	/*******************************************************************************/
	
	
	/******************************* VENTA FORMA PAGO ******************************/
	
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
	
	private MyArray selectedTarjetaCredito = new MyArray("", new ArrayList<MyPair>());	
	private Window w;	
	private UtilDTO utilDto = this.getUtilDto();
	private MyPair formaPagoTarjetaCredito = utilDto.getFormaPagoTarjetaCredito();
	private MyPair formaPagoChequeTercero = utilDto.getFormaPagoChequeTercero();
	private MyPair formaPagoTarjetaDebito = utilDto.getFormaPagoTarjetaDebito();
	private MyPair formaPagoRetencion = utilDto.getFormaPagoRetencion();
	private MyPair formaPagoDepositoBanco = utilDto.getFormaPagoDepositoBancario();
	
	/**
	 * Despliega la ventana para asignación de Forma de Pago..
	 */
	@Command 
	@NotifyChange("*")
	public void asignarFormaPago() {
		
		inicializarFormaPago();
		
		if (dato.getNvoFormaPago().getMontoGs() <= 0.001) {
			this.mensajeError("El Total de Formas de Pago no "
					+ "debe superar al monto de la Venta..");
			return;
		}
		
		w = (Window) Executions.createComponents(Configuracion.VENTA_ADD_FORMA_PAGO_ZUL, 
				this.mainComponent, null);
		Selectors.wireComponents(w, this, false);
		w.doOverlapped();
	}	
	
	private ReciboFormaPagoDTO selectedFormaPago;
	
	@Command
	@NotifyChange({ "dato", "selectedFormaPago" })
	public void deleteFormaPago() {
		if (this.mensajeSiNo("Desea eliminar el ítem..")) {
			this.dato.getSelectedVenta().getFormasPago()
					.remove(this.selectedFormaPago);
			this.selectedFormaPago = null;
		}
	}
	
	/**
	 * Segun se seleccione el tipo de pago despliega los campos en la ventana 
	 * de forma de pago..
	 * @throws Exception 
	 */
	@Command @NotifyChange("*")
	public void seleccionarFormaPago() throws Exception {
		this.reloadFormaPago(this.dato.getNvoFormaPago());
		String siglaFP = dato.getNvoFormaPago().getTipo().getSigla();
		String siglaFPCH = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO;
		String siglaFPCT = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO;
		String siglaFPTC = Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO;
		String siglaFPTD = Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO;
		String siglaFPDB = Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO;
		String siglaFPRE = Configuracion.SIGLA_FORMA_PAGO_RETENCION;
		
		if (siglaFP.equals(siglaFPCH)) {
			showCheque();
			rwBanco.setVisible(true); rwChequera.setVisible(true);
			rwNroCheque.setVisible(true); rwVencimiento.setVisible(true);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dbxGs.setReadonly(true); dbxUS.setReadonly(true);
			
		} else if(siglaFP.equals(siglaFPTC)){
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(true); rwEmisor.setVisible(true);
			rwNroTarjeta.setVisible(true); rwProcesadora.setVisible(true);
			rwNroComprobante.setVisible(true); rwCuotas.setVisible(true);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getText());
			
		} else if(siglaFP.equals(siglaFPTD)){
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(true); rwProcesadora.setVisible(true);
			rwNroComprobante.setVisible(true); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getText());
			
		} else if(siglaFP.equals(siglaFPDB)){
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(true); rwDepositoReferencia.setVisible(true);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getText());
		
		} else if (siglaFP.equals(siglaFPCT)) {
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(true); rwVencimiento.setVisible(true);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(true); rwLibrador.setVisible(true);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dbxGs.setReadonly(false); dbxUS.setReadonly(false);
			
		} else if (siglaFP.equals(siglaFPRE)) {
			rwBanco.setVisible(false); rwChequera.setVisible(false);
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(true); rwTimbradoRetencion.setVisible(true);
			rwTimbradoVencimiento.setVisible(true);
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
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getText());		
		}
		this.selectedTarjetaCredito = new MyArray("", new ArrayList<MyPair>());
		dato.getNvoFormaPago().setTarjetaTipo(new MyPair());
		dato.getNvoFormaPago().setTarjetaProcesadora(new MyArray());
		dato.getNvoFormaPago().setTarjetaNumero("");
		dato.getNvoFormaPago().setTarjetaNumeroComprobante("");
	}
	
	/**
	 * Segun se seleccione el tipo de pago despliega los campos en la ventana 
	 * de forma de pago..
	 * @throws Exception 
	 */
	@Command
	@NotifyChange("*")
	public void seleccionarFormaPagoEgreso() throws Exception {
		ReciboFormaPagoDTO formaPago = (ReciboFormaPagoDTO) this.dato
				.getReposicion().getPos14();
		this.reloadFormaPago(formaPago);
		String siglaFP = formaPago.getTipo().getSigla();
		String siglaFPCH = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO;

		if (siglaFP.equals(siglaFPCH)) {
			this.showChequeEgreso(formaPago);
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
	}
	
	@Command 
	@NotifyChange("*")
	public void addFormaPago(){
		
		Object[] validar = this.validarFormaDePago();
		boolean valido = (boolean) validar[0];
		String mensaje = (String) validar[1];
		
		if (valido == false) {
			this.mensajeError(mensaje);
			return;
		}
		
		dato.getNvoFormaPago().setDescripcion(this.getDescripcion());
		dato.getSelectedVenta().getFormasPago().add(dato.getNvoFormaPago());
		w.detach();
	}
	
	@Command 
	@NotifyChange("*")
	public void cancelarFormaPago(){
		w.detach();
	}
	
	
	/**
	 * Invoca a la clase WindowCheque para desplegar la ventana de Cheque..
	 * @throws Exception 
	 */
	private void showCheque() throws Exception {
		String beneficiario = (String) dato.getSelectedVenta().getCliente().getPos2();
		MyArray moneda = dato.getSelectedVenta().getMoneda();
		
		WindowCheque w = new WindowCheque();
		w.getChequeDTO().setBeneficiario(beneficiario);
		w.getChequeDTO().setMoneda(moneda);	
		w.getChequeDTO().setMonto(this.getMontoCheque());
		w.setMontoRecibo(this.getMontoCheque());
		w.show(WindowPopup.NUEVO);
		
		if (w.isClickAceptar()) {			
			dato.getNvoFormaPago().setChequePropio(w.getChequeDTO(), w.getCuentaDTO().getBancoDescripcion());
			dato.getNvoFormaPago().setDepositoBancoCta(w.getCuentaDTO().toMyArray());
			
			this.actualizarMontoFormaPago(w.getChequeDTO());
			
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getSigla() 
					+ " - " + dato.getNvoFormaPago().getBancoCta().getBanco().getPos1()
					+ " - " + dato.getNvoFormaPago().getChequeNumero());
		}
	}
	
	/**
	 * Invoca a la clase WindowCheque para desplegar la ventana de Cheque..
	 * @throws Exception 
	 */
	private void showChequeEgreso(ReciboFormaPagoDTO formaPago)
			throws Exception {

		boolean reposicion = (boolean) this.dato.getReposicion().getPos16();
		String beneficiario = reposicion ? "AL PORTADOR" : (String) dato
				.getReposicion().getPos1();
		
		double monto = (double) this.dato.getReposicion().getPos5();
		MyPair moneda_ = (MyPair) dato.getReposicion().getPos9();
		MyArray moneda = new MyArray();
		moneda.setId(moneda_.getId());
		moneda.setPos1(moneda_.getText());

		WindowCheque w = new WindowCheque();
		w.getChequeDTO().setBeneficiario(beneficiario);
		w.getChequeDTO().setMoneda(moneda);
		w.getChequeDTO().setMonto(monto);
		w.setMontoRecibo(monto);
		w.show(WindowPopup.NUEVO);

		if (w.isClickAceptar()) {
			formaPago.setChequePropio(w.getChequeDTO(), w.getCuentaDTO()
					.getBancoDescripcion());
			formaPago.setBancoCta(w.getCuentaDTO());
			formaPago.setDepositoBancoCta(w.getCuentaDTO().toMyArray());
			formaPago.setMontoGs(w.getChequeDTO().getMonto());
			formaPago.setMontoDs(w.getChequeDTO().getMonto());
			formaPago.setDescripcion(formaPago.getTipo().getSigla() + " - "
					+ formaPago.getBancoCta().getBanco().getPos1() + " - "
					+ formaPago.getChequeNumero());

		} else {
			formaPago.setTipo(this.dato.getDtoUtil().getFormaPagoEfectivo());
		}
	}
	
	/**
	 * Actualizar el Monto de la Forma de pago segun el cheque
	 * @param cheque
	 */
	public void actualizarMontoFormaPago(BancoChequeDTO cheque){
		if (cheque.getMoneda().getId().compareTo(this.monedaLocal.getId()) == 0) {
			dato.getNvoFormaPago().setMontoGs(cheque.getMonto());
			dato.getNvoFormaPago().setMontoDs(cheque.getMonto() / dato.getSelectedVenta().getTipoCambio());
		} else {
			dato.getNvoFormaPago().setMontoDs(cheque.getMonto());
			dato.getNvoFormaPago().setMontoGs(cheque.getMonto() * dato.getSelectedVenta().getTipoCambio());
		}
	}	
	
	/**
	 * Inicializa el Objeto ReciboFormaPago
	 */
	private void inicializarFormaPago() {
		
		double montoGs = dato.getSelectedVenta().getTotalImporteGs();
		double montoDs = dato.getSelectedVenta().getTotalImporteDs();
		double totalGs = (double) this.getDatosFormasPago()[0];
		double totalDs = (double) this.getDatosFormasPago()[1];
		
		dato.setNvoFormaPago(new ReciboFormaPagoDTO());
		dato.getNvoFormaPago().setTipo(formaPagoEfectivo);
		dato.getNvoFormaPago().setMoneda(this.monedaSelectedVenta());
		dato.getNvoFormaPago().setMontoGs(montoGs - totalGs);
		dato.getNvoFormaPago().setMontoDs(montoDs - totalDs);
	}
	
	/**
	 * Valida los datos ingresados en la Forma de Pago..
	 */
	private Object[] validarFormaDePago() {
		boolean valido = true;
		String mensaje = Configuracion.TEXTO_ERROR_VALIDACION;
		ReciboFormaPagoDTO formaPago = this.dato.getNvoFormaPago();
		long idFormaPago = formaPago.getTipo().getId().longValue();
		long idFormaPagoChequeTercero = this.formaPagoChequeTercero.getId().longValue();
		long idFormaPagoTarjetaCredito = this.formaPagoTarjetaCredito.getId().longValue();
		long idFormaPagoTarjetaDebito = this.formaPagoTarjetaDebito.getId().longValue();
		long idFormaPagoRetencion = this.formaPagoRetencion.getId().longValue();
		long idFormaPagoDepBanco = this.formaPagoDepositoBanco.getId().longValue();

		// Cuando la forma de pago es cheque..
		if (idFormaPago == idFormaPagoChequeTercero) {

			if (formaPago.getChequeBanco().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar una Cuenta..";
			}

			if (formaPago.getChequeNumero().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número de Cheque..";
			}
			
			if (formaPago.getChequeLibrador().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el Librador del Cheque..";
			}
			
			if (formaPago.getChequeFecha() == null) {
				valido = false;
				mensaje += "\n - Debe ingresar la fecha del Cheque..";
			}

		// Cuando la forma de pago es tarjeta de credito..
		} else if (idFormaPago == idFormaPagoTarjetaCredito) {

			if (((String) this.selectedTarjetaCredito.getPos1()).isEmpty()) {
				valido = false;
				mensaje += "\n - Debe seleccionar el Tipo de Tarjeta..";
			}

			if (formaPago.getTarjetaTipo().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar el Emisor de la Tarjeta..";
			}
			
			if (formaPago.getTarjetaProcesadora().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar la Procesadora de la Tarjeta..";
			}

			if (formaPago.getTarjetaNumero().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número de Tarjeta..";
			}

			if (formaPago.getTarjetaNumeroComprobante().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número del Comprobante..";
			}
		
		// Forma de pago con tarjeta de debito..
		} else if (idFormaPago == idFormaPagoTarjetaDebito) {
			
			if (formaPago.getTarjetaProcesadora().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar la Procesadora de la Tarjeta..";
			}
			
			if (formaPago.getTarjetaNumero().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número de Tarjeta..";
			}

			if (formaPago.getTarjetaNumeroComprobante().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número del Comprobante..";
			}
		
		// Forma de Pago con Retencion..
		} else if (idFormaPago == idFormaPagoRetencion) {
			
			if (formaPago.getRetencionNumero().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el número..";
			}
			
			if (formaPago.getRetencionTimbrado().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el timbrado..";
			}
			
			if (formaPago.getRetencionVencimiento() == null) {
				valido = false;
				mensaje += "\n - Debe ingresar el vencimiento del timbrado..";
			}
		
		// Forma de Pago con Deposito bancario..	
		} else if (idFormaPago == idFormaPagoDepBanco) {
			
			if (formaPago.getDepositoBancoCta().esNuevo() == true) {
				valido = false;
				mensaje += "\n - Debe seleccionar la Cuenta de Banco..";
			}
			
			if (formaPago.getDepositoNroReferencia().isEmpty()) {
				valido = false;
				mensaje += "\n - Debe ingresar el Número de referencia..";
			}
			
		}

		if (dato.getNvoFormaPago().getMontoGs() <= 0) {
			valido = false;
			mensaje += "\n - El monto debe ser mayor a cero..";
		}

		return new Object[] { valido, mensaje };
	}
	
	/*******************************************************************************/
	
	
	/*********************************** UTILES ************************************/
	
	private MyArray monedaLocal = this.dato.getDtoUtil().getMonedaGuaraniConSimbolo();
	private MyPair formaPagoEfectivo = this.dato.getDtoUtil().getFormaPagoEfectivo();
	
	@Command 
	public void dolarizarEgreso(){
		double valorGs = (double) this.dato.getReposicion().getPos5();
		double tc = (double) this.dato.getReposicion().getPos4();
		this.dato.getReposicion().setPos6(valorGs / tc); 
		BindUtils.postNotifyChange(null, null, this.dato.getReposicion(), "pos6");
	}
	
	@Command 
	public void guaranizarEgreso(){
		double valorDs = (double) this.dato.getReposicion().getPos6();
		double tc = (double) this.dato.getReposicion().getPos4();
		this.dato.getReposicion().setPos5(valorDs * tc); 
		BindUtils.postNotifyChange(null, null, this.dato.getReposicion(), "pos5");
	}
	
	@Command
	public void refreshTipoCambio(){
		MyPair moneda = (MyPair) this.dato.getReposicion().getPos9();
		double tc = dato.getDtoUtil().getCambioCompraBCP(moneda);
		this.dato.getReposicion().setPos4(tc);
		BindUtils.postNotifyChange(null, null, this.dato.getReposicion(), "pos4");
	}
	
	@Command 
	@NotifyChange("*")
	public void dolarizarFormaPago(){
		double montoGs = dato.getNvoFormaPago().getMontoGs();
		dato.getNvoFormaPago().setMontoDs(montoGs / dato.getSelectedVenta().getTipoCambio());
	}
	
	@Command 
	@NotifyChange("*")
	public void guaranizarFormaPago(){
		double montoDs = dato.getNvoFormaPago().getMontoDs();
		dato.getNvoFormaPago().setMontoGs(montoDs * dato.getSelectedVenta().getTipoCambio());
	}
	
	/**
	 * Arma una descripcion de la forma de pago segun el tipo seleccionado..
	 */
	private String getDescripcion(){
		String out = this.dato.getNvoFormaPago().getTipo().getText();
		
		String siglaFP = this.dato.getNvoFormaPago().getTipo().getSigla();
		String siglaFPCH = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO;
		String siglaFPTC = Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO;
		String siglaFPTD = Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO;
		
		if (siglaFP.compareTo(siglaFPCH) == 0) {
			out += " - " + dato.getNvoFormaPago().getBancoCta().getBancoDescripcion();
			
		} else if (siglaFP.compareTo(siglaFPTC) == 0) {
			out += " - " + dato.getNvoFormaPago().getTarjetaTipo().getText();
			out += " - " + dato.getNvoFormaPago().getTarjetaNumeroComprobante();
			
		} else if (siglaFP.compareTo(siglaFPTD) == 0) {
			out += " - " + dato.getNvoFormaPago().getTarjetaNumeroComprobante();
		}
		
		return out;
	}
	
	/**
	 * @return datos de las formas de Pago de la Venta..
	 * [0]: importe Forma Pago Moneda Local..
	 * [1]: importe Forma Pago Moneda Extranjera..
	 */
	public Object[] getDatosFormasPago(){
		
		double totalFormaPagoGs = 0;
		double totalFormaPagoDs = 0;
		
		for (ReciboFormaPagoDTO item : dato.getSelectedVenta().getFormasPago()) {
			totalFormaPagoGs += item.getMontoGs();
			totalFormaPagoDs += item.getMontoDs();
		}
		
		return new Object[]{totalFormaPagoGs, totalFormaPagoDs};
	}	
	
	/*******************************************************************************/
	
	
	/******************************** GETTER/SETTER ********************************/
	
	@DependsOn("selectedFormasPago")
	public boolean isDeleteFormaPagoDisabled() {
		return this.selectedFormasPago == null
				|| this.selectedFormasPago.size() == 0;
	}
	
	/**
	 * @return las formas de pago..
	 */
	public List<MyPair> getFormasDePago() {
		List<MyPair> out = new ArrayList<MyPair>();
		out.addAll(this.getUtilDto().getFormasDePago());
		out.remove(this.getUtilDto().getFormaPagoChequePropio());	
		return out;
	}
	
	/**
	 * @return las formas de pago..
	 */
	public List<MyPair> getFormasDePagoEgreso() {
		List<MyPair> out = new ArrayList<MyPair>();
		out.add(this.getUtilDto().getFormaPagoEfectivo());
		out.add(this.getUtilDto().getFormaPagoChequePropio());
		return out;
	}
	
	private MyPair monedaSelectedVenta() {
		MyArray moneda = this.dato.getSelectedVenta().getMoneda();
		return new MyPair(moneda.getId());
	}
	
	public UtilDTO getUtilDto() {
		return (UtilDTO) this.getDtoUtil();
	}
	
	public CajaPeriodoControlBody getDato() {
		return dato;
	}

	public void setDato(CajaPeriodoControlBody dato) {
		this.dato = dato;
	}
	
	private double getMontoCheque(){		
		if (dato.getSelectedVenta().isMonedaLocal() == true) {
			return dato.getNvoFormaPago().getMontoGs();
		} else {
			return dato.getNvoFormaPago().getMontoDs();
		}
	}

	public MyArray getSelectedTarjetaCredito() {
		return selectedTarjetaCredito;
	}

	public void setSelectedTarjetaCredito(MyArray selectedTarjetaCredito) {
		this.selectedTarjetaCredito = selectedTarjetaCredito;
		BindUtils.postNotifyChange(null, null, dato.getNvoFormaPago(), "*");
	}
	
	public boolean getCheckMarkFormaPago(){
		boolean out = false;		
		if (dato.getSelectedVenta().getFormasPago().size() > 0) {
			out = true;
		}		
		return out;
	}
	
	@DependsOn("dato.reposicion.pos10")
	public String getLabelFuncionario() {
		return "Funcionario:";
	}

	public ReciboFormaPagoDTO getSelectedFormaPago() {
		return selectedFormaPago;
	}

	public void setSelectedFormaPago(ReciboFormaPagoDTO selectedFormaPago) {
		this.selectedFormaPago = selectedFormaPago;
	}

	public List<ReciboFormaPagoDTO> getSelectedFormasPago() {
		return selectedFormasPago;
	}

	public void setSelectedFormasPago(List<ReciboFormaPagoDTO> selectedFormasPago) {
		this.selectedFormasPago = selectedFormasPago;
	}
}
