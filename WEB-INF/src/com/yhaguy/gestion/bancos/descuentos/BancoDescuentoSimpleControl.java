package com.yhaguy.gestion.bancos.descuentos;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;

public class BancoDescuentoSimpleControl extends SoloViewModel {

	private DescuentoChequesVM dato;	
	private ReciboFormaPagoDTO selectedFormaPago;
	
	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) DescuentoChequesVM dato) {
		this.dato = dato;
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(){
	}
	
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
	private Row rwChequeBanco;
	@Wire
	private Row rwNroCheque;
	@Wire
	private Row rwLibrador;
	@Wire
	private Row rwVencimiento;	
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
	private Row rwSaldoFavorCobrado;
	
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
		w = (Window) Executions.createComponents(Configuracion.PRESTAMOS_CC_ADD_FORMA_PAGO_ZUL_, this.mainComponent, null);
		Selectors.wireComponents(w, this, false);
		w.doOverlapped();
	}	
	
	@Command
	@NotifyChange({ "dato", "selectedFormasPago" })
	public void deleteFormaPago() {
		if (this.mensajeSiNo("Desea eliminar el ítem..")) {
			this.dato.getChequeDescuento().getFormasPago().remove(this.selectedFormaPago);
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
		String siglaFPTC = Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO;
		String siglaFPTD = Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO;
		String siglaFPDB = Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO;
		String siglaFPCT = Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO;
		String siglaFPDE = Configuracion.SIGLA_FORMA_PAGO_DEBITO_CTA_BANCARIA;

		if (siglaFP.equals(siglaFPTC)) {
			rwNroCheque.setVisible(false);
			rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(true);
			rwEmisor.setVisible(true);
			rwNroTarjeta.setVisible(true);
			rwProcesadora.setVisible(true);
			rwNroComprobante.setVisible(true);
			rwCuotas.setVisible(true);
			rwDepositoBanco.setVisible(false);
			rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false);
			rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false);
			rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dbxGs.setReadonly(false);
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getText());

		} else if (siglaFP.equals(siglaFPTD)) {
			rwNroCheque.setVisible(false);
			rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false);
			rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(true);
			rwProcesadora.setVisible(true);
			rwNroComprobante.setVisible(true);
			rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false);
			rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false);
			rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false);
			rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dbxGs.setReadonly(false);
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getText());

		} else if (siglaFP.equals(siglaFPDB)) {
			rwNroCheque.setVisible(false);
			rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false);
			rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false);
			rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false);
			rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(true);
			rwDepositoReferencia.setVisible(true);
			rwChequeBanco.setVisible(false);
			rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false);
			rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			dbxGs.setReadonly(false);
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getText());

		} else if (siglaFP.equals(siglaFPCT)) {
			rwNroCheque.setVisible(true); rwVencimiento.setVisible(true);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(true); rwLibrador.setVisible(true);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false);
			rwSaldoFavorCobrado.setVisible(false);
			dbxGs.setReadonly(false);
			
		} else if(siglaFP.compareTo(siglaFPDE) == 0) {
			rwNroCheque.setVisible(false); rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false); rwEmisor.setVisible(false);
			rwNroTarjeta.setVisible(false); rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false); rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(true); rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false); rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false); rwTimbradoRetencion.setVisible(false);
			rwTimbradoVencimiento.setVisible(false); 
			rwSaldoFavorCobrado.setVisible(false);
			dbxGs.setReadonly(false);
			dato.getNvoFormaPago().setDescripcion(dato.getNvoFormaPago().getTipo().getText());
		
		} else {
			rwNroCheque.setVisible(false);
			rwVencimiento.setVisible(false);
			rwTarjeta.setVisible(false);
			rwEmisor.setVisible(false);
			dbxGs.setReadonly(false);
			rwNroTarjeta.setVisible(false);
			rwProcesadora.setVisible(false);
			rwNroComprobante.setVisible(false);
			rwCuotas.setVisible(false);
			rwDepositoBanco.setVisible(false);
			rwDepositoReferencia.setVisible(false);
			rwChequeBanco.setVisible(false);
			rwLibrador.setVisible(false);
			rwNroRetencion.setVisible(false);
			rwTimbradoRetencion.setVisible(false);
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
		dato.getChequeDescuento().getFormasPago().add(dato.getNvoFormaPago());
		w.detach();
	}
	
	@Command 
	@NotifyChange("*")
	public void cancelarFormaPago(){
		w.detach();
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
		return new Object[] { valido, mensaje };
	}	
	
	
	/**
	 * GETS / SETS
	 */
	
	public UtilDTO getUtilDto() {
		return (UtilDTO) this.getDtoUtil();
	}
	
	/**
	 * @return descripcion de la forma de pago segun el tipo seleccionado..
	 */
	private String getDescripcion() {
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
	public Object[] getDatosFormasPago() {
		
		double totalFormaPagoGs = 0;
		double totalFormaPagoDs = 0;
		
		for (ReciboFormaPagoDTO item : dato.getChequeDescuento().getFormasPago()) {
			totalFormaPagoGs += item.getMontoGs();
			totalFormaPagoDs += item.getMontoDs();
		}
		
		return new Object[]{ totalFormaPagoGs, totalFormaPagoDs };
	}
	
	/**
	 * @return las formas de pago..
	 */
	public List<MyPair> getFormasDePago() {
		List<MyPair> out = new ArrayList<MyPair>();
		out.addAll(this.getUtilDto().getFormasDePago());
		out.remove(this.getUtilDto().getFormaPagoChequePropio());
		out.remove(this.getUtilDto().getFormaPagoChequeAutoCobranza());
		out.remove(this.getUtilDto().getFormaPagoDebitoCobranzaCentral());
		out.remove(this.getUtilDto().getFormaPagoRetencion());
		out.remove(this.getUtilDto().getFormaPagoRecaudacionCentral());
		out.remove(this.getUtilDto().getFormaPagoTransferenciaCentral());
		out.remove(this.getUtilDto().getFormaPagoSaldoFavorCobrado());
		out.remove(this.getUtilDto().getFormaPagoSaldoFavorGenerado());
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

	public MyArray getSelectedTarjetaCredito() {
		return selectedTarjetaCredito;
	}

	public void setSelectedTarjetaCredito(MyArray selectedTarjetaCredito) {
		this.selectedTarjetaCredito = selectedTarjetaCredito;
		BindUtils.postNotifyChange(null, null, dato.getNvoFormaPago(), "*");
	}
	
	public boolean getCheckMarkFormaPago(){
		boolean out = false;		
		if (dato.getChequeDescuento().getFormasPago().size() > 0) {
			out = true;
		}		
		return out;
	}

	public ReciboFormaPagoDTO getSelectedFormaPago() {
		return selectedFormaPago;
	}

	public void setSelectedFormaPago(ReciboFormaPagoDTO selectedFormaPago) {
		this.selectedFormaPago = selectedFormaPago;
	}

	public DescuentoChequesVM getDato() {
		return dato;
	}

	public void setDato(DescuentoChequesVM dato) {
		this.dato = dato;
	}
	
	/***********************************************************************************/	
}
