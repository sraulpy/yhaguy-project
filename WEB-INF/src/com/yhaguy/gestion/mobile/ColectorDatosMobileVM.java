package com.yhaguy.gestion.mobile;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.ColectorDatos;
import com.yhaguy.domain.RegisterDomain;

public class ColectorDatosMobileVM extends SimpleViewModel {

	private ColectorDatos colector = new ColectorDatos();
	
	@Init(superclass = true)
	public void init() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void enviar() throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.colector, "mobile");
		this.colector = new ColectorDatos();
		Clients.showNotification("ENVIADO");
	}

	/**
	 * GETTER / SETTER
	 */
	
	public List<String> getTiposDocumentos() {
		List<String> out = new ArrayList<String>();
		out.add("CEDULA");
		out.add("RUC");
		return out;
	}
	
	public ColectorDatos getColector() {
		return colector;
	}

	public void setColector(ColectorDatos colector) {
		this.colector = colector;
	}

}
