package com.yhaguy.gestion.bancos.conciliacion;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.BancoExtracto;

public class BancoConciliacionBrowser extends Browser {
	
	@Override
	public void setingInicial() {
		this.setWidthWindows("1200px");
		this.setHigthWindows("90%");
		this.addOrden("numero");
	}

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();

		col1.setCampo("numero");
		col1.setTitulo("Número");

		col2.setCampo("banco.banco.descripcion");
		col2.setTitulo("Banco");

		col3.setCampo("desde");
		col3.setTitulo("Desde");
		col3.setComponente(Browser.LABEL_DATE_SIMPLE);
		
		col4.setCampo("hasta");
		col4.setTitulo("Hasta");
		col4.setComponente(Browser.LABEL_DATE_SIMPLE);
		
		col5.setCampo("cerrado");
		col5.setTitulo("Estado");
		col5.setComponente("getCerradoComp");
		col5.setWidthColumna("60px");
		col5.setEstilo("text-align:center");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);		
		columnas.add(col5);
		return columnas;
	}
	
	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos){
		Image img = new Image();		
		boolean cerrado = (boolean) obj;
		img.setSrc(cerrado? Config.IMAGEN_OK : Config.IMAGEN_CANCEL);
		
		return img;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return BancoExtracto.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Conciliaciones de Bancos";
	}
}
