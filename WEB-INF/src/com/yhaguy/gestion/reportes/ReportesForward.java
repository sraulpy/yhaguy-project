package com.yhaguy.gestion.reportes;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Iframe;

import com.coreweb.Config;
import com.coreweb.control.SimpleViewModel;
import com.yhaguy.util.Utiles;

public class ReportesForward extends SimpleViewModel {
	
	@Wire
	private Iframe if_rep;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(this.mainComponent, this, false);
		this.forward();
	}
	
	/**
	 * redirecciona a reportes..
	 */
	private void forward() {
		String ip = this.getMyIP();
		String url = "http://reporte.yhaguyrepuestos.com.py:8081/yhaguy/yhaguy/gestion/reportes/reportes_.zul";
		if (ip.startsWith("192.168.") || ip.startsWith("10.25.")) {
			url = "http://reporte.yhaguyrepuestos.com.py:8081/yhaguy/yhaguy/gestion/reportes/reportes_.zul";
		}
		String usuario = this.getLoginNombre();
		String clave = (String) this.getAtributoSession(Config.CLAVE);
		String clave_ = Utiles.encriptar(clave, true);
		if_rep.setSrc(url + "?usuario=" + usuario + "&clave=" + clave_);
	}
}
