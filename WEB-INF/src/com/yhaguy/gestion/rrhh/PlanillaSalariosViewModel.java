package com.yhaguy.gestion.rrhh;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RRHHPlanillaSalarios;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class PlanillaSalariosViewModel extends SimpleViewModel {

	private String selectedMes = "";
	private String selectedAnho = "";
	
	private List<Object[]> selectedFuncionarios;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void generarPlanilla() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Object[] func : this.selectedFuncionarios) {
			RRHHPlanillaSalarios pl = new RRHHPlanillaSalarios();
			pl.setMes(this.selectedMes);
			pl.setAnho(this.selectedAnho);
			pl.setFuncionario((String) func[1]);
			rr.saveObject(pl, this.getLoginNombre());
		}
	}
	
	/**
	 * GETS / SETS 
	 */
	
	public List<RRHHPlanillaSalarios> getPlanillas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(RRHHPlanillaSalarios.class.getName());
	}
	
	public List<Object[]> getFuncionarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionarios_();
	}
	
	public List<String> getMeses() {
		return Utiles.getMeses_();
	}
	
	public List<String> getAnhos() {
		return Utiles.getAnhos();
	}

	public List<Object[]> getSelectedFuncionarios() {
		return selectedFuncionarios;
	}

	public void setSelectedFuncionarios(List<Object[]> selectedFuncionarios) {
		this.selectedFuncionarios = selectedFuncionarios;
	}

	public String getSelectedMes() {
		return selectedMes;
	}

	public void setSelectedMes(String selectedMes) {
		this.selectedMes = selectedMes;
	}

	public String getSelectedAnho() {
		return selectedAnho;
	}

	public void setSelectedAnho(String selectedAnho) {
		this.selectedAnho = selectedAnho;
	}
}
