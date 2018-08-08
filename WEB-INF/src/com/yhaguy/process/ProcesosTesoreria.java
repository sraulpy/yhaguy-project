package com.yhaguy.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.coreweb.domain.Tipo;
import com.coreweb.extras.csv.CSV;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoDebito;
import com.yhaguy.domain.BancoDescuentoCheque;
import com.yhaguy.domain.BancoMovimiento;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.CtaCteEmpresaMovimiento_2016;
import com.yhaguy.domain.CtaCteEmpresaMovimiento_2017;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.domain.RecaudacionCentral;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

@SuppressWarnings("unchecked")
public class ProcesosTesoreria {
	
	static final String SRC_CLIENTES_PATRICIA = "./WEB-INF/docs/process/CLIENTES-PATRICIA.csv";
	static final String SRC_CLIENTES_CLAUDIA = "./WEB-INF/docs/process/CLIENTES-CLAUDIA.csv";
	static final String SRC_CLIENTES_VIVIANA = "./WEB-INF/docs/process/CLIENTES-VIVIANA.csv";
	static final String SRC_FUNCIONARIOS = "./WEB-INF/docs/procesos/FUNCIONARIOS.csv";
	
	/**
	 * verificacion de facturas anuladas..
	 */
	public static void verificarVentasAnuladas() throws Exception {
		System.out.println("Verificando Facturas Anuladas..");
		String desde = "01-01-2016 00:00:00";
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date desde_ = formatter.parse(desde);
		Date hasta_ = new Date();

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> vtasAnuladas = rr.getVentasAnuladas(desde_, hasta_);

		for (Venta venta : vtasAnuladas) {
			CtaCteEmpresaMovimiento ctacte = rr
					.getCtaCteMovimientoByIdMovimiento(venta.getId(), venta
							.getTipoMovimiento().getSigla());
			if (ctacte == null) {
			} else if (ctacte.isAnulado() == false) {
				System.out.println(venta.getNumero() + " SALDO: " + ctacte.getSaldoFinal());
				ctacte.setSaldo(0);
				ctacte.setAnulado(true);
				rr.saveObject(ctacte, "sys");
			}
		}
	}
	
	/**
	 * verificacion de montos de cheques en ReciboFormaPago..
	 */
	public static void verificarMontoCheques() throws Exception {
		System.out.println("Verificando Montos de Cheques..");
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ReciboFormaPago> fps = rr.getObjects(ReciboFormaPago.class
				.getName());
		for (ReciboFormaPago fp : fps) {
			if (fp.isChequeTercero() && fp.getMontoChequeGs() == 0) {
				fp.setMontoChequeGs(fp.getMontoGs());
				rr.saveObject(fp, "sys");
				System.out.println("Corregido - Cheque: "
						+ fp.getChequeNumero() + " Monto: " + fp.getMontoGs());
			}
		}
	}
	
	/**
	 * verificacion de cheques de tercero..
	 */
	public static void verificarChequesTercero() throws Exception {
		System.out.println("Verificando Cheques de Terceros..");
		String desde = "01-01-2016 00:00:00";
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date desde_ = formatter.parse(desde);
		Date hasta_ = new Date();
		RegisterDomain rr = RegisterDomain.getInstance();
		Map<String, List<BancoChequeTercero>> duplicados = new HashMap<String, List<BancoChequeTercero>>();
		List<BancoChequeTercero> cheques = rr.getChequesTercero(desde_, hasta_, null, 0);
		for (BancoChequeTercero cheque : cheques) {
			List<BancoChequeTercero> chqs = rr.getChequesTercero(cheque.getNumero());
			if (chqs.size() > 1) {
				duplicados.put(cheque.getNumero(), chqs);
			}
		}
		for (String key : duplicados.keySet()) {
			List<BancoChequeTercero> chqs = duplicados.get(key);
			double acum = 0;
			for (BancoChequeTercero cheque : chqs) {
				acum += cheque.getMonto();
			}
			int index = 0;
			for (BancoChequeTercero cheque : chqs) {
				if (index == 0) {
					cheque.setMonto(acum);
				} else {
					cheque.setMonto(0);
					cheque.setAnulado(true);
					cheque.setDbEstado('D');
				}
				rr.saveObject(cheque, "sys");
				index ++;
			}
			System.out.println("CHEQUE CORREGIDO: " + key + " - " + acum);
		}
	}
	
	/**
	 * asigna los clientes a los cheques de terceros..
	 */
	public static void asignarClientesChequesTercero() throws Exception {
		System.out.println("Asignando Clientes a Cheques de Terceros..");
		String desde = "01-01-2015 00:00:00";
		String hasta = "30-12-2016 00:00:00";
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date desde_ = formatter.parse(desde);
		Date hasta_ = formatter.parse(hasta);
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoChequeTercero> cheques = rr.getChequesTercero(desde_, hasta_, null, 0);
		for (BancoChequeTercero cheque : cheques) {
			ReciboFormaPago fp = cheque.getReciboFormaPago();
			if (fp != null) {
				Recibo rec = rr.getRecibo(fp.getId());
				if (rec != null && cheque.getCliente() == null) {
					cheque.setCliente(rec.getCliente());
					rr.saveObject(cheque, "sys");
					System.out.println("Corregido: " + cheque.getNumero() + " - " + rec.getCliente().getRazonSocial());
				}				
			}					
		}		
		for (BancoChequeTercero cheque : cheques) {
			ReciboFormaPago fp = cheque.getReciboFormaPago();
			if (fp != null && cheque.getCliente() == null) {
				Venta vta = rr.getVenta(fp.getId());
				if (vta != null && cheque.getCliente() == null) {
					cheque.setCliente(vta.getCliente());
					rr.saveObject(cheque, "sys");
					System.out.println("Corregido: " + cheque.getNumero() + " - " + vta.getCliente().getRazonSocial());
				}				
			}					
		}		
	}
	
	/**
	 * setea los clientes que operan a credito..
	 */
	public static void setClienteCredito() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		Date hasta = Utiles.getFecha("13-09-2016 23:00:00");
		List<Venta> vtas = rr.getVentasCredito(desde, hasta, 0);
		for (Venta venta : vtas) {
			if (!venta.isAnulado()) {
				Cliente cli = venta.getCliente();
				if (!cli.isVentaCredito()) {
					cli.setVentaCredito(true);
					rr.saveObject(cli, "sys");
					System.out.println("CLIENTE A CREDITO: " + cli.getRazonSocial());
				}
			}			
		}
	}
	
	/**
	 * setea los nros de planillas en los cheques de terceros..
	 */
	public static void setPlanillaChequesDeTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CajaPeriodo> planillas = rr.getObjects(CajaPeriodo.class.getName());
		for (CajaPeriodo planilla : planillas) {
			for (ReciboFormaPago fp : planilla.getChequesTercero()) {
				BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
				if (cheque != null) {
					cheque.setNumeroPlanilla(planilla.getNumero() + " (" + Utiles.getDateToString(planilla.getApertura(), Utiles.DD_MM_YY) + ")");
					rr.saveObject(cheque, "process");
					System.out.println("Planilla: " + planilla.getNumero() + " - cheque: " + cheque.getNumero());
				}
			}
		}
	}
	
	/**
	 * setea los nros de recibos en los cheques de terceros..
	 */
	public static void setReciboChequesDeTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> recibos = rr.getObjects(Recibo.class.getName());
		for (Recibo recibo : recibos) {
			for (ReciboFormaPago fp : recibo.getFormasPago()) {
				BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
				if (cheque != null) {
					cheque.setNumeroRecibo(recibo.getNumero());
					rr.saveObject(cheque, "sys");
					System.out.println("Recibo: " + recibo.getNumero() + " - cheque: " + cheque.getNumero());
				}
			}
		}
	}
	
	/**
	 * setea los nros de ventas en los cheques de terceros..
	 */
	public static void setVentaChequesDeTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> ventas = rr.getObjects(Venta.class.getName());
		for (Venta venta : ventas) {
			for (ReciboFormaPago fp : venta.getFormasPago()) {
				BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
				if (cheque != null) {
					cheque.setNumeroVenta(venta.getNumero());
					rr.saveObject(cheque, "sys");
					System.out.println("Venta: " + venta.getNumero() + " - cheque: " + cheque.getNumero());
				}
			}
		}
	}
	
	/**
	 * setea los nros de depositos en los cheques de terceros..
	 */
	public static void setDepositoChequesDeTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoBoletaDeposito> depositos = rr.getObjects(BancoBoletaDeposito.class.getName());
		for (BancoBoletaDeposito deposito : depositos) {
			for (BancoChequeTercero cheque : deposito.getCheques()) {
				cheque.setNumeroDeposito(deposito.getNumeroBoleta());
				cheque.setDepositado(true);
				rr.saveObject(cheque, "sys");
				System.out.println("Deposito: " + deposito.getNumeroBoleta() + " - cheque: " + cheque.getNumero());										
			}
		}
	}
	
	/**
	 * setea los nros de descuentos en los cheques de terceros..
	 */
	public static void setDescuentoChequesDeTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> recibos = rr.getObjects(Recibo.class.getName());
		for (Recibo recibo : recibos) {
			for (ReciboFormaPago fp : recibo.getFormasPago()) {
				if (fp.isChequeTerceroAutocobranza()) {
					BancoChequeTercero cheque = rr.getChequesTercero(fp.getChequeNumero()).get(0);
					if (cheque != null) {
						cheque.setNumeroDescuento(recibo.getNumero());
						rr.saveObject(cheque, "sys");
						System.out.println("Recibo: " + recibo.getNumero()
								+ " - cheque: " + cheque.getNumero());
					}
				}				
			}
		}
	}
	
	/**
	 * setea los cheques en diferido o al dia..
	 */
	public static void setDiferidoChequesDeTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CajaPeriodo> planillas = rr.getObjects(CajaPeriodo.class.getName());
		for (CajaPeriodo planilla : planillas) {
			for (ReciboFormaPago rfp : planilla.getChequesTercero()) {
				BancoChequeTercero cheque = rr.getChequeTercero(rfp.getId());
				if (cheque != null) {
					cheque.setDiferido(rfp.isChequeAdelantado(planilla.getApertura()));
					rr.saveObject(cheque, "sys");
					System.out.println("Cheque: " + cheque.getNumero() + " - " + (cheque.isDiferido() ? "diferido" : "adelantado"));
				}
			}
		}	
	}
	
	/**
	 * setea a los cheques propios el banco..
	 */
	public static void setChequeBanco() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoCheque> cheques = rr.getObjects(BancoCheque.class.getName());
		for (BancoCheque cheque : cheques) {
			cheque.setBanco(rr.getBancosCta().get(1));
			rr.saveObject(cheque, "sys");
			System.out.println(cheque.getNumero() + " - " + cheque.getBanco().getBanco().getDescripcion());
		}
	}
	
	/**
	 * setea el origen de los movimientos de banco..
	 */
	public static void setOrigenBancoMovimientos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoMovimiento> movims = rr.getObjects(BancoMovimiento.class.getName());
		for (BancoMovimiento movim : movims) {
			if (movim.isDeposito()) {
				movim.setDescripcion("BOLETA DE DEPOSITO NRO. " + movim.getNroReferencia());
				rr.saveObject(movim, "sys");
				System.out.println("Boleta de Deposito Nro. " + movim.getNroReferencia());
			}
		}
		List<Recibo> pagos = rr.getObjects(Recibo.class.getName());
		for (Recibo pago : pagos) {
			if (!pago.isCobro()) {
				for (ReciboFormaPago fp : pago.getFormasPago()) {
					if (fp.isChequePropio()) {
						BancoMovimiento movim = rr.getBancoMovimiento(fp.getId());
						if (movim != null) {
							movim.setDescripcion("O.P. " + pago.getNumero().replace("REC-PAG-", "") + " - " + fp.getChequeLibrador());
							movim.setNroReferencia(fp.getChequeNumero());
							rr.saveObject(movim, "sys");
							System.out.println("Orden de Pago Nro. " + pago.getNumero());
						}
					}
				}
			}
		}
	}
	
	/**
	 * add movimiento bancos..
	 */
	public static void addMovimientoBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		Date hasta = new Date();
		List<BancoBoletaDeposito> depositos = rr.getBancoDepositos(desde, hasta, 1);
		for (BancoBoletaDeposito dep : depositos) {
			if (dep.isCerrado()) {
				BancoMovimiento movim = new BancoMovimiento();
				movim.setFecha(dep.getFecha());
				movim.setMonto(dep.getMonto());
				movim.setNroReferencia(dep.getNumeroBoleta());
				movim.setDescripcion("BOLETA DE DEPOSITO NRO. " + dep.getNumeroBoleta());
				movim.setNroCuenta(dep.getNroCuenta());
				movim.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEPOSITO_BANCARIO));
				rr.saveObject(movim, "sys");
				dep.setBancoMovimiento(movim);
				rr.saveObject(dep, "sys");
				System.out.println(movim.getDescripcion());
			}
		}
		
		List<BancoCheque> cheques = rr.getCheques(desde, hasta, 1);
		for (BancoCheque cheque : cheques) {
			if (!cheque.isAnulado()) {
				BancoMovimiento movim = new BancoMovimiento();
				movim.setFecha(cheque.getFechaEmision());
				movim.setMonto(cheque.getMonto());
				movim.setNroReferencia(cheque.getNumero() + "");
				movim.setDescripcion("O.P. ");
				movim.setNroCuenta(cheque.getBanco());
				movim.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_EMISION_CHEQUE));
				movim.setAuxi("RFP " + cheque.getReciboFormaPago().getId());
				rr.saveObject(movim, "sys");
				cheque.setMovimiento(movim);
				rr.saveObject(cheque, "sys");
				System.out.println(movim.getDescripcion());
			}
		}
	}
	
	/**
	 * add movimiento bancos..
	 */
	public static void addMovimientoBancoByBoletaDeposito(long idBoletaDeposito) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		BancoBoletaDeposito dep = (BancoBoletaDeposito) rr.getObject(BancoBoletaDeposito.class.getName(), idBoletaDeposito);
		if (dep.isCerrado()) {
			BancoMovimiento movim = new BancoMovimiento();
			movim.setFecha(dep.getFecha());
			movim.setMonto(dep.getMonto());
			movim.setNroReferencia(dep.getNumeroBoleta());
			movim.setDescripcion("BOLETA DE DEPOSITO NRO. " + dep.getNumeroBoleta());
			movim.setNroCuenta(dep.getNroCuenta());
			movim.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEPOSITO_BANCARIO));
			rr.saveObject(movim, "sys");
			dep.setBancoMovimiento(movim);
			rr.saveObject(dep, "sys");
			System.out.println(movim.getDescripcion());
		}
	}
	
	/**
	 * setear el nro del cheque..
	 */
	public static void setNumeroCheque() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoCheque> cheques = rr.getObjects(BancoCheque.class.getName());
		for (BancoCheque cheque : cheques) {
			cheque.setNumero(Long.parseLong(cheque.getReciboFormaPago().getChequeNumero()));
			rr.saveObject(cheque, "sys");
			System.out.println(cheque.getNumero());
		}
	}
	
	/**
	 * setea el origen de los cheques propios..
	 */
	public static void setOrigenChequesPropios() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CajaPeriodo> cajas = rr.getObjects(CajaPeriodo.class.getName());
		for (CajaPeriodo planilla : cajas) {
			for (Recibo pago : planilla.getRecibos()) {
				if (!pago.isCobro()) {
					for (ReciboFormaPago fp : pago.getFormasPago()) {
						if (fp.isChequePropio()) {
							BancoCheque cheque = rr.getChequePropio(fp.getId());
							cheque.setNumeroCaja(planilla.getNumero());
							cheque.setNumeroOrdenPago(pago.getNumero());
							rr.saveObject(cheque, "sys");
							System.out.println(cheque.getNumero() + " - " + pago.getNumero());
						}
					}
				}
			}
		}
	}
	
	/**
	 * add movimiento bancos..
	 */
	public static void addMovimientoDebitoBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoDebito> debitos = rr.getObjects(BancoDebito.class.getName());
		for (BancoDebito deb : debitos) {
			if (deb.isConfirmado()) {
				BancoMovimiento movim = new BancoMovimiento();
				movim.setFecha(deb.getFecha());
				movim.setMonto(deb.getImporte());
				movim.setNroReferencia(deb.getNumero());
				movim.setDescripcion(deb.getDescripcion());
				movim.setNroCuenta(deb.getCuenta());
				movim.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEBITO_BANCARIO));
				rr.saveObject(movim, "sys");
				System.out.println(movim.getDescripcion());
			}
		}
	}
	
	/**
	 * setea los vendedores a los cheques de tercero..
	 */
	public static void setVendedorChequesTercero() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoChequeTercero> cheques = rr.getObjects(BancoChequeTercero.class.getName());
		for (BancoChequeTercero cheque : cheques) {
			if (!cheque.getNumeroVenta().isEmpty()) {
				Venta venta = rr.getVenta(cheque.getNumeroVenta());
				cheque.setVendedor(venta.getVendedor().getRazonSocial());
				rr.saveObject(cheque, "process");
				System.out.println(cheque.getNumero() + " - " + cheque.getVendedor());
			
			} else if (!cheque.getNumeroRecibo().isEmpty()) {
				Recibo rec = rr.getRecibo(cheque.getNumeroRecibo());
				List<ReciboDetalle> list = new ArrayList<ReciboDetalle>();
				list.addAll(rec.getDetalles());
				Funcionario vendedor = (Funcionario) rr.getObject(Funcionario.class.getName(), list.get(0).getMovimiento().getIdVendedor());
				cheque.setVendedor(vendedor.getRazonSocial());
				rr.saveObject(cheque, "process");
				System.out.println(cheque.getNumero() + " - " + cheque.getVendedor());
			}
		}
	}

	/**
	 * asigna cobrador a los clientes..
	 */
	public static void setCobradorClientes(String src, long idFuncionario) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		Funcionario func = rr.getFuncionarioById(idFuncionario);
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "RUC", CSV.STRING } };
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {
			String ruc = csv.getDetalleString("RUC");			
			Cliente cli = rr.getClienteByRuc(ruc);
			if (cli != null) {
				cli.setCobrador(func);
				rr.saveObject(cli, "process");
				System.out.println(func.getRazonSocial() + " - " + cli.getRazonSocial());
			}
		}
	}
	
	/**
	 * asigna rubro a clientes que son funcionarios..
	 */
	public static void setRubroFuncionarioClientes(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		Set<Tipo> rubros = new HashSet<Tipo>();
		Tipo rubroFuncionario = rr.getTipoPorDescripcion("FUNCIONARIOS");
		rubros.add(rubroFuncionario);
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "CEDULA", CSV.STRING }, { "RAZONSOCIAL", CSV.STRING } };
		int count = 0;
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {	
			String cedula = csv.getDetalleString("CEDULA");
			String razonSocial = csv.getDetalleString("RAZONSOCIAL");
			String query = "select c from Cliente c where c.empresa.ruc like '%" + cedula + "%'";
			List<Cliente> list = rr.hql(query);
			for (Cliente cliente : list) {
				count ++;
				Empresa emp = cliente.getEmpresa();
				emp.setCi(cedula);
				emp.setRazonSocial(razonSocial);
				emp.setRubroEmpresas(rubros);
				rr.saveObject(emp, "process");
				System.out.println(count + " - " + cliente.getRazonSocial());
			}
			if (list.size() == 0) {
				count ++;
				System.err.println(count + " - " + cedula + "-" +  razonSocial);
			}
		}
	}
	
	/**
	 * setea el numero de caja a los recibos..
	 */
	public static void setNumeroPlanillaRecibos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CajaPeriodo> planillas = rr.getObjects(CajaPeriodo.class.getName());
		for (CajaPeriodo planilla : planillas) {
			for (Recibo rec : planilla.getRecibos()) {
				rec.setNumeroPlanilla(planilla.getNumero());
				rr.saveObject(rec, "process");
				System.out.println(planilla.getNumero() + " - " + rec.getNumero());
			}
		}
	}
	
	/**
	 * add movimiento bancos con forma pago deposito bancario..
	 */
	public static void addMovimientosBancoFormaPagoDepositoBancario() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> recibos = rr.getObjects(Recibo.class.getName());
		
		for (Recibo recibo : recibos) {
			for (ReciboFormaPago fpago : recibo.getFormasPago()) {
				if (fpago.isDepositoBancario()) {
					String descripcion = "DEP. BANCARIO SEGÚN RECIBO: " + recibo.getNumero();					
					BancoMovimiento movim = new BancoMovimiento();
					movim.setAnulado(false);
					movim.setAuxi("RFP " + fpago.getId());
					movim.setConciliado(false);
					movim.setDescripcion(descripcion);
					movim.setFecha(recibo.getFechaEmision());
					movim.setMonto(fpago.getMontoGs());
					movim.setNroCuenta(fpago.getDepositoBancoCta());
					movim.setNroReferencia(fpago.getDepositoNroReferencia());
					movim.setNumeroConciliacion("");
					movim.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEPOSITO_BANCARIO));
					rr.saveObject(movim, "process");
					System.out.println("REC. " + recibo.getNumero() + " - DEP. " + movim.getNroReferencia());
				}
			}
		}
	}
	
	/**
	 * verificar clientes duplicados..
	 */
	public static void chequearClientesDuplicados() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Cliente> clientes = rr.getObjects(Cliente.class.getName());
		System.out.println("VERIFICANDO CLIENTES DUPLICADOS.... (TOTAL CLIENTES:" + clientes.size() + " )");
		for (Cliente cliente : clientes) {
			if (!cliente.getRuc().equals("44444401-7")) {
				List<Cliente> cli = rr.getClientesByRuc(cliente.getRuc());
				if (cli.size() > 1) {
					System.out.println("DUPLICADO: " + cliente.getRuc() + " - ID: " + cliente.getEmpresa().getId());
				}
			}			
		}
	}
	
	/**
	 * agrega recaudaciones central..
	 */
	public static void addRecaudacionesCentral() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> recibos = rr.getObjects(Recibo.class.getName());

		for (Recibo recibo : recibos) {
			for (ReciboFormaPago fpago : recibo.getFormasPago()) {
				if (fpago.isRecaudacionCentral()) {
					if (rr.getRecaudacionCentral(recibo.getNumero()) == null) {
						RecaudacionCentral rcc = new RecaudacionCentral();
						rcc.setNumeroRecibo(recibo.getNumero());
						rcc.setImporteGs(fpago.getMontoGs());
						rcc.setSaldoGs(fpago.getMontoGs());
						rcc.setNumeroCheque("");
						rcc.setNumeroDeposito("");
						rr.saveObject(rcc, "process");
						System.out.println("AGREGADO: " + rcc.getNumeroRecibo());
					}					
				}
			}
		}
	}
	
	/**
	 * agrega cheques de terceros segun formas de Pago..
	 */
	public static void addChequeTerceros() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-03-2018 00:00:00");
		Date hasta = new Date();
		List<Recibo> recibos = rr.getCobranzas(desde, hasta, 2);
		for (Recibo recibo : recibos) {
			for (ReciboFormaPago rfp : recibo.getFormasPago()) {
				if (rfp.isChequeTercero()) {
					BancoChequeTercero cheque = rr.getChequeTercero(rfp.getId());
					if (cheque == null) {
						cheque = new BancoChequeTercero();
						cheque.setAnulado(false);
						cheque.setAuxi("RFP " + rfp.getId());
						cheque.setBanco(rfp.getChequeBanco());
						cheque.setCliente(recibo.getCliente());
						cheque.setDepositado(false);
						cheque.setDescontado(false);
						cheque.setFecha(rfp.getChequeFecha());
						cheque.setDiferido(rfp.isChequeAdelantado(recibo.getFechaEmision()));
						cheque.setLibrado(rfp.getChequeLibrador());
						cheque.setMoneda(recibo.getMoneda());
						cheque.setMonto(rfp.getMontoChequeGs());
						cheque.setNumero(rfp.getChequeNumero());
						cheque.setNumeroDeposito("");
						cheque.setNumeroDescuento("");
						cheque.setNumeroReembolso("");
						cheque.setNumeroPlanilla(recibo.getNumeroPlanilla());
						cheque.setNumeroRecibo(recibo.getNumero());
						cheque.setNumeroVenta("");
						cheque.setObservacion("");
						cheque.setRechazado(false);
						cheque.setReciboFormaPago(rfp);
						cheque.setSucursalApp(recibo.getSucursal());
						cheque.setVendedor("");
						rr.saveObject(cheque, "process");
						System.out.println("CHEQUE AGREGADO:" + cheque.getNumero());
					}
				}
			}
		}		
	}
	
	/**
	 * setea el nro de los recibos..
	 */
	public static void setNumeroRecibos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		Date hasta = new Date();
		List<Recibo> recibos = rr.getCobranzas(desde, hasta, 0);
		for (Recibo recibo : recibos) {
			if (recibo.isCobro()) {
				String numero = recibo.getNumero();
				long nro = Long.parseLong(numero.substring(numero.lastIndexOf("-") + 1, numero.length()));
				recibo.setNro(nro);
				rr.saveObject(recibo, "process");
				System.out.println(nro);
			}
		}		
	}
	
	/**
	 * setea el origen de las rccs..
	 */
	public static void setOrigenRecaudacionCentral() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoChequeTercero> cheques = rr.getObjects(BancoChequeTercero.class.getName());
		for (BancoChequeTercero cheque : cheques) {
			if (cheque.getRecaudacionesCentral().size() > 0) {
				for (RecaudacionCentral rcc : cheque.getRecaudacionesCentral()) {
					rcc.setNumeroCheque(cheque.getNumero());
					rcc.setNumeroDeposito(cheque.getNumeroDeposito());
					rr.saveObject(rcc, "process");
					System.out.println("Cheque nro. " + cheque.getNumero());
				}
			}
		}		
	}
	
	/**
	 * setea la fecha de emision de cheques de terceros..
	 */
	public static void setEmisionChequesTerceros() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CajaPeriodo> cajas = rr.getObjects(CajaPeriodo.class.getName());
		for (CajaPeriodo planilla : cajas) {
			for (Venta vta : planilla.getVentas()) {
				if ((!vta.isAnulado()) && vta.isVentaContado()) {
					for (ReciboFormaPago fp : vta.getFormasPago()) {
						if (fp.isChequeTercero()) {
							BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
							if (cheque != null) {
								cheque.setEmision(vta.getFecha());
								rr.saveObject(cheque, "process");
								System.out.println("venta:" + vta.getNumero());
							}							
						}
					}
				}
			}
			for (Recibo recibo : planilla.getRecibos()) {
				for (ReciboFormaPago fp : recibo.getFormasPago()) {
					if (fp.isChequeTercero()) {
						BancoChequeTercero cheque = rr.getChequeTercero(fp.getId());
						if (cheque != null) {
							cheque.setEmision(recibo.getFechaEmision());
							rr.saveObject(cheque, "process");
							System.out.println("recibo:" + recibo.getNumero());
						}						
					}
				}
			}
		}
	}
	
	/**
	 * pobla la tabla cta cte saldos 2016
	 */
	public static void poblarCtaCteSaldos2016() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		Date hasta = Utiles.getFecha("31-12-2016 23:59:00");
		
		List<Venta> vtas = rr.getVentasCredito(desde, hasta, 0);
		List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
		List<Recibo> recs = rr.getCobranzas(desde, hasta, 0);
		
		for (Venta vta : vtas) {
			if (!vta.isAnulado()) {
				CtaCteEmpresaMovimiento_2016 ctm = new CtaCteEmpresaMovimiento_2016();
				ctm.setTipoMovimiento(vta.getTipoMovimiento());
				ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
				ctm.setFechaEmision(vta.getFecha());
				ctm.setIdEmpresa(vta.getCliente().getIdEmpresa());
				ctm.setIdMovimientoOriginal(vta.getId());
				ctm.setIdVendedor(vta.getVendedor().getId());
				ctm.setImporteOriginal(vta.getTotalImporteGs());
				ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
				ctm.setNroComprobante(vta.getNumero());
				ctm.setSucursal(vta.getSucursal());
				ctm.setSaldo(vta.getTotalImporteGs());
				rr.saveObject(ctm, "process");
				System.out.println(vta.getNumero());
			}			
		}
		
		for (NotaCredito nc : ncs) {
			if (!nc.isAnulado()) {
				Venta vta = nc.getVentaAplicada();
				if (!vta.isVentaContado()) {
					String query = "select c from CtaCteEmpresaMovimiento_2016 c where c.idMovimientoOriginal = " + vta.getId()
							+ " and c.tipoMovimiento.id = " + vta.getTipoMovimiento().getId();	
					List<CtaCteEmpresaMovimiento_2016> list = rr.hql(query);
					if (list.size() > 0) {
						CtaCteEmpresaMovimiento_2016 ctm = list.get(0);	
						ctm.setSaldo(ctm.getSaldo() - nc.getImporteGs());
						rr.saveObject(ctm, "process");
						System.out.println("notacred: " + nc.getNumero());
					}
				}
			}
		}
		
		for (Recibo recibo : recs) {
			for (ReciboDetalle det : recibo.getDetalles()) {
				Venta vta = det.getVenta();
				if (vta != null) {
					String query = "select c from CtaCteEmpresaMovimiento_2016 c where c.idMovimientoOriginal = "
							+ vta.getId() + " and c.tipoMovimiento.id = " + vta.getTipoMovimiento().getId();
					List<CtaCteEmpresaMovimiento_2016> list = rr.hql(query);
					if (list.size() > 0) {
						CtaCteEmpresaMovimiento_2016 ctm = list.get(0);
						ctm.setSaldo(ctm.getSaldo() - det.getMontoGs());
						rr.saveObject(ctm, "process");
						System.out.println("recibo: " + recibo.getNumero());
					}
				}
			}
		}
	}
	
	/**
	 * pobla la tabla cta cte saldos 2017
	 */
	public static void poblarCtaCteSaldos2017() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Date desde = Utiles.getFecha("01-01-2017 00:00:00");
		Date hasta = Utiles.getFecha("31-12-2017 23:59:00");
		
		List<Venta> vtas = rr.getVentasCredito(desde, hasta, 0);
		List<NotaCredito> ncs = rr.getNotasCreditoVenta(desde, hasta, 0);
		List<Recibo> recs = rr.getCobranzas(desde, hasta, 0);
		
		for (Venta vta : vtas) {
			if (!vta.isAnulado()) {
				CtaCteEmpresaMovimiento_2017 ctm = new CtaCteEmpresaMovimiento_2017();
				ctm.setTipoMovimiento(vta.getTipoMovimiento());
				ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
				ctm.setFechaEmision(vta.getFecha());
				ctm.setIdEmpresa(vta.getCliente().getIdEmpresa());
				ctm.setIdMovimientoOriginal(vta.getId());
				ctm.setIdVendedor(vta.getVendedor().getId());
				ctm.setImporteOriginal(vta.getTotalImporteGs());
				ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
				ctm.setNroComprobante(vta.getNumero());
				ctm.setSucursal(vta.getSucursal());
				ctm.setSaldo(vta.getTotalImporteGs());
				rr.saveObject(ctm, "process");
				System.out.println(vta.getNumero());
			}			
		}
		
		for (NotaCredito nc : ncs) {
			if (!nc.isAnulado()) {
				Venta vta = nc.getVentaAplicada();
				if (!vta.isVentaContado()) {
					String query = "select c from CtaCteEmpresaMovimiento_2017 c where c.idMovimientoOriginal = " + vta.getId()
							+ " and c.tipoMovimiento.id = " + vta.getTipoMovimiento().getId();	
					List<CtaCteEmpresaMovimiento_2017> list = rr.hql(query);
					if (list.size() > 0) {
						CtaCteEmpresaMovimiento_2017 ctm = list.get(0);	
						ctm.setSaldo(ctm.getSaldo() - nc.getImporteGs());
						rr.saveObject(ctm, "process");
						System.out.println("notacred: " + nc.getNumero());
					}					
				}
			}
		}
		
		for (Recibo recibo : recs) {
			for (ReciboDetalle det : recibo.getDetalles()) {
				Venta vta = det.getVenta();
				if (vta != null) {
					String query = "select c from CtaCteEmpresaMovimiento_2017 c where c.idMovimientoOriginal = "
							+ vta.getId() + " and c.tipoMovimiento.id = " + vta.getTipoMovimiento().getId();
					List<CtaCteEmpresaMovimiento_2017> list = rr.hql(query);
					if (list.size() > 0) {
						CtaCteEmpresaMovimiento_2017 ctm = list.get(0);
						ctm.setSaldo(ctm.getSaldo() - det.getMontoGs());
						rr.saveObject(ctm, "process");
						System.out.println("recibo: " + recibo.getNumero());
					}
				}
			}
		}
	}
	
	/**
	 * verifica recibos anulados..
	 */
	public static void verificarRecibosAnulados() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> recs = rr.getObjects(Recibo.class.getName());
		for (Recibo recibo : recs) {
			if (recibo.getTotalImporteGs() == 0) {
				recibo.setEstadoComprobante(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO));
				rr.saveObject(recibo, "process");
				System.out.println("RECIBO ANULADO: " + recibo.getNumero());
			}
		}
	}
	
	/**
	 * asigna nro importacion a cta cte..
	 */
	public static void setCtaCteNumeroImportacion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Gasto> gastos = rr.getObjects(Gasto.class.getName());
		for (Gasto gasto : gastos) {
			if (gasto.isGastoImportacion()) {
				CtaCteEmpresaMovimiento movim = rr.getCtaCteMovimientoByIdMovimiento(gasto.getId(), gasto.getTipoMovimiento().getSigla());
				movim.setNumeroImportacion(gasto.getNumeroImportacion());
				rr.saveObject(movim, "process");
				System.out.println(movim.getNumeroImportacion());
			}
		}
	}
	
	/**
	 * setea el nro de planilla y pago de los gastos..
	 */
	public static void setOrigenGastos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> pagos = rr.getObjects(Recibo.class.getName());
		for (Recibo pago : pagos) {
			if (pago.isPago()) {
				for (ReciboDetalle det : pago.getDetalles()) {
					Gasto gasto = det.getGasto();
					if (gasto != null) {
						gasto.setNumeroOrdenPago(pago.getNumero());
						gasto.setCajaPagoNumero(pago.getNumeroPlanilla());
						rr.saveObject(gasto, "process");
						System.out.println(gasto.getNumeroFactura() + " - " + pago.getNumero());
					}
				}
			}
		}
		
		List<CajaPeriodo> cajas = rr.getObjects(CajaPeriodo.class.getName());
		for (CajaPeriodo planilla : cajas) {
			for (Gasto gasto : planilla.getGastos()) {
				gasto.setCajaPagoNumero(planilla.getNumero());
				rr.saveObject(gasto, "process");
				System.out.println(gasto.getNumeroFactura() + " - " + planilla.getNumero());
			}
		}
		
		List<OrdenPedidoGasto> peds = rr.getObjects(OrdenPedidoGasto.class.getName());
		for (OrdenPedidoGasto ped : peds) {
			for (Gasto gasto : ped.getGastos()) {
				gasto.setObservacion(ped.getDescripcion());
				rr.saveObject(gasto, "process");
				System.out.println(gasto.getNumeroFactura() + " - " + ped.getDescripcion());
			}
		}
	}
	
	/**
	 * setea los datos de clientes..
	 */
	public static void setDatosClientes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Cliente> clientes = rr.getClientes();
		for (Cliente cliente : clientes) {
			cliente.getEmpresa().setDireccion_(cliente.getEmpresa().getDireccion());
			cliente.getEmpresa().setTelefono_(cliente.getEmpresa().getTelefono());
			cliente.getEmpresa().setCorreo_(cliente.getEmpresa().getCorreo());
			rr.saveObject(cliente, "process");
			System.out.println(cliente.getRazonSocial() + " - " + cliente.getEmpresa().getDireccion_());
		}
	}
	
	/**
	 * setea las fechas de deposito y descuento de cheques de terceros..
	 */
	public static void setFechaDescuentoChequesTerceros() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Recibo> recibos = rr.getObjects(Recibo.class.getName());
		for (Recibo recibo : recibos) {
			for (ReciboFormaPago fp : recibo.getFormasPago()) {
				if (fp.isChequeTerceroAutocobranza()) {
					BancoChequeTercero cheque = rr.getChequesTercero(fp.getChequeNumero()).get(0);
					if (cheque != null) {
						cheque.setFechaDescuento(recibo.getFechaEmision());
						rr.saveObject(cheque, "sys");
						System.out.println("Recibo: " + recibo.getNumero() + " - cheque: " + cheque.getNumero());
					}
				}				
			}
		}
		
		List<BancoDescuentoCheque> descuentos = rr.getObjects(BancoDescuentoCheque.class.getName());
		for (BancoDescuentoCheque desc : descuentos) {
			for (BancoChequeTercero cheque : desc.getCheques()) {
				cheque.setFechaDescuento(desc.getFecha());
				rr.saveObject(cheque, "sys");
				System.out.println("Descuento: " + desc.getId() + " - cheque: " + cheque.getNumero());
			}
		}
		
		List<BancoBoletaDeposito> depositos = rr.getObjects(BancoBoletaDeposito.class.getName());
		for (BancoBoletaDeposito dep : depositos) {
			for (BancoChequeTercero cheque : dep.getCheques()) {
				cheque.setFechaDeposito(dep.getFecha());
				rr.saveObject(cheque, "sys");
				System.out.println("Deposito: " + dep.getNumeroBoleta() + " - cheque: " + cheque.getNumero());
			}
		}
	
	}
	
	public static void main(String[] args) {
		try {
			//ProcesosTesoreria.verificarVentasAnuladas();
			//ProcesosTesoreria.verificarChequesTercero();
			//ProcesosTesoreria.asignarClientesChequesTercero();
			//ProcesosTesoreria.setClienteCredito();
			//ProcesosTesoreria.setPlanillaChequesDeTercero();
			//ProcesosTesoreria.setReciboChequesDeTercero();
			//ProcesosTesoreria.setVentaChequesDeTercero();
			//ProcesosTesoreria.setDepositoChequesDeTercero();
			//ProcesosTesoreria.setDescuentoChequesDeTercero();
			//ProcesosTesoreria.setDiferidoChequesDeTercero();
			//ProcesosTesoreria.setOrigenBancoMovimientos();
			//ProcesosTesoreria.setChequeBanco();
			//ProcesosTesoreria.addMovimientoBancos();
			//ProcesosTesoreria.setNumeroCheque();
			//ProcesosTesoreria.setOrigenChequesPropios();
			//ProcesosTesoreria.addMovimientoDebitoBancos();
			//ProcesosTesoreria.setCobradorClientes(SRC_CLIENTES_PATRICIA, 74);
			//ProcesosTesoreria.setVendedorChequesTercero();
			//ProcesosTesoreria.setNumeroPlanillaRecibos();
			//ProcesosTesoreria.addMovimientosBancoFormaPagoDepositoBancario();
			//ProcesosTesoreria.chequearClientesDuplicados();
			//ProcesosTesoreria.addRecaudacionesCentral();
			ProcesosTesoreria.addChequeTerceros();
			//ProcesosTesoreria.setNumeroRecibos();
			//ProcesosTesoreria.setOrigenRecaudacionCentral();
			//ProcesosTesoreria.setEmisionChequesTerceros();
			//ProcesosTesoreria.poblarCtaCteSaldos2017();
			//ProcesosTesoreria.setRubroFuncionarioClientes(SRC_FUNCIONARIOS);
			//ProcesosTesoreria.verificarRecibosAnulados();
			//ProcesosTesoreria.setCtaCteNumeroImportacion();
			//ProcesosTesoreria.setOrigenGastos();
			//ProcesosTesoreria.setDatosClientes();
			//ProcesosTesoreria.setFechaDescuentoChequesTerceros();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
