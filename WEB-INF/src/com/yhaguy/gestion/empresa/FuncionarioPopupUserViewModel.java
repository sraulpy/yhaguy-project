package com.yhaguy.gestion.empresa;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.SoloViewModel;
import com.yhaguy.UtilDTO;

public class FuncionarioPopupUserViewModel extends SoloViewModel {

	private FuncionarioControlBody dato;

	@AfterCompose(superclass = true)
	public void afterFuncionarioPopupUserViewModel() {
	}

	@Init(superclass = true)
	public void initFuncionarioPopupUserViewModel(@ExecutionArgParam("dato") FuncionarioControlBody dato) {
		this.dato = dato;
	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}

	public FuncionarioControlBody getDato() {
		return dato;
	}

	public void setDato(FuncionarioControlBody dto) {
		this.dato = dto;
	}

	public UtilDTO getDtoUtil() {
		UtilDTO u = (UtilDTO) super.getDtoUtil();
		return u;
	}
	
	@NotifyChange("*")
	@Command
	public void actualizarSucursalOperativa(){
		
	}

}
