package com.yhaguy.gestion.comun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloStock;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.util.Utiles;


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
			long cantidad, String user, boolean actualizar) throws Exception {
		if (!actualizar) return;
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
	
	/**
	 * recalcula el stock segun deposito..
	 */
	public static void recalcularStock(long idArticulo, long idDeposito, String user) throws Exception {
		List<Object[]> historial = getHistorialMovimientos(idArticulo, idDeposito, 0, true, new Date(), false);
		String stockHistorial_ = historial.size() > 0 ? (String) historial.get(historial.size() - 1)[7] : "0";
		long stockHistorial = Long.parseLong(stockHistorial_);
		RegisterDomain rr = RegisterDomain.getInstance();
		
		ArticuloDeposito adp = rr.getArticuloDeposito(idArticulo, idDeposito);
		adp.setStock(stockHistorial);
		rr.saveObject(adp, user);
	}
	
	/**
	 * @return historial de movimientos del articulo..
	 * [0]: fecha
	 * [1]: hora
	 * [2]: numero
	 * [3]: concepto
	 * [4]: dep
	 * [5]: entrada
	 * [6]: salida
	 * [7]: saldo
	 * [8]: importe 
	 */
	public static List<Object[]> getHistorialMovimientosGenerico(long idArticulo, long idSucursal, Date hasta) throws Exception {		
		String campoFecha = "fechaVolcado";		
		Date desde = Utiles.getFechaInicioOperaciones();
		return ControlArticuloStock.getHistorialMovimientosGenerico(idArticulo, idSucursal, desde, hasta, campoFecha);
	}
	
	/**
	 * @return historial de movimientos del articulo..
	 * [0]: fecha
	 * [1]: hora
	 * [2]: numero
	 * [3]: concepto
	 * [4]: dep
	 * [5]: entrada
	 * [6]: salida
	 * [7]: saldo
	 * [8]: importe 
	 */
	public static List<Object[]> getHistorialMovimientos(long idArticulo, long idDeposito, long idSucursal,
			boolean incluirDepositoVirtual, Date hasta, boolean valorizado) throws Exception {
		Date desde = Utiles.getFecha("05-10-2018 00:00:00");
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			desde = Utiles.getFecha("01-08-2016 00:00:00");
		}
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YMRA)) {
			desde = Utiles.getFecha("01-01-2016 00:00:00");
		}
		String campoFecha = "fechaVolcado";
		return ControlArticuloStock.getHistorialMovimientos(idArticulo, idDeposito, idSucursal, desde, hasta,
				incluirDepositoVirtual, campoFecha);
	}
	
	/**
	 * @return historial de movimientos del articulo..
	 * [0]: fecha
	 * [1]: hora
	 * [2]: numero
	 * [3]: concepto
	 * [4]: dep
	 * [5]: entrada
	 * [6]: salida
	 * [7]: saldo
	 * [8]: importe 
	 */
	public static List<Object[]> getHistorialMovimientos(long idArticulo, long idDeposito, long idSucursal, Date desde,
			Date hasta, boolean incluirDepositoVirtual, String campoFecha) throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo articulo = rr.getArticuloById(idArticulo);

		List<Object[]> data = new ArrayList<Object[]>();
		List<Object[]> historico;
		List<Object[]> historicoEntrada;
		List<Object[]> historicoSalida;

		List<Object[]> ventas = rr.getVentasPorArticulo(idArticulo, idDeposito, desde, hasta, incluirDepositoVirtual);
		List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(idArticulo, idDeposito, desde, hasta, incluirDepositoVirtual);
		List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(idArticulo, idDeposito, desde, hasta, incluirDepositoVirtual);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo_(idArticulo, idDeposito, desde, hasta, incluirDepositoVirtual);
		List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(idArticulo, idDeposito, desde, hasta, incluirDepositoVirtual, campoFecha);
		List<Object[]> transfs = rr.getTransferenciasPorArticulo(idArticulo, idDeposito, desde, hasta, true, incluirDepositoVirtual);
		List<Object[]> transfs_ = rr.getTransferenciasPorArticulo(idArticulo, idDeposito, desde, hasta, false, incluirDepositoVirtual);
		List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(idArticulo, idDeposito, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_POSITIVO, incluirDepositoVirtual);
		List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idArticulo, idDeposito, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO, incluirDepositoVirtual);
		List<Object[]> migracion = rr.getMigracionPorArticulo(articulo.getCodigoInterno(), desde, hasta, idDeposito);

		historicoEntrada = new ArrayList<Object[]>();
		historicoSalida = new ArrayList<Object[]>();
		
		historicoEntrada.addAll(migracion);
		historicoEntrada.addAll(ajustStockPost);
		historicoEntrada.addAll(ntcsv);
		historicoEntrada.addAll(compras);
		historicoEntrada.addAll(importaciones);
		
		for (Object[] movim : ajustStockNeg) {
			movim[3] = (int) movim[3] * -1;
		}
		
		historicoSalida.addAll(ajustStockNeg);
		historicoSalida.addAll(ventas);
		historicoSalida.addAll(ntcsc);
		historicoSalida.addAll(transfs_);

		for (Object[] movim : historicoEntrada) {
			movim[0] = "(+)" + movim[0];
		}
		
		for (Object[] movim : transfs) {
			movim[0] = "(+)" + movim[0];
		}

		historico = new ArrayList<Object[]>();
		historico.addAll(historicoEntrada);
		historico.addAll(historicoSalida);
		historico.addAll(transfs);

		// ordena la lista segun fecha..
		Collections.sort(historico, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha1.compareTo(fecha2);
			}
		});
				
		long saldo = 0;
		for (Object[] hist : historico) {
			boolean ent = ((String) hist[0]).startsWith("(+)");
			String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
			String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
			String numero = (String) hist[2];
			String concepto = ((String) hist[0]).replace("(+)", "");
			String entrada = ent ? hist[3] + "" : "";
			String salida = ent ? "" : hist[3] + "";
			String importe = Utiles.getNumberFormat((double) hist[4]);
			String dep = (String) hist[6];
			saldo += ent ? Long.parseLong(hist[3] + "") :  Long.parseLong(hist[3] + "") * -1;
			data.add(new Object[] { fecha, hora, numero, concepto, dep, entrada, salida, saldo + "", importe });
		}	
		return data;
	}
	
	/**
	 * @return historial de movimientos del articulo..
	 * [0]: fecha
	 * [1]: hora
	 * [2]: numero
	 * [3]: concepto
	 * [4]: dep
	 * [5]: entrada
	 * [6]: salida
	 * [7]: saldo
	 * [8]: importe 
	 */
	@SuppressWarnings("unchecked")
	public static List<Object[]> getHistorialMovimientosGenerico(long idArticulo, long idSucursal, Date desde,
			Date hasta, String campoFecha) throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo articulo = rr.getArticuloById(idArticulo);
		String codigo = articulo.getCodigoInterno();

		List<Object[]> data = new ArrayList<Object[]>();
		List<Object[]> historico;
		List<Object[]> historicoEntrada;
		List<Object[]> historicoSalida;
		
		historicoEntrada = (List<Object[]>) getHistoricoEntrada(idArticulo, codigo, desde, hasta, idSucursal)[0];
		historicoSalida = (List<Object[]>) getHistoricoSalida(idArticulo, codigo, desde, hasta, idSucursal)[0];

		for (Object[] movim : historicoEntrada) {
			movim[0] = "(+)" + movim[0];
		}

		historico = new ArrayList<Object[]>();
		historico.addAll(historicoEntrada);
		historico.addAll(historicoSalida);

		// ordena la lista segun fecha..
		Collections.sort(historico, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha1.compareTo(fecha2);
			}
		});
		
		long saldo = 0;
		for (Object[] hist : historico) {
			boolean ent = ((String) hist[0]).startsWith("(+)");
			String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YY);
			String hora = Utiles.getDateToString((Date) hist[1], "hh:mm");
			String numero = (String) hist[2];
			String concepto = ((String) hist[0]).replace("(+)", "");
			String entrada = ent ? hist[3] + "" : "";
			String salida = ent ? "" : hist[3] + "";
			String importe = Utiles.getNumberFormat((double) hist[4]);
			String dep = "";
			saldo += ent ? Long.parseLong(hist[3] + "") :  Long.parseLong(hist[3] + "") * -1;
			data.add(new Object[] { fecha, hora, numero, concepto, dep, entrada, salida, saldo + "", importe });
			System.out.println("--- " + concepto + " " + numero);
		}	
		return data;
	}
	
	/**
	 * recupera el historico de movimientos del articulo..
	 */
	public static Object[] getHistoricoEntrada(long idArticulo, String codigo, Date desde, Date hasta, long idSucursal) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		long total = 0;
		boolean fechaHora = true;
		boolean mra = Configuracion.empresa.equals(Configuracion.EMPRESA_YMRA);
		if (mra) idSucursal = SucursalApp.ID_MRA_MRA ;
		RegisterDomain rr = RegisterDomain.getInstance();
		
		List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticuloCosto(idArticulo, desde, hasta, fechaHora, idSucursal);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(idArticulo, desde, hasta, fechaHora, idSucursal);
		List<Object[]> importaciones = rr.getComprasImportacionPorArticuloFechaCierre(idArticulo, desde, hasta, fechaHora, idSucursal);
		List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(idArticulo, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_POSITIVO, fechaHora);
		List<Object[]> transfsOrigenMRA = rr.getTransferenciasPorArticuloOrigenMRA(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transfsOrigenCentral = rr.getTransferenciasPorArticuloOrigenCentral(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transfsOrigenDifInventario = rr.getTransferenciasPorArticuloOrigenDiferenciaInv2019(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transfsOrigenDifInventarioMRA = rr.getTransferenciasPorArticuloOrigenDiferenciaInvMRA2019(idArticulo, desde, hasta, fechaHora);
		List<Object[]> migracion = rr.getMigracionesPorArticulo(codigo, desde, hasta);
		
		out.addAll(migracion);
		out.addAll(ajustStockPost);		
		out.addAll(ntcsv);
		out.addAll(compras);
		out.addAll(importaciones);
		out.addAll(mra ? transfsOrigenCentral : transfsOrigenMRA);
		out.addAll(mra ? transfsOrigenDifInventarioMRA : transfsOrigenDifInventario);
		Object[] cierre = null;
		
		for (Object[] item : out) {
			String concepto = (String) item[0];
			if (!concepto.equals("FAC.COMPRA CREDITO")
					&& !concepto.equals("FAC.COMPRA CONTADO")
					&& !concepto.equals("FAC. IMPORTACIÓN CRÉDITO")
					&& !concepto.equals("FAC. IMPORTACIÓN CONTADO")
					&& !concepto.equals("NOTA DE CRÉDITO-VENTA")
					&& !(concepto.equals("AJUSTE STOCK POSITIVO"))
					&& !(concepto.equals("MIGRACION"))
					&& !concepto.equals("FAC. IMPORTACIÓN CRE.")
					&& !concepto.equals("FAC. IMPORTACIÓN CON.")
					&& !(concepto.equals("TRANSF. EXTERNA"))
					&& !(concepto.equals("TRANSF. INTERNA"))) {
				item[4] = 0.0;
			}
			total += Long.parseLong(item[3] + "");
		}
		// ordena la lista segun fecha..
		Collections.sort(out, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha1.compareTo(fecha2);
			}
		});
		
		return new Object[] { out, total, importaciones, cierre };
	}
	
	/**
	 * recupera el historico de movimientos del articulo..
	 */
	public static Object[] getHistoricoSalida(long idArticulo, String codigo, Date desde, Date hasta, long idSucursal) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		long total = 0;
		boolean fechaHora = true;
		boolean mra = Configuracion.empresa.equals(Configuracion.EMPRESA_YMRA);
		if (mra) idSucursal = SucursalApp.ID_MRA_MRA ;
		RegisterDomain rr = RegisterDomain.getInstance();		
		List<Object[]> ventas = rr.getVentasPorArticuloCosto(idArticulo, desde, hasta, fechaHora, idSucursal);
		List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(idArticulo, desde, hasta, fechaHora, idSucursal);
		List<Object[]> transfsDestinoMRA = rr.getTransferenciasPorArticuloDestinoMRA(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transfsDestinoCentral = rr.getTransferenciasPorArticuloDestinoCentral(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transfsDestinoDifInventario = rr.getTransferenciasPorArticuloDestinoDiferenciaInv2019(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transfsDestinoDifInventarioMRA = rr.getTransferenciasPorArticuloDestinoDiferenciaInvMRA2019(idArticulo, desde, hasta, fechaHora);
		List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idArticulo, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO, fechaHora);
		
		for (Object[] item : ajustStockNeg) {
			item[3] = (Long.parseLong(item[3] + "") * -1);
		}
		out.addAll(ventas);
		out.addAll(ntcsc);		
		out.addAll(mra ? transfsDestinoCentral : transfsDestinoMRA);
		out.addAll(mra ? transfsDestinoDifInventarioMRA : transfsDestinoDifInventario);
		out.addAll(ajustStockNeg);
		
		for (Object[] item : out) {
			total += Long.parseLong(item[3] + "");
		}
		// ordena la lista segun fecha..
		Collections.sort(out, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha1.compareTo(fecha2);
			}
		});
		
		return new Object[] { out, total };
	}
}
