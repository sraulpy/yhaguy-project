package com.yhaguy.process;

import java.util.List;

import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.domain.RegisterDomain;

public class ProcesosCompras {

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
	
	public static void main(String[] args) {
		try {
			ProcesosCompras.setNumeroFacturaOrdenGastos();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
