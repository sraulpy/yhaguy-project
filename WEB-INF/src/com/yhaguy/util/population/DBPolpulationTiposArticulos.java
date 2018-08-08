package com.yhaguy.util.population;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.domain.TipoTipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;

public class DBPolpulationTiposArticulos {

	private RegisterDomain rr = RegisterDomain.getInstance();
	private boolean graba = true;

	private void grabarDB(Domain d) throws Exception {
		if (graba == true) {
			rr.saveObject(d, Configuracion.USER_SYSTEMA);
		}
	}

	private static String[][] marcaCSVArr = { { "-1", "Sin Marca" },
			{ "74", "DAYCO" }, { "2", "AUTOLINEA HUBNER" },
			{ "75", "PRIORITI" }, { "4", "BEPO" }, { "5", "BINS" },
			{ "6", "BOECHAT" }, { "7", "CINPAL" }, { "8", "CIPEC" },
			{ "9", "COFAP" }, { "10", "DANA" }, { "11", "FAG" },
			{ "12", "FRICCION" }, { "13", "FRUM" }, { "14", "GARRET" },
			{ "15", "INA" }, { "16", "INCODIESEL" }, { "17", "IRMA CESTARI" },
			{ "18", "KNORR" }, { "19", "KS" }, { "20", "MAR" },
			{ "21", "MAX GEAR" }, { "22", "MEC-PAR" }, { "23", "RIOSULENSE" },
			{ "24", "RODAFUSO" }, { "25", "RODAROS" }, { "26", "SABO" },
			{ "27", "SACHS" }, { "28", "SIEMENS VDO" }, { "91", "LEMFORDER" },
			{ "30", "VETORE" }, { "31", "WABCO" }, { "32", "NAKATA" },
			{ "33", "SPICER" }, { "34", "TECFIL" }, { "41", "..." },
			{ "1", "..." }, { "37", "SUSUKI" }, { "38", "TOYOTA" },
			{ "39", "VALMET" }, { "3", "..." }, { "36", ".." }, { "42", "VW" },
			{ "40", "..." }, { "76", "UNION REBIT" }, { "43", "..." },
			{ "49", "..." }, { "77", "AMALCABURIO" }, { "78", "EURO-POLO" },
			{ "79", "GENMOT" }, { "80", "AIRTECH" }, { "81", "HONGKONG" },
			{ "82", "FEBI" }, { "83", "NIKIMOTO NGK" },
			{ "84", "IBRAL LEVANTAEJES" }, { "85", "BATISTA" },
			{ "86", "PLATINUM" }, { "87", "URBA BROSOL" }, { "88", "AFFINIA" },
			{ "35", "BATEBOL" }, { "89", "CINA" }, { "90", "SUPORTE REI" },
			{ "92", "URBA" }, { "93", "BROSOL" }, { "94", "UNIVERSAL" },
			{ "95", "UNIFAP" }, { "96", "FALSI & FALSI" }, { "97", "TOTA" },
			{ "98", "CAFIL" }, { "99", "VICTOR REINZ" }, { "100", "MONARCA" },
			{ "101", "POLO" }, { "102", "METALAUTO" }, { "103", "FERVI AIRE" },
			{ "104", "KOBLA" }, { "105", "PERFECT CIRCLE" },
			{ "106", "AIRTECH" }, { "107", "NTI" }, { "108", "THERMOID" },
			{ "109", "COLAR" }, { "110", "TIPH" }, { "111", "PENTIUS" },
			{ "112", "LANSS" }, { "113", "VALVOLINE" }, { "114", "MAHLE" },
			{ "115", "METAL LEVE" }, { "116", "VARGA" }, { "117", "FRASS LE" },
			{ "118", "DRIWAY" }, { "119", "LUK" }, { "120", "COBREQ" },
			{ "121", "GATES" }, { "122", "TRW VARGA" }, { "123", "SUSIN" },
			{ "124", "HERCULES" }, { "125", "DRIVER" },
			{ "126", "MAGNOPECAS" }, { "127", "KL" }, { "128", "MERITOR" },
			{ "129", "BATISTA" }, { "130", "ROFREIOS" }, { "131", "BUXTON" },
			{ "132", "APC" }, { "133", "BMC" }, { "134", "FCS" },
			{ "135", "ORGUS" }, { "136", "DIESEL TECHNIC" },
			{ "137", "WAYSER" }, { "138", "FARJ" }, { "139", "GIR" },
			{ "140", "FANIA" }, { "141", "QUINELATO" }, { "142", "MARQUETTI" },
			{ "143", "LUCIFLEX" }, { "144", "STA CRUZ" }, { "145", "BOSCH" },
			{ "146", "TRW" }, { "147", "PEA" }, { "148", "VOLFFER" },
			{ "149", "RAVEL" }, { "150", "ARCA" }, { "151", "TRUCKLINE" },
			{ "152", "WAHLER" }, { "153", "RDA" }, { "154", "TIMKEN" },
			{ "155", "AMERICAN" }, { "156", "ENGATCAR" }, { "157", "ARCA" },
			{ "158", "SHUNK" }, { "159", "NINO" }, { "160", "GRANERO" },
			{ "161", "HELLA" }, { "162", "RYG" }, { "163", "FLEXIL" },
			{ "164", "ZF" }, { "165", "FRT" }, { "166", "GMB" },
			{ "167", "KSA" }, { "168", "STAHL" }, { "171", "SHADEK" },
			{ "169", "ARGENPART" }, { "170", "RCD" }, { "172", "FLAG" },
			{ "173", "SINASUL" }, { "174", "LINCH" }, { "175", "SPACO" },
			{ "176", "PRADOLUX" }, { "177", "ORION" }, { "178", "SPAAL" },
			{ "179", "BIRKSON" }, { "180", "CONTROIL" }, { "181", "MAXYTIME" },
			{ "182", "WILLTEC" }, { "183", "MARILIA" }, { "184", "IASA" },
			{ "185", "AXEL" }, { "186", "EDNTI" }, { "187", "UDEX" },
			{ "188", "SUPERBRAKE" }, { "189", "SUMITOMO" }, { "196", "VALEO" },
			{ "190", "EURORICAMBI" }, { "191", "ENGRETECNICA" },
			{ "192", "GAVER" }, { "193", "CENTERFLEX" },
			{ "194", "AUTOTRAVI" }, { "195", "FNA" }, { "197", "MEGAFILTER" },
			{ "198", "CVT" }, { "199", "IAM" }, { "200", "GAUE" },
			{ "201", "GRAN SASSO" }, { "202", "BROMER" }, { "203", "ARIELO" },
			{ "204", "INDEBRAS" }, { "205", "FOCCUS" }, { "206", "BOVENAU" },
			{ "207", "UNIFAP" }, { "208", "IMA" }, { "209", "BRASMECK" },
			{ "210", "STEVAUX" }, { "212", "RAYBAN" }, { "211", "BENFLEX" },
			{ "213", "BUXTON" }, { "214", "UETA" }, { "215", "BERGSON" },
			{ "216", "INDISA" }, { "217", "INDIANAPOLIS" },
			{ "218", "VOLTEX" }, { "219", "ROSS" }, { "220", "SAFISA" },
			{ "221", "BORG WAGNER TUBO SYSTEMS" }, { "222", "MASTER POWER" },
			{ "223", "REIBA" }, { "224", "FORJISINTER" },
			{ "225", "NIELSEN BUSSCAR" }, { "226", "ROTA" },
			{ "227", "MONROE" }, { "228", "MAXXIS" }, { "229", "YOKOHAMA" },
			{ "230", "GT" }, { "231", "HANKOOK" }, { "232", "DUNLOP" },
			{ "246", "FATE" }, { "234", "TOYO" }, { "235", "LINGLONG" },
			{ "236", "GOOD YEAR" }, { "237", "FIRESTONE" },
			{ "238", "MICHELIN" }, { "239", "COOPER" }, { "240", "ROADSHINE" },
			{ "241", "DAEWOOD" }, { "242", "NEXEN" }, { "244", "KUMHO" },
			{ "243", "CONTINENTAL" }, { "247", "BRIDGESTONE" },
			{ "245", "PIRELLI" }, { "248", "PLATIN" }, { "249", "HELIAR" },
			{ "250", "RAIDEN" }, { "251", "DUROLINE" }, { "252", "KAHVECI" },
			{ "253", "LUBRAX" }, { "254", "LUBERFINER" },
			{ "255", "MASTERFIL" }, { "256", "TECSERVICE" },
			{ "257", "BUYUKDEMIR" }, { "258", "SPARCO" },
			{ "259", "RACORPARKER" }, { "260", "FLEETGUARD" },
			{ "261", "CHEVRON" }, { "262", "KACMAZLAR" },
			{ "263", "EAGLE ONE" }, { "264", "TANCLICK" }, { "265", "HS" },
			{ "266", "TRAMONTINA" }, { "267", "ROADSTONE" },
			{ "268", "YHAGUY SERVI" }, { "269", "POTENZA" },
			{ "270", "MEYLE" }, { "271", "ROTALA" }, { "272", "FALKEN" },
			{ "273", "DURAVIS" }, { "274", "DERUBIO" }, { "275", "FIRESTONE" },
			{ "276", "DUELER" }, { "277", "ARBEX" }, { "278", "RAYBESTOS" },
			{ "279", "BORLEM" }, { "280", "XBRI" }, { "281", "SUPER 2000" },
			{ "282", "KOASTAL" }, { "283", "GENERAL TIRE" }, { "284", "BARUM" } };

	private static String[][] parteCSAr = { { "-1", "Sin Grupo" },
			{ "1", "MOTOR" }, { "2", "CAJA" }, { "3", "DIFERENCIAL" },
			{ "4", "ACCESORIOS" }, { "5", "BI" }, { "6", "CA" },
			{ "7", "CAJA ZF" }, { "8", "CARDAN" }, { "9", "CATR" },
			{ "10", "CA¥O" }, { "11", "CHASIS" }, { "12", "CHAyPIN" },
			{ "13", "CRR" }, { "14", "CUBAUTO" }, { "15", "CUBCAM" },
			{ "16", "CUBCTA" }, { "17", "CUBLIV" }, { "18", "CYD" },
			{ "19", "CYE" }, { "20", "DIF" }, { "21", "DIRECCION" },
			{ "22", "DISPLA" }, { "23", "EJE" }, { "24", "ELE" },
			{ "25", "EMBRAGUES" }, { "26", "ESC" }, { "27", "FARO" },
			{ "28", "FERRETERIA" }, { "29", "FIL" }, { "30", "FILT" },
			{ "31", "FRENOS" }, { "32", "GOMERIA" }, { "33", "H" },
			{ "34", "INTE" }, { "35", "INYEC" }, { "36", "JUNTAS" },
			{ "37", "LUBRI" }, { "38", "LYA" }, { "39", "LYO" },
			{ "40", "MB" }, { "41", "MO" }, { "42", "MOT" }, { "43", "MOTOR" },
			{ "44", "MYF" }, { "45", "OTROS" }, { "46", "PE" },
			{ "47", "PULI" }, { "48", "RUEDA" }, { "49", "SE" },
			{ "50", "SUSPENSION" }, { "51", "SYD" }, { "52", "TAPA" },
			{ "53", "TC" }, { "54", "TORN" }, { "55", "TUER" },
			{ "56", "VALV" }, { "57", "VAR" }, { "58", "VYF" },
			{ "59", "WYNNPROF" }, { "60", "WYNNSCONS" }, { "61", "Z" },
			{ "62", "Automovil" }, { "63", "Camioneta" }, { "64", "Camiones" },
			{ "65", "SERVICIOS" }, { "66", "COMBUSTIBLE" }, { "67", "MASA" },
			{ "68", "VARIOS" }, { "69", "CABINA" }, { "70", "CARRETA" } };

	private static String[][] lineaCSVAr = { { "-1", "Sin linea" },
			{ "2", "PESADA" }, { "3", "NAVIERA" }, { "7", "PROMO" },
			{ "20", "CONSTRUCTORA" }, { "23", "LIVIANA" },
			{ "24", "AGRICOLA" }, { "28", "OTROS" }, { "29", "PESADA" } };

	private static String[][] familiaCSVAr = { { "-1", "SIN FAMILIA" },
			{ "0", "CONTABILIDAD" }, { "1", "CUBIERTAS" }, { "2", "FILTROS" },
			{ "3", "BATERIAS" }, { "4", "LUBRICANTES" }, { "5", "REPUESTOS" },
			{ "6", "MARKETING" }, { "7", "SERVICIOS" },
			{ "8", "PROMO VALVOLINE" }, { "9", "OTROS" },
			{ "10", "RETAIL SHOP" } };

	private void cargaTipos(String tipoTipo, String sigla, String[][] datos)
			throws Exception {

		// Tipo Tipo
		TipoTipo tt = new TipoTipo();
		tt.setDescripcion(tipoTipo);
		grabarDB(tt);

		for (int i = 0; i < datos.length; i++) {
			// Tipo
			String id = datos[i][0];
			String des = datos[i][1];

			Tipo t = new Tipo();
			t.setSigla(sigla);
			t.setAuxi(id);
			t.setDescripcion(des);
			t.setTipoTipo(tt);
			grabarDB(t);
		}

	}

	public void cargaTipoArticulos() throws Exception {

		// Marcas
		this.cargaTipos(Configuracion.ID_TIPO_ARTICULO_MARCA,
				Configuracion.SIGLA_ARTICULO_MARCA_DEFAULT, marcaCSVArr);

		// linea
		this.cargaTipos(Configuracion.ID_TIPO_ARTICULO_LINEA,
				Configuracion.SIGLA_ARTICULO_LINEA_DEFAULT, lineaCSVAr);

		// familia
		this.cargaTipos(Configuracion.ID_TIPO_ARTICULO_FAMILIA,
				Configuracion.SIGLA_ARTICULO_FAMILIA_DEFAULT, familiaCSVAr);

		// parte
		this.cargaTipos(Configuracion.ID_TIPO_ARTICULO_PARTE,
				Configuracion.SIGLA_ARTICULO_PARTE_DEFAULT, parteCSAr);
	}

	public static void main(String[] args) throws Exception {
		DBPolpulationTiposArticulos tt = new DBPolpulationTiposArticulos();
		tt.cargaTipoArticulos();
	}

}
