package com.yhaguy.sifen;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class FacturaDataSource implements JRDataSource {


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
	String qr = "https://ekuatia.set.gov.py/consultas-test/qr?nVersion=150&amp;Id=01800248848001001000000122023042516821921089&amp;dFeEmiDE=323032332d30342d32355431363a32323a3135&amp;dNumIDRec=4579993&amp;dTotGralOpe=240000&amp;dTotIVA=21818&amp;cItems=2&amp;DigestValue=5377725a507678336b43566642774665416b734f36374e714b63354f4c506d476a706657587561514d30553d&amp;IdCSC=0002&amp;cHashQR=1c1c24a178fa831b4de130e6336d2d874ff93e543a706d9ec95d63b40e1686b4";
	String ImageQR = "https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=" + qr;
	String Id = "01800248848001001000000122023042516821921089";
	
	List<Object[]> detalle = new ArrayList<Object[]>();

	public FacturaDataSource() {
		List<Object[]> list = new ArrayList<Object[]>();
		list.add(new Object[] { "001", "Batería marca lucas 90amp", "UNI", 120000.0, 1, 0.0, 10, 120000.0 });
		list.add(new Object[] { "002", "Lubricante marca valvoline 1lt", "UNI", 120000.0, 1, 0.0, 10, 120000.0 });
		this.detalle = list;
	}

	private int index = -1;
	
	

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] item = this.detalle.get(index);

		if ("dNomEmi".equals(fieldName)) {
			value = dNomEmi;
		} else if ("dDirEmi".equals(fieldName)) {
			value = dDirEmi;
		} else if ("dTelEmi".equals(fieldName)) {
			value = dTelEmi;
		} else if ("dEmailE".equals(fieldName)) {
			value = dEmailE;
		} else if ("dRucEm".equals(fieldName)) {
			value = dRucEm;
		} else if ("dDVEmi".equals(fieldName)) {
			value = dDVEmi;
		} else if ("dNumTim".equals(fieldName)) {
			value = dNumTim;
		} else if ("V_dFeIniT".equals(fieldName)) {
			value = V_dFeIniT;
		} else if ("dDesTiDE".equals(fieldName)) {
			value = dDesTiDE;
		} else if ("dEst".equals(fieldName)) {
			value = dEst;
		} else if ("dPunExp".equals(fieldName)) {
			value = dPunExp;
		} else if ("dNumDoc".equals(fieldName)) {
			value = dNumDoc;
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
		}  else if ("ImageQR".equals(fieldName)) {
			value = ImageQR;
		}  else if ("Id".equals(fieldName)) {
			value = Id;
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
