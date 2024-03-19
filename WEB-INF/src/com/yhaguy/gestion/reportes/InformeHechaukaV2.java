package com.yhaguy.gestion.reportes;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zul.Filedownload;

import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.ImportacionFactura;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaDebito;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

public class InformeHechaukaV2 {
	
	static final String PATH_FILE = "/yhaguy/archivos/tesaka/";
	static final NumberFormat FORMATTER = new DecimalFormat("##########0");

	/**
	 * Generar informe Hechauka..
	 */
	public static synchronized void generarInformeHechauka(List<Venta> ventas, List<NotaCredito> notasCredito, List<NotaDebito> notasDebito)
			throws Exception {
		Misc misc = new Misc();
		List<String> objects = new ArrayList<String>();

		int registros = 0;
		double montoTotal = 0;
		String periodo = "";
		
		for (Venta venta : ventas) {
			
			if (!venta.isAnulado()) {
				String rSocial = venta.getDenominacion() == null ? venta.getCliente().getRazonSocial() : venta.getDenominacion();
				String doc = venta.getCliente().getRuc();
				String ced = venta.getCliente().getEmpresa().getCi();
				String tipoDoc = "11";
				if (doc.isEmpty() || doc == null || doc.length() < 3 || doc.equals(Configuracion.RUC_EMPRESA_LOCAL)) {
					if (ced != null && ced.length() > 3) {
						doc = ced;
						tipoDoc = "12";
					} else {
						doc = "X";
						rSocial = "SIN NOMBRE";
						tipoDoc = "15";
					}
					System.out.println("--- SIN RUC: " + doc);
				} else if (doc.equals(Configuracion.RUC_DIPLOMATICOS)) {
					rSocial = "AGENTES DIPLOMATICOS";
					tipoDoc = "16";
				}

				String nro = venta.getNumero();
				String fecha = misc.dateToString(venta.getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
				periodo = Utiles.getDateToString(venta.getFecha(), "yyyyMM");
				double iva10 = redondear(venta.getTotalIva10());
				double gravada10 = redondear(venta.getTotalGravado10());
				double iva5 = redondear(venta.getTotalIva5());
				double gravada5 = redondear(venta.getTotalGravado5());
				double exenta = redondear(venta.getTotalExenta());
				double importe = iva10 + gravada10 + iva5 + gravada5 + exenta;
				long cond = venta.isVentaContado() ? 1 : 2;
				String col1 = "1";
				String col2 = tipoDoc;
				String col3 = doc.contains("-") ? doc.substring(0, doc.length() - 2) : doc;
				String col4 = rSocial;
				String col5 = "109";
				String col6 = fecha;
				String col7 = venta.getTimbrado();
				String col8 = nro;
				String col9 = FORMATTER.format(gravada10 + iva10) + "";
				String col10 = FORMATTER.format(gravada5 + iva5) + "";
				String col11 = FORMATTER.format(exenta) + "";
				String col12 = FORMATTER.format(importe) + "";
				String col13 = cond + "";
				String col14 = venta.isMonedaLocal() ? "N" : "S";
				String col15 = "S";
				String col16 = "N";
				String col17 = "N";
				String col18 = "";
				String col19 = "";
				String col20 = "";

				String object = col1 + " \t" + col2 + " \t" + col3 + " \t" + col4 + " \t" + col5 + " \t" + col6 + " \t"
						+ col7 + " \t" + col8 + " \t" + col9 + " \t" + col10 + " \t" + col11 + " \t" + col12 + " \t"
						+ col13 + " \t" + col14 + " \t" + col15 + " \t" + col16 + " \t" + col17 + " \t" + col18 + "" + "\t"
						+ col19 + "\t" + col20 + "\r\n";
				objects.add(object);
				registros++;
				montoTotal += importe;

			}			
		}
		
		for (NotaDebito nd : notasDebito) {
			
			if (!nd.isAnulado()) {
				String rSocial = nd.getCliente().getRazonSocial();
				String ruc = nd.getCliente().getRuc();
				String nro = nd.getNumero();
				String ncr = nd.getNotaCredito() != null ? nd.getNotaCredito().getNumero() : "";
				String timbNcr = nd.getNotaCredito() != null ? nd.getNotaCredito().getTimbrado_() : "";
				String fecha = misc.dateToString(nd.getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
				periodo = Utiles.getDateToString(nd.getFecha(), "yyyyMM");
				double iva10 = redondear(nd.getTotalIva10());
				double gravada10 = redondear(nd.getTotalGravado10());
				double iva5 = redondear(0.0);
				double gravada5 = redondear(0.0);
				double exenta = redondear(0.0);
				double importe = iva10 + gravada10 + iva5 + gravada5 + exenta;
				
				String col1 = "1";
				String col2 = "11";
				String col3 = ruc.substring(0, ruc.length() - 2);
				String col4 = rSocial;
				String col5 = "111";
				String col6 = fecha;
				String col7 = nd.getTimbrado();
				String col8 = nro;
				String col9 = FORMATTER.format(gravada10 + iva10) + "";
				String col10 = FORMATTER.format(gravada5 + iva5) + "";
				String col11 = FORMATTER.format(exenta) + "";
				String col12 = FORMATTER.format(importe) + "";	
				String col13 = "1";
				String col14 = "N";
				String col15 = "S";
				String col16 = "N";
				String col17 = "N";
				String col18 = "";
				String col19 = ncr;
				String col20 = timbNcr;
				
				String object = col1 
						+ " \t" + col2 + " \t" + col3 + " \t" + col4
						+ " \t" + col5 + " \t" + col6 + " \t" + col7
						+ " \t" + col8 + " \t" + col9 + " \t" + col10
						+ " \t" + col11 + " \t" + col12 + " \t" + col13
						+ " \t" + col14 + " \t" + col15 + " \t" + col16
						+ " \t" + col17
						+ " \t" + col18 + "\t" + col19 + "\t" + col20 + "\r\n";
				
				objects.add(object);
				registros++;
				montoTotal += importe;							
			}			
		}
		
		for (NotaCredito nc : notasCredito) {
			
			if (!nc.isAnulado()) {
				String aplicado = "";
				String aplicadoTimbrado = "";
				CompraLocalFactura compra = nc.getCompraAplicada();
				Gasto gasto = nc.getGastoAplicado();
				
				if (compra != null) {
					aplicado = compra.getNumero();
					aplicadoTimbrado = compra.getTimbrado().getNumero();
				}
				
				if (gasto != null) {
					aplicado = gasto.getNumeroFactura();
					aplicadoTimbrado = gasto.getTimbrado();
				}
				
				String rSocial = nc.getProveedor().getRazonSocial();
				String doc = nc.getProveedor().getRuc();
				String ced = nc.getProveedor().getEmpresa().getCi();
				String tipoDoc = "11";
				if (doc.isEmpty() || doc == null || doc.length() < 3 || doc.equals(Configuracion.RUC_EMPRESA_LOCAL)) {
					if (ced != null && ced.length() > 3) {
						doc = ced;
						tipoDoc = "12";
					} else {
						doc = Configuracion.RUC_EMPRESA_LOCAL;
						rSocial = "SIN NOMBRE";
						tipoDoc = "15";
					}
					System.out.println("--- SIN RUC: " + doc);
				} else if (doc.equals(Configuracion.RUC_DIPLOMATICOS)) {
					rSocial = "AGENTES DIPLOMATICOS";
					tipoDoc = "16";
				}				

				String nro = nc.getNumero();
				String fecha = misc.dateToString(nc.getFechaEmision(), Misc.DD_MM_YYYY).replace("-", "/");
				periodo = Utiles.getDateToString(nc.getFechaEmision(), "yyyyMM");
				double iva10 = redondear(nc.getTotalIva10());
				double gravada = redondear(nc.getTotalGravado10());
				double exenta = redondear(nc.getTotalExenta());
				double importe = iva10 + gravada + exenta;
				
				String col1 = "1";
				String col2 = tipoDoc;
				String col3 = doc.contains("-") ? doc.substring(0, doc.length() - 2) : doc;
				String col4 = rSocial;
				String col5 = "110";
				String col6 = fecha;
				String col7 = nc.getTimbrado() != null? nc.getTimbrado().getNumero() : "";
				String col8 = nro;
				String col9 = FORMATTER.format(gravada + iva10) + "";
				String col10 = FORMATTER.format(0.0) + "";
				String col11 = FORMATTER.format(exenta) + "";
				String col12 = FORMATTER.format(importe) + "";	
				String col13 = "1";
				String col14 = nc.isMonedaLocal() ? "N" : "S";
				String col15 = "S";
				String col16 = "N";
				String col17 = "N";
				String col18 = aplicado;
				String col19 = aplicadoTimbrado;
				String col20 = "";
				
				String object = col1 
						+ " \t" + col2 + " \t" + col3 + " \t" + col4
						+ " \t" + col5 + " \t" + col6 + " \t" + col7
						+ " \t" + col8 + " \t" + col9 + " \t" + col10
						+ " \t" + col11 + " \t" + col12 + " \t" + col13
						+ " \t" + col14 + " \t" + col15 + " \t" + col16
						+ " \t" + col17
						+ " \t" + col18 + "" + "\t" + col19 + "\t" + col20 + "\r\n";
				
				objects.add(object);
				registros++;
				montoTotal += importe;			
			}			
		}
		
		String cabecera = Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA) ? 
				getCabeceraBaterias(registros, montoTotal, periodo) : getCabecera(registros, montoTotal, periodo);

		saveArchivo(
				objects,
				cabecera,
				"InformeRG90_Ventas-"
						+ misc.dateToString(new Date(), Misc.DD_MM_YYYY));
	}
	
	/**
	 * Generar informe Hechauka..
	 */
	public static synchronized void generarInformeHechaukaCompras(List<NotaCredito> ncs,
			List<CompraLocalFactura> compras, List<Gasto> gastos, List<ImportacionFactura> importaciones, 
			boolean incluirBaseImponible, boolean incluirGastos) throws Exception {
		Misc misc = new Misc();
		List<String> objects = new ArrayList<String>();

		int registros = 0;
		double montoTotal = 0;
		String periodo = "";
		
		for (CompraLocalFactura compra : compras) {	
			String ruc = compra.getProveedor().getRuc();			
			if (ruc.isEmpty()) {
				ruc = Configuracion.RUC_EMPRESA_LOCAL;
			}

			String rSocial = compra.getProveedor().getRazonSocial();
			String nro = compra.getNumero();
			String fecha = misc.dateToString(compra.getFechaOriginal(), Misc.DD_MM_YYYY).replace("-", "/");
			double iva10 = redondear(compra.getIva10());
			double gravada10 = redondear(compra.getGravada10());
			double exenta = redondear(compra.getExenta());
			double gravada5 = redondear(compra.getGravada5());
			double iva5 = redondear(compra.getIva5());
			double importe = redondear(gravada10 + iva10 + gravada5 + iva5 + exenta);
			long cond = compra.isContado() ? 1 : 2;
			
			String col1 = "2";
			String col2 = "11";
			String col3 = ruc.substring(0, ruc.length() - 2);
			String col4 = rSocial;
			String col5 = "109";
			String col6 = fecha;
			String col7 = compra.getTimbrado() != null? compra.getTimbrado().getNumero() : "0";
			String col8 = nro;
			String col9 = FORMATTER.format(gravada10 + iva10) + "";
			String col10 = FORMATTER.format(gravada5 + iva5) + "";
			String col11 = FORMATTER.format(exenta) + "";
			String col12 = FORMATTER.format(importe) + "";
			String col13 = cond + "";
			String col14 = compra.isMonedaLocal() ? "N" : "S";
			String col15 = "S";
			String col16 = "N";
			String col17 = "N";
			String col18 = "N";
			String col19 = "";
			String col20 = "";

			String object = col1 + " \t" + col2 + " \t" + col3 + " \t" + col4 + " \t" + col5 + " \t" + col6 + " \t"
					+ col7 + " \t" + col8 + " \t" + col9 + " \t" + col10 + " \t" + col11 + " \t" + col12 + " \t"
					+ col13 + " \t" + col14 + " \t" + col15 + " \t" + col16 + " \t" + col17 + " \t" + col18 + "" + "\t" + col19 + "" + "\t"
					+ col20 + "" + "\r\n";
			objects.add(object);
			registros++;
			montoTotal += importe;
		}
		
		if (incluirGastos) {
			for (Gasto gasto : gastos) {
				String ruc = gasto.getProveedor().getRuc();		
				String rSocial = gasto.getProveedor().getRazonSocial();
				
				if (!ruc.equals(Proveedor.RUC_DIR_NAC_ADUANAS) && !ruc.equals(Proveedor.RUC_MOPC) && !rSocial.equals(Proveedor.RS_PASAJES)) {				
					if (ruc.isEmpty()) {
						ruc = Configuracion.RUC_EMPRESA_LOCAL;
					}
					
					String timbrado = gasto.getTimbrado();
					String nro = gasto.getNumeroFactura();
					String fecha = misc.dateToString(gasto.getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
					double iva10 = redondear(gasto.getIva10());
					double gravada10 = redondear(gasto.getGravada10());
					double exenta = redondear(gasto.getExenta());
					double gravada5 = redondear(gasto.getGravada5());
					double iva5 = redondear(gasto.getIva5());
					double importe = redondear(gravada10 + iva10 + gravada5 + iva5 + exenta);
					long cond = gasto.isContado() ? 1 : 2;
					
					String col1 = "2";
					String col2 = "11";
					String col3 = ruc.substring(0, ruc.length() - 2);
					String col4 = rSocial;
					String col5 = "109";
					String col6 = fecha;
					String col7 = timbrado;
					String col8 = nro;
					String col9 = FORMATTER.format(gravada10 + iva10) + "";
					String col10 = FORMATTER.format(gravada5 + iva5) + "";
					String col11 = FORMATTER.format(exenta) + "";
					String col12 = FORMATTER.format(importe) + "";
					String col13 = cond + "";
					String col14 = gasto.isMonedaLocal() ? "N" : "S";
					String col15 = "S";
					String col16 = "N";
					String col17 = "N";
					String col18 = "N";
					String col19 = "";
					String col20 = "";

					String object = col1 + " \t" + col2 + " \t" + col3 + " \t" + col4 + " \t" + col5 + " \t" + col6 + " \t"
							+ col7 + " \t" + col8 + " \t" + col9 + " \t" + col10 + " \t" + col11 + " \t" + col12 + " \t"
							+ col13 + " \t" + col14 + " \t" + col15 + " \t" + col16 + " \t" + col17 + " \t" + col18 + "" + "\t" + col19 + "" + "\t"
							+ col20 + "" + "\r\n";
					
					objects.add(object);
					registros++;
					montoTotal += importe;
				}
			}
		}		
		
		if (incluirBaseImponible) {
			for (Gasto gasto : gastos) {			
				String ruc = gasto.getProveedor().getRuc();		
				
				if (ruc.equals(Proveedor.RUC_DIR_NAC_ADUANAS)) {				
					ruc = Configuracion.RUC_EMPRESA_EXTERIOR;

					String rSocial = "DIRECCIÓN GENERAL DE ADUANAS";
					String timbrado = "0";
					String nro = gasto.getNumeroFactura();
					String fecha = misc.dateToString(gasto.getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
					double iva10 = redondear(gasto.getIva10());
					double gravada10 = redondear(gasto.getBaseImponible());
					double exenta = redondear(gasto.getExenta());
					double gravada5 = redondear(gasto.getGravada5());
					double iva5 = redondear(gasto.getIva5());
					double importe = redondear(gravada10 + iva10 + gravada5 + iva5 + exenta);
					long cond = 1;
					
					String col1 = "2";
					String col2 = "17";
					String col3 = ruc.substring(0, ruc.length() - 2);
					String col4 = rSocial;
					String col5 = "107";
					String col6 = fecha;
					String col7 = timbrado;
					String col8 = nro;
					String col9 = FORMATTER.format(gravada10 + iva10) + "";
					String col10 = FORMATTER.format(gravada5 + iva5) + "";
					String col11 = FORMATTER.format(exenta) + "";
					String col12 = FORMATTER.format(importe) + "";
					String col13 = cond + "";
					String col14 = gasto.isMonedaLocal() ? "N" : "S";
					String col15 = "S";
					String col16 = "N";
					String col17 = "N";
					String col18 = "N";
					String col19 = "";
					String col20 = "";

					String object = col1 + " \t" + col2 + " \t" + col3 + " \t" + col4 + " \t" + col5 + " \t" + col6 + " \t"
							+ col7 + " \t" + col8 + " \t" + col9 + " \t" + col10 + " \t" + col11 + " \t" + col12 + " \t"
							+ col13 + " \t" + col14 + " \t" + col15 + " \t" + col16 + " \t" + col17 + " \t" + col18 + "" + "\t" + col19 + "" + "\t"
							+ col20 + "" + "\r\n";
					
					objects.add(object);
					registros++;
					montoTotal += importe;
				}
			}
		}
		
		for (ImportacionFactura compra : importaciones) {			
			String ruc = Configuracion.RUC_EMPRESA_EXTERIOR;			
			if (ruc.isEmpty()) {
				ruc = Configuracion.RUC_EMPRESA_EXTERIOR;
			}

			String rSocial = compra.getProveedor().getRazonSocial();
			String timbrado = "0";
			String nro = compra.getNumero();
			String fecha = misc.dateToString(compra.getFechaOriginal(), Misc.DD_MM_YYYY).replace("-", "/");
			double importe = redondear(compra.getTotalImporteDs() * compra.getPorcProrrateo());
			long cond = 1;
			
			String col1 = "2";
			String col2 = "17";
			String col3 = ruc.substring(0, ruc.length() - 2);
			String col4 = rSocial;
			String col5 = "107";
			String col6 = fecha;
			String col7 = timbrado;
			String col8 = nro;
			String col9 = FORMATTER.format(0.0) + "";
			String col10 = FORMATTER.format(0.0) + "";
			String col11 = FORMATTER.format(importe) + "";
			String col12 = FORMATTER.format(importe) + "";
			String col13 = cond + "";
			String col14 = "S";
			String col15 = "S";
			String col16 = "N";
			String col17 = "N";
			String col18 = "N";
			String col19 = "";
			String col20 = "";

			String object = col1 + " \t" + col2 + " \t" + col3 + " \t" + col4 + " \t" + col5 + " \t" + col6 + " \t"
					+ col7 + " \t" + col8 + " \t" + col9 + " \t" + col10 + " \t" + col11 + " \t" + col12 + " \t"
					+ col13 + " \t" + col14 + " \t" + col15 + " \t" + col16 + " \t" + col17 + " \t" + col18 + "" + "\t" + col19 + "" + "\t"
					+ col20 + "" + "\r\n";
			
			objects.add(object);
			registros++;
			montoTotal += importe;
		}

		for (NotaCredito nc : ncs) {			
			if (!nc.isAnulado()) {				
				String aplicado = "";
				String aplicadoTimbrado = "";
				Venta vta = nc.getVentaAplicada();
				
				if (vta != null) {
					aplicado = vta.getNumero();
					aplicadoTimbrado = vta.getTimbrado();
				}
				
				String rSocial = nc.getCliente().getRazonSocial();
				String doc = nc.getCliente().getRuc();
				String ced = nc.getCliente().getEmpresa().getCi();
				String tipoDoc = "11";
				if (doc.isEmpty() || doc == null || doc.length() < 3 || doc.equals(Configuracion.RUC_EMPRESA_LOCAL)) {
					if (ced != null && ced.length() > 3) {
						doc = ced;
						tipoDoc = "12";
					} else {
						doc = Configuracion.RUC_EMPRESA_LOCAL;
						rSocial = "SIN NOMBRE";
						tipoDoc = "15";
					}
				} else if (doc.equals(Configuracion.RUC_DIPLOMATICOS)) {
					rSocial = "AGENTES DIPLOMATICOS";
					tipoDoc = "16";
				}

				String timbrado = nc.getTimbrado_();
				String nro = nc.getNumero();
				String fecha = misc.dateToString(nc.getFechaEmision(), Misc.DD_MM_YYYY).replace("-", "/");
				double importe = redondear(nc.getImporteGs());
				double iva10 = redondear(nc.getTotalIva10());
				double gravada = redondear(nc.getTotalGravado10());
				double exenta = redondear(nc.getTotalExenta()); 
				long cond = 1;
				
				String col1 = "2";
				String col2 = tipoDoc;
				String col3 = doc.contains("-") ? doc.substring(0, doc.length() - 2) : doc;
				String col4 = rSocial;
				String col5 = "110";
				String col6 = fecha;
				String col7 = timbrado;
				String col8 = nro;
				String col9 = FORMATTER.format(gravada + iva10) + "";
				String col10 = FORMATTER.format(0.0) + "";
				String col11 = FORMATTER.format(exenta) + "";
				String col12 = FORMATTER.format(importe) + "";
				String col13 = cond + "";
				String col14 = nc.isMonedaLocal() ? "N" : "S";
				String col15 = "S";
				String col16 = "N";
				String col17 = "N";
				String col18 = "N";
				String col19 = aplicado;
				String col20 = aplicadoTimbrado;

				String object = col1 + " \t" + col2 + " \t" + col3 + " \t" + col4 + " \t" + col5 + " \t" + col6 + " \t"
						+ col7 + " \t" + col8 + " \t" + col9 + " \t" + col10 + " \t" + col11 + " \t" + col12 + " \t"
						+ col13 + " \t" + col14 + " \t" + col15 + " \t" + col16 + " \t" + col17 + " \t" + col18 + "" + "\t" + col19 + "" + "\t"
						+ col20 + "" + "\r\n";
				
				objects.add(object);
				registros++;
				montoTotal += importe;
			}	
		}
		
		if (gastos.size() > 0) {
			periodo = Utiles.getDateToString(gastos.get(0).getFecha(), "yyyyMM");		
		} else if(compras.size() > 0) {
			periodo = Utiles.getDateToString(compras.get(0).getFechaOriginal(), "yyyyMM");
		}
		
		saveArchivo(
				objects,
				getCabeceraNotaCred(registros, montoTotal, periodo),
				"InformeRG90_Compras-"
						+ misc.dateToString(new Date(), Misc.DD_MM_YYYY));
	}
	
	/**
	 * graba el archivo para el hechauka..
	 */
	private static synchronized void saveArchivo(List<String> objects, String cabecera, String name) 
			throws Exception {
		FileWriter file = new FileWriter(Configuracion.pathRetencionTesaka + name +".txt");
		file.write(cabecera);
		for (String string : objects) {
			file.write(string);
		}	
		file.flush();
		file.close();
		Filedownload.save(InformeHechaukaV2.PATH_FILE + name +".txt", null);
	}
	
	/**
	 * @return el monto redondeado..
	 */
	private static double redondear(double monto) {
		return Math.rint(monto * 1) / 1;
	}
	
	/**
	 * @return los datos de cabecera..
	 */
	private static String getCabecera(int registros, double montoTotal, String periodo) {
		String out = "";
		
		String col1 = "1";
		String col3 = "1";
		String col4 = "921";
		String col5 = "221";
		String ruc = "80024884";
		String dv = "8";
		String rSocial = "YHAGUY REPUESTOS S.A.";
		String rucRep = "344003";
		String dvRep = "6";
		String rSocialRep = "FEDERICO ELADIO FRUTOS AGUILERA";
		int cantReg = registros;
		double monto = montoTotal;
		
		out = col1 + "\t" + periodo + "\t" + col3 + "\t" + col4 + "\t" + col5
				+ "\t" + ruc + "\t" + dv + "\t" + rSocial + "\t" + rucRep
				+ "\t" + dvRep + "\t" + rSocialRep + "\t" + cantReg + "" + "\t"
				+ FORMATTER.format(monto) + "" + "\t\t" + "2" + "\r\n";
		
		return out;
	}
	
	/**
	 * @return los datos de cabecera..
	 */
	private static String getCabeceraBaterias(int registros, double montoTotal, String periodo) {	
		String out = "";
		
		String col1 = "1";
		String col3 = "1";
		String col4 = "921";
		String col5 = "221";
		String ruc = "80093865";
		String dv = "5";
		String rSocial = "YHAGUY BATERIAS S.A.";
		String rucRep = "344003";
		String dvRep = "6";
		String rSocialRep = "FEDERICO ELADIO FRUTOS AGUILERA";
		int cantReg = registros;
		double monto = montoTotal;
		
		out = col1 + "\t" + periodo + "\t" + col3 + "\t" + col4 + "\t" + col5
				+ "\t" + ruc + "\t" + dv + "\t" + rSocial + "\t" + rucRep
				+ "\t" + dvRep + "\t" + rSocialRep + "\t" + cantReg + "" + "\t"
				+ FORMATTER.format(monto) + "" + "\t\t" + "2" + "\r\n";
		
		return out;
	}
	
	/**
	 * @return los datos de cabecera de nota cred..
	 */
	private static String getCabeceraNotaCred(int registros, double montoTotal, String periodo) {
		String out = "";
		
		String col1 = "1";
		String col3 = "1";
		String col4 = "911";
		String col5 = "211";
		String ruc = "80024884";
		String dv = "8";
		String rSocial = "YHAGUY REPUESTOS S.A.";
		String rucRep = "344003";
		String dvRep = "6";
		String rSocialRep = "FEDERICO ELADIO FRUTOS AGUILERA";
		int cantReg = registros;
		double monto = montoTotal;
		
		out = col1 + "\t" + periodo + "\t" + col3 + "\t" + col4 + "\t" + col5
				+ "\t" + ruc + "\t" + dv + "\t" + rSocial + "\t" + rucRep
				+ "\t" + dvRep + "\t" + rSocialRep + "\t" + cantReg + "" + "\t"
				+ FORMATTER.format(monto) + "" + "\t"+ "NO" +"\t" + "2" + "\r\n";
		
		return out;
	}
}
