package com.yhaguy.util.reporte;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.extras.reporte.DatosColumnas;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.constant.StretchType;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;

public class ReportePrueba2 extends ReporteYhaguy {

	String cliente = "Daniel Romero";
	String cuenta = "112346";
	long saldo = 10000;
	long deuda = 1234567890;

	@Override
	public void informacionReporte() {
		// TODO Auto-generated method stub
		// titulo del reporte
		this.setTitulo("Prueba reporte BODY");

		// archivo de salida
		// this.setArchivo("/prueba/prueba-reporte2.pdf");
		this.setBody(this.getCuerpo(cliente, cuenta, saldo, deuda));
		// this.setFooter(this.getCuerpo(cliente, cuenta, saldo, deuda));

	}

	private ComponentBuilder getCuerpo(String cliente, String cuenta,
			long saldo, long deuda) {
		// ComponentBuilder out = null;
		VerticalListBuilder out = null;
		// HorizontalListBuilder out = null;

		out = cmp.verticalList();

		// out.add(cmp.horizontalList().add(cmp.text("Cliente:"+cliente)).add(cmp.text("Saldo:"+saldo)));
		// out.add(cmp.horizontalList().add(cmp.text("Cuenta:"+cuenta)).add(cmp.text("Deuda:"+deuda)));

		// out.add(cmp.horizontalFlowList().add(this.textoParValor("Cliente",
		// cliente)).add(this.textoParValor("Saldo",saldo )));
		// out.add(cmp.horizontalList().add(this.textoParValor("Cuenta",
		// cuenta)).add(this.textoParValor("Deuda",deuda )));

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Cliente", cliente))
				.add(this.textoParValor("Saldo", saldo))
				.add(this.textoParValor("Cuenta", cuenta))
				.add(this.textoParValor("Deuda", deuda)));

		out.add(cmp.verticalGap(30));
		HorizontalListBuilder hh = cmp.horizontalList();
		
		
		
		hh.add(this.getTabla());
		hh.add(this.getTabla());
		out.add(hh);
		return out;
	}

	private ComponentBuilder getTabla() {
		ComponentBuilder out = null;

		String[][] cols = { { "Col1",  WIDTH + "20" },
				{ "Col2",  DERECHA + WIDTH + "20" },
				{ "Col3", DERECHA + WIDTH + "20" } };

		List<Object[]> datos = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Object[] d = { "Dato c1Dato c1Dato c1Dato c1Dato c1Dato c1 c1Dato c1 c1Dato c1 c1Dato c1 c1Dato c1-" + i, "Dato c2Dato c2Dato c2Dato c2-" + i, i };
			datos.add(d);
		}

		String prop = TABLA_TITULO+"Titulo de la Tabla";
		out = this.getTabla(cols, datos, prop);
		return out;
	}

	public static void main(String[] args) {

		ReportePrueba2 rp = new ReportePrueba2();
		rp.setUsuario("Daniel");
		rp.setDirectorioBase("/home/rodrigo/Escritorio/reportes-jasper");
		rp.setEmpresa("Empresa Yhaguy S.A.");
		rp.setBorrarDespuesDeVer(true);
		rp.ejecutar(true);

	}

}
