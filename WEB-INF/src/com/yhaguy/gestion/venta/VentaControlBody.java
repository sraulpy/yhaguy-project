package com.yhaguy.gestion.venta;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.domain.IiD;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.comun.ControlArticuloStock;
import com.yhaguy.gestion.comun.ControlLogica;
import com.yhaguy.gestion.comun.ReservaDTO;
import com.yhaguy.gestion.comun.ReservaDetalleDTO;
import com.yhaguy.gestion.empresa.ClienteDTO;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class VentaControlBody extends BodyApp {	
	
	final static String TIPO_PARAM = "tipo";
	final static String TIPO_PEDIDO = "pedido";
	final static String TIPO_PRESUPUESTO = "presupuesto";
	final static String TIPO_FACTURA = "factura";
	
	final static String LABEL_TOTAL_A_PAGAR = "Total a Pagar:";
	final static String LABEL_DESCUENTO = "Descuento:";
	final static String LABEL_IVA_10 = "IVA 10%:";
	final static String LABEL_IVA_5 = "IVA 5%:";
	
	final static String ZUL_DATOS_REPARTO = "/yhaguy/gestion/venta/DatosReparto.zul";
	final static String ZUL_ITEM_SERVICIO = "/yhaguy/gestion/venta/itemServicio.zul";
	final static String TITULO_IMPORTAR_PRESUPUESTO = "Presupuestos Pendientes";
	
	final static String ALTA_CLIENTE_OCASIONAL = "Alta de Cliente Ocasional";
	final static String EDIT_CLIENTE_OCASIONAL = "Editar Cliente Ocasional";
	
	final static int STOCK_DISPONIBLE_OK = 0;
	final static int STOCK_DISPONIBLE_MSG = 1;
	
	private MyArray tipoMovimiento; 	
	private MyArray estado;
	private String tipo;

	private AssemblerVenta assembler = new AssemblerVenta();
	private VentaDTO dto = new VentaDTO();
	private VentaDTO presupuestoTmp;
	
	private List<VentaDetalleDTO> selectedItems;
	private VentaDetalleDTO nvoItem;
	private ReciboFormaPagoDTO nvoFormaPago = new ReciboFormaPagoDTO();
	
	@Wire
	private Textbox txNro;
	
	@Wire
	private HtmlBasedComponent bruc;
	
	@Wire
	private Button btnFormaPago;

	@Init(superclass = true)
	public void init(@ExecutionParam(TIPO_PARAM) String tipo) {
		this.tipo = tipo;
		this.dto.setMoneda(this.monedaLocal);
		
		if (tipo.compareTo(TIPO_PEDIDO) == 0) {
			this.tipoMovimiento = this.tm_PedidoVenta;
			this.dto.setTipoMovimiento(this.tipoMovimiento);
			this.estado = this.estado_SoloPedido;
			this.setTextoFormularioCorriente("Pedido de Venta");
		}
		
		if (tipo.compareTo(TIPO_PRESUPUESTO) == 0) {
			this.tipoMovimiento = this.tm_PresupuestoVenta;
			this.dto.setTipoMovimiento(this.tipoMovimiento);
			this.estado = this.estado_SoloPresupuesto;
			this.setTextoFormularioCorriente("Presupuesto de Venta");
		}
		
		if (tipo.compareTo(TIPO_FACTURA) == 0) {
			this.tipoMovimiento = this.tm_FacturaContado;
			this.dto.setTipoMovimiento(this.tipoMovimiento);
			this.estado = this.estado_PedidoFacturado;
			this.setTextoFormularioCorriente("Factura de Venta");
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {		
	}

	@Override
	public Assembler getAss() {
		return this.assembler;
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {		
		this.dto = (VentaDTO) dto;
		this.tipoMovimiento = this.dto.getTipoMovimiento();
		this.selectedItems = new ArrayList<VentaDetalleDTO>();	
		
		if (this.dto.getEstadoComprobante() != null) {			
			this.enmascararAnulados(true);
		} else {
			this.enmascararAnulados(false);		
		}	
		
		if (this.dto.isFacturaContado()) {
			this.btnFormaPago.setDisabled(false);
			this.btnFormaPago.setVisible(true);
		} else {
			this.btnFormaPago.setVisible(false);
		}
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		VentaDTO out = new VentaDTO();
		this.sugerirValores(out);
		this.bruc.focus();
		return out;
	}

	@Override
	public String getEntidadPrincipal() {
		return Venta.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}	
	
	@Override
	public Browser getBrowser() {
		String sigla = "'" + this.tipoMovimiento.getPos2() + "'";
		String where = "tipoMovimiento.sigla like ";
		String aux;
		
		if (this.tipoMovimiento.compareTo(tm_FacturaContado) == 0) {
			aux = "'" + this.tm_FacturaCredito.getPos2() + "'";
			where += sigla + " or " + where + aux;
		} else if (this.tipoMovimiento.compareTo(tm_FacturaCredito) == 0) {
			aux = "'" + this.tm_FacturaContado.getPos2() + "'";
			where += sigla + " or " + where + aux;
		} else {
			where += sigla;
		}		
		return new VentaBrowser(where, this.tipo);
	}	
	
	@Override
	public void showInformacion() throws Exception { 
		this.informacionAdicional();				
	}
	
	@Override
	public boolean getInformacionDeshabilitado(){
		return this.dto.esNuevo();
	}
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return this.dto.esNuevo();
	}
	
	@Override
	public void showImprimir() {
		boolean imprimirPrecio = this.mensajeSiNo("Imprimir Precios e Importes..?");
		try {
			this.imprimirVenta(imprimirPrecio);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/************************** COMANDOS ***************************/
	
	@Command 
	@NotifyChange("*")
	public void pasarAPedido() throws Exception {
		this.pasarAPedidoVenta();
	}	
	
	@Command 
	@NotifyChange("*")
	public void buscarVendedor() throws Exception {		
		String nombre = (String) this.dto.getVendedor().getPos1();
		MyArray vendedor = this.ctr.buscarFuncionario(nombre);
		if (vendedor != null) {
			this.dto.setVendedor(vendedor);
		} else {
			this.dto.setVendedor(this.dto.getVendedor());
		}		
	}
	
	@Command 
	@NotifyChange("*")
	public void addClienteOcasional() throws Exception {
		this.add_clienteOcasional();
	}	
	
	@Command 
	@NotifyChange("*")
	public void eliminarItem() throws Exception {
		this.deleteItem();
	}	
	
	@Command
	@NotifyChange("*")
	public void insertarServicio() throws Exception {
		this.insertServicio();
		this.bruc.focus();
	}
	
	@Command
	@NotifyChange("*")
	public void formaDePago() throws Exception {
		this.asignarFormaPago();
	}
	
	/***************************************************************/
	
	
	/************************** FUNCIONES **************************/
	
	/**
	 * pasa a Pedido de Venta..
	 */
	private void pasarAPedidoVenta() throws Exception {
		// verifica si es posible realizar reservas..
		Object[] stockDisp = this.verificarStock(this.dto.getDetalles());
		boolean stockDispOk = (boolean) stockDisp[STOCK_DISPONIBLE_OK];
		String stockDispMsg = (String) stockDisp[STOCK_DISPONIBLE_MSG];
		if (stockDispOk == false) {
			this.mensajeError(stockDispMsg);
			return;
		}

		if (mensajeSiNo("Está seguro de pasar a Pedido el Presupuesto?") == true) {
			// Controlar si es posible la reserva de items..

			this.presupuestoTmp = this.dto;
			this.estado = this.estado_SoloPedido;
			this.setDTOCorriente(this.crearPedidoDesdePresupuesto(this.dto,
					false));
			this.setTextoFormularioCorriente("Pedido de Venta");
			this.tipo = TIPO_PEDIDO;
			this.dto = (VentaDTO) this.saveDTO(this.dto);

			this.presupuestoTmp.setIdEnlaceSiguiente(this.dto.getId());
			this.presupuestoTmp.setNumeroPedido(this.dto.getNumero());
			this.presupuestoTmp.setReadonly();
			this.presupuestoTmp.setEstado(this.utilDto.getEstadoVenta_pasadoApedido());
			this.saveDTO(this.presupuestoTmp);
			this.crearReserva(this.dto, getDtoUtil().getReservaVenta());
			this.getCtrAgenda().addDetalle(ControlAgendaEvento.NORMAL,
					this.getCtrAgendaKey(), 0,
					EVENTO_PASAR_A_PEDIDO + this.dto.getNumero(), null);
			this.mensajePopupTemporal("Información Grabada..", 1000);
			this.setEstadoABMConsulta();
		}
	}
	
	/**
	 * Retorna el Pedido creado a partir del Presupuesto que 
	 * recibe como parámetro..
	 */
	public VentaDTO crearPedidoDesdePresupuesto(IiD presupuesto, 
			boolean grabar) throws Exception{
		
		return crearVentaDesde(presupuesto, this.estado_SoloPedido, 
				this.tm_PedidoVenta, true, false, false, grabar, "", "").get(0);
	}
	
	/**
	 * Retorna las Facturas Contado creadas a partir del Pedido que 
	 * recibe como parámetro..
	 */
	public List<VentaDTO> crearFacturaContadoDesdePedido(IiD pedido, 
			boolean grabar, String numeroPlanillaCaja, String timbrado) throws Exception{		
		return crearVentaDesde(pedido, this.estado_PedidoFacturado, 
				this.tm_FacturaContado, false, true, false, grabar, numeroPlanillaCaja, timbrado);
	}
	
	/**
	 * Retorna las Facturas Credito creadas a partir del Pedido que 
	 * recibe como parámetro..
	 */
	public List<VentaDTO> crearFacturaCreditoDesdePedido(IiD pedido, 
			boolean grabar, String numeroPlanillaCaja, String timbrado) throws Exception{
		return crearVentaDesde(pedido, this.estado_PedidoFacturado, 
				this.tm_FacturaCredito, false, false, true, grabar, numeroPlanillaCaja, timbrado);
	}	
	
	/**
	 * Crea el Pedido o Facturas de Venta a partir de otro..
	 */
	private List<VentaDTO> crearVentaDesde(IiD venta, MyArray estado,
			MyArray tipoMovimiento, boolean crearPedido,
			boolean crearFacturaContado, boolean crearFacturaCredito,
			boolean grabar, String numeroPlanillaCaja, String timbrado) throws Exception {

		VentaDTO desde = null;

		if ((venta instanceof VentaDTO) == false) {
			desde = (VentaDTO) this.getDTOById(Venta.class.getName(), venta.getId());
		} else {
			desde = (VentaDTO) venta;
		}

		List<VentaDTO> ventas = new ArrayList<VentaDTO>();

		if (crearPedido == false) {
			for (int i = 1; i <= desde.getCantidadFacturas_a_generar(); i++) {
				ventas.add(this.crearVentaDesde(desde, estado, tipoMovimiento,
						crearPedido, crearFacturaContado, crearFacturaCredito,
						grabar, i, numeroPlanillaCaja, timbrado));
			}
		} else {
			ventas.add(this.crearVentaDesde(desde, estado, tipoMovimiento,
					crearPedido, crearFacturaContado, crearFacturaCredito,
					grabar, 1, numeroPlanillaCaja, timbrado));
		}		

		return ventas;
	}
	
	
	/**
	 * Crea el Pedido o Facturas de Venta a partir de otro..
	 */
	private VentaDTO crearVentaDesde(VentaDTO desde, MyArray estado,
			MyArray tipoMovimiento, boolean crearPedido,
			boolean crearFacturaContado, boolean crearFacturaCredito,
			boolean grabar, int desglose, String numeroPlanillaCaja, String timbrado) throws Exception {

		VentaDTO out = new VentaDTO();
		out.setCliente(desde.getCliente());
		out.setCondicionPago(desde.getCondicionPago());
		out.setDeposito(desde.getDeposito());
		out.setVendedor(desde.getVendedor());
		out.setDetalles(this.crearDetalleDesde(desde.getDetallesDesglose(desglose)));
		out.setEstado(estado);
		out.setFecha(new Date());
		out.setMoneda(desde.getMoneda());
		out.setTipoCambio(desde.getTipoCambio());
		out.setTipoMovimiento(tipoMovimiento);
		out.setSucursal(desde.getSucursal());
		out.setVencimiento(desde.getVencimiento());
		out.setModoVenta(desde.getModoVenta());
		out.setReparto(desde.isReparto());
		out.setPuntoPartida(desde.getPuntoPartida());
		out.setFechaTraslado(desde.getFechaTraslado());
		out.setFechaFinTraslado(desde.getFechaFinTraslado());
		out.setRepartidor(desde.getRepartidor());
		out.setCedulaRepartidor(desde.getCedulaRepartidor());
		out.setMarcaVehiculo(desde.getMarcaVehiculo());
		out.setChapaVehiculo(desde.getChapaVehiculo());
		out.setDenominacion(desde.getDenominacion());
		out.setNumeroPlanillaCaja(numeroPlanillaCaja);
		out.setPreparadoPor(desde.getPreparadoPor());
		out.setObservacion(desde.getObservacion());
		out.setTimbrado(timbrado);

		if (crearPedido == true) {
			out.setAtendido(this.getAcceso().getFuncionario());
			out.setNumeroPresupuesto(desde.getNumero());
			out.setNumero(Configuracion.NRO_VENTA_PEDIDO
					+ "-"
					+ AutoNumeroControl.getAutoNumero(
							Configuracion.NRO_VENTA_PEDIDO, 7));

		} else if (crearFacturaContado == true) {
			out.setDeposito(new MyPair(Deposito.ID_DEPOSITO_PRINCIPAL));
			out.setAtendido(desde.getAtendido());
			out.setNumeroPresupuesto(desde.getNumeroPresupuesto());
			out.setNumeroPedido(desde.getNumero());
			out.setFormasPago(desde.getFormasPago());
			out.setReadonly();
			out.setNumero(desde.getNumerosFacturas().get(desglose - 1));

		} else if (crearFacturaCredito == true) {
			out.setDeposito(new MyPair(Deposito.ID_DEPOSITO_PRINCIPAL));
			out.setAtendido(desde.getAtendido());
			out.setNumeroPresupuesto(desde.getNumeroPresupuesto());
			out.setNumeroPedido(desde.getNumero());
			out.setReadonly();
			out.setNumero(desde.getNumerosFacturas().get(desglose - 1));

		} else {
			throw new Exception("Error al invocar el método [crearVentaDesde]");
		}

		if (grabar == true) {
			out = (VentaDTO) this.saveDTO(out, new AssemblerVenta());
			desde.setEstado(crearPedido ? estado_Pasado_a_Pedido
					: estado_PedidoFacturado);
			desde.setNumeroPedido(crearPedido ? out.getNumero() : "");
			desde.setNumeroFactura(crearPedido ? "" : out.getNumero());
			// el pedido no hace referencia a formas de pago..
			desde.setFormasPago(new ArrayList<ReciboFormaPagoDTO>());
			desde.setReadonly();
			desde.setIdEnlaceSiguiente(out.getId());
			desde = (VentaDTO) this.saveDTO(desde, new AssemblerVenta());
			
			if (crearPedido == false) {
				for (VentaDetalleDTO item : out.getDetalles()) {
					RegisterDomain rr = RegisterDomain.getInstance();
					ArticuloDeposito adp = rr.getArticuloDeposito(item.getArticulo().getId(), Configuracion.ID_DEPOSITO_PRINCIPAL);
					ControlArticuloStock.actualizarStock(adp.getId(), item.getCantidad() * -1, this.getLoginNombre());
					ControlArticuloStock.addMovimientoStock(out.getId(), out
							.getTipoMovimiento().getId(), item.getCantidad()
							* -1, adp.getId(), this.getLoginNombre());
				}
			}
		}

		return out;
	}
	
	
	/**
	 * @return una Lista de Detalles con el mismo contenido de la que recibe..
	 */
	private List<VentaDetalleDTO> crearDetalleDesde(List<VentaDetalleDTO> items)
			throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<VentaDetalleDTO> out = new ArrayList<VentaDetalleDTO>();

		for (VentaDetalleDTO item : items) {
			Articulo art = rr.getArticuloById(item.getArticulo().getId());
			double costoGs = art.getCostoGs();
			if (art.getCodigoInterno().startsWith("@")) {
				costoGs = 0;
			}
			VentaDetalleDTO nvo = new VentaDetalleDTO();
			nvo.setArticulo(item.getArticulo());
			nvo.setDescripcion(item.getDescripcion());
			nvo.setCantidad(item.getCantidad());
			nvo.setCostoUnitarioGs(costoGs);
			nvo.setCostoUnitarioDs(item.getCostoUnitarioDs());
			nvo.setDescuentoUnitarioGs(item.getDescuentoUnitarioGs());
			nvo.setDescuentoUnitarioDs(item.getDescuentoUnitarioDs());
			nvo.setImpuestoFinal(item.getImpuestoFinal());
			nvo.setImpuestoUnitario(item.getImpuestoUnitario());
			nvo.setPrecioVentaUnitarioGs(item.getPrecioVentaUnitarioGs());
			nvo.setPrecioVentaUnitarioDs(item.getPrecioVentaUnitarioDs());
			nvo.setNombreRegla(item.getNombreRegla());
			nvo.setPrecioVentaFinalGs(item.getPrecioVentaFinalGs());
			nvo.setPrecioVentaFinalDs(item.getPrecioVentaFinalDs());
			nvo.setPrecioVentaFinalUnitarioGs(item.getPrecioVentaFinalUnitarioGs());
			nvo.setPrecioVentaFinalUnitarioDs(item.getPrecioVentaFinalUnitarioDs());
			nvo.setPrecioGs(item.getPrecioGs());
			nvo.setTipoIVA(item.getTipoIVA());
			nvo.setListaPrecio(item.getListaPrecio());
			
			out.add(nvo);
		}
		return out;
	}
	
	/**
	 * verifica si es posible realizar las reservas de los articulos..
	 * retorna un arreglo de objeto que...
	 * obj[0] : booleano que indica si es posible la reserva de todos los items
	 * obj[1] : String que es el mensaje con los items que no se pueden reservar
	 * */
	private Object[] verificarStock(List<VentaDetalleDTO> items) throws Exception{
		boolean out = true;
		String msgError = "Los siguientes ítems no se pueden reservar: \n";		
		long idDep = this.dto.getDeposito().getId();
		
		for (VentaDetalleDTO item : items) {
			long idArt = item.getArticulo().getId();
			long cant = item.getCantidad();
			long stock = this.ctr.stockDisponible(idArt, idDep);
			if (cant > stock) {
				out = false;
				msgError = msgError + "\n - " + item.getArticulo().getPos1() 
						+ " -pedido: " + cant + " -stock: " + stock;
			}
		}		
		return new Object[]{ out, msgError };
	}
	
	/**
	 * Agrega un cliente ocasional..
	 */
	private void add_clienteOcasional() {
		
		WindowClienteOcasional win = new WindowClienteOcasional();
		ClienteDTO clienteOcasional = this.dto.getClienteOcasional();
		win.show(WindowPopup.NUEVO, win, clienteOcasional);
		
		String evento = EVENTO_ALTA_CLIENTE_OCASIONAL;
		
		if (clienteOcasional != null) {
			evento = EVENTO_EDIT_CLIENTE_OCASIONAL;
		}
		
		if (win.isClickAceptar() == true) {				
			this.dto.setCliente(win.getClienteMyArray());
			this.dto.setClienteOcasional(win.getClienteDto());			
			this.addEventoAgenda(evento + this.dto.getCliente().getPos2());			
		}	
	}
	
	/**
	 * @return un arreglo de String.. 
	 * [0]:src del icono para la operacion alta de cliente ocasional.
	 * [1]:el texto que aparece en el tooltip.
	 */ 	
	public String[] getAddClienteOcasionalIcono() {
		
		String icon = "z-icon-plus";
		String tooltip = ALTA_CLIENTE_OCASIONAL;
		MyPair tipoCliente = null;
		MyPair tipoOcasional = null;
		
		if (this.dto.getClienteOcasional() != null) {
			tipoCliente = (MyPair) this.dto.getCliente().getPos5();
			tipoOcasional = this.utilDto.getTipoClienteOcasional();
			
			if (tipoCliente.getId().longValue() == tipoOcasional.getId().longValue()) { 
				icon = "z-icon-edit";
				tooltip = EDIT_CLIENTE_OCASIONAL;
			}
		}		
		return new String[]{icon, tooltip};
	}
	
	/**
	 * elimina el item..
	 */
	private void deleteItem() {
		// solicita confirmacion para eliminar..
		if (this.confirmarEliminarItems() == true) {
			for (VentaDetalleDTO item : this.selectedItems) {
				this.dto.getDetalles().remove(item);
			}
			this.selectedItems = null;
		}
	}
	
	/**
	 * @return el mensaje de confirmacion..
	 */
	private boolean confirmarEliminarItems() {
		// si no hay seleccionados no hace nada
		if (this.selectedItems.size() == 0) {
			return false;
		}

		String txt = "Está seguro que quiere eliminar los items: \n";
		for (VentaDetalleDTO vd : this.selectedItems) {
			txt += "\n"+vd.getArticulo().getPos1() + " - cantidad: "
					+ vd.getCantidad();
		}
		return this.mensajeSiNo(txt);
	}
	
	/**
	 * despliega la ventana de informacion adicional..
	 */
	private void informacionAdicional() throws Exception {
		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.SOLO_LECTURA);
		w.setTitulo("Información Adicional");
		w.setWidth("400px");
		w.setHigth("400px");
		w.setSoloBotonCerrar();
		w.setDato(this);
		w.show(Configuracion.INFO_ADICIONAL_VENTA_PEDIDO_ZUL);
	}
	
	/**
	 * inserta un item de Servicio..
	 */
	private void insertServicio() throws Exception {
		this.nvoItem = new VentaDetalleDTO();
		this.nvoItem.setTipoIVA(this.getIva10());
		this.buscarItemsDeServicio();
		this.nvoItem = null;
	}
	
	/**
	 * Buscador de items de servicio..
	 */
	private void buscarItemsDeServicio() throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(Articulo.class);
		b.setAtributos(new String[]{"codigoInterno", "descripcion"});		
		b.setNombresColumnas(new String[]{"Código", "Descripción"});
		b.setAnchoColumnas(new String[]{"110px", ""});
		b.setTitulo("Ítems de Servicio");
		b.setWidth("600px");
		b.addWhere("servicio = 'TRUE'");
		b.setContinuaSiHayUnElemento(false);
		b.show("%");
		if (b.isClickAceptar()) {
			MyArray art = b.getSelectedItem();
			art.setPos4(art.getPos2());
			art.setPos5(true);
			this.nvoItem.setArticulo(art);
			this.openItemServicio();
		}
	}
	
	/**
	 * Despliega la ventana para insertar un servicio..
	 */
	private void openItemServicio() throws Exception {
		WindowPopup wp = new WindowPopup();		
		wp.setDato(this);
		wp.setCheckAC(new ValidadorInsertarServicio());
		wp.setTitulo("Ítem de Servicio");
		wp.setHigth("350px");
		wp.setWidth("450px");
		wp.setModo(WindowPopup.NUEVO);		
		wp.show(ZUL_ITEM_SERVICIO);
		if (wp.isClickAceptar()) {
			this.dto.getDetalles().add(this.nvoItem);
		}
	}
	
	/**
	 * Impresion de la venta..
	 */
	private void imprimirVenta(boolean imprimirPrecio) throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();
		RegisterDomain rr = RegisterDomain.getInstance();

		for (VentaDetalleDTO item : this.dto.getDetalles()) {
			long stock = rr.getStockDisponible(item.getArticulo().getId(), 2);
			Object[] obj1 = new Object[] {
					item.getArticulo().getPos1(),
					this.getMaxLength((String) item.getArticulo().getPos4(), 28),
					item.getCantidad(), stock, item.getPrecioGs(), item.getImporteGs() };
			Object[] obj2 = new Object[] { item.getArticulo().getPos1(),
					item.getArticulo().getPos4(), item.getCantidad(), stock };
			data.add(imprimirPrecio ? obj1 : obj2);
		}

		ReporteYhaguy rep = imprimirPrecio ? new ReporteVenta(this.dto)
				: new ReporteVentaSinPrecio(this.dto);
		rep.setTitulo((String) this.dto.getTipoMovimiento().getPos1());
		rep.setDatosReporte(data);
		rep.setBorrarDespuesDeVer(true);

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/**
	 * Despliega la ventana para asignar las formas de pago..
	 */
	private void asignarFormaPago() throws Exception {

		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Asignar Formas de Pago");
		wp.setHigth("400px");
		wp.setWidth("580px");
		wp.setDato(this);
		wp.setCheckAC(new ValidadorFormaPagoVenta(this));
		wp.show(Configuracion.VENTA_LISTA_FORMA_PAGO_ZUL_);

		if (wp.isClickAceptar() == true) {
			this.addSaldoFavorCliente(this.dto);
			this.cancelSaldoFavorCliente(this.dto);
			this.dto = (VentaDTO) this.saveDTO(this.dto);			
			this.addMovimientoBanco(this.dto);
			
			Clients.showNotification("Registro actualizado..");
		}
	}
	
	/**
	 * Invoca a la API de Banco para agregar los movimientos de banco..
	 */
	private void addMovimientoBanco(VentaDTO venta) throws Exception {
		ControlBancoMovimiento ctr = new ControlBancoMovimiento(null);

		for (ReciboFormaPagoDTO formaPago : venta.getFormasPago()) {
			ctr.registrarMovimientoBanco(formaPago, venta.getFecha(),
					venta.getSucursal(), venta.getCliente().getId(),
					venta.getNumeroPlanillaCaja(), "", venta.getNumero(), 
					(String) venta.getVendedor().getPos1());
		}
	}
	
	/**
	 * agrega los saldos a favor del cliente..
	 */
	private void addSaldoFavorCliente(VentaDTO venta) throws Exception {
		for (ReciboFormaPagoDTO item : venta.getFormasPago()) {
			if (item.isSaldoFavorGenerado()) {
				RegisterDomain rr = RegisterDomain.getInstance();
				CtaCteEmpresaMovimiento ctm = new CtaCteEmpresaMovimiento();
				ctm.setFechaEmision(new Date());
				ctm.setCerrado(true);
				ctm.setIdEmpresa((long) venta.getCliente().getPos4());
				ctm.setIdMovimientoOriginal(venta.getId());
				ctm.setImporteOriginal(venta.getTotalImporteGs());
				ctm.setSaldo(item.getMontoGs() > 0 ? item.getMontoGs() * -1 : item.getMontoGs());
				ctm.setNroComprobante(venta.getNumero());
				ctm.setSucursal(rr.getSucursalAppById(2));
				ctm.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
				ctm.setTipoCaracterMovimiento(rr.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE));
				ctm.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_CTA_CTE_SALDO_FAVOR));
				rr.saveObject(ctm, this.getLoginNombre());
			}
		}
	}
	
	/**
	 * cancela los saldos a favor del cliente..
	 */
	private void cancelSaldoFavorCliente(VentaDTO venta) throws Exception {
		for (ReciboFormaPagoDTO item : venta.getFormasPago()) {
			if (item.isSaldoFavorCobrado()) {
				RegisterDomain rr = RegisterDomain.getInstance();
				CtaCteEmpresaMovimiento ctm = rr.getCtaCteEmpresaMovimientoById(item.getCtaCteSaldoFavor().getId());
				ctm.setSaldo(ctm.getSaldo() + item.getMontoGs());
				rr.saveObject(ctm, this.getLoginNombre());
			}
		}
	}
	
	/***************************************************************/
	
	
	/********************************* BUSCAR CLIENTE **********************************/

	@Command @NotifyChange("*")
	public void buscarCliente(@BindingParam("filtro") int filtro) throws Exception {
		
		// Filtro: 1 = Razón Social, 2 = RUC
		String codigo = "";
		String razonSocial = (String) this.dto.getCliente().getPos2();
		String ruc = (String) this.dto.getCliente().getPos3();
		String fantasia = (String) this.dto.getCliente().getPos4();
		MyArray cli = this.ctr.buscarCliente(codigo, razonSocial, ruc, fantasia, filtro, "");

		if (cli != null) {
			
			this.dto.setCliente(cli);
			
			if (this.dto.getClienteOcasional() != null) {
				this.dto.setClienteOcasional(null);
			}
		}
	}
	
	/***********************************************************************************/	
	
	
	/****************************** INSERTAR/EDITAR ITEM *******************************/
	
	@Command 
	@NotifyChange("*")
	public void insertarPedidoDetalle() throws Exception {		
		VentaDetalleDTO det = new VentaDetalleDTO();
		det.setTipoIVA(this.getIva10());
		det.setListaPrecio(this.getListaPrecio());
		det.getArticulo().setPos5(false);
		boolean ok = this.abrirVentanaInsertarDetalle(det, true);
		if (ok == true){
			this.dto.getDetalles().add(det);
		}
		this.bruc.focus();
	}
	
	@Command 
	@NotifyChange("*")
	public void editarPedidoDetalle(@BindingParam("det") VentaDetalleDTO det ) 
			throws Exception {
		// verificar el stock disponible
		det.setStockDisponible(this.ctr.stockDisponible(det.getArticulo().getId(), 
				this.dto.getDeposito().getId()));
		
		// serializar el objeto
		byte[] ori = this.m.serializar(det);		
		
		//false porque no puede seleccionar otro articulo para este item
		boolean ok = this.abrirVentanaInsertarDetalle(det, false); 
		if (ok == false){
			// restaurar el que estaba
			VentaDetalleDTO oriDet = (VentaDetalleDTO)this.m.deSerializar(ori); 
			
			int pos = this.dto.getDetalles().indexOf(det);
			this.dto.getDetalles().set(pos, oriDet);

		}
		
	}
	
	@Command
	public boolean abrirVentanaInsertarDetalle(VentaDetalleDTO det,
			boolean editArt) throws Exception {
		VentaItemControl vpi = new VentaItemControl();

		String modo = WindowPopup.SOLO_LECTURA;
		if (this.isDeshabilitado() == false) {
			modo = WindowPopup.NUEVO;
		}
		vpi.show(modo, det, this.dto.getDetalles(), this.dto.getCliente(),
				this.dto.getDeposito(), this.tipo, this.dto.getReserva(),
				this.getCtrAgenda(), this.getCtrAgendaKey(),
				this.dto.getVendedor(), this.dto.getSucursal(),
				this.dto.getModoVenta(), editArt, this.dto.getTipoCambio(),
				this.dto.getMoneda(), this.dto.isReparto(), this.dto);
		return vpi.isClickAceptar();
	}
	
	/***********************************************************************************/	
	
	
	/******************************* IMPORTAR PRESUPUESTO ******************************/
	
	private static String[] attPresupuesto = {"fecha", "numero", 
		"cliente.empresa.razonSocial", "atendido.empresa.razonSocial", 
		"vendedor.empresa.razonSocial"};
	
	private static String[] colPresupuesto = {"Fecha", "Número", "Cliente", 
		"Atendido", "Vendedor"};
	
	private static String[] tipos = {Config.TIPO_DATE, Config.TIPO_STRING, 
		Config.TIPO_STRING, Config.TIPO_STRING, Config.TIPO_STRING};
	
	private static String[] anchoColumnas = {"170px", "110px", "", "120px", 
		"120px"};
	
	private VentaDTO selectedPresupuesto;
	private String hoy = m.dateToString(new Date(), Misc.YYYY_MM_DD);
	
	@Command @NotifyChange("*")
	public void importarPresupuesto() throws Exception {
		
		String sigla = Configuracion.SIGLA_VENTA_ESTADO_SOLO_PRESUPUESTO;
		long idSuc = this.dto.getSucursal().getId();
		long idDep = this.dto.getDeposito().getId();
		String where = "estado.sigla like '" + sigla + "' and sucursal.id = "
				+ idSuc + " and deposito.id = " + idDep;
		
		BuscarElemento b = new BuscarElemento();
		b.setClase(Venta.class);
		b.setTitulo(TITULO_IMPORTAR_PRESUPUESTO + " [Sucursal: " 
				+ this.dto.getSucursal().getText() + "  - Depósito: " 
					+ this.dto.getDeposito().getText() + " ]");
		b.setAtributos(attPresupuesto);
		b.setNombresColumnas(colPresupuesto);
		b.setAnchoColumnas(anchoColumnas);
		b.setTipos(tipos);
		b.setHeight("400px");
		b.setWidth("960px");
		b.addWhere(where);
		b.setContinuaSiHayUnElemento(false);
		b.show(hoy);
		if (b.isClickAceptar()) {
			long id = b.getSelectedItem().getId();
			this.enlazarPresupuesto(id);			
			this.addEventoAgenda(EVENTO_IMPORTAR_PRESUPUESTO 
					+ this.dto.getNumeroPresupuesto());
			this.mensajePopupTemporal(EVENTO_IMPORTAR_PRESUPUESTO
					+ "  -  " + this.dto.getNumeroPresupuesto());
		}
	}
	
	/**
	 * Enlaza los datos del presupuesto seleccionado
	 * al Pedido de Venta Corriente..
	 */
	private void enlazarPresupuesto(long idPresupuesto) throws Exception {
		
		//convierte el presupuesto Seleccionado a DTO
		String clase = Venta.class.getName();
		this.selectedPresupuesto = (VentaDTO) this.getDTOById(clase, idPresupuesto);
		
		String numeroPresupuesto = this.selectedPresupuesto.getNumero();
		boolean reparto = this.selectedPresupuesto.isReparto();
		MyArray cliente = this.selectedPresupuesto.getCliente();
		MyArray vendedor = this.selectedPresupuesto.getVendedor();
		MyArray moneda = this.selectedPresupuesto.getMoneda();		
		
		this.dto.setNumeroPresupuesto(numeroPresupuesto);
		this.dto.setReparto(reparto);
		this.dto.setCliente(cliente);
		this.dto.setVendedor(vendedor);
		this.dto.setMoneda(moneda);
		this.dto.setReparto(reparto);
		
		for (VentaDetalleDTO item : this.selectedPresupuesto.getDetalles()) {
			VentaDetalleDTO det = new VentaDetalleDTO();
			det.setArticulo(item.getArticulo());
			det.setCantidad(item.getCantidad());
			det.setDescripcion(item.getDescripcion());
			det.setNombreRegla(item.getNombreRegla());	
			det.setStockDisponible(item.getStockDisponible());
			det.setCostoUnitarioGs(item.getCostoUnitarioGs());
			det.setCostoUnitarioDs(item.getCostoUnitarioDs());			
			det.setDescuentoUnitarioGs(item.getDescuentoUnitarioGs());
			det.setDescuentoUnitarioDs(item.getDescuentoUnitarioDs());					
			det.setPrecioVentaUnitarioGs(item.getPrecioVentaUnitarioGs());
			det.setPrecioVentaUnitarioDs(item.getPrecioVentaUnitarioDs());
			det.setPrecioVentaFinalUnitarioGs(item.getPrecioVentaFinalUnitarioGs());
			det.setPrecioVentaFinalUnitarioDs(item.getPrecioVentaFinalUnitarioDs());	
			det.setPrecioVentaFinalGs(item.getPrecioVentaFinalGs());
			det.setPrecioVentaFinalDs(item.getPrecioVentaFinalDs());	
			det.setTipoIVA(item.getTipoIVA());
			det.setPrecioGs(item.getPrecioGs());
			
			this.dto.getDetalles().add(det);
		}
	}
	
	/**
	 * Levanta los presupuestos cuyo estado es 'Solo Presupuesto'
	 * de la Sucursal y el Deposito de quien lo invoca..
	 * [0]: id - [1]: fecha - [2]: numero [3]: razonSocial Cliente
	 * [4]: atendido nombre - [5]: vendedor nombre - [6] deposito	
	 * [7]: sucursal  
	 */
	public List<Object[]> getPresupuestosPendientes() throws Exception{
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> list = rr.getPresupuestosPendientes(
				this.dto.getSucursal(), this.dto.getDeposito());		
		return list;
	}
	
	/***********************************************************************************/	
	
	
	/********************************** CERRAR PEDIDO **********************************/
	
	@Command @NotifyChange("*")
	public void cerrarPedido() throws Exception {
		
		if (this.validarFormulario() == true) {
			
			this.dto.setEstado(estado_PedidoCerrado);
			this.dto = (VentaDTO) this.saveDTO(this.dto);
			this.mensajePopupTemporal("Pedido Correctamente Aprobado..");
			this.setEstadoABMConsulta();
			
			this.addEventoAgenda(EVENTO_CIERRE_PEDIDO + this.dto.getNumero());
			this.grabarEventosAgenda();
			
		} else {
			this.mensajeError(this.mensajeError);
		}
	}	
	
	/***********************************************************************************/	
	
	
	/************************************ AGENDA ***************************************/
		
	//Lista de eventos
	private static String EVENTO_CREACION_PRESUPUESTO = "Se creó el presupuesto Nro. ";
	private static String EVENTO_CREACION_PEDIDO = "Se creó el pedido Nro. ";
	private static String EVENTO_PASAR_A_PEDIDO = "El presupuesto pasó a Pedido Nro. ";
	private static String EVENTO_MODIFICACION_PRESUPUESTO = "Se modificó el presupuesto Nro. ";
	private static String EVENTO_MODIFICACION_PEDIDO = "Se modificó el pedido Nro. ";
	private static String EVENTO_ALTA_CLIENTE_OCASIONAL = "Se dió de alta el cliente ocasional: ";
	private static String EVENTO_EDIT_CLIENTE_OCASIONAL = "Se editó el cliente ocasional: ";
	private static String EVENTO_IMPORTAR_PRESUPUESTO = "Se importó el Presupuesto Nro. ";
	private static String EVENTO_CIERRE_PEDIDO = "Se cerró el Pedido Nro. ";
	
	@Override
	public int getCtrAgendaTipo(){
		return ControlAgendaEvento.NORMAL;
	}
	
	@Override
	public String getCtrAgendaKey(){
		return this.getClaveAgenda()[0];
	}
	
	@Override
	public String getCtrAgendaTitulo(){		
		return this.getClaveAgenda()[1] + this.getCtrAgendaKey();
	}
	
	@Override
	public boolean getAgendaDeshabilitado() throws Exception {
		return this.dto.esNuevo();
	}	
	
	/**
	 * Retorna la clave que sera la misma para mantener
	 * ligado al [Presupuesto - Pedido - Factura] a una 
	 * sola agenda..
	 */
	private String[] getClaveAgenda(){
		
		String nroPresupuesto = this.dto.getNumeroPresupuesto();
		String nroPedido = this.dto.getNumeroPedido();
		
		String numero = "";
		String titulo = "";		
		
		if (nroPresupuesto.length() > 0) {
			numero = nroPresupuesto;
			titulo = "PRESUPUESTO Nro. ";
			
		} else if (nroPedido.length() > 0){
			numero = nroPedido;
			titulo = "PEDIDO Nro. ";
			
		} else {
			numero = this.dto.getNumero();
			titulo = this.tipo.toUpperCase() + " Nro. ";
		}
		
		return new String[]{ numero, titulo };
	}

	/***********************************************************************************/
	
	
	/****************************** CREACION DE PEDIDOS ********************************/
	
	/**
	 * Genera un pedido de Venta Contado para facturar desde caja..
	 */
	public void addPedidoVentaContado(String numero, Date fecha,
			String codigoCliente, MyPair moneda, double totalGravado,
			double totalExenta, double totalIva) throws Exception {
		
		MyPair deposito = this.utilDto.getMRA_deposito();
		MyPair modoVenta = this.utilDto.getMRA_modoVenta();
		MyPair sucursal = this.utilDto.getMRA_sucursal();
		MyArray vendedor = this.utilDto.getMRA_vendedor();
		
		List<VentaDetalleDTO> detalles = this.getDetallesMRA(totalGravado + totalExenta + totalIva);
		
		VentaDTO venta = new VentaDTO();
		venta.setTipoMovimiento(this.tm_PedidoVenta);
		venta.setCondicionPago(this.condicionContado);
		venta.setNumero(numero);
		venta.setFecha(fecha);
		venta.setCliente(this.getClienteByCodigo(codigoCliente));
		venta.setMoneda(this.myPairToMyarray(moneda));
		venta.setDeposito(deposito);
		venta.setEstado(this.estado_PedidoCerrado);
		venta.setModoVenta(modoVenta);
		venta.setObservacion("Importado desde Sistema C.R.");
		venta.setVendedor(vendedor);
		venta.setAtendido(vendedor);
		venta.setSucursal(sucursal);
		venta.setDetalles(detalles);
		venta.setTipoCambio(1);
		venta.setTotalImporteGs(totalGravado + totalExenta + totalIva);
		venta.setTotalImporteDs(totalGravado + totalExenta + totalIva);
		venta.setReadonly();
		this.saveDTO(venta);
	}
	
	/**
	 * Genera un pedido de Venta Credito para facturar desde caja..
	 */
	public void addPedidoVentaCredito(String numero, Date fecha,
			String codigoCliente, MyPair moneda, double totalGravado,
			double totalExenta, double totalIva) throws Exception {
		
		MyPair deposito = this.utilDto.getMRA_deposito();
		MyPair modoVenta = this.utilDto.getMRA_modoVenta();
		MyPair sucursal = this.utilDto.getMRA_sucursal();
		MyArray vendedor = this.utilDto.getMRA_vendedor();
		
		List<VentaDetalleDTO> detalles = this.getDetallesMRA(totalGravado + totalExenta + totalIva);
		
		VentaDTO venta = new VentaDTO();
		venta.setTipoMovimiento(this.tm_PedidoVenta);
		venta.setCondicionPago(this.condicionCredito);
		venta.setNumero(numero);
		venta.setFecha(fecha);
		venta.setCliente(this.getClienteByCodigo(codigoCliente));
		venta.setMoneda(this.myPairToMyarray(moneda));
		venta.setDeposito(deposito);
		venta.setEstado(this.estado_PedidoCerrado);
		venta.setModoVenta(modoVenta);
		venta.setObservacion("Importado desde Sistema C.R.");
		venta.setVendedor(vendedor);
		venta.setAtendido(vendedor);
		venta.setSucursal(sucursal);
		venta.setDetalles(detalles);
		venta.setTipoCambio(1);
		venta.setTotalImporteGs(totalGravado + totalExenta + totalIva);
		venta.setTotalImporteDs(totalGravado + totalExenta + totalIva);
		venta.setReadonly();
		this.saveDTO(venta);
	}
	
	/**
	 * @return el cliente segun el codigo..
	 */
	private MyArray getClienteByCodigo(String codigo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Cliente cl = rr.getClienteByCodigoMRA(codigo);
		MyArray out = new MyArray();
		if (cl != null) 
			out.setId(cl.getId());
		return out;
	}
	
	/**
	 * @return un MyArray a partir de un MyPair..
	 */
	private MyArray myPairToMyarray(MyPair mypair) {
		MyArray out = new MyArray();
		out.setId(mypair.getId());
		out.setPos1(mypair.getText());
		out.setPos2(mypair.getSigla());
		return out;
	}
	
	/**
	 * @return los items de las ventas que se migran en MRA..
	 */
	private List<VentaDetalleDTO> getDetallesMRA(double importe) {
		List<VentaDetalleDTO> out = new ArrayList<VentaDetalleDTO>();
		
		MyArray articulo = this.utilDto.getMRA_articulo();
		
		VentaDetalleDTO item = new VentaDetalleDTO();
		item.setArticulo(articulo);
		item.setDescripcion("Generado desde Sistema C.R.");
		item.setCantidad(1);
		item.setCostoUnitarioGs(0);
		item.setCostoUnitarioDs(0);
		item.setDescuentoUnitarioGs(0);
		item.setDescuentoUnitarioDs(0);
		item.setImpuestoFinal(0);
		item.setImpuestoUnitario(0);
		item.setPrecioVentaUnitarioGs(importe);
		item.setPrecioVentaUnitarioDs(importe);
		item.setNombreRegla("Sin regla..");
		item.setPrecioVentaFinalGs(importe);
		item.setPrecioVentaFinalDs(importe);
		item.setPrecioVentaFinalUnitarioGs(importe);
		item.setPrecioVentaFinalUnitarioDs(importe);
		out.add(item);
		
		return out;
	}
	
	/***********************************************************************************/
	
	
	/************************************** UTILES *************************************/
	
	private static String LABEL_PRESUPUESTO = "PRESUPUESTO";
	private static String LABEL_PEDIDO = "PEDIDO";
	private static String LABEL_FACTURA_CONTADO = "FAC. CONTADO";
	private static String LABEL_FACTURA_CREDITO = "FAC. CRÉDITO";
	private static String STYLE_LABEL_BLUE = "color:blue;font-weight:bold;font-size:14px";
	private static String STYLE_LABEL_RED = "color:red;font-weight:bold;font-size:14px";
	private static String STYLE_LABEL_GREEN = "color:green;font-weight:bold;font-size:14px";
	
	@Wire
	private Textbox txDep;
	
	private ControlLogica ctr = new ControlLogica(null);
	private UtilDTO utilDto = this.getDtoUtil();
	private MyArray tm_PresupuestoVenta = utilDto.getTmPresupuestoVenta();
	private MyArray tm_PedidoVenta = utilDto.getTmPedidoVenta();
	private MyArray tm_FacturaContado = utilDto.getTmFacturaVentaContado();
	private MyArray tm_FacturaCredito = utilDto.getTmFacturaVentaCredito();
	private MyArray monedaLocal = utilDto.getMonedaGuaraniConSimbolo();
	private MyArray condicionContado = utilDto.getCondicionPagoContado();
	private MyArray condicionCredito = utilDto.getCondicionPagoCredito30();
	
	// Estados..
	private MyArray estado_SoloPresupuesto = utilDto.getEstadoVenta_soloPresupuesto();
	private MyArray estado_Pasado_a_Pedido = utilDto.getEstadoVenta_pasadoApedido();
	private MyArray estado_SoloPedido = utilDto.getEstadoVenta_soloPedido();
	private MyArray estado_PedidoCerrado = utilDto.getEstadoVenta_cerrado();
	private MyArray estado_PedidoFacturado = utilDto.getEstadoVenta_facturado();
	
	@Command
	public void verImagen(@BindingParam("parent") Component parent, @BindingParam("popup") Popup popup) throws Exception {
		popup.open(100, 100);
		Clients.evalJavaScript("setImage('" + this.dto.getUrlImagen() + "')");
	}
	
	private void sugerirValores(VentaDTO out) throws Exception {
		MyPair deposito = this.getUsuarioPropiedad().getDepositoHabFacturar(this.utilDto);
		MyPair sucursal = this.getAcceso().getSucursalOperativa();
		MyArray usuarioFuncionario = this.getAcceso().getFuncionario();
		MyArray vendedor = new MyArray();
		vendedor.setId(usuarioFuncionario.getId());
		vendedor.setPos1(usuarioFuncionario.getPos1());
		
		out.setTipoMovimiento(this.tipoMovimiento);
		out.setEstado(this.estado);	
		out.setCondicionPago(utilDto.getCondicionPagoContado());
		out.setMoneda(this.utilDto.getMonedaGuaraniConSimbolo());
		out.setTipoCambio(this.utilDto.getCambioCompraBCP(out.getMoneda()));
		out.setAtendido(usuarioFuncionario);
		out.setVendedor(vendedor);
		out.setSucursal(sucursal);
		out.setDeposito(deposito);
		out.setModoVenta(this.getUsuarioPropiedad().getModoVenta(utilDto.getModosVenta()));
		
		// las ventas de yhaguy baterias se hacen por reparto..
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
			out.setReparto(true);
			out.setVendedor(new MyArray());
		}
		
		// en mra el pedido lo prepara el vendedor de mostrador..
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_MRA)) {
			out.setPreparadoPor((String) usuarioFuncionario.getPos1());
		}
	}
	
	/**
	 * Retorna algunos datos de la factura..
	 * @return [0]: total del importe en moneda local
	 * @return [1]: total del importe en moneda extranjera
	 */
	public Object[] getDatosVenta(){
		
		double totalImporteGs = 0;
		double totalImporteDs = 0;
		
		for (VentaDetalleDTO item : this.dto.getDetalles()) {
			totalImporteGs += item.getPrecioVentaFinalGs();
			totalImporteDs += item.getPrecioVentaFinalDs();
		}		
		return new Object[]{totalImporteGs, totalImporteDs};
	}
	
	@Command
	public void refreshTipoCambio(){
		this.dto.setTipoCambio(this.utilDto.getCambioCompraBCP(this.dto.getMoneda()));
		BindUtils.postNotifyChange(null, null, this.dto, "tipoCambio");
	}
	
	//Para habilitar/deshabilitar el checkmark..
	public boolean getCheckmarkVentaPedido(){
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getDetalles().size() > 0) {
			out = true;
		}
		return out;
	}
	
	@DependsOn("dto.tipoMovimiento")
	public boolean isPresupuesto(){		
		return this.tipoMovimiento.compareTo(this.tm_PresupuestoVenta) == 0;
	}
	
	@DependsOn("dto.tipoMovimiento")
	public boolean isPedido(){		
		return this.tipoMovimiento.compareTo(this.tm_PedidoVenta) == 0;
	}
	
	@DependsOn("dto.tipoMovimiento")
	public boolean isFactura(){
		boolean out = false;
		boolean contado = this.tipoMovimiento.compareTo(this.tm_FacturaCredito) == 0;
		boolean credito = this.tipoMovimiento.compareTo(this.tm_FacturaContado) == 0;
		if ((contado == true) || (credito == true)) {
			out = true;
		}
		return out;
	}
	
	@DependsOn("dto.tipoMovimiento")
	public boolean isFacturaContado(){		
		return this.tipoMovimiento.compareTo(this.tm_FacturaContado) == 0;
	}
	
	@DependsOn("dto.tipoMovimiento")
	public boolean isFacturaCredito(){		
		return this.tipoMovimiento.compareTo(this.tm_FacturaCredito) == 0;
	}
	
	//Retorna el label y el estilo para la vista..
	public String[] getLabelTipoMovimiento() {
		String label = "";
		String style = "";
		
 		if (this.isPresupuesto() == true) {
 			label = LABEL_PRESUPUESTO;
 			style = STYLE_LABEL_BLUE;
		} else if (this.isPedido() == true) {
			label = LABEL_PEDIDO;
			style = STYLE_LABEL_RED;
		} else if (this.isFacturaContado() == true) {
			label = LABEL_FACTURA_CONTADO;
			style = STYLE_LABEL_GREEN;
		} else {
			label = LABEL_FACTURA_CREDITO;
			style = STYLE_LABEL_GREEN;
		}
 		return new String[]{ label, style };
	}
	
	
	/**
	 * Metodo usado para la generacion de Reserva..
	 */
	public void crearReserva(VentaDTO venta, MyPair tipoReserva) throws Exception {

		MyArray tipoMov = venta.getTipoMovimiento();
		MyPair estado = getDtoUtil().getEstado_reservaActiva();
		List<VentaDetalleDTO> items = venta.getDetalles();		
		
		// crea una reserva y sus detalles
		// actualiza el stock de los articulos del deposito de salida
		ReservaDTO reservaDto = this.ctr.crearReservaVentaPedido(
				venta.getId(), tipoMov, tipoReserva, estado, venta.getFecha(), 
				venta.getAtendido(), venta.getDeposito(), items);
		
		for (VentaDetalleDTO item : items) {
			item.setReservado(true);
		}
		
		venta.setReserva(reservaDto);		
		this.saveDTO(venta, new AssemblerVenta());
	}
	
	/**
	 * Metodo usado para la finalizacion de Reservas de Venta..
	 */
	public void finalizarReserva(VentaDTO venta) throws Exception {
		ReservaDTO reserva = venta.getReserva();
				
		if (reserva == null) {
			return;
		}

		Hashtable<Long, ReservaDetalleDTO> items = new Hashtable<>();

		// pone los items de la reserva en una tabla..
		for (ReservaDetalleDTO item : reserva.getDetalles()) {
			items.put(item.getIdDetalleOrigen(), item);
		}
		this.ctr.modificarReserva(reserva, ControlLogica.FINALIZAR_RESERVA);
	}
	
	@Command
	public void calcularVencimiento() {
		
		int plazo = (int) this.dto.getCondicionPago().getPos2();
		int cuotas = (int) this.dto.getCondicionPago().getPos3();
		
		this.dto.setCuotas(cuotas);
		this.dto.setVencimiento(m.agregarDias(new Date(), plazo));
		
		BindUtils.postNotifyChange(null, null, this.dto, "vencimiento");
		BindUtils.postNotifyChange(null, null, this.dto, "cuotas");
	}

	@Command
	public void refreshValores() {
	}
	
	//Retorna true si se puede editar el cliente..
	public boolean isClienteEditable(){
		boolean out = false;
		if ((this.isDeshabilitado() == false)
				&& (this.dto.getDetalles().size() == 0)) {
			out = true;
		}
		return out;
	}
		
	//usado para el label de la columna Precio
	@DependsOn("dto.moneda")
	public String getLabelPrecio(){
		return "Precio " + this.dto.getMoneda().getPos2();
	}
		
	//usado para el label de la columna Importe
	@DependsOn("dto.moneda")
	public String getLabelImporte(){
		return "Importe " + this.dto.getMoneda().getPos2();
	}
		
	//usado para el label de la columna Descuento
	@DependsOn("dto.moneda")
	public String getLabelDescuento(){
		return "Descuento " + this.dto.getMoneda().getPos2();
	}
	
	//usado para el label cantidad del footer
	@DependsOn("dto.detalles")
	public String getLabelCantidad(){
		return this.dto.getDetalles().size() + " ítems";
	}
	
	//retorna true si es venta en moneda local
	@DependsOn("dto.moneda")
	public boolean isVentaMonedaLocal(){
		String sigla = (String) this.dto.getMoneda().getPos2();
		String sigla_ = Configuracion.SIGLA_MONEDA_GUARANI;
		return sigla.compareTo(sigla_) == 0;
	}
	
	//para mostrar el icono que representa al estado del presupuesto
	@DependsOn("dto.estado")
	public String getSrcEstadoPresupuesto(){
		boolean cerrado = (this.dto.getEstado().getId().compareTo(
				estado_Pasado_a_Pedido.getId()) == 0);
		return cerrado ? Config.IMAGEN_OK : Config.IMAGEN_CANCEL;
	}
	
	//para mostrar el icono que representa al estado del pedido
	public String getSrcEstadoPedido(){
		boolean cerrado = (this.dto.getEstado().getId().compareTo(
				estado_PedidoCerrado.getId()) == 0);
		boolean facturado = (this.dto.getEstado().getId().compareTo(
				estado_PedidoFacturado.getId()) == 0);
		
		return (cerrado || facturado) ? Config.IMAGEN_OK : Config.IMAGEN_CANCEL;		
	}
	
	/***********************************************************************************/	
	
	
	/******************************** VALIDAR CUENTA ***********************************/
	
	@GlobalCommand
	public void validarCuenta() throws Exception {
	}
	
	/***********************************************************************************/
	
	
	/******************************* VALIDACIONES AL GRABAR ****************************/
	
	private String mensajeError = "";
	
	@Override
	public boolean verificarAlGrabar() {
		boolean out = false;
		
		try {
			this.dto.setDenominacion((String) this.dto.getCliente().getPos2());
			out = this.validarFormulario();
			
			if ((this.dto.esNuevo() == true) && (out == true)) {				
				if (this.isPresupuesto() == true) {
					this.dto.setNumero(Configuracion.NRO_VENTA_PRESUPUESTO + "-" +
							AutoNumeroControl.getAutoNumero(Configuracion.NRO_VENTA_PRESUPUESTO, 7));
					this.dto.setNumeroPresupuesto(this.dto.getNumero());
					this.addEventoAgenda(EVENTO_CREACION_PRESUPUESTO + this.dto.getNumero());
				} else {
					this.dto.setNumero(Configuracion.NRO_VENTA_PEDIDO + "-" +
							AutoNumeroControl.getAutoNumero(Configuracion.NRO_VENTA_PEDIDO, 7));
					this.dto.setNumeroPedido(this.dto.getNumeroPedido());
					this.addEventoAgenda(EVENTO_CREACION_PEDIDO + this.dto.getNumero());
				}				
			}
			
			if (out == true) {				
				
				//Verifica si hubo modificaciones para agregar el dato en la agenda.
				if ((this.dto.esNuevo() == false) && (this.siHuboCambiosEnDTO() == true)) {
					
					if (this.isPresupuesto() == true) {
						this.addEventoAgenda(EVENTO_MODIFICACION_PRESUPUESTO + this.dto.getNumero());
					}else{
						this.addEventoAgenda(EVENTO_MODIFICACION_PEDIDO + this.dto.getNumero());
					}
				}								
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return out;
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeError;
	}
	
	private boolean validarFormulario() throws Exception {
		boolean out = true;
		this.mensajeError = "No se puede completar la operación debido a: \n";
		
		//verifica que se haya seleccionado un cliente.
		if ((this.dto.getCliente().esNuevo() == true)
				&& (this.dto.getClienteOcasional() == null)) {
			out = false;
			mensajeError += "\n - Debe seleccionar un Cliente..";
		}
		
		//verifica que el detalle no este vacio.
		if (this.dto.getDetalles().size() == 0) {
			out = false;
			mensajeError += "\n - Debe ingresar al menos un ítem..";
		}
		
		/*//verifica que el usuario tenga asignado un Modo de Venta
		if (this.dto.getModoVenta().esNuevo() == true) {
			out = false;
			mensajeError += "\n - El Modo de Venta no fue asignada..";
		}*/
		
		//si es Pedido verifica si se pueden reservar los items.
		if ((this.isPresupuesto() == false) && this.dto.esNuevo() == true) {
			Object[] obj = this.verificarStock(this.dto.getDetalles());
			boolean stockOk = (boolean) obj[0];
			String stockMsg = (String) obj[1];
			if (stockOk == false) {
				out = false;
			}
			mensajeError += "\n" + stockMsg;
		}
		
		// verifica el saldo disponible..
		double importeVenta = this.dto.getTotalImporteGs();
		double disponible = this.dto.getCreditoDisponible();
		if ((!this.dto.isCondicionContado()) && (importeVenta > disponible)) {
			out = false;
			mensajeError += "\n - LINEA DE CREDITO INSUFICIENTE..";
		}
		
		return out;
	}
	
	
	/************************ VALIDACIONES *************************/
	
	/**
	 * Validador insertar servicio..
	 */
	class ValidadorInsertarServicio implements VerificaAceptarCancelar {

		private String mensaje;
		
		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			if (nvoItem.getCantidad() <= 0 ) {
				this.mensaje += "\n - La cantidad debe ser mayor a cero..";
				out = false;
			}
			
			if (nvoItem.getPrecioGs() <= 0) {
				this.mensaje += "\n - El precio debe ser mayor a cero..";
				out = false;
			}
			
			return out;
		}

		@Override
		public String textoVerificarAceptar() {
			return this.mensaje;
		}

		@Override
		public boolean verificarCancelar() {
			return true;
		}

		@Override
		public String textoVerificarCancelar() {
			return "Error al cancelar..";
		}		
	}
	
	/***************************************************************/
	
	
	/************************ GETTER/SETTER ************************/
	
	@DependsOn({ "dto.cliente", "dto.vendedor", "dto.deposito" })
	public boolean isDetalleVisible() {
		return (this.dto.getCliente().esNuevo() == false
				|| this.dto.getClienteOcasional() != null)
					&& (!this.dto.getVendedor().esNuevo())
					&& (this.dto.getDeposito().esNuevo() == false);
	}
	
	@DependsOn({ "deshabilitado", "selectedItems" })
	public boolean isDeleteItemDisabled() {
		return this.isDeshabilitado() || this.selectedItems == null
				|| this.selectedItems.size() == 0;
	}
	
	@DependsOn({ "deshabilitado", "selectedItems" })
	public boolean isImportarDisabled() {
		return this.isDeshabilitado()
				|| (this.selectedItems != null && this.selectedItems.size() > 0);
	}
	
	@DependsOn({ "deshabilitado", "dto" })
	public boolean isCerrarDisabled() throws Exception {
		return this.isDeshabilitado() || this.dto.esNuevo() || Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS);
	}
	
	/**
	 * @return si la operacion es habilitada..
	 */
	public boolean isOperacionHabilitada(String operacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.isOperacionHabilitada(this.getLoginNombre(), operacion);
	}
	
	/**
	 * @return longitud maxima de un string..
	 */
	private String getMaxLength(String string, int max) {
		if (string.length() > max)
			return string.substring(0, max) + "...";
		return string;
	}
	
	/**
	 * @return la lista de precio definida al cliente..
	 */
	private MyArray getListaPrecio() throws Exception {
		MyArray out = new MyArray();
		RegisterDomain rr = RegisterDomain.getInstance();
		Cliente cli = rr.getClienteById(this.dto.getCliente().getId());
		if (cli.getListaPrecio() != null) {
			out.setId(cli.getListaPrecio().getId());
			out.setPos1(cli.getListaPrecio().getDescripcion());
			out.setPos2(cli.getListaPrecio().getMargen());
			out.setPos3(cli.getListaPrecio().getFormula());
		} else {
			ArticuloListaPrecio lp = rr.getListaDePrecio(2);
			if (lp != null) {
				out = new MyArray(lp.getDescripcion(), lp.getMargen(), lp.getFormula());
				out.setId(lp.getId());
			}
		}
		return out;
	}
	
	/**
	 * @return condiciones de pago..
	 */
	public List<MyArray> getCondicionesPagos() {
		if (!Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
			return this.getDtoUtil().getCondicionesPago();
		}
		List<MyArray> out = new ArrayList<MyArray>();
		out.add(this.getDtoUtil().getCondicionPagoContado());
		out.add(this.getDtoUtil().getCondicionPagoCredito90());
		return out;
	}
	
	/**
	 * @return los depositos..
	 */
	public List<MyPair> getDepositos() {
		return this.getDtoUtil().getDepositosMyPair();
	}
	
	public VentaDTO getDto() {
		return dto;
	}

	public void setDto(VentaDTO dto) {
		this.dto = dto;
	}
	
	public List<VentaDetalleDTO> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<VentaDetalleDTO> selectedItems) {
		this.selectedItems = selectedItems;
	}	
	
	public String getLabelTotalPagar(){
		return LABEL_TOTAL_A_PAGAR;
	}
	
	public String getLabelIva10(){
		return LABEL_IVA_10;
	}
	
	public String getLabelIva5(){
		return LABEL_IVA_5;
	}
	
	public String getLabelDescuento_(){
		return LABEL_DESCUENTO;
	}

	public VentaDTO getSelectedPresupuesto() {
		return selectedPresupuesto;
	}

	public void setSelectedPresupuesto(VentaDTO selectedPresupuesto_) {
		this.selectedPresupuesto = selectedPresupuesto_;
	}

	public VentaDetalleDTO getNvoItem() {
		return nvoItem;
	}

	public void setNvoItem(VentaDetalleDTO nvoItem) {
		this.nvoItem = nvoItem;
	}

	public ReciboFormaPagoDTO getNvoFormaPago() {
		return nvoFormaPago;
	}

	public void setNvoFormaPago(ReciboFormaPagoDTO nvoFormaPago) {
		this.nvoFormaPago = nvoFormaPago;
	}	
	
	public MyArray getSelectedChequera() {
		return new MyArray();
	}
}

/**
 * Reporte de Venta..
 */
class ReporteVenta extends ReporteYhaguy {
	
	private VentaDTO venta;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_LONG, 30, false);
	static DatosColumnas col3_ = new DatosColumnas("Stock", TIPO_LONG, 30, false);
	static DatosColumnas col4 = new DatosColumnas("Precio", TIPO_DOUBLE, 30, false);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE, 30, true);
	
	public ReporteVenta(VentaDTO venta) {
		this.venta = venta;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col3_);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.venta.getNumero();
		String origen = this.venta.getSucursal().getText();
		String cliente = (String) this.venta.getCliente().getPos2();
		String condicion = (String) this.venta.getCondicionPago().getPos1();
		String observacion = (String) this.venta.getObservacion();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Número", numero))
				.add(this.textoParValor("Sucursal", origen)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Cliente", cliente))
				.add(this.textoParValor("Condición", condicion)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));
		
		out.add(cmp.horizontalFlowList()
				.add(this.textoParValor("Observación", observacion))
				.add(this.venta.isPresupuesto() ? this.textoParValor("Válido por", this.venta.getValidez() + " Días") : this.texto("")));
		
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Venta sin precios..
 */
class ReporteVentaSinPrecio extends ReporteYhaguy {
	
	private VentaDTO venta;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_LONG, 30, false);
	static DatosColumnas col4 = new DatosColumnas("Stock", TIPO_LONG, 30, false);
	
	public ReporteVentaSinPrecio(VentaDTO venta) {
		this.venta = venta;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("ventas");
		this.setNombreArchivo("Venta-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.venta.getNumero();
		String origen = this.venta.getSucursal().getText();
		String cliente = (String) this.venta.getCliente().getPos2();
		String condicion = (String) this.venta.getCondicionPago().getPos1();
		String moneda = (String) this.venta.getMoneda().getPos1();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", numero)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Sucursal", origen)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Cliente", cliente)));
		
		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Condición", condicion)));
		
		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Moneda", moneda)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * validador de la forma de pago de ventas..
 */
class ValidadorFormaPagoVenta implements VerificaAceptarCancelar {

	// private VentaControlBody ctr;
	private String mensaje;

	public ValidadorFormaPagoVenta(VentaControlBody ctr) {
		//this.ctr = ctr;
	}

	@Override
	public boolean verificarAceptar() {
		boolean valido = true;
		this.mensaje = "No se puede continuar la operación debido a: \n";

		return valido;
	}

	@Override
	public String textoVerificarAceptar() {
		return this.mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al cancelar";
	}

}
