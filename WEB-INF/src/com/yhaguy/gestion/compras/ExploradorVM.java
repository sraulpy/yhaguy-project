package com.yhaguy.gestion.compras;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class ExploradorVM extends SimpleViewModel {
	
	static final String FILTRO_LOCALES = "LOCALES";
	static final String FILTRO_IMPORTACION = "IMPORTACIONES";
	
	private String selectedFiltro = FILTRO_LOCALES;	
	
	private Date desde;
	private Date hasta;
	private Date ventasDesde;
	private Date ventasHasta;
	
	private String filterNumero = "";
	private String filterRazonSocial = "";
	private String filterRuc = "";
	private String filterComprador = "";
	private String filterCodigo = "";
	private String filterFamilia = "";
	
	private double promedio = 0;
	private long totalCompras = 0;
	private long totalVentas = 0;
	
	@Wire
	private Listbox lst;
	
	@Wire
	private Window win;
		
	@Init(superclass = true)
	public void init() {
		try {
			this.desde = Utiles.getFechaInicioMes();
			this.hasta = new Date();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("selectedFiltro")
	public void selectFilter(@BindingParam("filter") int filter) {
		if (filter == 1) {
			this.selectedFiltro = FILTRO_LOCALES;
		} else if (filter == 2) {
			this.selectedFiltro = FILTRO_IMPORTACION;
		} 
	}
	
	@Command
	public void buscarVentas() {
		Clients.showBusy(this.lst, "Procesando ventas...");
		Events.echoEvent("onLater", this.lst, null);
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void clearProgress(@BindingParam("items") List<Object[]> items) throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(lst);
			}
		});
		timer.setParent(this.win);		
		this.procesarVentas(items);
	}
	
	/**
	 * procesa las ventas..
	 */
	private void procesarVentas(List<Object[]> items) throws Exception {		
		if (this.ventasDesde == null || this.ventasHasta == null) {
			return;
		}
		this.totalVentas = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Object[] item : items) {
			String cod = (String) item[6];
			List<Object[]> list = rr.getVentasDetallado(this.ventasDesde, this.ventasHasta, cod);
			if (list.size() > 0) {
				item[10] = list.get(0)[1];
				long c = (long) item[9];
				long v = (long) item[10];
				item[11] = Utiles.obtenerPorcentajeDelValor(v, c);
				this.totalVentas += (long) item[10];
			}
		}
		
		this.promedio = Utiles.obtenerPorcentajeDelValor(this.totalVentas, this.totalCompras);
		
		BindUtils.postNotifyChange(null, null, items, "*");
		BindUtils.postNotifyChange(null, null, this, "promedio");
		BindUtils.postNotifyChange(null, null, this, "totalVentas");
	}	
	
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "desde", "hasta", "filterNumero", "filterRazonSocial", "filterRuc", "selectedFiltro",
			"filterComprador", "filterCodigo", "filterFamilia" })
	public List<Object[]> getCompras() throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();

		if (this.desde == null || this.hasta == null) {
			return out;
		}

		RegisterDomain rr = RegisterDomain.getInstance();

		if (this.selectedFiltro.equals(FILTRO_LOCALES)) {
			out = rr.getComprasLocales(this.desde, this.hasta, this.filterNumero, this.filterRuc,
					this.filterRazonSocial, this.filterComprador, this.filterCodigo, this.filterFamilia);
		}
		
		this.totalCompras = 0;
		this.totalVentas = 0;
		this.promedio = 0;
		for (Object[] c : out) {
			this.totalCompras += (long) c[9];
		}	
		BindUtils.postNotifyChange(null, null, this, "totalCompras");
		BindUtils.postNotifyChange(null, null, this, "promedio");
		BindUtils.postNotifyChange(null, null, this, "totalVentas");
		
		return out;
	}

	public String getSelectedFiltro() {
		return selectedFiltro;
	}

	public void setSelectedFiltro(String selectedFiltro) {
		this.selectedFiltro = selectedFiltro;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public String getFilterRuc() {
		return filterRuc;
	}

	public void setFilterRuc(String filterRuc) {
		this.filterRuc = filterRuc;
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

	public String getFilterComprador() {
		return filterComprador;
	}

	public void setFilterComprador(String filterComprador) {
		this.filterComprador = filterComprador;
	}

	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}

	public String getFilterFamilia() {
		return filterFamilia;
	}

	public void setFilterFamilia(String filterFamilia) {
		this.filterFamilia = filterFamilia;
	}

	public Date getVentasDesde() {
		return ventasDesde;
	}

	public void setVentasDesde(Date ventasDesde) {
		this.ventasDesde = ventasDesde;
	}

	public Date getVentasHasta() {
		return ventasHasta;
	}

	public void setVentasHasta(Date ventasHasta) {
		this.ventasHasta = ventasHasta;
	}

	public double getPromedio() {
		return promedio;
	}

	public void setPromedio(double promedio) {
		this.promedio = promedio;
	}

	public long getTotalCompras() {
		return totalCompras;
	}

	public void setTotalCompras(long totalCompras) {
		this.totalCompras = totalCompras;
	}

	public long getTotalVentas() {
		return totalVentas;
	}

	public void setTotalVentas(long totalVentas) {
		this.totalVentas = totalVentas;
	}
}
