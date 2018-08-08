package com.yhaguy.gestion.bancos.libro;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.BancoCta;

public class BancoBrowser extends Browser{

	@Override
	public void setingInicial() {
		this.setWidthWindows("980px");
		this.setHigthWindows("80%");
		this.addOrden("nroCuenta");
	}
	
	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();

		col1.setCampo("nroCuenta"); 	
		col1.setTitulo("Número de Cuenta");
		col1.setComponente(LABEL_NUMERICO);
		col1.setTipo(Config.TIPO_NUMERICO);
		
		col2.setCampo("fechaApertura"); 	
		col2.setTitulo("Fecha de Apertura");
		col2.setComponente(LABEL_DATE);
		col2.setTipo(Config.TIPO_DATE);
		
		col3.setCampo("banco.descripcion"); 	
		col3.setTitulo("Banco");

		col4.setCampo("moneda.descripcion"); 	
		col4.setTitulo("Moneda");		
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		
		return columnas;
	}	

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return BancoCta.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Libro Banco";
	}	
}
