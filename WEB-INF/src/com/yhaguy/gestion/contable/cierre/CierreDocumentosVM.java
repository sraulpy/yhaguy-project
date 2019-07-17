package com.yhaguy.gestion.contable.cierre;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;

public class CierreDocumentosVM extends SimpleViewModel {

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * GETS / SETS
	 */
	
	public List<TipoMovimiento> getTiposMovimientos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTiposDeMovimientos();
	}
}
