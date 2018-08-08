package com.yhaguy.gestion.venta.preparacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.Utiles;

public class PreparacionPedidosMobileVM extends SimpleViewModel {

	private String filterNumero = "";
	private String selectedPreparador;
	private Venta selectedVenta;
	
	@Init
	public void init() {
	}
	
	@AfterCompose
	public void AfterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void deleteItem(@BindingParam("venta") Venta venta, @BindingParam("item") VentaDetalle item) 
		throws Exception {
		venta.getDetalles().remove(item);
		Venta generado = new Venta();
		generado.getDetalles().add(item);
		this.trasladarVenta(venta, generado);
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(venta, this.getLoginNombre());
		rr.saveObject(generado, this.getLoginNombre());
		BindUtils.postNotifyChange(null, null, venta, "*");
	}
	
	@Command
	@NotifyChange("*")
	public void aprobarPedido(@BindingParam("venta") Venta venta,
			@BindingParam("comp1") Component comp1,
			@BindingParam("comp2") Component comp2,
			@BindingParam("comp3") Component comp3,
			@BindingParam("comp4") Component comp4) throws Exception {
		if (venta.getDetalles().size() == 0) {
			return;
		}
		if (!this.validarStock()) {
			Clients.showNotification("STOCK INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		venta.setEstado(rr.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_CERRADO));
		venta.setFecha(new Date());
		rr.saveObject(venta, this.getLoginNombre());
		this.mensajePopupTemporal("PEDIDO CORRECTAMENTE APROBADO..");
		comp1.setVisible(true);
		comp2.setVisible(false);
		comp3.setVisible(true);
		comp4.setVisible(false);
		this.selectedVenta = null;
		
		EventQueues.lookup(Configuracion.EVENTO_NUEVO_PEDIDO,
				EventQueues.APPLICATION, true).publish(
				new Event(Configuracion.ON_NUEVO_PEDIDO, null, null));
	}
	
	/**
	 * @return true si hay stock suficiente..
	 */
	private boolean validarStock() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (VentaDetalle item : this.selectedVenta.getDetalles()) {
			long stock = rr.getStockDisponible(item.getArticulo().getId(), this.selectedVenta.getDeposito().getId());
			if (stock <= 0) {
				return false;
			}
			if (item.getCantidad() > stock) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * copia los datos de cabecera..
	 */
	private void trasladarVenta(Venta desde, Venta hasta) throws Exception {
		hasta.setAtendido(desde.getAtendido());
		hasta.setCliente(desde.getCliente());
		hasta.setCondicionPago(desde.getCondicionPago());
		hasta.setCuotas(desde.getCuotas());
		hasta.setDenominacion(desde.getDenominacion());
		hasta.setDeposito(desde.getDeposito());
		hasta.setDescripcionCondicion(desde.getDescripcionCondicion());
		hasta.setDescripcionTipoMovimiento(desde.getDescripcionTipoMovimiento());
		hasta.setEstado(desde.getEstado());
		hasta.setFecha(new Date());
		hasta.setMoneda(desde.getMoneda());
		hasta.setNumero("V-PEG-" + AutoNumeroControl.getAutoNumero("V-PEG", 7));
		hasta.setObservacion(desde.getObservacion());
		hasta.setSucursal(desde.getSucursal());
		hasta.setTipoMovimiento(desde.getTipoMovimiento());
		hasta.setTotalImporteDs(desde.getTotalImporteDs());
		hasta.setTipoCambio(desde.getTipoCambio());
		hasta.setTotalImporteGs(desde.getTotalImporteGs());
		hasta.setVencimiento(desde.getVencimiento());
		hasta.setVendedor(desde.getVendedor());
		hasta.setCedulaRepartidor(desde.getCedulaRepartidor());
		hasta.setChapaVehiculo(desde.getChapaVehiculo());
		hasta.setMarcaVehiculo(desde.getMarcaVehiculo());
		hasta.setModoVenta(desde.getModoVenta());
		hasta.setPuntoPartida(desde.getPuntoPartida());
		hasta.setReparto(desde.isReparto());
		hasta.setNumeroFactura(desde.getNumeroFactura());
		hasta.setNumeroNotaCredito(desde.getNumeroNotaCredito());
		hasta.setNumeroPedido(desde.getNumeroPedido());
		hasta.setNumeroPlanillaCaja(desde.getNumeroPlanillaCaja());
		hasta.setNumeroPresupuesto(desde.getNumeroPresupuesto());
		hasta.setNumeroReciboCobro(desde.getNumeroReciboCobro());
		hasta.setFechaFinTraslado(desde.getFechaFinTraslado());
		hasta.setFechaTraslado(desde.getFechaTraslado());
		hasta.setRepartidor(desde.getRepartidor());
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return los pedidos pendientes de facturacion..
	 */
	@DependsOn("selectedPreparador")
	public List<Venta> getPedidosPendientes() throws Exception {
		Date desde = Utiles.getFecha(Utiles.getDateToString(Utiles.agregarDias(new Date(), -7), Utiles.DD_MM_YYYY + " 00:00:00"));
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Venta> out = new ArrayList<Venta>();
		for (Venta venta : rr.getPedidosPendientesAprobacion(desde, new Date())) {
			if (venta.getPreparadoPor().equals(this.selectedPreparador)) {
				out.add(venta);
			}
		}
		return out;
	}
	
	/**
	 * @return los preparadores de pedido..
	 */
	public List<String> getPreparadores() throws Exception {
		List<String> out = new ArrayList<String>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Funcionario func : rr.getFuncionariosDeposito()) {
			out.add(func.getRazonSocial().toUpperCase());
		}
		return out;
	}
	
	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public Venta getSelectedVenta() {
		return selectedVenta;
	}

	public void setSelectedVenta(Venta selectedVenta) {
		this.selectedVenta = selectedVenta;
	}

	public String getSelectedPreparador() {
		return selectedPreparador;
	}

	public void setSelectedPreparador(String selectedPreparador) {
		this.selectedPreparador = selectedPreparador;
	}
}
