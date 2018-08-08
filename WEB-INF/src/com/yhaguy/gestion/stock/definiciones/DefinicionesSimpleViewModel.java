package com.yhaguy.gestion.stock.definiciones;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SimpleViewModel;

public class DefinicionesSimpleViewModel extends SimpleViewModel{
	
	StockDefinicionesViewModel dato;
	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) StockDefinicionesViewModel dato)
			throws Exception {
		this.dato = dato;
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());
		this.setTextoFormularioCorriente("Definiciones");
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}

	public StockDefinicionesViewModel getDato() {
		return dato;
	}

	public void setDato(StockDefinicionesViewModel dato) {
		this.dato = dato;
	}
}
