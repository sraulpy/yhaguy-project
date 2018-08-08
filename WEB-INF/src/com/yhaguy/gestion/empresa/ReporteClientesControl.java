package com.yhaguy.gestion.empresa;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.*;

import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.yhaguy.gestion.empresa.ReporteClientesDepuracion;

public class ReporteClientesControl  extends SimpleViewModel{

	Date fechaDesde = this.m.getFechaHoy00();
	Date fechaHasta = this.m.getFechaManana();
	boolean prioridad = true;
	boolean soloCompleto = false;
	
	
	@Init(superclass = true)
	public void initReporteClientesControl() {
	}

	@AfterCompose(superclass = true)
	public void afterComposeReporteClientesControl() {
	}
	
	
	@Command
	@NotifyChange ("*")
	public void generarReporte() throws Exception {
		
		this.fechaDesde = this.m.toFecha0000(this.fechaDesde);
		this.fechaHasta = this.m.toFecha2400(this.fechaHasta);
		
		ReporteClientesDepuracion rep =  new ReporteClientesDepuracion();
		rep.reportePrioridad(this.fechaDesde, this.fechaHasta, this.prioridad, this.soloCompleto);
		
		ViewPdf vp = new ViewPdf();
		vp.showReporte(rep, this);
		
	}
	

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public boolean isPrioridad() {
		return prioridad;
	}

	public void setPrioridad(boolean prioridad) {
		this.prioridad = prioridad;
	}

	public boolean isSoloCompleto() {
		return soloCompleto;
	}

	public void setSoloCompleto(boolean soloCompleto) {
		this.soloCompleto = soloCompleto;
	}
	
}
