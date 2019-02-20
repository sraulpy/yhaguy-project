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
import com.yhaguy.domain.ArticuloSubMarca;
import com.yhaguy.domain.RegisterDomain;

public class ComprasDefinicionesViewModel extends SimpleViewModel {
	
	private ArticuloFamilia selectedFamilia;
	private ArticuloFamilia nuevaFamilia = new ArticuloFamilia();
	
	private ArticuloMarca selectedMarca;
	private ArticuloMarca nuevaMarca = new ArticuloMarca();
	
	private ArticuloSubMarca selectedSubMarca;
	private ArticuloSubMarca nuevaSubMarca = new ArticuloSubMarca();
	
	private ArticuloGrupo selectedGrupo;
	private ArticuloGrupo nuevoGrupo = new ArticuloGrupo();
	
	private ArticuloSubGrupo selectedSubGrupo;
	private ArticuloSubGrupo nuevoSubGrupo = new ArticuloSubGrupo();

	private ArticuloAplicacion selectedAplicacion;
	private ArticuloAplicacion nuevaAplicacion = new ArticuloAplicacion();
	
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
	
	@Command
	@NotifyChange({ "subMarcas", "nuevaSubMarca", "selectedSubMarca" })
	public void addSubMarca(@BindingParam("comp") Popup comp) throws Exception {
		if (this.nuevaSubMarca.getDescripcion() == null || this.nuevaSubMarca.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nuevaSubMarca.setDescripcion(this.nuevaSubMarca.getDescripcion().toUpperCase());
		rr.saveObject(this.nuevaSubMarca, this.getLoginNombre());
		this.nuevaSubMarca = new ArticuloSubMarca();
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	@NotifyChange({ "subMarcas", "nuevaSubMarca", "selectedSubMarca" })
	public void saveSubMarca(@BindingParam("comp") Popup comp) throws Exception {
		if (this.selectedSubMarca.getDescripcion() == null || this.selectedSubMarca.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedSubMarca.setDescripcion(this.selectedSubMarca.getDescripcion().toUpperCase());
		rr.saveObject(this.selectedSubMarca, this.getLoginNombre());
		this.selectedSubMarca = new ArticuloSubMarca();
		this.selectedSubMarca = null;
		comp.close();
		Clients.showNotification("REGISTRO MODIFICADO..");
	}
	
	@Command
	@NotifyChange({ "grupos", "nuevoGrupo", "selectedGrupo" })
	public void addGrupo(@BindingParam("comp") Popup comp) throws Exception {
		if (this.nuevoGrupo.getDescripcion() == null || this.nuevoGrupo.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nuevoGrupo.setDescripcion(this.nuevoGrupo.getDescripcion().toUpperCase());
		rr.saveObject(this.nuevoGrupo, this.getLoginNombre());
		this.nuevoGrupo = new ArticuloGrupo();
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	@NotifyChange({ "grupos", "nuevoGrupo", "selectedGrupo" })
	public void saveGrupo(@BindingParam("comp") Popup comp) throws Exception {
		if (this.selectedGrupo.getDescripcion() == null || this.selectedGrupo.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedGrupo.setDescripcion(this.selectedGrupo.getDescripcion().toUpperCase());
		rr.saveObject(this.selectedGrupo, this.getLoginNombre());
		this.selectedGrupo = new ArticuloGrupo();
		this.selectedGrupo = null;
		comp.close();
		Clients.showNotification("REGISTRO MODIFICADO..");
	}
	
	@Command
	@NotifyChange({ "subGrupos", "nuevoSubGrupo", "selectedSubGrupo" })
	public void addSubGrupo(@BindingParam("comp") Popup comp) throws Exception {
		if (this.nuevoSubGrupo.getDescripcion() == null || this.nuevoSubGrupo.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nuevoSubGrupo.setDescripcion(this.nuevoSubGrupo.getDescripcion().toUpperCase());
		rr.saveObject(this.nuevoSubGrupo, this.getLoginNombre());
		this.nuevoSubGrupo = new ArticuloSubGrupo();
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	@NotifyChange({ "subGrupos", "nuevoSubGrupo", "selectedSubGrupo" })
	public void saveSubGrupo(@BindingParam("comp") Popup comp) throws Exception {
		if (this.selectedSubGrupo.getDescripcion() == null || this.selectedSubGrupo.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedSubGrupo.setDescripcion(this.selectedSubGrupo.getDescripcion().toUpperCase());
		rr.saveObject(this.selectedSubGrupo, this.getLoginNombre());
		this.selectedSubGrupo = new ArticuloSubGrupo();
		this.selectedSubGrupo = null;
		comp.close();
		Clients.showNotification("REGISTRO MODIFICADO..");
	}
	
	@Command
	@NotifyChange({ "aplicaciones", "nuevaAplicacion", "selectedAplicacion" })
	public void addAplicacion(@BindingParam("comp") Popup comp) throws Exception {
		if (this.nuevaAplicacion.getDescripcion() == null || this.nuevaAplicacion.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nuevaAplicacion.setDescripcion(this.nuevaAplicacion.getDescripcion().toUpperCase());
		rr.saveObject(this.nuevaAplicacion, this.getLoginNombre());
		this.nuevaAplicacion = new ArticuloAplicacion();
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO..");
	}
	
	@Command
	@NotifyChange({ "aplicaciones", "nuevaAplicacion", "selectedAplicacion" })
	public void saveAplicacion(@BindingParam("comp") Popup comp) throws Exception {
		if (this.selectedAplicacion.getDescripcion() == null || this.selectedAplicacion.getDescripcion().trim().isEmpty()) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		this.selectedAplicacion.setDescripcion(this.selectedAplicacion.getDescripcion().toUpperCase());
		rr.saveObject(this.selectedAplicacion, this.getLoginNombre());
		this.selectedAplicacion = new ArticuloAplicacion();
		this.selectedAplicacion = null;
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
	public List<ArticuloSubMarca> getSubMarcas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloSubMarca.class.getName());
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

	public ArticuloSubMarca getSelectedSubMarca() {
		return selectedSubMarca;
	}

	public void setSelectedSubMarca(ArticuloSubMarca selectedSubMarca) {
		this.selectedSubMarca = selectedSubMarca;
	}

	public ArticuloSubMarca getNuevaSubMarca() {
		return nuevaSubMarca;
	}

	public void setNuevaSubMarca(ArticuloSubMarca nuevaSubMarca) {
		this.nuevaSubMarca = nuevaSubMarca;
	}

	public ArticuloGrupo getSelectedGrupo() {
		return selectedGrupo;
	}

	public void setSelectedGrupo(ArticuloGrupo selectedGrupo) {
		this.selectedGrupo = selectedGrupo;
	}

	public ArticuloGrupo getNuevoGrupo() {
		return nuevoGrupo;
	}

	public void setNuevoGrupo(ArticuloGrupo nuevoGrupo) {
		this.nuevoGrupo = nuevoGrupo;
	}

	public ArticuloSubGrupo getSelectedSubGrupo() {
		return selectedSubGrupo;
	}

	public void setSelectedSubGrupo(ArticuloSubGrupo selectedSubGrupo) {
		this.selectedSubGrupo = selectedSubGrupo;
	}

	public ArticuloSubGrupo getNuevoSubGrupo() {
		return nuevoSubGrupo;
	}

	public void setNuevoSubGrupo(ArticuloSubGrupo nuevoSubGrupo) {
		this.nuevoSubGrupo = nuevoSubGrupo;
	}

	public ArticuloAplicacion getSelectedAplicacion() {
		return selectedAplicacion;
	}

	public void setSelectedAplicacion(ArticuloAplicacion selectedAplicacion) {
		this.selectedAplicacion = selectedAplicacion;
	}

	public ArticuloAplicacion getNuevaAplicacion() {
		return nuevaAplicacion;
	}

	public void setNuevaAplicacion(ArticuloAplicacion nuevaAplicacion) {
		this.nuevaAplicacion = nuevaAplicacion;
	}
}
