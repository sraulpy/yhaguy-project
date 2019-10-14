package com.yhaguy.gestion.bancos.tarjetas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.CierreDocumento;
import com.yhaguy.domain.ProcesadoraTarjeta;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.util.Utiles;

public class BancoTarjetaExploradorVM extends SimpleViewModel {
	
	static final String T_CREDITO = "TARJETA DE CRÉDITO";
	static final String T_DEBITO = "TARJETA DE DÉBITO";
	
	static final String FILTRO_ACREDITADOS = "ACREDITADOS";
	static final String FILTRO_PENDIENTES = "PENDIENTES";
	
	private String selectedFiltro = FILTRO_PENDIENTES;
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String procesadora = "";
	private String operacion = "";
	
	private String filterFechaDD_ = "";
	private String filterFechaMM_ = "";
	private String filterFechaAA_ = "";
	
	private Date fechaCierre;
	
	private Tipo selectedTipo;
	private Object[] selectedItem;
	private ReciboFormaPago selected;
	private Object[] totales = new Object[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
	
	@Wire
	private Popup pop_det;

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
			this.filterFechaMM_ = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA_ = Utiles.getAnhoActual();
			if (this.filterFechaMM_.length() == 1) {
				this.filterFechaMM_ = "0" + this.filterFechaMM_;
			}
			this.selectedTipo = this.getTiposTarjetas().get(0);
			
			RegisterDomain rr = RegisterDomain.getInstance();
			List<CierreDocumento> cierres = rr.getCierreDocumentos();
			if (cierres.size() > 0) {
				this.fechaCierre = cierres.get(0).getFecha();
			} else {
				this.fechaCierre = new Date();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("selectedFiltro")
	public void selectFilter(@BindingParam("filter") int filter) {
		if (filter == 1) {
			this.selectedFiltro = FILTRO_ACREDITADOS;
		} else if (filter == 2) {
			this.selectedFiltro = FILTRO_PENDIENTES;
		}
	}
	
	@Command
	@NotifyChange({ "selectedItem", "selected" })
	public void verItems(@BindingParam("item") Object[] item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedItem = item;
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selected = (ReciboFormaPago) rr.getObject(ReciboFormaPago.class.getName(), (long) item[0]);
		this.pop_det.open(parent, "start_before");
	}
	
	@Command
	@NotifyChange("selected")
	public void guardarCambios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.selected, this.getLoginNombre());
		Clients.showNotification("REGISTRO GUARDADO");
	}
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA", "filterFechaDD_", "filterFechaMM_",
			"filterFechaAA_", "selectedTipo", "selectedFiltro", "procesadora", "operacion" })
	public List<Object[]> getMovimientos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		boolean acreditado = this.selectedFiltro.equals(FILTRO_ACREDITADOS) ? true : false;
		List<Object[]> list = rr.getFormasPagoTarjeta_(this.getFilterFecha(), this.selectedTipo.getSigla(), 0, 0,
				acreditado, this.getFilterFecha_(), this.procesadora, this.operacion);
		double totalImporte = 0;
		double totalCredito = 0;
		for (Object[] item : list) {
			totalImporte += ((double) item[3]);
			totalCredito += ((double) item[8]);
		}
		this.totales = new Object[] { totalImporte, totalCredito };

		return list;
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "selected", "fechaCierre" })
	public boolean isGuardarHabilitado() {
		if (this.selected == null) {
			return false;
		}
		return this.selected.getFechaOperacion().compareTo(this.fechaCierre) > 0;
	}
	
	/**
	 * @return si la operacion es habilitada..
	 */
	public boolean isOperacionHabilitada(String operacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.isOperacionHabilitada(this.getLoginNombre(), operacion);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcesadoraTarjeta> getProcesadoras() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ProcesadoraTarjeta.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<BancoCta> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(BancoCta.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<SucursalApp> getSucursales() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(SucursalApp.class.getName());
	}
	
	/**
	 * @return los tipos de tarjeta
	 */
	public List<Tipo> getTiposTarjetas() throws Exception {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO));
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO));
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
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha_() {
		if (this.filterFechaAA_.isEmpty() && this.filterFechaDD_.isEmpty() && this.filterFechaMM_.isEmpty())
			return "";
		if (this.filterFechaAA_.isEmpty())
			return this.filterFechaMM_ + "-" + this.filterFechaDD_;
		if (this.filterFechaMM_.isEmpty())
			return this.filterFechaAA_;
		if (this.filterFechaMM_.isEmpty() && this.filterFechaDD_.isEmpty())
			return this.filterFechaAA_;
		return this.filterFechaAA_ + "-" + this.filterFechaMM_ + "-" + this.filterFechaDD_;
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

	public Tipo getSelectedTipo() {
		return selectedTipo;
	}

	public void setSelectedTipo(Tipo selectedTipo) {
		this.selectedTipo = selectedTipo;
	}

	public Object[] getTotales() {
		return totales;
	}

	public void setTotales(Object[] totales) {
		this.totales = totales;
	}

	public String getSelectedFiltro() {
		return selectedFiltro;
	}

	public void setSelectedFiltro(String selectedFiltro) {
		this.selectedFiltro = selectedFiltro;
	}

	public String getFilterFechaDD_() {
		return filterFechaDD_;
	}

	public void setFilterFechaDD_(String filterFechaDD_) {
		this.filterFechaDD_ = filterFechaDD_;
	}

	public String getFilterFechaMM_() {
		return filterFechaMM_;
	}

	public void setFilterFechaMM_(String filterFechaMM_) {
		this.filterFechaMM_ = filterFechaMM_;
	}

	public String getFilterFechaAA_() {
		return filterFechaAA_;
	}

	public void setFilterFechaAA_(String filterFechaAA_) {
		this.filterFechaAA_ = filterFechaAA_;
	}

	public String getProcesadora() {
		return procesadora;
	}

	public void setProcesadora(String procesadora) {
		this.procesadora = procesadora;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public Object[] getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Object[] selectedItem) {
		this.selectedItem = selectedItem;
	}

	public ReciboFormaPago getSelected() {
		return selected;
	}

	public void setSelected(ReciboFormaPago selected) {
		this.selected = selected;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}	
}
