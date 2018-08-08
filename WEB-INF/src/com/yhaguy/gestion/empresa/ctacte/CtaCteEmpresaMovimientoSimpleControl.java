package com.yhaguy.gestion.empresa.ctacte;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.libro.BancoControlBody;
import com.yhaguy.gestion.empresa.EmpresaControlBody;

public class CtaCteEmpresaMovimientoSimpleControl extends SoloViewModel {

	EmpresaControlBody empCtrlBdy;
	CtaCteAsignarImputacionSimpleControl asigImpSimCtrl;
	
	private List<CtaCteImputacionDTO> selectedImputaciones = new ArrayList<CtaCteImputacionDTO>();

	@AfterCompose(superclass = true)
	public void afterCtaCteEmpresaMovimientoSimpleControl() {
	}

	@Init(superclass = true)
	public void initCtaCteEmpresaMovimientoSimpleControl(
			@ExecutionArgParam("dato") EmpresaControlBody dato) {
		this.empCtrlBdy = dato;
	}

	@Override
	public String getAliasFormularioCorriente() {
		return ID.F_CTA_CTE_EMPRESA_DETALLE_MOVIMIENTO;
	}

	public CtaCteEmpresaMovimientoDTO getMovimientoSelecDto() {
		return empCtrlBdy.getSelectedMov();
	}

	public void setMovimientoSelecDto(CtaCteEmpresaMovimientoDTO dto) {
		this.empCtrlBdy.setSelectedMov(dto);
	}

	public List<CtaCteImputacionDTO> getSelectedImputaciones() {
		return selectedImputaciones;
	}

	public void setSelectedImputaciones(
			List<CtaCteImputacionDTO> selectedImputaciones) {
		this.selectedImputaciones = selectedImputaciones;
	}

	
	public CtaCteAsignarImputacionSimpleControl getAsigImpSimCtrl() {
		return asigImpSimCtrl;
	}

	public void setAsigImpSimCtrl(
			CtaCteAsignarImputacionSimpleControl asigImpSimCtrl) {
		this.asigImpSimCtrl = asigImpSimCtrl;
	}

	@Command
	@NotifyChange("*")
	public void asignarImputacion() throws Exception {

		if (isAsignarImputacionDisabled()) {
			this.mensajeError("No posee los permisos necesarios para acceder a esta operacion");
			return;
		}
		
		try {

			WindowPopup win = new WindowPopup();
			win.setDato(this);
			win.setHigth("600px");
			win.setWidth("1000px");
			win.setTitulo("Asignar Imputacion");
			win.setModo(WindowPopup.NUEVO);
			win.show(Configuracion.CTACTE_EMPRESA_ASIGNAR_IMPUTACION_ZUL);
			
			if (win.isClickAceptar() == true) {

				this.asigImpSimCtrl.actualizarCtaCte();				
				//Refresca la lista de movimientos
				this.empCtrlBdy.buscarMovimientos();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getMovimientoAplicable() {
		// Cambiar a tipo de movimiento
		if (this.getMovimientoSelecDto().getSaldo() < 0) {
			return false;
		}
		return true;
	}

	public boolean isAsignarImputacionDisabled() throws Exception {
		if (this.operacionHabilitada("AbrirAsignarImputacionCtaCte", ID.F_CTA_CTE_EMPRESA_ASIGNAR_IMPUTACION))
			return false;
		return true;
	}
	
	//********************************Asignar Imputacion**********************************/
	
	

}
