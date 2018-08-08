package com.yhaguy.util.migracion.cipec;

import java.io.BufferedReader;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.coreweb.domain.*;
import com.coreweb.extras.reporte.DatosReporte;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloMarcaAplicacion;
import com.yhaguy.domain.ArticuloModeloAplicacion;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.ProveedorArticulo;
import com.yhaguy.domain.RegisterDomain;

public class CipecMigracion extends CipecConfiguracion {

	static RegisterDomain rr = RegisterDomain.getInstance();

	public static Tipo CIPEC_MARCA = new Tipo();

	public static Tipo CIPEC_UNIDAD = new Tipo();

	public static Hashtable<String, Tipo> estados = new Hashtable<>();

	public static Hashtable<String, Tipo> partes = new Hashtable<>();

	public static Hashtable<String, Tipo> familias = new Hashtable<>();

	public static Hashtable<String, Tipo> lineas = new Hashtable<>();

	public static void grabarDB(Object dom) {

		try {
			 rr.saveObject((Domain) dom, "MIGRACION");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void cargaInicial() throws Exception {

		// cargar Cipec como MARCA TIPO
		// =============================================================================

		TipoTipo MARCA_TIPO = new TipoTipo();
		MARCA_TIPO.setId((long) 57);
		MARCA_TIPO.setDescripcion(Configuracion.ID_TIPO_ARTICULO_MARCA);

		grabarDB(MARCA_TIPO);

		CIPEC_MARCA.setId((long) 191);
		CIPEC_MARCA.setSigla(Configuracion.SIGLA_ARTICULO_MARCA);
		CIPEC_MARCA.setDescripcion("CIPEC");
		CIPEC_MARCA.setTipoTipo(MARCA_TIPO);

		grabarDB(CIPEC_MARCA);

		// cargar Cipec como ESTADO TIPO
		// ============================================================================

		TipoTipo ESTADO_TIPO = new TipoTipo();
		ESTADO_TIPO.setId((long) 41);
		ESTADO_TIPO.setDescripcion(Configuracion.ID_TIPO_ARTICULO_ESTADO);

		grabarDB(ESTADO_TIPO);

		// ===========ARTICULO ESTADO
		Tipo ACTIVO = new Tipo();
		ACTIVO.setDescripcion("ACTIVO");
		ACTIVO.setSigla(Configuracion.SIGLA_ARTICULO_ESTADO);
		ACTIVO.setTipoTipo(ESTADO_TIPO);
		// ...
		estados.put("A", ACTIVO);

		grabarDB(ACTIVO);

		Tipo INACTIVO = new Tipo();
		INACTIVO.setDescripcion("INACTIVO");
		INACTIVO.setSigla(Configuracion.SIGLA_ARTICULO_ESTADO);
		INACTIVO.setTipoTipo(ESTADO_TIPO);
		// ...
		estados.put("I", INACTIVO);

		grabarDB(INACTIVO);

		// cargar Cipec como PARTE TIPO
		// =============================================================================

		TipoTipo PARTE_TIPO = new TipoTipo();
		PARTE_TIPO.setId((long) 58);
		PARTE_TIPO.setDescripcion(Configuracion.ID_TIPO_ARTICULO_PARTE);

		grabarDB(PARTE_TIPO);

		// ===========ARTICULO PARTE
		Tipo CAJA = new Tipo();
		CAJA.setDescripcion("CAJA");
		CAJA.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		CAJA.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("2", CAJA);

		grabarDB(CAJA);

		Tipo DIFERENCIAL = new Tipo();
		DIFERENCIAL.setDescripcion("DIFERENCIAL");
		DIFERENCIAL.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		DIFERENCIAL.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("3", DIFERENCIAL);
		grabarDB(DIFERENCIAL);

		Tipo CAJAZF = new Tipo();
		CAJAZF.setDescripcion("CAJAZF");
		CAJAZF.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		CAJAZF.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("7", CAJAZF);
		grabarDB(CAJAZF);

		Tipo MOTOR = new Tipo();
		MOTOR.setDescripcion("MOTOR");
		MOTOR.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		MOTOR.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("1", MOTOR);
		grabarDB(MOTOR);

		Tipo CARDAN = new Tipo();
		CARDAN.setDescripcion("DIRECCION");
		CARDAN.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		CARDAN.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("8", CARDAN);
		grabarDB(CARDAN);

		Tipo DIRECCION = new Tipo();
		DIRECCION.setDescripcion("DIRECCION");
		DIRECCION.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		DIRECCION.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("21", DIRECCION);
		grabarDB(DIRECCION);

		Tipo ELE = new Tipo();
		ELE.setDescripcion("ELE");
		ELE.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		ELE.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("24", ELE);
		grabarDB(ELE);

		Tipo FRENOS = new Tipo();
		FRENOS.setDescripcion("FRENOS");
		FRENOS.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		FRENOS.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("31", FRENOS);
		grabarDB(FRENOS);

		Tipo OTROS = new Tipo();
		OTROS.setDescripcion("OTROS");
		OTROS.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		OTROS.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("45", OTROS);
		grabarDB(OTROS);

		Tipo SinParte = new Tipo();
		SinParte.setDescripcion("SinParte");
		SinParte.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
		SinParte.setTipoTipo(PARTE_TIPO);
		// ...
		partes.put("-1", SinParte);
		grabarDB(SinParte);

		// ===========ARTICULO FAMILIA
		// ====================================================================================

		// cargar Cinpal como FAMILIA TIPO

		TipoTipo FAMILIA_TIPO = new TipoTipo();
		FAMILIA_TIPO.setId((long) 56);
		FAMILIA_TIPO.setDescripcion(Configuracion.ID_TIPO_ARTICULO_FAMILIA);

		grabarDB(FAMILIA_TIPO);

		Tipo REPUESTOS = new Tipo();
		REPUESTOS.setDescripcion("REPUESTOS");
		REPUESTOS.setSigla(Configuracion.SIGLA_ARTICULO_FAMILIA);
		REPUESTOS.setTipoTipo(FAMILIA_TIPO);

		
		// ...
		familias.put("5", REPUESTOS);

		grabarDB(REPUESTOS);

		Tipo SinFamilia = new Tipo();
		SinFamilia.setDescripcion("Sin Familia");
		SinFamilia.setSigla(Configuracion.SIGLA_ARTICULO_FAMILIA);
		SinFamilia.setTipoTipo(FAMILIA_TIPO);
		// ...
		familias.put("-1", SinFamilia);

		grabarDB(SinFamilia);

		// ===========ARTICULO LINEA
		// ====================================================================================

		// cargar Cinpal como LINEA TIPO

		TipoTipo LINEA_TIPO = new TipoTipo();
		LINEA_TIPO.setId((long) 55);
		LINEA_TIPO.setDescripcion(Configuracion.ID_TIPO_ARTICULO_LINEA);

		grabarDB(LINEA_TIPO);

		Tipo PESADA = new Tipo();
		PESADA.setDescripcion("PESADA");
		PESADA.setSigla(Configuracion.SIGLA_ARTICULO_LINEA);
		PESADA.setTipoTipo(LINEA_TIPO);
		// ...
		lineas.put("2", PESADA);

		grabarDB(PESADA);

		Tipo SinLinea = new Tipo();
		SinLinea.setDescripcion("Sin Linea");
		SinLinea.setSigla(Configuracion.SIGLA_ARTICULO_LINEA);
		SinLinea.setTipoTipo(LINEA_TIPO);
		// ...
		lineas.put("-1", SinLinea);

		grabarDB(SinLinea);

		// cargar Cinpal como UNIDAD DE MEDIDA TIPO
		// =============================================================================

		TipoTipo UNIDAD_TIPO = new TipoTipo();
		UNIDAD_TIPO.setId((long) 59);
		UNIDAD_TIPO.setDescripcion(Configuracion.ID_TIPO_ARTICULO_UNID_MED);

		grabarDB(UNIDAD_TIPO);

		CIPEC_UNIDAD.setId((long) 194);
		CIPEC_UNIDAD.setSigla(Configuracion.SIGLA_ARTICULO_UNID_MED);
		CIPEC_UNIDAD.setDescripcion("UNID.");
		CIPEC_UNIDAD.setTipoTipo(UNIDAD_TIPO);

		grabarDB(CIPEC_UNIDAD);

		// modelos

	}

	public static void cargaCinpal() throws Exception {
		int p = 0;
		String linha = "";
		BufferedReader jedi = abrirArchivoJedi();

		// leer la cabecera y la primer linea
		linha = jedi.readLine() + " ";

		Proveedor provCinpal = (Proveedor) rr.getObject(
				Proveedor.class.getName(), 9);
		String siHayCodigo = "";

		// recorrer el archivo Jedi
		while (jedi.ready()) {

			try {

				p++;
				// lee el dato
				linha = jedi.readLine() + " ";
				String[] datoJedi = parserLinea(linha);
				System.out.println("Leyendo:" + linha);

				// Información del Jedi
				String codigoProducto = getCodigoProducto(datoJedi);
				String jediIdArticulo = getColJediCinpal("IDARTICULO", datoJedi);
				String jediDescripcion = getColJediCinpal("DESCRIPCION",
						datoJedi);
				String jediCodigoBarra = getColJediCinpal("CODIGOBARRA",
						datoJedi);
				String jediCodigofabrica = getColJediCinpal("CODIGOFABRICA",
						datoJedi);
				String jediCodigoOriginal = getColJediCinpal("CODIGOORIGINAL",
						datoJedi);
				String jediIdMarca = getColJediCinpal("IDMARCA", datoJedi);
				String jediMarca = getColJediCinpal("MARCA", datoJedi);
				String jediIdParte = getColJediCinpal("IDPARTE", datoJedi);
				String jediParte = getColJediCinpal("PARTE", datoJedi);
				String jediIdMarcaAplicacion = getColJediCinpal(
						"IDMARCAAPLICACION", datoJedi);
				String jediMarcaAplicacion = getColJediCinpal(
						"MARCAAPLICACION", datoJedi);
				String jediIdModeloAplicacion = getColJediCinpal(
						"IDMODELOAPLICACION", datoJedi);
				String jediModeloAplicacion = getColJediCinpal(
						"MODELOAPLICACION", datoJedi);
				String jediIdFamilia = getColJediCinpal("IDFAMILIA", datoJedi);
				String jediFamilia = getColJediCinpal("FAMILIA", datoJedi);
				String jediIdLinea = getColJediCinpal("IDLINEA", datoJedi);
				String jediLinea = getColJediCinpal("LINEA", datoJedi);
				String jediEstado = getColJediCinpal("ESTADO", datoJedi);
				String jediIdColeccion = getColJediCinpal("IDCOLECCION",
						datoJedi);
				String jediColeccion = getColJediCinpal("COLECCION", datoJedi);
				String jediFechaAlta = getColJediCinpal("FECHAALTA", datoJedi);

				// =========== inicio ===========

				Articulo art_cinpal = new Articulo();

				// Información de Cinpal

				// ======== codigo original ================
				ResultSet cinpal1 = getConsulta1Cipec(codigoProducto.trim());

				if (codigoProducto.trim().compareTo("7350191") == 0) {
					System.out.println("aca");
				}

				String codigoOriginal = "";
				String observacaoe = "";
				String codigoFabricaEnCinpal = "";
				String descripcionArticuloFabricaEnCinpal = "";
				
				String foto = "";
				boolean conBarra = false;

				while (cinpal1.next()) {

					// Sacar la informacion de la consulta hecha a Cinpal
					String db_descripcion = cinpal1
							.getString("descricaoprodutoe");

					String db_numeroempresaproduto = cinpal1
							.getString("numeroempresaproduto");
					codigoFabricaEnCinpal = db_numeroempresaproduto;

					String db_descricaoprodutoe = cinpal1
							.getString("descricaoprodutoe");

					String db_arquivofotoproduto = cinpal1
							.getString("arquivofotoproduto");
					
					
					String db_codigoreferenciacruzada = cinpal1
							.getString("codigoreferenciacruzada");
					String db_numerofabricante = cinpal1
							.getString("numerofabricante");

					String db_codigofabricante = cinpal1
							.getString("codigofabricante");
					String db_descricaofabricantee = cinpal1
							.getString("descricaofabricantee");
					String db_descricaoabrevfabricantee = cinpal1
							.getString("descricaoabrevfabricantee");
					String db_observacaoe = cinpal1.getString("observacaoe");
					
					int mas=0;
					if (db_numerofabricante == null) {
						siHayCodigo = siHayCodigo + " = " + jediIdArticulo;
						// nada
					} else {
						conBarra = true;
						codigoOriginal =   codigoOriginal.trim()+ "/" + db_numerofabricante.trim() ;
					}

					if ((db_arquivofotoproduto != null) && (db_arquivofotoproduto.trim().length()>0)){
						foto = db_arquivofotoproduto.trim();
					}

					
					// para tomar todos los códigos originales
					descripcionArticuloFabricaEnCinpal = db_descripcion;
					observacaoe = db_observacaoe;
				}

				
				if (conBarra == true){
					codigoOriginal = codigoOriginal.substring(1);
				}
				
				if (codigoOriginal.trim().length()==0){
					codigoOriginal = jediCodigoOriginal;
				}
				
				// ============== modelos aplicacion ==================
				ResultSet cinpal2 = getConsulta2Cipec(codigoProducto.trim());

				while (cinpal2.next()) {
					String codigoaplicacao = cinpal2
							.getString("codigoaplicacao");
					String complementoaplicacaoe = cinpal2
							.getString("complementoaplicacaoe");

					// con este dato vamos a poder buscar el modelo que le
					// corresponde a
					// este articulo
					String descricaoaplicacaoe = (cinpal2
							.getString("descricaoaplicacaoe") + "").trim();
					String descricaotipoaplicacaoe = cinpal2
							.getString("descricaotipoaplicacaoe");

					String marca = (cinpal2
							.getString("descricaoabrevfabricantee") + "")
							.trim();

					for (int i = 0; i < 5; i++) {
						descricaoaplicacaoe = descricaoaplicacaoe.replaceAll(
								"  ", " ");
					}

					// descricaoaplicacaoe buscar el modelo
					ArticuloModeloAplicacion ma = rr
							.getArticuloModeloAplicacionByDescripcion(
									descricaoaplicacaoe, marca);
					if (ma != null) {
//						art_cinpal.getArticuloModeloAplicaciones().add(ma);
					}

				}

				System.out.println(p + ") "
						+ descripcionArticuloFabricaEnCinpal);

				// =================================================================
				// =================================================================
				// =================================================================
				// Desde acá armar los objetos de Yhaguy y grabarlos, es decir,
				// usar el rr

				// aca armar los objetos de Yhaguy, parecido a como se hace en
				// el Population

				// PRUEBA
				String obs = "";
				if (observacaoe == null) {
					observacaoe = obs;
				}

				art_cinpal.setDescripcion(jediDescripcion);
				art_cinpal.setCodigoInterno(jediIdArticulo);
				art_cinpal.setCodigoBarra(jediCodigoBarra);
				art_cinpal.setCodigoOriginal(codigoOriginal);// le tenemos mas
																// fe a cinpal
				
				if (foto.trim().length() > 0){
					art_cinpal.setUrlImagen(CipecConfiguracion.URL_IMAGEN+foto);					
				}
				

				String x255 = " ";
				for (int i = 0; i < 255; i++) {
					x255 = x255 + " ";
				}
				
				observacaoe = (observacaoe + x255).substring(0, 250);
				art_cinpal.setObservacion(observacaoe);
				art_cinpal.setImportado(true);

				// Cargar Marca
				art_cinpal.setArticuloMarca(CIPEC_MARCA);

				// Cargar Estado Articulo
				Tipo estado = estados.get(jediEstado);
				art_cinpal.setArticuloEstado(estado);

				// cargar la parte
				Tipo parte = partes.get(jediIdParte);
				art_cinpal.setArticuloParte(parte);

				// cargar familia
				Tipo familia = familias.get(jediIdFamilia);
				art_cinpal.setArticuloFamilia(familia);

				// cargar familia
				Tipo linea = lineas.get(jediIdLinea);
				art_cinpal.setArticuloLinea(linea);

				// Cargar Unidad de Medida
				art_cinpal.setArticuloUnidadMedida(CIPEC_UNIDAD);

				// Cargar Fecha Alta de Articulos
				Misc misc = new Misc();
				art_cinpal.setFechaAlta(misc.stringToDate(jediFechaAlta));

				// NO TENEMOS, NI DE CINPAL NI DE JEDI
				// art_cinpal.setPeso(1.5);
				// art_cinpal.setVolumen(11.5);
				// art_cinpal.setArticuloPresentacion(null);

				// SI CARGAMOS ANTES LOS TIPOS Y LUEGO ASIGNAMOS...?

				// modelo ---------------------
				// buscar el modeloaplicacion que tiene esta descripcion
				// "descricaoaplicacaoe"

				// COMO ASIGNAMOS ESTOS VALORES...?

				Set<ProveedorArticulo> prart = new HashSet<ProveedorArticulo>();
				ProveedorArticulo pa = new ProveedorArticulo();
				pa.setProveedor(provCinpal);

				if (jediCodigofabrica.trim().length() == 0) {
					jediCodigofabrica = codigoFabricaEnCinpal;

				}
				pa.setCodigoFabrica(jediCodigofabrica);
				pa.setDescripcionArticuloProveedor(descripcionArticuloFabricaEnCinpal);

				prart.add(pa);
				art_cinpal.setProveedorArticulos(prart);

				grabarDB(art_cinpal);

				// System.out.println("Parte: " + jediIdParte +" - "+ jediParte
				// );
				// System.out.println("Familia: " + jediIdFamilia +" - "+
				// jediFamilia );
				// System.out.println("Estado: " + jediEstado );

			} catch (Exception e) {
				e.printStackTrace();
			}

		}// while
		System.out.println("\n\n\n\n" + siHayCodigo);

	}

	public static void main(String[] args) throws Exception {

		/*
		 * OJO, seguir los pasos no hay que cargar dos veces los datos de Tipo
		 */

		setingInicial();

		conectarCipec();

		cargaInicial();

		cargaCinpal();

	}

}
