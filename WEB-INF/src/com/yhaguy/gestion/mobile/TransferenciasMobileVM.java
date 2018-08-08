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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.TransferenciaDetalle;
import com.yhaguy.gestion.comun.ControlArticuloStock;

public class TransferenciasMobileVM extends SimpleViewModel {
	
	private static String KEY = Configuracion.NRO_TRANSFERENCIA_INTERNA;
	
	private String filterCodigo = "";
	private int cantidad = 0;
	private int disponible = 0;
	
	private Transferencia transferencia;
	private Articulo selectedArticulo;

	@Init(superclass = true)
	public void init() {
		try {
			this.inicializarTransferencia();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void confirmarTransferencia() throws Exception {		
		if (!this.validarDatos()) {
			return;
		}		
		RegisterDomain rr = RegisterDomain.getInstance();
		this.transferencia.getDetalles().add(this.getDetalle());
		this.transferencia.setNumero(KEY + "-" + AutoNumeroControl.getAutoNumero(KEY, 7));
		rr.saveObject(this.transferencia, "mobile");
		
		for (TransferenciaDetalle item : this.transferencia.getDetalles()) {
			this.actualizarStock(item);
		}
		
		this.selectedArticulo = null;
		this.cantidad = 0;
		this.disponible = 0;
		this.inicializarTransferencia();
		Clients.showNotification("TRANSFERENCIA ENVIADA..!");
	}
	
	@Command
	@NotifyChange({ "selectedArticulo", "disponible" })
	public void selectDepositoOrigen() {
		this.selectedArticulo = null;
		this.disponible = 0;
	}
	
	@Command
	@NotifyChange("disponible")
	public void selectArticulo(@BindingParam("comp") Bandbox comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.disponible = (int) rr.getStockDisponible(this.selectedArticulo.getId(), this.transferencia.getDepositoSalida().getId());
		comp.close();
	}
	
	/**
	 * inicializa la transferencia..
	 */
	private void inicializarTransferencia() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.transferencia = new Transferencia();
		this.transferencia.setFechaCreacion(new Date());
		this.transferencia.setSucursal(rr.getSucursalAppById(2));
		this.transferencia.setNumero(KEY + "-" + AutoNumeroControl.getAutoNumero(KEY, 7, true));
		this.transferencia.setTransferenciaTipo(rr.getTipoPorSigla(Configuracion.SIGLA_TM_TRANSF_INTERNA));
		this.transferencia.setTransferenciaEstado(rr.getTipoPorSigla(Configuracion.SIGLA_ESTADO_TRANSF_CONFIRMADA));
		this.transferencia.setAuxi("R");
		this.transferencia.setObservacion("DESDE APP MÓVIL..");
	}
	
	/**
	 * @return true si son datos validos..
	 */
	private boolean validarDatos() throws Exception {
		if (this.transferencia.getDepositoSalida() == null
				|| this.transferencia.getDepositoEntrada() == null
				|| this.selectedArticulo == null
				|| this.cantidad <= 0) {
			Clients.showNotification("DATOS NO VALIDOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return false;
		}
		if (this.disponible <= 0 || this.disponible < this.cantidad) {
			Clients.showNotification("STOCK INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return false;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		int disp = (int) rr.getStockDisponible(this.selectedArticulo.getId(), this.transferencia.getDepositoSalida().getId());
		if (disp < this.cantidad) {
			Clients.showNotification("STOCK INSUFICIENTE..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return false;
		}
		return true;
	}
	
	/**
	 * actualiza el stock..resta al deposito origen y suma al deposito destino..
	 */
	private void actualizarStock(TransferenciaDetalle item) throws Exception {
		
		ControlArticuloStock.actualizarStock(item.getArticulo().getId(),
				this.transferencia.getDepositoSalida().getId(),
				(item.getCantidad() * -1), this.getLoginNombre());

		ControlArticuloStock.actualizarStock(item.getArticulo().getId(),
				this.transferencia.getDepositoEntrada().getId(), item.getCantidad(),
				this.getLoginNombre());
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn("filterCodigo")
	public List<Articulo> getArticulos() throws Exception {
		if (this.filterCodigo.isEmpty()) {
			return new ArrayList<Articulo>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();		
		return rr.getArticulos(this.filterCodigo, 50);
	}
	
	/**
	 * @return los tipos de transferencia..
	 */
	public List<Tipo> getTiposTransferencia() throws Exception {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_TM_TRANSF_INTERNA));
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_TM_TRANSF_EXTERNA));
		return out;
	}
	
	/**
	 * @return los depositos..
	 */
	public List<Deposito> getDepositosOrigen() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Deposito> out = rr.getDepositosPorSucursal((long) 2);
		return out;
	}
	
	@DependsOn("transferencia.depositoSalida")
	public List<Deposito> getDepositosDestino() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Deposito> out = rr.getDepositosPorSucursal((long) 2);
		int index = -1;
		for (int i = 0; i < out.size(); i++) {
			if (this.transferencia.getDepositoSalida() != null) {
				if (out.get(i).getDescripcion().equals(this.transferencia.getDepositoSalida().getDescripcion())) {
					index = i;
				}
			}		
		}
		if (index >= 0) {
			out.remove(index);
		}
		return out;
	}
	
	/**
	 * @return el detalle de la transferencia..
	 */
	private TransferenciaDetalle getDetalle() {
		TransferenciaDetalle out = new TransferenciaDetalle();
		out.setCantidad(this.cantidad);
		out.setCantidadRecibida(new Integer(this.cantidad).longValue());
		out.setArticulo(this.selectedArticulo);
		out.setCosto(this.selectedArticulo.getCostoGs());
		return out;
	}
	
	public Articulo getSelectedArticulo() {
		return selectedArticulo;
	}

	public void setSelectedArticulo(Articulo selectedArticulo) {
		this.selectedArticulo = selectedArticulo;
	}

	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}

	public Transferencia getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(Transferencia transferencia) {
		this.transferencia = transferencia;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getDisponible() {
		return disponible;
	}

	public void setDisponible(int disponible) {
		this.disponible = disponible;
	}	
}
