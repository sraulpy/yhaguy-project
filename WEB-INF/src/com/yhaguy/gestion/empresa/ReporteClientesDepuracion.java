package com.yhaguy.gestion.empresa;

import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.functors.ForClosure;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import bsh.This;

import com.coreweb.componente.ViewPdf;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class ReporteClientesDepuracion extends ReporteYhaguy {

	Date fechaDesde;
	Date fechaHasta;
	String prioridad = "NO";
	String completo = "NO";

	// define las columnas del reporte
	static DatosColumnas col1 = new DatosColumnas("RUC", TIPO_STRING, 30);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING, 60);
	static DatosColumnas col3 = new DatosColumnas("Ult. Mod.", TIPO_DATE, 28);
	static DatosColumnas col4 = new DatosColumnas("PRI", TIPO_LONG, 15);
	static DatosColumnas col5 = new DatosColumnas("COMP", TIPO_BOOLEAN, 15);
	static DatosColumnas col6 = new DatosColumnas("Observación", TIPO_STRING, 95);

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	static {

		col1.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col2.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col3.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		col4.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		col5.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		col6.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);

		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {

		// titulo del reporte
		this.setTitulo("LISTADO DE CLIENTES - PARA DEPURACIÓN");

		// archivo de salida
		this.setDirectorio("/Clientes/");
		this.setNombreArchivo("cliente");

		// columnas del reporte
		this.setTitulosColumnas(cols);
		this.setBody(this.getCabecera());

		// orientacion: vertical / horizontal
		// this.setApaisada();
		this.setVertical();

		// Definir tipo de Pagina
		this.setOficio();
	}

	private ComponentBuilder getCabecera() {
		VerticalListBuilder out = null;

		out = cmp.verticalList();

		HorizontalListBuilder f1 = cmp.horizontalList();
		f1.add(this.textoParValor("Desde: ", this.fechaDesde));
		f1.add(this.textoParValor("Hasta: ", this.fechaHasta));
		
		HorizontalListBuilder f2 = cmp.horizontalList();
		f2.add(this.textoParValor("Prioridad: ", this.prioridad));
		f2.add(this.textoParValor("Completo: ", this.completo));

		out.add(f1);
		out.add(f2);

		return out;
	}

	public void reportePrioridad(Date fechaDesde, Date fechaHasta,
			boolean prioridad, boolean completo) throws Exception {
		
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		
		Misc m = new Misc();
		RegisterDomain rr = RegisterDomain.getInstance();

		String whereCompleto = "";
		if (completo == true) {
			whereCompleto = "AND cli.completo='true'";
			this.completo = "SI";
		}

		String orden = "order by emp.razonSocial";
		if (prioridad == true) {
			orden = "order by cli.prioridad ";
			this.prioridad = "SI";
		}
		
		String consulta = " "
				+ " select cli.id , emp.ruc , emp.razonSocial , emp.modificado , cli.prioridad , cli.completo , emp.observacion "
				+ " from  Cliente cli join cli.empresa emp "
				+ " where emp.modificado >= ? AND emp.modificado <= ? "
				+ whereCompleto +" "+ orden;

		System.out.println(consulta);
		
		Object[] param = new Object[2];
		param[0] = fechaDesde;
		param[1] = fechaHasta;


		List<Object[]> resultado1 = rr.hql(consulta, param);

		imprimirResultado(resultado1);

		List<Object[]> resultado = new ArrayList<>();
		List<Object[]> lista = resultado1;

		for (int i = 0; i < lista.size(); i++) {
			Object[] dato = lista.get(i);
			Object[] datoResultado = new Object[6];

			datoResultado[0] = dato[1];
			datoResultado[1] = dato[2];
			datoResultado[2] = dato[3];
			datoResultado[3] = dato[4];
			datoResultado[4] = dato[5];
			datoResultado[5] = dato[6];

			resultado.add(datoResultado);
		}
		// cargar los datos en el reporte
		this.setDatosReporte(resultado);

	}

	public static void main(String[] args) throws Exception {

		Misc m = new Misc();

		Date fechaDesde = m.agregarDias(new Date(), -1);
		Date fechaHasta = m.agregarDias(new Date(), +1);

		ReporteClientesDepuracion dr = new ReporteClientesDepuracion();
		dr.reportePrioridad(fechaDesde, fechaHasta, true, false);

		dr.setBorrarDespuesDeVer(false);
		dr.ejecutar(true);

	}

	private static void imprimirResultado(List<Object[]> pepe) {
		int j = 0;
		System.out.println("--------------------------------------------");
		for (Iterator iterator = pepe.iterator(); iterator.hasNext();) {
			Object[] o = (Object[]) iterator.next();

			String fila = "";
			for (int i = 0; i < o.length; i++) {
				fila += o[i] + " - ";
			}
			System.out.println((j++) + ") " + fila);
		}
		System.out.println("--------------------------------------------");
	}
}