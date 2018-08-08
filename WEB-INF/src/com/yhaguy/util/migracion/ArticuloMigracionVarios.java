package com.yhaguy.util.migracion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.yhaguy.domain.Articulo;

public class ArticuloMigracionVarios extends ArticuloMigracion {

	static Hashtable<String, Set<String>> modeloMarca = new Hashtable<>();
	static Set<String> margaModeloCombinado = new HashSet<>();

	static void putModeloMarca(String modelo, String marca) {

		if ((modelo != null) && (marca != null)) {
			
			margaModeloCombinado.add(marca+" ** "+modelo);
			
			Set<String> ss = modeloMarca.get(modelo);
			if (ss == null) {
				HashSet<String> hs = new HashSet<>();
				modeloMarca.put(modelo, hs);
				ss = hs;
			}
			ss.add(marca);
		}
	}

	static void combinaModeloMarca(String idModelo, String idMarca) {
		String marca = "";
		String modelo = "";

		String ma = ArticuloMigracionDatos.marcaCSV.get(idMarca);
		String mo = ArticuloMigracionDatos.modeloCSV.get(idModelo);
		putModeloMarca(mo, ma);
	}

	static void marcaModelo() throws Exception {

		Session session = rr.SESSIONgetSession();
		Transaction tx = null;

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

			// puse null para que compile, pero hay que ver bien antes de usarlo
			if (esArticuloVenta(idArticulo, null) == true) {
				pReal++;
				//System.out.println(p + ")" + idArticulo + " - ");
				boolean siMarca = false;
				boolean siModelo = false;
				String idMarca = getColArt("IDCOLOR", dato);
				String idModelo = getColArt("IDMEDIDA", dato);

				// Combina modelo marca
				combinaModeloMarca(idModelo, idMarca);

			} else {
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

	// para ver si hay mayores de 1
	public static void verModeloMarca() throws Exception {
		
		Enumeration<String> ks = modeloMarca.keys();
		while (ks.hasMoreElements()) {
			String k = (String) ks.nextElement();
			Set<String> s = modeloMarca.get(k);
			if (s.size() > 1){
				System.out.println(k +" ; " + s);
			}
			
		}
		
	}

	public static void printMarcaModelo(){
		ArrayList l = new ArrayList( margaModeloCombinado);
		Collections.sort(l);
		System.out.println("\n\n\n\n");
		for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			System.out.println(s);
		}
		
	}
	public static void main(String[] args) throws Exception {

		marcaModelo();
		verModeloMarca();

		printMarcaModelo();
	}
}
