package com.yhaguy.gestion.bancos.descuentos;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.BancoDescuentoCheque;
import com.yhaguy.util.Utiles;

public class BancoDescuentoChequeBrowser extends Browser {
	
	private String where = "";
	
	public BancoDescuentoChequeBrowser(String where) {
		this.where = "(" + where + ")";
	}
	
	@Override
	public void setingInicial() {
		this.setWidthWindows("100%");
		this.setHigthWindows("90%");
		this.addOrden("fecha desc");
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
		ColumnaBrowser col8 = new ColumnaBrowser();

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
		
		col6.setCampo("totalImporte_gs");
		col6.setTitulo("Importe");
		col6.setWidthColumna("150px");
		col6.setComponente("getImporteGsComp");
		col6.setTipo(Config.TIPO_NUMERICO);
		
		col7.setCampo("auxi");
		col7.setWhere(this.where);
		col7.setWidthColumna("0px");
		
		col8.setCampo("confirmado"); 	
		col8.setTitulo("Est.");
		col8.setComponente("getCerradoComp");
		col8.setTipo(Config.TIPO_BOOL);
		col8.setWidthColumna("40px");	
		col8.setEstilo("text-align:center");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col5);
		columnas.add(col6);
		columnas.add(col7);
		columnas.add(col8);
		return columnas;
	}
	
	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos) {
		Image img = (Image) this.getImagenOKCancel(obj, datos);
		if ((boolean) obj == true) {
			img.setSrc(Config.IMAGEN_CHECK);
			img.setTooltiptext("Confirmado..");
		} else {
			img.setTooltiptext("Pendiente..");
			img.setSrc(Config.ICONO_EXCLAMACION_16X16);
		}
		return img;
	}
	
	public HtmlBasedComponent getImporteGsComp(Object obj, Object[] datos) {
		Label l = new Label();
		double importe = (double) obj;
		l.setValue(Utiles.getNumberFormat(importe));
		return l;
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
