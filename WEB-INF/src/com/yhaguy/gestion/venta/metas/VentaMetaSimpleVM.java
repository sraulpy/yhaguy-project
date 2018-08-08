package com.yhaguy.gestion.venta.metas;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;

public class VentaMetaSimpleVM extends SoloViewModel {
	
	private VentaMetaViewModel dato;

	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) VentaMetaViewModel dato) {
		this.dato = dato;
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}	

	
	/**
	 * GETS / SETS
	 */
	public List<MyPair> getVendedores() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Funcionario> vends = rr.getVendedores();
		for (Funcionario vend : vends) {
			MyPair myp = new MyPair(vend.getId(), vend.getRazonSocial().toUpperCase());
			out.add(myp);
		}
		return out;
	}
	
	public VentaMetaViewModel getDato() {
		return dato;
	}

	public void setDato(VentaMetaViewModel dato) {
		this.dato = dato;
	}	
}
