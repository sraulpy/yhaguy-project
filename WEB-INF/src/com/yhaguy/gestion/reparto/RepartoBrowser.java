package com.yhaguy.gestion.reparto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Reparto;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

public class RepartoBrowser extends Browser {

	AccesoDTO acceso = (AccesoDTO) this.getAtributoSession(Configuracion.ACCESO); // Acceso del usuario cte

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();

		UtilDTO util = (UtilDTO) this.getDtoUtil();
		
		col1.setCampo("numero");
		col1.setTitulo("Número");
		col1.setWhere("tipoReparto.id = " + util.getTipoRepartoYhaguy().getId());
		
		col2.setCampo("fechaCreacion");
		col2.setTitulo("Fecha");
		col2.setTipo(Config.TIPO_DATE);
		col2.setComponente(Browser.LABEL_DATE);
		col2.setValue(Utiles.getDateToString(new Date(), "yyyy-MM"));

		col3.setCampo("creador.empresa.razonSocial");
		col3.setTitulo("Creador");
		
		col6.setCampo("repartidor.empresa.razonSocial");
		col6.setTitulo("Repartidor");

		col4.setCampo("estadoReparto.descripcion");
		col4.setTitulo("Estado");

		// trae solo los repartos que coinciden con la sucursal operativa del
		// usuario cte
		col5.setCampo("sucursal.nombre");
		col5.setTitulo("Sucursal");
		col5.setWhere("sucursal.id = " + acceso.getSucursalOperativa().getId());

		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col6);
		columnas.add(col4);				
		return columnas;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return Reparto.class;
	}

	@Override
	public void setingInicial() {
		this.setWidthWindows("1100px");
		this.setHigthWindows("90%");
		this.addOrden("id");
	}

	@Override
	public String getTituloBrowser() {
		return "Repartos";
	}

}
