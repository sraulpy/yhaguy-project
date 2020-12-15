package com.yhaguy.gestion.rrhh;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class VisorMarcacionesVM extends SimpleViewModel {
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaYY = "";
	private String filterFuncionario = "";

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaYY = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return las marcaciones..
	 */
	@DependsOn({ "filterFechaDD", "filterFechaMM", "filterFechaYY", "filterFuncionario" })
	public List<Object[]> getMarcaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getMarcaciones(this.getFilterFecha(), this.filterFuncionario);
		for (Object[] item : out) {
			item[4] = this.getTipo((String) item[1]);
			item[5] = Utiles.getFecha(((String) item[1]).replaceAll("entrada_", "").replaceAll("salida_", "").replaceAll("interna_", ""));
		}
		return out;
	}
	
	/**
	 * @return el tipo
	 */
	private String getTipo(String descripcion) {
		String out = "";
		if (descripcion.startsWith("entrada")) {
			out = "ENTRADA";
		}
		if (descripcion.startsWith("entrada_interna")) {
			out = "ENTRADA INTERNA";
		}
		if (descripcion.startsWith("salida")) {
			out = "SALIDA";
		}
		if (descripcion.startsWith("salida_interna")) {
			out = "SALIDA INTERNA";
		}
		return out;
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaYY.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaYY.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaYY;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaYY;
		return this.filterFechaYY + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
	}

	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFechaDD) {
		this.filterFechaDD = filterFechaDD;
	}

	public String getFilterFechaMM() {
		return filterFechaMM;
	}

	public void setFilterFechaMM(String filterFechaMM) {
		this.filterFechaMM = filterFechaMM;
	}

	public String getFilterFechaYY() {
		return filterFechaYY;
	}

	public void setFilterFechaYY(String filterFechaYY) {
		this.filterFechaYY = filterFechaYY;
	}

	public String getFilterFuncionario() {
		return filterFuncionario;
	}

	public void setFilterFuncionario(String filterFuncionario) {
		this.filterFuncionario = filterFuncionario;
	}
}
