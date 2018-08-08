package com.yhaguy.util.migracion;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import com.coreweb.util.Misc;

public class ClientesMigraPrioridad {

	private static Misc m = new Misc();
	private static Map<String, Double> cli = new Hashtable<String, Double>();
	private static Map<String, Integer> cliPos = new Hashtable<String, Integer>();
	private static Map<String, String> cliNombre = new Hashtable<String, String>();

	static String archOrdenado = "./WEB-INF/docs/migracion/2013-clientesOrdanados.txt";
	static String ventasCSV = "./WEB-INF/docs/migracion/2013-todo-venta.csv";

	public static int getPrioridad(String idPersona){
		int p = 999999;
		Integer i = cliPos.get(idPersona);
		if (i != null){
			p = i.intValue();
		}
		return p;
	}
	
	public static void prioridadCliente(boolean  print) throws Exception {

		long p = 0;
		boolean vuelta = true;

		String csv = ventasCSV;

		File f = new File(csv);
		BufferedReader entrada;

		entrada = new BufferedReader(new FileReader(f));
		String linea = "";
		// leer la cabecera
		linea = entrada.readLine() + " ";

		while (entrada.ready() && vuelta) {
			p++;
			linea = entrada.readLine() + " ";
			String[] dato = linea.split("\t");

			// para saber que todos los ID de personas son numerico (no hay
			// desfasaje)
			long dd = Long.parseLong(dato[6]);
			if (dato.length != 8) {
				System.out.println(p + " - " + linea);
			}

			// tratar datos
			String idCliente = dato[6]; //"["+dato[6]+"] "+dato[5];
			String clienteNombre = dato[7]; 
			
			cliNombre.put(idCliente, clienteNombre);
			
			double valor = Double.parseDouble(dato[3]);

			double acu = 0;
			Object va = cli.get(idCliente);
			if (va != null) {
				acu = (double) va;
			}
			acu += valor;
			cli.put(idCliente, acu);

			// System.out.println(p+") "+ m.formato(idCliente, 8) + "   " +
			// m.formatoNumero(acu));

			// System.out.println(p + ") " + dato[3] + " - " + dato[6] + " - " +
			// dato[7]);

			if (p > 100) {
				// vuelta = false;
			}

		}

		// ordernar
		System.out.println(".... ordernado...");
		Map<String, Double> sorted = sortByValues(cli);
		// System.out.println("Sorted Map in Java by values: " + sorted);

		StringBuffer texto = new StringBuffer();
		int posicion = 1;
		List<String> keys = new ArrayList<String>(sorted.keySet());
		for (String key: keys) {
			cliPos.put(key, posicion);
			String nombre = cliNombre.get(key);
			if (print == true){
				String lin = posicion+"-"+m.formato(key+"-"+nombre,28) + ": " +  m.formatoNumeroBig(sorted.get(key), false);
				texto.append(lin+"\n");
			    System.out.println(lin);
			}
			posicion++;
		}
		
		m.grabarStringToArchivo(archOrdenado, texto.toString());
		if (print == true){
			System.out.println("Cantidad de clientes: "+keys.size());
		}
		
		
	}

	public static void main(String[] args) throws Exception {
		
		prioridadCliente(true);

	}

	/*
	 * Java method to sort Map in Java by value e.g. HashMap or Hashtable throw
	 * NullPointerException if Map contains null values It also sort values even
	 * if they are duplicates
	 */
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(
			Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return (int) (o1.getValue().compareTo(o2.getValue()) * -1);
			}
		});

		// LinkedHashMap will keep the keys in the order they are inserted
		// which is currently sorted on natural ordering
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

}