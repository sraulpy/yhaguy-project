package com.yhaguy.gestion.mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.ArticuloListaPrecioDetalle;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Remision;
import com.yhaguy.domain.RemisionDetalle;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Vehiculo;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.util.Utiles;

public class VentasMobileViewModel extends SimpleViewModel {

	private String razonSocial = "";
	private String razonSocialVendedor = "";
	private String codigoInterno = "";
	private String observacion = "";
	private String numero = "";
	private String vehiculo = "";
	private String formaEntrega = Venta.FORMA_ENTREGA_EMPAQUE;
	
	private Empresa selectedEmpresa;
	private Funcionario selectedVendedor;
	private Deposito selectedDeposito;
	private CondicionPago selectedCondicion;
	private Articulo selectedArticulo;
	private ArticuloListaPrecio selectedPrecio;
	private String tipo;
	
	private List<VentaDetalle> detalles = new ArrayList<VentaDetalle>();
	private VentaDetalle selectedDetalle;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void selectTipo(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("tipo") String tipo) {
		this.tipo = tipo;
		comp1.setVisible(false);
		comp2.setVisible(true);
		comp3.setVisible(true);
	}
	
	@Command
	@NotifyChange("*")
	public void crearDetalle(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) throws Exception {
		if (this.tipo.equals("PEDIDO") && this.selectedArticulo.getStock() <= 0) {
			Clients.showNotification("STOCK INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		for (VentaDetalle item : this.detalles) {
			if (item.getArticulo().getId().longValue() == this.selectedArticulo.getId().longValue()) {
				Clients.showNotification("ITEM DUPLICADO..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
				return;
			}
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedDetalle = new VentaDetalle();
		this.selectedDetalle.setArticulo(this.selectedArticulo);
		this.selectedDetalle.setTipoIVA(rr.getTipoPorSigla(Configuracion.SIGLA_IVA_10));
		comp1.setVisible(false);
		comp2.setVisible(true);
		comp3.setVisible(true);
		comp4.setVisible(false);
	}
	
	@Command
	@NotifyChange("*")
	public void addDetalle(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) {
		this.detalles.add(this.selectedDetalle);
		this.selectedArticulo = null;
		this.selectedPrecio = null;
		this.codigoInterno = "";
		comp1.setVisible(false);
		comp2.setVisible(true);
		comp3.setVisible(true);
		comp4.setVisible(false);
	}
	
	@Command
	@NotifyChange("selectedDetalle")
	public void selectPrecio() throws Exception {
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			this.setPrecioVentaBaterias();
		} else {
			this.setPrecioVenta();
		}		
	}
	
	@Command
	@NotifyChange("*")
	public void generarRemision(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) throws Exception {
		if (!this.validarStock()) {
			Clients.showNotification("STOCK INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		if (!this.validarLineaCredito()) {
			Clients.showNotification("LINEA DE CREDITO INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		this.generarPedido(true);
		
		comp1.setVisible(false);
		comp2.setVisible(true);
		comp3.setVisible(true);
		comp4.setVisible(false);
		Clients.showNotification("PEDIDO GENERADO: " + this.numero);
		
		EventQueues.lookup(Configuracion.EVENTO_NUEVO_PEDIDO,
				EventQueues.APPLICATION, true).publish(
				new Event(Configuracion.ON_NUEVO_PEDIDO, null, null));
	}
	
	@Command
	@NotifyChange("*")
	public void generarPedidoVenta(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) throws Exception {
		if (!this.validarStock()) {
			Clients.showNotification("STOCK INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		if (!this.validarLineaCredito()) {
			Clients.showNotification("LINEA DE CREDITO INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		this.generarPedido(false);
		
		comp1.setVisible(false);
		comp2.setVisible(true);
		comp3.setVisible(true);
		comp4.setVisible(false);
		Clients.showNotification("PEDIDO GENERADO: " + this.numero);
		
		EventQueues.lookup(Configuracion.EVENTO_NUEVO_PEDIDO,
				EventQueues.APPLICATION, true).publish(
				new Event(Configuracion.ON_NUEVO_PEDIDO, null, null));
	}
	
	/**
	 * genera el pedido de venta..
	 */
	private void generarPedido(boolean remision) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Venta venta = new Venta();
		venta.setAtendido(this.selectedVendedor);
		venta.setVendedor(this.selectedVendedor);
		venta.setCedulaRepartidor("");
		venta.setChapaVehiculo("");
		venta.setCliente(rr.getClienteByEmpresa(this.selectedEmpresa.getId()));
		venta.setCondicionPago(this.selectedCondicion);
		venta.setCuotas(0);
		venta.setDenominacion(venta.getCliente().getRazonSocial());
		venta.setDeposito(this.selectedDeposito);
		venta.getDetalles().addAll(this.detalles);
		String estado = this.tipo.equals("PEDIDO") ? Configuracion.SIGLA_VENTA_ESTADO_SOLO_PEDIDO : Configuracion.SIGLA_VENTA_ESTADO_SOLO_PRESUPUESTO;
		venta.setEstado(rr.getTipoPorSigla(estado));
		venta.setFecha(new Date());
		venta.setFechaFinTraslado("");
		venta.setFechaTraslado("");
		venta.setMarcaVehiculo("");
		venta.setModoVenta(rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_VENTA_MOSTRADOR));
		venta.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		String key = this.tipo.equals("PEDIDO") ? Configuracion.NRO_VENTA_PEDIDO : Configuracion.NRO_VENTA_PRESUPUESTO;
		venta.setNumero(key + "-" + AutoNumeroControl.getAutoNumero(key, 7));
		venta.setNumeroFactura("");
		venta.setNumeroNotaCredito("");
		venta.setNumeroPedido("");
		venta.setNumeroPlanillaCaja("");
		venta.setNumeroPresupuesto("");
		venta.setNumeroReciboCobro("");
		venta.setObservacion(this.observacion.isEmpty()? "sin obs.." : this.observacion);
		venta.setPreparadoPor("");
		venta.setPuntoPartida("");
		venta.setRepartidor("");
		venta.setReparto(false);
		venta.setSucursal(rr.getSucursalAppById(2));
		venta.setTipoCambio(0);
		venta.setTipoMovimiento(this.getTipoMovimiento());
		venta.setTotalImporteGs(venta.getImporteGs());
		venta.setValidez(0);
		venta.setVencimiento(this.selectedCondicion.getId().longValue() > 1? Utiles.agregarDias(new Date(), 30) : new Date());
		venta.setFormaEntrega(this.getFormaEntrega());
		rr.saveObject(venta, "mobile");
		this.numero = venta.getNumero();
		
		if (remision) {
			String nro = this.numero.replace("V-PED-", "001-001-");
			Remision rem = new Remision();
			rem.setFecha(new Date());
			rem.setImporteGs(venta.getTotalImporteGs_());
			rem.setNumero(nro);
			rem.setObservacion(observacion);
			rem.setVenta(venta);
			rem.setVehiculo(this.vehiculo);
			for (VentaDetalle item : venta.getDetalles()) {
				RemisionDetalle det = new RemisionDetalle();
				det.setArticulo(item.getArticulo());
				det.setCantidad(Integer.parseInt(item.getCantidad() + ""));
				rem.getDetalles().add(det);
			}
			rr.saveObject(rem, "mobile");
		}
	}
	
	/**
	 * set precio de venta..
	 */
	private void setPrecioVenta() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Cliente cli = rr.getClienteByEmpresa(this.selectedEmpresa.getId());
		Object[] art = rr.getArticulo_(this.selectedDetalle.getArticulo().getId());
		double precio = 0;
		long idListaPrecio = this.selectedDetalle.getListaPrecio().getId();
		if (idListaPrecio == ArticuloListaPrecio.ID_LISTA) precio = (double) art[2];
		if (idListaPrecio == ArticuloListaPrecio.ID_MINORISTA) precio = (double) art[3];
		if (idListaPrecio == ArticuloListaPrecio.ID_MAYORISTA_GS) precio = (double) art[4];
		if (idListaPrecio == ArticuloListaPrecio.ID_MAYORISTA_DS) precio = (double) art[5];
		if (idListaPrecio == ArticuloListaPrecio.ID_TRANSPORTADORA) precio = (double) art[7];
		if (idListaPrecio == ArticuloListaPrecio.ID_PROMOCION) precio = (double) art[9];
		if (this.selectedDetalle.isExenta()) {
			precio = precio - Utiles.getIVA(precio, 10);
		}
		this.selectedDetalle.setPrecioGs(precio);
		double dto_mayorista = cli.getDescuentoMayorista();
		double porcentajeDescuento = (double) art[6];
		this.selectedDetalle.setDescuentoUnitarioGs(Utiles.obtenerValorDelPorcentaje((this.selectedDetalle.getPrecioGs() * this.selectedDetalle.getCantidad()), porcentajeDescuento));
		if (idListaPrecio == ArticuloListaPrecio.ID_MAYORISTA_GS) {
			double descuento = porcentajeDescuento > dto_mayorista ? porcentajeDescuento : dto_mayorista;
			this.selectedDetalle.setDescuentoUnitarioGs(Utiles.obtenerValorDelPorcentaje((this.selectedDetalle.getPrecioGs() * this.selectedDetalle.getCantidad()), descuento));
		}
	}
	
	/**
	 * setea el precio de venta para la empresa baterias..
	 */
	private void setPrecioVentaBaterias() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long idListaPrecio = this.selectedDetalle.getListaPrecio().getId();
		String codArticulo = this.selectedDetalle.getArticulo().getCodigoInterno();		
		ArticuloListaPrecioDetalle lista = rr.getListaPrecioDetalle(idListaPrecio, codArticulo);
		String formula = this.selectedDetalle.getListaPrecio().getFormula();
		if (lista != null && formula == null) {
			this.selectedDetalle.setPrecioGs(this.selectedCondicion.isCondicionContado()? lista.getPrecioGs_contado() : lista.getPrecioGs_credito());
		} else {
			double costo = this.selectedDetalle.getArticulo().getCostoGs();
			int margen = this.selectedDetalle.getListaPrecio().getMargen();
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
					this.selectedDetalle.setPrecioGs(this.selectedCondicion.isCondicionContado()? formulaCont : formulaCred);
				} else {
					margen = distribuidor.getMargen();
					double precioGs = ControlArticuloCosto.getPrecioVenta(costo, margen);
					double formula_ = precioGs + Utiles.obtenerValorDelPorcentaje(precioGs, 10);
					this.selectedDetalle.setPrecioGs(formula_);
				}

			// formula lista precio minorista..
			} else if (idListaPrecio == 3 && formula != null) {
				ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
				ArticuloListaPrecioDetalle precioDet = rr.getListaPrecioDetalle(distribuidor.getId(), codArticulo);
				if (precioDet != null) {
					double cont = precioDet.getPrecioGs_contado() + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_contado(), 10);
					double cred = precioDet.getPrecioGs_credito() + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_credito(), 10);
					double formulaCont = (cont * 1.15) / 0.8;
					double formulaCred = (cred * 1.15) / 0.8;
					this.selectedDetalle.setPrecioGs(this.selectedCondicion.isCondicionContado()? formulaCont : formulaCred);
				} else {
					margen = distribuidor.getMargen();
					double precioGs = ControlArticuloCosto.getPrecioVenta(costo, margen);
					double formula_ = ((precioGs + Utiles.obtenerValorDelPorcentaje(precioGs, 10)) * 1.15) / 0.8;
					this.selectedDetalle.setPrecioGs(formula_);
				}

			} else {
				this.selectedDetalle.setPrecioGs(precio);
			}		
		}
	}
	
	/**
	 * @return true si hay disponibilidad..
	 */
	private boolean validarStock() throws Exception {
		boolean out = true;
		RegisterDomain rr = RegisterDomain.getInstance();
		for (VentaDetalle item : this.detalles) {
			long stock = rr.getStockDisponible(item.getArticulo().getId(), this.selectedDeposito.getId());
			if (stock <= 0) {
				out = false;
			}
			if (item.getCantidad() <= 0) {
				out = false;
			}
			if (item.getCantidad() > stock) {
				out = false;
			}
		}
		return out;
	}
	
	/**
	 * 
	 * @return true si la linea de credito es valida..
	 */
	private boolean validarLineaCredito() throws Exception {
		boolean out = true;
		double importeVenta = this.getTotalImporteGs();
		double disponible = this.getCreditoDisponible();
		if ((!this.selectedCondicion.isCondicionContado()) && (importeVenta > disponible)) {
			out = false;
		}
		return out;
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn("razonSocial")
	public List<Empresa> getEmpresas() throws Exception {
		if (this.razonSocial.isEmpty()) {
			return new ArrayList<Empresa>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresas("", "", this.razonSocial, "");
	}
	
	/**
	 * @return los vendedores..
	 */
	public List<Funcionario> getVendedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getVendedores();
	}
	
	@DependsOn("codigoInterno")
	public List<Articulo> getArticulos() throws Exception {
		if (this.codigoInterno.isEmpty()) {
			return new ArrayList<Articulo>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Articulo> out = rr.getArticulos_(this.codigoInterno, 50);
		for (Articulo art : out) {
			art.setStock(rr.getStockDisponible(art.getId(), this.selectedDeposito.getId()));
		}
		return out;
	}
	
	/**
	 * @return los depositos..
	 */
	public List<Deposito> getDepositos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getDepositos();
	}
	
	/**
	 * @return los depositos de remision..
	 */
	public List<Deposito> getDepositosRemision() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Deposito> out = new ArrayList<Deposito>();
		List<Deposito> list = rr.getDepositosPorSucursal((long) 2);
		for (Deposito dep : list) {
			if (dep.getDescripcion().contains("MOVIL")) {
				out.add(dep);
			}
		}
		return out;
	}
	
	/**
	 * @return los vehiculos..
	 */
	public List<String> getVehiculos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<String> out = new ArrayList<String>();
		for (Vehiculo vehiculo : rr.getVehiculosSucursal(2)) {
			out.add(vehiculo.getDescripcion().toUpperCase());
		}
		return out;
	}
	
	/**
	 * @return las condiciones..
	 */
	@DependsOn("selectedEmpresa")
	public List<CondicionPago> getCondiciones() throws Exception {
		List<CondicionPago> out = new ArrayList<CondicionPago>();
		RegisterDomain rr = RegisterDomain.getInstance();
		CondicionPago contado = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CONTADO);
		CondicionPago credito = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_30);
		out.add(contado); 
		// verifica si el cliente esta habilitado para credito..
		if (this.selectedEmpresa != null && !this.selectedEmpresa.isCuentaBloqueada()) {
			Cliente cli = rr.getClienteByEmpresa(this.selectedEmpresa.getId());
			if (cli.isVentaCredito()) {
				out.add(credito);
			}
		}
		return out;
	}
	
	/**
	 * @return la lista de precio definida al cliente..
	 */
	@DependsOn("selectedEmpresa")
	public List<ArticuloListaPrecio> getListaPrecio() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getListasDePrecio();
	}
	
	/**
	 * @return las formas de entrega..
	 */
	public List<String> getFormasEntrega() {
		return Venta.getFormasEntrega();
	}
	
	/**
	 * @return el importe en gs.
	 */
	private double getTotalImporteGs() {
		double out = 0;
		for (VentaDetalle item : this.detalles) {
			out += item.getImporteGs();
		}
		return out;
	}
	
	/**
	 * @return el credito disponible..
	 */
	public double getCreditoDisponible() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Cliente cli = rr.getClienteByEmpresa(this.selectedEmpresa.getId());
		double saldo = rr.getSaldoCtaCte(this.selectedEmpresa.getId());
		return (cli.getLimiteCredito() + Utiles.obtenerValorDelPorcentaje(cli.getLimiteCredito(), Venta.MARGEN_LINEA_CREDITO)) - saldo;
	}
	
	/**
	 * @return el tipo de movimiento
	 */
	private TipoMovimiento getTipoMovimiento() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipoMovimientoBySigla(this.tipo.equals("PEDIDO") ? Configuracion.SIGLA_TM_PEDIDO_VENTA : Configuracion.SIGLA_TM_PRESUPUESTO_VENTA);
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public Empresa getSelectedEmpresa() {
		return selectedEmpresa;
	}

	public void setSelectedEmpresa(Empresa selectedEmpresa) {
		this.selectedEmpresa = selectedEmpresa;
	}

	public String getRazonSocialVendedor() {
		return razonSocialVendedor;
	}

	public void setRazonSocialVendedor(String razonSocialVendedor) {
		this.razonSocialVendedor = razonSocialVendedor;
	}

	public Funcionario getSelectedVendedor() {
		return selectedVendedor;
	}

	public void setSelectedVendedor(Funcionario selectedVendedor) {
		this.selectedVendedor = selectedVendedor;
	}

	public Deposito getSelectedDeposito() {
		return selectedDeposito;
	}

	public void setSelectedDeposito(Deposito selectedDeposito) {
		this.selectedDeposito = selectedDeposito;
	}

	public CondicionPago getSelectedCondicion() {
		return selectedCondicion;
	}

	public void setSelectedCondicion(CondicionPago selectedCondicion) {
		this.selectedCondicion = selectedCondicion;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public Articulo getSelectedArticulo() {
		return selectedArticulo;
	}

	public void setSelectedArticulo(Articulo selectedArticulo) {
		this.selectedArticulo = selectedArticulo;
	}

	public List<VentaDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<VentaDetalle> detalles) {
		this.detalles = detalles;
	}

	public VentaDetalle getSelectedDetalle() {
		return selectedDetalle;
	}

	public void setSelectedDetalle(VentaDetalle selectedDetalle) {
		this.selectedDetalle = selectedDetalle;
	}

	public ArticuloListaPrecio getSelectedPrecio() {
		return selectedPrecio;
	}

	public void setSelectedPrecio(ArticuloListaPrecio selectedPrecio) {
		this.selectedPrecio = selectedPrecio;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(String vehiculo) {
		this.vehiculo = vehiculo;
	}

	public String getFormaEntrega() {
		return formaEntrega;
	}

	public void setFormaEntrega(String formaEntrega) {
		this.formaEntrega = formaEntrega;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
