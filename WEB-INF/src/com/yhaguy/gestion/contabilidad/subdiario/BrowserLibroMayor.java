package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser2;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.RegisterDomain;

public class BrowserLibroMayor extends Browser2 {

	String query = " Select sd.numero, c.codigo, pc.codigo, sdd.descripcion, sdd.importe, sdd.importe"
			+ " from SubDiario as sd inner join sd.detalles as sdd  inner join sdd.cuenta c inner join c.planCuenta pc ";

	@Override
	public String getCabeceraZulUrl() {
		// TODO Auto-generated method stub
		return "/yhaguy/gestion/contabilidad/subdiario/visor_LibroMayor.zul";
	}

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		ColumnaBrowser subdiario = new ColumnaBrowser();
		subdiario.setCampo("sd.numero");
		subdiario.setTitulo("Subdiario");
		subdiario.setWidthColumna("150px");
		subdiario.setWidthComponente("120px");

		ColumnaBrowser descripcion = new ColumnaBrowser();
		descripcion.setCampo("sdd.descripcion");
		descripcion.setTitulo("descripcion");
//		descripcion.setWidthColumna("100px");
		descripcion.setWidthComponente("480px");

		ColumnaBrowser cuenta = new ColumnaBrowser();
		cuenta.setCampo("c.codigo");
		cuenta.setTitulo("Cuenta");
		cuenta.setWidthColumna("150px");
		cuenta.setWidthComponente("120px");

		ColumnaBrowser planCuenta = new ColumnaBrowser();
		planCuenta.setCampo("cp.codigo");
		planCuenta.setTitulo("Plan Cuenta");
		planCuenta.setWidthColumna("150px");
		planCuenta.setWidthComponente("120px");

		ColumnaBrowser importeDebe = new ColumnaBrowser();
		importeDebe.setCampo("sdd.importe");
		importeDebe.setTitulo("Debe");
		importeDebe.setComponente("getDebeComp");
		importeDebe.setTipo(Config.TIPO_NUMERICO);
		importeDebe.setWidthColumna("150px");
		importeDebe.setWidthComponente("120px");

		ColumnaBrowser importeHaber = new ColumnaBrowser();
		importeHaber.setCampo("sdd.importe");
		importeHaber.setTitulo("Haber");
		importeHaber.setComponente("getHaberComp");
		importeHaber.setTipo(Config.TIPO_NUMERICO);
		importeHaber.setWidthColumna("150px");
		importeHaber.setWidthComponente("120px");

		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(subdiario);
		columnas.add(cuenta);
		columnas.add(planCuenta);
		columnas.add(descripcion);
		columnas.add(importeDebe);
		columnas.add(importeHaber);

		return columnas;
	}

	@Override
	public String getQuery() {
		return this.query;
	}

	public void setQueryNuevo(String query) {
		this.query = query;
	}

	@Override
	public void setingInicial() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTituloBrowser() {
		// TODO Auto-generated method stub
		return null;
	}

	public HtmlBasedComponent getDebeComp(Object obj, Object[] datos) {
		return  getValorComp(obj, datos, true);
	}

	public HtmlBasedComponent getHaberComp(Object obj, Object[] datos) {
		return  getValorComp(obj, datos, false);
	}

	public HtmlBasedComponent getValorComp(Object obj, Object[] datos, boolean debeHaber) {
		Textbox l = new Textbox();
		l.setReadonly(true);
		l.setInplace(true);
		l.setStyle("text-align:right");
		l.setValue("");
		double valor = (double) obj;
		if ((debeHaber == true)&&(valor >= 0)) {
			// debe
			l.setValue(this.m.formatoGs(valor) );
		}
		if ((debeHaber == false)&&(valor < 0)) {
			// haber
			l.setValue(this.m.formatoGs(valor * -1) );
		}
		return l;
	}

	
	
	
	
	public static void main(String[] args) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		String query = " Select sd.numero, sdd.descripcion, sdd.importe, c.codigo, pc.codigo"
				+ " from SubDiario as sd inner join sd.detalles as sdd  inner join sdd.cuenta c inner join c.planCuenta pc ";
		
		List<Object[]> obj = rr.hql(query);
		for(Object oj[] : obj){
			System.out.println("numero: "+oj[0]+" codigo: "+oj[3]+" codigo: "+oj[4]+" importe: "+oj[2]+"\n");
		}
	}

}
