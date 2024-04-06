package com.yhaguy.gestion.cobranzas;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Filedownload;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class ResumenDiarioVM extends SimpleViewModel {

	private String anho;
	private String mes;
	
	private double totalContado = 0.0;
	private double totalCobroCap = 0.0;
	private double totalCobroInt = 0.0;
	private double totalCobroAdm = 0.0;
	private double total = 0.0;
	
	private List<Object[]> listResumen;
	
	@Init(superclass = true)
	public void init() {
		try {
			this.anho = Utiles.getAnhoActual();
			this.mes = (String) Utiles.getNombreMes(new Date());
			this.listResumen = new ArrayList<>();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@DependsOn({ "anho", "mes" })
	public List<Object[]> getResumen() throws Exception {
		this.totalContado = 0.0; this.totalCobroCap = 0.0;
		this.totalCobroInt = 0.0; this.totalCobroAdm = 0.0;
		this.total = 0.0;
		int nroMes = Utiles.getNumeroMes(this.mes);
		int last = Utiles.getUltimoDiaMes(Integer.parseInt(this.anho), nroMes); 
        String last_ = String.format("%02d", last);
        String nroMes_ = String.format("%02d", nroMes);
		RegisterDomain rr = RegisterDomain.getInstance();
		String desde = this.anho + "-" + nroMes_ + "-" + "01" + " 00:00:00";
		String hasta = this.anho + "-" + nroMes_ + "-" + last_ + " 23:59:00";
		
		Map<String, Object[]> map = new HashMap<>();
		
		List<Object[]> cobros = rr.getResumenCobranzas(desde, hasta, CajaPeriodo.TIPO_COBROS_MOBILE);
		List<Object[]> cobrosInt = rr.getResumenCobranzas(desde, hasta, CajaPeriodo.TIPO_COBROS_MOBILE_INT);
		List<Object[]> cobrosAdm = rr.getResumenCobranzas(desde, hasta, CajaPeriodo.TIPO_COBROS);
		List<Object[]> contado = rr.getResumenContado(desde, hasta);
		List<Object[]> notaCred = rr.getResumenNotaCreditoContado(desde, hasta);
		
		for (int i = 0; i < contado.size(); i++) {
			map.put(contado.get(i)[0] + "", new Object[] { contado.get(i)[0], contado.get(i)[1], 0.0, 0.0, 0.0, 0.0 });
		}
		
		for (int i = 0; i < notaCred.size(); i++) {
			Object[] data = map.get(notaCred.get(i)[0] + "");
			double ncr = (double) notaCred.get(i)[1];
			if (data != null) {
				double importe = (double) data[1];
				data[1] = (importe - ncr);
			} else {
				data = new Object[] { notaCred.get(i)[0], (ncr * -1), 0.0, 0.0, 0.0, 0.0 };
			}			
			map.put(notaCred.get(i)[0] + "", data);
		}
		
		for (int i = 0; i < cobros.size(); i++) {
			Object[] data = map.get(cobros.get(i)[0] + "");
			if (data != null) {
				data[2] = cobros.get(i)[1];
			} else {
				data = new Object[] { cobros.get(i)[0], 0.0, cobros.get(i)[1], 0.0, 0.0, 0.0 };
			}
			map.put(cobros.get(i)[0] + "", data);
		}
		
		for (int i = 0; i < cobrosInt.size(); i++) {
			Object[] data = map.get(cobrosInt.get(i)[0] + "");
			if (data != null) {
				data[3] = cobrosInt.get(i)[1];
			} else {
				data = new Object[] { cobrosInt.get(i)[0], 0.0, 0.0, cobrosInt.get(i)[1], 0.0, 0.0 };
			}
			map.put(cobrosInt.get(i)[0] + "", data);
		}
		
		for (int i = 0; i < cobrosAdm.size(); i++) {
			Object[] data = map.get(cobrosAdm.get(i)[0] + "");
			if (data != null) {
				data[4] = cobrosAdm.get(i)[1];
			} else {
				data = new Object[] { cobrosAdm.get(i)[0], 0.0, 0.0, 0.0, cobrosAdm.get(i)[1], 0.0 };
			}
			map.put(cobrosAdm.get(i)[0] + "", data);
		}
		
		for (String key : map.keySet()) {
			Object[] data = map.get(key);
			double a = (double) data[1];
			double b = (double) data[2];
			double c = (double) data[3];
			double d = (double) data[4];
			this.totalContado += a;
			this.totalCobroCap += b;
			this.totalCobroInt += c;
			this.totalCobroAdm += d;			
			data[5] = (a + b + c + d);
			double e = (double) data[5];
			this.total += e;
			map.put(key, data);
		}		
		BindUtils.postNotifyChange(null, null, this, "totalContado");
		BindUtils.postNotifyChange(null, null, this, "totalCobroCap");
		BindUtils.postNotifyChange(null, null, this, "totalCobroInt");
		BindUtils.postNotifyChange(null, null, this, "totalCobroAdm");
		BindUtils.postNotifyChange(null, null, this, "total");
		
		List<Object[]> out = new ArrayList<Object[]>(map.values());
		Collections.sort(out, Comparator.comparing(o -> (Date) o[0]));
		this.listResumen = out;
		return out;
	}
	
	@Command
	public void exportExcel() throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Resumen Diario Cobranzas");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("FECHA");
		r.createCell(cell++).setCellValue("CONTADO");
		r.createCell(cell++).setCellValue("MOVILES CAPITAL");
		r.createCell(cell++).setCellValue("MOVILES INTERIOR");	
		r.createCell(cell++).setCellValue("ADMINISTRACION");
		r.createCell(cell++).setCellValue("TOTAL DEL DIA");
		for (Object[] c : this.listResumen) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(Utiles.getDateToString((Date) c[0], "dd-MM-yyyy"));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[1]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[2]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[3]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[4]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[5]));
		}
		listSheet.autoSizeColumn(0);
		listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2);
		listSheet.autoSizeColumn(3);
		listSheet.autoSizeColumn(4);
		listSheet.autoSizeColumn(5);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("ResumenDiarioCobranzas.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * GET'S SET'S
	 */

	public List<String> getMeses() {
		return Utiles.getMeses_();
	}
	
	public List<String> getAnhos() {
		return Utiles.getAnhos();
	}
	
	public String getAnho() {
		return anho;
	}

	public void setAnho(String anho) {
		this.anho = anho;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public double getTotalContado() {
		return totalContado;
	}

	public void setTotalContado(double totalContado) {
		this.totalContado = totalContado;
	}

	public double getTotalCobroCap() {
		return totalCobroCap;
	}

	public void setTotalCobroCap(double totalCobroCap) {
		this.totalCobroCap = totalCobroCap;
	}

	public double getTotalCobroInt() {
		return totalCobroInt;
	}

	public void setTotalCobroInt(double totalCobroInt) {
		this.totalCobroInt = totalCobroInt;
	}

	public double getTotalCobroAdm() {
		return totalCobroAdm;
	}

	public void setTotalCobroAdm(double totalCobroAdm) {
		this.totalCobroAdm = totalCobroAdm;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}	
}
