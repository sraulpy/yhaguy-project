package com.yhaguy.gestion.compras.importacion;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;

import com.coreweb.control.SimpleViewModel;

public class ImportacionForward extends SimpleViewModel {

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
		String url = "https://reporte.yhaguyrepuestos.com.py:8081/yhaguy/sistema.zul";
		Executions.getCurrent().sendRedirect(url, "blank");
	}
}
