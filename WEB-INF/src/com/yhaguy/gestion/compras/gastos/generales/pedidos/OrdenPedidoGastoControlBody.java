package com.yhaguy.gestion.compras.gastos.generales.pedidos;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Tab;

import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.CentroCosto;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.debitos.BancoDebitoDTO;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;
import com.yhaguy.gestion.compras.gastos.subdiario.AssemblerGasto;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoDetalleDTO;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class OrdenPedidoGastoControlBody extends BodyApp {
	
	static final String ZUL_ADD_ITEM_FAC = "/yhaguy/gestion/compras/gastos/generales/pedidos/insertarItem.zul";
	static final String ZUL_IMPORT_OC = "/yhaguy/gestion/compras/gastos/generales/pedidos/importarOrdenCompra.zul";

	private OrdenPedidoGastoDTO dto = new OrdenPedidoGastoDTO();
	private OrdenPedidoGastoDetalleDTO nvoDetalle;
	
	private GastoDTO dtoGasto = new GastoDTO();
	private GastoDetalleDTO gastoDetalle;

	private MyArray selectedAplicacionDebitoBancario;
	
	private boolean solicitarCabecera = false;
	private boolean noDeshabilitado = false;
	
	private List<OrdenPedidoGastoDetalleDTO> selectedItems;
	private List<OrdenPedidoGastoDetalleDTO> selectedItemsImportar;
	private List<GastoDetalleDTO> selectedItemsFac;
	private String itemsEliminar;
	private String mensaje;
	
	private String filterNumeroImportacion = "";
	private String filterNumeroFactura = "";
	private String filterRazonSocial = "";
	
	@Wire
	private Tab tabOrdenCompra;

	@Init(superclass = true)
	public void init() {
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}

	@Override
	public Assembler getAss() {
		return new AssemblerOrdenPedidoGasto();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public String getEntidadPrincipal() {
		return OrdenPedidoGasto.class.getName();
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		this.solicitarCabecera = true;		
		OrdenPedidoGastoDTO gasto = new OrdenPedidoGastoDTO();
		this.dtoGasto = new GastoDTO();
		this.sugerirValores(gasto);
		this.tabOrdenCompra.setSelected(true);
		return gasto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (OrdenPedidoGastoDTO) dto;
		if (this.dto.getGastos().size() > 0)
			this.dtoGasto = this.dto.getGastos().get(0);
		if (this.dtoGasto.esNuevo())
			this.inicializarGasto();
		
		if (this.dto.getEstado().equals(Configuracion.ESTADO_CANCELADO_KEY)) {			
			this.enmascararAnulados(true);
		} else {
			this.enmascararAnulados(false);		
		}
	}

	@Override
	public Browser getBrowser() {
		return new OrdenPedidoGastoBrowser();
	}
	
	@Override
	public boolean getImprimirDeshabilitado() throws Exception {
		return this.dto.isAutorizado() == false;
	};
	
	@Override
	public void showImprimir() throws Exception {
		this.imprimirOrdenCompra();
	}
	
	@Override
	public int getCtrAgendaTipo() {
		return ControlAgendaEvento.NORMAL;
	}

	@Override
	public String getCtrAgendaKey() {
		return this.dto.getNumero();
	}

	@Override
	public String getCtrAgendaTitulo() {
		return "[Orden de Compra: " + this.getCtrAgendaKey() + "]";
	}	
	
	@Override
	public boolean getAgendaDeshabilitado(){
		return this.dto.esNuevo();
	}
	
	@Override
	public boolean verificarAlGrabar() {
		return this.validarFormulario();
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensaje;
	}
	
	/*********************************************************/
	
	
	/************************ COMANDOS ***********************/
	
	@Command
	@NotifyChange("*")
	public void autorizarOrden() throws Exception {
		this.autorizarOrdenCompra();		
	}
	
	@Command
	@NotifyChange("*")
	public void eliminarItem() {
		this.deleteItem();
	}	
	
	@Command
	@NotifyChange("*")
	public void insertarItemDetalle() throws Exception {
		this.showItem(new OrdenPedidoGastoDetalleDTO(), WindowPopup.NUEVO);
	}
	
	@Command
	public void verItem(@BindingParam("item") OrdenPedidoGastoDetalleDTO item)
			throws Exception {
		this.showItem(item, WindowPopup.SOLO_LECTURA);
	}
	
	@Command
	@NotifyChange("*")
	public void asignarTimbrado() {
		this.asignacionTimbrado();
	}
	
	@Command
	@NotifyChange("dtoGasto")
	public void selectCondicion() {
		this.setCondicion();
	}
	
	@Command
	@NotifyChange("dtoGasto")
	public void selectVencimiento() {
		this.setVencimiento();
	}
	
	@Command
	@NotifyChange("*")
	public void insertItemFactura() throws Exception {
		this.insertarItemFactura();
	}
	
	@Command
	@NotifyChange("*")
	public void deleteItemFac() {
		this.deleteItemFactura();
	}
	
	@Command
	@NotifyChange("*")
	public void importarOrdenCompra() throws Exception {
		this.importarOC();
	}
	
	@Command
	@NotifyChange("*")
	public void confirmarGasto() throws Exception {
		this.confirmar();
	}
	
	/*********************************************************/
	
	
	/*********************** FUNCIONES ***********************/
	
	/**
	 * setea valores por defecto..
	 */
	private void sugerirValores(OrdenPedidoGastoDTO gasto) {
		gasto.setIdUsuarioCarga(this.getUs().getId());
		gasto.setNombreUsuarioCarga(this.getUs().getNombre());
		gasto.setCondicionPago(this.sugerirCondicion());
		gasto.setDepartamento(this.getAcceso().getDepartamento());
		gasto.setSucursal(this.getAcceso().getSucursalOperativa());
	}
	
	/**
	 * autoriza la orden de compra..
	 */
	private void autorizarOrdenCompra() throws Exception {
		if (mensajeSiNo("Desea autorizar la Orden nro: " + this.dto.getNumero()) == true) {
			this.dto.setAutorizado(true);
			this.dto.setEstado(Configuracion.ESTADO_AUTORIZADO_KEY);
			this.dto.setIdUsuarioAutoriza(this.getUs().getId());
			this.dto.setNombreUsuarioAutoriza(this.getUs().getNombre());
			this.dto.setFechaAutorizacion(new Date());
			this.dto = (OrdenPedidoGastoDTO) this.saveDTO(this.dto);
			this.setEstadoABMConsulta();
			mensajePopupTemporal("Orden de Compra autorizada..");
			this.addEventoAgenda("Se autorizó la Orden de Compra..");
		}
	}

	/**
	 * Sugiere un valor por defecto...
	 */
	public MyArray sugerirCondicion() {
		MyArray out = new MyArray();
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			CondicionPago pcp = rr.getCondicionPagoById(2);
			out.setId(pcp.getId());
			out.setPos1(pcp.getDescripcion());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * eliminar item..
	 */
	private void deleteItem() {
		if (this.mensajeSiNo("Desea eliminar los ítems seleccionados..")) {
			for (OrdenPedidoGastoDetalleDTO d : this.selectedItems) {
				this.dto.getOrdenPedidoGastoDetalle().remove(d);
			}
			this.selectedItems = new ArrayList<OrdenPedidoGastoDetalleDTO>();
		}
	}
	
	/**
	 * Despliega la ventana del detalle del pedido..
	 */
	private void showItem(OrdenPedidoGastoDetalleDTO item, String modo)
			throws Exception {

		this.nvoDetalle = item;
		item.setDepartamento(this.getDepartamento());
		item.setCentroCosto(this.getCentroCosto());
		item.setIva(this.getIva10());

		WindowPopup w = new WindowPopup();
		w.setModo(modo);
		w.setDato(this);
		w.setWidth("470px");
		w.setHigth("310px");
		w.setTitulo("Detalle del Pedido de Gasto");
		w.show(Configuracion.ORDEN_PEDIDO_GASTO_DETALLE_ZUL);

		if (w.isClickAceptar()) {
			this.addEventoAgenda(Configuracion.TEXTO_ITEM_AGREGADO
					+ this.nvoDetalle.getDepartamento().getPos1());
			this.dto.getOrdenPedidoGastoDetalle().add(nvoDetalle);
		}
	}
	
	/**
	 * impresion de la orden de compra..
	 */
	public void imprimirOrdenCompra() {
		List<Object[]> data = new ArrayList<>();
		Object[] obj;
		for (OrdenPedidoGastoDetalleDTO d : this.dto
				.getOrdenPedidoGastoDetalle()) {
			obj = new Object[] {
					d.getArticuloGasto().getCuentaContable().getPos1(),
					d.getArticuloGasto().getCuentaContable().getPos2(),
					d.getImporte() };
			data.add(obj);
		}

		ReporteOrdenCompra r = new ReporteOrdenCompra(this.dto);
		r.setDatosReporte(data);
		r.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonCancelar(false);
		vp.setBotonImprimir(false);
		vp.showReporte(r, this);
	}
	
	/**
	 * inicializa los datos de la factura de Gasto..
	 */
	private void inicializarGasto() {
		this.dtoGasto.setTipoMovimiento(this.getDtoUtil().getTmFacturaGastoContado());
		this.dtoGasto.setCondicionPago(this.getDtoUtil().getCondicionPagoContado());
		this.dtoGasto.setProveedor(this.dto.getProveedor());
		this.dtoGasto.setEstadoComprobante(this.getEstadoComprobantePendiente());
		this.dtoGasto.setFecha(new Date());
		this.dtoGasto.setVencimiento(new Date());
		this.dtoGasto.setFondoFijo(false);
		this.dtoGasto.setMoneda(this.getDtoUtil().getMonedaGuaraniConSimbolo());
		this.dtoGasto.setSucursal(this.dto.getSucursal());
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	/**
	 * asignar timbrado..
	 */
	private void asignacionTimbrado() {
		WindowTimbrado w = new WindowTimbrado();
		w.setIdProveedor(this.dtoGasto.getProveedor().getId());
		w.setTimbrado("%");
		w.show(WindowPopup.NUEVO, w);
		if (w.isClickAceptar()) {
			this.dtoGasto.setTimbrado(w.getSelectedTimbrado());
		}	
	}
	
	/**
	 * asignacion de la condicion..
	 */
	private void setCondicion() {
		String sigla = (String) this.dtoGasto.getTipoMovimiento().getPos2();
		if (sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO)) {
			this.dtoGasto.setCondicionPago(this.getDtoUtil()
					.getCondicionPagoCredito30());
		} else {
			this.dtoGasto.setCondicionPago(this.getDtoUtil()
					.getCondicionPagoContado());
		}
		this.setVencimiento();
	}
	
	/**
	 * asignar vencimiento..
	 */
	private void setVencimiento() {
		if (this.dtoGasto.isCredito() == false) {
			this.dtoGasto.setVencimiento(this.dtoGasto.getFecha());
		} else {
			this.dtoGasto.setVencimiento(this.m.agregarDias(this.dtoGasto
					.getFecha(), (int) this.dtoGasto.getCondicionPago()
					.getPos2()));
		}
	}
	
	/**
	 * insertar item en factura..
	 */
	private void insertarItemFactura() throws Exception {
		this.gastoDetalle = new GastoDetalleDTO();
		this.gastoDetalle.setCentroCosto(this.getCentroCosto());
		this.gastoDetalle.setTipoIva(this.getDtoUtil().getTipoIva10());
		this.gastoDetalle.setCantidad(1);

		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.NUEVO);
		w.setDato(this);
		w.setWidth("470px");
		w.setHigth("310px");
		w.setTitulo("Ítem Factura Gasto");
		w.show(ZUL_ADD_ITEM_FAC);
		if (w.isClickAceptar()) {
			this.dtoGasto.getDetalles().add(this.gastoDetalle);
		}
		this.gastoDetalle = null;
	}
	
	/**
	 * eliminar item factura..
	 */
	private void deleteItemFactura() {
		if(this.mensajeSiNo("Desea eliminar los ítems seleccionados..") == false)
			return;
		this.dtoGasto.getDetalles().removeAll(this.selectedItemsFac);
		this.selectedItemsFac = null;
	}
	
	/**
	 * importar items de la orden de compra..
	 */
	private void importarOC() throws Exception {
		this.selectedItemsImportar = new ArrayList<OrdenPedidoGastoDetalleDTO>();
		WindowPopup wp = new WindowPopup();
		wp.setModo(WindowPopup.NUEVO);
		wp.setCheckAC(null);
		wp.setDato(this);
		wp.setHigth("500px");
		wp.setWidth("800px");
		wp.setTitulo("Importar ítems de la Orden de Compra");		
		wp.show(ZUL_IMPORT_OC);
		if (wp.isClickAceptar()) {
			this.importOrdenCompra();
		}
	}
	
	/**
	 * importa los items de la orden de compra..
	 */
	private void importOrdenCompra() {
		for (OrdenPedidoGastoDetalleDTO item : this.selectedItemsImportar) {
			GastoDetalleDTO det = new GastoDetalleDTO();
			det.setArticuloGasto(item.getArticuloGasto());
			det.setCantidad(1);
			det.setCentroCosto(item.getCentroCosto());
			det.setMontoGs(item.getImporte());
			det.setObservacion(item.getDescripcion());
			det.setTipoIva(this.getIva(item.getIva()));
			det.getIvaCalculado();
			this.dtoGasto.getDetalles().add(det);
		}
	}

	/**
	 * confirmacion del gasto..
	 */
	private void confirmar() throws Exception {
		if(this.mensajeSiNo("Desea confirmar el Gasto..") == false)
			return;
		this.dtoGasto.setEstadoComprobante(this.getEstadoComprobanteCerrado());
		this.dtoGasto.setObservacion(this.dto.getDescripcion());
		this.dtoGasto = (GastoDTO) this.saveDTO(this.dtoGasto, new AssemblerGasto());
		this.dto.setReadonly();
		this.dto.setConfirmado(true);
		this.dto.getGastos().add(this.dtoGasto);
		this.dto = (OrdenPedidoGastoDTO) this.saveDTO(this.dto);
		this.setEstadoABMConsulta();
		if (!this.dtoGasto.isNo_generar_saldo()) {
			this.actualizarCtaCteProveedor(this.dtoGasto);
		}		
		this.generarDebitoBancario(this.dtoGasto);
		Clients.showNotification("Gasto confirmado..");		
	}
	
	/**
	 * Actualiza la Cta Cte del Proveedor..
	 */
	private void actualizarCtaCteProveedor(GastoDTO gasto) throws Exception {

		ControlCtaCteEmpresa ctr = new ControlCtaCteEmpresa(null);

		String sigla = (String) gasto.getTipoMovimiento().getPos2();
		long idEmpresa = (long) this.dto.getProveedor().getPos6();
		
		if (gasto.isGastoImportacion()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			try {
				Proveedor desp = rr.getProveedores(gasto.getDespachante()).get(0);
				idEmpresa = desp.getEmpresa().getId();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		
		MyPair empresa = new MyPair(idEmpresa);
		long idMoneda = gasto.getMoneda().getId();
		MyPair moneda = new MyPair(idMoneda);
		moneda.setSigla((String) gasto.getMoneda().getPos2());
		MyPair caracter = getDtoUtil().getCtaCteEmpresaCaracterMovProveedor();
		MyArray sucursal = new MyArray();
		sucursal.setId(this.dto.getSucursal().getId());

		double importe = gasto.getImporteTotal(moneda.getSigla());

		// verifica que sea movimiento credito o contado
		if (sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO)) {
			ctr.addCtaCteEmpresaMovimientoFacturaCredito(empresa,
					gasto.getId(), gasto.getNumeroFactura(), gasto.getFecha(),
					0, 1, importe, 0, importe, moneda,
					gasto.getTipoMovimiento(), caracter, sucursal, gasto.getNumeroImportacion(),
					gasto.getTipoCambio());

		} else if (sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO) 
				|| sigla.equals(Configuracion.SIGLA_TM_BOLETA_VENTA)) {

			ctr.addCtaCteEmpresaMovimientoFacturaContado(empresa,
					gasto.getId(), gasto.getNumeroFactura(), gasto.getFecha(),
					importe, moneda, gasto.getTipoMovimiento(), caracter,
					sucursal, true, gasto.getNumeroImportacion());
		}
	}
	
	/**
	 * Generar debito bancario
	 */
	private void generarDebitoBancario(GastoDTO gasto) 
		throws Exception {
		if (!gasto.isDebitoBancario()) {
			return;
		}
		BancoDebitoDTO debDto = new BancoDebitoDTO();
		debDto.setConfirmado(true);
		debDto.setCuenta((BancoCtaDTO) this.getDTOById(BancoCta.class.getName(), (long) 1, new AssemblerBancoCtaCte()));
		debDto.setDescripcion(gasto.getDetalles().get(0).getArticuloGasto().getDescripcion());
		debDto.setFecha(gasto.getFecha());
		debDto.setImporte(gasto.getTotalImporteGs());
		debDto.setNumero(gasto.getNumeroFactura());
		debDto.setSucursal(gasto.getSucursal());
		
		ControlBancoMovimiento.addDebitoBancario(debDto, this.getLoginNombre());
	}
	
	/**
	 * @return las facturas de proveedor con saldo..
	 */
	@DependsOn({ "filterNumeroFactura", "filterRazonSocial" })
	public List<MyArray> getFacturasConSaldo() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CtaCteEmpresaMovimiento> movims = rr.getMovimientosConSaldo(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR);
		for (CtaCteEmpresaMovimiento movim : movims) {
			String razonSocial = (String) rr.getDatosEmpresa(movim.getIdEmpresa())[0];
			if (movim.getNroComprobante().contains(this.filterNumeroFactura)
					&& razonSocial.toUpperCase().contains(this.filterRazonSocial.toUpperCase())) {
				MyArray my = new MyArray(movim.getNroComprobante(), razonSocial, movim.getSaldo());
				my.setId(movim.getId());
				out.add(my);
			}
		}
		return out;
	}
	
	/**
	 * @return validacion los datos del Formulario..
	 */
	public boolean validarFormulario() {
		boolean out = true;
		this.mensaje = "No se puede continuar debido a:";

		try {
			boolean duplicado = this.isGastoDuplicado();

			if (this.dto.getProveedor().esNuevo() == true) {
				this.mensaje += "\n - Debe seleccionar un proveedor..";
				out = false;
			}

			if (this.dto.getOrdenPedidoGastoDetalle().size() == 0) {
				this.mensaje += "\n - Debe ingresar al menos un ítem..";
				out = false;
			}

			if (this.dto.getDescripcion().trim().length() == 0) {
				this.mensaje += Configuracion.TEXTO_ERROR_CAMPOS_REQUERIDOS;
				out = false;
			}

			if ((this.dtoGasto.getNumeroFactura().isEmpty() == false)
					&& (this.dtoGasto.getDetalles().size() == 0)) {
				this.mensaje += "\n - Debe ingresar al menos un ítem a la factura..";
				out = false;
			}
			
			if ((this.dtoGasto.getNumeroImportacion().isEmpty())
					&& (this.dtoGasto.isGastoImportacion())) {
				this.mensaje += "\n - Debe asignar la importación..";
				out = false;
			}
			
			if (this.dtoGasto.isDebitoBancario() && this.dtoGasto.getBanco() == null) {
				this.mensaje += "\n - Debe seleccionar el Banco a debitar..";
				out = false;
			}

			if (duplicado) {
				this.mensaje += "\n - Ya existe un Gasto con el mismo numero de factura y timbrado..";
				out = false;
			}

			if ((this.dto.isAutorizado())
					&& (this.dtoGasto.getDetalles().size() > 0)
					&& (duplicado == false)) {
				this.dtoGasto = (GastoDTO) this.saveDTO(this.dtoGasto,
						new AssemblerGasto());
				this.dto.getGastos().add(this.dtoGasto);
			}

			if (this.dto.esNuevo() == true && out == true) {
				this.dto.setNumero(AutoNumeroControl.getAutoNumeroKey(
						Configuracion.NRO_ORDEN_PEDIDO_GASTO, 7));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	//Para evitar ingresar una nueva Factura con nro y timbrado duplicado..
	private boolean isGastoDuplicado() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		String timbrado = (String) this.dtoGasto.getTimbrado().getPos1();
		String numero = this.dtoGasto.getNumeroFactura();
		return rr.getGastoByNumero(numero, timbrado) != null;
	}
	
	/*********************************************************/

	
	/************** UPLOAD DE PRESUPUESTOS *******************/
	
	@SuppressWarnings("static-access")
	//Upload Pedido
	@Listen("onUpload=#presupuestoUpload")
	public void uploadPresupuesto(UploadEvent event) throws Exception {

		if (this.dto.esNuevo() == true) {
			this.mensajeError("El pedido aún no fue guardado..");
			return;
		}

		String name = "/" + this.dto.getNumero() + "_" + m.getIdUnique();
		String path = Sessions.getCurrent().getWebApp()
				.getRealPath(Configuracion.pathPresupuestoGasto);

		Media media = event.getMedia();
		
		if (media.getContentType().compareTo("application/pdf") != 0) {
			this.mensajeError("Solo es permitido adjuntar en formato pdf..");
			return;
		}

		InputStream file = new ByteArrayInputStream(media.getByteData());
		m.uploadFile(path, name, ".pdf", file);
		
		this.getCtrAgenda().addDetalle(this.getCtrAgendaTipo(), this.getCtrAgendaKey(), 
				0, "Presupuesto Adjuntado", Configuracion.pathPresupuestoGasto + name + ".pdf");

		BindUtils.postNotifyChange(null, null, this, "*");
		
		this.mensajePopupTemporal("Presupuesto Adjuntado - Ver en la agenda");
	}
	
	/*********************************************************/
	
	@DependsOn("filterNumeroImportacion")
	public List<MyArray> getImportaciones() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> list = rr.getImportaciones(this.filterNumeroImportacion);
		for (Object[] imp : list) {
			long id = (long) imp[0];
			String nro = (String) imp[1];
			String desp = (String) imp[2];
			MyArray my = new MyArray(nro, desp);
			my.setId(id);
			out.add(my);
		}
		return out;
	}
	
	/**
	 * @return la descripcion del estado segun los valores: PEN: Pendiente, AUT:
	 * Autorizado, CAN Cancelado, CER Cerrado...
	 */
	public String getEstado() {
		String out = "";
		if (this.dto.getEstado().compareTo(Configuracion.ESTADO_PENDIENTE_KEY) == 0) {
			out = "Pendiente";
		}
		if (this.dto.getEstado().compareTo(Configuracion.ESTADO_AUTORIZADO_KEY) == 0) {
			out = "Autorizado";
		}
		if (this.dto.getEstado().compareTo(Configuracion.ESTADO_CANCELADO_KEY) == 0) {
			out = "Cancelado";
		}
		if (this.dto.getEstado().compareTo(Configuracion.ESTADO_CERRADO_KEY) == 0) {
			out = "Cerrado";
		}
		return out;
	}
	
	/**
	 * Habilitar/deshabilitar accion adjuntar presupuesto
	 */
	public boolean isAdjuntarDisabled() {
		return (this.isDeshabilitado() == true) 
				|| (this.dto.esNuevo() == true)
				|| (this.dto.isAutorizado() == true);
	}
	
	/**
	 * Habilitar/deshabilitar accion eliminar item
	 */
	@DependsOn({"deshabilitado", "selectedItems", "dto"})
	public boolean isEliminarItemDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.selectedItems == null)
				|| (this.selectedItems.size() == 0)
				|| (this.dto.isAutorizado() == true);
	}
	
	/**
	 * Habilitar/deshabilitar accion insertar item
	 */
	public boolean isInsertarItemDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.dto.isAutorizado() == true);
	}
	
	/**
	 * Habilitar/deshabilitar accion autorizar pedido
	 */
	public boolean isAutorizarDisabled() {
		boolean operacionHabilitada = false;
		try {
			operacionHabilitada = this
					.operacionHabilitada(ID.O_ORDEN_PEDIDO_GASTO_AUTORIZAR) == true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (this.isDeshabilitado() == true) || (this.dto.esNuevo() == true)
				|| (this.dto.isAutorizado() == true)
				|| (operacionHabilitada == false);
	}
	
	@DependsOn({ "dto.proveedor", "dto.descripcion" })
	public boolean isDetalleVisible() {
		return (this.dto.getProveedor().esNuevo() == false) && (this.dto.getDescripcion().isEmpty() == false);
	}
	
	@DependsOn({ "solicitarCabecera", "deshabilitado" })
	public boolean isSolicitarCabeceraVisible() {
		return (this.solicitarCabecera == true) && (this.isDeshabilitado() == false);
	}
	
	@DependsOn({ "dto.autorizado", "dtoGasto.numeroFactura", "dtoGasto.timbrado" })
	public boolean isDetalleFacturaVisible() {
		return (this.dto.isAutorizado())
				&& (this.dtoGasto.getNumeroFactura().isEmpty() == false)
				&& (((String) this.dtoGasto.getTimbrado().getPos1()).isEmpty() == false);
	}
	
	@DependsOn({ "dtoGasto", "deshabilitado" })
	public boolean isCheckmarkFacVisible() {
		return this.isDeshabilitado() == false
				&& this.dtoGasto.getDetalles().size() > 0;	
	}
	
	@DependsOn({"deshabilitado", "selectedItemsFac"})
	public boolean isEliminarItemFacDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.selectedItemsFac == null)
				|| (this.selectedItemsFac.size() == 0);
	}
	
	@DependsOn({"deshabilitado", "dtoGasto"})
	public boolean isConfirmarDisabled() {
		return this.isDeshabilitado() || this.dtoGasto.esNuevo();
	}
	
	/**
	 * @return los bancos..
	 */
	public List<MyArray> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<BancoCta> bancos = rr.getBancosCta();
		List<MyArray> out = new ArrayList<MyArray>();
		for (BancoCta banco : bancos) {
			MyArray my = new MyArray(banco.getBanco().getDescripcion().toUpperCase());
			my.setId(banco.getId());
			out.add(my);
		}
		return out;
	}
	
	public double getTotalImporte() {
		double out = 0;
		List<OrdenPedidoGastoDetalleDTO> ldetalle = this.dto
				.getOrdenPedidoGastoDetalle();
		for (int i = 0; i < ldetalle.size(); i++) {
			OrdenPedidoGastoDetalleDTO odetalle = ldetalle.get(i);
			out = out + odetalle.getImporte();
		}
		return out;
	}
	
	public List<MyArray> getMovimientosDeGasto() {
		List<MyArray> out = this.getDtoUtil().getMovimientosDeGasto();
		out.remove(this.getDtoUtil().getTmOrdenGasto());
		return out;
	}
	
	private MyArray getDepartamento() {
		return this.getDtoUtil().getDepartamentos().get(0);
	}
	
	private MyArray getIva(MyPair iva) {
		MyArray out = new MyArray();
		out.setId(iva.getId());
		out.setPos1(iva.getText());
		out.setPos2(iva.getSigla());
		return out;
	}
	
	private MyArray getCentroCosto() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CentroCosto cc = (CentroCosto) rr.getObject(CentroCosto.class.getName(), 1);
		MyArray out = new MyArray();
		out.setId(cc.getId());
		out.setPos1(cc.getDescripcion());
		return out;
	}
	
	public boolean getCheckmarkDetalle() {
		boolean out = false;
		if (this.isDeshabilitado() == false
				&& this.dto.getOrdenPedidoGastoDetalle().size() > 0) {
			out = true;
		}
		return out;
	}

	public boolean isSolicitarCabecera() {
		return solicitarCabecera;
	}

	public void setSolicitarCabecera(boolean solicitarCabecera) {
		this.solicitarCabecera = solicitarCabecera;
	}	
	public OrdenPedidoGastoDTO getDto() {
		return dto;
	}

	public void setDto(OrdenPedidoGastoDTO dto) {
		this.dto = dto;
	}

	public OrdenPedidoGastoDetalleDTO getNvoDetalle() {
		return nvoDetalle;
	}

	public void setNvoDetalle(OrdenPedidoGastoDetalleDTO nvoDetalle) {
		this.nvoDetalle = nvoDetalle;
	}
	
	public List<OrdenPedidoGastoDetalleDTO> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<OrdenPedidoGastoDetalleDTO> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public String getItemsEliminar() {
		return itemsEliminar;
	}

	public void setItemsEliminar(String itemsEliminar) {
		this.itemsEliminar = itemsEliminar;
	}

	public GastoDTO getDtoGasto() {
		return dtoGasto;
	}

	public void setDtoGasto(GastoDTO dtoGasto) {
		this.dtoGasto = dtoGasto;
	}

	public GastoDetalleDTO getGastoDetalle() {
		return gastoDetalle;
	}

	public void setGastoDetalle(GastoDetalleDTO gastoDetalle) {
		this.gastoDetalle = gastoDetalle;
	}

	public List<GastoDetalleDTO> getSelectedItemsFac() {
		return selectedItemsFac;
	}

	public void setSelectedItemsFac(List<GastoDetalleDTO> selectedItemsFac) {
		this.selectedItemsFac = selectedItemsFac;
	}

	public List<OrdenPedidoGastoDetalleDTO> getSelectedItemsImportar() {
		return selectedItemsImportar;
	}

	public void setSelectedItemsImportar(
			List<OrdenPedidoGastoDetalleDTO> selectedItemsImportar) {
		this.selectedItemsImportar = selectedItemsImportar;
	}
	
	public boolean isNoDeshabilitado() {
		return noDeshabilitado;
	}

	public void setNoDeshabilitado(boolean noDeshabilitado) {
		this.noDeshabilitado = noDeshabilitado;
	}

	public String getFilterNumeroImportacion() {
		return filterNumeroImportacion;
	}

	public void setFilterNumeroImportacion(String filterNumeroImportacion) {
		this.filterNumeroImportacion = filterNumeroImportacion;
	}

	public String getFilterNumeroFactura() {
		return filterNumeroFactura;
	}

	public void setFilterNumeroFactura(String filterNumeroFactura) {
		this.filterNumeroFactura = filterNumeroFactura;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public MyArray getSelectedAplicacionDebitoBancario() {
		return selectedAplicacionDebitoBancario;
	}

	public void setSelectedAplicacionDebitoBancario(MyArray selectedFactura) {
		this.selectedAplicacionDebitoBancario = selectedFactura;
	}
}

/**
 * Reporte de Orden de Compra..
 */
class ReporteOrdenCompra extends ReporteYhaguy {
	
	private OrdenPedidoGastoDTO gasto;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Importe", TIPO_DOUBLE, 30, true);
	
	public ReporteOrdenCompra(OrdenPedidoGastoDTO gasto) {
		this.gasto = gasto;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Orden de Compra Nro. " + gasto.getNumero());
		this.setDirectorio("gastos/pedidos");
		this.setNombreArchivo("OrdenCompra-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.gasto.getNumero();
		String origen = this.gasto.getSucursal().getText();
		String proveedor = (String) this.gasto.getProveedor().getPos2();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", numero)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Sucursal", origen)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Proveedor", proveedor)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
