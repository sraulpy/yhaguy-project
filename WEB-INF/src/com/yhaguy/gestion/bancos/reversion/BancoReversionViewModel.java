package com.yhaguy.gestion.bancos.reversion;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoEgreso;
import com.yhaguy.domain.RegisterDomain;

public class BancoReversionViewModel extends SimpleViewModel {
	
	private BancoEgreso nva_reversion;
	private BancoEgreso selected_reversion;
	
	private String filter_banco = "";
	private String filter_numero = "";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";

	@Init(superclass = true)
	public void init() {
		try {
			this.inicializarDatos();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addReversion(@BindingParam("comp") Popup comp) throws Exception {
		if(!this.isDatosValidos()) {
			Clients.showNotification("NO SE PUDO GUARDAR, VERIFIQUE LOS DATOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.nva_reversion, this.getLoginNombre());
		comp.close();
		Clients.showNotification("REGISTRO GUARDADO..");
		this.inicializarDatos();
	}
	
	@Command
	public void verificarCotizacion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		double tc = rr.getTipoCambioVenta(this.nva_reversion.getFecha(), 0);
		this.nva_reversion.setTipoCambio(tc);
		BindUtils.postNotifyChange(null, null, this.nva_reversion, "tipoCambio");
	}
	
	@Command
	public void selectMoneda() throws Exception {
		this.nva_reversion.setMoneda(this.nva_reversion.getBanco().getMoneda());
		BindUtils.postNotifyChange(null, null, this.nva_reversion, "moneda");
		this.verificarCotizacion();		
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean isDatosValidos() {
		boolean out = true;
		if (this.nva_reversion.getBanco() == null) {
			out = false;
		}
		if (this.nva_reversion.getMoneda() == null) {
			out = false;
		}
		if (this.nva_reversion.getImporte() <= 0.001) {
			out = false;
		}
		if ((!this.nva_reversion.isMonedaLocal()) && 
				this.nva_reversion.getTipoCambio() <= 1) {
			out = false;
		}
		return out;
	}
	
	/**
	 * inicializar datos..
	 */
	private void inicializarDatos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nva_reversion = new BancoEgreso();
		this.nva_reversion.setFecha(new Date());
		this.nva_reversion.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		this.nva_reversion.setNumero("");
		this.nva_reversion.setConcepto(BancoEgreso.CONCEPTO_REVERSION);
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filter_banco", "filter_numero", "filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<BancoEgreso> getReversiones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoEgresos(this.getFilterFecha(), this.filter_banco, this.filter_numero, BancoEgreso.CONCEPTO_REVERSION);
	}
	
	/**
	 * @return los bancos..
	 */
	public List<BancoCta> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancosCta();
	}
	
	/**
	 * @return las monedas..
	 */
	public List<Tipo> getMonedas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_MONEDA);
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

	public String getFilter_banco() {
		return filter_banco;
	}

	public void setFilter_banco(String filter_banco) {
		this.filter_banco = filter_banco;
	}

	public String getFilter_numero() {
		return filter_numero;
	}

	public void setFilter_numero(String filter_numero) {
		this.filter_numero = filter_numero;
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

	public BancoEgreso getNva_reversion() {
		return nva_reversion;
	}

	public void setNva_reversion(BancoEgreso nva_transferencia) {
		this.nva_reversion = nva_transferencia;
	}

	public BancoEgreso getSelected_reversion() {
		return selected_reversion;
	}

	public void setSelected_apitalizacion(BancoEgreso selected_transferencia) {
		this.selected_reversion = selected_transferencia;
	}

	public String getFilter_origen() {
		return filter_banco;
	}

	public void setFilter_origen(String filter_origen) {
		this.filter_banco = filter_origen;
	}

	public void setSelected_reversion(BancoEgreso selected_capitalizacion) {
		this.selected_reversion = selected_capitalizacion;
	}
}
