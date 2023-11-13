package com.yhaguy.gestion.rrhh;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Filedownload;

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
			this.planillas.add(new BeanIps(item.getCedula(), item.getFuncionario(), item.getDiasTrabajados(),
					item.getBonificacion(), (item.getTotalHaberes_() - item.getBonificacion() - item.getAguinaldo())));
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
	
	@Command
	public void exportExcel() throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Planilla Salarios");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("CEDULA");
		r.createCell(cell++).setCellValue("APELLIDOS Y NOMBRES");
		r.createCell(cell++).setCellValue("DIAS TRABAJADOS");
		r.createCell(cell++).setCellValue("BONIF.FLIAR");
		r.createCell(cell++).setCellValue("COMISION");
		r.createCell(cell++).setCellValue("SALARIO");
		r.createCell(cell++).setCellValue("TOTAL");
		r.createCell(cell++).setCellValue("IPS 9%");
		
		double comisiones = 0.0;
		double salarios = 0.0;
		double totales = 0.0;
		double ips = 0.0;	
		double bonif = 0.0;
		
		for (BeanIps p : this.getPlanillas()) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(p.getCedula() + ""); 
			row.createCell(cellIndex++).setCellValue(p.getFuncionario() + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getDiasTrabajados())); 
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getBonificacion()));
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getComision()));
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getSalario()));
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getTotal()));
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getIps()));
			
			comisiones += p.getComision();
			salarios += p.getSalario();
			totales += p.getTotal();
			ips += p.getIps();
			bonif += p.getBonificacion();
		}
		
		Row t = listSheet.createRow(rowIndex++);
		int cell_ = 0;
		t.createCell(cell_++).setCellValue("TOTALES");
		t.createCell(cell_++).setCellValue("");
		t.createCell(cell_++).setCellValue("");
		t.createCell(cell_++).setCellValue(Utiles.getRedondeo(bonif));
		t.createCell(cell_++).setCellValue(Utiles.getRedondeo(comisiones));
		t.createCell(cell_++).setCellValue(Utiles.getRedondeo(salarios));
		t.createCell(cell_++).setCellValue(Utiles.getRedondeo(totales));
		t.createCell(cell_++).setCellValue(Utiles.getRedondeo(ips));
		
		listSheet.autoSizeColumn(0); listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2); listSheet.autoSizeColumn(3);
		listSheet.autoSizeColumn(4); listSheet.autoSizeColumn(5);
		listSheet.autoSizeColumn(6); listSheet.autoSizeColumn(7);
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("PlanillaIPS.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
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
		double bonif = 0.0;
		for (BeanIps item : this.planillas) {
			comisiones += item.getComision();
			salarios += item.getSalario();
			totales += item.getTotal();
			ips += item.getIps();
			bonif += item.getBonificacion();
		}		
		return new Double[] { comisiones, salarios, totales, ips, bonif, totales * 0.255 };
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
