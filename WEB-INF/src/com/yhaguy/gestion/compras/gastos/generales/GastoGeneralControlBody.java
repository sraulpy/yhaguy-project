package com.yhaguy.gestion.compras.gastos.generales;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.Config;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.extras.browser.Browser;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.AutonumeroYhaguy;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.compras.gastos.generales.pedidos.OrdenPedidoGastoDTO;
import com.yhaguy.gestion.compras.gastos.generales.pedidos.OrdenPedidoGastoDetalleDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoDetalleDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoSubDiarioControlBody;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;
import com.yhaguy.gestion.contabilidad.subdiario.SubDiarioDetalleDTO;
import com.yhaguy.gestion.empresa.ctacte.ControlCtaCteEmpresa;

public class GastoGeneralControlBody extends GastoSubDiarioControlBody {

	private OrdenPedidoGastoDTO dto = new OrdenPedidoGastoDTO();
	private GastoDTO dtoGasto = new GastoDTO();
	
	static final String PREFIJO_IVA = "IVA - ";	
	
	@Init(superclass=true)
	public void init() {
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose() {		
	}

	@Override
	public Assembler getAss() {
		return new AssemblerGastoGeneral();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (OrdenPedidoGastoDTO) dto;
		
		if (!this.dto.getSubDiario().isConfirmado()) {
			this.poblarDetallesTemporales();
		} else {
			this.dto.getSubDiario().setDetallesTemporal(this.dto.getSubDiario().getDetalles());			
		}
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		return new OrdenPedidoGastoDTO();
	}

	@Override
	public String getEntidadPrincipal() {
		return OrdenPedidoGasto.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return getAllDTOs(this.getEntidadPrincipal());
	}
	
	public Browser getBrowser(){
		return new GastoGeneralBrowser();
	}
	
	
	/**************************** AGENDA *******************************/
	
	/**
	 * Se invoca al control de la Agenda 'ctrAgenda' y se muestra mediante
	 * mostrarAgenda(). Eventos que se generan automaticamente: - Cuando se
	 * agrega un Gasto - Cuando se confirma el Sub-Diario
	 */
	
	@Override
	public int getCtrAgendaTipo(){
		return ControlAgendaEvento.COTIZACION;
	}
	
	@Override
	public String getCtrAgendaKey(){
		return this.dto.getNumero();
	}
	
	@Override
	public String getCtrAgendaTitulo(){
		return "[Pedido: " + this.getCtrAgendaKey() + "]";
	}
	
	@Override
	public boolean getAgendaDeshabilitado(){
		return this.dto.esNuevo();
	}
	
	
	/**
	 * En esta lista se va guardando los eventos de la agenda para que se
	 * guarden una vez que se dio click en Guardar Object[0] : tipoAgenda,
	 * Object[1] : claveAgenda, Object[2] tipoDetalle, Object[3] texto,
	 * Object[4] : link
	 * */
	private List<Object[]> eventosAgenda = new ArrayList<Object[]>();
	
	public List<Object[]> getEventosAgenda() {
		return eventosAgenda;
	}

	public void setEventosAgenda(List<Object[]> eventosAgenda) {
		this.eventosAgenda = eventosAgenda;
	}
	
	//Metodo para crear eventos en la agenda y poder cargarlos a la lista eventosAgenda
	public void addEventoAgenda(int tipoAgenda, String claveAgenda, int tipoDetalle, String texto, String link){
		Object[] evento = new Object[5];
		evento[0] = tipoAgenda;
		evento[1] = claveAgenda;
		evento[2] = tipoDetalle;
		evento[3] = texto;
		evento[4] = link;
		this.eventosAgenda.add(evento);
	}

	/*******************************************************************/
	
	
	/******************** TOTAL GRID SUB-DIARIO ************************/
	
	/**
	 * @return el total de importe de la lista de SubDiarios Detalle..
	 */
	
	public double getTotalSubDiarioDetalle(){
		double out = 0;
		for (SubDiarioDetalleDTO d: this.dto.getSubDiario().getDetallesTemporal()) {
			out = out + d.getImporte();
		}
		return out;
	}
	
	/*******************************************************************/	
	
	
	/*************** POPUP ACEPTAR-CANCELAR DE GASTOS ******************/
	
	@Command @NotifyChange("*")
	public void abrirVentanaGastos() throws Exception {
		
		if (this.dto.getSubDiario().isConfirmado()) {
			mensajeError(Configuracion.TEXTO_ERROR_SUB_DIARIO_CONFIRMADO);
			return;
		}		
		this.abrirVentanaGastos(new GastoDTO(), WindowPopup.NUEVO);		
	}
	
	/**
	 * Abre el formulario de Factura de Gasto..
	 */
	public void abrirVentanaGastos(GastoDTO gasto, String modo) throws Exception{		
		
		MyArray monedaGuarani = this.getDtoUtil().getMonedaGuaraniConSimbolo();		
		double tipoCambio = this.getDtoUtil().getCambioVentaBCP(monedaGuarani);
		
		this.dtoGasto = gasto;
		
		if (gasto.esNuevo() == true) {
			this.dtoGasto.setTipoCambio(tipoCambio);
			this.dtoGasto.setMoneda(this.utilDto.getMonedaGuaraniConSimbolo());
			this.dtoGasto.setCondicionPago(this.dto.getCondicionPago());
			this.dtoGasto.setVencimiento(this.calcularVencimiento());
			this.dtoGasto.setTipoMovimiento(this.getTipoMovimiento(dtoGasto.getCondicionPago()));		
			this.dtoGasto.setProveedor(this.dto.getProveedor());
			this.dtoGasto.setEstadoComprobante(this.utilDto.getEstadoComprobanteConfeccionado());
		}		
		
		List<SubDiarioDetalleDTO> detTemporal = this.dto.getSubDiario().getDetallesTemporal();
			
		WindowPopup w = new WindowPopup();			
		w.setModo(modo);
		w.setDato(this.dtoGasto);
		w.setWidth("1030px");
		w.setHigth("670px");
		w.setTitulo(Configuracion.TITULO_FORMULARIO_GASTOS);
		w.setCheckAC(new ValidadorFacturaGasto(this.dtoGasto, monedaGuarani, detTemporal));
		w.show(Configuracion.GASTOS_FACTURA_ZUL);
		
		if (w.isClickAceptar() == true) {
			this.agregarGasto();
			this.addEventoAgenda(ControlAgendaEvento.NORMAL, this.dto.getNumero(),
					0, Configuracion.TEXTO_GASTO_AGREGADO + this.dtoGasto.getNumeroFactura(), "");
		}		
	}	
	
	/**
	 * Visualizar la factura (solo lectura) haciendo doble click en el item..
	 */
	@Command @NotifyChange("*")
	public void mostrarVentanaGastos(
			@BindingParam("detalle") SubDiarioDetalleDTO detalle)
			throws Exception {

		if (detalle.isEditable() == true) {

			String descripcion = detalle.getDescripcion();

			// busca entre los gastos aquel que coincida con el nro y timbrado
			for (GastoDTO gasto : this.dto.getGastos()) {

				String desc = Configuracion.CAMPO_NRO_FACTURA
						+ gasto.getNumeroFactura() + " - ("
						+ gasto.getTimbrado().getPos1() + ")";

				if (descripcion.trim().toLowerCase()
						.compareTo(desc.trim().toLowerCase()) == 0) {
					this.dtoGasto = gasto;
				}
			}

			this.abrirVentanaGastos(this.dtoGasto, WindowPopup.SOLO_LECTURA);
		}
	}
	
	public void actualizarSubDiariosDetalle() throws Exception{
		List<SubDiarioDetalleDTO> l = new ArrayList<SubDiarioDetalleDTO>();
		
		for (SubDiarioDetalleDTO d : l) {
			this.dto.getSubDiario().getDetalles().remove(d);
			this.dto.getSubDiario().getDetallesTemporal().remove(d);
		}
		this.agregarSubDiariosDetalle();
	}	
	
	/*******************************************************************/	
	
	
	/******************** POBLAR DETALLES TEMPORALES *******************/
	
	private double totalGastos = 0;
	
	public void poblarDetallesTemporales() {
		
		this.totalGastos = 0;
		
		for (SubDiarioDetalleDTO d: this.dto.getSubDiario().getDetalles()) {
			this.dto.getSubDiario().getDetallesTemporal().add(d);
		}
		
		for (OrdenPedidoGastoDetalleDTO d : this.dto.getOrdenPedidoGastoDetalle()) {
			
			double importe = d.getImporte();
			this.totalGastos += importe;
			
			SubDiarioDetalleDTO s = crearSubDiarioDetalle(  
					Configuracion.CUENTA_HABER_KEY,
					Configuracion.TEXTO_GASTOS_DEPARTAMENTO +
					(String)d.getDepartamento().getPos1(),
					Configuracion.ALIAS_CUENTA_PROVEEDORES_VARIOS, importe,	false);			
			
			this.dto.getSubDiario().getDetallesTemporal().add(s);
		}
	}
	
	public SubDiarioDetalleDTO crearSubDiarioDetalle(int tipo,
			String descripcion, String alias, double importe, boolean editable) {
		RegisterDomain rr = RegisterDomain.getInstance();
		SubDiarioDetalleDTO out = new SubDiarioDetalleDTO();
		try {
			out.setTipo(tipo);
			out.setDescripcion(descripcion.toUpperCase());
			out.setCuenta(rr.getCuentaContableByAlias(alias));
			out.setImporte(importe * tipo);
			out.setEditable(editable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	
	public SubDiarioDetalleDTO crearSubDiarioDetalle(int tipo,
			String descripcion, MyArray ctaCtable, double importe) {
		SubDiarioDetalleDTO out = new SubDiarioDetalleDTO();
		out.setTipo(tipo);
		out.setDescripcion(descripcion.toUpperCase());
		out.setCuenta(ctaCtable);
		out.setImporte(importe * tipo);
		out.setEditable(false);
		return out;
	}
	
	/*******************************************************************/
	
	
	/************************** AGREGAR GASTOS *************************/
	
	/**
	 * Si los totales coinciden el gasto se desglosa en distintas cuentas ('Importacion en Curso' o
	 * 'Iva a Pagar') de acuerdo a los datos que tiene mediante la funcion agregarSubDiariosDetalle() 
	 * y los agrega al Sub-Diario mediante la funcion agregarGasto().. 
	 * @throws Exception 
	 */

	public void agregarGasto() throws Exception {		
		this.dto.getGastos().add(this.dtoGasto);
		agregarSubDiariosDetalle();		
	}

	public void agregarSubDiariosDetalle() throws Exception{
		
		for (GastoDetalleDTO det : this.dtoGasto.getDetalles()) {
			SubDiarioDetalleDTO sd = new SubDiarioDetalleDTO();
			sd.setTipo(Configuracion.CUENTA_DEBE_KEY);
			sd.setDescripcion(Configuracion.CAMPO_NRO_FACTURA 
					+ this.dtoGasto.getNumeroFactura() + " - ("
					+ this.dtoGasto.getTimbrado().getPos1() + ")");
			sd.setCuenta(det.getArticuloGasto().getCuentaContable());
			sd.setImporte(det.getImporteGs() - det.getIvaCalculado());
			//sd.setGasto(this.dtoGasto);
			sd.setEditable(true);
			this.dto.getSubDiario().getDetalles().add(sd);
			this.dto.getSubDiario().getDetallesTemporal().add(sd);
			
			//Desglose del iva..
			if (det.getIvaCalculado() > 0) {

				MyArray ctaIva = null;
				
				if (det.getTipoIva().getId().longValue() == getDtoUtil()
						.getTipoIva10().getId()) {
					ctaIva = this.getDtoUtil().getCuentaIvaCF10();
					
				} else {
					ctaIva = this.getDtoUtil().getCuentaIvaCF5();
				}

				SubDiarioDetalleDTO iva = new SubDiarioDetalleDTO();
				iva.setTipo(Configuracion.CUENTA_DEBE_KEY);
				iva.setDescripcion(PREFIJO_IVA
						+ Configuracion.CAMPO_NRO_FACTURA
						+ this.dtoGasto.getNumeroFactura() + " - ("
						+ this.dtoGasto.getTimbrado().getPos1() + ")");
				iva.setCuenta(ctaIva);
				iva.setImporte(det.getIvaCalculado());
				// iva.setGasto(this.dtoGasto);
				iva.setEditable(false);

				this.dto.getSubDiario().getDetalles().add(iva);
				this.dto.getSubDiario().getDetallesTemporal().add(iva);
			}
		}
		
	}
	
	/*******************************************************************/	
	
	
	/********************** CONFIRMAR SUB-DIARIO ***********************/
	
	/**
	 * Si el total del Sub-Diario esta balanceado (Saldo=0) y si todos los gastos
	 * cuentan con su comprobante fisico confirma el Sub-Diario, en caso contrario
	 * despliega un mensaje de error y retorna sin hacer nada..
	 */
	
	@Command @NotifyChange("*")
	public void confirmarSubDiario() {
		
		if (this.dto.getSubDiario().isConfirmado()) {
			mensajeError(Configuracion.TEXTO_ERROR_SUB_DIARIO_CONFIRMADO);
			return;
		}
		if (this.getTotalSubDiarioDetalle() > 0.001 || this.getTotalSubDiarioDetalle() < -0.001) {
			mensajeError(Configuracion.TEXTO_ERROR_SUB_DIARIO);
			return;
		}
		for (GastoDTO d : this.dto.getGastos()) {
			
			if (!d.isExisteComprobanteFisico()) {
				mensajeError(Configuracion.TEXTO_ERROR_GASTOS_SIN_COMPROBANTE
								+"\n Gasto Nro. " + d.getNumeroFactura());
				return;
			}
		}
		try {
			if (this.validarFormulario() == true) {
				
				if (this.mensajeSiNo("Desea confirmar el Sub-Diario?") == true) {
					this.confirmSubDiario();
				}
			}			
		} catch (Exception e) {
			mensajeError(e.getMessage());
		}		
	}
	
	/**
	 * Confirma el sub-diario
	 */
	private void confirmSubDiario() throws Exception {
						
		String nro = AutonumeroYhaguy.getNumeroSubDiario(this.sucursalOperativa);

		this.addEventoAgenda(ControlAgendaEvento.NORMAL,
				this.dto.getNumero(), 0,
				Configuracion.TEXTO_SUB_DIARIO_CONFIRMADO, "");
		
		for (Object[] o : this.eventosAgenda) {
			this.getCtrAgenda().addDetalle((int) o[0], (String) o[1],
					(int) o[2], (String) o[3], (String) o[4]);
		}
		
		this.dto.getSubDiario().setDetalles(
				this.dto.getSubDiario().getDetallesTemporal());
		this.dto.getSubDiario().setConfirmado(true);
		this.dto.getSubDiario().setNumero(nro);
		this.dto.setReadonly();

		this.saveDTO(dto);
		this.actualizarCtaCteProveedor();
		this.setEstadoABMConsulta();
		
		this.mensajePopupTemporal(Configuracion.TEXTO_SUB_DIARIO_CONFIRMADO);
	}
	
	
	/**
	 * Actualiza la Cta Cte del Proveedor..
	 */
	public void actualizarCtaCteProveedor() throws Exception {	
		
		ControlCtaCteEmpresa ctr = new ControlCtaCteEmpresa(null);
		
		for (GastoDTO gasto : this.dto.getGastos()) {
			
			String sigla = (String) gasto.getTipoMovimiento().getPos2();
			long idEmpresa = (long) this.dto.getProveedor().getPos6();
			MyPair empresa = new MyPair(idEmpresa);
			long idMoneda = gasto.getMoneda().getId();
			MyPair moneda = new MyPair(idMoneda);
			moneda.setSigla((String) gasto.getMoneda().getPos2());
			MyPair caracter = getDtoUtil().getCtaCteEmpresaCaracterMovProveedor();
			MyArray sucursal = new MyArray();
			sucursal.setId(this.dto.getSucursal().getId());
			
			double importe = gasto.getImporteTotal(moneda.getSigla());
			
			//verifica que sea movimiento credito o contado
			if (sigla.compareTo(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO) == 0) {			
				ctr.addCtaCteEmpresaMovimientoFacturaCredito(empresa,
						gasto.getId() , gasto.getNumeroFactura(), gasto.getFecha(),
						0, 1, importe, 0, importe, moneda, gasto.getTipoMovimiento(),
						caracter, sucursal, gasto.getNumeroImportacion(),
						gasto.getTipoCambio());
				
			} else if (sigla
					.compareTo(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO) == 0) {

				ctr.addCtaCteEmpresaMovimientoFacturaContado(empresa,
						gasto.getId(), gasto.getNumeroFactura(), gasto.getFecha(),
						importe, moneda, gasto.getTipoMovimiento(),
						caracter, sucursal, true, gasto.getNumeroImportacion());
			}			
		}
	}
	
	/*******************************************************************/
	
	
	/**************************** UTILES *******************************/
	
	private UtilDTO utilDto = (UtilDTO) this.getDtoUtil();
	private MyArray facturaGastoContado = utilDto.getTmFacturaGastoContado();
	private MyArray facturaGastoCredito = utilDto.getTmFacturaGastoCredito();
	private MyArray condicionPagoContado = utilDto.getCondicionPagoContado();
	private MyPair sucursalOperativa = this.getAcceso().getSucursalOperativa();
	
	private boolean noDeshabilitado = false;

	public boolean isNoDeshabilitado() {
		return noDeshabilitado;
	}

	public void setNoDeshabilitado(boolean noDeshabilitado) {
		this.noDeshabilitado = noDeshabilitado;
	}	
	
	public double getTotalGastos(){
		if (this.dto.getSubDiario().isConfirmado() == true) {
			this.totalGastos = 0;
		}
		return this.totalGastos;
	}
	
	public double getTotalOrdenPedidoGasto(){
		double out = 0;
		for (OrdenPedidoGastoDetalleDTO d: this.dto.getOrdenPedidoGastoDetalle()) {
			out = out + d.getImporte();
		}
		return out;
	}
	
	//retorna el tipo de movimiento segun la condicion de pago
	private MyArray getTipoMovimiento(MyArray condicion){
		if (condicion.compareTo(condicionPagoContado) == 0) {
			return facturaGastoContado;
		} else {
			return facturaGastoCredito;
		}
	}
	
	//retorna la fecha de vencimiento segun el plazo y la emision
	private Date calcularVencimiento(){
		Date emision = this.dtoGasto.getFecha();
		int plazo = (int) this.dtoGasto.getCondicionPago().getPos2();
		return m.calcularFechaVencimiento(emision, plazo);
	}
	
	/*******************************************************************/
	
	
	/******************** ELIMINAR ITEM DEL DETALLE ********************/
	
	private SubDiarioDetalleDTO selectedItem;
	
	/**
	 * Cambia el color del row correspondiente al iva desglosado del item
	 * seleccionado..
	 */
	@Command @NotifyChange("*")
	public void notifyList() {
		List<SubDiarioDetalleDTO> list = this.getIvaDesglosado();

		for (SubDiarioDetalleDTO item : this.dto.getSubDiario()
				.getDetallesTemporal()) {
			item.setChecked(false);
		}

		if (list != null) {
			for (SubDiarioDetalleDTO item : list) {
				item.setChecked(true);
			}
		}
	}
	
	/**
	 * Borra el item seleccionado + su iva desglosado..
	 */		
	@Command @NotifyChange("*")
	public void deleteItem() {

		if (this.dto.getSubDiario().isConfirmado() == true) {
			this.mensajeError("El sub-diario ya fue confirmado..");
			return;

		} else if (this.selectedItem == null) {
			this.mensajeError("No se seleccionó ningún ítem..");
			return;

		} else if (this.mensajeSiNo("Está seguro de eliminar el ítem: \n - "
				+ this.selectedItem.getCuenta().getPos2()) == false) {
			return;
		}

		SubDiarioDetalleDTO delete = null;

		for (SubDiarioDetalleDTO item : this.dto.getSubDiario()
				.getDetallesTemporal()) {
			if (item.compareTo(this.selectedItem) == 0) {
				delete = item;
			}
		}

		if (delete != null) {
			this.dto.getSubDiario().getDetallesTemporal().remove(delete);
			this.dto.getSubDiario().getDetalles().remove(delete);
		}

		this.deleteIvaDesglosado();
		this.selectedItem = null;
	}
	
	/**
	 * elimina el iva desglosado del selectedItem..
	 */
	private void deleteIvaDesglosado() {
		List<SubDiarioDetalleDTO> desglose = this.getIvaDesglosado();
		
		if (desglose != null) {
			for (SubDiarioDetalleDTO iva : desglose) {
				this.dto.getSubDiario().getDetallesTemporal().remove(iva);
				this.dto.getSubDiario().getDetalles().remove(iva);
			}
		}
	}
	
	/**
	 * Devuelve el item correspondiente al iva desglosado del selectedItem..
	 */
	private List<SubDiarioDetalleDTO> getIvaDesglosado() {
		List<SubDiarioDetalleDTO> out = new ArrayList<SubDiarioDetalleDTO>();
		
		String desc = PREFIJO_IVA + this.selectedItem.getDescripcion();
				
		for (SubDiarioDetalleDTO item : this.dto.getSubDiario().getDetallesTemporal()) {
			if (desc.compareTo(item.getDescripcion()) == 0) {
				out.add(item);
			}
		}
		
		return out.size() > 0? out : null;
	}
	
	/*******************************************************************/
	
	
	/********************* REPORTE GASTO GENERAL ***********************/
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return this.dto.esNuevo();
	}
	
	@Override
	public void showImprimir() {
		this.reporteGastoGeneral();
	}
	
	@Command
	public void reporteGastoGeneral() {
		
		List<Object[]> data = new ArrayList<>();
		Object[] obj = new Object[5];		
		for (SubDiarioDetalleDTO d : this.dto.getSubDiario().getDetallesTemporal()) {
			obj = new Object[5];
			obj[0] = d.getTipoCuenta();
			obj[1] = d.getCuenta().getPos1();
			obj[2] = d.getCuenta().getPos2();
			obj[3] = d.getDescripcion();
			obj[4] = d.getImporte();
			data.add(obj);	
		}
						
		
		ReporteGastoGeneral r = new ReporteGastoGeneral();
		r.setProveedorRuc((String) this.dto.getProveedor().getPos3());
		r.setProveedorRazonSocial((String) this.dto.getProveedor().getPos2());
		r.setNroPedido(this.dto.getNumero());
		r.setSucursal(this.dto.getSucursal().getText());
		r.setFechaPedido(this.dto.getFechaCarga());
		r.setApaisada();
		if (this.dto.getSubDiario() != null) {
			r.setSubDiario(this.dto.getSubDiario().getNumero());
			r.setSubDiarioDescripcion(this.dto.getSubDiario().getDescripcion());
			r.setSubDiarioFecha(this.dto.getSubDiario().getFecha());
		}
		r.setDatosReporte(data);
		
		ViewPdf vp = new ViewPdf();
		vp.showReporte(r, this);
	}
	
	/*******************************************************************/
	
	
	/************************* VALIDACIONES ****************************/	
	
	private String mensajeVerificarGrabar = "";
	
	// Valida los datos del Formulario..
	public boolean validarFormulario() {
		boolean out = true;
		this.mensajeVerificarGrabar = "No se puede completar la operación debido a:";

		if (this.dto.getSubDiario().getDescripcion().trim().length() == 0) {
			this.mensajeVerificarGrabar += Configuracion.TEXTO_ERROR_CAMPOS_REQUERIDOS;
			out = false;
		}

		if (out == true) {
			for (GastoDTO gasto : this.dto.getGastos()) {
				if (gasto.getTimbrado().esNuevo()) {
					WindowTimbrado w = new WindowTimbrado();
					w.agregarTimbrado(gasto.getTimbrado(), gasto.getProveedor()
							.getId());
				}
			}
		}

		return out;
	}
		
	/**
	 * Se ejecuta antes de Grabar el Formulario, para validar previamente..
	 * */
	public boolean verificarAlGrabar() {
		try {
			if (this.dto.getSubDiario().esNuevo()
					&& this.validarFormulario() == true) {
				this.dto.getSubDiario().setNumero("EN PROCESO..");
				this.dto.getSubDiario().setSucursal(this.sucursalOperativa);
			}
			if (this.validarFormulario() == true) {
				for (Object[] o : this.eventosAgenda) {
					this.getCtrAgenda().addDetalle((int) o[0], (String) o[1],
							(int) o[2], (String) o[3], (String) o[4]);
				}
			}
		} catch (Exception e) {
			mensajeError(e.getMessage());
		}

		return this.validarFormulario();
	}

	public String textoErrorVerificarGrabar() {
		return this.mensajeVerificarGrabar;
	}
	
	/*******************************************************************/
	
	
	/**************************** GET/SET ******************************/
	
	public OrdenPedidoGastoDTO getDto() {
		return dto;
	}

	public void setDto(OrdenPedidoGastoDTO dto) {
		this.dto = dto;
	}

	public GastoDTO getDtoGasto() {
		return dtoGasto;
	}

	public void setDtoGasto(GastoDTO dtoGasto) {
		this.dtoGasto = dtoGasto;
	}

	public SubDiarioDetalleDTO getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(SubDiarioDetalleDTO selectedItem) {
		this.selectedItem = selectedItem;
	}

	@Override
	public boolean isDisabled() {
		return (this.isDeshabilitado() == true)
				|| (this.dto.getSubDiario().isConfirmado() == true);
	}

	@Override
	public boolean isConfirmarDisabled() {
		boolean operacionEnabled = false;
		try {
			operacionEnabled = this
					.operacionHabilitada(ID.O_CONFIRMAR_SUB_DIARIO) == true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (this.isDisabled() == true) || (operacionEnabled == false)
				|| (this.dto.getSubDiario().esNuevo() == true);
	}

	@Override @DependsOn("selectedItem")
	public boolean isEliminarItemDisabled() {
		return (this.isDisabled()) || (this.selectedItem == null);
	}
}

// Validador de la carga de factura de gasto..
class ValidadorFacturaGasto implements VerificaAceptarCancelar {
	
	private String mensajeVerificaAceptar = Configuracion.TEXTO_ERROR_VALIDACION;
	private GastoDTO dtoGasto;
	private MyArray monedaGuarani;
	private List<SubDiarioDetalleDTO> detalles;
	
	private Misc m = new Misc();
	
	public ValidadorFacturaGasto(GastoDTO dtoGasto, MyArray monedaGuarani,
			List<SubDiarioDetalleDTO> detalles) {
		this.dtoGasto = dtoGasto;
		this.monedaGuarani = monedaGuarani;
		this.detalles = detalles;
	}
	
	@Override
	public String textoVerificarAceptar() {
		return this.mensajeVerificaAceptar;
	}

	@Override
	public String textoVerificarCancelar() {
		return "Error al cancelar";
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}
	
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;		
		mensajeVerificaAceptar = "No se puede realizar la operación debido a: \n";
		
		double totalAsignado = 0;
		String nroFactura = this.dtoGasto.getNumeroFactura();
		String idTimbrado = this.dtoGasto.getTimbrado().getId() + "";
		String nroTimbrado = (String) this.dtoGasto.getTimbrado().getPos1();
		String[] tipos = { Config.TIPO_STRING, Config.TIPO_NUMERICO };
		String[] campos = { "numeroFactura", "timbrado.id" };
		String[] values = { nroFactura, idTimbrado };
		RegisterDomain rr = RegisterDomain.getInstance();
		String descripcion = Configuracion.CAMPO_NRO_FACTURA 
				+ this.dtoGasto.getNumeroFactura() + " - ("
				+ this.dtoGasto.getTimbrado().getPos1() + ")";
		
		try {
			
			//Guaraniza el totalAsignado..
			if (this.dtoGasto.getMoneda().compareTo(this.monedaGuarani) == 0) {
				totalAsignado = this.dtoGasto.getTotalAsignado();
			} else {
				totalAsignado = this.dtoGasto.getTotalAsignado() * this.dtoGasto.getTipoCambio();
			}		
			
			//Valida que se haya asignado el Proveedor..
			if (this.dtoGasto.getProveedor() == null) {
				mensajeVerificaAceptar += "\n - Debe seleccionar un Proveedor..";
				out = false;
			}
			
			//Valida la fecha de la factura
			if (this.dtoGasto.getFecha().compareTo(m.agregarDias(new Date(), 1)) != -1) {
				mensajeVerificaAceptar += "\n - La fecha no puede ser mayor a la del día..";
				out = false;
			}	
			
			//Valida el nro de factura..
			if (((this.dtoGasto.getNumeroFactura().split("-").length != 3) 
					|| !m.containsOnlyNumbers(this.dtoGasto.getNumeroFactura()))) {
				out = false;
				mensajeVerificaAceptar += "\n - Mal formato del número de Factura..";
			}
			
			//Valida el nro de timbrado..
			if ((nroTimbrado.trim().length() == 0) || (nroTimbrado.compareTo("0") == 0)) {
				out = false;
				mensajeVerificaAceptar += "\n - El timbrado no puede quedar vacío..";
			}
			
			//Valida si la factura ya existe..
			if (rr.existe(Gasto.class, campos, tipos, values, this.dtoGasto) == true) {
				out = false;
				mensajeVerificaAceptar += "\n - Ya existe en la Base de Datos una Factura "
						+ "con el mismo número y el mismo timbrado..";
			}
			
			//Valida el total Asignado..
			if (this.dtoGasto.getTotalAsignado() <= 0.0001) {
				mensajeVerificaAceptar += "\n - El Total de la Factura debe ser mayor a cero..";
				out = false;
			}
			
			//Valida el vencimiento..
			if (this.dtoGasto.getFecha().compareTo((Date) this.dtoGasto.getTimbrado().getPos2()) > 0) {
				mensajeVerificaAceptar += "\n - La fecha de la factura no puede ser mayor o "
						+ "igual a la fecha de vencimiento del timbrado..";
				out = false;
			}
			
			//Valida el campo existe cmpbte fisico..
			if ((this.dtoGasto.isExisteComprobanteFisico() == false) && 
					(this.dtoGasto.getMotivoComprobanteFisico().trim().length() == 0)) {
				mensajeVerificaAceptar += "\n - Debe especificar el motivo por el cual no "
						+ "se cuenta con el Comprobante físico..";
				out = false;
			}
			
			//Valida los totales del importe..
			if (m.esAproximado(totalAsignado, this.dtoGasto.getTotalImporteGs(), Configuracion.
					VALOR_DIFERENCIA_ACEPTADA_GASTOS) == false) {
				mensajeVerificaAceptar += "\n - El Total de Importe no coincide..";
				out = false;
			}
			
			//Valida el total del Iva..
			if (m.esAproximado(this.dtoGasto.getTotalIvaAsignado(), this.dtoGasto.getTotalIvaCalculado(), 
					Configuracion.VALOR_DIFERENCIA_ACEPTADA_IVA) == false) {
				mensajeVerificaAceptar += "\n - El Total de Iva no coincide..";
				out = false;
			}
			
			//Valida si la factura ya se ingreso al detalle..
			for (SubDiarioDetalleDTO det : this.detalles) {
				if (descripcion.toUpperCase().compareTo(det.getDescripcion().toUpperCase()) == 0) {
					out = false;
					mensajeVerificaAceptar += "\n - Ya ingresó una Factura "
							+ "con el mismo número y el mismo timbrado..";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return out;
	}
}
