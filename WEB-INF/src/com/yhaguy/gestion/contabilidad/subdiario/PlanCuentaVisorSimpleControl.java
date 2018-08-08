package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Execution;

import com.coreweb.Config;
import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.PlanDeCuenta;
import com.yhaguy.domain.RegisterDomain;

public class PlanCuentaVisorSimpleControl extends SimpleViewModel{
	
	BrowserPlanCuenta bpc = null;
	private List<Object[]> data;

	@Init(superclass = true)
	public void initPlanCuentaVisorSimpleControl(
			@ContextParam(ContextType.EXECUTION) Execution execution)
			throws Exception {

		this.bpc = (BrowserPlanCuenta) execution
				.getAttribute(Config.BROWSER2_VM);

		this.bpc.refreshBrowser();

	}

	@AfterCompose(superclass = true)
	public void afterComposePlanCuentaVisorSimpleControl() {
	}	
	@NotifyChange("*")
	@Command
	public void reportePlanCuenta() throws Exception{
		
		RegisterDomain rr = RegisterDomain.getInstance();
		data = rr.getPlanDeCuentasReporte();
		
		ReportePlanCuenta repoPC = new ReportePlanCuenta();
		repoPC.setDatosReporte(data);
		//repoPC.setApaisada();
		
		ViewPdf vp = new ViewPdf();
		vp.showReporte(repoPC, this);
		
	}
}
