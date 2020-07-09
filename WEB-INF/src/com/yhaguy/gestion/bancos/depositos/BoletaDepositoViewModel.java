package com.yhaguy.gestion.bancos.depositos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.domain.Domain;
import com.coreweb.domain.IiD;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.CajaAuditoria;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.RecaudacionCentral;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoMovimiento;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.bancos.libro.BancoMovimientoDTO;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class BoletaDepositoViewModel extends BodyApp {
	
	private final static String[] ATT_CHEQUE = { "fecha", "banco.descripcion", "numero", "librado", "moneda.sigla", "monto" };
	private final static String[] COLUMNAS = { "Fecha", "Banco", "Numero", "Librado Por", "Moneda", "Importe" };
	private final static String[] WIDTHS = { "120px", "100px", "100px", "Librado Por", "100px", "100px" };
	private final static String[] TIPOS = { Config.TIPO_STRING, Config.TIPO_STRING, Config.TIPO_STRING,
			Config.TIPO_STRING, Config.TIPO_STRING, Config.TIPO_NUMERICO };
	
	static final String ZUL_INSERT_ITEM = "/yhaguy/gestion/bancos/insertarPlanillas.zul";
	static final String ZUL_INSERT_RCC = "/yhaguy/gestion/bancos/insertarRecaudacionCentral.zul";

	private BancoBoletaDepositoDTO bancoDeposito = new BancoBoletaDepositoDTO();
	private List<MyArray> selectedCheques = new ArrayList<MyArray>();
	private String mensajeError = "";
	
	private List<Object[]> eventosAgenda = new ArrayList<Object[]>();
	
	private String filterNumeroPlanilla = "%";
	private String filterCheque = "";
	private String filterNumeroRecibo = "";
	private double totalRecaudacionCentral = 0;
	
	private String selectedPlanilla;
	
	private MyArray chequeRecaudacionCentral;
	private List<MyArray> selectedsRecaudacionesCentral = new ArrayList<MyArray>();
	private MyArray selectedRecaudacion;
	
	private ControlBancoMovimiento ctrBanco = new ControlBancoMovimiento(null);
	
	@Wire
	private Popup popRecaudacion;

	@Init(superclass = true)
	public void init() {
	}

	@AfterCompose(superclass = true)
	public void AfterCompose() {
	}	

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeError;
	}

	@Override
	public Assembler getAss() {
		return new AssemblerBancoBoletaDeposito();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.bancoDeposito;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.bancoDeposito = (BancoBoletaDepositoDTO) dto;
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		BancoBoletaDepositoDTO bd = new BancoBoletaDepositoDTO();
		bd.setSucursalApp(this.getAcceso().getSucursalOperativa());
		return bd;
	}

	@Override
	public String getEntidadPrincipal() {
		return BancoBoletaDeposito.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(getEntidadPrincipal());
	}

	@Override
	public Browser getBrowser() {
		return new BancoBoletaDepositoBrowser();
	}
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return !this.bancoDeposito.isCerrado();
	}
	
	@Override
	public void showImprimir() {
		this.imprimirBoleta();
	}
	
	@Override
	public boolean verificarAlGrabar() {
		boolean out = true;		
		try {
			out = this.validarFormulario();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return out;

	}

	@Command
	@NotifyChange("*")
	public void buscarChequeTercero() throws Exception {

		if (this.bancoDeposito.getNroCuenta() == null) {
			this.mensajeError("Debe seleccionar una cuenta primero.");
			return;
		}

		BuscarElemento b = new BuscarElemento();
		//String whereCheque1 = "c.moneda.id =" + this.bancoDeposito.getNroCuenta().getMoneda().getId();
		String whereCheque2 = "c.sucursalApp.id = " + this.getAcceso().getSucursalOperativa().getId();
		String whereCheque3 = "c.depositado  != true ";
		String whereCheque6 = "c.descontado != true ";

		b.setClase(BancoChequeTercero.class);
		b.setAtributos(ATT_CHEQUE);
		b.setNombresColumnas(COLUMNAS);
		b.setTipos(TIPOS);
		b.setTitulo("Cheques de Terceros");
		b.setAnchoColumnas(WIDTHS);
		//b.addWhere(whereCheque1);
		b.addWhere(whereCheque2);
		b.addWhere(whereCheque3);
		b.addWhere(whereCheque6);

		b.setWidth("1000px");
		b.show("%", 2);

		if (b.isClickAceptar()) {
			this.bancoDeposito.getCheques().add(b.getSelectedItem());
		}
	}
	
	@Command
	@NotifyChange("*")
	public void buscarChequeRechazado() throws Exception {

		if (this.bancoDeposito.getNroCuenta() == null) {
			this.mensajeError("Debe seleccionar una cuenta primero.");
			return;
		}

		BuscarElemento b = new BuscarElemento();
		String whereCheque1 = "c.sucursalApp.id = " + this.getAcceso().getSucursalOperativa().getId();
		String whereCheque2 = "c.rechazado  = 'true' ";

		b.setClase(BancoChequeTercero.class);
		b.setAtributos(ATT_CHEQUE);
		b.setNombresColumnas(COLUMNAS);
		b.setTitulo("Cheques Rechazados");
		b.addWhere(whereCheque1);
		b.addWhere(whereCheque2);

		b.setWidth("1000px");
		b.show("%", 2);

		if (b.isClickAceptar()) {
			this.bancoDeposito.getCheques().add(b.getSelectedItem());
		}
	}
	
	@Command
	@NotifyChange("*")
	public void insertarChequesDePlanilla() {
		if (!this
				.mensajeSiNo("Desea insertar los cheques de la Planilla de Caja Nro. "
						+ this.selectedPlanilla)) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		CajaPeriodo planilla;
		try {
			planilla = rr.getCajaPlanillas(this.selectedPlanilla).get(0);

			for (BancoChequeTercero cheque : planilla.getChequesAlDia()) {
				MyArray my = new MyArray();
				my.setId(cheque.getId());
				my.setPos1(cheque.getFecha());
				my.setPos2(new MyPair(cheque.getBanco().getId(), cheque
						.getBanco().getDescripcion()));
				my.setPos3(cheque.getNumero());
				my.setPos4(cheque.getLibrado());
				my.setPos5(new MyPair());
				my.setPos6(cheque.getMonto());
				my.setPos7(cheque.isDepositado());
				my.setPos8(new MyPair(cheque.getSucursalApp().getId(), cheque
						.getSucursalApp().getDescripcion()));
				this.bancoDeposito.getCheques().add(my);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"No se encontraron los cheques..Contacte con un administrador del sistema..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}

	@Command
	@NotifyChange("*")
	public void removerChequesSeleccionados() {
		if (this.selectedCheques.size() == 0 || this.selectedCheques == null) {
			this.mensajeError("No se han seleccionado elementos.");
			return;
		}

		this.bancoDeposito.getCheques().removeAll(this.selectedCheques);
		this.selectedCheques = new ArrayList<MyArray>();

	}
	
	@Command
	@NotifyChange("*")
	public void insertarPlanilla() throws Exception {
		this.selectedPlanilla = null;
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setModo(WindowPopup.NUEVO);
		wp.setHigth("300px");
		wp.setWidth("400px");
		wp.setTitulo("Insertar Planillas..");
		wp.show(ZUL_INSERT_ITEM);
		if (wp.isClickAceptar()) {
			String planillas = this.bancoDeposito.getPlanillaCaja();
			if (planillas.isEmpty()) {
				this.bancoDeposito.setPlanillaCaja(this.selectedPlanilla);
			} else {
				this.bancoDeposito.setPlanillaCaja(this.bancoDeposito.getPlanillaCaja() + ";" + this.selectedPlanilla);
			}
		}
	}
	
	@Command
	@NotifyChange("*")
	public void addRecaudacionCentral() throws Exception {
		this.selectedsRecaudacionesCentral = new ArrayList<MyArray>();
		this.chequeRecaudacionCentral = this.inicializarRecaudacionCentral();		
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setModo(WindowPopup.NUEVO);
		wp.setHigth("500px");
		wp.setWidth("600px");
		wp.setCheckAC(new ValidadorRecaudacionCentral());
		wp.setTitulo("Recaudación Casa Central");
		wp.show(ZUL_INSERT_RCC);
		if (wp.isClickAceptar()) {
			List<MyPair> rccs = new ArrayList<MyPair>();
			for (MyArray rcc : this.selectedsRecaudacionesCentral) {
				MyPair myprcc = new MyPair(rcc.getId(), rcc.getPos1() + " - " + rcc.getPos2());
				myprcc.setSigla(String.valueOf(rcc.getPos4()));
				rccs.add(myprcc);
			}
			this.chequeRecaudacionCentral.setPos10(rccs);
			this.chequeRecaudacionCentral.setPos11("RCC");
			this.bancoDeposito.getCheques().add(this.chequeRecaudacionCentral);
		}
	}
	
	@Command
	@NotifyChange("selectedRecaudacion")
	public void verItems(@BindingParam("item") MyArray item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedRecaudacion = item;
		this.popRecaudacion.open(parent, "start_before");
	}
	
	/**
	 * inicializa la recaudacion central..
	 */
	private MyArray inicializarRecaudacionCentral() {
		MyArray my = new MyArray();
		my.setPos1(new Date());
		my.setPos2(new MyPair());
		my.setPos3("");
		my.setPos4("");
		my.setPos5(new MyPair());
		my.setPos6((double) 0.0);
		my.setPos7(false);
		my.setPos8(new MyPair(this.getSucursal().getId()));
		my.setPos9(new MyPair());
		return my;
	}
	
	/**
	 * Impresion de la Boleta..
	 */
	private void imprimirBoleta() {
		List<Object[]> data = new ArrayList<Object[]>();

		for (MyArray cheque : this.bancoDeposito.getCheques()) {
			Object[] obj = new Object[] {
					Utiles.getDateToString((Date) cheque.getPos1(),
							Utiles.DD_MM_YYYY),
					cheque.getPos2().toString().toUpperCase(),
					cheque.getPos3().toString().toUpperCase(),
					(double) cheque.getPos6() };
			data.add(obj);
		}

		ReporteYhaguy rep = new ReporteBoletaDeposito(this.bancoDeposito);
		rep.setDatosReporte(data);
		rep.setBorrarDespuesDeVer(true);

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}

	public boolean itemDuplicado(MyArray item) {
		boolean out = false;
		for (MyArray m : this.bancoDeposito.getCheques()) {
			if (m.getId() == item.getId()) {
				out = true;
			}
		}
		return out;
	}

	@Command
	@NotifyChange({ "opChequesDisabled", "efectivoDisabled" })
	public void updateChequesDisabled() {
	}

	@Command
	@NotifyChange("*")
	public void cerrarBoletaDeposito() throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();

		if (mensajeSiNo("Esta seguro de cerrar la boleta de depósito?")) {
			this.bancoDeposito.setReadonly();
			this.bancoDeposito.setCerrado(true);
			this.saveDTO(bancoDeposito);
			this.volcarBancoMovimiento();
			List<IiD> cheques = new ArrayList<IiD>();
			cheques.addAll(this.bancoDeposito.getCheques());
			rr.updateChequesDepositados(cheques, this.bancoDeposito.getNumeroBoleta(), this.bancoDeposito.getFecha());
			this.setEstadoABMConsulta();

			this.mensajePopupTemporal("Documento Cerrado");
			this.actualizarDto();
			this.generarCajaAuditoria();
		}
	}
	
	/**
	 * generar caja auditoria..
	 */
	private void generarCajaAuditoria() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoBoletaDeposito deposito = (BancoBoletaDeposito) rr.getObject(BancoBoletaDeposito.class.getName(),
				this.bancoDeposito.getId());

		if (deposito.getTotalEfectivo() > 0) {
			CajaAuditoria efectivo = new CajaAuditoria();
			efectivo.setConcepto(CajaAuditoria.CONCEPTO_DEPOSITO_EFECTIVO);
			efectivo.setDescripcion("DEPÓSITO NRO. " + deposito.getNumeroBoleta() + " - "
					+ deposito.getNroCuenta().getBancoDescripcion() + "");
			efectivo.setFecha(deposito.getFecha());
			efectivo.setImporte(deposito.getTotalEfectivo());
			efectivo.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
			efectivo.setResumen("");
			efectivo.setNumero(deposito.getNumeroBoleta());
			efectivo.setSupervisor(this.getLoginNombre());
			rr.saveObject(efectivo, this.getLoginNombre());
		}

		// actualiza auditoria de caja..
		for (BancoChequeTercero cheque : deposito.getCheques()) {
			CajaAuditoria chq = new CajaAuditoria();
			chq.setConcepto(CajaAuditoria.CONCEPTO_DEPOSITO_CHEQUE);
			chq.setDescripcion("DEPÓSITO NRO. (" + deposito.getNumeroBoleta() + " - "
					+ deposito.getNroCuenta().getBancoDescripcion() + ") CHEQUE: " + cheque.getNumero() + " - "
					+ cheque.getBanco().getDescripcion());
			chq.setFecha(deposito.getFecha());
			chq.setImporte(cheque.getMonto());
			chq.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
			chq.setResumen(deposito.getNumeroBoleta());
			chq.setNumero(cheque.getNumero());
			chq.setSupervisor(this.getLoginNombre());
			rr.saveObject(chq, this.getLoginNombre());
		}
	}

	/**
	 * vuelca los movimientos en el libro banco..
	 */
	private void volcarBancoMovimiento() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoMovimientoDTO bancoMov = new BancoMovimientoDTO();
		bancoMov.setFecha(this.bancoDeposito.getFecha());
		bancoMov.setMonto(this.bancoDeposito.getMonto());
		bancoMov.setNroReferencia(this.bancoDeposito.getNumeroBoleta());
		bancoMov.setDescripcion("BOLETA DE DEPOSITO NRO. " + this.bancoDeposito.getNumeroBoleta());
		MyPair cuenta = new MyPair();
		cuenta.setId(this.bancoDeposito.getNroCuenta().getId());
		bancoMov.setNroCuenta(cuenta);
		bancoMov.setTipoMovimiento(this.getDtoUtil().getTmBancoDepositoBancario());

		Domain d = new AssemblerBancoMovimiento().dtoToDomain(bancoMov);
		rr.saveObject(d, this.getLoginNombre());
	}
	
	/**
	 * Validador recaudacion central..
	 */
	class ValidadorRecaudacionCentral implements VerificaAceptarCancelar {

		private String mensaje;
		
		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			if (chequeRecaudacionCentral.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe seleccionar un cheque..";
			}
			
			if (selectedsRecaudacionesCentral.size() == 0) {
				out = false;
				this.mensaje += "\n - Debe seleccionar las recaudaciones a descontar..";
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

	private boolean validarFormulario() throws Exception {

		boolean out = true;
		this.mensajeError = "No se puede completar la operación debido a: \n";
		
		if (this.bancoDeposito.esNuevo() && this.ctrBanco.isBancoDepositoDuplicada(this.bancoDeposito.getNumeroBoleta())) {
			out = false;
			this.mensajeError += "Ya existe un Depósito con el numero: " + this.bancoDeposito.getNumeroBoleta();
		}

		if (this.bancoDeposito.getNroCuenta() == null) {
			this.mensajeError += "\n - Debe seleccionar una cuenta.";
			out = false;
		}

		if (this.bancoDeposito.getNumeroBoleta().trim().length() == 0) {
			this.mensajeError += "\n - Debe asignar el número de boleta.";
			out = false;
		}

		if (this.bancoDeposito.getFecha() == null) {
			this.mensajeError += "\n - Debe asignar la fecha de la boleta de depósito.";
			out = false;
		}

		if (this.bancoDeposito.getMonto() == 0) {
			this.mensajeError += "\n - El monto total debe ser superior a 0.";
			out = false;
		}

		for (MyArray m : this.bancoDeposito.getCheques()) {
			if ((boolean) m.getPos7() == true) {
				this.mensajeError += "\n - Uno de los cheques seleccionados ya ha sido depositado con anterioridad.";
				out = false;
				break;
			}
		}

		return out;
	}	
	
	public void addEventoAgenda(int tipoAgenda, String claveAgenda, int tipoDetalle, String texto, String link) {
		Object[] evento = new Object[5];
		evento[0] = tipoAgenda;
		evento[1] = claveAgenda;
		evento[2] = tipoDetalle;
		evento[3] = texto;
		evento[4] = link;
		this.eventosAgenda.add(evento);
	}

	public void addEventoAgenda(String texto) {
		Object[] evento = new Object[5];
		evento[0] = ControlAgendaEvento.NORMAL;
		evento[1] = this.bancoDeposito.getNumeroBoleta();
		evento[2] = 0;
		evento[3] = texto;
		evento[4] = "";
		this.eventosAgenda.add(evento);
	}

	@Override
	public boolean getAgendaDeshabilitado() throws Exception {
		return false;
	}

	@NotifyChange("*")
	private void actualizarDto() throws Exception {
		this.bancoDeposito = (BancoBoletaDepositoDTO) this.getDTOById(BancoBoletaDeposito.class.getName(),
				this.bancoDeposito.getId());
	}

	@NotifyChange("totalDeposito")
	@Command
	public void actualizarTotalDeposito() {

	}
	
	/**
	 * GETS / SETS
	 */		
	@DependsOn("filterNumeroPlanilla")
	public List<String> getPlanillasCaja() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCajaPlanillasNumeros(this.filterNumeroPlanilla);
	}
	
	@DependsOn({ "bancoDeposito.nroCuenta", "bancoDeposito.numeroBoleta",
			"bancoDeposito.fecha", "bancoDeposito.planillaCaja",
			"bancoDeposito.monto", "bancoDeposito.observacion" })
	public boolean isDetalleVisible() {
		return (!this.bancoDeposito.getNroCuenta().esNuevo())
				&& (!this.bancoDeposito.getNumeroBoleta().isEmpty())
				&& (this.bancoDeposito.getFecha() != null)
				&& (this.bancoDeposito.getMonto() > 0)
				&& (!this.bancoDeposito.getObservacion().isEmpty());
	}
	
	/**
	 * @return los clientes para recaudacion central..
	 */
	public List<MyPair> getClientes() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Cliente> clientes = rr.getClientesByRuc(Configuracion.RUC_YHAGUY_REPUESTOS);
		for (Cliente cliente : clientes) {
			MyPair myp = new MyPair(cliente.getId(), cliente.getRazonSocial());
			out.add(myp);
		}
		return out;
	}
	
	/**
	 * @return los cheques de tercero..
	 */
	@DependsOn("filterCheque")
	public List<MyArray> getCheques() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoChequeTercero> cheques = rr.getChequesTercero("", "", "",
				this.filterCheque, "", false, false);
		for (BancoChequeTercero cheque : cheques) {
			MyArray my = new MyArray();
			my.setId(cheque.getId());
			my.setPos1(cheque.getFecha());
			my.setPos2(new MyPair(cheque.getBanco().getId(), cheque.getBanco().getDescripcion()));
			my.setPos3(cheque.getNumero());
			my.setPos4(cheque.getLibrado());
			my.setPos5(new MyPair(cheque.getMoneda().getId()));
			my.setPos6(cheque.getMonto());
			my.setPos7(false);
			my.setPos8(new MyPair(this.getSucursal().getId()));
			out.add(my);
		}
		return out;
	}
	
	/**
	 * @return las recaudaciones con saldo..
	 */
	@DependsOn("filterNumeroRecibo")
	public List<MyArray> getRecaudacionesPendientes() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<RecaudacionCentral> rccs = rr.getRecaudacionesCentralPendientes();
		for (RecaudacionCentral rcc : rccs) {
			MyArray my = new MyArray(rcc.getNumeroRecibo(), rcc.getImporteGs(), rcc.getSaldoGs(), (double) 0.0);
			my.setId(rcc.getId());
			if (rcc.getNumeroRecibo().contains(this.filterNumeroRecibo)) {
				out.add(my);
			}
		}
		return out;
	}
	
	@Override
	public int getCtrAgendaTipo() {
		return ControlAgendaEvento.NORMAL;
	}

	@Override
	public String getCtrAgendaKey() {
		return this.bancoDeposito.getNumeroBoleta();
	}

	@Override
	public String getCtrAgendaTitulo() {
		return "[Boleta de Depósito: " + this.getCtrAgendaKey() + "]";
	}
	
	public List<Object[]> getEventosAgenda() {
		return eventosAgenda;
	}

	public void setEventosAgenda(List<Object[]> eventosAgenda) {
		this.eventosAgenda = eventosAgenda;
	}
	
	public boolean isEfectivoDisabled() {
		return false;
	}

	public double getTotalChequesBanco() {
		double sum = 0;
		for (MyArray m : this.bancoDeposito.getCheques()) {
			if (((IiD) m.getPos2()).getId()
					.compareTo(((IiD) this.bancoDeposito.getNroCuenta().getBanco().getPos7()).getId()) == 0) {
				sum += (double) m.getPos6();
			}
		}
		return sum;
	}

	public double getTotalChequesOtrosBancos() {
		double sum = 0;
		for (MyArray m : this.bancoDeposito.getCheques()) {
			if (((IiD) m.getPos2()).getId()
					.compareTo(((IiD) this.bancoDeposito.getNroCuenta().getBanco().getPos7()).getId()) != 0) {
				sum += (double) m.getPos6();
			}
		}
		return sum;
	}

	public double getTotalDeposito() {
		double sum = 0;

		sum = this.bancoDeposito.getTotalEfectivo() + this.getTotalChequesBanco() + this.getTotalChequesOtrosBancos();

		return sum;
	}
	
	public BancoBoletaDepositoDTO getBancoDeposito() {
		return bancoDeposito;
	}

	public void setBancoDeposito(BancoBoletaDepositoDTO bancoDeposito) {
		this.bancoDeposito = bancoDeposito;
	}

	public List<MyArray> getSelectedCheques() {
		return selectedCheques;
	}

	public void setSelectedCheques(List<MyArray> selectedCheques) {
		this.selectedCheques = selectedCheques;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public boolean getBuscarDisabled() {
		if (this.isDeshabilitado()) {
			return true;
		}
		return false;
	}

	public BancoCtaDTO getCuenta() {
		return this.bancoDeposito.getNroCuenta();
	}

	public void setCuenta(BancoCtaDTO cuenta) {
		this.bancoDeposito.setNroCuenta(cuenta);
	}

	public String getMonedaCuenta() {
		if (this.bancoDeposito == null) {
			return "";

		} else if (this.bancoDeposito != null && this.bancoDeposito.getNroCuenta() == null) {
			return "";
		} else {
			return (String) this.bancoDeposito.getNroCuenta().getMoneda().getPos1();
		}
	}

	public String getBancoCuenta() {

		if (this.bancoDeposito == null) {
			return "";
		} else if (this.bancoDeposito != null && this.bancoDeposito.getNroCuenta() == null) {
			return "";
		} else {
			return (String) this.bancoDeposito.getNroCuenta().getBancoDescripcion();
		}
	}
	
	public String getNroCuenta() {
		if(this.bancoDeposito.getNroCuenta() != null){
			return this.bancoDeposito.getNroCuenta().getNroCuenta();
		}
		return "";
	
	}

	public String getFilterNumeroPlanilla() {
		return filterNumeroPlanilla;
	}

	public void setFilterNumeroPlanilla(String filterNumeroPlanilla) {
		this.filterNumeroPlanilla = filterNumeroPlanilla;
	}

	public String getSelectedPlanilla() {
		return selectedPlanilla;
	}

	public void setSelectedPlanilla(String selectedPlanilla) {
		this.selectedPlanilla = selectedPlanilla;
	}

	public MyArray getChequeRecaudacionCentral() {
		return chequeRecaudacionCentral;
	}

	public void setChequeRecaudacionCentral(MyArray chequeRecaudacionCentral) {
		this.chequeRecaudacionCentral = chequeRecaudacionCentral;
	}

	public List<MyArray> getSelectedsRecaudacionesCentral() {
		return selectedsRecaudacionesCentral;
	}

	public void setSelectedsRecaudacionesCentral(
			List<MyArray> selectedsRecaudacionesCentral) {
		this.selectedsRecaudacionesCentral = selectedsRecaudacionesCentral;
	}

	public MyArray getSelectedRecaudacion() {
		return selectedRecaudacion;
	}

	public void setSelectedRecaudacion(MyArray selectedRecaudacion) {
		this.selectedRecaudacion = selectedRecaudacion;
	}

	public String getFilterCheque() {
		return filterCheque;
	}

	public void setFilterCheque(String filterCheque) {
		this.filterCheque = filterCheque;
	}

	public String getFilterNumeroRecibo() {
		return filterNumeroRecibo;
	}

	public void setFilterNumeroRecibo(String filterNumeroRecibo) {
		this.filterNumeroRecibo = filterNumeroRecibo;
	}

	public double getTotalRecaudacionCentral() {
		return totalRecaudacionCentral;
	}

	public void setTotalRecaudacionCentral(double totalRecaudacionCentral) {
		this.totalRecaudacionCentral = totalRecaudacionCentral;
	}
}

/**
 * Reporte de Boleta de Deposito..
 */
class ReporteBoletaDeposito extends ReporteYhaguy {
	
	private BancoBoletaDepositoDTO boleta;	
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Banco", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Número", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, true);
	
	public ReporteBoletaDeposito(BancoBoletaDepositoDTO boleta) {
		this.boleta = boleta;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Boleta de Depósito");
		this.setDirectorio("banco");
		this.setNombreArchivo("Boleta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.boleta.getNumeroBoleta();
		String sucursal = this.boleta.getSucursalApp().getText();
		String cuenta = this.boleta.getNroCuenta().getNroCuenta();
		String banco = (String) this.boleta.getNroCuenta().getBancoDescripcion();
		String planilla = this.boleta.getPlanillaCaja();
		String total = Utiles.getNumberFormat(this.boleta.getMonto());
		String efectivo = Utiles.getNumberFormat(this.boleta.getTotalEfectivo());
		String cheque = Utiles.getNumberFormat(this.boleta.getTotalImporteCheques());

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Nro. Boleta", numero))
				.add(this.textoParValor("Cuenta", cuenta))
				.add(this.textoParValor("Banco", banco))
				.add(this.textoParValor("Sucursal", sucursal)));
		
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Planilla Caja", planilla))
				.add(this.textoParValor("Total Depósito", total))
				.add(this.textoParValor("Efectivo", efectivo))
				.add(this.textoParValor("Cheque", cheque)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
