package com.yhaguy.gestion.venta.preparacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;
import com.yhaguy.util.Utiles;

public class PreparacionPedidosVM extends SimpleViewModel {

	private List<Venta> pedidos1 = new ArrayList<Venta>();
	private List<Venta> pedidos2 = new ArrayList<Venta>();
	private List<Venta> pedidos3 = new ArrayList<Venta>();
	private List<Venta> pedidos4 = new ArrayList<Venta>();
	
	private List<Venta> pedidosPend = new ArrayList<Venta>();
	
	private boolean editando = false;
	private boolean mostrarPendientes = false;
	private int enCola = 0;
	
	private int sizeNuevosPedidos = 0;
	
	@Wire
	private Window win;
	
	@SuppressWarnings("unchecked")
	@Init(superclass = true)
	public void init() {
		EventQueues.lookup(Configuracion.EVENTO_NUEVO_PEDIDO,
				EventQueues.APPLICATION, true).subscribe(nuevoPedido);
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void distribuirPedidos_() throws Exception {
		this.sizeNuevosPedidos = 0;
		Clients.showBusy(this.win, "Buscando pedidos pendientes...");
		Events.echoEvent("onLater", this.win, null);
	}
	
	@Command
	@NotifyChange("*")
	public void mostrarPedidosPendientes() throws Exception {
		this.mostrarPendientes = true;
		this.distribuirPedidosPendientes();
	}
	
	@Command
	@NotifyChange("*")
	public void mostrarPedidosEnCola() throws Exception {
		this.mostrarPendientes = false;
		this.distribuirPedidos();
	}
	
	@Command
	@NotifyChange("*")
	public void aprobarPedido(@BindingParam("venta") Venta venta) throws Exception {
		if (venta.getDetalles().size() == 0) {
			return;
		}
		if (!this.validarStock(venta)) {
			Clients.showNotification("STOCK INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		venta.setEstado(rr.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_CERRADO));
		venta.setFecha(new Date());
		rr.saveObject(venta, this.getLoginNombre());
		this.distribuirPedidos();
		this.editando = false;
		this.mensajePopupTemporal("PEDIDO CORRECTAMENTE APROBADO..");
	}
	
	@Command
	@NotifyChange("*")
	public void pedidoPendiente(@BindingParam("venta") Venta venta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		venta.setAuxi("PENDIENTE");
		rr.saveObject(venta, this.getLoginNombre());
		this.distribuirPedidos();
		this.editando = false;
		this.mensajePopupTemporal("AGREGADO A PEDIDOS PENDIENTES..");
	}
	
	@Command
	@NotifyChange("*")
	public void pedidoEnCola(@BindingParam("venta") Venta venta) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		venta.setAuxi("");
		rr.saveObject(venta, this.getLoginNombre());
		this.getPedidosPend().remove(venta);
		this.distribuirPedidosPendientes();
		this.editando = false;
		this.mensajePopupTemporal("AGREGADO A PEDIDOS EN COLA..");
	}
	
	@Command
	@NotifyChange("*")
	public void deleteVenta(@BindingParam("venta") Venta venta) 
		throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.deleteObject(venta);
		this.distribuirPedidos();
		this.editando = false;
		this.mensajePopupTemporal("PEDIDO ELIMINADO..");
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
		this.editando(venta);
		BindUtils.postNotifyChange(null, null, venta, "*");
	}
	
	@Command
	public void editando(@BindingParam("venta") Venta venta) {
		this.editando = true;
		venta.setAuxi("PREPARANDO");
		BindUtils.postNotifyChange(null, null, venta, "auxi");
	}
	
	@Command
	public void setPreparador(@BindingParam("venta") Venta venta) throws Exception {
		venta.setAuxi("PREPARANDO");
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(venta, this.getLoginNombre());
		BindUtils.postNotifyChange(null, null, venta, "auxi");
	}
	
	@Command
	public void verificarNuevosPedidos() throws Exception {
		int sizeView = 0;
		sizeView += this.pedidos1.size();
		sizeView += this.pedidos2.size();
		sizeView += this.pedidos3.size();	
		sizeView += this.pedidos4.size();
		if (!this.editando) {
			if (this.mostrarPendientes) {
				this.distribuirPedidosPendientes();
			} else {
				this.distribuirPedidos();
			}
			BindUtils.postNotifyChange(null, null, this, "*");
		}
		this.enCola = sizeView;
		BindUtils.postNotifyChange(null, null, this, "enCola");
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	@NotifyChange("*")
	public void clearProgress() throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(win);
			}
		});
		timer.setParent(this.mainComponent);		
		this.distribuirPedidos();
	}
	
	/**
	 * @return los pedidos pendientes de facturacion..
	 */
	public List<Venta> getPedidosPendientes() throws Exception {
		Date desde = Utiles.getFecha(Utiles.getDateToString(Utiles.agregarDias(new Date(), -7), 
				Utiles.DD_MM_YYYY + " 00:00:00"));
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getPedidosPendientesAprobacion(desde, new Date());
	}
	
	/**
	 * distribuir pedidos en 4 listas..
	 */
	public void distribuirPedidos() throws Exception {
		this.pedidos1 = new ArrayList<Venta>();
		this.pedidos2 = new ArrayList<Venta>();
		this.pedidos3 = new ArrayList<Venta>();
		this.pedidos4 = new ArrayList<Venta>();
		this.pedidosPend = new ArrayList<Venta>();
		int index = 1;		
		for (Venta venta : this.getPedidosPendientes()) {
			if (!venta.getAuxi().equals("PENDIENTE")) {
				if (index == 1) {
					this.pedidos1.add(venta);
					index++;
				} else {
					if (index == 2) {
						this.pedidos2.add(venta);
						index++;
					} else {
						if (index == 3) {
							this.pedidos3.add(venta);
							index ++;
						} else {
							if (index == 4) {
								this.pedidos4.add(venta);
								index = 1;
							}
						}
					} 
				}
			} else {
				this.pedidosPend.add(venta);
			}
		}
	}

	/**
	 * distribuir pedidos en 4 listas..
	 */
	public void distribuirPedidosPendientes() throws Exception {
		this.pedidos1 = new ArrayList<Venta>();
		this.pedidos2 = new ArrayList<Venta>();
		this.pedidos3 = new ArrayList<Venta>();
		this.pedidos4 = new ArrayList<Venta>();
		int index = 1;		
		for (Venta venta : this.getPedidosPend()) {
			if (index == 1) {
				this.pedidos1.add(venta);
				index++;
			} else {
				if (index == 2) {
					this.pedidos2.add(venta);
					index++;
				} else {
					if (index == 3) {
						this.pedidos3.add(venta);
						index ++;
					} else {
						if (index == 4) {
							this.pedidos4.add(venta);
							index = 1;
						}
					}
				}
			}		
		}
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
	 * @return true si hay disponibilidad..
	 */
	private boolean validarStock(Venta venta) throws Exception {
		boolean out = true;
		RegisterDomain rr = RegisterDomain.getInstance();
		for (VentaDetalle item : venta.getDetalles()) {
			long stock = rr.getStockDisponible(item.getArticulo().getId(), venta.getDeposito().getId());
			if (stock <= 0) {
				out = false;
			}
		}
		return out;
	}
	
	
	/**
	 * Notificacion de nuevos Pedidos..
	 */
	@SuppressWarnings("rawtypes")
	private EventListener nuevoPedido = new EventListener() {
		@Override
		public void onEvent(Event event) throws Exception {
			sizeNuevosPedidos ++;
			BindUtils.postNotifyChange(null, null, PreparacionPedidosVM.this, "sizeNuevosPedidos");
		}
	};
	
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return true si la operacion esta habilitada..
	 */
	public boolean isOperacionHabilitada(String operacion) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.isOperacionHabilitada(this.getLoginNombre(), operacion);
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

	/**
	 * @return el acceso..
	 */
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		AccesoDTO out = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		if (out == null) {
			try {
				AssemblerAcceso as = new AssemblerAcceso();
				out = (AccesoDTO) as.obtenerAccesoDTO(Configuracion.USER_MOBILE);
				s.setAttribute(Configuracion.ACCESO, out);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}			
		return out;
	}

	public List<Venta> getPedidos1() {
		return pedidos1;
	}

	public void setPedidos1(List<Venta> pedidos1) {
		this.pedidos1 = pedidos1;
	}

	public List<Venta> getPedidos2() {
		return pedidos2;
	}

	public void setPedidos2(List<Venta> pedidos2) {
		this.pedidos2 = pedidos2;
	}

	public List<Venta> getPedidos3() {
		return pedidos3;
	}

	public void setPedidos3(List<Venta> pedidos3) {
		this.pedidos3 = pedidos3;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public int getEnCola() {
		return enCola;
	}

	public void setEnCola(int enCola) {
		this.enCola = enCola;
	}

	public List<Venta> getPedidosPend() {
		return pedidosPend;
	}

	public void setPedidosPend(List<Venta> pedidosPend) {
		this.pedidosPend = pedidosPend;
	}

	public List<Venta> getPedidos4() {
		return pedidos4;
	}

	public void setPedidos4(List<Venta> pedidos4) {
		this.pedidos4 = pedidos4;
	}

	public int getSizeNuevosPedidos() {
		return sizeNuevosPedidos;
	}

	public void setSizeNuevosPedidos(int sizeNuevosPedidos) {
		this.sizeNuevosPedidos = sizeNuevosPedidos;
	}
}
