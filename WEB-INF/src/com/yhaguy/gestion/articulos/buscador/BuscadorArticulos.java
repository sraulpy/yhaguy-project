package com.yhaguy.gestion.articulos.buscador;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class BuscadorArticulos {
	
	static final String ZUL_BUSCADOR_ART = "/yhaguy/gestion/articulos/BuscarArticulos.zul";
	
	@Command
	public void buscarArticulos() {		
		Window win = (Window) Executions.createComponents(ZUL_BUSCADOR_ART, null, null);
		win.doModal();
	}
}
