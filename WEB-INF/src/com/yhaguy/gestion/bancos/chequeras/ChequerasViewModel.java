package com.yhaguy.gestion.bancos.chequeras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.coreweb.domain.AutoNumero;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class ChequerasViewModel extends SimpleViewModel {

	private AutoNumero nvaChequera = new AutoNumero();
	
	private String filterBanco = "";
	
	@Init(superclass = true)
	public void init() {
		this.nvaChequera.setDescripcion(null);
		this.nvaChequera.setNumeroDesde(0);
		this.nvaChequera.setNumeroHasta(0);
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addChequera(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvaChequera.setAuxi("CHEQUERA");
		this.nvaChequera.setNumero(this.nvaChequera.getNumeroDesde() - 1);
		this.nvaChequera.setKey(this.nvaChequera.getDescripcion() + "-" + Utiles.getDateToString(new Date(), Utiles.DD_MM_YY));
		rr.saveObject(this.nvaChequera, this.getLoginNombre());
		Clients.showNotification("REGISTRO AGREGADO");
		comp.close();
		this.nvaChequera.setNumeroDesde(0);
		this.nvaChequera.setNumeroHasta(0);
		this.nvaChequera.setDescripcion(null);
	}
	
	/**
	 * GETS / SETS
	 */
	@DependsOn("filterBanco")
	public List<AutoNumero> getChequeras() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getBancoChequeras(this.filterBanco);
	}
	
	/**
	 * @return los bancos..
	 */
	public List<String> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<String> out = new ArrayList<String>();
		for (BancoCta cta : rr.getBancosCta()) {
			out.add(cta.getDescripcion());
		}
		Collections.sort(out, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int compare = o1.compareTo(o2);				
				return compare;
			}
		});
		return out;
	}

	public AutoNumero getNvaChequera() {
		return nvaChequera;
	}

	public void setNvaChequera(AutoNumero nvaChequera) {
		this.nvaChequera = nvaChequera;
	}

	public String getFilterBanco() {
		return filterBanco;
	}

	public void setFilterBanco(String filterBanco) {
		this.filterBanco = filterBanco;
	}
}
