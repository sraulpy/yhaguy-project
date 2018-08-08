package com.yhaguy.util.migracion.cinpal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

public class CinpalConfiguracion {

	static String[] parserLinea(String linea) {
		// divide la línea, que es un String, en todas las columnas que hay (o campos). 
		// En este caso, considera "," como separador
		String[] dato = linea.split(",");
		return dato;
	}

	static String getCol(Hashtable<String, Integer> tabla, String col,
			String[] dato) {
		int p = tabla.get(col);
		String out = (dato[p]).trim();
		out = out.replaceAll("\"", "");
		if (col.compareTo("IDPERSONA") == 0) {
			out = out.replace(",", "");
		}
		if (col.compareTo("RUC") == 0) {
			out = out.replace(" ", "");
		}
		return out;
	}

	
	static String URL_IMAGEN = "cinpal/";
	
	
	// =================== JEDI
	// ==================================================================
	static String JEDI_CINPAL = "./WEB-INF/docs/migracion/cinpal/Articulo_Cinpal_en_Jedisoft.csv";

	//========================================================
	// todo esto es para poder acceder de forma facil a las columnas de los datos
	static String[] colNombreJediCinpal = {

	"IDARTICULO", "DESCRIPCION", "CODIGOFABRICA", "CODIGOBARRA",
			"CODIGOORIGINAL", "IDMARCA", "MARCA", "IDPARTE", "PARTE",
			"IDMARCAAPLICACION", "MARCAAPLICACION", "IDMODELOAPLICACION",
			"MODELOAPLICACION", "IDFAMILIA", "FAMILIA", "IDLINEA", "LINEA",
			"ESTADO", "IDCOLECCION", "COLECCION", "FECHAALTA" };

	static Hashtable<String, Integer> colJediCinpal = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colNombreJediCinpal.length; i++) {
			String c = colNombreJediCinpal[i];
			colJediCinpal.put(c, i);
		}
	}

	//========================================================

	
	static String getColJediCinpal(String col, String[] dato) {
		return getCol(colJediCinpal, col, dato);
	}

	static BufferedReader abrirArchivoJedi() throws Exception {
		String ff = JEDI_CINPAL;
		File f = new File(ff);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		return entrada;
	}

	static String getCodigoProducto(String[] dato) {
		String out = "";
		out = getColJediCinpal("IDARTICULO", dato);
		out = out.substring(4, 14);
		out = out.replaceAll("\\.", "");
		out = "" + Long.parseLong(out);
		return out;
	}

	// ================== BD =========================================

	public static Connection CINPAL_CONEXION = null;

	public static void conectarCinpal() throws Exception {
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/cinpal";
		CINPAL_CONEXION = DriverManager.getConnection(url, "postgres",
				"postgres");
	}

	public static ResultSet getConsulta1Cinpal(String codigoFabrica)
			throws Exception {
		ResultSet out = null;

		String sql1 = "select "
				+ " p.codigoproduto, p.numeroempresaproduto, p.descricaoprodutoe, p.arquivofotoproduto , "
				+ " r.codigoreferenciacruzada , r.numerofabricante, "
				+ " f.codigofabricante, f.descricaofabricantee, f.descricaoabrevfabricantee, "
				+ " po.observacaoe "
				+ " from (produto as p "
				+ " left join referenciacruzada r on p.codigoproduto = r.codigoproduto )"
				+ " left join fabricante f on r.codigofabricante = f.codigofabricante "
				+ " left join produto_obs po on p.codigoproduto = po.codigoproduto "
				+ " where p.numeroempresaproduto = xxx";
		sql1 = sql1.replace("xxx", "'" + codigoFabrica + "'");

		System.out.println("\n\n"+sql1+"\n\n");
		
		Statement s = CINPAL_CONEXION.createStatement(
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		
		out = s.executeQuery(sql1);
		/*
		if (out.next() == false) {
			throw new Exception(
					"Error consulta 1, no encontró datos para este codigo de fabrica:"
							+ codigoFabrica);
		}
		*/

		return out;
	}

	public static ResultSet getConsulta2Cinpal(String codigoFabrica)
			throws Exception {
		ResultSet out = null;

		String sql2 = "select "
				+ " ap.codigoaplicacao, ap.complementoaplicacaoe,"
				+ "  a.descricaoaplicacaoe,"
				+ " ta.descricaotipoaplicacaoe,"
				+ " fa.descricaoabrevfabricantee"
				+ " from "
				+ " ("
				+ " produto as p "
				+ "  left join aplicacao_produto ap on p.codigoproduto = ap.codigoproduto "
				+ " left join aplicacao a on ap.codigoaplicacao = a.codigoaplicacao "
				+ " left join tipoaplicacao ta on  ta.codigotipoaplicacao = a.codigotipoaplicacao"
				+ "  left join fabricante fa on a.codigofabricante = fa.codigofabricante"
				+ "  )" + " where p.numeroempresaproduto = xxx";

		sql2 = sql2.replace("xxx", "'" + codigoFabrica + "'");

		Statement s = CINPAL_CONEXION.createStatement(
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		out = s.executeQuery(sql2);
		/*
		if (out.next() == false) {
			throw new Exception(
					"Error consulta 2, no encontró datos para este codigo de fabrica:"
							+ codigoFabrica);
		}
		*/
		return out;
	}

	// ==============================================================

}
