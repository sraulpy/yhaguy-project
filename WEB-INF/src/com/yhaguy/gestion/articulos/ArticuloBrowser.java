package com.yhaguy.gestion.articulos;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.Articulo;

public class ArticuloBrowser extends Browser{

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser codInterno = new ColumnaBrowser();
		ColumnaBrowser colOriginal = new ColumnaBrowser();
		ColumnaBrowser colProveedor = new ColumnaBrowser();
		ColumnaBrowser colDescripcion = new ColumnaBrowser();

		codInterno.setCampo("codigoInterno"); 	
		codInterno.setTitulo("Cod. Interno");
		codInterno.setWidthColumna("130px");
		
		colOriginal.setCampo("codigoOriginal"); 	
		colOriginal.setTitulo("Cod. Original");
		colOriginal.setWidthColumna("130px");
		
		colProveedor.setCampo("codigoProveedor"); 	
		colProveedor.setTitulo("Cod. Proveedor");
		colProveedor.setWidthColumna("130px");
		
		colDescripcion.setCampo("descripcion"); 	
		colDescripcion.setTitulo("Descripción");
		
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(codInterno);
		columnas.add(colProveedor);
		columnas.add(colOriginal);
		columnas.add(colDescripcion);
		
		return columnas;
	}	

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		// TODO Auto-generated method stub
		return Articulo.class;
	}

	@Override
	public void setingInicial() {
		this.addOrden("descripcion");
		this.setWidthWindows("900px");
		this.setHigthWindows("80%");
	}

	@Override
	public String getTituloBrowser() {
		return "Artículo";
	}

}
