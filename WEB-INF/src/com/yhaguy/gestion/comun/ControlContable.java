package com.yhaguy.gestion.comun;

import java.util.Date;
import java.util.List;

import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.domain.ContableAsiento;
import com.yhaguy.domain.ContableAsientoDetalle;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

public class ControlContable {

	public static final String KEY_ASIENTOS = "ASIENTOS";
	
	public static final String COD_CTA_CLIENTES = "1.01.03.01.001";
	public static final String COD_CTA_VENTA_MERCADERIAS = "4.01.01.00.000";
	public static final String COD_CTA_IVA_A_PAGAR = "2.01.03.01.002";
	public static final String COD_CTA_RECAUDACIONES_A_DEPOSITAR = "1.01.01.01.000";
	public static final String COD_CTA_COSTO_MERCADERIAS_GRAVADAS = "5.01.01.00.000";
	public static final String COD_CTA_MERCADERIAS_GRAVADAS = "1.01.04.01.001";
	
	/**
	 * Genera los asientos contables..
	 */
	public static void generarAsientos(Date desde, Date hasta, String user) throws Exception {
		
		double total_venta_ivainc = 0;
		double total_venta_siniva = 0;
		double total_iva = 0;
		
		double total_venta_cont = 0;
		double total_costo_vta = 0;
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> ventas = rr.getVentas(desde, hasta, 0);			
		for (Venta venta : ventas) {
			if (!venta.isAnulado()) {
				total_venta_ivainc += venta.getTotalImporteGs();
				total_venta_siniva += venta.getTotalImporteGsSinIva();
				total_iva += venta.getTotalIva10();
				total_costo_vta += venta.getTotalCostoGsSinIva();
				if (venta.isVentaContado()) {
					total_venta_cont += venta.getTotalImporteGs();
				}
			}
		}
		
		double total_nc_ivainc = 0;
		double total_nc_siniva = 0;
		double total_nc_iva = 0;
		
		double total_nc_cont = 0;
		double total_nc_costo = 0;
		
		List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);			
		for (NotaCredito nc : ncs) {
			if (!nc.isAnulado()) {
				total_nc_ivainc += nc.getImporteGs();
				total_nc_siniva += nc.getTotalImporteGsSinIva();
				total_nc_iva += nc.getTotalIva10();
				if (nc.isNotaCreditoVentaContado()) {
					total_nc_cont += nc.getImporteGs();
				}
				if (nc.isMotivoDevolucion()) {
					total_nc_costo += nc.getTotalCostoGsSinIva();
				}
			}
		}
		
		ControlContable.generarAsientosDeVentas(desde, hasta, total_venta_ivainc, total_venta_siniva, total_iva, user);
		ControlContable.generarAsientosDeNotasCredito(desde, hasta, total_nc_ivainc, total_nc_siniva, total_nc_iva, user);
		ControlContable.generarAsientosDeRecaudacionesADepositar(desde, hasta, (total_venta_cont - total_nc_cont), user);
		ControlContable.generarAsientosDeCostoDeVentas(desde, hasta, (total_costo_vta - total_nc_costo), user);
	}
	
	/**
	 * Genera los asientos de ventas..
	 */
	public static void generarAsientosDeVentas(Date desde, Date hasta, double total_venta_ivainc,
			double total_venta_siniva, double total_iva, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		ContableAsiento asiento = new ContableAsiento();
		asiento.setDescripcion("ASIENTO DE VENTAS " + Utiles.getDateToString(hasta, "MM-yyyy"));
		asiento.setFecha(hasta);
		asiento.setNumero(AutoNumeroControl.getAutoNumero(KEY_ASIENTOS, 5));
		
		ContableAsientoDetalle det1 = new ContableAsientoDetalle();
		det1.setCuenta(rr.getPlanCuenta(COD_CTA_CLIENTES));
		det1.setDescripcion("VENTAS PERIODO: " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det1.setDebe(total_venta_ivainc);
		det1.setHaber(0);
		
		ContableAsientoDetalle det2 = new ContableAsientoDetalle();
		det2.setCuenta(rr.getPlanCuenta(COD_CTA_VENTA_MERCADERIAS));
		det2.setDescripcion("VENTAS PERIODO: " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det2.setDebe(0);
		det2.setHaber(total_venta_siniva);
		
		ContableAsientoDetalle det3 = new ContableAsientoDetalle();
		det3.setCuenta(rr.getPlanCuenta(COD_CTA_IVA_A_PAGAR));
		det3.setDescripcion("VENTAS PERIODO: " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det3.setDebe(0);
		det3.setHaber(total_iva);
		
		asiento.getDetalles().add(det1);
		asiento.getDetalles().add(det2);
		asiento.getDetalles().add(det3);
		
		rr.saveObject(asiento, user);
	}
	
	/**
	 * Genera los asientos de notas de credito de venta..
	 */
	public static void generarAsientosDeNotasCredito(Date desde, Date hasta,
			double total_nc_ivainc, double total_nc_siniva, double total_iva, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		ContableAsiento asiento = new ContableAsiento();
		asiento.setDescripcion("ASIENTO DE NOTAS DE CREDITO " + Utiles.getDateToString(hasta, "MM-yyyy"));
		asiento.setFecha(hasta);
		asiento.setNumero(AutoNumeroControl.getAutoNumero(KEY_ASIENTOS, 5));
		
		ContableAsientoDetalle det1 = new ContableAsientoDetalle();
		det1.setCuenta(rr.getPlanCuenta(COD_CTA_VENTA_MERCADERIAS));
		det1.setDescripcion("NOTAS CREDITO VTA. PERIODO: " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det1.setDebe(total_nc_siniva);
		det1.setHaber(0);
		
		ContableAsientoDetalle det2 = new ContableAsientoDetalle();
		det2.setCuenta(rr.getPlanCuenta(COD_CTA_IVA_A_PAGAR));
		det2.setDescripcion("NOTAS CREDITO VTA. PERIODO: " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det2.setDebe(total_iva);
		det2.setHaber(0);
		
		ContableAsientoDetalle det3 = new ContableAsientoDetalle();
		det3.setCuenta(rr.getPlanCuenta(COD_CTA_CLIENTES));
		det3.setDescripcion("NOTAS CREDITO VTA. PERIODO: " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det3.setDebe(0);
		det3.setHaber(total_nc_ivainc);
		
		asiento.getDetalles().add(det1);
		asiento.getDetalles().add(det2);
		asiento.getDetalles().add(det3);
		
		rr.saveObject(asiento, user);
	}
	
	/**
	 * Genera los asientos de notas de recaudaciones a depositar..
	 */
	public static void generarAsientosDeRecaudacionesADepositar(Date desde, Date hasta, double total_venta_cont, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		ContableAsiento asiento = new ContableAsiento();
		asiento.setDescripcion("ASIENTO DE RECAUDACIONES A DEPOSITAR " + Utiles.getDateToString(hasta, "MM-yyyy"));
		asiento.setFecha(hasta);
		asiento.setNumero(AutoNumeroControl.getAutoNumero(KEY_ASIENTOS, 5));
		
		ContableAsientoDetalle det1 = new ContableAsientoDetalle();
		det1.setCuenta(rr.getPlanCuenta(COD_CTA_RECAUDACIONES_A_DEPOSITAR));
		det1.setDescripcion("POR VENTAS CONTADO " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det1.setDebe(total_venta_cont);
		det1.setHaber(0);
		
		ContableAsientoDetalle det2 = new ContableAsientoDetalle();
		det2.setCuenta(rr.getPlanCuenta(COD_CTA_CLIENTES));
		det2.setDescripcion("POR VENTAS CONTADO " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det2.setDebe(0);
		det2.setHaber(total_venta_cont);
		
		asiento.getDetalles().add(det1);
		asiento.getDetalles().add(det2);
		
		rr.saveObject(asiento, user);
	}
	
	/**
	 * Genera los asientos de costo de ventas..
	 */
	public static void generarAsientosDeCostoDeVentas(Date desde, Date hasta, double total_costo_venta, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		ContableAsiento asiento = new ContableAsiento();
		asiento.setDescripcion("ASIENTO DE COSTO DE VENTAS " + Utiles.getDateToString(hasta, "MM-yyyy"));
		asiento.setFecha(hasta);
		asiento.setNumero(AutoNumeroControl.getAutoNumero(KEY_ASIENTOS, 5));
		
		ContableAsientoDetalle det1 = new ContableAsientoDetalle();
		det1.setCuenta(rr.getPlanCuenta(COD_CTA_COSTO_MERCADERIAS_GRAVADAS));
		det1.setDescripcion("COSTO DE VENTAS " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det1.setDebe(total_costo_venta);
		det1.setHaber(0);
		
		ContableAsientoDetalle det2 = new ContableAsientoDetalle();
		det2.setCuenta(rr.getPlanCuenta(COD_CTA_MERCADERIAS_GRAVADAS));
		det2.setDescripcion("COSTO DE VENTAS " + Utiles.getDateToString(hasta, "MM-yyyy"));
		det2.setDebe(0);
		det2.setHaber(total_costo_venta);
		
		asiento.getDetalles().add(det1);
		asiento.getDetalles().add(det2);
		
		rr.saveObject(asiento, user);
	}
	
}
