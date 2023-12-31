package com.yhaguy.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.coreweb.domain.Tipo;
import com.coreweb.extras.csv.CSV;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloCosto;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ArticuloMarca;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.CompraLocalOrdenDetalle;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.HistoricoMovimientoArticulo;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.ProveedorArticulo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.TransferenciaDetalle;
import com.yhaguy.gestion.comun.ControlArticuloStock;
import com.yhaguy.util.Barcode;
import com.yhaguy.util.Utiles;

@SuppressWarnings("unchecked")
public class ProcesosArticulos {
	
	static final String SRC_CUBIERTAS = "./WEB-INF/docs/process/CUBIERTAS.csv";
	static final String SRC_REPUESTOS = "./WEB-INF/docs/process/REPUESTOS.csv";
	static final String SRC_BATERIAS = "./WEB-INF/docs/process/BATERIAS.csv";
	static final String SRC_FAMILIAS_MARCAS = "./WEB-INF/docs/process/ARTICULO_FAMILIA_MARCA.csv";
	static final String SRC_FAMILIAS_MRA = "./WEB-INF/docs/process/FAMILIAS_MRA.csv";
	
	static final String SRC_VALVOLINE = "./WEB-INF/docs/process/VALVOLINE.csv";
	static final String SRC_JHONSON = "./WEB-INF/docs/process/JHONSON.csv";
	static final String SRC_BATEBOL = "./WEB-INF/docs/process/BATEBOL.csv";
	static final String SRC_INVENTARIO_MINORISTA = "./WEB-INF/docs/migracion/central/INVENTARIO_MINORISTA.csv";
	static final String SRC_INVENTARIO_MAYORISTA = "./WEB-INF/docs/migracion/central/INVENTARIO_MAYORISTA.csv";
	static final String SRC_INVENTARIO_MCAL = "./WEB-INF/docs/migracion/central/INVENTARIO_MCAL.csv";
	
	static final String SRC_HISTORICO_MOVIMIENTOS = "./WEB-INF/docs/procesos/ZF_BRASIL_MARZO19.csv";
	static final String SRC_PRECIOS = "./WEB-INF/docs/procesos/PRECIOS.csv";
	static final String SRC_MARCAS_FAMILIAS = "./WEB-INF/docs/procesos/MARCA_FAMILIA.csv";
	static final String SRC_PRECIOS_MRA = "./WEB-INF/docs/procesos/PRECIOS_MRA.csv";
	static final String SRC_STOCK_MRA = "./WEB-INF/docs/procesos/STOCK_MRA.csv";
	
	static final String SRC_INV_BATERIAS_MRA = "./WEB-INF/docs/procesos/mra/BATERIAS_INVENTARIO.csv";
	static final String SRC_INV_FILTROS_MRA = "./WEB-INF/docs/procesos/mra/FILTROS_INVENTARIO.csv";
	static final String SRC_INV_LUBRICANTES_MRA = "./WEB-INF/docs/procesos/mra/LUBRICANTES_INVENTARIO.csv";
	static final String SRC_INV_CUBIERTAS_MRA = "./WEB-INF/docs/procesos/mra/CUBIERTAS_INVENTARIO.csv";
	static final String SRC_INV_REPUESTOS_MRA = "./WEB-INF/docs/procesos/mra/REPUESTOS_INVENTARIO.csv";
	
	static final String SRC_INV_CUBIERTAS_AUT = "./WEB-INF/docs/procesos/autocentro/cubiertas.csv";
	
	/**
	 * asigna familia a los articulos..
	 */
	public static void setFamiliaArticulos(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");	
			String descFamilia = csv.getDetalleString("FAMILIA");
			Articulo art = rr.getArticuloByCodigoInterno(codigo);
			Tipo familia = rr.getTipoPorDescripcion(descFamilia);
			if (art != null) {
				art.setArticuloFamilia(familia);
				rr.saveObject(art, "sys");
				System.out.println(art.getCodigoInterno() + " - familia asignada: " + descFamilia);
			}
		}
	}
	
	/**
	 * asigna familia y marca a los articulos..
	 */
	public static void setMarcaFamiliaArticulos(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "FAMILIA", CSV.STRING }, { "MARCA", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");	
			String descFamilia = csv.getDetalleString("FAMILIA");
			String descMarca = csv.getDetalleString("MARCA");
			Articulo art = rr.getArticuloByCodigoInterno(codigo);
			
			Tipo familia = rr.getTipoPorDescripcion(descFamilia);
			Tipo marca = rr.getTipoPorDescripcion(descMarca);
			
			if (art != null && familia != null && marca != null) {
				art.setArticuloFamilia(familia);
				art.setArticuloMarca(marca);
				rr.saveObject(art, "sys");
				System.out.println(art.getCodigoInterno() + " - familia y marca asignada..");
			}
		}
	}
	
	
	/**
	 * asigna proveedor a los articulos..
	 */
	public static void setProveedorArticulos(String src, long idProveedor) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		Proveedor proveedor = rr.getProveedorById(idProveedor);
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");			
			Articulo art = rr.getArticuloByCodigoInterno(codigo);
			if (art != null) {
				ProveedorArticulo provArt = new ProveedorArticulo();
				provArt.setCodigoFabrica(art.getCodigoInterno());
				provArt.setProveedor(proveedor);
				
				art.getProveedorArticulos().add(provArt);
				rr.saveObject(art, "sys");
				System.out.println(art.getCodigoInterno() + " - proveedor asignado..");
			}
		}
	}
	

	/**
	 * recalculo de stock de articulos..
	 * respeta el total de entrada - salida, respeta el stock de cada deposito
	 * y la diferencia lo transfiere al deposito principal..
	 */
	public static void recalcularStock(long idDeposito, long idSucursal) throws Exception {		
		
		String desde="01-01-2016 00:00:00";
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date desde_ = formatter.parse(desde);
		Date hasta_ = new Date();
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloDeposito> adps = rr.getArticulosPorDeposito(idDeposito);
		
		for (ArticuloDeposito adp : adps) {
			
			if (!adp.getArticulo().getCodigoInterno().startsWith("@")) {	
				List<Object[]> historicoEntrada;
				List<Object[]> historicoSalida;
				
				long entrada = 0;
				long salida = 0;
				long stock = adp.getStock();
				
				long idArticulo = adp.getArticulo().getId();
				
				List<Object[]> ventas = rr.getVentasPorArticulo(idArticulo, desde_, hasta_);
				List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(idArticulo, desde_, hasta_);
				List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(idArticulo, desde_, hasta_);
				List<Object[]> compras = rr.getComprasLocalesPorArticulo(idArticulo, desde_, hasta_);
				List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(idArticulo, desde_, hasta_);
				List<Object[]> transferencias = rr.getTransferenciasPorArticulo(idArticulo, desde_, hasta_);
				List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(idArticulo, desde_, hasta_, idSucursal, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
				List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idArticulo, desde_, hasta_, idSucursal, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
				List<Object[]> migracion = rr.getMigracionPorArticulo(adp.getArticulo().getCodigoInterno(), desde_, hasta_, idSucursal);
				
				historicoEntrada = new ArrayList<Object[]>();
				historicoEntrada.addAll(migracion);
				historicoEntrada.addAll(ajustStockPost);		
				historicoEntrada.addAll(ntcsv);
				historicoEntrada.addAll(compras);
				historicoEntrada.addAll(importaciones);
				
				historicoSalida = new ArrayList<Object[]>();
				historicoSalida.addAll(ajustStockNeg);
				historicoSalida.addAll(ventas);
				historicoSalida.addAll(ntcsc);
				
				for (Object[] transf : transferencias) {
					long idsuc = (long) transf[6];
					if((idsuc == idSucursal)) {
						historicoSalida.add(transf);
					} else {				
						historicoEntrada.add(transf);
					}
				}
				
				for(Object[] obj : historicoEntrada){
					entrada += Long.parseLong(String.valueOf(obj[3]));
				}
				
				for(Object[] obj : historicoSalida){
					long cantidad = Long.parseLong(String.valueOf(obj[3]));
					if(cantidad < 0)
						cantidad = cantidad * -1;
					salida += cantidad;
				}
				
				if (stock - (entrada - salida) != 0) {
					long recalc = (entrada - salida);
					if (recalc < 0) {
						adp.setStock(0);
					} else {
						adp.setStock(recalc);
					}
					adp.setAuxi("Stock recalculado [" + adp.getStock() + "] "  + new Date());
					rr.saveObject(adp, "sys");
					System.out.println(adp.getArticulo().getCodigoInterno() + " recalculado..");
				}	
			}			
		}
		
		for (long i = 3; i <= 11; i++) {
			if (i != idDeposito ) {
				List<ArticuloDeposito> adps_ = rr.getArticulosPorDeposito(i);
				for (ArticuloDeposito adp_ : adps_) {
					ArticuloDeposito adp = rr.getArticuloDeposito(adp_.getArticulo().getId(), idDeposito);
					if (adp != null && !adp.getArticulo().getCodigoInterno().startsWith("@")) {
						adp.setStock(adp.getStock() - adp_.getStock());
						rr.saveObject(adp, "sys");
						System.out.println(adp.getArticulo().getCodigoInterno() + " deposito recalculado..");
					}
				}
			}
		}
	}
	
	/**
	 * recalculo de stock de articulos..
	 * respeta el total de entrada - salida, respeta el stock de cada deposito
	 * y la diferencia lo transfiere al deposito de control..
	 */
	public static void recalcularStock_(long idDeposito, long idSucursal, long idArticulo) throws Exception {		
		
		String desde="01-01-2016 00:00:00";
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date desde_ = formatter.parse(desde);
		Date hasta_ = new Date();
		long idDepositoControl = 13;
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Articulo> arts = new ArrayList<Articulo>();
		
		if (idArticulo <= 0) {
			arts = rr.getArticulos();
		} else {
			Articulo art = rr.getArticuloById(idArticulo);
			arts.add(art);
		}
		
		for (Articulo art : arts) {
			ArticuloDeposito adp = rr.getArticuloDeposito(art.getId(), idDepositoControl);
			if (adp == null) {
				adp = new ArticuloDeposito();
				adp.setArticulo(art);
				adp.setDeposito((Deposito) rr.getObject(Deposito.class.getName(), idDepositoControl));
				adp.setStock(0);
				adp.setStockMaximo(0);
				adp.setStockMinimo(0);
				adp.setUbicacion("");
				rr.saveObject(adp, "process");
			}
		}
		
		List<ArticuloDeposito> adps = new ArrayList<ArticuloDeposito>();
		
		if (idArticulo <= 0) {
			adps = rr.getArticulosPorDeposito(idDepositoControl);
		} else {
			ArticuloDeposito adp = rr.getArticuloDeposito(idArticulo, idDepositoControl);
			adps.add(adp);
		}
		
		for (ArticuloDeposito adp : adps) {
			
			if (!adp.getArticulo().getCodigoInterno().startsWith("@")) {	
				List<Object[]> historicoEntrada;
				List<Object[]> historicoSalida;
				
				long entrada = 0;
				long salida = 0;
				long stock = adp.getStock();
				
				long idarticulo = adp.getArticulo().getId();
				//long iddeposito = adp.getDeposito().getId();
				
				List<Object[]> ventas = rr.getVentasPorArticulo(idarticulo, desde_, hasta_);
				List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(idarticulo, desde_, hasta_);
				List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(idarticulo, desde_, hasta_);
				List<Object[]> compras = rr.getComprasLocalesPorArticulo(idarticulo, desde_, hasta_);
				List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(idarticulo, desde_, hasta_);
				List<Object[]> transfs = rr.getTransferenciasPorArticulo(idarticulo, desde_, hasta_);
				List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(idarticulo, desde_, hasta_, idSucursal, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
				List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(idarticulo, desde_, hasta_, idSucursal, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
				List<Object[]> migracion = rr.getMigracionPorArticulo(adp.getArticulo().getCodigoInterno(), desde_, hasta_, idSucursal);
				
				historicoEntrada = new ArrayList<Object[]>();
				historicoEntrada.addAll(migracion);
				historicoEntrada.addAll(ajustStockPost);		
				historicoEntrada.addAll(ntcsv);
				historicoEntrada.addAll(compras);
				historicoEntrada.addAll(importaciones);
				
				historicoSalida = new ArrayList<Object[]>();
				historicoSalida.addAll(ajustStockNeg);
				historicoSalida.addAll(ventas);
				historicoSalida.addAll(ntcsc);
				
				for (Object[] transf : transfs) {
					long idsuc = (long) transf[6];
					if((idsuc == idSucursal)) {
						historicoSalida.add(transf);
					} else {				
						historicoEntrada.add(transf);
					}
				}
				
				for(Object[] obj : historicoEntrada){
					entrada += Long.parseLong(String.valueOf(obj[3]));
				}
				
				for(Object[] obj : historicoSalida){
					long cantidad = Long.parseLong(String.valueOf(obj[3]));
					if(cantidad < 0) cantidad = cantidad * -1;
					salida += cantidad;
				}
				
				if (stock - (entrada - salida) != 0) {
					long recalc = (entrada - salida);
					adp.setStock(recalc);
					adp.setAuxi("Stock recalculado [" + adp.getStock() + "] "  + new Date());
					rr.saveObject(adp, "sys");
					System.out.println(adp.getArticulo().getCodigoInterno() + " recalculado..");
				}	
			}			
		}
		
		for (long i = 2; i <= 18; i++) {
			if (i != idDepositoControl ) {
				List<ArticuloDeposito> adps_ = new ArrayList<ArticuloDeposito>();
				if (idArticulo <= 0) {
					adps_ = rr.getArticulosPorDeposito(i);
				} else {
					ArticuloDeposito adp = rr.getArticuloDeposito(idArticulo, i);
					if (adp != null) {
						adps_.add(adp);
					}
				}
				for (ArticuloDeposito adp_ : adps_) {
					ArticuloDeposito adp = rr.getArticuloDeposito(adp_.getArticulo().getId(), idDepositoControl);
					if (adp != null && !adp.getArticulo().getCodigoInterno().startsWith("@")) {
						if (adp_.getStock() < 0) {
							adp_.setStock(0);
							rr.saveObject(adp_, "sys");
						} else {
							adp.setStock(adp.getStock() - adp_.getStock());
							rr.saveObject(adp, "sys");
						}
						System.out.println(adp.getArticulo().getCodigoInterno() + " deposito recalculado..");
					}
				}
			}
		}
	}
	
	/**
	 * verifica el ultimo costo de los articulos..
	 */
	public static void verificarUltimoCosto() throws Exception {/*
		String desde="01-01-2016 00:00:00";
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date desde_ = formatter.parse(desde);
		Date hasta_ = new Date();
		Misc misc = new Misc();
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CompraLocalFactura> compras = rr.getComprasLocales(desde_, hasta_);
		for (CompraLocalFactura compra : compras) {
			for (CompraLocalFacturaDetalle item : compra.getDetalles()) {
				ArticuloDeposito adep = rr.getArticuloDeposito(item.getArticulo().getId(), 2);
				if ((adep != null) && (adep.getCosto() - item.getCostoGs()) == 0) {
					double costoSinIva = misc.redondeoDosDecimales(misc.calcularGravado(item.getCostoGs(), 10));
					adep.setCosto(costoSinIva);
					rr.saveObject(adep, "sys");
					System.out.println(item.getArticulo().getCodigoInterno() + " -Costo s/iva: " + costoSinIva);
				}
			}
		}
	*/}
	
	/**
	 * setea el costo a los articulos..
	 */
	public static void setCostoArticulos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Articulo> arts = rr.getObjects(Articulo.class.getName());
		for (Articulo art : arts) {
			double costo = rr.getUltimoCostoGs(art.getId());
			art.setCostoGs(costo);
			rr.saveObject(art, "process");
			System.out.println(art.getCodigoInterno());
		}
	}
	
	/**
	 * setea el articulo al historial de costos del mismo..
	 */
	public static void setHistorialCostoArticulo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloCosto> arts = rr.getObjects(ArticuloCosto.class.getName());
		for (ArticuloCosto art : arts) {
			//art.setArticulo(art.getArticuloDep().getArticulo());
			//rr.saveObject(art, "process");
			System.out.println(art.getArticulo().getCodigoInterno());
		}
	}
	
	/**
	 * pobla los datos del historico de stock del articulo..
	 */
	public static void poblarHistoricoStock() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Articulo> arts = rr.getArticulos();
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		Date hasta = new Date();

		Calendar start = Calendar.getInstance();
		start.setTime(Utiles.getFecha("01-01-2017 00:00:00"));
		Calendar end = Calendar.getInstance();
		end.setTime(hasta);

		for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
			for (Articulo art : arts) {
				if (!art.getCodigoInterno().startsWith("@")) {
					List<Object[]> historicoEntrada = new ArrayList<Object[]>();
					List<Object[]> historicoSalida = new ArrayList<Object[]>();
					long stockEntrada = 0;
					long stockSalida = 0;
					
					List<Object[]> ventas = rr.getVentasPorArticulo(art.getId(), desde, date);
					List<Object[]> ntcsv = rr.getNotasCreditoVtaPorArticulo(art.getId(), desde, date);
					List<Object[]> ntcsc = rr.getNotasCreditoCompraPorArticulo(art.getId(), desde, date);
					List<Object[]> compras = rr.getComprasLocalesPorArticulo(art.getId(), desde, date);
					List<Object[]> importaciones = rr.getComprasImportacionPorArticulo(art.getId(), desde, date);
					List<Object[]> transfs = rr.getTransferenciasPorArticulo(art.getId(), desde, date);
					List<Object[]> ajustStockPost = rr.getAjustesPorArticulo(art.getId(), desde, date, 2, Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
					List<Object[]> ajustStockNeg = rr.getAjustesPorArticulo(art.getId(), desde, date, 2, Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
					List<Object[]> migracion = rr.getMigracionPorArticulo(art.getCodigoInterno(), desde, date, 2);
					
					historicoEntrada = new ArrayList<Object[]>();
					historicoEntrada.addAll(migracion);
					historicoEntrada.addAll(ajustStockPost);		
					historicoEntrada.addAll(ntcsv);
					historicoEntrada.addAll(compras);
					historicoEntrada.addAll(importaciones);
					
					historicoSalida = new ArrayList<Object[]>();
					historicoSalida.addAll(ajustStockNeg);
					historicoSalida.addAll(ventas);
					historicoSalida.addAll(ntcsc);
					
					for (Object[] transf : transfs) {
						long idsuc = (long) transf[6];
						if(idsuc == 2) {
							historicoSalida.add(transf);
						} else {				
							historicoEntrada.add(transf);
						}
					}
					
					for(Object[] obj : historicoEntrada){
						stockEntrada += Long.parseLong(String.valueOf(obj[3]));
					}
					
					for(Object[] obj : historicoSalida){
						long cantidad = Long.parseLong(String.valueOf(obj[3]));
						if(cantidad < 0)
							cantidad = cantidad * -1;
						stockSalida += cantidad;
					}	
					System.out.println(Utiles.getDateToString(date, Utiles.DD_MM_YY) 
							+ " stock: " + (stockEntrada - stockSalida) + " - " + art.getCodigoInterno());
				}
			}		    
		}		
	}
	
	/**
	 * Genera los codigos de barra..
	 */
	public static void generarBarcodes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Articulo> arts = rr.getArticulos(11, 0, 0);
		for (Articulo art : arts) {
			Barcode.generarBarcode(art.getCodigoInterno(), art.getDescripcion());
			System.out.println(art.getCodigoInterno());		
		}
	}
	
	/**
	 * crea un ajuste de stock..
	 */
	public static void addAjusteStockPositivo(String src, long idDeposito, Date fecha) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "POSITIVO", CSV.STRING }, { "NEGATIVO", CSV.STRING } };
		
		Set<AjusteStockDetalle> dets = new HashSet<AjusteStockDetalle>();
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("CODIGO");	
			String stock = csv.getDetalleString("POSITIVO");
			Integer stock_ = Integer.parseInt(stock);
			
			if (stock_ > 0) {
				Articulo art = rr.getArticuloByCodigoInterno(codigo);				
				if (art != null) {					
					AjusteStockDetalle item = new AjusteStockDetalle();
					item.setArticulo(art);
					item.setCantidad(stock_);
					item.setCostoGs(art.getCostoGs());
					dets.add(item);
					System.out.println(art.getCodigoInterno() + " - STOCK: " + stock );
				} else {
					System.err.println("NO ENCONTRADO: " + codigo);
				}
			}
		}
		if (dets.size() > 0) {
			Deposito dep = (Deposito) rr.getObject(Deposito.class.getName(), idDeposito);
			SucursalApp suc = rr.getSucursalAppById(Long.parseLong(dep.getAuxi()));
			TipoMovimiento tm = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
			Tipo estado = rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CONFECCIONADO);
			AjusteStock ajuste = new AjusteStock();
			ajuste.setAutorizadoPor("INVENTARIO OFICIAL");
			ajuste.setDeposito(dep);
			ajuste.setDescripcion("INVENTARIO OFICIAL 02/2019 " + dep.getDescripcion());
			ajuste.setDetalles(dets);
			ajuste.setEstadoComprobante(estado);
			ajuste.setFecha(fecha);
			ajuste.setNumero("AJT-" + AutoNumeroControl.getAutoNumero("AJT", 7));
			ajuste.setSucursal(suc);
			ajuste.setTipoMovimiento(tm);
			rr.saveObject(ajuste, "sys");
			System.out.println("AJUSTE POR INVENTARIO: " + ajuste.getNumero());
		}
	}
	
	/**
	 * genera transferencia interna..
	 */
	public static void addTransferenciaInterna(String src, long idOrigen, long idDestino, Date fecha) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "POSITIVO", CSV.STRING }, { "NEGATIVO", CSV.STRING } };
		
		Set<TransferenciaDetalle> dets = new HashSet<TransferenciaDetalle>();
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("CODIGO");	
			String stock = csv.getDetalleString("NEGATIVO");
			Integer stock_ = Integer.parseInt(stock);
			
			if (stock_ < 0) {
				Articulo art = rr.getArticuloByCodigoInterno(codigo);				
				if (art != null) {					
					TransferenciaDetalle item = new TransferenciaDetalle();
					item.setArticulo(art);
					item.setCantidad(stock_ * -1);
					item.setCantidadEnviada(stock_ * -1);
					item.setCantidadPedida(stock_ * -1);
					item.setCantidadRecibida(stock_ * -1);
					item.setCosto(art.getCostoGs());
					item.setEstado("Pendiente");
					dets.add(item);
					System.out.println(art.getCodigoInterno() + " - STOCK: " + stock );
				} else {
					System.err.println("NO ENCONTRADO: " + codigo);
				}
			}
		}
		if (dets.size() > 0) {
			Deposito origen = (Deposito) rr.getObject(Deposito.class.getName(), idOrigen);
			Deposito destino = (Deposito) rr.getObject(Deposito.class.getName(), idDestino);
			SucursalApp suc = rr.getSucursalAppById(Long.parseLong(origen.getAuxi()));
			SucursalApp suc_ = rr.getSucursalAppById(Long.parseLong(destino.getAuxi()));
			Funcionario func = (Funcionario) rr.getObject(Deposito.class.getName(), 54);
			Tipo tm = rr.getTipoPorSigla(Configuracion.SIGLA_TM_TRANSF_INTERNA);
			Transferencia transf = new Transferencia();
			transf.setDepositoEntrada(destino);
			transf.setDepositoSalida(origen);
			transf.setDetalles(dets);
			transf.setFechaCreacion(fecha);
			transf.setFechaEnvio(fecha);
			transf.setFechaRecepcion(fecha);
			transf.setFuncionarioCreador(func);
			transf.setFuncionarioEnvio(func);
			transf.setFuncionarioReceptor(func);
			transf.setNumero("TRF-INT-" + AutoNumeroControl.getAutoNumero("TRF-INT", 7));
			transf.setNumeroRemision("");
			transf.setObservacion("INVENTARIO OFICIAL 02/2019 " + origen.getDescripcion());
			transf.setSucursal(suc);
			transf.setSucursalDestino(suc_);
			transf.setTransferenciaEstado(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_TRANSF_PENDIENTE));
			transf.setTransferenciaTipo(tm);
			rr.saveObject(transf, "sys");
			System.out.println("TRANSFERENCIA INTERNA POR INVENTARIO: " + transf.getNumero());
		}
	}
	
	/**
	 * pobla la tabla historico_movimientos..
	 */
	public static void poblarHistoricoMovimientos(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } }; 
		String[][] det = { { "CODIGO", CSV.STRING }, { "CANTIDAD", CSV.STRING }, { "FECHA", CSV.STRING },
				{ "PROVEEDOR", CSV.STRING }, { "COSTODS", CSV.STRING }, { "COSTOGS", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");		
			String cantidad = csv.getDetalleString("CANTIDAD");
			String fecha = csv.getDetalleString("FECHA");
			String proveedor = csv.getDetalleString("PROVEEDOR");
			String costods = csv.getDetalleString("COSTODS");
			String costogs = csv.getDetalleString("COSTOGS");
			HistoricoMovimientoArticulo hist = new HistoricoMovimientoArticulo();
			hist.setCodigo(codigo);
			hist.setCantidad(Long.parseLong(cantidad));
			hist.setFechaUltimaCompraLocal(fecha);
			hist.setProveedor(proveedor);
			hist.setCostoFob(Double.parseDouble(costods));
			hist.setCostoGs(Double.parseDouble(costogs));
			rr.saveObject(hist, "sys");
			System.out.println(codigo);
		}
	}
	
	/**
	 * asigna precio a los articulos..
	 */
	public static void setPrecioArticulos(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "MAYORISTA", CSV.STRING }, { "MINORISTA", CSV.STRING }, { "LISTA", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");	
			Double mayorista = Double.parseDouble(csv.getDetalleString("MAYORISTA"));
			Double minorista = Double.parseDouble(csv.getDetalleString("MINORISTA"));
			Double lista = Double.parseDouble(csv.getDetalleString("LISTA"));
			Articulo art = rr.getArticuloByCodigoInterno(codigo);
			if (art != null) {
				art.setPrecioGs(mayorista);
				art.setPrecioMinoristaGs(minorista);
				art.setPrecioListaGs(lista);
				rr.saveObject(art, "sys");
				System.out.println(art.getCodigoInterno());
			}
		}
	}
	
	/**
	 * asigna marca y familia a los articulos..
	 */
	public static void setFamiliaMarca(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "FAMILIA", CSV.STRING }, { "MARCA", CSV.STRING } };
		
		ArticuloFamilia filtros = rr.getArticuloFamilia(ArticuloFamilia.FILTROS);
		ArticuloFamilia lubricantes = rr.getArticuloFamilia(ArticuloFamilia.LUBRICANTES);
		ArticuloFamilia cubiertas = rr.getArticuloFamilia(ArticuloFamilia.CUBIERTAS);
		ArticuloFamilia repuestos = rr.getArticuloFamilia(ArticuloFamilia.REPUESTOS);
		ArticuloFamilia baterias = rr.getArticuloFamilia(ArticuloFamilia.BATERIAS);
		
		Map<Long, ArticuloFamilia> flias = new HashMap<Long, ArticuloFamilia>();
		flias.put(filtros.getId(), filtros);
		flias.put(lubricantes.getId(), lubricantes);
		flias.put(cubiertas.getId(), cubiertas);
		flias.put(repuestos.getId(), repuestos);
		flias.put(baterias.getId(), baterias);
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");	
			String familia = csv.getDetalleString("FAMILIA");
			String marca = csv.getDetalleString("MARCA");
			Articulo art = rr.getArticuloByCodigoInterno(codigo);
			if (art != null) {
				ArticuloMarca marca_ = (ArticuloMarca) rr.getObject(ArticuloMarca.class.getName(), Long.parseLong(marca));
				art.setFamilia(flias.get(Long.parseLong(familia)));
				art.setMarca(marca_);
				rr.saveObject(art, "sys");
				System.out.println(art.getCodigoInterno());
			}
		}
	}
	
	/**
	 * asignacion de precios mra
	 */
	public static void setPreciosMra(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "MAYORISTA", CSV.STRING }, { "MINORISTA", CSV.STRING }, { "LISTA", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {

			String codigo = csv.getDetalleString("CODIGO");	
			Double mayorista = Double.parseDouble(csv.getDetalleString("MAYORISTA"));
			Double minorista = Double.parseDouble(csv.getDetalleString("MINORISTA"));
			Double lista = Double.parseDouble(csv.getDetalleString("LISTA"));
			Articulo art = rr.getArticuloByCodigoInterno(codigo);
			if (art != null) {
				Object[] cl = rr.getUltimaCompraLocal(art.getId());
				Object[] tr = rr.getUltimaTransferenciaPorArticulo(art.getId());
				Date fcl = cl != null ? ((Date) cl[1]) : Utiles.getFecha("01-01-2000 00:00:00");
				Date ftr = tr != null ? ((Date) tr[1]) : Utiles.getFecha("01-01-2000 00:00:00");
				
				if (ftr.compareTo(fcl) > 0) {
					art.setPrecioGs(mayorista);
					art.setPrecioMinoristaGs(minorista);
					art.setPrecioListaGs(lista);
					rr.saveObject(art, "sys");
					System.out.println("transferencia:" + art.getCodigoInterno());
				} else {
					System.err.println("compra_local: " + art.getCodigoInterno());
				}
			} else {
				System.err.println("codigo_dif: " + codigo);
			}
		}
	}
	
	/**
	 * verifica si existen articulos que necesiten recalculo de stock.
	 * [0]: articulo.codigoInterno
	 * [1]: deposito.descripcion
	 * [2]: deposito.stock
	 * [3]: historial.saldo
	 */
	public static List<Object[]> verificarStock(long idSucursal, long idFamilia) throws Exception {
		Date desde = Utiles.getFecha("05-10-2018 00:00:00");
		List<Object[]> out = new ArrayList<Object[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		
		for (Object[] art : rr.getArticulos(0, idFamilia)) {
			long idArticulo = (long) art[0];
			Object[] ent = ControlArticuloStock.getHistoricoEntrada(idArticulo, (String) art[1], desde, new Date(), idSucursal);
			Object[] sal = ControlArticuloStock.getHistoricoSalida(idArticulo, (String) art[1], desde, new Date(), idSucursal);
			long saldo = ((long) ent[1] - (long) sal[1]);
			
			long stock = rr.getStockArticulo((String) art[1]);
			
			if (saldo != stock) {				
				if (saldo < 0) {
					if (stock > 0) {
						System.out.println("---> ITEM: " + art[1] + " SALDO: " + ((long) ent[1] - (long) sal[1]) + " STOCK: " + stock);
						out.add(new Object[] { art[1], stock, saldo });
					}
				} else {
					System.out.println("---> ITEM: " + art[1] + " SALDO: " + ((long) ent[1] - (long) sal[1]) + " STOCK: " + stock);
					out.add(new Object[] { art[1], stock, saldo });
				}				
			}			
		}		
		return out;
	}
	
	/**
	 * verifica si existen articulos que necesiten recalculo de stock.
	 * [0]: articulo.codigoInterno
	 * [1]: deposito.descripcion
	 * [2]: deposito.stock
	 * [3]: historial.saldo
	 */
	public static List<Object[]> verificarStockGroupauto(long idSucursal, long idFamilia) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Deposito> deps = rr.getDepositos_();
		
		for (Object[] art : rr.getArticulos(0, idFamilia)) {
			long idArticulo = (long) art[0];
			
			for (Deposito dep : deps) {		
				long stock = 0;
				long saldo = 0;
				
				List<Object[]> historial = ControlArticuloStock.getHistorialMovimientos(idArticulo, dep.getId(), idSucursal, true, new Date(), false);
				String hist = historial.size() > 0 ? (String) historial.get(historial.size() - 1)[7] : "0";
				
				stock += rr.getStockDisponible(idArticulo, dep.getId());
				saldo += Long.parseLong(hist);		
				
				if (stock != saldo) {				
					if (stock <= 0) {
						if (saldo > 0) {
							System.out.println("---> ITEM: " + art[1] + " SALDO: " + saldo + " STOCK: " + stock);
							out.add(new Object[] { art[1], stock, saldo });
						}
					} else {
						System.out.println("---> ITEM: " + art[1] + " SALDO: " + saldo + " STOCK: " + stock);
						out.add(new Object[] { art[1], stock, saldo });
					}				
				}
			}			
		}		
		return out;
	}
	
	/**
	 * proceso para unificar articulos mra.
	 */
	public static void migrarStockMra(String src) throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "DESCRIPCION", CSV.STRING }, { "STOCK", CSV.STRING },
				{ "COSTO", CSV.STRING }, { "MAYORISTA", CSV.STRING }, { "MINORISTA", CSV.STRING }, { "LISTA", CSV.STRING },
				{ "MARCA", CSV.STRING }, { "FAMILIA", CSV.STRING }};
		Deposito dep = (Deposito) rr.getObject(Deposito.class.getName(), 14);	
		Proveedor prov = rr.getProveedorById(208);
		AjusteStock ajuste = new AjusteStock();
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("CODIGO");
			String descripcion = csv.getDetalleString("DESCRIPCION");
			String stock = csv.getDetalleString("STOCK");
			String costo = csv.getDetalleString("COSTO");
			String mayorista = csv.getDetalleString("MAYORISTA");
			String minorista = csv.getDetalleString("MINORISTA");
			String lista = csv.getDetalleString("LISTA");
			String marca = csv.getDetalleString("MARCA");
			String familia = csv.getDetalleString("FAMILIA");
			Articulo art = rr.getArticuloByCodigoInterno(codigo);
			if (art != null) {
				ArticuloDeposito ad = new ArticuloDeposito();
				ad.setDeposito(dep);
				ad.setArticulo(art);
				ad.setStock(Long.parseLong(stock));
				rr.saveObject(ad, "sys");
				AjusteStockDetalle adet = new AjusteStockDetalle();
				adet.setArticulo(art);
				adet.setCantidad(Integer.parseInt(stock));
				adet.setCostoGs(art.getCostoGs());
				ajuste.getDetalles().add(adet);
				System.out.println("FOUND --- " + codigo);
			} else {				
				Articulo ar = new Articulo();
				ar.setAuxi("mra");
				ar.setObservacion(marca);
				ar.setIp_pc(familia);
				ar.setCodigoInterno(codigo);
				ar.setDescripcion(descripcion);
				ar.setProveedor(prov);
				ar.setCostoGs(Double.parseDouble(costo));
				ar.setPrecioGs(Double.parseDouble(mayorista));
				ar.setPrecioListaGs(Double.parseDouble(lista));
				ar.setPrecioMinoristaGs(Double.parseDouble(minorista));
				rr.saveObject(ar, "sys");
				ArticuloDeposito ad = new ArticuloDeposito();
				ad.setDeposito(dep);
				ad.setArticulo(ar);
				ad.setStock(Long.parseLong(stock));
				rr.saveObject(ad, "sys");
				AjusteStockDetalle adet = new AjusteStockDetalle();
				adet.setArticulo(ar);
				adet.setCantidad(Integer.parseInt(stock));
				adet.setCostoGs(ar.getCostoGs());
				ajuste.getDetalles().add(adet);
				System.err.println("ADD --- " + codigo);
			}
		}	
		ajuste.setAutorizadoPor("");
		ajuste.setDeposito(dep);
		ajuste.setDescripcion("migración m.r.a.");
		ajuste.setEstadoComprobante(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CERRADO));
		ajuste.setFecha(new Date());
		ajuste.setNumero("AJT-000100");
		ajuste.setSucursal(rr.getSucursalAppById(2));
		ajuste.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO));
		rr.saveObject(ajuste, "sys");
	}
	
	/**
	 * ajuste por inventario mra
	 */
	public static void addAjusteMRA(String src) throws Exception {
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "STOCK", CSV.STRING }, { "ORIGEN", CSV.STRING } };
		
		Map<String, Integer> inventario = new HashMap<String, Integer>();
		int totalDif = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		
		Set<AjusteStockDetalle> detsNegativos = new HashSet<AjusteStockDetalle>();
		Set<AjusteStockDetalle> detsPositivos = new HashSet<AjusteStockDetalle>();
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("CODIGO");	
			String stock = csv.getDetalleString("STOCK");	
			String origen = csv.getDetalleString("ORIGEN");
			
			if (origen.equals("INVENTARIO")) {
				Integer cant = Integer.parseInt(stock);
				inventario.put(codigo, cant);
			}
			
			if (origen.equals("SISTEMA")) {
				Articulo art = rr.getArticulo(codigo);
				if (art != null) {
					Integer cant = Integer.parseInt(stock);
					Integer inv = inventario.get(codigo);
					if (inv != null) {
						Integer dif = inv - cant;
						System.out.println(codigo + " " + dif);
						totalDif += dif;
						
						if (dif < 0) {
							AjusteStockDetalle d = new AjusteStockDetalle();
							d.setArticulo(art);
							d.setCantidad(dif);
							d.setCantidadSistema(0);
							d.setCostoGs(art.getCostoGs());
							d.setCostoPromedioGs(art.getCostoGs());
							detsNegativos.add(d);
						}
						
						if (dif > 0) {
							AjusteStockDetalle d = new AjusteStockDetalle();
							d.setArticulo(art);
							d.setCantidad(dif);
							d.setCantidadSistema(0);
							d.setCostoGs(art.getCostoGs());
							d.setCostoPromedioGs(art.getCostoGs());
							detsPositivos.add(d);
						}
					}
				}				
			}
		}
		
		if (detsPositivos.size() > 0) {
			AjusteStock aj = new AjusteStock();
			aj.setAutorizadoPor("INVENTARIO 2022 REPUESTOS");
			aj.setDescripcion("INVENTARIO 2022 REPUESTOS");
			aj.setDetalles(detsPositivos);
			aj.setFecha(new Date());
			aj.setNumero("08");
			aj.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO));
			rr.saveObject(aj, "sys");
		}
		
		if (detsNegativos.size() > 0) {
			AjusteStock aj = new AjusteStock();
			aj.setAutorizadoPor("INVENTARIO 2022 CUBIERTAS");
			aj.setDescripcion("INVENTARIO 2022 CUBIERTAS");
			aj.setDetalles(detsNegativos);
			aj.setFecha(new Date());
			aj.setNumero("09");
			aj.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO));
			rr.saveObject(aj, "sys");
		}
		
		System.out.println(totalDif);
	}
	
	/**
	 * inventario autocentro
	 */
	public static void addInventarioAutocentro(String src) throws Exception {
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CODIGO", CSV.STRING }, { "CANTIDAD", CSV.STRING }, { "CONCEPTO", CSV.STRING }, { "COSTO", CSV.STRING } };
		
		RegisterDomain rr = RegisterDomain.getInstance();
		
		Set<CompraLocalOrdenDetalle> detOrden = new HashSet<CompraLocalOrdenDetalle>();
		Set<CompraLocalFacturaDetalle> detCompras = new HashSet<CompraLocalFacturaDetalle>();
		Set<NotaCreditoDetalle> detNotaCred = new HashSet<NotaCreditoDetalle>();
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) {
			String codigo = csv.getDetalleString("CODIGO");	
			String cantidad = csv.getDetalleString("CANTIDAD");	
			String concepto = csv.getDetalleString("CONCEPTO");
			String costo = csv.getDetalleString("COSTO");
			
			if (concepto.equals("FACTURACION")) {
				Articulo art = rr.getArticulo(codigo);
				if (art != null) {
					System.out.println(art.getCodigoInterno());
					CompraLocalOrdenDetalle d = new CompraLocalOrdenDetalle();
					d.setArticulo(art);
					d.setCantidad(Integer.parseInt(cantidad));
					d.setCantidadRecibida(Integer.parseInt(cantidad));
					d.setCostoDs(0);
					d.setCostoGs(Double.parseDouble(costo));
					d.setIva(rr.getTipoPorSigla(Configuracion.SIGLA_IVA_10));
					detOrden.add(d);
					
					CompraLocalFacturaDetalle df = new CompraLocalFacturaDetalle();
					df.setArticulo(art);
					df.setCantidad(Integer.parseInt(cantidad));
					df.setCantidadRecibida(Integer.parseInt(cantidad));
					df.setCostoDs(0);
					df.setCostoFinalGs(Double.parseDouble(costo));
					df.setCostoGs(Double.parseDouble(costo));
					df.setCostoPromedioGs(Double.parseDouble(costo));
					df.setIva(rr.getTipoPorSigla(Configuracion.SIGLA_IVA_10));
					df.setListaGs(art.getPrecioListaGs());
					df.setMinoristaGs(art.getPrecioMinoristaGs());
					df.setPrecioFinalGs(art.getPrecioGs());
					detCompras.add(df);
				}				
			} else {
				Articulo art = rr.getArticulo(codigo);
				if (art != null) {
					int cant = Integer.parseInt(cantidad);
					if (cant > 0) {
						NotaCreditoDetalle d = new NotaCreditoDetalle();
						d.setArticulo(art);
						d.setCantidad(Integer.parseInt(cantidad));
						d.setMontoGs(Double.parseDouble(costo));
						detNotaCred.add(d);	
					}									
				}				
			}
		}
		CompraLocalOrden oc = new CompraLocalOrden();
		oc.setFechaCreacion(new Date());
		oc.setNumero("OCL-00001");
		oc.setDetalles(detOrden);
		rr.saveObject(oc, "sys");		
	}
	
	public static void main(String[] args) {
		try {
			//ProcesosArticulos.setFamiliaArticulos(SRC_BATERIAS);
			//ProcesosArticulos.recalcularStock(2, 2);
			//ProcesosArticulos.verificarUltimoCosto();
			//ProcesosArticulos.setProveedorArticulos(SRC_JHONSON, 40);
			//ProcesosArticulos.setCostoArticulos();
			//ProcesosArticulos.setHistorialCostoArticulo();
			//ProcesosArticulos.poblarHistoricoStock();
			//ProcesosArticulos.setMarcaFamiliaArticulos(SRC_FAMILIAS_MARCAS);
			//ProcesosArticulos.generarBarcodes();
			//ProcesosArticulos.addAjusteStockPositivo(SRC_INVENTARIO_MINORISTA, Deposito.ID_MINORISTA, Utiles.getFecha("22-01-2019 00:00:00"));
			//ProcesosArticulos.addAjusteStockPositivo(SRC_INVENTARIO_MAYORISTA, Deposito.ID_MAYORISTA, Utiles.getFecha("22-01-2019 00:00:00"));
			//ProcesosArticulos.addAjusteStockPositivo(SRC_INVENTARIO_MCAL, Deposito.ID_MCAL_LOPEZ, Utiles.getFecha("22-01-2019 00:00:00"));
			//ProcesosArticulos.addTransferenciaInterna(SRC_INVENTARIO_MINORISTA, Deposito.ID_MINORISTA, Deposito.ID_VIRTUAL_INVENTARIO, Utiles.getFecha("22-01-2019 00:00:00"));
			//ProcesosArticulos.addTransferenciaInterna(SRC_INVENTARIO_MAYORISTA, Deposito.ID_MAYORISTA, Deposito.ID_VIRTUAL_INVENTARIO, Utiles.getFecha("22-01-2019 00:00:00"));
			//ProcesosArticulos.addTransferenciaInterna(SRC_INVENTARIO_MCAL, Deposito.ID_MCAL_LOPEZ, Deposito.ID_VIRTUAL_INVENTARIO, Utiles.getFecha("22-01-2019 00:00:00"));
			//ProcesosArticulos.poblarHistoricoMovimientos(SRC_HISTORICO_MOVIMIENTOS);
			//ProcesosArticulos.setPrecioArticulos(SRC_PRECIOS);
			//ProcesosArticulos.setFamiliaMarca(SRC_MARCAS_FAMILIAS);
			//ProcesosArticulos.verificarStock(2, 1);
			//ProcesosArticulos.migrarStockMra(SRC_STOCK_MRA);
			//ProcesosArticulos.addAjusteMRA(SRC_INV_REPUESTOS_MRA);
			//ProcesosArticulos.addInventarioAutocentro(SRC_INV_CUBIERTAS_AUT);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
