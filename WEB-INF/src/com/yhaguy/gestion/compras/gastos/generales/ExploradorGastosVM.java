package com.yhaguy.gestion.compras.gastos.generales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ExploradorGastosVM extends SimpleViewModel {
	
	static final String FILTRO_TODOS = "TODOS";
	static final String FILTRO_GASTOS_CAJA_CHICA = "GASTOS_CAJA_CHICA";
	static final String FILTRO_GASTOS_IMPORTACION = "GASTOS_IMPORTACION";
	static final String FILTRO_PAGADOS = "PAGADOS";
	static final String FILTRO_PENDIENTES_PAGO = "PENDIENTES_PAGO";
	
	private String selectedFiltro = FILTRO_TODOS;
	
	private Object[] selectedGasto_;
	private Gasto selectedGasto;
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String filterNumero = "";
	private String filterRazonSocial = "";
	private String filterRuc = "";
	private String filterCaja = "";
	private String filterPago = "";
	private String filterImportacion = "";
	private String filterDescripcion = "";
	private String filterSucursal = "";
	private String filterCuenta = "";
	private String filterArticuloGasto = "";
	
	private int listSize = 0;
	private double totalImporteGs = 0;
	
	private Window win;
	
	@Wire
	private Popup pop_det;
	
	@Wire
	private Popup pop_img;

	@Init(superclass = true)
	public void init() {
		try {
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
	@NotifyChange("selectedFiltro")
	public void selectFilter(@BindingParam("filter") int filter) {
		if (filter == 1) {
			this.selectedFiltro = FILTRO_TODOS;
		} else if (filter == 2) {
			this.selectedFiltro = FILTRO_GASTOS_CAJA_CHICA;
		} else if (filter == 3) {
			this.selectedFiltro = FILTRO_GASTOS_IMPORTACION;
		} else if (filter == 4) {
			this.selectedFiltro = FILTRO_PAGADOS;
		} else {
			this.selectedFiltro = FILTRO_PENDIENTES_PAGO;
		}
	}
	
	@Command
	@NotifyChange("selectedGasto")
	public void verItems(@BindingParam("item") Gasto item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedGasto = item;
		this.pop_det.open(parent, "start_before");
	}
	
	@Command
	@NotifyChange("selectedGasto")
	public void verImagen(@BindingParam("item") Gasto item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedGasto = item;
		this.pop_img.open(200, 100);
		Clients.evalJavaScript("setImage('" + this.selectedGasto.getUrlImagen() + "')");
	}
	
	@Command
	public void imprimir() throws Exception {
		this.imprimir_();
	}
	
	@Command
	public void guardarCambios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedGasto.setImporteGs(this.selectedGasto.getImporteGs_());
		this.selectedGasto.setImporteDs(this.selectedGasto.getImporteDs_());
		rr.saveObject(this.selectedGasto, this.getLoginNombre());
		Clients.showNotification("REGISTRO GUARDADO..");
	}
	
	/**
	 * Despliega el Reporte de Pago..
	 */
	private void imprimir_() throws Exception {		
		String source = ReportesViewModel.SOURCE_RECIBO;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ReciboDataSource();
		params.put("title", this.selectedGasto.getTipoMovimiento().getDescripcion());
		params.put("fieldRazonSocial", "Proveedor:");
		params.put("RazonSocial", this.selectedGasto.getProveedor().getRazonSocial());
		params.put("Ruc", this.selectedGasto.getProveedor().getRuc());
		params.put("NroRecibo", this.selectedGasto.getNumeroFactura());
		params.put("Moneda", this.selectedGasto.isMonedaLocal() ? "Guaraníes:" : "Dólares:");
		params.put("Moneda_", this.selectedGasto.isMonedaLocal() ? "Gs." : "U$D");
		params.put("ImporteEnLetra", this.selectedGasto.isMonedaLocal() ? this.selectedGasto.getImporteEnLetras() : this.selectedGasto.getImporteEnLetrasDs());
		params.put("TotalImporteGs",
				this.selectedGasto.isMonedaLocal() ? Utiles.getNumberFormat(this.selectedGasto.getImporteGs())
						: Utiles.getNumberFormatDs(this.selectedGasto.getImporteDs()));
		params.put("Usuario", this.getUs().getNombre());
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
	 * DataSource del Recibo..
	 */
	class ReciboDataSource implements JRDataSource {

		List<MyArray> detalle = new ArrayList<MyArray>();

		public ReciboDataSource() {
			MyArray m = new MyArray();
			m.setPos1(Utiles.getDateToString(selectedGasto.getFecha(), Utiles.DD_MM_YYYY));
			m.setPos2(selectedGasto.getDescripcionCuenta1());
			m.setPos3(selectedGasto.isMonedaLocal() ? selectedGasto.getImporteGs() : selectedGasto.getImporteDs());
			m.setPos4("Detalle");
			this.detalle.add(m);
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			MyArray item = this.detalle.get(index);

			if ("FechaFactura".equals(fieldName)) {
				value = item.getPos1();
			} else if ("DescFactura".equals(fieldName)) {
				value = item.getPos2();
			} else if ("Importe".equals(fieldName)) {
				double importe = (double) item.getPos3();
				value = selectedGasto.isMonedaLocal() ? Utiles.getNumberFormat(importe) : Utiles.getNumberFormatDs(importe);
			} else if ("TotalImporte".equals(fieldName)) {
				double importe = selectedGasto.isMonedaLocal() ? selectedGasto.getImporteGs() : selectedGasto.getImporteDs();
				value = selectedGasto.isMonedaLocal() ? Utiles.getNumberFormat(importe) : Utiles.getNumberFormatDs(importe);
			} else if ("TipoDetalle".equals(fieldName)) {
				value = item.getPos4();
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < detalle.size() - 1) {
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
		"filterNumero", "filterRazonSocial", "filterRuc", "filterCaja", 
		"filterPago", "filterImportacion", "selectedFiltro", "filterDescripcion", "filterSucursal", "filterCuenta" })
	public List<Object[]> getGastos() throws Exception {
		this.totalImporteGs = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> aux = new ArrayList<Object[]>();
		List<Object[]> out = rr.getGastos(this.getFilterFecha(),
				this.filterNumero, this.filterRazonSocial, this.filterRuc, 
				this.filterCaja, this.filterPago, this.filterImportacion, this.filterDescripcion, this.filterSucursal, this.filterCuenta);
		for (Object[] gasto_ : out) {
			Gasto gasto = (Gasto) gasto_[0];
			double montoGs = (double) gasto_[2];
			if (this.selectedFiltro.equals(FILTRO_TODOS)) {
				aux.add(gasto_);
				this.totalImporteGs += montoGs;
			}
			if (this.selectedFiltro.equals(FILTRO_GASTOS_CAJA_CHICA)) {
				if (gasto.isFondoFijo()) {
					aux.add(gasto_);
					this.totalImporteGs += montoGs;
				}
			} 
			if (this.selectedFiltro.equals(FILTRO_GASTOS_IMPORTACION)) {
				if (gasto.isGastoImportacion()) {
					aux.add(gasto_);
					this.totalImporteGs += montoGs;
				}
			}
			if (this.selectedFiltro.equals(FILTRO_PAGADOS)) {
				if (gasto.isPagado()) {
					aux.add(gasto_);
					this.totalImporteGs += montoGs;
				}
			}
			if (this.selectedFiltro.equals(FILTRO_PENDIENTES_PAGO)) {
				if (gasto.isPendientePago()) {
					aux.add(gasto_);
					this.totalImporteGs += montoGs;
				}
			}
		}
		this.listSize = out.size();
		BindUtils.postNotifyChange(null, null, this, "listSize");
		BindUtils.postNotifyChange(null, null, this, "totalImporteGs");
		return aux;
	}
	
	@DependsOn("filterArticuloGasto")
	public List<ArticuloGasto> getArticulosGastos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulosGastos(this.filterArticuloGasto, 100);
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
	 * @return tipos de movimientos..
	 */
	public List<TipoMovimiento> getTiposMovimientos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTiposDeMovimientos("Gasto");
	}
	
	/**
	 * @return tipos de movimientos..
	 */
	public List<CondicionPago> getCondiciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CondicionPago> out = new ArrayList<CondicionPago>();
		out.add(rr.getCondicionPagoById(1));
		out.add(rr.getCondicionPagoById(2));
		return out;
	}
	
	/**
	 * @return las monedas..
	 */
	public List<Tipo> getMonedas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_MONEDA);
	}
	
	/**
	 * @return los tipos de iva..
	 */
	public List<Tipo> getTiposIva() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_IVA);
	}
	
	/**
	 * @return las sucursales..
	 */
	@SuppressWarnings("unchecked")
	public List<SucursalApp> getSucursales() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(SucursalApp.class.getName());
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

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
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

	public String getFilterCaja() {
		return filterCaja;
	}

	public void setFilterCaja(String filterCaja) {
		this.filterCaja = filterCaja;
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

	public String getFilterPago() {
		return filterPago;
	}

	public void setFilterPago(String filterPago) {
		this.filterPago = filterPago;
	}

	public Gasto getSelectedGasto() {
		return selectedGasto;
	}

	public void setSelectedGasto(Gasto selectedGasto) {
		this.selectedGasto = selectedGasto;
	}

	public String getFilterImportacion() {
		return filterImportacion;
	}

	public void setFilterImportacion(String filterImportacion) {
		this.filterImportacion = filterImportacion;
	}

	public String getSelectedFiltro() {
		return selectedFiltro;
	}

	public void setSelectedFiltro(String selectedFiltro) {
		this.selectedFiltro = selectedFiltro;
	}

	public String getFilterDescripcion() {
		return filterDescripcion;
	}

	public void setFilterDescripcion(String filterDescripcion) {
		this.filterDescripcion = filterDescripcion;
	}

	public String getFilterSucursal() {
		return filterSucursal;
	}

	public void setFilterSucursal(String filterSucursal) {
		this.filterSucursal = filterSucursal;
	}

	public String getFilterCuenta() {
		return filterCuenta;
	}

	public void setFilterCuenta(String filterCuenta) {
		this.filterCuenta = filterCuenta;
	}

	public Object[] getSelectedGasto_() {
		return selectedGasto_;
	}

	public void setSelectedGasto_(Object[] selectedGasto_) {
		this.selectedGasto_ = selectedGasto_;
		this.selectedGasto = (Gasto) selectedGasto_[0];
	}

	public String getFilterArticuloGasto() {
		return filterArticuloGasto;
	}

	public void setFilterArticuloGasto(String filterArticuloGasto) {
		this.filterArticuloGasto = filterArticuloGasto;
	}
}
