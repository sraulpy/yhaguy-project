package com.yhaguy.gestion.pagos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.roshka.sifen.core.beans.DocumentoElectronico;
import com.roshka.sifen.core.exceptions.SifenException;
import com.yhaguy.Configuracion;
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
import com.yhaguy.sifen.FacturaDataSource;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

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
			my.setPos5(pago.isMonedaLocal() ? pago.getTotalImporteGs() : pago.getTotalImporteDs());
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
			my.setPos12(pago.isAnulado());
			my.setPos13(pago.getMoneda().getSigla());
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
		
		if (CajaUtil.CAJAS_ABIERTAS.get(this.selectedItem.getPos6()) != null) {
			String msg = "CAJA BLOQUEADA POR USUARIO: " + CajaUtil.CAJAS_ABIERTAS.get(this.selectedItem.getPos6());
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
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
			
			if (CajaUtil.CAJAS_ABIERTAS.get(this.selectedItem.getPos6()) != null) {
				String msg = "CAJA BLOQUEADA POR USUARIO: " + CajaUtil.CAJAS_ABIERTAS.get(this.selectedItem.getPos6());
				Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
			
			String numeroRecibo = ((String) this.nvoRecibo.getPos1()).toUpperCase();
			Date fechaRecibo = (Date) this.nvoRecibo.getPos2();
			long idOrdenPago = this.selectedItem.getId();
			String user = this.getLoginNombre();
			AssemblerRecibo.registrarReciboPago(numeroRecibo, fechaRecibo, idOrdenPago, user, false, false);			
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
		params.put("fieldRazonSocial", this.pagoDto.isCobro() ? "Recibí de:" : "A la Orden de:");
		params.put("RazonSocial", this.pagoDto.getRazonSocial());
		params.put("Ruc", this.pagoDto.getRuc());
		params.put("NroRecibo", this.pagoDto.getNumero());
		params.put("Moneda", this.pagoDto.isMonedaLocal() ? "Guaraníes:" : "Dólares:");
		params.put("Moneda_", this.pagoDto.isMonedaLocal() ? "Gs." : "U$D");
		params.put("ImporteEnLetra", this.pagoDto.isMonedaLocal() ? this.pagoDto.getImporteEnLetras() : this.pagoDto.getImporteEnLetrasDs());
		params.put("TotalImporteGs",
				this.pagoDto.isMonedaLocal() ? Utiles.getNumberFormat(this.pagoDto.getTotalImporteGs())
						: Utiles.getNumberFormatDs(this.pagoDto.getTotalImporteDs()));
		params.put("Usuario", this.getUs().getNombre());
		this.imprimirComprobante(source, params, dataSource, this.selectedFormato);
	}
	
	@Command
	public void test() {
		try {
			String xml = new String(Files.readAllBytes(Paths.get("C:\\facturacionelectronica\\DE.xml")), StandardCharsets.UTF_8);
			DocumentoElectronico DE = new DocumentoElectronico(xml);
			String source = "/reportes/jasper/Factura.jasper";
			Map<String, Object> params = new HashMap<String, Object>();
			JRDataSource dataSource = new TestDataSource(DE);
			this.imprimirComprobante(source, params, dataSource, this.selectedFormato);
		} catch (IOException | SifenException e) {
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
		
		try {
			String root = Sessions.getCurrent().getWebApp().getRealPath("/");
			
			JasperDesign jasperDesign = JRXmlLoader.load(root + "/reportes/jasper/Factura.jrxml");
	        JasperReport jasperReport =(JasperReport)JasperCompileManager.compileReport(jasperDesign);
	        Map<String,Object> parameters = new HashMap<String,Object>();
	       
	        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
	        JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\wamp64\\www\\test\\Factura.pdf");
	        
		} catch (Exception e) {
			e.printStackTrace();
		}		

		Executions.getCurrent().sendRedirect("http://localhost/test/Factura.pdf", "_blank");
	}
	
	public static void main(String[] args)
    {

        try {
            JasperDesign jasperDesign = JRXmlLoader.load("C:\\projects\\yhaguy-baterias\\reportes\\jasper\\Factura.jrxml");
            JasperReport jasperReport =(JasperReport)JasperCompileManager.compileReport(jasperDesign);
            Map<String,Object> parameters = new HashMap<String,Object>();
            
            JRDataSource dataSource = new FacturaDataSource();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\tmp\\Factura.pdf");
            
        
            }catch(Exception e)
            {
                e.printStackTrace();
            }
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
				m.setPos3(pagoDto.isMonedaLocal() ? item.getMontoGs() : item.getMontoDs());
				m.setPos4("Facturas");
				this.detalle.add(m);
			}
			for (ReciboFormaPagoDTO item : pagoDto.getFormasPago()) {
				MyArray my = new MyArray();
				my.setPos1(m.dateToString(item.getModificado(), Misc.DD_MM_YYYY));
				my.setPos2(item.getDescripcion());
				if (!item.getBancoCta().esNuevo()) {
					my.setPos2(item.getDescripcion() + " " + item.getBancoCta().getBancoDescripcion() + " " + item.getBancoCta().getNroCuenta());
				}
				my.setPos3(pagoDto.isMonedaLocal() ? item.getMontoGs() : item.getMontoDs());
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
				value = pagoDto.isMonedaLocal() ? Utiles.getNumberFormat(importe) : Utiles.getNumberFormatDs(importe);
			} else if ("TotalImporte".equals(fieldName)) {
				double importe = pagoDto.isMonedaLocal() ? pagoDto.getTotalImporteGs() : pagoDto.getTotalImporteDs();
				value = pagoDto.isMonedaLocal() ? Utiles.getNumberFormat(importe) : Utiles.getNumberFormatDs(importe);
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
	 * DataSource..
	 */
	public class TestDataSource implements JRDataSource {

		DocumentoElectronico DE;
		
		String dNomEmi = "Yhaguy Repuestos S.A.";
		String dDirEmi = "Av. Mariscal José Félix Estigarribia, Fernando de la Mora";
		String dTelEmi = "021 507 857";
		String dEmailE = "info@yhaguyrepuestos.com.py";
		String dRucEm = "80024884";
		String dDVEmi = "8";
		String dNumTim = "12560724";
		String V_dFeIniT = "30/03/2023";
		String dDesTiDE = "Factura electrónica";
		String dEst = "001"; String dPunExp = "001"; String dNumDoc = "0000001";
		
		String V_dFeEmiDE = "25/04/2023 22:15"; String dDCondOpe = "Crédito";
		String iNatRec = "2"; String dRucRec = "";
		String dDVRec = ""; String dNumIDRec = "4579993";
		String dNomRec = "Martin Zarza"; String iCondCred = "1";
		String dCuotas = "1"; String dPlazoCre = "30 días";
		String dDirRec = ""; int iCondOpe = 2; 
		String dDCondCred = "Plazo"; String cMoneOpe = "PYG";
		String dTelRec = ""; String dDesTipTra = "Venta de mercadería";
		double dSub10 = 240000; double dSub5 = 0; double dSubExe = 0;
		double dTotOpe = 240000; double dTotGralOpe = 240000; 
		double dIVA5 = 0; double dIVA10 = 21818;
		String dCarQR = "https://ekuatia.set.gov.py/consultas-test/qr?nVersion=150&Id=01800248848001001000000122023042516821921089&dFeEmiDE=323032332d30342d32355431363a32323a3135&dNumIDRec=4579993&dTotGralOpe=240000&dTotIVA=21818&cItems=2&DigestValue=5377725a507678336b43566642774665416b734f36374e714b63354f4c506d476a706657587561514d30553d&IdCSC=0002&cHashQR=1c1c24a178fa831b4de130e6336d2d874ff93e543a706d9ec95d63b40e1686b4";
		String Id = "01800248848001001000000122023042516821921089";
		String logo = "C:\\tmp\\logo.png";
		
		List<Object[]> detalle = new ArrayList<Object[]>();

		public TestDataSource(DocumentoElectronico DE) {
			this.DE = DE; 
			List<Object[]> list = new ArrayList<Object[]>();
			list.add(new Object[] { "001", "BATERÍA MARCA LUCAS 90AMP", "UNI", 120000.0, 1, 0.0, 10, 120000.0 });
			list.add(new Object[] { "002", "LUBRICANTE MARCA VALVOLINE 1LT", "UNI", 120000.0, 1, 0.0, 10, 120000.0 });
			this.detalle = list;
		}

		private int index = -1;
		
		

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			Object[] item = this.detalle.get(index);

			if ("dNomEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdNomEmi();
			} else if ("dDirEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdDirEmi();
			} else if ("dTelEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdTelEmi();
			} else if ("dEmailE".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdEmailE();
			} else if ("dRucEm".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdRucEm();
			} else if ("dDVEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdDVEmi();
			} else if ("dNumTim".equals(fieldName)) {
				value = this.DE.getgTimb().getdNumTim();
			} else if ("V_dFeIniT".equals(fieldName)) {
				value = this.DE.getgTimb().getdFeIniT();
			} else if ("dDesTiDE".equals(fieldName)) {
				value = this.DE.getgTimb().getiTiDE().getDescripcion();
			} else if ("dEst".equals(fieldName)) {
				value = this.DE.getgTimb().getdEst();
			} else if ("dPunExp".equals(fieldName)) {
				value = this.DE.getgTimb().getdPunExp();
			} else if ("dNumDoc".equals(fieldName)) {
				value = this.DE.getgTimb().getdNumDoc();
			} else if ("V_dFeEmiDE".equals(fieldName)) {
				value = V_dFeEmiDE;
			} else if ("dDCondOpe".equals(fieldName)) {
				value = dDCondOpe;
			} else if ("iNatRec".equals(fieldName)) {
				value = Integer.parseInt(iNatRec);
			} else if ("dRucRec".equals(fieldName)) {
				value = dRucRec;
			} else if ("dNumIDRec".equals(fieldName)) {
				value = dNumIDRec;
			} else if ("dNomRec".equals(fieldName)) {
				value = dNomRec;
			} else if ("iCondCred".equals(fieldName)) {
				value = Integer.parseInt(iCondCred);
			} else if ("dCuotas".equals(fieldName)) {
				value = dCuotas;
			} else if ("dPlazoCre".equals(fieldName)) {
				value = dPlazoCre;
			} else if ("dDirRec".equals(fieldName)) {
				value = dDirRec;
			} else if ("dDCondCred".equals(fieldName)) {
				value = dDCondCred;
			} else if ("iCondOpe".equals(fieldName)) {
				value = iCondOpe;
			} else if ("cMoneOpe".equals(fieldName)) {
				value = cMoneOpe;
			} else if ("dDesTipTra".equals(fieldName)) {
				value = dDesTipTra;
			} else if ("dCodInt".equals(fieldName)) {
				value = item[0];
			} else if ("dDesProSer".equals(fieldName)) {
				value = item[1];
			}  else if ("dDesUniMed".equals(fieldName)) {
				value = item[2];
			}  else if ("dPUniProSer".equals(fieldName)) {
				value = item[3];
			}  else if ("dCantProSer".equals(fieldName)) {
				value = item[4];
			}  else if ("dDescItem".equals(fieldName)) {
				value = item[5];
			}  else if ("dTasaIVA".equals(fieldName)) {
				value = item[6];
			}  else if ("dTotOpeItem".equals(fieldName)) {
				value = item[7];
			}  else if ("dTotOpe".equals(fieldName)) {
				value = dTotOpe;
			}  else if ("dTotGralOpe".equals(fieldName)) {
				value = dTotGralOpe;
			}  else if ("dSub10".equals(fieldName)) {
				value = dSub10;
			}  else if ("dSub5".equals(fieldName)) {
				value = dSub5;
			}  else if ("dSubExe".equals(fieldName)) {
				value = dSubExe;
			}  else if ("dIVA5".equals(fieldName)) {
				value = dIVA5;
			}  else if ("dIVA10".equals(fieldName)) {
				value = dIVA10;
			}  else if ("dCarQR".equals(fieldName)) {
				value = dCarQR;
			}  else if ("Id".equals(fieldName)) {
				value = Id;
			}  else if ("logo".equals(fieldName)) {
				value = logo;
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

