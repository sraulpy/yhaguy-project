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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ChequesViewModel extends SimpleViewModel {
	
	static final String FILTRO_TODOS = "TODOS";
	static final String FILTRO_AL_DIA = "AL DIA";
	static final String FILTRO_DIFERIDOS = "DIFERIDOS";
	static final String FILTRO_CONCILIADOS = "CONCILIADOS";
	static final String FILTRO_NO_CONCILIADOS = "NO CONCILIADOS";
	static final String FILTRO_A_VENCER = "A VENCER";
	static final String FILTRO_PENDIENTE_COBRO = "PENDIENTE COBRO";
	
	static final String SOURCE_ALDIA = "/yhaguy/gestion/bancos/impresion_cheque_al_dia.zul";
	static final String SOURCE_DIFERIDO = "/yhaguy/gestion/bancos/impresion_cheque_diferido.zul";
	
	private Date fechaCobro;
	
	private String filterCuenta = "";
	private String filterBanco = "";
	private String filterNro = "";	
	private String filterBeneficiario = "";
	private String filterNumeroCaja = "";
	private String filterNumeroOrdenPago = "";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private String selectedFiltro = FILTRO_TODOS;
	private double totalImporteGs = 0;
	
	private MyArray selectedItem;
	private MyArray selectedItem_;
	private Window win;
	
	@Init(superclass = true)
	public void init() {
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	
	/******************** COMANDOS *********************/
	
	@Command
	public void imprimirCheque() {
		this.imprimirCheque(this.selectedItem);
	}
	
	@Command
	public void listadoCheques() throws Exception {
		this.reporteCheques();
	}
	
	@Command
	@NotifyChange("selectedFiltro")
	public void selectFilter(@BindingParam("filter") int filter) {
		if (filter == 1) {
			this.selectedFiltro = FILTRO_TODOS;
		} else if (filter == 2) {
			this.selectedFiltro = FILTRO_AL_DIA;
		} else if (filter == 3) {
			this.selectedFiltro = FILTRO_DIFERIDOS;
		} else if (filter == 4) {
			this.selectedFiltro = FILTRO_NO_CONCILIADOS;
		} else if (filter == 5) {
			this.selectedFiltro = FILTRO_A_VENCER;
		} else if (filter == 6) {
			this.selectedFiltro = FILTRO_PENDIENTE_COBRO;
		}
	}
	
	@Command
	@NotifyChange("*")
	public void registrarCheque() throws Exception {
		this.showChequePropio();
	}
	
	@Command
	@NotifyChange("*")
	public void setChequeCobrado(@BindingParam("cobrado") boolean cobrado, @BindingParam("comp") Popup comp) throws Exception {
		if (cobrado && this.fechaCobro == null) {
			return;
		}
		this.registrarChequeCobrado(this.selectedItem_, cobrado, this.fechaCobro);
		comp.close();
		this.fechaCobro = null;
	}
	
	@Command
	@NotifyChange("selectedItem_")
	public void openChequeCobrado(@BindingParam("cheque") MyArray cheque,
			@BindingParam("popup") Popup popup,
			@BindingParam("comp") Component comp) {
		this.selectedItem_ = cheque;
		popup.open(comp, "start_before");
	}
	
	/***************************************************/
	
	
	/******************** FUNCIONES ********************/

	/**
	 * impresion del cheque..
	 */
	private void imprimirCheque(MyArray cheque) {
		boolean alDia = (boolean) cheque.getPos8();
		String source = SOURCE_ALDIA;
		if (!alDia) {
			source = SOURCE_DIFERIDO;
		}
		this.win = (Window) Executions.createComponents(source, this.mainComponent, null);
		this.win.doModal();
	}
	
	/**
	 * registrar cheque cobrado..
	 */
	private void registrarChequeCobrado(MyArray cheque, boolean cobrado, Date fechaCobro) throws Exception {
		ControlBancoMovimiento.setChequeCobrado(cheque.getId(), cobrado, fechaCobro, this.getLoginNombre());
		Clients.showNotification("Registro actualizado..!");
	}
	
	@DependsOn({ "filterNumeroCaja", "filterNumeroOrdenPago", "filterCuenta",
			"filterBanco", "filterNro", "filterBeneficiario", "selectedFiltro",
			"filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<MyArray> getCheques() throws Exception {
		boolean aVencer = this.selectedFiltro.equals(FILTRO_A_VENCER);
		boolean pendienteCobro = this.selectedFiltro.equals(FILTRO_PENDIENTE_COBRO);
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoCheque> cheques = rr.getCheques(this.filterNro, this.filterBanco,
				this.filterCuenta, this.filterBeneficiario, this.filterNumeroCaja, 
				this.filterNumeroOrdenPago, aVencer, pendienteCobro, null, null, this.getFilterFecha());
		return this.chequesToMyArray(cheques);
	}
	
	/**
	 * @return cheques convertidos a myarray..
	 */
	private List<MyArray> chequesToMyArray(List<BancoCheque> cheques) {
		List<MyArray> out = new ArrayList<MyArray>();
		this.totalImporteGs = 0;
		for (BancoCheque cheque : cheques) {
			MyArray my = new MyArray();
			my.setId(cheque.getId());
			my.setPos1(cheque.getBanco().getNroCuenta());
			my.setPos2(cheque.getBanco().getBancoDescripcion());
			my.setPos3(cheque.getNumero());
			my.setPos4(cheque.getFechaEmision());
			my.setPos5(cheque.getFechaVencimiento());
			my.setPos6(cheque.getBeneficiario());
			my.setPos7(cheque.getMonto());
			my.setPos8(cheque.isChequeAlDia());
			my.setPos9(cheque.getNumeroCaja());
			my.setPos10(cheque.getNumeroOrdenPago());
			my.setPos11(cheque.isCobrado());
			my.setPos12(cheque.isAnulado());
			my.setPos13(cheque.getFechaCobro());
			if (this.selectedFiltro.equals(FILTRO_AL_DIA) && cheque.isChequeAlDia()) {
				out.add(my);
				this.totalImporteGs += cheque.getMonto();
			} else if (this.selectedFiltro.equals(FILTRO_DIFERIDOS) && !cheque.isChequeAlDia()) {
				out.add(my);
				this.totalImporteGs += cheque.getMonto();
			} else if (this.selectedFiltro.equals(FILTRO_A_VENCER) && cheque.isAvencer()) {
				out.add(my);
				this.totalImporteGs += cheque.getMonto();
			} else if (this.selectedFiltro.equals(FILTRO_NO_CONCILIADOS) && !cheque.getMovimiento().isConciliado()) {
				out.add(my);
				this.totalImporteGs += cheque.getMonto();
			} else if (this.selectedFiltro.equals(FILTRO_PENDIENTE_COBRO) && !cheque.isCobrado()) {
				out.add(my);
				this.totalImporteGs += cheque.getMonto();
			} else if (this.selectedFiltro.equals(FILTRO_TODOS)) {
				out.add(my);
				this.totalImporteGs += cheque.getMonto();
			}
		}
		BindUtils.postNotifyChange(null, null, this, "totalImporteGs");
		return out;
	}
	
	/**
	 * Despliega la ventana del cheque propio..
	 */
	private void showChequePropio() throws Exception {
		String beneficiario = "";
		MyArray moneda = ((UtilDTO) this.getDtoUtil()).getMonedaGuaraniConSimbolo();
		
		WindowCheque w = new WindowCheque();
		w.getChequeDTO().setBeneficiario(beneficiario);
		w.getChequeDTO().setMoneda(moneda);	
		w.getChequeDTO().setMonto(0);
		w.setMontoRecibo(0);
		w.show(WindowPopup.NUEVO, true);
		
		if (w.isClickAceptar()) {
			w.getChequeDTO().setBanco(w.getCuentaDTO());
			w.getChequeDTO().setEstadoComprobante(((UtilDTO) this.getDtoUtil()).getEstadoComprobanteConfeccionado());
			w.getChequeDTO().setModoDeCreacion(((UtilDTO) this.getDtoUtil()).getChequeManual());
			ControlBancoMovimiento.registrarChequePropioManual(w.getChequeDTO(), this.getLoginNombre());
			this.mensajePopupTemporal("Cheque Registrado..", 5000);
		}
	}
	
	/**
	 * reporte de cheques..
	 */
	private void reporteCheques() throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();
		for (MyArray cheque : this.getCheques()) {
			data.add(new Object[] {
					Utiles.getDateToString((Date) cheque.getPos4(), Utiles.DD_MM_YYYY), 
					Utiles.getDateToString((Date) cheque.getPos5(), Utiles.DD_MM_YYYY),
					cheque.getPos3() + "",
					cheque.getPos2().toString().toUpperCase(),
					cheque.getPos6(), cheque.getPos7() });
		}

		String sucursal = this.getAcceso().getSucursalOperativa().getText();
		String banco_ = "TODOS..";
		ReporteChequesPropios rep = new ReporteChequesPropios(banco_, sucursal);
		rep.setDatosReporte(data);
		rep.setApaisada();
		rep.setBorrarDespuesDeVer(true);

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/**
	 * DataSource del cheque..
	 */
	class ChequeDataSource implements JRDataSource {
		
		MyArray cheque;

		public ChequeDataSource(MyArray cheque) {
			this.cheque = cheque;
		}
		
		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			Date emision = (Date) this.cheque.getPos4();
			String[] fecha = m.dateToString(emision, Utiles.DD_MM_YY).split("-");
			int mes = Integer.parseInt(fecha[1]);
			Date vencimiento = (Date) this.cheque.getPos5();
			String[] fechaVto = m.dateToString(vencimiento, Misc.DD_MM_YYYY).split("-");
			int mesVto = Integer.parseInt(fechaVto[1]);

			if ("NroCheque".equals(fieldName)) {
				Long nro = (Long) this.cheque.getPos3();
				value = String.valueOf(nro);
			} else if ("Beneficiario".equals(fieldName)) {
				value = this.cheque.getPos6();
			} else if ("Importe".equals(fieldName)) {
				double importe = (double) this.cheque.getPos7();
				value = Misc.FORMATTER.format(importe);
			} else if ("Dia".equals(fieldName)) {
				value = fecha[0];
			} else if ("Mes".equals(fieldName)) {
				String mes_ = mes + "";
				if (mes < 10) {
					mes_ = "0" + mes;
				}
				value = mes_;
			} else if ("Anho".equals(fieldName)) {
				value = fecha[2];
			} else if ("DiaVto".equals(fieldName)) {
				value = fechaVto[0];
			} else if ("MesVto".equals(fieldName)) {
				value = m.getMesEnLetras(mesVto);
			} else if ("AnhoVto".equals(fieldName)) {
				value = fechaVto[2];
			} else if ("ImporteLetras".equals(fieldName)) {
				double importe = (double) this.cheque.getPos7();
				value = m.numberToLetter(importe);
			} else if ("Fecha".equals(fieldName)) {
				value = Utiles.getDateToString(vencimiento, Utiles.DD_MM_YY);
			} else if ("Proveedor".equals(fieldName)) {
				value = Utiles.getMaxLength((String) this.cheque.getPos6(), 10);
			} else if ("OrdenPago".equals(fieldName)) {
				value = this.cheque.getPos10().toString().replace("REC-PAG-", "O.P.");
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < 0) {
				index++;
				return true;
			}
			return false;
		}
	}

	/***************************************************/
	
	
	/****************** GETTER/SETTER ******************/
	
	/**
	 * @return los filtros..
	 */
	public List<String> getFiltros() {
		List<String> out = new ArrayList<String>();
		out.add(FILTRO_TODOS);
		out.add(FILTRO_AL_DIA);
		out.add(FILTRO_DIFERIDOS);
		out.add(FILTRO_CONCILIADOS);
		out.add(FILTRO_NO_CONCILIADOS);
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
	
	@DependsOn("selectedItem")
	public String getImporteEnLetras() {
		if (this.selectedItem == null) return "";
		double importe = (double) this.selectedItem.getPos7();
		return m.numberToLetter(importe);
	}
	
	@DependsOn("selectedItem")
	public String getOrdenPago() {
		if (this.selectedItem == null) return "";
		return this.selectedItem.getPos10().toString().replace("REC-PAG-", "O.P.");
	}

	private AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}
	
	public String getFilterBanco() {
		return filterBanco;
	}

	public void setFilterBanco(String filterBanco) {
		this.filterBanco = filterBanco;
	}

	public String getFilterNro() {
		return filterNro;
	}

	public void setFilterNro(String filterNro) {
		this.filterNro = filterNro;
	}

	public String getFilterCuenta() {
		return filterCuenta;
	}

	public void setFilterCuenta(String filterCuenta) {
		this.filterCuenta = filterCuenta;
	}

	public String getFilterBeneficiario() {
		return filterBeneficiario;
	}

	public void setFilterBeneficiario(String filterBeneficiario) {
		this.filterBeneficiario = filterBeneficiario;
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
	}

	public String getFilterNumeroCaja() {
		return filterNumeroCaja;
	}

	public void setFilterNumeroCaja(String filterNumeroCaja) {
		this.filterNumeroCaja = filterNumeroCaja;
	}

	public String getFilterNumeroOrdenPago() {
		return filterNumeroOrdenPago;
	}

	public void setFilterNumeroOrdenPago(String filterNumeroOrdenPago) {
		this.filterNumeroOrdenPago = filterNumeroOrdenPago;
	}

	public String getSelectedFiltro() {
		return selectedFiltro;
	}

	public void setSelectedFiltro(String selectedFiltro) {
		this.selectedFiltro = selectedFiltro;
	}

	public double getTotalImporteGs() {
		return totalImporteGs;
	}

	public void setTotalImporteGs(double totalImporteGs) {
		this.totalImporteGs = totalImporteGs;
	}

	public MyArray getSelectedItem_() {
		return selectedItem_;
	}

	public void setSelectedItem_(MyArray selectedItem_) {
		this.selectedItem_ = selectedItem_;
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

	public Date getFechaCobro() {
		return fechaCobro;
	}

	public void setFechaCobro(Date fechaCobro) {
		this.fechaCobro = fechaCobro;
	}
}

/**
 * Reporte de Cheques Propios TES-00025..
 */
class ReporteChequesPropios extends ReporteYhaguy {
	
	private String banco;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Emisión", TIPO_STRING,30);
	static DatosColumnas col1 = new DatosColumnas("Vencimiento", TIPO_STRING,30);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Banco", TIPO_STRING, 40);
	static DatosColumnas col4 = new DatosColumnas("Beneficiario", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE_GS, 30, true);

	public ReporteChequesPropios(String banco, String sucursal) {
		this.banco = banco;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Emision de Cheques");
		this.setDirectorio("banco");
		this.setNombreArchivo("Cheque-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde", "- - -"))
				.add(this.textoParValor("Hasta", "- - -"))
				.add(this.textoParValor("Banco", this.banco.toUpperCase()))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

