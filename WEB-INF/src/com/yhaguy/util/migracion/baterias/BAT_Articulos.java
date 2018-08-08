package com.yhaguy.util.migracion.baterias;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloPrecioReferencia;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.population.DBPopulationTipos;

public class BAT_Articulos {
	
	static RegisterDomain rr = RegisterDomain.getInstance();
	
	static String src = "./WEB-INF/docs/MigracionBaterias/Articulos/articulos.csv";

	String[][] cab = { { "Empresa", CSV.STRING } };
	String[][] det = { { "CODIGO", CSV.STRING }, { "DESCRIPCION", CSV.STRING },
			{ "COD_PROVEEDOR", CSV.STRING }, { "COD_ORIGINAL", CSV.STRING },
			{ "MARCA", CSV.STRING }, { "PROVEEDOR", CSV.STRING }};
	
	/**
	 * pobla los articulos..
	 */
	public void poblarArticulos(DBPopulationTipos tt, Deposito[] deps) throws Exception {

		System.out.println("---------------------- Poblando Articulos ----------------------");

		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");
			String codProveedor = csv.getDetalleString("COD_PROVEEDOR");
			String codOriginal = csv.getDetalleString("COD_ORIGINAL");
			String descripcion = csv.getDetalleString("DESCRIPCION");
			
			System.out.println(codigo + " - " + descripcion );
			
			Articulo artBat = new Articulo();
			artBat.setDescripcion(descripcion);
			artBat.setCodigoInterno(codigo);
			artBat.setCodigoProveedor(codProveedor);
			artBat.setCodigoOriginal(codOriginal);
			artBat.setCodigoBarra(codigo);
			artBat.setObservacion("");
			artBat.setArticuloMarca(tt.sinReferenciaTipo);
			artBat.setArticuloParte(tt.sinReferenciaTipo);
			artBat.setArticuloLinea(tt.sinReferenciaTipo);
			artBat.setArticuloFamilia(tt.sinReferenciaTipo);
			artBat.setArticuloEstado(tt.estadoArticulo1);
			artBat.setArticuloPresentacion(rr.getArticuloPresentacionById(1));
			artBat.setImportado(true);
			rr.saveObject(artBat, "Migracion");
			
			ArticuloPrecioReferencia artPrec = new ArticuloPrecioReferencia();
			artPrec.setPrecio(100000);
			artPrec.setDescripcion("Precio Generico");
			artPrec.setActivo(true);
			artPrec.setMoneda(tt.guarani);
			artPrec.setArticulo(artBat);
			rr.saveObject(artPrec, "Migracion");	
			
			for (Deposito deposito : deps) {
				ArticuloDeposito artDep = new ArticuloDeposito();
				artDep.setUbicacion("");
				artDep.setStock(0);
				artDep.setStockMinimo(0);
				artDep.setStockMaximo(100000);
				//artDep.setCosto(0);
				artDep.setDeposito(deposito);
				artDep.setArticulo(artBat);
				rr.saveObject(artDep, "Migracion");
			}
		}

		System.out.println("---------------------- Fin Articulos ----------------------");
	}
}
