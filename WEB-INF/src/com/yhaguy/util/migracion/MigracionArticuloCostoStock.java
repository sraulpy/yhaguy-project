package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Hashtable;

import com.coreweb.domain.Domain;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloCosto;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloStock;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;

public class MigracionArticuloCostoStock {

	static String archivoCostoStock = "./WEB-INF/docs/migracion/articulos/Articulo/Stock_Costo_Articulos_GE.csv";

	static RegisterDomain rr = RegisterDomain.getInstance();

	static void grabar(Domain d) throws Exception {
		boolean graba = true;
		if (graba == true) {
			rr.saveObject(d, "migra");
		}
	}

	// ========================================================
	// todo esto es para poder acceder de forma facil a las columnas de los
	// datos

	static String[] colNombre = {

	"IDARTICULO", "DESCRIPCION", "REFERENCIA", "x1", "x2", "COSTOUS",
			"COSTOGS", "STOCK", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO",
			"JUNIO", "JULIO", "AGOSTO", "SETIEMBRE", "OCTUBRE", "NOVIEMBRE",
			"DICIEMBRE" };

	static Hashtable<String, Integer> colNombresPos = new Hashtable<>();

	static void setingInicial() {
		// cargar las posiciones según los campos
		for (int i = 0; i < colNombre.length; i++) {
			String c = colNombre[i];
			colNombresPos.put(c, i);
		}
	}

	static String getDato(String col, String[] dato) {
		return getCol(colNombresPos, col, dato);
	}

	static double getDatoDouble(String col, String[] dato) {
		String d = getCol(colNombresPos, col, dato);
		d = d.replaceAll("\\.", "");
		d = d.replaceAll(",", ".");
		double out = 0;
		out = Double.parseDouble(d.trim());
		return out;
	}

	static String getCol(Hashtable<String, Integer> tabla, String col,
			String[] dato) {
		int p = tabla.get(col);
		String out = (dato[p]).trim();
		out = out.replaceAll("\"", "");
		return out;
	}

	static BufferedReader abrirArchivo() throws Exception {
		String ff = archivoCostoStock;
		File f = new File(ff);
		BufferedReader entrada = new BufferedReader(new FileReader(f));
		return entrada;
	}

	static String[] parserLinea(String linea) {
		// divide la línea, que es un String, en todas las columnas que hay (o
		// campos).
		// En este caso, considera "," como separador
		String[] dato = linea.split("\t");
		return dato;
	}

	// ========================================================

	public static void migrarDatos() throws Exception {

		setingInicial();

		Hashtable<String, String> ubi = MigracionArticuloUbicacion.ubicas;

		// Deposito dep1 = new Deposito();
		// dep1.setDescripcion("S1-D1");
		// dep1.setObservacion("Depósito Central");
		// rr.saveObject(dep1, "Migra"); if (1 ==1){ return; }
		// if (1 ==1){ return; }

		// buscar el deposito
		Deposito dep = (Deposito) rr.getObject(Deposito.class.getName(),
				(long) 1);
		System.out.println(dep);

		String linea = "";

		BufferedReader archivo = abrirArchivo();

		linea = archivo.readLine();

		int i = 0;
		while (archivo.ready() == true) {

			linea = archivo.readLine();
			String[] datos = parserLinea(linea);

			String X1 = getDato("x1", datos);
			String IDARTICULO = getDato("IDARTICULO", datos);

			if (X1.trim().compareTo("1") == 0) {
				i++;
				// System.out.println(i + ") " + IDARTICULO + " - " + COSTOUS +
				// " - "+ COSTOGS + " - " + STOCK);

				// buscar el artículo
				Articulo art = rr.getArticuloByCodigoInterno(IDARTICULO);
				if (art == null) {
					System.out.println("" + IDARTICULO);
				} else {
					System.out.println(i+"   " + IDARTICULO);
					double COSTOUS = getDatoDouble("COSTOUS", datos);
					double COSTOGS = getDatoDouble("COSTOGS", datos);
					long STOCK = (long) getDatoDouble("STOCK", datos);

					// == deposito
					ArticuloDeposito artDep = new ArticuloDeposito();
					artDep.setDeposito(dep);
					artDep.setArticulo(art);
					//artDep.setCosto(COSTOGS);
					artDep.setStock(STOCK);
					// buscar su ubicacion
					String ubicacion = ubi.get(IDARTICULO);
					if (ubicacion == null) {
						ubicacion = "";
						// System.out.println("No hay ubicacion para: "+IDARTICULO);
					}
					artDep.setUbicacion(ubicacion);
					grabar(artDep);

					// == costo
					ArticuloCosto artCos = new ArticuloCosto();
					//artCos.setArticuloDep(artDep);
					artCos.setCostoFinalGs(COSTOGS);
					artCos.setCostoFinalDs(COSTOUS);
					artCos.setFechaCompra(new Date());

					grabar(artCos);

					// == stock
					ArticuloStock artStk = new ArticuloStock();
					artStk.setArticuloDep(artDep);
					artStk.setCantidad(STOCK);
					artStk.setFechaMovimiento(new Date());
					grabar(artStk);

				}

			}

		}
		grabar(dep);

	}

	public static void main(String[] args) throws Exception {
		migrarDatos();
	}

}
