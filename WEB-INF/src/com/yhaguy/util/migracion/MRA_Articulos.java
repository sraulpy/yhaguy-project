package com.yhaguy.util.migracion;

import java.util.HashMap;
import java.util.Map;

import com.coreweb.extras.csv.CSV;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloPrecioReferencia;
import com.yhaguy.domain.ArticuloUbicacion;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.population.DBPopulationTipos;

public class MRA_Articulos {
	
	/**
	 * El listado que debe sacarse del Sistema C.R. se llama
	 * listado de Articulos por proveedor (Valorizado) ('ALT+F2' para copiarlo al servidor)
	 * El listado de ubicaciones es el nro 3
	 */
	
	static RegisterDomain rr = RegisterDomain.getInstance();
	
	static String src = "./WEB-INF/docs/MigracionMariano/Articulos/articulos.csv";
	static String srcUbi = "./WEB-INF/docs/MigracionMariano/Articulos/ubicaciones.csv";
	static String srcCods = "./WEB-INF/docs/MigracionMariano/Articulos/codigos.csv";

	String[][] cab = { { "Empresa", CSV.STRING } };
	String[][] det = { { "CODIGO", CSV.STRING },
			{ "DESCRIPCION", CSV.STRING }, { "COSTO", CSV.NUMERICO },
			{ "PRECIO", CSV.NUMERICO }, { "STOCK", CSV.NUMERICO } };
	
	String[][] detUbi = {{"CODIGO", CSV.STRING}, {"UBICACION", CSV.STRING}};
	String[][] detCods = { { "CODIGO", CSV.STRING },
			{ "CODPROVEEDOR", CSV.STRING }, { "CODORIGINAL", CSV.STRING } };
	
	/**
	 * @return y persiste las ubicaciones..
	 */
	private Map<String, ArticuloUbicacion> getUbicaciones() throws Exception {

		Map<String, ArticuloUbicacion> out = new HashMap<String, ArticuloUbicacion>();
		RegisterDomain rr = RegisterDomain.getInstance();
		CSV csv = new CSV(cab, detUbi, srcUbi);

		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("CODIGO");
			String ubicacion = csv.getDetalleString("UBICACION");
			if ((this.isUbicacionValida(ubicacion) == true)
					&& (this.isUbicacionDuplicada(ubicacion) == false)) {
				String[] ubi = this.getUbicacion(ubicacion);
				ArticuloUbicacion ub = new ArticuloUbicacion();
				ub.setEstante(ubi[0]);
				ub.setFila(ubi[1]);
				ub.setColumna(ubi[2]);
				rr.saveObject(ub, "migracion");
				out.put(codigo, ub);
			}
		}
		return out;
	}
	
	/**
	 * @return los codigos..
	 */
	private Map<String, String[]> getCodigos() throws Exception {
		Map<String, String[]> out = new HashMap<String, String[]>();
		CSV csv = new CSV(cab, detCods, srcCods);
		csv.start();
		while (csv.hashNext()){
			String codigo = csv.getDetalleString("CODIGO");
			String proveedor = csv.getDetalleString("CODPROVEEDOR");
			String original = csv.getDetalleString("CODORIGINAL");
			out.put(codigo, new String[]{proveedor, original});
		}
		return out;
	}
	
	/**
	 * @return si el formato de ubicacion es valido
	 */
	private boolean isUbicacionValida(String ubicacion){
		if(ubicacion.trim().length() != 5)
			return false;		
		String[] out = ubicacion.replace(".", "-").split("-");
		return out.length == 3;
	}
	
	/**
	 * @return 
	 */
	private boolean isUbicacionDuplicada(String ubicacion) throws Exception {
		String[] ubi = this.getUbicacion(ubicacion);
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getUbicacion(ubi[0], ubi[1], ubi[2]).size() > 0;
	}
	
	
	/**
	 * @return estante, fila, colummna
	 */
	private String[] getUbicacion(String ubicacion) {		
		return ubicacion.replace(".", "-").split("-");
	}
	
	/**
	 * pobla los articulos..
	 */
	public void poblarArticulos(DBPopulationTipos tt, Deposito[] deps) throws Exception {
		
		System.out.println("--------------------- Obteniendo Ubicaciones --------------------");
		Map<String, ArticuloUbicacion> ubicaciones = this.getUbicaciones();
		
		System.out.println("--------------------- Obteniendo Codigos ----------------------");
		Map<String, String[]> codigos = this.getCodigos();

		System.out.println("---------------------- Poblando Articulos ----------------------");

		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");
			String[] cods = codigos.get(codigo);
			String codProveedor = cods == null? "" : cods[0];
			String codOriginal = cods == null? "" : cods[1];
			String descripcion = csv.getDetalleString("DESCRIPCION");
			double costo = csv.getDetalleDouble("COSTO");
			double precio = csv.getDetalleDouble("PRECIO");
			int stock = ((Float)csv.getDetalle("STOCK")).intValue();
			ArticuloUbicacion ubicacion = ubicaciones.get(codigo);
			
			System.out.println(codigo + " - " + descripcion + " - " + costo
					+ " - " + precio + " - " + stock );
			
			Articulo artMra = new Articulo();
			artMra.setDescripcion(descripcion);
			artMra.setCodigoInterno(codigo);
			artMra.setCodigoProveedor(codProveedor);
			artMra.setCodigoOriginal(codOriginal);
			artMra.setCodigoBarra(codigo);
			artMra.setObservacion("");
			artMra.setArticuloMarca(tt.sinReferenciaTipo);
			artMra.setArticuloParte(tt.sinReferenciaTipo);
			artMra.setArticuloLinea(tt.sinReferenciaTipo);
			artMra.setArticuloFamilia(tt.sinReferenciaTipo);
			artMra.setArticuloEstado(tt.estadoArticulo1);
			artMra.setArticuloPresentacion(rr.getArticuloPresentacionById(1));
			artMra.setImportado(true);		
			if(ubicacion != null)
				artMra.getUbicaciones().add(ubicacion);
			rr.saveObject(artMra, "Migracion");
			
			ArticuloPrecioReferencia artPrec = new ArticuloPrecioReferencia();
			artPrec.setPrecio(100000);
			artPrec.setDescripcion("Precio Generico");
			artPrec.setActivo(true);
			artPrec.setMoneda(tt.guarani);
			artPrec.setArticulo(artMra);
			rr.saveObject(artPrec, "Migracion");	
			
			for (Deposito deposito : deps) {
				ArticuloDeposito artDep = new ArticuloDeposito();
				artDep.setUbicacion("");
				artDep.setStock(stock);
				artDep.setStockMinimo(0);
				artDep.setStockMaximo(100000);
				artDep.setDeposito(deposito);
				artDep.setArticulo(artMra);
				rr.saveObject(artDep, "Migracion");
			}
		}

		System.out.println("---------------------- Fin Articulos ----------------------");
	}

	public static void main(String[] args) throws Exception {
		MRA_Articulos mr = new MRA_Articulos();
		Map<String, ArticuloUbicacion> ubis = mr.getUbicaciones();
		for (String key : ubis.keySet()) {
			System.out.println(ubis.get(key).getEstante());
		}
	}
}
