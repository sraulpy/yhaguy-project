package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.A;

import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;

public class FuncionarioBrowser extends Browser {

	@Override
	public void setingInicial() {
		this.setWidthWindows("100%");
		this.setHigthWindows("90%");
		this.addOrden("apellidos, nombres");
	}

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		// TODO Auto-generated method stub

		ColumnaBrowser col0 = new ColumnaBrowser();
		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();
		ColumnaBrowser col5 = new ColumnaBrowser();

		col0.setCampo("apellidos");
		col0.setTitulo("Apellidos");
		
		col1.setCampo("nombres");
		col1.setTitulo("Nombres");

		col2.setCampo("empresa.ci");
		col2.setTitulo("Cédula Identidad");

		col4.setCampo("funcionarioCargo.descripcion");
		col4.setTitulo("Cargo");
		
		col5.setCampo("funcionarioEstado.sigla");
		col5.setTitulo("Estado");
		col5.setComponente("getEstadoComp");
		col5.setWidthColumna("70px");
		col5.setEstilo("text-align:center");
		col5.setWhere("funcionarioEstado.sigla = '" + Configuracion.SIGLA_TIPO_FUNCIONARIO_ESTADO_ACTIVO + "'");

		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col0);
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col4);
		columnas.add(col5);

		return columnas;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return Funcionario.class;
	}

	@Override
	public String getTituloBrowser() {
		return "Funcionarios";
	}

	public HtmlBasedComponent getEstadoComp(Object obj, Object[] datos){
		A img = new A();
		
		String sigla = (String) obj;
		boolean activo = sigla.equals(Configuracion.SIGLA_TIPO_FUNCIONARIO_ESTADO_ACTIVO);
		
		img.setIconSclass(activo? "z-icon-check" : "z-icon-times-circle");
		img.setStyle(activo? "color:green;font-size:11pt" : "color:red;font-size:11pt");
		img.setTooltiptext(activo? "Activo" : "Inactivo");
		
		return img;
	}
}
