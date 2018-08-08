package com.yhaguy.gestion.reparto;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SoloViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;

public class VerDetallesControl extends SoloViewModel{
	private RepartoSimpleVM dato;// = new RepartoControlBody();

	public RepartoSimpleVM getDato() {
		return dato;
	}

	public void setDato(RepartoSimpleVM dato) {
		this.dato = dato;
	}

	@Init(superclass = true)
	public void initInsertarDetalleRepartoControl(
			@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) RepartoSimpleVM dato) {
		this.dato = dato;
	}

	@AfterCompose(superclass = true)
	public void afterComposeInsertarDetalleRepartoControl() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return ID.F_REPARTO;
	}
	
}
