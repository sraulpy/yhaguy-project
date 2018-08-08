package com.yhaguy.gestion.venta.listaprecio;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;

public class VentaListaPrecioSimpleVM extends SoloViewModel {
	
	private VentaListaPrecioViewModel dato;
	
	private String razonSocialProveedorExterior = "";

	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) VentaListaPrecioViewModel dato) {
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
	@DependsOn("razonSocialProveedorExterior")
	public List<Proveedor> getProveedoresExterior() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedoresExterior(this.razonSocialProveedorExterior);
	}
	
	public VentaListaPrecioViewModel getDato() {
		return dato;
	}

	public void setDato(VentaListaPrecioViewModel dato) {
		this.dato = dato;
	}

	public String getRazonSocialProveedorExterior() {
		return razonSocialProveedorExterior;
	}

	public void setRazonSocialProveedorExterior(String razonSocialProveedorExterior) {
		this.razonSocialProveedorExterior = razonSocialProveedorExterior;
	}
	
}
