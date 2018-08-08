package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.ArrayList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class ReportePlanCuenta extends ReporteYhaguy{

	// define las columnas del reporte
	
	
	static DatosColumnas col1 = new DatosColumnas("Coigo", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Descripcion", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("tipo", TIPO_STRING);

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	static {
		col1.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col2.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col3.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);

		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {

		// titulo del reporte
		this.setTitulo("Reporte Plan de Cuentas");

		// columnas del reporte
		this.setTitulosColumnas(cols);
		this.setBody(this.getCabecera());
	}

	private ComponentBuilder getCabecera() {
		VerticalListBuilder out = null;

		out = cmp.verticalList();

		return out;
	}

}
