package com.yhaguy.gestion.compras.definiciones;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.ArticuloAPI;
import com.yhaguy.domain.ArticuloAplicacion;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.ArticuloGrupo;
import com.yhaguy.domain.ArticuloIndicecarga;
import com.yhaguy.domain.ArticuloLado;
import com.yhaguy.domain.ArticuloMarca;
import com.yhaguy.domain.ArticuloModelo;
import com.yhaguy.domain.ArticuloPresentacion;
import com.yhaguy.domain.ArticuloSubGrupo;
import com.yhaguy.domain.RegisterDomain;

public class ComprasDefinicionesViewModel extends SimpleViewModel {
	
	private ArticuloFamilia selectedFamilia;
	private ArticuloFamilia nuevaFamilia = new ArticuloFamilia();
	
	private ArticuloMarca selectedMarca;
	private ArticuloMarca nuevaMarca = new ArticuloMarca();
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	
	@Command
	@NotifyChange({ "familias", "nuevaFamilia", "selectedFamilia" })
	public void addFamilia(@BindingParam("comp") Popup comp) throws Exception {
		if (this.nuevaFamilia.getDescripcion() == null || this.nuevaFamilia.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nuevaFamilia.setDescripcion(this.nuevaFamilia.getDescripcion().toUpperCase());
		rr.saveObject(this.nuevaFamilia, this.getLoginNombre());
		this.nuevaFamilia = new ArticuloFamilia();
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	@NotifyChange({ "familias", "nuevaFamilia", "selectedFamilia" })
	public void saveFamilia(@BindingParam("comp") Popup comp) throws Exception {
		if (this.selectedFamilia.getDescripcion() == null || this.selectedFamilia.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedFamilia.setDescripcion(this.selectedFamilia.getDescripcion().toUpperCase());
		rr.saveObject(this.selectedFamilia, this.getLoginNombre());
		this.nuevaFamilia = new ArticuloFamilia();
		this.selectedFamilia = null;
		comp.close();
		Clients.showNotification("REGISTRO MODIFICADO..");
	}
	
	@Command
	@NotifyChange({ "marcas", "nuevaMarca", "selectedMarca" })
	public void addMarca(@BindingParam("comp") Popup comp) throws Exception {
		if (this.nuevaMarca.getDescripcion() == null || this.nuevaMarca.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nuevaMarca.setDescripcion(this.nuevaMarca.getDescripcion().toUpperCase());
		rr.saveObject(this.nuevaMarca, this.getLoginNombre());
		this.nuevaMarca = new ArticuloMarca();
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	@NotifyChange({ "marcas", "nuevaMarca", "selectedMarca" })
	public void saveMarca(@BindingParam("comp") Popup comp) throws Exception {
		if (this.selectedMarca.getDescripcion() == null || this.selectedMarca.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedMarca.setDescripcion(this.selectedMarca.getDescripcion().toUpperCase());
		rr.saveObject(this.selectedMarca, this.getLoginNombre());
		this.selectedMarca = new ArticuloMarca();
		this.selectedMarca = null;
		comp.close();
		Clients.showNotification("REGISTRO MODIFICADO..");
	}
	
	
	/**
	 * GETS / SETS
	 */
	
	@SuppressWarnings("unchecked")
	public List<ArticuloFamilia> getFamilias() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloFamilia.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloMarca> getMarcas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloMarca.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloGrupo> getGrupos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloGrupo.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloSubGrupo> getSubGrupos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloSubGrupo.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloAplicacion> getAplicaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloAplicacion.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloModelo> getModelos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloModelo.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloModelo> getPartes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloModelo.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloPresentacion> getPresentaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloPresentacion.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloAPI> getApis() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloAPI.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloIndicecarga> getIndicesCarga() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloIndicecarga.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloLado> getLados() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloLado.class.getName());
	}

	public ArticuloFamilia getSelectedFamilia() {
		return selectedFamilia;
	}

	public void setSelectedFamilia(ArticuloFamilia selectedFamilia) {
		this.selectedFamilia = selectedFamilia;
	}

	public ArticuloFamilia getNuevaFamilia() {
		return nuevaFamilia;
	}

	public void setNuevaFamilia(ArticuloFamilia nuevaFamilia) {
		this.nuevaFamilia = nuevaFamilia;
	}

	public ArticuloMarca getSelectedMarca() {
		return selectedMarca;
	}

	public void setSelectedMarca(ArticuloMarca selectedMarca) {
		this.selectedMarca = selectedMarca;
	}

	public ArticuloMarca getNuevaMarca() {
		return nuevaMarca;
	}

	public void setNuevaMarca(ArticuloMarca nuevaMarca) {
		this.nuevaMarca = nuevaMarca;
	}
}
