package com.yhaguy.util.migracion.urba;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.coreweb.Config;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloMarcaAplicacion;
import com.yhaguy.domain.ArticuloModeloAplicacion;
import com.yhaguy.domain.ArticuloPresentacion;
import com.yhaguy.domain.RegisterDomain;

public class urba_jedi {

	static Misc m = new Misc();
	
	static String LINEA_LIVIANA = "LIVIANA";
	static String PARTE_CAJA = "CAJA";
	
	public static String Articulo_URBA_en_Jedisoft_GEcsv= "./WEB-INF/docs/migracion/articulos/Articulo_URBA_en_Jedisoft_GE.csv";

	// ========================================================
	// todo esto es para poder acceder de forma fácil a las columnas de los datos
	
	static String[] colNombreDet = { "IDARTICULO", "DESCRIPCION","CODIGO_FABRICA" ,"CODIGO_BARRA" ,"CODIGO_ORIGINAL","IDMARCA" , "MARCA" , "IDPARTE", "PARTE" ,"IDMARCA_APLICACION" , "MARCA_APLICACION" , "IDMODELO_APLICACION" , "MODELO_APLICACION" , "IDFAMILIA" ,"FAMILIA" ,"IDLINEA" ,"LINEA" , "ESTADO" , "IDCOLECCION","COLECCION" , "FECHA_ALTA"};
	
	
	static Hashtable<String, Integer> colNombresPos = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colNombreDet.length; i++) {
			String c = colNombreDet[i];
			colNombresPos.put(c, i);
		}
	}

	static String getDato(String col, String[] dato) {
		return getCol(colNombresPos, col, dato);
	}

	static double getDatoDouble(String col, String[] dato) {
		String d = getCol(colNombresPos, col, dato);
		String dd = d.replace(',', '.');
		double out = 0;
		if (d.compareTo("Null") != 0) {
			out = Double.parseDouble(dd.trim());
		}		
		return out;
	}
	
	static java.util.Date getDatoDate(String col, String[] dato) {
		String f = getCol(colNombresPos, col, dato);
		java.util.Date fh = m.stringMesDiaAnoToDate(f);
		return fh;
	}

	static String getCol(Hashtable<String, Integer> tabla, String col,
			String[] dato) {
		int p = tabla.get(col);
		String out = (dato[p]).trim();
		out = out.replaceAll("\"", "");
		return out;
	}

	static BufferedReader abrirArchivo() throws Exception {
		String ff = Articulo_URBA_en_Jedisoft_GEcsv;
		File f = new File(ff);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		return entrada;
	}

	static String[] parserLinea(String linea) {
		// divide la línea, que es un String, en todas las columnas que hay (o
		// campos).
		// En este caso, considera "tab" como separador
		String[] dato = linea.split("\t");
		return dato;
	}
	public static void migrarDatos() throws Exception {

		// crear una instancia 
		
		RegisterDomain rr = RegisterDomain.getInstance();
		setingInicial();
		String linea = "";

		BufferedReader archivo = abrirArchivo();

		linea = archivo.readLine();
		
		
		
		
		
		
		
		/**
		 * Primero levantamos los Tipos
		 */
		
		// Estados del articulo
		Tipo estado_activo = rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_ESTADO_ACTIVO);
		
		// Familias del articulo
		Tipo familia_repuestos = rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_FAMILIA_REPUESTOS);		
		
		// Marcas del Articulo
		Tipo marca_Urba = rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_MARCA_URBA) ;
		
		// Partes del Articulo
	    Tipo parte_Caja = rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_PARTE_CAJA);
		
	    Tipo parte_Motor = rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_PARTE_MOTOR);
		// Lineas del Articulo
		
	    Tipo linea_pesada = rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_LINEA_PESADA) ;
		
	    Tipo linea_Liviana = rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_LINEA_LIVIANA) ;
		
		// Unidad_Medida del Articulo
		Tipo unidad_und = rr.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_UNID_MED_UND) ;
		
		// Articulo Presentacion por Defecto
		ArticuloPresentacion artp = rr.getArticuloPresentacionDefault();
		
		
		
		
		
		
		
		
		int i = 0;
		while (archivo.ready() == true) {
			i++;

			linea = archivo.readLine();
			String[] datos = parserLinea(linea);

			String IDARTICULO = getDato("IDARTICULO", datos);
			String DESCRIPCION = getDato("DESCRIPCION", datos);
			String CODIGO_FABRICA = getDato("CODIGO_FABRICA", datos);
			String CODIGO_BARRA= getDato("CODIGO_BARRA", datos);
			String CODIGO_ORIGINAL= getDato("CODIGO_ORIGINAL", datos);
			String IDMARCA= getDato("IDMARCA", datos);
			String MARCA= getDato("MARCA", datos);
			String IDPARTE= getDato("IDPARTE", datos);
			String PARTE= getDato("PARTE", datos);
			String IDMARCA_APLICACION= getDato("IDMARCA_APLICACION", datos);
			String MARCA_APLICACION= getDato("MARCA_APLICACION", datos);
			String IDMODELO_APLICACION= getDato("IDMODELO_APLICACION", datos);
			String MODELO_APLICACION= getDato("MODELO_APLICACION", datos);
			String IDFAMILIA= getDato("IDFAMILIA", datos);
			String FAMILIA= getDato("FAMILIA", datos);
			String IDLINEA= getDato("IDLINEA", datos);
			String LINEA= getDato("LINEA", datos);
			String ESTADO= getDato("ESTADO", datos);
			String IDCOLECCION= getDato("IDCOLECCION", datos);
			String COLECCION= getDato("COLECCION", datos);
			String FECHA_ALTA= getDato("FECHA_ALTA", datos);	
			
			
			// creamos el articulo..
			Articulo art = new Articulo();
			art.setCodigoInterno(IDARTICULO);
			art.setDescripcion(DESCRIPCION);
			art.setCodigoBarra(CODIGO_BARRA);
			art.setCodigoOriginal(CODIGO_ORIGINAL);
			art.setObservacion("sin observacion");
			art.setVolumen(0);
						
			// asignamos los tipos
			art.setArticuloEstado(estado_activo);
			art.setArticuloFamilia(familia_repuestos);
			art.setArticuloMarca(marca_Urba);
			
			if (LINEA.trim().compareTo(LINEA_LIVIANA.trim()) == 0) {
				art.setArticuloLinea(linea_Liviana);
			} else {
				art.setArticuloLinea(linea_pesada);
			}
		
			
			art.setArticuloUnidadMedida(unidad_und);
		
			
			if (PARTE.trim().compareTo(PARTE_CAJA.trim()) == 0) {
				art.setArticuloParte(parte_Caja);
			} else {
				art.setArticuloParte(parte_Motor);
			
			//Asignacion de la Marca Aplicacion	
			ArticuloMarcaAplicacion am = new ArticuloMarcaAplicacion();
			am.setDescripcion(MARCA_APLICACION);
			am.setSigla(MARCA_APLICACION);
				
			if (rr.existe(ArticuloMarcaAplicacion.class, 
					"descripcion", Config.TIPO_STRING, MARCA_APLICACION, am) == false) {
				rr.saveObject(am, "MIGRACION");
			} else {
				am = rr.getArticuloMarcaAplicacionByDescripcion(MARCA_APLICACION);
			}
								
			//Asignacion del modelo aplicacion
			Set<ArticuloModeloAplicacion> amps = new HashSet<ArticuloModeloAplicacion>();
			
			if (MODELO_APLICACION.trim().contains("/")) {
				String[] modelos = MODELO_APLICACION.split("/");
				
				for (int j = 0; j < modelos.length; j++) {
					
					ArticuloModeloAplicacion amp = new ArticuloModeloAplicacion();
					amp.setDescripcion(modelos[j]);
					amp.setArticuloMarcaAplicacion(am);
					amps.add(amp);
					rr.saveObject(amp, "MIGRACION");
				}
			} else {
				
				ArticuloModeloAplicacion amp = new ArticuloModeloAplicacion();
				amp.setDescripcion(MODELO_APLICACION);
				amp.setArticuloMarcaAplicacion(am);
				amps.add(amp);
				rr.saveObject(amp, "MIGRACION");
			}
			
//			art.setArticuloModeloAplicaciones(amps);
						
			rr.saveObject(art, "MIGRACION");                                        
			
			
			
			
			
			System.out.println(i + ") " + IDARTICULO + " - " +  DESCRIPCION +" - "+CODIGO_FABRICA+" - "+CODIGO_BARRA+" - "+CODIGO_ORIGINAL+" - "+IDMARCA+" - "+MARCA+" -"+IDPARTE+" - "+PARTE+" - "+IDMARCA_APLICACION+" - "+MARCA_APLICACION+" - "+IDMODELO_APLICACION+" - "+MODELO_APLICACION+" - "+IDFAMILIA+" - "+FAMILIA+" - "+IDLINEA+" - "+LINEA+" - "+ESTADO+" - "+IDCOLECCION+" - "+COLECCION+" - "+FECHA_ALTA );
		}
		}
	}

	public static void main(String[] args) throws Exception {
		migrarDatos();
		
	}
		
	
	}
	
	
