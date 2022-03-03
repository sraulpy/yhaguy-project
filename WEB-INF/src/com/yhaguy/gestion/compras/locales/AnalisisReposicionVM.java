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
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AnalisisReposicion;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class AnalisisReposicionVM extends SimpleViewModel {
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaYY = "";
	
	private AnalisisReposicion analisis;
	private String razonSocialProveedor = "";

	@Init(superclass = true)
	public void init() {
		try {
			this.analisis = new AnalisisReposicion();
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

		ReporteAnalisisReposicion rep = new ReporteAnalisisReposicion(desde,
				hasta, getAcceso().getSucursalOperativa().getText());
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
	private String sucursal;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Nro.", TIPO_INTEGER, 20);
	static DatosColumnas col2 = new DatosColumnas("Código", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Ventas", TIPO_DOUBLE);
	static DatosColumnas col4 = new DatosColumnas("Ped. Reposicion", TIPO_DOUBLE);
	static DatosColumnas col5 = new DatosColumnas("Compras", TIPO_DOUBLE);

	public ReporteAnalisisReposicion(Date desde, Date hasta, String sucursal) {
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
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
						m.dateToString(this.hasta, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
