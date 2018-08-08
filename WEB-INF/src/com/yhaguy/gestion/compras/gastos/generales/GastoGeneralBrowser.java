package com.yhaguy.gestion.compras.gastos.generales;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.OrdenPedidoGasto;

public class GastoGeneralBrowser extends Browser{

	@Override
	public void setingInicial() {
		this.setWidthWindows("100%");
		this.setHigthWindows("80%");
		this.addOrden("numero");
	}
	
	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col1_ = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();
		ColumnaBrowser col8 = new ColumnaBrowser();

		col1.setCampo("numero"); 	
		col1.setTitulo("Orden Nro.");
		col1.setWidthColumna("100px");	
		
		col1_.setCampo("numeroFactura"); 	
		col1_.setTitulo("Factura Nro.");
		col1_.setWidthColumna("100px");
		
		col2.setCampo("fechaCarga"); 	
		col2.setTitulo("Fecha");
		col2.setTipo(Config.TIPO_DATE);
		col2.setComponente(LABEL_DATE);
		
		col3.setCampo("proveedor.empresa.razonSocial"); 	
		col3.setTitulo("Proveedor");
		
		col4.setCampo("departamento.nombre"); 	
		col4.setTitulo("Departamento");	
		
		col5.setCampo("nombreUsuarioCarga"); 	
		col5.setTitulo("Usuario");		
		
		col6.setCampo("auxi"); 	
		col6.setTitulo("Descripción Cuenta");
		
		col7.setCampo("autorizado"); 
		col7.setWhere("autorizado = 'true'");
		col7.setTitulo("Autorizado");
		col7.setComponente("getCerradoComp");
		col7.setTipo(Config.TIPO_BOOL);
		col7.setWidthColumna("80px");	
		col7.setEstilo("text-align:center");
		
		col8.setCampo("condicionPago.descripcion"); 	
		col8.setTitulo("Condición");
		col8.setVisible(false);
		
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
		// TODO Auto-generated method stub
		return OrdenPedidoGasto.class;
	}

	@Override
	public String getTituloBrowser() {
		// TODO Auto-generated method stub
		return "Órdenes de Pedido Gasto";
	}	
}
