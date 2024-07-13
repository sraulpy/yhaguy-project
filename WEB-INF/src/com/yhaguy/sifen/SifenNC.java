package com.yhaguy.sifen;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import com.roshka.sifen.core.fields.request.de.TgCamFE;
import com.roshka.sifen.core.fields.request.de.TgCamIVA;
import com.roshka.sifen.core.fields.request.de.TgCamItem;
import com.roshka.sifen.core.fields.request.de.TgCamNCDE;
import com.roshka.sifen.core.fields.request.de.TgDatRec;
import com.roshka.sifen.core.fields.request.de.TgDtipDE;
import com.roshka.sifen.core.fields.request.de.TgEmis;
import com.roshka.sifen.core.fields.request.de.TgOpeCom;
import com.roshka.sifen.core.fields.request.de.TgOpeDE;
import com.roshka.sifen.core.fields.request.de.TgPaConEIni;
import com.roshka.sifen.core.fields.request.de.TgPagCred;
import com.roshka.sifen.core.fields.request.de.TgTimb;
import com.roshka.sifen.core.fields.request.de.TgTotSub;
import com.roshka.sifen.core.fields.request.de.TgValorItem;
import com.roshka.sifen.core.fields.request.de.TgValorRestaItem;
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
import com.roshka.sifen.core.types.TiNatRec;
import com.roshka.sifen.core.types.TiTIpoDoc;
import com.roshka.sifen.core.types.TiTiOpe;
import com.roshka.sifen.core.types.TiTiPago;
import com.roshka.sifen.core.types.TiTipCont;
import com.roshka.sifen.core.types.TiTipDocAso;
import com.roshka.sifen.core.types.TiTipDocRec;
import com.roshka.sifen.internal.ctx.GenerationCtx;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.Venta;

public class SifenNC {

	private final static Logger logger = Logger.getLogger(SifenNC.class.toString());
	
	public void testRecepcionDE(NotaCredito data, boolean testing) throws SifenException {
		Date in = data.getFechaEmision();
		LocalDateTime currentDate = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        
		Venta venta = data.getVentaAplicada();
		
        boolean contribuyente = true;
        String dv = "";
        
        if (!data.getCliente().getRuc().contains("-")
        		|| data.getCliente().getRuc().equals(Configuracion.RUC_EMPRESA_LOCAL)) {
			contribuyente = false;
		} else {
			dv = data.getCliente().getRuc().split("-")[1];
		}         

        // Grupo A
        DocumentoElectronico DE = new DocumentoElectronico();
        DE.setdFecFirma(currentDate);
        DE.setdSisFact((short) 1);

        // Grupo B
        TgOpeDE gOpeDE = new TgOpeDE();
        gOpeDE.setiTipEmi(TTipEmi.NORMAL);
        DE.setgOpeDE(gOpeDE);

        // Grupo C
        TgTimb gTimb = new TgTimb();
        gTimb.setiTiDE(TTiDE.NOTA_DE_CREDITO_ELECTRONICA);
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
        
        TgCamNCDE gCamNCDE  = new TgCamNCDE();
        gCamNCDE.setiMotEmi(data.getMotivoNC());

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
            gDatRec.setdRucRec(data.getCliente().getRuc().split("-")[0]);
            gDatRec.setdDVRec(Short.valueOf(dv));
            gDatRec.setdNomRec(data.getCliente().getRazonSocial());
		} else {
	        gDatRec.setiNatRec(TiNatRec.NO_CONTRIBUYENTE);
	        gDatRec.setiTiOpe(TiTiOpe.B2C);
	        gDatRec.setcPaisRec(PaisType.PRY);
	        gDatRec.setiTipIDRec(TiTipDocRec.CEDULA_PARAGUAYA);
	        gDatRec.setdNumIDRec(data.getCliente().getEmpresa().getCi());
	        gDatRec.setdNomRec(data.getCliente().getRazonSocial());
		}
        gDatRec.setcDepRec(TDepartamento.CAPITAL);
        gDatRec.setcCiuRec(1);
        gDatRec.setdDesCiuRec("ASUNCION (DISTRITO)");
        dDatGralOpe.setgDatRec(gDatRec);
        gDatRec.setdDirRec("021 999 999");
        
        if (!data.getCliente().getEmpresa().getDireccion_().trim().isEmpty()) {
        	gDatRec.setdDirRec(data.getCliente().getEmpresa().getDireccion_().trim());
		}
        if (!data.getCliente().getEmpresa().getTelefono_().trim().isEmpty()) {
        	gDatRec.setdTelRec(data.getCliente().getEmpresa().getTelefono_().trim());
		}
        
        DE.setgDatGralOpe(dDatGralOpe);

        // Grupo E
        TgDtipDE gDtipDE = new TgDtipDE();

        TgCamFE gCamFE = new TgCamFE();
        gCamFE.setiIndPres(TiIndPres.OPERACION_ELECTRONICA);
        gDtipDE.setgCamFE(gCamFE);

        TgCamCond gCamCond = new TgCamCond();
        gCamCond.setiCondOpe(venta.getDescripcionCondicion().equals("CONTADO")? TiCondOpe.CONTADO : TiCondOpe.CREDITO);
        
        if (venta.getDescripcionCondicion().equals("CONTADO")) {
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
        for (NotaCreditoDetalle d : data.getDetallesArticulos_()) {
            TgCamItem gCamItem = new TgCamItem();
            gCamItem.setdCodInt(d.getArticulo().getCodigoInterno());
            gCamItem.setdDesProSer(d.getArticulo().getDescripcion());
            gCamItem.setcUniMed(TcUniMed.UNI);
            gCamItem.setdCantProSer(BigDecimal.valueOf(d.getCantidad()));

            TgValorItem gValorItem = new TgValorItem();
            gValorItem.setdPUniProSer(BigDecimal.valueOf(d.getMontoGs()));

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
        gDtipDE.setgCamNCDE(gCamNCDE);
        DE.setgDtipDE(gDtipDE);
        
        List<TgCamDEAsoc> gCamDEAsocList = new ArrayList<TgCamDEAsoc>();
        TgCamDEAsoc gCamDEAsoc = new TgCamDEAsoc();
        gCamDEAsoc.setiTipDocAso(TiTipDocAso.ELECTRONICO);
        gCamDEAsoc.setdCdCDERef(venta.getCdc());
        gCamDEAsoc.setdEstDocAso(venta.getNumero().split("-")[0]);
        gCamDEAsoc.setdPExpDocAso(venta.getNumero().split("-")[1]);
        gCamDEAsoc.setdNumDocAso(venta.getNumero().split("-")[2]);
        gCamDEAsoc.setiTipoDocAso(TiTIpoDoc.FACTURA);
        gCamDEAsoc.setdFecEmiDI(isEmpresaYRSA() ? SifenParams.VIGENCIA_TEST_YRSA : SifenParams.VIGENCIA_TEST_GRPT);
        gCamDEAsoc.setdNTimDI((isEmpresaYRSA() ? SifenParams.TIMBRADO_TEST_YRSA : SifenParams.TIMBRADO_TEST_GRPT) + "");
        gCamDEAsocList.add(gCamDEAsoc);
        DE.setgCamDEAsocList(gCamDEAsocList);

        // Grupo E
        DE.setgTotSub(new TgTotSub());

        logger.info("CDC del Documento Electrónico -> " + DE.obtenerCDC());

        RespuestaRecepcionLoteDE ret = Sifen.recepcionLoteDE(Collections.singletonList(DE));
        logger.info(ret.getRespuestaBruta());
        
        
		try {
			PrintWriter doc = new PrintWriter(SifenParams.PATH_NOTAS_CREDITO + data.getNumero() + ".xml");
			doc.println(ret.getRespuestaBruta());
			doc.close();
			
			data.setCdc(DE.obtenerCDC());
			data.setRespuestaSET(ret.getdCodRes().equals("0300") ? "Aprobado" : "Pendiente");
			data.setObservacionSET(ret.getdProtConsLote());
			data.setUrl(DE.getEnlaceQR());
			
			DE.generarXml(GenerationCtx.getDefaultFromConfig(Sifen.getSifenConfig()), SifenParams.PATH_NOTAS_CREDITO + data.getNumero() + ".xml");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
    }
	
	public void testConsultaDE(String cdc, String numero) throws SifenException {
        RespuestaConsultaDE ret = Sifen.consultaDE(cdc);
        logger.info(ret.getRespuestaBruta());        
		try {
			OutputStream os = new FileOutputStream("C:\\notascreditodebito\\" + numero + ".xml");
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
