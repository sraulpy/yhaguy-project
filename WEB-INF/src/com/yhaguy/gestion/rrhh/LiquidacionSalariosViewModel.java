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
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RRHHLiquidacionSalario;
import com.yhaguy.domain.RRHHLiquidacionSalarioDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class LiquidacionSalariosViewModel extends SimpleViewModel {

	private String filterRazonSocial = "";
	
	private RRHHLiquidacionSalario n_liquidacion;
	private RRHHLiquidacionSalarioDetalle n_detalle;
	
	private Object[] selectedLiquidacion;
	
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
	public void imprimir() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		RRHHLiquidacionSalario liquidacion = (RRHHLiquidacionSalario) rr.getObject(RRHHLiquidacionSalario.class.getName(), (long) this.selectedLiquidacion[0]);
		this.imprimirLiquidacion(liquidacion);
	}
	
	@Command
	@NotifyChange("*")
	public void addDetalle() {
		this.n_liquidacion.getDetalles().add(this.n_detalle);
		this.n_detalle = new RRHHLiquidacionSalarioDetalle();
		this.tx_concepto.focus();
	}
	
	@Command
	@NotifyChange("*")
	public void guardarLiquidacion(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.n_liquidacion.setImporteGs(this.n_liquidacion.getImporteGs_());
		rr.saveObject(this.n_liquidacion, this.getLoginNombre());
		this.imprimirLiquidacion(this.n_liquidacion);
		comp.close();
		this.inicializarDatos();
	}
	
	/**
	 * inicializacion
	 */
	private void inicializarDatos() {
		this.n_liquidacion = new RRHHLiquidacionSalario();
		this.n_liquidacion.setFecha(new Date());
		this.n_detalle = new RRHHLiquidacionSalarioDetalle();
	}
	
	/**
	 * Despliega el Reporte de Orden de Servicio Tecnico..
	 */
	private void imprimirLiquidacion(RRHHLiquidacionSalario liquidacion) throws Exception {		
		String source = ReportesViewModel.SOURCE_LIQUIDACION_SALARIO;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new LiquidacionSalarioDataSource(liquidacion);
		params.put("Fecha", Utiles.getDateToString(liquidacion.getFecha(), Utiles.DD_MM_YYYY));
		params.put("Funcionario", liquidacion.getFuncionario().getRazonSocial());
		params.put("Cargo", liquidacion.getCargo().toUpperCase());
		params.put("Periodo", Utiles.getNombreMes(liquidacion.getFecha()) + " " +  Utiles.getDateToString(liquidacion.getFecha(), "yyyy"));
		params.put("Usuario", getUs().getNombre());
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

		List<RRHHLiquidacionSalarioDetalle> detalle = new ArrayList<RRHHLiquidacionSalarioDetalle>();
		List<MyArray> dets = new ArrayList<MyArray>();
		double totalImporteGs = 0;

		public LiquidacionSalarioDataSource(RRHHLiquidacionSalario liquidacion) {
			this.totalImporteGs = liquidacion.getImporteGs_();
			for (RRHHLiquidacionSalarioDetalle item : liquidacion.getDetalles()) {
				this.dets.add(new MyArray("  ", 
						item.getConcepto().toUpperCase(),
						Utiles.getNumberFormat(item.getHaberes()),
						Utiles.getNumberFormat(item.getDescuentos())));
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
	
	@DependsOn("filterRazonSocial")
	public List<Funcionario> getFuncionarios() throws Exception {
		if (this.filterRazonSocial.trim().isEmpty()) {
			return new ArrayList<Funcionario>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionarios(this.filterRazonSocial);
	}
	
	public List<Object[]> getLiquidaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getLiquidaciones();
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

	public RRHHLiquidacionSalarioDetalle getN_detalle() {
		return n_detalle;
	}

	public void setN_detalle(RRHHLiquidacionSalarioDetalle n_detalle) {
		this.n_detalle = n_detalle;
	}

	public Object[] getSelectedLiquidacion() {
		return selectedLiquidacion;
	}

	public void setSelectedLiquidacion(Object[] selectedLiquidacion) {
		this.selectedLiquidacion = selectedLiquidacion;
	}
}
