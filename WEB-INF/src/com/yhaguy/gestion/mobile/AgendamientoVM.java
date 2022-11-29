package com.yhaguy.gestion.mobile;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TareaProgramada;

public class AgendamientoVM extends SimpleViewModel {
	
	private Date desde;
	private Date hasta;
	
	@Init(superclass = true)
	public void init() {
		this.desde = new Date();
		this.hasta = new Date();
	}
	
	/**
	 * @return las tareas programadas..
	 */
	@DependsOn({ "desde", "hasta" })
	public List<TareaProgramada> getTareas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<TareaProgramada> tareas = rr.getTareasProgramadas(this.desde, this.hasta, "PASAR A COBRAR");			
		return tareas;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}
}
