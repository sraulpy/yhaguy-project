package com.yhaguy.process;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coreweb.domain.Tipo;
import com.coreweb.domain.TipoTipo;
import com.coreweb.extras.csv.CSV;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.HistoricoComisiones;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.migracion.baterias.BAT_Costos;

public class ProcesosVentas {
	
	static final String SRC_RUBROS = "./WEB-INF/docs/procesos/RUBROS.csv";
	static final String SRC_EMPRESAS_RUBROS = "./WEB-INF/docs/procesos/EMPRESAS_RUBROS.csv";

	/**
	 * setea el numero de planilla de caja 
	 * de la venta..
	 */
	public static void setNumeroPlanillaCaja(long idDesde, long idHasta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		for (long i = idDesde; i <= idHasta; i++) {
			CajaPeriodo planilla = (CajaPeriodo) rr.getObject(CajaPeriodo.class.getName(), i);
			for (Venta venta : planilla.getVentas()) {
				System.out.println("-VTA: " + venta.getNumero() + " -PLANILLA: " + planilla.getNumero());
				venta.setNumeroPlanillaCaja(planilla.getNumero());
				rr.saveObject(venta, "sys");
			}
		}
	}
	
	/**
	 * setear costos de ventas
	 */
	public static void setCostoVentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> ventas = rr.getVentas(Utiles.getFecha("01-08-2016 00:00:00"), Utiles.getFecha("10-10-2016 00:00:00"), 0);
		Map<String, Double> costos = BAT_Costos.getCostosOld();
		for (Venta venta : ventas) {
			for (VentaDetalle item : venta.getDetalles()) {
				if (!item.getArticulo().getCodigoInterno().startsWith("@")) {
					if (costos.get(item.getArticulo().getCodigoInterno()) != null) {
						item.setCostoUnitarioGs(costos.get(item.getArticulo().getCodigoInterno()));
						rr.saveObject(item, "sys");
						System.out.println("Venta nro. " + venta.getNumero() + " " + venta.getFecha() + " actualizado..");
					}
					/*ArticuloDeposito adp = rr.getArticuloDeposito(item.getArticulo().getId(), 2);
					item.setCostoUnitarioGs(adp.getCosto());*/
				}
			}
		}
	}
	
	/**
	 * setear costos de notas de credito..
	 */
	public static void setCostoNotasCredito() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Map<String, Double> costos = BAT_Costos.getCostosOld();
		List<NotaCredito> notascredito = rr.getNotasCreditoVenta(Utiles.getFecha("01-08-2016 00:00:00"), Utiles.getFecha("01-11-2016 00:00:00"), 0);
		for (NotaCredito nc : notascredito) {
			for (NotaCreditoDetalle item : nc.getDetalles()) {
				if (item.getArticulo() != null && (!item.getArticulo().getCodigoInterno().startsWith("@"))) {
					if (costos.get(item.getArticulo().getCodigoInterno()) != null) {
						item.setCostoGs(costos.get(item.getArticulo().getCodigoInterno()));
						rr.saveObject(item, "sys");
						System.out.println("Nota credito nro. " + nc.getNumero() + " actualizado.. " + item.getCostoGs());
					}					
					/*ArticuloDeposito adp = rr.getArticuloDeposito(item.getArticulo().getId(), 2);
					item.setCostoGs(adp.getCosto());
					rr.saveObject(item, "sys");
					System.out.println("Nota credito nro. " + nc.getNumero() + " actualizado.. " + item.getCostoGs());*/
				}
			}
		}
	}
	
	/**
	 * asigna los numeros de recibos de cobros a las ventas..
	 */
	public static void setNumeroRecibosCobros() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> recibos = rr.getCobranzas(Utiles.getFecha("01-01-2016 00:00:00"), Utiles.getFecha("09-11-2016 00:00:00"), 0);
		for (Recibo recibo : recibos) {
			for (ReciboDetalle item : recibo.getDetalles()) {
				Venta vta = (Venta) rr.getObject(Venta.class.getName(), item.getMovimiento().getIdMovimientoOriginal());
				if (vta.getNumeroReciboCobro() == null || vta.getNumeroReciboCobro().isEmpty()) {
					vta.setNumeroReciboCobro(recibo.getNumero());
				} else {
					vta.setNumeroReciboCobro(vta.getNumeroReciboCobro() + ";" + recibo.getNumero());
				}
				System.out.println("Venta " + vta.getNumero() + " asignada: " + recibo.getNumero());
				rr.saveObject(vta, "sys");
			}
		}
	}
	
	/**
	 * agrega rubros..
	 */
	public static void addRubros(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		TipoTipo tt = (TipoTipo) rr.getObject(TipoTipo.class.getName(), 39);
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "DESCRIPCION", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {
			String desc = csv.getDetalleString("DESCRIPCION");	
			Tipo tipo = new Tipo();
			tipo.setTipoTipo(tt);
			tipo.setSigla("RUB-EMP");
			tipo.setDescripcion(desc);
			rr.saveObject(tipo, "process");
			System.out.println("TIPO AGREGADO: " + tipo.getDescripcion());
		}
	}
	
	/**
	 * asigna rubros a clientes..
	 */
	public static void setRubros(String src) throws Exception {
		Map<String, Tipo> rubros = new HashMap<String, Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "RUC", CSV.STRING }, { "DESCRIPCION", CSV.STRING } };
		
		List<Tipo> rubros_ = rr.getTipos("Rubros Empresas");
		for (Tipo tipo : rubros_) {
			rubros.put(tipo.getDescripcion(), tipo);
		}
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String ruc = csv.getDetalleString("RUC");
			String desc = csv.getDetalleString("DESCRIPCION");	
			Cliente cli = rr.getClienteByRuc(ruc);
			Tipo rubro = rubros.get(desc);
			if (cli != null && rubro != null) {
				cli.getEmpresa().getRubroEmpresas().add(rubro);
				rr.saveObject(cli, "process");
				System.out.println("CLIENTE: " + cli.getRazonSocial() + " RUBRO: " + rubro.getDescripcion());
			}
		}
	}
	
	/**
	 * historico comisiones..
	 */
	@SuppressWarnings("deprecation")
	public static void addHistoricoComisiones(Date desde, Date hasta, long idSucursal) {
		try {	
			desde.setHours(0); desde.setMinutes(0); desde.setSeconds(0);
			hasta.setHours(23); hasta.setMinutes(0); hasta.setSeconds(0);
			Misc m = new Misc();
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Proveedor> proveedores = rr.getProveedoresExterior("");	
			Map<String, BeanComision> result = new HashMap<String, BeanComision>();

			List<Venta> ventas = null;
			List<Object[]> cobros = null;
			
			ventas = rr.getVentasContado(desde, hasta, 0);
			cobros = rr.getCobranzasPorVendedor(desde, hasta, 0, idSucursal);
			
			// Ventas contado..
			for (Venta venta : ventas) {
				if (!venta.isAnulado()) {
					for (Proveedor prov : proveedores) {
						String key = venta.getVendedor().getId() + "-" + prov.getId();
						
						double importeVta = venta.getImporteByProveedor(prov.getId());
						double importeNcr = 0;
						
						NotaCredito nc = rr.getNotaCreditoVenta(venta.getId());
						if (nc != null) {
							int cantidad = venta.getCantidadItemsByProveedor(prov.getId());
							double monto = (nc.getImporteGs() / venta.getDetalles().size()) * cantidad;
							importeNcr += (monto - Utiles.getIVA(monto, 10));
						}
						
						BeanComision bc = result.get(key);
						if(bc == null) bc = new BeanComision();							
						bc.setImporteVenta(bc.getImporteVenta() + ((importeVta - Utiles.getIVA(importeVta, 10)) 
								- (importeNcr - Utiles.getIVA(importeNcr, Configuracion.VALOR_IVA_10))));
						bc.setImporteNcred(bc.getImporteNcred() + importeNcr - Utiles.getIVA(importeNcr, Configuracion.VALOR_IVA_10));
						result.put(key, bc);
						System.out.println("VENTA: " + venta.getNumero());
					}
				}					
			}
			int index = 0;
			
			// Cobranzas..
			for (Object[] cobro : cobros) {
				ReciboDetalle det = (ReciboDetalle) cobro[3];
				long idVend = (long) cobro[4];
				double importeCobrado = (double) cobro[2];
				
				for (Proveedor prov : proveedores) {
					String key = idVend + "-" + prov.getId();
					
					double porc = 0;
					double importeCobro = 0;
					double importeNcr = 0;
					double importeProv = det.getImporteByProveedor(prov.getId());
					double importeVenta = det.getImporteVenta();
					
					porc = Utiles.obtenerPorcentajeDelValor(importeProv, importeVenta);
					importeCobro = m.obtenerValorDelPorcentaje(importeCobrado, porc);
					
					BeanComision bc = result.get(key);
					if(bc == null) {
						bc = new BeanComision();							
					}
					bc.setImporteCobro(bc.getImporteCobro() + 
							((importeCobro - Utiles.getIVA(importeCobro, Configuracion.VALOR_IVA_10))));
					bc.setImporteNcred(bc.getImporteNcred() + importeNcr - Utiles.getIVA(importeNcr, Configuracion.VALOR_IVA_10));
					result.put(key, bc);
					System.out.println("COBRO: " + cobro[1] + " - " + index + " de " + cobros.size());
					index ++;
				}						
			}
			
			for (Funcionario vend : rr.getFuncionarios("")) {
				for (Proveedor proveedor : proveedores) {
					BeanComision bc = result.get(vend.getId() + "-" + proveedor.getId());
					if (bc != null) {
						HistoricoComisiones com = new HistoricoComisiones();
						com.setMes(Utiles.getNumeroMes(hasta));
						com.setVendedor(vend.getRazonSocial());
						com.setProveedor(proveedor.getRazonSocial());
						com.setImporteVenta(bc.getImporteVenta());
						com.setImporteCobro(bc.getImporteCobro());
						com.setImporteNotaCredito(bc.getImporteNcred());
						com.setAnho(Utiles.getAnhoActual());
						
						Object[] porc_com = vend.getPorcentajeComision(proveedor.getId());
						double porc_vta = (double) porc_com[0];
						double porc_cob = (double) porc_com[1];
						com.setPorc_Venta(porc_vta);
						com.setPorc_Cobro(porc_cob);
						rr.saveObject(com, "process");
						System.out.println("AGREGADO: " + com.getVendedor());
					}
				}
			}		

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addHistoricoVentaVendedor() {
		
	}
	
	public static void main(String[] args) {
		try {
			//ProcesosVentas.setNumeroPlanillaCaja(201, 212);
			//ProcesosVentas.setCostoVentas();
			//ProcesosVentas.setCostoNotasCredito();
			//ProcesosVentas.setNumeroRecibosCobros();
			//ProcesosVentas.addRubros(SRC_RUBROS);
			//ProcesosVentas.setRubros(SRC_EMPRESAS_RUBROS);
			ProcesosVentas.addHistoricoComisiones(Utiles.getFecha("01-03-2017 00:00:00"), Utiles.getFecha("31-03-2017 23:00:00"), 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class BeanComision {
	double importeVenta;
	double importeCobro;
	double importeNcred;

	public double getImporteVenta() {
		return importeVenta;
	}

	public void setImporteVenta(double importeVenta) {
		this.importeVenta = importeVenta;
	}

	public double getImporteCobro() {
		return importeCobro;
	}

	public void setImporteCobro(double importeCobro) {
		this.importeCobro = importeCobro;
	}

	public double getImporteNcred() {
		return importeNcred;
	}

	public void setImporteNcred(double importeNcred) {
		this.importeNcred = importeNcred;
	}
}
