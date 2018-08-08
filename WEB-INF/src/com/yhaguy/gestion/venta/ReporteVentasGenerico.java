package com.yhaguy.gestion.venta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.yhaguy.util.reporte.ReporteYhaguy;

/**
 * Reporte de Ventas Generico CON-00005..
 */
public class ReporteVentasGenerico extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private double totalSinIva;
	private Date desde;
	private Date hasta;
	private String vendedor;
	private String cliente;
	private String proveedor;
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 30);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Razón Social", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ruc", TIPO_STRING, 35);
	static DatosColumnas col5 = new DatosColumnas("Tipo", TIPO_STRING, 35);
	static DatosColumnas col6 = new DatosColumnas("Importe", TIPO_DOUBLE, 35,
			true);

	public ReporteVentasGenerico(double totalSinIva, Date desde, Date hasta,
			String vendedor, String cliente, String sucursal, String proveedor) {
		this.totalSinIva = totalSinIva;
		this.desde = desde;
		this.hasta = hasta;
		this.vendedor = vendedor;
		this.cliente = cliente;
		this.sucursal = sucursal;
		this.proveedor = proveedor;
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col5);
		cols.add(col6);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Listado de Ventas / Notas de Credito");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Total Sin IVA",
						FORMATTER.format(this.totalSinIva)))
				.add(this.textoParValor("Vendedor", this.vendedor))
				.add(this.textoParValor("Cliente", this.cliente)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.texto("")).add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}