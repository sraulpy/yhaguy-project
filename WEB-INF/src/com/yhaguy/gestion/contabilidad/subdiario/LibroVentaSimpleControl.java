package com.yhaguy.gestion.contabilidad.subdiario;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Execution;

import com.coreweb.Config;
import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;

public class LibroVentaSimpleControl extends SimpleViewModel{
	
	BrowserLibroVenta blv = null;
	private Date fechaDesde = new Date();
	private Date fechaHasta = new Date();
	private List<Object[]> data;
	boolean visible = false;

	@Init(superclass = true)
	public void initLibroVentaSimpleControl(
			@ContextParam(ContextType.EXECUTION) Execution execution)
			throws Exception {

		this.blv = (BrowserLibroVenta) execution
				.getAttribute(Config.BROWSER2_VM);
		this.blv.setWhere(" vf.id = 0 ");
		this.blv.refreshBrowser();	

	}

	@AfterCompose(superclass = true)
	public void afterComposeLibroVentaSimpleControl() {
	}

	@NotifyChange("*")
	@Command
	public void buscar() throws Exception {
		
		Hashtable<String, Object> params = new Hashtable<>();
		params.put(":fdesde", this.m.toFecha0000(getFechaDesde()));
		params.put(":fhasta", this.m.toFecha2400(getFechaHasta()));
		
		this.blv.setWhere(" vf.emision between :fdesde and :fhasta ", params);
		this.blv.refreshBrowser();
		this.data = this.blv.getDatos();
		if(this.data.size()>0){
			setVisible(true);
		}else{
			setVisible(false);
		}
		
	}
	
	@NotifyChange("*")
	@Command
	public void reporteLibroVenta(){
		ReporteLibroVenta repoLV = new ReporteLibroVenta();
		repoLV.setDatosReporte(data);
		repoLV.setFechaDeste(this.m.dateToString(getFechaDesde(), this.m.DD_MM_YYYY));
		repoLV.setFechaHasta(this.m.dateToString(getFechaHasta(), this.m.DD_MM_YYYY));
		repoLV.setApaisada();
		
		ViewPdf vp = new ViewPdf();
		vp.showReporte(repoLV, this);
		
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

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	


}
