package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.extras.browser.Browser2;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.coreweb.extras.reporte.DatosReporte;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.reporte.ReporteVacio;

public class BrowserPlanCuenta extends Browser2{

	String query = "Select pc.codigo, pc.descripcion, t.descripcion"
			+ " from PlanDeCuenta pc inner join pc.tipoCuenta t ";
	
	@Override
	public String getCabeceraZulUrl() {
		return "/yhaguy/gestion/contabilidad/subdiario/visor_PlanCuenta.zul";
	}

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		
		ColumnaBrowser codigo = new ColumnaBrowser();
		codigo.setCampo("pc.codigo");
		codigo.setTitulo("Codigo");
		codigo.setWidthColumna("200px");
		codigo.setWidthComponente("170px");

		ColumnaBrowser descripcion = new ColumnaBrowser();
		descripcion.setCampo("pc.descripcion");
		descripcion.setTitulo("Descripcion");
		descripcion.setWidthColumna("640px");
		descripcion.setWidthComponente("610px");
		
		ColumnaBrowser tipo = new ColumnaBrowser();
		tipo.setCampo("t.descripcion");
		tipo.setTitulo("Tipo");
		tipo.setWidthColumna("400px");
		tipo.setWidthComponente("370px");
		
	
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(codigo);
		columnas.add(descripcion);
		columnas.add(tipo);		
		
		return columnas;
	}

	@Override
	public String getQuery() {		
		return this.query;
	}

	public void setQuery(String query) {
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
	
	@Override
	public DatosReporte getReporteVacio() {
		// TODO Auto-generated method stub
		return new ReporteVacio();
	}

	@Override
	public String getTituloReporte() {
		// TODO Auto-generated method stub
		return "Plan de Cuentas";
	}
	
	
	
	public static void main(String[] args) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
				
		String query = "Select pc.codigo, pc.descripcion, t.descripcion"
				+ " from PlanDeCuenta pc inner join pc.tipoCuenta t ";
		
		//System.out.println(query);
		List<Object[]> obj = rr.hql(query);
		for(Object oj[] : obj){

			System.out.println("\ncodigo: "+oj[0]+";	Descripcion: "+oj[1]+";	Tipo: "+oj[2]);
		}
		
	}

}
