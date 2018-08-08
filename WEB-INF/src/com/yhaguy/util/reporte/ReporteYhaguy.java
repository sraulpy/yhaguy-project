package com.yhaguy.util.reporte;

import com.coreweb.extras.reporte.DatosReporte;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;

public abstract class ReporteYhaguy extends DatosReporte {

	public Misc m = new Misc();
	@Override
	public void setDatosReportes() {
		
		String logo = Configuracion.DIRECTORIO_BASE_REAL+"/logo.png";
		
		this.setEmpresa(Configuracion.empresa);
		this.setLogoEmpresa(logo, 50, 30);
		
		this.informacionReporte();
	}
	
	
	public abstract void informacionReporte();
	
}
