package com.yhaguy.gestion.compras.gastos.generales;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class ReporteGastoGeneral extends ReporteYhaguy {

	String TITULO = "Sub-Diario Gastos Generales";
	String URL_ARCHIVO = "compras/gastos/";
	String NOMBRE_ARCHIVO = "gasto_general";

	// Columnas del Reporte
	static DatosColumnas col1 = new DatosColumnas("Tipo", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Código", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Descripción Cuenta",
			TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Descripción del Item",
			TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE, 40,
			true);

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	};

	private String nroPedido;
	private String proveedorRuc;
	private String proveedorRazonSocial;
	private String sucursal;
	private Date fechaPedido;
	Misc misc = new Misc();

	private String subDiario;
	private String subDiarioDescripcion;
	private Date subDiarioFecha;

	public String getSubDiario() {
		return subDiario;
	}

	public void setSubDiario(String subDiario) {
		this.subDiario = subDiario;
	}

	public String getSubDiarioDescripcion() {
		return subDiarioDescripcion;
	}

	public void setSubDiarioDescripcion(String subDiarioDescripcion) {
		this.subDiarioDescripcion = subDiarioDescripcion;
	}

	public Date getSubDiarioFecha() {
		return subDiarioFecha;
	}

	public void setSubDiarioFecha(Date subDiarioFecha) {
		this.subDiarioFecha = subDiarioFecha;
	}

	public String getNroPedido() {
		return nroPedido;
	}

	public void setNroPedido(String nroPedido) {
		this.nroPedido = nroPedido;
	}

	public String getProveedorRuc() {
		return proveedorRuc;
	}

	public void setProveedorRuc(String proveedorRuc) {
		this.proveedorRuc = proveedorRuc;
	}

	public String getProveedorRazonSocial() {
		return proveedorRazonSocial;
	}

	public void setProveedorRazonSocial(String proveedorRazonSocial) {
		this.proveedorRazonSocial = proveedorRazonSocial;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public Date getFechaPedido() {
		return fechaPedido;
	}

	public void setFechaPedido(Date fechaPedido) {
		this.fechaPedido = fechaPedido;
	}

	@Override
	public void informacionReporte() {
		this.setTitulo(this.TITULO);
		this.setDirectorio(this.URL_ARCHIVO);
		this.setNombreArchivo(this.NOMBRE_ARCHIVO);
		this.setTitulosColumnas(cols);
		if (this.subDiario == null) {
			this.setBody(this.getCuerpo(nroPedido, proveedorRuc, sucursal,
					fechaPedido, null, null, null));
		} else {
			this.setBody(this.getCuerpo(nroPedido, proveedorRuc, sucursal,
					fechaPedido, subDiario, subDiarioDescripcion,
					subDiarioFecha));
		}
	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo(String nroPedido, String proveedor,
			String sucursal, Date fechaPedido, String subDiario,
			String subDiarioDescripcion, Date subDiarioFecha) {

		Misc m = new Misc();
		String fechaPedidoStr = m.dateToString(fechaPedido,
				Misc.DD_MM__YYY_HORA_MIN);
		Misc m1 = new Misc();
		String subDiarioFechaSt = m1.dateToString(subDiarioFecha,
				Misc.DD_MM__YYY_HORA_MIN);


		VerticalListBuilder out = null;
		out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("NÚMERO PEDIDO", nroPedido))
				.add(this.textoParValor("FECHA PEDIDO", fechaPedidoStr)));

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("PROVEEDOR",this.proveedorRuc + this.proveedorRazonSocial))
				.add(this.textoParValor("SUCURSAL", sucursal)));

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("NÚMERO SUB-DIARIO", subDiario))
				.add(this.textoParValor("FECHA SUB-DIARIO", subDiarioFechaSt)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("DESCRIPCIÓN SUB-DIARIO",
						subDiarioDescripcion)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
