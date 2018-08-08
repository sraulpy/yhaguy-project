package com.yhaguy.gestion.bancos.cheques;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SoloViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;

public class ChequesTercerosSimpleVM extends SoloViewModel {

	private ChequesTercerosViewModel dato;
	
	@Init(superclass = true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) ChequesTercerosViewModel dato) {
		this.dato = dato;
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	
	/**
	 * GETS / SETS 
	 */
	
	public UtilDTO getDtoUtil() {
		UtilDTO u = (UtilDTO)super.getDtoUtil();
		return u;
	}
	
	public ChequesTercerosViewModel getDato() {
		return dato;
	}

	public void setDato(ChequesTercerosViewModel dato) {
		this.dato = dato;
	}
}
