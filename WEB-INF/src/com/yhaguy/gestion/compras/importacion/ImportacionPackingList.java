package com.yhaguy.gestion.compras.importacion;

import java.util.ArrayList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class ImportacionPackingList extends ReporteYhaguy {

	String proveedor = "";
	String factura = "";
	String numeroOrden = " ";
	String contador = "";

	public String getEncargado() {
		return contador;
	}

	public void setEncargado(String encargado) {
		this.contador = encargado;
	}

	public String getNumeroOrden() {
		return numeroOrden;
	}

	public String getFactura() {
		return factura;
	}

	public String getProvedor() {
		return proveedor;
	}

	public void setNumeroOrden(String NumeroOrden) {
		this.numeroOrden = NumeroOrden;
	}

	public void setFactura(String Fecha) {
		this.factura = Fecha;

	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	// define las columnas
	static DatosColumnas col1 = new DatosColumnas("Codigo Fabrica", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Codigo Interno", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Descripcion ", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Cant 1 ", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Cant 2 ", TIPO_STRING);
	static DatosColumnas col6 = new DatosColumnas("Cant 3 ", TIPO_STRING);

	// TIPO_STRING);

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	static {
		// col1.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		col2.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);
		// col3.setAlineacionColuman(COLUMNA_ALINEADA_IZQUIERDA);

		// Ancho de cada Columna se define en esta parte
		col4.setAncho(20);
		col5.setAncho(20);
		col6.setAncho(20);
		col1.setAncho(47);
		col2.setAncho(47);

		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);

	}

	@Override
	public void informacionReporte() {
		// Titulo
		this.setTitulo("Reporte Importacion Packinglist");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCabecera());

	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCabecera() {

		VerticalListBuilder out = null;

		out = cmp.verticalList();

		HorizontalListBuilder f1 = cmp.horizontalList();
		f1.add(this.textoParValor("Proveedor ", this.proveedor));
		f1.add(this.textoParValor("Fecha ", "        /        /       "));

		HorizontalListBuilder f2 = cmp.horizontalList();
		f2.add(this.textoParValor("Nro. Orden Compra ", this.numeroOrden));
		f2.add(this.textoParValor("Contador ", this.contador));

		HorizontalListBuilder f3 = cmp.horizontalList();

		f3.add(this.textoParValor("Factura ", this.factura));

		out.add(f1);
		out.add(f2);
		out.add(f3);

		return out;
	}

	public static void main(String[] args) throws Exception {

		List<Object[]> data = new ArrayList<>();
		ImportacionPackingList dr = new ImportacionPackingList();

		dr.setProveedor("");
		dr.Fecha("");
		dr.setNumeroOrden("");
		dr.setDatosReporte(data);
		dr.ejecutar(true);

	}

	private void Fecha(String string) {
		// TODO Auto-generated method stub

	}

}
