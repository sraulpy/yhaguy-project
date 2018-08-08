package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.domain.Funcionario;

public class FuncionarioBrowser extends Browser {

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

		col1.setCampo("empresa.nombre");
		col1.setTitulo("Nombre");

		col2.setCampo("empresa.ci");
		col2.setTitulo("C.I.");

		col3.setCampo("empresa.ruc");
		col3.setTitulo("RUC");

		col4.setCampo("funcionarioCargo.descripcion");
		col4.setTitulo("Cargo");

		col5.setCampo("funcionarioEstado.descripcion");
		col5.setTitulo("Estado");

		col6.setCampo("empresa.fechaIngreso");
		col6.setTitulo("Fecha de Ingreso");
		col6.setComponente(LABEL_DATE);
		col6.setWidthColumna("150px");

		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		columnas.add(col5);
		columnas.add(col6);

		return columnas;
	}

	@Override
	public Class getEntidadPrincipal() {
		return Funcionario.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Funcionarios";
	}

}
