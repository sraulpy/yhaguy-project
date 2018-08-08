package com.yhaguy.gestion.rrhh;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.HistoricoComisiones;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;
import com.yhaguy.process.ProcesosVentas;
import com.yhaguy.util.Utiles;

public class ComisionesVentasViewModel extends SimpleViewModel {

	private String selectedAnho = Utiles.getAnhoActual();
	private MyArray selectedMes;
	private String selectedVendedor;
	private String filterVendedor = "";
	
	private Object[] selectedFormato;
	
	private Window win;
	
	@Wire
	private Window win_;
	
	@Wire
	private Vbox vl_coms;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@SuppressWarnings("unchecked")
	@Command
	public void updateComision(@BindingParam("item") HistoricoComisiones item, 
			@BindingParam("det") Object[] det) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		rr.saveObject(item, this.getLoginNombre());
		double totalComision = 0;
		List<HistoricoComisiones> list = (List<HistoricoComisiones>) det[1];
		for (HistoricoComisiones com : list) {
			totalComision += com.getTotalComision();
		}
		det[3] = totalComision;
		BindUtils.postNotifyChange(null, null, item, "*");
		BindUtils.postNotifyChange(null, null, det, "*");
	}
	
	@Command
	public void procesarMovimientos_() {
		Clients.showBusy(this.vl_coms, "PROCESANDO VENTAS Y COBRANZAS POR VENDEDOR Y POR PROVEEDOR...");
		Events.echoEvent("onLater", this.vl_coms, null);
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
				Clients.clearBusy(vl_coms);
			}
		});
		timer.setParent(this.win_);
		
		this.procesarMovimientos();
	}
	
	/**
	 * procesa los movimientos..
	 */
	@SuppressWarnings("deprecation")
	public void procesarMovimientos() throws Exception {
		MyArray actual = Utiles.getMesCorriente(Utiles.getAnhoActual());
		int mes = (int) actual.getPos4();
		int seleccionado = (int) this.selectedMes.getPos1();
		long idSuc = this.getAcceso().getSucursalOperativa().getId();
		if (mes <= seleccionado) {
			Clients.showNotification("Solo se puede procesar movimientos de meses anteriores al actual..");
			return;
		}
		Date desde = Utiles.getFechaInicioMes(seleccionado);
		Date hasta = Utiles.getFechaFinMes(seleccionado);
		desde.setSeconds(0);desde.setMinutes(0);desde.setHours(0);
		hasta.setSeconds(59);hasta.setMinutes(59);hasta.setHours(23);
		ProcesosVentas.addHistoricoComisiones(desde, hasta, idSuc);
	}
	
	@Command
	public void verReporte() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<HistoricoComisiones> list = rr.getHistoricoComisiones((int) this.selectedMes.getPos1(), this.selectedAnho);
		List<MyArray> values = new ArrayList<MyArray>();

		for (HistoricoComisiones com : list) {
			values.add(new MyArray(
					com.getVendedor(),
					com.getProveedor(),
					Utiles.getRedondeo(com.getImporteVenta()), Utiles.getRedondeo(com.getComisionVenta()),
					Utiles.getRedondeo(com.getImporteCobro()), Utiles.getRedondeo(com.getComisionCobro()),
					Utiles.getRedondeo(com.getImporteNotaCredito()),
					Utiles.getRedondeo(com.getTotalSaldoGs()),
					Utiles.getRedondeo(com.getTotalComision())));
		}
		
		Collections.sort(values, new Comparator<MyArray>() {
			@Override
			public int compare(MyArray o1, MyArray o2) {
				String v1 = (String) o1.getPos1();
				String v2 = (String) o2.getPos1();
				int compare = v1.compareTo(v2);				
				return compare;
			}
		});
		
		String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_COMISION_COBRO;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ComisionCobroDataSource(values);
		params.put("Titulo", "Comisión por Ventas Cobradas (S/iva)");
		params.put("Usuario", getUs().getNombre());
		params.put("Vendedor", "TODOS..");
		params.put("mes", this.selectedMes.getPos2());
		params.put("anho", this.selectedAnho);
		imprimirJasper(source, params, dataSource, this.selectedFormato);
	}
	
	/**
	 * Despliega el reporte en un pdf para su impresion..
	 */
	public void imprimirJasper(String source, Map<String, Object> parametros,
			JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions
				.createComponents(
						com.yhaguy.gestion.reportes.formularios.ReportesViewModel.ZUL_REPORTES,
						this.mainComponent, params);
		this.win.doModal();
	}

	/**
	 * GETS / SETS
	 */
		
	@DependsOn({ "selectedMes", "selectedAnho", "filterVendedor" })
	public List<Object[]> getComisiones() throws Exception {
		List<Object[]> out = new ArrayList<Object[]>();
		if (this.selectedMes == null) {
			return out;
		}		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<HistoricoComisiones> list = rr.getHistoricoComisiones((int) this.selectedMes.getPos1(), this.selectedAnho);		
		Map<String, ArrayList<HistoricoComisiones>> datos = new HashMap<String, ArrayList<HistoricoComisiones>>();
		
		for (HistoricoComisiones com : list) {
			if (datos.get(com.getVendedor()) != null) {
				ArrayList<HistoricoComisiones> l = datos.get(com.getVendedor());
				l.add(com);
				datos.put(com.getVendedor(), l);
			} else {
				ArrayList<HistoricoComisiones> l = new ArrayList<HistoricoComisiones>();
				l.add(com);
				datos.put(com.getVendedor(), l);
			}
		}
		
		for (String key : datos.keySet()) {
			double saldo = 0;
			double comision = 0;
			double meta = 0;
			double ventas = 0;
			List<HistoricoComisiones> coms = datos.get(key);
			for (HistoricoComisiones c : coms) {
				saldo += c.getTotalSaldoGs();
				comision += c.getTotalComision();
			}
			//Funcionario func = rr.getFuncionario(key);
			//int mes = (int) this.selectedMes.getPos1();
			//meta = func.getMeta((int) this.selectedMes.getPos1());
			//ventas = func.getTotalVentas(Utiles.getFechaInicioMes(mes), Utiles.getFechaFinMes(mes));
			Object[] com = new Object[]{ key, coms, saldo, comision, meta, ventas, (meta - ventas) };
			out.add(com);
		}
		
		// prepara el filtro..
		List<Object[]> out_ = new ArrayList<Object[]>();
		for (Object[] object : out) {
			String vend = (String) object[0];
			if (vend.toLowerCase().contains(this.filterVendedor.toLowerCase())) {
				out_.add(object);
			}
		}
		
		// ordena la lista..
		Collections.sort(out_, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String c1 = (String) o1[0];
				String c2 = (String) o2[0];
				return c1.compareTo(c2);
			}
		});
		
		return out_;
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
	
	/**
	 * @return los vendedores..
	 */
	public List<String> getVendedores() throws Exception {
		List<String> out = new ArrayList<String>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Funcionario> vends = rr.getVendedores();
		for (Funcionario vend : vends) {
			out.add(vend.getRazonSocial());
		}
		// ordena la lista..
		Collections.sort(out, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return out;
	}
	
	/**
	 * @return los formatos de reporte..
	 */
	public List<Object[]> getFormatos() {
		List<Object[]> out = new ArrayList<Object[]>();
		out.add(ReportesViewModel.FORMAT_PDF);
		out.add(ReportesViewModel.FORMAT_XLS);
		out.add(ReportesViewModel.FORMAT_CSV);
		return out;
	}
	
	/**
	 * @return las familias..
	 */
	public List<Tipo> getFamiliasArticulos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Tipo> out = rr.getTipos(Configuracion.ID_TIPO_ARTICULO_FAMILIA);
		out.remove(0);
		return out;
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

	public String getSelectedVendedor() {
		return selectedVendedor;
	}

	public void setSelectedVendedor(String selectedVendedor) {
		this.selectedVendedor = selectedVendedor;
	}

	public String getFilterVendedor() {
		return filterVendedor;
	}

	public void setFilterVendedor(String filterVendedor) {
		this.filterVendedor = filterVendedor;
	}

	public Object[] getSelectedFormato() {
		return selectedFormato;
	}

	public void setSelectedFormato(Object[] selectedFormato) {
		this.selectedFormato = selectedFormato;
	}	
}

/**
 * DataSource de Comision por cobros..
 */
class ComisionCobroDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<MyArray> values = new ArrayList<MyArray>();
	HashMap<String, Double> totalSaldo = new HashMap<String, Double>();
	Misc misc = new Misc();
	
	public ComisionCobroDataSource(List<MyArray> values) {
		this.values = values;
		for (MyArray value : this.values) {
			Double saldo = totalSaldo.get(value.getPos1());
			if (saldo == null) {
				this.totalSaldo.put((String) value.getPos1(),
						(Double) value.getPos9());
			} else {
				this.totalSaldo.put((String) value.getPos1(), saldo
						+ (Double) value.getPos9());
			}
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		MyArray det = this.values.get(index);

		if ("TituloDetalle".equals(fieldName)) {
			value = det.getPos1();
		} else if ("Proveedor".equals(fieldName)) {
			value = det.getPos2();
		} else if ("ImporteVenta".equals(fieldName)) {
			double venta = (double) det.getPos3();
			value = FORMATTER.format(venta);
		} else if ("ComisionVenta".equals(fieldName)) {
			double com_venta = (double) det.getPos4();
			value = FORMATTER.format(com_venta);
		} else if ("ImporteCobro".equals(fieldName)) {
			double cobro = (double) det.getPos5();
			value = FORMATTER.format(cobro);
		} else if ("ComisionCobro".equals(fieldName)) {
			double com_cobro = (double) det.getPos6();
			value = FORMATTER.format(com_cobro);
		} else if ("ImporteNC".equals(fieldName)) {
			double nc = (double) det.getPos5();
			value = FORMATTER.format(nc);
		} else if ("SaldoGs".equals(fieldName)) {
			double saldo = (double) det.getPos8();
			value = FORMATTER.format(saldo);
		} else if ("ComisionGs".equals(fieldName)) {
			double saldo = (double) det.getPos9();
			value = FORMATTER.format(saldo);
		} else if ("TotalImporte".equals(fieldName)) {
			double total = this.totalSaldo.get(det.getPos1());
			value = FORMATTER.format(total);
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}
