package com.yhaguy.gestion.stock.inventarios;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.AjusteStockDetalle;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;

public class InventarioMobileVM extends SimpleViewModel {
	
	private String filterNumero = "";
	private String filterCodigo = "";
	private int cantidadConteo = 0;
	
	private String selectedContador = "";
	
	private AjusteStock selectedInventario;
	private Articulo selectedArticulo;

	@Init
	public void init() {}
	
	@AfterCompose
	public void AfterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	@Command
	@NotifyChange({ "cantidadConteo", "selectedArticulo" })
	public void grabarConteo() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		long sistema = rr.getStockDisponible(this.selectedArticulo.getId(), 
				this.selectedInventario.getDeposito().getId());
		boolean registrado = false;
		
		for (AjusteStockDetalle item : this.selectedInventario.getDetalles()) {
			if (item.getArticulo().getCodigoInterno().equals(this.selectedArticulo.getCodigoInterno())) {
				registrado = true;
				item.setCantidad(item.getCantidad() + this.getCantidadConteo());
				item.setCantidadSistema(Long.valueOf(sistema).intValue());
			}
		}
		
		if (!registrado) {
			AjusteStockDetalle det = new AjusteStockDetalle();		
			det.setArticulo(this.selectedArticulo);
			det.setCantidad(this.getCantidadConteo());
			det.setCantidadSistema(Long.valueOf(sistema).intValue());
			det.setCostoGs(0);
			this.selectedInventario.getDetalles().add(det);
		}		
		rr.saveObject(this.selectedInventario, "mobile");
		
		this.cantidadConteo = 0;
		this.selectedArticulo = null;
		Clients.showNotification("CONTEO ENVIADO..!");
	}
	
	@DependsOn("selectedContador")
	public List<AjusteStock> getInventariosPendientes() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getInventariosPendientes(this.selectedContador);
	}

	@DependsOn("filterCodigo")
	public List<Articulo> getArticulos() throws Exception {
		if (this.filterCodigo.isEmpty()) {
			return new ArrayList<Articulo>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();		
		return rr.getArticulos(this.filterCodigo, 50);
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return los contadores..
	 */
	public List<String> getContadores() throws Exception {
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

	public AjusteStock getSelectedInventario() {
		return selectedInventario;
	}

	public void setSelectedInventario(AjusteStock selectedInventario) {
		this.selectedInventario = selectedInventario;
	}

	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}

	public Articulo getSelectedArticulo() {
		return selectedArticulo;
	}

	public void setSelectedArticulo(Articulo selectedArticulo) {
		this.selectedArticulo = selectedArticulo;
	}

	public int getCantidadConteo() {
		return cantidadConteo;
	}

	public void setCantidadConteo(int cantidadConteo) {
		this.cantidadConteo = cantidadConteo;
	}

	public String getSelectedContador() {
		return selectedContador;
	}

	public void setSelectedContador(String selectedContador) {
		this.selectedContador = selectedContador;
	}
}
