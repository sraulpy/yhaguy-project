package com.yhaguy.gestion.pagos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
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
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.caja.recibos.AssemblerRecibo;
import com.yhaguy.gestion.caja.recibos.ReciboDTO;
import com.yhaguy.gestion.caja.recibos.ReciboDetalleDTO;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

@SuppressWarnings("unchecked")
public class PagosViewModel extends SimpleViewModel {
	
	static final String FILTRO_TODOS = "TODOS";
	static final String FILTRO_CON_REC = "CON REC";
	static final String FILTRO_SIN_REC = "SIN REC";
	
	static final String ZUL_REGISTRAR_RECIBO = "/yhaguy/gestion/pagos/registrarRecibo.zul";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String filterNumero = "";
	private String filterNumeroRecibo = "";
	private String filterRazonSocial = "";
	private String filterRuc = "";
	private String filterCaja = "";
	
	private int listSize = 0;
	private double totalImporteGs = 0;
	
	private DetalleMovimiento detalle = new DetalleMovimiento();
	private MyArray selectedItem;
	private ReciboDTO pagoDto;
	private Object[] selectedFormato;
	private MyArray nvoRecibo = new MyArray();
	
	private String selectedFiltro = FILTRO_TODOS;
	
	private Window win;
	
	@Wire
	private Popup popDetallePagos;

	@Init(superclass = true)
	public void init(){
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
	@NotifyChange("detalle")
	public void verItems(@BindingParam("item") MyArray item,
			@BindingParam("parent") Component parent) throws Exception {
		this.detalle = new DetalleMovimiento();
		this.detalle.setEmision((Date) item.getPos1());
		this.detalle.setNumero(String.valueOf(item.getPos2()));
		this.detalle.setTipoMovimiento((String) item.getPos7());
		this.detalle.setProveedor((String) item.getPos3());
		this.detalle.setDetalles((List<MyArray>) item.getPos8());
		this.detalle.setFormasPago((List<MyArray>) item.getPos9());
		this.popDetallePagos.open(parent, "start_before");
	}
	
	@Command
	public void listadoPagos() throws Exception {
		this.reportePagos();
	}
	
	@Command
	public void imprimirItem() throws Exception {
		this.imprimirPago();
	}
	
	@Command
	@NotifyChange("*")
	public void registrarRecibo() throws Exception {
		this.registrarRecibo_();
	}
	
	@Command
	@NotifyChange({ "selectedFiltro", "totalImporteGs" })
	public void selectFilter(@BindingParam("filter") int filter) {
		if (filter == 1) {
			this.selectedFiltro = FILTRO_TODOS;
		} else if (filter == 2) {
			this.selectedFiltro = FILTRO_CON_REC;
		} else if (filter == 3) {
			this.selectedFiltro = FILTRO_SIN_REC;
		}
	}
	 
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA",
			"filterNumero", "filterRazonSocial", "filterRuc", "filterCaja", 
			"filterNumeroRecibo", "selectedFiltro" })
	public List<MyArray> getPagos() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		this.totalImporteGs = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> pagos = rr.getPagos(this.getFilterFecha(),
				this.filterNumero, this.filterRazonSocial, this.filterRuc, this.filterCaja, this.filterNumeroRecibo);
		for (Recibo pago : pagos) {
			MyArray my = new MyArray();
			List<MyArray> dets = new ArrayList<MyArray>();
			List<MyArray> fpgs = new ArrayList<MyArray>();
			my.setId(pago.getId());
			my.setPos1(pago.getFechaEmision());
			my.setPos2(pago.getNumero());
			my.setPos3(pago.getProveedor().getRazonSocial());
			my.setPos4(pago.getProveedor().getRuc());
			my.setPos5(pago.getTotalImporteGs());
			my.setPos6(pago.getNumeroPlanilla());
			my.setPos7(pago.getTipoMovimiento().getDescripcion());		
			for (ReciboDetalle det : pago.getDetalles()) {
				if (det.getMovimiento() == null) {
					dets.add(new MyArray(Utiles.getDateToString(pago.getFechaEmision(), Utiles.DD_MM_YYYY),
							Utiles.getDateToString(pago.getFechaEmision(), Utiles.DD_MM_YYYY),
							det.getConcepto(), det.getMontoGs(), det.getMontoGs()));
				} else {
					dets.add(new MyArray(
							Utiles.getDateToString(det.getMovimiento().getFechaEmision(), Utiles.DD_MM_YYYY), 
							Utiles.getDateToString(det.getMovimiento().getFechaVencimiento(), Utiles.DD_MM_YYYY), 
							det.getMovimiento().getNroComprobante(), det.getMontoGs(), det.getMontoGs()));
				}				
			}
			for (ReciboFormaPago fp : pago.getFormasPago()) {
				fpgs.add(new MyArray(fp.getDescripcion(), fp.getMontoGs()));
			}
			my.setPos8(dets);
			my.setPos9(fpgs);
			my.setPos10(pago.getNumeroRecibo().isEmpty() ? "- - -" : pago.getNumeroRecibo());
			my.setPos11(pago.isEntregado());
			switch (this.selectedFiltro) {
			case FILTRO_TODOS:
				out.add(my);
				this.totalImporteGs += pago.getTotalImporteGs();
				break;
			case FILTRO_CON_REC:
				if(pago.isEntregado()){
					out.add(my);
					this.totalImporteGs += pago.getTotalImporteGs();
				}
				break;
			case FILTRO_SIN_REC:
				if(!pago.isEntregado()){
					out.add(my);
					this.totalImporteGs += pago.getTotalImporteGs();
				}
				break;
			}
		}
		this.listSize = out.size();
		BindUtils.postNotifyChange(null, null, this, "listSize");
		BindUtils.postNotifyChange(null, null, this, "totalImporteGs");
		return out;
	}
	
	/**
	 * registra los datos del recibo de pago..
	 */
	private void registrarRecibo_() throws Exception {	
		this.nvoRecibo.setPos1("");
		this.nvoRecibo.setPos2(new Date());
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setModo(WindowPopup.NUEVO);
		wp.setHigth("300px");
		wp.setWidth("400px");
		wp.setCheckAC(new ValidadorRegistrarRecibo());
		wp.setTitulo("Registrar Recibo de Pago");
		wp.show(ZUL_REGISTRAR_RECIBO);
		if (wp.isClickAceptar()) {
			String numeroRecibo = ((String) this.nvoRecibo.getPos1()).toUpperCase();
			Date fechaRecibo = (Date) this.nvoRecibo.getPos2();
			long idOrdenPago = this.selectedItem.getId();
			String user = this.getLoginNombre();
			AssemblerRecibo.registrarReciboPago(numeroRecibo, fechaRecibo, idOrdenPago, user, false);			
			this.selectedItem = null;
			this.mensajePopupTemporal("Recibo Registrado..", 5000);
		}
	
	}
	
	/**
	 * reporte de pagos..
	 */
	private void reportePagos() throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();
		for (MyArray recibo : this.getPagos()) {
			Object[] obj = new Object[] {
					Utiles.getDateToString((Date) recibo.getPos1(), Utiles.DD_MM_YY),
					recibo.getPos2(),
					recibo.getPos3().toString().toUpperCase(),
					recibo.getPos4(),
					recibo.getPos5()};
			data.add(obj);
		}

		ReportePagos rep = new ReportePagos(this.getAcceso().getSucursalOperativa().getText());
		rep.setDatosReporte(data);
		rep.setBorrarDespuesDeVer(true);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/**
	 * Despliega el Reporte de Pago..
	 */
	private void imprimirPago() throws Exception {
		this.pagoDto = (ReciboDTO) this.getDTOById(Recibo.class.getName(), this.selectedItem.getId(), new AssemblerRecibo());
		
		String source = ReportesViewModel.SOURCE_RECIBO;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ReciboDataSource();
		params.put("title", this.pagoDto.getTipoMovimiento().getPos1());
		params.put("fieldRazonSocial", this.pagoDto.isCobro() ? "Recibí de:"
				: "A la Orden de:");
		params.put("RazonSocial", this.pagoDto.getRazonSocial());
		params.put("Ruc", this.pagoDto.getRuc());
		params.put("NroRecibo", this.pagoDto.getNumero());
		params.put("ImporteEnLetra", this.pagoDto.getImporteEnLetras());
		params.put("TotalImporteGs", Utiles.getNumberFormat(this.pagoDto.getTotalImporteGs()));
		params.put("Usuario", this.getUs().getNombre());
		this.imprimirComprobante(source, params, dataSource, this.selectedFormato);
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
			for (ReciboDetalleDTO item : pagoDto.getDetalles()) {
				MyArray m = new MyArray();
				m.setPos1(item.getFecha());
				m.setPos2(item.getDescripcion());
				m.setPos3(item.getMontoGs());
				m.setPos4("Facturas");
				this.detalle.add(m);
			}
			for (ReciboFormaPagoDTO item : pagoDto.getFormasPago()) {
				MyArray my = new MyArray();
				my.setPos1(m.dateToString(item.getModificado(), Misc.DD_MM_YYYY));
				my.setPos2(item.getDescripcion());
				my.setPos3(item.getMontoGs());
				my.setPos4("Formas de Pago");
				this.detalle.add(my);
			}
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
				value = Utiles.getNumberFormat(importe);
			} else if ("TotalImporte".equals(fieldName)) {
				double importe = pagoDto.getTotalImporteGs();
				value = Utiles.getNumberFormat(importe);
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
	 * contiene los datos del movimiento..
	 */
	public class DetalleMovimiento {
		
		private Date emision; 
		private String numero;
		private String tipoMovimiento;
		private String proveedor;
		private List<MyArray> detalles;
		private List<MyArray> formasPago;
		
		/**
		 * @return el importe total..
		 */
		public double getTotalImporteGs() {
			if (this.detalles == null) {
				return 0;
			}
			double out = 0;
			for (MyArray item : detalles) {
				double importe = (double) item.getPos5();
				out += importe;
			}			
			return out;
		}
		
		/**
		 * @return el importe total de formas pago..
		 */
		public double getTotalImporteFormaPagoGs() {
			if (this.formasPago == null) {
				return 0;
			}
			double out = 0;
			for (MyArray item : formasPago) {
				double importe = (double) item.getPos2();
				out += importe;
			}			
			return out;
		}

		public String getNumero() {
			return numero;
		}

		public void setNumero(String numero) {
			this.numero = numero;
		}

		public Date getEmision() {
			return emision;
		}

		public void setEmision(Date emision) {
			this.emision = emision;
		}

		public List<MyArray> getDetalles() {
			return detalles;
		}

		public void setDetalles(List<MyArray> detalle) {
			this.detalles = detalle;
		}

		public String getTipoMovimiento() {
			return tipoMovimiento;
		}

		public void setTipoMovimiento(String tipoMovimiento) {
			this.tipoMovimiento = tipoMovimiento.toUpperCase();
		}

		public String getProveedor() {
			return proveedor;
		}

		public void setProveedor(String cliente) {
			this.proveedor = cliente.toUpperCase();
		}

		public List<MyArray> getFormasPago() {
			return formasPago;
		}

		public void setFormasPago(List<MyArray> formasPago) {
			this.formasPago = formasPago;
		}			
	}	
	
	/**
	 * validador registrar recibo..
	 */
	class ValidadorRegistrarRecibo implements VerificaAceptarCancelar {
		
		String mensaje = "";

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a:";
			
			if (nvoRecibo.getPos1().toString().isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar el número de recibo..";
			}
			
			return out;
		}

		@Override
		public String textoVerificarAceptar() {
			return this.mensaje;
		}

		@Override
		public boolean verificarCancelar() {
			return true;
		}

		@Override
		public String textoVerificarCancelar() {
			return "";
		}
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn("selectedItem")
	public boolean isEntregado() {
		if(this.selectedItem == null) return false;
		return (boolean) this.selectedItem.getPos11();
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
	 * @return los formatos de reporte..
	 */
	public List<Object[]> getFormatos() {
		List<Object[]> out = new ArrayList<Object[]>();
		out.add(ReportesViewModel.FORMAT_PDF);
		out.add(ReportesViewModel.FORMAT_XLS);
		out.add(ReportesViewModel.FORMAT_CSV);
		return out;
	}
	
	private AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}

	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFecha) {
		this.filterFechaDD = filterFecha;
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

	public String getFilterCaja() {
		return filterCaja;
	}

	public void setFilterCaja(String filterCaja) {
		this.filterCaja = filterCaja;
	}

	public DetalleMovimiento getDetalle() {
		return detalle;
	}

	public void setDetalle(DetalleMovimiento detalle) {
		this.detalle = detalle;
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
	}

	public Object[] getSelectedFormato() {
		return selectedFormato;
	}

	public void setSelectedFormato(Object[] selectedFormato) {
		this.selectedFormato = selectedFormato;
	}

	public MyArray getNvoRecibo() {
		return nvoRecibo;
	}

	public void setNvoRecibo(MyArray nvoRecibo) {
		this.nvoRecibo = nvoRecibo;
	}

	public String getFilterNumeroRecibo() {
		return filterNumeroRecibo;
	}

	public void setFilterNumeroRecibo(String filterNumeroRecibo) {
		this.filterNumeroRecibo = filterNumeroRecibo;
	}

	public String getSelectedFiltro() {
		return selectedFiltro;
	}

	public void setSelectedFiltro(String selectedFiltro) {
		this.selectedFiltro = selectedFiltro;
	}
}

/**
 * Reporte de Recibos..
 */
class ReportePagos extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 40);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Proveedor", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 40);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE, 40, true);

	private String sucursal;

	public ReportePagos(String sucursal) {
		this.sucursal = sucursal;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Órdenes de Pago");
		this.setDirectorio("recibos");
		this.setNombreArchivo("pago-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}
}

