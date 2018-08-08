package com.yhaguy.gestion.comun;

import java.util.Date;
import java.util.List;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloCosto;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.util.Utiles;

public class ControlArticuloCosto {

	/**
	 * crea un movimiento de costo de articulo..
	 */
	public static void addMovimientoCosto(long idArticulo, double costoGs,
			Date fecha, long idMovimiento, long idTipoMovimiento, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		TipoMovimiento tm = rr.getTipoMovimientoById(idTipoMovimiento);
		Articulo art = rr.getArticuloById(idArticulo);
		ArticuloCosto costo = new ArticuloCosto();
		costo.setArticulo(art);
		costo.setCostoFinalGs(costoGs);
		costo.setFechaCompra(fecha);
		costo.setIdMovimiento(idMovimiento);
		costo.setTipoMovimiento(tm);
		rr.saveObject(costo, user);
	}
	
	
	/**
	 * actualiza el costo del articulo segun deposito..
	 */
	public static void actualizarCosto(long idArticulo,
			double costoGs, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloById(idArticulo);
		art.setCostoGs(costoGs);
		rr.saveObject(art, user);
	}
	
	/**
	 * el precio de venta segun su margen..
	 */
	public static double getPrecioVenta(double costo, int margen) throws Exception {
		int porcIva = Configuracion.VALOR_IVA_10;
		double iva = Utiles.obtenerValorDelPorcentaje(costo, porcIva);
		double ganancia = Utiles.obtenerValorDelPorcentaje(costo + iva, margen);
		double precio = Utiles.redondeoCuatroDecimales((costo + iva + ganancia));
		return precio;
	}
	
	/**
	 * el costo promedio del articulo..
	 */
	public static double getCostoPromedio(long idArticulo, Date fecha) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloCosto> costos = rr.getArticuloCostos(idArticulo, fecha);		
		double suma = 0; 
		double cant = 0;		
		for (ArticuloCosto costo : costos) {
			suma += costo.getCostoFinalGs();
			cant ++;
		}
		return suma == 0 ? 0 : Utiles.getRedondeo(suma / cant);
	}
	
	/**
	 * el costo ultimo del articulo..
	 */
	public static double getCostoUltimo(long idArticulo, Date fecha) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloCosto> costos = rr.getArticuloCostos(idArticulo, fecha);
		double costo = costos.size() > 0? costos.get(costos.size() - 1).getCostoFinalGs() : 0.0;
		return Utiles.getRedondeo(costo);
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(ControlArticuloCosto.getPrecioVenta(158518, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
