package com.yhaguy.gestion.empresa.ctacte.auditoria;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlCuentaCorriente;
import com.yhaguy.process.ProcesosTesoreria;
import com.yhaguy.util.Utiles;

public class AuditoriaSaldosViewModel extends SimpleViewModel {
	
	static final String CTA_GS = "Cta. Gs.";
	static final String CTA_DS = "Cta. USD.";
	
	private String filterRuc = "";
	private String filterRazonSocial = "";
	
	private String selectedMoneda = CTA_GS;
	
	private Object[] selectedEmpresa;
	private CtaCteEmpresaMovimiento selectedItem;
	
	private List<CtaCteEmpresaMovimiento> saldos;
	private double totalSaldo = 0;
	private double totalSaldo_ = 0;
	private double totalSaldoAplicado = 0;
	
	@Wire
	private Listbox listFac;
	
	@Wire
	private Window win;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void buscarSaldos() {
		Clients.showBusy(this.listFac, "Verificando aplicaciones de saldos...");
		Events.echoEvent("onLater", this.listFac, null);
	}
	
	@Command
	@NotifyChange("*")
	public void actualizarSaldo(@BindingParam("item") CtaCteEmpresaMovimiento item) throws Exception {
		ProcesosTesoreria.depurarSaldosPorVenta(item.getIdMovimientoOriginal());
		this.buscarSaldos();
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
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
				Clients.clearBusy(listFac);
			}
		});
		timer.setParent(this.win);
		buscarSaldos_();
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filterRuc", "filterRazonSocial" })
	public List<Object[]> getEmpresas() throws Exception {
		if (this.filterRuc.trim().isEmpty() && this.filterRazonSocial.trim().isEmpty()) {
			return new ArrayList<Object[]>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();		
		return rr.getEmpresas(this.filterRuc, this.filterRazonSocial);
	}
	
	/**
	 * busqueda de saldos..
	 */
	private void buscarSaldos_() throws Exception {
		this.totalSaldo = 0;
		this.totalSaldo_= 0;
		Date desde = Utiles.getFecha("01-01-2016 00:00:00");
		String siglaMoneda = this.selectedMoneda.equals(CTA_GS) ? Configuracion.SIGLA_MONEDA_GUARANI
				: Configuracion.SIGLA_MONEDA_DOLAR;
		RegisterDomain rr = RegisterDomain.getInstance();
		Map<Long, Double> acum = new HashMap<Long, Double>();
		List<CtaCteEmpresaMovimiento> list = rr.getSaldosCtaCte(((long) this.selectedEmpresa[0]), Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE,
				Configuracion.SIGLA_TM_FAC_VENTA_CREDITO, siglaMoneda, desde);
		List<CtaCteEmpresaMovimiento> list_ = new ArrayList<CtaCteEmpresaMovimiento>();
		for (CtaCteEmpresaMovimiento ctacte : list) {
			Double saldo = acum.get(ctacte.getIdMovimientoOriginal());
			if (saldo != null) {
				saldo += ctacte.getSaldo();
			} else {
				saldo = ctacte.getSaldo();
				list_.add(ctacte);
			}
			acum.put(ctacte.getIdMovimientoOriginal(), saldo);
		}
		for (CtaCteEmpresaMovimiento ctacte : list_) {
			ctacte.setSaldo(acum.get(ctacte.getIdMovimientoOriginal()));
			long idVenta = ctacte.getIdMovimientoOriginal();
			List<Object[]> aplicaciones = ControlCuentaCorriente.getAplicacionesVenta(idVenta);
			if (aplicaciones.size() > 0) {
				Object[] last = aplicaciones.get(aplicaciones.size() - 1);
				ctacte.setAux((double) last[7]);
			} else {
				ctacte.setAux(ctacte.getSaldo());
			}
			this.totalSaldo += ctacte.getSaldo();
			this.totalSaldo_ += ctacte.getAux();
		}
		this.saldos = list_;
		BindUtils.postNotifyChange(null, null, this, "saldos");
		BindUtils.postNotifyChange(null, null, this, "totalSaldo");
		BindUtils.postNotifyChange(null, null, this, "totalSaldo_");
	}
	
	@DependsOn("selectedItem")
	public List<Object[]> getAplicaciones() throws Exception {
		if (this.selectedItem == null) return new ArrayList<Object[]>();
		long idVenta = this.selectedItem.getIdMovimientoOriginal();
		List<Object[]> list = ControlCuentaCorriente.getAplicacionesVenta(idVenta);
		Object[] last = list.get(list.size() - 1);
		this.totalSaldoAplicado = (double) last[7];
		BindUtils.postNotifyChange(null, null, this, "totalSaldoAplicado");
		return list;
	}
	
	/**
	 * @return las monedas..
	 */
	public List<String> getMonedas() {
		List<String> out = new ArrayList<String>();
		out.add(CTA_GS);
		out.add(CTA_DS);
		return out;
	}

	public String getFilterRuc() {
		return filterRuc;
	}

	public void setFilterRuc(String filterRuc) {
		this.filterRuc = filterRuc;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public Object[] getSelectedEmpresa() {
		return selectedEmpresa;
	}

	public void setSelectedEmpresa(Object[] selectedEmpresa) {
		this.selectedEmpresa = selectedEmpresa;
	}

	public String getSelectedMoneda() {
		return selectedMoneda;
	}

	public void setSelectedMoneda(String selectedMoneda) {
		this.selectedMoneda = selectedMoneda;
	}

	public CtaCteEmpresaMovimiento getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(CtaCteEmpresaMovimiento selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<CtaCteEmpresaMovimiento> getSaldos() {
		return saldos;
	}

	public void setSaldos(List<CtaCteEmpresaMovimiento> saldos) {
		this.saldos = saldos;
	}

	public double getTotalSaldo() {
		return totalSaldo;
	}

	public void setTotalSaldo(double totalSaldo) {
		this.totalSaldo = totalSaldo;
	}

	public double getTotalSaldo_() {
		return totalSaldo_;
	}

	public void setTotalSaldo_(double totalSaldo_) {
		this.totalSaldo_ = totalSaldo_;
	}

	public double getTotalSaldoAplicado() {
		return totalSaldoAplicado;
	}

	public void setTotalSaldoAplicado(double totalSaldoAplicado) {
		this.totalSaldoAplicado = totalSaldoAplicado;
	}
}
