package com.yhaguy.gestion.contable.cotizaciones;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.extras.csv.CSV;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoCambio;
import com.yhaguy.process.ProcesosTesoreria;
import com.yhaguy.util.Utiles;

public class CotizacionesViewModel extends SimpleViewModel {
	
	static final String PATH = Configuracion.pathCotizaciones;
	static final String LIST_ACTUALIZADOS = "/yhaguy/gestion/contabilidad/cotizaciones/actualizados.zul";
	
	private TipoCambio nvaCotizacion;
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private Date desde;
	private Date hasta;
	
	private Window win;
	
	List<String[]> actualizados;

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
			this.inicializar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@SuppressWarnings("deprecation")
	@Command
	@NotifyChange("*")
	public void addCotizacion(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvaCotizacion.getFecha().setHours(0);
		this.nvaCotizacion.getFecha().setMinutes(0);
		this.nvaCotizacion.getFecha().setSeconds(0);
		rr.saveObject(this.nvaCotizacion, this.getLoginNombre());
		comp.close();
		Clients.showNotification("REGISTRO AGREGADO");
		this.inicializar();
	}
	
	@Command 
	@NotifyChange("*")
	public void uploadFileCotizaciones(@BindingParam("file") Media file) {
		try {
			Misc misc = new Misc();
			String name = "cotizaciones_" + Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY);
			boolean isText = "text/csv".equals(file.getContentType());
			InputStream file_ = new ByteArrayInputStream(isText ? file.getStringData().getBytes() : file.getByteData());
			misc.uploadFile(PATH, name, ".csv", file_);
			this.deleteCotizaciones();
			this.csvCotizaciones();
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	/**
	 * actualiza las cotizaciones..
	 */
	@Command
	@NotifyChange("actualizados")
	public void actualizarMovimientos() throws Exception {
		this.actualizados = ProcesosTesoreria.actualizarCotizacionesGastos(this.desde, this.hasta);
		this.win = (Window) Executions.createComponents(LIST_ACTUALIZADOS, this.mainComponent, null);
		this.win.doModal();
	}
	
	/**
	 * borra las cotizaciones..
	 */
	private void deleteCotizaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<TipoCambio> list = rr.getCotizaciones(this.desde, this.hasta);
		for (TipoCambio item : list) {
			rr.deleteObject(item);
		}
	}

	/**
	 * csv cotizaciones..
	 */
	private void csvCotizaciones() {
		try {			
			RegisterDomain rr = RegisterDomain.getInstance();
			Tipo moneda = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR);
			Tipo set = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CAMBIO_SET);
			String[][] cab = { { "Empresa", CSV.STRING } };
			String[][] det = { { "FECHA", CSV.STRING }, { "COMPRA", CSV.STRING }, { "VENTA", CSV.STRING } };	
			CSV csv = new CSV(cab, det, PATH + "cotizaciones_" + Utiles.getDateToString(new Date(), Utiles.DD_MM_YYYY) + ".csv", ';');
			csv.start();
			int size = 0;
			while (csv.hashNext()) {
				String fecha = csv.getDetalleString("FECHA");	
				String compra = csv.getDetalleString("COMPRA");
				String venta = csv.getDetalleString("VENTA");
				
				TipoCambio tc = new TipoCambio();
				tc.setVenta(Double.parseDouble(venta));
				tc.setCompra(Double.parseDouble(compra));
				tc.setFecha(Utiles.getFecha(fecha, Utiles.DD_MM_YYYY));
				tc.setMoneda(moneda);
				tc.setTipoCambio(set);	
				rr.saveObject(tc, this.getLoginNombre());
				size ++;
			}
			Clients.showNotification(size + " registror importados..");
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al leer el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}	
	
	/**
	 * inicializa los datos de cotizacion..
	 */
	private void inicializar() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.nvaCotizacion = new TipoCambio();
		this.nvaCotizacion.setFecha(new Date());
		this.nvaCotizacion.setTipoCambio(rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CAMBIO_SET));
		this.nvaCotizacion.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR));
	}

	/**
	 * GETS / SETS
	 */
	
	@DependsOn({"filterFechaDD", "filterFechaMM", "filterFechaAA" })
	public List<TipoCambio> getCotizaciones() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCotizaciones(this.getFilterFecha());
	}
	
	@DependsOn("actualizados")
	public String getFooter() {
		return this.actualizados.size() + " items";
	}
	
	/**
	 * @return cotizacion compra vigente..
	 */
	public double getTipoCambioCompra() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipoCambioCompra();
	}
	
	/**
	 * @return cotizacion venta vigente..
	 */
	public double getTipoCambioVenta() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getTipoCambioVenta();
	}
	
	/**
	 * @return el filtro de fecha..
	 */
	private String getFilterFecha() {
		if (this.filterFechaAA.isEmpty() && this.filterFechaDD.isEmpty() && this.filterFechaMM.isEmpty())
			return "";
		if (this.filterFechaAA.isEmpty())
			return this.filterFechaMM + "-" + this.filterFechaDD;
		if (this.filterFechaMM.isEmpty())
			return this.filterFechaAA;
		if (this.filterFechaMM.isEmpty() && this.filterFechaDD.isEmpty())
			return this.filterFechaAA;
		return this.filterFechaAA + "-" + this.filterFechaMM + "-" + this.filterFechaDD;
	}
	
	public String getFilterFechaDD() {
		return filterFechaDD;
	}

	public void setFilterFechaDD(String filterFechaDD) {
		this.filterFechaDD = filterFechaDD;
	}

	public String getFilterFechaMM() {
		return filterFechaMM;
	}

	public void setFilterFechaMM(String filterFechaMM) {
		this.filterFechaMM = filterFechaMM;
	}

	public String getFilterFechaAA() {
		return filterFechaAA;
	}

	public void setFilterFechaAA(String filterFechaAA) {
		this.filterFechaAA = filterFechaAA;
	}

	public TipoCambio getNvaCotizacion() {
		return nvaCotizacion;
	}

	public void setNvaCotizacion(TipoCambio nvaCotizacion) {
		this.nvaCotizacion = nvaCotizacion;
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

	public List<String[]> getActualizados() {
		return actualizados;
	}

	public void setActualizados(List<String[]> actualizados) {
		this.actualizados = actualizados;
	}
	
}
