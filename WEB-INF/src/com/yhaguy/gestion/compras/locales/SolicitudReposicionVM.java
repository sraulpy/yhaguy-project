package com.yhaguy.gestion.compras.locales;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloReposicion;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.CompraLocalOrdenDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;

public class SolicitudReposicionVM extends SimpleViewModel {
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaYY = "";
	private String filterCodigo = "";
	private String filterFuncionario = "";
	private String filterProveedor = "";
	private String filterEstado = "";
	
	private List<Object[]> selectedItems;
	private List<CompraLocalOrdenDetalle> detalles;
	
	private Proveedor proveedor;

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaYY = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void prepararSolicitud(@BindingParam("parent") Component parent, @BindingParam("popup") Popup popup) throws Exception {
		this.detalles = new ArrayList<CompraLocalOrdenDetalle>();
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Object[] art : this.selectedItems) {
			long idArt = (long) art[0];
			int cant = (int) art[5];
			Articulo ar = rr.getArticuloById(idArt);
			CompraLocalOrdenDetalle det = new CompraLocalOrdenDetalle();
			det.setArticulo(ar);
			det.setCantidad(cant);
			det.setCantidadRecibida(cant);
			det.setCostoGs(0);
			det.setCostoDs(0);
			det.setOrdenCompra(true);
			this.detalles.add(det);
		}
		popup.open(parent, "after_start");
	}
	
	@Command
	public void generarOrdenCompra(@BindingParam("popup") Popup popup) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CompraLocalOrden oc = new CompraLocalOrden();
		oc.setAutorizado(false);
		oc.setAutorizadoPor("");
		oc.setFechaCreacion(new Date());
		oc.setNumero(Configuracion.NRO_COMPRA_LOCAL_ORDEN + "-"
				+ AutoNumeroControl.getAutoNumero(Configuracion.NRO_COMPRA_LOCAL_ORDEN, 5));
		oc.setObservacion("SOLICITUD REPOSICION");
		oc.setProveedor(proveedor);
		oc.setSucursal(rr.getSucursalAppById(this.getAcceso().getSucursalOperativa().getId()));
		oc.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ORDEN_COMPRA));
		oc.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		oc.setTipoCambio(1);
		oc.setCondicionPago(rr.getCondicionPagoById(1));
		Set<CompraLocalOrdenDetalle> dets = new HashSet<CompraLocalOrdenDetalle>();
		for (CompraLocalOrdenDetalle det : this.detalles) {
			dets.add(det);
		}
		oc.setDetalles(dets);
		for (Object[] det : this.selectedItems) {
			long id = (long) det[7];
			ArticuloReposicion rep = (ArticuloReposicion) rr.getObject(ArticuloReposicion.class.getName(), id);
			rep.setEstado(ArticuloReposicion.ESTADO_GENERADO);
			rep.setNumeroOrdenCompra(oc.getNumero());
			rr.saveObject(rep, this.getLoginNombre());
		}
		rr.saveObject(oc, this.getLoginNombre());
		Clients.showNotification("ORDEN COMPRA GENERADA NRO. " + oc.getNumero());
		popup.close();
	}
	
	/**
	 * GETS AND SETS
	 */
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaYY", "filterCodigo", "filterFuncionario", "filterEstado" })
	public List<Object[]> getReposiciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticuloReposiciones(this.getFilterFecha(), this.filterCodigo, this.filterFuncionario, this.filterEstado);
	}
	
	@DependsOn("filterProveedor")
	public List<Proveedor> getProveedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedores(this.filterProveedor);
	}
	
	/**
	 * @return el acceso..
	 */
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaYY.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaYY.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaYY;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaYY;
		return this.filterFechaYY + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
	}

	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFechaDD) {
		this.filterFechaDD = filterFechaDD;
	}

	public String getFilterFechaMM() {
		return filterFechaMM;
	}

	public void setFilterFechaMM(String filterFechaMM) {
		this.filterFechaMM = filterFechaMM;
	}

	public String getFilterFechaYY() {
		return filterFechaYY;
	}

	public void setFilterFechaYY(String filterFechaYY) {
		this.filterFechaYY = filterFechaYY;
	}

	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}

	public String getFilterFuncionario() {
		return filterFuncionario;
	}

	public void setFilterFuncionario(String filterFuncionario) {
		this.filterFuncionario = filterFuncionario;
	}

	public List<Object[]> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<Object[]> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public String getFilterProveedor() {
		return filterProveedor;
	}

	public void setFilterProveedor(String filterProveedor) {
		this.filterProveedor = filterProveedor;
	}

	public List<CompraLocalOrdenDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<CompraLocalOrdenDetalle> detalles) {
		this.detalles = detalles;
	}

	public String getFilterEstado() {
		return filterEstado;
	}

	public void setFilterEstado(String filterEstado) {
		this.filterEstado = filterEstado;
	}
}
