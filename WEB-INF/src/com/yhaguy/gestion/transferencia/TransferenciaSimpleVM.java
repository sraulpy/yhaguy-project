package com.yhaguy.gestion.transferencia;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ReservaDTO;
import com.yhaguy.gestion.comun.ReservaDetalleDTO;

public class TransferenciaSimpleVM extends SoloViewModel {

	private TransferenciaControlBody dato;

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) TransferenciaControlBody dato) {
		this.dato = dato;
		this.sugerirCantidades();
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}
	
	/**
	 * busqueda del articulo..
	 */
	@Command
	public void buscarArticulo() throws Exception {
		MyPair deposito = this.dato.getDto().getDepositoSalida();

		String[] att = new String[] { "articulo.codigoInterno",
				"articulo.descripcion", "stock", "articulo.id" };
		String[] cols = new String[] { "Código", "Descripción", "Disponible",
				"" };
		String[] widths = new String[] { "150px", "", "100px", "0px" };

		BuscarElemento be = new BuscarElemento();
		be.setClase(ArticuloDeposito.class);
		be.setAtributos(att);
		be.setNombresColumnas(cols);
		be.setWidth("700px");
		be.setAnchoColumnas(widths);
		be.setTitulo("Artículos del Depósito: " + deposito.getText());
		be.addWhere("c.deposito.id = " + deposito.getId());
		be.show((String) this.dato.getNvoDetalle().getArticulo().getPos1());

		if (be.isClickAceptar()) {
			MyArray selected = be.getSelectedItem();
			MyArray articulo = new MyArray();
			articulo.setId((long) selected.getPos4());
			articulo.setPos1(selected.getPos1());
			articulo.setPos2(selected.getPos2());
			articulo.setPos3(selected.getPos3());
			this.dato.getNvoDetalle().setArticulo(articulo);
			this.setCosto(this.dato.getNvoDetalle());
		}
		BindUtils.postNotifyChange(null, null, this.dato, "nvoDetalle");
	}
	
	/**
	 * Validacion de cantidad..
	 */
	@Command
	public void validarCantidad(@BindingParam("comp") Component comp) {
		TransferenciaDetalleDTO item = this.dato.getNvoDetalle();
		int cantidad = item.getCantidad();
		long disponible = item.getStockDisponible();
		long reservado = this.getCantidadReservada(item.getId().longValue());
		long actualizado = cantidad - reservado;
		
		if ((cantidad <= reservado) || (actualizado <= disponible)) 
			return;
		
		if (cantidad > disponible) {
			Clients.showNotification("Stock insuficiente..",
					Clients.NOTIFICATION_TYPE_ERROR, comp, null, 1500);
			this.dato.getNvoDetalle().setCantidad(0);
		}
		BindUtils.postNotifyChange(null, null, this.dato.getNvoDetalle(),
				"cantidad");
	}
	
	/**
	 * @return la cantidad reservada del item..
	 */
	private long getCantidadReservada(long idItem) {
		ReservaDTO reserva = this.dato.getDto().getReserva();
		
		if (reserva != null) 		
		for (ReservaDetalleDTO item : this.dato.getDto().getReserva().getDetalles()) {
			long idOrigen = item.getIdDetalleOrigen();
			if (idOrigen == idItem) {
				return item.getCantidadReservada();
			}
		};
		return 0;
	}
	
	/**
	 * sugiere las cantidades recibidas..
	 */
	private void sugerirCantidades() {
		for (TransferenciaDetalleDTO item : this.dato.getDto().getDetalles()) {
			item.setCantidadRecibida(item.getCantidad());
		}
	}
	
	/**
	 * setea el costo al item de la transferencia..
	 */
	private void setCosto(TransferenciaDetalleDTO item) throws Exception {
		long idDeposito = this.dato.getDto().getDepositoSalida().getId().longValue();
		long idArticulo = item.getArticulo().getId().longValue();
		double costo = this.getCosto(idDeposito, idArticulo);
		item.setCosto(costo);
	}

	/**
	 * GET - SET
	 */
	private double getCosto(long idDeposito, long idArticulo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloById(idArticulo);
		return art.getCostoGs();
	}
	
	public TransferenciaControlBody getDato() {
		return dato;
	}

	public void setDato(TransferenciaControlBody dato) {
		this.dato = dato;
	}

}
