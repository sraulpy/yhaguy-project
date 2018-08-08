package com.yhaguy.gestion.venta.misventas;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class MisVentas {
	
	static final String ZUL_MIS_VENTAS = "/yhaguy/gestion/venta/MisVentas.zul";
	
	@Command
	public void misVentas() {		
		Window win = (Window) Executions.createComponents(ZUL_MIS_VENTAS, null, null);
		win.doModal();
	}
}
