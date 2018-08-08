package com.yhaguy.gestion.reparto.pendientes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class RepartoPendientesSimpleVM extends SimpleViewModel {
	
	private String filter_numero = "";
	private String filter_razonSocial = "";
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private int size = 0;

	@Init(superclass = true)
	public void init() {
		try {
			this.filterFechaDD = Utiles.getDateToString(new Date(), "dd");
			this.filterFechaMM = "" + Utiles.getNumeroMesCorriente();
			this.filterFechaAA = Utiles.getAnhoActual();
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
	@NotifyChange("*")
	public void refresh() {
	}
	
	@Command
	public void reporte() throws Exception {
		this.reporteRepartosPendientes();
	}
	
	/**
	 * reporte de repartos pendientes..
	 */
	private void reporteRepartosPendientes() throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();
		for (Object[] vta : this.getVentas()) {
			Object[] obj = new Object[] {
					Utiles.getDateToString((Date) vta[0], Utiles.DD_MM_YY),
					vta[1],
					vta[2],
					vta[3],
					Utiles.getNumberFormat((double) vta[4])};
			data.add(obj);
		}

		ReporteRepartoPendientes rep = new ReporteRepartoPendientes();
		rep.setDatosReporte(data);
		rep.setBorrarDespuesDeVer(true);
		rep.setApaisada();

		ViewPdf vp = new ViewPdf();
		vp.setBotonImprimir(false);
		vp.setBotonCancelar(false);
		vp.showReporte(rep, this);
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return las ventas para reparto..
	 * [0]:fecha
	 * [1]:numero
	 * [2]:tipoMovimiento.descripcion
	 * [3]:razonSocial
	 * [4]:totalImporteGs
	 */
	@DependsOn({ "filter_numero", "filterFechaDD", "filterFechaMM", "filterFechaAA", "filter_razonSocial" })
	public List<Object[]> getVentas() throws Exception {
		this.size = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> ventas;
		
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_BATERIAS)) {
			ventas = rr.getVentasParaRepartoBaterias_(this.getFilterFecha(), this.filter_numero, this.filter_razonSocial);
		} else {
			return new ArrayList<Object[]>();
		}
		this.size = ventas.size();
		BindUtils.postNotifyChange(null, null, this, "size");
		return ventas;
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

	public String getFilter_numero() {
		return filter_numero;
	}

	public void setFilter_numero(String filter_numero) {
		this.filter_numero = filter_numero;
	}

	public String getFilter_razonSocial() {
		return filter_razonSocial;
	}

	public void setFilter_razonSocial(String filter_razonSocial) {
		this.filter_razonSocial = filter_razonSocial;
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

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}

/**
 * Reporte de Repartos Pendientes..
 */
class ReporteRepartoPendientes extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_STRING, 20);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 30);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING, 40);
	static DatosColumnas col4 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Importe", TIPO_STRING, 20);

	public ReporteRepartoPendientes() {
	}

	static {
		col5.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Facturas de venta pendientes de reparto");
		this.setDirectorio("ventas");
		this.setNombreArchivo("serv-");
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
