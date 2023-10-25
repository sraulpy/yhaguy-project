package com.yhaguy.gestion.rrhh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RRHHPlanillaSalarios;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class PlanillaIpsViewModel extends SimpleViewModel {
	
	private String selectedAnho = "";
	private String selectedMes = "";
	
	private List<BeanIps> planillas = new ArrayList<>();

	@Init(superclass = true)
	public void init() {
		try {
			this.selectedAnho = Utiles.getAnhoActual();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange({ "planillas", "totales" })
	public void selectPeriodo() throws Exception {
		this.planillas = new ArrayList<>();
		for (RRHHPlanillaSalarios item : this.getPlanillaSalarios()) {
			this.planillas.add(new BeanIps(item.getCedula(), item.getFuncionario(), item.getDiasTrabajados(), item.getBonificacion(), item.getSalarioFinal()));
		}
		Map<String, Double> map = new HashMap<>();
		for (RRHHPlanillaSalarios item : this.getPlanillaComisiones()) {
			map.put(item.getCedula(), item.getComisiones());
		}
		for (BeanIps item : this.planillas) {
			if (map.get(item.getCedula()) != null) {
				item.setComision(map.get(item.getCedula()));
			} else {
				item.setComision(0.0);
			}
		}
	}
	
	/**
	 * @return las planillas..
	 */
	public List<RRHHPlanillaSalarios> getPlanillaSalarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<RRHHPlanillaSalarios> out = rr.getPlanillaSalarios(this.selectedMes, this.selectedAnho, RRHHPlanillaSalarios.TIPO_SALARIOS);
		return out;
	}
	
	/**
	 * @return las planillas..
	 */
	public List<RRHHPlanillaSalarios> getPlanillaComisiones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<RRHHPlanillaSalarios> out = rr.getPlanillaSalarios(this.selectedMes, this.selectedAnho, RRHHPlanillaSalarios.TIPO_COMISIONES);
		return out;
	}
	
	public Double[] getTotales() {
		double comisiones = 0.0;
		double salarios = 0.0;
		double totales = 0.0;
		double ips = 0.0;		
		for (BeanIps item : this.planillas) {
			comisiones += item.getComision();
			salarios += item.getSalario();
			totales += item.getTotal();
			ips += item.getIps();
		}		
		return new Double[] { comisiones, salarios, totales, ips, totales * 0.255 };
	}

	public List<String> getMeses() {
		return Utiles.getMeses_();
	}
	
	public List<String> getAnhos() {
		return Utiles.getAnhos();
	}
	
	public String getSelectedAnho() {
		return selectedAnho;
	}

	public void setSelectedAnho(String selectedAnho) {
		this.selectedAnho = selectedAnho;
	}

	public String getSelectedMes() {
		return selectedMes;
	}

	public void setSelectedMes(String selectedMes) {
		this.selectedMes = selectedMes;
	}

	public List<BeanIps> getPlanillas() {
		return planillas;
	}

	public void setPlanillas(List<BeanIps> planillas) {
		this.planillas = planillas;
	}	
}
