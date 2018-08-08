package com.yhaguy.gestion.compras.gastos.subdiario;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.CentroCosto;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;
import com.yhaguy.gestion.contabilidad.subdiario.SubDiarioDTO;

public class GastoSimpleControl extends SoloViewModel implements VerificaAceptarCancelar {
	
	static final String ZUL_FORMA_PAGO_GASTO = "/yhaguy/gestion/compras/gastos/subDiario/GastoFormaPago.zul";
	
	private GastoDTO dto = new GastoDTO();
	private SubDiarioDTO subDiario = new SubDiarioDTO();
	
	private List<MyArray> monedasSeleccionadas = new ArrayList<MyArray>();
	private GastoDetalleDTO nvoItem = new GastoDetalleDTO();
	
	private List<GastoDetalleDTO> selectedItems = new ArrayList<GastoDetalleDTO>();
	private String itemsEliminar;
	
	private UtilDTO utilDto = (UtilDTO) this.getDtoUtil();
	
	private List<MyArray> movimientosDeGasto = new ArrayList<MyArray>();
	private MyArray facturaGastoContado = utilDto.getTmFacturaGastoContado();
	private MyArray facturaGastoCredito = utilDto.getTmFacturaGastoCredito();
	private MyArray autoFactura = utilDto.getTmAutoFactura();
	private MyArray boletaVenta = utilDto.getTmBoletaVenta();
	private MyArray condicionContado = utilDto.getCondicionPagoContado();
	private MyArray condicionCredito30 = utilDto.getCondicionPagoCredito30();
	private MyArray otrosComprobantes = utilDto.getTmOtrosComprobantes();
	
	private String mensajeError = "";
	
	private ArticuloGastoDTO nvoItemGasto = new ArticuloGastoDTO();
	private ReciboFormaPagoDTO nvoFormaPago;
	private ReciboFormaPagoDTO selectedFormaPago;
	
	private Window win;
	
	@Wire
	private Textbox txb_benef;	
	@Wire
	private Row rwNroRetencion;
	@Wire
	private Row rwTimbradoRetencion;
	@Wire
	private Row rwTimbradoVencimiento;
	@Wire
	private Doublebox dbxGs;
	
	@Init(superclass = true)
	public void init(@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) GastoDTO dato) {
		this.dto = dato;
		this.monedasSeleccionadas.add(this.dto.getMoneda()); 
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(){
	}	
	
	@Override
	public String getAliasFormularioCorriente(){
		return ID.F_FACTURA_GASTO;
	}	
	
	/*********************** COMANDOS ***********************/	
	
	@Command
	@NotifyChange("*")
	public void openFormasPago() {
		double totalGs = this.dto.getImporteGs();
		double totalGs_ = 0;
		for (ReciboFormaPagoDTO item : this.dto.getFormasPago()) {
			totalGs_ += item.getMontoGs();
		}
		
		this.nvoFormaPago = new ReciboFormaPagoDTO();
		this.nvoFormaPago.setTipo(this.utilDto.getFormaPagoEfectivo());
		this.nvoFormaPago.setMontoGs(totalGs - totalGs_);
		this.win = (Window) Executions.createComponents(ZUL_FORMA_PAGO_GASTO,
				this.mainComponent, null);
		Selectors.wireComponents(this.win, this, false);
		this.win.doOverlapped();
	}
	
	@Command
	@NotifyChange("*")
	public void addFormaPago() {
		this.dto.getFormasPago().add(this.nvoFormaPago);
		this.nvoFormaPago = null;
		this.win.detach();
	}
	
	@Command
	@NotifyChange("*")
	public void deleteFormaPago() {
		if(this.mensajeSiNo("Desea eliminar el item..?") == false)
			return;
		this.dto.getFormasPago().remove(this.selectedFormaPago);
		this.selectedFormaPago = null;
	}
	
	@Command 
	@NotifyChange("*")
	public void seleccionarFormaPago() throws Exception {
		this.reloadFormaPago(this.nvoFormaPago);
		String siglaFP = this.getNvoFormaPago().getTipo().getSigla();
		String siglaFPRE = Configuracion.SIGLA_FORMA_PAGO_RETENCION;

		if (siglaFP.equals(siglaFPRE)) {
			this.rwNroRetencion.setVisible(true);
			this.rwTimbradoRetencion.setVisible(true);
			this.rwTimbradoVencimiento.setVisible(true);
			this.nvoFormaPago.setMontoGs(this.getImporteRetencion());

		} else {
			this.rwNroRetencion.setVisible(false);
			this.rwTimbradoRetencion.setVisible(false);
			this.rwTimbradoVencimiento.setVisible(false);
			this.dbxGs.setReadonly(false);
			this.nvoFormaPago.setDescripcion(this.nvoFormaPago.getTipo()
					.getText());
		}
	}
	
	/**
	 * Inicializa los datos de forma de pago..
	 */
	private void reloadFormaPago(ReciboFormaPagoDTO formaPago) {
		formaPago.setDescripcion("");
		formaPago.setTarjetaNumero("");
		formaPago.setTarjetaNumeroComprobante("");
		formaPago.setTarjetaProcesadora(new MyArray());
		formaPago.setTarjetaCuotas(0);
		formaPago.setTarjetaTipo(new MyPair());
		formaPago.setBancoCta(null);
		formaPago.setChequeFecha(null);
		formaPago.setChequeBanco(new MyPair());
		formaPago.setChequeNumero("");
		formaPago.setChequeLibrador("");
		formaPago.setDepositoBancoCta(new MyArray());
		formaPago.setDepositoNroReferencia("");
		formaPago.setRetencionNumero("");
		formaPago.setRetencionTimbrado("");
		formaPago.setRetencionVencimiento(null);
	}
	
	@Command 
	@NotifyChange("*")
	public void abrirVentanaTimbrado() {
		String nroTimbrado = (String) this.dto.getTimbrado().getPos1();

		WindowTimbrado w = new WindowTimbrado();
		w.setIdProveedor(this.dto.getProveedor().getId());
		w.setTimbrado(nroTimbrado);
		w.show(WindowPopup.NUEVO, w);

		if (w.isClickAceptar()) {
			this.dto.setTimbrado(w.getSelectedTimbrado());
		} else {
			this.dto.setTimbrado(new MyArray("", null));
		}

		BindUtils.postNotifyChange(null, null, this.dto, "timbrado");
	}	
	
	@Command
	@NotifyChange({"dto", "simboloMoneda", "labelTotalFactura", "format"})
	public void refreshValores(){		
		double tipoCambio = utilDto.getCambioVentaBCP(this.dto.getMoneda());
		this.dto.setTipoCambio(tipoCambio);	
		actualizarDetalle();
	}	
	
	/**
	 * Actualiza los valores del Detalle respetando la columna 'Moneda Local'..
	 */
	private void actualizarDetalle(){
		for (GastoDetalleDTO d : this.dto.getDetalles()) {
			d.setMontoDs(d.getMontoGs() / this.dto.getTipoCambio());
		}
		BindUtils.postNotifyChange(null, null, this.dto.getDetalles(), "*");
	}

	@Command
	@NotifyChange("*")
	public void abrirVentanaInsertarItem() throws Exception {
		this.nvoItem = new GastoDetalleDTO();
		this.nvoItem.setCentroCosto(this.getCentroCosto());
		this.nvoItem.setTipoIva(this.getUtilDto().getTipoIva10());

		if (this.dto.isAutoFactura() || this.dto.isBoletaVenta())
			this.nvoItem.setTipoIva(this.getUtilDto().getTipoIvaExento());

		this.nvoItem.setCantidad(1);

		WindowPopup w = new WindowPopup();
		w.setDato(this);
		w.setCheckAC(this);
		w.setModo(WindowPopup.NUEVO);
		w.setTitulo("Insertar ítem de Gasto");
		w.setWidth("470px");
		w.setHigth("330px");
		w.show(Configuracion.INSERTAR_ITEM_GASTO);
		if (w.isClickAceptar()) {
			this.dto.getDetalles().add(this.nvoItem);
			if (this.dto.isGastoContado() 
					|| this.dto.isAutoFactura() 
						|| this.dto.isBoletaVenta()) {
				this.nvoFormaPago = new ReciboFormaPagoDTO();
				this.nvoFormaPago.setDescripcion("EFECTIVO");
				this.nvoFormaPago.setMoneda(this.getUtilDto().getMonedaGuarani());
				this.nvoFormaPago.setMontoGs(this.nvoItem.getImporteGs());
				this.nvoFormaPago.setTipo(this.getUtilDto().getFormaPagoEfectivo());
				this.dto.getFormasPago().add(this.nvoFormaPago);
			}
		}
	}
		
	@Command
	@NotifyChange("*")
	public void editarItem(@BindingParam("item") GastoDetalleDTO item)
			throws Exception {

		// Serializar el Objeto
		byte[] ori = this.m.serializar(item);
		this.nvoItem = item;

		String modo = this.dto.isFondoFijo() ? WindowPopup.SOLO_LECTURA
				: WindowPopup.NUEVO;

		boolean ok = this.abrirVentanaEditarItem(item, modo);

		if (ok == false) {
			// Restaurar el objeto
			GastoDetalleDTO itemOrig = (GastoDetalleDTO) this.m
					.deSerializar(ori);
			int pos = this.dto.getDetalles().indexOf(item);
			this.dto.getDetalles().set(pos, itemOrig);
		}
	}
	
	/**
	 * despliega la ventana del item..
	 */
	public boolean abrirVentanaEditarItem(GastoDetalleDTO item, String modo)
			throws Exception {

		WindowPopup w = new WindowPopup();
		w.setDato(this);
		w.setCheckAC(this);
		w.setModo(modo);
		w.setTitulo("Insertar ítem de Gasto");
		w.setHigth("440px");
		w.setWidth("550px");
		
		if(modo.equals(WindowPopup.SOLO_LECTURA))
			w.setSoloBotonCerrar();
		
		w.show(Configuracion.INSERTAR_ITEM_GASTO);

		return w.isClickAceptar();
	}	

	@Command
	@NotifyChange("*")
	public void eliminarItem(){
		
		if (this.selectedItems.size() == 0) {
			mensajeError("Debe seleccionar al menos un ítem..");
			return;
		}
		
		if (this.confirmarEliminarItem() == true) {
			for (GastoDetalleDTO d : this.selectedItems) {
				this.dto.getDetalles().remove(d);
			}
			this.selectedItems = new ArrayList<GastoDetalleDTO>();
		}		
	}
	
	private boolean confirmarEliminarItem(){
		this.itemsEliminar = "Esta seguro de eliminar los sgts ítems: \n";		
		for (GastoDetalleDTO d : this.selectedItems) {
			this.itemsEliminar = this.itemsEliminar + "\n - " + d.getArticuloGasto().getDescripcion();
		}		
		return this.mensajeSiNo(this.itemsEliminar);
	}
	
	/**
	 * @return todos los tipos de movimiento de Gasto que son Facturas
	 */
	public List<MyArray> getMovimientosDeGasto() {
		this.movimientosDeGasto.clear();
		this.movimientosDeGasto.add(this.facturaGastoContado);
		this.movimientosDeGasto.add(this.autoFactura);
		this.movimientosDeGasto.add(this.boletaVenta);
		this.movimientosDeGasto.add(this.facturaGastoCredito);
		this.movimientosDeGasto.add(this.otrosComprobantes);
		return this.movimientosDeGasto;
	}
	
	@Command
	@NotifyChange("dto")
	public void updateTipoMovimiento() throws Exception {
		if(this.dto.isAutoFactura() == false) {
			this.dto.setProveedor(new MyArray());
			this.dto.setNumeroFactura("");
			this.dto.setTimbrado(new MyArray());
			this.dto.setNumeroTimbrado("");
			this.dto.setBeneficiario("");
		}			
		this.modificarCondicionPago();
		this.calcularVencimiento();
		this.verificarAutoFactura();
	}
	
	@Command
	public void updateCondicionPago(){
		this.calcularVencimiento();
		this.modificarTipoMovimiento();
	}
	
	@Command
	public void updateFecha(){
		this.calcularVencimiento();
	}
	
	private void modificarTipoMovimiento(){
		MyArray condicion = this.dto.getCondicionPago();
		if (condicion.compareTo(condicionContado) == 0) {
			this.dto.setTipoMovimiento(facturaGastoContado);
		} else {
			this.dto.setTipoMovimiento(facturaGastoCredito);
		}
		BindUtils.postNotifyChange(null, null, this.dto, "tipoMovimiento");
	}	
	
	private void calcularVencimiento(){
		int plazo = (int) this.dto.getCondicionPago().getPos2();
		this.dto.setVencimiento(m.agregarDias(this.dto.getFecha(), plazo));		
		BindUtils.postNotifyChange(null, null, this.dto, "vencimiento");
	}	
	
	private void modificarCondicionPago(){
		MyArray tipoMovimiento = this.dto.getTipoMovimiento();
		MyArray condicionPago = this.dto.getCondicionPago();
		if (tipoMovimiento.compareTo(facturaGastoContado) == 0) {
			this.dto.setCondicionPago(condicionContado);
		} else if ((tipoMovimiento.compareTo(facturaGastoCredito) == 0)
					&& (condicionPago.compareTo(condicionContado) == 0)) {
			this.dto.setCondicionPago(condicionCredito30);
		}
		BindUtils.postNotifyChange(null, null, this.dto, "condicionPago");	
	}			
	
	/**
	 * verifica la numeracion de autofacturas..
	 */
	private void verificarAutoFactura() throws Exception {
		if (this.dto.isAutoFactura() == false)
			return;

		MyArray talonario = this.dto.getTalonarioAutoFactura();
		int boca = (int) talonario.getPos2();
		int punto = (int) talonario.getPos3();
		String nro = AutoNumeroControl.getAutoNumero(
				(String) talonario.getPos1(), 7, true);
		
		this.dto.setNumeroFactura("00" + boca + "-00" + punto + "-" + nro);
		this.dto.setTimbrado(this.getTimbrado((MyPair) talonario.getPos6()));
		this.dto.setNumeroTimbrado((String) this.dto.getTimbrado().getPos1());
		this.dto.setProveedor(this.getProveedor(Configuracion.ID_PROVEEDOR_YHAGUY_MRA));	

		BindUtils.postNotifyChange(null, null, this.dto, "numeroFactura");
		BindUtils.postNotifyChange(null, null, this.dto, "numeroTimbrado");
		BindUtils.postNotifyChange(null, null, this.dto, "timbrado");
		BindUtils.postNotifyChange(null, null, this.dto, "proveedor");
		
		this.txb_benef.focus();
	}
	
	
	
	@Override
	public boolean verificarAceptar() {
		boolean out = true;
		mensajeError = "No se puede realizar la operación debido a: \n";
		GastoDetalleDTO item = this.getNvoItem();
		
		if (item.getArticuloGasto().esNuevo() == true) {
			mensajeError = mensajeError + "\n - Debe seleccionar un Artículo..";
			out = false;
		}
		
		if (item.getObservacion().trim().length() == 0) {
			mensajeError = mensajeError + "\n - Debe ingresar una observación..";
			out = false;
		}
		
		if (item.getCentroCosto().esNuevo() == true) {
			mensajeError = mensajeError + "\n - Debe seleccionar un Centro de Costo..";
			out = false;
		}
		
		if (item.getCantidad() <= 0) {
			mensajeError = mensajeError + "\n - La cantidad debe ser mayor a cero..";
			out = false;
		}
		
		if (item.getMontoGs() <= 0) {
			mensajeError = mensajeError + "\n - El costo debe ser mayor a cero..";
			out = false;
		}
		
		return out;
	}	
	
	/*********************** GET/SET ***********************/
	
	/**
	 * @return las formas de pago..
	 */
	public List<MyPair> getFormasDePago() {
		
		List<MyPair> out = new ArrayList<MyPair>();
		out.addAll(this.utilDto.getFormasDePago());
		out.remove(this.utilDto.getFormaPagoChequePropio());
		out.remove(this.utilDto.getFormaPagoChequeTercero());
		out.remove(this.utilDto.getFormaPagoTarjetaCredito());
		out.remove(this.utilDto.getFormaPagoTarjetaDebito());
		out.remove(this.utilDto.getFormaPagoDepositoBancario());
		out.remove(this.utilDto.getFormaPagoChequeAutoCobranza());	
		return out;
	}
	
	@DependsOn({ "dto.proveedor", "dto.numeroFactura", "dto.timbrado",
			"dto.beneficiario" })
	public boolean isDetalleVisible() {
		if (this.dto.isAutoFactura())
			return this.dto.getBeneficiario().isEmpty() == false;

		String timbrado = (String) this.dto.getTimbrado().getPos1();
		return (this.dto.getProveedor().esNuevo() == false)
				&& (this.dto.getNumeroFactura().isEmpty() == false)
				&& (timbrado.isEmpty() == false);
	}
	
	/**
	 * @return el centro de costo..
	 */
	private MyArray getCentroCosto() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CentroCosto cc = (CentroCosto) rr.getObject(CentroCosto.class.getName(), 1);
		MyArray out = new MyArray();
		out.setId(cc.getId());
		out.setPos1(cc.getDescripcion());
		return out;
	}
	
	/**
	 * @return timbrado como MyArray..
	 */
	private MyArray getTimbrado(MyPair timbrado) {
		MyArray out = new MyArray();
		out.setId(timbrado.getId());
		out.setPos1(timbrado.getText());		
		return out;
	}
	
	/**
	 * @return timbrado como MyArray..
	 */
	private MyArray getProveedor(long idProveedor) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Proveedor prov = rr.getProveedorById(idProveedor);
		MyArray out = new MyArray();
		out.setId(idProveedor);	
		out.setPos1(prov.getCodigoEmpresa());
		out.setPos2(prov.getRazonSocial());
		out.setPos3(prov.getRuc());
		return out;
	}

	/**
	 * @return el String para el label del total de la Factura
	 */
	public String getLabelTotalFactura() {
		return "Total Factura " + this.dto.getMoneda().getPos2();
	}

	/**
	 * @return el String para el simbolo de la moneda en los valores
	 */
	public String getSimboloMoneda() {
		return this.dto.getMoneda().getPos2() + "";
	}
	
	/**
	 * @return el importe calculado de retencion..
	 */
	private double getImporteRetencion() {
		double totalIva = this.dto.getImporteIva10();
		return this.m.obtenerValorDelPorcentaje(totalIva,
				Configuracion.PORCENTAJE_RETENCION);
	}
	
	@Override
	public String textoVerificarAceptar() {
		return this.mensajeError;
	}

	@Override
	public boolean verificarCancelar() {
		return true;
	}

	@Override
	public String textoVerificarCancelar() {
		return null;
	}

	public UtilDTO getUtilDto() {
		return utilDto;
	}
	
	public ArticuloGastoDTO getNvoItemGasto() {
		return nvoItemGasto;
	}

	public void setNvoItemGasto(ArticuloGastoDTO nvoItemGasto) {
		this.nvoItemGasto = nvoItemGasto;
	}

	public boolean getCheckmark(){
		boolean out = false;
		if (this.dto.getDetalles().size() > 0) {
			out = true;
		}
		return out;
	}

	public GastoDetalleDTO getNvoItem() {
		return nvoItem;
	}

	public void setNvoItem(GastoDetalleDTO nvoItem) {
		this.nvoItem = nvoItem;
	}
	
	public GastoDTO getDto() {
		return dto;
	}

	public void setDto(GastoDTO dto) {
		this.dto = dto;
	}	
	
	public SubDiarioDTO getSubDiario() {
		return subDiario;
	}

	public void setSubDiario(SubDiarioDTO subDiario) {
		this.subDiario = subDiario;
	}
	
	public List<GastoDetalleDTO> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<GastoDetalleDTO> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public String getItemsEliminar() {
		return itemsEliminar;
	}

	public void setItemsEliminar(String itemsEliminar) {
		this.itemsEliminar = itemsEliminar;
	}

	public ReciboFormaPagoDTO getNvoFormaPago() {
		return nvoFormaPago;
	}

	public void setNvoFormaPago(ReciboFormaPagoDTO nvoFormaPago) {
		this.nvoFormaPago = nvoFormaPago;
	}

	public ReciboFormaPagoDTO getSelectedFormaPago() {
		return selectedFormaPago;
	}

	public void setSelectedFormaPago(ReciboFormaPagoDTO selectedFormaPago) {
		this.selectedFormaPago = selectedFormaPago;
	}
}
