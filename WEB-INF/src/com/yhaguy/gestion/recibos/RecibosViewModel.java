package com.yhaguy.gestion.recibos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

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

import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.CierreDocumento;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.caja.periodo.CajaUtil;
import com.yhaguy.gestion.caja.recibos.AssemblerRecibo;
import com.yhaguy.gestion.caja.recibos.ReciboDTO;
import com.yhaguy.gestion.caja.recibos.ReciboDetalleDTO;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.process.ProcesosTesoreria;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@SuppressWarnings("unchecked")
public class RecibosViewModel extends SimpleViewModel {
	
	static final String FILTRO_RECIBOS = "RECIBOS";
	static final String FILTRO_REEMBOLSOS_CC = "REEMBOLSOS PRESTAMOS C.C.";
	static final String FILTRO_REEMBOLSOS_CHEQUES_RECH = "REEMBOLSOS CHEQUES RECHAZADOS";
	static final String FILTRO_ANTICIPOS = "ANTICIPOS";
	
	static final String ZUL_DETALLE = "/yhaguy/gestion/recibos/detalle_recibo.zul";	
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	private String filterNumero = "";
	private String filterRazonSocial = "";
	private String filterRuc = "";
	private String filterCaja = "";
	private String filterCobrador = "";
	
	private String selectedFiltro = FILTRO_RECIBOS;
	
	private int listSize = 0;
	private double totalImporteGs = 0;
	
	private DetalleMovimiento detalle = new DetalleMovimiento();
	private MyArray selectedItem;
	private ReciboDTO reciboDto;
	private Object[] selectedFormato;
	
	private Date fechaCierre;
	private boolean sinCorreo;
	
	private Window win;
		
	@Wire
	private Popup popDetalleRecibo;

	@Init(superclass = true)
	public void init(){
		try {
			this.sinCorreo = false;
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
			RegisterDomain rr = RegisterDomain.getInstance();
			List<CierreDocumento> cierres = rr.getCierreDocumentos();
			if (cierres.size() > 0) {
				this.fechaCierre = cierres.get(0).getFecha();
			} else {
				this.fechaCierre = new Date();
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
			this.selectedFiltro = FILTRO_RECIBOS;
		} else if (filter == 2) {
			this.selectedFiltro = FILTRO_REEMBOLSOS_CC;
		} else if (filter == 3) {
			this.selectedFiltro = FILTRO_REEMBOLSOS_CHEQUES_RECH;
		} else if (filter == 4) {
			this.selectedFiltro = FILTRO_ANTICIPOS;
		}
	}
	
	@Command
	@NotifyChange({ "detalle", "selectedItem" })
	public void verItems(@BindingParam("item") MyArray item,
			@BindingParam("parent") Component parent) throws Exception {
		this.selectedItem = item;
		this.detalle = new DetalleMovimiento();
		this.detalle.setEmision((Date) item.getPos1());
		this.detalle.setNumero(String.valueOf(item.getPos2()));
		this.detalle.setTipoMovimiento((String) item.getPos7());
		this.detalle.setCliente((String) item.getPos3());
		this.detalle.setDetalles((List<MyArray>) item.getPos8());
		this.detalle.setFormasPago((List<MyArray>) item.getPos9());
		this.detalle.setCobrador((String) item.getPos11());
		this.detalle.setMoneda((String) item.getPos12());
		this.detalle.setTipoCambio((double) item.getPos13());
		//this.popDetalleRecibo.open(parent, "start_before");
		
		this.win = (Window) Executions.createComponents(ZUL_DETALLE, this.mainComponent, null);
		this.win.doOverlapped();
	}
	
	@Command
	public void listadoRecibos() throws Exception {
		this.reporteRecibos();
	}
	
	@Command
	public void imprimirItem() throws Exception {
		this.imprimirRecibo();
	}
	
	@Command
	@NotifyChange("*")
	public void saveRecibo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo rec = (Recibo) rr.getObject(Recibo.class.getName(), this.selectedItem.getId());
		if (rec != null) {
			rec.setNumero(this.detalle.getNumero());
			rec.setNumero_(this.detalle.getNumero());
			rec.setFechaEmision(this.detalle.getEmision());
			rec.setCobrador(this.detalle.getCobrador());
			rr.saveObject(rec, this.getLoginNombre());
			CtaCteEmpresaMovimiento movim = rr.getCtaCteMovimientoByIdMovimiento(rec.getId(), rec.getTipoMovimiento().getSigla());
			if (movim != null) {
				movim.setNroComprobante(this.detalle.getNumero());
				rr.saveObject(movim, this.getLoginNombre());
			}
			for (ReciboFormaPago fp : rec.getFormasPago()) {
				if (fp.isChequeTercero()) {
					BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
					if (cheque != null) {
						cheque.setNumeroRecibo(rec.getNumero());
						rr.saveObject(cheque, this.getLoginNombre());
					}
				}
			}
		}
		this.win.detach();
		Clients.showNotification("REGISTRO GUARDADO..");
	}
	
	@Command
	public void habilitarImpresion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo rec = (Recibo) rr.getObject(Recibo.class.getName(), this.selectedItem.getId());
		rec.setOrden(Utiles.getDateToString(new Date(), Utiles.YYYY_MM_DD_HH_MM_SS));
		rr.saveObject(rec, rec.getUsuarioMod());
		Clients.showNotification("IMPRESIÓN HABILITADA..");
	}
	
	@Command
	@NotifyChange("*")
	public void anularRecibo(@BindingParam("motivo") String motivo) throws Exception {
		if (motivo.trim().isEmpty()) {
			Clients.showNotification("DEBE INGRESAR EL MOTIVO..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		List<CtaCteEmpresaMovimiento> movims = new ArrayList<CtaCteEmpresaMovimiento>();
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo rec = (Recibo) rr.getObject(Recibo.class.getName(), this.selectedItem.getId());
		String caja = rec.getNumeroPlanilla();
		if (CajaUtil.CAJAS_ABIERTAS.get(caja) != null) {
			String msg = "CAJA " + caja + " BLOQUEADA POR USUARIO: " + CajaUtil.CAJAS_ABIERTAS.get(caja);
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		if (rec.getFechaEmision().compareTo(Utiles.getFechaInicioMes()) < 0) {
			Clients.showNotification("EL RECIBO NO CORRESPONDE AL MES CORRIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		rec.setTotalImporteDs(0);
		rec.setTotalImporteGs(0);
		rec.setMotivoAnulacion(motivo);
		rec.setEstadoComprobante(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO));
		for (ReciboFormaPago fp : rec.getFormasPago()) {
			if (fp.isChequeTercero()) {
				BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
				if (cheque != null) {
					rr.deleteObject(cheque);
				}
			}
			rr.deleteObject(fp);
		}
		for (ReciboDetalle det : rec.getDetalles()) {
			movims.add(det.getMovimiento());
			rr.deleteObject(det);
		}
		for (CtaCteEmpresaMovimiento movim : movims) {
			if (movim.isVentaCredito()) {
				ProcesosTesoreria.depurarSaldosPorVenta(movim.getIdMovimientoOriginal());
			}
		}
		CtaCteEmpresaMovimiento movim = rr.getCtaCteMovimientoByIdMovimiento(rec.getId(), rec.getTipoMovimiento().getSigla());
		if (movim != null) {
			movim.setAnulado(true);
			rr.saveObject(movim, this.getLoginNombre());
		}
		rec.setDetalles(new HashSet<ReciboDetalle>());
		rec.setFormasPago(new HashSet<ReciboFormaPago>());
		rr.saveObject(rec, this.getLoginNombre());
		this.win.detach();
		Clients.showNotification("RECIBO ANULADO");
	}
	
	@Command
    public void copy(@BindingParam("numero") String numero) {
      Clients.evalJavaScript("writeToClipboard('"+ this.getUrl(numero) +"')");
    }
	
	@Command
	@NotifyChange("selectedItem")
	public void sendRecibo() {		
		String destino = (String) this.selectedItem.getPos14();
		
		if (!destino.trim().isEmpty()) {
			boolean valido = this.isValido(destino);
			if (!valido) {
				Clients.showNotification("Correo no válido", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
		}		
		
		if (destino.trim().isEmpty()) {
			destino = "laurap@yhaguyrepuestos.com.py";
		}
		
		String[] send = new String[] { destino };
		String[] sendCC = new String[] { "estelap@yhaguyrepuestos.com.py", "vanesar@yhaguyrepuestos.com.py",
				"rodrigol@yhaguyrepuestos.com.py", "laurap@yhaguyrepuestos.com.py" };
		String[] sendCCO = new String[] { "sergioa@yhaguyrepuestos.com.py" };
		try {		
			this.generatePDF();
			String asunto = "Recibo Digital - " + Configuracion.empresa;
			String root = Sessions.getCurrent().getWebApp().getRealPath("/");			
			EnviarCorreo enviarCorreo = new EnviarCorreo();			
			enviarCorreo.sendMessage(send, sendCC, sendCCO,
					asunto, "Estimado Cliente: " + this.selectedItem.getPos3()
					+ "\nAdjunto el Recibo Nro. " + this.reciboDto.getNumero()
					+ "\nde fecha: " + Utiles.getDateToString((Date) this.selectedItem.getPos1(), "dd-MM-yyyy") + "\n"
					+ "\n" + asunto,
					"", "", "ReciboDigital" + ".pdf",
					root + "/yhaguy/archivos/recibos/" + this.reciboDto.getNumero() + ".pdf", null, null);	
			Clients.showNotification("Correo Enviado");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaAA",
			"filterNumero", "filterRazonSocial", "filterRuc", 
			"filterCaja", "selectedFiltro", "filterCobrador" })
	public List<MyArray> getRecibos() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		this.totalImporteGs = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> recibos = new ArrayList<Recibo>();
		
		if (this.selectedFiltro.equals(FILTRO_RECIBOS)) {
			recibos = rr.getRecibos(this.getFilterFecha(),
					this.filterNumero, this.filterRazonSocial, this.filterRuc, this.filterCaja, this.filterCobrador);
		}
		
		if (this.selectedFiltro.equals(FILTRO_REEMBOLSOS_CC)) {
			recibos = rr.getReembolsosPrestamosCC(this.getFilterFecha(),
					this.filterNumero, this.filterRazonSocial, this.filterRuc, this.filterCaja, this.filterCobrador);
		}
		
		if (this.selectedFiltro.equals(FILTRO_REEMBOLSOS_CHEQUES_RECH)) {
			recibos = rr.getReembolsosChequesRechazados(this.getFilterFecha(),
					this.filterNumero, this.filterRazonSocial, this.filterRuc, this.filterCaja, this.filterCobrador);
		}
		
		if (this.selectedFiltro.equals(FILTRO_ANTICIPOS)) {
			recibos = rr.getAnticipos(this.getFilterFecha(),
					this.filterNumero, this.filterRazonSocial, this.filterRuc, this.filterCaja, this.filterCobrador);
		}
		
		for (Recibo recibo : recibos) {			
			MyArray my = new MyArray();
			List<MyArray> dets = new ArrayList<MyArray>();
			List<MyArray> fpgs = new ArrayList<MyArray>();
			my.setId(recibo.getId());
			my.setPos1(recibo.getFechaEmision());
			my.setPos2(recibo.getNumero());
			my.setPos3(recibo.getCliente().getRazonSocial());
			my.setPos4(recibo.getCliente().getRuc());
			my.setPos5(recibo.getTotalImporteGs());
			my.setPos6(recibo.getNumeroPlanilla());
			my.setPos7(recibo.getTipoMovimiento().getDescripcion());			
			for (ReciboDetalle det : recibo.getDetalles()) {
				if (det.getMovimiento() != null) {
					dets.add(new MyArray(this.m.dateToString(det.getMovimiento()
							.getFechaEmision(), Misc.DD_MM_YYYY), this.m
							.dateToString(
									det.getMovimiento().getFechaVencimiento(),
									Misc.DD_MM_YYYY), det.getMovimiento()
							.getNroComprobante(), det.getMontoGs(), det
							.getMontoGs()));
				}				
			}
			for (ReciboFormaPago fp : recibo.getFormasPago()) {
				MyArray fmp = new MyArray(fp.getDescripcion(), fp.getMontoGs(), "", "", "");
				if (fp.isChequeTercero()) {
					fmp = new MyArray(fp.getDescripcion(), fp.getMontoGs(),
							Utiles.getDateToString(fp.getFechaOperacion(), Utiles.DD_MM_YYYY),
							Utiles.getDateToString(fp.getChequeFecha(), Utiles.DD_MM_YYYY),
							fp.getChequeBanco().getDescripcion());
				}
				if (fp.isDepositoBancario()) {
					fmp = new MyArray(fp.getDescripcion(), fp.getMontoGs(),
							Utiles.getDateToString(fp.getFechaOperacion(), Utiles.DD_MM_YYYY),
							"",
							fp.getDepositoBancoCta().getBancoDescripcion());
				}
				fpgs.add(fmp);
			}
			my.setPos8(dets);
			my.setPos9(fpgs);
			my.setPos10(recibo.isAnulado());	
			my.setPos11(recibo.getCobrador());
			my.setPos12(recibo.getMoneda().getDescripcion());
			my.setPos13(recibo.getTipoCambio());
			my.setPos14(recibo.getCliente().getEmpresa().getCorreo_());
			my.setPos15(recibo.getObservacion());
			out.add(my);
			this.totalImporteGs += recibo.getTotalImporteGs();
		}
		this.listSize = out.size();
		BindUtils.postNotifyChange(null, null, this, "listSize");
		BindUtils.postNotifyChange(null, null, this, "totalImporteGs");
		return out;
	}
	
	/**
	 * reporte de recibos..
	 */
	private void reporteRecibos() throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();
		for (MyArray recibo : this.getRecibos()) {
			boolean anulado = (boolean) recibo.getPos10();
			Object[] obj = new Object[] {
					Utiles.getDateToString((Date) recibo.getPos1(), Utiles.DD_MM_YY),
					recibo.getPos2(),
					anulado ? "ANULADO.." : recibo.getPos3().toString().toUpperCase(),
					recibo.getPos4(),
					recibo.getPos5()};
			data.add(obj);
		}

		ReporteRecibos rep = new ReporteRecibos(this.getAcceso().getSucursalOperativa().getText());
		rep.setDatosReporte(data);
		rep.setBorrarDespuesDeVer(true);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/**
	 * Despliega el Reporte de Recibo..
	 */
	private void imprimirRecibo() throws Exception {
		this.reciboDto = (ReciboDTO) this.getDTOById(Recibo.class.getName(), this.selectedItem.getId(), new AssemblerRecibo());
		
		String source = ReportesViewModel.SOURCE_RECIBO;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ReciboDataSource();
		params.put("title", this.reciboDto.getTipoMovimiento().getPos1());
		params.put("fieldRazonSocial", this.reciboDto.isCobro() ? "Recibí de:" : "A la Orden de:");
		params.put("RazonSocial", this.reciboDto.getRazonSocial());
		params.put("Ruc", this.reciboDto.getRuc());
		params.put("NroRecibo", this.reciboDto.getNumero());
		params.put("ImporteEnLetra", this.reciboDto.getImporteEnLetras());
		params.put("TotalImporteGs", Utiles.getNumberFormat(this.reciboDto.getTotalImporteGs()));
		this.imprimirComprobante(source, params, dataSource, this.selectedFormato);
	}
	
	/**
	 * genera el PDF.
	 */
	private void generatePDF() throws Exception {
		this.reciboDto = (ReciboDTO) this.getDTOById(Recibo.class.getName(), this.selectedItem.getId(), new AssemblerRecibo());
		
		this.reciboDto.setObservacion("RECIBO DIGITAL");
		this.saveDTO(this.reciboDto, new AssemblerRecibo());
		this.selectedItem.setPos15("RECIBO DIGITAL");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "RECIBO DIGITAL");
		params.put("fieldRazonSocial", this.reciboDto.isCobro() ? "Recibí de:" : "A la Orden de:");
		params.put("RazonSocial", this.reciboDto.getRazonSocial());
		params.put("Ruc", this.reciboDto.getRuc());
		params.put("NroRecibo", this.reciboDto.getNumero());
		params.put("ImporteEnLetra", this.reciboDto.getImporteEnLetras());
		params.put("TotalImporteGs", Utiles.getNumberFormat(this.reciboDto.getTotalImporteGs()));
		params.put("Emision", Utiles.getDateToString(this.reciboDto.getFechaEmision(), "dd-MM-yyyy"));
		params.put("Footer", Configuracion.empresa + " - " + "Ruta Mcal. Estigarribia Km 9.5");
		params.put("Empresa", Configuracion.empresa);
		params.put("RucEmpresa", Utiles.getRucEmpresa());
		
		try {
			String root = Sessions.getCurrent().getWebApp().getRealPath("/");
			JasperDesign jasperDesign = JRXmlLoader.load(root + "/reportes/jasper/ReciboDigital.jrxml");
            JasperReport jasperReport =(JasperReport)JasperCompileManager.compileReport(jasperDesign);
            
            DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
            JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.awt.ignore.missing.font","true");
            
            JRDataSource dataSource = new ReciboDataSource();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, root + "/yhaguy/archivos/recibos/" + this.reciboDto.getNumero() + ".pdf");
	        
		} catch (Exception e) {
			e.printStackTrace();
		}	
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
		
		String root = Sessions.getCurrent().getWebApp().getRealPath("/");

		public ReciboDataSource() {
			for (ReciboDetalleDTO item : reciboDto.getDetalles()) {
				MyArray m = new MyArray();
				m.setPos1(item.getFecha());
				m.setPos2(item.getDescripcion());
				m.setPos3(item.getMontoGs());
				m.setPos4("Facturas");
				this.detalle.add(m);
			}
			for (ReciboFormaPagoDTO item : reciboDto.getFormasPago()) {
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
				double importe = reciboDto.getTotalImporteGs();
				value = Utiles.getNumberFormat(importe);
			} else if ("TipoDetalle".equals(fieldName)) {
				value = item.getPos4();
			}  else if ("logo".equals(fieldName)) {
				value = root + "/logo.png";
			} else if ("dCarQR".equals(fieldName)) {
				value = getCurrentURL() + "/yhaguy/archivos/recibos/" + reciboDto.getNumero() + ".pdf";
				System.out.println(value);
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
		private String cliente;
		private String cobrador;
		private String moneda;
		private double tipoCambio;
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

		public String getCliente() {
			return cliente;
		}

		public void setCliente(String cliente) {
			this.cliente = cliente.toUpperCase();
		}

		public List<MyArray> getFormasPago() {
			return formasPago;
		}

		public void setFormasPago(List<MyArray> formasPago) {
			this.formasPago = formasPago;
		}

		public String getCobrador() {
			return cobrador;
		}

		public void setCobrador(String cobrador) {
			this.cobrador = cobrador;
		}

		public String getMoneda() {
			return moneda;
		}

		public void setMoneda(String moneda) {
			this.moneda = moneda;
		}

		public double getTipoCambio() {
			return tipoCambio;
		}

		public void setTipoCambio(double tipoCambio) {
			this.tipoCambio = tipoCambio;
		}			
	}	
	
	/**
	 * GETS / SETS
	 */
	
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
	 * @return url
	 */
	private String getCurrentURL() {
		String port = (Executions.getCurrent().getServerPort() == 80) ? ""
				: (":" + Executions.getCurrent().getServerPort());
		String url = Executions.getCurrent().getScheme() + "://" + Executions.getCurrent().getServerName() + port
				+ Executions.getCurrent().getContextPath();
		return url;
	}
	
	/**
	 * @return correo valido..
	 */
	private boolean isValido(String correo) {
		try {
			InternetAddress emailAddr = new InternetAddress(correo);
		    emailAddr.validate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @return url digital
	 */
	public String getUrl(String numero) {
		return this.getCurrentURL() + "/yhaguy/archivos/recibos/" + numero + ".pdf";
	}
	
	@DependsOn({ "detalle", "fechaCierre" })
	public boolean isGuardarHabilitado() {
		if (this.detalle == null) {
			return false;
		}
		return this.detalle.getEmision().compareTo(this.fechaCierre) > 0;
	}
	
	private AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}
	
	/**
	 * @return true si la operacion es habilitada..
	 */
	public boolean isOperacionHabilitada(String operacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.isOperacionHabilitada(this.getLoginNombre(), operacion);
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
	
	/**
	 * @return los cobradores..
	 */
	public List<String> getCobradores() throws Exception {
		List<String> out = new ArrayList<String>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Funcionario func : rr.getFuncionariosCobradores()) {
			out.add(func.getRazonSocial().toUpperCase());
		}
		return out;
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

	public String getSelectedFiltro() {
		return selectedFiltro;
	}

	public void setSelectedFiltro(String selectedFiltro) {
		this.selectedFiltro = selectedFiltro;
	}

	public String getFilterCobrador() {
		return filterCobrador;
	}

	public void setFilterCobrador(String filterCobrador) {
		this.filterCobrador = filterCobrador;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public boolean isSinCorreo() {
		return sinCorreo;
	}

	public void setSinCorreo(boolean sinCorreo) {
		this.sinCorreo = sinCorreo;
	}
}

/**
 * Reporte de Recibos..
 */
class ReporteRecibos extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 40);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 40);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE, 40, true);

	private String sucursal;

	public ReporteRecibos(String sucursal) {
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
		this.setTitulo("Recibos de Cobros");
		this.setDirectorio("recibos");
		this.setNombreArchivo("recibo-");
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
