package com.yhaguy.util.migracion.baterias;

import java.util.HashMap;
import java.util.Map;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.RegisterDomain;

public class BAT_Costos {

	static RegisterDomain rr = RegisterDomain.getInstance();
	
	static String src = "./WEB-INF/docs/Baterias/Costos.csv";
	
	static String[][] cab = { { "Empresa", CSV.STRING } };
	static String[][] det = { { "CODIGO", CSV.STRING }, { "COSTO", CSV.STRING } };
	
	public void poblarCostos() throws Exception {
		CSV csv = new CSV(cab, det, src);
		csv.start();
		
		while (csv.hashNext()) {
			
			String codigo = csv.getDetalleString("CODIGO");
			//double costo = Double.parseDouble(csv.getDetalleString("COSTO").replace(".", "").replace(",", ""));
			
			Articulo art = rr.getArticulo(codigo);
			if (art != null) {
				ArticuloDeposito adp = rr.getArticuloDeposito(art.getId(), 2);
				
				if (adp != null) {
					//adp.setCosto(costo);
					rr.saveObject(adp, "sys");
					
					System.out.println("encontrado: " + codigo);
				} else {
					System.err.println("no encontrado adp: " + codigo);
				}
			} else {
				System.err.println("no encontrado: " + codigo);
			}
						
		}
	}	
	
	public static Map<String, Double> getCostosOld() throws Exception {
		Map<String, Double> out = new HashMap<String, Double>();
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		
		while (csv.hashNext()) {			
			String codigo = csv.getDetalleString("CODIGO");
			double costo = Double.parseDouble(csv.getDetalleString("COSTO").replace(".", "").replace(",", ""));
			out.put(codigo, costo);									
		}
		
		return out;
	}
	
	public static void main(String[] args) {
		try {
			BAT_Costos test = new BAT_Costos();
			test.poblarCostos();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
