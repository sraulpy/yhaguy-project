package com.yhaguy.gestion.venta;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Window;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.extras.agenda.ControlAgendaEvento;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.ArticuloListaPrecioDetalle;
import com.yhaguy.domain.ArticuloPrecioMinimo;
import com.yhaguy.domain.ArticuloUbicacion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.gestion.comun.ReservaDTO;
import com.yhaguy.util.Utiles;

public class VentaItemControl extends SoloViewModel {
	
	static final String[] ATRIBUTOS = new String[] { "articulo.codigoInterno",
			"articulo.codigoProveedor", "articulo.codigoOriginal",
			"articulo.descripcion", "stock", "articulo.id" };
	
	static final String[] COLUMNAS = new String[] { "Código Interno", "Código Proveedor",
			"Código Original", "Descripción", "Stock", "" };
	
	static final String[] ANCHOS = new String[] { "130px", "130px", "130px", "",
			"60px", "0px", "0px" };
	
	@Wire
	private Longbox cant;
	
	@Wire
	private Combobox cmblistaPrecio;
	
	@Wire
	private Doublebox dbxPrecio;

	private VentaDetalleDTO det;
	private VentaDTO dto;
	private List<VentaDetalleDTO> dets;
	private MyArray cliente;
	private MyPair deposito;
	private MyPair sucursal;
	private MyArray vendedor;
	private MyPair modoVenta;
	private ReservaDTO reserva;
	private String tipo;
	private WindowPopup wp;
	private long cantInicial; //cantidad inicial del item - para saber si se modifico la cantidad.
	private ControlAgendaEvento ctrAgenda;
	private String claveAgenda;
	private boolean editarArticulo; //si es false no permite seleccionar otro articulo
	private double tipoCambio; // el tipo de cambio de la venta..
	private MyArray moneda;
	private boolean reparto;
	
	public boolean getEditarArticulo(){
		return this.editarArticulo;
	}

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) VentaItemControl dato) {
		this.det = dato.getDet();
		this.dto = dato.getDto();
		this.cliente = dato.cliente;
		this.deposito = dato.deposito;
		this.sucursal = dato.sucursal;
		this.vendedor = dato.vendedor;
		this.reserva = dato.reserva;
		this.tipo = dato.tipo;
		this.cantInicial = dato.cantInicial;
		this.ctrAgenda = dato.ctrAgenda;
		this.claveAgenda = dato.claveAgenda;
		this.editarArticulo = dato.editarArticulo;
		this.modoVenta = dato.modoVenta;
		this.tipoCambio = dato.tipoCambio;
		this.moneda = dato.moneda;
		this.reparto = dato.reparto;
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	public void show(String modo, VentaDetalleDTO det,
			List<VentaDetalleDTO> dets, MyArray cliente, MyPair deposito,
			String tipo, ReservaDTO reserva, ControlAgendaEvento ctrAgenda,
			String claveAgenda, MyArray vendedor, MyPair sucursal,
			MyPair modoVenta, boolean editArt, double tipoCambio,
			MyArray moneda, boolean reparto, VentaDTO dto)
			throws Exception {

		this.cliente = cliente;
		this.deposito = deposito;
		this.sucursal = sucursal;
		this.vendedor = vendedor;
		this.modoVenta = modoVenta;
		this.dto = dto;
		this.det = det;
		this.dets = dets;
		this.tipo = tipo;
		this.reserva = reserva;
		this.ctrAgenda = ctrAgenda;
		this.claveAgenda = claveAgenda;
		this.cantInicial = det.getCantidad();
		this.editarArticulo = editArt;
		this.tipoCambio = tipoCambio;
		this.moneda = moneda;
		this.reparto = reparto;

		wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorVentaPedidoItem(this.det, this.cantInicial,
				this.tipo));
		wp.setModo(modo);
		wp.setTitulo("Ítem de Venta");
		wp.setWidth("600px");
		wp.setHigth("550px");
		wp.show(Configuracion.VENTA_ITEM_ZUL);
	}

	public boolean isClickAceptar() {
		return this.wp.isClickAceptar();
	}

	@Override
	public String getAliasFormularioCorriente() {
		
		if (this.tipo.compareTo(VentaControlBody.TIPO_PEDIDO) == 0) {
			return ID.F_VENTA_PEDIDO;
		} else {
			return ID.F_VENTA_PRESUPUESTO;
		}		
	}
	
	@Command
	@NotifyChange("*")
	public void buscarArticulo(@BindingParam("filtro") int filtro) throws Exception {

		String codInte = (String) this.det.getArticulo().getPos1();
		String codProv = (String) this.det.getArticulo().getPos2();
		String codOrig = (String) this.det.getArticulo().getPos3();
		String desc = this.det.getDescripcion();

		BuscarElemento be = new BuscarElemento();
		be.setClase(ArticuloDeposito.class);
		be.setAtributos(ATRIBUTOS);
		be.setNombresColumnas(COLUMNAS);
		be.setWidth("1000px");
		be.setAnchoColumnas(ANCHOS);
		be.setTitulo("Artículos del Depósito: " + deposito.getText());
		be.addWhere("c.deposito.id = " + deposito.getId() + " and c.articulo.articuloEstado.sigla = '" + Configuracion.SIGLA_ARTICULO_ESTADO_ACTIVO + "' ");
		be.show(this.getFiltro(filtro), filtro);

		if (be.isClickAceptar() == true) {
			long idAr = (long) be.getSelectedItem().getPos6();
			long disp = (long) be.getSelectedItem().getPos5();
			codInte = (String) be.getSelectedItem().getPos1();
			codProv = (String) be.getSelectedItem().getPos2();
			codOrig = (String) be.getSelectedItem().getPos3();
			desc = (String) be.getSelectedItem().getPos4();
			this.det.getArticulo().setId(idAr);
			this.det.getArticulo().setPos1(codInte);
			this.det.getArticulo().setPos2(codProv);
			this.det.getArticulo().setPos3(codOrig);
			this.det.getArticulo().setPos4(desc);
			this.det.setDescripcion(desc);
			this.det.setStockDisponible(disp);
			this.det.setPrecioGs(0);
			this.det.setCantidad(0);
			this.det.setCostoIvaIncluido(false);
			this.det.setUbicacion(this.getUbicacion(idAr));
			this.cant.focus();
		}
	}
	
	/**
	 * @return la ubicacion del articulo..
	 */
	private String getUbicacion(long idArticulo) throws Exception {
		String out = "";
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloUbicacion> ubics = rr.getUbicacion(idArticulo);
		for (ArticuloUbicacion ubic : ubics) {
			out += ubic.getEstante() + "." + ubic.getFila() + "." + ubic.getColumna() + " - ";
		}		
		return out.isEmpty() ? "SIN UBIC.." : out.substring(0, out.length() - 2);
	}
	
	/**
	 * @return el valor del filtro..
	 */
	private String getFiltro(int filtro) {
		String codInte = (String) this.det.getArticulo().getPos1();
		String codProv = (String) this.det.getArticulo().getPos2();
		String codOrig = (String) this.det.getArticulo().getPos3();
		String desc = this.det.getDescripcion();
		
		switch (filtro) {
		case 0:
			return codInte;
		case 1:
			return codProv;
		case 2:
			return codOrig;
		case 3:
			return desc;
		}		
		return "";
	}
	
	/**
	 * Habilita la edición del precio inicializando los valores
	 * a cero..
	 * @param comp
	 */
	@Command @NotifyChange("*")
	public void reloadPrecio(@BindingParam("comp") Longbox comp){
		if (this.det.esNuevo() == true) {
			this.det.setCantidad(0);
		}		
		this.det.setPrecioVentaUnitarioGs(0.0);
		this.det.setPrecioVentaUnitarioDs(0.0);
		this.det.setDescuentoUnitarioGs(0.0);
		this.det.setDescuentoUnitarioDs(0.0);
		this.det.setPrecioVentaFinalUnitarioGs(0.0);
		this.det.setPrecioVentaFinalUnitarioDs(0.0);
		this.det.setPrecioVentaFinalGs(0.0);
		this.det.setPrecioVentaFinalDs(0.0);		
		this.det.setNombreRegla("");
		this.det.setCoef_descuento(0.0);
		comp.focus();
	}
	
	/**
	 * Calcula los datos del Precio Final a partir del unitario
	 * @param el precio unitario en moneda local..
	 */
	public void reCalcularPrecio() {
		double precioUnitario = this.det.getPrecioVentaUnitarioGs();
		
		double descuentoGs = this.det.getDescuentoUnitarioGs();
		double precioFinalUndGs = precioUnitario - descuentoGs;
		double precioFinalGs = precioFinalUndGs * this.det.getCantidad();
		
		double descuentoDs = descuentoGs / this.tipoCambio;
		double precioUnitarioDs = precioUnitario / this.tipoCambio;
		double precioFinalUndDs = precioFinalUndGs / this.tipoCambio;
		double precioFinalDs = precioFinalGs / this.tipoCambio;
		
		this.det.setPrecioVentaUnitarioGs(precioUnitario);
		this.det.setPrecioVentaFinalUnitarioGs(precioFinalUndGs);
		this.det.setPrecioVentaFinalGs(precioFinalGs);
		this.det.setDescuentoUnitarioDs(descuentoDs);
		this.det.setPrecioVentaUnitarioDs(precioUnitarioDs);
		this.det.setPrecioVentaFinalUnitarioDs(precioFinalUndDs);
		this.det.setPrecioVentaFinalDs(precioFinalDs);
	}
	
	@Command
	@NotifyChange("det")
	public void obtenerPrecioVenta() throws Exception {
		if (this.isEmpresaBaterias()) {
			this.setPrecioVentaBaterias();
		} else {
			this.setPrecioVenta();
		}
		this.det.setDescuentoPorcentaje(0);
		this.det.setDescuentoUnitarioGs(0);
		
		// verifica si se habilito el precio minimo..
		double precioMinimo = this.getPrecioMinimo();
		if(precioMinimo > 0)
			this.det.setPrecioMinimoGs(precioMinimo);		
		this.dbxPrecio.focus();
	}
	
	/**
	 * setea el precio de venta..
	 */
	private void setPrecioVenta() throws Exception {
		double costo = this.getCostoArticulo();
		int margen = (int) this.det.getListaPrecio().getPos2();
		int margenMin = Configuracion.PORCENTAJE_RENTABILIDAD_MINIMA;
		double ganancia = this.m.obtenerValorDelPorcentaje(costo, margen);
		double gananciaMin = this.m.obtenerValorDelPorcentaje(costo, margenMin);
		double porcIva = 10;
		double iva = this.m.obtenerValorDelPorcentaje((costo + ganancia),
				porcIva);
		double ivaMin = this.m.obtenerValorDelPorcentaje((costo + gananciaMin),
				porcIva);
		this.det.setPrecioGs(costo + ganancia + iva);
		this.det.setPrecioMinimoGs(costo + gananciaMin + ivaMin);
	}
	
	/**
	 * setea el precio de venta para la empresa baterias..
	 */
	private void setPrecioVentaBaterias() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idListaPrecio = this.det.getListaPrecio().getId();
		String codArticulo = (String) this.det.getArticulo().getPos1();		
		ArticuloListaPrecioDetalle lista = rr.getListaPrecioDetalle(idListaPrecio, codArticulo);
		String formula = (String) this.det.getListaPrecio().getPos3();
		if (lista != null && formula == null) {
			this.det.setPrecioGs(this.dto.isCondicionContado()? lista.getPrecioGs_contado() : lista.getPrecioGs_credito());
		} else {
			double costo = this.getCostoArticulo();
			int margen = (int) this.det.getListaPrecio().getPos2();
			double precio = ControlArticuloCosto.getPrecioVenta(costo, margen);
			
			// formula lista precio mayorista..
			if (idListaPrecio == 2 && formula != null) {
				ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
				ArticuloListaPrecioDetalle precioDet = rr.getListaPrecioDetalle(distribuidor.getId(), codArticulo);
				if (precioDet != null) {
					double cont = precioDet.getPrecioGs_contado();
					double cred = precioDet.getPrecioGs_credito();
					double formulaCont = cont + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_contado(), 10);
					double formulaCred = cred + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_credito(), 10);
					this.det.setPrecioGs(this.dto.isCondicionContado()? formulaCont : formulaCred);
					this.det.setPrecioMinimoGs(this.det.getPrecioGs());
				} else {
					margen = distribuidor.getMargen();
					double precioGs = ControlArticuloCosto.getPrecioVenta(costo, margen);
					double formula_ = precioGs + Utiles.obtenerValorDelPorcentaje(precioGs, 10);
					this.det.setPrecioGs(formula_);
					this.det.setPrecioMinimoGs(formula_);
				}

			// formula lista precio minorista..
			} else if (idListaPrecio == 3 && formula != null) {
				ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
				ArticuloListaPrecioDetalle precioDet = rr.getListaPrecioDetalle(distribuidor.getId(), codArticulo);
				if (precioDet != null) {
					double cont = precioDet.getPrecioGs_contado() + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_contado(), 10);
					double cred = precioDet.getPrecioGs_credito() + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_credito(), 10);
					double formulaCont = (cont * 1.18) / 0.8;
					double formulaCred = (cred * 1.18) / 0.8;
					this.det.setPrecioGs(this.dto.isCondicionContado()? formulaCont : formulaCred);
					this.det.setPrecioMinimoGs(this.det.getPrecioGs());
				} else {
					margen = distribuidor.getMargen();
					double precioGs = ControlArticuloCosto.getPrecioVenta(costo, margen);
					double formula_ = ((precioGs + Utiles.obtenerValorDelPorcentaje(precioGs, 10)) * 1.18) / 0.8;
					this.det.setPrecioGs(formula_);
					this.det.setPrecioMinimoGs(formula_);
				}

			} else {
				this.det.setPrecioGs(precio);
				this.det.setPrecioMinimoGs(precio);
			}		
		}
	}
	
	/**
	 * @return el costo del articulo segun el ArticuloDeposito..
	 */
	private double getCostoArticulo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idArticulo = this.det.getArticulo().getId();
		Articulo art = rr.getArticuloById(idArticulo);
		return art.getCostoGs();
	}
	
	/**
	 * @return el precio minimo habilitado..
	 */
	private double getPrecioMinimo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idArticulo = this.det.getArticulo().getId();
		ArticuloPrecioMinimo art = rr.getArticuloPrecioMinimo(idArticulo);
		if(art != null)
			return art.getPrecioMinimo();
		return 0;
	}
	
	@Command
	public void validarPrecio(@BindingParam("comp") Component comp)
			throws Exception {
		double precio = this.det.getPrecioGs();
		double precioMinimo = this.det.getPrecioMinimoGs();
		double costoGs = this.getCostoArticulo();
		
		if ((precio - costoGs) >= costoGs) {
			Clients.showNotification("Utilidad mayor al 100 %..",
					Clients.NOTIFICATION_TYPE_ERROR, comp, null, 2000);
		}
		
		if (precio < precioMinimo && (!this.isEmpresaBaterias())) {
			Clients.showNotification("Precio menor al precio mínimo..",
					Clients.NOTIFICATION_TYPE_ERROR, comp, null, 2000);
			this.obtenerPrecioVenta();
		}
		BindUtils.postNotifyChange(null, null, this.det, "precioGs");
	}
	
	@Command
	public void verHistorial() {
		Window win = (Window) Executions.createComponents("/yhaguy/gestion/venta/VerHistorial.zul", this.mainComponent, null);
		win.doOverlapped();
	}
	
	@DependsOn("det")
	public List<MyArray> getHistorial() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> vtas = rr.getHistorialVentaArticulo(this.det.getArticulo().getId(), this.cliente.getId());
		for (Object[] vta : vtas) {
			Venta v = (Venta) vta[0];
			VentaDetalle d = (VentaDetalle) vta[1];
			MyArray my = new MyArray();
			my.setPos1(v.getFecha());
			my.setPos2(TipoMovimiento.getAbreviatura(v.getTipoMovimiento().getSigla()));
			my.setPos3(v.getNumero());
			my.setPos4(d.getCantidad());
			my.setPos5(d.getPrecioGs());
			out.add(my);
		}
		return out;
	}
	
	/**
	 * @return el titulo de la ventana historial ventas..
	 */
	public String getTitleHistorial() {
		return "Historial de Ventas - Cliente: " + this.cliente.getPos2();
	}
	
	/******************************** DESCUENTO *********************************/
	
	private Converter<Double, Double, Component> convert = new DescuentoConverter();
	
	/**
	 * descuento..
	 */
	public void descontar(double descuento, double porcDescuento, Component cmp){
		this.reCalcularPrecio();
	}
	
	@Command @NotifyChange("*")
	public void descontarGs(@BindingParam("cmp") Component cmp){		
		double descuento = this.det.getDescuentoUnitarioGs();
		double porcDescuento = descuento / this.det.getPrecioVentaUnitarioGs();
		this.det.setDescuentoPorcentaje(porcDescuento);
		this.descontar(descuento, porcDescuento, cmp);		 
	}
	
	@Command @NotifyChange("*")
	public void descontarDs(@BindingParam("cmp") Component cmp){
		double descuentoDs = this.det.getDescuentoUnitarioDs();
		this.det.setDescuentoUnitarioGs(descuentoDs * this.tipoCambio);		
		double descuentoGs = this.det.getDescuentoUnitarioGs();
		double porcDescuento = descuentoGs / this.det.getPrecioVentaUnitarioGs();
		this.det.setDescuentoPorcentaje(porcDescuento);
		this.descontar(descuentoGs, porcDescuento, cmp);		 
	}
	
	@Command @NotifyChange("*") 
	public void descontarPorcentaje(@BindingParam("cmp") Component cmp){
		double precio = this.det.getPrecioGs();
		double porcDescuento = this.det.getDescuentoPorcentaje();
		this.det.setDescuentoUnitarioGs(precio * this.det.getCantidad() * porcDescuento);
		this.descontar(this.det.getDescuentoUnitarioGs(), porcDescuento, cmp);
	}
	
	/****************************************************************************/
	
	
	/********************************** UTILES **********************************/
	
	@Command 
	@NotifyChange("*")
	public void verificarCantidad(@BindingParam("comp") Longbox comp) 
		throws Exception {
		long cant = this.det.getCantidad();
		long cant_ = this.det.getCantidad() - cantInicial;

		// verifica si es una cantidad negativa..
		if (cant < 0) {
			m.mensajePopupTemporal("No se aceptan cantidades negativas ",
					"error", comp);
			this.det.setCantidad(0);

			// verifica disponibilidad de stock si se esta insertando..
		} else if ((this.tipo.compareTo(VentaControlBody.TIPO_PEDIDO) == 0)
				&& (this.det.esNuevo() == true)
				&& (cant > this.det.getStockDisponible())) {
			m.mensajePopupTemporal("Stock insuficiente", "error", comp);
			this.det.setCantidad(0);
		
			//verifica la disponibilidad de stock si se esta modificando..
		} else if ((this.tipo.compareTo(VentaControlBody.TIPO_PEDIDO) == 0)
				&& (this.det.esNuevo() == false)
				&& (cant_ > this.det.getStockDisponible())) {
			m.mensajePopupTemporal("Stock insuficiente", "error", comp);
			this.det.setCantidad(cantInicial);
		}
		this.obtenerPrecioVenta();
		this.dbxPrecio.focus();
	}
	
	/**
	 *	Validador de Insertar/Editar item..
	 */
	class ValidadorVentaPedidoItem implements VerificaAceptarCancelar {
		
		private VentaDetalleDTO item = new VentaDetalleDTO();
		private long cantInicial;
		private String mensajeError = "";	
		String tipo;	

		//constructor
		public ValidadorVentaPedidoItem(VentaDetalleDTO item, long cantInicial,
				String tipo) {
			this.item = item;
			this.cantInicial = cantInicial;
			this.tipo = tipo;
		}

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensajeError = "No se puede realizar la operación debido a: \n";
			
			//debe seleccionarse un articulo..
			if (this.item.getArticulo().esNuevo() == true) {
				this.mensajeError += "\n - Debe seleccionar un artículo..";
				out = false;
			}
			
			//en caso de editar la descripcion no puede quedar vacia..
			if (this.item.getDescripcion().trim().length() == 0) {
				this.mensajeError += "\n - La descripción del ítem no puede ser vacía..";
				out = false;
			}
			
			//la cantidad debe ser mayor a cero..
			if (this.item.getCantidad() <= 0) {
				this.mensajeError += "\n - La cantidad debe ser mayor a cero..";
				out = false;
			}
			
			// verifica si el item ya exite en el detalle..
			if (this.isDuplicado(this.item)) {
				this.mensajeError += "\n - No se aceptan items duplicados..";
				out = false;
			}
			
			//verifica disponibilidad de stock si se esta insertando..
			if ((this.tipo.compareTo(VentaControlBody.TIPO_PEDIDO) == 0) 
					&& (this.item.esNuevo() == true)
						&& (this.item.getCantidad() > this.item.getStockDisponible())) {
				this.mensajeError += "\n - Stock insuficiente, favor verifique..";
				out = false;
			}
			
			//verifica la disponibilidad de stock si se esta modificando..
			long cant = this.item.getCantidad() - cantInicial;
			
			if ((this.tipo.compareTo(VentaControlBody.TIPO_PEDIDO) == 0) 
					&& (this.item.esNuevo() == false)
						&& (cant > this.item.getStockDisponible())) {
				this.mensajeError += "\n - Stock insuficiente, favor verifique..";
				out = false;
			}
			
			//verifica que se haya aplicado una regla de precio..
			if (this.item.getListaPrecio() == null) {
				this.mensajeError += "\n - Debe seleccionar una lista de precio..";
				out = false;
			}
			
			return out;
		}
		
		/**
		 * @return si el item ya existe en el detalle..
		 */
		private boolean isDuplicado(VentaDetalleDTO item) {
			long codigo = item.getArticulo().getId().longValue();
			for (VentaDetalleDTO detalle : dets) {
				long codigo_ = detalle.getArticulo().getId().longValue();
				if(codigo == codigo_)
					return true;
			}			
			return false;
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
			return "Error al Cancelar..";
		}	
	}
	
	/**
	 * GETS / SETS 
	 */
	
	/**
	 * @return las listas de precio..
	 * pos1:descripcion
	 * pos2:margen
	 */
	public List<MyArray> getListasDePrecio() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloListaPrecio> precios = rr.getListasDePrecio();
		List<MyArray> out = new ArrayList<MyArray>();
		for (ArticuloListaPrecio precio : precios) {
			MyArray mprecio = new MyArray(precio.getDescripcion(), precio.getMargen(), precio.getFormula());
			mprecio.setId(precio.getId());
			out.add(mprecio);
		}
		return out;
	}
	
	/**
	 * @return las listas de precio habilitadas por cliente..
	 * pos1:descripcion
	 * pos2:margen
	 */
	public List<MyArray> getListasDePrecioHabilitadas() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		out.add(this.det.getListaPrecio());
		return out;
	}
	
	public boolean isEmpresaBaterias() {
		return Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS);
	}
	
	public UtilDTO getUtilDto(){
		return (UtilDTO) this.getDtoUtil();
	}
	
	public MyArray getMoneda(){
		return moneda;
	}
	
	public boolean isMonedaLocal(){
		String sigla = (String) moneda.getPos2();
		return sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0;
	}
	
	public boolean isVentaConReparto(){
		return this.reparto;
	}
	
	public VentaDetalleDTO getDet() {
		return det;
	}

	public void setDet(VentaDetalleDTO det) {
		this.det = det;
	}
	
	public MyPair getModoVenta() {
		return modoVenta;
	}

	public void setModoVenta(MyPair modoVenta) {
		this.modoVenta = modoVenta;
	}

	public Converter<Double, Double, Component> getConvert() {
		return convert;
	}

	public List<VentaDetalleDTO> getDets() {
		return dets;
	}

	public void setDets(List<VentaDetalleDTO> dets) {
		this.dets = dets;
	}

	public VentaDTO getDto() {
		return dto;
	}

	public void setDto(VentaDTO dto) {
		this.dto = dto;
	}
}

// Converter del descuento..
class DescuentoConverter implements Converter<Double, Double, Component>{

	@Override
	public Double coerceToBean(Double val, Component arg1, BindContext arg2) {		
		return val / 100;
	}

	@Override
	public Double coerceToUi(Double val, Component arg1, BindContext arg2) {
		return val * 100;
	}	
}

