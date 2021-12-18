package com.yhaguy.gestion.rrhh;

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
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RRHHPermiso;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class PermisosViewModel extends SimpleViewModel {
	
	private RRHHPermiso nvo_permiso;
	private RRHHPermiso selected_permiso;
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private Window win;

	@Init(superclass = true)
	public void init() {
		try {
			this.inicializarDatos();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addPermiso(@BindingParam("comp") Popup comp) throws Exception {
		if(!this.isDatosValidos()) {
			Clients.showNotification("NO SE PUDO GUARDAR, VERIFIQUE LOS DATOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvo_permiso.setCargo(this.nvo_permiso.getCargo().toUpperCase());
		this.nvo_permiso.setMotivo(this.nvo_permiso.getMotivo().toUpperCase());
		rr.saveObject(this.nvo_permiso, this.getLoginNombre());
		comp.close();
		
		this.imprimirPermiso(this.nvo_permiso);
		
		this.inicializarDatos();
	}
	
	@Command
	@NotifyChange("*")
	public void aprobar() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selected_permiso.setAprobado(true);
		rr.saveObject(this.selected_permiso, this.getLoginNombre());
		Clients.showNotification("PERMISO APROBADO..");
	}
	
	@Command
	public void imprimir() throws Exception {
		this.imprimirPermiso(this.selected_permiso);
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean isDatosValidos() {
		boolean out = true;
		if (this.nvo_permiso.getSupervisor() == null) {
			out = false;
		}
		if (this.nvo_permiso.getSalida() == null) {
			out = false;
		}
		if (this.nvo_permiso.getRegreso() == null) {
			out = false;
		}
		if (this.nvo_permiso.getCargo() == null || this.nvo_permiso.getCargo().trim().isEmpty()) {
			out = false;
		}
		if (this.nvo_permiso.getMotivo() == null || this.nvo_permiso.getMotivo().trim().isEmpty()) {
			out = false;
		}
		return out; 
	}
	
	/**
	 * inicializar datos..
	 */
	private void inicializarDatos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvo_permiso = new RRHHPermiso();
		this.nvo_permiso.setFecha(new Date());
		this.nvo_permiso.setSalida(new Date());
		this.nvo_permiso.setRegreso(new Date());
		this.nvo_permiso.setFuncionario(rr.getFuncionario(this.getAcceso().getFuncionario().getId()));
	}
	
	/**
	 * Despliega el Reporte de Permiso..
	 */
	private void imprimirPermiso(RRHHPermiso permiso) throws Exception {		
		String source = ReportesViewModel.SOURCE_PERMISO;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new PermisoDataSource(permiso);
		params.put("title", "SOLICITUD DE PERMISO");
		params.put("Fecha", Utiles.getDateToString(permiso.getFecha(), Utiles.DD_MM_YYYY));
		params.put("Usuario", getUs().getNombre());
		this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	public void imprimirComprobante(String source,
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
	 * DataSource del Acuse..
	 */
	class PermisoDataSource implements JRDataSource {

		RRHHPermiso permiso;

		public PermisoDataSource(RRHHPermiso permiso) {
			this.permiso = permiso;
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			if ("Funcionario".equals(fieldName)) {
				value = this.permiso.getFuncionario().getRazonSocial();
			}
			if ("Supervisor".equals(fieldName)) {
				value = this.permiso.getSupervisor().getRazonSocial();
			}
			if ("Salida".equals(fieldName)) {
				value = Utiles.getDateToString(this.permiso.getSalida(), "dd-MM-yyyy HH:mm");
			}
			if ("Regreso".equals(fieldName)) {
				value = Utiles.getDateToString(this.permiso.getRegreso(), "dd-MM-yyyy HH:mm");
			}
			if ("Motivo".equals(fieldName)) {
				value = this.permiso.getMotivo();
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < 0) {
				index++;
				return true;
			}
			return false;
		}
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<RRHHPermiso> getPermisos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getRRHHpermisos(this.getFilterFecha());
	}
	
	/**
	 * @return los funcionarios..
	 */
	public List<Funcionario> getFuncionarios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionarios();
	}
	
	private AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaAA.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaAA.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaAA;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaAA;
		return this.filterFechaAA + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
	}

	public RRHHPermiso getNvo_permiso() {
		return nvo_permiso;
	}

	public void setNvo_permiso(RRHHPermiso nvo_permiso) {
		this.nvo_permiso = nvo_permiso;
	}

	public RRHHPermiso getSelected_permiso() {
		return selected_permiso;
	}

	public void setSelected_permiso(RRHHPermiso selected_permiso) {
		this.selected_permiso = selected_permiso;
	}

	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFechaDD) {
		this.filterFechaDD = filterFechaDD;
	}

	public String getFilterFechaMM() {
		return filterFechaMM;
	}

	public void setFilterFechaMM(String filterFechaMM) {
		this.filterFechaMM = filterFechaMM;
	}

	public String getFilterFechaAA() {
		return filterFechaAA;
	}

	public void setFilterFechaAA(String filterFechaAA) {
		this.filterFechaAA = filterFechaAA;
	}
}
