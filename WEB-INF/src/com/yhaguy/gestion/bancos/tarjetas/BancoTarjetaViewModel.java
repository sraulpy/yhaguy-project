package com.yhaguy.gestion.bancos.tarjetas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Popup;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.domain.Tipo;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.ProcesadoraTarjeta;
import com.yhaguy.domain.ReciboFormaPago;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.util.Utiles;

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
	
	/**
	 * busca los movimientos de tarjeta..
	 */
	private void buscarMovimientos_() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> list = rr.getFormasPago(this.desde, this.hasta, this.selectedFormaPago.getSigla(),
				this.selectedProcesadora.getId(), this.selectedSucursal.getId());
		List<Object[]> list_ = new ArrayList<Object[]>();
		for (Object[] item : list) {
			double importe = (double) item[3];
			double acreditado = (double) item[8];
			//item = Arrays.copyOf(item, item.length + 8);
			item[4] = Utiles.obtenerValorDelPorcentaje(importe, this.comision);
			item[5] = Utiles.getIVA((double) item[4], 10);
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
