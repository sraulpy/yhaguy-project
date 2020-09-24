package com.yhaguy.gestion.pagares;

import java.util.ArrayList;
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
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Pagare;
import com.yhaguy.domain.RegisterDomain;

public class PagaresViewModel extends SimpleViewModel {
	
	private Pagare nvo_pagare;
	
	private String filter_ruc = "";
	private String filter_razonSocial = "";

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
	public void addPagare(@BindingParam("comp") Popup comp) throws Exception {
		if(!this.isDatosValidos()) {
			Clients.showNotification("NO SE PUDO GUARDAR, VERIFIQUE LOS DATOS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(this.nvo_pagare, this.getLoginNombre());
		comp.close();
		Clients.showNotification("REGISTRO GUARDADO..");
		this.inicializarDatos();
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean isDatosValidos() {
		boolean out = true;
		if (this.nvo_pagare.getBeneficiario() == null) {
			out = false;
		}
		if (this.nvo_pagare.getFirmante() == null) {
			out = false;
		}
		if (this.nvo_pagare.getMoneda() == null) {
			out = false;
		}
		if (this.nvo_pagare.getImporte() <= 001) {
			out = false;
		}
		return out;
	}
	
	/**
	 * inicializar datos..
	 */
	private void inicializarDatos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();		
		this.nvo_pagare = new Pagare();
		this.nvo_pagare.setFecha(new Date());
		this.nvo_pagare.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		this.nvo_pagare.setNumero("");
		this.nvo_pagare.setBeneficiario(this.getBeneficiario());
	}

	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return los pagares..
	 */
	public List<Pagare> getPagares() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getPagares();
	}
	
	/**
	 * @return las monedas..
	 */
	public List<Tipo> getMonedas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipos(Configuracion.ID_TIPO_MONEDA);
	}
	
	@DependsOn({ "filter_ruc", "filter_razonSocial" })
	public List<Empresa> getEmpresas() throws Exception {
		if(this.filter_ruc.trim().isEmpty() && this.filter_razonSocial.trim().isEmpty()) return new ArrayList<Empresa>();
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getEmpresas(this.filter_ruc, "", this.filter_razonSocial, "");
	}
	
	/**
	 * @return el beneficiario..
	 */
	public Empresa getBeneficiario() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_YRPS)) {
			Empresa emp = rr.getEmpresaById(Configuracion.ID_EMPRESA_YRPS);
			return emp;
		}
		return null;
	}

	public Pagare getNvo_pagare() {
		return nvo_pagare;
	}

	public void setNvo_pagare(Pagare nvo_pagare) {
		this.nvo_pagare = nvo_pagare;
	}

	public String getFilter_ruc() {
		return filter_ruc;
	}

	public void setFilter_ruc(String filter_ruc) {
		this.filter_ruc = filter_ruc;
	}

	public String getFilter_razonSocial() {
		return filter_razonSocial;
	}

	public void setFilter_razonSocial(String filter_razonSocial) {
		this.filter_razonSocial = filter_razonSocial;
	}
}
