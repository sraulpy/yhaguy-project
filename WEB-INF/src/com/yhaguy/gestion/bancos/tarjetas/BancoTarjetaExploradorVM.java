package com.yhaguy.gestion.bancos.tarjetas;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.ProcesadoraTarjeta;
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
	
	private Tipo selectedTipo;
	private Object[] totales = new Object[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
			this.selectedTipo = this.getTiposTarjetas().get(0);
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
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA", "selectedTipo", "selectedFiltro" })
	public List<Object[]> getMovimientos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		boolean acreditado = this.selectedFiltro.equals(FILTRO_ACREDITADOS) ? true : false;
		List<Object[]> list = rr.getFormasPagoTarjeta_(this.getFilterFecha(), this.selectedTipo.getSigla(), 0, 0, acreditado);
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
}
