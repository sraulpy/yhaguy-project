package com.yhaguy.gestion.rrhh;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.FuncionarioDescuento;
import com.yhaguy.domain.RRHHPlanillaSalarios;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class PlanillaSalariosViewModel extends SimpleViewModel {
	
	static final String ZUL_IMPRESION_LIQUIDACION = "/yhaguy/gestion/rrhh/impresion_liquidacion.zul";

	private String selectedMes = "";
	private String selectedAnho = "";
	private String selectedTipo = "";
	
	private Object[] selectedFormato;
	
	private RRHHPlanillaSalarios selectedPlanilla;
	
	private List<Object[]> selectedFuncionarios;
	private List<Object[]> selectedFuncionarios_;
	private List<RRHHPlanillaSalarios> planillas = new ArrayList<RRHHPlanillaSalarios>();
	
	private Window win;
	
	@Init(superclass = true)
	public void init() {
		try {
			this.selectedAnho = Utiles.getAnhoActual();
			this.selectedMes = (String) Utiles.getMesCorriente(this.selectedAnho).getPos2();
			this.planillas = this.getPlanillas_();
		} catch (Exception e) {
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void generarPlanilla(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Object[] func : this.selectedFuncionarios) {
			Funcionario f = rr.getFuncionarioById((long) func[0]);
			RRHHPlanillaSalarios pl = new RRHHPlanillaSalarios();
			pl.setMes(this.selectedMes);
			pl.setAnho(this.selectedAnho);
			pl.setTipo(this.selectedTipo);
			pl.setFuncionario((String) func[1]);
			pl.setCedula((String) func[2]);
			pl.setCargo((String) func[3]);
			if (pl.getTipo().equals(RRHHPlanillaSalarios.TIPO_SALARIOS)) {
				pl.setDiasTrabajados(30);
				pl.setSalarios(f.getSalarioVigente());
				pl.setBonificacion(f.getBonificacionFamiliarVigente());
				pl.setResponsabilidad(f.getBonificacionResponsabilidadVigente());
				for (FuncionarioDescuento desc : f.getDescuentos()) {
					if (desc.getCuotas() == 0 || desc.getSaldoCuotas() > 0) {
						double importe = desc.getImporteGs() * -1;
						switch (desc.getDescripcion()) {
						case FuncionarioDescuento.PRESTAMO:
							pl.setPrestamos(pl.getPrestamos() + importe);
							break;
						case FuncionarioDescuento.CORPORATIVO:
							pl.setCorporativo(pl.getCorporativo() + importe);
							break;
						case FuncionarioDescuento.OTROS:	
							pl.setOtrosDescuentos(pl.getOtrosDescuentos() + importe);
							break;
						case FuncionarioDescuento.REPUESTOS:	
							pl.setRepuestos(pl.getRepuestos() + importe);
							break;
						case FuncionarioDescuento.UNIFORME:		
							pl.setUniforme(pl.getUniforme() + importe);
							break;
						}
					}
					if (desc.getSaldoCuotas() > 0) {
						desc.setSaldoCuotas(desc.getSaldoCuotas() - 1);
						rr.saveObject(desc, this.getLoginNombre());
					}
				}
			}
			rr.saveObject(pl, this.getLoginNombre());
			comp.close();
		}
		this.planillas = this.getPlanillas_();
		this.selectedFuncionarios = new ArrayList<Object[]>();
	}
	
	@Command
	@NotifyChange("*")
	public void addPlanilla(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Object[] func : this.selectedFuncionarios_) {
			Funcionario f = rr.getFuncionarioById((long) func[0]);
			RRHHPlanillaSalarios pl = new RRHHPlanillaSalarios();
			pl.setMes(this.selectedMes);
			pl.setAnho(this.selectedAnho);
			pl.setTipo(this.selectedTipo);
			pl.setFuncionario((String) func[1]);
			pl.setCedula((String) func[2]);
			pl.setCargo((String) func[3]);
			if (pl.getTipo().equals(RRHHPlanillaSalarios.TIPO_SALARIOS)) {
				pl.setDiasTrabajados(30);
				pl.setSalarios(f.getSalarioVigente());
				pl.setBonificacion(f.getBonificacionFamiliarVigente());
				pl.setResponsabilidad(f.getBonificacionResponsabilidadVigente());
				for (FuncionarioDescuento desc : f.getDescuentos()) {
					if (desc.getCuotas() == 0 || desc.getSaldoCuotas() > 0) {
						double importe = desc.getImporteGs() * -1;
						switch (desc.getDescripcion()) {
						case FuncionarioDescuento.PRESTAMO:
							pl.setPrestamos(pl.getPrestamos() + importe);
							break;
						case FuncionarioDescuento.CORPORATIVO:
							pl.setCorporativo(pl.getCorporativo() + importe);
							break;
						case FuncionarioDescuento.OTROS:	
							pl.setOtrosDescuentos(pl.getOtrosDescuentos() + importe);
							break;
						case FuncionarioDescuento.REPUESTOS:	
							pl.setRepuestos(pl.getRepuestos() + importe);
							break;
						case FuncionarioDescuento.UNIFORME:		
							pl.setUniforme(pl.getUniforme() + importe);
							break;
						}
					}
					if (desc.getSaldoCuotas() > 0) {
						desc.setSaldoCuotas(desc.getSaldoCuotas() - 1);
						rr.saveObject(desc, this.getLoginNombre());
					}
				}
			}
			rr.saveObject(pl, this.getLoginNombre());
			comp.close();
		}
		this.planillas = this.getPlanillas_();
		this.selectedFuncionarios_ = new ArrayList<Object[]>();
	}
	
	@Command
	@NotifyChange({ "totales" })
	public void saveItem(@BindingParam("item") RRHHPlanillaSalarios item) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(item, this.getLoginNombre());
	}
	
	@Command
	@NotifyChange({ "planillas", "totales" })
	public void selectPeriodo() throws Exception {
		this.planillas = this.getPlanillas_();
	}
	
	@Command
	public void imprimir() throws Exception {
		if (this.selectedPlanilla.getTotalACobrar() < 0) {
			return;
		}
		this.imprimirLiquidacion();
	}
	
	@Command
	public void imprimirPlanilla() throws Exception {
		this.imprimirPlanilla_(this.selectedFormato);
	}
	
	@Command
	public void imprimirRecibo() throws Exception {
		this.imprimirRecibo_();
	}
	
	@Command
	public void exportExcel(@BindingParam("items") List<RRHHPlanillaSalarios> items) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Planilla Salarios");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("CEDULA");
		r.createCell(cell++).setCellValue("APELLIDOS Y NOMBRES");
		r.createCell(cell++).setCellValue("DIAS TRABAJADOS");
		r.createCell(cell++).setCellValue("CANT.HS.EXTRAS DIURNAS");
		r.createCell(cell++).setCellValue("CANT.HS.EXTRAS NOCTURNAS");
		r.createCell(cell++).setCellValue("SUELDO");
		r.createCell(cell++).setCellValue("BONIF.RESPONSABILIDAD");
		r.createCell(cell++).setCellValue("JORNAL DIARIO");
		r.createCell(cell++).setCellValue("SALARIO");
		r.createCell(cell++).setCellValue("BONIF.FAMILIAR");
		r.createCell(cell++).setCellValue("OTROS HABERES");
		r.createCell(cell++).setCellValue("HS.EXTRAS DIURNAS");
		r.createCell(cell++).setCellValue("HS.EXTRAS NOCTURNAS");
		r.createCell(cell++).setCellValue("AGUINALDO");
		r.createCell(cell++).setCellValue("COMISION");
		r.createCell(cell++).setCellValue("VACACIONES");
		r.createCell(cell++).setCellValue("TOTAL HABERES");
		
		r.createCell(cell++).setCellValue("PRESTAMOS");r.createCell(cell++).setCellValue("ANTICIPO SALARIO");
		r.createCell(cell++).setCellValue("ANTICIPO COMISION");
		r.createCell(cell++).setCellValue("ANTICIPO AGUINALDO");r.createCell(cell++).setCellValue("OTROS DESCUENTOS");
		r.createCell(cell++).setCellValue("CORPORATIVO");r.createCell(cell++).setCellValue("UNIFORME");
		r.createCell(cell++).setCellValue("REPUESTOS");r.createCell(cell++).setCellValue("AUSENCIA");
		r.createCell(cell++).setCellValue("IPS");r.createCell(cell++).setCellValue("TOTAL DESCUENTOS");
		r.createCell(cell++).setCellValue("TOTAL A COBRAR");
		for (RRHHPlanillaSalarios p : items) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(p.getCedula() + ""); row.createCell(cellIndex++).setCellValue(p.getFuncionario() + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getDiasTrabajados()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getCantidadHorasExtras()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getCantidadHorasExtrasNoc()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getSalarios()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getResponsabilidad()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getJornalDiario()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getSalarioFinal()) + "");	row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getBonificacion()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getOtrosHaberes()) + "");	row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getExtrasDiurnas()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getExtrasNocturnas()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getAguinaldo()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getComision()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getVacaciones()) + "");row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getTotalHaberes_()) + "");
			
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getPrestamos()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getAnticipo())+ "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getAdelantos()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getAnticipoAguinaldo()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getOtrosDescuentos()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getCorporativo()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getUniforme()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getRepuestos()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getAusencia()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getIps()) + ""); row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getTotalADescontar()) + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getRedondeo(p.getTotalACobrar()) + "");
		}
		listSheet.autoSizeColumn(0); listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2); listSheet.autoSizeColumn(3);
		listSheet.autoSizeColumn(4); listSheet.autoSizeColumn(5);
		listSheet.autoSizeColumn(6); listSheet.autoSizeColumn(7);
		listSheet.autoSizeColumn(8); listSheet.autoSizeColumn(9);
		listSheet.autoSizeColumn(10); listSheet.autoSizeColumn(11);
		listSheet.autoSizeColumn(12); listSheet.autoSizeColumn(13);
		listSheet.autoSizeColumn(14); listSheet.autoSizeColumn(15);
		listSheet.autoSizeColumn(16); listSheet.autoSizeColumn(17);
		listSheet.autoSizeColumn(18); listSheet.autoSizeColumn(19);
		listSheet.autoSizeColumn(20); listSheet.autoSizeColumn(21);
		listSheet.autoSizeColumn(22); listSheet.autoSizeColumn(23);
		listSheet.autoSizeColumn(24); listSheet.autoSizeColumn(25);
		listSheet.autoSizeColumn(26); listSheet.autoSizeColumn(27);
		listSheet.autoSizeColumn(28);
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("PlanillaSalarios.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	@NotifyChange("*")
	public void cerrarPlanilla() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (RRHHPlanillaSalarios pl : this.getPlanillas()) {
			pl.setCerrado(true);
			rr.saveObject(pl, this.getLoginNombre());
		}
		Clients.showNotification("PLANILLA CERRADA");
	}
	
	/**
	 * Despliega el Recibo comun..
	 */
	private void imprimirRecibo_() throws Exception {		
		String concepto = "anticipo de salarios";
		double importe = this.selectedPlanilla.getAnticipo();
		if (importe == 0) {
			importe = this.selectedPlanilla.getAdelantos();
			concepto = "anticipo de comisiones";
		}
		
		if (this.selectedPlanilla.getTipo().equals(RRHHPlanillaSalarios.TIPO_PREMIOS)) {
			concepto = "premios";
			importe = this.selectedPlanilla.getOtrosHaberes();
		}
		
		if (this.selectedPlanilla.getTipo().equals(RRHHPlanillaSalarios.TIPO_AGUINALDOS)) {
			importe = this.selectedPlanilla.getAnticipoAguinaldo();
			if (this.selectedPlanilla.getAnticipoAguinaldo2() > 0) {
				importe = this.selectedPlanilla.getAnticipoAguinaldo2();
			}
			if (this.selectedPlanilla.getAnticipoAguinaldo3() > 0) {
				importe = this.selectedPlanilla.getAnticipoAguinaldo3();
			}
			if (importe > 0) {
				concepto = "anticipo de aguinaldo";
			}
		}
		
		if (importe < 0) importe = importe * -1;
		
		String descripcion = "Pago de " + concepto + " correspondiente al mes de "
				+ this.selectedPlanilla.getMes().toLowerCase() + " " + this.selectedPlanilla.getAnho();
		String descripcionAnticipoAguinaldo = "Pago de " + concepto + " correspondiente al periodo "
				+ this.selectedPlanilla.getAnho();

		String source = ReportesViewModel.SOURCE_RECIBO_COMUN;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Fecha", Utiles.getDateToString(new Date(), "dd"));
		params.put("Funcionario", this.selectedPlanilla.getFuncionario());
		params.put("ImporteGs", Utiles.getNumberFormat(importe));
		params.put("ImporteLetras", m.numberToLetter(Utiles.getRedondeo(importe)));
		params.put("Concepto", concepto.equals("anticipo de aguinaldo") ? descripcionAnticipoAguinaldo : descripcion);
		params.put("Cargo", this.selectedPlanilla.getCargo());
		params.put("Periodo", "Asunción, " + Utiles.getDateToString(new Date(), "dd") + " de " +
				this.selectedPlanilla.getMes().toLowerCase() + " de " +  this.selectedPlanilla.getAnho());
		params.put("Usuario", getUs().getNombre());
		this.imprimirComprobante(source, params, null, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * Despliega el Reporte de liquidacion de salario..
	 */
	private void imprimirLiquidacion() throws Exception {		
		//String source = ReportesViewModel.SOURCE_LIQUIDACION_SALARIO;
		Map<String, Object> params = new HashMap<String, Object>();
		//JRDataSource dataSource = new LiquidacionSalarioDataSource(this.selectedPlanilla);
		params.put("Fecha", Utiles.getDateToString(this.selectedPlanilla.getModificado(), Utiles.DD_MM_YYYY));
		params.put("Funcionario", this.selectedPlanilla.getFuncionario());
		params.put("Cargo", this.selectedPlanilla.getCargo());
		params.put("Periodo", this.selectedPlanilla.getMes() + " " +  this.selectedPlanilla.getAnho());
		params.put("Usuario", getUs().getNombre());
		//this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
		this.win = (Window) Executions.createComponents(ZUL_IMPRESION_LIQUIDACION, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * Despliega el Reporte de planilla de salario..
	 */
	private void imprimirPlanilla_(Object[] formato) throws Exception {		
		String pdf = (String) formato[1];
		String source = pdf.equals(ReportesViewModel.FORMAT_PDF[1]) ? ReportesViewModel.SOURCE_PLANILLA_SALARIOS : ReportesViewModel.SOURCE_PLANILLA_SALARIOS_;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new PlanillaSalariosDataSource(this.planillas, this.getTotales());
		params.put("Periodo", this.getSelectedMes() + " " +  this.getSelectedAnho() + " - " + this.selectedTipo);
		params.put("Usuario", getUs().getNombre());
		params.put("Titulo", "Planilla de Salarios");
		this.imprimirComprobante(source, params, dataSource, formato);
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	private void imprimirComprobante(String source,
			Map<String, Object> parametros, JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);
		this.win = (Window) Executions.createComponents(ReportesViewModel.ZUL_REPORTES, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * DataSource del Servicio Tecnico..
	 */
	class LiquidacionSalarioDataSource implements JRDataSource {

		List<MyArray> dets = new ArrayList<MyArray>();
		double totalImporteGs = 0;

		public LiquidacionSalarioDataSource(RRHHPlanillaSalarios liquidacion) {
			this.totalImporteGs = liquidacion.getTotalACobrar();
			if (liquidacion.getSalarios() > 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.SALARIOS,
						Utiles.getNumberFormat(liquidacion.getSalarios()),
						Utiles.getNumberFormat(0.0)));
			}
			if (liquidacion.getComision() > 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.COMISION,
						Utiles.getNumberFormat(liquidacion.getComision()),
						Utiles.getNumberFormat(0.0)));
			}
			if (liquidacion.getBonificacion() > 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.BONIFICACION,
						Utiles.getNumberFormat(liquidacion.getBonificacion()),
						Utiles.getNumberFormat(0.0)));
			}
			if (liquidacion.getOtrosHaberes() > 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.OTROS_HABERES,
						Utiles.getNumberFormat(liquidacion.getOtrosHaberes()),
						Utiles.getNumberFormat(0.0)));
			}
			if (liquidacion.getHorasExtras() > 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.HORAS_EXTRAS,
						Utiles.getNumberFormat(liquidacion.getHorasExtras()),
						Utiles.getNumberFormat(0.0)));
			}
			if (liquidacion.getResponsabilidad() > 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.RESPONSABILIDAD,
						Utiles.getNumberFormat(liquidacion.getResponsabilidad()),
						Utiles.getNumberFormat(0.0)));
			}
			if (liquidacion.getVacaciones() > 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.VACACIONES,
						Utiles.getNumberFormat(liquidacion.getVacaciones()),
						Utiles.getNumberFormat(0.0)));
			}
			if (liquidacion.getAnticipo() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.ANTICIPO,						
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getAnticipo())));
			}
			if (liquidacion.getPrestamos() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.PRESTAMOS,
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getPrestamos())));
			}
			if (liquidacion.getAdelantos() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.ADELANTOS,
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getAdelantos())));
			}
			if (liquidacion.getOtrosDescuentos() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.OTROS_DESCUENTOS,						
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getOtrosDescuentos())));
			}
			if (liquidacion.getCorporativo() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.CORPORATIVO,						
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getCorporativo())));
			}
			if (liquidacion.getUniforme() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.UNIFORME,						
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getUniforme())));
			}
			if (liquidacion.getRepuestos() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.REPUESTOS,						
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getRepuestos())));
			}
			if (liquidacion.getSeguro() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.SEGURO,
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getSeguro())));
			}
			if (liquidacion.getEmbargo() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.EMBARGO,
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getEmbargo())));
			}
			if (liquidacion.getSeguroVehicular() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.SEGURO_VEHICULAR,
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getSeguroVehicular())));
			}
			if (liquidacion.getAusencia() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.AUSENCIA,
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getAusencia())));
			}
			if (liquidacion.getIps() < 0) {
				this.dets.add(new MyArray("  ", 
						RRHHPlanillaSalarios.IPS,
						Utiles.getNumberFormat(0.0),
						Utiles.getNumberFormat(liquidacion.getIps())));
			}
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			MyArray item = this.dets.get(index);
			if ("TituloDetalle".equals(fieldName)) {
				value = item.getPos1();
			} else if ("Descripcion".equals(fieldName)) {
				value = item.getPos2();
			} else if ("Haberes".equals(fieldName)) {
				value = item.getPos3();
			} else if ("Descuentos".equals(fieldName)) {
				value = item.getPos4();
			} else if ("TotalImporteGs".equals(fieldName)) {
				value = Utiles.getNumberFormat(this.totalImporteGs);
			} else if ("ImporteLetras".equals(fieldName)) {
				double importe = this.totalImporteGs > 0 ? this.totalImporteGs : 0.0;
				value = m.numberToLetter(importe);
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < dets.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}
	
	/**
	 * DataSource de planilla de salarios..
	 */
	class PlanillaSalariosDataSource implements JRDataSource {

		List<RRHHPlanillaSalarios> salarios;
		double totalImporteGs = 0;
		Object[] totales;

		public PlanillaSalariosDataSource(List<RRHHPlanillaSalarios> salarios, Object[] totales) {
			this.salarios = salarios;
			this.totales = totales;
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			RRHHPlanillaSalarios item = this.salarios.get(index);
			if ("Funcionario".equals(fieldName)) {
				value = item.getFuncionario();
			} else if ("Acobrar".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getTotalACobrar());
			} else if ("Descuentos".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getTotalADescontar());
			} else if ("Salarios".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getSalarios());
			} else if ("Comision".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getComision());
			} else if ("Anticipo".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAnticipo());
			} else if ("Bonificacion".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getBonificacion());
			} else if ("Aguinaldo".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAguinaldo());
			} else if ("Otroshaberes".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getOtrosHaberes());
			} else if ("Prestamos".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getPrestamos());
			} else if ("Adelantos".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAdelantos());
			} else if ("AnticipoAguinaldo".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAnticipoAguinaldo());
			} else if ("Otrosdtos".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getOtrosDescuentos());
			} else if ("Corporativo".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getCorporativo());
			} else if ("Uniforme".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getUniforme());
			} else if ("Repuestos".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getRepuestos());
			} else if ("Seguro".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getSeguro());
			} else if ("Embargo".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getEmbargo());
			} else if ("Ips".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getIps());
			} else if ("DiasTrabajados".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getDiasTrabajados());
			} else if ("CantHorasExtras".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getCantidadHorasExtras());
			} else if ("Responsabilidad".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getResponsabilidad());
			} else if ("Vacaciones".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getVacaciones());
			} else if ("TotalHaberes".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getTotalHaberes_());
			} else if ("Ausencia".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAusencia());
			} else if ("SeguroVehicular".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getSeguroVehicular());
			}  else if ("HorasExtras".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getHorasExtras());
			} else if ("Acobrar_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[12]);
			} else if ("Descuentos_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[13]);
			} else if ("Salarios_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[0]);
			} else if ("Comision_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[1]);
			} else if ("Anticipo_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[2]);
			} else if ("Bonificacion_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[3]);
			} else if ("Aguinaldo_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[22]);
			} else if ("Otroshaberes_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[4]);
			} else if ("Prestamos_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[14]);
			} else if ("Adelantos_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[15]);
			}  else if ("AnticipoAguinaldo_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[23]);
			} else if ("Otrosdtos_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[5]);
			} else if ("Corporativo_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[6]);
			} else if ("Uniforme_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[7]);
			} else if ("Repuestos_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[8]);
			} else if ("Seguro_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[9]);
			} else if ("Embargo_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[10]);
			} else if ("Ips_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[11]);
			} else if ("Responsabilidad_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[17]);
			}  else if ("Vacaciones_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[18]);
			} else if ("TotalHaberes_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[21]);
			} else if ("SeguroVehicular_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[19]);
			} else if ("Ausencia_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[20]);
			} else if ("HorasExtras_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[16]);
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < salarios.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}
	
	/**
	 * GETS / SETS 
	 */
	public Object[] getTotales() {
		Object[] out = new Object[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		if (this.planillas != null) {
			for (RRHHPlanillaSalarios item : this.planillas) {
				double sal = (double) out[0]; double com = (double) out[1];			
				double ant = (double) out[2]; double bon = (double) out[3];			
				double hab = (double) out[4]; double dto = (double) out[5];			
				double cor = (double) out[6]; double uni = (double) out[7];			
				double rep = (double) out[8]; double seg = (double) out[9];			
				double emb = (double) out[10]; double ips = (double) out[11];
				double tco = (double) out[12]; double tde = (double) out[13];
				double pre = (double) out[14]; double ade = (double) out[15];
				double hex = (double) out[16]; double res = (double) out[17];
				double vac = (double) out[18]; double sev = (double) out[19];
				double aus = (double) out[20]; double tha = (double) out[21];
				double agu = (double) out[22]; double ang = (double) out[23];
				double hed = (double) out[24]; double hen = (double) out[25];
				double an2 = (double) out[26]; double an3 = (double) out[27];
				sal += item.getSalarioFinal(); com += item.getComision();
				ant += item.getAnticipo(); bon += item.getBonificacion();
				hab += item.getOtrosHaberes(); dto += item.getOtrosDescuentos();
				cor += item.getCorporativo(); uni += item.getUniforme();
				rep += item.getRepuestos(); seg += item.getSeguro();
				emb += item.getEmbargo(); ips += item.getIps();
				tco += item.getTotalACobrar(); tde += item.getTotalADescontar();
				pre += item.getPrestamos(); ade += item.getAdelantos();
				hex += item.getHorasExtras(); res += item.getResponsabilidad();
				vac += item.getVacaciones(); sev += item.getSeguroVehicular();
				aus += item.getAusencia(); tha += item.getTotalHaberes_();
				agu += item.getAguinaldo(); ang += item.getAnticipoAguinaldo();
				hed += item.getExtrasDiurnas(); hen += item.getExtrasNocturnas();
				an2 += item.getAnticipoAguinaldo2(); an3 += item.getAnticipoAguinaldo3();
				out = new Object[] { sal, com, ant, bon, hab, dto, cor, uni, rep, seg, emb, ips, tco, tde, pre, ade,
						hex, res, vac, sev, aus, tha, agu, ang, hed, hen, an2, an3 };
			}
		}
		return out;
	}
	
	@DependsOn("selectedPlanilla")
	public List<MyArray> getDetalles() {
		RRHHPlanillaSalarios liquidacion = this.selectedPlanilla;
		List<MyArray> dets = new ArrayList<MyArray>();
		
		if (liquidacion.getSalarioFinal() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.SALARIOS, Utiles.getNumberFormat(liquidacion.getSalarioFinal() - liquidacion.getResponsabilidad()),
					Utiles.getNumberFormat(0.0)));
		}
		if (liquidacion.getResponsabilidad() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.RESPONSABILIDAD, Utiles.getNumberFormat(liquidacion.getResponsabilidad()),
					Utiles.getNumberFormat(0.0)));
		}
		if (liquidacion.getBonificacion() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.BONIFICACION,
					Utiles.getNumberFormat(liquidacion.getBonificacion()), Utiles.getNumberFormat(0.0)));
		}
		if (liquidacion.getOtrosHaberes() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.OTROS_HABERES,
					Utiles.getNumberFormat(liquidacion.getOtrosHaberes()), Utiles.getNumberFormat(0.0)));
		}
		if ((liquidacion.getExtrasDiurnas() + liquidacion.getExtrasNocturnas()) > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.HORAS_EXTRAS,
					Utiles.getNumberFormat(liquidacion.getExtrasDiurnas() + liquidacion.getExtrasNocturnas()), Utiles.getNumberFormat(0.0)));
		}		
		if (liquidacion.getComision() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.COMISION, Utiles.getNumberFormat(liquidacion.getComision()),
					Utiles.getNumberFormat(0.0)));
		}		
		if (liquidacion.getVacaciones() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.VACACIONES,
					Utiles.getNumberFormat(liquidacion.getVacaciones()), Utiles.getNumberFormat(0.0)));
		}
		if (liquidacion.getAguinaldo() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.AGUINALDO, Utiles.getNumberFormat(liquidacion.getAguinaldo()),
					Utiles.getNumberFormat(0.0)));
		}
		
		// DESCUENTOS
		
		if (liquidacion.getSeguroVehicular() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.SEGURO_VEHICULAR, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getSeguroVehicular())));
		}
		if (liquidacion.getPrestamos() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.PRESTAMOS, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getPrestamos())));
		}		
		if (liquidacion.getAnticipo() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.ANTICIPO, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getAnticipo())));
		}
		if (liquidacion.getAdelantos() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.ADELANTOS, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getAdelantos())));
		}
		if (liquidacion.getOtrosDescuentos() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.OTROS_DESCUENTOS, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getOtrosDescuentos())));
		}
		if (liquidacion.getCorporativo() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.CORPORATIVO, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getCorporativo())));
		}
		if (liquidacion.getUniforme() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.UNIFORME, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getUniforme())));
		}
		if (liquidacion.getRepuestos() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.REPUESTOS, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getRepuestos())));
		}
		if (liquidacion.getSeguro() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.SEGURO, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getSeguro())));
		}
		if (liquidacion.getEmbargo() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.EMBARGO, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getEmbargo())));
		}
		if (liquidacion.getAusencia() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.AUSENCIA, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getAusencia())));
		}		
		if (liquidacion.getIps() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.IPS, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getIps())));
		}
		if (liquidacion.getAnticipoAguinaldo() < 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.ANTICIPO_AGUINALDO, Utiles.getNumberFormat(0.0),
					Utiles.getNumberFormat(liquidacion.getAnticipoAguinaldo())));
		}
		return dets;
	}	
	
	/**
	 * @return la fecha actual
	 */
	public String getFecha() {
		return Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY);
	}
	
	/**
	 * @return los tipos de salarios..
	 */
	@DependsOn("selectedMes")
	public List<String> getTipos() {
		List<String> out = new ArrayList<>();
		out.add(RRHHPlanillaSalarios.TIPO_COMISIONES);
		out.add(RRHHPlanillaSalarios.TIPO_SALARIOS);
		out.add(RRHHPlanillaSalarios.TIPO_PREMIOS);
		if (this.selectedMes.equals("DICIEMBRE")) {
			out.add(RRHHPlanillaSalarios.TIPO_AGUINALDOS);
		}
		return out;
	}
	
	/**
	 * @return los formatos
	 */
	public List<Object[]> getFormatos() {
		List<Object[]> out = new ArrayList<>();
		out.add(ReportesViewModel.FORMAT_PDF);
		out.add(ReportesViewModel.FORMAT_XLS);
		return out;
	}
	
	/**
	 * @return las planillas..
	 */
	public List<RRHHPlanillaSalarios> getPlanillas_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<RRHHPlanillaSalarios> out = rr.getPlanillaSalarios(this.selectedMes, this.selectedAnho, this.selectedTipo);
		return out;
	}
	
	/**
	 * @return el tipo de liquidacion..
	 */
	public String getTipoLiquidacion() {
		String out = "LIQUIDACIÓN DE SALARIOS";
		if (this.selectedTipo.equals(RRHHPlanillaSalarios.TIPO_AGUINALDOS)) {
			out = "LIQUIDACIÓN DE AGUINALDOS";
		}
		if (this.selectedTipo.equals(RRHHPlanillaSalarios.TIPO_COMISIONES)) {
			out = "LIQUIDACIÓN DE COMISIONES";
		}
		return out;
	}
	
	/**
	 * @return el articulo..
	 */
	public String getDescripcionArticulo() {
		return this.selectedTipo.equals(RRHHPlanillaSalarios.TIPO_AGUINALDOS)
				? "Conforme al artículo 243 del código del trabajador"
				: "Conforme al artículo 235 del código del trabajador";
	}
	
	public List<Object[]> getFuncionarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionarios_();
	}
	
	@DependsOn("planillas")
	public List<Object[]> getFuncionarios_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Map<String, String> cis = new HashMap<String, String>();
		for (RRHHPlanillaSalarios pl : this.getPlanillas()) {
			cis.put(pl.getCedula(), pl.getCedula());
		}
		List<Object[]> list = rr.getFuncionarios_();
		List<Object[]> out = new ArrayList<Object[]>();
		for (Object[] l : list) {
			String ci = (String) l[2];
			if (cis.get(ci) == null) {
				out.add(l);
			}
		}
		return out;
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

	public List<RRHHPlanillaSalarios> getPlanillas() {
		return planillas;
	}

	public void setPlanillas(List<RRHHPlanillaSalarios> planillas) {
		this.planillas = planillas;
	}

	public RRHHPlanillaSalarios getSelectedPlanilla() {
		return selectedPlanilla;
	}

	public void setSelectedPlanilla(RRHHPlanillaSalarios selectedPlanilla) {
		this.selectedPlanilla = selectedPlanilla;
	}

	public String getSelectedTipo() {
		return selectedTipo;
	}

	public void setSelectedTipo(String selectedTipo) {
		this.selectedTipo = selectedTipo;
	}

	public Object[] getSelectedFormato() {
		return selectedFormato;
	}

	public void setSelectedFormato(Object[] selectedFormato) {
		this.selectedFormato = selectedFormato;
	}

	public List<Object[]> getSelectedFuncionarios_() {
		return selectedFuncionarios_;
	}

	public void setSelectedFuncionarios_(List<Object[]> selectedFuncionarios_) {
		this.selectedFuncionarios_ = selectedFuncionarios_;
	}
}
