package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class ReporteLibroCompra extends ReporteYhaguy {

	String fechaDeste = "";
	String fechaHasta = "";

	// define las columnas del reporte
	
	
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_DATE,120);
	static DatosColumnas col2 = new DatosColumnas("Tipo", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Numero", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Timbrado", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Proveedor", TIPO_STRING, 120);
	static DatosColumnas col6 = new DatosColumnas("Ruc", TIPO_STRING);
	static DatosColumnas col7 = new DatosColumnas("Importe", TIPO_DOUBLE);
	static DatosColumnas col8 = new DatosColumnas("Moneda", TIPO_STRING);
	static DatosColumnas col9 = new DatosColumnas("Cambio", TIPO_DOUBLE);
	static DatosColumnas col10 = new DatosColumnas("Tipo IVA", TIPO_STRING);
	static DatosColumnas col11 = new DatosColumnas("Gravada", TIPO_DOUBLE);
	static DatosColumnas col12 = new DatosColumnas("IVA", TIPO_DOUBLE);
	static DatosColumnas col13 = new DatosColumnas("Exenta", TIPO_DOUBLE);

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	static {
		col1.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col2.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col3.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col4.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col5.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col6.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col7.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col8.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col9.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col10.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		col11.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col12.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col13.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);

		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
		cols.add(col9);
		cols.add(col10);
		cols.add(col11);
		cols.add(col12);
		cols.add(col13);
	}

	@Override
	public void informacionReporte() {

		// titulo del reporte
		this.setTitulo("Reporte Libro Compra");

		// columnas del reporte
		this.setTitulosColumnas(cols);
		this.setBody(this.getCabecera());
	}

	private ComponentBuilder getCabecera() {
		VerticalListBuilder out = null;

		out = cmp.verticalList();

		HorizontalListBuilder f1 = cmp.horizontalList();
		f1.add(this.textoParValor("Fecha desde", this.fechaDeste));
		f1.add(this.textoParValor("Fecha hasta", this.fechaHasta));
		out.add(f1);
		return out;
	}

	// gets sets

	public String getFechaDeste() {
		return fechaDeste;
	}

	public void setFechaDeste(String fechaDeste) {
		this.fechaDeste = fechaDeste;
	}

	public String getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(String fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public static void main(String[] args) throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		
		String query = "Select cf.emision, cf.condicion, cf.numero, cf.timbrado, cf.razonSocial, cf.ruc, cf.importeGs,"
				+ " cf.moneda, cf.tipoCambio, cf.tipoIva, cf.gravada, cf.iva, cf.exenta"
				+ " from CompraFiscal cf  ";
		System.out.println(query);
		List<Object[]> data = rr.hql(query);

		ReporteLibroCompra dr = new ReporteLibroCompra();
		dr.setDatosReporte(data);
		dr.setFechaDeste("21-06-2015");
		dr.setFechaHasta("23-06-2015");
		dr.setApaisada();

		dr.ejecutar(true);
	}

}
