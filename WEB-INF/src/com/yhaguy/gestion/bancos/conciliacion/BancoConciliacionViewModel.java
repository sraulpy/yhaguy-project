package com.yhaguy.gestion.bancos.conciliacion;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.csv.CSV;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoExtracto;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.reportes.formularios.ReportesViewModel;
import com.yhaguy.util.Utiles;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class BancoConciliacionViewModel extends BodyApp {
	
	static final String PATH = Configuracion.pathConciliaciones;
	final static String KEY_NRO = "CONCI";

	static final String[][] CAB = { { "Empresa", CSV.STRING } };
	static final String[][] DET = { { "FECHA", CSV.STRING }, { "NUMERO", CSV.STRING }, { "DESCRIPCION", CSV.STRING },
			{ "DEBE", CSV.STRING }, { "HABER", CSV.STRING }, { "SALDO", CSV.STRING } };
	
	private BancoExtractoDTO dto = new BancoExtractoDTO();
	private Map<String, String> detalles1 = new HashMap<String, String>();
	private Map<String, String> detalles2 = new HashMap<String, String>();
	
	private Object[] selectedItem1;
	private BancoExtractoDetalleDTO selectedItem2;
	private List<BancoExtractoDetalleDTO> selectedItems2;
	private List<Object[]> selectedItems1;
	private List<Object[]> movimientosBanco_ = new ArrayList<Object[]>();
	
	private String mensajeValidacion = "";
	
	private String filterConcepto = "";
	private String filterNumero = "";
	
	private double totalDebe = 0;
	private double totalHaber = 0;
	private double totalSaldo = 0;
	
	private Window win;
	
	private int size = 0;	
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public boolean verificarAlGrabar() {
		try {
			return this.validarDatos();
		} catch (Exception e) {
			e.printStackTrace();
			this.mensajePopupTemporalWarning("Hubo un error al intentar guardar..");
			return false;
		}
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeValidacion;
	}

	@Override
	public Assembler getAss() {
		return new BancoExtractoAssembler();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (BancoExtractoDTO) dto;		
		this.detalles2 = this.dto.getNumeros();
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		BancoExtractoDTO nDto = new BancoExtractoDTO();
		this.sugerirValores(nDto);
		return nDto;
	}

	@Override
	public String getEntidadPrincipal() {
		return BancoExtracto.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(getEntidadPrincipal());
	}
	
	@Override
	public Browser getBrowser() {
		return new BancoConciliacionBrowser();
	}
	
	/**
	 * COMANDOS
	 */
	
	@Command 
	@NotifyChange("*")
	public void uploadFile(@BindingParam("file") Media file) {
		try {
			Misc misc = new Misc();
			String name = this.dto.getNumero();
			boolean isText = "text/csv".equals(file.getContentType());
			InputStream file_ = new ByteArrayInputStream(isText ? file.getStringData().getBytes() : file.getByteData());
			misc.uploadFile(PATH, name, ".csv", file_);
			this.csvConciliacion();
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al intentar subir el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	@Command
	@NotifyChange({ "selectedItem2", "selectedItems2" })
	public void selectDetalle1() {
		this.selectedItem2 = null;
		this.selectedItems2 = new ArrayList<>();
		for (BancoExtractoDetalleDTO item : this.dto.getDetalles2()) {
			String nro1 = (String) this.selectedItem1[2];
			String nro2 = item.getNumero();
			String[] nro2_ = item.getAuxi().split(";");
			for (int i = 0; i < nro2_.length; i++) {
				if (nro1.equals(nro2) || nro1.equals(nro2_[i])) {
					this.selectedItem2 = item;
					this.selectedItems2.add(item);
				}
			}
		}
	}
	
	@Command
	@NotifyChange({ "selectedItem1", "selectedItems1", "movimientosBanco_" })
	public void selectDetalle2() throws Exception {
		this.selectedItem1 = null;
		this.selectedItems1 = new ArrayList<>();
		for (Object[] item : this.movimientosBanco_) {
			String nro1 = this.selectedItem2.getNumero();
			String nro2 = (String) item[2];
			if (nro1.equals(nro2)) {
				this.selectedItem1 = item;
				this.selectedItems1.add(item);
			} else {
				String[] nro1_ = this.selectedItem2.getAuxi().split(";");
				for (int i = 0; i < nro1_.length; i++) {
					if (nro2.equals(nro1_[i])) {
						this.selectedItems1.add(item);
					}
				}
			}
		}
	}
	
	@Command
	@NotifyChange({ "selectedItem1", "selectedItems1" })
	public void selectItem1(@BindingParam("item") Object[] item) {
		boolean check = (boolean) item[18];
		this.selectedItems1 = new ArrayList<>();
		this.deseleccionar1(item);
		if (check) {
			this.selectedItem1 = item;
			this.selectedItems1.add(item);
		} else {
			this.selectedItem1 = null;
			this.selectedItems1.remove(item);
		}
	}
	
	@Command
	@NotifyChange({ "selectedItem2", "selectedItems2" })
	public void selectItem2(@BindingParam("item") BancoExtractoDetalleDTO item) {
		boolean check = item.isChecked();
		this.selectedItems2 = new ArrayList<>();
		this.deseleccionar2(item);
		if (check) {
			this.selectedItem2 = item;
			this.selectedItems2.add(item);
		} else {
			this.selectedItem2 = null;
			this.selectedItems2.remove(item);
		}
	}
	
	@Command
	public void conciliar() {
		this.conciliarMovimiento();
	}
	
	@Command
	public void resumen() {
		this.resumenConciliacion();
	}
	
	@Command
	@NotifyChange("*")
	public void confirmar() {
		try {
			this.confirmarConciliacion();
		} catch (Exception e) {
			e.printStackTrace();
			this.mensajePopupTemporalWarning("Hubo un error al confirmar", 5000);
		}
	}
	
	
	/**
	 * FUNCIONES
	 */
	
	private void sugerirValores(BancoExtractoDTO dto) throws Exception {
		dto.setNumero(KEY_NRO + "-" + AutoNumeroControl.getAutoNumero(KEY_NRO, 5, true));
		dto.setSucursal(this.getSucursal());
	}
	
	/**
	 * deseleccionar demas items..
	 */
	private void deseleccionar1(Object[] item) {
		for (Object[] item_ : this.movimientosBanco_) {
			if (item_ != item) {
				item_[18] = false;
				BindUtils.postNotifyChange(null, null, item_, "*");
			}
		}
	}
	
	/**
	 * deseleccionar demas items..
	 */
	private void deseleccionar2(BancoExtractoDetalleDTO item) {
		for (BancoExtractoDetalleDTO item_ : this.dto.getDetalles2()) {
			if (item_ != item) {
				item_.setChecked(false);;
				BindUtils.postNotifyChange(null, null, item_, "*");
			}
		}
	}
	
	/**
	 * csv de conciliacion..
	 */
	private void csvConciliacion() {
		try {
			this.detalles2.clear();
			List<BancoExtractoDetalleDTO> list = new ArrayList<BancoExtractoDetalleDTO>();
			
			CSV csv = new CSV(CAB, DET, PATH + this.dto.getNumero() + ".csv", ',');

			csv.start();
			while (csv.hashNext()) {
				String numero = csv.getDetalleString("NUMERO");
				String descripcion = csv.getDetalleString("DESCRIPCION");
				String debe = csv.getDetalleString("DEBE");
				String haber = csv.getDetalleString("HABER");
				String fecha = csv.getDetalleString("FECHA");
				Date fecha_ = Utiles.getFecha(fecha + "/" + Utiles.getDateToString(this.dto.getDesde(), "yyyy"), "dd/MMM/yyyy");
				
				String[] numero_ = numero.split(" ");
				double importe; 
				double debe_ = 0; 
				double haber_ = 0;
				if (!debe.equals("0")) {
					importe = Double.parseDouble(debe.replace(".", ""));
					debe_ = importe;
				} else {
					importe = Double.parseDouble(haber.replace(".", ""));
					haber_ = importe;
				}
				
				BancoExtractoDetalleDTO det = new BancoExtractoDetalleDTO();
				det.setNumero(numero_[1]);
				det.setDescripcion(descripcion);
				det.setFecha(fecha_);
				det.setDebe(debe_);
				det.setHaber(haber_);
				det.setConciliado(false);
				det.setNumero_(Double.parseDouble(numero_[1]));
				det.setImporte_(importe);
				det.setConciliado(this.detalles1.get(det.getNumero()) != null);
				list.add(det);
			}
			
			this.dto.getDetalles2().addAll(list);
			this.detalles2 = this.dto.getNumeros();
			this.mensajePopupTemporal("Archivo correctamente importado..");
		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un problema al leer el archivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}
	
	/**
	 * conciliacion manual..
	 */
	private void conciliarMovimiento() {
		this.selectedItem1[15] = true;
		String nro = (String) this.selectedItem1[2];	
		for (BancoExtractoDetalleDTO item : this.dto.getDetalles2()) {
			if (item.getNumero().equals(this.selectedItem2.getNumero())) {
				String auxi = item.getAuxi();
				if (auxi != null && !auxi.isEmpty()) {
					item.setAuxi(auxi + ";" + nro);
				} else {
					item.setAuxi(nro);
				}
				item.setConciliado(true);
				BindUtils.postNotifyChange(null, null, item, "*");
			}
		}
		this.detalles2 = this.dto.getNumeros();
		this.selectedItem1 = null; this.selectedItems1 = new ArrayList<>();
		this.selectedItem2 = null; this.selectedItems2 = new ArrayList<>();
		BindUtils.postNotifyChange(null, null, this, "selectedItem1");
		BindUtils.postNotifyChange(null, null, this, "selectedItem2");
		BindUtils.postNotifyChange(null, null, this, "selectedItems1");
		BindUtils.postNotifyChange(null, null, this, "selectedItems2");
	}
	
	/**
	 * confirmar conciliacion..
	 */
	private void confirmarConciliacion() throws Exception {
		if (!this.mensajeSiNo("Desea confirmar la conciliacion..?")) {
			return;
		}
		this.dto.setCerrado(true);
		this.dto.setReadonly();
		this.dto = (BancoExtractoDTO) this.saveDTO(this.dto);
		this.setEstadoABMConsulta();
		Clients.showNotification("Conciliacion confirmada..");
	}
	
	/**
	 * impresion del resumen de conciliacion..
	 */
	private void resumenConciliacion() {		
		String source = ReportesViewModel.SOURCE_RESUMEN_CONCILIACION;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new ResumenConciliacioDataSource(this.dto, this.movimientosBanco_);
		params.put("Usuario", getUs().getNombre());
		params.put("ConciliacionNro", this.dto.getNumero());
		params.put("Banco", this.dto.getBanco().getBanco().getPos1());
		params.put("Desde", Utiles.getDateToString(this.dto.getDesde(), Utiles.DD_MM_YY));
		params.put("Hasta", Utiles.getDateToString(this.dto.getHasta(), Utiles.DD_MM_YY));
		imprimirJasper(source, params, dataSource, ReportesViewModel.FORMAT_PDF);
	}
	
	/**
	 * @return la validacion de datos..
	 */
	private boolean validarDatos() throws Exception {
		boolean out = true;
		this.mensajeValidacion = "No se pudo completar la operación debido a: ";
		if (this.dto.getDetalles2().isEmpty()) {
			out = false;
			this.mensajeValidacion += "\n - Debe importar el extracto..";
		}
		if (this.dto.esNuevo() && out) {
			this.dto.setNumero(KEY_NRO + "-" + AutoNumeroControl.getAutoNumero(KEY_NRO, 5));
		}
		return out;
	}
	
	/**
	 * Despliega el reporte en un pdf para su impresion..
	 */
	private void imprimirJasper(String source, Map<String, Object> parametros,
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
	
	/**
	 * @return
	 * [0]:fecha
	 * [1]:hora
	 * [2]:numero
	 * [3]:concepto
	 * [4]:entrada
	 * [5]:salida
	 * [6]:saldo_
	 * [7]:banco
	 * [9]:icono
	 * [10]:origen
	 */
	@DependsOn({ "dto.desde", "dto.hasta", "dto.banco", "filterConcepto", "filterNumero" })
	public List<Object[]> getMovimientosBanco() throws Exception {
		
		if (this.dto.getDesde() == null || this.dto.getHasta() == null || this.dto.getBanco() == null) {
			return new ArrayList<Object[]>();
		}
		
		this.detalles1.clear();
		this.movimientosBanco_.clear();
		Date desde = this.dto.getDesde();
		Date hasta = this.dto.getHasta();

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> data = new ArrayList<Object[]>();
		List<Object[]> historico;
		List<Object[]> historicoDEBE;
		List<Object[]> historicoHABER;

		long idBanco = this.dto.getBanco().getId();

		List<Object[]> depositos = rr.getDepositosPorBanco(idBanco, desde, hasta);
		List<Object[]> descuentos = rr.getDescuentosPorBanco(idBanco, desde, hasta);
		List<Object[]> prestamosInternos = rr.getPrestamosInternosPorBanco(idBanco, desde, hasta);
		List<Object[]> transferenciasEnviadas = rr.getTransferenciasOrigenPorBanco(idBanco, desde, hasta);
		List<Object[]> transferenciasRecibidas = rr.getTransferenciasDestinoPorBanco(idBanco, desde, hasta);
		List<Object[]> prestamosBancarios = rr.getPrestamosBancariosPorBanco(idBanco, desde, hasta);
		List<Object[]> cheques = rr.getChequesPropiosPorBanco(idBanco, desde, hasta);
		List<Object[]> chequesRechazados = rr.getChequesRechazadosPorBancoPorDeposito(idBanco, desde, hasta);
		List<Object[]> chequesRechazados_ = rr.getChequesRechazadosPorBancoPorDescuento(idBanco, desde, hasta);
		List<Object[]> gastos = rr.getGastosBancariosPorBanco(idBanco, desde, hasta);
		List<Object[]> formasPagoDebito = rr.getFormasPagoDebitoBancarioPorBanco(idBanco, desde, hasta);
		List<Object[]> formasPagoDeposito = rr.getFormasPagoDepositoBancarioEnRecibosPorBanco(idBanco, desde, hasta);
		List<Object[]> formasPagoDeposito_ = rr.getFormasPagoDepositoBancarioEnVentasPorBanco(idBanco, desde, hasta);

		historicoDEBE = new ArrayList<Object[]>();
		historicoHABER = new ArrayList<Object[]>();

		historicoDEBE.addAll(depositos);
		historicoDEBE.addAll(descuentos);
		historicoDEBE.addAll(transferenciasRecibidas);
		historicoDEBE.addAll(prestamosBancarios);
		historicoDEBE.addAll(formasPagoDeposito);
		historicoDEBE.addAll(formasPagoDeposito_);
		
		historicoHABER.addAll(cheques);
		historicoHABER.addAll(chequesRechazados);
		historicoHABER.addAll(chequesRechazados_);
		historicoHABER.addAll(transferenciasEnviadas);
		historicoHABER.addAll(prestamosInternos);
		historicoHABER.addAll(gastos);
		historicoHABER.addAll(formasPagoDebito);

		for (Object[] movim : historicoDEBE) {
			movim[0] = "(+)" + movim[0];
		}

		historico = new ArrayList<Object[]>();
		historico.addAll(historicoDEBE);
		historico.addAll(historicoHABER);

		// ordena la lista segun fecha..
		Collections.sort(historico, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				Date fecha1 = (Date) o1[1];
				Date fecha2 = (Date) o2[1];
				return fecha1.compareTo(fecha2);
			}
		});

		double entrada_ = 0;
		double salida_ = 0;
		double saldo = 0;

		Collections.sort(historico, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String val1 = (String) o1[4];
				String val2 = (String) o2[4];
				int compare = val1.compareTo(val2);
				if (compare == 0) {
					Date emision1 = (Date) o1[1];
					Date emision2 = (Date) o2[1];
					return emision1.compareTo(emision2);
				} else {
					return compare;
				}
			}
		});
		
		for (Object[] hist : historico) {
			String banco = (String) hist[4];
			boolean ent = ((String) hist[0]).startsWith("(+)");
			String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YYYY);
			String hora = Utiles.getDateToString((Date) hist[1], "HH:mm");
			String numero = hist[2] + "";
			String concepto = ((String) hist[0]).replace("(+)", "");
			String origen = (String) hist[5];
			String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "0";
			String salida = ent ? "0" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
			double debe = ent ? (double) hist[3] : 0.0;
			double haber = ent ? 0.0 : (double) hist[3];
			
			if (concepto.toUpperCase().contains(this.filterConcepto.toUpperCase())
					&& numero.contains(this.filterNumero)) {
				entrada_ += ent ? Double.parseDouble(hist[3] + "") : 0.0;
				salida_ += ent ? 0.0 : Double.parseDouble(hist[3] + "");
				saldo += ent ? Double.parseDouble(hist[3] + "") : Double.parseDouble(hist[3] + "") * -1;
				String saldo_ = Utiles.getNumberFormat(saldo);
				boolean conciliado = this.detalles2.get(numero) != null;
				data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, banco,
						(Date) hist[1], ent ? "" : "", origen.replace("REC-PAG-", "ORDEN PAGO ")
								.replace("CJP-", "CAJA ").replace("CAJAS:", "").toUpperCase(), entrada_, salida_, saldo, ent, conciliado, debe, haber, false });
				this.detalles1.put(numero, numero);
			}
		}
		this.movimientosBanco_.addAll(data);
		this.totalDebe = entrada_;
		this.totalHaber = salida_;
		this.totalSaldo = saldo;
		BindUtils.postNotifyChange(null, null, this, "totalDebe");
		BindUtils.postNotifyChange(null, null, this, "totalHaber");
		BindUtils.postNotifyChange(null, null, this, "totalSaldo");
		BindUtils.postNotifyChange(null, null, this, "movimientosBanco_");
		return data;
	}
	
	@DependsOn({ "dto.banco", "dto.desde", "dto.hasta" })
	public boolean isDetalleVisible() {
		return (this.dto.getBanco() != null && this.dto.getDesde() != null && this.dto.getHasta() != null); 
	}
	
	@DependsOn({ "selectedItem1", "selectedItem2" })
	public boolean isConciliarEnable() {
		return (!this.isDeshabilitado()) && (this.selectedItem1 != null && (boolean) this.selectedItem1[18] == true)
				&& (this.selectedItem2 != null && this.selectedItem2.isChecked());
	}
	
	public List<BancoCtaDTO> getBancos() throws Exception {
		List<BancoCtaDTO> out = new ArrayList<BancoCtaDTO>();
		for (DTO dto : this.getAllDTOs(BancoCta.class.getName(), new AssemblerBancoCtaCte())) {
			BancoCtaDTO bdto = (BancoCtaDTO) dto;
			out.add(bdto);
		}
		return out;
	}
	
	public BancoExtractoDTO getDto() {
		return dto;
	}

	public void setDto(BancoExtractoDTO dto) {
		this.dto = dto;
	}

	public BancoExtractoDetalleDTO getSelectedItem2() {
		return selectedItem2;
	}

	public void setSelectedItem2(BancoExtractoDetalleDTO selectedItem2) {
		this.selectedItem2 = selectedItem2;
	}

	public List<BancoExtractoDetalleDTO> getSelectedItems2() {
		return selectedItems2;
	}

	public void setSelectedItems2(List<BancoExtractoDetalleDTO> selectedItems2) {
		this.selectedItems2 = selectedItems2;
	}

	public String getFilterConcepto() {
		return filterConcepto;
	}

	public void setFilterConcepto(String filterConcepto) {
		this.filterConcepto = filterConcepto;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}

	public double getTotalDebe() {
		return totalDebe;
	}

	public void setTotalDebe(double totalDebe) {
		this.totalDebe = totalDebe;
	}

	public double getTotalHaber() {
		return totalHaber;
	}

	public void setTotalHaber(double totalHaber) {
		this.totalHaber = totalHaber;
	}

	public double getTotalSaldo() {
		return totalSaldo;
	}

	public void setTotalSaldo(double totalSaldo) {
		this.totalSaldo = totalSaldo;
	}

	public Map<String, String> getDetalles2() {
		return detalles2;
	}

	public void setDetalles2(Map<String, String> detalles2) {
		this.detalles2 = detalles2;
	}

	public Object[] getSelectedItem1() {
		return selectedItem1;
	}

	public void setSelectedItem1(Object[] selectedItem1) {
		this.selectedItem1 = selectedItem1;
	}

	public List<Object[]> getSelectedItems1() {
		return selectedItems1;
	}

	public void setSelectedItems1(List<Object[]> selectedItems1) {
		this.selectedItems1 = selectedItems1;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<Object[]> getMovimientosBanco_() {
		return movimientosBanco_;
	}

	public void setMovimientosBanco_(List<Object[]> movimientosBanco_) {
		this.movimientosBanco_ = movimientosBanco_;
	}
}

/**
 * DataSource de Resumen Conciliacion..
 */
class ResumenConciliacioDataSource implements JRDataSource {
	
	private BancoExtractoDTO dto;
	private List<Object[]> items = new ArrayList<>();
	private Map<String, Double> totales1 = new HashMap<>();
	private Map<String, Double> totales2 = new HashMap<>();
	
	public ResumenConciliacioDataSource(BancoExtractoDTO dto, List<Object[]> movimientosBanco) {
		this.dto = dto;
		for (Object[] item : movimientosBanco) {
			item[6] = item[2];
			item[7] = item[3];
			item[8] = true;
			this.items.add(item);
			String key = (String) item[3];
			double importe = (double) item[16];
			if (importe == 0) importe = (double) item[17];
			Double acum = this.totales1.get(key);
			if (acum == null) {
				this.totales1.put(key, importe);
			} else {
				acum += importe;
				this.totales1.put(key, acum);
			}
			this.machear((String) item[2], (String) item[3]);
		}
		
		Collections.sort(this.items, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String val1 = (String) o1[7];
				String val2 = (String) o2[7];
				int compare = val1.compareTo(val2);
				if (compare == 0) {
					String concepto1 = (String) o1[6];
					String concepto2 = (String) o2[6];
					return concepto1.compareTo(concepto2);
				} else {
					return compare;
				}
			}
		});
	}
	
	/**
	 * machear items..
	 */
	private void machear(String nro, String concepto) {
		for (BancoExtractoDetalleDTO item : this.dto.getDetalles2()) {
			String nro2 = item.getNumero();
			String[] nro2_ = item.getAuxi().split(";");
			for (int i = 0; i < nro2_.length; i++) {
				if (nro.equals(nro2) || nro.equals(nro2_[i])) {
					this.items.add(new Object[] { 
							Utiles.getDateToString(item.getFecha(), Utiles.DD_MM_YYYY), "",
							item.getNumero(), item.getDescripcion(), Utiles.getNumberFormat(item.getDebe()),
							Utiles.getNumberFormat(item.getHaber()), nro, concepto, false });
					
					double importe = item.getDebe() == 0 ? item.getHaber() : item.getDebe();					
					Double acum = this.totales2.get(concepto);
					if (acum == null) {
						this.totales2.put(concepto, importe);
					} else {
						acum += importe;
						this.totales2.put(concepto, acum);
					}
				}
			}
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.items.get(index);
		boolean interno = (boolean) det[8];
		String key = (String) det[7];

		if ("Numero".equals(fieldName)) {
			String nro = (String) det[2];
			value = nro;
		} else if ("Concepto".equals(fieldName)) {
			String concepto = ((String) det[3]).toLowerCase();
			value = concepto;
		} else if ("Importe".equals(fieldName)) {
			String val = (String) det[4];
			value = interno ? (val.equals("0") ? (String) det[5] : val) : "";
		}  else if ("Importe_".equals(fieldName)) {
			String val = (String) det[4];
			value = interno ? "" : val.equals("0") ? (String) det[5] : val;
		} else if ("TituloDetalle".equals(fieldName)) {
			value = (String) det[7];
		} else if ("TotalImporte".equals(fieldName)) {
			Double total = this.totales1.get(key);
			value = Utiles.getNumberFormat(total);
		} else if ("TotalImporte_".equals(fieldName)) {
			Double total_ = this.totales2.get(key);
			value = Utiles.getNumberFormat(total_);
		} else if ("Interno".equals(fieldName)) {
			value = interno ? "1" : "2";
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.items.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}
