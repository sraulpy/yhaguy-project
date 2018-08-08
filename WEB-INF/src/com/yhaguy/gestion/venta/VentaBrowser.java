package com.yhaguy.gestion.venta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

public class VentaBrowser extends Browser{

	private String where = "";
	private String tipo;
	
	public VentaBrowser(String where, String tipo){
		this.where = "(" + where + ")";
		this.tipo = tipo;
	}
	
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
		ColumnaBrowser col8 = new ColumnaBrowser();
		ColumnaBrowser col9 = new ColumnaBrowser();
		ColumnaBrowser col10 = new ColumnaBrowser();

		col1.setCampo("numero"); 	
		col1.setTitulo("Número");
		col1.setWidthColumna("110px");
		
		col2.setCampo("fecha");
		col2.setTitulo("Fecha");
		col2.setWidthColumna("140px");
		col2.setTipo(Config.TIPO_DATE);
		col2.setComponente(Browser.LABEL_DATE);
		col2.setValue(Utiles.getDateToString(new Date(), "yyyy-MM-dd"));
		
		col3.setCampo("cliente.empresa.razonSocial"); 	
		col3.setTitulo("Cliente");
		
		col4.setCampo("cliente.empresa.ruc"); 	
		col4.setTitulo("Ruc");
		col4.setWidthColumna("90px");
		
		col5.setCampo("vendedor.empresa.razonSocial"); 	
		col5.setTitulo("Vendedor");
		col5.setWidthColumna("100px");
		
		col8.setCampo("tipoMovimiento.descripcion");
		col8.setTitulo("Tipo Movimiento");
		col8.setVisible(false);
		col8.setWhere(this.where);
		
		col9.setCampo("estado.sigla");
		col9.setTitulo("Estado");
		col9.setComponente("getCerradoComp");
		col9.setWidthColumna("60px");
		col9.setEstilo("text-align:center");
		
		col10.setCampo("totalImporteGs");
		col10.setTitulo("Importe Gs");
		col10.setComponente("getDoublebox");
		col10.setWidthColumna("80px");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		columnas.add(col5);
		columnas.add(col8);
		columnas.add(col10);
		columnas.add(col9);
		
		return columnas;
	}	
	
	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos){
		Image img = new Image();
		
		String sigla = (String) obj;		
		
		boolean pasadoApedido = sigla.compareTo(Configuracion.
				SIGLA_VENTA_ESTADO_PASADO_A_PEDIDO) == 0;
		boolean pedidoCerrado = sigla.compareTo(Configuracion.
				SIGLA_VENTA_ESTADO_CERRADO) == 0;
		boolean pedidoFacturado = sigla.compareTo(Configuracion.
				SIGLA_VENTA_ESTADO_FACTURADO) == 0;
		
		if (this.tipo.compareTo(VentaControlBody.TIPO_PRESUPUESTO) == 0) {
			img.setSrc(pasadoApedido? Config.IMAGEN_OK : Config.IMAGEN_CANCEL);
		} else {
			img.setSrc((pedidoCerrado || pedidoFacturado)? 
					Config.IMAGEN_OK : Config.IMAGEN_CANCEL);
		}
		
		return img;
	}
	
	public HtmlBasedComponent getDoublebox(Object obj, Object[] datos) {
		Doublebox t = new Doublebox();
		t.setFormat("###,###,###");
		t.setStyle("text-align: right; background:transparent; border: solid 0px");
		t.setReadonly(true);
		t.setHflex("true");
		t.setValue((double) obj);
		return t;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return Venta.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Pedidos de Venta";
	}	
}
