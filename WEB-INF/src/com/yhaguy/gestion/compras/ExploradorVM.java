package com.yhaguy.gestion.compras;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
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
			if (!(this.getLoginNombre().equals("vanesar") 
					|| this.getLoginNombre().equals("federico")
					|| this.getLoginNombre().equals("sergio")
					|| this.getLoginNombre().equals("oscarv"))) {
				this.saltoDePagina("/");
			}
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
	
	@Command
	public void exportExcel(@BindingParam("items") List<Object[]> items) throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Rendimiento Compras");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("FECHA");
		r.createCell(cell++).setCellValue("FACTURA");
		r.createCell(cell++).setCellValue("PROVEEDOR");
		r.createCell(cell++).setCellValue("COMPRADOR");
		r.createCell(cell++).setCellValue("FAMILIA");
		r.createCell(cell++).setCellValue("CODIGO");
		r.createCell(cell++).setCellValue("COMPRAS");
		r.createCell(cell++).setCellValue("VENTAS");
		r.createCell(cell++).setCellValue("RENDIMIENTO");
		for (Object[] c : items) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(Utiles.getDateToString((Date) c[0], "dd-MM-yyyy") + "");
			row.createCell(cellIndex++).setCellValue(c[1] + "");
			row.createCell(cellIndex++).setCellValue(c[2] + "");
			row.createCell(cellIndex++).setCellValue(c[5] + "");
			row.createCell(cellIndex++).setCellValue(c[8] + "");
			row.createCell(cellIndex++).setCellValue(c[6] + "");
			row.createCell(cellIndex++).setCellValue(c[7] + "");
			row.createCell(cellIndex++).setCellValue(c[10] + "");
			row.createCell(cellIndex++).setCellValue(c[11] + "");
		}
		listSheet.autoSizeColumn(0);
		listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2);
		listSheet.autoSizeColumn(3);
		listSheet.autoSizeColumn(4);
		listSheet.autoSizeColumn(5);
		listSheet.autoSizeColumn(6);
		listSheet.autoSizeColumn(7);
		listSheet.autoSizeColumn(8);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("RendimientoCompras.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * procesa las ventas..
	 */
	private void procesarVentas(List<Object[]> items) throws Exception {		
		if (this.ventasDesde == null || this.ventasHasta == null) {
			return;
		}
		this.totalVentas = 0;
		Map<String, Long> values = new HashMap<String, Long>();
		Map<String, Integer> indices = new HashMap<String, Integer>();
		int index = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Object[] item : items) {
			String cod = (String) item[6];
			values.put(cod, (long) 0);
			indices.put(cod, index);
			index ++;
			List<Object[]> list = rr.getVentasDetallado(this.ventasDesde, this.ventasHasta, cod);
			if (list.size() > 0) {
				long v = (long) list.get(0)[1];
				values.put(cod, v);
			}
		}
		for (Object[] item : items) {
			String cod = (String) item[6];
			
			long c = Long.parseLong(item[7] + "");
			long v = values.get(cod);
			
			if (v >= c) {
				item[10] = c;
			} else {
				item[10] = v;
			}
			
			item[11] = Utiles.obtenerPorcentajeDelValor(Double.parseDouble(item[10] + ""), c);
			this.totalVentas += (long) item[10];
			
			if (v >= c) {
				values.put(cod, (v - c));
			} else {
				values.put(cod, (long) 0);
			}
		}
		
		for (String key : values.keySet()) {
			long v = values.get(key);
			if (v > 0) {
				Object[] item = items.get(indices.get(key));
				long cantV = (long) item[10];
				item[10] = cantV + v;
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
			this.totalCompras += (int) c[7];
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
