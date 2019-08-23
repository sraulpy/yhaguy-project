package com.yhaguy.gestion.caja.periodo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.A;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.util.Utiles;

public class CajaPeriodoBrowser extends Browser {

	@Override
	public void setingInicial() {
		this.setWidthWindows("1200px");
		this.setHigthWindows("80%");
		this.addOrden("numero");
	}
	
	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		
		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();
		ColumnaBrowser col8 = new ColumnaBrowser();
		
		col1.setCampo("numero");
		col1.setTitulo("Número");
		col1.setWidthColumna("110px");
		
		col2.setCampo("apertura");
		col2.setTitulo("Apertura");
		col2.setTipo(Config.TIPO_DATE);
		col2.setComponente(LABEL_DATE);
		col2.setValue(Utiles.getDateToString(new Date(), "yyyy-MM-dd"));
		
		col3.setCampo("cierre");
		col3.setTitulo("Cierre");
		col3.setTipo(Config.TIPO_DATE);
		col3.setComponente(LABEL_DATE);
		
		col4.setCampo("responsable.empresa.razonSocial");
		col4.setTitulo("Responsable");
		
		col5.setCampo("estado.sigla");
		col5.setTitulo("Estado");
		col5.setComponente("getCerradoComp");
		col5.setWidthColumna("70px");
		col5.setEstilo("text-align:center");
		
		col6.setCampo("tipo");
		col6.setTitulo("Tipo");
		
		col7.setCampo("caja.sucursal.descripcion");
		col7.setTitulo("Sucursal");
		
		col8.setCampo("controlRendicion");
		col8.setTitulo("Rendición");
		col8.setComponente("getRendicionComp");
		col8.setWidthColumna("70px");
		col8.setEstilo("text-align:center");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		columnas.add(col6);
		columnas.add(col7);
		columnas.add(col5);
		columnas.add(col8);
		
		return columnas;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return CajaPeriodo.class;
	}	

	@Override
	public String getTituloBrowser() {
		return "Planillas de Caja";
	}

	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos){
		A img = new A();
		
		String sigla = (String) obj;
		boolean cerrado = sigla.equals(Configuracion.SIGLA_CAJA_PERIODO_CERRADA);
		
		img.setIconSclass(cerrado? "z-icon-lock" : "z-icon-unlock");
		img.setStyle(cerrado? "color:green;font-size:11pt" : "color:red;font-size:11pt");
		img.setTooltiptext(cerrado? "Cerrado" : "Abierto");
		
		return img;
	}
	
	public HtmlBasedComponent getRendicionComp(Object obj, Object[] datos) {
		A img = new A();
		
		boolean cerrado = (boolean) obj;
		
		img.setIconSclass(cerrado? "z-icon-check-circle" : "z-icon-exclamation-circle");
		img.setStyle(cerrado? "color:green;font-size:11pt" : "color:red;font-size:11pt");
		img.setTooltiptext(cerrado? "Controlado" : "Pendiente");
		
		return img;
	}
}
