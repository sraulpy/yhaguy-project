package com.yhaguy.gestion.compras.locales;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.util.Utiles;

public class CompraLocalBrowser extends Browser {

	@Override
	public void setingInicial() {
		this.setWidthWindows("1200px");
		this.setHigthWindows("85%");
		this.addOrden("id");
	}
	
	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col1_ = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();
		ColumnaBrowser col8 = new ColumnaBrowser();

		col1.setCampo("numero"); 	
		col1.setTitulo("Número");
		col1.setWidthColumna("100px");
		
		col1_.setCampo("numeroFactura"); 	
		col1_.setTitulo("Número Factura");
		col1_.setWidthColumna("150px");
		
		col2.setCampo("proveedor.empresa.razonSocial"); 	
		col2.setTitulo("Proveedor");
		
		col3.setCampo("fechaCreacion"); 	
		col3.setTitulo("Fecha Creación");
		col3.setTipo(Config.TIPO_DATE);
		col3.setComponente(Browser.LABEL_DATE);
		col3.setWidthColumna("170px");
		col3.setValue(Utiles.getDateToString(new Date(), "yyyy-MM"));		
		
		col4.setCampo("sucursal.descripcion"); 	
		col4.setTitulo("Sucursal");	
		col4.setVisible(false);

		col5.setCampo("condicionPago.descripcion"); 	
		col5.setTitulo("Condición");
		col5.setWidthColumna("100px");
		col5.setVisible(false);
		
		col6.setCampo("moneda.sigla"); 	
		col6.setTitulo("Moneda");
		col6.setWidthColumna("70px");
		col6.setEstilo("text-align:center");			
		
		col7.setCampo("autorizado"); 	
		col7.setTitulo("Autorizado");	
		col7.setWidthColumna("80px");
		col7.setComponente("getAutorizadoComp");
		col7.setEstilo("text-align:center");
		
		col8.setCampo("cerrado"); 	
		col8.setTitulo("Cerrado");	
		col8.setWidthColumna("80px");
		col8.setComponente("getCerradoComp");
		col8.setEstilo("text-align:center");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col1_);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		columnas.add(col5);
		columnas.add(col6);
		columnas.add(col7);
		columnas.add(col8);
		
		return columnas;
	}	
	
	public HtmlBasedComponent getImagenCheck(Object obj, Object[] datos) {
		Image img = new Image();
		if ((boolean) obj == true) {
			img.setSrc(Config.IMAGEN_CHECK);
		} else {
			img.setSrc(Config.ICONO_EXCLAMACION_16X16);
		}
		return img;
	}
	
	public HtmlBasedComponent getAutorizadoComp(Object obj, Object[] datos) {
		Image img = (Image) this.getImagenCheck(obj, datos);
		if ((boolean) obj == true) {
			img.setTooltiptext("Autorizado..");
		} else {
			img.setTooltiptext("Pendiente de Autorizar..");
		}
		return img;
	}
	
	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos) {
		Image img = (Image) this.getImagenCheck(obj, datos);
		if ((boolean) obj == true) {
			img.setTooltiptext("Cerrado..");
		} else {
			img.setTooltiptext("Pendiente..");
		}
		return img;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return CompraLocalOrden.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Compras Locales";
	}	
}
