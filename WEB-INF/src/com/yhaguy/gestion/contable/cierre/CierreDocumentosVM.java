package com.yhaguy.gestion.contable.cierre;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.CierreDocumento;
import com.yhaguy.domain.RegisterDomain;

public class CierreDocumentosVM extends SimpleViewModel {

	private Date fecha;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addCierre() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CierreDocumento cierre = new CierreDocumento();
		cierre.setFecha(this.fecha);
		rr.saveObject(cierre, this.getLoginNombre());
		Clients.showNotification("Cierre de documentos realizado.");
		this.fecha = null;
	}

	/**
	 * GETS / SETS
	 */
	
	public List<CierreDocumento> getCierres() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCierreDocumentos();
	}
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}
