package com.yhaguy.gestion.contable.asientos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.ContableAsiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlContable;
import com.yhaguy.util.Utiles;

public class AsientosViewModel extends SimpleViewModel {
	
	private String selectedAnho = Utiles.getAnhoActual();
	private MyArray selectedMes;
	
	@Wire
	private Window win_;
	
	@Wire
	private Vbox vl_asien;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void procesarAsientos() {
		Clients.showBusy(this.vl_asien, "PROCESANDO ASIENTOS CONTABLES DEL MES DE "+ this.selectedMes.getPos2() +" ...");
		Events.echoEvent("onLater", this.vl_asien, null);
	}
	
	/**
	 * procesa los asientos..
	 */
	@SuppressWarnings("deprecation")
	private void procesarAsientos_() throws Exception {
		MyArray actual = Utiles.getMesCorriente(Utiles.getAnhoActual());
		int mes = (int) actual.getPos4();
		int seleccionado = (int) this.selectedMes.getPos1();
		if (mes <= seleccionado) {
			Clients.showNotification("Solo se puede procesar asientos de meses anteriores al actual..");
			return;
		}
		Date desde = Utiles.getFechaInicioMes(seleccionado);
		Date hasta = Utiles.getFechaFinMes(seleccionado);
		desde.setSeconds(0);desde.setMinutes(0);desde.setHours(0);
		hasta.setSeconds(59);hasta.setMinutes(59);hasta.setHours(23);
		ControlContable.generarAsientos(desde, hasta, this.getLoginNombre());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	@NotifyChange("*")
	public void clearProgress() throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(vl_asien);
			}
		});
		timer.setParent(this.win_);		
		this.procesarAsientos_();
	}

	/**
	 * GETS / SETS
	 */
	
	@SuppressWarnings("unchecked")
	@DependsOn({ "selectedMes", "selectedAnho" })
	public List<ContableAsiento> getAsientos() throws Exception {
		if (this.selectedMes == null) {
			return new ArrayList<ContableAsiento>();
		}		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ContableAsiento> list = rr.getObjects(ContableAsiento.class.getName());	
		return list;
	}
	
	/**
	 * @return los anhos..
	 */
	public List<String> getAnhos() {
		return Utiles.getAnhos();
	}
	
	/**
	 * @return los meses..
	 */
	public List<MyArray> getMeses() {
		return Utiles.getMeses();
	}
	
	public String getSelectedAnho() {
		return selectedAnho;
	}

	public void setSelectedAnho(String selectedAnho) {
		this.selectedAnho = selectedAnho;
	}

	public MyArray getSelectedMes() {
		return selectedMes;
	}

	public void setSelectedMes(MyArray selectedMes) {
		this.selectedMes = selectedMes;
	}	
}
