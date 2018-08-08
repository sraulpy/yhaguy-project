package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import bsh.This;

import com.coreweb.control.Control;
import com.coreweb.domain.Domain;
import com.coreweb.dto.Assembler;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.AutonumeroYhaguy;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;

public class ControlSubDiario extends Control {

	public ControlSubDiario(Assembler ass) {
		super(ass);
		// TODO Auto-generated constructor stub
	}

	private UtilDTO getUtilDto() {
		return (UtilDTO) getDtoUtil();
	}

	/* ******************************************************** */

	private double importeRetencion = 637780;
	private double porcentajeRetencion = 0.30; // 30% del iva

	private RegisterDomain rr = RegisterDomain.getInstance();

	/* ********************************************************* */

	private MyArray CUENTA_RETENCION_IVA = null;
	private MyArray CUENTA_IVA_COMPRAS = null;
	private MyArray CUENTA_CAJA_GASTOS = null;
	private MyArray CUENTA_CHEQUE_DIF_A_PAGAR = null;
	private MyArray CUENTA_MERCADERIA_GRAVADAS = null;
	private MyArray CUENTA_IVA_VENTAS = null;
	private MyArray CUENTA_COBRO_CHEQUE_A_DEPOSITAR = null;
	private MyArray CUENTA_COBRO_EFECTIVO = null;
	private MyArray CUENTA_COBRO_TARJETA_CREDITO = null;
	private MyArray CUENTA_COBRO_TARJETA_DEBITO = null;
	private MyArray CUENTA_COBRO_RETENCION = null;
	private MyArray CUENTA_COBRO_DEPOSITO_BANCARIO = null;

	/* ********************************************************* */

	private AssemblerSubDiario ass = new AssemblerSubDiario();
	private MyPair sucursal = null;

	private static String[] camposCuentaContable = { "codigo", "descripcion",
			"alias" };

	public MyArray getCuentaContableFuncionario(long id) throws Exception {
		String query = "select f.cuentaContable from Funcionario f where f.id = "
				+ id;
		CuentaContable cta = (CuentaContable) this.rr.hqlToObject(query);
		MyArray m = this.ass.createMyArray(cta, camposCuentaContable);
		return m;
	}

	public MyArray getCuentaContableProveedor(long id) throws Exception {
		String query = "select p.cuentaContable from Proveedor p where p.id = "
				+ id;
		CuentaContable cta = (CuentaContable) this.rr.hqlToObject(query);
		MyArray m = this.ass.createMyArray(cta, camposCuentaContable);
		return m;
	}

	private MyPair getSucursal() {
		if (this.sucursal == null) {
			Session s = Sessions.getCurrent();
			this.sucursal = ((AccesoDTO) s.getAttribute(Configuracion.ACCESO))
					.getSucursalOperativa();
		}
		return this.sucursal;
	}

	private boolean grabar = true;

	public void grabarSubDiarioDto(SubDiarioDTO dto) throws Exception {
		if (this.grabar == true) {
			this.saveDTOsimple(dto, this.ass);
		}
	}

	/**
	 * Crea el subdiario
	 * 
	 * @param descripcion
	 * @return
	 * @throws Exception
	 */
	private SubDiarioDTO getSubdiario(String descripcion) throws Exception {
		SubDiarioDTO dto = new SubDiarioDTO();
		dto.setSucursal(this.getSucursal());
		dto.setNumero(AutonumeroYhaguy.getNumeroSubDiario(this.getSucursal()));
		return dto;
	}

	/**
	 * Crea un subdiario detalle
	 * 
	 * @param debeHaber
	 * @param cuenta
	 * @param descripcion
	 * @param importe
	 * @return
	 */
	private SubDiarioDetalleDTO getDetalle(boolean debeHaber, MyArray cuenta,
			String descripcion, double importe) {
		SubDiarioDetalleDTO dto = new SubDiarioDetalleDTO();
		if (debeHaber == true) {
			dto.setTipo(Configuracion.CUENTA_DEBE_KEY);
		} else {
			dto.setTipo(Configuracion.CUENTA_HABER_KEY);
		}
		dto.setCuenta(cuenta);
		dto.setDescripcion(descripcion);
		dto.setImporte(importe);
		return dto;
	}

	/**
	 * Crea un subdiario detalle DEBE
	 * 
	 * @param cuenta
	 * @param descripcion
	 * @param importe
	 * @return
	 */
	private SubDiarioDetalleDTO getDebe(MyArray cuenta, String descripcion,
			double importe) {
		return getDetalle(true, cuenta, descripcion, importe);
	}

	/**
	 * Crea un subdiario detalle HABER
	 * 
	 * @param cuenta
	 * @param descripcion
	 * @param importe
	 * @return
	 */
	private SubDiarioDetalleDTO getHaber(MyArray cuenta, String descripcion,
			double importe) {
		return getDetalle(false, cuenta, descripcion, importe);
	}

	// ==========================================================
	// Asiento de pago
	/**
	 * 
	 * == 1 Subdiario con el pago * (D) Parner 1.650.000 * (H) Retención 45.000
	 * * (H) Cheque dif.a pagar 1.605.000 *
	 * 
	 * @param proveedor
	 * @param importe
	 * @throws Exception
	 */
	public SubDiarioDTO subDiarioPagoProveedor(String descripcion,
			MyArray cuentaProveedor, double importeTotal,
			double importeRetencion, double importeCheque,
			double importeEfectivo, double importeDepositoBanco,
			double importeCobroConRetencion) throws Exception {

		double totalesDesglosados = importeRetencion + importeCheque
				+ importeEfectivo + importeDepositoBanco
				+ importeCobroConRetencion;

		// control de importes
		if ((importeTotal > 0)
				&& (false == this.m.esIgual(importeTotal, totalesDesglosados))) {

			String msg = "importeTotal:"
					+ importeTotal
					+ (" importeCheque:" + importeCheque + " importeEfectivo:"
							+ importeEfectivo + " importeRetencion:"
							+ importeRetencion + " importeDepositosBanco:"
							+ importeDepositoBanco
							+ " importeCobroConRetencion:" + importeCobroConRetencion);
			throw new Exception("Los importe NO balancean:\n" + msg);
		}

		SubDiarioDTO dto = this.getSubdiario(descripcion);

		// proveedor
		SubDiarioDetalleDTO proveSDD = this.getDebe(cuentaProveedor,
				descripcion, importeTotal);
		dto.getDetalles().add(proveSDD);

		// saber si corresponde retención
		if (importeRetencion > 0) {
			SubDiarioDetalleDTO reteSDD = this.getHaber(
					this.getCUENTA_RETENCION_IVA(), descripcion,
					importeRetencion);
			dto.getDetalles().add(reteSDD);
		}
		if (importeCheque > 0) {
			SubDiarioDetalleDTO chequeSDD = this.getHaber(
					this.getCUENTA_CHEQUE_DIF_A_PAGAR(), descripcion,
					importeCheque);
			dto.getDetalles().add(chequeSDD);
		}
		if (importeEfectivo > 0) {
			SubDiarioDetalleDTO efectivoSDD = this.getHaber(
					this.getCUENTA_CAJA_GASTOS(), descripcion, importeEfectivo);
			dto.getDetalles().add(efectivoSDD);
		}
		if (importeDepositoBanco > 0) {
			SubDiarioDetalleDTO depositoSDD = this.getHaber(
					this.getCUENTA_COBRO_DEPOSITO_BANCARIO(), descripcion,
					importeDepositoBanco);
			dto.getDetalles().add(depositoSDD);
		}
		if (importeCobroConRetencion > 0) {
			SubDiarioDetalleDTO cobroConRetencionSDD = this.getHaber(
					this.getCUENTA_COBRO_RETENCION(), descripcion,
					importeCobroConRetencion);
			dto.getDetalles().add(cobroConRetencionSDD);
		}

		// grabar
		this.grabarSubDiarioDto(dto);
		return dto;
	}

	// ==========================================================
	// Reposición de Caja

	/**
	 * Reposicion de caja chica * (D) Caja Chica 1.000.000 * (H) Cheque Dife
	 * 1.000.000
	 * 
	 * @param descripcion
	 * @param importeTotal
	 * @throws Exception
	 */
	public void subDiarioReposicionCajaChicaConCheque(String descripcion,
			double importeTotal) throws Exception {

		SubDiarioDTO dto = this.getSubdiario(descripcion);

		// Caja chica
		SubDiarioDetalleDTO proveSDD = this.getDebe(
				this.getCUENTA_CAJA_GASTOS(), descripcion, importeTotal);
		dto.getDetalles().add(proveSDD);

		SubDiarioDetalleDTO chequeSDD = this.getHaber(
				this.getCUENTA_CHEQUE_DIF_A_PAGAR(), descripcion, importeTotal);
		dto.getDetalles().add(chequeSDD);

		// grabar
		this.grabarSubDiarioDto(dto);

	}

	// ==========================================================
	// Vale funcionario
	/**
	 * Vale funcionario * (D) Cuenta Anticipo Funcionario 1.000.000 * (H) Caja
	 * Chica 1.000.000
	 * 
	 * @param descripcion
	 * @param cuentaFuncionario
	 * @param importeTotal
	 * @throws Exception
	 */
	public SubDiarioDTO subDiarioValeFuncionarioEfectivo(String descripcion,
			MyArray cuentaFuncionario, double importeTotal) throws Exception {

		SubDiarioDTO dto = this.getSubdiario(descripcion);

		// Caja chica
		SubDiarioDetalleDTO funcSDD = this.getDebe(cuentaFuncionario,
				descripcion, importeTotal);
		dto.getDetalles().add(funcSDD);

		SubDiarioDetalleDTO cajaSDD = this.getHaber(
				this.getCUENTA_CAJA_GASTOS(), descripcion, importeTotal);
		dto.getDetalles().add(cajaSDD);

		// grabar
		this.grabarSubDiarioDto(dto);
		return dto;

	}

	// ==========================================================
	// Lista de gastos
	/**
	 * Lista de detalles de gastos: * Pos1: MyArray de Cuenta * Pos2: String
	 * descripcion * Pos3: double Importe * Pos4: double Iva *
	 * 
	 * @param detalles
	 * @throws Exception
	 */
	public SubDiarioDTO subDiarioListaGastos(String descripcion,
			List<MyArray> detalles) throws Exception {

		double totalCaja = 0;

		SubDiarioDTO dto = this.getSubdiario(descripcion);

		for (int i = 0; i < detalles.size(); i++) {
			MyArray det = detalles.get(i);
			MyArray cuenta = (MyArray) det.getPos1();
			String desc = (String) det.getPos2();
			double importe = (double) det.getPos3();
			double iva = (double) det.getPos4();

			totalCaja += (importe + iva);

			if (importe > 0) {
				SubDiarioDetalleDTO detSDD = this
						.getDebe(cuenta, desc, importe);
				dto.getDetalles().add(detSDD);
			}
			if (iva > 0) {
				SubDiarioDetalleDTO detSDD = this.getDebe(
						this.getCUENTA_IVA_COMPRAS(), desc, iva);
				dto.getDetalles().add(detSDD);
			}
		}

		SubDiarioDetalleDTO cajSDD = this.getHaber(
				this.getCUENTA_CAJA_GASTOS(), descripcion, totalCaja);
		dto.getDetalles().add(cajSDD);

		// grabar
		this.grabarSubDiarioDto(dto);
		return dto;
	}

	// ===========================================================
	// ===========================================================
	// ========== VENTA - COBRO ==================================
	// ===========================================================
	// ===========================================================
	// ===========================================================

	/**
	 * retorna la cuenta que corresponde con cada forma de pago
	 * 
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	private MyArray getCuentaFormaPago(MyPair tipo) throws Exception {

		MyArray out = null;
		long tipoId = tipo.getId();

		long chTer = this.getUtilDto().getFormaPagoChequeTercero().getId();
		long tjCre = this.getUtilDto().getFormaPagoTarjetaCredito().getId();
		long tjDeb = this.getUtilDto().getFormaPagoTarjetaDebito().getId();
		long efec = this.getUtilDto().getFormaPagoEfectivo().getId();
		long reten = this.getUtilDto().getFormaPagoRetencion().getId();

		if (tipoId == chTer) {
			out = this.getCUENTA_COBRO_CHEQUE_A_DEPOSITAR();
		} else if (tipoId == tjCre) {
			out = this.getCUENTA_COBRO_TARJETA_CREDITO();
		} else if (tipoId == tjDeb) {
			out = this.getCUENTA_COBRO_TARJETA_DEBITO();
		} else if (tipoId == efec) {
			out = this.getCUENTA_COBRO_EFECTIVO();
		} else if (tipoId == reten) {
			out = this.getCUENTA_COBRO_RETENCION();
		} else {
			throw new Exception(
					"Error (getCuentaFormaPago), no hay cuenta para el tipo: "
							+ tipo);
		}

		return out;
	}

	/**
	 * Dado un subdiario, y la lista de formas de pagos, carga todos los
	 * detalles de forma de pago.
	 * 
	 * @param dto
	 * @param descripcion
	 * @param cuentaCliente
	 * @param importeTotal
	 * @param formaDePago
	 * @throws Exception
	 */
	private void actualizaSubdiarioConFormaPago(SubDiarioDTO dto,
			String descripcion, MyArray cuentaCliente, double importeTotal,
			List<MyArray> formaDePago) throws Exception {

		double sumaPagos = 0;

		for (int i = 0; i < formaDePago.size(); i++) {
			MyArray fdp = formaDePago.get(i);
			MyPair tipoFDP = (MyPair) fdp.getPos1();
			String desc = (String) fdp.getPos2();
			double importe = (double) fdp.getPos3();

			sumaPagos += importe;

			if (importe > 0) {
				MyArray cuenta = this.getCuentaFormaPago(tipoFDP);
				SubDiarioDetalleDTO fdpSDD = this.getHaber(cuenta, descripcion,
						importe);
				dto.getDetalles().add(fdpSDD);
			}
		}

		if (false == this.m.esIgual(importeTotal, sumaPagos)) {
			throw new Exception(
					"La suma de las formas de pago no es igual al total del cobro");
		}

		// -- sacar del cliente
		SubDiarioDetalleDTO clieSDD = this.getHaber(cuentaCliente, descripcion,
				importeTotal);
		dto.getDetalles().add(clieSDD);
	}

	// ==========================================================
	// Venta y Cobro

	public void subDiarioVenta(String descripcion, MyArray cuentaCliente,
			double importeTotal, double importeGravado, double importeIva,
			double importeCosto, List<MyArray> formaDePago) throws Exception {

		// control de importes
		if (false == this.m
				.esIgual(importeTotal, (importeGravado + importeIva))) {
			String msg = "Error de importes\n";
			msg += " importeTotal:" + importeTotal;
			msg += " importeGravado:" + importeGravado;
			msg += " importeIva" + importeIva;
			throw new Exception(msg);
		}

		SubDiarioDTO dto = this.getSubdiario(descripcion);

		// ------------- VENTA ----------------------
		// Poner en la cta cte del cliente
		SubDiarioDetalleDTO cliSDD = this.getDebe(cuentaCliente, descripcion,
				importeTotal);
		dto.getDetalles().add(cliSDD);
		// ---------
		SubDiarioDetalleDTO merSDD = this.getHaber(
				this.getCUENTA_MERCADERIA_GRAVADAS(), descripcion,
				importeGravado);
		dto.getDetalles().add(merSDD);
		// ---------
		SubDiarioDetalleDTO ivaSDD = this.getHaber(this.getCUENTA_IVA_VENTAS(),
				descripcion, importeIva);
		dto.getDetalles().add(ivaSDD);
		// ---------
		// ------------- COBROS ----------------------
		this.actualizaSubdiarioConFormaPago(dto, descripcion, cuentaCliente,
				importeTotal, formaDePago);

		// grabar
		this.grabarSubDiarioDto(dto);

	}

	// --- solo cobro con recibo
	/**
	 * Subdiario de pago (Recibo)
	 * 
	 * @param descripcion
	 * @param cuentaCliente
	 * @param importeTotal
	 * @param formaDePago
	 * @throws Exception
	 */
	public void subDiarioCobro(String descripcion, MyArray cuentaCliente,
			double importeTotal, List<MyArray> formaDePago) throws Exception {

		SubDiarioDTO dto = this.getSubdiario(descripcion);

		this.actualizaSubdiarioConFormaPago(dto, descripcion, cuentaCliente,
				importeTotal, formaDePago);
		// grabar
		this.grabarSubDiarioDto(dto);

	}


	
	public void subDiarioDeposito(String descripcion, double importeEfectivo,
			double importeChequePropio, double importeChequeOtrosBancos, MyArray cuentaBanco) throws Exception {

		double importeTotal = 0;
		SubDiarioDTO dto = this.getSubdiario(descripcion);

		// Importe a depositar
		if (importeEfectivo > 0) {
			SubDiarioDetalleDTO depoSDD = this.getDebe(
					this.getCUENTA_COBRO_EFECTIVO(), descripcion, importeEfectivo);
			dto.getDetalles().add(depoSDD);	
			importeTotal += importeEfectivo;
		}
		// Importe a depositar
		if (importeChequePropio > 0) {
			SubDiarioDetalleDTO depoSDD = this.getDebe(
					this.getCUENTA_COBRO_CHEQUE_A_DEPOSITAR(), descripcion, importeChequePropio);
			dto.getDetalles().add(depoSDD);	
			importeTotal += importeChequePropio;

		}
		
		// Importe a depositar
		if (importeChequeOtrosBancos > 0) {
			SubDiarioDetalleDTO depoSDD = this.getDebe(
					this.getCUENTA_COBRO_CHEQUE_A_DEPOSITAR(), descripcion, importeChequeOtrosBancos);
			dto.getDetalles().add(depoSDD);	
			importeTotal += importeChequeOtrosBancos;
		}
		
		SubDiarioDetalleDTO bancoSDD = this.getHaber(cuentaBanco, descripcion, importeTotal);
		dto.getDetalles().add(bancoSDD);

		// grabar
		this.grabarSubDiarioDto(dto);

	}
	
	
	
	
	
	public boolean isGrabar() {
		return grabar;
	}

	public void setGrabar(boolean grabar) {
		this.grabar = grabar;
	}

	private MyArray getCUENTA_RETENCION_IVA() throws Exception {
		if (CUENTA_RETENCION_IVA == null) {
			CUENTA_RETENCION_IVA = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_RETENCION_IVA);
		}
		return CUENTA_RETENCION_IVA;
	}

	private MyArray getCUENTA_IVA_COMPRAS() throws Exception {
		if (CUENTA_IVA_COMPRAS == null) {
			CUENTA_IVA_COMPRAS = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_IVA_COMPRAS);
		}
		return CUENTA_IVA_COMPRAS;
	}

	private MyArray getCUENTA_CAJA_GASTOS() throws Exception {
		if (CUENTA_CAJA_GASTOS == null) {
			CUENTA_CAJA_GASTOS = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_CAJA_GASTOS);
		}
		return CUENTA_CAJA_GASTOS;
	}

	private MyArray getCUENTA_CHEQUE_DIF_A_PAGAR() throws Exception {
		if (CUENTA_CHEQUE_DIF_A_PAGAR == null) {
			CUENTA_CHEQUE_DIF_A_PAGAR = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_CHEQUE_DIF_A_PAGAR);
		}
		return CUENTA_CHEQUE_DIF_A_PAGAR;
	}

	private MyArray getCUENTA_MERCADERIA_GRAVADAS() throws Exception {
		if (CUENTA_MERCADERIA_GRAVADAS == null) {
			CUENTA_MERCADERIA_GRAVADAS = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_MERCADERIA_GRAVADAS);
		}
		return CUENTA_MERCADERIA_GRAVADAS;
	}

	private MyArray getCUENTA_IVA_VENTAS() throws Exception {
		if (CUENTA_IVA_VENTAS == null) {
			CUENTA_IVA_VENTAS = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_IVA_VENTAS);
		}
		return CUENTA_IVA_VENTAS;
	}

	private MyArray getCUENTA_COBRO_CHEQUE_A_DEPOSITAR() throws Exception {
		if (CUENTA_COBRO_CHEQUE_A_DEPOSITAR == null) {
			CUENTA_COBRO_CHEQUE_A_DEPOSITAR = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_COBRO_CHEQUE_A_DEPOSITAR);
		}
		return CUENTA_COBRO_CHEQUE_A_DEPOSITAR;
	}

	private MyArray getCUENTA_COBRO_EFECTIVO() throws Exception {
		if (CUENTA_COBRO_EFECTIVO == null) {
			CUENTA_COBRO_EFECTIVO = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_COBRO_EFECTIVO);
		}
		return CUENTA_COBRO_EFECTIVO;
	}

	private MyArray getCUENTA_COBRO_TARJETA_CREDITO() throws Exception {
		if (CUENTA_COBRO_TARJETA_CREDITO == null) {
			CUENTA_COBRO_TARJETA_CREDITO = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_COBRO_TARJETA_CREDITO);
		}
		return CUENTA_COBRO_TARJETA_CREDITO;
	}

	private MyArray getCUENTA_COBRO_TARJETA_DEBITO() throws Exception {
		if (CUENTA_COBRO_TARJETA_DEBITO == null) {
			CUENTA_COBRO_TARJETA_DEBITO = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_COBRO_TARJETA_DEBITO);
		}
		return CUENTA_COBRO_TARJETA_DEBITO;
	}

	private MyArray getCUENTA_COBRO_RETENCION() throws Exception {
		if (CUENTA_COBRO_RETENCION == null) {
			CUENTA_COBRO_RETENCION = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_COBRO_RETENCION);
		}
		return CUENTA_COBRO_RETENCION;
	}

	private MyArray getCUENTA_COBRO_DEPOSITO_BANCARIO() throws Exception {
		if (CUENTA_COBRO_DEPOSITO_BANCARIO == null) {
			CUENTA_COBRO_DEPOSITO_BANCARIO = rr
					.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_COBRO_DEPOSITO_BANCARIO);
		}
		return CUENTA_COBRO_DEPOSITO_BANCARIO;
	}

}
