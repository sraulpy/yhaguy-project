package com.yhaguy.gestion.transferencia;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.util.Utiles;

public class TransferenciaExternaBrowser extends Browser{

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		
		UtilDTO util = (UtilDTO) this.getDtoUtil();
		
		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();
		ColumnaBrowser col8 = new ColumnaBrowser();
		
		col1.setCampo("numero");
		col1.setTitulo("Número");
		col1.setWhere("transferenciaTipo.id = " + util.getTipoTransferenciaExterna().getId());
		col1.setWidthColumna("120px");
		
		col2.setCampo("depositoSalida.descripcion");
		col2.setTitulo("Depósito Salida");
		col2.setWidthColumna("150px");
		
		col3.setCampo("depositoEntrada.descripcion");
		col3.setTitulo("Depósito Entrada");
		col3.setWidthColumna("150px");
		
		col4.setCampo("fechaCreacion");
		col4.setTitulo("Fecha");
		col4.setTipo(Config.TIPO_DATE);
		col4.setComponente(LABEL_DATE);
		col4.setValue(Utiles.getDateToString(new Date(), "yyyy-MM"));
		col4.setWidthColumna("130px");
		
		col5.setCampo("transferenciaEstado.descripcion");
		col5.setTitulo("Estado");
		col5.setWidthColumna("130px");
		
		col6.setCampo("numeroRemision");
		col6.setTitulo("Nro. Remision");
		col6.setWidthColumna("130px");
		
		col7.setCampo("observacion");
		col7.setTitulo("Observación");
		
		col8.setCampo("transferenciaEstado.sigla");
		col8.setTitulo("Estado");
		col8.setComponente("getCerradoComp");
		col8.setWidthColumna("60px");
		col8.setEstilo("text-align:center");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		//columnas.add(col3);
		columnas.add(col4);
		columnas.add(col5);
		columnas.add(col6);
		columnas.add(col7);
		columnas.add(col8);
		
		return columnas;
	}
	
	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos){
		Image img = new Image();
		
		String sigla = (String) obj;		
		
		boolean confirmado = sigla.equals(Configuracion.SIGLA_ESTADO_TRANSF_CONFIRMADA);
		
		img.setSrc(confirmado? Config.IMAGEN_OK : Config.IMAGEN_CANCEL);
		
		return img;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return Transferencia.class;
	}

	@Override
	public void setingInicial() {
		this.setWidthWindows("100%");
		this.setHigthWindows("90%");
		this.addOrden("fechaCreacion");
	}

	@Override
	public String getTituloBrowser() {
		return "Transferencias Externas";
	}

}

