package com.yhaguy.gestion.venta.remision;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Remision;
import com.yhaguy.domain.RemisionDetalle;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.Utiles;

public class VentaRemisionViewModel extends SimpleViewModel {
	
	static final String ZUL_IMPRESION_REMISION = "/yhaguy/gestion/venta/remision/impresion_remision.zul";
	
	private Remision nvaRemision;
	private Remision selectedRemision;
	private Object[] selectedRemision_;
	
	private Object[] selectedVenta;
	
	private String filterNumero = "";
	private String filterCliente = "";
	
	private String filterNroRemision = "";
	private String filterNroVenta = "";
	private String filterCliente_ = "";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private Window win;

	@Init(superclass = true)
	public void init() {
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
	public void nuevaRemision(@BindingParam("comp") Popup comp, @BindingParam("parent") Component parent) {
		this.nvaRemision = new Remision();
		this.nvaRemision.setFecha(new Date());
		comp.open(parent, "after_start");
	}
	
	@Command
	@NotifyChange("*")
	public void selectVenta(@BindingParam("comp") Bandbox comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idVenta = (long) this.selectedVenta[0];
		Venta venta = (Venta) rr.getObject(Venta.class.getName(), idVenta);
		this.nvaRemision.setVenta(venta);
		this.nvaRemision.getDetalles().clear();
		for (VentaDetalle item : venta.getDetalles()) {
			Long cant = item.getCantidad();
			RemisionDetalle det = new RemisionDetalle();
			det.setArticulo(item.getArticulo());
			det.setCantidad(cant.intValue());
			this.nvaRemision.getDetalles().add(det);
		}
		comp.close();
	}
	
	@Command
	@NotifyChange("*")
	public void selectRemision() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idRemision = (long) this.selectedRemision_[0];
		Remision rem = (Remision) rr.getObject(Remision.class.getName(), idRemision);
		this.selectedRemision = rem;
	}
	
	@Command
	@NotifyChange("*")
	public void deleteItem(@BindingParam("item") RemisionDetalle item) {
		this.nvaRemision.getDetalles().remove(item);
	}
	
	@Command
	@NotifyChange("*")
	public void addRemision(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.nvaRemision, this.getLoginNombre());
		this.selectedRemision = this.nvaRemision;
		comp.close();
		this.imprimir();
	}
	
	@Command
	public void imprimir() {
		this.win = (Window) Executions.createComponents(ZUL_IMPRESION_REMISION, this.mainComponent, null);
		this.win.doModal();
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filterNroRemision", "filterNroVenta", "filterCliente_", "filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<Object[]> getRemisiones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getRemisiones(this.filterNroRemision, this.filterNroVenta, this.filterCliente_, this.getFilterFecha());
	}
	
	@DependsOn({ "filterNumero", "filterCliente" })
	public List<Object[]> getVentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getVentas(this.filterNumero, this.filterCliente);
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

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public String getFilterCliente() {
		return filterCliente;
	}

	public void setFilterCliente(String filterCliente) {
		this.filterCliente = filterCliente;
	}

	public Remision getNvaRemision() {
		return nvaRemision;
	}

	public void setNvaRemision(Remision nvaRemision) {
		this.nvaRemision = nvaRemision;
	}

	public Object[] getSelectedVenta() {
		return selectedVenta;
	}

	public void setSelectedVenta(Object[] selectedVenta) {
		this.selectedVenta = selectedVenta;
	}

	public String getFilterNroRemision() {
		return filterNroRemision;
	}

	public void setFilterNroRemision(String filterNroRemision) {
		this.filterNroRemision = filterNroRemision;
	}

	public String getFilterNroVenta() {
		return filterNroVenta;
	}

	public void setFilterNroVenta(String filterNroVenta) {
		this.filterNroVenta = filterNroVenta;
	}

	public String getFilterCliente_() {
		return filterCliente_;
	}

	public void setFilterCliente_(String filterCliente_) {
		this.filterCliente_ = filterCliente_;
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

	public Remision getSelectedRemision() {
		return selectedRemision;
	}

	public void setSelectedRemision(Remision selectedRemision) {
		this.selectedRemision = selectedRemision;
	}

	public Object[] getSelectedRemision_() {
		return selectedRemision_;
	}

	public void setSelectedRemision_(Object[] selectedRemision_) {
		this.selectedRemision_ = selectedRemision_;
	}
}
