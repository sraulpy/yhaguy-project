package com.yhaguy.gestion.sifen;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.roshka.sifen.core.SifenConfig;
import com.roshka.sifen.core.beans.DocumentoElectronico;
import com.roshka.sifen.core.exceptions.SifenException;
import com.roshka.sifen.core.fields.request.de.TgActEco;
import com.roshka.sifen.core.fields.request.de.TgCamItem;
import com.roshka.sifen.core.types.TTiDE;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Remision;
import com.yhaguy.domain.Vehiculo;
import com.yhaguy.domain.Venta;
import com.yhaguy.sifen.SifenNC;
import com.yhaguy.sifen.SifenParams;
import com.yhaguy.sifen.SifenREM;
import com.yhaguy.sifen.SifenTest;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.Utiles;

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

public class SifenViewModel extends SimpleViewModel {
	
	static final boolean TESTING = false;
	
	private Date filterDesde;
	private Date filterHasta;
	
	private Object[] selectedItem;
	
	private List<Funcionario> choferes;
	private List<Vehiculo> vehiculos;
	
	private Funcionario selectedChofer;
	private Vehiculo selectedVehiculo;

	@Init(superclass = true)
	public void init() {
		this.filterDesde = new Date();
		this.filterHasta = new Date();		
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.choferes = rr.getChoferes("");
			this.vehiculos = rr.getVehiculosSucursal(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.configSifen(TESTING);
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void generarFE(@BindingParam("bean") Object[] bean) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Venta venta = (Venta) rr.getObject(Venta.class.getName(), (long) bean[8]);
			SifenTest test = new SifenTest();
			test.testRecepcionDE(venta, false, TESTING);
			this.generarPDFFE(venta);			
			rr.saveObject(venta, this.getLoginNombre());
			bean[9] = venta.getRespuestaSET();
			bean[10] = venta.getUrl();
			BindUtils.postNotifyChange(null, null, bean, "*");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	public void generarFEAsync(@BindingParam("bean") Object[] bean) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Venta venta = (Venta) rr.getObject(Venta.class.getName(), (long) bean[8]);
			SifenTest test = new SifenTest();
			test.testRecepcionDE(venta, true, TESTING);
			this.imprimirFE(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	public void generarNRE(@BindingParam("bean") Object[] bean) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Remision rem = (Remision) rr.getObject(Remision.class.getName(), (long) bean[8]);
			rem.setChofer((Funcionario) bean[11]);
			rem.setVehiculo_((Vehiculo) bean[12]);
			SifenREM sf = new SifenREM();
			sf.testRecepcionDE(rem, false);
			this.generarPDFNRE(rem);			
			rr.saveObject(rem, this.getLoginNombre());
			bean[9] = rem.getRespuestaSET();
			bean[10] = rem.getUrl();
			BindUtils.postNotifyChange(null, null, bean, "*");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	public void generarNREAsync(@BindingParam("bean") Object[] bean) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			Remision rem = (Remision) rr.getObject(Remision.class.getName(), (long) bean[8]);
			rem.setChofer((Funcionario) bean[11]);
			rem.setVehiculo_((Vehiculo) bean[12]);
			SifenREM sf = new SifenREM();
			sf.testRecepcionDE(rem, true);
			this.imprimirNRE(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	public void generarNCR(@BindingParam("bean") Object[] bean) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			NotaCredito ncred = (NotaCredito) rr.getObject(NotaCredito.class.getName(), (long) bean[8]);
			SifenNC test = new SifenNC();
			test.testRecepcionDE(ncred);
			this.generarPDFNCR(ncred);			
			rr.saveObject(ncred, this.getLoginNombre());
			bean[9] = ncred.getRespuestaSET();
			bean[10] = ncred.getUrl();
			BindUtils.postNotifyChange(null, null, bean, "*");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	public void imprimirFE(@BindingParam("bean") Object[] bean) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta venta = (Venta) rr.getObject(Venta.class.getName(), (long) bean[8]);
		this.generarPDFFE(venta);
		Executions.getCurrent().sendRedirect(this.getUrl(venta.getNumero()), "_blank");
	}
	
	@Command
	public void imprimirNRE(@BindingParam("bean") Object[] bean) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Remision rem = (Remision) rr.getObject(Remision.class.getName(), (long) bean[8]);
		this.generarPDFNRE(rem);
		Executions.getCurrent().sendRedirect(this.getUrlNRE(rem.getNumero()), "_blank");
	}
	
	@Command
	public void imprimirNCR(@BindingParam("bean") Object[] bean) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		NotaCredito ncr = (NotaCredito) rr.getObject(NotaCredito.class.getName(), (long) bean[8]);
		this.generarPDFNCR(ncr);
		Executions.getCurrent().sendRedirect(this.getUrlNCR(ncr.getNumero()), "_blank");
	}
	
	@Command
	@NotifyChange("*")
	public void actualizar() {
		Clients.showNotification("DATOS ACTUALIZADOS");
	}
	
	@Command
	@NotifyChange("selectedItem")
	public void openPopup(@BindingParam("bean") Object[] bean, @BindingParam("pop") Popup pop,
			@BindingParam("parent") Component parent) {
		this.selectedItem = bean;
		pop.open(parent, "after_end");
	}
	
	@Command
	@NotifyChange("selectedItem")
	public void sendFE() {		
		String destino = (String) this.selectedItem[11];
		
		if (!destino.trim().isEmpty()) {
			boolean valido = this.isValido(destino);
			if (!valido) {
				Clients.showNotification("Correo no válido", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
		}		
		
		if (destino.trim().isEmpty()) {
			destino = "sergioa@yhaguyrepuestos.com.py";
		}
		
		String[] send = new String[] { destino };
		String[] sendCC = new String[] { "estelap@yhaguyrepuestos.com.py", "vanesar@yhaguyrepuestos.com.py",
				"rodrigol@yhaguyrepuestos.com.py" };
		String[] sendCCO = new String[] { "sergioa@yhaguyrepuestos.com.py" };
		try {
			String asunto = "Factura Electrónica - " + Configuracion.empresa;
			String root = Sessions.getCurrent().getWebApp().getRealPath("/");			
			EnviarCorreo enviarCorreo = new EnviarCorreo();			
			enviarCorreo.sendMessage(send, sendCC, sendCCO,
					asunto, "Estimado Cliente: " + this.selectedItem[12]
					+ "\nAdjunto su comprobante electrónico " + this.selectedItem[2]
					+ "\n" + asunto, "", "", "FacturaElectronica" + ".pdf",
					root + "/yhaguy/archivos/sifen/FE/" + this.selectedItem[2] + ".pdf", 
					"FacturaElectronica" + ".xml",
					"C:\\facturacionelectronica\\" + this.selectedItem[2] + ".xml");
			Clients.showNotification("Correo Enviado");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	@NotifyChange("selectedItem")
	public void sendNRE() {		
		String destino = (String) this.selectedItem[13];
		
		if (!destino.trim().isEmpty()) {
			boolean valido = this.isValido(destino);
			if (!valido) {
				Clients.showNotification("Correo no válido", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
		}		
		
		if (destino.trim().isEmpty()) {
			destino = "sergioa@yhaguyrepuestos.com.py";
		}
		
		String[] send = new String[] { destino };
		String[] sendCC = new String[] { "estelap@yhaguyrepuestos.com.py", "vanesar@yhaguyrepuestos.com.py",
		"rodrigol@yhaguyrepuestos.com.py" };
		String[] sendCCO = new String[] { "sergioa@yhaguyrepuestos.com.py" };
		try {
			String asunto = "Nota de Remisión Electrónica - " + Configuracion.empresa;
			String root = Sessions.getCurrent().getWebApp().getRealPath("/");			
			EnviarCorreo enviarCorreo = new EnviarCorreo();			
			enviarCorreo.sendMessage(send, sendCC, sendCCO,
					asunto, "Estimado Cliente: " + this.selectedItem[4]
					+ "\nAdjunto su comprobante electrónico " + this.selectedItem[2]
					+ "\n" + asunto, "", "", "NotaRemisionElectronica" + ".pdf",
					root + "/yhaguy/archivos/sifen/NRE/" + this.selectedItem[2] + ".pdf", 
					"NotaRemisionElectronica" + ".xml",
					"C:\\notasremision\\" + this.selectedItem[2] + ".xml");
			Clients.showNotification("Correo Enviado");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	@NotifyChange("selectedItem")
	public void sendNCR() {		
		String destino = (String) this.selectedItem[11];
		
		if (!destino.trim().isEmpty()) {
			boolean valido = this.isValido(destino);
			if (!valido) {
				Clients.showNotification("Correo no válido", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
		}		
		
		if (destino.trim().isEmpty()) {
			destino = "sergioa@yhaguyrepuestos.com.py";
		}
		
		String[] send = new String[] { destino };
		String[] sendCC = new String[] { "estelap@yhaguyrepuestos.com.py", "vanesar@yhaguyrepuestos.com.py",
		"rodrigol@yhaguyrepuestos.com.py" };
		String[] sendCCO = new String[] { "sergioa@yhaguyrepuestos.com.py" };
		try {
			String asunto = "Nota de Crédito Electrónica - " + Configuracion.empresa;
			String root = Sessions.getCurrent().getWebApp().getRealPath("/");			
			EnviarCorreo enviarCorreo = new EnviarCorreo();			
			enviarCorreo.sendMessage(send, sendCC, sendCCO,
					asunto, "Estimado Cliente: " + this.selectedItem[4]
					+ "\nAdjunto su comprobante electrónico " + this.selectedItem[2]
					+ "\n" + asunto, "", "", "NotaCreditoElectronica" + ".pdf",
					root + "/yhaguy/archivos/sifen/NCR/" + this.selectedItem[2] + ".pdf", 
					"NotaCreditoElectronica" + ".xml",
					"C:\\notascredito\\" + this.selectedItem[2] + ".xml");
			Clients.showNotification("Correo Enviado");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	@NotifyChange("selectedItem")
	public void checkDatosRemision(@BindingParam("pop") Popup pop) {
		this.selectedItem[11] = this.selectedChofer;
		this.selectedItem[12] = this.selectedVehiculo;
		pop.close();
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
	 * @return url digital
	 */
	public String getUrl(String numero) {
		return this.getCurrentURL() + "/yhaguy/archivos/sifen/FE/" + numero + ".pdf";
	}
	
	/**
	 * @return url digital
	 */
	public String getUrlNRE(String numero) {
		return this.getCurrentURL() + "/yhaguy/archivos/sifen/NRE/" + numero + ".pdf";
	}
	
	/**
	 * @return url digital
	 */
	public String getUrlNCR(String numero) {
		return this.getCurrentURL() + "/yhaguy/archivos/sifen/NCR/" + numero + ".pdf";
	}
	
	/**
	 * genera el PDF de la FE..
	 */
	private void generarPDFFE(Venta bean) {
		try {
			String path = SifenParams.PATH_FACTURAS + bean.getNumero() + ".xml";
			
			
			String xml = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
			DocumentoElectronico DE = new DocumentoElectronico(xml);
			DE.setEnlaceQR(Utiles.parseXML(path, "dCarQR"));
			JRDataSource dataSource = new FacturaDataSource(DE);

			String root = Sessions.getCurrent().getWebApp().getRealPath("/");

			JasperDesign jasperDesign = JRXmlLoader.load(root + "/reportes/jasper/Factura.jrxml");
			JasperReport jasperReport = (JasperReport) JasperCompileManager.compileReport(jasperDesign);
			Map<String, Object> parameters = new HashMap<String, Object>();

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			JasperExportManager.exportReportToPdfFile(jasperPrint,
					root + "/yhaguy/archivos/sifen/FE/" + bean.getNumero() + ".pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * genera el PDF de la NRE..
	 */
	private void generarPDFNRE(Remision bean) {
		try {
			String path = SifenParams.PATH_NOTAS_REMISION + bean.getNumero() + ".xml";
			String xml = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
			DocumentoElectronico DE = new DocumentoElectronico(xml);
			DE.setEnlaceQR(Utiles.parseXML(path, "dCarQR"));
			JRDataSource dataSource = new FacturaDataSource(DE);

			String root = Sessions.getCurrent().getWebApp().getRealPath("/");

			JasperDesign jasperDesign = JRXmlLoader.load(root + "/reportes/jasper/NotaRemision.jrxml");
			JasperReport jasperReport = (JasperReport) JasperCompileManager.compileReport(jasperDesign);
			Map<String, Object> parameters = new HashMap<String, Object>();

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			JasperExportManager.exportReportToPdfFile(jasperPrint,
					root + "/yhaguy/archivos/sifen/NRE/" + bean.getNumero() + ".pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * genera el PDF de la FE..
	 */
	private void generarPDFNCR(NotaCredito bean) {
		try {
			String path = SifenParams.PATH_NOTAS_CREDITO + bean.getNumero() + ".xml";
			String xml = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
			DocumentoElectronico DE = new DocumentoElectronico(xml);
			DE.setEnlaceQR(Utiles.parseXML(path, "dCarQR"));
			JRDataSource dataSource = new FacturaDataSource(DE);

			String root = Sessions.getCurrent().getWebApp().getRealPath("/");

			JasperDesign jasperDesign = JRXmlLoader.load(root + "/reportes/jasper/NotaCreditoSifen.jrxml");
			JasperReport jasperReport = (JasperReport) JasperCompileManager.compileReport(jasperDesign);
			Map<String, Object> parameters = new HashMap<String, Object>();

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			JasperExportManager.exportReportToPdfFile(jasperPrint,
					root + "/yhaguy/archivos/sifen/NCR/" + bean.getNumero() + ".pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * config sifen
	 */
	private void configSifen(boolean testing) {
		try {
			SifenConfig config = null;			
			
			if (this.isEmpresaYRSA()) {
				
				if (!testing) {
					config = new SifenConfig(SifenConfig.TipoAmbiente.PROD, "0001", // ID CSC
							"Dc9458D44421F13B398E2A29f6D292c7", // CSC
							SifenConfig.TipoCertificadoCliente.PFX, SifenParams.SIFEN_DIR + "firma_yrsa.p12",
							"saturnina");
				} else {
					config = new SifenConfig(SifenConfig.TipoAmbiente.DEV, "0001", // ID CSC
							"ABCD0000000000000000000000000000", // CSC EFGH0000000000000000000000000000
							SifenConfig.TipoCertificadoCliente.PFX, 
							SifenParams.SIFEN_DIR + "firma_yrsa.p12", // 
							"saturnina");
				}		
							
			} else {
				config = new SifenConfig(SifenConfig.TipoAmbiente.DEV, "0001", // ID CSC
						"ABCD0000000000000000000000000000", // CSC EFGH0000000000000000000000000000
						SifenConfig.TipoCertificadoCliente.PFX, 
						SifenParams.SIFEN_DIR + "groupauto.p12", // 
						"gr80124to");
			}			
			config.setHabilitarNotaTecnica13(true);
			com.roshka.sifen.Sifen.setSifenConfig(config);
			
		} catch (SifenException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * DataSource..
	 */
	public class FacturaDataSource implements JRDataSource {

		DocumentoElectronico DE;		
		String dCuotas = "1"; 
		String dPlazoCre = "30 días"; 
		
		List<TgCamItem> detalle = new ArrayList<TgCamItem>();

		public FacturaDataSource(DocumentoElectronico DE) {
			this.DE = DE; 
			this.detalle = this.DE.getgDtipDE().getgCamItemList();
		}

		private int index = -1;		

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			TgCamItem item = this.detalle.get(index);

			if ("dNomEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdNomEmi();
			} else if ("dDirEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdDirEmi();
			} else if ("dDesCiuEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdDesCiuEmi();
			} else if ("dTelEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdTelEmi();
			} else if ("dEmailE".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdEmailE();
			} else if ("dDesActEco".equals(fieldName)) {
				TgActEco a1 = this.DE.getgDatGralOpe().getgEmis().getgActEcoList().get(0);
				value = a1.getdDesActEco();
			} else if ("dDesActEco2".equals(fieldName)) {
				TgActEco a2 = this.DE.getgDatGralOpe().getgEmis().getgActEcoList().get(1);
				value = a2.getdDesActEco();
			} else if ("dRucEm".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdRucEm();
			} else if ("dDVEmi".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgEmis().getdDVEmi();
			} else if ("dNumTim".equals(fieldName)) {
				value = this.DE.getgTimb().getdNumTim() + "";
			} else if ("V_dFeIniT".equals(fieldName)) {
				try {
					Date d = new SimpleDateFormat("yyyy-MM-dd").parse(this.DE.getgTimb().getdFeIniT().toString());
					value = Utiles.getDateToString(d, "dd-MM-yyyy") + "";
				} catch (ParseException e) {
					e.printStackTrace();
				}				
			} else if ("dDesTiDE".equals(fieldName)) {
				value = this.DE.getgTimb().getiTiDE().getDescripcion();
			} else if ("dEst".equals(fieldName)) {
				value = this.DE.getgTimb().getdEst();
			} else if ("dPunExp".equals(fieldName)) {
				value = this.DE.getgTimb().getdPunExp();
			} else if ("dNumDoc".equals(fieldName)) {
				value = this.DE.getgTimb().getdNumDoc();
			} else if ("V_dFeEmiDE".equals(fieldName)) {
				try {
					Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(this.DE.getgDatGralOpe().getdFeEmiDE().toString());
					value = Utiles.getDateToString(d, "dd-MM-yyyy HH:mm:ss") + "";
				} catch (ParseException e) {
					e.printStackTrace();
				}			
			} else if ("dDCondOpe".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgCamCond().getiCondOpe().getDescripcion();
			} else if ("iNatRec".equals(fieldName)) {
				value = Integer.parseInt(this.DE.getgDatGralOpe().getgDatRec().getiNatRec().getVal() + "");
			} else if ("dRucRec".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgDatRec().getdRucRec();
			} else if ("dDVRec".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgDatRec().getdDVRec() + "";
			} else if ("dNumIDRec".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgDatRec().getdNumIDRec();
			} else if ("dNomRec".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgDatRec().getdNomRec();
			} else if ("iCondCred".equals(fieldName)) {
				if (!this.DE.getgDtipDE().getgCamCond().getiCondOpe().getDescripcion().equals("Contado")) {
					value = Integer.parseInt(this.DE.getgDtipDE().getgCamCond().getgPagCred().getiCondCred().getVal() + "");
				}				
			} else if ("dCuotas".equals(fieldName)) {
				value = dCuotas;
			} else if ("dPlazoCre".equals(fieldName)) {
				if (this.DE.getgDtipDE().getgCamCond().getiCondOpe().getDescripcion().equals("Contado")) {
					value = "";
				} else {
					value = this.DE.getgDtipDE().getgCamCond().getgPagCred().getdPlazoCre();
				}
			} else if ("dDirRec".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgDatRec().getdDirRec();
			} else if ("dTelRec".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgDatRec().getdTelRec();
			} else if ("dDCondCred".equals(fieldName)) {
				if (!this.DE.getgDtipDE().getgCamCond().getiCondOpe().getDescripcion().equals("Contado")) {
					value = this.DE.getgDtipDE().getgCamCond().getgPagCred().getiCondCred().getDescripcion();
				}
			} else if ("iCondOpe".equals(fieldName)) {
				value = Integer.parseInt(this.DE.getgDtipDE().getgCamCond().getiCondOpe().getVal() + "");
			} else if ("cMoneOpe".equals(fieldName)) {
				value = this.DE.getgDatGralOpe().getgOpeCom().getcMoneOpe().getDescripcion();
			} else if ("dDesTipDocAso".equals(fieldName)) {
				value = this.DE.getgCamDEAsocList().get(0).getiTipDocAso().getDescripcion();
			} else if ("iTipDocAso".equals(fieldName)) {
				value = Integer.parseInt(this.DE.getgCamDEAsocList().get(0).getiTipDocAso().getVal() + "");
			} else if ("dCdCDERef".equals(fieldName)) {
				value = this.DE.getgCamDEAsocList().get(0).getdCdCDERef();
			} else if ("dDesTipTra".equals(fieldName)) {
				if(this.DE.getgDatGralOpe().getgOpeCom().getiTipTra() != null) {
					value = this.DE.getgDatGralOpe().getgOpeCom().getiTipTra().getDescripcion();
				}				
			} else if ("dCodInt".equals(fieldName)) {
				value = item.getdCodInt();
			} else if ("dDesMotEmi".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgCamNCDE().getiMotEmi().getDescripcion();
			} else if ("dDesProSer".equals(fieldName)) {
				value = item.getdDesProSer();
			}  else if ("dDesUniMed".equals(fieldName)) {
				value = item.getcUniMed().getAbreviatura();
			}  else if ("dPUniProSer".equals(fieldName)) {
				value = Double.parseDouble(item.getgValorItem().getdPUniProSer() + "");
			}  else if ("dCantProSer".equals(fieldName)) {
				if (this.DE.getgTimb().getiTiDE().equals(TTiDE.NOTA_DE_CREDITO_ELECTRONICA)) {
					value = item.getdCantProSer();
				} else if(this.DE.getgTimb().getiTiDE().equals(TTiDE.NOTA_DE_REMISION_ELECTRONICA)) {
					value = Double.parseDouble(item.getdCantProSer() + "");
				} else {
					value = Double.parseDouble(item.getdCantProSer() + "");
				}				
			}  else if ("dDescItem".equals(fieldName)) {
				value = Double.parseDouble(item.getgValorItem().getgValorRestaItem().getdDescItem() + "");
			}  else if ("dTotDesc".equals(fieldName)) {
				value = Double.parseDouble(this.DE.getgTotSub().getdTotDesc() + "");
			}  else if ("dTasaIVA".equals(fieldName)) {
				value = Integer.parseInt(item.getgCamIVA().getdTasaIVA().intValueExact() + "");
			}  else if ("dTotOpeItem".equals(fieldName)) {
				value = Double.parseDouble(item.getgValorItem().getgValorRestaItem().getdTotOpeItem() + "");
			}  else if ("dTotOpe".equals(fieldName)) {
				value = Double.parseDouble(this.DE.getgTotSub().getdTotOpe() + "");
			} else if ("dTotGralOpe".equals(fieldName)) {
				value = Double.parseDouble(this.DE.getgTotSub().getdTotOpe() + "");
				if (!this.DE.getgDatGralOpe().getgOpeCom().getcMoneOpe().getDescripcion().contains("Guarani")) {
					value = Double.parseDouble(this.DE.getgTotSub().getdTotOpe() + "") * Double.parseDouble(this.DE.getgDatGralOpe().getgOpeCom().getdTiCam() + "");
				}
			} else if ("dTiCam".equals(fieldName)) {
				value = "";
				if (!this.DE.getgDatGralOpe().getgOpeCom().getcMoneOpe().getDescripcion().contains("Guarani")) {					
					value = Utiles.getNumberFormat(Double.parseDouble(this.DE.getgDatGralOpe().getgOpeCom().getdTiCam() + ""));
				}
			} else if ("tc".equals(fieldName)) {
				value = "";
				if (!this.DE.getgDatGralOpe().getgOpeCom().getcMoneOpe().getDescripcion().contains("Guarani")) {					
					value = "Tipo de Cambio: ";
				}
			}  else if ("dSub10".equals(fieldName)) {
				value = Double.parseDouble(this.DE.getgTotSub().getdSub10() + "");
			}  else if ("dSub5".equals(fieldName)) {
				value = Double.parseDouble(this.DE.getgTotSub().getdSub5() + "");
			}  else if ("dSubExe".equals(fieldName)) {
				value = Double.parseDouble(this.DE.getgTotSub().getdSubExe() + "");
			}  else if ("dIVA5".equals(fieldName)) {
				value = Double.parseDouble(this.DE.getgTotSub().getdIVA5() + "");
			}  else if ("dIVA10".equals(fieldName)) {
				value = Double.parseDouble(this.DE.getgTotSub().getdIVA10() + "");
			}  else if ("dCarQR".equals(fieldName)) {
				value = this.DE.getEnlaceQR();
			}  else if ("Id".equals(fieldName)) {
				value = this.DE.getId();
			}  else if ("logo".equals(fieldName)) {
				value = getPathLogo();
			}  else if ("dDesMotEmiNR".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgCamNRE().getiMotEmiNR().getDescripcion();
			}  else if ("dDesRespEmiNR".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgCamNRE().getiRespEmiNR().getDescripcion();
			}  else if ("V_dIniTras".equals(fieldName)) {
				try {
					Date d = new SimpleDateFormat("yyyy-MM-dd").parse(this.DE.getgDtipDE().getgTransp().getdIniTras().toString());
					value = Utiles.getDateToString(d, "dd-MM-yyyy") + "";
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}  else if ("V_dFinTras".equals(fieldName)) {
				try {
					Date d = new SimpleDateFormat("yyyy-MM-dd").parse(this.DE.getgDtipDE().getgTransp().getdFinTras().toString());
					value = Utiles.getDateToString(d, "dd-MM-yyyy") + "";
				} catch (ParseException e) {
					e.printStackTrace();
				}			
			}  else if ("dDirLocSal".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamSal().getdDirLocSal();
			}  else if ("dDesCiuSal".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamSal().getdDesCiuSal();
			}  else if ("dDesDepSal".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamSal().getcDepSal().getDescripcion();
			}  else if ("dKmR".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgCamNRE().getdKmR() + "";
			}  else if ("dNumCasSal".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamSal().getdNumCasSal() + "";
			}  else if ("dNumCasEnt".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamEntList().get(0).getdNumCasEnt() + "";
			}  else if ("dDesTipTrans".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getiTipTrans().getDescripcion();
			}  else if ("dDesModTrans".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getiModTrans().getDescripcion();
			}  else if ("iRespFlete".equals(fieldName)) {
				value = "Emisor de la factura";
			}  else if ("cCondNeg".equals(fieldName)) {
				if (this.DE.getgDtipDE().getgTransp().getcCondNeg() != null) {
					value = this.DE.getgDtipDE().getgTransp().getcCondNeg().getDescripcion();
				} else {
					value = "";
				}
			}  else if ("dMarVeh".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgVehTrasList().get(0).getdMarVeh();
			}  else if ("dNroIDVeh".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgVehTrasList().get(0).getdNroIDVeh();
			}  else if ("dNroMatVeh".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgVehTrasList().get(0).getdNroMatVeh();
			}  else if ("dTiVehTras".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgVehTrasList().get(0).getdTiVehTras();
			}  else if ("iNatTrans".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamTrans().getiNatTrans().getDescripcion();
			}  else if ("dNomTrans".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamTrans().getdNomTrans();
			}  else if ("dNumIDChof".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamTrans().getdNumIDChof();
			}  else if ("dNomChof".equals(fieldName)) {
				value = this.DE.getgDtipDE().getgTransp().getgCamTrans().getdNomChof();
			}  else if ("dNombAg".equals(fieldName)) {
				String n = this.DE.getgDtipDE().getgTransp().getgCamTrans().getdNombAg();
				value = n != null ? n : "";
			}  else if ("dRucAg".equals(fieldName)) {
				String n = this.DE.getgDtipDE().getgTransp().getgCamTrans().getdRucAg();
				value = n != null ? n : "";
			}  else if ("dDVAg".equals(fieldName)) {				
				String n = this.DE.getgDtipDE().getgTransp().getgCamTrans().getdRucAg();
				value = n != null ? this.DE.getgDtipDE().getgTransp().getgCamTrans().getdDVAg() + "" : "";
			}  else if ("dDirAge".equals(fieldName)) {
				String n = this.DE.getgDtipDE().getgTransp().getgCamTrans().getdDirAge();
				value = n != null ? n : "";
			} else if ("dEstDocAso".equals(fieldName)) {
				value = this.DE.getgCamDEAsocList().get(0).getdEstDocAso();
			} else if ("dPExpDocAso".equals(fieldName)) {
				value = this.DE.getgCamDEAsocList().get(0).getdPExpDocAso();
			} else if ("dNumDocAso".equals(fieldName)) {
				value = this.DE.getgCamDEAsocList().get(0).getdNumDocAso();
			}  else if ("fsc".equals(fieldName)) {
				value = this.DE.getFsc();
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
	 * GET'S AND SET'S
	 */
	
	@DependsOn({ "filterDesde", "filterHasta" })
	public List<Object[]> getVentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getSifenVentas(this.filterDesde, this.filterHasta);
	}
	
	@DependsOn({ "filterDesde", "filterHasta" })
	public List<Object[]> getRemisiones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getSifenRemisiones(this.filterDesde, this.filterHasta);
	}
	
	@DependsOn({ "filterDesde", "filterHasta" })
	public List<Object[]> getNotasCredito() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getSifenNotasCredito(this.filterDesde, this.filterHasta);
	}  
	
	/**
	 * @return empresa yrsa
	 */
	public boolean isEmpresaYRSA() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_YRSA);
	}
	
	/**
	 * @return path del logo..
	 */
	public String getPathLogo() {
		return this.isEmpresaYRSA() ? SifenParams.SIFEN_LOGO_PATH : SifenParams.SIFEN_LOGO_PATH_GRPT;
	}

	public Date getFilterDesde() {
		return filterDesde;
	}

	public void setFilterDesde(Date filterDesde) {
		if (Utiles.diasEntreFechas(filterDesde, this.filterHasta) > 30) {
			Clients.showNotification("RANGO PERMITIDO HASTA 30 DÍAS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		this.filterDesde = filterDesde;
	}

	public Date getFilterHasta() {
		return filterHasta;
	}

	public void setFilterHasta(Date filterHasta) {
		if (Utiles.diasEntreFechas(this.filterDesde, filterHasta) > 30) {
			Clients.showNotification("RANGO PERMITIDO HASTA 30 DÍAS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		this.filterHasta = filterHasta;
	}

	public Object[] getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Object[] selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<Funcionario> getChoferes() {
		return choferes;
	}

	public void setChoferes(List<Funcionario> choferes) {
		this.choferes = choferes;
	}

	public List<Vehiculo> getVehiculos() {
		return vehiculos;
	}

	public void setVehiculos(List<Vehiculo> vehiculos) {
		this.vehiculos = vehiculos;
	}

	public Funcionario getSelectedChofer() {
		return selectedChofer;
	}

	public void setSelectedChofer(Funcionario selectedChofer) {
		this.selectedChofer = selectedChofer;
	}

	public Vehiculo getSelectedVehiculo() {
		return selectedVehiculo;
	}

	public void setSelectedVehiculo(Vehiculo selectedVehiculo) {
		this.selectedVehiculo = selectedVehiculo;
	}
}
