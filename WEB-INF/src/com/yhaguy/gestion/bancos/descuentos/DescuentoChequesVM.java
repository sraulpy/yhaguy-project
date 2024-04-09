package com.yhaguy.gestion.bancos.descuentos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.domain.IiD;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoDescuentoCheque;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.cheques.AssemblerBancoCheque;
import com.yhaguy.gestion.bancos.cheques.BancoChequeDTO;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class DescuentoChequesVM extends BodyApp {
	
	final static String ABM_DESCUENTO = "descuento";
	final static String ABM_ANTICIPO = "anticipo";
	final static String ABM_PRESTAMO_DEUDOR = "prestamo";
	final static String ABM_ENVIO = "envio";
	final static String ABM_PRESTAMO_ACREEDOR = "prestamo_acreedor";
	
	final static String ZUL_CANCELACION_PRESTAMOS = "/yhaguy/gestion/bancos/cancelacion_saldos.zul";
	
	private String tipo = "";
	
	private String filterRazonSocial = "";
	private String filterRuc = "";
	
	private String filter_ruc = "";
	private String filter_razonSocial = "";

	private String mensajeError = "";
	private BancoDescuentoChequeDTO chequeDescuento = new BancoDescuentoChequeDTO();
	private List<MyArray> selectedCheques = new ArrayList<MyArray>();
	private ReciboFormaPagoDTO nvoFormaPago = new ReciboFormaPagoDTO();
	private List<CtaCteEmpresaMovimiento> selectedMovimientos;
	private Empresa selectedEmpresa;
	
	@Init(superclass = true)
	public void init(@ExecutionParam("tipo") String tipo) {
		this.tipo = tipo;
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}	

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeError;
	}

	@Override
	public Assembler getAss() {
		return new AssemblerBancoDescuentoCheque();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.chequeDescuento;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.chequeDescuento = (BancoDescuentoChequeDTO) dto;
		if (this.chequeDescuento != null) {
			this.addFilter();
		}
	}
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return this.chequeDescuento.esNuevo();
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		BancoDescuentoChequeDTO bd = new BancoDescuentoChequeDTO();
		bd.setSucursalApp(this.getAcceso().getSucursalOperativa());
		bd.setFecha(new Date());
		bd.setAuxi(this.tipo.equals(ABM_ANTICIPO) ? "anticipo" : (this.tipo
				.equals(ABM_PRESTAMO_DEUDOR) ? "prestamo" : (this.tipo
				.equals(ABM_ENVIO) ? "envio" : (this.tipo
				.equals(ABM_PRESTAMO_ACREEDOR) ? "prestamo_acreedor" : ""))));
		return bd;
	}

	@Override
	public String getEntidadPrincipal() {
		return BancoDescuentoCheque.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(getEntidadPrincipal());
	}

	@Override
	public Browser getBrowser() {
		String where = this.tipo.equals(ABM_ANTICIPO) ? "auxi = 'anticipo'" : 
			(this.tipo.equals(ABM_PRESTAMO_DEUDOR) ? "auxi = 'prestamo'" : 
				(this.tipo.equals(ABM_PRESTAMO_ACREEDOR) ? "auxi = 'prestamo_acreedor'" :
				(this.tipo.equals(ABM_ENVIO) ? "auxi = 'envio'" : "auxi != 'anticipo' and auxi != 'prestamo' and auxi != 'envio'")));
		return new BancoDescuentoChequeBrowser(where);
	}
	
	@Override
	public void showImprimir() throws Exception {
		this.imprimir();
	}

	static final String[] ATT_CHEQUE_TERCERO = { "fecha", "banco.descripcion", "numero", "librado", "monto", "montoAcreditado" };
	static final String[] COLS_CHEQUE_TERCERO = { "Fecha", "Banco", "Numero", "Librado Por", "Monto" };
	
	static final String[] ATT_CHEQUE_PROPIO = { "numero", "banco.banco.descripcion", "beneficiario" };
	static final String[] COLS_CHEQUE_PROPIO = { "Número", "Banco", "Beneficiario" };

	@Command
	@NotifyChange("*")
	public void buscarChequeTercero() throws Exception {

		BuscarElemento b = new BuscarElemento();
		String whereCheque1 = "c.sucursalApp.id = " + this.getAcceso().getSucursalOperativa().getId();
		String whereCheque2 = "c.depositado  != 'true' ";
		String whereCheque3 = "c.descontado  != 'true' and c.anulado = 'false' ";
		String whereCheque4 = "c.moneda.id  = " + this.chequeDescuento.getMoneda().getId();

		b.setClase(BancoChequeTercero.class);
		b.setAtributos(ATT_CHEQUE_TERCERO);
		b.setNombresColumnas(COLS_CHEQUE_TERCERO);
		b.setAnchoColumnas(new String[]{"120px", "", "120px", "", "120px", "0px"});
		b.setTitulo("Cheques de Terceros");
		b.addWhere(whereCheque1);
		b.addWhere(whereCheque2);
		b.addWhere(whereCheque3);
		b.addWhere(whereCheque4);
		b.setWidth("1000px");
		b.show("%", 2);

		if (b.isClickAceptar()) {
			if (this.itemDuplicado(b.getSelectedItem())) {
				this.mensajeError(Configuracion.TEXTO_ERROR_ITEM_DUPLICADO);
			} else {
				if (this.tipo.equals(ABM_PRESTAMO_DEUDOR)) {
					this.chequeDescuento.getCheques_().add(b.getSelectedItem());
				} else {
					this.chequeDescuento.getCheques().add(b.getSelectedItem());
				}
			}
			if (this.tipo.equals(ABM_PRESTAMO_DEUDOR)) {
				for (MyArray cheq : this.chequeDescuento.getCheques_()) {
					if (cheq.getId().longValue() == b.getSelectedItem().getId().longValue()) {
						cheq.setPos6(cheq.getPos5());
					}
				}
			} else {
				for (MyArray cheq : this.chequeDescuento.getCheques()) {
					if (cheq.getId().longValue() == b.getSelectedItem().getId().longValue()) {
						cheq.setPos6(cheq.getPos5());
					}
				}
			}
		}
	}
	
	@Command
	@NotifyChange("*")
	public void buscarChequePropio() throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(BancoCheque.class);
		b.setAssembler(new AssemblerBancoCheque());
		b.setAtributos(ATT_CHEQUE_PROPIO);
		b.setNombresColumnas(COLS_CHEQUE_PROPIO);
		b.setTipos(new String[]{ Config.TIPO_NUMERICO, Config.TIPO_STRING, Config.TIPO_STRING });
		b.setAnchoColumnas(new String[]{ "120px", "", "" });
		b.setTitulo("Cheques Propios");
		b.setWidth("600px");
		b.show("%", 1);

		if (b.isClickAceptar()) {
			this.chequeDescuento.getChequesPropios().add((BancoChequeDTO) b.getSelectedItemDTO());
		}
	}

	@Command
	@NotifyChange("*")
	public void removerChequesSeleccionados() {

		if (this.selectedCheques.size() == 0 || this.selectedCheques == null) {
			this.mensajeError("No se han seleccionado elementos.");
			return;
		}

		this.chequeDescuento.getCheques_().removeAll(this.selectedCheques);
		this.chequeDescuento.getCheques().removeAll(this.selectedCheques);
		this.chequeDescuento.get_cheques().removeAll(this.selectedCheques);
		this.selectedCheques = new ArrayList<MyArray>();

	}
	
	@Command
	public void addFilter() {
		if (this.chequeDescuento != null && this.chequeDescuento.getBanco() != null) {
			this.filter_razonSocial = "BANCO " + this.chequeDescuento.getBanco().getPos1().toString().toUpperCase();
			BindUtils.postNotifyChange(null, null, this, "filter_razonSocial");
		}		
	}

	public boolean itemDuplicado(MyArray item) {
		boolean out = false;
		for (MyArray m : this.chequeDescuento.getCheques()) {
			if (m.getId() == item.getId()) {
				out = true;
			}
		}
		return out;
	}
	
	@Command
	@NotifyChange("*")
	public void formaDePago() throws Exception {
		this.asignarFormaPago();
	}
	
	@Command
	public void refreshCancelacion() {
		double can = 0;
		for (CtaCteEmpresaMovimiento mov : this.selectedMovimientos) {
			can += mov.getSaldo();
		}
		this.chequeDescuento.setCancelacionAnticipada(can);
		BindUtils.postNotifyChange(null, null, this.chequeDescuento, "cancelacionAnticipada");
	}
	
	/**
	 * @return el String concatenado..
	 */
	public String concat(String uno, String dos) {
		return uno + " - " + dos;
	}
	
	/**
	 * Despliega la ventana para asignar las formas de pago..
	 */
	private void asignarFormaPago() throws Exception {

		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Asignar Formas de Pago");
		wp.setHigth("400px");
		wp.setWidth("580px");
		wp.setDato(this);
		wp.setCheckAC(null);
		wp.show(Configuracion.PRESTAMOS_CC_FORMA_PAGO_ZUL);

		if (wp.isClickAceptar() == true) {
		}
	}

	@Command
	@NotifyChange("*")
	public void cerrarDescuentoCheque() throws Exception {

		if (this.validarCerrar() == false) {
			this.mensajeError(this.mensajeError);
			return;
		}

		RegisterDomain rr = RegisterDomain.getInstance();

		if (mensajeSiNo("Esta seguro de cerrar el registro?")) {

			this.chequeDescuento.setReadonly();
			this.chequeDescuento.setConfirmado(true);
			this.saveDTO(chequeDescuento);

			if (!this.tipo.equals(ABM_PRESTAMO_ACREEDOR)) {
				List<IiD> cheques = new ArrayList<IiD>();
				cheques.addAll(this.chequeDescuento.getCheques());
				boolean prestamo = this.tipo.equals(ABM_PRESTAMO_DEUDOR);
				rr.updateChequesDescontados(cheques, 
						(this.tipo.equals(ABM_ANTICIPO)? "ANTICIPO " :  
								(this.tipo.equals(ABM_ENVIO)? "ENVIO C.C. " : ""))
								+ this.chequeDescuento.getId() + "",
								this.chequeDescuento.getFecha(), prestamo);
			}
			
			if (this.tipo.equals(ABM_PRESTAMO_DEUDOR)) {
				ControlCuentaCorriente.addPrestamoInternoDeudor(this.chequeDescuento.getId(), this.getLoginNombre());
			}
			
			if (this.tipo.equals(ABM_PRESTAMO_ACREEDOR)) {
				ControlCuentaCorriente.addPrestamoInternoAcreedor(this.chequeDescuento.getId(), this.getLoginNombre());
			}
			
			this.setEstadoABMConsulta();
			this.mensajePopupTemporal("Documento Cerrado");
			this.actualizarDto();
		}
	}
	
	@Command
	@NotifyChange("*")
	public void addLiquidacionPrestamo() throws Exception {
		this.chequeDescuento.setLiq_registrado(true);
		this.saveDTO(chequeDescuento);
		this.mensajePopupTemporal("Liquidación de Préstamo Registrado..");
		this.actualizarDto();
		ControlBancoMovimiento.addMovimientoDescuentoCheques(this.chequeDescuento, this.getLoginNombre());
	}

	@NotifyChange("*")
	private void actualizarDto() throws Exception {
		this.chequeDescuento = (BancoDescuentoChequeDTO) this.getDTOById(BancoDescuentoCheque.class.getName(), this.chequeDescuento.getId());
	}
	
	@Override
	public boolean verificarAlGrabar() {
		boolean out = true;
		
		if (this.validarFormulario() == false) {
			this.mensajeError(this.mensajeError);
			return false;
		}
		return out;

	}

	/**
	 * @return true si se validaron correctamente los datos..
	 */
	private boolean validarFormulario() {
		boolean out = true;
		this.mensajeError = "No se puede completar la operación debido a: \n";

		if (this.chequeDescuento.getTotalChequesDescontado() == 0) {
			this.mensajeError += "\n - Debe ingresar el monto total.";
			out = false;
		}

		if (this.chequeDescuento.getMoneda() == null || this.chequeDescuento.getMoneda().esNuevo()) {
			this.mensajeError += "\n - Debe seleccionar una moneda.";
			out = false;
		}

		if (this.chequeDescuento.getObservacion().trim().length() == 0) {
			this.mensajeError += "\n - El campo observacion no puede estar vacio.";
			out = false;
		}

		return out;
	}

	private boolean validarCerrar() {

		boolean out = true;
		this.mensajeError = "";

		if (this.chequeDescuento.getTotalChequesDescontado() == 0) {
			this.mensajeError += "\n - Debe ingresar el monto total.";
			out = false;
		}

		if (this.chequeDescuento.getMoneda() == null || this.chequeDescuento.getMoneda().esNuevo()) {
			this.mensajeError += "\n - Debe seleccionar una moneda.";
			out = false;
		}

		if (this.chequeDescuento.getObservacion().trim().length() == 0) {
			this.mensajeError += "\n - El campo observacion no puede estar vacio.";
			out = false;
		}

		return out;
	}
	
	/**
	 * impresion del reporte..
	 */
	private void imprimir() {
		String titulo = this.tipo.equals(ABM_ANTICIPO) ? "Anticipo de Utilidad"
				: ( (this.tipo.equals(ABM_PRESTAMO_DEUDOR) || this.tipo.equals(ABM_PRESTAMO_ACREEDOR) ) ? "Préstamos Internos" : 
					(this.tipo.equals(ABM_ENVIO) ? this.getAliasFormularioCorriente() : "Descuento de Cheques"));
		ReporteYhaguy rep = new BancoDescuentoChequeReporte(this.chequeDescuento, titulo);
		rep.setOficio();
		rep.setBorrarDespuesDeVer(true);
		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return los bancos..
	 */
	public List<MyArray> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoCta> list = rr.getBancosCta();
		List<MyArray> out = new ArrayList<MyArray>();
		for (BancoCta banco : list) {
			MyArray my = new MyArray(new MyPair(banco.getBanco().getId(), banco.getBanco().getDescripcion().toUpperCase()));
			my.setId(banco.getId());
			my.setPos2(banco.getNroCuenta());
			out.add(my);
		}
		return out;
	}
	
	@DependsOn({ "filter_ruc", "filter_razonSocial" })
	public List<Empresa> getEmpresas_() throws Exception {
		if(this.filter_ruc.trim().isEmpty() && this.filter_razonSocial.trim().isEmpty()) return new ArrayList<Empresa>();
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresas(this.filter_ruc, "", this.filter_razonSocial, "");
	}
	
	@DependsOn({ "filterRuc", "filterRazonSocial" })
	public List<MyArray> getEmpresas() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		if(this.filterRuc.trim().isEmpty() && this.filterRazonSocial.trim().isEmpty())
			return out;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> list = rr.getEmpresas(this.filterRuc, this.filterRazonSocial);
		for (Object[] emp : list) {
			MyArray my = new MyArray(emp[2], emp[1]);
			my.setId((Long) emp[0]);
			out.add(my);
		}
		return out;
	}
	
	@DependsOn("selectedEmpresa")
	public List<CtaCteEmpresaMovimiento> getSaldos() throws Exception {
		if (this.selectedEmpresa == null) return new ArrayList<CtaCteEmpresaMovimiento>();
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getMovimientosConSaldo(Utiles.getFechaInicioOperaciones(), new Date(),
				Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR, 0, this.selectedEmpresa.getId(),
				this.chequeDescuento.getMoneda().getId());
	}
	
	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public BancoDescuentoChequeDTO getChequeDescuento() {
		return chequeDescuento;
	}

	public void setChequeDescuento(BancoDescuentoChequeDTO chequeDescuento) {
		this.chequeDescuento = chequeDescuento;
	}

	public List<MyArray> getSelectedCheques() {
		return selectedCheques;
	}

	public void setSelectedCheques(List<MyArray> selectedCheques) {
		this.selectedCheques = selectedCheques;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public ReciboFormaPagoDTO getNvoFormaPago() {
		return nvoFormaPago;
	}

	public void setNvoFormaPago(ReciboFormaPagoDTO nvoFormaPago) {
		this.nvoFormaPago = nvoFormaPago;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public String getFilterRuc() {
		return filterRuc;
	}

	public void setFilterRuc(String filterRuc) {
		this.filterRuc = filterRuc;
	}

	public String getFilter_ruc() {
		return filter_ruc;
	}

	public void setFilter_ruc(String filter_ruc) {
		this.filter_ruc = filter_ruc;
	}

	public String getFilter_razonSocial() {
		return filter_razonSocial;
	}

	public void setFilter_razonSocial(String filter_razonSocial) {
		this.filter_razonSocial = filter_razonSocial;
	}

	public List<CtaCteEmpresaMovimiento> getSelectedMovimientos() {
		return selectedMovimientos;
	}

	public void setSelectedMovimientos(List<CtaCteEmpresaMovimiento> selectedMovimientos) {
		this.selectedMovimientos = selectedMovimientos;
	}

	public Empresa getSelectedEmpresa() {
		return selectedEmpresa;
	}

	public void setSelectedEmpresa(Empresa selectedEmpresa) {
		this.selectedEmpresa = selectedEmpresa;
	}
}
