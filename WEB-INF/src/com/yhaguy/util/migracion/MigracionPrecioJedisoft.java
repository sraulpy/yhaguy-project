package com.yhaguy.util.migracion;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.domain.ArticuloPrecioJedisoft;
import com.yhaguy.domain.RegisterDomain;

public class MigracionPrecioJedisoft {

	static RegisterDomain rr = RegisterDomain.getInstance();
	
	static String src = "./WEB-INF/docs/MRA/PRECIOS-JEDISOFT.csv";
	
	String[][] cab = { { "Empresa", CSV.STRING } };
	String[][] det = { { "IDPLANVENTA", CSV.STRING },
			{ "IDARTICULO", CSV.STRING }, { "PRECIO", CSV.STRING },
			{ "MINIMO", CSV.STRING } };
	
	public void poblarPrecios() throws Exception {
		CSV csv = new CSV(cab, det, src);
		csv.start();
		
		while (csv.hashNext()) {
			
			long idplanventa = Long.parseLong(csv.getDetalleString("IDPLANVENTA"));
			String codigo = csv.getDetalleString("IDARTICULO");
			double precio = Double.parseDouble(csv.getDetalleString("PRECIO").replace(".", "").replace(",", "."));
			double preciominimo = Double.parseDouble(csv.getDetalleString("MINIMO").replace(".", "").replace(",", "."));
			
			System.out.println(idplanventa + " - " + codigo + " - " + precio + " - " + preciominimo);
			ArticuloPrecioJedisoft art = new ArticuloPrecioJedisoft();
			art.setIdPlanVenta(idplanventa);
			art.setCodigoInterno(codigo);
			art.setPrecio(precio);
			art.setPrecioMinimo(preciominimo);
			rr.saveObject(art, "sys");
		}
	}	
	
	public static void main(String[] args) {
		try {
			MigracionPrecioJedisoft test = new MigracionPrecioJedisoft();
			test.poblarPrecios();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
