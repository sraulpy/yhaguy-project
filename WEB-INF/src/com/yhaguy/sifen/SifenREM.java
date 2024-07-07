package com.yhaguy.sifen;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.roshka.sifen.Sifen;
import com.roshka.sifen.core.beans.DocumentoElectronico;
import com.roshka.sifen.core.beans.response.RespuestaConsultaDE;
import com.roshka.sifen.core.beans.response.RespuestaRecepcionLoteDE;
import com.roshka.sifen.core.exceptions.SifenException;
import com.roshka.sifen.core.fields.request.de.TdDatGralOpe;
import com.roshka.sifen.core.fields.request.de.TgActEco;
import com.roshka.sifen.core.fields.request.de.TgCamCond;
import com.roshka.sifen.core.fields.request.de.TgCamDEAsoc;
import com.roshka.sifen.core.fields.request.de.TgCamEnt;
import com.roshka.sifen.core.fields.request.de.TgCamFE;
import com.roshka.sifen.core.fields.request.de.TgCamIVA;
import com.roshka.sifen.core.fields.request.de.TgCamItem;
import com.roshka.sifen.core.fields.request.de.TgCamNRE;
import com.roshka.sifen.core.fields.request.de.TgCamSal;
import com.roshka.sifen.core.fields.request.de.TgCamTrans;
import com.roshka.sifen.core.fields.request.de.TgDatRec;
import com.roshka.sifen.core.fields.request.de.TgDtipDE;
import com.roshka.sifen.core.fields.request.de.TgEmis;
import com.roshka.sifen.core.fields.request.de.TgOpeCom;
import com.roshka.sifen.core.fields.request.de.TgOpeDE;
import com.roshka.sifen.core.fields.request.de.TgPaConEIni;
import com.roshka.sifen.core.fields.request.de.TgPagCred;
import com.roshka.sifen.core.fields.request.de.TgTimb;
import com.roshka.sifen.core.fields.request.de.TgTotSub;
import com.roshka.sifen.core.fields.request.de.TgTransp;
import com.roshka.sifen.core.fields.request.de.TgValorItem;
import com.roshka.sifen.core.fields.request.de.TgValorRestaItem;
import com.roshka.sifen.core.fields.request.de.TgVehTras;
import com.roshka.sifen.core.types.CMondT;
import com.roshka.sifen.core.types.PaisType;
import com.roshka.sifen.core.types.TDepartamento;
import com.roshka.sifen.core.types.TTImp;
import com.roshka.sifen.core.types.TTiDE;
import com.roshka.sifen.core.types.TTipEmi;
import com.roshka.sifen.core.types.TTipTra;
import com.roshka.sifen.core.types.TcUniMed;
import com.roshka.sifen.core.types.TiAfecIVA;
import com.roshka.sifen.core.types.TiCondCred;
import com.roshka.sifen.core.types.TiCondOpe;
import com.roshka.sifen.core.types.TiIndPres;
import com.roshka.sifen.core.types.TiModTrans;
import com.roshka.sifen.core.types.TiMotivTras;
import com.roshka.sifen.core.types.TiNatRec;
import com.roshka.sifen.core.types.TiRespEmiNR;
import com.roshka.sifen.core.types.TiRespFlete;
import com.roshka.sifen.core.types.TiTIpoDoc;
import com.roshka.sifen.core.types.TiTTrans;
import com.roshka.sifen.core.types.TiTiOpe;
import com.roshka.sifen.core.types.TiTiPago;
import com.roshka.sifen.core.types.TiTipCont;
import com.roshka.sifen.core.types.TiTipDocAso;
import com.roshka.sifen.core.types.TiTipDocRec;
import com.roshka.sifen.internal.ctx.GenerationCtx;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Remision;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.Utiles;

public class SifenREM {

	private final static Logger logger = Logger.getLogger(SifenREM.class.toString());
	
	public void testRecepcionDE(Remision data, boolean async, boolean testing) throws SifenException {		
		Date in = data.getFecha();
		LocalDateTime currentDate = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        
        boolean contribuyente = true;
        String dv = "";
        
        if (!data.getVenta().getCliente().getRuc().contains("-")) {
			contribuyente = false;
		} else {
			dv = data.getVenta().getCliente().getRuc().split("-")[1];
		}        

        // Grupo A
        DocumentoElectronico DE = new DocumentoElectronico();
        DE.setdFecFirma(currentDate);
        DE.setdSisFact((short) 1);

        // Grupo B
        TgOpeDE gOpeDE = new TgOpeDE();
        gOpeDE.setiTipEmi(TTipEmi.NORMAL);
        gOpeDE.setdCodSeg("000000001");
        gOpeDE.setdInfoFisc("Información de interés del Fisco respecto al DE");
        DE.setgOpeDE(gOpeDE);

        // Grupo C
        TgTimb gTimb = new TgTimb();
        gTimb.setiTiDE(TTiDE.NOTA_DE_REMISION_ELECTRONICA);
        gTimb.setdNumTim(isEmpresaYRSA() ? (testing ? SifenParams.TIMBRADO_TEST_YRSA : SifenParams.TIMBRADO_PROD) : SifenParams.TIMBRADO_TEST_GRPT);
        gTimb.setdEst("001");
        gTimb.setdPunExp(data.getNumero().split("-")[1]);
        gTimb.setdNumDoc(data.getNumero().split("-")[2]);
        gTimb.setdFeIniT(isEmpresaYRSA() ? (testing ? SifenParams.VIGENCIA_TEST_YRSA : SifenParams.VIGENCIA_PROD) : SifenParams.VIGENCIA_TEST_GRPT);
        DE.setgTimb(gTimb);

        // Grupo D
        TdDatGralOpe dDatGralOpe = new TdDatGralOpe();
        dDatGralOpe.setdFeEmiDE(currentDate);

        TgOpeCom gOpeCom = new TgOpeCom();
        gOpeCom.setiTipTra(TTipTra.VENTA_MERCADERIA);
        gOpeCom.setiTImp(TTImp.IVA);
        gOpeCom.setcMoneOpe(CMondT.PYG);
        dDatGralOpe.setgOpeCom(gOpeCom);
        
        TgCamNRE gCamNRE = new TgCamNRE();
        gCamNRE.setiMotEmiNR(TiMotivTras.TRASLADO_POR_VENTAS);
        gCamNRE.setiRespEmiNR(TiRespEmiNR.EMISOR_FACTURA);
        gCamNRE.setdKmR(10);
        gCamNRE.setdFecEm(LocalDate.parse(Utiles.getDateToString(new Date(), "yyyy-MM-dd")));

        TgEmis gEmis = new TgEmis();
        gEmis.setdRucEm(isEmpresaYRSA() ? SifenParams.YRSA_RUC_EMI : SifenParams.GRPT_RUC_EMI);
        gEmis.setdDVEmi(isEmpresaYRSA() ? SifenParams.YRSA_DV_EMI : SifenParams.GRPT_DV_EMI);
        gEmis.setiTipCont(TiTipCont.PERSONA_JURIDICA);
        gEmis.setdNomEmi(isEmpresaYRSA() ? SifenParams.YRSA_NOM_EMI : SifenParams.GRPT_NOM_EMI);
        gEmis.setdDirEmi(isEmpresaYRSA() ? SifenParams.YRSA_DIR_EMI : SifenParams.GRPT_DIR_EMI);
        gEmis.setdNumCas("670");
        gEmis.setcDepEmi(TDepartamento.CAPITAL);
        gEmis.setcCiuEmi(1);
        gEmis.setdDesCiuEmi("ASUNCION (DISTRITO)");
        gEmis.setdTelEmi(isEmpresaYRSA() ? SifenParams.YRSA_TEL_EMI : SifenParams.GRPT_TEL_EMI);
        gEmis.setdEmailE(isEmpresaYRSA() ? SifenParams.YRSA_EMAIL_EMI : SifenParams.GRPT_EMAIL_EMI);

        List<TgActEco> gActEcoList = new ArrayList<>();
        TgActEco gActEco = new TgActEco();
        gActEco.setcActEco("45303");
        gActEco.setdDesActEco("COMERCIO DE PARTES, PIEZAS Y ACCESORIOS NUEVOS Y USADOS PARA VEHÍCULOS AUTOMOTORES");
        gActEcoList.add(gActEco);

        TgActEco gActEco2 = new TgActEco();
        gActEco2.setcActEco("45201");
        gActEco2.setdDesActEco("MANTENIMIENTO Y REPARACIÓN MECÁNICA DE VEHÍCULOS");
        gActEcoList.add(gActEco2);

        gEmis.setgActEcoList(gActEcoList);
        dDatGralOpe.setgEmis(gEmis);

        TgDatRec gDatRec = new TgDatRec();
        if (contribuyente) {
        	gDatRec.setiNatRec(TiNatRec.CONTRIBUYENTE);
            gDatRec.setiTiOpe(TiTiOpe.B2C);
            gDatRec.setcPaisRec(PaisType.PRY);
            gDatRec.setiTiContRec(TiTipCont.PERSONA_FISICA);
            gDatRec.setdRucRec(data.getVenta().getCliente().getRuc().split("-")[0]);
            gDatRec.setdDVRec(Short.valueOf(dv));
            gDatRec.setdNomRec(data.getVenta().getCliente().getRazonSocial());
		} else {
	        gDatRec.setiNatRec(TiNatRec.NO_CONTRIBUYENTE);
	        gDatRec.setiTiOpe(TiTiOpe.B2C);
	        gDatRec.setcPaisRec(PaisType.PRY);
	        gDatRec.setiTipIDRec(TiTipDocRec.CEDULA_PARAGUAYA);
	        gDatRec.setdNumIDRec(data.getVenta().getCliente().getEmpresa().getCi());
	        gDatRec.setdNomRec(data.getVenta().getCliente().getRazonSocial());
		}
        gDatRec.setcDepRec(TDepartamento.CAPITAL);
        gDatRec.setcCiuRec(1);
        gDatRec.setdDesCiuRec("ASUNCION (DISTRITO)");
        dDatGralOpe.setgDatRec(gDatRec);
        gDatRec.setdDirRec("021 500 500");
        DE.setgDatGralOpe(dDatGralOpe);

        // Grupo E
        TgDtipDE gDtipDE = new TgDtipDE();

        TgCamFE gCamFE = new TgCamFE();
        gCamFE.setiIndPres(TiIndPres.OPERACION_ELECTRONICA);
        gDtipDE.setgCamFE(gCamFE);

        TgCamCond gCamCond = new TgCamCond();
        gCamCond.setiCondOpe(data.getVenta().getDescripcionCondicion().equals("CONTADO")? TiCondOpe.CONTADO : TiCondOpe.CREDITO);
        
        if (data.getVenta().getDescripcionCondicion().equals("CONTADO")) {
        	TgPaConEIni gPaConEIni = new TgPaConEIni();
            gPaConEIni.setiTiPago(TiTiPago.EFECTIVO);
            gPaConEIni.setdDesTiPag(gPaConEIni.getiTiPago().getDescripcion());
            gPaConEIni.setdMonTiPag(new BigDecimal(data.getImporteGs()));
            gPaConEIni.setcMoneTiPag(CMondT.PYG);
            List<TgPaConEIni> fps = new ArrayList<TgPaConEIni>();
            fps.add(gPaConEIni);
            gCamCond.setgPaConEIniList(fps);
		}        

        TgPagCred gPagCred = new TgPagCred();
        gPagCred.setiCondCred(TiCondCred.PLAZO);
        gPagCred.setdPlazoCre("30 días");

        gCamCond.setgPagCred(gPagCred);
        gDtipDE.setgCamCond(gCamCond);

        List<TgCamItem> gCamItemList = new ArrayList<>();
        for (VentaDetalle d : data.getVenta().getDetalles()) {
            TgCamItem gCamItem = new TgCamItem();
            gCamItem.setdCodInt(d.getArticulo().getCodigoInterno());
            gCamItem.setdDesProSer(d.getArticulo().getDescripcion());
            gCamItem.setcUniMed(TcUniMed.UNI);
            gCamItem.setdCantProSer(BigDecimal.valueOf(d.getCantidad()));

            TgValorItem gValorItem = new TgValorItem();
            gValorItem.setdPUniProSer(BigDecimal.valueOf(d.getPrecioGs()).setScale(2, RoundingMode.HALF_UP));

            TgValorRestaItem gValorRestaItem = new TgValorRestaItem();
            gValorItem.setgValorRestaItem(gValorRestaItem);
            gCamItem.setgValorItem(gValorItem);

            TgCamIVA gCamIVA = new TgCamIVA();
            gCamIVA.setiAfecIVA(TiAfecIVA.GRAVADO);
            gCamIVA.setdPropIVA(BigDecimal.valueOf(100));
            gCamIVA.setdTasaIVA(BigDecimal.valueOf(10));
            gCamItem.setgCamIVA(gCamIVA);

            gCamItemList.add(gCamItem);
        }

        gDtipDE.setgCamItemList(gCamItemList);
        gDtipDE.setgCamNRE(gCamNRE);
        DE.setgDtipDE(gDtipDE);
        
        List<TgCamDEAsoc> gCamDEAsocList = new ArrayList<TgCamDEAsoc>();
        TgCamDEAsoc gCamDEAsoc = new TgCamDEAsoc();
        gCamDEAsoc.setiTipDocAso(TiTipDocAso.ELECTRONICO);
        gCamDEAsoc.setdCdCDERef(data.getVenta().getCdc());
        gCamDEAsoc.setdEstDocAso(data.getVenta().getNumero().split("-")[0]);
        gCamDEAsoc.setdPExpDocAso(data.getVenta().getNumero().split("-")[1]);
        gCamDEAsoc.setdNumDocAso(data.getVenta().getNumero().split("-")[2]);
        gCamDEAsoc.setiTipoDocAso(TiTIpoDoc.FACTURA);
        gCamDEAsoc.setdFecEmiDI(isEmpresaYRSA() ? SifenParams.VIGENCIA_TEST_YRSA : SifenParams.VIGENCIA_TEST_GRPT);
        gCamDEAsoc.setdNTimDI((isEmpresaYRSA() ? SifenParams.TIMBRADO_TEST_YRSA : SifenParams.TIMBRADO_TEST_GRPT) + "");
        gCamDEAsocList.add(gCamDEAsoc);
        DE.setgCamDEAsocList(gCamDEAsocList);
        
        TgTransp gTransp = new TgTransp();
        gTransp.setiTipTrans(TiTTrans.PROPIO);
        gTransp.setiModTrans(TiModTrans.TERRESTRE);
        gTransp.setiRespFlete(TiRespFlete.EMISOR_FACTURA_ELECTRONICA);
        gTransp.setdIniTras(LocalDate.parse(Utiles.getDateToString(new Date(), "yyyy-MM-dd")));
        gTransp.setdFinTras(LocalDate.parse(Utiles.getDateToString(new Date(), "yyyy-MM-dd")));
        
        TgCamSal gCamSal = new TgCamSal();
        gCamSal.setdDirLocSal(isEmpresaYRSA() ? SifenParams.YRSA_DIR_EMI : SifenParams.GRPT_DIR_EMI);
        gCamSal.setdNumCasSal((short) 670);
        gCamSal.setdComp1Sal(isEmpresaYRSA() ? SifenParams.YRSA_DIR_EMI : SifenParams.GRPT_DIR_EMI);
        gCamSal.setcDepSal(TDepartamento.CAPITAL);
        gCamSal.setcCiuSal(1);
        gCamSal.setdDesCiuSal("ASUNCION (DISTRITO)");
        
        TgCamEnt gCamEnt = new TgCamEnt();
        gCamEnt.setdDirLocEnt("Innominado");
        gCamEnt.setdNumCasEnt(Short.valueOf("9999"));
        gCamEnt.setdComp1Ent("Alternativa");
        gCamEnt.setcDepEnt(TDepartamento.CAPITAL);
        gCamEnt.setcCiuEnt(1);
        gCamEnt.setdDesCiuEnt("ASUNCION (DISTRITO)");
        List<TgCamEnt> gCamEntList = new ArrayList<TgCamEnt>();
        gCamEntList.add(gCamEnt);
        
        TgVehTras gVehTras = new TgVehTras();
        gVehTras.setdTiVehTras("Terrestre");
        gVehTras.setdMarVeh(data.getVehiculo_().getMarca());
        gVehTras.setdTipIdenVeh((short)2);
        gVehTras.setdNroIDVeh("9999");
        gVehTras.setdNroMatVeh(data.getVehiculo_().getChapa());
        List<TgVehTras> gVehTrasList = new ArrayList<TgVehTras>();
        gVehTrasList.add(gVehTras);
        
        TgCamTrans gCamTrans = new TgCamTrans();
        gCamTrans.setiNatTrans(TiNatRec.CONTRIBUYENTE);
        gCamTrans.setdNomTrans(isEmpresaYRSA() ? SifenParams.YRSA_NOM_EMI : SifenParams.GRPT_NOM_EMI);
        gCamTrans.setdRucTrans(isEmpresaYRSA() ? SifenParams.YRSA_RUC_EMI : SifenParams.GRPT_RUC_EMI);
        gCamTrans.setdDVTrans(isEmpresaYRSA() ? (short) 8 : (short) 4);
        gCamTrans.setdDomFisc(isEmpresaYRSA() ? SifenParams.YRSA_DIR_EMI : SifenParams.GRPT_DIR_EMI);
        gCamTrans.setdNumIDChof(data.getChofer().getCedula());
        gCamTrans.setdNomChof(data.getChofer().getRazonSocial());
        gCamTrans.setdDirChof("Innominado");
        
        gTransp.setgCamSal(gCamSal);   
        gTransp.setgCamEntList(gCamEntList);
        gTransp.setgVehTrasList(gVehTrasList);
        gTransp.setgCamTrans(gCamTrans);
        gDtipDE.setgTransp(gTransp);

        // Grupo E
        DE.setgTotSub(new TgTotSub());

        logger.info("CDC del Documento Electrónico -> " + DE.obtenerCDC());
        
        if (async) {
        	DE.generarXml(GenerationCtx.getDefaultFromConfig(Sifen.getSifenConfig()), SifenParams.PATH_NOTAS_REMISION + data.getNumero() + ".xml");
		} else {
			//RespuestaRecepcionDE ret = Sifen.recepcionDE(DE);
            RespuestaRecepcionLoteDE ret = Sifen.recepcionLoteDE(Collections.singletonList(DE));
            logger.info(ret.getRespuestaBruta());	        
	        
			try {
				PrintWriter doc = new PrintWriter(SifenParams.PATH_NOTAS_REMISION + data.getNumero() + ".xml");
    			doc.println(ret.getRespuestaBruta());
    			doc.close();
    			
    			data.setCdc(DE.obtenerCDC());
    			data.setRespuestaSET(ret.getdCodRes().equals("0300") ? "Aprobado" : "Pendiente");
    			data.setObservacionSET(ret.getdMsgRes());
    			data.setUrl(DE.getEnlaceQR());
				
    			DE.generarXml(GenerationCtx.getDefaultFromConfig(Sifen.getSifenConfig()), SifenParams.PATH_NOTAS_REMISION + data.getNumero() + ".xml");
				
			} catch (Exception e) {
				e.printStackTrace();
			}  
		}
              
    }
	
	public void testConsultaDE(String cdc, String numero) throws SifenException {
        RespuestaConsultaDE ret = Sifen.consultaDE(cdc);
        logger.info(ret.getRespuestaBruta());        
		try {
			OutputStream os = new FileOutputStream(SifenParams.PATH_NOTAS_REMISION + numero + ".xml");
			PrintWriter doc = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
			doc.println(ret.getRespuestaBruta());
			doc.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
	
	public boolean isEmpresaYRSA() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_YRSA);
	}
}
