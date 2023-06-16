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
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.FuncionarioDescuento;
import com.yhaguy.domain.RRHHLiquidacionSalario;
import com.yhaguy.domain.RRHHPlanillaSalarios;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class LiquidacionSalariosViewModel extends SimpleViewModel {

	private String filterRazonSocial = "";
	
	private RRHHLiquidacionSalario n_liquidacion;	
	private RRHHLiquidacionSalario selectedLiquidacion;
	
	private Window win;
	
	@Wire
	private Textbox tx_concepto;
	
	@Init(superclass = true)
	public void init() {
		this.inicializarDatos();
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void imprimir(@BindingParam("liquidacion") RRHHLiquidacionSalario liquidacion) throws Exception {
		this.imprimirLiquidacion(liquidacion);
	}
	
	
	@Command
	@NotifyChange("n_liquidacion")
	public void selectFuncionario() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<RRHHLiquidacionSalario> list = rr.getLiquidaciones(this.n_liquidacion.getFuncionario().getId());
		if (list.size() > 0) {
			this.n_liquidacion.setFuncionario(null);
			Clients.showNotification("YA EXISTE UNA LIQUIDACIÓN PARA EL FUNCIONARIO SELECCIONADO", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		this.n_liquidacion.setFechaIngreso(this.n_liquidacion.getFuncionario().getEmpresa().getFechaIngreso());
		this.n_liquidacion.setSalario(this.n_liquidacion.getFuncionario().getIngresosVigente());
		this.n_liquidacion.setJornalDiario(this.getPromedioIngresos() / 30);
		this.n_liquidacion.setDiasTrabajados(Integer.parseInt(Utiles.getDateToString(new Date(), "dd")));
		this.n_liquidacion.setAguinaldo(this.getAguinaldoProporcional());
		this.n_liquidacion.setOtrosDescuentos(this.getTotalOtrosDescuentos());
	}
	
	@Command
	@NotifyChange("*")
	public void confirmar() {
		if (!this.validar()) {
			return;
		}
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			rr.saveObject(this.n_liquidacion, this.getLoginNombre());
			
			String anho = Utiles.getDateToString(new Date(), "yyyy");
			String tipo = RRHHPlanillaSalarios.TIPO_SALARIOS;
			Funcionario func = this.n_liquidacion.getFuncionario();
			
			List<RRHHPlanillaSalarios> list = rr.getPlanillaSalariosByCedula(anho, tipo, func.getCedula());
			if (list.size() > 0) {
				rr.deleteObject(list.get(0));
			}
			
			RRHHPlanillaSalarios sal = new RRHHPlanillaSalarios();
			sal.setTipo(tipo);
			sal.setAnho(anho);
			sal.setMes((String) Utiles.getMesCorriente(anho).getPos1());
			sal.setFuncionario(func.getApellidos() + ", " + func.getNombres());
			sal.setCedula(func.getCedula());
			sal.setCargo(func.getFuncionarioCargo().getDescripcion());
			sal.setDiasTrabajados(this.n_liquidacion.getDiasTrabajados());
			sal.setSalarios(this.n_liquidacion.getJornalDiario() * 30);
			sal.setAguinaldo(this.n_liquidacion.getAguinaldo());
			sal.setIndemnizacion(this.n_liquidacion.getHaberesIndemnizacion());
			sal.setPreaviso(this.n_liquidacion.getHaberesPreAviso());
			sal.setVacaciones(this.n_liquidacion.getHaberesVacacionesCausadas() + this.n_liquidacion.getHaberesVacacionesProporcional());
			sal.setOtrosDescuentos(this.n_liquidacion.getOtrosDescuentos());
			rr.saveObject(sal, this.getLoginNombre());
			
			this.imprimir(this.n_liquidacion);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * @return validacion..
	 */
	private boolean validar() {
		boolean out = true;
		
		if (this.n_liquidacion.getMotivo() == null || this.n_liquidacion.getMotivo().isEmpty()) {
			Clients.showNotification("DEBE ASIGNAR EL MOTIVO", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return false;
		}
		
		return out;
	}
	
	/**
	 * inicializacion
	 */
	private void inicializarDatos() {
		this.n_liquidacion = new RRHHLiquidacionSalario();
		this.n_liquidacion.setFecha(new Date());
	}
	
	/**
	 * Despliega el Reporte de Orden de Servicio Tecnico..
	 */
	private void imprimirLiquidacion(RRHHLiquidacionSalario liquidacion) throws Exception {		
		
		String source = ReportesViewModel.SOURCE_LIQUIDACION_SALARIO;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new LiquidacionSalarioDataSource(liquidacion);
		params.put("Fecha", Utiles.getDateToString(liquidacion.getFecha(), Utiles.DD_MM_YYYY));
		params.put("Ingreso", Utiles.getDateToString(liquidacion.getFechaIngreso(), Utiles.DD_MM_YYYY));
		params.put("Funcionario", liquidacion.getFuncionario().getRazonSocial());
		params.put("Antiguedad", liquidacion.getAntiguedad() + " meses y " + liquidacion.getAntiguedadDias() + " días");
		params.put("SalarioPromedio", Utiles.getNumberFormat(this.getPromedioIngresos()));
		params.put("JornalPromedio", Utiles.getNumberFormat(liquidacion.getJornalDiario()));
		params.put("DiasPreAviso", Utiles.getNumberFormat(liquidacion.getDiasPreAviso()));
		params.put("DiasIndemnizacion", Utiles.getNumberFormat(liquidacion.getDiasIndemnizacion()));
		params.put("HaberesPreAviso", Utiles.getNumberFormat(liquidacion.getHaberesPreAviso()));
		params.put("HaberesIndemnizacion", Utiles.getNumberFormat(liquidacion.getHaberesIndemnizacion()));
		params.put("DiasTrabajados", Utiles.getNumberFormat(liquidacion.getDiasTrabajados()));
		params.put("HaberesDiasTrabajados", Utiles.getNumberFormat(liquidacion.getHaberesDiasTrabajados()));
		params.put("DiasVacacionesCausadas", Utiles.getNumberFormat(liquidacion.getVacacionesCausadas()));
		params.put("HaberesVacacionesCausadas", Utiles.getNumberFormat(liquidacion.getHaberesVacacionesCausadas()));
		params.put("DiasVacacionesProporcional", Utiles.getNumberFormat(liquidacion.getVacacionesProporcionales()));
		params.put("HaberesVacacionesProporcional", Utiles.getNumberFormat(liquidacion.getHaberesVacacionesProporcional()));
		params.put("Aguinaldo", Utiles.getNumberFormat(liquidacion.getAguinaldo()));
		params.put("SubTotales", Utiles.getNumberFormat(liquidacion.getTotalHaberes() + liquidacion.getAguinaldo()));
		params.put("Ips", "-" + Utiles.getNumberFormat(liquidacion.getIps()));
		params.put("OtrosDescuentos",  "-" + Utiles.getNumberFormat(liquidacion.getOtrosDescuentos()));
		params.put("TotalACobrar", Utiles.getNumberFormat(liquidacion.getTotalACobrar()));
		params.put("Empresa", Configuracion.empresa);
		params.put("ImporteLetras", m.numberToLetter(Utiles.getRedondeo(liquidacion.getTotalACobrar())));
		params.put("Cedula", "C.I. " + liquidacion.getFuncionario().getCedula());
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

		this.win = (Window) Executions.createComponents(
				ReportesViewModel.ZUL_REPORTES, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * DataSource del Servicio Tecnico..
	 */
	class LiquidacionSalarioDataSource implements JRDataSource {

		List<MyArray> dets = new ArrayList<MyArray>();
		double totalImporteGs = 0;

		public LiquidacionSalarioDataSource(RRHHLiquidacionSalario liquidacion) {
			this.totalImporteGs = 0.0;
			this.dets.add(new MyArray());
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
				value = m.numberToLetter(this.totalImporteGs);
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
	 * GETS / SETS
	 */
	
	/**
	 * @return los funcionarios activos..
	 */
	public List<Funcionario> getFuncionarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionariosActivos();
	}
	
	public List<RRHHLiquidacionSalario> getLiquidaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getLiquidaciones();
	}
	
	/**
	 * @return los motivos..
	 */
	public List<String> getMotivos() {
		return RRHHLiquidacionSalario.getMotivos();
	}
	
	/**
	 * @return los salarios..
	 */
	@DependsOn("n_liquidacion")
	public List<RRHHPlanillaSalarios> getSalarios() throws Exception {
		if (this.n_liquidacion == null || this.n_liquidacion.getFuncionario() == null) {
			return new ArrayList<RRHHPlanillaSalarios>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getPlanillaSalariosByCedula(Utiles.getDateToString(new Date(), "yyyy"),
				RRHHPlanillaSalarios.TIPO_SALARIOS, this.n_liquidacion.getFuncionario().getCedula());
	}
	
	/**
	 * @return ultimos seis salarios..
	 */
	private List<RRHHPlanillaSalarios> getUltimosSalarios() throws Exception {
		if (this.n_liquidacion == null || this.n_liquidacion.getFuncionario() == null) {
			return new ArrayList<RRHHPlanillaSalarios>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getPlanillaSalariosByCedulaUltimosSeis(RRHHPlanillaSalarios.TIPO_SALARIOS, this.n_liquidacion.getFuncionario().getCedula());
	}
	
	@DependsOn("n_liquidacion")
	public double getTotalSalarios() {
		double out = 0.0;
		try {
			for (RRHHPlanillaSalarios salario : this.getSalarios()) {
				out += salario.getSalarioFinal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	@DependsOn("n_liquidacion")
	public double getTotalSalariosU6() {
		double out = 0.0;
		try {
			for (RRHHPlanillaSalarios salario : this.getUltimosSalarios()) {
				out += salario.getSalarioFinal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	@DependsOn("n_liquidacion")
	public double getTotalOtrosHaberes() {
		double out = 0.0;
		try {
			for (RRHHPlanillaSalarios salario : this.getSalarios()) {
				out += salario.getOtrosHaberes();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	@DependsOn("n_liquidacion")
	public double getTotalOtrosHaberesU6() {
		double out = 0.0;
		try {
			for (RRHHPlanillaSalarios salario : this.getUltimosSalarios()) {
				out += salario.getOtrosHaberes();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	@DependsOn("n_liquidacion")
	public double getTotalComisiones() {
		double out = 0.0;
		try {
			for (RRHHPlanillaSalarios salario : this.getSalarios()) {
				out += salario.getComisiones();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	@DependsOn("n_liquidacion")
	public double getTotalComisionesU6() {
		double out = 0.0;
		try {
			for (RRHHPlanillaSalarios salario : this.getUltimosSalarios()) {
				out += salario.getComisiones();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * @return total otros descuentos
	 */
	private double getTotalOtrosDescuentos() {
		double out = 0.0;
		try {
			Funcionario f = this.n_liquidacion.getFuncionario();
			if (f != null) {
				for (FuncionarioDescuento desc : f.getDescuentos()) {
					if (desc.getSaldoCuotas() > 0) {
						out += desc.getImporteGs() * desc.getSaldoCuotas();						
					}		
					if (desc.getCuotas() == 0 || desc.getSaldoCuotas() > 0) {
						out += desc.getImporteGs();						
					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	@DependsOn("n_liquidacion")
	public double getTotalIngresos() {
		return this.getTotalSalarios() + this.getTotalOtrosHaberes() + this.getTotalComisiones();
	}
	
	@DependsOn("n_liquidacion")
	public double getTotalIngresosU6() {
		return this.getTotalSalariosU6() + this.getTotalOtrosHaberesU6() + this.getTotalComisionesU6();
	}
	
	@DependsOn("n_liquidacion")
	public double getPromedioIngresos() throws Exception {
		int size = this.getUltimosSalarios().size();
		int divisor = size < 6 ? size : 6;
		return this.getTotalIngresosU6() / divisor;
	}
	
	@DependsOn("n_liquidacion")
	public double getAguinaldoProporcional() {
		double out = this.getTotalIngresos();
		if (out > 0) {
			return out / 12;
		}
		return 0.0;
	}
	
	public RRHHLiquidacionSalario getN_liquidacion() {
		return n_liquidacion;
	}

	public void setN_liquidacion(RRHHLiquidacionSalario n_liquidacion) {
		this.n_liquidacion = n_liquidacion;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public RRHHLiquidacionSalario getSelectedLiquidacion() {
		return selectedLiquidacion;
	}

	public void setSelectedLiquidacion(RRHHLiquidacionSalario selectedLiquidacion) {
		this.selectedLiquidacion = selectedLiquidacion;
	}
}
