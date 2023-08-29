package com.roshka.sifen;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.roshka.sifen.core.SifenConfig;
import com.roshka.sifen.core.beans.DocumentoElectronico;
import com.roshka.sifen.core.beans.response.RespuestaRecepcionDE;
import com.roshka.sifen.core.exceptions.SifenException;
import com.roshka.sifen.core.fields.request.de.TdDatGralOpe;
import com.roshka.sifen.core.fields.request.de.TgActEco;
import com.roshka.sifen.core.fields.request.de.TgCamCond;
import com.roshka.sifen.core.fields.request.de.TgCamFE;
import com.roshka.sifen.core.fields.request.de.TgCamIVA;
import com.roshka.sifen.core.fields.request.de.TgCamItem;
import com.roshka.sifen.core.fields.request.de.TgDatRec;
import com.roshka.sifen.core.fields.request.de.TgDtipDE;
import com.roshka.sifen.core.fields.request.de.TgEmis;
import com.roshka.sifen.core.fields.request.de.TgOpeCom;
import com.roshka.sifen.core.fields.request.de.TgOpeDE;
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
import com.roshka.sifen.core.types.TiTiOpe;
import com.roshka.sifen.core.types.TiTipCont;
import com.roshka.sifen.core.types.TiTipDocRec;
import com.yhaguy.Configuracion;
import com.yhaguy.sifen.SifenTest;

public class Test {

	protected final static Logger logger = Logger.getLogger(Test.class.toString());
	
	public void testRecepcionDE() throws SifenException {
        LocalDateTime currentDate = LocalDateTime.now();

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
        gTimb.setiTiDE(TTiDE.FACTURA_ELECTRONICA);
        gTimb.setdNumTim(12560739);
        gTimb.setdEst("001");
        gTimb.setdPunExp("001");
        gTimb.setdNumDoc("0000173");
        gTimb.setdFeIniT(LocalDate.parse("2023-03-30"));
        DE.setgTimb(gTimb);

        // Grupo D
        TdDatGralOpe dDatGralOpe = new TdDatGralOpe();
        dDatGralOpe.setdFeEmiDE(currentDate);

        TgOpeCom gOpeCom = new TgOpeCom();
        gOpeCom.setiTipTra(TTipTra.PRESTACION_SERVICIOS);
        gOpeCom.setiTImp(TTImp.IVA);
        gOpeCom.setcMoneOpe(CMondT.PYG);
        dDatGralOpe.setgOpeCom(gOpeCom);

        TgEmis gEmis = new TgEmis();
        gEmis.setdRucEm("80009607");
        gEmis.setdDVEmi("0");
        gEmis.setiTipCont(TiTipCont.PERSONA_JURIDICA);
        gEmis.setdNomEmi("DE generado en ambiente de prueba - sin valor comercial ni fiscal");
        gEmis.setdDirEmi("Onofre Gómez esq. Avda. del Yacht Lambaré - Paraguay");
        gEmis.setdNumCas("670");
        gEmis.setcDepEmi(TDepartamento.CAPITAL);
        gEmis.setcCiuEmi(1);
        gEmis.setdDesCiuEmi("ASUNCION (DISTRITO)");
        gEmis.setdTelEmi("021 900 303");
        gEmis.setdEmailE("info@nobel.com.py");

        List<TgActEco> gActEcoList = new ArrayList<>();
        TgActEco gActEco = new TgActEco();
        gActEco.setcActEco("18111");
        gActEco.setdDesActEco("ACTIVIDADES DE IMPRENTA");
        gActEcoList.add(gActEco);

        TgActEco gActEco2 = new TgActEco();
        gActEco2.setcActEco("47610");
        gActEco2.setdDesActEco("COMERCIO AL POR MENOR DE LIBROS, PERIÓDICOS Y ARTÍCULOS DE PAPELERÍA");
        gActEcoList.add(gActEco2);

        gEmis.setgActEcoList(gActEcoList);
        dDatGralOpe.setgEmis(gEmis);

        TgDatRec gDatRec = new TgDatRec();
        gDatRec.setiNatRec(TiNatRec.NO_CONTRIBUYENTE);
        gDatRec.setiTiOpe(TiTiOpe.B2C);
        gDatRec.setcPaisRec(PaisType.PRY);
        gDatRec.setiTipIDRec(TiTipDocRec.CEDULA_PARAGUAYA);
        gDatRec.setdNumIDRec("4579993");
        gDatRec.setdNomRec("Martin Zarza");
        dDatGralOpe.setgDatRec(gDatRec);
        DE.setgDatGralOpe(dDatGralOpe);

        // Grupo E
        TgDtipDE gDtipDE = new TgDtipDE();

        TgCamFE gCamFE = new TgCamFE();
        gCamFE.setiIndPres(TiIndPres.OPERACION_ELECTRONICA);
        gDtipDE.setgCamFE(gCamFE);

        TgCamCond gCamCond = new TgCamCond();
        gCamCond.setiCondOpe(TiCondOpe.CREDITO);

        TgPagCred gPagCred = new TgPagCred();
        gPagCred.setiCondCred(TiCondCred.PLAZO);
        gPagCred.setdPlazoCre("60 días");

        gCamCond.setgPagCred(gPagCred);
        gDtipDE.setgCamCond(gCamCond);

        List<TgCamItem> gCamItemList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            TgCamItem gCamItem = new TgCamItem();
            gCamItem.setdCodInt(i == 0 ? "001" : "002");
            gCamItem.setdDesProSer(i == 0 ? "Servicio de Liquidación de IVA" : "Servicio de Liquidación de IRP");
            gCamItem.setcUniMed(TcUniMed.UNI);
            gCamItem.setdCantProSer(BigDecimal.valueOf(123600.00));

            TgValorItem gValorItem = new TgValorItem();
            gValorItem.setdPUniProSer(BigDecimal.valueOf(465.00));

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
        DE.setgDtipDE(gDtipDE);

        // Grupo E
        DE.setgTotSub(new TgTotSub());

        logger.info("CDC del Documento Electrónico -> " + DE.obtenerCDC());

        RespuestaRecepcionDE ret = Sifen.recepcionDE(DE);
        logger.info(ret.getRespuestaBruta());
    }
	
	public static void main(String[] args) {
		try {
			SifenConfig config = new SifenConfig(SifenConfig.TipoAmbiente.DEV, "0002", // ID CSC
					"EFGH0000000000000000000000000000", // CSC
					SifenConfig.TipoCertificadoCliente.PFX, 
					Configuracion.SIFEN_DIR + "nobel.p12", // lelis1357
					"OFIUCOME33");
			config.setHabilitarNotaTecnica13(true);
			com.roshka.sifen.Sifen.setSifenConfig(config);
			SifenTest test = new SifenTest();
			test.testConsultaDE("01800096070001001000050822023053014495152927", "test");
			
		} catch (SifenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
