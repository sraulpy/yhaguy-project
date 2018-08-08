package com.yhaguy.gestion.notacredito;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.UtilDTO;

public class NotaCreditoSimpleControl extends SoloViewModel {
	
	private NotaCreditoControlBody dato;

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) NotaCreditoControlBody dato) {
		this.dato = dato;
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(){
	}
	
	@Override
	public String getAliasFormularioCorriente(){
		return dato.getAliasFormularioCorriente();
	}	
	
	/**
	 * verifica la cantidad..
	 */
	@Command
	public void validarCantidad(@BindingParam("item") MyArray item,
			@BindingParam("comp") Intbox comp) {
		int factura = (int) item.getPos3();
		int devolucion = (int) item.getPos6();
		if (factura < devolucion) {
			item.setPos6(0);
			Clients.showNotification("cantidad no valida..",
					Clients.NOTIFICATION_TYPE_ERROR, comp, null, 2000);
			BindUtils.postNotifyChange(null, null, item, "pos6");
		}
	}
	
	/**
	 * verifica la diferencia de precio..
	 */
	@Command
	public void validarDiferencia(@BindingParam("item") MyArray item,
			@BindingParam("comp") Doublebox comp) {
		double factura = (double) item.getPos4();
		double diferencia = (double) item.getPos7();
		if (factura < diferencia) {
			item.setPos7((double) 0);
			Clients.showNotification("valor no valido..",
					Clients.NOTIFICATION_TYPE_ERROR, comp, null, 2000);
			BindUtils.postNotifyChange(null, null, item, "pos7");
		}
	}
	
	@Command
	public void convertirMoneda() {
		double tc = this.dato.getDto().getTipoCambio();
		this.dato.getNvoItem().setMontoGs(this.dato.getNvoItem().getMontoDs() * tc);
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "montoGs");
	}
	
	/***************************** GET/SET *****************************/
	
	public NotaCreditoControlBody getDato() {
		return dato;
	}

	public void setDato(NotaCreditoControlBody dato) {
		this.dato = dato;
	}
	
	public UtilDTO getUtiDto(){
		return (UtilDTO) this.getDtoUtil();
	}
}
