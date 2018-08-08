package com.yhaguy.gestion.venta.misventas;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class VentasPerdidas {
	
	static final String ZUL_VENTAS_PERDIDAS = "/yhaguy/gestion/venta/ventas_perdidas.zul";
	
	@Command
	public void ventasPerdidas() {		
		Window win = (Window) Executions.createComponents(ZUL_VENTAS_PERDIDAS, null, null);
		win.doOverlapped();
	}
}
