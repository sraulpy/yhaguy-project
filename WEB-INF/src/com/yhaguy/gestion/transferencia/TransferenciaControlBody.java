package com.yhaguy.gestion.transferencia;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.alerta.ControlAlertas;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.gestion.articulos.buscador.BuscadorArticulosViewModel;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.gestion.comun.ControlArticuloStock;
import com.yhaguy.gestion.comun.ControlLogica;
import com.yhaguy.gestion.comun.ReservaDTO;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class TransferenciaControlBody extends BodyApp {

	static final String NRO_TRFI_KEY = Configuracion.NRO_TRANSFERENCIA_INTERNA;
	static final String NRO_TRFE_KEY = Configuracion.NRO_TRANSFERENCIA_EXTERNA;

	static final String ZUL_PRINT = "/yhaguy/gestion/transferencia/impresion.zul";

	private TransferenciaDTO dto = new TransferenciaDTO();
	private TransferenciaDetalleDTO nvoDetalle;
	private boolean solicitarCabecera = false;
	private List<TransferenciaDetalleDTO> selectedItems;
	private String mensajeValidacion;
	List<MyPair> sucursales = new ArrayList<MyPair>();

	private ControlAlertas ctrAlertas = new ControlAlertas();
	private ControlLogica ctr = new ControlLogica(null);

	private Window selectPrint;
	private Window win;	

	/**************************************************/

	@Init(superclass = true)
	public void init() {
		this.setSucursales(this.getDtoUtil().getSucursalesMyPair()); 
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public Assembler getAss() {
		return new AssemblerTransferencia();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public boolean getImprimirDeshabilitado() throws Exception {
		return this.dto.esNuevo() || this.isTransferido() == false;
	}

	@Override
	public boolean getInformacionDeshabilitado() throws Exception {
		return this.dto.esNuevo();
	}

	@Override
	public void showInformacion() throws Exception {
		this.verInformacion();
	}

	@Override
	public void showImprimir() {
		this.imprimir();
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (TransferenciaDTO) dto;
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		this.solicitarCabecera = true;

		TransferenciaDTO dto = new TransferenciaDTO();
		this.inicializarValores(dto);

		return dto;
	}

	@Override
	public String getEntidadPrincipal() {
		return Transferencia.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}

	@Override
	public Browser getBrowser() {
		return this.isTransferenciaInterna() ? new TransferenciaBrowser()
				: new TransferenciaExternaBrowser();
	}

	@Override
	public boolean verificarAlGrabar() {
		return this.validarFormulario();
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeValidacion;
	}

	@Override
	public int getCtrAgendaTipo() {
		return ControlAgendaEvento.COTIZACION;
	}

	@Override
	public String getCtrAgendaKey() {
		return this.dto.getNumero();
	}

	@Override
	public String getCtrAgendaTitulo() {
		return "[Transferencia: " + this.getCtrAgendaKey() + "]";
	}

	@Override
	public boolean getAgendaDeshabilitado() throws Exception {
		return this.dto.esNuevo();
	}

	/**************************************************/

	
	/******************** COMANDOS ********************/

	@Command
	@NotifyChange("dto")
	public void insertarItem() throws Exception {
		this.insertarItem(new TransferenciaDetalleDTO(), false);
	}

	@Command
	@NotifyChange("dto")
	public void editarItem(@BindingParam("item") TransferenciaDetalleDTO item)
			throws Exception {
		long idArticulo = item.getArticulo().getId().longValue();
		long idDeposito = this.dto.getDepositoSalida().getId().longValue();
		long stockDisponible = this.ctr.stockDisponible(idArticulo, idDeposito);
		item.getArticulo().setPos3(stockDisponible);
		this.insertarItem(item, true);
	}

	@Command
	@NotifyChange("*")
	public void eliminarItem() throws Exception {
		if (this.confirmarEliminarItems() == true) {
			for (TransferenciaDetalleDTO item : this.selectedItems) {
				this.addEventoAgenda(Configuracion.ELIMINAR_ITEM
						+ "transferencia, art. " + item.getArticulo().getPos1());
				this.dto.getDetalles().remove(item);

			}
			this.selectedItems = null;
		}
	}

	/**
	 * Confirmacion de transferencia..
	 */
	@Command
	@NotifyChange("*")
	public void confirmarTransferencia() throws Exception {

		if (this.verificarStock() == false) {
			this.mensajeError("Verificar Stock..");
			return;
		}

		if (this.mensajeSiNo("Esta seguro de confirmar la transferencia..") == false) {
			return;
		}

		for (TransferenciaDetalleDTO item : this.dto.getDetalles()) {
			item.setEstado(TransferenciaDetalleDTO.ESTADO_CONFIRMADO);
		}

		if (this.isTransferenciaInterna()) {
			this.confirmarTransferenciaInterna();
		} else {
			this.confirmarTransferenciaExterna();
		}
		this.addEventoAgenda("Se confirmó la transferencia.");
		this.grabarEventosAgenda();
		Clients.showNotification("Transferencia confirmada..");
	}

	@Command
	public void printDocumentoInterno() {
		this.selectPrint.detach();
		this.imprimirTransferencia();
	}

	@Command
	public void imprimirRemision() {
		this.selectPrint.detach();
		String source = ReportesViewModel.SOURCE_REMISION;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new RemisionDataSource();
		params.put("RazonSocial", Configuracion.empresa);
		params.put("Ruc", "80024884-8");
		params.put("Numero", this.dto.getObservacion());
		params.put("FechaTraslado", this.m.dateToString(this.dto.getFechaCreacion(), Misc.DD_MM_YYYY));
		this.imprimirComprobante(source, params, dataSource);	
	}

	/**************************************************/

	/******************* FUNCIONES ********************/

	/**
	 * confirmacion de transferencia interna..
	 */
	private void confirmarTransferenciaInterna() throws Exception {
		MyPair estado = this.getDtoUtil().getEstadoTransferenciaConfirmada();
		this.setNumero(this.dto);
		this.dto.setTransferenciaEstado(estado);
		this.dto.setReadonly();
		this.dto = (TransferenciaDTO) this.saveDTO(this.dto);
		this.finalizarReserva(this.dto);
		this.transferirStock(false);
		this.setEstadoABMConsulta();
	}

	/**
	 * confirmacion de transferencia externa..
	 */
	private void confirmarTransferenciaExterna() throws Exception {
		MyPair estado = this.getDtoUtil().getEstadoTransferenciaConfirmada();
		this.setNumero(this.dto);
		this.dto.setTransferenciaEstado(estado);
		this.dto.setReadonly();
		this.dto = (TransferenciaDTO) this.saveDTO(this.dto);
		this.transferirStock(true);
		if (!this.isTransferenciaInterna()) {
			this.actualizarCtaCte();
		}
		this.setEstadoABMConsulta();
		Clients.showNotification("Transferencia Confirmada..");
	}

	/**
	 * inserta o modifica un item..
	 */
	private void insertarItem(TransferenciaDetalleDTO item, boolean edit)
			throws Exception {
		this.nvoDetalle = item;

		WindowPopup wp = new WindowPopup();
		wp.setTitulo("Insertar ítem..");
		wp.setCheckAC(new ValidadorInsertarItem(edit));
		wp.setDato(this);
		wp.setModo(WindowPopup.NUEVO);
		wp.setWidth("400px");
		wp.setHigth("330px");
		wp.show(Configuracion.INSERTAR_ITEM_TRANSFERENCIA_ZUL);

		if (wp.isClickAceptar()) {
			if (edit) {
				item.setEditado(true);
				this.addEventoAgenda(Configuracion.EDITAR_ITEM
						+ "transferencia, art. "
						+ this.nvoDetalle.getArticulo().getPos1());

			} else {
				this.dto.getDetalles().add(this.nvoDetalle);
				this.addEventoAgenda(Configuracion.NUEVO_ITEM
						+ "transferencia, art. "
						+ this.nvoDetalle.getArticulo().getPos1());
			}
		}
		this.nvoDetalle = null;
	}

	/**
	 * Finalizacion de reservas..
	 */
	public void finalizarReserva(TransferenciaDTO transf) throws Exception {
		ReservaDTO reserva = transf.getReserva();
		if (reserva == null) {
			return;
		}
		this.ctr.modificarReserva(reserva, ControlLogica.FINALIZAR_RESERVA);
	}

	/**
	 * Valida la disponibilidad de stock..
	 */
	private boolean verificarStock() {
		return true;
	}

	/**
	 * Transferencia de stock..
	 */
	private void transferirStock(boolean actualizarCosto) throws Exception {
		for (TransferenciaDetalleDTO item : this.dto.getDetalles()) {
			this.actualizarStock(item);
			if (actualizarCosto) {
				this.actualizarCosto(item);
			}			
		}
	}
	
	/**
	 * actualiza el stock..resta al deposito origen y suma al deposito destino..
	 */
	private void actualizarStock(TransferenciaDetalleDTO item) throws Exception {
		
		ControlArticuloStock.actualizarStock(item.getArticulo().getId(),
				this.dto.getDepositoSalida().getId(),
				(item.getCantidad() * -1), this.getLoginNombre());

		ControlArticuloStock.actualizarStock(item.getArticulo().getId(),
				this.dto.getDepositoEntrada().getId(), item.getCantidad(),
				this.getLoginNombre());

		ControlArticuloStock.addMovimientoStock(this.dto.getId(), this
				.getDtoUtil().getTmTransferenciaMercaderia().getId(), (item
				.getCantidad() * -1), item.getArticulo().getId(), this.dto
				.getDepositoSalida().getId(), this.getLoginNombre());

		ControlArticuloStock.addMovimientoStock(this.dto.getId(), this
				.getDtoUtil().getTmTransferenciaMercaderia().getId(), (item
				.getCantidad()), item.getArticulo().getId(), this.dto
				.getDepositoEntrada().getId(), this.getLoginNombre());
	}
	
	/**
	 * Actualiza los costos..
	 */
	private void actualizarCosto(TransferenciaDetalleDTO item) throws Exception {

		ControlArticuloCosto.actualizarCosto(item.getArticulo().getId(), item.getCosto(),
				this.getLoginNombre());

		ControlArticuloCosto.addMovimientoCosto(item.getArticulo().getId(), item.getCosto(),
				new Date(), this.dto.getId(), this.getDtoUtil()
						.getTmTransferenciaMercaderia().getId(),
				this.getLoginNombre());
	}
	
	/**
	 * actualiza cta cte..
	 */
	private void actualizarCtaCte() throws Exception {
		ControlCtaCteEmpresa ctacte = new ControlCtaCteEmpresa(null);
		MyPair emp = new MyPair();
		emp.setId(Configuracion.ID_EMPRESA_YHAGUY_CENTRAL);

		long idMovimientoOriginal = this.dto.getId();
		String nroComprobante = this.dto.getNumero();
		Date fechaEmision = this.dto.getFechaCreacion();
		double importeOriginal = this.dto.getTotalCosto();
		MyPair moneda = this.getDtoUtil().getMonedaGuarani();
		MyArray tipoMovimiento = this.getDtoUtil()
				.getTmTransferenciaMercaderia();
		
		MyPair tipoCaracterMovimiento = this.getDtoUtil()
				.getCtaCteEmpresaCaracterMovProveedor();
		
		if (this.dto.getSucursal().getId().longValue() == BuscadorArticulosViewModel.ID_SUC_PRINCIPAL) {
			tipoCaracterMovimiento = this.getDtoUtil().getCtaCteEmpresaCaracterMovCliente();
		}	
		
		MyArray sucursal = new MyArray();
		sucursal.setId(this.dto.getSucursal().getId());

		ctacte.addTransferencia(emp, idMovimientoOriginal, nroComprobante,
				fechaEmision, importeOriginal, moneda, tipoMovimiento,
				tipoCaracterMovimiento, sucursal);
	}

	/**
	 * Seleccion de impresion..
	 */
	private void imprimir() {
		if (this.isTransferenciaInterna()) {
			this.imprimirTransferencia();

			// si es transf ext permite seleccionar la impresion..
		} else {
			this.selectPrint = (Window) Executions.createComponents(ZUL_PRINT,
					this.mainComponent, null);
			this.selectPrint.doModal();
		}

	}

	/**
	 * Impresion de la transferencia..
	 */
	private void imprimirTransferencia() {
		List<Object[]> data = new ArrayList<Object[]>();

		for (TransferenciaDetalleDTO item : this.dto.getDetalles()) {

			Object[] obj1 = new Object[] { item.getArticulo().getPos1(),
					item.getArticulo().getPos2(), item.getCantidad() };

			Object[] obj2 = new Object[] { item.getArticulo().getPos1(),
					item.getArticulo().getPos2(), item.getCantidad(), item.getCosto(), item.getCostoTotal() };

			data.add(this.isTransferenciaInterna() ? obj1 : obj2);
		}

		ReporteYhaguy rep = null;

		if (this.isTransferenciaInterna()) {
			rep = new ReporteTransferenciaInterna(this.dto);
		} else {
			rep = new ReporteTransferenciaExterna(this.dto);
		}

		rep.setDatosReporte(data);
		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}

	/**
	 * Despliega informacion adicional..
	 */
	public void verInformacion() {
		String txt = "";
		txt += "Confirmación Pedido Transferencia\n";
		txt += "- Cerrar Pedido: \n";
		txt += "- Confirmar Pedido: \n";
		txt += "Confirmación Transferencia\n";
		txt += "- Enviada: \n";
		txt += "- Recibida: \n";
		txt += "- Ubicada: \n";
		this.mensajeInfo(txt);
	}

	/**
	 * inicializa los valores por defecto..
	 */
	public void inicializarValores(TransferenciaDTO dto) {
		this.selectedItems = null;

		MyArray funcionario = this.getAcceso().getFuncionario();
		MyPair elaboracion = this.getDtoUtil()
				.getEstadoTransferenciaElaboracion();
		MyPair transInterna = this.getDtoUtil().getTipoTransferenciaInterna();
		MyPair transExterna = this.getDtoUtil().getTipoTransferenciaExterna();
		MyPair sucursal = this.getAcceso().getSucursalOperativa();

		if (this.isTransferenciaInterna()) {
			dto.setTransferenciaTipo(transInterna);
		} else {
			dto.setTransferenciaTipo(transExterna);
		}

		dto.setTransferenciaEstado(elaboracion);
		dto.setFuncionarioCreador(funcionario);
		dto.setFechaCreacion(new Date());
		dto.setDepositoSalida(new MyPair());
		dto.setDepositoEntrada(new MyPair());
		dto.setSucursal(sucursal);
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	public void imprimirComprobante(String source,
			Map<String, Object> parametros, JRDataSource dataSource) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);

		this.win = (Window) Executions.createComponents(
				ReportesViewModel.ZUL_REPORTES, this.mainComponent, params);
		this.win.doModal();
	}

	/**
	 * @return true si se confirma la eliminacion de items..
	 */
	private boolean confirmarEliminarItems() {
		String mensaje = "Está seguro que quiere eliminar los items: \n";

		for (TransferenciaDetalleDTO item : this.selectedItems) {
			mensaje += "\n" + item.getArticulo().getPos1() + " - cantidad: "
					+ item.getCantidad();
		}
		return this.mensajeSiNo(mensaje);
	}

	/**
	 * setea el numero..
	 */
	private void setNumero(TransferenciaDTO dto) throws Exception {
		String key = this.getNroKey();
		String nro = AutoNumeroControl.getAutoNumeroKey(key, 7);
		dto.setNumero(nro);
	}

	/**************************************************/
	

	/******************* VALIDACIONES *****************/

	/**
	 * @return true si los datos estan correctos..
	 */
	private boolean validarFormulario() {
		boolean out = true;
		this.mensajeValidacion = "No se puede completar la operación debido a: \n";

		try {

			if (this.dto.getDetalles().size() == 0) {
				out = false;
				this.mensajeValidacion += "\n - Debe ingresar al menos un ítem..";
			}

			if ((out == true) && (this.dto.esNuevo())) {
				String key = this.getNroKeyPendiente();
				String nro = AutoNumeroControl.getAutoNumero(key, 5);
				this.dto.setNumero("PNDTE-" + nro);
				this.addEventoAgenda("Se creó la transferencia "
						+ this.dto.getNumero());

			}
			if (out == true) {
				
				this.grabarEventosAgenda();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return out;
	}

	/**
	 * Validador insertar item..
	 */
	class ValidadorInsertarItem implements VerificaAceptarCancelar {

		private String mensaje;
		private boolean edit;

		public ValidadorInsertarItem(boolean edit) {
			this.edit = edit;
		}

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede realizar la operación debido a: \n";

			MyArray articulo = nvoDetalle.getArticulo();
			int cantidad = nvoDetalle.getCantidad();

			if (articulo.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe seleccionar un artículo..";
			}

			if (cantidad <= 0) {
				out = false;
				this.mensaje += "\n - La cantidad debe ser mayor a cero..";
			}

			if ((this.edit == false) && (this.isItemDuplicado(articulo))) {
				out = false;
				this.mensaje += "\n - El artículo ya fue ingresado..";
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

		/**
		 * @return true si el item ya fue cargado..
		 */
		private boolean isItemDuplicado(MyArray articulo) {
			long id = articulo.getId().longValue();

			for (TransferenciaDetalleDTO item : dto.getDetalles()) {
				if (item.getArticulo().getId().longValue() == id)
					return true;
			}
			return false;
		}

	}

	/********************* GET/SET *********************/

	public ControlAlertas getCtrAlertas() {
		return ctrAlertas;
	}

	public List<MyPair> getSucursales() {
		List<MyPair> out = new ArrayList<MyPair>();		
		for (MyPair sucursal : this.sucursales) {
				out.add(sucursal);
		}
		out.remove(this.getAcceso().getSucursalOperativa());
		return out;
	}

	public void setSucursales(List<MyPair> sucursales) {
		this.sucursales = sucursales;
	}

	/**
	 * @return los depositos segun la sucursal..
	 */
	public List<MyPair> getDepositosBySucursal(Long id) throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Deposito> deps = rr.getDepositosPorSucursal(id);
		for (Deposito deposito : deps) {
			MyPair dep = new MyPair(deposito.getId(), deposito.getDescripcion());
			out.add(dep);
		}
		return out;
	}

	/**
	 * @return los depositos de la sucursal operativa..
	 */
	public List<MyPair> getDepositos() throws Exception {
		MyPair suc = this.getAcceso().getSucursalOperativa();
		return this.getDepositosBySucursal(suc.getId());
	}

	@DependsOn("dto.depositoSalida")
	public List<MyPair> getDepositosDestino() throws Exception {
		List<MyPair> out = this.getDepositos();
		out.remove(this.dto.getDepositoSalida());
		return out;
	}

	@DependsOn("dto.sucursalDestino")
	public List<MyPair> getDepositosDestinoExt() throws Exception {
		Long idSuc = this.dto.getSucursalDestino().getId();
		return this.getDepositosBySucursal(idSuc);
	}

	@DependsOn({ "dto.depositoEntrada", "dto.depositoSalida" })
	public boolean isDetalleVisible() {
		return ((this.dto.getDepositoEntrada().esNuevo() == false) && (this.dto
				.getDepositoSalida().esNuevo() == false));
	}

	@DependsOn({ "dto.depositoSalida", "dto.sucursalDestino", "dto.depositoEntrada" })
	public boolean isDetalleVisibleExt() {
		return ((this.dto.getDepositoSalida().esNuevo() == false)
				&& (this.dto.getSucursalDestino().esNuevo() == false) && (this.dto
				.getDepositoEntrada().esNuevo() == false));
	}

	@DependsOn("detalleVisible")
	public boolean isSolicitarCabeceraVisible() {
		return this.isDetalleVisible() == false;
	}

	@DependsOn("detalleVisibleExt")
	public boolean isSolicitarCabeceraVisibleExt() {
		return this.isDetalleVisibleExt() == false;
	}

	/**
	 * @return true si es transferencia interna..
	 */
	public boolean isTransferenciaInterna() {
		return this.getAliasFormularioCorriente().equals(
				ID.F_TRANSFERENCIA_INTERNA_ABM);
	}

	@DependsOn({ "deshabilitado", "selectedItems" })
	public boolean isEliminarItemDisabled() {
		return (this.isDeshabilitado() == true) || (this.selectedItems == null)
				|| (this.selectedItems.size() == 0);
	}

	@DependsOn({ "deshabilitado", "selectedItems", "dto.transferenciaEstado" })
	public boolean isEliminarItemExtDisabled() {
		long elaboracion = this.getDtoUtil()
				.getEstadoTransferenciaElaboracion().getId().longValue();
		long actual = this.dto.getTransferenciaEstado().getId().longValue();
		if (actual != elaboracion)
			return true;
		return (this.isDeshabilitado() == true) || (this.selectedItems == null)
				|| (this.selectedItems.size() == 0);
	}

	@DependsOn({ "deshabilitado", "dto.transferenciaEstado" })
	public boolean isInsertarDisabled() {
		long elaboracion = this.getDtoUtil()
				.getEstadoTransferenciaElaboracion().getId().longValue();
		long actual = this.dto.getTransferenciaEstado().getId().longValue();
		if (this.isTransferenciaInterna())
			return this.isDeshabilitado();
		return (this.isDeshabilitado() == true) || (actual != elaboracion);
	}

	@DependsOn({ "deshabilitado", "dto" })
	public boolean isConfirmarDisabled() {
		return (this.isDeshabilitado() == true) || (this.dto.esNuevo() == true);
	}

	@DependsOn({ "deshabilitado", "dto", "dto.transferenciaEstado" })
	public boolean isConfirmarExtDisabled() {
		long elaboracion = this.getDtoUtil()
				.getEstadoTransferenciaElaboracion().getId().longValue();
		long actual = this.dto.getTransferenciaEstado().getId().longValue();
		return (this.isDeshabilitado() == true) || this.dto.esNuevo()
				|| (actual != elaboracion);
	}

	@DependsOn({ "deshabilitado", "dto.transferenciaEstado" })
	public boolean isRecepcionarDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.isElaboracion() == true) || !this.dto.isEnTransito();
	}

	@DependsOn({ "deshabilitado", "dto" })
	public boolean isCheckmarkVisible() {
		return (this.isDeshabilitado() == false)
				&& (this.dto.getDetalles().size() > 0);
	}

	@DependsOn("dto.transferenciaEstado")
	public boolean isTransferido() {
		long confirmado = this.getIdEstadoConfirmado();
		long actual = this.getIdEstadoActual();
		return actual == confirmado;
	}

	@DependsOn("dto.transferenciaEstado")
	public boolean isElaboracion() {
		long elaboracion = this.getIdEstadoElaboracion();
		long actual = this.getIdEstadoActual();
		return actual == elaboracion;
	}

	private long getIdEstadoActual() {
		return this.dto.getTransferenciaEstado().getId().longValue();
	}

	private long getIdEstadoElaboracion() {
		return this.getDtoUtil().getEstadoTransferenciaElaboracion().getId()
				.longValue();
	}

	private long getIdEstadoConfirmado() {
		return this.getDtoUtil().getEstadoTransferenciaConfirmada().getId()
				.longValue();
	}

	private String getNroKey() {
		return this.isTransferenciaInterna() ? NRO_TRFI_KEY : NRO_TRFE_KEY;
	}

	private String getNroKeyPendiente() {
		return (this.isTransferenciaInterna() ? NRO_TRFI_KEY : NRO_TRFE_KEY)
				+ "-PEND";
	}

	public boolean isSolicitarCabecera() {
		return solicitarCabecera;
	}

	public void setSolicitarCabecera(boolean solicitarCabecera) {
		this.solicitarCabecera = solicitarCabecera;
	}

	public TransferenciaDTO getDto() {
		return dto;
	}

	public void setDto(TransferenciaDTO dto) {
		this.dto = dto;
	}

	public TransferenciaDetalleDTO getNvoDetalle() {
		return nvoDetalle;
	}

	public void setNvoDetalle(TransferenciaDetalleDTO nvoDetalle) {
		this.nvoDetalle = nvoDetalle;
	}

	public List<TransferenciaDetalleDTO> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<TransferenciaDetalleDTO> selectedItems) {
		this.selectedItems = selectedItems;
	}

	/**
	 * validador de recepcion..
	 */
	class ValidadorRecepcion implements VerificaAceptarCancelar {

		private String mensaje;

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";

			/*if (selectedDestinoExt.esNuevo()) {
				out = false;
				this.mensaje += "\n - Debe indicar el destino..";
			}*/
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
			return "error al cancelar..";
		}
	}
	
	/**
	 * DataSource de la remision..
	 */
	class RemisionDataSource implements JRDataSource {

		Map<String, Double> totales = new HashMap<String, Double>();
		Misc misc = new Misc();
		
		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			List<BeanTransferenciaDetalle> dets = new ArrayList<BeanTransferenciaDetalle>();

			for (TransferenciaDetalleDTO det : dto.getDetalles()) {
				dets.add(new BeanTransferenciaDetalle(String.valueOf(det
						.getCantidad()), (String) det.getArticulo().getPos1()
						+ " - " + (String) det.getArticulo().getPos2()));
			}

			if ("Facturas".equals(fieldName)) {
				value = dets;
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < 0) {
				index++;
				return true;
			}
			return false;
		}
	}
}

/**
 * Reporte de Transferencia..
 */
class ReporteTransferenciaInterna extends ReporteYhaguy {

	private TransferenciaDTO transferencia;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_INTEGER, 30,
			false);

	public ReporteTransferenciaInterna(TransferenciaDTO transf) {
		this.transferencia = transf;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo(this.transferencia.getTransferenciaTipo().getText());
		this.setDirectorio("transferencias");
		this.setNombreArchivo("Transferencia");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.transferencia.getNumero();
		String origen = this.transferencia.getDepositoSalida().getText();
		String destino = this.transferencia.getDepositoEntrada().getText();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", numero)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Origen", origen)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Destino", destino)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}

/**
 * Reporte de Transferencia..
 */
class ReporteTransferenciaExterna extends ReporteYhaguy {

	private TransferenciaDTO transferencia;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Cantidad", TIPO_INTEGER, 30,
			false);
	static DatosColumnas col4 = new DatosColumnas("Costo", TIPO_DOUBLE, 30,
			false);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_DOUBLE, 30,
			true);

	public ReporteTransferenciaExterna(TransferenciaDTO transf) {
		this.transferencia = transf;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo(this.transferencia.getTransferenciaTipo().getText());
		this.setDirectorio("transferencias");
		this.setNombreArchivo("Transferencia");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		String numero = this.transferencia.getNumero();
		String origen = this.transferencia.getSucursal().getText() + " - "
				+ this.transferencia.getDepositoSalida().getText();
		String destino = this.transferencia.getSucursalDestino().getText();

		VerticalListBuilder out = cmp.verticalList();

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Número", numero)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Origen", origen)));

		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Destino", destino)));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
