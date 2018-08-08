package com.yhaguy.gestion.venta.controltalonario;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.chart.Charts;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.TipoTipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.ControlTalonario;
import com.yhaguy.domain.RegisterDomain;

public class ControlTalonarioViewModel extends SimpleViewModel {
	
	private int alerta;
	
	private ControlTalonario selectedControl;
	private ControlTalonario n_control;
	
	@Wire
    Charts chart;

	@Init(superclass = true)
	public void init() {
		try {
			this.alerta = this.getConfiguracionAlerta();
			this.selectedControl = this.getControlTalonarios().get(0);
			BindUtils.postNotifyChange(null, null, this, "alerta");
			BindUtils.postNotifyChange(null, null, this, "selectedControl");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
		try {
			this.crearGrafico();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	@NotifyChange("n_control")
	public void agregarControlTalonario(@BindingParam("comp") Popup comp, @BindingParam("ref") Component ref) {
		this.n_control = new ControlTalonario();
		this.n_control.setActivo(true);
		this.n_control.setReceptor(this.getLoginNombre().toUpperCase());
		this.n_control.setFecha(new Date());
		comp.open(ref, "after_start");
	}
	
	@Command
	@NotifyChange("*")
	public void saveControlTalonario(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.n_control, this.getLoginNombre());
		this.n_control = null;
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	@NotifyChange("*")
	public void inactivar() throws Exception {
		if (!this.mensajeSiNo("Desea inactivar el talonario seleccionado..")) return;
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedControl.setActivo(false);
		rr.saveObject(this.selectedControl, this.getLoginNombre());
		this.selectedControl = null;
		Clients.showNotification("REGISTRO INACTIVADO..");
	}
	
	@Command
	@NotifyChange("*")
	public void saveConfiguracion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		TipoTipo tt = rr.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_ALERTA_TALONARIO);
		tt.setAuxi(String.valueOf(this.alerta));
		rr.saveObject(tt, this.getLoginNombre());
		Clients.showNotification("REGISTRO ACTUALIZADO..");
	}
	
	/**
	 * crea el grafico..
	 */
	@Command
	public void crearGrafico() throws Exception {        
        if (this.selectedControl == null) {
			return;
		}        
        CategoryModel model = new DefaultCategoryModel();        
        model.setValue("Cantidad Impresas", "Control Talonarios", this.selectedControl.getFacturadas());
        model.setValue("Cantidad Facturas", "Control Talonarios", this.selectedControl.getUnidades());        
        chart.setModel(model);        
        chart.getCredits().setEnabled(false); 
	}
	
	/**
	 * GETS / SETS
	 */
	
	public List<ControlTalonario> getControlTalonarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getControlTalonarioActivos();
	}
	
	public int getConfiguracionAlerta() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		TipoTipo conf =  rr.getTipoTipoPorDescripcion(Configuracion.ID_TIPO_ALERTA_TALONARIO);	
		return Integer.parseInt(conf.getAuxi());
	}

	public ControlTalonario getSelectedControl() {
		return selectedControl;
	}

	public void setSelectedControl(ControlTalonario selectedControl) {
		this.selectedControl = selectedControl;
	}

	public ControlTalonario getN_control() {
		return n_control;
	}

	public void setN_control(ControlTalonario n_control) {
		this.n_control = n_control;
	}

	public int getAlerta() {
		return alerta;
	}

	public void setAlerta(int alerta) {
		this.alerta = alerta;
	}	
}
