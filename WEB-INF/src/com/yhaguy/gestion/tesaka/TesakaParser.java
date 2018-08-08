package com.yhaguy.gestion.tesaka;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Filedownload;

import com.coreweb.extras.csv.CSV;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.GastoDetalle;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Retencion;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;

public class TesakaParser {
	
	public static final String PATH_FILE = "/yhaguy/archivos/tesaka/";
	
	/**
	 * genera el archivo para las retenciones..
	 */
	public static void generarArchivoRetenciones (
			List<CtaCteEmpresaMovimientoDTO> movimientos, String nroPago)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		List<String> jsonCompras = new ArrayList<String>();

		// levanta las compras y gastos..
		for (CtaCteEmpresaMovimientoDTO item : movimientos) {
			String sigla = (String) item.getTipoMovimiento().getPos2();
			
			if (sigla.equals(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO)
					|| sigla.equals(Configuracion.SIGLA_TM_FAC_COMPRA_CONTADO)) {
				CompraLocalFactura compra = rr.getFacturaCompraById(item
						.getIdMovimientoOriginal());
				jsonCompras.add(compraToJsonString(compra));
			}
			
			if (sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO)
					|| sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO)) {
				Gasto gasto = rr.getGastoById(item
						.getIdMovimientoOriginal());
				jsonCompras.add(gastoToJsonString(gasto));
			}
		}
		saveArchivoRetencion(jsonCompras, nroPago);
	}
	
	/**
	 * marca al pago como tesaka generado..
	 */
	public static String setTesakaPago(long idPago, String user) throws Exception {
		Misc misc = new Misc();
		String fecha = misc.dateToString(new Date(), Misc.DD_MM__YYY_HORA_MIN);
		String tesaka = "Generado por " + user + " - " + fecha;
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo pago = rr.getOrdenPagoById(idPago);
		pago.setTesaka(tesaka);
		rr.saveObject(pago, user);
		return tesaka;
	}
	
	/**
	 * marca al movimiento como tesaka generado..
	 */
	public static void setTesakaMovimiento(
			List<CtaCteEmpresaMovimientoDTO> movimientos, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (CtaCteEmpresaMovimientoDTO item : movimientos) {
			CtaCteEmpresaMovimiento movim = rr
					.getCtaCteEmpresaMovimientoById(item.getId());
			movim.setTesaka(true);
			rr.saveObject(movim, user);
		}
	}
	
	/**
	 * @return la compra en formato JSON object..
	 */
	@SuppressWarnings("unchecked")
	private static String compraToJsonString(CompraLocalFactura compra) {
		Misc misc = new Misc();
		
		// detalle..
		JSONArray detalle = new JSONArray();		
		for (CompraLocalFacturaDetalle item : compra.getDetalles()) {
			int signo = item.isDescuento() ? -1 : 1;
			if (item.getArticulo().getDescripcion().equals("@DESCUENTO")) {
				signo = -1;
			}
			JSONObject det = new JSONObject();
			det.put("cantidad", item.getCantidad());
			det.put("tasaAplica", "10");
			det.put("precioUnitario", item.getCostoGs() * signo);
			det.put("descripcion", item.getArticulo().getDescripcion());
			detalle.add(det);
		}

		// retencion..
		JSONObject retencion = new JSONObject();
		retencion.put("fecha", misc.dateToString(new Date(), Misc.YYYY_MM_DD));
		retencion.put("moneda", "PYG");
		retencion.put("retencionRenta", false);
		retencion.put("conceptoRenta", "");
		retencion.put("ivaPorcentaje5", 30);
		retencion.put("ivaPorcentaje10", 30);
		retencion.put("rentaCabezasBase", 0);
		retencion.put("rentaCabezasCantidad", 0);
		retencion.put("rentaToneladasBase", 0);
		retencion.put("rentaToneladasCantidad", 0);
		retencion.put("rentaPorcentaje", 0);
		retencion.put("retencionIva", true);
		retencion.put("conceptoIva", "IVA.1");

		// informado..
		JSONObject informado = new JSONObject();
		int lengthRuc = compra.getProveedor().getRuc().length();
		informado.put("situacion", "contribuyente");
		informado.put("nombre", compra.getProveedor().getRazonSocial());
		informado.put("ruc", compra.getProveedor().getRuc().substring(0, lengthRuc - 2));
		informado.put("dv", compra.getProveedor().getRuc().substring(lengthRuc - 1, lengthRuc));
		informado.put("domicilio", compra.getProveedor().getDireccion());
		informado.put("tipoIdentificacion", "");
		informado.put("identificacion", "");
		informado.put("direccion", "");
		informado.put("correoElectronico", "");
		informado.put("pais", "");
		informado.put("telefono", "");

		// transaccion..
		JSONObject transaccion = new JSONObject();
		transaccion.put("numeroComprobanteVenta", compra.getNumero());
		transaccion.put("condicionCompra", compra.isContado() ? "CONTADO" : "CREDITO");
		transaccion.put("tipoComprobante", 1);
		transaccion.put("fecha", misc.dateToString(compra.getFechaOriginal(), Misc.YYYY_MM_DD));
		transaccion.put("numeroTimbrado", compra.getTimbrado().getNumero());
		if (!compra.isContado()) {
			transaccion.put("cuotas", 1);
		}

		// atributos..
		JSONObject atributos = new JSONObject();
		atributos.put("fechaCreacion", misc.dateToString(new Date(), Misc.YYYY_MM_DD));
		atributos.put("fechaHoraCreacion", misc.dateToString(new Date(), Misc.YYYY_MM_DD_HORA_MIN_SEG));

		JSONObject out = new JSONObject();
		out.put("detalle", detalle);
		out.put("retencion", retencion);
		out.put("informado", informado);
		out.put("transaccion", transaccion);
		out.put("atributos", atributos);
		
		return out.toJSONString();
	}
	
	/**
	 * @return el gasto en formato JSON object..
	 */
	@SuppressWarnings("unchecked")
	private static String gastoToJsonString(Gasto gasto) {
		Misc misc = new Misc();
		
		// detalle..
		JSONArray detalle = new JSONArray();		
		for (GastoDetalle item : gasto.getDetalles()) {
			JSONObject det = new JSONObject();
			det.put("cantidad", item.getCantidad());
			det.put("tasaAplica", "10");
			det.put("precioUnitario", item.getMontoGs());
			det.put("descripcion", item.getArticuloGasto().getDescripcion());
			detalle.add(det);
		}

		// retencion..
		JSONObject retencion = new JSONObject();
		retencion.put("fecha", misc.dateToString(new Date(), Misc.YYYY_MM_DD));
		retencion.put("moneda", "PYG");
		retencion.put("retencionRenta", false);
		retencion.put("conceptoRenta", "");
		retencion.put("ivaPorcentaje5", 30);
		retencion.put("ivaPorcentaje10", 30);
		retencion.put("rentaCabezasBase", 0);
		retencion.put("rentaCabezasCantidad", 0);
		retencion.put("rentaToneladasBase", 0);
		retencion.put("rentaToneladasCantidad", 0);
		retencion.put("rentaPorcentaje", 0);
		retencion.put("retencionIva", true);
		retencion.put("conceptoIva", "IVA.1");

		// informado..
		JSONObject informado = new JSONObject();
		int lengthRuc = gasto.getProveedor().getRuc().length();
		informado.put("situacion", "contribuyente");
		informado.put("nombre", gasto.getProveedor().getRazonSocial());
		informado.put("ruc", gasto.getProveedor().getRuc().substring(0, lengthRuc - 2));
		informado.put("dv", gasto.getProveedor().getRuc().substring(lengthRuc - 1, lengthRuc));
		informado.put("domicilio", gasto.getProveedor().getDireccion());
		informado.put("tipoIdentificacion", "");
		informado.put("identificacion", "");
		informado.put("direccion", "");
		informado.put("correoElectronico", "");
		informado.put("pais", "");
		informado.put("telefono", "");

		// transaccion..
		JSONObject transaccion = new JSONObject();
		transaccion.put("numeroComprobanteVenta", gasto.getNumeroFactura());
		transaccion.put("condicionCompra", gasto.isContado() ? "CONTADO" : "CREDITO");
		transaccion.put("tipoComprobante", 1);
		transaccion.put("fecha", misc.dateToString(gasto.getFecha(), Misc.YYYY_MM_DD));
		transaccion.put("numeroTimbrado", gasto.getTimbrado().getNumero());
		if (!gasto.isContado()) {
			transaccion.put("cuotas", 1);
		}

		// atributos..
		JSONObject atributos = new JSONObject();
		atributos.put("fechaCreacion", misc.dateToString(new Date(), Misc.YYYY_MM_DD));
		atributos.put("fechaHoraCreacion", misc.dateToString(new Date(), Misc.YYYY_MM_DD_HORA_MIN_SEG));

		JSONObject out = new JSONObject();
		out.put("detalle", detalle);
		out.put("retencion", retencion);
		out.put("informado", informado);
		out.put("transaccion", transaccion);
		out.put("atributos", atributos);
		
		return out.toJSONString();
	}
	
	/**
	 * graba el archivo json de retencion..
	 */
	private static void saveArchivoRetencion(List<String> objects, String name) throws Exception {
		String tesaka = "";
		for (int i = 0; i < objects.size(); i++) {
			tesaka += objects.get(i);
			if(i < (objects.size() - 1))
				tesaka += ",";
		}
		
		FileWriter file = new FileWriter(Configuracion.pathRetencionTesaka + name +".json");
		file.write("[");
		file.write(tesaka);
		file.write("]");
		file.flush();
		file.close();
		
		Filedownload.save(PATH_FILE + name +".json", null);
	}
	
	/**
	 * Upload del archivo de tesaka para importar las retenciones..
	 */
	public static void uploadArchivoRetencion(Media file, int periodo, String user, String tipo) throws Exception {
		Misc misc = new Misc();
		String name = "RET-" + misc.dateToString(new Date(), Misc.DD_MM__YYY_HORA_MIN);
		String path = Configuracion.pathRetencionTesaka;		
		misc.uploadFile(path, name, ".csv", (InputStream) new ByteArrayInputStream(file.getByteData()));
		
		addCabeceraArchivoRetencion(name, periodo, user, tipo);
	}
	
	/**
	 * Agrega la cabecera al archivo csv de retencion..
	 */
	private static void addCabeceraArchivoRetencion(String name, int periodo, String user, String tipo) throws Exception {
		String path = Configuracion.pathRetencionTesaka + name +".csv";
		String columns = tipo.equals(Retencion.RECIBIDAS) ? ",,,,,,,,,,,,,,,,," : ",,,,,,,,,";
		
		File file = new File(path);
		List<String> lines = FileUtils.readLines(file);
		lines.set(0, "Empresa, Yhaguy Repuestos");
		lines.add(1, columns);
		FileUtils.writeLines(file, lines);	
		
		readCsvRetenciones(path, periodo, user, tipo);
	}
	
	/**
	 * lee el archivo csv de retenciones..y graba en la bd..
	 */
	private static void readCsvRetenciones(String path, int periodo, String user, String tipo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "estado", CSV.STRING }, { "numero", CSV.STRING },
				{ "situacion", CSV.STRING },
				{ "tipo identificacion", CSV.STRING },
				{ "identificacion", CSV.STRING }, { "nombre", CSV.STRING },
				{ "fecha", CSV.STRING }, { "factura", CSV.STRING },
				{ "total", CSV.STRING } };

		if (tipo.equals(Retencion.RECIBIDAS)) {
			det = new String[][] { { "Tipo", CSV.STRING },
					{ "Estado", CSV.STRING }, { "Comprobante", CSV.STRING },
					{ "RUC Informante", CSV.STRING },
					{ "Informante", CSV.STRING },
					{ "Informado Tipo Identificacion", CSV.STRING },
					{ "RUC Informado", CSV.STRING },
					{ "Identificacion", CSV.STRING },
					{ "Informado", CSV.STRING }, { "Control", CSV.STRING },
					{ "Fecha Emision", CSV.STRING },
					{ "Comprobante Venta", CSV.STRING },
					{ "Timbrado", CSV.STRING }, { "Total Renta", CSV.STRING },
					{ "Total Iva", CSV.STRING },
					{ "Total Cabezas", CSV.STRING },
					{ "Total Toneladas", CSV.STRING } };
		}

		CSV csv = new CSV(cab, det, path);
		csv.start();

		while (csv.hashNext()) {
			String column1 = tipo.equals(Retencion.RECIBIDAS) ? "Estado" : "estado";
			String column2 = tipo.equals(Retencion.RECIBIDAS) ? "Comprobante" : "numero";
			String column3 = tipo.equals(Retencion.RECIBIDAS) ? "RUC Informante" : "identificacion";
			String column4 = tipo.equals(Retencion.RECIBIDAS) ? "Informante" : "nombre";
			String column5 = tipo.equals(Retencion.RECIBIDAS) ? "Fecha Emision" : "fecha";
			String column6 = tipo.equals(Retencion.RECIBIDAS) ? "Comprobante Venta" : "factura";
			String column7 = tipo.equals(Retencion.RECIBIDAS) ? "Total Iva" : "total";
			
			String estado = csv.getDetalleString(column1);
			String numero = csv.getDetalleString(column2);
			String ruc = csv.getDetalleString(column3);
			String razonSocial = csv.getDetalleString(column4);
			String fecha = csv.getDetalleString(column5);
			String factura = csv.getDetalleString(column6);
			String importeGs = csv.getDetalleString(column7);
			
			Retencion ret = new Retencion();
			ret.setEstado(estado);
			ret.setNumero(numero);
			ret.setRuc(ruc);
			ret.setRazonSocial(razonSocial);
			ret.setFecha(fecha);
			ret.setNumeroFactura(factura);
			ret.setImporteGs(Double.parseDouble(importeGs));
			ret.setPeriodo(periodo);
			ret.setTipoRetencion(tipo);
			
			if (tipo.equals(Retencion.EMITIDAS)) {
				Recibo pago = rr.getOrdenPago(ret.getNumeroFactura());				
				ret.setNumeroOrdenPago(pago == null ? "---" : pago.getNumero());
			} else {
				ret.setNumeroOrdenPago("---");
			}
			rr.saveObject(ret, user);
		}
	}
}
