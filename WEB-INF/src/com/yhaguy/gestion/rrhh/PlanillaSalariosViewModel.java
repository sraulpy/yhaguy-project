package com.yhaguy.gestion.rrhh;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
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
	
	private RRHHPlanillaSalarios selectedPlanilla;
	
	private List<Object[]> selectedFuncionarios;
	private List<RRHHPlanillaSalarios> planillas;
	
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
			RRHHPlanillaSalarios pl = new RRHHPlanillaSalarios();
			pl.setMes(this.selectedMes);
			pl.setAnho(this.selectedAnho);
			pl.setTipo(this.selectedTipo);
			pl.setFuncionario((String) func[1]);
			pl.setCedula((String) func[2]);
			pl.setCargo((String) func[3]);
			rr.saveObject(pl, this.getLoginNombre());
			comp.close();
		}
		this.planillas = this.getPlanillas_();
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
		this.imprimirLiquidacion();
	}
	
	@Command
	public void imprimirPlanilla() throws Exception {
		this.imprimirPlanilla_();
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
	private void imprimirPlanilla_() throws Exception {		
		String source = ReportesViewModel.SOURCE_PLANILLA_SALARIOS;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new PlanillaSalariosDataSource(this.planillas, this.getTotales());
		params.put("Periodo", this.getSelectedMes() + " " +  this.getSelectedAnho() + " - " + this.selectedTipo);
		params.put("Usuario", getUs().getNombre());
		params.put("Titulo", "Planilla de Salarios");
		this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
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
			} else if ("Otroshaberes".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getOtrosHaberes());
			} else if ("Prestamos".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getPrestamos());
			} else if ("Adelantos".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getAdelantos());
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
			} else if ("Otroshaberes_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[4]);
			} else if ("Prestamos_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[14]);
			} else if ("Adelantos_t".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) this.totales[15]);
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
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
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
				sal += item.getSalarios(); com += item.getComision();
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
				out = new Object[] { sal, com, ant, bon, hab, dto, cor, uni, rep, seg, emb, ips, tco, tde, pre, ade,
						hex, res, vac, sev, aus, tha, agu, ang };
			}
		}
		return out;
	}
	
	@DependsOn("selectedPlanilla")
	public List<MyArray> getDetalles() {
		RRHHPlanillaSalarios liquidacion = this.selectedPlanilla;
		List<MyArray> dets = new ArrayList<MyArray>();
		
		if (liquidacion.getSalarios() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.SALARIOS, Utiles.getNumberFormat(liquidacion.getSalarios()),
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
		if (liquidacion.getHorasExtras() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.HORAS_EXTRAS,
					Utiles.getNumberFormat(liquidacion.getHorasExtras()), Utiles.getNumberFormat(0.0)));
		}
		if (liquidacion.getResponsabilidad() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.RESPONSABILIDAD,
					Utiles.getNumberFormat(liquidacion.getResponsabilidad()), Utiles.getNumberFormat(0.0)));
		}
		if (liquidacion.getAdelantos() > 0) {
			dets.add(new MyArray("  ", RRHHPlanillaSalarios.ADELANTOS, Utiles.getNumberFormat(liquidacion.getAdelantos()),
					Utiles.getNumberFormat(0.0)));
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
	public List<String> getTipos() {
		List<String> out = new ArrayList<>();
		out.add(RRHHPlanillaSalarios.TIPO_COMISIONES);
		out.add(RRHHPlanillaSalarios.TIPO_SALARIOS);
		out.add(RRHHPlanillaSalarios.TIPO_PREMIOS);
		out.add(RRHHPlanillaSalarios.TIPO_AGUINALDOS);
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
}
