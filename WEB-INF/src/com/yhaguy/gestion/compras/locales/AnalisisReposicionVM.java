package com.yhaguy.gestion.compras.locales;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AnalisisReposicion;
import com.yhaguy.domain.AnalisisReposicionDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class AnalisisReposicionVM extends SimpleViewModel {
	
	static final String ZUL_DETALLE = "/yhaguy/gestion/compras/locales/analisis_reposicion_detalle.zul";
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaYY = "";
	
	private AnalisisReposicion analisis;
	private String razonSocialProveedor = "";
	
	private Window win;

	@Init(superclass = true)
	public void init() {
		try {
			this.analisis = new AnalisisReposicion();
			this.analisis.setTipoRanking(AnalisisReposicion.POR_UNIDADES);
			this.analisis.setIncluirDevoluciones(false);
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaYY = Utiles.getAnhoActual();
			if (this.filterFechaMM.length() == 1) {
				this.filterFechaMM = "0" + this.filterFechaMM;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	public void ejecutar() {
		try {
			this.generarInforme();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * generacion del informe..
	 */
	private void generarInforme() throws Exception {
		Date desde = this.analisis.getDesde();
		Date hasta = this.analisis.getHasta();
		Proveedor prov = this.analisis.getProveedor();
		
		if (desde == null || hasta == null || prov == null) {
			Clients.showNotification("DEBE COMPLETAR LOS PARÁMETROS..", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		
		this.analisis.getDetalles().clear();

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> data = new ArrayList<Object[]>();
		List<Object[]> ventas = rr.getVentasDetallado_(desde, hasta, prov.getId());
		List<Object[]> repos = rr.getArticuloReposicionesDetallado(desde, hasta, prov.getId());
		List<Object[]> compras = rr.getComprasLocalesDetallado_(desde, hasta, prov.getId());
		Map<String, Double> mapRepos = new HashMap<String, Double>();
		Map<String, Double> mapCompras = new HashMap<String, Double>();
		
		for (Object[] obj : repos) {
			String key = (String) obj[1];
			Double value = (Double) obj[2];
			mapRepos.put(key, value);
		}
		
		for (Object[] obj : compras) {
			String key = (String) obj[1];
			Double value = (Double) obj[2];
			mapCompras.put(key, value);
		}

		for (int i = 0; i < ventas.size(); i++) {
			Object[] venta = ventas.get(i);
			Double rep = mapRepos.get(venta[1]);
			if (rep == null) {
				rep = 0.0;
			}
			Double com = mapCompras.get(venta[1]);
			if (com == null) {
				com = 0.0;
			}
			data.add(new Object[] { i + 1, venta[1], venta[2], rep, com });
		}
		
		for (Object[] item : data) {
			AnalisisReposicionDetalle det = new AnalisisReposicionDetalle();
			det.setRanking((int) item[0]);
			det.setCodigoInterno((String) item[1]);
			det.setVentasUnidades((double) item[2]);
			det.setVentasImporte(0.0);
			det.setPedidoReposicion((double) item[3]);
			det.setComprasUnidades((double) item[4]);
			det.setSugerido(0.0);
			det.setObservacion("");
			this.analisis.getDetalles().add(det);
		}
		
		this.win = (Window) Executions.createComponents(ZUL_DETALLE, this.mainComponent, null);
		win.doModal();
	}
	
	@Command
	public void confirmar() {
		Date desde = this.analisis.getDesde();
		Date hasta = this.analisis.getHasta();
		
		List<Object[]> data = new ArrayList<Object[]>();
		for (AnalisisReposicionDetalle item : this.analisis.getDetallesOrdenado()) {
			data.add(new Object[] { item.getRanking(), item.getCodigoInterno(), item.getVentasUnidades(),
					item.getPedidoReposicion(), item.getComprasUnidades(), item.getSugerido(), item.getObservacion() });
		}
		
		this.win.detach();
		
		ReporteAnalisisReposicion rep = new ReporteAnalisisReposicion(desde,
				hasta, this.analisis.getProveedor().getRazonSocial(), this.analisis.getTipoRanking());
		rep.setDatosReporte(data);
		rep.setApaisada();
		rep.setLegal();
		
		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, AnalisisReposicionVM.this);
	}

	/**
	 * GETS AND SETS
	 */
	
	@DependsOn("razonSocialProveedor")
	public List<Proveedor> getProveedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getProveedores(this.razonSocialProveedor);
	}
	
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		AccesoDTO out = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		if (out == null) {
			try {
				AssemblerAcceso as = new AssemblerAcceso();
				out = (AccesoDTO) as.obtenerAccesoDTO(Configuracion.USER_MOBILE);
				s.setAttribute(Configuracion.ACCESO, out);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}			
		return out;
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

	public String getFilterFechaYY() {
		return filterFechaYY;
	}

	public void setFilterFechaYY(String filterFechaYY) {
		this.filterFechaYY = filterFechaYY;
	}

	public AnalisisReposicion getAnalisis() {
		return analisis;
	}

	public void setAnalisis(AnalisisReposicion analisis) {
		this.analisis = analisis;
	}

	public String getRazonSocialProveedor() {
		return razonSocialProveedor;
	}

	public void setRazonSocialProveedor(String razonSocialProveedor) {
		this.razonSocialProveedor = razonSocialProveedor;
	}
	
}

/**
 * Reporte de analisis reposicion..
 */
class ReporteAnalisisReposicion extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;
	private String proveedor;
	private String tipoRanking;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Ranking", TIPO_INTEGER, 25);
	static DatosColumnas col2 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ventas", TIPO_DOUBLE, 20);
	static DatosColumnas col4 = new DatosColumnas("Ped.Rep.", TIPO_DOUBLE, 20);
	static DatosColumnas col5 = new DatosColumnas("Compras", TIPO_DOUBLE, 20);
	static DatosColumnas col6 = new DatosColumnas("Sugerido", TIPO_DOUBLE, 20);
	static DatosColumnas col7 = new DatosColumnas("Observación", TIPO_STRING);

	public ReporteAnalisisReposicion(Date desde, Date hasta, String proveedor, String tipoRanking) {
		this.desde = desde;
		this.hasta = hasta;
		this.proveedor = proveedor;
		this.tipoRanking = tipoRanking;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Analisis de Reposicion");
		this.setDirectorio("compras");
		this.setNombreArchivo("Analisis-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Desde",
						m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta",
						m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp
				.horizontalFlowList()
				.add(this.textoParValor("Proveedor", this.proveedor)).add(this.textoParValor("Ranking", this.tipoRanking)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
