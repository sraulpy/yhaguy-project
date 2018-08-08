package com.yhaguy.gestion.bancos.libro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.componente.ViewPdf;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoMovimiento;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class BancoControlBody extends BodyApp {
	
	static final String IMG_ENTRADA = "z-icon-arrow-right";
	static final String IMG_SALIDA = "z-icon-arrow-left";

	private BancoCtaDTO dto = new BancoCtaDTO();
	private BancoMovimientoDTO dtoMovimiento = new BancoMovimientoDTO();
	private BancoMovimientoDTO selectedMovimento = new BancoMovimientoDTO();
	
	private List<BancoMovimientoDTO> movimientos = new ArrayList<BancoMovimientoDTO>();	
	
	private BancoMovimiento saldoInicial;
	private String selectedAnho;
	private String selectedMes;
	
	private Date desde;
	private Date hasta;
	
	private double totalDebe = 0;
	private double totalHaber = 0;
	private double totalSaldo = 0;
	
	private String filterConcepto = "";
	private String filterNumero = "";

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

	@Override
	public Assembler getAss() {
		return new AssemblerBancoCtaCte();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.inicializarSaldoInicial(dto.getId());
		this.dto = (BancoCtaDTO) dto;
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		return new BancoCtaDTO();
	}

	@Override
	public String getEntidadPrincipal() {
		return BancoCta.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}

	public Browser getBrowser() {
		return new BancoBrowser();
	}
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return (this.dto.esNuevo());
	}
	
	@Override
	public void showImprimir() {
		this.imprimirLibroBanco();
	}

	@Override
	public boolean verificarAlGrabar() {
		boolean grabar = true;
		if (grabar == true && this.dto.esNuevo() == true) {
			CuentaContable cuenta = new CuentaContable();
			cuenta.setAlias("CT-BANCO- " + this.dto.getBanco().getPos1());
		}
		return grabar;
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return "";
	}
	
	@Command
	@NotifyChange({ "saldoInicial", "movimientosBanco" })
	public void registrarSaldoInicial(@BindingParam("comp") Popup comp) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		this.saldoInicial.setFecha(Utiles.getFecha("01-" + this.selectedMes + "-" + this.selectedAnho + " 00:00:00"));
		rr.saveObject(this.saldoInicial, this.getLoginNombre());
		this.inicializarSaldoInicial(this.dto.getId());
		comp.close();
		Clients.showNotification("REGISTRO GUARDADO..!");
	}
	
	/**
	 * FUNCIONES..
	 */
	
	/**
	 * imprimir libro banco
	 */
	private void imprimirLibroBanco() {
		try {
			List<Object[]> data = new ArrayList<Object[]>();

			for (Object[] movim : this.getMovimientosBanco()) {
				Object[] obj1 = new Object[] { movim[3], movim[0], movim[2], movim[4], movim[5], movim[6] };
				data.add(obj1);
			}

			ReporteYhaguy rep = new ReporteLibroBanco((String) this.dto.getBanco()
					.getPos1(), Utiles.getDateToString(this.desde, Utiles.DD_MM_YYYY), Utiles.getDateToString(this.hasta, Utiles.DD_MM_YYYY), this
					.getSucursal().getText());
			rep.setDatosReporte(data);
			rep.setApaisada();

			ViewPdf vp = new ViewPdf();
			vp.setBotonImprimir(false);
			vp.setBotonCancelar(false);
			vp.showReporte(rep, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * inicializa los datos del saldo inicial..
	 */
	private void inicializarSaldoInicial(long idBanco) {
		try {
			RegisterDomain rr = RegisterDomain.getInstance();
			this.saldoInicial = new BancoMovimiento();
			this.saldoInicial.setNroCuenta((BancoCta) rr.getObject(BancoCta.class.getName(), idBanco));
			this.saldoInicial.setTipoMovimiento(rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_SALDO_INICIAL_BANCO));
			this.saldoInicial.setDescripcion(this.saldoInicial.getTipoMovimiento().getDescripcion());
			this.saldoInicial.setMonto(0);
			this.selectedAnho = Utiles.getAnhoActual();
			this.selectedMes = Utiles.getMesActual();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	@DependsOn({ "desde", "hasta", "filterConcepto", "filterNumero" })
	public List<Object[]> getMovimientosBanco() throws Exception {
		Date desde = this.desde;
		Date hasta = this.hasta;

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> data = new ArrayList<Object[]>();
		List<Object[]> historico;
		List<Object[]> historicoDEBE;
		List<Object[]> historicoHABER;

		long idBanco = this.dto.getId();

		List<Object[]> saldosIniciales = rr.getSaldosInicialesPorBanco(idBanco, desde, hasta);
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

		historicoDEBE.addAll(saldosIniciales);
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
			
			if (concepto.toUpperCase().contains(this.filterConcepto.toUpperCase())
					&& numero.contains(this.filterNumero)) {
				entrada_ += ent ? Double.parseDouble(hist[3] + "") : 0.0;
				salida_ += ent ? 0.0 : Double.parseDouble(hist[3] + "");
				saldo += ent ? Double.parseDouble(hist[3] + "") : Double.parseDouble(hist[3] + "") * -1;
				String saldo_ = Utiles.getNumberFormat(saldo);
				data.add(new Object[] { fecha, hora, numero, concepto, entrada, salida, saldo_, banco,
						(Date) hist[1], ent ? IMG_ENTRADA : IMG_SALIDA, origen.replace("REC-PAG-", "ORDEN PAGO ")
								.replace("CJP-", "CAJA ").replace("CAJAS:", "").toUpperCase(), entrada_, salida_, saldo, ent });
			}
		}
		this.totalDebe = entrada_;
		this.totalHaber = salida_;
		this.totalSaldo = saldo;
		BindUtils.postNotifyChange(null, null, this, "totalDebe");
		BindUtils.postNotifyChange(null, null, this, "totalHaber");
		BindUtils.postNotifyChange(null, null, this, "totalSaldo");
		return data;
	}
	
	/**
	 * @return la descripcion del saldo inicial..
	 */
	public String getDescripcionSaldoInicial() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		TipoMovimiento tm = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_SALDO_INICIAL_BANCO);
		return tm.getDescripcion().toUpperCase();
	}
	
	public List<String> getAnhos() {
		return Utiles.getAnhos();
	}
	
	public List<String> getMeses() {
		return Utiles.getNumeroMeses_();
	}
	
	public List<BancoMovimientoDTO> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<BancoMovimientoDTO> movimientos) {
		this.movimientos = movimientos;
	}

	public BancoMovimientoDTO getSelectedMovimento() {
		return selectedMovimento;
	}

	public void setSelectedMovimento(BancoMovimientoDTO selectedMovimento) {
		this.selectedMovimento = selectedMovimento;
	}

	public boolean isTabsVisible() {
		boolean out = true;

		if (this.dto.esNuevo() == true) {
			out = false;
		}

		return out;

	}
	
	public BancoCtaDTO getDto() {
		return dto;
	}

	public void setDto(BancoCtaDTO dto) {
		this.dto = dto;
	}

	public BancoMovimientoDTO getDtoMovimiento() {
		return dtoMovimiento;
	}

	public void setDtoMovimiento(BancoMovimientoDTO dtoMovimiento) {
		this.dtoMovimiento = dtoMovimiento;
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

	public void setTotalSaldo(double totalSaldo) {
		this.totalSaldo = totalSaldo;
	}

	public double getTotalSaldo() {
		return totalSaldo;
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

	public BancoMovimiento getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(BancoMovimiento saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public String getSelectedAnho() {
		return selectedAnho;
	}

	public void setSelectedAnho(String selectedAnho) {
		this.selectedAnho = selectedAnho;
	}

	public String getSelectedMes() {
		return selectedMes;
	}

	public void setSelectedMes(String selectedMes) {
		this.selectedMes = selectedMes;
	}
}

/**
 * Reporte de Libro Banco..
 */
class ReporteLibroBanco extends ReporteYhaguy {
	
	private String banco;
	private String desde;
	private String hasta;
	private String sucursal;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Concepto", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Fecha", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col5 = new DatosColumnas("Debe", TIPO_STRING, 40);
	static DatosColumnas col6 = new DatosColumnas("Haber", TIPO_STRING, 40);
	static DatosColumnas col7 = new DatosColumnas("Saldo", TIPO_STRING, 40);
	
	public ReporteLibroBanco(String banco, String desde, String hasta, String sucursal) {
		this.banco = banco;
		this.desde = desde;
		this.hasta = hasta;
		this.sucursal = sucursal;
	}
	
	static {
		col5.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col6.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		col7.setAlineacionColuman(COLUMNA_ALINEADA_DERECHA);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Libro Banco");
		this.setDirectorio("banco");
		this.setNombreArchivo("librobanco-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
		this.setBorrarDespuesDeVer(true);
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
				.add(this.textoParValor("Banco", this.banco))
				.add(cmp.horizontalFlowList()
						.add(this.textoParValor("Desde", this.desde))
						.add(this.textoParValor("Hasta", this.hasta))
						.add(this.textoParValor("Sucursal", this.sucursal))));

		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
