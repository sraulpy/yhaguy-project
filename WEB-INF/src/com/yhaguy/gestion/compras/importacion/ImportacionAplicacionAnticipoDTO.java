package com.yhaguy.gestion.compras.importacion;

import com.coreweb.dto.DTO;
import com.yhaguy.gestion.empresa.ctacte.CtaCteEmpresaMovimientoDTO;

@SuppressWarnings("serial")
public class ImportacionAplicacionAnticipoDTO extends DTO {

	private double importeGs = 0;
	private double importeDs = 0;
	
	private CtaCteEmpresaMovimientoDTO movimiento;

	public double getImporteGs() {
		return importeGs;
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = this.getMisc().redondeoCuatroDecimales(importeGs);
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = this.getMisc().redondeoCuatroDecimales(importeDs);
	}

	public CtaCteEmpresaMovimientoDTO getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(CtaCteEmpresaMovimientoDTO movimiento) {
		this.movimiento = movimiento;
	}	
}
