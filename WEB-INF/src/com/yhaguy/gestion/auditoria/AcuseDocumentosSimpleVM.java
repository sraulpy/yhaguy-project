package com.yhaguy.gestion.auditoria;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.Config;
import com.coreweb.control.SoloViewModel;
import com.yhaguy.domain.AcuseDocumentoDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;

public class AcuseDocumentosSimpleVM extends SoloViewModel {

	private AcuseDocumentosViewModel dato;
	private AcuseDocumentoDetalle nvoDocumento = new AcuseDocumentoDetalle();

	@Init(superclass = true)
	public void init(@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) AcuseDocumentosViewModel dato) {
		this.dato = dato;
		this.setAliasFormularioCorriente(dato.getAliasFormularioCorriente());
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addDocumento() {
		this.dato.getNvoAcuse().getDetalles().add(this.nvoDocumento);
		this.nvoDocumento = new AcuseDocumentoDetalle();
	}
	
	/**
	 * @return los tipos de movimientos..
	 */
	public List<String> getTiposDeMovimientos() throws Exception {
		List<String> out = new ArrayList<String>();
		out.add("(VARIOS)");
		RegisterDomain rr = RegisterDomain.getInstance();
		List<TipoMovimiento> tms = rr.getTiposDeMovimientos();
		for (TipoMovimiento tm : tms) {
			out.add(tm.getDescripcion().toUpperCase());
		}
		return out;
	}

	public AcuseDocumentosViewModel getDato() {
		return dato;
	}

	public void setDato(AcuseDocumentosViewModel dato) {
		this.dato = dato;
	}

	public AcuseDocumentoDetalle getNvoDocumento() {
		return nvoDocumento;
	}

	public void setNvoDocumento(AcuseDocumentoDetalle nvoDocumento) {
		this.nvoDocumento = nvoDocumento;
	}	
}
