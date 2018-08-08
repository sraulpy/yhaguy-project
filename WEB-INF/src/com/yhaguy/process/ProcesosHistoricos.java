package com.yhaguy.process;

import java.util.Date;
import java.util.List;

import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.HistoricoCobranzaDiaria;
import com.yhaguy.domain.HistoricoCobranzaVendedor;
import com.yhaguy.domain.HistoricoMovimientos;
import com.yhaguy.domain.HistoricoVentaDiaria;
import com.yhaguy.domain.HistoricoVentaVendedor;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

public class ProcesosHistoricos {

	/**
	 * procesa el historico de movimientos 
	 */
	public static void addHistoricoMovimientos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		
		rr.deleteAllObjects(HistoricoMovimientos.class.getName());
		
		List<Cliente> clientes = rr.getClientes();
		for (Cliente cliente : clientes) {
			double totalVCD = 0;
			double totalVCR = 0;
			double totalNCR = 0;
			double totalSAL = 0;
			double totalCHE = 0;
			List<Venta> vtasContado = rr.getVentasContado(desde, new Date(), cliente.getId());
			List<Venta> vtasCredito = rr.getVentasCredito(desde, new Date(), cliente.getId());
			List<NotaCredito> notasCredito = rr.getNotasCreditoVenta(desde, new Date(), cliente.getId());
			
			for (Venta cont : vtasContado) {
				totalVCD += cont.getTotalImporteGs();
			}
			for (Venta cre : vtasCredito) {
				totalVCR += cre.getTotalImporteGs();
			}
			for (NotaCredito nc : notasCredito) {
				totalNCR += nc.getImporteGs();
			}
			
			if (totalVCD > 0 || totalVCR > 0) {
				
				totalSAL = rr.getSaldoCtaCte(cliente.getEmpresa().getId());
				List<BancoChequeTercero> cheques = rr.getChequesPendientesClientes(cliente.getId());
				for (BancoChequeTercero cheque : cheques) {
					totalCHE += cheque.getMonto();
				}
				
				HistoricoMovimientos hist = new HistoricoMovimientos();
				hist.setIdCliente(cliente.getId());
				hist.setRuc(cliente.getRuc());
				hist.setRazonSocial(cliente.getRazonSocial());
				hist.setTotalVentasContado(totalVCD);
				hist.setTotalVentasCredito(totalVCR);
				hist.setTotalNotasDeCredito(totalNCR);
				hist.setTotalSaldoGs(totalSAL);
				hist.setTotalChequePendientesGs(totalCHE);
				rr.saveObject(hist, "process");				
				System.out.println(cliente.getRazonSocial());
			}
		}
	}
	
	/**
	 * procesa el historico de ventas / metas de toda la sucursal..
	 */
	public static void addHistoricoVentaMetaSucursal(double meta) throws Exception {
		System.out.println("Procesando Historico Venta / Metas..");		
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFechaInicioMes();
		Date hasta = Utiles.getFechaFinMes();
		int anho = Integer.parseInt(Utiles.getAnhoActual());
		int mes = Integer.parseInt(Utiles.getMesActual());
		
		double ventas = 0;
		double ncreds = 0;
		double ventas_servicios = 0;
		
		List<Venta> vtas = rr.getVentas(desde, hasta, 0);
		List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
		
		for (Venta vta : vtas) {
			ventas += vta.isAnulado() ? 0 : vta.getTotalImporteGsSinIva();
			ventas_servicios += vta.isAnulado() ? 0 : (vta.getVendedor().getRazonSocial().equals("SERVICIO") ? vta.getTotalImporteGsSinIva() : 0 );
		}
		
		for (NotaCredito nc : ncs) {
			ncreds += nc.isAnulado()? 0 : nc.getTotalImporteGsSinIva();
		}
				
		HistoricoVentaVendedor hist = rr.getHistoricoVentaVendedor(anho, mes, 0);
		if (hist == null) hist = new HistoricoVentaVendedor();
		
		hist.setAnho(anho);
		hist.setMes(mes);
		hist.setId_funcionario(0);
		hist.setTotal_venta(ventas);
		hist.setTotal_venta_servicio(ventas_servicios);
		hist.setTotal_notacredito(ncreds);
		hist.setMeta(meta);
		rr.saveObject(hist, "process");
		System.out.println(mes + " - " + anho + " - " + ventas);
	}
	
	/**
	 * actualiza el historico venta - meta..
	 */
	public static void updateHistoricoVentaMeta(double venta, double notaCredito, boolean servicio) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		int anho = Integer.parseInt(Utiles.getAnhoActual());
		int mes = Integer.parseInt(Utiles.getMesActual());
		HistoricoVentaVendedor hist = rr.getHistoricoVentaVendedor(anho, mes, 0);
		if (hist == null) hist = new HistoricoVentaVendedor();		
		hist.setAnho(anho);
		hist.setMes(mes);
		hist.setId_funcionario(0);
		hist.setTotal_venta(hist.getTotal_venta() + venta);
		hist.setTotal_notacredito(hist.getTotal_notacredito() + notaCredito);
		if (servicio) {
			hist.setTotal_venta_servicio(hist.getTotal_venta_servicio() + venta);
		}
		rr.saveObject(hist, "process");
	}
	
	/**
	 * actualiza el historico venta diaria..
	 */
	@SuppressWarnings("deprecation")
	public static void updateHistoricoVentaDiaria(Date fecha, double venta, double notaCredito) throws Exception {
		fecha.setHours(0); fecha.setMinutes(0); fecha.setSeconds(0);
		RegisterDomain rr = RegisterDomain.getInstance();
		HistoricoVentaDiaria hist = rr.getHistoricoVentaDiaria(fecha, 0);
		if (hist == null) hist = new HistoricoVentaDiaria();		
		hist.setFecha(fecha);
		hist.setId_funcionario(0);
		hist.setTotal_venta(hist.getTotal_venta() + venta);
		hist.setTotal_notacredito(hist.getTotal_notacredito() + notaCredito);
		rr.saveObject(hist, "process");
	}
	
	/**
	 * actualiza el historico cobranza - meta..
	 */
	public static void updateHistoricoCobranzaMeta(double cobranza) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		int anho = Integer.parseInt(Utiles.getAnhoActual());
		int mes = Integer.parseInt(Utiles.getMesActual());
		HistoricoCobranzaVendedor hist = rr.getHistoricoCobranzaVendedor(anho, mes, 0);
		if (hist == null) hist = new HistoricoCobranzaVendedor();		
		hist.setAnho(anho);
		hist.setMes(mes);
		hist.setId_funcionario(0);
		hist.setTotal_cobranza(hist.getTotal_cobranza() + cobranza);
		rr.saveObject(hist, "process");
	}
	
	/**
	 * actualiza el historico cobranza diaria..
	 */
	@SuppressWarnings("deprecation")
	public static void updateHistoricoCobranzaDiaria(Date fecha, double cobranza) throws Exception {
		fecha.setHours(0); fecha.setMinutes(0); fecha.setSeconds(0);
		RegisterDomain rr = RegisterDomain.getInstance();
		HistoricoCobranzaDiaria hist = rr.getHistoricoCobranzaDiaria(fecha, 0);
		if (hist == null) hist = new HistoricoCobranzaDiaria();		
		hist.setFecha(fecha);
		hist.setId_funcionario(0);
		hist.setTotal_cobranza(hist.getTotal_cobranza() + cobranza);
		rr.saveObject(hist, "process");
	}
	
	public static void main(String[] args) {
		try {
			ProcesosHistoricos.addHistoricoMovimientos();
			//ProcesosHistoricos.addHistoricoVentaMetaSucursal(new Double(2700000000L));
			//ProcesosHistoricos.updateHistoricoVentaDiaria(new Date(), 120000, 0);
			//ProcesosHistoricos.updateHistoricoSaldoCtaCte(1, "2017");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
