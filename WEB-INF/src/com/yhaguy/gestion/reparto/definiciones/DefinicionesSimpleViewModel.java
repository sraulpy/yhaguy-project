package com.yhaguy.gestion.reparto.definiciones;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SimpleViewModel;

public class DefinicionesSimpleViewModel extends SimpleViewModel {

	private RepartoDefinicionesVM dato;

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) RepartoDefinicionesVM dato) {
		this.dato = dato;
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());
		this.setTextoFormularioCorriente("Definiciones");
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}	

	/**************** GET AND SET ******************/

	public RepartoDefinicionesVM getDato() {
		return dato;
	}

	public void setDato(RepartoDefinicionesVM dato) {
		this.dato = dato;
		
	}
}
