package com.yhaguy.gestion.stock.valorizado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloCostoPromediogs;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.ImportacionFactura;
import com.yhaguy.domain.ImportacionFacturaDetalle;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.Utiles;

public class StockValorizadoVM extends SimpleViewModel {
	
	public static final long ID_SUC_PRINCIPAL = 2;
	public static final long ID_DEP_1 = Configuracion.ID_DEPOSITO_PRINCIPAL;

	private Date fechaHasta;
	private Object[] selectedItem;
	private SucursalApp selectedSucursal;
	
	private double totalPromedio = 0.0;
	private long totalStock = 0;
	private long totalIngresos = 0;
	private long totalEgresos = 0;	
	private long saldoInicial = 0;
	private double totalImportaciones = 0;
	private double totalFlete = 0;
	private double totalGastos = 0;
	private double totalSeguro = 0;
	private double totalComprasLocales = 0;
	private double totalNotasCreditoCompra = 0;
	private double totalCostoVentas = 0;
	
	private Object[] selectedArticulo;
	private String filterCodigo = "";
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}	
	
	@NotifyChange("*")
	@Command
	public void procesar() {
	}
	
	/**
	 * GETS / SETS
	 */
	
	@SuppressWarnings("unchecked")
	@DependsOn({ "selectedArticulo", "fechaHasta", "selectedSucursal" })
	public List<Object[]> getArticulos() throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		
		this.totalPromedio = 0.0;
		this.totalStock = 0;
		this.totalIngresos = 0;
		this.totalEgresos = 0;
		this.saldoInicial = 0;
		
		if (this.selectedArticulo == null) return out;
		if (this.fechaHasta == null) return out;
		if (this.selectedSucursal == null) return out;
		
		String codigoInterno = (String) this.selectedArticulo[1];
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> arts = rr.getArticulosMercaderias(codigoInterno, "", "");
		for (Object[] art : arts) {
			long idArticulo = (long) art[0];
			String codigo = (String) art[1];
			String descripcion = (String) art[2];
			Object[] ent = this.getHistoricoEntrada(idArticulo, codigo, this.fechaHasta);
			List<Object[]> entradas = (List<Object[]>) ent[0];
			List<Object[]> compras = this.getHistoricoCompras(idArticulo, codigo, this.fechaHasta);
			long totalEntradas = (long) ent[1];
			Object[] sal = this.getHistoricoSalida(idArticulo, codigo, this.fechaHasta);
			List<Object[]> salidas = (List<Object[]>) sal[0];
			long totalSalidas = (long) sal[1];
			long stock = (totalEntradas - totalSalidas);
			double ultCosto = this.getCostoUltimo(compras, (double) art[3]);
			double prmCosto = this.getCostoPromedio(compras, (double) art[3]);
			double prmTotal = stock > 0 ? (prmCosto * stock) : 0.0;
			this.totalPromedio += prmTotal;
			this.totalStock += stock;
			this.totalIngresos += totalEntradas;
			this.totalEgresos += totalSalidas;
			out.add(new Object[] { idArticulo, codigo, stock, ultCosto, prmCosto, entradas, totalEntradas, salidas,
					totalSalidas, stock, prmTotal, descripcion });	
		}
		if(out.size() > 0) this.selectedItem = out.get(0);
		
		BindUtils.postNotifyChange(null, null, this, "totalPromedio");
		BindUtils.postNotifyChange(null, null, this, "totalStock");
		BindUtils.postNotifyChange(null, null, this, "totalIngresos");
		BindUtils.postNotifyChange(null, null, this, "totalEgresos");
		BindUtils.postNotifyChange(null, null, this, "saldoInicial");
		BindUtils.postNotifyChange(null, null, this, "selectedItem");
		return out;
	}
	
	/**
	 * @return el costo promedio..
	 */
	private double getCostoPromedioCalculado(long stock, long cantidad, double ultCostoPromedio, double costoFinal) {
		double out = ((cantidad * costoFinal) + (ultCostoPromedio * stock)) / (cantidad + stock);		
		return out;
	}
	
	/**
	 * @return el costo ultimo..
	 */
	@SuppressWarnings("unused")
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
			return (double) compra[4];
		}
		return 0.0;
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
		//List<Object[]> transfsOrigenMRA = rr.getTransferenciasPorArticuloOrigenMRA(idArticulo, desde, hasta, fechaHora);
		//List<Object[]> transfsOrigenDifInventario = rr.getTransferenciasPorArticuloOrigenDiferenciaInv2019(idArticulo, desde, hasta, fechaHora);
		List<Object[]> migracion = rr.getMigracionesPorArticulo(codigo, desde, hasta);
		
		out.addAll(migracion);
		out.addAll(ajustStockPost);		
		out.addAll(ntcsv);
		out.addAll(compras);
		out.addAll(importaciones);
		//out.addAll(transfsOrigenMRA);
		//out.addAll(transfsOrigenDifInventario);
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
		//List<Object[]> transfsDestinoMRA = rr.getTransferenciasPorArticuloDestinoMRA(idArticulo, desde, hasta, fechaHora);
		//List<Object[]> transfsDestinoDifInventario = rr.getTransferenciasPorArticuloDestinoDiferenciaInv2019(idArticulo, desde, hasta, fechaHora);
		List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idArticulo, desde, hasta, idSucursal, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO, fechaHora);
		
		for (Object[] item : ajustStockNeg) {
			item[3] = (Long.parseLong(item[3] + "") * -1);
		}
		out.addAll(ventas);
		out.addAll(ntcsc);		
		//out.addAll(transfsDestinoMRA);
		//out.addAll(transfsDestinoDifInventario);
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
		
		out.addAll(importaciones);
		out.addAll(notasCredito);
		out.addAll(compras);
		out.addAll(ajustes);
		out.addAll(transferenciasOrigenMra);
		
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
	private List<Object[]> getHistoricoCompras(Date desde, Date hasta) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		boolean fechaHora = true;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> importaciones = rr.getComprasImportacionPorArticuloFechaCierre(desde, hasta, fechaHora);
		List<Object[]> notasCredito = rr.getNotasCreditoVtaPorArticuloCosto(desde, hasta, fechaHora);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(desde, hasta, fechaHora);
		List<Object[]> ajustes = rr.getAjustesPorArticulo(desde, hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_POSITIVO, fechaHora);
		List<Object[]> transferenciasOrigenMRA = rr.getTransferenciasPorArticuloOrigenMRA(desde, hasta, fechaHora);
		
		out.addAll(importaciones);
		out.addAll(notasCredito);
		out.addAll(compras);
		out.addAll(ajustes);
		out.addAll(transferenciasOrigenMRA);
		
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
	 * @return las importaciones..
	 */
	public List<Object[]> getImportaciones() throws Exception {
		this.totalImportaciones = 0;
		this.totalFlete = 0;
		this.totalGastos = 0;
		this.totalSeguro = 0;
		if (this.fechaHasta == null) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getImportaciones(this.getFechaDesde(), this.fechaHasta);
		for (Object[] item : out) {
			this.totalImportaciones += ((double) item[3]);
			this.totalFlete += ((double) item[5]);
			this.totalGastos += ((double) item[6]);
			this.totalSeguro += ((double) item[7]);
		}
		BindUtils.postNotifyChange(null, null, this, "totalImportaciones");
		BindUtils.postNotifyChange(null, null, this, "totalFlete");
		BindUtils.postNotifyChange(null, null, this, "totalGastos");
		BindUtils.postNotifyChange(null, null, this, "totalSeguro");
		return out;
	}
	
	/**
	 * @return las compras locales..
	 */
	public List<Object[]> getComprasLocales() throws Exception {
		this.totalComprasLocales = 0;
		if (this.fechaHasta == null) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getComprasLocales(this.getFechaDesde(), this.fechaHasta);
		for (Object[] item : out) {
			this.totalComprasLocales += ((double) item[3]);
		}
		this.totalComprasLocales = (this.totalComprasLocales - Utiles.getIVA(this.totalComprasLocales, 10));
		BindUtils.postNotifyChange(null, null, this, "totalComprasLocales");
		return out;
	}
	
	/**
	 * @return las notas credito compras locales..
	 */
	public List<Object[]> getNotasCreditoCompra() throws Exception {
		this.totalNotasCreditoCompra = 0;
		if (this.fechaHasta == null) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getNotasCreditoCompra(this.getFechaDesde(), this.fechaHasta);
		for (Object[] item : out) {
			this.totalNotasCreditoCompra += ((double) item[3]);
		}
		this.totalNotasCreditoCompra = (this.totalNotasCreditoCompra - Utiles.getIVA(this.totalNotasCreditoCompra, 10));
		BindUtils.postNotifyChange(null, null, this, "totalNotasCreditoCompra");
		return out;
	}
	
	@DependsOn("fechaHasta")
	public List<Object[]> getCostoVentas() throws Exception {
		this.totalCostoVentas = 0;
		if (this.fechaHasta == null) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = new ArrayList<Object[]>();
		List<Object[]> ncs = rr.getNotasCredito(this.getFechaDesde(), this.fechaHasta);
		List<Object[]> vts = rr.getVentas(this.getFechaDesde(), this.fechaHasta);
		for (Object[] item : ncs) {
			item[3] = ((double) item[3] * -1);
			if ((double) item[3] < 0) {
				out.add(item);
			}
		}
		out.addAll(vts);
		for (Object[] item : out) {
			this.totalCostoVentas += ((double) item[3]);
		}
		BindUtils.postNotifyChange(null, null, this, "totalCostoVentas");
		return out;
	}
	
	/**
	 * @return los articulos..
	 */
	@DependsOn("filterCodigo")
	public List<Object[]> getListaArticulos() throws Exception {
		if (this.filterCodigo.trim().isEmpty()) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulos(this.filterCodigo, "");
	}
	
	/**
	 * @return las sucursales..
	 */
	@SuppressWarnings("unchecked")
	public List<SucursalApp> getSucursales() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<SucursalApp> list = rr.getObjects(SucursalApp.class.getName());
		List<SucursalApp> out = new ArrayList<SucursalApp>();
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRSA)) {
			SucursalApp suc = rr.getSucursalAppById(SucursalApp.ID_CENTRAL);
			out.add(suc);
		} else {
			for (SucursalApp suc : list) {
				if (!suc.getAuxi().equals("V")) {
					out.add(suc);
				}
			}
		}		
		return out;
	}
	
	/**
	 * @return saldo inicial valores
	 */
	public double getSaldoInicialValores() {
		return 5940000000.0;
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
	
	@DependsOn({ "saldoInicial", "totalIngresos" })
	public long getTotalIngreso() {
		return this.totalIngresos - this.saldoInicial;
	}
	
	@DependsOn({ "totalIngresos", "totalEgresos" })
	public long getSaldoCantidad() {
		return this.totalIngresos - this.totalEgresos;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Object[] getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Object[] selectedItem) {
		this.selectedItem = selectedItem;
	}

	public double getTotalPromedio() {
		return totalPromedio;
	}

	public void setTotalPromedio(double totalPromedio) {
		this.totalPromedio = totalPromedio;
	}

	public long getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(long totalStock) {
		this.totalStock = totalStock;
	}

	public long getTotalIngresos() {
		return totalIngresos;
	}

	public void setTotalIngresos(long totalIngresos) {
		this.totalIngresos = totalIngresos;
	}

	public long getTotalEgresos() {
		return totalEgresos;
	}

	public void setTotalEgresos(long totalEgresos) {
		this.totalEgresos = totalEgresos;
	}

	public long getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(long saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public double getTotalImportaciones() {
		return totalImportaciones;
	}

	public void setTotalImportaciones(double totalImportaciones) {
		this.totalImportaciones = totalImportaciones;
	}

	public double getTotalFlete() {
		return totalFlete;
	}

	public void setTotalFlete(double totalFlete) {
		this.totalFlete = totalFlete;
	}

	public double getTotalGastos() {
		return totalGastos;
	}

	public void setTotalGastos(double totalGastos) {
		this.totalGastos = totalGastos;
	}

	public void setTotalCostoVentas(double totalCostoVentas) {
		this.totalCostoVentas = totalCostoVentas;
	}

	public double getTotalCostoVentas() {
		return totalCostoVentas;
	}

	public double getTotalSeguro() {
		return totalSeguro;
	}

	public void setTotalSeguro(double totalSeguro) {
		this.totalSeguro = totalSeguro;
	}
	
	public static void main(String[] args) {
		try {
			StockValorizadoVM st = new StockValorizadoVM();
			st.testCostoPromedio();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void testCostoPromedio() throws Exception {		
		Date desde = Utiles.getFecha("28-04-2021 00:00:00");
		Date hasta = Utiles.getFecha("31-05-2021 23:00:00");
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
								double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, promedioVta);
								item.setCostoPromedioGs(promedio);
								rr.saveObject(item, item.getUsuarioMod());
								
								costoPromedio = promedio;
								fecha = nc.getFechaEmision();
								descripcion = "NOTA CREDITO:" + " " + nc.getNumero();
								
								ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
								prm.setArticulo(art);
								prm.setCostoPromedio(costoPromedio);
								prm.setUltimoCosto(costoUltimo);
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
						double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, promedioVta);
						item.setCostoPromedioGs(promedio);
						rr.saveObject(item, item.getUsuarioMod());
						
						costoPromedio = promedio;
						fecha = nc.getFechaEmision();
						descripcion = "NOTA CREDITO:" + " " + nc.getNumero();
						
						ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
						prm.setArticulo(art);
						prm.setCostoPromedio(costoPromedio);
						prm.setUltimoCosto(costoUltimo);
						prm.setDescripcion(descripcion);
						prm.setFecha(fecha);
						rr.saveObject(prm, "sys");
						System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);					
					}				
				}				
			}
			
			if (tipo.equals("FAC.COMPRA CREDITO") || tipo.equals("FAC.COMPRA CONTADO")) {
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
					prm.setUltimoCosto(costoUltimo);
					prm.setDescripcion(descripcion);
					prm.setFecha(fecha);
					rr.saveObject(prm, "sys");
					System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);
				}
			}
			
			if ((tipo.equals("FAC. IMPORTACIÓN CRÉDITO") || tipo.equals("FAC. IMPORTACIÓN CONTADO"))
					|| (tipo.equals("FAC. IMPORTACIÓN CRE.") || tipo.equals("FAC. IMPORTACIÓN CON."))) {
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
						prm.setUltimoCosto(costoUltimo);
						prm.setDescripcion(descripcion);
						prm.setFecha(fecha);
						rr.saveObject(prm, "sys");
						System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);
					}					
				}
			}
			
			if (tipo.equals("AJUSTE STOCK POSITIVO")) {
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
						double promedio = this.getCostoPromedioCalculado(stock, item.getCantidad(), ultPromedio, item.getCostoGs());
						item.setCostoPromedioGs(promedio);
						rr.saveObject(item, item.getUsuarioMod());	
						
						costoPromedio = promedio;
						fecha = aj.getFecha();
						descripcion = "AJUSTE:" + " " + aj.getNumero();
						
						ArticuloCostoPromediogs prm = new ArticuloCostoPromediogs();
						prm.setArticulo(art);
						prm.setCostoPromedio(costoPromedio);
						prm.setUltimoCosto(costoUltimo);
						prm.setDescripcion(descripcion);
						prm.setFecha(fecha);
						rr.saveObject(prm, "sys");
						System.out.println(art.getCodigoInterno() + " " + costoPromedio + " " + descripcion + " " + fecha);
					}					
				}
			}
		}
	}

	public double getTotalComprasLocales() {
		return totalComprasLocales;
	}

	public void setTotalComprasLocales(double totalComprasLocales) {
		this.totalComprasLocales = totalComprasLocales;
	}

	public double getTotalNotasCreditoCompra() {
		return totalNotasCreditoCompra;
	}

	public void setTotalNotasCreditoCompra(double totalNotasCreditoCompra) {
		this.totalNotasCreditoCompra = totalNotasCreditoCompra;
	}

	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}

	public Object[] getSelectedArticulo() {
		return selectedArticulo;
	}

	public void setSelectedArticulo(Object[] selectedArticulo) {
		this.selectedArticulo = selectedArticulo;
	}

	public SucursalApp getSelectedSucursal() {
		return selectedSucursal;
	}

	public void setSelectedSucursal(SucursalApp selectedSucursal) {
		this.selectedSucursal = selectedSucursal;
	}
}
