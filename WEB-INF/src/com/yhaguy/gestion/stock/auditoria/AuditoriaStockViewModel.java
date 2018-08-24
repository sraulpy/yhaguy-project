package com.yhaguy.gestion.stock.auditoria;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlArticuloStock;
import com.yhaguy.inicio.AccesoDTO;

public class AuditoriaStockViewModel extends SimpleViewModel {
	
	public static final long ID_SUC_PRINCIPAL = 2;
	
	private String filterCodigo = "";
	private String filterDescripcion = "";
	
	private long stock = 0;
	private long stock_ = 0;
	private long saldo = 0;
	
	private Object[] selectedArticulo;
	private MyArray selectedDeposito;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Command
	@NotifyChange("*")
	public void recalcularStock(@BindingParam("item") MyArray item) throws Exception {
		long stockActual = (long) item.getPos2();
		long stockHistorial = Long.parseLong((String) item.getPos3());
		long dif = stockActual - stockHistorial;
		RegisterDomain rr = RegisterDomain.getInstance();
		
		ArticuloDeposito adp = rr.getArticuloDeposito((long) this.selectedArticulo[0], item.getId());
		adp.setStock(stockHistorial);
		rr.saveObject(adp, this.getLoginNombre());
		
		ArticuloDeposito adp_ = rr.getArticuloDeposito((long) this.selectedArticulo[0], Deposito.ID_DEPOSITO_CONTROL);
		if (adp_ != null) {
			adp_.setStock(adp_.getStock() + dif);
			rr.saveObject(adp_, this.getLoginNombre());
		}
		Clients.showNotification("ITEM RECALCULADO..");
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filterCodigo", "filterDescripcion" })
	public List<Object[]> getArticulos() throws Exception {
		if (this.filterCodigo.trim().isEmpty() && this.filterDescripcion.trim().isEmpty()) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getArticulos(this.filterCodigo, this.filterDescripcion);
	}
	
	/**
	 * @return los precios del articulo..
	 */
	@DependsOn("selectedArticulo")
	public List<MyArray> getExistencia() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		
		if(this.selectedArticulo == null) return out;
		
		this.stock = 0;	
		this.stock_ = 0;
		long idArticulo = (long) this.selectedArticulo[0];
		
		for (MyPair dep : this.getDepositos()) {
			List<Object[]> historial = this.getHistorial(idArticulo, dep.getId());
			MyArray my = new MyArray();
			my.setId(dep.getId());
			my.setPos1(dep.getText());
			my.setPos2(this.getStock(idArticulo, dep.getId()));
			my.setPos3(historial.size() > 0 ? historial.get(historial.size() - 1)[7] : "0");
			out.add(my);
			this.stock += (long) my.getPos2();
			this.stock_ += Long.parseLong((String) my.getPos3());
		}		
		BindUtils.postNotifyChange(null, null, this, "stock");
		BindUtils.postNotifyChange(null, null, this, "stock_");
		return out;
	}
	
	@DependsOn({ "selectedArticulo", "selectedDeposito" })
	public List<Object[]> getHistorial() throws Exception {
		if (this.selectedArticulo == null || this.selectedDeposito == null) {
			this.saldo = 0;
			return new ArrayList<Object[]>();
		}
		List<Object[]> historial = ControlArticuloStock.getHistorialMovimientos((long) this.selectedArticulo[0],
				this.selectedDeposito.getId(), this.getAcceso().getSucursalOperativa().getId()); 
		this.saldo = historial.size() > 0 ? Long.parseLong((String) historial.get(historial.size() - 1)[7]) : 0;
		BindUtils.postNotifyChange(null, null, this, "saldo");
		return historial;
	}
	
	/**
	 * @return el historial de articulo..
	 */
	private List<Object[]> getHistorial(long idArticulo, long idDeposito) throws Exception {
		return ControlArticuloStock.getHistorialMovimientos(idArticulo, idDeposito,
				this.getAcceso().getSucursalOperativa().getId());
	}
	
	/**
	 * @return el stock del articulo..
	 */
	private long getStock(long idArticulo, long idDeposito) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloDeposito adp = rr.getArticuloDeposito(idArticulo, idDeposito);
		if(adp == null)
			return 0;
		return adp.getStock();
	}
	
	/**
	 * @return los depositos de la sucursal..
	 */
	private List<MyPair> getDepositos() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Deposito> deps = rr.getDepositosPorSucursal(ID_SUC_PRINCIPAL);
		for (Deposito dep : deps) {
			out.add(new MyPair(dep.getId(), dep.getDescripcion()));
		}
		return out;
	}
	
	private AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		AccesoDTO out = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);			
		return out;
	}
	
	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}

	public String getFilterDescripcion() {
		return filterDescripcion;
	}

	public void setFilterDescripcion(String filterDescripcion) {
		this.filterDescripcion = filterDescripcion;
	}

	public Object[] getSelectedArticulo() {
		return selectedArticulo;
	}

	public void setSelectedArticulo(Object[] selectedArticulo) {
		this.selectedArticulo = selectedArticulo;
	}

	public long getStock() {
		return stock;
	}

	public void setStock(long stock) {
		this.stock = stock;
	}

	public MyArray getSelectedDeposito() {
		return selectedDeposito;
	}

	public void setSelectedDeposito(MyArray selectedDeposito) {
		this.selectedDeposito = selectedDeposito;
	}

	public long getStock_() {
		return stock_;
	}

	public void setStock_(long stock_) {
		this.stock_ = stock_;
	}

	public long getSaldo() {
		return saldo;
	}

	public void setSaldo(long saldo) {
		this.saldo = saldo;
	}
}
