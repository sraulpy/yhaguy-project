package com.yhaguy.gestion.compras.locales;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class Reposicion {
	
	static final String ZUL_REPOSICION = "/yhaguy/gestion/compras/locales/reposicion.zul";
	
	@Command
	public void misVentas() {		
		Window win = (Window) Executions.createComponents(ZUL_REPOSICION, null, null);
		win.doModal();
	}
}
