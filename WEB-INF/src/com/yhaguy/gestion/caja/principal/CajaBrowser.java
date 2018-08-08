package com.yhaguy.gestion.caja.principal;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Caja;

public class CajaBrowser extends Browser{

	@Override
	public void setingInicial() {
		this.setWidthWindows("900px");
		this.setHigthWindows("600px");
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
		ColumnaBrowser col8 = new ColumnaBrowser();

		col1.setCampo("numero"); 	
		col1.setTitulo("Numero");
		col1.setWidthColumna("90px");	
		
		col2.setCampo("fecha"); 	
		col2.setTitulo("Fecha");
		col2.setTipo(Config.TIPO_DATE);
		col2.setComponente(Browser.LABEL_DATE);
		col2.setWidthColumna("150px");
		
		col3.setCampo("clasificacion.descripcion"); 	
		col3.setTitulo("Clasificación");
		
		col4.setCampo("tipo.descripcion"); 	
		col4.setTitulo("Tipo");	
		
		col5.setCampo("responsable.empresa.razonSocial"); 	
		col5.setTitulo("Responsable");	
		
		col8.setCampo("estado.sigla");
		col8.setTitulo("Estado");
		col8.setComponente("getCerradoComp");
		col8.setEstilo("text-align:center");
		col8.setWidthColumna("80px");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col4);
		columnas.add(col5);
		columnas.add(col8);
		
		return columnas;
	}	
	
	UtilDTO utilDto = (UtilDTO) this.getDtoUtil();
	
	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos) {
		Image img = new Image();
		String s = (String) obj;
		if (s.compareTo(utilDto.getCajaEstadoActivo().getSigla()) == 0) {
			img.setSrc(Config.IMAGEN_OK);
			img.setTooltiptext("Activo..");
		} else {
			img.setSrc(Config.IMAGEN_CANCEL);
			img.setTooltiptext("Inactivo..");
		}
		return img;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		// TODO Auto-generated method stub
		return Caja.class;
	}

	@Override
	public String getTituloBrowser() {
		// TODO Auto-generated method stub
		return "Cajas";
	}	
}
