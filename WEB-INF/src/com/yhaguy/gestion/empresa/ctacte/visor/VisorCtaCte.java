package com.yhaguy.gestion.empresa.ctacte.visor;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class VisorCtaCte {
	
	static final String ZUL_VISOR_CTA_CTE = "/yhaguy/gestion/empresa/VisorCtaCte.zul";
	
	@Command
	public void visorCtaCte() {		
		Window win = (Window) Executions.createComponents(ZUL_VISOR_CTA_CTE, null, null);
		win.doModal();
	}
}
