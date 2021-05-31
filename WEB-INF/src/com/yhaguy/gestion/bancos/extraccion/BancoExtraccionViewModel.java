package com.yhaguy.gestion.bancos.extraccion;

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
import com.yhaguy.domain.BancoExtraccion;
import com.yhaguy.domain.RegisterDomain;

public class BancoExtraccionViewModel extends SimpleViewModel {
	
	private BancoExtraccion nva_extraccion;
	private BancoExtraccion selected_extraccion;
	
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
	public void addExtraccion(@BindingParam("comp") Popup comp) throws Exception {
		if(!this.isDatosValidos()) {
			Clients.showNotification("NO SE PUDO GUARDAR, VERIFIQUE LOS DATOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.nva_extraccion, this.getLoginNombre());
		comp.close();
		Clients.showNotification("REGISTRO GUARDADO..");
		this.inicializarDatos();
	}
	
	@Command
	public void verificarCotizacion() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		double tc = rr.getTipoCambioVenta(this.nva_extraccion.getFecha(), 0);
		this.nva_extraccion.setTipoCambio(tc);
		BindUtils.postNotifyChange(null, null, this.nva_extraccion, "tipoCambio");
	}
	
	@Command
	public void selectMoneda() throws Exception {
		this.nva_extraccion.setMoneda(this.nva_extraccion.getBanco().getMoneda());
		BindUtils.postNotifyChange(null, null, this.nva_extraccion, "moneda");
		this.verificarCotizacion();		
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean isDatosValidos() {
		boolean out = true;
		if (this.nva_extraccion.getBanco() == null) {
			out = false;
		}
		if (this.nva_extraccion.getMoneda() == null) {
			out = false;
		}
		if (this.nva_extraccion.getImporte() <= 001) {
			out = false;
		}
		if ((!this.nva_extraccion.isMonedaLocal()) && 
				this.nva_extraccion.getTipoCambio() <= 1) {
			out = false;
		}
		return out;
	}
	
	/**
	 * inicializar datos..
	 */
	private void inicializarDatos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nva_extraccion = new BancoExtraccion();
		this.nva_extraccion.setFecha(new Date());
		this.nva_extraccion.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		this.nva_extraccion.setNumero("");
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filter_banco", "filter_numero", "filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<BancoExtraccion> getExtracciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoExtracciones(this.getFilterFecha(), this.filter_banco, this.filter_numero);
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

	public BancoExtraccion getNva_extraccion() {
		return nva_extraccion;
	}

	public void setNva_extraccion(BancoExtraccion nva_transferencia) {
		this.nva_extraccion = nva_transferencia;
	}

	public BancoExtraccion getSelected_extraccion() {
		return selected_extraccion;
	}

	public void setSelected_extraccion(BancoExtraccion selected_transferencia) {
		this.selected_extraccion = selected_transferencia;
	}

	public String getFilter_origen() {
		return filter_banco;
	}

	public void setFilter_origen(String filter_origen) {
		this.filter_banco = filter_origen;
	}
}
