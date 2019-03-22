package com.yhaguy.gestion.bancos.tarjetas;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.ProcesadoraTarjeta;
import com.yhaguy.domain.RegisterDomain;

public class BancoTarjetaViewModel extends SimpleViewModel {
	
	static final String T_CREDITO = "TARJETA DE CRÉDITO";
	static final String T_DEBITO = "TARJETA DE DÉBITO";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	
	/**
	 * GETS / SETS
	 */
	
	@SuppressWarnings("unchecked")
	public List<ProcesadoraTarjeta> getProcesadoras() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ProcesadoraTarjeta.class.getName());
	}
	
	/**
	 * @return los tipos de tarjeta
	 */
	public List<String> getTiposTarjetas() {
		List<String> out = new ArrayList<String>();
		out.add(T_CREDITO);
		out.add(T_DEBITO);
		return out;
	}
	
	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFechaDD) {
		this.filterFechaDD = filterFechaDD;
	}

	public String getFilterFechaMM() {
		return filterFechaMM;
	}

	public void setFilterFechaMM(String filterFechaMM) {
		this.filterFechaMM = filterFechaMM;
	}

	public String getFilterFechaAA() {
		return filterFechaAA;
	}

	public void setFilterFechaAA(String filterFechaAA) {
		this.filterFechaAA = filterFechaAA;
	}	
}
