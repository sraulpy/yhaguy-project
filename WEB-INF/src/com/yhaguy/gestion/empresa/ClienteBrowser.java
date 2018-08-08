package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.Cliente;

public class ClienteBrowser extends Browser{

	@Override
	public void setingInicial() {
		this.setWidthWindows("1020px");
		this.setHigthWindows("80%");
	}
	
	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		ColumnaBrowser col7 = new ColumnaBrowser();

		col1.setCampo("empresa.razonSocial"); 	
		col1.setTitulo("Razón Social");
		
		col2.setCampo("empresa.nombre");
		col2.setTitulo("Nombre Fantasía");
		
		col3.setCampo("empresa.ruc"); 	
		col3.setTitulo("Ruc");
		col3.setWidthColumna("150px");
		
		col4.setCampo("empresa.ci"); 	
		col4.setTitulo("Cédula");
		col4.setWidthColumna("150px");
		
		col5.setCampo("empresa.tipoPersona.descripcion");
		col5.setTitulo("Tipo");
		col5.setWidthColumna("150px");
		
		col6.setCampo("categoriaCliente.descripcion");
		col6.setTitulo("Categoría");
		col6.setWidthColumna("150px");
		col6.setVisible(false);
		
		col7.setCampo("tipoCliente.descripcion"); 	
		col7.setTitulo("Clasificación");
		col7.setWidthColumna("150px");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		//columnas.add(col5);
		columnas.add(col6);
		columnas.add(col7);
		
		return columnas;
	}
		

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return Cliente.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Clientes";
	}	
}
