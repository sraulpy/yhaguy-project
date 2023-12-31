package com.yhaguy.gestion.comun;

import java.util.Date;
import java.util.List;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloCosto;
import com.yhaguy.domain.ArticuloCostoPromedio;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.util.Utiles;

public class ControlArticuloCosto {

	/**
	 * crea un movimiento de costo de articulo..
	 */
	public static void addMovimientoCosto(long idArticulo, double costoGs,
			Date fecha, long idMovimiento, long idTipoMovimiento, String user) throws Exception {
		if (fecha == null) {
			fecha = new Date();
		}
		if (costoGs > 10) {
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
			
			ArticuloCostoPromedio prom = new ArticuloCostoPromedio();
			prom.setArticulo(art);
			prom.setCostoFinalGs(costoGs);
			prom.setFechaCompra(fecha);
			prom.setIdMovimiento(idMovimiento);
			prom.setTipoMovimiento(tm);
			rr.saveObject(prom, user);
		}		
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
	 * el precio de venta segun su margen..
	 */
	public static double getPrecioVenta_(long idArticulo, long idListaPrecio) throws Exception {
		double out = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		Object[] art = rr.getArticulo_(idArticulo);
		if(art != null) {
			if (idListaPrecio == ArticuloListaPrecio.ID_LISTA) out = (double) art[2];
			if (idListaPrecio == ArticuloListaPrecio.ID_MINORISTA) out = (double) art[3];
			if (idListaPrecio == ArticuloListaPrecio.ID_MAYORISTA_GS) out = (double) art[4];
			if (idListaPrecio == ArticuloListaPrecio.ID_MAYORISTA_DS) out = (double) art[5];
			if (idListaPrecio == ArticuloListaPrecio.ID_TRANSPORTADORA) out = (double) art[7];
			if (idListaPrecio == ArticuloListaPrecio.ID_IMP_BATERIAS) out = (double) art[8];
			if (idListaPrecio == ArticuloListaPrecio.ID_PROMOCION) out = (double) art[9];
			if (idListaPrecio == ArticuloListaPrecio.ID_MAYORISTA_CONTADO) out = (double) art[10];
		}
		return out;
	}
	
	/**
	 * el costo promedio del articulo..
	 */
	public static double getCostoPromedio(long idArticulo, Date fecha) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> costos = rr.getArticuloCostos_(idArticulo, fecha);		
		double suma = 0; 
		double cant = 0;		
		for (Object[] costo : costos) {
			double costoFinalGs = (double) costo[1];
			suma += costoFinalGs;
			cant ++;
		}
		return suma == 0 ? 0 : Utiles.getRedondeo(suma / cant);
	}
	
	/**
	 * el costo promedio del articulo..
	 */
	public static double getCostoPromedio_(long idArticulo, Date fecha) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> costos = rr.getArticuloCostosPromedio(idArticulo, fecha);		
		double suma = 0; 
		double cant = 0;		
		for (Object[] costo : costos) {
			double costoFinalGs = (double) costo[1];
			suma += costoFinalGs;
			cant ++;
		}
		return suma == 0 ? 0 : Utiles.getRedondeo(suma / cant);
	}
	
	/**
	 * el costo ultimo del articulo..
	 */
	public static double getCostoUltimoGs(long idArticulo, Date fecha) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return Utiles.getRedondeo(rr.getCostoUltimo(idArticulo, fecha));
	}
	
	/**
	 * el costo ultimo del articulo..
	 */
	public static double getCostoPromedioGs(long idArticulo, Date fecha) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return Utiles.getRedondeo(rr.getCostoPromedio(idArticulo, fecha));
	}
	
	/**
	 * actualiza el precio del articulo..
	 */
	public static void actualizarPrecio(long idArticulo, double mayoristaGs, double minoristaGs, double listaGs,
			double transportadoraGs, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloById(idArticulo);
		if (art != null) {
			art.setPrecioGs(mayoristaGs);
			art.setPrecioMinoristaGs(minoristaGs);
			art.setPrecioListaGs(listaGs);
			art.setPrecioTransportadora(transportadoraGs);
			rr.saveObject(art, user);
		}
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(ControlArticuloCosto.getPrecioVenta(158518, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
