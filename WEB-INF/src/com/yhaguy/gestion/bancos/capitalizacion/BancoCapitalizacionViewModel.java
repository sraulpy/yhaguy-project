package com.yhaguy.gestion.bancos.capitalizacion;

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
import com.yhaguy.domain.BancoIngreso;
import com.yhaguy.domain.RegisterDomain;

public class BancoCapitalizacionViewModel extends SimpleViewModel {
	
	private BancoIngreso nva_capitalizacion;
	private BancoIngreso selected_capitalizacion;
	
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
	public void addCapitalizacion(@BindingParam("comp") Popup comp) throws Exception {
		if(!this.isDatosValidos()) {
			Clients.showNotification("NO SE PUDO GUARDAR, VERIFIQUE LOS DATOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.nva_capitalizacion, this.getLoginNombre());
		comp.close();
		Clients.showNotification("REGISTRO GUARDADO..");
		this.inicializarDatos();
	}
	
	@Command
	public void verificarCotizacion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		double tc = rr.getTipoCambioVenta(this.nva_capitalizacion.getFecha(), 0);
		this.nva_capitalizacion.setTipoCambio(tc);
		BindUtils.postNotifyChange(null, null, this.nva_capitalizacion, "tipoCambio");
	}
	
	@Command
	public void selectMoneda() throws Exception {
		this.nva_capitalizacion.setMoneda(this.nva_capitalizacion.getBanco().getMoneda());
		BindUtils.postNotifyChange(null, null, this.nva_capitalizacion, "moneda");
		this.verificarCotizacion();		
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean isDatosValidos() {
		boolean out = true;
		if (this.nva_capitalizacion.getBanco() == null) {
			out = false;
		}
		if (this.nva_capitalizacion.getMoneda() == null) {
			out = false;
		}
		if (this.nva_capitalizacion.getImporte() <= 0.001) {
			out = false;
		}
		if ((!this.nva_capitalizacion.isMonedaLocal()) && 
				this.nva_capitalizacion.getTipoCambio() <= 1) {
			out = false;
		}
		return out;
	}
	
	/**
	 * inicializar datos..
	 */
	private void inicializarDatos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nva_capitalizacion = new BancoIngreso();
		this.nva_capitalizacion.setFecha(new Date());
		this.nva_capitalizacion.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		this.nva_capitalizacion.setNumero("");
		this.nva_capitalizacion.setConcepto(BancoIngreso.CONCEPTO_CAPITALIZACION_INTERESES);
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filter_banco", "filter_numero", "filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<BancoIngreso> getCapitalizaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoIngresos(this.getFilterFecha(), this.filter_banco, this.filter_numero, BancoIngreso.CONCEPTO_CAPITALIZACION_INTERESES);
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

	public BancoIngreso getNva_capitalizacion() {
		return nva_capitalizacion;
	}

	public void setNva_capitalizacion(BancoIngreso nva_transferencia) {
		this.nva_capitalizacion = nva_transferencia;
	}

	public BancoIngreso getSelected_capitalizacion() {
		return selected_capitalizacion;
	}

	public void setSelected_apitalizacion(BancoIngreso selected_transferencia) {
		this.selected_capitalizacion = selected_transferencia;
	}

	public String getFilter_origen() {
		return filter_banco;
	}

	public void setFilter_origen(String filter_origen) {
		this.filter_banco = filter_origen;
	}

	public void setSelected_capitalizacion(BancoIngreso selected_capitalizacion) {
		this.selected_capitalizacion = selected_capitalizacion;
	}
}
