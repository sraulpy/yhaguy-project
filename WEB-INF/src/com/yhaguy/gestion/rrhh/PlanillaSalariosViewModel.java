package com.yhaguy.gestion.rrhh;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
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
	
	private double totalSalarios = 0;
	private double totalComision = 0;
	private double totalAnticipo = 0;
	private double totalBonificacion = 0;
	private double totalOtrosHaberes = 0;
	private double totalOtrosDescuentos = 0;
	private double totalCorporativo = 0;
	private double totalUniforme = 0;
	private double totalRepuestos = 0;
	private double totalSeguro = 0;
	private double totalEmbargo = 0;
	private double totalIps = 0;
	
	@Init(superclass = true)
	public void init() {
		try {
			this.selectedAnho = Utiles.getAnhoActual();
			this.selectedMes = (String) Utiles.getMesCorriente(this.selectedAnho).getPos2();
		} catch (Exception e) {
		}
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
	
	@Command
	@NotifyChange({ "totalSalarios", "totalComision", "totalAnticipo", "totalBonificacion", "totalOtrosHaberes",
			"totalOtrosDescuentos", "totalCorporativo", "totalUniforme", "totalRepuestos", "totalSeguro",
			"totalEmbargo", "totalIps" })
	public void saveItem(@BindingParam("item") RRHHPlanillaSalarios item) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(item, this.getLoginNombre());
		this.totalSalarios += item.getSalarios();
		this.totalComision += item.getComision();
		this.totalAnticipo += item.getAnticipo();
		this.totalBonificacion += item.getBonificacion();
		this.totalOtrosHaberes += item.getOtrosHaberes();
		this.totalOtrosDescuentos += item.getOtrosDescuentos();
		this.totalCorporativo += item.getCorporativo();
		this.totalUniforme += item.getUniforme();
		this.totalRepuestos += item.getRepuestos();
		this.totalSeguro += item.getSeguro();
		this.totalEmbargo += item.getEmbargo();
		this.totalIps += item.getIps();
	}
	
	/**
	 * GETS / SETS 
	 */
	
	@DependsOn({ "selectedMes", "selectedAnho" })
	public List<RRHHPlanillaSalarios> getPlanillas() throws Exception {
		this.totalSalarios = 0; this.totalComision = 0;
		this.totalAnticipo = 0; this.totalBonificacion = 0;
		this.totalOtrosHaberes = 0; this.totalOtrosDescuentos = 0;
		this.totalCorporativo = 0; this.totalUniforme = 0;
		this.totalRepuestos = 0; this.totalSeguro = 0;
		this.totalEmbargo = 0; this.totalIps = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<RRHHPlanillaSalarios> out = rr.getPlanillaSalarios(this.selectedMes, this.selectedAnho);
		for (RRHHPlanillaSalarios item : out) {
			this.totalSalarios += item.getSalarios();
			this.totalComision += item.getComision();
			this.totalAnticipo += item.getAnticipo();
			this.totalBonificacion += item.getBonificacion();
			this.totalOtrosHaberes += item.getOtrosHaberes();
			this.totalOtrosDescuentos += item.getOtrosDescuentos();
			this.totalCorporativo += item.getCorporativo();
			this.totalUniforme += item.getUniforme();
			this.totalRepuestos += item.getRepuestos();
			this.totalSeguro += item.getSeguro();
			this.totalEmbargo += item.getEmbargo();
			this.totalIps += item.getIps();
		}
		BindUtils.postNotifyChange(null, null, this, "totalSalarios");
		BindUtils.postNotifyChange(null, null, this, "totalComision");
		BindUtils.postNotifyChange(null, null, this, "totalAnticipo");
		BindUtils.postNotifyChange(null, null, this, "totalBonificacion");
		BindUtils.postNotifyChange(null, null, this, "totalOtrosHaberes");
		BindUtils.postNotifyChange(null, null, this, "totalOtrosDescuentos");
		BindUtils.postNotifyChange(null, null, this, "totalCorporativo");
		BindUtils.postNotifyChange(null, null, this, "totalUniforme");
		BindUtils.postNotifyChange(null, null, this, "totalRepuestos");
		BindUtils.postNotifyChange(null, null, this, "totalSeguro");
		BindUtils.postNotifyChange(null, null, this, "totalEmbargo");
		BindUtils.postNotifyChange(null, null, this, "totalIps");
		return out;
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

	public double getTotalSalarios() {
		return totalSalarios;
	}

	public void setTotalSalarios(double totalSalarios) {
		this.totalSalarios = totalSalarios;
	}

	public double getTotalComision() {
		return totalComision;
	}

	public void setTotalComision(double totalComision) {
		this.totalComision = totalComision;
	}

	public double getTotalAnticipo() {
		return totalAnticipo;
	}

	public void setTotalAnticipo(double totalAnticipo) {
		this.totalAnticipo = totalAnticipo;
	}

	public double getTotalBonificacion() {
		return totalBonificacion;
	}

	public void setTotalBonificacion(double totalBonificacion) {
		this.totalBonificacion = totalBonificacion;
	}

	public double getTotalOtrosHaberes() {
		return totalOtrosHaberes;
	}

	public void setTotalOtrosHaberes(double totalOtrosHaberes) {
		this.totalOtrosHaberes = totalOtrosHaberes;
	}

	public double getTotalOtrosDescuentos() {
		return totalOtrosDescuentos;
	}

	public void setTotalOtrosDescuentos(double totalOtrosDescuentos) {
		this.totalOtrosDescuentos = totalOtrosDescuentos;
	}

	public double getTotalCorporativo() {
		return totalCorporativo;
	}

	public void setTotalCorporativo(double totalCorporativo) {
		this.totalCorporativo = totalCorporativo;
	}

	public double getTotalUniforme() {
		return totalUniforme;
	}

	public void setTotalUniforme(double totalUniforme) {
		this.totalUniforme = totalUniforme;
	}

	public double getTotalRepuestos() {
		return totalRepuestos;
	}

	public void setTotalRepuestos(double totalRepuestos) {
		this.totalRepuestos = totalRepuestos;
	}

	public double getTotalSeguro() {
		return totalSeguro;
	}

	public void setTotalSeguro(double totalSeguro) {
		this.totalSeguro = totalSeguro;
	}

	public double getTotalEmbargo() {
		return totalEmbargo;
	}

	public void setTotalEmbargo(double totalEmbargo) {
		this.totalEmbargo = totalEmbargo;
	}

	public double getTotalIps() {
		return totalIps;
	}

	public void setTotalIps(double totalIps) {
		this.totalIps = totalIps;
	}
}
