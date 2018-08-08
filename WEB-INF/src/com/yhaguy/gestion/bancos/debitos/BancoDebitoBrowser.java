package com.yhaguy.gestion.bancos.debitos;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.BancoDebito;

public class BancoDebitoBrowser extends Browser {

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		ColumnaBrowser numero = new ColumnaBrowser();
		ColumnaBrowser fecha = new ColumnaBrowser();
		ColumnaBrowser banco = new ColumnaBrowser();
		ColumnaBrowser descripcion = new ColumnaBrowser();
		ColumnaBrowser importe = new ColumnaBrowser();
		ColumnaBrowser confirmado = new ColumnaBrowser();
		
		numero.setCampo("numero");
		numero.setTitulo("Número");
		numero.setWidthColumna("110px");
		
		fecha.setCampo("fecha");
		fecha.setTitulo("Fecha");
		fecha.setTipo(Config.TIPO_DATE);
		fecha.setComponente(Browser.LABEL_DATE);
		
		banco.setCampo("cuenta.banco.descripcion");
		banco.setTitulo("Banco");
		
		descripcion.setCampo("descripcion");
		descripcion.setTitulo("Descripción");
		
		importe.setCampo("importe"); 	
		importe.setTitulo("Importe");
		importe.setComponente(Browser.LABEL_NUMERICO);
		importe.setWidthColumna("110px");
		
		confirmado.setCampo("confirmado");
		confirmado.setTitulo("Estado");
		confirmado.setComponente("getConfirmadoComp");
		confirmado.setWidthColumna("60px");
		confirmado.setEstilo("text-align:center");
		
		List<ColumnaBrowser> out = new ArrayList<ColumnaBrowser>();
		out.add(numero);
		out.add(fecha);
		out.add(banco);
		out.add(descripcion);
		out.add(importe);
		out.add(confirmado);
		
		return out;
	}
	
	public HtmlBasedComponent getConfirmadoComp(Object obj, Object[] datos){
		Image img = new Image();
		
		boolean confirmado = (boolean) obj;
		img.setSrc(confirmado? Config.IMAGEN_OK : Config.IMAGEN_CANCEL);
		
		return img;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return BancoDebito.class;
	}

	@Override
	public void setingInicial() {
		this.addOrden("id");
		this.setWidthWindows("900px");
		this.setHigthWindows("80%");
	}

	@Override
	public String getTituloBrowser() {
		return "Débitos Bancarios";
	}	
}
