package com.yhaguy.gestion.compras.importacion;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.ImportacionPedidoCompra;

public class ImportacionPedidoCompraBrowser extends Browser{

	@Override
	public void setingInicial() {
		this.setWidthWindows("1200px");
		this.setHigthWindows("90%");
		this.addOrden("fechaCreacion desc, numeroPedidoCompra");
	}
	
	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col1_ = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col4_ = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();
		ColumnaBrowser col8 = new ColumnaBrowser();
		ColumnaBrowser col9 = new ColumnaBrowser();
		ColumnaBrowser col10 = new ColumnaBrowser();
		ColumnaBrowser col11 = new ColumnaBrowser();
		ColumnaBrowser col12 = new ColumnaBrowser();
		ColumnaBrowser col13 = new ColumnaBrowser();
		ColumnaBrowser col14 = new ColumnaBrowser();

		col1.setCampo("numeroPedidoCompra"); 	
		col1.setTitulo("Número");
		col1.setWidthColumna("85px");
		
		col1_.setCampo("numeroFactura"); 	
		col1_.setTitulo("Factura Nro.");
		col1_.setWidthColumna("85px");
		
		col2.setCampo("proveedor.empresa.razonSocial"); 	
		col2.setTitulo("Proveedor");
		
		col3.setCampo("estado.descripcion"); 	
		col3.setTitulo("Etapa Actual");
		col3.setWidthColumna("165px");
		col3.setVisible(false);
		
		col4.setCampo("fechaCreacion");
		col4.setTitulo("Fecha Creación");
		col4.setComponente(Browser.LABEL_DATE);
		col4.setTipo(Config.TIPO_DATE);
		col4.setWidthColumna("130px");
		col4.setWidthComponente("120px");
		
		col4_.setCampo("fechaFactura");
		col4_.setTitulo("Fecha Factura");
		col4_.setComponente(Browser.LABEL_DATE);
		col4_.setTipo(Config.TIPO_DATE);
		col4_.setWidthColumna("130px");
		col4_.setWidthComponente("120px");
		
		col5.setCampo("moneda.sigla");
		col5.setTitulo("Moneda");
		col5.setWidthColumna("60px");
		col5.setEstilo("text-align:center");
		
		col6.setCampo("propietarioActual");
		col6.setTitulo("Propietario");
		col6.setWidthColumna("95px");
		col6.setComponente("getPropietarioComp");
		col6.setVisible(false);
		
		col7.setCampo("confirmadoImportacion"); 	
		col7.setTitulo("Imp.");
		col7.setComponente("getConfirmadoComp");
		col7.setTipo(Config.TIPO_BOOL);
		col7.setWidthColumna("50px");
		col7.setEstilo("text-align:center");
		col7.setVisible(false);

		col8.setCampo("confirmadoVentas"); 	
		col8.setTitulo("Ven.");
		col8.setComponente("getConfirmadoComp");
		col8.setTipo(Config.TIPO_BOOL);
		col8.setVisible(true);
		col8.setWidthColumna("50px");
		col8.setEstilo("text-align:center");
		col8.setVisible(false);
		
		col9.setCampo("confirmadoAdministracion"); 	
		col9.setTitulo("Adm.");
		col9.setComponente("getConfirmadoComp");
		col9.setTipo(Config.TIPO_BOOL);
		col9.setVisible(true);
		col9.setWidthColumna("50px");
		col9.setEstilo("text-align:center");
		col9.setVisible(false);
	
		col10.setCampo("pedidoConfirmado"); 	
		col10.setTitulo("Confirmado");
		col10.setComponente("getCerradoComp");
		col10.setTipo(Config.TIPO_BOOL);
		col10.setWidthColumna("80px");
		col10.setEstilo("text-align:center");
		
		col11.setCampo("importacionConfirmada"); 	
		col11.setTitulo("Cerrado");
		col11.setComponente("getCerradoComp");
		col11.setTipo(Config.TIPO_BOOL);
		col11.setWidthColumna("80px");
		col11.setEstilo("text-align:center");

		col12.setCampo("cambio"); 	
		col12.setTitulo("Cambio");
		col12.setComponente(Browser.LABEL_NUMERICO);
		col12.setTipo(Config.TIPO_NUMERICO);
		col12.setWidthComponente("50px");
		col12.setVisible(false);
		
		col13.setCampo("totalImporteGs"); 	
		col13.setTitulo("Importe Gs.");
		col13.setComponente(Browser.LABEL_NUMERICO);
		col13.setTipo(Config.TIPO_NUMERICO);
		col13.setWidthColumna("120px");	
		
		col14.setCampo("totalImporteDs"); 	
		col14.setTitulo("Importe U$D.");
		col14.setComponente(Browser.LABEL_NUMERICO);
		col14.setTipo(Config.TIPO_NUMERICO);
		col14.setWidthColumna("120px");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col1_);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		columnas.add(col4_);
		columnas.add(col5);
		columnas.add(col13);
		columnas.add(col14);
		columnas.add(col6);
		columnas.add(col7);
		columnas.add(col8);
		columnas.add(col9);
		columnas.add(col10);
		columnas.add(col11);		
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
	
	public HtmlBasedComponent getConfirmadoComp(Object obj, Object[] datos) {
		Image img = (Image) this.getImagenCheck(obj, datos);
		if ((boolean) obj == true) {
			img.setTooltiptext("Confirmado..");
		} 
		return img;
	}
	
	public HtmlBasedComponent getPropietarioComp(Object obj, Object[] datos) {
		Label l = new Label();
		int propietario = (int) obj;
		if (propietario == Configuracion.PROPIETARIO_IMPORTACION_KEY) {
			l.setValue("Importación");
		} else if (propietario == Configuracion.PROPIETARIO_VENTAS_KEY) {
			l.setValue("Ventas");
		} else if (propietario == Configuracion.PROPIETARIO_ADMINISTRACION_KEY) {
			l.setValue("Administración");
		} else {
			l.setValue("");
		}
		return l;
	}
		

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		// TODO Auto-generated method stub
		return ImportacionPedidoCompra.class;
	}

	@Override
	public String getTituloBrowser() {
		// TODO Auto-generated method stub
		return "Órdenes de Compra Importación";
	}	
}
