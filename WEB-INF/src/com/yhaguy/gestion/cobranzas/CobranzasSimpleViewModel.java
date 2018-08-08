package com.yhaguy.gestion.cobranzas;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;

public class CobranzasSimpleViewModel extends SoloViewModel {
	
	private CobranzasViewModel dato;

	@Init(superclass = true)
	public void Init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) CobranzasViewModel dato) {
		this.dato = dato;
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}

	public CobranzasViewModel getDato() {
		return dato;
	}

	public void setDato(CobranzasViewModel dato) {
		this.dato = dato;
	}
}
