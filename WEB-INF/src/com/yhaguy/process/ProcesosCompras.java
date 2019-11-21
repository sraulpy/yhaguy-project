package com.yhaguy.process;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Tipo;
import com.coreweb.extras.csv.CSV;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.CompraLocalOrdenDetalle;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;

public class ProcesosCompras {

	static final String SRC_COMPRA = "./WEB-INF/docs/procesos/COMPRA.csv";
	
	/**
	 * setea el nro de factura a las ordenes de compra de gastos..
	 */
	@SuppressWarnings("unchecked")
	public static void setNumeroFacturaOrdenGastos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<OrdenPedidoGasto> ords = rr.getObjects(OrdenPedidoGasto.class.getName());
		for (OrdenPedidoGasto orden : ords) {
			if (orden.getGastos().size() > 0) {
				for (Gasto fac : orden.getGastos()) {
					orden.setNumeroFactura(fac.getNumeroFactura());
					rr.saveObject(orden, "process");
					System.out.println("orden: " + orden.getNumero() + " - fac: " + fac.getNumeroFactura());
				}
			}
		}
	}
	
	/**
	 * genera compras..
	 */
	public static void generarCompras(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "FACTURA", CSV.STRING }, { "CODIGO", CSV.STRING }, { "CANTIDAD", CSV.STRING }, { "COSTO", CSV.STRING }, { "PRECIO", CSV.STRING } };
		
		Tipo gs = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		
		SucursalApp central = rr.getSucursalAppById(2);
		Tipo iva10 = rr.getTipoById(124);
		Proveedor proveedor = rr.getProveedorById(208);
		HashMap<String, Set<CompraLocalOrdenDetalle>> detalles = new HashMap<String, Set<CompraLocalOrdenDetalle>>();
		
		CSV csv = new CSV(cab, det, src);
		csv.start();
		while (csv.hashNext()) { 
			String factura = csv.getDetalleString("FACTURA");
			String cantidad = csv.getDetalleString("CANTIDAD");
			String costo = csv.getDetalleString("COSTO");
			String codigo = csv.getDetalleString("CODIGO");
			
			Set<CompraLocalOrdenDetalle> dets = detalles.get(factura);
			if (dets == null) {
				dets = new HashSet<CompraLocalOrdenDetalle>();
			}
		
			Articulo articulo = rr.getArticulo(codigo);	
			
			CompraLocalOrdenDetalle item = new CompraLocalOrdenDetalle();
			item.setArticulo(articulo);
			item.setCantidad(Integer.parseInt(cantidad));
			item.setCostoGs(Double.parseDouble(costo));
			item.setIva(iva10);
			dets.add(item);
			detalles.put(factura, dets);
			System.out.println(item.getArticulo().getCodigoInterno());
		}
		
		for (String key : detalles.keySet()) {
			Set<CompraLocalOrdenDetalle> dets = detalles.get(key);
			CompraLocalOrden oc = new CompraLocalOrden();
			oc.setProveedor(proveedor);
			oc.setCondicionPago(rr.getCondicionPagoById(2));
			oc.setAutorizado(false);
			oc.setDetalles(dets);
			oc.setFechaCreacion(new Date());
			oc.setMoneda(gs);
			oc.setNumero("OCL-" + AutoNumeroControl.getAutoNumero(Configuracion.NRO_COMPRA_LOCAL_ORDEN));
			oc.setObservacion(key);
			oc.setSucursal(central);
			oc.setTipoCambio(1);
			oc.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ORDEN_COMPRA));
			
			rr.saveObject(oc, "sys");
			System.out.println("OC GENERADO: " + oc.getNumero());
		}
	}
	
	public static void main(String[] args) {
		try {
			//ProcesosCompras.setNumeroFacturaOrdenGastos();
			ProcesosCompras.generarCompras(SRC_COMPRA);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
