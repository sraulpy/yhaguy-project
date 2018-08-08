package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloInformacionExtra;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.ProveedorArticulo;
import com.yhaguy.domain.RegisterDomain;

public class ArticuloMigracion {

	// public static String articuloCSV =
	// "./WEB-INF/docs/migracion/articulos/Articulo/Articulos_GE.csv";
	// public static String articuloNOCSV =
	// "./WEB-INF/docs/migracion/articulos/articuloNO.txt";

	public static String articuloCSV = "./WEB-INF/docs/migracion/final/Articulo_GE.csv";
	public static String articuloNOCSV = "./WEB-INF/docs/migracion/final/Articulo_GE_NO_csv.txt";

	static Hashtable<String, Object> artInv = new Hashtable<>();
	static Hashtable<String, Object> art = new Hashtable<>();
	static ArrayList<String> artNo = new ArrayList<>();

	static Misc m = new Misc();
	static boolean siGraba = true;

	static String[] colArt = { "IDARTICULO", "DESCRIPCION", "CODIGO_FABRICA",
			"CODIGO_BARRA", "CODIGO_ORIGINAL", "IDMARCA", "MARCA", "IDPARTE",
			"PARTE", "IDMARCA_APLICACION", "MARCA_APLICACION",
			"IDMODELO_APLICACION", "MODELO_APLICACION", "IDFAMILIA", "FAMILIA",
			"IDLINEA", "LINEA", "ESTADO", "IDCOLECCION", "COLECCION",
			"FECHA_ALTA" };

	private static String[][] marcaVehiculo = { { "-1", "Sin Marca" },
			{ "1", "MERCEDES BENZ" }, { "2", "SCANIA" }, { "3", "VOLVO" },
			{ "4", "VW" }, { "5", "FORD" }, { "6", "FIAT" },
			{ "8", "CHEVROLET" }, { "9", "RENAULT" }, { "10", "HONDA" },
			{ "31", "SPAAL" }, { "32", "MITSUBISHI" }, { "33", "TOYOTA" },
			{ "34", "PERKINS/MAXION" }, { "35", "CORSA/VECTRA" },
			{ "36", "CORSA/MONZA" }, { "37", "FUSCA" }, { "38", "CORSA" },
			{ "39", "AGRALE" }, { "40", "GM" }, { "41", "FORD/GM" },
			{ "42", "FIAT IVECO" }, { "43", "SEAT" }, { "44", "AUDI" },
			{ "45", "AGRALE" }, { "46", "PEUGEOT" }, { "47", "MWM" },
			{ "48", "RANDON" }, { "49", "NWM" }, { "51", "CATERPILLAR" },
			{ "52", "VALMET-VALTRA" }, { "50", "DAEWOO" },
			{ "53", "NEW HOLLAND" }, { "57", "KIA" }, { "58", "CITROEN" },
			{ "54", "J.I.CASE" }, { "55", "MASSEY/MAXION/AGCO" },
			{ "56", "KOMATSU" }, { "59", "ISUZU" }, { "60", "NISSAN" },
			{ "61", "IVECO" }, { "62", "MAXION" },
			{ "63", "CNH LATINO AMERICANA" }, { "64", "YANMAR" },
			{ "65", "VALMET" }, { "66", "YAMAHA" }, { "67", "FNM" },
			{ "68", "CBT" }, { "69", "HATSUDA" }, { "70", "DODGE" },
			{ "71", "MARCHESAN" }, { "72", "SCHANEIDER" }, { "73", "TOBATA" },
			{ "74", "FORD NEW HOLLAND" }, { "75", "CHRYSLER" },
			{ "76", "AGCO DO BRASIL" }, { "77", "MAQUINARIAS AGRICOLA" },
			{ "78", "FIAT ALLIS" }, { "79", "MASSEY" }, { "80", "EATON" },
			{ "81", "ALFA ROMEO" }, { "82", "SLC" }, { "83", "MF" },
			{ "84", "VILLARES" }, { "85", "SEMEATO" }, { "86", "VME" },
			{ "87", "JACTO" }, { "88", "HUBER WABCO" }, { "89", "LAVRALE" },
			{ "90", "THOMMIGS" }, { "91", "FARYMANN" }, { "93", "CARRETA" },
			{ "94", "SC/MB/FORD" }, { "92", "VIBRO" }, { "95", "CAV" },
			{ "96", "TRUCKI" }, { "97", "OTTO DEUTZ" }, { "98", "FNH" },
			{ "99", "GMC" }, { "100", "CHRYSLER" }, { "101", "LADA" },
			{ "102", "GURGEL" }, { "103", "JOHN DEERE" },
			{ "104", "HYUNDAY ROBE" }, { "105", "HYUNDAI" }, { "106", "VWC" },
			{ "107", "ASIA MOTOR" }, { "108", "J.I. CASE" },
			{ "109", "DYNAPAC" }, { "110", "CASE" }, { "111", "CHANA MOTOR" },
			{ "112", "PENTIUS" }, { "113", "BYPAS" }, { "114", "SUZUKI" },
			{ "115", "DAIHATSU" }, { "116", "MAZDA" }, { "117", "ASIA TOPIC" },
			{ "118", "INGERSOLL RAND" }, { "119", "CHALMERS" },
			{ "120", "SULLIAR" }, { "121", "COSECHADORA CLASS" },
			{ "122", "EUCLID" }, { "123", "CAMION NISSAN" },
			{ "124", "CAMION ISUZU" }, { "125", "CLARK" },
			{ "126", "FORD CARGO" }, { "127", "DEUTZ" }, { "128", "MICHIGAN" } };

	private static String[][] modeloVehiculo = { { "-1", "Sin Modelo" },
			{ "0", "111" }, { "1", "112" }, { "2", "113" }, { "3", "124" },
			{ "4", "P93" }, { "5", "P94" }, { "6", "1113" }, { "7", "1313" },
			{ "8", "1513" }, { "9", "1318" }, { "10", "1620" },
			{ "11", "OM400" }, { "12", "OM500" }, { "13", "OM904" },
			{ "14", "OM906" }, { "15", "Modelo Nuevo" },
			{ "16", "Modelo Viejo" }, { "17", "608" }, { "18", "709" },
			{ "19", "N-10" }, { "20", "1935" }, { "21", "1621" },
			{ "22", "1630" }, { "23", "1519" }, { "24", "2325" },
			{ "25", "1525" }, { "26", "1929" }, { "27", "LO-912" },
			{ "28", "2314" }, { "29", "2225" }, { "30", "2219/20" },
			{ "31", "S6-80" }, { "32", "2013" }, { "33", "OM447" },
			{ "34", "EV-90" }, { "35", "4405" }, { "36", "EV-80B" },
			{ "37", "EV-80/81" }, { "38", "LS1938" }, { "39", "1113/1518" },
			{ "40", "1214" }, { "41", "1618" }, { "42", "340" },
			{ "43", "915" }, { "44", "O370/1R-O400R" }, { "45", "710" },
			{ "46", "712" }, { "47", "1113/1313" }, { "48", "1117/8" },
			{ "49", "1924" }, { "50", "715" }, { "51", "1610" },
			{ "52", "1938" }, { "53", "L200" }, { "54", "143" },
			{ "55", "B 7" }, { "56", "FH12/NH12" }, { "57", "FH12" },
			{ "58", "NH12" }, { "59", "FM12/NM12" }, { "60", "B10" },
			{ "61", "B58" }, { "62", "F10/12" }, { "63", "VW11.130" },
			{ "64", "VW13.130" }, { "65", "VW11.130/13130" },
			{ "66", "VW8100/F-4000" }, { "67", "OM370/371" }, { "68", "912" },
			{ "69", "A 10" }, { "70", "D 20" }, { "71", "D-70" },
			{ "72", "D10/C10" }, { "73", "D10/20" }, { "74", "F-4000" },
			{ "75", "F 1000" }, { "76", "F-600/700/750" }, { "77", "F-14000" },
			{ "79", "1721" }, { "78", "NH12" }, { "84", "2635" },
			{ "85", "P93/94" }, { "86", "TD 70" }, { "87", "TD 100" },
			{ "88", "TD 120/121/122" }, { "89", "1.4/1.6" },
			{ "90", "1500/1600" }, { "91", "1.0" }, { "92", "1.8/2.0" },
			{ "93", "1.0/1.4" }, { "80", "2213" }, { "94", "1.6/1.8" },
			{ "81", "1218" }, { "82", "1114" }, { "83", "C-D 10" },
			{ "95", "1.3" }, { "96", "1.6" }, { "97", "1.9" },
			{ "98", "1.6/2.0" }, { "99", "1.8" }, { "100", "1.0/1.3" },
			{ "101", "1.0/1.5" }, { "102", "1.0/1.6" }, { "103", "1500/1600" },
			{ "104", "113/124" }, { "105", "1418" }, { "106", "O352" },
			{ "107", "D 10" }, { "108", "2.5/2.8" }, { "109", "8.3" },
			{ "110", "9.6" }, { "111", "1300" }, { "112", "OM364" },
			{ "113", "1.0 /1.4 /1.5" }, { "114", "1.3/1.4/1.5" },
			{ "115", "1.0/1.3/1.5" }, { "116", "1.3/1.4/1.6" },
			{ "117", "1.6/1.8/2.0/2.2" }, { "118", "1.0/1.4/1.6" },
			{ "119", "1.5/1.6" }, { "120", "1.6/1.8/2.0" },
			{ "121", "AD 3152" }, { "122", "F 11000" }, { "123", "BF 161" },
			{ "124", "6.6" }, { "125", "N10/12" }, { "126", "N12" },
			{ "127", "BR7" }, { "128", "B-10M" }, { "129", "R-751/R-752" },
			{ "130", "C-10/C-14" }, { "131", "O-370" },
			{ "132", "M80/85/90/93/790" }, { "133", "S10" }, { "134", "D-12" },
			{ "135", "B-12" }, { "136", "1518" }, { "137", "CARGO 1114" },
			{ "138", "11000/12000/14000" }, { "139", "412 D" },
			{ "154", "2.4" }, { "155", "Euro Cargo E120 E15" },
			{ "175", "C1114" }, { "176", "C1621/170 E 21" }, { "140", "K113" },
			{ "156", "NL10/NL12" }, { "157", "2.0" }, { "158", "17-210" },
			{ "159", "24-220" }, { "160", "GMC 6-100" },
			{ "161", "MWM D 229-4" }, { "162", "147" }, { "163", "A3" },
			{ "164", "1.5/2.0" }, { "165", "1.3/1.5" }, { "166", "1.5" },
			{ "167", "D 40" }, { "168", "12 170" }, { "169", "8210.42" },
			{ "170", "CARGO 2932e" }, { "171", "6 CT" }, { "172", "C814" },
			{ "173", "F1000/350/4000" }, { "174", "F 350" },
			{ "177", "C1113" }, { "178", "C1218" }, { "179", "OM 355" },
			{ "180", "1.7" }, { "141", "R 112" }, { "181", "GMC 3-500/6-150" },
			{ "182", "OM 457" }, { "183", "NL 10" }, { "184", "BR7" },
			{ "185", "7500" }, { "186", "1420" }, { "187", "P93/R93/T93" },
			{ "188", "94" }, { "189", "142" }, { "190", "NL 340" },
			{ "191", "FM12/FH12" }, { "142", "R 143" }, { "143", "OM449" },
			{ "144", "OM 906 LA" }, { "145", "712 C" }, { "146", "OM 904" },
			{ "147", "F1000/ D 60" }, { "148", "OM924" },
			{ "149", "F-1000/4000" }, { "150", "CARGO 1621" },
			{ "151", "CARGO 1622" }, { "152", "8150" }, { "153", "1720" },
			{ "192", "1315" }, { "193", "KRONE" },
			{ "194", "RI32218;RE32216" }, { "195", "RI/RE-32218" },
			{ "196", "GUERRA-8\"" }, { "197", "B10/2" }, { "198", "1516" },
			{ "199", "IBERO-8\"" }, { "200", "CDI 311/12/13" },
			{ "213", "1625" }, { "201", "RANDON-8\"" },
			{ "202", "TR 15 X 7*" }, { "203", "BANDEIRANTES" },
			{ "204", "A6" }, { "205", "A8" }, { "206", "BE2.1.2" },
			{ "207", "BABE2.2.2" }, { "208", "BE2.5.1" }, { "209", "BE2.7.2" },
			{ "210", "BE2.7.1/BE2.8.1" }, { "211", "BE2.2.2" },
			{ "212", "BE2.2.2" }, { "214", "M/V/M/N" }, { "215", "CR 13/19" },
			{ "216", "N10/NL10" }, { "217", "2.0/2.2" }, { "218", "2.5/4.1" },
			{ "219", "1.4" }, { "220", "2.5" }, { "221", "MWM 225/226/229" },
			{ "222", "1.0/1.6/1.8" }, { "223", "N/A 410T" },
			{ "224", "D-229" }, { "225", "3.0" }, { "226", "C/21D" },
			{ "230", "L101/S101" }, { "227", "110" }, { "228", "CARRETA" },
			{ "229", "CARRETA" }, { "231", "1414" }, { "232", "2635" },
			{ "233", "1420" }, { "234", "TRATOR 8240" }, { "235", "LK-140" },
			{ "236", "BR 116" }, { "237", "35-300" }, { "238", "7.90" },
			{ "239", "14-150" }, { "240", "VW7.110" }, { "241", "CARGO" },
			{ "242", "CARGO 1422" }, { "243", "12E" }, { "244", "148" },
			{ "245", "1180" }, { "246", "1280" }, { "247", "118" },
			{ "248", "5030" }, { "261", "O-371" }, { "249", "5630" },
			{ "250", "5610" }, { "251", "4600" }, { "252", "6610" },
			{ "253", "1105/2150" }, { "254", "4610" }, { "255", "W20" },
			{ "256", "265" }, { "257", "D65" }, { "258", "D50" },
			{ "259", "TRATOR 65" }, { "260", "1235" }, { "262", "1622" },
			{ "263", "1308" }, { "264", "14B" }, { "266", "814/914" },
			{ "267", "1111" }, { "265", "6.8/6.9" }, { "268", "1524" },
			{ "269", "LPO" }, { "270", "C10/C20" }, { "271", "C10/D10" },
			{ "272", "8.1" }, { "273", "7-100" },
			{ "274", "F7000/12000/13000" }, { "275", "FH12/FM10/12" },
			{ "276", "680" }, { "277", "140" }, { "278", "114" },
			{ "279", "B10M" }, { "280", "F-2000" }, { "281", "914" },
			{ "282", "AC64/98 D64/88" }, { "283", "400" }, { "284", "L75" },
			{ "285", "1819" }, { "286", "F 100" }, { "287", "F-250" },
			{ "288", "405" }, { "289", "307" }, { "290", "1.0/1.8" },
			{ "291", "C3" }, { "292", "1634" }, { "293", "LK 3814" },
			{ "294", "PERKINS" }, { "295", "LK 38" }, { "296", "LK 3815" },
			{ "297", "LK 3816" }, { "298", "E 37" }, { "299", "312/321/331" },
			{ "300", "1921" }, { "301", "MF-65" }, { "302", "X10" },
			{ "303", "TRATOR 4600" }, { "304", "C10" }, { "305", "D225" },
			{ "306", "2.2" }, { "307", "LAVRALE" }, { "308", "1419" },
			{ "309", "AD-14C" }, { "310", "CBT 1020" },
			{ "311", "11R/11T/NSB11" }, { "312", "62/65/68/85/88" },
			{ "313", "DT 180/RD" }, { "314", "BOSCH" }, { "315", "D 9500" },
			{ "316", "60/62/65/80" }, { "317", "100/1020/1065" },
			{ "318", "SLC" }, { "319", "D222" }, { "320", "1020/1090" },
			{ "321", "AD78" }, { "322", "2070/2080" },
			{ "323", "TX 1100/1200" }, { "324", "4600/6600" },
			{ "325", "M130" }, { "326", "190" }, { "327", "3.1" },
			{ "328", "TC11" }, { "329", "EATON/VME" }, { "330", "5600/6600" },
			{ "331", "180/215" }, { "332", "BALDAN" }, { "333", "11/TC10" },
			{ "334", "2.3" }, { "335", "8700/9700" }, { "336", "D226" },
			{ "337", "6600" }, { "338", "F-100/1000" }, { "339", "F-400" },
			{ "340", "F-600" }, { "341", "F-600/7000" }, { "342", "6357" },
			{ "343", "A60-C60-D60" }, { "344", "C60-D60-D70-D90" },
			{ "345", "ACD-20/40" }, { "346", "F-700/F-750/F-950" },
			{ "347", "AD4" }, { "348", "111AT" }, { "349", "68/110/360/600" },
			{ "350", "MF-65X" }, { "351", "MF-50/55" }, { "352", "MF-290" },
			{ "353", "MF-270/285/295" }, { "354", "100" },
			{ "355", "MF-270/295" }, { "356", "MF-C3MD30" },
			{ "357", "AD14B" }, { "358", "110" }, { "359", "331" },
			{ "360", "135/DT180" }, { "361", "P3640" }, { "362", "118" },
			{ "363", "R5/T5" }, { "364", "IE 70/IE 90" }, { "365", "D11000" },
			{ "366", "D100" }, { "367", "8140" }, { "368", "360/600" },
			{ "369", "2300" }, { "370", "115" }, { "371", "C3MD30" },
			{ "372", "D232" }, { "373", "RD 350" }, { "374", "AS140/160" },
			{ "375", "4500/4600" }, { "376", "JK 2000" },
			{ "377", "6600/7600" }, { "378", "13/18/R/B8" },
			{ "379", "1000/1065" }, { "380", "NL12" }, { "381", "1.3/1.6" },
			{ "382", "360" }, { "383", "380" }, { "384", "450" },
			{ "385", "400" }, { "386", "TD121" }, { "387", "TD101" },
			{ "388", "TD122" }, { "389", "14.10" }, { "390", "TD123" },
			{ "391", "TD100/2/3" }, { "392", "TD120" }, { "393", "OTROS" },
			{ "394", "112/3" }, { "395", "124/P94" }, { "396", "TODOS" },
			{ "397", "VM17" }, { "398", "1941" }, { "399", "THD 100EC/ED" },
			{ "400", "110/1" }, { "401", "TRUCKI" }, { "402", "814" },
			{ "403", "110 ID" }, { "404", "4200" }, { "405", "8260" },
			{ "406", "D-100" }, { "407", "1800" }, { "408", "321" },
			{ "409", "VM17" }, { "410", "220" }, { "411", "2.4/2.8" },
			{ "412", "5S 484" }, { "413", "9M 9740" }, { "414", "13170" },
			{ "415", "K3500" }, { "416", "TRACTOR" }, { "417", "3224" },
			{ "418", "4.1" }, { "419", "2.7" }, { "420", "K2700" },
			{ "421", "504" }, { "422", "V8" }, { "423", "L300" },
			{ "424", "11140" }, { "425", "508" }, { "426", "180" },
			{ "427", "6150" }, { "428", "FM12" }, { "429", "2.8" },
			{ "430", "1050" }, { "431", "LB70" }, { "432", "6350" },
			{ "433", "8450" }, { "434", "PC200" }, { "435", "1722" },
			{ "436", "1526" }, { "437", "5600/6300" }, { "438", "180 D" },
			{ "439", "104" }, { "440", "708" }, { "441", "AE-100" },
			{ "442", "141" }, { "443", "1316" } };

	
	private static Hashtable<String, String> marca = new Hashtable<>();
	private static Hashtable<String, String> modelo = new Hashtable<>();
	
	static{
		// marca
		for (int i = 0; i < marcaVehiculo.length; i++) {
			String id = marcaVehiculo[i][0];
			String ma = marcaVehiculo[i][1];
			marca.put(id, ma);
		}
		// modelo
		for (int i = 0; i < modeloVehiculo.length; i++) {
			String id = modeloVehiculo[i][0];
			String ma = modeloVehiculo[i][1];
			modelo.put(id, ma);
		}
	}
	
	/*
	 * "IDARTICULO","IDCOLOR","IDGRUPO","IDMEDIDA",
	 * "IDLINEA","DESCRIPCION","PESO","VOLUMEN", "xxIMPUESTO","ESTADO",
	 * "xxCOMISION",
	 * "REFERENCIA","xxSTOCK","xxCOSTO_PROMEDIO","NROPARTE","xxxxDESCRIPCION_CORTA"
	 * ,
	 * "IDIMPUESTO","IDFAMILIA","xxxxSERIE","IDMARCA","xxxxTALLE","IDCOLECCION"
	 * ,"BARCODE",
	 * "xxxxIDPLANCUENTA","STOCK_MINIMO","STOCK_MAXIMO","xxxxPRECIO_SUGERIDO"
	 * ,"REPLICA",
	 * "xxxIDIMPUESTO_IMP","xxxGARANTIA_CLIENTE","xxxGARANTIA_PROVEEDOR"
	 * ,"xxxKIT","xxSTOCK_KIT",
	 * "xxIDREGIMEN","xxxCOSTO_PROM_USD","xxxCODIGO_NCM"
	 * ,"xxxPRODUCCION","xxCOMBO","xxIDGASTO_IMP",
	 * "xxLOTE","xxID_COD","xxPORC_MERMA"
	 * ,"xxPORC_RENDIMIENTO","xxMINUTA","xxFABRICA",
	 * "xxSTOCK_LOTE","xxDEP_ORIGEN"
	 */
	static Hashtable<String, Integer> colPArt = new Hashtable<>();

	static RegisterDomain rr = RegisterDomain.getInstance();

	static Tipo ARTICULO_ESTADO_ACTIVO;
	static Tipo TIPO_SIN_REFERENCIA;

	static {
		// cargar las posiciones según los campos
		for (int i = 0; i < colArt.length; i++) {
			String c = colArt[i];
			colPArt.put(c, i);
		}
		// algunos tipos que necesitamos

		try {

			ARTICULO_ESTADO_ACTIVO = rr
					.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_ESTADO_ACTIVO);
			TIPO_SIN_REFERENCIA = rr.getTipoPorSigla("sin-referencia");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void grabarDB(Domain d, Session session) throws Exception {
		if (siGraba == true) {
			rr.SESSIONsaveObjectDomain(d, session, "MIGRACION");
		}
	}

	public static Transaction commitDB(long p, Session session, Transaction tx)
			throws Exception {
		if (siGraba == true) {
			if (p % 100 == 0) {
				session.flush();
				session.clear();
			}

			if (p % 1000 == 0) {
				tx.commit();
				tx = null;
			}
		}
		return tx;
	}

	static String getColArt(String col, String[] dato) {
		return getCol(colPArt, col, dato);
	}

	static String getCol(Hashtable<String, Integer> tabla, String col,
			String[] dato) {
		int p = tabla.get(col);
		String out = (dato[p]).trim();
		// quitar las comillas al inicio y al final

		int fin = out.lastIndexOf('\"');
		if (fin == (out.length() - 1)) {
			out = out.substring(0, fin);
		}

		int ini = out.indexOf('\"');
		if (ini == 0) {
			out = out.substring(1);
		}
		return out;
	}

	// ======================================================================
	static Set<String> tipoSigla = new HashSet<>();

	static Tipo getTipo(String idJedi, String tipo) throws Exception {
		Tipo t = rr.getTipoPorSiglaAuxi(tipo, idJedi);
		if (t == null) {
			String ts = idJedi + "" + tipo;
			tipoSigla.add(ts);
			t = TIPO_SIN_REFERENCIA;
		}
		return t;
	}

	// ======================================================================

	static boolean esArticuloVenta(String cod, String idFamilia) {
		
		try {

			char[] arr = cod.toCharArray();
			if (arr[0] == '@') {
				return false;
			}
			if ((arr[0] + arr[1] + "-").compareTo("CT") == 0) {
				return false;
			}
			if ((idFamilia.compareTo("0") == 0)
					|| (idFamilia.compareTo("10") == 0)) {
				// 0=contabilidad, 10=shop
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// =========================================================

	static ProveedorArticulo getProveedorArticulo(String idProveedor,
			String codigoProveedor, String descripcion) throws Exception {
		
		String query = "select p from Proveedor p where p.empresa.codigoEmpresa like '%-"+idProveedor+"-%'";
		Proveedor pro = null;	
		List l =  rr.hql(query);
		if (l.size() == 1){
			 pro = (Proveedor) l.get(0);
		}
		
		ProveedorArticulo proA = null;

		if (pro != null) {
			proA = new ProveedorArticulo();
			proA.setCodigoFabrica(codigoProveedor);
			proA.setDescripcionArticuloProveedor(descripcion);
			proA.setProveedor(pro);
		} else {
			// System.out.println("No se encontró idprov:"+idProveedor +
			// "   codProd:"+ codigoProveedor);
		}
		return proA;
	}

	// =========================================================
	static void recorreArti() throws Exception {

		Session session = rr.SESSIONgetSession();
		Transaction tx = null;

		artNo = new ArrayList<>();

		String ff = articuloCSV;
		File f = new File(ff);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		String linea = entrada.readLine() + " ";
		long p = 0;
		long pReal = 0;

		while (entrada.ready()) {

			if (tx == null) {
				tx = session.beginTransaction();
			}

			linea = entrada.readLine() + " ";
			String[] dato = linea.split("\t");

			String idArticulo = getColArt("IDARTICULO", dato);
			String descripcion = getColArt("DESCRIPCION", dato);
			String idFamilia = getColArt("IDFAMILIA", dato);

			if (esArticuloVenta(idArticulo, idFamilia) == true) {
				pReal++;
				System.out.println(p + ")" + idArticulo + " - " + descripcion);
				Articulo a = new Articulo();

				a.setCodigoInterno(idArticulo);
				a.setDescripcion(descripcion);

				String nroParte = getColArt("PARTE", dato);
				a.setCodigoOriginal(nroParte);

				String codBarra = getColArt("CODIGO_BARRA", dato);
				a.setCodigoBarra(codBarra);

				a.setArticuloEstado(ARTICULO_ESTADO_ACTIVO);

				a.setArticuloFamilia(getTipo(idFamilia,
						Configuracion.SIGLA_ARTICULO_FAMILIA_DEFAULT));

				String idMarca = getColArt("IDMARCA", dato);
				a.setArticuloMarca(getTipo(idMarca,
						Configuracion.SIGLA_ARTICULO_MARCA_DEFAULT));

				String idParte = getColArt("IDPARTE", dato);
				a.setArticuloParte(getTipo(idParte,
						Configuracion.SIGLA_ARTICULO_PARTE_DEFAULT));

				String idLinea = getColArt("IDLINEA", dato);
				a.setArticuloLinea(getTipo(idLinea,
						Configuracion.SIGLA_ARTICULO_LINEA_DEFAULT));

				String codigoProveedor = getColArt("CODIGO_FABRICA", dato);
				String idProveedor = getColArt("IDCOLECCION", dato);
				// Buscar el proveedor
				ProveedorArticulo pa = getProveedorArticulo(idProveedor,
						codigoProveedor, descripcion);
				if (pa != null) {
					grabarDB(pa, session);
					a.getProveedorArticulos().add(pa);
				}

				// **(ToDo información extra) Marca y Modelo
				String idMarcaVehiculo = getColArt("IDMARCA_APLICACION", dato);
				String idModeloVehiculo = getColArt("IDMODELO_APLICACION", dato);
				
				String marcaV = marca.get(idMarcaVehiculo);
				String modeloV = modelo.get(idModeloVehiculo);
				String infoExtra = "MARCA:"+marcaV+" -MODELO:"+modeloV;
				ArticuloInformacionExtra artInfoExtra = new ArticuloInformacionExtra();
				artInfoExtra.setDescripcion(infoExtra);
				grabarDB(artInfoExtra, session);
				
				a.getArticuloInformacionExtras().add(artInfoExtra);

				grabarDB(a, session);

			} else {
				System.out.println("------------------NO--" + p + ")"
						+ idArticulo + " - " + descripcion);
			}

			p++;
			tx = commitDB(p, session, tx);
		}
		if (tx != null) {
			tx.commit();
		}

		rr.SESSIONcloseSession(session);
		entrada.close();
		System.out.println("Total:" + p + "   rales:" + pReal + "    dif:"
				+ (p - pReal));
	}

	// =======================================================
	static void grabaNo() throws Exception {

		String out = "";
		ArrayList<String> ar = artNo;
		for (Iterator iterator = ar.iterator(); iterator.hasNext();) {
			String line = (String) iterator.next();
			out += line + "\n";
		}

		m.grabarStringToArchivo(articuloNOCSV, out);

	}

	public static void main(String[] args) throws Exception {

		// cargarInventario();
		recorreArti();
		System.out.println("===============================");
		for (Iterator iterator = tipoSigla.iterator(); iterator.hasNext();) {
			String d = (String) iterator.next();
			System.out.println(d);
		}
		System.out.println("===============================");

	}
	
	
	public static void xmain(String[] args) throws Exception {
		String idProveedor = "";
		String  codigoProveedor = "";
		String descripcion = ""; 
		ArticuloMigracion a = new ArticuloMigracion();
		ProveedorArticulo pro = a.getProveedorArticulo(idProveedor, codigoProveedor, descripcion);
		
		System.out.println(pro);
		
	}

}
