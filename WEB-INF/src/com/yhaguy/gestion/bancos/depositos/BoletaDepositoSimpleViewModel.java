package com.yhaguy.gestion.bancos.depositos;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;

public class BoletaDepositoSimpleViewModel extends SoloViewModel {
	
	private BoletaDepositoViewModel dato;

	@Init(superclass=true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) BoletaDepositoViewModel dato) {
		this.dato = dato;
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose() {
	}
	
	@Command
	public void notifyTotalRecaudacion(@BindingParam("item") MyArray item) {		
		if (!this.dato.getSelectedsRecaudacionesCentral().contains(item)) {
			this.dato.getSelectedsRecaudacionesCentral().add(item);
		}		
		double out = 0;
		for (MyArray rcc : this.dato.getSelectedsRecaudacionesCentral()) {
			out += (double) rcc.getPos4();
		}
		this.dato.setTotalRecaudacionCentral(out);
		BindUtils.postNotifyChange(null, null, this.dato, "selectedsRecaudacionesCentral");
		BindUtils.postNotifyChange(null, null, this.dato, "totalRecaudacionCentral");
	}

	/**
	 * GETS / SETS
	 */
	
	/**
	 * utilDto de la app.
	 */
	public UtilDTO getDtoUtil() {
		UtilDTO u = (UtilDTO)super.getDtoUtil();
		return u;
	}
	
	public BoletaDepositoViewModel getDato() {
		return dato;
	}

	public void setDato(BoletaDepositoViewModel dato) {
		this.dato = dato;
	}	
}
