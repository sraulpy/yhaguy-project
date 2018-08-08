package com.yhaguy.util.migracion.cipec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

public class CipecConfiguracion {

	static String[] parserLinea(String linea) {
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

	
	static String URL_IMAGEN = "cipec/";
	
	
	// =================== JEDI
	// ==================================================================
	static String JEDI_CIPEC = "./WEB-INF/docs/migracion/cipec/Articulo_Cipec_en_Jedisoft.csv";

	static String[] colNombreJediCipec = {

	"IDARTICULO", "DESCRIPCION", "CODIGOFABRICA", "CODIGOBARRA",
			"CODIGOORIGINAL", "IDMARCA", "MARCA", "IDPARTE", "PARTE",
			"IDMARCAAPLICACION", "MARCAAPLICACION", "IDMODELOAPLICACION",
			"MODELOAPLICACION", "IDFAMILIA", "FAMILIA", "IDLINEA", "LINEA",
			"ESTADO", "IDCOLECCION", "COLECCION", "FECHAALTA" };

	static Hashtable<String, Integer> colJediCipec = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colNombreJediCipec.length; i++) {
			String c = colNombreJediCipec[i];
			colJediCipec.put(c, i);
		}
	}

	static String getColJediCinpal(String col, String[] dato) {
		return getCol(colJediCipec, col, dato);
	}

	static BufferedReader abrirArchivoJedi() throws Exception {
		String ff = JEDI_CIPEC;
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

	public static Connection CIPEC_CONEXION = null;

	public static void conectarCipec() throws Exception {
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://127.0.0.1:5432/cipec";
		CIPEC_CONEXION = DriverManager.getConnection(url, "postgres",
				"postgres");
	}

	public static ResultSet getConsulta1Cipec(String codigoFabrica)
			throws Exception {
		ResultSet out = null;

		String sql1 = "select "
				+ " p.codigoproduto, p.numeroproduto, p.codprodgrid, p.codprodgridpesq, p.descricaoproduto, p.arquivofotoproduto "
				+ " r.codigoreferenciacruzada , r.numeroproduto, r.numeroprodutopesq, "
				+ " f.codigofabricante, f.descricaofabricante, "
				+ " from (produto as p "
				+ " left join referenciacruzada r on r.codigoproduto = p.codigoproduto )"
				+ " left join fabricante f on f.Codigofabricante = r.CodigoFabricante "
				+ " where p.codprodgrid = xxx";
		sql1 = sql1.replace("xxx", "'" + codigoFabrica + "'");

		System.out.println("\n\n"+sql1+"\n\n");
		
		Statement s = CIPEC_CONEXION.createStatement(
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

	public static ResultSet getConsulta2Cipec(String codigoFabrica)
			throws Exception {
		ResultSet out = null;

		String sql2 = "select "
				+ " pa.codigoaplicacao, pa.codigoproduto,"
				+ " a.codigoaplicacao, a.codigofabricante, a.descricaoaplicacao,"
				+ " p.codigogrupoproduto,"
				+ " gp.descricaogrupoprodutoe,"
				+ " fa.descricaofabricante,"
				+ " from "
				+ " ("
				+ " produto as p "
				+ " left join produtoaplicacao pa on p.codigoproduto = pa.codigoproduto"
				+ " left join grupoproduto gp on  gp.codigogrupoproduto = p.codigogrupoproduto"
				+ " left join aplicacao a on pa.codigoaplicacao = a.codigoaplicacao"
				+ " left join fabricante fa on a.codigofabricante = fa.codigofabricante"
				+ "  )" + " where p.codprodgrid = xxx";

		sql2 = sql2.replace("xxx", "'" + codigoFabrica + "'");

		Statement s = CIPEC_CONEXION.createStatement(
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
