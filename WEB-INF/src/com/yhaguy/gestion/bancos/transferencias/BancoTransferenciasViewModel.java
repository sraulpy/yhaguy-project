package com.yhaguy.gestion.bancos.transferencias;

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
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoTransferencia;
import com.yhaguy.domain.RegisterDomain;

public class BancoTransferenciasViewModel extends SimpleViewModel {
	
	private BancoTransferencia nva_transferencia;
	private BancoTransferencia selected_transferencia;
	
	private String filter_origen = "";
	private String filter_destino = "";
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
	public void addTransferencia(@BindingParam("comp") Popup comp) throws Exception {
		if(!this.isDatosValidos()) {
			Clients.showNotification("NO SE PUDO GUARDAR, VERIFIQUE LOS DATOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.nva_transferencia, this.getLoginNombre());
		comp.close();
		Clients.showNotification("REGISTRO GUARDADO..");
		this.inicializarDatos();
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean isDatosValidos() {
		boolean out = true;
		if (this.nva_transferencia.getOrigen() == null) {
			out = false;
		}
		if (this.nva_transferencia.getDestino() == null) {
			out = false;
		}
		if (this.nva_transferencia.getMoneda() == null) {
			out = false;
		}
		if (this.nva_transferencia.getImporte() <= 500) {
			out = false;
		}
		return out;
	}
	
	/**
	 * inicializar datos..
	 */
	private void inicializarDatos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nva_transferencia = new BancoTransferencia();
		this.nva_transferencia.setFecha(new Date());
		this.nva_transferencia.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		this.nva_transferencia.setNumero("");
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filter_origen", "filter_destino", "filter_numero", "filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<BancoTransferencia> getTransferencias() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoTransferencias(this.getFilterFecha(), this.filter_origen, this.filter_destino, this.filter_numero);
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
		return filter_origen;
	}

	public void setFilter_banco(String filter_banco) {
		this.filter_origen = filter_banco;
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

	public String getFilter_ruc() {
		return filter_destino;
	}

	public void setFilter_ruc(String filter_ruc) {
		this.filter_destino = filter_ruc;
	}

	public BancoTransferencia getNva_transferencia() {
		return nva_transferencia;
	}

	public void setNva_transferencia(BancoTransferencia nva_transferencia) {
		this.nva_transferencia = nva_transferencia;
	}

	public BancoTransferencia getSelected_transferencia() {
		return selected_transferencia;
	}

	public void setSelected_transferencia(BancoTransferencia selected_transferencia) {
		this.selected_transferencia = selected_transferencia;
	}

	public String getFilter_origen() {
		return filter_origen;
	}

	public void setFilter_origen(String filter_origen) {
		this.filter_origen = filter_origen;
	}

	public String getFilter_destino() {
		return filter_destino;
	}

	public void setFilter_destino(String filter_destino) {
		this.filter_destino = filter_destino;
	}
}
