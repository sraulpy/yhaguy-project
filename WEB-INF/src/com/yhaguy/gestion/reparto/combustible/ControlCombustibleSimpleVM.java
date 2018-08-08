package com.yhaguy.gestion.reparto.combustible;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;

public class ControlCombustibleSimpleVM extends SoloViewModel {
		
	private ControlCombustibleViewModel dato;

	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) ControlCombustibleViewModel dato) {
		this.dato = dato;
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * GETS / SETS
	 */
	public ControlCombustibleViewModel getDato() {
		return dato;
	}

	public void setDato(ControlCombustibleViewModel dato) {
		this.dato = dato;
	}
	
	@DependsOn({ "dato.nvoCombustible.pos4", "dato.nvoCombustible.pos5" })
	public double getImporteGs() {
		double litros = (double) this.dato.getNvoCombustible().getPos4();
		double costoGs = (double) this.dato.getNvoCombustible().getPos5();
		return litros * costoGs;
	}
	
	/**
	 * @return los tipos de combustible..
	 */
	public List<MyPair> getTiposDeCombustible() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Tipo> combs = rr.getTipos(Configuracion.ID_TIPO_COMBUSTIBLE);
		for (Tipo comb : combs) {
			out.add(new MyPair(comb.getId(), comb.getDescripcion(), comb.getSigla()));
		}
		return out;
	}
}
