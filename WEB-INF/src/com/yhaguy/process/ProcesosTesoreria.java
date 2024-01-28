package com.yhaguy.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.coreweb.domain.Tipo;
import com.coreweb.extras.csv.CSV;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AjusteCtaCte;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.BancoBoletaDeposito;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoChequeTercero;
import com.yhaguy.domain.BancoDebito;
import com.yhaguy.domain.BancoDescuentoCheque;
import com.yhaguy.domain.BancoMovimiento;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.CtaCteEmpresaMovimiento_2016;
import com.yhaguy.domain.CtaCteEmpresaMovimiento_2017;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.EmpresaCartera;
import com.yhaguy.domain.EmpresaRubro;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.OrdenCompra;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RecaudacionCentral;
import com.yhaguy.domain.Recibo;
import com.yhaguy.domain.ReciboDetalle;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Timbrado;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Venta;
import com.yhaguy.util.Utiles;

@SuppressWarnings("unchecked")
public class ProcesosTesoreria {
	
	static final String SRC_CLIENTES_PATRICIA = "./WEB-INF/docs/process/CLIENTES-PATRICIA.csv";
	static final String SRC_CLIENTES_CLAUDIA = "./WEB-INF/docs/process/CLIENTES-CLAUDIA.csv";
	static final String SRC_CLIENTES_VIVIANA = "./WEB-INF/docs/process/CLIENTES-VIVIANA.csv";
	static final String SRC_FUNCIONARIOS = "./WEB-INF/docs/procesos/FUNCIONARIOS.csv";
	static final String SRC_CARTERA = "./WEB-INF/docs/procesos/CARTERA_CLIENTES.csv";
	static final String SRC_SALDOS_CLIENTES_MRA = "./WEB-INF/docs/procesos/SALDOS_CLIENTES_MRA.csv";
	
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
		List<BancoChequeTercero> cheques = rr.getChequesTercero(desde_, hasta_, null, 0, null, null);
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
		List<BancoChequeTercero> cheques = rr.getChequesTercero(desde_, hasta_, null, 0, null, null);
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
	public static void setRubroFuncionarioClientes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } };
		String[][] det = { { "RAZONSOCIAL", CSV.STRING }, { "RUC", CSV.STRING } };
		int count = 0;
		
		EmpresaRubro rubroFuncionario = (EmpresaRubro) rr.getObject(EmpresaRubro.class.getName(), 18);
		
		CSV csv = new CSV(cab, det, SRC_FUNCIONARIOS);

		csv.start();
		while (csv.hashNext()) {	
			String razonSocial = csv.getDetalleString("RAZONSOCIAL");
			String ruc = csv.getDetalleString("RUC");
			String query = "select e from Empresa e where e.ruc = '" + ruc + "'";
			List<Empresa> list = rr.hql(query);
			if (list.size() == 1) {
				for (Empresa emp : list) {
					emp.setRubro(rubroFuncionario);
					rr.saveObject(emp, emp.getUsuarioMod());
					System.out.println(count + " - " + emp.getRazonSocial());
				}
			}			
			if (list.size() == 0) {
				System.err.println(count + " - " + ruc + "-" +  razonSocial);
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
	 * verificar proveedores duplicados..
	 */
	public static void unificarProveedor(long idProveedor1, long idProveedor2) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Proveedor p1 = (Proveedor) rr.getObject(Proveedor.class.getName(), idProveedor1);
		Proveedor p2 = (Proveedor) rr.getObject(Proveedor.class.getName(), idProveedor2);
		
		List<Gasto> gastos = rr.getGastos(idProveedor2);
		for (Gasto gasto : gastos) {
			gasto.setProveedor(p1);
			rr.saveObject(gasto, "depuracion_proveedores");
			System.out.println("gasto:" + gasto.getNumeroFactura());
		}
		
		List<OrdenPedidoGasto> ordenes = rr.getOrdenPedidoGastos(idProveedor2);
		for (OrdenPedidoGasto ord : ordenes) {
			ord.setProveedor(p1);
			rr.saveObject(ord, "depuracion_proveedores");
			System.out.println("orden_gasto:" + ord.getNumero());
		}
		
		Set<Timbrado> timbrados = p2.getTimbrados();
		p2.getTimbrados().removeAll(timbrados);
		rr.saveObject(p2, "");
		System.out.println("timbrados removidos");
		
		List<Recibo> pagos = rr.getPagos(idProveedor2);
		for (Recibo pago : pagos) {
			pago.setProveedor(p1);
			rr.saveObject(pago, "depuracion_proveedores");
			System.out.println("pago:" + pago.getNumero());
		}
		
		List<NotaCredito> notasCredito = rr.getNotasCreditos(idProveedor2);
		for (NotaCredito nc : notasCredito) {
			nc.setProveedor(p1);
			rr.saveObject(nc, "depuracion_proveedores");
			System.out.println("notacredito:" + nc.getNumero());
		}
		
		List<CompraLocalFactura> compras = rr.getCompraLocalFacturas(idProveedor2);
		for (CompraLocalFactura cm : compras) {
			cm.setProveedor(p1);
			rr.saveObject(cm, "depuracion_proveedores");
			System.out.println("compra_factura:" + cm.getNumero());
		}
		
		List<CompraLocalOrden> clordenes = rr.getCompraLocalOrdenes(idProveedor2);
		for (CompraLocalOrden cl : clordenes) {
			cl.setProveedor(p1);
			rr.saveObject(cl, "depuracion_proveedores");
			System.out.println("compra_orden:" + cl.getNumero());
		}
		
		List<OrdenCompra> ordencompras = rr.getOrdenesCompra(idProveedor2);
		for (OrdenCompra cl : ordencompras) {
			cl.setProveedor(p1);
			rr.saveObject(cl, "depuracion_proveedores");
			System.out.println("compra_orden:" + cl.getNumero());
		}
		
		List<Articulo> articulos = rr.getArticulos(idProveedor2);
		for (Articulo cl : articulos) {
			cl.setProveedor(p1);
			rr.saveObject(cl, "depuracion_proveedores");
			System.out.println("articulo:" + cl.getCodigoInterno());
		}
		
		List<CtaCteEmpresaMovimiento> ctactes = rr.getCtaCteMovimientos(p2.getIdEmpresa(), 99);
		for (CtaCteEmpresaMovimiento ctacte : ctactes) {
			ctacte.setIdEmpresa(p1.getIdEmpresa());
			rr.saveObject(ctacte, "depuracion_proveedores");
			System.out.println("ctacte:" + ctacte.getNroComprobante());
		}
		
		rr.deleteObject(p2);
		System.out.println("proveedor unificado");
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
		Date desde = Utiles.getFecha("01-10-2019 00:00:00");
		Date hasta = new Date();
		List<Recibo> recibos = rr.getCobranzas(desde, hasta, 2, 0, true, true, null);
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
		List<Venta> ventas = rr.getVentasContado(desde, hasta, 0, 0);
		for (Venta vta : ventas) {
			for (ReciboFormaPago rfp : vta.getFormasPago()) {
				if (rfp.isChequeTercero()) {
					BancoChequeTercero cheque = rr.getChequeTercero(rfp.getId());
					if (cheque == null) {
						cheque = new BancoChequeTercero();
						cheque.setAnulado(false);
						cheque.setAuxi("RFP " + rfp.getId());
						cheque.setBanco(rfp.getChequeBanco());
						cheque.setCliente(vta.getCliente());
						cheque.setDepositado(false);
						cheque.setDescontado(false);
						cheque.setFecha(rfp.getChequeFecha());
						cheque.setDiferido(rfp.isChequeAdelantado(vta.getFecha()));
						cheque.setLibrado(rfp.getChequeLibrador());
						cheque.setMoneda(vta.getMoneda());
						cheque.setMonto(rfp.getMontoChequeGs());
						cheque.setNumero(rfp.getChequeNumero());
						cheque.setNumeroDeposito("");
						cheque.setNumeroDescuento("");
						cheque.setNumeroReembolso("");
						cheque.setNumeroPlanilla(vta.getNumeroPlanillaCaja());
						cheque.setNumeroRecibo(vta.getNumero());
						cheque.setNumeroVenta("");
						cheque.setObservacion("");
						cheque.setRechazado(false);
						cheque.setReciboFormaPago(rfp);
						cheque.setSucursalApp(vta.getSucursal());
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
		List<Recibo> recibos = rr.getCobranzas(desde, hasta, 0, 0, true, true, null);
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
		List<Recibo> recs = rr.getCobranzas(desde, hasta, 0, 0, true, true, null);
		
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
		List<Recibo> recs = rr.getCobranzas(desde, hasta, 0, 0, true, true, null);
		
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
	
	/**
	 * depura los saldos por nota de credito..
	 */
	public static void depurarSaldosPorNotaCredito() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCredito> ncs = rr.getObjects(NotaCredito.class.getName());
		for (NotaCredito nc : ncs) {
			if (nc.isNotaCreditoVenta() && (!nc.isNotaCreditoVentaContado())) {
				double recs = 0;
				double ncrs = 0;
				Venta vta = nc.getVentaAplicada();
				CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(vta.getId(), vta.getTipoMovimiento().getSigla());
				List<Object[]> recs_ = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
				for (Object[] rec : recs_) {
					ReciboDetalle rdet = (ReciboDetalle) rec[1];
					recs += rdet.getMontoGs();
				}
				List<NotaCredito> ncrs_ = rr.getNotaCreditosByVenta(vta.getId());
				for (NotaCredito ncr : ncrs_) {
					if (!ncr.isAnulado()) {
						ncrs += ncr.getImporteGs();
					}				
				}
				if (ctacte != null && ctacte.getSaldo() > 0) {
					ctacte.setSaldo(vta.getTotalImporteGs() - (ncrs + recs));
					rr.saveObject(ctacte, ctacte.getUsuarioMod());
					System.out.println();
					System.out.println("-------- N.C.: " + nc.getNumero() + " - " + nc.getCliente().getRazonSocial() + " -------------");
					System.out.println("VENTA: " + Utiles.getNumberFormat(vta.getTotalImporteGs()) );
					System.out.println("N.CRE: " + Utiles.getNumberFormat(ncrs));
					System.out.println("RECIB: " + Utiles.getNumberFormat(recs));
					System.out.println("CT.CT: " + Utiles.getNumberFormat(ctacte.getSaldo()));
					System.out.println("V-N-R: " + Utiles.getNumberFormat(vta.getTotalImporteGs() - (ncrs + recs)));
					System.out.println("----------------------------------------------------");
				}
			}
		}
	}
	
	/**
	 * depura los saldos por nota de credito..
	 */
	public static void depurarSaldosNotaCredito(Date desde, Date hasta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCredito> ncs_ = rr.getNotasCreditoVenta(desde, hasta);
		for (NotaCredito ncr : ncs_) {
			if (ncr.isNotaCreditoVenta() && (!ncr.isNotaCreditoVentaContado())) {
				Venta vta = ncr.getVentaAplicada();
				CtaCteEmpresaMovimiento ctacteVta = rr.getCtaCteMovimientoByIdMovimiento(vta.getId(), vta.getTipoMovimiento().getSigla());
				//CtaCteEmpresaMovimiento ctacteNcr = rr.getCtaCteMovimientoByIdMovimiento(ncr.getId(), ncr.getTipoMovimiento().getSigla());
				
				double totalNCR = 0;
				double totalREC = 0;
				double totalCRE = 0;
				double totalDEB = 0;
				
				if (vta != null) {
					List<NotaCredito> ncs = rr.getNotaCreditosByVenta(vta.getId());
					for (NotaCredito nc : ncs) {
						if (!nc.isAnulado()) {
							totalNCR += nc.getImporteGs();
						}				
					}
					List<Object[]> recs = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
					for (Object[] rec : recs) {
						ReciboDetalle rdet = (ReciboDetalle) rec[1];
						totalREC += rdet.getMontoGs();
					}
					
					List<AjusteCtaCte> ajustes = rr.getAjustesCredito(vta.getId(), vta.getTipoMovimiento().getId());
					for (AjusteCtaCte ajuste : ajustes) {
						totalCRE += ajuste.getImporte();			
					}
					
					List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(vta.getId(), vta.getTipoMovimiento().getId());
					for (AjusteCtaCte ajuste : ajustes_) {
						totalDEB += ajuste.getImporte();	
					}
				}
				
				double aplicaciones = (vta.getImporteGs() + totalDEB) - (totalNCR + totalREC + totalCRE);
				
				System.out.println(ncr.getNumero() + " saldo: " + Utiles.getNumberFormat(ctacteVta.getSaldo()) + " aplic: " + Utiles.getNumberFormat(aplicaciones));				
				
			}
		}
	}
	
	public static void depurarSaldosVentasCredito(Date desde, Date hasta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> vtas = rr.getVentasCredito(desde, hasta, 0);
		for (Venta vta : vtas) {
				CtaCteEmpresaMovimiento ctacteVta = rr.getCtaCteMovimientoByIdMovimiento(vta.getId(), vta.getTipoMovimiento().getSigla());
				//CtaCteEmpresaMovimiento ctacteNcr = rr.getCtaCteMovimientoByIdMovimiento(ncr.getId(), ncr.getTipoMovimiento().getSigla());
				
				double totalNCR = 0;
				double totalREC = 0;
				double totalCRE = 0;
				double totalDEB = 0;
				
				if (vta != null) {
					List<NotaCredito> ncs = rr.getNotaCreditosByVenta(vta.getId());
					for (NotaCredito nc : ncs) {
						if (!nc.isAnulado()) {
							totalNCR += nc.getImporteGs();
						}				
					}
					List<Object[]> recs = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
					for (Object[] rec : recs) {
						ReciboDetalle rdet = (ReciboDetalle) rec[1];
						totalREC += rdet.getMontoGs();
					}
					
					List<AjusteCtaCte> ajustes = rr.getAjustesCredito(vta.getId(), vta.getTipoMovimiento().getId());
					for (AjusteCtaCte ajuste : ajustes) {
						totalCRE += ajuste.getImporte();			
					}
					
					List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(vta.getId(), vta.getTipoMovimiento().getId());
					for (AjusteCtaCte ajuste : ajustes_) {
						totalDEB += ajuste.getImporte();	
					}
				}
				
				double aplicaciones = (vta.getImporteGs() + totalDEB) - (totalNCR + totalREC + totalCRE);
				
				System.out.println(vta.getNumero() + " saldo: " + Utiles.getNumberFormat(ctacteVta.getSaldo()) + " aplic: " + Utiles.getNumberFormat(aplicaciones));				
				
			}		
	}

	
	/**
	 * depura los saldos por nota de credito..
	 */
	public static void depurarSaldosNotaCreditoExtracto() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCredito> ncs = rr.getObjects(NotaCredito.class.getName());
		for (NotaCredito nc : ncs) {
			if (nc.isNotaCreditoVenta() && (!nc.isNotaCreditoVentaContado())) {
				CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(nc.getId(), nc.getTipoMovimiento().getSigla(), nc.getCliente().getIdEmpresa());				
				if (ctacte == null) {
					CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
					ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA));
					ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
					ctm.setFechaEmision(nc.getFechaEmision());
					ctm.setFechaVencimiento(nc.getFechaEmision());
					ctm.setIdEmpresa(nc.getCliente().getIdEmpresa());
					ctm.setIdMovimientoOriginal(nc.getId());
					ctm.setIdVendedor(nc.getVendedor().getId());
					ctm.setImporteOriginal(nc.isMonedaLocal() ? nc.getImporteGs() : nc.getImporteDs());
					ctm.setSaldo(0);
					ctm.setMoneda(nc.getMoneda());
					ctm.setNroComprobante(nc.getNumero());
					ctm.setSucursal(nc.getSucursal());
					rr.saveObject(ctm, "sys");
					System.out.println(nc.getNumero() + " - " + nc.getCliente().getRazonSocial());
				}
			}
		}
	}
	
	/**
	 * depura los saldos por nota de credito..
	 */
	public static void depurarSaldosVentaCredito(Date desde, Date hasta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> vtas = rr.getVentasCredito(desde, hasta, 0);
		for (Venta vta : vtas) {
			if (vta.isMonedaLocal()) {
				double recs = 0;
				double ncrs = 0;
				CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(vta.getId(), vta.getTipoMovimiento().getSigla(), vta.getCliente().getIdEmpresa());
				List<Object[]> recs_ = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
				for (Object[] rec : recs_) {
					ReciboDetalle rdet = (ReciboDetalle) rec[1];
					recs += rdet.getMontoGs();
				}
				List<NotaCredito> ncrs_ = rr.getNotaCreditosByVenta(vta.getId());
				for (NotaCredito ncr : ncrs_) {
					if (!ncr.isAnulado()) {
						ncrs += ncr.getImporteGs();
					}				
				}
				if (ctacte != null && ctacte.getSaldo() > 0) {
					String ctct = Utiles.getNumberFormat(ctacte.getSaldo());
					String hist = Utiles.getNumberFormat(vta.getTotalImporteGs() - (ncrs + recs));
					if (!ctct.equals(hist)) {
						System.out.println();
						System.out.println("-------- FAC.NRO: " + vta.getNumero() + " - " + vta.getCliente().getRazonSocial() + " -------------");
						System.out.println("VENTA: " + Utiles.getNumberFormat(vta.getTotalImporteGs()) );
						System.out.println("N.CRE: " + Utiles.getNumberFormat(ncrs));
						System.out.println("RECIB: " + Utiles.getNumberFormat(recs));
						System.out.println("CT.CT: " + Utiles.getNumberFormat(ctacte.getSaldo()));
						System.out.println("V-N-R: " + Utiles.getNumberFormat(vta.getTotalImporteGs() - (ncrs + recs)));
						System.out.println("----------------------------------------------------");
					}				
				}
			}		
		}
	}
	
	/**
	 * depura los saldos por nota de credito..
	 */
	public static void depurarSaldosVentaCreditonNegativo(Date desde, Date hasta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> vtas = rr.getVentasCredito(desde, hasta, 0);
		for (Venta vta : vtas) {
			if (vta.isMonedaLocal()) {
				double recs = 0;
				double ncrs = 0;
				CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(vta.getId(), vta.getTipoMovimiento().getSigla(), vta.getCliente().getIdEmpresa());
				List<Object[]> recs_ = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
				for (Object[] rec : recs_) {
					ReciboDetalle rdet = (ReciboDetalle) rec[1];
					recs += rdet.getMontoGs();
				}
				List<NotaCredito> ncrs_ = rr.getNotaCreditosByVenta(vta.getId());
				for (NotaCredito ncr : ncrs_) {
					if (!ncr.isAnulado()) {
						ncrs += ncr.getImporteGs();
					}				
				}
				if (ctacte != null) {
					double hist_ = (vta.getTotalImporteGs() - (ncrs + recs));
					if (hist_ < 0) {
						ctacte.setSaldo(hist_);
						rr.saveObject(ctacte, ctacte.getUsuarioMod());
						System.out.println();
						System.out.println("-------- FAC.NRO: " + vta.getNumero() + " - " + vta.getCliente().getRazonSocial() + " -------------");
						System.out.println("VENTA: " + Utiles.getNumberFormat(vta.getTotalImporteGs()) );
						System.out.println("N.CRE: " + Utiles.getNumberFormat(ncrs));
						System.out.println("RECIB: " + Utiles.getNumberFormat(recs));
						System.out.println("CT.CT: " + Utiles.getNumberFormat(ctacte.getSaldo()));
						System.out.println("V-N-R: " + Utiles.getNumberFormat(vta.getTotalImporteGs() - (ncrs + recs)));
						System.out.println("----------------------------------------------------");
					}				
				}
			}		
		}
	}
	
	/**
	 * depura los saldos por caja..
	 */
	public static void depurarSaldosPorCaja(long idCaja) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CajaPeriodo caja = (CajaPeriodo) rr.getObject(CajaPeriodo.class.getName(), idCaja);
		for (Recibo recibo : caja.getRecibos()) {
			for (ReciboDetalle det : recibo.getDetalles()) {
				Venta vta = det.getVenta();
				if (vta != null) {
					double recs = 0;
					double ncrs = 0;
					List<Object[]> recs_ = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
					for (Object[] rec : recs_) {
						ReciboDetalle rdet = (ReciboDetalle) rec[1];
						recs += rdet.getMontoGs();
					}
					List<NotaCredito> ncrs_ = rr.getNotaCreditosByVenta(vta.getId());
					for (NotaCredito ncr : ncrs_) {
						if (!ncr.isAnulado()) {
							ncrs += ncr.getImporteGs();
						}
					}
					List<CtaCteEmpresaMovimiento> ctactes = rr.getCtaCteMovimientosByIdMovimiento(vta.getId(),
							vta.getTipoMovimiento().getSigla());
					for (CtaCteEmpresaMovimiento ctacte : ctactes) {
						ctacte.setSaldo(0);
						rr.saveObject(ctacte, ctacte.getUsuarioMod());
					}
					double hist = vta.getTotalImporteGs() - (ncrs + recs);
					CtaCteEmpresaMovimiento ctacte = ctactes.get(2);
					ctacte.setSaldo(hist);
					rr.saveObject(ctacte, ctacte.getUsuarioMod());
					System.out.println("DEPURADO: " + vta.getCliente().getRazonSocial() + " - " + vta.getNumero());				
				}
			}
		}
	}
	
	/**
	 * depura los saldos por caja..
	 */
	public static void depurarSaldosPorVenta(long idVenta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta vta = (Venta) rr.getObject(Venta.class.getName(), idVenta);
		if (vta != null) {
			double recs = 0;
			double ncrs = 0;
			double ajtc = 0;
			double ajtd = 0;
			List<Object[]> recs_ = rr.getRecibosByVenta(vta.getId(), vta.getTipoMovimiento().getId());
			for (Object[] rec : recs_) {
				ReciboDetalle rdet = (ReciboDetalle) rec[1];
				recs += rdet.getMontoGs();
			}
			List<NotaCredito> ncrs_ = rr.getNotaCreditosByVenta(vta.getId());
			for (NotaCredito ncr : ncrs_) {
				if (!ncr.isAnulado()) {
					ncrs += ncr.getImporteGs();
				}
			}
			List<AjusteCtaCte> ajustes = rr.getAjustesCredito(vta.getId(), vta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes) {
				ajtc += ajuste.getImporte();
			}
			
			List<AjusteCtaCte> ajustes_ = rr.getAjustesDebito(vta.getId(), vta.getTipoMovimiento().getId());
			for (AjusteCtaCte ajuste : ajustes_) {
				ajtd += ajuste.getImporte();
			}
			List<CtaCteEmpresaMovimiento> ctactes = rr.getCtaCteMovimientosByIdMovimiento(vta.getId(), vta.getTipoMovimiento().getSigla());
			for (CtaCteEmpresaMovimiento ctacte : ctactes) {
				ctacte.setSaldo(0);
				rr.saveObject(ctacte, ctacte.getUsuarioMod());
			}
			double hist = (vta.getTotalImporteGs() + ajtd) - (ncrs + recs + ajtc);
			CtaCteEmpresaMovimiento ctacte = ctactes.get(ctactes.size() - 1);
			ctacte.setSaldo(hist);
			rr.saveObject(ctacte, ctacte.getUsuarioMod());
			System.out.println("DEPURADO: " + vta.getCliente().getRazonSocial() + " - " + vta.getNumero());				
		}	
	}
	
	/**
	 * verifica los saldos por factura de venta..
	 */
	public static void verificarSaldosVentaCreditoExtracto() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCredito> ncs = rr.getObjects(NotaCredito.class.getName());
		for (NotaCredito nc : ncs) {
			if (nc.isNotaCreditoVenta() && (!nc.isNotaCreditoVentaContado())) {
				CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(nc.getId(), nc.getTipoMovimiento().getSigla(), nc.getCliente().getIdEmpresa());				
				if (ctacte == null) {
					CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
					ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA));
					ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
					ctm.setFechaEmision(nc.getFechaEmision());
					ctm.setFechaVencimiento(nc.getFechaEmision());
					ctm.setIdEmpresa(nc.getCliente().getIdEmpresa());
					ctm.setIdMovimientoOriginal(nc.getId());
					ctm.setIdVendedor(nc.getVendedor().getId());
					ctm.setImporteOriginal(nc.isMonedaLocal() ? nc.getImporteGs() : nc.getImporteDs());
					ctm.setSaldo(0);
					ctm.setMoneda(nc.getMoneda());
					ctm.setNroComprobante(nc.getNumero());
					ctm.setSucursal(nc.getSucursal());
					rr.saveObject(ctm, "sys");
					System.out.println(nc.getNumero() + " - " + nc.getCliente().getRazonSocial());
				}
			}
		}
	}
	
	/**
	 * asignacion de cartera de clientes..
	 */
	public static void asignacionDeCartera(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		String[][] cab = { { "Empresa", CSV.STRING } }; 
		String[][] det = { { "CLIENTE", CSV.STRING }, { "RUC", CSV.STRING }, { "CARTERA", CSV.STRING },
				{ "TELECOBRADOR", CSV.STRING } };
		
		List<EmpresaCartera> carteras = rr.getCarteras();
		Map<String, EmpresaCartera> map = new HashMap<String, EmpresaCartera>();
		for (EmpresaCartera item : carteras) {
			map.put(item.getDescripcion(), item);
		}
		List<Funcionario> cobradores = rr.getTeleCobradores();
		Map<String, Funcionario> mapCobs = new HashMap<String, Funcionario>();
		for (Funcionario item : cobradores) {
			mapCobs.put(item.getDescripcion(), item);
		}
		
		List<String> noEncontrado = new ArrayList<String>();
		
		CSV csv = new CSV(cab, det, src);

		csv.start();
		while (csv.hashNext()) {

			String cliente = csv.getDetalleString("CLIENTE");		
			String ruc = csv.getDetalleString("RUC");
			String cartera = csv.getDetalleString("CARTERA"); 
			String telecobrador = csv.getDetalleString("TELECOBRADOR"); 
			
			EmpresaCartera cart = map.get(cartera);
			Funcionario cob = mapCobs.get(telecobrador);
			Empresa emp = rr.getEmpresa(cliente, ruc);
			if (emp != null) {
				emp.setCartera(cart);
				rr.saveObject(emp, emp.getUsuarioMod());
				Cliente cli = rr.getClienteByEmpresa(emp.getId());
				if (cli != null) {
					cli.setCobrador(cob);
					rr.saveObject(cli, cli.getUsuarioMod());
				}				
				System.out.println(cliente);
			} else {
				noEncontrado.add(cliente);
			}
		}
		System.out.println("- - - - - - - - - ");
		for (String cliente : noEncontrado) {
			System.err.println(cliente);
		}
	}
	
	/**
	 * verifica los montos de boletas de deposito..
	 */
	public static void verificarBancoDepositos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoBoletaDeposito> list = rr.getObjects(BancoBoletaDeposito.class.getName());
		for (BancoBoletaDeposito dep : list) {
			dep.setTotalImporte_gs(dep.getTotalImporteGs());
			dep.setMonto(dep.getTotalImporteGs());
			rr.saveObject(dep, dep.getUsuarioMod());
			System.out.println(dep.getNumeroBoleta());
		}		
	}
	
	/**
	 * actualiza las cotizaciones de ventas
	 * [0]: fecha
	 * [1]: numero
	 * [2]: concepto
	 * [3]: cotizacion
	 */
	public static List<String[]> actualizarCotizacionesVentas(Date desde, Date hasta) throws Exception {
		List<String[]> out = new ArrayList<String[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> ventas = rr.getVentasDolares(desde, hasta);
		for (Venta venta : ventas) {
			Double tc = rr.getTipoCambioCompra(venta.getFecha(), -1);
			venta.setTipoCambio(tc);
			venta.recalcularCotizacion();
			rr.saveObject(venta, venta.getUsuarioMod());
			out.add(new String[] { Utiles.getDateToString(venta.getFecha(), Utiles.DD_MM_YYYY), venta.getNumero(),
					venta.getDescripcionTipoMovimiento(), Utiles.getNumberFormat(venta.getTipoCambio()) });
		}
		return out;
	}
	
	/**
	 * actualiza las cotizaciones de compras
	 * [0]: fecha
	 * [1]: numero
	 * [2]: concepto
	 * [3]: cotizacion
	 */
	public static List<String[]> actualizarCotizacionesCompras(Date desde, Date hasta) throws Exception {
		List<String[]> out = new ArrayList<String[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CompraLocalFactura> compras = rr.getComprasDolares(desde, hasta);
		for (CompraLocalFactura compra : compras) {
			Double tc = rr.getTipoCambioVenta(compra.getFechaOriginal(), -1);
			compra.setTipoCambio(tc);
			compra.recalcularCotizacion();
			rr.saveObject(compra, compra.getUsuarioMod());
			out.add(new String[] { Utiles.getDateToString(compra.getFechaOriginal(), Utiles.DD_MM_YYYY), compra.getNumero(),
					compra.getDescripcionTipoMovimiento(), Utiles.getNumberFormat(compra.getTipoCambio()) });
		}
		return out;
	}
	
	/**
	 * actualiza las cotizaciones de compras
	 * [0]: fecha
	 * [1]: numero
	 * [2]: concepto
	 * [3]: cotizacion
	 */
	public static List<String[]> actualizarCotizacionesGastos(Date desde, Date hasta) throws Exception {
		List<String[]> out = new ArrayList<String[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Gasto> gastos = rr.getGastosDolares(desde, hasta);
		for (Gasto gasto : gastos) {
			if (!gasto.isGastoImportacion()) {
				Double tc = rr.getTipoCambioVenta(gasto.getFecha(), -1);
				gasto.setTipoCambio(tc);
				gasto.recalcularCotizacion();
				rr.saveObject(gasto, gasto.getUsuarioMod());
				out.add(new String[] { Utiles.getDateToString(gasto.getFecha(), Utiles.DD_MM_YYYY), gasto.getNumeroFactura(),
						gasto.getDescripcionTipoMovimiento(), Utiles.getNumberFormat(gasto.getTipoCambio()) });
			}
		}
		return out;
	}
	
	/**
	 * actualiza las cotizaciones de notas de credito compras
	 * [0]: fecha
	 * [1]: numero
	 * [2]: concepto
	 * [3]: cotizacion
	 */
	public static List<String[]> actualizarCotizacionesNotasCreditoCompra(Date desde, Date hasta) throws Exception {
		List<String[]> out = new ArrayList<String[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCredito> ncs = rr.getNotasCreditoCompraDolares(desde, hasta);
		for (NotaCredito nc : ncs) {
			Double tc = rr.getTipoCambioVenta(nc.getFechaEmision(), -1);
			nc.setTipoCambio(tc);
			nc.recalcularCotizacion();
			rr.saveObject(nc, nc.getUsuarioMod());
			out.add(new String[] { Utiles.getDateToString(nc.getFechaEmision(), Utiles.DD_MM_YYYY), nc.getNumero(),
					nc.getTipoMovimiento().getDescripcion(), Utiles.getNumberFormat(nc.getTipoCambio()) });
		}
		return out;
	}
	
	/**
	 * actualiza las cotizaciones de notas de credito compras
	 * [0]: fecha
	 * [1]: numero
	 * [2]: concepto
	 * [3]: cotizacion
	 */
	public static List<String[]> actualizarCotizacionesNotasCreditoVenta(Date desde, Date hasta) throws Exception {
		List<String[]> out = new ArrayList<String[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCredito> ncs = rr.getNotasCreditoVentaDolares(desde, hasta);
		for (NotaCredito nc : ncs) {
			Double tc = rr.getTipoCambioCompra(nc.getFechaEmision(), -1);
			nc.setTipoCambio(tc);
			nc.recalcularCotizacion();
			rr.saveObject(nc, nc.getUsuarioMod());
			out.add(new String[] { Utiles.getDateToString(nc.getFechaEmision(), Utiles.DD_MM_YYYY), nc.getNumero(),
					nc.getTipoMovimiento().getDescripcion(), Utiles.getNumberFormat(nc.getTipoCambio()) });
		}
		return out;
	}
	
	/**
	 * verifica la cotizacion de gastos en ctacte..
	 */
	public static void verificarCotizacionGastos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Gasto> gastos = rr.getGastosDolares(Utiles.getFechaInicioOperaciones(), new Date());
		for (Gasto gasto : gastos) {
			if (!gasto.isGastoImportacion()) {
				CtaCteEmpresaMovimiento ctacte = rr.getCtaCteMovimientoByIdMovimiento(gasto.getId(),
						gasto.getTipoMovimiento().getSigla());
				if (ctacte != null) {
					if (ctacte.getTipoCambio() <= 0) {
						double tc = rr.getTipoCambioVenta(gasto.getFecha(), -1);
						ctacte.setTipoCambio(tc);
						gasto.setTipoCambio(tc);
						rr.saveObject(ctacte, "sys");
						rr.saveObject(gasto, "sys");
						System.out.println(gasto.getNumeroFactura() + " - " + gasto.getTipoCambio());
					}
				}
			}
		}
	}
	
	/**
	 * Proceso que migra los clientes de mra
	 */
	public static void migracionClientesMRA(String src) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		TipoMovimiento tmVentaCredito = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
		Tipo moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		Tipo caracter = rr.getTipoById(98);
		Tipo ciudad = rr.getTipoPorDescripcion("ASUNCION");
		Tipo pais = rr.getTipoPorDescripcion("Paraguay");
		EmpresaCartera cartera = rr.getCartera("CORRIENTE");
		EmpresaRubro rubro = rr.getRubro("CONSUMIDOR FINAL");
		SucursalApp suc = rr.getSucursalAppById(1);
		Funcionario vend = rr.getFuncionarioById(54);
		
		String[][] cab = { { "Empresa", CSV.STRING } }; 
		String[][] det = { { "RUC", CSV.STRING }, { "IMPORTE", CSV.STRING }, { "SALDO", CSV.STRING },
				{ "EMISION", CSV.STRING }, { "VENCIMIENTO", CSV.STRING }, { "VENDEDOR", CSV.STRING },
				{ "NROCOMPROBANTE", CSV.STRING }, { "DIRECCION", CSV.STRING }, { "TELEFONO", CSV.STRING } };
		
		List<String> noEncontrado = new ArrayList<String>();
		
		CSV csv = new CSV(cab, det, src);
		long idmov = -1;

		csv.start();
		while (csv.hashNext()) {		
			String ruc = csv.getDetalleString("RUC");
			String importe = csv.getDetalleString("IMPORTE");
			String saldo = csv.getDetalleString("SALDO");
			String emision = csv.getDetalleString("EMISION");
			String vencimiento = csv.getDetalleString("VENCIMIENTO");
			String vendedor = csv.getDetalleString("VENDEDOR");
			String nroComprobante = csv.getDetalleString("NROCOMPROBANTE");
			String direccion = csv.getDetalleString("DIRECCION");
			String telefono = csv.getDetalleString("TELEFONO");
			idmov --;
			
			Empresa emp = rr.getEmpresaByRuc(ruc);
			if (emp != null) {
				CtaCteEmpresaMovimiento cm = new CtaCteEmpresaMovimiento();
				cm.setAnulado(false);
				cm.setCarteraCliente(cartera);
				cm.setCliente(rr.getClienteByEmpresa(emp.getId()));
				cm.setFechaEmision(Utiles.getFecha(emision, Utiles.YYYY_MM_DD_HH_MM_SS));
				cm.setFechaVencimiento(Utiles.getFecha(vencimiento, Utiles.YYYY_MM_DD_HH_MM_SS));
				cm.setIdEmpresa(emp.getId());
				cm.setIdVendedor(0);
				cm.setImporteOriginal(Double.parseDouble(importe));
				cm.setMoneda(moneda);
				cm.setNroComprobante(nroComprobante);
				cm.setNumeroImportacion("");
				cm.setObservacion(vendedor);
				cm.setSaldo(Double.parseDouble(saldo));
				cm.setSucursal(suc);
				cm.setTipoCaracterMovimiento(caracter);
				cm.setTipoMovimiento(tmVentaCredito);
				cm.setIdMovimientoOriginal(idmov);
				rr.saveObject(cm, "sys");
				System.out.println("ADD CTM: " + ruc);
			} else {
				String rsocial = rr.getRazonSocialSET(ruc);
				if (!rsocial.isEmpty()) {
					emp = new Empresa();
					emp.setRuc(ruc);
					emp.setRazonSocial(rsocial);
					emp.setCartera(cartera);
					emp.setCi("");
					emp.setCiudad(ciudad);
					emp.setDireccion_(direccion);
					emp.setTelefono_(telefono);
					emp.setNombre(rsocial);
					emp.setObservacion("MIGRACION MRA");
					emp.setPais(pais);
					emp.setRubro(rubro);
					emp.setVendedor(vend);
					rr.saveObject(emp, "sys");
					Cliente cli = new Cliente();
					cli.setCartera(cartera.getDescripcion());
					cli.setEmpresa(emp);
					rr.saveObject(cli, "sys");
					
					CtaCteEmpresaMovimiento cm = new CtaCteEmpresaMovimiento();
					cm.setAnulado(false);
					cm.setCarteraCliente(cartera);
					cm.setCliente(rr.getClienteByEmpresa(emp.getId()));
					cm.setFechaEmision(Utiles.getFecha(emision, Utiles.DD_MM_YYYY));
					cm.setFechaVencimiento(Utiles.getFecha(vencimiento, Utiles.DD_MM_YYYY));
					cm.setIdEmpresa(emp.getId());
					cm.setIdVendedor(0);
					cm.setImporteOriginal(Double.parseDouble(importe));
					cm.setMoneda(moneda);
					cm.setNroComprobante(nroComprobante);
					cm.setNumeroImportacion("");
					cm.setObservacion(vendedor);
					cm.setSaldo(Double.parseDouble(saldo));
					cm.setSucursal(suc);
					cm.setTipoCaracterMovimiento(caracter);
					cm.setTipoMovimiento(tmVentaCredito);
					cm.setIdMovimientoOriginal(idmov);
					rr.saveObject(cm, "sys");
					System.out.println("ADD CLI: " + ruc);
				}
				noEncontrado.add(ruc);
			}
		}		
		System.out.println("- - - - - - - - - REMANENTES - - - - - - - - -");
		for (String cliente : noEncontrado) {
			System.err.println(cliente);
		}
	}
	
	/**
	 * verifica los gastos de caja chica..
	 */
	public static void verificarGastosCajaChica() throws Exception {
		Date desde = Utiles.getFecha("01-01-2020 00:00:00");
		Date hasta = new Date();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Gasto> gastos = rr.getGastos(desde, hasta);
		for (Gasto gasto : gastos) {
			if (gasto.isFondoFijo()) {
				if (gasto.getFormasPago().size() == 0) {
					//ReciboFormaPago fp = new ReciboFormaPago();
					//fp.setTipo(rr.getTipoById(83));
					//fp.setFechaOperacion(gasto.getFecha());
					//fp.setMoneda(gasto.getMoneda());
					//fp.setMontoGs(gasto.getImporteGs());
					//fp.setNroComprobanteAsociado(gasto.getNumeroFactura());
					//gasto.getFormasPago().add(fp);
					//rr.saveObject(gasto, gasto.getUsuarioMod());
					System.out.println("--- " + gasto.getNumeroFactura());
				}
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
			//ProcesosTesoreria.addChequeTerceros();
			//ProcesosTesoreria.setNumeroRecibos();
			//ProcesosTesoreria.setOrigenRecaudacionCentral();
			//ProcesosTesoreria.setEmisionChequesTerceros();
			//ProcesosTesoreria.poblarCtaCteSaldos2017();
			//ProcesosTesoreria.setRubroFuncionarioClientes();
			//ProcesosTesoreria.verificarRecibosAnulados();
			//ProcesosTesoreria.setCtaCteNumeroImportacion();
			//ProcesosTesoreria.setOrigenGastos();
			//ProcesosTesoreria.setDatosClientes();
			//ProcesosTesoreria.setFechaDescuentoChequesTerceros();
			//ProcesosTesoreria.depurarSaldosPorNotaCredito();
			//ProcesosTesoreria.depurarSaldosVentaCredito(Utiles.getFecha("10-10-2018 00:00:00"), new Date());
			//ProcesosTesoreria.depurarSaldosNotaCredito();
			//ProcesosTesoreria.depurarSaldosVentaCreditonNegativo(Utiles.getFecha("10-10-2018 00:00:00"), new Date());
			//ProcesosTesoreria.depurarSaldosNotaCreditoExtracto();
			//ProcesosTesoreria.depurarSaldosPorCaja(2362);
			//ProcesosTesoreria.depurarSaldosPorVenta(59103);
			//ProcesosTesoreria.asignacionDeCartera(SRC_CARTERA);
			//ProcesosTesoreria.verificarBancoDepositos();
			//ProcesosTesoreria.verificarCotizacionGastos();
			//ProcesosTesoreria.migracionClientesMRA(SRC_SALDOS_CLIENTES_MRA);
			//ProcesosTesoreria.verificarGastosCajaChica();
			//ProcesosTesoreria.unificarProveedor(265, 348);
			//ProcesosTesoreria.depurarSaldosNotaCredito(Utiles.getFecha("01-06-2021 00:00:00"), new Date());
			ProcesosTesoreria.depurarSaldosVentasCredito(Utiles.getFecha("01-12-2021 00:00:00"), new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
