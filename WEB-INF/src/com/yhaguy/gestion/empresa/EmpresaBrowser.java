package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.Cliente;

public class EmpresaBrowser extends Browser{

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
		//col1.setWidthColumna("85px");
		
		col2.setCampo("empresa.ruc"); 	
		col2.setTitulo("Ruc");
		
		col3.setCampo("empresa.ci"); 	
		col3.setTitulo("Cédula");
		//col3.setWidthColumna("165px");
		
		col4.setCampo("empresa.tipoPersona.descripcion");
		col4.setTitulo("Tipo");
		//col4.setWidthColumna("100px");
		
		col5.setCampo("empresa.nombre");
		col5.setTitulo("Nombre Fantasía");
		//col5.setWidthColumna("60px");
		//col5.setEstilo("text-align:center");
		
		col6.setCampo("categoriaCliente.descripcion");
		col6.setTitulo("Categoría");
		//col6.setWidthColumna("95px");
		//col6.setComponente("getPropietarioComp");
		
		col7.setCampo("tipoCliente.descripcion"); 	
		col7.setTitulo("Clasificación");
	
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		//columnas.add(col4);
		columnas.add(col5);
		columnas.add(col6);
		columnas.add(col7);
		
		return columnas;
	}
		

	@Override
	public Class getEntidadPrincipal() {
		return Cliente.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Clientes";
	}	
}
