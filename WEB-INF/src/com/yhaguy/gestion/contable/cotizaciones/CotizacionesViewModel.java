package com.yhaguy.gestion.contable.cotizaciones;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoCambio;
import com.yhaguy.util.Utiles;

public class CotizacionesViewModel extends SimpleViewModel {
	
	private TipoCambio nvaCotizacion;
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
			this.inicializar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@SuppressWarnings("deprecation")
	@Command
	@NotifyChange("*")
	public void addCotizacion(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvaCotizacion.getFecha().setHours(0);
		this.nvaCotizacion.getFecha().setMinutes(0);
		this.nvaCotizacion.getFecha().setSeconds(0);
		rr.saveObject(this.nvaCotizacion, this.getLoginNombre());
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO");
		this.inicializar();
	}
	
	/**
	 * inicializa los datos de cotizacion..
	 */
	private void inicializar() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvaCotizacion = new TipoCambio();
		this.nvaCotizacion.setFecha(new Date());
		this.nvaCotizacion.setTipoCambio(rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CAMBIO_SET));
		this.nvaCotizacion.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR));
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({"filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<TipoCambio> getCotizaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCotizaciones(this.getFilterFecha());
	}
	
	/**
	 * @return cotizacion compra vigente..
	 */
	public double getTipoCambioCompra() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipoCambioCompra();
	}
	
	/**
	 * @return cotizacion venta vigente..
	 */
	public double getTipoCambioVenta() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipoCambioVenta();
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaAA.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaAA.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaAA;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaAA;
		return this.filterFechaAA + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
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

	public String getFilterFechaAA() {
		return filterFechaAA;
	}

	public void setFilterFechaAA(String filterFechaAA) {
		this.filterFechaAA = filterFechaAA;
	}

	public TipoCambio getNvaCotizacion() {
		return nvaCotizacion;
	}

	public void setNvaCotizacion(TipoCambio nvaCotizacion) {
		this.nvaCotizacion = nvaCotizacion;
	}
	
}
