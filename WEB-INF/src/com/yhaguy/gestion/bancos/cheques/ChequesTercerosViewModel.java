package com.yhaguy.gestion.bancos.cheques;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class ChequesTercerosViewModel extends SimpleViewModel {
	
	static final String FILTRO_TODOS = "TODOS";
	static final String FILTRO_DEPOSITADOS = "DEPOSITADOS";
	static final String FILTRO_DESCONTADOS = "DESCONTADOS";
	static final String FILTRO_RECHAZADOS = "RECHAZADOS";
	static final String FILTRO_RECHAZOS_INTERNOS = "RECHAZOS INTERNOS";
	static final String FILTRO_A_DEPOSITAR = "A DEPOSITAR";
	
	static final String ZUL_REGISTRAR_CHEQUE = "/yhaguy/gestion/bancos/registrarChequeTercero.zul";
	
	private String filterPlanilla = "";
	private String filterRecibo = "";
	private String filterVenta = "";
	private String filterReembolso = "";
	private String filterDeposito = "";
	private String filterDescuento = "";	
	private String filterRazonSocial = "";
	private String filterRuc = "";
	private String filterBanco = "";
	private String filterNumero = "";
	private String filterLibrador = "";
	private String filterVendedor = "";
	private String filterCliente = "";
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String filterImporteGs = "";
	
	private boolean reembolsados = true;
	private boolean sin_reembolso = true;
	private boolean parcial = true;
	
	private MyArray selectedCheque;
	private String selectedFiltro = FILTRO_TODOS;
	private MyArray nvoCheque;
	
	private double totalImporte = 0;
	private Date fechaRechazo = new Date();
	private boolean rechazoInterno = false;
	
	@Wire
	private Popup pop_img;

	@Init(superclass = true)
	public void init() {
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void registrarChequeRechazado() throws Exception {
		boolean rechazado = (boolean) this.selectedCheque.getPos8();
		if (rechazado) {
			this.mensajePopupTemporalWarning("Ya esta registrado como cheque rechazado..");
			this.selectedCheque = null;
			return;
		}
		long idCheque = this.selectedCheque.getId();
		String motivo = (String) this.selectedCheque.getPos21();
		if (this.rechazoInterno) {
			ControlBancoMovimiento.registrarChequeRechazadoInterno(idCheque, motivo, this.fechaRechazo, this.getLoginNombre());
		} else {
			ControlBancoMovimiento.registrarChequeRechazado(idCheque, motivo, this.fechaRechazo, this.getLoginNombre());
		}
		
		Clients.showNotification("Cheque registrado..");
		this.selectedCheque = null;
		this.fechaRechazo = new Date();
		this.rechazoInterno = false;
	}
	
	@Command
	public void listadoCheques() throws Exception {
		this.reporteCheques();
	}
	
	@Command
	public void registrarCheque() throws Exception {
		this.registrarChequeTercero();
	}
	
	@Command
	@NotifyChange("selectedGasto")
	public void verImagen(@BindingParam("item") MyArray item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedCheque = item;
		this.pop_img.open(200, 100);
		Clients.evalJavaScript("setImage('" + this.getUrlImagen() + "')");
	}
	
	@Command
	@NotifyChange("selectedFiltro")
	public void selectFilter(@BindingParam("filter") int filter) {
		if (filter == 1) {
			this.selectedFiltro = FILTRO_TODOS;
		} else if (filter == 2) {
			this.selectedFiltro = FILTRO_DEPOSITADOS;
		} else if (filter == 3) {
			this.selectedFiltro = FILTRO_DESCONTADOS;
		} else if (filter == 4) {
			this.selectedFiltro = FILTRO_RECHAZADOS;
		} else if (filter == 5) {
			this.selectedFiltro = FILTRO_RECHAZOS_INTERNOS;
		} else {
			this.selectedFiltro = FILTRO_A_DEPOSITAR;
		}
	}
	
	/**
	 * registra un nuevo cheque manual..
	 */
	private void registrarChequeTercero() throws Exception {	
		this.inicializarCheque();
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setModo(WindowPopup.NUEVO);
		wp.setHigth("300px");
		wp.setWidth("400px");
		wp.setCheckAC(new ValidadorRegistroCheque());
		wp.setTitulo("Registrar Cheque de Tercero");
		wp.show(ZUL_REGISTRAR_CHEQUE);
		if (wp.isClickAceptar()) {
			ControlBancoMovimiento.registrarChequeTerceroManual(this.nvoCheque, this.getLoginNombre());
			this.mensajePopupTemporal("Cheque Registrado..", 5000);
		}
	}
	
	/**
	 * inicializa el nvoCheque..
	 */
	private void inicializarCheque() {
		this.nvoCheque = new MyArray();
		this.nvoCheque.setPos1(new MyPair());
		this.nvoCheque.setPos2("");
		this.nvoCheque.setPos3(new Date());
		this.nvoCheque.setPos4(new MyPair());
		this.nvoCheque.setPos5((double) 0.0);
		this.nvoCheque.setPos6(this.getAcceso().getSucursalOperativa());
	}
	
	@DependsOn({ "filterPlanilla", "filterRecibo", "filterVenta", "filterDeposito", "filterDescuento",
			"filterRazonSocial", "filterRuc", "filterBanco", "filterNumero", "filterLibrador", "filterVendedor",
			"selectedFiltro", "filterReembolso", "filterFechaDD", "filterFechaMM", "filterFechaAA", "filterImporteGs",
			"reembolsados", "sin_reembolso", "parcial" })
	public List<MyArray> getCheques() throws Exception {
		this.selectedCheque = null;
		String depositado = null;
		String descontado = null;
		String rechazado = null;
		String rechazoInterno = null;
		if (this.selectedFiltro.equals(FILTRO_A_DEPOSITAR)) {
			depositado = "false";
			descontado = "false";
			rechazado = "false";
			rechazoInterno = "false";
		}
		if (this.selectedFiltro.equals(FILTRO_DEPOSITADOS)) {
			depositado = "true";
		}
		if (this.selectedFiltro.equals(FILTRO_DESCONTADOS)) {
			descontado = "true";
		}
		if (this.selectedFiltro.equals(FILTRO_RECHAZADOS)) {
			rechazado = "true";
		}
		if (this.selectedFiltro.equals(FILTRO_RECHAZOS_INTERNOS)) {
			rechazoInterno = "true";
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoChequeTercero> cheques = rr.getChequesTercero(
				this.filterPlanilla, this.filterRecibo, this.filterVenta,
				this.filterReembolso, this.filterDeposito, this.filterDescuento,
				this.filterRazonSocial, this.filterRuc, this.filterBanco,
				this.filterNumero, this.filterLibrador, this.filterVendedor, 
				depositado, descontado, rechazado, rechazoInterno, null, null, null, null,
				this.getFilterFecha(), this.filterImporteGs, true);
		return this.chequesToMyArray(cheques);
	}
	
	private List<MyArray> chequesToMyArray(List<BancoChequeTercero> cheques) {
		this.totalImporte = 0;
		List<MyArray> out = new ArrayList<MyArray>();
		for (BancoChequeTercero cheque : cheques) {			
			boolean cli = cheque.getCliente() == null;
			MyArray my = new MyArray();
			my.setId(cheque.getId());
			my.setPos1(cli ? "N/D" : cheque.getCliente().getRazonSocial());
			my.setPos2(cli ? "N/D" : cheque.getCliente().getRuc());
			my.setPos3(cheque.getBanco().getDescripcion());
			my.setPos4(cheque.getNumero());
			my.setPos5(cheque.getFecha());
			my.setPos6(cheque.getLibrado());
			my.setPos7(cheque.getMonto());
			my.setPos8(cheque.isRechazado());
			my.setPos9(cheque.getNumeroPlanilla().isEmpty() ? "- - -" : cheque.getNumeroPlanilla());
			my.setPos10(cheque.getNumeroRecibo().isEmpty() ? "- - -" : cheque.getNumeroRecibo());
			my.setPos11(cheque.getNumeroVenta().isEmpty() ? "- - -" : cheque.getNumeroVenta());
			my.setPos12(cheque.getNumeroDeposito().isEmpty() ? "- - -" : cheque.getNumeroDeposito());
			my.setPos13(cheque.getNumeroDescuento().isEmpty() ? "- - -" : cheque.getNumeroDescuento());
			my.setPos14(cheque.isDiferido() ? "DIFERIDO" : "AL DIA");
			my.setPos15(cheque.isDepositado());
			my.setPos16(cheque.isDescontado());
			my.setPos17(cheque.getVendedor());
			my.setPos18(cheque.getNumeroReembolso().isEmpty() ? "- - -" : cheque.getNumeroReembolso());
			my.setPos19(cheque.isReembolsado());
			my.setPos20(cheque.isRechazado() || cheque.isRechazoInterno() ? (cheque.isReembolsado()? (cheque.isCancelado()? "color:green" : "color:orange") : "color:red") : "");
			my.setPos21(cheque.getObservacion());
			boolean added = false;
			if (this.selectedFiltro.equals(FILTRO_RECHAZADOS) || this.selectedFiltro.equals(FILTRO_RECHAZOS_INTERNOS)) {
				if (this.isReembolsados() && this.isSin_reembolso() && this.isParcial()) {
					out.add(my);
					this.totalImporte += cheque.getMonto();
					added = true;
				} else if(this.isReembolsados() && cheque.isReembolsado()) {
					if (!this.isParcial() && cheque.isCancelado()) {
						out.add(my);
						this.totalImporte += cheque.getMonto();
						added = true;
					} else if (this.isParcial()) {
						out.add(my);
						this.totalImporte += cheque.getMonto();
						added = true;
					}
					
				} else if (this.isSin_reembolso() && !cheque.isReembolsado()) {
					out.add(my);
					this.totalImporte += cheque.getMonto();
					added = true;
				} else if (this.isParcial() && !cheque.isCancelado() && cheque.isReembolsado() && !added) {
					out.add(my);
					this.totalImporte += cheque.getMonto();
					added = true;
				}
			} else {
				out.add(my);
				this.totalImporte += cheque.getMonto();
				added = true;
			}			
		}
		BindUtils.postNotifyChange(null, null, this, "totalImporte");
		return out;
	}
	
	/**
	 * reporte de cheques..
	 */
	private void reporteCheques() throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();
		for (MyArray cheque : this.getCheques()) {
			String librador = (String) (cheque.getPos6() == null ? "---" : cheque.getPos6());
			String razonSocial = (String) cheque.getPos1();
			Object[] obj = new Object[] {
					m.dateToString((Date) cheque.getPos5(), Utiles.DD_MM_YY),
					cheque.getPos4(),
					cheque.getPos3().toString().toUpperCase(),
					razonSocial.toUpperCase(),
					librador.toUpperCase(),
					(boolean) cheque.getPos15() ? "SI" : "NO",
					(boolean) cheque.getPos16() ? "SI" : "NO", cheque.getPos7() };
			data.add(obj);
		}

		ReporteChequesDeTerceros rep = new ReporteChequesDeTerceros(this
				.getAcceso().getSucursalOperativa().getText());
		rep.setDatosReporte(data);
		rep.setBorrarDespuesDeVer(true);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/**
	 * Validador recaudacion central..
	 */
	class ValidadorRegistroCheque implements VerificaAceptarCancelar {

		private String mensaje;
		
		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			MyPair banco = (MyPair) nvoCheque.getPos1();
			String numero = (String) nvoCheque.getPos2();
			MyPair cliente = (MyPair) nvoCheque.getPos4();
			double importe = (double) nvoCheque.getPos5();
			
			if (banco.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe asignar un banco..";
			}
			
			if(numero.isEmpty()){
				out = false;
				this.mensaje += "\n - Debe ingresar el número de cheque..";
			}
			
			if (importe <= 0) {
				out = false;
				this.mensaje += "\n - El importe no puede ser cero..";
			}
			
			if (cliente.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe asignar el cliente..";
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
	
	/**
	 * @return los filtros..
	 */
	public List<String> getFiltros() {
		List<String> out = new ArrayList<String>();
		out.add(FILTRO_TODOS);
		out.add(FILTRO_DEPOSITADOS);
		out.add(FILTRO_DESCONTADOS);
		out.add(FILTRO_RECHAZADOS);
		out.add(FILTRO_A_DEPOSITAR);
		return out;
	}
	
	/**
	 * @return la url de la foto..
	 */
	private String getUrlImagen() {
		if (this.selectedCheque == null)
			return "http://190.211.240.30/images/default.png";
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
			return Configuracion.URL_IMAGES_PUBLIC_MRA + "cheques/" + this.selectedCheque.getPos4() + ".png";
		}
		return Configuracion.URL_IMAGES_PUBLIC_BAT + "cheques/" + this.selectedCheque.getPos4() + ".png";
	}
	
	/**
	 * @return los clientes para recaudacion central..
	 */
	@DependsOn("filterCliente")
	public List<MyPair> getClientes() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Cliente> clientes = rr.getClientes(this.filterCliente);
		for (Cliente cliente : clientes) {
			MyPair myp = new MyPair(cliente.getId(), cliente.getRazonSocial());
			out.add(myp);
		}
		return out;
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
	
	private AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
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

	public String getFilterBanco() {
		return filterBanco;
	}

	public void setFilterBanco(String filterBanco) {
		this.filterBanco = filterBanco;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public String getFilterLibrador() {
		return filterLibrador;
	}

	public void setFilterLibrador(String filterLibrador) {
		this.filterLibrador = filterLibrador;
	}

	public MyArray getSelectedCheque() {
		return selectedCheque;
	}

	public void setSelectedCheque(MyArray selectedCheque) {
		this.selectedCheque = selectedCheque;
	}

	public String getFilterPlanilla() {
		return filterPlanilla;
	}

	public void setFilterPlanilla(String filterPlanilla) {
		this.filterPlanilla = filterPlanilla;
	}

	public String getFilterRecibo() {
		return filterRecibo;
	}

	public void setFilterRecibo(String filterRecibo) {
		this.filterRecibo = filterRecibo;
	}

	public String getFilterVenta() {
		return filterVenta;
	}

	public void setFilterVenta(String filterVenta) {
		this.filterVenta = filterVenta;
	}

	public String getFilterDeposito() {
		return filterDeposito;
	}

	public void setFilterDeposito(String filterDeposito) {
		this.filterDeposito = filterDeposito;
	}

	public String getFilterDescuento() {
		return filterDescuento;
	}

	public void setFilterDescuento(String filterDescuento) {
		this.filterDescuento = filterDescuento;
	}

	public String getSelectedFiltro() {
		return selectedFiltro;
	}

	public void setSelectedFiltro(String selectedFiltro) {
		this.selectedFiltro = selectedFiltro;
	}

	public double getTotalImporte() {
		return totalImporte;
	}

	public void setTotalImporte(double totalImporte) {
		this.totalImporte = totalImporte;
	}

	public String getFilterVendedor() {
		return filterVendedor;
	}

	public void setFilterVendedor(String filterVendedor) {
		this.filterVendedor = filterVendedor;
	}

	public MyArray getNvoCheque() {
		return nvoCheque;
	}

	public void setNvoCheque(MyArray nvoCheque) {
		this.nvoCheque = nvoCheque;
	}

	public String getFilterCliente() {
		return filterCliente;
	}

	public void setFilterCliente(String filterCliente) {
		this.filterCliente = filterCliente;
	}

	public String getFilterReembolso() {
		return filterReembolso;
	}

	public void setFilterReembolso(String filterReembolso) {
		this.filterReembolso = filterReembolso;
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

	public String getFilterImporteGs() {
		return filterImporteGs;
	}

	public void setFilterImporteGs(String filterImporteGs) {
		this.filterImporteGs = filterImporteGs;
	}

	public boolean isReembolsados() {
		return reembolsados;
	}

	public void setReembolsados(boolean reembolsados) {
		this.reembolsados = reembolsados;
	}

	public boolean isSin_reembolso() {
		return sin_reembolso;
	}

	public void setSin_reembolso(boolean sin_reembolso) {
		this.sin_reembolso = sin_reembolso;
	}

	public boolean isParcial() {
		return parcial;
	}

	public void setParcial(boolean parcial) {
		this.parcial = parcial;
	}

	public Date getFechaRechazo() {
		return fechaRechazo;
	}

	public void setFechaRechazo(Date fechaRechazo) {
		this.fechaRechazo = fechaRechazo;
	}

	public boolean isRechazoInterno() {
		return rechazoInterno;
	}

	public void setRechazoInterno(boolean rechazoInterno) {
		this.rechazoInterno = rechazoInterno;
	}
}

/**
 * Reporte de Cheques de Terceros..
 */
class ReporteChequesDeTerceros extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Vto.", TIPO_STRING, 25);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Banco", TIPO_STRING, 55);
	static DatosColumnas col3 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col3_ = new DatosColumnas("Librador", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Dep.", TIPO_STRING, 20);
	static DatosColumnas col5 = new DatosColumnas("Des.", TIPO_STRING, 20);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE, 35, true);

	private String sucursal;

	public ReporteChequesDeTerceros(String sucursal) {
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col3_);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Cheques de Clientes");
		this.setDirectorio("banco");
		this.setNombreArchivo("cheque-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}
