package com.yhaguy.process;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.coreweb.domain.Tipo;
import com.coreweb.domain.TipoTipo;
import com.coreweb.extras.csv.CSV;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.EmpresaCartera;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.HistoricoComisiones;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.ConnectDB;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.migracion.baterias.BAT_Costos;

public class ProcesosVentas {
	
	static final String SRC_RUBROS = "./WEB-INF/docs/procesos/RUBROS.csv";
	static final String SRC_EMPRESAS_RUBROS = "./WEB-INF/docs/procesos/EMPRESAS_RUBROS.csv";
	static final String SRC_MIGRACION_VTAS = "./WEB-INF/docs/migracion/central/MIGRACION_VENTAS.csv";
	static final String SRC_MIGRACION_VTAS_ANULADOS = "./WEB-INF/docs/migracion/central/MIGRACION_VENTAS_ANULADOS.csv";
	static final String SRC_CLIENTE_VENDEDOR = "./WEB-INF/docs/migracion/central/CLIENTE_VENDEDOR.csv";
	static final String SRC_VENTA = "./WEB-INF/docs/procesos/VENTA.csv";
	static final String SRC_CIUDADES = "./WEB-INF/docs/procesos/CIUDADES.csv";
	static final String SRC_NC_VENTA = "./WEB-INF/docs/procesos/NOTA_CREDITO.csv";

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
		List<Recibo> recibos = rr.getCobranzas(Utiles.getFecha("01-01-2016 00:00:00"), Utiles.getFecha("09-11-2016 00:00:00"), 0, 0, true, true, null);
		for (Recibo recibo : recibos) {
			for (ReciboDetalle item : recibo.getDetalles()) {
				Venta vta = (Venta) rr.getObject(Venta.class.getName(), item.getMovimiento().getIdMovimientoOriginal());
				if (vta.getNumeroReciboCobro() == null || vta.getNumeroReciboCobro().isEmpty()) {
					vta.setNumeroReciboCobro(recibo.getNumero());
				} else {
					vta.setNumeroReciboCobro(vta.getNumeroReciboCobro() + ";" + recibo.getNumero());
				}
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
	 * asigna vendedor a clientes..
	 */
	public static void setClienteVendedor() throws Exception {
		ConnectDB conn = ConnectDB.getInstance();
		String src = SRC_CLIENTE_VENDEDOR;
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "VENDEDOR", CSV.STRING }, { "CLIENTE", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String vendedor = csv.getDetalleString("VENDEDOR");
			String cliente = csv.getDetalleString("CLIENTE");	
			Cliente cli = rr.getClienteByRazonSocial(cliente.toUpperCase());
			Funcionario func = rr.getFuncionario(vendedor.toUpperCase());
			if (cli != null && func != null) {
				cli.getEmpresa().setVendedor(func);
				rr.saveObject(cli.getEmpresa(), cli.getEmpresa().getUsuarioMod());
				System.out.println("CLIENTE: " + cli.getRazonSocial() + " VENDEDOR: " + func.getRazonSocial());
			} else {
				ResultSet result = conn.getDatosCliente(cliente);
				if (!result.next()) {
					System.err.println(cliente + " - Ruc: - - -");
				} else {
					while (result.next()) {
						String ruc = (String) result.getObject(1);
						cli = rr.getClienteByRuc(ruc);
						if (cli != null) {
							System.out.println("ENCONTRADO: " + cli.getRazonSocial());
							rr.saveObject(cli.getEmpresa(), cli.getEmpresa().getUsuarioMod());
						} else {
							System.out.println("NO ENCONTRADO: " + cliente + " - " + ruc);
						}
					}
				}
			}
		}
	}
	
	/**
	 * historico comisiones..
	 */
	@SuppressWarnings("deprecation")
	public static void addHistoricoComisiones(Date desde, Date hasta, long idSucursal, long idVendedor) {
		try {	
			desde.setHours(0); desde.setMinutes(0); desde.setSeconds(0);
			hasta.setHours(23); hasta.setMinutes(0); hasta.setSeconds(0);
			Misc m = new Misc();
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Proveedor> proveedores = rr.getProveedoresExterior("");	
			Map<String, BeanComision> result = new HashMap<String, BeanComision>();

			List<Venta> ventas = null;
			List<Object[]> cobros = null;
			
			ventas = rr.getVentasContado(desde, hasta, 0, idVendedor);
			cobros = rr.getCobranzasPorVendedor(desde, hasta, idVendedor, idSucursal);
			
			// Ventas contado..
			for (Venta venta : ventas) {
				if (!venta.isAnulado()) {
					for (Proveedor prov : proveedores) {
						String key = venta.getVendedor().getId() + "-" + prov.getId();
						
						double importeVta = venta.getImporteByProveedor(prov.getId());
						double importeNcr = 0;
						
						NotaCredito nc = rr.getNotaCreditoVenta(venta.getId());
						if (nc != null && nc.isMotivoDescuento()) {
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
			
			if (idVendedor == 0) {
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
			} else {
				Funcionario vend = rr.getFuncionario(idVendedor);
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
	
	/**
	 * asigna rubros a clientes..
	 */
	public static void migrarVentas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "FECHA", CSV.STRING }, { "NROMOVIMIENTO", CSV.STRING }, { "RUC", CSV.STRING }, { "PERSONA", CSV.STRING },
				{ "IDTIPOMOVIMIENTO", CSV.STRING }, { "IDSUCURSAL", CSV.STRING }, { "IDMONEDA", CSV.STRING },
				{ "TOTALGRAVADA", CSV.STRING }, { "TOTALGRAVADAUSD", CSV.STRING }, { "TOTALIVA", CSV.STRING }, { "TOTALGRAVADA_", CSV.STRING }};
		
		Tipo gs = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		
		SucursalApp central = rr.getSucursalAppById(2);
		SucursalApp gam = rr.getSucursalAppById(4);
		SucursalApp mcal = rr.getSucursalAppById(3);
		TipoMovimiento contado = rr.getTipoMovimientoById(18);
		TipoMovimiento credito = rr.getTipoMovimientoById(19);
		Tipo iva10 = rr.getTipoById(124);
		Articulo articulo = rr.getArticulo("@MIGRACION");
		
		CSV csv = new CSV(cab, det, SRC_MIGRACION_VTAS_ANULADOS);
		csv.start();
		while (csv.hashNext()) {
			String fecha = csv.getDetalleString("FECHA");	
			String nro = csv.getDetalleString("NROMOVIMIENTO");	
			String ruc = csv.getDetalleString("RUC");	
			String razonsocial = csv.getDetalleString("PERSONA");
			String suc = csv.getDetalleString("IDSUCURSAL");	
			String idTm = csv.getDetalleString("IDTIPOMOVIMIENTO"); 
			String gravada = csv.getDetalleString("TOTALGRAVADA_");
			String iva = csv.getDetalleString("TOTALIVA");
			Cliente cliente = rr.getClienteByRuc(ruc);
			double gravada_ = Double.parseDouble(gravada.replace(",", "."));
			double iva_ = Double.parseDouble(iva.replace(",", "."));
			
			Set<VentaDetalle> dets = new HashSet<VentaDetalle>();
			VentaDetalle item = new VentaDetalle();
			item.setArticulo(articulo);
			item.setCantidad(1);
			item.setPrecioGs(gravada_ + iva_);
			item.setTipoIVA(iva10);
			dets.add(item);
			
			Venta vta = new Venta();
			vta.setCliente(cliente);
			vta.setDenominacion(razonsocial);
			vta.setFecha(Utiles.getFecha(fecha, "MM/dd/yyyy hh:mm:ss"));
			vta.setMoneda(gs);
			vta.setNumero("001-001-" + nro);
			vta.setObservacion("MIGRACION");
			vta.setSucursal(suc.equals("1") ? central : (suc.equals("2") ? mcal : gam));
			vta.setTipoMovimiento(idTm.equals("43") ? contado : credito);
			vta.setTotalImporteGs(gravada_ + iva_);
			vta.setDetalles(dets);
			vta.setEstadoComprobante(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO));
			rr.saveObject(vta, "migracion");
			System.out.println(vta.getTotalImporteGs());
		}
	}
	
	/**
	 * genera ventas..
	 */
	public static void generarVentas(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "NUMERO", CSV.STRING }, { "CODIGO", CSV.STRING }, { "CANTIDAD", CSV.STRING }, { "COSTO", CSV.STRING } };
		
		Tipo gs = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		
		SucursalApp central = rr.getSucursalAppById(2);
		Tipo iva10 = rr.getTipoById(124);
		Cliente cliente = rr.getClienteById(22133);
		Funcionario func = rr.getFuncionario_(2);
		Deposito deposito = (Deposito) rr.getObject(Deposito.class.getName(), 2);
		Set<VentaDetalle> dets = new HashSet<VentaDetalle>();
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) { 
			String cantidad = csv.getDetalleString("CANTIDAD");
			String costo = csv.getDetalleString("COSTO");
			String codigo = csv.getDetalleString("CODIGO");
			
			Articulo articulo = rr.getArticulo(codigo);			
			
			VentaDetalle item = new VentaDetalle();
			item.setArticulo(articulo);
			item.setCantidad(Long.parseLong(cantidad));
			item.setPrecioGs(Double.parseDouble(costo));
			item.setCostoUnitarioGs(articulo.getCostoGs());
			item.setDescripcion(articulo.getDescripcion());
			item.setListaPrecio(rr.getListaDePrecio(3));
			item.setTipoIVA(iva10);
			dets.add(item);
			System.out.println(item.getArticulo().getCodigoInterno());
		}
		
		Venta vta = new Venta();
		vta.setCliente(cliente);
		vta.setCondicionPago(rr.getCondicionPagoById(2));
		vta.setAtendido(func);
		vta.setCartera(EmpresaCartera.CORRIENTE);
		vta.setFormaEntrega(Venta.FORMA_ENTREGA_EMPAQUE);
		vta.setVendedor(func);
		vta.setTecnico(func);
		vta.setTipoCambio(1);
		vta.setVencimiento(Utiles.agregarDias(new Date(), 30));
		vta.setDenominacion(cliente.getRazonSocial());
		vta.setFecha(new Date());
		vta.setMoneda(gs);
		vta.setNumero("V-PED-" + AutoNumeroControl.getAutoNumero(Configuracion.NRO_VENTA_PEDIDO));
		vta.setObservacion("INVENTARIO GROUPAUTO");
		vta.setSucursal(central);
		vta.setDeposito(deposito);
		vta.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PEDIDO_VENTA));
		vta.setTotalImporteGs(vta.getTotalImporteGs_());
		vta.setDetalles(dets);
		vta.setEstado(rr.getTipoById(118));
		rr.saveObject(vta, "sys");
		System.out.println("PEDIDO GENERADO: " + vta.getNumero());
	}
	
	/**
	 * pobla las ciudades..
	 */
	public static void poblarCiudades(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CIUDAD", CSV.STRING }, { "DEPARTAMENTO", CSV.STRING } };

		TipoTipo tt = new TipoTipo();
		tt.setDescripcion("CIUDADES");
		rr.saveObject(tt, "sys");
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String ciudad = csv.getDetalleString("CIUDAD");
			String depmto = csv.getDetalleString("DEPARTAMENTO");
			Tipo tp = new Tipo();
			tp.setDescripcion(ciudad);
			tp.setSigla(depmto);
			tp.setTipoTipo(tt);
			rr.saveObject(tp, "sys");
			System.out.println(tp.getDescripcion());
		}
	}
	
	/**
	 * genera notas de credito compra..
	 */
	public static void generarNotasCredito(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "NUMERO", CSV.STRING }, { "CODIGO", CSV.STRING }, { "CANTIDAD", CSV.STRING }, { "COSTO", CSV.STRING } };
		
		Tipo gs = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		
		SucursalApp suc = rr.getSucursalAppById(2);
		Tipo iva10 = rr.getTipoById(124);
		Cliente cliente = rr.getClienteById(22133);
		Funcionario func = rr.getFuncionario_(2);
		Venta venta = (Venta) rr.getObject(Venta.class.getName(), 10212);
		HashMap<String, Set<NotaCreditoDetalle>> detalles = new HashMap<String, Set<NotaCreditoDetalle>>();
		
		// numeros desde 297 al 326 fecha 25/05
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) { 
			String numero = csv.getDetalleString("NUMERO");
			String cantidad = csv.getDetalleString("CANTIDAD");
			String costo = csv.getDetalleString("COSTO");
			String codigo = csv.getDetalleString("CODIGO");
			
			Set<NotaCreditoDetalle> dets = detalles.get(numero);
			if (dets == null) {
				dets = new HashSet<NotaCreditoDetalle>();
			}
		
			Articulo articulo = rr.getArticulo(codigo);	
			
			NotaCreditoDetalle item = new NotaCreditoDetalle();
			item.setArticulo(articulo);
			item.setCantidad(Integer.parseInt(cantidad));
			item.setCostoGs(articulo.getCostoGs());
			item.setMontoGs(Double.parseDouble(costo));
			item.setImporteGs(item.getMontoGs() * item.getCantidad());
			item.setTipoIva(iva10);
			item.setTipoDetalle(rr.getTipoById(214));
			item.setVenta(venta);
			dets.add(item);
			detalles.put(numero, dets);
			System.out.println(item.getArticulo().getCodigoInterno());
		}
		
		for (String key : detalles.keySet()) {
			NotaCreditoDetalle item = new NotaCreditoDetalle();
			item.setArticulo(null);
			item.setCantidad(1);
			item.setTipoIva(iva10);
			item.setTipoDetalle(rr.getTipoById(213));
			item.setVenta(venta);
			
			Set<NotaCreditoDetalle> dets = detalles.get(key);
			dets.add(item);
			
			NotaCredito nc = new NotaCredito();
			nc.setCliente(cliente);
			nc.setDeposito((Deposito) rr.getObject(Deposito.class.getName(), 2));
			nc.setDetalles(dets);
			nc.setEstadoComprobante(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_APROBADO));
			nc.setFechaEmision(Utiles.getFecha("25-05-2020", Utiles.DD_MM_YYYY));
			nc.setMoneda(gs);
			nc.setNumero(key);
			nc.setObservacion("INVENTARIO GROUPAUTO");
			nc.setSucursal(suc);
			nc.setTimbrado_("");
			nc.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA));
			nc.setAuxi(NotaCredito.NCR_CREDITO);
			nc.setVendedor(func);
			rr.saveObject(nc, "sys");
			System.out.println("NC GENERADO: " + nc.getNumero());
		}
	}
	
	public static void main(String[] args) {
		try {
			//ProcesosVentas.setNumeroPlanillaCaja(201, 212);
			//ProcesosVentas.setCostoVentas();
			//ProcesosVentas.setCostoNotasCredito();
			//ProcesosVentas.setNumeroRecibosCobros();
			//ProcesosVentas.addRubros(SRC_RUBROS);
			//ProcesosVentas.setRubros(SRC_EMPRESAS_RUBROS);
			//ProcesosVentas.addHistoricoComisiones(Utiles.getFecha("01-03-2017 00:00:00"), Utiles.getFecha("31-03-2017 23:00:00"), 2);
			//ProcesosVentas.migrarVentas();
			//ProcesosVentas.setClienteVendedor();
			ProcesosVentas.generarVentas(SRC_VENTA);
			//ProcesosVentas.poblarCiudades(SRC_CIUDADES);
			//ProcesosVentas.generarNotasCredito(SRC_NC_VENTA);
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
