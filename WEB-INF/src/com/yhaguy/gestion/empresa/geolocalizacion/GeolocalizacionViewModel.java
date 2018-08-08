package com.yhaguy.gestion.empresa.geolocalizacion;

import java.util.ArrayList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.gmaps.Gmaps;
import org.zkoss.gmaps.Gmarker;
import org.zkoss.gmaps.event.MapMouseEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Div;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class GeolocalizacionViewModel extends SimpleViewModel {
	
	private boolean open = false;
	private boolean registrarUbicacion = false;
	
	private String razonSocial = "";
	private String razonSocial_ = "";
	private Empresa selectedEmpresa;
	private Cliente selectedCliente;
	private Tipo selectedZona;
	private Funcionario selectedVendedor;
	private Funcionario selectedCobrador;
	
	private List<String> zonas_ = new ArrayList<String>();
	private List<Funcionario> vendedores_ = new ArrayList<Funcionario>();
	private List<Funcionario> cobradores_ = new ArrayList<Funcionario>();
	
	private List<Empresa> empresas = new ArrayList<Empresa>();
	
	@Wire
	private Borderlayout body;
	
	@Wire
	private Window win;
	
	@Wire
	private Div dv_mapa;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void buscarClientes() throws Exception {
		Clients.showBusy(this.dv_mapa, "Buscando Clientes Geo-localizados...");
		Events.echoEvent("onLater", this.dv_mapa, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	@NotifyChange("*")
	public void clearProgress() throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(dv_mapa);
			}
		});
		timer.setParent(this.win);		
		this.buscarClientes_();
	}
	
	@Command
	@NotifyChange({ "razonSocial", "selectedEmpresa", "selectedZona", "selectedVendedor", "selectedCobrador" })
	public void verClientesUbicados(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2) {
		this.razonSocial = "";
		this.selectedEmpresa = null;
		this.selectedZona = null;
		this.selectedVendedor = null;
		this.selectedCobrador = null;
		comp1.setVisible(false);
		comp2.setVisible(true);
	}
	
	@Command
	@NotifyChange({ "razonSocial", "selectedEmpresa" })
	public void refreshSelectedEmpresa() {
		this.selectedEmpresa = null;
		this.razonSocial = "";
	}
	
	@Command
	@NotifyChange("selectedZona")
	public void refreshSelectedZona() {
		this.selectedZona = null;
	}
	
	@Command
	@NotifyChange("selectedVendedor")
	public void refreshSelectedVendedor() {
		this.selectedVendedor = null;
	}
	
	@Command
	@NotifyChange("selectedCobrador")
	public void refreshSelectedCobrador() {
		this.selectedCobrador = null;
	}
	
	@Command
	@NotifyChange({ "selectedZona", "selectedVendedor", "selectedCobrador" })
	public void selectCliente(@BindingParam("comp") Bandbox comp) {
		this.selectedZona = null;
		this.selectedVendedor = null;
		this.selectedCobrador = null;
		comp.close();
	}
	
	@Command
	@NotifyChange({ "razonSocial", "selectedEmpresa", "selectedVendedor", "selectedCobrador" })
	public void selectZona() {
		this.selectedEmpresa = null;
		this.selectedCobrador = null;
		this.selectedVendedor = null;
		this.razonSocial = "";
	}
	
	@Command
	@NotifyChange({ "razonSocial", "selectedEmpresa", "selectedZona", "selectedCobrador" })
	public void selectVendedor() {
		this.selectedEmpresa = null;
		this.razonSocial = "";
		this.selectedZona = null;
		this.selectedCobrador = null;
	}
	
	@Command
	@NotifyChange({ "razonSocial", "selectedEmpresa", "selectedZona", "selectedVendedor" })
	public void selectCobrador() {
		this.selectedEmpresa = null;
		this.razonSocial = "";
		this.selectedZona = null;
		this.selectedVendedor = null;
	}
	
	@Command
	public void saveEmpresa(@BindingParam("emp") Empresa emp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(emp, this.getLoginNombre());
		Clients.showNotification("REGISTRO GUARDADO..");
	}
	
	@Command
	@NotifyChange("*")
	public void registrarUbicacion(@BindingParam("comp1") Component comp1, 
			@BindingParam("comp2") Component comp2) {
		this.razonSocial = "NO REGISTRADO..";
		this.selectedCobrador = null;
		this.selectedEmpresa = null;
		this.selectedVendedor = null;
		this.selectedZona = null;
		this.registrarUbicacion = true;
		comp1.setVisible(false);
		comp2.setVisible(true);
	}
	
	@SuppressWarnings("deprecation")
	@Command
	@NotifyChange("registrarUbicacion")
	public void getCoordenadas(@ContextParam(ContextType.TRIGGER_EVENT) MapMouseEvent event,
			@BindingParam("map") Gmaps map, @BindingParam("ref") Component ref) {
		if(!this.registrarUbicacion) return;
		if (this.selectedCliente == null) {
			Clients.showNotification("Debe Seleccionar un Cliente..", Clients.NOTIFICATION_TYPE_ERROR, ref, null, 0);
			return;
		}
		Gmarker mark = new Gmarker();
		mark.setLat(event.getLat());
		mark.setLng(event.getLng());
		mark.setIconImage("/core/images/map.png");
		mark.setContent(this.selectedCliente.getRazonSocial());
		mark.setOpen(true);
		map.appendChild(mark);
		this.registrarUbicacion = false;
		this.selectedCliente.getEmpresa().setLatitud(String.valueOf(event.getLat()));
		this.selectedCliente.getEmpresa().setLongitud(String.valueOf(event.getLng()));
	}
	
	@Command
	@NotifyChange("*")
	public void saveCliente(@BindingParam("comp1") Component comp1, 
			@BindingParam("comp2") Component comp2) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.selectedCliente, this.getLoginNombre());
		this.selectedCliente = null;
		this.razonSocial = "";
		this.razonSocial_ = "";
		this.registrarUbicacion = false;
		Clients.showNotification("REGISTRO GUARDADO..");
		comp1.setVisible(false);
		comp2.setVisible(true);
	}
	
	@Command
	@NotifyChange("*")
	public void cancelarRegistrar(@BindingParam("comp1") Component comp1, 
			@BindingParam("comp2") Component comp2) {
		this.selectedCliente = null;
		this.razonSocial = "";
		this.razonSocial_ = "";
		this.registrarUbicacion = false;
		comp1.setVisible(false);
		comp2.setVisible(true);
	}
	
	@Command
	public void verPendientesLocalizacion() throws Exception {
		this.reportePendientesLocalizacion();
	}
	
	@Command
	public void print() {
		Window test = (Window) Executions.createComponents("/yhaguy/gestion/empresa/print_map.zul", this.win, null);
		test.doModal();
	}
	
	@Command
	public void cancelar(@BindingParam("win") Window win) {
		this.body.setVisible(true);
		win.detach();
	}
	
	/**
	 * busca los clientes con facturas vencidas..
	 */
	public void buscarClientes_() throws Exception {		
		this.empresas.addAll(this.getEmpresasUbicadas());
		this.vendedores_.addAll(this.getVendedores());
		this.cobradores_.addAll(this.getCobradores());
		for (Tipo zona : this.getZonas()) {
			this.zonas_.add(zona.getDescripcion());
		}
		BindUtils.postNotifyChange(null, null, this, "empresas");
		BindUtils.postNotifyChange(null, null, this, "vendedores_");
		BindUtils.postNotifyChange(null, null, this, "cobradores_");
	}
	
	/**
	 * genera el reporte de clientes pendientes de localizar..
	 */
	private void reportePendientesLocalizacion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> pendientes = rr.getClientesNoGeolocalizados();
		List<Object[]> data = new ArrayList<Object[]>();
		data.addAll(pendientes);
		
		ReportePendientesLocalizacion rep = new ReportePendientesLocalizacion();
		rep.setDatosReporte(data);
		rep.setApaisada();			

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);	
	}
	
	/**
	 * GETS / SETS
	 */
	@DependsOn({ "razonSocial" })
	public List<Empresa> getClientesUbicados() throws Exception {
		if (this.razonSocial.isEmpty()) {
			return new ArrayList<Empresa>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresasGeolocalizadas(this.razonSocial, 30);
	}
	
	@DependsOn({ "razonSocial" })
	public List<Empresa> getClientesUbicados_() throws Exception {
		if (this.razonSocial.isEmpty()) {
			return new ArrayList<Empresa>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresasGeolocalizadas(this.razonSocial, 30);
	}
	
	@DependsOn({ "razonSocial_" })
	public List<Cliente> getClientes() throws Exception {
		if (this.razonSocial_.isEmpty()) {
			return new ArrayList<Cliente>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getClientes(razonSocial_);	
	}
	
	/**
	 * @return las zonas..
	 */
	public List<Tipo> getZonas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_ZONAS);
	}
	
	/**
	 * @return los vendedores..
	 */
	public List<Funcionario> getVendedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getVendedores();
	}
	
	/**
	 * @return los cobradores..
	 */
	public List<Funcionario> getCobradores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionariosCobradores();
	}
	
	/**
	 * @return las empresas ubicadas..
	 */
	public List<Empresa> getEmpresasUbicadas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresasGeolocalizadas(this.razonSocial, 500);
	}
	
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public Empresa getSelectedEmpresa() {
		return selectedEmpresa;
	}

	public void setSelectedEmpresa(Empresa selectedEmpresa) {
		this.selectedEmpresa = selectedEmpresa;
	}

	public Tipo getSelectedZona() {
		return selectedZona;
	}

	public void setSelectedZona(Tipo selectedZona) {
		this.selectedZona = selectedZona;
	}

	public List<String> getZonas_() {
		return zonas_;
	}

	public void setZonas_(List<String> zonas_) {
		this.zonas_ = zonas_;
	}

	public Funcionario getSelectedVendedor() {
		return selectedVendedor;
	}

	public void setSelectedVendedor(Funcionario vendedor) {
		this.selectedVendedor = vendedor;
	}

	public List<Funcionario> getVendedores_() {
		return vendedores_;
	}

	public void setVendedores_(List<Funcionario> vendedores_) {
		this.vendedores_ = vendedores_;
	}

	public Funcionario getSelectedCobrador() {
		return selectedCobrador;
	}

	public void setSelectedCobrador(Funcionario selectedCobrador) {
		this.selectedCobrador = selectedCobrador;
	}

	public List<Funcionario> getCobradores_() {
		return cobradores_;
	}

	public void setCobradores_(List<Funcionario> cobradores_) {
		this.cobradores_ = cobradores_;
	}

	public String getRazonSocial_() {
		return razonSocial_;
	}

	public void setRazonSocial_(String razonSocial_) {
		this.razonSocial_ = razonSocial_;
	}

	public Cliente getSelectedCliente() {
		return selectedCliente;
	}

	public void setSelectedCliente(Cliente selectedCliente) {
		this.selectedCliente = selectedCliente;
	}

	public boolean isRegistrarUbicacion() {
		return registrarUbicacion;
	}

	public void setRegistrarUbicacion(boolean registrarUbicacion) {
		this.registrarUbicacion = registrarUbicacion;
	}

	public List<Empresa> getEmpresas() {
		return empresas;
	}
	
	@DependsOn({ "selectedEmpresa", "selectedZona", "selectedVendedor", "selectedCobrador" })
	public List<Empresa> getEmpresas_() {
		List<Empresa> out = new ArrayList<Empresa>();
		if (this.selectedEmpresa != null) {
			out.add(this.selectedEmpresa);
			return out;
		}
		if (this.selectedZona != null) {
			for (Empresa empresa : this.empresas) {
				if (empresa.getZona().equals(this.selectedZona.getDescripcion())) {
					out.add(empresa);					
				}
			}
			return out;
		}
		if (this.selectedVendedor != null) {
			for (Empresa empresa : this.empresas) {
				if (empresa.getVendedor().getId().longValue() == this.selectedVendedor.getId().longValue()) {
					out.add(empresa);
				}
			}
			return out;
		}		
		if (this.selectedCobrador != null) {
			for (Empresa empresa : this.empresas) {
				if (empresa.getCobrador().getId().longValue() == this.selectedCobrador.getId().longValue()) {
					out.add(empresa);
				}
			}
			return out;
		}
		return this.empresas;
	}

	public void setEmpresas(List<Empresa> empresas) {
		this.empresas = empresas;
	}
}

/**
 * REPORTE clientes pendientes de localizar
 */
class ReportePendientesLocalizacion extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Direccion", TIPO_STRING);

	public ReportePendientesLocalizacion() {
	}

	static {
		cols.add(col1);
		cols.add(col2);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Clientes sin geolocalizacion");
		this.setDirectorio("Clientes");
		this.setNombreArchivo("Pend-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		return out;
	}
}
