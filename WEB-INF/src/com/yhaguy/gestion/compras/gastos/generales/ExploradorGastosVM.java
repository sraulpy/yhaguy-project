package com.yhaguy.gestion.compras.gastos.generales;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
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
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class ExploradorGastosVM extends SimpleViewModel {
	
	static final String FILTRO_TODOS = "TODOS";
	static final String FILTRO_GASTOS_CAJA_CHICA = "GASTOS_CAJA_CHICA";
	static final String FILTRO_GASTOS_IMPORTACION = "GASTOS_IMPORTACION";
	static final String FILTRO_PAGADOS = "PAGADOS";
	static final String FILTRO_PENDIENTES_PAGO = "PENDIENTES_PAGO";
	
	private String selectedFiltro = FILTRO_TODOS;
	
	private Gasto selectedGasto;
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String filterNumero = "";
	private String filterRazonSocial = "";
	private String filterRuc = "";
	private String filterCaja = "";
	private String filterPago = "";
	private String filterImportacion = "";
	private String filterDescripcion = "";
	
	private int listSize = 0;
	private double totalImporteGs = 0;
	
	@Wire
	private Popup pop_det;
	
	@Wire
	private Popup pop_img;

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
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
			this.selectedFiltro = FILTRO_TODOS;
		} else if (filter == 2) {
			this.selectedFiltro = FILTRO_GASTOS_CAJA_CHICA;
		} else if (filter == 3) {
			this.selectedFiltro = FILTRO_GASTOS_IMPORTACION;
		} else if (filter == 4) {
			this.selectedFiltro = FILTRO_PAGADOS;
		} else {
			this.selectedFiltro = FILTRO_PENDIENTES_PAGO;
		}
	}
	
	@Command
	@NotifyChange("selectedGasto")
	public void verItems(@BindingParam("item") Gasto item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedGasto = item;
		this.pop_det.open(parent, "start_before");
	}
	
	@Command
	@NotifyChange("selectedGasto")
	public void verImagen(@BindingParam("item") Gasto item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedGasto = item;
		this.pop_img.open(200, 100);
		Clients.evalJavaScript("setImage('" + this.selectedGasto.getUrlImagen() + "')");
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA",
		"filterNumero", "filterRazonSocial", "filterRuc", "filterCaja", 
		"filterPago", "filterImportacion", "selectedFiltro", "filterDescripcion" })
	public List<Gasto> getGastos() throws Exception {
		this.totalImporteGs = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Gasto> aux = new ArrayList<Gasto>();
		List<Gasto> out = rr.getGastos(this.getFilterFecha(),
				this.filterNumero, this.filterRazonSocial, this.filterRuc, 
				this.filterCaja, this.filterPago, this.filterImportacion, this.filterDescripcion);
		for (Gasto gasto : out) {
			if (this.selectedFiltro.equals(FILTRO_TODOS)) {
				aux.add(gasto);
				this.totalImporteGs += gasto.getImporteGs();
			}
			if (this.selectedFiltro.equals(FILTRO_GASTOS_CAJA_CHICA)) {
				if (gasto.isFondoFijo()) {
					aux.add(gasto);
					this.totalImporteGs += gasto.getImporteGs();
				}
			} 
			if (this.selectedFiltro.equals(FILTRO_GASTOS_IMPORTACION)) {
				if (gasto.isGastoImportacion()) {
					aux.add(gasto);
					this.totalImporteGs += gasto.getImporteGs();
				}
			}
			if (this.selectedFiltro.equals(FILTRO_PAGADOS)) {
				if (gasto.isPagado()) {
					aux.add(gasto);
					this.totalImporteGs += gasto.getImporteGs();
				}
			}
			if (this.selectedFiltro.equals(FILTRO_PENDIENTES_PAGO)) {
				if (gasto.isPendientePago()) {
					aux.add(gasto);
					this.totalImporteGs += gasto.getImporteGs();
				}
			}
		}
		this.listSize = out.size();
		BindUtils.postNotifyChange(null, null, this, "listSize");
		BindUtils.postNotifyChange(null, null, this, "totalImporteGs");
		return aux;
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

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
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

	public String getFilterCaja() {
		return filterCaja;
	}

	public void setFilterCaja(String filterCaja) {
		this.filterCaja = filterCaja;
	}

	public int getListSize() {
		return listSize;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public double getTotalImporteGs() {
		return totalImporteGs;
	}

	public void setTotalImporteGs(double totalImporteGs) {
		this.totalImporteGs = totalImporteGs;
	}

	public String getFilterPago() {
		return filterPago;
	}

	public void setFilterPago(String filterPago) {
		this.filterPago = filterPago;
	}

	public Gasto getSelectedGasto() {
		return selectedGasto;
	}

	public void setSelectedGasto(Gasto selectedGasto) {
		this.selectedGasto = selectedGasto;
	}

	public String getFilterImportacion() {
		return filterImportacion;
	}

	public void setFilterImportacion(String filterImportacion) {
		this.filterImportacion = filterImportacion;
	}

	public String getSelectedFiltro() {
		return selectedFiltro;
	}

	public void setSelectedFiltro(String selectedFiltro) {
		this.selectedFiltro = selectedFiltro;
	}

	public String getFilterDescripcion() {
		return filterDescripcion;
	}

	public void setFilterDescripcion(String filterDescripcion) {
		this.filterDescripcion = filterDescripcion;
	}
}
