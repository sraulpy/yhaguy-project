package com.yhaguy.gestion.compras.gastos.ordencompra;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.OrdenCompra;
import com.yhaguy.domain.OrdenCompraDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

public class OrdenCompraViewModel extends SimpleViewModel {
	
	private OrdenCompra nvoOrdenCompra;
	private OrdenCompra selectedOrdenCompra;
	private OrdenCompraDetalle nvoDetalle = new OrdenCompraDetalle();
	
	private String filterRazonSocial = "";
	private String filterRuc = "";
	private String filterNumero = "";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String filterNumeroOrden = "";
	private String filterRazonSocial_ = "";
	private String filterAutorizadoPor = "";
	private String filterSolicitadoPor = "";
	
	private int listSize = 0;
	private double totalImporteGs = 0;
	
	private Window win;
	
	@Wire
	private Popup pop_img;
	
	@Init(superclass = true)
	public void init() {
		try {
			this.inicializarOrdenCompra();
			this.inicializarDetalle();
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void imprimirOrden() throws Exception {
		this.imprimirOrdenCompra(this.selectedOrdenCompra);
	}
	
	@Command
	@NotifyChange("*")
	public void addDetalle() {
		this.nvoOrdenCompra.getDetalles().add(this.nvoDetalle);
		this.inicializarDetalle();
	}
	
	@Command
	@NotifyChange("nvoOrdenCompra")
	public void deleteItem(@BindingParam("item") OrdenCompraDetalle item) {
		this.nvoOrdenCompra.getDetalles().remove(item);
	}
	
	@Command
	@NotifyChange("*")
	public void addOrdenCompra(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvoOrdenCompra.setNumero("ORDEN-" + AutoNumeroControl.getAutoNumero("ORDEN-", 7));
		rr.saveObject(this.nvoOrdenCompra, this.getLoginNombre());
		this.imprimirOrdenCompra(this.nvoOrdenCompra);
		this.inicializarOrdenCompra();
		this.inicializarDetalle();
		comp.close();
	}
	
	@Command
	@NotifyChange("selectedOrdenCompra")
	public void verImagen(@BindingParam("item") OrdenCompra item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedOrdenCompra = item;
		this.pop_img.open(200, 100);
		Clients.evalJavaScript("setImage('" + this.selectedOrdenCompra.getUrlImagen() + "')");
	}
	
	/**
	 * Despliega el Reporte de Orden de Compra..
	 */
	private void imprimirOrdenCompra(OrdenCompra orden) throws Exception {		
		String source = ReportesViewModel.SOURCE_ORDEN_COMPRA;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new OrdenCompraDataSource(orden);
		params.put("Fecha", Utiles.getDateToString(orden.getFecha(), Utiles.DD_MM_YYYY));
		params.put("Numero", orden.getNumero());
		params.put("AutorizadoPor", orden.getAutorizadoPor());
		params.put("SolicitadoPor", orden.getSolicitadoPor());
		params.put("Observacion", orden.getObservacion());
		params.put("Proveedor", orden.getProveedor().getRazonSocial());
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
	 * inicializa la orden de compra..
	 */
	private void inicializarOrdenCompra() throws Exception {
		this.nvoOrdenCompra = new OrdenCompra();
		this.nvoOrdenCompra.setFecha(new Date());
		this.nvoOrdenCompra.setNumero("ORDEN-" + AutoNumeroControl.getAutoNumero("ORDEN-", 7, true));
		this.nvoOrdenCompra.setObservacion("Sin obs..");
		this.nvoOrdenCompra.setAutorizadoPor(this.getUs().getNombre().toUpperCase());
		this.nvoOrdenCompra.setProveedor(null);
	}
	
	/**
	 * inicializa el detalle del servicio..
	 */
	private void inicializarDetalle() {
		this.nvoDetalle = new OrdenCompraDetalle();
		this.nvoDetalle.setDescripcion("");
		this.nvoDetalle.setCantidad(0);
		this.nvoDetalle.setPrecioGs(0);
	}
	
	/**
	 * DataSource de la orden de compra..
	 */
	class OrdenCompraDataSource implements JRDataSource {

		private List<OrdenCompraDetalle> dets = new ArrayList<OrdenCompraDetalle>();
		
		public OrdenCompraDataSource(OrdenCompra orden) {
			this.dets.addAll(orden.getDetalles());
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			OrdenCompraDetalle item = this.dets.get(index);
			if ("TituloDetalle".equals(fieldName)) {
				value = " ";
			} else if ("Descripcion".equals(fieldName)) {
				value = item.getDescripcion();
			} else if ("Cantidad".equals(fieldName)) {
				value = item.getCantidad() + "";
			} else if ("PrecioGs".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getPrecioGs());
			} else if ("ImporteGs".equals(fieldName)) {
				value = Utiles.getNumberFormat(item.getImporteGs());
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
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA",
		"filterNumeroOrden", "filterRazonSocial_", "filterAutorizadoPor", "filterSolicitadoPor" })
	public List<OrdenCompra> getOrdenesCompra() throws Exception {
		this.totalImporteGs = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<OrdenCompra> out = rr.getOrdenesCompra(this.getFilterFecha(),
				this.filterNumeroOrden, this.filterRazonSocial_,
				this.filterAutorizadoPor, this.filterSolicitadoPor);
		for (OrdenCompra orden : out) {
			this.totalImporteGs += orden.getTotalImporteGs();
		}
		this.listSize = out.size();
		BindUtils.postNotifyChange(null, null, this, "totalImporteGs");
		BindUtils.postNotifyChange(null, null, this, "listSize");
		return out;
	}
	
	@DependsOn({ "filterRazonSocial", "filterRuc" })
	public List<Proveedor> getProveedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedores(this.filterRazonSocial, this.filterRuc);
	}
	
	@DependsOn({ "nvoOrdenCompra.proveedor", "nvoOrdenCompra.autorizadoPor", "nvoOrdenCompra.solicitadoPor" })
	public boolean isDetalleVisible() {
		return this.nvoOrdenCompra.getProveedor() != null
				&& this.nvoOrdenCompra.getAutorizadoPor() != null
				&& this.nvoOrdenCompra.getSolicitadoPor() != null;
	}
	
	@DependsOn({ "nvoDetalle.precioGs", "nvoDetalle.cantidad", "nvoDetalle.descripcion" })
	public boolean isAddDetalleDisabled() {
		return this.nvoDetalle.getPrecioGs() <= 0 
				|| this.nvoDetalle.getCantidad() <= 0
				|| this.nvoDetalle.getDescripcion().isEmpty();
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
	
	/**
	 * @return el acceso
	 */
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}
	
	public OrdenCompra getSelectedOrdenCompra() {
		return selectedOrdenCompra;
	}

	public void setSelectedOrdenCompra(OrdenCompra selectedOrdenCompra) {
		this.selectedOrdenCompra = selectedOrdenCompra;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public String getFilterRuc() {
		return filterRuc;
	}

	public void setFilterRuc(String filterRuc) {
		this.filterRuc = filterRuc;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
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

	public String getFilterNumeroOrden() {
		return filterNumeroOrden;
	}

	public void setFilterNumeroOrden(String filterNumeroOrden) {
		this.filterNumeroOrden = filterNumeroOrden;
	}

	public String getFilterRazonSocial_() {
		return filterRazonSocial_;
	}

	public void setFilterRazonSocial_(String filterRazonSocial_) {
		this.filterRazonSocial_ = filterRazonSocial_;
	}

	public String getFilterAutorizadoPor() {
		return filterAutorizadoPor;
	}

	public void setFilterAutorizadoPor(String filterReceptor) {
		this.filterAutorizadoPor = filterReceptor;
	}

	public String getFilterSolicitadoPor() {
		return filterSolicitadoPor;
	}

	public void setFilterSolicitadoPor(String filterTecnico) {
		this.filterSolicitadoPor = filterTecnico;
	}

	public OrdenCompra getNvoOrdenCompra() {
		return nvoOrdenCompra;
	}

	public void setNvoOrdenCompra(OrdenCompra nvoOrdenCompra) {
		this.nvoOrdenCompra = nvoOrdenCompra;
	}

	public OrdenCompraDetalle getNvoDetalle() {
		return nvoDetalle;
	}

	public void setNvoDetalle(OrdenCompraDetalle nvoDetalle) {
		this.nvoDetalle = nvoDetalle;
	}

	public int getListSize() {
		return listSize;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public double getTotalImporteGs() {
		return totalImporteGs;
	}

	public void setTotalImporteGs(double totalImporteGs) {
		this.totalImporteGs = totalImporteGs;
	}
}
