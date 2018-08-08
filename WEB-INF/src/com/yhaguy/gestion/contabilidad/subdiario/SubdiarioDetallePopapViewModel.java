package com.yhaguy.gestion.contabilidad.subdiario;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.Config;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.comun.ControlLogica;

public class SubdiarioDetallePopapViewModel extends SoloViewModel {

	private SubdiarioSimpleControl dato;
	private ControlLogica ctr = new ControlLogica(null);

	@Init(superclass = true)
	public void initSubdiarioDetallePopapViewModel(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) SubdiarioSimpleControl datoDetalle) {
		this.dato = datoDetalle;
	}

	@AfterCompose(superclass = true)
	public void afterComposeSubdiarioDetallePopapViewModel() {

	}

	@Override
	public String getAliasFormularioCorriente() {
		return this.dato.getAliasFormularioCorriente();
	}

	// comandos

	@NotifyChange("*")
	@Command
	public void debe() {
		this.dato.setDebe(true);
		this.dato.setHaber(false);
		this.getDato().getSubDiarioDetalleNuevo()
				.setTipo(Configuracion.CUENTA_DEBE_KEY);
	}

	@NotifyChange("*")
	@Command
	public void haber() {
		this.dato.setDebe(false);
		this.dato.setHaber(true);
		this.getDato().getSubDiarioDetalleNuevo()
				.setTipo(Configuracion.CUENTA_HABER_KEY);
	}

	@Command
	@NotifyChange("*")
	public void buscarCtaContable() throws Exception {
		String nombre = (String) this.dato.getSubDiarioDetalleNuevo()
				.getCuenta().getPos1();
		MyArray ctaContableEncontrada = this.ctr.buscarCuentaContable("",
				nombre, 1, null);
		if (ctaContableEncontrada != null) {
			this.dato.getSubDiarioDetalleNuevo().setCuenta(
					ctaContableEncontrada);
		} else {
			this.dato.getSubDiarioDetalleNuevo().setCuenta(
					this.dato.getSubDiarioDetalleNuevo().getCuenta());
		}
	}

	// Get and Set

	public SubdiarioSimpleControl getDato() {
		return dato;
	}

	public void setDato(SubdiarioSimpleControl dato) {
		this.dato = dato;
	}

}
