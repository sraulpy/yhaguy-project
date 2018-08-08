package com.yhaguy.util.comision;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import com.coreweb.extras.csv.CSV;
import com.coreweb.util.Misc;

public class Cons {

	/*
	 * SQL usado para sacar la info del mes ***** OJO: Son 2 sql, uno para las
	 * cobranzas, y otro que suma las ventas del mes que NO estan cobradas,
	 * ayuda para obtener la meta.
	 * 
	 * 
	 * PASOS 1. Ejecutar los 2 (dos) sql (ventas y cobranzas) -----[no]. 
	 * 2. (deprecated) Armar 1 cvs con los 2 sql 
	 * 3. LLevar a LibreOffice, separador TAB 
	 * 4. Poner el campo Mes arriba 
	 * 5. Sumar los campos numericos 
	 * 6. Guardar como ods 
	 * 7. exportar a cvs 
	 * 7. Editar cvs, quitar los ", 00:00:00.000" por nada 
	 * 8. Quitar compos [null] por 0
	 * 9. Quitar las Ñ por N y puede que algún ñ en caracater raro
	 * 10. ES S.A. (1225) para Sixto. Lo que hago es de las cobranzas, quitar todos los
	 *    movimientos de ES S.A. y poner como Usuario y Vendedor a Sixto Sanches (9-SSHANCHEZ)
	 */

	/*
	 * **************** SQL con TODO 01 cobranzas ********************* esto son
	 * las cobranzas solamente (primer SQL)
	 * 
	  SELECT a.IDRECIBO, a.IDTIPORECIBO, a.NRORECIBO, c.IDTIPOMOVIMIENTO,
	 a.FECHA as FECHA_RECIBO, c.FECHA as FECHA_MOV, a.TOTAL as TOTAL_RECIBO,
	  b.MONTO as MONTO_DETALLE_RECIBO, c.DEBE, d.GRAVADA, d.PORC_DESCUENTO,
	  d.PRECIO_IVA, d.CANTIDAD, a.ESTADO, a.FECHA_CARGA, d.IDMOVIMIENTO,
	  c.NROMOVIMIENTO, c.IDVENDEDOR, g.APELLIDO, g.NOMBRE, c.IDUSER,
	  d.IDARTICULO, SUBSTR(e.DESCRIPCION , 0 , 20) , e.IDCOLECCION as IDPROVEEDOR,
	  f.DESCRIPCION as DESCRIPCION_PROVEEDOR, "" as APELLIDO_USER, "" as
	  NOMBRE_USER, h.IDPERSONA, SUBSTR(h.PERSONA , 0 , 20) as PERSONA, e.IDMARCA, m.DESCRIPCION as DESCRIPCION_MARCA
	  
	  FROM (((((((RECIBOS a join DETALLERECIBO b on a.IDRECIBO = b.IDRECIBO)
	  join MOVIMIENTOS c on b.IDMOVIMIENTO = c.IDMOVIMIENTO) join
	  DETALLEMOVIMIENTO d on c.IDMOVIMIENTO = d.IDMOVIMIENTO) join ARTICULO e
	  on e.IDARTICULO = d.IDARTICULO) join COLECCION f on f.IDCOLECCION =
	  e.IDCOLECCION) join VENDEDOR g on g.IDVENDEDOR = c.IDVENDEDOR) join
	  PERSONA h on h.IDPERSONA = c.IDPERSONA) join Marca m on m.IDMARCA = e.IDMARCA
	 

	  
	 where (a.FECHA >= '10/01/2015') and (a.FECHA <= '10/31/2015') and
	  (a.IDTIPORECIBO in (0,1,10)) and (a.ESTADO = 'E') order by a.IDRECIBO,
	  c.NROMOVIMIENTO

	 * 
	 * 
	 * ****************************************************
	 * 
	 * ============= solo ventas =================
	 * 
	  SELECT 0 as IDRECIBO, 0 as IDTIPORECIBO, 0 as NRORECIBO,
	  c.IDTIPOMOVIMIENTO, 0 as FECHA_RECIBO, c.FECHA as FECHA_MOV, 0 as
	  TOTAL_RECIBO, 0 as MONTO_DETALLE_RECIBO, c.DEBE, d.GRAVADA,
	  d.PORC_DESCUENTO, d.PRECIO_IVA, d.CANTIDAD, 0 as ESTADO, 0 as
	  FECHA_CARGA, d.IDMOVIMIENTO, c.NROMOVIMIENTO, c.IDVENDEDOR, g.APELLIDO,
	  g.NOMBRE, c.IDUSER, d.IDARTICULO, SUBSTR(e.DESCRIPCION , 0 , 20) ,
	  e.IDCOLECCION as IDPROVEEDOR, f.DESCRIPCION as DESCRIPCION_PROVEEDOR, "" as
	  APELLIDO_USER, "" as NOMBRE_USER, h.IDPERSONA, SUBSTR(h.PERSONA , 0 , 20)
	  as PERSONA, e.IDMARCA as IDMARCA, m.DESCRIPCION as DESCRIPCION_MARCA
	  
	  FROM ((((( MOVIMIENTOS c join DETALLEMOVIMIENTO d on c.IDMOVIMIENTO =
	  d.IDMOVIMIENTO) join ARTICULO e on e.IDARTICULO = d.IDARTICULO) join
	  COLECCION f on f.IDCOLECCION = e.IDCOLECCION) join VENDEDOR g on
	  g.IDVENDEDOR = c.IDVENDEDOR) join PERSONA h on h.IDPERSONA = c.IDPERSONA) join MARCA m on m.IDMARCA = e.IDMARCA
	  
	  
	 where (c.FECHA >= '10/01/2015') and (c.FECHA <= '10/31/2015') and
	  (c.idtipomovimiento = 43 or c.idtipomovimiento = 13 or c.idtipomovimiento
	  = 44) and (c.estado = "F") order by c.NROMOVIMIENTO
	  ==============================================================
	 */

	public static String MES_CORRIENTE = "10.2015";
	public static String MES_STR = "2015-10-octubre"; // OJO poner las ventas de ES.S.A. cómo hechas por SIXTO, así se calculan
	
	
	public static boolean DEBUG = false;
	public static boolean SI_VENTAS = false;

	public static String direBase = "/home/daniel/datos/yhaguy/comisiones/";
	
	/* archivo de ventas del mes */
	public static String direOut = direBase
			+ MES_STR + "/";
	
	public static String direFileResumen = direOut + "resumen.txt";
	public static String direFileError = direOut + "error.txt";

	public static String fileVentas = direOut + "datos" + "/l-ventas.csv";
	public static String fileCobranzas = direOut + "datos" + "/l-cobranzas.csv";

	public static String FECHA_EJECUCION = "Generado: "
			+ (new Misc()).getFechaHoyDetalle() + " \n ------------------- \n";

	public static int EXTERNO = 0;
	public static int MOSTRADOR = 1;
	public static int AUXILIAR = 2;
	public static int LOS_CRESPI = 3;

	public static String idPorcDefault = "otraProveedor";

	public static int ID_CONTADO = 43;
	public static int ID_CREDITO = 44;
	public static int ID_NDC = 13;

	public static StringBuffer LOGs = new StringBuffer();
	public static StringBuffer OUT = new StringBuffer();

	public static void error(String linea) {
		LOGs.append(linea + "\n");
	}

	public static void print(String linea) {
		OUT.append(linea + "\n");
	}

	public static double[][] porDevolucion = { { 0.007, 0.003 },
			{ 0.02, 0.0015 }, { 0.04, 0.0005 } };

	/*******************************************************************/
	// para saber los usuarios de los vendedores Externos, de esa forma sabemos
	// si es una venta compartida o no
	public static Object[][] usuarioVendedor = { { EXTERNO, "otro-e", "1" },
			{ EXTERNO, "JAGUILERA", "3" }, { EXTERNO, "EALEGRE", "2" },
			{ EXTERNO, "RAMARILLA", "4" }, { MOSTRADOR, "MANOLO", "5" },
			{ EXTERNO, "CAYALA", "6" }, 
			{ MOSTRADOR, "RBRITOS", "73" },
			{ EXTERNO, "RCESPEDES", "77" },
			{ MOSTRADOR, "NCRESPI", "61" }, { MOSTRADOR, "JDUARTE", "20" },
			{ MOSTRADOR, "HFERNANDEZ", "29" }, { EXTERNO, "DFERREIRA", "27" },
			{ EXTERNO, "GNUNEZ", "12" }, { MOSTRADOR, "DGAONA", "54" },
			 { EXTERNO, "AGOMEZ", "67" },
			{ EXTERNO, "FGRANADA", "72" }, 
			{ EXTERNO, "LLEGUIZAMO", "62" }, { EXTERNO, "FLEON", "70" },
			{ EXTERNO, "otro-ea", "60" }, { EXTERNO, "JGIMENEZ", "23" },
			{ EXTERNO, "JOSORIO", "71" }, { MOSTRADOR, "MOTAZU", "22" },
			{ EXTERNO, "LRISSO", "25" }, { EXTERNO, "ARODRIGUEZ", "78" },
			{ MOSTRADOR, "SSANCHEZ", "9" }, // ANTES ERA EXTERNO
			{ MOSTRADOR, "CSANTACRUZ", "13" },{ EXTERNO, "DLOPEZ", "56" },
			{ EXTERNO, "ASUGASTI", "24" }, { EXTERNO, "MURDAPILLE", "28" },
			{ EXTERNO, "EVAZQUEZ", "49" }, 
			{ EXTERNO, "-sin-usuario-", "136" },
			
			 { MOSTRADOR, "SCUEVAS", "8" },
			{ MOSTRADOR, "RJGONZALEZ", "57" }, { MOSTRADOR, "LMACIEL", "15" },
			{ MOSTRADOR, "JMAIDANA", "34" }, { MOSTRADOR, "otro-eb", "64" },
			{ MOSTRADOR, "JRIOS", "37" },
			{ MOSTRADOR, "CVALDEZ", "14" },
			{ MOSTRADOR, "DMARTINEZ", "113" },
			// { AUXILIAR, "MESTECHE", "52" },
			{ MOSTRADOR, "MESTECHE", "52" },  { MOSTRADOR, "PROLON", "99" },
			{ MOSTRADOR, "DAGUILAR", "-1" },{ MOSTRADOR, "AMENDOZA", "94" },
			 { EXTERNO, "CORTIZ", "31" },
			{ MOSTRADOR, "GBENITEZ", "121" } ,
			{ MOSTRADOR, "otro-q", "92" }, // Sixto Sanchez Temporal Mostrador
			{ MOSTRADOR, "FMATTESICH", "132" } ,
			{ MOSTRADOR, "LGONCALVES", "otro-lg" } ,

			// Los Crespi
			{ LOS_CRESPI, "otro-be", "117" } , // Matto Arturo
			{ LOS_CRESPI, "OCARBALLO", "135" } , // Matto Arturo
			{ LOS_CRESPI, "HALVAREZ", "79" }, // Hernán Alvarez
			{ LOS_CRESPI, "otro-ce", "101" } ,  // Sanchez Higinio
			{ LOS_CRESPI, "HGIMENEZ", "81" }, // Hugo Gimenez
			{ MOSTRADOR, "JVELAZQUEZ", "55" }, // Jorge velazquez
			{ LOS_CRESPI, "LVERA", "76" }, // Librada vera
			{ LOS_CRESPI, "LBENITEZ", "69" }, // Luis Benitez
			{ LOS_CRESPI, "RFLORENTIN", "102" }, // Rolando Florentin
			{ LOS_CRESPI, "otro-de", "138" } , // Ramos Luis
			{ LOS_CRESPI, "otro-ee", "141" } , // Romero Nidia

			{ MOSTRADOR, "otro-ae", "116" } , // Miguel Acosta			

			{ MOSTRADOR, "PJARA", "68" }, // Pablo Jara

	//		{ MOSTRADOR, "OCARBALLO", "135" }, // OSCAR CARBALLO
			{ MOSTRADOR, "AMAGUILERA", "133" }, // ADRIAN MARTIN AGUILERA
			
		
			
	/*
	 * { AUXILIAR, "PROLON", "id-most" }, { AUXILIAR, "RGIMENEZ", "18" }, {
	 * AUXILIAR, "LMOREL", "7" }, { AUXILIAR, "LLOPEZ", "80" }, { AUXILIAR,
	 * "DLOPEZ", "56" }, { AUXILIAR, "MEZARATE", "id-most" }, { AUXILIAR,
	 * "FZARACHO", "id-most" }, { AUXILIAR, "DGOLDARAZ", "id-most" }
	 */
	};



	
	
	public static Object[][] auxiliaresTemporales = {
			// dato de alta a pedido de Raquel
			{ "OVERA4", "id-most", new Rango("1900.01.01", "2020.12.31") },
		
			{ "JMAUGER", "ost", new Rango("2015.05.01", "2012.12.31")},
			
			{ "HALVAREZ", "79", new Rango("1900.01.01", "2012.12.31") },

			{ "PROLON", "99", new Rango("1900.01.01", "2013.05.31") },
			
			{ "RGIMENEZ", "18", new Rango("1900.01.01", "2020.12.31") },
			{ "RGIMENEZ6", "id-most", new Rango("1900.01.01", "2020.12.31") },
			
			{ "LMOREL", "7", new Rango("1900.01.01", "2020.12.31") },
			{ "LMOREL6", "id-most", new Rango("1900.01.01", "2020.12.31") },
			
			{ "LLOPEZ", "80", new Rango("1900.01.01", "2020.12.31") },
			{ "DLOPEZ", "56", new Rango("1900.01.01", "2013.07.31") },

			{ "MEZARATE", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "MEZARATE6", "id-most", new Rango("1900.01.01", "2020.12.31") },
			
			{ "FZARACHO", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "DGOLDARAZ", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "DBOSCHERT", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "DBOSCHER", "id-most", new Rango("1900.01.01", "2020.12.31") },
			

			{ "POJEDACEN", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "POJEDA", "65", new Rango("1900.01.01", "2020.12.31") },
			{ "POJEDA5", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "POJEDA3", "id-most", new Rango("1900.01.01", "2020.12.31") },
			
			{ "LCIBILS", "95", new Rango("1900.01.01", "2020.12.31") },
			{ "GORTIZ", "96", new Rango("1900.01.01", "2020.12.31") },
			{ "VRIVEROS", "id-most", new Rango("2013.04.01", "2020.12.31") },
			{ "OESPINOLA", "97", new Rango("1900.01.01", "2020.12.31") },
			{ "ABOGADO", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "ABOGADOFDO", "id-most", new Rango("1900.01.01", "2020.12.31") },
			
			{ "LPORTILLO", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "LPORTILLO5", "id-most", new Rango("1900.01.01", "2020.12.31") },
			{ "LPORTILLO6", "id-most", new Rango("1900.01.01", "2020.12.31") },
			
			{ "ADURE", "id-most", new Rango("1900.01.01", "2020.12.31") },
			
			{ "VGONZALEZ", "id-most", new Rango("1900.01.01", "2020.12.31") },
			
			
			{ "NCRESPI", "61", new Rango("2013.08.01", "2020.12.31") },
			{ "MANOLO", "5", new Rango("2013.10.01", "2020.12.31") },
			{ "SCUEVAS", "8", new Rango("2013.10.01", "2020.12.31") },
			{ "CSANTACRUZ", "13", new Rango("2013.10.01", "2020.12.31") },

			{ "EORTELLA4", "id-most", new Rango("2013.12.01", "2020.12.31") },
			{ "EORTELLA5", "id-most", new Rango("2013.12.01", "2020.12.31") },
			{ "EORTELLA6", "id-most", new Rango("2013.12.01", "2020.12.31") },
			
			{ "HINSFRAN", "118", new Rango("2014.01.01", "2020.12.31") },
			// Natalia me pidio
			{ "PROLON", "99", new Rango("2014.01.01", "2020.12.31") },
			{ "DGAONA", "54", new Rango("2014.01.01", "2020.12.31") },
		
			
			
	};

	/******************** METAS ***********************************/
	public static int indiceMetaMinima = 0;
	public static int indiceMetaMaxima = 9;

	public static double m = 1000000;

	public static String meta0 = "meta0";
	public static double[] metaV0 = { 1 * m, 120 * m, 125 * m, 130 * m, 135 * m,	140 * m, 145 * m, 150 * m, 160 * m, 180 * m};

	public static String metaCO = "metaCO";
	public static double[] metaCOv = { 1 * m, 410 * m , 420 * m , 430 * m , 440 * m , 450 * m , 460 * m , 470 * m , 480 * m , 500 * m };

	public static String metaDF = "metaDF";
	public static double[] metaDFv = { 1 * m, 210 * m , 220 * m , 230 * m , 240 * m , 250 * m , 260 * m , 270 * m , 280 * m , 300 * m };
	
	public static String metaGN = "metaGN";
	public static double[] metaGNv = { 1 * m, 360 * m , 370 * m , 380 * m , 390 * m , 405 * m , 425 * m , 460 * m , 480 * m , 500 * m };
	
	public static String metaLR = "metaLR";
	public static double[] metaLRv  ={ 1 * m, 340 * m , 345 * m , 350 * m , 355 * m , 360 * m , 365 * m , 370 * m , 375 * m , 380 * m  };
	
	public static String metaSS = "metaSS";
//	public static double[] metaSSv = {350 * m , 380 * m , 400 * m , 420 * m , 440 * m , 460 * m , 475 * m , 485 * m , 500 * m , 500 * m };
	public static double[] metaSSv = { 1 * m, 450 * m , 475 * m , 485 * m , 500 * m , 520 * m , 540 * m , 560 * m , 580 * m , 600 * m  };
	
	
	public static String metaDefault = "metaDefault";
	public static double[] metaVD = {  1 * m, 210 * m, 215 * m, 220 * m, 225 * m, 230 * m, 235 * m, 240 * m, 255 * m, 275 * m};

	
	
	
	
	public static String[][] idVendedorPorMeta = { 
		{ "31", metaSS }, 
		{ "12", metaCO },
		{ "25", metaGN }, 
		{ "27", metaLR },
		{ "136", metaDF },
//		{ "9", metaSS },
		};

	/* Metas antes de los cambios del 6 de Marzo
		{ "12", metaGN }, 
		{ "25", metaLR },
		{ "27", metaDF }, 
		{ "31", metaCO },
		{ "9", metaSS },
	
	*/
	
	/***********************************************************************/

	public static String CONTADO = "CTO";
	public static String COBRANZA = "CRE";

	public static double PORC_MOSTRADOR_CONTADO_def = 0.016;
	public static double PORC_MOSTRADOR_COBRANZA_def = 0.012;
	public static double PORC_EXTERNO_COBRANZA = 0.012;

	public static Object[][] idVendedorMostradorPorcentaje = {
			{ "54", 0.012, 0.012 }, // Dahiana Gaona
			{ "73", 0.015, 0.013 }, // Richard Britos
			{ "99", 0.02, 0.02 }, // Pamela Rolon
			{ "22", 0.025, 0.025 }, // Miguel Otazu
			{ "20", 0.025, 0.025 }, // Julio Duarte
			{ "29", 0.025, 0.025 }, // Hugo Fernandez
			{ "94", 0.02, 0.018 }, // Alverto Mendoza
			{ "68", 0.025, 0.025 }, // Pablo Jara
			{ "116", 0.025, 0.025 }, // Miguel Acosta
			
//			{ "135", 0.025, 0.025 }, // OSCAR CARBALLO
			{ "133", 0.025, 0.025 }, // ADRIAN MARTIN AGUILERA
			
			{ "9", 0.025, 0.025 }, // SIXTO SANCHEZ
			{ "55", 0.0050, 0.0050 }, // JORGE VAZQUEZ (0.50% para todas las ventas)


			// Esto se carga para que siiga el curso, pero NO se usa, 
			// por eso le pongo cero			
			// Los Crespi
			/* aca
			{ "116", 0.0, 0.0 }, // Miguel Acosta
			{ "102", 0.0, 0.0 }, // Rolando Florentin
			{ "79", 0.0, 0.0 }, // Hernan Alvarez
			{ "81", 0.0, 0.0 }, // Hugo Gimenez
			{ "117", 0.0, 0.0 }, // Matto Arturo
			{ "101", 0.0, 0.0 }, // Sanches Higinio
			{ "55", 0.0, 0.0 }, // Jorge Velazquez
			{ "76", 0.0, 0.0 }, // Librada Vera
			{ "69", 0.0, 0.0 }, // Luis Benitez
			{ "68", 0.0, 0.0 }, // Pablo Jara
			*/
	};
	

	// Los Crespi, Id de Vendedor y el Id de las Metas x Marcas
	public static Object[][] idVendedorLosCrespi = {
	//	{ "116", 1 }, // Miguel Acosta
		{ "102", 1 }, // Rolando Florentin
		{ "79", 1 }, // Hernan Alvarez
		{ "81", 1 }, // Hugo Gimenez
		{ "117", 1}, // Matto Arturo
		{ "135", 1}, // Oscar Carballo
		
		{ "101", 1 }, // Sanches Higinio
//		paso a venderores del mostrador para poder ponerle una comisión fija para todo lo demas
//		{ "55", 1 }, // Jorge Velazquez
		{ "76", 1 }, // Librada Vera
		{ "69", 1}, // Luis Benitez
		{ "141", 1}, // Romero Nidia
		{ "138", 1}, // Ramos Luis
		//{ "68", 1 }, // Pablo Jara
};
	
	
	// comisión especial de algunos vendedores con respecto a proveedores
	public static Object [][] idcomisionEspecialPorVendedorProveedor = {
		{"55-10219", 0.0091, 0.0078, "Jorge Velazquez, BATEBOL LTDA.INDUSTRIA DE BATERIAS"},
		{"55-57088", 0.0180, 0.0160, "Jorge Velazquez, JOHNSON CONTROLS"},
		{"55-86299", 0.0230, 0.0200, "Jorge Velazquez, JOHNSON CONTROLS COLOMBIA S.A.S"},
		{"55-10501", 0.0100, 0.0100, "Jorge Velazquez, PUMA ENERGY PARAGUAY"},
		// esto ya está en marcas con porcentaje especial
		//		{"55-xx", 0.0050, 0.0050, "Jorge Velazquez, CASTROL"},
		{"TD-10501", 0.0100, 0.0100, "Para todos, PUMA ENERGY PARAGUAY"},
	};
	
	public static Hashtable<String, Double[]> comisionEspecialPorVendedorProveedor = new Hashtable<>();
	public static void cargaComisionEspecialVendedorProveedor(){
		for (int i = 0; i < idcomisionEspecialPorVendedorProveedor.length; i++) {
			Object[] d = idcomisionEspecialPorVendedorProveedor[i];
			Double[] porComi = {(Double) d[1], (Double) d[2]};
			comisionEspecialPorVendedorProveedor.put((String) d[0], porComi);			
		}
	}
	
	public static String textoComisionEspecialPorVendedorProveedor(){
		String out = "\n\n";
		out += "Comisión especial de Vendedores por Proveedor\n";
		out += "--------------------------------------------------------------------\n";
		for (int i = 0; i < idcomisionEspecialPorVendedorProveedor.length; i++) {
			Object[] d = idcomisionEspecialPorVendedorProveedor[i];
			String ctdo = misc.formatoDs(((double)d[1])*100, 8, false)+"%";
			String cred = misc.formatoDs(((double)d[2])*100, 8, false)+"%";
			String nomb = misc.formato("["+d[0]+"] "+d[3], 50, true);
			out+= nomb + ctdo + cred+"\n";
		}
		out += "--------------------------------------------------------------------\n";
		

		
		return out;
	}
	
	
	// vendedores sobre los cuales se debe considerar venta asignada
	// vendedores compañeros 

	public static Object[][] idMarcasVendedorAsignado = {
		
	};

	public static Object[][] xxxxxidMarcasVendedorAsignado = {
        {"250", "RAIDEN", "79", new String[]{"73","102"} }, // Raiden - Hernan Alvarez, comparte con Rolando
        {"249", "HELIAR", "79", new String[]{"73","102"} }, // Heliar - Hernan Alvarez, comparte con Rolando
        
        {"234", "TOYO", "102", new String[]{"79", "73"} }, // Toyo - Rolando Britos, comparte con Hernan y Richard
        {"248", "PLATIN", "102", new String[]{"79", "73"} },  // Platin - Richard Britos, comparte con Hernan y Richard
	};


		
	public static Hashtable<String, Integer> idVendedorLosCrespiTable = new Hashtable<String, Integer>();

	public static Hashtable<String, Double> idVendedorMostradorPorcentajeTable = new Hashtable<String, Double>();

	public static void cargaVenMostradorPorcentaje() {
		for (int i = 0; i < idVendedorMostradorPorcentaje.length; i++) {
			String idV = (String) idVendedorMostradorPorcentaje[i][0];
			Double cto = (Double) idVendedorMostradorPorcentaje[i][1];
			Double cre = (Double) idVendedorMostradorPorcentaje[i][2];

			idVendedorMostradorPorcentajeTable.put(idV + CONTADO, cto);
			idVendedorMostradorPorcentajeTable.put(idV + COBRANZA, cre);
		}
		// cargo dos valores por default
		idVendedorMostradorPorcentajeTable.put(CONTADO,
				PORC_MOSTRADOR_CONTADO_def);
		idVendedorMostradorPorcentajeTable.put(COBRANZA,
				PORC_MOSTRADOR_COBRANZA_def);
		
		
		// Carga los Crespi
		for (int i = 0; i < idVendedorLosCrespi.length ; i++) {
			String idV = (String) idVendedorLosCrespi[i][0];
			Integer meta = (Integer) idVendedorLosCrespi[i][1];
			idVendedorLosCrespiTable.put(idV, meta);
		}
		
		
		
	}

	public static double getPorcMostrador(String idV, String tipo) {
		Double d = idVendedorMostradorPorcentajeTable.get(idV + tipo);
		if (d == null) {
			d = idVendedorMostradorPorcentajeTable.get(tipo);
		}
		return d.doubleValue();
	}

	/***********************************************************************/

	public static String[][] clientesNoConsiderados = {
			{ "54481", "Arandu Repuestos" },
			{ "57562", "Katupyry Repuestos S.A." } };


	public static boolean esClienteNoConsiderado(String idPersona) {
		boolean out = false;
		for (int i = 0; i < clientesNoConsiderados.length; i++) {
			String id = clientesNoConsiderados[i][0];
			if (id.compareTo(idPersona) == 0) {
				out = true;
			}
		}
		return out;
	}

	// Id cliente, Cliente, porcentaje, true: NO compartida, false: según lo que corresponda
	public static Object[][] porcentajePorCliente = {
			{ "4783", "Central Repuestos S.A.", 0.005, true },
			{ "3123", "Brianza S.A.", 0.005, true }, 
			{ "23", "Todo Mercedes", 0.005, true } ,
			{ "5826", "Tractopar", 0.01, false}, 
		// 	{ "2618", "Neumatec", 0.01, false} 
			};

	public static double getPorcentajeClienteEspecial(String idPersona) {
		double out = -1;
		for (int i = 0; i < porcentajePorCliente.length; i++) {
			String id = (String) porcentajePorCliente[i][0];
			if (id.compareTo(idPersona) == 0) {
				out = (double) porcentajePorCliente[i][2];
			}
		}
		return out;
	}
	
	
	public static boolean getPorcentajeClienteEspecialNOCompartida(String idPersona) {
		boolean out = false; // por defecto es según corresponda
		for (int i = 0; i < porcentajePorCliente.length; i++) {
			String id = (String) porcentajePorCliente[i][0];
			if (id.compareTo(idPersona) == 0) {
				out = (boolean) porcentajePorCliente[i][3];
			}
		}
		return out;
	}
	

	
	// Id marca
	public static Object[][] porcentajePorMarca = {
			{ "308", "Castrol", 0.005 },
			};

	public static double getPorcentajeMarcaEspecial(String idMarca) {
		double out = -1;
		for (int i = 0; i < porcentajePorMarca.length; i++) {
			String id = (String) porcentajePorMarca[i][0];
			if (id.compareTo(idMarca) == 0) {
				out = (double) porcentajePorMarca[i][2];
			}
		}
		return out;
	}

	

	public static String textoClienteNoConsiderados() {
		String out = " \n\n";
		out += "Clientes que NO suman en la comision ni en la venta del mes\n";
		out += "-----------------------------------------------------------\n";
		for (int i = 0; i < clientesNoConsiderados.length; i++) {
			String id = ("[" + clientesNoConsiderados[i][0] + "]          ")
					.substring(0, 10);
			String cl = clientesNoConsiderados[i][1];
			out += id + cl + "\n";
		}
		out += "-----------------------------------------------------------"
				+ "\n";
		return out;
	}

	public static String textoPorcentajeEspecialClientes() {
		Misc m = new Misc();
		String out = " \n\n";
		out += "Clientes con porcentaje especial (TRUE: ES NO COMPARTIDA)"
				+ "\n";
		out += "-----------------------------------------------------------"
				+ "\n";
		for (int i = 0; i < porcentajePorCliente.length; i++) {
			String id = ("[" + porcentajePorCliente[i][0] + "]          ")
					.substring(0, 10);
			String cl = ("" + porcentajePorCliente[i][1] + "                         ")
					.substring(0, 25);
			String por = m.formatoDs(
					((double) porcentajePorCliente[i][2]) * 100, 5, false)
					+ "%";
			String comp = "   "+porcentajePorCliente[i][3]+"";
			
			out += id + cl + por + comp + "\n";
		}
		out += "-----------------------------------------------------------"
				+ "\n";

		return out;
	}

	
	public static String textoPorcentajeEspecialMarca() {
		Misc m = new Misc();
		String out = " \n\n";
		out += "Marcas con porcentaje especial"
				+ "\n";
		out += "-----------------------------------------------------------"
				+ "\n";
		for (int i = 0; i < porcentajePorMarca.length; i++) {
			String id = ("[" + porcentajePorMarca[i][0] + "]          ")
					.substring(0, 10);
			String mk = ("" + porcentajePorMarca[i][1] + "                         ")
					.substring(0, 25);
			String por = m.formatoDs(
					((double) porcentajePorMarca[i][2]) * 100, 5, false)
					+ "%";
				
			out += id + mk + por + "\n";
		}
		out += "-----------------------------------------------------------"
				+ "\n";

		return out;
	}
	
	
	
	public static String textoVendedoresSinComisionXProveedor(){
		
		String out = "\n\n";
		out += "Vendedores que NO cobram comision para ciertas fábricas\n";
		out += "-----------------------------------------------------------\n";
		
		iniPrintColumanas(2);
		addPrintDato(0, "Fabricas que NO pagan comision");
		addPrintDato(0, "------------------------------");
		
		for (int i = 0; i < fabricasNoComision.length; i++) {		
			addPrintDato(0, "("+fabricasNoComision[i][0]+") "+fabricasNoComision[i][1]);
		}
		addPrintDato(0, "------------------------------");


		addPrintDato(1, "Vendedores en este régimen ");
		addPrintDato(1, "------------------------------");
		
		for (int i = 0; i < vendedoresFabricasNoComision.length; i++) {
			String idV = (String) vendedoresFabricasNoComision[i][0];
			String nV = (String) vendedoresFabricasNoComision[i][2];

			String s = "000"+idV.trim();
			s = s.substring(s.length()-3);
			
			addPrintDato(1, "("+s+") - "+nV);
		}
		
		addPrintDato(1, "------------------------------");
		out += printCol(new int[]{30,30}, "|");
		return out;
	}

	/********************* PARA LEER DEL CSV *******************************/

	public static String[][] cab = { { "Mes", CSV.STRING } };

	public static String[][] cabDet = {
			{ "IDMOVIMIENTO", CSV.NUMERICO }, // id
			{ "IDRECIBO", CSV.NUMERICO }, { "IDTIPORECIBO", CSV.NUMERICO },
			{ "NRORECIBO", CSV.STRING }, { "IDTIPOMOVIMIENTO", CSV.NUMERICO },
			{ "FECHA_RECIBO", CSV.STRING }, { "FECHA_MOV", CSV.STRING },
			{ "TOTAL_RECIBO", CSV.NUMERICO },
			{ "MONTO_DETALLE_RECIBO", CSV.NUMERICO }, { "DEBE", CSV.NUMERICO },
			{ "GRAVADA", CSV.NUMERICO }, { "PRECIO_IVA", CSV.NUMERICO },
			{ "CANTIDAD", CSV.NUMERICO }, { "ESTADO", CSV.STRING },
			{ "FECHA_CARGA", CSV.STRING }, { "IDMOVIMIENTO", CSV.NUMERICO },
			{ "NROMOVIMIENTO", CSV.NUMERICO }, { "IDVENDEDOR", CSV.NUMERICO, },
			{ "APELLIDO", CSV.STRING }, { "NOMBRE", CSV.STRING },
			{ "IDUSER", CSV.STRING }, { "APELLIDO_USER", CSV.STRING },
			{ "NOMBRE_USER", CSV.STRING }, { "IDARTICULO", CSV.STRING },
			{ "SUBSTR", CSV.STRING }, { "IDMARCA", CSV.NUMERICO },
			{ "DESCRIPCION_MARCA", CSV.STRING },
			{ "PORC_DESCUENTO", CSV.NUMERICO }, { "IDPERSONA", CSV.NUMERICO },
			{ "PERSONA", CSV.STRING }, { "IDPROVEEDOR", CSV.NUMERICO },
			{ "DESCRIPCION_PROVEEDOR", CSV.STRING } };

	/************** TABLA DE COMPROBANTES, YA PASADOS DESDE EL CSV **********************/

	// poner los campos de los comprobantes
	private static String[] camposComprobante = { "ID", "IDRECIBO",
			"IDTIPORECIBO", "NRORECIBO", "IDTIPOMOVIMIENTO", "FECHA_RECIBO",
			"FECHA_MOV", "TOTAL_RECIBO", "MONTO_DETALLE_RECIBO", "DEBE",
			"GRAVADA", "PRECIO_IVA", "CANTIDAD", "ESTADO", "FECHA_CARGA",
			"IDMOVIMIENTO", "NROMOVIMIENTO", "IDVENDEDOR", "APELLIDO",
			"NOMBRE", "IDUSER", "APELLIDO_USER", "NOMBRE_USER", "IDARTICULO",
			"SUBSTR", "IDPROVEEDOR", "DESCRIPCION_IDPROVEEDOR", "ESCONTADO", "IDPERSONA",
			"PERSONA", "IDMARCA", "DESCRIPCION_MARCA", "IDPROVEEDOR", "DESCRIPCION_PROVEEDOR" };

	public static Tabla ventas = new Tabla(camposComprobante);
	public static Tabla cobranzas = new Tabla(camposComprobante);

	public static void csvtoTable(String file, Tabla table) throws Exception {
		System.out.println("Datos: " + file);
		/*
		 * cargar los comprobantes del CSV o de donde sea
		 */

		String direccion = file;

		// Leer el CSV
		CSV csv = new CSV(cab, cabDet, direccion);

		// Como recorrer el detalle
		csv.start();
		int contador = 0;
		int nroLinea = 0;

		while (csv.hashNext() /* && nroLinea < 50 */) {

			nroLinea++;
			// Lee el detalle de las facturaciones
			double IDRECIBO = csv.getDetalleDouble("IDRECIBO");
			double IDTIPORECIBO = csv.getDetalleDouble("IDTIPORECIBO");
			String NRORECIBO = csv.getDetalleString("NRORECIBO");
			long IDTIPOMOVIMIENTO = (long) csv
					.getDetalleDouble("IDTIPOMOVIMIENTO");
			String FECHA_RECIBO = csv.getDetalleString("FECHA_RECIBO");
			String FECHA_MOV = csv.getDetalleString("FECHA_MOV");
			double TOTAL_RECIBO = csv.getDetalleDouble("TOTAL_RECIBO");
			double MONTO_DETALLE_RECIBO = csv
					.getDetalleDouble("MONTO_DETALLE_RECIBO");
			double DEBE = csv.getDetalleDouble("DEBE");

			double GRAVADA = csv.getDetalleDouble("GRAVADA");
			double POR_DESC = csv.getDetalleDouble("PORC_DESCUENTO");
			// quitando el descuento
			GRAVADA = GRAVADA - GRAVADA * (POR_DESC / 100);

			double PRECIO_IVA = csv.getDetalleDouble("PRECIO_IVA");
			double CANTIDAD = csv.getDetalleDouble("CANTIDAD");
			String ESTADO = csv.getDetalleString("ESTADO");
			String FECHA_CARGA = csv.getDetalleString("FECHA_CARGA");
			double IDMOVIMIENTO = csv.getDetalleDouble("IDMOVIMIENTO");
			String NROMOVIMIENTO = ""
					+ ((long) csv.getDetalleDouble("NROMOVIMIENTO"));
			String IDVENDEDOR = ""
					+ ((long) csv.getDetalleDouble("IDVENDEDOR"));
			String APELLIDO = csv.getDetalleString("APELLIDO");
			String NOMBRE = csv.getDetalleString("NOMBRE");
			String IDUSER = csv.getDetalleString("IDUSER");
			String APELLIDO_USER = csv.getDetalleString("APELLIDO_USER");
			String NOMBRE_USER = csv.getDetalleString("NOMBRE_USER");
			String IDARTICULO = csv.getDetalleString("IDARTICULO");
			String SUBSTR = csv.getDetalleString("SUBSTR");
			String IDPROVEEDOR = "" + ((long) csv.getDetalleDouble("IDPROVEEDOR"));
			String DESCRIPCION_PROVEEDOR = csv
					.getDetalleString("DESCRIPCION_PROVEEDOR");
			String IDPERSONA = "" + ((long) csv.getDetalleDouble("IDPERSONA"));
			String PERSONA = csv.getDetalleString("PERSONA");
			String IDMARCA = "" + ((long) csv.getDetalleDouble("IDMARCA"));
			String DESCRIPCION_MARCA = csv
					.getDetalleString("DESCRIPCION_MARCA");


			boolean esContado = false;

			if (IDTIPOMOVIMIENTO == ID_CONTADO) {
				esContado = true;
			}

			if (IDTIPOMOVIMIENTO == ID_NDC) {
				GRAVADA = GRAVADA * -1;
			}

			if ((IDTIPOMOVIMIENTO == ID_CONTADO
					|| IDTIPOMOVIMIENTO == ID_CREDITO || IDTIPOMOVIMIENTO == ID_NDC)) {

				contador++;
				// Cargarlos detalles en la tabla Comprobante
				table.newRow();
				table.addDataRow("ID", contador);
				table.addDataRow("IDRECIBO", IDRECIBO);
				table.addDataRow("IDTIPORECIBO", IDTIPORECIBO);
				table.addDataRow("NRORECIBO", NRORECIBO);
				table.addDataRow("IDTIPOMOVIMIENTO", IDTIPOMOVIMIENTO);
				table.addDataRow("FECHA_RECIBO", FECHA_RECIBO);
				table.addDataRow("FECHA_MOV", FECHA_MOV);
				table.addDataRow("TOTAL_RECIBO", TOTAL_RECIBO);
				table.addDataRow("MONTO_DETALLE_RECIBO", MONTO_DETALLE_RECIBO);
				table.addDataRow("DEBE", DEBE);
				table.addDataRow("GRAVADA", GRAVADA);
				table.addDataRow("PRECIO_IVA", PRECIO_IVA);
				table.addDataRow("CANTIDAD", CANTIDAD);
				table.addDataRow("ESTADO", ESTADO);
				table.addDataRow("FECHA_CARGA", FECHA_CARGA);
				table.addDataRow("IDMOVIMIENTO", IDMOVIMIENTO);
				table.addDataRow("NROMOVIMIENTO", NROMOVIMIENTO);
				table.addDataRow("IDVENDEDOR", IDVENDEDOR);
				table.addDataRow("APELLIDO", APELLIDO);
				table.addDataRow("NOMBRE", NOMBRE);
				table.addDataRow("IDUSER", IDUSER);
				table.addDataRow("APELLIDO_USER", APELLIDO_USER);
				table.addDataRow("NOMBRE_USER", NOMBRE_USER);
				table.addDataRow("IDARTICULO", IDARTICULO);
				table.addDataRow("SUBSTR", SUBSTR); // Descripcion del
													// Articulo
				table.addDataRow("IDPROVEEDOR", IDPROVEEDOR);
				table.addDataRow("DESCRIPCION_PROVEEDOR", DESCRIPCION_PROVEEDOR);
				table.addDataRow("ESCONTADO", esContado);
				table.addDataRow("IDPERSONA", IDPERSONA);
				table.addDataRow("PERSONA", PERSONA);
				table.addDataRow("IDMARCA", IDMARCA);
				table.addDataRow("DESCRIPCION_MARCA", DESCRIPCION_MARCA);
				table.saveRow();
			} else {
				error("Error IDTIPOMOVIMIENTO:" + IDTIPOMOVIMIENTO
						+ " IDRECIBO:" + IDRECIBO + " IDMOVIMIENTO:"
						+ IDMOVIMIENTO + "" + file);
			}

		}

	}

	/********************* PORCENTAJES DE COMISIONES **********************/

	public static Hashtable<String, Object[]> porcentajePorProveedor = new Hashtable<String, Object[]>();

	
	public static double[] grupoPorcentaje0 = {	0.0132,	0.0134,	0.0135,	0.0136,	0.014,	0.0142,	0.0145,	0.0147,	0.015,	0.0154};
	public static double[] grupoPorcentaje1 = {	0.0155, 0.0158,	0.0159,	0.016,	0.0164,	0.0167,	0.0171,	0.0174,	0.0176,	0.0179};
	public static double[] grupoPorcentaje2 = {	0.0180, 0.0184,	0.0185,	0.0186,	0.0189,	0.0192,	0.0195,	0.0197,	0.0199,	0.02};
	public static double[] grupoPorcentajeOtro = {	0.0123,	0.0123,	0.0123,	0.0123,	0.0123,	0.0123,	0.0123,	0.0123,	0.0123,	0.0123};

	
	
	
	public static void cargaPorcentajesPorProveedor() {

		// (HACER) Traer del CSV los proveedores con sus respectivos porcentajes

		porcentajePorProveedor.put("10303", new Object[] {"10303","ZF DO BRASIL LTDA.SACHS",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("62175", new Object[] {"62175","MAHLE BRASIL",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("10145", new Object[] {"10145","CINPAL",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("10106", new Object[] {"10106","AFFINIA AUTOMOTIVA LTDA.",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("10508", new Object[] {"10508","IDEAL STANDARD WABCO TRAN",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("10116", new Object[] {"10116","SUPORTE REI",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("10482", new Object[] {"10482","ROLAMENTOS FAG LTDA.",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("10506", new Object[] {"10506","SABO INDUS.E COMERCIO DE",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("54300", new Object[] {"54300","ZF SACHS ALEMANIA",grupoPorcentaje0, "Grupo 0"});
		porcentajePorProveedor.put("10122", new Object[] {"10122","FRUM",grupoPorcentaje0, "Grupo 0"});


		porcentajePorProveedor.put("10110", new Object[] {"10110","MAR ROLAMENTOS",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10483", new Object[] {"10483","INSTALADORA SAO MARCOS LT",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10495", new Object[] {"10495","KNORR BREMSE SIST.P/VEIC.",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("54501", new Object[] {"54501","FEBI BILSTEIN ALEMANIA",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("53760", new Object[] {"53760","GARRET",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("56039", new Object[] {"56039","AIRTECH",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10132", new Object[] {"10132","FRICCION S.R.L.",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10117", new Object[] {"10117","VETORE IND.E COMERCIO",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10108", new Object[] {"10108","AUTOLINEA HUBNER",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10199", new Object[] {"10199","TECNICA INDUSTRIAL TIPH S",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10111", new Object[] {"10111","MEC-PAR",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("56520", new Object[] {"56520","HONGKONG WOOD BEST INDUST",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10105", new Object[] {"10105","SIEMENS VDO AUTOMOTIVE",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("60909", new Object[] {"60909","DUROLINE TRADING COMPANY LIMITED",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("53759", new Object[] {"53759","RIOSULENSE",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10115", new Object[] {"10115","AMALCABURIO",grupoPorcentaje1, "Grupo 1"});
		porcentajePorProveedor.put("10268", new Object[] {"10268","MAGNETI MARELLI COFAP AUT",grupoPorcentaje1, "Grupo 1"});


		porcentajePorProveedor.put("56698", new Object[] {"56698","DAYCO ARGENTINA S.A.",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("10114", new Object[] {"10114","RODAROS",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("53534", new Object[] {"53534","CINPAL ARGENTINA",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("10733", new Object[] {"10733","ZF SACHS ARGENTINA S.A.",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("53840", new Object[] {"53840","ESPERANZA-RODAFUSO",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("58944", new Object[] {"58944","AFFINIA URUGUAY",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("10313", new Object[] {"10313","BOECHAT",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("10424", new Object[] {"10424","Biagio Dell'Agll & CIA Ltda.",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("79094", new Object[] {"79094","MAHLE FILTROS BRASIL",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("80103", new Object[] {"80103","MAHLE SINGAPORE",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("79537", new Object[] {"79537","DUROLINE S.A. BRASIL",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("78812", new Object[] {"78812","MAXION  PRIORITY",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("57937", new Object[] {"57937","GENMOT GENEL MOTOR STANDA",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("53374", new Object[] {"53374","UNIONREBIT",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("80263", new Object[] {"80263","RELEMIX",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("82756", new Object[] {"82756","Valeo",grupoPorcentaje2, "Grupo 2"});
		porcentajePorProveedor.put("10303", new Object[] {"81674","GRUPO ELITE S.A",grupoPorcentaje2, "Grupo 2"});
		
		porcentajePorProveedor.put(idPorcDefault, new Object[] { idPorcDefault,
				"otras", grupoPorcentajeOtro, "Grupo Default" });

	}

	public static double calculoDevolucion(double totalVenta,
			double porDevolucion) {
		double out = 0.0;
		double intervalo1, intervalo2;
		// 0.0% <= porDevolucion < 0.7% ......0.3%
		// 0.7% <= porDevolucion < 2% ......0.15%
		// 2% <= porDevolucion < 4% ......0.05%

		intervalo1 = 0;
		for (int j = 0; j < Cons.porDevolucion.length; j++) {
			intervalo2 = Cons.porDevolucion[j][0];
			if (porDevolucion >= intervalo1 && porDevolucion < intervalo2) {
				out = totalVenta * Cons.porDevolucion[j][1];
			}
			intervalo1 = Cons.porDevolucion[j][0];
		}

		return out;
	}

	public static String textoPorDevolucion() {
		Misc m = new Misc();
		NumberFormat fp = new DecimalFormat("##0.00 %");
		String out = "";
		out += "Plus por devolucion\n";
		out += "----------------------------------------\n";
		double desde = 0;
		double hasta = 0;
		double por = 0;
		for (int i = 0; i < Cons.porDevolucion.length; i++) {
			hasta = Cons.porDevolucion[i][0];
			por = Cons.porDevolucion[i][1];
			out += "[ >= " + m.formato(fp.format(desde), 8, false) + "  y  <  "
					+ m.formato(fp.format(hasta), 8, false) + " ] "
					+ m.formato(fp.format(por), 8, false) + "\n";
			desde = Cons.porDevolucion[i][0];
			;
		}
		hasta = 1;
		por = 0;
		out += "[ >= " + m.formato(fp.format(desde), 8, false) + "  y  <  "
				+ m.formato(fp.format(hasta), 8, false) + " ] "
				+ m.formato(fp.format(por), 8, false) + "\n";
		out += "----------------------------------------\n";
		return out;
	}

	public static String textoUsuariosAuxiliares() {
		String out = "\n\n";
		out += "Usuarios AUXILIARES (NO se considera venta compartida)\n";
		out += "------------------------------------------------------\n";

		String linea = "";

		// {"HALVAREZ", "79", new Rango("1900.01.01", "2012.12.31")},
		for (int i = 0; i < auxiliaresTemporales.length; i++) {
			Object[] dato = auxiliaresTemporales[i];
			String user = (String) dato[0];
			String id = (String) dato[1];

			linea = ("[" + user + "] " + id + "                  ").substring(
					0, 20);
			linea += "  Fechas:";

			for (int j = 2; j < dato.length; j++) {
				Rango r = (Rango) dato[j];
				linea += "  " + r;
			}
			out += linea + "\n";

		}

		out += "------------------------------------------------------\n";
		return out;
	}

	public static void xxmain(String[] args) {
		System.out.println(Cons.textoPorDevolucion());
	}
	
	
	//========================================
	//========================================
	// auxiliar, porque de esta manera puedo poner fábricas diferentes para 
	// cada venderor, por ahora son las mismas, pero ...
	private static String[][] fabricasNoComision = {
		{"10219","BATEBOL LTDA.IND"},
		{"10666","CHAMPION LABORAT"},
		{"82080","CONTINENTAL CUBI"},
		{"82726","CONTINENTAL CUBI"},
		{"84633","CONTINENTAL CUBI"},
		{"83631","CONTINENTAL CUBI"},
		{"10675","COOPER TIRE & RU"},
		{"10204","FILTROS HS de Si"},
		{"54505","GITI TIRE CUBIER"},
		{"53910","GRUPO KOLIMA"},
		{"10665","INDUSTRIAS MIGUE"},
		{"10673","ITOCHU CORPORATI"},
		{"10730","J B IMPORTACIONE"},
		{"57088","JOHNSON CONTROLS"},
		{"86299","JOHNSON CONTROLS"},
		{"74605","MAHLE ALEMANIA"},
		{"79094","MAHLE FILTROS BR"},
		{"10689","PEKO INCORPORATI"},
		{"10718","PENTIUS AUTOMOTI"},
		{"10662","SOFAPE S/A"},
		{"10672","TECNECO FILTERS"},
		{"10696","VALVOLINE ARGENT"},
		{"58253","YIGITAKU"},
		{"10501","PUMA ENERGY PARAGUAY S.A."}
		};
	// que vendedor y que lista de fábricas NO cobra comision
	private static Object[][] vendedoresFabricasNoComision = {
	//	{"9", fabricasNoComision, "Sixto Sanchez"},
		/* desde Julio 2015
		{"31", fabricasNoComision, "Carlos Ortiz"},
		{"27", fabricasNoComision, "Diego Ferreira"},
		{"12", fabricasNoComision, "Gabriel Nunez"},
		{"25", fabricasNoComision, "Lorenzo Risso"},
		{"133", fabricasNoComision, "ADRIAN MARTIN AGUILERA"},
		*/
		{"135", fabricasNoComision, "OSCAR CARBALLO"},
		{"4", fabricasNoComision, "ROQUE AMARILLA"},
		
	};

	// fabricas con comisión especial en todas las ventas
	private static String[][] fabricasComisionEspecialTodos = {
		{"xdr10501","PUMA ENERGY PARAGUAY S.A.", "",""},
	};
	
	private static Hashtable <String, String> hashVendedorFabrica = new Hashtable<>();
	
	private static String getIdViFas(String idV, String idFas){
		return (idV.trim()+"-"+idFas.trim());
	}

	static Misc misc = new Misc();
	static String ff = "/home/daniel/datos/yhaguy/comisiones/2014-08-agosto/datos/idVidFas.txt";
	
	static{
		// caraga un has con idVendedor y idFas, entonces si 
		// está en el hash es porque NO le corresponde comision
		for (int i = 0; i < vendedoresFabricasNoComision.length; i++) {
			String idV = (String) vendedoresFabricasNoComision[i][0];
			String[][] fas = (String[][]) vendedoresFabricasNoComision[i][1];
			for (int j = 0; j < fas.length; j++) {
				String idVIdFas = getIdViFas(idV,fas[j][0]);
				hashVendedorFabrica.put(idVIdFas, "ok");
				misc.agregarStringToArchivo(ff, idVIdFas+"\n");

			}
		}
		misc.agregarStringToArchivo(ff, "********************************\n");
		misc.agregarStringToArchivo(ff, "********************************\n");
	}
	
	
	
	public static boolean cobraComision(String idVendedor, String idFabrica){
		String idVidFas = getIdViFas(idVendedor, idFabrica);
		
		misc.agregarStringToArchivo(ff, idVidFas+"\n");
		
		Object o = hashVendedorFabrica.get(idVidFas);
		if (o == null){
			// si no está, cobra comision
			return true;
		}
		misc.agregarStringToArchivo(ff, "---------SI \n");
		return false;
	}
	
	
	
	private static ArrayList<String>[] cols = null;
	
	
	static public void iniPrintColumanas(int nColumnas){
		cols = new ArrayList[nColumnas];
		for (int i = 0; i < nColumnas; i++) {
			cols[i] = new ArrayList<String>();
		}
	}
	
	static public void addPrintDato(int col, String dato){
		ArrayList<String> ld = cols[col];
		ld.add(dato);		
	}
	
	static public String printCol(int[] anchos, String sep){
		String out = "";
		boolean hayDato = true;
		int vuelta = 0;
		while (hayDato == true){
			hayDato = false;
			String linea = "";
			for (int i = 0; i < cols.length; i++) {
				String str = "";
				ArrayList<String> ld = cols[i];
				if (vuelta < ld.size()){
					str = ld.get(vuelta);
					hayDato = true;
				}
				str += "                                                                            ";
				str = str.substring(0, anchos[i]);
				linea += sep + str;
			}
			if (hayDato == true){
				out += linea+"\n";
			}
			
			vuelta++;
		}
		return out;
		
	}
	
	
	public static void main(String[] args) {
		iniPrintColumanas(3);
		addPrintDato(0, "1.1");
		addPrintDato(0, "2.1");
		addPrintDato(0, "3.1");
		addPrintDato(1, "1.2");
		addPrintDato(1, "2.2");
		addPrintDato(2, "1.3");
		addPrintDato(2, "2.3");
		addPrintDato(2, "3.3");
		
		System.out.println(printCol(new int[]{10,10,10}, "|"));
		
	}
	
}
