package com.yhaguy.util.population;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.hibernate.FlushMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.coreweb.util.Ruc;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RucSet;

//org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider
//org.hibernate.connection.C3P0ConnectionProvider

/**
 * Carga los RUC del SET Borra lo que hay y carga todos Recorre los archivos
 * 
 */
public class DBPopulationSET {

	Ruc rucV = new Ruc();
	
	static long iniTime = 0;
	static long finTime = 0;

	static String folder = "./WEB-INF/docs/BD-SET/";

	static String[] arsNo = { "ruc0.txt" };
	static String[] ars = { "ruc0.txt", "ruc1.txt", "ruc2.txt", "ruc3.txt",
			"ruc4.txt", "ruc5.txt", "ruc6.txt", "ruc7.txt", "ruc8.txt",
			"ruc9.txt" };

	static long nRucs = 0;
	static long nRucsProblemas = 0;

	RegisterDomain rr = RegisterDomain.getInstance();
	Session session;

	public void dropTable() throws Exception {

		// rr.deleteAllObjects(RucSet.class.getName());
		
		String delTablas = "DROP TABLE IF EXISTS " +
				"rucset " +
				"CASCADE";

		rr.sql2(delTablas);
		rr.resetTables();
		
	}

	public String[] parseLinea(String linea) throws Exception {
		String[] dato = linea.split("\\|");
		return dato;
	}

	public String[] parseLineaOld(String linea) throws Exception {
		String[] out = new String[4];
		char[] lc = linea.toCharArray();
		int p = 0;

		try {

			String str = "";
			for (int i = 0; i < lc.length; i++) {
				char c = lc[i];
				if (c == '|') {
					boolean error = false;
					if ((p == 0) || (p == 2)) {
						// debe ser un numero
						try {
							long l = Long.parseLong(str);
						} catch (Exception e) {
							error = true;
						}
					}

					if (error == false) {
						out[p] = str;
						str = "";
						p++;
					}
				} else {
					str += c;
				}
			}
		} catch (Exception e) {
			// System.out.println("parser problema: " + linea);
			// out = new String[] {"----", linea, "-", ""};
			throw new Exception("parser problema: " + linea);
		}

		if (p != 4) {
			// System.out.println("p != 4: ["+p+"]  " +linea);
			throw new Exception("p != 4: [" + p + "]  " + linea);
		}

		return out;
	}

	public void cargaRucSet(String txt, int nAr) throws Exception {
		// 0 1 2 3
		// 1000060|BENITEZ CENTURION, CARLOS ADAN|7|BECC623200D|

		this.session = rr.SESSIONgetSession();
		Transaction tx = this.session.beginTransaction();

		System.out.println("Leyendo archivo: " + txt);

		File f = new File(folder + txt);
		BufferedReader entrada;

		entrada = new BufferedReader(new FileReader(f));
		String linea = "";

		int p = 0;

		while (entrada.ready()) {
			p++;
			linea = (entrada.readLine()).trim();

			try {
				String[] dato = parseLinea(linea);
				String ruc = dato[0] + "-" + dato[2];
				String raSo = dato[1];
				String rucOld = dato[3];

				if (rucV.validarRuc(ruc) == false){
					System.out.println("RUC falso: "+linea);
				}else{
					RucSet rs = new RucSet();
					rs.setRuc(ruc);
					rs.setRazonSocial(raSo);
					rs.setRucOld(rucOld);

					rr.SESSIONsaveObjectDomain(rs, this.session, "SYS");
				}
				
				if (p % 100 == 0) {

					this.session.flush();
					this.session.clear();
				}

				nRucs++;

			} catch (Exception e) {
				nRucsProblemas++;
				System.out
						.println(txt + " " + e.getMessage() + "     " + linea);
			}

		}
		System.out.println("... commit");

		tx.commit();
		entrada.close();

		rr.SESSIONcloseSession(this.session);

		finTime = System.currentTimeMillis();
		System.out.println("Tiempo:" + (finTime - iniTime));

	}

	public void cargaAllRuc() throws Exception {

		for (int i = 0; (i < ars.length); i++) {
			// for (int i = 0; (i < 1); i++) {
			String ff = ars[i];

			// RunThread th = new RunThread(ff, i);
			// th.start();
			cargaRucSet(ff, i);
		}
		long ceroTime = System.currentTimeMillis();

	}

	public static void main(String[] args) throws Exception {

		/*
		 * String linea = "1023840|VALDEZ, MIGUEL ANGEL|9|VAMI6321602|";
		 * DBPopulationSET dbx = new DBPopulationSET(); String[] d =
		 * dbx.parseLinea(linea); for (int i = 0; i < d.length; i++) {
		 * System.out.println(i+") "+d[i]); }
		 * 
		 * if (1 == 1){ return; }
		 */

		long ceroTime = System.currentTimeMillis();
		DBPopulationSET db = new DBPopulationSET();
		System.out.println("... borrando tabla RucSet ...");
		db.dropTable();
		

		iniTime = System.currentTimeMillis();

		db.cargaAllRuc();

		finTime = System.currentTimeMillis();
		System.out.println("Tiempo:" + ((finTime - iniTime) / 1000));

		System.out.println("----- FIN POPULATION ------\n" + "Rucs: " + nRucs
				+ "\n" + "Rucs problemas:" + nRucsProblemas + "\n"
				+ "Drop DB: " + (iniTime - ceroTime) + "\n" + "Cargar BD:"
				+ (finTime - iniTime));

	}

}

class RunThread extends Thread {

	DBPopulationSET set;
	String ff;
	int i;

	public RunThread(String ff, int i) {
		this.ff = ff;
		this.i = i;
		this.set = new DBPopulationSET();
	}

	public void run() {
		try {
			this.set.cargaRucSet(this.ff, this.i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
