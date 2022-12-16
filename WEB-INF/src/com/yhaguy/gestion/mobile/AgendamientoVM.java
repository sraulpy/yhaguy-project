package com.yhaguy.gestion.mobile;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TareaProgramada;
import com.yhaguy.domain.TestA;
import com.yhaguy.util.Utiles;

public class AgendamientoVM extends SimpleViewModel {
	
	private Date desde;
	private Date hasta;
	
	private TareaProgramada selectedTarea;
	
	@Init(superclass = true)
	public void init() {
		this.desde = new Date();
		this.hasta = new Date();
	}
	
	@Command
	public void selectTarea() throws Exception {
		
		String lat = this.selectedTarea.getEmpresa().getLatitud();
		String lng = this.selectedTarea.getEmpresa().getLongitud();
		
		String url = "https://maps.google.com/?q=" + lat + "," + lng;
		
		TestA t = new TestA();
		t.setName(url);
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(t, "mobile");
	}
	
	@Command
	public void realizarTarea() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		TareaProgramada tarea = (TareaProgramada) rr.getObject(
				TareaProgramada.class.getName(), this.selectedTarea.getId());
		tarea.setRealizado(true);
		tarea.setRealizadoPor("mobile: " + Utiles.getDateToString(new Date(), "dd-MM-yyyy hh:mm:ss"));
		rr.saveObject(tarea, "mobile");
		Clients.showNotification("TAREA REALIZADA..");
	}
	
	/**
	 * @return las tareas programadas..
	 */
	@DependsOn({ "desde", "hasta" })
	public List<TareaProgramada> getTareas() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<TareaProgramada> tareas = rr.getTareasProgramadas(this.desde, this.hasta, "PASAR A COBRAR");			
		return tareas;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public TareaProgramada getSelectedTarea() {
		return selectedTarea;
	}

	public void setSelectedTarea(TareaProgramada selectedTarea) {
		this.selectedTarea = selectedTarea;
	}
}
