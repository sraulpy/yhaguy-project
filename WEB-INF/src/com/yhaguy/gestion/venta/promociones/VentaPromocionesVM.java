package com.yhaguy.gestion.venta.promociones;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.VentaVale;

public class VentaPromocionesVM extends SimpleViewModel {
	
	private VentaVale vale;
	
	private String filterCodigoInterno = "";
	private String filterDescripcion = "";
	
	private Object[] selectedArticulo;
	

	@Init(superclass = true)
	public void init() {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.vale = (VentaVale) rr.getObject(VentaVale.class.getName(), 1);
			if (this.vale == null) {
				this.vale = new VentaVale();
				rr.saveObject(this.vale, this.getLoginNombre());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void saveVale() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.vale, this.getLoginNombre());
	}
	
	@Command
	@NotifyChange("*")
	public void addArticulo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Articulo art = rr.getArticuloById((long) this.selectedArticulo[0]);
		this.vale.getArticulos().add(art);
		rr.saveObject(art, this.getLoginNombre());
		Clients.showNotification("ITEM AGREGADO..");
	}
	
	@DependsOn({ "filterCodigoInterno", "filterDescripcion" })
	public List<Object[]> getArticulos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulos(this.filterCodigoInterno, this.filterDescripcion);
	}

	public String getFilterCodigoInterno() {
		return filterCodigoInterno;
	}

	public void setFilterCodigoInterno(String filterCodigoInterno) {
		this.filterCodigoInterno = filterCodigoInterno;
	}

	public String getFilterDescripcion() {
		return filterDescripcion;
	}

	public void setFilterDescripcion(String filterDescripcion) {
		this.filterDescripcion = filterDescripcion;
	}

	public VentaVale getVale() {
		return vale;
	}

	public void setVale(VentaVale vale) {
		this.vale = vale;
	}

	public Object[] getSelectedArticulo() {
		return selectedArticulo;
	}

	public void setSelectedArticulo(Object[] selectedArticulo) {
		this.selectedArticulo = selectedArticulo;
	}
}
