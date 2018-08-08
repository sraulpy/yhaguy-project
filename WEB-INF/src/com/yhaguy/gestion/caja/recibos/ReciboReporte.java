package com.yhaguy.gestion.caja.recibos;

import java.util.ArrayList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class ReciboReporte extends ReporteYhaguy {

	private static String URL_ARCHIVO_COBRO = "recibos/cobros";
	private static String URL_ARCHIVO_PAGO = "recibos/pagos";
	private static String TITULO_COBRO = "Recibo de Cobro";
	private static String TITULO_PAGO = "Órden de Pago";
	private static String NOMBRE_ARCHIVO = "Recibo";

	private boolean cobro = false;

	private String nroRecibo;
	private String beneficiario;
	private String nroCaja;
	private String moneda;
	private String sucursal;

	// Columnas del Reporte

	static DatosColumnas col1 = new DatosColumnas("N°", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Descripcion", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Comprobantes", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Fecha", TIPO_DATE);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE, 70,true);	

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();

	static {
		col1.setAncho(15);
		col4.setAncho(35);
		col5.setAncho(55);
		col1.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		col4.setAlineacionColuman(COLUMNA_ALINEADA_CENTRADA);
		
		
		
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);		
		cols.add(col5);
		
	};

	@Override
	public void informacionReporte() {
		this.setTitulo(this.isCobro() ? TITULO_COBRO : TITULO_PAGO);
		this.setDirectorio(this.isCobro() ? URL_ARCHIVO_COBRO
				: URL_ARCHIVO_PAGO);
		this.setNombreArchivo(NOMBRE_ARCHIVO);
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo(nroRecibo, beneficiario, nroCaja, moneda,
				sucursal));
		// this.setFooter(footer);
	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo(String nroRecibo, String beneficiario,
			String nroCaja, String moneda, String sucursal) {

		VerticalListBuilder out = null;
		out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", nroRecibo)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Caja", nroCaja)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Moneda", moneda)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Sucursal", sucursal)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor(this.getCampoBeneficiario(), beneficiario)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}

	private String getCampoBeneficiario() {
		return this.isCobro() ? "Cliente" : "Proveedor";
	}

	public String getNroRecibo() {
		return nroRecibo;
	}

	public void setNroRecibo(String nroOrdenPago) {
		this.nroRecibo = nroOrdenPago;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public boolean isCobro() {
		return cobro;
	}

	public void setCobro(boolean cobro) {
		this.cobro = cobro;
	}

	public String getNroCaja() {
		return nroCaja;
	}

	public void setNroCaja(String nroCaja) {
		this.nroCaja = nroCaja;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
}
