package com.yhaguy.gestion.reportes;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Bandbox;

import com.coreweb.Config;
import com.coreweb.control.SimpleViewModel;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Deposito;

public class ReportesSimpleVM extends SimpleViewModel {
	
	private ReportesViewModel dato;
	
	private List<Deposito> selectedItems = new ArrayList<Deposito>();

	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) ReportesViewModel dato) {
		this.dato = dato;		
		String labelF = this.getUs().formLabel(dato.getAliasFormularioCorriente());
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());	
		this.setTextoFormularioCorriente(labelF);
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void excluirCliente(@BindingParam("comp") Bandbox comp) {
		this.dato.getFiltro().getSelectedClientes().add(this.dato.getFiltro().getCliente());
		this.dato.getFiltro().setCliente(null);
		this.dato.getFiltro().setRazonSocialCliente("");
		comp.close();
	}
	
	@Command
	public void selectResumen(@BindingParam("comp") Bandbox comp) throws Exception {
		this.dato.getFiltro().selectResumen();
		comp.close();
	}
	
	/**
	 * GET / SET
	 */
	
	/**
	 * @return el zul que contiene los parametros del reporte..
	 */
	public String getParametroZul() {
		return "/yhaguy/gestion/reportes/parametros/" + this.dato.getSelectedReporte().getPos1() + ".zul";
	}
	
	public UtilDTO getUtilDto(){
		return (UtilDTO) this.getDtoUtil();		
	}
	
	public ReportesViewModel getDato() {
		return dato;
	}

	public void setDato(ReportesViewModel dato) {
		this.dato = dato;
	}

	public List<Deposito> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<Deposito> selectedItems) {
		this.selectedItems = selectedItems;
	}
}
