package com.yhaguy.gestion.compras.definiciones;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;

public class CuentasContablesDefinicionesSimpleVM extends SoloViewModel {

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) CuentasContablesDefinicionesViewModel dato) {
		this.dato = dato;
		this.mostrarDatos(dato.getTipoSeleccionado());
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}
	
	private CuentasContablesDefinicionesViewModel dato;
	
	private boolean cuentaContable = false;
	private boolean planDeCuenta = false;
	private boolean centroCosto = false;
	private boolean departamento = false;
	private boolean listCentroCosto = false;
	private boolean listCuentaContable = false;
	
	/**
	 * Determina que datos mostrar en el popup..
	 * @param tipo
	 */
	private void mostrarDatos(int tipo) {
		
		switch (tipo) {
		
		case CuentasContablesDefinicionesViewModel.DATOS_CENTRO_COSTO:
			this.centroCosto = true;
			break;

		case CuentasContablesDefinicionesViewModel.DATOS_CUENTA_CONTABLE:
			this.cuentaContable = true;
			break;
			
		case CuentasContablesDefinicionesViewModel.DATOS_DEPARTAMENTO:
			this.departamento = true;
			break;
			
		case CuentasContablesDefinicionesViewModel.DATOS_PLAN_DE_CUENTA:
			this.planDeCuenta = true;
			break;		
			
		case CuentasContablesDefinicionesViewModel.DATOS_LIST_CENTRO_COSTO:
			this.listCentroCosto = true;
			break;
			
		case CuentasContablesDefinicionesViewModel.DATOS_LIST_CUENTA_CONTABLE:
			this.listCuentaContable = true;
			break;
		}
	}
	
	
	/**
	 * Concatena dos Strings..
	 */
	public String concat(String uno, String dos) {
		return uno + " - " + dos;
	}
	
	
	public boolean isCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(boolean cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public boolean isPlanDeCuenta() {
		return planDeCuenta;
	}

	public void setPlanDeCuenta(boolean planDeCuenta) {
		this.planDeCuenta = planDeCuenta;
	}

	public boolean isCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(boolean centroCosto) {
		this.centroCosto = centroCosto;
	}

	public boolean isDepartamento() {
		return departamento;
	}

	public void setDepartamento(boolean departamento) {
		this.departamento = departamento;
	}

	public CuentasContablesDefinicionesViewModel getDato() {
		return dato;
	}

	public void setDato(CuentasContablesDefinicionesViewModel dato) {
		this.dato = dato;
	}

	public boolean isListCentroCosto() {
		return listCentroCosto;
	}

	public void setListCentroCosto(boolean listCentroCosto) {
		this.listCentroCosto = listCentroCosto;
	}

	public boolean isListCuentaContable() {
		return listCuentaContable;
	}

	public void setListCuentaContable(boolean listCuentaContable) {
		this.listCuentaContable = listCuentaContable;
	}
	
}
