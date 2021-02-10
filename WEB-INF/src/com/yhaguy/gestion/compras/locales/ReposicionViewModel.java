package com.yhaguy.gestion.compras.locales;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloReposicion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;

public class ReposicionViewModel extends SimpleViewModel {

	private ArticuloReposicion reposicion;
	private String codigoArticulo = "";
	
	@Init
	public void init() {
		this.reposicion = new ArticuloReposicion();
	}
	
	@Command
	@NotifyChange("*")
	public void addReposicion(@BindingParam("win") Window win) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.reposicion.setFecha(new Date());
		this.reposicion.setSolicitante(((String) this.getAcceso().getFuncionario().getPos1()).toUpperCase());
		rr.saveObject(this.reposicion, this.getLoginNombre());
		Clients.showNotification("SOLICITUD REALIZADA");
		win.detach();
	}

	/**
	 * GETS AND SETS
	 */
	
	@DependsOn("codigoArticulo")
	public List<Articulo> getArticulos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulos(this.codigoArticulo, 30);
	}
	
	@DependsOn({ "reposicion.articulo", "reposicion.cantidad", "reposicion.observacion" })
	public boolean isAgregarDisabled() {
		return this.reposicion.getArticulo() == null || this.reposicion.getCantidad() <= 0
				|| this.reposicion.getObservacion() == null
				|| this.reposicion.getObservacion().trim().isEmpty();
	}
	
	/**
	 * @return el acceso..
	 */
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}
	
	public ArticuloReposicion getReposicion() {
		return reposicion;
	}

	public void setReposicion(ArticuloReposicion reposicion) {
		this.reposicion = reposicion;
	}

	public String getCodigoArticulo() {
		return codigoArticulo;
	}

	public void setCodigoArticulo(String codigoArticulo) {
		this.codigoArticulo = codigoArticulo;
	}
}
