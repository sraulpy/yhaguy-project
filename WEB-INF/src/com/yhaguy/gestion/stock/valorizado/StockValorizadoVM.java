package com.yhaguy.gestion.stock.valorizado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class StockValorizadoVM extends SimpleViewModel {
	
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
	
	/**
	 * GETS / SETS
	 */
	
	@SuppressWarnings("unchecked")
	@DependsOn({ "fechaHasta" })
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
			Object[] ent = this.getHistoricoEntrada(idArticulo, codigo);
			List<Object[]> entradas = (List<Object[]>) ent[0];
			List<Object[]> compras = (List<Object[]>) ent[2];
			Object[] cierre = (Object[]) ent[3];
			long totalEntradas = (long) ent[1];
			Object[] sal = this.getHistoricoSalida(idArticulo, codigo);
			List<Object[]> salidas = (List<Object[]>) sal[0];
			long totalSalidas = (long) sal[1];
			long stock = (totalEntradas - totalSalidas);
			double ultCosto = this.getCostoUltimo(compras, (double) art[3]);
			double prmCosto = this.getCostoPromedio(stock, compras, ultCosto, cierre);
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
			if (totalEntradas > 0) {
				out.add(new Object[] { idArticulo, codigo, stock, ultCosto, prmCosto, entradas, totalEntradas, salidas,
						totalSalidas, stock, prmTotal, descripcion });
			}			
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
	private double getCostoPromedio(long stock, List<Object[]> compras, double ultCosto, Object[] cierre) {
		if (cierre != null) {
			compras.add(cierre);
		}
		double out = 0.0;
		if (stock <= 0) {
			return out;
		}
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
		long saldo = stock;
		double acum = 0;
		for (int i = compras.size() - 1; i >= 0; i--) {
			Object[] compra = compras.get(i);
			long cantCompra = Long.parseLong(compra[3] + "");
			if (stock <= cantCompra) {
				return (double) compra[4];
			}		
			double costo = (double) compra[4];
			if (saldo > cantCompra) {
				acum += (cantCompra * costo);
			} else {
				acum += (saldo * costo);
				return (acum / stock);
			}
			saldo -= cantCompra;
		}
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
	 * recupera el historico de movimientos del articulo..
	 */
	private Object[] getHistoricoEntrada(long idArticulo, String codigo) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		long total = 0;
		Date desde = this.getFechaDesde();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> transfsMra = rr.getTransferenciasPorArticuloMRAentrada(idArticulo, desde, this.fechaHasta);
		List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(idArticulo, desde, this.fechaHasta);
		List<Object[]> compras = rr.getComprasLocalesPorArticulo(idArticulo, desde, this.fechaHasta);
		List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(idArticulo, desde, this.fechaHasta);
		List<Object[]> saldoInicial = rr.getAjustesPorArticuloStockValorizado(idArticulo, desde, this.fechaHasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_SALDO_INICIAL_STOCK_VALORIZADO);
		List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(idArticulo, desde, this.fechaHasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
		List<Object[]> migracion = rr.getMigracionPorArticulo(codigo, desde, this.fechaHasta, 0);
		
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
	private Object[] getHistoricoSalida(long idArticulo, String codigo) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		long total = 0;
		Date desde = this.getFechaDesde();
		RegisterDomain rr = RegisterDomain.getInstance();		
		List<Object[]> ventas = rr.getVentasPorArticulo(idArticulo, desde, this.fechaHasta);
		List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(idArticulo, desde, this.fechaHasta);
		List<Object[]> transfs = rr.getTransferenciasPorArticulo(idArticulo, desde, this.fechaHasta);
		List<Object[]> transfsMra_ = rr.getTransferenciasPorArticuloMRAsalida(idArticulo, desde, this.fechaHasta);
		List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idArticulo, desde, this.fechaHasta, ID_SUC_PRINCIPAL, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
		
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
	
	@DependsOn("fechaHasta")
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
		return Utiles.getFecha("01-01-2020 00:00:00");
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
}
