package com.yhaguy.gestion.empresa.ctacte;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.compras.locales.CompraLocalOrdenDTO;
import com.yhaguy.gestion.empresa.EmpresaDTO;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class ReporteMovimientosCtaCte extends ReporteYhaguy {

	EmpresaDTO empresa;
	List<MyArray> movimientos = new ArrayList<MyArray>();
	boolean isCliente = false;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Emision", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Vencimiento", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Sucursal", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Tipo de Movimiento", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Nro. Comprobante", TIPO_STRING, 50, false);
	static DatosColumnas col6 = new DatosColumnas("Moneda", TIPO_STRING, 30, false);
	static DatosColumnas col7 = new DatosColumnas("Importe Original", TIPO_DOUBLE, 30, true);
	static DatosColumnas col8 = new DatosColumnas("Saldo", TIPO_DOUBLE, 30, true);
	static DatosColumnas col9 = new DatosColumnas("Saldo Gs.", TIPO_DOUBLE, 30, true);

	public ReporteMovimientosCtaCte(List<MyArray> movimientos, EmpresaDTO empresa, Boolean isCliente) {

		this.isCliente = isCliente;
		this.movimientos = movimientos;
		this.empresa = empresa;
		List<Object[]> data = new ArrayList<Object[]>();
		for (MyArray m : movimientos) {
			Object[] obj1 = new Object[] { this.m.dateToString((Date) m.getPos3(), "dd-MM-yyyy"),
					this.m.dateToString((Date) m.getPos4(), "dd-MM-yyyy"), m.getPos11(), m.getPos8(), m.getPos2(),
					((MyPair) m.getPos7()).getText(), m.getPos5(), m.getPos6(), m.getPos12() };
			data.add(obj1);
		}
		this.setDatosReporte(data);
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
		cols.add(col8);
		cols.add(col9);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Estado de Cuentas");
		this.setDirectorio("");
		this.setBorrarDespuesDeVer(true);
		this.setNombreArchivo("Estado de Cuentas - " + this.empresa.getRazonSocial());
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String nombre = this.empresa.getNombre();
		String razonSocial = this.empresa.getRazonSocial();
		String ruc = this.empresa.getRuc();
		String estadoCtaCteCliente = this.empresa.getCtaCteEmpresa().getEstadoComoCliente().getText();
		String lineaCredito = (String) this.empresa.getCtaCteEmpresa().getLineaCredito().getPos1();
		String condicionPago = (String) this.empresa.getCtaCteEmpresa().getCondicionPagoCliente().getPos1();
		Date fechaAperturaCliente = this.empresa.getCtaCteEmpresa().getFechaAperturaCuentaCliente();

		String estadoCtaCteProveedor = this.empresa.getCtaCteEmpresa().getEstadoComoProveedor().getText();
		Date fechaAperturaProveedor = this.empresa.getCtaCteEmpresa().getFechaAperturaCuentaProveedor();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.textoParValor("Nombre", nombre)));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Razón Social", razonSocial)));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("RUC", ruc)));

		if (this.isCliente == true) {

			out.add(cmp.horizontalFlowList().add(this.textoParValor("Estado Cuenta", estadoCtaCteCliente)));

			out.add(cmp.horizontalFlowList().add(this.textoParValor("Linea de Crédito", lineaCredito)));

			out.add(cmp.horizontalFlowList().add(this.textoParValor("Condición Pago", condicionPago)));

			out.add(cmp.horizontalFlowList().add(this.textoParValor("Fecha Apertura", fechaAperturaCliente)));

		} else {

			out.add(cmp.horizontalFlowList().add(this.textoParValor("Estado Cuenta", estadoCtaCteProveedor)));
			out.add(cmp.horizontalFlowList().add(this.textoParValor("Fecha Apertura", fechaAperturaProveedor)));
			out.add(cmp.horizontalFlowList().add(this.texto("")));

		}
		return out;
	}
}