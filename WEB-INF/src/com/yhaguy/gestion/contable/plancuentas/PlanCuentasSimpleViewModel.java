package com.yhaguy.gestion.contable.plancuentas;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.PlanDeCuenta;
import com.yhaguy.domain.RegisterDomain;

public class PlanCuentasSimpleViewModel extends SimpleViewModel {
	
	private String filterCodigo = "";
	private String filterDescripcion = "";
	
	private String filterCodigo_ = "";
	private String filterDescripcion_ = "";
	
	private PlanDeCuenta selectedPlanCuenta;
	private CuentaContable selectedCuenta;
	private CuentaContable nvaCuentaContable = new CuentaContable();
	
	private List<PlanDeCuenta> planCuentas;

	@Init(superclass = true)
	public void init() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.planCuentas = rr.getPlanDeCuentas(this.filterCodigo, this.filterDescripcion);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void refresh() throws Exception {
		this.filterCodigo = "";
		this.filterCodigo_ = "";
		this.filterDescripcion = "";
		this.filterDescripcion_ = "";
		this.selectedCuenta = null;
		this.selectedPlanCuenta = null;
		this.setPlanCuentas_();
	}
	
	@Command
	@NotifyChange({ "selectedPlanCuenta", "filterCodigo_", "filterDescripcion_" })
	public void selectPlanCuenta() {
		this.filterCodigo_ = "";
		this.filterDescripcion_ = "";
		for (PlanDeCuenta cuenta : planCuentas) {
			if (cuenta.getId().longValue() == this.selectedCuenta.getPlanCuenta().getId().longValue()) {
				this.setSelectedPlanCuenta(cuenta);
			}
		}
	}
	
	@Command
	public void setPlanCuentas_() throws Exception {
		this.selectedPlanCuenta = null;
		RegisterDomain rr = RegisterDomain.getInstance();
		this.planCuentas = rr.getPlanDeCuentas(this.filterCodigo, this.filterDescripcion);
		BindUtils.postNotifyChange(null, null, this, "planCuentas");
		BindUtils.postNotifyChange(null, null, this, "selectedPlanCuenta");
	}
	
	@Command
	@NotifyChange("*")
	public void addCuenta(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String cod = "CT-" + AutoNumeroControl.getAutoNumero("CT", 7);
		this.nvaCuentaContable.setDescripcion(this.nvaCuentaContable.getDescripcion().toUpperCase());
		this.nvaCuentaContable.setAlias(cod);
		this.nvaCuentaContable.setCodigo(cod);
		this.nvaCuentaContable.setPlanCuenta(this.selectedPlanCuenta);
		rr.saveObject(this.nvaCuentaContable, this.getLoginNombre());
		this.nvaCuentaContable = new CuentaContable();
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
		this.refresh();
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "selectedPlanCuenta", "filterCodigo_", "filterDescripcion_" })
	public List<CuentaContable> getCuentasContables() throws Exception {
		long idPlancuenta = this.selectedPlanCuenta == null ? 0 : this.selectedPlanCuenta.getId();
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCuentasContables(idPlancuenta, this.filterCodigo_, this.filterDescripcion_);
	}
	
	/**
	 * @return el codigo de CT para cuentas..
	 */
	public String getCodigo() throws Exception {
		return "CT-" + AutoNumeroControl.getAutoNumero("CT", 7, true);
	}

	public PlanDeCuenta getSelectedPlanCuenta() {
		return selectedPlanCuenta;
	}

	public void setSelectedPlanCuenta(PlanDeCuenta selectedPlanCuenta) {
		this.selectedPlanCuenta = selectedPlanCuenta;
	}

	public CuentaContable getSelectedCuenta() {
		return selectedCuenta;
	}

	public void setSelectedCuenta(CuentaContable selectedCuenta) {
		this.selectedCuenta = selectedCuenta;
	}

	public List<PlanDeCuenta> getPlanCuentas() {
		return planCuentas;
	}

	public void setPlanCuentas(List<PlanDeCuenta> planCuentas) {
		this.planCuentas = planCuentas;
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

	public String getFilterCodigo_() {
		return filterCodigo_;
	}

	public void setFilterCodigo_(String filterCodigo_) {
		this.filterCodigo_ = filterCodigo_;
	}

	public String getFilterDescripcion_() {
		return filterDescripcion_;
	}

	public void setFilterDescripcion_(String filterDescripcion_) {
		this.filterDescripcion_ = filterDescripcion_;
	}

	public CuentaContable getNvaCuentaContable() {
		return nvaCuentaContable;
	}

	public void setNvaCuentaContable(CuentaContable nvaCuentaContable) {
		this.nvaCuentaContable = nvaCuentaContable;
	}
}
