package com.yhaguy.gestion.notadebito;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

public class NotaDebitoSimpleVM extends SoloViewModel {

	private NotaDebitoControlBody dato;
	
	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) NotaDebitoControlBody dato) {
		this.dato = dato;
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * @return los tipos de iva..
	 */
	public List<MyPair> getTiposDeIva() {
		List<MyPair> out = new ArrayList<MyPair>();
		for (MyArray my : this.dato.getDtoUtil().getTiposDeIva()) {
			MyPair myp = new MyPair(my.getId(), (String) my.getPos1());
			out.add(myp);
		}
		return out;
	}

	public NotaDebitoControlBody getDato() {
		return dato;
	}

	public void setDato(NotaDebitoControlBody dato) {
		this.dato = dato;
	}
}
