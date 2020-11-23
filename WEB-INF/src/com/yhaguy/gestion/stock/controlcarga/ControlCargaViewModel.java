package com.yhaguy.gestion.stock.controlcarga;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.ArticuloControlCarga;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class ControlCargaViewModel extends SimpleViewModel {
	
	private String filterCodigo = "";
	private String filterDescripcion = "";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private Object[] selectedArticulo;	
	private ArticuloControlCarga nvoControl;
	
	@Init(superclass = true)
	public void init() {
		this.nvoControl = new ArticuloControlCarga();
		this.nvoControl.setFecha(new Date());
		this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
		this.filterFechaAA = Utiles.getAnhoActual();
		if (this.filterFechaMM.length() == 1) {
			this.filterFechaMM = "0" + this.filterFechaMM;
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addControlCarga(@BindingParam("comp") Popup comp) throws Exception {
		if (this.selectedArticulo == null || this.nvoControl.getCantidad() <= 0) {
			Clients.showNotification("Faltan Datos", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvoControl.setArticulo(rr.getArticuloById((long) this.selectedArticulo[0]));
		rr.saveObject(this.nvoControl, this.getLoginNombre());
		this.nvoControl = new ArticuloControlCarga();
		this.nvoControl.setFecha(new Date());
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO");
	}
	
	/**
	 * GET'S AND SET'S
	 */
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA", "filterCodigo", "filterDescripcion" })
	public List<Object[]> getControles() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticuloControlCargas(this.getFilterFecha(), this.filterCodigo, this.filterDescripcion);
	}
	
	@DependsOn({ "filterCodigo", "filterDescripcion" })
	public List<Object[]> getArticulos() throws Exception {
		if (this.filterCodigo.trim().isEmpty() && this.filterDescripcion.trim().isEmpty()) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulos(this.filterCodigo, this.filterDescripcion);
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

	public ArticuloControlCarga getNvoControl() {
		return nvoControl;
	}

	public void setNvoControl(ArticuloControlCarga nvoControl) {
		this.nvoControl = nvoControl;
	}

	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}

	public String getFilterDescripcion() {
		return filterDescripcion;
	}

	public void setFilterDescripcion(String filterDescripcion) {
		this.filterDescripcion = filterDescripcion;
	}

	public Object[] getSelectedArticulo() {
		return selectedArticulo;
	}

	public void setSelectedArticulo(Object[] selectedArticulo) {
		this.selectedArticulo = selectedArticulo;
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
}
