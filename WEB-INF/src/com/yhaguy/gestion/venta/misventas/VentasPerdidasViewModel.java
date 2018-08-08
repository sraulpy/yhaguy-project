package com.yhaguy.gestion.venta.misventas;

import java.util.Date;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.VentaPerdida;

public class VentasPerdidasViewModel extends SimpleViewModel {

	private VentaPerdida ventaPerdida;
	private Window win;
	
	@Init
	public void init(@ContextParam(ContextType.VIEW) Component view) {
		this.ventaPerdida = new VentaPerdida();
		this.win = (Window) view;
	}
	
	@Command
	@NotifyChange("*")
	public void addVentaPerdida() throws Exception {
		if (this.ventaPerdida.getArticulo() == null
				&& this.ventaPerdida.getMotivo() == null
				&& this.ventaPerdida.getCliente() == null) {
			this.win.detach();
			return;
		}
		this.ventaPerdida.setFecha(new Date());
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.ventaPerdida, this.getLoginNombre());
		this.ventaPerdida = new VentaPerdida();
		this.win.detach();
		Clients.showNotification("Registro Guardado Correctamente..");
	}

	/**
	 * GETS / SETS
	 */
	
	public VentaPerdida getVentaPerdida() {
		return ventaPerdida;
	}

	public void setVentaPerdida(VentaPerdida ventaPerdida) {
		this.ventaPerdida = ventaPerdida;
	}	
}
