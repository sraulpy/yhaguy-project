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
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;

public class ReportesForward extends SimpleViewModel {
	
	static final String URL_REPORTE_REPUESTOS = "https://reporte.yhaguyrepuestos.com.py:8081/yhaguy/yhaguy/gestion/reportes/reportes_.zul";
	static final String URL_REPORTE_GTSA = "https://reporte.gtsa.com.py:8081/yhaguy/yhaguy/gestion/reportes/reportes_.zul";
	static final String URL_REPORTE_REPRESENTACIONES = "https://repre.yhaguyrepuestos.com.py:8083/yhaguy/yhaguy/gestion/reportes/reportes_.zul";
	
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
		Misc m = new Misc();
		String url = this.getUrl();
		String usuario = this.getLoginNombre();
		String clave = (String) this.getAtributoSession(Config.CLAVE);
		String clave_ = m.encriptar(clave);
		if_rep.setSrc(url + "?usuario=" + usuario + "&clave=" + clave_);
	}
	
	/**
	 * @return url de reportes..
	 */
	private String getUrl() {
		switch (Configuracion.empresa) {
		case Configuracion.EMPRESA_GTSA:
			return URL_REPORTE_GTSA;
		case Configuracion.EMPRESA_YRSA:
			return URL_REPORTE_REPUESTOS;
		case Configuracion.EMPRESA_YRPS:
			return URL_REPORTE_REPRESENTACIONES;
		}
		return "";
	}
}
