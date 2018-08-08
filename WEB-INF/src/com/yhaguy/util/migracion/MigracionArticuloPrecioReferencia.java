package com.yhaguy.util.migracion;

import com.coreweb.domain.Tipo;
import com.coreweb.extras.csv.CSV;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloPrecioReferencia;
import com.yhaguy.domain.RegisterDomain;

public class MigracionArticuloPrecioReferencia {
	
	static final String SRC = "./WEB-INF/docs/migracion/ArticuloPrecioReferencia.csv";

	/**
	 * Precios de referencia..
	 */
	
	String[][] cab = {{"Empresa", CSV.STRING}};
	String[][] det = {{"CODIGOINTERNO", CSV.STRING}, {"PRECIO", CSV.NUMERICO}};
	
	/**
	 * Asigna el precio de referencia al articulo..
	 */
	public void asignarPrecioDeReferencia() throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();
		CSV csv = new CSV(cab, det, SRC, '\t');	
		
		csv.start();
		while (csv.hashNext()) {
			
			String codigo = csv.getDetalleString("CODIGOINTERNO");
			Double precio = csv.getDetalleDouble("PRECIO");
			
			Tipo moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
			Articulo articulo = rr.getArticuloByCodigoInterno(codigo);
			
			if (articulo != null) {
				ArticuloPrecioReferencia apr = new ArticuloPrecioReferencia();
				apr.setActivo(true);
				apr.setArticulo(articulo);
				apr.setMoneda(moneda);
				apr.setPrecio(precio);
				apr.setDescripcion("Migracion inicial..");
				rr.saveObject(apr, "Migracion");
				System.out.println("Articulo: " + articulo.getCodigoInterno() + " - Precio: " + precio);
			} else {
				System.err.println("Código no encontrado: " + codigo);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		MigracionArticuloPrecioReferencia mig = new MigracionArticuloPrecioReferencia();
		mig.asignarPrecioDeReferencia();
	}
}
