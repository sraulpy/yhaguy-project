package com.yhaguy.gestion.rrhh;

import java.util.Date;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class MarcacionesViewModel extends SimpleViewModel {
	
	static final String API = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&amp;data=";
	static final String ZUL_QR = "/yhaguy/gestion/rrhh/marcaciones_qr.zul";
	static final String ZUL_NOTIFICACION = "/yhaguy/gestion/rrhh/notificacion.zul";
	
	private String link;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void openCodigo(@BindingParam("tipo") int tipo) throws Exception {
		switch (tipo) {
		case 1:
			this.link = this.getEntrada();
			break;
		case 2:
			this.link = this.getEntradaInterna();
			break;
		case 3:
			this.link = this.getSalida();
			break;
		case 4:
			this.link = this.getSalidaInterna();
			break;
		}
		
		Window win = (Window) Executions.createComponents(ZUL_QR, this.mainComponent, null);
		win.doModal();
	}
	
	/**
	 * @return la descripcion..
	 */
	public String getDescripcion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Object[] marcacion = rr.getUltimaMarcacion();
		return marcacion[1] + " - " + marcacion[2];
	}
	
	/**
	 * @return
	 */
	public String getEmpresaDescripcion() {
		return Configuracion.empresa;
	}
	
	public String getEntrada() {
		return API + "entrada_" + this.getFechaHora(); 
	}
	
	public String getSalida() {
		return API + "salida_" + this.getFechaHora(); 
	}
	
	public String getEntradaInterna() {
		return API + "entrada_interna_" + this.getFechaHora(); 
	}
	
	public String getSalidaInterna() {
		return API + "salida_interna" + this.getFechaHora(); 
	}
	
	public String getFechaHora() {
		return Utiles.getDateToString(new Date(), "dd-MM-yyyy HH:mm:ss");
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
