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

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.roshka.sifen.core.SifenConfig;
import com.roshka.sifen.core.beans.DocumentoElectronico;
import com.roshka.sifen.core.exceptions.SifenException;
import com.roshka.sifen.core.fields.request.de.TgActEco;
import com.roshka.sifen.core.fields.request.de.TgCamItem;
import com.roshka.sifen.core.types.TTiDE;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.sifen.SifenParams;
import com.yhaguy.sifen.SifenTest;
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
	
	private Date filterDesde;
	private Date filterHasta;

	@Init(superclass = true)
	public void init() {
		this.filterDesde = new Date();
		this.filterHasta = new Date();
		this.configSifen();
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
			test.testRecepcionDE(venta, false);
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
	public void imprimirFE(@BindingParam("bean") Object[] bean) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta venta = (Venta) rr.getObject(Venta.class.getName(), (long) bean[8]);
		this.generarPDFFE(venta);
		Executions.getCurrent().sendRedirect(this.getUrl(venta.getNumero()), "_blank");
	}
	
	@Command
	@NotifyChange("*")
	public void actualizar() {
		Clients.showNotification("DATOS ACTUALIZADOS");
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
	 * config sifen
	 */
	private void configSifen() {
		try {
			SifenConfig config = new SifenConfig(SifenConfig.TipoAmbiente.DEV, "0001", // ID CSC
					"ABCD0000000000000000000000000000", // CSC EFGH0000000000000000000000000000
					SifenConfig.TipoCertificadoCliente.PFX, 
					SifenParams.SIFEN_DIR + "firma_yrsa.p12", // 
					"lelis1357");
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
				value = this.DE.getgDtipDE().getgCamCond().getgPagCred().getdPlazoCre();
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
				value = 0.0;
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
				value = SifenParams.SIFEN_LOGO_PATH;
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
				value = this.DE.getgDtipDE().getgTransp().getcCondNeg().getDescripcion();
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
		return rr.getVentas(this.filterDesde, this.filterHasta, Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
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
}
