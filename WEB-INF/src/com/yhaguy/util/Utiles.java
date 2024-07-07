package com.yhaguy.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;

import net.sf.jme.Monto;

public class Utiles {
	
	public static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	public static final NumberFormat FORMATTER_DS = new DecimalFormat("###,###,##0.00");
	public static final NumberFormat FORMATTER_DS_ = new DecimalFormat("###,###,##0.0000");
	public static final String DD_MM_YYYY = "dd-MM-yyyy";
	public static final String DD_MM_YY = "dd-MM-yy";
	public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy hh:mm:ss";
	public static final String D_MMMM_YYYY = "d 'de' MMMM 'del' yyyy";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd hh:mm:ss";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	
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
	
	public static final String ANHO_2012 = "2012";
	public static final String ANHO_2013 = "2013";
	public static final String ANHO_2014 = "2014";
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
	public static Date getFechaInicioMes(int mes, int anho) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, mes - 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.YEAR, anho);
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
	 * @return la fecha de inicio del mes..
	 */
	@SuppressWarnings("deprecation")
	public static Date getFechaActual() {
		Date out = new Date();
		out.setHours(0);out.setMinutes(0);out.setSeconds(0);
		return out;
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
	 * @return la fecha de fin del mes..
	 */
	public static Date getFechaFinMes(int mes, int anho) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes - 1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		cal.set(Calendar.YEAR, anho);
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
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			return Utiles.getFecha("01-08-2016 00:00:00");
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YMRA)) {
			return Utiles.getFecha("01-01-2016 00:00:00");
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRPS)) {
			return Utiles.getFecha("01-11-2019 00:00:00");
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRSA)) {
			return Utiles.getFecha("05-10-2018 00:00:00");
		}
		return Utiles.getFecha("01-06-2020 00:00:00");
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
	 * @return el dia segun fecha..
	 */
	public static String getDia(Date fecha) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		int day = c.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case 1:
			return "DOMINGO";
		case 2:
			return "LUNES";
		case 3:
			return "MARTES";
		case 4:
			return "MIERCOLES";
		case 5:
			return "JUEVES";
		case 6:
			return "VIERNES";
		case 7:
			return "SABADO";
		}
		return "";
	}
	
	/**
	 * @return
	 * [0]:horas
	 * [1]:minutos
	 * [2]:segundos
	 */
	public static Object[] diferenciaTiempo(Date fecha1, Date fecha2) {
		long uno = fecha1.getTime();
		long dos = fecha2.getTime();
		long diferencia = uno - dos;
		long segundos = (diferencia / 1000) % 60;
		long minutos = (diferencia / (1000 * 60)) % 60;
		long horas = (diferencia / (1000 * 60 * 60)) % 24;
		return new Object[] { horas, minutos, segundos };
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
			return string.substring(0, max) + "..";
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
	 * @return fecha en letras..
	 */
	public static String getDateLetter() {
		Locale locale = new Locale("es-PY", "PY");
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
		String date = dateFormat.format(new Date());
		return date;
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
		String secretKey = "qualityinfosolutions";
		String base64EncryptedString = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

			SecretKey key = new SecretKeySpec(keyBytes, "DESede");
			Cipher cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] plainTextBytes = cadena.getBytes("utf-8");
			byte[] buf = cipher.doFinal(plainTextBytes);
			byte[] base64Bytes = Base64.encodeBase64(buf);
			base64EncryptedString = new String(base64Bytes);

		} catch (Exception ex) {
		}
		return base64EncryptedString;
	}
	
	public static String Desencriptar(String textoEncriptado) throws Exception {

		String secretKey = "qualityinfosolutions"; 
		String base64EncryptedString = "";

		try {
			byte[] message = Base64.decodeBase64(textoEncriptado.getBytes("utf-8"));
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			SecretKey key = new SecretKeySpec(keyBytes, "DESede");

			Cipher decipher = Cipher.getInstance("DESede");
			decipher.init(Cipher.DECRYPT_MODE, key);

			byte[] plainText = decipher.doFinal(message);

			base64EncryptedString = new String(plainText, "UTF-8");

		} catch (Exception ex) {
			ex.printStackTrace();
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
	 * @return el numero del mes segun parametro..
	 */
	public static int getNumeroMes_(Date fecha) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
	    return Integer.parseInt(dateFormat.format(fecha));
	}
	
	/**
	 * @return el nombre del mes..
	 */
	public static String getNombreMes(Date fecha) {
		Format formatter = new SimpleDateFormat("MMMM"); 
	    String out = formatter.format(fecha);
	    return out.toUpperCase();
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
		out.add(ANHO_2020);
		out.add(ANHO_2021);
		out.add(ANHO_2022);
		out.add(ANHO_2023);
		out.add(ANHO_2024);
		out.add(ANHO_2025);
		out.add(ANHO_2026);
		out.add(ANHO_2027);
		out.add(ANHO_2028);
		out.add(ANHO_2029);
		out.add(ANHO_2030);
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
	
	/**
	 * @return true si solo contiene numeros..
	 */
	public static boolean validarNumeroFactura(String str) {
		if (str == null || str.length() == 0)
			return false;

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)) && (Character.toString(str.charAt(i)).compareTo("-") != 0)) {
				return false;
			}
		}
		return true;
	}
	
	// Convierte un numero a su equivalente en Letras rango (0 a 999999999999)..
    public static String numeroAletras(Object numero) {
        String out = "";

        try {
            if (numero instanceof Integer) {
                out = Monto.aLetras(numero.toString());

            } else if (numero instanceof Long) {
                out = Monto.aLetras(numero.toString());

            } else if (numero instanceof Double) {

                DecimalFormat f = new DecimalFormat("##0.00");
                String valor = f.format(numero);
                String sepDec = f.getDecimalFormatSymbols().getDecimalSeparator() + "";
                int index = valor.lastIndexOf(sepDec);
                String entero = valor.substring(0, index);
                String decimal = valor.substring(index + 1);

                if (decimal.compareTo("00") != 0) {
                    out = Monto.aLetras(entero) + decimal + "/100";
                } else {
                    out = Monto.aLetras(entero);
                }

            } else if (numero instanceof String) {
                out = Monto.aLetras((String) numero);

            } else {
                throw new Exception("El tipo de dato no es un número");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String first = out.trim().substring(0, 1);
        return out.replaceFirst(first, first.toUpperCase());
    }

    /**
     * @return validador de password
     */
	public static boolean isValidPassword(String password, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
	
	public static int getAnhos(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
            (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) &&   
            a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }
	
	public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    
    /**
	 * parse xml
	 */
	public static String parseXML(String xmlPath, String tag) {
		DocumentBuilder dBuilder;
		try {
			dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(new File(xmlPath));
			return doc.getElementsByTagName(tag).item(0).getTextContent();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	/**
	 * @return ruc según empresa..
	 */
	public static String getRucEmpresa() {
		switch (Configuracion.empresa) {
		case Configuracion.EMPRESA_YRSA:
			return Configuracion.RUC_YHAGUY_REPUESTOS;
			
		case Configuracion.EMPRESA_GROUPAUTO:
			return Configuracion.RUC_GROUPAUTO;
			
		case Configuracion.EMPRESA_YRPS:
			return Configuracion.RUC_YHAGUY_REPRESENTACIONES;
			
		case Configuracion.EMPRESA_AUTOCENTRO:
			return Configuracion.RUC_YHAGUY_REPRESENTACIONES;
		}
		return "";
	}
	
	/**
	 * @return getUltimoDiaMes..
	 */
	public static int getUltimoDiaMes(int anho, int mes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, anho);
        calendar.set(Calendar.MONTH, mes - 1); // (0-based index)
        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);        
        return lastDayOfMonth;
	}
	
	/**
	 * @return nro de mes..
	 */
	public static int getNumeroMes(String mes) {
		Map<String, Integer> monthMap = new HashMap<>();
		monthMap.put("enero", 1);
		monthMap.put("febrero", 2);
		monthMap.put("marzo", 3);
		monthMap.put("abril", 4);
		monthMap.put("mayo", 5);
		monthMap.put("junio", 6);
		monthMap.put("julio", 7);
		monthMap.put("agosto", 8);
		monthMap.put("septiembre", 9);
		monthMap.put("octubre", 10);
		monthMap.put("noviembre", 11);
		monthMap.put("diciembre", 12);
		return monthMap.get(mes.toLowerCase());
	}
	
	/**
	 * @return only numbers..
	 */
	public static boolean containsOnlyNumbers(String str) {
        return str.matches("\\d+");
    }
	
	public static void mainX(String[] args) {		
		JSONParser parser = new JSONParser();
		try {
			Map<String, String> map = new HashMap<String, String>();
			Object obj = parser.parse(new FileReader("C:\\Users\\Sergio Raul\\Desktop\\010206022021.json"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject data = (JSONObject) jsonObject.get("data");
			JSONArray list = (JSONArray) data.get("listaRemesaNacional");
			for (Object item : list) {
				JSONObject ob = (JSONObject) item;
				String cod = ob.get("sucursalDestino") + "";
				map.put(cod, cod);
			}
			for (String key : map.keySet()) {
				System.out.println(key);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
