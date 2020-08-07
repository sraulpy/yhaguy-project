package com.yhaguy.gestion.caja.auditoria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class CajaAuditoriaVM extends SimpleViewModel {
	
	private Date filterDesde;
	private Date filterHasta;
	
	private String filterCaja = "";
	private String filterConcepto = "";
	private String filterNumero = "";
	private String filterFormaPago = "";
	private String filterChequeNro = "";
	
	private double totalDebe = 0;
	private double totalHaber = 0;
	private double totalSaldo = 0;

	@Init(superclass = true)
	public void init() {
		try {
			this.filterDesde = Utiles.getFechaInicioMes();
			this.filterHasta = new Date();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return
	 * [0]:fecha
	 * [1]:hora
	 * [2]:planilla
	 * [3]:tipoMovimiento
	 * [4]:numero 
	 * [5]:concepto 
	 * [6]:chequeNro
	 * [7]:chequeNro
	 * [8]:entrada 
	 * [9]:salida 
	 * [10]:saldo
	 */
	@DependsOn({ "filterDesde", "filterHasta", "filterCaja", "filterConcepto", "filterNumero", "filterFormaPago",
			"filterChequeNro" })
	public List<Object[]> getMovimientosCaja() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> data = new ArrayList<Object[]>();
		List<Object[]> historico;
		List<Object[]> historicoDEBE;
		List<Object[]> historicoHABER;
		
		List<Object[]> efectivoVentas = rr.getEfectivoVentas(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> efectivoRecibos = rr.getEfectivoRecibos(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> efectivoExcedente = rr.getEfectivoExcedenteCaja(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> chequeVentas = rr.getChequeVentas(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> chequeRecibos = rr.getChequeRecibos(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> recaudacionMra = rr.getRecaudacionMra(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> reposicionCajaChica = rr.getReposicionCajaChica(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> tarjetaCreditoVentas = rr.getTarjetaCreditoVentas(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> tarjetaDebitoVentas = rr.getTarjetaDebitoVentas(this.filterDesde, this.filterHasta, this.filterCaja);
		
		List<Object[]> efectivoNotasCredito = rr.getEfectivoNotasCredito(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> efectivoPagos = rr.getEfectivoPagos(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> efectivoGastos = rr.getEfectivoGastos(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> efectivoDepositos = rr.getEfectivoDepositos(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> chequePagos = rr.getChequePagos(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> chequeDepositos = rr.getChequeDepositos(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> chequeDescuentos = rr.getChequeDescuentos(this.filterDesde, this.filterHasta, this.filterCaja);
		List<Object[]> recaudacionMraPago = rr.getRecaudacionMraPago(this.filterDesde, this.filterHasta, this.filterCaja);
		
		historicoDEBE = new ArrayList<Object[]>();
		historicoHABER = new ArrayList<Object[]>();
		
		historicoDEBE.addAll(efectivoVentas);
		historicoDEBE.addAll(efectivoRecibos);
		historicoDEBE.addAll(efectivoExcedente);
		historicoDEBE.addAll(chequeVentas);
		historicoDEBE.addAll(chequeRecibos);
		historicoDEBE.addAll(recaudacionMra);
		historicoDEBE.addAll(reposicionCajaChica);
		historicoDEBE.addAll(tarjetaCreditoVentas);
		historicoDEBE.addAll(tarjetaDebitoVentas);
		
		historicoHABER.addAll(efectivoNotasCredito);
		historicoHABER.addAll(efectivoPagos);
		historicoHABER.addAll(efectivoGastos);
		historicoHABER.addAll(efectivoDepositos);
		historicoHABER.addAll(chequePagos);
		historicoHABER.addAll(chequeDepositos);
		historicoHABER.addAll(chequeDescuentos);
		historicoHABER.addAll(recaudacionMraPago);
		
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
		
		for (Object[] hist : historico) {
			boolean ent = ((String) hist[0]).startsWith("(+)");
			String fecha = Utiles.getDateToString((Date) hist[1], Utiles.DD_MM_YYYY);
			String hora = Utiles.getDateToString((Date) hist[1], "HH:mm");
			String planilla = hist[2] + "";
			String tipoMovimiento = hist[5] + "";
			String numero = hist[4] + "";
			String formaPago = ((String) hist[0]).replace("(+)", "");
			String chequeNro = (String) hist[6];
			String descripcion = (String) hist[7];
			String entrada = ent ? Utiles.getNumberFormat(Double.parseDouble(hist[3] + "")) : "0";
			String salida = ent ? "0" : Utiles.getNumberFormat(Double.parseDouble(hist[3] + ""));
			
			if (tipoMovimiento.toUpperCase().contains(this.filterConcepto.toUpperCase())
					&& numero.contains(this.filterNumero)
					&& formaPago.toUpperCase().contains(this.filterFormaPago.toUpperCase())
					&& chequeNro.contains(this.filterChequeNro)) {
				entrada_ += ent ? Double.parseDouble(hist[3] + "") : 0.0;
				salida_ += ent ? 0.0 : Double.parseDouble(hist[3] + "");
				saldo += ent ? Double.parseDouble(hist[3] + "") : Double.parseDouble(hist[3] + "") * -1;
				String saldo_ = Utiles.getNumberFormat(saldo);
				data.add(new Object[] { fecha, hora, planilla, tipoMovimiento, numero, formaPago, chequeNro, descripcion, entrada, salida, saldo_ });
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

	public Date getFilterDesde() {
		return filterDesde;
	}

	public void setFilterDesde(Date filterDesde) {
		this.filterDesde = filterDesde;
	}

	public Date getFilterHasta() {
		return filterHasta;
	}

	public void setFilterHasta(Date filterHasta) {
		this.filterHasta = filterHasta;
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

	public String getFilterCaja() {
		return filterCaja;
	}

	public void setFilterCaja(String filterCaja) {
		this.filterCaja = filterCaja;
	}

	public String getFilterFormaPago() {
		return filterFormaPago;
	}

	public void setFilterFormaPago(String filterFormaPago) {
		this.filterFormaPago = filterFormaPago;
	}

	public String getFilterChequeNro() {
		return filterChequeNro;
	}

	public void setFilterChequeNro(String filterChequeNro) {
		this.filterChequeNro = filterChequeNro;
	}
}
