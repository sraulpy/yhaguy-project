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
	
	private Empresa selectedEmpresa;
	private Funcionario selectedVendedor;
	private Deposito selectedDeposito;
	private CondicionPago selectedCondicion;
	private Articulo selectedArticulo;
	private ArticuloListaPrecio selectedPrecio;
	
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
	public void crearDetalle(@BindingParam("comp1") Component comp1, @BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3, @BindingParam("comp4") Component comp4) throws Exception {
		if (this.selectedArticulo.getStock() <= 0) {
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
		this.setPrecioVentaBaterias();
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
		venta.setEstado(rr.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_SOLO_PEDIDO));
		venta.setFecha(new Date());
		venta.setFechaFinTraslado("");
		venta.setFechaTraslado("");
		venta.setMarcaVehiculo("");
		venta.setModoVenta(rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_VENTA_MOSTRADOR));
		venta.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		venta.setNumero(Configuracion.NRO_VENTA_PEDIDO + "-" + 
				AutoNumeroControl.getAutoNumero(Configuracion.NRO_VENTA_PEDIDO, 7));
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
		venta.setReparto(true);
		venta.setSucursal(rr.getSucursalAppById(2));
		venta.setTipoCambio(0);
		venta.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PEDIDO_VENTA));
		venta.setTotalImporteGs(venta.getImporteGs());
		venta.setValidez(0);
		venta.setVencimiento(new Date());
		rr.saveObject(venta, "mobile");
		this.numero = venta.getNumero();
		comp1.setVisible(false);
		comp2.setVisible(true);
		comp3.setVisible(true);
		comp4.setVisible(false);
		Clients.showNotification("PEDIDO GENERADO: " + venta.getNumero());
		
		EventQueues.lookup(Configuracion.EVENTO_NUEVO_PEDIDO,
				EventQueues.APPLICATION, true).publish(
				new Event(Configuracion.ON_NUEVO_PEDIDO, null, null));
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
	
	@DependsOn("razonSocialVendedor")
	public List<Funcionario> getVendedores() throws Exception {
		if (this.razonSocialVendedor.isEmpty()) {
			return new ArrayList<Funcionario>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getFuncionarios(this.razonSocialVendedor);
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
		return rr.getDepositosPorSucursal((long) 2);
	}
	
	/**
	 * @return las condiciones..
	 */
	@DependsOn("selectedEmpresa")
	public List<CondicionPago> getCondiciones() throws Exception {
		List<CondicionPago> out = new ArrayList<CondicionPago>();
		RegisterDomain rr = RegisterDomain.getInstance();
		CondicionPago contado = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CONTADO);
		CondicionPago credito = rr.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_90);
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
		if (this.selectedEmpresa == null) {
			return new ArrayList<ArticuloListaPrecio>();
		}
		List<ArticuloListaPrecio> out = new ArrayList<ArticuloListaPrecio>();
		RegisterDomain rr = RegisterDomain.getInstance();
		Cliente cli = rr.getClienteByEmpresa(this.selectedEmpresa.getId());
		if (cli.getListaPrecio() != null) {
			out.add(cli.getListaPrecio());
		} else {
			ArticuloListaPrecio lp = rr.getListaDePrecio(2);
			if (lp != null) {
				out.add(lp);
			}
		}
		return out;
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
}
