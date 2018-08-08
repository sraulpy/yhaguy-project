package com.yhaguy.gestion.modulos;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;

public class PopupSimpleViewModel extends SoloViewModel {

	ConfiguracionModuloVM dato;

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) ConfiguracionModuloVM dato)
			throws Exception {
		this.dato = dato;
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}
	
	
	/************ GET Y SET *************/

	
	public ConfiguracionModuloVM getDato() {
		return dato;
	}

	public void setDato(ConfiguracionModuloVM dato) {
		this.dato = dato;
	}

}
