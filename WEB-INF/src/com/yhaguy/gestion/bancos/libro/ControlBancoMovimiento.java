package com.yhaguy.gestion.bancos.libro;

import java.util.Date;
import java.util.List;

import com.coreweb.control.Control;
import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoDebito;
import com.yhaguy.domain.BancoMovimiento;
import com.yhaguy.domain.BancoTarjeta;
import com.yhaguy.domain.ProcesadoraTarjeta;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.gestion.bancos.cheques.AssemblerBancoCheque;
import com.yhaguy.gestion.bancos.cheques.BancoChequeDTO;
import com.yhaguy.gestion.bancos.debitos.BancoDebitoDTO;
import com.yhaguy.gestion.bancos.descuentos.BancoDescuentoChequeDTO;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.util.Utiles;

public class ControlBancoMovimiento extends Control {

	public ControlBancoMovimiento(Assembler ass) {
		super(ass);
	}

	private UtilDTO getUtilDto() {
		return (UtilDTO) getDtoUtil();
	}
	
	/**
	 * agrega un cheque de tercero desde una forma pago..
	 */
	public static void addChequeTercero(ReciboFormaPago formaPago, Recibo rec, String user) 
		throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoChequeTercero bct = new BancoChequeTercero();
		bct.setFecha(formaPago.getChequeFecha());
		bct.setBanco(formaPago.getChequeBanco());
		bct.setNumero(formaPago.getChequeNumero());
		bct.setLibrado(formaPago.getChequeLibrador());
		bct.setMoneda(formaPago.getMoneda());
		bct.setCliente(rec.getCliente());
		bct.setNumeroPlanilla(rec.getNumeroPlanilla());
		bct.setNumeroRecibo(rec.getNumero());
		bct.setNumeroVenta("");
		bct.setNumeroReembolso("");
		bct.setVendedor("");
		bct.setMonto(formaPago.getMontoChequeGs());
		bct.setDepositado(false);
		bct.setSucursalApp(rec.getSucursal());
		bct.setObservacion("");
		bct.setAuxi("RFP " + formaPago.getId());
		bct.setDiferido(formaPago.isChequeAdelantado(new Date()));
		bct.setNumeroDeposito("");
		bct.setNumeroDescuento("");		
		bct.setReciboFormaPago(formaPago);
		rr.saveObject(bct, user);
	}
	
	/**
	 * agrega el movimiento debito banco..
	 */
	public static void addMovimientoDebitoBancario(BancoDebitoDTO dto, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoMovimiento bm = new BancoMovimiento();
		bm.setDescripcion(dto.getDescripcion());
		bm.setFecha(dto.getFecha());
		bm.setMonto(dto.getImporte());
		bm.setNroCuenta((BancoCta) rr.getObject(BancoCta.class.getName(), dto.getCuenta().getId()));
		bm.setNroReferencia(dto.getNumero());
		bm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEBITO_BANCARIO));		
		rr.saveObject(bm, user);
	}
	
	/**
	 * agrega el movimiento descuento de cheque..
	 */
	public static void addMovimientoDescuentoCheques(BancoDescuentoChequeDTO dto, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoMovimiento bm = new BancoMovimiento();
		bm.setDescripcion("DESCUENTO DE CHEQUES NRO. " + dto.getId() + "(AL DIA)");
		bm.setFecha(dto.getFecha());
		bm.setMonto(dto.getLiq_neto_aldia());
		bm.setNroCuenta((BancoCta) rr.getObject(BancoCta.class.getName(), 1));
		bm.setNroReferencia("DESC. NRO. " + dto.getId());
		bm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEBITO_BANCARIO));		
		rr.saveObject(bm, user);
		
		BancoMovimiento bm_ = new BancoMovimiento();
		bm_.setDescripcion("DESCUENTO DE CHEQUES NRO. " + dto.getId() + "(DIFERIDOS)");
		bm_.setFecha(dto.getFecha());
		bm_.setMonto(dto.getLiq_neto_diferidos());
		bm_.setNroCuenta((BancoCta) rr.getObject(BancoCta.class.getName(), 1));
		bm_.setNroReferencia("DESC. NRO. " + dto.getId());
		bm_.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEBITO_BANCARIO));		
		rr.saveObject(bm_, user);
	}
	
	/**
	 * agrega el movimiento deposito bancario..
	 */
	public static void addMovimientoDepositoBancario(BancoBoletaDeposito dep, String user)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		// marca los cheques como depositados..
		for (BancoChequeTercero cheque : dep.getCheques()) {
			cheque.setDepositado(true);
			cheque.setNumeroDeposito(dep.getNumeroBoleta());
			cheque.setFechaDeposito(dep.getFecha());
			rr.saveObject(cheque, user);
		}
	}
	
	/**
	 * agrega un debito bancario a partir del dto..
	 */
	public static void addDebitoBancario(BancoDebitoDTO dto, String user) 
		throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoDebito deb = new BancoDebito();
		deb.setConfirmado(true);
		deb.setCuenta((BancoCta) rr.getObject(BancoCta.class.getName(), dto.getCuenta().getId()));
		deb.setDescripcion(dto.getDescripcion());
		deb.setFecha(dto.getFecha());
		deb.setImporte(dto.getImporte());
		deb.setNumero(dto.getNumero());
		deb.setSucursal((SucursalApp) rr.getObject(SucursalApp.class.getName(), dto.getSucursal().getId()));
		rr.saveObject(deb, user);
		
		addMovimientoDebitoBancario(dto, user);
	}
	
	/**
	 * registra el cheque como rechazado..
	 */
	public static void registrarChequeRechazado(long idCheque, String motivo, Date fechaRechazo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoChequeTercero cheque = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), idCheque);
		cheque.setRechazado(true);
		cheque.setObservacion(motivo.toUpperCase());
		cheque.setFechaRechazo(fechaRechazo);
		rr.saveObject(cheque, user);
		
		ControlCuentaCorriente.addChequeRechazado(idCheque, user);
	}
	
	/**
	 * registra el cheque como rechazado interno..
	 */
	public static void registrarChequeRechazadoInterno(long idCheque, String motivo, Date fechaRechazo, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoChequeTercero cheque = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), idCheque);
		cheque.setRechazoInterno(true);
		cheque.setObservacion(motivo.toUpperCase());
		cheque.setFechaRechazo(fechaRechazo);
		rr.saveObject(cheque, user);
		
		ControlCuentaCorriente.addChequeRechazado(idCheque, user);
	}
	
	/**
	 * reembolso de cheques rechazados..
	 */
	public static void reembolsoChequeRechazado(Recibo reembolso, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (ReciboDetalle det : reembolso.getDetalles()) {
			long idCheque = det.getMovimiento().getIdMovimientoOriginal();
			BancoChequeTercero cheque = (BancoChequeTercero) rr.getObject(BancoChequeTercero.class.getName(), idCheque);
			cheque.setReembolsado(true);
			cheque.setCancelado(det.getMovimiento().getSaldo() <= 500);
			cheque.setNumeroReembolso(reembolso.getNumero());
			rr.saveObject(cheque, user);
		}
	}
	
	/**
	 * registra el cheque de tercero manual..
	 */
	public static void registrarChequeTerceroManual(MyArray cheque, String user) throws Exception {
		MyPair banco = (MyPair) cheque.getPos1();
		String numero = (String) cheque.getPos2();
		Date fecha = (Date) cheque.getPos3();
		MyPair cliente = (MyPair) cheque.getPos4();		
		double importe = (double) cheque.getPos5();
		MyPair sucursal = (MyPair) cheque.getPos6();
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoChequeTercero ncheque = new BancoChequeTercero();
		ncheque.setBanco(rr.getTipoById(banco.getId()));
		ncheque.setCliente(rr.getClienteById(cliente.getId()));
		ncheque.setDepositado(false);
		ncheque.setDescontado(false);
		ncheque.setFecha(fecha);
		ncheque.setDiferido(!ncheque.isChequeAlDia(new Date()));
		ncheque.setLibrado(cliente.getText().toUpperCase());
		ncheque.setMoneda(rr.getMonedas().get(0));
		ncheque.setMonto(importe);
		ncheque.setNumero(numero);
		ncheque.setNumeroDeposito("");
		ncheque.setNumeroDescuento("");
		ncheque.setNumeroPlanilla("");
		ncheque.setNumeroRecibo("");
		ncheque.setNumeroVenta("");
		ncheque.setNumeroReembolso("");
		ncheque.setObservacion("cheque ingresado manualmente..");
		ncheque.setRechazado(false);
		ncheque.setSucursalApp(rr.getSucursalAppById(sucursal.getId()));
		ncheque.setVendedor("- - -");
		rr.saveObject(ncheque, user);
	}
	
	/**
	 * registra el movimiento cheque propio manual..
	 */
	public static void registrarChequePropioManual(BancoChequeDTO cheque,
			String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		BancoMovimiento bmov = new BancoMovimiento();
		bmov.setAnulado(false);
		bmov.setConciliado(false);
		bmov.setDescripcion(cheque.getBeneficiario());
		bmov.setFecha(cheque.getFechaVencimiento());
		bmov.setMonto(cheque.getMonto());
		bmov.setNroCuenta((BancoCta) rr.getObject(BancoCta.class.getName(), cheque.getBanco().getId()));
		bmov.setNroReferencia(cheque.getNumero() + "");
		bmov.setNumeroConciliacion("");
		bmov.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_EMISION_CHEQUE));
		rr.saveObject(bmov, user);

		BancoCheque cheque_ = (BancoCheque) new AssemblerBancoCheque().dtoToDomain(cheque);
		cheque_.setMovimiento(bmov);
		rr.saveObject(cheque_, user);
	}
	
	/**
	 * @return true si ya existe el banco deposito..
	 */
	public boolean isBancoDepositoDuplicada(String numero) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoDeposito(numero) != null;
	}
	
	/**
	 * setea el cheque propio como cobrado..
	 */
	public static void setChequeCobrado(long idCheque, boolean cobrado, Date fechaCobro, String user) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoCheque cheque = rr.getChequeById(idCheque);
		cheque.setCobrado(cobrado);
		cheque.setFechaCobro(fechaCobro);
		rr.saveObject(cheque, user);
	}
	
	

	/*********************************/

	/**
	 * Este método es sólo para cuando se quiere registrar un cheque que es
	 * realizado a mano y no se tiene ningún registro de pago. Los cheques que
	 * corresponden a pagos se registran automáticamente con el pago.
	 * 
	 * @param nroCuenta
	 * @param cheque
	 * @return
	 * @throws Exception
	 */
	public BancoMovimientoDTO crearBancoMovimientoChequePropio(
			MyPair nroCuenta, BancoChequeDTO cheque) throws Exception {

		AssemblerBancoMovimiento as = new AssemblerBancoMovimiento();

		BancoMovimientoDTO out = new BancoMovimientoDTO();
		out.setNroCuenta(nroCuenta);

		out.setFecha(cheque.getFechaVencimiento());
		out.setMonto(cheque.getMonto());

		// agregar estado comprobante y modo de creacion al cheque
		cheque.setEstadoComprobante(this.getUtilDto()
				.getEstadoComprobanteConfeccionado());
		cheque.setModoDeCreacion(this.getUtilDto().getChequeManual());

		as.setCheque(out, cheque);

		out.setChequeDto(cheque);
		out.setNroReferencia("- - -");
		out.setTipoMovimiento(this.getUtilDto().getTmBancoEmisionCheque());
		out.setDescripcion("CH Propio " + cheque.getNumero() + " p/"
				+ cheque.getBeneficiario());

		// en el cheque se setea el movimiento
		cheque.setMovimiento(out);

		return out;
	}

	/**
	 * Se usa para grabar los cheques que fueron actualizados (VER)
	 * 
	 * @param lbm
	 * @throws Exception
	 */
	public void grabarMovimientosSoloNuevos(List<BancoMovimientoDTO> lbm)
			throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		AssemblerBancoMovimiento assBm = new AssemblerBancoMovimiento();
		AssemblerBancoCheque assCh = new AssemblerBancoCheque();

		for (int i = 0; i < lbm.size(); i++) {
			BancoMovimientoDTO bm = lbm.get(i);

			if (bm.esNuevo() == true) {
				boolean tieneCheque = (bm.getChequeDto() != null);

				Domain d = null;
				if (tieneCheque == true) {
					// el cheque graba el movimiento
					BancoChequeDTO ch = bm.getChequeDto();
					d = assCh.dtoToDomain(ch);
				} else {
					d = assBm.dtoToDomain(bm);
				}

				rr.saveObject(d, this.getLoginNombre());
			}

		}

	}

	/**
	 * Registra la forma de pago y los datos extras, ej, cheques propios,
	 * terceros, pagos con tarjetas
	 * 
	 * @param rfpDto
	 * @throws Exception
	 */
	public void registrarMovimientoBanco(ReciboFormaPagoDTO rfpDto, Date fecha,
			MyPair sucursalApp, long idCliente, String planilla, String recibo, String venta, String vendedor) throws Exception {
		
		RegisterDomain rr = RegisterDomain.getInstance();

		// según la forma de pago ver que hacer

		long tipoFP = rfpDto.getTipo().getId();
		long cheqTer = this.getUtilDto().getFormaPagoChequeTercero().getId();
		long cheqProp = this.getUtilDto().getFormaPagoChequePropio().getId();
		long deposito = this.getUtilDto().getFormaPagoDepositoBancario().getId();
		long cheqTerAut = this.getUtilDto().getFormaPagoChequeAutoCobranza().getId();
		
		Date fechaPlanilla = rr.getFechaPlanilla(planilla);
		
		/*
		 * Efectivo: Esto ya está en la caja, no genera movimiento de banco. El
		 * movimiento se genera con la boleta de depósito.
		 */

		/*
		 * Pago con retencion: No genera movimiento de banco.
		 */

		/*
		 * Cheques de Tercero: Guardar los cheques en la tabla ChequesTercero,
		 * allí se registra para luego depositarlos y hacer todo el tratamiento
		 * (rechazados, etc) Luego hay que hacer algo que sea Boleta de
		 * Depósito, tanto para cheques como para efectivo de las cajas, y esa
		 * boleta de depósito va al libro de banco.
		 */

		if (tipoFP == cheqTer) {
			BancoChequeTercero bct = new BancoChequeTercero();

			bct.setFecha(rfpDto.getChequeFecha());
			bct.setBanco(rr.getTipoById(rfpDto.getChequeBanco().getId()));
			bct.setNumero(rfpDto.getChequeNumero());
			bct.setLibrado(rfpDto.getChequeLibrador());
			bct.setMoneda(rr.getTipoById(rfpDto.getMoneda().getId()));
			bct.setCliente(rr.getClienteById(idCliente));
			bct.setNumeroPlanilla(planilla + " (" + Utiles.getDateToString(fechaPlanilla, Utiles.DD_MM_YY) + ")");
			bct.setNumeroRecibo(recibo);
			bct.setNumeroVenta(venta);
			bct.setVendedor(vendedor);
			bct.setMonto(rfpDto.getMontoChequeGs());
			bct.setDepositado(false);
			bct.setSucursalApp(rr.getSucursalAppById(sucursalApp.getId()));
			bct.setObservacion("");
			bct.setAuxi("RFP " + rfpDto.getId());
			bct.setDiferido(rfpDto.isChequeAdelantado(fechaPlanilla));
			bct.setNumeroDeposito("");
			bct.setNumeroDescuento("");
			
			ReciboFormaPago rfp = (ReciboFormaPago) rr.getObject(ReciboFormaPago.class.getName(), rfpDto.getId());
			bct.setReciboFormaPago(rfp);

			rr.saveObject(bct, this.getLoginNombre());
		}
		
		if (tipoFP == cheqTerAut) {
			BancoChequeTercero bct = rr.getChequesTercero(rfpDto.getChequeNumero()).get(0);
			bct.setDescontado(true);
			bct.setObservacion("Descontado por AutoCobranza..");
			bct.setNumeroDescuento(recibo);
			rr.saveObject(bct, this.getLoginNombre());
		}

		/*
		 * Cheques propios. LLamar al asembler, que él solo se graba en el
		 * BancoMovimiento
		 */
		if (tipoFP == cheqProp) {
			
			BancoChequeDTO bchPro = new BancoChequeDTO();

			// enlaza el cheque con la forma de pago
			bchPro.setReciboFormaPago(rfpDto.toMyPair());
			bchPro.setNumero(Long.parseLong(rfpDto.getChequeNumero()));
			bchPro.setFechaEmision(new Date());
			bchPro.setFechaVencimiento(rfpDto.getChequeFecha());
			bchPro.setBeneficiario(rfpDto.getChequeLibrador());
			bchPro.setConcepto(rfpDto.getDescripcion());
			MyArray maMoneda = new MyArray();
			maMoneda.setId(rfpDto.getMoneda().getId());
			bchPro.setMoneda(maMoneda);
			bchPro.setMonto(rfpDto.getMontoGs());
			bchPro.setEstadoComprobante(this.getUtilDto().getEstadoComprobantePendiente());
			bchPro.setModoDeCreacion(this.getUtilDto().getChequeAutomatico());
			bchPro.setBanco((BancoCtaDTO) getDTOById(BancoCta.class.getName(), rfpDto
					.getDepositoBancoCta().getId(), new AssemblerBancoCtaCte()));
			bchPro.setNumeroCaja(planilla);
			bchPro.setNumeroOrdenPago(recibo);

			// banco movimiento
			BancoMovimientoDTO bm = new BancoMovimientoDTO();
			bm.setNroCuenta(rfpDto.getDepositoBancoCta().toMyPair());
			bm.setFecha(bchPro.getFechaVencimiento());
			bm.setMonto(bchPro.getMonto());
			bm.setNroReferencia(""+bchPro.getNumero());
			bm.setTipoMovimiento(this.getUtilDto().getTmBancoEmisionCheque());
			bm.setAuxi("RFP " + rfpDto.getId());
			String descripcion = "O.P. " + recibo.replace("REC-PAG-", "") + " - " + bchPro.getBeneficiario();
			bm.setDescripcion(descripcion);
			
			bchPro.setMovimiento(bm);
			
			AssemblerBancoCheque ass = new AssemblerBancoCheque();
			Domain d = ass.dtoToDomain(bchPro);
			rr.saveObject(d, this.getLoginNombre());
		}

		if (tipoFP == deposito) {			
			String descripcion = "DEP. BANCARIO SEGÚN RECIBO: " + recibo;
			BancoCta cuenta = (BancoCta) rr.getObject(BancoCta.class.getName(), 
					rfpDto.getDepositoBancoCta().getId());
			
			BancoMovimiento movim = new BancoMovimiento();
			movim.setAnulado(false);
			movim.setAuxi("RFP " + rfpDto.getId());
			movim.setConciliado(false);
			movim.setDescripcion(descripcion);
			movim.setFecha(fecha);
			movim.setMonto(rfpDto.getMontoGs());
			movim.setNroCuenta(cuenta);
			movim.setNroReferencia(rfpDto.getDepositoNroReferencia());
			movim.setNumeroConciliacion("");
			movim.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEPOSITO_BANCARIO));
			rr.saveObject(movim, this.getLoginNombre());
		}
	}
}

class AssemblerBancoTarjeta extends Assembler {

	@Override
	public Domain dtoToDomain(DTO dto) throws Exception {
		throw new Exception(
				"\n\n\n\nNo usar este método: AssemblerBancoTarjeta.dtoToDomain\n\n\n\n\n\n");
	}

	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		throw new Exception(
				"\n\n\n\nNo usar este método: AssemblerBancoTarjeta.domainToDto\n\n\n\n\n\n");
	}

	BancoTarjeta getBancoTarjeta(ReciboFormaPagoDTO rfpDto, Date fecha,
			MyPair sucursalApp, UtilDTO utilDto) throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();

		// Procesadora
		ProcesadoraTarjeta proDom = (ProcesadoraTarjeta) rr.getObject(
				ProcesadoraTarjeta.class.getName(), rfpDto
						.getTarjetaProcesadora().getId());

		// Sucursal
		SucursalApp sucDom = (SucursalApp) rr.getObject(
				SucursalApp.class.getName(), sucursalApp.getId());

		// ReciboFormaPago
		ReciboFormaPago recDom = (ReciboFormaPago) rr.getObject(
				ReciboFormaPago.class.getName(), rfpDto.getId());

		// Tarjeta Tipo
		Tipo tjTipoDom = this.myPairToTipo(rfpDto.getTarjetaTipo());

		BancoTarjeta bt = new BancoTarjeta();

		bt.setSucursalApp(sucDom);
		bt.setReciboFormaPago(recDom);
		bt.setTarjetaTipo(tjTipoDom);
		bt.setProcesadora(proDom);
		bt.setFecha(fecha);
		bt.setTarjetaNumero(rfpDto.getTarjetaNumero());
		bt.setComprobanteNumero(rfpDto.getTarjetaNumeroComprobante());
		bt.setCuotas(rfpDto.getTarjetaCuotas());

		double monto = 0;
		long moneda = rfpDto.getMoneda().getId();
		if (utilDto.getMonedaGuarani().getId() == moneda) {
			monto = rfpDto.getMontoGs();
		} else {
			monto = rfpDto.getMontoDs();
		}
		bt.setImporte(monto);
		bt.setSaldo(monto);

		return bt;

	}

}
