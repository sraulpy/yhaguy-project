package com.yhaguy.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.HistoricoCobranzaDiaria;
import com.yhaguy.domain.HistoricoCobranzaVendedor;
import com.yhaguy.domain.HistoricoMovimientoArticulo;
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
				
		HistoricoVentaVendedor hist = rr.getHistoricoVentaVendedor(anho, mes, 0, 2);
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
		HistoricoVentaVendedor hist = rr.getHistoricoVentaVendedor(anho, mes, 0, 2);
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
	
	/**
	 * genera el historico de movimientos articulos..
	 */
	public static void addHistoricoMovimientoArticulo(Date desde, Date hasta, String user) throws Exception {
		try {			
			RegisterDomain rr = RegisterDomain.getInstance();
			rr.deleteAllObjects(HistoricoMovimientoArticulo.class.getName());
			List<Object[]> ventas = rr.getVentas(desde, hasta);
			List<Object[]> values = new ArrayList<Object[]>();
			Map<Long, Long> arts = new HashMap<Long, Long>();
			Map<String, Integer> cants = new HashMap<String, Integer>();
			Map<Long, Object[]> stock1 = new HashMap<Long, Object[]>();
			Map<Long, Object[]> stock2 = new HashMap<Long, Object[]>();
			Map<Long, Object[]> stock3 = new HashMap<Long, Object[]>();
			Map<Long, Object[]> stock4 = new HashMap<Long, Object[]>();
			Map<Long, Object[]> stock5 = new HashMap<Long, Object[]>();
			Map<Long, Object[]> stock6 = new HashMap<Long, Object[]>();
			Map<Long, Object[]> stock7 = new HashMap<Long, Object[]>();
			Map<Long, Object[]> stock8 = new HashMap<Long, Object[]>();
			
			for (Object[] venta : ventas) {
				int mes = Utiles.getNumeroMes((Date) venta[19]);
				String cod = (String) venta[1];
				String key = cod + "-" + mes;
				Integer acum = cants.get(key);
				if (acum != null) {
					acum += ((Long) venta[18]).intValue();
					cants.put(key, acum);
				} else {
					cants.put(key, ((Long) venta[18]).intValue());
				}
				arts.put((long) venta[0], (long) venta[0]);
			}
			
			for (Long idArticulo : arts.keySet()) {
				Object[] st1 = rr.getStockArticulo(idArticulo, Deposito.ID_MINORISTA);
				Object[] st2 = rr.getStockArticulo(idArticulo, Deposito.ID_CENTRAL_TEMPORAL);
				Object[] st3 = rr.getStockArticulo(idArticulo, Deposito.ID_CENTRAL_RECLAMOS);
				Object[] st4 = rr.getStockArticulo(idArticulo, Deposito.ID_CENTRAL_REPOSICION);
				Object[] st5 = rr.getStockArticulo(idArticulo, Deposito.ID_MCAL_LOPEZ);
				Object[] st6 = rr.getStockArticulo(idArticulo, Deposito.ID_MCAL_TEMPORAL);
				Object[] st7 = rr.getStockArticulo(idArticulo, Deposito.ID_MAYORISTA);
				Object[] st8 = rr.getStockArticulo(idArticulo, Deposito.ID_MAYORISTA_TEMPORAL);
				stock1.put(idArticulo, st1);
				stock2.put(idArticulo, st2);
				stock3.put(idArticulo, st3);
				stock4.put(idArticulo, st4);
				stock5.put(idArticulo, st5);
				stock6.put(idArticulo, st6);
				stock7.put(idArticulo, st7);
				stock8.put(idArticulo, st8);
			}
			
			for (Object[] venta : ventas) {
				int mes = Utiles.getNumeroMes((Date) venta[19]);
				String cod = (String) venta[1];
				String key = cod + "-" + mes;
				Integer acum = cants.get(key);
				if (acum != null) {
					acum += ((Long) venta[18]).intValue();
					cants.put(key, acum);
				} else {
					cants.put(key, ((Long) venta[18]).intValue());
				}
			}
			
			for (Object[] venta : ventas) {
				Object[] compraLocal = rr.getUltimaCompraLocal((long) venta[0]);
				Object[] compraImpor = rr.getUltimaCompraImportacion((long) venta[0]);
				
				venta = Arrays.copyOf(venta, venta.length + 3);
				Date fcl = (Date) compraLocal[1];
				Date fcI = (Date) compraImpor[1];
				if (fcI == null || (fcl != null && fcl.compareTo(fcI) >= 0)) {
					venta[20] = compraLocal[0];
					venta[21] = compraLocal[1];
					venta[22] = compraLocal[2];
				} else {
					venta[20] = compraImpor[0];
					venta[21] = compraImpor[1];
					venta[22] = compraImpor[2];
				}
				values.add(venta);
			}
			
			for (Object[] det : values) {				
				String cod = (String) det[1];
				String codProveedor = (String) det[2];
				String referencia = (String) det[3];
				String nroParte = (String) det[4];
				String descripcion = (String) det[6];
				String ochentaVeinte = (String) det[7];
				String abc = (String) det[8];
				String familia = (String) det[9];
				String marca = (String) det[10];
				String linea = (String) det[11];
				String grupo = (String) det[12];
				String aplicacion = (String) det[13];
				String modelo = (String) det[14];
				String peso = Utiles.getNumberFormat((double) det[15]);
				String volumen = Utiles.getNumberFormat((double) det[16]);
				String proveedor = (String) det[17];
				String cantidad = det[20] + "";
				String fechaUltimaCompra = det[21] + "";
				String proveedoUltimaCompra = (String) det[22];
				
				Object[] st = stock1.get(det[0]);
				String dep_1 = st != null ? st[1] + "" : "0";
				
				Object[] st2 = stock2.get(det[0]);
				String dep_2 = st2 != null ? st2[1] + "" : "0";
				
				Object[] st3 = stock3.get(det[0]);
				String dep_3 = st3 != null ? st3[1] + "" : "0";
				
				Object[] st4 = stock4.get(det[0]);
				String dep_4 = st4 != null ? st4[1] + "" : "0";	
				
				Object[] st5 = stock5.get(det[0]);
				String dep_5 = st5 != null ? st5[1] + "" : "0";	
				
				Object[] st6 = stock6.get(det[0]);
				String dep_6 = st6 != null ? st6[1] + "" : "0";
				
				Object[] st7 = stock7.get(det[0]);
				String dep_7 = st7 != null ? st7[1] + "" : "0";	

				Object[] st8 = stock8.get(det[0]);
				String dep_8 = st8 != null ? st8[1] + "" : "0";
				
				Integer cantEnero = cants.get(cod + "-1");
				String enero = cantEnero != null ? cantEnero + "" : "0";
				
				Integer cantFebrero = cants.get(cod + "-2");
				String febrero = cantFebrero != null ? cantFebrero + "" : "0";
				
				Integer cantMarzo = cants.get(cod + "-3");
				String marzo = cantMarzo != null ? cantMarzo + "" : "0";
				
				Integer cantAbril = cants.get(cod + "-4");
				String abril = cantAbril != null ? cantAbril + "" : "0";
				
				Integer cantMayo = cants.get(cod + "-5");
				String mayo = cantMayo != null ? cantMayo + "" : "0";	
				
				Integer cantJunio = cants.get(cod + "-6");
				String junio = cantJunio != null ? cantJunio + "" : "0";
				
				Integer cantJulio = cants.get(cod + "-7");
				String julio = cantJulio != null ? cantJulio + "" : "0";
				
				Integer cantAgosto = cants.get(cod + "-8");
				String agosto = cantAgosto != null ? cantAgosto + "" : "0";
				
				Integer cantSetiembre = cants.get(cod + "-9");
				String setiembre = cantSetiembre != null ? cantSetiembre + "" : "0";
				
				Integer cantOctubre = cants.get(cod + "-10");
				String octubre = cantOctubre != null ? cantOctubre + "" : "0";
				
				Integer cantNoviembre = cants.get(cod + "-11");
				String noviembre = cantNoviembre != null ? cantNoviembre + "" : "0";
				
				Integer cantDiciembre = cants.get(cod + "-12");
				String diciembre = cantDiciembre != null ? cantDiciembre + "" : "0";
				
				HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
				hist.setCodigo(cod);
				hist.setCodigoProveedor(codProveedor);
				hist.setDescripcion(descripcion);
				hist.setReferencia(referencia);
				hist.setNroParte(nroParte);
				hist.setEstado((boolean) det[5] ? "ACTIVO" : "INACTIVO");
				hist.setArticulo("");
				hist.setFamilia(familia);
				hist.setMarca(marca);
				hist.setLinea(linea);
				hist.setGrupo(grupo);
				hist.setOchentaVeinte(ochentaVeinte);
				hist.setAbc(abc);
				hist.setAplicacion(aplicacion);
				hist.setModelo(modelo);
				hist.setPeso(peso);
				hist.setVolumen(volumen);
				hist.setProveedor(proveedor);
				hist.setCantidad(Long.parseLong(cantidad));
				hist.setStock1(Long.parseLong(dep_1));
				hist.setStock2(Long.parseLong(dep_2));
				hist.setStock3(Long.parseLong(dep_3));
				hist.setStock4(Long.parseLong(dep_4));
				hist.setStock5(Long.parseLong(dep_5));
				hist.setStock6(Long.parseLong(dep_6));
				hist.setStock7(Long.parseLong(dep_7));
				hist.setStock8(Long.parseLong(dep_8));
				hist.setStockGral(hist.getStock1() + hist.getStock2() + hist.getStock3() + hist.getStock4() + hist.getStock5() + hist.getStock6() + hist.getStock7() + hist.getStock8());
				hist.setStockMinimo(0);
				hist.setStockMaximo(0);
				hist.setFechaUltimaCompra(fechaUltimaCompra);
				hist.setProveedorUltimaCompra(proveedoUltimaCompra);
				hist.setFechaUltimaVenta("");
				hist.setCostoFob(0);
				hist.setCoeficiente(0);
				hist.setTipoCambio(0);
				hist.setCostoGs(0);
				hist.setMayoristaGs(0);
				hist.setClienteGral(0);
				hist.setClienteMesVigente(0);
				hist.setEnero(Long.parseLong(enero));
				hist.setFebrero(Long.parseLong(febrero));
				hist.setMarzo(Long.parseLong(marzo));
				hist.setAbril(Long.parseLong(abril));
				hist.setMayo(Long.parseLong(mayo));
				hist.setJunio(Long.parseLong(junio));
				hist.setJulio(Long.parseLong(julio));
				hist.setAgosto(Long.parseLong(agosto));
				hist.setSetiembre(Long.parseLong(setiembre));
				hist.setOctubre(Long.parseLong(octubre));
				hist.setNoviembre(Long.parseLong(noviembre));
				hist.setDiciembre(Long.parseLong(diciembre));
				hist.setTotal(hist.getEnero() + hist.getFebrero() + hist.getMarzo() + hist.getAbril() + hist.getMayo() + hist.getJunio()
						+ hist.getJulio() + hist.getAgosto() + hist.getSetiembre() + hist.getOctubre() + hist.getNoviembre() + hist.getDiciembre());
				
				rr.saveObject(hist, user);
				System.out.println(hist.getCodigo() + " - " + hist.getProveedor()); 			
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			ProcesosHistoricos.addHistoricoMovimientoArticulo(Utiles.getFecha("01-10-2018 00:00:00"), Utiles.getFecha("10-10-2018 00:00:00"), "sys");
			//ProcesosHistoricos.addHistoricoMovimientos();
			//ProcesosHistoricos.addHistoricoVentaMetaSucursal(new Double(2700000000L));
			//ProcesosHistoricos.updateHistoricoVentaDiaria(new Date(), 120000, 0);
			//ProcesosHistoricos.updateHistoricoSaldoCtaCte(1, "2017");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
