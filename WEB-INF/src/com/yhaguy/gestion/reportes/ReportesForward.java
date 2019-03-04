package com.yhaguy.gestion.reportes;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;

import com.coreweb.Config;
import com.coreweb.control.SimpleViewModel;
import com.yhaguy.util.Utiles;

public class ReportesForward extends SimpleViewModel {

	@Init(superclass = true)
	public void init() {
		String ip = this.getMyIP();
		String url = "http://190.211.240.130:8081/yhaguy/yhaguy/gestion/reportes/reportes_.zul";
		if (ip.startsWith("192.168.") || ip.startsWith("10.25.")) {
			url = "http://10.25.1.251:8081/yhaguy/yhaguy/gestion/reportes/reportes_.zul";
		}
		String usuario = this.getLoginNombre();
		String clave = (String) this.getAtributoSession(Config.CLAVE);
		String clave_ = Utiles.encriptar(clave, true);
		Executions.getCurrent().sendRedirect(url + "?usuario=" + usuario + "&clave=" + clave_, "_blank");
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
}
