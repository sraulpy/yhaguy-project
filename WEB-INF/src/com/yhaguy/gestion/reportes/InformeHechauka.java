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
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

public class InformeHechauka {
	
	static final String PATH_FILE = "/yhaguy/archivos/tesaka/";
	static final NumberFormat FORMATTER = new DecimalFormat("##########0");

	/**
	 * Generar informe Hechauka..
	 */
	public static synchronized void generarInformeHechauka(List<Venta> ventas)
			throws Exception {
		Misc misc = new Misc();
		List<String> objects = new ArrayList<String>();

		int registros = 0;
		double montoTotal = 0;
		String periodo = "";

		for (Venta venta : ventas) {

			String ruc = venta.getCliente().getRuc();
			if (ruc.isEmpty()) {
				ruc = Configuracion.RUC_EMPRESA_LOCAL;
			}

			String col1 = "2";
			String col2 = ruc.substring(0, ruc.length() - 2);
			String dv = ruc.substring(ruc.length() - 1);
			String rSocial = venta.getDenominacion() == null ? venta.getCliente().getRazonSocial() : venta.getDenominacion();
			String col5 = "1";
			String nro = venta.getNumero();
			String fecha = misc.dateToString(venta.getFecha(), Misc.DD_MM_YYYY).replace("-", "/");
			periodo = Utiles.getDateToString(venta.getFecha(), "yyyyMM");
			double importe = venta.isAnulado() ? 0 : redondear(venta.getTotalImporteGs());
			double iva10 = venta.isAnulado() ? 0 : redondear(misc.calcularIVA(importe, 10));
			double gravada = importe - iva10;
			long col10 = 0;
			long col11 = 0;
			long col12 = 0;
			long col14 = venta.isVentaContado() ? 1 : 2;
			long col15 = 0;
			long col16 = 0;
			String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial
					+ " \t" + col5 + " \t" + nro + " \t" + fecha + " \t"
					+ FORMATTER.format(gravada) + "" + " \t"
					+ FORMATTER.format(iva10) + "" + "\t"
					+ FORMATTER.format(col10) + "" + "\t"
					+ FORMATTER.format(col11) + "" + "\t"
					+ FORMATTER.format(col12) + "" + "\t"
					+ FORMATTER.format(importe) + "" + "\t" + col14 + "" + "\t"
					+ col15 + "" + "\t" + col16 + "" + "\r\n";
			objects.add(object);
			registros++;
			montoTotal += importe;
		}
		
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
	public static synchronized void generarInformeHechaukaNotaCred(List<NotaCredito> ncs) throws Exception {
		Misc misc = new Misc();
		List<String> objects = new ArrayList<String>();

		int registros = 0;
		double montoTotal = 0;

		for (NotaCredito nc : ncs) {

			String ruc = nc.getCliente().getRuc();
			if (ruc.isEmpty()) {
				ruc = Configuracion.RUC_EMPRESA_LOCAL;
			}

			String col1 = "2";
			String col2 = ruc.substring(0, ruc.length() - 2);
			String dv = ruc.substring(ruc.length() - 1);
			String rSocial = nc.getCliente().getRazonSocial();
			String col5 = "1";
			String nro = nc.getNumero();
			String fecha = misc.dateToString(nc.getFechaEmision(), Misc.DD_MM_YYYY).replace("-", "/");
			double importe = nc.isAnulado() ? 0 : redondear(nc.getImporteGs());
			double iva10 = nc.isAnulado() ? 0 : redondear(misc.calcularIVA(importe, 10));
			double gravada = importe - iva10;
			long col10 = 0;
			long col11 = 0;
			long col12 = 0;
			long col14 = nc.isNotaCreditoVentaContado() ? 1 : 2;
			long col15 = 0;
			long col16 = 0;
			String object = col1 + " \t" + col2 + " \t" + dv + " \t" + rSocial
					+ " \t" + col5 + " \t" + nro + " \t" + fecha + " \t"
					+ FORMATTER.format(gravada) + "" + " \t"
					+ FORMATTER.format(iva10) + "" + "\t"
					+ FORMATTER.format(col10) + "" + "\t"
					+ FORMATTER.format(col11) + "" + "\t"
					+ FORMATTER.format(col12) + "" + "\t"
					+ FORMATTER.format(importe) + "" + "\t" + col14 + "" + "\t"
					+ col15 + "" + "\t" + col16 + "" + "\r\n";
			objects.add(object);
			registros++;
			montoTotal += importe;
		}
		saveArchivo(
				objects,
				getCabeceraNotaCred(registros, montoTotal),
				"HechaukaNotasCredito-"
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
