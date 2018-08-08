package com.yhaguy.gestion.compras.definiciones;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.UtilDTO;
import com.yhaguy.gestion.comun.ControlLogica;

public class ArticuloGastoDefinicionesSoloViewModel extends SoloViewModel {

	private ArticuloGastoDefinicionesViewModel dato;
	private ControlLogica ctr = new ControlLogica(null);

	List<MyPair> listaTipoIvaMyPair;

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) ArticuloGastoDefinicionesViewModel dato)
			throws Exception {

		this.listaTipoIvaMyPair = new ArrayList<MyPair>();
		for (MyArray tipoIva : this.getUtil().getTiposDeIva()) {
			MyPair tipoIvaMyPair = new MyPair();
			tipoIvaMyPair.setId(tipoIva.getId());
			tipoIvaMyPair.setText((String) tipoIva.getPos1());
			tipoIvaMyPair.setSigla((String) tipoIva.getPos2());
			this.listaTipoIvaMyPair.add(tipoIvaMyPair);
		}

		this.dato = dato;
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}

	public UtilDTO getUtil() {
		return (UtilDTO) this.getDtoUtil();
	}

	@Command
	@NotifyChange("*")
	public void buscarCtaContable() throws Exception {
		String nombre = (String) this.dato.getCtaContable().getPos1();
		MyArray ctaContableEncontrada =  this.ctr.buscarCuentaContable("", nombre, 1, null);
		if(ctaContableEncontrada != null){
			this.dato.setCtaContable(ctaContableEncontrada);
			if(this.dato.isAgregar()){
				this.dato.getArticuloGasto().setPos5(new MyPair(ctaContableEncontrada.getId(), (String) ctaContableEncontrada.getPos1()));
			}else if (this.dato.isEditar()){
				this.dato.getSelectArticuloGasto().setPos5(new MyPair(ctaContableEncontrada.getId(), (String) ctaContableEncontrada.getPos1()));
			}
		}else {
			this.dato.setCtaContable(this.dato.getCtaContable());
		}
	}

	// ///// get y set ///////

	public ArticuloGastoDefinicionesViewModel getDato() {
		return dato;
	}

	public void setDato(ArticuloGastoDefinicionesViewModel dato) {
		this.dato = dato;
	}

	public List<MyPair> getListaTipoIvaMyPair() {
		return listaTipoIvaMyPair;
	}

	public void setListaTipoIvaMyPair(List<MyPair> listaTipoIvaMyPair) {
		this.listaTipoIvaMyPair = listaTipoIvaMyPair;
	}

}
