package com.yhaguy.gestion.bancos.depositos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Image;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.util.Utiles;

public class BancoBoletaDepositoBrowser extends Browser {
	
	@Override
	public void setingInicial() {
		this.setWidthWindows("1200px");
		this.setHigthWindows("90%");
		this.addOrden("id");
	}

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();

		col1.setCampo("nroCuenta.nroCuenta");
		col1.setTitulo("Numero Cuenta");

		col3.setCampo("fecha");
		col3.setTitulo("Fecha");
		col3.setTipo(Config.TIPO_DATE);
		col3.setComponente(Browser.LABEL_DATE);
		col3.setValue(Utiles.getDateToString(new Date(), "yyyy-MM"));

		col4.setCampo("numeroBoleta");
		col4.setTitulo("Numero Boleta");
		
		col5.setCampo("monto");
		col5.setTitulo("Monto");
		col5.setComponente("getDoublebox");
		
		col6.setCampo("nroCuenta.banco.descripcion");
		col6.setTitulo("Banco");
		
		col7.setCampo("cerrado");
		col7.setTitulo("Estado");
		col7.setComponente("getCerradoComp");
		col7.setWidthColumna("60px");
		col7.setEstilo("text-align:center");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col4);
		columnas.add(col3);
		columnas.add(col1);
		columnas.add(col6);		
		columnas.add(col5);
		columnas.add(col7);
		return columnas;
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
	
	public HtmlBasedComponent getCerradoComp(Object obj, Object[] datos){
		Image img = new Image();		
		boolean cerrado = (boolean) obj;
		img.setSrc(cerrado? Config.IMAGEN_OK : Config.IMAGEN_CANCEL);
		
		return img;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return BancoBoletaDeposito.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Boletas de Depósito";
	}

}
