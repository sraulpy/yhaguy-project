package com.yhaguy.gestion.stock.recalculo;

import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.HistoricoRecalculoStock;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.process.ProcesosArticulos;

public class RecalculoStockViewModel extends SimpleViewModel {
	
	private String observacion = "";
	
	@Wire
	private Listbox listRecalc;
	
	@Wire
	private Window win;
	
	@Wire
	private Button btnRecalc;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * realiza el recalculo de stock..
	 */
	private void recalcularStock() 
		throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		HistoricoRecalculoStock hist = new HistoricoRecalculoStock();
		hist.setFecha(new Date());
		hist.setObservacion(observacion.isEmpty() ? "Sin Observacion.." : this.observacion);
		hist.setUsuario(this.getLoginNombre());
		rr.saveObject(hist, this.getLoginNombre());
		this.observacion = "";
		
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
			ProcesosArticulos.recalcularStock_(2, 2);
		} else {
			ProcesosArticulos.recalcularStock(2, 2);
		}		
		Clients.showNotification("STOCK RECALCULADO");
		this.btnRecalc.setDisabled(false);
		BindUtils.postNotifyChange(null, null, this, "*");
	}
	
	@Command
	@NotifyChange("*")
	public void test(@BindingParam("comp") Popup comp) throws Exception {
		comp.close();
		this.btnRecalc.setDisabled(true);
		Clients.showBusy(this.listRecalc, "Recalculando Stock...");
		Events.echoEvent("onLater", this.listRecalc, null);
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Command
	@NotifyChange("*")
	public void clearProgress() throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(listRecalc);
			}
		});
		timer.setParent(this.win);
		
		this.recalcularStock();
	}

	/**
	 * GETS / SETS
	 */
	
	public List<HistoricoRecalculoStock> getRecalculos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getHistoricoRecalculos();
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
}
