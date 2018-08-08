package com.yhaguy.gestion.venta.ruteo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Div;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Usuario;
import com.yhaguy.domain.AccesoApp;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;

public class RuteoVendedoresViewModel extends SimpleViewModel {
	
	private Date desde;
	private Date hasta;
	
	private Funcionario selectedVendedor;
	
	@Wire
	private Borderlayout body;
	
	@Wire
	private Window win;
	
	@Wire
	private Div dv_mapa;

	@Init(superclass = true)
	public void init() {
		this.desde = new Date();
		this.hasta = new Date();
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void buscarVendedores() throws Exception {
		Clients.showBusy(this.dv_mapa, "Buscando registro de vendedores...");
		Events.echoEvent("onLater", this.dv_mapa, null);
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
				Clients.clearBusy(dv_mapa);
			}
		});
		timer.setParent(this.win);		
		this.buscarVendedores_();
	}
	
	@Command
	public void saveEmpresa(@BindingParam("emp") Empresa emp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(emp, this.getLoginNombre());
		Clients.showNotification("REGISTRO GUARDADO..");
	}
	
	@Command
	public void print() {
		Window test = (Window) Executions.createComponents("/yhaguy/gestion/empresa/print_map.zul", this.win, null);
		test.doModal();
	}
	
	/**
	 * busca los registros de vendedores..
	 */
	private void buscarVendedores_() throws Exception {
		// nada que hacer..
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return los vendedores..
	 */
	public List<Funcionario> getVendedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getVendedores();
	}
	
	/**
	 * @return el ruteo de vendedores..
	 */
	@DependsOn({ "selectedVendedor", "desde", "hasta" })
	public List<Object[]> getRuteoVendedores() throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> list = rr.getRuteoVendedores(this.desde, this.hasta);
		if (this.selectedVendedor == null) {
			out.addAll(list);
		} else {
			for (Object[] ruteo : list) {
				String vendedor = (String) ruteo[0];
				String vendedor_ = vendedor.substring(0, vendedor.indexOf('-'));
				List<AccesoApp> accs = new ArrayList<AccesoApp>();
				accs.addAll(this.selectedVendedor.getAccesos());
				if (accs.size() > 0) {
					Usuario user = accs.get(0).getUsuario();
					if (user != null) {
						String login = user.getLogin();
						if (login.equals(vendedor_)) {
							out.add(ruteo);
						}
					}				
				}
			}
		}
		
		for (Object[] obj : out) {
			System.out.println(obj[0]);
		}
		return out;
	}

	public Funcionario getSelectedVendedor() {
		return selectedVendedor;
	}

	public void setSelectedVendedor(Funcionario selectedVendedor) {
		this.selectedVendedor = selectedVendedor;
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
}
