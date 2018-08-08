package com.yhaguy.gestion.notacredito;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.ImportacionFactura;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.ServicioTecnico;
import com.yhaguy.domain.ServicioTecnicoDetalle;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Venta;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;
import com.yhaguy.gestion.comun.ControlArticuloStock;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class NotaCreditoControlBody extends BodyApp {
	
	final static String TIPO_PARAM  = "tipo";
	final static String TIPO_VENTA  = "venta";
	final static String TIPO_COMPRA = "compra";
	final static String TIPO_SOLICITUD_NC_VENTA  = "solicitudNCVenta";
	final static String TIPO_SOLICITUD_NC_COMPRA = "solicitudNCCompra";
	final static String ZUL_DETALLE_FACTURA = "/yhaguy/gestion/notacredito/DetalleFactura.zul";
	final static String ZUL_DETALLE_DEVOLUCION = "/yhaguy/gestion/notacredito/DetalleDevolucion.zul";
	final static String OPER_APROBAR_NC_DEV = "aprobarNotaCreditoDevolucion";
	
	final static String STYLE_LABEL_BLUE = "color:blue;font-weight:bold;font-size:14px";
	final static String STYLE_LABEL_RED = "color:red;font-weight:bold;font-size:14px";
	
	private NotaCreditoDTO dto = new NotaCreditoDTO();
	private MyArray tipoMovimiento = new MyArray();
	
	private NotaCreditoDetalleDTO nvoItem = new NotaCreditoDetalleDTO(this.getIva10());
	private NotaCreditoDetalleDTO selectedItemFac;
	private MyArray selectedFactura = new MyArray();
	private List<NotaCreditoDetalleDTO> selectedItemsArt = new ArrayList<NotaCreditoDetalleDTO>();
	private List<MyArray> _selectedItemsArt;
	private MyArray selectedServicioTecnico;
	
	private String msgError = "";
	private String numeroOriginal = "";
	private String filter_numero = "";
	
	private Window win;
	
	static String[] tipos = new String[] { Config.TIPO_STRING, Config.TIPO_STRING,
			Config.TIPO_DATE, Config.TIPO_STRING, Config.TIPO_NUMERICO };
	
 	
	@Init(superclass = true)
	public void init(@ExecutionParam(TIPO_PARAM) String tipo){
		
		switch (tipo) {
		
		case TIPO_VENTA:			
			this.tipoMovimiento = this.tm_notaCreditoVenta;
			this.dto.setTipoMovimiento(tm_notaCreditoVenta);			
			this.setTextoFormularioCorriente("Nota de Crédito Venta");			
			break;

		case TIPO_COMPRA:
			this.tipoMovimiento = this.tm_notaCreditoCompra;
			this.dto.setTipoMovimiento(tm_notaCreditoCompra);
			this.setTextoFormularioCorriente("Nota de Crédito Compra");
			break;
			
		case TIPO_SOLICITUD_NC_VENTA:
			this.tipoMovimiento = this.tm_solicitud_NC_Venta;
			this.dto.setTipoMovimiento(tm_solicitud_NC_Venta);
			this.setTextoFormularioCorriente("Solicitud Nota Crédito");
			break;
			
		case TIPO_SOLICITUD_NC_COMPRA:
			this.tipoMovimiento = this.tm_solicitud_NC_Compra;
			this.dto.setTipoMovimiento(tm_solicitud_NC_Compra);
			this.setTextoFormularioCorriente("Solicitud Nota Crédito");
			break;
			
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(){
	}	

	@Override
	public Assembler getAss() {
		return new NotaCreditoAssembler();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.selectedFactura = new  MyArray();
		this.selectedItemFac = null;
		this.selectedItemsArt = new ArrayList<NotaCreditoDetalleDTO>();
		this.dto = (NotaCreditoDTO) dto;	
		this.numeroOriginal = this.dto.getNumero();
		if (this.dto.isAnulado()) {			
			this.enmascararAnulados(true);
		} else {
			this.enmascararAnulados(false);		
		}
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		this.selectedItemFac = null;
		NotaCreditoDTO out = new NotaCreditoDTO();
		sugerirValores(out);		
		return out;
	}

	@Override
	public String getEntidadPrincipal() {
		return NotaCredito.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(getEntidadPrincipal());
	}
	
	@Override
	public Browser getBrowser() {
		String where = "tipoMovimiento.id = " + this.tipoMovimiento.getId();
		return new NotaCreditoBrowser(where, (this.tipoMovimiento == this.tm_notaCreditoCompra 
				|| this.tipoMovimiento == this.tm_solicitud_NC_Compra));
	}	
	
	@Override
	public boolean verificarAlGrabar() {
		boolean out = false;
		try {
			out = this.validarFormulario();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return (this.dto.esNuevo());
	}
	
	@Override
	public void showImprimir() {
		try {
			if (this.dto.isSolicitudNotaCreditoVenta()) {
				this.imprimirSolicitudNC();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return msgError;
	}
	
	
	/************************ COMANDOS ************************/			
	
	/**
	 * Busqueda de las facturas de Venta..
	 */
	@Command
	@NotifyChange("*")
	public void buscarFacturas() throws Exception {
		if (this.dto.isNotaCreditoVenta()) {
			this.buscarVentas();
		} else {
			if (this.mensajeSiNo("Factura de compra de mercaderías..?")) {
				this.buscarCompras();
			} else {
				this.buscarGastos();
			}
		}
	}
	
	@Command
	@NotifyChange("*")
	public void findTimbrado() {
		this.buscarTimbrado();
	}
	
	@Command
	@NotifyChange("*")
	public void confirmar() throws Exception {
		this.confirmarNC();
	}	
	
	@Command 
	@NotifyChange("*")
	public void eliminarItems(
			@BindingParam("items") List<NotaCreditoDetalleDTO> items) {
		this.deleteItems(items);
	}
	
	@Command
	@NotifyChange("*")
	public void eliminarItemFac() {
		this.deleteItemFac();
	}
	
	@Command 
	@NotifyChange("*")
	public void insertarDevolucion() throws Exception {
		this.addDevolucion();
	}
	
	@Command 
	@NotifyChange("*")
	public void selectServicioTecnico(@BindingParam("comp") Popup comp) {
		this.dto.getServiciosTecnicos().add(this.selectedServicioTecnico);
		comp.close();
	}
	
	/**************************************************************/
	
	
	/************************** FUNCIONES *************************/
	
	/**
	 * Busca las ventas..
	 */
	private void buscarVentas() throws Exception {

		String campoImporte = this.dto.isMonedaLocal() ? "totalImporteGs"
				: "totalImporteDs";

		String[] atributos = new String[] { "tipoMovimiento.descripcion",
				"numero", "fecha", "sucursal.descripcion", campoImporte };

		String[] columnas = new String[] { "Tipo Movimiento", "Número",
				"Fecha", "Sucursal",
				"Importe " + this.dto.getMoneda().getSigla() };

		String[] tipos = new String[] { Config.TIPO_STRING, Config.TIPO_STRING,
				Config.TIPO_DATE, Config.TIPO_STRING, Config.TIPO_NUMERICO };

		long idCliente = this.dto.getCliente().getId();

		BuscarElemento b = new BuscarElemento();
		b.setClase(Venta.class);
		b.setTitulo("Facturas de Venta - Cliente: "
				+ this.dto.getCliente().getPos2() + " en Moneda: "
				+ this.dto.getMoneda().getSigla());
		b.setTipos(tipos);
		b.setAtributos(atributos);
		b.setNombresColumnas(columnas);
		b.setWidth("980px");
		b.addOrden("numero");
		b.addWhere("c.cliente.id = " + idCliente + " and "
				+ "(c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_VENTA_CONTADO + "' "
				+ " or c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_VENTA_CREDITO
				+ "') and c.moneda.id = " + this.dto.getMoneda().getId()
				+ " and c.estadoComprobante is null");
		b.setContinuaSiHayUnElemento(false);
		b.show("%");

		if (b.isClickAceptar()) {

			if (this.existeFacturaEnDetalle(b.getSelectedItem()) == true) {
				String error = "No se puede completar la operación debido a: \n";
				error += "\n - Ya existe la factura en el detalle..";
				this.mensajeError(error);
				return;
			}

			this.nvoItem = this.crearDetalleDesde(b.getSelectedItem(), true, false, false);

			if (this.dto.isMotivoDescuento()) {
				this.abrirPopupDetalle(ZUL_DETALLE_FACTURA);

			} else {
				this.dto.getDetalles().add(this.nvoItem);
			}
		}
	}
	
	/**
	 * Busca las compras..
	 */
	private void buscarCompras() throws Exception {
		
		String campoImporte = this.dto.isMonedaLocal() ? "importeGs"
				: "importeDs";	
		
		String campoImporteDs = this.dto.isMonedaLocal() ? "totalAsignadoGs"
				: "totalAsignadoDs";
		
		String[] atributos = new String[] { "tipoMovimiento.descripcion", "numero",
				"fechaCreacion", "sucursal.descripcion", campoImporte };
		
		String[] atributosDs = new String[] { "tipoMovimiento.descripcion", "numero",
				"fechaCreacion", "observacion", campoImporteDs };

		String[] columnas = new String[] { "Tipo Movimiento", "Número",
				"Fecha", "Sucursal", "Importe " 
				+ this.dto.getMoneda().getSigla() };
		
		String[] columnasDs = new String[] { "Tipo Movimiento", "Número",
				"Fecha", "Observacion", "Importe " 
				+ this.dto.getMoneda().getSigla() };

		long idProveedor = this.dto.getProveedor().getId();

		BuscarElemento b = new BuscarElemento();
		b.setClase(this.dto.isMonedaLocal() ? CompraLocalFactura.class : ImportacionFactura.class);
		b.setTitulo("Facturas de " + (this.dto.isMonedaLocal() ? "Compras" : "Importacion") + " - Proveedor: "
				+ this.dto.getProveedor().getPos2() + " en Moneda: "
				+ this.dto.getMoneda().getSigla());
		b.setAtributos(this.dto.isMonedaLocal() ? atributos : atributosDs);
		b.setNombresColumnas(this.dto.isMonedaLocal() ? columnas : columnasDs);
		b.setTipos(tipos);
		b.setWidth("1000px");
		b.addOrden("numero");
		b.addWhere("c.proveedor.id = " + idProveedor + " and "
				+ "(c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_COMPRA_CONTADO + "' "
				+ " or c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO + "' "
				+ " or c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_IMPORT_CONTADO + "' "
				+ " or c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_IMPORT_CREDITO
				+ "') and c.moneda.id = " + this.dto.getMoneda().getId());
		b.setContinuaSiHayUnElemento(false);
		b.show("%");

		if (b.isClickAceptar()) {

			if (this.existeFacturaEnDetalle(b.getSelectedItem()) == true) {
				String error = "No se puede completar la operación debido a: \n";
				error += "\n - Ya existe la factura en el detalle..";
				this.mensajeError(error);
				return;
			}

			this.nvoItem = this.crearDetalleDesde(b.getSelectedItem(), false, true, false);

			if (this.dto.isMotivoDescuento()) {
				this.abrirPopupDetalle(ZUL_DETALLE_FACTURA);

			} else {
				this.dto.getDetalles().add(this.nvoItem);
			}
		}
	}
	
	/**
	 * Busca los gastos..
	 */
	private void buscarGastos() throws Exception {
		
		String campoImporte = this.dto.isMonedaLocal() ? "importeGs"
				: "importeDs";	
		
		String[] atributos = new String[] { "tipoMovimiento.descripcion", "numeroFactura",
				"fecha", "sucursal.descripcion", campoImporte };

		String[] columnas = new String[] { "Tipo Movimiento", "Número",
				"Fecha", "Sucursal", "Importe " 
				+ this.dto.getMoneda().getSigla() };

		long idProveedor = this.dto.getProveedor().getId();

		BuscarElemento b = new BuscarElemento();
		b.setClase(Gasto.class);
		b.setTitulo("Facturas de Gasto - Proveedor: "
				+ this.dto.getProveedor().getPos2() + " en Moneda: "
				+ this.dto.getMoneda().getSigla());
		b.setAtributos(atributos);
		b.setNombresColumnas(columnas);
		b.setTipos(tipos);
		b.setWidth("800px");
		b.addOrden("numeroFactura");
		b.addWhere("c.proveedor.id = " + idProveedor + " and "
				+ "(c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_GASTO_CONTADO + "' "
				+ " or c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_GASTO_CREDITO
				+ "') and c.moneda.id = " + this.dto.getMoneda().getId());
		b.setContinuaSiHayUnElemento(false);
		b.show("%");

		if (b.isClickAceptar()) {

			if (this.existeFacturaEnDetalle(b.getSelectedItem()) == true) {
				String error = "No se puede completar la operación debido a: \n";
				error += "\n - Ya existe la factura en el detalle..";
				this.mensajeError(error);
				return;
			}

			this.nvoItem = this.crearDetalleDesde(b.getSelectedItem(), false, false, true);

			if (this.dto.isMotivoDescuento()) {
				this.abrirPopupDetalle(ZUL_DETALLE_FACTURA);

			} else {
				this.dto.getDetalles().add(this.nvoItem);
			}
		}
	}
	
	/**
	 * Despliega el popup para agregar o modificar un detalle..
	 */
	private void abrirPopupDetalle(String zul) throws Exception {
		
		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.NUEVO);
		wp.setDato(this);
		wp.setTitulo("Detalle de Nota de Crédito");
		wp.setWidth("400px");
		wp.setHigth("230px");
		// wp.setCheckAC(new ValidadorInsertarFactura(this));
		wp.show(zul);
		
		if (wp.isClickAceptar()) {
			this.dto.getDetalles().add(this.nvoItem);
		}
	}
	
	/**
	 * Crea un Detalle de NC a partir de una factura de venta o de Compra..
	 */
	private NotaCreditoDetalleDTO crearDetalleDesde(MyArray desde, boolean venta, boolean compra, boolean gasto) 
		throws Exception {

		NotaCreditoDetalleDTO out = new NotaCreditoDetalleDTO(this.getIva10());		
		out.setCantidad(1);
		out.setTipoDetalle(getDtoUtil().getNotaCreditoDetalleFactura());		
		if(venta){
			out.setVenta(desde);
		} else if(compra) {
			if(this.dto.isMonedaLocal()) {
				out.setCompra(desde);
				out.setDeposito(this.getDepositoByFacturaCompra(desde.getId()));
			} else {
				out.setImportacion(desde);
				out.setDeposito(this.getDepositoByFacturaImportacion(desde.getId()));
			}
		} else if (gasto) {
			out.setGasto(desde);
		}
		return out;
	} 
	
	/**
	 * Busca la factura dentro del detalle de facturas..
	 */
	private boolean existeFacturaEnDetalle(MyArray factura) {
		for (NotaCreditoDetalleDTO det : this.dto.getDetallesFacturas()) {
			if (det.getVenta().getId().longValue() == factura.getId()
					.longValue()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return el deposito a partir del id de la compra..
	 */
	private MyPair getDepositoByFacturaCompra(long idCompra) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Deposito dep = rr.getDepositoByFacturaCompraLocal(idCompra);		
		return new MyPair(dep.getId(), dep.getDescripcion());
	}
	
	/**
	 * @return el deposito a partir del id de la importacion..
	 */
	private MyPair getDepositoByFacturaImportacion(long idImportacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Deposito dep = rr.getDepositoByFacturaImportacion(idImportacion);	
		return new MyPair(dep.getId(), dep.getDescripcion());
	}
	
	/**
	 * asignacion de timbrado..
	 */
	private void buscarTimbrado() {
		WindowTimbrado w = new WindowTimbrado();
		w.setIdProveedor(this.dto.getProveedor().getId());
		w.setTimbrado("");		
		w.show(WindowPopup.NUEVO, w);
		if (w.isClickAceptar()) {
			this.dto.setTimbrado(w.getSelectedTimbrado());
		}
	}
	
	/**
	 * eliminar items..
	 */
	private void deleteItems(List<NotaCreditoDetalleDTO> items) {
		// solicita confirmacion para eliminar..
		if (this.confirmarEliminarItems(items) == true) {

			for (NotaCreditoDetalleDTO item : items) {
				this.dto.getDetalles().remove(item);
			}
			this.substractImporteFactura(items);

			this.selectedItemsArt = new ArrayList<NotaCreditoDetalleDTO>();
		}
	}
	
	/**
	 * @return confirmacion para eliminar items..
	 */
	private boolean confirmarEliminarItems(List<NotaCreditoDetalleDTO> items) {
		if (items.size() == 0) {
			return false;
		}
		String texto = "Está seguro que quiere eliminar los items: \n";
		for (NotaCreditoDetalleDTO item : items) {			
			texto += "\n - " + item.getDescripcion();
		}
		return this.mensajeSiNo(texto);
	}
	
	/**
	 * elimina el item de tipo factura..
	 */
	private void deleteItemFac() {
		if(this.mensajeSiNo("Desea eliminar el ítem seleccionado..") == false)
			return;
		this.dto.getDetalles().remove(this.selectedItemFac);
		this.selectedItemFac = null;
	}
	
	/**
	 * Actualiza los montos aplicados en facturas..
	 */
	public void substractImporteFactura(List<NotaCreditoDetalleDTO> items) {
		
		if ((this.dto.isNotaCreditoVenta() == true)
				|| (this.dto.isSolicitudNotaCreditoVenta() == true)) {
			
			for (NotaCreditoDetalleDTO item : items) {
				for (NotaCreditoDetalleDTO det : this.dto.getDetallesFacturas()) {
					if (item.getVenta().getId().compareTo(det.getVenta().getId()) == 0){
						det.setMontoGs(det.getMontoGs() - item.getImporteGs());
						det.setMontoDs(det.getMontoDs() - item.getImporteDs());
					}
				}
			}			
			
		} else {
			// Notas de credito de compra ToDo..
		}
	}
	
	/**
	 * inserta items de devolucion..
	 */
	private void addDevolucion() throws Exception {
		WindowPopup wp = new WindowPopup();
		wp.setHigth("550px");
		wp.setWidth("900px");
		wp.setDato(this);
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Devoluciones: " + this.selectedItemFac.getDescripcion());
		wp.setCheckAC(new ValidadorInsertarDevolucion());
		wp.show(ZUL_DETALLE_DEVOLUCION);

		if (wp.isClickAceptar()) {
			for (MyArray item : this._selectedItemsArt) {
				int cantidad = (this.dto.isMotivoDevolucion() || this.dto.isMotivoReclamo()) ? (int) item
						.getPos6() : (int) item.getPos3();
				double desc = (double) item.getPos8();
				double cant = (int) item.getPos3();
				double monto = (this.dto.isMotivoDevolucion() || this.dto.isMotivoReclamo()) ? 
						((double) item.getPos4() - (desc / cant)) : (double) item.getPos7();
				MyArray compra = this.selectedItemFac.getCompra();
				MyArray venta = this.selectedItemFac.getVenta();

				NotaCreditoDetalleDTO nvoItem = new NotaCreditoDetalleDTO(this.getIva10());
				nvoItem.setTipoDetalle(this.getItemTipoArticulo());
				nvoItem.setArticulo(item);
				nvoItem.setCantidad(cantidad);
				nvoItem.setMontoGs(monto);
				nvoItem.setCompra(compra);
				nvoItem.setVenta(venta);
				nvoItem.setDeposito(this.dto.isNotaCreditoCompra() ? this
						.getDepositoByFacturaCompra(compra.getId()) : null);
				this.dto.getDetalles().add(nvoItem);
			}
		}
		this._selectedItemsArt = null;
	}	
	
	/**
	 * Actualiza los montos aplicados en facturas..
	 */
	public void addImporteFactura(NotaCreditoDetalleDTO item) {		
		if ((this.dto.isNotaCreditoVenta() == true)
				|| (this.dto.isSolicitudNotaCreditoVenta() == true)) {
			
			for (NotaCreditoDetalleDTO det : this.dto.getDetallesFacturas()) {
				if (item.getVenta().getId().compareTo(det.getVenta().getId()) == 0){
					det.setMontoGs(det.getMontoGs() + item.getImporteGs());
					det.setMontoDs(det.getMontoDs() + item.getImporteDs());
				}
			}
			
		} else {
			// Notas de credito de compra ToDo..
		}
	}
	
	/**
	 * Confirma la nota de credito
	 * - marca al dto para actualizar los datos desde el assembler
	 */
	private void confirmarNC() throws Exception {
		if(this.mensajeSiNo("Desea confirmar la Nota de Crédito") == false)
			return;
		this.dto.setEstadoComprobante(this.getEstadoComprobanteCerrado());
		this.dto.setReadonly();
		this.dto.setActualizarDatos(true);
		this.dto = (NotaCreditoDTO) this.saveDTO(this.dto);
		this.setEstadoABMConsulta();
		Clients.showNotification("Nota de Crédito confirmada..");
	}
	
	/**
	 * Despliega el Reporte de Orden de Solicitud N.C..
	 */
	private void imprimirSolicitudNC() throws Exception {		
		String source = ReportesViewModel.SOURCE_SOLICITUD_NC;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new SolicitudNotaCreditoDataSource();
		params.put("Fecha", Utiles.getDateToString(this.dto.getFechaEmision(), Utiles.DD_MM_YYYY));
		params.put("Numero", this.dto.getNumero());
		params.put("Motivo", this.dto.getMotivo().getText().toUpperCase());
		params.put("Observacion", this.dto.getObservacion().toUpperCase());
		params.put("Cliente", this.dto.getCliente().getPos2());
		params.put("Usuario", getUs().getNombre());
		this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	private void imprimirComprobante(String source,
			Map<String, Object> parametros, JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions.createComponents(
				ReportesViewModel.ZUL_REPORTES, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * @return validacion de datos antes de grabar..
	 */
	private boolean validarFormulario() throws Exception {
		boolean out = true;
		this.msgError = "No se puede realizar la operación debido a: \n";

		if (this.dto.getDetallesFacturas().size() == 0) {
			out = false;
			this.msgError += "\n - Debe ingresar las Facturas..";
		}
		
		if ((this.dto.isNotaCreditoVenta() == false)
				&& (this.dto.getNumero().isEmpty())) {
			out = false;
			this.msgError += "\n - Debe ingresar el número..";
		}
		
		if((this.dto.isMotivoDescuento() == false)
				&& (this.dto.getDetallesArticulos().size() == 0)) {
			out = false;
			this.msgError += "\n - Debe ingresar al menos un ítem..";
		}
		
		if (this.dto.isNotaCreditoCompra() && this.isNotaCreditoDuplicado()) {
			out = false;
			this.msgError += "\n - Ya existe una Nota de Crédito con el mismo número..";
		}

		if (out == true) {
			if ((this.dto.isSolicitudNotaCreditoVenta() == true)
					&& (this.dto.esNuevo() == true)) {

				this.dto.setNumero(Configuracion.NRO_SOLICITUD_NC_VENTA
						+ "-"
						+ AutoNumeroControl.getAutoNumero(
								Configuracion.NRO_SOLICITUD_NC_VENTA, 7));

				this.addEventoAgenda(EVENTO_CREACION_SOLICITUD_NC
						+ this.dto.getNumero());

			} else if ((this.dto.isNotaCreditoVenta() == true)
					&& (this.dto.esNuevo() == true)) {
				this.dto.setNumero(Configuracion.NRO_NC_VENTA
						+ "-"
						+ AutoNumeroControl.getAutoNumero(
								Configuracion.NRO_NC_VENTA, 7));
			}
			this.updateImporte();
		}
		return out;
	}
	
	/**
	 * @return true si ya existe la nc en la bd..
	 */
	private boolean isNotaCreditoDuplicado() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		String numero = this.dto.getNumero();
		String timbrado = (String) this.dto.getTimbrado().getPos1();
		
		if (this.numeroOriginal.equals(numero)) {
			return false;
		}
		
		return rr.getNotaCreditoByNumero(numero, timbrado) != null;
	}
	
	/**************************************************************/
	
	
	/********************** APROBAR SOLICITUD *********************/
	
	/**
	 * Aprueba la Solicitud de N.C.
	 */
	@Command @NotifyChange("*")
	public void aprobarSolicitud() throws Exception {

		if (this.mensajeSiNo("Esta seguro de aprobar esta Solicitud de Nota de Crédito..") == false) {
			return;
		}

		this.dto.setEstadoComprobante(getDtoUtil()
				.getEstadoComprobanteAprobado());
		this.dto.setReadonly();
		this.saveDTO(dto);
		this.setEstadoABMConsulta();
		Clients.showNotification("Solicitud Aprobada..");
		this.getCtrAgenda()
				.addDetalle(this.getCtrAgendaTipo(), this.getCtrAgendaKey(), 0,
						EVENTO_APROBACION_SOLICITUD_NC, null);
	}
	
	/**************************************************************/
	
	
	/**************** CREACION DE SOLICITUD NCR *******************/
	
	/**
	 * Genera una Solicitud de NCR VTA para aplicarlo en una caja..
	 */
	public void addSolicitudNotaCreditoVenta(String numero, Date fecha,
			String codigoCliente, MyPair moneda, double totalGravado,
			double totalExenta, double totalIva) throws Exception {
		
		MyPair sucursal = this.utilDto.getMRA_sucursal();
		MyArray cliente = this.getClienteByCodigo(codigoCliente);
		String observacion = "Generado desde Sistema C.R.";
		double importe = totalExenta + totalGravado + totalIva;
		
		List<NotaCreditoDetalleDTO> detalles = this.getDetallesMRA(importe);

		NotaCreditoDTO nc = new NotaCreditoDTO();
		nc.setTipoMovimiento(this.tm_solicitud_NC_Venta);
		nc.setNumero(numero);
		nc.setFechaEmision(fecha);
		nc.setEstadoComprobante(this.estadoComprobanteAprobado);
		nc.setCliente(cliente);				
		nc.setImporteDs(importe);
		nc.setImporteGs(importe);
		nc.setImporteIva(totalIva);
		nc.setEnlace(numero);
		nc.setMoneda(moneda);
		nc.setMotivo(this.motivoDescuento);
		nc.setObservacion(observacion);
		nc.setSucursal(sucursal);
		nc.setTipoCambio(1);		
		nc.setDetalles(detalles);		
		nc.setReadonly();

		this.saveDTO(nc);		
	}
	
	/**
	 * @return los items de las notas de credito que se migran en MRA..
	 */
	private List<NotaCreditoDetalleDTO> getDetallesMRA(double importe) {
		List<NotaCreditoDetalleDTO> out = new ArrayList<NotaCreditoDetalleDTO>();
		
		MyArray venta = new MyArray();
		MyPair tipoDetalle = this.utilDto.getNotaCreditoDetalleFactura();
		
		NotaCreditoDetalleDTO item = new NotaCreditoDetalleDTO(this.getIva10());
		item.setCantidad(1);
		item.setImporteDs(importe);
		item.setImporteGs(importe);
		item.setMontoDs(importe);
		item.setMontoGs(importe);
		item.setTipoDetalle(tipoDetalle);
		item.setVenta(venta);
		out.add(item);
		
		return out;
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
	
	/**************************************************************/
	
	
	/******************** INFORMACION ADICIONAL *******************/
	
	@Override
	public void showInformacion() throws Exception {
		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.NUEVO);
		w.setTitulo("Información Adicional");
		w.setDato(this);
		w.setWidth("400px");
		w.setHigth("260px");
		w.setSoloBotonCerrar();
		w.show(Configuracion.INFO_ADICIONAL_NOTA_CREDITO_ZUL);
	}
	
	@Override
	public boolean getInformacionDeshabilitado() {
		return this.dto.esNuevo();
	}
	
	/**************************************************************/
	
	
	/**************************** AGENDA **************************/
	
	//Lista de eventos
	private static String EVENTO_CREACION_SOLICITUD_NC = "Se creó la Solicitud N.C. Nro. ";
	private static String EVENTO_APROBACION_SOLICITUD_NC = "Se aprobó la Solicitud de N.C.";
	
	@Override
	public int getCtrAgendaTipo() {
		return ControlAgendaEvento.NORMAL;
	}

	@Override
	public String getCtrAgendaKey() {
		return this.getClaveAgenda()[0];
	}

	@Override
	public String getCtrAgendaTitulo() {
		return this.getClaveAgenda()[1];
	}
	
	@Override
	public boolean getAgendaDeshabilitado() throws Exception {
		return this.dto.esNuevo();
	}
	
	/**
	 * Retorna la clave que sera la misma tanto para 
	 * la [ Solicitud N.C. - Nota de Crédito ] de esa 
	 * forma utilizan una sola agenda..
	 */
	private String[] getClaveAgenda() {

		String numero = "";
		String titulo = "SOLICITUD N.C. Nro. ";
		
		if (this.dto.isSolicitudNotaCreditoVenta() == true) {
			numero = this.dto.getNumero();			
			
		} else {
			numero = this.dto.getEnlace();
		}
		titulo += numero;

		return new String[] { numero, titulo };
	}
	
	/**************************************************************/
	
	
	/**************************** UTILES **************************/
	
	private UtilDTO utilDto = this.getDtoUtil();
	
	private MyArray tm_notaCreditoVenta  = this.utilDto.getTmNotaCreditoVenta();
	private MyArray tm_notaCreditoCompra = this.utilDto.getTmNotaCreditoCompra();
	private MyArray tm_solicitud_NC_Venta = this.utilDto.getTmSolicitudNotaCreditoVenta();
	private MyArray tm_solicitud_NC_Compra = this.utilDto.getTmSolicitudNotaCreditoCompra();
	
	private MyPair estadoComprobanteBorrador = this.utilDto.getEstadoComprobantePendiente();
	private MyPair estadoComprobanteAprobado = this.utilDto.getEstadoComprobanteAprobado();
	private MyPair motivoDescuento = this.utilDto.getMotivoNotaCreditoDescuento();
	
	/**
	 * Inicializa valores por defecto para un nuevo DTO..
	 */
	private void sugerirValores(NotaCreditoDTO dto){
		MyPair sucursal = this.getAcceso().getSucursalOperativa();
		
		dto.setTipoMovimiento(this.tipoMovimiento);
		dto.setSucursal(sucursal);
		dto.setEstadoComprobante(estadoComprobanteBorrador);
		dto.setMoneda(getDtoUtil().getMonedaGuarani());
		dto.setTipoCambio(getDtoUtil().getCambioCompraBCP(dto.getMoneda()));
		
		this.selectedFactura = new MyArray();
		this.selectedItemsArt = new ArrayList<NotaCreditoDetalleDTO>();
	}
	
	/**
	 * Para hacer un refresh de la pagina..
	 */
	@Command @NotifyChange("*")
	public void refresh(){		
	}
	
	/**
	 * Actualiza el valor del Tipo de cambio segun la moneda..
	 */
	@Command @NotifyChange("*")
	public void refreshTipoCambio(){
		this.dto.setTipoCambio(getDtoUtil().getCambioCompraBCP(this.dto.getMoneda()));
	}
	
	/**
	 * Concatena dos String.
	 */
	public String concat(String str1, String str2){
		return str1 + " - " + str2;
	}
	
	/**
	 * Para la lista del combobox del articulo..
	 */
	public String concatArticulo(String str1, String str2, int cant){
		return str1 + " - " + str2 + " - Cantidad: " + cant;
	}
	
	/**
	 * Actualiza el importe de la cabecera
	 */
	private void updateImporte() {
		double importeGs = (double) (this.dto.isMotivoDescuento() ? this.dto
				.getImportesFacturas()[0]
				: this.dto.getImportesDevoluciones()[0]);
		double importeDs = (double) (this.dto.isMotivoDescuento() ? this.dto
				.getImportesFacturas()[1]
				: this.dto.getImportesDevoluciones()[1]);
		this.dto.setImporteGs(importeGs);
		this.dto.setImporteDs(importeDs);
	}
	
	/**
	 * Crea una N.C. de tipo Venta a partir de la solicitud
	 */
	public NotaCreditoDTO crearNotaCreditoVentaDesde(NotaCreditoDTO desde,
			MyPair estadoCbte) throws Exception {

		return this.crearNotaCreditoDesde(desde, estadoCbte, getDtoUtil()
				.getTmNotaCreditoVenta(), true);
	}
	
	/**
	 * Crea una Nota de Credito a partir de la solicitud..
	 */
	private NotaCreditoDTO crearNotaCreditoDesde(NotaCreditoDTO desde,
			MyPair estadoCbte, MyArray tipoMovimiento, boolean grabar)
			throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String sigla = (String) tipoMovimiento.getPos2();

		NotaCreditoDTO out = new NotaCreditoDTO();
		out.setCliente(desde.getCliente());
		out.setEstadoComprobante(estadoCbte);
		out.setFechaEmision(new Date());
		out.setImporteDs(desde.getImporteDs());
		out.setImporteGs(desde.getImporteGs());
		out.setImporteIva(desde.getImporteIva());
		out.setEnlace(desde.getNumero());
		out.setCajaNro(desde.getCajaNro());
		out.setPlanillaCajaNro(desde.getPlanillaCajaNro());
		out.setCajero(desde.getCajero());
		out.setMoneda(desde.getMoneda());
		out.setMotivo(desde.getMotivo());
		out.setObservacion(desde.getObservacion());
		out.setProveedor(desde.getProveedor());
		out.setSucursal(desde.getSucursal());
		out.setTipoCambio(desde.getTipoCambio());
		out.setTipoMovimiento(tipoMovimiento);
		out.setDetalles(this.crearDetalleDesde(desde.getDetalles(), desde));
		out.setNumero(desde.getNumeroNotaCredito());
		out.setTimbrado_(desde.getTimbrado_());
		out.setAuxi(out.isNotaCreditoVentaContado() ? TipoMovimiento.NC_CONTADO : TipoMovimiento.NC_CREDITO);
		for (MyArray serv : desde.getServiciosTecnicos()) {
			out.getServiciosTecnicos().add(serv);
		}
		out.setReadonly();
		desde.getServiciosTecnicos().removeAll(desde.getServiciosTecnicos());

		if (grabar == true) {
			out = (NotaCreditoDTO) this.saveDTO(out, new NotaCreditoAssembler());
			desde.setEstadoComprobante(getDtoUtil().getEstadoComprobanteCerrado());
			desde.setEnlace(out.getNumero());
			desde.setReadonly();
			desde = (NotaCreditoDTO) this.saveDTO(desde, new NotaCreditoAssembler());
		}
		
		// actualiza el stock de los articulos..
		// asigna el nro de nc a la venta..
		if (sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA)) {				
			for (NotaCreditoDetalleDTO item : out.getDetallesArticulos()) {
				ArticuloDeposito adp = rr.getArticuloDeposito(item.getArticulo().getId(), Deposito.ID_DEPOSITO_PRINCIPAL);
				ControlArticuloStock.actualizarStock(adp.getId(), item.getCantidad(), this.getLoginNombre());
				ControlArticuloStock.addMovimientoStock(out.getId(), out
						.getTipoMovimiento().getId(), item.getCantidad(), adp
						.getId(), this.getLoginNombre());
			}
		}
		// referencia a los servicios tecnicos..
		for (MyArray servtec : out.getServiciosTecnicos()) {
			ServicioTecnico st = (ServicioTecnico) rr.getObject(ServicioTecnico.class.getName(), servtec.getId());
			st.setNumeroReclamo(out.getNumero());
			rr.saveObject(st, this.getLoginNombre());
		}		
		
		return out;
	}
	
	/**
	 * Devuelve una lista de detalles con el mismo contenido de la que recibe..
	 */
	private List<NotaCreditoDetalleDTO> crearDetalleDesde(
			List<NotaCreditoDetalleDTO> desde, NotaCreditoDTO notaCredito) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCreditoDetalleDTO> out = new ArrayList<NotaCreditoDetalleDTO>();
		
		for (NotaCreditoDetalleDTO item : desde) {
			double costoGs = 0;
			if ((!item.getArticulo().esNuevo()) && notaCredito.isMotivoDevolucion()) {
				Articulo art = rr.getArticuloById(item.getArticulo().getId());
				costoGs = art.getCostoGs();
			}
			NotaCreditoDetalleDTO nvo = new NotaCreditoDetalleDTO(this.getIva10());
			nvo.setArticulo(item.getArticulo());
			nvo.setCantidad(item.getCantidad());
			nvo.setCompra(item.getCompra());
			nvo.setGasto(item.getGasto());
			nvo.setCostoGs(costoGs);
			nvo.setImporteGs(item.getImporteGs());
			nvo.setImporteDs(item.getImporteDs());			
			nvo.setMontoGs(item.getMontoGs());
			nvo.setMontoDs(item.getMontoDs());
			nvo.setTipoDetalle(item.getTipoDetalle());
			nvo.setVenta(item.getVenta());
			out.add(nvo);
		}
		
		return out;
	}
	
	/**
	 * Devuelve la ruta del icono que representa el estado del documento..
	 */
	@DependsOn("dto.estadoComprobante")
	public String getSrcEstadoComprobante() {
		String siglaDto = this.dto.getEstadoComprobante().getSigla();
		String siglaCbte = getDtoUtil().getEstadoComprobantePendiente()
				.getSigla();
		boolean pendiente = siglaDto.compareTo(siglaCbte) == 0;		
		return pendiente? Config.ICONO_EXCLAMACION_16X16 : Config.IMAGEN_OK;
	}
	
	/**
	 * Devuelve el estilo del label del Tipo Movimiento
	 */
	public String getStyleTipoMovimiento(){
		boolean sncv = this.dto.isSolicitudNotaCreditoVenta();
		return sncv? STYLE_LABEL_BLUE : STYLE_LABEL_RED;
	}	
	
	/**************************************************************/	
	
	
	/**
	 * TESTING..
	 */
	static String[] attNC = { "fechaEmision", "numero",
			"cliente.empresa.razonSocial", "motivo.descripcion",
			"estadoComprobante.descripcion" };

	static String[] tiposNC = { Config.TIPO_DATE, Config.TIPO_STRING,
			Config.TIPO_STRING, Config.TIPO_STRING, Config.TIPO_STRING };

	static String[] colsNC = { "Fecha Emisión", "Número", "Cliente", "Motivo",
			"Estado" };

	static String[] anchosColNC = { "160px", "110px", "", "110px", "110px" };

	@Command
	@NotifyChange("*")
	public void devolucionConNotaDeCredito() throws Exception {

		String sigla = Configuracion.SIGLA_ESTADO_COMPROBANTE_APROBADO;
		long idSuc = this.getAcceso().getSucursalOperativa().getId().longValue();
		String where = "estadoComprobante.sigla = '" + sigla
				+ "' and sucursal.id = " + idSuc;

		BuscarElemento b = new BuscarElemento();
		b.setTitulo("Solicitudes de Nota de Crédito Aprobadas - Sucursal: "
				+ this.dto.getSucursal().getText());
		b.setWidth("960px");
		b.setHeight("430px");
		b.setClase(NotaCredito.class);
		b.setAssembler(new NotaCreditoAssembler());
		b.addWhere(where);
		b.addOrden("numero");
		b.setAtributos(attNC);
		b.setTipos(tiposNC);
		b.setNombresColumnas(colsNC);
		b.setAnchoColumnas(anchosColNC);
		b.setContinuaSiHayUnElemento(false);
		b.show("%");

		if (b.isClickAceptar()) {
			NotaCreditoDTO nc = (NotaCreditoDTO) b.getSelectedItemDTO();
			this.dto = this.crearNotaCreditoVentaDesde(nc, getDtoUtil().getEstadoComprobanteCerrado());
			this.setEstadoABMConsulta(); 
		}
	}	
	
	/**************************************************************/
	
	
	/************************* VALIDACIONES ***********************/
	
	/**
	 * Validador Insertar Devolucion
	 */
	class ValidadorInsertarDevolucion implements VerificaAceptarCancelar {

		private String mensaje = "";
		
		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			if(_selectedItemsArt == null || _selectedItemsArt.size() == 0) {
				this.mensaje += "\n - Debe seleccionar los ítems a devolver..";
				return false;
			}				
			
			for (MyArray item : _selectedItemsArt) {
				
				if(dto.isMotivoDevolucion()) {
					int devolucion = (int) item.getPos6();
					if(devolucion <= 0){
						out = false;
						this.mensaje += "\n - Las cantidades deben ser mayor a cero..";
					}
				}
				
				if(dto.isMotivoDiferenciaPrecio()) {
					double diferencia = (double) item.getPos7();
					if(diferencia <= 0){
						out = false;
						this.mensaje += "\n - Las diferencias deben ser mayor a cero..";
					}
				}				
				
				for (NotaCreditoDetalleDTO det : dto.getDetallesArticulos()) {
					if ((det.getArticulo().getId().longValue() == item.getId()
							.longValue())) {
						out = false;
						this.mensaje += "\n - Ya existe el ítem en el detalle..";
					}
				}
			}
			return out;
		}

		@Override
		public String textoVerificarAceptar() {
			return mensaje;
		}

		@Override
		public boolean verificarCancelar() {
			return true;
		}

		@Override
		public String textoVerificarCancelar() {
			return "Error al Cancelar";
		}
	}
	
	/**
	 * DataSource de la Solicitud de Nota de Credito..
	 */
	class SolicitudNotaCreditoDataSource implements JRDataSource {

		List<ServicioTecnicoDetalle> detalle = new ArrayList<ServicioTecnicoDetalle>();
		List<MyArray> dets = new ArrayList<MyArray>();
		String observacion = "";

		public SolicitudNotaCreditoDataSource() {
			this.observacion = dto.getObservacion();
			for (NotaCreditoDetalleDTO item : dto.getDetallesArticulos()) {
				this.dets.add(new MyArray("ÍTEMS", 
						item.getArticulo().getPos1(),
						item.getArticulo().getPos2(), 
						item.getCantidad() + ""));
			}
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			MyArray item = this.dets.get(index);
			if ("TituloDetalle".equals(fieldName)) {
				value = item.getPos1();
			} else if ("Codigo".equals(fieldName)) {
				value = item.getPos3();
			} else if ("Descripcion".equals(fieldName)) {
				value = item.getPos2();
			} else if ("Cantidad".equals(fieldName)) {
				value = item.getPos4();
			} 
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < dets.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}
	
	/**************************************************************/	
	
	
	/************************* GET / SET **************************/
	
	@DependsOn("filter_numero")
	public List<MyArray> getServiciosTecnicos() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> list = rr.getServiciosTecnicos_("", this.filter_numero, (String) this.dto.getCliente().getPos2(), "", "", "");
		for (Object[] item : list) {
			long id = (long) item[0];
			String nro = (String) item[1];
			MyArray my = new MyArray(nro);
			my.setId(id);
			out.add(my);
		}
		return out;
	}
	
	@DependsOn({ "dto.cliente", "dto.motivo", "dto.numero" })
	public boolean isDetalleVisible() {
		boolean newPersona = this.dto.isNotaCreditoVenta() ? this.dto
				.getCliente().esNuevo() : this.dto.getProveedor().esNuevo();
		boolean numeroOK = this.dto.isNotaCreditoVenta() ? true : !this.dto
				.getNumero().isEmpty();
		boolean timbradoOK = this.dto.isNotaCreditoVenta() ? true
				: !((String) this.dto.getTimbrado().getPos1()).isEmpty();
		return ((newPersona == false)
				&& (this.dto.getMotivo().esNuevo() == false)
				&& (numeroOK == true) && (timbradoOK == true));
	}
	
	@DependsOn("detalleVisible")
	public boolean isSolicitarCabeceraVisible() {
		return this.isDetalleVisible() == false;
	}
	
	@DependsOn({ "deshabilitado", "selectedItemFac", "dto" })
	public boolean isDeleteFacturaDisabled() {
		return this.isDeshabilitado() || this.selectedItemFac == null
				|| this.dto.getDetallesArticulos().size() > 0;
	}
	
	@DependsOn({ "deshabilitado", "selectedItemsArt" })
	public boolean isDeleteArticuloDisabled() {
		return this.isDeshabilitado() || this.selectedItemsArt.size() == 0;
	}
	
	@DependsOn({ "deshabilitado", "selectedItemFac" })
	public boolean isAddArticuloDisabled() {
		return this.isDeshabilitado() || this.selectedItemFac == null;
	}
	
	@DependsOn("dto")
	public boolean isDetalleArticuloVisible() {
		return this.dto.isMotivoDevolucion() || this.dto.isMotivoDiferenciaPrecio()
				|| this.dto.isMotivoReclamo();
	}
	
	@DependsOn({"deshabilitado", "dto"})
	public boolean isConfirmarDisabled() {
		return this.isDeshabilitado()
				|| this.dto.esNuevo()
				|| (this.dto.isMotivoDescuento() && this.dto
						.getDetallesFacturas().size() == 0);
	}
	
	/**
	 * @return el tipo de item articulo..
	 */
	private MyPair getItemTipoArticulo() {
		return this.getDtoUtil().getNotaCreditoDetalleArticulo();
	}
	
	public List<MyPair> getMotivosNCCompra() {
		return this.utilDto.getMotivosNotaCredito();
	}
	
	public List<MyPair> getMotivosNCVenta() {
		List<MyPair> out = new ArrayList<MyPair>();
		out.addAll(this.utilDto.getMotivosNotaCredito());
		out.remove(this.utilDto.getMotivoNotaCreditoDifPrecio());
		return out;
	}

	public NotaCreditoDTO getDto() {
		return dto;
	}

	public void setDto(NotaCreditoDTO dto) {
		this.dto = dto;
	}

	public NotaCreditoDetalleDTO getNvoItem() {
		return nvoItem;
	}

	public void setNvoItem(NotaCreditoDetalleDTO nvoItem) {
		this.nvoItem = nvoItem;
	}

	public MyArray getSelectedFactura() {
		return selectedFactura;
	}

	public void setSelectedFactura(MyArray selectedFactura) {
		this.selectedFactura = selectedFactura;
	}

	public boolean isMotivoEditable() {
		if (this.isDeshabilitado() == true) {
			return false;
		} else {
			return this.dto.isDetalleEmpty();
		}
	}	
	
	public boolean isAprobarEnabled() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();

		boolean habilitado = ((this.isDeshabilitado() == false) && (this.dto
				.esNuevo() == false));
		
		boolean permitido = rr.isOperacionHabilitada(this.getLoginNombre(), OPER_APROBAR_NC_DEV);

		boolean devolucionOK = (this.dto.isMotivoDevolucion() == true)
				&& (this.dto.getDetallesArticulos().size() > 0);

		boolean descuentoOK = (this.dto.isMotivoDevolucion() == false)
				&& (this.dto.getDetallesFacturas().size() > 0);

		return habilitado && (devolucionOK || descuentoOK) && permitido;
	}
	
	public boolean getCheckmarkFacturas() {
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getDetallesFacturas().size() > 0) {
			out = true;
		}
		return out;
	}

	public boolean getCheckmarkDevoluciones() {
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getDetallesArticulos().size() > 0) {
			out = true;
		}
		return out;
	}

	public String getLabelMonto() {
		return "Monto " + this.dto.getMoneda().getSigla();
	}

	public String getLabelImporte() {
		return "Importe " + this.dto.getMoneda().getSigla();
	}

	public List<NotaCreditoDetalleDTO> getSelectedItemsArt() {
		return selectedItemsArt;
	}

	public void setSelectedItemsArt(List<NotaCreditoDetalleDTO> selectedItemsArt) {
		this.selectedItemsArt = selectedItemsArt;
	}

	public NotaCreditoDetalleDTO getSelectedItemFac() {
		return selectedItemFac;
	}

	public void setSelectedItemFac(NotaCreditoDetalleDTO selectedItemFac) {
		this.selectedItemFac = selectedItemFac;
	}

	public List<MyArray> get_selectedItemsArt() {
		return _selectedItemsArt;
	}

	public void set_selectedItemsArt(List<MyArray> _selectedItemsArt) {
		this._selectedItemsArt = _selectedItemsArt;
	}

	public String getFilter_numero() {
		return filter_numero;
	}

	public void setFilter_numero(String filter_numero) {
		this.filter_numero = filter_numero;
	}

	public MyArray getSelectedServicioTecnico() {
		return selectedServicioTecnico;
	}

	public void setSelectedServicioTecnico(MyArray selectedServicioTecnico) {
		this.selectedServicioTecnico = selectedServicioTecnico;
	}
}

/**
 * Validador insertar factura..
 */
class ValidadorInsertarFactura implements VerificaAceptarCancelar {
	
	private NotaCreditoControlBody ctr;
	private NotaCreditoDetalleDTO nvoItem;
	private String mensaje = "";
	
	public ValidadorInsertarFactura(NotaCreditoControlBody ctr) {
		this.ctr = ctr;
		this.nvoItem = ctr.getNvoItem();
	}

	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		mensaje = "No se puede completar la operación debido a: \n";
		double importe = 0;

		if (nvoItem.getMontoGs() <= 0) {
			out = false;
			mensaje += "\n - El monto debe ser mayor a cero..";
		}
		
		if (this.ctr.getDto().isNotaCreditoVenta()) {
			importe = (double) this.nvoItem.getVenta().getPos5();
		} else {
			importe = this.nvoItem.isFacturaCompra() ? (double) this.nvoItem.getCompra().getPos5() : (double) this.nvoItem.getGasto().getPos5();
		}
		
		if (importe < nvoItem.getMontoGs()) {
			out = false;
			mensaje += "\n - El monto a aplicar no debe ser mayor al importe de la factura";
		}

		return out;
	}

	@Override
	public String textoVerificarAceptar() {
		return mensaje;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al Cancelar";
	}	
}
