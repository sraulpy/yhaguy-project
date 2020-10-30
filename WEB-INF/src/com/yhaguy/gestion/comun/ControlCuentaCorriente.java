package com.yhaguy.gestion.comun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteCtaCte;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoDescuentoCheque;
import com.yhaguy.domain.BancoPrestamo;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.RecaudacionCentral;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Venta;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.gestion.notacredito.NotaCreditoDTO;
import com.yhaguy.gestion.notadebito.NotaDebitoDTO;
import com.yhaguy.util.Utiles;

public class ControlCuentaCorriente {

	/**
	 * agregar movimiento nota credito compra gasto e importacion..
	 */
	public static void addNotaCreditoCompra(NotaCreditoDTO dto, String user) throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		MyArray factura = dto.getFacturas().get(0);
		TipoMovimiento tm = rr.getTipoMovimientoById(((MyPair) factura.getPos4()).getId());
		
		CtaCteEmpresaMovimiento ctmCompra = rr.getCtaCteMovimientoByIdMovimiento(factura.getId(), tm.getSigla());
		
		// Gasto Contado de Caja Chica..no generan movimientos en cta cte.
		if (ctmCompra == null && tm.getSigla().equals(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO)) {
			return;
		}
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_CREDITO_COMPRA));
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		ctm.setFechaEmision(dto.getFechaEmision());
		ctm.setFechaVencimiento(dto.getFechaEmision());
		ctm.setIdEmpresa((long) dto.getProveedor().getPos4());
		ctm.setIdMovimientoOriginal(dto.getId());
		ctm.setIdVendedor(dto.getVendedor().getId());
		ctm.setImporteOriginal(dto.isMonedaLocal() ? dto.getImporteGs() : dto.getImporteDs());
		ctm.setMoneda(rr.getTipoPorSigla(dto.getMoneda().getSigla()));
		ctm.setTipoCambio(dto.getTipoCambio());
		ctm.setNroComprobante(dto.getNumero());
		ctm.setSucursal(rr.getSucursalAppById(dto.getSucursal().getId()));
		
		if (ctmCompra != null) {
			double saldo = ctmCompra.getSaldo() - (dto.isMonedaLocal() ? dto.getImporteGs() : dto.getImporteDs());			
			if (saldo <= 0) {
				ctmCompra.setSaldo(0);
				ctm.setSaldo(saldo);
			} else {
				ctmCompra.setSaldo(saldo);
				ctm.setSaldo(0);
			}
			rr.saveObject(ctmCompra, user);
		} else {
			ctm.setSaldo((dto.isMonedaLocal() ? dto.getImporteGs() : dto.getImporteDs()) * -1);
		}
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento nota credito venta..
	 */
	public static void addNotaCreditoVenta(NotaCreditoDTO dto, String user) 
			throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		MyArray factura = dto.getFacturas().get(0);
		TipoMovimiento tm = rr.getTipoMovimientoById(((MyPair) factura.getPos4()).getId());
		
		CtaCteEmpresaMovimiento ctmVenta = rr.getCtaCteMovimientoByIdMovimiento(factura.getId(), tm.getSigla());
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA));
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
		ctm.setFechaEmision(dto.getFechaEmision());
		ctm.setFechaVencimiento(dto.getFechaEmision());
		ctm.setIdEmpresa((long) dto.getCliente().getPos4());
		ctm.setIdMovimientoOriginal(dto.getId());
		ctm.setIdVendedor(dto.getVendedor().getId());
		ctm.setImporteOriginal(dto.isMonedaLocal() ? dto.getImporteGs() : dto.getImporteDs());
		ctm.setMoneda(rr.getTipoPorSigla(dto.getMoneda().getSigla()));
		ctm.setNroComprobante(dto.getNumero());
		ctm.setSucursal(rr.getSucursalAppById(dto.getSucursal().getId()));
		ctm.setSaldo(0);
		
		recalcularSaldoCtaCte(ctmVenta, 0, 0);
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento nota credito venta..
	 */
	public static void addNotaCreditoVenta(NotaCredito nc, String user) 
			throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta vta = nc.getVentaAplicada();
		TipoMovimiento tm = vta.getTipoMovimiento();
		
		CtaCteEmpresaMovimiento ctmVenta = rr.getCtaCteMovimientoByIdMovimiento(vta.getId(), tm.getSigla());
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA));
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
		ctm.setFechaEmision(nc.getFechaEmision());
		ctm.setFechaVencimiento(nc.getFechaEmision());
		ctm.setIdEmpresa(nc.getCliente().getIdEmpresa());
		ctm.setIdMovimientoOriginal(nc.getId());
		ctm.setIdVendedor(nc.getVendedor().getId());
		ctm.setImporteOriginal(nc.isMonedaLocal() ? nc.getImporteGs() : nc.getImporteDs());
		ctm.setMoneda(nc.getMoneda());
		ctm.setNroComprobante(nc.getNumero());
		ctm.setSucursal(nc.getSucursal());
		
		double saldo = ctmVenta.getSaldo() - (nc.isMonedaLocal() ? nc.getImporteGs() : nc.getImporteDs());
		
		if (saldo <= 0) {
			ctmVenta.setSaldo(0);
			ctm.setSaldo(saldo);
		} else {
			ctmVenta.setSaldo(saldo);
			ctm.setSaldo(0);
		}
		
		rr.saveObject(ctmVenta, user);
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento recibo de pago..
	 */
	public static void addReciboDePago(long idOrdenPago, String user, boolean aCobrar) throws Exception {	
		if (aCobrar) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo pago = rr.getOrdenPagoById(idOrdenPago);
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(pago.getTipoMovimiento());
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		ctm.setFechaEmision(pago.getFechaEmision());
		ctm.setIdEmpresa(pago.getProveedor().getIdEmpresa());
		ctm.setIdMovimientoOriginal(pago.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(pago.isMonedaLocal() ? pago.getTotalImporteGs() : pago.getTotalImporteDs());
		ctm.setMoneda(pago.getMoneda());
		ctm.setNroComprobante(pago.getNumero());
		ctm.setSucursal(pago.getSucursal());
		ctm.setSaldo(0);
		
		for (ReciboDetalle det : pago.getDetalles()) {
			if (det.getMovimiento() != null && !det.getMovimiento().isGastoContado()) {
				CtaCteEmpresaMovimiento ctmCompra = det.getMovimiento();
				ctmCompra.setSaldo(ctmCompra.getSaldo() - (pago.isMonedaLocal() ? det.getMontoGs() : det.getMontoDs()));
				rr.saveObject(ctmCompra, user);
			}
		}	
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento recibo de pago (solo cancela los gastos contado)..
	 */
	public static void addReciboDePagoGastosContado(long idOrdenPago, String user, boolean saldoAcobrar) throws Exception {		
		if (saldoAcobrar) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo pago = rr.getOrdenPagoById(idOrdenPago);
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(pago.getTipoMovimiento());
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		ctm.setFechaEmision(pago.getFechaEmision());
		ctm.setIdEmpresa(pago.getProveedor().getIdEmpresa());
		ctm.setIdMovimientoOriginal(pago.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(pago.getTotalImporteGs());
		ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		ctm.setNroComprobante(pago.getNumero());
		ctm.setSucursal(pago.getSucursal());
		ctm.setSaldo(0);
		
		for (ReciboDetalle det : pago.getDetalles()) {
			if (det.getMovimiento() != null && !det.getMovimiento().isGastoCredito()) {
				CtaCteEmpresaMovimiento ctmCompra = det.getMovimiento();
				ctmCompra.setSaldo(ctmCompra.getSaldo() - det.getMontoGs());
				rr.saveObject(ctmCompra, user);
			}
		}	
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento recibo de cobro..
	 */
	public static void addReciboDeCobro(long idRecibo, String user) throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo cobro = (Recibo) rr.getObject(Recibo.class.getName(), idRecibo);
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(cobro.getTipoMovimiento());
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
		ctm.setFechaEmision(cobro.getFechaEmision());
		ctm.setIdEmpresa(cobro.getCliente().getIdEmpresa());
		ctm.setIdMovimientoOriginal(cobro.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(cobro.isMonedaLocal() ? cobro.getTotalImporteGs() : cobro.getTotalImporteDs());
		ctm.setMoneda(cobro.getMoneda());
		ctm.setNroComprobante(cobro.getNumero());
		ctm.setSucursal(cobro.getSucursal());
		ctm.setSaldo(0);
		if (cobro.isCobroAnticipo()) {
			ctm.setSaldo(ctm.getImporteOriginal() * -1);
		}
		
		for (ReciboDetalle det : cobro.getDetalles()) {
			if (det.getMovimiento() != null) {
				double monto = cobro.isMonedaLocal() ? det.getMontoGs() : det.getMontoDs();
				CtaCteEmpresaMovimiento ctmVenta = det.getMovimiento();
				ctmVenta.setSaldo(ctmVenta.getSaldo() - monto);
				rr.saveObject(ctmVenta, user);
			}
		}	
		rr.saveObject(ctm, user);
		ControlCuentaCorriente.addRecaudacionCentral(idRecibo, user);
		
		if (cobro.isCancelacionCheque()) {
			ControlBancoMovimiento.reembolsoChequeRechazado(cobro, user);
		}
	}
	
	/**
	 * agregar movimiento recibo con saldo deudor..
	 */
	public static void addReciboDeudor(long idRecibo, String user) throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo cobro = (Recibo) rr.getObject(Recibo.class.getName(), idRecibo);
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(cobro.getTipoMovimiento());
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		ctm.setFechaEmision(cobro.getFechaEmision());
		ctm.setFechaVencimiento(cobro.getFechaEmision());
		ctm.setIdEmpresa(cobro.getCliente().getIdEmpresa());
		ctm.setIdMovimientoOriginal(cobro.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(cobro.isMonedaLocal() ? cobro.getTotalImporteGs() : cobro.getTotalImporteDs());
		ctm.setMoneda(cobro.getMoneda());
		ctm.setNroComprobante(cobro.getNumero());
		ctm.setSucursal(cobro.getSucursal());
		ctm.setSaldo(cobro.isMonedaLocal() ? cobro.getTotalImporteGs() : cobro.getTotalImporteDs());	
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento recibo con saldo a cobrar..
	 */
	public static void addPagoAcobrar(long idRecibo, String user) throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo cobro = (Recibo) rr.getObject(Recibo.class.getName(), idRecibo);
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(cobro.getTipoMovimiento());
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE)); 
		ctm.setFechaEmision(cobro.getFechaEmision());
		ctm.setFechaVencimiento(cobro.getFechaEmision());
		ctm.setIdEmpresa(cobro.getProveedor().getIdEmpresa());
		ctm.setIdMovimientoOriginal(cobro.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(cobro.isMonedaLocal() ? cobro.getTotalImporteGs() : cobro.getTotalImporteDs());
		ctm.setMoneda(cobro.getMoneda());
		ctm.setNroComprobante(cobro.getNumero());
		ctm.setSucursal(cobro.getSucursal());
		ctm.setSaldo(cobro.isMonedaLocal() ? cobro.getTotalImporteGs() : cobro.getTotalImporteDs());	
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento recibo de pago anticipado..
	 */
	public static void addReciboDePagoAnticipado(long idOrdenPago, String user, String siglaMoneda) throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo pago = rr.getOrdenPagoById(idOrdenPago);
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(pago.getTipoMovimiento());
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		ctm.setFechaEmision(pago.getFechaEmision());
		ctm.setIdEmpresa(pago.getProveedor().getIdEmpresa());
		ctm.setIdMovimientoOriginal(pago.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(pago.isMonedaLocal() ? pago.getTotalImporteGs() : pago.getTotalImporteDs());
		ctm.setMoneda(rr.getTipoPorSigla(siglaMoneda));
		ctm.setTipoCambio(pago.getTipoCambio());
		ctm.setNroComprobante(pago.getNumero());
		ctm.setSucursal(pago.getSucursal());
		ctm.setSaldo((pago.isMonedaLocal() ? pago.getTotalImporteGs() : pago.getTotalImporteDs()) * -1);	
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento recaudacion casa central..
	 */
	public static void addRecaudacionCentral(long idRecibo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo cobro = (Recibo) rr.getObject(Recibo.class.getName(), idRecibo);
		
		for (ReciboFormaPago fp : cobro.getFormasPago()) {
			if (fp.isRecaudacionCentral()) {
				RecaudacionCentral rcc = new RecaudacionCentral();
				rcc.setNumeroRecibo(cobro.getNumero());
				rcc.setImporteGs(fp.getMontoGs());
				rcc.setSaldoGs(fp.getMontoGs());
				rcc.setNumeroCheque("");
				rcc.setNumeroDeposito("");
				rcc.setRazonSocial(cobro.getCliente().getRazonSocial());
				rr.saveObject(rcc, user);
			}
		}
	}
	
	/**
	 * agregar movimiento cheque rechazado..
	 */
	public static void addChequeRechazado(long idCheque, String user) throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoChequeTercero cheque = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), idCheque);
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_CHEQUE_RECHAZADO));
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
		ctm.setFechaEmision(cheque.getFecha());
		ctm.setIdEmpresa(cheque.getCliente().getIdEmpresa());
		ctm.setIdMovimientoOriginal(cheque.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(cheque.getMonto());
		ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		ctm.setNroComprobante(cheque.getNumero());
		ctm.setSucursal(cheque.getSucursalApp());
		ctm.setSaldo(cheque.getMonto());	
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento cheque propio rechazado..
	 */
	public static void addChequePropioRechazado(long idCheque, long idEmpresa, String user) throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoCheque cheque = (BancoCheque) rr.getObject(BancoCheque.class.getName(), idCheque);
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_CHEQUE_RECHAZADO));
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		ctm.setFechaEmision(cheque.getFechaEmision());
		ctm.setIdEmpresa(idEmpresa);
		ctm.setIdMovimientoOriginal(cheque.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(cheque.getMonto());
		ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		ctm.setNroComprobante(cheque.getNumero() + "");
		ctm.setSucursal(rr.getSucursalAppById(2));
		ctm.setSaldo(cheque.getMonto());	
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento prestamo casa central..
	 */
	public static void addPrestamoCentral(long idPrestamo, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoDescuentoCheque prestamo = (BancoDescuentoCheque) rr.getObject(BancoDescuentoCheque.class.getName(), idPrestamo);
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_INTERNO));
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
		ctm.setFechaEmision(prestamo.getFecha());
		ctm.setIdEmpresa(Configuracion.ID_EMPRESA_YHAGUY_CENTRAL);
		ctm.setIdMovimientoOriginal(prestamo.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(prestamo.getTotalImporteGs());
		ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		ctm.setNroComprobante("PRESTAMO NRO. " + prestamo.getId());
		ctm.setSucursal(prestamo.getSucursalApp());
		ctm.setSaldo(prestamo.getTotalImporteGs());
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento prestamos internos..
	 */
	public static void addPrestamoInternoDeudor(long idPrestamo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoDescuentoCheque prestamo = (BancoDescuentoCheque) rr.getObject(BancoDescuentoCheque.class.getName(), idPrestamo);
		for (BancoCheque cheque : prestamo.getChequesPropios()) {
			CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
			ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_INTERNO));
			ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
			ctm.setFechaEmision(prestamo.getFecha());
			ctm.setFechaVencimiento(cheque.getFechaVencimiento());
			ctm.setIdEmpresa(prestamo.getAcreedor().getId());
			ctm.setIdMovimientoOriginal(prestamo.getId());
			ctm.setIdVendedor(0);
			ctm.setImporteOriginal(cheque.getMonto());
			ctm.setMoneda(prestamo.getMoneda());
			ctm.setNroComprobante("PRESTAMO NRO. " + prestamo.getId());
			ctm.setSucursal(prestamo.getSucursalApp());
			ctm.setSaldo(cheque.getMonto());
			rr.saveObject(ctm, user);
		}
		for (BancoChequeTercero cheque : prestamo.getCheques_()) {
			CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
			ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_INTERNO));
			ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
			ctm.setFechaEmision(prestamo.getFecha());
			ctm.setFechaVencimiento(cheque.getFecha());
			ctm.setIdEmpresa(prestamo.getAcreedor().getId());
			ctm.setIdMovimientoOriginal(prestamo.getId());
			ctm.setIdVendedor(0);
			ctm.setImporteOriginal(cheque.getMonto());
			ctm.setMoneda(prestamo.getMoneda());
			ctm.setNroComprobante("PRESTAMO NRO. " + prestamo.getId());
			ctm.setSucursal(prestamo.getSucursalApp());
			ctm.setSaldo(cheque.getMonto());
			rr.saveObject(ctm, user);
		}
		for (ReciboFormaPago item : prestamo.getFormasPago()) {
			CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
			ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_INTERNO));
			ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
			ctm.setFechaEmision(prestamo.getFecha());
			ctm.setFechaVencimiento(prestamo.getFecha());
			ctm.setIdEmpresa(prestamo.getAcreedor().getId());
			ctm.setIdMovimientoOriginal(prestamo.getId());
			ctm.setIdVendedor(0);
			ctm.setImporteOriginal(item.getMontoGs());
			ctm.setMoneda(prestamo.getMoneda());
			ctm.setNroComprobante("PRESTAMO NRO. " + prestamo.getId());
			ctm.setSucursal(prestamo.getSucursalApp());
			ctm.setSaldo(item.getMontoGs());
			rr.saveObject(ctm, user);
		}
	}
	
	/**
	 * agregar movimiento prestamos internos..
	 */
	public static void addPrestamoInternoAcreedor(long idPrestamo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoDescuentoCheque prestamo = (BancoDescuentoCheque) rr.getObject(BancoDescuentoCheque.class.getName(), idPrestamo);
		for (BancoCheque cheque : prestamo.getChequesPropios()) {
			CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
			ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_INTERNO));
			ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
			ctm.setFechaEmision(prestamo.getFecha());
			ctm.setFechaVencimiento(cheque.getFechaVencimiento());
			ctm.setIdEmpresa(prestamo.getAcreedor().getId());
			ctm.setIdMovimientoOriginal(prestamo.getId());
			ctm.setIdVendedor(0);
			ctm.setImporteOriginal(cheque.getMonto());
			ctm.setMoneda(prestamo.getMoneda());
			ctm.setNroComprobante("PRESTAMO NRO. " + prestamo.getId());
			ctm.setSucursal(prestamo.getSucursalApp());
			ctm.setSaldo(cheque.getMonto());
			rr.saveObject(ctm, user);
		}
		for (BancoChequeTercero cheque : prestamo.getCheques()) {
			CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
			ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_INTERNO));
			ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
			ctm.setFechaEmision(prestamo.getFecha());
			ctm.setFechaVencimiento(cheque.getFecha());
			ctm.setIdEmpresa(prestamo.getAcreedor().getId());
			ctm.setIdMovimientoOriginal(prestamo.getId());
			ctm.setIdVendedor(0);
			ctm.setImporteOriginal(cheque.getMonto());
			ctm.setMoneda(prestamo.getMoneda());
			ctm.setNroComprobante("PRESTAMO NRO. " + prestamo.getId());
			ctm.setSucursal(prestamo.getSucursalApp());
			ctm.setSaldo(cheque.getMonto());
			rr.saveObject(ctm, user);
		}
		for (ReciboFormaPago item : prestamo.getFormasPago()) {
			CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
			ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_INTERNO));
			ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
			ctm.setFechaEmision(prestamo.getFecha());
			ctm.setFechaVencimiento(prestamo.getFecha());
			ctm.setIdEmpresa(prestamo.getAcreedor().getId());
			ctm.setIdMovimientoOriginal(prestamo.getId());
			ctm.setIdVendedor(0);
			ctm.setImporteOriginal(item.getMontoGs());
			ctm.setMoneda(prestamo.getMoneda());
			ctm.setNroComprobante("PRESTAMO NRO. " + prestamo.getId());
			ctm.setSucursal(prestamo.getSucursalApp());
			ctm.setSaldo(item.getMontoGs());
			rr.saveObject(ctm, user);
		}
	}
	
	/**
	 * agregar movimiento nota credito compra..
	 */
	public static void addNotaDebitoVenta(NotaDebitoDTO dto, String user) 
			throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();		
		
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_DEBITO_VENTA));
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
		ctm.setFechaEmision(dto.getFecha());
		ctm.setFechaVencimiento(dto.getFecha());
		ctm.setIdEmpresa(dto.getCliente().getEmpresa().getId());
		ctm.setIdMovimientoOriginal(dto.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(dto.getTotalImporteGs());
		ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		ctm.setNroComprobante(dto.getNumero());
		ctm.setSucursal(rr.getSucursalAppById(dto.getSucursal().getId()));
		ctm.setSaldo(dto.getTotalImporteGs());
		rr.saveObject(ctm, user);
	}
	
	/**
	 * agregar movimiento prestamo bancario..
	 */
	public static void addPrestamoBancario(BancoPrestamo prestamo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		int cuotas = prestamo.getCuotas();
		String dd = Utiles.getDateToString(prestamo.getFecha(), "dd");
		String mm = Utiles.getDateToString(prestamo.getFecha(), "MM");
		String aa = Utiles.getDateToString(prestamo.getFecha(), "yyyy");
		int acum = Integer.parseInt(mm);
		
		for (int i = 1; i <= cuotas; i++) {
			acum += prestamo.getMesesTipoVencimiento();
			if(acum >= 12) {
				acum = acum - 12;
				aa = ((Integer.parseInt(aa) + 1) + "");
			}
			int mes_ = acum + 0;
			CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
			ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_BANCARIO));
			ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
			ctm.setFechaEmision(prestamo.getFecha());
			ctm.setFechaVencimiento(Utiles.getFecha(dd + "-" + (mes_ > 9? ("" + mes_) : ("0" + mes_)) + "-" + aa + " 00:00:00"));
			ctm.setIdEmpresa(prestamo.getCtacte().getId());
			ctm.setIdMovimientoOriginal(prestamo.getId());
			ctm.setIdVendedor(0);
			ctm.setImporteOriginal(prestamo.getDeudaTotal());
			ctm.setMoneda(prestamo.getMoneda());
			ctm.setNroComprobante(prestamo.getNumero() + " (" + (i > 9? ("" + i) : ("0" + i)) + "/" + cuotas + ")");
			ctm.setSucursal(rr.getSucursalAppById(2));
			ctm.setSaldo(prestamo.getDeudaTotal() / cuotas);
			rr.saveObject(ctm, user);
		}		
	}
	
	/**
	 * agregar movimiento prestamo bancario..
	 */
	public static void addPrestamoBancario(BancoPrestamo prestamo, List<Object[]> cuotas, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		int cuotas_ = prestamo.getCuotas();
		
		for (Object[] cuota : cuotas) {
			int nroCuota = (int) cuota[0];
			CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
			ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESTAMO_BANCARIO));
			ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
			ctm.setFechaEmision(prestamo.getFecha());
			ctm.setFechaVencimiento((Date) cuota[1]);
			ctm.setIdEmpresa(prestamo.getCtacte().getId());
			ctm.setIdMovimientoOriginal(prestamo.getId());
			ctm.setIdVendedor(0);
			ctm.setImporteOriginal(prestamo.getDeudaTotal());
			ctm.setMoneda(prestamo.getMoneda());
			ctm.setNroComprobante(prestamo.getNumero() + " (" + (nroCuota > 9? ("" + nroCuota) : ("0" + nroCuota)) + "/" + cuotas_ + ")");
			ctm.setSucursal(rr.getSucursalAppById(2));
			ctm.setSaldo((double) cuota[2]);
			rr.saveObject(ctm, user);
		}	
		for (CtaCteEmpresaMovimiento ctm : prestamo.getCancelaciones()) {
			ctm.setSaldo(0);
			rr.saveObject(ctm, user);
		}
	}
	
	/**
	 * bloquea la cuenta del cliente..
	 */
	public static void bloquearCliente(long idEmpresa, String motivo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Empresa emp = rr.getEmpresaById(idEmpresa);
		if (emp != null) {
			emp.setCuentaBloqueada(true);
			emp.setMotivoBloqueo(motivo);
			rr.saveObject(emp, user);
		}		
	}
	
	/**
	 * verifica si es un desbloqueo de la cuenta del cliente en forma temporal..
	 */
	public static void verificarBloqueoTemporal(long idEmpresa, long idCliente, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Empresa emp = rr.getEmpresaById(idEmpresa);
		if(emp.getAuxi().equals(Empresa.DESBLOQUEO_TEMPORAL)) {
			emp.setAuxi("");
			emp.setCuentaBloqueada(true);
			emp.setMotivoBloqueo("Restauracion desbloqueo temporal..");
			rr.saveObject(emp, user);
			
			Cliente cli = rr.getClienteById(idCliente);
			cli.setVentaCredito(false);
			rr.saveObject(cli, user);
		}
	}
	
	/**
	 * verifica si corresponde desbloquear al cliente..
	 */
	public static void verificarBloqueoCliente(long idEmpresa, long idCliente, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Empresa emp = rr.getEmpresaById(idEmpresa);
		Date hoy = new Date();
		boolean desbloquear = true;
		
		List<Object[]> vencidos = rr.getCtaCteMovimientosVencidos(idEmpresa);
		
		for (Object[] movim : vencidos) {
			Date vto = (Date) movim[2];
			long dias = Utiles.diasEntreFechas(vto, hoy);
			if (dias >= 60) {
				desbloquear = false;
			}
		}
		
		if(desbloquear) {
			emp.setAuxi("");
			emp.setCuentaBloqueada(false);
			emp.setMotivoBloqueo("Restauracion automatica por cobro de facturas..");
			rr.saveObject(emp, user);
			
			Cliente cli = rr.getClienteById(idCliente);
			cli.setVentaCredito(true);
			rr.saveObject(cli, user);
		}
	}
	
	/**
	 * agregar movimiento gasto..
	 */
	public static void addGastoImportacion(Gasto gasto, String nroImportacion, long idEmpresa, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(gasto.getTipoMovimiento());
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		ctm.setFechaEmision(gasto.getFecha());
		ctm.setFechaVencimiento(gasto.getVencimiento());
		ctm.setTipoCambio(gasto.getTipoCambio());
		ctm.setIdEmpresa(idEmpresa);
		ctm.setIdMovimientoOriginal(gasto.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(gasto.isMonedaLocal() ? gasto.getImporteGs_() : gasto.getImporteDs_());
		ctm.setMoneda(gasto.getMoneda());
		ctm.setNroComprobante(gasto.getNumeroFactura());
		ctm.setSucursal(gasto.getSucursal());
		ctm.setSaldo(ctm.getImporteOriginal());
		ctm.setNumeroImportacion(nroImportacion);
		rr.saveObject(ctm, user);	
	}
	
	/**
	 * agregar movimiento gasto..
	 */
	public static void addGasto(long idGasto, long idEmpresa, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Gasto gasto = rr.getGastoById(idGasto);
		CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
		ctm.setTipoMovimiento(gasto.getTipoMovimiento());
		ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR));
		ctm.setFechaEmision(gasto.getFecha());
		ctm.setFechaVencimiento(gasto.getVencimiento());
		ctm.setTipoCambio(gasto.getTipoCambio());
		ctm.setIdEmpresa(idEmpresa);
		ctm.setIdMovimientoOriginal(gasto.getId());
		ctm.setIdVendedor(0);
		ctm.setImporteOriginal(gasto.isMonedaLocal() ? gasto.getImporteGs_() : gasto.getImporteDs_());
		ctm.setMoneda(gasto.getMoneda());
		ctm.setNroComprobante(gasto.getNumeroFactura());
		ctm.setSucursal(gasto.getSucursal());
		ctm.setSaldo(ctm.getImporteOriginal());
		ctm.setNumeroImportacion("");
		rr.saveObject(ctm, user);	
	}
	
	/**
	 * recalcula el saldo en cta cte.
	 */
	public static void recalcularSaldoCtaCte(CtaCteEmpresaMovimiento ctacte, double aplicadoGs, double aplicadoDs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (ctacte.isVentaCredito()) {
			Venta venta = (Venta) rr.getObject(Venta.class.getName(), ctacte.getIdMovimientoOriginal());
			if (venta != null) {
				recalcularSaldoVentaCredito(venta);
			}
		}
		if (ctacte.isNotaCreditoVenta()) {
			NotaCredito ncr = (NotaCredito) rr.getObject(NotaCredito.class.getName(), ctacte.getIdMovimientoOriginal());
			if (ncr != null) {
				recalcularSaldoNotaCredito(ncr);
			}
		}
		if (ctacte.isAnticipoCobro()) {
			Recibo anticipo = (Recibo) rr.getObject(Recibo.class.getName(), ctacte.getIdMovimientoOriginal());
			if (anticipo != null) {
				recalcularSaldoAnticipo(anticipo);
			}
		}
		if (ctacte.isAnticipoPago()) {
			Recibo anticipo = (Recibo) rr.getObject(Recibo.class.getName(), ctacte.getIdMovimientoOriginal());
			if (anticipo != null) {
				if (anticipo.isMonedaLocal()) {
					aplicarSaldoAnticipoPagoGs(anticipo, aplicadoGs);
				} else {
					aplicarSaldoAnticipoPagoDs(anticipo, aplicadoDs);
				}				
			}
		}
		if (ctacte.isGasto()) {
			Gasto gasto = (Gasto) rr.getObject(Gasto.class.getName(), ctacte.getIdMovimientoOriginal()); 
			if (gasto != null) {
				if (gasto.isMonedaLocal()) {
					aplicarSaldoGastoGs(gasto, aplicadoGs);
				} else {
					aplicarSaldoGastoDs(gasto, aplicadoDs);
				}
			}
		}
		if (ctacte.isCompra()) {
			CompraLocalFactura fac = (CompraLocalFactura) rr.getObject(CompraLocalFactura.class.getName(), ctacte.getIdMovimientoOriginal()); 
			if (fac != null) {
				if (fac.isMonedaLocal()) {
					aplicarSaldoCompraGs(fac, aplicadoGs);
				} else {
					aplicarSaldoCompraDs(fac, aplicadoDs);
				}
			}
		}
		if (ctacte.isPrestamoInterno()) {
			if (ctacte.isMonedaLocal()) {
				aplicarSaldoPrestamoInternoGs(ctacte, aplicadoGs);
			} else {
				aplicarSaldoPrestamoInternoDs(ctacte, aplicadoDs);
			}
		}
	}
	
	/**
	 * recalcula los saldos por venta credito..
	 */
	public static void recalcularSaldoVentaCredito(Venta venta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (venta.isMonedaLocal()) {
			double recs = 0;
			double ncrs = 0;
			double ajcr = 0;
			double ajdb = 0;
			CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(venta.getId(), venta.getTipoMovimiento().getSigla(), venta.getCliente().getIdEmpresa());
			List<Object[]> recs_ = rr.getRecibosByVenta(venta.getId(), venta.getTipoMovimiento().getId());
			for (Object[] rec : recs_) {
				ReciboDetalle rdet = (ReciboDetalle) rec[1];
				recs += rdet.getMontoGs();
			}
			List<NotaCredito> ncrs_ = rr.getNotaCreditosByVenta(venta.getId());
			for (NotaCredito ncr : ncrs_) {
				if (!ncr.isAnulado()) {
					ncrs += ncr.getImporteGs();
				}				
			}
			List<AjusteCtaCte> ajcr_ = rr.getAjustesCredito(venta.getId(), venta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajc : ajcr_) {
				ajcr += ajc.getImporte();				
			}
			List<AjusteCtaCte> ajdb_ = rr.getAjustesDebito(venta.getId(), venta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajc : ajdb_) {
				ajdb += ajc.getImporte();				
			}
			if (ctacte != null) {
				double hist_ = ((venta.getTotalImporteGs() + ajdb) - (ncrs + recs + ajcr));
				ctacte.setSaldo(hist_);
				rr.saveObject(ctacte, ctacte.getUsuarioMod());				
			}
		}		
		if (!venta.isMonedaLocal()) {
			double recs = 0;
			double ncrs = 0;
			double ajcr = 0;
			double ajdb = 0;
			CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(venta.getId(), venta.getTipoMovimiento().getSigla(), venta.getCliente().getIdEmpresa());
			List<Object[]> recs_ = rr.getRecibosByVenta(venta.getId(), venta.getTipoMovimiento().getId());
			for (Object[] rec : recs_) {
				ReciboDetalle rdet = (ReciboDetalle) rec[1];
				recs += rdet.getMontoDs();
			}
			List<NotaCredito> ncrs_ = rr.getNotaCreditosByVenta(venta.getId());
			for (NotaCredito ncr : ncrs_) {
				if (!ncr.isAnulado()) {
					ncrs += ncr.getImporteDs();
				}				
			}
			List<AjusteCtaCte> ajcr_ = rr.getAjustesCredito(venta.getId(), venta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajc : ajcr_) {
				ajcr += ajc.getImporte();				
			}
			List<AjusteCtaCte> ajdb_ = rr.getAjustesDebito(venta.getId(), venta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajc : ajdb_) {
				ajdb += ajc.getImporte();				
			}
			if (ctacte != null) {
				double hist_ = ((venta.getTotalImporteDs() + ajdb) - (ncrs + recs + ajcr));
				ctacte.setSaldo(hist_);
				rr.saveObject(ctacte, ctacte.getUsuarioMod());				
			}
		}
	}
	
	/**
	 * recalcula los saldos por nota de credito..
	 */
	public static void recalcularSaldoNotaCredito(NotaCredito nc) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (nc.isNotaCreditoVenta() && (!nc.isNotaCreditoVentaContado()) && nc.isMonedaLocal()) {		
			double ncr = nc.getImporteGs();
			double vta = nc.getVentaAplicada().getImporteGs();
			double saldo = ncr > vta ? (ncr - vta) : 0.0;
			double ajcr = 0;
			double ajdb = 0;
			
			CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(nc.getId(), nc.getTipoMovimiento().getSigla(), nc.getCliente().getIdEmpresa());
			
			List<AjusteCtaCte> ajcr_ = rr.getAjustesCredito(nc.getId(), nc.getTipoMovimiento().getId());
			for (AjusteCtaCte ajc : ajcr_) {
				ajcr += ajc.getImporte();				
			}
			
			List<AjusteCtaCte> ajdb_ = rr.getAjustesDebito(nc.getId(), nc.getTipoMovimiento().getId());
			for (AjusteCtaCte ajc : ajdb_) {
				ajdb += ajc.getImporte();				
			}
			
			if (ctacte != null) {
				double hist_ = ((saldo + ajcr) - (ajdb));
				if (hist_ < 0) {
					ctacte.setSaldo(hist_);
					rr.saveObject(ctacte, ctacte.getUsuarioMod());
				}				
			}
		}
	}
	
	/**
	 * recalcula los saldos por anticipo de cobro..
	 */
	public static void recalcularSaldoAnticipo(Recibo anticipo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (anticipo.isMonedaLocal()) {
			double ajcr = 0;
			double ajdb = 0;
			CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(anticipo.getId(), anticipo.getTipoMovimiento().getSigla(), anticipo.getCliente().getIdEmpresa());
			
			List<AjusteCtaCte> ajcr_ = rr.getAjustesCredito(anticipo.getId(), anticipo.getTipoMovimiento().getId());
			for (AjusteCtaCte ajc : ajcr_) {
				ajcr += ajc.getImporte();				
			}
			List<AjusteCtaCte> ajdb_ = rr.getAjustesDebito(anticipo.getId(), anticipo.getTipoMovimiento().getId());
			for (AjusteCtaCte ajc : ajdb_) {
				ajdb += ajc.getImporte();				
			}
			if (ctacte != null) {
				double hist_ = ((anticipo.getTotalImporteGs() - ajdb) + (ajcr));
				ctacte.setSaldo(hist_);
				rr.saveObject(ctacte, ctacte.getUsuarioMod());				
			}
		}
	}
	
	/**
	 * aplica los saldos por anticipo pago..
	 */
	public static void aplicarSaldoAnticipoPagoGs(Recibo anticipo, double aplicadoGs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(anticipo.getId(), anticipo.getTipoMovimiento().getSigla());
		ctacte.setSaldo(ctacte.getSaldo() + aplicadoGs);	
		rr.saveObject(ctacte, ctacte.getUsuarioMod());
	}
	
	/**
	 * aplica los saldos por anticipo pago..
	 */
	public static void aplicarSaldoAnticipoPagoDs(Recibo anticipo, double aplicadoDs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(anticipo.getId(), anticipo.getTipoMovimiento().getSigla());
		ctacte.setSaldo(ctacte.getSaldo() + aplicadoDs);	
		rr.saveObject(ctacte, ctacte.getUsuarioMod());
	}
	
	/**
	 * recalcula los saldos por gasto..
	 */
	public static void aplicarSaldoGastoGs(Gasto gasto, double aplicadoGs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(gasto.getId(), gasto.getTipoMovimiento().getSigla());
		ctacte.setSaldo(ctacte.getSaldo() - aplicadoGs);	
		rr.saveObject(ctacte, ctacte.getUsuarioMod());
	}
	
	/**
	 * recalcula los saldos por gasto..
	 */
	public static void aplicarSaldoGastoDs(Gasto gasto, double aplicadoDs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(gasto.getId(), gasto.getTipoMovimiento().getSigla());
		ctacte.setSaldo(ctacte.getSaldo() - aplicadoDs);	
		rr.saveObject(ctacte, ctacte.getUsuarioMod());
	}
	
	/**
	 * recalcula los saldos por compra..
	 */
	public static void aplicarSaldoCompraGs(CompraLocalFactura compra, double aplicadoGs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(compra.getId(), compra.getTipoMovimiento().getSigla());
		ctacte.setSaldo(ctacte.getSaldo() - aplicadoGs);	
		rr.saveObject(ctacte, ctacte.getUsuarioMod());
	}
	
	/**
	 * recalcula los saldos por compra..
	 */
	public static void aplicarSaldoCompraDs(CompraLocalFactura compra, double aplicadoDs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(compra.getId(), compra.getTipoMovimiento().getSigla());
		ctacte.setSaldo(ctacte.getSaldo() - aplicadoDs);	
		rr.saveObject(ctacte, ctacte.getUsuarioMod());
	}
	
	/**
	 * recalcula los saldos por prestamo..
	 */
	public static void aplicarSaldoPrestamoInternoGs(CtaCteEmpresaMovimiento ctacte, double aplicadoGs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte_ = (CtaCteEmpresaMovimiento) rr.getObject(CtaCteEmpresaMovimiento.class.getName(), ctacte.getId());
		ctacte_.setSaldo(ctacte_.getSaldo() - aplicadoGs);	
		rr.saveObject(ctacte_, ctacte_.getUsuarioMod());
	}
	
	/**
	 * recalcula los saldos por prestamo..
	 */
	public static void aplicarSaldoPrestamoInternoDs(CtaCteEmpresaMovimiento ctacte, double aplicadoDs) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CtaCteEmpresaMovimiento ctacte_ = (CtaCteEmpresaMovimiento) rr.getObject(CtaCteEmpresaMovimiento.class.getName(), ctacte.getId());
		ctacte_.setSaldo(ctacte_.getSaldo() - aplicadoDs);	
		rr.saveObject(ctacte_, ctacte_.getUsuarioMod());
	}
	
	/**
	 * @return las aplicaciones de una venta..
	 * [0]: emision
	 * [1]: numero
	 * [2]: concepto
	 * [3]: importe
	 * [4]: +-
	 * [5]: debe
	 * [6]: haber
	 * [7]: saldo
	 */
	public static List<Object[]> getAplicacionesVenta(long idVenta) throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta vta = (Venta) rr.getObject(Venta.class.getName(), idVenta);
		if (vta != null) {
			Date emision_ = vta.getFecha();
			String numero_ = vta.getNumero();
			String concepto_ = vta.getTipoMovimiento().getDescripcion();
			Object[] det_ = new Object[] { emision_, numero_, concepto_, vta.getTotalImporteGs(), 1, 0.0, 0.0, 0.0 };
			out.add(det_);
			
			List<NotaCredito> ncs = rr.getNotaCreditosByVenta(vta.getId());
			for (NotaCredito nc : ncs) {
				if (!nc.isAnulado()) {
					Date emision = nc.getFechaEmision();
					String numero = nc.getNumero();
					String concepto = nc.getTipoMovimiento().getDescripcion() + " - "
							+ nc.getMotivo().getDescripcion().toUpperCase();
					Object[] det = new Object[] { emision, numero, concepto, nc.getImporteGs(), -1, 0.0, 0.0, 0.0 };
					out.add(det);
				}
			}
			List<Object[]> recs = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
			for (Object[] rec : recs) {
				Recibo recibo = (Recibo) rec[0];
				ReciboDetalle rdet = (ReciboDetalle) rec[1];				
				Date emision = recibo.getFechaEmision();
				String numero = recibo.getNumero();
				String concepto = recibo.getTipoMovimiento().getDescripcion();
				Object[] det = new Object[] { emision, numero, concepto, rdet.getMontoGs(), -1, 0.0, 0.0, 0.0 };
				out.add(det);
			}
			
			List<AjusteCtaCte> ajustes = rr.getAjustesCredito(vta.getId(), vta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes) {				
				Date emision = ajuste.getFecha();
				String numero = ajuste.getDebito().getNroComprobante();
				String concepto = "CREDITO CTA.CTE.";
				Object[] det = new Object[] { emision, numero, concepto, ajuste.getImporte(), -1, 0.0, 0.0, 0.0 };
				out.add(det);
			}
			
			List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(vta.getId(), vta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes_) {				
				Date emision = ajuste.getFecha();
				String numero = ajuste.getDebito().getNroComprobante();
				String concepto = "DEBITO CTA.CTE.";
				Object[] det = new Object[] { emision, numero, concepto, ajuste.getImporte(), 1, 0.0, 0.0, 0.0 };
				out.add(det);
			}
		}
		// ordena la lista segun fecha..
		Collections.sort(out, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[0];
				Date fecha2 = (Date) o2[0];
				return fecha1.compareTo(fecha2);
			}
		});
		double acum = 0;
		for (Object[] item : out) {
			int signo = (int) item[4];
			double importe = (double) item[3];
			double debe = signo > 0? importe : 0.0;
			double haber = signo < 0? importe : 0.0;
			double saldo = acum + (importe * signo);
			item[5] = debe;
			item[6] = haber;
			item[7] = saldo;
			acum = saldo;
		}
		return out;
	}
}
