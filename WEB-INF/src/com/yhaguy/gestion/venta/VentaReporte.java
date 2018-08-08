package com.yhaguy.gestion.venta;

import java.util.ArrayList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class VentaReporte extends ReporteYhaguy {

	List<Object[]> formasDePagos = new ArrayList<Object[]>();
	VentaDTO pedido = new VentaDTO();

	@Override
	public void informacionReporte() {
		this.setTitulo(this.isVentaContado() ? TITULO_VENTA_CONTADO
				: TITULO_VENTA_CREDITO);
		this.setDirectorio(this.isVentaContado() ? URL_ARCHIVO_VENTA_CONTADO
				: URL_ARCHIVO_VENTA_CREDITO);
		this.setNombreArchivo(NOMBRE_ARCHIVO);
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo(this.numero, this.cliente));
	}

	private static String URL_ARCHIVO_VENTA_CONTADO = "ventas/contado";
	private static String URL_ARCHIVO_VENTA_CREDITO = "ventas/credito";
	private static String TITULO_VENTA_CONTADO = "Factura Venta Contado";
	private static String TITULO_VENTA_CREDITO = "Factura Venta Crédito";
	private static String NOMBRE_ARCHIVO = "Venta";

	private static DatosColumnas col1 = new DatosColumnas("Ítem", TIPO_STRING);
	private static DatosColumnas col2 = new DatosColumnas("Cantidad",
			TIPO_LONG, 70, false);
	private static DatosColumnas col3 = new DatosColumnas("Importe",
			TIPO_DOUBLE, 70, true);

	private static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	private String numero;
	private String cliente;
	private boolean ventaContado;

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo(String nroFactura, String cliente) {

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", nroFactura)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Cliente", cliente)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		// agragamos las tablas
		out.add(cmp.verticalGap(10));
		out.add(this.getFormasDePago());

		return out;
	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getFormasDePago() {
		ComponentBuilder out = null;

		String[][] cols = { { "tipo", WIDTH + "50" },
				{ "Monto", DERECHA + WIDTH + "70" }, };

		for (ReciboFormaPagoDTO formaPago : pedido.getFormasPago()) {

			Object[] obj = new Object[2];
			obj[0] = formaPago.getTipo().getText();
			obj[1] = formaPago.getMontoGs();
			formasDePagos.add(obj);
		}

		String prop = TABLA_TITULO + "Titulo de la Tabla";
		out = this.getTabla(cols, this.getFormasDePagos(), prop);
		return out;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public boolean isVentaContado() {
		return ventaContado;
	}

	public void setVentaContado(boolean ventaContado) {
		this.ventaContado = ventaContado;
	}

	public List<Object[]> getFormasDePagos() {
		return formasDePagos;
	}

	public void setFormasDePagos(List<Object[]> formasDePagos) {
		this.formasDePagos = formasDePagos;
	}

	public VentaDTO getPedido() {
		return pedido;
	}

	public void setPedido(VentaDTO pedido) {
		this.pedido = pedido;
	}

}
