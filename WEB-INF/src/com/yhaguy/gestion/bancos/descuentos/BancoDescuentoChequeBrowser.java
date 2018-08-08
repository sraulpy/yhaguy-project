package com.yhaguy.gestion.bancos.descuentos;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.BancoDescuentoCheque;

public class BancoDescuentoChequeBrowser extends Browser {
	
	private String where = "";
	
	public BancoDescuentoChequeBrowser(String where) {
		this.where = "(" + where + ")";
	}
	
	@Override
	public void setingInicial() {
		this.setWidthWindows("1100px");
		this.setHigthWindows("90%");
		this.addOrden("fecha");
	}

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();

		col1.setCampo("id");
		col1.setTitulo("ID");
		col1.setTipo(LONG_BOX);
		
		col2.setCampo("fecha");
		col2.setTitulo("Fecha");
		col2.setWidthColumna("150px");
		col2.setTipo(Config.TIPO_DATE);
		col2.setComponente(Browser.LABEL_DATE);
		
		col3.setCampo("banco.banco.descripcion");
		col3.setTitulo("Banco");
		col3.setWidthColumna("150px");
		
		col5.setCampo("observacion");
		col5.setTitulo("Detalles");
		
		col6.setCampo("auxi");
		col6.setWhere(this.where);
		col6.setWidthColumna("0px");
		
		col7.setCampo("confirmado"); 	
		col7.setTitulo("Est.");
		col7.setComponente("getCerradoComp");
		col7.setTipo(Config.TIPO_BOOL);
		col7.setWidthColumna("40px");	
		col7.setEstilo("text-align:center");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col5);
		columnas.add(col6);
		columnas.add(col7);
		return columnas;
	}
	
	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos) {
		Image img = (Image) this.getImagenOKCancel(obj, datos);
		if ((boolean) obj == true) {
			img.setTooltiptext("Confirmado..");
		} else {
			img.setTooltiptext("Pendiente..");
		}
		return img;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return BancoDescuentoCheque.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Descuento de Cheques";
	}

}
