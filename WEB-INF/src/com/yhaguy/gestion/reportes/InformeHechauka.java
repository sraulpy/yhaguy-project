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
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

public class InformeHechauka {
	
	static final String PATH_FILE = "/yhaguy/archivos/tesaka/";
	static final NumberFormat FORMATTER = new DecimalFormat("##########0");

	/**
	 * Generar informe Hechauka..
	 */
	public static synchronized void generarInformeHechauka(List<Venta> ventas, List<NotaCredito> notasCredito)
			throws Exception {
		Misc misc = new Misc();
		List<String> objects = new ArrayList<String>();

		int registros = 0;
		double montoTotal = 0;
		String periodo = ""; 

		double cons_gravada = 0;
		double cons_iva10 = 0;
		
		for (Venta venta : ventas) {
			
			if (!venta.isAnulado()) {
				String rSocial = venta.getDenominacion() == null ? venta.getCliente().getRazonSocial() : venta.getDenominacion();
				String ruc = venta.getCliente().getRuc();
				if (ruc.isEmpty() || ruc == null || ruc.length() < 3 || ruc.equals(Configuracion.RUC_EMPRESA_LOCAL)) {
					ruc = Configuracion.RUC_EMPRESA_LOCAL;
					rSocial = "IMPORTE CONSOLIDADO";
					cons_iva10 += redondear(venta.getTotalIva10());
					cons_gravada += redondear(venta.getTotalGravado10());
				} else {
					String col1 = "2";
					String col2 = ruc.substring(0, ruc.length() - 2);
					String dv = ruc.substring(ruc.length() - 1);
					String col5 = "1";
					String nro = venta.getNumero();
					String fecha = misc.dateToString(venta.getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
					periodo = Utiles.getDateToString(venta.getFecha(), "yyyyMM");
					double iva10 = redondear(venta.getTotalIva10());
					double gravada10 = redondear(venta.getTotalGravado10());
					double iva5 = redondear(venta.getTotalIva5());
					double gravada5 = redondear(venta.getTotalGravado5());
					double exenta = redondear(venta.getTotalExenta());
					double importe = iva10 + gravada10 + iva5 + gravada5 + exenta;
					long col14 = venta.isVentaContado() ? 1 : 2;
					long col15 = 0;
					String col16 = venta.getTimbrado();
					String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial
							+ " \t" + col5 + " \t" + nro + " \t" + fecha + " \t"
							+ FORMATTER.format(gravada10) + "" + " \t"
							+ FORMATTER.format(iva10) + "" + "\t"
							+ FORMATTER.format(gravada5) + "" + "\t"
							+ FORMATTER.format(iva5) + "" + "\t"
							+ FORMATTER.format(exenta) + "" + "\t"
							+ FORMATTER.format(importe) + "" + "\t" + col14 + "" + "\t"
							+ col15 + "" + "\t" + col16 + "" + "\r\n";
					objects.add(object);
					registros++;
					montoTotal += importe;
				}				
			}			
		}
		
		for (NotaCredito nc : notasCredito) {
			
			if (!nc.isAnulado()) {
				String rSocial = nc.getProveedor().getRazonSocial();
				String ruc = nc.getProveedor().getRuc();
				if (ruc.isEmpty() || ruc == null || ruc.length() < 3 || ruc.equals(Configuracion.RUC_EMPRESA_LOCAL)) {
					ruc = Configuracion.RUC_EMPRESA_LOCAL;
					rSocial = "IMPORTE CONSOLIDADO";
					cons_iva10 += redondear(nc.getTotalIva10());
					cons_gravada += redondear(nc.getTotalGravado10());
				} else {
					String col1 = "2";
					String col2 = ruc.substring(0, ruc.length() - 2);
					String dv = ruc.substring(ruc.length() - 1);
					String col5 = "3";
					String nro = nc.getNumero();
					String fecha = misc.dateToString(nc.getFechaEmision(), Misc.DD_MM_YYYY).replace("-", "/");
					periodo = Utiles.getDateToString(nc.getFechaEmision(), "yyyyMM");
					double iva10 = redondear(nc.getTotalIva10());
					double gravada = redondear(nc.getTotalGravado10());
					double exenta = redondear(nc.getTotalExenta());
					double importe = iva10 + gravada + exenta;
					long col10 = 0;
					long col11 = 0;
					long col14 = 2;
					long col15 = 0;
					String col16 = nc.getTimbrado() != null? nc.getTimbrado().getNumero() : "";
					String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial
							+ " \t" + col5 + " \t" + nro + " \t" + fecha + " \t"
							+ FORMATTER.format(gravada) + "" + " \t"
							+ FORMATTER.format(iva10) + "" + "\t"
							+ FORMATTER.format(col10) + "" + "\t"
							+ FORMATTER.format(col11) + "" + "\t"
							+ FORMATTER.format(exenta) + "" + "\t"
							+ FORMATTER.format(importe) + "" + "\t" + col14 + "" + "\t"
							+ col15 + "" + "\t" + col16 + "" + "\r\n";
					objects.add(object);
					registros++;
					montoTotal += importe;
				}
			}			
		}
		
		String ruc = Configuracion.RUC_EMPRESA_LOCAL;
		String rSocial = "IMPORTE CONSOLIDADO";
		String col1 = "2";
		String col2 = ruc.substring(0, ruc.length() - 2);
		String dv = ruc.substring(ruc.length() - 1);
		String col5 = "0";
		String nro = "0";
		String fecha = misc.dateToString(ventas.get(0).getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
		periodo = Utiles.getDateToString(ventas.get(0).getFecha(), "yyyyMM");
		long col10 = 0;
		long col11 = 0;
		long col12 = 0;
		long col14 = 2;
		long col15 = 0;
		String col16 = "0";
		String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial
				+ " \t" + col5 + " \t" + nro + " \t" + fecha + " \t"
				+ FORMATTER.format(cons_gravada) + "" + " \t"
				+ FORMATTER.format(cons_iva10) + "" + "\t"
				+ FORMATTER.format(col10) + "" + "\t"
				+ FORMATTER.format(col11) + "" + "\t"
				+ FORMATTER.format(col12) + "" + "\t"
				+ FORMATTER.format(cons_gravada + cons_iva10) + "" + "\t" + col14 + "" + "\t"
				+ col15 + "" + "\t" + col16 + "" + "\r\n";
		objects.add(object);
		registros++;
		montoTotal += (cons_gravada + cons_iva10);
		
		String cabecera = Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS) ? 
				getCabeceraBaterias(registros, montoTotal, periodo) : getCabecera(registros, montoTotal, periodo);

		saveArchivo(
				objects,
				cabecera,
				"HechaukaVentas-"
						+ misc.dateToString(new Date(), Misc.DD_MM_YYYY));
	}
	
	/**
	 * Generar informe Hechauka..
	 */
	public static synchronized void generarInformeHechaukaCompras(List<NotaCredito> ncs,
			List<CompraLocalFactura> compras, List<Gasto> gastos, List<ImportacionFactura> importaciones) throws Exception {
		Misc misc = new Misc();
		List<String> objects = new ArrayList<String>();

		int registros = 0;
		double montoTotal = 0;
		
		for (CompraLocalFactura compra : compras) {
			
			String ruc = compra.getProveedor().getRuc();			
			if (ruc.isEmpty()) {
				ruc = Configuracion.RUC_EMPRESA_LOCAL;
			}

			String col1 = "1";
			String col2 = ruc.substring(0, ruc.length() - 2);
			String dv = ruc.substring(ruc.length() - 1);
			String rSocial = compra.getProveedor().getRazonSocial();
			String timbrado = compra.getTimbrado() != null? compra.getTimbrado().getNumero() : "";
			String col5 = "1";
			String nro = compra.getNumero();
			String fecha = misc.dateToString(compra.getFechaOriginal(), Misc.DD_MM_YYYY).replace("-", "/");
			double importe = redondear(compra.getImporteGs());
			double iva10 = redondear(misc.calcularIVA(importe, 10));
			double gravada = redondear(importe - iva10);
			long col10 = 0;
			long col11 = 0;
			long col12 = 0;
			long col14 = compra.isContado() ? 1 : 2;
			long col15 = 0;
			long col16 = 0;
			String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial + " \t" + timbrado + " \t" + col5 + " \t" + nro + " \t"
					+ fecha + " \t" + FORMATTER.format(gravada) + "" + " \t" + FORMATTER.format(iva10) + "" + "\t"
					+ FORMATTER.format(col10) + "" + "\t" + FORMATTER.format(col11) + "" + "\t"
					+ FORMATTER.format(col12) + "" + "\t" + col14 + "" + "\t"
					+ col15 + "" + "\t" + col16 + "" + "\r\n";
			objects.add(object);
			registros++;
			montoTotal += importe;
		}
		
		for (Gasto gasto : gastos) {
			
			String ruc = gasto.getProveedor().getRuc();		
			
			if (!ruc.equals("80029222-7")) {				
				if (ruc.isEmpty()) {
					ruc = Configuracion.RUC_EMPRESA_LOCAL;
				}

				String col1 = "2";
				String col2 = ruc.substring(0, ruc.length() - 2);
				String dv = ruc.substring(ruc.length() - 1);
				String rSocial = gasto.getProveedor().getRazonSocial();
				String timbrado = gasto.getTimbrado();
				String col5 = "1";
				String nro = gasto.getNumeroFactura();
				String fecha = misc.dateToString(gasto.getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
				double importe = redondear(gasto.getImporteGs());
				double iva10 = redondear(gasto.getIva10());
				double gravada = redondear(gasto.getGravada10());
				double exenta = redondear(gasto.getExenta());
				double gravada5 = redondear(gasto.getGravada5());
				double iva5 = redondear(gasto.getIva5());
				long col14 = gasto.isContado() ? 1 : 2;
				long col15 = 0;
				long col16 = 0;
				String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial + " \t" + timbrado + " \t" + col5 + " \t" + nro + " \t"
						+ fecha + " \t" + FORMATTER.format(gravada) + "" + " \t" + FORMATTER.format(iva10) + "" + "\t"
						+ FORMATTER.format(gravada5) + "" + "\t" + FORMATTER.format(iva5) + "" + "\t"
						+ FORMATTER.format(exenta) + "" + "\t" + col14 + "" + "\t"
						+ col15 + "" + "\t" + col16 + "" + "\r\n";
				objects.add(object);
				registros++;
				montoTotal += importe;
			}
		}
		
		for (Gasto gasto : gastos) {
			
			String ruc = gasto.getProveedor().getRuc();		
			
			if (ruc.equals("80029222-7")) {				
				ruc = Configuracion.RUC_EMPRESA_EXTERIOR;

				String col1 = "2";
				String col2 = ruc.substring(0, ruc.length() - 2);
				String dv = ruc.substring(ruc.length() - 1);
				String rSocial = "PROVEEDORES DEL EXTERIOR";
				String timbrado = gasto.getTimbrado();
				String col5 = "4";
				String nro = gasto.getNumeroFactura();
				String fecha = misc.dateToString(gasto.getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
				double iva10 = redondear(gasto.getIva10());
				double gravada = redondear(gasto.getBaseImponible());
				double importe = redondear(gravada + iva10);
				long col10 = 0;
				long col11 = 0;
				long col12 = 0;
				long col14 = gasto.isContado() ? 1 : 2;
				long col15 = 0;
				long col16 = 0;
				String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial + " \t" + timbrado + " \t" + col5 + " \t" + nro + " \t"
						+ fecha + " \t" + FORMATTER.format(gravada) + "" + " \t" + FORMATTER.format(iva10) + "" + "\t"
						+ FORMATTER.format(col10) + "" + "\t" + FORMATTER.format(col11) + "" + "\t"
						+ FORMATTER.format(col12) + "" + "\t" + col14 + "" + "\t"
						+ col15 + "" + "\t" + col16 + "" + "\r\n";
				objects.add(object);
				registros++;
				montoTotal += importe;
			}
		}
		
		for (ImportacionFactura compra : importaciones) {
			
			String ruc = Configuracion.RUC_EMPRESA_EXTERIOR;			
			if (ruc.isEmpty()) {
				ruc = Configuracion.RUC_EMPRESA_EXTERIOR;
			}

			String col1 = "2";
			String col2 = ruc.substring(0, ruc.length() - 2);
			String dv = ruc.substring(ruc.length() - 1);
			String rSocial = compra.getProveedor().getRazonSocial();
			String timbrado = " ";
			String col5 = "4";
			String nro = compra.getNumero();
			String fecha = misc.dateToString(compra.getFechaOriginal(), Misc.DD_MM_YYYY).replace("-", "/");
			double importe = redondear(compra.getTotalImporteDs() * compra.getPorcProrrateo());
			double iva10 = 0.0;
			double gravada = 0.0;
			long col10 = 0;
			long col11 = 0;
			long col12 = 0;
			long col14 = 2;
			long col15 = 0;
			long col16 = 0;
			String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial + " \t" + timbrado + " \t" + col5 + " \t" + nro + " \t"
					+ fecha + " \t" + FORMATTER.format(gravada) + "" + " \t" + FORMATTER.format(iva10) + "" + "\t"
					+ FORMATTER.format(col10) + "" + "\t" + FORMATTER.format(col11) + "" + "\t"
					+ FORMATTER.format(col12) + "" + "\t" + col14 + "" + "\t"
					+ col15 + "" + "\t" + col16 + "" + "\r\n";
			objects.add(object);
			registros++;
			montoTotal += importe;
		}

		for (NotaCredito nc : ncs) {
			
			if (!nc.isAnulado()) {
				String ruc = nc.getCliente().getRuc();
				if (ruc.isEmpty()) {
					ruc = Configuracion.RUC_EMPRESA_LOCAL;
				}

				String col1 = "2";
				String col2 = ruc.substring(0, ruc.length() - 2);
				String dv = ruc.substring(ruc.length() - 1);
				String rSocial = nc.getCliente().getRazonSocial();
				String timbrado = nc.getTimbrado_();
				String col5 = "3";
				String nro = nc.getNumero();
				String fecha = misc.dateToString(nc.getFechaEmision(), Misc.DD_MM_YYYY).replace("-", "/");
				double importe = redondear(nc.getImporteGs());
				double iva10 = redondear(nc.getTotalIva10());
				double gravada = redondear(nc.getTotalGravado10());
				double exenta = redondear(nc.getTotalExenta()); 
				long col10 = 0;
				long col11 = 0;
				long col14 = nc.isNotaCreditoVentaContado() ? 1 : 2;
				long col15 = 0;
				long col16 = 0;
				String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial + " \t" + timbrado
						+ " \t" + col5 + " \t" + nro + " \t" + fecha + " \t"
						+ FORMATTER.format(gravada) + "" + " \t"
						+ FORMATTER.format(iva10) + "" + "\t"
						+ FORMATTER.format(col10) + "" + "\t"
						+ FORMATTER.format(col11) + "" + "\t"
						+ FORMATTER.format(exenta) + "" + "\t"
						+ col14 + "" + "\t"
						+ col15 + "" + "\t" + col16 + "" + "\r\n";
				objects.add(object);
				registros++;
				montoTotal += importe;
			}			
		}
		saveArchivo(
				objects,
				getCabeceraNotaCred(registros, montoTotal),
				"HechaukaCompras-"
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
		Filedownload.save(InformeHechauka.PATH_FILE + name +".txt", null);
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
	private static String getCabeceraNotaCred(int registros, double montoTotal) {
		String out = "";
		
		String col1 = "1";
		String periodo = "201601";
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
