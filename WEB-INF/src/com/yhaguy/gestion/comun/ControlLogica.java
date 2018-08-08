package com.yhaguy.gestion.comun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.control.Control;
import com.coreweb.domain.IiD;
import com.coreweb.dto.Assembler;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.PlanDeCuenta;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Venta;
import com.yhaguy.gestion.articulos.ArticuloDTO;
import com.yhaguy.gestion.articulos.AssemblerArticulo;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.empresa.AssemblerCliente;
import com.yhaguy.gestion.empresa.AssemblerFuncionario;
import com.yhaguy.gestion.empresa.AssemblerProveedor;
import com.yhaguy.gestion.empresa.ClienteDTO;
import com.yhaguy.gestion.empresa.FuncionarioDTO;
import com.yhaguy.gestion.empresa.ProveedorDTO;
import com.yhaguy.gestion.transferencia.TransferenciaDetalleDTO;
import com.yhaguy.gestion.venta.AssemblerVenta;
import com.yhaguy.gestion.venta.VentaDTO;
import com.yhaguy.gestion.venta.VentaDetalleDTO;

public class ControlLogica extends Control {

	public ControlLogica(Assembler ass) {
		super(ass);
	}

	RegisterDomain rr = RegisterDomain.getInstance();

	/**
	 * Este método se encarga de crear un nuevo articuloDeposito
	 */
	private ArticuloDepositoDTO crearArticuloDeposito(Long idMov, MyArray tipoMov, MyArray articulo, MyPair deposito)
			throws Exception {

		ArticuloDepositoDTO articuloDep = new ArticuloDepositoDTO();
		AssemblerArticuloDeposito as = new AssemblerArticuloDeposito();

		articuloDep.setDeposito(deposito);
		articuloDep.setArticulo(articulo);
		articuloDep.setStockMaximo(new Long(0));
		articuloDep.setStockMinimo(new Long(0));

		ArticuloDeposito articuloDepDom = (ArticuloDeposito) as.dtoToDomain(articuloDep);
		rr.saveObject(articuloDepDom, this.getLoginNombre());
		articuloDep = (ArticuloDepositoDTO) as.domainToDto(articuloDepDom);

		return articuloDep;

	}

	/**
	 * actualiza el articulo deposito a partir de una nota de credito compra..
	 */
	public void actualizarArticuloDepositoNotaCreditoCompra(Long idMov, MyArray tipoMov, MyArray articulo,
			Long cantidad, double costo, boolean actualizarCosto, MyPair deposito, String op) throws Exception {

		this.actualizarArticuloDeposito(idMov, tipoMov, articulo, cantidad, 0, 0, 0, 0, costo, 0, actualizarCosto,
				deposito, op, null, false, true);
	}

	/**
	 * Este método se encarga de actualizar un articuloDeposito a partir de una
	 * transferencia..
	 */
	public void actualizarArticuloDepositoTransferencia(Long idMov, MyArray tipoMov, MyArray articulo, Long cantidad,
			double costo, boolean actualizarCosto, MyPair deposito, String op, ReservaDetalleDTO reservaDet)
					throws Exception {

		this.actualizarArticuloDeposito(idMov, tipoMov, articulo, cantidad, 0, 0, 0, 0, costo, 0, actualizarCosto,
				deposito, op, reservaDet, true, false);
	}

	/**
	 * Este método se encarga de actualizar un articuloDeposito a partir de una
	 * compra..
	 */
	public void actualizarArticuloDepositoCompra(Long idMov, MyArray tipoMov, MyArray articulo, Long cantidad,
			double costo, boolean actualizarCosto, MyPair deposito, String op) throws Exception {

		this.actualizarArticuloDeposito(idMov, tipoMov, articulo, cantidad, 0, 0, 0, 0, costo, 0, actualizarCosto,
				deposito, op, null, true, true);
	}

	/**
	 * Este método se encarga de actualizar un articuloDeposito, ya sea por
	 * disminución o por aumento de su stock..
	 */
	private void actualizarArticuloDeposito(Long idMov, MyArray tipoMov, MyArray articulo, Long cantidad,
			double costoCIFGs, double costoCIFDs, double costoFOBGs, double costoFOBDs, double costoFinalGs,
			double costoFinalDs, boolean actualizarCosto, MyPair deposito, String op, ReservaDetalleDTO reservaDet,
			boolean isCompra, boolean isCostoIvaIncluido) throws Exception {

		// recupera el articuloDep correspondiente
		ArticuloDeposito articuloDep = rr.recuperarArticuloDep(deposito.getId(), articulo.getId());
		AssemblerArticuloDeposito ass = new AssemblerArticuloDeposito();
		ArticuloDepositoDTO articuloDepDto;

		if (articuloDep == null) {
			articuloDepDto = this.crearArticuloDeposito(idMov, tipoMov, articulo, deposito);
		} else {
			articuloDepDto = (ArticuloDepositoDTO) ass.domainToDto(articuloDep);
		}

		// asociar la reservaDetalle con el ArticuloDeposito
		if (reservaDet != null) {
			reservaDet.setArticuloDeposito(articuloDepDto.toMyArray());
		}
		
		long cantidadFinal;

		// verifica si suma o resta
		if (op.compareTo(Configuracion.OP_SUMA) == 0) {
			cantidadFinal = articuloDepDto.getStock() + cantidad;
		} else {
			cantidadFinal = articuloDepDto.getStock() - cantidad;
			cantidad = cantidad * -1;
		}
		
		if (isCompra == true) {
			articuloDepDto.setStock(cantidadFinal);
		}
		
		if (actualizarCosto == true) {
			ControlArticuloCosto.actualizarCosto(articulo.getId(), costoFinalGs, this.getLoginNombre());
		}

		// guarda el articuloDepDto
		articuloDepDto = (ArticuloDepositoDTO) this.saveDTO(articuloDepDto, ass);

		// crea el movimiento correspondiente en el stock del articulo
		if (isCompra == true) {
			ControlArticuloStock.addMovimientoStock(idMov, tipoMov.getId(),
					cantidad, articuloDepDto.getId(), this.getLoginNombre());
		}	
	}

	/**
	 * Este método es el método general de creación de reservas, que recibe la
	 * lista de detalles ya en formato de ReservaDetalleDTO. A cada
	 * reservaDetalle le asigna el ArticuloDeposito correspondiente..
	 */
	private ReservaDTO crearReserva(Long idMov, MyArray tipoMov, MyPair tipo, MyPair estado, Date fecha,
			MyArray funcionarioCreador, MyPair depositoSalida, List<ReservaDetalleDTO> detallesR) throws Exception {
		ReservaDTO reservaDto = new ReservaDTO();
		reservaDto.setFecha(fecha);
		reservaDto.setTipoReserva(tipo);
		reservaDto.setEstadoReserva(estado);
		reservaDto.setFuncionarioEmisor(funcionarioCreador);
		reservaDto.setDepositoSalida(depositoSalida);
		reservaDto.setDetalles(detallesR);

		for (ReservaDetalleDTO item : detallesR) {
			this.actualizarArticuloDeposito(idMov, tipoMov, item.getArticulo(), item.getCantidadReservada(), 0, 0, 0, 0,
					0, 0, false, reservaDto.getDepositoSalida(), Configuracion.OP_RESTA, item, false, false);
		}
		return reservaDto;
	}

	/******************************************************************************/

	/******************************** FUNCIONARIOS ********************************/

	private static String[] FUNCIONARIO_ATRIBUTOS = new String[] { "empresa.nombre" };
	private static String[] FUNCIONARIO_COLUMNAS = new String[] { "Nombre" };

	/**
	 * @param nombre
	 * @return MyArray con la informacion del funcionario pos1:nombre
	 * @throws Exception
	 */
	public MyArray buscarFuncionario(String nombre) throws Exception {
		MyArray out = null;

		BuscarElemento b = new BuscarElemento();
		b.setClase(Funcionario.class);
		b.setAtributos(FUNCIONARIO_ATRIBUTOS);
		b.setNombresColumnas(FUNCIONARIO_COLUMNAS);
		b.setTitulo("Buscar Funcionario");
		b.setWidth("400px");
		b.show(nombre);
		if (b.isClickAceptar()) {
			out = b.getSelectedItem();
		}

		return out;
	}

	/**
	 * Retorna un FuncionarioDTO a partir del nombre (equivalente a razonSocial)
	 * 
	 * @param nombre
	 * @return
	 * @throws Exception
	 */
	public FuncionarioDTO buscarFuncionarioDTO(String nombre) throws Exception {
		FuncionarioDTO dto = null;

		MyArray ma = this.buscarFuncionario(nombre);
		if (ma != null) {
			AssemblerFuncionario ass = new AssemblerFuncionario();
			dto = (FuncionarioDTO) ass.getDTObyId(Funcionario.class.getName(), ma.getId());
		}

		return dto;
	}

	/******************************************************************************/

	/******************************** PROVEEDORES *********************************/

	private static String[] PROVEEDOR_ATTRIBUTOS = new String[] { "empresa.codigoEmpresa", "empresa.razonSocial",
			"empresa.ruc", "empresa.id" };

	private static String[] PROVEEDOR_NOMBRES_COLUMNAS = new String[] { "Código", "Razón Social", "Ruc", "Id Empresa" };

	private static String[] WIDTH_COLUMNS = { "0px", "40%", "10%", "0px", "0px", "0px", "0px", "0px", "0px", "40%", "0px", "0px" };

	/**
	 * 
	 * @param codigo
	 * @param razonSocial
	 * @param ruc
	 * @param posFiltro
	 *            : Filtro de busqueda, 0 = codigo, 1 = razon social, 2 = ruc
	 * @return
	 * @throws Exception
	 */
	public ProveedorDTO buscarProveedorDTO(String codigo, String razonSocial, String ruc, int posFiltro, String where)
			throws Exception {
		ProveedorDTO dto = null;

		MyArray ma = this.buscarProveedor(codigo, razonSocial, ruc, posFiltro, where);
		if (ma != null) {
			AssemblerProveedor ass = new AssemblerProveedor();
			dto = (ProveedorDTO) ass.getDTObyId(Proveedor.class.getName(), ma.getId());
		}
		return dto;
	}

	/**
	 * 
	 * @param codigo
	 * @param razonSocial
	 * @param ruc
	 * @param posFiltro
	 *            : Filtro de busqueda, 0 = codigo, 1 = razon social, 2 = ruc
	 * @param where
	 * @return Una MyArray con la información completa del Cliente. Las
	 *         posiciones son: Codigo, Razon Social, Ruc
	 * @throws Exception
	 */
	public MyArray buscarProveedor(String codigo, String razonSocial, String ruc, int posFiltro, String where)
			throws Exception {
		MyArray out = null;
		String dato = "";
		switch (posFiltro) {
		case 0:
			dato = codigo;
			break;
		case 1:
			dato = razonSocial;
			break;
		case 2:
			dato = ruc;
			break;
		}

		BuscarElemento b = new BuscarElemento();
		b.setClase(Proveedor.class);
		b.setAtributos(PROVEEDOR_ATTRIBUTOS);
		b.setTitulo("Buscar Proveedor");
		b.setNombresColumnas(PROVEEDOR_NOMBRES_COLUMNAS);
		b.setAnchoColumnas(WIDTH_COLUMNS);
		if (where.trim().length() > 0) {
			b.addWhere(where);
		}
		b.setWidth("900px");
		b.show(dato, posFiltro);
		if (b.isClickAceptar() == true) {
			out = b.getSelectedItem();
		}

		return out;
	}

	// Busca el ProveedorDto segun la razonSocial
	public ProveedorDTO buscarProveedorDTO_RazonSocial(String razonSocial, String where) throws Exception {
		return this.buscarProveedorDTO("", razonSocial, "", 1, where);
	}

	// Busca el ProveedorDto segun el ruc
	public ProveedorDTO buscarProveedorDTO_Ruc(String ruc, String where) throws Exception {
		return this.buscarProveedorDTO("", "", ruc, 2, where);
	}

	/******************************************************************************/

	/******************************* CLIENTE **************************************/

	private static String[] CLIENTE_ATTRIBUTOS = new String[] {
			"empresa.codigoEmpresa", "empresa.razonSocial", "empresa.ruc",
			"empresa.id", "empresa.id", "empresa.id", "empresa.id",
			"empresa.id", "empresa.cuentaBloqueada", "empresa.nombre", 
			"ventaCredito", "limiteCredito" };
	private static String[] CLIENTE_NOMBRES_COLUMNAS = new String[] { "Código",
			"Razón Social", "Ruc", "Id Empresa", "", "", "", "", "", "Nombre Fantasía", "", "" };

	/**
	 * 
	 * @param codigo
	 * @param razonSocial
	 * @param ruc
	 * @param posFiltro
	 *            : Filtro de busqueda, 0 = codigo, 1 = razon social, 2 = ruc
	 * @return
	 * @throws Exception
	 */
	public ClienteDTO buscarClienteDTO(String codigo, String razonSocial, String ruc, String fantasia, int posFiltro, String where)
			throws Exception {
		ClienteDTO dto = null;

		MyArray ma = this.buscarCliente(codigo, razonSocial, ruc, fantasia, posFiltro, where);
		if (ma != null) {
			AssemblerCliente ass = new AssemblerCliente();
			dto = (ClienteDTO) ass.getDTObyId(Cliente.class.getName(), ma.getId());
		}
		return dto;
	}

	/**
	 * 
	 * @param codigo
	 * @param razonSocial
	 * @param ruc
	 * @param posFiltro
	 *            : Filtro de busqueda, 0 = codigo, 1 = razon social, 2 = ruc
	 * @return Un MyArray con la información completa del Cliente. Las
	 *         posiciones son: Codigo, Razon Social, Ruc
	 * @throws Exception
	 */
	public MyArray buscarCliente(String codigo, String razonSocial, String ruc, String fantasia, int posFiltro, String where)
			throws Exception {
		MyArray out = null;
		String dato = "";
		switch (posFiltro) {
		case 0:
			dato = codigo;
			break;
		case 1:
			dato = razonSocial;
			break;
		case 2:
			dato = ruc;
			break;
		case 9:
			dato = fantasia;
			break;
		}

		BuscarElemento b = new BuscarElemento();
		b.setClase(Cliente.class);
		b.setAtributos(CLIENTE_ATTRIBUTOS);
		b.setTitulo("Buscar Cliente");
		b.setNombresColumnas(CLIENTE_NOMBRES_COLUMNAS);
		b.setAnchoColumnas(WIDTH_COLUMNS);
		b.setWidth("900px");
		if (where.trim().length() > 0) {
			b.addWhere(where);
		}
		b.show(dato, posFiltro);
		if (b.isClickAceptar() == true) {
			out = b.getSelectedItem();
		}

		return out;
	}

	public String obtenerAlertaListaDestinoPredefinida(String descripcion) {
		return (String) rr.getAlertaListaDestino(descripcion);
	}

	/******************************************************************************/

	/********************************* BANCO *************************************/

	private static String[] BANCO_ATTRIBUTOS = new String[] { "nroCuenta", "banco.descripcion" };

	private static String[] BANCO_NOMBRES_COLUMNAS = new String[] { "Numero Cuenta", "Banco" };

	public BancoCtaDTO buscarBancoCuenta() throws Exception {
		BancoCtaDTO out = new BancoCtaDTO();

		BuscarElemento b = new BuscarElemento();
		b.setClase(BancoCta.class);
		b.setAtributos(BANCO_ATTRIBUTOS);
		b.setTitulo("Buscar Banco Cuenta");
		b.setNombresColumnas(BANCO_NOMBRES_COLUMNAS);
		b.setAssembler(new AssemblerBancoCtaCte());
		b.setWidth("800px");
		b.show("");
		if (b.isClickAceptar() == true) {
			out = (BancoCtaDTO) b.getSelectedItemDTO();
		}

		return out;
	}

	/**
	 * 
	 * @param codigo
	 * @param razonSocial
	 * @param ruc
	 * @param posFiltro
	 *            : Filtro de busqueda, 0 = codigo, 1 = razon social, 2 = ruc
	 * @return
	 * @throws Exception
	 */
	public BancoCtaDTO buscarCuentaBancoDTO(String nroCuenta, String banco, String tipo, String moneda, int posFiltro,
			String where) throws Exception {
		BancoCtaDTO dto = null;

		MyArray ma = this.buscarCuentaBanco(nroCuenta, banco, tipo, moneda, posFiltro, where);
		if (ma != null) {
			AssemblerBancoCtaCte ass = new AssemblerBancoCtaCte();
			dto = (BancoCtaDTO) ass.getDTObyId(BancoCta.class.getName(), ma.getId());
		}
		return dto;
	}

	private static String[] BANCO_CTA_ATRIBUTOS = new String[] { "nroCuenta", "banco.descripcion", "tipo.descripcion", "moneda.descripcion" };

	private static String[] BANCO_CTA_NOMBRES_COLUMNAS = new String[] { "Nro. Cuenta", "Banco", "Tipo de Cuenta",
			"Moneda" };

	public MyArray buscarCuentaBanco(String nroCuenta, String banco, String tipo, String moneda, int posFiltro,
			String where) throws Exception {
		MyArray out = null;
		String dato = "";
		switch (posFiltro) {
		case 0:
			dato = nroCuenta;
			break;
		case 1:
			dato = banco;
			break;
		}

		BuscarElemento b = new BuscarElemento();
		b.setClase(BancoCta.class);
		b.setAtributos(BANCO_CTA_ATRIBUTOS);
		b.setTitulo("Buscar Cuenta Bancaria");
		b.setNombresColumnas(BANCO_CTA_NOMBRES_COLUMNAS);
		b.setWidth("900px");
		if (where.trim().length() > 0) {
			b.addWhere(where);
		}
		b.show(dato, posFiltro);
		if (b.isClickAceptar() == true) {
			out = b.getSelectedItem();
		}

		return out;
	}

	/************************* CUENTA CONTABLE ****************************/

	private static String[] CUENTA_CONTABLE_ATRIBUTOS = new String[] { "codigo", "descripcion", "planCuenta.codigo",
			"planCuenta.descripcion" };

	private static String[] CUENTA_CONTABLE_COLUMNAS = new String[] { "Código", "Descripción", "Plan de Cuenta",
			"Descripción Plan de Cuenta" };

	private static String[] CUENTA_CONTABLE_ANCHO_COL = new String[] { "90px", "", "100px", "" };

	/**
	 * @param nombreCuenta
	 * @return MyArray con la informacion del funcionario pos1:codigo
	 *         pos2:descripcion pos3:codigo plan cuenta pos4:descripcion plan
	 *         cuenta
	 * @throws Exception
	 */
	public MyArray buscarCuentaContable(String codigo, String descripcion, int posFiltro, String where)
			throws Exception {
		MyArray out = null;

		String dato = "";
		switch (posFiltro) {
		case 0:
			dato = codigo;
			break;
		case 1:
			dato = descripcion;
			break;
		}

		BuscarElemento b = new BuscarElemento();
		b.setClase(CuentaContable.class);
		b.setAtributos(CUENTA_CONTABLE_ATRIBUTOS);
		b.setNombresColumnas(CUENTA_CONTABLE_COLUMNAS);
		b.setAnchoColumnas(CUENTA_CONTABLE_ANCHO_COL);
		b.setTitulo("Buscar Cuenta contable");
		b.setWidth("850px");
		b.addOrden("codigo");

		if (where != null) {
			b.addWhere(where);
		}

		b.show(dato, posFiltro);
		if (b.isClickAceptar()) {
			out = b.getSelectedItem();
		}
		return out;
	}

	/************************* CUENTA CONTABLE ****************************/

	private static String[] PLAN_DE_CUENTA_ATRIBUTOS = new String[] { "codigo", "descripcion" };

	private static String[] PLAN_DE_CUENTA_COLUMNAS = new String[] { "Código", "Descripción" };

	private static String[] PLAN_DE_CUENTA_ANCHO_COL = new String[] { "90px", "" };

	/**
	 * @param nombrePlanCuenta
	 * @return MyArray con la informacion del plan de cuenta pos1:codigo
	 *         pos2:descripcion
	 * @throws Exception
	 */
	public MyArray buscarPlanCuenta(String codigo, String descripcion, int posFiltro, String where) throws Exception {
		MyArray out = null;

		String dato = "";
		switch (posFiltro) {
		case 0:
			dato = codigo;
			break;
		case 1:
			dato = descripcion;
			break;
		}

		BuscarElemento b = new BuscarElemento();
		b.setClase(PlanDeCuenta.class);
		b.setAtributos(PLAN_DE_CUENTA_ATRIBUTOS);
		b.setNombresColumnas(PLAN_DE_CUENTA_COLUMNAS);
		b.setAnchoColumnas(PLAN_DE_CUENTA_ANCHO_COL);
		b.setTitulo("Buscar Plan de Cuenta");
		b.setWidth("850px");
		b.addOrden("codigo");

		if (where != null) {
			b.addWhere(where);
		}

		b.show(dato, posFiltro);
		if (b.isClickAceptar()) {
			out = b.getSelectedItem();
		}
		return out;
	}

	/********************************** RESERVAS **********************************/

	/**
	 * Este metodo crea una reserva para un Pedido de Venta..
	 */
	public ReservaDTO crearReservaVentaPedido(Long idMov, MyArray tipoMov, MyPair tipo, MyPair estado, Date fecha,
			MyArray funcionarioCreador, MyPair depositoSalida, List<VentaDetalleDTO> itemsPedido) throws Exception {

		List<ReservaDetalleDTO> itemsReserva = new ArrayList<ReservaDetalleDTO>();

		for (VentaDetalleDTO item : itemsPedido) {
			long cantReserva = item.getCantidad();

			// creacion de los detalles de la reserva
			if (item.isServicio() == false) {
				ReservaDetalleDTO resDet = new ReservaDetalleDTO();
				resDet.setCantidadReservada(cantReserva);
				resDet.setArticulo(item.getArticulo());
				resDet.setIdDetalleOrigen(item.getId());
				itemsReserva.add(resDet);
			}
		}

		ReservaDTO reservaDto = this.crearReserva(idMov, tipoMov, tipo, estado, fecha, funcionarioCreador,
				depositoSalida, itemsReserva);
		return reservaDto;
	}

	/**
	 * Crea la reserva para una transferencia..
	 */
	public ReservaDTO crearReservaTransferencia(Long idMov, MyArray tipoMov, MyPair tipo, MyPair estado, Date fecha,
			MyArray funcionarioCreador, MyPair depositoSalida, List<TransferenciaDetalleDTO> items) throws Exception {

		List<ReservaDetalleDTO> itemsReserva = new ArrayList<ReservaDetalleDTO>();

		for (TransferenciaDetalleDTO itemTransf : items) {
			ReservaDetalleDTO item = new ReservaDetalleDTO();
			item.setCantidadReservada(itemTransf.getCantidad());
			item.setArticulo(itemTransf.getArticulo());
			item.setIdDetalleOrigen(itemTransf.getId());
			itemsReserva.add(item);
		}
		ReservaDTO reservaDto = this.crearReserva(idMov, tipoMov, tipo, estado, fecha, funcionarioCreador,
				depositoSalida, itemsReserva);
		return reservaDto;
	}

	/******************************************************************************/

	/**************************
	 * DISPONIBILIDAD DE STOCK
	 ***************************/

	/**
	 * Retorna la disponibilidad de stock a partir del id del articulo, el id
	 * del deposito..
	 */
	public long stockDisponible(long idArt, long idDep) throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		long stock = rr.getStockDisponible(idArt, idDep);

		return stock;
	}

	/******************************************************************************/

	/**************************
	 * LIBERAR/MODIFICAR RESERVAS
	 ************************/

	public final static int LIBERAR_RESERVA = 1;
	public final static int MODIFICAR_RESERVA = 2;
	public final static int VERIFICAR_RESERVA = 3;
	public final static int FINALIZAR_RESERVA = 4;

	/**
	 * Libera o modifica una Reserva segun la operacion que recibe como
	 * parametro.. Retorna un arreglo de objetos donde: [0]: booleano que indica
	 * que se pudo realizar la operacion.. [1]: mensaje..
	 */
	public Object[] modificarReserva(ReservaDTO reserva, int operacion) throws Exception {

		boolean correcto = true;
		String mensaje = "";

		MyPair deposito = reserva.getDepositoSalida();

		// antes de liberar o modificar verifica los datos..
		if ((operacion != LIBERAR_RESERVA) && (operacion != FINALIZAR_RESERVA)) {
			for (ReservaDetalleDTO item : reserva.getDetalles()) {
				Object[] obj = (Object[]) modificarReservaItem(item, deposito, VERIFICAR_RESERVA);

				correcto = (boolean) obj[0];
				mensaje = (String) obj[1];

				if (correcto == false) {
					return new Object[] { correcto, mensaje };
				}
			}
		}

		if (operacion == FINALIZAR_RESERVA) {
			reserva.setEstadoReserva(getUtilDto().getEstado_reservaFinalizada());
			reserva.setReadonly();
		}

		// si pasa la verificacion correctamente modifica los items
		for (ReservaDetalleDTO item : reserva.getDetalles()) {
			modificarReservaItem(item, deposito, operacion);
		}
		this.saveDTO(reserva, new AssemblerReserva());

		return new Object[] { correcto, mensaje };
	}

	/**
	 * Libera o modifica la reserva del Item que recibe como parametro.. Guarda
	 * en la BD el cambio en el ArticuloDeposito correspondiente.. Retorna un
	 * arreglo de objetos donde: [0]: booleano que indica si se pudo realizar la
	 * operacion.. [1]: mensaje..
	 */
	public Object[] modificarReservaItem(ReservaDetalleDTO item,
			MyPair deposito, int operacion) throws Exception {

		boolean correcto = true;
		String mensaje = "";

		AssemblerArticuloDeposito ass = new AssemblerArticuloDeposito();
		Long idArticulo = item.getArticulo().getId();
		Long idDeposito = deposito.getId();

		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloDeposito ad = rr.getArticuloDeposito(idArticulo, idDeposito);
		ArticuloDepositoDTO art = (ArticuloDepositoDTO) ass.domainToDto(ad);
		//long stock = 0;

		switch (operacion) {

		case VERIFICAR_RESERVA:

			if (ad == null) {
				correcto = false;
				mensaje = "No se encontró el ArticuloDeposito..";

			} else if ((art.getStock() - item.getCantidadActualizada()) < 0) {
				correcto = false;
				mensaje = "Stock insuficiente..";
			}

			break;

		case LIBERAR_RESERVA:

			//stock = art.getStock() + item.getCantidadReservada();
			//art.setStock(stock);
			item.setCantidadReservada(0);
			this.saveDTO(art, ass);

			break;

		case MODIFICAR_RESERVA:

			//stock = art.getStock() - item.getCantidadActualizada();
			//art.setStock(stock);
			this.saveDTO(art, ass);

			break;
		}

		return new Object[] { correcto, mensaje };
	}

	/******************************************************************************/

	/**
	 * @return booleano que indica si el deposito que viene como parametro esta
	 *         contenido en la sucursal.
	 */
	public boolean isDepositoEnSucursal(IiD deposito, IiD sucursal) throws Exception {
		boolean out = false;
		RegisterDomain rr = RegisterDomain.getInstance();
		SucursalApp suc = rr.getSucursalAppById(sucursal.getId());
		for (Deposito dep : suc.getDepositos()) {
			if (dep.getId().compareTo(deposito.getId()) == 0) {
				out = true;
			}
		}
		return out;
	}

	/**************************** BUSCAR ARTICULO *****************************/

	private static String[] ARTICULO_ATRIBUTOS = new String[] { "codigoInterno", "descripcion", "articuloFamilia",
			"articuloMarca", "articuloParte", "articuloLinea" };

	private static String[] ARTICULO_NOMBRES_COLUMNAS = new String[] { "Código interno", "Descripción" };

	/**
	 * retorna MyArray con las siguientes posiciones
	 * 
	 * @param codigoInterno
	 * @param descripcion
	 * @param posFiltro
	 * @param where
	 * @return pos1:codigoInterno,pos2:descripcion,pos3:articuloFamilia,pos4:
	 *         articuloMarca,pos5:articuloParte,pos6:articuloLinea
	 * @throws Exception
	 */
	public MyArray buscarArticuloMyArray(String codigoInterno, String descripcion, int posFiltro, String where)
			throws Exception {
		MyArray out = null;
		String dato = "";
		switch (posFiltro) {
		case 0:
			dato = codigoInterno;
			break;
		case 1:
			dato = descripcion;
			break;
		}

		BuscarElemento b = new BuscarElemento();
		b.setClase(Articulo.class);
		b.setAtributos(ARTICULO_ATRIBUTOS);
		b.setTitulo("Buscar Articulo");
		b.setNombresColumnas(ARTICULO_NOMBRES_COLUMNAS);
		if (where.trim().length() > 0) {
			b.addWhere(where);
		}
		b.setWidth("800px");
		b.show(dato, posFiltro);
		if (b.isClickAceptar() == true) {
			out = b.getSelectedItem();
		}

		return out;
	}

	/**
	 * retorna dto de articulo
	 * 
	 * @param codigoInterno
	 * @param descripcion
	 * @param posFiltro
	 * @param where
	 * @return
	 * @throws Exception
	 */
	public ArticuloDTO buscarArticuloDTO(String codigoInterno, String descripcion, int posFiltro, String where)
			throws Exception {
		ArticuloDTO dto = null;

		MyArray ma = this.buscarArticuloMyArray(codigoInterno, descripcion, posFiltro, where);
		if (ma != null) {
			AssemblerArticulo ass = new AssemblerArticulo();
			dto = (ArticuloDTO) ass.getDTObyId(Articulo.class.getName(), ma.getId());
		}
		return dto;
	}

	/******************************************************************************/

	/**
	 * Devuelve una venta a partir del tipoMovimiento y el id del movimiento
	 * 
	 * @throws Exception
	 */
	public VentaDTO buscarVenta(long idTipoMovimiento, long idMovimiento) throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		Venta ven = rr.getMovimientoVenta(idTipoMovimiento, idMovimiento);
		AssemblerVenta ass = new AssemblerVenta();
		VentaDTO venDTO = new VentaDTO();
		venDTO = (VentaDTO) ass.domainToDto(ven);

		return venDTO;
	}

	private UtilDTO getUtilDto() {
		return (UtilDTO) getDtoUtil();
	}
}
