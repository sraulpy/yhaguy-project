package com.yhaguy.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;

import com.coreweb.util.MyArray;

public class Utiles {
	
	public static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	public static final NumberFormat FORMATTER_DS = new DecimalFormat("###,###,##0.00");
	public static final NumberFormat FORMATTER_DS_ = new DecimalFormat("###,###,##0.0000");
	public static final String DD_MM_YYYY = "dd-MM-yyyy";
	public static final String DD_MM_YY = "dd-MM-yy";
	public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy hh:mm:ss";
	public static final String D_MMMM_YYYY = "d 'de' MMMM 'del' yyyy";
	
	public static final String ENERO = "ENERO";
	public static final String FEBRERO = "FEBRERO";
	public static final String MARZO = "MARZO";
	public static final String ABRIL = "ABRIL";
	public static final String MAYO = "MAYO";
	public static final String JUNIO = "JUNIO";
	public static final String JULIO = "JULIO";
	public static final String AGOSTO = "AGOSTO";
	public static final String SETIEMBRE = "SETIEMBRE";
	public static final String OCTUBRE = "OCTUBRE";
	public static final String NOVIEMBRE = "NOVIEMBRE";
	public static final String DICIEMBRE = "DICIEMBRE";
	
	public static final int NRO_ENERO = 1;
	public static final int NRO_FEBRERO = 2;
	public static final int NRO_MARZO = 3;
	public static final int NRO_ABRIL = 4;
	public static final int NRO_MAYO = 5;
	public static final int NRO_JUNIO = 6;
	public static final int NRO_JULIO = 7;
	public static final int NRO_AGOSTO = 8;
	public static final int NRO_SETIEMBRE = 9;
	public static final int NRO_OCTUBRE = 10;
	public static final int NRO_NOVIEMBRE = 11;
	public static final int NRO_DICIEMBRE = 12;
	
	public static final String NRO_ENERO_ = "01";
	public static final String NRO_FEBRERO_ = "02";
	public static final String NRO_MARZO_ = "03";
	public static final String NRO_ABRIL_ = "04";
	public static final String NRO_MAYO_ = "05";
	public static final String NRO_JUNIO_ = "06";
	public static final String NRO_JULIO_ = "07";
	public static final String NRO_AGOSTO_ = "08";
	public static final String NRO_SETIEMBRE_ = "09";
	public static final String NRO_OCTUBRE_ = "10";
	public static final String NRO_NOVIEMBRE_ = "11";
	public static final String NRO_DICIEMBRE_ = "12";
	
	public static final String ANHO_2015 = "2015";
	public static final String ANHO_2016 = "2016";
	public static final String ANHO_2017 = "2017";
	public static final String ANHO_2018 = "2018";
	public static final String ANHO_2019 = "2019";
	public static final String ANHO_2020 = "2020";
	public static final String ANHO_2021 = "2021";
	public static final String ANHO_2022 = "2022";
	public static final String ANHO_2023 = "2023";
	public static final String ANHO_2024 = "2024";
	public static final String ANHO_2025 = "2025";
	public static final String ANHO_2026 = "2026";
	public static final String ANHO_2027 = "2027";
	public static final String ANHO_2028 = "2028";
	public static final String ANHO_2029 = "2029";
	public static final String ANHO_2030 = "2030";
	
	/**
	 * @return el anho actual..
	 */
	public static final String getAnhoActual() {
		return Utiles.getDateToString(new Date(), "yyyy");
	}
	
	/**
	 * @return el anho actual..
	 */
	public static final String getMesActual() {
		return Utiles.getDateToString(new Date(), "MM");
	}

	/**
	 * @return la fecha tipo Date a partir de un String..
	 */
	public static Date getFecha(String fecha) throws Exception {
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		return formatter.parse(fecha);		
	}
	
	/**
	 * @return la fecha tipo Date a partir de un String..
	 */
	public static Date getFecha(String fecha, String format) throws Exception {
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(fecha);		
	}
	
	/**
	 * @return la fecha de inicio del mes..
	 */
	public static Date getFechaInicioMes(int mes) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, mes - 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}
	
	/**
	 * @return la fecha de inicio del mes..
	 */
	@SuppressWarnings("deprecation")
	public static Date getFechaInicioMes() {
		Date inicio = Utiles.getFechaInicioMes(Integer.parseInt(Utiles.getMesActual()));
		inicio.setHours(0);inicio.setMinutes(0);inicio.setSeconds(0);
		return inicio;
	}
	
	/**
	 * @return la fecha de fin del mes corriente..
	 */
	public static Date getFechaFinMes() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		return cal.getTime();
	}
	
	/**
	 * @return la fecha de fin del mes..
	 */
	public static Date getFechaFinMes(int mes) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes - 1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		return cal.getTime();
	}
	
	/**
	 * @return la fecha de inicio del anho corriente..
	 */
	public static Date getFechaInicioAnho() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_YEAR, 1);
		return c.getTime();
	}
	
	/**
	 * @return la fecha de inicio de operaciones..
	 */
	public static Date getFechaInicioOperaciones() throws Exception {
		return Utiles.getFecha("01-01-2016 00:00:00");
	}
	
	/**
	 * @return true si es despues del horario dado..
	 */
	public static boolean after(Date horario) {
		Calendar cal = Calendar.getInstance();
		return cal.getTime().after(horario);
	}
	
	/**
	 * Agrega dias a una fecha dada..
	 */
	public static Date agregarDias(Date fecha, int dias) {
		return DateUtils.addDays(fecha, dias);
	}
	
	/**
	 * @return el numero de meses entre dos fechas..
	 */
	public static int getNumeroMeses(Date fecha1, Date fecha2) {
	    Calendar d1 = Calendar.getInstance();
	    d1.setTime(fecha1);
	    Calendar d2 = Calendar.getInstance();
	    d2.setTime(fecha2);
	    int diff = (d2.get(Calendar.YEAR) - d1.get(Calendar.YEAR)) * 12 + d2.get(Calendar.MONTH) - d1.get(Calendar.MONTH);
	    return diff;
	}
	
	/**
	 * @return el string con un limite de longitud..
	 */
	public static String getMaxLength(String string, int max) {
		if (string.length() > max)
			return string.substring(0, max) + "...";
		return string;
	}
	
	/**
	 * @return el nro formateado a decimales..
	 */
	public static String getNumberFormat(double param) {
		return FORMATTER.format(param);
	}
	
	/**
	 * @return el nro formateado a decimales..
	 */
	public static String getNumberFormatDs(double param) {
		return FORMATTER_DS.format(param);
	}
	
	/**
	 * @return el nro formateado a decimales 4 dec..
	 */
	public static String getNumberFormatDs_(double param) {
		return FORMATTER_DS_.format(param);
	}
	
	/**
	 * @return la fecha en formato string..
	 */
	public static String getDateToString(Date date, String format) {
		if (date == null) {
			return "-";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String out2 = sdf.format(date);

		return out2;
	}
	
	/**
	 * @return el valor redondeado..
	 */
	public static double getRedondeo(double valor) {
		return Math.rint(valor * 1) / 1;
	}
	
	/**
	 * @return el valor redondeado a dos decimales..
	 */
	public static double redondeoDosDecimales(double d) {
		double d2 = Math.rint(d * 100) / 100;
		return d2;
	}

	/**
	 * @return el valor redondeado a cuatro decimales..
	 */
	public static double redondeoCuatroDecimales(double d) {
		double d2 = Math.rint(d * 10000) / 10000;
		return d2;
	}
	
	/**
	 * @return el iva.. 
	 */
	public static double getIVA(double ivaIncluido, int porcentajeIva) {
		return (ivaIncluido / (100 + porcentajeIva)) * porcentajeIva;
	}
	
	/**
	 * @return el porcentaje segun el valor..
	 */
	public static double obtenerPorcentajeDelValor(double valor1, double valor2) {
		return (valor1 / valor2) * 100;
	}
	
	/**
	 * @return el valor segun el porcentaje
	 */
	public static double obtenerValorDelPorcentaje(double valor, double porcentaje) {
		return (valor * porcentaje) / 100;
	}
	
	/**
	 * @return la fecha en hora 23:59
	 */
	public static Date toFecha2400(Date fecha) {
		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(fecha);
		dateCal.set(Calendar.HOUR_OF_DAY, 23);
		dateCal.set(Calendar.MINUTE, 59);
		dateCal.set(Calendar.SECOND, 59);
		dateCal.set(Calendar.MILLISECOND, 999);
		return dateCal.getTime();
	}

	/**
	 * @return la fecha en hora 00:00
	 */
	public static Date toFecha0000(Date fecha) {
		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(fecha);
		dateCal.set(Calendar.HOUR_OF_DAY, 0);
		dateCal.set(Calendar.MINUTE, 0);
		dateCal.set(Calendar.SECOND, 0);
		dateCal.set(Calendar.MILLISECOND, 0);
		return dateCal.getTime();
	}
	
	/**
	 * @return el nro de dias entre dos fechas..
	 */
	public static long diasEntreFechas(Date d1, Date d2) {
		long aux = 0;
		long out = 0;
		long ld1 = toFecha0000(d1).getTime();
		long ld2 = toFecha2400(d2).getTime();

		long dia = (24 * 60 * 60 * 1000);
		long diff = ld2 - ld1;

		if (diff < dia && diff > 0) {
			return 0;
		}
		if (diff < 0) {
			aux = -1;
			if (diff * -1 < dia) {
				return -1;
			}

		}
		out = (diff / dia) + aux;
		return out;
	}
	
	/**
	 * @return el string encriptado..
	 */
	public static String encriptar(String cadena, boolean caseSensitive) {

		if (caseSensitive == false) {
			cadena = cadena.toLowerCase();
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "** error encriptacion **";
		}
		md.reset();
		md.update(cadena.getBytes());
		byte[] b = md.digest();
		String out = toHexadecimal(b);
		return out;
	}
	
	public static String Desencriptar(String textoEncriptado) throws Exception {

		String secretKey = "qualityinfosolutions"; // llave para desenciptar
													// datos
		String base64EncryptedString = "";

		try {
			byte[] message = Base64.decodeBase64(textoEncriptado
					.getBytes("utf-8"));
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			SecretKey key = new SecretKeySpec(keyBytes, "DESede");

			Cipher decipher = Cipher.getInstance("DESede");
			decipher.init(Cipher.DECRYPT_MODE, key);

			byte[] plainText = decipher.doFinal(message);

			base64EncryptedString = new String(plainText, "UTF-8");

		} catch (Exception ex) {
		}
		return base64EncryptedString;
	}
	
	/**
	 * @return el byte a hexadecimal..
	 */
	public static String toHexadecimal(byte[] digest) {
		String hash = "";
		for (byte aux : digest) {
			int b = aux & 0xff;
			if (Integer.toHexString(b).length() == 1)
				hash += "0";
			hash += Integer.toHexString(b);
		}
		return hash;
	}
	
	/**
	 * @return el mes corriente..
	 */
	public static MyArray getMesCorriente(String anho) throws Exception {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int mes = cal.get(Calendar.MONTH);
		Map<Integer, MyArray> meses = Utiles.getMeses(anho);
		return meses.get(mes + 1);
	}
	
	/**
	 * @return el mes corriente..
	 */
	public static MyArray getMes(Date date, String anho) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int mes = cal.get(Calendar.MONTH);
		Map<Integer, MyArray> meses = Utiles.getMeses(anho);
		return meses.get(mes + 1);
	}
	
	/**
	 * @return el numero del mes corriente..
	 */
	public static int getNumeroMesCorriente() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int mes = cal.get(Calendar.MONTH);
		return mes + 1;
	}

	/**
	 * @return el numero del mes segun parametro..
	 */
	public static int getNumeroMes(Date fecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		int mes = cal.get(Calendar.MONTH);
		return mes + 1;
	}
	
	/**
	 * @return los meses segun anho..
	 */
	public static Map<Integer, MyArray> getMeses(String anho) throws Exception {
		Map<Integer, MyArray> out = new HashMap<Integer, MyArray>();
		out.put(NRO_ENERO, new MyArray(ENERO, Utiles.getFecha("01-01-" + anho + " 00:00:00"), Utiles.getFecha("31-01-" + anho + " 23:59:00"), NRO_ENERO));
		out.put(NRO_FEBRERO, new MyArray(FEBRERO, Utiles.getFecha("01-02-" + anho + " 00:00:00"), Utiles.getFecha("28-02-" + anho + " 23:59:00"), NRO_FEBRERO));
		out.put(NRO_MARZO, new MyArray(MARZO , Utiles.getFecha("01-03-" + anho + " 00:00:00"), Utiles.getFecha("31-03-" + anho + " 23:59:00"), NRO_MARZO));
		out.put(NRO_ABRIL, new MyArray(ABRIL, Utiles.getFecha("01-04-" + anho + " 00:00:00"), Utiles.getFecha("30-04-" + anho + " 23:59:00"), NRO_ABRIL));
		out.put(NRO_MAYO, new MyArray(MAYO, Utiles.getFecha("01-05-" + anho + " 00:00:00"), Utiles.getFecha("31-05-" + anho + " 23:59:00"), NRO_MAYO));
		out.put(NRO_JUNIO, new MyArray(JUNIO, Utiles.getFecha("01-06-" + anho + " 00:00:00"), Utiles.getFecha("30-06-" + anho + " 23:59:00"), NRO_JUNIO));
		out.put(NRO_JULIO, new MyArray(JULIO, Utiles.getFecha("01-07-" + anho + " 00:00:00"), Utiles.getFecha("31-07-" + anho + " 23:59:00"), NRO_JULIO));
		out.put(NRO_AGOSTO, new MyArray(AGOSTO, Utiles.getFecha("01-08-" + anho + " 00:00:00"), Utiles.getFecha("31-08-" + anho + " 23:59:00"), NRO_AGOSTO));
		out.put(NRO_SETIEMBRE, new MyArray(SETIEMBRE, Utiles.getFecha("01-09-" + anho + " 00:00:00"), Utiles.getFecha("30-09-" + anho + " 23:59:00"), NRO_SETIEMBRE));
		out.put(NRO_OCTUBRE, new MyArray(OCTUBRE, Utiles.getFecha("01-10-" + anho + " 00:00:00"), Utiles.getFecha("31-10-" + anho + " 23:59:00"), NRO_OCTUBRE));
		out.put(NRO_NOVIEMBRE, new MyArray(NOVIEMBRE, Utiles.getFecha("01-11-" + anho + " 00:00:00"), Utiles.getFecha("30-11-" + anho + " 23:59:00"), NRO_NOVIEMBRE));
		out.put(NRO_DICIEMBRE, new MyArray(DICIEMBRE, Utiles.getFecha("01-12-" + anho + " 00:00:00"), Utiles.getFecha("31-12-" + anho + " 23:59:00"), NRO_DICIEMBRE));
		return out;
	}
	
	/**
	 * @return los meses en myarray
	 */
	public static List<MyArray> getMeses() {
		List<MyArray> out = new ArrayList<MyArray>();
		out.add(new MyArray(NRO_ENERO, ENERO));
		out.add(new MyArray(NRO_FEBRERO, FEBRERO));
		out.add(new MyArray(NRO_MARZO, MARZO));
		out.add(new MyArray(NRO_ABRIL, ABRIL));
		out.add(new MyArray(NRO_MAYO, MAYO));
		out.add(new MyArray(NRO_JUNIO, JUNIO));
		out.add(new MyArray(NRO_JULIO, JULIO));
		out.add(new MyArray(NRO_AGOSTO, AGOSTO));
		out.add(new MyArray(NRO_SETIEMBRE, SETIEMBRE));
		out.add(new MyArray(NRO_OCTUBRE, OCTUBRE));
		out.add(new MyArray(NRO_NOVIEMBRE, NOVIEMBRE));
		out.add(new MyArray(NRO_DICIEMBRE, DICIEMBRE));
		return out;
	}
	
	/**
	 * @return los meses en String..
	 */
	public static List<String> getMeses_() {
		List<String> out = new ArrayList<String>();
		out.add(ENERO);
		out.add(FEBRERO);
		out.add(MARZO);
		out.add(ABRIL);
		out.add(MAYO);
		out.add(JUNIO);
		out.add(JULIO);
		out.add(AGOSTO);
		out.add(SETIEMBRE);
		out.add(OCTUBRE);
		out.add(NOVIEMBRE);
		out.add(DICIEMBRE);
		return out;
	}
	
	/**
	 * @return los nros de meses en String..
	 */
	public static List<String> getNumeroMeses_() {
		List<String> out = new ArrayList<String>();
		out.add(NRO_ENERO_);
		out.add(NRO_FEBRERO_);
		out.add(NRO_MARZO_);
		out.add(NRO_ABRIL_);
		out.add(NRO_MAYO_);
		out.add(NRO_JUNIO_);
		out.add(NRO_JULIO_);
		out.add(NRO_AGOSTO_);
		out.add(NRO_SETIEMBRE_);
		out.add(NRO_OCTUBRE_);
		out.add(NRO_NOVIEMBRE_);
		out.add(NRO_DICIEMBRE_);
		return out;
	}
	
	/**
	 * @return los anhos..
	 */
	public static List<String> getAnhos() {
		List<String> out = new ArrayList<String>();
		out.add(ANHO_2015);
		out.add(ANHO_2016);
		out.add(ANHO_2017);
		out.add(ANHO_2018);
		out.add(ANHO_2019);
		out.add(ANHO_2020);
		return out;
	}
	
	/**
	 * @return el promedio..
	 */
	public static long getPromedio(List<Long> values) {
	      long sum = 0;
	      for (Long value : values) {
			sum += value;
		}
	      return sum / values.size();
	  }
}
