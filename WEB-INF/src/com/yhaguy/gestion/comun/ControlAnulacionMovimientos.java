package com.yhaguy.gestion.comun;

import java.util.HashSet;
import java.util.List;

import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloStock;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoMovimiento;
import com.yhaguy.domain.BancoTarjeta;
import com.yhaguy.domain.CajaReposicion;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.FuncionarioCtaCteDetalle;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.NotaCreditoDetalle;
import com.yhaguy.domain.NotaDebito;
import com.yhaguy.domain.NotaDebitoDetalle;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RetencionIva;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.TransferenciaDetalle;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.process.ProcesosHistoricos;

public class ControlAnulacionMovimientos {

	/**
	 * - anulacion del cobro..
	 * - anulacion de la aplicacion de factura (devolucion de saldo)
	 * - anulacion del movimiento cheque tercero asociado..
	 * - anulacion del movimiento tarjeta asociado..
	 */
	public static void anularCobro(long idCobro, String motivo, String user) throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		
		// anula el recibo..
		Recibo rec = (Recibo) rr.getObject(Recibo.class.getName(), idCobro);
		rec.setEstadoComprobante(getEstadoComprobanteAnulado());
		rec.setMotivoAnulacion(motivo);
		rr.saveObject(rec, user);
		
		// anula los movimientos asociados..
		for (ReciboFormaPago fpago : rec.getFormasPago()) {
			String sigla = fpago.getTipo().getSigla();
			
			switch (sigla) {
			case Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO:	
				anularChequeTercero(fpago.getId(), user);
				break;

			case Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO:	
				anularMovimientoTarjeta(fpago.getId(), user);
				break;
				
			case Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO:
				anularMovimientoBanco(fpago.getId(), user);
				break;
				
			case Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO:
				anularMovimientoBanco(fpago.getId(), user);
				break;
			}
		}
		
		// Falta llamar a ctacte y desaplicar el cobro..		
	}
	
	/**
	 * - anulacion del pago..
	 * - anulacion de la aplicacion de factura (devolucion de saldo)
	 * - anulacion del movimiento cheque propio asociado..
	 * - anulacion de la retencion asociada..
	 */
	public static void anularPago(long idPago, String motivo, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		// anula el recibo..
		Recibo pago = (Recibo) rr.getObject(Recibo.class.getName(), idPago);
		pago.setEstadoComprobante(getEstadoComprobanteAnulado());
		pago.setMotivoAnulacion(motivo);

		// anula la retencion..
		if (pago.getRetencion() != null)
			anularRetencionIva(pago.getRetencion().getId(), user);

		// anula los movimientos asociados..
		for (ReciboFormaPago fpago : pago.getFormasPago()) {
			String sigla = fpago.getTipo().getSigla();

			switch (sigla) {
			case Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO:
				anularChequePropio(fpago.getId(), user);
				anularMovimientoBanco(fpago.getId(), user);
				break;
			}
		}

		// Falta llamar a ctacte y desaplicar el pago..
		
		rr.saveObject(pago, user);
	}
	
	/**
	 * - anulacion de reposicion de caja..
	 * - anulacion del movimiento cheque propio asociado..
	 * - anulacion del movimiento de banco asociado..
	 */
	public static void anularReposicionCaja(long idReposicion, String motivo,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		String clase = CajaReposicion.class.getName();
		
		CajaReposicion rep = (CajaReposicion) rr.getObject(clase, idReposicion);
		rep.setEstadoComprobante(getEstadoComprobanteAnulado());
		rep.setMotivoAnulacion(motivo);
		
		String sigla = rep.getFormaPago().getTipo().getSigla();
		
		if(sigla.equals(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO)) {
			anularChequePropio(rep.getFormaPago().getId(), user);
			anularMovimientoBanco(rep.getFormaPago().getId(), user);
		}				
		rr.saveObject(rep, user);
	}
	
	/**
	 * - anulacion de la venta credito..
	 * - anulacion del saldo en cta cte..
	 * - anulacion del pedido de venta..
	 * - anulacion del movimiento de stock..
	 * - anulacion de la reserva..
	 * - actualizar el stock del articulodeposito..
	 */
	public static void anularVentaCredito(long idVenta, String motivo,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		// anula la venta..
		Venta venta = (Venta) rr.getObject(Venta.class.getName(), idVenta);
		venta.setEstadoComprobante(getEstadoComprobanteAnulado());
		venta.setObservacion(motivo);
		
		if (venta.isVentaConNotaDeCredito()) {
			throw new Exception("La venta tiene una Nota de Crédito aplicada: " + venta.getNumeroNotaCredito());
		}
		
		if (venta.isVentaCobrada()) {
			throw new Exception("La venta tiene un Recibo de cobro aplicado: "
					+ venta.getNumeroReciboCobro());
		}		
				
		// actualiza el stock del articulo..
		for (VentaDetalle item : venta.getDetalles()) {
			ArticuloDeposito adp = rr.getArticuloDeposito(item.getArticulo().getId(), venta.getDeposito().getId());
			if (adp == null) {
				adp = new ArticuloDeposito();
				adp.setArticulo(item.getArticulo());
				adp.setDeposito(venta.getDeposito());
				adp.setStock(0);
				rr.saveObject(adp, user);
			}
			ControlArticuloStock.actualizarStock(adp.getId(), item.getCantidad(), user);
		}
		
		// actualizar cta cte..
		List<CtaCteEmpresaMovimiento> ctactes = rr.getCtaCteMovimientosByIdMovimiento(
				venta.getId(), venta.getTipoMovimiento().getSigla());
		for (CtaCteEmpresaMovimiento ctacte : ctactes) {
			ctacte.setSaldo(0);
			ctacte.setAnulado(true);
			rr.saveObject(ctacte, user);
		}
		
		rr.saveObject(venta, user);
		
		// actualiza el historico venta / metas..
		double tot_vta = venta.getTotalImporteGsSinIva();
		ProcesosHistoricos.updateHistoricoVentaMeta((tot_vta * -1), 0, venta.getVendedor().getRazonSocial().equals("SERVICIO"));
		ProcesosHistoricos.updateHistoricoVentaDiaria(venta.getFecha(), (tot_vta * -1), 0);
		Control.updateControlTalonario(user, -1);
	}
	
	/**
	 * - anulacion de la venta contado..
	 * - anulacion del pedido de venta..
	 * - anulacion del movimiento de stock..
	 * - anulacion de la reserva..
	 * - anulacion de los movimientos de formas de pago asociados..
	 */
	public static void anularVentaContado(long idVenta, String motivo,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		// anula la venta..
		Venta venta = (Venta) rr.getObject(Venta.class.getName(), idVenta);
		venta.setEstadoComprobante(getEstadoComprobanteAnulado());
		venta.setObservacion(motivo);
		
		if (venta.isVentaConNotaDeCredito()) {
			throw new Exception("La venta tiene una Nota de Crédito aplicada: " + venta.getNumeroNotaCredito());
		}		
		rr.saveObject(venta, user);
		
		// anula los movimientos de formaPago asociados..
		for (ReciboFormaPago fpago : venta.getFormasPago()) {
			String sigla = fpago.getTipo().getSigla();
			
			switch (sigla) {
			case Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO:	
				anularChequeTercero(fpago.getId(), user);
				break;

			case Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO:	
				anularMovimientoTarjeta(fpago.getId(), user);
				break;
				
			case Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO:
				anularMovimientoBanco(fpago.getId(), user);
				break;
				
			case Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO:
				anularMovimientoBanco(fpago.getId(), user);
				break;
			}
		}
		
		// actualiza el stock del articulo..
		for (VentaDetalle item : venta.getDetalles()) {
			ArticuloDeposito adp = rr.getArticuloDeposito(item.getArticulo().getId(), venta.getDeposito().getId());
			if (adp == null) {
				adp = new ArticuloDeposito();
				adp.setArticulo(item.getArticulo());
				adp.setDeposito(venta.getDeposito());
				adp.setStock(0);
				rr.saveObject(adp, user);
			}
			if (adp != null) {
				ControlArticuloStock.actualizarStock(adp.getId(), item.getCantidad(), user);
			}
		}	
		
		// actualiza el historico venta / metas..
		double tot_vta = venta.getTotalImporteGsSinIva();
		ProcesosHistoricos.updateHistoricoVentaMeta((tot_vta * -1), 0, venta.getVendedor().getRazonSocial().equals("SERVICIO"));
		ProcesosHistoricos.updateHistoricoVentaDiaria(venta.getFecha(), (tot_vta * -1), 0);
		ProcesosHistoricos.updateHistoricoCobranzaMeta((tot_vta * -1));
		ProcesosHistoricos.updateHistoricoCobranzaDiaria(venta.getFecha(), (tot_vta * -1));
		Control.updateControlTalonario(user, -1);
	}
	
	/**
	 * anulacion del ajuste de stock..
	 */
	public static void anularAjusteStock(long idAjuste, String motivo,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		AjusteStock ajuste = (AjusteStock) rr.getObject(
				AjusteStock.class.getName(), idAjuste);
		ajuste.setEstadoComprobante(getEstadoComprobanteAnulado());
		ajuste.setDescripcion(motivo + " - " + " Anulado por: " + user);
		rr.saveObject(ajuste, user);

		//actualiza el stock del articulo..
		for (AjusteStockDetalle item : ajuste.getDetalles()) {
			ControlArticuloStock.actualizarStock(item.getArticulo().getId(),
					ajuste.getDeposito().getId(), item.getCantidad() * -1, user, true);
		}
	}
	
	/**
	 * - anulacion del movimiento de stock..
	 * - actualizacion del stock..
	 */
	public static void anularMovimientoStock(long idMovimiento,
			long idTipoMovimiento, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloStock> arts = rr.getStockMovimientos(idMovimiento,
				idTipoMovimiento);
		for (ArticuloStock item : arts) {
			item.setAnulado(true);
			
			// actualiza el stock..
			ControlArticuloStock.actualizarStock(item.getArticuloDep().getId(), (item.getCantidad() * -1), user);
			
			rr.saveObject(item, user);
		}
	}
	
	/**
	 * anulacion de un gasto de caja chica..
	 */
	public static void anularGastoCajaChica(long idGasto, String motivo,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Gasto gasto = rr.getGastoById(idGasto);
		gasto.setEstadoComprobante(getEstadoComprobanteAnulado());
		gasto.setMotivoComprobanteFisico(motivo);
		rr.saveObject(gasto, user);
	}
	
	/**
	 * - anulacion del egreso..
	 * - anulacion del movimiento cheque propio asociado..
	 * - anulacion del movimiento de banco asociado..
	 * - anulacion del movimiento de funcionario asociado..
	 */
	public static void anularEgresoCaja(long idEgreso, String motivo,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String clase = CajaReposicion.class.getName();

		CajaReposicion egreso = (CajaReposicion) rr.getObject(clase, idEgreso);
		egreso.setEstadoComprobante(getEstadoComprobanteAnulado());
		egreso.setMotivoAnulacion(motivo);

		String siglaTipo = egreso.getTipoEgreso().getSigla();
		if (!siglaTipo
				.equals(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO_SIN_COMP))
			anularMovimientoFuncionario(egreso.getNumero(), user);

		String sigla = egreso.getFormaPago().getTipo().getSigla();

		if (sigla.equals(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO)) {
			anularChequePropio(egreso.getFormaPago().getId(), user);
			anularMovimientoBanco(egreso.getFormaPago().getId(), user);
		}
		rr.saveObject(egreso, user);
	}
	
	/**
	 * anulacion de una nota de credito de venta..
	 */
	public static void anularNotaCreditoVenta(long idNotaCredito, String motivo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		// anula la nota de credito..
		NotaCredito nc = (NotaCredito) rr.getObject(NotaCredito.class.getName(), idNotaCredito);
		nc.setEstadoComprobante(getEstadoComprobanteAnulado());
		nc.setObservacion(motivo);		
		rr.saveObject(nc, user);
		
		// actualiza el stock del articulo si es motivo devolucion..
		if (nc.isMotivoDevolucion()) {
			for (NotaCreditoDetalle item : nc.getDetallesArticulos()) {
				ArticuloDeposito adp = rr.getArticuloDeposito(item.getArticulo().getId(), nc.getVentaAplicada().getDeposito().getId());
				if (adp == null) {
					adp = new ArticuloDeposito();
					adp.setArticulo(item.getArticulo());
					adp.setDeposito(nc.getVentaAplicada().getDeposito());
					adp.setStock(0);
					rr.saveObject(adp, user);
				}
				ControlArticuloStock.actualizarStock(adp.getId(), item.getCantidad() * -1, user);
			}
		}		
		
		// actualizar cta cte..
		List<CtaCteEmpresaMovimiento> ctactes = rr.getCtaCteMovimientosByIdMovimiento(nc.getId(), nc.getTipoMovimiento().getSigla());
		double saldo = 0;
		double importe = nc.getImporteGs();
		for (CtaCteEmpresaMovimiento ctacte : ctactes) {
			saldo += ctacte.getSaldo();
			ctacte.setSaldo(0);
			ctacte.setAnulado(true);
			rr.saveObject(ctacte, user);
		}
		
		List<CtaCteEmpresaMovimiento> ctactes_ = rr.getCtaCteMovimientosByIdMovimiento(nc.getVentaAplicada().getId(), nc.getVentaAplicada().getTipoMovimiento().getSigla());
		if (ctactes_.size() > 0) {
			CtaCteEmpresaMovimiento ctacte = ctactes_.get(ctactes_.size() - 1);
			ctacte.setSaldo(ctacte.getSaldo() + (importe - saldo));
		}
		
		// actualiza el historico venta / metas..
		double tot_nc = nc.getTotalImporteGsSinIva();
		ProcesosHistoricos.updateHistoricoVentaMeta((tot_nc), 0, nc.getVendedor().getRazonSocial().equals("SERVICIO"));
		ProcesosHistoricos.updateHistoricoVentaDiaria(nc.getFechaEmision(), (tot_nc), 0);
	}
	
	/**
	 * anulacion de una transferencia externa..
	 */
	public static void anularTransferenciaRemision(long idTransferencia, String motivo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		Transferencia transf = (Transferencia) rr.getObject(Transferencia.class.getName(), idTransferencia);
		transf.setTransferenciaEstado(getEstadoComprobanteAnulado());
		transf.setObservacion(motivo);		
		rr.saveObject(transf, user);
		
		for (TransferenciaDetalle item : transf.getDetalles()) {
			ControlArticuloStock.recalcularStock(item.getArticulo().getId(), transf.getDepositoSalida().getId(), user);
			ControlArticuloStock.recalcularStock(item.getArticulo().getId(), transf.getDepositoEntrada().getId(), user);
		}
	}
	
	/**
	 * anulacion de una nota de debito..
	 */
	public static void anularNotaDebito(long idNotaDebito, String motivo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		NotaDebito nd = (NotaDebito) rr.getObject(NotaDebito.class.getName(), idNotaDebito);
		nd.setEstadoComprobante(getEstadoComprobanteAnulado());
		nd.setAuxi(motivo);
		nd.setImporteGs(0);
		for (NotaDebitoDetalle ndet : nd.getDetalles()) {
			ndet.setImporteGs(0);
		}
		rr.saveObject(nd, user);
		CtaCteEmpresaMovimiento movim = rr.getCtaCteMovimientoByIdMovimiento(nd.getId().longValue(), nd.getTipoMovimiento().getSigla());
		movim.setAnulado(true);
		movim.setSaldo(0);
		rr.saveObject(movim, user);
	}
	
	/**
	 * anulacion de una orden de pago..
	 */
	public static void anularOrdenPago(long idOrden, String motivo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Recibo op = (Recibo) rr.getObject(Recibo.class.getName(), idOrden);
		op.setEstadoComprobante(getEstadoComprobanteAnulado());
		op.setMotivoAnulacion(motivo);
		op.setTotalImporteGs(0);
		op.setTotalImporteDs(0);	
		
		if (op.isEntregado()) {
			for (ReciboDetalle item : op.getDetalles()) {
				if (item.getMovimiento() != null) {
					item.getMovimiento().setSaldo(item.getMovimiento().getSaldo() + (op.isMonedaLocal() ? item.getMontoGs() : item.getMontoDs()));
					rr.saveObject(item.getMovimiento(), user);
				}
			}
		}		
		for (ReciboFormaPago fp : op.getFormasPago()) {
			if (fp.isChequePropio()) {
				ControlAnulacionMovimientos.anularChequePropio(fp.getId(), user);
			}
		}		
		for (ReciboFormaPago fp : op.getFormasPago()) {
			if (!fp.isChequePropio()) {
				rr.deleteObject(fp);
			}
		}		
		for (ReciboDetalle item : op.getDetalles()) {
			rr.deleteObject(item);
		}
		op.setDetalles(new HashSet<ReciboDetalle>());
		op.setFormasPago(new HashSet<ReciboFormaPago>());
		rr.saveObject(op, user);
		
		CtaCteEmpresaMovimiento movim = rr.getCtaCteMovimientoByIdMovimiento(op.getId().longValue(), op.getTipoMovimiento().getSigla());
		if (movim != null) {
			movim.setAnulado(true);
			movim.setSaldo(0);
			rr.saveObject(movim, user);
		}		
	}
	
	/**
	 * anulacion de una compra local..
	 */
	public static void anularCompraLocal(long idCompraLocal) {
	}
	
	/**
	 * anulacion de un gasto..
	 */
	public static void anularGasto(long idGasto) {
	}
	
	/**
	 * anulacion de una retencion..
	 */
	public static void anularRetencionIva(long idRetencion, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		RetencionIva ret = rr.getRetencion(idRetencion);
		ret.setEstadoComprobante(getEstadoComprobanteAnulado());
		rr.saveObject(ret, user);
	}
	
	/**
	 * anulacion de cheque propio..
	 */
	public static void anularChequePropio(long idFormaPago, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoCheque cheque = rr.getChequePropio(idFormaPago);
		if (cheque != null) {
			cheque.setAnulado(true);
			cheque.setMonto(0);
			rr.saveObject(cheque, user);
		}
	}
	
	/**
	 * anulacion de cheque de tercero..
	 */
	public static void anularChequeTercero(long idFormaPago, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoChequeTercero cheque = rr.getChequeTercero(idFormaPago);
		cheque.setAnulado(true);
		rr.saveObject(cheque, user);
	}
	
	/**
	 * anulacion de tarjeta..
	 */
	public static void anularMovimientoTarjeta(long idFormaPago, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoTarjeta tarjeta = rr.getTarjeta(idFormaPago);
		tarjeta.setAnulado(true);
		rr.saveObject(tarjeta, user);
	}
	
	/**
	 * anulacion de BancoMovimiento..
	 */
	public static void anularMovimientoBanco(long idFormaPago, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoMovimiento bmv = rr.getBancoMovimiento(idFormaPago);
		bmv.setAnulado(true);
		rr.saveObject(bmv, user);
	}
	
	/**
	 * anulacion de FuncionarioMovimiento..
	 */
	public static void anularMovimientoFuncionario(String nroComprobante,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		FuncionarioCtaCteDetalle det = rr
				.getFuncionarioMovimiento(nroComprobante);
		det.setAnulado(true);
		rr.saveObject(det, user);
	}

	/**
	 * @return el estado comprobante anulado..
	 */
	private static Tipo getEstadoComprobanteAnulado() throws Exception {
		String sigla = Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO;
		RegisterDomain rr = RegisterDomain.getInstance();
		Tipo out = rr.getTipoPorSigla(sigla);
		return out;
	}
}
