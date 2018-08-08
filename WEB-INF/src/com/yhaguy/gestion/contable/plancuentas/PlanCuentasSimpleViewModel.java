package com.yhaguy.gestion.contable.plancuentas;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.PlanCuenta;
import com.yhaguy.domain.RegisterDomain;

public class PlanCuentasSimpleViewModel extends SimpleViewModel {

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return el plan de cuentas..
	 */
	public List<PlanCuenta> getPlanDeCuentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getPlanCuentas();
	}
}
