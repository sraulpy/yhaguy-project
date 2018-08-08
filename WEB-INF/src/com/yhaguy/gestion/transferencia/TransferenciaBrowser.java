package com.yhaguy.gestion.transferencia;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.Config;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.inicio.AccesoDTO;

public class TransferenciaBrowser extends Browser{
	
	AccesoDTO acceso = (AccesoDTO) this
			.getAtributoSession(Configuracion.ACCESO); // Acceso del usuario cte
	
	// falta el filtro por sucursal operativa del usuario
	
	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		
		UtilDTO util = (UtilDTO) this.getDtoUtil();
		
		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();
		ColumnaBrowser col6 = new ColumnaBrowser();
		
		col1.setCampo("numero");
		col1.setTitulo("Número");
		col1.setWhere("transferenciaTipo.id = " + util.getTipoTransferenciaInterna().getId());
		
		col2.setCampo("depositoSalida.descripcion");
		col2.setTitulo("Depósito Salida");
		
		col3.setCampo("depositoEntrada.descripcion");
		col3.setTitulo("Depósito Entrada");
		
		col4.setCampo("fechaCreacion");
		col4.setTitulo("Creacion Pedido");
		col4.setTipo(Config.TIPO_DATE);
		col4.setComponente(LABEL_DATE);
		
		col5.setCampo("transferenciaEstado.descripcion");
		col5.setTitulo("Estado");
		
		col6.setCampo("observacion");
		col6.setTitulo("Observación");
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		columnas.add(col5);
		columnas.add(col6);
		
		return columnas;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return Transferencia.class;
	}

	@Override
	public void setingInicial() {
		this.setWidthWindows("850px");
		this.setHigthWindows("75%");
		this.addOrden("fechaCreacion");
	}

	@Override
	public String getTituloBrowser() {
		return "Transferencias Internas";
	}

}

