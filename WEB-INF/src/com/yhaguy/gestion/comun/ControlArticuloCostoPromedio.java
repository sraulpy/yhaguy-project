package com.yhaguy.gestion.comun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloCostoPromediogs;
import com.yhaguy.domain.ArticuloHistorialMigracion;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.ImportacionFactura;
import com.yhaguy.domain.ImportacionFacturaDetalle;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.TransferenciaDetalle;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.Utiles;

public class ControlArticuloCostoPromedio {
	
	public static final long ID_SUC_PRINCIPAL = 2;
	public static final long ID_DEP_1 = Configuracion.ID_DEPOSITO_PRINCIPAL;
	
	private SucursalApp selectedSucursal;	

	@SuppressWarnings("unused")
	private void testCostoPromedio() throws Exception {		
		Date desde = Utiles.getFecha("06-07-2021 00:00:00");
		Date hasta = Utiles.getFecha("30-08-2021 23:00:00");
		RegisterDomain rr = RegisterDomain.getInstance();
		
		this.selectedSucursal = rr.getSucursalAppById(ID_SUC_PRINCIPAL);
		
		List<Object[]> entradas = this.getHistoricoCompras(desde, hasta);
		
		for (Object[] compra : entradas) {
			String tipo = (String) compra[1];
			long id = (long) compra[0];
			
			double costoPromedio = 0;
			double costoUltimo = 0;
			String descripcion = "";
			Date fecha = null;
			
			if (tipo.equals("NOTA DE CRÉDITO-VENTA")) {
				this.addCostoPromedioNotaCredito(id);
			}
			
			if (tipo.equals("TRANSF. EXTERNA")) {
				this.addCostoPromedioTransferencia(id);
			}
			
			if (tipo.equals("FAC.COMPRA CREDITO") || tipo.equals("FAC.COMPRA CONTADO")) {
				this.addCostoPromedioCompralocal(id);
			}
			
			if ((tipo.equals("FAC. IMPORTACIÓN CRÉDITO") || tipo.equals("FAC. IMPORTACIÓN CONTADO"))
					|| (tipo.equals("FAC. IMPORTACIÓN CRE.") || tipo.equals("FAC. IMPORTACIÓN CON."))) {
				this.addCostoPromedioImportacion(id);
			}
			
			if (tipo.equals("AJUSTE STOCK POSITIVO")) {
				this.addCostoPromedioAjuste(id);
			}
		}
	}
	
	/**
	 * add costo promedio migracion
	 */
	public void addCostoPromedioMigracion(long id) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.selectedSucursal = rr.getSucursalAppById(ID_SUC_PRINCIPAL);
			String descripcion = "";
			Date fecha = null;
			ArticuloHistorialMigracion mg = (ArticuloHistorialMigracion) rr.getObject(ArticuloHistorialMigracion.class.getName(), id);			
			Articulo art = rr.getArticulo(mg.getCodigoInterno());
			if (art != null) {
				Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), mg.getFechaAlta());
				Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), mg.getFechaAlta());
				long totalEntradas = (long) ent[1];
				long totalSalidas = (long) sal[1];
				long stock = (totalEntradas) - totalSalidas;
				if (stock < 0) stock = 0;
				List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(mg.getFechaAlta(), -1));
				double ultPromedio = this.getCostoPromedio(compras, mg.getCosto());
				double ultCosto = this.getCostoUltimo(compras, mg.getCosto());
				double promedio = this.getCostoPromedioCalculado(stock, mg.getStock(), ultPromedio, mg.getCosto());				
				double costoPromedio = promedio;
				fecha = mg.getFechaAlta();
				descripcion = "MIGRACION:" + mg.getCodigoInterno();
				
				ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
				prm.setArticulo(art);
				prm.setCostoPromedio(costoPromedio);
				prm.setUltimoCosto(ultCosto);
				prm.setDescripcion(descripcion);
				prm.setFecha(fecha);
				rr.saveObject(prm, "sys");
				System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add costo promedio compra local
	 */
	public void addCostoPromedioCompralocal(long id) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.selectedSucursal = rr.getSucursalAppById(ID_SUC_PRINCIPAL);
			double costoPromedio = 0;
			String descripcion = "";
			Date fecha = null;
			CompraLocalFactura cf = rr.getCompraLocalFactura(id);
			for (CompraLocalFacturaDetalle item : cf.getDetalles()) {						
				Articulo art = item.getArticulo();
				Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), cf.getFechaCreacion());
				Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), cf.getFechaCreacion());
				long totalEntradas = (long) ent[1];
				long totalSalidas = (long) sal[1];
				long stock = (totalEntradas) - totalSalidas;
				if (stock < 0) stock = 0;
				List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(cf.getFechaCreacion(), -1));
				double ultPromedio = this.getCostoPromedio(compras, item.getCostoFinalGs());
				double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, item.getCostoFinalGs());
				item.setCostoPromedioGs(promedio);
				rr.saveObject(item, item.getUsuarioMod());	
				
				costoPromedio = promedio;
				fecha = cf.getFechaCreacion();
				descripcion = "COMPRA LOCAL:" + " " + cf.getNumero();
				
				ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
				prm.setArticulo(art);
				prm.setCostoPromedio(costoPromedio);
				prm.setUltimoCosto(item.getCostoFinalGs());
				prm.setDescripcion(descripcion);
				prm.setFecha(fecha);
				rr.saveObject(prm, "sys");
				System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add costo promedio importacion
	 */
	public void addCostoPromedioImportacion(long id) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.selectedSucursal = rr.getSucursalAppById(ID_SUC_PRINCIPAL);
			double costoPromedio = 0;
			String descripcion = "";
			Date fecha = null;
			ImportacionFactura cf = rr.getImportacionFacturaById(id);
			for (ImportacionFacturaDetalle item : cf.getDetalles()) {						
				Articulo art = item.getArticulo();
				if (art != null) {
					Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), cf.getFechaVolcado());
					Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), cf.getFechaVolcado());
					long totalEntradas = (long) ent[1];
					long totalSalidas = (long) sal[1];
					long stock = (totalEntradas) - totalSalidas;
					if (stock < 0) stock = 0;
					List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(cf.getFechaVolcado(), -1));
					double ultPromedio = this.getCostoPromedio(compras, item.getCostoFinalGs());
					double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, item.getCostoFinalGs());
					item.setCostoPromedioGs(promedio);
					rr.saveObject(item, item.getUsuarioMod());	
					
					costoPromedio = promedio;
					fecha = cf.getFechaVolcado();
					descripcion = "IMPORTACION:" + " " + cf.getNumero();
					
					ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
					prm.setArticulo(art);
					prm.setCostoPromedio(costoPromedio);
					prm.setUltimoCosto(item.getCostoFinalGs());
					prm.setDescripcion(descripcion);
					prm.setFecha(fecha);
					rr.saveObject(prm, "sys");
					System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);
				}					
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add costo promedio nota credito
	 */
	public void addCostoPromedioNotaCredito(long id) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.selectedSucursal = rr.getSucursalAppById(ID_SUC_PRINCIPAL);
			double costoPromedio = 0;
			String descripcion = "";
			Date fecha = null;
			NotaCredito nc = rr.getNotaCredito(id);
			if (nc.isVentaAplicadaCorrecta()) {
				for (NotaCreditoDetalle item : nc.getDetallesArticulos()) {
					Venta vta = nc.getVentaAplicada();					
					for (VentaDetalle det : vta.getDetalles()) {
						if (det.getArticulo().getId().longValue() == item.getArticulo().getId().longValue()) {						
							Articulo art = item.getArticulo();
							Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), nc.getFechaEmision());
							Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), nc.getFechaEmision());
							long totalEntradas = (long) ent[1];
							long totalSalidas = (long) sal[1];
							long stock = (totalEntradas) - totalSalidas;
							if (stock < 0) stock = 0;
							List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(nc.getFechaEmision(), -1));
							List<Object[]> comprasFechaVta = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(vta.getFecha(), -1));
							double promedioVta = this.getCostoPromedio(comprasFechaVta, det.getCostoUnitarioGs());
							double ultPromedio = this.getCostoPromedio(compras, det.getCostoUnitarioGs());
							double ultCosto = this.getCostoUltimo(compras, det.getCostoUnitarioGs());
							double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, promedioVta);
							item.setCostoPromedioGs(promedio);
							rr.saveObject(item, item.getUsuarioMod());
							
							costoPromedio = promedio;
							fecha = nc.getFechaEmision();
							descripcion = "NOTA CREDITO:" + " " + nc.getNumero();
							
							ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
							prm.setArticulo(art);
							prm.setCostoPromedio(costoPromedio);
							prm.setUltimoCosto(ultCosto);
							prm.setDescripcion(descripcion);
							prm.setFecha(fecha);
							rr.saveObject(prm, "sys");
							System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);
						}
					}
				}
			} else {
				// venta aplicada no correspondiente..
				for (NotaCreditoDetalle item : nc.getDetallesArticulos()) {						
					Articulo art = item.getArticulo();
					Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), nc.getFechaEmision());
					Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), nc.getFechaEmision());
					long totalEntradas = (long) ent[1];
					long totalSalidas = (long) sal[1];
					long stock = (totalEntradas) - totalSalidas;
					if (stock < 0) stock = 0;
					List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(nc.getFechaEmision(), -1));
					List<Object[]> comprasFechaVta = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(nc.getFechaEmision(), -1));
					double promedioVta = this.getCostoPromedio(comprasFechaVta, item.getCostoGs());
					double ultPromedio = this.getCostoPromedio(compras, item.getCostoGs());
					double ultCosto = this.getCostoUltimo(compras, item.getCostoGs());
					double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, promedioVta);
					item.setCostoPromedioGs(promedio);
					rr.saveObject(item, item.getUsuarioMod());
					
					costoPromedio = promedio;
					fecha = nc.getFechaEmision();
					descripcion = "NOTA CREDITO:" + " " + nc.getNumero();
					
					ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
					prm.setArticulo(art);
					prm.setCostoPromedio(costoPromedio);
					prm.setUltimoCosto(ultCosto);
					prm.setDescripcion(descripcion);
					prm.setFecha(fecha);
					rr.saveObject(prm, "sys");
					System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);					
				}				
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}					
	}
	
	public void addCostoPromedioAjuste(long id) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.selectedSucursal = rr.getSucursalAppById(ID_SUC_PRINCIPAL);
			double costoPromedio = 0;
			String descripcion = "";
			Date fecha = null;
			AjusteStock aj = (AjusteStock) rr.getObject(AjusteStock.class.getName(), id);
			for (AjusteStockDetalle item : aj.getDetalles()) {
				if (item.getCostoGs() > 0) {
					Articulo art = item.getArticulo();
					Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), aj.getFecha());
					Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), aj.getFecha());
					long totalEntradas = (long) ent[1];
					long totalSalidas = (long) sal[1];
					long stock = (totalEntradas) - totalSalidas;
					if (stock < 0) stock = 0;
					List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(aj.getFecha(), -1));
					double ultPromedio = this.getCostoPromedio(compras, item.getCostoGs());
					double ultCosto = this.getCostoUltimo(compras, item.getCostoGs());
					double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, item.getCostoGs());
					item.setCostoPromedioGs(promedio);
					rr.saveObject(item, item.getUsuarioMod());	
					
					costoPromedio = promedio;
					fecha = aj.getFecha();
					descripcion = "AJUSTE:" + " " + aj.getNumero();
					
					ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
					prm.setArticulo(art);
					prm.setCostoPromedio(costoPromedio);
					prm.setUltimoCosto(ultCosto);
					prm.setDescripcion(descripcion);
					prm.setFecha(fecha);
					rr.saveObject(prm, "sys");
					System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);
				}					
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add costo promedio transferencia
	 */
	public void addCostoPromedioTransferencia(long id) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.selectedSucursal = rr.getSucursalAppById(ID_SUC_PRINCIPAL);
			double costoPromedio = 0;
			String descripcion = "";
			Date fecha = null;
			Transferencia tr = rr.getTransferencia(id);
			for (TransferenciaDetalle item : tr.getDetalles()) {
				Articulo art = item.getArticulo();
				Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), tr.getFechaCreacion());
				Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), tr.getFechaCreacion());
				long totalEntradas = (long) ent[1];
				long totalSalidas = (long) sal[1];
				long stock = (totalEntradas) - totalSalidas;
				if (stock < 0)
					stock = 0;
				List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(),
						DateUtils.addMinutes(tr.getFechaCreacion(), -1));
				double ultPromedio = this.getCostoPromedio(compras, item.getCosto());
				double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, item.getCosto());
				item.setCostoPromedioGs(promedio);
				rr.saveObject(item, item.getUsuarioMod());

				costoPromedio = promedio;
				fecha = tr.getFechaCreacion();
				descripcion = "TRANSFERENCIA EXT.:" + " " + tr.getNumero();

				ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
				prm.setArticulo(art);
				prm.setCostoPromedio(costoPromedio);
				prm.setUltimoCosto(item.getCosto());
				prm.setDescripcion(descripcion);
				prm.setFecha(fecha);
				rr.saveObject(prm, "sys");
				System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);

			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return el costo promedio..
	 */
	private double getCostoPromedioCalculado(long stock, long cantidad, double ultCostoPromedio, double costoFinal) {
		double out = ((cantidad * costoFinal) + (ultCostoPromedio * stock)) / (cantidad + stock);		
		return out;
	}
	
	/**
	 * @return el costo promedio..
	 */
	@SuppressWarnings("unused")
	private double getCostoPromedio(List<Object[]> compras, double ultCosto) {
		if (compras.size() == 0) {
			return ultCosto;
		}
		// ordena la lista segun fecha..
		Collections.sort(compras, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha1.compareTo(fecha2);
			}
		});
		for (int i = compras.size() - 1; i >= 0; i--) {
			Object[] compra = compras.get(i);
			double costo = (double) compra[13];
			return costo > 0 ? costo : (double) compra[4];
		}
		return 0.0;
	}
	
	/**
	 * @return el costo ultimo..
	 */
	private double getCostoUltimo(List<Object[]> compras, double ultCosto) {
		if (compras.size() == 0) {
			return ultCosto;
		}
		// ordena la lista segun fecha..
		Collections.sort(compras, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha1.compareTo(fecha2);
			}
		});
		for (int i = compras.size() - 1; i >= 0; i--) {
			Object[] compra = compras.get(i);
			double costo = (double) compra[4];
			if (costo > 0) {
				return (double) compra[4];
			}			
		}
		return 0.0;
	}
	
	/**
	 * recupera el historico de movimientos del articulo..
	 */
	private List<Object[]> getHistoricoCompras(Date desde, Date hasta) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		boolean fechaHora = true;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> importaciones = rr.getComprasImportacionPorArticuloFechaCierre(desde, hasta, fechaHora);
		List<Object[]> notasCredito = rr.getNotasCreditoVtaPorArticuloCosto(desde, hasta, fechaHora);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(desde, hasta, fechaHora);
		List<Object[]> ajustes = rr.getAjustesPorArticulo(desde, hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_POSITIVO, fechaHora);
		List<Object[]> transferenciasOrigenMRA = rr.getTransferenciasPorArticuloOrigenMRA(desde, hasta, fechaHora);
		List<Object[]> transferenciasOrigenCentral = rr.getTransferenciasPorArticuloOrigenCentral(desde, hasta, fechaHora);
		
		out.addAll(importaciones);
		out.addAll(notasCredito);
		out.addAll(compras);
		out.addAll(ajustes);
		if (!this.isEmpresaGTSA()) {
			out.addAll(this.isEmpresaMRA() ? transferenciasOrigenCentral : transferenciasOrigenMRA);
		}		
		
		// ordena la lista segun fecha..
		Collections.sort(out, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[2];
				Date fecha2 = (Date) o2[2];
				return fecha1.compareTo(fecha2);
			}
		});
		
		return out;
	}
	
	/**
	 * recupera el historico de movimientos del articulo..
	 */
	private List<Object[]> getHistoricoCompras(long idArticulo, String codigo, Date hasta) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		Date desde = this.getFechaDesde();
		boolean fechaHora = true;
		long idSucursal = this.selectedSucursal.getId();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> importaciones = rr.getComprasImportacionPorArticuloFechaCierre(idArticulo, desde, hasta, fechaHora, idSucursal);
		List<Object[]> notasCredito = rr.getNotasCreditoVtaPorArticuloCosto(idArticulo, desde, hasta, fechaHora);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(idArticulo, desde, hasta, fechaHora);
		List<Object[]> ajustes = rr.getAjustesPorArticulo(idArticulo, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_POSITIVO, fechaHora);
		List<Object[]> transferenciasOrigenMra = rr.getTransferenciasPorArticuloOrigenMRA(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transferenciasOrigenCentral = rr.getTransferenciasPorArticuloOrigenCentral(idArticulo, desde, hasta, fechaHora);
		List<Object[]> migraciones = rr.getMigracionesPorArticulo(codigo, desde, hasta);
		
		out.addAll(importaciones);
		out.addAll(notasCredito);
		out.addAll(compras);
		out.addAll(ajustes);
		out.addAll(migraciones);
		if (!this.isEmpresaGTSA()) {
			out.addAll(this.isEmpresaMRA() ? transferenciasOrigenCentral : transferenciasOrigenMra);
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
		
		return out;
	}
	
	/**
	 * recupera el historico de movimientos del articulo..
	 */
	private Object[] getHistoricoEntrada(long idArticulo, String codigo, Date hasta) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		long total = 0;
		Date desde = this.getFechaDesde();
		boolean fechaHora = true;
		long idSucursal = this.selectedSucursal.getId();
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
		if (!this.isEmpresaGTSA()) {
			out.addAll(this.isEmpresaMRA() ? transfsOrigenCentral : transfsOrigenMRA);
			out.addAll(this.isEmpresaMRA() ? transfsOrigenDifInventarioMRA : transfsOrigenDifInventario);
		}
		
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
	private Object[] getHistoricoSalida(long idArticulo, String codigo, Date hasta) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		long total = 0;
		Date desde = this.getFechaDesde();
		boolean fechaHora = true;
		long idSucursal = this.selectedSucursal.getId();
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
		out.addAll(ajustStockNeg);
		if (!this.isEmpresaGTSA()) {
			out.addAll(this.isEmpresaMRA() ? transfsDestinoCentral : transfsDestinoMRA);
			out.addAll(this.isEmpresaMRA() ? transfsDestinoDifInventarioMRA : transfsDestinoDifInventario);
		}			
		
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
	
	public Date getFechaDesde() throws Exception {
		return Utiles.getFechaInicioOperaciones();
	}
	
	/**
	 * @return true si es mra..
	 */
	public boolean isEmpresaMRA() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_YMRA);
	}
	
	/**
	 * @return true si es gtsa..
	 */
	public boolean isEmpresaGTSA() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA);
	}
	
	public static void main(String[] args) {
		try {
			ControlArticuloCostoPromedio ctr = new ControlArticuloCostoPromedio();
			ctr.testCostoPromedio();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
