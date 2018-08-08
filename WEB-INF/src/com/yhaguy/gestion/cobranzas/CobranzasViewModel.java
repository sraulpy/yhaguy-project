package com.yhaguy.gestion.cobranzas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.api.RenderContext;
import org.zkoss.calendar.impl.SimpleCalendarEvent;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.LlamadaCobranza;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Sucursal;
import com.yhaguy.domain.TareaProgramada;
import com.yhaguy.util.reporte.ReporteYhaguy;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CobranzasViewModel extends SimpleViewModel {
	
	static final String VER_CLIENTES = "Clientes";
	static final String VER_TAREAS = "Tareas";
	static final String VER_LLAMADAS = "Llamadas";
	static final String VER_CALENDARIO = "Calendario";
	
	static final String CAL_DIA = "Día";
	static final String CAL_SEMANA = "Semana";
	static final String CAL_MES = "Mes";
	
	static final String ZUL_LLAMADA = "/yhaguy/gestion/cobranzas/llamadas.zul";
	
	private String filter_ruc = "";
	private String filter_razonsocial = "";
	
	private List<MyArray> clientes;
	
	private MyArray selectedItem;
	private MyArray selectedTarea;
	private MyArray nuevaLlamada;
	
	private String selectedVista = VER_CLIENTES;
	private String selectedCalendario = CAL_MES;
	private String selectedResultado;
	
	private Date desde = new Date();
	private Date hasta = new Date();
	
	private boolean verPendientes = false;
	
	private DemoCalendarModel calendarModel;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
		try {
			calendarModel = new DemoCalendarModel(new DemoCalendarData(this.getTodasLasTareasProgramadas()).getCalendarEvents());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Wire
	private Window win;
	
	@Wire
	private Listbox listCli;
	
	@Wire
	private Popup pop_coment;
	
	@Wire
	private Label coment;
	
	/******************** COMANDOS ********************/
	
	@Command
	@NotifyChange("*")
	public void buscarClientes() throws Exception {
		Clients.showBusy(this.listCli, "Buscando Clientes con facturas vencidas...");
		Events.echoEvent("onLater", this.listCli, null);
	}
	
	@Command
	@NotifyChange({"*"})
	public void registrarLlamada() throws Exception {
		this.selectedResultado = null;
		this.nuevaLlamada = new MyArray();
		this.nuevaLlamada.setPos1(this.selectedItem);
		this.nuevaLlamada.setPos2(new Date());
		this.nuevaLlamada.setPos3("");
		this.nuevaLlamada.setPos4(new Date());
		WindowPopup wp = new WindowPopup();
		wp.setCheckAC(null);
		wp.setDato(this);
		wp.setHigth("430px");
		wp.setWidth("400px");
		wp.setModo(WindowPopup.NUEVO);
		wp.setTitulo("Registro de llamada al Cliente..");
		wp.show(ZUL_LLAMADA);
		if (wp.isClickAceptar()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			Empresa empresa = rr.getEmpresaById(this.selectedItem.getId());
			LlamadaCobranza lc = new LlamadaCobranza();
			lc.setDetalle(((String) this.nuevaLlamada.getPos3()).toUpperCase());
			lc.setEmpresa(empresa);
			lc.setFecha(new Date());
			lc.setResultado(this.selectedResultado);
			lc.setUsuario(this.getUs().getNombre());
			rr.saveObject(lc, this.getLoginNombre());
			
			if (!this.selectedResultado.equals(LlamadaCobranza.NO_RESPONDE)) {
				TareaProgramada tarea = new TareaProgramada();
				tarea.setEmpresa(empresa);
				tarea.setFecha((Date) this.nuevaLlamada.getPos4());
				tarea.setTarea(lc.getResultado());
				tarea.setObservacion(lc.getDetalle());
				tarea.setRealizado(false);
				rr.saveObject(tarea, this.getLoginNombre());
			}
			
			Clients.showNotification("Registro Guardado..");
		}
	}
	
	@Command
	@NotifyChange("*")
	public void realizarTarea() throws Exception {
		boolean realizado = (boolean) this.selectedTarea.getPos3();
		if (realizado) {
			Clients.showNotification("LA TAREA YA FUE REALIZADA POR "
					+ this.selectedTarea.getPos5().toString().toUpperCase(),
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		if (this.mensajeSiNo("Realizar la tarea seleccionada..?") == false)
			return;
		RegisterDomain rr = RegisterDomain.getInstance();
		TareaProgramada tarea = (TareaProgramada) rr.getObject(
				TareaProgramada.class.getName(), this.selectedTarea.getId());
		tarea.setRealizado(true);
		tarea.setRealizadoPor(this.getUs().getNombre());
		rr.saveObject(tarea, this.getLoginNombre());
		Clients.showNotification("TAREA REALIZADA..");
		
		this.selectedTarea = null;
	}
	
	@Command
	public void registrarTarea() {
		Clients.showNotification("Registrar tarea..");
	}
	
	@Command
	public void verComentario(@BindingParam("item") MyArray item, 
			@BindingParam("comp") Component comp) {
		this.coment.setValue((String) item.getPos2());
		this.pop_coment.open(comp, "after_end");
	}
	
	/**
	 * Cierra la ventana de progreso..
	 */
	@Command
	@NotifyChange("*")
	public void clearProgress() throws Exception {
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(false);

		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			@Override
			public void onEvent(Event evt) throws Exception {
				Clients.clearBusy(listCli);
			}
		});
		timer.setParent(this.win);
		
		this.buscarClientes_();
	}
	
	@Command
	@NotifyChange("selectedVista")
	public void selectFilter(@BindingParam("filter") int filter) {
		if (filter == 1) {
			this.selectedVista = VER_CLIENTES;
		} else if (filter == 2) {
			this.selectedVista = VER_TAREAS;
		} else {
			this.selectedVista = VER_CALENDARIO;
		}
	}
	
	@Command
	public void imprimir() throws Exception {
		this.imprimirTareas();
	}
	
	@Command
	public void updateTelefono(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Empresa emp = rr.getEmpresaById(this.selectedItem.getId());
		emp.setTelefono_((String) this.selectedItem.getPos3());
		for (Sucursal suc : emp.getSucursales()) {
			suc.setTelefono((String) this.selectedItem.getPos3());
			rr.saveObject(suc, this.getLoginNombre());
		}
		rr.saveObject(emp, this.getLoginNombre());
		comp.close();
		Clients.showNotification("NRO. DE TELEFONO ACTUALIZADO..");
	}
	
	/**************************************************/
	
	
	/******************* FUNCIONES ********************/
	
	/**
	 * busca los clientes con facturas vencidas..
	 */
	public void buscarClientes_() throws Exception {		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> clientes = rr.getClientesConFacturasVencidas_();
		this.clientes = new ArrayList<MyArray>();
		for (Object[] emp : clientes) {
			long idEmp = (long) emp[0];
			String ruc = (String) emp[1];
			String razonSocial = (String) emp[2];
			String telefono = (String) emp[3];
			boolean cuentaBloqueada = (boolean) emp[4];
			MyArray my = new MyArray();
			my.setId(idEmp);
			my.setPos1(ruc);
			my.setPos2(razonSocial);
			my.setPos3(telefono);
			my.setPos4(cuentaBloqueada);
			this.clientes.add(my);
		}	
		BindUtils.postNotifyChange(null, null, this, "clientes");
	}
	
	/**
	 * Impresion de Tareas..
	 */
	private void imprimirTareas() throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();

		for (MyArray item : this.getTodasLasTareasProgramadas()) {
			Date fecha = (Date) item.getPos1();
			boolean estado = (boolean) item.getPos3();
			Object[] obj1 = new Object[] { estado ? "REALIZADO" : "PENDIENTE",
					item.getPos6(), this.m.dateToString(fecha, Misc.DD_MM__YYY_HORA_MIN),
					item.getPos4(), item.getPos2() };
			data.add(obj1);
		}

		ReporteYhaguy rep = new ReporteTareas();
		rep.setDatosReporte(data);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}	
	
	/**************************************************/
	
	
	/******************** GET/SET *********************/
	
	@DependsOn({ "filter_ruc", "filter_razonsocial" })
	public List<MyArray> getClientes_() {
		List<MyArray> out = new ArrayList<MyArray>();
		if(this.clientes == null) return out;
		for (MyArray cli : this.clientes) {
			String ruc = (String) cli.getPos1();
			String razonsocial = (String) cli.getPos2();
			if (ruc.contains(this.filter_ruc) && razonsocial.toUpperCase().contains(this.filter_razonsocial.toUpperCase())) {
				out.add(cli);
			}
		}
		return out;
	}
	
	/**
	 * @return las facturas vencidas..
	 */
	@DependsOn("selectedItem")
	public List<MyArray> getFacturasVencidas() throws Exception {
		if (this.selectedItem == null) {
			return new ArrayList<MyArray>();
		}
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CtaCteEmpresaMovimiento> facs = rr.getFacturasVencidas(this.selectedItem.getId());
		for (CtaCteEmpresaMovimiento fac : facs) {
			MyArray my = new MyArray();
			my.setId(fac.getId());
			my.setPos1(fac.getFechaEmision_());
			my.setPos2(fac.getFechaVencimiento_());
			my.setPos3(fac.getNroComprobante().replace("(1/1)", ""));
			my.setPos4(fac.getTipoMovimiento().getDescripcion());
			my.setPos5(fac.getImporteOriginalFinal());
			my.setPos6(fac.getSaldoFinal());
			out.add(my);
		}
		return out;
	}
	
	@DependsOn("selectedItem")
	public List<MyArray> getLlamadas() throws Exception {		
		if (this.selectedItem == null) {
			return new ArrayList<MyArray>();
		}
		
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<LlamadaCobranza> lms = rr.getLlamadasCobranza(this.selectedItem.getId());
		for (LlamadaCobranza item : lms) {
			MyArray my = new MyArray();
			my.setId(item.getId());
			my.setPos1(item.getFecha());
			my.setPos2(item.getUsuario());
			my.setPos3(item.getResultado());
			my.setPos4(item.getDetalle());
			out.add(my);
		}		
		return out;
	}
	
	@DependsOn("selectedItem")
	public List<MyArray> getTareasProgramadas() throws Exception {
		if (this.selectedItem == null) {
			return new ArrayList<MyArray>();
		}
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<TareaProgramada> tareas = rr
				.getTareasProgramadas(this.selectedItem.getId());
		for (TareaProgramada item : tareas) {
			MyArray my = new MyArray();
			my.setId(item.getId());
			my.setPos1(item.isRealizado());
			my.setPos2(item.getFecha());
			my.setPos3(item.getTarea());
			my.setPos4(item.getObservacion());
			my.setPos5(item.getRealizadoPor());
			out.add(my);
		}
		return out;
	}
	
	@DependsOn({ "desde", "hasta", "verPendientes" })
	public List<MyArray> getTodasLasTareasProgramadas() throws Exception {		
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<TareaProgramada> tareas = rr.getTareasProgramadas(this.desde, this.hasta);
		for (TareaProgramada item : tareas) {
			if (item.getEmpresa() != null) {
				MyArray my = new MyArray();
				my.setId(item.getId());
				my.setPos1(item.getFecha());
				my.setPos2(item.getObservacion());
				my.setPos3(item.isRealizado());
				my.setPos4(item.getEmpresa().getRazonSocial());
				my.setPos5(item.getRealizadoPor());
				my.setPos6(item.getTarea());
				if (this.verPendientes == true) {
					if (!item.isRealizado()) {
						out.add(my);
					}
				} else {
					out.add(my);
				}
			
			}
		}		
		return out;
	}
	
	
	/**
	 * @return las vistas del formulario..
	 */
	public List<String> getVistas() {
		List<String> out = new ArrayList<String>();
		out.add(VER_CLIENTES);
		out.add(VER_TAREAS);
		out.add(VER_LLAMADAS);
		out.add(VER_CALENDARIO);
		return out;
	}
	
	/**
	 * @return las vistas del formulario..
	 */
	public List<String> getCalendarios() {
		List<String> out = new ArrayList<String>();
		out.add(CAL_DIA);
		out.add(CAL_SEMANA);
		out.add(CAL_MES);
		return out;
	}
	
	/**
	 * @return los posibles resultados de la llamada..
	 */
	public List<String> getResultados() {
		return LlamadaCobranza.getResultados();
	}
	
	@DependsOn("selectedVista")
	public boolean isTareasVisible() {
		return this.selectedVista.equals(VER_TAREAS);
	}
	
	@DependsOn("selectedVista")
	public boolean isCalendarioVisible() {
		return this.selectedVista.equals(VER_CALENDARIO);
	}
	
	@DependsOn("selectedVista")
	public boolean isClientesVisible() {
		return this.selectedVista.equals(VER_CLIENTES);
	}
	
	@DependsOn("selectedResultado")
	public boolean isFieldTareasVisible() {
		if(this.selectedResultado == null)
			return false;
		return !this.selectedResultado.equals(LlamadaCobranza.NO_RESPONDE);
	}
	
	@DependsOn("selectedCalendario")
	public String getModoCalendario() {
		return this.selectedCalendario.equals(CAL_MES)? "month" : "default";
	}
	
	@DependsOn("selectedCalendario")
	public String getDiasCalendario() {
		return this.selectedCalendario.equals(CAL_DIA)? "1" : "7";
	}
	
	@DependsOn("facturasVencidas")
	public double getTotalSaldo() throws Exception {
		double out = 0;
		for (MyArray fac : this.getFacturasVencidas()) {
			out += (double) fac.getPos6();
		}
		return out;
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
	}

	public String getSelectedVista() {
		return selectedVista;
	}

	public void setSelectedVista(String selectedVista) {
		this.selectedVista = selectedVista;
	}

	public String getSelectedResultado() {
		return selectedResultado;
	}

	public void setSelectedResultado(String selectedResultado) {
		this.selectedResultado = selectedResultado;
	}

	public MyArray getNuevaLlamada() {
		return nuevaLlamada;
	}

	public void setNuevaLlamada(MyArray nuevaLlamada) {
		this.nuevaLlamada = nuevaLlamada;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public MyArray getSelectedTarea() {
		return selectedTarea;
	}

	public void setSelectedTarea(MyArray selectedTarea) {
		this.selectedTarea = selectedTarea;
	}

	public void setClientes(List<MyArray> clientes) {
		this.clientes = clientes;
	}

	public List<MyArray> getClientes() {
		return clientes;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public boolean isVerPendientes() {
		return verPendientes;
	}

	public void setVerPendientes(boolean verPendientes) {
		this.verPendientes = verPendientes;
	}

	public String getSelectedCalendario() {
		return selectedCalendario;
	}

	public void setSelectedCalendario(String selectedCalendario) {
		this.selectedCalendario = selectedCalendario;
	}

	public DemoCalendarModel getCalendarModel() {
		return calendarModel;
	}

	public void setCalendarModel(DemoCalendarModel calendarModel) {
		this.calendarModel = calendarModel;
	}

	public String getFilter_ruc() {
		return filter_ruc;
	}

	public void setFilter_ruc(String filter_ruc) {
		this.filter_ruc = filter_ruc;
	}

	public String getFilter_razonsocial() {
		return filter_razonsocial;
	}

	public void setFilter_razonsocial(String filter_razonsocial) {
		this.filter_razonsocial = filter_razonsocial;
	}
}

/**
 * Reporte de Tareas..
 */
class ReporteTareas extends ReporteYhaguy {
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Estado", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Tarea", TIPO_STRING, 50);
	static DatosColumnas col3 = new DatosColumnas("Horario", TIPO_STRING, 50);
	static DatosColumnas col4 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Observación", TIPO_STRING);
	
	public ReporteTareas() {
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
		this.setTitulo("Planilla de Tareas");
		this.setDirectorio("tareas");
		this.setNombreArchivo("Tarea-");
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

		return out;
	}
}

/**
 * test
 */
class DemoCalendarModel extends SimpleCalendarModel {
    private static final long serialVersionUID = 1L;
     
    private String filterText = "";
 
    public DemoCalendarModel(List<CalendarEvent> calendarEvents) {
        super(calendarEvents);
    }
 
    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }
 
    @Override
    public List<CalendarEvent> get(Date beginDate, Date endDate, RenderContext rc) {
        List<CalendarEvent> list = new LinkedList<CalendarEvent>();
        long begin = beginDate.getTime();
        long end = endDate.getTime();
                 
        for (Iterator<?> itr = _list.iterator(); itr.hasNext();) {
            Object obj = itr.next();
            CalendarEvent ce = obj instanceof CalendarEvent ? (CalendarEvent)obj : null;
             
            if(ce == null) break;
             
            long b = ce.getBeginDate().getTime();
            long e = ce.getEndDate().getTime();
            if (e >= begin && b < end && ce.getContent().toLowerCase().contains(filterText.toLowerCase()))
                list.add(ce);
        }
        return list;
    } 
}

/**
 * test
 */
class DemoCalendarData {
	 
    private List<CalendarEvent> calendarEvents = new LinkedList<CalendarEvent>();
    private final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
 
    public DemoCalendarData(List<MyArray> tareas) {
        init(tareas);
    }
 
    private void init(List<MyArray> tareas) {
    	
    	for (MyArray tarea : tareas) {
			Date fecha = (Date) tarea.getPos1();
			String tarea_ = (String) tarea.getPos6();
			calendarEvents.add(new DemoCalendarEvent(this.getDate(fecha), fecha, "#A32929", "#D96666", tarea_));
		}
    	
    	/*
        int mod = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String date2 = mod > 9 ? year + "/" + mod + "" : year + "/" + "0" + mod;
        String date1 = --mod > 9 ? year + "/" + mod + "" : year + "/" + "0" + mod;
        ++mod;
        String date3 = ++mod > 9 ? year + "/" + mod + "" : year + "/" + "0" + mod;
        // Red Events
        calendarEvents.add(new DemoCalendarEvent(getDate(date1 + "/28 00:00"), getDate(date1 + "/29 00:00"), "#A32929", "#D96666", "ZK Jet Released"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date1 + "/04 02:00"), getDate(date1 + "/05 03:00"), "#A32929", "#D96666", "Experience ZK SpreadSheet Live Demo!"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/21 05:00"), getDate(date2 + "/21 07:00"), "#A32929", "#D96666", "New Features of ZK Spreadsheet"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/08 00:00"), getDate(date2 + "/09 00:00"), "#A32929", "#D96666", "ZK Spreadsheet Released"));
        // Blue Events
        calendarEvents.add(new DemoCalendarEvent(getDate(date1 + "/29 03:00"), getDate(date2 + "/02 06:00"), "#3467CE", "#668CD9", "ZK Released"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/02 10:00"), getDate(date2 + "/02 12:30"), "#3467CE", "#668CD9", "New Feature of ZK "));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/17 14:00"), getDate(date2 + "/18 16:00"), "#3467CE", "#668CD9", "Case Study - Mecatena"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date3 + "/01 14:30"), getDate(date3 + "/01 17:30"), "#3467CE", "#668CD9", "ZK Unit Testing Project - zunit"));
        // Purple Events
        calendarEvents.add(new DemoCalendarEvent(getDate(date1 + "/29 08:00"), getDate(date2 + "/03 12:00"), "#7A367A", "#B373B3", "ZK Studio released"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/07 08:00"), getDate(date2 + "/07 12:00"), "#7A367A", "#B373B3", "Tutorial : Reading from the DB with Netbeans and ZK"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/13 11:00"), getDate(date2 + "/13 14:30"), "#7A367A", "#B373B3", "Small talk - ZK Charts"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/16 14:00"), getDate(date2 + "/18 16:00"), "#7A367A", "#B373B3", "Style Guide for ZK released !"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date3 + "/02 12:00"), getDate(date3 + "/02 17:00"), "#7A367A", "#B373B3", "Small talk -- Simple Database Access From ZK"));
        // Khaki Events
        calendarEvents.add(new DemoCalendarEvent(getDate(date1 + "/03 00:00"), getDate(date1 + "/04 00:00"), "#88880E", "#BFBF4D", "ZK UK User Group"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/13 05:00"), getDate(date2 + "/13 07:00"), "#88880E", "#BFBF4D", "How to Test ZK Application with Selenium"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/24 19:30"), getDate(date2 + "/24 20:00"), "#88880E", "#BFBF4D", "ZK Alfresco Talk"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date3 + "/03 00:00"), getDate(date3 + "/04 00:00"), "#88880E", "#BFBF4D", "ZK selected as SourceForge.net Project of the Month"));
        // Green Events
        calendarEvents.add(new DemoCalendarEvent(getDate(date1 + "/28 10:00"), getDate(date1 + "/28 12:30"), "#0D7813", "#4CB052", "ZK Mobile Released"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/03 00:00"), getDate(date2 + "/03 05:30"), "#0D7813", "#4CB052", "ZK Gmaps released"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/05 20:30"), getDate(date2 + "/06 00:00"), "#0D7813", "#4CB052", "Refresh with Five New ZK Themes!"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date2 + "/23 00:00"), getDate(date2 + "/25 16:30"), "#0D7813", "#4CB052", "ZK Roadmap Announced"));
        calendarEvents.add(new DemoCalendarEvent(getDate(date3 + "/01 08:30"), getDate(date3 + "/01 19:30"), "#0D7813", "#4CB052", "Build Database CRUD Application in 6 Steps"));
    */}
 

	private Date getDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String fecha = sdf.format(date) + " 00:00";
		try {
			return DATA_FORMAT.parse(fecha);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
 
    public List<CalendarEvent> getCalendarEvents() {
        return calendarEvents;
    }
}

/**
 * test
 */
class DemoCalendarEvent extends SimpleCalendarEvent {
    private static final long serialVersionUID = 1L;
 
    public DemoCalendarEvent(Date beginDate, Date endDate, String headerColor, String contentColor, String content) {
        setHeaderColor(headerColor);
        setContentColor(contentColor);
        setContent(content);
        setBeginDate(beginDate);
        setEndDate(endDate);
    }
 
    public DemoCalendarEvent(Date beginDate, Date endDate, String headerColor, String contentColor, String content,
            String title) {
        setHeaderColor(headerColor);
        setContentColor(contentColor);
        setContent(content);
        setTitle(title);
        setBeginDate(beginDate);
        setEndDate(endDate);
    }
 
    public DemoCalendarEvent(Date beginDate, Date endDate, String headerColor, String contentColor, String content,
            String title, boolean locked) {
        setHeaderColor(headerColor);
        setContentColor(contentColor);
        setContent(content);
        setTitle(title);
        setBeginDate(beginDate);
        setEndDate(endDate);
        setLocked(locked);
    }
     
    public DemoCalendarEvent() {
        setHeaderColor("#FFFFFF");
        setContentColor("#000000");
    }
     
    @Override
    public Date getBeginDate() {
        return super.getBeginDate();
    }
 
    @Override
    public Date getEndDate() {
        return super.getEndDate();
    }
}
