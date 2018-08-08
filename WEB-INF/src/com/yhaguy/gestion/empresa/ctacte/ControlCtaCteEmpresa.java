package com.yhaguy.gestion.empresa.ctacte;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.coreweb.control.Control;
import com.coreweb.domain.IiD;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.Assembler;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.CtaCteImputacion;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.CtaCteVisCliente;
import com.yhaguy.domain.CtaCteVisProveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.gestion.caja.recibos.ReciboDetalleDTO;
import com.yhaguy.gestion.notacredito.NotaCreditoDetalleDTO;

public class ControlCtaCteEmpresa extends Control {

	double tipoCambio = 1;
	RegisterDomain rr = RegisterDomain.getInstance();
	AssemblerCtaCteEmpresaMovimiento movimientoAss = new AssemblerCtaCteEmpresaMovimiento();
	AssemblerCtaCteImputacion imputacionAss = new AssemblerCtaCteImputacion();

	public ControlCtaCteEmpresa(Assembler ass) {
		super(ass);
		// TODO Auto-generated constructor stub
	}

	private UtilDTO getUtilDto() {
		return (UtilDTO) getDtoUtil();
	}

	// ************************************************************************************************************//

	/**
	 * Obtiene el estado de la cuenta corriente de una empresa en caracter de
	 * Proveedor (Si Activo, Inactivo, Bloqueado o Sin Cta. Cte).
	 * 
	 * @param empresa
	 *            Empresa de la cual se quiere conocer su estado.
	 * @return MyPair con la informacion del estado de la empresa. Retorna
	 *         "Sin Cta. Cte" en el caso de que la empresa no posea una cuenta o
	 *         posea como valor de estado "null".
	 * @throws Exception
	 * 
	 */
	public MyPair getEstadoCtaCteEmpresaComoProveedor(IiD empresa) {

		MyPair estado = new MyPair();
		try {

			Tipo estadoDom = rr.getCtaCteEmpresaEstadoProveedorByIdEmpresa(empresa.getId());
			estado = movimientoAss.tipoToMyPair(estadoDom);

		} catch (Exception e) {

			estado = this.getUtilDto().getCtaCteEmpresaEstadoSinCuenta();
			System.out.println("[ControlCtaCteEmpresa]" + e.getMessage());

		}
		return estado;

	}

	/**
	 * Obtiene el estado de la CtaCte de una empresa en caracter de cliente (Si
	 * Activo, Inactivo, Bloqueado o Sin Cta. Cte.).
	 * 
	 * @param empresa
	 *            Empresa de la cual se quiere conocer su estado.
	 * @return MyPair Con la informacion del estado de la empresa. Retorna
	 *         "Sin Cta. Cte" en el caso de que la empresa no posea una cuenta o
	 *         posea como valor de estado "null".
	 * @throws Exception
	 * 
	 */
	public MyPair getEstadoCtaCteEmpresaComoCliente(IiD empresa) {
		MyPair estado = new MyPair();
		try {
			Tipo estadoDom = rr.getCtaCteEmpresaEstadoClienteByIdEmpresa(empresa.getId());
			estado = movimientoAss.tipoToMyPair(estadoDom);

		} catch (Exception e) {

			estado = this.getUtilDto().getCtaCteEmpresaEstadoSinCuenta();
			System.out.println("[ControlCtaCteEmpresa] : " + e.getMessage());

		}
		return estado;

	}

	/**
	 * Obtiene la informacion de la Linea de Credito como Cliente de una
	 * empresa.
	 * 
	 * @param empresa
	 *            Cliente del cual se quiere recuperar su linea de credito.
	 * 
	 * @return Informacion de la linea de credito. Retorna "lineaCreditoDefault"
	 *         si no posee linea de credito, si esta no esta seteada o si el
	 *         cliente no posee Cta. Cte.
	 * 
	 *         -Pos1 ="descripcion" -Pos2 ="linea"
	 * 
	 */
	public MyArray getLineaCreditoCliente(IiD empresa) {

		MyArray lineaCredito = new MyArray();
		try {
			CtaCteLineaCredito lineaCreditoDom = rr.getCtaCteEmpresaLineaCreditoClienteByIdEmpresa(empresa.getId());
			lineaCredito.setId(lineaCreditoDom.getId());
			lineaCredito.setPos1(lineaCreditoDom.getDescripcion());
			lineaCredito.setPos2(lineaCreditoDom.getLinea());

		} catch (Exception e) {
			lineaCredito = this.getUtilDto().getCtaCteLineaCreditoDefault();
			System.out.println("[ControlCtaCteEmpresa] " + e.getMessage());
		}

		return lineaCredito;
	}

	/**
	 * Calcula el saldo disponible de la linea de credito de un cliente al
	 * momento de llamar al metodo.(EN GUARANIES)
	 * 
	 * @param empresa
	 *            Empresa de la cual se quiere saber el credito disponible.
	 * @return Credito disponible en la linea.
	 * 
	 * 
	 */
	public double getCreditoDisponibleLineaCreditoCliente(IiD empresa) throws Exception {

		MyArray lineaCredito = this.getLineaCreditoCliente(empresa);

		double linea = (double) lineaCredito.getPos2();

		double saldoCtaCte = this.getSaldoPendienteEmpresa(empresa,
				this.getUtilDto().getCtaCteEmpresaCaracterMovCliente());

		double creditoDisponible = linea;

		if (saldoCtaCte > 0.001) {
			creditoDisponible = linea - saldoCtaCte;
		}

		return creditoDisponible;
	}

	/**
	 * Calcula y retorna el saldo pendiente de la empresa en sus operaciones
	 * (movimientos) como Proveedor o como Cliente(En Guaranies). Obs.: Se
	 * realiza una "Guaranizacion" de todas las cuentas al cambio del dia para
	 * tener un saldo pendiente unificado. Obs.: El tipo de cambio utilizado en
	 * el metodo es de compraApp cuando el tercero realizo los movimientos en
	 * caracter de cliente y ventaApp cuando el tercero tiene caracter de
	 * proveedor en los movimientos realizados.
	 * 
	 * @param idEmpresa
	 *            Empresa de la que se quiere saber su saldo pendiente
	 * @return Saldo de los movimientos pendientes de la empresa
	 * @throws Exception
	 */
	public double getSaldoPendienteEmpresa(IiD empresa, MyPair caracterMovimiento) throws Exception {

		double sumaSaldos = 0;
		double cambio = 1;
		Tipo caracter = movimientoAss.MyPairToTipo(caracterMovimiento);
		List<Object[]> saldos = rr.getCtaCteEmpresaSaldos(empresa, caracter);

		for (Object[] o : saldos) {

			if (caracterMovimiento.getSigla().compareTo(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE) == 0)
				cambio = this.getUtilDto().getCambioCompraBCP((Tipo) o[2]);

			else if (caracterMovimiento.getSigla().compareTo(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR) == 0)
				cambio = this.getUtilDto().getCambioVentaBCP((Tipo) o[2]);

			sumaSaldos += (Double) o[1] * cambio;
		}
		return sumaSaldos;
	}

	/**
	 * MyArray con la informacion de si la cuenta esta o no habilitada para
	 * realizar movimientos a credito Pos1 = boolean (True = habilitado, False =
	 * Deshabilitado) Pos2 = MyArray con el mensaje de advertencia
	 * 
	 * @param empresa
	 * @return
	 * @throws Exception
	 */
	public MyArray isCreditoClienteHabilitado(IiD empresa) throws Exception {

		MyArray mhab = new MyArray();
		String mensaje = "";
		mhab.setPos1(false);

		MyPair estadoCtaCteEmp = this.getEstadoCtaCteEmpresaComoCliente(empresa);

		if (estadoCtaCteEmp.compareTo(this.getUtilDto().getCtaCteEmpresaEstadoActivo()) == 0) {

			mensaje = "Estado de la Cta. Cte. del cliente: " + estadoCtaCteEmp.getText();
			mhab.setPos2(mensaje);
			mhab.setPos1(true);

			return mhab;
		}

		mensaje = "Estado de la Cta. Cte. del cliente: " + estadoCtaCteEmp.getText();
		mhab.setPos2(mensaje);

		return mhab;

	}

	// *************************OBTENER CUENTAS****************************//
	public List<MyArray> getListadoCuentasClientes() throws Exception {

		List<MyArray> listadoCuentas = new ArrayList<MyArray>();
		Map<Long, List<CtaCteVisCliente>> cuentasClientes = rr.getListadoClienteCuentas();
		double pendienteGs = 0;
		double pendienteTotalGuaranies = 0;
		double tipoCambio = 1;
		double lineaCredito = 0;
		int aux = 0;

		for (Long l : cuentasClientes.keySet()) {

			pendienteTotalGuaranies = 0;
			List<MyArray> pendientesPorMoneda = new ArrayList<MyArray>();
			MyArray pendientePorMon = new MyArray();
			MyArray c = new MyArray();
			c.setId(l);
			c.setPos1(l);
			aux = 0;

			for (CtaCteVisCliente cta : cuentasClientes.get(l)) {

				pendientePorMon = new MyArray();
				if (aux == 0) {
					c.setPos2(cta.getRazonSocial());
					c.setPos3(cta.getFechaAperturaCuentaCliente());
					c.setPos4(cta.getEstadoComoCliente());
					c.setPos5(cta.getLineaCredito());
					lineaCredito = cta.getLineaCredito().getLinea();
				}
				if (cta.getTipoMoneda() == null) {
					tipoCambio = 1;
				} else {
					tipoCambio = this.getTipoCambioCompraBcp(cta.getTipoMoneda());
				}
				pendienteGs = this.convertirMonedaAGs(cta.getPendiente(), tipoCambio);
				pendienteTotalGuaranies += pendienteGs;
				pendientePorMon.setPos1(cta.getTipoMoneda());
				pendientePorMon.setPos2(cta.getPendiente());
				pendientePorMon.setPos3(tipoCambio);
				pendientePorMon.setPos4(pendienteGs);
				pendientesPorMoneda.add(pendientePorMon);
				aux += 1;
			}
			c.setPos6(pendienteTotalGuaranies);
			c.setPos7(lineaCredito - pendienteTotalGuaranies);
			c.setPos8(pendientesPorMoneda);

			listadoCuentas.add(c);
		}
		return listadoCuentas;
	}

	public List<MyArray> getListadoCuentasProveedores() throws Exception {

		List<MyArray> listadoCuentas = new ArrayList<MyArray>();
		Map<Long, List<CtaCteVisProveedor>> cuentasProveedores = rr.getListadoProveedorCuentas();
		double pendienteGs = 0;
		double pendienteTotalGuaranies = 0;
		double tipoCambio = 1;
		int aux = 0;

		for (Long l : cuentasProveedores.keySet()) {

			pendienteTotalGuaranies = 0;
			List<MyArray> pendientesPorMoneda = new ArrayList<MyArray>();
			MyArray pendientePorMon = new MyArray();
			MyArray c = new MyArray();
			c.setId(l);
			c.setPos1(l);
			aux = 0;

			for (CtaCteVisProveedor cta : cuentasProveedores.get(l)) {

				pendientePorMon = new MyArray();
				if (aux == 0) {
					c.setPos2(cta.getRazonSocial());
					c.setPos3(cta.getFechaAperturaCuentaProveedor());
					c.setPos4(cta.getEstadoComoProveedor());
				}
				if (cta.getTipoMoneda() == null) {
					tipoCambio = 1;
				} else {
					tipoCambio = this.getTipoCambioVentaBcp(cta.getTipoMoneda());
				}
				pendienteGs = this.convertirMonedaAGs(cta.getPendiente(), tipoCambio);
				pendienteTotalGuaranies += pendienteGs;
				pendientePorMon.setPos1(cta.getTipoMoneda());
				pendientePorMon.setPos2(cta.getPendiente());
				pendientePorMon.setPos3(tipoCambio);
				pendientePorMon.setPos4(pendienteGs);
				pendientesPorMoneda.add(pendientePorMon);
				aux += 1;
			}
			c.setPos5(pendienteTotalGuaranies);
			c.setPos6(pendientesPorMoneda);

			listadoCuentas.add(c);
		}
		return listadoCuentas;
	}

	// ******************************************OBTENER
	// MOVIMIENTOS***************************************//
	/**
	 * Este metodo retorna el DTO de un movimiento de la cuenta corriente de
	 * acuerdo a su id
	 * 
	 * @param movimiento
	 * @return CtaCteEmpresaMovimientoDTO
	 * 
	 */
	public CtaCteEmpresaMovimientoDTO getCtaCteEmpresaMovimientoDTO(IiD movimiento) throws Exception {

		AssemblerCtaCteEmpresaMovimiento ass = new AssemblerCtaCteEmpresaMovimiento();
		CtaCteEmpresaMovimientoDTO dto = (CtaCteEmpresaMovimientoDTO) ass
				.getDTObyId(CtaCteEmpresaMovimiento.class.getName(), movimiento.getId());
		return dto;

	}

	/**
	 * Devuelve un movimiento de acuerdo a los parametros.
	 * 
	 * @param idMovimientoOriginal
	 * @param tipoMovimiento
	 * @return Movimiento de acuerdo a los parametros. Retorna una lista vacia
	 *         si no existe el movimiento
	 * 
	 *         Ob.: Retorna una lista ya que algunos movimientos son
	 *         fracmentados en varios de acuerdo a su vencimiento.
	 * @throws Exception
	 */
	public List<CtaCteEmpresaMovimientoDTO> getCtaCteEmpresaMovimientoByMovimientoOriginal(long idMovimientoOriginal,
			IiD tipoMovimiento) throws Exception {

		List<CtaCteEmpresaMovimiento> movimientosDomain = rr
				.getCtaCteEmpresaMovimientoPorMovimientoOriginal(idMovimientoOriginal, tipoMovimiento.getId());
		List<CtaCteEmpresaMovimientoDTO> movimientosDTO = new ArrayList<CtaCteEmpresaMovimientoDTO>();
		for (CtaCteEmpresaMovimiento mov : movimientosDomain)
			movimientosDTO.add((CtaCteEmpresaMovimientoDTO) movimientoAss.domainToDto(mov));

		return movimientosDTO;
	}

	/**
	 * 
	 * Devuelve los movimientos de la CtaCte de una empresa de acuerdo a los
	 * parametros. Si no se encuentra ningun movimiento retorna una lista vacia
	 * 
	 * @param empresa
	 *            Empresa de la cual se quiere recuperar sus movimientos.
	 * @param caracterMovimientos
	 *            Para seleccionar los movimientos (Todos, como Cliente o como
	 *            Proveedor).
	 * @param fechaDesde
	 *            Rango para la busqueda desde esa fecha, si es null busca desde
	 *            el "Inicio de los tiempos :)".
	 * @param fechaHasta
	 *            Rango para la busqueda hasta esa fecha, si es null busca desde
	 *            la fecha de inicio hasta el "Final de los tiempos :)" Si tanto
	 *            la fecha de inicio como la fecha de fin son nulos busca todos
	 *            los movimientos.
	 * @param pendientes
	 *            true = solo movimientos pendientes false = todos los
	 *            movimientos.
	 * @param vencidos
	 *            true = retorna los movimientos pendientes vencidos
	 * @return List<MyArray> Las posiciones son: id = id del movimiento, pos1 =
	 *         idMovimientoOriginal, pos2 = nroComprobante, pos3 = fechaEmision
	 *         pos4 = m.fechaVencimiento, pos5 = importeOriginal, pos6 = saldo,
	 *         pos7 = moneda, pos8 = tipoMovimiento, pos9 =
	 *         tipoCategoriaMovimiento, pos10 = usuarioMod, pos11 = sucursal,
	 *         pos12 = saldoEnGs
	 * 
	 */
	public List<MyArray> getCtaCteEmpresaMovimientosMyArray(IiD empresa, MyPair caracterMovimientos, MyPair sucursalApp,
			Date fechaDesde, Date fechaHasta, boolean pendientes, boolean vencidos) throws Exception {

		Tipo caracter = movimientoAss.MyPairToTipo(caracterMovimientos);
		Tipo sucursal = movimientoAss.MyPairToTipo(sucursalApp);

		List<Object[]> lista = rr.getCtaCteEmpresaMovimientos(empresa, caracter, sucursal, fechaDesde, fechaHasta,
				pendientes, vencidos);

		List<MyArray> movimientosPendientes = this.listaObjectMovimientosToListaMyArrayMovimientos(lista);

		return movimientosPendientes;

	}

	/**
	 * @return los movimientos con saldo..
	 */
	public List<CtaCteEmpresaMovimientoDTO> getCtaCteEmpresaMovimientosPendientes(IiD empresa, MyPair caracter, long idMoneda) throws Exception {

		Tipo caracter_ = movimientoAss.MyPairToTipo(caracter);
		List<CtaCteEmpresaMovimiento> movimientosPendientesDom = rr.getMovimientosPendientes(empresa, caracter_, idMoneda);
		List<CtaCteEmpresaMovimientoDTO> movimientosPendientesDto = new ArrayList<CtaCteEmpresaMovimientoDTO>();
		for (CtaCteEmpresaMovimiento m : movimientosPendientesDom) {
			movimientosPendientesDto.add((CtaCteEmpresaMovimientoDTO) movimientoAss.domainToDto(m));
		}
		return movimientosPendientesDto;
	}

	/**
	 * Obtiene el listado de los movimientos de facturas de una empresa
	 * 
	 * @param empresa
	 *            Empresa de la cual se quiere obtener sus movimientos
	 * @param caracterMovimientos
	 *            Caracter en que la empresa realizo el movimiento. Ej.:
	 *            Caracter de cliente cuando se requiera obtener las facturas de
	 *            ventas realizadas al cliente
	 * @return Las facturas contado y credito correspondientes a los movimientos
	 *         de la empresa
	 * @throws Exception
	 */
	public List<CtaCteEmpresaMovimientoDTO> getCtaCteEmpresaMovimientosFacturas(IiD empresa, MyPair caracterMovimientos)
			throws Exception {
		Tipo caracter = movimientoAss.MyPairToTipo(caracterMovimientos);
		List<CtaCteEmpresaMovimiento> movimientosFactura = rr.getMovimientosFacturas(empresa, caracter);
		List<CtaCteEmpresaMovimientoDTO> movimientosPendientesDto = new ArrayList<CtaCteEmpresaMovimientoDTO>();
		for (CtaCteEmpresaMovimiento m : movimientosFactura) {
			movimientosPendientesDto.add((CtaCteEmpresaMovimientoDTO) movimientoAss.domainToDto(m));
		}
		return movimientosPendientesDto;
	}

	// ******************************************ANHADIR
	// MOVIMIENTOS*******************************************//
	/**
	 * 
	 * @param empresa
	 *            El "Cliente" o el "Proveedor" con el que se realizo la
	 *            operacion.
	 * @param idMovimientoOriginal
	 *            El ID del movimiento original.
	 * @param nroComprobante
	 *            El numero de comprobante correspondiente a la operacion.
	 * @param fechaEmision
	 *            Fecha de emision del comprobante.
	 * @param importeOriginal
	 *            Importe total del comprobante.
	 * @param moneda
	 *            Moneda en la que se realizo la operacion(Guaranies, $, etc.).
	 * @param tipoMovimiento
	 *            Tipo de movimiento de la operacion. Ej.: Venta Contado, Compra
	 *            Local Contado, etc. Este dato debe ser un MyArray Donde la
	 *            pos2 si o si debe contener la sigla del movimiento
	 * @param tipoCaracterMovimiento
	 *            Caracter del movimiento de la empresa con la que se realizo la
	 *            operacion. Ej.: Si se realizo una venta, entonces el caracter
	 *            del movimiento de la Empresa con la que se realizo la
	 *            operacion es "Cliente", si se realiza una compra, el caracter
	 *            del movimiento de la Empresa con la que se realizo la
	 *            operacion es "Proveedor".
	 * @param sucursal
	 *            Sucursal nonde se produjo el movimiento.
	 * @throws Exception
	 * 
	 */
	public void addCtaCteEmpresaMovimientoFacturaContado(IiD empresa, long idMovimientoOriginal, String nroComprobante,
			Date fechaEmision, double importeOriginal, MyPair moneda, MyArray tipoMovimiento,
			MyPair tipoCaracterMovimiento, MyArray sucursal, boolean mantieneSaldo, String numeroImportacion) throws Exception {

		CtaCteEmpresaMovimientoDTO nuevoMovimientoDTO = new CtaCteEmpresaMovimientoDTO();
		nuevoMovimientoDTO.setIdEmpresa(empresa.getId());
		nuevoMovimientoDTO.setIdMovimientoOriginal(idMovimientoOriginal);
		nuevoMovimientoDTO.setNroComprobante(nroComprobante);
		nuevoMovimientoDTO.setFechaEmision(fechaEmision);
		nuevoMovimientoDTO.setFechaVencimiento(null);
		nuevoMovimientoDTO.setImporteOriginal(importeOriginal);
		nuevoMovimientoDTO.setNumeroImportacion(numeroImportacion);
		if (mantieneSaldo == true) {
			nuevoMovimientoDTO.setSaldo(importeOriginal);
		} else {
			nuevoMovimientoDTO.setSaldo(0);
		}
		nuevoMovimientoDTO.setMoneda(moneda);
		nuevoMovimientoDTO.setTipoMovimiento(tipoMovimiento);
		nuevoMovimientoDTO.setTipoCaracterMovimiento(tipoCaracterMovimiento);
		nuevoMovimientoDTO.setSucursal(sucursal);

		this.addMovimiento(nuevoMovimientoDTO);
	}

	/**
	 * El metodo anula los movimientos correspondientes a una factura contado.
	 * 
	 * @param idMovimientoOriginal
	 *            El id del movimiento original.
	 * @param tipoMovimiento
	 *            Tipo de movimiento de la operacion. Ej.: Venta Credito, Compra
	 *            Local Credito, etc.Este dato debe ser un IiD
	 * 
	 * @throws Exception
	 */
	public void revertCtaCteEmpresaMovimientoFacturaContado(Long idMovimientoOriginal, IiD tipoMovimiento)
			throws Exception {
		List<CtaCteEmpresaMovimientoDTO> movimiento = new ArrayList<CtaCteEmpresaMovimientoDTO>();
		movimiento = this.getCtaCteEmpresaMovimientoByMovimientoOriginal(idMovimientoOriginal, tipoMovimiento);
		for (CtaCteEmpresaMovimientoDTO mov : movimiento) {
			mov.setAnulado(true);
			CtaCteEmpresaMovimiento movDomain = (CtaCteEmpresaMovimiento) movimientoAss.dtoToDomain(mov);
			rr.saveObject(movDomain, "CtaCte");
		}
	}

	/**
	 * Para la anulacion de las facturas de credito [Ver validaciones]
	 * @param idMovimientoOriginal
	 * @param tipoMovimiento
	 * @throws Exception
	 */
	public void revertCtaCteEmpresaMovimientoFacturaCredito(Long idMovimientoOriginal, IiD tipoMovimiento)
			throws Exception {
		List<CtaCteEmpresaMovimientoDTO> movimiento = new ArrayList<CtaCteEmpresaMovimientoDTO>();
		movimiento = this.getCtaCteEmpresaMovimientoByMovimientoOriginal(idMovimientoOriginal, tipoMovimiento);
		for (CtaCteEmpresaMovimientoDTO mov : movimiento) {
			mov.setAnulado(true);
			CtaCteEmpresaMovimiento movDomain = (CtaCteEmpresaMovimiento) movimientoAss.dtoToDomain(mov);
			rr.saveObject(movDomain, "CtaCte");
		}
	}

	/**
	 * 
	 * Anula los movimientos de cobro (recibos)[AUN FALTA TERMINAR]
	 * 
	 * @param idMovimientoOriginal
	 * @param tipoMovimiento
	 * @throws Exception
	 */
	public void revertCtaCteEmpresaMovimientoCobro(Long idMovimientoOriginal, IiD tipoMovimiento) throws Exception {
		List<CtaCteEmpresaMovimientoDTO> movimiento = new ArrayList<CtaCteEmpresaMovimientoDTO>();
		movimiento = this.getCtaCteEmpresaMovimientoByMovimientoOriginal(idMovimientoOriginal, tipoMovimiento);
		for (CtaCteEmpresaMovimientoDTO mov : movimiento) {
			mov.setAnulado(true);
			CtaCteEmpresaMovimiento movDomain = (CtaCteEmpresaMovimiento) movimientoAss.dtoToDomain(mov);
			rr.saveObject(movDomain, "CtaCte");
		}
	}

	

	/**
	 * add ctactemovimiento..
	 */
	public void addCtaCteEmpresaMovimientoFacturaCredito(IiD empresa, long idMovimientoOriginal, String nroComprobante,
			Date fechaEmision, int diasEntreVencimientos, int cuotasVencimiento, double importeOriginal,
			double entregaInicial, double saldo, MyPair moneda, MyArray tipoMovimiento, MyPair tipoCaracterMovimiento,
			MyArray sucursal, String numeroImportacion, double tipoCambio) throws Exception {

		CtaCteEmpresaMovimientoDTO nuevoMovimientoDTO = new CtaCteEmpresaMovimientoDTO();
		nuevoMovimientoDTO.setIdEmpresa(empresa.getId());
		nuevoMovimientoDTO.setIdMovimientoOriginal(idMovimientoOriginal);
		nuevoMovimientoDTO.setFechaEmision(fechaEmision);
		nuevoMovimientoDTO.setSaldo(saldo);
		nuevoMovimientoDTO.setMoneda(moneda);
		nuevoMovimientoDTO.setTipoCambio(tipoCambio);
		nuevoMovimientoDTO.setTipoMovimiento(tipoMovimiento);
		nuevoMovimientoDTO.setTipoCaracterMovimiento(tipoCaracterMovimiento);
		nuevoMovimientoDTO.setSucursal(sucursal);
		nuevoMovimientoDTO.setNumeroImportacion(numeroImportacion);

		if (entregaInicial > 0) {

			nuevoMovimientoDTO.setImporteOriginal(entregaInicial);
			nuevoMovimientoDTO.setNroComprobante(nroComprobante + " (" + 1 + "/" + (cuotasVencimiento + 1) + ")");
			nuevoMovimientoDTO.setSaldo(0);
			this.addMovimiento(nuevoMovimientoDTO);

			for (int i = 1; i <= cuotasVencimiento; i++) {

				nuevoMovimientoDTO.setFechaVencimiento(this.m.agregarDias(fechaEmision, diasEntreVencimientos * i));
				nuevoMovimientoDTO.setNroComprobante(nroComprobante + " (" + (i + 1) + "/" + (cuotasVencimiento + 1) + ")");
				nuevoMovimientoDTO.setImporteOriginal(importeOriginal);
				nuevoMovimientoDTO.setSaldo(importeOriginal / cuotasVencimiento);
				this.addMovimiento(nuevoMovimientoDTO);
			}

		} else {

			for (int i = 1; i <= cuotasVencimiento; i++) {

				nuevoMovimientoDTO.setFechaVencimiento(this.m.agregarDias(fechaEmision, diasEntreVencimientos * i));
				nuevoMovimientoDTO.setNroComprobante(nroComprobante + " (" + i + "/" + (cuotasVencimiento) + ")");
				nuevoMovimientoDTO.setImporteOriginal(importeOriginal);
				nuevoMovimientoDTO.setSaldo(saldo / cuotasVencimiento);
				this.addMovimiento(nuevoMovimientoDTO);
			}

		}
	}

	/**
	 * Agrega un nuevo movimiento a la cuenta corriente(Facturas) ya sean al
	 * contado o a credito.
	 * 
	 * @param nuevoMovimientoDTO
	 *            Dto con la informacion relacionada al movimiento que sera
	 *            almacenado.
	 * @throws Exception
	 * 
	 */
	public void addMovimiento(CtaCteEmpresaMovimientoDTO nuevoMovimientoDTO) throws Exception {

		CtaCteEmpresaMovimiento nuevoMovimientoDom = (CtaCteEmpresaMovimiento) movimientoAss.dtoToDomain(nuevoMovimientoDTO);
		rr.saveObject(nuevoMovimientoDom, this.getLoginNombre());
	}

	/**
	 * Metodo para anhadir a los movimmientos de la cuenta corriente un
	 * pago/cobro con sus respectivas imputaciones.
	 * 
	 * @param empresa
	 *            Empresa con la que se realizo la operacion
	 * @param idMovimientoOriginal
	 *            Identificador del movimiento original
	 * @param nroComprobante
	 *            Nro del documento de la operacion
	 * @param fechaEmision
	 *            Fecha de emision del documento de la operacion
	 * @param importeOriginal
	 *            Importe total inicial de la operacion
	 * @param saldo
	 *            Saldo pendiente si hubiere saldo a favor de la empresa
	 * @param moneda
	 *            Tipo de moneda con la que se realizo la operacion
	 * @param cambio
	 *            Tipo de cambio con la que se realizo la operacion
	 * @param tipoMovimiento
	 *            Tipo de movimiento de la operacion
	 * @param tipoCaracterMovimiento
	 *            Caracter del movimiento con que la tercera empresa realizo la
	 *            operacion(En caso de pago seria como proveedor)
	 * @param sucursal
	 *            Sucursal donde se realizo la operacion(De Yhaguy)
	 * @param detallesRecibo
	 *            Contiene los movimientos que afecto la operacion mas los
	 *            montos
	 * @throws Exception
	 * 
	 */
	public void addMovimientoRecibo(IiD empresa, long idMovimientoOriginal, String nroComprobante, Date fechaEmision,
			double importeOriginal, double saldo, MyPair moneda, double cambio, MyArray tipoMovimiento,
			MyPair tipoCaracterMovimiento, MyArray sucursal, List<ReciboDetalleDTO> detallesRecibo) throws Exception {

		CtaCteEmpresaMovimientoDTO recibo = new CtaCteEmpresaMovimientoDTO();

		recibo.setIdEmpresa(empresa.getId());
		recibo.setIdMovimientoOriginal(idMovimientoOriginal);
		recibo.setNroComprobante(nroComprobante);
		recibo.setFechaEmision(fechaEmision);
		recibo.setImporteOriginal(importeOriginal);
		recibo.setSaldo(saldo);
		recibo.setMoneda(moneda);
		recibo.setTipoMovimiento(tipoMovimiento);
		recibo.setTipoCaracterMovimiento(tipoCaracterMovimiento);
		recibo.setSucursal(sucursal);

		for (ReciboDetalleDTO detalleRecibo : detallesRecibo) {

			CtaCteImputacionDTO imputacionDto = new CtaCteImputacionDTO();

			imputacionDto.setQuienImputa(nroComprobante);
			imputacionDto.setDondeImputa(detalleRecibo.getMovimiento().getNroComprobante());
			imputacionDto.setMoneda(moneda);

			/*
			 * Par saber cual de los montos del detalle del recibo se va a
			 * utilizar como monto de la imputacion de acuerdo a la moneda de la
			 * operacion.
			 */
			if (recibo.getMoneda().compareTo(this.getUtilDto().getMonedaGuarani()) == 0) {
				imputacionDto.setMontoImputado(detalleRecibo.getMontoGs());
			} else {
				imputacionDto.setMontoImputado(detalleRecibo.getMontoDs());
			}

			/*
			 * Para calcular y guardar su tipo de imputacion(Parcial o
			 * Completa).
			 */
			if (detalleRecibo.getMovimiento().getImporteOriginal() == detalleRecibo.getMontoGs()) {
				imputacionDto.setTipoImputacion(this.getUtilDto().getCtaCteImputacionCompleta());
			} else {
				imputacionDto.setTipoImputacion(this.getUtilDto().getCtaCteImputacionParcial());
			}

			/*
			 * Para saber que cambio debe ser almacenado
			 */
			if (detalleRecibo.getMovimiento().getMoneda().compareTo(moneda) == 0) {
				imputacionDto.setTipoCambio(1);
			} else if (detalleRecibo.getMovimiento().getMoneda().compareTo(this.getUtilDto().getMonedaGuarani()) == 0
					&& recibo.getMoneda().compareTo(this.getUtilDto().getMonedaGuarani()) != 0) {
				imputacionDto.setTipoCambio(cambio);
			} else if (detalleRecibo.getMovimiento().getMoneda().compareTo(this.getUtilDto().getMonedaGuarani()) != 0
					&& recibo.getMoneda().compareTo(this.getUtilDto().getMonedaGuarani()) == 0) {
				imputacionDto.setTipoCambio(detalleRecibo.getTipoCambio());
			} else {
				imputacionDto.setTipoCambio(cambio);
			}

			this.setAss(imputacionAss);
			imputacionDto = (CtaCteImputacionDTO) this.saveDTO(imputacionDto);
			this.setAss(movimientoAss);

			detalleRecibo.getMovimiento().getImputaciones().add(imputacionDto);
			this.saveDTO(detalleRecibo.getMovimiento());

			recibo.getImputaciones().add(imputacionDto);

		}

		this.setAss(movimientoAss);
		this.saveDTO(recibo);
	}

	public void asignarImputacion(IiD empresa, CtaCteEmpresaMovimientoDTO mov,
			List<CtaCteEmpresaDetalleImputacion> detallesAnticipo) throws Exception {

		for (CtaCteEmpresaDetalleImputacion detalleAnticipo : detallesAnticipo) {

			CtaCteImputacionDTO imputacion = new CtaCteImputacionDTO();

			this.asignarDatosImputacion(mov, detalleAnticipo.getMovimiento(), imputacion, detalleAnticipo.getMontoDs());

			this.setAss(imputacionAss);
			imputacion = (CtaCteImputacionDTO) this.saveDTO(imputacion);
			this.setAss(movimientoAss);

			detalleAnticipo.getMovimiento().getImputaciones().add(imputacion);
			this.saveDTO(detalleAnticipo.getMovimiento());

			mov.getImputaciones().add(imputacion);

		}

		this.setAss(movimientoAss);
		this.saveDTO(mov);
	}

	/**
	 * Anhade movimientos de cobro o pago a la cuenta corriente con sus
	 * respectivas imputaciones
	 * 
	 * @param params
	 * 
	 *            IiD empresa long idMovimientoOriginal String nroComprobante
	 *            Date fechaEmision double importeOriginal double saldo MyPair
	 *            moneda double cambio MyArray tipoMovimiento MyPair
	 *            tipoCaracterMovimiento MyArray sucursal List
	 *            <ReciboDetalleDTO> detallesRecibo
	 * 
	 * @throws Exception
	 */
	public void addMovimientoRecibo(Map<String, Object> params) throws Exception {

		IiD empresa = (IiD) params.get("empresa");
		long idMovimientoOriginal = (long) params.get("idMovimientoOriginal");
		String nroComprobante = (String) params.get("nroComprobante");
		Date fechaEmision = (Date) params.get("fechaEmision");
		double importeOriginal = (double) params.get("importeOriginal");
		double saldo = (double) params.get("saldo");
		MyPair moneda = (MyPair) params.get("moneda");
		double cambio = (double) params.get("cambio");
		MyArray tipoMovimiento = (MyArray) params.get("tipoMovimiento");
		MyPair tipoCaracterMovimiento = (MyPair) params.get("tipoCaracterMovimiento");
		MyArray sucursal = (MyArray) params.get("sucursal");
		@SuppressWarnings("unchecked")
		List<ReciboDetalleDTO> detallesRecibo = (List<ReciboDetalleDTO>) params.get("detallesRecibo");

		this.addMovimientoRecibo(empresa, idMovimientoOriginal, nroComprobante, fechaEmision, importeOriginal, saldo,
				moneda, cambio, tipoMovimiento, tipoCaracterMovimiento, sucursal, detallesRecibo);

	}

	/* El credito disminuye la deuda */
	/**
	 * Agrega el movimiento correspondiente a la Nota de Credito con sus
	 * respectivas imputaciones
	 * 
	 * @param empresa
	 *            Empresa con la que se realizo la operacion
	 * @param idMovimientoOriginal
	 *            Identificador del movimiento original
	 * @param nroComprobante
	 *            Nro. del Comprabante del movimiento.
	 * @param fechaEmision
	 *            Fecha de Emision del Comprobante.
	 * @param importeOriginal
	 *            Importe total de la Nota de Credito
	 * @param moneda
	 *            Moneda de la Nota de Credito
	 * @param tipoMovimiento
	 *            Tipo de Movimiento correspondiente a la aplicacion de la Nota
	 *            de Credito
	 * @param tipoCaracterMovimiento
	 *            Caracter en la que la Empresa(Tercero) realizo sus operaciones
	 * @param sucursal
	 *            SucursalApp en la que fue emitido el documento
	 * @throws Exception
	 */
	public void addMovimientoNotaCredito(IiD empresa, long idMovimientoOriginal, String nroComprobante,
			Date fechaEmision, double importeOriginal, MyPair moneda, MyArray tipoMovimiento,
			MyPair tipoCaracterMovimiento, MyArray sucursal, List<NotaCreditoDetalleDTO> detallesNota)
					throws Exception {

		CtaCteEmpresaMovimientoDTO nuevoMovimientoDTO = new CtaCteEmpresaMovimientoDTO();
		nuevoMovimientoDTO.setIdEmpresa(empresa.getId());
		nuevoMovimientoDTO.setIdMovimientoOriginal(idMovimientoOriginal);
		nuevoMovimientoDTO.setNroComprobante(nroComprobante);
		nuevoMovimientoDTO.setFechaEmision(fechaEmision);
		nuevoMovimientoDTO.setFechaVencimiento(null);
		nuevoMovimientoDTO.setImporteOriginal(importeOriginal);
		nuevoMovimientoDTO.setSaldo(importeOriginal);
		nuevoMovimientoDTO.setMoneda(moneda);
		nuevoMovimientoDTO.setTipoMovimiento(tipoMovimiento);
		nuevoMovimientoDTO.setTipoCaracterMovimiento(tipoCaracterMovimiento);
		nuevoMovimientoDTO.setSucursal(sucursal);

		this.addMovimientoNotaCreditoAux(nuevoMovimientoDTO, detallesNota);

	}

	/**
	 * 
	 * @param notaCredito
	 * @param detallesNota
	 * @throws Exception
	 * 
	 */
	private void addMovimientoNotaCreditoAux(CtaCteEmpresaMovimientoDTO notaCredito,
			List<NotaCreditoDetalleDTO> detallesNota) throws Exception {

		List<CtaCteEmpresaMovimientoDTO> movs;
		MyArray tipoMovimientoFactura;
		long idMovOriginalFactura = 0;

		for (NotaCreditoDetalleDTO detalle : detallesNota) {

			/*
			 * Obtiene el moviento/Movimientos(En caso de que sea venta a
			 * credito a cuotas) de la Cta. Cte. equivalente a la venta.
			 */
			idMovOriginalFactura = (long) detalle.getDatosCtaCte().getPos1();
			tipoMovimientoFactura = (MyArray) detalle.getDatosCtaCte().getPos2();
			movs = this.getCtaCteEmpresaMovimientoByMovimientoOriginal(idMovOriginalFactura, tipoMovimientoFactura);

			double montoImputado = 0;
			double montoAplicado = 0;

			if (this.isMovMonedaLocal(notaCredito))
				montoAplicado = detalle.getImporteGs();
			else
				montoAplicado = detalle.getImporteDs();

			// Si existe algun movimiento equivalente
			if (movs.size() > 0) {
				/*
				 * Obtiene el primer elemento de la lista(Ya que si el
				 * movimiento es contado tendra un solo movimiento)
				 */
				if (this.isDocMovFacturaContado(movs.get(0))) {

					CtaCteImputacionDTO imputacion = new CtaCteImputacionDTO();
					montoImputado = montoAplicado;
					this.asignarDatosImputacion(notaCredito, movs.get(0), imputacion, montoImputado);

					// Se guarda la imputacion
					this.setAss(imputacionAss);
					imputacion = (CtaCteImputacionDTO) this.saveDTO(imputacion);
					this.setAss(movimientoAss);

					// Se actualiza el movimiento del detalle
					movs.get(0).getImputaciones().add(imputacion);
					this.saveDTO(movs.get(0));

					// Se actualiza el movimiento de la nota de credito
					notaCredito.getImputaciones().add(imputacion);
					notaCredito.setSaldo(0);

				} else {
					for (CtaCteEmpresaMovimientoDTO mov : movs) {

						/*
						 * if(this.m.compararNumeros(montoAplicado, 0) == 0){
						 * return }
						 */

						if (this.m.compararNumeros(montoAplicado, mov.getSaldo()) >= 0) {
							montoAplicado = montoAplicado - mov.getSaldo();
							montoImputado = mov.getSaldo();
							mov.setSaldo(0);

						} else {

							montoImputado = montoAplicado;
							double aux = mov.getSaldo() - montoAplicado;
							montoAplicado = 0;
							mov.setSaldo(aux);

						}

						// Todavia no resta los saldos
						CtaCteImputacionDTO imputacion = new CtaCteImputacionDTO();
						this.asignarDatosImputacion(notaCredito, mov, imputacion, montoImputado);

						// Se guarda la imputacion
						this.setAss(imputacionAss);
						imputacion = (CtaCteImputacionDTO) this.saveDTO(imputacion);
						this.setAss(movimientoAss);

						// Se actualiza el movimiento del detalle
						mov.getImputaciones().add(imputacion);
						this.saveDTO(mov);

						// Se actualiza el movimiento de la nota de credito
						notaCredito.getImputaciones().add(imputacion);

						notaCredito.setSaldo(montoAplicado * (-1));

					}
				}
			}
		}
		// Se actualiza el movimiento de la nota de credito
		this.saveDTO(notaCredito, new AssemblerCtaCteEmpresaMovimiento());
	}

	public void addMovimientoNotaDebito(IiD empresa, long idMovimientoOriginal, String nroComprobante,
			Date fechaEmision, double importeOriginal, MyPair moneda, MyArray tipoMovimiento,
			MyPair tipoCaracterMovimiento, MyArray sucursal) throws Exception {

		CtaCteEmpresaMovimientoDTO nuevoMovimientoDTO = new CtaCteEmpresaMovimientoDTO();
		nuevoMovimientoDTO.setIdEmpresa(empresa.getId());
		nuevoMovimientoDTO.setIdMovimientoOriginal(idMovimientoOriginal);
		nuevoMovimientoDTO.setNroComprobante(nroComprobante);
		nuevoMovimientoDTO.setFechaEmision(fechaEmision);
		nuevoMovimientoDTO.setFechaVencimiento(null);
		nuevoMovimientoDTO.setImporteOriginal(importeOriginal);
		nuevoMovimientoDTO.setSaldo(0);
		nuevoMovimientoDTO.setMoneda(moneda);
		nuevoMovimientoDTO.setTipoMovimiento(tipoMovimiento);
		nuevoMovimientoDTO.setTipoCaracterMovimiento(tipoCaracterMovimiento);
		nuevoMovimientoDTO.setSucursal(sucursal);

		this.addMovimiento(nuevoMovimientoDTO);
	}

	/**
	 * Realiza la anulacion de una imputacion devolviendo los saldos al estado
	 * anterior a la imputacion
	 * 
	 * @param imputacion
	 * @throws Exception
	 */
	public void anularImputacion(CtaCteImputacion imputacion) throws Exception {
		List<CtaCteEmpresaMovimiento> listaMovimientosDom = rr.getCtaCteEmpresaMovimientosPorImputacion(imputacion);
		CtaCteEmpresaMovimientoDTO quienImputa = new CtaCteEmpresaMovimientoDTO();
		CtaCteEmpresaMovimientoDTO dondeImputa = new CtaCteEmpresaMovimientoDTO();

		for (CtaCteEmpresaMovimiento m : listaMovimientosDom) {
			if (m.getNroComprobante().compareTo(imputacion.getQuienImputa()) == 0) {
				quienImputa = (CtaCteEmpresaMovimientoDTO) movimientoAss.domainToDto(m);
			} else if (m.getNroComprobante().compareTo(imputacion.getDondeImputa()) == 0) {
				dondeImputa = (CtaCteEmpresaMovimientoDTO) movimientoAss.domainToDto(m);
			}
		}
		if ((((String) quienImputa.getTipoMovimiento().getPos2()).compareTo(Configuracion.SIGLA_TM_RECIBO_COBRO) == 0
				|| ((String) quienImputa.getTipoMovimiento().getPos2())
						.compareTo(Configuracion.SIGLA_TM_RECIBO_PAGO) == 0)
				&& (((String) dondeImputa.getTipoMovimiento().getPos2())
						.compareTo(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO) == 0
						|| ((String) dondeImputa.getTipoMovimiento().getPos2())
								.compareTo(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO) == 0)) {

			quienImputa.setSaldo(quienImputa.getSaldo() + (imputacion.getMontoImputado() * -1));
			dondeImputa.setSaldo(dondeImputa.getSaldo() + imputacion.getMontoImputado());
		}

		rr.saveObject((CtaCteEmpresaMovimiento) movimientoAss.dtoToDomain(dondeImputa), this.getLoginNombre());
		rr.saveObject((CtaCteEmpresaMovimiento) movimientoAss.dtoToDomain(quienImputa), this.getLoginNombre());
	}

	// El Tipo de movimiento debe ser transferencia mercaderias
	public void addTransferencia(IiD empresa, long idMovimientoOriginal, String nroComprobante, Date fechaEmision,
			double importeOriginal, MyPair moneda, MyArray tipoMovimiento, MyPair tipoCaracterMovimiento,
			MyArray sucursal) throws Exception {

		CtaCteEmpresaMovimientoDTO nuevoMovimientoDTO = new CtaCteEmpresaMovimientoDTO();
		nuevoMovimientoDTO.setIdEmpresa(empresa.getId());
		nuevoMovimientoDTO.setIdMovimientoOriginal(idMovimientoOriginal);
		nuevoMovimientoDTO.setNroComprobante(nroComprobante);
		nuevoMovimientoDTO.setFechaEmision(fechaEmision);
		nuevoMovimientoDTO.setFechaVencimiento(null);
		nuevoMovimientoDTO.setImporteOriginal(importeOriginal);
		nuevoMovimientoDTO.setSaldo(importeOriginal);
		nuevoMovimientoDTO.setMoneda(moneda);
		nuevoMovimientoDTO.setTipoMovimiento(tipoMovimiento);
		nuevoMovimientoDTO.setTipoCaracterMovimiento(tipoCaracterMovimiento);
		nuevoMovimientoDTO.setSucursal(sucursal);

		this.addMovimiento(nuevoMovimientoDTO);
	}

	// ******************************************UTILES*******************************************//

	/**
	 * Convierte una lista de array de objetos a una lista de MyArray de acuerdo
	 * a lo que se trae de la base de datos son las posiciones.
	 * 
	 * @return List<MyArray> Las posiciones son: id = id del movimiento (long)
	 *         pos1 = idMovimientoOriginal(long) pos2 = nroComprobante (String)
	 *         pos3 = fechaEmision (Date) pos4 = fechaVencimiento (Date) pos5 =
	 *         importeOriginal (double) pos6 = saldo (double) pos7 = moneda
	 *         (MyPair) pos8 = tipoMovimiento (String) pos9 =
	 *         tipoCaracterMovimiento (MyPair) pos10 = usuarioMod pos11 =
	 *         sucursal () pos12 = saldoEnGs (double)
	 * @throws Exception
	 */
	private List<MyArray> listaObjectMovimientosToListaMyArrayMovimientos(List<Object[]> lista) throws Exception {

		double saldoEnGs = 0;
		List<MyArray> myArrays = new ArrayList<MyArray>();
		double cambio = 0;

		for (Object[] obj : lista) {
			MyArray m = new MyArray();
			m.setId((Long) obj[0]);
			m.setPos1(obj[1]);
			m.setPos2(obj[2]);
			m.setPos3(obj[3]);
			m.setPos4(obj[4]);
			m.setPos5(obj[5]);
			m.setPos6(obj[6]);

			MyPair moneda = movimientoAss.tipoToMyPair((Tipo) obj[7]);
			m.setPos7(moneda);

			m.setPos8(obj[8]);

			MyPair caracterMovimiento = movimientoAss.tipoToMyPair(rr.getTipoPorSigla((String) obj[9]));
			m.setPos9(caracterMovimiento);

			m.setPos10(obj[10]);
			m.setPos11(obj[11]);

			if (caracterMovimiento.getSigla().compareTo(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE) == 0)
				cambio = this.getUtilDto().getCambioCompraBCP(moneda);
			else if (caracterMovimiento.getSigla().compareTo(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR) == 0)
				cambio = this.getUtilDto().getCambioVentaBCP(moneda);

			saldoEnGs = this.convertirMonedaAGs((double) m.getPos6(), cambio);

			m.setPos12(saldoEnGs);

			myArrays.add(m);
		}
		return myArrays;
	}

	/**
	 * Asigna los datos de la imputacion de acuerdo a los datos obtenidos de la
	 * lista de parametros. Obs. 1: El monto a imputar debe estar en la moneda
	 * orignal del movimiento "quienImputa". Obs. 2: Ahora mismo utiliza el tipo
	 * de cambio compra, pero eso deberia ser variable de acuerdo a si la
	 * imputacion se realiza sobre operaciones de clientes o de proveedores.
	 * 
	 * @param quienImputa
	 *            El moviento que aplicara la imputacion.
	 * @param dondeImputa
	 *            El movimiento sobre el cual se aplica la imputacion.
	 * @param montoAimputar
	 *            El monto imputado por el movimiento "quienImputa" al
	 *            movimiento "dondeImputa". Esta debe estar en la moneda
	 *            original del movimiento "quienImputa".
	 * 
	 * 
	 */
	private void asignarDatosImputacion(CtaCteEmpresaMovimientoDTO quienImputa, CtaCteEmpresaMovimientoDTO dondeImputa,
			CtaCteImputacionDTO imputacion, double montoAimputar) {

		imputacion.setQuienImputa(quienImputa.getNroComprobante());
		imputacion.setDondeImputa(dondeImputa.getNroComprobante());
		imputacion.setMontoImputado(montoAimputar);

		/*
		 * La moneda es igual a la moneda con la que se aplico la imputacion,
		 * dicho de otro modo es la moneda del movimiento "quienImputa".
		 */
		imputacion.setMoneda(quienImputa.getMoneda());

		/*
		 * El tipo de cambio a ser almacenado debe ser seleccionado de uno de
		 * los siguientes casos.
		 */
		if (quienImputa.getMoneda().compareTo(dondeImputa.getMoneda()) == 0) {

			imputacion.setTipoCambio(1);

		} else if (this.isMovMonedaLocal(quienImputa) == false && this.isMovMonedaLocal(dondeImputa) == true) {

			imputacion.setTipoCambio(getTipoCambioCompraBcp(quienImputa.getMoneda()));

		} else if (this.isMovMonedaLocal(quienImputa) == true && this.isMovMonedaLocal(dondeImputa) == false) {

			imputacion.setTipoCambio(getTipoCambioCompraBcp(dondeImputa.getMoneda()));

		} else {

			imputacion.setTipoCambio(getTipoCambioCompraBcp(quienImputa.getMoneda()));
		}

		/*
		 * 
		 * Para saber si la imputacion es parcial o completa la condicion sera
		 * que el monto que se imputo haya afectado en su totalidad al importe
		 * del movimiento imputado. Para facilitar esa comparacion se hace la
		 * conversion de los dos montos a gs.
		 */
		double montoAimputarGs = convertirMonedaAGs(montoAimputar, getTipoCambioCompraBcp(quienImputa.getMoneda()));

		double importeOrigGsDondeImputa = convertirMonedaAGs(dondeImputa.getImporteOriginal(),
				getTipoCambioCompraBcp(dondeImputa.getMoneda()));

		if (this.m.compararNumeros(montoAimputarGs, importeOrigGsDondeImputa) == 0) {
			imputacion.setTipoImputacion(this.getUtilDto().getCtaCteImputacionCompleta());
		} else {
			imputacion.setTipoImputacion(this.getUtilDto().getCtaCteImputacionParcial());
		}

	}

	/**
	 * Para saber si el documento del TipoMovimiento es una factura contado.
	 * 
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	private boolean isDocMovFacturaContado(CtaCteEmpresaMovimientoDTO mov) throws Exception {
		// Trae para evitar usar el MyArray
		TipoMovimiento tm = (TipoMovimiento) rr.getTipoMovimientoById(mov.getTipoMovimiento().getId());
		Tipo tipoDocDelTipoMovimiento = tm.getTipoDocumento();
		boolean isFacCon = false;
		if (tipoDocDelTipoMovimiento.getSigla().compareTo(Configuracion.SIGLA_DOC_FAC_CONTADO) == 0)
			isFacCon = true;

		return isFacCon;
	}

	/**
	 * Retorna true si el movimiento fue realizado en moneda local
	 * 
	 * @param mov
	 *            Movimiento de la Cta. Cte. de la cual se quiere saber si fue
	 *            realizado en moneda local.
	 * @return true si lo fue.
	 */
	private boolean isMovMonedaLocal(CtaCteEmpresaMovimientoDTO mov) {
		return this.isMonedaLocal(mov.getMoneda());
	}

	/**
	 * Conocer si la moneda del parametro es la moneda local
	 * 
	 * @param moneda
	 * @return true si es moneda local
	 */
	private boolean isMonedaLocal(MyPair moneda) {
		boolean isMonLocal = false;
		MyPair monedaLocal = this.getUtilDto().getMonedaGuarani();
		if (moneda.compareTo(monedaLocal) == 0)
			isMonLocal = true;
		return isMonLocal;
	}

	/**
	 * @param moneda
	 * @param cambio
	 * @return
	 */
	private double convertirMonedaAGs(double moneda, double cambio) {
		return moneda * cambio;
	}

	private double getTipoCambioCompraBcp(IiD moneda) {
		return getUtilDto().getCambioCompraBCP(moneda);
	}

	private double getTipoCambioVentaBcp(IiD moneda) {
		return getUtilDto().getCambioVentaBCP(moneda);
	}

}