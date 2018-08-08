package com.yhaguy.gestion.caja.principal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class ReporteCajaPrincipal extends ReporteYhaguy {

	String numero = "";
	String creadoPor = "";
	String responsable = "";
	Date fechaHoraCreacion;
	String sucursal = "";
	String estado = "";

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCreadoPor() {
		return creadoPor;
	}

	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public Date getFechaHoraCreacion() {
		return fechaHoraCreacion;
	}

	public void setFechaHoraCreacion(Date fechaHoraCreacion) {
		this.fechaHoraCreacion = fechaHoraCreacion;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	// Columnas del Reporte

	static DatosColumnas col1 = new DatosColumnas("Tipo", TIPO_STRING, 47);
	static DatosColumnas col2 = new DatosColumnas("Cierre", TIPO_STRING, 35);
	static DatosColumnas col3 = new DatosColumnas("Descripcion", TIPO_STRING,
			70);
	static DatosColumnas col4 = new DatosColumnas("Supervisor", TIPO_DOUBLE, 55);

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	static {

		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);

	};

	@Override
	public void informacionReporte() {
		// titulo del reporte
		this.setTitulo("Caja Principal");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCabecera());

	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCabecera() {

		VerticalListBuilder out = null;

		out = cmp.verticalList();

		HorizontalListBuilder f1 = cmp.horizontalList();
		f1.add(this.textoParValor("Número ", this.numero));
		f1.add(this.textoParValor("Fecha Hora Creacion ",this.fechaHoraCreacion));

		HorizontalListBuilder f2 = cmp.horizontalList();
		f2.add(this.textoParValor("Creado Por ", this.creadoPor));
		f2.add(this.textoParValor("Sucursal ", this.sucursal));

		HorizontalListBuilder f3 = cmp.horizontalList();
		f3.add(this.textoParValor("Responsable ", this.responsable));
		f3.add(this.textoParValor("Estado ", this.estado));

		out.add(f1);
		out.add(f2);
		out.add(f3);

		out.add(cmp.horizontalFlowList().add(this.texto("")));
		return out;
	}

	public static void main(String[] args) throws Exception {

		List<Object[]> data = new ArrayList<>();
		ReporteCajaPrincipal rcp = new ReporteCajaPrincipal();
		rcp.setCreadoPor("");
		rcp.setResponsable("");
		rcp.setNumero("");
		rcp.setSucursal("");
		rcp.setEstado("");

		rcp.setDatosReporte(data);
		rcp.ejecutar(true);

	}

}
