package com.yhaguy.gestion.comun;

import java.util.Date;

import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloStock;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;


public class ControlArticuloStock {
	
	/**
	 * agrega un movimiento al historial de stock..
	 */
	public static void addMovimientoStock(long idMovimiento,
			long idTipoMovimiento, long cantidad, long idArticuloDeposito,
			String user) throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		TipoMovimiento tipomov = rr.getTipoMovimientoById(idTipoMovimiento);
		ArticuloDeposito artdep = rr.getArticuloDepById(idArticuloDeposito);

		ArticuloStock stock = new ArticuloStock();
		stock.setIdMovimiento(idMovimiento);
		stock.setTipoMovimiento(tipomov);
		stock.setArticuloDep(artdep);
		stock.setCantidad(cantidad);
		stock.setFechaMovimiento(new Date());

		rr.saveObject(stock, user);
	}
	
	/**
	 * agrega un movimiento al historial de stock..
	 */
	public static void addMovimientoStock(long idMovimiento,
			long idTipoMovimiento, long cantidad, long idArticulo, long idDeposito,
			String user) throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		TipoMovimiento tipomov = rr.getTipoMovimientoById(idTipoMovimiento);
		ArticuloDeposito artdep = rr.getArticuloDeposito(idArticulo, idDeposito);

		ArticuloStock stock = new ArticuloStock();
		stock.setIdMovimiento(idMovimiento);
		stock.setTipoMovimiento(tipomov);
		stock.setArticuloDep(artdep);
		stock.setCantidad(cantidad);
		stock.setFechaMovimiento(new Date());

		rr.saveObject(stock, user);
	}
	
	/**
	 * actualiza el stock del articulo segun deposito..
	 */
	public static void actualizarStock(long idArticulo, long idDeposito,
			long cantidad, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloDeposito art = rr.getArticuloDeposito(idArticulo, idDeposito);
		if (art == null) {
			art = crearArticuloDeposito(idArticulo, idDeposito, user);
		}
		art.setStock(art.getStock() + cantidad);
		rr.saveObject(art, user);
	}
	
	/**
	 * actualiza el stock del articulo deposito..
	 */
	public static void actualizarStock(long idArticuloDep, long cantidad, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloDeposito art = rr.getArticuloDepById(idArticuloDep);
		art.setStock(art.getStock() + cantidad);
		rr.saveObject(art, user);
	}
	
	/**
	 * crea un articulo deposito..
	 */
	public static ArticuloDeposito crearArticuloDeposito(long idArticulo, long idDeposito,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloById(idArticulo);
		Deposito dep = (Deposito) rr.getObject(Deposito.class.getName(),
				idDeposito);
		ArticuloDeposito ad = new ArticuloDeposito();
		ad.setArticulo(art);
		ad.setDeposito(dep);
		ad.setStockMaximo(0);
		ad.setStockMinimo(0);
		rr.saveObject(ad, user);
		return ad;
	}
}
