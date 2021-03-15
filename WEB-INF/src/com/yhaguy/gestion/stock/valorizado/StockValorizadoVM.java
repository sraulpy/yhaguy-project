package com.yhaguy.gestion.stock.valorizado;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloCostoPromediogs;
import com.yhaguy.domain.ImportacionFactura;
import com.yhaguy.domain.ImportacionFacturaDetalle;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.Utiles;

public class StockValorizadoVM {
	
	public static final long ID_SUC_PRINCIPAL = 2;
	public static final long ID_DEP_1 = Configuracion.ID_DEPOSITO_PRINCIPAL;

	private Date fechaHasta;
	private Object[] selectedItem;
	
	private double totalPromedio = 0.0;
	private long totalStock = 0;
	private long totalIngresos = 0;
	private long totalEgresos = 0;	
	private long saldoInicial = 0;
	private double saldoInicialValores = 0;
	private double totalImportaciones = 0;
	private double totalFlete = 0;
	private double totalGastos = 0;
	private double totalSeguro = 0;
	private double totalCostoVentas = 0;
	
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
	public List<Object[]> getArticulos() throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		this.totalPromedio = 0.0;
		this.totalStock = 0;
		this.totalIngresos = 0;
		this.totalEgresos = 0;
		this.saldoInicial = 0;
		this.saldoInicialValores = 0.0;
		if (this.fechaHasta == null) {
			return out;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> arts = rr.getArticulosMercaderias("", "", "BATERIAS");
		for (Object[] art : arts) {
			long idArticulo = (long) art[0];
			String codigo = (String) art[1];
			String descripcion = (String) art[2];
			Object[] ent = this.getHistoricoEntrada(idArticulo, codigo, this.fechaHasta);
			List<Object[]> entradas = (List<Object[]>) ent[0];
			List<Object[]> compras = (List<Object[]>) ent[2];
			Object[] cierre = (Object[]) ent[3];
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
			if (cierre != null) {
				double costo = (double) cierre[4];
				long cant = Long.parseLong(cierre[3] + "");
				this.saldoInicial += cant;
				this.saldoInicialValores += (costo * cant);
			}
			out.add(new Object[] { idArticulo, codigo, stock, ultCosto, prmCosto, entradas, totalEntradas, salidas,
					totalSalidas, stock, prmTotal, descripcion });		
		}
		
		BindUtils.postNotifyChange(null, null, this, "totalPromedio");
		BindUtils.postNotifyChange(null, null, this, "totalStock");
		BindUtils.postNotifyChange(null, null, this, "totalIngresos");
		BindUtils.postNotifyChange(null, null, this, "totalEgresos");
		BindUtils.postNotifyChange(null, null, this, "saldoInicial");
		BindUtils.postNotifyChange(null, null, this, "saldoInicialValores");
		return out;
	}
	
	/**
	 * @return el costo promedio..
	 */
	private double getCostoPromedioCalculado(long stock, List<Object[]> compras, double ultCosto, Object[] cierre) {
		if (cierre != null) {
			compras.add(cierre);
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
		long saldo = stock;
		double acum = 0;
		for (int i = compras.size() - 1; i >= 0; i--) {
			Object[] compra = compras.get(i);
			//System.out.println(compra[2] + "");
			long cantCompra = Long.parseLong(compra[3] + "");	
			double costo = (double) compra[4];
			if (saldo > cantCompra) {
				acum += (cantCompra * costo);
			} else {
				acum += (saldo * costo);
				return (acum / stock);
			}
			saldo -= cantCompra;
		}
		return ultCosto;
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
			return (double) compra[13];
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
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> transfsMra = rr.getTransferenciasPorArticuloMRAentrada(idArticulo, desde, hasta, fechaHora);
		List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(idArticulo, desde, hasta, fechaHora);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(idArticulo, desde, hasta, fechaHora);
		List<Object[]> importaciones = rr.getComprasImportacionPorArticuloFechaCierre(idArticulo, desde, hasta, fechaHora);
		List<Object[]> saldoInicial = rr.getAjustesPorArticuloStockValorizado(idArticulo, desde, hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_SALDO_INICIAL_STOCK_VALORIZADO, fechaHora);
		List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(idArticulo, desde, hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_POSITIVO, fechaHora);
		List<Object[]> migracion = rr.getMigracionPorArticulo(codigo, desde, hasta, 0);
		
		out.addAll(migracion);
		out.addAll(saldoInicial);
		out.addAll(ajustStockPost);		
		out.addAll(ntcsv);
		out.addAll(compras);
		out.addAll(importaciones);
		if (this.isEmpresaMRA()) out.addAll(transfsMra);
		Object[] cierre = null;
		
		for (Object[] item : out) {
			String concepto = (String) item[0];
			if (!concepto.equals("FAC.COMPRA CREDITO")
					&& !concepto.equals("FAC.COMPRA CONTADO")
					&& !concepto.equals("FAC. IMPORTACIÓN CRÉDITO")
					&& !concepto.equals("FAC. IMPORTACIÓN CONTADO")
					&& !(concepto.equals("SALDO INICIAL AL CIERRE"))) {
				item[4] = 0.0;
			}
			if (concepto.equals("SALDO INICIAL AL CIERRE")) {
				cierre = item;
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
		RegisterDomain rr = RegisterDomain.getInstance();		
		List<Object[]> ventas = rr.getVentasPorArticuloCosto(idArticulo, desde, hasta, fechaHora);
		List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transfs = rr.getTransferenciasPorArticulo(idArticulo, desde, hasta, fechaHora);
		List<Object[]> transfsMra_ = rr.getTransferenciasPorArticuloMRAsalida(idArticulo, desde, hasta, fechaHora);
		List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idArticulo, desde, hasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO, fechaHora);
		
		for (Object[] item : ajustStockNeg) {
			item[3] = (Long.parseLong(item[3] + "") * -1);
		}
		out.addAll(ventas);
		out.addAll(ntcsc);		
		out.addAll(transfs);
		out.addAll(transfsMra_);
		out.addAll(ajustStockNeg);
		if (this.isEmpresaMRA()) out.addAll(this.isEmpresaMRA() ? transfsMra_ : transfs);
		
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
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> importaciones = rr.getComprasImportacionPorArticuloFechaCierre(idArticulo, desde, hasta, fechaHora);
		
		out.addAll(importaciones);
		
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
	
	public Date getFechaDesde() throws Exception {
		return Utiles.getFecha("01-08-2016 00:00:00");
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

	public double getSaldoInicialValores() {
		return saldoInicialValores;
	}

	public void setSaldoInicialValores(double saldoInicialValores) {
		this.saldoInicialValores = saldoInicialValores;
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
			//st.testimp();
			st.test();
			//st.testnc();
			//st.testCostoPromedio();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void testimp() throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ImportacionPedidoCompra> imps = rr.getImportacionPedidosCompra();
		for (ImportacionPedidoCompra imp : imps) {
			ImportacionFactura fac = imp.getImportacionFactura_().get(0);
			for (ImportacionFacturaDetalle item : fac.getDetalles()) {
				Articulo art = item.getArticulo();
				Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), fac.getFechaVolcado());
				Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), fac.getFechaVolcado());
				long totalEntradas = (long) ent[1];
				long totalSalidas = (long) sal[1];
				List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), DateUtils.addMinutes(fac.getFechaVolcado(), 1));
				long stock = (totalEntradas + item.getCantidad_()) - totalSalidas;
				double promedio = this.getCostoPromedioCalculado(stock, compras, item.getCostoFinalGs(), null);
				item.setCostoPromedioGs(promedio);
				rr.saveObject(item, item.getUsuarioMod());			
				System.out.println(fac.getNumero() + " - " + fac.getFechaVolcado() + " - " + art.getCodigoInterno() + " - " + stock + " - " + promedio);
			}
		}		
	}
	
	private void test() throws Exception {
		Date desde = Utiles.getFecha("01-01-2018 00:00:00");
		this.fechaHasta = Utiles.getFecha("31-12-2018 23:00:00");
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> ventas = rr.getVentas(desde, this.fechaHasta, 0);
		for (Venta venta : ventas) {
			if (!venta.isAnulado()) {
				for (VentaDetalle item : venta.getDetalles()) {
					Articulo art = item.getArticulo();
					List<Object[]> compras = this.getHistoricoCompras(art.getId(), art.getCodigoInterno(), venta.getFecha());
					double promedio = this.getCostoPromedio(compras, item.getCostoUnitarioGs());
					item.setCostoPromedioGs(promedio);
					rr.saveObject(item, item.getUsuarioMod());
				}
				System.out.println(venta.getNumero() + " - " + venta.getFecha());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void testnc() throws Exception {
		Date desde = Utiles.getFecha("01-01-2018 00:00:00");
		this.fechaHasta = Utiles.getFecha("31-01-2018 23:00:00");
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCredito> ncs = rr.getNotasCreditoVentaByMotivo(desde, this.fechaHasta, Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION);
		for (NotaCredito nc : ncs) {
			if (!nc.isAnulado()) {
				double totalCostoPromedio = 0;
				for (NotaCreditoDetalle item : nc.getDetallesArticulos()) {
					Articulo art = item.getArticulo();
					Object[] ent = this.getHistoricoEntrada(art.getId(), art.getCodigoInterno(), nc.getFechaEmision());
					Object[] sal = this.getHistoricoSalida(art.getId(), art.getCodigoInterno(), nc.getFechaEmision());
					long totalEntradas = (long) ent[1];
					long totalSalidas = (long) sal[1];
					List<Object[]> compras = (List<Object[]>) ent[2];
					long stock = totalEntradas - totalSalidas;
					double promedio = this.getCostoPromedioCalculado(stock, compras, item.getCostoGs(), null);
					item.setCostoPromedioGs(promedio);
					totalCostoPromedio += (promedio * item.getCantidad());
				}
				nc.setCostoPromedioGs(totalCostoPromedio);
				rr.saveObject(nc, nc.getUsuarioMod());
				System.out.println(nc.getNumero() + " - " + Utiles.getNumberFormat(totalCostoPromedio));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void testCostoPromedio() throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> arts = rr.getArticulosMercaderias("", "", "BATERIAS");
		
		for (Object[] art : arts) {
			Date current = Utiles.getFecha("01-01-2019 00:00:00");
			Date end = Utiles.getFecha("31-12-2019 23:00:00");
			this.fechaHasta = end;
			
			long idArticulo = (long) art[0];
			String codigo = (String) art[1];
			Articulo articulo = rr.getArticuloById(idArticulo);
			
			while (current.before(end)) {
		        System.out.println(current);
		        
		        Object[] ent = this.getHistoricoEntrada(idArticulo, codigo, current);
				List<Object[]> compras = (List<Object[]>) ent[2];
				long totalEntradas = (long) ent[1];
				Object[] sal = this.getHistoricoSalida(idArticulo, codigo, current);
				long totalSalidas = (long) sal[1];
				long stock = (totalEntradas - totalSalidas);
				double ultCosto = this.getCostoUltimo(compras, (double) art[3]);
				double prmCosto = this.getCostoPromedioCalculado(stock, compras, ultCosto, null);
		        
				if (prmCosto > 0 || ultCosto > 0) {
					ArticuloCostoPromediogs ar = new ArticuloCostoPromediogs();
					ar.setCostoPromedio(prmCosto);
					ar.setUltimoCosto(ultCosto);
					ar.setArticulo(articulo);
					ar.setFecha(current);
					rr.saveObject(ar, "sys");
				}				
				System.out.println(codigo + " - " + prmCosto);
		        
		        Calendar calendar = Calendar.getInstance();
		        calendar.setTime(current);
		        calendar.add(Calendar.DATE, 1);
		        current = calendar.getTime();
		    }		
		}
	}
}
