package com.yhaguy.gestion.compras.gastos.generales.pedidos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.util.Utiles;

public class OrdenPedidoGastoBrowser extends Browser{

	@Override
	public void setingInicial() {
		this.setWidthWindows("1200px");
		this.setHigthWindows("85%");
		this.addOrden("id");
	}
	
	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col1_ = new ColumnaBrowser();
		ColumnaBrowser col1_1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();
		ColumnaBrowser col8 = new ColumnaBrowser();

		col1.setCampo("numero"); 	
		col1.setTitulo("Orden Nro.");
		col1.setWidthColumna("100px");
		
		col1_.setCampo("numeroFactura"); 	
		col1_.setTitulo("Factura Nro.");
		col1_.setWidthColumna("130px");
		
		col1_1.setCampo("numeroImportacion"); 	
		col1_1.setTitulo("Importación Nro.");
		col1_1.setWidthColumna("150px");
		col1_1.setVisible(false);
		
		col2.setCampo("fechaCarga"); 	
		col2.setTitulo("Fecha");
		col2.setTipo(Config.TIPO_DATE);
		col2.setComponente(LABEL_DATE);
		col2.setWidthColumna("125px");
		col2.setValue(Utiles.getDateToString(new Date(), "yyyy-MM"));
		
		col3.setCampo("proveedor.empresa.razonSocial"); 	
		col3.setTitulo("Proveedor");
		
		col4.setCampo("auxi"); 	
		col4.setTitulo("Descripción Cuenta");
		
		col5.setCampo("descripcion");
		col5.setTitulo("Descripción Gasto");
		
		col7.setCampo("confirmado"); 	
		col7.setTitulo("Est.");
		col7.setComponente("getCerradoComp");
		col7.setTipo(Config.TIPO_BOOL);
		col7.setWidthColumna("40px");	
		col7.setEstilo("text-align:center");
		
		col8.setCampo("condicionPago.descripcion"); 	
		col8.setTitulo("Condición");
		col8.setVisible(false);
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col1_);
		columnas.add(col1_1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		columnas.add(col5);
		columnas.add(col7);
		columnas.add(col8);
		
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
		return OrdenPedidoGasto.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Órdenes de Pedido Gasto";
	}	
}
