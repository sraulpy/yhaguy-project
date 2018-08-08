package com.yhaguy.gestion.notacredito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.util.Utiles;

public class NotaCreditoBrowser extends Browser {
	
	String where = "";
	boolean compra;
	
	public NotaCreditoBrowser(String where, boolean compra) {
		this.where = where;
		this.compra = compra;
	}
	
	@Override
	public void setingInicial() {
		this.setWidthWindows("1000px");
		this.setHigthWindows("80%");
		this.addOrden("id");
	}

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		
		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3_ = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		
		col1.setCampo("numero");
		col1.setTitulo("Número");
		col1.setWidthColumna("120px");
		
		col2.setCampo("fechaEmision");
		col2.setTitulo("Fecha Emisión");
		col2.setTipo(Config.TIPO_DATE);
		col2.setComponente(Browser.LABEL_DATE);
		col2.setWidthColumna("130px");
		
		if (compra) {
			col3_.setCampo("proveedor.empresa.razonSocial");
			col3_.setTitulo("Proveedor");
			col2.setValue(Utiles.getDateToString(new Date(), "yyyy"));
		} else {
			col3_.setCampo("cliente.empresa.razonSocial");
			col3_.setTitulo("Cliente");
			col2.setValue(Utiles.getDateToString(new Date(), "yyyy-MM-dd"));
		}
		
		col3.setCampo("motivo.descripcion");
		col3.setTitulo("Motivo");
		col3.setWidthColumna("120px");
		
		col4.setCampo("sucursal.descripcion");
		col4.setTitulo("Sucursal");	
		col4.setWidthColumna("130px");
		
		col5.setCampo("estadoComprobante.sigla");
		col5.setTitulo("Estado");
		col5.setComponente("getEstadoComp");
		col5.setWidthColumna("60px");
		col5.setEstilo("text-align:center");
		
		col6.setCampo("tipoMovimiento.descripcion");
		col6.setTitulo("Tipo Movimiento");
		col6.setVisible(false);
		col6.setWhere(this.where);
		
		List<ColumnaBrowser> cols = new ArrayList<ColumnaBrowser>();
		cols.add(col1);
		cols.add(col2);
		cols.add(col3_);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		
		return cols;
	}
	
	public HtmlBasedComponent getFechaComp(Object obj, Object[] datos) {
		Label l = (Label) this.getLabelDate(obj, datos);		
		return l;
	}
	
	public HtmlBasedComponent getEstadoComp(Object obj, Object[] datos) {
		Image img = new Image();

		String sigla = (String) obj;
		boolean pendiente = sigla
				.compareTo(Configuracion.SIGLA_ESTADO_COMPROBANTE_PENDIENTE) == 0;
		
		img.setSrc(pendiente? Config.ICONO_EXCLAMACION_16X16 : Config.IMAGEN_OK);
		img.setTooltiptext(pendiente? "pendiente" : "confirmado");

		return img;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return NotaCredito.class;
	}	

	@Override
	public String getTituloBrowser() {
		return "Notas de Crédito";
	}

}
