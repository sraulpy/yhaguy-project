package com.yhaguy.gestion.bancos.tarjetas;

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
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.ProcesadoraTarjeta;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class BancoTarjetaViewModel extends SimpleViewModel {
	
	static final String T_CREDITO = "TARJETA DE CRÉDITO";
	static final String T_DEBITO = "TARJETA DE DÉBITO";
	
	private Date desde;
	private Date hasta;
	private Tipo selectedFormaPago;
	private ProcesadoraTarjeta selectedProcesadora;
	private SucursalApp selectedSucursal;
	private BancoCta selectedBanco;
	private double comision = 0;
	private double renta = 1;
	private double retencionIva = 0.90909;
	
	private String filterFechaDD = "";
	private String filterFechaMM = "";
	private String filterFechaAA = "";
	
	private List<Object[]> movimientos = new ArrayList<Object[]>();
	private List<Object[]> selectedMovimientos;
	
	private Window win;

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Command
	@NotifyChange("*")
	public void buscarMovimientos() throws Exception {
		this.buscarMovimientos_();
	}
	
	@Command
	@NotifyChange("*")
	public void confirmar(@BindingParam("comp") Popup comp) throws Exception {
		this.confirmarMovimientos();
		comp.close();
	}
	
	@Command
	public void imprimirPDF() throws Exception {
		this.imprimir();
	}
	
	/**
	 * busca los movimientos de tarjeta..
	 */
	private void buscarMovimientos_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> list = rr.getFormasPagoTarjeta(this.desde, this.hasta, this.selectedFormaPago.getSigla(),
				this.selectedProcesadora.getId(), this.selectedSucursal.getId());
		List<Object[]> list_ = new ArrayList<Object[]>();
		for (Object[] item : list) {
			double importe = (double) item[3];
			double acreditado = (double) item[8];
			//item = Arrays.copyOf(item, item.length + 8);
			item[4] = Utiles.obtenerValorDelPorcentaje(importe, this.comision);
			item[5] = Utiles.obtenerValorDelPorcentaje((double) item[4], 10);
			item[6] = Utiles.obtenerValorDelPorcentaje(importe, this.renta);
			item[7] = Utiles.obtenerValorDelPorcentaje(importe, this.retencionIva);
			item[8] = acreditado > 0 ? acreditado : importe - (((double) item[4]) + ((double) item[5]) + ((double) item[6]) + ((double) item[7]));
			item[9] = item[1];
			list_.add(item);
		}
		this.selectedMovimientos = null;
		this.movimientos.clear();
		this.movimientos = list_;
	}
	
	/**
	 * confirma los movimientos..
	 */
	private void confirmarMovimientos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		for (Object[] item : this.selectedMovimientos) {
			item[11] = true;
			ReciboFormaPago fp = (ReciboFormaPago) rr.getObject(ReciboFormaPago.class.getName(), (long) item[0]);
			fp.setAcreditado(true);
			fp.setFechaAcreditacion((Date) item[9]);
			fp.setReciboDebitoNro((String) item[10]);
			fp.setImporteAcreditado((double) item[8]);
			fp.setDepositoBancoCta(this.selectedBanco);
			rr.saveObject(fp, this.getLoginNombre());
		}
		this.selectedMovimientos = null;
		Clients.showNotification("REGISTROS CONFIRMADOS..!");
	}
	
	/**
	 * Despliega el Reporte de Tarjetas..
	 */
	private void imprimir() throws Exception {	
		String source = ReportesViewModel.SOURCE_CONCILIACION_TARJETAS;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new TarjetasDataSource();
		params.put("Titulo", "CONCILIACIÓN DE TARJETAS");
		params.put("Usuario", this.getLoginNombre());
		params.put("Desde", Utiles.getDateToString(this.getDesde(), "dd-MM-yyyy"));
		params.put("Hasta", Utiles.getDateToString(this.getHasta(), "dd-MM-yyyy"));
		params.put("Procesadora", this.selectedProcesadora.getNombre().toUpperCase());
		params.put("TipoTarjeta", this.selectedFormaPago.getDescripcion().toUpperCase());
		this.imprimirComprobante(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * Despliega el comprobante en un pdf para su impresion..
	 */
	public void imprimirComprobante(String source,
			Map<String, Object> parametros, JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions.createComponents(
				ReportesViewModel.ZUL_REPORTES, this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * DataSource de Tarjetas..
	 */
	class TarjetasDataSource implements JRDataSource {

		public TarjetasDataSource() {
		}

		private int index = -1;

		@Override
		public Object getFieldValue(JRField field) throws JRException {
			Object value = null;
			String fieldName = field.getName();
			Object[] item = movimientos.get(index);
			Object[] totales = getTotales();

			if ("Numero".equals(fieldName)) {
				value = item[2] + "";
			} else if ("Fecha".equals(fieldName)) {
				value = Utiles.getDateToString((Date) item[1], Utiles.DD_MM_YYYY);
			} else if ("Importe".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) item[3]);
			} else if ("Comision".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) item[4]);
			} else if ("IvaComision".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) item[5]);
			} else if ("Renta".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) item[6]);
			} else if ("IvaImporte".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) item[7]);
			} else if ("Credito".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) item[8]);
			} else if ("FechaCredito".equals(fieldName)) {
				value = Utiles.getDateToString((Date) item[9], Utiles.DD_MM_YYYY);
			} else if ("NumeroTD".equals(fieldName)) {
				value = item[10] + "";
			} else if ("Tot_importe".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) totales[0]);
			} else if ("Tot_comision".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) totales[1]);
			} else if ("Tot_ivacomision".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) totales[2]);
			} else if ("Tot_renta".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) totales[3]);
			} else if ("Tot_ivaimporte".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) totales[4]);
			} else if ("Tot_credito".equals(fieldName)) {
				value = Utiles.getNumberFormat((double) totales[5]);
			}
			return value;
		}

		@Override
		public boolean next() throws JRException {
			if (index < movimientos.size() - 1) {
				index++;
				return true;
			}
			return false;
		}
	}
	
	@Command
	public void exportar() throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Conciliación Tarjetas");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("FECHA");
		r.createCell(cell++).setCellValue("NRO.OPERACION");
		r.createCell(cell++).setCellValue("IMPORTE");
		r.createCell(cell++).setCellValue("COMISION");
		r.createCell(cell++).setCellValue("IVA COMISION");
		r.createCell(cell++).setCellValue("RENTA");
		r.createCell(cell++).setCellValue("IVA IMPORTE");
		r.createCell(cell++).setCellValue("CREDITO");		
		r.createCell(cell++).setCellValue("FECHA CREDITO");
		r.createCell(cell++).setCellValue("NRO.TARJETA DEBITO");
		for (Object[] c : this.movimientos) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(Utiles.getDateToString((Date) c[1], Utiles.DD_MM_YYYY));
			row.createCell(cellIndex++).setCellValue(c[2] + "");
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[3]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[4]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[5]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[6]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[7]));
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) c[8]));
			row.createCell(cellIndex++).setCellValue(Utiles.getDateToString((Date) c[9], Utiles.DD_MM_YYYY));
			row.createCell(cellIndex++).setCellValue(c[10] + "");
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
		listSheet.autoSizeColumn(9);
		listSheet.autoSizeColumn(10);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("ConciliacionTarjetas.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return los totales..
	 */
	public Object[] getTotales() {
		double totalImporte = 0;
		double totalComision = 0;
		double totalIvaComision = 0;
		double totalRenta = 0;
		double totalIvaRetencion = 0;
		double totalCredito = 0;
		for (Object[] item : movimientos) {
			totalImporte += ((double) item[3]);
			totalComision += ((double) item[4]);
			totalIvaComision += ((double) item[5]);
			totalRenta += ((double) item[6]);
			totalIvaRetencion += ((double) item[7]);
			totalCredito += ((double) item[8]);
		}
		return new Object[] { totalImporte, totalComision, totalIvaComision, totalRenta, totalIvaRetencion, totalCredito };
	}
	
	@DependsOn({ "selectedProcesadora", "selectedFormaPago", "desde", "hasta", "selectedSucursal" })
	public boolean isRefrescarEnabled() {
		return this.selectedProcesadora != null && this.selectedFormaPago != null && this.desde != null
				&& this.hasta != null && this.selectedSucursal != null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcesadoraTarjeta> getProcesadoras() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ProcesadoraTarjeta.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<BancoCta> getBancos() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(BancoCta.class.getName());
	}
	
	@SuppressWarnings("unchecked")
	public List<SucursalApp> getSucursales() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(SucursalApp.class.getName());
	}
	
	/**
	 * @return los tipos de tarjeta
	 */
	public List<Tipo> getTiposTarjetas() throws Exception {
		List<Tipo> out = new ArrayList<Tipo>();
		RegisterDomain rr = RegisterDomain.getInstance();
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO));
		out.add(rr.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO));
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

	public String getFilterFechaAA() {
		return filterFechaAA;
	}

	public void setFilterFechaAA(String filterFechaAA) {
		this.filterFechaAA = filterFechaAA;
	}

	public List<Object[]> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<Object[]> movimientos) {
		this.movimientos = movimientos;
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

	public Tipo getSelectedFormaPago() {
		return selectedFormaPago;
	}

	public void setSelectedFormaPago(Tipo selectedFormaPago) {
		this.selectedFormaPago = selectedFormaPago;
	}

	public double getComision() {
		return comision;
	}

	public void setComision(double comision) {
		this.comision = comision;
	}

	public double getRenta() {
		return renta;
	}

	public void setRenta(double renta) {
		this.renta = renta;
	}

	public double getRetencionIva() {
		return retencionIva;
	}

	public void setRetencionIva(double retencionIva) {
		this.retencionIva = retencionIva;
	}

	public ProcesadoraTarjeta getSelectedProcesadora() {
		return selectedProcesadora;
	}

	public void setSelectedProcesadora(ProcesadoraTarjeta selectedProcesadora) {
		this.selectedProcesadora = selectedProcesadora;
	}

	public SucursalApp getSelectedSucursal() {
		return selectedSucursal;
	}

	public void setSelectedSucursal(SucursalApp selectedSucursal) {
		this.selectedSucursal = selectedSucursal;
	}

	public BancoCta getSelectedBanco() {
		return selectedBanco;
	}

	public void setSelectedBanco(BancoCta selectedBanco) {
		this.selectedBanco = selectedBanco;
	}

	public List<Object[]> getSelectedMovimientos() {
		return selectedMovimientos;
	}

	public void setSelectedMovimientos(List<Object[]> selectedMovimientos) {
		this.selectedMovimientos = selectedMovimientos;
	}	
}
